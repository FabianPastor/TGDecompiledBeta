package org.telegram.messenger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;

public class MessagesController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    private static volatile MessagesController[] Instance = new MessagesController[3];
    public static final int UPDATE_MASK_ALL = 1535;
    public static final int UPDATE_MASK_AVATAR = 2;
    public static final int UPDATE_MASK_CHAT = 8192;
    public static final int UPDATE_MASK_CHAT_AVATAR = 8;
    public static final int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static final int UPDATE_MASK_CHAT_NAME = 16;
    public static final int UPDATE_MASK_CHECK = 65536;
    public static final int UPDATE_MASK_MESSAGE_TEXT = 32768;
    public static final int UPDATE_MASK_NAME = 1;
    public static final int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static final int UPDATE_MASK_PHONE = 1024;
    public static final int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static final int UPDATE_MASK_REORDER = 131072;
    public static final int UPDATE_MASK_SELECT_DIALOG = 512;
    public static final int UPDATE_MASK_SEND_STATE = 4096;
    public static final int UPDATE_MASK_STATUS = 4;
    public static final int UPDATE_MASK_USER_PHONE = 128;
    public static final int UPDATE_MASK_USER_PRINT = 64;
    private static volatile long lastPasswordCheckTime;
    private static volatile long lastThemeCheckTime;
    private int DIALOGS_LOAD_TYPE_CACHE = 1;
    private int DIALOGS_LOAD_TYPE_CHANNEL = 2;
    private int DIALOGS_LOAD_TYPE_UNKNOWN = 3;
    protected ArrayList<TLRPC.Dialog> allDialogs = new ArrayList<>();
    public float animatedEmojisZoom;
    public int availableMapProviders;
    public boolean backgroundConnection;
    public boolean blockedCountry;
    public boolean blockedEndReached;
    public SparseIntArray blockedUsers = new SparseIntArray();
    public int callConnectTimeout;
    public int callPacketTimeout;
    public int callReceiveTimeout;
    public int callRingTimeout;
    public boolean canRevokePmInbox;
    private SparseArray<SparseArray<String>> channelAdmins = new SparseArray<>();
    private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray<>();
    private SparseIntArray channelsPts = new SparseIntArray();
    private ConcurrentHashMap<Integer, TLRPC.Chat> chats = new ConcurrentHashMap<>(100, 1.0f, 2);
    private SparseBooleanArray checkingLastMessagesDialogs = new SparseBooleanArray();
    private boolean checkingProxyInfo;
    private int checkingProxyInfoRequestId;
    private boolean checkingTosUpdate;
    private LongSparseArray<TLRPC.Dialog> clearingHistoryDialogs = new LongSparseArray<>();
    private ArrayList<Long> createdDialogIds = new ArrayList<>();
    private ArrayList<Long> createdDialogMainThreadIds = new ArrayList<>();
    private ArrayList<Long> createdScheduledDialogIds = new ArrayList<>();
    private Runnable currentDeleteTaskRunnable;
    private int currentDeletingTaskChannelId;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public String dcDomainName;
    public LongSparseArray<Integer> deletedHistory = new LongSparseArray<>();
    private LongSparseArray<TLRPC.Dialog> deletingDialogs = new LongSparseArray<>();
    private final Comparator<TLRPC.Dialog> dialogComparator = new Comparator() {
        public final int compare(Object obj, Object obj2) {
            return MessagesController.this.lambda$new$1$MessagesController((TLRPC.Dialog) obj, (TLRPC.Dialog) obj2);
        }
    };
    public LongSparseArray<MessageObject> dialogMessage = new LongSparseArray<>();
    public SparseArray<MessageObject> dialogMessagesByIds = new SparseArray<>();
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds = new LongSparseArray<>();
    private SparseArray<ArrayList<TLRPC.Dialog>> dialogsByFolder = new SparseArray<>();
    public ArrayList<TLRPC.Dialog> dialogsCanAddUsers = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsChannelsOnly = new ArrayList<>();
    private SparseBooleanArray dialogsEndReached = new SparseBooleanArray();
    public ArrayList<TLRPC.Dialog> dialogsForward = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsGroupsOnly = new ArrayList<>();
    private boolean dialogsInTransaction;
    public boolean dialogsLoaded;
    public ArrayList<TLRPC.Dialog> dialogsServerOnly = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsUsersOnly = new ArrayList<>();
    public LongSparseArray<TLRPC.Dialog> dialogs_dict = new LongSparseArray<>();
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
    private SharedPreferences emojiPreferences;
    public boolean enableJoined;
    private ConcurrentHashMap<Integer, TLRPC.EncryptedChat> encryptedChats = new ConcurrentHashMap<>(10, 1.0f, 2);
    private SparseArray<TLRPC.ExportedChatInvite> exportedChats = new SparseArray<>();
    public boolean firstGettingTask;
    private SparseArray<TLRPC.ChatFull> fullChats = new SparseArray<>();
    private SparseArray<TLRPC.UserFull> fullUsers = new SparseArray<>();
    private boolean getDifferenceFirstSync = true;
    private boolean gettingAppChangelog;
    public boolean gettingDifference;
    private SparseBooleanArray gettingDifferenceChannels = new SparseBooleanArray();
    private boolean gettingNewDeleteTask;
    private SparseBooleanArray gettingUnknownChannels = new SparseBooleanArray();
    private LongSparseArray<Boolean> gettingUnknownDialogs = new LongSparseArray<>();
    public String gifSearchBot;
    public ArrayList<TLRPC.RecentMeUrl> hintDialogs = new ArrayList<>();
    public String imageSearchBot;
    private String installReferer;
    private boolean isLeftProxyChannel;
    private ArrayList<Integer> joiningToChannels = new ArrayList<>();
    public boolean keepAliveService;
    private int lastCheckProxyId;
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private LongSparseArray<Long> lastScheduledServerQueryTime = new LongSparseArray<>();
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private ArrayList<Integer> loadedFullChats = new ArrayList<>();
    private ArrayList<Integer> loadedFullParticipants = new ArrayList<>();
    private ArrayList<Integer> loadedFullUsers = new ArrayList<>();
    private boolean loadingAppConfig;
    public boolean loadingBlockedUsers = false;
    private SparseIntArray loadingChannelAdmins = new SparseIntArray();
    private SparseBooleanArray loadingDialogs = new SparseBooleanArray();
    private ArrayList<Integer> loadingFullChats = new ArrayList<>();
    private ArrayList<Integer> loadingFullParticipants = new ArrayList<>();
    private ArrayList<Integer> loadingFullUsers = new ArrayList<>();
    private int loadingNotificationSettings;
    private boolean loadingNotificationSignUpSettings;
    private LongSparseArray<Boolean> loadingPeerSettings = new LongSparseArray<>();
    private SparseIntArray loadingPinnedDialogs = new SparseIntArray();
    private boolean loadingUnreadDialogs;
    private SharedPreferences mainPreferences;
    public String mapKey;
    public int mapProvider;
    public int maxBroadcastCount = 100;
    public int maxCaptionLength;
    public int maxEditTime;
    public int maxFaveStickersCount;
    public int maxFolderPinnedDialogsCount;
    public int maxGroupCount;
    public int maxMegagroupCount;
    public int maxMessageLength;
    public int maxPinnedDialogsCount;
    public int maxRecentGifsCount;
    public int maxRecentStickersCount;
    private SparseIntArray migratedChats = new SparseIntArray();
    private boolean migratingDialogs;
    public int minGroupConvertSize = 200;
    private SparseIntArray needShortPollChannels = new SparseIntArray();
    private SparseIntArray needShortPollOnlines = new SparseIntArray();
    private SparseIntArray nextDialogsCacheOffset = new SparseIntArray();
    private int nextProxyInfoCheckTime;
    private int nextTosCheckTime;
    private SharedPreferences notificationsPreferences;
    private ConcurrentHashMap<String, TLObject> objectsByUsernames = new ConcurrentHashMap<>(100, 1.0f, 2);
    private boolean offlineSent;
    public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap<>(20, 1.0f, 2);
    private Runnable passwordCheckRunnable = new Runnable() {
        public final void run() {
            MessagesController.this.lambda$new$0$MessagesController();
        }
    };
    private LongSparseArray<SparseArray<MessageObject>> pollsToCheck = new LongSparseArray<>();
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public LongSparseArray<CharSequence> printingStrings = new LongSparseArray<>();
    public LongSparseArray<Integer> printingStringsTypes = new LongSparseArray<>();
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap<>(20, 1.0f, 2);
    private TLRPC.Dialog proxyDialog;
    private String proxyDialogAddress;
    private long proxyDialogId;
    public boolean qrLoginCamera;
    public int ratingDecay;
    private ArrayList<ReadTask> readTasks = new ArrayList<>();
    private LongSparseArray<ReadTask> readTasksMap = new LongSparseArray<>();
    public boolean registeringForPush;
    private LongSparseArray<ArrayList<Integer>> reloadingMessages = new LongSparseArray<>();
    private HashMap<String, ArrayList<MessageObject>> reloadingScheduledWebpages = new HashMap<>();
    private LongSparseArray<ArrayList<MessageObject>> reloadingScheduledWebpagesPending = new LongSparseArray<>();
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages = new HashMap<>();
    private LongSparseArray<ArrayList<MessageObject>> reloadingWebpagesPending = new LongSparseArray<>();
    private TLRPC.messages_Dialogs resetDialogsAll;
    private TLRPC.TL_messages_peerDialogs resetDialogsPinned;
    private boolean resetingDialogs;
    public int revokeTimeLimit;
    public int revokeTimePmLimit;
    public int secretWebpagePreview;
    public SparseArray<LongSparseArray<Boolean>> sendingTypings = new SparseArray<>();
    private SparseBooleanArray serverDialogsEndReached = new SparseBooleanArray();
    private SparseIntArray shortPollChannels = new SparseIntArray();
    private SparseIntArray shortPollOnlines = new SparseIntArray();
    private int statusRequest;
    private int statusSettingState;
    public boolean suggestContacts = true;
    public String suggestedLangCode;
    private Runnable themeCheckRunnable = $$Lambda$RQB0Jwr1FTqp6hrbGUHuOs9k1I.INSTANCE;
    public int totalBlockedCount = -1;
    public int unreadUnmutedDialogs;
    private final Comparator<TLRPC.Update> updatesComparator = new Comparator() {
        public final int compare(Object obj, Object obj2) {
            return MessagesController.this.lambda$new$2$MessagesController((TLRPC.Update) obj, (TLRPC.Update) obj2);
        }
    };
    private SparseArray<ArrayList<TLRPC.Updates>> updatesQueueChannels = new SparseArray<>();
    private ArrayList<TLRPC.Updates> updatesQueuePts = new ArrayList<>();
    private ArrayList<TLRPC.Updates> updatesQueueQts = new ArrayList<>();
    private ArrayList<TLRPC.Updates> updatesQueueSeq = new ArrayList<>();
    private SparseLongArray updatesStartWaitTimeChannels = new SparseLongArray();
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    private String uploadingAvatar;
    private HashMap<String, Object> uploadingThemes = new HashMap<>();
    private String uploadingWallpaper;
    private Theme.OverrideWallpaperInfo uploadingWallpaperInfo;
    private ConcurrentHashMap<Integer, TLRPC.User> users = new ConcurrentHashMap<>(100, 1.0f, 2);
    public String venueSearchBot;
    private ArrayList<Long> visibleDialogMainThreadIds = new ArrayList<>();
    private ArrayList<Long> visibleScheduledDialogMainThreadIds = new ArrayList<>();
    public int webFileDatacenterId;
    public String youtubePipType;

    public static class PrintingUser {
        public TLRPC.SendMessageAction action;
        public long lastTime;
        public int userId;
    }

    static /* synthetic */ void lambda$blockUser$47(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$completeReadTask$163(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$deleteUserPhoto$68(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$hidePeerSettingsBar$33(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$installTheme$71(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$installTheme$72(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMentionMessageAsRead$158(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMentionsAsRead$165(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMessageContentAsRead$156(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$processUpdates$254(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$reportSpam$34(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$reportSpam$35(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$saveTheme$70(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$unblockUser$63(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$unregistedPush$201(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public /* synthetic */ void lambda$new$0$MessagesController() {
        getUserConfig().checkSavedPassword();
    }

    private class UserActionUpdatesSeq extends TLRPC.Updates {
        private UserActionUpdatesSeq() {
        }
    }

    private class UserActionUpdatesPts extends TLRPC.Updates {
        private UserActionUpdatesPts() {
        }
    }

    private class ReadTask {
        public long dialogId;
        public int maxDate;
        public int maxId;
        public long sendRequestTime;

        private ReadTask() {
        }
    }

    public /* synthetic */ int lambda$new$1$MessagesController(TLRPC.Dialog dialog, TLRPC.Dialog dialog2) {
        boolean z = dialog instanceof TLRPC.TL_dialogFolder;
        if (z && !(dialog2 instanceof TLRPC.TL_dialogFolder)) {
            return -1;
        }
        if (!z && (dialog2 instanceof TLRPC.TL_dialogFolder)) {
            return 1;
        }
        if (!dialog.pinned && dialog2.pinned) {
            return 1;
        }
        if (dialog.pinned && !dialog2.pinned) {
            return -1;
        }
        if (!dialog.pinned || !dialog2.pinned) {
            MediaDataController mediaDataController = getMediaDataController();
            long lastMessageOrDraftDate = DialogObject.getLastMessageOrDraftDate(dialog, mediaDataController.getDraft(dialog.id));
            long lastMessageOrDraftDate2 = DialogObject.getLastMessageOrDraftDate(dialog2, mediaDataController.getDraft(dialog2.id));
            if (lastMessageOrDraftDate < lastMessageOrDraftDate2) {
                return 1;
            }
            if (lastMessageOrDraftDate > lastMessageOrDraftDate2) {
                return -1;
            }
            return 0;
        }
        int i = dialog.pinnedNum;
        int i2 = dialog2.pinnedNum;
        if (i < i2) {
            return 1;
        }
        if (i > i2) {
            return -1;
        }
        return 0;
    }

    public /* synthetic */ int lambda$new$2$MessagesController(TLRPC.Update update, TLRPC.Update update2) {
        int updateType = getUpdateType(update);
        int updateType2 = getUpdateType(update2);
        if (updateType != updateType2) {
            return AndroidUtilities.compare(updateType, updateType2);
        }
        if (updateType == 0) {
            return AndroidUtilities.compare(getUpdatePts(update), getUpdatePts(update2));
        }
        if (updateType == 1) {
            return AndroidUtilities.compare(getUpdateQts(update), getUpdateQts(update2));
        }
        if (updateType != 2) {
            return 0;
        }
        int updateChannelId = getUpdateChannelId(update);
        int updateChannelId2 = getUpdateChannelId(update2);
        if (updateChannelId == updateChannelId2) {
            return AndroidUtilities.compare(getUpdatePts(update), getUpdatePts(update2));
        }
        return AndroidUtilities.compare(updateChannelId, updateChannelId2);
    }

    public static MessagesController getInstance(int i) {
        MessagesController messagesController = Instance[i];
        if (messagesController == null) {
            synchronized (MessagesController.class) {
                messagesController = Instance[i];
                if (messagesController == null) {
                    MessagesController[] messagesControllerArr = Instance;
                    MessagesController messagesController2 = new MessagesController(i);
                    messagesControllerArr[i] = messagesController2;
                    messagesController = messagesController2;
                }
            }
        }
        return messagesController;
    }

    public static SharedPreferences getNotificationsSettings(int i) {
        return getInstance(i).notificationsPreferences;
    }

    public static SharedPreferences getGlobalNotificationsSettings() {
        return getInstance(0).notificationsPreferences;
    }

    public static SharedPreferences getMainSettings(int i) {
        return getInstance(i).mainPreferences;
    }

    public static SharedPreferences getGlobalMainSettings() {
        return getInstance(0).mainPreferences;
    }

    public static SharedPreferences getEmojiSettings(int i) {
        return getInstance(i).emojiPreferences;
    }

    public static SharedPreferences getGlobalEmojiSettings() {
        return getInstance(0).emojiPreferences;
    }

    public MessagesController(int i) {
        super(i);
        int i2 = 2;
        this.currentAccount = i;
        ImageLoader.getInstance();
        getMessagesStorage();
        getLocationController();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MessagesController.this.lambda$new$3$MessagesController();
            }
        });
        addSupportUser();
        if (this.currentAccount == 0) {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.notificationsPreferences = context.getSharedPreferences("Notifications" + this.currentAccount, 0);
            Context context2 = ApplicationLoader.applicationContext;
            this.mainPreferences = context2.getSharedPreferences("mainconfig" + this.currentAccount, 0);
            Context context3 = ApplicationLoader.applicationContext;
            this.emojiPreferences = context3.getSharedPreferences("emoji" + this.currentAccount, 0);
        }
        this.enableJoined = this.notificationsPreferences.getBoolean("EnableContactJoined", true);
        this.secretWebpagePreview = this.mainPreferences.getInt("secretWebpage2", 2);
        this.maxGroupCount = this.mainPreferences.getInt("maxGroupCount", 200);
        this.maxMegagroupCount = this.mainPreferences.getInt("maxMegagroupCount", 10000);
        this.maxRecentGifsCount = this.mainPreferences.getInt("maxRecentGifsCount", 200);
        this.maxRecentStickersCount = this.mainPreferences.getInt("maxRecentStickersCount", 30);
        this.maxFaveStickersCount = this.mainPreferences.getInt("maxFaveStickersCount", 5);
        this.maxEditTime = this.mainPreferences.getInt("maxEditTime", 3600);
        this.ratingDecay = this.mainPreferences.getInt("ratingDecay", 2419200);
        this.linkPrefix = this.mainPreferences.getString("linkPrefix", "t.me");
        this.callReceiveTimeout = this.mainPreferences.getInt("callReceiveTimeout", 20000);
        this.callRingTimeout = this.mainPreferences.getInt("callRingTimeout", 90000);
        this.callConnectTimeout = this.mainPreferences.getInt("callConnectTimeout", 30000);
        this.callPacketTimeout = this.mainPreferences.getInt("callPacketTimeout", 10000);
        this.maxPinnedDialogsCount = this.mainPreferences.getInt("maxPinnedDialogsCount", 5);
        this.maxFolderPinnedDialogsCount = this.mainPreferences.getInt("maxFolderPinnedDialogsCount", 100);
        this.maxMessageLength = this.mainPreferences.getInt("maxMessageLength", 4096);
        this.maxCaptionLength = this.mainPreferences.getInt("maxCaptionLength", 1024);
        this.mapProvider = this.mainPreferences.getInt("mapProvider", 0);
        this.availableMapProviders = this.mainPreferences.getInt("availableMapProviders", 3);
        this.mapKey = this.mainPreferences.getString("pk", (String) null);
        this.installReferer = this.mainPreferences.getString("installReferer", (String) null);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
        this.youtubePipType = this.mainPreferences.getString("youtubePipType", "disabled");
        this.keepAliveService = this.mainPreferences.getBoolean("keepAliveService", false);
        this.backgroundConnection = this.mainPreferences.getBoolean("keepAliveService", false);
        this.proxyDialogId = this.mainPreferences.getLong("proxy_dialog", 0);
        this.proxyDialogAddress = this.mainPreferences.getString("proxyDialogAddress", (String) null);
        this.nextTosCheckTime = this.notificationsPreferences.getInt("nextTosCheckTime", 0);
        this.venueSearchBot = this.mainPreferences.getString("venueSearchBot", "foursquare");
        this.gifSearchBot = this.mainPreferences.getString("gifSearchBot", "gif");
        this.imageSearchBot = this.mainPreferences.getString("imageSearchBot", "pic");
        this.blockedCountry = this.mainPreferences.getBoolean("blockedCountry", false);
        this.dcDomainName = this.mainPreferences.getString("dcDomainName2", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : "apv3.stel.com");
        this.webFileDatacenterId = this.mainPreferences.getInt("webFileDatacenterId", ConnectionsManager.native_isTestBackend(this.currentAccount) == 0 ? 4 : i2);
        this.suggestedLangCode = this.mainPreferences.getString("suggestedLangCode", "en");
        this.animatedEmojisZoom = this.mainPreferences.getFloat("animatedEmojisZoom", 0.625f);
        this.qrLoginCamera = this.mainPreferences.getBoolean("qrLoginCamera", false);
    }

    public /* synthetic */ void lambda$new$3$MessagesController() {
        MessagesController messagesController = getMessagesController();
        getNotificationCenter().addObserver(messagesController, NotificationCenter.FileDidUpload);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.FileDidFailUpload);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileDidLoad);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileDidFailToLoad);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.messageReceivedByServer);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.updateMessageMedia);
    }

    private void loadAppConfig() {
        if (!this.loadingAppConfig) {
            this.loadingAppConfig = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_help_getAppConfig(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadAppConfig$5$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadAppConfig$5$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$4$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$MessagesController(TLObject tLObject) {
        boolean z;
        boolean z2;
        boolean z3;
        if (tLObject instanceof TLRPC.TL_jsonObject) {
            SharedPreferences.Editor edit = this.mainPreferences.edit();
            TLRPC.TL_jsonObject tL_jsonObject = (TLRPC.TL_jsonObject) tLObject;
            int size = tL_jsonObject.value.size();
            boolean z4 = false;
            boolean z5 = false;
            for (int i = 0; i < size; i++) {
                TLRPC.TL_jsonObjectValue tL_jsonObjectValue = tL_jsonObject.value.get(i);
                if ("emojies_animated_zoom".equals(tL_jsonObjectValue.key)) {
                    TLRPC.JSONValue jSONValue = tL_jsonObjectValue.value;
                    if (jSONValue instanceof TLRPC.TL_jsonNumber) {
                        double d = ((TLRPC.TL_jsonNumber) jSONValue).value;
                        if (((double) this.animatedEmojisZoom) != d) {
                            this.animatedEmojisZoom = (float) d;
                            edit.putFloat("animatedEmojisZoom", this.animatedEmojisZoom);
                        }
                    }
                } else if ("youtube_pip".equals(tL_jsonObjectValue.key)) {
                    TLRPC.JSONValue jSONValue2 = tL_jsonObjectValue.value;
                    if (jSONValue2 instanceof TLRPC.TL_jsonString) {
                        TLRPC.TL_jsonString tL_jsonString = (TLRPC.TL_jsonString) jSONValue2;
                        if (!tL_jsonString.value.equals(this.youtubePipType)) {
                            this.youtubePipType = tL_jsonString.value;
                            edit.putString("youtubePipType", this.youtubePipType);
                        }
                    }
                } else {
                    if ("background_connection".equals(tL_jsonObjectValue.key)) {
                        TLRPC.JSONValue jSONValue3 = tL_jsonObjectValue.value;
                        if ((jSONValue3 instanceof TLRPC.TL_jsonBool) && (z3 = ((TLRPC.TL_jsonBool) jSONValue3).value) != this.backgroundConnection) {
                            this.backgroundConnection = z3;
                            edit.putBoolean("backgroundConnection", this.backgroundConnection);
                        }
                    } else {
                        if ("keep_alive_service".equals(tL_jsonObjectValue.key)) {
                            TLRPC.JSONValue jSONValue4 = tL_jsonObjectValue.value;
                            if ((jSONValue4 instanceof TLRPC.TL_jsonBool) && (z2 = ((TLRPC.TL_jsonBool) jSONValue4).value) != this.keepAliveService) {
                                this.keepAliveService = z2;
                                edit.putBoolean("keepAliveService", this.keepAliveService);
                            }
                        } else if ("qr_login_camera".equals(tL_jsonObjectValue.key)) {
                            TLRPC.JSONValue jSONValue5 = tL_jsonObjectValue.value;
                            if ((jSONValue5 instanceof TLRPC.TL_jsonBool) && (z = ((TLRPC.TL_jsonBool) jSONValue5).value) != this.qrLoginCamera) {
                                this.qrLoginCamera = z;
                                edit.putBoolean("qrLoginCamera", this.qrLoginCamera);
                            }
                        }
                    }
                    z4 = true;
                    z5 = true;
                }
                z4 = true;
            }
            if (z4) {
                edit.commit();
            }
            if (z5) {
                ApplicationLoader.startPushService();
                ConnectionsManager connectionsManager = getConnectionsManager();
                connectionsManager.setPushConnectionEnabled(connectionsManager.isPushConnectionEnabled());
            }
        }
        this.loadingAppConfig = false;
    }

    public void updateConfig(TLRPC.TL_config tL_config) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_config) {
            private final /* synthetic */ TLRPC.TL_config f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$updateConfig$6$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$updateConfig$6$MessagesController(TLRPC.TL_config tL_config) {
        String str;
        getDownloadController().loadAutoDownloadConfig(false);
        loadAppConfig();
        this.maxMegagroupCount = tL_config.megagroup_size_max;
        this.maxGroupCount = tL_config.chat_size_max;
        this.maxEditTime = tL_config.edit_time_limit;
        this.ratingDecay = tL_config.rating_e_decay;
        this.maxRecentGifsCount = tL_config.saved_gifs_limit;
        this.maxRecentStickersCount = tL_config.stickers_recent_limit;
        this.maxFaveStickersCount = tL_config.stickers_faved_limit;
        this.revokeTimeLimit = tL_config.revoke_time_limit;
        this.revokeTimePmLimit = tL_config.revoke_pm_time_limit;
        this.canRevokePmInbox = tL_config.revoke_pm_inbox;
        this.linkPrefix = tL_config.me_url_prefix;
        if (this.linkPrefix.endsWith("/")) {
            String str2 = this.linkPrefix;
            this.linkPrefix = str2.substring(0, str2.length() - 1);
        }
        if (this.linkPrefix.startsWith("https://")) {
            this.linkPrefix = this.linkPrefix.substring(8);
        } else if (this.linkPrefix.startsWith("http://")) {
            this.linkPrefix = this.linkPrefix.substring(7);
        }
        this.callReceiveTimeout = tL_config.call_receive_timeout_ms;
        this.callRingTimeout = tL_config.call_ring_timeout_ms;
        this.callConnectTimeout = tL_config.call_connect_timeout_ms;
        this.callPacketTimeout = tL_config.call_packet_timeout_ms;
        this.maxPinnedDialogsCount = tL_config.pinned_dialogs_count_max;
        this.maxFolderPinnedDialogsCount = tL_config.pinned_infolder_count_max;
        this.maxMessageLength = tL_config.message_length_max;
        this.maxCaptionLength = tL_config.caption_length_max;
        this.preloadFeaturedStickers = tL_config.preload_featured_stickers;
        String str3 = tL_config.venue_search_username;
        if (str3 != null) {
            this.venueSearchBot = str3;
        }
        String str4 = tL_config.gif_search_username;
        if (str4 != null) {
            this.gifSearchBot = str4;
        }
        if (this.imageSearchBot != null) {
            this.imageSearchBot = tL_config.img_search_username;
        }
        this.blockedCountry = tL_config.blocked_mode;
        this.dcDomainName = tL_config.dc_txt_domain_name;
        this.webFileDatacenterId = tL_config.webfile_dc_id;
        String str5 = tL_config.suggested_lang_code;
        if (str5 != null && ((str = this.suggestedLangCode) == null || !str.equals(str5))) {
            this.suggestedLangCode = tL_config.suggested_lang_code;
            LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        }
        Theme.loadRemoteThemes(this.currentAccount, false);
        Theme.checkCurrentRemoteTheme(false);
        if (tL_config.static_maps_provider == null) {
            tL_config.static_maps_provider = "telegram";
        }
        this.mapKey = null;
        this.mapProvider = 2;
        this.availableMapProviders = 0;
        FileLog.d("map providers = " + tL_config.static_maps_provider);
        String[] split = tL_config.static_maps_provider.split(",");
        for (int i = 0; i < split.length; i++) {
            String[] split2 = split[i].split("\\+");
            if (split2.length > 0) {
                String[] split3 = split2[0].split(":");
                if (split3.length > 0) {
                    if ("yandex".equals(split3[0])) {
                        if (i == 0) {
                            if (split2.length > 1) {
                                this.mapProvider = 3;
                            } else {
                                this.mapProvider = 1;
                            }
                        }
                        this.availableMapProviders |= 4;
                    } else if ("google".equals(split3[0])) {
                        if (i == 0 && split2.length > 1) {
                            this.mapProvider = 4;
                        }
                        this.availableMapProviders |= 1;
                    } else if ("telegram".equals(split3[0])) {
                        if (i == 0) {
                            this.mapProvider = 2;
                        }
                        this.availableMapProviders |= 2;
                    }
                    if (split3.length > 1) {
                        this.mapKey = split3[1];
                    }
                }
            }
        }
        SharedPreferences.Editor edit = this.mainPreferences.edit();
        edit.putInt("maxGroupCount", this.maxGroupCount);
        edit.putInt("maxMegagroupCount", this.maxMegagroupCount);
        edit.putInt("maxEditTime", this.maxEditTime);
        edit.putInt("ratingDecay", this.ratingDecay);
        edit.putInt("maxRecentGifsCount", this.maxRecentGifsCount);
        edit.putInt("maxRecentStickersCount", this.maxRecentStickersCount);
        edit.putInt("maxFaveStickersCount", this.maxFaveStickersCount);
        edit.putInt("callReceiveTimeout", this.callReceiveTimeout);
        edit.putInt("callRingTimeout", this.callRingTimeout);
        edit.putInt("callConnectTimeout", this.callConnectTimeout);
        edit.putInt("callPacketTimeout", this.callPacketTimeout);
        edit.putString("linkPrefix", this.linkPrefix);
        edit.putInt("maxPinnedDialogsCount", this.maxPinnedDialogsCount);
        edit.putInt("maxFolderPinnedDialogsCount", this.maxFolderPinnedDialogsCount);
        edit.putInt("maxMessageLength", this.maxMessageLength);
        edit.putInt("maxCaptionLength", this.maxCaptionLength);
        edit.putBoolean("preloadFeaturedStickers", this.preloadFeaturedStickers);
        edit.putInt("revokeTimeLimit", this.revokeTimeLimit);
        edit.putInt("revokeTimePmLimit", this.revokeTimePmLimit);
        edit.putInt("mapProvider", this.mapProvider);
        String str6 = this.mapKey;
        if (str6 != null) {
            edit.putString("pk", str6);
        } else {
            edit.remove("pk");
        }
        edit.putBoolean("canRevokePmInbox", this.canRevokePmInbox);
        edit.putBoolean("blockedCountry", this.blockedCountry);
        edit.putString("venueSearchBot", this.venueSearchBot);
        edit.putString("gifSearchBot", this.gifSearchBot);
        edit.putString("imageSearchBot", this.imageSearchBot);
        edit.putString("dcDomainName2", this.dcDomainName);
        edit.putInt("webFileDatacenterId", this.webFileDatacenterId);
        edit.putString("suggestedLangCode", this.suggestedLangCode);
        edit.commit();
        LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(this.currentAccount, tL_config.lang_pack_version, tL_config.base_lang_pack_version);
        getNotificationCenter().postNotificationName(NotificationCenter.configLoaded, new Object[0]);
    }

    public void addSupportUser() {
        TLRPC.TL_userForeign_old2 tL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
        tL_userForeign_old2.phone = "333";
        tL_userForeign_old2.id = 333000;
        tL_userForeign_old2.first_name = "Telegram";
        tL_userForeign_old2.last_name = "";
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
        TLRPC.TL_userForeign_old2 tL_userForeign_old22 = new TLRPC.TL_userForeign_old2();
        tL_userForeign_old22.phone = "42777";
        tL_userForeign_old22.id = 777000;
        tL_userForeign_old22.verified = true;
        tL_userForeign_old22.first_name = "Telegram";
        tL_userForeign_old22.last_name = "Notifications";
        tL_userForeign_old22.status = null;
        tL_userForeign_old22.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old22, true);
    }

    public TLRPC.InputUser getInputUser(TLRPC.User user) {
        if (user == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (user.id == getUserConfig().getClientUserId()) {
            return new TLRPC.TL_inputUserSelf();
        }
        TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
        tL_inputUser.user_id = user.id;
        tL_inputUser.access_hash = user.access_hash;
        return tL_inputUser;
    }

    public TLRPC.InputUser getInputUser(int i) {
        return getInputUser(getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(i)));
    }

    public static TLRPC.InputChannel getInputChannel(TLRPC.Chat chat) {
        if (!(chat instanceof TLRPC.TL_channel) && !(chat instanceof TLRPC.TL_channelForbidden)) {
            return new TLRPC.TL_inputChannelEmpty();
        }
        TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
        tL_inputChannel.channel_id = chat.id;
        tL_inputChannel.access_hash = chat.access_hash;
        return tL_inputChannel;
    }

    public TLRPC.InputChannel getInputChannel(int i) {
        return getInputChannel(getChat(Integer.valueOf(i)));
    }

    public TLRPC.InputPeer getInputPeer(int i) {
        if (i < 0) {
            int i2 = -i;
            TLRPC.Chat chat = getChat(Integer.valueOf(i2));
            if (ChatObject.isChannel(chat)) {
                TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                tL_inputPeerChannel.channel_id = i2;
                tL_inputPeerChannel.access_hash = chat.access_hash;
                return tL_inputPeerChannel;
            }
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = i2;
            return tL_inputPeerChat;
        }
        TLRPC.User user = getUser(Integer.valueOf(i));
        TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
        tL_inputPeerUser.user_id = i;
        if (user == null) {
            return tL_inputPeerUser;
        }
        tL_inputPeerUser.access_hash = user.access_hash;
        return tL_inputPeerUser;
    }

    public TLRPC.Peer getPeer(int i) {
        if (i < 0) {
            int i2 = -i;
            TLRPC.Chat chat = getChat(Integer.valueOf(i2));
            if ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) {
                TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                tL_peerChannel.channel_id = i2;
                return tL_peerChannel;
            }
            TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
            tL_peerChat.chat_id = i2;
            return tL_peerChat;
        }
        getUser(Integer.valueOf(i));
        TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
        tL_peerUser.user_id = i;
        return tL_peerUser;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC.InputFile inputFile;
        Theme.ThemeAccent themeAccent;
        Theme.ThemeInfo themeInfo;
        TLRPC.InputFile inputFile2;
        TLRPC.TL_theme tL_theme;
        TLRPC.TL_inputThemeSettings tL_inputThemeSettings = null;
        if (i == NotificationCenter.FileDidUpload) {
            String str = objArr[0];
            TLRPC.InputFile inputFile3 = objArr[1];
            String str2 = this.uploadingAvatar;
            if (str2 == null || !str2.equals(str)) {
                String str3 = this.uploadingWallpaper;
                if (str3 == null || !str3.equals(str)) {
                    Object obj = this.uploadingThemes.get(str);
                    if (obj instanceof Theme.ThemeInfo) {
                        Theme.ThemeInfo themeInfo2 = (Theme.ThemeInfo) obj;
                        if (str.equals(themeInfo2.uploadingThumb)) {
                            themeInfo2.uploadedThumb = inputFile3;
                            themeInfo2.uploadingThumb = null;
                        } else if (str.equals(themeInfo2.uploadingFile)) {
                            themeInfo2.uploadedFile = inputFile3;
                            themeInfo2.uploadingFile = null;
                        }
                        inputFile = themeInfo2.uploadedThumb;
                        inputFile2 = themeInfo2.uploadedFile;
                        themeInfo = themeInfo2;
                        themeAccent = null;
                    } else if (obj instanceof Theme.ThemeAccent) {
                        Theme.ThemeAccent themeAccent2 = (Theme.ThemeAccent) obj;
                        if (str.equals(themeAccent2.uploadingThumb)) {
                            themeAccent2.uploadedThumb = inputFile3;
                            themeAccent2.uploadingThumb = null;
                        } else if (str.equals(themeAccent2.uploadingFile)) {
                            themeAccent2.uploadedFile = inputFile3;
                            themeAccent2.uploadingFile = null;
                        }
                        themeInfo = themeAccent2.parentTheme;
                        themeAccent = themeAccent2;
                        inputFile = themeAccent2.uploadedThumb;
                        inputFile2 = themeAccent2.uploadedFile;
                    } else {
                        inputFile = null;
                        inputFile2 = null;
                        themeInfo = null;
                        themeAccent = null;
                    }
                    this.uploadingThemes.remove(str);
                    if (inputFile2 != null && inputFile != null) {
                        new File(str);
                        TLRPC.TL_account_uploadTheme tL_account_uploadTheme = new TLRPC.TL_account_uploadTheme();
                        tL_account_uploadTheme.mime_type = "application/x-tgtheme-android";
                        tL_account_uploadTheme.file_name = "theme.attheme";
                        tL_account_uploadTheme.file = inputFile2;
                        tL_account_uploadTheme.file.name = "theme.attheme";
                        tL_account_uploadTheme.thumb = inputFile;
                        tL_account_uploadTheme.thumb.name = "theme-preview.jpg";
                        tL_account_uploadTheme.flags |= 1;
                        if (themeAccent != null) {
                            themeAccent.uploadedFile = null;
                            themeAccent.uploadedThumb = null;
                            tL_theme = themeAccent.info;
                            tL_inputThemeSettings = new TLRPC.TL_inputThemeSettings();
                            tL_inputThemeSettings.base_theme = Theme.getBaseThemeByKey(themeInfo.name);
                            tL_inputThemeSettings.accent_color = themeAccent.accentColor;
                            int i3 = themeAccent.myMessagesAccentColor;
                            if (i3 != 0) {
                                tL_inputThemeSettings.message_bottom_color = i3;
                                tL_inputThemeSettings.flags |= 1;
                            }
                            int i4 = themeAccent.myMessagesGradientAccentColor;
                            if (i4 != 0) {
                                tL_inputThemeSettings.message_top_color = i4;
                                tL_inputThemeSettings.flags |= 1;
                            } else {
                                int i5 = tL_inputThemeSettings.message_bottom_color;
                                if (i5 != 0) {
                                    tL_inputThemeSettings.message_top_color = i5;
                                }
                            }
                            tL_inputThemeSettings.flags |= 2;
                            tL_inputThemeSettings.wallpaper_settings = new TLRPC.TL_wallPaperSettings();
                            if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
                                TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                                tL_inputWallPaperSlug.slug = themeAccent.patternSlug;
                                tL_inputThemeSettings.wallpaper = tL_inputWallPaperSlug;
                                TLRPC.WallPaperSettings wallPaperSettings = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings.intensity = (int) (themeAccent.patternIntensity * 100.0f);
                                wallPaperSettings.flags |= 8;
                            } else {
                                tL_inputThemeSettings.wallpaper = new TLRPC.TL_inputWallPaperNoFile();
                            }
                            TLRPC.WallPaperSettings wallPaperSettings2 = tL_inputThemeSettings.wallpaper_settings;
                            wallPaperSettings2.motion = themeAccent.patternMotion;
                            long j = themeAccent.backgroundOverrideColor;
                            if (j != 0) {
                                wallPaperSettings2.background_color = (int) j;
                                wallPaperSettings2.flags |= 1;
                            }
                            long j2 = themeAccent.backgroundGradientOverrideColor;
                            if (j2 != 0) {
                                TLRPC.WallPaperSettings wallPaperSettings3 = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings3.second_background_color = (int) j2;
                                wallPaperSettings3.flags |= 16;
                                wallPaperSettings3.rotation = AndroidUtilities.getWallpaperRotation(themeAccent.backgroundRotation, true);
                            }
                        } else {
                            themeInfo.uploadedFile = null;
                            themeInfo.uploadedThumb = null;
                            tL_theme = themeInfo.info;
                        }
                        getConnectionsManager().sendRequest(tL_account_uploadTheme, new RequestDelegate(tL_theme, themeInfo, tL_inputThemeSettings, themeAccent) {
                            private final /* synthetic */ TLRPC.TL_theme f$1;
                            private final /* synthetic */ Theme.ThemeInfo f$2;
                            private final /* synthetic */ TLRPC.TL_inputThemeSettings f$3;
                            private final /* synthetic */ Theme.ThemeAccent f$4;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$didReceivedNotification$16$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                            }
                        });
                        return;
                    }
                    return;
                }
                TLRPC.TL_account_uploadWallPaper tL_account_uploadWallPaper = new TLRPC.TL_account_uploadWallPaper();
                tL_account_uploadWallPaper.file = inputFile3;
                tL_account_uploadWallPaper.mime_type = "image/jpeg";
                Theme.OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
                TLRPC.TL_wallPaperSettings tL_wallPaperSettings = new TLRPC.TL_wallPaperSettings();
                tL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
                tL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
                tL_account_uploadWallPaper.settings = tL_wallPaperSettings;
                getConnectionsManager().sendRequest(tL_account_uploadWallPaper, new RequestDelegate(overrideWallpaperInfo, tL_wallPaperSettings) {
                    private final /* synthetic */ Theme.OverrideWallpaperInfo f$1;
                    private final /* synthetic */ TLRPC.TL_wallPaperSettings f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$didReceivedNotification$10$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
            tL_photos_uploadProfilePhoto.file = inputFile3;
            getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$didReceivedNotification$8$MessagesController(tLObject, tL_error);
                }
            });
        } else if (i == NotificationCenter.FileDidFailUpload) {
            String str4 = objArr[0];
            String str5 = this.uploadingAvatar;
            if (str5 == null || !str5.equals(str4)) {
                String str6 = this.uploadingWallpaper;
                if (str6 == null || !str6.equals(str4)) {
                    Object remove = this.uploadingThemes.remove(str4);
                    if (remove instanceof Theme.ThemeInfo) {
                        Theme.ThemeInfo themeInfo3 = (Theme.ThemeInfo) remove;
                        themeInfo3.uploadedFile = null;
                        themeInfo3.uploadedThumb = null;
                        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo3, null);
                    } else if (remove instanceof Theme.ThemeAccent) {
                        Theme.ThemeAccent themeAccent3 = (Theme.ThemeAccent) remove;
                        themeAccent3.uploadingThumb = null;
                        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeAccent3.parentTheme, themeAccent3);
                    }
                } else {
                    this.uploadingWallpaper = null;
                    this.uploadingWallpaperInfo = null;
                }
            } else {
                this.uploadingAvatar = null;
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            if (!objArr[6].booleanValue()) {
                Integer num = objArr[0];
                Integer num2 = objArr[1];
                Long l = objArr[3];
                MessageObject messageObject = this.dialogMessage.get(l.longValue());
                if (messageObject != null && (messageObject.getId() == num.intValue() || messageObject.messageOwner.local_id == num.intValue())) {
                    messageObject.messageOwner.id = num2.intValue();
                    messageObject.messageOwner.send_state = 0;
                }
                TLRPC.Dialog dialog = this.dialogs_dict.get(l.longValue());
                if (dialog != null && dialog.top_message == num.intValue()) {
                    dialog.top_message = num2.intValue();
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
                MessageObject messageObject2 = this.dialogMessagesByIds.get(num.intValue());
                this.dialogMessagesByIds.remove(num.intValue());
                if (messageObject2 != null) {
                    this.dialogMessagesByIds.put(num2.intValue(), messageObject2);
                }
                int longValue = (int) l.longValue();
                if (longValue < 0) {
                    int i6 = -longValue;
                    TLRPC.ChatFull chatFull = this.fullChats.get(i6);
                    TLRPC.Chat chat = getChat(Integer.valueOf(i6));
                    if (chat != null && !ChatObject.hasAdminRights(chat) && chatFull != null && chatFull.slowmode_seconds != 0) {
                        chatFull.slowmode_next_send_date = getConnectionsManager().getCurrentTime() + chatFull.slowmode_seconds;
                        chatFull.flags |= 262144;
                        getMessagesStorage().updateChatInfo(chatFull, false);
                    }
                }
            }
        } else if (i == NotificationCenter.updateMessageMedia) {
            TLRPC.Message message = objArr[0];
            MessageObject messageObject3 = this.dialogMessagesByIds.get(message.id);
            if (messageObject3 != null) {
                messageObject3.messageOwner.media = message.media;
                TLRPC.MessageMedia messageMedia = message.media;
                if (messageMedia.ttl_seconds == 0) {
                    return;
                }
                if ((messageMedia.photo instanceof TLRPC.TL_photoEmpty) || (messageMedia.document instanceof TLRPC.TL_documentEmpty)) {
                    messageObject3.setType();
                    getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                }
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$8$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.User user = getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                putUser(user, true);
            } else {
                getUserConfig().setCurrentUser(user);
            }
            if (user != null) {
                TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
                ArrayList<TLRPC.PhotoSize> arrayList = tL_photos_photo.photo.sizes;
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
                user.photo = new TLRPC.TL_userProfilePhoto();
                TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
                userProfilePhoto.photo_id = tL_photos_photo.photo.id;
                if (closestPhotoSizeWithSize != null) {
                    userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
                }
                if (closestPhotoSizeWithSize2 != null) {
                    user.photo.photo_big = closestPhotoSizeWithSize2.location;
                } else if (closestPhotoSizeWithSize != null) {
                    user.photo.photo_small = closestPhotoSizeWithSize.location;
                }
                getMessagesStorage().clearUserPhotos(user.id);
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(user);
                getMessagesStorage().putUsersAndChats(arrayList2, (ArrayList<TLRPC.Chat>) null, false, true);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$null$7$MessagesController();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$null$7$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 2);
        getUserConfig().saveConfig(true);
    }

    public /* synthetic */ void lambda$didReceivedNotification$10$MessagesController(Theme.OverrideWallpaperInfo overrideWallpaperInfo, TLRPC.TL_wallPaperSettings tL_wallPaperSettings, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tLObject;
        File file = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
        if (tL_wallPaper != null) {
            try {
                AndroidUtilities.copyFile(file, FileLoader.getPathToAttach(tL_wallPaper.document, true));
            } catch (Exception unused) {
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(tL_wallPaper, tL_wallPaperSettings, overrideWallpaperInfo, file) {
            private final /* synthetic */ TLRPC.TL_wallPaper f$1;
            private final /* synthetic */ TLRPC.TL_wallPaperSettings f$2;
            private final /* synthetic */ Theme.OverrideWallpaperInfo f$3;
            private final /* synthetic */ File f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MessagesController.this.lambda$null$9$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$MessagesController(TLRPC.TL_wallPaper tL_wallPaper, TLRPC.TL_wallPaperSettings tL_wallPaperSettings, Theme.OverrideWallpaperInfo overrideWallpaperInfo, File file) {
        if (this.uploadingWallpaper != null && tL_wallPaper != null) {
            tL_wallPaper.settings = tL_wallPaperSettings;
            tL_wallPaper.flags |= 4;
            overrideWallpaperInfo.slug = tL_wallPaper.slug;
            overrideWallpaperInfo.saveOverrideWallpaper();
            ArrayList arrayList = new ArrayList();
            arrayList.add(tL_wallPaper);
            getMessagesStorage().putWallpapers(arrayList, 2);
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 320);
            if (closestPhotoSizeWithSize != null) {
                ImageLoader.getInstance().replaceImageInCache(Utilities.MD5(file.getAbsolutePath()) + "@100_100", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tL_wallPaper.document), false);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersNeedReload, tL_wallPaper.slug);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$16$MessagesController(TLRPC.TL_theme tL_theme, Theme.ThemeInfo themeInfo, TLRPC.TL_inputThemeSettings tL_inputThemeSettings, Theme.ThemeAccent themeAccent, TLObject tLObject, TLRPC.TL_error tL_error) {
        String name = tL_theme != null ? tL_theme.title : themeInfo.getName();
        int lastIndexOf = name.lastIndexOf(".attheme");
        if (lastIndexOf > 0) {
            name = name.substring(0, lastIndexOf);
        }
        if (tLObject != null) {
            TLRPC.Document document = (TLRPC.Document) tLObject;
            TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
            tL_inputDocument.access_hash = document.access_hash;
            tL_inputDocument.id = document.id;
            tL_inputDocument.file_reference = document.file_reference;
            if (tL_theme == null || !tL_theme.creator) {
                TLRPC.TL_account_createTheme tL_account_createTheme = new TLRPC.TL_account_createTheme();
                tL_account_createTheme.document = tL_inputDocument;
                tL_account_createTheme.flags |= 4;
                tL_account_createTheme.slug = (tL_theme == null || TextUtils.isEmpty(tL_theme.slug)) ? "" : tL_theme.slug;
                tL_account_createTheme.title = name;
                if (tL_inputThemeSettings != null) {
                    tL_account_createTheme.settings = tL_inputThemeSettings;
                    tL_account_createTheme.flags |= 8;
                }
                getConnectionsManager().sendRequest(tL_account_createTheme, new RequestDelegate(themeInfo, themeAccent) {
                    private final /* synthetic */ Theme.ThemeInfo f$1;
                    private final /* synthetic */ Theme.ThemeAccent f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$null$12$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_account_updateTheme tL_account_updateTheme = new TLRPC.TL_account_updateTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            tL_account_updateTheme.theme = tL_inputTheme;
            tL_account_updateTheme.slug = tL_theme.slug;
            tL_account_updateTheme.flags |= 1;
            tL_account_updateTheme.title = name;
            tL_account_updateTheme.flags |= 2;
            tL_account_updateTheme.document = tL_inputDocument;
            tL_account_updateTheme.flags |= 4;
            if (tL_inputThemeSettings != null) {
                tL_account_updateTheme.settings = tL_inputThemeSettings;
                tL_account_updateTheme.flags |= 8;
            }
            tL_account_updateTheme.format = "android";
            getConnectionsManager().sendRequest(tL_account_updateTheme, new RequestDelegate(themeInfo, themeAccent) {
                private final /* synthetic */ Theme.ThemeInfo f$1;
                private final /* synthetic */ Theme.ThemeAccent f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$null$14$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(themeInfo, themeAccent) {
            private final /* synthetic */ Theme.ThemeInfo f$1;
            private final /* synthetic */ Theme.ThemeAccent f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$null$15$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$12$MessagesController(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo, themeAccent) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ Theme.ThemeInfo f$2;
            private final /* synthetic */ Theme.ThemeAccent f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$null$11$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$11$MessagesController(TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        if (tLObject instanceof TLRPC.TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TLRPC.TL_theme) tLObject, this.currentAccount, false);
            installTheme(themeInfo, themeAccent, themeInfo == Theme.getCurrentNightTheme());
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public /* synthetic */ void lambda$null$14$MessagesController(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo, themeAccent) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ Theme.ThemeInfo f$2;
            private final /* synthetic */ Theme.ThemeAccent f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$null$13$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$13$MessagesController(TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        if (tLObject instanceof TLRPC.TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TLRPC.TL_theme) tLObject, this.currentAccount, false);
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public /* synthetic */ void lambda$null$15$MessagesController(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public void cleanup() {
        getContactsController().cleanup();
        MediaController.getInstance().cleanup();
        getNotificationsController().cleanup();
        getSendMessagesHelper().cleanup();
        getSecretChatHelper().cleanup();
        getLocationController().cleanup();
        getMediaDataController().cleanup();
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).putLong("lastStickersLoadTimeMask", 0).putLong("lastStickersLoadTimeFavs", 0).commit();
        this.mainPreferences.edit().remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").remove("dcDomainName2").remove("webFileDatacenterId").remove("themehint").commit();
        this.lastScheduledServerQueryTime.clear();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.reloadingScheduledWebpages.clear();
        this.reloadingScheduledWebpagesPending.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.loadingPinnedDialogs.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsers.clear();
        this.fullChats.clear();
        this.dialogsByFolder.clear();
        this.unreadUnmutedDialogs = 0;
        this.joiningToChannels.clear();
        this.migratedChats.clear();
        this.channelViewsToSend.clear();
        this.pollsToCheck.clear();
        this.pollsToCheckSize = 0;
        this.dialogsServerOnly.clear();
        this.dialogsForward.clear();
        this.allDialogs.clear();
        this.dialogsCanAddUsers.clear();
        this.dialogsChannelsOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsUsersOnly.clear();
        this.dialogMessagesByIds.clear();
        this.dialogMessagesByRandomIds.clear();
        this.channelAdmins.clear();
        this.loadingChannelAdmins.clear();
        this.users.clear();
        this.objectsByUsernames.clear();
        this.chats.clear();
        this.dialogMessage.clear();
        this.deletedHistory.clear();
        this.printingUsers.clear();
        this.printingStrings.clear();
        this.printingStringsTypes.clear();
        this.onlinePrivacy.clear();
        this.loadingPeerSettings.clear();
        this.deletingDialogs.clear();
        this.clearingHistoryDialogs.clear();
        this.lastPrintingStringCount = 0;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                MessagesController.this.lambda$cleanup$17$MessagesController();
            }
        });
        this.createdDialogMainThreadIds.clear();
        this.visibleDialogMainThreadIds.clear();
        this.visibleScheduledDialogMainThreadIds.clear();
        this.blockedUsers.clear();
        this.sendingTypings.clear();
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.dialogsLoaded = false;
        this.nextDialogsCacheOffset.clear();
        this.loadingDialogs.clear();
        this.dialogsEndReached.clear();
        this.serverDialogsEndReached.clear();
        this.loadingAppConfig = false;
        this.checkingTosUpdate = false;
        this.nextTosCheckTime = 0;
        this.nextProxyInfoCheckTime = 0;
        this.checkingProxyInfo = false;
        this.loadingUnreadDialogs = false;
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskChannelId = 0;
        this.gettingNewDeleteTask = false;
        this.loadingBlockedUsers = false;
        this.totalBlockedCount = -1;
        this.blockedEndReached = false;
        this.firstGettingTask = false;
        this.updatingState = false;
        this.resetingDialogs = false;
        this.lastStatusUpdateTime = 0;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.uploadingWallpaper = null;
        this.uploadingWallpaperInfo = null;
        this.uploadingThemes.clear();
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                MessagesController.this.lambda$cleanup$18$MessagesController();
            }
        });
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$cleanup$17$MessagesController() {
        this.readTasks.clear();
        this.readTasksMap.clear();
        this.updatesQueueSeq.clear();
        this.updatesQueuePts.clear();
        this.updatesQueueQts.clear();
        this.gettingUnknownChannels.clear();
        this.gettingUnknownDialogs.clear();
        this.updatesStartWaitTimeSeq = 0;
        this.updatesStartWaitTimePts = 0;
        this.updatesStartWaitTimeQts = 0;
        this.createdDialogIds.clear();
        this.createdScheduledDialogIds.clear();
        this.gettingDifference = false;
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
    }

    public /* synthetic */ void lambda$cleanup$18$MessagesController() {
        getConnectionsManager().setIsUpdating(false);
        this.updatesQueueChannels.clear();
        this.updatesStartWaitTimeChannels.clear();
        this.gettingDifferenceChannels.clear();
        this.channelsPts.clear();
        this.shortPollChannels.clear();
        this.needShortPollChannels.clear();
        this.shortPollOnlines.clear();
        this.needShortPollOnlines.clear();
    }

    public TLRPC.User getUser(Integer num) {
        return this.users.get(num);
    }

    public TLObject getUserOrChat(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        return this.objectsByUsernames.get(str.toLowerCase());
    }

    public ConcurrentHashMap<Integer, TLRPC.User> getUsers() {
        return this.users;
    }

    public ConcurrentHashMap<Integer, TLRPC.Chat> getChats() {
        return this.chats;
    }

    public TLRPC.Chat getChat(Integer num) {
        return this.chats.get(num);
    }

    public TLRPC.EncryptedChat getEncryptedChat(Integer num) {
        return this.encryptedChats.get(num);
    }

    public TLRPC.EncryptedChat getEncryptedChatDB(int i, boolean z) {
        TLRPC.EncryptedChat encryptedChat = this.encryptedChats.get(Integer.valueOf(i));
        if (encryptedChat != null) {
            if (!z) {
                return encryptedChat;
            }
            if (!(encryptedChat instanceof TLRPC.TL_encryptedChatWaiting) && !(encryptedChat instanceof TLRPC.TL_encryptedChatRequested)) {
                return encryptedChat;
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList arrayList = new ArrayList();
        getMessagesStorage().getEncryptedChat(i, countDownLatch, arrayList);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (arrayList.size() != 2) {
            return encryptedChat;
        }
        TLRPC.EncryptedChat encryptedChat2 = (TLRPC.EncryptedChat) arrayList.get(0);
        putEncryptedChat(encryptedChat2, false);
        putUser((TLRPC.User) arrayList.get(1), true);
        return encryptedChat2;
    }

    public boolean isDialogVisible(long j, boolean z) {
        return (z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds).contains(Long.valueOf(j));
    }

    public void setLastVisibleDialogId(long j, boolean z, boolean z2) {
        ArrayList<Long> arrayList = z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds;
        if (!z2) {
            arrayList.remove(Long.valueOf(j));
        } else if (!arrayList.contains(Long.valueOf(j))) {
            arrayList.add(Long.valueOf(j));
        }
    }

    public void setLastCreatedDialogId(long j, boolean z, boolean z2) {
        if (!z) {
            ArrayList<Long> arrayList = this.createdDialogMainThreadIds;
            if (!z2) {
                arrayList.remove(Long.valueOf(j));
                SparseArray sparseArray = this.pollsToCheck.get(j);
                if (sparseArray != null) {
                    int size = sparseArray.size();
                    for (int i = 0; i < size; i++) {
                        ((MessageObject) sparseArray.valueAt(i)).pollVisibleOnScreen = false;
                    }
                }
            } else if (!arrayList.contains(Long.valueOf(j))) {
                arrayList.add(Long.valueOf(j));
            } else {
                return;
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable(z, z2, j) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$setLastCreatedDialogId$19$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$setLastCreatedDialogId$19$MessagesController(boolean z, boolean z2, long j) {
        ArrayList<Long> arrayList = z ? this.createdScheduledDialogIds : this.createdDialogIds;
        if (!z2) {
            arrayList.remove(Long.valueOf(j));
        } else if (!arrayList.contains(Long.valueOf(j))) {
            arrayList.add(Long.valueOf(j));
        }
    }

    public TLRPC.ExportedChatInvite getExportedInvite(int i) {
        return this.exportedChats.get(i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c0, code lost:
        r6 = r6.status;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c4, code lost:
        r7 = r2.status;
     */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0028 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean putUser(org.telegram.tgnet.TLRPC.User r6, boolean r7) {
        /*
            r5 = this;
            r0 = 0
            if (r6 != 0) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 1
            if (r7 == 0) goto L_0x0016
            int r7 = r6.id
            int r2 = r7 / 1000
            r3 = 333(0x14d, float:4.67E-43)
            if (r2 == r3) goto L_0x0016
            r2 = 777000(0xbdb28, float:1.088809E-39)
            if (r7 == r2) goto L_0x0016
            r7 = 1
            goto L_0x0017
        L_0x0016:
            r7 = 0
        L_0x0017:
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r2 = r5.users
            int r3 = r6.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            java.lang.Object r2 = r2.get(r3)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            if (r2 != r6) goto L_0x0028
            return r0
        L_0x0028:
            if (r2 == 0) goto L_0x003d
            java.lang.String r3 = r2.username
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x003d
            java.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.tgnet.TLObject> r3 = r5.objectsByUsernames
            java.lang.String r4 = r2.username
            java.lang.String r4 = r4.toLowerCase()
            r3.remove(r4)
        L_0x003d:
            java.lang.String r3 = r6.username
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0050
            java.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.tgnet.TLObject> r3 = r5.objectsByUsernames
            java.lang.String r4 = r6.username
            java.lang.String r4 = r4.toLowerCase()
            r3.put(r4, r6)
        L_0x0050:
            boolean r3 = r6.min
            r4 = 0
            if (r3 == 0) goto L_0x0097
            if (r2 == 0) goto L_0x008a
            if (r7 != 0) goto L_0x011c
            boolean r7 = r6.bot
            if (r7 == 0) goto L_0x0072
            java.lang.String r7 = r6.username
            if (r7 == 0) goto L_0x006a
            r2.username = r7
            int r7 = r2.flags
            r7 = r7 | 8
            r2.flags = r7
            goto L_0x0072
        L_0x006a:
            int r7 = r2.flags
            r7 = r7 & -9
            r2.flags = r7
            r2.username = r4
        L_0x0072:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r6.photo
            if (r6 == 0) goto L_0x0080
            r2.photo = r6
            int r6 = r2.flags
            r6 = r6 | 32
            r2.flags = r6
            goto L_0x011c
        L_0x0080:
            int r6 = r2.flags
            r6 = r6 & -33
            r2.flags = r6
            r2.photo = r4
            goto L_0x011c
        L_0x008a:
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r7 = r5.users
            int r1 = r6.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r7.put(r1, r6)
            goto L_0x011c
        L_0x0097:
            if (r7 != 0) goto L_0x00cf
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r7 = r5.users
            int r3 = r6.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r7.put(r3, r6)
            int r7 = r6.id
            org.telegram.messenger.UserConfig r3 = r5.getUserConfig()
            int r3 = r3.getClientUserId()
            if (r7 != r3) goto L_0x00be
            org.telegram.messenger.UserConfig r7 = r5.getUserConfig()
            r7.setCurrentUser(r6)
            org.telegram.messenger.UserConfig r7 = r5.getUserConfig()
            r7.saveConfig(r1)
        L_0x00be:
            if (r2 == 0) goto L_0x011c
            org.telegram.tgnet.TLRPC$UserStatus r6 = r6.status
            if (r6 == 0) goto L_0x011c
            org.telegram.tgnet.TLRPC$UserStatus r7 = r2.status
            if (r7 == 0) goto L_0x011c
            int r6 = r6.expires
            int r7 = r7.expires
            if (r6 == r7) goto L_0x011c
            return r1
        L_0x00cf:
            if (r2 != 0) goto L_0x00dd
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r7 = r5.users
            int r1 = r6.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r7.put(r1, r6)
            goto L_0x011c
        L_0x00dd:
            boolean r7 = r2.min
            if (r7 == 0) goto L_0x011c
            r6.min = r0
            boolean r7 = r2.bot
            if (r7 == 0) goto L_0x00fc
            java.lang.String r7 = r2.username
            if (r7 == 0) goto L_0x00f4
            r6.username = r7
            int r7 = r6.flags
            r7 = r7 | 8
            r6.flags = r7
            goto L_0x00fc
        L_0x00f4:
            int r7 = r6.flags
            r7 = r7 & -9
            r6.flags = r7
            r6.username = r4
        L_0x00fc:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r2.photo
            if (r7 == 0) goto L_0x0109
            r6.photo = r7
            int r7 = r6.flags
            r7 = r7 | 32
            r6.flags = r7
            goto L_0x0111
        L_0x0109:
            int r7 = r6.flags
            r7 = r7 & -33
            r6.flags = r7
            r6.photo = r4
        L_0x0111:
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r7 = r5.users
            int r1 = r6.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r7.put(r1, r6)
        L_0x011c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.putUser(org.telegram.tgnet.TLRPC$User, boolean):boolean");
    }

    public void putUsers(ArrayList<TLRPC.User> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            boolean z2 = false;
            for (int i = 0; i < size; i++) {
                if (putUser(arrayList.get(i), z)) {
                    z2 = true;
                }
            }
            if (z2) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$putUsers$20$MessagesController();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$putUsers$20$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 4);
    }

    public void putChat(TLRPC.Chat chat, boolean z) {
        TLRPC.Chat chat2;
        if (chat != null && (chat2 = this.chats.get(Integer.valueOf(chat.id))) != chat) {
            if (chat2 != null && !TextUtils.isEmpty(chat2.username)) {
                this.objectsByUsernames.remove(chat2.username.toLowerCase());
            }
            if (!TextUtils.isEmpty(chat.username)) {
                this.objectsByUsernames.put(chat.username.toLowerCase(), chat);
            }
            if (!chat.min) {
                int i = 0;
                if (!z) {
                    if (chat2 != null) {
                        if (chat.version != chat2.version) {
                            this.loadedFullChats.remove(Integer.valueOf(chat.id));
                        }
                        int i2 = chat2.participants_count;
                        if (i2 != 0 && chat.participants_count == 0) {
                            chat.participants_count = i2;
                            chat.flags |= 131072;
                        }
                        TLRPC.TL_chatBannedRights tL_chatBannedRights = chat2.banned_rights;
                        int i3 = tL_chatBannedRights != null ? tL_chatBannedRights.flags : 0;
                        TLRPC.TL_chatBannedRights tL_chatBannedRights2 = chat.banned_rights;
                        int i4 = tL_chatBannedRights2 != null ? tL_chatBannedRights2.flags : 0;
                        TLRPC.TL_chatBannedRights tL_chatBannedRights3 = chat2.default_banned_rights;
                        int i5 = tL_chatBannedRights3 != null ? tL_chatBannedRights3.flags : 0;
                        TLRPC.TL_chatBannedRights tL_chatBannedRights4 = chat.default_banned_rights;
                        if (tL_chatBannedRights4 != null) {
                            i = tL_chatBannedRights4.flags;
                        }
                        chat2.default_banned_rights = chat.default_banned_rights;
                        if (chat2.default_banned_rights == null) {
                            chat2.flags &= -262145;
                        } else {
                            chat2.flags = 262144 | chat2.flags;
                        }
                        chat2.banned_rights = chat.banned_rights;
                        if (chat2.banned_rights == null) {
                            chat2.flags &= -32769;
                        } else {
                            chat2.flags = 32768 | chat2.flags;
                        }
                        chat2.admin_rights = chat.admin_rights;
                        if (chat2.admin_rights == null) {
                            chat2.flags &= -16385;
                        } else {
                            chat2.flags |= 16384;
                        }
                        if (!(i3 == i4 && i5 == i)) {
                            AndroidUtilities.runOnUIThread(new Runnable(chat) {
                                private final /* synthetic */ TLRPC.Chat f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    MessagesController.this.lambda$putChat$21$MessagesController(this.f$1);
                                }
                            });
                        }
                    }
                    this.chats.put(Integer.valueOf(chat.id), chat);
                } else if (chat2 == null) {
                    this.chats.put(Integer.valueOf(chat.id), chat);
                } else if (chat2.min) {
                    chat.min = false;
                    chat.title = chat2.title;
                    chat.photo = chat2.photo;
                    chat.broadcast = chat2.broadcast;
                    chat.verified = chat2.verified;
                    chat.megagroup = chat2.megagroup;
                    TLRPC.TL_chatBannedRights tL_chatBannedRights5 = chat2.default_banned_rights;
                    if (tL_chatBannedRights5 != null) {
                        chat.default_banned_rights = tL_chatBannedRights5;
                        chat.flags |= 262144;
                    }
                    TLRPC.TL_chatAdminRights tL_chatAdminRights = chat2.admin_rights;
                    if (tL_chatAdminRights != null) {
                        chat.admin_rights = tL_chatAdminRights;
                        chat.flags |= 16384;
                    }
                    TLRPC.TL_chatBannedRights tL_chatBannedRights6 = chat2.banned_rights;
                    if (tL_chatBannedRights6 != null) {
                        chat.banned_rights = tL_chatBannedRights6;
                        chat.flags |= 32768;
                    }
                    String str = chat2.username;
                    if (str != null) {
                        chat.username = str;
                        chat.flags |= 64;
                    } else {
                        chat.flags &= -65;
                        chat.username = null;
                    }
                    int i6 = chat2.participants_count;
                    if (i6 != 0 && chat.participants_count == 0) {
                        chat.participants_count = i6;
                        chat.flags |= 131072;
                    }
                    this.chats.put(Integer.valueOf(chat.id), chat);
                }
            } else if (chat2 == null) {
                this.chats.put(Integer.valueOf(chat.id), chat);
            } else if (!z) {
                chat2.title = chat.title;
                chat2.photo = chat.photo;
                chat2.broadcast = chat.broadcast;
                chat2.verified = chat.verified;
                chat2.megagroup = chat.megagroup;
                TLRPC.TL_chatBannedRights tL_chatBannedRights7 = chat.default_banned_rights;
                if (tL_chatBannedRights7 != null) {
                    chat2.default_banned_rights = tL_chatBannedRights7;
                    chat2.flags |= 262144;
                }
                TLRPC.TL_chatAdminRights tL_chatAdminRights2 = chat.admin_rights;
                if (tL_chatAdminRights2 != null) {
                    chat2.admin_rights = tL_chatAdminRights2;
                    chat2.flags |= 16384;
                }
                TLRPC.TL_chatBannedRights tL_chatBannedRights8 = chat.banned_rights;
                if (tL_chatBannedRights8 != null) {
                    chat2.banned_rights = tL_chatBannedRights8;
                    chat2.flags |= 32768;
                }
                String str2 = chat.username;
                if (str2 != null) {
                    chat2.username = str2;
                    chat2.flags |= 64;
                } else {
                    chat2.flags &= -65;
                    chat2.username = null;
                }
                int i7 = chat.participants_count;
                if (i7 != 0) {
                    chat2.participants_count = i7;
                }
            }
        }
    }

    public /* synthetic */ void lambda$putChat$21$MessagesController(TLRPC.Chat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    public void putChats(ArrayList<TLRPC.Chat> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                putChat(arrayList.get(i), z);
            }
        }
    }

    public void setReferer(String str) {
        if (str != null) {
            this.installReferer = str;
            this.mainPreferences.edit().putString("installReferer", str).commit();
        }
    }

    public void putEncryptedChat(TLRPC.EncryptedChat encryptedChat, boolean z) {
        if (encryptedChat != null) {
            if (z) {
                this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
            } else {
                this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            }
        }
    }

    public void putEncryptedChats(ArrayList<TLRPC.EncryptedChat> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                putEncryptedChat(arrayList.get(i), z);
            }
        }
    }

    public TLRPC.UserFull getUserFull(int i) {
        return this.fullUsers.get(i);
    }

    public TLRPC.ChatFull getChatFull(int i) {
        return this.fullChats.get(i);
    }

    public void cancelLoadFullUser(int i) {
        this.loadingFullUsers.remove(Integer.valueOf(i));
    }

    public void cancelLoadFullChat(int i) {
        this.loadingFullChats.remove(Integer.valueOf(i));
    }

    /* access modifiers changed from: protected */
    public void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<TLRPC.Dialog> arrayList, long j) {
        if (j != 0 || (arrayList != null && !arrayList.isEmpty())) {
            TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    TLRPC.InputPeer inputPeer = getInputPeer((int) arrayList.get(i).id);
                    if (!(inputPeer instanceof TLRPC.TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                        tL_inputDialogPeer.peer = inputPeer;
                        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                    }
                }
            } else {
                TLRPC.InputPeer inputPeer2 = getInputPeer((int) j);
                if (!(inputPeer2 instanceof TLRPC.TL_inputPeerChannel) || inputPeer2.access_hash != 0) {
                    TLRPC.TL_inputDialogPeer tL_inputDialogPeer2 = new TLRPC.TL_inputDialogPeer();
                    tL_inputDialogPeer2.peer = inputPeer2;
                    tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer2);
                } else {
                    return;
                }
            }
            if (!tL_messages_getPeerDialogs.peers.isEmpty()) {
                getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$reloadDialogsReadValue$22$MessagesController(tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$reloadDialogsReadValue$22$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tL_messages_peerDialogs.dialogs.size(); i++) {
                TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(i);
                if (dialog.read_inbox_max_id == 0) {
                    dialog.read_inbox_max_id = 1;
                }
                if (dialog.read_outbox_max_id == 0) {
                    dialog.read_outbox_max_id = 1;
                }
                DialogObject.initDialog(dialog);
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TLRPC.TL_updateReadChannelInbox tL_updateReadChannelInbox = new TLRPC.TL_updateReadChannelInbox();
                        tL_updateReadChannelInbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelInbox.max_id = dialog.read_inbox_max_id;
                        arrayList.add(tL_updateReadChannelInbox);
                    } else {
                        TLRPC.TL_updateReadHistoryInbox tL_updateReadHistoryInbox = new TLRPC.TL_updateReadHistoryInbox();
                        tL_updateReadHistoryInbox.peer = dialog.peer;
                        tL_updateReadHistoryInbox.max_id = dialog.read_inbox_max_id;
                        arrayList.add(tL_updateReadHistoryInbox);
                    }
                }
                Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (num2 == null) {
                    num2 = 0;
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, num2.intValue())));
                if (num2.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TLRPC.TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TLRPC.TL_updateReadChannelOutbox();
                        tL_updateReadChannelOutbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelOutbox.max_id = dialog.read_outbox_max_id;
                        arrayList.add(tL_updateReadChannelOutbox);
                    } else {
                        TLRPC.TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox = new TLRPC.TL_updateReadHistoryOutbox();
                        tL_updateReadHistoryOutbox.peer = dialog.peer;
                        tL_updateReadHistoryOutbox.max_id = dialog.read_outbox_max_id;
                        arrayList.add(tL_updateReadHistoryOutbox);
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                processUpdateArray(arrayList, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
            }
        }
    }

    public String getAdminRank(int i, int i2) {
        SparseArray sparseArray = this.channelAdmins.get(i);
        if (sparseArray == null) {
            return null;
        }
        return (String) sparseArray.get(i2);
    }

    public boolean isChannelAdminsLoaded(int i) {
        return this.channelAdmins.get(i) != null;
    }

    public void loadChannelAdmins(int i, boolean z) {
        if (SystemClock.elapsedRealtime() - ((long) this.loadingChannelAdmins.get(i)) >= 60) {
            this.loadingChannelAdmins.put(i, (int) (SystemClock.elapsedRealtime() / 1000));
            if (z) {
                getMessagesStorage().loadChannelAdmins(i);
                return;
            }
            TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
            tL_channels_getParticipants.channel = getInputChannel(i);
            tL_channels_getParticipants.limit = 100;
            tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
            getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadChannelAdmins$23$MessagesController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadChannelAdmins$23$MessagesController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_channels_channelParticipants) {
            processLoadedAdminsResponse(i, (TLRPC.TL_channels_channelParticipants) tLObject);
        }
    }

    public void processLoadedAdminsResponse(int i, TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants) {
        SparseArray sparseArray = new SparseArray(tL_channels_channelParticipants.participants.size());
        for (int i2 = 0; i2 < tL_channels_channelParticipants.participants.size(); i2++) {
            TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipants.participants.get(i2);
            int i3 = channelParticipant.user_id;
            String str = channelParticipant.rank;
            if (str == null) {
                str = "";
            }
            sparseArray.put(i3, str);
        }
        processLoadedChannelAdmins(sparseArray, i, false);
    }

    public void processLoadedChannelAdmins(SparseArray<String> sparseArray, int i, boolean z) {
        if (!z) {
            getMessagesStorage().putChannelAdmins(i, sparseArray);
        }
        AndroidUtilities.runOnUIThread(new Runnable(i, sparseArray, z) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ SparseArray f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$processLoadedChannelAdmins$24$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedChannelAdmins$24$MessagesController(int i, SparseArray sparseArray, boolean z) {
        this.channelAdmins.put(i, sparseArray);
        if (z) {
            this.loadingChannelAdmins.delete(i);
            loadChannelAdmins(i, false);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFullChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v8, resolved type: org.telegram.tgnet.TLRPC$TL_channels_getFullChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFullChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFullChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFullChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFullChat} */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0066, code lost:
        if (r9.dialogs_read_outbox_max.get(java.lang.Long.valueOf(r4)) == null) goto L_0x0068;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadFullChat(int r10, int r11, boolean r12) {
        /*
            r9 = this;
            java.util.ArrayList<java.lang.Integer> r0 = r9.loadedFullChats
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            boolean r0 = r0.contains(r1)
            java.util.ArrayList<java.lang.Integer> r1 = r9.loadingFullChats
            java.lang.Integer r2 = java.lang.Integer.valueOf(r10)
            boolean r1 = r1.contains(r2)
            if (r1 != 0) goto L_0x0086
            if (r12 != 0) goto L_0x001b
            if (r0 == 0) goto L_0x001b
            goto L_0x0086
        L_0x001b:
            java.util.ArrayList<java.lang.Integer> r12 = r9.loadingFullChats
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r12.add(r1)
            int r12 = -r10
            long r4 = (long) r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r3 = r9.getChat(r12)
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r12 == 0) goto L_0x0049
            org.telegram.tgnet.TLRPC$TL_channels_getFullChannel r12 = new org.telegram.tgnet.TLRPC$TL_channels_getFullChannel
            r12.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = getInputChannel((org.telegram.tgnet.TLRPC.Chat) r3)
            r12.channel = r1
            boolean r1 = r3.megagroup
            if (r1 == 0) goto L_0x006c
            r0 = r0 ^ 1
            r9.loadChannelAdmins(r10, r0)
            goto L_0x006c
        L_0x0049:
            org.telegram.tgnet.TLRPC$TL_messages_getFullChat r12 = new org.telegram.tgnet.TLRPC$TL_messages_getFullChat
            r12.<init>()
            r12.chat_id = r10
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r9.dialogs_read_inbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r4)
            java.lang.Object r0 = r0.get(r1)
            if (r0 == 0) goto L_0x0068
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r9.dialogs_read_outbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r4)
            java.lang.Object r0 = r0.get(r1)
            if (r0 != 0) goto L_0x006c
        L_0x0068:
            r0 = 0
            r9.reloadDialogsReadValue(r0, r4)
        L_0x006c:
            org.telegram.tgnet.ConnectionsManager r0 = r9.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$VAJW75r5ALAtTw6hMiEJQY4ufUc r8 = new org.telegram.messenger.-$$Lambda$MessagesController$VAJW75r5ALAtTw6hMiEJQY4ufUc
            r1 = r8
            r2 = r9
            r6 = r10
            r7 = r11
            r1.<init>(r3, r4, r6, r7)
            int r10 = r0.sendRequest(r12, r8)
            if (r11 == 0) goto L_0x0086
            org.telegram.tgnet.ConnectionsManager r12 = r9.getConnectionsManager()
            r12.bindRequestToGuid(r10, r11)
        L_0x0086:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.loadFullChat(int, int, boolean):void");
    }

    public /* synthetic */ void lambda$loadFullChat$27$MessagesController(TLRPC.Chat chat, long j, int i, int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        long j2 = j;
        int i3 = i;
        TLRPC.TL_error tL_error2 = tL_error;
        if (tL_error2 == null) {
            TLRPC.TL_messages_chatFull tL_messages_chatFull = (TLRPC.TL_messages_chatFull) tLObject;
            getMessagesStorage().putUsersAndChats(tL_messages_chatFull.users, tL_messages_chatFull.chats, true, true);
            getMessagesStorage().updateChatInfo(tL_messages_chatFull.full_chat, false);
            if (ChatObject.isChannel(chat)) {
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j));
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    ArrayList arrayList = new ArrayList();
                    TLRPC.TL_updateReadChannelInbox tL_updateReadChannelInbox = new TLRPC.TL_updateReadChannelInbox();
                    tL_updateReadChannelInbox.channel_id = i3;
                    tL_updateReadChannelInbox.max_id = tL_messages_chatFull.full_chat.read_inbox_max_id;
                    arrayList.add(tL_updateReadChannelInbox);
                    processUpdateArray(arrayList, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
                }
                Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(j));
                if (num2 == null) {
                    num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_outbox_max_id, num2.intValue())));
                if (num2.intValue() == 0) {
                    ArrayList arrayList2 = new ArrayList();
                    TLRPC.TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TLRPC.TL_updateReadChannelOutbox();
                    tL_updateReadChannelOutbox.channel_id = i3;
                    tL_updateReadChannelOutbox.max_id = tL_messages_chatFull.full_chat.read_outbox_max_id;
                    arrayList2.add(tL_updateReadChannelOutbox);
                    processUpdateArray(arrayList2, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(i3, tL_messages_chatFull, i2) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLRPC.TL_messages_chatFull f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MessagesController.this.lambda$null$25$MessagesController(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tL_error2, i3) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$null$26$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$25$MessagesController(int i, TLRPC.TL_messages_chatFull tL_messages_chatFull, int i2) {
        this.fullChats.put(i, tL_messages_chatFull.full_chat);
        applyDialogNotificationsSettings((long) (-i), tL_messages_chatFull.full_chat.notify_settings);
        for (int i3 = 0; i3 < tL_messages_chatFull.full_chat.bot_info.size(); i3++) {
            getMediaDataController().putBotInfo(tL_messages_chatFull.full_chat.bot_info.get(i3));
        }
        this.exportedChats.put(i, tL_messages_chatFull.full_chat.exported_invite);
        this.loadingFullChats.remove(Integer.valueOf(i));
        this.loadedFullChats.add(Integer.valueOf(i));
        putUsers(tL_messages_chatFull.users, false);
        putChats(tL_messages_chatFull.chats, false);
        if (tL_messages_chatFull.full_chat.stickerset != null) {
            getMediaDataController().getGroupStickerSetById(tL_messages_chatFull.full_chat.stickerset);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, tL_messages_chatFull.full_chat, Integer.valueOf(i2), false, null);
    }

    public /* synthetic */ void lambda$null$26$MessagesController(TLRPC.TL_error tL_error, int i) {
        checkChannelError(tL_error.text, i);
        this.loadingFullChats.remove(Integer.valueOf(i));
    }

    public void loadFullUser(TLRPC.User user, int i, boolean z) {
        if (user != null && !this.loadingFullUsers.contains(Integer.valueOf(user.id))) {
            if (z || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                TLRPC.TL_users_getFullUser tL_users_getFullUser = new TLRPC.TL_users_getFullUser();
                tL_users_getFullUser.id = getInputUser(user);
                long j = (long) user.id;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(j)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j)) == null) {
                    reloadDialogsReadValue((ArrayList<TLRPC.Dialog>) null, j);
                }
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_users_getFullUser, new RequestDelegate(user, i) {
                    private final /* synthetic */ TLRPC.User f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadFullUser$30$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                }), i);
            }
        }
    }

    public /* synthetic */ void lambda$loadFullUser$30$MessagesController(TLRPC.User user, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.UserFull userFull = (TLRPC.UserFull) tLObject;
            getMessagesStorage().updateUserInfo(userFull, false);
            AndroidUtilities.runOnUIThread(new Runnable(userFull, user, i) {
                private final /* synthetic */ TLRPC.UserFull f$1;
                private final /* synthetic */ TLRPC.User f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MessagesController.this.lambda$null$28$MessagesController(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(user) {
            private final /* synthetic */ TLRPC.User f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$29$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$28$MessagesController(TLRPC.UserFull userFull, TLRPC.User user, int i) {
        savePeerSettings((long) userFull.user.id, userFull.settings, false);
        applyDialogNotificationsSettings((long) user.id, userFull.notify_settings);
        if (userFull.bot_info instanceof TLRPC.TL_botInfo) {
            getMediaDataController().putBotInfo(userFull.bot_info);
        }
        int indexOfKey = this.blockedUsers.indexOfKey(user.id);
        if (userFull.blocked) {
            if (indexOfKey < 0) {
                this.blockedUsers.put(user.id, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (indexOfKey >= 0) {
            this.blockedUsers.removeAt(indexOfKey);
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.fullUsers.put(user.id, userFull);
        this.loadingFullUsers.remove(Integer.valueOf(user.id));
        this.loadedFullUsers.add(Integer.valueOf(user.id));
        String str = user.first_name + user.last_name + user.username;
        ArrayList arrayList = new ArrayList();
        arrayList.add(userFull.user);
        putUsers(arrayList, false);
        getMessagesStorage().putUsersAndChats(arrayList, (ArrayList<TLRPC.Chat>) null, false, true);
        if (str != null) {
            if (!str.equals(userFull.user.first_name + userFull.user.last_name + userFull.user.username)) {
                getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1);
            }
        }
        if (userFull.bot_info instanceof TLRPC.TL_botInfo) {
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, userFull.bot_info, Integer.valueOf(i));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.id), userFull, null);
    }

    public /* synthetic */ void lambda$null$29$MessagesController(TLRPC.User user) {
        this.loadingFullUsers.remove(Integer.valueOf(user.id));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMessages} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_channels_getMessages} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMessages} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMessages} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void reloadMessages(java.util.ArrayList<java.lang.Integer> r10, long r11, boolean r13) {
        /*
            r9 = this;
            boolean r0 = r10.isEmpty()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            int r0 = r9.currentAccount
            org.telegram.tgnet.TLRPC$Chat r5 = org.telegram.messenger.ChatObject.getChatByDialog(r11, r0)
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r0 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$TL_channels_getMessages r0 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = getInputChannel((org.telegram.tgnet.TLRPC.Chat) r5)
            r0.channel = r1
            r0.id = r7
            goto L_0x002d
        L_0x0026:
            org.telegram.tgnet.TLRPC$TL_messages_getMessages r0 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages
            r0.<init>()
            r0.id = r7
        L_0x002d:
            android.util.LongSparseArray<java.util.ArrayList<java.lang.Integer>> r1 = r9.reloadingMessages
            java.lang.Object r1 = r1.get(r11)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            r2 = 0
        L_0x0036:
            int r3 = r10.size()
            if (r2 >= r3) goto L_0x0051
            java.lang.Object r3 = r10.get(r2)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r1 == 0) goto L_0x004b
            boolean r4 = r1.contains(r3)
            if (r4 == 0) goto L_0x004b
            goto L_0x004e
        L_0x004b:
            r7.add(r3)
        L_0x004e:
            int r2 = r2 + 1
            goto L_0x0036
        L_0x0051:
            boolean r10 = r7.isEmpty()
            if (r10 == 0) goto L_0x0058
            return
        L_0x0058:
            if (r1 != 0) goto L_0x0064
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            android.util.LongSparseArray<java.util.ArrayList<java.lang.Integer>> r10 = r9.reloadingMessages
            r10.put(r11, r1)
        L_0x0064:
            r1.addAll(r7)
            org.telegram.tgnet.ConnectionsManager r10 = r9.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$Liv50krJ6cv1Bd4MsaAD3ZIs3gY r8 = new org.telegram.messenger.-$$Lambda$MessagesController$Liv50krJ6cv1Bd4MsaAD3ZIs3gY
            r1 = r8
            r2 = r9
            r3 = r11
            r6 = r13
            r1.<init>(r3, r5, r6, r7)
            r10.sendRequest(r0, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.reloadMessages(java.util.ArrayList, long, boolean):void");
    }

    public /* synthetic */ void lambda$reloadMessages$32$MessagesController(long j, TLRPC.Chat chat, boolean z, ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        long j2 = j;
        TLRPC.Chat chat2 = chat;
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < messages_messages.users.size(); i++) {
                TLRPC.User user = messages_messages.users.get(i);
                sparseArray.put(user.id, user);
            }
            SparseArray sparseArray2 = new SparseArray();
            for (int i2 = 0; i2 < messages_messages.chats.size(); i2++) {
                TLRPC.Chat chat3 = messages_messages.chats.get(i2);
                sparseArray2.put(chat3.id, chat3);
            }
            Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(j));
            if (num == null) {
                num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j2));
                this.dialogs_read_inbox_max.put(Long.valueOf(j), num);
            }
            Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num2 == null) {
                num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j2));
                this.dialogs_read_outbox_max.put(Long.valueOf(j), num2);
            }
            Integer num3 = num2;
            ArrayList arrayList2 = new ArrayList();
            int i3 = 0;
            while (i3 < messages_messages.messages.size()) {
                TLRPC.Message message = messages_messages.messages.get(i3);
                if (chat2 != null && chat2.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                message.dialog_id = j2;
                if (!z) {
                    message.unread = (message.out ? num3 : num).intValue() < message.id;
                }
                MessageObject messageObject = r9;
                ArrayList arrayList3 = arrayList2;
                MessageObject messageObject2 = new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) sparseArray, (SparseArray<TLRPC.Chat>) sparseArray2, true);
                arrayList3.add(messageObject);
                i3++;
                arrayList2 = arrayList3;
            }
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            getMessagesStorage().putMessages(messages_messages, j, -1, 0, false, z);
            AndroidUtilities.runOnUIThread(new Runnable(j, arrayList, arrayList2) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void run() {
                    MessagesController.this.lambda$null$31$MessagesController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$31$MessagesController(long j, ArrayList arrayList, ArrayList arrayList2) {
        ArrayList arrayList3 = this.reloadingMessages.get(j);
        if (arrayList3 != null) {
            arrayList3.removeAll(arrayList);
            if (arrayList3.isEmpty()) {
                this.reloadingMessages.remove(j);
            }
        }
        MessageObject messageObject = this.dialogMessage.get(j);
        if (messageObject != null) {
            int i = 0;
            while (true) {
                if (i >= arrayList2.size()) {
                    break;
                }
                MessageObject messageObject2 = (MessageObject) arrayList2.get(i);
                if (messageObject == null || messageObject.getId() != messageObject2.getId()) {
                    i++;
                } else {
                    this.dialogMessage.put(j, messageObject2);
                    if (messageObject2.messageOwner.to_id.channel_id == 0) {
                        MessageObject messageObject3 = this.dialogMessagesByIds.get(messageObject2.getId());
                        this.dialogMessagesByIds.remove(messageObject2.getId());
                        if (messageObject3 != null) {
                            this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                        }
                    }
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList2);
    }

    public void hidePeerSettingsBar(long j, TLRPC.User user, TLRPC.Chat chat) {
        if (user != null || chat != null) {
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            edit.putInt("dialog_bar_vis3" + j, 3);
            edit.commit();
            if (((int) j) != 0) {
                TLRPC.TL_messages_hidePeerSettingsBar tL_messages_hidePeerSettingsBar = new TLRPC.TL_messages_hidePeerSettingsBar();
                if (user != null) {
                    tL_messages_hidePeerSettingsBar.peer = getInputPeer(user.id);
                } else if (chat != null) {
                    tL_messages_hidePeerSettingsBar.peer = getInputPeer(-chat.id);
                }
                getConnectionsManager().sendRequest(tL_messages_hidePeerSettingsBar, $$Lambda$MessagesController$jQptSvsMXJJ6kN0kNQTGJMVx9hQ.INSTANCE);
            }
        }
    }

    public void reportSpam(long j, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, boolean z) {
        if (user != null || chat != null || encryptedChat != null) {
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            edit.putInt("dialog_bar_vis3" + j, 3);
            edit.commit();
            if (((int) j) != 0) {
                TLRPC.TL_account_reportPeer tL_account_reportPeer = new TLRPC.TL_account_reportPeer();
                if (chat != null) {
                    tL_account_reportPeer.peer = getInputPeer(-chat.id);
                } else if (user != null) {
                    tL_account_reportPeer.peer = getInputPeer(user.id);
                }
                if (z) {
                    tL_account_reportPeer.reason = new TLRPC.TL_inputReportReasonGeoIrrelevant();
                } else {
                    tL_account_reportPeer.reason = new TLRPC.TL_inputReportReasonSpam();
                }
                getConnectionsManager().sendRequest(tL_account_reportPeer, $$Lambda$MessagesController$r40aIljP9XXEZHDSJKpMdZBFBYA.INSTANCE, 2);
            } else if (encryptedChat != null && encryptedChat.access_hash != 0) {
                TLRPC.TL_messages_reportEncryptedSpam tL_messages_reportEncryptedSpam = new TLRPC.TL_messages_reportEncryptedSpam();
                tL_messages_reportEncryptedSpam.peer = new TLRPC.TL_inputEncryptedChat();
                TLRPC.TL_inputEncryptedChat tL_inputEncryptedChat = tL_messages_reportEncryptedSpam.peer;
                tL_inputEncryptedChat.chat_id = encryptedChat.id;
                tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
                getConnectionsManager().sendRequest(tL_messages_reportEncryptedSpam, $$Lambda$MessagesController$Hs86dTNrkWNjJ_kpoVItJmnx3wk.INSTANCE, 2);
            }
        }
    }

    private void savePeerSettings(long j, TLRPC.TL_peerSettings tL_peerSettings, boolean z) {
        if (tL_peerSettings != null) {
            SharedPreferences sharedPreferences = this.notificationsPreferences;
            if (sharedPreferences.getInt("dialog_bar_vis3" + j, 0) != 3) {
                SharedPreferences.Editor edit = this.notificationsPreferences.edit();
                boolean z2 = !tL_peerSettings.report_spam && !tL_peerSettings.add_contact && !tL_peerSettings.block_contact && !tL_peerSettings.share_contact && !tL_peerSettings.report_geo;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("peer settings loaded for " + j + " add = " + tL_peerSettings.add_contact + " block = " + tL_peerSettings.block_contact + " spam = " + tL_peerSettings.report_spam + " share = " + tL_peerSettings.share_contact + " geo = " + tL_peerSettings.report_geo + " hide = " + z2);
                }
                edit.putInt("dialog_bar_vis3" + j, z2 ? 1 : 2);
                edit.putBoolean("dialog_bar_share" + j, tL_peerSettings.share_contact);
                edit.putBoolean("dialog_bar_report" + j, tL_peerSettings.report_spam);
                edit.putBoolean("dialog_bar_add" + j, tL_peerSettings.add_contact);
                edit.putBoolean("dialog_bar_block" + j, tL_peerSettings.block_contact);
                edit.putBoolean("dialog_bar_exception" + j, tL_peerSettings.need_contacts_exception);
                edit.putBoolean("dialog_bar_location" + j, tL_peerSettings.report_geo);
                edit.commit();
                getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(j));
            }
        }
    }

    public void loadPeerSettings(TLRPC.User user, TLRPC.Chat chat) {
        int i;
        if (user != null || chat != null) {
            if (user != null) {
                i = user.id;
            } else {
                i = -chat.id;
            }
            long j = (long) i;
            if (this.loadingPeerSettings.indexOfKey(j) < 0) {
                this.loadingPeerSettings.put(j, true);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("request spam button for " + j);
                }
                SharedPreferences sharedPreferences = this.notificationsPreferences;
                int i2 = sharedPreferences.getInt("dialog_bar_vis3" + j, 0);
                if (i2 != 1 && i2 != 3) {
                    TLRPC.TL_messages_getPeerSettings tL_messages_getPeerSettings = new TLRPC.TL_messages_getPeerSettings();
                    if (user != null) {
                        tL_messages_getPeerSettings.peer = getInputPeer(user.id);
                    } else if (chat != null) {
                        tL_messages_getPeerSettings.peer = getInputPeer(-chat.id);
                    }
                    getConnectionsManager().sendRequest(tL_messages_getPeerSettings, new RequestDelegate(j) {
                        private final /* synthetic */ long f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$loadPeerSettings$37$MessagesController(this.f$1, tLObject, tL_error);
                        }
                    });
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("dialog bar already hidden for " + j);
                }
            }
        }
    }

    public /* synthetic */ void lambda$loadPeerSettings$37$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(j, tLObject) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$null$36$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$36$MessagesController(long j, TLObject tLObject) {
        this.loadingPeerSettings.remove(j);
        if (tLObject != null) {
            savePeerSettings(j, (TLRPC.TL_peerSettings) tLObject, false);
        }
    }

    /* access modifiers changed from: protected */
    public void processNewChannelDifferenceParams(int i, int i2, int i3) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewChannelDifferenceParams pts = " + i + " pts_count = " + i2 + " channeldId = " + i3);
        }
        int i4 = this.channelsPts.get(i3);
        if (i4 == 0) {
            i4 = getMessagesStorage().getChannelPtsSync(i3);
            if (i4 == 0) {
                i4 = 1;
            }
            this.channelsPts.put(i3, i4);
        }
        if (i4 + i2 == i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(i3, i);
            getMessagesStorage().saveChannelPts(i3, i);
        } else if (i4 != i) {
            long j = this.updatesStartWaitTimeChannels.get(i3);
            if (this.gettingDifferenceChannels.get(i3) || j == 0 || Math.abs(System.currentTimeMillis() - j) <= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ADD CHANNEL UPDATE TO QUEUE pts = " + i + " pts_count = " + i2);
                }
                if (j == 0) {
                    this.updatesStartWaitTimeChannels.put(i3, System.currentTimeMillis());
                }
                UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
                userActionUpdatesPts.pts = i;
                userActionUpdatesPts.pts_count = i2;
                userActionUpdatesPts.chat_id = i3;
                ArrayList arrayList = this.updatesQueueChannels.get(i3);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.updatesQueueChannels.put(i3, arrayList);
                }
                arrayList.add(userActionUpdatesPts);
                return;
            }
            getChannelDifference(i3);
        }
    }

    /* access modifiers changed from: protected */
    public void processNewDifferenceParams(int i, int i2, int i3, int i4) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewDifferenceParams seq = " + i + " pts = " + i2 + " date = " + i3 + " pts_count = " + i4);
        }
        if (i2 != -1) {
            if (getMessagesStorage().getLastPtsValue() + i4 == i2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY PTS");
                }
                getMessagesStorage().setLastPtsValue(i2);
                getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
            } else if (getMessagesStorage().getLastPtsValue() != i2) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("ADD UPDATE TO QUEUE pts = " + i2 + " pts_count = " + i4);
                    }
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
                    userActionUpdatesPts.pts = i2;
                    userActionUpdatesPts.pts_count = i4;
                    this.updatesQueuePts.add(userActionUpdatesPts);
                } else {
                    getDifference();
                }
            }
        }
        if (i == -1) {
            return;
        }
        if (getMessagesStorage().getLastSeqValue() + 1 == i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY SEQ");
            }
            getMessagesStorage().setLastSeqValue(i);
            if (i3 != -1) {
                getMessagesStorage().setLastDateValue(i3);
            }
            getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        } else if (getMessagesStorage().getLastSeqValue() == i) {
        } else {
            if (this.gettingDifference || this.updatesStartWaitTimeSeq == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ADD UPDATE TO QUEUE seq = " + i);
                }
                if (this.updatesStartWaitTimeSeq == 0) {
                    this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                UserActionUpdatesSeq userActionUpdatesSeq = new UserActionUpdatesSeq();
                userActionUpdatesSeq.seq = i;
                this.updatesQueueSeq.add(userActionUpdatesSeq);
                return;
            }
            getDifference();
        }
    }

    public void didAddedNewTask(int i, SparseArray<ArrayList<Long>> sparseArray) {
        Utilities.stageQueue.postRunnable(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$didAddedNewTask$38$MessagesController(this.f$1);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable(sparseArray) {
            private final /* synthetic */ SparseArray f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$didAddedNewTask$39$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$didAddedNewTask$38$MessagesController(int i) {
        int i2;
        if ((this.currentDeletingTaskMids == null && !this.gettingNewDeleteTask) || ((i2 = this.currentDeletingTaskTime) != 0 && i < i2)) {
            getNewDeleteTask((ArrayList<Integer>) null, 0);
        }
    }

    public /* synthetic */ void lambda$didAddedNewTask$39$MessagesController(SparseArray sparseArray) {
        getNotificationCenter().postNotificationName(NotificationCenter.didCreatedNewDeleteTask, sparseArray);
    }

    public void getNewDeleteTask(ArrayList<Integer> arrayList, int i) {
        Utilities.stageQueue.postRunnable(new Runnable(arrayList, i) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$getNewDeleteTask$40$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$getNewDeleteTask$40$MessagesController(ArrayList arrayList, int i) {
        this.gettingNewDeleteTask = true;
        getMessagesStorage().getNewTask(arrayList, i);
    }

    private boolean checkDeletingTask(boolean z) {
        int i;
        int currentTime = getConnectionsManager().getCurrentTime();
        if (this.currentDeletingTaskMids == null || (!z && ((i = this.currentDeletingTaskTime) == 0 || i > currentTime))) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (this.currentDeleteTaskRunnable != null && !z) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        AndroidUtilities.runOnUIThread(new Runnable(new ArrayList(this.currentDeletingTaskMids)) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$checkDeletingTask$42$MessagesController(this.f$1);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$checkDeletingTask$42$MessagesController(ArrayList arrayList) {
        if (arrayList.isEmpty() || ((Integer) arrayList.get(0)).intValue() <= 0) {
            deleteMessages(arrayList, (ArrayList<Long>) null, (TLRPC.EncryptedChat) null, 0, 0, false, false);
        } else {
            getMessagesStorage().emptyMessagesMedia(arrayList);
        }
        Utilities.stageQueue.postRunnable(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$41$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$41$MessagesController(ArrayList arrayList) {
        getNewDeleteTask(arrayList, this.currentDeletingTaskChannelId);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
    }

    public void processLoadedDeleteTask(int i, ArrayList<Integer> arrayList, int i2) {
        Utilities.stageQueue.postRunnable(new Runnable(arrayList, i) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$processLoadedDeleteTask$44$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedDeleteTask$44$MessagesController(ArrayList arrayList, int i) {
        this.gettingNewDeleteTask = false;
        if (arrayList != null) {
            this.currentDeletingTaskTime = i;
            this.currentDeletingTaskMids = arrayList;
            if (this.currentDeleteTaskRunnable != null) {
                Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
                this.currentDeleteTaskRunnable = null;
            }
            if (!checkDeletingTask(false)) {
                this.currentDeleteTaskRunnable = new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$null$43$MessagesController();
                    }
                };
                Utilities.stageQueue.postRunnable(this.currentDeleteTaskRunnable, ((long) Math.abs(getConnectionsManager().getCurrentTime() - this.currentDeletingTaskTime)) * 1000);
                return;
            }
            return;
        }
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
    }

    public /* synthetic */ void lambda$null$43$MessagesController() {
        checkDeletingTask(true);
    }

    public void loadDialogPhotos(int i, int i2, long j, boolean z, int i3) {
        if (z) {
            getMessagesStorage().getDialogPhotos(i, i2, j, i3);
        } else if (i > 0) {
            TLRPC.User user = getUser(Integer.valueOf(i));
            if (user != null) {
                TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
                tL_photos_getUserPhotos.limit = i2;
                tL_photos_getUserPhotos.offset = 0;
                tL_photos_getUserPhotos.max_id = (long) ((int) j);
                tL_photos_getUserPhotos.user_id = getInputUser(user);
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_photos_getUserPhotos, new RequestDelegate(i, i2, j, i3) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ long f$3;
                    private final /* synthetic */ int f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r6;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadDialogPhotos$45$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                    }
                }), i3);
            }
        } else if (i < 0) {
            TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
            tL_messages_search.limit = i2;
            tL_messages_search.offset_id = (int) j;
            tL_messages_search.q = "";
            tL_messages_search.peer = getInputPeer(i);
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate(i, i2, j, i3) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadDialogPhotos$46$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            }), i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$45$MessagesController(int i, int i2, long j, int i3, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processLoadedUserPhotos((TLRPC.photos_Photos) tLObject, i, i2, j, false, i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$46$MessagesController(int i, int i2, long j, int i3, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.Photo photo;
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            TLRPC.TL_photos_photos tL_photos_photos = new TLRPC.TL_photos_photos();
            tL_photos_photos.count = messages_messages.count;
            tL_photos_photos.users.addAll(messages_messages.users);
            for (int i4 = 0; i4 < messages_messages.messages.size(); i4++) {
                TLRPC.MessageAction messageAction = messages_messages.messages.get(i4).action;
                if (!(messageAction == null || (photo = messageAction.photo) == null)) {
                    tL_photos_photos.photos.add(photo);
                }
            }
            processLoadedUserPhotos(tL_photos_photos, i, i2, j, false, i3);
        }
    }

    public void blockUser(int i) {
        TLRPC.User user = getUser(Integer.valueOf(i));
        if (user != null && this.blockedUsers.indexOfKey(i) < 0) {
            this.blockedUsers.put(i, 1);
            if (user.bot) {
                getMediaDataController().removeInline(i);
            } else {
                getMediaDataController().removePeer(i);
            }
            int i2 = this.totalBlockedCount;
            if (i2 >= 0) {
                this.totalBlockedCount = i2 + 1;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            TLRPC.TL_contacts_block tL_contacts_block = new TLRPC.TL_contacts_block();
            tL_contacts_block.id = getInputUser(user);
            getConnectionsManager().sendRequest(tL_contacts_block, $$Lambda$MessagesController$9pXuu16zaRwP_GF6IvLmyU9Fgvg.INSTANCE);
        }
    }

    public void setUserBannedRole(int i, TLRPC.User user, TLRPC.TL_chatBannedRights tL_chatBannedRights, boolean z, BaseFragment baseFragment) {
        if (user != null && tL_chatBannedRights != null) {
            TLRPC.TL_channels_editBanned tL_channels_editBanned = new TLRPC.TL_channels_editBanned();
            tL_channels_editBanned.channel = getInputChannel(i);
            tL_channels_editBanned.user_id = getInputUser(user);
            tL_channels_editBanned.banned_rights = tL_chatBannedRights;
            getConnectionsManager().sendRequest(tL_channels_editBanned, new RequestDelegate(i, baseFragment, tL_channels_editBanned, z) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_channels_editBanned f$3;
                private final /* synthetic */ boolean f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$setUserBannedRole$50$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setUserBannedRole$50$MessagesController(int i, BaseFragment baseFragment, TLRPC.TL_channels_editBanned tL_channels_editBanned, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$48$MessagesController(this.f$1);
                }
            }, 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_channels_editBanned, z) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ BaseFragment f$2;
            private final /* synthetic */ TLRPC.TL_channels_editBanned f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MessagesController.this.lambda$null$49$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$48$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$49$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_editBanned tL_channels_editBanned, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editBanned, Boolean.valueOf(z));
    }

    public void setChannelSlowMode(int i, int i2) {
        TLRPC.TL_channels_toggleSlowMode tL_channels_toggleSlowMode = new TLRPC.TL_channels_toggleSlowMode();
        tL_channels_toggleSlowMode.seconds = i2;
        tL_channels_toggleSlowMode.channel = getInputChannel(i);
        getConnectionsManager().sendRequest(tL_channels_toggleSlowMode, new RequestDelegate(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$setChannelSlowMode$52$MessagesController(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setChannelSlowMode$52$MessagesController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$51$MessagesController(this.f$1);
                }
            }, 1000);
        }
    }

    public /* synthetic */ void lambda$null$51$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public void setDefaultBannedRole(int i, TLRPC.TL_chatBannedRights tL_chatBannedRights, boolean z, BaseFragment baseFragment) {
        if (tL_chatBannedRights != null) {
            TLRPC.TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights = new TLRPC.TL_messages_editChatDefaultBannedRights();
            tL_messages_editChatDefaultBannedRights.peer = getInputPeer(-i);
            tL_messages_editChatDefaultBannedRights.banned_rights = tL_chatBannedRights;
            getConnectionsManager().sendRequest(tL_messages_editChatDefaultBannedRights, new RequestDelegate(i, baseFragment, tL_messages_editChatDefaultBannedRights, z) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_messages_editChatDefaultBannedRights f$3;
                private final /* synthetic */ boolean f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$setDefaultBannedRole$55$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setDefaultBannedRole$55$MessagesController(int i, BaseFragment baseFragment, TLRPC.TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$53$MessagesController(this.f$1);
                }
            }, 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, z) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ BaseFragment f$2;
            private final /* synthetic */ TLRPC.TL_messages_editChatDefaultBannedRights f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MessagesController.this.lambda$null$54$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$53$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$54$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, Boolean.valueOf(z));
    }

    public void setUserAdminRole(int i, TLRPC.User user, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str, boolean z, BaseFragment baseFragment, boolean z2) {
        if (user != null && tL_chatAdminRights != null) {
            TLRPC.Chat chat = getChat(Integer.valueOf(i));
            if (ChatObject.isChannel(chat)) {
                TLRPC.TL_channels_editAdmin tL_channels_editAdmin = new TLRPC.TL_channels_editAdmin();
                tL_channels_editAdmin.channel = getInputChannel(chat);
                tL_channels_editAdmin.user_id = getInputUser(user);
                tL_channels_editAdmin.admin_rights = tL_chatAdminRights;
                tL_channels_editAdmin.rank = str;
                getConnectionsManager().sendRequest(tL_channels_editAdmin, new RequestDelegate(i, baseFragment, tL_channels_editAdmin, z) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ BaseFragment f$2;
                    private final /* synthetic */ TLRPC.TL_channels_editAdmin f$3;
                    private final /* synthetic */ boolean f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$setUserAdminRole$58$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin = new TLRPC.TL_messages_editChatAdmin();
            tL_messages_editChatAdmin.chat_id = i;
            tL_messages_editChatAdmin.user_id = getInputUser(user);
            tL_messages_editChatAdmin.is_admin = tL_chatAdminRights.change_info || tL_chatAdminRights.delete_messages || tL_chatAdminRights.ban_users || tL_chatAdminRights.invite_users || tL_chatAdminRights.pin_messages || tL_chatAdminRights.add_admins;
            $$Lambda$MessagesController$b56InizIt0I4FbnoQmxOOGkS67M r11 = new RequestDelegate(i, baseFragment, tL_messages_editChatAdmin) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_messages_editChatAdmin f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$setUserAdminRole$61$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            };
            if (!tL_messages_editChatAdmin.is_admin || !z2) {
                getConnectionsManager().sendRequest(tL_messages_editChatAdmin, r11);
            } else {
                addUserToChat(i, user, (TLRPC.ChatFull) null, 0, (String) null, baseFragment, new Runnable(tL_messages_editChatAdmin, r11) {
                    private final /* synthetic */ TLRPC.TL_messages_editChatAdmin f$1;
                    private final /* synthetic */ RequestDelegate f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MessagesController.this.lambda$setUserAdminRole$62$MessagesController(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$setUserAdminRole$58$MessagesController(int i, BaseFragment baseFragment, TLRPC.TL_channels_editAdmin tL_channels_editAdmin, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$56$MessagesController(this.f$1);
                }
            }, 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_channels_editAdmin, z) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ BaseFragment f$2;
            private final /* synthetic */ TLRPC.TL_channels_editAdmin f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MessagesController.this.lambda$null$57$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$56$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$57$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_editAdmin tL_channels_editAdmin, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editAdmin, Boolean.valueOf(z));
    }

    public /* synthetic */ void lambda$null$59$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$setUserAdminRole$61$MessagesController(int i, BaseFragment baseFragment, TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$59$MessagesController(this.f$1);
                }
            }, 1000);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_messages_editChatAdmin) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_messages_editChatAdmin f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MessagesController.this.lambda$null$60$MessagesController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$60$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatAdmin, false);
    }

    public /* synthetic */ void lambda$setUserAdminRole$62$MessagesController(TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(tL_messages_editChatAdmin, requestDelegate);
    }

    public void unblockUser(int i) {
        TLRPC.TL_contacts_unblock tL_contacts_unblock = new TLRPC.TL_contacts_unblock();
        TLRPC.User user = getUser(Integer.valueOf(i));
        if (user != null) {
            this.totalBlockedCount--;
            this.blockedUsers.delete(user.id);
            tL_contacts_unblock.id = getInputUser(user);
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            getConnectionsManager().sendRequest(tL_contacts_unblock, $$Lambda$MessagesController$hZMObZRDUapS7Alox_T46Sd5rlU.INSTANCE);
        }
    }

    public void getBlockedUsers(boolean z) {
        int i;
        if (getUserConfig().isClientActivated() && !this.loadingBlockedUsers) {
            this.loadingBlockedUsers = true;
            TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked = new TLRPC.TL_contacts_getBlocked();
            if (z) {
                i = 0;
            } else {
                i = this.blockedUsers.size();
            }
            tL_contacts_getBlocked.offset = i;
            tL_contacts_getBlocked.limit = z ? 20 : 100;
            getConnectionsManager().sendRequest(tL_contacts_getBlocked, new RequestDelegate(z, tL_contacts_getBlocked) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ TLRPC.TL_contacts_getBlocked f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$getBlockedUsers$65$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getBlockedUsers$65$MessagesController(boolean z, TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, z, tL_contacts_getBlocked) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLRPC.TL_contacts_getBlocked f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$null$64$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$64$MessagesController(TLObject tLObject, boolean z, TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked) {
        if (tLObject != null) {
            TLRPC.contacts_Blocked contacts_blocked = (TLRPC.contacts_Blocked) tLObject;
            putUsers(contacts_blocked.users, false);
            getMessagesStorage().putUsersAndChats(contacts_blocked.users, (ArrayList<TLRPC.Chat>) null, true, true);
            if (z) {
                this.blockedUsers.clear();
            }
            this.totalBlockedCount = Math.max(contacts_blocked.count, contacts_blocked.blocked.size());
            this.blockedEndReached = contacts_blocked.blocked.size() < tL_contacts_getBlocked.limit;
            int size = contacts_blocked.blocked.size();
            for (int i = 0; i < size; i++) {
                this.blockedUsers.put(contacts_blocked.blocked.get(i).user_id, 1);
            }
            this.loadingBlockedUsers = false;
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
    }

    public void deleteUserPhoto(TLRPC.InputPhoto inputPhoto) {
        if (inputPhoto == null) {
            TLRPC.TL_photos_updateProfilePhoto tL_photos_updateProfilePhoto = new TLRPC.TL_photos_updateProfilePhoto();
            tL_photos_updateProfilePhoto.id = new TLRPC.TL_inputPhotoEmpty();
            getUserConfig().getCurrentUser().photo = new TLRPC.TL_userProfilePhotoEmpty();
            TLRPC.User user = getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
            }
            if (user != null) {
                user.photo = getUserConfig().getCurrentUser().photo;
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1535);
                getConnectionsManager().sendRequest(tL_photos_updateProfilePhoto, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$deleteUserPhoto$67$MessagesController(tLObject, tL_error);
                    }
                });
                return;
            }
            return;
        }
        TLRPC.TL_photos_deletePhotos tL_photos_deletePhotos = new TLRPC.TL_photos_deletePhotos();
        tL_photos_deletePhotos.id.add(inputPhoto);
        getConnectionsManager().sendRequest(tL_photos_deletePhotos, $$Lambda$MessagesController$sAwCUX3IhR8N0CEEhE4RmQefts.INSTANCE);
    }

    public /* synthetic */ void lambda$deleteUserPhoto$67$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.User user = getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                putUser(user, false);
            } else {
                getUserConfig().setCurrentUser(user);
            }
            if (user != null) {
                getMessagesStorage().clearUserPhotos(user.id);
                ArrayList arrayList = new ArrayList();
                arrayList.add(user);
                getMessagesStorage().putUsersAndChats(arrayList, (ArrayList<TLRPC.Chat>) null, false, true);
                user.photo = (TLRPC.UserProfilePhoto) tLObject;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$null$66$MessagesController();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$null$66$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1535);
        getUserConfig().saveConfig(true);
    }

    public void processLoadedUserPhotos(TLRPC.photos_Photos photos_photos, int i, int i2, long j, boolean z, int i3) {
        if (!z) {
            getMessagesStorage().putUsersAndChats(photos_photos.users, (ArrayList<TLRPC.Chat>) null, true, true);
            getMessagesStorage().putDialogPhotos(i, photos_photos);
        } else if (photos_photos == null || photos_photos.photos.isEmpty()) {
            loadDialogPhotos(i, i2, j, false, i3);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(photos_photos, z, i, i2, i3) {
            private final /* synthetic */ TLRPC.photos_Photos f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MessagesController.this.lambda$processLoadedUserPhotos$69$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedUserPhotos$69$MessagesController(TLRPC.photos_Photos photos_photos, boolean z, int i, int i2, int i3) {
        putUsers(photos_photos.users, z);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z), Integer.valueOf(i3), photos_photos.photos);
    }

    public void uploadAndApplyUserAvatar(TLRPC.FileLocation fileLocation) {
        if (fileLocation != null) {
            this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + fileLocation.volume_id + "_" + fileLocation.local_id + ".jpg";
            getFileLoader().uploadFile(this.uploadingAvatar, false, true, 16777216);
        }
    }

    public void saveTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z, boolean z2) {
        TLRPC.TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        if (tL_theme != null) {
            TLRPC.TL_account_saveTheme tL_account_saveTheme = new TLRPC.TL_account_saveTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            tL_account_saveTheme.theme = tL_inputTheme;
            tL_account_saveTheme.unsave = z2;
            getConnectionsManager().sendRequest(tL_account_saveTheme, $$Lambda$MessagesController$WKM5a9edf5QSZ_85kx_7hVixAo.INSTANCE);
            getConnectionsManager().resumeNetworkMaybe();
        }
        if (!z2) {
            installTheme(themeInfo, themeAccent, z);
        }
    }

    public void installTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z) {
        TLRPC.TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        String str = themeAccent != null ? themeAccent.patternSlug : themeInfo.slug;
        boolean z2 = themeAccent == null && themeInfo.isBlured;
        boolean z3 = themeAccent != null ? themeAccent.patternMotion : themeInfo.isMotion;
        TLRPC.TL_account_installTheme tL_account_installTheme = new TLRPC.TL_account_installTheme();
        tL_account_installTheme.dark = z;
        if (tL_theme != null) {
            tL_account_installTheme.format = "android";
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            tL_account_installTheme.theme = tL_inputTheme;
            tL_account_installTheme.flags |= 2;
        }
        getConnectionsManager().sendRequest(tL_account_installTheme, $$Lambda$MessagesController$JU6Vn2XI6a6J78k6RJwedOdT0ek.INSTANCE);
        if (!TextUtils.isEmpty(str)) {
            TLRPC.TL_account_installWallPaper tL_account_installWallPaper = new TLRPC.TL_account_installWallPaper();
            TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
            tL_inputWallPaperSlug.slug = str;
            tL_account_installWallPaper.wallpaper = tL_inputWallPaperSlug;
            tL_account_installWallPaper.settings = new TLRPC.TL_wallPaperSettings();
            TLRPC.TL_wallPaperSettings tL_wallPaperSettings = tL_account_installWallPaper.settings;
            tL_wallPaperSettings.blur = z2;
            tL_wallPaperSettings.motion = z3;
            getConnectionsManager().sendRequest(tL_account_installWallPaper, $$Lambda$MessagesController$cHxeHBiTL_l8pWJucJD2ba8DkY.INSTANCE);
        }
    }

    public void saveThemeToServer(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        File file;
        String str;
        if (themeInfo != null) {
            if (themeAccent != null) {
                str = themeAccent.saveToFile().getAbsolutePath();
                file = themeAccent.getPathToWallpaper();
            } else {
                str = themeInfo.pathToFile;
                file = null;
            }
            String str2 = str;
            File file2 = file;
            if (str2 != null && !this.uploadingThemes.containsKey(str2)) {
                this.uploadingThemes.put(str2, themeAccent != null ? themeAccent : themeInfo);
                Utilities.globalQueue.postRunnable(new Runnable(str2, file2, themeAccent, themeInfo) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ File f$2;
                    private final /* synthetic */ Theme.ThemeAccent f$3;
                    private final /* synthetic */ Theme.ThemeInfo f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        MessagesController.this.lambda$saveThemeToServer$74$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$saveThemeToServer$74$MessagesController(String str, File file, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo) {
        AndroidUtilities.runOnUIThread(new Runnable(Theme.createThemePreviewImage(str, file != null ? file.getAbsolutePath() : null), str, themeAccent, themeInfo) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ Theme.ThemeAccent f$3;
            private final /* synthetic */ Theme.ThemeInfo f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MessagesController.this.lambda$null$73$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [org.telegram.ui.ActionBar.Theme$ThemeInfo] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$73$MessagesController(java.lang.String r3, java.lang.String r4, org.telegram.ui.ActionBar.Theme.ThemeAccent r5, org.telegram.ui.ActionBar.Theme.ThemeInfo r6) {
        /*
            r2 = this;
            if (r3 != 0) goto L_0x0008
            java.util.HashMap<java.lang.String, java.lang.Object> r3 = r2.uploadingThemes
            r3.remove(r4)
            return
        L_0x0008:
            java.util.HashMap<java.lang.String, java.lang.Object> r0 = r2.uploadingThemes
            if (r5 == 0) goto L_0x000e
            r1 = r5
            goto L_0x000f
        L_0x000e:
            r1 = r6
        L_0x000f:
            r0.put(r3, r1)
            if (r5 != 0) goto L_0x0019
            r6.uploadingFile = r4
            r6.uploadingThumb = r3
            goto L_0x001d
        L_0x0019:
            r5.uploadingFile = r4
            r5.uploadingThumb = r3
        L_0x001d:
            org.telegram.messenger.FileLoader r5 = r2.getFileLoader()
            r6 = 67108864(0x4000000, float:1.5046328E-36)
            r0 = 1
            r1 = 0
            r5.uploadFile(r4, r1, r0, r6)
            org.telegram.messenger.FileLoader r4 = r2.getFileLoader()
            r5 = 16777216(0x1000000, float:2.3509887E-38)
            r4.uploadFile(r3, r1, r0, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$null$73$MessagesController(java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeAccent, org.telegram.ui.ActionBar.Theme$ThemeInfo):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.tgnet.TLRPC$TL_account_saveWallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_account_installWallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_account_saveWallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.tgnet.TLRPC$TL_account_saveWallPaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveWallpaperToServer(java.io.File r6, org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r7, boolean r8, long r9) {
        /*
            r5 = this;
            java.lang.String r0 = r5.uploadingWallpaper
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L_0x0035
            java.io.File r0 = new java.io.File
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.String r4 = r7.originalFileName
            r0.<init>(r3, r4)
            if (r6 == 0) goto L_0x0028
            java.lang.String r3 = r6.getAbsolutePath()
            java.lang.String r4 = r5.uploadingWallpaper
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x0025
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0028
        L_0x0025:
            r5.uploadingWallpaperInfo = r7
            return
        L_0x0028:
            org.telegram.messenger.FileLoader r0 = r5.getFileLoader()
            java.lang.String r3 = r5.uploadingWallpaper
            r0.cancelUploadFile(r3, r1)
            r5.uploadingWallpaper = r2
            r5.uploadingWallpaperInfo = r2
        L_0x0035:
            r0 = 1
            if (r6 == 0) goto L_0x004d
            java.lang.String r6 = r6.getAbsolutePath()
            r5.uploadingWallpaper = r6
            r5.uploadingWallpaperInfo = r7
            org.telegram.messenger.FileLoader r6 = r5.getFileLoader()
            java.lang.String r7 = r5.uploadingWallpaper
            r8 = 16777216(0x1000000, float:2.3509887E-38)
            r6.uploadFile(r7, r1, r0, r8)
            goto L_0x011a
        L_0x004d:
            boolean r6 = r7.isDefault()
            if (r6 != 0) goto L_0x011a
            boolean r6 = r7.isColor()
            if (r6 != 0) goto L_0x011a
            boolean r6 = r7.isTheme()
            if (r6 != 0) goto L_0x011a
            org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r6 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
            r6.<init>()
            java.lang.String r1 = r7.slug
            r6.slug = r1
            org.telegram.tgnet.TLRPC$TL_wallPaperSettings r1 = new org.telegram.tgnet.TLRPC$TL_wallPaperSettings
            r1.<init>()
            boolean r3 = r7.isBlurred
            r1.blur = r3
            boolean r3 = r7.isMotion
            r1.motion = r3
            int r3 = r7.color
            if (r3 == 0) goto L_0x008f
            r1.background_color = r3
            int r3 = r1.flags
            r3 = r3 | r0
            r1.flags = r3
            float r3 = r7.intensity
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            float r3 = r3 * r4
            int r3 = (int) r3
            r1.intensity = r3
            int r3 = r1.flags
            r3 = r3 | 8
            r1.flags = r3
        L_0x008f:
            int r3 = r7.gradientColor
            if (r3 == 0) goto L_0x00a3
            r1.second_background_color = r3
            int r3 = r7.rotation
            int r0 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r3, r0)
            r1.rotation = r0
            int r0 = r1.flags
            r0 = r0 | 16
            r1.flags = r0
        L_0x00a3:
            if (r8 == 0) goto L_0x00af
            org.telegram.tgnet.TLRPC$TL_account_installWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_installWallPaper
            r0.<init>()
            r0.wallpaper = r6
            r0.settings = r1
            goto L_0x00b8
        L_0x00af:
            org.telegram.tgnet.TLRPC$TL_account_saveWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_saveWallPaper
            r0.<init>()
            r0.wallpaper = r6
            r0.settings = r1
        L_0x00b8:
            r3 = 0
            int r6 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x00bf
            goto L_0x010e
        L_0x00bf:
            org.telegram.tgnet.NativeByteBuffer r6 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0101 }
            r9 = 1024(0x400, float:1.435E-42)
            r6.<init>((int) r9)     // Catch:{ Exception -> 0x0101 }
            r9 = 21
            r6.writeInt32(r9)     // Catch:{ Exception -> 0x00ff }
            boolean r9 = r7.isBlurred     // Catch:{ Exception -> 0x00ff }
            r6.writeBool(r9)     // Catch:{ Exception -> 0x00ff }
            boolean r9 = r7.isMotion     // Catch:{ Exception -> 0x00ff }
            r6.writeBool(r9)     // Catch:{ Exception -> 0x00ff }
            int r9 = r7.color     // Catch:{ Exception -> 0x00ff }
            r6.writeInt32(r9)     // Catch:{ Exception -> 0x00ff }
            int r9 = r7.gradientColor     // Catch:{ Exception -> 0x00ff }
            r6.writeInt32(r9)     // Catch:{ Exception -> 0x00ff }
            int r9 = r7.rotation     // Catch:{ Exception -> 0x00ff }
            r6.writeInt32(r9)     // Catch:{ Exception -> 0x00ff }
            float r9 = r7.intensity     // Catch:{ Exception -> 0x00ff }
            double r9 = (double) r9     // Catch:{ Exception -> 0x00ff }
            r6.writeDouble(r9)     // Catch:{ Exception -> 0x00ff }
            r6.writeBool(r8)     // Catch:{ Exception -> 0x00ff }
            java.lang.String r8 = r7.slug     // Catch:{ Exception -> 0x00ff }
            r6.writeString(r8)     // Catch:{ Exception -> 0x00ff }
            java.lang.String r7 = r7.originalFileName     // Catch:{ Exception -> 0x00ff }
            r6.writeString(r7)     // Catch:{ Exception -> 0x00ff }
            int r7 = r6.position()     // Catch:{ Exception -> 0x00ff }
            r6.limit(r7)     // Catch:{ Exception -> 0x00ff }
            goto L_0x0106
        L_0x00ff:
            r7 = move-exception
            goto L_0x0103
        L_0x0101:
            r7 = move-exception
            r6 = r2
        L_0x0103:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0106:
            org.telegram.messenger.MessagesStorage r7 = r5.getMessagesStorage()
            long r9 = r7.createPendingTask(r6)
        L_0x010e:
            org.telegram.tgnet.ConnectionsManager r6 = r5.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$Sq9QMUU7HOVWr8Dc5OGCzMKjLbk r7 = new org.telegram.messenger.-$$Lambda$MessagesController$Sq9QMUU7HOVWr8Dc5OGCzMKjLbk
            r7.<init>(r9)
            r6.sendRequest(r0, r7)
        L_0x011a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.saveWallpaperToServer(java.io.File, org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, boolean, long):void");
    }

    public /* synthetic */ void lambda$saveWallpaperToServer$75$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        getMessagesStorage().removePendingTask(j);
    }

    public void markChannelDialogMessageAsDeleted(ArrayList<Integer> arrayList, int i) {
        MessageObject messageObject = this.dialogMessage.get((long) (-i));
        if (messageObject != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (messageObject.getId() == arrayList.get(i2).intValue()) {
                    messageObject.deleted = true;
                    return;
                }
            }
        }
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC.EncryptedChat encryptedChat, long j, int i, boolean z, boolean z2) {
        deleteMessages(arrayList, arrayList2, encryptedChat, j, i, z, z2, 0, (TLObject) null);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC.EncryptedChat encryptedChat, long j, int i, boolean z, boolean z2, long j2, TLObject tLObject) {
        ArrayList<Integer> arrayList3;
        long j3;
        TLRPC.TL_messages_deleteMessages tL_messages_deleteMessages;
        NativeByteBuffer nativeByteBuffer;
        long j4;
        TLRPC.TL_channels_deleteMessages tL_channels_deleteMessages;
        NativeByteBuffer nativeByteBuffer2;
        long j5;
        TLRPC.TL_messages_deleteScheduledMessages tL_messages_deleteScheduledMessages;
        NativeByteBuffer nativeByteBuffer3;
        char c;
        ArrayList<Integer> arrayList4 = arrayList;
        ArrayList<Long> arrayList5 = arrayList2;
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        long j6 = j;
        int i2 = i;
        if ((arrayList4 != null && !arrayList.isEmpty()) || tLObject != null) {
            if (j2 == 0) {
                arrayList3 = new ArrayList<>();
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    Integer num = arrayList4.get(i3);
                    if (num.intValue() > 0) {
                        arrayList3.add(num);
                    }
                }
                if (z2) {
                    c = 1;
                    getMessagesStorage().markMessagesAsDeleted(arrayList, true, i, false, true);
                } else {
                    c = 1;
                    if (i2 == 0) {
                        for (int i4 = 0; i4 < arrayList.size(); i4++) {
                            MessageObject messageObject = this.dialogMessagesByIds.get(arrayList4.get(i4).intValue());
                            if (messageObject != null) {
                                messageObject.deleted = true;
                            }
                        }
                    } else {
                        markChannelDialogMessageAsDeleted(arrayList4, i2);
                    }
                    getMessagesStorage().markMessagesAsDeleted(arrayList, true, i, z, false);
                    getMessagesStorage().updateDialogsWithDeletedMessages(arrayList4, (ArrayList<Long>) null, true, i2);
                }
                NotificationCenter notificationCenter = getNotificationCenter();
                int i5 = NotificationCenter.messagesDeleted;
                Object[] objArr = new Object[3];
                objArr[0] = arrayList4;
                objArr[c] = Integer.valueOf(i);
                objArr[2] = Boolean.valueOf(z2);
                notificationCenter.postNotificationName(i5, objArr);
            } else {
                arrayList3 = null;
            }
            if (z2) {
                if (tLObject != null) {
                    tL_messages_deleteScheduledMessages = (TLRPC.TL_messages_deleteScheduledMessages) tLObject;
                    j5 = j2;
                } else {
                    TLRPC.TL_messages_deleteScheduledMessages tL_messages_deleteScheduledMessages2 = new TLRPC.TL_messages_deleteScheduledMessages();
                    tL_messages_deleteScheduledMessages2.id = arrayList3;
                    tL_messages_deleteScheduledMessages2.peer = getInputPeer((int) j6);
                    try {
                        nativeByteBuffer3 = new NativeByteBuffer(tL_messages_deleteScheduledMessages2.getObjectSize() + 16);
                        try {
                            nativeByteBuffer3.writeInt32(18);
                            nativeByteBuffer3.writeInt64(j6);
                            nativeByteBuffer3.writeInt32(i2);
                            tL_messages_deleteScheduledMessages2.serializeToStream(nativeByteBuffer3);
                        } catch (Exception e) {
                            e = e;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        nativeByteBuffer3 = null;
                        FileLog.e((Throwable) e);
                        j5 = getMessagesStorage().createPendingTask(nativeByteBuffer3);
                        tL_messages_deleteScheduledMessages = tL_messages_deleteScheduledMessages2;
                        getConnectionsManager().sendRequest(tL_messages_deleteScheduledMessages, new RequestDelegate(j5) {
                            private final /* synthetic */ long f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$deleteMessages$76$MessagesController(this.f$1, tLObject, tL_error);
                            }
                        });
                    }
                    j5 = getMessagesStorage().createPendingTask(nativeByteBuffer3);
                    tL_messages_deleteScheduledMessages = tL_messages_deleteScheduledMessages2;
                }
                getConnectionsManager().sendRequest(tL_messages_deleteScheduledMessages, new RequestDelegate(j5) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$deleteMessages$76$MessagesController(this.f$1, tLObject, tL_error);
                    }
                });
            } else if (i2 != 0) {
                if (tLObject != null) {
                    tL_channels_deleteMessages = (TLRPC.TL_channels_deleteMessages) tLObject;
                    j4 = j2;
                } else {
                    TLRPC.TL_channels_deleteMessages tL_channels_deleteMessages2 = new TLRPC.TL_channels_deleteMessages();
                    tL_channels_deleteMessages2.id = arrayList3;
                    tL_channels_deleteMessages2.channel = getInputChannel(i2);
                    try {
                        nativeByteBuffer2 = new NativeByteBuffer(tL_channels_deleteMessages2.getObjectSize() + 8);
                        try {
                            nativeByteBuffer2.writeInt32(7);
                            nativeByteBuffer2.writeInt32(i2);
                            tL_channels_deleteMessages2.serializeToStream(nativeByteBuffer2);
                        } catch (Exception e3) {
                            e = e3;
                        }
                    } catch (Exception e4) {
                        e = e4;
                        nativeByteBuffer2 = null;
                        FileLog.e((Throwable) e);
                        j4 = getMessagesStorage().createPendingTask(nativeByteBuffer2);
                        tL_channels_deleteMessages = tL_channels_deleteMessages2;
                        getConnectionsManager().sendRequest(tL_channels_deleteMessages, new RequestDelegate(i2, j4) {
                            private final /* synthetic */ int f$1;
                            private final /* synthetic */ long f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$deleteMessages$77$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        });
                    }
                    j4 = getMessagesStorage().createPendingTask(nativeByteBuffer2);
                    tL_channels_deleteMessages = tL_channels_deleteMessages2;
                }
                getConnectionsManager().sendRequest(tL_channels_deleteMessages, new RequestDelegate(i2, j4) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$deleteMessages$77$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else {
                if (!(arrayList5 == null || encryptedChat2 == null || arrayList2.isEmpty())) {
                    getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat2, arrayList5, (TLRPC.Message) null);
                }
                if (tLObject != null) {
                    tL_messages_deleteMessages = (TLRPC.TL_messages_deleteMessages) tLObject;
                    j3 = j2;
                } else {
                    TLRPC.TL_messages_deleteMessages tL_messages_deleteMessages2 = new TLRPC.TL_messages_deleteMessages();
                    tL_messages_deleteMessages2.id = arrayList3;
                    tL_messages_deleteMessages2.revoke = z;
                    try {
                        nativeByteBuffer = new NativeByteBuffer(tL_messages_deleteMessages2.getObjectSize() + 8);
                        try {
                            nativeByteBuffer.writeInt32(7);
                            nativeByteBuffer.writeInt32(i2);
                            tL_messages_deleteMessages2.serializeToStream(nativeByteBuffer);
                        } catch (Exception e5) {
                            e = e5;
                        }
                    } catch (Exception e6) {
                        e = e6;
                        nativeByteBuffer = null;
                        FileLog.e((Throwable) e);
                        j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        tL_messages_deleteMessages = tL_messages_deleteMessages2;
                        getConnectionsManager().sendRequest(tL_messages_deleteMessages, new RequestDelegate(j3) {
                            private final /* synthetic */ long f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$deleteMessages$78$MessagesController(this.f$1, tLObject, tL_error);
                            }
                        });
                    }
                    j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    tL_messages_deleteMessages = tL_messages_deleteMessages2;
                }
                getConnectionsManager().sendRequest(tL_messages_deleteMessages, new RequestDelegate(j3) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$deleteMessages$78$MessagesController(this.f$1, tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$deleteMessages$76$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$deleteMessages$77$MessagesController(int i, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewChannelDifferenceParams(tL_messages_affectedMessages.pts, tL_messages_affectedMessages.pts_count, i);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$deleteMessages$78$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void pinMessage(TLRPC.Chat chat, TLRPC.User user, int i, boolean z) {
        if (chat != null || user != null) {
            TLRPC.TL_messages_updatePinnedMessage tL_messages_updatePinnedMessage = new TLRPC.TL_messages_updatePinnedMessage();
            tL_messages_updatePinnedMessage.peer = getInputPeer(chat != null ? -chat.id : user.id);
            tL_messages_updatePinnedMessage.id = i;
            tL_messages_updatePinnedMessage.silent = !z;
            getConnectionsManager().sendRequest(tL_messages_updatePinnedMessage, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$pinMessage$79$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$pinMessage$79$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void deleteUserChannelHistory(TLRPC.Chat chat, TLRPC.User user, int i) {
        if (i == 0) {
            getMessagesStorage().deleteUserChannelHistory(chat.id, user.id);
        }
        TLRPC.TL_channels_deleteUserHistory tL_channels_deleteUserHistory = new TLRPC.TL_channels_deleteUserHistory();
        tL_channels_deleteUserHistory.channel = getInputChannel(chat);
        tL_channels_deleteUserHistory.user_id = getInputUser(user);
        getConnectionsManager().sendRequest(tL_channels_deleteUserHistory, new RequestDelegate(chat, user) {
            private final /* synthetic */ TLRPC.Chat f$1;
            private final /* synthetic */ TLRPC.User f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$deleteUserChannelHistory$80$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$deleteUserChannelHistory$80$MessagesController(TLRPC.Chat chat, TLRPC.User user, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            int i = tL_messages_affectedHistory.offset;
            if (i > 0) {
                deleteUserChannelHistory(chat, user, i);
            }
            processNewChannelDifferenceParams(tL_messages_affectedHistory.pts, tL_messages_affectedHistory.pts_count, chat.id);
        }
    }

    public ArrayList<TLRPC.Dialog> getAllDialogs() {
        return this.allDialogs;
    }

    public boolean isDialogsEndReached(int i) {
        return this.dialogsEndReached.get(i);
    }

    public boolean isLoadingDialogs(int i) {
        return this.loadingDialogs.get(i);
    }

    public boolean isServerDialogsEndReached(int i) {
        return this.serverDialogsEndReached.get(i);
    }

    public boolean hasHiddenArchive() {
        return SharedConfig.archiveHidden && this.dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
    }

    public ArrayList<TLRPC.Dialog> getDialogs(int i) {
        ArrayList<TLRPC.Dialog> arrayList = this.dialogsByFolder.get(i);
        return arrayList == null ? new ArrayList<>() : arrayList;
    }

    public void putAllNeededDraftDialogs() {
        LongSparseArray<TLRPC.DraftMessage> drafts = getMediaDataController().getDrafts();
        int size = drafts.size();
        for (int i = 0; i < size; i++) {
            putDraftDialogIfNeed(drafts.keyAt(i), drafts.valueAt(i));
        }
    }

    public void putDraftDialogIfNeed(long j, TLRPC.DraftMessage draftMessage) {
        if (this.dialogs_dict.indexOfKey(j) < 0) {
            MediaDataController mediaDataController = getMediaDataController();
            int size = this.allDialogs.size();
            int i = 1;
            if (size > 0) {
                TLRPC.Dialog dialog = this.allDialogs.get(size - 1);
                if (((long) draftMessage.date) < DialogObject.getLastMessageOrDraftDate(dialog, mediaDataController.getDraft(dialog.id))) {
                    return;
                }
            }
            TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
            tL_dialog.id = j;
            tL_dialog.draft = draftMessage;
            tL_dialog.folder_id = mediaDataController.getDraftFolderId(j);
            if (j >= 0 || !ChatObject.isChannel(getChat(Integer.valueOf((int) (-j))))) {
                i = 0;
            }
            tL_dialog.flags = i;
            this.dialogs_dict.put(j, tL_dialog);
            this.allDialogs.add(tL_dialog);
            sortDialogs((SparseArray<TLRPC.Chat>) null);
        }
    }

    public void removeDraftDialogIfNeed(long j) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog != null && dialog.top_message == 0) {
            this.dialogs_dict.remove(dialog.id);
            this.allDialogs.remove(dialog);
        }
    }

    private void removeDialog(TLRPC.Dialog dialog) {
        if (dialog != null) {
            long j = dialog.id;
            if (this.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
                Utilities.stageQueue.postRunnable(new Runnable(j) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MessagesController.this.lambda$removeDialog$81$MessagesController(this.f$1);
                    }
                });
            }
            this.allDialogs.remove(dialog);
            this.dialogsCanAddUsers.remove(dialog);
            this.dialogsChannelsOnly.remove(dialog);
            this.dialogsGroupsOnly.remove(dialog);
            this.dialogsUsersOnly.remove(dialog);
            this.dialogsForward.remove(dialog);
            this.dialogs_dict.remove(j);
            ArrayList arrayList = this.dialogsByFolder.get(dialog.folder_id);
            if (arrayList != null) {
                arrayList.remove(dialog);
            }
        }
    }

    public /* synthetic */ void lambda$removeDialog$81$MessagesController(long j) {
        int i = -((int) j);
        this.channelsPts.delete(i);
        this.shortPollChannels.delete(i);
        this.needShortPollChannels.delete(i);
        this.shortPollOnlines.delete(i);
        this.needShortPollOnlines.delete(i);
    }

    public void deleteDialog(long j, int i) {
        deleteDialog(j, i, false);
    }

    public void deleteDialog(long j, int i, boolean z) {
        deleteDialog(j, true, i, 0, z, (TLRPC.InputPeer) null, 0);
    }

    public void setDialogsInTransaction(boolean z) {
        this.dialogsInTransaction = z;
        if (!z) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0304  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deleteDialog(long r28, boolean r30, int r31, int r32, boolean r33, org.telegram.tgnet.TLRPC.InputPeer r34, long r35) {
        /*
            r27 = this;
            r11 = r27
            r5 = r28
            r0 = r30
            r7 = r31
            r1 = r32
            r9 = r33
            r2 = 2
            if (r7 != r2) goto L_0x0017
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            r0.deleteDialog(r5, r7)
            return
        L_0x0017:
            r3 = 3
            if (r7 == 0) goto L_0x001c
            if (r7 != r3) goto L_0x0023
        L_0x001c:
            org.telegram.messenger.MediaDataController r4 = r27.getMediaDataController()
            r4.uninstallShortcut(r5)
        L_0x0023:
            int r4 = (int) r5
            r8 = 32
            long r12 = r5 >> r8
            int r8 = (int) r12
            r12 = 0
            r14 = 1
            r15 = 0
            if (r0 == 0) goto L_0x0246
            if (r1 != 0) goto L_0x0034
            r16 = 1
            goto L_0x0036
        L_0x0034:
            r16 = 0
        L_0x0036:
            if (r16 == 0) goto L_0x0046
            org.telegram.messenger.MessagesStorage r2 = r27.getMessagesStorage()
            int r2 = r2.getDialogMaxMessageId(r5)
            if (r2 <= 0) goto L_0x0046
            int r1 = java.lang.Math.max(r2, r1)
        L_0x0046:
            org.telegram.messenger.MessagesStorage r2 = r27.getMessagesStorage()
            r2.deleteDialog(r5, r7)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r11.dialogs_dict
            java.lang.Object r2 = r2.get(r5)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC.Dialog) r2
            if (r7 == 0) goto L_0x0059
            if (r7 != r3) goto L_0x0060
        L_0x0059:
            org.telegram.messenger.NotificationsController r10 = r27.getNotificationsController()
            r10.deleteNotificationChannel(r5)
        L_0x0060:
            if (r2 == 0) goto L_0x01c8
            if (r16 == 0) goto L_0x0076
            int r1 = r2.top_message
            int r1 = java.lang.Math.max(r15, r1)
            int r10 = r2.read_inbox_max_id
            int r1 = java.lang.Math.max(r1, r10)
            int r10 = r2.read_outbox_max_id
            int r1 = java.lang.Math.max(r1, r10)
        L_0x0076:
            if (r7 == 0) goto L_0x0080
            if (r7 != r3) goto L_0x007b
            goto L_0x0080
        L_0x007b:
            r2.unread_count = r15
            r17 = r4
            goto L_0x00c7
        L_0x0080:
            org.telegram.tgnet.TLRPC$Dialog r10 = r11.proxyDialog
            r17 = r4
            if (r10 == 0) goto L_0x008e
            long r3 = r10.id
            int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r10 != 0) goto L_0x008e
            r3 = 1
            goto L_0x008f
        L_0x008e:
            r3 = 0
        L_0x008f:
            if (r3 == 0) goto L_0x00af
            r11.isLeftProxyChannel = r14
            org.telegram.tgnet.TLRPC$Dialog r4 = r11.proxyDialog
            long r14 = r4.id
            int r4 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r4 >= 0) goto L_0x00aa
            int r4 = (int) r14
            int r4 = -r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r11.getChat(r4)
            if (r4 == 0) goto L_0x00aa
            r10 = 1
            r4.left = r10
        L_0x00aa:
            r4 = 0
            r11.sortDialogs(r4)
            goto L_0x00c6
        L_0x00af:
            r11.removeDialog(r2)
            android.util.SparseIntArray r4 = r11.nextDialogsCacheOffset
            int r14 = r2.folder_id
            r15 = 0
            int r4 = r4.get(r14, r15)
            if (r4 <= 0) goto L_0x00c6
            android.util.SparseIntArray r14 = r11.nextDialogsCacheOffset
            int r15 = r2.folder_id
            r10 = 1
            int r4 = r4 - r10
            r14.put(r15, r4)
        L_0x00c6:
            r15 = r3
        L_0x00c7:
            if (r15 != 0) goto L_0x01c5
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r11.dialogMessage
            long r12 = r2.id
            java.lang.Object r3 = r3.get(r12)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r11.dialogMessage
            long r12 = r2.id
            r4.remove(r12)
            if (r3 == 0) goto L_0x00ea
            int r4 = r3.getId()
            android.util.SparseArray<org.telegram.messenger.MessageObject> r12 = r11.dialogMessagesByIds
            int r13 = r3.getId()
            r12.remove(r13)
            goto L_0x00fb
        L_0x00ea:
            int r4 = r2.top_message
            android.util.SparseArray<org.telegram.messenger.MessageObject> r3 = r11.dialogMessagesByIds
            java.lang.Object r3 = r3.get(r4)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            android.util.SparseArray<org.telegram.messenger.MessageObject> r12 = r11.dialogMessagesByIds
            int r13 = r2.top_message
            r12.remove(r13)
        L_0x00fb:
            if (r3 == 0) goto L_0x010c
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            long r12 = r3.random_id
            r18 = 0
            int r3 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1))
            if (r3 == 0) goto L_0x010c
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r11.dialogMessagesByRandomIds
            r3.remove(r12)
        L_0x010c:
            r3 = 1
            if (r7 != r3) goto L_0x01bf
            if (r17 == 0) goto L_0x01bf
            if (r4 <= 0) goto L_0x01bf
            org.telegram.tgnet.TLRPC$TL_messageService r3 = new org.telegram.tgnet.TLRPC$TL_messageService
            r3.<init>()
            int r4 = r2.top_message
            r3.id = r4
            org.telegram.messenger.UserConfig r4 = r27.getUserConfig()
            int r4 = r4.getClientUserId()
            long r12 = (long) r4
            int r4 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x012b
            r4 = 1
            goto L_0x012c
        L_0x012b:
            r4 = 0
        L_0x012c:
            r3.out = r4
            org.telegram.messenger.UserConfig r4 = r27.getUserConfig()
            int r4 = r4.getClientUserId()
            r3.from_id = r4
            int r4 = r3.flags
            r4 = r4 | 256(0x100, float:3.59E-43)
            r3.flags = r4
            org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear r4 = new org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            r4.<init>()
            r3.action = r4
            int r2 = r2.last_message_date
            r3.date = r2
            r4 = r17
            long r12 = (long) r4
            r3.dialog_id = r12
            if (r4 <= 0) goto L_0x015c
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r3.to_id = r2
            org.telegram.tgnet.TLRPC$Peer r2 = r3.to_id
            r2.user_id = r4
            goto L_0x0182
        L_0x015c:
            int r2 = -r4
            java.lang.Integer r12 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r12 = r11.getChat(r12)
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r12 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$TL_peerChannel r12 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r12.<init>()
            r3.to_id = r12
            org.telegram.tgnet.TLRPC$Peer r12 = r3.to_id
            r12.channel_id = r2
            goto L_0x0182
        L_0x0177:
            org.telegram.tgnet.TLRPC$TL_peerChat r12 = new org.telegram.tgnet.TLRPC$TL_peerChat
            r12.<init>()
            r3.to_id = r12
            org.telegram.tgnet.TLRPC$Peer r12 = r3.to_id
            r12.chat_id = r2
        L_0x0182:
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject
            int r12 = r11.currentAccount
            java.util.ArrayList<java.lang.Long> r13 = r11.createdDialogIds
            long r10 = r3.dialog_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            boolean r10 = r13.contains(r10)
            r2.<init>(r12, r3, r10)
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r10.add(r2)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r2.add(r3)
            r3 = 0
            r11 = r27
            r11.updateInterfaceWithMessages(r5, r10, r3)
            org.telegram.messenger.MessagesStorage r20 = r27.getMessagesStorage()
            r22 = 0
            r23 = 1
            r24 = 0
            r25 = 0
            r26 = 0
            r21 = r2
            r20.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r21, (boolean) r22, (boolean) r23, (boolean) r24, (int) r25, (boolean) r26)
            goto L_0x01c9
        L_0x01bf:
            r4 = r17
            r3 = 0
            r2.top_message = r3
            goto L_0x01c9
        L_0x01c5:
            r4 = r17
            goto L_0x01c9
        L_0x01c8:
            r15 = 0
        L_0x01c9:
            if (r16 == 0) goto L_0x01f7
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r11.dialogs_read_inbox_max
            java.lang.Long r3 = java.lang.Long.valueOf(r28)
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 == 0) goto L_0x01e1
            int r2 = r2.intValue()
            int r1 = java.lang.Math.max(r2, r1)
        L_0x01e1:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r11.dialogs_read_outbox_max
            java.lang.Long r3 = java.lang.Long.valueOf(r28)
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 == 0) goto L_0x01f7
            int r2 = r2.intValue()
            int r1 = java.lang.Math.max(r2, r1)
        L_0x01f7:
            boolean r2 = r11.dialogsInTransaction
            if (r2 != 0) goto L_0x0236
            if (r15 == 0) goto L_0x0211
            org.telegram.messenger.NotificationCenter r2 = r27.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r10 = 1
            java.lang.Object[] r12 = new java.lang.Object[r10]
            java.lang.Boolean r13 = java.lang.Boolean.valueOf(r10)
            r14 = 0
            r12[r14] = r13
            r2.postNotificationName(r3, r12)
            goto L_0x0236
        L_0x0211:
            r14 = 0
            org.telegram.messenger.NotificationCenter r2 = r27.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            java.lang.Object[] r12 = new java.lang.Object[r14]
            r2.postNotificationName(r3, r12)
            org.telegram.messenger.NotificationCenter r2 = r27.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.removeAllMessagesFromDialog
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]
            java.lang.Long r13 = java.lang.Long.valueOf(r28)
            r12[r14] = r13
            java.lang.Boolean r13 = java.lang.Boolean.valueOf(r14)
            r10 = 1
            r12[r10] = r13
            r2.postNotificationName(r3, r12)
        L_0x0236:
            org.telegram.messenger.MessagesStorage r2 = r27.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r2 = r2.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$dt98ODWXoNLrHu8D7iAdSBOvu5o r3 = new org.telegram.messenger.-$$Lambda$MessagesController$dt98ODWXoNLrHu8D7iAdSBOvu5o
            r3.<init>(r5)
            r2.postRunnable(r3)
        L_0x0246:
            r12 = r1
            r1 = 3
            if (r7 != r1) goto L_0x024b
            return
        L_0x024b:
            if (r4 == 0) goto L_0x0338
            if (r34 != 0) goto L_0x0255
            org.telegram.tgnet.TLRPC$InputPeer r1 = r11.getInputPeer(r4)
            r13 = r1
            goto L_0x0257
        L_0x0255:
            r13 = r34
        L_0x0257:
            if (r13 != 0) goto L_0x025a
            return
        L_0x025a:
            boolean r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel
            r2 = 2147483647(0x7fffffff, float:NaN)
            if (r1 == 0) goto L_0x0266
            if (r7 == 0) goto L_0x0264
            goto L_0x0266
        L_0x0264:
            r4 = 0
            goto L_0x02c3
        L_0x0266:
            if (r12 <= 0) goto L_0x0289
            if (r12 == r2) goto L_0x0289
            android.util.LongSparseArray<java.lang.Integer> r3 = r11.deletedHistory
            r4 = 0
            java.lang.Integer r8 = java.lang.Integer.valueOf(r4)
            java.lang.Object r3 = r3.get(r5, r8)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            android.util.LongSparseArray<java.lang.Integer> r8 = r11.deletedHistory
            int r3 = java.lang.Math.max(r3, r12)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r8.put(r5, r3)
            goto L_0x028a
        L_0x0289:
            r4 = 0
        L_0x028a:
            r14 = 0
            int r3 = (r35 > r14 ? 1 : (r35 == r14 ? 0 : -1))
            if (r3 != 0) goto L_0x02c3
            org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x02b5 }
            int r8 = r13.getObjectSize()     // Catch:{ Exception -> 0x02b5 }
            int r8 = r8 + 28
            r3.<init>((int) r8)     // Catch:{ Exception -> 0x02b5 }
            r8 = 13
            r3.writeInt32(r8)     // Catch:{ Exception -> 0x02b3 }
            r3.writeInt64(r5)     // Catch:{ Exception -> 0x02b3 }
            r3.writeBool(r0)     // Catch:{ Exception -> 0x02b3 }
            r3.writeInt32(r7)     // Catch:{ Exception -> 0x02b3 }
            r3.writeInt32(r12)     // Catch:{ Exception -> 0x02b3 }
            r3.writeBool(r9)     // Catch:{ Exception -> 0x02b3 }
            r13.serializeToStream(r3)     // Catch:{ Exception -> 0x02b3 }
            goto L_0x02ba
        L_0x02b3:
            r0 = move-exception
            goto L_0x02b7
        L_0x02b5:
            r0 = move-exception
            r3 = 0
        L_0x02b7:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02ba:
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            long r14 = r0.createPendingTask(r3)
            goto L_0x02c5
        L_0x02c3:
            r14 = r35
        L_0x02c5:
            r0 = 64
            if (r1 == 0) goto L_0x0304
            if (r7 != 0) goto L_0x02d9
            r3 = 0
            int r0 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x02d8
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            r0.removePendingTask(r14)
        L_0x02d8:
            return
        L_0x02d9:
            org.telegram.tgnet.TLRPC$TL_channels_deleteHistory r1 = new org.telegram.tgnet.TLRPC$TL_channels_deleteHistory
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_inputChannel r3 = new org.telegram.tgnet.TLRPC$TL_inputChannel
            r3.<init>()
            r1.channel = r3
            org.telegram.tgnet.TLRPC$InputChannel r3 = r1.channel
            int r4 = r13.channel_id
            r3.channel_id = r4
            long r4 = r13.access_hash
            r3.access_hash = r4
            if (r12 <= 0) goto L_0x02f2
            goto L_0x02f5
        L_0x02f2:
            r12 = 2147483647(0x7fffffff, float:NaN)
        L_0x02f5:
            r1.max_id = r12
            org.telegram.tgnet.ConnectionsManager r2 = r27.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$X985wOXUqmlc4HM0aeQg2XDjVqk r3 = new org.telegram.messenger.-$$Lambda$MessagesController$X985wOXUqmlc4HM0aeQg2XDjVqk
            r3.<init>(r14)
            r2.sendRequest(r1, r3, r0)
            goto L_0x0353
        L_0x0304:
            org.telegram.tgnet.TLRPC$TL_messages_deleteHistory r8 = new org.telegram.tgnet.TLRPC$TL_messages_deleteHistory
            r8.<init>()
            r8.peer = r13
            if (r7 != 0) goto L_0x030e
            goto L_0x030f
        L_0x030e:
            r2 = r12
        L_0x030f:
            r8.max_id = r2
            if (r7 == 0) goto L_0x0315
            r10 = 1
            goto L_0x0316
        L_0x0315:
            r10 = 0
        L_0x0316:
            r8.just_clear = r10
            r8.revoke = r9
            org.telegram.tgnet.ConnectionsManager r10 = r27.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$QPEdl9oObsTFzJV-259xd5Fwg8k r3 = new org.telegram.messenger.-$$Lambda$MessagesController$QPEdl9oObsTFzJV-259xd5Fwg8k
            r1 = r3
            r2 = r27
            r0 = r3
            r3 = r14
            r5 = r28
            r7 = r31
            r14 = r8
            r8 = r12
            r9 = r33
            r12 = r10
            r10 = r13
            r1.<init>(r3, r5, r7, r8, r9, r10)
            r1 = 64
            r12.sendRequest(r14, r0, r1)
            goto L_0x0353
        L_0x0338:
            r1 = 1
            if (r7 != r1) goto L_0x034c
            org.telegram.messenger.SecretChatHelper r0 = r27.getSecretChatHelper()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r11.getEncryptedChat(r1)
            r2 = 0
            r0.sendClearHistoryMessage(r1, r2)
            goto L_0x0353
        L_0x034c:
            org.telegram.messenger.SecretChatHelper r0 = r27.getSecretChatHelper()
            r0.declineSecretChat(r8)
        L_0x0353:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteDialog(long, boolean, int, int, boolean, org.telegram.tgnet.TLRPC$InputPeer, long):void");
    }

    public /* synthetic */ void lambda$deleteDialog$83$MessagesController(long j) {
        AndroidUtilities.runOnUIThread(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$82$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$82$MessagesController(long j) {
        getNotificationsController().removeNotificationsForDialog(j);
    }

    public /* synthetic */ void lambda$deleteDialog$84$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$deleteDialog$85$MessagesController(long j, long j2, int i, int i2, boolean z, TLRPC.InputPeer inputPeer, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        if (tL_error == null) {
            TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            if (tL_messages_affectedHistory.offset > 0) {
                deleteDialog(j2, false, i, i2, z, inputPeer, 0);
            }
            processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            getMessagesStorage().onDeleteQueryComplete(j2);
            return;
        }
    }

    public void saveGif(Object obj, TLRPC.Document document) {
        if (obj != null && MessageObject.isGifDocument(document)) {
            TLRPC.TL_messages_saveGif tL_messages_saveGif = new TLRPC.TL_messages_saveGif();
            tL_messages_saveGif.id = new TLRPC.TL_inputDocument();
            TLRPC.InputDocument inputDocument = tL_messages_saveGif.id;
            inputDocument.id = document.id;
            inputDocument.access_hash = document.access_hash;
            inputDocument.file_reference = document.file_reference;
            if (inputDocument.file_reference == null) {
                inputDocument.file_reference = new byte[0];
            }
            tL_messages_saveGif.unsave = false;
            getConnectionsManager().sendRequest(tL_messages_saveGif, new RequestDelegate(obj, tL_messages_saveGif) {
                private final /* synthetic */ Object f$1;
                private final /* synthetic */ TLRPC.TL_messages_saveGif f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$saveGif$86$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$saveGif$86$MessagesController(Object obj, TLRPC.TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tL_messages_saveGif);
        }
    }

    public void saveRecentSticker(Object obj, TLRPC.Document document, boolean z) {
        if (obj != null && document != null) {
            TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = new TLRPC.TL_messages_saveRecentSticker();
            tL_messages_saveRecentSticker.id = new TLRPC.TL_inputDocument();
            TLRPC.InputDocument inputDocument = tL_messages_saveRecentSticker.id;
            inputDocument.id = document.id;
            inputDocument.access_hash = document.access_hash;
            inputDocument.file_reference = document.file_reference;
            if (inputDocument.file_reference == null) {
                inputDocument.file_reference = new byte[0];
            }
            tL_messages_saveRecentSticker.unsave = false;
            tL_messages_saveRecentSticker.attached = z;
            getConnectionsManager().sendRequest(tL_messages_saveRecentSticker, new RequestDelegate(obj, tL_messages_saveRecentSticker) {
                private final /* synthetic */ Object f$1;
                private final /* synthetic */ TLRPC.TL_messages_saveRecentSticker f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$saveRecentSticker$87$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$saveRecentSticker$87$MessagesController(Object obj, TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tL_messages_saveRecentSticker);
        }
    }

    public void loadChannelParticipants(Integer num) {
        if (!this.loadingFullParticipants.contains(num) && !this.loadedFullParticipants.contains(num)) {
            this.loadingFullParticipants.add(num);
            TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
            tL_channels_getParticipants.channel = getInputChannel(num.intValue());
            tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
            tL_channels_getParticipants.offset = 0;
            tL_channels_getParticipants.limit = 32;
            getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate(num) {
                private final /* synthetic */ Integer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadChannelParticipants$89$MessagesController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadChannelParticipants$89$MessagesController(Integer num, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, num) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ Integer f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$null$88$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$88$MessagesController(TLRPC.TL_error tL_error, TLObject tLObject, Integer num) {
        if (tL_error == null) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            putUsers(tL_channels_channelParticipants.users, false);
            getMessagesStorage().putUsersAndChats(tL_channels_channelParticipants.users, (ArrayList<TLRPC.Chat>) null, true, true);
            getMessagesStorage().updateChannelUsers(num.intValue(), tL_channels_channelParticipants.participants);
            this.loadedFullParticipants.add(num);
        }
        this.loadingFullParticipants.remove(num);
    }

    public void processChatInfo(int i, TLRPC.ChatFull chatFull, ArrayList<TLRPC.User> arrayList, boolean z, boolean z2, boolean z3, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new Runnable(z, i, z3, z2, chatFull, arrayList, messageObject) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ TLRPC.ChatFull f$5;
            private final /* synthetic */ ArrayList f$6;
            private final /* synthetic */ MessageObject f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MessagesController.this.lambda$processChatInfo$90$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$processChatInfo$90$MessagesController(boolean z, int i, boolean z2, boolean z3, TLRPC.ChatFull chatFull, ArrayList arrayList, MessageObject messageObject) {
        if (z && i > 0 && !z2) {
            loadFullChat(i, 0, z3);
        }
        if (chatFull != null) {
            if (this.fullChats.get(i) == null) {
                this.fullChats.put(i, chatFull);
            }
            putUsers(arrayList, z);
            if (chatFull.stickerset != null) {
                getMediaDataController().getGroupStickerSetById(chatFull.stickerset);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, 0, Boolean.valueOf(z2), messageObject);
        }
    }

    public void loadUserInfo(TLRPC.User user, boolean z, int i) {
        getMessagesStorage().loadUserInfo(user, z, i);
    }

    public void processUserInfo(TLRPC.User user, TLRPC.UserFull userFull, boolean z, boolean z2, MessageObject messageObject, int i) {
        AndroidUtilities.runOnUIThread(new Runnable(z, user, i, z2, userFull, messageObject) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ TLRPC.User f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ TLRPC.UserFull f$5;
            private final /* synthetic */ MessageObject f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MessagesController.this.lambda$processUserInfo$91$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$processUserInfo$91$MessagesController(boolean z, TLRPC.User user, int i, boolean z2, TLRPC.UserFull userFull, MessageObject messageObject) {
        if (z) {
            loadFullUser(user, i, z2);
        }
        if (userFull != null) {
            if (this.fullUsers.get(user.id) == null) {
                this.fullUsers.put(user.id, userFull);
                if (userFull.blocked) {
                    this.blockedUsers.put(user.id, 1);
                } else {
                    this.blockedUsers.delete(user.id);
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.id), userFull, messageObject);
        }
    }

    public void updateTimerProc() {
        ArrayList arrayList;
        ArrayList arrayList2;
        long currentTimeMillis = System.currentTimeMillis();
        checkDeletingTask(false);
        checkReadTasks();
        if (getUserConfig().isClientActivated()) {
            if (getConnectionsManager().getPauseTime() != 0 || !ApplicationLoader.isScreenOn || ApplicationLoader.mainInterfacePausedStageQueue) {
                if (this.statusSettingState != 2 && !this.offlineSent && Math.abs(System.currentTimeMillis() - getConnectionsManager().getPauseTime()) >= 2000) {
                    this.statusSettingState = 2;
                    if (this.statusRequest != 0) {
                        getConnectionsManager().cancelRequest(this.statusRequest, true);
                    }
                    TLRPC.TL_account_updateStatus tL_account_updateStatus = new TLRPC.TL_account_updateStatus();
                    tL_account_updateStatus.offline = true;
                    this.statusRequest = getConnectionsManager().sendRequest(tL_account_updateStatus, new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$updateTimerProc$93$MessagesController(tLObject, tL_error);
                        }
                    });
                }
            } else if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && this.statusSettingState != 1 && (this.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000 || this.offlineSent)) {
                this.statusSettingState = 1;
                if (this.statusRequest != 0) {
                    getConnectionsManager().cancelRequest(this.statusRequest, true);
                }
                TLRPC.TL_account_updateStatus tL_account_updateStatus2 = new TLRPC.TL_account_updateStatus();
                tL_account_updateStatus2.offline = false;
                this.statusRequest = getConnectionsManager().sendRequest(tL_account_updateStatus2, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$updateTimerProc$92$MessagesController(tLObject, tL_error);
                    }
                });
            }
            if (this.updatesQueueChannels.size() != 0) {
                for (int i = 0; i < this.updatesQueueChannels.size(); i++) {
                    int keyAt = this.updatesQueueChannels.keyAt(i);
                    if (this.updatesStartWaitTimeChannels.valueAt(i) + 1500 < currentTimeMillis) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("QUEUE CHANNEL " + keyAt + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        }
                        processChannelsUpdatesQueue(keyAt, 0);
                    }
                }
            }
            for (int i2 = 0; i2 < 3; i2++) {
                if (getUpdatesStartTime(i2) != 0 && getUpdatesStartTime(i2) + 1500 < currentTimeMillis) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(i2 + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                    }
                    processUpdatesQueue(i2, 0);
                }
            }
        }
        if (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= 5000) {
            this.lastViewsCheckTime = System.currentTimeMillis();
            if (this.channelViewsToSend.size() != 0) {
                int i3 = 0;
                while (i3 < this.channelViewsToSend.size()) {
                    int keyAt2 = this.channelViewsToSend.keyAt(i3);
                    TLRPC.TL_messages_getMessagesViews tL_messages_getMessagesViews = new TLRPC.TL_messages_getMessagesViews();
                    tL_messages_getMessagesViews.peer = getInputPeer(keyAt2);
                    tL_messages_getMessagesViews.id = this.channelViewsToSend.valueAt(i3);
                    tL_messages_getMessagesViews.increment = i3 == 0;
                    getConnectionsManager().sendRequest(tL_messages_getMessagesViews, new RequestDelegate(keyAt2, tL_messages_getMessagesViews) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ TLRPC.TL_messages_getMessagesViews f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$updateTimerProc$95$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    });
                    i3++;
                }
                this.channelViewsToSend.clear();
            }
            if (this.pollsToCheckSize > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$updateTimerProc$97$MessagesController();
                    }
                });
            }
        }
        if (!this.onlinePrivacy.isEmpty()) {
            ArrayList arrayList3 = null;
            int currentTime = getConnectionsManager().getCurrentTime();
            for (Map.Entry next : this.onlinePrivacy.entrySet()) {
                if (((Integer) next.getValue()).intValue() < currentTime - 30) {
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                    }
                    arrayList3.add(next.getKey());
                }
            }
            if (arrayList3 != null) {
                Iterator it = arrayList3.iterator();
                while (it.hasNext()) {
                    this.onlinePrivacy.remove((Integer) it.next());
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$updateTimerProc$98$MessagesController();
                    }
                });
            }
        }
        if (this.shortPollChannels.size() != 0) {
            int i4 = 0;
            while (i4 < this.shortPollChannels.size()) {
                int keyAt3 = this.shortPollChannels.keyAt(i4);
                if (((long) this.shortPollChannels.valueAt(i4)) < System.currentTimeMillis() / 1000) {
                    this.shortPollChannels.delete(keyAt3);
                    i4--;
                    if (this.needShortPollChannels.indexOfKey(keyAt3) >= 0) {
                        getChannelDifference(keyAt3);
                    }
                }
                i4++;
            }
        }
        if (this.shortPollOnlines.size() != 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime() / 1000;
            int i5 = 0;
            while (i5 < this.shortPollOnlines.size()) {
                int keyAt4 = this.shortPollOnlines.keyAt(i5);
                if (((long) this.shortPollOnlines.valueAt(i5)) < elapsedRealtime) {
                    if (this.needShortPollChannels.indexOfKey(keyAt4) >= 0) {
                        this.shortPollOnlines.put(keyAt4, (int) (300 + elapsedRealtime));
                    } else {
                        this.shortPollOnlines.delete(keyAt4);
                        i5--;
                    }
                    TLRPC.TL_messages_getOnlines tL_messages_getOnlines = new TLRPC.TL_messages_getOnlines();
                    tL_messages_getOnlines.peer = getInputPeer(-keyAt4);
                    getConnectionsManager().sendRequest(tL_messages_getOnlines, new RequestDelegate(keyAt4) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$updateTimerProc$100$MessagesController(this.f$1, tLObject, tL_error);
                        }
                    });
                }
                i5++;
            }
        }
        if (!this.printingUsers.isEmpty() || this.lastPrintingStringCount != this.printingUsers.size()) {
            ArrayList arrayList4 = new ArrayList(this.printingUsers.keySet());
            int i6 = 0;
            boolean z = false;
            while (i6 < arrayList4.size()) {
                long longValue = ((Long) arrayList4.get(i6)).longValue();
                ArrayList arrayList5 = this.printingUsers.get(Long.valueOf(longValue));
                if (arrayList5 != null) {
                    boolean z2 = z;
                    int i7 = 0;
                    while (i7 < arrayList5.size()) {
                        PrintingUser printingUser = (PrintingUser) arrayList5.get(i7);
                        ArrayList arrayList6 = arrayList4;
                        if (printingUser.lastTime + ((long) (printingUser.action instanceof TLRPC.TL_sendMessageGamePlayAction ? 30000 : 5900)) < currentTimeMillis) {
                            arrayList5.remove(printingUser);
                            i7--;
                            z2 = true;
                        }
                        i7++;
                        arrayList4 = arrayList6;
                    }
                    arrayList = arrayList4;
                    z = z2;
                } else {
                    arrayList = arrayList4;
                }
                if (arrayList5 == null || arrayList5.isEmpty()) {
                    this.printingUsers.remove(Long.valueOf(longValue));
                    arrayList2 = arrayList;
                    arrayList2.remove(i6);
                    i6--;
                } else {
                    arrayList2 = arrayList;
                }
                i6++;
                arrayList4 = arrayList2;
            }
            updatePrintingStrings();
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$updateTimerProc$101$MessagesController();
                    }
                });
            }
        }
        if (Theme.selectedAutoNightType == 1 && Math.abs(currentTimeMillis - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.themeCheckRunnable);
            lastThemeCheckTime = currentTimeMillis;
        }
        if (getUserConfig().savedPasswordHash != null && Math.abs(currentTimeMillis - lastPasswordCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.passwordCheckRunnable);
            lastPasswordCheckTime = currentTimeMillis;
        }
        if (this.lastPushRegisterSendTime != 0 && Math.abs(SystemClock.elapsedRealtime() - this.lastPushRegisterSendTime) >= 10800000) {
            GcmPushListenerService.sendRegistrationToServer(SharedConfig.pushString);
        }
        getLocationController().update();
        lambda$checkProxyInfo$104$MessagesController(false);
        checkTosUpdate();
    }

    public /* synthetic */ void lambda$updateTimerProc$92$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.lastStatusUpdateTime = System.currentTimeMillis();
            this.offlineSent = false;
            this.statusSettingState = 0;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + 5000;
            }
        }
        this.statusRequest = 0;
    }

    public /* synthetic */ void lambda$updateTimerProc$93$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.offlineSent = true;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + 5000;
            }
        }
        this.statusRequest = 0;
    }

    public /* synthetic */ void lambda$updateTimerProc$95$MessagesController(int i, TLRPC.TL_messages_getMessagesViews tL_messages_getMessagesViews, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            SparseArray sparseArray = new SparseArray();
            SparseIntArray sparseIntArray = (SparseIntArray) sparseArray.get(i);
            if (sparseIntArray == null) {
                sparseIntArray = new SparseIntArray();
                sparseArray.put(i, sparseIntArray);
            }
            int i2 = 0;
            while (i2 < tL_messages_getMessagesViews.id.size() && i2 < vector.objects.size()) {
                sparseIntArray.put(tL_messages_getMessagesViews.id.get(i2).intValue(), ((Integer) vector.objects.get(i2)).intValue());
                i2++;
            }
            getMessagesStorage().putChannelViews(sparseArray, tL_messages_getMessagesViews.peer instanceof TLRPC.TL_inputPeerChannel);
            AndroidUtilities.runOnUIThread(new Runnable(sparseArray) {
                private final /* synthetic */ SparseArray f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$94$MessagesController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$94$MessagesController(SparseArray sparseArray) {
        getNotificationCenter().postNotificationName(NotificationCenter.didUpdatedMessagesViews, sparseArray);
    }

    public /* synthetic */ void lambda$updateTimerProc$97$MessagesController() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int size = this.pollsToCheck.size();
        int i = 0;
        while (i < size) {
            SparseArray valueAt = this.pollsToCheck.valueAt(i);
            if (valueAt != null) {
                int size2 = valueAt.size();
                int i2 = 0;
                while (i2 < size2) {
                    MessageObject messageObject = (MessageObject) valueAt.valueAt(i2);
                    if (Math.abs(elapsedRealtime - messageObject.pollLastCheckTime) >= 30000) {
                        messageObject.pollLastCheckTime = elapsedRealtime;
                        TLRPC.TL_messages_getPollResults tL_messages_getPollResults = new TLRPC.TL_messages_getPollResults();
                        tL_messages_getPollResults.peer = getInputPeer((int) messageObject.getDialogId());
                        tL_messages_getPollResults.msg_id = messageObject.getId();
                        getConnectionsManager().sendRequest(tL_messages_getPollResults, new RequestDelegate() {
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$null$96$MessagesController(tLObject, tL_error);
                            }
                        });
                    } else if (!messageObject.pollVisibleOnScreen) {
                        valueAt.remove(messageObject.getId());
                        size2--;
                        i2--;
                    }
                    i2++;
                }
                if (valueAt.size() == 0) {
                    LongSparseArray<SparseArray<MessageObject>> longSparseArray = this.pollsToCheck;
                    longSparseArray.remove(longSparseArray.keyAt(i));
                    size--;
                    i--;
                }
            }
            i++;
        }
        this.pollsToCheckSize = this.pollsToCheck.size();
    }

    public /* synthetic */ void lambda$null$96$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$updateTimerProc$98$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 4);
    }

    public /* synthetic */ void lambda$updateTimerProc$100$MessagesController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_chatOnlines tL_chatOnlines = (TLRPC.TL_chatOnlines) tLObject;
            getMessagesStorage().updateChatOnlineCount(i, tL_chatOnlines.onlines);
            AndroidUtilities.runOnUIThread(new Runnable(i, tL_chatOnlines) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLRPC.TL_chatOnlines f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MessagesController.this.lambda$null$99$MessagesController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$99$MessagesController(int i, TLRPC.TL_chatOnlines tL_chatOnlines) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatOnlineCountDidLoad, Integer.valueOf(i), Integer.valueOf(tL_chatOnlines.onlines));
    }

    public /* synthetic */ void lambda$updateTimerProc$101$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 64);
    }

    private void checkTosUpdate() {
        if (this.nextTosCheckTime <= getConnectionsManager().getCurrentTime() && !this.checkingTosUpdate && getUserConfig().isClientActivated()) {
            this.checkingTosUpdate = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_help_getTermsOfServiceUpdate(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$checkTosUpdate$103$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkTosUpdate$103$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.checkingTosUpdate = false;
        if (tLObject instanceof TLRPC.TL_help_termsOfServiceUpdateEmpty) {
            this.nextTosCheckTime = ((TLRPC.TL_help_termsOfServiceUpdateEmpty) tLObject).expires;
        } else if (tLObject instanceof TLRPC.TL_help_termsOfServiceUpdate) {
            TLRPC.TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate = (TLRPC.TL_help_termsOfServiceUpdate) tLObject;
            this.nextTosCheckTime = tL_help_termsOfServiceUpdate.expires;
            AndroidUtilities.runOnUIThread(new Runnable(tL_help_termsOfServiceUpdate) {
                private final /* synthetic */ TLRPC.TL_help_termsOfServiceUpdate f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$102$MessagesController(this.f$1);
                }
            });
        } else {
            this.nextTosCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        }
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
    }

    public /* synthetic */ void lambda$null$102$MessagesController(TLRPC.TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 4, tL_help_termsOfServiceUpdate.terms_of_service);
    }

    public void checkProxyInfo(boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable(z) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$checkProxyInfo$104$MessagesController(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* renamed from: checkProxyInfoInternal */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$checkProxyInfo$104$MessagesController(boolean r12) {
        /*
            r11 = this;
            r0 = 0
            if (r12 == 0) goto L_0x0009
            boolean r1 = r11.checkingProxyInfo
            if (r1 == 0) goto L_0x0009
            r11.checkingProxyInfo = r0
        L_0x0009:
            if (r12 != 0) goto L_0x0017
            int r12 = r11.nextProxyInfoCheckTime
            org.telegram.tgnet.ConnectionsManager r1 = r11.getConnectionsManager()
            int r1 = r1.getCurrentTime()
            if (r12 > r1) goto L_0x001b
        L_0x0017:
            boolean r12 = r11.checkingProxyInfo
            if (r12 == 0) goto L_0x001c
        L_0x001b:
            return
        L_0x001c:
            int r12 = r11.checkingProxyInfoRequestId
            r1 = 1
            if (r12 == 0) goto L_0x002c
            org.telegram.tgnet.ConnectionsManager r12 = r11.getConnectionsManager()
            int r2 = r11.checkingProxyInfoRequestId
            r12.cancelRequest(r2, r1)
            r11.checkingProxyInfoRequestId = r0
        L_0x002c:
            android.content.SharedPreferences r12 = getGlobalMainSettings()
            java.lang.String r2 = "proxy_enabled"
            boolean r2 = r12.getBoolean(r2, r0)
            java.lang.String r3 = ""
            java.lang.String r4 = "proxy_ip"
            java.lang.String r4 = r12.getString(r4, r3)
            java.lang.String r5 = "proxy_secret"
            java.lang.String r12 = r12.getString(r5, r3)
            long r5 = r11.proxyDialogId
            r7 = 0
            int r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r3 == 0) goto L_0x0067
            java.lang.String r3 = r11.proxyDialogAddress
            if (r3 == 0) goto L_0x0067
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            boolean r3 = r3.equals(r5)
            if (r3 != 0) goto L_0x0067
            r3 = 1
            goto L_0x0068
        L_0x0067:
            r3 = 0
        L_0x0068:
            int r5 = r11.lastCheckProxyId
            int r5 = r5 + r1
            r11.lastCheckProxyId = r5
            r5 = 2
            if (r2 == 0) goto L_0x0095
            boolean r2 = android.text.TextUtils.isEmpty(r4)
            if (r2 != 0) goto L_0x0095
            boolean r2 = android.text.TextUtils.isEmpty(r12)
            if (r2 != 0) goto L_0x0095
            r11.checkingProxyInfo = r1
            int r2 = r11.lastCheckProxyId
            org.telegram.tgnet.TLRPC$TL_help_getProxyData r6 = new org.telegram.tgnet.TLRPC$TL_help_getProxyData
            r6.<init>()
            org.telegram.tgnet.ConnectionsManager r9 = r11.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$x4R-id_j--SYV1kFvIpW7aSeLSk r10 = new org.telegram.messenger.-$$Lambda$MessagesController$x4R-id_j--SYV1kFvIpW7aSeLSk
            r10.<init>(r2, r4, r12)
            int r12 = r9.sendRequest(r6, r10)
            r11.checkingProxyInfoRequestId = r12
            goto L_0x0096
        L_0x0095:
            r3 = 2
        L_0x0096:
            if (r3 == 0) goto L_0x00dd
            r11.proxyDialogId = r7
            r12 = 0
            r11.proxyDialogAddress = r12
            android.content.SharedPreferences r12 = getGlobalMainSettings()
            android.content.SharedPreferences$Editor r12 = r12.edit()
            long r6 = r11.proxyDialogId
            java.lang.String r2 = "proxy_dialog"
            android.content.SharedPreferences$Editor r12 = r12.putLong(r2, r6)
            java.lang.String r2 = "proxyDialogAddress"
            android.content.SharedPreferences$Editor r12 = r12.remove(r2)
            r12.commit()
            org.telegram.tgnet.ConnectionsManager r12 = r11.getConnectionsManager()
            int r12 = r12.getCurrentTime()
            int r12 = r12 + 3600
            r11.nextProxyInfoCheckTime = r12
            if (r3 != r5) goto L_0x00d5
            r11.checkingProxyInfo = r0
            int r12 = r11.checkingProxyInfoRequestId
            if (r12 == 0) goto L_0x00d5
            org.telegram.tgnet.ConnectionsManager r12 = r11.getConnectionsManager()
            int r2 = r11.checkingProxyInfoRequestId
            r12.cancelRequest(r2, r1)
            r11.checkingProxyInfoRequestId = r0
        L_0x00d5:
            org.telegram.messenger.-$$Lambda$MessagesController$DBAgk1weV09qLtd-t72gEXkuPiA r12 = new org.telegram.messenger.-$$Lambda$MessagesController$DBAgk1weV09qLtd-t72gEXkuPiA
            r12.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12)
        L_0x00dd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$checkProxyInfo$104$MessagesController(boolean):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004d, code lost:
        if (r5.restricted == false) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0079, code lost:
        if (r5.restricted == false) goto L_0x0025;
     */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00af  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$checkProxyInfoInternal$109$MessagesController(int r11, java.lang.String r12, java.lang.String r13, org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC.TL_error r15) {
        /*
            r10 = this;
            int r15 = r10.lastCheckProxyId
            if (r11 == r15) goto L_0x0005
            return
        L_0x0005:
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_help_proxyDataEmpty
            java.lang.String r0 = "proxyDialogAddress"
            java.lang.String r1 = "proxy_dialog"
            r2 = 0
            r3 = 1
            if (r15 == 0) goto L_0x0017
            org.telegram.tgnet.TLRPC$TL_help_proxyDataEmpty r14 = (org.telegram.tgnet.TLRPC.TL_help_proxyDataEmpty) r14
            int r11 = r14.expires
            r10.nextProxyInfoCheckTime = r11
            goto L_0x00c7
        L_0x0017:
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo
            if (r15 == 0) goto L_0x00bb
            r8 = r14
            org.telegram.tgnet.TLRPC$TL_help_proxyDataPromo r8 = (org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo) r8
            org.telegram.tgnet.TLRPC$Peer r14 = r8.peer
            int r15 = r14.user_id
            if (r15 == 0) goto L_0x0028
            long r14 = (long) r15
        L_0x0025:
            r6 = r14
            r3 = 0
            goto L_0x007f
        L_0x0028:
            int r15 = r14.chat_id
            if (r15 == 0) goto L_0x0056
            int r14 = -r15
            long r14 = (long) r14
            r4 = 0
        L_0x002f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r8.chats
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0053
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r8.chats
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC.Chat) r5
            int r6 = r5.id
            org.telegram.tgnet.TLRPC$Peer r7 = r8.peer
            int r7 = r7.chat_id
            if (r6 != r7) goto L_0x0050
            boolean r4 = r5.kicked
            if (r4 != 0) goto L_0x0054
            boolean r4 = r5.restricted
            if (r4 == 0) goto L_0x0053
            goto L_0x0054
        L_0x0050:
            int r4 = r4 + 1
            goto L_0x002f
        L_0x0053:
            r3 = 0
        L_0x0054:
            r6 = r14
            goto L_0x007f
        L_0x0056:
            int r14 = r14.channel_id
            int r14 = -r14
            long r14 = (long) r14
            r4 = 0
        L_0x005b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r8.chats
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0025
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r8.chats
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC.Chat) r5
            int r6 = r5.id
            org.telegram.tgnet.TLRPC$Peer r7 = r8.peer
            int r7 = r7.channel_id
            if (r6 != r7) goto L_0x007c
            boolean r4 = r5.kicked
            if (r4 != 0) goto L_0x0054
            boolean r4 = r5.restricted
            if (r4 == 0) goto L_0x0025
            goto L_0x0054
        L_0x007c:
            int r4 = r4 + 1
            goto L_0x005b
        L_0x007f:
            r10.proxyDialogId = r6
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r12)
            r14.append(r13)
            java.lang.String r12 = r14.toString()
            r10.proxyDialogAddress = r12
            android.content.SharedPreferences r12 = getGlobalMainSettings()
            android.content.SharedPreferences$Editor r12 = r12.edit()
            long r13 = r10.proxyDialogId
            android.content.SharedPreferences$Editor r12 = r12.putLong(r1, r13)
            java.lang.String r13 = r10.proxyDialogAddress
            android.content.SharedPreferences$Editor r12 = r12.putString(r0, r13)
            r12.commit()
            int r12 = r8.expires
            r10.nextProxyInfoCheckTime = r12
            if (r3 != 0) goto L_0x00c7
            org.telegram.messenger.-$$Lambda$MessagesController$o3RiJfgJ59II6TcwHd_4BAGZrNE r12 = new org.telegram.messenger.-$$Lambda$MessagesController$o3RiJfgJ59II6TcwHd_4BAGZrNE
            r4 = r12
            r5 = r10
            r9 = r11
            r4.<init>(r6, r8, r9)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12)
            goto L_0x00c7
        L_0x00bb:
            org.telegram.tgnet.ConnectionsManager r11 = r10.getConnectionsManager()
            int r11 = r11.getCurrentTime()
            int r11 = r11 + 3600
            r10.nextProxyInfoCheckTime = r11
        L_0x00c7:
            if (r3 == 0) goto L_0x00ee
            r11 = 0
            r10.proxyDialogId = r11
            android.content.SharedPreferences r11 = getGlobalMainSettings()
            android.content.SharedPreferences$Editor r11 = r11.edit()
            long r12 = r10.proxyDialogId
            android.content.SharedPreferences$Editor r11 = r11.putLong(r1, r12)
            android.content.SharedPreferences$Editor r11 = r11.remove(r0)
            r11.commit()
            r10.checkingProxyInfoRequestId = r2
            r10.checkingProxyInfo = r2
            org.telegram.messenger.-$$Lambda$MessagesController$DBAgk1weV09qLtd-t72gEXkuPiA r11 = new org.telegram.messenger.-$$Lambda$MessagesController$DBAgk1weV09qLtd-t72gEXkuPiA
            r11.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r11)
        L_0x00ee:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$checkProxyInfoInternal$109$MessagesController(int, java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$null$108$MessagesController(long j, TLRPC.TL_help_proxyDataPromo tL_help_proxyDataPromo, int i) {
        TLRPC.Dialog dialog = this.proxyDialog;
        if (!(dialog == null || j == dialog.id)) {
            removeProxyDialog();
        }
        this.proxyDialog = this.dialogs_dict.get(j);
        if (this.proxyDialog != null) {
            this.checkingProxyInfo = false;
            sortDialogs((SparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
            return;
        }
        SparseArray sparseArray = new SparseArray();
        SparseArray sparseArray2 = new SparseArray();
        for (int i2 = 0; i2 < tL_help_proxyDataPromo.users.size(); i2++) {
            TLRPC.User user = tL_help_proxyDataPromo.users.get(i2);
            sparseArray.put(user.id, user);
        }
        for (int i3 = 0; i3 < tL_help_proxyDataPromo.chats.size(); i3++) {
            TLRPC.Chat chat = tL_help_proxyDataPromo.chats.get(i3);
            sparseArray2.put(chat.id, chat);
        }
        TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        TLRPC.Peer peer = tL_help_proxyDataPromo.peer;
        if (peer.user_id != 0) {
            tL_inputDialogPeer.peer = new TLRPC.TL_inputPeerUser();
            TLRPC.InputPeer inputPeer = tL_inputDialogPeer.peer;
            int i4 = tL_help_proxyDataPromo.peer.user_id;
            inputPeer.user_id = i4;
            TLRPC.User user2 = (TLRPC.User) sparseArray.get(i4);
            if (user2 != null) {
                tL_inputDialogPeer.peer.access_hash = user2.access_hash;
            }
        } else if (peer.chat_id != 0) {
            tL_inputDialogPeer.peer = new TLRPC.TL_inputPeerChat();
            TLRPC.InputPeer inputPeer2 = tL_inputDialogPeer.peer;
            int i5 = tL_help_proxyDataPromo.peer.chat_id;
            inputPeer2.chat_id = i5;
            TLRPC.Chat chat2 = (TLRPC.Chat) sparseArray2.get(i5);
            if (chat2 != null) {
                tL_inputDialogPeer.peer.access_hash = chat2.access_hash;
            }
        } else {
            tL_inputDialogPeer.peer = new TLRPC.TL_inputPeerChannel();
            TLRPC.InputPeer inputPeer3 = tL_inputDialogPeer.peer;
            int i6 = tL_help_proxyDataPromo.peer.channel_id;
            inputPeer3.channel_id = i6;
            TLRPC.Chat chat3 = (TLRPC.Chat) sparseArray2.get(i6);
            if (chat3 != null) {
                tL_inputDialogPeer.peer.access_hash = chat3.access_hash;
            }
        }
        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
        this.checkingProxyInfoRequestId = getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate(i, tL_help_proxyDataPromo, j) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLRPC.TL_help_proxyDataPromo f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$null$107$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$107$MessagesController(int i, TLRPC.TL_help_proxyDataPromo tL_help_proxyDataPromo, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (i == this.lastCheckProxyId) {
            this.checkingProxyInfoRequestId = 0;
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (tL_messages_peerDialogs == null || tL_messages_peerDialogs.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        MessagesController.this.lambda$null$106$MessagesController();
                    }
                });
            } else {
                getMessagesStorage().putUsersAndChats(tL_help_proxyDataPromo.users, tL_help_proxyDataPromo.chats, true, true);
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
                tL_messages_dialogs.users = tL_messages_peerDialogs.users;
                tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
                tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
                getMessagesStorage().putDialogs(tL_messages_dialogs, 2);
                AndroidUtilities.runOnUIThread(new Runnable(tL_help_proxyDataPromo, tL_messages_peerDialogs, j) {
                    private final /* synthetic */ TLRPC.TL_help_proxyDataPromo f$1;
                    private final /* synthetic */ TLRPC.TL_messages_peerDialogs f$2;
                    private final /* synthetic */ long f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$105$MessagesController(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
            this.checkingProxyInfo = false;
        }
    }

    public /* synthetic */ void lambda$null$105$MessagesController(TLRPC.TL_help_proxyDataPromo tL_help_proxyDataPromo, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        putUsers(tL_help_proxyDataPromo.users, false);
        putChats(tL_help_proxyDataPromo.chats, false);
        putUsers(tL_messages_peerDialogs.users, false);
        putChats(tL_messages_peerDialogs.chats, false);
        TLRPC.Dialog dialog = this.proxyDialog;
        if (dialog != null) {
            int i = (int) dialog.id;
            if (i < 0) {
                TLRPC.Chat chat = getChat(Integer.valueOf(-i));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.proxyDialog);
                }
            } else {
                removeDialog(dialog);
            }
        }
        this.proxyDialog = tL_messages_peerDialogs.dialogs.get(0);
        TLRPC.Dialog dialog2 = this.proxyDialog;
        dialog2.id = j;
        dialog2.folder_id = 0;
        if (DialogObject.isChannel(dialog2)) {
            SparseIntArray sparseIntArray = this.channelsPts;
            TLRPC.Dialog dialog3 = this.proxyDialog;
            sparseIntArray.put(-((int) dialog3.id), dialog3.pts);
        }
        Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(this.proxyDialog.id));
        if (num == null) {
            num = 0;
        }
        this.dialogs_read_inbox_max.put(Long.valueOf(this.proxyDialog.id), Integer.valueOf(Math.max(num.intValue(), this.proxyDialog.read_inbox_max_id)));
        Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(this.proxyDialog.id));
        if (num2 == null) {
            num2 = 0;
        }
        this.dialogs_read_outbox_max.put(Long.valueOf(this.proxyDialog.id), Integer.valueOf(Math.max(num2.intValue(), this.proxyDialog.read_outbox_max_id)));
        this.dialogs_dict.put(j, this.proxyDialog);
        if (!tL_messages_peerDialogs.messages.isEmpty()) {
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (int i2 = 0; i2 < tL_messages_peerDialogs.users.size(); i2++) {
                TLRPC.User user = tL_messages_peerDialogs.users.get(i2);
                sparseArray.put(user.id, user);
            }
            for (int i3 = 0; i3 < tL_messages_peerDialogs.chats.size(); i3++) {
                TLRPC.Chat chat2 = tL_messages_peerDialogs.chats.get(i3);
                sparseArray2.put(chat2.id, chat2);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, tL_messages_peerDialogs.messages.get(0), (SparseArray<TLRPC.User>) sparseArray, (SparseArray<TLRPC.Chat>) sparseArray2, false);
            this.dialogMessage.put(j, messageObject);
            TLRPC.Dialog dialog4 = this.proxyDialog;
            if (dialog4.last_message_date == 0) {
                dialog4.last_message_date = messageObject.messageOwner.date;
            }
        }
        sortDialogs((SparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    public /* synthetic */ void lambda$null$106$MessagesController() {
        TLRPC.Dialog dialog = this.proxyDialog;
        if (dialog != null) {
            int i = (int) dialog.id;
            if (i < 0) {
                TLRPC.Chat chat = getChat(Integer.valueOf(-i));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.proxyDialog);
                }
            } else {
                removeDialog(dialog);
            }
            this.proxyDialog = null;
            sortDialogs((SparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public void removeProxyDialog() {
        TLRPC.Dialog dialog = this.proxyDialog;
        if (dialog != null) {
            int i = (int) dialog.id;
            if (i < 0) {
                TLRPC.Chat chat = getChat(Integer.valueOf(-i));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.proxyDialog);
                }
            } else {
                removeDialog(dialog);
            }
            this.proxyDialog = null;
            sortDialogs((SparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public boolean isProxyDialog(long j, boolean z) {
        TLRPC.Dialog dialog = this.proxyDialog;
        return dialog != null && dialog.id == j && (!z || this.isLeftProxyChannel);
    }

    private String getUserNameForTyping(TLRPC.User user) {
        if (user == null) {
            return "";
        }
        String str = user.first_name;
        if (str != null && str.length() > 0) {
            return user.first_name;
        }
        String str2 = user.last_name;
        if (str2 == null || str2.length() <= 0) {
            return "";
        }
        return user.last_name;
    }

    private void updatePrintingStrings() {
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (Map.Entry next : this.printingUsers.entrySet()) {
            long longValue = ((Long) next.getKey()).longValue();
            ArrayList arrayList = (ArrayList) next.getValue();
            int i = (int) longValue;
            if (i > 0 || i == 0 || arrayList.size() == 1) {
                PrintingUser printingUser = (PrintingUser) arrayList.get(0);
                TLRPC.User user = getUser(Integer.valueOf(printingUser.userId));
                if (user != null) {
                    TLRPC.SendMessageAction sendMessageAction = printingUser.action;
                    if (sendMessageAction instanceof TLRPC.TL_sendMessageRecordAudioAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsRecordingAudio", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("RecordingAudio", NUM));
                        }
                        longSparseArray2.put(longValue, 1);
                    } else if ((sendMessageAction instanceof TLRPC.TL_sendMessageRecordRoundAction) || (sendMessageAction instanceof TLRPC.TL_sendMessageUploadRoundAction)) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsRecordingRound", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("RecordingRound", NUM));
                        }
                        longSparseArray2.put(longValue, 4);
                    } else if (sendMessageAction instanceof TLRPC.TL_sendMessageUploadAudioAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingAudio", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingAudio", NUM));
                        }
                        longSparseArray2.put(longValue, 2);
                    } else if ((sendMessageAction instanceof TLRPC.TL_sendMessageUploadVideoAction) || (sendMessageAction instanceof TLRPC.TL_sendMessageRecordVideoAction)) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingVideo", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingVideoStatus", NUM));
                        }
                        longSparseArray2.put(longValue, 2);
                    } else if (sendMessageAction instanceof TLRPC.TL_sendMessageUploadDocumentAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingFile", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingFile", NUM));
                        }
                        longSparseArray2.put(longValue, 2);
                    } else if (sendMessageAction instanceof TLRPC.TL_sendMessageUploadPhotoAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingPhoto", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingPhoto", NUM));
                        }
                        longSparseArray2.put(longValue, 2);
                    } else if (sendMessageAction instanceof TLRPC.TL_sendMessageGamePlayAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingGame", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingGame", NUM));
                        }
                        longSparseArray2.put(longValue, 3);
                    } else {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsTypingGroup", NUM, getUserNameForTyping(user)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("Typing", NUM));
                        }
                        longSparseArray2.put(longValue, 0);
                    }
                }
            } else {
                StringBuilder sb = new StringBuilder();
                Iterator it = arrayList.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    TLRPC.User user2 = getUser(Integer.valueOf(((PrintingUser) it.next()).userId));
                    if (user2 != null) {
                        if (sb.length() != 0) {
                            sb.append(", ");
                        }
                        sb.append(getUserNameForTyping(user2));
                        i2++;
                        continue;
                    }
                    if (i2 == 2) {
                        break;
                    }
                }
                if (sb.length() != 0) {
                    if (i2 == 1) {
                        longSparseArray.put(longValue, LocaleController.formatString("IsTypingGroup", NUM, sb.toString()));
                    } else if (arrayList.size() > 2) {
                        String pluralString = LocaleController.getPluralString("AndMoreTypingGroup", arrayList.size() - 2);
                        try {
                            longSparseArray.put(longValue, String.format(pluralString, new Object[]{sb.toString(), Integer.valueOf(arrayList.size() - 2)}));
                        } catch (Exception unused) {
                            longSparseArray.put(longValue, "LOC_ERR: AndMoreTypingGroup");
                        }
                    } else {
                        longSparseArray.put(longValue, LocaleController.formatString("AreTypingGroup", NUM, sb.toString()));
                    }
                    longSparseArray2.put(longValue, 0);
                }
            }
        }
        this.lastPrintingStringCount = longSparseArray.size();
        AndroidUtilities.runOnUIThread(new Runnable(longSparseArray, longSparseArray2) {
            private final /* synthetic */ LongSparseArray f$1;
            private final /* synthetic */ LongSparseArray f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$updatePrintingStrings$110$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$updatePrintingStrings$110$MessagesController(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.printingStrings = longSparseArray;
        this.printingStringsTypes = longSparseArray2;
    }

    public void cancelTyping(int i, long j) {
        LongSparseArray longSparseArray = this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00e2, code lost:
        r1 = getEncryptedChat(java.lang.Integer.valueOf(r3));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendTyping(long r9, int r11, int r12) {
        /*
            r8 = this;
            r0 = 0
            int r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x0007
            return
        L_0x0007:
            android.util.SparseArray<android.util.LongSparseArray<java.lang.Boolean>> r0 = r8.sendingTypings
            java.lang.Object r0 = r0.get(r11)
            android.util.LongSparseArray r0 = (android.util.LongSparseArray) r0
            if (r0 == 0) goto L_0x0018
            java.lang.Object r1 = r0.get(r9)
            if (r1 == 0) goto L_0x0018
            return
        L_0x0018:
            if (r0 != 0) goto L_0x0024
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            android.util.SparseArray<android.util.LongSparseArray<java.lang.Boolean>> r1 = r8.sendingTypings
            r1.put(r11, r0)
        L_0x0024:
            int r1 = (int) r9
            r2 = 32
            long r2 = r9 >> r2
            int r3 = (int) r2
            r2 = 2
            r4 = 1
            if (r1 == 0) goto L_0x00df
            org.telegram.tgnet.TLRPC$TL_messages_setTyping r3 = new org.telegram.tgnet.TLRPC$TL_messages_setTyping
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputPeer r1 = r8.getInputPeer(r1)
            r3.peer = r1
            org.telegram.tgnet.TLRPC$InputPeer r1 = r3.peer
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel
            if (r5 == 0) goto L_0x0050
            int r1 = r1.channel_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r8.getChat(r1)
            if (r1 == 0) goto L_0x004f
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0050
        L_0x004f:
            return
        L_0x0050:
            org.telegram.tgnet.TLRPC$InputPeer r1 = r3.peer
            if (r1 != 0) goto L_0x0055
            return
        L_0x0055:
            if (r11 != 0) goto L_0x005f
            org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x005f:
            if (r11 != r4) goto L_0x0069
            org.telegram.tgnet.TLRPC$TL_sendMessageRecordAudioAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageRecordAudioAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x0069:
            if (r11 != r2) goto L_0x0073
            org.telegram.tgnet.TLRPC$TL_sendMessageCancelAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageCancelAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x0073:
            r1 = 3
            if (r11 != r1) goto L_0x007e
            org.telegram.tgnet.TLRPC$TL_sendMessageUploadDocumentAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageUploadDocumentAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x007e:
            r1 = 4
            if (r11 != r1) goto L_0x0089
            org.telegram.tgnet.TLRPC$TL_sendMessageUploadPhotoAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageUploadPhotoAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x0089:
            r1 = 5
            if (r11 != r1) goto L_0x0094
            org.telegram.tgnet.TLRPC$TL_sendMessageUploadVideoAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageUploadVideoAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x0094:
            r1 = 6
            if (r11 != r1) goto L_0x009f
            org.telegram.tgnet.TLRPC$TL_sendMessageGamePlayAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageGamePlayAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x009f:
            r1 = 7
            if (r11 != r1) goto L_0x00aa
            org.telegram.tgnet.TLRPC$TL_sendMessageRecordRoundAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageRecordRoundAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x00aa:
            r1 = 8
            if (r11 != r1) goto L_0x00b6
            org.telegram.tgnet.TLRPC$TL_sendMessageUploadRoundAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageUploadRoundAction
            r1.<init>()
            r3.action = r1
            goto L_0x00c1
        L_0x00b6:
            r1 = 9
            if (r11 != r1) goto L_0x00c1
            org.telegram.tgnet.TLRPC$TL_sendMessageUploadAudioAction r1 = new org.telegram.tgnet.TLRPC$TL_sendMessageUploadAudioAction
            r1.<init>()
            r3.action = r1
        L_0x00c1:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r4)
            r0.put(r9, r1)
            org.telegram.tgnet.ConnectionsManager r0 = r8.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$IGidZkAWPC5rW-wgDRliLBWyjdc r1 = new org.telegram.messenger.-$$Lambda$MessagesController$IGidZkAWPC5rW-wgDRliLBWyjdc
            r1.<init>(r11, r9)
            int r9 = r0.sendRequest(r3, r1, r2)
            if (r12 == 0) goto L_0x012a
            org.telegram.tgnet.ConnectionsManager r10 = r8.getConnectionsManager()
            r10.bindRequestToGuid(r9, r12)
            goto L_0x012a
        L_0x00df:
            if (r11 == 0) goto L_0x00e2
            return
        L_0x00e2:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r8.getEncryptedChat(r1)
            byte[] r3 = r1.auth_key
            if (r3 == 0) goto L_0x012a
            int r3 = r3.length
            if (r3 <= r4) goto L_0x012a
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
            if (r3 == 0) goto L_0x012a
            org.telegram.tgnet.TLRPC$TL_messages_setEncryptedTyping r3 = new org.telegram.tgnet.TLRPC$TL_messages_setEncryptedTyping
            r3.<init>()
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r5 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat
            r5.<init>()
            r3.peer = r5
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r5 = r3.peer
            int r6 = r1.id
            r5.chat_id = r6
            long r6 = r1.access_hash
            r5.access_hash = r6
            r3.typing = r4
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r4)
            r0.put(r9, r1)
            org.telegram.tgnet.ConnectionsManager r0 = r8.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$C-dHVT_MVteNe3lNHjhC0uNY-cE r1 = new org.telegram.messenger.-$$Lambda$MessagesController$C-dHVT_MVteNe3lNHjhC0uNY-cE
            r1.<init>(r11, r9)
            int r9 = r0.sendRequest(r3, r1, r2)
            if (r12 == 0) goto L_0x012a
            org.telegram.tgnet.ConnectionsManager r10 = r8.getConnectionsManager()
            r10.bindRequestToGuid(r9, r12)
        L_0x012a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.sendTyping(long, int, int):void");
    }

    public /* synthetic */ void lambda$sendTyping$112$MessagesController(int i, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, j) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$null$111$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$111$MessagesController(int i, long j) {
        LongSparseArray longSparseArray = this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    public /* synthetic */ void lambda$sendTyping$114$MessagesController(int i, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, j) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$null$113$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$113$MessagesController(int i, long j) {
        LongSparseArray longSparseArray = this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    /* access modifiers changed from: protected */
    public void removeDeletedMessagesFromArray(long j, ArrayList<TLRPC.Message> arrayList) {
        int i = 0;
        int intValue = this.deletedHistory.get(j, 0).intValue();
        if (intValue != 0) {
            int size = arrayList.size();
            while (i < size) {
                if (arrayList.get(i).id <= intValue) {
                    arrayList.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
        }
    }

    public void loadMessages(long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, boolean z2, boolean z3, int i8) {
        loadMessages(j, i, i2, i3, z, i4, i5, i6, i7, z2, z3, i8, 0, 0, 0, false, 0);
    }

    public void loadMessages(long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, boolean z2, boolean z3, int i8, int i9, int i10, int i11, boolean z4, int i12) {
        loadMessagesInternal(j, i, i2, i3, z, i4, i5, i6, i7, z2, z3, i8, i9, i10, i11, z4, i12, true);
    }

    private void loadMessagesInternal(long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, boolean z2, boolean z3, int i8, int i9, int i10, int i11, boolean z4, int i12, boolean z5) {
        long j2 = j;
        int i13 = i;
        int i14 = i2;
        boolean z6 = z;
        int i15 = i4;
        int i16 = i5;
        int i17 = i6;
        int i18 = i7;
        boolean z7 = z3;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load messages in chat " + j2 + " count " + i13 + " max_id " + i14 + " cache " + z6 + " mindate = " + i15 + " guid " + i16 + " load_type " + i17 + " last_message_id " + i18 + " scheduled " + z7 + " index " + i8 + " firstUnread " + i9 + " unread_count " + i10 + " last_date " + i11 + " queryFromServer " + z4);
        } else {
            int i19 = i8;
            int i20 = i9;
            int i21 = i10;
            boolean z8 = z4;
        }
        int i22 = (int) j2;
        if (z6 || i22 == 0) {
            int i23 = i3;
            int i24 = i16;
            getMessagesStorage().getMessages(j, i, i2, i3, i4, i5, i6, z2, z3, i8);
        } else if (z7) {
            TLRPC.TL_messages_getScheduledHistory tL_messages_getScheduledHistory = new TLRPC.TL_messages_getScheduledHistory();
            tL_messages_getScheduledHistory.peer = getInputPeer(i22);
            tL_messages_getScheduledHistory.hash = i15;
            $$Lambda$MessagesController$ZXeoHsP9YUZHogX5q_6ckSLU6J0 r17 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$MessagesController$ZXeoHsP9YUZHogX5q_6ckSLU6J0 r0 = new RequestDelegate(this, i2, i3, j, i, i5, i9, i7, i10, i11, i6, z2, i8, z4, i12) {
                private final /* synthetic */ MessagesController f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$10;
                private final /* synthetic */ boolean f$11;
                private final /* synthetic */ int f$12;
                private final /* synthetic */ boolean f$13;
                private final /* synthetic */ int f$14;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ int f$7;
                private final /* synthetic */ int f$8;
                private final /* synthetic */ int f$9;

                {
                    this.f$0 = r4;
                    this.f$1 = r5;
                    this.f$2 = r6;
                    this.f$3 = r7;
                    this.f$4 = r9;
                    this.f$5 = r10;
                    this.f$6 = r11;
                    this.f$7 = r12;
                    this.f$8 = r13;
                    this.f$9 = r14;
                    this.f$10 = r15;
                    this.f$11 = r16;
                    this.f$12 = r17;
                    this.f$13 = r18;
                    this.f$14 = r19;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController messagesController = this.f$0;
                    MessagesController messagesController2 = messagesController;
                    messagesController2.lambda$loadMessagesInternal$115$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, tLObject, tL_error);
                }
            };
            getConnectionsManager().bindRequestToGuid(connectionsManager.sendRequest(tL_messages_getScheduledHistory, r17), i5);
        } else {
            int i25 = i16;
            if (!z5 || !((i17 == 3 || i17 == 2) && i18 == 0)) {
                TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
                tL_messages_getHistory.peer = getInputPeer(i22);
                if (i17 == 4) {
                    tL_messages_getHistory.add_offset = (-i13) + 5;
                } else if (i17 == 3) {
                    tL_messages_getHistory.add_offset = (-i13) / 2;
                } else if (i17 == 1) {
                    tL_messages_getHistory.add_offset = (-i13) - 1;
                } else if (i17 == 2 && i14 != 0) {
                    tL_messages_getHistory.add_offset = (-i13) + 6;
                } else if (i22 < 0 && i14 != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-i22)))) {
                    tL_messages_getHistory.add_offset = -1;
                    tL_messages_getHistory.limit++;
                }
                tL_messages_getHistory.limit = i13;
                tL_messages_getHistory.offset_id = i14;
                tL_messages_getHistory.offset_date = i3;
                ConnectionsManager connectionsManager2 = getConnectionsManager();
                $$Lambda$MessagesController$W9Hjq5DFm9fLkmjLcEXNDY3Wnkc r21 = r0;
                $$Lambda$MessagesController$W9Hjq5DFm9fLkmjLcEXNDY3Wnkc r02 = new RequestDelegate(this, j, i, i2, i3, i5, i9, i7, i10, i11, i6, z2, i8, z4, i12) {
                    private final /* synthetic */ MessagesController f$0;
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ int f$10;
                    private final /* synthetic */ boolean f$11;
                    private final /* synthetic */ int f$12;
                    private final /* synthetic */ boolean f$13;
                    private final /* synthetic */ int f$14;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;
                    private final /* synthetic */ int f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ int f$6;
                    private final /* synthetic */ int f$7;
                    private final /* synthetic */ int f$8;
                    private final /* synthetic */ int f$9;

                    {
                        this.f$0 = r4;
                        this.f$1 = r5;
                        this.f$2 = r7;
                        this.f$3 = r8;
                        this.f$4 = r9;
                        this.f$5 = r10;
                        this.f$6 = r11;
                        this.f$7 = r12;
                        this.f$8 = r13;
                        this.f$9 = r14;
                        this.f$10 = r15;
                        this.f$11 = r16;
                        this.f$12 = r17;
                        this.f$13 = r18;
                        this.f$14 = r19;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController messagesController = this.f$0;
                        MessagesController messagesController2 = messagesController;
                        messagesController2.lambda$loadMessagesInternal$117$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, tLObject, tL_error);
                    }
                };
                getConnectionsManager().bindRequestToGuid(connectionsManager2.sendRequest(tL_messages_getHistory, r21), i5);
                return;
            }
            TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
            TLRPC.InputPeer inputPeer = getInputPeer(i22);
            TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            tL_inputDialogPeer.peer = inputPeer;
            tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
            $$Lambda$MessagesController$mSP0lpFs6AbCGriw85_qqZKixvk r16 = r0;
            ConnectionsManager connectionsManager3 = getConnectionsManager();
            $$Lambda$MessagesController$mSP0lpFs6AbCGriw85_qqZKixvk r03 = new RequestDelegate(j, i, i2, i3, i4, i5, i6, z2, i8, i9, i11, z4) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int f$10;
                private final /* synthetic */ int f$11;
                private final /* synthetic */ boolean f$12;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ int f$7;
                private final /* synthetic */ boolean f$8;
                private final /* synthetic */ int f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                    this.f$8 = r10;
                    this.f$9 = r11;
                    this.f$10 = r12;
                    this.f$11 = r13;
                    this.f$12 = r14;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadMessagesInternal$116$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, tLObject, tL_error);
                }
            };
            connectionsManager3.sendRequest(tL_messages_getPeerDialogs, r16);
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$115$MessagesController(int i, int i2, long j, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, int i10, boolean z2, int i11, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i12;
        int i13 = i2;
        if (tLObject != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            if (!(messages_messages instanceof TLRPC.TL_messages_messagesNotModified)) {
                if (i13 == 0 || messages_messages.messages.isEmpty()) {
                    i12 = i;
                } else {
                    ArrayList<TLRPC.Message> arrayList = messages_messages.messages;
                    int i14 = arrayList.get(arrayList.size() - 1).id;
                    int size = messages_messages.messages.size() - 1;
                    while (true) {
                        if (size < 0) {
                            break;
                        }
                        TLRPC.Message message = messages_messages.messages.get(size);
                        if (message.date > i13) {
                            i14 = message.id;
                            break;
                        }
                        size--;
                    }
                    i12 = i14;
                }
                processLoadedMessages(messages_messages, j, i3, i12, i2, false, i4, i5, i6, i7, i8, i9, z, false, true, i10, z2, i11);
            }
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$116$MessagesController(long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, int i8, int i9, boolean z2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty()) {
                TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(0);
                if (dialog.top_message != 0) {
                    TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                    tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
                    tL_messages_dialogs.users = tL_messages_peerDialogs.users;
                    tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
                    tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
                    getMessagesStorage().putDialogs(tL_messages_dialogs, 0);
                }
                loadMessagesInternal(j, i, i2, i3, false, i4, i5, i6, dialog.top_message, z, false, i7, i8, dialog.unread_count, i9, z2, dialog.unread_mentions_count, false);
            }
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$117$MessagesController(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, int i10, boolean z2, int i11, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i12;
        int i13 = i3;
        if (tLObject != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            removeDeletedMessagesFromArray(j, messages_messages.messages);
            if (messages_messages.messages.size() > i) {
                messages_messages.messages.remove(0);
            }
            if (i13 == 0 || messages_messages.messages.isEmpty()) {
                i12 = i2;
            } else {
                ArrayList<TLRPC.Message> arrayList = messages_messages.messages;
                int i14 = arrayList.get(arrayList.size() - 1).id;
                int size = messages_messages.messages.size() - 1;
                while (true) {
                    if (size < 0) {
                        break;
                    }
                    TLRPC.Message message = messages_messages.messages.get(size);
                    if (message.date > i13) {
                        i14 = message.id;
                        break;
                    }
                    size--;
                }
                i12 = i14;
            }
            processLoadedMessages(messages_messages, j, i, i12, i3, false, i4, i5, i6, i7, i8, i9, z, false, false, i10, z2, i11);
        }
    }

    public void reloadWebPages(long j, HashMap<String, ArrayList<MessageObject>> hashMap, boolean z) {
        HashMap<String, ArrayList<MessageObject>> hashMap2 = z ? this.reloadingScheduledWebpages : this.reloadingWebpages;
        LongSparseArray<ArrayList<MessageObject>> longSparseArray = z ? this.reloadingScheduledWebpagesPending : this.reloadingWebpagesPending;
        for (Map.Entry next : hashMap.entrySet()) {
            String str = (String) next.getKey();
            ArrayList arrayList = (ArrayList) next.getValue();
            ArrayList arrayList2 = hashMap2.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
                hashMap2.put(str, arrayList2);
            }
            arrayList2.addAll(arrayList);
            TLRPC.TL_messages_getWebPagePreview tL_messages_getWebPagePreview = new TLRPC.TL_messages_getWebPagePreview();
            tL_messages_getWebPagePreview.message = str;
            getConnectionsManager().sendRequest(tL_messages_getWebPagePreview, new RequestDelegate(hashMap2, str, longSparseArray, j, z) {
                private final /* synthetic */ HashMap f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ LongSparseArray f$3;
                private final /* synthetic */ long f$4;
                private final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r7;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$reloadWebPages$119$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reloadWebPages$119$MessagesController(HashMap hashMap, String str, LongSparseArray longSparseArray, long j, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(hashMap, str, tLObject, longSparseArray, j, z) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ LongSparseArray f$4;
            private final /* synthetic */ long f$5;
            private final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r8;
            }

            public final void run() {
                MessagesController.this.lambda$null$118$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$118$MessagesController(HashMap hashMap, String str, TLObject tLObject, LongSparseArray longSparseArray, long j, boolean z) {
        TLObject tLObject2 = tLObject;
        ArrayList arrayList = (ArrayList) hashMap.remove(str);
        if (arrayList != null) {
            TLRPC.TL_messages_messages tL_messages_messages = new TLRPC.TL_messages_messages();
            if (!(tLObject2 instanceof TLRPC.TL_messageMediaWebPage)) {
                for (int i = 0; i < arrayList.size(); i++) {
                    ((MessageObject) arrayList.get(i)).messageOwner.media.webpage = new TLRPC.TL_webPageEmpty();
                    tL_messages_messages.messages.add(((MessageObject) arrayList.get(i)).messageOwner);
                }
            } else {
                TLRPC.TL_messageMediaWebPage tL_messageMediaWebPage = (TLRPC.TL_messageMediaWebPage) tLObject2;
                TLRPC.WebPage webPage = tL_messageMediaWebPage.webpage;
                if ((webPage instanceof TLRPC.TL_webPage) || (webPage instanceof TLRPC.TL_webPageEmpty)) {
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        ((MessageObject) arrayList.get(i2)).messageOwner.media.webpage = tL_messageMediaWebPage.webpage;
                        if (i2 == 0) {
                            ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(i2)).messageOwner);
                        }
                        tL_messages_messages.messages.add(((MessageObject) arrayList.get(i2)).messageOwner);
                    }
                } else {
                    LongSparseArray longSparseArray2 = longSparseArray;
                    longSparseArray.put(webPage.id, arrayList);
                }
            }
            if (!tL_messages_messages.messages.isEmpty()) {
                getMessagesStorage().putMessages((TLRPC.messages_Messages) tL_messages_messages, j, -2, 0, false, z);
                getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList);
            }
        }
    }

    public void processLoadedMessages(TLRPC.messages_Messages messages_messages, long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, int i8, int i9, boolean z2, boolean z3, boolean z4, int i10, boolean z5, int i11) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processLoadedMessages size " + messages_messages.messages.size() + " in chat " + j + " count " + i + " max_id " + i2 + " cache " + z + " guid " + i4 + " load_type " + i9 + " last_message_id " + i6 + " isChannel " + z2 + " index " + i10 + " firstUnread " + i5 + " unread_count " + i7 + " last_date " + i8 + " queryFromServer " + z5);
        } else {
            TLRPC.messages_Messages messages_messages2 = messages_messages;
            long j2 = j;
            int i12 = i;
            int i13 = i2;
            boolean z6 = z;
            int i14 = i4;
            int i15 = i5;
            int i16 = i6;
            int i17 = i7;
            int i18 = i8;
            int i19 = i9;
            boolean z7 = z2;
            int i20 = i10;
            boolean z8 = z5;
        }
        Utilities.stageQueue.postRunnable(new Runnable(this, messages_messages, j, z4, z, i, i9, z5, i5, i2, i3, i4, i6, z2, i10, i7, i8, i11, z3) {
            private final /* synthetic */ MessagesController f$0;
            private final /* synthetic */ TLRPC.messages_Messages f$1;
            private final /* synthetic */ int f$10;
            private final /* synthetic */ int f$11;
            private final /* synthetic */ int f$12;
            private final /* synthetic */ boolean f$13;
            private final /* synthetic */ int f$14;
            private final /* synthetic */ int f$15;
            private final /* synthetic */ int f$16;
            private final /* synthetic */ int f$17;
            private final /* synthetic */ boolean f$18;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ boolean f$7;
            private final /* synthetic */ int f$8;
            private final /* synthetic */ int f$9;

            {
                this.f$0 = r4;
                this.f$1 = r5;
                this.f$2 = r6;
                this.f$3 = r8;
                this.f$4 = r9;
                this.f$5 = r10;
                this.f$6 = r11;
                this.f$7 = r12;
                this.f$8 = r13;
                this.f$9 = r14;
                this.f$10 = r15;
                this.f$11 = r16;
                this.f$12 = r17;
                this.f$13 = r18;
                this.f$14 = r19;
                this.f$15 = r20;
                this.f$16 = r21;
                this.f$17 = r22;
                this.f$18 = r23;
            }

            public final void run() {
                MessagesController messagesController = this.f$0;
                MessagesController messagesController2 = messagesController;
                messagesController2.lambda$processLoadedMessages$123$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:117:0x02b0, code lost:
        if (r3[0] < 110) goto L_0x02b7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x030b A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedMessages$123$MessagesController(org.telegram.tgnet.TLRPC.messages_Messages r26, long r27, boolean r29, boolean r30, int r31, int r32, boolean r33, int r34, int r35, int r36, int r37, int r38, boolean r39, int r40, int r41, int r42, int r43, boolean r44) {
        /*
            r25 = this;
            r15 = r25
            r14 = r26
            r12 = r27
            r11 = r29
            boolean r0 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messages_channelMessages
            r10 = 1
            if (r0 == 0) goto L_0x006d
            int r0 = (int) r12
            int r6 = -r0
            if (r11 != 0) goto L_0x004a
            android.util.SparseIntArray r0 = r15.channelsPts
            int r0 = r0.get(r6)
            if (r0 != 0) goto L_0x004a
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            int r0 = r0.getChannelPtsSync(r6)
            if (r0 != 0) goto L_0x004a
            android.util.SparseIntArray r0 = r15.channelsPts
            int r1 = r14.pts
            r0.put(r6, r1)
            android.util.SparseIntArray r0 = r15.needShortPollChannels
            int r0 = r0.indexOfKey(r6)
            if (r0 < 0) goto L_0x0045
            android.util.SparseIntArray r0 = r15.shortPollChannels
            int r0 = r0.indexOfKey(r6)
            if (r0 >= 0) goto L_0x0045
            r2 = 2
            r3 = 0
            r5 = 0
            r0 = r25
            r1 = r6
            r0.getChannelDifference(r1, r2, r3, r5)
            goto L_0x0048
        L_0x0045:
            r15.getChannelDifference(r6)
        L_0x0048:
            r0 = 1
            goto L_0x004b
        L_0x004a:
            r0 = 0
        L_0x004b:
            r1 = 0
        L_0x004c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r14.chats
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x006a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r14.chats
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            int r3 = r2.id
            if (r3 != r6) goto L_0x0067
            boolean r1 = r2.megagroup
            r19 = r0
            r20 = r1
            goto L_0x0071
        L_0x0067:
            int r1 = r1 + 1
            goto L_0x004c
        L_0x006a:
            r19 = r0
            goto L_0x006f
        L_0x006d:
            r19 = 0
        L_0x006f:
            r20 = 0
        L_0x0071:
            int r0 = (int) r12
            r1 = 32
            long r1 = r12 >> r1
            int r2 = (int) r1
            if (r30 != 0) goto L_0x007e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r14.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r1)
        L_0x007e:
            if (r2 == r10) goto L_0x0138
            if (r0 == 0) goto L_0x0138
            if (r30 == 0) goto L_0x0138
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r0 = r14.messages
            int r0 = r0.size()
            r1 = 0
            if (r0 == 0) goto L_0x00ac
            if (r11 == 0) goto L_0x0138
            long r3 = android.os.SystemClock.elapsedRealtime()
            android.util.LongSparseArray<java.lang.Long> r0 = r15.lastScheduledServerQueryTime
            java.lang.Long r5 = java.lang.Long.valueOf(r1)
            java.lang.Object r0 = r0.get(r12, r5)
            java.lang.Long r0 = (java.lang.Long) r0
            long r5 = r0.longValue()
            long r3 = r3 - r5
            r5 = 60000(0xea60, double:2.9644E-319)
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x0138
        L_0x00ac:
            if (r11 == 0) goto L_0x00ff
            android.util.LongSparseArray<java.lang.Long> r0 = r15.lastScheduledServerQueryTime
            long r3 = android.os.SystemClock.elapsedRealtime()
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r0.put(r12, r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r0 = r14.messages
            int r0 = r0.size()
            r2 = r1
            r1 = 0
        L_0x00c3:
            if (r1 >= r0) goto L_0x00f9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r14.messages
            java.lang.Object r4 = r4.get(r1)
            org.telegram.tgnet.TLRPC$Message r4 = (org.telegram.tgnet.TLRPC.Message) r4
            int r5 = r4.id
            if (r5 >= 0) goto L_0x00d2
            goto L_0x00f5
        L_0x00d2:
            r6 = 20261(0x4var_, double:1.00103E-319)
            long r2 = r2 * r6
            r16 = 2147483648(0x80000000, double:1.0609978955E-314)
            long r2 = r2 + r16
            long r9 = (long) r5
            long r2 = r2 + r9
            long r2 = r2 % r16
            long r2 = r2 * r6
            long r2 = r2 + r16
            int r5 = r4.edit_date
            long r8 = (long) r5
            long r2 = r2 + r8
            long r2 = r2 % r16
            long r2 = r2 * r6
            long r2 = r2 + r16
            int r4 = r4.date
            long r4 = (long) r4
            long r2 = r2 + r4
            long r2 = r2 % r16
        L_0x00f5:
            int r1 = r1 + 1
            r10 = 1
            goto L_0x00c3
        L_0x00f9:
            int r0 = (int) r2
            r10 = 1
            int r0 = r0 - r10
            r16 = r0
            goto L_0x0101
        L_0x00ff:
            r16 = 0
        L_0x0101:
            org.telegram.messenger.-$$Lambda$MessagesController$0fpTg472XUFWRngdu80nTLaFYAA r22 = new org.telegram.messenger.-$$Lambda$MessagesController$0fpTg472XUFWRngdu80nTLaFYAA
            r0 = r22
            r1 = r25
            r2 = r27
            r4 = r31
            r5 = r32
            r6 = r33
            r7 = r34
            r8 = r35
            r9 = r36
            r10 = r16
            r11 = r37
            r12 = r38
            r13 = r39
            r14 = r29
            r15 = r40
            r16 = r41
            r17 = r42
            r18 = r43
            r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r22)
            r8 = r26
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r0 = r8.messages
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0139
            return
        L_0x0138:
            r8 = r14
        L_0x0139:
            android.util.SparseArray r9 = new android.util.SparseArray
            r9.<init>()
            android.util.SparseArray r10 = new android.util.SparseArray
            r10.<init>()
            r0 = 0
        L_0x0144:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r8.users
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x015c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r8.users
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            int r2 = r1.id
            r9.put(r2, r1)
            int r0 = r0 + 1
            goto L_0x0144
        L_0x015c:
            r0 = 0
        L_0x015d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r8.chats
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x0175
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r8.chats
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            int r2 = r1.id
            r10.put(r2, r1)
            int r0 = r0 + 1
            goto L_0x015d
        L_0x0175:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r0 = r8.messages
            int r11 = r0.size()
            r15 = r25
            if (r30 != 0) goto L_0x0246
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r15.dialogs_read_inbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r27)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x01a6
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            r12 = r27
            r14 = 0
            int r0 = r0.getDialogReadMax(r14, r12)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r15.dialogs_read_inbox_max
            java.lang.Long r2 = java.lang.Long.valueOf(r27)
            r1.put(r2, r0)
            goto L_0x01a9
        L_0x01a6:
            r12 = r27
            r14 = 0
        L_0x01a9:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r15.dialogs_read_outbox_max
            java.lang.Long r2 = java.lang.Long.valueOf(r27)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x01ce
            org.telegram.messenger.MessagesStorage r1 = r25.getMessagesStorage()
            r7 = 1
            int r1 = r1.getDialogReadMax(r7, r12)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r15.dialogs_read_outbox_max
            java.lang.Long r3 = java.lang.Long.valueOf(r27)
            r2.put(r3, r1)
            goto L_0x01cf
        L_0x01ce:
            r7 = 1
        L_0x01cf:
            r2 = 0
        L_0x01d0:
            if (r2 >= r11) goto L_0x022f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r3 = r8.messages
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC.Message) r3
            if (r20 == 0) goto L_0x01e3
            int r4 = r3.flags
            r5 = -2147483648(0xfffffffvar_, float:-0.0)
            r4 = r4 | r5
            r3.flags = r4
        L_0x01e3:
            r6 = r29
            if (r6 != 0) goto L_0x022c
            org.telegram.tgnet.TLRPC$MessageAction r4 = r3.action
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r5 == 0) goto L_0x0208
            int r4 = r4.user_id
            java.lang.Object r4 = r9.get(r4)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC.User) r4
            if (r4 == 0) goto L_0x0208
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x0208
            org.telegram.tgnet.TLRPC$TL_replyKeyboardHide r4 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide
            r4.<init>()
            r3.reply_markup = r4
            int r4 = r3.flags
            r4 = r4 | 64
            r3.flags = r4
        L_0x0208:
            org.telegram.tgnet.TLRPC$MessageAction r4 = r3.action
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r5 != 0) goto L_0x0228
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0213
            goto L_0x0228
        L_0x0213:
            boolean r4 = r3.out
            if (r4 == 0) goto L_0x0219
            r4 = r1
            goto L_0x021a
        L_0x0219:
            r4 = r0
        L_0x021a:
            int r4 = r4.intValue()
            int r5 = r3.id
            if (r4 >= r5) goto L_0x0224
            r4 = 1
            goto L_0x0225
        L_0x0224:
            r4 = 0
        L_0x0225:
            r3.unread = r4
            goto L_0x022c
        L_0x0228:
            r3.unread = r14
            r3.media_unread = r14
        L_0x022c:
            int r2 = r2 + 1
            goto L_0x01d0
        L_0x022f:
            r6 = r29
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            r1 = r26
            r2 = r27
            r4 = r32
            r5 = r35
            r14 = r6
            r6 = r19
            r7 = r29
            r0.putMessages((org.telegram.tgnet.TLRPC.messages_Messages) r1, (long) r2, (int) r4, (int) r5, (boolean) r6, (boolean) r7)
            goto L_0x024a
        L_0x0246:
            r12 = r27
            r14 = r29
        L_0x024a:
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            r5 = 0
        L_0x025a:
            if (r5 >= r11) goto L_0x0313
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r8.messages
            java.lang.Object r1 = r1.get(r5)
            r4 = r1
            org.telegram.tgnet.TLRPC$Message r4 = (org.telegram.tgnet.TLRPC.Message) r4
            r4.dialog_id = r12
            org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject
            int r2 = r15.currentAccount
            r16 = 1
            r1 = r3
            r23 = r3
            r3 = r4
            r24 = r4
            r4 = r9
            r17 = r5
            r5 = r10
            r15 = r6
            r6 = r16
            r1.<init>((int) r2, (org.telegram.tgnet.TLRPC.Message) r3, (android.util.SparseArray<org.telegram.tgnet.TLRPC.User>) r4, (android.util.SparseArray<org.telegram.tgnet.TLRPC.Chat>) r5, (boolean) r6)
            r1 = r23
            r1.scheduled = r14
            r7.add(r1)
            if (r30 == 0) goto L_0x0309
            r2 = r24
            boolean r3 = r2.legacy
            r4 = 110(0x6e, float:1.54E-43)
            if (r3 == 0) goto L_0x029c
            int r3 = r2.layer
            if (r3 >= r4) goto L_0x029c
            int r3 = r2.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.add(r3)
            goto L_0x02c1
        L_0x029c:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported
            if (r5 == 0) goto L_0x02c1
            byte[] r3 = r3.bytes
            if (r3 == 0) goto L_0x02c1
            int r5 = r3.length
            if (r5 == 0) goto L_0x02b5
            int r5 = r3.length
            r6 = 1
            if (r5 != r6) goto L_0x02b3
            r5 = 0
            byte r3 = r3[r5]
            if (r3 >= r4) goto L_0x02c3
            goto L_0x02b7
        L_0x02b3:
            r5 = 0
            goto L_0x02c3
        L_0x02b5:
            r5 = 0
            r6 = 1
        L_0x02b7:
            int r3 = r2.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.add(r3)
            goto L_0x02c3
        L_0x02c1:
            r5 = 0
            r6 = 1
        L_0x02c3:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r4 == 0) goto L_0x030b
            org.telegram.tgnet.TLRPC$WebPage r3 = r3.webpage
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending
            if (r4 == 0) goto L_0x02e5
            int r3 = r3.date
            org.telegram.tgnet.ConnectionsManager r4 = r25.getConnectionsManager()
            int r4 = r4.getCurrentTime()
            if (r3 > r4) goto L_0x02e5
            int r1 = r2.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r0.add(r1)
            goto L_0x030b
        L_0x02e5:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r3 = r3.webpage
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webPageUrlPending
            if (r4 == 0) goto L_0x030b
            java.lang.String r3 = r3.url
            java.lang.Object r3 = r15.get(r3)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            if (r3 != 0) goto L_0x0305
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            java.lang.String r2 = r2.url
            r15.put(r2, r3)
        L_0x0305:
            r3.add(r1)
            goto L_0x030b
        L_0x0309:
            r5 = 0
            r6 = 1
        L_0x030b:
            int r1 = r17 + 1
            r5 = r1
            r6 = r15
            r15 = r25
            goto L_0x025a
        L_0x0313:
            r15 = r6
            org.telegram.messenger.-$$Lambda$MessagesController$16RHpu2Fb-Sswwrxh8-Ypcw4Xso r22 = new org.telegram.messenger.-$$Lambda$MessagesController$16RHpu2Fb-Sswwrxh8-Ypcw4Xso
            r20 = r0
            r0 = r22
            r1 = r25
            r2 = r26
            r3 = r30
            r4 = r29
            r5 = r33
            r6 = r32
            r11 = r7
            r7 = r34
            r8 = r31
            r9 = r27
            r12 = r38
            r13 = r41
            r14 = r42
            r21 = r15
            r15 = r44
            r16 = r37
            r17 = r40
            r18 = r35
            r19 = r43
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r22)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedMessages$123$MessagesController(org.telegram.tgnet.TLRPC$messages_Messages, long, boolean, boolean, int, int, boolean, int, int, int, int, int, boolean, int, int, int, int, boolean):void");
    }

    public /* synthetic */ void lambda$null$120$MessagesController(long j, int i, int i2, boolean z, int i3, int i4, int i5, int i6, int i7, int i8, boolean z2, boolean z3, int i9, int i10, int i11, int i12) {
        loadMessages(j, i, (i2 != 2 || !z) ? i4 : i3, i5, false, i6, i7, i2, i8, z2, z3, i9, i3, i10, i11, z, i12);
    }

    public /* synthetic */ void lambda$null$122$MessagesController(TLRPC.messages_Messages messages_messages, boolean z, boolean z2, boolean z3, int i, int i2, int i3, long j, ArrayList arrayList, int i4, int i5, int i6, boolean z4, int i7, int i8, int i9, int i10, ArrayList arrayList2, HashMap hashMap) {
        int i11;
        long j2;
        boolean z5;
        MessagesController messagesController;
        int i12;
        int i13;
        TLRPC.messages_Messages messages_messages2 = messages_messages;
        boolean z6 = z;
        boolean z7 = z2;
        long j3 = j;
        putUsers(messages_messages2.users, z6);
        putChats(messages_messages2.chats, z6);
        if (z7) {
            int i14 = i;
            i11 = 0;
        } else {
            int i15 = i;
            if (!z3 || i15 != 2) {
                i12 = i2;
                i13 = Integer.MAX_VALUE;
            } else {
                i13 = Integer.MAX_VALUE;
                for (int i16 = 0; i16 < messages_messages2.messages.size(); i16++) {
                    TLRPC.Message message = messages_messages2.messages.get(i16);
                    if (!message.out || message.from_scheduled) {
                        int i17 = message.id;
                        if (i17 > i2 && i17 < i13) {
                            i13 = i17;
                        }
                    } else {
                        int i18 = i2;
                    }
                }
                i12 = i2;
            }
            i11 = i13 == Integer.MAX_VALUE ? i12 : i13;
        }
        int i19 = i3;
        if (z7 && i19 == 1) {
            getNotificationCenter().postNotificationName(NotificationCenter.scheduledMessagesUpdated, Long.valueOf(j), Integer.valueOf(arrayList.size()));
        }
        if (((int) j3) != 0) {
            getMediaDataController().loadReplyMessagesForMessages(arrayList, j, z2, new Runnable(this, j, i3, arrayList, z, i11, i4, i5, i6, i, z4, i7, i8, i9, i10, z2) {
                private final /* synthetic */ MessagesController f$0;
                private final /* synthetic */ long f$1;
                private final /* synthetic */ boolean f$10;
                private final /* synthetic */ int f$11;
                private final /* synthetic */ int f$12;
                private final /* synthetic */ int f$13;
                private final /* synthetic */ int f$14;
                private final /* synthetic */ boolean f$15;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ ArrayList f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ int f$7;
                private final /* synthetic */ int f$8;
                private final /* synthetic */ int f$9;

                {
                    this.f$0 = r4;
                    this.f$1 = r5;
                    this.f$2 = r7;
                    this.f$3 = r8;
                    this.f$4 = r9;
                    this.f$5 = r10;
                    this.f$6 = r11;
                    this.f$7 = r12;
                    this.f$8 = r13;
                    this.f$9 = r14;
                    this.f$10 = r15;
                    this.f$11 = r16;
                    this.f$12 = r17;
                    this.f$13 = r18;
                    this.f$14 = r19;
                    this.f$15 = r20;
                }

                public final void run() {
                    MessagesController messagesController = this.f$0;
                    MessagesController messagesController2 = messagesController;
                    messagesController2.lambda$null$121$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15);
                }
            });
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i3), arrayList, Boolean.valueOf(z), Integer.valueOf(i11), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i), Boolean.valueOf(z4), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i9), Integer.valueOf(i10), Boolean.valueOf(z2));
        }
        if (!arrayList2.isEmpty()) {
            messagesController = this;
            z5 = z2;
            j2 = j;
            messagesController.reloadMessages(arrayList2, j2, z5);
        } else {
            messagesController = this;
            z5 = z2;
            j2 = j;
        }
        if (!hashMap.isEmpty()) {
            messagesController.reloadWebPages(j2, hashMap, z5);
        }
    }

    public /* synthetic */ void lambda$null$121$MessagesController(long j, int i, ArrayList arrayList, boolean z, int i2, int i3, int i4, int i5, int i6, boolean z2, int i7, int i8, int i9, int i10, boolean z3) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i), arrayList, Boolean.valueOf(z), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Boolean.valueOf(z2), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i9), Integer.valueOf(i10), Boolean.valueOf(z3));
    }

    public void loadHintDialogs() {
        if (this.hintDialogs.isEmpty() && !TextUtils.isEmpty(this.installReferer)) {
            TLRPC.TL_help_getRecentMeUrls tL_help_getRecentMeUrls = new TLRPC.TL_help_getRecentMeUrls();
            tL_help_getRecentMeUrls.referer = this.installReferer;
            getConnectionsManager().sendRequest(tL_help_getRecentMeUrls, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadHintDialogs$125$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadHintDialogs$125$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$124$MessagesController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$124$MessagesController(TLObject tLObject) {
        TLRPC.TL_help_recentMeUrls tL_help_recentMeUrls = (TLRPC.TL_help_recentMeUrls) tLObject;
        putUsers(tL_help_recentMeUrls.users, false);
        putChats(tL_help_recentMeUrls.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(tL_help_recentMeUrls.urls);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private TLRPC.TL_dialogFolder ensureFolderDialogExists(int i, boolean[] zArr) {
        if (i == 0) {
            return null;
        }
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC.Dialog dialog = this.dialogs_dict.get(makeFolderDialogId);
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            if (zArr != null) {
                zArr[0] = false;
            }
            return (TLRPC.TL_dialogFolder) dialog;
        }
        if (zArr != null) {
            zArr[0] = true;
        }
        TLRPC.TL_dialogFolder tL_dialogFolder = new TLRPC.TL_dialogFolder();
        tL_dialogFolder.id = makeFolderDialogId;
        tL_dialogFolder.peer = new TLRPC.TL_peerUser();
        tL_dialogFolder.folder = new TLRPC.TL_folder();
        TLRPC.TL_folder tL_folder = tL_dialogFolder.folder;
        tL_folder.id = i;
        tL_folder.title = LocaleController.getString("ArchivedChats", NUM);
        tL_dialogFolder.pinned = true;
        int i2 = 0;
        for (int i3 = 0; i3 < this.allDialogs.size(); i3++) {
            TLRPC.Dialog dialog2 = this.allDialogs.get(i3);
            if (dialog2.pinned) {
                i2 = Math.max(dialog2.pinnedNum, i2);
            } else if (dialog2.id != this.proxyDialogId) {
                break;
            }
        }
        tL_dialogFolder.pinnedNum = i2 + 1;
        TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
        tL_messages_dialogs.dialogs.add(tL_dialogFolder);
        getMessagesStorage().putDialogs(tL_messages_dialogs, 1);
        this.dialogs_dict.put(makeFolderDialogId, tL_dialogFolder);
        this.allDialogs.add(0, tL_dialogFolder);
        return tL_dialogFolder;
    }

    /* access modifiers changed from: private */
    /* renamed from: removeFolder */
    public void lambda$onFolderEmpty$126$MessagesController(int i) {
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC.Dialog dialog = this.dialogs_dict.get(makeFolderDialogId);
        if (dialog != null) {
            this.dialogs_dict.remove(makeFolderDialogId);
            this.allDialogs.remove(dialog);
            sortDialogs((SparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            getNotificationCenter().postNotificationName(NotificationCenter.folderBecomeEmpty, Integer.valueOf(i));
        }
    }

    /* access modifiers changed from: protected */
    public void onFolderEmpty(int i) {
        if (getUserConfig().getDialogLoadOffsets(i)[0] == Integer.MAX_VALUE) {
            lambda$onFolderEmpty$126$MessagesController(i);
            return;
        }
        loadDialogs(i, 0, 10, false, new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$onFolderEmpty$126$MessagesController(this.f$1);
            }
        });
    }

    public void checkIfFolderEmpty(int i) {
        if (i != 0) {
            getMessagesStorage().checkIfFolderEmpty(i);
        }
    }

    public int addDialogToFolder(long j, int i, int i2, long j2) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(Long.valueOf(j));
        return addDialogToFolder(arrayList, i, i2, (ArrayList<TLRPC.TL_inputFolderPeer>) null, j2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0143 A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0145  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int addDialogToFolder(java.util.ArrayList<java.lang.Long> r22, int r23, int r24, java.util.ArrayList<org.telegram.tgnet.TLRPC.TL_inputFolderPeer> r25, long r26) {
        /*
            r21 = this;
            r1 = r21
            r8 = r23
            r0 = r24
            org.telegram.tgnet.TLRPC$TL_folders_editPeerFolders r9 = new org.telegram.tgnet.TLRPC$TL_folders_editPeerFolders
            r9.<init>()
            r10 = 0
            r13 = 1
            r14 = 0
            int r2 = (r26 > r10 ? 1 : (r26 == r10 ? 0 : -1))
            if (r2 != 0) goto L_0x0116
            org.telegram.messenger.UserConfig r2 = r21.getUserConfig()
            int r15 = r2.getClientUserId()
            int r7 = r22.size()
            r2 = 0
            r3 = 0
            r5 = 0
            r16 = 0
        L_0x0024:
            if (r5 >= r7) goto L_0x00ba
            r6 = r22
            java.lang.Object r4 = r6.get(r5)
            java.lang.Long r4 = (java.lang.Long) r4
            long r10 = r4.longValue()
            boolean r4 = org.telegram.messenger.DialogObject.isPeerDialogId(r10)
            if (r4 != 0) goto L_0x003f
            boolean r4 = org.telegram.messenger.DialogObject.isSecretDialogId(r10)
            if (r4 != 0) goto L_0x003f
            goto L_0x005e
        L_0x003f:
            if (r8 != r13) goto L_0x0054
            long r12 = (long) r15
            int r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r4 == 0) goto L_0x005e
            r12 = 777000(0xbdb28, double:3.83889E-318)
            int r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r4 == 0) goto L_0x005e
            boolean r4 = r1.isProxyDialog(r10, r14)
            if (r4 == 0) goto L_0x0054
            goto L_0x005e
        L_0x0054:
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r4 = r1.dialogs_dict
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC.Dialog) r4
            if (r4 != 0) goto L_0x0064
        L_0x005e:
            r19 = r5
            r20 = r7
            r12 = 1
            goto L_0x00b1
        L_0x0064:
            r4.folder_id = r8
            if (r0 <= 0) goto L_0x006e
            r12 = 1
            r4.pinned = r12
            r4.pinnedNum = r0
            goto L_0x0073
        L_0x006e:
            r12 = 1
            r4.pinned = r14
            r4.pinnedNum = r14
        L_0x0073:
            if (r3 != 0) goto L_0x007a
            boolean[] r3 = new boolean[r12]
            r1.ensureFolderDialogExists(r8, r3)
        L_0x007a:
            r13 = r3
            boolean r2 = org.telegram.messenger.DialogObject.isSecretDialogId(r10)
            if (r2 == 0) goto L_0x0092
            org.telegram.messenger.MessagesStorage r2 = r21.getMessagesStorage()
            r3 = 0
            r4 = 0
            r19 = r5
            r5 = r10
            r20 = r7
            r7 = r23
            r2.setDialogsFolderId(r3, r4, r5, r7)
            goto L_0x00af
        L_0x0092:
            r19 = r5
            r20 = r7
            org.telegram.tgnet.TLRPC$TL_inputFolderPeer r2 = new org.telegram.tgnet.TLRPC$TL_inputFolderPeer
            r2.<init>()
            r2.folder_id = r8
            int r3 = (int) r10
            org.telegram.tgnet.TLRPC$InputPeer r3 = r1.getInputPeer(r3)
            r2.peer = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputFolderPeer> r3 = r9.folder_peers
            r3.add(r2)
            int r2 = r2.getObjectSize()
            int r16 = r16 + r2
        L_0x00af:
            r3 = r13
            r2 = 1
        L_0x00b1:
            int r5 = r19 + 1
            r7 = r20
            r10 = 0
            r13 = 1
            goto L_0x0024
        L_0x00ba:
            r12 = 1
            if (r2 != 0) goto L_0x00be
            return r14
        L_0x00be:
            r2 = 0
            r1.sortDialogs(r2)
            org.telegram.messenger.NotificationCenter r0 = r21.getNotificationCenter()
            int r4 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            java.lang.Object[] r5 = new java.lang.Object[r14]
            r0.postNotificationName(r4, r5)
            if (r16 == 0) goto L_0x0110
            org.telegram.tgnet.NativeByteBuffer r4 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0100 }
            int r0 = r16 + 12
            r4.<init>((int) r0)     // Catch:{ Exception -> 0x0100 }
            r0 = 17
            r4.writeInt32(r0)     // Catch:{ Exception -> 0x00fe }
            r4.writeInt32(r8)     // Catch:{ Exception -> 0x00fe }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputFolderPeer> r0 = r9.folder_peers     // Catch:{ Exception -> 0x00fe }
            int r0 = r0.size()     // Catch:{ Exception -> 0x00fe }
            r4.writeInt32(r0)     // Catch:{ Exception -> 0x00fe }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputFolderPeer> r0 = r9.folder_peers     // Catch:{ Exception -> 0x00fe }
            int r0 = r0.size()     // Catch:{ Exception -> 0x00fe }
            r2 = 0
        L_0x00ee:
            if (r2 >= r0) goto L_0x0105
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputFolderPeer> r5 = r9.folder_peers     // Catch:{ Exception -> 0x00fe }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ Exception -> 0x00fe }
            org.telegram.tgnet.TLRPC$TL_inputFolderPeer r5 = (org.telegram.tgnet.TLRPC.TL_inputFolderPeer) r5     // Catch:{ Exception -> 0x00fe }
            r5.serializeToStream(r4)     // Catch:{ Exception -> 0x00fe }
            int r2 = r2 + 1
            goto L_0x00ee
        L_0x00fe:
            r0 = move-exception
            goto L_0x0102
        L_0x0100:
            r0 = move-exception
            r4 = r2
        L_0x0102:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0105:
            org.telegram.messenger.MessagesStorage r0 = r21.getMessagesStorage()
            long r4 = r0.createPendingTask(r4)
            r17 = r4
            goto L_0x0112
        L_0x0110:
            r17 = 0
        L_0x0112:
            r0 = r3
            r2 = r17
            goto L_0x011f
        L_0x0116:
            r0 = r25
            r2 = 0
            r12 = 1
            r9.folder_peers = r0
            r0 = r2
            r2 = r26
        L_0x011f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputFolderPeer> r4 = r9.folder_peers
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0141
            org.telegram.tgnet.ConnectionsManager r4 = r21.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$V8fZup4ioZ_r02qxwmjTn8YZJzw r5 = new org.telegram.messenger.-$$Lambda$MessagesController$V8fZup4ioZ_r02qxwmjTn8YZJzw
            r5.<init>(r2)
            r4.sendRequest(r9, r5)
            org.telegram.messenger.MessagesStorage r2 = r21.getMessagesStorage()
            r3 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputFolderPeer> r4 = r9.folder_peers
            r5 = 0
            r7 = r23
            r2.setDialogsFolderId(r3, r4, r5, r7)
        L_0x0141:
            if (r0 != 0) goto L_0x0145
            r12 = 0
            goto L_0x014b
        L_0x0145:
            boolean r0 = r0[r14]
            if (r0 == 0) goto L_0x014b
            r13 = 2
            r12 = 2
        L_0x014b:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.addDialogToFolder(java.util.ArrayList, int, int, java.util.ArrayList, long):int");
    }

    public /* synthetic */ void lambda$addDialogToFolder$127$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadDialogs(int i, int i2, int i3, boolean z) {
        loadDialogs(i, i2, i3, z, (Runnable) null);
    }

    public void loadDialogs(int i, int i2, int i3, boolean z, Runnable runnable) {
        MessageObject messageObject;
        int i4;
        if (!this.loadingDialogs.get(i) && !this.resetingDialogs) {
            boolean z2 = true;
            this.loadingDialogs.put(i, true);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("folderId = " + i + " load cacheOffset = " + i2 + " count = " + i3 + " cache = " + z);
            }
            if (z) {
                MessagesStorage messagesStorage = getMessagesStorage();
                int i5 = i2 == 0 ? 0 : this.nextDialogsCacheOffset.get(i, 0);
                if (i2 != 0) {
                    z2 = false;
                }
                messagesStorage.getDialogs(i, i5, i3, z2);
                return;
            }
            TLRPC.TL_messages_getDialogs tL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
            tL_messages_getDialogs.limit = i3;
            tL_messages_getDialogs.exclude_pinned = true;
            if (i != 0) {
                tL_messages_getDialogs.flags |= 2;
                tL_messages_getDialogs.folder_id = i;
            }
            int[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(i);
            if (dialogLoadOffsets[0] == -1) {
                ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
                int size = dialogs.size() - 1;
                while (true) {
                    if (size < 0) {
                        z2 = false;
                        break;
                    }
                    TLRPC.Dialog dialog = dialogs.get(size);
                    if (!dialog.pinned) {
                        long j = dialog.id;
                        int i6 = (int) (j >> 32);
                        if (!(((int) j) == 0 || i6 == 1 || dialog.top_message <= 0 || (messageObject = this.dialogMessage.get(j)) == null || messageObject.getId() <= 0)) {
                            TLRPC.Message message = messageObject.messageOwner;
                            tL_messages_getDialogs.offset_date = message.date;
                            tL_messages_getDialogs.offset_id = message.id;
                            TLRPC.Peer peer = message.to_id;
                            int i7 = peer.channel_id;
                            if (i7 == 0 && (i7 = peer.chat_id) == 0) {
                                i4 = peer.user_id;
                            } else {
                                i4 = -i7;
                            }
                            tL_messages_getDialogs.offset_peer = getInputPeer(i4);
                        }
                    }
                    size--;
                }
                if (!z2) {
                    tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
                }
            } else if (dialogLoadOffsets[0] == Integer.MAX_VALUE) {
                this.dialogsEndReached.put(i, true);
                this.serverDialogsEndReached.put(i, true);
                this.loadingDialogs.put(i, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                return;
            } else {
                tL_messages_getDialogs.offset_id = dialogLoadOffsets[0];
                tL_messages_getDialogs.offset_date = dialogLoadOffsets[1];
                if (tL_messages_getDialogs.offset_id == 0) {
                    tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
                } else {
                    if (dialogLoadOffsets[4] != 0) {
                        tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChannel();
                        tL_messages_getDialogs.offset_peer.channel_id = dialogLoadOffsets[4];
                    } else if (dialogLoadOffsets[2] != 0) {
                        tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerUser();
                        tL_messages_getDialogs.offset_peer.user_id = dialogLoadOffsets[2];
                    } else {
                        tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChat();
                        tL_messages_getDialogs.offset_peer.chat_id = dialogLoadOffsets[3];
                    }
                    tL_messages_getDialogs.offset_peer.access_hash = (((long) dialogLoadOffsets[5]) << 32) | ((long) dialogLoadOffsets[5]);
                }
            }
            getConnectionsManager().sendRequest(tL_messages_getDialogs, new RequestDelegate(i, i3, runnable) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadDialogs$128$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadDialogs$128$MessagesController(int i, int i2, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Dialogs messages_dialogs = (TLRPC.messages_Dialogs) tLObject;
            processLoadedDialogs(messages_dialogs, (ArrayList<TLRPC.EncryptedChat>) null, i, 0, i2, 0, false, false, false);
            if (runnable != null && messages_dialogs.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        }
    }

    public void loadGlobalNotificationsSettings() {
        if (this.loadingNotificationSettings == 0 && !getUserConfig().notificationsSettingsLoaded) {
            SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
            SharedPreferences.Editor editor = null;
            if (notificationsSettings.contains("EnableGroup")) {
                boolean z = notificationsSettings.getBoolean("EnableGroup", true);
                SharedPreferences.Editor edit = notificationsSettings.edit();
                if (!z) {
                    edit.putInt("EnableGroup2", Integer.MAX_VALUE);
                    edit.putInt("EnableChannel2", Integer.MAX_VALUE);
                }
                edit.remove("EnableGroup").commit();
                editor = edit;
            }
            if (notificationsSettings.contains("EnableAll")) {
                boolean z2 = notificationsSettings.getBoolean("EnableAll", true);
                if (editor == null) {
                    editor = notificationsSettings.edit();
                }
                if (!z2) {
                    editor.putInt("EnableAll2", Integer.MAX_VALUE);
                }
                editor.remove("EnableAll").commit();
            }
            if (editor != null) {
                editor.commit();
            }
            this.loadingNotificationSettings = 3;
            for (int i = 0; i < 3; i++) {
                TLRPC.TL_account_getNotifySettings tL_account_getNotifySettings = new TLRPC.TL_account_getNotifySettings();
                if (i == 0) {
                    tL_account_getNotifySettings.peer = new TLRPC.TL_inputNotifyChats();
                } else if (i == 1) {
                    tL_account_getNotifySettings.peer = new TLRPC.TL_inputNotifyUsers();
                } else if (i == 2) {
                    tL_account_getNotifySettings.peer = new TLRPC.TL_inputNotifyBroadcasts();
                }
                getConnectionsManager().sendRequest(tL_account_getNotifySettings, new RequestDelegate(i) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadGlobalNotificationsSettings$130$MessagesController(this.f$1, tLObject, tL_error);
                    }
                });
            }
        }
        if (!getUserConfig().notificationsSignUpSettingsLoaded) {
            loadSignUpNotificationsSettings();
        }
    }

    public /* synthetic */ void lambda$loadGlobalNotificationsSettings$130$MessagesController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, i) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$null$129$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$129$MessagesController(TLObject tLObject, int i) {
        if (tLObject != null) {
            this.loadingNotificationSettings--;
            TLRPC.TL_peerNotifySettings tL_peerNotifySettings = (TLRPC.TL_peerNotifySettings) tLObject;
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            if (i == 0) {
                if ((tL_peerNotifySettings.flags & 1) != 0) {
                    edit.putBoolean("EnablePreviewGroup", tL_peerNotifySettings.show_previews);
                }
                if ((tL_peerNotifySettings.flags & 4) != 0) {
                    edit.putInt("EnableGroup2", tL_peerNotifySettings.mute_until);
                }
            } else if (i == 1) {
                if ((tL_peerNotifySettings.flags & 1) != 0) {
                    edit.putBoolean("EnablePreviewAll", tL_peerNotifySettings.show_previews);
                }
                if ((tL_peerNotifySettings.flags & 4) != 0) {
                    edit.putInt("EnableAll2", tL_peerNotifySettings.mute_until);
                }
            } else if (i == 2) {
                if ((tL_peerNotifySettings.flags & 1) != 0) {
                    edit.putBoolean("EnablePreviewChannel", tL_peerNotifySettings.show_previews);
                }
                if ((tL_peerNotifySettings.flags & 4) != 0) {
                    edit.putInt("EnableChannel2", tL_peerNotifySettings.mute_until);
                }
            }
            edit.commit();
            if (this.loadingNotificationSettings == 0) {
                getUserConfig().notificationsSettingsLoaded = true;
                getUserConfig().saveConfig(false);
            }
        }
    }

    public void loadSignUpNotificationsSettings() {
        if (!this.loadingNotificationSignUpSettings) {
            this.loadingNotificationSignUpSettings = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getContactSignUpNotification(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadSignUpNotificationsSettings$132$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadSignUpNotificationsSettings$132$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$131$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$131$MessagesController(TLObject tLObject) {
        this.loadingNotificationSignUpSettings = false;
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        this.enableJoined = tLObject instanceof TLRPC.TL_boolFalse;
        edit.putBoolean("EnableContactJoined", this.enableJoined);
        edit.commit();
        getUserConfig().notificationsSignUpSettingsLoaded = true;
        getUserConfig().saveConfig(false);
    }

    public void forceResetDialogs() {
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        getNotificationsController().deleteAllNotificationChannels();
    }

    /* access modifiers changed from: protected */
    public void loadUnknownDialog(TLRPC.InputPeer inputPeer, long j) {
        NativeByteBuffer nativeByteBuffer;
        if (inputPeer != null) {
            long peerDialogId = DialogObject.getPeerDialogId(inputPeer);
            if (this.gettingUnknownDialogs.indexOfKey(peerDialogId) < 0) {
                this.gettingUnknownDialogs.put(peerDialogId, true);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("load unknown dialog " + peerDialogId);
                }
                TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
                TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                tL_inputDialogPeer.peer = inputPeer;
                tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                if (j == 0) {
                    try {
                        nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 4);
                        try {
                            nativeByteBuffer.writeInt32(15);
                            inputPeer.serializeToStream(nativeByteBuffer);
                        } catch (Exception e) {
                            e = e;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        nativeByteBuffer = null;
                        FileLog.e((Throwable) e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate(j, peerDialogId) {
                            private final /* synthetic */ long f$1;
                            private final /* synthetic */ long f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r4;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$loadUnknownDialog$133$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        });
                    }
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                }
                getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate(j, peerDialogId) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadUnknownDialog$133$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$loadUnknownDialog$133$MessagesController(long j, long j2, TLObject tLObject, TLRPC.TL_error tL_error) {
        long j3 = j;
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty()) {
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                processLoadedDialogs(tL_messages_dialogs, (ArrayList<TLRPC.EncryptedChat>) null, ((TLRPC.TL_dialog) tL_messages_peerDialogs.dialogs.get(0)).folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_UNKNOWN, false, false, false);
            }
        }
        if (j3 != 0) {
            getMessagesStorage().removePendingTask(j3);
        }
        this.gettingUnknownDialogs.delete(j2);
    }

    private void fetchFolderInLoadedPinnedDialogs(TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs) {
        TLRPC.InputPeer inputPeer;
        int size = tL_messages_peerDialogs.dialogs.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(i2);
            if (dialog instanceof TLRPC.TL_dialogFolder) {
                TLRPC.TL_dialogFolder tL_dialogFolder = (TLRPC.TL_dialogFolder) dialog;
                long peerDialogId = DialogObject.getPeerDialogId(dialog.peer);
                if (tL_dialogFolder.top_message == 0 || peerDialogId == 0) {
                    tL_messages_peerDialogs.dialogs.remove(tL_dialogFolder);
                } else {
                    int size2 = tL_messages_peerDialogs.messages.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        TLRPC.Message message = tL_messages_peerDialogs.messages.get(i3);
                        if (peerDialogId == MessageObject.getDialogId(message) && dialog.top_message == message.id) {
                            TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                            tL_dialog.peer = dialog.peer;
                            tL_dialog.top_message = dialog.top_message;
                            tL_dialog.folder_id = tL_dialogFolder.folder.id;
                            tL_dialog.flags |= 16;
                            tL_messages_peerDialogs.dialogs.add(tL_dialog);
                            TLRPC.Peer peer = dialog.peer;
                            if (!(peer instanceof TLRPC.TL_peerChannel)) {
                                if (!(peer instanceof TLRPC.TL_peerChat)) {
                                    inputPeer = new TLRPC.TL_inputPeerUser();
                                    inputPeer.user_id = dialog.peer.user_id;
                                    int size3 = tL_messages_peerDialogs.users.size();
                                    while (true) {
                                        if (i >= size3) {
                                            break;
                                        }
                                        TLRPC.User user = tL_messages_peerDialogs.users.get(i);
                                        if (user.id == inputPeer.user_id) {
                                            inputPeer.access_hash = user.access_hash;
                                            break;
                                        }
                                        i++;
                                    }
                                } else {
                                    inputPeer = new TLRPC.TL_inputPeerChat();
                                    inputPeer.chat_id = dialog.peer.chat_id;
                                }
                            } else {
                                inputPeer = new TLRPC.TL_inputPeerChannel();
                                inputPeer.channel_id = dialog.peer.channel_id;
                                int size4 = tL_messages_peerDialogs.chats.size();
                                while (true) {
                                    if (i >= size4) {
                                        break;
                                    }
                                    TLRPC.Chat chat = tL_messages_peerDialogs.chats.get(i);
                                    if (chat.id == inputPeer.channel_id) {
                                        inputPeer.access_hash = chat.access_hash;
                                        break;
                                    }
                                    i++;
                                }
                            }
                            loadUnknownDialog(inputPeer, 0);
                            return;
                        }
                    }
                    return;
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:68:0x01c4, code lost:
        if (r11.migrated_to != null) goto L_0x017b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void resetDialogs(boolean r26, int r27, int r28, int r29, int r30) {
        /*
            r25 = this;
            r6 = r25
            r7 = 1
            r0 = 0
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            if (r26 == 0) goto L_0x0055
            boolean r1 = r6.resetingDialogs
            if (r1 == 0) goto L_0x000f
            return
        L_0x000f:
            org.telegram.messenger.UserConfig r1 = r25.getUserConfig()
            r1.setPinnedDialogsLoaded(r7, r0)
            r6.resetingDialogs = r7
            org.telegram.tgnet.TLRPC$TL_messages_getPinnedDialogs r8 = new org.telegram.tgnet.TLRPC$TL_messages_getPinnedDialogs
            r8.<init>()
            org.telegram.tgnet.ConnectionsManager r9 = r25.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$oDaJBMfZ5FWgtLiRrHMTpaIL0vQ r10 = new org.telegram.messenger.-$$Lambda$MessagesController$oDaJBMfZ5FWgtLiRrHMTpaIL0vQ
            r0 = r10
            r1 = r25
            r2 = r27
            r3 = r28
            r4 = r29
            r5 = r30
            r0.<init>(r2, r3, r4, r5)
            r9.sendRequest(r8, r10)
            org.telegram.tgnet.TLRPC$TL_messages_getDialogs r8 = new org.telegram.tgnet.TLRPC$TL_messages_getDialogs
            r8.<init>()
            r0 = 100
            r8.limit = r0
            r8.exclude_pinned = r7
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r8.offset_peer = r0
            org.telegram.tgnet.ConnectionsManager r7 = r25.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$21H1u-9Jbe7vowKHvar_GydIFa3A r9 = new org.telegram.messenger.-$$Lambda$MessagesController$21H1u-9Jbe7vowKHvar_GydIFa3A
            r0 = r9
            r0.<init>(r2, r3, r4, r5)
            r7.sendRequest(r8, r9)
            goto L_0x02d2
        L_0x0055:
            org.telegram.tgnet.TLRPC$TL_messages_peerDialogs r2 = r6.resetDialogsPinned
            if (r2 == 0) goto L_0x02d2
            org.telegram.tgnet.TLRPC$messages_Dialogs r2 = r6.resetDialogsAll
            if (r2 == 0) goto L_0x02d2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r2.messages
            int r10 = r2.size()
            org.telegram.tgnet.TLRPC$messages_Dialogs r2 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs
            int r18 = r2.size()
            org.telegram.tgnet.TLRPC$TL_messages_peerDialogs r2 = r6.resetDialogsPinned
            r6.fetchFolderInLoadedPinnedDialogs(r2)
            org.telegram.tgnet.TLRPC$messages_Dialogs r2 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs
            org.telegram.tgnet.TLRPC$TL_messages_peerDialogs r3 = r6.resetDialogsPinned
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r3 = r3.dialogs
            r2.addAll(r3)
            org.telegram.tgnet.TLRPC$messages_Dialogs r2 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r2.messages
            org.telegram.tgnet.TLRPC$TL_messages_peerDialogs r3 = r6.resetDialogsPinned
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r3 = r3.messages
            r2.addAll(r3)
            org.telegram.tgnet.TLRPC$messages_Dialogs r2 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r2.users
            org.telegram.tgnet.TLRPC$TL_messages_peerDialogs r3 = r6.resetDialogsPinned
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r3.users
            r2.addAll(r3)
            org.telegram.tgnet.TLRPC$messages_Dialogs r2 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r2.chats
            org.telegram.tgnet.TLRPC$TL_messages_peerDialogs r3 = r6.resetDialogsPinned
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r3.chats
            r2.addAll(r3)
            android.util.LongSparseArray r15 = new android.util.LongSparseArray
            r15.<init>()
            android.util.LongSparseArray r2 = new android.util.LongSparseArray
            r2.<init>()
            android.util.SparseArray r3 = new android.util.SparseArray
            r3.<init>()
            android.util.SparseArray r4 = new android.util.SparseArray
            r4.<init>()
            r5 = 0
        L_0x00b1:
            org.telegram.tgnet.TLRPC$messages_Dialogs r8 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r8.users
            int r8 = r8.size()
            if (r5 >= r8) goto L_0x00cd
            org.telegram.tgnet.TLRPC$messages_Dialogs r8 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r8.users
            java.lang.Object r8 = r8.get(r5)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC.User) r8
            int r9 = r8.id
            r3.put(r9, r8)
            int r5 = r5 + 1
            goto L_0x00b1
        L_0x00cd:
            r5 = 0
        L_0x00ce:
            org.telegram.tgnet.TLRPC$messages_Dialogs r8 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r8.chats
            int r8 = r8.size()
            if (r5 >= r8) goto L_0x00ea
            org.telegram.tgnet.TLRPC$messages_Dialogs r8 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r8.chats
            java.lang.Object r8 = r8.get(r5)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
            int r9 = r8.id
            r4.put(r9, r8)
            int r5 = r5 + 1
            goto L_0x00ce
        L_0x00ea:
            r5 = 0
            r14 = r5
            r8 = 0
        L_0x00ed:
            org.telegram.tgnet.TLRPC$messages_Dialogs r9 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r9.messages
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x015b
            org.telegram.tgnet.TLRPC$messages_Dialogs r9 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r9.messages
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$Message r9 = (org.telegram.tgnet.TLRPC.Message) r9
            if (r8 >= r10) goto L_0x010c
            if (r14 == 0) goto L_0x010b
            int r11 = r9.date
            int r12 = r14.date
            if (r11 >= r12) goto L_0x010c
        L_0x010b:
            r14 = r9
        L_0x010c:
            org.telegram.tgnet.TLRPC$Peer r11 = r9.to_id
            int r12 = r11.channel_id
            if (r12 == 0) goto L_0x012d
            java.lang.Object r11 = r4.get(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC.Chat) r11
            if (r11 == 0) goto L_0x011f
            boolean r12 = r11.left
            if (r12 == 0) goto L_0x011f
            goto L_0x0158
        L_0x011f:
            if (r11 == 0) goto L_0x013e
            boolean r11 = r11.megagroup
            if (r11 == 0) goto L_0x013e
            int r11 = r9.flags
            r12 = -2147483648(0xfffffffvar_, float:-0.0)
            r11 = r11 | r12
            r9.flags = r11
            goto L_0x013e
        L_0x012d:
            int r11 = r11.chat_id
            if (r11 == 0) goto L_0x013e
            java.lang.Object r11 = r4.get(r11)
            org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC.Chat) r11
            if (r11 == 0) goto L_0x013e
            org.telegram.tgnet.TLRPC$InputChannel r11 = r11.migrated_to
            if (r11 == 0) goto L_0x013e
            goto L_0x0158
        L_0x013e:
            org.telegram.messenger.MessageObject r11 = new org.telegram.messenger.MessageObject
            int r12 = r6.currentAccount
            r24 = 0
            r19 = r11
            r20 = r12
            r21 = r9
            r22 = r3
            r23 = r4
            r19.<init>((int) r20, (org.telegram.tgnet.TLRPC.Message) r21, (android.util.SparseArray<org.telegram.tgnet.TLRPC.User>) r22, (android.util.SparseArray<org.telegram.tgnet.TLRPC.Chat>) r23, (boolean) r24)
            long r12 = r11.getDialogId()
            r2.put(r12, r11)
        L_0x0158:
            int r8 = r8 + 1
            goto L_0x00ed
        L_0x015b:
            r8 = 0
        L_0x015c:
            org.telegram.tgnet.TLRPC$messages_Dialogs r9 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r9 = r9.dialogs
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x0228
            org.telegram.tgnet.TLRPC$messages_Dialogs r9 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r9 = r9.dialogs
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$Dialog r9 = (org.telegram.tgnet.TLRPC.Dialog) r9
            org.telegram.messenger.DialogObject.initDialog(r9)
            long r11 = r9.id
            r16 = 0
            int r13 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
            if (r13 != 0) goto L_0x017e
        L_0x017b:
            r13 = r1
            goto L_0x0222
        L_0x017e:
            int r13 = r9.last_message_date
            if (r13 != 0) goto L_0x0190
            java.lang.Object r11 = r2.get(r11)
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            if (r11 == 0) goto L_0x0190
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            int r11 = r11.date
            r9.last_message_date = r11
        L_0x0190:
            boolean r11 = org.telegram.messenger.DialogObject.isChannel(r9)
            if (r11 == 0) goto L_0x01b3
            long r11 = r9.id
            int r12 = (int) r11
            int r11 = -r12
            java.lang.Object r11 = r4.get(r11)
            org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC.Chat) r11
            if (r11 == 0) goto L_0x01a7
            boolean r11 = r11.left
            if (r11 == 0) goto L_0x01a7
            goto L_0x017b
        L_0x01a7:
            android.util.SparseIntArray r11 = r6.channelsPts
            long r12 = r9.id
            int r13 = (int) r12
            int r12 = -r13
            int r13 = r9.pts
            r11.put(r12, r13)
            goto L_0x01c7
        L_0x01b3:
            long r11 = r9.id
            int r13 = (int) r11
            if (r13 >= 0) goto L_0x01c7
            int r12 = (int) r11
            int r11 = -r12
            java.lang.Object r11 = r4.get(r11)
            org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC.Chat) r11
            if (r11 == 0) goto L_0x01c7
            org.telegram.tgnet.TLRPC$InputChannel r11 = r11.migrated_to
            if (r11 == 0) goto L_0x01c7
            goto L_0x017b
        L_0x01c7:
            long r11 = r9.id
            r15.put(r11, r9)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r11 = r6.dialogs_read_inbox_max
            long r12 = r9.id
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            java.lang.Object r11 = r11.get(r12)
            java.lang.Integer r11 = (java.lang.Integer) r11
            if (r11 != 0) goto L_0x01dd
            r11 = r1
        L_0x01dd:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r12 = r6.dialogs_read_inbox_max
            r13 = r1
            long r0 = r9.id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            int r1 = r11.intValue()
            int r11 = r9.read_inbox_max_id
            int r1 = java.lang.Math.max(r1, r11)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r12.put(r0, r1)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r6.dialogs_read_outbox_max
            long r11 = r9.id
            java.lang.Long r1 = java.lang.Long.valueOf(r11)
            java.lang.Object r0 = r0.get(r1)
            r1 = r0
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x0209
            r1 = r13
        L_0x0209:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r6.dialogs_read_outbox_max
            long r11 = r9.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            int r1 = r1.intValue()
            int r9 = r9.read_outbox_max_id
            int r1 = java.lang.Math.max(r1, r9)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r0.put(r11, r1)
        L_0x0222:
            int r8 = r8 + 1
            r1 = r13
            r0 = 0
            goto L_0x015c
        L_0x0228:
            org.telegram.tgnet.TLRPC$messages_Dialogs r0 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r0 = r0.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r0)
            r0 = 0
        L_0x0230:
            org.telegram.tgnet.TLRPC$messages_Dialogs r1 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r1.messages
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x02b8
            org.telegram.tgnet.TLRPC$messages_Dialogs r1 = r6.resetDialogsAll
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r1.messages
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Message r1 = (org.telegram.tgnet.TLRPC.Message) r1
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r8 == 0) goto L_0x0265
            int r4 = r4.user_id
            java.lang.Object r4 = r3.get(r4)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC.User) r4
            if (r4 == 0) goto L_0x0265
            boolean r4 = r4.bot
            if (r4 == 0) goto L_0x0265
            org.telegram.tgnet.TLRPC$TL_replyKeyboardHide r4 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide
            r4.<init>()
            r1.reply_markup = r4
            int r4 = r1.flags
            r4 = r4 | 64
            r1.flags = r4
        L_0x0265:
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r8 != 0) goto L_0x02af
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0270
            goto L_0x02af
        L_0x0270:
            boolean r4 = r1.out
            if (r4 == 0) goto L_0x0277
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r4 = r6.dialogs_read_outbox_max
            goto L_0x0279
        L_0x0277:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r4 = r6.dialogs_read_inbox_max
        L_0x0279:
            long r8 = r1.dialog_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            java.lang.Object r8 = r4.get(r8)
            java.lang.Integer r8 = (java.lang.Integer) r8
            if (r8 != 0) goto L_0x02a0
            org.telegram.messenger.MessagesStorage r8 = r25.getMessagesStorage()
            boolean r9 = r1.out
            long r11 = r1.dialog_id
            int r8 = r8.getDialogReadMax(r9, r11)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            long r11 = r1.dialog_id
            java.lang.Long r9 = java.lang.Long.valueOf(r11)
            r4.put(r9, r8)
        L_0x02a0:
            int r4 = r8.intValue()
            int r8 = r1.id
            if (r4 >= r8) goto L_0x02aa
            r4 = 1
            goto L_0x02ab
        L_0x02aa:
            r4 = 0
        L_0x02ab:
            r1.unread = r4
            r4 = 0
            goto L_0x02b4
        L_0x02af:
            r4 = 0
            r1.unread = r4
            r1.media_unread = r4
        L_0x02b4:
            int r0 = r0 + 1
            goto L_0x0230
        L_0x02b8:
            org.telegram.messenger.MessagesStorage r8 = r25.getMessagesStorage()
            org.telegram.tgnet.TLRPC$messages_Dialogs r9 = r6.resetDialogsAll
            r11 = r27
            r12 = r28
            r13 = r29
            r0 = r14
            r14 = r30
            r16 = r2
            r17 = r0
            r8.resetDialogs(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r6.resetDialogsPinned = r5
            r6.resetDialogsAll = r5
        L_0x02d2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.resetDialogs(boolean, int, int, int, int):void");
    }

    public /* synthetic */ void lambda$resetDialogs$134$MessagesController(int i, int i2, int i3, int i4, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            this.resetDialogsPinned = (TLRPC.TL_messages_peerDialogs) tLObject;
            for (int i5 = 0; i5 < this.resetDialogsPinned.dialogs.size(); i5++) {
                this.resetDialogsPinned.dialogs.get(i5).pinned = true;
            }
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    public /* synthetic */ void lambda$resetDialogs$135$MessagesController(int i, int i2, int i3, int i4, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.resetDialogsAll = (TLRPC.messages_Dialogs) tLObject;
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: protected */
    public void completeDialogsReset(TLRPC.messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, LongSparseArray<TLRPC.Dialog> longSparseArray, LongSparseArray<MessageObject> longSparseArray2, TLRPC.Message message) {
        Utilities.stageQueue.postRunnable(new Runnable(i3, i4, i5, messages_dialogs, longSparseArray, longSparseArray2) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ TLRPC.messages_Dialogs f$4;
            private final /* synthetic */ LongSparseArray f$5;
            private final /* synthetic */ LongSparseArray f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MessagesController.this.lambda$completeDialogsReset$137$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$completeDialogsReset$137$MessagesController(int i, int i2, int i3, TLRPC.messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.gettingDifference = false;
        getMessagesStorage().setLastPtsValue(i);
        getMessagesStorage().setLastDateValue(i2);
        getMessagesStorage().setLastQtsValue(i3);
        getDifference();
        AndroidUtilities.runOnUIThread(new Runnable(messages_dialogs, longSparseArray, longSparseArray2) {
            private final /* synthetic */ TLRPC.messages_Dialogs f$1;
            private final /* synthetic */ LongSparseArray f$2;
            private final /* synthetic */ LongSparseArray f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$null$136$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$136$MessagesController(TLRPC.messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        TLRPC.messages_Dialogs messages_dialogs2 = messages_dialogs;
        LongSparseArray longSparseArray3 = longSparseArray;
        this.resetingDialogs = false;
        applyDialogsNotificationsSettings(messages_dialogs2.dialogs);
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearAllDrafts(false);
        mediaDataController.loadDraftsIfNeed();
        putUsers(messages_dialogs2.users, false);
        putChats(messages_dialogs2.chats, false);
        for (int i = 0; i < this.allDialogs.size(); i++) {
            TLRPC.Dialog dialog = this.allDialogs.get(i);
            if (!DialogObject.isSecretDialogId(dialog.id)) {
                this.dialogs_dict.remove(dialog.id);
                MessageObject messageObject = this.dialogMessage.get(dialog.id);
                this.dialogMessage.remove(dialog.id);
                if (messageObject != null) {
                    this.dialogMessagesByIds.remove(messageObject.getId());
                    long j = messageObject.messageOwner.random_id;
                    if (j != 0) {
                        this.dialogMessagesByRandomIds.remove(j);
                    }
                }
            }
        }
        for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
            long keyAt = longSparseArray3.keyAt(i2);
            TLRPC.Dialog dialog2 = (TLRPC.Dialog) longSparseArray3.valueAt(i2);
            TLRPC.DraftMessage draftMessage = dialog2.draft;
            if (draftMessage instanceof TLRPC.TL_draftMessage) {
                mediaDataController.saveDraft(dialog2.id, draftMessage, (TLRPC.Message) null, false);
            }
            this.dialogs_dict.put(keyAt, dialog2);
            MessageObject messageObject2 = (MessageObject) longSparseArray2.get(dialog2.id);
            this.dialogMessage.put(keyAt, messageObject2);
            if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                long j2 = messageObject2.messageOwner.random_id;
                if (j2 != 0) {
                    this.dialogMessagesByRandomIds.put(j2, messageObject2);
                }
            }
        }
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (int i3 = 0; i3 < size; i3++) {
            this.allDialogs.add(this.dialogs_dict.valueAt(i3));
        }
        sortDialogs((SparseArray<TLRPC.Chat>) null);
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, false);
        this.dialogsEndReached.put(1, true);
        this.serverDialogsEndReached.put(1, false);
        int totalDialogsCount = getUserConfig().getTotalDialogsCount(0);
        int[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(0);
        if (!(totalDialogsCount >= 400 || dialogLoadOffsets[0] == -1 || dialogLoadOffsets[0] == Integer.MAX_VALUE)) {
            loadDialogs(0, 0, 100, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private void migrateDialogs(int i, int i2, int i3, int i4, int i5, long j) {
        if (!this.migratingDialogs && i != -1) {
            this.migratingDialogs = true;
            TLRPC.TL_messages_getDialogs tL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
            tL_messages_getDialogs.exclude_pinned = true;
            tL_messages_getDialogs.limit = 100;
            tL_messages_getDialogs.offset_id = i;
            tL_messages_getDialogs.offset_date = i2;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start migrate with id " + i + " date " + LocaleController.getInstance().formatterStats.format(((long) i2) * 1000));
            }
            if (i == 0) {
                tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
            } else {
                if (i5 != 0) {
                    tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChannel();
                    tL_messages_getDialogs.offset_peer.channel_id = i5;
                } else if (i3 != 0) {
                    tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerUser();
                    tL_messages_getDialogs.offset_peer.user_id = i3;
                } else {
                    tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChat();
                    tL_messages_getDialogs.offset_peer.chat_id = i4;
                }
                tL_messages_getDialogs.offset_peer.access_hash = j;
            }
            getConnectionsManager().sendRequest(tL_messages_getDialogs, new RequestDelegate(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$migrateDialogs$141$MessagesController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$migrateDialogs$141$MessagesController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable((TLRPC.messages_Dialogs) tLObject, i) {
                private final /* synthetic */ TLRPC.messages_Dialogs f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MessagesController.this.lambda$null$139$MessagesController(this.f$1, this.f$2);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MessagesController.this.lambda$null$140$MessagesController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$139$MessagesController(TLRPC.messages_Dialogs messages_dialogs, int i) {
        int i2;
        SQLiteCursor sQLiteCursor;
        int i3;
        SQLiteCursor sQLiteCursor2;
        TLRPC.Message message;
        TLRPC.messages_Dialogs messages_dialogs2 = messages_dialogs;
        int i4 = i;
        try {
            getUserConfig().setTotalDialogsCount(0, getUserConfig().getTotalDialogsCount(0) + messages_dialogs2.dialogs.size());
            TLRPC.Message message2 = null;
            for (int i5 = 0; i5 < messages_dialogs2.messages.size(); i5++) {
                TLRPC.Message message3 = messages_dialogs2.messages.get(i5);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("search migrate id " + message3.id + " date " + LocaleController.getInstance().formatterStats.format(((long) message3.date) * 1000));
                }
                if (message2 == null || message3.date < message2.date) {
                    message2 = message3;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrate step with id " + message2.id + " date " + LocaleController.getInstance().formatterStats.format(((long) message2.date) * 1000));
            }
            int i6 = 2;
            int i7 = -1;
            if (messages_dialogs2.dialogs.size() >= 100) {
                i2 = message2.id;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("migrate stop due to not 100 dialogs");
                }
                for (int i8 = 0; i8 < 2; i8++) {
                    getUserConfig().setDialogsLoadOffset(i8, Integer.MAX_VALUE, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
                }
                i2 = -1;
            }
            StringBuilder sb = new StringBuilder(messages_dialogs2.dialogs.size() * 12);
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i9 = 0; i9 < messages_dialogs2.dialogs.size(); i9++) {
                TLRPC.Dialog dialog = messages_dialogs2.dialogs.get(i9);
                DialogObject.initDialog(dialog);
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(dialog.id);
                longSparseArray.put(dialog.id, dialog);
            }
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE did IN (%s)", new Object[]{sb.toString()}), new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                int intValue = queryFinalized.intValue(1);
                TLRPC.Dialog dialog2 = (TLRPC.Dialog) longSparseArray.get(longValue);
                if (dialog2.folder_id == intValue) {
                    longSparseArray.remove(longValue);
                    if (dialog2 != null) {
                        messages_dialogs2.dialogs.remove(dialog2);
                        int i10 = 0;
                        while (true) {
                            if (i10 >= messages_dialogs2.messages.size()) {
                                break;
                            }
                            TLRPC.Message message4 = messages_dialogs2.messages.get(i10);
                            if (MessageObject.getDialogId(message4) == longValue) {
                                messages_dialogs2.messages.remove(i10);
                                i10--;
                                if (message4.id == dialog2.top_message) {
                                    dialog2.top_message = 0;
                                    break;
                                }
                            }
                            i10++;
                        }
                    }
                }
            }
            queryFinalized.dispose();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrate found missing dialogs " + messages_dialogs2.dialogs.size());
            }
            SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
            if (queryFinalized2.next()) {
                int max = Math.max(NUM, queryFinalized2.intValue(0));
                int i11 = i2;
                int i12 = 0;
                while (i12 < messages_dialogs2.messages.size()) {
                    TLRPC.Message message5 = messages_dialogs2.messages.get(i12);
                    if (message5.date < max) {
                        if (i4 != i7) {
                            int i13 = 0;
                            while (i13 < i6) {
                                getUserConfig().setDialogsLoadOffset(i13, getUserConfig().migrateOffsetId, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
                                i13++;
                                message2 = message2;
                                queryFinalized2 = queryFinalized2;
                                i6 = 2;
                            }
                            message = message2;
                            sQLiteCursor2 = queryFinalized2;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(((long) max) * 1000));
                            }
                            i11 = -1;
                        } else {
                            message = message2;
                            sQLiteCursor2 = queryFinalized2;
                        }
                        messages_dialogs2.messages.remove(i12);
                        i12--;
                        long dialogId = MessageObject.getDialogId(message5);
                        TLRPC.Dialog dialog3 = (TLRPC.Dialog) longSparseArray.get(dialogId);
                        longSparseArray.remove(dialogId);
                        if (dialog3 != null) {
                            messages_dialogs2.dialogs.remove(dialog3);
                        }
                    } else {
                        message = message2;
                        sQLiteCursor2 = queryFinalized2;
                    }
                    i12++;
                    message2 = message;
                    queryFinalized2 = sQLiteCursor2;
                    i6 = 2;
                    i7 = -1;
                }
                TLRPC.Message message6 = message2;
                sQLiteCursor = queryFinalized2;
                if (message6 != null) {
                    message2 = message6;
                    if (message2.date < max && i4 != -1) {
                        for (int i14 = 0; i14 < 2; i14++) {
                            getUserConfig().setDialogsLoadOffset(i14, getUserConfig().migrateOffsetId, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(((long) max) * 1000));
                        }
                        i3 = -1;
                    }
                } else {
                    message2 = message6;
                }
                i3 = i11;
            } else {
                sQLiteCursor = queryFinalized2;
                i3 = i2;
            }
            sQLiteCursor.dispose();
            getUserConfig().migrateOffsetDate = message2.date;
            if (message2.to_id.channel_id != 0) {
                getUserConfig().migrateOffsetChannelId = message2.to_id.channel_id;
                getUserConfig().migrateOffsetChatId = 0;
                getUserConfig().migrateOffsetUserId = 0;
                int i15 = 0;
                while (true) {
                    if (i15 >= messages_dialogs2.chats.size()) {
                        break;
                    }
                    TLRPC.Chat chat = messages_dialogs2.chats.get(i15);
                    if (chat.id == getUserConfig().migrateOffsetChannelId) {
                        getUserConfig().migrateOffsetAccess = chat.access_hash;
                        break;
                    }
                    i15++;
                }
            } else if (message2.to_id.chat_id != 0) {
                getUserConfig().migrateOffsetChatId = message2.to_id.chat_id;
                getUserConfig().migrateOffsetChannelId = 0;
                getUserConfig().migrateOffsetUserId = 0;
                int i16 = 0;
                while (true) {
                    if (i16 >= messages_dialogs2.chats.size()) {
                        break;
                    }
                    TLRPC.Chat chat2 = messages_dialogs2.chats.get(i16);
                    if (chat2.id == getUserConfig().migrateOffsetChatId) {
                        getUserConfig().migrateOffsetAccess = chat2.access_hash;
                        break;
                    }
                    i16++;
                }
            } else if (message2.to_id.user_id != 0) {
                getUserConfig().migrateOffsetUserId = message2.to_id.user_id;
                int i17 = 0;
                getUserConfig().migrateOffsetChatId = 0;
                getUserConfig().migrateOffsetChannelId = 0;
                while (true) {
                    if (i17 >= messages_dialogs2.users.size()) {
                        break;
                    }
                    TLRPC.User user = messages_dialogs2.users.get(i17);
                    if (user.id == getUserConfig().migrateOffsetUserId) {
                        getUserConfig().migrateOffsetAccess = user.access_hash;
                        break;
                    }
                    i17++;
                }
            }
            processLoadedDialogs(messages_dialogs, (ArrayList<TLRPC.EncryptedChat>) null, 0, i3, 0, 0, false, true, false);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MessagesController.this.lambda$null$138$MessagesController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$138$MessagesController() {
        this.migratingDialogs = false;
    }

    public /* synthetic */ void lambda$null$140$MessagesController() {
        this.migratingDialogs = false;
    }

    public void processLoadedDialogs(TLRPC.messages_Dialogs messages_dialogs, ArrayList<TLRPC.EncryptedChat> arrayList, int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3) {
        Utilities.stageQueue.postRunnable(new Runnable(i, i4, messages_dialogs, z, i3, arrayList, i2, z3, z2) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ TLRPC.messages_Dialogs f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ ArrayList f$6;
            private final /* synthetic */ int f$7;
            private final /* synthetic */ boolean f$8;
            private final /* synthetic */ boolean f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
            }

            public final void run() {
                MessagesController.this.lambda$processLoadedDialogs$144$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0289, code lost:
        if (r2.get(r3) == null) goto L_0x028b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x02d5, code lost:
        if (r5 == r1.id) goto L_0x02d9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedDialogs$144$MessagesController(int r24, int r25, org.telegram.tgnet.TLRPC.messages_Dialogs r26, boolean r27, int r28, java.util.ArrayList r29, int r30, boolean r31, boolean r32) {
        /*
            r23 = this;
            r14 = r23
            r9 = r24
            r10 = r25
            r11 = r26
            r12 = r29
            boolean r0 = r14.firstGettingTask
            r1 = 0
            r13 = 1
            r15 = 0
            java.lang.Integer r16 = java.lang.Integer.valueOf(r15)
            if (r0 != 0) goto L_0x001a
            r14.getNewDeleteTask(r1, r15)
            r14.firstGettingTask = r13
        L_0x001a:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0048
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "loaded folderId "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = " loadType "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r2 = " count "
            r0.append(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r11.dialogs
            int r2 = r2.size()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0048:
            org.telegram.messenger.UserConfig r0 = r23.getUserConfig()
            int[] r5 = r0.getDialogLoadOffsets(r9)
            int r0 = r14.DIALOGS_LOAD_TYPE_CACHE
            if (r10 != r0) goto L_0x0070
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r11.dialogs
            int r0 = r0.size()
            if (r0 != 0) goto L_0x0070
            org.telegram.messenger.-$$Lambda$MessagesController$8ByUO_dN8BrXHUo6Z9CyTSqM3zk r7 = new org.telegram.messenger.-$$Lambda$MessagesController$8ByUO_dN8BrXHUo6Z9CyTSqM3zk
            r0 = r7
            r1 = r23
            r2 = r26
            r3 = r24
            r4 = r27
            r6 = r28
            r0.<init>(r2, r3, r4, r5, r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)
            return
        L_0x0070:
            android.util.LongSparseArray r7 = new android.util.LongSparseArray
            r7.<init>()
            android.util.LongSparseArray r8 = new android.util.LongSparseArray
            r8.<init>()
            android.util.SparseArray r6 = new android.util.SparseArray
            r6.<init>()
            android.util.SparseArray r4 = new android.util.SparseArray
            r4.<init>()
            r0 = 0
        L_0x0085:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r11.users
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x009d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r11.users
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            int r3 = r2.id
            r6.put(r3, r2)
            int r0 = r0 + 1
            goto L_0x0085
        L_0x009d:
            r0 = 0
        L_0x009e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r11.chats
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x00b6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r11.chats
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            int r3 = r2.id
            r4.put(r3, r2)
            int r0 = r0 + 1
            goto L_0x009e
        L_0x00b6:
            if (r12 == 0) goto L_0x00d8
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            int r2 = r29.size()
            r3 = 0
        L_0x00c2:
            if (r3 >= r2) goto L_0x00d6
            java.lang.Object r17 = r12.get(r3)
            r1 = r17
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = (org.telegram.tgnet.TLRPC.EncryptedChat) r1
            int r13 = r1.id
            r0.put(r13, r1)
            int r3 = r3 + 1
            r1 = 0
            r13 = 1
            goto L_0x00c2
        L_0x00d6:
            r13 = r0
            goto L_0x00d9
        L_0x00d8:
            r13 = 0
        L_0x00d9:
            int r0 = r14.DIALOGS_LOAD_TYPE_CACHE
            if (r10 != r0) goto L_0x00e4
            android.util.SparseIntArray r0 = r14.nextDialogsCacheOffset
            int r1 = r30 + r28
            r0.put(r9, r1)
        L_0x00e4:
            r0 = 0
            r1 = 0
        L_0x00e6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r11.messages
            int r2 = r2.size()
            r17 = 0
            if (r0 >= r2) goto L_0x016e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r11.messages
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC.Message) r2
            if (r1 == 0) goto L_0x0100
            int r3 = r2.date
            int r15 = r1.date
            if (r3 >= r15) goto L_0x0101
        L_0x0100:
            r1 = r2
        L_0x0101:
            org.telegram.tgnet.TLRPC$Peer r3 = r2.to_id
            int r15 = r3.channel_id
            if (r15 == 0) goto L_0x0134
            java.lang.Object r3 = r4.get(r15)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            if (r3 == 0) goto L_0x0124
            boolean r15 = r3.left
            if (r15 == 0) goto L_0x0124
            r27 = r13
            long r12 = r14.proxyDialogId
            int r15 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1))
            if (r15 == 0) goto L_0x0161
            int r15 = r3.id
            int r15 = -r15
            long r9 = (long) r15
            int r15 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r15 == 0) goto L_0x0126
            goto L_0x0161
        L_0x0124:
            r27 = r13
        L_0x0126:
            if (r3 == 0) goto L_0x0147
            boolean r3 = r3.megagroup
            if (r3 == 0) goto L_0x0147
            int r3 = r2.flags
            r9 = -2147483648(0xfffffffvar_, float:-0.0)
            r3 = r3 | r9
            r2.flags = r3
            goto L_0x0147
        L_0x0134:
            r27 = r13
            int r3 = r3.chat_id
            if (r3 == 0) goto L_0x0147
            java.lang.Object r3 = r4.get(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            if (r3 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$InputChannel r3 = r3.migrated_to
            if (r3 == 0) goto L_0x0147
            goto L_0x0161
        L_0x0147:
            org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject
            int r9 = r14.currentAccount
            r22 = 0
            r17 = r3
            r18 = r9
            r19 = r2
            r20 = r6
            r21 = r4
            r17.<init>((int) r18, (org.telegram.tgnet.TLRPC.Message) r19, (android.util.SparseArray<org.telegram.tgnet.TLRPC.User>) r20, (android.util.SparseArray<org.telegram.tgnet.TLRPC.Chat>) r21, (boolean) r22)
            long r9 = r3.getDialogId()
            r8.put(r9, r3)
        L_0x0161:
            int r0 = r0 + 1
            r9 = r24
            r10 = r25
            r13 = r27
            r12 = r29
            r15 = 0
            goto L_0x00e6
        L_0x016e:
            r27 = r13
            if (r31 != 0) goto L_0x024e
            if (r32 != 0) goto L_0x024e
            r0 = 0
            r2 = r5[r0]
            r3 = -1
            if (r2 == r3) goto L_0x024e
            r9 = r25
            if (r9 != 0) goto L_0x024e
            org.telegram.messenger.UserConfig r2 = r23.getUserConfig()
            r10 = r24
            int r2 = r2.getTotalDialogsCount(r10)
            if (r1 == 0) goto L_0x021d
            int r3 = r1.id
            r5 = r5[r0]
            if (r3 == r5) goto L_0x021d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r11.dialogs
            int r0 = r0.size()
            int r2 = r2 + r0
            int r0 = r1.id
            int r3 = r1.date
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r5 = r1.channel_id
            if (r5 == 0) goto L_0x01c7
            r1 = 0
        L_0x01a2:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r11.chats
            int r12 = r12.size()
            if (r1 >= r12) goto L_0x01bc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r11.chats
            java.lang.Object r12 = r12.get(r1)
            org.telegram.tgnet.TLRPC$Chat r12 = (org.telegram.tgnet.TLRPC.Chat) r12
            int r13 = r12.id
            if (r13 != r5) goto L_0x01b9
            long r12 = r12.access_hash
            goto L_0x01be
        L_0x01b9:
            int r1 = r1 + 1
            goto L_0x01a2
        L_0x01bc:
            r12 = r17
        L_0x01be:
            r15 = r2
            r19 = r12
            r12 = 0
            r2 = r0
            r13 = r5
        L_0x01c4:
            r5 = 0
            goto L_0x022a
        L_0x01c7:
            int r5 = r1.chat_id
            if (r5 == 0) goto L_0x01ef
            r1 = 0
        L_0x01cc:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r11.chats
            int r12 = r12.size()
            if (r1 >= r12) goto L_0x01e6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r11.chats
            java.lang.Object r12 = r12.get(r1)
            org.telegram.tgnet.TLRPC$Chat r12 = (org.telegram.tgnet.TLRPC.Chat) r12
            int r13 = r12.id
            if (r13 != r5) goto L_0x01e3
            long r12 = r12.access_hash
            goto L_0x01e8
        L_0x01e3:
            int r1 = r1 + 1
            goto L_0x01cc
        L_0x01e6:
            r12 = r17
        L_0x01e8:
            r15 = r2
            r19 = r12
            r13 = 0
            r2 = r0
            r12 = r5
            goto L_0x01c4
        L_0x01ef:
            int r1 = r1.user_id
            if (r1 == 0) goto L_0x0215
            r5 = 0
        L_0x01f4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r11.users
            int r12 = r12.size()
            if (r5 >= r12) goto L_0x020e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r11.users
            java.lang.Object r12 = r12.get(r5)
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC.User) r12
            int r13 = r12.id
            if (r13 != r1) goto L_0x020b
            long r12 = r12.access_hash
            goto L_0x0210
        L_0x020b:
            int r5 = r5 + 1
            goto L_0x01f4
        L_0x020e:
            r12 = r17
        L_0x0210:
            r5 = r1
            r15 = r2
            r19 = r12
            goto L_0x0219
        L_0x0215:
            r15 = r2
            r19 = r17
            r5 = 0
        L_0x0219:
            r12 = 0
            r13 = 0
            r2 = r0
            goto L_0x022a
        L_0x021d:
            r0 = 2147483647(0x7fffffff, float:NaN)
            r15 = r2
            r19 = r17
            r2 = 2147483647(0x7fffffff, float:NaN)
            r3 = 0
            r5 = 0
            r12 = 0
            r13 = 0
        L_0x022a:
            org.telegram.messenger.UserConfig r0 = r23.getUserConfig()
            r1 = r24
            r9 = r4
            r4 = r5
            r5 = r12
            r12 = r6
            r6 = r13
            r13 = r7
            r21 = r12
            r12 = r8
            r7 = r19
            r0.setDialogsLoadOffset(r1, r2, r3, r4, r5, r6, r7)
            org.telegram.messenger.UserConfig r0 = r23.getUserConfig()
            r0.setTotalDialogsCount(r10, r15)
            org.telegram.messenger.UserConfig r0 = r23.getUserConfig()
            r1 = 0
            r0.saveConfig(r1)
            goto L_0x0255
        L_0x024e:
            r10 = r24
            r9 = r4
            r21 = r6
            r13 = r7
            r12 = r8
        L_0x0255:
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            r0 = 0
        L_0x025b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r11.dialogs
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x0375
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r11.dialogs
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC.Dialog) r1
            org.telegram.messenger.DialogObject.initDialog(r1)
            long r2 = r1.id
            int r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r4 != 0) goto L_0x027a
            r4 = r25
            r2 = r27
            goto L_0x036f
        L_0x027a:
            int r4 = (int) r2
            r5 = 32
            long r2 = r2 >> r5
            int r3 = (int) r2
            if (r4 != 0) goto L_0x028f
            if (r27 == 0) goto L_0x028f
            r2 = r27
            java.lang.Object r3 = r2.get(r3)
            if (r3 != 0) goto L_0x0291
        L_0x028b:
            r4 = r25
            goto L_0x036f
        L_0x028f:
            r2 = r27
        L_0x0291:
            long r3 = r14.proxyDialogId
            int r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r5 == 0) goto L_0x029f
            long r5 = r1.id
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x029f
            r14.proxyDialog = r1
        L_0x029f:
            int r3 = r1.last_message_date
            if (r3 != 0) goto L_0x02b3
            long r3 = r1.id
            java.lang.Object r3 = r12.get(r3)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            if (r3 == 0) goto L_0x02b3
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.date
            r1.last_message_date = r3
        L_0x02b3:
            boolean r3 = org.telegram.messenger.DialogObject.isChannel(r1)
            if (r3 == 0) goto L_0x02e5
            long r3 = r1.id
            int r4 = (int) r3
            int r3 = -r4
            java.lang.Object r3 = r9.get(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            if (r3 == 0) goto L_0x02d8
            boolean r4 = r3.megagroup
            boolean r3 = r3.left
            if (r3 == 0) goto L_0x02d9
            long r5 = r14.proxyDialogId
            int r3 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r3 == 0) goto L_0x028b
            long r7 = r1.id
            int r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r3 == 0) goto L_0x02d9
            goto L_0x028b
        L_0x02d8:
            r4 = 1
        L_0x02d9:
            android.util.SparseIntArray r3 = r14.channelsPts
            long r5 = r1.id
            int r6 = (int) r5
            int r5 = -r6
            int r6 = r1.pts
            r3.put(r5, r6)
            goto L_0x02fa
        L_0x02e5:
            long r3 = r1.id
            int r5 = (int) r3
            if (r5 >= 0) goto L_0x02f9
            int r4 = (int) r3
            int r3 = -r4
            java.lang.Object r3 = r9.get(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            if (r3 == 0) goto L_0x02f9
            org.telegram.tgnet.TLRPC$InputChannel r3 = r3.migrated_to
            if (r3 == 0) goto L_0x02f9
            goto L_0x028b
        L_0x02f9:
            r4 = 1
        L_0x02fa:
            long r5 = r1.id
            r13.put(r5, r1)
            if (r4 == 0) goto L_0x0317
            int r3 = r14.DIALOGS_LOAD_TYPE_CACHE
            r4 = r25
            if (r4 != r3) goto L_0x0319
            int r3 = r1.read_outbox_max_id
            if (r3 == 0) goto L_0x030f
            int r3 = r1.read_inbox_max_id
            if (r3 != 0) goto L_0x0319
        L_0x030f:
            int r3 = r1.top_message
            if (r3 == 0) goto L_0x0319
            r15.add(r1)
            goto L_0x0319
        L_0x0317:
            r4 = r25
        L_0x0319:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r14.dialogs_read_inbox_max
            long r5 = r1.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            java.lang.Object r3 = r3.get(r5)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r3 != 0) goto L_0x032b
            r3 = r16
        L_0x032b:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r5 = r14.dialogs_read_inbox_max
            long r6 = r1.id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            int r3 = r3.intValue()
            int r7 = r1.read_inbox_max_id
            int r3 = java.lang.Math.max(r3, r7)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r5.put(r6, r3)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r14.dialogs_read_outbox_max
            long r5 = r1.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            java.lang.Object r3 = r3.get(r5)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r3 != 0) goto L_0x0356
            r3 = r16
        L_0x0356:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r5 = r14.dialogs_read_outbox_max
            long r6 = r1.id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            int r3 = r3.intValue()
            int r1 = r1.read_outbox_max_id
            int r1 = java.lang.Math.max(r3, r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r5.put(r6, r1)
        L_0x036f:
            int r0 = r0 + 1
            r27 = r2
            goto L_0x025b
        L_0x0375:
            r4 = r25
            int r0 = r14.DIALOGS_LOAD_TYPE_CACHE
            if (r4 == r0) goto L_0x0415
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r0 = r11.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r0)
            r0 = 0
        L_0x0381:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r11.messages
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x040c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r11.messages
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Message r1 = (org.telegram.tgnet.TLRPC.Message) r1
            org.telegram.tgnet.TLRPC$MessageAction r2 = r1.action
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r3 == 0) goto L_0x03b5
            int r2 = r2.user_id
            r3 = r21
            java.lang.Object r2 = r3.get(r2)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            if (r2 == 0) goto L_0x03b7
            boolean r2 = r2.bot
            if (r2 == 0) goto L_0x03b7
            org.telegram.tgnet.TLRPC$TL_replyKeyboardHide r2 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide
            r2.<init>()
            r1.reply_markup = r2
            int r2 = r1.flags
            r2 = r2 | 64
            r1.flags = r2
            goto L_0x03b7
        L_0x03b5:
            r3 = r21
        L_0x03b7:
            org.telegram.tgnet.TLRPC$MessageAction r2 = r1.action
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r5 != 0) goto L_0x0401
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r2 == 0) goto L_0x03c2
            goto L_0x0401
        L_0x03c2:
            boolean r2 = r1.out
            if (r2 == 0) goto L_0x03c9
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r14.dialogs_read_outbox_max
            goto L_0x03cb
        L_0x03c9:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r14.dialogs_read_inbox_max
        L_0x03cb:
            long r5 = r1.dialog_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            java.lang.Object r5 = r2.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 != 0) goto L_0x03f2
            org.telegram.messenger.MessagesStorage r5 = r23.getMessagesStorage()
            boolean r6 = r1.out
            long r7 = r1.dialog_id
            int r5 = r5.getDialogReadMax(r6, r7)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            long r6 = r1.dialog_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            r2.put(r6, r5)
        L_0x03f2:
            int r2 = r5.intValue()
            int r5 = r1.id
            if (r2 >= r5) goto L_0x03fc
            r2 = 1
            goto L_0x03fd
        L_0x03fc:
            r2 = 0
        L_0x03fd:
            r1.unread = r2
            r2 = 0
            goto L_0x0406
        L_0x0401:
            r2 = 0
            r1.unread = r2
            r1.media_unread = r2
        L_0x0406:
            int r0 = r0 + 1
            r21 = r3
            goto L_0x0381
        L_0x040c:
            r2 = 0
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            r0.putDialogs(r11, r2)
            goto L_0x0416
        L_0x0415:
            r2 = 0
        L_0x0416:
            int r0 = r14.DIALOGS_LOAD_TYPE_CHANNEL
            if (r4 != r0) goto L_0x042c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r0 = r11.chats
            java.lang.Object r0 = r0.get(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            int r1 = r0.id
            r14.getChannelDifference(r1)
            int r0 = r0.id
            r14.checkChannelInviter(r0)
        L_0x042c:
            org.telegram.messenger.-$$Lambda$MessagesController$vVkJdb5avF7UAQQt5Tq-ADdhTlw r16 = new org.telegram.messenger.-$$Lambda$MessagesController$vVkJdb5avF7UAQQt5Tq-ADdhTlw
            r0 = r16
            r1 = r23
            r2 = r25
            r3 = r26
            r4 = r29
            r5 = r32
            r6 = r24
            r7 = r13
            r8 = r12
            r10 = r28
            r11 = r31
            r12 = r30
            r13 = r15
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogs$144$MessagesController(int, int, org.telegram.tgnet.TLRPC$messages_Dialogs, boolean, int, java.util.ArrayList, int, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$null$142$MessagesController(TLRPC.messages_Dialogs messages_dialogs, int i, boolean z, int[] iArr, int i2) {
        putUsers(messages_dialogs.users, true);
        this.loadingDialogs.put(i, false);
        if (z) {
            this.dialogsEndReached.put(i, false);
            this.serverDialogsEndReached.put(i, false);
        } else if (iArr[0] == Integer.MAX_VALUE) {
            this.dialogsEndReached.put(i, true);
            this.serverDialogsEndReached.put(i, true);
        } else {
            loadDialogs(i, 0, i2, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* JADX WARNING: type inference failed for: r2v13 */
    /* JADX WARNING: type inference failed for: r2v14 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r2v8, types: [boolean, int] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$143$MessagesController(int r23, org.telegram.tgnet.TLRPC.messages_Dialogs r24, java.util.ArrayList r25, boolean r26, int r27, android.util.LongSparseArray r28, android.util.LongSparseArray r29, android.util.SparseArray r30, int r31, boolean r32, int r33, java.util.ArrayList r34) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = r25
            r4 = r27
            r5 = r28
            r6 = r29
            r7 = r31
            int r8 = r0.DIALOGS_LOAD_TYPE_CACHE
            if (r1 == r8) goto L_0x0020
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r2.dialogs
            r0.applyDialogsNotificationsSettings(r8)
            org.telegram.messenger.MediaDataController r8 = r22.getMediaDataController()
            r8.loadDraftsIfNeed()
        L_0x0020:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r2.users
            int r9 = r0.DIALOGS_LOAD_TYPE_CACHE
            r10 = 1
            r11 = 0
            if (r1 != r9) goto L_0x002a
            r9 = 1
            goto L_0x002b
        L_0x002a:
            r9 = 0
        L_0x002b:
            r0.putUsers(r8, r9)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r2.chats
            int r9 = r0.DIALOGS_LOAD_TYPE_CACHE
            if (r1 != r9) goto L_0x0036
            r9 = 1
            goto L_0x0037
        L_0x0036:
            r9 = 0
        L_0x0037:
            r0.putChats(r8, r9)
            r8 = 0
            if (r3 == 0) goto L_0x0065
            r9 = 0
        L_0x003e:
            int r12 = r25.size()
            if (r9 >= r12) goto L_0x0065
            java.lang.Object r12 = r3.get(r9)
            org.telegram.tgnet.TLRPC$EncryptedChat r12 = (org.telegram.tgnet.TLRPC.EncryptedChat) r12
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
            if (r13 == 0) goto L_0x005f
            int r13 = r12.layer
            int r13 = org.telegram.messenger.AndroidUtilities.getMyLayerVersion(r13)
            r14 = 101(0x65, float:1.42E-43)
            if (r13 >= r14) goto L_0x005f
            org.telegram.messenger.SecretChatHelper r13 = r22.getSecretChatHelper()
            r13.sendNotifyLayerMessage(r12, r8)
        L_0x005f:
            r0.putEncryptedChat(r12, r10)
            int r9 = r9 + 1
            goto L_0x003e
        L_0x0065:
            if (r26 != 0) goto L_0x0074
            int r3 = r0.DIALOGS_LOAD_TYPE_UNKNOWN
            if (r1 == r3) goto L_0x0074
            int r3 = r0.DIALOGS_LOAD_TYPE_CHANNEL
            if (r1 == r3) goto L_0x0074
            android.util.SparseBooleanArray r3 = r0.loadingDialogs
            r3.put(r4, r11)
        L_0x0074:
            r0.dialogsLoaded = r10
            if (r26 == 0) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r3 = r0.allDialogs
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r3 = r0.allDialogs
            int r9 = r3.size()
            int r9 = r9 - r10
            java.lang.Object r3 = r3.get(r9)
            org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC.Dialog) r3
            int r3 = r3.last_message_date
            goto L_0x0091
        L_0x0090:
            r3 = 0
        L_0x0091:
            r9 = 0
            r12 = 0
            r13 = 0
        L_0x0094:
            int r14 = r28.size()
            if (r9 >= r14) goto L_0x0210
            long r10 = r5.keyAt(r9)
            java.lang.Object r14 = r5.valueAt(r9)
            org.telegram.tgnet.TLRPC$Dialog r14 = (org.telegram.tgnet.TLRPC.Dialog) r14
            int r8 = r0.DIALOGS_LOAD_TYPE_UNKNOWN
            if (r1 == r8) goto L_0x00b1
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r0.dialogs_dict
            java.lang.Object r8 = r8.get(r10)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC.Dialog) r8
            goto L_0x00b2
        L_0x00b1:
            r8 = 0
        L_0x00b2:
            if (r26 == 0) goto L_0x00ba
            if (r8 == 0) goto L_0x00ba
            int r15 = r14.folder_id
            r8.folder_id = r15
        L_0x00ba:
            if (r26 == 0) goto L_0x00c5
            if (r3 == 0) goto L_0x00c5
            int r15 = r14.last_message_date
            if (r15 >= r3) goto L_0x00c5
            r15 = r3
            goto L_0x0204
        L_0x00c5:
            int r15 = r0.DIALOGS_LOAD_TYPE_CACHE
            if (r1 == r15) goto L_0x00e4
            org.telegram.tgnet.TLRPC$DraftMessage r15 = r14.draft
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_draftMessage
            if (r15 == 0) goto L_0x00e4
            org.telegram.messenger.MediaDataController r16 = r22.getMediaDataController()
            r15 = r3
            long r2 = r14.id
            org.telegram.tgnet.TLRPC$DraftMessage r5 = r14.draft
            r20 = 0
            r21 = 0
            r17 = r2
            r19 = r5
            r16.saveDraft(r17, r19, r20, r21)
            goto L_0x00e5
        L_0x00e4:
            r15 = r3
        L_0x00e5:
            int r2 = r14.folder_id
            if (r2 == r4) goto L_0x00eb
            int r12 = r12 + 1
        L_0x00eb:
            if (r8 != 0) goto L_0x0124
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogs_dict
            r2.put(r10, r14)
            long r2 = r14.id
            java.lang.Object r2 = r6.get(r2)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.dialogMessage
            r3.put(r10, r2)
            if (r2 == 0) goto L_0x0121
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.to_id
            int r3 = r3.channel_id
            if (r3 != 0) goto L_0x0121
            android.util.SparseArray<org.telegram.messenger.MessageObject> r3 = r0.dialogMessagesByIds
            int r5 = r2.getId()
            r3.put(r5, r2)
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            long r10 = r3.random_id
            r13 = 0
            int r3 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0121
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.dialogMessagesByRandomIds
            r3.put(r10, r2)
        L_0x0121:
            r13 = 1
            goto L_0x0204
        L_0x0124:
            int r2 = r0.DIALOGS_LOAD_TYPE_CACHE
            if (r1 == r2) goto L_0x012c
            org.telegram.tgnet.TLRPC$PeerNotifySettings r2 = r14.notify_settings
            r8.notify_settings = r2
        L_0x012c:
            boolean r2 = r14.pinned
            r8.pinned = r2
            int r2 = r14.pinnedNum
            r8.pinnedNum = r2
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessage
            java.lang.Object r2 = r2.get(r10)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            if (r2 == 0) goto L_0x0142
            boolean r3 = r2.deleted
            if (r3 != 0) goto L_0x01aa
        L_0x0142:
            if (r2 == 0) goto L_0x01aa
            int r3 = r8.top_message
            if (r3 <= 0) goto L_0x0149
            goto L_0x01aa
        L_0x0149:
            r3 = r12
            r5 = r13
            long r12 = r14.id
            java.lang.Object r8 = r6.get(r12)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            boolean r12 = r2.deleted
            if (r12 != 0) goto L_0x0163
            if (r8 == 0) goto L_0x0163
            org.telegram.tgnet.TLRPC$Message r12 = r8.messageOwner
            int r12 = r12.date
            org.telegram.tgnet.TLRPC$Message r13 = r2.messageOwner
            int r13 = r13.date
            if (r12 <= r13) goto L_0x0202
        L_0x0163:
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r12 = r0.dialogs_dict
            r12.put(r10, r14)
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r12 = r0.dialogMessage
            r12.put(r10, r8)
            if (r8 == 0) goto L_0x0191
            org.telegram.tgnet.TLRPC$Message r10 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r10 = r10.to_id
            int r10 = r10.channel_id
            if (r10 != 0) goto L_0x0191
            android.util.SparseArray<org.telegram.messenger.MessageObject> r10 = r0.dialogMessagesByIds
            int r11 = r8.getId()
            r10.put(r11, r8)
            if (r8 == 0) goto L_0x0191
            org.telegram.tgnet.TLRPC$Message r10 = r8.messageOwner
            long r10 = r10.random_id
            r12 = 0
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x0191
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r12 = r0.dialogMessagesByRandomIds
            r12.put(r10, r8)
        L_0x0191:
            android.util.SparseArray<org.telegram.messenger.MessageObject> r8 = r0.dialogMessagesByIds
            int r10 = r2.getId()
            r8.remove(r10)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            long r10 = r2.random_id
            r12 = 0
            int r2 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x0202
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessagesByRandomIds
            r2.remove(r10)
            goto L_0x0202
        L_0x01aa:
            r3 = r12
            r5 = r13
            int r12 = r14.top_message
            int r8 = r8.top_message
            if (r12 < r8) goto L_0x0202
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r0.dialogs_dict
            r8.put(r10, r14)
            long r12 = r14.id
            java.lang.Object r8 = r6.get(r12)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r12 = r0.dialogMessage
            r12.put(r10, r8)
            if (r8 == 0) goto L_0x01e8
            org.telegram.tgnet.TLRPC$Message r10 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r10 = r10.to_id
            int r10 = r10.channel_id
            if (r10 != 0) goto L_0x01e8
            android.util.SparseArray<org.telegram.messenger.MessageObject> r10 = r0.dialogMessagesByIds
            int r11 = r8.getId()
            r10.put(r11, r8)
            if (r8 == 0) goto L_0x01e8
            org.telegram.tgnet.TLRPC$Message r10 = r8.messageOwner
            long r10 = r10.random_id
            r12 = 0
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x01e8
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r12 = r0.dialogMessagesByRandomIds
            r12.put(r10, r8)
        L_0x01e8:
            if (r2 == 0) goto L_0x0202
            android.util.SparseArray<org.telegram.messenger.MessageObject> r8 = r0.dialogMessagesByIds
            int r10 = r2.getId()
            r8.remove(r10)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            long r10 = r2.random_id
            r12 = 0
            int r2 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x0202
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessagesByRandomIds
            r2.remove(r10)
        L_0x0202:
            r12 = r3
            r13 = r5
        L_0x0204:
            int r9 = r9 + 1
            r2 = r24
            r5 = r28
            r3 = r15
            r8 = 0
            r10 = 1
            r11 = 0
            goto L_0x0094
        L_0x0210:
            r5 = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.allDialogs
            r2.clear()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogs_dict
            int r2 = r2.size()
            r3 = 0
        L_0x021d:
            if (r3 >= r2) goto L_0x022d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r6 = r0.allDialogs
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r0.dialogs_dict
            java.lang.Object r8 = r8.valueAt(r3)
            r6.add(r8)
            int r3 = r3 + 1
            goto L_0x021d
        L_0x022d:
            if (r26 == 0) goto L_0x0232
            r2 = r30
            goto L_0x0233
        L_0x0232:
            r2 = 0
        L_0x0233:
            r0.sortDialogs(r2)
            r22.putAllNeededDraftDialogs()
            int r2 = r0.DIALOGS_LOAD_TYPE_CHANNEL
            r3 = 2147483647(0x7fffffff, float:NaN)
            if (r1 == r2) goto L_0x029f
            int r2 = r0.DIALOGS_LOAD_TYPE_UNKNOWN
            if (r1 == r2) goto L_0x029f
            if (r26 != 0) goto L_0x029f
            android.util.SparseBooleanArray r2 = r0.dialogsEndReached
            r6 = r24
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r6.dialogs
            int r8 = r8.size()
            if (r8 == 0) goto L_0x025a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r6.dialogs
            int r8 = r8.size()
            if (r8 == r7) goto L_0x025e
        L_0x025a:
            if (r1 != 0) goto L_0x025e
            r8 = 1
            goto L_0x025f
        L_0x025e:
            r8 = 0
        L_0x025f:
            r2.put(r4, r8)
            if (r12 <= 0) goto L_0x0283
            r2 = 20
            if (r12 >= r2) goto L_0x0283
            if (r4 != 0) goto L_0x0283
            android.util.SparseBooleanArray r2 = r0.dialogsEndReached
            r8 = 1
            r2.put(r8, r8)
            org.telegram.messenger.UserConfig r2 = r22.getUserConfig()
            int[] r2 = r2.getDialogLoadOffsets(r4)
            r9 = 0
            r2 = r2[r9]
            if (r2 != r3) goto L_0x0284
            android.util.SparseBooleanArray r2 = r0.serverDialogsEndReached
            r2.put(r8, r8)
            goto L_0x0284
        L_0x0283:
            r8 = 1
        L_0x0284:
            if (r32 != 0) goto L_0x029f
            android.util.SparseBooleanArray r2 = r0.serverDialogsEndReached
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r9 = r6.dialogs
            int r9 = r9.size()
            if (r9 == 0) goto L_0x0298
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r6 = r6.dialogs
            int r6 = r6.size()
            if (r6 == r7) goto L_0x029b
        L_0x0298:
            if (r1 != 0) goto L_0x029b
            goto L_0x029c
        L_0x029b:
            r8 = 0
        L_0x029c:
            r2.put(r4, r8)
        L_0x029f:
            org.telegram.messenger.UserConfig r2 = r22.getUserConfig()
            int r2 = r2.getTotalDialogsCount(r4)
            org.telegram.messenger.UserConfig r6 = r22.getUserConfig()
            int[] r6 = r6.getDialogLoadOffsets(r4)
            if (r32 != 0) goto L_0x02c7
            if (r26 != 0) goto L_0x02c7
            r8 = 400(0x190, float:5.6E-43)
            if (r2 >= r8) goto L_0x02c7
            r2 = 0
            r8 = r6[r2]
            r9 = -1
            if (r8 == r9) goto L_0x02c8
            r6 = r6[r2]
            if (r6 == r3) goto L_0x02c8
            r3 = 100
            r0.loadDialogs(r4, r2, r3, r2)
            goto L_0x02c8
        L_0x02c7:
            r2 = 0
        L_0x02c8:
            org.telegram.messenger.NotificationCenter r3 = r22.getNotificationCenter()
            int r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            java.lang.Object[] r8 = new java.lang.Object[r2]
            r3.postNotificationName(r6, r8)
            if (r26 == 0) goto L_0x02f2
            org.telegram.messenger.UserConfig r1 = r22.getUserConfig()
            r3 = r33
            r1.migrateOffsetId = r3
            org.telegram.messenger.UserConfig r1 = r22.getUserConfig()
            r1.saveConfig(r2)
            r0.migratingDialogs = r2
            org.telegram.messenger.NotificationCenter r1 = r22.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.needReloadRecentDialogsSearch
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r1.postNotificationName(r3, r2)
            goto L_0x02fe
        L_0x02f2:
            r22.generateUpdateMessage()
            if (r5 != 0) goto L_0x02fe
            int r3 = r0.DIALOGS_LOAD_TYPE_CACHE
            if (r1 != r3) goto L_0x02fe
            r0.loadDialogs(r4, r2, r7, r2)
        L_0x02fe:
            org.telegram.messenger.UserConfig r1 = r22.getUserConfig()
            int r1 = r1.migrateOffsetId
            org.telegram.messenger.UserConfig r2 = r22.getUserConfig()
            int r2 = r2.migrateOffsetDate
            org.telegram.messenger.UserConfig r3 = r22.getUserConfig()
            int r3 = r3.migrateOffsetUserId
            org.telegram.messenger.UserConfig r4 = r22.getUserConfig()
            int r4 = r4.migrateOffsetChatId
            org.telegram.messenger.UserConfig r5 = r22.getUserConfig()
            int r5 = r5.migrateOffsetChannelId
            org.telegram.messenger.UserConfig r6 = r22.getUserConfig()
            long r6 = r6.migrateOffsetAccess
            r23 = r22
            r24 = r1
            r25 = r2
            r26 = r3
            r27 = r4
            r28 = r5
            r29 = r6
            r23.migrateDialogs(r24, r25, r26, r27, r28, r29)
            boolean r1 = r34.isEmpty()
            if (r1 != 0) goto L_0x0340
            r1 = r34
            r2 = 0
            r0.reloadDialogsReadValue(r1, r2)
        L_0x0340:
            r22.loadUnreadDialogs()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$null$143$MessagesController(int, org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, boolean, int, android.util.LongSparseArray, android.util.LongSparseArray, android.util.SparseArray, int, boolean, int, java.util.ArrayList):void");
    }

    private void applyDialogNotificationsSettings(long j, TLRPC.PeerNotifySettings peerNotifySettings) {
        int i;
        long j2 = j;
        TLRPC.PeerNotifySettings peerNotifySettings2 = peerNotifySettings;
        if (peerNotifySettings2 != null) {
            SharedPreferences sharedPreferences = this.notificationsPreferences;
            int i2 = sharedPreferences.getInt("notify2_" + j2, -1);
            SharedPreferences sharedPreferences2 = this.notificationsPreferences;
            int i3 = sharedPreferences2.getInt("notifyuntil_" + j2, 0);
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            TLRPC.Dialog dialog = this.dialogs_dict.get(j2);
            if (dialog != null) {
                dialog.notify_settings = peerNotifySettings2;
            }
            if ((peerNotifySettings2.flags & 2) != 0) {
                edit.putBoolean("silent_" + j2, peerNotifySettings2.silent);
            } else {
                edit.remove("silent_" + j2);
            }
            boolean z = true;
            if ((peerNotifySettings2.flags & 4) == 0) {
                if (i2 != -1) {
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                    edit.remove("notify2_" + j2);
                } else {
                    z = false;
                }
                getMessagesStorage().setDialogFlags(j2, 0);
            } else if (peerNotifySettings2.mute_until > getConnectionsManager().getCurrentTime()) {
                if (peerNotifySettings2.mute_until <= getConnectionsManager().getCurrentTime() + 31536000) {
                    if (i2 == 3 && i3 == peerNotifySettings2.mute_until) {
                        z = false;
                    } else {
                        edit.putInt("notify2_" + j2, 3);
                        edit.putInt("notifyuntil_" + j2, peerNotifySettings2.mute_until);
                        if (dialog != null) {
                            dialog.notify_settings.mute_until = 0;
                        }
                    }
                    i = peerNotifySettings2.mute_until;
                } else if (i2 != 2) {
                    edit.putInt("notify2_" + j2, 2);
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                    }
                    i = 0;
                } else {
                    i = 0;
                    z = false;
                }
                getMessagesStorage().setDialogFlags(j2, (((long) i) << 32) | 1);
                getNotificationsController().removeNotificationsForDialog(j2);
            } else {
                if (i2 == 0 || i2 == 1) {
                    z = false;
                } else {
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                    edit.putInt("notify2_" + j2, 0);
                }
                getMessagesStorage().setDialogFlags(j2, 0);
            }
            edit.commit();
            if (z) {
                getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
            }
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<TLRPC.Dialog> arrayList) {
        SharedPreferences.Editor editor = null;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.Dialog dialog = arrayList.get(i);
            if (dialog.peer != null && (dialog.notify_settings instanceof TLRPC.TL_peerNotifySettings)) {
                if (editor == null) {
                    editor = this.notificationsPreferences.edit();
                }
                TLRPC.Peer peer = dialog.peer;
                int i2 = peer.user_id;
                if (i2 == 0) {
                    int i3 = peer.chat_id;
                    if (i3 != 0) {
                        i2 = -i3;
                    } else {
                        i2 = -peer.channel_id;
                    }
                }
                if ((dialog.notify_settings.flags & 2) != 0) {
                    editor.putBoolean("silent_" + i2, dialog.notify_settings.silent);
                } else {
                    editor.remove("silent_" + i2);
                }
                TLRPC.PeerNotifySettings peerNotifySettings = dialog.notify_settings;
                if ((peerNotifySettings.flags & 4) == 0) {
                    editor.remove("notify2_" + i2);
                } else if (peerNotifySettings.mute_until <= getConnectionsManager().getCurrentTime()) {
                    editor.putInt("notify2_" + i2, 0);
                } else if (dialog.notify_settings.mute_until > getConnectionsManager().getCurrentTime() + 31536000) {
                    editor.putInt("notify2_" + i2, 2);
                    dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                } else {
                    editor.putInt("notify2_" + i2, 3);
                    editor.putInt("notifyuntil_" + i2, dialog.notify_settings.mute_until);
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
    }

    public void reloadMentionsCountForChannels(ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$reloadMentionsCountForChannels$147$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$reloadMentionsCountForChannels$147$MessagesController(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            long j = (long) (-((Integer) arrayList.get(i)).intValue());
            TLRPC.TL_messages_getUnreadMentions tL_messages_getUnreadMentions = new TLRPC.TL_messages_getUnreadMentions();
            tL_messages_getUnreadMentions.peer = getInputPeer((int) j);
            tL_messages_getUnreadMentions.limit = 1;
            getConnectionsManager().sendRequest(tL_messages_getUnreadMentions, new RequestDelegate(j) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$null$146$MessagesController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$146$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, j) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$null$145$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$145$MessagesController(TLObject tLObject, long j) {
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        if (messages_messages != null) {
            int i = messages_messages.count;
            if (i == 0) {
                i = messages_messages.messages.size();
            }
            getMessagesStorage().resetMentionsCount(j, i);
        }
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> longSparseArray, LongSparseArray<Integer> longSparseArray2) {
        AndroidUtilities.runOnUIThread(new Runnable(longSparseArray, longSparseArray2) {
            private final /* synthetic */ LongSparseArray f$1;
            private final /* synthetic */ LongSparseArray f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$processDialogsUpdateRead$148$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$148$MessagesController(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        boolean z;
        if (longSparseArray != null) {
            z = false;
            for (int i = 0; i < longSparseArray.size(); i++) {
                long keyAt = longSparseArray.keyAt(i);
                TLRPC.Dialog dialog = this.dialogs_dict.get(keyAt);
                if (dialog != null) {
                    int i2 = dialog.unread_count;
                    dialog.unread_count = ((Integer) longSparseArray.valueAt(i)).intValue();
                    if (i2 != 0 && dialog.unread_count == 0 && !isDialogMuted(keyAt)) {
                        this.unreadUnmutedDialogs--;
                    } else if (i2 == 0 && !dialog.unread_mark && dialog.unread_count != 0 && !isDialogMuted(keyAt)) {
                        this.unreadUnmutedDialogs++;
                    }
                    if (dialog.folder_id == 1) {
                        z = true;
                    }
                }
            }
        } else {
            z = false;
        }
        if (longSparseArray2 != null) {
            for (int i3 = 0; i3 < longSparseArray2.size(); i3++) {
                TLRPC.Dialog dialog2 = this.dialogs_dict.get(longSparseArray2.keyAt(i3));
                if (dialog2 != null) {
                    dialog2.unread_mentions_count = ((Integer) longSparseArray2.valueAt(i3)).intValue();
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(dialog2.id))) {
                        getNotificationCenter().postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(dialog2.id), Integer.valueOf(dialog2.unread_mentions_count));
                    }
                }
            }
        }
        if (z) {
            updateFolderUnreadCounter();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 256);
        if (longSparseArray != null) {
            getNotificationsController().processDialogsUpdateRead(longSparseArray);
        }
    }

    private void updateFolderUnreadCounter() {
        TLRPC.Dialog dialog = this.dialogs_dict.get(DialogObject.makeFolderDialogId(1));
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            dialog.unread_count = 0;
            dialog.unread_mentions_count = 0;
            ArrayList arrayList = this.dialogsByFolder.get(1);
            if (arrayList != null) {
                boolean z = getNotificationsController().showBadgeMessages;
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    TLRPC.Dialog dialog2 = (TLRPC.Dialog) arrayList.get(i);
                    if (z) {
                        if (isDialogMuted(dialog2.id)) {
                            dialog.unread_count += dialog2.unread_count;
                        } else {
                            dialog.unread_mentions_count += dialog2.unread_count;
                        }
                    } else if (isDialogMuted(dialog2.id)) {
                        dialog.unread_count++;
                    } else {
                        dialog.unread_mentions_count++;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkLastDialogMessage(TLRPC.Dialog dialog, TLRPC.InputPeer inputPeer, long j) {
        NativeByteBuffer nativeByteBuffer;
        Exception e;
        int i = (int) dialog.id;
        if (i != 0 && this.checkingLastMessagesDialogs.indexOfKey(i) < 0) {
            TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
            if (inputPeer == null) {
                inputPeer = getInputPeer(i);
            }
            tL_messages_getHistory.peer = inputPeer;
            if (tL_messages_getHistory.peer != null) {
                tL_messages_getHistory.limit = 1;
                this.checkingLastMessagesDialogs.put(i, true);
                if (j == 0) {
                    try {
                        nativeByteBuffer = new NativeByteBuffer(tL_messages_getHistory.peer.getObjectSize() + 60);
                        try {
                            nativeByteBuffer.writeInt32(14);
                            nativeByteBuffer.writeInt64(dialog.id);
                            nativeByteBuffer.writeInt32(dialog.top_message);
                            nativeByteBuffer.writeInt32(dialog.read_inbox_max_id);
                            nativeByteBuffer.writeInt32(dialog.read_outbox_max_id);
                            nativeByteBuffer.writeInt32(dialog.unread_count);
                            nativeByteBuffer.writeInt32(dialog.last_message_date);
                            nativeByteBuffer.writeInt32(dialog.pts);
                            nativeByteBuffer.writeInt32(dialog.flags);
                            nativeByteBuffer.writeBool(dialog.pinned);
                            nativeByteBuffer.writeInt32(dialog.pinnedNum);
                            nativeByteBuffer.writeInt32(dialog.unread_mentions_count);
                            nativeByteBuffer.writeBool(dialog.unread_mark);
                            nativeByteBuffer.writeInt32(dialog.folder_id);
                            tL_messages_getHistory.peer.serializeToStream(nativeByteBuffer);
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        Exception exc = e3;
                        nativeByteBuffer = null;
                        e = exc;
                        FileLog.e((Throwable) e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate(i, dialog, j) {
                            private final /* synthetic */ int f$1;
                            private final /* synthetic */ TLRPC.Dialog f$2;
                            private final /* synthetic */ long f$3;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$checkLastDialogMessage$152$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                            }
                        });
                    }
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                }
                getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate(i, dialog, j) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ TLRPC.Dialog f$2;
                    private final /* synthetic */ long f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$checkLastDialogMessage$152$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$checkLastDialogMessage$152$MessagesController(int i, TLRPC.Dialog dialog, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            removeDeletedMessagesFromArray((long) i, messages_messages.messages);
            if (!messages_messages.messages.isEmpty()) {
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                TLRPC.Message message = messages_messages.messages.get(0);
                TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                tL_dialog.flags = dialog.flags;
                tL_dialog.top_message = message.id;
                tL_dialog.last_message_date = message.date;
                tL_dialog.notify_settings = dialog.notify_settings;
                tL_dialog.pts = dialog.pts;
                tL_dialog.unread_count = dialog.unread_count;
                tL_dialog.unread_mark = dialog.unread_mark;
                tL_dialog.unread_mentions_count = dialog.unread_mentions_count;
                tL_dialog.read_inbox_max_id = dialog.read_inbox_max_id;
                tL_dialog.read_outbox_max_id = dialog.read_outbox_max_id;
                tL_dialog.pinned = dialog.pinned;
                tL_dialog.pinnedNum = dialog.pinnedNum;
                tL_dialog.folder_id = dialog.folder_id;
                long j2 = dialog.id;
                tL_dialog.id = j2;
                message.dialog_id = j2;
                tL_messages_dialogs.users.addAll(messages_messages.users);
                tL_messages_dialogs.chats.addAll(messages_messages.chats);
                tL_messages_dialogs.dialogs.add(tL_dialog);
                tL_messages_dialogs.messages.addAll(messages_messages.messages);
                tL_messages_dialogs.count = 1;
                processDialogsUpdate(tL_messages_dialogs, (ArrayList<TLRPC.EncryptedChat>) null);
                getMessagesStorage().putMessages(messages_messages.messages, true, true, false, getDownloadController().getAutodownloadMask(), true, false);
            } else {
                AndroidUtilities.runOnUIThread(new Runnable(dialog) {
                    private final /* synthetic */ TLRPC.Dialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$150$MessagesController(this.f$1);
                    }
                });
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$151$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$150$MessagesController(TLRPC.Dialog dialog) {
        if (getMediaDataController().getDraft(dialog.id) == null) {
            TLRPC.Dialog dialog2 = this.dialogs_dict.get(dialog.id);
            if (dialog2 == null) {
                getMessagesStorage().isDialogHasTopMessage(dialog.id, new Runnable(dialog) {
                    private final /* synthetic */ TLRPC.Dialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$149$MessagesController(this.f$1);
                    }
                });
            } else if (dialog2.top_message == 0) {
                deleteDialog(dialog.id, 3);
            }
        }
    }

    public /* synthetic */ void lambda$null$149$MessagesController(TLRPC.Dialog dialog) {
        deleteDialog(dialog.id, 3);
    }

    public /* synthetic */ void lambda$null$151$MessagesController(int i) {
        this.checkingLastMessagesDialogs.delete(i);
    }

    public void processDialogsUpdate(TLRPC.messages_Dialogs messages_dialogs, ArrayList<TLRPC.EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new Runnable(messages_dialogs) {
            private final /* synthetic */ TLRPC.messages_Dialogs f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$processDialogsUpdate$154$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processDialogsUpdate$154$MessagesController(TLRPC.messages_Dialogs messages_dialogs) {
        long j;
        MessageObject messageObject;
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        TLRPC.messages_Dialogs messages_dialogs2 = messages_dialogs;
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        SparseArray sparseArray = new SparseArray(messages_dialogs2.users.size());
        SparseArray sparseArray2 = new SparseArray(messages_dialogs2.chats.size());
        LongSparseArray longSparseArray3 = new LongSparseArray();
        int i = 0;
        for (int i2 = 0; i2 < messages_dialogs2.users.size(); i2++) {
            TLRPC.User user = messages_dialogs2.users.get(i2);
            sparseArray.put(user.id, user);
        }
        for (int i3 = 0; i3 < messages_dialogs2.chats.size(); i3++) {
            TLRPC.Chat chat3 = messages_dialogs2.chats.get(i3);
            sparseArray2.put(chat3.id, chat3);
        }
        int i4 = 0;
        while (true) {
            j = 0;
            if (i4 >= messages_dialogs2.messages.size()) {
                break;
            }
            TLRPC.Message message = messages_dialogs2.messages.get(i4);
            long j2 = this.proxyDialogId;
            if (j2 == 0 || j2 != message.dialog_id) {
                TLRPC.Peer peer = message.to_id;
                int i5 = peer.channel_id;
                if (i5 != 0) {
                    TLRPC.Chat chat4 = (TLRPC.Chat) sparseArray2.get(i5);
                    if (chat4 != null && chat4.left) {
                        i4++;
                    }
                } else {
                    int i6 = peer.chat_id;
                    if (!(i6 == 0 || (chat2 = (TLRPC.Chat) sparseArray2.get(i6)) == null || chat2.migrated_to == null)) {
                        i4++;
                    }
                }
            }
            MessageObject messageObject2 = r7;
            MessageObject messageObject3 = new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) sparseArray, (SparseArray<TLRPC.Chat>) sparseArray2, false);
            longSparseArray2.put(messageObject2.getDialogId(), messageObject2);
            i4++;
        }
        while (i < messages_dialogs2.dialogs.size()) {
            TLRPC.Dialog dialog = messages_dialogs2.dialogs.get(i);
            DialogObject.initDialog(dialog);
            long j3 = this.proxyDialogId;
            if (j3 == j || j3 != dialog.id) {
                if (DialogObject.isChannel(dialog)) {
                    TLRPC.Chat chat5 = (TLRPC.Chat) sparseArray2.get(-((int) dialog.id));
                    if (chat5 != null && chat5.left) {
                        i++;
                        j = 0;
                    }
                } else {
                    long j4 = dialog.id;
                    if (!(((int) j4) >= 0 || (chat = (TLRPC.Chat) sparseArray2.get(-((int) j4))) == null || chat.migrated_to == null)) {
                        i++;
                        j = 0;
                    }
                }
            }
            if (dialog.last_message_date == 0 && (messageObject = (MessageObject) longSparseArray2.get(dialog.id)) != null) {
                dialog.last_message_date = messageObject.messageOwner.date;
            }
            longSparseArray.put(dialog.id, dialog);
            longSparseArray3.put(dialog.id, Integer.valueOf(dialog.unread_count));
            Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
            if (num == null) {
                num = 0;
            }
            this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num.intValue(), dialog.read_inbox_max_id)));
            Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
            if (num2 == null) {
                num2 = 0;
            }
            this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num2.intValue(), dialog.read_outbox_max_id)));
            i++;
            j = 0;
        }
        AndroidUtilities.runOnUIThread(new Runnable(messages_dialogs, longSparseArray, longSparseArray2, longSparseArray3) {
            private final /* synthetic */ TLRPC.messages_Dialogs f$1;
            private final /* synthetic */ LongSparseArray f$2;
            private final /* synthetic */ LongSparseArray f$3;
            private final /* synthetic */ LongSparseArray f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MessagesController.this.lambda$null$153$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$153$MessagesController(TLRPC.messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        long j;
        TLRPC.messages_Dialogs messages_dialogs2 = messages_dialogs;
        LongSparseArray longSparseArray4 = longSparseArray;
        LongSparseArray longSparseArray5 = longSparseArray2;
        putUsers(messages_dialogs2.users, true);
        putChats(messages_dialogs2.chats, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray4.keyAt(i);
            TLRPC.Dialog dialog = (TLRPC.Dialog) longSparseArray4.valueAt(i);
            TLRPC.Dialog dialog2 = this.dialogs_dict.get(keyAt);
            if (dialog2 == null) {
                this.nextDialogsCacheOffset.put(dialog.folder_id, this.nextDialogsCacheOffset.get(dialog.folder_id, 0) + 1);
                this.dialogs_dict.put(keyAt, dialog);
                MessageObject messageObject = (MessageObject) longSparseArray5.get(dialog.id);
                this.dialogMessage.put(keyAt, messageObject);
                if (messageObject == null) {
                    checkLastDialogMessage(dialog, (TLRPC.InputPeer) null, 0);
                } else if (messageObject.messageOwner.to_id.channel_id == 0) {
                    this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                    long j2 = messageObject.messageOwner.random_id;
                    if (j2 != 0) {
                        this.dialogMessagesByRandomIds.put(j2, messageObject);
                    }
                }
            } else {
                dialog2.unread_count = dialog.unread_count;
                int i2 = dialog2.unread_mentions_count;
                int i3 = dialog.unread_mentions_count;
                if (i2 != i3) {
                    dialog2.unread_mentions_count = i3;
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(dialog2.id))) {
                        getNotificationCenter().postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(dialog2.id), Integer.valueOf(dialog2.unread_mentions_count));
                    }
                }
                MessageObject messageObject2 = this.dialogMessage.get(keyAt);
                if (messageObject2 != null && dialog2.top_message <= 0) {
                    MessageObject messageObject3 = (MessageObject) longSparseArray5.get(dialog.id);
                    if (messageObject2.deleted || messageObject3 == null || messageObject3.messageOwner.date > messageObject2.messageOwner.date) {
                        this.dialogs_dict.put(keyAt, dialog);
                        this.dialogMessage.put(keyAt, messageObject3);
                        if (messageObject3 != null && messageObject3.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                            long j3 = messageObject3.messageOwner.random_id;
                            if (j3 != 0) {
                                this.dialogMessagesByRandomIds.put(j3, messageObject3);
                            }
                        }
                        this.dialogMessagesByIds.remove(messageObject2.getId());
                        long j4 = messageObject2.messageOwner.random_id;
                        if (j4 != 0) {
                            this.dialogMessagesByRandomIds.remove(j4);
                        }
                    }
                } else if ((messageObject2 != null && messageObject2.deleted) || dialog.top_message > dialog2.top_message) {
                    this.dialogs_dict.put(keyAt, dialog);
                    MessageObject messageObject4 = (MessageObject) longSparseArray5.get(dialog.id);
                    this.dialogMessage.put(keyAt, messageObject4);
                    if (messageObject4 != null && messageObject4.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject4.getId(), messageObject4);
                        long j5 = messageObject4.messageOwner.random_id;
                        if (j5 != 0) {
                            this.dialogMessagesByRandomIds.put(j5, messageObject4);
                        }
                    }
                    if (messageObject2 != null) {
                        this.dialogMessagesByIds.remove(messageObject2.getId());
                        long j6 = messageObject2.messageOwner.random_id;
                        j = 0;
                        if (j6 != 0) {
                            this.dialogMessagesByRandomIds.remove(j6);
                        }
                    } else {
                        j = 0;
                    }
                    if (messageObject4 == null) {
                        checkLastDialogMessage(dialog, (TLRPC.InputPeer) null, j);
                    }
                }
            }
        }
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.allDialogs.add(this.dialogs_dict.valueAt(i4));
        }
        sortDialogs((SparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationsController().processDialogsUpdateRead(longSparseArray3);
    }

    public void addToViewsQueue(MessageObject messageObject) {
        Utilities.stageQueue.postRunnable(new Runnable(messageObject) {
            private final /* synthetic */ MessageObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$addToViewsQueue$155$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$addToViewsQueue$155$MessagesController(MessageObject messageObject) {
        int dialogId = (int) messageObject.getDialogId();
        int id = messageObject.getId();
        ArrayList arrayList = this.channelViewsToSend.get(dialogId);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.channelViewsToSend.put(dialogId, arrayList);
        }
        if (!arrayList.contains(Integer.valueOf(id))) {
            arrayList.add(Integer.valueOf(id));
        }
    }

    public void addToPollsQueue(long j, ArrayList<MessageObject> arrayList) {
        SparseArray sparseArray = this.pollsToCheck.get(j);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            this.pollsToCheck.put(j, sparseArray);
            this.pollsToCheckSize++;
        }
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            ((MessageObject) sparseArray.valueAt(i)).pollVisibleOnScreen = false;
        }
        int size2 = arrayList.size();
        for (int i2 = 0; i2 < size2; i2++) {
            MessageObject messageObject = arrayList.get(i2);
            if (messageObject.type == 17) {
                int id = messageObject.getId();
                MessageObject messageObject2 = (MessageObject) sparseArray.get(id);
                if (messageObject2 != null) {
                    messageObject2.pollVisibleOnScreen = true;
                } else {
                    sparseArray.put(id, messageObject);
                }
            }
        }
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        if (!messageObject.scheduled) {
            ArrayList arrayList = new ArrayList();
            long id = (long) messageObject.getId();
            int i = messageObject.messageOwner.to_id.channel_id;
            if (i != 0) {
                id |= ((long) i) << 32;
            }
            if (messageObject.messageOwner.mentioned) {
                getMessagesStorage().markMentionMessageAsRead(messageObject.getId(), messageObject.messageOwner.to_id.channel_id, messageObject.getDialogId());
            }
            arrayList.add(Long.valueOf(id));
            getMessagesStorage().markMessagesContentAsRead(arrayList, 0);
            getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, arrayList);
            if (messageObject.getId() < 0) {
                markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, Integer.MIN_VALUE);
            } else if (messageObject.messageOwner.to_id.channel_id != 0) {
                TLRPC.TL_channels_readMessageContents tL_channels_readMessageContents = new TLRPC.TL_channels_readMessageContents();
                tL_channels_readMessageContents.channel = getInputChannel(messageObject.messageOwner.to_id.channel_id);
                if (tL_channels_readMessageContents.channel != null) {
                    tL_channels_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
                    getConnectionsManager().sendRequest(tL_channels_readMessageContents, $$Lambda$MessagesController$P3y_yXlYAcq9BY9lnJVB8baeIk.INSTANCE);
                }
            } else {
                TLRPC.TL_messages_readMessageContents tL_messages_readMessageContents = new TLRPC.TL_messages_readMessageContents();
                tL_messages_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
                getConnectionsManager().sendRequest(tL_messages_readMessageContents, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$markMessageContentAsRead$157$MessagesController(tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$markMessageContentAsRead$157$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public void markMentionMessageAsRead(int i, int i2, long j) {
        getMessagesStorage().markMentionMessageAsRead(i, i2, j);
        if (i2 != 0) {
            TLRPC.TL_channels_readMessageContents tL_channels_readMessageContents = new TLRPC.TL_channels_readMessageContents();
            tL_channels_readMessageContents.channel = getInputChannel(i2);
            if (tL_channels_readMessageContents.channel != null) {
                tL_channels_readMessageContents.id.add(Integer.valueOf(i));
                getConnectionsManager().sendRequest(tL_channels_readMessageContents, $$Lambda$MessagesController$bKyWz4d9RqjIgwxMvWTMoX7LgpQ.INSTANCE);
                return;
            }
            return;
        }
        TLRPC.TL_messages_readMessageContents tL_messages_readMessageContents = new TLRPC.TL_messages_readMessageContents();
        tL_messages_readMessageContents.id.add(Integer.valueOf(i));
        getConnectionsManager().sendRequest(tL_messages_readMessageContents, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$markMentionMessageAsRead$159$MessagesController(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$159$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0080  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void markMessageAsRead(int r10, int r11, org.telegram.tgnet.TLRPC.InputChannel r12, int r13, long r14) {
        /*
            r9 = this;
            if (r10 == 0) goto L_0x009a
            if (r13 > 0) goto L_0x0006
            goto L_0x009a
        L_0x0006:
            if (r11 == 0) goto L_0x0011
            if (r12 != 0) goto L_0x0011
            org.telegram.tgnet.TLRPC$InputChannel r12 = r9.getInputChannel((int) r11)
            if (r12 != 0) goto L_0x0011
            return
        L_0x0011:
            r0 = 0
            int r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x004d
            r14 = 0
            org.telegram.tgnet.NativeByteBuffer r15 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x003e }
            r0 = 16
            if (r12 == 0) goto L_0x0023
            int r1 = r12.getObjectSize()     // Catch:{ Exception -> 0x003e }
            goto L_0x0024
        L_0x0023:
            r1 = 0
        L_0x0024:
            int r0 = r0 + r1
            r15.<init>((int) r0)     // Catch:{ Exception -> 0x003e }
            r14 = 11
            r15.writeInt32(r14)     // Catch:{ Exception -> 0x003c }
            r15.writeInt32(r10)     // Catch:{ Exception -> 0x003c }
            r15.writeInt32(r11)     // Catch:{ Exception -> 0x003c }
            r15.writeInt32(r13)     // Catch:{ Exception -> 0x003c }
            if (r11 == 0) goto L_0x0045
            r12.serializeToStream(r15)     // Catch:{ Exception -> 0x003c }
            goto L_0x0045
        L_0x003c:
            r14 = move-exception
            goto L_0x0042
        L_0x003e:
            r15 = move-exception
            r8 = r15
            r15 = r14
            r14 = r8
        L_0x0042:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x0045:
            org.telegram.messenger.MessagesStorage r14 = r9.getMessagesStorage()
            long r14 = r14.createPendingTask(r15)
        L_0x004d:
            org.telegram.tgnet.ConnectionsManager r0 = r9.getConnectionsManager()
            int r5 = r0.getCurrentTime()
            org.telegram.messenger.MessagesStorage r1 = r9.getMessagesStorage()
            r7 = 0
            r2 = r10
            r3 = r11
            r4 = r5
            r6 = r13
            r1.createTaskForMid(r2, r3, r4, r5, r6, r7)
            if (r11 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$TL_channels_readMessageContents r11 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents
            r11.<init>()
            r11.channel = r12
            java.util.ArrayList<java.lang.Integer> r12 = r11.id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r12.add(r10)
            org.telegram.tgnet.ConnectionsManager r10 = r9.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$a8AfI5PchvJOW7BJMH5vX8bjCw0 r12 = new org.telegram.messenger.-$$Lambda$MessagesController$a8AfI5PchvJOW7BJMH5vX8bjCw0
            r12.<init>(r14)
            r10.sendRequest(r11, r12)
            goto L_0x009a
        L_0x0080:
            org.telegram.tgnet.TLRPC$TL_messages_readMessageContents r11 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents
            r11.<init>()
            java.util.ArrayList<java.lang.Integer> r12 = r11.id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r12.add(r10)
            org.telegram.tgnet.ConnectionsManager r10 = r9.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$L2Rh9QgSg-jXaF9a3gzsXr_ZEb0 r12 = new org.telegram.messenger.-$$Lambda$MessagesController$L2Rh9QgSg-jXaF9a3gzsXr_ZEb0
            r12.<init>(r14)
            r10.sendRequest(r11, r12)
        L_0x009a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markMessageAsRead(int, int, org.telegram.tgnet.TLRPC$InputChannel, int, long):void");
    }

    public /* synthetic */ void lambda$markMessageAsRead$160$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$markMessageAsRead$161$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void markMessageAsRead(long j, long j2, int i) {
        TLRPC.EncryptedChat encryptedChat;
        if (j2 != 0 && j != 0) {
            if (i > 0 || i == Integer.MIN_VALUE) {
                int i2 = (int) (j >> 32);
                if (((int) j) == 0 && (encryptedChat = getEncryptedChat(Integer.valueOf(i2))) != null) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Long.valueOf(j2));
                    getSecretChatHelper().sendMessagesReadMessage(encryptedChat, arrayList, (TLRPC.Message) null);
                    if (i > 0) {
                        int currentTime = getConnectionsManager().getCurrentTime();
                        getMessagesStorage().createTaskForSecretChat(encryptedChat.id, currentTime, currentTime, 0, arrayList);
                    }
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readHistory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readHistory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLRPC$TL_channels_readHistory} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readHistory} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void completeReadTask(org.telegram.messenger.MessagesController.ReadTask r6) {
        /*
            r5 = this;
            long r0 = r6.dialogId
            int r2 = (int) r0
            r3 = 32
            long r0 = r0 >> r3
            int r1 = (int) r0
            if (r2 == 0) goto L_0x003b
            org.telegram.tgnet.TLRPC$InputPeer r0 = r5.getInputPeer(r2)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel
            if (r1 == 0) goto L_0x0022
            org.telegram.tgnet.TLRPC$TL_channels_readHistory r0 = new org.telegram.tgnet.TLRPC$TL_channels_readHistory
            r0.<init>()
            int r1 = -r2
            org.telegram.tgnet.TLRPC$InputChannel r1 = r5.getInputChannel((int) r1)
            r0.channel = r1
            int r6 = r6.maxId
            r0.max_id = r6
            goto L_0x002e
        L_0x0022:
            org.telegram.tgnet.TLRPC$TL_messages_readHistory r1 = new org.telegram.tgnet.TLRPC$TL_messages_readHistory
            r1.<init>()
            r1.peer = r0
            int r6 = r6.maxId
            r1.max_id = r6
            r0 = r1
        L_0x002e:
            org.telegram.tgnet.ConnectionsManager r6 = r5.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$mms-IBNoBdN67tH76zAO3xKUj68 r1 = new org.telegram.messenger.-$$Lambda$MessagesController$mms-IBNoBdN67tH76zAO3xKUj68
            r1.<init>()
            r6.sendRequest(r0, r1)
            goto L_0x0072
        L_0x003b:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r5.getEncryptedChat(r0)
            byte[] r1 = r0.auth_key
            if (r1 == 0) goto L_0x0072
            int r1 = r1.length
            r2 = 1
            if (r1 <= r2) goto L_0x0072
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat
            if (r1 == 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$TL_messages_readEncryptedHistory r1 = new org.telegram.tgnet.TLRPC$TL_messages_readEncryptedHistory
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat
            r2.<init>()
            r1.peer = r2
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer
            int r3 = r0.id
            r2.chat_id = r3
            long r3 = r0.access_hash
            r2.access_hash = r3
            int r6 = r6.maxDate
            r1.max_date = r6
            org.telegram.tgnet.ConnectionsManager r6 = r5.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0 r0 = org.telegram.messenger.$$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0.INSTANCE
            r6.sendRequest(r1, r0)
        L_0x0072:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.completeReadTask(org.telegram.messenger.MessagesController$ReadTask):void");
    }

    public /* synthetic */ void lambda$completeReadTask$162$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null && (tLObject instanceof TLRPC.TL_messages_affectedMessages)) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    private void checkReadTasks() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int size = this.readTasks.size();
        int i = 0;
        while (i < size) {
            ReadTask readTask = this.readTasks.get(i);
            if (readTask.sendRequestTime <= elapsedRealtime) {
                completeReadTask(readTask);
                this.readTasks.remove(i);
                this.readTasksMap.remove(readTask.dialogId);
                i--;
                size--;
            }
            i++;
        }
    }

    public void markDialogAsReadNow(long j) {
        Utilities.stageQueue.postRunnable(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$markDialogAsReadNow$164$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$markDialogAsReadNow$164$MessagesController(long j) {
        ReadTask readTask = this.readTasksMap.get(j);
        if (readTask != null) {
            completeReadTask(readTask);
            this.readTasks.remove(readTask);
            this.readTasksMap.remove(j);
        }
    }

    public void markMentionsAsRead(long j) {
        int i = (int) j;
        if (i != 0) {
            getMessagesStorage().resetMentionsCount(j, 0);
            TLRPC.TL_messages_readMentions tL_messages_readMentions = new TLRPC.TL_messages_readMentions();
            tL_messages_readMentions.peer = getInputPeer(i);
            getConnectionsManager().sendRequest(tL_messages_readMentions, $$Lambda$MessagesController$WKod0JD8yhqRRb3q2XGhKTgK1JI.INSTANCE);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0092  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void markDialogAsRead(long r18, int r20, int r21, int r22, boolean r23, int r24, boolean r25, int r26) {
        /*
            r17 = this;
            r9 = r17
            r10 = r18
            r12 = r20
            r13 = r21
            r14 = r22
            int r0 = (int) r10
            r1 = 32
            long r2 = r10 >> r1
            int r3 = (int) r2
            org.telegram.messenger.NotificationsController r2 = r17.getNotificationsController()
            boolean r15 = r2.showBadgeMessages
            r16 = 1
            if (r0 == 0) goto L_0x0095
            if (r12 != 0) goto L_0x001d
            return
        L_0x001d:
            long r2 = (long) r12
            long r4 = (long) r13
            r13 = 0
            if (r0 >= 0) goto L_0x003a
            int r0 = -r0
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r6 = r9.getChat(r6)
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x003a
            long r6 = (long) r0
            long r0 = r6 << r1
            long r2 = r2 | r0
            long r0 = r0 | r4
            r5 = r0
            r3 = r2
            r7 = 1
            goto L_0x003d
        L_0x003a:
            r5 = r4
            r7 = 0
            r3 = r2
        L_0x003d:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r9.dialogs_read_inbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r18)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x004f
            java.lang.Integer r0 = java.lang.Integer.valueOf(r13)
        L_0x004f:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r9.dialogs_read_inbox_max
            java.lang.Long r2 = java.lang.Long.valueOf(r18)
            int r0 = r0.intValue()
            int r0 = java.lang.Math.max(r0, r12)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r1.put(r2, r0)
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            r1 = r18
            r8 = r26
            r0.processPendingRead(r1, r3, r5, r7, r8)
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$XwgyweEtDE07QZ0DyXMbifsq5Ak r7 = new org.telegram.messenger.-$$Lambda$MessagesController$XwgyweEtDE07QZ0DyXMbifsq5Ak
            r0 = r7
            r1 = r17
            r2 = r18
            r4 = r24
            r5 = r20
            r6 = r15
            r15 = r7
            r7 = r23
            r0.<init>(r2, r4, r5, r6, r7)
            r8.postRunnable(r15)
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r12 == r0) goto L_0x0092
            goto L_0x00ee
        L_0x0092:
            r16 = 0
            goto L_0x00ee
        L_0x0095:
            if (r14 != 0) goto L_0x0098
            return
        L_0x0098:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = r9.getEncryptedChat(r0)
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            long r3 = (long) r12
            long r5 = (long) r13
            r7 = 0
            r1 = r18
            r9 = r8
            r8 = r26
            r0.processPendingRead(r1, r3, r5, r7, r8)
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$_ziRmGsp8OtorSdwXBpuudGlD6c r7 = new org.telegram.messenger.-$$Lambda$MessagesController$_ziRmGsp8OtorSdwXBpuudGlD6c
            r0 = r7
            r1 = r17
            r2 = r18
            r4 = r22
            r5 = r23
            r6 = r24
            r10 = r7
            r7 = r21
            r11 = r8
            r8 = r15
            r0.<init>(r2, r4, r5, r6, r7, r8)
            r11.postRunnable(r10)
            if (r9 == 0) goto L_0x00ee
            int r0 = r9.ttl
            if (r0 <= 0) goto L_0x00ee
            org.telegram.tgnet.ConnectionsManager r0 = r17.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            int r3 = java.lang.Math.max(r0, r14)
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            int r1 = r9.id
            r4 = 0
            r5 = 0
            r2 = r22
            r0.createTaskForSecretChat(r1, r2, r3, r4, r5)
        L_0x00ee:
            if (r16 == 0) goto L_0x0105
            org.telegram.messenger.DispatchQueue r7 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$MessagesController$PG02LYk94FZTZxqn2NcjESCdKK0 r8 = new org.telegram.messenger.-$$Lambda$MessagesController$PG02LYk94FZTZxqn2NcjESCdKK0
            r0 = r8
            r1 = r17
            r2 = r18
            r4 = r25
            r5 = r22
            r6 = r20
            r0.<init>(r2, r4, r5, r6)
            r7.postRunnable(r8)
        L_0x0105:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markDialogAsRead(long, int, int, int, boolean, int, boolean, int):void");
    }

    public /* synthetic */ void lambda$markDialogAsRead$167$MessagesController(long j, int i, int i2, boolean z, boolean z2) {
        AndroidUtilities.runOnUIThread(new Runnable(j, i, i2, z, z2) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                MessagesController.this.lambda$null$166$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$166$MessagesController(long j, int i, int i2, boolean z, boolean z2) {
        TLRPC.Dialog dialog;
        long j2 = j;
        int i3 = i2;
        TLRPC.Dialog dialog2 = this.dialogs_dict.get(j2);
        if (dialog2 != null) {
            int i4 = dialog2.unread_count;
            if (i == 0 || i3 >= dialog2.top_message) {
                dialog2.unread_count = 0;
            } else {
                dialog2.unread_count = Math.max(i4 - i, 0);
                if (i3 != Integer.MIN_VALUE) {
                    int i5 = dialog2.unread_count;
                    int i6 = dialog2.top_message;
                    if (i5 > i6 - i3) {
                        dialog2.unread_count = i6 - i3;
                    }
                }
            }
            int i7 = dialog2.folder_id;
            if (!(i7 == 0 || (dialog = this.dialogs_dict.get(DialogObject.makeFolderDialogId(i7))) == null)) {
                if (z) {
                    if (isDialogMuted(dialog2.id)) {
                        dialog.unread_count -= i4 - dialog2.unread_count;
                    } else {
                        dialog.unread_mentions_count -= i4 - dialog2.unread_count;
                    }
                } else if (dialog2.unread_count == 0) {
                    if (isDialogMuted(dialog2.id)) {
                        dialog.unread_count--;
                    } else {
                        dialog.unread_mentions_count--;
                    }
                }
            }
            if ((i4 != 0 || dialog2.unread_mark) && dialog2.unread_count == 0 && !isDialogMuted(j)) {
                this.unreadUnmutedDialogs--;
            }
            if (dialog2.unread_mark) {
                dialog2.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog2.id, false);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 256);
        }
        if (!z2) {
            getNotificationsController().processReadMessages((SparseLongArray) null, j, 0, i2, false);
            LongSparseArray longSparseArray = new LongSparseArray(1);
            longSparseArray.put(j2, 0);
            getNotificationsController().processDialogsUpdateRead(longSparseArray);
            return;
        }
        getNotificationsController().processReadMessages((SparseLongArray) null, j, 0, i2, true);
        LongSparseArray longSparseArray2 = new LongSparseArray(1);
        longSparseArray2.put(j2, -1);
        getNotificationsController().processDialogsUpdateRead(longSparseArray2);
    }

    public /* synthetic */ void lambda$markDialogAsRead$169$MessagesController(long j, int i, boolean z, int i2, int i3, boolean z2) {
        AndroidUtilities.runOnUIThread(new Runnable(j, i, z, i2, i3, z2) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                MessagesController.this.lambda$null$168$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$168$MessagesController(long j, int i, boolean z, int i2, int i3, boolean z2) {
        TLRPC.Dialog dialog;
        getNotificationsController().processReadMessages((SparseLongArray) null, j, i, 0, z);
        TLRPC.Dialog dialog2 = this.dialogs_dict.get(j);
        if (dialog2 != null) {
            int i4 = dialog2.unread_count;
            if (i2 == 0 || i3 <= dialog2.top_message) {
                dialog2.unread_count = 0;
            } else {
                dialog2.unread_count = Math.max(i4 - i2, 0);
                if (i3 != Integer.MAX_VALUE) {
                    int i5 = dialog2.unread_count;
                    int i6 = dialog2.top_message;
                    if (i5 > i3 - i6) {
                        dialog2.unread_count = i3 - i6;
                    }
                }
            }
            int i7 = dialog2.folder_id;
            if (!(i7 == 0 || (dialog = this.dialogs_dict.get(DialogObject.makeFolderDialogId(i7))) == null)) {
                if (z2) {
                    if (isDialogMuted(dialog2.id)) {
                        dialog.unread_count -= i4 - dialog2.unread_count;
                    } else {
                        dialog.unread_mentions_count -= i4 - dialog2.unread_count;
                    }
                } else if (dialog2.unread_count == 0) {
                    if (isDialogMuted(dialog2.id)) {
                        dialog.unread_count--;
                    } else {
                        dialog.unread_mentions_count--;
                    }
                }
            }
            if ((i4 != 0 || dialog2.unread_mark) && dialog2.unread_count == 0 && !isDialogMuted(j)) {
                this.unreadUnmutedDialogs--;
            }
            if (dialog2.unread_mark) {
                dialog2.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog2.id, false);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 256);
        }
        LongSparseArray longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j, 0);
        getNotificationsController().processDialogsUpdateRead(longSparseArray);
    }

    public /* synthetic */ void lambda$markDialogAsRead$170$MessagesController(long j, boolean z, int i, int i2) {
        ReadTask readTask = this.readTasksMap.get(j);
        if (readTask == null) {
            readTask = new ReadTask();
            readTask.dialogId = j;
            readTask.sendRequestTime = SystemClock.elapsedRealtime() + 5000;
            if (!z) {
                this.readTasksMap.put(j, readTask);
                this.readTasks.add(readTask);
            }
        }
        readTask.maxDate = i;
        readTask.maxId = i2;
        if (z) {
            completeReadTask(readTask);
        }
    }

    public int createChat(String str, ArrayList<Integer> arrayList, String str2, int i, Location location, String str3, BaseFragment baseFragment) {
        if (i == 0) {
            TLRPC.TL_messages_createChat tL_messages_createChat = new TLRPC.TL_messages_createChat();
            tL_messages_createChat.title = str;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC.User user = getUser(arrayList.get(i2));
                if (user != null) {
                    tL_messages_createChat.users.add(getInputUser(user));
                }
            }
            return getConnectionsManager().sendRequest(tL_messages_createChat, new RequestDelegate(baseFragment, tL_messages_createChat) {
                private final /* synthetic */ BaseFragment f$1;
                private final /* synthetic */ TLRPC.TL_messages_createChat f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$createChat$173$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 2);
        } else if (i != 2 && i != 4) {
            return 0;
        } else {
            TLRPC.TL_channels_createChannel tL_channels_createChannel = new TLRPC.TL_channels_createChannel();
            tL_channels_createChannel.title = str;
            if (str2 == null) {
                str2 = "";
            }
            tL_channels_createChannel.about = str2;
            if (i == 4) {
                tL_channels_createChannel.megagroup = true;
            } else {
                tL_channels_createChannel.broadcast = true;
            }
            if (location != null) {
                tL_channels_createChannel.geo_point = new TLRPC.TL_inputGeoPoint();
                tL_channels_createChannel.geo_point.lat = location.getLatitude();
                tL_channels_createChannel.geo_point._long = location.getLongitude();
                tL_channels_createChannel.address = str3;
                tL_channels_createChannel.flags |= 4;
            }
            return getConnectionsManager().sendRequest(tL_channels_createChannel, new RequestDelegate(baseFragment, tL_channels_createChannel) {
                private final /* synthetic */ BaseFragment f$1;
                private final /* synthetic */ TLRPC.TL_channels_createChannel f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$createChat$176$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 2);
        }
    }

    public /* synthetic */ void lambda$createChat$173$MessagesController(BaseFragment baseFragment, TLRPC.TL_messages_createChat tL_messages_createChat, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_messages_createChat) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_messages_createChat f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MessagesController.this.lambda$null$171$MessagesController(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) tLObject;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable(updates) {
            private final /* synthetic */ TLRPC.Updates f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$172$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$171$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_createChat tL_messages_createChat) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_createChat, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$null$172$MessagesController(TLRPC.Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        ArrayList<TLRPC.Chat> arrayList = updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(updates.chats.get(0).id));
    }

    public /* synthetic */ void lambda$createChat$176$MessagesController(BaseFragment baseFragment, TLRPC.TL_channels_createChannel tL_channels_createChannel, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_channels_createChannel) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_channels_createChannel f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MessagesController.this.lambda$null$174$MessagesController(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) tLObject;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable(updates) {
            private final /* synthetic */ TLRPC.Updates f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$175$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$174$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_createChannel tL_channels_createChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_createChannel, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$null$175$MessagesController(TLRPC.Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        ArrayList<TLRPC.Chat> arrayList = updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(updates.chats.get(0).id));
    }

    public void convertToMegaGroup(Context context, int i, BaseFragment baseFragment, MessagesStorage.IntCallback intCallback) {
        TLRPC.TL_messages_migrateChat tL_messages_migrateChat = new TLRPC.TL_messages_migrateChat();
        tL_messages_migrateChat.chat_id = i;
        AlertDialog alertDialog = new AlertDialog(context, 3);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(tL_messages_migrateChat, new RequestDelegate(context, alertDialog, intCallback, baseFragment, tL_messages_migrateChat) {
            private final /* synthetic */ Context f$1;
            private final /* synthetic */ AlertDialog f$2;
            private final /* synthetic */ MessagesStorage.IntCallback f$3;
            private final /* synthetic */ BaseFragment f$4;
            private final /* synthetic */ TLRPC.TL_messages_migrateChat f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$convertToMegaGroup$180$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                MessagesController.this.lambda$convertToMegaGroup$181$MessagesController(this.f$1, dialogInterface);
            }
        });
        try {
            alertDialog.show();
        } catch (Exception unused) {
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$180$MessagesController(Context context, AlertDialog alertDialog, MessagesStorage.IntCallback intCallback, BaseFragment baseFragment, TLRPC.TL_messages_migrateChat tL_messages_migrateChat, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog) {
                private final /* synthetic */ Context f$0;
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.lambda$null$177(this.f$0, this.f$1);
                }
            });
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new Runnable(updates) {
                private final /* synthetic */ TLRPC.Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.lambda$null$178(MessagesStorage.IntCallback.this, this.f$1);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(intCallback, context, alertDialog, tL_error, baseFragment, tL_messages_migrateChat) {
            private final /* synthetic */ MessagesStorage.IntCallback f$1;
            private final /* synthetic */ Context f$2;
            private final /* synthetic */ AlertDialog f$3;
            private final /* synthetic */ TLRPC.TL_error f$4;
            private final /* synthetic */ BaseFragment f$5;
            private final /* synthetic */ TLRPC.TL_messages_migrateChat f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MessagesController.this.lambda$null$179$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    static /* synthetic */ void lambda$null$177(Context context, AlertDialog alertDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ void lambda$null$178(MessagesStorage.IntCallback intCallback, TLRPC.Updates updates) {
        if (intCallback != null) {
            for (int i = 0; i < updates.chats.size(); i++) {
                TLRPC.Chat chat = updates.chats.get(i);
                if (ChatObject.isChannel(chat)) {
                    intCallback.run(chat.id);
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$179$MessagesController(MessagesStorage.IntCallback intCallback, Context context, AlertDialog alertDialog, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_migrateChat tL_messages_migrateChat) {
        if (intCallback != null) {
            intCallback.run(0);
        }
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_migrateChat, false);
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$181$MessagesController(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public void addUsersToChannel(int i, ArrayList<TLRPC.InputUser> arrayList, BaseFragment baseFragment) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel = new TLRPC.TL_channels_inviteToChannel();
            tL_channels_inviteToChannel.channel = getInputChannel(i);
            tL_channels_inviteToChannel.users = arrayList;
            getConnectionsManager().sendRequest(tL_channels_inviteToChannel, new RequestDelegate(baseFragment, tL_channels_inviteToChannel) {
                private final /* synthetic */ BaseFragment f$1;
                private final /* synthetic */ TLRPC.TL_channels_inviteToChannel f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$addUsersToChannel$183$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addUsersToChannel$183$MessagesController(BaseFragment baseFragment, TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_channels_inviteToChannel) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_channels_inviteToChannel f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MessagesController.this.lambda$null$182$MessagesController(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$182$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_inviteToChannel, true);
    }

    public void toogleChannelSignatures(int i, boolean z) {
        TLRPC.TL_channels_toggleSignatures tL_channels_toggleSignatures = new TLRPC.TL_channels_toggleSignatures();
        tL_channels_toggleSignatures.channel = getInputChannel(i);
        tL_channels_toggleSignatures.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_toggleSignatures, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toogleChannelSignatures$185$MessagesController(tLObject, tL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toogleChannelSignatures$185$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MessagesController.this.lambda$null$184$MessagesController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$184$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 8192);
    }

    public void toogleChannelInvitesHistory(int i, boolean z) {
        TLRPC.TL_channels_togglePreHistoryHidden tL_channels_togglePreHistoryHidden = new TLRPC.TL_channels_togglePreHistoryHidden();
        tL_channels_togglePreHistoryHidden.channel = getInputChannel(i);
        tL_channels_togglePreHistoryHidden.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_togglePreHistoryHidden, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$toogleChannelInvitesHistory$187$MessagesController(tLObject, tL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toogleChannelInvitesHistory$187$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MessagesController.this.lambda$null$186$MessagesController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$186$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 8192);
    }

    public void updateChatAbout(int i, String str, TLRPC.ChatFull chatFull) {
        if (chatFull != null) {
            TLRPC.TL_messages_editChatAbout tL_messages_editChatAbout = new TLRPC.TL_messages_editChatAbout();
            tL_messages_editChatAbout.peer = getInputPeer(-i);
            tL_messages_editChatAbout.about = str;
            getConnectionsManager().sendRequest(tL_messages_editChatAbout, new RequestDelegate(chatFull, str) {
                private final /* synthetic */ TLRPC.ChatFull f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$updateChatAbout$189$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 64);
        }
    }

    public /* synthetic */ void lambda$updateChatAbout$189$MessagesController(TLRPC.ChatFull chatFull, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable(chatFull, str) {
                private final /* synthetic */ TLRPC.ChatFull f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MessagesController.this.lambda$null$188$MessagesController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$188$MessagesController(TLRPC.ChatFull chatFull, String str) {
        chatFull.about = str;
        getMessagesStorage().updateChatInfo(chatFull, false);
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, 0, false, null);
    }

    public void updateChannelUserName(int i, String str) {
        TLRPC.TL_channels_updateUsername tL_channels_updateUsername = new TLRPC.TL_channels_updateUsername();
        tL_channels_updateUsername.channel = getInputChannel(i);
        tL_channels_updateUsername.username = str;
        getConnectionsManager().sendRequest(tL_channels_updateUsername, new RequestDelegate(i, str) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$updateChannelUserName$191$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$updateChannelUserName$191$MessagesController(int i, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable(i, str) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MessagesController.this.lambda$null$190$MessagesController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$190$MessagesController(int i, String str) {
        TLRPC.Chat chat = getChat(Integer.valueOf(i));
        if (str.length() != 0) {
            chat.flags |= 64;
        } else {
            chat.flags &= -65;
        }
        chat.username = str;
        ArrayList arrayList = new ArrayList();
        arrayList.add(chat);
        getMessagesStorage().putUsersAndChats((ArrayList<TLRPC.User>) null, arrayList, true, true);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 8192);
    }

    public void sendBotStart(TLRPC.User user, String str) {
        if (user != null) {
            TLRPC.TL_messages_startBot tL_messages_startBot = new TLRPC.TL_messages_startBot();
            tL_messages_startBot.bot = getInputUser(user);
            tL_messages_startBot.peer = getInputPeer(user.id);
            tL_messages_startBot.start_param = str;
            tL_messages_startBot.random_id = Utilities.random.nextLong();
            getConnectionsManager().sendRequest(tL_messages_startBot, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$sendBotStart$192$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendBotStart$192$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public boolean isJoiningChannel(int i) {
        return this.joiningToChannels.contains(Integer.valueOf(i));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_messages_addChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: org.telegram.tgnet.TLRPC$TL_channels_joinChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: org.telegram.tgnet.TLRPC$TL_messages_startBot} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: org.telegram.tgnet.TLRPC$TL_messages_addChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v21, resolved type: org.telegram.tgnet.TLRPC$TL_messages_addChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: org.telegram.tgnet.TLRPC$TL_messages_addChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v23, resolved type: org.telegram.tgnet.TLRPC$TL_messages_addChatUser} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addUserToChat(int r14, org.telegram.tgnet.TLRPC.User r15, org.telegram.tgnet.TLRPC.ChatFull r16, int r17, java.lang.String r18, org.telegram.ui.ActionBar.BaseFragment r19, java.lang.Runnable r20) {
        /*
            r13 = this;
            r9 = r13
            r4 = r14
            r0 = r15
            r1 = r16
            r2 = r18
            if (r0 != 0) goto L_0x000a
            return
        L_0x000a:
            r3 = 0
            r5 = 1
            if (r4 <= 0) goto L_0x00b5
            int r1 = r9.currentAccount
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r14, r1)
            if (r6 == 0) goto L_0x0024
            java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r1 = r13.getChat(r1)
            boolean r1 = r1.megagroup
            if (r1 == 0) goto L_0x0024
            r7 = 1
            goto L_0x0025
        L_0x0024:
            r7 = 0
        L_0x0025:
            org.telegram.tgnet.TLRPC$InputUser r3 = r13.getInputUser((org.telegram.tgnet.TLRPC.User) r15)
            if (r2 == 0) goto L_0x0057
            if (r6 == 0) goto L_0x0030
            if (r7 != 0) goto L_0x0030
            goto L_0x0057
        L_0x0030:
            org.telegram.tgnet.TLRPC$TL_messages_startBot r0 = new org.telegram.tgnet.TLRPC$TL_messages_startBot
            r0.<init>()
            r0.bot = r3
            if (r6 == 0) goto L_0x0041
            int r1 = -r4
            org.telegram.tgnet.TLRPC$InputPeer r1 = r13.getInputPeer(r1)
            r0.peer = r1
            goto L_0x004c
        L_0x0041:
            org.telegram.tgnet.TLRPC$TL_inputPeerChat r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat
            r1.<init>()
            r0.peer = r1
            org.telegram.tgnet.TLRPC$InputPeer r1 = r0.peer
            r1.chat_id = r4
        L_0x004c:
            r0.start_param = r2
            java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random
            long r1 = r1.nextLong()
            r0.random_id = r1
            goto L_0x009d
        L_0x0057:
            if (r6 == 0) goto L_0x0090
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC.TL_inputUserSelf
            if (r0 == 0) goto L_0x007f
            java.util.ArrayList<java.lang.Integer> r0 = r9.joiningToChannels
            java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x006a
            return
        L_0x006a:
            org.telegram.tgnet.TLRPC$TL_channels_joinChannel r0 = new org.telegram.tgnet.TLRPC$TL_channels_joinChannel
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = r13.getInputChannel((int) r14)
            r0.channel = r1
            java.util.ArrayList<java.lang.Integer> r1 = r9.joiningToChannels
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            r1.add(r2)
            goto L_0x009d
        L_0x007f:
            org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel r0 = new org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = r13.getInputChannel((int) r14)
            r0.channel = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputUser> r1 = r0.users
            r1.add(r3)
            goto L_0x009d
        L_0x0090:
            org.telegram.tgnet.TLRPC$TL_messages_addChatUser r0 = new org.telegram.tgnet.TLRPC$TL_messages_addChatUser
            r0.<init>()
            r0.chat_id = r4
            r1 = r17
            r0.fwd_limit = r1
            r0.user_id = r3
        L_0x009d:
            r10 = r0
            org.telegram.tgnet.ConnectionsManager r11 = r13.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$VLQQD_L7rWydtqdkTepxBzSikLA r12 = new org.telegram.messenger.-$$Lambda$MessagesController$VLQQD_L7rWydtqdkTepxBzSikLA
            r0 = r12
            r1 = r13
            r2 = r6
            r4 = r14
            r5 = r19
            r6 = r10
            r8 = r20
            r0.<init>(r2, r3, r4, r5, r6, r7, r8)
            r11.sendRequest(r10, r12)
            goto L_0x0151
        L_0x00b5:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatFull
            if (r2 == 0) goto L_0x0151
            r2 = 0
        L_0x00ba:
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r6 = r6.participants
            int r6 = r6.size()
            if (r2 >= r6) goto L_0x00d8
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r6 = r6.participants
            java.lang.Object r6 = r6.get(r2)
            org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC.ChatParticipant) r6
            int r6 = r6.user_id
            int r7 = r0.id
            if (r6 != r7) goto L_0x00d5
            return
        L_0x00d5:
            int r2 = r2 + 1
            goto L_0x00ba
        L_0x00d8:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r2 = r13.getChat(r2)
            int r4 = r2.participants_count
            int r4 = r4 + r5
            r2.participants_count = r4
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r4.add(r2)
            org.telegram.messenger.MessagesStorage r2 = r13.getMessagesStorage()
            r6 = 0
            r2.putUsersAndChats(r6, r4, r5, r5)
            org.telegram.tgnet.TLRPC$TL_chatParticipant r2 = new org.telegram.tgnet.TLRPC$TL_chatParticipant
            r2.<init>()
            int r0 = r0.id
            r2.user_id = r0
            org.telegram.messenger.UserConfig r0 = r13.getUserConfig()
            int r0 = r0.getClientUserId()
            r2.inviter_id = r0
            org.telegram.tgnet.ConnectionsManager r0 = r13.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r2.date = r0
            org.telegram.tgnet.TLRPC$ChatParticipants r0 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r0 = r0.participants
            r0.add(r3, r2)
            org.telegram.messenger.MessagesStorage r0 = r13.getMessagesStorage()
            r0.updateChatInfo(r1, r5)
            org.telegram.messenger.NotificationCenter r0 = r13.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r4 = 4
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            r4[r5] = r1
            r1 = 2
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r3)
            r4[r1] = r7
            r1 = 3
            r4[r1] = r6
            r0.postNotificationName(r2, r4)
            org.telegram.messenger.NotificationCenter r0 = r13.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.updateInterfaces
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r4 = 32
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r2[r3] = r4
            r0.postNotificationName(r1, r2)
        L_0x0151:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.addUserToChat(int, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$ChatFull, int, java.lang.String, org.telegram.ui.ActionBar.BaseFragment, java.lang.Runnable):void");
    }

    public /* synthetic */ void lambda$addUserToChat$196$MessagesController(boolean z, TLRPC.InputUser inputUser, int i, BaseFragment baseFragment, TLObject tLObject, boolean z2, Runnable runnable, TLObject tLObject2, TLRPC.TL_error tL_error) {
        boolean z3;
        TLRPC.InputUser inputUser2 = inputUser;
        int i2 = i;
        if (z && (inputUser2 instanceof TLRPC.TL_inputUserSelf)) {
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$193$MessagesController(this.f$1);
                }
            });
        }
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tLObject, z, z2, inputUser) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ TLRPC.InputUser f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    MessagesController.this.lambda$null$194$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) tLObject2;
        int i3 = 0;
        while (true) {
            if (i3 >= updates.updates.size()) {
                z3 = false;
                break;
            }
            TLRPC.Update update = updates.updates.get(i3);
            if ((update instanceof TLRPC.TL_updateNewChannelMessage) && (((TLRPC.TL_updateNewChannelMessage) update).message.action instanceof TLRPC.TL_messageActionChatAddUser)) {
                z3 = true;
                break;
            }
            i3++;
        }
        processUpdates(updates, false);
        if (z) {
            if (!z3 && (inputUser2 instanceof TLRPC.TL_inputUserSelf)) {
                generateJoinMessage(i, true);
            }
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$195$MessagesController(this.f$1);
                }
            }, 1000);
        }
        if (z && (inputUser2 instanceof TLRPC.TL_inputUserSelf)) {
            getMessagesStorage().updateDialogsWithDeletedMessages(new ArrayList(), (ArrayList<Long>) null, true, i);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$null$193$MessagesController(int i) {
        this.joiningToChannels.remove(Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$null$194$MessagesController(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2, TLRPC.InputUser inputUser) {
        int i = this.currentAccount;
        Object[] objArr = new Object[1];
        objArr[0] = Boolean.valueOf(z && !z2);
        AlertsCreator.processError(i, tL_error, baseFragment, tLObject, objArr);
        if (z && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 8192);
        }
    }

    public /* synthetic */ void lambda$null$195$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public void deleteUserFromChat(int i, TLRPC.User user, TLRPC.ChatFull chatFull) {
        deleteUserFromChat(i, user, chatFull, false, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: org.telegram.tgnet.TLRPC$TL_channels_editBanned} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: org.telegram.tgnet.TLRPC$TL_channels_leaveChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: org.telegram.tgnet.TLRPC$TL_channels_deleteChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deleteUserFromChat(int r6, org.telegram.tgnet.TLRPC.User r7, org.telegram.tgnet.TLRPC.ChatFull r8, boolean r9, boolean r10) {
        /*
            r5 = this;
            if (r7 != 0) goto L_0x0003
            return
        L_0x0003:
            r0 = 0
            r1 = 1
            if (r6 <= 0) goto L_0x0098
            org.telegram.tgnet.TLRPC$InputUser r8 = r5.getInputUser((org.telegram.tgnet.TLRPC.User) r7)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r5.getChat(r2)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x006a
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputUserSelf
            if (r4 == 0) goto L_0x003b
            boolean r1 = r2.creator
            if (r1 == 0) goto L_0x002f
            if (r9 == 0) goto L_0x002f
            org.telegram.tgnet.TLRPC$TL_channels_deleteChannel r9 = new org.telegram.tgnet.TLRPC$TL_channels_deleteChannel
            r9.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = getInputChannel((org.telegram.tgnet.TLRPC.Chat) r2)
            r9.channel = r1
            goto L_0x0077
        L_0x002f:
            org.telegram.tgnet.TLRPC$TL_channels_leaveChannel r9 = new org.telegram.tgnet.TLRPC$TL_channels_leaveChannel
            r9.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = getInputChannel((org.telegram.tgnet.TLRPC.Chat) r2)
            r9.channel = r1
            goto L_0x0077
        L_0x003b:
            org.telegram.tgnet.TLRPC$TL_channels_editBanned r9 = new org.telegram.tgnet.TLRPC$TL_channels_editBanned
            r9.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r2 = getInputChannel((org.telegram.tgnet.TLRPC.Chat) r2)
            r9.channel = r2
            r9.user_id = r8
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r2.<init>()
            r9.banned_rights = r2
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r9.banned_rights
            r2.view_messages = r1
            r2.send_media = r1
            r2.send_messages = r1
            r2.send_stickers = r1
            r2.send_gifs = r1
            r2.send_games = r1
            r2.send_inline = r1
            r2.embed_links = r1
            r2.pin_messages = r1
            r2.send_polls = r1
            r2.invite_users = r1
            r2.change_info = r1
            goto L_0x0077
        L_0x006a:
            org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser r9 = new org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser
            r9.<init>()
            r9.chat_id = r6
            org.telegram.tgnet.TLRPC$InputUser r1 = r5.getInputUser((org.telegram.tgnet.TLRPC.User) r7)
            r9.user_id = r1
        L_0x0077:
            int r7 = r7.id
            org.telegram.messenger.UserConfig r1 = r5.getUserConfig()
            int r1 = r1.getClientUserId()
            if (r7 != r1) goto L_0x0088
            int r7 = -r6
            long r1 = (long) r7
            r5.deleteDialog(r1, r0, r10)
        L_0x0088:
            org.telegram.tgnet.ConnectionsManager r7 = r5.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$P-liPLC_3IVRpqiHlrqdxeDAwTU r10 = new org.telegram.messenger.-$$Lambda$MessagesController$P-liPLC_3IVRpqiHlrqdxeDAwTU
            r10.<init>(r3, r8, r6)
            r6 = 64
            r7.sendRequest(r9, r10, r6)
            goto L_0x011b
        L_0x0098:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_chatFull
            if (r9 == 0) goto L_0x011b
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r6 = r5.getChat(r6)
            int r9 = r6.participants_count
            int r9 = r9 - r1
            r6.participants_count = r9
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r9.add(r6)
            org.telegram.messenger.MessagesStorage r6 = r5.getMessagesStorage()
            r10 = 0
            r6.putUsersAndChats(r10, r9, r1, r1)
            r6 = 0
        L_0x00ba:
            org.telegram.tgnet.TLRPC$ChatParticipants r9 = r8.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r9 = r9.participants
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x00e0
            org.telegram.tgnet.TLRPC$ChatParticipants r9 = r8.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r9 = r9.participants
            java.lang.Object r9 = r9.get(r6)
            org.telegram.tgnet.TLRPC$ChatParticipant r9 = (org.telegram.tgnet.TLRPC.ChatParticipant) r9
            int r9 = r9.user_id
            int r2 = r7.id
            if (r9 != r2) goto L_0x00dd
            org.telegram.tgnet.TLRPC$ChatParticipants r7 = r8.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r7 = r7.participants
            r7.remove(r6)
            r6 = 1
            goto L_0x00e1
        L_0x00dd:
            int r6 = r6 + 1
            goto L_0x00ba
        L_0x00e0:
            r6 = 0
        L_0x00e1:
            if (r6 == 0) goto L_0x0108
            org.telegram.messenger.MessagesStorage r6 = r5.getMessagesStorage()
            r6.updateChatInfo(r8, r1)
            org.telegram.messenger.NotificationCenter r6 = r5.getNotificationCenter()
            int r7 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r9 = 4
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r0] = r8
            java.lang.Integer r8 = java.lang.Integer.valueOf(r0)
            r9[r1] = r8
            r8 = 2
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r0)
            r9[r8] = r2
            r8 = 3
            r9[r8] = r10
            r6.postNotificationName(r7, r9)
        L_0x0108:
            org.telegram.messenger.NotificationCenter r6 = r5.getNotificationCenter()
            int r7 = org.telegram.messenger.NotificationCenter.updateInterfaces
            java.lang.Object[] r8 = new java.lang.Object[r1]
            r9 = 32
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r0] = r9
            r6.postNotificationName(r7, r8)
        L_0x011b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteUserFromChat(int, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$ChatFull, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$deleteUserFromChat$198$MessagesController(boolean z, TLRPC.InputUser inputUser, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            if (z && !(inputUser instanceof TLRPC.TL_inputUserSelf)) {
                AndroidUtilities.runOnUIThread(new Runnable(i) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$197$MessagesController(this.f$1);
                    }
                }, 1000);
            }
        }
    }

    public /* synthetic */ void lambda$null$197$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatTitle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_channels_editTitle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatTitle} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatTitle} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void changeChatTitle(int r4, java.lang.String r5) {
        /*
            r3 = this;
            if (r4 <= 0) goto L_0x0030
            int r0 = r3.currentAccount
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r4, r0)
            if (r0 == 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$TL_channels_editTitle r0 = new org.telegram.tgnet.TLRPC$TL_channels_editTitle
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r4 = r3.getInputChannel((int) r4)
            r0.channel = r4
            r0.title = r5
            goto L_0x0021
        L_0x0018:
            org.telegram.tgnet.TLRPC$TL_messages_editChatTitle r0 = new org.telegram.tgnet.TLRPC$TL_messages_editChatTitle
            r0.<init>()
            r0.chat_id = r4
            r0.title = r5
        L_0x0021:
            org.telegram.tgnet.ConnectionsManager r4 = r3.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$eeEoncUtImD3KY99RbahhTT8gY8 r5 = new org.telegram.messenger.-$$Lambda$MessagesController$eeEoncUtImD3KY99RbahhTT8gY8
            r5.<init>()
            r1 = 64
            r4.sendRequest(r0, r5, r1)
            goto L_0x006a
        L_0x0030:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r3.getChat(r4)
            r4.title = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r5.add(r4)
            org.telegram.messenger.MessagesStorage r4 = r3.getMessagesStorage()
            r0 = 0
            r1 = 1
            r4.putUsersAndChats(r0, r5, r1, r1)
            org.telegram.messenger.NotificationCenter r4 = r3.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r0 = 0
            java.lang.Object[] r2 = new java.lang.Object[r0]
            r4.postNotificationName(r5, r2)
            org.telegram.messenger.NotificationCenter r4 = r3.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.updateInterfaces
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 16
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1[r0] = r2
            r4.postNotificationName(r5, r1)
        L_0x006a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.changeChatTitle(int, java.lang.String):void");
    }

    public /* synthetic */ void lambda$changeChatTitle$199$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_channels_editPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void changeChatAvatar(int r2, org.telegram.tgnet.TLRPC.InputFile r3, org.telegram.tgnet.TLRPC.FileLocation r4, org.telegram.tgnet.TLRPC.FileLocation r5) {
        /*
            r1 = this;
            int r0 = r1.currentAccount
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r2, r0)
            if (r0 == 0) goto L_0x0029
            org.telegram.tgnet.TLRPC$TL_channels_editPhoto r0 = new org.telegram.tgnet.TLRPC$TL_channels_editPhoto
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r2 = r1.getInputChannel((int) r2)
            r0.channel = r2
            if (r3 == 0) goto L_0x0021
            org.telegram.tgnet.TLRPC$TL_inputChatUploadedPhoto r2 = new org.telegram.tgnet.TLRPC$TL_inputChatUploadedPhoto
            r2.<init>()
            r0.photo = r2
            org.telegram.tgnet.TLRPC$InputChatPhoto r2 = r0.photo
            r2.file = r3
            goto L_0x0045
        L_0x0021:
            org.telegram.tgnet.TLRPC$TL_inputChatPhotoEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputChatPhotoEmpty
            r2.<init>()
            r0.photo = r2
            goto L_0x0045
        L_0x0029:
            org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto r0 = new org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto
            r0.<init>()
            r0.chat_id = r2
            if (r3 == 0) goto L_0x003e
            org.telegram.tgnet.TLRPC$TL_inputChatUploadedPhoto r2 = new org.telegram.tgnet.TLRPC$TL_inputChatUploadedPhoto
            r2.<init>()
            r0.photo = r2
            org.telegram.tgnet.TLRPC$InputChatPhoto r2 = r0.photo
            r2.file = r3
            goto L_0x0045
        L_0x003e:
            org.telegram.tgnet.TLRPC$TL_inputChatPhotoEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputChatPhotoEmpty
            r2.<init>()
            r0.photo = r2
        L_0x0045:
            org.telegram.tgnet.ConnectionsManager r2 = r1.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$wy09bZPmqKXvxXv6-hJzlanOWGw r3 = new org.telegram.messenger.-$$Lambda$MessagesController$wy09bZPmqKXvxXv6-hJzlanOWGw
            r3.<init>(r4, r5)
            r4 = 64
            r2.sendRequest(r0, r3, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.changeChatAvatar(int, org.telegram.tgnet.TLRPC$InputFile, org.telegram.tgnet.TLRPC$FileLocation, org.telegram.tgnet.TLRPC$FileLocation):void");
    }

    public /* synthetic */ void lambda$changeChatAvatar$200$MessagesController(TLRPC.FileLocation fileLocation, TLRPC.FileLocation fileLocation2, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.Photo photo;
        if (tL_error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            int size = updates.updates.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    photo = null;
                    break;
                }
                TLRPC.Update update = updates.updates.get(i);
                if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                    TLRPC.MessageAction messageAction = ((TLRPC.TL_updateNewChannelMessage) update).message.action;
                    if (messageAction instanceof TLRPC.TL_messageActionChatEditPhoto) {
                        photo = messageAction.photo;
                        if (photo instanceof TLRPC.TL_photo) {
                            break;
                        }
                    } else {
                        continue;
                    }
                } else if (update instanceof TLRPC.TL_updateNewMessage) {
                    TLRPC.MessageAction messageAction2 = ((TLRPC.TL_updateNewMessage) update).message.action;
                    if (messageAction2 instanceof TLRPC.TL_messageActionChatEditPhoto) {
                        photo = messageAction2.photo;
                        if (photo instanceof TLRPC.TL_photo) {
                            break;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
                i++;
            }
            if (photo != null) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 150);
                if (!(closestPhotoSizeWithSize == null || fileLocation == null)) {
                    FileLoader.getPathToAttach(fileLocation, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize, true));
                    ImageLoader.getInstance().replaceImageInCache(fileLocation.volume_id + "_" + fileLocation.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForPhoto(closestPhotoSizeWithSize, photo), true);
                }
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                if (!(closestPhotoSizeWithSize2 == null || fileLocation2 == null)) {
                    FileLoader.getPathToAttach(fileLocation2, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize2, true));
                }
            }
            processUpdates(updates, false);
        }
    }

    public void unregistedPush() {
        if (getUserConfig().registeredForPush && SharedConfig.pushString.length() == 0) {
            TLRPC.TL_account_unregisterDevice tL_account_unregisterDevice = new TLRPC.TL_account_unregisterDevice();
            tL_account_unregisterDevice.token = SharedConfig.pushString;
            tL_account_unregisterDevice.token_type = 2;
            for (int i = 0; i < 3; i++) {
                UserConfig instance = UserConfig.getInstance(i);
                if (i != this.currentAccount && instance.isClientActivated()) {
                    tL_account_unregisterDevice.other_uids.add(Integer.valueOf(instance.getClientUserId()));
                }
            }
            getConnectionsManager().sendRequest(tL_account_unregisterDevice, $$Lambda$MessagesController$Qu0ArHMVMrsMl5O52igKgpwq98.INSTANCE);
        }
    }

    public void performLogout(int i) {
        boolean z = true;
        if (i == 1) {
            unregistedPush();
            getConnectionsManager().sendRequest(new TLRPC.TL_auth_logOut(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$performLogout$202$MessagesController(tLObject, tL_error);
                }
            });
        } else {
            ConnectionsManager connectionsManager = getConnectionsManager();
            if (i != 2) {
                z = false;
            }
            connectionsManager.cleanup(z);
        }
        getUserConfig().clearConfig();
        getNotificationCenter().postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        getMessagesStorage().cleanup(false);
        cleanup();
        getContactsController().deleteUnknownAppAccounts();
    }

    public /* synthetic */ void lambda$performLogout$202$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        getConnectionsManager().cleanup(false);
    }

    public void generateUpdateMessage() {
        String str;
        if (!this.gettingAppChangelog && !BuildVars.DEBUG_VERSION && (str = SharedConfig.lastUpdateVersion) != null && !str.equals(BuildVars.BUILD_VERSION_STRING)) {
            this.gettingAppChangelog = true;
            TLRPC.TL_help_getAppChangelog tL_help_getAppChangelog = new TLRPC.TL_help_getAppChangelog();
            tL_help_getAppChangelog.prev_app_version = SharedConfig.lastUpdateVersion;
            getConnectionsManager().sendRequest(tL_help_getAppChangelog, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$generateUpdateMessage$203$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$generateUpdateMessage$203$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
            SharedConfig.saveConfig();
        }
        if (tLObject instanceof TLRPC.Updates) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void registerForPush(String str) {
        if (!TextUtils.isEmpty(str) && !this.registeringForPush && getUserConfig().getClientUserId() != 0) {
            if (!getUserConfig().registeredForPush || !str.equals(SharedConfig.pushString)) {
                this.registeringForPush = true;
                this.lastPushRegisterSendTime = SystemClock.elapsedRealtime();
                if (SharedConfig.pushAuthKey == null) {
                    SharedConfig.pushAuthKey = new byte[256];
                    Utilities.random.nextBytes(SharedConfig.pushAuthKey);
                    SharedConfig.saveConfig();
                }
                TLRPC.TL_account_registerDevice tL_account_registerDevice = new TLRPC.TL_account_registerDevice();
                tL_account_registerDevice.token_type = 2;
                tL_account_registerDevice.token = str;
                tL_account_registerDevice.no_muted = false;
                tL_account_registerDevice.secret = SharedConfig.pushAuthKey;
                for (int i = 0; i < 3; i++) {
                    UserConfig instance = UserConfig.getInstance(i);
                    if (i != this.currentAccount && instance.isClientActivated()) {
                        int clientUserId = instance.getClientUserId();
                        tL_account_registerDevice.other_uids.add(Integer.valueOf(clientUserId));
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("add other uid = " + clientUserId + " for account " + this.currentAccount);
                        }
                    }
                }
                getConnectionsManager().sendRequest(tL_account_registerDevice, new RequestDelegate(str) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$registerForPush$205$MessagesController(this.f$1, tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$registerForPush$205$MessagesController(String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("account " + this.currentAccount + " registered for push");
            }
            getUserConfig().registeredForPush = true;
            SharedConfig.pushString = str;
            getUserConfig().saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MessagesController.this.lambda$null$204$MessagesController();
            }
        });
    }

    public /* synthetic */ void lambda$null$204$MessagesController() {
        this.registeringForPush = false;
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_updates_getState(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadCurrentState$206$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadCurrentState$206$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.updatingState = false;
        if (tL_error == null) {
            TLRPC.TL_updates_state tL_updates_state = (TLRPC.TL_updates_state) tLObject;
            getMessagesStorage().setLastDateValue(tL_updates_state.date);
            getMessagesStorage().setLastPtsValue(tL_updates_state.pts);
            getMessagesStorage().setLastSeqValue(tL_updates_state.seq);
            getMessagesStorage().setLastQtsValue(tL_updates_state.qts);
            for (int i = 0; i < 3; i++) {
                processUpdatesQueue(i, 2);
            }
            getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        } else if (tL_error.code != 401) {
            loadCurrentState();
        }
    }

    private int getUpdateSeq(TLRPC.Updates updates) {
        if (updates instanceof TLRPC.TL_updatesCombined) {
            return updates.seq_start;
        }
        return updates.seq;
    }

    private void setUpdatesStartTime(int i, long j) {
        if (i == 0) {
            this.updatesStartWaitTimeSeq = j;
        } else if (i == 1) {
            this.updatesStartWaitTimePts = j;
        } else if (i == 2) {
            this.updatesStartWaitTimeQts = j;
        }
    }

    public long getUpdatesStartTime(int i) {
        if (i == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (i == 1) {
            return this.updatesStartWaitTimePts;
        }
        if (i == 2) {
            return this.updatesStartWaitTimeQts;
        }
        return 0;
    }

    private int isValidUpdate(TLRPC.Updates updates, int i) {
        if (i == 0) {
            int updateSeq = getUpdateSeq(updates);
            if (getMessagesStorage().getLastSeqValue() + 1 == updateSeq || getMessagesStorage().getLastSeqValue() == updateSeq) {
                return 0;
            }
            return getMessagesStorage().getLastSeqValue() < updateSeq ? 1 : 2;
        } else if (i == 1) {
            if (updates.pts <= getMessagesStorage().getLastPtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastPtsValue() + updates.pts_count == updates.pts ? 0 : 1;
        } else if (i != 2) {
            return 0;
        } else {
            if (updates.pts <= getMessagesStorage().getLastQtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastQtsValue() + updates.updates.size() == updates.pts ? 0 : 1;
        }
    }

    private void processChannelsUpdatesQueue(int i, int i2) {
        char c;
        ArrayList arrayList = this.updatesQueueChannels.get(i);
        if (arrayList != null) {
            int i3 = this.channelsPts.get(i);
            if (arrayList.isEmpty() || i3 == 0) {
                this.updatesQueueChannels.remove(i);
                return;
            }
            Collections.sort(arrayList, $$Lambda$MessagesController$iWcS0cq_95jswayvvTaTSKnQLGc.INSTANCE);
            if (i2 == 2) {
                this.channelsPts.put(i, ((TLRPC.Updates) arrayList.get(0)).pts);
            }
            boolean z = false;
            while (arrayList.size() > 0) {
                TLRPC.Updates updates = (TLRPC.Updates) arrayList.get(0);
                int i4 = updates.pts;
                if (i4 <= i3) {
                    c = 2;
                } else {
                    c = updates.pts_count + i3 == i4 ? (char) 0 : 1;
                }
                if (c == 0) {
                    processUpdates(updates, true);
                    arrayList.remove(0);
                    z = true;
                } else if (c == 1) {
                    long j = this.updatesStartWaitTimeChannels.get(i);
                    if (j == 0 || (!z && Math.abs(System.currentTimeMillis() - j) > 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN CHANNEL " + i + " UPDATES QUEUE - getChannelDifference ");
                        }
                        this.updatesStartWaitTimeChannels.delete(i);
                        this.updatesQueueChannels.remove(i);
                        getChannelDifference(i);
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN CHANNEL " + i + " UPDATES QUEUE - will wait more time");
                    }
                    if (z) {
                        this.updatesStartWaitTimeChannels.put(i, System.currentTimeMillis());
                        return;
                    }
                    return;
                } else {
                    arrayList.remove(0);
                }
            }
            this.updatesQueueChannels.remove(i);
            this.updatesStartWaitTimeChannels.delete(i);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES CHANNEL " + i + " QUEUE PROCEED - OK");
            }
        }
    }

    private void processUpdatesQueue(int i, int i2) {
        ArrayList<TLRPC.Updates> arrayList;
        if (i == 0) {
            arrayList = this.updatesQueueSeq;
            Collections.sort(arrayList, new Comparator() {
                public final int compare(Object obj, Object obj2) {
                    return MessagesController.this.lambda$processUpdatesQueue$208$MessagesController((TLRPC.Updates) obj, (TLRPC.Updates) obj2);
                }
            });
        } else if (i == 1) {
            arrayList = this.updatesQueuePts;
            Collections.sort(arrayList, $$Lambda$MessagesController$hGUHFGlJkXICR41QitU5GeMfKbg.INSTANCE);
        } else if (i == 2) {
            arrayList = this.updatesQueueQts;
            Collections.sort(arrayList, $$Lambda$MessagesController$r6viuXq_dpBF5FJFu8L7BQA0K6U.INSTANCE);
        } else {
            arrayList = null;
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            if (i2 == 2) {
                TLRPC.Updates updates = arrayList.get(0);
                if (i == 0) {
                    getMessagesStorage().setLastSeqValue(getUpdateSeq(updates));
                } else if (i == 1) {
                    getMessagesStorage().setLastPtsValue(updates.pts);
                } else {
                    getMessagesStorage().setLastQtsValue(updates.pts);
                }
            }
            boolean z = false;
            while (arrayList.size() > 0) {
                TLRPC.Updates updates2 = arrayList.get(0);
                int isValidUpdate = isValidUpdate(updates2, i);
                if (isValidUpdate == 0) {
                    processUpdates(updates2, true);
                    arrayList.remove(0);
                    z = true;
                } else if (isValidUpdate != 1) {
                    arrayList.remove(0);
                } else if (getUpdatesStartTime(i) == 0 || (!z && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(i)) > 1500)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN UPDATES QUEUE - getDifference");
                    }
                    setUpdatesStartTime(i, 0);
                    arrayList.clear();
                    getDifference();
                    return;
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN UPDATES QUEUE - will wait more time");
                    }
                    if (z) {
                        setUpdatesStartTime(i, System.currentTimeMillis());
                        return;
                    }
                    return;
                }
            }
            arrayList.clear();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES QUEUE PROCEED - OK");
            }
        }
        setUpdatesStartTime(i, 0);
    }

    public /* synthetic */ int lambda$processUpdatesQueue$208$MessagesController(TLRPC.Updates updates, TLRPC.Updates updates2) {
        return AndroidUtilities.compare(getUpdateSeq(updates), getUpdateSeq(updates2));
    }

    /* access modifiers changed from: protected */
    public void loadUnknownChannel(TLRPC.Chat chat, long j) {
        if ((chat instanceof TLRPC.TL_channel) && this.gettingUnknownChannels.indexOfKey(chat.id) < 0) {
            if (chat.access_hash != 0) {
                TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                int i = chat.id;
                tL_inputPeerChannel.channel_id = i;
                tL_inputPeerChannel.access_hash = chat.access_hash;
                this.gettingUnknownChannels.put(i, true);
                TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
                TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                tL_inputDialogPeer.peer = tL_inputPeerChannel;
                tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                if (j == 0) {
                    NativeByteBuffer nativeByteBuffer = null;
                    try {
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(chat.getObjectSize() + 4);
                        try {
                            nativeByteBuffer2.writeInt32(0);
                            chat.serializeToStream(nativeByteBuffer2);
                            nativeByteBuffer = nativeByteBuffer2;
                        } catch (Exception e) {
                            NativeByteBuffer nativeByteBuffer3 = nativeByteBuffer2;
                            e = e;
                            nativeByteBuffer = nativeByteBuffer3;
                            FileLog.e((Throwable) e);
                            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                            getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate(j, chat) {
                                private final /* synthetic */ long f$1;
                                private final /* synthetic */ TLRPC.Chat f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r4;
                                }

                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    MessagesController.this.lambda$loadUnknownChannel$211$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                                }
                            });
                        }
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e((Throwable) e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate(j, chat) {
                            private final /* synthetic */ long f$1;
                            private final /* synthetic */ TLRPC.Chat f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r4;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$loadUnknownChannel$211$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        });
                    }
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                }
                getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate(j, chat) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ TLRPC.Chat f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$loadUnknownChannel$211$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (j != 0) {
                getMessagesStorage().removePendingTask(j);
            }
        }
    }

    public /* synthetic */ void lambda$loadUnknownChannel$211$MessagesController(long j, TLRPC.Chat chat, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty() && !tL_messages_peerDialogs.chats.isEmpty()) {
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                processLoadedDialogs(tL_messages_dialogs, (ArrayList<TLRPC.EncryptedChat>) null, ((TLRPC.TL_dialog) tL_messages_peerDialogs.dialogs.get(0)).folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_CHANNEL, false, false, false);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        this.gettingUnknownChannels.delete(chat.id);
    }

    public void startShortPoll(TLRPC.Chat chat, boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable(z, chat) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ TLRPC.Chat f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$startShortPoll$212$MessagesController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$startShortPoll$212$MessagesController(boolean z, TLRPC.Chat chat) {
        if (z) {
            this.needShortPollChannels.delete(chat.id);
            if (chat.megagroup) {
                this.needShortPollOnlines.delete(chat.id);
                return;
            }
            return;
        }
        this.needShortPollChannels.put(chat.id, 0);
        if (this.shortPollChannels.indexOfKey(chat.id) < 0) {
            getChannelDifference(chat.id, 3, 0, (TLRPC.InputChannel) null);
        }
        if (chat.megagroup) {
            this.needShortPollOnlines.put(chat.id, 0);
            if (this.shortPollOnlines.indexOfKey(chat.id) < 0) {
                this.shortPollOnlines.put(chat.id, 0);
            }
        }
    }

    private void getChannelDifference(int i) {
        getChannelDifference(i, 0, 0, (TLRPC.InputChannel) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r1 = r1.id;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isSupportUser(org.telegram.tgnet.TLRPC.User r1) {
        /*
            if (r1 == 0) goto L_0x009b
            boolean r0 = r1.support
            if (r0 != 0) goto L_0x0099
            int r1 = r1.id
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 333000(0x514c8, float:4.66632E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 4240000(0x40b280, float:5.941505E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 4244000(0x40CLASSNAME, float:5.94711E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 4245000(0x40CLASSNAME, float:5.948512E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 4246000(0x40c9f0, float:5.949913E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 410000(0x64190, float:5.74532E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 420000(0x668a0, float:5.88545E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 431000(0x69398, float:6.0396E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 431415000(0x19b6ded8, float:1.8908365E-23)
            if (r1 == r0) goto L_0x0099
            r0 = 434000(0x69var_, float:6.08164E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 4243000(0x40be38, float:5.94571E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 439000(0x6b2d8, float:6.1517E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 449000(0x6d9e8, float:6.29183E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 450000(0x6ddd0, float:6.30584E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 452000(0x6e5a0, float:6.33387E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 454000(0x6ed70, float:6.3619E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 4254000(0x40e930, float:5.961124E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 455000(0x6var_, float:6.37591E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 460000(0x704e0, float:6.44597E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 470000(0x72bf0, float:6.5861E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 479000(0x74var_, float:6.71222E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 796000(0xCLASSNAME, float:1.115434E-39)
            if (r1 == r0) goto L_0x0099
            r0 = 482000(0x75ad0, float:6.75426E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 490000(0x77a10, float:6.86636E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 496000(0x79180, float:6.95044E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 497000(0x79568, float:6.96445E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 498000(0x79950, float:6.97847E-40)
            if (r1 == r0) goto L_0x0099
            r0 = 4298000(0x419510, float:6.022781E-39)
            if (r1 != r0) goto L_0x009b
        L_0x0099:
            r1 = 1
            goto L_0x009c
        L_0x009b:
            r1 = 0
        L_0x009c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.isSupportUser(org.telegram.tgnet.TLRPC$User):boolean");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void getChannelDifference(int r16, int r17, long r18, org.telegram.tgnet.TLRPC.InputChannel r20) {
        /*
            r15 = this;
            r7 = r15
            r3 = r16
            r4 = r17
            r0 = r18
            android.util.SparseBooleanArray r2 = r7.gettingDifferenceChannels
            boolean r2 = r2.get(r3)
            if (r2 == 0) goto L_0x0010
            return
        L_0x0010:
            r2 = 100
            r5 = 3
            r6 = 1
            if (r4 != r6) goto L_0x0022
            android.util.SparseIntArray r2 = r7.channelsPts
            int r2 = r2.get(r3)
            if (r2 == 0) goto L_0x001f
            return
        L_0x001f:
            r2 = 1
            r8 = 1
            goto L_0x0044
        L_0x0022:
            android.util.SparseIntArray r8 = r7.channelsPts
            int r8 = r8.get(r3)
            if (r8 != 0) goto L_0x0041
            org.telegram.messenger.MessagesStorage r8 = r15.getMessagesStorage()
            int r8 = r8.getChannelPtsSync(r3)
            if (r8 == 0) goto L_0x0039
            android.util.SparseIntArray r9 = r7.channelsPts
            r9.put(r3, r8)
        L_0x0039:
            if (r8 != 0) goto L_0x0041
            r9 = 2
            if (r4 == r9) goto L_0x0040
            if (r4 != r5) goto L_0x0041
        L_0x0040:
            return
        L_0x0041:
            if (r8 != 0) goto L_0x0044
            return
        L_0x0044:
            if (r20 != 0) goto L_0x0062
            java.lang.Integer r9 = java.lang.Integer.valueOf(r16)
            org.telegram.tgnet.TLRPC$Chat r9 = r15.getChat(r9)
            if (r9 != 0) goto L_0x005d
            org.telegram.messenger.MessagesStorage r9 = r15.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChatSync(r3)
            if (r9 == 0) goto L_0x005d
            r15.putChat(r9, r6)
        L_0x005d:
            org.telegram.tgnet.TLRPC$InputChannel r9 = getInputChannel((org.telegram.tgnet.TLRPC.Chat) r9)
            goto L_0x0064
        L_0x0062:
            r9 = r20
        L_0x0064:
            r10 = 0
            if (r9 == 0) goto L_0x00ef
            long r12 = r9.access_hash
            int r14 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r14 != 0) goto L_0x0070
            goto L_0x00ef
        L_0x0070:
            int r12 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x009d
            r1 = 0
            org.telegram.tgnet.NativeByteBuffer r10 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0090 }
            int r0 = r9.getObjectSize()     // Catch:{ Exception -> 0x0090 }
            int r0 = r0 + 12
            r10.<init>((int) r0)     // Catch:{ Exception -> 0x0090 }
            r0 = 6
            r10.writeInt32(r0)     // Catch:{ Exception -> 0x008e }
            r10.writeInt32(r3)     // Catch:{ Exception -> 0x008e }
            r10.writeInt32(r4)     // Catch:{ Exception -> 0x008e }
            r9.serializeToStream(r10)     // Catch:{ Exception -> 0x008e }
            goto L_0x0095
        L_0x008e:
            r0 = move-exception
            goto L_0x0092
        L_0x0090:
            r0 = move-exception
            r10 = r1
        L_0x0092:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0095:
            org.telegram.messenger.MessagesStorage r0 = r15.getMessagesStorage()
            long r0 = r0.createPendingTask(r10)
        L_0x009d:
            r10 = r0
            android.util.SparseBooleanArray r0 = r7.gettingDifferenceChannels
            r0.put(r3, r6)
            org.telegram.tgnet.TLRPC$TL_updates_getChannelDifference r0 = new org.telegram.tgnet.TLRPC$TL_updates_getChannelDifference
            r0.<init>()
            r0.channel = r9
            org.telegram.tgnet.TLRPC$TL_channelMessagesFilterEmpty r1 = new org.telegram.tgnet.TLRPC$TL_channelMessagesFilterEmpty
            r1.<init>()
            r0.filter = r1
            r0.pts = r8
            r0.limit = r2
            if (r4 == r5) goto L_0x00b8
            goto L_0x00b9
        L_0x00b8:
            r6 = 0
        L_0x00b9:
            r0.force = r6
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x00db
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "start getChannelDifference with pts = "
            r1.append(r2)
            r1.append(r8)
            java.lang.String r2 = " channelId = "
            r1.append(r2)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
        L_0x00db:
            org.telegram.tgnet.ConnectionsManager r8 = r15.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$x5i0ne0KceS-W4h5GT2Mo-qkysQ r9 = new org.telegram.messenger.-$$Lambda$MessagesController$x5i0ne0KceS-W4h5GT2Mo-qkysQ
            r1 = r9
            r2 = r15
            r3 = r16
            r4 = r17
            r5 = r10
            r1.<init>(r3, r4, r5)
            r8.sendRequest(r0, r9)
            return
        L_0x00ef:
            int r2 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r2 == 0) goto L_0x00fa
            org.telegram.messenger.MessagesStorage r2 = r15.getMessagesStorage()
            r2.removePendingTask(r0)
        L_0x00fa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.getChannelDifference(int, int, long, org.telegram.tgnet.TLRPC$InputChannel):void");
    }

    public /* synthetic */ void lambda$getChannelDifference$221$MessagesController(int i, int i2, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.Chat chat;
        int i3 = i;
        long j2 = j;
        TLRPC.TL_error tL_error2 = tL_error;
        if (tLObject != null) {
            TLRPC.updates_ChannelDifference updates_channeldifference = (TLRPC.updates_ChannelDifference) tLObject;
            SparseArray sparseArray = new SparseArray();
            int i4 = 0;
            for (int i5 = 0; i5 < updates_channeldifference.users.size(); i5++) {
                TLRPC.User user = updates_channeldifference.users.get(i5);
                sparseArray.put(user.id, user);
            }
            int i6 = 0;
            while (true) {
                if (i6 >= updates_channeldifference.chats.size()) {
                    chat = null;
                    break;
                }
                chat = updates_channeldifference.chats.get(i6);
                if (chat.id == i3) {
                    break;
                }
                i6++;
            }
            ArrayList arrayList = new ArrayList();
            if (!updates_channeldifference.other_updates.isEmpty()) {
                while (i4 < updates_channeldifference.other_updates.size()) {
                    TLRPC.Update update = updates_channeldifference.other_updates.get(i4);
                    if (update instanceof TLRPC.TL_updateMessageID) {
                        arrayList.add((TLRPC.TL_updateMessageID) update);
                        updates_channeldifference.other_updates.remove(i4);
                        i4--;
                    }
                    i4++;
                }
            }
            getMessagesStorage().putUsersAndChats(updates_channeldifference.users, updates_channeldifference.chats, true, true);
            AndroidUtilities.runOnUIThread(new Runnable(updates_channeldifference) {
                private final /* synthetic */ TLRPC.updates_ChannelDifference f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$213$MessagesController(this.f$1);
                }
            });
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, i, updates_channeldifference, chat, sparseArray, i2, j) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ TLRPC.updates_ChannelDifference f$3;
                private final /* synthetic */ TLRPC.Chat f$4;
                private final /* synthetic */ SparseArray f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ long f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run() {
                    MessagesController.this.lambda$null$219$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        } else if (tL_error2 != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error2, i) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MessagesController.this.lambda$null$220$MessagesController(this.f$1, this.f$2);
                }
            });
            this.gettingDifferenceChannels.delete(i);
            if (j2 != 0) {
                getMessagesStorage().removePendingTask(j2);
            }
        }
    }

    public /* synthetic */ void lambda$null$213$MessagesController(TLRPC.updates_ChannelDifference updates_channeldifference) {
        putUsers(updates_channeldifference.users, false);
        putChats(updates_channeldifference.chats, false);
    }

    public /* synthetic */ void lambda$null$219$MessagesController(ArrayList arrayList, int i, TLRPC.updates_ChannelDifference updates_channeldifference, TLRPC.Chat chat, SparseArray sparseArray, int i2, long j) {
        if (!arrayList.isEmpty()) {
            SparseArray sparseArray2 = new SparseArray();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                TLRPC.TL_updateMessageID tL_updateMessageID = (TLRPC.TL_updateMessageID) it.next();
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tL_updateMessageID.random_id, (Integer) null, tL_updateMessageID.id, 0, false, i, -1);
                if (updateMessageStateAndId != null) {
                    sparseArray2.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray2.size() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable(sparseArray2) {
                    private final /* synthetic */ SparseArray f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$214$MessagesController(this.f$1);
                    }
                });
                Utilities.stageQueue.postRunnable(new Runnable(updates_channeldifference, i, chat, sparseArray, i2, j) {
                    private final /* synthetic */ TLRPC.updates_ChannelDifference f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ TLRPC.Chat f$3;
                    private final /* synthetic */ SparseArray f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ long f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$218$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable(updates_channeldifference, i, chat, sparseArray, i2, j) {
            private final /* synthetic */ TLRPC.updates_ChannelDifference f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ TLRPC.Chat f$3;
            private final /* synthetic */ SparseArray f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ long f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MessagesController.this.lambda$null$218$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$214$MessagesController(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            int i2 = (int) jArr[1];
            getSendMessagesHelper().processSentMessage(i2);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i2), Integer.valueOf(keyAt), null, Long.valueOf(jArr[0]), 0L, -1, false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x018f A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x014b  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0187  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$218$MessagesController(org.telegram.tgnet.TLRPC.updates_ChannelDifference r17, int r18, org.telegram.tgnet.TLRPC.Chat r19, android.util.SparseArray r20, int r21, long r22) {
        /*
            r16 = this;
            r6 = r16
            r7 = r17
            r8 = r18
            r0 = r19
            r9 = r22
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifference
            r2 = -2147483648(0xfffffffvar_, float:-0.0)
            r3 = 0
            r11 = 1
            if (r1 != 0) goto L_0x00b4
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty
            if (r1 == 0) goto L_0x0018
            goto L_0x00b4
        L_0x0018:
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong
            if (r1 == 0) goto L_0x01d7
            int r1 = -r8
            long r4 = (long) r1
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r6.dialogs_read_inbox_max
            java.lang.Long r12 = java.lang.Long.valueOf(r4)
            java.lang.Object r1 = r1.get(r12)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x0041
            org.telegram.messenger.MessagesStorage r1 = r16.getMessagesStorage()
            int r1 = r1.getDialogReadMax(r3, r4)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r12 = r6.dialogs_read_inbox_max
            java.lang.Long r13 = java.lang.Long.valueOf(r4)
            r12.put(r13, r1)
        L_0x0041:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r12 = r6.dialogs_read_outbox_max
            java.lang.Long r13 = java.lang.Long.valueOf(r4)
            java.lang.Object r12 = r12.get(r13)
            java.lang.Integer r12 = (java.lang.Integer) r12
            if (r12 != 0) goto L_0x0064
            org.telegram.messenger.MessagesStorage r12 = r16.getMessagesStorage()
            int r12 = r12.getDialogReadMax(r11, r4)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r13 = r6.dialogs_read_outbox_max
            java.lang.Long r14 = java.lang.Long.valueOf(r4)
            r13.put(r14, r12)
        L_0x0064:
            r13 = 0
        L_0x0065:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r14 = r7.messages
            int r14 = r14.size()
            if (r13 >= r14) goto L_0x00a6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r14 = r7.messages
            java.lang.Object r14 = r14.get(r13)
            org.telegram.tgnet.TLRPC$Message r14 = (org.telegram.tgnet.TLRPC.Message) r14
            r14.dialog_id = r4
            org.telegram.tgnet.TLRPC$MessageAction r15 = r14.action
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r15 != 0) goto L_0x0094
            if (r0 == 0) goto L_0x0083
            boolean r15 = r0.left
            if (r15 != 0) goto L_0x0094
        L_0x0083:
            boolean r15 = r14.out
            if (r15 == 0) goto L_0x0089
            r15 = r12
            goto L_0x008a
        L_0x0089:
            r15 = r1
        L_0x008a:
            int r15 = r15.intValue()
            int r11 = r14.id
            if (r15 >= r11) goto L_0x0094
            r11 = 1
            goto L_0x0095
        L_0x0094:
            r11 = 0
        L_0x0095:
            r14.unread = r11
            if (r0 == 0) goto L_0x00a2
            boolean r11 = r0.megagroup
            if (r11 == 0) goto L_0x00a2
            int r11 = r14.flags
            r11 = r11 | r2
            r14.flags = r11
        L_0x00a2:
            int r13 = r13 + 1
            r11 = 1
            goto L_0x0065
        L_0x00a6:
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            r1 = r7
            org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong r1 = (org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong) r1
            r2 = r21
            r0.overwriteChannel(r8, r1, r2)
            goto L_0x01d7
        L_0x00b4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r7.new_messages
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x01b5
            android.util.LongSparseArray r1 = new android.util.LongSparseArray
            r1.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r7.new_messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r4)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            int r5 = -r8
            long r11 = (long) r5
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r5 = r6.dialogs_read_inbox_max
            java.lang.Long r13 = java.lang.Long.valueOf(r11)
            java.lang.Object r5 = r5.get(r13)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 != 0) goto L_0x00f0
            org.telegram.messenger.MessagesStorage r5 = r16.getMessagesStorage()
            int r5 = r5.getDialogReadMax(r3, r11)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r13 = r6.dialogs_read_inbox_max
            java.lang.Long r14 = java.lang.Long.valueOf(r11)
            r13.put(r14, r5)
        L_0x00f0:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r13 = r6.dialogs_read_outbox_max
            java.lang.Long r14 = java.lang.Long.valueOf(r11)
            java.lang.Object r13 = r13.get(r14)
            java.lang.Integer r13 = (java.lang.Integer) r13
            if (r13 != 0) goto L_0x0114
            org.telegram.messenger.MessagesStorage r13 = r16.getMessagesStorage()
            r14 = 1
            int r13 = r13.getDialogReadMax(r14, r11)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r14 = r6.dialogs_read_outbox_max
            java.lang.Long r15 = java.lang.Long.valueOf(r11)
            r14.put(r15, r13)
        L_0x0114:
            r14 = 0
        L_0x0115:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r7.new_messages
            int r15 = r15.size()
            if (r14 >= r15) goto L_0x019d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r7.new_messages
            java.lang.Object r15 = r15.get(r14)
            org.telegram.tgnet.TLRPC$Message r15 = (org.telegram.tgnet.TLRPC.Message) r15
            if (r0 == 0) goto L_0x012b
            boolean r3 = r0.left
            if (r3 != 0) goto L_0x0142
        L_0x012b:
            boolean r3 = r15.out
            if (r3 == 0) goto L_0x0131
            r3 = r13
            goto L_0x0132
        L_0x0131:
            r3 = r5
        L_0x0132:
            int r3 = r3.intValue()
            int r2 = r15.id
            if (r3 >= r2) goto L_0x0142
            org.telegram.tgnet.TLRPC$MessageAction r2 = r15.action
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r2 != 0) goto L_0x0142
            r2 = 1
            goto L_0x0143
        L_0x0142:
            r2 = 0
        L_0x0143:
            r15.unread = r2
            if (r0 == 0) goto L_0x0153
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x0153
            int r2 = r15.flags
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = r2 | r3
            r15.flags = r2
            goto L_0x0155
        L_0x0153:
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
        L_0x0155:
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject
            int r3 = r6.currentAccount
            java.util.ArrayList<java.lang.Long> r0 = r6.createdDialogIds
            r21 = r5
            java.lang.Long r5 = java.lang.Long.valueOf(r11)
            boolean r0 = r0.contains(r5)
            r5 = r20
            r2.<init>((int) r3, (org.telegram.tgnet.TLRPC.Message) r15, (android.util.SparseArray<org.telegram.tgnet.TLRPC.User>) r5, (boolean) r0)
            boolean r0 = r2.isOut()
            if (r0 == 0) goto L_0x0176
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            boolean r0 = r0.from_scheduled
            if (r0 == 0) goto L_0x017f
        L_0x0176:
            boolean r0 = r2.isUnread()
            if (r0 == 0) goto L_0x017f
            r4.add(r2)
        L_0x017f:
            java.lang.Object r0 = r1.get(r11)
            java.util.ArrayList r0 = (java.util.ArrayList) r0
            if (r0 != 0) goto L_0x018f
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.put(r11, r0)
        L_0x018f:
            r0.add(r2)
            int r14 = r14 + 1
            r0 = r19
            r5 = r21
            r2 = -2147483648(0xfffffffvar_, float:-0.0)
            r3 = 0
            goto L_0x0115
        L_0x019d:
            org.telegram.messenger.-$$Lambda$MessagesController$l0dMSZgmN0gECx0z-m67_Y-by64 r0 = new org.telegram.messenger.-$$Lambda$MessagesController$l0dMSZgmN0gECx0z-m67_Y-by64
            r0.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r0 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$cgs0OFAJnqEUItiFpqM9coCMvSw r1 = new org.telegram.messenger.-$$Lambda$MessagesController$cgs0OFAJnqEUItiFpqM9coCMvSw
            r1.<init>(r4, r7)
            r0.postRunnable(r1)
        L_0x01b5:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r7.other_updates
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01ca
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.other_updates
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r7.chats
            r4 = 1
            r5 = 0
            r0 = r16
            r0.processUpdateArray(r1, r2, r3, r4, r5)
        L_0x01ca:
            r0 = 1
            r6.processChannelsUpdatesQueue(r8, r0)
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            int r1 = r7.pts
            r0.saveChannelPts(r8, r1)
        L_0x01d7:
            android.util.SparseBooleanArray r0 = r6.gettingDifferenceChannels
            r0.delete(r8)
            android.util.SparseIntArray r0 = r6.channelsPts
            int r1 = r7.pts
            r0.put(r8, r1)
            int r0 = r7.flags
            r0 = r0 & 2
            if (r0 == 0) goto L_0x01f9
            android.util.SparseIntArray r0 = r6.shortPollChannels
            long r1 = java.lang.System.currentTimeMillis()
            r3 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r3
            int r2 = (int) r1
            int r1 = r7.timeout
            int r2 = r2 + r1
            r0.put(r8, r2)
        L_0x01f9:
            boolean r0 = r7.isFinal
            if (r0 != 0) goto L_0x0200
            r6.getChannelDifference(r8)
        L_0x0200:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0274
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "received channel difference with pts = "
            r0.append(r1)
            int r1 = r7.pts
            r0.append(r1)
            java.lang.String r1 = " channelId = "
            r0.append(r1)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "new_messages = "
            r0.append(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r7.new_messages
            int r1 = r1.size()
            r0.append(r1)
            java.lang.String r1 = " messages = "
            r0.append(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r7.messages
            int r1 = r1.size()
            r0.append(r1)
            java.lang.String r1 = " users = "
            r0.append(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r7.users
            int r1 = r1.size()
            r0.append(r1)
            java.lang.String r1 = " chats = "
            r0.append(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r7.chats
            int r1 = r1.size()
            r0.append(r1)
            java.lang.String r1 = " other updates = "
            r0.append(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.other_updates
            int r1 = r1.size()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0274:
            r0 = 0
            int r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r2 == 0) goto L_0x0281
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            r0.removePendingTask(r9)
        L_0x0281:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$null$218$MessagesController(org.telegram.tgnet.TLRPC$updates_ChannelDifference, int, org.telegram.tgnet.TLRPC$Chat, android.util.SparseArray, int, long):void");
    }

    public /* synthetic */ void lambda$null$215$MessagesController(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i), false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$217$MessagesController(ArrayList arrayList, TLRPC.updates_ChannelDifference updates_channeldifference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$216$MessagesController(this.f$1);
                }
            });
        }
        getMessagesStorage().putMessages(updates_channeldifference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false);
    }

    public /* synthetic */ void lambda$null$216$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, (CountDownLatch) null);
    }

    public /* synthetic */ void lambda$null$220$MessagesController(TLRPC.TL_error tL_error, int i) {
        checkChannelError(tL_error.text, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkChannelError(java.lang.String r6, int r7) {
        /*
            r5 = this;
            int r0 = r6.hashCode()
            r1 = -1809401834(0xfffffffvar_b816, float:-8.417163E-27)
            r2 = 0
            r3 = 2
            r4 = 1
            if (r0 == r1) goto L_0x002b
            r1 = -795226617(0xffffffffd099ce07, float:-2.064333E10)
            if (r0 == r1) goto L_0x0021
            r1 = -471086771(0xffffffffe3ebCLASSNAMEd, float:-8.69898E21)
            if (r0 == r1) goto L_0x0017
            goto L_0x0035
        L_0x0017:
            java.lang.String r0 = "CHANNEL_PUBLIC_GROUP_NA"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0035
            r6 = 1
            goto L_0x0036
        L_0x0021:
            java.lang.String r0 = "CHANNEL_PRIVATE"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0035
            r6 = 0
            goto L_0x0036
        L_0x002b:
            java.lang.String r0 = "USER_BANNED_IN_CHANNEL"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0035
            r6 = 2
            goto L_0x0036
        L_0x0035:
            r6 = -1
        L_0x0036:
            if (r6 == 0) goto L_0x006d
            if (r6 == r4) goto L_0x0055
            if (r6 == r3) goto L_0x003d
            goto L_0x0084
        L_0x003d:
            org.telegram.messenger.NotificationCenter r6 = r5.getNotificationCenter()
            int r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r1[r2] = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
            r1[r4] = r7
            r6.postNotificationName(r0, r1)
            goto L_0x0084
        L_0x0055:
            org.telegram.messenger.NotificationCenter r6 = r5.getNotificationCenter()
            int r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r1[r2] = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r4)
            r1[r4] = r7
            r6.postNotificationName(r0, r1)
            goto L_0x0084
        L_0x006d:
            org.telegram.messenger.NotificationCenter r6 = r5.getNotificationCenter()
            int r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r1[r2] = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
            r1[r4] = r7
            r6.postNotificationName(r0, r1)
        L_0x0084:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.checkChannelError(java.lang.String, int):void");
    }

    public void getDifference() {
        getDifference(getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue(), false);
    }

    public void getDifference(int i, int i2, int i3, boolean z) {
        registerForPush(SharedConfig.pushString);
        if (getMessagesStorage().getLastPtsValue() == 0) {
            loadCurrentState();
        } else if (z || !this.gettingDifference) {
            this.gettingDifference = true;
            TLRPC.TL_updates_getDifference tL_updates_getDifference = new TLRPC.TL_updates_getDifference();
            tL_updates_getDifference.pts = i;
            tL_updates_getDifference.date = i2;
            tL_updates_getDifference.qts = i3;
            if (this.getDifferenceFirstSync) {
                tL_updates_getDifference.flags |= 1;
                if (ApplicationLoader.isConnectedOrConnectingToWiFi()) {
                    tL_updates_getDifference.pts_total_limit = 5000;
                } else {
                    tL_updates_getDifference.pts_total_limit = 1000;
                }
                this.getDifferenceFirstSync = false;
            }
            if (tL_updates_getDifference.date == 0) {
                tL_updates_getDifference.date = getConnectionsManager().getCurrentTime();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start getDifference with date = " + i2 + " pts = " + i + " qts = " + i3);
            }
            getConnectionsManager().setIsUpdating(true);
            getConnectionsManager().sendRequest(tL_updates_getDifference, new RequestDelegate(i2, i3) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$getDifference$230$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getDifference$230$MessagesController(int i, int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i3 = 0;
        if (tL_error == null) {
            TLRPC.updates_Difference updates_difference = (TLRPC.updates_Difference) tLObject;
            if (updates_difference instanceof TLRPC.TL_updates_differenceTooLong) {
                AndroidUtilities.runOnUIThread(new Runnable(updates_difference, i, i2) {
                    private final /* synthetic */ TLRPC.updates_Difference f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$222$MessagesController(this.f$1, this.f$2, this.f$3);
                    }
                });
                return;
            }
            if (updates_difference instanceof TLRPC.TL_updates_differenceSlice) {
                TLRPC.TL_updates_state tL_updates_state = updates_difference.intermediate_state;
                getDifference(tL_updates_state.pts, tL_updates_state.date, tL_updates_state.qts, true);
            }
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (int i4 = 0; i4 < updates_difference.users.size(); i4++) {
                TLRPC.User user = updates_difference.users.get(i4);
                sparseArray.put(user.id, user);
            }
            for (int i5 = 0; i5 < updates_difference.chats.size(); i5++) {
                TLRPC.Chat chat = updates_difference.chats.get(i5);
                sparseArray2.put(chat.id, chat);
            }
            ArrayList arrayList = new ArrayList();
            if (!updates_difference.other_updates.isEmpty()) {
                while (i3 < updates_difference.other_updates.size()) {
                    TLRPC.Update update = updates_difference.other_updates.get(i3);
                    if (update instanceof TLRPC.TL_updateMessageID) {
                        arrayList.add((TLRPC.TL_updateMessageID) update);
                        updates_difference.other_updates.remove(i3);
                    } else {
                        if (getUpdateType(update) == 2) {
                            int updateChannelId = getUpdateChannelId(update);
                            int i6 = this.channelsPts.get(updateChannelId);
                            if (i6 == 0 && (i6 = getMessagesStorage().getChannelPtsSync(updateChannelId)) != 0) {
                                this.channelsPts.put(updateChannelId, i6);
                            }
                            if (i6 != 0 && getUpdatePts(update) <= i6) {
                                updates_difference.other_updates.remove(i3);
                            }
                        }
                        i3++;
                    }
                    i3--;
                    i3++;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(updates_difference) {
                private final /* synthetic */ TLRPC.updates_Difference f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$223$MessagesController(this.f$1);
                }
            });
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(updates_difference, arrayList, sparseArray, sparseArray2) {
                private final /* synthetic */ TLRPC.updates_Difference f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ SparseArray f$3;
                private final /* synthetic */ SparseArray f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    MessagesController.this.lambda$null$229$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
            return;
        }
        this.gettingDifference = false;
        getConnectionsManager().setIsUpdating(false);
    }

    public /* synthetic */ void lambda$null$222$MessagesController(TLRPC.updates_Difference updates_difference, int i, int i2) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), updates_difference.pts, i, i2);
    }

    public /* synthetic */ void lambda$null$223$MessagesController(TLRPC.updates_Difference updates_difference) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        putUsers(updates_difference.users, false);
        putChats(updates_difference.chats, false);
    }

    public /* synthetic */ void lambda$null$229$MessagesController(TLRPC.updates_Difference updates_difference, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2) {
        TLRPC.updates_Difference updates_difference2 = updates_difference;
        getMessagesStorage().putUsersAndChats(updates_difference2.users, updates_difference2.chats, true, false);
        if (!arrayList.isEmpty()) {
            SparseArray sparseArray3 = new SparseArray();
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_updateMessageID tL_updateMessageID = (TLRPC.TL_updateMessageID) arrayList.get(i);
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tL_updateMessageID.random_id, (Integer) null, tL_updateMessageID.id, 0, false, 0, -1);
                if (updateMessageStateAndId != null) {
                    sparseArray3.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray3.size() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable(sparseArray3) {
                    private final /* synthetic */ SparseArray f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MessagesController.this.lambda$null$224$MessagesController(this.f$1);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable(updates_difference2, sparseArray, sparseArray2) {
            private final /* synthetic */ TLRPC.updates_Difference f$1;
            private final /* synthetic */ SparseArray f$2;
            private final /* synthetic */ SparseArray f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$null$228$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$224$MessagesController(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            int i2 = (int) jArr[1];
            getSendMessagesHelper().processSentMessage(i2);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i2), Integer.valueOf(keyAt), null, Long.valueOf(jArr[0]), 0L, -1, false);
        }
    }

    public /* synthetic */ void lambda$null$228$MessagesController(TLRPC.updates_Difference updates_difference, SparseArray sparseArray, SparseArray sparseArray2) {
        TLRPC.updates_Difference updates_difference2 = updates_difference;
        int i = 0;
        if (!updates_difference2.new_messages.isEmpty() || !updates_difference2.new_encrypted_messages.isEmpty()) {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i2 = 0; i2 < updates_difference2.new_encrypted_messages.size(); i2++) {
                ArrayList<TLRPC.Message> decryptMessage = getSecretChatHelper().decryptMessage(updates_difference2.new_encrypted_messages.get(i2));
                if (decryptMessage != null && !decryptMessage.isEmpty()) {
                    updates_difference2.new_messages.addAll(decryptMessage);
                }
            }
            ImageLoader.saveMessagesThumbs(updates_difference2.new_messages);
            ArrayList arrayList = new ArrayList();
            int clientUserId = getUserConfig().getClientUserId();
            for (int i3 = 0; i3 < updates_difference2.new_messages.size(); i3++) {
                TLRPC.Message message = updates_difference2.new_messages.get(i3);
                if (message.dialog_id == 0) {
                    TLRPC.Peer peer = message.to_id;
                    int i4 = peer.chat_id;
                    if (i4 != 0) {
                        message.dialog_id = (long) (-i4);
                    } else {
                        if (peer.user_id == getUserConfig().getClientUserId()) {
                            message.to_id.user_id = message.from_id;
                        }
                        message.dialog_id = (long) message.to_id.user_id;
                    }
                }
                if (((int) message.dialog_id) != 0) {
                    TLRPC.MessageAction messageAction = message.action;
                    if (messageAction instanceof TLRPC.TL_messageActionChatDeleteUser) {
                        TLRPC.User user = (TLRPC.User) sparseArray.get(messageAction.user_id);
                        if (user != null && user.bot) {
                            message.reply_markup = new TLRPC.TL_replyKeyboardHide();
                            message.flags |= 64;
                        }
                    } else {
                        SparseArray sparseArray3 = sparseArray;
                    }
                    TLRPC.MessageAction messageAction2 = message.action;
                    if ((messageAction2 instanceof TLRPC.TL_messageActionChatMigrateTo) || (messageAction2 instanceof TLRPC.TL_messageActionChannelCreate)) {
                        message.unread = false;
                        message.media_unread = false;
                    } else {
                        ConcurrentHashMap<Long, Integer> concurrentHashMap = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                        Integer num = concurrentHashMap.get(Long.valueOf(message.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message.out, message.dialog_id));
                            concurrentHashMap.put(Long.valueOf(message.dialog_id), num);
                        }
                        message.unread = num.intValue() < message.id;
                    }
                } else {
                    SparseArray sparseArray4 = sparseArray;
                }
                if (message.dialog_id == ((long) clientUserId)) {
                    message.unread = false;
                    message.media_unread = false;
                    message.out = true;
                }
                MessageObject messageObject = new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) sparseArray, (SparseArray<TLRPC.Chat>) sparseArray2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                if ((!messageObject.isOut() || messageObject.messageOwner.from_scheduled) && messageObject.isUnread()) {
                    arrayList.add(messageObject);
                }
                ArrayList arrayList2 = (ArrayList) longSparseArray.get(message.dialog_id);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    longSparseArray.put(message.dialog_id, arrayList2);
                }
                arrayList2.add(messageObject);
            }
            AndroidUtilities.runOnUIThread(new Runnable(longSparseArray) {
                private final /* synthetic */ LongSparseArray f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MessagesController.this.lambda$null$225$MessagesController(this.f$1);
                }
            });
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, updates_difference2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ TLRPC.updates_Difference f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MessagesController.this.lambda$null$227$MessagesController(this.f$1, this.f$2);
                }
            });
            getSecretChatHelper().processPendingEncMessages();
        }
        if (!updates_difference2.other_updates.isEmpty()) {
            processUpdateArray(updates_difference2.other_updates, updates_difference2.users, updates_difference2.chats, true, 0);
        }
        if (updates_difference2 instanceof TLRPC.TL_updates_difference) {
            this.gettingDifference = false;
            getMessagesStorage().setLastSeqValue(updates_difference2.state.seq);
            getMessagesStorage().setLastDateValue(updates_difference2.state.date);
            getMessagesStorage().setLastPtsValue(updates_difference2.state.pts);
            getMessagesStorage().setLastQtsValue(updates_difference2.state.qts);
            getConnectionsManager().setIsUpdating(false);
            while (i < 3) {
                processUpdatesQueue(i, 1);
                i++;
            }
        } else if (updates_difference2 instanceof TLRPC.TL_updates_differenceSlice) {
            getMessagesStorage().setLastDateValue(updates_difference2.intermediate_state.date);
            getMessagesStorage().setLastPtsValue(updates_difference2.intermediate_state.pts);
            getMessagesStorage().setLastQtsValue(updates_difference2.intermediate_state.qts);
        } else if (updates_difference2 instanceof TLRPC.TL_updates_differenceEmpty) {
            this.gettingDifference = false;
            getMessagesStorage().setLastSeqValue(updates_difference2.seq);
            getMessagesStorage().setLastDateValue(updates_difference2.date);
            getConnectionsManager().setIsUpdating(false);
            while (i < 3) {
                processUpdatesQueue(i, 1);
                i++;
            }
        }
        getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("received difference with date = " + getMessagesStorage().getLastDateValue() + " pts = " + getMessagesStorage().getLastPtsValue() + " seq = " + getMessagesStorage().getLastSeqValue() + " messages = " + updates_difference2.new_messages.size() + " users = " + updates_difference2.users.size() + " chats = " + updates_difference2.chats.size() + " other updates = " + updates_difference2.other_updates.size());
        }
    }

    public /* synthetic */ void lambda$null$225$MessagesController(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i), false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$227$MessagesController(ArrayList arrayList, TLRPC.updates_Difference updates_difference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, updates_difference) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ TLRPC.updates_Difference f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MessagesController.this.lambda$null$226$MessagesController(this.f$1, this.f$2);
                }
            });
        }
        getMessagesStorage().putMessages(updates_difference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false);
    }

    public /* synthetic */ void lambda$null$226$MessagesController(ArrayList arrayList, TLRPC.updates_Difference updates_difference) {
        getNotificationsController().processNewMessages(arrayList, !(updates_difference instanceof TLRPC.TL_updates_differenceSlice), false, (CountDownLatch) null);
    }

    public void markDialogAsUnread(long j, TLRPC.InputPeer inputPeer, long j2) {
        NativeByteBuffer nativeByteBuffer;
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog != null) {
            dialog.unread_mark = true;
            if (dialog.unread_count == 0 && !isDialogMuted(j)) {
                this.unreadUnmutedDialogs++;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 256);
            getMessagesStorage().setDialogUnread(j, true);
        }
        int i = (int) j;
        if (i != 0) {
            TLRPC.TL_messages_markDialogUnread tL_messages_markDialogUnread = new TLRPC.TL_messages_markDialogUnread();
            tL_messages_markDialogUnread.unread = true;
            if (inputPeer == null) {
                inputPeer = getInputPeer(i);
            }
            if (!(inputPeer instanceof TLRPC.TL_inputPeerEmpty)) {
                TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                tL_inputDialogPeer.peer = inputPeer;
                tL_messages_markDialogUnread.peer = tL_inputDialogPeer;
                if (j2 == 0) {
                    try {
                        nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 12);
                        try {
                            nativeByteBuffer.writeInt32(9);
                            nativeByteBuffer.writeInt64(j);
                            inputPeer.serializeToStream(nativeByteBuffer);
                        } catch (Exception e) {
                            e = e;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        nativeByteBuffer = null;
                        FileLog.e((Throwable) e);
                        j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_markDialogUnread, new RequestDelegate(j2) {
                            private final /* synthetic */ long f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$markDialogAsUnread$231$MessagesController(this.f$1, tLObject, tL_error);
                            }
                        });
                    }
                    j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                }
                getConnectionsManager().sendRequest(tL_messages_markDialogUnread, new RequestDelegate(j2) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$markDialogAsUnread$231$MessagesController(this.f$1, tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$markDialogAsUnread$231$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadUnreadDialogs() {
        if (!this.loadingUnreadDialogs && !getUserConfig().unreadDialogsLoaded) {
            this.loadingUnreadDialogs = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_messages_getDialogUnreadMarks(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadUnreadDialogs$233$MessagesController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadUnreadDialogs$233$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$232$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$232$MessagesController(TLObject tLObject) {
        long j;
        int i;
        if (tLObject != null) {
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            int size = vector.objects.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC.DialogPeer dialogPeer = (TLRPC.DialogPeer) vector.objects.get(i2);
                if (dialogPeer instanceof TLRPC.TL_dialogPeer) {
                    TLRPC.Peer peer = ((TLRPC.TL_dialogPeer) dialogPeer).peer;
                    int i3 = peer.user_id;
                    if (i3 == 0) {
                        j = 0;
                    } else if (i3 != 0) {
                        j = (long) i3;
                    } else {
                        int i4 = peer.chat_id;
                        if (i4 != 0) {
                            i = -i4;
                        } else {
                            i = -peer.channel_id;
                        }
                        j = (long) i;
                    }
                    getMessagesStorage().setDialogUnread(j, true);
                    TLRPC.Dialog dialog = this.dialogs_dict.get(j);
                    if (dialog != null && !dialog.unread_mark) {
                        dialog.unread_mark = true;
                        if (dialog.unread_count == 0 && !isDialogMuted(j)) {
                            this.unreadUnmutedDialogs++;
                        }
                    }
                }
            }
            getUserConfig().unreadDialogsLoaded = true;
            getUserConfig().saveConfig(false);
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 256);
            this.loadingUnreadDialogs = false;
        }
    }

    public void reorderPinnedDialogs(int i, ArrayList<TLRPC.InputDialogPeer> arrayList, long j) {
        NativeByteBuffer nativeByteBuffer;
        TLRPC.TL_messages_reorderPinnedDialogs tL_messages_reorderPinnedDialogs = new TLRPC.TL_messages_reorderPinnedDialogs();
        tL_messages_reorderPinnedDialogs.folder_id = i;
        tL_messages_reorderPinnedDialogs.force = true;
        if (j == 0) {
            ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
            if (!dialogs.isEmpty()) {
                int size = dialogs.size();
                int i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    TLRPC.Dialog dialog = dialogs.get(i3);
                    if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                        if (dialog.pinned) {
                            getMessagesStorage().setDialogPinned(dialog.id, dialog.pinnedNum);
                            if (((int) dialog.id) != 0) {
                                TLRPC.InputPeer inputPeer = getInputPeer((int) dialogs.get(i3).id);
                                TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                                tL_inputDialogPeer.peer = inputPeer;
                                tL_messages_reorderPinnedDialogs.order.add(tL_inputDialogPeer);
                                i2 += tL_inputDialogPeer.getObjectSize();
                            }
                        } else if (dialog.id != this.proxyDialogId) {
                            break;
                        }
                    }
                }
                try {
                    nativeByteBuffer = new NativeByteBuffer(i2 + 12);
                    try {
                        nativeByteBuffer.writeInt32(16);
                        nativeByteBuffer.writeInt32(i);
                        nativeByteBuffer.writeInt32(tL_messages_reorderPinnedDialogs.order.size());
                        int size2 = tL_messages_reorderPinnedDialogs.order.size();
                        for (int i4 = 0; i4 < size2; i4++) {
                            tL_messages_reorderPinnedDialogs.order.get(i4).serializeToStream(nativeByteBuffer);
                        }
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new RequestDelegate(j) {
                            private final /* synthetic */ long f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$reorderPinnedDialogs$234$MessagesController(this.f$1, tLObject, tL_error);
                            }
                        });
                    }
                } catch (Exception e2) {
                    e = e2;
                    nativeByteBuffer = null;
                    FileLog.e((Throwable) e);
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new RequestDelegate(j) {
                        private final /* synthetic */ long f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.lambda$reorderPinnedDialogs$234$MessagesController(this.f$1, tLObject, tL_error);
                        }
                    });
                }
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
            } else {
                return;
            }
        } else {
            tL_messages_reorderPinnedDialogs.order = arrayList;
        }
        getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new RequestDelegate(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.lambda$reorderPinnedDialogs$234$MessagesController(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$reorderPinnedDialogs$234$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public boolean pinDialog(long j, boolean z, TLRPC.InputPeer inputPeer, long j2) {
        long j3;
        NativeByteBuffer nativeByteBuffer;
        long j4 = j;
        boolean z2 = z;
        int i = (int) j4;
        TLRPC.Dialog dialog = this.dialogs_dict.get(j4);
        if (dialog != null && dialog.pinned != z2) {
            int i2 = dialog.folder_id;
            ArrayList<TLRPC.Dialog> dialogs = getDialogs(i2);
            dialog.pinned = z2;
            if (z2) {
                int i3 = 0;
                for (int i4 = 0; i4 < dialogs.size(); i4++) {
                    TLRPC.Dialog dialog2 = dialogs.get(i4);
                    if (!(dialog2 instanceof TLRPC.TL_dialogFolder)) {
                        if (dialog2.pinned) {
                            i3 = Math.max(dialog2.pinnedNum, i3);
                        } else if (dialog2.id != this.proxyDialogId) {
                            break;
                        }
                    }
                }
                dialog.pinnedNum = i3 + 1;
            } else {
                dialog.pinnedNum = 0;
            }
            sortDialogs((SparseArray<TLRPC.Chat>) null);
            if (!z2 && dialogs.get(dialogs.size() - 1) == dialog && !this.dialogsEndReached.get(i2)) {
                dialogs.remove(dialogs.size() - 1);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            if (!(i == 0 || j2 == -1)) {
                TLRPC.TL_messages_toggleDialogPin tL_messages_toggleDialogPin = new TLRPC.TL_messages_toggleDialogPin();
                tL_messages_toggleDialogPin.pinned = z2;
                TLRPC.InputPeer inputPeer2 = inputPeer == null ? getInputPeer(i) : inputPeer;
                if (inputPeer2 instanceof TLRPC.TL_inputPeerEmpty) {
                    return false;
                }
                TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                tL_inputDialogPeer.peer = inputPeer2;
                tL_messages_toggleDialogPin.peer = tL_inputDialogPeer;
                if (j2 == 0) {
                    try {
                        nativeByteBuffer = new NativeByteBuffer(inputPeer2.getObjectSize() + 16);
                        try {
                            nativeByteBuffer.writeInt32(4);
                            nativeByteBuffer.writeInt64(j4);
                            nativeByteBuffer.writeBool(z2);
                            inputPeer2.serializeToStream(nativeByteBuffer);
                        } catch (Exception e) {
                            e = e;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        nativeByteBuffer = null;
                        FileLog.e((Throwable) e);
                        j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_toggleDialogPin, new RequestDelegate(j3) {
                            private final /* synthetic */ long f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.lambda$pinDialog$235$MessagesController(this.f$1, tLObject, tL_error);
                            }
                        });
                        getMessagesStorage().setDialogPinned(j4, dialog.pinnedNum);
                        return true;
                    }
                    j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                } else {
                    j3 = j2;
                }
                getConnectionsManager().sendRequest(tL_messages_toggleDialogPin, new RequestDelegate(j3) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.lambda$pinDialog$235$MessagesController(this.f$1, tLObject, tL_error);
                    }
                });
            }
            getMessagesStorage().setDialogPinned(j4, dialog.pinnedNum);
            return true;
        } else if (dialog != null) {
            return true;
        } else {
            return false;
        }
    }

    public /* synthetic */ void lambda$pinDialog$235$MessagesController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadPinnedDialogs(int i, long j, ArrayList<Long> arrayList) {
        if (this.loadingPinnedDialogs.indexOfKey(i) < 0 && !getUserConfig().isPinnedDialogsLoaded(i)) {
            this.loadingPinnedDialogs.put(i, 1);
            TLRPC.TL_messages_getPinnedDialogs tL_messages_getPinnedDialogs = new TLRPC.TL_messages_getPinnedDialogs();
            tL_messages_getPinnedDialogs.folder_id = i;
            getConnectionsManager().sendRequest(tL_messages_getPinnedDialogs, new RequestDelegate(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$loadPinnedDialogs$238$MessagesController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadPinnedDialogs$238$MessagesController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        MessageObject messageObject;
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            ArrayList arrayList = new ArrayList(tL_messages_peerDialogs.dialogs);
            fetchFolderInLoadedPinnedDialogs(tL_messages_peerDialogs);
            TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
            tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
            tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
            tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
            tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
            LongSparseArray longSparseArray = new LongSparseArray();
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (int i2 = 0; i2 < tL_messages_peerDialogs.users.size(); i2++) {
                TLRPC.User user = tL_messages_peerDialogs.users.get(i2);
                sparseArray.put(user.id, user);
            }
            for (int i3 = 0; i3 < tL_messages_peerDialogs.chats.size(); i3++) {
                TLRPC.Chat chat3 = tL_messages_peerDialogs.chats.get(i3);
                sparseArray2.put(chat3.id, chat3);
            }
            for (int i4 = 0; i4 < tL_messages_peerDialogs.messages.size(); i4++) {
                TLRPC.Message message = tL_messages_peerDialogs.messages.get(i4);
                TLRPC.Peer peer = message.to_id;
                int i5 = peer.channel_id;
                if (i5 != 0) {
                    TLRPC.Chat chat4 = (TLRPC.Chat) sparseArray2.get(i5);
                    if (chat4 != null && chat4.left) {
                    }
                } else {
                    int i6 = peer.chat_id;
                    if (!(i6 == 0 || (chat2 = (TLRPC.Chat) sparseArray2.get(i6)) == null || chat2.migrated_to == null)) {
                    }
                }
                MessageObject messageObject2 = new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) sparseArray, (SparseArray<TLRPC.Chat>) sparseArray2, false);
                longSparseArray.put(messageObject2.getDialogId(), messageObject2);
            }
            boolean z = !arrayList.isEmpty() && (arrayList.get(0) instanceof TLRPC.TL_dialogFolder);
            int size = arrayList.size();
            for (int i7 = 0; i7 < size; i7++) {
                TLRPC.Dialog dialog = (TLRPC.Dialog) arrayList.get(i7);
                dialog.pinned = true;
                DialogObject.initDialog(dialog);
                if (DialogObject.isChannel(dialog)) {
                    TLRPC.Chat chat5 = (TLRPC.Chat) sparseArray2.get(-((int) dialog.id));
                    if (chat5 != null && chat5.left) {
                    }
                } else {
                    long j = dialog.id;
                    if (!(((int) j) >= 0 || (chat = (TLRPC.Chat) sparseArray2.get(-((int) j))) == null || chat.migrated_to == null)) {
                    }
                }
                if (dialog.last_message_date == 0 && (messageObject = (MessageObject) longSparseArray.get(dialog.id)) != null) {
                    dialog.last_message_date = messageObject.messageOwner.date;
                }
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num.intValue(), dialog.read_inbox_max_id)));
                Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (num2 == null) {
                    num2 = 0;
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num2.intValue(), dialog.read_outbox_max_id)));
            }
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ TLRPC.TL_messages_peerDialogs f$4;
                private final /* synthetic */ LongSparseArray f$5;
                private final /* synthetic */ TLRPC.TL_messages_dialogs f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    MessagesController.this.lambda$null$237$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$237$MessagesController(int i, ArrayList arrayList, boolean z, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TLRPC.TL_messages_dialogs tL_messages_dialogs) {
        AndroidUtilities.runOnUIThread(new Runnable(i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ TLRPC.TL_messages_peerDialogs f$4;
            private final /* synthetic */ LongSparseArray f$5;
            private final /* synthetic */ TLRPC.TL_messages_dialogs f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MessagesController.this.lambda$null$236$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$236$MessagesController(int i, ArrayList arrayList, boolean z, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TLRPC.TL_messages_dialogs tL_messages_dialogs) {
        boolean z2;
        int i2 = i;
        ArrayList arrayList2 = arrayList;
        TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs2 = tL_messages_peerDialogs;
        this.loadingPinnedDialogs.delete(i2);
        applyDialogsNotificationsSettings(arrayList2);
        ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
        int i3 = z;
        int i4 = 0;
        boolean z3 = false;
        for (int i5 = 0; i5 < dialogs.size(); i5++) {
            TLRPC.Dialog dialog = dialogs.get(i5);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                long j = dialog.id;
                if (((int) j) == 0) {
                    if (i3 < arrayList.size()) {
                        arrayList2.add(i3, dialog);
                    } else {
                        arrayList2.add(dialog);
                    }
                    i3++;
                } else if (dialog.pinned) {
                    i4 = Math.max(dialog.pinnedNum, i4);
                    dialog.pinned = false;
                    dialog.pinnedNum = 0;
                    i3++;
                    z3 = true;
                } else if (j != this.proxyDialogId) {
                    break;
                }
            }
        }
        ArrayList arrayList3 = new ArrayList();
        if (!arrayList.isEmpty()) {
            putUsers(tL_messages_peerDialogs2.users, false);
            putChats(tL_messages_peerDialogs2.chats, false);
            int size = arrayList.size();
            int i6 = 0;
            z2 = false;
            while (i6 < size) {
                TLRPC.Dialog dialog2 = (TLRPC.Dialog) arrayList2.get(i6);
                dialog2.pinnedNum = (size - i6) + i4;
                arrayList3.add(Long.valueOf(dialog2.id));
                TLRPC.Dialog dialog3 = this.dialogs_dict.get(dialog2.id);
                if (dialog3 != null) {
                    dialog3.pinned = true;
                    dialog3.pinnedNum = dialog2.pinnedNum;
                    getMessagesStorage().setDialogPinned(dialog2.id, dialog2.pinnedNum);
                    LongSparseArray longSparseArray2 = longSparseArray;
                } else {
                    this.dialogs_dict.put(dialog2.id, dialog2);
                    MessageObject messageObject = (MessageObject) longSparseArray.get(dialog2.id);
                    this.dialogMessage.put(dialog2.id, messageObject);
                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        long j2 = messageObject.messageOwner.random_id;
                        if (j2 != 0) {
                            this.dialogMessagesByRandomIds.put(j2, messageObject);
                        }
                    }
                    z2 = true;
                }
                i6++;
                z3 = true;
            }
        } else {
            z2 = false;
        }
        if (z3) {
            if (z2) {
                this.allDialogs.clear();
                int size2 = this.dialogs_dict.size();
                for (int i7 = 0; i7 < size2; i7++) {
                    this.allDialogs.add(this.dialogs_dict.valueAt(i7));
                }
            }
            sortDialogs((SparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getMessagesStorage().unpinAllDialogsExceptNew(arrayList3, i2);
        getMessagesStorage().putDialogs(tL_messages_dialogs, 1);
        getUserConfig().setPinnedDialogsLoaded(i2, true);
        getUserConfig().saveConfig(false);
    }

    public void generateJoinMessage(int i, boolean z) {
        TLRPC.Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i, this.currentAccount)) {
            if ((!chat.left && !chat.kicked) || z) {
                TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
                tL_messageService.flags = 256;
                int newMessageId = getUserConfig().getNewMessageId();
                tL_messageService.id = newMessageId;
                tL_messageService.local_id = newMessageId;
                tL_messageService.date = getConnectionsManager().getCurrentTime();
                tL_messageService.from_id = getUserConfig().getClientUserId();
                tL_messageService.to_id = new TLRPC.TL_peerChannel();
                tL_messageService.to_id.channel_id = i;
                tL_messageService.dialog_id = (long) (-i);
                tL_messageService.post = true;
                tL_messageService.action = new TLRPC.TL_messageActionChatAddUser();
                tL_messageService.action.users.add(Integer.valueOf(getUserConfig().getClientUserId()));
                if (chat.megagroup) {
                    tL_messageService.flags |= Integer.MIN_VALUE;
                }
                getUserConfig().saveConfig(false);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(tL_messageService);
                arrayList.add(new MessageObject(this.currentAccount, tL_messageService, true));
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MessagesController.this.lambda$generateJoinMessage$240$MessagesController(this.f$1);
                    }
                });
                getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList2, true, true, false, 0, false);
                AndroidUtilities.runOnUIThread(new Runnable(i, arrayList) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ ArrayList f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MessagesController.this.lambda$generateJoinMessage$241$MessagesController(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$generateJoinMessage$240$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$239$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$239$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, (CountDownLatch) null);
    }

    public /* synthetic */ void lambda$generateJoinMessage$241$MessagesController(int i, ArrayList arrayList) {
        updateInterfaceWithMessages((long) (-i), arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* access modifiers changed from: protected */
    public void deleteMessagesByPush(long j, ArrayList<Integer> arrayList, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, i, j) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MessagesController.this.lambda$deleteMessagesByPush$243$MessagesController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$deleteMessagesByPush$243$MessagesController(ArrayList arrayList, int i, long j) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList, i) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MessagesController.this.lambda$null$242$MessagesController(this.f$1, this.f$2);
            }
        });
        getMessagesStorage().deletePushMessages(j, arrayList);
        getMessagesStorage().updateDialogsWithDeletedMessages(arrayList, getMessagesStorage().markMessagesAsDeleted(arrayList, false, i, true, false), false, i);
    }

    public /* synthetic */ void lambda$null$242$MessagesController(ArrayList arrayList, int i) {
        int i2 = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(i), false);
        if (i == 0) {
            int size = arrayList.size();
            while (i2 < size) {
                MessageObject messageObject = this.dialogMessagesByIds.get(((Integer) arrayList.get(i2)).intValue());
                if (messageObject != null) {
                    messageObject.deleted = true;
                }
                i2++;
            }
            return;
        }
        MessageObject messageObject2 = this.dialogMessage.get((long) (-i));
        if (messageObject2 != null) {
            int size2 = arrayList.size();
            while (i2 < size2) {
                if (messageObject2.getId() == ((Integer) arrayList.get(i2)).intValue()) {
                    messageObject2.deleted = true;
                    return;
                }
                i2++;
            }
        }
    }

    public void checkChannelInviter(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$checkChannelInviter$249$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$checkChannelInviter$249$MessagesController(int i) {
        TLRPC.Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i, this.currentAccount) && !chat.creator) {
            TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
            tL_channels_getParticipant.channel = getInputChannel(i);
            tL_channels_getParticipant.user_id = new TLRPC.TL_inputUserSelf();
            getConnectionsManager().sendRequest(tL_channels_getParticipant, new RequestDelegate(chat, i) {
                private final /* synthetic */ TLRPC.Chat f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.lambda$null$248$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$248$MessagesController(TLRPC.Chat chat, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant = (TLRPC.TL_channels_channelParticipant) tLObject;
        if (tL_channels_channelParticipant != null) {
            TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipant.participant;
            if ((channelParticipant instanceof TLRPC.TL_channelParticipantSelf) && channelParticipant.inviter_id != getUserConfig().getClientUserId()) {
                if (!chat.megagroup || !getMessagesStorage().isMigratedChat(chat.id)) {
                    AndroidUtilities.runOnUIThread(new Runnable(tL_channels_channelParticipant) {
                        private final /* synthetic */ TLRPC.TL_channels_channelParticipant f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MessagesController.this.lambda$null$244$MessagesController(this.f$1);
                        }
                    });
                    getMessagesStorage().putUsersAndChats(tL_channels_channelParticipant.users, (ArrayList<TLRPC.Chat>) null, true, true);
                    TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
                    tL_messageService.media_unread = true;
                    tL_messageService.unread = true;
                    tL_messageService.flags = 256;
                    tL_messageService.post = true;
                    if (chat.megagroup) {
                        tL_messageService.flags |= Integer.MIN_VALUE;
                    }
                    int newMessageId = getUserConfig().getNewMessageId();
                    tL_messageService.id = newMessageId;
                    tL_messageService.local_id = newMessageId;
                    tL_messageService.date = tL_channels_channelParticipant.participant.date;
                    tL_messageService.action = new TLRPC.TL_messageActionChatAddUser();
                    tL_messageService.from_id = tL_channels_channelParticipant.participant.inviter_id;
                    tL_messageService.action.users.add(Integer.valueOf(getUserConfig().getClientUserId()));
                    tL_messageService.to_id = new TLRPC.TL_peerChannel();
                    tL_messageService.to_id.channel_id = i;
                    tL_messageService.dialog_id = (long) (-i);
                    getUserConfig().saveConfig(false);
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
                    for (int i2 = 0; i2 < tL_channels_channelParticipant.users.size(); i2++) {
                        TLRPC.User user = tL_channels_channelParticipant.users.get(i2);
                        concurrentHashMap.put(Integer.valueOf(user.id), user);
                    }
                    arrayList2.add(tL_messageService);
                    arrayList.add(new MessageObject(this.currentAccount, (TLRPC.Message) tL_messageService, (AbstractMap<Integer, TLRPC.User>) concurrentHashMap, true));
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MessagesController.this.lambda$null$246$MessagesController(this.f$1);
                        }
                    });
                    getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList2, true, true, false, 0, false);
                    AndroidUtilities.runOnUIThread(new Runnable(i, arrayList) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ ArrayList f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            MessagesController.this.lambda$null$247$MessagesController(this.f$1, this.f$2);
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$244$MessagesController(TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant) {
        putUsers(tL_channels_channelParticipant.users, false);
    }

    public /* synthetic */ void lambda$null$245$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, (CountDownLatch) null);
    }

    public /* synthetic */ void lambda$null$246$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$245$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$247$MessagesController(int i, ArrayList arrayList) {
        updateInterfaceWithMessages((long) (-i), arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private int getUpdateType(TLRPC.Update update) {
        if ((update instanceof TLRPC.TL_updateNewMessage) || (update instanceof TLRPC.TL_updateReadMessagesContents) || (update instanceof TLRPC.TL_updateReadHistoryInbox) || (update instanceof TLRPC.TL_updateReadHistoryOutbox) || (update instanceof TLRPC.TL_updateDeleteMessages) || (update instanceof TLRPC.TL_updateWebPage) || (update instanceof TLRPC.TL_updateEditMessage) || (update instanceof TLRPC.TL_updateFolderPeers)) {
            return 0;
        }
        if (update instanceof TLRPC.TL_updateNewEncryptedMessage) {
            return 1;
        }
        return ((update instanceof TLRPC.TL_updateNewChannelMessage) || (update instanceof TLRPC.TL_updateDeleteChannelMessages) || (update instanceof TLRPC.TL_updateEditChannelMessage) || (update instanceof TLRPC.TL_updateChannelWebPage)) ? 2 : 3;
    }

    private static int getUpdatePts(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateDeleteMessages) {
            return ((TLRPC.TL_updateDeleteMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryOutbox) {
            return ((TLRPC.TL_updateReadHistoryOutbox) update).pts;
        }
        if (update instanceof TLRPC.TL_updateNewMessage) {
            return ((TLRPC.TL_updateNewMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateEditMessage) {
            return ((TLRPC.TL_updateEditMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateWebPage) {
            return ((TLRPC.TL_updateWebPage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryInbox) {
            return ((TLRPC.TL_updateReadHistoryInbox) update).pts;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadMessagesContents) {
            return ((TLRPC.TL_updateReadMessagesContents) update).pts;
        }
        if (update instanceof TLRPC.TL_updateChannelTooLong) {
            return ((TLRPC.TL_updateChannelTooLong) update).pts;
        }
        if (update instanceof TLRPC.TL_updateFolderPeers) {
            return ((TLRPC.TL_updateFolderPeers) update).pts;
        }
        return 0;
    }

    private static int getUpdatePtsCount(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateDeleteMessages) {
            return ((TLRPC.TL_updateDeleteMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryOutbox) {
            return ((TLRPC.TL_updateReadHistoryOutbox) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateNewMessage) {
            return ((TLRPC.TL_updateNewMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateEditMessage) {
            return ((TLRPC.TL_updateEditMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateWebPage) {
            return ((TLRPC.TL_updateWebPage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryInbox) {
            return ((TLRPC.TL_updateReadHistoryInbox) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadMessagesContents) {
            return ((TLRPC.TL_updateReadMessagesContents) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateFolderPeers) {
            return ((TLRPC.TL_updateFolderPeers) update).pts_count;
        }
        return 0;
    }

    private static int getUpdateQts(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateNewEncryptedMessage) {
            return ((TLRPC.TL_updateNewEncryptedMessage) update).qts;
        }
        return 0;
    }

    private static int getUpdateChannelId(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).message.to_id.channel_id;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).message.to_id.channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelOutbox) {
            return ((TLRPC.TL_updateReadChannelOutbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelMessageViews) {
            return ((TLRPC.TL_updateChannelMessageViews) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelTooLong) {
            return ((TLRPC.TL_updateChannelTooLong) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelPinnedMessage) {
            return ((TLRPC.TL_updateChannelPinnedMessage) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelReadMessagesContents) {
            return ((TLRPC.TL_updateChannelReadMessagesContents) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelAvailableMessages) {
            return ((TLRPC.TL_updateChannelAvailableMessages) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannel) {
            return ((TLRPC.TL_updateChannel) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelInbox) {
            return ((TLRPC.TL_updateReadChannelInbox) update).channel_id;
        }
        if (!BuildVars.LOGS_ENABLED) {
            return 0;
        }
        FileLog.e("trying to get unknown update channel_id for " + update);
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0320, code lost:
        if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r6.updatesStartWaitTimeQts) > 1500) goto L_0x01dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x06b2, code lost:
        if (r3 != null) goto L_0x06b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x06b6, code lost:
        if (r5 != null) goto L_0x06b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x06df, code lost:
        if (r3 != null) goto L_0x06e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x06e3, code lost:
        if (r5 != null) goto L_0x06b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x024c, code lost:
        if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r6.updatesStartWaitTimePts) > 1500) goto L_0x01dc;
     */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x06f9  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0735 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0768  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x076b  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0731 A[EDGE_INSN: B:471:0x0731->B:328:0x0731 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0111  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processUpdates(org.telegram.tgnet.TLRPC.Updates r24, boolean r25) {
        /*
            r23 = this;
            r6 = r23
            r7 = r24
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShort
            r8 = 0
            r9 = 0
            r10 = 1
            if (r0 == 0) goto L_0x0020
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            org.telegram.tgnet.TLRPC$Update r0 = r7.update
            r1.add(r0)
            r2 = 0
            r3 = 0
            r4 = 0
            int r5 = r7.date
            r0 = r23
            r0.processUpdateArray(r1, r2, r3, r4, r5)
            goto L_0x007b
        L_0x0020:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShortChatMessage
            java.lang.String r11 = " count = "
            java.lang.String r12 = "add to queue"
            java.lang.String r15 = " "
            if (r0 != 0) goto L_0x0612
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage
            if (r1 == 0) goto L_0x0030
            goto L_0x0612
        L_0x0030:
            boolean r3 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined
            if (r3 != 0) goto L_0x0080
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates
            if (r0 == 0) goto L_0x0039
            goto L_0x0080
        L_0x0039:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updatesTooLong
            if (r0 == 0) goto L_0x0049
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0046
            java.lang.String r0 = "need get diff TL_updatesTooLong"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0046:
            r0 = 0
            r9 = 1
            goto L_0x007c
        L_0x0049:
            boolean r0 = r7 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesSeq
            if (r0 == 0) goto L_0x0057
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r7.seq
            r0.setLastSeqValue(r1)
            goto L_0x007b
        L_0x0057:
            boolean r0 = r7 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesPts
            if (r0 == 0) goto L_0x007b
            int r0 = r7.chat_id
            if (r0 == 0) goto L_0x0072
            android.util.SparseIntArray r1 = r6.channelsPts
            int r2 = r7.pts
            r1.put(r0, r2)
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r7.chat_id
            int r2 = r7.pts
            r0.saveChannelPts(r1, r2)
            goto L_0x007b
        L_0x0072:
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r7.pts
            r0.setLastPtsValue(r1)
        L_0x007b:
            r0 = 0
        L_0x007c:
            r19 = 0
            goto L_0x0939
        L_0x0080:
            r1 = r8
            r0 = 0
        L_0x0082:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r7.chats
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x00cc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r7.chats
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channel
            if (r4 == 0) goto L_0x00c9
            boolean r4 = r2.min
            if (r4 == 0) goto L_0x00c9
            int r4 = r2.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r6.getChat(r4)
            if (r4 == 0) goto L_0x00aa
            boolean r5 = r4.min
            if (r5 == 0) goto L_0x00b7
        L_0x00aa:
            org.telegram.messenger.MessagesStorage r4 = r23.getMessagesStorage()
            int r5 = r7.chat_id
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChatSync(r5)
            r6.putChat(r4, r10)
        L_0x00b7:
            if (r4 == 0) goto L_0x00bd
            boolean r4 = r4.min
            if (r4 == 0) goto L_0x00c9
        L_0x00bd:
            if (r1 != 0) goto L_0x00c4
            android.util.SparseArray r1 = new android.util.SparseArray
            r1.<init>()
        L_0x00c4:
            int r4 = r2.id
            r1.put(r4, r2)
        L_0x00c9:
            int r0 = r0 + 1
            goto L_0x0082
        L_0x00cc:
            if (r1 == 0) goto L_0x010e
            r0 = 0
        L_0x00cf:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r2 = r7.updates
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x010e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r2 = r7.updates
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$Update r2 = (org.telegram.tgnet.TLRPC.Update) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage
            if (r4 == 0) goto L_0x010b
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r2 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.message
            org.telegram.tgnet.TLRPC$Peer r2 = r2.to_id
            int r2 = r2.channel_id
            int r4 = r1.indexOfKey(r2)
            if (r4 < 0) goto L_0x010b
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0109
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "need get diff because of min channel "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0109:
            r0 = 1
            goto L_0x010f
        L_0x010b:
            int r0 = r0 + 1
            goto L_0x00cf
        L_0x010e:
            r0 = 0
        L_0x010f:
            if (r0 != 0) goto L_0x060c
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r7.chats
            r1.putUsersAndChats(r2, r4, r10, r10)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.util.Comparator<org.telegram.tgnet.TLRPC$Update> r2 = r6.updatesComparator
            java.util.Collections.sort(r1, r2)
            r18 = r0
            r19 = 0
        L_0x0127:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r7.updates
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0514
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r7.updates
            java.lang.Object r0 = r0.get(r9)
            org.telegram.tgnet.TLRPC$Update r0 = (org.telegram.tgnet.TLRPC.Update) r0
            int r1 = r6.getUpdateType(r0)
            if (r1 != 0) goto L_0x0268
            org.telegram.tgnet.TLRPC$TL_updates r5 = new org.telegram.tgnet.TLRPC$TL_updates
            r5.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r5.updates
            r1.add(r0)
            int r1 = getUpdatePts(r0)
            r5.pts = r1
            int r1 = getUpdatePtsCount(r0)
            r5.pts_count = r1
        L_0x0153:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            int r1 = r1.size()
            if (r10 >= r1) goto L_0x0189
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.lang.Object r1 = r1.get(r10)
            org.telegram.tgnet.TLRPC$Update r1 = (org.telegram.tgnet.TLRPC.Update) r1
            int r2 = getUpdatePts(r1)
            int r4 = getUpdatePtsCount(r1)
            int r20 = r6.getUpdateType(r1)
            if (r20 != 0) goto L_0x0189
            int r9 = r5.pts
            int r9 = r9 + r4
            if (r9 != r2) goto L_0x0189
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r9 = r5.updates
            r9.add(r1)
            r5.pts = r2
            int r1 = r5.pts_count
            int r1 = r1 + r4
            r5.pts_count = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            r1.remove(r10)
            r9 = 0
            goto L_0x0153
        L_0x0189:
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastPtsValue()
            int r2 = r5.pts_count
            int r1 = r1 + r2
            int r2 = r5.pts
            if (r1 != r2) goto L_0x01eb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r5.updates
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r7.chats
            r9 = 0
            int r0 = r7.date
            r21 = r0
            r0 = r23
            r22 = r3
            r3 = r4
            r13 = 0
            r4 = r9
            r9 = r5
            r5 = r21
            boolean r0 = r0.processUpdateArray(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x01e0
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x01dc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "need get diff inner TL_updates, pts: "
            r0.append(r1)
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastPtsValue()
            r0.append(r1)
            r0.append(r15)
            int r1 = r7.seq
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x01dc:
            r18 = 1
            goto L_0x0508
        L_0x01e0:
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r9.pts
            r0.setLastPtsValue(r1)
            goto L_0x0508
        L_0x01eb:
            r22 = r3
            r9 = r5
            r13 = 0
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastPtsValue()
            int r2 = r9.pts
            if (r1 == r2) goto L_0x0508
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x022f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " need get diff, pts: "
            r1.append(r0)
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r0 = r0.getLastPtsValue()
            r1.append(r0)
            r1.append(r15)
            int r0 = r9.pts
            r1.append(r0)
            r1.append(r11)
            int r0 = r9.pts_count
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x022f:
            boolean r0 = r6.gettingDifference
            if (r0 != 0) goto L_0x024e
            long r0 = r6.updatesStartWaitTimePts
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x024e
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x01dc
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r6.updatesStartWaitTimePts
            long r0 = r0 - r2
            long r0 = java.lang.Math.abs(r0)
            r2 = 1500(0x5dc, double:7.41E-321)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x01dc
        L_0x024e:
            long r0 = r6.updatesStartWaitTimePts
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 != 0) goto L_0x025a
            long r0 = java.lang.System.currentTimeMillis()
            r6.updatesStartWaitTimePts = r0
        L_0x025a:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0261
            org.telegram.messenger.FileLog.d(r12)
        L_0x0261:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates> r0 = r6.updatesQueuePts
            r0.add(r9)
            goto L_0x0508
        L_0x0268:
            r22 = r3
            r13 = 0
            int r1 = r6.getUpdateType(r0)
            if (r1 != r10) goto L_0x0357
            org.telegram.tgnet.TLRPC$TL_updates r9 = new org.telegram.tgnet.TLRPC$TL_updates
            r9.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r9.updates
            r1.add(r0)
            int r1 = getUpdateQts(r0)
            r9.pts = r1
        L_0x0282:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            int r1 = r1.size()
            if (r10 >= r1) goto L_0x02ae
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.lang.Object r1 = r1.get(r10)
            org.telegram.tgnet.TLRPC$Update r1 = (org.telegram.tgnet.TLRPC.Update) r1
            int r2 = getUpdateQts(r1)
            int r3 = r6.getUpdateType(r1)
            if (r3 != r10) goto L_0x02ae
            int r3 = r9.pts
            int r3 = r3 + r10
            if (r3 != r2) goto L_0x02ae
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r3 = r9.updates
            r3.add(r1)
            r9.pts = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            r1.remove(r10)
            goto L_0x0282
        L_0x02ae:
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastQtsValue()
            if (r1 == 0) goto L_0x033c
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastQtsValue()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r2 = r9.updates
            int r2 = r2.size()
            int r1 = r1 + r2
            int r2 = r9.pts
            if (r1 != r2) goto L_0x02cc
            goto L_0x033c
        L_0x02cc:
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastPtsValue()
            int r2 = r9.pts
            if (r1 == r2) goto L_0x0508
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x0303
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " need get diff, qts: "
            r1.append(r0)
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r0 = r0.getLastQtsValue()
            r1.append(r0)
            r1.append(r15)
            int r0 = r9.pts
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0303:
            boolean r0 = r6.gettingDifference
            if (r0 != 0) goto L_0x0322
            long r0 = r6.updatesStartWaitTimeQts
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x0322
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x01dc
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r6.updatesStartWaitTimeQts
            long r0 = r0 - r2
            long r0 = java.lang.Math.abs(r0)
            r2 = 1500(0x5dc, double:7.41E-321)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x01dc
        L_0x0322:
            long r0 = r6.updatesStartWaitTimeQts
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 != 0) goto L_0x032e
            long r0 = java.lang.System.currentTimeMillis()
            r6.updatesStartWaitTimeQts = r0
        L_0x032e:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0335
            org.telegram.messenger.FileLog.d(r12)
        L_0x0335:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates> r0 = r6.updatesQueueQts
            r0.add(r9)
            goto L_0x0508
        L_0x033c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r9.updates
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r7.chats
            r4 = 0
            int r5 = r7.date
            r0 = r23
            r0.processUpdateArray(r1, r2, r3, r4, r5)
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r9.pts
            r0.setLastQtsValue(r1)
            r19 = 1
            goto L_0x0508
        L_0x0357:
            int r1 = r6.getUpdateType(r0)
            r2 = 2
            if (r1 != r2) goto L_0x0516
            int r9 = getUpdateChannelId(r0)
            android.util.SparseIntArray r1 = r6.channelsPts
            int r1 = r1.get(r9)
            if (r1 != 0) goto L_0x0396
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getChannelPtsSync(r9)
            if (r1 != 0) goto L_0x0391
            r3 = 0
        L_0x0375:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r7.chats
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0396
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r7.chats
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC.Chat) r4
            int r5 = r4.id
            if (r5 != r9) goto L_0x038e
            r6.loadUnknownChannel(r4, r13)
            r3 = 1
            goto L_0x0397
        L_0x038e:
            int r3 = r3 + 1
            goto L_0x0375
        L_0x0391:
            android.util.SparseIntArray r3 = r6.channelsPts
            r3.put(r9, r1)
        L_0x0396:
            r3 = 0
        L_0x0397:
            org.telegram.tgnet.TLRPC$TL_updates r5 = new org.telegram.tgnet.TLRPC$TL_updates
            r5.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r5.updates
            r4.add(r0)
            int r4 = getUpdatePts(r0)
            r5.pts = r4
            int r4 = getUpdatePtsCount(r0)
            r5.pts_count = r4
        L_0x03ad:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r7.updates
            int r4 = r4.size()
            if (r10 >= r4) goto L_0x03eb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r7.updates
            java.lang.Object r4 = r4.get(r10)
            org.telegram.tgnet.TLRPC$Update r4 = (org.telegram.tgnet.TLRPC.Update) r4
            int r13 = getUpdatePts(r4)
            int r14 = getUpdatePtsCount(r4)
            int r10 = r6.getUpdateType(r4)
            if (r10 != r2) goto L_0x03eb
            int r10 = getUpdateChannelId(r4)
            if (r9 != r10) goto L_0x03eb
            int r10 = r5.pts
            int r10 = r10 + r14
            if (r10 != r13) goto L_0x03eb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r10 = r5.updates
            r10.add(r4)
            r5.pts = r13
            int r4 = r5.pts_count
            int r4 = r4 + r14
            r5.pts_count = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r7.updates
            r10 = 1
            r4.remove(r10)
            r13 = 0
            goto L_0x03ad
        L_0x03eb:
            if (r3 != 0) goto L_0x04f0
            int r2 = r5.pts_count
            int r2 = r2 + r1
            int r3 = r5.pts
            if (r2 != r3) goto L_0x044e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r5.updates
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r7.chats
            r4 = 0
            int r10 = r7.date
            r0 = r23
            r13 = r5
            r5 = r10
            boolean r0 = r0.processUpdateArray(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x043c
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x041f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "need get channel diff inner TL_updates, channel_id = "
            r0.append(r1)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x041f:
            if (r8 != 0) goto L_0x0429
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            goto L_0x0508
        L_0x0429:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            boolean r0 = r8.contains(r0)
            if (r0 != 0) goto L_0x0508
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            r8.add(r0)
            goto L_0x0508
        L_0x043c:
            android.util.SparseIntArray r0 = r6.channelsPts
            int r1 = r13.pts
            r0.put(r9, r1)
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r13.pts
            r0.saveChannelPts(r9, r1)
            goto L_0x0508
        L_0x044e:
            r13 = r5
            if (r1 == r3) goto L_0x0508
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0484
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            java.lang.String r0 = " need get channel diff, pts: "
            r2.append(r0)
            r2.append(r1)
            r2.append(r15)
            int r0 = r13.pts
            r2.append(r0)
            r2.append(r11)
            int r0 = r13.pts_count
            r2.append(r0)
            java.lang.String r0 = " channelId = "
            r2.append(r0)
            r2.append(r9)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0484:
            org.telegram.messenger.support.SparseLongArray r0 = r6.updatesStartWaitTimeChannels
            long r0 = r0.get(r9)
            android.util.SparseBooleanArray r2 = r6.gettingDifferenceChannels
            boolean r2 = r2.get(r9)
            if (r2 != 0) goto L_0x04c2
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x04c2
            long r2 = java.lang.System.currentTimeMillis()
            long r2 = r2 - r0
            long r2 = java.lang.Math.abs(r2)
            r4 = 1500(0x5dc, double:7.41E-321)
            int r10 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r10 > 0) goto L_0x04a8
            goto L_0x04c2
        L_0x04a8:
            if (r8 != 0) goto L_0x04b0
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            goto L_0x0508
        L_0x04b0:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            boolean r0 = r8.contains(r0)
            if (r0 != 0) goto L_0x0508
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            r8.add(r0)
            goto L_0x0508
        L_0x04c2:
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x04d1
            org.telegram.messenger.support.SparseLongArray r0 = r6.updatesStartWaitTimeChannels
            long r1 = java.lang.System.currentTimeMillis()
            r0.put(r9, r1)
        L_0x04d1:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x04d8
            org.telegram.messenger.FileLog.d(r12)
        L_0x04d8:
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates>> r0 = r6.updatesQueueChannels
            java.lang.Object r0 = r0.get(r9)
            java.util.ArrayList r0 = (java.util.ArrayList) r0
            if (r0 != 0) goto L_0x04ec
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates>> r1 = r6.updatesQueueChannels
            r1.put(r9, r0)
        L_0x04ec:
            r0.add(r13)
            goto L_0x0508
        L_0x04f0:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0508
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "need load unknown channel = "
            r0.append(r1)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0508:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r7.updates
            r1 = 0
            r0.remove(r1)
            r3 = r22
            r9 = 0
            r10 = 1
            goto L_0x0127
        L_0x0514:
            r22 = r3
        L_0x0516:
            if (r22 == 0) goto L_0x0537
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r0 = r0.getLastSeqValue()
            r1 = 1
            int r0 = r0 + r1
            int r1 = r7.seq_start
            if (r0 == r1) goto L_0x0535
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r0 = r0.getLastSeqValue()
            int r1 = r7.seq_start
            if (r0 != r1) goto L_0x0533
            goto L_0x0535
        L_0x0533:
            r0 = 0
            goto L_0x0552
        L_0x0535:
            r0 = 1
            goto L_0x0552
        L_0x0537:
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r0 = r0.getLastSeqValue()
            r1 = 1
            int r0 = r0 + r1
            int r1 = r7.seq
            if (r0 == r1) goto L_0x0535
            if (r1 == 0) goto L_0x0535
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r0 = r0.getLastSeqValue()
            if (r1 != r0) goto L_0x0533
            goto L_0x0535
        L_0x0552:
            if (r0 == 0) goto L_0x057e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r7.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r7.chats
            r4 = 0
            int r5 = r7.date
            r0 = r23
            r0.processUpdateArray(r1, r2, r3, r4, r5)
            int r0 = r7.seq
            if (r0 == 0) goto L_0x0609
            int r0 = r7.date
            if (r0 == 0) goto L_0x0573
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r7.date
            r0.setLastDateValue(r1)
        L_0x0573:
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r1 = r7.seq
            r0.setLastSeqValue(r1)
            goto L_0x0609
        L_0x057e:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05cd
            if (r22 == 0) goto L_0x05a9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "need get diff TL_updatesCombined, seq: "
            r0.append(r1)
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastSeqValue()
            r0.append(r1)
            r0.append(r15)
            int r1 = r7.seq_start
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x05cd
        L_0x05a9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "need get diff TL_updates, seq: "
            r0.append(r1)
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastSeqValue()
            r0.append(r1)
            r0.append(r15)
            int r1 = r7.seq
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x05cd:
            boolean r0 = r6.gettingDifference
            if (r0 != 0) goto L_0x05ed
            long r0 = r6.updatesStartWaitTimeSeq
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x05ed
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r6.updatesStartWaitTimeSeq
            long r0 = r0 - r2
            long r0 = java.lang.Math.abs(r0)
            r2 = 1500(0x5dc, double:7.41E-321)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x05eb
            goto L_0x05ed
        L_0x05eb:
            r9 = 1
            goto L_0x060f
        L_0x05ed:
            long r0 = r6.updatesStartWaitTimeSeq
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x05fb
            long r0 = java.lang.System.currentTimeMillis()
            r6.updatesStartWaitTimeSeq = r0
        L_0x05fb:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0604
            java.lang.String r0 = "add TL_updates/Combined to queue"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0604:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates> r0 = r6.updatesQueueSeq
            r0.add(r7)
        L_0x0609:
            r9 = r18
            goto L_0x060f
        L_0x060c:
            r9 = r0
            r19 = 0
        L_0x060f:
            r0 = 0
            goto L_0x0939
        L_0x0612:
            if (r0 == 0) goto L_0x0617
            int r0 = r7.from_id
            goto L_0x0619
        L_0x0617:
            int r0 = r7.user_id
        L_0x0619:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r1 = r6.getUser(r1)
            if (r1 == 0) goto L_0x0627
            boolean r2 = r1.min
            if (r2 == 0) goto L_0x063a
        L_0x0627:
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r1 = r1.getUserSync(r0)
            if (r1 == 0) goto L_0x0636
            boolean r2 = r1.min
            if (r2 == 0) goto L_0x0636
            r1 = r8
        L_0x0636:
            r2 = 1
            r6.putUser(r1, r2)
        L_0x063a:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r7.fwd_from
            if (r2 == 0) goto L_0x0684
            int r2 = r2.from_id
            if (r2 == 0) goto L_0x065e
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r6.getUser(r2)
            if (r2 != 0) goto L_0x065c
            org.telegram.messenger.MessagesStorage r2 = r23.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r7.fwd_from
            int r3 = r3.from_id
            org.telegram.tgnet.TLRPC$User r2 = r2.getUserSync(r3)
            r3 = 1
            r6.putUser(r2, r3)
        L_0x065c:
            r9 = 1
            goto L_0x0660
        L_0x065e:
            r2 = r8
            r9 = 0
        L_0x0660:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r7.fwd_from
            int r3 = r3.channel_id
            if (r3 == 0) goto L_0x0682
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r6.getChat(r3)
            if (r3 != 0) goto L_0x0680
            org.telegram.messenger.MessagesStorage r3 = r23.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r7.fwd_from
            int r4 = r4.channel_id
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChatSync(r4)
            r4 = 1
            r6.putChat(r3, r4)
        L_0x0680:
            r9 = 1
            goto L_0x0687
        L_0x0682:
            r3 = r8
            goto L_0x0687
        L_0x0684:
            r2 = r8
            r3 = r2
            r9 = 0
        L_0x0687:
            int r4 = r7.via_bot_id
            if (r4 == 0) goto L_0x06a6
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r4 = r6.getUser(r4)
            if (r4 != 0) goto L_0x06a3
            org.telegram.messenger.MessagesStorage r4 = r23.getMessagesStorage()
            int r5 = r7.via_bot_id
            org.telegram.tgnet.TLRPC$User r4 = r4.getUserSync(r5)
            r5 = 1
            r6.putUser(r4, r5)
        L_0x06a3:
            r5 = r4
            r4 = 1
            goto L_0x06a8
        L_0x06a6:
            r5 = r8
            r4 = 0
        L_0x06a8:
            boolean r10 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage
            if (r10 == 0) goto L_0x06bd
            if (r1 == 0) goto L_0x06bb
            if (r9 == 0) goto L_0x06b4
            if (r2 != 0) goto L_0x06b4
            if (r3 == 0) goto L_0x06bb
        L_0x06b4:
            if (r4 == 0) goto L_0x06b9
            if (r5 != 0) goto L_0x06b9
            goto L_0x06bb
        L_0x06b9:
            r2 = 0
            goto L_0x06e6
        L_0x06bb:
            r2 = 1
            goto L_0x06e6
        L_0x06bd:
            int r13 = r7.chat_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r13 = r6.getChat(r13)
            if (r13 != 0) goto L_0x06d7
            org.telegram.messenger.MessagesStorage r13 = r23.getMessagesStorage()
            int r14 = r7.chat_id
            org.telegram.tgnet.TLRPC$Chat r13 = r13.getChatSync(r14)
            r14 = 1
            r6.putChat(r13, r14)
        L_0x06d7:
            if (r13 == 0) goto L_0x06bb
            if (r1 == 0) goto L_0x06bb
            if (r9 == 0) goto L_0x06e1
            if (r2 != 0) goto L_0x06e1
            if (r3 == 0) goto L_0x06bb
        L_0x06e1:
            if (r4 == 0) goto L_0x06b9
            if (r5 != 0) goto L_0x06b9
            goto L_0x06bb
        L_0x06e6:
            if (r2 != 0) goto L_0x0731
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r7.entities
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0731
            r3 = 0
        L_0x06f1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r7.entities
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0731
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r7.entities
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$MessageEntity r4 = (org.telegram.tgnet.TLRPC.MessageEntity) r4
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName
            if (r5 == 0) goto L_0x072e
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r4 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r4
            int r4 = r4.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r5 = r6.getUser(r5)
            if (r5 == 0) goto L_0x0717
            boolean r5 = r5.min
            if (r5 == 0) goto L_0x072e
        L_0x0717:
            org.telegram.messenger.MessagesStorage r5 = r23.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r4 = r5.getUserSync(r4)
            if (r4 == 0) goto L_0x0726
            boolean r5 = r4.min
            if (r5 == 0) goto L_0x0726
            r4 = r8
        L_0x0726:
            if (r4 != 0) goto L_0x072a
            r2 = 1
            goto L_0x0731
        L_0x072a:
            r4 = 1
            r6.putUser(r1, r4)
        L_0x072e:
            int r3 = r3 + 1
            goto L_0x06f1
        L_0x0731:
            boolean r3 = r7.out
            if (r3 != 0) goto L_0x0765
            if (r1 == 0) goto L_0x0765
            org.telegram.tgnet.TLRPC$UserStatus r3 = r1.status
            if (r3 == 0) goto L_0x0765
            int r3 = r3.expires
            if (r3 > 0) goto L_0x0765
            org.telegram.tgnet.ConnectionsManager r3 = r23.getConnectionsManager()
            int r3 = r3.getCurrentTime()
            int r4 = r7.date
            int r3 = r3 - r4
            int r3 = java.lang.Math.abs(r3)
            r4 = 30
            if (r3 >= r4) goto L_0x0765
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r3 = r6.onlinePrivacy
            int r1 = r1.id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            int r4 = r7.date
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3.put(r1, r4)
            r9 = 1
            goto L_0x0766
        L_0x0765:
            r9 = 0
        L_0x0766:
            if (r2 == 0) goto L_0x076b
            r2 = 1
            goto L_0x0935
        L_0x076b:
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastPtsValue()
            int r2 = r7.pts_count
            int r1 = r1 + r2
            int r2 = r7.pts
            if (r1 != r2) goto L_0x08c0
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message
            r1.<init>()
            int r2 = r7.id
            r1.id = r2
            org.telegram.messenger.UserConfig r2 = r23.getUserConfig()
            int r2 = r2.getClientUserId()
            if (r10 == 0) goto L_0x07a5
            boolean r3 = r7.out
            if (r3 == 0) goto L_0x0794
            r1.from_id = r2
            goto L_0x0796
        L_0x0794:
            r1.from_id = r0
        L_0x0796:
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r3.<init>()
            r1.to_id = r3
            org.telegram.tgnet.TLRPC$Peer r3 = r1.to_id
            r3.user_id = r0
            long r3 = (long) r0
            r1.dialog_id = r3
            goto L_0x07b8
        L_0x07a5:
            r1.from_id = r0
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat
            r3.<init>()
            r1.to_id = r3
            org.telegram.tgnet.TLRPC$Peer r3 = r1.to_id
            int r4 = r7.chat_id
            r3.chat_id = r4
            int r3 = -r4
            long r3 = (long) r3
            r1.dialog_id = r3
        L_0x07b8:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r7.fwd_from
            r1.fwd_from = r3
            boolean r3 = r7.silent
            r1.silent = r3
            boolean r3 = r7.out
            r1.out = r3
            boolean r3 = r7.mentioned
            r1.mentioned = r3
            boolean r3 = r7.media_unread
            r1.media_unread = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r7.entities
            r1.entities = r3
            java.lang.String r3 = r7.message
            r1.message = r3
            int r3 = r7.date
            r1.date = r3
            int r3 = r7.via_bot_id
            r1.via_bot_id = r3
            int r3 = r7.flags
            r3 = r3 | 256(0x100, float:3.59E-43)
            r1.flags = r3
            int r3 = r7.reply_to_msg_id
            r1.reply_to_msg_id = r3
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r3.<init>()
            r1.media = r3
            boolean r3 = r1.out
            if (r3 == 0) goto L_0x07f4
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r6.dialogs_read_outbox_max
            goto L_0x07f6
        L_0x07f4:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r6.dialogs_read_inbox_max
        L_0x07f6:
            long r4 = r1.dialog_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            java.lang.Object r4 = r3.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 != 0) goto L_0x081d
            org.telegram.messenger.MessagesStorage r4 = r23.getMessagesStorage()
            boolean r5 = r1.out
            long r11 = r1.dialog_id
            int r4 = r4.getDialogReadMax(r5, r11)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            long r11 = r1.dialog_id
            java.lang.Long r5 = java.lang.Long.valueOf(r11)
            r3.put(r5, r4)
        L_0x081d:
            int r3 = r4.intValue()
            int r4 = r1.id
            if (r3 >= r4) goto L_0x0827
            r3 = 1
            goto L_0x0828
        L_0x0827:
            r3 = 0
        L_0x0828:
            r1.unread = r3
            long r3 = r1.dialog_id
            long r11 = (long) r2
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x083a
            r2 = 0
            r1.unread = r2
            r1.media_unread = r2
            r2 = 1
            r1.out = r2
            goto L_0x083b
        L_0x083a:
            r2 = 1
        L_0x083b:
            org.telegram.messenger.MessagesStorage r3 = r23.getMessagesStorage()
            int r4 = r7.pts
            r3.setLastPtsValue(r4)
            org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject
            int r4 = r6.currentAccount
            java.util.ArrayList<java.lang.Long> r5 = r6.createdDialogIds
            long r11 = r1.dialog_id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            boolean r5 = r5.contains(r11)
            r3.<init>(r4, r1, r5)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r4.add(r3)
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r12.add(r1)
            if (r10 == 0) goto L_0x0886
            boolean r1 = r7.out
            if (r1 != 0) goto L_0x0877
            int r1 = r7.user_id
            long r10 = (long) r1
            boolean r1 = r6.updatePrintingUsersWithNewMessages(r10, r4)
            if (r1 == 0) goto L_0x0877
            goto L_0x0878
        L_0x0877:
            r2 = 0
        L_0x0878:
            if (r2 == 0) goto L_0x087d
            r23.updatePrintingStrings()
        L_0x087d:
            org.telegram.messenger.-$$Lambda$MessagesController$eEjbksoOfkx6xTQV2_OzG5pnRpU r1 = new org.telegram.messenger.-$$Lambda$MessagesController$eEjbksoOfkx6xTQV2_OzG5pnRpU
            r1.<init>(r2, r0, r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x089b
        L_0x0886:
            int r0 = r7.chat_id
            int r0 = -r0
            long r0 = (long) r0
            boolean r0 = r6.updatePrintingUsersWithNewMessages(r0, r4)
            if (r0 == 0) goto L_0x0893
            r23.updatePrintingStrings()
        L_0x0893:
            org.telegram.messenger.-$$Lambda$MessagesController$YKPZ7XgAhwef5KnSIvIkaDgL_A0 r1 = new org.telegram.messenger.-$$Lambda$MessagesController$YKPZ7XgAhwef5KnSIvIkaDgL_A0
            r1.<init>(r0, r7, r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x089b:
            boolean r0 = r3.isOut()
            if (r0 != 0) goto L_0x08b1
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r0 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$W3uVVOb0uoxODPQw9DMQq3gOXyM r1 = new org.telegram.messenger.-$$Lambda$MessagesController$W3uVVOb0uoxODPQw9DMQq3gOXyM
            r1.<init>(r4)
            r0.postRunnable(r1)
        L_0x08b1:
            org.telegram.messenger.MessagesStorage r11 = r23.getMessagesStorage()
            r13 = 0
            r14 = 1
            r15 = 0
            r16 = 0
            r17 = 0
            r11.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r12, (boolean) r13, (boolean) r14, (boolean) r15, (int) r16, (boolean) r17)
            goto L_0x0934
        L_0x08c0:
            r2 = 1
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            int r0 = r0.getLastPtsValue()
            int r1 = r7.pts
            if (r0 == r1) goto L_0x0934
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x08fd
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "need get diff short message, pts: "
            r0.append(r1)
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastPtsValue()
            r0.append(r1)
            r0.append(r15)
            int r1 = r7.pts
            r0.append(r1)
            r0.append(r11)
            int r1 = r7.pts_count
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x08fd:
            boolean r0 = r6.gettingDifference
            if (r0 != 0) goto L_0x091a
            long r0 = r6.updatesStartWaitTimePts
            r3 = 0
            int r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x091a
            long r0 = java.lang.System.currentTimeMillis()
            long r3 = r6.updatesStartWaitTimePts
            long r0 = r0 - r3
            long r0 = java.lang.Math.abs(r0)
            r3 = 1500(0x5dc, double:7.41E-321)
            int r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r5 > 0) goto L_0x0935
        L_0x091a:
            long r0 = r6.updatesStartWaitTimePts
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0928
            long r0 = java.lang.System.currentTimeMillis()
            r6.updatesStartWaitTimePts = r0
        L_0x0928:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x092f
            org.telegram.messenger.FileLog.d(r12)
        L_0x092f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates> r0 = r6.updatesQueuePts
            r0.add(r7)
        L_0x0934:
            r2 = 0
        L_0x0935:
            r0 = r9
            r19 = 0
            r9 = r2
        L_0x0939:
            org.telegram.messenger.SecretChatHelper r1 = r23.getSecretChatHelper()
            r1.processPendingEncMessages()
            if (r25 != 0) goto L_0x097a
            r1 = 0
        L_0x0943:
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates>> r2 = r6.updatesQueueChannels
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0969
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Updates>> r2 = r6.updatesQueueChannels
            int r2 = r2.keyAt(r1)
            if (r8 == 0) goto L_0x0962
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            boolean r3 = r8.contains(r3)
            if (r3 == 0) goto L_0x0962
            r6.getChannelDifference(r2)
            r3 = 0
            goto L_0x0966
        L_0x0962:
            r3 = 0
            r6.processChannelsUpdatesQueue(r2, r3)
        L_0x0966:
            int r1 = r1 + 1
            goto L_0x0943
        L_0x0969:
            r3 = 0
            if (r9 == 0) goto L_0x0970
            r23.getDifference()
            goto L_0x097a
        L_0x0970:
            r1 = 0
        L_0x0971:
            r2 = 3
            if (r1 >= r2) goto L_0x097a
            r6.processUpdatesQueue(r1, r3)
            int r1 = r1 + 1
            goto L_0x0971
        L_0x097a:
            if (r19 == 0) goto L_0x0994
            org.telegram.tgnet.TLRPC$TL_messages_receivedQueue r1 = new org.telegram.tgnet.TLRPC$TL_messages_receivedQueue
            r1.<init>()
            org.telegram.messenger.MessagesStorage r2 = r23.getMessagesStorage()
            int r2 = r2.getLastQtsValue()
            r1.max_qts = r2
            org.telegram.tgnet.ConnectionsManager r2 = r23.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$41Nrry903aXeWspe_mkLTT84RF4 r3 = org.telegram.messenger.$$Lambda$MessagesController$41Nrry903aXeWspe_mkLTT84RF4.INSTANCE
            r2.sendRequest(r1, r3)
        L_0x0994:
            if (r0 == 0) goto L_0x099e
            org.telegram.messenger.-$$Lambda$MessagesController$guZRpI4v5zfmsZAbWkFbheWJ3nw r0 = new org.telegram.messenger.-$$Lambda$MessagesController$guZRpI4v5zfmsZAbWkFbheWJ3nw
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x099e:
            org.telegram.messenger.MessagesStorage r0 = r23.getMessagesStorage()
            org.telegram.messenger.MessagesStorage r1 = r23.getMessagesStorage()
            int r1 = r1.getLastSeqValue()
            org.telegram.messenger.MessagesStorage r2 = r23.getMessagesStorage()
            int r2 = r2.getLastPtsValue()
            org.telegram.messenger.MessagesStorage r3 = r23.getMessagesStorage()
            int r3 = r3.getLastDateValue()
            org.telegram.messenger.MessagesStorage r4 = r23.getMessagesStorage()
            int r4 = r4.getLastQtsValue()
            r0.saveDiffParams(r1, r2, r3, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    public /* synthetic */ void lambda$processUpdates$250$MessagesController(boolean z, int i, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 64);
        }
        updateInterfaceWithMessages((long) i, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdates$251$MessagesController(boolean z, TLRPC.Updates updates, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 64);
        }
        updateInterfaceWithMessages((long) (-updates.chat_id), arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$252$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, (CountDownLatch) null);
    }

    public /* synthetic */ void lambda$processUpdates$253$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$252$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processUpdates$255$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 4);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1474, code lost:
        if (r5 != null) goto L_0x1479;
     */
    /* JADX WARNING: Removed duplicated region for block: B:725:0x1062  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x10ed  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x1282  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x128f  */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x12ad  */
    /* JADX WARNING: Removed duplicated region for block: B:839:0x12b0  */
    /* JADX WARNING: Removed duplicated region for block: B:843:0x12b8  */
    /* JADX WARNING: Removed duplicated region for block: B:844:0x12be  */
    /* JADX WARNING: Removed duplicated region for block: B:847:0x12c9  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x12ea  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x12f0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processUpdateArray(java.util.ArrayList<org.telegram.tgnet.TLRPC.Update> r55, java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r56, java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat> r57, boolean r58, int r59) {
        /*
            r54 = this;
            r13 = r54
            r0 = r56
            r1 = r57
            boolean r2 = r55.isEmpty()
            r14 = 1
            if (r2 == 0) goto L_0x001a
            if (r0 != 0) goto L_0x0011
            if (r1 == 0) goto L_0x0019
        L_0x0011:
            org.telegram.messenger.-$$Lambda$MessagesController$96FGA5dNZDEhfxY_JPUPryeGAxo r2 = new org.telegram.messenger.-$$Lambda$MessagesController$96FGA5dNZDEhfxY_JPUPryeGAxo
            r2.<init>(r0, r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x0019:
            return r14
        L_0x001a:
            long r2 = java.lang.System.currentTimeMillis()
            if (r0 == 0) goto L_0x0041
            java.util.concurrent.ConcurrentHashMap r4 = new java.util.concurrent.ConcurrentHashMap
            r4.<init>()
            int r5 = r56.size()
            r6 = 0
        L_0x002a:
            if (r6 >= r5) goto L_0x003e
            java.lang.Object r7 = r0.get(r6)
            org.telegram.tgnet.TLRPC$User r7 = (org.telegram.tgnet.TLRPC.User) r7
            int r8 = r7.id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r4.put(r8, r7)
            int r6 = r6 + 1
            goto L_0x002a
        L_0x003e:
            r11 = r4
            r4 = 1
            goto L_0x0045
        L_0x0041:
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r4 = r13.users
            r11 = r4
            r4 = 0
        L_0x0045:
            if (r1 == 0) goto L_0x0067
            java.util.concurrent.ConcurrentHashMap r5 = new java.util.concurrent.ConcurrentHashMap
            r5.<init>()
            int r6 = r57.size()
            r7 = 0
        L_0x0051:
            if (r7 >= r6) goto L_0x0065
            java.lang.Object r8 = r1.get(r7)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
            int r9 = r8.id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r5.put(r9, r8)
            int r7 = r7 + 1
            goto L_0x0051
        L_0x0065:
            r12 = r5
            goto L_0x006b
        L_0x0067:
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$Chat> r4 = r13.chats
            r12 = r4
            r4 = 0
        L_0x006b:
            if (r58 == 0) goto L_0x006e
            r4 = 0
        L_0x006e:
            if (r0 != 0) goto L_0x0072
            if (r1 == 0) goto L_0x007a
        L_0x0072:
            org.telegram.messenger.-$$Lambda$MessagesController$Rnq16UxMC9aNdrPpQ47iT5M3qV0 r5 = new org.telegram.messenger.-$$Lambda$MessagesController$Rnq16UxMC9aNdrPpQ47iT5M3qV0
            r5.<init>(r0, r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5)
        L_0x007a:
            int r0 = r55.size()
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r14 = 0
            r16 = 0
            r18 = 0
            r24 = 0
            r26 = 0
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
        L_0x00a5:
            if (r10 >= r0) goto L_0x131c
            r1 = r55
            java.lang.Object r17 = r1.get(r10)
            r15 = r17
            org.telegram.tgnet.TLRPC$Update r15 = (org.telegram.tgnet.TLRPC.Update) r15
            boolean r17 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r57 = r0
            if (r17 == 0) goto L_0x00cb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "process update "
            r0.append(r1)
            r0.append(r15)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x00cb:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage
            r20 = r2
            java.lang.String r3 = " channelId = "
            if (r0 != 0) goto L_0x0f5c
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage
            if (r1 != 0) goto L_0x0f5c
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage
            if (r1 == 0) goto L_0x00dd
            goto L_0x0f5c
        L_0x00dd:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents
            if (r0 == 0) goto L_0x0112
            org.telegram.tgnet.TLRPC$TL_updateReadMessagesContents r15 = (org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents) r15
            if (r5 != 0) goto L_0x00ea
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
        L_0x00ea:
            java.util.ArrayList<java.lang.Integer> r0 = r15.messages
            int r0 = r0.size()
            r1 = 0
        L_0x00f1:
            if (r1 >= r0) goto L_0x010a
            java.util.ArrayList<java.lang.Integer> r2 = r15.messages
            java.lang.Object r2 = r2.get(r1)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            long r2 = (long) r2
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r5.add(r2)
            int r1 = r1 + 1
            goto L_0x00f1
        L_0x010a:
            r25 = r4
        L_0x010c:
            r43 = r10
            r4 = r11
        L_0x010f:
            r3 = 0
            goto L_0x1311
        L_0x0112:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents
            if (r0 == 0) goto L_0x014d
            org.telegram.tgnet.TLRPC$TL_updateChannelReadMessagesContents r15 = (org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents) r15
            if (r5 != 0) goto L_0x011f
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
        L_0x011f:
            java.util.ArrayList<java.lang.Integer> r0 = r15.messages
            int r0 = r0.size()
            r2 = 0
        L_0x0126:
            if (r2 >= r0) goto L_0x010a
            java.util.ArrayList<java.lang.Integer> r3 = r15.messages
            java.lang.Object r3 = r3.get(r2)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r17 = r2
            long r1 = (long) r3
            int r3 = r15.channel_id
            r25 = r4
            long r3 = (long) r3
            r19 = 32
            long r3 = r3 << r19
            long r1 = r1 | r3
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r5.add(r1)
            int r2 = r17 + 1
            r4 = r25
            goto L_0x0126
        L_0x014d:
            r25 = r4
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox
            if (r0 == 0) goto L_0x01b0
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r15 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox) r15
            if (r14 != 0) goto L_0x015c
            org.telegram.messenger.support.SparseLongArray r14 = new org.telegram.messenger.support.SparseLongArray
            r14.<init>()
        L_0x015c:
            org.telegram.tgnet.TLRPC$Peer r0 = r15.peer
            int r1 = r0.chat_id
            if (r1 == 0) goto L_0x016f
            int r0 = -r1
            int r1 = r15.max_id
            long r1 = (long) r1
            r14.put(r0, r1)
            org.telegram.tgnet.TLRPC$Peer r0 = r15.peer
            int r0 = r0.chat_id
            int r0 = -r0
            goto L_0x017b
        L_0x016f:
            int r0 = r0.user_id
            int r1 = r15.max_id
            long r1 = (long) r1
            r14.put(r0, r1)
            org.telegram.tgnet.TLRPC$Peer r0 = r15.peer
            int r0 = r0.user_id
        L_0x017b:
            long r0 = (long) r0
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r13.dialogs_read_inbox_max
            java.lang.Long r3 = java.lang.Long.valueOf(r0)
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 != 0) goto L_0x0197
            org.telegram.messenger.MessagesStorage r2 = r54.getMessagesStorage()
            r3 = 0
            int r2 = r2.getDialogReadMax(r3, r0)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
        L_0x0197:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r13.dialogs_read_inbox_max
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            int r1 = r2.intValue()
            int r2 = r15.max_id
            int r1 = java.lang.Math.max(r1, r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r3.put(r0, r1)
            goto L_0x010c
        L_0x01b0:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox
            if (r0 == 0) goto L_0x0214
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryOutbox r15 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox) r15
            r4 = r27
            if (r4 != 0) goto L_0x01c1
            org.telegram.messenger.support.SparseLongArray r27 = new org.telegram.messenger.support.SparseLongArray
            r27.<init>()
            r4 = r27
        L_0x01c1:
            org.telegram.tgnet.TLRPC$Peer r0 = r15.peer
            int r1 = r0.chat_id
            if (r1 == 0) goto L_0x01d4
            int r0 = -r1
            int r1 = r15.max_id
            long r1 = (long) r1
            r4.put(r0, r1)
            org.telegram.tgnet.TLRPC$Peer r0 = r15.peer
            int r0 = r0.chat_id
            int r0 = -r0
            goto L_0x01e0
        L_0x01d4:
            int r0 = r0.user_id
            int r1 = r15.max_id
            long r1 = (long) r1
            r4.put(r0, r1)
            org.telegram.tgnet.TLRPC$Peer r0 = r15.peer
            int r0 = r0.user_id
        L_0x01e0:
            long r0 = (long) r0
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r13.dialogs_read_outbox_max
            java.lang.Long r3 = java.lang.Long.valueOf(r0)
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 != 0) goto L_0x01fc
            org.telegram.messenger.MessagesStorage r2 = r54.getMessagesStorage()
            r3 = 1
            int r2 = r2.getDialogReadMax(r3, r0)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
        L_0x01fc:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r13.dialogs_read_outbox_max
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            int r1 = r2.intValue()
            int r2 = r15.max_id
            int r1 = java.lang.Math.max(r1, r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r3.put(r0, r1)
            goto L_0x023f
        L_0x0214:
            r4 = r27
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteMessages
            if (r0 == 0) goto L_0x0243
            org.telegram.tgnet.TLRPC$TL_updateDeleteMessages r15 = (org.telegram.tgnet.TLRPC.TL_updateDeleteMessages) r15
            r2 = r32
            if (r2 != 0) goto L_0x0227
            android.util.SparseArray r32 = new android.util.SparseArray
            r32.<init>()
            r2 = r32
        L_0x0227:
            r0 = 0
            java.lang.Object r1 = r2.get(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 != 0) goto L_0x0238
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2.put(r0, r1)
        L_0x0238:
            java.util.ArrayList<java.lang.Integer> r0 = r15.messages
            r1.addAll(r0)
        L_0x023d:
            r32 = r2
        L_0x023f:
            r27 = r4
            goto L_0x010c
        L_0x0243:
            r2 = r32
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteScheduledMessages
            if (r0 == 0) goto L_0x027f
            org.telegram.tgnet.TLRPC$TL_updateDeleteScheduledMessages r15 = (org.telegram.tgnet.TLRPC.TL_updateDeleteScheduledMessages) r15
            r1 = r33
            if (r1 != 0) goto L_0x0256
            android.util.SparseArray r33 = new android.util.SparseArray
            r33.<init>()
            r1 = r33
        L_0x0256:
            org.telegram.tgnet.TLRPC$Peer r0 = r15.peer
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel
            if (r3 == 0) goto L_0x0265
            int r0 = r0.channel_id
            java.lang.Object r3 = r1.get(r0)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            goto L_0x026d
        L_0x0265:
            r0 = 0
            java.lang.Object r3 = r1.get(r0)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            r0 = 0
        L_0x026d:
            if (r3 != 0) goto L_0x0277
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r1.put(r0, r3)
        L_0x0277:
            java.util.ArrayList<java.lang.Integer> r0 = r15.messages
            r3.addAll(r0)
            r33 = r1
            goto L_0x023d
        L_0x027f:
            r1 = r33
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserTyping
            r27 = r1
            if (r0 != 0) goto L_0x0e35
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatUserTyping
            if (r1 == 0) goto L_0x028d
            goto L_0x0e35
        L_0x028d:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipants
            if (r0 == 0) goto L_0x02b1
            org.telegram.tgnet.TLRPC$TL_updateChatParticipants r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipants) r15
            r8 = r8 | 32
            if (r35 != 0) goto L_0x029c
            java.util.ArrayList r35 = new java.util.ArrayList
            r35.<init>()
        L_0x029c:
            r0 = r35
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r15.participants
            r0.add(r1)
            r35 = r0
        L_0x02a5:
            r32 = r2
            r43 = r10
            r33 = r27
            r3 = 0
        L_0x02ac:
            r27 = r4
            r4 = r11
            goto L_0x1311
        L_0x02b1:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserStatus
            if (r0 == 0) goto L_0x02c6
            r8 = r8 | 4
            if (r36 != 0) goto L_0x02be
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x02be:
            r0 = r36
            r0.add(r15)
        L_0x02c3:
            r36 = r0
            goto L_0x02a5
        L_0x02c6:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName
            if (r0 == 0) goto L_0x02d9
            r8 = r8 | 1
            if (r36 != 0) goto L_0x02d3
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x02d3:
            r0 = r36
            r0.add(r15)
            goto L_0x02c3
        L_0x02d9:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhoto
            if (r0 == 0) goto L_0x02f8
            r0 = r15
            org.telegram.tgnet.TLRPC$TL_updateUserPhoto r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhoto) r0
            r8 = r8 | 2
            org.telegram.messenger.MessagesStorage r1 = r54.getMessagesStorage()
            int r0 = r0.user_id
            r1.clearUserPhotos(r0)
            if (r36 != 0) goto L_0x02f2
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x02f2:
            r0 = r36
            r0.add(r15)
            goto L_0x02c3
        L_0x02f8:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhone
            if (r0 == 0) goto L_0x030b
            r8 = r8 | 1024(0x400, float:1.435E-42)
            if (r36 != 0) goto L_0x0305
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0305:
            r0 = r36
            r0.add(r15)
            goto L_0x02c3
        L_0x030b:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerSettings
            if (r0 == 0) goto L_0x0397
            r0 = r15
            org.telegram.tgnet.TLRPC$TL_updatePeerSettings r0 = (org.telegram.tgnet.TLRPC.TL_updatePeerSettings) r0
            if (r7 != 0) goto L_0x0319
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
        L_0x0319:
            org.telegram.tgnet.TLRPC$Peer r1 = r0.peer
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerUser
            if (r3 == 0) goto L_0x0389
            int r1 = r1.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Object r1 = r11.get(r1)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            if (r1 == 0) goto L_0x0389
            boolean r1 = r1.contact
            r3 = -1
            if (r1 == 0) goto L_0x035e
            org.telegram.tgnet.TLRPC$Peer r1 = r0.peer
            int r1 = r1.user_id
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            int r1 = r7.indexOf(r1)
            if (r1 == r3) goto L_0x0344
            r7.remove(r1)
        L_0x0344:
            org.telegram.tgnet.TLRPC$Peer r1 = r0.peer
            int r1 = r1.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r1 = r7.contains(r1)
            if (r1 != 0) goto L_0x0389
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r7.add(r0)
            goto L_0x0389
        L_0x035e:
            org.telegram.tgnet.TLRPC$Peer r1 = r0.peer
            int r1 = r1.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            int r1 = r7.indexOf(r1)
            if (r1 == r3) goto L_0x036f
            r7.remove(r1)
        L_0x036f:
            org.telegram.tgnet.TLRPC$Peer r1 = r0.peer
            int r1 = r1.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r1 = r7.contains(r1)
            if (r1 != 0) goto L_0x0389
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer
            int r0 = r0.user_id
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r7.add(r0)
        L_0x0389:
            if (r36 != 0) goto L_0x0390
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0390:
            r0 = r36
            r0.add(r15)
            goto L_0x02c3
        L_0x0397:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage
            if (r0 == 0) goto L_0x0472
            org.telegram.messenger.SecretChatHelper r0 = r54.getSecretChatHelper()
            org.telegram.tgnet.TLRPC$TL_updateNewEncryptedMessage r15 = (org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage) r15
            org.telegram.tgnet.TLRPC$EncryptedMessage r1 = r15.message
            java.util.ArrayList r0 = r0.decryptMessage(r1)
            if (r0 == 0) goto L_0x045b
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x045b
            org.telegram.tgnet.TLRPC$EncryptedMessage r1 = r15.message
            int r1 = r1.chat_id
            r33 = r5
            r32 = r6
            long r5 = (long) r1
            r1 = 32
            long r5 = r5 << r1
            if (r9 != 0) goto L_0x03c2
            android.util.LongSparseArray r9 = new android.util.LongSparseArray
            r9.<init>()
        L_0x03c2:
            r1 = r9
            java.lang.Object r3 = r1.get(r5)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            if (r3 != 0) goto L_0x03d3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r1.put(r5, r3)
        L_0x03d3:
            int r15 = r0.size()
            r9 = 0
        L_0x03d8:
            if (r9 >= r15) goto L_0x044f
            java.lang.Object r17 = r0.get(r9)
            r19 = r0
            r0 = r17
            org.telegram.tgnet.TLRPC$Message r0 = (org.telegram.tgnet.TLRPC.Message) r0
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r0)
            if (r24 != 0) goto L_0x03f5
            java.util.ArrayList r17 = new java.util.ArrayList
            r17.<init>()
            r53 = r17
            r17 = r1
            r1 = r53
            goto L_0x03f9
        L_0x03f5:
            r17 = r1
            r1 = r24
        L_0x03f9:
            r1.add(r0)
            r22 = r1
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject
            r23 = r7
            int r7 = r13.currentAccount
            r40 = r8
            java.util.ArrayList<java.lang.Long> r8 = r13.createdDialogIds
            r39 = r9
            java.lang.Long r9 = java.lang.Long.valueOf(r5)
            boolean r24 = r8.contains(r9)
            r41 = r5
            r9 = r33
            r5 = r1
            r6 = r7
            r8 = r23
            r7 = r0
            r0 = r8
            r33 = r40
            r8 = r11
            r40 = r9
            r23 = r39
            r9 = r12
            r43 = r10
            r10 = r24
            r5.<init>((int) r6, (org.telegram.tgnet.TLRPC.Message) r7, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.User>) r8, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.Chat>) r9, (boolean) r10)
            r3.add(r1)
            if (r32 != 0) goto L_0x0436
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            goto L_0x0438
        L_0x0436:
            r5 = r32
        L_0x0438:
            r5.add(r1)
            int r9 = r23 + 1
            r7 = r0
            r32 = r5
            r1 = r17
            r0 = r19
            r24 = r22
            r8 = r33
            r33 = r40
            r5 = r41
            r10 = r43
            goto L_0x03d8
        L_0x044f:
            r17 = r1
            r0 = r7
            r43 = r10
            r40 = r33
            r33 = r8
            r9 = r17
            goto L_0x0464
        L_0x045b:
            r40 = r5
            r32 = r6
            r0 = r7
            r33 = r8
            r43 = r10
        L_0x0464:
            r7 = r0
            r6 = r32
            r8 = r33
            r5 = r40
        L_0x046b:
            r3 = 0
            r32 = r2
            r33 = r27
            goto L_0x02ac
        L_0x0472:
            r40 = r5
            r32 = r6
            r0 = r7
            r33 = r8
            r43 = r10
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping
            if (r1 == 0) goto L_0x0528
            org.telegram.tgnet.TLRPC$TL_updateEncryptedChatTyping r15 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping) r15
            int r1 = r15.chat_id
            r3 = 1
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r13.getEncryptedChatDB(r1, r3)
            if (r1 == 0) goto L_0x0517
            int r3 = r15.chat_id
            long r5 = (long) r3
            r3 = 32
            long r5 = r5 << r3
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> r3 = r13.printingUsers
            java.lang.Long r7 = java.lang.Long.valueOf(r5)
            java.lang.Object r3 = r3.get(r7)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            if (r3 != 0) goto L_0x04ac
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> r7 = r13.printingUsers
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r7.put(r5, r3)
        L_0x04ac:
            int r5 = r3.size()
            r6 = 0
        L_0x04b1:
            if (r6 >= r5) goto L_0x04d7
            java.lang.Object r7 = r3.get(r6)
            org.telegram.messenger.MessagesController$PrintingUser r7 = (org.telegram.messenger.MessagesController.PrintingUser) r7
            int r8 = r7.userId
            int r10 = r1.user_id
            if (r8 != r10) goto L_0x04ce
            r41 = r9
            r9 = r20
            r7.lastTime = r9
            org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction r5 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction
            r5.<init>()
            r7.action = r5
            r5 = 1
            goto L_0x04dc
        L_0x04ce:
            r41 = r9
            r9 = r20
            int r6 = r6 + 1
            r9 = r41
            goto L_0x04b1
        L_0x04d7:
            r41 = r9
            r9 = r20
            r5 = 0
        L_0x04dc:
            if (r5 != 0) goto L_0x04f5
            org.telegram.messenger.MessagesController$PrintingUser r5 = new org.telegram.messenger.MessagesController$PrintingUser
            r5.<init>()
            int r6 = r1.user_id
            r5.userId = r6
            r5.lastTime = r9
            org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction r6 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction
            r6.<init>()
            r5.action = r6
            r3.add(r5)
            r16 = 1
        L_0x04f5:
            org.telegram.tgnet.ConnectionsManager r3 = r54.getConnectionsManager()
            int r3 = r3.getCurrentTime()
            int r3 = r3 - r59
            int r3 = java.lang.Math.abs(r3)
            r5 = 30
            if (r3 >= r5) goto L_0x051b
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r3 = r13.onlinePrivacy
            int r1 = r1.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r59)
            r3.put(r1, r5)
            goto L_0x051b
        L_0x0517:
            r41 = r9
            r9 = r20
        L_0x051b:
            r7 = r0
        L_0x051c:
            r20 = r9
            r6 = r32
            r8 = r33
            r5 = r40
            r9 = r41
            goto L_0x046b
        L_0x0528:
            r41 = r9
            r9 = r20
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead
            if (r1 == 0) goto L_0x0558
            org.telegram.tgnet.TLRPC$TL_updateEncryptedMessagesRead r15 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r15
            r1 = r31
            if (r1 != 0) goto L_0x053d
            android.util.SparseIntArray r31 = new android.util.SparseIntArray
            r31.<init>()
            r1 = r31
        L_0x053d:
            int r3 = r15.chat_id
            int r5 = r15.max_date
            r1.put(r3, r5)
            r8 = r38
            if (r8 != 0) goto L_0x054f
            java.util.ArrayList r38 = new java.util.ArrayList
            r38.<init>()
            r8 = r38
        L_0x054f:
            r8.add(r15)
            r7 = r0
            r31 = r1
        L_0x0555:
            r38 = r8
            goto L_0x051c
        L_0x0558:
            r1 = r31
            r8 = r38
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd
            if (r5 == 0) goto L_0x0598
            org.telegram.tgnet.TLRPC$TL_updateChatParticipantAdd r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd) r15
            org.telegram.messenger.MessagesStorage r44 = r54.getMessagesStorage()
            int r3 = r15.chat_id
            int r5 = r15.user_id
            r47 = 0
            int r6 = r15.inviter_id
            int r7 = r15.version
            r45 = r3
            r46 = r5
            r48 = r6
            r49 = r7
            r44.updateChatInfo(r45, r46, r47, r48, r49)
        L_0x057b:
            r19 = r0
            r31 = r1
            r50 = r8
            r51 = r9
            r15 = r11
            r10 = r26
            r0 = r29
            r1 = r32
            r17 = r34
            r11 = r41
            r29 = r2
        L_0x0590:
            r53 = r30
            r30 = r14
            r14 = r53
            goto L_0x0e19
        L_0x0598:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete
            if (r5 == 0) goto L_0x05b6
            org.telegram.tgnet.TLRPC$TL_updateChatParticipantDelete r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete) r15
            org.telegram.messenger.MessagesStorage r44 = r54.getMessagesStorage()
            int r3 = r15.chat_id
            int r5 = r15.user_id
            r47 = 1
            r48 = 0
            int r6 = r15.version
            r45 = r3
            r46 = r5
            r49 = r6
            r44.updateChatInfo(r45, r46, r47, r48, r49)
            goto L_0x057b
        L_0x05b6:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDcOptions
            if (r5 != 0) goto L_0x0df7
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateConfig
            if (r5 == 0) goto L_0x05c0
            goto L_0x0df7
        L_0x05c0:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryption
            if (r5 == 0) goto L_0x05ce
            org.telegram.messenger.SecretChatHelper r3 = r54.getSecretChatHelper()
            org.telegram.tgnet.TLRPC$TL_updateEncryption r15 = (org.telegram.tgnet.TLRPC.TL_updateEncryption) r15
            r3.processUpdateEncryption(r15, r11)
            goto L_0x057b
        L_0x05ce:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserBlocked
            if (r5 == 0) goto L_0x05e5
            org.telegram.tgnet.TLRPC$TL_updateUserBlocked r15 = (org.telegram.tgnet.TLRPC.TL_updateUserBlocked) r15
            org.telegram.messenger.MessagesStorage r3 = r54.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r3 = r3.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$Gnr81CP5OzSDaKQVyhrL4AOWsFU r5 = new org.telegram.messenger.-$$Lambda$MessagesController$Gnr81CP5OzSDaKQVyhrL4AOWsFU
            r5.<init>(r15)
            r3.postRunnable(r5)
            goto L_0x057b
        L_0x05e5:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNotifySettings
            if (r5 == 0) goto L_0x05fc
            if (r36 != 0) goto L_0x05f0
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x05f0:
            r3 = r36
            r3.add(r15)
            r7 = r0
            r31 = r1
            r36 = r3
            goto L_0x0555
        L_0x05fc:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateServiceNotification
            if (r5 == 0) goto L_0x0704
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r15 = (org.telegram.tgnet.TLRPC.TL_updateServiceNotification) r15
            boolean r3 = r15.popup
            if (r3 == 0) goto L_0x0618
            java.lang.String r3 = r15.message
            if (r3 == 0) goto L_0x0618
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0618
            org.telegram.messenger.-$$Lambda$MessagesController$u9vf8aXfatYjuDXmDkbXgB2qhzw r3 = new org.telegram.messenger.-$$Lambda$MessagesController$u9vf8aXfatYjuDXmDkbXgB2qhzw
            r3.<init>(r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
        L_0x0618:
            int r3 = r15.flags
            r5 = 2
            r3 = r3 & r5
            if (r3 == 0) goto L_0x06ed
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message
            r3.<init>()
            org.telegram.messenger.UserConfig r5 = r54.getUserConfig()
            int r5 = r5.getNewMessageId()
            r3.id = r5
            r3.local_id = r5
            org.telegram.messenger.UserConfig r5 = r54.getUserConfig()
            r6 = 0
            r5.saveConfig(r6)
            r5 = 1
            r3.unread = r5
            r5 = 256(0x100, float:3.59E-43)
            r3.flags = r5
            int r5 = r15.inbox_date
            if (r5 == 0) goto L_0x0645
            r3.date = r5
            goto L_0x0650
        L_0x0645:
            long r5 = java.lang.System.currentTimeMillis()
            r19 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r19
            int r6 = (int) r5
            r3.date = r6
        L_0x0650:
            r5 = 777000(0xbdb28, float:1.088809E-39)
            r3.from_id = r5
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r5.<init>()
            r3.to_id = r5
            org.telegram.tgnet.TLRPC$Peer r5 = r3.to_id
            org.telegram.messenger.UserConfig r6 = r54.getUserConfig()
            int r6 = r6.getClientUserId()
            r5.user_id = r6
            r5 = 777000(0xbdb28, double:3.83889E-318)
            r3.dialog_id = r5
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r15.media
            if (r5 == 0) goto L_0x0679
            r3.media = r5
            int r5 = r3.flags
            r5 = r5 | 512(0x200, float:7.175E-43)
            r3.flags = r5
        L_0x0679:
            java.lang.String r5 = r15.message
            r3.message = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r15.entities
            if (r5 == 0) goto L_0x0689
            r3.entities = r5
            int r5 = r3.flags
            r5 = r5 | 128(0x80, float:1.794E-43)
            r3.flags = r5
        L_0x0689:
            if (r24 != 0) goto L_0x0690
            java.util.ArrayList r24 = new java.util.ArrayList
            r24.<init>()
        L_0x0690:
            r15 = r24
            r15.add(r3)
            org.telegram.messenger.MessageObject r7 = new org.telegram.messenger.MessageObject
            int r6 = r13.currentAccount
            java.util.ArrayList<java.lang.Long> r5 = r13.createdDialogIds
            r19 = r0
            r31 = r1
            long r0 = r3.dialog_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            boolean r0 = r5.contains(r0)
            r5 = r7
            r1 = r32
            r17 = r15
            r15 = r7
            r7 = r3
            r50 = r8
            r8 = r11
            r51 = r9
            r10 = r41
            r9 = r12
            r20 = r11
            r11 = r10
            r10 = r0
            r5.<init>((int) r6, (org.telegram.tgnet.TLRPC.Message) r7, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.User>) r8, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.Chat>) r9, (boolean) r10)
            if (r11 != 0) goto L_0x06c7
            android.util.LongSparseArray r9 = new android.util.LongSparseArray
            r9.<init>()
            goto L_0x06c8
        L_0x06c7:
            r9 = r11
        L_0x06c8:
            long r5 = r3.dialog_id
            java.lang.Object r0 = r9.get(r5)
            java.util.ArrayList r0 = (java.util.ArrayList) r0
            if (r0 != 0) goto L_0x06dc
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            long r5 = r3.dialog_id
            r9.put(r5, r0)
        L_0x06dc:
            r0.add(r15)
            if (r1 != 0) goto L_0x06e7
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            goto L_0x06e8
        L_0x06e7:
            r6 = r1
        L_0x06e8:
            r6.add(r15)
            r1 = r6
            goto L_0x06fe
        L_0x06ed:
            r19 = r0
            r31 = r1
            r50 = r8
            r51 = r9
            r20 = r11
            r1 = r32
            r11 = r41
            r9 = r11
            r17 = r24
        L_0x06fe:
            r6 = r1
            r32 = r2
            r24 = r17
            goto L_0x0728
        L_0x0704:
            r19 = r0
            r31 = r1
            r50 = r8
            r51 = r9
            r20 = r11
            r1 = r32
            r11 = r41
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogPinned
            if (r0 == 0) goto L_0x073b
            if (r36 != 0) goto L_0x071d
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x071d:
            r0 = r36
            r0.add(r15)
        L_0x0722:
            r36 = r0
        L_0x0724:
            r6 = r1
            r32 = r2
        L_0x0727:
            r9 = r11
        L_0x0728:
            r7 = r19
            r8 = r33
            r5 = r40
            r38 = r50
            r3 = 0
            r33 = r27
            r27 = r4
            r4 = r20
            r20 = r51
            goto L_0x1311
        L_0x073b:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs
            if (r0 == 0) goto L_0x074c
            if (r36 != 0) goto L_0x0746
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0746:
            r0 = r36
            r0.add(r15)
            goto L_0x0722
        L_0x074c:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateFolderPeers
            if (r0 == 0) goto L_0x076c
            if (r36 != 0) goto L_0x0757
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0757:
            r0 = r36
            r0.add(r15)
            org.telegram.tgnet.TLRPC$TL_updateFolderPeers r15 = (org.telegram.tgnet.TLRPC.TL_updateFolderPeers) r15
            org.telegram.messenger.MessagesStorage r5 = r54.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_folderPeer> r6 = r15.folder_peers
            r7 = 0
            r8 = 0
            r10 = 0
            r5.setDialogsFolderId(r6, r7, r8, r10)
            goto L_0x0722
        L_0x076c:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePrivacy
            if (r0 == 0) goto L_0x077d
            if (r36 != 0) goto L_0x0777
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0777:
            r0 = r36
            r0.add(r15)
            goto L_0x0722
        L_0x077d:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateWebPage
            if (r0 == 0) goto L_0x0798
            org.telegram.tgnet.TLRPC$TL_updateWebPage r15 = (org.telegram.tgnet.TLRPC.TL_updateWebPage) r15
            r0 = r29
            if (r0 != 0) goto L_0x078e
            android.util.LongSparseArray r29 = new android.util.LongSparseArray
            r29.<init>()
            r0 = r29
        L_0x078e:
            org.telegram.tgnet.TLRPC$WebPage r3 = r15.webpage
            long r5 = r3.id
            r0.put(r5, r3)
        L_0x0795:
            r29 = r0
            goto L_0x0724
        L_0x0798:
            r0 = r29
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelWebPage
            if (r5 == 0) goto L_0x07b1
            org.telegram.tgnet.TLRPC$TL_updateChannelWebPage r15 = (org.telegram.tgnet.TLRPC.TL_updateChannelWebPage) r15
            if (r0 != 0) goto L_0x07a9
            android.util.LongSparseArray r29 = new android.util.LongSparseArray
            r29.<init>()
            r0 = r29
        L_0x07a9:
            org.telegram.tgnet.TLRPC$WebPage r3 = r15.webpage
            long r5 = r3.id
            r0.put(r5, r3)
            goto L_0x0795
        L_0x07b1:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelTooLong
            if (r5 == 0) goto L_0x084d
            r5 = r15
            org.telegram.tgnet.TLRPC$TL_updateChannelTooLong r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelTooLong) r5
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x07d3
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            r6.append(r3)
            int r3 = r5.channel_id
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x07d3:
            android.util.SparseIntArray r3 = r13.channelsPts
            int r6 = r5.channel_id
            int r3 = r3.get(r6)
            if (r3 != 0) goto L_0x082c
            org.telegram.messenger.MessagesStorage r3 = r54.getMessagesStorage()
            int r6 = r5.channel_id
            int r3 = r3.getChannelPtsSync(r6)
            if (r3 != 0) goto L_0x0825
            int r6 = r5.channel_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.Object r6 = r12.get(r6)
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC.Chat) r6
            if (r6 == 0) goto L_0x07fb
            boolean r7 = r6.min
            if (r7 == 0) goto L_0x0805
        L_0x07fb:
            int r6 = r5.channel_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r6 = r13.getChat(r6)
        L_0x0805:
            if (r6 == 0) goto L_0x080b
            boolean r7 = r6.min
            if (r7 == 0) goto L_0x0819
        L_0x080b:
            org.telegram.messenger.MessagesStorage r6 = r54.getMessagesStorage()
            int r7 = r5.channel_id
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChatSync(r7)
            r7 = 1
            r13.putChat(r6, r7)
        L_0x0819:
            if (r6 == 0) goto L_0x082c
            boolean r7 = r6.min
            if (r7 != 0) goto L_0x082c
            r7 = 0
            r13.loadUnknownChannel(r6, r7)
            goto L_0x082c
        L_0x0825:
            android.util.SparseIntArray r6 = r13.channelsPts
            int r7 = r5.channel_id
            r6.put(r7, r3)
        L_0x082c:
            if (r3 == 0) goto L_0x0843
            int r6 = r5.flags
            r7 = 1
            r6 = r6 & r7
            if (r6 == 0) goto L_0x083e
            int r6 = r5.pts
            if (r6 <= r3) goto L_0x0843
            int r3 = r5.channel_id
            r13.getChannelDifference(r3)
            goto L_0x0843
        L_0x083e:
            int r3 = r5.channel_id
            r13.getChannelDifference(r3)
        L_0x0843:
            r29 = r2
            r15 = r20
            r10 = r26
            r17 = r34
            goto L_0x0590
        L_0x084d:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox
            if (r5 == 0) goto L_0x08a0
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r15 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox) r15
            int r3 = r15.max_id
            long r5 = (long) r3
            int r3 = r15.channel_id
            long r7 = (long) r3
            r9 = 32
            long r7 = r7 << r9
            long r5 = r5 | r7
            int r3 = -r3
            long r7 = (long) r3
            if (r14 != 0) goto L_0x0866
            org.telegram.messenger.support.SparseLongArray r14 = new org.telegram.messenger.support.SparseLongArray
            r14.<init>()
        L_0x0866:
            int r3 = r15.channel_id
            int r3 = -r3
            r14.put(r3, r5)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r13.dialogs_read_inbox_max
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            java.lang.Object r3 = r3.get(r5)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r3 != 0) goto L_0x0887
            org.telegram.messenger.MessagesStorage r3 = r54.getMessagesStorage()
            r5 = 0
            int r3 = r3.getDialogReadMax(r5, r7)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
        L_0x0887:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r5 = r13.dialogs_read_inbox_max
            java.lang.Long r6 = java.lang.Long.valueOf(r7)
            int r3 = r3.intValue()
            int r7 = r15.max_id
            int r3 = java.lang.Math.max(r3, r7)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r5.put(r6, r3)
            goto L_0x0795
        L_0x08a0:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox
            if (r5 == 0) goto L_0x090b
            org.telegram.tgnet.TLRPC$TL_updateReadChannelOutbox r15 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox) r15
            int r3 = r15.max_id
            long r5 = (long) r3
            int r3 = r15.channel_id
            long r7 = (long) r3
            r9 = 32
            long r7 = r7 << r9
            long r5 = r5 | r7
            int r3 = -r3
            long r7 = (long) r3
            if (r4 != 0) goto L_0x08ba
            org.telegram.messenger.support.SparseLongArray r3 = new org.telegram.messenger.support.SparseLongArray
            r3.<init>()
            goto L_0x08bb
        L_0x08ba:
            r3 = r4
        L_0x08bb:
            int r4 = r15.channel_id
            int r4 = -r4
            r3.put(r4, r5)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r4 = r13.dialogs_read_outbox_max
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            java.lang.Object r4 = r4.get(r5)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 != 0) goto L_0x08dc
            org.telegram.messenger.MessagesStorage r4 = r54.getMessagesStorage()
            r5 = 1
            int r4 = r4.getDialogReadMax(r5, r7)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
        L_0x08dc:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r5 = r13.dialogs_read_outbox_max
            java.lang.Long r6 = java.lang.Long.valueOf(r7)
            int r4 = r4.intValue()
            int r7 = r15.max_id
            int r4 = java.lang.Math.max(r4, r7)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r5.put(r6, r4)
            r29 = r0
            r6 = r1
            r32 = r2
            r9 = r11
            r7 = r19
            r4 = r20
            r8 = r33
            r5 = r40
            r38 = r50
            r20 = r51
            r33 = r27
            r27 = r3
            goto L_0x010f
        L_0x090b:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages
            if (r5 == 0) goto L_0x0951
            r5 = r15
            org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages r5 = (org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages) r5
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x092d
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            r6.append(r3)
            int r3 = r5.channel_id
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x092d:
            if (r2 != 0) goto L_0x0936
            android.util.SparseArray r32 = new android.util.SparseArray
            r32.<init>()
            r2 = r32
        L_0x0936:
            int r3 = r5.channel_id
            java.lang.Object r3 = r2.get(r3)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            if (r3 != 0) goto L_0x094a
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            int r6 = r5.channel_id
            r2.put(r6, r3)
        L_0x094a:
            java.util.ArrayList<java.lang.Integer> r5 = r5.messages
            r3.addAll(r5)
            goto L_0x0795
        L_0x0951:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannel
            if (r5 == 0) goto L_0x0988
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r5 == 0) goto L_0x0973
            r5 = r15
            org.telegram.tgnet.TLRPC$TL_updateChannel r5 = (org.telegram.tgnet.TLRPC.TL_updateChannel) r5
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            r6.append(r3)
            int r3 = r5.channel_id
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x0973:
            if (r36 != 0) goto L_0x097a
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x097a:
            r3 = r36
            r3.add(r15)
            r29 = r0
            r6 = r1
            r32 = r2
            r36 = r3
            goto L_0x0727
        L_0x0988:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews
            if (r5 == 0) goto L_0x09d9
            r5 = r15
            org.telegram.tgnet.TLRPC$TL_updateChannelMessageViews r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews) r5
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x09aa
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            r6.append(r3)
            int r3 = r5.channel_id
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x09aa:
            r10 = r30
            if (r10 != 0) goto L_0x09b5
            android.util.SparseArray r30 = new android.util.SparseArray
            r30.<init>()
            r10 = r30
        L_0x09b5:
            int r3 = r5.channel_id
            java.lang.Object r3 = r10.get(r3)
            android.util.SparseIntArray r3 = (android.util.SparseIntArray) r3
            if (r3 != 0) goto L_0x09c9
            android.util.SparseIntArray r3 = new android.util.SparseIntArray
            r3.<init>()
            int r6 = r5.channel_id
            r10.put(r6, r3)
        L_0x09c9:
            int r6 = r5.id
            int r5 = r5.views
            r3.put(r6, r5)
            r29 = r0
            r6 = r1
            r32 = r2
        L_0x09d5:
            r30 = r10
            goto L_0x0727
        L_0x09d9:
            r10 = r30
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin
            if (r5 == 0) goto L_0x0a07
            org.telegram.tgnet.TLRPC$TL_updateChatParticipantAdmin r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin) r15
            org.telegram.messenger.MessagesStorage r44 = r54.getMessagesStorage()
            int r3 = r15.chat_id
            int r5 = r15.user_id
            r47 = 2
            boolean r6 = r15.is_admin
            int r7 = r15.version
            r45 = r3
            r46 = r5
            r48 = r6
            r49 = r7
            r44.updateChatInfo(r45, r46, r47, r48, r49)
        L_0x09fa:
            r29 = r2
            r30 = r14
            r15 = r20
            r17 = r34
        L_0x0a02:
            r14 = r10
            r10 = r26
            goto L_0x0e19
        L_0x0a07:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights
            if (r5 == 0) goto L_0x0a2f
            r3 = r15
            org.telegram.tgnet.TLRPC$TL_updateChatDefaultBannedRights r3 = (org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights) r3
            org.telegram.tgnet.TLRPC$Peer r5 = r3.peer
            int r6 = r5.channel_id
            if (r6 == 0) goto L_0x0a15
            goto L_0x0a17
        L_0x0a15:
            int r6 = r5.chat_id
        L_0x0a17:
            org.telegram.messenger.MessagesStorage r5 = r54.getMessagesStorage()
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r3.default_banned_rights
            int r3 = r3.version
            r5.updateChatDefaultBannedRights(r6, r7, r3)
            if (r36 != 0) goto L_0x0a29
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0a29:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0a2f:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSets
            if (r5 == 0) goto L_0x0a47
            if (r36 != 0) goto L_0x0a3a
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0a3a:
            r3 = r36
            r3.add(r15)
        L_0x0a3f:
            r29 = r0
            r6 = r1
            r32 = r2
            r36 = r3
            goto L_0x09d5
        L_0x0a47:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder
            if (r5 == 0) goto L_0x0a58
            if (r36 != 0) goto L_0x0a52
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0a52:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0a58:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewStickerSet
            if (r5 == 0) goto L_0x0a69
            if (r36 != 0) goto L_0x0a63
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0a63:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0a69:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDraftMessage
            if (r5 == 0) goto L_0x0a7a
            if (r36 != 0) goto L_0x0a74
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0a74:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0a7a:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateSavedGifs
            if (r5 == 0) goto L_0x0a8b
            if (r36 != 0) goto L_0x0a85
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0a85:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0a8b:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage
            if (r5 != 0) goto L_0x0CLASSNAME
            boolean r6 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEditMessage
            if (r6 == 0) goto L_0x0a95
            goto L_0x0CLASSNAME
        L_0x0a95:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage
            if (r5 == 0) goto L_0x0ac4
            r5 = r15
            org.telegram.tgnet.TLRPC$TL_updateChannelPinnedMessage r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage) r5
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x0ab7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            r6.append(r3)
            int r3 = r5.channel_id
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x0ab7:
            org.telegram.messenger.MessagesStorage r3 = r54.getMessagesStorage()
            int r6 = r5.channel_id
            int r5 = r5.id
            r3.updateChatPinnedMessage(r6, r5)
            goto L_0x09fa
        L_0x0ac4:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage
            if (r3 == 0) goto L_0x0ad7
            org.telegram.tgnet.TLRPC$TL_updateChatPinnedMessage r15 = (org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage) r15
            org.telegram.messenger.MessagesStorage r3 = r54.getMessagesStorage()
            int r5 = r15.chat_id
            int r6 = r15.id
            r3.updateChatPinnedMessage(r5, r6)
            goto L_0x09fa
        L_0x0ad7:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage
            if (r3 == 0) goto L_0x0aea
            org.telegram.tgnet.TLRPC$TL_updateUserPinnedMessage r15 = (org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage) r15
            org.telegram.messenger.MessagesStorage r3 = r54.getMessagesStorage()
            int r5 = r15.user_id
            int r6 = r15.id
            r3.updateUserPinnedMessage(r5, r6)
            goto L_0x09fa
        L_0x0aea:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers
            if (r3 == 0) goto L_0x0afc
            if (r36 != 0) goto L_0x0af5
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0af5:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0afc:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePhoneCall
            if (r3 == 0) goto L_0x0b0e
            if (r36 != 0) goto L_0x0b07
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0b07:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0b0e:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPack
            if (r3 == 0) goto L_0x0b1e
            org.telegram.tgnet.TLRPC$TL_updateLangPack r15 = (org.telegram.tgnet.TLRPC.TL_updateLangPack) r15
            org.telegram.messenger.-$$Lambda$MessagesController$TGaV3j0rMT2kXtICENm7gz6gqZw r3 = new org.telegram.messenger.-$$Lambda$MessagesController$TGaV3j0rMT2kXtICENm7gz6gqZw
            r3.<init>(r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            goto L_0x09fa
        L_0x0b1e:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong
            if (r3 == 0) goto L_0x0b31
            org.telegram.tgnet.TLRPC$TL_updateLangPackTooLong r15 = (org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong) r15
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            int r5 = r13.currentAccount
            java.lang.String r6 = r15.lang_code
            r3.reloadCurrentRemoteLocale(r5, r6)
            goto L_0x09fa
        L_0x0b31:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateFavedStickers
            if (r3 == 0) goto L_0x0b43
            if (r36 != 0) goto L_0x0b3c
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0b3c:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0b43:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateContactsReset
            if (r3 == 0) goto L_0x0b55
            if (r36 != 0) goto L_0x0b4e
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0b4e:
            r3 = r36
            r3.add(r15)
            goto L_0x0a3f
        L_0x0b55:
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages
            if (r3 == 0) goto L_0x0b82
            org.telegram.tgnet.TLRPC$TL_updateChannelAvailableMessages r15 = (org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages) r15
            r3 = r34
            if (r3 != 0) goto L_0x0b66
            android.util.SparseIntArray r34 = new android.util.SparseIntArray
            r34.<init>()
            r3 = r34
        L_0x0b66:
            int r5 = r15.channel_id
            int r5 = r3.get(r5)
            if (r5 == 0) goto L_0x0b72
            int r6 = r15.available_min_id
            if (r5 >= r6) goto L_0x0b79
        L_0x0b72:
            int r5 = r15.channel_id
            int r6 = r15.available_min_id
            r3.put(r5, r6)
        L_0x0b79:
            r29 = r0
            r6 = r1
            r32 = r2
            r34 = r3
            goto L_0x09d5
        L_0x0b82:
            r3 = r34
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark
            if (r5 == 0) goto L_0x0b9f
            if (r36 != 0) goto L_0x0b8f
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0b8f:
            r5 = r36
            r5.add(r15)
        L_0x0b94:
            r29 = r0
            r6 = r1
            r32 = r2
            r34 = r3
            r36 = r5
            goto L_0x09d5
        L_0x0b9f:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateMessagePoll
            if (r5 == 0) goto L_0x0be3
            r5 = r15
            org.telegram.tgnet.TLRPC$TL_updateMessagePoll r5 = (org.telegram.tgnet.TLRPC.TL_updateMessagePoll) r5
            org.telegram.messenger.SendMessagesHelper r6 = r54.getSendMessagesHelper()
            long r7 = r5.poll_id
            long r6 = r6.getVoteSendTime(r7)
            long r8 = android.os.SystemClock.elapsedRealtime()
            long r8 = r8 - r6
            long r6 = java.lang.Math.abs(r8)
            r8 = 600(0x258, double:2.964E-321)
            int r17 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r17 >= 0) goto L_0x0bc9
        L_0x0bbf:
            r29 = r2
            r17 = r3
            r30 = r14
            r15 = r20
            goto L_0x0a02
        L_0x0bc9:
            org.telegram.messenger.MessagesStorage r6 = r54.getMessagesStorage()
            long r7 = r5.poll_id
            org.telegram.tgnet.TLRPC$TL_poll r9 = r5.poll
            org.telegram.tgnet.TLRPC$PollResults r5 = r5.results
            r6.updateMessagePollResults(r7, r9, r5)
            if (r36 != 0) goto L_0x0bdd
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0bdd:
            r5 = r36
            r5.add(r15)
            goto L_0x0b94
        L_0x0be3:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateMessageReactions
            if (r5 == 0) goto L_0x0CLASSNAME
            r5 = r15
            org.telegram.tgnet.TLRPC$TL_updateMessageReactions r5 = (org.telegram.tgnet.TLRPC.TL_updateMessageReactions) r5
            org.telegram.tgnet.TLRPC$Peer r6 = r5.peer
            int r7 = r6.chat_id
            if (r7 == 0) goto L_0x0bf5
        L_0x0bf0:
            int r6 = -r7
        L_0x0bf1:
            long r6 = (long) r6
            r45 = r6
            goto L_0x0bfd
        L_0x0bf5:
            int r7 = r6.channel_id
            if (r7 == 0) goto L_0x0bfa
            goto L_0x0bf0
        L_0x0bfa:
            int r6 = r6.user_id
            goto L_0x0bf1
        L_0x0bfd:
            org.telegram.messenger.MessagesStorage r44 = r54.getMessagesStorage()
            int r6 = r5.msg_id
            org.telegram.tgnet.TLRPC$Peer r7 = r5.peer
            int r7 = r7.channel_id
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            r47 = r6
            r48 = r7
            r49 = r5
            r44.updateMessageReactions(r45, r47, r48, r49)
            if (r36 != 0) goto L_0x0c1a
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            goto L_0x0c1c
        L_0x0c1a:
            r5 = r36
        L_0x0c1c:
            r5.add(r15)
            goto L_0x0b94
        L_0x0CLASSNAME:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerLocated
            if (r5 == 0) goto L_0x0CLASSNAME
            if (r36 != 0) goto L_0x0c2c
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0c2c:
            r5 = r36
            r5.add(r15)
            goto L_0x0b94
        L_0x0CLASSNAME:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateTheme
            if (r5 == 0) goto L_0x0CLASSNAME
            if (r36 != 0) goto L_0x0c3e
            java.util.ArrayList r36 = new java.util.ArrayList
            r36.<init>()
        L_0x0c3e:
            r5 = r36
            r5.add(r15)
            goto L_0x0b94
        L_0x0CLASSNAME:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateGeoLiveViewed
            if (r5 == 0) goto L_0x0bbf
            org.telegram.messenger.LocationController r5 = r54.getLocationController()
            r5.setNewLocationEndWatchTime()
            goto L_0x0bbf
        L_0x0CLASSNAME:
            r3 = r34
            org.telegram.messenger.UserConfig r6 = r54.getUserConfig()
            int r6 = r6.getClientUserId()
            if (r5 == 0) goto L_0x0ca4
            org.telegram.tgnet.TLRPC$TL_updateEditChannelMessage r15 = (org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage) r15
            org.telegram.tgnet.TLRPC$Message r5 = r15.message
            org.telegram.tgnet.TLRPC$Peer r7 = r5.to_id
            int r7 = r7.channel_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.Object r7 = r12.get(r7)
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC.Chat) r7
            if (r7 != 0) goto L_0x0c7e
            org.telegram.tgnet.TLRPC$Peer r7 = r5.to_id
            int r7 = r7.channel_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r7 = r13.getChat(r7)
        L_0x0c7e:
            if (r7 != 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessagesStorage r7 = r54.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Peer r8 = r5.to_id
            int r8 = r8.channel_id
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChatSync(r8)
            r8 = 1
            r13.putChat(r7, r8)
        L_0x0CLASSNAME:
            if (r7 == 0) goto L_0x0c9d
            boolean r7 = r7.megagroup
            if (r7 == 0) goto L_0x0c9d
            int r7 = r5.flags
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            r7 = r7 | r8
            r5.flags = r7
        L_0x0c9d:
            r29 = r2
            r17 = r3
            r3 = r5
            r2 = 1
            goto L_0x0cbe
        L_0x0ca4:
            org.telegram.tgnet.TLRPC$TL_updateEditMessage r15 = (org.telegram.tgnet.TLRPC.TL_updateEditMessage) r15
            org.telegram.tgnet.TLRPC$Message r5 = r15.message
            long r7 = r5.dialog_id
            r29 = r2
            r17 = r3
            long r2 = (long) r6
            int r9 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r9 != 0) goto L_0x0cbc
            r2 = 0
            r5.unread = r2
            r5.media_unread = r2
            r2 = 1
            r5.out = r2
            goto L_0x0cbd
        L_0x0cbc:
            r2 = 1
        L_0x0cbd:
            r3 = r5
        L_0x0cbe:
            boolean r5 = r3.out
            if (r5 != 0) goto L_0x0cd0
            int r5 = r3.from_id
            org.telegram.messenger.UserConfig r7 = r54.getUserConfig()
            int r7 = r7.getClientUserId()
            if (r5 != r7) goto L_0x0cd0
            r3.out = r2
        L_0x0cd0:
            if (r58 != 0) goto L_0x0d2c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r3.entities
            int r2 = r2.size()
            r5 = 0
        L_0x0cd9:
            if (r5 >= r2) goto L_0x0d2c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r3.entities
            java.lang.Object r7 = r7.get(r5)
            org.telegram.tgnet.TLRPC$MessageEntity r7 = (org.telegram.tgnet.TLRPC.MessageEntity) r7
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName
            if (r8 == 0) goto L_0x0d25
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r7 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r7
            int r7 = r7.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r7)
            r15 = r20
            java.lang.Object r8 = r15.get(r8)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC.User) r8
            if (r8 == 0) goto L_0x0cfd
            boolean r9 = r8.min
            if (r9 == 0) goto L_0x0d05
        L_0x0cfd:
            java.lang.Integer r8 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r8 = r13.getUser(r8)
        L_0x0d05:
            if (r8 == 0) goto L_0x0d0b
            boolean r9 = r8.min
            if (r9 == 0) goto L_0x0d21
        L_0x0d0b:
            org.telegram.messenger.MessagesStorage r8 = r54.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r7 = r8.getUserSync(r7)
            if (r7 == 0) goto L_0x0d1c
            boolean r8 = r7.min
            if (r8 == 0) goto L_0x0d1c
            r7 = 1
            r8 = 0
            goto L_0x0d1e
        L_0x0d1c:
            r8 = r7
            r7 = 1
        L_0x0d1e:
            r13.putUser(r8, r7)
        L_0x0d21:
            if (r8 != 0) goto L_0x0d27
            r7 = 0
            return r7
        L_0x0d25:
            r15 = r20
        L_0x0d27:
            int r5 = r5 + 1
            r20 = r15
            goto L_0x0cd9
        L_0x0d2c:
            r15 = r20
            org.telegram.tgnet.TLRPC$Peer r2 = r3.to_id
            int r5 = r2.chat_id
            if (r5 == 0) goto L_0x0d39
            int r2 = -r5
            long r7 = (long) r2
            r3.dialog_id = r7
            goto L_0x0d5b
        L_0x0d39:
            int r5 = r2.channel_id
            if (r5 == 0) goto L_0x0d42
            int r2 = -r5
            long r7 = (long) r2
            r3.dialog_id = r7
            goto L_0x0d5b
        L_0x0d42:
            int r2 = r2.user_id
            org.telegram.messenger.UserConfig r5 = r54.getUserConfig()
            int r5 = r5.getClientUserId()
            if (r2 != r5) goto L_0x0d54
            org.telegram.tgnet.TLRPC$Peer r2 = r3.to_id
            int r5 = r3.from_id
            r2.user_id = r5
        L_0x0d54:
            org.telegram.tgnet.TLRPC$Peer r2 = r3.to_id
            int r2 = r2.user_id
            long r7 = (long) r2
            r3.dialog_id = r7
        L_0x0d5b:
            boolean r2 = r3.out
            if (r2 == 0) goto L_0x0d62
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r13.dialogs_read_outbox_max
            goto L_0x0d64
        L_0x0d62:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r13.dialogs_read_inbox_max
        L_0x0d64:
            long r7 = r3.dialog_id
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            java.lang.Object r5 = r2.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 != 0) goto L_0x0d8b
            org.telegram.messenger.MessagesStorage r5 = r54.getMessagesStorage()
            boolean r7 = r3.out
            long r8 = r3.dialog_id
            int r5 = r5.getDialogReadMax(r7, r8)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            long r7 = r3.dialog_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            r2.put(r7, r5)
        L_0x0d8b:
            int r2 = r5.intValue()
            int r5 = r3.id
            if (r2 >= r5) goto L_0x0d95
            r2 = 1
            goto L_0x0d96
        L_0x0d95:
            r2 = 0
        L_0x0d96:
            r3.unread = r2
            long r7 = r3.dialog_id
            long r5 = (long) r6
            int r2 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r2 != 0) goto L_0x0da7
            r2 = 1
            r3.out = r2
            r2 = 0
            r3.unread = r2
            r3.media_unread = r2
        L_0x0da7:
            boolean r2 = r3.out
            if (r2 == 0) goto L_0x0db5
            java.lang.String r2 = r3.message
            if (r2 != 0) goto L_0x0db5
            java.lang.String r2 = ""
            r3.message = r2
            r3.attachPath = r2
        L_0x0db5:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r3)
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject
            int r6 = r13.currentAccount
            java.util.ArrayList<java.lang.Long> r5 = r13.createdDialogIds
            long r7 = r3.dialog_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            boolean r20 = r5.contains(r7)
            r5 = r2
            r7 = r3
            r8 = r15
            r9 = r12
            r30 = r14
            r14 = r10
            r10 = r20
            r5.<init>((int) r6, (org.telegram.tgnet.TLRPC.Message) r7, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.User>) r8, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.Chat>) r9, (boolean) r10)
            r10 = r26
            if (r10 != 0) goto L_0x0ddf
            android.util.LongSparseArray r26 = new android.util.LongSparseArray
            r26.<init>()
            r10 = r26
        L_0x0ddf:
            long r5 = r3.dialog_id
            java.lang.Object r5 = r10.get(r5)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x0df3
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            long r6 = r3.dialog_id
            r10.put(r6, r5)
        L_0x0df3:
            r5.add(r2)
            goto L_0x0e19
        L_0x0df7:
            r19 = r0
            r31 = r1
            r50 = r8
            r51 = r9
            r15 = r11
            r10 = r26
            r0 = r29
            r1 = r32
            r17 = r34
            r11 = r41
            r29 = r2
            r53 = r30
            r30 = r14
            r14 = r53
            org.telegram.tgnet.ConnectionsManager r2 = r54.getConnectionsManager()
            r2.updateDcSettings()
        L_0x0e19:
            r6 = r1
            r26 = r10
            r9 = r11
            r34 = r17
            r7 = r19
            r32 = r29
            r8 = r33
            r5 = r40
            r38 = r50
            r20 = r51
            r3 = 0
            r29 = r0
            r33 = r27
            r27 = r4
            r4 = r15
            goto L_0x1161
        L_0x0e35:
            r40 = r5
            r1 = r6
            r33 = r8
            r43 = r10
            r8 = r11
            r51 = r20
            r10 = r26
            r17 = r34
            r50 = r38
            r11 = r9
            r9 = r29
            r29 = r2
            r2 = r7
            r53 = r30
            r30 = r14
            r14 = r53
            if (r0 == 0) goto L_0x0e5c
            org.telegram.tgnet.TLRPC$TL_updateUserTyping r15 = (org.telegram.tgnet.TLRPC.TL_updateUserTyping) r15
            int r0 = r15.user_id
            org.telegram.tgnet.TLRPC$SendMessageAction r3 = r15.action
            r5 = r3
            r3 = 0
            goto L_0x0e69
        L_0x0e5c:
            org.telegram.tgnet.TLRPC$TL_updateChatUserTyping r15 = (org.telegram.tgnet.TLRPC.TL_updateChatUserTyping) r15
            int r0 = r15.chat_id
            int r3 = r15.user_id
            org.telegram.tgnet.TLRPC$SendMessageAction r5 = r15.action
            r53 = r3
            r3 = r0
            r0 = r53
        L_0x0e69:
            org.telegram.messenger.UserConfig r6 = r54.getUserConfig()
            int r6 = r6.getClientUserId()
            if (r0 == r6) goto L_0x0var_
            int r3 = -r3
            long r6 = (long) r3
            r20 = 0
            int r3 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r3 != 0) goto L_0x0e7c
            long r6 = (long) r0
        L_0x0e7c:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> r3 = r13.printingUsers
            java.lang.Long r15 = java.lang.Long.valueOf(r6)
            java.lang.Object r3 = r3.get(r15)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            boolean r15 = r5 instanceof org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction
            if (r15 == 0) goto L_0x0ec9
            if (r3 == 0) goto L_0x0ec0
            int r5 = r3.size()
            r15 = 0
        L_0x0e93:
            if (r15 >= r5) goto L_0x0eae
            java.lang.Object r20 = r3.get(r15)
            r26 = r4
            r4 = r20
            org.telegram.messenger.MessagesController$PrintingUser r4 = (org.telegram.messenger.MessagesController.PrintingUser) r4
            int r4 = r4.userId
            if (r4 != r0) goto L_0x0ea9
            r3.remove(r15)
            r16 = 1
            goto L_0x0eb0
        L_0x0ea9:
            int r15 = r15 + 1
            r4 = r26
            goto L_0x0e93
        L_0x0eae:
            r26 = r4
        L_0x0eb0:
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0ec2
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> r3 = r13.printingUsers
            java.lang.Long r4 = java.lang.Long.valueOf(r6)
            r3.remove(r4)
            goto L_0x0ec2
        L_0x0ec0:
            r26 = r4
        L_0x0ec2:
            r32 = r1
            r34 = r2
            r1 = r51
            goto L_0x0var_
        L_0x0ec9:
            r26 = r4
            if (r3 != 0) goto L_0x0edb
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> r4 = r13.printingUsers
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            r4.put(r6, r3)
        L_0x0edb:
            java.util.Iterator r4 = r3.iterator()
        L_0x0edf:
            boolean r6 = r4.hasNext()
            if (r6 == 0) goto L_0x0var_
            java.lang.Object r6 = r4.next()
            org.telegram.messenger.MessagesController$PrintingUser r6 = (org.telegram.messenger.MessagesController.PrintingUser) r6
            int r7 = r6.userId
            if (r7 != r0) goto L_0x0edf
            r32 = r1
            r34 = r2
            r1 = r51
            r6.lastTime = r1
            org.telegram.tgnet.TLRPC$SendMessageAction r4 = r6.action
            java.lang.Class r4 = r4.getClass()
            java.lang.Class r7 = r5.getClass()
            if (r4 == r7) goto L_0x0var_
            r16 = 1
        L_0x0var_:
            r6.action = r5
            r4 = 1
            goto L_0x0var_
        L_0x0var_:
            r32 = r1
            r34 = r2
            r1 = r51
            r4 = 0
        L_0x0var_:
            if (r4 != 0) goto L_0x0var_
            org.telegram.messenger.MessagesController$PrintingUser r4 = new org.telegram.messenger.MessagesController$PrintingUser
            r4.<init>()
            r4.userId = r0
            r4.lastTime = r1
            r4.action = r5
            r3.add(r4)
            r16 = 1
        L_0x0var_:
            org.telegram.tgnet.ConnectionsManager r3 = r54.getConnectionsManager()
            int r3 = r3.getCurrentTime()
            int r3 = r3 - r59
            int r3 = java.lang.Math.abs(r3)
            r4 = 30
            if (r3 >= r4) goto L_0x0f4a
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r3 = r13.onlinePrivacy
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r59)
            r3.put(r0, r4)
            goto L_0x0f4a
        L_0x0var_:
            r32 = r1
            r34 = r2
            r26 = r4
            r1 = r51
        L_0x0f4a:
            r20 = r1
            r4 = r8
            r6 = r32
            r8 = r33
            r7 = r34
            r5 = r40
            r38 = r50
            r3 = 0
            r34 = r17
            goto L_0x1156
        L_0x0f5c:
            r25 = r4
            r40 = r5
            r43 = r10
            r1 = r20
            r10 = r26
            r26 = r27
            r27 = r33
            r4 = r34
            r50 = r38
            r34 = r7
            r33 = r8
            r8 = r11
            r11 = r9
            r9 = r29
            r29 = r32
            r32 = r6
            r53 = r30
            r30 = r14
            r14 = r53
            if (r0 == 0) goto L_0x0var_
            r0 = r15
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r0 = (org.telegram.tgnet.TLRPC.TL_updateNewMessage) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.message
            goto L_0x0fc7
        L_0x0var_:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage
            if (r0 == 0) goto L_0x0var_
            r0 = r15
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r0 = (org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.message
            goto L_0x0fc7
        L_0x0var_:
            r0 = r15
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r0 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.message
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x0fb4
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            r6.append(r3)
            org.telegram.tgnet.TLRPC$Peer r3 = r0.to_id
            int r3 = r3.channel_id
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x0fb4:
            boolean r3 = r0.out
            if (r3 != 0) goto L_0x0fc7
            int r3 = r0.from_id
            org.telegram.messenger.UserConfig r6 = r54.getUserConfig()
            int r6 = r6.getClientUserId()
            if (r3 != r6) goto L_0x0fc7
            r3 = 1
            r0.out = r3
        L_0x0fc7:
            org.telegram.tgnet.TLRPC$Peer r3 = r0.to_id
            int r6 = r3.channel_id
            if (r6 == 0) goto L_0x0fcf
        L_0x0fcd:
            r3 = 0
            goto L_0x0fdb
        L_0x0fcf:
            int r6 = r3.chat_id
            if (r6 == 0) goto L_0x0fd4
            goto L_0x0fcd
        L_0x0fd4:
            int r3 = r3.user_id
            if (r3 == 0) goto L_0x0fd9
            goto L_0x0fda
        L_0x0fd9:
            r3 = 0
        L_0x0fda:
            r6 = 0
        L_0x0fdb:
            if (r6 == 0) goto L_0x1000
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)
            java.lang.Object r7 = r12.get(r7)
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC.Chat) r7
            if (r7 != 0) goto L_0x0ff1
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r7 = r13.getChat(r7)
        L_0x0ff1:
            if (r7 != 0) goto L_0x1001
            org.telegram.messenger.MessagesStorage r7 = r54.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChatSync(r6)
            r5 = 1
            r13.putChat(r7, r5)
            goto L_0x1001
        L_0x1000:
            r7 = 0
        L_0x1001:
            if (r25 == 0) goto L_0x10f7
            if (r6 == 0) goto L_0x1021
            if (r7 != 0) goto L_0x1021
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x101f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found chat "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x101f:
            r0 = 0
            return r0
        L_0x1021:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r0.entities
            int r5 = r5.size()
            int r5 = r5 + 3
            r6 = r3
            r3 = 0
        L_0x102b:
            if (r3 >= r5) goto L_0x10f7
            r20 = r1
            if (r3 == 0) goto L_0x105e
            r1 = 1
            if (r3 != r1) goto L_0x103e
            int r1 = r0.from_id
            boolean r2 = r0.post
            r6 = r1
            r1 = 2
            if (r2 == 0) goto L_0x105f
            r2 = 1
            goto L_0x1060
        L_0x103e:
            r1 = 2
            if (r3 != r1) goto L_0x1048
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r0.fwd_from
            if (r2 == 0) goto L_0x105b
            int r2 = r2.from_id
            goto L_0x105c
        L_0x1048:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r0.entities
            int r6 = r3 + -3
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$MessageEntity r2 = (org.telegram.tgnet.TLRPC.MessageEntity) r2
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName
            if (r6 == 0) goto L_0x105b
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r2 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r2
            int r2 = r2.user_id
            goto L_0x105c
        L_0x105b:
            r2 = 0
        L_0x105c:
            r6 = r2
            goto L_0x105f
        L_0x105e:
            r1 = 2
        L_0x105f:
            r2 = 0
        L_0x1060:
            if (r6 <= 0) goto L_0x10ed
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            java.lang.Object r1 = r8.get(r1)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            r38 = r4
            if (r1 == 0) goto L_0x1076
            if (r2 != 0) goto L_0x107e
            boolean r4 = r1.min
            if (r4 == 0) goto L_0x107e
        L_0x1076:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r1 = r13.getUser(r1)
        L_0x107e:
            if (r1 == 0) goto L_0x1086
            if (r2 != 0) goto L_0x109b
            boolean r4 = r1.min
            if (r4 == 0) goto L_0x109b
        L_0x1086:
            org.telegram.messenger.MessagesStorage r1 = r54.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r1 = r1.getUserSync(r6)
            if (r1 == 0) goto L_0x1097
            if (r2 != 0) goto L_0x1097
            boolean r2 = r1.min
            if (r2 == 0) goto L_0x1097
            r1 = 0
        L_0x1097:
            r2 = 1
            r13.putUser(r1, r2)
        L_0x109b:
            if (r1 != 0) goto L_0x10b7
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x10b5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found user "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x10b5:
            r0 = 0
            return r0
        L_0x10b7:
            boolean r2 = r0.out
            if (r2 != 0) goto L_0x10ef
            r2 = 1
            if (r3 != r2) goto L_0x10ef
            org.telegram.tgnet.TLRPC$UserStatus r1 = r1.status
            if (r1 == 0) goto L_0x10ef
            int r1 = r1.expires
            if (r1 > 0) goto L_0x10ef
            org.telegram.tgnet.ConnectionsManager r1 = r54.getConnectionsManager()
            int r1 = r1.getCurrentTime()
            int r2 = r0.date
            int r1 = r1 - r2
            int r1 = java.lang.Math.abs(r1)
            r2 = 30
            if (r1 >= r2) goto L_0x10ef
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r1 = r13.onlinePrivacy
            java.lang.Integer r4 = java.lang.Integer.valueOf(r6)
            int r2 = r0.date
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1.put(r4, r2)
            r1 = r33 | 4
            r33 = r1
            goto L_0x10ef
        L_0x10ed:
            r38 = r4
        L_0x10ef:
            int r3 = r3 + 1
            r1 = r20
            r4 = r38
            goto L_0x102b
        L_0x10f7:
            r20 = r1
            r38 = r4
            if (r7 == 0) goto L_0x1108
            boolean r1 = r7.megagroup
            if (r1 == 0) goto L_0x1108
            int r1 = r0.flags
            r2 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r2
            r0.flags = r1
        L_0x1108:
            org.telegram.tgnet.TLRPC$MessageAction r1 = r0.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r2 == 0) goto L_0x1169
            int r1 = r1.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Object r1 = r8.get(r1)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            if (r1 == 0) goto L_0x112e
            boolean r1 = r1.bot
            if (r1 == 0) goto L_0x112e
            org.telegram.tgnet.TLRPC$TL_replyKeyboardHide r1 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide
            r1.<init>()
            r0.reply_markup = r1
            int r1 = r0.flags
            r1 = r1 | 64
            r0.flags = r1
            goto L_0x1169
        L_0x112e:
            int r1 = r0.from_id
            org.telegram.messenger.UserConfig r2 = r54.getUserConfig()
            int r2 = r2.getClientUserId()
            if (r1 != r2) goto L_0x1169
            org.telegram.tgnet.TLRPC$MessageAction r1 = r0.action
            int r1 = r1.user_id
            org.telegram.messenger.UserConfig r2 = r54.getUserConfig()
            int r2 = r2.getClientUserId()
            if (r1 != r2) goto L_0x1169
            r4 = r8
            r6 = r32
            r8 = r33
            r7 = r34
            r34 = r38
            r5 = r40
            r38 = r50
            r3 = 0
        L_0x1156:
            r33 = r27
            r32 = r29
            r29 = r9
            r9 = r11
            r27 = r26
            r26 = r10
        L_0x1161:
            r53 = r30
            r30 = r14
            r14 = r53
            goto L_0x1311
        L_0x1169:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r0)
            org.telegram.messenger.UserConfig r1 = r54.getUserConfig()
            int r1 = r1.getClientUserId()
            org.telegram.tgnet.TLRPC$Peer r2 = r0.to_id
            int r3 = r2.chat_id
            if (r3 == 0) goto L_0x117f
            int r2 = -r3
            long r2 = (long) r2
            r0.dialog_id = r2
            goto L_0x1197
        L_0x117f:
            int r3 = r2.channel_id
            if (r3 == 0) goto L_0x1188
            int r2 = -r3
            long r2 = (long) r2
            r0.dialog_id = r2
            goto L_0x1197
        L_0x1188:
            int r3 = r2.user_id
            if (r3 != r1) goto L_0x1190
            int r3 = r0.from_id
            r2.user_id = r3
        L_0x1190:
            org.telegram.tgnet.TLRPC$Peer r2 = r0.to_id
            int r2 = r2.user_id
            long r2 = (long) r2
            r0.dialog_id = r2
        L_0x1197:
            java.util.ArrayList<java.lang.Long> r2 = r13.createdDialogIds
            long r3 = r0.dialog_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x11c1
            long r2 = r0.grouped_id
            r4 = 0
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x11c1
            org.telegram.messenger.ImageLoader$MessageThumb r2 = org.telegram.messenger.ImageLoader.generateMessageThumb(r0)
            if (r2 == 0) goto L_0x11c1
            if (r37 != 0) goto L_0x11ba
            java.util.ArrayList r37 = new java.util.ArrayList
            r37.<init>()
        L_0x11ba:
            r3 = r37
            r3.add(r2)
            r37 = r3
        L_0x11c1:
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x121c
            if (r18 != 0) goto L_0x11cc
            java.util.ArrayList r18 = new java.util.ArrayList
            r18.<init>()
        L_0x11cc:
            r1 = r18
            r1.add(r0)
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject
            int r6 = r13.currentAccount
            java.util.ArrayList<java.lang.Long> r3 = r13.createdScheduledDialogIds
            long r4 = r0.dialog_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            boolean r3 = r3.contains(r4)
            r5 = r2
            r7 = r0
            r4 = r8
            r15 = r9
            r9 = r12
            r39 = r15
            r15 = r10
            r10 = r3
            r5.<init>((int) r6, (org.telegram.tgnet.TLRPC.Message) r7, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.User>) r8, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.Chat>) r9, (boolean) r10)
            r3 = 1
            r2.scheduled = r3
            if (r28 != 0) goto L_0x11f7
            android.util.LongSparseArray r28 = new android.util.LongSparseArray
            r28.<init>()
        L_0x11f7:
            r3 = r28
            long r5 = r0.dialog_id
            java.lang.Object r5 = r3.get(r5)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x120d
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            long r6 = r0.dialog_id
            r3.put(r6, r5)
        L_0x120d:
            r5.add(r2)
            r18 = r1
            r28 = r3
            r9 = r11
            r2 = r24
            r8 = r33
            r3 = 0
            goto L_0x12f9
        L_0x121c:
            r4 = r8
            r39 = r9
            r15 = r10
            if (r24 != 0) goto L_0x1227
            java.util.ArrayList r24 = new java.util.ArrayList
            r24.<init>()
        L_0x1227:
            r2 = r24
            r2.add(r0)
            boolean r3 = r0.out
            if (r3 == 0) goto L_0x1233
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r13.dialogs_read_outbox_max
            goto L_0x1235
        L_0x1233:
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r13.dialogs_read_inbox_max
        L_0x1235:
            long r5 = r0.dialog_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            java.lang.Object r5 = r3.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 != 0) goto L_0x125c
            org.telegram.messenger.MessagesStorage r5 = r54.getMessagesStorage()
            boolean r6 = r0.out
            long r8 = r0.dialog_id
            int r5 = r5.getDialogReadMax(r6, r8)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            long r8 = r0.dialog_id
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            r3.put(r6, r5)
        L_0x125c:
            int r3 = r5.intValue()
            int r5 = r0.id
            if (r3 >= r5) goto L_0x1278
            if (r7 == 0) goto L_0x126c
            boolean r3 = org.telegram.messenger.ChatObject.isNotInChat(r7)
            if (r3 != 0) goto L_0x1278
        L_0x126c:
            org.telegram.tgnet.TLRPC$MessageAction r3 = r0.action
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r5 != 0) goto L_0x1278
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r3 != 0) goto L_0x1278
            r3 = 1
            goto L_0x1279
        L_0x1278:
            r3 = 0
        L_0x1279:
            r0.unread = r3
            long r5 = r0.dialog_id
            long r7 = (long) r1
            int r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x128f
            boolean r1 = r0.from_scheduled
            r3 = 0
            if (r1 != 0) goto L_0x1289
            r0.unread = r3
        L_0x1289:
            r0.media_unread = r3
            r1 = 1
            r0.out = r1
            goto L_0x1290
        L_0x128f:
            r3 = 0
        L_0x1290:
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject
            int r6 = r13.currentAccount
            java.util.ArrayList<java.lang.Long> r5 = r13.createdDialogIds
            long r7 = r0.dialog_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            boolean r10 = r5.contains(r7)
            r5 = r1
            r7 = r0
            r8 = r4
            r9 = r12
            r5.<init>((int) r6, (org.telegram.tgnet.TLRPC.Message) r7, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.User>) r8, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.Chat>) r9, (boolean) r10)
            int r5 = r1.type
            r6 = 11
            if (r5 != r6) goto L_0x12b0
            r33 = r33 | 8
            goto L_0x12b6
        L_0x12b0:
            r6 = 10
            if (r5 != r6) goto L_0x12b6
            r33 = r33 | 16
        L_0x12b6:
            if (r11 != 0) goto L_0x12be
            android.util.LongSparseArray r9 = new android.util.LongSparseArray
            r9.<init>()
            goto L_0x12bf
        L_0x12be:
            r9 = r11
        L_0x12bf:
            long r5 = r0.dialog_id
            java.lang.Object r5 = r9.get(r5)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x12d3
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            long r6 = r0.dialog_id
            r9.put(r6, r5)
        L_0x12d3:
            r5.add(r1)
            boolean r0 = r1.isOut()
            if (r0 == 0) goto L_0x12e2
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r0 = r0.from_scheduled
            if (r0 == 0) goto L_0x12f7
        L_0x12e2:
            boolean r0 = r1.isUnread()
            if (r0 == 0) goto L_0x12f7
            if (r32 != 0) goto L_0x12f0
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            goto L_0x12f2
        L_0x12f0:
            r6 = r32
        L_0x12f2:
            r6.add(r1)
            r32 = r6
        L_0x12f7:
            r8 = r33
        L_0x12f9:
            r24 = r2
            r33 = r27
            r6 = r32
            r7 = r34
            r34 = r38
            r5 = r40
            r38 = r50
            r27 = r26
            r32 = r29
            r29 = r39
            r26 = r15
            goto L_0x1161
        L_0x1311:
            int r10 = r43 + 1
            r0 = r57
            r11 = r4
            r2 = r20
            r4 = r25
            goto L_0x00a5
        L_0x131c:
            r40 = r5
            r4 = r11
            r15 = r26
            r26 = r27
            r39 = r29
            r29 = r32
            r27 = r33
            r50 = r38
            r3 = 0
            r32 = r6
            r33 = r8
            r11 = r9
            r38 = r34
            r34 = r7
            r53 = r30
            r30 = r14
            r14 = r53
            if (r11 == 0) goto L_0x1359
            int r0 = r11.size()
            r1 = 0
        L_0x1342:
            if (r1 >= r0) goto L_0x1359
            long r5 = r11.keyAt(r1)
            java.lang.Object r2 = r11.valueAt(r1)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            boolean r2 = r13.updatePrintingUsersWithNewMessages(r5, r2)
            if (r2 == 0) goto L_0x1356
            r16 = 1
        L_0x1356:
            int r1 = r1 + 1
            goto L_0x1342
        L_0x1359:
            r8 = r16
            if (r8 == 0) goto L_0x1360
            r54.updatePrintingStrings()
        L_0x1360:
            if (r34 == 0) goto L_0x136c
            org.telegram.messenger.ContactsController r0 = r54.getContactsController()
            r7 = r34
            r0.processContactsUpdates(r7, r4)
            goto L_0x136e
        L_0x136c:
            r7 = r34
        L_0x136e:
            if (r32 == 0) goto L_0x1382
            org.telegram.messenger.MessagesStorage r0 = r54.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r0 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$Zv5FRAVG-J53wGTHAwMM5uE87Wo r1 = new org.telegram.messenger.-$$Lambda$MessagesController$Zv5FRAVG-J53wGTHAwMM5uE87Wo
            r6 = r32
            r1.<init>(r6)
            r0.postRunnable(r1)
        L_0x1382:
            if (r18 == 0) goto L_0x139b
            org.telegram.messenger.MessagesStorage r17 = r54.getMessagesStorage()
            r19 = 1
            r20 = 1
            r21 = 0
            org.telegram.messenger.DownloadController r0 = r54.getDownloadController()
            int r22 = r0.getAutodownloadMask()
            r23 = 1
            r17.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r18, (boolean) r19, (boolean) r20, (boolean) r21, (int) r22, (boolean) r23)
        L_0x139b:
            if (r24 == 0) goto L_0x13c8
            org.telegram.messenger.StatsController r0 = r54.getStatsController()
            int r1 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            int r2 = r24.size()
            r4 = 1
            r0.incrementReceivedItemsCount(r1, r4, r2)
            org.telegram.messenger.MessagesStorage r19 = r54.getMessagesStorage()
            r21 = 1
            r22 = 1
            r23 = 0
            org.telegram.messenger.DownloadController r0 = r54.getDownloadController()
            int r0 = r0.getAutodownloadMask()
            r25 = 0
            r20 = r24
            r24 = r0
            r19.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r20, (boolean) r21, (boolean) r22, (boolean) r23, (int) r24, (boolean) r25)
        L_0x13c8:
            if (r15 == 0) goto L_0x140b
            int r0 = r15.size()
            r1 = 0
        L_0x13cf:
            if (r1 >= r0) goto L_0x140b
            org.telegram.tgnet.TLRPC$TL_messages_messages r2 = new org.telegram.tgnet.TLRPC$TL_messages_messages
            r2.<init>()
            java.lang.Object r4 = r15.valueAt(r1)
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            int r5 = r4.size()
            r6 = 0
        L_0x13e1:
            if (r6 >= r5) goto L_0x13f3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r2.messages
            java.lang.Object r10 = r4.get(r6)
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            r9.add(r10)
            int r6 = r6 + 1
            goto L_0x13e1
        L_0x13f3:
            org.telegram.messenger.MessagesStorage r16 = r54.getMessagesStorage()
            long r18 = r15.keyAt(r1)
            r20 = -2
            r21 = 0
            r22 = 0
            r23 = 0
            r17 = r2
            r16.putMessages((org.telegram.tgnet.TLRPC.messages_Messages) r17, (long) r18, (int) r20, (int) r21, (boolean) r22, (boolean) r23)
            int r1 = r1 + 1
            goto L_0x13cf
        L_0x140b:
            if (r14 == 0) goto L_0x1415
            org.telegram.messenger.MessagesStorage r0 = r54.getMessagesStorage()
            r1 = 1
            r0.putChannelViews(r14, r1)
        L_0x1415:
            org.telegram.messenger.-$$Lambda$MessagesController$JGthECDkZYjTCuTa_DusXO85MSY r16 = new org.telegram.messenger.-$$Lambda$MessagesController$JGthECDkZYjTCuTa_DusXO85MSY
            r0 = r16
            r10 = r27
            r12 = r31
            r1 = r54
            r9 = r7
            r7 = r29
            r2 = r33
            r17 = 0
            r3 = r36
            r6 = r26
            r5 = r38
            r4 = r39
            r5 = r11
            r11 = r6
            r6 = r28
            r7 = r15
            r15 = r10
            r10 = r35
            r26 = r11
            r11 = r14
            r14 = r12
            r12 = r37
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            org.telegram.messenger.MessagesStorage r0 = r54.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r9 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$xcy_je40mQkkPrT8kyD_ok5QRUE r10 = new org.telegram.messenger.-$$Lambda$MessagesController$xcy_je40mQkkPrT8kyD_ok5QRUE
            r0 = r10
            r2 = r30
            r3 = r26
            r4 = r14
            r5 = r40
            r6 = r29
            r7 = r15
            r8 = r38
            r0.<init>(r2, r3, r4, r5, r6, r7, r8)
            r9.postRunnable(r10)
            if (r39 == 0) goto L_0x146a
            org.telegram.messenger.MessagesStorage r0 = r54.getMessagesStorage()
            r1 = r39
            r0.putWebPages(r1)
        L_0x146a:
            r1 = r26
            if (r30 != 0) goto L_0x1477
            if (r1 != 0) goto L_0x1477
            if (r14 != 0) goto L_0x1477
            r5 = r40
            if (r5 == 0) goto L_0x1493
            goto L_0x1479
        L_0x1477:
            r5 = r40
        L_0x1479:
            if (r30 != 0) goto L_0x1482
            if (r5 == 0) goto L_0x147e
            goto L_0x1482
        L_0x147e:
            r2 = r30
            r3 = 1
            goto L_0x148c
        L_0x1482:
            org.telegram.messenger.MessagesStorage r0 = r54.getMessagesStorage()
            r2 = r30
            r3 = 1
            r0.updateDialogsWithReadMessages(r2, r1, r5, r3)
        L_0x148c:
            org.telegram.messenger.MessagesStorage r0 = r54.getMessagesStorage()
            r0.markMessagesAsRead(r2, r1, r14, r3)
        L_0x1493:
            if (r5 == 0) goto L_0x14a4
            org.telegram.messenger.MessagesStorage r0 = r54.getMessagesStorage()
            org.telegram.tgnet.ConnectionsManager r1 = r54.getConnectionsManager()
            int r1 = r1.getCurrentTime()
            r0.markMessagesContentAsRead(r5, r1)
        L_0x14a4:
            r1 = r29
            if (r1 == 0) goto L_0x14cc
            int r0 = r1.size()
            r2 = 0
        L_0x14ad:
            if (r2 >= r0) goto L_0x14cc
            int r3 = r1.keyAt(r2)
            java.lang.Object r4 = r1.valueAt(r2)
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            org.telegram.messenger.MessagesStorage r5 = r54.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r5 = r5.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$RP3wwVIGzm9hBA1N9dWlKNUkaFQ r6 = new org.telegram.messenger.-$$Lambda$MessagesController$RP3wwVIGzm9hBA1N9dWlKNUkaFQ
            r6.<init>(r4, r3)
            r5.postRunnable(r6)
            int r2 = r2 + 1
            goto L_0x14ad
        L_0x14cc:
            if (r15 == 0) goto L_0x14ed
            int r0 = r15.size()
            r1 = 0
        L_0x14d3:
            if (r1 >= r0) goto L_0x14ed
            int r5 = r15.keyAt(r1)
            java.lang.Object r2 = r15.valueAt(r1)
            r3 = r2
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            org.telegram.messenger.MessagesStorage r2 = r54.getMessagesStorage()
            r4 = 1
            r6 = 0
            r7 = 1
            r2.markMessagesAsDeleted(r3, r4, r5, r6, r7)
            int r1 = r1 + 1
            goto L_0x14d3
        L_0x14ed:
            r1 = r38
            if (r1 == 0) goto L_0x1513
            int r0 = r1.size()
            r2 = 0
        L_0x14f6:
            if (r2 >= r0) goto L_0x1513
            int r3 = r1.keyAt(r2)
            int r4 = r1.valueAt(r2)
            org.telegram.messenger.MessagesStorage r5 = r54.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r5 = r5.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MessagesController$YpS3d6nStkAeVus78dbY1P4pSWY r6 = new org.telegram.messenger.-$$Lambda$MessagesController$YpS3d6nStkAeVus78dbY1P4pSWY
            r6.<init>(r3, r4)
            r5.postRunnable(r6)
            int r2 = r2 + 1
            goto L_0x14f6
        L_0x1513:
            r1 = r50
            if (r1 == 0) goto L_0x1536
            int r0 = r1.size()
            r2 = 0
        L_0x151c:
            if (r2 >= r0) goto L_0x1536
            java.lang.Object r3 = r1.get(r2)
            org.telegram.tgnet.TLRPC$TL_updateEncryptedMessagesRead r3 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r3
            org.telegram.messenger.MessagesStorage r4 = r54.getMessagesStorage()
            int r5 = r3.chat_id
            int r6 = r3.max_date
            int r7 = r3.date
            r8 = 1
            r9 = 0
            r4.createTaskForSecretChat(r5, r6, r7, r8, r9)
            int r2 = r2 + 1
            goto L_0x151c
        L_0x1536:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdateArray(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, boolean, int):boolean");
    }

    public /* synthetic */ void lambda$processUpdateArray$256$MessagesController(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public /* synthetic */ void lambda$processUpdateArray$257$MessagesController(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public /* synthetic */ void lambda$processUpdateArray$259$MessagesController(TLRPC.TL_updateUserBlocked tL_updateUserBlocked) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_updateUserBlocked) {
            private final /* synthetic */ TLRPC.TL_updateUserBlocked f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$258$MessagesController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$258$MessagesController(TLRPC.TL_updateUserBlocked tL_updateUserBlocked) {
        if (!tL_updateUserBlocked.blocked) {
            this.blockedUsers.delete(tL_updateUserBlocked.user_id);
        } else if (this.blockedUsers.indexOfKey(tL_updateUserBlocked.user_id) < 0) {
            this.blockedUsers.put(tL_updateUserBlocked.user_id, 1);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdateArray$260$MessagesController(TLRPC.TL_updateServiceNotification tL_updateServiceNotification) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 2, tL_updateServiceNotification.message, tL_updateServiceNotification.type);
    }

    public /* synthetic */ void lambda$processUpdateArray$261$MessagesController(TLRPC.TL_updateLangPack tL_updateLangPack) {
        LocaleController.getInstance().saveRemoteLocaleStringsForCurrentLocale(tL_updateLangPack.difference, this.currentAccount);
    }

    public /* synthetic */ void lambda$null$262$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, (CountDownLatch) null);
    }

    public /* synthetic */ void lambda$processUpdateArray$263$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.this.lambda$null$262$MessagesController(this.f$1);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:417:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x09a2  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0a54  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0a61  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0a65  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0a84  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0a94  */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0a98  */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0aad  */
    /* JADX WARNING: Removed duplicated region for block: B:503:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processUpdateArray$268$MessagesController(int r33, java.util.ArrayList r34, android.util.LongSparseArray r35, android.util.LongSparseArray r36, android.util.LongSparseArray r37, android.util.LongSparseArray r38, boolean r39, java.util.ArrayList r40, java.util.ArrayList r41, android.util.SparseArray r42, java.util.ArrayList r43) {
        /*
            r32 = this;
            r8 = r32
            r9 = r34
            r10 = r35
            r11 = r36
            r12 = r37
            r13 = r38
            r14 = r41
            r15 = r43
            r3 = 0
            if (r9 == 0) goto L_0x0880
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            int r6 = r34.size()
            r22 = r33
            r7 = 0
            r20 = 0
            r21 = 0
        L_0x0028:
            if (r7 >= r6) goto L_0x0856
            java.lang.Object r0 = r9.get(r7)
            org.telegram.tgnet.TLRPC$Update r0 = (org.telegram.tgnet.TLRPC.Update) r0
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePrivacy
            if (r2 == 0) goto L_0x00c4
            org.telegram.tgnet.TLRPC$TL_updatePrivacy r0 = (org.telegram.tgnet.TLRPC.TL_updatePrivacy) r0
            org.telegram.tgnet.TLRPC$PrivacyKey r2 = r0.key
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp
            if (r1 == 0) goto L_0x004b
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r1.setPrivacyRules(r0, r3)
        L_0x0045:
            r18 = 3
            r19 = 2
            goto L_0x00b9
        L_0x004b:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite
            if (r1 == 0) goto L_0x005a
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r2 = 1
            r1.setPrivacyRules(r0, r2)
            goto L_0x0045
        L_0x005a:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneCall
            if (r1 == 0) goto L_0x0069
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r2 = 2
            r1.setPrivacyRules(r0, r2)
            goto L_0x0045
        L_0x0069:
            r19 = 2
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneP2P
            if (r1 == 0) goto L_0x007c
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r2 = 3
            r1.setPrivacyRules(r0, r2)
            r18 = 3
            goto L_0x00b9
        L_0x007c:
            r18 = 3
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyProfilePhoto
            if (r1 == 0) goto L_0x008d
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r2 = 4
            r1.setPrivacyRules(r0, r2)
            goto L_0x00b9
        L_0x008d:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyForwards
            if (r1 == 0) goto L_0x009c
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r2 = 5
            r1.setPrivacyRules(r0, r2)
            goto L_0x00b9
        L_0x009c:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneNumber
            if (r1 == 0) goto L_0x00ab
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r2 = 6
            r1.setPrivacyRules(r0, r2)
            goto L_0x00b9
        L_0x00ab:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyAddedByPhone
            if (r1 == 0) goto L_0x00b9
            org.telegram.messenger.ContactsController r1 = r32.getContactsController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PrivacyRule> r0 = r0.rules
            r2 = 7
            r1.setPrivacyRules(r0, r2)
        L_0x00b9:
            r31 = r4
            r9 = r5
            r16 = r6
            r17 = r7
        L_0x00c0:
            r1 = r22
            goto L_0x0846
        L_0x00c4:
            r18 = 3
            r19 = 2
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserStatus
            if (r1 == 0) goto L_0x0126
            org.telegram.tgnet.TLRPC$TL_updateUserStatus r0 = (org.telegram.tgnet.TLRPC.TL_updateUserStatus) r0
            int r1 = r0.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r8.getUser(r1)
            org.telegram.tgnet.TLRPC$UserStatus r2 = r0.status
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_userStatusRecently
            if (r3 == 0) goto L_0x00e3
            r3 = -100
            r2.expires = r3
            goto L_0x00f4
        L_0x00e3:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_userStatusLastWeek
            if (r3 == 0) goto L_0x00ec
            r3 = -101(0xffffffffffffff9b, float:NaN)
            r2.expires = r3
            goto L_0x00f4
        L_0x00ec:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_userStatusLastMonth
            if (r3 == 0) goto L_0x00f4
            r3 = -102(0xffffffffffffff9a, float:NaN)
            r2.expires = r3
        L_0x00f4:
            if (r1 == 0) goto L_0x00fe
            int r2 = r0.user_id
            r1.id = r2
            org.telegram.tgnet.TLRPC$UserStatus r2 = r0.status
            r1.status = r2
        L_0x00fe:
            org.telegram.tgnet.TLRPC$TL_user r1 = new org.telegram.tgnet.TLRPC$TL_user
            r1.<init>()
            int r2 = r0.user_id
            r1.id = r2
            org.telegram.tgnet.TLRPC$UserStatus r2 = r0.status
            r1.status = r2
            r4.add(r1)
            int r1 = r0.user_id
            org.telegram.messenger.UserConfig r2 = r32.getUserConfig()
            int r2 = r2.getClientUserId()
            if (r1 != r2) goto L_0x00b9
            org.telegram.messenger.NotificationsController r1 = r32.getNotificationsController()
            org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
            int r0 = r0.expires
            r1.setLastOnlineFromOtherDevice(r0)
            goto L_0x00b9
        L_0x0126:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName
            if (r1 == 0) goto L_0x0182
            org.telegram.tgnet.TLRPC$TL_updateUserName r0 = (org.telegram.tgnet.TLRPC.TL_updateUserName) r0
            int r1 = r0.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r8.getUser(r1)
            if (r1 == 0) goto L_0x0168
            boolean r2 = org.telegram.messenger.UserObject.isContact(r1)
            if (r2 != 0) goto L_0x0146
            java.lang.String r2 = r0.first_name
            r1.first_name = r2
            java.lang.String r2 = r0.last_name
            r1.last_name = r2
        L_0x0146:
            java.lang.String r2 = r1.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0155
            java.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.tgnet.TLObject> r2 = r8.objectsByUsernames
            java.lang.String r3 = r1.username
            r2.remove(r3)
        L_0x0155:
            java.lang.String r2 = r0.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0164
            java.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.tgnet.TLObject> r2 = r8.objectsByUsernames
            java.lang.String r3 = r0.username
            r2.put(r3, r1)
        L_0x0164:
            java.lang.String r2 = r0.username
            r1.username = r2
        L_0x0168:
            org.telegram.tgnet.TLRPC$TL_user r1 = new org.telegram.tgnet.TLRPC$TL_user
            r1.<init>()
            int r2 = r0.user_id
            r1.id = r2
            java.lang.String r2 = r0.first_name
            r1.first_name = r2
            java.lang.String r2 = r0.last_name
            r1.last_name = r2
            java.lang.String r0 = r0.username
            r1.username = r0
            r5.add(r1)
            goto L_0x00b9
        L_0x0182:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogPinned
            if (r1 == 0) goto L_0x01d9
            org.telegram.tgnet.TLRPC$TL_updateDialogPinned r0 = (org.telegram.tgnet.TLRPC.TL_updateDialogPinned) r0
            org.telegram.tgnet.TLRPC$DialogPeer r1 = r0.peer
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer
            if (r2 == 0) goto L_0x0198
            org.telegram.tgnet.TLRPC$TL_dialogPeer r1 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r1
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = org.telegram.messenger.DialogObject.getPeerDialogId((org.telegram.tgnet.TLRPC.Peer) r1)
            r2 = r1
            goto L_0x019a
        L_0x0198:
            r2 = 0
        L_0x019a:
            boolean r1 = r0.pinned
            r26 = 0
            r27 = -1
            r24 = r1
            r1 = r32
            r29 = r2
            r31 = r4
            r4 = r24
            r9 = r5
            r5 = r26
            r16 = r6
            r17 = r7
            r15 = 2
            r6 = r27
            boolean r1 = r1.pinDialog(r2, r4, r5, r6)
            if (r1 != 0) goto L_0x01d5
            org.telegram.messenger.UserConfig r1 = r32.getUserConfig()
            int r2 = r0.folder_id
            r3 = 0
            r1.setPinnedDialogsLoaded(r2, r3)
            org.telegram.messenger.UserConfig r1 = r32.getUserConfig()
            r1.saveConfig(r3)
            int r0 = r0.folder_id
            r1 = r29
            r4 = 0
            r8.loadPinnedDialogs(r0, r1, r4)
            goto L_0x00c0
        L_0x01d5:
            r3 = 0
            r4 = 0
            goto L_0x00c0
        L_0x01d9:
            r31 = r4
            r9 = r5
            r16 = r6
            r17 = r7
            r3 = 0
            r4 = 0
            r15 = 2
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs
            if (r1 == 0) goto L_0x024f
            org.telegram.tgnet.TLRPC$TL_updatePinnedDialogs r0 = (org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs) r0
            org.telegram.messenger.UserConfig r1 = r32.getUserConfig()
            int r2 = r0.folder_id
            r1.setPinnedDialogsLoaded(r2, r3)
            org.telegram.messenger.UserConfig r1 = r32.getUserConfig()
            r1.saveConfig(r3)
            int r1 = r0.flags
            r2 = 1
            r1 = r1 & r2
            if (r1 == 0) goto L_0x0245
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DialogPeer> r5 = r0.order
            int r6 = r5.size()
            r7 = 0
        L_0x020b:
            if (r7 >= r6) goto L_0x0246
            java.lang.Object r19 = r5.get(r7)
            r2 = r19
            org.telegram.tgnet.TLRPC$DialogPeer r2 = (org.telegram.tgnet.TLRPC.DialogPeer) r2
            boolean r15 = r2 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer
            if (r15 == 0) goto L_0x0232
            org.telegram.tgnet.TLRPC$TL_dialogPeer r2 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r2
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            int r15 = r2.user_id
            if (r15 == 0) goto L_0x0225
            r23 = r5
            long r4 = (long) r15
            goto L_0x0236
        L_0x0225:
            r23 = r5
            int r4 = r2.chat_id
            if (r4 == 0) goto L_0x022d
            int r2 = -r4
            goto L_0x0230
        L_0x022d:
            int r2 = r2.channel_id
            int r2 = -r2
        L_0x0230:
            long r4 = (long) r2
            goto L_0x0236
        L_0x0232:
            r23 = r5
            r4 = 0
        L_0x0236:
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            r1.add(r2)
            int r7 = r7 + 1
            r5 = r23
            r2 = 1
            r4 = 0
            r15 = 2
            goto L_0x020b
        L_0x0245:
            r1 = 0
        L_0x0246:
            int r0 = r0.folder_id
            r4 = 0
            r8.loadPinnedDialogs(r0, r4, r1)
            goto L_0x00c0
        L_0x024f:
            r4 = 0
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateFolderPeers
            if (r1 == 0) goto L_0x0292
            org.telegram.tgnet.TLRPC$TL_updateFolderPeers r0 = (org.telegram.tgnet.TLRPC.TL_updateFolderPeers) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_folderPeer> r1 = r0.folder_peers
            int r1 = r1.size()
            r2 = 0
        L_0x025e:
            if (r2 >= r1) goto L_0x028e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_folderPeer> r6 = r0.folder_peers
            java.lang.Object r6 = r6.get(r2)
            org.telegram.tgnet.TLRPC$TL_folderPeer r6 = (org.telegram.tgnet.TLRPC.TL_folderPeer) r6
            org.telegram.tgnet.TLRPC$Peer r7 = r6.peer
            long r4 = org.telegram.messenger.DialogObject.getPeerDialogId((org.telegram.tgnet.TLRPC.Peer) r7)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r7 = r8.dialogs_dict
            java.lang.Object r4 = r7.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC.Dialog) r4
            if (r4 != 0) goto L_0x0279
            goto L_0x0289
        L_0x0279:
            int r5 = r4.folder_id
            int r6 = r6.folder_id
            if (r5 == r6) goto L_0x0289
            r4.pinned = r3
            r4.pinnedNum = r3
            r4.folder_id = r6
            r4 = 0
            r8.ensureFolderDialogExists(r6, r4)
        L_0x0289:
            int r2 = r2 + 1
            r4 = 0
            goto L_0x025e
        L_0x028e:
            r21 = 1
            goto L_0x0848
        L_0x0292:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhoto
            if (r1 == 0) goto L_0x02ba
            org.telegram.tgnet.TLRPC$TL_updateUserPhoto r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhoto) r0
            int r1 = r0.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r8.getUser(r1)
            if (r1 == 0) goto L_0x02a8
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r0.photo
            r1.photo = r2
        L_0x02a8:
            org.telegram.tgnet.TLRPC$TL_user r1 = new org.telegram.tgnet.TLRPC$TL_user
            r1.<init>()
            int r2 = r0.user_id
            r1.id = r2
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            r1.photo = r0
            r9.add(r1)
            goto L_0x00c0
        L_0x02ba:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhone
            if (r1 == 0) goto L_0x02fd
            org.telegram.tgnet.TLRPC$TL_updateUserPhone r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhone) r0
            int r1 = r0.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r8.getUser(r1)
            if (r1 == 0) goto L_0x02eb
            java.lang.String r2 = r0.phone
            r1.phone = r2
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.phoneBookQueue
            org.telegram.messenger.-$$Lambda$MessagesController$k7hEf2Sqkmp6V-chvj_ni0sdttw r4 = new org.telegram.messenger.-$$Lambda$MessagesController$k7hEf2Sqkmp6V-chvj_ni0sdttw
            r4.<init>(r1)
            r2.postRunnable(r4)
            boolean r1 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r1 == 0) goto L_0x02eb
            org.telegram.messenger.NotificationCenter r1 = r32.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r1.postNotificationName(r2, r4)
        L_0x02eb:
            org.telegram.tgnet.TLRPC$TL_user r1 = new org.telegram.tgnet.TLRPC$TL_user
            r1.<init>()
            int r2 = r0.user_id
            r1.id = r2
            java.lang.String r0 = r0.phone
            r1.phone = r0
            r9.add(r1)
            goto L_0x00c0
        L_0x02fd:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNotifySettings
            if (r1 == 0) goto L_0x04a1
            org.telegram.tgnet.TLRPC$TL_updateNotifySettings r0 = (org.telegram.tgnet.TLRPC.TL_updateNotifySettings) r0
            org.telegram.tgnet.TLRPC$PeerNotifySettings r1 = r0.notify_settings
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerNotifySettings
            if (r1 == 0) goto L_0x0848
            if (r20 != 0) goto L_0x0311
            android.content.SharedPreferences r1 = r8.notificationsPreferences
            android.content.SharedPreferences$Editor r20 = r1.edit()
        L_0x0311:
            r1 = r20
            org.telegram.tgnet.ConnectionsManager r2 = r32.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            org.telegram.tgnet.TLRPC$NotifyPeer r4 = r0.peer
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyPeer
            if (r5 == 0) goto L_0x0435
            org.telegram.tgnet.TLRPC$TL_notifyPeer r4 = (org.telegram.tgnet.TLRPC.TL_notifyPeer) r4
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer
            int r5 = r4.user_id
            if (r5 == 0) goto L_0x032b
            long r4 = (long) r5
            goto L_0x0335
        L_0x032b:
            int r5 = r4.chat_id
            if (r5 == 0) goto L_0x0331
            int r4 = -r5
            goto L_0x0334
        L_0x0331:
            int r4 = r4.channel_id
            int r4 = -r4
        L_0x0334:
            long r4 = (long) r4
        L_0x0335:
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r6 = r8.dialogs_dict
            java.lang.Object r6 = r6.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC.Dialog) r6
            if (r6 == 0) goto L_0x0343
            org.telegram.tgnet.TLRPC$PeerNotifySettings r7 = r0.notify_settings
            r6.notify_settings = r7
        L_0x0343:
            org.telegram.tgnet.TLRPC$PeerNotifySettings r7 = r0.notify_settings
            int r7 = r7.flags
            r15 = 2
            r7 = r7 & r15
            java.lang.String r15 = "silent_"
            if (r7 == 0) goto L_0x0364
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r15)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            org.telegram.tgnet.TLRPC$PeerNotifySettings r15 = r0.notify_settings
            boolean r15 = r15.silent
            r1.putBoolean(r7, r15)
            goto L_0x0376
        L_0x0364:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r15)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            r1.remove(r7)
        L_0x0376:
            org.telegram.tgnet.TLRPC$PeerNotifySettings r7 = r0.notify_settings
            int r15 = r7.flags
            r20 = 4
            r15 = r15 & 4
            java.lang.String r3 = "notify2_"
            if (r15 == 0) goto L_0x0414
            int r15 = r7.mute_until
            if (r15 <= r2) goto L_0x03f2
            r7 = 31536000(0x1e13380, float:8.2725845E-38)
            int r2 = r2 + r7
            if (r15 <= r2) goto L_0x03aa
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r3 = 2
            r1.putInt(r2, r3)
            if (r6 == 0) goto L_0x03a8
            org.telegram.tgnet.TLRPC$PeerNotifySettings r0 = r0.notify_settings
            r2 = 2147483647(0x7fffffff, float:NaN)
            r0.mute_until = r2
        L_0x03a8:
            r15 = 0
            goto L_0x03db
        L_0x03aa:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r3 = 3
            r1.putInt(r2, r3)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "notifyuntil_"
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            org.telegram.tgnet.TLRPC$PeerNotifySettings r3 = r0.notify_settings
            int r3 = r3.mute_until
            r1.putInt(r2, r3)
            if (r6 == 0) goto L_0x03db
            org.telegram.tgnet.TLRPC$PeerNotifySettings r0 = r0.notify_settings
            r0.mute_until = r15
        L_0x03db:
            org.telegram.messenger.MessagesStorage r0 = r32.getMessagesStorage()
            long r2 = (long) r15
            r6 = 32
            long r2 = r2 << r6
            r6 = 1
            long r2 = r2 | r6
            r0.setDialogFlags(r4, r2)
            org.telegram.messenger.NotificationsController r0 = r32.getNotificationsController()
            r0.removeNotificationsForDialog(r4)
            goto L_0x049d
        L_0x03f2:
            r2 = 0
            if (r6 == 0) goto L_0x03f7
            r7.mute_until = r2
        L_0x03f7:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            r1.putInt(r0, r2)
            org.telegram.messenger.MessagesStorage r0 = r32.getMessagesStorage()
            r6 = 0
            r0.setDialogFlags(r4, r6)
            goto L_0x049d
        L_0x0414:
            r2 = 0
            if (r6 == 0) goto L_0x0419
            r7.mute_until = r2
        L_0x0419:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            r1.remove(r0)
            org.telegram.messenger.MessagesStorage r0 = r32.getMessagesStorage()
            r2 = 0
            r0.setDialogFlags(r4, r2)
            goto L_0x049d
        L_0x0435:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyChats
            if (r2 == 0) goto L_0x0458
            org.telegram.tgnet.TLRPC$PeerNotifySettings r2 = r0.notify_settings
            int r3 = r2.flags
            r4 = 1
            r3 = r3 & r4
            if (r3 == 0) goto L_0x0448
            boolean r2 = r2.show_previews
            java.lang.String r3 = "EnablePreviewGroup"
            r1.putBoolean(r3, r2)
        L_0x0448:
            org.telegram.tgnet.TLRPC$PeerNotifySettings r0 = r0.notify_settings
            int r2 = r0.flags
            r3 = 4
            r2 = r2 & r3
            if (r2 == 0) goto L_0x049d
            int r0 = r0.mute_until
            java.lang.String r2 = "EnableGroup2"
            r1.putInt(r2, r0)
            goto L_0x049d
        L_0x0458:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyUsers
            if (r2 == 0) goto L_0x047b
            org.telegram.tgnet.TLRPC$PeerNotifySettings r2 = r0.notify_settings
            int r3 = r2.flags
            r4 = 1
            r3 = r3 & r4
            if (r3 == 0) goto L_0x046b
            boolean r2 = r2.show_previews
            java.lang.String r3 = "EnablePreviewAll"
            r1.putBoolean(r3, r2)
        L_0x046b:
            org.telegram.tgnet.TLRPC$PeerNotifySettings r0 = r0.notify_settings
            int r2 = r0.flags
            r3 = 4
            r2 = r2 & r3
            if (r2 == 0) goto L_0x049d
            int r0 = r0.mute_until
            java.lang.String r2 = "EnableAll2"
            r1.putInt(r2, r0)
            goto L_0x049d
        L_0x047b:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyBroadcasts
            if (r2 == 0) goto L_0x049d
            org.telegram.tgnet.TLRPC$PeerNotifySettings r2 = r0.notify_settings
            int r3 = r2.flags
            r4 = 1
            r3 = r3 & r4
            if (r3 == 0) goto L_0x048e
            boolean r2 = r2.show_previews
            java.lang.String r3 = "EnablePreviewChannel"
            r1.putBoolean(r3, r2)
        L_0x048e:
            org.telegram.tgnet.TLRPC$PeerNotifySettings r0 = r0.notify_settings
            int r2 = r0.flags
            r3 = 4
            r2 = r2 & r3
            if (r2 == 0) goto L_0x049d
            int r0 = r0.mute_until
            java.lang.String r2 = "EnableChannel2"
            r1.putInt(r2, r0)
        L_0x049d:
            r20 = r1
            goto L_0x0848
        L_0x04a1:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannel
            if (r1 == 0) goto L_0x04fa
            org.telegram.tgnet.TLRPC$TL_updateChannel r0 = (org.telegram.tgnet.TLRPC.TL_updateChannel) r0
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r1 = r8.dialogs_dict
            int r2 = r0.channel_id
            long r2 = (long) r2
            long r2 = -r2
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC.Dialog) r1
            int r2 = r0.channel_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r8.getChat(r2)
            if (r2 == 0) goto L_0x04ed
            if (r1 != 0) goto L_0x04d4
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channel
            if (r3 == 0) goto L_0x04d4
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x04d4
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$MessagesController$wJ5JalVVW9yaNMkgKt1IKwZClNY r2 = new org.telegram.messenger.-$$Lambda$MessagesController$wJ5JalVVW9yaNMkgKt1IKwZClNY
            r2.<init>(r0)
            r1.postRunnable(r2)
            goto L_0x04ed
        L_0x04d4:
            boolean r2 = r2.left
            if (r2 == 0) goto L_0x04ed
            if (r1 == 0) goto L_0x04ed
            org.telegram.tgnet.TLRPC$Dialog r2 = r8.proxyDialog
            if (r2 == 0) goto L_0x04e6
            long r2 = r2.id
            long r4 = r1.id
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x04ed
        L_0x04e6:
            long r1 = r1.id
            r3 = 0
            r8.deleteDialog(r1, r3)
            goto L_0x04ee
        L_0x04ed:
            r3 = 0
        L_0x04ee:
            r1 = r22
            r1 = r1 | 8192(0x2000, float:1.14794E-41)
            int r0 = r0.channel_id
            r2 = 1
            r8.loadFullChat(r0, r3, r2)
            goto L_0x0846
        L_0x04fa:
            r1 = r22
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights
            if (r2 == 0) goto L_0x0523
            org.telegram.tgnet.TLRPC$TL_updateChatDefaultBannedRights r0 = (org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights) r0
            org.telegram.tgnet.TLRPC$Peer r2 = r0.peer
            int r3 = r2.channel_id
            if (r3 == 0) goto L_0x0509
            goto L_0x050b
        L_0x0509:
            int r3 = r2.chat_id
        L_0x050b:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r8.getChat(r2)
            if (r2 == 0) goto L_0x0846
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r0.default_banned_rights
            r2.default_banned_rights = r0
            org.telegram.messenger.-$$Lambda$MessagesController$-_A-zPdho0WiN66Q5g4H70UkDlI r0 = new org.telegram.messenger.-$$Lambda$MessagesController$-_A-zPdho0WiN66Q5g4H70UkDlI
            r0.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0846
        L_0x0523:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSets
            if (r2 == 0) goto L_0x0534
            org.telegram.tgnet.TLRPC$TL_updateStickerSets r0 = (org.telegram.tgnet.TLRPC.TL_updateStickerSets) r0
            org.telegram.messenger.MediaDataController r0 = r32.getMediaDataController()
            r2 = 0
            r3 = 1
            r0.loadStickers(r2, r2, r3)
            goto L_0x0846
        L_0x0534:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder
            if (r2 == 0) goto L_0x0547
            org.telegram.tgnet.TLRPC$TL_updateStickerSetsOrder r0 = (org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder) r0
            org.telegram.messenger.MediaDataController r2 = r32.getMediaDataController()
            boolean r3 = r0.masks
            java.util.ArrayList<java.lang.Long> r0 = r0.order
            r2.reorderStickers(r3, r0)
            goto L_0x0846
        L_0x0547:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateFavedStickers
            if (r2 == 0) goto L_0x0557
            org.telegram.messenger.MediaDataController r0 = r32.getMediaDataController()
            r2 = 2
            r3 = 0
            r4 = 1
            r0.loadRecents(r2, r3, r3, r4)
            goto L_0x0846
        L_0x0557:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateContactsReset
            if (r2 == 0) goto L_0x0564
            org.telegram.messenger.ContactsController r0 = r32.getContactsController()
            r0.forceImportContacts()
            goto L_0x0846
        L_0x0564:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewStickerSet
            if (r2 == 0) goto L_0x0575
            org.telegram.tgnet.TLRPC$TL_updateNewStickerSet r0 = (org.telegram.tgnet.TLRPC.TL_updateNewStickerSet) r0
            org.telegram.messenger.MediaDataController r2 = r32.getMediaDataController()
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r0.stickerset
            r2.addNewStickerSet(r0)
            goto L_0x0846
        L_0x0575:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateSavedGifs
            if (r2 == 0) goto L_0x058c
            android.content.SharedPreferences r0 = r8.emojiPreferences
            android.content.SharedPreferences$Editor r0 = r0.edit()
            java.lang.String r2 = "lastGifLoadTime"
            r6 = 0
            android.content.SharedPreferences$Editor r0 = r0.putLong(r2, r6)
            r0.commit()
            goto L_0x0846
        L_0x058c:
            r6 = 0
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateRecentStickers
            if (r2 == 0) goto L_0x05a3
            android.content.SharedPreferences r0 = r8.emojiPreferences
            android.content.SharedPreferences$Editor r0 = r0.edit()
            java.lang.String r2 = "lastStickersLoadTime"
            android.content.SharedPreferences$Editor r0 = r0.putLong(r2, r6)
            r0.commit()
            goto L_0x0846
        L_0x05a3:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDraftMessage
            if (r2 == 0) goto L_0x05d1
            org.telegram.tgnet.TLRPC$TL_updateDraftMessage r0 = (org.telegram.tgnet.TLRPC.TL_updateDraftMessage) r0
            org.telegram.tgnet.TLRPC$Peer r2 = r0.peer
            int r3 = r2.user_id
            if (r3 == 0) goto L_0x05b3
            long r2 = (long) r3
        L_0x05b0:
            r22 = r2
            goto L_0x05be
        L_0x05b3:
            int r3 = r2.channel_id
            if (r3 == 0) goto L_0x05b9
            int r2 = -r3
            goto L_0x05bc
        L_0x05b9:
            int r2 = r2.chat_id
            int r2 = -r2
        L_0x05bc:
            long r2 = (long) r2
            goto L_0x05b0
        L_0x05be:
            org.telegram.messenger.MediaDataController r21 = r32.getMediaDataController()
            org.telegram.tgnet.TLRPC$DraftMessage r0 = r0.draft
            r25 = 0
            r26 = 1
            r24 = r0
            r21.saveDraft(r22, r24, r25, r26)
            r22 = r1
            goto L_0x028e
        L_0x05d1:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers
            if (r2 == 0) goto L_0x05df
            org.telegram.messenger.MediaDataController r0 = r32.getMediaDataController()
            r2 = 0
            r0.markFaturedStickersAsRead(r2)
            goto L_0x0846
        L_0x05df:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePhoneCall
            if (r2 == 0) goto L_0x073e
            org.telegram.tgnet.TLRPC$TL_updatePhoneCall r0 = (org.telegram.tgnet.TLRPC.TL_updatePhoneCall) r0
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r0.phone_call
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x0619
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Received call in update: "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "call id "
            r3.append(r4)
            long r4 = r0.id
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x0619:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallRequested
            if (r3 == 0) goto L_0x071a
            int r3 = r0.date
            int r4 = r8.callRingTimeout
            int r4 = r4 / 1000
            int r3 = r3 + r4
            org.telegram.tgnet.ConnectionsManager r4 = r32.getConnectionsManager()
            int r4 = r4.getCurrentTime()
            if (r3 >= r4) goto L_0x0639
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0846
            java.lang.String r0 = "ignoring too old call"
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x0846
        L_0x0639:
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r3 < r4) goto L_0x0656
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.app.NotificationManagerCompat r3 = androidx.core.app.NotificationManagerCompat.from(r3)
            boolean r3 = r3.areNotificationsEnabled()
            if (r3 != 0) goto L_0x0656
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0846
            java.lang.String r0 = "Ignoring incoming call because notifications are disabled in system"
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x0846
        L_0x0656:
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r4 = "phone"
            java.lang.Object r3 = r3.getSystemService(r4)
            android.telephony.TelephonyManager r3 = (android.telephony.TelephonyManager) r3
            if (r2 != 0) goto L_0x06d0
            org.telegram.tgnet.TLRPC$PhoneCall r2 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent
            if (r2 != 0) goto L_0x06d0
            int r2 = r3.getCallState()
            if (r2 == 0) goto L_0x066d
            goto L_0x06d0
        L_0x066d:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0687
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Starting service for call "
            r2.append(r3)
            long r3 = r0.id
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0687:
            org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent = r0
            android.content.Intent r2 = new android.content.Intent
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.voip.VoIPService> r4 = org.telegram.messenger.voip.VoIPService.class
            r2.<init>(r3, r4)
            java.lang.String r3 = "is_outgoing"
            r4 = 0
            r2.putExtra(r3, r4)
            int r3 = r0.participant_id
            org.telegram.messenger.UserConfig r4 = r32.getUserConfig()
            int r4 = r4.getClientUserId()
            if (r3 != r4) goto L_0x06a7
            int r0 = r0.admin_id
            goto L_0x06a9
        L_0x06a7:
            int r0 = r0.participant_id
        L_0x06a9:
            java.lang.String r3 = "user_id"
            r2.putExtra(r3, r0)
            int r0 = r8.currentAccount
            java.lang.String r3 = "account"
            r2.putExtra(r3, r0)
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x06ca }
            r3 = 26
            if (r0 < r3) goto L_0x06c3
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x06ca }
            r0.startForegroundService(r2)     // Catch:{ all -> 0x06ca }
            goto L_0x0846
        L_0x06c3:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x06ca }
            r0.startService(r2)     // Catch:{ all -> 0x06ca }
            goto L_0x0846
        L_0x06ca:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0846
        L_0x06d0:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x06ef
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Auto-declining call "
            r2.append(r3)
            long r3 = r0.id
            r2.append(r3)
            java.lang.String r3 = " because there's already active one"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x06ef:
            org.telegram.tgnet.TLRPC$TL_phone_discardCall r2 = new org.telegram.tgnet.TLRPC$TL_phone_discardCall
            r2.<init>()
            org.telegram.tgnet.TLRPC$TL_inputPhoneCall r3 = new org.telegram.tgnet.TLRPC$TL_inputPhoneCall
            r3.<init>()
            r2.peer = r3
            org.telegram.tgnet.TLRPC$TL_inputPhoneCall r3 = r2.peer
            long r4 = r0.access_hash
            r3.access_hash = r4
            long r4 = r0.id
            r3.id = r4
            org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy r0 = new org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy
            r0.<init>()
            r2.reason = r0
            org.telegram.tgnet.ConnectionsManager r0 = r32.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$KwSrP312VOB27NH519srqu8bVm0 r3 = new org.telegram.messenger.-$$Lambda$MessagesController$KwSrP312VOB27NH519srqu8bVm0
            r3.<init>()
            r0.sendRequest(r2, r3)
            goto L_0x0846
        L_0x071a:
            if (r2 == 0) goto L_0x0723
            if (r0 == 0) goto L_0x0723
            r2.onCallUpdated(r0)
            goto L_0x0846
        L_0x0723:
            org.telegram.tgnet.TLRPC$PhoneCall r2 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent
            if (r2 == 0) goto L_0x0846
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0730
            java.lang.String r2 = "Updated the call while the service is starting"
            org.telegram.messenger.FileLog.d(r2)
        L_0x0730:
            long r2 = r0.id
            org.telegram.tgnet.TLRPC$PhoneCall r4 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent
            long r4 = r4.id
            int r15 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r15 != 0) goto L_0x0846
            org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent = r0
            goto L_0x0846
        L_0x073e:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark
            if (r2 == 0) goto L_0x079f
            org.telegram.tgnet.TLRPC$TL_updateDialogUnreadMark r0 = (org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark) r0
            org.telegram.tgnet.TLRPC$DialogPeer r2 = r0.peer
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer
            if (r3 == 0) goto L_0x0760
            org.telegram.tgnet.TLRPC$TL_dialogPeer r2 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r2
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            int r3 = r2.user_id
            if (r3 == 0) goto L_0x0755
            long r2 = (long) r3
        L_0x0753:
            r4 = r2
            goto L_0x0761
        L_0x0755:
            int r3 = r2.chat_id
            if (r3 == 0) goto L_0x075b
            int r2 = -r3
            goto L_0x075e
        L_0x075b:
            int r2 = r2.channel_id
            int r2 = -r2
        L_0x075e:
            long r2 = (long) r2
            goto L_0x0753
        L_0x0760:
            r4 = r6
        L_0x0761:
            org.telegram.messenger.MessagesStorage r2 = r32.getMessagesStorage()
            boolean r3 = r0.unread
            r2.setDialogUnread(r4, r3)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r8.dialogs_dict
            java.lang.Object r2 = r2.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC.Dialog) r2
            if (r2 == 0) goto L_0x079a
            boolean r3 = r2.unread_mark
            boolean r0 = r0.unread
            if (r3 == r0) goto L_0x079a
            r2.unread_mark = r0
            int r0 = r2.unread_count
            if (r0 != 0) goto L_0x0797
            boolean r0 = r8.isDialogMuted(r4)
            if (r0 != 0) goto L_0x0797
            boolean r0 = r2.unread_mark
            if (r0 == 0) goto L_0x0791
            int r0 = r8.unreadUnmutedDialogs
            r2 = 1
            int r0 = r0 + r2
            r8.unreadUnmutedDialogs = r0
            goto L_0x0797
        L_0x0791:
            r2 = 1
            int r0 = r8.unreadUnmutedDialogs
            int r0 = r0 - r2
            r8.unreadUnmutedDialogs = r0
        L_0x0797:
            r0 = r1 | 256(0x100, float:3.59E-43)
            goto L_0x079b
        L_0x079a:
            r0 = r1
        L_0x079b:
            r22 = r0
            goto L_0x0848
        L_0x079f:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateMessagePoll
            if (r2 == 0) goto L_0x07c6
            org.telegram.tgnet.TLRPC$TL_updateMessagePoll r0 = (org.telegram.tgnet.TLRPC.TL_updateMessagePoll) r0
            org.telegram.messenger.NotificationCenter r2 = r32.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.didUpdatePollResults
            r4 = 3
            java.lang.Object[] r5 = new java.lang.Object[r4]
            long r6 = r0.poll_id
            java.lang.Long r4 = java.lang.Long.valueOf(r6)
            r6 = 0
            r5[r6] = r4
            org.telegram.tgnet.TLRPC$TL_poll r4 = r0.poll
            r6 = 1
            r5[r6] = r4
            org.telegram.tgnet.TLRPC$PollResults r0 = r0.results
            r4 = 2
            r5[r4] = r0
            r2.postNotificationName(r3, r5)
            goto L_0x0846
        L_0x07c6:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerSettings
            if (r2 == 0) goto L_0x07e8
            org.telegram.tgnet.TLRPC$TL_updatePeerSettings r0 = (org.telegram.tgnet.TLRPC.TL_updatePeerSettings) r0
            org.telegram.tgnet.TLRPC$Peer r2 = r0.peer
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_peerUser
            if (r3 == 0) goto L_0x07d6
            int r2 = r2.user_id
        L_0x07d4:
            long r2 = (long) r2
            goto L_0x07e1
        L_0x07d6:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_peerChat
            if (r3 == 0) goto L_0x07dd
            int r2 = r2.chat_id
            goto L_0x07df
        L_0x07dd:
            int r2 = r2.channel_id
        L_0x07df:
            int r2 = -r2
            goto L_0x07d4
        L_0x07e1:
            org.telegram.tgnet.TLRPC$TL_peerSettings r0 = r0.settings
            r4 = 1
            r8.savePeerSettings(r2, r0, r4)
            goto L_0x0846
        L_0x07e8:
            r4 = 1
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerLocated
            if (r2 == 0) goto L_0x07fc
            org.telegram.messenger.NotificationCenter r2 = r32.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.newPeopleNearbyAvailable
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r4 = 0
            r5[r4] = r0
            r2.postNotificationName(r3, r5)
            goto L_0x0846
        L_0x07fc:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateMessageReactions
            if (r2 == 0) goto L_0x0835
            org.telegram.tgnet.TLRPC$TL_updateMessageReactions r0 = (org.telegram.tgnet.TLRPC.TL_updateMessageReactions) r0
            org.telegram.tgnet.TLRPC$Peer r2 = r0.peer
            int r3 = r2.chat_id
            if (r3 == 0) goto L_0x080b
        L_0x0808:
            int r2 = -r3
        L_0x0809:
            long r2 = (long) r2
            goto L_0x0813
        L_0x080b:
            int r3 = r2.channel_id
            if (r3 == 0) goto L_0x0810
            goto L_0x0808
        L_0x0810:
            int r2 = r2.user_id
            goto L_0x0809
        L_0x0813:
            org.telegram.messenger.NotificationCenter r4 = r32.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.didUpdateReactions
            r6 = 3
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r3 = 0
            r7[r3] = r2
            int r2 = r0.msg_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3 = 1
            r7[r3] = r2
            org.telegram.tgnet.TLRPC$TL_messageReactions r0 = r0.reactions
            r2 = 2
            r7[r2] = r0
            r4.postNotificationName(r5, r7)
            goto L_0x0846
        L_0x0835:
            r3 = 1
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateTheme
            if (r2 == 0) goto L_0x0846
            org.telegram.tgnet.TLRPC$TL_updateTheme r0 = (org.telegram.tgnet.TLRPC.TL_updateTheme) r0
            org.telegram.tgnet.TLRPC$Theme r0 = r0.theme
            org.telegram.tgnet.TLRPC$TL_theme r0 = (org.telegram.tgnet.TLRPC.TL_theme) r0
            int r2 = r8.currentAccount
            r4 = 0
            org.telegram.ui.ActionBar.Theme.setThemeUploadInfo(r4, r4, r0, r2, r3)
        L_0x0846:
            r22 = r1
        L_0x0848:
            int r7 = r17 + 1
            r15 = r43
            r5 = r9
            r6 = r16
            r4 = r31
            r3 = 0
            r9 = r34
            goto L_0x0028
        L_0x0856:
            r31 = r4
            r9 = r5
            r1 = r22
            if (r20 == 0) goto L_0x086d
            r20.commit()
            org.telegram.messenger.NotificationCenter r0 = r32.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.notificationsSettingsUpdated
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r0.postNotificationName(r2, r4)
            goto L_0x086e
        L_0x086d:
            r3 = 0
        L_0x086e:
            org.telegram.messenger.MessagesStorage r0 = r32.getMessagesStorage()
            r2 = r31
            r4 = 1
            r0.updateUsers(r2, r4, r4, r4)
            org.telegram.messenger.MessagesStorage r0 = r32.getMessagesStorage()
            r0.updateUsers(r9, r3, r4, r4)
            goto L_0x0885
        L_0x0880:
            r4 = 1
            r1 = r33
            r21 = 0
        L_0x0885:
            if (r10 == 0) goto L_0x0963
            org.telegram.messenger.NotificationCenter r0 = r32.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.didReceivedWebpagesInUpdates
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r5[r3] = r10
            r0.postNotificationName(r2, r5)
            r0 = 0
            r2 = 2
        L_0x0896:
            if (r0 >= r2) goto L_0x0963
            if (r0 != r4) goto L_0x089d
            android.util.LongSparseArray<java.util.ArrayList<org.telegram.messenger.MessageObject>> r2 = r8.reloadingScheduledWebpagesPending
            goto L_0x089f
        L_0x089d:
            android.util.LongSparseArray<java.util.ArrayList<org.telegram.messenger.MessageObject>> r2 = r8.reloadingWebpagesPending
        L_0x089f:
            int r3 = r35.size()
            r4 = 0
        L_0x08a4:
            if (r4 >= r3) goto L_0x095b
            long r5 = r10.keyAt(r4)
            java.lang.Object r7 = r2.get(r5)
            java.util.ArrayList r7 = (java.util.ArrayList) r7
            r2.remove(r5)
            if (r7 == 0) goto L_0x0951
            java.lang.Object r5 = r10.valueAt(r4)
            org.telegram.tgnet.TLRPC$WebPage r5 = (org.telegram.tgnet.TLRPC.WebPage) r5
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPage
            if (r9 != 0) goto L_0x08d3
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPageEmpty
            if (r9 == 0) goto L_0x08c9
            goto L_0x08d3
        L_0x08c9:
            long r9 = r5.id
            r2.put(r9, r7)
            r20 = r2
            r15 = 0
            goto L_0x0915
        L_0x08d3:
            int r9 = r7.size()
            r10 = 0
            r15 = 0
        L_0x08da:
            if (r10 >= r9) goto L_0x0913
            java.lang.Object r17 = r7.get(r10)
            r20 = r2
            r2 = r17
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            r2.webpage = r5
            if (r10 != 0) goto L_0x0903
            java.lang.Object r2 = r7.get(r10)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r15 = r2.getDialogId()
            java.lang.Object r2 = r7.get(r10)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r2)
        L_0x0903:
            java.lang.Object r2 = r7.get(r10)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            r6.add(r2)
            int r10 = r10 + 1
            r2 = r20
            goto L_0x08da
        L_0x0913:
            r20 = r2
        L_0x0915:
            boolean r2 = r6.isEmpty()
            if (r2 != 0) goto L_0x0953
            org.telegram.messenger.MessagesStorage r22 = r32.getMessagesStorage()
            r24 = 1
            r25 = 1
            r26 = 0
            org.telegram.messenger.DownloadController r2 = r32.getDownloadController()
            int r27 = r2.getAutodownloadMask()
            r2 = 1
            if (r0 != r2) goto L_0x0933
            r28 = 1
            goto L_0x0935
        L_0x0933:
            r28 = 0
        L_0x0935:
            r23 = r6
            r22.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r23, (boolean) r24, (boolean) r25, (boolean) r26, (int) r27, (boolean) r28)
            org.telegram.messenger.NotificationCenter r2 = r32.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects
            r6 = 2
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.lang.Long r6 = java.lang.Long.valueOf(r15)
            r10 = 0
            r9[r10] = r6
            r6 = 1
            r9[r6] = r7
            r2.postNotificationName(r5, r9)
            goto L_0x0953
        L_0x0951:
            r20 = r2
        L_0x0953:
            int r4 = r4 + 1
            r10 = r35
            r2 = r20
            goto L_0x08a4
        L_0x095b:
            int r0 = r0 + 1
            r10 = r35
            r2 = 2
            r4 = 1
            goto L_0x0896
        L_0x0963:
            if (r11 == 0) goto L_0x097d
            int r0 = r36.size()
            r2 = 0
        L_0x096a:
            if (r2 >= r0) goto L_0x0983
            long r3 = r11.keyAt(r2)
            java.lang.Object r5 = r11.valueAt(r2)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            r6 = 0
            r8.updateInterfaceWithMessages(r3, r5, r6)
            int r2 = r2 + 1
            goto L_0x096a
        L_0x097d:
            if (r21 == 0) goto L_0x0985
            r2 = 0
            r8.sortDialogs(r2)
        L_0x0983:
            r0 = 1
            goto L_0x0986
        L_0x0985:
            r0 = 0
        L_0x0986:
            if (r12 == 0) goto L_0x09a0
            int r2 = r37.size()
            r3 = 0
        L_0x098d:
            if (r3 >= r2) goto L_0x09a0
            long r4 = r12.keyAt(r3)
            java.lang.Object r6 = r12.valueAt(r3)
            java.util.ArrayList r6 = (java.util.ArrayList) r6
            r7 = 1
            r8.updateInterfaceWithMessages(r4, r6, r7)
            int r3 = r3 + 1
            goto L_0x098d
        L_0x09a0:
            if (r13 == 0) goto L_0x0a51
            int r2 = r38.size()
            r3 = r0
            r0 = 0
        L_0x09a8:
            if (r0 >= r2) goto L_0x0a4e
            long r4 = r13.keyAt(r0)
            java.lang.Object r6 = r13.valueAt(r0)
            java.util.ArrayList r6 = (java.util.ArrayList) r6
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r7 = r8.dialogMessage
            java.lang.Object r7 = r7.get(r4)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            if (r7 == 0) goto L_0x09ee
            int r9 = r6.size()
            r10 = 0
        L_0x09c3:
            if (r10 >= r9) goto L_0x09ee
            java.lang.Object r11 = r6.get(r10)
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            int r12 = r7.getId()
            int r15 = r11.getId()
            if (r12 != r15) goto L_0x09f0
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r8.dialogMessage
            r3.put(r4, r11)
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.to_id
            if (r3 == 0) goto L_0x09ed
            int r3 = r3.channel_id
            if (r3 != 0) goto L_0x09ed
            android.util.SparseArray<org.telegram.messenger.MessageObject> r3 = r8.dialogMessagesByIds
            int r7 = r11.getId()
            r3.put(r7, r11)
        L_0x09ed:
            r3 = 1
        L_0x09ee:
            r11 = 0
            goto L_0x0a1e
        L_0x09f0:
            long r15 = r7.getDialogId()
            long r20 = r11.getDialogId()
            int r12 = (r15 > r20 ? 1 : (r15 == r20 ? 0 : -1))
            if (r12 != 0) goto L_0x0a1a
            org.telegram.tgnet.TLRPC$Message r12 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r12 = r12.action
            boolean r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r12 == 0) goto L_0x0a1a
            org.telegram.messenger.MessageObject r12 = r7.replyMessageObject
            if (r12 == 0) goto L_0x0a1a
            int r12 = r12.getId()
            int r15 = r11.getId()
            if (r12 != r15) goto L_0x0a1a
            r7.replyMessageObject = r11
            r11 = 0
            r7.generatePinMessageText(r11, r11)
            r3 = 1
            goto L_0x0a1e
        L_0x0a1a:
            r11 = 0
            int r10 = r10 + 1
            goto L_0x09c3
        L_0x0a1e:
            org.telegram.messenger.MediaDataController r20 = r32.getMediaDataController()
            r24 = 0
            r25 = 0
            r21 = r6
            r22 = r4
            r20.loadReplyMessagesForMessages(r21, r22, r24, r25)
            org.telegram.messenger.NotificationCenter r7 = r32.getNotificationCenter()
            int r9 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects
            r10 = 3
            java.lang.Object[] r12 = new java.lang.Object[r10]
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            r5 = 0
            r12[r5] = r4
            r4 = 1
            r12[r4] = r6
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r5)
            r6 = 2
            r12[r6] = r4
            r7.postNotificationName(r9, r12)
            int r0 = r0 + 1
            goto L_0x09a8
        L_0x0a4e:
            r5 = 0
            r0 = r3
            goto L_0x0a52
        L_0x0a51:
            r5 = 0
        L_0x0a52:
            if (r0 == 0) goto L_0x0a5f
            org.telegram.messenger.NotificationCenter r0 = r32.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r0.postNotificationName(r2, r3)
        L_0x0a5f:
            if (r39 == 0) goto L_0x0a63
            r1 = r1 | 64
        L_0x0a63:
            if (r40 == 0) goto L_0x0a69
            r0 = r1 | 1
            r1 = r0 | 128(0x80, float:1.794E-43)
        L_0x0a69:
            if (r14 == 0) goto L_0x0a82
            int r0 = r41.size()
            r2 = 0
        L_0x0a70:
            if (r2 >= r0) goto L_0x0a82
            java.lang.Object r3 = r14.get(r2)
            org.telegram.tgnet.TLRPC$ChatParticipants r3 = (org.telegram.tgnet.TLRPC.ChatParticipants) r3
            org.telegram.messenger.MessagesStorage r4 = r32.getMessagesStorage()
            r4.updateChatParticipants(r3)
            int r2 = r2 + 1
            goto L_0x0a70
        L_0x0a82:
            if (r42 == 0) goto L_0x0a94
            org.telegram.messenger.NotificationCenter r0 = r32.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.didUpdatedMessagesViews
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r5 = 0
            r4[r5] = r42
            r0.postNotificationName(r2, r4)
            goto L_0x0a96
        L_0x0a94:
            r3 = 1
            r5 = 0
        L_0x0a96:
            if (r1 == 0) goto L_0x0aa9
            org.telegram.messenger.NotificationCenter r0 = r32.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.updateInterfaces
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r3[r5] = r1
            r0.postNotificationName(r2, r3)
        L_0x0aa9:
            r1 = r43
            if (r1 == 0) goto L_0x0ab4
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            r0.putThumbsToCache(r1)
        L_0x0ab4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processUpdateArray$268$MessagesController(int, java.util.ArrayList, android.util.LongSparseArray, android.util.LongSparseArray, android.util.LongSparseArray, android.util.LongSparseArray, boolean, java.util.ArrayList, java.util.ArrayList, android.util.SparseArray, java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$null$264$MessagesController(TLRPC.User user) {
        getContactsController().addContactToPhoneBook(user, true);
    }

    public /* synthetic */ void lambda$null$265$MessagesController(TLRPC.TL_updateChannel tL_updateChannel) {
        getChannelDifference(tL_updateChannel.channel_id, 1, 0, (TLRPC.InputChannel) null);
    }

    public /* synthetic */ void lambda$null$266$MessagesController(TLRPC.Chat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    public /* synthetic */ void lambda$null$267$MessagesController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$processUpdateArray$270$MessagesController(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2, SparseIntArray sparseIntArray2) {
        AndroidUtilities.runOnUIThread(new Runnable(sparseLongArray, sparseLongArray2, sparseIntArray, arrayList, sparseArray, sparseArray2, sparseIntArray2) {
            private final /* synthetic */ SparseLongArray f$1;
            private final /* synthetic */ SparseLongArray f$2;
            private final /* synthetic */ SparseIntArray f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ SparseArray f$5;
            private final /* synthetic */ SparseArray f$6;
            private final /* synthetic */ SparseIntArray f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MessagesController.this.lambda$null$269$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$269$MessagesController(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2, SparseIntArray sparseIntArray2) {
        MessageObject messageObject;
        int i;
        MessageObject messageObject2;
        int i2;
        MessageObject messageObject3;
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        SparseIntArray sparseIntArray3 = sparseIntArray;
        SparseArray sparseArray3 = sparseArray;
        SparseArray sparseArray4 = sparseArray2;
        SparseIntArray sparseIntArray4 = sparseIntArray2;
        int i3 = 0;
        if (!(sparseLongArray3 == null && sparseLongArray4 == null)) {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesRead, sparseLongArray3, sparseLongArray4);
            if (sparseLongArray3 != null) {
                getNotificationsController().processReadMessages(sparseLongArray, 0, 0, 0, false);
                SharedPreferences.Editor edit = this.notificationsPreferences.edit();
                int size = sparseLongArray.size();
                int i4 = 0;
                for (int i5 = 0; i5 < size; i5++) {
                    int keyAt = sparseLongArray3.keyAt(i5);
                    int valueAt = (int) sparseLongArray3.valueAt(i5);
                    TLRPC.Dialog dialog = this.dialogs_dict.get((long) keyAt);
                    if (dialog != null && (i2 = dialog.top_message) > 0 && i2 <= valueAt && (messageObject3 = this.dialogMessage.get(dialog.id)) != null && !messageObject3.isOut()) {
                        messageObject3.setIsRead();
                        i4 |= 256;
                    }
                    if (keyAt != getUserConfig().getClientUserId()) {
                        edit.remove("diditem" + keyAt);
                        edit.remove("diditemo" + keyAt);
                    }
                }
                edit.commit();
                i3 = i4;
            } else {
                i3 = 0;
            }
            if (sparseLongArray4 != null) {
                int size2 = sparseLongArray2.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    int keyAt2 = sparseLongArray4.keyAt(i6);
                    int valueAt2 = (int) sparseLongArray4.valueAt(i6);
                    TLRPC.Dialog dialog2 = this.dialogs_dict.get((long) keyAt2);
                    if (dialog2 != null && (i = dialog2.top_message) > 0 && i <= valueAt2 && (messageObject2 = this.dialogMessage.get(dialog2.id)) != null && messageObject2.isOut()) {
                        messageObject2.setIsRead();
                        i3 |= 256;
                    }
                }
            }
        }
        if (sparseIntArray3 != null) {
            int size3 = sparseIntArray.size();
            for (int i7 = 0; i7 < size3; i7++) {
                int keyAt3 = sparseIntArray3.keyAt(i7);
                int valueAt3 = sparseIntArray3.valueAt(i7);
                getNotificationCenter().postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(keyAt3), Integer.valueOf(valueAt3));
                long j = ((long) keyAt3) << 32;
                if (!(this.dialogs_dict.get(j) == null || (messageObject = this.dialogMessage.get(j)) == null || messageObject.messageOwner.date > valueAt3)) {
                    messageObject.setIsRead();
                    i3 |= 256;
                }
            }
        }
        if (arrayList != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, arrayList);
        }
        if (sparseArray3 != null) {
            int size4 = sparseArray.size();
            for (int i8 = 0; i8 < size4; i8++) {
                int keyAt4 = sparseArray3.keyAt(i8);
                ArrayList arrayList2 = (ArrayList) sparseArray3.valueAt(i8);
                if (arrayList2 != null) {
                    getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList2, Integer.valueOf(keyAt4), false);
                    if (keyAt4 == 0) {
                        int size5 = arrayList2.size();
                        for (int i9 = 0; i9 < size5; i9++) {
                            MessageObject messageObject4 = this.dialogMessagesByIds.get(((Integer) arrayList2.get(i9)).intValue());
                            if (messageObject4 != null) {
                                messageObject4.deleted = true;
                            }
                        }
                    } else {
                        MessageObject messageObject5 = this.dialogMessage.get((long) (-keyAt4));
                        if (messageObject5 != null) {
                            int size6 = arrayList2.size();
                            int i10 = 0;
                            while (true) {
                                if (i10 >= size6) {
                                    break;
                                } else if (messageObject5.getId() == ((Integer) arrayList2.get(i10)).intValue()) {
                                    messageObject5.deleted = true;
                                    break;
                                } else {
                                    i10++;
                                }
                            }
                        }
                    }
                }
            }
            getNotificationsController().removeDeletedMessagesFromNotifications(sparseArray3);
        }
        if (sparseArray4 != null) {
            int size7 = sparseArray2.size();
            for (int i11 = 0; i11 < size7; i11++) {
                int keyAt5 = sparseArray4.keyAt(i11);
                ArrayList arrayList3 = (ArrayList) sparseArray4.valueAt(i11);
                if (arrayList3 != null) {
                    getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList3, Integer.valueOf(keyAt5), true);
                }
            }
        }
        if (sparseIntArray4 != null) {
            int size8 = sparseIntArray2.size();
            int i12 = 0;
            while (true) {
                if (i12 >= size8) {
                    break;
                }
                int keyAt6 = sparseIntArray4.keyAt(i12);
                int valueAt4 = sparseIntArray4.valueAt(i12);
                long j2 = (long) (-keyAt6);
                getNotificationCenter().postNotificationName(NotificationCenter.historyCleared, Long.valueOf(j2), Integer.valueOf(valueAt4));
                MessageObject messageObject6 = this.dialogMessage.get(j2);
                if (messageObject6 != null && messageObject6.getId() <= valueAt4) {
                    messageObject6.deleted = true;
                    break;
                }
                i12++;
            }
            getNotificationsController().removeDeletedHisoryFromNotifications(sparseIntArray4);
        }
        if (i3 != 0) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(i3));
        }
    }

    public /* synthetic */ void lambda$processUpdateArray$271$MessagesController(ArrayList arrayList, int i) {
        getMessagesStorage().updateDialogsWithDeletedMessages(arrayList, getMessagesStorage().markMessagesAsDeleted(arrayList, false, i, true, false), false, i);
    }

    public /* synthetic */ void lambda$processUpdateArray$272$MessagesController(int i, int i2) {
        getMessagesStorage().updateDialogsWithDeletedMessages(new ArrayList(), getMessagesStorage().markMessagesAsDeleted(i, i2, false, true), false, i);
    }

    public boolean isDialogMuted(long j) {
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        int i = sharedPreferences.getInt("notify2_" + j, -1);
        if (i == -1) {
            return !getNotificationsController().isGlobalNotificationsEnabled(j);
        }
        if (i == 2) {
            return true;
        }
        if (i == 3) {
            SharedPreferences sharedPreferences2 = this.notificationsPreferences;
            if (sharedPreferences2.getInt("notifyuntil_" + j, 0) >= getConnectionsManager().getCurrentTime()) {
                return true;
            }
        }
        return false;
    }

    private boolean updatePrintingUsersWithNewMessages(long j, ArrayList<MessageObject> arrayList) {
        boolean z;
        if (j > 0) {
            if (this.printingUsers.get(Long.valueOf(j)) != null) {
                this.printingUsers.remove(Long.valueOf(j));
                return true;
            }
        } else if (j < 0) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<MessageObject> it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject next = it.next();
                if (!arrayList2.contains(Integer.valueOf(next.messageOwner.from_id))) {
                    arrayList2.add(Integer.valueOf(next.messageOwner.from_id));
                }
            }
            ArrayList arrayList3 = this.printingUsers.get(Long.valueOf(j));
            if (arrayList3 != null) {
                int i = 0;
                z = false;
                while (i < arrayList3.size()) {
                    if (arrayList2.contains(Integer.valueOf(((PrintingUser) arrayList3.get(i)).userId))) {
                        arrayList3.remove(i);
                        i--;
                        if (arrayList3.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(j));
                        }
                        z = true;
                    }
                    i++;
                }
            } else {
                z = false;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:143:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateInterfaceWithMessages(long r27, java.util.ArrayList<org.telegram.messenger.MessageObject> r29, boolean r30) {
        /*
            r26 = this;
            r0 = r26
            r7 = r27
            r9 = r29
            if (r9 == 0) goto L_0x031c
            boolean r1 = r29.isEmpty()
            if (r1 == 0) goto L_0x0010
            goto L_0x031c
        L_0x0010:
            int r1 = (int) r7
            r11 = 0
            if (r1 != 0) goto L_0x0016
            r1 = 1
            goto L_0x0017
        L_0x0016:
            r1 = 0
        L_0x0017:
            if (r30 != 0) goto L_0x00d5
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
        L_0x001e:
            int r13 = r29.size()
            if (r2 >= r13) goto L_0x00d0
            java.lang.Object r13 = r9.get(r2)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            if (r3 == 0) goto L_0x005a
            if (r1 != 0) goto L_0x0038
            int r14 = r13.getId()
            int r15 = r3.getId()
            if (r14 > r15) goto L_0x005a
        L_0x0038:
            if (r1 != 0) goto L_0x0046
            int r14 = r13.getId()
            if (r14 >= 0) goto L_0x0050
            int r14 = r3.getId()
            if (r14 >= 0) goto L_0x0050
        L_0x0046:
            int r14 = r13.getId()
            int r15 = r3.getId()
            if (r14 < r15) goto L_0x005a
        L_0x0050:
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            int r14 = r14.date
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            int r15 = r15.date
            if (r14 <= r15) goto L_0x0064
        L_0x005a:
            org.telegram.tgnet.TLRPC$Message r3 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.to_id
            int r3 = r3.channel_id
            if (r3 == 0) goto L_0x0063
            r5 = r3
        L_0x0063:
            r3 = r13
        L_0x0064:
            if (r4 != 0) goto L_0x006d
            boolean r14 = r13.isOut()
            if (r14 != 0) goto L_0x006d
            r4 = 1
        L_0x006d:
            boolean r14 = r13.isOut()
            if (r14 == 0) goto L_0x00bf
            boolean r14 = r13.isSending()
            if (r14 != 0) goto L_0x00bf
            boolean r14 = r13.isForwarded()
            if (r14 != 0) goto L_0x00bf
            boolean r14 = r13.isNewGif()
            if (r14 == 0) goto L_0x0095
            org.telegram.messenger.MediaDataController r14 = r26.getMediaDataController()
            org.telegram.tgnet.TLRPC$Message r15 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r15.media
            org.telegram.tgnet.TLRPC$Document r12 = r12.document
            int r15 = r15.date
            r14.addRecentGif(r12, r15)
            goto L_0x00bf
        L_0x0095:
            boolean r12 = r13.isAnimatedEmoji()
            if (r12 != 0) goto L_0x00bf
            boolean r12 = r13.isSticker()
            if (r12 != 0) goto L_0x00a7
            boolean r12 = r13.isAnimatedSticker()
            if (r12 == 0) goto L_0x00bf
        L_0x00a7:
            org.telegram.messenger.MediaDataController r14 = r26.getMediaDataController()
            r15 = 0
            org.telegram.tgnet.TLRPC$Message r12 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r12.media
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            int r12 = r12.date
            r19 = 0
            r16 = r13
            r17 = r10
            r18 = r12
            r14.addRecentSticker(r15, r16, r17, r18, r19)
        L_0x00bf:
            boolean r10 = r13.isOut()
            if (r10 == 0) goto L_0x00cc
            boolean r10 = r13.isSent()
            if (r10 == 0) goto L_0x00cc
            r6 = 1
        L_0x00cc:
            int r2 = r2 + 1
            goto L_0x001e
        L_0x00d0:
            r10 = r3
            r12 = r4
            r13 = r5
            r14 = r6
            goto L_0x00d9
        L_0x00d5:
            r10 = 0
            r12 = 0
            r13 = 0
            r14 = 0
        L_0x00d9:
            org.telegram.messenger.MediaDataController r1 = r26.getMediaDataController()
            r6 = 0
            r2 = r29
            r3 = r27
            r5 = r30
            r1.loadReplyMessagesForMessages(r2, r3, r5, r6)
            org.telegram.messenger.NotificationCenter r1 = r26.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.Long r4 = java.lang.Long.valueOf(r27)
            r3[r11] = r4
            r4 = 1
            r3[r4] = r9
            r4 = 2
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r30)
            r3[r4] = r5
            r1.postNotificationName(r2, r3)
            if (r10 == 0) goto L_0x031c
            if (r30 == 0) goto L_0x0109
            goto L_0x031c
        L_0x0109:
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r1 = r0.dialogs_dict
            java.lang.Object r1 = r1.get(r7)
            org.telegram.tgnet.TLRPC$TL_dialog r1 = (org.telegram.tgnet.TLRPC.TL_dialog) r1
            org.telegram.tgnet.TLRPC$Message r2 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            r3 = 0
            if (r2 == 0) goto L_0x01bd
            if (r1 == 0) goto L_0x01bc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.allDialogs
            r2.remove(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogsServerOnly
            r2.remove(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogsCanAddUsers
            r2.remove(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogsChannelsOnly
            r2.remove(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogsGroupsOnly
            r2.remove(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogsUsersOnly
            r2.remove(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogsForward
            r2.remove(r1)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.dialogs_dict
            long r5 = r1.id
            r2.remove(r5)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r0.dialogs_read_inbox_max
            long r5 = r1.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r2.remove(r5)
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r0.dialogs_read_outbox_max
            long r5 = r1.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r2.remove(r5)
            android.util.SparseIntArray r2 = r0.nextDialogsCacheOffset
            int r5 = r1.folder_id
            int r2 = r2.get(r5, r11)
            if (r2 <= 0) goto L_0x0170
            android.util.SparseIntArray r5 = r0.nextDialogsCacheOffset
            int r6 = r1.folder_id
            r7 = 1
            int r2 = r2 - r7
            r5.put(r6, r2)
        L_0x0170:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessage
            long r5 = r1.id
            r2.remove(r5)
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog>> r2 = r0.dialogsByFolder
            int r5 = r1.folder_id
            java.lang.Object r2 = r2.get(r5)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 == 0) goto L_0x0186
            r2.remove(r1)
        L_0x0186:
            android.util.SparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessagesByIds
            int r5 = r1.top_message
            java.lang.Object r2 = r2.get(r5)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            android.util.SparseArray<org.telegram.messenger.MessageObject> r5 = r0.dialogMessagesByIds
            int r6 = r1.top_message
            r5.remove(r6)
            if (r2 == 0) goto L_0x01a6
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            long r5 = r2.random_id
            int r2 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x01a6
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessagesByRandomIds
            r2.remove(r5)
        L_0x01a6:
            r1.top_message = r11
            org.telegram.messenger.NotificationsController r2 = r26.getNotificationsController()
            long r3 = r1.id
            r2.removeNotificationsForDialog(r3)
            org.telegram.messenger.NotificationCenter r1 = r26.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.needReloadRecentDialogsSearch
            java.lang.Object[] r3 = new java.lang.Object[r11]
            r1.postNotificationName(r2, r3)
        L_0x01bc:
            return
        L_0x01bd:
            if (r1 != 0) goto L_0x025c
            java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r1 = r0.getChat(r1)
            if (r13 == 0) goto L_0x01cb
            if (r1 == 0) goto L_0x01d3
        L_0x01cb:
            if (r1 == 0) goto L_0x01d4
            boolean r2 = org.telegram.messenger.ChatObject.isNotInChat(r1)
            if (r2 == 0) goto L_0x01d4
        L_0x01d3:
            return
        L_0x01d4:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0208
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "not found dialog with id "
            r2.append(r5)
            r2.append(r7)
            java.lang.String r5 = " dictCount = "
            r2.append(r5)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r0.dialogs_dict
            int r5 = r5.size()
            r2.append(r5)
            java.lang.String r5 = " allCount = "
            r2.append(r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r0.allDialogs
            int r5 = r5.size()
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0208:
            org.telegram.tgnet.TLRPC$TL_dialog r2 = new org.telegram.tgnet.TLRPC$TL_dialog
            r2.<init>()
            r2.id = r7
            int r5 = r10.getId()
            r2.top_message = r5
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            int r5 = r5.date
            r2.last_message_date = r5
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            r2.flags = r1
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r1 = r0.dialogs_dict
            r1.put(r7, r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r0.allDialogs
            r1.add(r2)
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r0.dialogMessage
            r1.put(r7, r10)
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 != 0) goto L_0x024e
            android.util.SparseArray<org.telegram.messenger.MessageObject> r1 = r0.dialogMessagesByIds
            int r5 = r10.getId()
            r1.put(r5, r10)
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            long r5 = r1.random_id
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x024e
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r0.dialogMessagesByRandomIds
            r1.put(r5, r10)
        L_0x024e:
            org.telegram.messenger.MessagesStorage r1 = r26.getMessagesStorage()
            org.telegram.messenger.-$$Lambda$MessagesController$Ji4MTx4hnSHDwbZ4kj_JP_rllDU r3 = new org.telegram.messenger.-$$Lambda$MessagesController$Ji4MTx4hnSHDwbZ4kj_JP_rllDU
            r3.<init>(r2, r7)
            r1.getDialogFolderId(r7, r3)
            goto L_0x030c
        L_0x025c:
            if (r12 == 0) goto L_0x0284
            int r2 = r1.folder_id
            r5 = 1
            if (r2 != r5) goto L_0x0285
            long r12 = r1.id
            boolean r2 = r0.isDialogMuted(r12)
            if (r2 != 0) goto L_0x0285
            r1.folder_id = r11
            r1.pinned = r11
            r1.pinnedNum = r11
            org.telegram.messenger.MessagesStorage r20 = r26.getMessagesStorage()
            r21 = 0
            r22 = 0
            long r11 = r1.id
            r25 = 0
            r23 = r11
            r20.setDialogsFolderId(r21, r22, r23, r25)
            r11 = 1
            goto L_0x0285
        L_0x0284:
            r5 = 1
        L_0x0285:
            int r2 = r1.top_message
            if (r2 <= 0) goto L_0x0297
            int r2 = r10.getId()
            if (r2 <= 0) goto L_0x0297
            int r2 = r10.getId()
            int r6 = r1.top_message
            if (r2 > r6) goto L_0x02bd
        L_0x0297:
            int r2 = r1.top_message
            if (r2 >= 0) goto L_0x02a9
            int r2 = r10.getId()
            if (r2 >= 0) goto L_0x02a9
            int r2 = r10.getId()
            int r6 = r1.top_message
            if (r2 < r6) goto L_0x02bd
        L_0x02a9:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessage
            int r2 = r2.indexOfKey(r7)
            if (r2 < 0) goto L_0x02bd
            int r2 = r1.top_message
            if (r2 < 0) goto L_0x02bd
            int r2 = r1.last_message_date
            org.telegram.tgnet.TLRPC$Message r6 = r10.messageOwner
            int r6 = r6.date
            if (r2 > r6) goto L_0x030d
        L_0x02bd:
            android.util.SparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessagesByIds
            int r6 = r1.top_message
            java.lang.Object r2 = r2.get(r6)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            android.util.SparseArray<org.telegram.messenger.MessageObject> r6 = r0.dialogMessagesByIds
            int r9 = r1.top_message
            r6.remove(r9)
            if (r2 == 0) goto L_0x02dd
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            long r11 = r2.random_id
            int r2 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x02dd
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r0.dialogMessagesByRandomIds
            r2.remove(r11)
        L_0x02dd:
            int r2 = r10.getId()
            r1.top_message = r2
            org.telegram.tgnet.TLRPC$Message r2 = r10.messageOwner
            int r2 = r2.date
            r1.last_message_date = r2
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r0.dialogMessage
            r1.put(r7, r10)
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 != 0) goto L_0x030c
            android.util.SparseArray<org.telegram.messenger.MessageObject> r1 = r0.dialogMessagesByIds
            int r2 = r10.getId()
            r1.put(r2, r10)
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            long r1 = r1.random_id
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x030c
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.dialogMessagesByRandomIds
            r3.put(r1, r10)
        L_0x030c:
            r11 = 1
        L_0x030d:
            if (r11 == 0) goto L_0x0313
            r1 = 0
            r0.sortDialogs(r1)
        L_0x0313:
            if (r14 == 0) goto L_0x031c
            org.telegram.messenger.MediaDataController r1 = r26.getMediaDataController()
            r1.increasePeerRaiting(r7)
        L_0x031c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updateInterfaceWithMessages(long, java.util.ArrayList, boolean):void");
    }

    public /* synthetic */ void lambda$updateInterfaceWithMessages$273$MessagesController(TLRPC.Dialog dialog, long j, int i) {
        if (i == -1) {
            int i2 = (int) j;
            if (i2 != 0) {
                loadUnknownDialog(getInputPeer(i2), 0);
            }
        } else if (i != 0) {
            dialog.folder_id = i;
            sortDialogs((SparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
        }
    }

    public void addDialogAction(long j, boolean z) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog != null) {
            if (z) {
                this.clearingHistoryDialogs.put(j, dialog);
            } else {
                this.deletingDialogs.put(j, dialog);
                this.allDialogs.remove(dialog);
                sortDialogs((SparseArray<TLRPC.Chat>) null);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
        }
    }

    public void removeDialogAction(long j, boolean z, boolean z2) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog != null) {
            if (z) {
                this.clearingHistoryDialogs.remove(j);
            } else {
                this.deletingDialogs.remove(j);
                if (!z2) {
                    this.allDialogs.add(dialog);
                    sortDialogs((SparseArray<TLRPC.Chat>) null);
                }
            }
            if (!z2) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
            }
        }
    }

    public boolean isClearingDialog(long j) {
        return this.clearingHistoryDialogs.get(j) != null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x011d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sortDialogs(android.util.SparseArray<org.telegram.tgnet.TLRPC.Chat> r14) {
        /*
            r13 = this;
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r13.dialogsServerOnly
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r13.dialogsCanAddUsers
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r13.dialogsChannelsOnly
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r13.dialogsGroupsOnly
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r13.dialogsUsersOnly
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r13.dialogsForward
            r0.clear()
            r0 = 0
            r1 = 0
        L_0x0020:
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog>> r2 = r13.dialogsByFolder
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0038
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog>> r2 = r13.dialogsByFolder
            java.lang.Object r2 = r2.valueAt(r1)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 == 0) goto L_0x0035
            r2.clear()
        L_0x0035:
            int r1 = r1 + 1
            goto L_0x0020
        L_0x0038:
            r13.unreadUnmutedDialogs = r0
            org.telegram.messenger.UserConfig r1 = r13.getUserConfig()
            int r1 = r1.getClientUserId()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r13.allDialogs
            java.util.Comparator<org.telegram.tgnet.TLRPC$Dialog> r3 = r13.dialogComparator
            java.util.Collections.sort(r2, r3)
            r2 = 1
            r13.isLeftProxyChannel = r2
            org.telegram.tgnet.TLRPC$Dialog r3 = r13.proxyDialog
            if (r3 == 0) goto L_0x006a
            long r3 = r3.id
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x006a
            int r4 = (int) r3
            int r3 = -r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r13.getChat(r3)
            if (r3 == 0) goto L_0x006a
            boolean r3 = r3.left
            if (r3 != 0) goto L_0x006a
            r13.isLeftProxyChannel = r0
        L_0x006a:
            org.telegram.messenger.NotificationsController r3 = r13.getNotificationsController()
            boolean r3 = r3.showBadgeMessages
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r4 = r13.allDialogs
            int r4 = r4.size()
            r5 = r4
            r4 = 0
            r6 = 0
        L_0x0079:
            if (r4 >= r5) goto L_0x0158
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r7 = r13.allDialogs
            java.lang.Object r7 = r7.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r7 = (org.telegram.tgnet.TLRPC.Dialog) r7
            long r8 = r7.id
            r10 = 32
            long r10 = r8 >> r10
            int r11 = (int) r10
            int r9 = (int) r8
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_dialog
            if (r8 == 0) goto L_0x0122
            if (r9 == 0) goto L_0x010d
            if (r11 == r2) goto L_0x010d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.dialogsServerOnly
            r8.add(r7)
            boolean r8 = org.telegram.messenger.DialogObject.isChannel(r7)
            if (r8 == 0) goto L_0x00e2
            int r8 = -r9
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r8 = r13.getChat(r8)
            if (r8 == 0) goto L_0x00c2
            boolean r10 = r8.megagroup
            if (r10 == 0) goto L_0x00c2
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r10 = r8.admin_rights
            if (r10 == 0) goto L_0x00b9
            boolean r11 = r10.post_messages
            if (r11 != 0) goto L_0x00bd
            boolean r10 = r10.add_admins
            if (r10 != 0) goto L_0x00bd
        L_0x00b9:
            boolean r10 = r8.creator
            if (r10 == 0) goto L_0x00c2
        L_0x00bd:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r10 = r13.dialogsCanAddUsers
            r10.add(r7)
        L_0x00c2:
            if (r8 == 0) goto L_0x00ce
            boolean r10 = r8.megagroup
            if (r10 == 0) goto L_0x00ce
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.dialogsGroupsOnly
            r8.add(r7)
            goto L_0x010d
        L_0x00ce:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r10 = r13.dialogsChannelsOnly
            r10.add(r7)
            boolean r10 = org.telegram.messenger.ChatObject.hasAdminRights(r8)
            if (r10 == 0) goto L_0x00e0
            boolean r8 = org.telegram.messenger.ChatObject.canPost(r8)
            if (r8 == 0) goto L_0x00e0
            goto L_0x010d
        L_0x00e0:
            r8 = 0
            goto L_0x010e
        L_0x00e2:
            if (r9 >= 0) goto L_0x0104
            if (r14 == 0) goto L_0x00f9
            int r8 = -r9
            java.lang.Object r8 = r14.get(r8)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
            if (r8 == 0) goto L_0x00f9
            org.telegram.tgnet.TLRPC$InputChannel r8 = r8.migrated_to
            if (r8 == 0) goto L_0x00f9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r7 = r13.allDialogs
            r7.remove(r4)
            goto L_0x014c
        L_0x00f9:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.dialogsCanAddUsers
            r8.add(r7)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.dialogsGroupsOnly
            r8.add(r7)
            goto L_0x010d
        L_0x0104:
            if (r9 <= 0) goto L_0x010d
            if (r9 == r1) goto L_0x010d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.dialogsUsersOnly
            r8.add(r7)
        L_0x010d:
            r8 = 1
        L_0x010e:
            if (r8 == 0) goto L_0x0122
            int r8 = r7.folder_id
            if (r8 != 0) goto L_0x0122
            if (r9 != r1) goto L_0x011d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r6 = r13.dialogsForward
            r6.add(r0, r7)
            r6 = 1
            goto L_0x0122
        L_0x011d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.dialogsForward
            r8.add(r7)
        L_0x0122:
            int r8 = r7.unread_count
            if (r8 != 0) goto L_0x012a
            boolean r8 = r7.unread_mark
            if (r8 == 0) goto L_0x0137
        L_0x012a:
            long r8 = r7.id
            boolean r8 = r13.isDialogMuted(r8)
            if (r8 != 0) goto L_0x0137
            int r8 = r13.unreadUnmutedDialogs
            int r8 = r8 + r2
            r13.unreadUnmutedDialogs = r8
        L_0x0137:
            org.telegram.tgnet.TLRPC$Dialog r8 = r13.proxyDialog
            if (r8 == 0) goto L_0x0151
            long r9 = r7.id
            long r11 = r8.id
            int r8 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r8 != 0) goto L_0x0151
            boolean r8 = r13.isLeftProxyChannel
            if (r8 == 0) goto L_0x0151
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r7 = r13.allDialogs
            r7.remove(r4)
        L_0x014c:
            int r4 = r4 + -1
            int r5 = r5 + -1
            goto L_0x0155
        L_0x0151:
            r8 = -1
            r13.addDialogToItsFolder(r8, r7, r3)
        L_0x0155:
            int r4 = r4 + r2
            goto L_0x0079
        L_0x0158:
            org.telegram.tgnet.TLRPC$Dialog r14 = r13.proxyDialog
            if (r14 == 0) goto L_0x016b
            boolean r1 = r13.isLeftProxyChannel
            if (r1 == 0) goto L_0x016b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r13.allDialogs
            r1.add(r0, r14)
            r14 = -2
            org.telegram.tgnet.TLRPC$Dialog r1 = r13.proxyDialog
            r13.addDialogToItsFolder(r14, r1, r3)
        L_0x016b:
            if (r6 != 0) goto L_0x019a
            org.telegram.messenger.UserConfig r14 = r13.getUserConfig()
            org.telegram.tgnet.TLRPC$User r14 = r14.getCurrentUser()
            if (r14 == 0) goto L_0x019a
            org.telegram.tgnet.TLRPC$TL_dialog r1 = new org.telegram.tgnet.TLRPC$TL_dialog
            r1.<init>()
            int r2 = r14.id
            long r2 = (long) r2
            r1.id = r2
            org.telegram.tgnet.TLRPC$TL_peerNotifySettings r2 = new org.telegram.tgnet.TLRPC$TL_peerNotifySettings
            r2.<init>()
            r1.notify_settings = r2
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r1.peer = r2
            org.telegram.tgnet.TLRPC$Peer r2 = r1.peer
            int r14 = r14.id
            r2.user_id = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r14 = r13.dialogsForward
            r14.add(r0, r1)
        L_0x019a:
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog>> r14 = r13.dialogsByFolder
            int r14 = r14.size()
            if (r0 >= r14) goto L_0x01be
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog>> r14 = r13.dialogsByFolder
            int r14 = r14.keyAt(r0)
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog>> r1 = r13.dialogsByFolder
            java.lang.Object r1 = r1.valueAt(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x01bb
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog>> r1 = r13.dialogsByFolder
            r1.remove(r14)
        L_0x01bb:
            int r0 = r0 + 1
            goto L_0x019a
        L_0x01be:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.sortDialogs(android.util.SparseArray):void");
    }

    private void addDialogToItsFolder(int i, TLRPC.Dialog dialog, boolean z) {
        int i2;
        TLRPC.Dialog dialog2;
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            dialog.unread_count = 0;
            dialog.unread_mentions_count = 0;
            i2 = 0;
        } else {
            i2 = dialog.folder_id;
        }
        ArrayList arrayList = this.dialogsByFolder.get(i2);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.dialogsByFolder.put(i2, arrayList);
        }
        if (!(i2 == 0 || dialog.unread_count == 0 || (dialog2 = this.dialogs_dict.get(DialogObject.makeFolderDialogId(i2))) == null)) {
            if (z) {
                if (isDialogMuted(dialog.id)) {
                    dialog2.unread_count += dialog.unread_count;
                } else {
                    dialog2.unread_mentions_count += dialog.unread_count;
                }
            } else if (isDialogMuted(dialog.id)) {
                dialog2.unread_count++;
            } else {
                dialog2.unread_mentions_count++;
            }
        }
        if (i == -1) {
            arrayList.add(dialog);
        } else if (i != -2) {
            arrayList.add(i, dialog);
        } else if (arrayList.isEmpty() || !(arrayList.get(0) instanceof TLRPC.TL_dialogFolder)) {
            arrayList.add(0, dialog);
        } else {
            arrayList.add(1, dialog);
        }
    }

    public static String getRestrictionReason(ArrayList<TLRPC.TL_restrictionReason> arrayList) {
        if (arrayList.isEmpty()) {
            return null;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.TL_restrictionReason tL_restrictionReason = arrayList.get(i);
            if ("all".equals(tL_restrictionReason.platform) || "android".equals(tL_restrictionReason.platform)) {
                return tL_restrictionReason.text;
            }
        }
        return null;
    }

    private static void showCantOpenAlert(BaseFragment baseFragment, String str) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setMessage(str);
            baseFragment.showDialog(builder.create());
        }
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment) {
        return checkCanOpenChat(bundle, baseFragment, (MessageObject) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMessages} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.tgnet.TLRPC$TL_channels_getMessages} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMessages} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMessages} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkCanOpenChat(android.os.Bundle r9, org.telegram.ui.ActionBar.BaseFragment r10, org.telegram.messenger.MessageObject r11) {
        /*
            r8 = this;
            r0 = 1
            if (r9 == 0) goto L_0x00d6
            if (r10 != 0) goto L_0x0007
            goto L_0x00d6
        L_0x0007:
            r1 = 0
            java.lang.String r2 = "user_id"
            int r2 = r9.getInt(r2, r1)
            java.lang.String r3 = "chat_id"
            int r3 = r9.getInt(r3, r1)
            java.lang.String r4 = "message_id"
            int r4 = r9.getInt(r4, r1)
            r5 = 0
            if (r2 == 0) goto L_0x0028
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r8.getUser(r2)
            r3 = r5
            goto L_0x0037
        L_0x0028:
            if (r3 == 0) goto L_0x0035
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r8.getChat(r2)
            r3 = r2
            r2 = r5
            goto L_0x0037
        L_0x0035:
            r2 = r5
            r3 = r2
        L_0x0037:
            if (r2 != 0) goto L_0x003c
            if (r3 != 0) goto L_0x003c
            return r0
        L_0x003c:
            if (r3 == 0) goto L_0x0045
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r3.restriction_reason
            java.lang.String r5 = getRestrictionReason(r2)
            goto L_0x004d
        L_0x0045:
            if (r2 == 0) goto L_0x004d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r2.restriction_reason
            java.lang.String r5 = getRestrictionReason(r2)
        L_0x004d:
            if (r5 == 0) goto L_0x0053
            showCantOpenAlert(r10, r5)
            return r1
        L_0x0053:
            if (r4 == 0) goto L_0x00d6
            if (r11 == 0) goto L_0x00d6
            if (r3 == 0) goto L_0x00d6
            long r4 = r3.access_hash
            r6 = 0
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x00d6
            long r4 = r11.getDialogId()
            int r2 = (int) r4
            if (r2 == 0) goto L_0x00d6
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r4 = r10.getParentActivity()
            r5 = 3
            r0.<init>(r4, r5)
            if (r2 >= 0) goto L_0x007d
            int r3 = -r2
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r8.getChat(r3)
        L_0x007d:
            if (r2 > 0) goto L_0x00a8
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 != 0) goto L_0x0086
            goto L_0x00a8
        L_0x0086:
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r8.getChat(r2)
            org.telegram.tgnet.TLRPC$TL_channels_getMessages r3 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r2 = getInputChannel((org.telegram.tgnet.TLRPC.Chat) r2)
            r3.channel = r2
            java.util.ArrayList<java.lang.Integer> r2 = r3.id
            int r11 = r11.getId()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r2.add(r11)
            goto L_0x00ba
        L_0x00a8:
            org.telegram.tgnet.TLRPC$TL_messages_getMessages r3 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages
            r3.<init>()
            java.util.ArrayList<java.lang.Integer> r2 = r3.id
            int r11 = r11.getId()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r2.add(r11)
        L_0x00ba:
            org.telegram.tgnet.ConnectionsManager r11 = r8.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$Fxut3hB2IXEcZhR2QyK02YJccCA r2 = new org.telegram.messenger.-$$Lambda$MessagesController$Fxut3hB2IXEcZhR2QyK02YJccCA
            r2.<init>(r0, r10, r9)
            int r9 = r11.sendRequest(r3, r2)
            org.telegram.messenger.-$$Lambda$MessagesController$Q3gE4D6e8Y_f7KoqBc8WY4mTi3Y r11 = new org.telegram.messenger.-$$Lambda$MessagesController$Q3gE4D6e8Y_f7KoqBc8WY4mTi3Y
            r11.<init>(r9, r10)
            r0.setOnCancelListener(r11)
            r10.setVisibleDialog(r0)
            r0.show()
            return r1
        L_0x00d6:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.checkCanOpenChat(android.os.Bundle, org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.MessageObject):boolean");
    }

    public /* synthetic */ void lambda$checkCanOpenChat$275$MessagesController(AlertDialog alertDialog, BaseFragment baseFragment, Bundle bundle, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, baseFragment, bundle) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ BaseFragment f$3;
                private final /* synthetic */ Bundle f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    MessagesController.this.lambda$null$274$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$274$MessagesController(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        putUsers(messages_messages.users, false);
        putChats(messages_messages.chats, false);
        getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
        baseFragment.presentFragment(new ChatActivity(bundle), true);
    }

    public /* synthetic */ void lambda$checkCanOpenChat$276$MessagesController(int i, BaseFragment baseFragment, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
        if (baseFragment != null) {
            baseFragment.setVisibleDialog((Dialog) null);
        }
    }

    public static void openChatOrProfileWith(TLRPC.User user, TLRPC.Chat chat, BaseFragment baseFragment, int i, boolean z) {
        if ((user != null || chat != null) && baseFragment != null) {
            String str = null;
            if (chat != null) {
                str = getRestrictionReason(chat.restriction_reason);
            } else if (user != null) {
                str = getRestrictionReason(user.restriction_reason);
                if (i != 3 && user.bot) {
                    i = 1;
                    z = true;
                }
            }
            if (str != null) {
                showCantOpenAlert(baseFragment, str);
                return;
            }
            Bundle bundle = new Bundle();
            if (chat != null) {
                bundle.putInt("chat_id", chat.id);
            } else {
                bundle.putInt("user_id", user.id);
            }
            if (i == 0) {
                baseFragment.presentFragment(new ProfileActivity(bundle));
            } else if (i == 2) {
                baseFragment.presentFragment(new ChatActivity(bundle), true, true);
            } else {
                baseFragment.presentFragment(new ChatActivity(bundle), z);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void openByUserName(java.lang.String r6, org.telegram.ui.ActionBar.BaseFragment r7, int r8) {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x0069
            if (r7 != 0) goto L_0x0006
            goto L_0x0069
        L_0x0006:
            org.telegram.tgnet.TLObject r0 = r5.getUserOrChat(r6)
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.User
            r2 = 0
            if (r1 == 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            boolean r1 = r0.min
            if (r1 == 0) goto L_0x0016
            goto L_0x0026
        L_0x0016:
            r1 = r2
            goto L_0x0028
        L_0x0018:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r1 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            boolean r1 = r0.min
            if (r1 == 0) goto L_0x0023
            goto L_0x0026
        L_0x0023:
            r1 = r0
            r0 = r2
            goto L_0x0028
        L_0x0026:
            r0 = r2
            r1 = r0
        L_0x0028:
            r3 = 0
            if (r0 == 0) goto L_0x002f
            openChatOrProfileWith(r0, r2, r7, r8, r3)
            goto L_0x0069
        L_0x002f:
            r0 = 1
            if (r1 == 0) goto L_0x0036
            openChatOrProfileWith(r2, r1, r7, r0, r3)
            goto L_0x0069
        L_0x0036:
            android.app.Activity r1 = r7.getParentActivity()
            if (r1 != 0) goto L_0x003d
            return
        L_0x003d:
            org.telegram.ui.ActionBar.AlertDialog[] r0 = new org.telegram.ui.ActionBar.AlertDialog[r0]
            org.telegram.ui.ActionBar.AlertDialog r1 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r2 = r7.getParentActivity()
            r4 = 3
            r1.<init>(r2, r4)
            r0[r3] = r1
            org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername r1 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername
            r1.<init>()
            r1.username = r6
            org.telegram.tgnet.ConnectionsManager r6 = r5.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MessagesController$E9I-rOP2XDEloKCu7sFp3jhnG60 r2 = new org.telegram.messenger.-$$Lambda$MessagesController$E9I-rOP2XDEloKCu7sFp3jhnG60
            r2.<init>(r0, r7, r8)
            int r6 = r6.sendRequest(r1, r2)
            org.telegram.messenger.-$$Lambda$MessagesController$g4XyDsaZinAOpJNBuk94VuSsZ40 r8 = new org.telegram.messenger.-$$Lambda$MessagesController$g4XyDsaZinAOpJNBuk94VuSsZ40
            r8.<init>(r0, r6, r7)
            r6 = 500(0x1f4, double:2.47E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r6)
        L_0x0069:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.openByUserName(java.lang.String, org.telegram.ui.ActionBar.BaseFragment, int):void");
    }

    public /* synthetic */ void lambda$openByUserName$278$MessagesController(AlertDialog[] alertDialogArr, BaseFragment baseFragment, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, baseFragment, tL_error, tLObject, i) {
            private final /* synthetic */ AlertDialog[] f$1;
            private final /* synthetic */ BaseFragment f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;
            private final /* synthetic */ TLObject f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MessagesController.this.lambda$null$277$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$277$MessagesController(AlertDialog[] alertDialogArr, BaseFragment baseFragment, TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Exception unused) {
        }
        alertDialogArr[0] = null;
        baseFragment.setVisibleDialog((Dialog) null);
        if (tL_error == null) {
            TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
            putUsers(tL_contacts_resolvedPeer.users, false);
            putChats(tL_contacts_resolvedPeer.chats, false);
            getMessagesStorage().putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
            if (!tL_contacts_resolvedPeer.chats.isEmpty()) {
                openChatOrProfileWith((TLRPC.User) null, tL_contacts_resolvedPeer.chats.get(0), baseFragment, 1, false);
            } else if (!tL_contacts_resolvedPeer.users.isEmpty()) {
                openChatOrProfileWith(tL_contacts_resolvedPeer.users.get(0), (TLRPC.Chat) null, baseFragment, i, false);
            }
        } else if (baseFragment != null && baseFragment.getParentActivity() != null) {
            try {
                Toast.makeText(baseFragment.getParentActivity(), LocaleController.getString("NoUsernameFound", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$openByUserName$280$MessagesController(AlertDialog[] alertDialogArr, int i, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    MessagesController.this.lambda$null$279$MessagesController(this.f$1, dialogInterface);
                }
            });
            baseFragment.showDialog(alertDialogArr[0]);
        }
    }

    public /* synthetic */ void lambda$null$279$MessagesController(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public void ensureMessagesLoaded(long j, boolean z, int i, Runnable runnable) {
        int i2;
        long j2 = j;
        SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
        if (i == 0) {
            i2 = notificationsSettings.getInt("diditem" + j2, 0);
        } else {
            i2 = i;
        }
        if (i2 == 0 || !getMessagesStorage().checkMessageId(j2, z, i2)) {
            int generateClassGuid = ConnectionsManager.generateClassGuid();
            final int i3 = generateClassGuid;
            final long j3 = j;
            final Runnable runnable2 = runnable;
            getNotificationCenter().addObserver(new NotificationCenter.NotificationCenterDelegate() {
                public void didReceivedNotification(int i, int i2, Object... objArr) {
                    if (i == NotificationCenter.messagesDidLoad && objArr[10].intValue() == i3) {
                        boolean booleanValue = objArr[3].booleanValue();
                        if (!objArr[2].isEmpty() || !booleanValue) {
                            MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
                            runnable2.run();
                            return;
                        }
                        MessagesController.this.loadMessages(j3, 20, 3, 0, false, 0, i3, 3, 0, false, false, 0);
                    }
                }
            }, NotificationCenter.messagesDidLoad);
            loadMessages(j, 1, i2, 0, true, 0, generateClassGuid, 3, 0, false, false, 0);
            return;
        }
        runnable.run();
    }
}
