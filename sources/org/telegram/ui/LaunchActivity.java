package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.StatFs;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGameShortName;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
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
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

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
    class C14512 implements OnTouchListener {
        C14512() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty() || event.getAction() != 1) {
                return false;
            }
            float x = event.getX();
            float y = event.getY();
            int[] location = new int[2];
            LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(location);
            int viewX = location[0];
            int viewY = location[1];
            if (LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) viewX) && x < ((float) (LaunchActivity.this.layersActionBarLayout.getWidth() + viewX)) && y > ((float) viewY) && y < ((float) (LaunchActivity.this.layersActionBarLayout.getHeight() + viewY)))) {
                return false;
            }
            if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                int a = 0;
                while (LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                    LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                    a = (a - 1) + 1;
                }
                LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$3 */
    class C14533 implements OnClickListener {
        C14533() {
        }

        public void onClick(View v) {
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$7 */
    class C14557 implements Runnable {
        final /* synthetic */ Bundle val$args;

        C14557(Bundle bundle) {
            this.val$args = bundle;
        }

        public void run() {
            LaunchActivity.this.presentFragment(new CancelAccountDeletionActivity(this.val$args));
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
    class C21614 implements OnItemClickListener {
        C21614() {
        }

        public void onItemClick(View view, int position) {
            boolean z = false;
            if (position == 0) {
                DrawerLayoutAdapter access$700 = LaunchActivity.this.drawerLayoutAdapter;
                if (!LaunchActivity.this.drawerLayoutAdapter.isAccountsShowed()) {
                    z = true;
                }
                access$700.setAccountsShowed(z, true);
            } else if (view instanceof DrawerUserCell) {
                LaunchActivity.this.switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (view instanceof DrawerAddCell) {
                int freeAccount = -1;
                for (int a = 0; a < 3; a++) {
                    if (!UserConfig.getInstance(a).isClientActivated()) {
                        freeAccount = a;
                        break;
                    }
                }
                if (freeAccount >= 0) {
                    LaunchActivity.this.presentFragment(new LoginActivity(freeAccount));
                }
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else {
                int id = LaunchActivity.this.drawerLayoutAdapter.getId(position);
                if (id == 2) {
                    LaunchActivity.this.presentFragment(new GroupCreateActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 3) {
                    args = new Bundle();
                    args.putBoolean("onlyUsers", true);
                    args.putBoolean("destroyAfterSelect", true);
                    args.putBoolean("createSecretChat", true);
                    args.putBoolean("allowBots", false);
                    LaunchActivity.this.presentFragment(new ContactsActivity(args));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 4) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                        LaunchActivity.this.presentFragment(new ChannelIntroActivity());
                        preferences.edit().putBoolean("channel_intro", true).commit();
                    } else {
                        args = new Bundle();
                        args.putInt("step", 0);
                        LaunchActivity.this.presentFragment(new ChannelCreateActivity(args));
                    }
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 6) {
                    LaunchActivity.this.presentFragment(new ContactsActivity(null));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 7) {
                    LaunchActivity.this.presentFragment(new InviteContactsActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 8) {
                    LaunchActivity.this.presentFragment(new SettingsActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 9) {
                    Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 10) {
                    LaunchActivity.this.presentFragment(new CallLogActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (id == 11) {
                    args = new Bundle();
                    args.putInt("user_id", UserConfig.getInstance(LaunchActivity.this.currentAccount).getClientUserId());
                    LaunchActivity.this.presentFragment(new ChatActivity(args));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity$6 */
    class C21626 implements PasscodeViewDelegate {
        C21626() {
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

    /* renamed from: org.telegram.ui.LaunchActivity$8 */
    class C21648 implements SharingLocationsAlertDelegate {
        final /* synthetic */ int[] val$intentAccount;

        C21648(int[] iArr) {
            this.val$intentAccount = iArr;
        }

        public void didSelectLocation(SharingLocationInfo info) {
            this.val$intentAccount[0] = info.messageObject.currentAccount;
            LaunchActivity.this.switchToAccount(this.val$intentAccount[0], true);
            LocationActivity locationActivity = new LocationActivity(2);
            locationActivity.setMessageObject(info.messageObject);
            final long dialog_id = info.messageObject.getDialogId();
            locationActivity.setDelegate(new LocationActivityDelegate() {
                public void didSelectLocation(MessageMedia location, int live) {
                    SendMessagesHelper.getInstance(C21648.this.val$intentAccount[0]).sendMessage(location, dialog_id, null, null, null);
                }
            });
            LaunchActivity.this.presentFragment(locationActivity);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onCreate(Bundle savedInstanceState) {
        int dp;
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        this.currentAccount = UserConfig.selectedAccount;
        if (!UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            Intent intent = getIntent();
            if (intent == null || intent.getAction() == null || !("android.intent.action.SEND".equals(intent.getAction()) || intent.getAction().equals("android.intent.action.SEND_MULTIPLE"))) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                long crashed_time = preferences.getLong("intro_crashed_time", 0);
                boolean fromIntro = intent.getBooleanExtra("fromIntro", false);
                if (fromIntro) {
                    preferences.edit().putLong("intro_crashed_time", 0).commit();
                }
                if (Math.abs(crashed_time - System.currentTimeMillis()) >= 120000 && intent != null && !fromIntro && ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().isEmpty()) {
                    Intent intent2 = new Intent(this, IntroActivity.class);
                    intent2.setData(intent.getData());
                    startActivity(intent2);
                    super.onCreate(savedInstanceState);
                    finish();
                    return;
                }
            }
            super.onCreate(savedInstanceState);
            finish();
            return;
        }
        requestWindowFeature(1);
        setTheme(R.style.Theme.TMessages);
        if (VERSION.SDK_INT >= 21) {
            try {
                setTaskDescription(new TaskDescription(null, null, Theme.getColor(Theme.key_actionBarDefault) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
            } catch (Exception e) {
            }
        }
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
            try {
                getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= 24) {
            AndroidUtilities.isInMultiwindow = isInMultiWindowMode();
        }
        Theme.createChatResources(this, false);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        }
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        this.actionBarLayout = new ActionBarLayout(this);
        this.drawerLayoutContainer = new DrawerLayoutContainer(this);
        setContentView(this.drawerLayoutContainer, new LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            View c14471 = new RelativeLayout(this) {
                private boolean inLayout;

                public void requestLayout() {
                    if (!this.inLayout) {
                        super.requestLayout();
                    }
                }

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    this.inLayout = true;
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(width, height);
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.tabletFullSize = true;
                        LaunchActivity.this.actionBarLayout.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    } else {
                        LaunchActivity.this.tabletFullSize = false;
                        int leftWidth = (width / 100) * 35;
                        if (leftWidth < AndroidUtilities.dp(320.0f)) {
                            leftWidth = AndroidUtilities.dp(320.0f);
                        }
                        LaunchActivity.this.actionBarLayout.measure(MeasureSpec.makeMeasureSpec(leftWidth, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                        LaunchActivity.this.shadowTabletSide.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1.0f), NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                        LaunchActivity.this.rightActionBarLayout.measure(MeasureSpec.makeMeasureSpec(width - leftWidth, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    }
                    LaunchActivity.this.backgroundTablet.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    LaunchActivity.this.shadowTablet.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                    LaunchActivity.this.layersActionBarLayout.measure(MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), width), NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528.0f), height), NUM));
                    this.inLayout = false;
                }

                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    int width = r - l;
                    int height = b - t;
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                    } else {
                        int leftWidth = (width / 100) * 35;
                        if (leftWidth < AndroidUtilities.dp(320.0f)) {
                            leftWidth = AndroidUtilities.dp(320.0f);
                        }
                        LaunchActivity.this.shadowTabletSide.layout(leftWidth, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + leftWidth, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                        LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                        LaunchActivity.this.rightActionBarLayout.layout(leftWidth, 0, LaunchActivity.this.rightActionBarLayout.getMeasuredWidth() + leftWidth, LaunchActivity.this.rightActionBarLayout.getMeasuredHeight());
                    }
                    int x = (width - LaunchActivity.this.layersActionBarLayout.getMeasuredWidth()) / 2;
                    int y = (height - LaunchActivity.this.layersActionBarLayout.getMeasuredHeight()) / 2;
                    LaunchActivity.this.layersActionBarLayout.layout(x, y, LaunchActivity.this.layersActionBarLayout.getMeasuredWidth() + x, LaunchActivity.this.layersActionBarLayout.getMeasuredHeight() + y);
                    LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
                    LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
                }
            };
            this.drawerLayoutContainer.addView(c14471, LayoutHelper.createFrame(-1, -1.0f));
            this.backgroundTablet = new View(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            this.backgroundTablet.setBackgroundDrawable(drawable);
            c14471.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            c14471.addView(this.actionBarLayout);
            this.rightActionBarLayout = new ActionBarLayout(this);
            this.rightActionBarLayout.init(rightFragmentsStack);
            this.rightActionBarLayout.setDelegate(this);
            c14471.addView(this.rightActionBarLayout);
            this.shadowTabletSide = new FrameLayout(this);
            this.shadowTabletSide.setBackgroundColor(NUM);
            c14471.addView(this.shadowTabletSide);
            this.shadowTablet = new FrameLayout(this);
            this.shadowTablet.setVisibility(layerFragmentsStack.isEmpty() ? 8 : 0);
            this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            c14471.addView(this.shadowTablet);
            this.shadowTablet.setOnTouchListener(new C14512());
            this.shadowTablet.setOnClickListener(new C14533());
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(R.drawable.boxshadow);
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
            this.layersActionBarLayout.setVisibility(layerFragmentsStack.isEmpty() ? 8 : 0);
            c14471.addView(this.layersActionBarLayout);
        } else {
            this.drawerLayoutContainer.addView(this.actionBarLayout, new LayoutParams(-1, -1));
        }
        this.sideMenu = new RecyclerListView(this);
        ((DefaultItemAnimator) this.sideMenu.getItemAnimator()).setDelayAnimations(false);
        this.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
        this.sideMenu.setLayoutManager(new LinearLayoutManager(this, 1, false));
        RecyclerListView recyclerListView = this.sideMenu;
        Adapter drawerLayoutAdapter = new DrawerLayoutAdapter(this);
        this.drawerLayoutAdapter = drawerLayoutAdapter;
        recyclerListView.setAdapter(drawerLayoutAdapter);
        this.drawerLayoutContainer.setDrawerLayout(this.sideMenu);
        LayoutParams layoutParams = (FrameLayout.LayoutParams) this.sideMenu.getLayoutParams();
        Point screenSize = AndroidUtilities.getRealScreenSize();
        if (AndroidUtilities.isTablet()) {
            dp = AndroidUtilities.dp(320.0f);
        } else {
            dp = Math.min(AndroidUtilities.dp(320.0f), Math.min(screenSize.x, screenSize.y) - AndroidUtilities.dp(56.0f));
        }
        layoutParams.width = dp;
        layoutParams.height = -1;
        this.sideMenu.setLayoutParams(layoutParams);
        this.sideMenu.setOnItemClickListener(new C21614());
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        Theme.loadWallpaper();
        this.passcodeView = new PasscodeView(this);
        this.drawerLayoutContainer.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        checkCurrentAccount();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeOtherAppActivities);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.notificationsCountUpdated);
        if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                DialogsActivity dialogsActivity = new DialogsActivity(null);
                dialogsActivity.setSideMenu(this.sideMenu);
                this.actionBarLayout.addFragmentToStack(dialogsActivity);
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            } else {
                this.actionBarLayout.addFragmentToStack(new LoginActivity());
                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            }
            if (savedInstanceState != null) {
                try {
                    String fragmentName = savedInstanceState.getString("fragment");
                    if (fragmentName != null) {
                        ChatActivity chat;
                        BaseFragment settings;
                        BaseFragment groupCreateFinalActivity;
                        ChannelCreateActivity channel;
                        ChannelEditActivity channel2;
                        Bundle args = savedInstanceState.getBundle("args");
                        Object obj = -1;
                        switch (fragmentName.hashCode()) {
                            case -1529105743:
                                if (fragmentName.equals("wallpapers")) {
                                    obj = 6;
                                }
                            case -1349522494:
                                if (fragmentName.equals("chat_profile")) {
                                    obj = 5;
                                }
                            case 3052376:
                                if (fragmentName.equals("chat")) {
                                    obj = null;
                                }
                            case 3108362:
                                if (fragmentName.equals("edit")) {
                                    obj = 4;
                                }
                                switch (obj) {
                                    case null:
                                        if (args != null) {
                                            chat = new ChatActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(chat)) {
                                                chat.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 1:
                                        settings = new SettingsActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                    case 2:
                                        if (args != null) {
                                            groupCreateFinalActivity = new GroupCreateFinalActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 3:
                                        if (args != null) {
                                            channel = new ChannelCreateActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel)) {
                                                channel.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 4:
                                        if (args != null) {
                                            channel2 = new ChannelEditActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel2)) {
                                                channel2.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 5:
                                        if (args != null) {
                                            groupCreateFinalActivity = new ProfileActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 6:
                                        settings = new WallpapersActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                }
                                break;
                            case 98629247:
                                if (fragmentName.equals("group")) {
                                    obj = 2;
                                }
                                switch (obj) {
                                    case null:
                                        if (args != null) {
                                            chat = new ChatActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(chat)) {
                                                chat.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 1:
                                        settings = new SettingsActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                    case 2:
                                        if (args != null) {
                                            groupCreateFinalActivity = new GroupCreateFinalActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 3:
                                        if (args != null) {
                                            channel = new ChannelCreateActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel)) {
                                                channel.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 4:
                                        if (args != null) {
                                            channel2 = new ChannelEditActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel2)) {
                                                channel2.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 5:
                                        if (args != null) {
                                            groupCreateFinalActivity = new ProfileActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 6:
                                        settings = new WallpapersActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                }
                                break;
                            case 738950403:
                                if (fragmentName.equals("channel")) {
                                    obj = 3;
                                }
                                switch (obj) {
                                    case null:
                                        if (args != null) {
                                            chat = new ChatActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(chat)) {
                                                chat.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 1:
                                        settings = new SettingsActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                    case 2:
                                        if (args != null) {
                                            groupCreateFinalActivity = new GroupCreateFinalActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 3:
                                        if (args != null) {
                                            channel = new ChannelCreateActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel)) {
                                                channel.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 4:
                                        if (args != null) {
                                            channel2 = new ChannelEditActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel2)) {
                                                channel2.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 5:
                                        if (args != null) {
                                            groupCreateFinalActivity = new ProfileActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 6:
                                        settings = new WallpapersActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                }
                                break;
                            case 1434631203:
                                if (fragmentName.equals("settings")) {
                                    obj = 1;
                                }
                                switch (obj) {
                                    case null:
                                        if (args != null) {
                                            chat = new ChatActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(chat)) {
                                                chat.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 1:
                                        settings = new SettingsActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                    case 2:
                                        if (args != null) {
                                            groupCreateFinalActivity = new GroupCreateFinalActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 3:
                                        if (args != null) {
                                            channel = new ChannelCreateActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel)) {
                                                channel.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 4:
                                        if (args != null) {
                                            channel2 = new ChannelEditActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(channel2)) {
                                                channel2.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 5:
                                        if (args != null) {
                                            groupCreateFinalActivity = new ProfileActivity(args);
                                            if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                                groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                                break;
                                            }
                                        }
                                        break;
                                    case 6:
                                        settings = new WallpapersActivity();
                                        this.actionBarLayout.addFragmentToStack(settings);
                                        settings.restoreSelfArgs(savedInstanceState);
                                        break;
                                }
                                break;
                        }
                        switch (obj) {
                            case null:
                                if (args != null) {
                                    chat = new ChatActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(chat)) {
                                        chat.restoreSelfArgs(savedInstanceState);
                                        break;
                                    }
                                }
                                break;
                            case 1:
                                settings = new SettingsActivity();
                                this.actionBarLayout.addFragmentToStack(settings);
                                settings.restoreSelfArgs(savedInstanceState);
                                break;
                            case 2:
                                if (args != null) {
                                    groupCreateFinalActivity = new GroupCreateFinalActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                        groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                        break;
                                    }
                                }
                                break;
                            case 3:
                                if (args != null) {
                                    channel = new ChannelCreateActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(channel)) {
                                        channel.restoreSelfArgs(savedInstanceState);
                                        break;
                                    }
                                }
                                break;
                            case 4:
                                if (args != null) {
                                    channel2 = new ChannelEditActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(channel2)) {
                                        channel2.restoreSelfArgs(savedInstanceState);
                                        break;
                                    }
                                }
                                break;
                            case 5:
                                if (args != null) {
                                    groupCreateFinalActivity = new ProfileActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                        groupCreateFinalActivity.restoreSelfArgs(savedInstanceState);
                                        break;
                                    }
                                }
                                break;
                            case 6:
                                settings = new WallpapersActivity();
                                this.actionBarLayout.addFragmentToStack(settings);
                                settings.restoreSelfArgs(savedInstanceState);
                                break;
                        }
                    }
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
        } else {
            BaseFragment fragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(0);
            if (fragment instanceof DialogsActivity) {
                ((DialogsActivity) fragment).setSideMenu(this.sideMenu);
            }
            boolean allowOpen = true;
            if (AndroidUtilities.isTablet()) {
                allowOpen = this.actionBarLayout.fragmentsStack.size() <= 1 && this.layersActionBarLayout.fragmentsStack.isEmpty();
                if (this.layersActionBarLayout.fragmentsStack.size() == 1 && (this.layersActionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
                    allowOpen = false;
                }
            }
            if (this.actionBarLayout.fragmentsStack.size() == 1 && (this.actionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
                allowOpen = false;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(allowOpen, false);
        }
        checkLayout();
        handleIntent(getIntent(), false, savedInstanceState != null, false);
        try {
            String os1 = Build.DISPLAY;
            String os2 = Build.USER;
            if (os1 != null) {
                os1 = os1.toLowerCase();
            } else {
                os1 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (os2 != null) {
                os2 = os1.toLowerCase();
            } else {
                os2 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (os1.contains("flyme") || os2.contains("flyme")) {
                AndroidUtilities.incorrectDisplaySizeFix = true;
                View view = getWindow().getDecorView().getRootView();
                ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
                final View view2 = view;
                OnGlobalLayoutListener c14545 = new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        int height = view2.getMeasuredHeight();
                        if (VERSION.SDK_INT >= 21) {
                            height -= AndroidUtilities.statusBarHeight;
                        }
                        if (height > AndroidUtilities.dp(100.0f) && height < AndroidUtilities.displaySize.y && AndroidUtilities.dp(100.0f) + height > AndroidUtilities.displaySize.y) {
                            AndroidUtilities.displaySize.y = height;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("fix display size y to " + AndroidUtilities.displaySize.y);
                            }
                        }
                    }
                };
                this.onGlobalLayoutListener = c14545;
                viewTreeObserver.addOnGlobalLayoutListener(c14545);
            }
        } catch (Throwable e222) {
            FileLog.m3e(e222);
        }
        MediaController.getInstance().setBaseActivity(this, true);
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
                }
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
            this.passcodeView.setDelegate(new C21626());
        }
    }

    private boolean handleIntent(android.content.Intent r86, boolean r87, boolean r88, boolean r89) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r32_2 'currentData' org.telegram.ui.LaunchActivity$VcardData) in PHI: PHI: (r32_3 'currentData' org.telegram.ui.LaunchActivity$VcardData) = (r32_2 'currentData' org.telegram.ui.LaunchActivity$VcardData), (r32_1 'currentData' org.telegram.ui.LaunchActivity$VcardData), (r32_1 'currentData' org.telegram.ui.LaunchActivity$VcardData), (r32_4 'currentData' org.telegram.ui.LaunchActivity$VcardData) binds: {(r32_2 'currentData' org.telegram.ui.LaunchActivity$VcardData)=B:53:0x0197, (r32_1 'currentData' org.telegram.ui.LaunchActivity$VcardData)=B:68:0x020b, (r32_1 'currentData' org.telegram.ui.LaunchActivity$VcardData)=B:70:0x0217, (r32_4 'currentData' org.telegram.ui.LaunchActivity$VcardData)=B:71:0x0219}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r85 = this;
        r4 = org.telegram.messenger.AndroidUtilities.handleProxyIntent(r85, r86);
        if (r4 == 0) goto L_0x0009;
    L_0x0006:
        r61 = 1;
    L_0x0008:
        return r61;
    L_0x0009:
        r4 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r4 == 0) goto L_0x0034;
    L_0x000f:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x0034;
    L_0x0019:
        if (r86 == 0) goto L_0x0028;
    L_0x001b:
        r4 = "android.intent.action.MAIN";
        r5 = r86.getAction();
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x0034;
    L_0x0028:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r5 = 0;
        r16 = 1;
        r0 = r16;
        r4.closePhoto(r5, r0);
    L_0x0034:
        r43 = r86.getFlags();
        r4 = 1;
        r0 = new int[r4];
        r47 = r0;
        r4 = 0;
        r5 = "currentAccount";
        r16 = org.telegram.messenger.UserConfig.selectedAccount;
        r0 = r86;
        r1 = r16;
        r5 = r0.getIntExtra(r5, r1);
        r47[r4] = r5;
        r4 = 0;
        r4 = r47[r4];
        r5 = 1;
        r0 = r85;
        r0.switchToAccount(r4, r5);
        if (r89 != 0) goto L_0x0087;
    L_0x0058:
        r4 = 1;
        r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);
        if (r4 != 0) goto L_0x0063;
    L_0x005f:
        r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r4 == 0) goto L_0x0087;
    L_0x0063:
        r85.showPasscodeActivity();
        r0 = r86;
        r1 = r85;
        r1.passcodeSaveIntent = r0;
        r0 = r87;
        r1 = r85;
        r1.passcodeSaveIntentIsNew = r0;
        r0 = r88;
        r1 = r85;
        r1.passcodeSaveIntentIsRestore = r0;
        r0 = r85;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r5 = 0;
        r4.saveConfig(r5);
        r61 = 0;
        goto L_0x0008;
    L_0x0087:
        r61 = 0;
        r4 = 0;
        r65 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r62 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r63 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r64 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r53 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r52 = java.lang.Integer.valueOf(r4);
        r36 = 0;
        r4 = org.telegram.messenger.SharedConfig.directShare;
        if (r4 == 0) goto L_0x00c4;
    L_0x00ad:
        if (r86 == 0) goto L_0x01fd;
    L_0x00af:
        r4 = r86.getExtras();
        if (r4 == 0) goto L_0x01fd;
    L_0x00b5:
        r4 = r86.getExtras();
        r5 = "dialogId";
        r16 = 0;
        r0 = r16;
        r36 = r4.getLong(r5, r0);
    L_0x00c4:
        r69 = 0;
        r71 = 0;
        r70 = 0;
        r4 = 0;
        r0 = r85;
        r0.photoPathsArray = r4;
        r4 = 0;
        r0 = r85;
        r0.videoPath = r4;
        r4 = 0;
        r0 = r85;
        r0.sendingText = r4;
        r4 = 0;
        r0 = r85;
        r0.documentsPathsArray = r4;
        r4 = 0;
        r0 = r85;
        r0.documentsOriginalPathsArray = r4;
        r4 = 0;
        r0 = r85;
        r0.documentsMimeType = r4;
        r4 = 0;
        r0 = r85;
        r0.documentsUrisArray = r4;
        r4 = 0;
        r0 = r85;
        r0.contactsToSend = r4;
        r0 = r85;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.isClientActivated();
        if (r4 == 0) goto L_0x02c1;
    L_0x0100:
        r4 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r4 = r4 & r43;
        if (r4 != 0) goto L_0x02c1;
    L_0x0106:
        if (r86 == 0) goto L_0x02c1;
    L_0x0108:
        r4 = r86.getAction();
        if (r4 == 0) goto L_0x02c1;
    L_0x010e:
        if (r88 != 0) goto L_0x02c1;
    L_0x0110:
        r4 = "android.intent.action.SEND";
        r5 = r86.getAction();
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x05d0;
    L_0x011d:
        r42 = 0;
        r76 = r86.getType();
        if (r76 == 0) goto L_0x045e;
    L_0x0125:
        r4 = "text/x-vcard";
        r0 = r76;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x045e;
    L_0x0130:
        r4 = r86.getExtras();	 Catch:{ Exception -> 0x02ac }
        r5 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x02ac }
        r77 = r4.get(r5);	 Catch:{ Exception -> 0x02ac }
        r77 = (android.net.Uri) r77;	 Catch:{ Exception -> 0x02ac }
        if (r77 == 0) goto L_0x045a;	 Catch:{ Exception -> 0x02ac }
    L_0x013f:
        r31 = r85.getContentResolver();	 Catch:{ Exception -> 0x02ac }
        r0 = r31;	 Catch:{ Exception -> 0x02ac }
        r1 = r77;	 Catch:{ Exception -> 0x02ac }
        r72 = r0.openInputStream(r1);	 Catch:{ Exception -> 0x02ac }
        r84 = new java.util.ArrayList;	 Catch:{ Exception -> 0x02ac }
        r84.<init>();	 Catch:{ Exception -> 0x02ac }
        r32 = 0;	 Catch:{ Exception -> 0x02ac }
        r27 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x02ac }
        r4 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x02ac }
        r5 = "UTF-8";	 Catch:{ Exception -> 0x02ac }
        r0 = r72;	 Catch:{ Exception -> 0x02ac }
        r4.<init>(r0, r5);	 Catch:{ Exception -> 0x02ac }
        r0 = r27;	 Catch:{ Exception -> 0x02ac }
        r0.<init>(r4);	 Catch:{ Exception -> 0x02ac }
    L_0x0163:
        r48 = r27.readLine();	 Catch:{ Exception -> 0x02ac }
        if (r48 == 0) goto L_0x03d4;	 Catch:{ Exception -> 0x02ac }
    L_0x0169:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0170;	 Catch:{ Exception -> 0x02ac }
    L_0x016d:
        org.telegram.messenger.FileLog.m0d(r48);	 Catch:{ Exception -> 0x02ac }
    L_0x0170:
        r4 = ":";	 Catch:{ Exception -> 0x02ac }
        r0 = r48;	 Catch:{ Exception -> 0x02ac }
        r24 = r0.split(r4);	 Catch:{ Exception -> 0x02ac }
        r0 = r24;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.length;	 Catch:{ Exception -> 0x02ac }
        r5 = 2;	 Catch:{ Exception -> 0x02ac }
        if (r4 != r5) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x017f:
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = "BEGIN";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0201;	 Catch:{ Exception -> 0x02ac }
    L_0x018b:
        r4 = 1;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = "VCARD";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0201;	 Catch:{ Exception -> 0x02ac }
    L_0x0197:
        r32 = new org.telegram.ui.LaunchActivity$VcardData;	 Catch:{ Exception -> 0x02ac }
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r1 = r85;	 Catch:{ Exception -> 0x02ac }
        r0.<init>();	 Catch:{ Exception -> 0x02ac }
        r0 = r84;	 Catch:{ Exception -> 0x02ac }
        r1 = r32;	 Catch:{ Exception -> 0x02ac }
        r0.add(r1);	 Catch:{ Exception -> 0x02ac }
    L_0x01a8:
        if (r32 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x01aa:
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = "FN";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.startsWith(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 != 0) goto L_0x01cc;	 Catch:{ Exception -> 0x02ac }
    L_0x01b6:
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = "ORG";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.startsWith(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x03af;	 Catch:{ Exception -> 0x02ac }
    L_0x01c2:
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.name;	 Catch:{ Exception -> 0x02ac }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x03af;	 Catch:{ Exception -> 0x02ac }
    L_0x01cc:
        r51 = 0;	 Catch:{ Exception -> 0x02ac }
        r50 = 0;	 Catch:{ Exception -> 0x02ac }
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = ";";	 Catch:{ Exception -> 0x02ac }
        r56 = r4.split(r5);	 Catch:{ Exception -> 0x02ac }
        r0 = r56;	 Catch:{ Exception -> 0x02ac }
        r5 = r0.length;	 Catch:{ Exception -> 0x02ac }
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
    L_0x01de:
        if (r4 >= r5) goto L_0x0240;	 Catch:{ Exception -> 0x02ac }
    L_0x01e0:
        r55 = r56[r4];	 Catch:{ Exception -> 0x02ac }
        r16 = "=";	 Catch:{ Exception -> 0x02ac }
        r0 = r55;	 Catch:{ Exception -> 0x02ac }
        r1 = r16;	 Catch:{ Exception -> 0x02ac }
        r25 = r0.split(r1);	 Catch:{ Exception -> 0x02ac }
        r0 = r25;	 Catch:{ Exception -> 0x02ac }
        r0 = r0.length;	 Catch:{ Exception -> 0x02ac }
        r16 = r0;	 Catch:{ Exception -> 0x02ac }
        r17 = 2;	 Catch:{ Exception -> 0x02ac }
        r0 = r16;	 Catch:{ Exception -> 0x02ac }
        r1 = r17;	 Catch:{ Exception -> 0x02ac }
        if (r0 == r1) goto L_0x021c;	 Catch:{ Exception -> 0x02ac }
    L_0x01fa:
        r4 = r4 + 1;	 Catch:{ Exception -> 0x02ac }
        goto L_0x01de;	 Catch:{ Exception -> 0x02ac }
    L_0x01fd:
        r36 = 0;	 Catch:{ Exception -> 0x02ac }
        goto L_0x00c4;	 Catch:{ Exception -> 0x02ac }
    L_0x0201:
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = "END";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x01a8;	 Catch:{ Exception -> 0x02ac }
    L_0x020d:
        r4 = 1;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = "VCARD";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x01a8;	 Catch:{ Exception -> 0x02ac }
    L_0x0219:
        r32 = 0;	 Catch:{ Exception -> 0x02ac }
        goto L_0x01a8;	 Catch:{ Exception -> 0x02ac }
    L_0x021c:
        r16 = 0;	 Catch:{ Exception -> 0x02ac }
        r16 = r25[r16];	 Catch:{ Exception -> 0x02ac }
        r17 = "CHARSET";	 Catch:{ Exception -> 0x02ac }
        r16 = r16.equals(r17);	 Catch:{ Exception -> 0x02ac }
        if (r16 == 0) goto L_0x022e;	 Catch:{ Exception -> 0x02ac }
    L_0x0229:
        r16 = 1;	 Catch:{ Exception -> 0x02ac }
        r50 = r25[r16];	 Catch:{ Exception -> 0x02ac }
        goto L_0x01fa;	 Catch:{ Exception -> 0x02ac }
    L_0x022e:
        r16 = 0;	 Catch:{ Exception -> 0x02ac }
        r16 = r25[r16];	 Catch:{ Exception -> 0x02ac }
        r17 = "ENCODING";	 Catch:{ Exception -> 0x02ac }
        r16 = r16.equals(r17);	 Catch:{ Exception -> 0x02ac }
        if (r16 == 0) goto L_0x01fa;	 Catch:{ Exception -> 0x02ac }
    L_0x023b:
        r16 = 1;	 Catch:{ Exception -> 0x02ac }
        r51 = r25[r16];	 Catch:{ Exception -> 0x02ac }
        goto L_0x01fa;	 Catch:{ Exception -> 0x02ac }
    L_0x0240:
        r4 = 1;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r0.name = r4;	 Catch:{ Exception -> 0x02ac }
        if (r51 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x0249:
        r4 = "QUOTED-PRINTABLE";	 Catch:{ Exception -> 0x02ac }
        r0 = r51;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.equalsIgnoreCase(r4);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x0254:
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.name;	 Catch:{ Exception -> 0x02ac }
        r5 = "=";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.endsWith(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0284;	 Catch:{ Exception -> 0x02ac }
    L_0x0261:
        if (r51 == 0) goto L_0x0284;	 Catch:{ Exception -> 0x02ac }
    L_0x0263:
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.name;	 Catch:{ Exception -> 0x02ac }
        r5 = 0;	 Catch:{ Exception -> 0x02ac }
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r0 = r0.name;	 Catch:{ Exception -> 0x02ac }
        r16 = r0;	 Catch:{ Exception -> 0x02ac }
        r16 = r16.length();	 Catch:{ Exception -> 0x02ac }
        r16 = r16 + -1;	 Catch:{ Exception -> 0x02ac }
        r0 = r16;	 Catch:{ Exception -> 0x02ac }
        r4 = r4.substring(r5, r0);	 Catch:{ Exception -> 0x02ac }
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r0.name = r4;	 Catch:{ Exception -> 0x02ac }
        r48 = r27.readLine();	 Catch:{ Exception -> 0x02ac }
        if (r48 != 0) goto L_0x0392;	 Catch:{ Exception -> 0x02ac }
    L_0x0284:
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.name;	 Catch:{ Exception -> 0x02ac }
        r4 = r4.getBytes();	 Catch:{ Exception -> 0x02ac }
        r28 = org.telegram.messenger.AndroidUtilities.decodeQuotedPrintable(r4);	 Catch:{ Exception -> 0x02ac }
        if (r28 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x0292:
        r0 = r28;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.length;	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x0297:
        r35 = new java.lang.String;	 Catch:{ Exception -> 0x02ac }
        r0 = r35;	 Catch:{ Exception -> 0x02ac }
        r1 = r28;	 Catch:{ Exception -> 0x02ac }
        r2 = r50;	 Catch:{ Exception -> 0x02ac }
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x02ac }
        if (r35 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x02a4:
        r0 = r35;	 Catch:{ Exception -> 0x02ac }
        r1 = r32;	 Catch:{ Exception -> 0x02ac }
        r1.name = r0;	 Catch:{ Exception -> 0x02ac }
        goto L_0x0163;
    L_0x02ac:
        r40 = move-exception;
        org.telegram.messenger.FileLog.m3e(r40);
        r42 = 1;
    L_0x02b2:
        if (r42 == 0) goto L_0x02c1;
    L_0x02b4:
        r4 = "Unsupported content";
        r5 = 0;
        r0 = r85;
        r4 = android.widget.Toast.makeText(r0, r4, r5);
        r4.show();
    L_0x02c1:
        r4 = r65.intValue();
        if (r4 == 0) goto L_0x0d60;
    L_0x02c7:
        r24 = new android.os.Bundle;
        r24.<init>();
        r4 = "user_id";
        r5 = r65.intValue();
        r0 = r24;
        r0.putInt(r4, r5);
        r4 = r64.intValue();
        if (r4 == 0) goto L_0x02ea;
    L_0x02de:
        r4 = "message_id";
        r5 = r64.intValue();
        r0 = r24;
        r0.putInt(r4, r5);
    L_0x02ea:
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0313;
    L_0x02f2:
        r4 = 0;
        r4 = r47[r4];
        r5 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = mainFragmentsStack;
        r16 = mainFragmentsStack;
        r16 = r16.size();
        r16 = r16 + -1;
        r0 = r16;
        r4 = r4.get(r0);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r24;
        r4 = r5.checkCanOpenChat(r0, r4);
        if (r4 == 0) goto L_0x0333;
    L_0x0313:
        r44 = new org.telegram.ui.ChatActivity;
        r0 = r44;
        r1 = r24;
        r0.<init>(r1);
        r0 = r85;
        r4 = r0.actionBarLayout;
        r5 = 0;
        r16 = 1;
        r17 = 1;
        r0 = r44;
        r1 = r16;
        r2 = r17;
        r4 = r4.presentFragment(r0, r5, r1, r2);
        if (r4 == 0) goto L_0x0333;
    L_0x0331:
        r61 = 1;
    L_0x0333:
        if (r61 != 0) goto L_0x038a;
    L_0x0335:
        if (r87 != 0) goto L_0x038a;
    L_0x0337:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1188;
    L_0x033d:
        r0 = r85;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.isClientActivated();
        if (r4 != 0) goto L_0x1154;
    L_0x034b:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x036f;
    L_0x0357:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r4.addFragmentToStack(r5);
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
    L_0x036f:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x038a;
    L_0x037c:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r4.showLastFragment();
        r0 = r85;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
    L_0x038a:
        r4 = 0;
        r0 = r86;
        r0.setAction(r4);
        goto L_0x0008;
    L_0x0392:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02ac }
        r4.<init>();	 Catch:{ Exception -> 0x02ac }
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r5 = r0.name;	 Catch:{ Exception -> 0x02ac }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x02ac }
        r0 = r48;	 Catch:{ Exception -> 0x02ac }
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x02ac }
        r4 = r4.toString();	 Catch:{ Exception -> 0x02ac }
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r0.name = r4;	 Catch:{ Exception -> 0x02ac }
        goto L_0x0254;	 Catch:{ Exception -> 0x02ac }
    L_0x03af:
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = "TEL";	 Catch:{ Exception -> 0x02ac }
        r4 = r4.startsWith(r5);	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x03bb:
        r4 = 1;	 Catch:{ Exception -> 0x02ac }
        r4 = r24[r4];	 Catch:{ Exception -> 0x02ac }
        r5 = 1;	 Catch:{ Exception -> 0x02ac }
        r59 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r4, r5);	 Catch:{ Exception -> 0x02ac }
        r4 = r59.length();	 Catch:{ Exception -> 0x02ac }
        if (r4 <= 0) goto L_0x0163;	 Catch:{ Exception -> 0x02ac }
    L_0x03c9:
        r0 = r32;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.phones;	 Catch:{ Exception -> 0x02ac }
        r0 = r59;	 Catch:{ Exception -> 0x02ac }
        r4.add(r0);	 Catch:{ Exception -> 0x02ac }
        goto L_0x0163;
    L_0x03d4:
        r27.close();	 Catch:{ Exception -> 0x0452 }
        r72.close();	 Catch:{ Exception -> 0x0452 }
    L_0x03da:
        r22 = 0;
    L_0x03dc:
        r4 = r84.size();	 Catch:{ Exception -> 0x02ac }
        r0 = r22;	 Catch:{ Exception -> 0x02ac }
        if (r0 >= r4) goto L_0x02b2;	 Catch:{ Exception -> 0x02ac }
    L_0x03e4:
        r0 = r84;	 Catch:{ Exception -> 0x02ac }
        r1 = r22;	 Catch:{ Exception -> 0x02ac }
        r83 = r0.get(r1);	 Catch:{ Exception -> 0x02ac }
        r83 = (org.telegram.ui.LaunchActivity.VcardData) r83;	 Catch:{ Exception -> 0x02ac }
        r0 = r83;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.name;	 Catch:{ Exception -> 0x02ac }
        if (r4 == 0) goto L_0x0457;	 Catch:{ Exception -> 0x02ac }
    L_0x03f4:
        r0 = r83;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.phones;	 Catch:{ Exception -> 0x02ac }
        r4 = r4.isEmpty();	 Catch:{ Exception -> 0x02ac }
        if (r4 != 0) goto L_0x0457;	 Catch:{ Exception -> 0x02ac }
    L_0x03fe:
        r0 = r85;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.contactsToSend;	 Catch:{ Exception -> 0x02ac }
        if (r4 != 0) goto L_0x040d;	 Catch:{ Exception -> 0x02ac }
    L_0x0404:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x02ac }
        r4.<init>();	 Catch:{ Exception -> 0x02ac }
        r0 = r85;	 Catch:{ Exception -> 0x02ac }
        r0.contactsToSend = r4;	 Catch:{ Exception -> 0x02ac }
    L_0x040d:
        r26 = 0;	 Catch:{ Exception -> 0x02ac }
    L_0x040f:
        r0 = r83;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.phones;	 Catch:{ Exception -> 0x02ac }
        r4 = r4.size();	 Catch:{ Exception -> 0x02ac }
        r0 = r26;	 Catch:{ Exception -> 0x02ac }
        if (r0 >= r4) goto L_0x0457;	 Catch:{ Exception -> 0x02ac }
    L_0x041b:
        r0 = r83;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.phones;	 Catch:{ Exception -> 0x02ac }
        r0 = r26;	 Catch:{ Exception -> 0x02ac }
        r59 = r4.get(r0);	 Catch:{ Exception -> 0x02ac }
        r59 = (java.lang.String) r59;	 Catch:{ Exception -> 0x02ac }
        r80 = new org.telegram.tgnet.TLRPC$TL_userContact_old2;	 Catch:{ Exception -> 0x02ac }
        r80.<init>();	 Catch:{ Exception -> 0x02ac }
        r0 = r59;	 Catch:{ Exception -> 0x02ac }
        r1 = r80;	 Catch:{ Exception -> 0x02ac }
        r1.phone = r0;	 Catch:{ Exception -> 0x02ac }
        r0 = r83;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.name;	 Catch:{ Exception -> 0x02ac }
        r0 = r80;	 Catch:{ Exception -> 0x02ac }
        r0.first_name = r4;	 Catch:{ Exception -> 0x02ac }
        r4 = "";	 Catch:{ Exception -> 0x02ac }
        r0 = r80;	 Catch:{ Exception -> 0x02ac }
        r0.last_name = r4;	 Catch:{ Exception -> 0x02ac }
        r4 = 0;	 Catch:{ Exception -> 0x02ac }
        r0 = r80;	 Catch:{ Exception -> 0x02ac }
        r0.id = r4;	 Catch:{ Exception -> 0x02ac }
        r0 = r85;	 Catch:{ Exception -> 0x02ac }
        r4 = r0.contactsToSend;	 Catch:{ Exception -> 0x02ac }
        r0 = r80;	 Catch:{ Exception -> 0x02ac }
        r4.add(r0);	 Catch:{ Exception -> 0x02ac }
        r26 = r26 + 1;	 Catch:{ Exception -> 0x02ac }
        goto L_0x040f;	 Catch:{ Exception -> 0x02ac }
    L_0x0452:
        r40 = move-exception;	 Catch:{ Exception -> 0x02ac }
        org.telegram.messenger.FileLog.m3e(r40);	 Catch:{ Exception -> 0x02ac }
        goto L_0x03da;
    L_0x0457:
        r22 = r22 + 1;
        goto L_0x03dc;
    L_0x045a:
        r42 = 1;
        goto L_0x02b2;
    L_0x045e:
        r4 = "android.intent.extra.TEXT";
        r0 = r86;
        r74 = r0.getStringExtra(r4);
        if (r74 != 0) goto L_0x0478;
    L_0x0469:
        r4 = "android.intent.extra.TEXT";
        r0 = r86;
        r75 = r0.getCharSequenceExtra(r4);
        if (r75 == 0) goto L_0x0478;
    L_0x0474:
        r74 = r75.toString();
    L_0x0478:
        r4 = "android.intent.extra.SUBJECT";
        r0 = r86;
        r73 = r0.getStringExtra(r4);
        if (r74 == 0) goto L_0x0537;
    L_0x0483:
        r4 = r74.length();
        if (r4 == 0) goto L_0x0537;
    L_0x0489:
        r4 = "http://";
        r0 = r74;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x049f;
    L_0x0494:
        r4 = "https://";
        r0 = r74;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x04c3;
    L_0x049f:
        if (r73 == 0) goto L_0x04c3;
    L_0x04a1:
        r4 = r73.length();
        if (r4 == 0) goto L_0x04c3;
    L_0x04a7:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r73;
        r4 = r4.append(r0);
        r5 = "\n";
        r4 = r4.append(r5);
        r0 = r74;
        r4 = r4.append(r0);
        r74 = r4.toString();
    L_0x04c3:
        r0 = r74;
        r1 = r85;
        r1.sendingText = r0;
    L_0x04c9:
        r4 = "android.intent.extra.STREAM";
        r0 = r86;
        r57 = r0.getParcelableExtra(r4);
        if (r57 == 0) goto L_0x05c6;
    L_0x04d4:
        r0 = r57;
        r4 = r0 instanceof android.net.Uri;
        if (r4 != 0) goto L_0x04e2;
    L_0x04da:
        r4 = r57.toString();
        r57 = android.net.Uri.parse(r4);
    L_0x04e2:
        r77 = r57;
        r77 = (android.net.Uri) r77;
        if (r77 == 0) goto L_0x04f0;
    L_0x04e8:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r77);
        if (r4 == 0) goto L_0x04f0;
    L_0x04ee:
        r42 = 1;
    L_0x04f0:
        if (r42 != 0) goto L_0x02b2;
    L_0x04f2:
        if (r77 == 0) goto L_0x0546;
    L_0x04f4:
        if (r76 == 0) goto L_0x0501;
    L_0x04f6:
        r4 = "image/";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0512;
    L_0x0501:
        r4 = r77.toString();
        r4 = r4.toLowerCase();
        r5 = ".jpg";
        r4 = r4.endsWith(r5);
        if (r4 == 0) goto L_0x0546;
    L_0x0512:
        r0 = r85;
        r4 = r0.photoPathsArray;
        if (r4 != 0) goto L_0x0521;
    L_0x0518:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r85;
        r0.photoPathsArray = r4;
    L_0x0521:
        r46 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;
        r46.<init>();
        r0 = r77;
        r1 = r46;
        r1.uri = r0;
        r0 = r85;
        r4 = r0.photoPathsArray;
        r0 = r46;
        r4.add(r0);
        goto L_0x02b2;
    L_0x0537:
        if (r73 == 0) goto L_0x04c9;
    L_0x0539:
        r4 = r73.length();
        if (r4 <= 0) goto L_0x04c9;
    L_0x053f:
        r0 = r73;
        r1 = r85;
        r1.sendingText = r0;
        goto L_0x04c9;
    L_0x0546:
        r58 = org.telegram.messenger.AndroidUtilities.getPath(r77);
        if (r58 == 0) goto L_0x05a6;
    L_0x054c:
        r4 = "file:";
        r0 = r58;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0563;
    L_0x0557:
        r4 = "file://";
        r5 = "";
        r0 = r58;
        r58 = r0.replace(r4, r5);
    L_0x0563:
        if (r76 == 0) goto L_0x0578;
    L_0x0565:
        r4 = "video/";
        r0 = r76;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0578;
    L_0x0570:
        r0 = r58;
        r1 = r85;
        r1.videoPath = r0;
        goto L_0x02b2;
    L_0x0578:
        r0 = r85;
        r4 = r0.documentsPathsArray;
        if (r4 != 0) goto L_0x0590;
    L_0x057e:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r85;
        r0.documentsPathsArray = r4;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r85;
        r0.documentsOriginalPathsArray = r4;
    L_0x0590:
        r0 = r85;
        r4 = r0.documentsPathsArray;
        r0 = r58;
        r4.add(r0);
        r0 = r85;
        r4 = r0.documentsOriginalPathsArray;
        r5 = r77.toString();
        r4.add(r5);
        goto L_0x02b2;
    L_0x05a6:
        r0 = r85;
        r4 = r0.documentsUrisArray;
        if (r4 != 0) goto L_0x05b5;
    L_0x05ac:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r85;
        r0.documentsUrisArray = r4;
    L_0x05b5:
        r0 = r85;
        r4 = r0.documentsUrisArray;
        r0 = r77;
        r4.add(r0);
        r0 = r76;
        r1 = r85;
        r1.documentsMimeType = r0;
        goto L_0x02b2;
    L_0x05c6:
        r0 = r85;
        r4 = r0.sendingText;
        if (r4 != 0) goto L_0x02b2;
    L_0x05cc:
        r42 = 1;
        goto L_0x02b2;
    L_0x05d0:
        r4 = r86.getAction();
        r5 = "android.intent.action.SEND_MULTIPLE";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0742;
    L_0x05dd:
        r42 = 0;
        r4 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x0728 }
        r0 = r86;	 Catch:{ Exception -> 0x0728 }
        r78 = r0.getParcelableArrayListExtra(r4);	 Catch:{ Exception -> 0x0728 }
        r76 = r86.getType();	 Catch:{ Exception -> 0x0728 }
        if (r78 == 0) goto L_0x0632;	 Catch:{ Exception -> 0x0728 }
    L_0x05ee:
        r22 = 0;	 Catch:{ Exception -> 0x0728 }
    L_0x05f0:
        r4 = r78.size();	 Catch:{ Exception -> 0x0728 }
        r0 = r22;	 Catch:{ Exception -> 0x0728 }
        if (r0 >= r4) goto L_0x062a;	 Catch:{ Exception -> 0x0728 }
    L_0x05f8:
        r0 = r78;	 Catch:{ Exception -> 0x0728 }
        r1 = r22;	 Catch:{ Exception -> 0x0728 }
        r57 = r0.get(r1);	 Catch:{ Exception -> 0x0728 }
        r57 = (android.os.Parcelable) r57;	 Catch:{ Exception -> 0x0728 }
        r0 = r57;	 Catch:{ Exception -> 0x0728 }
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0728 }
        if (r4 != 0) goto L_0x0610;	 Catch:{ Exception -> 0x0728 }
    L_0x0608:
        r4 = r57.toString();	 Catch:{ Exception -> 0x0728 }
        r57 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0728 }
    L_0x0610:
        r0 = r57;	 Catch:{ Exception -> 0x0728 }
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0728 }
        r77 = r0;	 Catch:{ Exception -> 0x0728 }
        if (r77 == 0) goto L_0x0627;	 Catch:{ Exception -> 0x0728 }
    L_0x0618:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r77);	 Catch:{ Exception -> 0x0728 }
        if (r4 == 0) goto L_0x0627;	 Catch:{ Exception -> 0x0728 }
    L_0x061e:
        r0 = r78;	 Catch:{ Exception -> 0x0728 }
        r1 = r22;	 Catch:{ Exception -> 0x0728 }
        r0.remove(r1);	 Catch:{ Exception -> 0x0728 }
        r22 = r22 + -1;	 Catch:{ Exception -> 0x0728 }
    L_0x0627:
        r22 = r22 + 1;	 Catch:{ Exception -> 0x0728 }
        goto L_0x05f0;	 Catch:{ Exception -> 0x0728 }
    L_0x062a:
        r4 = r78.isEmpty();	 Catch:{ Exception -> 0x0728 }
        if (r4 == 0) goto L_0x0632;	 Catch:{ Exception -> 0x0728 }
    L_0x0630:
        r78 = 0;	 Catch:{ Exception -> 0x0728 }
    L_0x0632:
        if (r78 == 0) goto L_0x073f;	 Catch:{ Exception -> 0x0728 }
    L_0x0634:
        if (r76 == 0) goto L_0x068f;	 Catch:{ Exception -> 0x0728 }
    L_0x0636:
        r4 = "image/";	 Catch:{ Exception -> 0x0728 }
        r0 = r76;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0728 }
        if (r4 == 0) goto L_0x068f;	 Catch:{ Exception -> 0x0728 }
    L_0x0641:
        r22 = 0;	 Catch:{ Exception -> 0x0728 }
    L_0x0643:
        r4 = r78.size();	 Catch:{ Exception -> 0x0728 }
        r0 = r22;	 Catch:{ Exception -> 0x0728 }
        if (r0 >= r4) goto L_0x072e;	 Catch:{ Exception -> 0x0728 }
    L_0x064b:
        r0 = r78;	 Catch:{ Exception -> 0x0728 }
        r1 = r22;	 Catch:{ Exception -> 0x0728 }
        r57 = r0.get(r1);	 Catch:{ Exception -> 0x0728 }
        r57 = (android.os.Parcelable) r57;	 Catch:{ Exception -> 0x0728 }
        r0 = r57;	 Catch:{ Exception -> 0x0728 }
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0728 }
        if (r4 != 0) goto L_0x0663;	 Catch:{ Exception -> 0x0728 }
    L_0x065b:
        r4 = r57.toString();	 Catch:{ Exception -> 0x0728 }
        r57 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0728 }
    L_0x0663:
        r0 = r57;	 Catch:{ Exception -> 0x0728 }
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0728 }
        r77 = r0;	 Catch:{ Exception -> 0x0728 }
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.photoPathsArray;	 Catch:{ Exception -> 0x0728 }
        if (r4 != 0) goto L_0x0678;	 Catch:{ Exception -> 0x0728 }
    L_0x066f:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0728 }
        r4.<init>();	 Catch:{ Exception -> 0x0728 }
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r0.photoPathsArray = r4;	 Catch:{ Exception -> 0x0728 }
    L_0x0678:
        r46 = new org.telegram.messenger.SendMessagesHelper$SendingMediaInfo;	 Catch:{ Exception -> 0x0728 }
        r46.<init>();	 Catch:{ Exception -> 0x0728 }
        r0 = r77;	 Catch:{ Exception -> 0x0728 }
        r1 = r46;	 Catch:{ Exception -> 0x0728 }
        r1.uri = r0;	 Catch:{ Exception -> 0x0728 }
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.photoPathsArray;	 Catch:{ Exception -> 0x0728 }
        r0 = r46;	 Catch:{ Exception -> 0x0728 }
        r4.add(r0);	 Catch:{ Exception -> 0x0728 }
        r22 = r22 + 1;	 Catch:{ Exception -> 0x0728 }
        goto L_0x0643;	 Catch:{ Exception -> 0x0728 }
    L_0x068f:
        r22 = 0;	 Catch:{ Exception -> 0x0728 }
    L_0x0691:
        r4 = r78.size();	 Catch:{ Exception -> 0x0728 }
        r0 = r22;	 Catch:{ Exception -> 0x0728 }
        if (r0 >= r4) goto L_0x072e;	 Catch:{ Exception -> 0x0728 }
    L_0x0699:
        r0 = r78;	 Catch:{ Exception -> 0x0728 }
        r1 = r22;	 Catch:{ Exception -> 0x0728 }
        r57 = r0.get(r1);	 Catch:{ Exception -> 0x0728 }
        r57 = (android.os.Parcelable) r57;	 Catch:{ Exception -> 0x0728 }
        r0 = r57;	 Catch:{ Exception -> 0x0728 }
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0728 }
        if (r4 != 0) goto L_0x06b1;	 Catch:{ Exception -> 0x0728 }
    L_0x06a9:
        r4 = r57.toString();	 Catch:{ Exception -> 0x0728 }
        r57 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0728 }
    L_0x06b1:
        r0 = r57;	 Catch:{ Exception -> 0x0728 }
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0728 }
        r77 = r0;	 Catch:{ Exception -> 0x0728 }
        r58 = org.telegram.messenger.AndroidUtilities.getPath(r77);	 Catch:{ Exception -> 0x0728 }
        r54 = r57.toString();	 Catch:{ Exception -> 0x0728 }
        if (r54 != 0) goto L_0x06c3;	 Catch:{ Exception -> 0x0728 }
    L_0x06c1:
        r54 = r58;	 Catch:{ Exception -> 0x0728 }
    L_0x06c3:
        if (r58 == 0) goto L_0x0709;	 Catch:{ Exception -> 0x0728 }
    L_0x06c5:
        r4 = "file:";	 Catch:{ Exception -> 0x0728 }
        r0 = r58;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0728 }
        if (r4 == 0) goto L_0x06dc;	 Catch:{ Exception -> 0x0728 }
    L_0x06d0:
        r4 = "file://";	 Catch:{ Exception -> 0x0728 }
        r5 = "";	 Catch:{ Exception -> 0x0728 }
        r0 = r58;	 Catch:{ Exception -> 0x0728 }
        r58 = r0.replace(r4, r5);	 Catch:{ Exception -> 0x0728 }
    L_0x06dc:
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.documentsPathsArray;	 Catch:{ Exception -> 0x0728 }
        if (r4 != 0) goto L_0x06f4;	 Catch:{ Exception -> 0x0728 }
    L_0x06e2:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0728 }
        r4.<init>();	 Catch:{ Exception -> 0x0728 }
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r0.documentsPathsArray = r4;	 Catch:{ Exception -> 0x0728 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0728 }
        r4.<init>();	 Catch:{ Exception -> 0x0728 }
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r0.documentsOriginalPathsArray = r4;	 Catch:{ Exception -> 0x0728 }
    L_0x06f4:
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.documentsPathsArray;	 Catch:{ Exception -> 0x0728 }
        r0 = r58;	 Catch:{ Exception -> 0x0728 }
        r4.add(r0);	 Catch:{ Exception -> 0x0728 }
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.documentsOriginalPathsArray;	 Catch:{ Exception -> 0x0728 }
        r0 = r54;	 Catch:{ Exception -> 0x0728 }
        r4.add(r0);	 Catch:{ Exception -> 0x0728 }
    L_0x0706:
        r22 = r22 + 1;	 Catch:{ Exception -> 0x0728 }
        goto L_0x0691;	 Catch:{ Exception -> 0x0728 }
    L_0x0709:
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.documentsUrisArray;	 Catch:{ Exception -> 0x0728 }
        if (r4 != 0) goto L_0x0718;	 Catch:{ Exception -> 0x0728 }
    L_0x070f:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0728 }
        r4.<init>();	 Catch:{ Exception -> 0x0728 }
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r0.documentsUrisArray = r4;	 Catch:{ Exception -> 0x0728 }
    L_0x0718:
        r0 = r85;	 Catch:{ Exception -> 0x0728 }
        r4 = r0.documentsUrisArray;	 Catch:{ Exception -> 0x0728 }
        r0 = r77;	 Catch:{ Exception -> 0x0728 }
        r4.add(r0);	 Catch:{ Exception -> 0x0728 }
        r0 = r76;	 Catch:{ Exception -> 0x0728 }
        r1 = r85;	 Catch:{ Exception -> 0x0728 }
        r1.documentsMimeType = r0;	 Catch:{ Exception -> 0x0728 }
        goto L_0x0706;
    L_0x0728:
        r40 = move-exception;
        org.telegram.messenger.FileLog.m3e(r40);
        r42 = 1;
    L_0x072e:
        if (r42 == 0) goto L_0x02c1;
    L_0x0730:
        r4 = "Unsupported content";
        r5 = 0;
        r0 = r85;
        r4 = android.widget.Toast.makeText(r0, r4, r5);
        r4.show();
        goto L_0x02c1;
    L_0x073f:
        r42 = 1;
        goto L_0x072e;
    L_0x0742:
        r4 = "android.intent.action.VIEW";
        r5 = r86.getAction();
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0c8d;
    L_0x074f:
        r34 = r86.getData();
        if (r34 == 0) goto L_0x02c1;
    L_0x0755:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r15 = 0;
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r59 = 0;
        r14 = 0;
        r60 = 0;
        r13 = 0;
        r12 = 0;
        r67 = r34.getScheme();
        if (r67 == 0) goto L_0x07de;
    L_0x0769:
        r4 = "http";
        r0 = r67;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x077f;
    L_0x0774:
        r4 = "https";
        r0 = r67;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0971;
    L_0x077f:
        r4 = r34.getHost();
        r45 = r4.toLowerCase();
        r4 = "telegram.me";
        r0 = r45;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x07b3;
    L_0x0792:
        r4 = "t.me";
        r0 = r45;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x07b3;
    L_0x079d:
        r4 = "telegram.dog";
        r0 = r45;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x07b3;
    L_0x07a8:
        r4 = "telesco.pe";
        r0 = r45;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x07de;
    L_0x07b3:
        r58 = r34.getPath();
        if (r58 == 0) goto L_0x07de;
    L_0x07b9:
        r4 = r58.length();
        r5 = 1;
        if (r4 <= r5) goto L_0x07de;
    L_0x07c0:
        r4 = 1;
        r0 = r58;
        r58 = r0.substring(r4);
        r4 = "joinchat/";
        r0 = r58;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0828;
    L_0x07d2:
        r4 = "joinchat/";
        r5 = "";
        r0 = r58;
        r7 = r0.replace(r4, r5);
    L_0x07de:
        if (r11 == 0) goto L_0x07fd;
    L_0x07e0:
        r4 = "@";
        r4 = r11.startsWith(r4);
        if (r4 == 0) goto L_0x07fd;
    L_0x07e9:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = " ";
        r4 = r4.append(r5);
        r4 = r4.append(r11);
        r11 = r4.toString();
    L_0x07fd:
        if (r59 != 0) goto L_0x0801;
    L_0x07ff:
        if (r60 == 0) goto L_0x0bec;
    L_0x0801:
        r24 = new android.os.Bundle;
        r24.<init>();
        r4 = "phone";
        r0 = r24;
        r1 = r59;
        r0.putString(r4, r1);
        r4 = "hash";
        r0 = r24;
        r1 = r60;
        r0.putString(r4, r1);
        r4 = new org.telegram.ui.LaunchActivity$7;
        r0 = r85;
        r1 = r24;
        r4.<init>(r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        goto L_0x02c1;
    L_0x0828:
        r4 = "addstickers/";
        r0 = r58;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0840;
    L_0x0833:
        r4 = "addstickers/";
        r5 = "";
        r0 = r58;
        r8 = r0.replace(r4, r5);
        goto L_0x07de;
    L_0x0840:
        r4 = "iv/";
        r0 = r58;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0878;
    L_0x084b:
        r4 = 0;
        r5 = "url";
        r0 = r34;
        r5 = r0.getQueryParameter(r5);
        r15[r4] = r5;
        r4 = 1;
        r5 = "rhash";
        r0 = r34;
        r5 = r0.getQueryParameter(r5);
        r15[r4] = r5;
        r4 = 0;
        r4 = r15[r4];
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0875;
    L_0x086c:
        r4 = 1;
        r4 = r15[r4];
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x07de;
    L_0x0875:
        r15 = 0;
        goto L_0x07de;
    L_0x0878:
        r4 = "msg/";
        r0 = r58;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x088e;
    L_0x0883:
        r4 = "share/";
        r0 = r58;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0900;
    L_0x088e:
        r4 = "url";
        r0 = r34;
        r11 = r0.getQueryParameter(r4);
        if (r11 != 0) goto L_0x089c;
    L_0x0899:
        r11 = "";
    L_0x089c:
        r4 = "text";
        r0 = r34;
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x08dc;
    L_0x08a7:
        r4 = r11.length();
        if (r4 <= 0) goto L_0x08c2;
    L_0x08ad:
        r12 = 1;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "\n";
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x08c2:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "text";
        r0 = r34;
        r5 = r0.getQueryParameter(r5);
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x08dc:
        r4 = r11.length();
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r4 <= r5) goto L_0x08eb;
    L_0x08e4:
        r4 = 0;
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r11 = r11.substring(r4, r5);
    L_0x08eb:
        r4 = "\n";
        r4 = r11.endsWith(r4);
        if (r4 == 0) goto L_0x07de;
    L_0x08f4:
        r4 = 0;
        r5 = r11.length();
        r5 = r5 + -1;
        r11 = r11.substring(r4, r5);
        goto L_0x08eb;
    L_0x0900:
        r4 = "confirmphone";
        r0 = r58;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x091f;
    L_0x090b:
        r4 = "phone";
        r0 = r34;
        r59 = r0.getQueryParameter(r4);
        r4 = "hash";
        r0 = r34;
        r60 = r0.getQueryParameter(r4);
        goto L_0x07de;
    L_0x091f:
        r4 = r58.length();
        r5 = 1;
        if (r4 < r5) goto L_0x07de;
    L_0x0926:
        r68 = r34.getPathSegments();
        r4 = r68.size();
        if (r4 <= 0) goto L_0x0954;
    L_0x0930:
        r4 = 0;
        r0 = r68;
        r6 = r0.get(r4);
        r6 = (java.lang.String) r6;
        r4 = r68.size();
        r5 = 1;
        if (r4 <= r5) goto L_0x0954;
    L_0x0940:
        r4 = 1;
        r0 = r68;
        r4 = r0.get(r4);
        r4 = (java.lang.String) r4;
        r13 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r13.intValue();
        if (r4 != 0) goto L_0x0954;
    L_0x0953:
        r13 = 0;
    L_0x0954:
        r4 = "start";
        r0 = r34;
        r9 = r0.getQueryParameter(r4);
        r4 = "startgroup";
        r0 = r34;
        r10 = r0.getQueryParameter(r4);
        r4 = "game";
        r0 = r34;
        r14 = r0.getQueryParameter(r4);
        goto L_0x07de;
    L_0x0971:
        r4 = "tg";
        r0 = r67;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x07de;
    L_0x097c:
        r79 = r34.toString();
        r4 = "tg:resolve";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0996;
    L_0x098b:
        r4 = "tg://resolve";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x09ec;
    L_0x0996:
        r4 = "tg:resolve";
        r5 = "tg://telegram.org";
        r0 = r79;
        r4 = r0.replace(r4, r5);
        r5 = "tg://resolve";
        r16 = "tg://telegram.org";
        r0 = r16;
        r79 = r4.replace(r5, r0);
        r34 = android.net.Uri.parse(r79);
        r4 = "domain";
        r0 = r34;
        r6 = r0.getQueryParameter(r4);
        r4 = "start";
        r0 = r34;
        r9 = r0.getQueryParameter(r4);
        r4 = "startgroup";
        r0 = r34;
        r10 = r0.getQueryParameter(r4);
        r4 = "game";
        r0 = r34;
        r14 = r0.getQueryParameter(r4);
        r4 = "post";
        r0 = r34;
        r4 = r0.getQueryParameter(r4);
        r13 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r13.intValue();
        if (r4 != 0) goto L_0x07de;
    L_0x09e9:
        r13 = 0;
        goto L_0x07de;
    L_0x09ec:
        r4 = "tg:join";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0a02;
    L_0x09f7:
        r4 = "tg://join";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0a29;
    L_0x0a02:
        r4 = "tg:join";
        r5 = "tg://telegram.org";
        r0 = r79;
        r4 = r0.replace(r4, r5);
        r5 = "tg://join";
        r16 = "tg://telegram.org";
        r0 = r16;
        r79 = r4.replace(r5, r0);
        r34 = android.net.Uri.parse(r79);
        r4 = "invite";
        r0 = r34;
        r7 = r0.getQueryParameter(r4);
        goto L_0x07de;
    L_0x0a29:
        r4 = "tg:addstickers";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0a3f;
    L_0x0a34:
        r4 = "tg://addstickers";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0a66;
    L_0x0a3f:
        r4 = "tg:addstickers";
        r5 = "tg://telegram.org";
        r0 = r79;
        r4 = r0.replace(r4, r5);
        r5 = "tg://addstickers";
        r16 = "tg://telegram.org";
        r0 = r16;
        r79 = r4.replace(r5, r0);
        r34 = android.net.Uri.parse(r79);
        r4 = "set";
        r0 = r34;
        r8 = r0.getQueryParameter(r4);
        goto L_0x07de;
    L_0x0a66:
        r4 = "tg:msg";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0a92;
    L_0x0a71:
        r4 = "tg://msg";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0a92;
    L_0x0a7c:
        r4 = "tg://share";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0a92;
    L_0x0a87:
        r4 = "tg:share";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0b38;
    L_0x0a92:
        r4 = "tg:msg";
        r5 = "tg://telegram.org";
        r0 = r79;
        r4 = r0.replace(r4, r5);
        r5 = "tg://msg";
        r16 = "tg://telegram.org";
        r0 = r16;
        r4 = r4.replace(r5, r0);
        r5 = "tg://share";
        r16 = "tg://telegram.org";
        r0 = r16;
        r4 = r4.replace(r5, r0);
        r5 = "tg:share";
        r16 = "tg://telegram.org";
        r0 = r16;
        r79 = r4.replace(r5, r0);
        r34 = android.net.Uri.parse(r79);
        r4 = "url";
        r0 = r34;
        r11 = r0.getQueryParameter(r4);
        if (r11 != 0) goto L_0x0ad4;
    L_0x0ad1:
        r11 = "";
    L_0x0ad4:
        r4 = "text";
        r0 = r34;
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x0b14;
    L_0x0adf:
        r4 = r11.length();
        if (r4 <= 0) goto L_0x0afa;
    L_0x0ae5:
        r12 = 1;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "\n";
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x0afa:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r11);
        r5 = "text";
        r0 = r34;
        r5 = r0.getQueryParameter(r5);
        r4 = r4.append(r5);
        r11 = r4.toString();
    L_0x0b14:
        r4 = r11.length();
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r4 <= r5) goto L_0x0b23;
    L_0x0b1c:
        r4 = 0;
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        r11 = r11.substring(r4, r5);
    L_0x0b23:
        r4 = "\n";
        r4 = r11.endsWith(r4);
        if (r4 == 0) goto L_0x07de;
    L_0x0b2c:
        r4 = 0;
        r5 = r11.length();
        r5 = r5 + -1;
        r11 = r11.substring(r4, r5);
        goto L_0x0b23;
    L_0x0b38:
        r4 = "tg:confirmphone";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0b4e;
    L_0x0b43:
        r4 = "tg://confirmphone";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0b7e;
    L_0x0b4e:
        r4 = "tg:confirmphone";
        r5 = "tg://telegram.org";
        r0 = r79;
        r4 = r0.replace(r4, r5);
        r5 = "tg://confirmphone";
        r16 = "tg://telegram.org";
        r0 = r16;
        r79 = r4.replace(r5, r0);
        r34 = android.net.Uri.parse(r79);
        r4 = "phone";
        r0 = r34;
        r59 = r0.getQueryParameter(r4);
        r4 = "hash";
        r0 = r34;
        r60 = r0.getQueryParameter(r4);
        goto L_0x07de;
    L_0x0b7e:
        r4 = "tg:openmessage";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0b94;
    L_0x0b89:
        r4 = "tg://openmessage";
        r0 = r79;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x07de;
    L_0x0b94:
        r4 = "tg:openmessage";
        r5 = "tg://telegram.org";
        r0 = r79;
        r4 = r0.replace(r4, r5);
        r5 = "tg://openmessage";
        r16 = "tg://telegram.org";
        r0 = r16;
        r79 = r4.replace(r5, r0);
        r34 = android.net.Uri.parse(r79);
        r4 = "user_id";
        r0 = r34;
        r81 = r0.getQueryParameter(r4);
        r4 = "chat_id";
        r0 = r34;
        r29 = r0.getQueryParameter(r4);
        r4 = "message_id";
        r0 = r34;
        r49 = r0.getQueryParameter(r4);
        if (r81 == 0) goto L_0x0be1;
    L_0x0bcd:
        r4 = java.lang.Integer.parseInt(r81);	 Catch:{ NumberFormatException -> 0x11ea }
        r65 = java.lang.Integer.valueOf(r4);	 Catch:{ NumberFormatException -> 0x11ea }
    L_0x0bd5:
        if (r49 == 0) goto L_0x07de;
    L_0x0bd7:
        r4 = java.lang.Integer.parseInt(r49);	 Catch:{ NumberFormatException -> 0x11e4 }
        r64 = java.lang.Integer.valueOf(r4);	 Catch:{ NumberFormatException -> 0x11e4 }
        goto L_0x07de;
    L_0x0be1:
        if (r29 == 0) goto L_0x0bd5;
    L_0x0be3:
        r4 = java.lang.Integer.parseInt(r29);	 Catch:{ NumberFormatException -> 0x11e7 }
        r62 = java.lang.Integer.valueOf(r4);	 Catch:{ NumberFormatException -> 0x11e7 }
        goto L_0x0bd5;
    L_0x0bec:
        if (r6 != 0) goto L_0x0bf8;
    L_0x0bee:
        if (r7 != 0) goto L_0x0bf8;
    L_0x0bf0:
        if (r8 != 0) goto L_0x0bf8;
    L_0x0bf2:
        if (r11 != 0) goto L_0x0bf8;
    L_0x0bf4:
        if (r14 != 0) goto L_0x0bf8;
    L_0x0bf6:
        if (r15 == 0) goto L_0x0c04;
    L_0x0bf8:
        r4 = 0;
        r5 = r47[r4];
        r16 = 0;
        r4 = r85;
        r4.runLinkRequest(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16);
        goto L_0x02c1;
    L_0x0c04:
        r16 = r85.getContentResolver();	 Catch:{ Exception -> 0x0c84 }
        r17 = r86.getData();	 Catch:{ Exception -> 0x0c84 }
        r18 = 0;	 Catch:{ Exception -> 0x0c84 }
        r19 = 0;	 Catch:{ Exception -> 0x0c84 }
        r20 = 0;	 Catch:{ Exception -> 0x0c84 }
        r21 = 0;	 Catch:{ Exception -> 0x0c84 }
        r33 = r16.query(r17, r18, r19, r20, r21);	 Catch:{ Exception -> 0x0c84 }
        if (r33 == 0) goto L_0x02c1;	 Catch:{ Exception -> 0x0c84 }
    L_0x0c1a:
        r4 = r33.moveToFirst();	 Catch:{ Exception -> 0x0c84 }
        if (r4 == 0) goto L_0x0c7f;	 Catch:{ Exception -> 0x0c84 }
    L_0x0c20:
        r4 = "account_name";	 Catch:{ Exception -> 0x0c84 }
        r0 = r33;	 Catch:{ Exception -> 0x0c84 }
        r4 = r0.getColumnIndex(r4);	 Catch:{ Exception -> 0x0c84 }
        r0 = r33;	 Catch:{ Exception -> 0x0c84 }
        r4 = r0.getString(r4);	 Catch:{ Exception -> 0x0c84 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Exception -> 0x0c84 }
        r23 = r4.intValue();	 Catch:{ Exception -> 0x0c84 }
        r22 = 0;	 Catch:{ Exception -> 0x0c84 }
    L_0x0c39:
        r4 = 3;	 Catch:{ Exception -> 0x0c84 }
        r0 = r22;	 Catch:{ Exception -> 0x0c84 }
        if (r0 >= r4) goto L_0x0c56;	 Catch:{ Exception -> 0x0c84 }
    L_0x0c3e:
        r4 = org.telegram.messenger.UserConfig.getInstance(r22);	 Catch:{ Exception -> 0x0c84 }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x0c84 }
        r0 = r23;	 Catch:{ Exception -> 0x0c84 }
        if (r4 != r0) goto L_0x0c8a;	 Catch:{ Exception -> 0x0c84 }
    L_0x0c4a:
        r4 = 0;	 Catch:{ Exception -> 0x0c84 }
        r47[r4] = r22;	 Catch:{ Exception -> 0x0c84 }
        r4 = 0;	 Catch:{ Exception -> 0x0c84 }
        r4 = r47[r4];	 Catch:{ Exception -> 0x0c84 }
        r5 = 1;	 Catch:{ Exception -> 0x0c84 }
        r0 = r85;	 Catch:{ Exception -> 0x0c84 }
        r0.switchToAccount(r4, r5);	 Catch:{ Exception -> 0x0c84 }
    L_0x0c56:
        r4 = "DATA4";	 Catch:{ Exception -> 0x0c84 }
        r0 = r33;	 Catch:{ Exception -> 0x0c84 }
        r4 = r0.getColumnIndex(r4);	 Catch:{ Exception -> 0x0c84 }
        r0 = r33;	 Catch:{ Exception -> 0x0c84 }
        r82 = r0.getInt(r4);	 Catch:{ Exception -> 0x0c84 }
        r4 = 0;	 Catch:{ Exception -> 0x0c84 }
        r4 = r47[r4];	 Catch:{ Exception -> 0x0c84 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x0c84 }
        r5 = org.telegram.messenger.NotificationCenter.closeChats;	 Catch:{ Exception -> 0x0c84 }
        r16 = 0;	 Catch:{ Exception -> 0x0c84 }
        r0 = r16;	 Catch:{ Exception -> 0x0c84 }
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0c84 }
        r16 = r0;	 Catch:{ Exception -> 0x0c84 }
        r0 = r16;	 Catch:{ Exception -> 0x0c84 }
        r4.postNotificationName(r5, r0);	 Catch:{ Exception -> 0x0c84 }
        r65 = java.lang.Integer.valueOf(r82);	 Catch:{ Exception -> 0x0c84 }
    L_0x0c7f:
        r33.close();	 Catch:{ Exception -> 0x0c84 }
        goto L_0x02c1;
    L_0x0c84:
        r40 = move-exception;
        org.telegram.messenger.FileLog.m3e(r40);
        goto L_0x02c1;
    L_0x0c8a:
        r22 = r22 + 1;
        goto L_0x0c39;
    L_0x0c8d:
        r4 = r86.getAction();
        r5 = "org.telegram.messenger.OPEN_ACCOUNT";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0ca1;
    L_0x0c9a:
        r4 = 1;
        r53 = java.lang.Integer.valueOf(r4);
        goto L_0x02c1;
    L_0x0ca1:
        r4 = r86.getAction();
        r5 = "new_dialog";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0cb5;
    L_0x0cae:
        r4 = 1;
        r52 = java.lang.Integer.valueOf(r4);
        goto L_0x02c1;
    L_0x0cb5:
        r4 = r86.getAction();
        r5 = "com.tmessages.openchat";
        r4 = r4.startsWith(r5);
        if (r4 == 0) goto L_0x0d3e;
    L_0x0cc2:
        r4 = "chatId";
        r5 = 0;
        r0 = r86;
        r30 = r0.getIntExtra(r4, r5);
        r4 = "userId";
        r5 = 0;
        r0 = r86;
        r82 = r0.getIntExtra(r4, r5);
        r4 = "encId";
        r5 = 0;
        r0 = r86;
        r41 = r0.getIntExtra(r4, r5);
        if (r30 == 0) goto L_0x0cfe;
    L_0x0ce2:
        r4 = 0;
        r4 = r47[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r16 = 0;
        r0 = r16;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r0 = r16;
        r4.postNotificationName(r5, r0);
        r62 = java.lang.Integer.valueOf(r30);
        goto L_0x02c1;
    L_0x0cfe:
        if (r82 == 0) goto L_0x0d1c;
    L_0x0d00:
        r4 = 0;
        r4 = r47[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r16 = 0;
        r0 = r16;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r0 = r16;
        r4.postNotificationName(r5, r0);
        r65 = java.lang.Integer.valueOf(r82);
        goto L_0x02c1;
    L_0x0d1c:
        if (r41 == 0) goto L_0x0d3a;
    L_0x0d1e:
        r4 = 0;
        r4 = r47[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r16 = 0;
        r0 = r16;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r0 = r16;
        r4.postNotificationName(r5, r0);
        r63 = java.lang.Integer.valueOf(r41);
        goto L_0x02c1;
    L_0x0d3a:
        r69 = 1;
        goto L_0x02c1;
    L_0x0d3e:
        r4 = r86.getAction();
        r5 = "com.tmessages.openplayer";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0d4f;
    L_0x0d4b:
        r71 = 1;
        goto L_0x02c1;
    L_0x0d4f:
        r4 = r86.getAction();
        r5 = "org.tmessages.openlocations";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x02c1;
    L_0x0d5c:
        r70 = 1;
        goto L_0x02c1;
    L_0x0d60:
        r4 = r62.intValue();
        if (r4 == 0) goto L_0x0dd4;
    L_0x0d66:
        r24 = new android.os.Bundle;
        r24.<init>();
        r4 = "chat_id";
        r5 = r62.intValue();
        r0 = r24;
        r0.putInt(r4, r5);
        r4 = r64.intValue();
        if (r4 == 0) goto L_0x0d89;
    L_0x0d7d:
        r4 = "message_id";
        r5 = r64.intValue();
        r0 = r24;
        r0.putInt(r4, r5);
    L_0x0d89:
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0db2;
    L_0x0d91:
        r4 = 0;
        r4 = r47[r4];
        r5 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = mainFragmentsStack;
        r16 = mainFragmentsStack;
        r16 = r16.size();
        r16 = r16 + -1;
        r0 = r16;
        r4 = r4.get(r0);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r24;
        r4 = r5.checkCanOpenChat(r0, r4);
        if (r4 == 0) goto L_0x0333;
    L_0x0db2:
        r44 = new org.telegram.ui.ChatActivity;
        r0 = r44;
        r1 = r24;
        r0.<init>(r1);
        r0 = r85;
        r4 = r0.actionBarLayout;
        r5 = 0;
        r16 = 1;
        r17 = 1;
        r0 = r44;
        r1 = r16;
        r2 = r17;
        r4 = r4.presentFragment(r0, r5, r1, r2);
        if (r4 == 0) goto L_0x0333;
    L_0x0dd0:
        r61 = 1;
        goto L_0x0333;
    L_0x0dd4:
        r4 = r63.intValue();
        if (r4 == 0) goto L_0x0e0d;
    L_0x0dda:
        r24 = new android.os.Bundle;
        r24.<init>();
        r4 = "enc_id";
        r5 = r63.intValue();
        r0 = r24;
        r0.putInt(r4, r5);
        r44 = new org.telegram.ui.ChatActivity;
        r0 = r44;
        r1 = r24;
        r0.<init>(r1);
        r0 = r85;
        r4 = r0.actionBarLayout;
        r5 = 0;
        r16 = 1;
        r17 = 1;
        r0 = r44;
        r1 = r16;
        r2 = r17;
        r4 = r4.presentFragment(r0, r5, r1, r2);
        if (r4 == 0) goto L_0x0333;
    L_0x0e09:
        r61 = 1;
        goto L_0x0333;
    L_0x0e0d:
        if (r69 == 0) goto L_0x0e63;
    L_0x0e0f:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 != 0) goto L_0x0e22;
    L_0x0e15:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4.removeAllFragments();
    L_0x0e1c:
        r61 = 0;
        r87 = 0;
        goto L_0x0333;
    L_0x0e22:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0e1c;
    L_0x0e2e:
        r22 = 0;
    L_0x0e30:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r4 = r4 + -1;
        if (r4 <= 0) goto L_0x0e5a;
    L_0x0e3e:
        r0 = r85;
        r5 = r0.layersActionBarLayout;
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r16 = 0;
        r0 = r16;
        r4 = r4.get(r0);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r5.removeFragmentFromStack(r4);
        r22 = r22 + -1;
        r22 = r22 + 1;
        goto L_0x0e30;
    L_0x0e5a:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r5 = 0;
        r4.closeLastFragment(r5);
        goto L_0x0e1c;
    L_0x0e63:
        if (r71 == 0) goto L_0x0e8e;
    L_0x0e65:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0e8a;
    L_0x0e71:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r5 = 0;
        r44 = r4.get(r5);
        r44 = (org.telegram.ui.ActionBar.BaseFragment) r44;
        r4 = new org.telegram.ui.Components.AudioPlayerAlert;
        r0 = r85;
        r4.<init>(r0);
        r0 = r44;
        r0.showDialog(r4);
    L_0x0e8a:
        r61 = 0;
        goto L_0x0333;
    L_0x0e8e:
        if (r70 == 0) goto L_0x0ec2;
    L_0x0e90:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0ebe;
    L_0x0e9c:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r5 = 0;
        r44 = r4.get(r5);
        r44 = (org.telegram.ui.ActionBar.BaseFragment) r44;
        r4 = new org.telegram.ui.Components.SharingLocationsAlert;
        r5 = new org.telegram.ui.LaunchActivity$8;
        r0 = r85;
        r1 = r47;
        r5.<init>(r1);
        r0 = r85;
        r4.<init>(r0, r5);
        r0 = r44;
        r0.showDialog(r4);
    L_0x0ebe:
        r61 = 0;
        goto L_0x0333;
    L_0x0ec2:
        r0 = r85;
        r4 = r0.videoPath;
        if (r4 != 0) goto L_0x0ee6;
    L_0x0ec8:
        r0 = r85;
        r4 = r0.photoPathsArray;
        if (r4 != 0) goto L_0x0ee6;
    L_0x0ece:
        r0 = r85;
        r4 = r0.sendingText;
        if (r4 != 0) goto L_0x0ee6;
    L_0x0ed4:
        r0 = r85;
        r4 = r0.documentsPathsArray;
        if (r4 != 0) goto L_0x0ee6;
    L_0x0eda:
        r0 = r85;
        r4 = r0.contactsToSend;
        if (r4 != 0) goto L_0x0ee6;
    L_0x0ee0:
        r0 = r85;
        r4 = r0.documentsUrisArray;
        if (r4 == 0) goto L_0x10a6;
    L_0x0ee6:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 != 0) goto L_0x0f02;
    L_0x0eec:
        r4 = 0;
        r4 = r47[r4];
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.closeChats;
        r16 = 0;
        r0 = r16;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r0 = r16;
        r4.postNotificationName(r5, r0);
    L_0x0f02:
        r4 = 0;
        r4 = (r36 > r4 ? 1 : (r36 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x1089;
    L_0x0f08:
        r24 = new android.os.Bundle;
        r24.<init>();
        r4 = "onlySelect";
        r5 = 1;
        r0 = r24;
        r0.putBoolean(r4, r5);
        r4 = "dialogsType";
        r5 = 3;
        r0 = r24;
        r0.putInt(r4, r5);
        r4 = "allowSwitchAccount";
        r5 = 1;
        r0 = r24;
        r0.putBoolean(r4, r5);
        r0 = r85;
        r4 = r0.contactsToSend;
        if (r4 == 0) goto L_0x0fe4;
    L_0x0f2e:
        r4 = "selectAlertString";
        r5 = "SendContactTo";
        r16 = NUM; // 0x7f0c05cd float:1.8612204E38 double:1.053098132E-314;
        r0 = r16;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r24;
        r0.putString(r4, r5);
        r4 = "selectAlertStringGroup";
        r5 = "SendContactToGroup";
        r16 = NUM; // 0x7f0c05c0 float:1.8612178E38 double:1.0530981257E-314;
        r0 = r16;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r24;
        r0.putString(r4, r5);
    L_0x0f56:
        r44 = new org.telegram.ui.DialogsActivity;
        r0 = r44;
        r1 = r24;
        r0.<init>(r1);
        r0 = r44;
        r1 = r85;
        r0.setDelegate(r1);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1011;
    L_0x0f6c:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x100e;
    L_0x0f78:
        r0 = r85;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r85;
        r5 = r0.layersActionBarLayout;
        r5 = r5.fragmentsStack;
        r5 = r5.size();
        r5 = r5 + -1;
        r4 = r4.get(r5);
        r4 = r4 instanceof org.telegram.ui.DialogsActivity;
        if (r4 == 0) goto L_0x100e;
    L_0x0f92:
        r66 = 1;
    L_0x0f94:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r5 = 1;
        r16 = 1;
        r0 = r44;
        r1 = r66;
        r2 = r16;
        r4.presentFragment(r0, r1, r5, r2);
        r61 = 1;
        r4 = org.telegram.ui.SecretMediaViewer.hasInstance();
        if (r4 == 0) goto L_0x103f;
    L_0x0fac:
        r4 = org.telegram.ui.SecretMediaViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x103f;
    L_0x0fb6:
        r4 = org.telegram.ui.SecretMediaViewer.getInstance();
        r5 = 0;
        r16 = 0;
        r0 = r16;
        r4.closePhoto(r5, r0);
    L_0x0fc2:
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x107b;
    L_0x0fd4:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r85;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        goto L_0x0333;
    L_0x0fe4:
        r4 = "selectAlertString";
        r5 = "SendMessagesTo";
        r16 = NUM; // 0x7f0c05cd float:1.8612204E38 double:1.053098132E-314;
        r0 = r16;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r24;
        r0.putString(r4, r5);
        r4 = "selectAlertStringGroup";
        r5 = "SendMessagesToGroup";
        r16 = NUM; // 0x7f0c05ce float:1.8612206E38 double:1.0530981326E-314;
        r0 = r16;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r0);
        r0 = r24;
        r0.putString(r4, r5);
        goto L_0x0f56;
    L_0x100e:
        r66 = 0;
        goto L_0x0f94;
    L_0x1011:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r5 = 1;
        if (r4 <= r5) goto L_0x103c;
    L_0x101e:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r85;
        r5 = r0.actionBarLayout;
        r5 = r5.fragmentsStack;
        r5 = r5.size();
        r5 = r5 + -1;
        r4 = r4.get(r5);
        r4 = r4 instanceof org.telegram.ui.DialogsActivity;
        if (r4 == 0) goto L_0x103c;
    L_0x1038:
        r66 = 1;
    L_0x103a:
        goto L_0x0f94;
    L_0x103c:
        r66 = 0;
        goto L_0x103a;
    L_0x103f:
        r4 = org.telegram.ui.PhotoViewer.hasInstance();
        if (r4 == 0) goto L_0x105d;
    L_0x1045:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x105d;
    L_0x104f:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r5 = 0;
        r16 = 1;
        r0 = r16;
        r4.closePhoto(r5, r0);
        goto L_0x0fc2;
    L_0x105d:
        r4 = org.telegram.ui.ArticleViewer.hasInstance();
        if (r4 == 0) goto L_0x0fc2;
    L_0x1063:
        r4 = org.telegram.ui.ArticleViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x0fc2;
    L_0x106d:
        r4 = org.telegram.ui.ArticleViewer.getInstance();
        r5 = 0;
        r16 = 1;
        r0 = r16;
        r4.close(r5, r0);
        goto L_0x0fc2;
    L_0x107b:
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x0333;
    L_0x1089:
        r39 = new java.util.ArrayList;
        r39.<init>();
        r4 = java.lang.Long.valueOf(r36);
        r0 = r39;
        r0.add(r4);
        r4 = 0;
        r5 = 0;
        r16 = 0;
        r0 = r85;
        r1 = r39;
        r2 = r16;
        r0.didSelectDialogs(r4, r1, r5, r2);
        goto L_0x0333;
    L_0x10a6:
        r4 = r53.intValue();
        if (r4 == 0) goto L_0x10f5;
    L_0x10ac:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r5 = new org.telegram.ui.SettingsActivity;
        r5.<init>();
        r16 = 0;
        r17 = 1;
        r18 = 1;
        r0 = r16;
        r1 = r17;
        r2 = r18;
        r4.presentFragment(r5, r0, r1, r2);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x10e8;
    L_0x10ca:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r85;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
    L_0x10e4:
        r61 = 1;
        goto L_0x0333;
    L_0x10e8:
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x10e4;
    L_0x10f5:
        r4 = r52.intValue();
        if (r4 == 0) goto L_0x0333;
    L_0x10fb:
        r24 = new android.os.Bundle;
        r24.<init>();
        r4 = "destroyAfterSelect";
        r5 = 1;
        r0 = r24;
        r0.putBoolean(r4, r5);
        r0 = r85;
        r4 = r0.actionBarLayout;
        r5 = new org.telegram.ui.ContactsActivity;
        r0 = r24;
        r5.<init>(r0);
        r16 = 0;
        r17 = 1;
        r18 = 1;
        r0 = r16;
        r1 = r17;
        r2 = r18;
        r4.presentFragment(r5, r0, r1, r2);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1147;
    L_0x1129:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r85;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
    L_0x1143:
        r61 = 1;
        goto L_0x0333;
    L_0x1147:
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x1143;
    L_0x1154:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x036f;
    L_0x1160:
        r38 = new org.telegram.ui.DialogsActivity;
        r4 = 0;
        r0 = r38;
        r0.<init>(r4);
        r0 = r85;
        r4 = r0.sideMenu;
        r0 = r38;
        r0.setSideMenu(r4);
        r0 = r85;
        r4 = r0.actionBarLayout;
        r0 = r38;
        r4.addFragmentToStack(r0);
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x036f;
    L_0x1188:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x036f;
    L_0x1194:
        r0 = r85;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.isClientActivated();
        if (r4 != 0) goto L_0x11bc;
    L_0x11a2:
        r0 = r85;
        r4 = r0.actionBarLayout;
        r5 = new org.telegram.ui.LoginActivity;
        r5.<init>();
        r4.addFragmentToStack(r5);
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 0;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x036f;
    L_0x11bc:
        r38 = new org.telegram.ui.DialogsActivity;
        r4 = 0;
        r0 = r38;
        r0.<init>(r4);
        r0 = r85;
        r4 = r0.sideMenu;
        r0 = r38;
        r0.setSideMenu(r4);
        r0 = r85;
        r4 = r0.actionBarLayout;
        r0 = r38;
        r4.addFragmentToStack(r0);
        r0 = r85;
        r4 = r0.drawerLayoutContainer;
        r5 = 1;
        r16 = 0;
        r0 = r16;
        r4.setAllowOpenDrawer(r5, r0);
        goto L_0x036f;
    L_0x11e4:
        r4 = move-exception;
        goto L_0x07de;
    L_0x11e7:
        r4 = move-exception;
        goto L_0x0bd5;
    L_0x11ea:
        r4 = move-exception;
        goto L_0x0bd5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    private void runLinkRequest(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, String[] instantView, int state) {
        final AlertDialog progressDialog = new AlertDialog(this, 1);
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        int requestId = 0;
        TLObject req;
        final String str;
        final String str2;
        if (username != null) {
            req = new TL_contacts_resolveUsername();
            req.username = username;
            final String str3 = game;
            final int i = intentAccount;
            str = botChat;
            str2 = botUser;
            final Integer num = messageId;
            requestId = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (!LaunchActivity.this.isFinishing()) {
                                try {
                                    progressDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                final TL_contacts_resolvedPeer res = response;
                                if (error != null || LaunchActivity.this.actionBarLayout == null || (str3 != null && (str3 == null || res.users.isEmpty()))) {
                                    try {
                                        Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound), 0).show();
                                        return;
                                    } catch (Throwable e2) {
                                        FileLog.m3e(e2);
                                        return;
                                    }
                                }
                                MessagesController.getInstance(i).putUsers(res.users, false);
                                MessagesController.getInstance(i).putChats(res.chats, false);
                                MessagesStorage.getInstance(i).putUsersAndChats(res.users, res.chats, false, true);
                                Bundle args;
                                DialogsActivity fragment;
                                if (str3 != null) {
                                    args = new Bundle();
                                    args.putBoolean("onlySelect", true);
                                    args.putBoolean("cantSendToChannels", true);
                                    args.putInt("dialogsType", 1);
                                    args.putString("selectAlertString", LocaleController.getString("SendGameTo", R.string.SendGameTo));
                                    args.putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", R.string.SendGameToGroup));
                                    fragment = new DialogsActivity(args);
                                    fragment.setDelegate(new DialogsActivityDelegate() {
                                        public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                            long did = ((Long) dids.get(0)).longValue();
                                            TL_inputMediaGame inputMediaGame = new TL_inputMediaGame();
                                            inputMediaGame.id = new TL_inputGameShortName();
                                            inputMediaGame.id.short_name = str3;
                                            inputMediaGame.id.bot_id = MessagesController.getInstance(i).getInputUser((User) res.users.get(0));
                                            SendMessagesHelper.getInstance(i).sendGame(MessagesController.getInstance(i).getInputPeer((int) did), inputMediaGame, 0, 0);
                                            Bundle args = new Bundle();
                                            args.putBoolean("scrollToTopOnResume", true);
                                            int lower_part = (int) did;
                                            int high_id = (int) (did >> 32);
                                            if (lower_part == 0) {
                                                args.putInt("enc_id", high_id);
                                            } else if (high_id == 1) {
                                                args.putInt("chat_id", lower_part);
                                            } else if (lower_part > 0) {
                                                args.putInt("user_id", lower_part);
                                            } else if (lower_part < 0) {
                                                args.putInt("chat_id", -lower_part);
                                            }
                                            if (MessagesController.getInstance(i).checkCanOpenChat(args, fragment)) {
                                                NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(args), true, false, true);
                                            }
                                        }
                                    });
                                    boolean removeLast = AndroidUtilities.isTablet() ? LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() > 0 && (LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity) : LaunchActivity.this.actionBarLayout.fragmentsStack.size() > 1 && (LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
                                    LaunchActivity.this.actionBarLayout.presentFragment(fragment, removeLast, true, true);
                                    if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                                        SecretMediaViewer.getInstance().closePhoto(false, false);
                                    } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                                        PhotoViewer.getInstance().closePhoto(false, true);
                                    } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                                        ArticleViewer.getInstance().close(false, true);
                                    }
                                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    if (AndroidUtilities.isTablet()) {
                                        LaunchActivity.this.actionBarLayout.showLastFragment();
                                        LaunchActivity.this.rightActionBarLayout.showLastFragment();
                                        return;
                                    }
                                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                } else if (str != null) {
                                    final User user = !res.users.isEmpty() ? (User) res.users.get(0) : null;
                                    if (user == null || (user.bot && user.bot_nochats)) {
                                        try {
                                            Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups), 0).show();
                                            return;
                                        } catch (Throwable e22) {
                                            FileLog.m3e(e22);
                                            return;
                                        }
                                    }
                                    args = new Bundle();
                                    args.putBoolean("onlySelect", true);
                                    args.putInt("dialogsType", 2);
                                    args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                                    fragment = new DialogsActivity(args);
                                    fragment.setDelegate(new DialogsActivityDelegate() {
                                        public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                            long did = ((Long) dids.get(0)).longValue();
                                            Bundle args = new Bundle();
                                            args.putBoolean("scrollToTopOnResume", true);
                                            args.putInt("chat_id", -((int) did));
                                            if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.getInstance(i).checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                                NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                MessagesController.getInstance(i).addUserToChat(-((int) did), user, null, 0, str, null);
                                                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(args), true, false, true);
                                            }
                                        }
                                    });
                                    LaunchActivity.this.presentFragment(fragment);
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
                                    if (str2 != null && res.users.size() > 0 && ((User) res.users.get(0)).bot) {
                                        args.putString("botUser", str2);
                                        isBot = true;
                                    }
                                    if (num != null) {
                                        args.putInt("message_id", num.intValue());
                                    }
                                    BaseFragment lastFragment = !LaunchActivity.mainFragmentsStack.isEmpty() ? (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1) : null;
                                    if (lastFragment != null && !MessagesController.getInstance(i).checkCanOpenChat(args, lastFragment)) {
                                        return;
                                    }
                                    if (isBot && lastFragment != null && (lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).getDialogId() == dialog_id) {
                                        ((ChatActivity) lastFragment).setBotUser(str2);
                                        return;
                                    }
                                    ChatActivity fragment2 = new ChatActivity(args);
                                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    LaunchActivity.this.actionBarLayout.presentFragment(fragment2, false, true, true);
                                }
                            }
                        }
                    });
                }
            });
        } else if (group != null) {
            if (state == 0) {
                req = new TL_messages_checkChatInvite();
                req.hash = group;
                final int i2 = intentAccount;
                final String str4 = group;
                str = username;
                str2 = sticker;
                final String str5 = botUser;
                final String str6 = botChat;
                final String str7 = message;
                final boolean z = hasUrl;
                final Integer num2 = messageId;
                final String str8 = game;
                final String[] strArr = instantView;
                requestId = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.LaunchActivity$10$1$1 */
                            class C14441 implements DialogInterface.OnClickListener {
                                C14441() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LaunchActivity.this.runLinkRequest(i2, str, str4, str2, str5, str6, str7, z, num2, str8, strArr, 1);
                                }
                            }

                            public void run() {
                                if (!LaunchActivity.this.isFinishing()) {
                                    try {
                                        progressDialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    Builder builder;
                                    if (error != null || LaunchActivity.this.actionBarLayout == null) {
                                        builder = new Builder(LaunchActivity.this);
                                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        if (error.text.startsWith("FLOOD_WAIT")) {
                                            builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
                                        } else {
                                            builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                        LaunchActivity.this.showAlertDialog(builder);
                                        return;
                                    }
                                    ChatInvite invite = response;
                                    if (invite.chat != null && !ChatObject.isLeftFromChat(invite.chat)) {
                                        MessagesController.getInstance(i2).putChat(invite.chat, false);
                                        ArrayList<Chat> chats = new ArrayList();
                                        chats.add(invite.chat);
                                        MessagesStorage.getInstance(i2).putUsersAndChats(null, chats, false, true);
                                        Bundle args = new Bundle();
                                        args.putInt("chat_id", invite.chat.id);
                                        if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.getInstance(i2).checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                            ChatActivity fragment = new ChatActivity(args);
                                            NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                            LaunchActivity.this.actionBarLayout.presentFragment(fragment, false, true, true);
                                        }
                                    } else if (((invite.chat != null || (invite.channel && !invite.megagroup)) && (invite.chat == null || (ChatObject.isChannel(invite.chat) && !invite.chat.megagroup))) || LaunchActivity.mainFragmentsStack.isEmpty()) {
                                        builder = new Builder(LaunchActivity.this);
                                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        String str = "ChannelJoinTo";
                                        Object[] objArr = new Object[1];
                                        objArr[0] = invite.chat != null ? invite.chat.title : invite.title;
                                        builder.setMessage(LocaleController.formatString(str, R.string.ChannelJoinTo, objArr));
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C14441());
                                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                        LaunchActivity.this.showAlertDialog(builder);
                                    } else {
                                        BaseFragment fragment2 = (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);
                                        fragment2.showDialog(new JoinGroupAlert(LaunchActivity.this, invite, str4, fragment2));
                                    }
                                }
                            }
                        });
                    }
                }, 2);
            } else if (state == 1) {
                req = new TL_messages_importChatInvite();
                req.hash = group;
                final int i3 = intentAccount;
                ConnectionsManager.getInstance(intentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        if (error == null) {
                            MessagesController.getInstance(i3).processUpdates((Updates) response, false);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (!LaunchActivity.this.isFinishing()) {
                                    try {
                                        progressDialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    if (error != null) {
                                        Builder builder = new Builder(LaunchActivity.this);
                                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        if (error.text.startsWith("FLOOD_WAIT")) {
                                            builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
                                        } else if (error.text.equals("USERS_TOO_MUCH")) {
                                            builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
                                        } else {
                                            builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                        LaunchActivity.this.showAlertDialog(builder);
                                    } else if (LaunchActivity.this.actionBarLayout != null) {
                                        Updates updates = response;
                                        if (!updates.chats.isEmpty()) {
                                            Chat chat = (Chat) updates.chats.get(0);
                                            chat.left = false;
                                            chat.kicked = false;
                                            MessagesController.getInstance(i3).putUsers(updates.users, false);
                                            MessagesController.getInstance(i3).putChats(updates.chats, false);
                                            Bundle args = new Bundle();
                                            args.putInt("chat_id", chat.id);
                                            if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.getInstance(i3).checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                                ChatActivity fragment = new ChatActivity(args);
                                                NotificationCenter.getInstance(i3).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                LaunchActivity.this.actionBarLayout.presentFragment(fragment, false, true, true);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }, 2);
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
            final boolean z2 = hasUrl;
            final int i4 = intentAccount;
            final String str9 = message;
            fragment2.setDelegate(new DialogsActivityDelegate() {
                public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence m, boolean param) {
                    long did = ((Long) dids.get(0)).longValue();
                    Bundle args = new Bundle();
                    args.putBoolean("scrollToTopOnResume", true);
                    args.putBoolean("hasUrl", z2);
                    int lower_part = (int) did;
                    int high_id = (int) (did >> 32);
                    if (lower_part == 0) {
                        args.putInt("enc_id", high_id);
                    } else if (high_id == 1) {
                        args.putInt("chat_id", lower_part);
                    } else if (lower_part > 0) {
                        args.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        args.putInt("chat_id", -lower_part);
                    }
                    if (MessagesController.getInstance(i4).checkCanOpenChat(args, fragment)) {
                        NotificationCenter.getInstance(i4).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        DataQuery.getInstance(i4).saveDraft(did, str9, null, null, false);
                        LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(args), true, false, true);
                    }
                }
            });
            presentFragment(fragment2, false, true);
        } else if (instantView != null) {
        }
        if (requestId != 0) {
            i3 = intentAccount;
            i4 = requestId;
            progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ConnectionsManager.getInstance(i3).cancelRequest(i4, true);
                    try {
                        dialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    public AlertDialog showAlertDialog(Builder builder) {
        AlertDialog alertDialog = null;
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
                public void onDismiss(DialogInterface dialog) {
                    if (LaunchActivity.this.visibleDialog != null && LaunchActivity.this.visibleDialog == LaunchActivity.this.localeDialog) {
                        try {
                            Toast.makeText(LaunchActivity.this, LaunchActivity.this.getStringForLanguageAlert(LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals("en") ? LaunchActivity.this.englishLocaleStrings : LaunchActivity.this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater), 1).show();
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
            boolean z;
            boolean z2;
            BaseFragment chatActivity = new ChatActivity(args);
            ActionBarLayout actionBarLayout = this.actionBarLayout;
            if (dialogsFragment != null) {
                z = true;
            } else {
                z = false;
            }
            if (dialogsFragment == null) {
                z2 = true;
            } else {
                z2 = false;
            }
            actionBarLayout.presentFragment(chatActivity, z, z2, true);
            if (this.videoPath != null) {
                chatActivity.openVideoEditor(this.videoPath, this.sendingText);
                this.sendingText = null;
            }
            if (this.photoPathsArray != null) {
                if (this.sendingText != null && this.sendingText.length() <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.photoPathsArray.size() == 1) {
                    ((SendingMediaInfo) this.photoPathsArray.get(0)).caption = this.sendingText;
                    this.sendingText = null;
                }
                SendMessagesHelper.prepareSendingMedia(this.photoPathsArray, did, null, null, false, false);
            }
            if (this.sendingText != null) {
                SendMessagesHelper.prepareSendingText(this.sendingText, did);
            }
            if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, did, null, null);
            }
            if (!(this.contactsToSend == null || this.contactsToSend.isEmpty())) {
                Iterator it = this.contactsToSend.iterator();
                while (it.hasNext()) {
                    SendMessagesHelper.getInstance(account).sendMessage((User) it.next(), did, null, null, null);
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

    public void presentFragment(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true);
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
        if (requestCode == 3 || requestCode == 4 || requestCode == 5 || requestCode == 19 || requestCode == 20) {
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
                        CameraController.getInstance().initCamera();
                        return;
                    }
                    return;
                } else if (requestCode == 19 || requestCode == 20) {
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
                } else if (requestCode == 19 || requestCode == 20) {
                    builder.setMessage(LocaleController.getString("PermissionNoCamera", R.string.PermissionNoCamera));
                }
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    @TargetApi(9)
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            LaunchActivity.this.startActivity(intent);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
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
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                MediaController.getInstance().seekToProgress(messageObject, messageObject.audioProgress);
            }
        }
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
        if (id == NotificationCenter.appDidLogout) {
            switchToAvailableAccountOrLogout();
        } else if (id == NotificationCenter.closeOtherAppActivities) {
            if (args[0] != this) {
                onFinish();
                finish();
            }
        } else if (id == NotificationCenter.didUpdatedConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("switch to state " + state);
                }
                this.currentConnectionState = state;
                updateCurrentConnectionState(account);
            }
        } else if (id == NotificationCenter.mainUserInfoChanged) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (id == NotificationCenter.needShowAlert) {
            Integer reason = args[0];
            builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (reason.intValue() != 2) {
                final int i = account;
                builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!LaunchActivity.mainFragmentsStack.isEmpty()) {
                            MessagesController.getInstance(i).openByUserName("spambot", (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), 1);
                        }
                    }
                });
            }
            if (reason.intValue() == 0) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam1", R.string.NobodyLikesSpam1));
            } else if (reason.intValue() == 1) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam2", R.string.NobodyLikesSpam2));
            } else if (reason.intValue() == 2) {
                builder.setMessage((String) args[1]);
            }
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder.create());
            }
        } else if (id == NotificationCenter.wasUnableToFindCurrentLocation) {
            HashMap<String, MessageObject> waitingForLocation = args[0];
            builder = new Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            final HashMap<String, MessageObject> hashMap = waitingForLocation;
            final int i2 = account;
            builder.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", R.string.ShareYouLocationUnableManually), new DialogInterface.OnClickListener() {

                /* renamed from: org.telegram.ui.LaunchActivity$19$1 */
                class C21601 implements LocationActivityDelegate {
                    C21601() {
                    }

                    public void didSelectLocation(MessageMedia location, int live) {
                        for (Entry<String, MessageObject> entry : hashMap.entrySet()) {
                            MessageObject messageObject = (MessageObject) entry.getValue();
                            SendMessagesHelper.getInstance(i2).sendMessage(location, messageObject.getDialogId(), messageObject, null, null);
                        }
                    }
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!LaunchActivity.mainFragmentsStack.isEmpty() && AndroidUtilities.isGoogleMapsInstalled((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                        LocationActivity fragment = new LocationActivity(0);
                        fragment.setDelegate(new C21601());
                        LaunchActivity.this.presentFragment(fragment);
                    }
                }
            });
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
            if (SharedConfig.passcodeHash.length() <= 0 || SharedConfig.allowScreenCapture) {
                try {
                    getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
                    return;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return;
                }
            }
            try {
                getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        } else if (id == NotificationCenter.reloadInterface) {
            rebuildAllFragments(false);
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
                final HashMap<String, Contact> contactHashMap = args[1];
                final boolean first = ((Boolean) args[2]).booleanValue();
                final boolean schedule = ((Boolean) args[3]).booleanValue();
                BaseFragment fragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                builder = new Builder((Context) this);
                builder.setTitle(LocaleController.getString("UpdateContactsTitle", R.string.UpdateContactsTitle));
                builder.setMessage(LocaleController.getString("UpdateContactsMessage", R.string.UpdateContactsMessage));
                final int i3 = account;
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContactsController.getInstance(i3).syncPhoneBookByAlert(contactHashMap, first, schedule, false);
                    }
                });
                i3 = account;
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContactsController.getInstance(i3).syncPhoneBookByAlert(contactHashMap, first, schedule, true);
                    }
                });
                i3 = account;
                builder.setOnBackButtonListener(new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContactsController.getInstance(i3).syncPhoneBookByAlert(contactHashMap, first, schedule, true);
                    }
                });
                AlertDialog dialog = builder.create();
                fragment.showDialog(dialog);
                dialog.setCanceledOnTouchOutside(false);
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            if (!args[0].booleanValue()) {
                if (this.sideMenu != null) {
                    this.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
                    this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
                    this.sideMenu.getAdapter().notifyDataSetChanged();
                }
                if (VERSION.SDK_INT >= 21) {
                    try {
                        setTaskDescription(new TaskDescription(null, null, Theme.getColor(Theme.key_actionBarDefault) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
                    } catch (Exception e3) {
                    }
                }
            }
        } else if (id == NotificationCenter.needSetDayNightTheme) {
            ThemeInfo theme = args[0];
            this.actionBarLayout.animateThemedValues(theme);
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.animateThemedValues(theme);
                this.rightActionBarLayout.animateThemedValues(theme);
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

    private String getStringForLanguageAlert(HashMap<String, String> map, String key, int intKey) {
        String value = (String) map.get(key);
        if (value == null) {
            return LocaleController.getString(key, intKey);
        }
        return value;
    }

    private void checkFreeDiscSpace() {
        if (VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.LaunchActivity$23$1 */
                class C14481 implements Runnable {
                    C14481() {
                    }

                    public void run() {
                        try {
                            AlertsCreator.createFreeSpaceDialog(LaunchActivity.this).show();
                        } catch (Throwable th) {
                        }
                    }
                }

                public void run() {
                    if (UserConfig.getInstance(LaunchActivity.this.currentAccount).isClientActivated()) {
                        try {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            if (Math.abs(preferences.getLong("last_space_check", 0) - System.currentTimeMillis()) >= 259200000) {
                                File path = FileLoader.getDirectory(4);
                                if (path != null) {
                                    long freeSpace;
                                    StatFs statFs = new StatFs(path.getAbsolutePath());
                                    if (VERSION.SDK_INT < 18) {
                                        freeSpace = (long) Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize());
                                    } else {
                                        freeSpace = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                                    }
                                    preferences.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                                    if (freeSpace < 104857600) {
                                        AndroidUtilities.runOnUIThread(new C14481());
                                    }
                                }
                            }
                        } catch (Throwable th) {
                        }
                    }
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
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
            final LanguageCell[] cells = new LanguageCell[2];
            final LocaleInfo[] selectedLanguage = new LocaleInfo[1];
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
                cells[a].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                cells[a].setLanguageSelected(a == 0);
                linearLayout.addView(cells[a], LayoutHelper.createLinear(-1, 48));
                cells[a].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Integer tag = (Integer) v.getTag();
                        selectedLanguage[0] = ((LanguageCell) v).getCurrentLocale();
                        for (int a = 0; a < cells.length; a++) {
                            boolean z;
                            LanguageCell languageCell = cells[a];
                            if (a == tag.intValue()) {
                                z = true;
                            } else {
                                z = false;
                            }
                            languageCell.setLanguageSelected(z);
                        }
                    }
                });
                a++;
            }
            LanguageCell cell = new LanguageCell(this, true);
            cell.setValue(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther), getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther));
            cell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LaunchActivity.this.localeDialog = null;
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(true);
                    LaunchActivity.this.presentFragment(new LanguageSelectActivity());
                    if (LaunchActivity.this.visibleDialog != null) {
                        LaunchActivity.this.visibleDialog.dismiss();
                        LaunchActivity.this.visibleDialog = null;
                    }
                }
            });
            linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
            builder.setView(linearLayout);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LocaleController.getInstance().applyLanguage(selectedLanguage[0], true, false, LaunchActivity.this.currentAccount);
                    LaunchActivity.this.rebuildAllFragments(true);
                }
            });
            this.localeDialog = showAlertDialog(builder);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", systemLang).commit();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void showLanguageAlert(boolean force) {
        try {
            if (!this.loadingLocaleDialog) {
                String showedLang = MessagesController.getGlobalMainSettings().getString("language_showed2", TtmlNode.ANONYMOUS_REGION_ID);
                final String systemLang = LocaleController.getSystemLocaleStringIso639().toLowerCase();
                if (force || !showedLang.equals(systemLang)) {
                    String arg;
                    final LocaleInfo[] infos = new LocaleInfo[2];
                    if (systemLang.contains("-")) {
                        arg = systemLang.split("-")[0];
                    } else {
                        arg = systemLang;
                    }
                    String alias;
                    if ("in".equals(arg)) {
                        alias = TtmlNode.ATTR_ID;
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
                        if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || (alias != null && info.shortName.equals(alias))) {
                            infos[1] = info;
                        }
                        if (infos[0] != null && infos[1] != null) {
                            break;
                        }
                    }
                    if (infos[0] != null && infos[1] != null && infos[0] != infos[1]) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("show lang alert for " + infos[0].getKey() + " and " + infos[1].getKey());
                        }
                        this.systemLocaleStrings = null;
                        this.englishLocaleStrings = null;
                        this.loadingLocaleDialog = true;
                        TL_langpack_getStrings req = new TL_langpack_getStrings();
                        req.lang_code = infos[1].shortName.replace("_", "-");
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                final HashMap<String, String> keys = new HashMap();
                                if (response != null) {
                                    Vector vector = (Vector) response;
                                    for (int a = 0; a < vector.objects.size(); a++) {
                                        LangPackString string = (LangPackString) vector.objects.get(a);
                                        keys.put(string.key, string.value);
                                    }
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        LaunchActivity.this.systemLocaleStrings = keys;
                                        if (LaunchActivity.this.englishLocaleStrings != null && LaunchActivity.this.systemLocaleStrings != null) {
                                            LaunchActivity.this.showLanguageAlertInternal(infos[1], infos[0], systemLang);
                                        }
                                    }
                                });
                            }
                        }, 8);
                        req = new TL_langpack_getStrings();
                        req.lang_code = infos[0].shortName.replace("_", "-");
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                final HashMap<String, String> keys = new HashMap();
                                if (response != null) {
                                    Vector vector = (Vector) response;
                                    for (int a = 0; a < vector.objects.size(); a++) {
                                        LangPackString string = (LangPackString) vector.objects.get(a);
                                        keys.put(string.key, string.value);
                                    }
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        LaunchActivity.this.englishLocaleStrings = keys;
                                        if (LaunchActivity.this.englishLocaleStrings != null && LaunchActivity.this.systemLocaleStrings != null) {
                                            LaunchActivity.this.showLanguageAlertInternal(infos[1], infos[0], systemLang);
                                        }
                                    }
                                });
                            }
                        }, 8);
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("alert already showed for " + showedLang);
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

    private void updateCurrentConnectionState(int account) {
        if (this.actionBarLayout != null) {
            String title = null;
            String subtitle = null;
            Runnable action = null;
            if (this.currentConnectionState == 2) {
                title = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
            } else if (this.currentConnectionState == 1) {
                title = LocaleController.getString("Connecting", R.string.Connecting);
                action = new Runnable() {
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
                title = LocaleController.getString("Updating", R.string.Updating);
            } else if (this.currentConnectionState == 4) {
                title = LocaleController.getString("ConnectingToProxy", R.string.ConnectingToProxy);
                subtitle = LocaleController.getString("ConnectingToProxyTapToDisable", R.string.ConnectingToProxyTapToDisable);
                action = new Runnable() {

                    /* renamed from: org.telegram.ui.LaunchActivity$31$1 */
                    class C14521 implements DialogInterface.OnClickListener {
                        C14521() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            Editor editor = MessagesController.getGlobalMainSettings().edit();
                            editor.putBoolean("proxy_enabled", false);
                            editor.commit();
                            for (int a = 0; a < 3; a++) {
                                ConnectionsManager.native_setProxySettings(a, TtmlNode.ANONYMOUS_REGION_ID, 0, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID);
                            }
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                        }
                    }

                    public void run() {
                        if (LaunchActivity.this.actionBarLayout != null && !LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty()) {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            BaseFragment fragment = (BaseFragment) LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1);
                            Builder builder = new Builder(LaunchActivity.this);
                            builder.setTitle(LocaleController.getString("Proxy", R.string.Proxy));
                            builder.setMessage(LocaleController.formatString("ConnectingToProxyDisableAlert", R.string.ConnectingToProxyDisableAlert, preferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID)));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyDisable", R.string.ConnectingToProxyDisable), new C14521());
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            fragment.showDialog(builder.create());
                        }
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(title, subtitle, action);
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
                } else if (lastFragment instanceof WallpapersActivity) {
                    outState.putString("fragment", "wallpapers");
                } else if ((lastFragment instanceof ProfileActivity) && ((ProfileActivity) lastFragment).isChat() && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat_profile");
                } else if ((lastFragment instanceof ChannelCreateActivity) && args != null && args.getInt("step") == 0) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "channel");
                } else if ((lastFragment instanceof ChannelEditActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "edit");
                }
                lastFragment.saveSelfArgs(outState);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            FileLog.m3e(e);
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
        boolean z = true;
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z2 = ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z2, true);
            if ((fragment instanceof DialogsActivity) && ((DialogsActivity) fragment).isMainDialogList() && layout != this.actionBarLayout) {
                this.actionBarLayout.removeAllFragments();
                this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false);
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
            } else if (fragment instanceof ChatActivity) {
                int a;
                ActionBarLayout actionBarLayout;
                if ((!this.tabletFullSize && layout == this.rightActionBarLayout) || (this.tabletFullSize && layout == this.actionBarLayout)) {
                    boolean result;
                    if (this.tabletFullSize && layout == this.actionBarLayout && this.actionBarLayout.fragmentsStack.size() == 1) {
                        result = false;
                    } else {
                        result = true;
                    }
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        actionBarLayout = this.layersActionBarLayout;
                        if (forceWithoutAnimation) {
                            z = false;
                        }
                        actionBarLayout.closeLastFragment(z);
                    }
                    if (!result) {
                        this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false);
                    }
                    return result;
                } else if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(fragment, removeLast, true, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    actionBarLayout = this.layersActionBarLayout;
                    if (forceWithoutAnimation) {
                        z = false;
                    }
                    actionBarLayout.closeLastFragment(z);
                    return false;
                } else if (!this.tabletFullSize || layout == this.actionBarLayout) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        r7 = this.layersActionBarLayout;
                        if (forceWithoutAnimation) {
                            z2 = false;
                        } else {
                            z2 = true;
                        }
                        r7.closeLastFragment(z2);
                    }
                    actionBarLayout = this.actionBarLayout;
                    if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                        z = false;
                    }
                    actionBarLayout.presentFragment(fragment, z, forceWithoutAnimation, false);
                    return false;
                } else {
                    r7 = this.actionBarLayout;
                    if (this.actionBarLayout.fragmentsStack.size() > 1) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    r7.presentFragment(fragment, z2, forceWithoutAnimation, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    actionBarLayout = this.layersActionBarLayout;
                    if (forceWithoutAnimation) {
                        z = false;
                    }
                    actionBarLayout.closeLastFragment(z);
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
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false);
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
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
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
