package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StatFs;
import android.text.TextUtils;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.PhoneFormat.PhoneFormat;
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
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.InputStickerSet;
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
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
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
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.SharingLocationsAlert;
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
    class C14572 implements OnTouchListener {
        C14572() {
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
            if (!LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation()) {
                if (x <= ((float) viewX) || x >= ((float) (LaunchActivity.this.layersActionBarLayout.getWidth() + viewX)) || y <= ((float) viewY) || y >= ((float) (LaunchActivity.this.layersActionBarLayout.getHeight() + viewY))) {
                    if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (int a = 0; a < LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
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
        C14593() {
        }

        public void onClick(View v) {
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

        public void onItemClick(View view, int position) {
            if (position == 0) {
                LaunchActivity.this.drawerLayoutAdapter.setAccountsShowed(LaunchActivity.this.drawerLayoutAdapter.isAccountsShowed() ^ true, true);
            } else if (view instanceof DrawerUserCell) {
                LaunchActivity.this.switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (view instanceof DrawerAddCell) {
                int freeAccount = -1;
                for (a = 0; a < 3; a++) {
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
                a = LaunchActivity.this.drawerLayoutAdapter.getId(position);
                if (a == 2) {
                    LaunchActivity.this.presentFragment(new GroupCreateActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (a == 3) {
                    Bundle args = new Bundle();
                    args.putBoolean("onlyUsers", true);
                    args.putBoolean("destroyAfterSelect", true);
                    args.putBoolean("createSecretChat", true);
                    args.putBoolean("allowBots", false);
                    LaunchActivity.this.presentFragment(new ContactsActivity(args));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (a == 4) {
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
                } else if (a == 6) {
                    LaunchActivity.this.presentFragment(new ContactsActivity(null));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (a == 7) {
                    LaunchActivity.this.presentFragment(new InviteContactsActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (a == 8) {
                    LaunchActivity.this.presentFragment(new SettingsActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (a == 9) {
                    Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (a == 10) {
                    LaunchActivity.this.presentFragment(new CallLogActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                } else if (a == 11) {
                    args = new Bundle();
                    args.putInt("user_id", UserConfig.getInstance(LaunchActivity.this.currentAccount).getClientUserId());
                    LaunchActivity.this.presentFragment(new ChatActivity(args));
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

    protected void onCreate(Bundle savedInstanceState) {
        String fragmentName;
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
        int i = -1;
        setContentView(this.drawerLayoutContainer, new LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            RelativeLayout launchLayout = new RelativeLayout(this) {
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
                    int leftWidth;
                    int width = r - l;
                    int height = b - t;
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                    } else {
                        leftWidth = (width / 100) * 35;
                        if (leftWidth < AndroidUtilities.dp(320.0f)) {
                            leftWidth = AndroidUtilities.dp(320.0f);
                        }
                        LaunchActivity.this.shadowTabletSide.layout(leftWidth, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + leftWidth, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                        LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                        LaunchActivity.this.rightActionBarLayout.layout(leftWidth, 0, LaunchActivity.this.rightActionBarLayout.getMeasuredWidth() + leftWidth, LaunchActivity.this.rightActionBarLayout.getMeasuredHeight());
                    }
                    leftWidth = (width - LaunchActivity.this.layersActionBarLayout.getMeasuredWidth()) / 2;
                    int y = (height - LaunchActivity.this.layersActionBarLayout.getMeasuredHeight()) / 2;
                    LaunchActivity.this.layersActionBarLayout.layout(leftWidth, y, LaunchActivity.this.layersActionBarLayout.getMeasuredWidth() + leftWidth, LaunchActivity.this.layersActionBarLayout.getMeasuredHeight() + y);
                    LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
                    LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
                }
            };
            this.drawerLayoutContainer.addView(launchLayout, LayoutHelper.createFrame(-1, -1.0f));
            this.backgroundTablet = new View(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            this.backgroundTablet.setBackgroundDrawable(drawable);
            launchLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            launchLayout.addView(this.actionBarLayout);
            this.rightActionBarLayout = new ActionBarLayout(this);
            this.rightActionBarLayout.init(rightFragmentsStack);
            this.rightActionBarLayout.setDelegate(this);
            launchLayout.addView(this.rightActionBarLayout);
            this.shadowTabletSide = new FrameLayout(this);
            this.shadowTabletSide.setBackgroundColor(NUM);
            launchLayout.addView(this.shadowTabletSide);
            this.shadowTablet = new FrameLayout(this);
            int i2 = 8;
            this.shadowTablet.setVisibility(layerFragmentsStack.isEmpty() ? 8 : 0);
            this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            launchLayout.addView(this.shadowTablet);
            this.shadowTablet.setOnTouchListener(new C14572());
            this.shadowTablet.setOnClickListener(new C14593());
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(R.drawable.boxshadow);
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
            ActionBarLayout actionBarLayout = this.layersActionBarLayout;
            if (!layerFragmentsStack.isEmpty()) {
                i2 = 0;
            }
            actionBarLayout.setVisibility(i2);
            launchLayout.addView(this.layersActionBarLayout);
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
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.sideMenu.getLayoutParams();
        Point screenSize = AndroidUtilities.getRealScreenSize();
        layoutParams.width = AndroidUtilities.isTablet() ? AndroidUtilities.dp(320.0f) : Math.min(AndroidUtilities.dp(320.0f), Math.min(screenSize.x, screenSize.y) - AndroidUtilities.dp(56.0f));
        layoutParams.height = -1;
        this.sideMenu.setLayoutParams(layoutParams);
        this.sideMenu.setOnItemClickListener(new C21674());
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
                    fragmentName = savedInstanceState.getString("fragment");
                    if (fragmentName != null) {
                        Bundle args = savedInstanceState.getBundle("args");
                        switch (fragmentName.hashCode()) {
                            case -1529105743:
                                if (fragmentName.equals("wallpapers")) {
                                    i = 6;
                                    break;
                                }
                                break;
                            case -1349522494:
                                if (fragmentName.equals("chat_profile")) {
                                    i = 5;
                                    break;
                                }
                                break;
                            case 3052376:
                                if (fragmentName.equals("chat")) {
                                    i = 0;
                                    break;
                                }
                                break;
                            case 3108362:
                                if (fragmentName.equals("edit")) {
                                    i = 4;
                                    break;
                                }
                                break;
                            case 98629247:
                                if (fragmentName.equals("group")) {
                                    i = 2;
                                    break;
                                }
                                break;
                            case 738950403:
                                if (fragmentName.equals("channel")) {
                                    i = 3;
                                    break;
                                }
                                break;
                            case 1434631203:
                                if (fragmentName.equals("settings")) {
                                    i = 1;
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                        switch (i) {
                            case 0:
                                if (args != null) {
                                    ChatActivity chat = new ChatActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(chat)) {
                                        chat.restoreSelfArgs(savedInstanceState);
                                    }
                                    break;
                                }
                                break;
                            case 1:
                                SettingsActivity settings = new SettingsActivity();
                                this.actionBarLayout.addFragmentToStack(settings);
                                settings.restoreSelfArgs(savedInstanceState);
                                break;
                            case 2:
                                if (args != null) {
                                    GroupCreateFinalActivity group = new GroupCreateFinalActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(group)) {
                                        group.restoreSelfArgs(savedInstanceState);
                                    }
                                    break;
                                }
                                break;
                            case 3:
                                if (args != null) {
                                    ChannelCreateActivity channel = new ChannelCreateActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(channel)) {
                                        channel.restoreSelfArgs(savedInstanceState);
                                    }
                                    break;
                                }
                                break;
                            case 4:
                                if (args != null) {
                                    ChannelEditActivity channel2 = new ChannelEditActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(channel2)) {
                                        channel2.restoreSelfArgs(savedInstanceState);
                                    }
                                    break;
                                }
                                break;
                            case 5:
                                if (args != null) {
                                    ProfileActivity profile = new ProfileActivity(args);
                                    if (this.actionBarLayout.addFragmentToStack(profile)) {
                                        profile.restoreSelfArgs(savedInstanceState);
                                    }
                                    break;
                                }
                                break;
                            case 6:
                                WallpapersActivity settings2 = new WallpapersActivity();
                                this.actionBarLayout.addFragmentToStack(settings2);
                                settings2.restoreSelfArgs(savedInstanceState);
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Throwable e3) {
                    FileLog.m3e(e3);
                }
            }
        } else {
            BaseFragment fragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(0);
            if (fragment instanceof DialogsActivity) {
                ((DialogsActivity) fragment).setSideMenu(this.sideMenu);
            }
            boolean allowOpen = true;
            if (AndroidUtilities.isTablet()) {
                boolean z = this.actionBarLayout.fragmentsStack.size() <= 1 && this.layersActionBarLayout.fragmentsStack.isEmpty();
                allowOpen = z;
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
            fragmentName = Build.USER;
            if (os1 != null) {
                os1 = os1.toLowerCase();
            } else {
                os1 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (fragmentName != null) {
                fragmentName = os1.toLowerCase();
            } else {
                fragmentName = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (os1.contains("flyme") || os2.contains("flyme")) {
                AndroidUtilities.incorrectDisplaySizeFix = true;
                final View view = getWindow().getDecorView().getRootView();
                ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
                OnGlobalLayoutListener c14605 = new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        int height = view.getMeasuredHeight();
                        if (VERSION.SDK_INT >= 21) {
                            height -= AndroidUtilities.statusBarHeight;
                        }
                        if (height > AndroidUtilities.dp(100.0f) && height < AndroidUtilities.displaySize.y && AndroidUtilities.dp(100.0f) + height > AndroidUtilities.displaySize.y) {
                            AndroidUtilities.displaySize.y = height;
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("fix display size y to ");
                                stringBuilder.append(AndroidUtilities.displaySize.y);
                                FileLog.m0d(stringBuilder.toString());
                            }
                        }
                    }
                };
                this.onGlobalLayoutListener = c14605;
                viewTreeObserver.addOnGlobalLayoutListener(c14605);
            }
        } catch (Throwable e4) {
            FileLog.m3e(e4);
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
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout != null) {
                int i = 8;
                int a;
                BaseFragment chatFragment;
                if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                    this.tabletFullSize = true;
                    if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        for (a = 0; a < this.rightActionBarLayout.fragmentsStack.size(); a = (a - 1) + 1) {
                            chatFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(a);
                            if (chatFragment instanceof ChatActivity) {
                                ((ChatActivity) chatFragment).setIgnoreAttachOnPause(true);
                            }
                            chatFragment.onPause();
                            this.rightActionBarLayout.fragmentsStack.remove(a);
                            this.actionBarLayout.fragmentsStack.add(chatFragment);
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

    private boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword) {
        Integer num;
        Integer num2;
        Integer num3;
        long dialogId;
        long j;
        int[] intentAccount;
        boolean z;
        Intent push_user_id;
        boolean push_chat_id;
        Throwable push_user_id2;
        Throwable e;
        Integer push_enc_id;
        Integer open_settings;
        Integer push_chat_id2;
        long dialogId2;
        Bundle args;
        int a;
        Integer num4;
        Bundle bundle;
        DialogsActivity dialogsActivity;
        Bundle args2;
        DialogsActivity fragment;
        boolean z2;
        ArrayList<Long> dids;
        Integer num5;
        int[] intentAccount2;
        LaunchActivity launchActivity = this;
        Intent intent2 = intent;
        boolean z3 = restore;
        if (AndroidUtilities.handleProxyIntent(this, intent)) {
            return true;
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible() && (intent2 == null || !"android.intent.action.MAIN".equals(intent.getAction()))) {
            PhotoViewer.getInstance().closePhoto(false, true);
        }
        int flags = intent.getFlags();
        Integer open_new_dialog = new int[]{intent2.getIntExtra("currentAccount", UserConfig.selectedAccount)};
        switchToAccount(open_new_dialog[0], true);
        if (fromPassword || !(AndroidUtilities.needShowPasscode(true) || SharedConfig.isWaitingForPasscodeEnter)) {
            boolean z4 = isNew;
            boolean pushOpened = false;
            Integer push_user_id3 = Integer.valueOf(0);
            Integer push_chat_id3 = Integer.valueOf(0);
            Integer push_enc_id2 = Integer.valueOf(0);
            Integer push_user_id4 = Integer.valueOf(0);
            Integer valueOf = Integer.valueOf(0);
            Integer valueOf2 = Integer.valueOf(0);
            long dialogId3 = 0;
            if (SharedConfig.directShare) {
                long j2 = (intent2 == null || intent.getExtras() == null) ? 0 : intent.getExtras().getLong("dialogId", 0);
                dialogId3 = j2;
            }
            Integer push_msg_id = dialogId3;
            boolean showDialogsList = false;
            boolean showPlayer = false;
            boolean showLocations = false;
            launchActivity.photoPathsArray = null;
            launchActivity.videoPath = null;
            launchActivity.sendingText = null;
            launchActivity.documentsPathsArray = null;
            launchActivity.documentsOriginalPathsArray = null;
            launchActivity.documentsMimeType = null;
            launchActivity.documentsUrisArray = null;
            launchActivity.contactsToSend = null;
            if (!UserConfig.getInstance(launchActivity.currentAccount).isClientActivated() || (flags & ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES) != 0 || intent2 == null || intent.getAction() == null || z3) {
                num = push_user_id3;
                num2 = push_chat_id3;
                num3 = push_user_id4;
                dialogId = push_msg_id;
                j = 0;
                intentAccount = open_new_dialog;
                z = false;
                push_user_id = intent2;
                push_chat_id = true;
            } else {
                long dialogId4;
                String type;
                String subject;
                String nameEncoding;
                String param;
                StringBuilder stringBuilder;
                if ("android.intent.action.SEND".equals(intent.getAction())) {
                    type = intent.getType();
                    boolean z5;
                    if (type == null || !type.equals("text/x-vcard")) {
                        num = push_user_id3;
                        num2 = push_chat_id3;
                        num3 = push_user_id4;
                        z5 = false;
                        dialogId4 = push_msg_id;
                        push_user_id3 = intent2.getStringExtra("android.intent.extra.TEXT");
                        if (push_user_id3 == null) {
                            CharSequence textSequence = intent2.getCharSequenceExtra("android.intent.extra.TEXT");
                            if (textSequence != null) {
                                push_user_id3 = textSequence.toString();
                            }
                        }
                        subject = intent2.getStringExtra("android.intent.extra.SUBJECT");
                        if (push_user_id3 != null && push_user_id3.length() != null) {
                            if (!((push_user_id3.startsWith("http://") == null && push_user_id3.startsWith("https://") == null) || subject == null || subject.length() == null)) {
                                push_user_id4 = new StringBuilder();
                                push_user_id4.append(subject);
                                push_user_id4.append("\n");
                                push_user_id4.append(push_user_id3);
                                push_user_id3 = push_user_id4.toString();
                            }
                            launchActivity.sendingText = push_user_id3;
                        } else if (subject != null && subject.length() > null) {
                            launchActivity.sendingText = subject;
                        }
                        push_user_id4 = intent2.getParcelableExtra("android.intent.extra.STREAM");
                        if (push_user_id4 != null) {
                            if (!(push_user_id4 instanceof Uri)) {
                                push_user_id4 = Uri.parse(push_user_id4.toString());
                            }
                            Uri uri = (Uri) push_user_id4;
                            if (!(uri == null || AndroidUtilities.isInternalUri(uri) == null)) {
                                z5 = true;
                            }
                            if (!z5) {
                                if (uri == null || ((type == null || type.startsWith("image/") == null) && uri.toString().toLowerCase().endsWith(".jpg") == null)) {
                                    push_msg_id = AndroidUtilities.getPath(uri);
                                    if (push_msg_id != null) {
                                        if (push_msg_id.startsWith("file:")) {
                                            push_msg_id = push_msg_id.replace("file://", TtmlNode.ANONYMOUS_REGION_ID);
                                        }
                                        if (type == null || !type.startsWith("video/")) {
                                            if (launchActivity.documentsPathsArray == null) {
                                                launchActivity.documentsPathsArray = new ArrayList();
                                                launchActivity.documentsOriginalPathsArray = new ArrayList();
                                            }
                                            launchActivity.documentsPathsArray.add(push_msg_id);
                                            launchActivity.documentsOriginalPathsArray.add(uri.toString());
                                        } else {
                                            launchActivity.videoPath = push_msg_id;
                                        }
                                    } else {
                                        if (launchActivity.documentsUrisArray == null) {
                                            launchActivity.documentsUrisArray = new ArrayList();
                                        }
                                        launchActivity.documentsUrisArray.add(uri);
                                        launchActivity.documentsMimeType = type;
                                    }
                                } else {
                                    if (launchActivity.photoPathsArray == null) {
                                        launchActivity.photoPathsArray = new ArrayList();
                                    }
                                    push_msg_id = new SendingMediaInfo();
                                    push_msg_id.uri = uri;
                                    launchActivity.photoPathsArray.add(push_msg_id);
                                }
                            }
                        } else if (launchActivity.sendingText == null) {
                            z = true;
                        }
                        z = z5;
                    } else {
                        try {
                            Uri uri2 = (Uri) intent.getExtras().get("android.intent.extra.STREAM");
                            Uri uri3;
                            if (uri2 != null) {
                                ContentResolver cr = getContentResolver();
                                InputStream game = cr.openInputStream(uri2);
                                ArrayList<VcardData> vcardDatas = new ArrayList();
                                VcardData currentData = null;
                                num = push_user_id3;
                                try {
                                    num2 = push_chat_id3;
                                    try {
                                        num3 = push_user_id4;
                                        try {
                                            z5 = false;
                                            InputStream stream = game;
                                            try {
                                                String push_user_id5;
                                                String line;
                                                ContentResolver contentResolver;
                                                push_user_id3 = new BufferedReader(new InputStreamReader(stream, C0542C.UTF8_NAME));
                                                while (true) {
                                                    subject = push_user_id3.readLine();
                                                    push_user_id5 = subject;
                                                    if (subject == null) {
                                                        break;
                                                    }
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        try {
                                                            FileLog.m0d(push_user_id5);
                                                        } catch (Throwable e2) {
                                                            push_user_id2 = e2;
                                                            dialogId4 = push_msg_id;
                                                        }
                                                    }
                                                    String[] args3 = push_user_id5.split(":");
                                                    line = push_user_id5;
                                                    dialogId4 = push_msg_id;
                                                    if (args3.length != 2) {
                                                        push_msg_id = dialogId4;
                                                    } else {
                                                        try {
                                                            if (args3[null].equals("BEGIN") == null || args3[1].equals("VCARD") == null) {
                                                                push_user_id4 = (args3[null].equals("END") == null || args3[1].equals("VCARD") == null) ? currentData : null;
                                                            } else {
                                                                push_user_id4 = new VcardData();
                                                                Integer currentData2 = push_user_id4;
                                                                vcardDatas.add(push_user_id4);
                                                                push_user_id4 = currentData2;
                                                            }
                                                            if (push_user_id4 == null) {
                                                                uri3 = uri2;
                                                                contentResolver = cr;
                                                            } else {
                                                                String[] params;
                                                                if (args3[0].startsWith("FN") == null) {
                                                                    if (args3[null].startsWith("ORG") == null || TextUtils.isEmpty(push_user_id4.name) == null) {
                                                                        if (args3[null].startsWith("TEL") != null) {
                                                                            String push_msg_id2 = PhoneFormat.stripExceptNumbers(args3[1], true);
                                                                            if (push_msg_id2.length() > 0) {
                                                                                push_user_id4.phones.add(push_msg_id2);
                                                                            }
                                                                        }
                                                                        uri3 = uri2;
                                                                        contentResolver = cr;
                                                                    }
                                                                }
                                                                Integer nameEncoding2 = null;
                                                                String nameCharset = null;
                                                                push_msg_id = args3[0].split(";");
                                                                int length = push_msg_id.length;
                                                                uri3 = uri2;
                                                                contentResolver = cr;
                                                                nameEncoding = nameEncoding2;
                                                                cr = nameCharset;
                                                                int uri4 = 0;
                                                                while (uri4 < length) {
                                                                    params = push_msg_id;
                                                                    int i = length;
                                                                    param = push_msg_id[uri4];
                                                                    push_msg_id = param.split("=");
                                                                    if (push_msg_id.length == 2) {
                                                                        if (push_msg_id[0].equals("CHARSET")) {
                                                                            cr = push_msg_id[1];
                                                                        } else if (push_msg_id[0].equals("ENCODING")) {
                                                                            nameEncoding = push_msg_id[1];
                                                                        }
                                                                    }
                                                                    uri4++;
                                                                    push_msg_id = params;
                                                                    length = i;
                                                                    z3 = restore;
                                                                }
                                                                params = push_msg_id;
                                                                push_user_id4.name = args3[1];
                                                                if (!(nameEncoding == null || nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") == null)) {
                                                                    while (push_user_id4.name.endsWith("=") != null && nameEncoding != null) {
                                                                        push_user_id4.name = push_user_id4.name.substring(0, push_user_id4.name.length() - 1);
                                                                        push_msg_id = push_user_id3.readLine();
                                                                        Integer num6;
                                                                        if (push_msg_id == null) {
                                                                            num6 = push_msg_id;
                                                                            break;
                                                                        }
                                                                        stringBuilder = new StringBuilder();
                                                                        stringBuilder.append(push_user_id4.name);
                                                                        stringBuilder.append(push_msg_id);
                                                                        push_user_id4.name = stringBuilder.toString();
                                                                        num6 = push_msg_id;
                                                                    }
                                                                    push_msg_id = AndroidUtilities.decodeQuotedPrintable(push_user_id4.name.getBytes());
                                                                    if (!(push_msg_id == null || push_msg_id.length == 0)) {
                                                                        param = new String(push_msg_id, cr);
                                                                        if (param != null) {
                                                                            push_user_id4.name = param;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            currentData = push_user_id4;
                                                            push_msg_id = dialogId4;
                                                            uri2 = uri3;
                                                            cr = contentResolver;
                                                            z4 = isNew;
                                                            z3 = restore;
                                                        } catch (Throwable e22) {
                                                            push_user_id2 = e22;
                                                        }
                                                    }
                                                }
                                                line = push_user_id5;
                                                dialogId4 = push_msg_id;
                                                uri3 = uri2;
                                                contentResolver = cr;
                                                try {
                                                    push_user_id3.close();
                                                    stream.close();
                                                } catch (Throwable e222) {
                                                    FileLog.m3e(e222);
                                                }
                                                for (push_chat_id3 = null; push_chat_id3 < vcardDatas.size(); push_chat_id3++) {
                                                    VcardData push_user_id6 = (VcardData) vcardDatas.get(push_chat_id3);
                                                    if (push_user_id6.name != null && push_user_id6.phones.isEmpty() == null) {
                                                        if (launchActivity.contactsToSend == null) {
                                                            launchActivity.contactsToSend = new ArrayList();
                                                        }
                                                        for (push_msg_id = null; push_msg_id < push_user_id6.phones.size(); push_msg_id++) {
                                                            param = (String) push_user_id6.phones.get(push_msg_id);
                                                            uri2 = new TL_userContact_old2();
                                                            uri2.phone = param;
                                                            uri2.first_name = push_user_id6.name;
                                                            uri2.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                                            uri2.id = 0;
                                                            launchActivity.contactsToSend.add(uri2);
                                                        }
                                                    }
                                                }
                                                z = z5;
                                            } catch (Throwable e2222) {
                                                dialogId4 = push_msg_id;
                                                push_user_id2 = e2222;
                                            }
                                        } catch (Throwable e22222) {
                                            z5 = false;
                                            dialogId4 = push_msg_id;
                                            push_user_id2 = e22222;
                                            FileLog.m3e(push_user_id2);
                                            z = true;
                                            if (z) {
                                                Toast.makeText(launchActivity, "Unsupported content", 0).show();
                                            }
                                            intentAccount = open_new_dialog;
                                            push_user_id = intent2;
                                            dialogId = dialogId4;
                                            push_chat_id = true;
                                            z = false;
                                            j = 0;
                                            push_enc_id = push_enc_id2;
                                            open_settings = valueOf;
                                            open_new_dialog = valueOf2;
                                            push_user_id4 = num;
                                            push_chat_id2 = num2;
                                            push_msg_id = num3;
                                            if (push_user_id4.intValue() == 0) {
                                                dialogId2 = new Bundle();
                                                dialogId2.putInt("user_id", push_user_id4.intValue());
                                                if (push_msg_id.intValue() != 0) {
                                                    dialogId2.putInt("message_id", push_msg_id.intValue());
                                                }
                                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(dialogId2), z, push_chat_id, push_chat_id)) {
                                                    pushOpened = true;
                                                }
                                            } else if (push_chat_id2.intValue() == 0) {
                                                args = new Bundle();
                                                args.putInt("chat_id", push_chat_id2.intValue());
                                                if (push_msg_id.intValue() != 0) {
                                                    args.putInt("message_id", push_msg_id.intValue());
                                                }
                                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                                    pushOpened = true;
                                                }
                                            } else if (push_enc_id.intValue() == 0) {
                                                args = new Bundle();
                                                args.putInt("enc_id", push_enc_id.intValue());
                                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                                    pushOpened = true;
                                                }
                                            } else if (!showDialogsList) {
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.actionBarLayout.removeAllFragments();
                                                } else if (!launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                    for (a = z; a < launchActivity.layersActionBarLayout.fragmentsStack.size() - push_chat_id; a = (a - 1) + push_chat_id) {
                                                        launchActivity.layersActionBarLayout.removeFragmentFromStack((BaseFragment) launchActivity.layersActionBarLayout.fragmentsStack.get(z));
                                                    }
                                                    launchActivity.layersActionBarLayout.closeLastFragment(z);
                                                }
                                                pushOpened = false;
                                                num4 = push_user_id4;
                                                push_chat_id = false;
                                                bundle = null;
                                                if (AndroidUtilities.isTablet()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                            dialogsActivity = new DialogsActivity(bundle);
                                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                        }
                                                    } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        dialogsActivity = new DialogsActivity(null);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    } else {
                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                }
                                                launchActivity.actionBarLayout.showLastFragment();
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.layersActionBarLayout.showLastFragment();
                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                }
                                                push_user_id.setAction(null);
                                                return pushOpened;
                                            } else if (!showPlayer) {
                                                if (!launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new AudioPlayerAlert(launchActivity));
                                                }
                                                pushOpened = false;
                                            } else if (!showLocations) {
                                                if (!launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new SharingLocationsAlert(launchActivity, new SharingLocationsAlertDelegate() {
                                                        public void didSelectLocation(SharingLocationInfo info) {
                                                            intentAccount[0] = info.messageObject.currentAccount;
                                                            LaunchActivity.this.switchToAccount(intentAccount[0], true);
                                                            LocationActivity locationActivity = new LocationActivity(2);
                                                            locationActivity.setMessageObject(info.messageObject);
                                                            final long dialog_id = info.messageObject.getDialogId();
                                                            locationActivity.setDelegate(new LocationActivityDelegate() {
                                                                public void didSelectLocation(MessageMedia location, int live) {
                                                                    SendMessagesHelper.getInstance(intentAccount[0]).sendMessage(location, dialog_id, null, null, null);
                                                                }
                                                            });
                                                            LaunchActivity.this.presentFragment(locationActivity);
                                                        }
                                                    }));
                                                }
                                                pushOpened = false;
                                            } else if (launchActivity.documentsUrisArray != null) {
                                                if (!AndroidUtilities.isTablet()) {
                                                    NotificationCenter.getInstance(intentAccount[z]).postNotificationName(NotificationCenter.closeChats, new Object[z]);
                                                }
                                                dialogId2 = dialogId;
                                                if (dialogId2 == j) {
                                                    args2 = new Bundle();
                                                    args2.putBoolean("onlySelect", push_chat_id);
                                                    args2.putInt("dialogsType", 3);
                                                    args2.putBoolean("allowSwitchAccount", push_chat_id);
                                                    if (launchActivity.contactsToSend != null) {
                                                        args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                                                        args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                                                    } else {
                                                        args2.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                                                        args2.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                                                    }
                                                    fragment = new DialogsActivity(args2);
                                                    fragment.setDelegate(launchActivity);
                                                    if (AndroidUtilities.isTablet()) {
                                                        if (launchActivity.layersActionBarLayout.fragmentsStack.size() <= 0) {
                                                        }
                                                    }
                                                    num4 = push_user_id4;
                                                    launchActivity.actionBarLayout.presentFragment(fragment, z, true, true);
                                                    pushOpened = true;
                                                    if (!SecretMediaViewer.hasInstance()) {
                                                    }
                                                    if (PhotoViewer.hasInstance()) {
                                                    }
                                                    if (ArticleViewer.hasInstance()) {
                                                    }
                                                    z2 = false;
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                                                    if (AndroidUtilities.isTablet()) {
                                                        launchActivity.actionBarLayout.showLastFragment();
                                                        launchActivity.rightActionBarLayout.showLastFragment();
                                                    } else {
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    }
                                                    push_chat_id = isNew;
                                                    bundle = null;
                                                    if (AndroidUtilities.isTablet()) {
                                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                            if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                            }
                                                        } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                            dialogsActivity = new DialogsActivity(bundle);
                                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                        }
                                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                            launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                        } else {
                                                            dialogsActivity = new DialogsActivity(null);
                                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                        }
                                                    }
                                                    launchActivity.actionBarLayout.showLastFragment();
                                                    if (AndroidUtilities.isTablet()) {
                                                        launchActivity.layersActionBarLayout.showLastFragment();
                                                        launchActivity.rightActionBarLayout.showLastFragment();
                                                    }
                                                    push_user_id.setAction(null);
                                                    return pushOpened;
                                                }
                                                dids = new ArrayList();
                                                dids.add(Long.valueOf(dialogId2));
                                                bundle = null;
                                                didSelectDialogs(null, dids, null, false);
                                                push_chat_id = isNew;
                                                if (AndroidUtilities.isTablet()) {
                                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                            dialogsActivity = new DialogsActivity(null);
                                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                        } else {
                                                            launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                        }
                                                    }
                                                } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                        dialogsActivity = new DialogsActivity(bundle);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    }
                                                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                    launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                }
                                                launchActivity.actionBarLayout.showLastFragment();
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.layersActionBarLayout.showLastFragment();
                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                }
                                                push_user_id.setAction(null);
                                                return pushOpened;
                                            } else if (open_settings.intValue() == 0) {
                                                launchActivity.actionBarLayout.presentFragment(new SettingsActivity(), z, push_chat_id, push_chat_id);
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.actionBarLayout.showLastFragment();
                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                                } else {
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                                }
                                                pushOpened = true;
                                            } else if (open_new_dialog.intValue() != 0) {
                                                args = new Bundle();
                                                args.putBoolean("destroyAfterSelect", push_chat_id);
                                                launchActivity.actionBarLayout.presentFragment(new ContactsActivity(args), z, push_chat_id, push_chat_id);
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.actionBarLayout.showLastFragment();
                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                                } else {
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                                }
                                                pushOpened = true;
                                            } else {
                                                num4 = push_user_id4;
                                                dialogId2 = dialogId;
                                                bundle = null;
                                                push_chat_id = isNew;
                                                if (AndroidUtilities.isTablet()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                        }
                                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                        dialogsActivity = new DialogsActivity(bundle);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    }
                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    } else {
                                                        dialogsActivity = new DialogsActivity(null);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    }
                                                }
                                                launchActivity.actionBarLayout.showLastFragment();
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.layersActionBarLayout.showLastFragment();
                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                }
                                                push_user_id.setAction(null);
                                                return pushOpened;
                                            }
                                            push_chat_id = isNew;
                                            bundle = null;
                                            if (AndroidUtilities.isTablet()) {
                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        dialogsActivity = new DialogsActivity(null);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    } else {
                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                }
                                            } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    dialogsActivity = new DialogsActivity(bundle);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                }
                                            } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                            }
                                            launchActivity.actionBarLayout.showLastFragment();
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.layersActionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                            }
                                            push_user_id.setAction(null);
                                            return pushOpened;
                                        }
                                    } catch (Throwable e222222) {
                                        num3 = push_user_id4;
                                        z5 = false;
                                        dialogId4 = push_msg_id;
                                        push_user_id2 = e222222;
                                        FileLog.m3e(push_user_id2);
                                        z = true;
                                        if (z) {
                                            Toast.makeText(launchActivity, "Unsupported content", 0).show();
                                        }
                                        intentAccount = open_new_dialog;
                                        push_user_id = intent2;
                                        dialogId = dialogId4;
                                        push_chat_id = true;
                                        z = false;
                                        j = 0;
                                        push_enc_id = push_enc_id2;
                                        open_settings = valueOf;
                                        open_new_dialog = valueOf2;
                                        push_user_id4 = num;
                                        push_chat_id2 = num2;
                                        push_msg_id = num3;
                                        if (push_user_id4.intValue() == 0) {
                                            dialogId2 = new Bundle();
                                            dialogId2.putInt("user_id", push_user_id4.intValue());
                                            if (push_msg_id.intValue() != 0) {
                                                dialogId2.putInt("message_id", push_msg_id.intValue());
                                            }
                                            if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(dialogId2), z, push_chat_id, push_chat_id)) {
                                                pushOpened = true;
                                            }
                                        } else if (push_chat_id2.intValue() == 0) {
                                            args = new Bundle();
                                            args.putInt("chat_id", push_chat_id2.intValue());
                                            if (push_msg_id.intValue() != 0) {
                                                args.putInt("message_id", push_msg_id.intValue());
                                            }
                                            if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                                pushOpened = true;
                                            }
                                        } else if (push_enc_id.intValue() == 0) {
                                            args = new Bundle();
                                            args.putInt("enc_id", push_enc_id.intValue());
                                            if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                                pushOpened = true;
                                            }
                                        } else if (!showDialogsList) {
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.actionBarLayout.removeAllFragments();
                                            } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                for (a = z; a < launchActivity.layersActionBarLayout.fragmentsStack.size() - push_chat_id; a = (a - 1) + push_chat_id) {
                                                    launchActivity.layersActionBarLayout.removeFragmentFromStack((BaseFragment) launchActivity.layersActionBarLayout.fragmentsStack.get(z));
                                                }
                                                launchActivity.layersActionBarLayout.closeLastFragment(z);
                                            }
                                            pushOpened = false;
                                            num4 = push_user_id4;
                                            push_chat_id = false;
                                            bundle = null;
                                            if (AndroidUtilities.isTablet()) {
                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        dialogsActivity = new DialogsActivity(null);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    } else {
                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                }
                                            } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    dialogsActivity = new DialogsActivity(bundle);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                }
                                            } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                            }
                                            launchActivity.actionBarLayout.showLastFragment();
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.layersActionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                            }
                                            push_user_id.setAction(null);
                                            return pushOpened;
                                        } else if (!showPlayer) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new AudioPlayerAlert(launchActivity));
                                            }
                                            pushOpened = false;
                                        } else if (!showLocations) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new SharingLocationsAlert(launchActivity, /* anonymous class already generated */));
                                            }
                                            pushOpened = false;
                                        } else if (launchActivity.documentsUrisArray != null) {
                                            if (AndroidUtilities.isTablet()) {
                                                NotificationCenter.getInstance(intentAccount[z]).postNotificationName(NotificationCenter.closeChats, new Object[z]);
                                            }
                                            dialogId2 = dialogId;
                                            if (dialogId2 == j) {
                                                args2 = new Bundle();
                                                args2.putBoolean("onlySelect", push_chat_id);
                                                args2.putInt("dialogsType", 3);
                                                args2.putBoolean("allowSwitchAccount", push_chat_id);
                                                if (launchActivity.contactsToSend != null) {
                                                    args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                                                } else {
                                                    args2.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                                                }
                                                fragment = new DialogsActivity(args2);
                                                fragment.setDelegate(launchActivity);
                                                if (AndroidUtilities.isTablet()) {
                                                    if (launchActivity.layersActionBarLayout.fragmentsStack.size() <= 0) {
                                                    }
                                                }
                                                num4 = push_user_id4;
                                                launchActivity.actionBarLayout.presentFragment(fragment, z, true, true);
                                                pushOpened = true;
                                                if (!SecretMediaViewer.hasInstance()) {
                                                }
                                                if (PhotoViewer.hasInstance()) {
                                                }
                                                if (ArticleViewer.hasInstance()) {
                                                }
                                                z2 = false;
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                } else {
                                                    launchActivity.actionBarLayout.showLastFragment();
                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                }
                                                push_chat_id = isNew;
                                                bundle = null;
                                                if (AndroidUtilities.isTablet()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                        }
                                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                        dialogsActivity = new DialogsActivity(bundle);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    }
                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    } else {
                                                        dialogsActivity = new DialogsActivity(null);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    }
                                                }
                                                launchActivity.actionBarLayout.showLastFragment();
                                                if (AndroidUtilities.isTablet()) {
                                                    launchActivity.layersActionBarLayout.showLastFragment();
                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                }
                                                push_user_id.setAction(null);
                                                return pushOpened;
                                            }
                                            dids = new ArrayList();
                                            dids.add(Long.valueOf(dialogId2));
                                            bundle = null;
                                            didSelectDialogs(null, dids, null, false);
                                            push_chat_id = isNew;
                                            if (AndroidUtilities.isTablet()) {
                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                        dialogsActivity = new DialogsActivity(null);
                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    } else {
                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                }
                                            } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    dialogsActivity = new DialogsActivity(bundle);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                }
                                            } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                            }
                                            launchActivity.actionBarLayout.showLastFragment();
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.layersActionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                            }
                                            push_user_id.setAction(null);
                                            return pushOpened;
                                        } else if (open_settings.intValue() == 0) {
                                            launchActivity.actionBarLayout.presentFragment(new SettingsActivity(), z, push_chat_id, push_chat_id);
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.actionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                            } else {
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                            }
                                            pushOpened = true;
                                        } else if (open_new_dialog.intValue() != 0) {
                                            args = new Bundle();
                                            args.putBoolean("destroyAfterSelect", push_chat_id);
                                            launchActivity.actionBarLayout.presentFragment(new ContactsActivity(args), z, push_chat_id, push_chat_id);
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.actionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                            } else {
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                            }
                                            pushOpened = true;
                                        } else {
                                            num4 = push_user_id4;
                                            dialogId2 = dialogId;
                                            bundle = null;
                                            push_chat_id = isNew;
                                            if (AndroidUtilities.isTablet()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    dialogsActivity = new DialogsActivity(bundle);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                }
                                            } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                } else {
                                                    dialogsActivity = new DialogsActivity(null);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                }
                                            }
                                            launchActivity.actionBarLayout.showLastFragment();
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.layersActionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                            }
                                            push_user_id.setAction(null);
                                            return pushOpened;
                                        }
                                        push_chat_id = isNew;
                                        bundle = null;
                                        if (AndroidUtilities.isTablet()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    dialogsActivity = new DialogsActivity(null);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                } else {
                                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                }
                                            }
                                        } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                dialogsActivity = new DialogsActivity(bundle);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            }
                                        } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                        launchActivity.actionBarLayout.showLastFragment();
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.layersActionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                        }
                                        push_user_id.setAction(null);
                                        return pushOpened;
                                    }
                                } catch (Throwable e2222222) {
                                    num2 = push_chat_id3;
                                    num3 = push_user_id4;
                                    z5 = false;
                                    dialogId4 = push_msg_id;
                                    push_user_id2 = e2222222;
                                    FileLog.m3e(push_user_id2);
                                    z = true;
                                    if (z) {
                                        Toast.makeText(launchActivity, "Unsupported content", 0).show();
                                    }
                                    intentAccount = open_new_dialog;
                                    push_user_id = intent2;
                                    dialogId = dialogId4;
                                    push_chat_id = true;
                                    z = false;
                                    j = 0;
                                    push_enc_id = push_enc_id2;
                                    open_settings = valueOf;
                                    open_new_dialog = valueOf2;
                                    push_user_id4 = num;
                                    push_chat_id2 = num2;
                                    push_msg_id = num3;
                                    if (push_user_id4.intValue() == 0) {
                                        dialogId2 = new Bundle();
                                        dialogId2.putInt("user_id", push_user_id4.intValue());
                                        if (push_msg_id.intValue() != 0) {
                                            dialogId2.putInt("message_id", push_msg_id.intValue());
                                        }
                                        if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(dialogId2), z, push_chat_id, push_chat_id)) {
                                            pushOpened = true;
                                        }
                                    } else if (push_chat_id2.intValue() == 0) {
                                        args = new Bundle();
                                        args.putInt("chat_id", push_chat_id2.intValue());
                                        if (push_msg_id.intValue() != 0) {
                                            args.putInt("message_id", push_msg_id.intValue());
                                        }
                                        if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                            pushOpened = true;
                                        }
                                    } else if (push_enc_id.intValue() == 0) {
                                        args = new Bundle();
                                        args.putInt("enc_id", push_enc_id.intValue());
                                        if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                            pushOpened = true;
                                        }
                                    } else if (!showDialogsList) {
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.actionBarLayout.removeAllFragments();
                                        } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            for (a = z; a < launchActivity.layersActionBarLayout.fragmentsStack.size() - push_chat_id; a = (a - 1) + push_chat_id) {
                                                launchActivity.layersActionBarLayout.removeFragmentFromStack((BaseFragment) launchActivity.layersActionBarLayout.fragmentsStack.get(z));
                                            }
                                            launchActivity.layersActionBarLayout.closeLastFragment(z);
                                        }
                                        pushOpened = false;
                                        num4 = push_user_id4;
                                        push_chat_id = false;
                                        bundle = null;
                                        if (AndroidUtilities.isTablet()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    dialogsActivity = new DialogsActivity(null);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                } else {
                                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                }
                                            }
                                        } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                dialogsActivity = new DialogsActivity(bundle);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            }
                                        } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                        launchActivity.actionBarLayout.showLastFragment();
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.layersActionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                        }
                                        push_user_id.setAction(null);
                                        return pushOpened;
                                    } else if (!showPlayer) {
                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new AudioPlayerAlert(launchActivity));
                                        }
                                        pushOpened = false;
                                    } else if (!showLocations) {
                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new SharingLocationsAlert(launchActivity, /* anonymous class already generated */));
                                        }
                                        pushOpened = false;
                                    } else if (launchActivity.documentsUrisArray != null) {
                                        if (AndroidUtilities.isTablet()) {
                                            NotificationCenter.getInstance(intentAccount[z]).postNotificationName(NotificationCenter.closeChats, new Object[z]);
                                        }
                                        dialogId2 = dialogId;
                                        if (dialogId2 == j) {
                                            args2 = new Bundle();
                                            args2.putBoolean("onlySelect", push_chat_id);
                                            args2.putInt("dialogsType", 3);
                                            args2.putBoolean("allowSwitchAccount", push_chat_id);
                                            if (launchActivity.contactsToSend != null) {
                                                args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                                                args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                                            } else {
                                                args2.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                                                args2.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                                            }
                                            fragment = new DialogsActivity(args2);
                                            fragment.setDelegate(launchActivity);
                                            if (AndroidUtilities.isTablet()) {
                                                if (launchActivity.layersActionBarLayout.fragmentsStack.size() <= 0) {
                                                }
                                            }
                                            num4 = push_user_id4;
                                            launchActivity.actionBarLayout.presentFragment(fragment, z, true, true);
                                            pushOpened = true;
                                            if (!SecretMediaViewer.hasInstance()) {
                                            }
                                            if (PhotoViewer.hasInstance()) {
                                            }
                                            if (ArticleViewer.hasInstance()) {
                                            }
                                            z2 = false;
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            } else {
                                                launchActivity.actionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                            }
                                            push_chat_id = isNew;
                                            bundle = null;
                                            if (AndroidUtilities.isTablet()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                    dialogsActivity = new DialogsActivity(bundle);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                }
                                            } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                } else {
                                                    dialogsActivity = new DialogsActivity(null);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                }
                                            }
                                            launchActivity.actionBarLayout.showLastFragment();
                                            if (AndroidUtilities.isTablet()) {
                                                launchActivity.layersActionBarLayout.showLastFragment();
                                                launchActivity.rightActionBarLayout.showLastFragment();
                                            }
                                            push_user_id.setAction(null);
                                            return pushOpened;
                                        }
                                        dids = new ArrayList();
                                        dids.add(Long.valueOf(dialogId2));
                                        bundle = null;
                                        didSelectDialogs(null, dids, null, false);
                                        push_chat_id = isNew;
                                        if (AndroidUtilities.isTablet()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    dialogsActivity = new DialogsActivity(null);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                } else {
                                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                }
                                            }
                                        } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                dialogsActivity = new DialogsActivity(bundle);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            }
                                        } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                        launchActivity.actionBarLayout.showLastFragment();
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.layersActionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                        }
                                        push_user_id.setAction(null);
                                        return pushOpened;
                                    } else if (open_settings.intValue() == 0) {
                                        launchActivity.actionBarLayout.presentFragment(new SettingsActivity(), z, push_chat_id, push_chat_id);
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.actionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                        } else {
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                        }
                                        pushOpened = true;
                                    } else if (open_new_dialog.intValue() != 0) {
                                        args = new Bundle();
                                        args.putBoolean("destroyAfterSelect", push_chat_id);
                                        launchActivity.actionBarLayout.presentFragment(new ContactsActivity(args), z, push_chat_id, push_chat_id);
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.actionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                        } else {
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                        }
                                        pushOpened = true;
                                    } else {
                                        num4 = push_user_id4;
                                        dialogId2 = dialogId;
                                        bundle = null;
                                        push_chat_id = isNew;
                                        if (AndroidUtilities.isTablet()) {
                                            if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                    launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                }
                                            } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                dialogsActivity = new DialogsActivity(bundle);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            }
                                        } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                            } else {
                                                dialogsActivity = new DialogsActivity(null);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            }
                                        }
                                        launchActivity.actionBarLayout.showLastFragment();
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.layersActionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                        }
                                        push_user_id.setAction(null);
                                        return pushOpened;
                                    }
                                    push_chat_id = isNew;
                                    bundle = null;
                                    if (AndroidUtilities.isTablet()) {
                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                dialogsActivity = new DialogsActivity(null);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            } else {
                                                launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                            }
                                        }
                                    } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            dialogsActivity = new DialogsActivity(bundle);
                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                        }
                                    } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    }
                                    launchActivity.actionBarLayout.showLastFragment();
                                    if (AndroidUtilities.isTablet()) {
                                        launchActivity.layersActionBarLayout.showLastFragment();
                                        launchActivity.rightActionBarLayout.showLastFragment();
                                    }
                                    push_user_id.setAction(null);
                                    return pushOpened;
                                }
                            }
                            num = push_user_id3;
                            num2 = push_chat_id3;
                            num3 = push_user_id4;
                            z5 = false;
                            dialogId4 = push_msg_id;
                            uri3 = uri2;
                            z = true;
                        } catch (Throwable e22222222) {
                            num = push_user_id3;
                            num2 = push_chat_id3;
                            num3 = push_user_id4;
                            z5 = false;
                            dialogId4 = push_msg_id;
                            push_user_id2 = e22222222;
                            FileLog.m3e(push_user_id2);
                            z = true;
                            if (z) {
                                Toast.makeText(launchActivity, "Unsupported content", 0).show();
                            }
                            intentAccount = open_new_dialog;
                            push_user_id = intent2;
                            dialogId = dialogId4;
                            push_chat_id = true;
                            z = false;
                            j = 0;
                            push_enc_id = push_enc_id2;
                            open_settings = valueOf;
                            open_new_dialog = valueOf2;
                            push_user_id4 = num;
                            push_chat_id2 = num2;
                            push_msg_id = num3;
                            if (push_user_id4.intValue() == 0) {
                                dialogId2 = new Bundle();
                                dialogId2.putInt("user_id", push_user_id4.intValue());
                                if (push_msg_id.intValue() != 0) {
                                    dialogId2.putInt("message_id", push_msg_id.intValue());
                                }
                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(dialogId2), z, push_chat_id, push_chat_id)) {
                                    pushOpened = true;
                                }
                            } else if (push_chat_id2.intValue() == 0) {
                                args = new Bundle();
                                args.putInt("chat_id", push_chat_id2.intValue());
                                if (push_msg_id.intValue() != 0) {
                                    args.putInt("message_id", push_msg_id.intValue());
                                }
                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                    pushOpened = true;
                                }
                            } else if (push_enc_id.intValue() == 0) {
                                args = new Bundle();
                                args.putInt("enc_id", push_enc_id.intValue());
                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                    pushOpened = true;
                                }
                            } else if (!showDialogsList) {
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.actionBarLayout.removeAllFragments();
                                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                    for (a = z; a < launchActivity.layersActionBarLayout.fragmentsStack.size() - push_chat_id; a = (a - 1) + push_chat_id) {
                                        launchActivity.layersActionBarLayout.removeFragmentFromStack((BaseFragment) launchActivity.layersActionBarLayout.fragmentsStack.get(z));
                                    }
                                    launchActivity.layersActionBarLayout.closeLastFragment(z);
                                }
                                pushOpened = false;
                                num4 = push_user_id4;
                                push_chat_id = false;
                                bundle = null;
                                if (AndroidUtilities.isTablet()) {
                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            dialogsActivity = new DialogsActivity(null);
                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                        } else {
                                            launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                    }
                                } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        dialogsActivity = new DialogsActivity(bundle);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                    launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                }
                                launchActivity.actionBarLayout.showLastFragment();
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.layersActionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                }
                                push_user_id.setAction(null);
                                return pushOpened;
                            } else if (!showPlayer) {
                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new AudioPlayerAlert(launchActivity));
                                }
                                pushOpened = false;
                            } else if (!showLocations) {
                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new SharingLocationsAlert(launchActivity, /* anonymous class already generated */));
                                }
                                pushOpened = false;
                            } else if (launchActivity.documentsUrisArray != null) {
                                if (AndroidUtilities.isTablet()) {
                                    NotificationCenter.getInstance(intentAccount[z]).postNotificationName(NotificationCenter.closeChats, new Object[z]);
                                }
                                dialogId2 = dialogId;
                                if (dialogId2 == j) {
                                    dids = new ArrayList();
                                    dids.add(Long.valueOf(dialogId2));
                                    bundle = null;
                                    didSelectDialogs(null, dids, null, false);
                                    push_chat_id = isNew;
                                    if (AndroidUtilities.isTablet()) {
                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                dialogsActivity = new DialogsActivity(null);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            } else {
                                                launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                            }
                                        }
                                    } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            dialogsActivity = new DialogsActivity(bundle);
                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                        }
                                    } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    }
                                    launchActivity.actionBarLayout.showLastFragment();
                                    if (AndroidUtilities.isTablet()) {
                                        launchActivity.layersActionBarLayout.showLastFragment();
                                        launchActivity.rightActionBarLayout.showLastFragment();
                                    }
                                    push_user_id.setAction(null);
                                    return pushOpened;
                                }
                                args2 = new Bundle();
                                args2.putBoolean("onlySelect", push_chat_id);
                                args2.putInt("dialogsType", 3);
                                args2.putBoolean("allowSwitchAccount", push_chat_id);
                                if (launchActivity.contactsToSend != null) {
                                    args2.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                                } else {
                                    args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                                }
                                fragment = new DialogsActivity(args2);
                                fragment.setDelegate(launchActivity);
                                if (AndroidUtilities.isTablet()) {
                                    if (launchActivity.actionBarLayout.fragmentsStack.size() <= 1) {
                                    }
                                }
                                num4 = push_user_id4;
                                launchActivity.actionBarLayout.presentFragment(fragment, z, true, true);
                                pushOpened = true;
                                if (!SecretMediaViewer.hasInstance()) {
                                }
                                if (PhotoViewer.hasInstance()) {
                                }
                                if (ArticleViewer.hasInstance()) {
                                }
                                z2 = false;
                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.actionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                } else {
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                }
                                push_chat_id = isNew;
                                bundle = null;
                                if (AndroidUtilities.isTablet()) {
                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        dialogsActivity = new DialogsActivity(bundle);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    } else {
                                        dialogsActivity = new DialogsActivity(null);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                }
                                launchActivity.actionBarLayout.showLastFragment();
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.layersActionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                }
                                push_user_id.setAction(null);
                                return pushOpened;
                            } else if (open_settings.intValue() == 0) {
                                launchActivity.actionBarLayout.presentFragment(new SettingsActivity(), z, push_chat_id, push_chat_id);
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                } else {
                                    launchActivity.actionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                }
                                pushOpened = true;
                            } else if (open_new_dialog.intValue() != 0) {
                                num4 = push_user_id4;
                                dialogId2 = dialogId;
                                bundle = null;
                                push_chat_id = isNew;
                                if (AndroidUtilities.isTablet()) {
                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        dialogsActivity = new DialogsActivity(bundle);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    } else {
                                        dialogsActivity = new DialogsActivity(null);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                }
                                launchActivity.actionBarLayout.showLastFragment();
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.layersActionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                }
                                push_user_id.setAction(null);
                                return pushOpened;
                            } else {
                                args = new Bundle();
                                args.putBoolean("destroyAfterSelect", push_chat_id);
                                launchActivity.actionBarLayout.presentFragment(new ContactsActivity(args), z, push_chat_id, push_chat_id);
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                } else {
                                    launchActivity.actionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                }
                                pushOpened = true;
                            }
                            push_chat_id = isNew;
                            bundle = null;
                            if (AndroidUtilities.isTablet()) {
                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                    if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    }
                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                    dialogsActivity = new DialogsActivity(bundle);
                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                }
                            } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                } else {
                                    dialogsActivity = new DialogsActivity(null);
                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                }
                            }
                            launchActivity.actionBarLayout.showLastFragment();
                            if (AndroidUtilities.isTablet()) {
                                launchActivity.layersActionBarLayout.showLastFragment();
                                launchActivity.rightActionBarLayout.showLastFragment();
                            }
                            push_user_id.setAction(null);
                            return pushOpened;
                        }
                    }
                    if (z) {
                        Toast.makeText(launchActivity, "Unsupported content", 0).show();
                    }
                } else {
                    num = push_user_id3;
                    num2 = push_chat_id3;
                    num3 = push_user_id4;
                    dialogId4 = push_msg_id;
                    String originalPath;
                    if (intent.getAction().equals("android.intent.action.SEND_MULTIPLE")) {
                        push_chat_id = false;
                        try {
                            int a2;
                            Uri uri5;
                            ArrayList<Parcelable> uris = intent2.getParcelableArrayListExtra("android.intent.extra.STREAM");
                            push_user_id4 = intent.getType();
                            if (uris != null) {
                                a2 = 0;
                                while (a2 < uris.size()) {
                                    push_msg_id = (Parcelable) uris.get(a2);
                                    if (!(push_msg_id instanceof Uri)) {
                                        push_msg_id = Uri.parse(push_msg_id.toString());
                                    }
                                    uri5 = (Uri) push_msg_id;
                                    if (uri5 != null && AndroidUtilities.isInternalUri(uri5)) {
                                        uris.remove(a2);
                                        a2--;
                                    }
                                    a2++;
                                }
                                if (uris.isEmpty()) {
                                    uris = null;
                                }
                            }
                            if (uris == null) {
                                push_chat_id = true;
                            } else if (push_user_id4 == null || !push_user_id4.startsWith("image/")) {
                                for (a2 = 0; a2 < uris.size(); a2++) {
                                    push_msg_id = (Parcelable) uris.get(a2);
                                    if (!(push_msg_id instanceof Uri)) {
                                        push_msg_id = Uri.parse(push_msg_id.toString());
                                    }
                                    uri5 = (Uri) push_msg_id;
                                    type = AndroidUtilities.getPath(uri5);
                                    originalPath = push_msg_id.toString();
                                    if (originalPath == null) {
                                        originalPath = type;
                                    }
                                    if (type != null) {
                                        if (type.startsWith("file:")) {
                                            type = type.replace("file://", TtmlNode.ANONYMOUS_REGION_ID);
                                        }
                                        if (launchActivity.documentsPathsArray == null) {
                                            launchActivity.documentsPathsArray = new ArrayList();
                                            launchActivity.documentsOriginalPathsArray = new ArrayList();
                                        }
                                        launchActivity.documentsPathsArray.add(type);
                                        launchActivity.documentsOriginalPathsArray.add(originalPath);
                                    } else {
                                        if (launchActivity.documentsUrisArray == null) {
                                            launchActivity.documentsUrisArray = new ArrayList();
                                        }
                                        launchActivity.documentsUrisArray.add(uri5);
                                        launchActivity.documentsMimeType = push_user_id4;
                                    }
                                }
                            } else {
                                for (a2 = 0; a2 < uris.size(); a2++) {
                                    push_msg_id = (Parcelable) uris.get(a2);
                                    if (!(push_msg_id instanceof Uri)) {
                                        push_msg_id = Uri.parse(push_msg_id.toString());
                                    }
                                    uri5 = (Uri) push_msg_id;
                                    if (launchActivity.photoPathsArray == null) {
                                        launchActivity.photoPathsArray = new ArrayList();
                                    }
                                    SendingMediaInfo info = new SendingMediaInfo();
                                    info.uri = uri5;
                                    launchActivity.photoPathsArray.add(info);
                                }
                            }
                        } catch (Throwable e222222222) {
                            FileLog.m3e(e222222222);
                            push_chat_id = true;
                        }
                        if (push_chat_id) {
                            Toast.makeText(launchActivity, "Unsupported content", null).show();
                        }
                    } else {
                        if ("android.intent.action.VIEW".equals(intent.getAction())) {
                            Uri data = intent.getData();
                            if (data != null) {
                                String username;
                                String group;
                                String str;
                                String botUser;
                                String botChat;
                                String game2;
                                String message;
                                String phoneHash;
                                String phone;
                                String str2;
                                Cursor cursor;
                                int accountId;
                                int i2;
                                int i3;
                                final Bundle args4;
                                push_msg_id = null;
                                type = null;
                                originalPath = null;
                                nameEncoding = null;
                                String phoneHash2 = null;
                                String phone2 = null;
                                String[] instantView = null;
                                Integer messageId = null;
                                String scheme = data.getScheme();
                                if (scheme != null) {
                                    username = null;
                                    group = null;
                                    if (scheme.equals("http") != null) {
                                        str = null;
                                        botUser = null;
                                    } else if (scheme.equals("https") != null) {
                                        str = null;
                                        botUser = null;
                                    } else if (scheme.equals("tg") != null) {
                                        String sticker;
                                        subject = data.toString();
                                        if (subject.startsWith("tg:resolve") != null) {
                                            str = null;
                                            botUser = null;
                                        } else if (subject.startsWith("tg://resolve") != null) {
                                            str = null;
                                            botUser = null;
                                        } else {
                                            Uri uri6;
                                            if (subject.startsWith("tg:join") != null) {
                                                str = null;
                                                botUser = null;
                                            } else if (subject.startsWith("tg://join") != null) {
                                                str = null;
                                                botUser = null;
                                            } else {
                                                if (subject.startsWith("tg:addstickers") != null) {
                                                    str = null;
                                                    botUser = null;
                                                } else if (subject.startsWith("tg://addstickers") != null) {
                                                    str = null;
                                                    botUser = null;
                                                } else {
                                                    if (subject.startsWith("tg:msg") != null || subject.startsWith("tg://msg") != null || subject.startsWith("tg://share") != null) {
                                                        str = null;
                                                        botUser = null;
                                                    } else if (subject.startsWith("tg:share") != null) {
                                                        str = null;
                                                        botUser = null;
                                                    } else {
                                                        if (subject.startsWith("tg:confirmphone") != null) {
                                                            str = null;
                                                            botUser = null;
                                                        } else if (subject.startsWith("tg://confirmphone") != null) {
                                                            str = null;
                                                            botUser = null;
                                                        } else {
                                                            if (subject.startsWith("tg:openmessage") == null) {
                                                                if (subject.startsWith("tg://openmessage") == null) {
                                                                    str = null;
                                                                    botUser = null;
                                                                }
                                                            }
                                                            str = null;
                                                            botUser = null;
                                                            data = Uri.parse(subject.replace("tg:openmessage", "tg://telegram.org").replace("tg://openmessage", "tg://telegram.org"));
                                                            push_user_id4 = data.getQueryParameter("user_id");
                                                            sticker = data.getQueryParameter("chat_id");
                                                            param = data.getQueryParameter("message_id");
                                                            if (push_user_id4 != null) {
                                                                try {
                                                                    data = Integer.valueOf(Integer.parseInt(push_user_id4));
                                                                } catch (NumberFormatException e3) {
                                                                }
                                                            } else {
                                                                if (sticker != null) {
                                                                    try {
                                                                        num2 = Integer.valueOf(Integer.parseInt(sticker));
                                                                    } catch (NumberFormatException e4) {
                                                                    }
                                                                }
                                                                data = num;
                                                            }
                                                            if (param != null) {
                                                                num5 = data;
                                                                try {
                                                                    num3 = Integer.valueOf(Integer.parseInt(param));
                                                                } catch (NumberFormatException e5) {
                                                                }
                                                            } else {
                                                                num5 = data;
                                                            }
                                                            botChat = null;
                                                            game2 = null;
                                                            phoneHash2 = null;
                                                            num = null;
                                                            instantView = null;
                                                            phone2 = null;
                                                            if (originalPath == null && originalPath.startsWith("@")) {
                                                                String message2 = new StringBuilder();
                                                                message2.append(" ");
                                                                message2.append(originalPath);
                                                                message = message2.toString();
                                                            } else {
                                                                message = originalPath;
                                                            }
                                                            if (phone2 != null) {
                                                                intentAccount2 = open_new_dialog;
                                                                phoneHash = phoneHash2;
                                                                phone = phone2;
                                                                str2 = scheme;
                                                                dialogId = dialogId4;
                                                                j = 0;
                                                            } else if (phoneHash2 != null) {
                                                                intentAccount2 = open_new_dialog;
                                                                phoneHash = phoneHash2;
                                                                phone = phone2;
                                                                str2 = scheme;
                                                                dialogId = dialogId4;
                                                                j = 0;
                                                            } else {
                                                                if (username == null && group == null && str == null && message == null && game2 == null) {
                                                                    if (instantView == null) {
                                                                        try {
                                                                            cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                                                                            if (cursor != null) {
                                                                                if (cursor.moveToFirst()) {
                                                                                    accountId = Utilities.parseInt(cursor.getString(cursor.getColumnIndex("account_name"))).intValue();
                                                                                    while (push_user_id4 < 3) {
                                                                                        try {
                                                                                            if (UserConfig.getInstance(push_user_id4).getClientUserId() == accountId) {
                                                                                                open_new_dialog[0] = push_user_id4;
                                                                                                try {
                                                                                                    switchToAccount(open_new_dialog[0], true);
                                                                                                    break;
                                                                                                } catch (Exception e6) {
                                                                                                    e222222222 = e6;
                                                                                                }
                                                                                            } else {
                                                                                            }
                                                                                        } catch (Exception e7) {
                                                                                            e222222222 = e7;
                                                                                        }
                                                                                    }
                                                                                    push_user_id4 = cursor.getInt(cursor.getColumnIndex("DATA4"));
                                                                                    NotificationCenter.getInstance(open_new_dialog[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                                                    num5 = Integer.valueOf(push_user_id4);
                                                                                }
                                                                                cursor.close();
                                                                            }
                                                                        } catch (Exception e8) {
                                                                            e222222222 = e8;
                                                                            FileLog.m3e(e222222222);
                                                                            intentAccount2 = open_new_dialog;
                                                                            push_chat_id3 = num2;
                                                                            push_user_id4 = num3;
                                                                            dialogId = dialogId4;
                                                                            push_user_id3 = num5;
                                                                            j = 0;
                                                                            push_chat_id2 = push_chat_id3;
                                                                            push_msg_id = push_user_id4;
                                                                            push_enc_id = push_enc_id2;
                                                                            open_settings = valueOf;
                                                                            open_new_dialog = valueOf2;
                                                                            intentAccount = intentAccount2;
                                                                            push_chat_id = true;
                                                                            z = false;
                                                                            push_user_id4 = push_user_id3;
                                                                            push_user_id = intent;
                                                                            if (push_user_id4.intValue() == 0) {
                                                                                dialogId2 = new Bundle();
                                                                                dialogId2.putInt("user_id", push_user_id4.intValue());
                                                                                if (push_msg_id.intValue() != 0) {
                                                                                    dialogId2.putInt("message_id", push_msg_id.intValue());
                                                                                }
                                                                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(dialogId2), z, push_chat_id, push_chat_id)) {
                                                                                    pushOpened = true;
                                                                                }
                                                                            } else if (push_chat_id2.intValue() == 0) {
                                                                                args = new Bundle();
                                                                                args.putInt("chat_id", push_chat_id2.intValue());
                                                                                if (push_msg_id.intValue() != 0) {
                                                                                    args.putInt("message_id", push_msg_id.intValue());
                                                                                }
                                                                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                                                                    pushOpened = true;
                                                                                }
                                                                            } else if (push_enc_id.intValue() == 0) {
                                                                                args = new Bundle();
                                                                                args.putInt("enc_id", push_enc_id.intValue());
                                                                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                                                                    pushOpened = true;
                                                                                }
                                                                            } else if (!showDialogsList) {
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    launchActivity.actionBarLayout.removeAllFragments();
                                                                                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                                    for (a = z; a < launchActivity.layersActionBarLayout.fragmentsStack.size() - push_chat_id; a = (a - 1) + push_chat_id) {
                                                                                        launchActivity.layersActionBarLayout.removeFragmentFromStack((BaseFragment) launchActivity.layersActionBarLayout.fragmentsStack.get(z));
                                                                                    }
                                                                                    launchActivity.layersActionBarLayout.closeLastFragment(z);
                                                                                }
                                                                                pushOpened = false;
                                                                                num4 = push_user_id4;
                                                                                push_chat_id = false;
                                                                                bundle = null;
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                            dialogsActivity = new DialogsActivity(null);
                                                                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                        } else {
                                                                                            launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                        }
                                                                                    }
                                                                                } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                        dialogsActivity = new DialogsActivity(bundle);
                                                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                    }
                                                                                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                                    launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                }
                                                                                launchActivity.actionBarLayout.showLastFragment();
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    launchActivity.layersActionBarLayout.showLastFragment();
                                                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                                                }
                                                                                push_user_id.setAction(null);
                                                                                return pushOpened;
                                                                            } else if (!showPlayer) {
                                                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new AudioPlayerAlert(launchActivity));
                                                                                }
                                                                                pushOpened = false;
                                                                            } else if (!showLocations) {
                                                                                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new SharingLocationsAlert(launchActivity, /* anonymous class already generated */));
                                                                                }
                                                                                pushOpened = false;
                                                                            } else if (launchActivity.documentsUrisArray != null) {
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    NotificationCenter.getInstance(intentAccount[z]).postNotificationName(NotificationCenter.closeChats, new Object[z]);
                                                                                }
                                                                                dialogId2 = dialogId;
                                                                                if (dialogId2 == j) {
                                                                                    dids = new ArrayList();
                                                                                    dids.add(Long.valueOf(dialogId2));
                                                                                    bundle = null;
                                                                                    didSelectDialogs(null, dids, null, false);
                                                                                    push_chat_id = isNew;
                                                                                    if (AndroidUtilities.isTablet()) {
                                                                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                            if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                                dialogsActivity = new DialogsActivity(null);
                                                                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                            } else {
                                                                                                launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                            }
                                                                                        }
                                                                                    } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                            dialogsActivity = new DialogsActivity(bundle);
                                                                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                        }
                                                                                    } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                    }
                                                                                    launchActivity.actionBarLayout.showLastFragment();
                                                                                    if (AndroidUtilities.isTablet()) {
                                                                                        launchActivity.layersActionBarLayout.showLastFragment();
                                                                                        launchActivity.rightActionBarLayout.showLastFragment();
                                                                                    }
                                                                                    push_user_id.setAction(null);
                                                                                    return pushOpened;
                                                                                }
                                                                                args2 = new Bundle();
                                                                                args2.putBoolean("onlySelect", push_chat_id);
                                                                                args2.putInt("dialogsType", 3);
                                                                                args2.putBoolean("allowSwitchAccount", push_chat_id);
                                                                                if (launchActivity.contactsToSend != null) {
                                                                                    args2.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                                                                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                                                                                } else {
                                                                                    args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                                                                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                                                                                }
                                                                                fragment = new DialogsActivity(args2);
                                                                                fragment.setDelegate(launchActivity);
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    if (launchActivity.actionBarLayout.fragmentsStack.size() <= 1) {
                                                                                    }
                                                                                }
                                                                                num4 = push_user_id4;
                                                                                launchActivity.actionBarLayout.presentFragment(fragment, z, true, true);
                                                                                pushOpened = true;
                                                                                if (!SecretMediaViewer.hasInstance()) {
                                                                                }
                                                                                if (PhotoViewer.hasInstance()) {
                                                                                }
                                                                                if (ArticleViewer.hasInstance()) {
                                                                                }
                                                                                z2 = false;
                                                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    launchActivity.actionBarLayout.showLastFragment();
                                                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                                                } else {
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                }
                                                                                push_chat_id = isNew;
                                                                                bundle = null;
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                        }
                                                                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                        dialogsActivity = new DialogsActivity(bundle);
                                                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                    }
                                                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                    } else {
                                                                                        dialogsActivity = new DialogsActivity(null);
                                                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                    }
                                                                                }
                                                                                launchActivity.actionBarLayout.showLastFragment();
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    launchActivity.layersActionBarLayout.showLastFragment();
                                                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                                                }
                                                                                push_user_id.setAction(null);
                                                                                return pushOpened;
                                                                            } else if (open_settings.intValue() == 0) {
                                                                                launchActivity.actionBarLayout.presentFragment(new SettingsActivity(), z, push_chat_id, push_chat_id);
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                                                                } else {
                                                                                    launchActivity.actionBarLayout.showLastFragment();
                                                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                                                                }
                                                                                pushOpened = true;
                                                                            } else if (open_new_dialog.intValue() != 0) {
                                                                                num4 = push_user_id4;
                                                                                dialogId2 = dialogId;
                                                                                bundle = null;
                                                                                push_chat_id = isNew;
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                        }
                                                                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                        dialogsActivity = new DialogsActivity(bundle);
                                                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                    }
                                                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                    } else {
                                                                                        dialogsActivity = new DialogsActivity(null);
                                                                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                    }
                                                                                }
                                                                                launchActivity.actionBarLayout.showLastFragment();
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    launchActivity.layersActionBarLayout.showLastFragment();
                                                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                                                }
                                                                                push_user_id.setAction(null);
                                                                                return pushOpened;
                                                                            } else {
                                                                                args = new Bundle();
                                                                                args.putBoolean("destroyAfterSelect", push_chat_id);
                                                                                launchActivity.actionBarLayout.presentFragment(new ContactsActivity(args), z, push_chat_id, push_chat_id);
                                                                                if (AndroidUtilities.isTablet()) {
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                                                                } else {
                                                                                    launchActivity.actionBarLayout.showLastFragment();
                                                                                    launchActivity.rightActionBarLayout.showLastFragment();
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                                                                }
                                                                                pushOpened = true;
                                                                            }
                                                                            push_chat_id = isNew;
                                                                            bundle = null;
                                                                            if (AndroidUtilities.isTablet()) {
                                                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                    if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                    }
                                                                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                    dialogsActivity = new DialogsActivity(bundle);
                                                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                }
                                                                            } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                                } else {
                                                                                    dialogsActivity = new DialogsActivity(null);
                                                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                                }
                                                                            }
                                                                            launchActivity.actionBarLayout.showLastFragment();
                                                                            if (AndroidUtilities.isTablet()) {
                                                                                launchActivity.layersActionBarLayout.showLastFragment();
                                                                                launchActivity.rightActionBarLayout.showLastFragment();
                                                                            }
                                                                            push_user_id.setAction(null);
                                                                            return pushOpened;
                                                                        }
                                                                        intentAccount2 = open_new_dialog;
                                                                        push_chat_id3 = num2;
                                                                        push_user_id4 = num3;
                                                                        dialogId = dialogId4;
                                                                        push_user_id3 = num5;
                                                                        j = 0;
                                                                    }
                                                                }
                                                                dialogId = dialogId4;
                                                                j = 0;
                                                                i2 = 1;
                                                                i3 = 3;
                                                                intentAccount2 = open_new_dialog;
                                                                runLinkRequest(open_new_dialog[0], username, group, str, botUser, botChat, message, num, messageId, game2, instantView, null);
                                                                push_chat_id3 = num2;
                                                                push_user_id4 = num3;
                                                                push_user_id3 = num5;
                                                            }
                                                            args4 = new Bundle();
                                                            args4.putString("phone", phone);
                                                            args4.putString("hash", phoneHash);
                                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                                public void run() {
                                                                    LaunchActivity.this.presentFragment(new CancelAccountDeletionActivity(args4));
                                                                }
                                                            });
                                                            push_chat_id3 = num2;
                                                            push_user_id4 = num3;
                                                            push_user_id3 = num5;
                                                        }
                                                        data = Uri.parse(subject.replace("tg:confirmphone", "tg://telegram.org").replace("tg://confirmphone", "tg://telegram.org"));
                                                        nameEncoding = data.getQueryParameter("phone");
                                                        phone2 = data.getQueryParameter("hash");
                                                    }
                                                    data = Uri.parse(subject.replace("tg:msg", "tg://telegram.org").replace("tg://msg", "tg://telegram.org").replace("tg://share", "tg://telegram.org").replace("tg:share", "tg://telegram.org"));
                                                    push_user_id4 = data.getQueryParameter(UpdateFragment.FRAGMENT_URL);
                                                    if (push_user_id4 == null) {
                                                        push_user_id4 = TtmlNode.ANONYMOUS_REGION_ID;
                                                    }
                                                    if (data.getQueryParameter(MimeTypes.BASE_TYPE_TEXT) != null) {
                                                        if (push_user_id4.length() > null) {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(push_user_id4);
                                                            stringBuilder.append("\n");
                                                            push_user_id4 = stringBuilder.toString();
                                                            instantView = true;
                                                        }
                                                        sticker = new StringBuilder();
                                                        sticker.append(push_user_id4);
                                                        sticker.append(data.getQueryParameter(MimeTypes.BASE_TYPE_TEXT));
                                                        push_user_id4 = sticker.toString();
                                                    }
                                                    if (push_user_id4.length() > 16384) {
                                                        sticker = null;
                                                        push_user_id4 = push_user_id4.substring(0, MessagesController.UPDATE_MASK_CHAT_ADMINS);
                                                    } else {
                                                        sticker = null;
                                                    }
                                                    originalPath = push_user_id4;
                                                    while (originalPath.endsWith("\n") != null) {
                                                        originalPath = originalPath.substring(sticker, originalPath.length() - 1);
                                                    }
                                                }
                                                data = Uri.parse(subject.replace("tg:addstickers", "tg://telegram.org").replace("tg://addstickers", "tg://telegram.org"));
                                                uri6 = data;
                                                str = data.getQueryParameter("set");
                                                botChat = type;
                                                game2 = phoneHash2;
                                                phoneHash2 = phone2;
                                                num5 = num;
                                                phone2 = nameEncoding;
                                                num = instantView;
                                                instantView = push_msg_id;
                                                if (originalPath == null) {
                                                }
                                                message = originalPath;
                                                if (phone2 != null) {
                                                    intentAccount2 = open_new_dialog;
                                                    phoneHash = phoneHash2;
                                                    phone = phone2;
                                                    str2 = scheme;
                                                    dialogId = dialogId4;
                                                    j = 0;
                                                } else if (phoneHash2 != null) {
                                                    intentAccount2 = open_new_dialog;
                                                    phoneHash = phoneHash2;
                                                    phone = phone2;
                                                    str2 = scheme;
                                                    dialogId = dialogId4;
                                                    j = 0;
                                                } else if (instantView == null) {
                                                    cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                                                    if (cursor != null) {
                                                        if (cursor.moveToFirst()) {
                                                            accountId = Utilities.parseInt(cursor.getString(cursor.getColumnIndex("account_name"))).intValue();
                                                            for (push_user_id4 = null; push_user_id4 < 3; push_user_id4++) {
                                                                if (UserConfig.getInstance(push_user_id4).getClientUserId() == accountId) {
                                                                    open_new_dialog[0] = push_user_id4;
                                                                    switchToAccount(open_new_dialog[0], true);
                                                                    break;
                                                                }
                                                            }
                                                            push_user_id4 = cursor.getInt(cursor.getColumnIndex("DATA4"));
                                                            NotificationCenter.getInstance(open_new_dialog[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                            num5 = Integer.valueOf(push_user_id4);
                                                        }
                                                        cursor.close();
                                                    }
                                                    intentAccount2 = open_new_dialog;
                                                    push_chat_id3 = num2;
                                                    push_user_id4 = num3;
                                                    dialogId = dialogId4;
                                                    push_user_id3 = num5;
                                                    j = 0;
                                                } else {
                                                    dialogId = dialogId4;
                                                    j = 0;
                                                    i2 = 1;
                                                    i3 = 3;
                                                    intentAccount2 = open_new_dialog;
                                                    runLinkRequest(open_new_dialog[0], username, group, str, botUser, botChat, message, num, messageId, game2, instantView, null);
                                                    push_chat_id3 = num2;
                                                    push_user_id4 = num3;
                                                    push_user_id3 = num5;
                                                }
                                                args4 = new Bundle();
                                                args4.putString("phone", phone);
                                                args4.putString("hash", phoneHash);
                                                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                                push_chat_id3 = num2;
                                                push_user_id4 = num3;
                                                push_user_id3 = num5;
                                            }
                                            data = Uri.parse(subject.replace("tg:join", "tg://telegram.org").replace("tg://join", "tg://telegram.org"));
                                            uri6 = data;
                                            group = data.getQueryParameter("invite");
                                            botChat = type;
                                            game2 = phoneHash2;
                                            phoneHash2 = phone2;
                                            num5 = num;
                                            phone2 = nameEncoding;
                                            num = instantView;
                                            instantView = push_msg_id;
                                            if (originalPath == null) {
                                            }
                                            message = originalPath;
                                            if (phone2 != null) {
                                                intentAccount2 = open_new_dialog;
                                                phoneHash = phoneHash2;
                                                phone = phone2;
                                                str2 = scheme;
                                                dialogId = dialogId4;
                                                j = 0;
                                            } else if (phoneHash2 != null) {
                                                intentAccount2 = open_new_dialog;
                                                phoneHash = phoneHash2;
                                                phone = phone2;
                                                str2 = scheme;
                                                dialogId = dialogId4;
                                                j = 0;
                                            } else if (instantView == null) {
                                                dialogId = dialogId4;
                                                j = 0;
                                                i2 = 1;
                                                i3 = 3;
                                                intentAccount2 = open_new_dialog;
                                                runLinkRequest(open_new_dialog[0], username, group, str, botUser, botChat, message, num, messageId, game2, instantView, null);
                                                push_chat_id3 = num2;
                                                push_user_id4 = num3;
                                                push_user_id3 = num5;
                                            } else {
                                                cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                                                if (cursor != null) {
                                                    if (cursor.moveToFirst()) {
                                                        accountId = Utilities.parseInt(cursor.getString(cursor.getColumnIndex("account_name"))).intValue();
                                                        for (push_user_id4 = null; push_user_id4 < 3; push_user_id4++) {
                                                            if (UserConfig.getInstance(push_user_id4).getClientUserId() == accountId) {
                                                                open_new_dialog[0] = push_user_id4;
                                                                switchToAccount(open_new_dialog[0], true);
                                                                break;
                                                            }
                                                        }
                                                        push_user_id4 = cursor.getInt(cursor.getColumnIndex("DATA4"));
                                                        NotificationCenter.getInstance(open_new_dialog[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                        num5 = Integer.valueOf(push_user_id4);
                                                    }
                                                    cursor.close();
                                                }
                                                intentAccount2 = open_new_dialog;
                                                push_chat_id3 = num2;
                                                push_user_id4 = num3;
                                                dialogId = dialogId4;
                                                push_user_id3 = num5;
                                                j = 0;
                                            }
                                            args4 = new Bundle();
                                            args4.putString("phone", phone);
                                            args4.putString("hash", phoneHash);
                                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                            push_chat_id3 = num2;
                                            push_user_id4 = num3;
                                            push_user_id3 = num5;
                                        }
                                        data = Uri.parse(subject.replace("tg:resolve", "tg://telegram.org").replace("tg://resolve", "tg://telegram.org"));
                                        push_user_id4 = data.getQueryParameter("domain");
                                        sticker = data.getQueryParameter(TtmlNode.START);
                                        param = data.getQueryParameter("startgroup");
                                        type = data.getQueryParameter("game");
                                        phoneHash2 = Utilities.parseInt(data.getQueryParameter("post"));
                                        if (phoneHash2.intValue() == 0) {
                                            phoneHash2 = null;
                                        }
                                        username = push_user_id4;
                                        botUser = sticker;
                                        botChat = param;
                                        game2 = type;
                                        messageId = phoneHash2;
                                        phoneHash2 = phone2;
                                        num5 = num;
                                        phone2 = nameEncoding;
                                        num = instantView;
                                        instantView = push_msg_id;
                                        if (originalPath == null) {
                                        }
                                        message = originalPath;
                                        if (phone2 != null) {
                                            intentAccount2 = open_new_dialog;
                                            phoneHash = phoneHash2;
                                            phone = phone2;
                                            str2 = scheme;
                                            dialogId = dialogId4;
                                            j = 0;
                                        } else if (phoneHash2 != null) {
                                            intentAccount2 = open_new_dialog;
                                            phoneHash = phoneHash2;
                                            phone = phone2;
                                            str2 = scheme;
                                            dialogId = dialogId4;
                                            j = 0;
                                        } else if (instantView == null) {
                                            cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                                            if (cursor != null) {
                                                if (cursor.moveToFirst()) {
                                                    accountId = Utilities.parseInt(cursor.getString(cursor.getColumnIndex("account_name"))).intValue();
                                                    for (push_user_id4 = null; push_user_id4 < 3; push_user_id4++) {
                                                        if (UserConfig.getInstance(push_user_id4).getClientUserId() == accountId) {
                                                            open_new_dialog[0] = push_user_id4;
                                                            switchToAccount(open_new_dialog[0], true);
                                                            break;
                                                        }
                                                    }
                                                    push_user_id4 = cursor.getInt(cursor.getColumnIndex("DATA4"));
                                                    NotificationCenter.getInstance(open_new_dialog[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                    num5 = Integer.valueOf(push_user_id4);
                                                }
                                                cursor.close();
                                            }
                                            intentAccount2 = open_new_dialog;
                                            push_chat_id3 = num2;
                                            push_user_id4 = num3;
                                            dialogId = dialogId4;
                                            push_user_id3 = num5;
                                            j = 0;
                                        } else {
                                            dialogId = dialogId4;
                                            j = 0;
                                            i2 = 1;
                                            i3 = 3;
                                            intentAccount2 = open_new_dialog;
                                            runLinkRequest(open_new_dialog[0], username, group, str, botUser, botChat, message, num, messageId, game2, instantView, null);
                                            push_chat_id3 = num2;
                                            push_user_id4 = num3;
                                            push_user_id3 = num5;
                                        }
                                        args4 = new Bundle();
                                        args4.putString("phone", phone);
                                        args4.putString("hash", phoneHash);
                                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                        push_chat_id3 = num2;
                                        push_user_id4 = num3;
                                        push_user_id3 = num5;
                                    } else {
                                        str = null;
                                        botUser = null;
                                    }
                                    subject = data.getHost().toLowerCase();
                                    if (!(subject.equals("telegram.me") == null && subject.equals("t.me") == null && subject.equals("telegram.dog") == null && subject.equals("telesco.pe") == null)) {
                                        push_user_id4 = data.getPath();
                                        if (push_user_id4 != null && push_user_id4.length() > 1) {
                                            push_user_id4 = push_user_id4.substring(1);
                                            if (push_user_id4.startsWith("joinchat/")) {
                                                group = push_user_id4.replace("joinchat/", TtmlNode.ANONYMOUS_REGION_ID);
                                            } else if (push_user_id4.startsWith("addstickers/")) {
                                                str = push_user_id4.replace("addstickers/", TtmlNode.ANONYMOUS_REGION_ID);
                                            } else if (push_user_id4.startsWith("iv/")) {
                                                null[0] = data.getQueryParameter(UpdateFragment.FRAGMENT_URL);
                                                null[1] = data.getQueryParameter("rhash");
                                                if (TextUtils.isEmpty(null[0]) || TextUtils.isEmpty(null[1])) {
                                                    push_msg_id = null;
                                                }
                                            } else {
                                                String str3;
                                                Integer num7;
                                                if (push_user_id4.startsWith("msg/")) {
                                                    str3 = subject;
                                                    num7 = push_user_id4;
                                                } else if (push_user_id4.startsWith("share/")) {
                                                    str3 = subject;
                                                    num7 = push_user_id4;
                                                } else if (push_user_id4.startsWith("confirmphone")) {
                                                    nameEncoding = data.getQueryParameter("phone");
                                                    phone2 = data.getQueryParameter("hash");
                                                } else if (push_user_id4.length() >= 1) {
                                                    List<String> segments = data.getPathSegments();
                                                    if (segments.size() > 0) {
                                                        param = (String) segments.get(0);
                                                        str3 = subject;
                                                        num7 = push_user_id4;
                                                        if (segments.size() > 1) {
                                                            subject = Utilities.parseInt((String) segments.get(1));
                                                            if (subject.intValue() == null) {
                                                                subject = null;
                                                            }
                                                            messageId = subject;
                                                        }
                                                        subject = param;
                                                    } else {
                                                        str3 = subject;
                                                        num7 = push_user_id4;
                                                        subject = username;
                                                    }
                                                    param = data.getQueryParameter(TtmlNode.START);
                                                    type = data.getQueryParameter("startgroup");
                                                    phoneHash2 = data.getQueryParameter("game");
                                                    username = subject;
                                                    botUser = param;
                                                }
                                                subject = data.getQueryParameter(UpdateFragment.FRAGMENT_URL);
                                                if (subject == null) {
                                                    subject = TtmlNode.ANONYMOUS_REGION_ID;
                                                }
                                                if (data.getQueryParameter(MimeTypes.BASE_TYPE_TEXT) != null) {
                                                    if (subject.length() > null) {
                                                        instantView = true;
                                                        push_user_id4 = new StringBuilder();
                                                        push_user_id4.append(subject);
                                                        push_user_id4.append("\n");
                                                        subject = push_user_id4.toString();
                                                    }
                                                    push_user_id4 = new StringBuilder();
                                                    push_user_id4.append(subject);
                                                    push_user_id4.append(data.getQueryParameter(MimeTypes.BASE_TYPE_TEXT));
                                                    subject = push_user_id4.toString();
                                                }
                                                if (subject.length() > 16384) {
                                                    push_user_id4 = null;
                                                    subject = subject.substring(0, MessagesController.UPDATE_MASK_CHAT_ADMINS);
                                                } else {
                                                    push_user_id4 = null;
                                                }
                                                originalPath = subject;
                                                while (originalPath.endsWith("\n") != null) {
                                                    originalPath = originalPath.substring(push_user_id4, originalPath.length() - 1);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    username = null;
                                    group = null;
                                    str = null;
                                    botUser = null;
                                }
                                botChat = type;
                                game2 = phoneHash2;
                                phoneHash2 = phone2;
                                num5 = num;
                                phone2 = nameEncoding;
                                num = instantView;
                                instantView = push_msg_id;
                                if (originalPath == null) {
                                }
                                message = originalPath;
                                if (phone2 != null) {
                                    intentAccount2 = open_new_dialog;
                                    phoneHash = phoneHash2;
                                    phone = phone2;
                                    str2 = scheme;
                                    dialogId = dialogId4;
                                    j = 0;
                                } else if (phoneHash2 != null) {
                                    intentAccount2 = open_new_dialog;
                                    phoneHash = phoneHash2;
                                    phone = phone2;
                                    str2 = scheme;
                                    dialogId = dialogId4;
                                    j = 0;
                                } else if (instantView == null) {
                                    dialogId = dialogId4;
                                    j = 0;
                                    i2 = 1;
                                    i3 = 3;
                                    intentAccount2 = open_new_dialog;
                                    runLinkRequest(open_new_dialog[0], username, group, str, botUser, botChat, message, num, messageId, game2, instantView, null);
                                    push_chat_id3 = num2;
                                    push_user_id4 = num3;
                                    push_user_id3 = num5;
                                } else {
                                    cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                                    if (cursor != null) {
                                        if (cursor.moveToFirst()) {
                                            accountId = Utilities.parseInt(cursor.getString(cursor.getColumnIndex("account_name"))).intValue();
                                            for (push_user_id4 = null; push_user_id4 < 3; push_user_id4++) {
                                                if (UserConfig.getInstance(push_user_id4).getClientUserId() == accountId) {
                                                    open_new_dialog[0] = push_user_id4;
                                                    switchToAccount(open_new_dialog[0], true);
                                                    break;
                                                }
                                            }
                                            push_user_id4 = cursor.getInt(cursor.getColumnIndex("DATA4"));
                                            NotificationCenter.getInstance(open_new_dialog[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                            num5 = Integer.valueOf(push_user_id4);
                                        }
                                        cursor.close();
                                    }
                                    intentAccount2 = open_new_dialog;
                                    push_chat_id3 = num2;
                                    push_user_id4 = num3;
                                    dialogId = dialogId4;
                                    push_user_id3 = num5;
                                    j = 0;
                                }
                                args4 = new Bundle();
                                args4.putString("phone", phone);
                                args4.putString("hash", phoneHash);
                                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                push_chat_id3 = num2;
                                push_user_id4 = num3;
                                push_user_id3 = num5;
                            } else {
                                intentAccount2 = open_new_dialog;
                                dialogId = dialogId4;
                                j = 0;
                                push_user_id3 = num;
                                push_chat_id3 = num2;
                                push_user_id4 = num3;
                            }
                            push_chat_id2 = push_chat_id3;
                            push_msg_id = push_user_id4;
                            push_enc_id = push_enc_id2;
                            open_settings = valueOf;
                            open_new_dialog = valueOf2;
                            intentAccount = intentAccount2;
                            push_chat_id = true;
                            z = false;
                            push_user_id4 = push_user_id3;
                            push_user_id = intent;
                        } else {
                            intentAccount2 = open_new_dialog;
                            dialogId = dialogId4;
                            j = 0;
                            push_user_id = intent;
                            if (intent.getAction().equals("org.telegram.messenger.OPEN_ACCOUNT")) {
                                push_chat_id = true;
                                valueOf = Integer.valueOf(1);
                            } else {
                                push_chat_id = true;
                                if (intent.getAction().equals("new_dialog") != null) {
                                    valueOf2 = Integer.valueOf(1);
                                } else if (intent.getAction().startsWith("com.tmessages.openchat") != null) {
                                    z = false;
                                    push_user_id4 = push_user_id.getIntExtra("chatId", 0);
                                    push_msg_id = push_user_id.getIntExtra("userId", 0);
                                    push_chat_id2 = push_user_id.getIntExtra("encId", 0);
                                    if (push_user_id4 != null) {
                                        intentAccount = intentAccount2;
                                        NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                        num2 = Integer.valueOf(push_user_id4);
                                    } else {
                                        intentAccount = intentAccount2;
                                        if (push_msg_id != null) {
                                            NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                            num = Integer.valueOf(push_msg_id);
                                        } else if (push_chat_id2 != null) {
                                            NotificationCenter.getInstance(intentAccount[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                            push_enc_id2 = Integer.valueOf(push_chat_id2);
                                        } else {
                                            showDialogsList = true;
                                        }
                                    }
                                } else {
                                    intentAccount = intentAccount2;
                                    z = false;
                                    if (intent.getAction().equals("com.tmessages.openplayer") != null) {
                                        showPlayer = true;
                                    } else if (intent.getAction().equals("org.tmessages.openlocations") != null) {
                                        showLocations = true;
                                    }
                                }
                            }
                            push_enc_id = push_enc_id2;
                            open_settings = valueOf;
                            open_new_dialog = valueOf2;
                            push_user_id4 = num;
                            push_chat_id2 = num2;
                            push_msg_id = num3;
                            intentAccount = intentAccount2;
                            z = false;
                        }
                        if (push_user_id4.intValue() == 0) {
                            dialogId2 = new Bundle();
                            dialogId2.putInt("user_id", push_user_id4.intValue());
                            if (push_msg_id.intValue() != 0) {
                                dialogId2.putInt("message_id", push_msg_id.intValue());
                            }
                            if (mainFragmentsStack.isEmpty() || MessagesController.getInstance(intentAccount[z]).checkCanOpenChat(dialogId2, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - push_chat_id))) {
                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(dialogId2), z, push_chat_id, push_chat_id)) {
                                    pushOpened = true;
                                }
                            }
                        } else if (push_chat_id2.intValue() == 0) {
                            args = new Bundle();
                            args.putInt("chat_id", push_chat_id2.intValue());
                            if (push_msg_id.intValue() != 0) {
                                args.putInt("message_id", push_msg_id.intValue());
                            }
                            if (mainFragmentsStack.isEmpty() || MessagesController.getInstance(intentAccount[z]).checkCanOpenChat(args, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - push_chat_id))) {
                                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                    pushOpened = true;
                                }
                            }
                        } else if (push_enc_id.intValue() == 0) {
                            args = new Bundle();
                            args.putInt("enc_id", push_enc_id.intValue());
                            if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                                pushOpened = true;
                            }
                        } else if (!showDialogsList) {
                            if (AndroidUtilities.isTablet()) {
                                launchActivity.actionBarLayout.removeAllFragments();
                            } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                for (a = z; a < launchActivity.layersActionBarLayout.fragmentsStack.size() - push_chat_id; a = (a - 1) + push_chat_id) {
                                    launchActivity.layersActionBarLayout.removeFragmentFromStack((BaseFragment) launchActivity.layersActionBarLayout.fragmentsStack.get(z));
                                }
                                launchActivity.layersActionBarLayout.closeLastFragment(z);
                            }
                            pushOpened = false;
                            num4 = push_user_id4;
                            push_chat_id = false;
                            bundle = null;
                            if (!(pushOpened || isNew)) {
                                if (AndroidUtilities.isTablet()) {
                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        dialogsActivity = new DialogsActivity(bundle);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    } else {
                                        dialogsActivity = new DialogsActivity(null);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                }
                                launchActivity.actionBarLayout.showLastFragment();
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.layersActionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                }
                            }
                            push_user_id.setAction(null);
                            return pushOpened;
                        } else if (!showPlayer) {
                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new AudioPlayerAlert(launchActivity));
                            }
                            pushOpened = false;
                        } else if (!showLocations) {
                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new SharingLocationsAlert(launchActivity, /* anonymous class already generated */));
                            }
                            pushOpened = false;
                        } else {
                            if (launchActivity.videoPath == null && launchActivity.photoPathsArray == null && launchActivity.sendingText == null && launchActivity.documentsPathsArray == null && launchActivity.contactsToSend == null) {
                                if (launchActivity.documentsUrisArray != null) {
                                    if (open_settings.intValue() == 0) {
                                        launchActivity.actionBarLayout.presentFragment(new SettingsActivity(), z, push_chat_id, push_chat_id);
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.actionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                        } else {
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                        }
                                        pushOpened = true;
                                    } else if (open_new_dialog.intValue() != 0) {
                                        args = new Bundle();
                                        args.putBoolean("destroyAfterSelect", push_chat_id);
                                        launchActivity.actionBarLayout.presentFragment(new ContactsActivity(args), z, push_chat_id, push_chat_id);
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.actionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                                        } else {
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                                        }
                                        pushOpened = true;
                                    } else {
                                        num4 = push_user_id4;
                                        dialogId2 = dialogId;
                                        bundle = null;
                                        push_chat_id = isNew;
                                        if (AndroidUtilities.isTablet()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                                    dialogsActivity = new DialogsActivity(null);
                                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                } else {
                                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                }
                                            }
                                        } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                                dialogsActivity = new DialogsActivity(bundle);
                                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                            }
                                        } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                        launchActivity.actionBarLayout.showLastFragment();
                                        if (AndroidUtilities.isTablet()) {
                                            launchActivity.layersActionBarLayout.showLastFragment();
                                            launchActivity.rightActionBarLayout.showLastFragment();
                                        }
                                        push_user_id.setAction(null);
                                        return pushOpened;
                                    }
                                }
                            }
                            if (AndroidUtilities.isTablet()) {
                                NotificationCenter.getInstance(intentAccount[z]).postNotificationName(NotificationCenter.closeChats, new Object[z]);
                            }
                            dialogId2 = dialogId;
                            if (dialogId2 == j) {
                                boolean z6;
                                args2 = new Bundle();
                                args2.putBoolean("onlySelect", push_chat_id);
                                args2.putInt("dialogsType", 3);
                                args2.putBoolean("allowSwitchAccount", push_chat_id);
                                if (launchActivity.contactsToSend != null) {
                                    args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                                } else {
                                    args2.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                                }
                                fragment = new DialogsActivity(args2);
                                fragment.setDelegate(launchActivity);
                                z = AndroidUtilities.isTablet() ? launchActivity.layersActionBarLayout.fragmentsStack.size() <= 0 && (launchActivity.layersActionBarLayout.fragmentsStack.get(launchActivity.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity) : launchActivity.actionBarLayout.fragmentsStack.size() <= 1 && (launchActivity.actionBarLayout.fragmentsStack.get(launchActivity.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
                                num4 = push_user_id4;
                                launchActivity.actionBarLayout.presentFragment(fragment, z, true, true);
                                pushOpened = true;
                                if (!SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                                    z6 = false;
                                    SecretMediaViewer.getInstance().closePhoto(false, false);
                                } else if (PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) {
                                    if (ArticleViewer.hasInstance() || !ArticleViewer.getInstance().isVisible()) {
                                        z2 = false;
                                    } else {
                                        z2 = false;
                                        ArticleViewer.getInstance().close(false, true);
                                    }
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                                    if (AndroidUtilities.isTablet()) {
                                        launchActivity.actionBarLayout.showLastFragment();
                                        launchActivity.rightActionBarLayout.showLastFragment();
                                    } else {
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                    push_chat_id = isNew;
                                    bundle = null;
                                    if (AndroidUtilities.isTablet()) {
                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                            }
                                        } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                            dialogsActivity = new DialogsActivity(bundle);
                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                        }
                                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        } else {
                                            dialogsActivity = new DialogsActivity(null);
                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                        }
                                    }
                                    launchActivity.actionBarLayout.showLastFragment();
                                    if (AndroidUtilities.isTablet()) {
                                        launchActivity.layersActionBarLayout.showLastFragment();
                                        launchActivity.rightActionBarLayout.showLastFragment();
                                    }
                                    push_user_id.setAction(null);
                                    return pushOpened;
                                } else {
                                    DialogsActivity dialogsActivity2 = fragment;
                                    z6 = false;
                                    PhotoViewer.getInstance().closePhoto(false, true);
                                }
                                z2 = z6;
                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                } else {
                                    launchActivity.actionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                }
                                push_chat_id = isNew;
                                bundle = null;
                                if (AndroidUtilities.isTablet()) {
                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                            dialogsActivity = new DialogsActivity(null);
                                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                        } else {
                                            launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                        }
                                    }
                                } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                        dialogsActivity = new DialogsActivity(bundle);
                                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                    }
                                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                    launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                }
                                launchActivity.actionBarLayout.showLastFragment();
                                if (AndroidUtilities.isTablet()) {
                                    launchActivity.layersActionBarLayout.showLastFragment();
                                    launchActivity.rightActionBarLayout.showLastFragment();
                                }
                                push_user_id.setAction(null);
                                return pushOpened;
                            }
                            dids = new ArrayList();
                            dids.add(Long.valueOf(dialogId2));
                            bundle = null;
                            didSelectDialogs(null, dids, null, false);
                            push_chat_id = isNew;
                            if (AndroidUtilities.isTablet()) {
                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                    if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                    }
                                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                    dialogsActivity = new DialogsActivity(bundle);
                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                }
                            } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                } else {
                                    dialogsActivity = new DialogsActivity(null);
                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                }
                            }
                            launchActivity.actionBarLayout.showLastFragment();
                            if (AndroidUtilities.isTablet()) {
                                launchActivity.layersActionBarLayout.showLastFragment();
                                launchActivity.rightActionBarLayout.showLastFragment();
                            }
                            push_user_id.setAction(null);
                            return pushOpened;
                        }
                        push_chat_id = isNew;
                        bundle = null;
                        if (AndroidUtilities.isTablet()) {
                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                    dialogsActivity = new DialogsActivity(null);
                                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                } else {
                                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                }
                            }
                        } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                            if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                                dialogsActivity = new DialogsActivity(bundle);
                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                            }
                        } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        }
                        launchActivity.actionBarLayout.showLastFragment();
                        if (AndroidUtilities.isTablet()) {
                            launchActivity.layersActionBarLayout.showLastFragment();
                            launchActivity.rightActionBarLayout.showLastFragment();
                        }
                        push_user_id.setAction(null);
                        return pushOpened;
                    }
                }
                intentAccount = open_new_dialog;
                push_user_id = intent2;
                dialogId = dialogId4;
                push_chat_id = true;
                z = false;
                j = 0;
            }
            push_enc_id = push_enc_id2;
            open_settings = valueOf;
            open_new_dialog = valueOf2;
            push_user_id4 = num;
            push_chat_id2 = num2;
            push_msg_id = num3;
            if (push_user_id4.intValue() == 0) {
                dialogId2 = new Bundle();
                dialogId2.putInt("user_id", push_user_id4.intValue());
                if (push_msg_id.intValue() != 0) {
                    dialogId2.putInt("message_id", push_msg_id.intValue());
                }
                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(dialogId2), z, push_chat_id, push_chat_id)) {
                    pushOpened = true;
                }
            } else if (push_chat_id2.intValue() == 0) {
                args = new Bundle();
                args.putInt("chat_id", push_chat_id2.intValue());
                if (push_msg_id.intValue() != 0) {
                    args.putInt("message_id", push_msg_id.intValue());
                }
                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                    pushOpened = true;
                }
            } else if (push_enc_id.intValue() == 0) {
                args = new Bundle();
                args.putInt("enc_id", push_enc_id.intValue());
                if (launchActivity.actionBarLayout.presentFragment(new ChatActivity(args), z, push_chat_id, push_chat_id)) {
                    pushOpened = true;
                }
            } else if (!showDialogsList) {
                if (AndroidUtilities.isTablet()) {
                    launchActivity.actionBarLayout.removeAllFragments();
                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    for (a = z; a < launchActivity.layersActionBarLayout.fragmentsStack.size() - push_chat_id; a = (a - 1) + push_chat_id) {
                        launchActivity.layersActionBarLayout.removeFragmentFromStack((BaseFragment) launchActivity.layersActionBarLayout.fragmentsStack.get(z));
                    }
                    launchActivity.layersActionBarLayout.closeLastFragment(z);
                }
                pushOpened = false;
                num4 = push_user_id4;
                push_chat_id = false;
                bundle = null;
                if (AndroidUtilities.isTablet()) {
                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                        if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                            dialogsActivity = new DialogsActivity(null);
                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        } else {
                            launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        }
                    }
                } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                    if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                        dialogsActivity = new DialogsActivity(bundle);
                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                }
                launchActivity.actionBarLayout.showLastFragment();
                if (AndroidUtilities.isTablet()) {
                    launchActivity.layersActionBarLayout.showLastFragment();
                    launchActivity.rightActionBarLayout.showLastFragment();
                }
                push_user_id.setAction(null);
                return pushOpened;
            } else if (!showPlayer) {
                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new AudioPlayerAlert(launchActivity));
                }
                pushOpened = false;
            } else if (!showLocations) {
                if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                    ((BaseFragment) launchActivity.actionBarLayout.fragmentsStack.get(z)).showDialog(new SharingLocationsAlert(launchActivity, /* anonymous class already generated */));
                }
                pushOpened = false;
            } else if (launchActivity.documentsUrisArray != null) {
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(intentAccount[z]).postNotificationName(NotificationCenter.closeChats, new Object[z]);
                }
                dialogId2 = dialogId;
                if (dialogId2 == j) {
                    dids = new ArrayList();
                    dids.add(Long.valueOf(dialogId2));
                    bundle = null;
                    didSelectDialogs(null, dids, null, false);
                    push_chat_id = isNew;
                    if (AndroidUtilities.isTablet()) {
                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                            if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                                dialogsActivity = new DialogsActivity(null);
                                dialogsActivity.setSideMenu(launchActivity.sideMenu);
                                launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                            } else {
                                launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                            }
                        }
                    } else if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                        if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                            dialogsActivity = new DialogsActivity(bundle);
                            dialogsActivity.setSideMenu(launchActivity.sideMenu);
                            launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    } else if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    }
                    launchActivity.actionBarLayout.showLastFragment();
                    if (AndroidUtilities.isTablet()) {
                        launchActivity.layersActionBarLayout.showLastFragment();
                        launchActivity.rightActionBarLayout.showLastFragment();
                    }
                    push_user_id.setAction(null);
                    return pushOpened;
                }
                args2 = new Bundle();
                args2.putBoolean("onlySelect", push_chat_id);
                args2.putInt("dialogsType", 3);
                args2.putBoolean("allowSwitchAccount", push_chat_id);
                if (launchActivity.contactsToSend != null) {
                    args2.putString("selectAlertString", LocaleController.getString("SendMessagesTo", R.string.SendMessagesTo));
                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", R.string.SendMessagesToGroup));
                } else {
                    args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendMessagesTo));
                    args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                }
                fragment = new DialogsActivity(args2);
                fragment.setDelegate(launchActivity);
                if (AndroidUtilities.isTablet()) {
                    if (launchActivity.actionBarLayout.fragmentsStack.size() <= 1) {
                    }
                }
                num4 = push_user_id4;
                launchActivity.actionBarLayout.presentFragment(fragment, z, true, true);
                pushOpened = true;
                if (!SecretMediaViewer.hasInstance()) {
                }
                if (PhotoViewer.hasInstance()) {
                }
                if (ArticleViewer.hasInstance()) {
                }
                z2 = false;
                launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z2, z2);
                if (AndroidUtilities.isTablet()) {
                    launchActivity.actionBarLayout.showLastFragment();
                    launchActivity.rightActionBarLayout.showLastFragment();
                } else {
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                }
                push_chat_id = isNew;
                bundle = null;
                if (AndroidUtilities.isTablet()) {
                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        }
                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                        dialogsActivity = new DialogsActivity(bundle);
                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    } else {
                        dialogsActivity = new DialogsActivity(null);
                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
                launchActivity.actionBarLayout.showLastFragment();
                if (AndroidUtilities.isTablet()) {
                    launchActivity.layersActionBarLayout.showLastFragment();
                    launchActivity.rightActionBarLayout.showLastFragment();
                }
                push_user_id.setAction(null);
                return pushOpened;
            } else if (open_settings.intValue() == 0) {
                launchActivity.actionBarLayout.presentFragment(new SettingsActivity(), z, push_chat_id, push_chat_id);
                if (AndroidUtilities.isTablet()) {
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                } else {
                    launchActivity.actionBarLayout.showLastFragment();
                    launchActivity.rightActionBarLayout.showLastFragment();
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                }
                pushOpened = true;
            } else if (open_new_dialog.intValue() != 0) {
                num4 = push_user_id4;
                dialogId2 = dialogId;
                bundle = null;
                push_chat_id = isNew;
                if (AndroidUtilities.isTablet()) {
                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                        if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                            launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                            launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        }
                    } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                        dialogsActivity = new DialogsActivity(bundle);
                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                    if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                        launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    } else {
                        dialogsActivity = new DialogsActivity(null);
                        dialogsActivity.setSideMenu(launchActivity.sideMenu);
                        launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
                launchActivity.actionBarLayout.showLastFragment();
                if (AndroidUtilities.isTablet()) {
                    launchActivity.layersActionBarLayout.showLastFragment();
                    launchActivity.rightActionBarLayout.showLastFragment();
                }
                push_user_id.setAction(null);
                return pushOpened;
            } else {
                args = new Bundle();
                args.putBoolean("destroyAfterSelect", push_chat_id);
                launchActivity.actionBarLayout.presentFragment(new ContactsActivity(args), z, push_chat_id, push_chat_id);
                if (AndroidUtilities.isTablet()) {
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(push_chat_id, z);
                } else {
                    launchActivity.actionBarLayout.showLastFragment();
                    launchActivity.rightActionBarLayout.showLastFragment();
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(z, z);
                }
                pushOpened = true;
            }
            push_chat_id = isNew;
            bundle = null;
            if (AndroidUtilities.isTablet()) {
                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                    if (launchActivity.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        launchActivity.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                        launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    }
                } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                    dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                }
            } else if (launchActivity.actionBarLayout.fragmentsStack.isEmpty()) {
                if (UserConfig.getInstance(launchActivity.currentAccount).isClientActivated()) {
                    launchActivity.actionBarLayout.addFragmentToStack(new LoginActivity());
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                } else {
                    dialogsActivity = new DialogsActivity(null);
                    dialogsActivity.setSideMenu(launchActivity.sideMenu);
                    launchActivity.actionBarLayout.addFragmentToStack(dialogsActivity);
                    launchActivity.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                }
            }
            launchActivity.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                launchActivity.layersActionBarLayout.showLastFragment();
                launchActivity.rightActionBarLayout.showLastFragment();
            }
            push_user_id.setAction(null);
            return pushOpened;
        }
        showPasscodeActivity();
        launchActivity.passcodeSaveIntent = intent2;
        launchActivity.passcodeSaveIntentIsNew = isNew;
        launchActivity.passcodeSaveIntentIsRestore = z3;
        UserConfig.getInstance(launchActivity.currentAccount).saveConfig(false);
        return false;
    }

    private void runLinkRequest(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, String game, String[] instantView, int state) {
        int i;
        LaunchActivity launchActivity;
        AlertDialog progressDialog;
        int i2 = intentAccount;
        String str = username;
        String str2 = group;
        String str3 = sticker;
        String str4 = message;
        int i3 = state;
        AlertDialog progressDialog2 = new AlertDialog(this, 1);
        progressDialog2.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog2.setCanceledOnTouchOutside(false);
        progressDialog2.setCancelable(false);
        int requestId = 0;
        final AlertDialog alertDialog;
        final String str5;
        final String str6;
        AlertDialog progressDialog3;
        boolean z;
        int i4;
        String str7;
        String str8;
        if (str != null) {
            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
            req.username = str;
            alertDialog = progressDialog2;
            final String str9 = game;
            C21739 c21739 = r1;
            final int i5 = i2;
            ConnectionsManager instance = ConnectionsManager.getInstance(intentAccount);
            str5 = botChat;
            TL_contacts_resolveUsername req2 = req;
            str6 = botUser;
            progressDialog3 = progressDialog2;
            final Integer progressDialog4 = messageId;
            C21739 c217392 = new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (!LaunchActivity.this.isFinishing()) {
                                try {
                                    alertDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                final TL_contacts_resolvedPeer res = response;
                                if (error != null || LaunchActivity.this.actionBarLayout == null || (str9 != null && (str9 == null || res.users.isEmpty()))) {
                                    try {
                                        Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound), 0).show();
                                    } catch (Throwable e2) {
                                        FileLog.m3e(e2);
                                    }
                                } else {
                                    MessagesController.getInstance(i5).putUsers(res.users, false);
                                    MessagesController.getInstance(i5).putChats(res.chats, false);
                                    MessagesStorage.getInstance(i5).putUsersAndChats(res.users, res.chats, false, true);
                                    if (str9 != null) {
                                        Bundle args = new Bundle();
                                        args.putBoolean("onlySelect", true);
                                        args.putBoolean("cantSendToChannels", true);
                                        args.putInt("dialogsType", 1);
                                        args.putString("selectAlertString", LocaleController.getString("SendGameTo", R.string.SendGameTo));
                                        args.putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", R.string.SendGameToGroup));
                                        DialogsActivity fragment = new DialogsActivity(args);
                                        fragment.setDelegate(new DialogsActivityDelegate() {
                                            public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                                long did = ((Long) dids.get(0)).longValue();
                                                TL_inputMediaGame inputMediaGame = new TL_inputMediaGame();
                                                inputMediaGame.id = new TL_inputGameShortName();
                                                inputMediaGame.id.short_name = str9;
                                                inputMediaGame.id.bot_id = MessagesController.getInstance(i5).getInputUser((User) res.users.get(0));
                                                SendMessagesHelper.getInstance(i5).sendGame(MessagesController.getInstance(i5).getInputPeer((int) did), inputMediaGame, 0, 0);
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
                                                if (MessagesController.getInstance(i5).checkCanOpenChat(args, fragment)) {
                                                    NotificationCenter.getInstance(i5).postNotificationName(NotificationCenter.closeChats, new Object[0]);
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
                                        } else {
                                            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                        }
                                    } else {
                                        BaseFragment lastFragment = null;
                                        if (str5 != null) {
                                            if (!res.users.isEmpty()) {
                                                lastFragment = (User) res.users.get(0);
                                            }
                                            final BaseFragment user = lastFragment;
                                            if (user != null) {
                                                if (!user.bot || !user.bot_nochats) {
                                                    Bundle args2 = new Bundle();
                                                    args2.putBoolean("onlySelect", true);
                                                    args2.putInt("dialogsType", 2);
                                                    args2.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                                                    DialogsActivity fragment2 = new DialogsActivity(args2);
                                                    fragment2.setDelegate(new DialogsActivityDelegate() {
                                                        public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                                            C21722 c21722 = this;
                                                            long did = ((Long) dids.get(0)).longValue();
                                                            Bundle args = new Bundle();
                                                            args.putBoolean("scrollToTopOnResume", true);
                                                            args.putInt("chat_id", -((int) did));
                                                            if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.getInstance(i5).checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                                                NotificationCenter.getInstance(i5).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                                MessagesController.getInstance(i5).addUserToChat(-((int) did), user, null, 0, str5, null);
                                                                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(args), true, false, true);
                                                            }
                                                        }
                                                    });
                                                    LaunchActivity.this.presentFragment(fragment2);
                                                }
                                            }
                                            try {
                                                Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups), 0).show();
                                            } catch (Throwable e3) {
                                                FileLog.m3e(e3);
                                            }
                                            return;
                                        }
                                        boolean isBot = false;
                                        Bundle args3 = new Bundle();
                                        long dialog_id;
                                        if (res.chats.isEmpty()) {
                                            args3.putInt("user_id", ((User) res.users.get(0)).id);
                                            dialog_id = (long) ((User) res.users.get(0)).id;
                                        } else {
                                            args3.putInt("chat_id", ((Chat) res.chats.get(0)).id);
                                            dialog_id = (long) (-((Chat) res.chats.get(0)).id);
                                        }
                                        if (str6 != null && res.users.size() > 0 && ((User) res.users.get(0)).bot) {
                                            args3.putString("botUser", str6);
                                            isBot = true;
                                        }
                                        if (progressDialog4 != null) {
                                            args3.putInt("message_id", progressDialog4.intValue());
                                        }
                                        if (!LaunchActivity.mainFragmentsStack.isEmpty()) {
                                            lastFragment = (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);
                                        }
                                        if (lastFragment == null || MessagesController.getInstance(i5).checkCanOpenChat(args3, lastFragment)) {
                                            if (isBot && lastFragment != null && (lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).getDialogId() == dialog_id) {
                                                ((ChatActivity) lastFragment).setBotUser(str6);
                                            } else {
                                                ChatActivity fragment3 = new ChatActivity(args3);
                                                NotificationCenter.getInstance(i5).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                LaunchActivity.this.actionBarLayout.presentFragment(fragment3, false, true, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            };
            requestId = instance.sendRequest(req2, c21739);
            z = hasUrl;
            i4 = i3;
            str7 = str2;
            i = i2;
            launchActivity = r15;
            progressDialog = progressDialog3;
            str8 = message;
        } else {
            progressDialog3 = progressDialog2;
            if (str2 == null) {
                i4 = i3;
                str7 = str2;
                i = i2;
                launchActivity = r15;
                progressDialog = progressDialog3;
                str2 = sticker;
                if (str2 != null) {
                    if (!mainFragmentsStack.isEmpty()) {
                        InputStickerSet stickerset = new TL_inputStickerSetShortName();
                        stickerset.short_name = str2;
                        BaseFragment fragment = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
                        fragment.showDialog(new StickersAlert(launchActivity, fragment, stickerset, null, null));
                    }
                    return;
                }
                str8 = message;
                if (str8 != null) {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    DialogsActivity fragment2 = new DialogsActivity(args);
                    z = hasUrl;
                    fragment2.setDelegate(new DialogsActivityDelegate() {
                        public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence m, boolean param) {
                            long did = ((Long) dids.get(0)).longValue();
                            Bundle args = new Bundle();
                            args.putBoolean("scrollToTopOnResume", true);
                            args.putBoolean("hasUrl", z);
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
                                DataQuery.getInstance(i).saveDraft(did, str8, null, null, false);
                                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(args), true, false, true);
                            }
                        }
                    });
                    presentFragment(fragment2, false, true);
                } else {
                    z = hasUrl;
                }
            } else if (i3 == 0) {
                TLObject req3 = new TL_messages_checkChatInvite();
                req3.hash = str2;
                alertDialog = progressDialog3;
                final int i6 = i2;
                final String str10 = str2;
                str5 = username;
                str6 = sticker;
                str7 = botUser;
                final String str11 = botChat;
                AnonymousClass10 anonymousClass10 = r1;
                str4 = message;
                ConnectionsManager instance2 = ConnectionsManager.getInstance(intentAccount);
                TLObject req4 = req3;
                final boolean z2 = hasUrl;
                final Integer num = messageId;
                TLObject req5 = req4;
                r15 = 2;
                str = game;
                final String[] strArr = instantView;
                AnonymousClass10 anonymousClass102 = new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.LaunchActivity$10$1$1 */
                            class C14501 implements DialogInterface.OnClickListener {
                                C14501() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LaunchActivity.this.runLinkRequest(i6, str5, str10, str6, str7, str11, str4, z2, num, str, strArr, 1);
                                }
                            }

                            public void run() {
                                if (!LaunchActivity.this.isFinishing()) {
                                    try {
                                        alertDialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    if (error != null || LaunchActivity.this.actionBarLayout == null) {
                                        Builder builder = new Builder(LaunchActivity.this);
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
                                        MessagesController.getInstance(i6).putChat(invite.chat, false);
                                        ArrayList<Chat> chats = new ArrayList();
                                        chats.add(invite.chat);
                                        MessagesStorage.getInstance(i6).putUsersAndChats(null, chats, false, true);
                                        Bundle args = new Bundle();
                                        args.putInt("chat_id", invite.chat.id);
                                        if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.getInstance(i6).checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                            ChatActivity fragment = new ChatActivity(args);
                                            NotificationCenter.getInstance(i6).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                            LaunchActivity.this.actionBarLayout.presentFragment(fragment, false, true, true);
                                        }
                                    } else if (((invite.chat != null || (invite.channel && !invite.megagroup)) && (invite.chat == null || (ChatObject.isChannel(invite.chat) && !invite.chat.megagroup))) || LaunchActivity.mainFragmentsStack.isEmpty()) {
                                        Builder builder2 = new Builder(LaunchActivity.this);
                                        builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        String str = "ChannelJoinTo";
                                        Object[] objArr = new Object[1];
                                        objArr[0] = invite.chat != null ? invite.chat.title : invite.title;
                                        builder2.setMessage(LocaleController.formatString(str, R.string.ChannelJoinTo, objArr));
                                        builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C14501());
                                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                        LaunchActivity.this.showAlertDialog(builder2);
                                    } else {
                                        BaseFragment fragment2 = (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);
                                        fragment2.showDialog(new JoinGroupAlert(LaunchActivity.this, invite, str10, fragment2));
                                    }
                                }
                            }
                        });
                    }
                };
                requestId = instance2.sendRequest(req5, anonymousClass10, r15);
                i = intentAccount;
                str8 = message;
                z = hasUrl;
                progressDialog = progressDialog3;
                i4 = state;
                str7 = group;
                launchActivity = this;
            } else {
                r15 = 2;
                if (state == 1) {
                    TL_messages_importChatInvite req6 = new TL_messages_importChatInvite();
                    req6.hash = group;
                    i = intentAccount;
                    progressDialog = progressDialog3;
                    ConnectionsManager.getInstance(intentAccount).sendRequest(req6, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            if (error == null) {
                                MessagesController.getInstance(i).processUpdates((Updates) response, false);
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
                                                MessagesController.getInstance(i).putUsers(updates.users, false);
                                                MessagesController.getInstance(i).putChats(updates.chats, false);
                                                Bundle args = new Bundle();
                                                args.putInt("chat_id", chat.id);
                                                if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.getInstance(i).checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                                    ChatActivity fragment = new ChatActivity(args);
                                                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                    LaunchActivity.this.actionBarLayout.presentFragment(fragment, false, true, true);
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }, r15);
                    str8 = message;
                    z = hasUrl;
                } else {
                    i = intentAccount;
                    progressDialog = progressDialog3;
                    str7 = group;
                    launchActivity = this;
                    str8 = message;
                    z = hasUrl;
                    str2 = sticker;
                }
            }
            if (requestId != 0) {
                final int reqId = requestId;
                progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectionsManager.getInstance(i).cancelRequest(reqId, true);
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
        str2 = sticker;
        if (requestId != 0) {
            final int reqId2 = requestId;
            progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), /* anonymous class already generated */);
            progressDialog.show();
        }
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
            return null;
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsFragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        LaunchActivity launchActivity = this;
        BaseFragment baseFragment = dialogsFragment;
        long did = ((Long) dids.get(0)).longValue();
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        Bundle args = new Bundle();
        int account = baseFragment != null ? dialogsFragment.getCurrentAccount() : launchActivity.currentAccount;
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
        if (MessagesController.getInstance(account).checkCanOpenChat(args, baseFragment)) {
            int i;
            Iterator it;
            int account2;
            ChatActivity fragment = new ChatActivity(args);
            launchActivity.actionBarLayout.presentFragment(fragment, baseFragment != null, baseFragment == null, true);
            if (launchActivity.videoPath != null) {
                fragment.openVideoEditor(launchActivity.videoPath, launchActivity.sendingText);
                launchActivity.sendingText = null;
            }
            if (launchActivity.photoPathsArray != null) {
                if (launchActivity.sendingText != null && launchActivity.sendingText.length() <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && launchActivity.photoPathsArray.size() == 1) {
                    ((SendingMediaInfo) launchActivity.photoPathsArray.get(0)).caption = launchActivity.sendingText;
                    launchActivity.sendingText = null;
                }
                i = account;
                SendMessagesHelper.prepareSendingMedia(launchActivity.photoPathsArray, did, null, null, false, 0);
            } else {
                i = account;
            }
            if (launchActivity.sendingText != null) {
                SendMessagesHelper.prepareSendingText(launchActivity.sendingText, did);
            }
            if (launchActivity.documentsPathsArray == null) {
                if (launchActivity.documentsUrisArray == null) {
                    Bundle bundle = args;
                    if (!(launchActivity.contactsToSend == null || launchActivity.contactsToSend.isEmpty())) {
                        it = launchActivity.contactsToSend.iterator();
                        while (it.hasNext()) {
                            account = i;
                            account2 = account;
                            SendMessagesHelper.getInstance(account).sendMessage((User) it.next(), did, null, null, null);
                            i = account2;
                        }
                    }
                    launchActivity.photoPathsArray = null;
                    launchActivity.videoPath = null;
                    launchActivity.sendingText = null;
                    launchActivity.documentsPathsArray = null;
                    launchActivity.documentsOriginalPathsArray = null;
                    launchActivity.contactsToSend = null;
                }
            }
            SendMessagesHelper.prepareSendingDocuments(launchActivity.documentsPathsArray, launchActivity.documentsOriginalPathsArray, launchActivity.documentsUrisArray, launchActivity.documentsMimeType, did, null, null);
            it = launchActivity.contactsToSend.iterator();
            while (it.hasNext()) {
                account = i;
                account2 = account;
                SendMessagesHelper.getInstance(account).sendMessage((User) it.next(), did, null, null, null);
                i = account2;
            }
            launchActivity.photoPathsArray = null;
            launchActivity.videoPath = null;
            launchActivity.sendingText = null;
            launchActivity.documentsPathsArray = null;
            launchActivity.documentsOriginalPathsArray = null;
            launchActivity.contactsToSend = null;
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
        if (!(requestCode == 3 || requestCode == 4 || requestCode == 5 || requestCode == 19)) {
            if (requestCode != 20) {
                if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == 0) {
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
        }
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
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("package:");
                        stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                        intent.setData(Uri.parse(stringBuilder.toString()));
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
        final int i;
        Context context = this;
        int i2 = id;
        final int i3 = account;
        if (i2 == NotificationCenter.appDidLogout) {
            switchToAvailableAccountOrLogout();
        } else if (i2 == NotificationCenter.closeOtherAppActivities) {
            if (args[0] != context) {
                onFinish();
                finish();
            }
        } else if (i2 == NotificationCenter.didUpdatedConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (context.currentConnectionState != state) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("switch to state ");
                    stringBuilder.append(state);
                    FileLog.m0d(stringBuilder.toString());
                }
                context.currentConnectionState = state;
                updateCurrentConnectionState(i3);
            }
        } else if (i2 == NotificationCenter.mainUserInfoChanged) {
            context.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (i2 == NotificationCenter.needShowAlert) {
            Integer reason = args[0];
            Builder builder = new Builder(context);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (reason.intValue() != 2) {
                builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!LaunchActivity.mainFragmentsStack.isEmpty()) {
                            MessagesController.getInstance(i3).openByUserName("spambot", (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), 1);
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
        } else if (i2 == NotificationCenter.wasUnableToFindCurrentLocation) {
            final HashMap<String, MessageObject> waitingForLocation = args[0];
            Builder builder2 = new Builder(context);
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder2.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", R.string.ShareYouLocationUnableManually), new DialogInterface.OnClickListener() {

                /* renamed from: org.telegram.ui.LaunchActivity$19$1 */
                class C21661 implements LocationActivityDelegate {
                    C21661() {
                    }

                    public void didSelectLocation(MessageMedia location, int live) {
                        for (Entry<String, MessageObject> entry : waitingForLocation.entrySet()) {
                            MessageObject messageObject = (MessageObject) entry.getValue();
                            SendMessagesHelper.getInstance(i3).sendMessage(location, messageObject.getDialogId(), messageObject, null, null);
                        }
                    }
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!LaunchActivity.mainFragmentsStack.isEmpty() && AndroidUtilities.isGoogleMapsInstalled((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                        LocationActivity fragment = new LocationActivity(0);
                        fragment.setDelegate(new C21661());
                        LaunchActivity.this.presentFragment(fragment);
                    }
                }
            });
            builder2.setMessage(LocaleController.getString("ShareYouLocationUnable", R.string.ShareYouLocationUnable));
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder2.create());
            }
        } else if (i2 == NotificationCenter.didSetNewWallpapper) {
            if (context.sideMenu != null) {
                View child = context.sideMenu.getChildAt(0);
                if (child != null) {
                    child.invalidate();
                }
            }
        } else if (i2 == NotificationCenter.didSetPasscode) {
            if (SharedConfig.passcodeHash.length() <= 0 || SharedConfig.allowScreenCapture) {
                try {
                    getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else {
                try {
                    getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        } else if (i2 == NotificationCenter.reloadInterface) {
            rebuildAllFragments(false);
        } else if (i2 == NotificationCenter.suggestedLangpack) {
            showLanguageAlert(false);
        } else if (i2 != NotificationCenter.openArticle) {
            if (i2 == NotificationCenter.hasNewContactsToImport) {
                if (context.actionBarLayout != null) {
                    if (!context.actionBarLayout.fragmentsStack.isEmpty()) {
                        int type = ((Integer) args[0]).intValue();
                        HashMap<String, Contact> contactHashMap = args[1];
                        boolean first = ((Boolean) args[2]).booleanValue();
                        boolean schedule = ((Boolean) args[3]).booleanValue();
                        BaseFragment fragment = (BaseFragment) context.actionBarLayout.fragmentsStack.get(context.actionBarLayout.fragmentsStack.size() - 1);
                        Builder builder3 = new Builder(context);
                        builder3.setTitle(LocaleController.getString("UpdateContactsTitle", R.string.UpdateContactsTitle));
                        builder3.setMessage(LocaleController.getString("UpdateContactsMessage", R.string.UpdateContactsMessage));
                        AnonymousClass20 anonymousClass20 = r1;
                        i = i3;
                        String string = LocaleController.getString("OK", R.string.OK);
                        final HashMap<String, Contact> hashMap = contactHashMap;
                        Builder builder4 = builder3;
                        final boolean z = first;
                        BaseFragment fragment2 = fragment;
                        final boolean z2 = schedule;
                        AnonymousClass20 anonymousClass202 = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, false);
                            }
                        };
                        builder4.setPositiveButton(string, anonymousClass20);
                        builder4.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, true);
                            }
                        });
                        builder4.setOnBackButtonListener(new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, true);
                            }
                        });
                        AlertDialog dialog = builder4.create();
                        fragment2.showDialog(dialog);
                        dialog.setCanceledOnTouchOutside(false);
                        i = id;
                    }
                }
                return;
            }
            int i4 = 0;
            i = id;
            if (i == NotificationCenter.didSetNewTheme) {
                if (!args[i4].booleanValue()) {
                    if (context.sideMenu != null) {
                        context.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
                        context.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
                        context.sideMenu.getAdapter().notifyDataSetChanged();
                    }
                    if (VERSION.SDK_INT >= 21) {
                        try {
                            setTaskDescription(new TaskDescription(null, null, Theme.getColor(Theme.key_actionBarDefault) | Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
                        } catch (Exception e3) {
                        }
                    }
                }
            } else if (i == NotificationCenter.needSetDayNightTheme) {
                ThemeInfo theme = args[0];
                context.actionBarLayout.animateThemedValues(theme);
                if (AndroidUtilities.isTablet()) {
                    context.layersActionBarLayout.animateThemedValues(theme);
                    context.rightActionBarLayout.animateThemedValues(theme);
                }
            } else if (i == NotificationCenter.notificationsCountUpdated && context.sideMenu != null) {
                i4 = 0;
                Integer accountNum = args[0];
                int count = context.sideMenu.getChildCount();
                while (i4 < count) {
                    View child2 = context.sideMenu.getChildAt(i4);
                    if ((child2 instanceof DrawerUserCell) && ((DrawerUserCell) child2).getAccountNumber() == accountNum.intValue()) {
                        child2.invalidate();
                        break;
                    }
                    i4++;
                }
            }
        } else if (!mainFragmentsStack.isEmpty()) {
            ArticleViewer.getInstance().setParentActivity(context, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1));
            ArticleViewer.getInstance().open((TL_webPage) args[0], (String) args[1]);
        } else {
            return;
        }
        i = i2;
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
                class C14541 implements Runnable {
                    C14541() {
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
                                        AndroidUtilities.runOnUIThread(new C14541());
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
        Throwable e;
        String str;
        LocaleInfo localeInfo;
        LocaleInfo localeInfo2;
        try {
            this.loadingLocaleDialog = false;
            localeInfo = systemInfo;
            try {
                boolean firstSystem;
                Builder builder;
                LinearLayout linearLayout;
                int i;
                final LanguageCell[] cells;
                final LocaleInfo[] selectedLanguage;
                LocaleInfo[] locales;
                String englishName;
                int a;
                LanguageCell cell;
                if (!localeInfo.builtIn) {
                    if (!LocaleController.getInstance().isCurrentLocalLocale()) {
                        firstSystem = false;
                        builder = new Builder(r1);
                        builder.setTitle(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
                        builder.setSubtitle(getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
                        linearLayout = new LinearLayout(r1);
                        linearLayout.setOrientation(1);
                        i = 2;
                        cells = new LanguageCell[2];
                        selectedLanguage = new LocaleInfo[1];
                        locales = new LocaleInfo[2];
                        englishName = getStringForLanguageAlert(r1.systemLocaleStrings, "English", R.string.English);
                        locales[0] = firstSystem ? localeInfo : englishInfo;
                        locales[1] = firstSystem ? englishInfo : localeInfo;
                        selectedLanguage[0] = firstSystem ? localeInfo : englishInfo;
                        a = 0;
                        while (a < i) {
                            cells[a] = new LanguageCell(r1, true);
                            try {
                                cells[a].setLanguage(locales[a], locales[a] != englishInfo ? englishName : null, true);
                                cells[a].setTag(Integer.valueOf(a));
                                cells[a].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                                cells[a].setLanguageSelected(a != 0);
                                linearLayout.addView(cells[a], LayoutHelper.createLinear(-1, 48));
                                cells[a].setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        Integer tag = (Integer) v.getTag();
                                        selectedLanguage[0] = ((LanguageCell) v).getCurrentLocale();
                                        int a = 0;
                                        while (a < cells.length) {
                                            cells[a].setLanguageSelected(a == tag.intValue());
                                            a++;
                                        }
                                    }
                                });
                                a++;
                                i = 2;
                            } catch (Exception e2) {
                                e = e2;
                            }
                        }
                        localeInfo2 = englishInfo;
                        cell = new LanguageCell(r1, true);
                        cell.setValue(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther), getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther));
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
                        r1.localeDialog = showAlertDialog(builder);
                        MessagesController.getGlobalMainSettings().edit().putString("language_showed2", systemLang).commit();
                    }
                }
                firstSystem = true;
                builder = new Builder(r1);
                builder.setTitle(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
                builder.setSubtitle(getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
                linearLayout = new LinearLayout(r1);
                linearLayout.setOrientation(1);
                i = 2;
                cells = new LanguageCell[2];
                selectedLanguage = new LocaleInfo[1];
                locales = new LocaleInfo[2];
                englishName = getStringForLanguageAlert(r1.systemLocaleStrings, "English", R.string.English);
                if (firstSystem) {
                }
                locales[0] = firstSystem ? localeInfo : englishInfo;
                if (firstSystem) {
                }
                locales[1] = firstSystem ? englishInfo : localeInfo;
                if (firstSystem) {
                }
                selectedLanguage[0] = firstSystem ? localeInfo : englishInfo;
                a = 0;
                while (a < i) {
                    cells[a] = new LanguageCell(r1, true);
                    if (locales[a] != englishInfo) {
                    }
                    cells[a].setLanguage(locales[a], locales[a] != englishInfo ? englishName : null, true);
                    cells[a].setTag(Integer.valueOf(a));
                    cells[a].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                    if (a != 0) {
                    }
                    cells[a].setLanguageSelected(a != 0);
                    linearLayout.addView(cells[a], LayoutHelper.createLinear(-1, 48));
                    cells[a].setOnClickListener(/* anonymous class already generated */);
                    a++;
                    i = 2;
                }
                localeInfo2 = englishInfo;
                cell = new LanguageCell(r1, true);
                cell.setValue(getStringForLanguageAlert(r1.systemLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther), getStringForLanguageAlert(r1.englishLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther));
                cell.setOnClickListener(/* anonymous class already generated */);
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
                builder.setView(linearLayout);
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), /* anonymous class already generated */);
                r1.localeDialog = showAlertDialog(builder);
                try {
                    MessagesController.getGlobalMainSettings().edit().putString("language_showed2", systemLang).commit();
                } catch (Exception e3) {
                    e = e3;
                    FileLog.m3e(e);
                }
            } catch (Exception e4) {
                e = e4;
                localeInfo2 = englishInfo;
                str = systemLang;
                FileLog.m3e(e);
            }
        } catch (Exception e5) {
            e = e5;
            localeInfo = systemInfo;
            localeInfo2 = englishInfo;
            str = systemLang;
            FileLog.m3e(e);
        }
    }

    private void showLanguageAlert(boolean force) {
        try {
            if (!this.loadingLocaleDialog) {
                String showedLang = MessagesController.getGlobalMainSettings().getString("language_showed2", TtmlNode.ANONYMOUS_REGION_ID);
                final String systemLang = LocaleController.getSystemLocaleStringIso639().toLowerCase();
                if (force || !showedLang.equals(systemLang)) {
                    int a;
                    LocaleInfo info;
                    StringBuilder stringBuilder;
                    TL_langpack_getStrings req;
                    final LocaleInfo[] infos = new LocaleInfo[2];
                    String arg = systemLang.contains("-") ? systemLang.split("-")[0] : systemLang;
                    String alias;
                    if ("in".equals(arg)) {
                        alias = TtmlNode.ATTR_ID;
                    } else if ("iw".equals(arg)) {
                        alias = "he";
                    } else if ("jw".equals(arg)) {
                        alias = "jv";
                    } else {
                        alias = null;
                        for (a = 0; a < LocaleController.getInstance().languages.size(); a++) {
                            info = (LocaleInfo) LocaleController.getInstance().languages.get(a);
                            if (info.shortName.equals("en")) {
                                infos[0] = info;
                            }
                            if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || (alias != null && info.shortName.equals(alias))) {
                                infos[1] = info;
                            }
                            if (infos[0] == null && infos[1] != null) {
                                break;
                            }
                        }
                        if (!(infos[0] == null || infos[1] == null)) {
                            if (infos[0] == infos[1]) {
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("show lang alert for ");
                                    stringBuilder.append(infos[0].getKey());
                                    stringBuilder.append(" and ");
                                    stringBuilder.append(infos[1].getKey());
                                    FileLog.m0d(stringBuilder.toString());
                                }
                                this.systemLocaleStrings = null;
                                this.englishLocaleStrings = null;
                                this.loadingLocaleDialog = true;
                                req = new TL_langpack_getStrings();
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
                                return;
                            }
                        }
                        return;
                    }
                    for (a = 0; a < LocaleController.getInstance().languages.size(); a++) {
                        info = (LocaleInfo) LocaleController.getInstance().languages.get(a);
                        if (info.shortName.equals("en")) {
                            infos[0] = info;
                        }
                        infos[1] = info;
                        if (infos[0] == null) {
                        }
                    }
                    if (infos[0] == infos[1]) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("show lang alert for ");
                            stringBuilder.append(infos[0].getKey());
                            stringBuilder.append(" and ");
                            stringBuilder.append(infos[1].getKey());
                            FileLog.m0d(stringBuilder.toString());
                        }
                        this.systemLocaleStrings = null;
                        this.englishLocaleStrings = null;
                        this.loadingLocaleDialog = true;
                        req = new TL_langpack_getStrings();
                        req.lang_code = infos[1].shortName.replace("_", "-");
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */, 8);
                        req = new TL_langpack_getStrings();
                        req.lang_code = infos[0].shortName.replace("_", "-");
                        req.keys.add("English");
                        req.keys.add("ChooseYourLanguage");
                        req.keys.add("ChooseYourLanguageOther");
                        req.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */, 8);
                        return;
                    }
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("alert already showed for ");
                    stringBuilder2.append(showedLang);
                    FileLog.m0d(stringBuilder2.toString());
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
                    class C14581 implements DialogInterface.OnClickListener {
                        C14581() {
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
                        if (LaunchActivity.this.actionBarLayout != null) {
                            if (!LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty()) {
                                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                BaseFragment fragment = (BaseFragment) LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1);
                                Builder builder = new Builder(LaunchActivity.this);
                                builder.setTitle(LocaleController.getString("Proxy", R.string.Proxy));
                                builder.setMessage(LocaleController.formatString("ConnectingToProxyDisableAlert", R.string.ConnectingToProxyDisableAlert, preferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID)));
                                builder.setPositiveButton(LocaleController.getString("ConnectingToProxyDisable", R.string.ConnectingToProxyDisable), new C14581());
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                fragment.showDialog(builder.create());
                            }
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
            return;
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
                cancel = 1 ^ ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onBackPressed();
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
        boolean result;
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
                if (!this.tabletFullSize) {
                    this.shadowTabletSide.setVisibility(0);
                    if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        this.backgroundTablet.setVisibility(0);
                    }
                }
                return false;
            } else if (fragment instanceof ChatActivity) {
                if ((!this.tabletFullSize && layout == this.rightActionBarLayout) || (this.tabletFullSize && layout == this.actionBarLayout)) {
                    int a;
                    if (this.tabletFullSize && layout == this.actionBarLayout) {
                        if (this.actionBarLayout.fragmentsStack.size() == 1) {
                            result = false;
                            if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                for (a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                                    this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                                }
                                this.layersActionBarLayout.closeLastFragment(forceWithoutAnimation ^ 1);
                            }
                            if (!result) {
                                this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false);
                            }
                            return result;
                        }
                    }
                    result = true;
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(forceWithoutAnimation ^ 1);
                    }
                    if (result) {
                        this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false);
                    }
                    return result;
                } else if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(fragment, removeLast, true, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(forceWithoutAnimation ^ 1);
                    }
                    return false;
                } else if (!this.tabletFullSize || layout == this.actionBarLayout) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(forceWithoutAnimation ^ 1);
                    }
                    ActionBarLayout actionBarLayout = this.actionBarLayout;
                    if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                        z = false;
                    }
                    actionBarLayout.presentFragment(fragment, z, forceWithoutAnimation, false);
                    return false;
                } else {
                    this.actionBarLayout.presentFragment(fragment, this.actionBarLayout.fragmentsStack.size() > 1, forceWithoutAnimation, false);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(forceWithoutAnimation ^ 1);
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
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false);
                return false;
            }
        }
        result = true;
        if (fragment instanceof LoginActivity) {
            if (mainFragmentsStack.size() == 0) {
                result = false;
            }
        } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
            result = false;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(result, false);
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
                    if (!this.tabletFullSize) {
                        this.shadowTabletSide.setVisibility(0);
                        if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                            this.backgroundTablet.setVisibility(0);
                        }
                    }
                    return false;
                }
            } else if (fragment instanceof ChatActivity) {
                int a;
                if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(fragment);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(true);
                    }
                    return false;
                } else if (this.tabletFullSize && layout != this.actionBarLayout) {
                    this.actionBarLayout.addFragmentToStack(fragment);
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        for (a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(true);
                    }
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
