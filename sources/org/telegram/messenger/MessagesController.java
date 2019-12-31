package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.DialogPeer;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.EncryptedMessage;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputDialogPeer;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.JSONValue;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.SendMessageAction;
import org.telegram.tgnet.TLRPC.TL_account_createTheme;
import org.telegram.tgnet.TLRPC.TL_account_getContactSignUpNotification;
import org.telegram.tgnet.TLRPC.TL_account_getNotifySettings;
import org.telegram.tgnet.TLRPC.TL_account_installTheme;
import org.telegram.tgnet.TLRPC.TL_account_installWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_registerDevice;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_account_saveTheme;
import org.telegram.tgnet.TLRPC.TL_account_saveWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC.TL_account_updateStatus;
import org.telegram.tgnet.TLRPC.TL_account_updateTheme;
import org.telegram.tgnet.TLRPC.TL_account_uploadTheme;
import org.telegram.tgnet.TLRPC.TL_account_uploadWallPaper;
import org.telegram.tgnet.TLRPC.TL_auth_logOut;
import org.telegram.tgnet.TLRPC.TL_boolFalse;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_botInfo;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_editPhoto;
import org.telegram.tgnet.TLRPC.TL_channels_editTitle;
import org.telegram.tgnet.TLRPC.TL_channels_getFullChannel;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_channels_leaveChannel;
import org.telegram.tgnet.TLRPC.TL_channels_readHistory;
import org.telegram.tgnet.TLRPC.TL_channels_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_channels_togglePreHistoryHidden;
import org.telegram.tgnet.TLRPC.TL_channels_toggleSignatures;
import org.telegram.tgnet.TLRPC.TL_channels_toggleSlowMode;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatOnlines;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_contactBlocked;
import org.telegram.tgnet.TLRPC.TL_contacts_block;
import org.telegram.tgnet.TLRPC.TL_contacts_getBlocked;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_contacts_unblock;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_dialogFolder;
import org.telegram.tgnet.TLRPC.TL_dialogPeer;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_folder;
import org.telegram.tgnet.TLRPC.TL_help_getAppChangelog;
import org.telegram.tgnet.TLRPC.TL_help_getAppConfig;
import org.telegram.tgnet.TLRPC.TL_help_getRecentMeUrls;
import org.telegram.tgnet.TLRPC.TL_help_getTermsOfServiceUpdate;
import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;
import org.telegram.tgnet.TLRPC.TL_help_recentMeUrls;
import org.telegram.tgnet.TLRPC.TL_help_termsOfServiceUpdate;
import org.telegram.tgnet.TLRPC.TL_help_termsOfServiceUpdateEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChannel;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputDialogPeer;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC.TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC.TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonGeoIrrelevant;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonSpam;
import org.telegram.tgnet.TLRPC.TL_inputTheme;
import org.telegram.tgnet.TLRPC.TL_inputThemeSettings;
import org.telegram.tgnet.TLRPC.TL_inputUser;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUserSelf;
import org.telegram.tgnet.TLRPC.TL_inputWallPaperNoFile;
import org.telegram.tgnet.TLRPC.TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC.TL_jsonBool;
import org.telegram.tgnet.TLRPC.TL_jsonNumber;
import org.telegram.tgnet.TLRPC.TL_jsonObject;
import org.telegram.tgnet.TLRPC.TL_jsonObjectValue;
import org.telegram.tgnet.TLRPC.TL_jsonString;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC.TL_messages_chatFull;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_deleteChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_deleteScheduledMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAbout;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights;
import org.telegram.tgnet.TLRPC.TL_messages_editChatPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_editChatTitle;
import org.telegram.tgnet.TLRPC.TL_messages_getDialogUnreadMarks;
import org.telegram.tgnet.TLRPC.TL_messages_getDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC.TL_messages_getHistory;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getMessagesViews;
import org.telegram.tgnet.TLRPC.TL_messages_getOnlines;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerSettings;
import org.telegram.tgnet.TLRPC.TL_messages_getPinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getPollResults;
import org.telegram.tgnet.TLRPC.TL_messages_getScheduledHistory;
import org.telegram.tgnet.TLRPC.TL_messages_getUnreadMentions;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_messages_hidePeerSettingsBar;
import org.telegram.tgnet.TLRPC.TL_messages_markDialogUnread;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_messagesNotModified;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readMentions;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_messages_reorderPinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_reportEncryptedSpam;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import org.telegram.tgnet.TLRPC.TL_messages_setTyping;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_messages_toggleDialogPin;
import org.telegram.tgnet.TLRPC.TL_messages_updatePinnedMessage;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerSettings;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_deletePhotos;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardHide;
import org.telegram.tgnet.TLRPC.TL_restrictionReason;
import org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageGamePlayAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordRoundAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordVideoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageTypingAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadDocumentAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadPhotoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadRoundAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadVideoAction;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_updateChannel;
import org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages;
import org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews;
import org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
import org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents;
import org.telegram.tgnet.TLRPC.TL_updateChannelTooLong;
import org.telegram.tgnet.TLRPC.TL_updateChannelWebPage;
import org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateFolderPeers;
import org.telegram.tgnet.TLRPC.TL_updateLangPack;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
import org.telegram.tgnet.TLRPC.TL_updateWebPage;
import org.telegram.tgnet.TLRPC.TL_updatesCombined;
import org.telegram.tgnet.TLRPC.TL_updates_difference;
import org.telegram.tgnet.TLRPC.TL_updates_differenceEmpty;
import org.telegram.tgnet.TLRPC.TL_updates_differenceSlice;
import org.telegram.tgnet.TLRPC.TL_updates_differenceTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_getDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getState;
import org.telegram.tgnet.TLRPC.TL_updates_state;
import org.telegram.tgnet.TLRPC.TL_userForeign_old2;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_users_getFullUser;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WallPaperSettings;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.contacts_Blocked;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;
import org.telegram.tgnet.TLRPC.updates_Difference;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;

public class MessagesController extends BaseController implements NotificationCenterDelegate {
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
    protected ArrayList<Dialog> allDialogs = new ArrayList();
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
    private SparseArray<SparseArray<String>> channelAdmins = new SparseArray();
    private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray();
    private SparseIntArray channelsPts = new SparseIntArray();
    private ConcurrentHashMap<Integer, Chat> chats = new ConcurrentHashMap(100, 1.0f, 2);
    private SparseBooleanArray checkingLastMessagesDialogs = new SparseBooleanArray();
    private boolean checkingProxyInfo;
    private int checkingProxyInfoRequestId;
    private boolean checkingTosUpdate;
    private LongSparseArray<Dialog> clearingHistoryDialogs = new LongSparseArray();
    private ArrayList<Long> createdDialogIds = new ArrayList();
    private ArrayList<Long> createdDialogMainThreadIds = new ArrayList();
    private ArrayList<Long> createdScheduledDialogIds = new ArrayList();
    private Runnable currentDeleteTaskRunnable;
    private int currentDeletingTaskChannelId;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public String dcDomainName;
    public LongSparseArray<Integer> deletedHistory = new LongSparseArray();
    private LongSparseArray<Dialog> deletingDialogs = new LongSparseArray();
    private final Comparator<Dialog> dialogComparator = new -$$Lambda$MessagesController$inGnIVSUUmZvhawO2qFC6eW_Ld4(this);
    public LongSparseArray<MessageObject> dialogMessage = new LongSparseArray();
    public SparseArray<MessageObject> dialogMessagesByIds = new SparseArray();
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds = new LongSparseArray();
    private SparseArray<ArrayList<Dialog>> dialogsByFolder = new SparseArray();
    public ArrayList<Dialog> dialogsCanAddUsers = new ArrayList();
    public ArrayList<Dialog> dialogsChannelsOnly = new ArrayList();
    private SparseBooleanArray dialogsEndReached = new SparseBooleanArray();
    public ArrayList<Dialog> dialogsForward = new ArrayList();
    public ArrayList<Dialog> dialogsGroupsOnly = new ArrayList();
    private boolean dialogsInTransaction;
    public boolean dialogsLoaded;
    public ArrayList<Dialog> dialogsServerOnly = new ArrayList();
    public ArrayList<Dialog> dialogsUsersOnly = new ArrayList();
    public LongSparseArray<Dialog> dialogs_dict = new LongSparseArray();
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    private SharedPreferences emojiPreferences;
    public boolean enableJoined;
    private ConcurrentHashMap<Integer, EncryptedChat> encryptedChats = new ConcurrentHashMap(10, 1.0f, 2);
    private SparseArray<ExportedChatInvite> exportedChats = new SparseArray();
    public boolean firstGettingTask;
    private SparseArray<ChatFull> fullChats = new SparseArray();
    private SparseArray<UserFull> fullUsers = new SparseArray();
    private boolean getDifferenceFirstSync = true;
    public boolean gettingDifference;
    private SparseBooleanArray gettingDifferenceChannels = new SparseBooleanArray();
    private boolean gettingNewDeleteTask;
    private SparseBooleanArray gettingUnknownChannels = new SparseBooleanArray();
    private LongSparseArray<Boolean> gettingUnknownDialogs = new LongSparseArray();
    public String gifSearchBot;
    public ArrayList<RecentMeUrl> hintDialogs = new ArrayList();
    public String imageSearchBot;
    private String installReferer;
    private boolean isLeftProxyChannel;
    private ArrayList<Integer> joiningToChannels = new ArrayList();
    public boolean keepAliveService;
    private int lastCheckProxyId;
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private LongSparseArray<Long> lastScheduledServerQueryTime = new LongSparseArray();
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private ArrayList<Integer> loadedFullChats = new ArrayList();
    private ArrayList<Integer> loadedFullParticipants = new ArrayList();
    private ArrayList<Integer> loadedFullUsers = new ArrayList();
    private boolean loadingAppConfig;
    public boolean loadingBlockedUsers = false;
    private SparseIntArray loadingChannelAdmins = new SparseIntArray();
    private SparseBooleanArray loadingDialogs = new SparseBooleanArray();
    private ArrayList<Integer> loadingFullChats = new ArrayList();
    private ArrayList<Integer> loadingFullParticipants = new ArrayList();
    private ArrayList<Integer> loadingFullUsers = new ArrayList();
    private int loadingNotificationSettings;
    private boolean loadingNotificationSignUpSettings;
    private LongSparseArray<Boolean> loadingPeerSettings = new LongSparseArray();
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
    private ConcurrentHashMap<String, TLObject> objectsByUsernames = new ConcurrentHashMap(100, 1.0f, 2);
    private boolean offlineSent;
    public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap(20, 1.0f, 2);
    private Runnable passwordCheckRunnable = new -$$Lambda$MessagesController$3aVF-iTJXsH45KV1-rOoji5zj20(this);
    private LongSparseArray<SparseArray<MessageObject>> pollsToCheck = new LongSparseArray();
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public LongSparseArray<CharSequence> printingStrings = new LongSparseArray();
    public LongSparseArray<Integer> printingStringsTypes = new LongSparseArray();
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0f, 2);
    private Dialog proxyDialog;
    private String proxyDialogAddress;
    private long proxyDialogId;
    public boolean qrLoginCamera;
    public int ratingDecay;
    private ArrayList<ReadTask> readTasks = new ArrayList();
    private LongSparseArray<ReadTask> readTasksMap = new LongSparseArray();
    public boolean registeringForPush;
    private LongSparseArray<ArrayList<Integer>> reloadingMessages = new LongSparseArray();
    private HashMap<String, ArrayList<MessageObject>> reloadingScheduledWebpages = new HashMap();
    private LongSparseArray<ArrayList<MessageObject>> reloadingScheduledWebpagesPending = new LongSparseArray();
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages = new HashMap();
    private LongSparseArray<ArrayList<MessageObject>> reloadingWebpagesPending = new LongSparseArray();
    private messages_Dialogs resetDialogsAll;
    private TL_messages_peerDialogs resetDialogsPinned;
    private boolean resetingDialogs;
    public int revokeTimeLimit;
    public int revokeTimePmLimit;
    public int secretWebpagePreview;
    public SparseArray<LongSparseArray<Boolean>> sendingTypings = new SparseArray();
    private SparseBooleanArray serverDialogsEndReached = new SparseBooleanArray();
    private SparseIntArray shortPollChannels = new SparseIntArray();
    private SparseIntArray shortPollOnlines = new SparseIntArray();
    private int statusRequest;
    private int statusSettingState;
    public boolean suggestContacts = true;
    public String suggestedLangCode;
    private Runnable themeCheckRunnable = -$$Lambda$RQB0Jwr1FTqp6hrbGUHuOs-9k1I.INSTANCE;
    public int totalBlockedCount = -1;
    public int unreadUnmutedDialogs;
    private final Comparator<Update> updatesComparator = new -$$Lambda$MessagesController$ebZej4dhcIpF5Mmy5QOrl8QRmY0(this);
    private SparseArray<ArrayList<Updates>> updatesQueueChannels = new SparseArray();
    private ArrayList<Updates> updatesQueuePts = new ArrayList();
    private ArrayList<Updates> updatesQueueQts = new ArrayList();
    private ArrayList<Updates> updatesQueueSeq = new ArrayList();
    private SparseLongArray updatesStartWaitTimeChannels = new SparseLongArray();
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    private String uploadingAvatar;
    private HashMap<String, Object> uploadingThemes = new HashMap();
    private String uploadingWallpaper;
    private OverrideWallpaperInfo uploadingWallpaperInfo;
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap(100, 1.0f, 2);
    public String venueSearchBot;
    private ArrayList<Long> visibleDialogMainThreadIds = new ArrayList();
    private ArrayList<Long> visibleScheduledDialogMainThreadIds = new ArrayList();
    public int webFileDatacenterId;
    public String youtubePipType;

    public static class PrintingUser {
        public SendMessageAction action;
        public long lastTime;
        public int userId;
    }

    private class ReadTask {
        public long dialogId;
        public int maxDate;
        public int maxId;
        public long sendRequestTime;

        private ReadTask() {
        }

        /* synthetic */ ReadTask(MessagesController messagesController, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class UserActionUpdatesPts extends Updates {
        private UserActionUpdatesPts() {
        }

        /* synthetic */ UserActionUpdatesPts(MessagesController messagesController, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class UserActionUpdatesSeq extends Updates {
        private UserActionUpdatesSeq() {
        }

        /* synthetic */ UserActionUpdatesSeq(MessagesController messagesController, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    static /* synthetic */ void lambda$blockUser$47(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$completeReadTask$163(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$deleteUserPhoto$68(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$hidePeerSettingsBar$33(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$installTheme$71(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$installTheme$72(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMentionMessageAsRead$158(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMentionsAsRead$165(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMessageContentAsRead$156(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$processUpdates$254(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$reportSpam$34(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$reportSpam$35(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$saveTheme$70(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$unblockUser$63(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$unregistedPush$201(TLObject tLObject, TL_error tL_error) {
    }

    public /* synthetic */ void lambda$new$0$MessagesController() {
        getUserConfig().checkSavedPassword();
    }

    public /* synthetic */ int lambda$new$1$MessagesController(Dialog dialog, Dialog dialog2) {
        boolean z = dialog instanceof TL_dialogFolder;
        if (z && !(dialog2 instanceof TL_dialogFolder)) {
            return -1;
        }
        if (!z && (dialog2 instanceof TL_dialogFolder)) {
            return 1;
        }
        if (!dialog.pinned && dialog2.pinned) {
            return 1;
        }
        if (dialog.pinned && !dialog2.pinned) {
            return -1;
        }
        if (dialog.pinned && dialog2.pinned) {
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

    public /* synthetic */ int lambda$new$2$MessagesController(Update update, Update update2) {
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
        updateType = getUpdateChannelId(update);
        updateType2 = getUpdateChannelId(update2);
        if (updateType == updateType2) {
            return AndroidUtilities.compare(getUpdatePts(update), getUpdatePts(update2));
        }
        return AndroidUtilities.compare(updateType, updateType2);
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$-Gw3Uv-wwUgMJp7JUqnur80LFBk(this));
        addSupportUser();
        String str = "emoji";
        String str2 = "mainconfig";
        String str3 = "Notifications";
        if (this.currentAccount == 0) {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences(str3, 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences(str2, 0);
            this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences(str, 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str3);
            stringBuilder.append(this.currentAccount);
            this.notificationsPreferences = context.getSharedPreferences(stringBuilder.toString(), 0);
            context = ApplicationLoader.applicationContext;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(this.currentAccount);
            this.mainPreferences = context.getSharedPreferences(stringBuilder2.toString(), 0);
            context = ApplicationLoader.applicationContext;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(str);
            stringBuilder3.append(this.currentAccount);
            this.emojiPreferences = context.getSharedPreferences(stringBuilder3.toString(), 0);
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
        this.mapKey = this.mainPreferences.getString("pk", null);
        this.installReferer = this.mainPreferences.getString("installReferer", null);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
        this.youtubePipType = this.mainPreferences.getString("youtubePipType", "disabled");
        String str4 = "keepAliveService";
        this.keepAliveService = this.mainPreferences.getBoolean(str4, false);
        this.backgroundConnection = this.mainPreferences.getBoolean(str4, false);
        this.proxyDialogId = this.mainPreferences.getLong("proxy_dialog", 0);
        this.proxyDialogAddress = this.mainPreferences.getString("proxyDialogAddress", null);
        this.nextTosCheckTime = this.notificationsPreferences.getInt("nextTosCheckTime", 0);
        this.venueSearchBot = this.mainPreferences.getString("venueSearchBot", "foursquare");
        this.gifSearchBot = this.mainPreferences.getString("gifSearchBot", "gif");
        this.imageSearchBot = this.mainPreferences.getString("imageSearchBot", "pic");
        this.blockedCountry = this.mainPreferences.getBoolean("blockedCountry", false);
        this.dcDomainName = this.mainPreferences.getString("dcDomainName2", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : "apv3.stel.com");
        SharedPreferences sharedPreferences = this.mainPreferences;
        if (ConnectionsManager.native_isTestBackend(this.currentAccount) == 0) {
            i2 = 4;
        }
        this.webFileDatacenterId = sharedPreferences.getInt("webFileDatacenterId", i2);
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
            getConnectionsManager().sendRequest(new TL_help_getAppConfig(), new -$$Lambda$MessagesController$ZEd2NwhNArWp0_3a1u7L3wUyxmU(this));
        }
    }

    public /* synthetic */ void lambda$loadAppConfig$5$MessagesController(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$VszrYfLrvTaDrQIQGUi8-O_imTo(this, tLObject));
    }

    public /* synthetic */ void lambda$null$4$MessagesController(TLObject tLObject) {
        if (tLObject instanceof TL_jsonObject) {
            Editor edit = this.mainPreferences.edit();
            TL_jsonObject tL_jsonObject = (TL_jsonObject) tLObject;
            int size = tL_jsonObject.value.size();
            Object obj = null;
            Object obj2 = null;
            Object obj3 = null;
            for (int i = 0; i < size; i++) {
                TL_jsonObjectValue tL_jsonObjectValue = (TL_jsonObjectValue) tL_jsonObject.value.get(i);
                JSONValue jSONValue;
                if ("emojies_animated_zoom".equals(tL_jsonObjectValue.key)) {
                    jSONValue = tL_jsonObjectValue.value;
                    if (jSONValue instanceof TL_jsonNumber) {
                        double d = (double) this.animatedEmojisZoom;
                        double d2 = ((TL_jsonNumber) jSONValue).value;
                        if (d != d2) {
                            this.animatedEmojisZoom = (float) d2;
                            edit.putFloat("animatedEmojisZoom", this.animatedEmojisZoom);
                        }
                    }
                } else {
                    UserConfig userConfig;
                    TL_jsonString tL_jsonString;
                    if ("wallet_config".equals(tL_jsonObjectValue.key)) {
                        if (tL_jsonObjectValue.value instanceof TL_jsonString) {
                            userConfig = getUserConfig();
                            tL_jsonString = (TL_jsonString) tL_jsonObjectValue.value;
                            if (!tL_jsonString.value.equals(userConfig.walletConfig)) {
                                userConfig.walletConfig = tL_jsonString.value;
                            }
                        }
                    } else {
                        if (!"wallet_blockchain_name".equals(tL_jsonObjectValue.key)) {
                            if ("youtube_pip".equals(tL_jsonObjectValue.key)) {
                                jSONValue = tL_jsonObjectValue.value;
                                if (jSONValue instanceof TL_jsonString) {
                                    tL_jsonString = (TL_jsonString) jSONValue;
                                    if (!tL_jsonString.value.equals(this.youtubePipType)) {
                                        this.youtubePipType = tL_jsonString.value;
                                        edit.putString("youtubePipType", this.youtubePipType);
                                    }
                                }
                            } else {
                                boolean z;
                                if ("background_connection".equals(tL_jsonObjectValue.key)) {
                                    jSONValue = tL_jsonObjectValue.value;
                                    if (jSONValue instanceof TL_jsonBool) {
                                        z = ((TL_jsonBool) jSONValue).value;
                                        if (z != this.backgroundConnection) {
                                            this.backgroundConnection = z;
                                            edit.putBoolean("backgroundConnection", this.backgroundConnection);
                                        }
                                    }
                                } else {
                                    if ("keep_alive_service".equals(tL_jsonObjectValue.key)) {
                                        jSONValue = tL_jsonObjectValue.value;
                                        if (jSONValue instanceof TL_jsonBool) {
                                            z = ((TL_jsonBool) jSONValue).value;
                                            if (z != this.keepAliveService) {
                                                this.keepAliveService = z;
                                                edit.putBoolean("keepAliveService", this.keepAliveService);
                                            }
                                        }
                                    } else {
                                        if ("qr_login_camera".equals(tL_jsonObjectValue.key)) {
                                            jSONValue = tL_jsonObjectValue.value;
                                            if (jSONValue instanceof TL_jsonBool) {
                                                z = ((TL_jsonBool) jSONValue).value;
                                                if (z != this.qrLoginCamera) {
                                                    this.qrLoginCamera = z;
                                                    edit.putBoolean("qrLoginCamera", this.qrLoginCamera);
                                                }
                                            }
                                        }
                                    }
                                }
                                obj = 1;
                                obj3 = 1;
                            }
                        } else if (tL_jsonObjectValue.value instanceof TL_jsonString) {
                            userConfig = getUserConfig();
                            tL_jsonString = (TL_jsonString) tL_jsonObjectValue.value;
                            if (!tL_jsonString.value.equals(userConfig.walletBlockchainName)) {
                                userConfig.walletBlockchainName = tL_jsonString.value;
                            }
                        }
                    }
                    obj2 = 1;
                }
                obj = 1;
            }
            if (obj != null) {
                edit.commit();
            }
            if (obj2 != null) {
                getTonController().onTonConfigUpdated();
                getUserConfig().saveConfig(false);
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            }
            if (obj3 != null) {
                ApplicationLoader.startPushService();
                ConnectionsManager connectionsManager = getConnectionsManager();
                connectionsManager.setPushConnectionEnabled(connectionsManager.isPushConnectionEnabled());
            }
        }
        this.loadingAppConfig = false;
    }

    public void updateConfig(TL_config tL_config) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$H9o5xE_MwHWS4ZrO4xT9MsqRUG4(this, tL_config));
    }

    public /* synthetic */ void lambda$updateConfig$6$MessagesController(TL_config tL_config) {
        String str;
        String str2;
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
            str = this.linkPrefix;
            this.linkPrefix = str.substring(0, str.length() - 1);
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
        str = tL_config.venue_search_username;
        if (str != null) {
            this.venueSearchBot = str;
        }
        str = tL_config.gif_search_username;
        if (str != null) {
            this.gifSearchBot = str;
        }
        if (this.imageSearchBot != null) {
            this.imageSearchBot = tL_config.img_search_username;
        }
        this.blockedCountry = tL_config.blocked_mode;
        this.dcDomainName = tL_config.dc_txt_domain_name;
        this.webFileDatacenterId = tL_config.webfile_dc_id;
        str = tL_config.suggested_lang_code;
        if (str != null) {
            str2 = this.suggestedLangCode;
            if (str2 == null || !str2.equals(str)) {
                this.suggestedLangCode = tL_config.suggested_lang_code;
                LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
            }
        }
        Theme.loadRemoteThemes(this.currentAccount, false);
        Theme.checkCurrentRemoteTheme(false);
        str2 = "telegram";
        if (tL_config.static_maps_provider == null) {
            tL_config.static_maps_provider = str2;
        }
        this.mapKey = null;
        this.mapProvider = 2;
        this.availableMapProviders = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("map providers = ");
        stringBuilder.append(tL_config.static_maps_provider);
        FileLog.d(stringBuilder.toString());
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
                    } else {
                        if ("google".equals(split3[0])) {
                            if (i == 0 && split2.length > 1) {
                                this.mapProvider = 4;
                            }
                            this.availableMapProviders |= 1;
                        } else if (str2.equals(split3[0])) {
                            if (i == 0) {
                                this.mapProvider = 2;
                            }
                            this.availableMapProviders |= 2;
                        }
                    }
                    if (split3.length > 1) {
                        this.mapKey = split3[1];
                    }
                }
            }
        }
        Editor edit = this.mainPreferences.edit();
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
        String str3 = this.mapKey;
        str2 = "pk";
        if (str3 != null) {
            edit.putString(str2, str3);
        } else {
            edit.remove(str2);
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
        TL_userForeign_old2 tL_userForeign_old2 = new TL_userForeign_old2();
        tL_userForeign_old2.phone = "333";
        tL_userForeign_old2.id = 333000;
        String str = "Telegram";
        tL_userForeign_old2.first_name = str;
        tL_userForeign_old2.last_name = "";
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
        tL_userForeign_old2 = new TL_userForeign_old2();
        tL_userForeign_old2.phone = "42777";
        tL_userForeign_old2.id = 777000;
        tL_userForeign_old2.verified = true;
        tL_userForeign_old2.first_name = str;
        tL_userForeign_old2.last_name = "Notifications";
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
    }

    public InputUser getInputUser(User user) {
        if (user == null) {
            return new TL_inputUserEmpty();
        }
        InputUser tL_inputUserSelf;
        if (user.id == getUserConfig().getClientUserId()) {
            tL_inputUserSelf = new TL_inputUserSelf();
        } else {
            InputUser tL_inputUser = new TL_inputUser();
            tL_inputUser.user_id = user.id;
            tL_inputUser.access_hash = user.access_hash;
            tL_inputUserSelf = tL_inputUser;
        }
        return tL_inputUserSelf;
    }

    public InputUser getInputUser(int i) {
        return getInputUser(getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(i)));
    }

    public static InputChannel getInputChannel(Chat chat) {
        if (!(chat instanceof TL_channel) && !(chat instanceof TL_channelForbidden)) {
            return new TL_inputChannelEmpty();
        }
        TL_inputChannel tL_inputChannel = new TL_inputChannel();
        tL_inputChannel.channel_id = chat.id;
        tL_inputChannel.access_hash = chat.access_hash;
        return tL_inputChannel;
    }

    public InputChannel getInputChannel(int i) {
        return getInputChannel(getChat(Integer.valueOf(i)));
    }

    public InputPeer getInputPeer(int i) {
        InputPeer tL_inputPeerChannel;
        if (i < 0) {
            i = -i;
            Chat chat = getChat(Integer.valueOf(i));
            if (ChatObject.isChannel(chat)) {
                tL_inputPeerChannel = new TL_inputPeerChannel();
                tL_inputPeerChannel.channel_id = i;
                tL_inputPeerChannel.access_hash = chat.access_hash;
                return tL_inputPeerChannel;
            }
            tL_inputPeerChannel = new TL_inputPeerChat();
            tL_inputPeerChannel.chat_id = i;
            return tL_inputPeerChannel;
        }
        User user = getUser(Integer.valueOf(i));
        tL_inputPeerChannel = new TL_inputPeerUser();
        tL_inputPeerChannel.user_id = i;
        if (user == null) {
            return tL_inputPeerChannel;
        }
        tL_inputPeerChannel.access_hash = user.access_hash;
        return tL_inputPeerChannel;
    }

    public Peer getPeer(int i) {
        if (i < 0) {
            i = -i;
            Chat chat = getChat(Integer.valueOf(i));
            Peer tL_peerChannel;
            if ((chat instanceof TL_channel) || (chat instanceof TL_channelForbidden)) {
                tL_peerChannel = new TL_peerChannel();
                tL_peerChannel.channel_id = i;
                return tL_peerChannel;
            }
            tL_peerChannel = new TL_peerChat();
            tL_peerChannel.chat_id = i;
            return tL_peerChannel;
        }
        getUser(Integer.valueOf(i));
        TL_peerUser tL_peerUser = new TL_peerUser();
        tL_peerUser.user_id = i;
        return tL_peerUser;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TL_inputThemeSettings tL_inputThemeSettings = null;
        String str;
        if (i == NotificationCenter.FileDidUpload) {
            str = (String) objArr[0];
            InputFile inputFile = (InputFile) objArr[1];
            String str2 = this.uploadingAvatar;
            if (str2 == null || !str2.equals(str)) {
                str2 = this.uploadingWallpaper;
                if (str2 == null || !str2.equals(str)) {
                    InputFile inputFile2;
                    ThemeInfo themeInfo;
                    ThemeAccent themeAccent;
                    Object obj = this.uploadingThemes.get(str);
                    if (obj instanceof ThemeInfo) {
                        ThemeInfo themeInfo2 = (ThemeInfo) obj;
                        if (str.equals(themeInfo2.uploadingThumb)) {
                            themeInfo2.uploadedThumb = inputFile;
                            themeInfo2.uploadingThumb = null;
                        } else if (str.equals(themeInfo2.uploadingFile)) {
                            themeInfo2.uploadedFile = inputFile;
                            themeInfo2.uploadingFile = null;
                        }
                        inputFile = themeInfo2.uploadedThumb;
                        inputFile2 = themeInfo2.uploadedFile;
                        themeInfo = themeInfo2;
                        themeAccent = null;
                    } else if (obj instanceof ThemeAccent) {
                        ThemeAccent themeAccent2 = (ThemeAccent) obj;
                        if (str.equals(themeAccent2.uploadingThumb)) {
                            themeAccent2.uploadedThumb = inputFile;
                            themeAccent2.uploadingThumb = null;
                        } else if (str.equals(themeAccent2.uploadingFile)) {
                            themeAccent2.uploadedFile = inputFile;
                            themeAccent2.uploadingFile = null;
                        }
                        ThemeInfo themeInfo3 = themeAccent2.parentTheme;
                        themeInfo = themeInfo3;
                        themeAccent = themeAccent2;
                        inputFile = themeAccent2.uploadedThumb;
                        inputFile2 = themeAccent2.uploadedFile;
                    } else {
                        inputFile = null;
                        inputFile2 = inputFile;
                        themeInfo = inputFile2;
                        themeAccent = themeInfo;
                    }
                    this.uploadingThemes.remove(str);
                    if (!(inputFile2 == null || inputFile == null)) {
                        TL_theme tL_theme;
                        File file = new File(str);
                        TL_account_uploadTheme tL_account_uploadTheme = new TL_account_uploadTheme();
                        tL_account_uploadTheme.mime_type = "application/x-tgtheme-android";
                        str2 = "theme.attheme";
                        tL_account_uploadTheme.file_name = str2;
                        tL_account_uploadTheme.file = inputFile2;
                        tL_account_uploadTheme.file.name = str2;
                        tL_account_uploadTheme.thumb = inputFile;
                        tL_account_uploadTheme.thumb.name = "theme-preview.jpg";
                        tL_account_uploadTheme.flags |= 1;
                        if (themeAccent != null) {
                            WallPaperSettings wallPaperSettings;
                            themeAccent.uploadedFile = null;
                            themeAccent.uploadedThumb = null;
                            tL_theme = themeAccent.info;
                            tL_inputThemeSettings = new TL_inputThemeSettings();
                            tL_inputThemeSettings.base_theme = Theme.getBaseThemeByKey(themeInfo.name);
                            tL_inputThemeSettings.accent_color = themeAccent.accentColor;
                            int i3 = themeAccent.myMessagesAccentColor;
                            if (i3 != 0) {
                                tL_inputThemeSettings.message_bottom_color = i3;
                                tL_inputThemeSettings.flags |= 1;
                            }
                            i3 = themeAccent.myMessagesGradientAccentColor;
                            if (i3 != 0) {
                                tL_inputThemeSettings.message_top_color = i3;
                                tL_inputThemeSettings.flags |= 1;
                            }
                            tL_inputThemeSettings.flags |= 2;
                            tL_inputThemeSettings.wallpaper_settings = new TL_wallPaperSettings();
                            if (TextUtils.isEmpty(themeAccent.patternSlug)) {
                                tL_inputThemeSettings.wallpaper = new TL_inputWallPaperNoFile();
                            } else {
                                TL_inputWallPaperSlug tL_inputWallPaperSlug = new TL_inputWallPaperSlug();
                                tL_inputWallPaperSlug.slug = themeAccent.patternSlug;
                                tL_inputThemeSettings.wallpaper = tL_inputWallPaperSlug;
                                wallPaperSettings = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings.intensity = (int) (themeAccent.patternIntensity * 100.0f);
                                wallPaperSettings.flags |= 8;
                            }
                            wallPaperSettings = tL_inputThemeSettings.wallpaper_settings;
                            wallPaperSettings.motion = themeAccent.patternMotion;
                            long j = themeAccent.backgroundOverrideColor;
                            if (j != 0) {
                                wallPaperSettings.background_color = (int) j;
                                wallPaperSettings.flags |= 1;
                            }
                            j = themeAccent.backgroundGradientOverrideColor;
                            if (j != 0) {
                                wallPaperSettings = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings.second_background_color = (int) j;
                                wallPaperSettings.flags |= 16;
                                wallPaperSettings.rotation = AndroidUtilities.getWallpaperRotation(themeAccent.backgroundRotation, true);
                            }
                        } else {
                            themeInfo.uploadedFile = null;
                            themeInfo.uploadedThumb = null;
                            tL_theme = themeInfo.info;
                        }
                        getConnectionsManager().sendRequest(tL_account_uploadTheme, new -$$Lambda$MessagesController$yUpA2fs0hD5PF0RFsNkGeJ9FyjY(this, tL_theme, themeInfo, tL_inputThemeSettings, themeAccent));
                    }
                } else {
                    TL_account_uploadWallPaper tL_account_uploadWallPaper = new TL_account_uploadWallPaper();
                    tL_account_uploadWallPaper.file = inputFile;
                    tL_account_uploadWallPaper.mime_type = "image/jpeg";
                    TL_wallPaperSettings tL_wallPaperSettings = new TL_wallPaperSettings();
                    OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
                    tL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
                    tL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
                    tL_account_uploadWallPaper.settings = tL_wallPaperSettings;
                    getConnectionsManager().sendRequest(tL_account_uploadWallPaper, new -$$Lambda$MessagesController$0X6aUkz7iyW23B1znikJVXca3kM(this, tL_wallPaperSettings));
                }
            } else {
                TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TL_photos_uploadProfilePhoto();
                tL_photos_uploadProfilePhoto.file = inputFile;
                getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new -$$Lambda$MessagesController$Y0BYXWIVD4hCv-fcSMgpAAgE97g(this));
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            str = (String) objArr[0];
            String str3 = this.uploadingAvatar;
            if (str3 == null || !str3.equals(str)) {
                str3 = this.uploadingWallpaper;
                if (str3 == null || !str3.equals(str)) {
                    Object remove = this.uploadingThemes.remove(str);
                    if (remove instanceof ThemeInfo) {
                        ThemeInfo themeInfo4 = (ThemeInfo) remove;
                        themeInfo4.uploadedFile = null;
                        themeInfo4.uploadedThumb = null;
                        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo4, null);
                    } else if (remove instanceof ThemeAccent) {
                        ((ThemeAccent) remove).uploadingThumb = null;
                        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, r13.parentTheme, r13);
                    }
                } else {
                    this.uploadingWallpaper = null;
                    this.uploadingWallpaperInfo = null;
                }
            } else {
                this.uploadingAvatar = null;
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            if (!((Boolean) objArr[6]).booleanValue()) {
                Integer num = (Integer) objArr[0];
                Integer num2 = (Integer) objArr[1];
                Long l = (Long) objArr[3];
                MessageObject messageObject = (MessageObject) this.dialogMessage.get(l.longValue());
                if (messageObject != null && (messageObject.getId() == num.intValue() || messageObject.messageOwner.local_id == num.intValue())) {
                    messageObject.messageOwner.id = num2.intValue();
                    messageObject.messageOwner.send_state = 0;
                }
                Dialog dialog = (Dialog) this.dialogs_dict.get(l.longValue());
                if (dialog != null && dialog.top_message == num.intValue()) {
                    dialog.top_message = num2.intValue();
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
                messageObject = (MessageObject) this.dialogMessagesByIds.get(num.intValue());
                this.dialogMessagesByIds.remove(num.intValue());
                if (messageObject != null) {
                    this.dialogMessagesByIds.put(num2.intValue(), messageObject);
                }
                i2 = (int) l.longValue();
                if (i2 < 0) {
                    i2 = -i2;
                    ChatFull chatFull = (ChatFull) this.fullChats.get(i2);
                    Chat chat = getChat(Integer.valueOf(i2));
                    if (!(chat == null || ChatObject.hasAdminRights(chat) || chatFull == null || chatFull.slowmode_seconds == 0)) {
                        chatFull.slowmode_next_send_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + chatFull.slowmode_seconds;
                        chatFull.flags |= 262144;
                        getMessagesStorage().updateChatInfo(chatFull, false);
                    }
                }
            }
        } else if (i == NotificationCenter.updateMessageMedia) {
            Message message = (Message) objArr[0];
            MessageObject messageObject2 = (MessageObject) this.dialogMessagesByIds.get(message.id);
            if (messageObject2 != null) {
                messageObject2.messageOwner.media = message.media;
                MessageMedia messageMedia = message.media;
                if (messageMedia.ttl_seconds != 0 && ((messageMedia.photo instanceof TL_photoEmpty) || (messageMedia.document instanceof TL_documentEmpty))) {
                    messageObject2.setType();
                    getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                }
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$8$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            User user = getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                putUser(user, true);
            } else {
                getUserConfig().setCurrentUser(user);
            }
            if (user != null) {
                TL_photos_photo tL_photos_photo = (TL_photos_photo) tLObject;
                ArrayList arrayList = tL_photos_photo.photo.sizes;
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
                user.photo = new TL_userProfilePhoto();
                UserProfilePhoto userProfilePhoto = user.photo;
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
                getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$1UIL110dbkCGyvpyo4jpNo9SP4I(this));
            }
        }
    }

    public /* synthetic */ void lambda$null$7$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(2));
        getUserConfig().saveConfig(true);
    }

    public /* synthetic */ void lambda$didReceivedNotification$10$MessagesController(TL_wallPaperSettings tL_wallPaperSettings, TLObject tLObject, TL_error tL_error) {
        TL_wallPaper tL_wallPaper = (TL_wallPaper) tLObject;
        File file = new File(ApplicationLoader.getFilesDirFixed(), this.uploadingWallpaperInfo.originalFileName);
        if (tL_wallPaper != null) {
            try {
                AndroidUtilities.copyFile(file, FileLoader.getPathToAttach(tL_wallPaper.document, true));
            } catch (Exception unused) {
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$UrQ0sTGI3Otzc2sSL6K_eOsR19E(this, tL_wallPaper, tL_wallPaperSettings, file));
    }

    public /* synthetic */ void lambda$null$9$MessagesController(TL_wallPaper tL_wallPaper, TL_wallPaperSettings tL_wallPaperSettings, File file) {
        if (this.uploadingWallpaper != null && tL_wallPaper != null) {
            tL_wallPaper.settings = tL_wallPaperSettings;
            tL_wallPaper.flags |= 4;
            OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
            overrideWallpaperInfo.slug = tL_wallPaper.slug;
            overrideWallpaperInfo.saveOverrideWallpaper();
            ArrayList arrayList = new ArrayList();
            arrayList.add(tL_wallPaper);
            getMessagesStorage().putWallpapers(arrayList, 2);
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 320);
            if (closestPhotoSizeWithSize != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(closestPhotoSizeWithSize.location.volume_id);
                stringBuilder.append("_");
                stringBuilder.append(closestPhotoSizeWithSize.location.local_id);
                String str = "@100_100";
                stringBuilder.append(str);
                String stringBuilder2 = stringBuilder.toString();
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(Utilities.MD5(file.getAbsolutePath()));
                stringBuilder3.append(str);
                ImageLoader.getInstance().replaceImageInCache(stringBuilder3.toString(), stringBuilder2, ImageLocation.getForDocument(closestPhotoSizeWithSize, tL_wallPaper.document), false);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersNeedReload, tL_wallPaper.slug);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$16$MessagesController(TL_theme tL_theme, ThemeInfo themeInfo, TL_inputThemeSettings tL_inputThemeSettings, ThemeAccent themeAccent, TLObject tLObject, TL_error tL_error) {
        String name = tL_theme != null ? tL_theme.title : themeInfo.getName();
        int lastIndexOf = name.lastIndexOf(".attheme");
        if (lastIndexOf > 0) {
            name = name.substring(0, lastIndexOf);
        }
        if (tLObject != null) {
            Document document = (Document) tLObject;
            TL_inputDocument tL_inputDocument = new TL_inputDocument();
            tL_inputDocument.access_hash = document.access_hash;
            tL_inputDocument.id = document.id;
            tL_inputDocument.file_reference = document.file_reference;
            if (tL_theme == null || !tL_theme.creator) {
                TL_account_createTheme tL_account_createTheme = new TL_account_createTheme();
                tL_account_createTheme.document = tL_inputDocument;
                tL_account_createTheme.flags |= 4;
                String str = (tL_theme == null || TextUtils.isEmpty(tL_theme.slug)) ? "" : tL_theme.slug;
                tL_account_createTheme.slug = str;
                tL_account_createTheme.title = name;
                if (tL_inputThemeSettings != null) {
                    tL_account_createTheme.settings = tL_inputThemeSettings;
                    tL_account_createTheme.flags |= 8;
                }
                getConnectionsManager().sendRequest(tL_account_createTheme, new -$$Lambda$MessagesController$8pFc6NDdyHIN9OiUggaLtP4v1c4(this, themeInfo, themeAccent));
                return;
            }
            TL_account_updateTheme tL_account_updateTheme = new TL_account_updateTheme();
            TL_inputTheme tL_inputTheme = new TL_inputTheme();
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
            getConnectionsManager().sendRequest(tL_account_updateTheme, new -$$Lambda$MessagesController$4i4T579xSt8cXV1phYKjHpEOfP0(this, themeInfo, themeAccent));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ynufN6abcXmOgpZMWypIEQxGwBU(this, themeInfo, themeAccent));
    }

    public /* synthetic */ void lambda$null$12$MessagesController(ThemeInfo themeInfo, ThemeAccent themeAccent, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$xzOiK08eeRnFb_SEqfm5LtW1m_Q(this, tLObject, themeInfo, themeAccent));
    }

    public /* synthetic */ void lambda$null$11$MessagesController(TLObject tLObject, ThemeInfo themeInfo, ThemeAccent themeAccent) {
        if (tLObject instanceof TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TL_theme) tLObject, this.currentAccount, false);
            installTheme(themeInfo, themeAccent, themeInfo == Theme.getCurrentNightTheme());
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public /* synthetic */ void lambda$null$14$MessagesController(ThemeInfo themeInfo, ThemeAccent themeAccent, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$amTW3hZkyMf6s2SlhP0wIqQF-bc(this, tLObject, themeInfo, themeAccent));
    }

    public /* synthetic */ void lambda$null$13$MessagesController(TLObject tLObject, ThemeInfo themeInfo, ThemeAccent themeAccent) {
        if (tLObject instanceof TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TL_theme) tLObject, this.currentAccount, false);
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public /* synthetic */ void lambda$null$15$MessagesController(ThemeInfo themeInfo, ThemeAccent themeAccent) {
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
        getTonController().cleanup();
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).putLong("lastStickersLoadTimeMask", 0).putLong("lastStickersLoadTimeFavs", 0).commit();
        this.mainPreferences.edit().remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").remove("dcDomainName2").remove("webFileDatacenterId").commit();
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$LRvJWXggpu3Zew0bMMevL6lf-VE(this));
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$_Oc_ayHHtf9WTSvn6DHxD5GWIc0(this));
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

    public User getUser(Integer num) {
        return (User) this.users.get(num);
    }

    public TLObject getUserOrChat(String str) {
        return (str == null || str.length() == 0) ? null : (TLObject) this.objectsByUsernames.get(str.toLowerCase());
    }

    public ConcurrentHashMap<Integer, User> getUsers() {
        return this.users;
    }

    public ConcurrentHashMap<Integer, Chat> getChats() {
        return this.chats;
    }

    public Chat getChat(Integer num) {
        return (Chat) this.chats.get(num);
    }

    public EncryptedChat getEncryptedChat(Integer num) {
        return (EncryptedChat) this.encryptedChats.get(num);
    }

    public EncryptedChat getEncryptedChatDB(int i, boolean z) {
        EncryptedChat encryptedChat = (EncryptedChat) this.encryptedChats.get(Integer.valueOf(i));
        if (encryptedChat != null) {
            if (!z) {
                return encryptedChat;
            }
            if (!((encryptedChat instanceof TL_encryptedChatWaiting) || (encryptedChat instanceof TL_encryptedChatRequested))) {
                return encryptedChat;
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList arrayList = new ArrayList();
        getMessagesStorage().getEncryptedChat(i, countDownLatch, arrayList);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (arrayList.size() != 2) {
            return encryptedChat;
        }
        encryptedChat = (EncryptedChat) arrayList.get(0);
        User user = (User) arrayList.get(1);
        putEncryptedChat(encryptedChat, false);
        putUser(user, true);
        return encryptedChat;
    }

    public boolean isDialogVisible(long j, boolean z) {
        return (z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds).contains(Long.valueOf(j));
    }

    public void setLastVisibleDialogId(long j, boolean z, boolean z2) {
        ArrayList arrayList = z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds;
        if (!z2) {
            arrayList.remove(Long.valueOf(j));
        } else if (!arrayList.contains(Long.valueOf(j))) {
            arrayList.add(Long.valueOf(j));
        }
    }

    public void setLastCreatedDialogId(long j, boolean z, boolean z2) {
        if (!z) {
            ArrayList arrayList = this.createdDialogMainThreadIds;
            if (!z2) {
                arrayList.remove(Long.valueOf(j));
                SparseArray sparseArray = (SparseArray) this.pollsToCheck.get(j);
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$PTdkE5WlMN7BYGypHn90wyIYfSU(this, z, z2, j));
    }

    public /* synthetic */ void lambda$setLastCreatedDialogId$19$MessagesController(boolean z, boolean z2, long j) {
        ArrayList arrayList = z ? this.createdScheduledDialogIds : this.createdDialogIds;
        if (!z2) {
            arrayList.remove(Long.valueOf(j));
        } else if (!arrayList.contains(Long.valueOf(j))) {
            arrayList.add(Long.valueOf(j));
        }
    }

    public ExportedChatInvite getExportedInvite(int i) {
        return (ExportedChatInvite) this.exportedChats.get(i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0028 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027 A:{RETURN} */
    public boolean putUser(org.telegram.tgnet.TLRPC.User r6, boolean r7) {
        /*
        r5 = this;
        r0 = 0;
        if (r6 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = 1;
        if (r7 == 0) goto L_0x0016;
    L_0x0007:
        r7 = r6.id;
        r2 = r7 / 1000;
        r3 = 333; // 0x14d float:4.67E-43 double:1.645E-321;
        if (r2 == r3) goto L_0x0016;
    L_0x000f:
        r2 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r7 == r2) goto L_0x0016;
    L_0x0014:
        r7 = 1;
        goto L_0x0017;
    L_0x0016:
        r7 = 0;
    L_0x0017:
        r2 = r5.users;
        r3 = r6.id;
        r3 = java.lang.Integer.valueOf(r3);
        r2 = r2.get(r3);
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        if (r2 != r6) goto L_0x0028;
    L_0x0027:
        return r0;
    L_0x0028:
        if (r2 == 0) goto L_0x003d;
    L_0x002a:
        r3 = r2.username;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x003d;
    L_0x0032:
        r3 = r5.objectsByUsernames;
        r4 = r2.username;
        r4 = r4.toLowerCase();
        r3.remove(r4);
    L_0x003d:
        r3 = r6.username;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0050;
    L_0x0045:
        r3 = r5.objectsByUsernames;
        r4 = r6.username;
        r4 = r4.toLowerCase();
        r3.put(r4, r6);
    L_0x0050:
        r3 = r6.min;
        r4 = 0;
        if (r3 == 0) goto L_0x0097;
    L_0x0055:
        if (r2 == 0) goto L_0x008a;
    L_0x0057:
        if (r7 != 0) goto L_0x011c;
    L_0x0059:
        r7 = r6.bot;
        if (r7 == 0) goto L_0x0072;
    L_0x005d:
        r7 = r6.username;
        if (r7 == 0) goto L_0x006a;
    L_0x0061:
        r2.username = r7;
        r7 = r2.flags;
        r7 = r7 | 8;
        r2.flags = r7;
        goto L_0x0072;
    L_0x006a:
        r7 = r2.flags;
        r7 = r7 & -9;
        r2.flags = r7;
        r2.username = r4;
    L_0x0072:
        r6 = r6.photo;
        if (r6 == 0) goto L_0x0080;
    L_0x0076:
        r2.photo = r6;
        r6 = r2.flags;
        r6 = r6 | 32;
        r2.flags = r6;
        goto L_0x011c;
    L_0x0080:
        r6 = r2.flags;
        r6 = r6 & -33;
        r2.flags = r6;
        r2.photo = r4;
        goto L_0x011c;
    L_0x008a:
        r7 = r5.users;
        r1 = r6.id;
        r1 = java.lang.Integer.valueOf(r1);
        r7.put(r1, r6);
        goto L_0x011c;
    L_0x0097:
        if (r7 != 0) goto L_0x00cf;
    L_0x0099:
        r7 = r5.users;
        r3 = r6.id;
        r3 = java.lang.Integer.valueOf(r3);
        r7.put(r3, r6);
        r7 = r6.id;
        r3 = r5.getUserConfig();
        r3 = r3.getClientUserId();
        if (r7 != r3) goto L_0x00be;
    L_0x00b0:
        r7 = r5.getUserConfig();
        r7.setCurrentUser(r6);
        r7 = r5.getUserConfig();
        r7.saveConfig(r1);
    L_0x00be:
        if (r2 == 0) goto L_0x011c;
    L_0x00c0:
        r6 = r6.status;
        if (r6 == 0) goto L_0x011c;
    L_0x00c4:
        r7 = r2.status;
        if (r7 == 0) goto L_0x011c;
    L_0x00c8:
        r6 = r6.expires;
        r7 = r7.expires;
        if (r6 == r7) goto L_0x011c;
    L_0x00ce:
        return r1;
    L_0x00cf:
        if (r2 != 0) goto L_0x00dd;
    L_0x00d1:
        r7 = r5.users;
        r1 = r6.id;
        r1 = java.lang.Integer.valueOf(r1);
        r7.put(r1, r6);
        goto L_0x011c;
    L_0x00dd:
        r7 = r2.min;
        if (r7 == 0) goto L_0x011c;
    L_0x00e1:
        r6.min = r0;
        r7 = r2.bot;
        if (r7 == 0) goto L_0x00fc;
    L_0x00e7:
        r7 = r2.username;
        if (r7 == 0) goto L_0x00f4;
    L_0x00eb:
        r6.username = r7;
        r7 = r6.flags;
        r7 = r7 | 8;
        r6.flags = r7;
        goto L_0x00fc;
    L_0x00f4:
        r7 = r6.flags;
        r7 = r7 & -9;
        r6.flags = r7;
        r6.username = r4;
    L_0x00fc:
        r7 = r2.photo;
        if (r7 == 0) goto L_0x0109;
    L_0x0100:
        r6.photo = r7;
        r7 = r6.flags;
        r7 = r7 | 32;
        r6.flags = r7;
        goto L_0x0111;
    L_0x0109:
        r7 = r6.flags;
        r7 = r7 & -33;
        r6.flags = r7;
        r6.photo = r4;
    L_0x0111:
        r7 = r5.users;
        r1 = r6.id;
        r1 = java.lang.Integer.valueOf(r1);
        r7.put(r1, r6);
    L_0x011c:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.putUser(org.telegram.tgnet.TLRPC$User, boolean):boolean");
    }

    public void putUsers(ArrayList<User> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            Object obj = null;
            for (int i = 0; i < size; i++) {
                if (putUser((User) arrayList.get(i), z)) {
                    obj = 1;
                }
            }
            if (obj != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$SJiPAbZe0GQodyMknNW1xOQlzWU(this));
            }
        }
    }

    public /* synthetic */ void lambda$putUsers$20$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    public void putChat(Chat chat, boolean z) {
        if (chat != null) {
            Chat chat2 = (Chat) this.chats.get(Integer.valueOf(chat.id));
            if (chat2 != chat) {
                if (!(chat2 == null || TextUtils.isEmpty(chat2.username))) {
                    this.objectsByUsernames.remove(chat2.username.toLowerCase());
                }
                if (!TextUtils.isEmpty(chat.username)) {
                    this.objectsByUsernames.put(chat.username.toLowerCase(), chat);
                }
                TL_chatBannedRights tL_chatBannedRights;
                TL_chatAdminRights tL_chatAdminRights;
                String str;
                if (!chat.min) {
                    boolean z2 = false;
                    int i;
                    if (!z) {
                        if (chat2 != null) {
                            if (chat.version != chat2.version) {
                                this.loadedFullChats.remove(Integer.valueOf(chat.id));
                            }
                            i = chat2.participants_count;
                            if (i != 0 && chat.participants_count == 0) {
                                chat.participants_count = i;
                                chat.flags |= 131072;
                            }
                            tL_chatBannedRights = chat2.banned_rights;
                            i = tL_chatBannedRights != null ? tL_chatBannedRights.flags : 0;
                            TL_chatBannedRights tL_chatBannedRights2 = chat.banned_rights;
                            int i2 = tL_chatBannedRights2 != null ? tL_chatBannedRights2.flags : 0;
                            TL_chatBannedRights tL_chatBannedRights3 = chat2.default_banned_rights;
                            boolean z3 = tL_chatBannedRights3 != null ? tL_chatBannedRights3.flags : false;
                            TL_chatBannedRights tL_chatBannedRights4 = chat.default_banned_rights;
                            if (tL_chatBannedRights4 != null) {
                                z2 = tL_chatBannedRights4.flags;
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
                            if (!(i == i2 && z3 == z2)) {
                                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$QI9kvFFWyf5nFEU6c4W_RoaNIeY(this, chat));
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
                        tL_chatBannedRights = chat2.default_banned_rights;
                        if (tL_chatBannedRights != null) {
                            chat.default_banned_rights = tL_chatBannedRights;
                            chat.flags |= 262144;
                        }
                        tL_chatAdminRights = chat2.admin_rights;
                        if (tL_chatAdminRights != null) {
                            chat.admin_rights = tL_chatAdminRights;
                            chat.flags |= 16384;
                        }
                        tL_chatBannedRights = chat2.banned_rights;
                        if (tL_chatBannedRights != null) {
                            chat.banned_rights = tL_chatBannedRights;
                            chat.flags |= 32768;
                        }
                        str = chat2.username;
                        if (str != null) {
                            chat.username = str;
                            chat.flags |= 64;
                        } else {
                            chat.flags &= -65;
                            chat.username = null;
                        }
                        i = chat2.participants_count;
                        if (i != 0 && chat.participants_count == 0) {
                            chat.participants_count = i;
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
                    tL_chatBannedRights = chat.default_banned_rights;
                    if (tL_chatBannedRights != null) {
                        chat2.default_banned_rights = tL_chatBannedRights;
                        chat2.flags |= 262144;
                    }
                    tL_chatAdminRights = chat.admin_rights;
                    if (tL_chatAdminRights != null) {
                        chat2.admin_rights = tL_chatAdminRights;
                        chat2.flags |= 16384;
                    }
                    tL_chatBannedRights = chat.banned_rights;
                    if (tL_chatBannedRights != null) {
                        chat2.banned_rights = tL_chatBannedRights;
                        chat2.flags |= 32768;
                    }
                    str = chat.username;
                    if (str != null) {
                        chat2.username = str;
                        chat2.flags |= 64;
                    } else {
                        chat2.flags &= -65;
                        chat2.username = null;
                    }
                    int i3 = chat.participants_count;
                    if (i3 != 0) {
                        chat2.participants_count = i3;
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$putChat$21$MessagesController(Chat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    public void putChats(ArrayList<Chat> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                putChat((Chat) arrayList.get(i), z);
            }
        }
    }

    public void setReferer(String str) {
        if (str != null) {
            this.installReferer = str;
            this.mainPreferences.edit().putString("installReferer", str).commit();
        }
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, boolean z) {
        if (encryptedChat != null) {
            if (z) {
                this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
            } else {
                this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            }
        }
    }

    public void putEncryptedChats(ArrayList<EncryptedChat> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                putEncryptedChat((EncryptedChat) arrayList.get(i), z);
            }
        }
    }

    public UserFull getUserFull(int i) {
        return (UserFull) this.fullUsers.get(i);
    }

    public ChatFull getChatFull(int i) {
        return (ChatFull) this.fullChats.get(i);
    }

    public void cancelLoadFullUser(int i) {
        this.loadingFullUsers.remove(Integer.valueOf(i));
    }

    public void cancelLoadFullChat(int i) {
        this.loadingFullChats.remove(Integer.valueOf(i));
    }

    /* Access modifiers changed, original: protected */
    public void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<Dialog> arrayList, long j) {
        if (j != 0 || (arrayList != null && !arrayList.isEmpty())) {
            TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    InputPeer inputPeer = getInputPeer((int) ((Dialog) arrayList.get(i)).id);
                    if (!(inputPeer instanceof TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                        TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                        tL_inputDialogPeer.peer = inputPeer;
                        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                    }
                }
            } else {
                InputPeer inputPeer2 = getInputPeer((int) j);
                if (!(inputPeer2 instanceof TL_inputPeerChannel) || inputPeer2.access_hash != 0) {
                    TL_inputDialogPeer tL_inputDialogPeer2 = new TL_inputDialogPeer();
                    tL_inputDialogPeer2.peer = inputPeer2;
                    tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer2);
                } else {
                    return;
                }
            }
            if (!tL_messages_getPeerDialogs.peers.isEmpty()) {
                getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$b1WVaYgf3OYXKAbftUm87IgCb0I(this));
            }
        }
    }

    public /* synthetic */ void lambda$reloadDialogsReadValue$22$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tL_messages_peerDialogs.dialogs.size(); i++) {
                Dialog dialog = (Dialog) tL_messages_peerDialogs.dialogs.get(i);
                if (dialog.read_inbox_max_id == 0) {
                    dialog.read_inbox_max_id = 1;
                }
                if (dialog.read_outbox_max_id == 0) {
                    dialog.read_outbox_max_id = 1;
                }
                DialogObject.initDialog(dialog);
                Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = Integer.valueOf(0);
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                        tL_updateReadChannelInbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelInbox.max_id = dialog.read_inbox_max_id;
                        arrayList.add(tL_updateReadChannelInbox);
                    } else {
                        TL_updateReadHistoryInbox tL_updateReadHistoryInbox = new TL_updateReadHistoryInbox();
                        tL_updateReadHistoryInbox.peer = dialog.peer;
                        tL_updateReadHistoryInbox.max_id = dialog.read_inbox_max_id;
                        arrayList.add(tL_updateReadHistoryInbox);
                    }
                }
                num = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = Integer.valueOf(0);
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_updateReadChannelOutbox();
                        tL_updateReadChannelOutbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelOutbox.max_id = dialog.read_outbox_max_id;
                        arrayList.add(tL_updateReadChannelOutbox);
                    } else {
                        TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox = new TL_updateReadHistoryOutbox();
                        tL_updateReadHistoryOutbox.peer = dialog.peer;
                        tL_updateReadHistoryOutbox.max_id = dialog.read_outbox_max_id;
                        arrayList.add(tL_updateReadHistoryOutbox);
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                processUpdateArray(arrayList, null, null, false, 0);
            }
        }
    }

    public String getAdminRank(int i, int i2) {
        SparseArray sparseArray = (SparseArray) this.channelAdmins.get(i);
        if (sparseArray == null) {
            return null;
        }
        return (String) sparseArray.get(i2);
    }

    public boolean isChannelAdminsLoaded(int i) {
        return this.channelAdmins.get(i) != null;
    }

    public void loadChannelAdmins(int i, boolean z) {
        if (SystemClock.uptimeMillis() - ((long) this.loadingChannelAdmins.get(i)) >= 60) {
            this.loadingChannelAdmins.put(i, (int) (SystemClock.uptimeMillis() / 1000));
            if (z) {
                getMessagesStorage().loadChannelAdmins(i);
            } else {
                TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
                tL_channels_getParticipants.channel = getInputChannel(i);
                tL_channels_getParticipants.limit = 100;
                tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
                getConnectionsManager().sendRequest(tL_channels_getParticipants, new -$$Lambda$MessagesController$_j0o08aPi5cogoGeyqOk4JFPNC8(this, i));
            }
        }
    }

    public /* synthetic */ void lambda$loadChannelAdmins$23$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_channels_channelParticipants) {
            processLoadedAdminsResponse(i, (TL_channels_channelParticipants) tLObject);
        }
    }

    public void processLoadedAdminsResponse(int i, TL_channels_channelParticipants tL_channels_channelParticipants) {
        SparseArray sparseArray = new SparseArray(tL_channels_channelParticipants.participants.size());
        for (int i2 = 0; i2 < tL_channels_channelParticipants.participants.size(); i2++) {
            ChannelParticipant channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i2);
            int i3 = channelParticipant.user_id;
            Object obj = channelParticipant.rank;
            if (obj == null) {
                obj = "";
            }
            sparseArray.put(i3, obj);
        }
        processLoadedChannelAdmins(sparseArray, i, false);
    }

    public void processLoadedChannelAdmins(SparseArray<String> sparseArray, int i, boolean z) {
        if (!z) {
            getMessagesStorage().putChannelAdmins(i, sparseArray);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$eJN8FRqnhVrxroxGjc2kYU8l8M0(this, i, sparseArray, z));
    }

    public /* synthetic */ void lambda$processLoadedChannelAdmins$24$MessagesController(int i, SparseArray sparseArray, boolean z) {
        this.channelAdmins.put(i, sparseArray);
        if (z) {
            this.loadingChannelAdmins.delete(i);
            loadChannelAdmins(i, false);
        }
    }

    public void loadFullChat(int i, int i2, boolean z) {
        boolean contains = this.loadedFullChats.contains(Integer.valueOf(i));
        if (!this.loadingFullChats.contains(Integer.valueOf(i))) {
            if (z || !contains) {
                TLObject tL_channels_getFullChannel;
                this.loadingFullChats.add(Integer.valueOf(i));
                long j = (long) (-i);
                Chat chat = getChat(Integer.valueOf(i));
                if (ChatObject.isChannel(chat)) {
                    tL_channels_getFullChannel = new TL_channels_getFullChannel();
                    tL_channels_getFullChannel.channel = getInputChannel(chat);
                    if (chat.megagroup) {
                        loadChannelAdmins(i, contains ^ 1);
                    }
                } else {
                    tL_channels_getFullChannel = new TL_messages_getFullChat();
                    tL_channels_getFullChannel.chat_id = i;
                    if (this.dialogs_read_inbox_max.get(Long.valueOf(j)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j)) == null) {
                        reloadDialogsReadValue(null, j);
                    }
                }
                i = getConnectionsManager().sendRequest(tL_channels_getFullChannel, new -$$Lambda$MessagesController$VAJW75r5ALAtTw6hMiEJQY4ufUc(this, chat, j, i, i2));
                if (i2 != 0) {
                    getConnectionsManager().bindRequestToGuid(i, i2);
                }
            }
        }
    }

    public /* synthetic */ void lambda$loadFullChat$27$MessagesController(Chat chat, long j, int i, int i2, TLObject tLObject, TL_error tL_error) {
        long j2 = j;
        int i3 = i;
        TL_error tL_error2 = tL_error;
        if (tL_error2 == null) {
            TL_messages_chatFull tL_messages_chatFull = (TL_messages_chatFull) tLObject;
            getMessagesStorage().putUsersAndChats(tL_messages_chatFull.users, tL_messages_chatFull.chats, true, true);
            getMessagesStorage().updateChatInfo(tL_messages_chatFull.full_chat, false);
            if (ChatObject.isChannel(chat)) {
                ArrayList arrayList;
                Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j));
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    arrayList = new ArrayList();
                    TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                    tL_updateReadChannelInbox.channel_id = i3;
                    tL_updateReadChannelInbox.max_id = tL_messages_chatFull.full_chat.read_inbox_max_id;
                    arrayList.add(tL_updateReadChannelInbox);
                    processUpdateArray(arrayList, null, null, false, 0);
                }
                num = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_outbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    arrayList = new ArrayList();
                    TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_updateReadChannelOutbox();
                    tL_updateReadChannelOutbox.channel_id = i3;
                    tL_updateReadChannelOutbox.max_id = tL_messages_chatFull.full_chat.read_outbox_max_id;
                    arrayList.add(tL_updateReadChannelOutbox);
                    processUpdateArray(arrayList, null, null, false, 0);
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ABRsa8DnNQCAwWkneP-WMpsWO-4(this, i3, tL_messages_chatFull, i2));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$WBTQ_ej5fbp9gom40OUrolrTguI(this, tL_error2, i3));
    }

    public /* synthetic */ void lambda$null$25$MessagesController(int i, TL_messages_chatFull tL_messages_chatFull, int i2) {
        this.fullChats.put(i, tL_messages_chatFull.full_chat);
        applyDialogNotificationsSettings((long) (-i), tL_messages_chatFull.full_chat.notify_settings);
        for (int i3 = 0; i3 < tL_messages_chatFull.full_chat.bot_info.size(); i3++) {
            getMediaDataController().putBotInfo((BotInfo) tL_messages_chatFull.full_chat.bot_info.get(i3));
        }
        this.exportedChats.put(i, tL_messages_chatFull.full_chat.exported_invite);
        this.loadingFullChats.remove(Integer.valueOf(i));
        this.loadedFullChats.add(Integer.valueOf(i));
        putUsers(tL_messages_chatFull.users, false);
        putChats(tL_messages_chatFull.chats, false);
        if (tL_messages_chatFull.full_chat.stickerset != null) {
            getMediaDataController().getGroupStickerSetById(tL_messages_chatFull.full_chat.stickerset);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, tL_messages_chatFull.full_chat, Integer.valueOf(i2), Boolean.valueOf(false), null);
    }

    public /* synthetic */ void lambda$null$26$MessagesController(TL_error tL_error, int i) {
        checkChannelError(tL_error.text, i);
        this.loadingFullChats.remove(Integer.valueOf(i));
    }

    public void loadFullUser(User user, int i, boolean z) {
        if (user != null && !this.loadingFullUsers.contains(Integer.valueOf(user.id))) {
            if (z || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                TL_users_getFullUser tL_users_getFullUser = new TL_users_getFullUser();
                tL_users_getFullUser.id = getInputUser(user);
                long j = (long) user.id;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(j)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j)) == null) {
                    reloadDialogsReadValue(null, j);
                }
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_users_getFullUser, new -$$Lambda$MessagesController$2i4iPd1YGcMIIO-bLCdZdnM4Fhk(this, user, i)), i);
            }
        }
    }

    public /* synthetic */ void lambda$loadFullUser$30$MessagesController(User user, int i, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            UserFull userFull = (UserFull) tLObject;
            getMessagesStorage().updateUserInfo(userFull, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$KYmIFLqqnbuHB9tjSOJfEUlZ30I(this, userFull, user, i));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$dcKUVSay9iaLE1j3BoK49R5HyyY(this, user));
    }

    public /* synthetic */ void lambda$null$28$MessagesController(UserFull userFull, User user, int i) {
        savePeerSettings((long) userFull.user.id, userFull.settings, false);
        applyDialogNotificationsSettings((long) user.id, userFull.notify_settings);
        if (userFull.bot_info instanceof TL_botInfo) {
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(user.first_name);
        stringBuilder.append(user.last_name);
        stringBuilder.append(user.username);
        String stringBuilder2 = stringBuilder.toString();
        ArrayList arrayList = new ArrayList();
        arrayList.add(userFull.user);
        putUsers(arrayList, false);
        getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
        if (stringBuilder2 != null) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(userFull.user.first_name);
            stringBuilder3.append(userFull.user.last_name);
            stringBuilder3.append(userFull.user.username);
            if (!stringBuilder2.equals(stringBuilder3.toString())) {
                getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
            }
        }
        if (userFull.bot_info instanceof TL_botInfo) {
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, userFull.bot_info, Integer.valueOf(i));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.id), userFull, null);
    }

    public /* synthetic */ void lambda$null$29$MessagesController(User user) {
        this.loadingFullUsers.remove(Integer.valueOf(user.id));
    }

    private void reloadMessages(ArrayList<Integer> arrayList, long j, boolean z) {
        if (!arrayList.isEmpty()) {
            TLObject tL_channels_getMessages;
            ArrayList arrayList2 = new ArrayList();
            Chat chatByDialog = ChatObject.getChatByDialog(j, this.currentAccount);
            if (ChatObject.isChannel(chatByDialog)) {
                tL_channels_getMessages = new TL_channels_getMessages();
                tL_channels_getMessages.channel = getInputChannel(chatByDialog);
                tL_channels_getMessages.id = arrayList2;
            } else {
                tL_channels_getMessages = new TL_messages_getMessages();
                tL_channels_getMessages.id = arrayList2;
            }
            ArrayList arrayList3 = (ArrayList) this.reloadingMessages.get(j);
            for (int i = 0; i < arrayList.size(); i++) {
                Integer num = (Integer) arrayList.get(i);
                if (arrayList3 == null || !arrayList3.contains(num)) {
                    arrayList2.add(num);
                }
            }
            if (!arrayList2.isEmpty()) {
                if (arrayList3 == null) {
                    arrayList3 = new ArrayList();
                    this.reloadingMessages.put(j, arrayList3);
                }
                arrayList3.addAll(arrayList2);
                getConnectionsManager().sendRequest(tL_channels_getMessages, new -$$Lambda$MessagesController$Liv50krJ6cv1Bd4MsaAD3ZIs3gY(this, j, chatByDialog, z, arrayList2));
            }
        }
    }

    public /* synthetic */ void lambda$reloadMessages$32$MessagesController(long j, Chat chat, boolean z, ArrayList arrayList, TLObject tLObject, TL_error tL_error) {
        long j2 = j;
        Chat chat2 = chat;
        if (tL_error == null) {
            ArrayList arrayList2;
            messages_Messages messages_messages = (messages_Messages) tLObject;
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < messages_messages.users.size(); i++) {
                User user = (User) messages_messages.users.get(i);
                sparseArray.put(user.id, user);
            }
            SparseArray sparseArray2 = new SparseArray();
            for (int i2 = 0; i2 < messages_messages.chats.size(); i2++) {
                Chat chat3 = (Chat) messages_messages.chats.get(i2);
                sparseArray2.put(chat3.id, chat3);
            }
            Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(j));
            if (num == null) {
                num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j2));
                this.dialogs_read_inbox_max.put(Long.valueOf(j), num);
            }
            Integer num2 = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num2 == null) {
                num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j2));
                this.dialogs_read_outbox_max.put(Long.valueOf(j), num2);
            }
            Integer num3 = num2;
            ArrayList arrayList3 = new ArrayList();
            int i3 = 0;
            while (i3 < messages_messages.messages.size()) {
                Message message = (Message) messages_messages.messages.get(i3);
                if (chat2 != null && chat2.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                message.dialog_id = j2;
                if (!z) {
                    message.unread = (message.out ? num3 : num).intValue() < message.id;
                }
                MessageObject messageObject = r9;
                int i4 = i3;
                arrayList2 = arrayList3;
                MessageObject messageObject2 = new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, true);
                arrayList2.add(messageObject);
                i3 = i4 + 1;
                arrayList3 = arrayList2;
            }
            arrayList2 = arrayList3;
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            getMessagesStorage().putMessages(messages_messages, j, -1, 0, false, z);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$DAqP0fbTQsk8M0mbJPhFbsD_WMI(this, j, arrayList, arrayList2));
        }
    }

    public /* synthetic */ void lambda$null$31$MessagesController(long j, ArrayList arrayList, ArrayList arrayList2) {
        ArrayList arrayList3 = (ArrayList) this.reloadingMessages.get(j);
        if (arrayList3 != null) {
            arrayList3.removeAll(arrayList);
            if (arrayList3.isEmpty()) {
                this.reloadingMessages.remove(j);
            }
        }
        MessageObject messageObject = (MessageObject) this.dialogMessage.get(j);
        if (messageObject != null) {
            int i = 0;
            while (i < arrayList2.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList2.get(i);
                if (messageObject == null || messageObject.getId() != messageObject2.getId()) {
                    i++;
                } else {
                    this.dialogMessage.put(j, messageObject2);
                    if (messageObject2.messageOwner.to_id.channel_id == 0) {
                        messageObject = (MessageObject) this.dialogMessagesByIds.get(messageObject2.getId());
                        this.dialogMessagesByIds.remove(messageObject2.getId());
                        if (messageObject != null) {
                            this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        }
                    }
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList2);
    }

    public void hidePeerSettingsBar(long j, User user, Chat chat) {
        if (user != null || chat != null) {
            Editor edit = this.notificationsPreferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("dialog_bar_vis3");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), 3);
            edit.commit();
            if (((int) j) != 0) {
                TL_messages_hidePeerSettingsBar tL_messages_hidePeerSettingsBar = new TL_messages_hidePeerSettingsBar();
                if (user != null) {
                    tL_messages_hidePeerSettingsBar.peer = getInputPeer(user.id);
                } else if (chat != null) {
                    tL_messages_hidePeerSettingsBar.peer = getInputPeer(-chat.id);
                }
                getConnectionsManager().sendRequest(tL_messages_hidePeerSettingsBar, -$$Lambda$MessagesController$jQptSvsMXJJ6kN0kNQTGJMVx9hQ.INSTANCE);
            }
        }
    }

    public void reportSpam(long j, User user, Chat chat, EncryptedChat encryptedChat, boolean z) {
        if (user != null || chat != null || encryptedChat != null) {
            Editor edit = this.notificationsPreferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("dialog_bar_vis3");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), 3);
            edit.commit();
            if (((int) j) != 0) {
                TL_account_reportPeer tL_account_reportPeer = new TL_account_reportPeer();
                if (chat != null) {
                    tL_account_reportPeer.peer = getInputPeer(-chat.id);
                } else if (user != null) {
                    tL_account_reportPeer.peer = getInputPeer(user.id);
                }
                if (z) {
                    tL_account_reportPeer.reason = new TL_inputReportReasonGeoIrrelevant();
                } else {
                    tL_account_reportPeer.reason = new TL_inputReportReasonSpam();
                }
                getConnectionsManager().sendRequest(tL_account_reportPeer, -$$Lambda$MessagesController$r40aIljP9XXEZHDSJKpMdZBFBYA.INSTANCE, 2);
            } else if (encryptedChat != null && encryptedChat.access_hash != 0) {
                TL_messages_reportEncryptedSpam tL_messages_reportEncryptedSpam = new TL_messages_reportEncryptedSpam();
                tL_messages_reportEncryptedSpam.peer = new TL_inputEncryptedChat();
                TL_inputEncryptedChat tL_inputEncryptedChat = tL_messages_reportEncryptedSpam.peer;
                tL_inputEncryptedChat.chat_id = encryptedChat.id;
                tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
                getConnectionsManager().sendRequest(tL_messages_reportEncryptedSpam, -$$Lambda$MessagesController$Hs86dTNrkWNjJ_kpoVItJmnx3wk.INSTANCE, 2);
            }
        }
    }

    private void savePeerSettings(long j, TL_peerSettings tL_peerSettings, boolean z) {
        if (tL_peerSettings != null) {
            SharedPreferences sharedPreferences = this.notificationsPreferences;
            StringBuilder stringBuilder = new StringBuilder();
            String str = "dialog_bar_vis3";
            stringBuilder.append(str);
            stringBuilder.append(j);
            if (sharedPreferences.getInt(stringBuilder.toString(), 0) != 3) {
                StringBuilder stringBuilder2;
                Editor edit = this.notificationsPreferences.edit();
                boolean z2 = (tL_peerSettings.report_spam || tL_peerSettings.add_contact || tL_peerSettings.block_contact || tL_peerSettings.share_contact || tL_peerSettings.report_geo) ? false : true;
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("peer settings loaded for ");
                    stringBuilder2.append(j);
                    stringBuilder2.append(" add = ");
                    stringBuilder2.append(tL_peerSettings.add_contact);
                    stringBuilder2.append(" block = ");
                    stringBuilder2.append(tL_peerSettings.block_contact);
                    stringBuilder2.append(" spam = ");
                    stringBuilder2.append(tL_peerSettings.report_spam);
                    stringBuilder2.append(" share = ");
                    stringBuilder2.append(tL_peerSettings.share_contact);
                    stringBuilder2.append(" geo = ");
                    stringBuilder2.append(tL_peerSettings.report_geo);
                    stringBuilder2.append(" hide = ");
                    stringBuilder2.append(z2);
                    FileLog.d(stringBuilder2.toString());
                }
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(j);
                edit.putInt(stringBuilder2.toString(), z2 ? 1 : 2);
                stringBuilder = new StringBuilder();
                stringBuilder.append("dialog_bar_share");
                stringBuilder.append(j);
                edit.putBoolean(stringBuilder.toString(), tL_peerSettings.share_contact);
                stringBuilder = new StringBuilder();
                stringBuilder.append("dialog_bar_report");
                stringBuilder.append(j);
                edit.putBoolean(stringBuilder.toString(), tL_peerSettings.report_spam);
                stringBuilder = new StringBuilder();
                stringBuilder.append("dialog_bar_add");
                stringBuilder.append(j);
                edit.putBoolean(stringBuilder.toString(), tL_peerSettings.add_contact);
                stringBuilder = new StringBuilder();
                stringBuilder.append("dialog_bar_block");
                stringBuilder.append(j);
                edit.putBoolean(stringBuilder.toString(), tL_peerSettings.block_contact);
                stringBuilder = new StringBuilder();
                stringBuilder.append("dialog_bar_exception");
                stringBuilder.append(j);
                edit.putBoolean(stringBuilder.toString(), tL_peerSettings.need_contacts_exception);
                stringBuilder = new StringBuilder();
                stringBuilder.append("dialog_bar_location");
                stringBuilder.append(j);
                edit.putBoolean(stringBuilder.toString(), tL_peerSettings.report_geo);
                edit.commit();
                getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(j));
            }
        }
    }

    public void loadPeerSettings(User user, Chat chat) {
        if (user != null || chat != null) {
            int i;
            if (user != null) {
                i = user.id;
            } else {
                i = -chat.id;
            }
            long j = (long) i;
            if (this.loadingPeerSettings.indexOfKey(j) < 0) {
                this.loadingPeerSettings.put(j, Boolean.valueOf(true));
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("request spam button for ");
                    stringBuilder.append(j);
                    FileLog.d(stringBuilder.toString());
                }
                SharedPreferences sharedPreferences = this.notificationsPreferences;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("dialog_bar_vis3");
                stringBuilder2.append(j);
                int i2 = sharedPreferences.getInt(stringBuilder2.toString(), 0);
                if (i2 == 1 || i2 == 3) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("dialog bar already hidden for ");
                        stringBuilder3.append(j);
                        FileLog.d(stringBuilder3.toString());
                    }
                    return;
                }
                TL_messages_getPeerSettings tL_messages_getPeerSettings = new TL_messages_getPeerSettings();
                if (user != null) {
                    tL_messages_getPeerSettings.peer = getInputPeer(user.id);
                } else if (chat != null) {
                    tL_messages_getPeerSettings.peer = getInputPeer(-chat.id);
                }
                getConnectionsManager().sendRequest(tL_messages_getPeerSettings, new -$$Lambda$MessagesController$5J6EJB4k7zkbaD5McoOncn6oROo(this, j));
            }
        }
    }

    public /* synthetic */ void lambda$loadPeerSettings$37$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$0BQBNes51Sit8-3hY7P1z9KygXI(this, j, tLObject));
    }

    public /* synthetic */ void lambda$null$36$MessagesController(long j, TLObject tLObject) {
        this.loadingPeerSettings.remove(j);
        if (tLObject != null) {
            savePeerSettings(j, (TL_peerSettings) tLObject, false);
        }
    }

    /* Access modifiers changed, original: protected */
    public void processNewChannelDifferenceParams(int i, int i2, int i3) {
        StringBuilder stringBuilder;
        String str = " pts_count = ";
        if (BuildVars.LOGS_ENABLED) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("processNewChannelDifferenceParams pts = ");
            stringBuilder.append(i);
            stringBuilder.append(str);
            stringBuilder.append(i2);
            stringBuilder.append(" channeldId = ");
            stringBuilder.append(i3);
            FileLog.d(stringBuilder.toString());
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
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ADD CHANNEL UPDATE TO QUEUE pts = ");
                    stringBuilder.append(i);
                    stringBuilder.append(str);
                    stringBuilder.append(i2);
                    FileLog.d(stringBuilder.toString());
                }
                if (j == 0) {
                    this.updatesStartWaitTimeChannels.put(i3, System.currentTimeMillis());
                }
                UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts(this, null);
                userActionUpdatesPts.pts = i;
                userActionUpdatesPts.pts_count = i2;
                userActionUpdatesPts.chat_id = i3;
                ArrayList arrayList = (ArrayList) this.updatesQueueChannels.get(i3);
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

    /* Access modifiers changed, original: protected */
    public void processNewDifferenceParams(int i, int i2, int i3, int i4) {
        String str = " pts_count = ";
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processNewDifferenceParams seq = ");
            stringBuilder.append(i);
            stringBuilder.append(" pts = ");
            stringBuilder.append(i2);
            stringBuilder.append(" date = ");
            stringBuilder.append(i3);
            stringBuilder.append(str);
            stringBuilder.append(i4);
            FileLog.d(stringBuilder.toString());
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
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("ADD UPDATE TO QUEUE pts = ");
                        stringBuilder2.append(i2);
                        stringBuilder2.append(str);
                        stringBuilder2.append(i4);
                        FileLog.d(stringBuilder2.toString());
                    }
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts(this, null);
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
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("ADD UPDATE TO QUEUE seq = ");
                    stringBuilder3.append(i);
                    FileLog.d(stringBuilder3.toString());
                }
                if (this.updatesStartWaitTimeSeq == 0) {
                    this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                UserActionUpdatesSeq userActionUpdatesSeq = new UserActionUpdatesSeq(this, null);
                userActionUpdatesSeq.seq = i;
                this.updatesQueueSeq.add(userActionUpdatesSeq);
                return;
            }
            getDifference();
        }
    }

    public void didAddedNewTask(int i, SparseArray<ArrayList<Long>> sparseArray) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$rDPlOFul736cgWHrU3EcJ8H3ENs(this, i));
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$LqVXXvpbSugUO7Ph2v9tZNwc3FU(this, sparseArray));
    }

    public /* synthetic */ void lambda$didAddedNewTask$38$MessagesController(int i) {
        if (this.currentDeletingTaskMids != null || this.gettingNewDeleteTask) {
            int i2 = this.currentDeletingTaskTime;
            if (i2 == 0 || i >= i2) {
                return;
            }
        }
        getNewDeleteTask(null, 0);
    }

    public /* synthetic */ void lambda$didAddedNewTask$39$MessagesController(SparseArray sparseArray) {
        getNotificationCenter().postNotificationName(NotificationCenter.didCreatedNewDeleteTask, sparseArray);
    }

    public void getNewDeleteTask(ArrayList<Integer> arrayList, int i) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$JPhrxTkqvT-dumvdhTetwujtwUY(this, arrayList, i));
    }

    public /* synthetic */ void lambda$getNewDeleteTask$40$MessagesController(ArrayList arrayList, int i) {
        this.gettingNewDeleteTask = true;
        getMessagesStorage().getNewTask(arrayList, i);
    }

    /* JADX WARNING: Missing block: B:5:0x0013, code skipped:
            if (r1 <= r0) goto L_0x0015;
     */
    private boolean checkDeletingTask(boolean r4) {
        /*
        r3 = this;
        r0 = r3.getConnectionsManager();
        r0 = r0.getCurrentTime();
        r1 = r3.currentDeletingTaskMids;
        r2 = 0;
        if (r1 == 0) goto L_0x0038;
    L_0x000d:
        if (r4 != 0) goto L_0x0015;
    L_0x000f:
        r1 = r3.currentDeletingTaskTime;
        if (r1 == 0) goto L_0x0038;
    L_0x0013:
        if (r1 > r0) goto L_0x0038;
    L_0x0015:
        r3.currentDeletingTaskTime = r2;
        r0 = r3.currentDeleteTaskRunnable;
        if (r0 == 0) goto L_0x0024;
    L_0x001b:
        if (r4 != 0) goto L_0x0024;
    L_0x001d:
        r4 = org.telegram.messenger.Utilities.stageQueue;
        r0 = r3.currentDeleteTaskRunnable;
        r4.cancelRunnable(r0);
    L_0x0024:
        r4 = 0;
        r3.currentDeleteTaskRunnable = r4;
        r4 = new java.util.ArrayList;
        r0 = r3.currentDeletingTaskMids;
        r4.<init>(r0);
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$yNKgQYZS7kBzxW3X5NgtHBuiANE;
        r0.<init>(r3, r4);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r4 = 1;
        return r4;
    L_0x0038:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.checkDeletingTask(boolean):boolean");
    }

    public /* synthetic */ void lambda$checkDeletingTask$42$MessagesController(ArrayList arrayList) {
        if (arrayList.isEmpty() || ((Integer) arrayList.get(0)).intValue() <= 0) {
            deleteMessages(arrayList, null, null, 0, 0, false, false);
        } else {
            getMessagesStorage().emptyMessagesMedia(arrayList);
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$uRkAhIPPshv899Zd6GSUcYwa4eM(this, arrayList));
    }

    public /* synthetic */ void lambda$null$41$MessagesController(ArrayList arrayList) {
        getNewDeleteTask(arrayList, this.currentDeletingTaskChannelId);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
    }

    public void processLoadedDeleteTask(int i, ArrayList<Integer> arrayList, int i2) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$stqvu1S1fXbEKuIXuRf_-ubdb58(this, arrayList, i));
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
                this.currentDeleteTaskRunnable = new -$$Lambda$MessagesController$HjsbS_MwTY7mBvfolw2AT1fSPhY(this);
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
            User user = getUser(Integer.valueOf(i));
            if (user != null) {
                TL_photos_getUserPhotos tL_photos_getUserPhotos = new TL_photos_getUserPhotos();
                tL_photos_getUserPhotos.limit = i2;
                tL_photos_getUserPhotos.offset = 0;
                tL_photos_getUserPhotos.max_id = (long) ((int) j);
                tL_photos_getUserPhotos.user_id = getInputUser(user);
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_photos_getUserPhotos, new -$$Lambda$MessagesController$HQYOt4JAoRbBIe6IMuTPRMWGURg(this, i, i2, j, i3)), i3);
            }
        } else if (i < 0) {
            TL_messages_search tL_messages_search = new TL_messages_search();
            tL_messages_search.filter = new TL_inputMessagesFilterChatPhotos();
            tL_messages_search.limit = i2;
            tL_messages_search.offset_id = (int) j;
            tL_messages_search.q = "";
            tL_messages_search.peer = getInputPeer(i);
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_search, new -$$Lambda$MessagesController$GKzfmccLGgKGrjKvVIX1pQo7rwE(this, i, i2, j, i3)), i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$45$MessagesController(int i, int i2, long j, int i3, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processLoadedUserPhotos((photos_Photos) tLObject, i, i2, j, false, i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$46$MessagesController(int i, int i2, long j, int i3, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            TL_photos_photos tL_photos_photos = new TL_photos_photos();
            tL_photos_photos.count = messages_messages.count;
            tL_photos_photos.users.addAll(messages_messages.users);
            for (int i4 = 0; i4 < messages_messages.messages.size(); i4++) {
                MessageAction messageAction = ((Message) messages_messages.messages.get(i4)).action;
                if (messageAction != null) {
                    Photo photo = messageAction.photo;
                    if (photo != null) {
                        tL_photos_photos.photos.add(photo);
                    }
                }
            }
            processLoadedUserPhotos(tL_photos_photos, i, i2, j, false, i3);
        }
    }

    public void blockUser(int i) {
        User user = getUser(Integer.valueOf(i));
        if (user != null && this.blockedUsers.indexOfKey(i) < 0) {
            this.blockedUsers.put(i, 1);
            if (user.bot) {
                getMediaDataController().removeInline(i);
            } else {
                getMediaDataController().removePeer(i);
            }
            i = this.totalBlockedCount;
            if (i >= 0) {
                this.totalBlockedCount = i + 1;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            TL_contacts_block tL_contacts_block = new TL_contacts_block();
            tL_contacts_block.id = getInputUser(user);
            getConnectionsManager().sendRequest(tL_contacts_block, -$$Lambda$MessagesController$9pXuu16zaRwP_GF6IvLmyU9Fgvg.INSTANCE);
        }
    }

    public void setUserBannedRole(int i, User user, TL_chatBannedRights tL_chatBannedRights, boolean z, BaseFragment baseFragment) {
        if (user != null && tL_chatBannedRights != null) {
            TL_channels_editBanned tL_channels_editBanned = new TL_channels_editBanned();
            tL_channels_editBanned.channel = getInputChannel(i);
            tL_channels_editBanned.user_id = getInputUser(user);
            tL_channels_editBanned.banned_rights = tL_chatBannedRights;
            getConnectionsManager().sendRequest(tL_channels_editBanned, new -$$Lambda$MessagesController$P9LuMlpKJpHH9MCTqMuosHCNo_g(this, i, baseFragment, tL_channels_editBanned, z));
        }
    }

    public /* synthetic */ void lambda$setUserBannedRole$50$MessagesController(int i, BaseFragment baseFragment, TL_channels_editBanned tL_channels_editBanned, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$XBt3Ae4ZO2XlKTTYJer2qtJBISg(this, i), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$O177BLBUk2fJYj890YZZFEgohAY(this, tL_error, baseFragment, tL_channels_editBanned, z));
    }

    public /* synthetic */ void lambda$null$48$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$49$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_editBanned tL_channels_editBanned, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editBanned, Boolean.valueOf(z));
    }

    public void setChannelSlowMode(int i, int i2) {
        TL_channels_toggleSlowMode tL_channels_toggleSlowMode = new TL_channels_toggleSlowMode();
        tL_channels_toggleSlowMode.seconds = i2;
        tL_channels_toggleSlowMode.channel = getInputChannel(i);
        getConnectionsManager().sendRequest(tL_channels_toggleSlowMode, new -$$Lambda$MessagesController$b1aEUJ9dQgncdXnK6aZPcAw4ri0(this, i));
    }

    public /* synthetic */ void lambda$setChannelSlowMode$52$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$RV-OSs9ZtoWl3tQ48T32zEsqDqE(this, i), 1000);
        }
    }

    public /* synthetic */ void lambda$null$51$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public void setDefaultBannedRole(int i, TL_chatBannedRights tL_chatBannedRights, boolean z, BaseFragment baseFragment) {
        if (tL_chatBannedRights != null) {
            TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights = new TL_messages_editChatDefaultBannedRights();
            tL_messages_editChatDefaultBannedRights.peer = getInputPeer(-i);
            tL_messages_editChatDefaultBannedRights.banned_rights = tL_chatBannedRights;
            getConnectionsManager().sendRequest(tL_messages_editChatDefaultBannedRights, new -$$Lambda$MessagesController$ytZHjxdxRqbqeZw6nl8SbtDJhNk(this, i, baseFragment, tL_messages_editChatDefaultBannedRights, z));
        }
    }

    public /* synthetic */ void lambda$setDefaultBannedRole$55$MessagesController(int i, BaseFragment baseFragment, TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$_eAgYyWqMBDk5AbyJ_v9gQwJL58(this, i), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ALVN1C_IGrnDgmYidx1PlwXFPQk(this, tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, z));
    }

    public /* synthetic */ void lambda$null$53$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$54$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, Boolean.valueOf(z));
    }

    public void setUserAdminRole(int i, User user, TL_chatAdminRights tL_chatAdminRights, String str, boolean z, BaseFragment baseFragment, boolean z2) {
        if (user != null && tL_chatAdminRights != null) {
            Chat chat = getChat(Integer.valueOf(i));
            if (ChatObject.isChannel(chat)) {
                TL_channels_editAdmin tL_channels_editAdmin = new TL_channels_editAdmin();
                tL_channels_editAdmin.channel = getInputChannel(chat);
                tL_channels_editAdmin.user_id = getInputUser(user);
                tL_channels_editAdmin.admin_rights = tL_chatAdminRights;
                tL_channels_editAdmin.rank = str;
                getConnectionsManager().sendRequest(tL_channels_editAdmin, new -$$Lambda$MessagesController$XSUUDIZziZiii8wBxUgRt4o_Qoo(this, i, baseFragment, tL_channels_editAdmin, z));
                return;
            }
            TL_messages_editChatAdmin tL_messages_editChatAdmin = new TL_messages_editChatAdmin();
            tL_messages_editChatAdmin.chat_id = i;
            tL_messages_editChatAdmin.user_id = getInputUser(user);
            boolean z3 = tL_chatAdminRights.change_info || tL_chatAdminRights.delete_messages || tL_chatAdminRights.ban_users || tL_chatAdminRights.invite_users || tL_chatAdminRights.pin_messages || tL_chatAdminRights.add_admins;
            tL_messages_editChatAdmin.is_admin = z3;
            -$$Lambda$MessagesController$b56InizIt0I4FbnoQmxOOGkS67M -__lambda_messagescontroller_b56inizit0i4fbnoqmxoogks67m = new -$$Lambda$MessagesController$b56InizIt0I4FbnoQmxOOGkS67M(this, i, baseFragment, tL_messages_editChatAdmin);
            if (tL_messages_editChatAdmin.is_admin && z2) {
                addUserToChat(i, user, null, 0, null, baseFragment, new -$$Lambda$MessagesController$QpTjLyJzhiQg0agugmjkvLNMICM(this, tL_messages_editChatAdmin, -__lambda_messagescontroller_b56inizit0i4fbnoqmxoogks67m));
            } else {
                getConnectionsManager().sendRequest(tL_messages_editChatAdmin, -__lambda_messagescontroller_b56inizit0i4fbnoqmxoogks67m);
            }
        }
    }

    public /* synthetic */ void lambda$setUserAdminRole$58$MessagesController(int i, BaseFragment baseFragment, TL_channels_editAdmin tL_channels_editAdmin, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$sxKTucilmWBYA6h1cHSpp3oPkb4(this, i), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$fjR0QhAOQGvuKzDDS7P5rRQqKo0(this, tL_error, baseFragment, tL_channels_editAdmin, z));
    }

    public /* synthetic */ void lambda$null$56$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$57$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_editAdmin tL_channels_editAdmin, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editAdmin, Boolean.valueOf(z));
    }

    public /* synthetic */ void lambda$null$59$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$setUserAdminRole$61$MessagesController(int i, BaseFragment baseFragment, TL_messages_editChatAdmin tL_messages_editChatAdmin, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$TWMoKIScflQBW8GbKkFOvILprZI(this, i), 1000);
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$t9gEk3oQoNc0K7D3N4XDrhrJv9w(this, tL_error, baseFragment, tL_messages_editChatAdmin));
        }
    }

    public /* synthetic */ void lambda$null$60$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_messages_editChatAdmin tL_messages_editChatAdmin) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatAdmin, Boolean.valueOf(false));
    }

    public /* synthetic */ void lambda$setUserAdminRole$62$MessagesController(TL_messages_editChatAdmin tL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(tL_messages_editChatAdmin, requestDelegate);
    }

    public void unblockUser(int i) {
        TL_contacts_unblock tL_contacts_unblock = new TL_contacts_unblock();
        User user = getUser(Integer.valueOf(i));
        if (user != null) {
            this.totalBlockedCount--;
            this.blockedUsers.delete(user.id);
            tL_contacts_unblock.id = getInputUser(user);
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            getConnectionsManager().sendRequest(tL_contacts_unblock, -$$Lambda$MessagesController$hZMObZRDUapS7Alox_T46Sd5rlU.INSTANCE);
        }
    }

    public void getBlockedUsers(boolean z) {
        if (getUserConfig().isClientActivated() && !this.loadingBlockedUsers) {
            int i;
            this.loadingBlockedUsers = true;
            TL_contacts_getBlocked tL_contacts_getBlocked = new TL_contacts_getBlocked();
            if (z) {
                i = 0;
            } else {
                i = this.blockedUsers.size();
            }
            tL_contacts_getBlocked.offset = i;
            tL_contacts_getBlocked.limit = z ? 20 : 100;
            getConnectionsManager().sendRequest(tL_contacts_getBlocked, new -$$Lambda$MessagesController$SOAsRLaE9mGeyi3JBQkLsnPO1nw(this, z, tL_contacts_getBlocked));
        }
    }

    public /* synthetic */ void lambda$getBlockedUsers$65$MessagesController(boolean z, TL_contacts_getBlocked tL_contacts_getBlocked, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$P7nV03ZbMlhqhHwEi6tP_PYFeCs(this, tLObject, z, tL_contacts_getBlocked));
    }

    public /* synthetic */ void lambda$null$64$MessagesController(TLObject tLObject, boolean z, TL_contacts_getBlocked tL_contacts_getBlocked) {
        if (tLObject != null) {
            contacts_Blocked contacts_blocked = (contacts_Blocked) tLObject;
            putUsers(contacts_blocked.users, false);
            getMessagesStorage().putUsersAndChats(contacts_blocked.users, null, true, true);
            if (z) {
                this.blockedUsers.clear();
            }
            this.totalBlockedCount = Math.max(contacts_blocked.count, contacts_blocked.blocked.size());
            this.blockedEndReached = contacts_blocked.blocked.size() < tL_contacts_getBlocked.limit;
            int size = contacts_blocked.blocked.size();
            for (int i = 0; i < size; i++) {
                this.blockedUsers.put(((TL_contactBlocked) contacts_blocked.blocked.get(i)).user_id, 1);
            }
            this.loadingBlockedUsers = false;
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
    }

    public void deleteUserPhoto(InputPhoto inputPhoto) {
        if (inputPhoto == null) {
            TL_photos_updateProfilePhoto tL_photos_updateProfilePhoto = new TL_photos_updateProfilePhoto();
            tL_photos_updateProfilePhoto.id = new TL_inputPhotoEmpty();
            getUserConfig().getCurrentUser().photo = new TL_userProfilePhotoEmpty();
            User user = getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
            }
            if (user != null) {
                user.photo = getUserConfig().getCurrentUser().photo;
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
                getConnectionsManager().sendRequest(tL_photos_updateProfilePhoto, new -$$Lambda$MessagesController$YA4X0K4czesPTfsszO__hGCvB7k(this));
            } else {
                return;
            }
        }
        TL_photos_deletePhotos tL_photos_deletePhotos = new TL_photos_deletePhotos();
        tL_photos_deletePhotos.id.add(inputPhoto);
        getConnectionsManager().sendRequest(tL_photos_deletePhotos, -$$Lambda$MessagesController$sAwCUX3IhR8N0C-EEhE4RmQefts.INSTANCE);
    }

    public /* synthetic */ void lambda$deleteUserPhoto$67$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            User user = getUser(Integer.valueOf(getUserConfig().getClientUserId()));
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
                getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
                user.photo = (UserProfilePhoto) tLObject;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$BCrJrfONRffODKnUFP1pp81h_-s(this));
            }
        }
    }

    public /* synthetic */ void lambda$null$66$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
        getUserConfig().saveConfig(true);
    }

    public void processLoadedUserPhotos(photos_Photos photos_photos, int i, int i2, long j, boolean z, int i3) {
        if (!z) {
            getMessagesStorage().putUsersAndChats(photos_photos.users, null, true, true);
            getMessagesStorage().putDialogPhotos(i, photos_photos);
        } else if (photos_photos == null || photos_photos.photos.isEmpty()) {
            loadDialogPhotos(i, i2, j, false, i3);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$BmQ4KzU527mjcBaLf7ITSJtYsl4(this, photos_photos, z, i, i2, i3));
    }

    public /* synthetic */ void lambda$processLoadedUserPhotos$69$MessagesController(photos_Photos photos_photos, boolean z, int i, int i2, int i3) {
        putUsers(photos_photos.users, z);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z), Integer.valueOf(i3), photos_photos.photos);
    }

    public void uploadAndApplyUserAvatar(FileLocation fileLocation) {
        if (fileLocation != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(FileLoader.getDirectory(4));
            stringBuilder.append("/");
            stringBuilder.append(fileLocation.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(fileLocation.local_id);
            stringBuilder.append(".jpg");
            this.uploadingAvatar = stringBuilder.toString();
            getFileLoader().uploadFile(this.uploadingAvatar, false, true, 16777216);
        }
    }

    public void saveTheme(ThemeInfo themeInfo, ThemeAccent themeAccent, boolean z, boolean z2) {
        TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        if (tL_theme != null) {
            TL_account_saveTheme tL_account_saveTheme = new TL_account_saveTheme();
            TL_inputTheme tL_inputTheme = new TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            tL_account_saveTheme.theme = tL_inputTheme;
            tL_account_saveTheme.unsave = z2;
            getConnectionsManager().sendRequest(tL_account_saveTheme, -$$Lambda$MessagesController$WKM5a9edf5QSZ_85kx_7-hVixAo.INSTANCE);
            getConnectionsManager().resumeNetworkMaybe();
        }
        if (!z2) {
            installTheme(themeInfo, themeAccent, z);
        }
    }

    public void installTheme(ThemeInfo themeInfo, ThemeAccent themeAccent, boolean z) {
        TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        CharSequence charSequence = themeAccent != null ? themeAccent.patternSlug : themeInfo.slug;
        boolean z2 = themeAccent == null && themeInfo.isBlured;
        boolean z3 = themeAccent != null ? themeAccent.patternMotion : themeInfo.isMotion;
        TL_account_installTheme tL_account_installTheme = new TL_account_installTheme();
        tL_account_installTheme.dark = z;
        if (tL_theme != null) {
            tL_account_installTheme.format = "android";
            TL_inputTheme tL_inputTheme = new TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            tL_account_installTheme.theme = tL_inputTheme;
            tL_account_installTheme.flags |= 2;
        }
        getConnectionsManager().sendRequest(tL_account_installTheme, -$$Lambda$MessagesController$JU6Vn2XI6a6J78k6RJwedOdT0ek.INSTANCE);
        if (!TextUtils.isEmpty(charSequence)) {
            TL_account_installWallPaper tL_account_installWallPaper = new TL_account_installWallPaper();
            TL_inputWallPaperSlug tL_inputWallPaperSlug = new TL_inputWallPaperSlug();
            tL_inputWallPaperSlug.slug = charSequence;
            tL_account_installWallPaper.wallpaper = tL_inputWallPaperSlug;
            tL_account_installWallPaper.settings = new TL_wallPaperSettings();
            TL_wallPaperSettings tL_wallPaperSettings = tL_account_installWallPaper.settings;
            tL_wallPaperSettings.blur = z2;
            tL_wallPaperSettings.motion = z3;
            getConnectionsManager().sendRequest(tL_account_installWallPaper, -$$Lambda$MessagesController$cHxeHBiTL_l8pWJ-ucJD2ba8DkY.INSTANCE);
        }
    }

    public void saveThemeToServer(ThemeInfo themeInfo, ThemeAccent themeAccent) {
        if (themeInfo != null) {
            String absolutePath;
            File pathToWallpaper;
            if (themeAccent != null) {
                absolutePath = themeAccent.saveToFile().getAbsolutePath();
                pathToWallpaper = themeAccent.getPathToWallpaper();
            } else {
                absolutePath = themeInfo.pathToFile;
                pathToWallpaper = null;
            }
            String str = absolutePath;
            File file = pathToWallpaper;
            if (str != null && !this.uploadingThemes.containsKey(str)) {
                this.uploadingThemes.put(str, themeAccent != null ? themeAccent : themeInfo);
                Utilities.globalQueue.postRunnable(new -$$Lambda$MessagesController$0nGFkqAGg_edYOn-uWXSeNvNlMI(this, str, file, themeAccent, themeInfo));
            }
        }
    }

    public /* synthetic */ void lambda$saveThemeToServer$74$MessagesController(String str, File file, ThemeAccent themeAccent, ThemeInfo themeInfo) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$yecm_B_GjFIPDLJORsqyNDL2PH4(this, Theme.createThemePreviewImage(str, file != null ? file.getAbsolutePath() : null), str, themeAccent, themeInfo));
    }

    public /* synthetic */ void lambda$null$73$MessagesController(String str, String str2, ThemeAccent themeAccent, ThemeInfo themeInfo) {
        if (str == null) {
            this.uploadingThemes.remove(str2);
            return;
        }
        this.uploadingThemes.put(str, themeAccent != null ? themeAccent : themeInfo);
        if (themeAccent == null) {
            themeInfo.uploadingFile = str2;
            themeInfo.uploadingThumb = str;
        } else {
            themeAccent.uploadingFile = str2;
            themeAccent.uploadingThumb = str;
        }
        getFileLoader().uploadFile(str2, false, true, 67108864);
        getFileLoader().uploadFile(str, false, true, 16777216);
    }

    public void saveWallpaperToServer(File file, OverrideWallpaperInfo overrideWallpaperInfo, boolean z, long j) {
        Throwable e;
        if (this.uploadingWallpaper != null) {
            File file2 = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
            if (file == null || !(file.getAbsolutePath().equals(this.uploadingWallpaper) || file.equals(file2))) {
                getFileLoader().cancelUploadFile(this.uploadingWallpaper, false);
                this.uploadingWallpaper = null;
                this.uploadingWallpaperInfo = null;
            } else {
                this.uploadingWallpaperInfo = overrideWallpaperInfo;
                return;
            }
        }
        if (file != null) {
            this.uploadingWallpaper = file.getAbsolutePath();
            this.uploadingWallpaperInfo = overrideWallpaperInfo;
            getFileLoader().uploadFile(this.uploadingWallpaper, false, true, 16777216);
        } else if (!(overrideWallpaperInfo.isDefault() || overrideWallpaperInfo.isColor() || overrideWallpaperInfo.isTheme())) {
            TLObject tL_account_installWallPaper;
            TL_inputWallPaperSlug tL_inputWallPaperSlug = new TL_inputWallPaperSlug();
            tL_inputWallPaperSlug.slug = overrideWallpaperInfo.slug;
            TL_wallPaperSettings tL_wallPaperSettings = new TL_wallPaperSettings();
            tL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
            tL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
            int i = overrideWallpaperInfo.color;
            if (i != 0) {
                tL_wallPaperSettings.background_color = i;
                tL_wallPaperSettings.flags |= 1;
                tL_wallPaperSettings.intensity = (int) (overrideWallpaperInfo.intensity * 100.0f);
                tL_wallPaperSettings.flags |= 8;
            }
            i = overrideWallpaperInfo.gradientColor;
            if (i != 0) {
                tL_wallPaperSettings.second_background_color = i;
                tL_wallPaperSettings.rotation = AndroidUtilities.getWallpaperRotation(overrideWallpaperInfo.rotation, true);
                tL_wallPaperSettings.flags |= 16;
            }
            if (z) {
                tL_account_installWallPaper = new TL_account_installWallPaper();
                tL_account_installWallPaper.wallpaper = tL_inputWallPaperSlug;
                tL_account_installWallPaper.settings = tL_wallPaperSettings;
            } else {
                tL_account_installWallPaper = new TL_account_saveWallPaper();
                tL_account_installWallPaper.wallpaper = tL_inputWallPaperSlug;
                tL_account_installWallPaper.settings = tL_wallPaperSettings;
            }
            if (j == 0) {
                NativeByteBuffer nativeByteBuffer;
                try {
                    nativeByteBuffer = new NativeByteBuffer(1024);
                    try {
                        nativeByteBuffer.writeInt32(21);
                        nativeByteBuffer.writeBool(overrideWallpaperInfo.isBlurred);
                        nativeByteBuffer.writeBool(overrideWallpaperInfo.isMotion);
                        nativeByteBuffer.writeInt32(overrideWallpaperInfo.color);
                        nativeByteBuffer.writeInt32(overrideWallpaperInfo.gradientColor);
                        nativeByteBuffer.writeInt32(overrideWallpaperInfo.rotation);
                        nativeByteBuffer.writeDouble((double) overrideWallpaperInfo.intensity);
                        nativeByteBuffer.writeBool(z);
                        nativeByteBuffer.writeString(overrideWallpaperInfo.slug);
                        nativeByteBuffer.writeString(overrideWallpaperInfo.originalFileName);
                        nativeByteBuffer.limit(nativeByteBuffer.position());
                    } catch (Exception e2) {
                        e = e2;
                    }
                } catch (Exception e3) {
                    e = e3;
                    nativeByteBuffer = null;
                    FileLog.e(e);
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_account_installWallPaper, new -$$Lambda$MessagesController$Sq9QMUU7HOVWr8Dc5OGCzMKjLbk(this, j));
                }
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tL_account_installWallPaper, new -$$Lambda$MessagesController$Sq9QMUU7HOVWr8Dc5OGCzMKjLbk(this, j));
        }
    }

    public /* synthetic */ void lambda$saveWallpaperToServer$75$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        getMessagesStorage().removePendingTask(j);
    }

    public void markChannelDialogMessageAsDeleted(ArrayList<Integer> arrayList, int i) {
        MessageObject messageObject = (MessageObject) this.dialogMessage.get((long) (-i));
        if (messageObject != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (messageObject.getId() == ((Integer) arrayList.get(i2)).intValue()) {
                    messageObject.deleted = true;
                    return;
                }
            }
        }
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, EncryptedChat encryptedChat, long j, int i, boolean z, boolean z2) {
        deleteMessages(arrayList, arrayList2, encryptedChat, j, i, z, z2, 0, null);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, EncryptedChat encryptedChat, long j, int i, boolean z, boolean z2, long j2, TLObject tLObject) {
        TLObject tLObject2;
        long j3;
        Throwable e;
        ArrayList<Integer> arrayList3 = arrayList;
        ArrayList<Long> arrayList4 = arrayList2;
        EncryptedChat encryptedChat2 = encryptedChat;
        long j4 = j;
        int i2 = i;
        if ((arrayList3 != null && !arrayList.isEmpty()) || tLObject != null) {
            ArrayList arrayList5;
            if (j2 == 0) {
                int i3;
                arrayList5 = new ArrayList();
                for (i3 = 0; i3 < arrayList.size(); i3++) {
                    Integer num = (Integer) arrayList3.get(i3);
                    if (num.intValue() > 0) {
                        arrayList5.add(num);
                    }
                }
                int i4;
                if (z2) {
                    i4 = 1;
                    getMessagesStorage().markMessagesAsDeleted(arrayList, true, i, false, true);
                } else {
                    i4 = 1;
                    if (i2 == 0) {
                        for (i3 = 0; i3 < arrayList.size(); i3++) {
                            MessageObject messageObject = (MessageObject) this.dialogMessagesByIds.get(((Integer) arrayList3.get(i3)).intValue());
                            if (messageObject != null) {
                                messageObject.deleted = true;
                            }
                        }
                    } else {
                        markChannelDialogMessageAsDeleted(arrayList3, i2);
                    }
                    getMessagesStorage().markMessagesAsDeleted(arrayList, true, i, z, false);
                    getMessagesStorage().updateDialogsWithDeletedMessages(arrayList3, null, true, i2);
                }
                getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList3, Integer.valueOf(i), Boolean.valueOf(z2));
            } else {
                arrayList5 = null;
            }
            TLObject tL_messages_deleteScheduledMessages;
            NativeByteBuffer nativeByteBuffer;
            if (z2) {
                if (tLObject != null) {
                    tLObject2 = (TL_messages_deleteScheduledMessages) tLObject;
                    j3 = j2;
                } else {
                    tL_messages_deleteScheduledMessages = new TL_messages_deleteScheduledMessages();
                    tL_messages_deleteScheduledMessages.id = arrayList5;
                    tL_messages_deleteScheduledMessages.peer = getInputPeer((int) j4);
                    try {
                        nativeByteBuffer = new NativeByteBuffer(tL_messages_deleteScheduledMessages.getObjectSize() + 16);
                        try {
                            nativeByteBuffer.writeInt32(18);
                            nativeByteBuffer.writeInt64(j4);
                            nativeByteBuffer.writeInt32(i2);
                            tL_messages_deleteScheduledMessages.serializeToStream(nativeByteBuffer);
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j3 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        tLObject2 = tL_messages_deleteScheduledMessages;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject2, new -$$Lambda$MessagesController$EYdujax8VU8FpXM76vBpruFGavc(this, j3));
                    }
                    j3 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                    tLObject2 = tL_messages_deleteScheduledMessages;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject2, new -$$Lambda$MessagesController$EYdujax8VU8FpXM76vBpruFGavc(this, j3));
            } else if (i2 != 0) {
                if (tLObject != null) {
                    tLObject2 = (TL_channels_deleteMessages) tLObject;
                    j3 = j2;
                } else {
                    tL_messages_deleteScheduledMessages = new TL_channels_deleteMessages();
                    tL_messages_deleteScheduledMessages.id = arrayList5;
                    tL_messages_deleteScheduledMessages.channel = getInputChannel(i2);
                    try {
                        nativeByteBuffer = new NativeByteBuffer(tL_messages_deleteScheduledMessages.getObjectSize() + 8);
                        try {
                            nativeByteBuffer.writeInt32(7);
                            nativeByteBuffer.writeInt32(i2);
                            tL_messages_deleteScheduledMessages.serializeToStream(nativeByteBuffer);
                        } catch (Exception e4) {
                            e = e4;
                        }
                    } catch (Exception e5) {
                        e = e5;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        tLObject2 = tL_messages_deleteScheduledMessages;
                        getConnectionsManager().sendRequest(tLObject2, new -$$Lambda$MessagesController$i8bQqWBo9GKsIgfEDp3AEWvXbos(this, i2, j3));
                    }
                    j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    tLObject2 = tL_messages_deleteScheduledMessages;
                }
                getConnectionsManager().sendRequest(tLObject2, new -$$Lambda$MessagesController$i8bQqWBo9GKsIgfEDp3AEWvXbos(this, i2, j3));
            } else {
                if (!(arrayList4 == null || encryptedChat2 == null || arrayList2.isEmpty())) {
                    getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat2, arrayList4, null);
                }
                if (tLObject != null) {
                    tLObject2 = (TL_messages_deleteMessages) tLObject;
                    j3 = j2;
                } else {
                    tL_messages_deleteScheduledMessages = new TL_messages_deleteMessages();
                    tL_messages_deleteScheduledMessages.id = arrayList5;
                    tL_messages_deleteScheduledMessages.revoke = z;
                    try {
                        nativeByteBuffer = new NativeByteBuffer(tL_messages_deleteScheduledMessages.getObjectSize() + 8);
                        try {
                            nativeByteBuffer.writeInt32(7);
                            nativeByteBuffer.writeInt32(i2);
                            tL_messages_deleteScheduledMessages.serializeToStream(nativeByteBuffer);
                        } catch (Exception e6) {
                            e = e6;
                        }
                    } catch (Exception e7) {
                        e = e7;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        tLObject2 = tL_messages_deleteScheduledMessages;
                        getConnectionsManager().sendRequest(tLObject2, new -$$Lambda$MessagesController$t17BPz4oxlXxLdN0e1UxZ2Znji4(this, j3));
                    }
                    j3 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    tLObject2 = tL_messages_deleteScheduledMessages;
                }
                getConnectionsManager().sendRequest(tLObject2, new -$$Lambda$MessagesController$t17BPz4oxlXxLdN0e1UxZ2Znji4(this, j3));
            }
        }
    }

    public /* synthetic */ void lambda$deleteMessages$76$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$deleteMessages$77$MessagesController(int i, long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewChannelDifferenceParams(tL_messages_affectedMessages.pts, tL_messages_affectedMessages.pts_count, i);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$deleteMessages$78$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void pinMessage(Chat chat, User user, int i, boolean z) {
        if (chat != null || user != null) {
            TL_messages_updatePinnedMessage tL_messages_updatePinnedMessage = new TL_messages_updatePinnedMessage();
            tL_messages_updatePinnedMessage.peer = getInputPeer(chat != null ? -chat.id : user.id);
            tL_messages_updatePinnedMessage.id = i;
            tL_messages_updatePinnedMessage.silent = z ^ 1;
            getConnectionsManager().sendRequest(tL_messages_updatePinnedMessage, new -$$Lambda$MessagesController$SqK2B3MDGPnqui97ni4rzMahKnQ(this));
        }
    }

    public /* synthetic */ void lambda$pinMessage$79$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public void deleteUserChannelHistory(Chat chat, User user, int i) {
        if (i == 0) {
            getMessagesStorage().deleteUserChannelHistory(chat.id, user.id);
        }
        TL_channels_deleteUserHistory tL_channels_deleteUserHistory = new TL_channels_deleteUserHistory();
        tL_channels_deleteUserHistory.channel = getInputChannel(chat);
        tL_channels_deleteUserHistory.user_id = getInputUser(user);
        getConnectionsManager().sendRequest(tL_channels_deleteUserHistory, new -$$Lambda$MessagesController$ey4T_WMgGVJBQnCY-lXK6CrY6Gw(this, chat, user));
    }

    public /* synthetic */ void lambda$deleteUserChannelHistory$80$MessagesController(Chat chat, User user, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedHistory tL_messages_affectedHistory = (TL_messages_affectedHistory) tLObject;
            int i = tL_messages_affectedHistory.offset;
            if (i > 0) {
                deleteUserChannelHistory(chat, user, i);
            }
            processNewChannelDifferenceParams(tL_messages_affectedHistory.pts, tL_messages_affectedHistory.pts_count, chat.id);
        }
    }

    public ArrayList<Dialog> getAllDialogs() {
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

    public ArrayList<Dialog> getDialogs(int i) {
        ArrayList<Dialog> arrayList = (ArrayList) this.dialogsByFolder.get(i);
        return arrayList == null ? new ArrayList() : arrayList;
    }

    public void putAllNeededDraftDialogs() {
        LongSparseArray drafts = getMediaDataController().getDrafts();
        int size = drafts.size();
        for (int i = 0; i < size; i++) {
            putDraftDialogIfNeed(drafts.keyAt(i), (DraftMessage) drafts.valueAt(i));
        }
    }

    public void putDraftDialogIfNeed(long j, DraftMessage draftMessage) {
        if (this.dialogs_dict.indexOfKey(j) < 0) {
            MediaDataController mediaDataController = getMediaDataController();
            int size = this.allDialogs.size();
            int i = 1;
            if (size > 0) {
                Dialog dialog = (Dialog) this.allDialogs.get(size - 1);
                if (((long) draftMessage.date) < DialogObject.getLastMessageOrDraftDate(dialog, mediaDataController.getDraft(dialog.id))) {
                    return;
                }
            }
            TL_dialog tL_dialog = new TL_dialog();
            tL_dialog.id = j;
            tL_dialog.draft = draftMessage;
            tL_dialog.folder_id = mediaDataController.getDraftFolderId(j);
            if (j >= 0 || !ChatObject.isChannel(getChat(Integer.valueOf((int) (-j))))) {
                i = 0;
            }
            tL_dialog.flags = i;
            this.dialogs_dict.put(j, tL_dialog);
            this.allDialogs.add(tL_dialog);
            sortDialogs(null);
        }
    }

    public void removeDraftDialogIfNeed(long j) {
        Dialog dialog = (Dialog) this.dialogs_dict.get(j);
        if (dialog != null && dialog.top_message == 0) {
            this.dialogs_dict.remove(dialog.id);
            this.allDialogs.remove(dialog);
        }
    }

    private void removeDialog(Dialog dialog) {
        if (dialog != null) {
            long j = dialog.id;
            if (this.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
                Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$boR4n-9VN161QONITGZTAEAufXE(this, j));
            }
            this.allDialogs.remove(dialog);
            this.dialogsCanAddUsers.remove(dialog);
            this.dialogsChannelsOnly.remove(dialog);
            this.dialogsGroupsOnly.remove(dialog);
            this.dialogsUsersOnly.remove(dialog);
            this.dialogsForward.remove(dialog);
            this.dialogs_dict.remove(j);
            this.dialogs_read_inbox_max.remove(Long.valueOf(j));
            this.dialogs_read_outbox_max.remove(Long.valueOf(j));
            ArrayList arrayList = (ArrayList) this.dialogsByFolder.get(dialog.folder_id);
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
        deleteDialog(j, true, i, 0, z, null, 0);
    }

    public void setDialogsInTransaction(boolean z) {
        this.dialogsInTransaction = z;
        if (!z) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
        }
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02bb  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0278  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0278  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02bb  */
    public void deleteDialog(long r27, boolean r29, int r30, int r31, boolean r32, org.telegram.tgnet.TLRPC.InputPeer r33, long r34) {
        /*
        r26 = this;
        r11 = r26;
        r5 = r27;
        r0 = r29;
        r7 = r30;
        r9 = r32;
        r1 = 2;
        if (r7 != r1) goto L_0x0015;
    L_0x000d:
        r0 = r26.getMessagesStorage();
        r0.deleteDialog(r5, r7);
        return;
    L_0x0015:
        r2 = 3;
        if (r7 == 0) goto L_0x001a;
    L_0x0018:
        if (r7 != r2) goto L_0x0021;
    L_0x001a:
        r3 = r26.getMediaDataController();
        r3.uninstallShortcut(r5);
    L_0x0021:
        r3 = (int) r5;
        r4 = 32;
        r12 = r5 >> r4;
        r4 = (int) r12;
        r12 = 0;
        r10 = 1;
        r14 = 0;
        if (r0 == 0) goto L_0x020c;
    L_0x002d:
        r15 = r26.getMessagesStorage();
        r15.deleteDialog(r5, r7);
        r15 = r11.dialogs_dict;
        r15 = r15.get(r5);
        r15 = (org.telegram.tgnet.TLRPC.Dialog) r15;
        if (r7 == 0) goto L_0x0040;
    L_0x003e:
        if (r7 != r2) goto L_0x0047;
    L_0x0040:
        r1 = r26.getNotificationsController();
        r1.deleteNotificationChannel(r5);
    L_0x0047:
        if (r15 == 0) goto L_0x01b5;
    L_0x0049:
        if (r31 != 0) goto L_0x005e;
    L_0x004b:
        r1 = r15.top_message;
        r1 = java.lang.Math.max(r14, r1);
        r8 = r15.read_inbox_max_id;
        r1 = java.lang.Math.max(r1, r8);
        r8 = r15.read_outbox_max_id;
        r1 = java.lang.Math.max(r1, r8);
        goto L_0x0060;
    L_0x005e:
        r1 = r31;
    L_0x0060:
        if (r7 == 0) goto L_0x006a;
    L_0x0062:
        if (r7 != r2) goto L_0x0065;
    L_0x0064:
        goto L_0x006a;
    L_0x0065:
        r15.unread_count = r14;
        r16 = r3;
        goto L_0x00b3;
    L_0x006a:
        r8 = r11.proxyDialog;
        r16 = r3;
        if (r8 == 0) goto L_0x0078;
    L_0x0070:
        r2 = r8.id;
        r8 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r8 != 0) goto L_0x0078;
    L_0x0076:
        r2 = 1;
        goto L_0x0079;
    L_0x0078:
        r2 = 0;
    L_0x0079:
        if (r2 == 0) goto L_0x009c;
    L_0x007b:
        r11.isLeftProxyChannel = r10;
        r3 = r11.proxyDialog;
        r17 = r15;
        r14 = r3.id;
        r3 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));
        if (r3 >= 0) goto L_0x0095;
    L_0x0087:
        r3 = (int) r14;
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r11.getChat(r3);
        if (r3 == 0) goto L_0x0095;
    L_0x0093:
        r3.left = r10;
    L_0x0095:
        r3 = 0;
        r11.sortDialogs(r3);
        r15 = r17;
        goto L_0x00b2;
    L_0x009c:
        r11.removeDialog(r15);
        r3 = r11.nextDialogsCacheOffset;
        r14 = r15.folder_id;
        r8 = 0;
        r3 = r3.get(r14, r8);
        if (r3 <= 0) goto L_0x00b2;
    L_0x00aa:
        r14 = r11.nextDialogsCacheOffset;
        r8 = r15.folder_id;
        r3 = r3 - r10;
        r14.put(r8, r3);
    L_0x00b2:
        r14 = r2;
    L_0x00b3:
        if (r14 != 0) goto L_0x01b1;
    L_0x00b5:
        r2 = r11.dialogMessage;
        r12 = r15.id;
        r2 = r2.get(r12);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r3 = r11.dialogMessage;
        r12 = r15.id;
        r3.remove(r12);
        if (r2 == 0) goto L_0x00d6;
    L_0x00c8:
        r3 = r2.getId();
        r8 = r11.dialogMessagesByIds;
        r12 = r2.getId();
        r8.remove(r12);
        goto L_0x00e7;
    L_0x00d6:
        r3 = r15.top_message;
        r2 = r11.dialogMessagesByIds;
        r2 = r2.get(r3);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r8 = r11.dialogMessagesByIds;
        r12 = r15.top_message;
        r8.remove(r12);
    L_0x00e7:
        if (r2 == 0) goto L_0x00f8;
    L_0x00e9:
        r2 = r2.messageOwner;
        r12 = r2.random_id;
        r17 = 0;
        r2 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1));
        if (r2 == 0) goto L_0x00f8;
    L_0x00f3:
        r2 = r11.dialogMessagesByRandomIds;
        r2.remove(r12);
    L_0x00f8:
        if (r7 != r10) goto L_0x01aa;
    L_0x00fa:
        if (r16 == 0) goto L_0x01aa;
    L_0x00fc:
        if (r3 <= 0) goto L_0x01aa;
    L_0x00fe:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r2.<init>();
        r3 = r15.top_message;
        r2.id = r3;
        r3 = r26.getUserConfig();
        r3 = r3.getClientUserId();
        r12 = (long) r3;
        r3 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1));
        if (r3 != 0) goto L_0x0116;
    L_0x0114:
        r3 = 1;
        goto L_0x0117;
    L_0x0116:
        r3 = 0;
    L_0x0117:
        r2.out = r3;
        r3 = r26.getUserConfig();
        r3 = r3.getClientUserId();
        r2.from_id = r3;
        r3 = r2.flags;
        r3 = r3 | 256;
        r2.flags = r3;
        r3 = new org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
        r3.<init>();
        r2.action = r3;
        r3 = r15.last_message_date;
        r2.date = r3;
        r3 = r16;
        r12 = (long) r3;
        r2.dialog_id = r12;
        if (r3 <= 0) goto L_0x0147;
    L_0x013b:
        r8 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r8.<init>();
        r2.to_id = r8;
        r8 = r2.to_id;
        r8.user_id = r3;
        goto L_0x016d;
    L_0x0147:
        r8 = -r3;
        r12 = java.lang.Integer.valueOf(r8);
        r12 = r11.getChat(r12);
        r12 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r12 == 0) goto L_0x0162;
    L_0x0156:
        r12 = new org.telegram.tgnet.TLRPC$TL_peerChannel;
        r12.<init>();
        r2.to_id = r12;
        r12 = r2.to_id;
        r12.channel_id = r8;
        goto L_0x016d;
    L_0x0162:
        r12 = new org.telegram.tgnet.TLRPC$TL_peerChat;
        r12.<init>();
        r2.to_id = r12;
        r12 = r2.to_id;
        r12.chat_id = r8;
    L_0x016d:
        r8 = new org.telegram.messenger.MessageObject;
        r12 = r11.currentAccount;
        r13 = r11.createdDialogIds;
        r10 = r2.dialog_id;
        r10 = java.lang.Long.valueOf(r10);
        r10 = r13.contains(r10);
        r8.<init>(r12, r2, r10);
        r10 = new java.util.ArrayList;
        r10.<init>();
        r10.add(r8);
        r11 = new java.util.ArrayList;
        r11.<init>();
        r11.add(r2);
        r2 = 0;
        r12 = r26;
        r12.updateInterfaceWithMessages(r5, r10, r2);
        r19 = r26.getMessagesStorage();
        r21 = 0;
        r22 = 1;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r20 = r11;
        r19.putMessages(r20, r21, r22, r23, r24, r25);
        goto L_0x01b9;
    L_0x01aa:
        r12 = r11;
        r3 = r16;
        r2 = 0;
        r15.top_message = r2;
        goto L_0x01b9;
    L_0x01b1:
        r12 = r11;
        r3 = r16;
        goto L_0x01b9;
    L_0x01b5:
        r12 = r11;
        r1 = r31;
        r14 = 0;
    L_0x01b9:
        r2 = r12.dialogsInTransaction;
        if (r2 != 0) goto L_0x01f9;
    L_0x01bd:
        if (r14 == 0) goto L_0x01d3;
    L_0x01bf:
        r2 = r26.getNotificationCenter();
        r10 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r11 = 1;
        r13 = new java.lang.Object[r11];
        r14 = java.lang.Boolean.valueOf(r11);
        r8 = 0;
        r13[r8] = r14;
        r2.postNotificationName(r10, r13);
        goto L_0x01fa;
    L_0x01d3:
        r8 = 0;
        r2 = r26.getNotificationCenter();
        r10 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r11 = new java.lang.Object[r8];
        r2.postNotificationName(r10, r11);
        r2 = r26.getNotificationCenter();
        r10 = org.telegram.messenger.NotificationCenter.removeAllMessagesFromDialog;
        r11 = 2;
        r11 = new java.lang.Object[r11];
        r13 = java.lang.Long.valueOf(r27);
        r11[r8] = r13;
        r13 = java.lang.Boolean.valueOf(r8);
        r14 = 1;
        r11[r14] = r13;
        r2.postNotificationName(r10, r11);
        goto L_0x01fa;
    L_0x01f9:
        r8 = 0;
    L_0x01fa:
        r2 = r26.getMessagesStorage();
        r2 = r2.getStorageQueue();
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$dt98ODWXoNLrHu8D7iAdSBOvu5o;
        r10.<init>(r12, r5);
        r2.postRunnable(r10);
        r10 = r1;
        goto L_0x0210;
    L_0x020c:
        r12 = r11;
        r8 = 0;
        r10 = r31;
    L_0x0210:
        r1 = 3;
        if (r7 != r1) goto L_0x0214;
    L_0x0213:
        return;
    L_0x0214:
        if (r3 == 0) goto L_0x02e7;
    L_0x0216:
        if (r33 != 0) goto L_0x021e;
    L_0x0218:
        r1 = r12.getInputPeer(r3);
        r11 = r1;
        goto L_0x0220;
    L_0x021e:
        r11 = r33;
    L_0x0220:
        if (r11 != 0) goto L_0x0223;
    L_0x0222:
        return;
    L_0x0223:
        r1 = r11 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r1 == 0) goto L_0x022c;
    L_0x022a:
        if (r7 == 0) goto L_0x0272;
    L_0x022c:
        if (r10 <= 0) goto L_0x0239;
    L_0x022e:
        if (r10 == r2) goto L_0x0239;
    L_0x0230:
        r3 = r12.deletedHistory;
        r4 = java.lang.Integer.valueOf(r10);
        r3.put(r5, r4);
    L_0x0239:
        r3 = 0;
        r13 = (r34 > r3 ? 1 : (r34 == r3 ? 0 : -1));
        if (r13 != 0) goto L_0x0272;
    L_0x023f:
        r3 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0264 }
        r4 = r11.getObjectSize();	 Catch:{ Exception -> 0x0264 }
        r4 = r4 + 28;
        r3.<init>(r4);	 Catch:{ Exception -> 0x0264 }
        r4 = 13;
        r3.writeInt32(r4);	 Catch:{ Exception -> 0x0262 }
        r3.writeInt64(r5);	 Catch:{ Exception -> 0x0262 }
        r3.writeBool(r0);	 Catch:{ Exception -> 0x0262 }
        r3.writeInt32(r7);	 Catch:{ Exception -> 0x0262 }
        r3.writeInt32(r10);	 Catch:{ Exception -> 0x0262 }
        r3.writeBool(r9);	 Catch:{ Exception -> 0x0262 }
        r11.serializeToStream(r3);	 Catch:{ Exception -> 0x0262 }
        goto L_0x0269;
    L_0x0262:
        r0 = move-exception;
        goto L_0x0266;
    L_0x0264:
        r0 = move-exception;
        r3 = 0;
    L_0x0266:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0269:
        r0 = r26.getMessagesStorage();
        r3 = r0.createPendingTask(r3);
        goto L_0x0274;
    L_0x0272:
        r3 = r34;
    L_0x0274:
        r0 = 64;
        if (r1 == 0) goto L_0x02bb;
    L_0x0278:
        if (r7 != 0) goto L_0x0288;
    L_0x027a:
        r7 = 0;
        r0 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
        if (r0 == 0) goto L_0x0287;
    L_0x0280:
        r0 = r26.getMessagesStorage();
        r0.removePendingTask(r3);
    L_0x0287:
        return;
    L_0x0288:
        r1 = new org.telegram.tgnet.TLRPC$TL_channels_deleteHistory;
        r1.<init>();
        r7 = new org.telegram.tgnet.TLRPC$TL_inputChannel;
        r7.<init>();
        r1.channel = r7;
        r7 = r1.channel;
        r8 = r11.channel_id;
        r7.channel_id = r8;
        r8 = r11.access_hash;
        r7.access_hash = r8;
        if (r10 <= 0) goto L_0x02a1;
    L_0x02a0:
        goto L_0x02a4;
    L_0x02a1:
        r10 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x02a4:
        r1.max_id = r10;
        r2 = r26.getConnectionsManager();
        r7 = new org.telegram.messenger.-$$Lambda$MessagesController$YuUQUrPMffEKOZbWptvyS2Mbhbs;
        r29 = r7;
        r30 = r26;
        r31 = r3;
        r33 = r27;
        r29.<init>(r30, r31, r33);
        r2.sendRequest(r1, r7, r0);
        goto L_0x0302;
    L_0x02bb:
        r13 = new org.telegram.tgnet.TLRPC$TL_messages_deleteHistory;
        r13.<init>();
        r13.peer = r11;
        if (r7 != 0) goto L_0x02c5;
    L_0x02c4:
        goto L_0x02c6;
    L_0x02c5:
        r2 = r10;
    L_0x02c6:
        r13.max_id = r2;
        if (r7 == 0) goto L_0x02cb;
    L_0x02ca:
        r8 = 1;
    L_0x02cb:
        r13.just_clear = r8;
        r13.revoke = r9;
        r14 = r26.getConnectionsManager();
        r15 = new org.telegram.messenger.-$$Lambda$MessagesController$G1oGTH0z1YiF5ntqkcQy1QJR8dg;
        r1 = r15;
        r2 = r26;
        r5 = r27;
        r7 = r30;
        r8 = r10;
        r9 = r32;
        r10 = r11;
        r1.<init>(r2, r3, r5, r7, r8, r9, r10);
        r14.sendRequest(r13, r15, r0);
        goto L_0x0302;
    L_0x02e7:
        r1 = 1;
        if (r7 != r1) goto L_0x02fb;
    L_0x02ea:
        r0 = r26.getSecretChatHelper();
        r1 = java.lang.Integer.valueOf(r4);
        r1 = r12.getEncryptedChat(r1);
        r2 = 0;
        r0.sendClearHistoryMessage(r1, r2);
        goto L_0x0302;
    L_0x02fb:
        r0 = r26.getSecretChatHelper();
        r0.declineSecretChat(r4);
    L_0x0302:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteDialog(long, boolean, int, int, boolean, org.telegram.tgnet.TLRPC$InputPeer, long):void");
    }

    public /* synthetic */ void lambda$deleteDialog$83$MessagesController(long j) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$p0w_m1fIbYlXjX2Cn0P0Jqiy208(this, j));
    }

    public /* synthetic */ void lambda$null$82$MessagesController(long j) {
        getNotificationsController().removeNotificationsForDialog(j);
    }

    public /* synthetic */ void lambda$deleteDialog$85$MessagesController(long j, long j2, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$TxbCaVeW9CnhDvFtjqAqv0jklT0(this, j2));
    }

    public /* synthetic */ void lambda$null$84$MessagesController(long j) {
        this.deletedHistory.remove(j);
    }

    public /* synthetic */ void lambda$deleteDialog$86$MessagesController(long j, long j2, int i, int i2, boolean z, InputPeer inputPeer, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        if (tL_error == null) {
            TL_messages_affectedHistory tL_messages_affectedHistory = (TL_messages_affectedHistory) tLObject;
            if (tL_messages_affectedHistory.offset > 0) {
                deleteDialog(j2, false, i, i2, z, inputPeer, 0);
            }
            processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            getMessagesStorage().onDeleteQueryComplete(j2);
            return;
        }
    }

    public void saveGif(Object obj, Document document) {
        if (obj != null && MessageObject.isGifDocument(document)) {
            TL_messages_saveGif tL_messages_saveGif = new TL_messages_saveGif();
            tL_messages_saveGif.id = new TL_inputDocument();
            InputDocument inputDocument = tL_messages_saveGif.id;
            inputDocument.id = document.id;
            inputDocument.access_hash = document.access_hash;
            inputDocument.file_reference = document.file_reference;
            if (inputDocument.file_reference == null) {
                inputDocument.file_reference = new byte[0];
            }
            tL_messages_saveGif.unsave = false;
            getConnectionsManager().sendRequest(tL_messages_saveGif, new -$$Lambda$MessagesController$xr1VGvar_ABGQvO5EA0pAB0eXhU(this, obj, tL_messages_saveGif));
        }
    }

    public /* synthetic */ void lambda$saveGif$87$MessagesController(Object obj, TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tL_messages_saveGif);
        }
    }

    public void saveRecentSticker(Object obj, Document document, boolean z) {
        if (obj != null && document != null) {
            TL_messages_saveRecentSticker tL_messages_saveRecentSticker = new TL_messages_saveRecentSticker();
            tL_messages_saveRecentSticker.id = new TL_inputDocument();
            InputDocument inputDocument = tL_messages_saveRecentSticker.id;
            inputDocument.id = document.id;
            inputDocument.access_hash = document.access_hash;
            inputDocument.file_reference = document.file_reference;
            if (inputDocument.file_reference == null) {
                inputDocument.file_reference = new byte[0];
            }
            tL_messages_saveRecentSticker.unsave = false;
            tL_messages_saveRecentSticker.attached = z;
            getConnectionsManager().sendRequest(tL_messages_saveRecentSticker, new -$$Lambda$MessagesController$ToFkgSIgWh8eBHwz_VIduAjWqwU(this, obj, tL_messages_saveRecentSticker));
        }
    }

    public /* synthetic */ void lambda$saveRecentSticker$88$MessagesController(Object obj, TL_messages_saveRecentSticker tL_messages_saveRecentSticker, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tL_messages_saveRecentSticker);
        }
    }

    public void loadChannelParticipants(Integer num) {
        if (!this.loadingFullParticipants.contains(num) && !this.loadedFullParticipants.contains(num)) {
            this.loadingFullParticipants.add(num);
            TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
            tL_channels_getParticipants.channel = getInputChannel(num.intValue());
            tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
            tL_channels_getParticipants.offset = 0;
            tL_channels_getParticipants.limit = 32;
            getConnectionsManager().sendRequest(tL_channels_getParticipants, new -$$Lambda$MessagesController$HC_pjBhtPzVYnJSawD_E8j_mMqw(this, num));
        }
    }

    public /* synthetic */ void lambda$loadChannelParticipants$90$MessagesController(Integer num, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$Pl_SPEHDlNg4Fmng7KNzFjWhLT8(this, tL_error, tLObject, num));
    }

    public /* synthetic */ void lambda$null$89$MessagesController(TL_error tL_error, TLObject tLObject, Integer num) {
        if (tL_error == null) {
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            putUsers(tL_channels_channelParticipants.users, false);
            getMessagesStorage().putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
            getMessagesStorage().updateChannelUsers(num.intValue(), tL_channels_channelParticipants.participants);
            this.loadedFullParticipants.add(num);
        }
        this.loadingFullParticipants.remove(num);
    }

    public void processChatInfo(int i, ChatFull chatFull, ArrayList<User> arrayList, boolean z, boolean z2, boolean z3, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$YbjB7P1AtOlqY4-kYsnspgmpoQg(this, z, i, z3, z2, chatFull, arrayList, messageObject));
    }

    public /* synthetic */ void lambda$processChatInfo$91$MessagesController(boolean z, int i, boolean z2, boolean z3, ChatFull chatFull, ArrayList arrayList, MessageObject messageObject) {
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
            getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(z2), messageObject);
        }
    }

    public void loadUserInfo(User user, boolean z, int i) {
        getMessagesStorage().loadUserInfo(user, z, i);
    }

    public void processUserInfo(User user, UserFull userFull, boolean z, boolean z2, MessageObject messageObject, int i) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$UR3wFUXqpvLIjSkHzdOh15UlhqU(this, z, user, i, z2, userFull, messageObject));
    }

    public /* synthetic */ void lambda$processUserInfo$92$MessagesController(boolean z, User user, int i, boolean z2, UserFull userFull, MessageObject messageObject) {
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
        int i;
        int keyAt;
        ArrayList arrayList;
        long uptimeMillis;
        int keyAt2;
        long currentTimeMillis = System.currentTimeMillis();
        checkDeletingTask(false);
        checkReadTasks();
        if (getUserConfig().isClientActivated()) {
            TL_account_updateStatus tL_account_updateStatus;
            if (getConnectionsManager().getPauseTime() == 0 && ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePausedStageQueue) {
                if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && this.statusSettingState != 1 && (this.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000 || this.offlineSent)) {
                    this.statusSettingState = 1;
                    if (this.statusRequest != 0) {
                        getConnectionsManager().cancelRequest(this.statusRequest, true);
                    }
                    tL_account_updateStatus = new TL_account_updateStatus();
                    tL_account_updateStatus.offline = false;
                    this.statusRequest = getConnectionsManager().sendRequest(tL_account_updateStatus, new -$$Lambda$MessagesController$h06mcQTgTHaTO-trMReHrMVWk6w(this));
                }
            } else if (!(this.statusSettingState == 2 || this.offlineSent || Math.abs(System.currentTimeMillis() - getConnectionsManager().getPauseTime()) < 2000)) {
                this.statusSettingState = 2;
                if (this.statusRequest != 0) {
                    getConnectionsManager().cancelRequest(this.statusRequest, true);
                }
                tL_account_updateStatus = new TL_account_updateStatus();
                tL_account_updateStatus.offline = true;
                this.statusRequest = getConnectionsManager().sendRequest(tL_account_updateStatus, new -$$Lambda$MessagesController$A5JUENTv6Earg4PHsuQcPWssROs(this));
            }
            if (this.updatesQueueChannels.size() != 0) {
                for (i = 0; i < this.updatesQueueChannels.size(); i++) {
                    int keyAt3 = this.updatesQueueChannels.keyAt(i);
                    if (this.updatesStartWaitTimeChannels.valueAt(i) + 1500 < currentTimeMillis) {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("QUEUE CHANNEL ");
                            stringBuilder.append(keyAt3);
                            stringBuilder.append(" UPDATES WAIT TIMEOUT - CHECK QUEUE");
                            FileLog.d(stringBuilder.toString());
                        }
                        processChannelsUpdatesQueue(keyAt3, 0);
                    }
                }
            }
            i = 0;
            while (i < 3) {
                if (getUpdatesStartTime(i) != 0 && getUpdatesStartTime(i) + 1500 < currentTimeMillis) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(i);
                        stringBuilder2.append(" QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        FileLog.d(stringBuilder2.toString());
                    }
                    processUpdatesQueue(i, 0);
                }
                i++;
            }
        }
        if (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= 5000) {
            this.lastViewsCheckTime = System.currentTimeMillis();
            if (this.channelViewsToSend.size() != 0) {
                i = 0;
                while (i < this.channelViewsToSend.size()) {
                    keyAt = this.channelViewsToSend.keyAt(i);
                    TL_messages_getMessagesViews tL_messages_getMessagesViews = new TL_messages_getMessagesViews();
                    tL_messages_getMessagesViews.peer = getInputPeer(keyAt);
                    tL_messages_getMessagesViews.id = (ArrayList) this.channelViewsToSend.valueAt(i);
                    tL_messages_getMessagesViews.increment = i == 0;
                    getConnectionsManager().sendRequest(tL_messages_getMessagesViews, new -$$Lambda$MessagesController$3H6xm0XGVg7V9AuczFNaKSC-Kgw(this, keyAt, tL_messages_getMessagesViews));
                    i++;
                }
                this.channelViewsToSend.clear();
            }
            if (this.pollsToCheckSize > 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$WlTRSSCpjSTvv4PzKFRCkAV3Re8(this));
            }
        }
        if (!this.onlinePrivacy.isEmpty()) {
            arrayList = null;
            keyAt = getConnectionsManager().getCurrentTime();
            for (Entry entry : this.onlinePrivacy.entrySet()) {
                if (((Integer) entry.getValue()).intValue() < keyAt - 30) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(entry.getKey());
                }
            }
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    this.onlinePrivacy.remove((Integer) it.next());
                }
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$bPqOQjoKdlc4VOjSd7n5U3rQ-T0(this));
            }
        }
        if (this.shortPollChannels.size() != 0) {
            i = 0;
            while (i < this.shortPollChannels.size()) {
                keyAt = this.shortPollChannels.keyAt(i);
                if (((long) this.shortPollChannels.valueAt(i)) < System.currentTimeMillis() / 1000) {
                    this.shortPollChannels.delete(keyAt);
                    i--;
                    if (this.needShortPollChannels.indexOfKey(keyAt) >= 0) {
                        getChannelDifference(keyAt);
                    }
                }
                i++;
            }
        }
        if (this.shortPollOnlines.size() != 0) {
            uptimeMillis = SystemClock.uptimeMillis() / 1000;
            i = 0;
            while (i < this.shortPollOnlines.size()) {
                keyAt2 = this.shortPollOnlines.keyAt(i);
                if (((long) this.shortPollOnlines.valueAt(i)) < uptimeMillis) {
                    if (this.needShortPollChannels.indexOfKey(keyAt2) >= 0) {
                        this.shortPollOnlines.put(keyAt2, (int) (300 + uptimeMillis));
                    } else {
                        this.shortPollOnlines.delete(keyAt2);
                        i--;
                    }
                    TL_messages_getOnlines tL_messages_getOnlines = new TL_messages_getOnlines();
                    tL_messages_getOnlines.peer = getInputPeer(-keyAt2);
                    getConnectionsManager().sendRequest(tL_messages_getOnlines, new -$$Lambda$MessagesController$GPwOJCGSpbQ3_YiywGzUwiOWoKE(this, keyAt2));
                }
                i++;
            }
        }
        if (!(this.printingUsers.isEmpty() && this.lastPrintingStringCount == this.printingUsers.size())) {
            arrayList = new ArrayList(this.printingUsers.keySet());
            keyAt2 = 0;
            Object obj = null;
            while (keyAt2 < arrayList.size()) {
                ArrayList arrayList2;
                ArrayList arrayList3;
                uptimeMillis = ((Long) arrayList.get(keyAt2)).longValue();
                ArrayList arrayList4 = (ArrayList) this.printingUsers.get(Long.valueOf(uptimeMillis));
                if (arrayList4 != null) {
                    Object obj2 = obj;
                    int i2 = 0;
                    while (i2 < arrayList4.size()) {
                        PrintingUser printingUser = (PrintingUser) arrayList4.get(i2);
                        arrayList2 = arrayList;
                        if (printingUser.lastTime + ((long) (printingUser.action instanceof TL_sendMessageGamePlayAction ? 30000 : 5900)) < currentTimeMillis) {
                            arrayList4.remove(printingUser);
                            i2--;
                            obj2 = 1;
                        }
                        i2++;
                        arrayList = arrayList2;
                    }
                    arrayList2 = arrayList;
                    obj = obj2;
                } else {
                    arrayList2 = arrayList;
                }
                if (arrayList4 == null || arrayList4.isEmpty()) {
                    this.printingUsers.remove(Long.valueOf(uptimeMillis));
                    arrayList3 = arrayList2;
                    arrayList3.remove(keyAt2);
                    keyAt2--;
                } else {
                    arrayList3 = arrayList2;
                }
                keyAt2++;
                arrayList = arrayList3;
            }
            updatePrintingStrings();
            if (obj != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$dGxBSu9ZLTuPJlYBeaPbaQ8g_Fw(this));
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
        checkProxyInfoInternal(false);
        checkTosUpdate();
    }

    public /* synthetic */ void lambda$updateTimerProc$93$MessagesController(TLObject tLObject, TL_error tL_error) {
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

    public /* synthetic */ void lambda$updateTimerProc$94$MessagesController(TLObject tLObject, TL_error tL_error) {
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

    public /* synthetic */ void lambda$updateTimerProc$96$MessagesController(int i, TL_messages_getMessagesViews tL_messages_getMessagesViews, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            Vector vector = (Vector) tLObject;
            SparseArray sparseArray = new SparseArray();
            SparseIntArray sparseIntArray = (SparseIntArray) sparseArray.get(i);
            if (sparseIntArray == null) {
                sparseIntArray = new SparseIntArray();
                sparseArray.put(i, sparseIntArray);
            }
            i = 0;
            while (i < tL_messages_getMessagesViews.id.size() && i < vector.objects.size()) {
                sparseIntArray.put(((Integer) tL_messages_getMessagesViews.id.get(i)).intValue(), ((Integer) vector.objects.get(i)).intValue());
                i++;
            }
            getMessagesStorage().putChannelViews(sparseArray, tL_messages_getMessagesViews.peer instanceof TL_inputPeerChannel);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$IRL1h93yR_mQfG3-FaJJnsEeHuQ(this, sparseArray));
        }
    }

    public /* synthetic */ void lambda$null$95$MessagesController(SparseArray sparseArray) {
        getNotificationCenter().postNotificationName(NotificationCenter.didUpdatedMessagesViews, sparseArray);
    }

    public /* synthetic */ void lambda$updateTimerProc$98$MessagesController() {
        long uptimeMillis = SystemClock.uptimeMillis();
        int size = this.pollsToCheck.size();
        int i = 0;
        while (i < size) {
            SparseArray sparseArray = (SparseArray) this.pollsToCheck.valueAt(i);
            if (sparseArray != null) {
                int size2 = sparseArray.size();
                int i2 = 0;
                while (i2 < size2) {
                    MessageObject messageObject = (MessageObject) sparseArray.valueAt(i2);
                    if (Math.abs(uptimeMillis - messageObject.pollLastCheckTime) >= 30000) {
                        messageObject.pollLastCheckTime = uptimeMillis;
                        TL_messages_getPollResults tL_messages_getPollResults = new TL_messages_getPollResults();
                        tL_messages_getPollResults.peer = getInputPeer((int) messageObject.getDialogId());
                        tL_messages_getPollResults.msg_id = messageObject.getId();
                        getConnectionsManager().sendRequest(tL_messages_getPollResults, new -$$Lambda$MessagesController$0ocpZLNPRqErh9KZ2xnWpXShVk8(this));
                    } else if (!messageObject.pollVisibleOnScreen) {
                        sparseArray.remove(messageObject.getId());
                        size2--;
                        i2--;
                    }
                    i2++;
                }
                if (sparseArray.size() == 0) {
                    LongSparseArray longSparseArray = this.pollsToCheck;
                    longSparseArray.remove(longSparseArray.keyAt(i));
                    size--;
                    i--;
                }
            }
            i++;
        }
        this.pollsToCheckSize = this.pollsToCheck.size();
    }

    public /* synthetic */ void lambda$null$97$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$updateTimerProc$99$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    public /* synthetic */ void lambda$updateTimerProc$101$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            TL_chatOnlines tL_chatOnlines = (TL_chatOnlines) tLObject;
            getMessagesStorage().updateChatOnlineCount(i, tL_chatOnlines.onlines);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$EHg3sSs0dZBuz5Z43ZiN-aVJ21M(this, i, tL_chatOnlines));
        }
    }

    public /* synthetic */ void lambda$null$100$MessagesController(int i, TL_chatOnlines tL_chatOnlines) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatOnlineCountDidLoad, Integer.valueOf(i), Integer.valueOf(tL_chatOnlines.onlines));
    }

    public /* synthetic */ void lambda$updateTimerProc$102$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
    }

    private void checkTosUpdate() {
        if (this.nextTosCheckTime <= getConnectionsManager().getCurrentTime() && !this.checkingTosUpdate && getUserConfig().isClientActivated()) {
            this.checkingTosUpdate = true;
            getConnectionsManager().sendRequest(new TL_help_getTermsOfServiceUpdate(), new -$$Lambda$MessagesController$HuA_cAidgysMWuMG0_82MbLLdqo(this));
        }
    }

    public /* synthetic */ void lambda$checkTosUpdate$104$MessagesController(TLObject tLObject, TL_error tL_error) {
        this.checkingTosUpdate = false;
        if (tLObject instanceof TL_help_termsOfServiceUpdateEmpty) {
            this.nextTosCheckTime = ((TL_help_termsOfServiceUpdateEmpty) tLObject).expires;
        } else if (tLObject instanceof TL_help_termsOfServiceUpdate) {
            TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate = (TL_help_termsOfServiceUpdate) tLObject;
            this.nextTosCheckTime = tL_help_termsOfServiceUpdate.expires;
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$Hxk4H8urU9dsoJ1ehd_6G40EQdk(this, tL_help_termsOfServiceUpdate));
        } else {
            this.nextTosCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        }
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
    }

    public /* synthetic */ void lambda$null$103$MessagesController(TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(4), tL_help_termsOfServiceUpdate.terms_of_service);
    }

    public void checkProxyInfo(boolean z) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$hiarKoyjfwKbFrK1Q936M_eTjxU(this, z));
    }

    public /* synthetic */ void lambda$checkProxyInfo$105$MessagesController(boolean z) {
        checkProxyInfoInternal(z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0098  */
    private void checkProxyInfoInternal(boolean r12) {
        /*
        r11 = this;
        r0 = 0;
        if (r12 == 0) goto L_0x0009;
    L_0x0003:
        r1 = r11.checkingProxyInfo;
        if (r1 == 0) goto L_0x0009;
    L_0x0007:
        r11.checkingProxyInfo = r0;
    L_0x0009:
        if (r12 != 0) goto L_0x0017;
    L_0x000b:
        r12 = r11.nextProxyInfoCheckTime;
        r1 = r11.getConnectionsManager();
        r1 = r1.getCurrentTime();
        if (r12 > r1) goto L_0x001b;
    L_0x0017:
        r12 = r11.checkingProxyInfo;
        if (r12 == 0) goto L_0x001c;
    L_0x001b:
        return;
    L_0x001c:
        r12 = r11.checkingProxyInfoRequestId;
        r1 = 1;
        if (r12 == 0) goto L_0x002c;
    L_0x0021:
        r12 = r11.getConnectionsManager();
        r2 = r11.checkingProxyInfoRequestId;
        r12.cancelRequest(r2, r1);
        r11.checkingProxyInfoRequestId = r0;
    L_0x002c:
        r12 = getGlobalMainSettings();
        r2 = "proxy_enabled";
        r2 = r12.getBoolean(r2, r0);
        r3 = "";
        r4 = "proxy_ip";
        r4 = r12.getString(r4, r3);
        r5 = "proxy_secret";
        r12 = r12.getString(r5, r3);
        r5 = r11.proxyDialogId;
        r7 = 0;
        r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r3 == 0) goto L_0x0067;
    L_0x004c:
        r3 = r11.proxyDialogAddress;
        if (r3 == 0) goto L_0x0067;
    L_0x0050:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r4);
        r5.append(r12);
        r5 = r5.toString();
        r3 = r3.equals(r5);
        if (r3 != 0) goto L_0x0067;
    L_0x0065:
        r3 = 1;
        goto L_0x0068;
    L_0x0067:
        r3 = 0;
    L_0x0068:
        r5 = r11.lastCheckProxyId;
        r5 = r5 + r1;
        r11.lastCheckProxyId = r5;
        r5 = 2;
        if (r2 == 0) goto L_0x0095;
    L_0x0070:
        r2 = android.text.TextUtils.isEmpty(r4);
        if (r2 != 0) goto L_0x0095;
    L_0x0076:
        r2 = android.text.TextUtils.isEmpty(r12);
        if (r2 != 0) goto L_0x0095;
    L_0x007c:
        r11.checkingProxyInfo = r1;
        r2 = r11.lastCheckProxyId;
        r6 = new org.telegram.tgnet.TLRPC$TL_help_getProxyData;
        r6.<init>();
        r9 = r11.getConnectionsManager();
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$WDlDK3LAQKEoE17BTn6zJG2p-h0;
        r10.<init>(r11, r2, r4, r12);
        r12 = r9.sendRequest(r6, r10);
        r11.checkingProxyInfoRequestId = r12;
        goto L_0x0096;
    L_0x0095:
        r3 = 2;
    L_0x0096:
        if (r3 == 0) goto L_0x00dd;
    L_0x0098:
        r11.proxyDialogId = r7;
        r12 = 0;
        r11.proxyDialogAddress = r12;
        r12 = getGlobalMainSettings();
        r12 = r12.edit();
        r6 = r11.proxyDialogId;
        r2 = "proxy_dialog";
        r12 = r12.putLong(r2, r6);
        r2 = "proxyDialogAddress";
        r12 = r12.remove(r2);
        r12.commit();
        r12 = r11.getConnectionsManager();
        r12 = r12.getCurrentTime();
        r12 = r12 + 3600;
        r11.nextProxyInfoCheckTime = r12;
        if (r3 != r5) goto L_0x00d5;
    L_0x00c4:
        r11.checkingProxyInfo = r0;
        r12 = r11.checkingProxyInfoRequestId;
        if (r12 == 0) goto L_0x00d5;
    L_0x00ca:
        r12 = r11.getConnectionsManager();
        r2 = r11.checkingProxyInfoRequestId;
        r12.cancelRequest(r2, r1);
        r11.checkingProxyInfoRequestId = r0;
    L_0x00d5:
        r12 = new org.telegram.messenger.-$$Lambda$MessagesController$DBAgk1weV09qLtd-t72gEXkuPiA;
        r12.<init>(r11);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r12);
    L_0x00dd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.checkProxyInfoInternal(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00af  */
    /* JADX WARNING: Missing block: B:22:0x004d, code skipped:
            if (r5.restricted == false) goto L_0x0053;
     */
    /* JADX WARNING: Missing block: B:34:0x0079, code skipped:
            if (r5.restricted == false) goto L_0x0025;
     */
    public /* synthetic */ void lambda$checkProxyInfoInternal$110$MessagesController(int r11, java.lang.String r12, java.lang.String r13, org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC.TL_error r15) {
        /*
        r10 = this;
        r15 = r10.lastCheckProxyId;
        if (r11 == r15) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_help_proxyDataEmpty;
        r0 = "proxyDialogAddress";
        r1 = "proxy_dialog";
        r2 = 0;
        r3 = 1;
        if (r15 == 0) goto L_0x0017;
    L_0x000f:
        r14 = (org.telegram.tgnet.TLRPC.TL_help_proxyDataEmpty) r14;
        r11 = r14.expires;
        r10.nextProxyInfoCheckTime = r11;
        goto L_0x00c7;
    L_0x0017:
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;
        if (r15 == 0) goto L_0x00bb;
    L_0x001b:
        r8 = r14;
        r8 = (org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo) r8;
        r14 = r8.peer;
        r15 = r14.user_id;
        if (r15 == 0) goto L_0x0028;
    L_0x0024:
        r14 = (long) r15;
    L_0x0025:
        r6 = r14;
        r3 = 0;
        goto L_0x007f;
    L_0x0028:
        r15 = r14.chat_id;
        if (r15 == 0) goto L_0x0056;
    L_0x002c:
        r14 = -r15;
        r14 = (long) r14;
        r4 = 0;
    L_0x002f:
        r5 = r8.chats;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x0053;
    L_0x0037:
        r5 = r8.chats;
        r5 = r5.get(r4);
        r5 = (org.telegram.tgnet.TLRPC.Chat) r5;
        r6 = r5.id;
        r7 = r8.peer;
        r7 = r7.chat_id;
        if (r6 != r7) goto L_0x0050;
    L_0x0047:
        r4 = r5.kicked;
        if (r4 != 0) goto L_0x0054;
    L_0x004b:
        r4 = r5.restricted;
        if (r4 == 0) goto L_0x0053;
    L_0x004f:
        goto L_0x0054;
    L_0x0050:
        r4 = r4 + 1;
        goto L_0x002f;
    L_0x0053:
        r3 = 0;
    L_0x0054:
        r6 = r14;
        goto L_0x007f;
    L_0x0056:
        r14 = r14.channel_id;
        r14 = -r14;
        r14 = (long) r14;
        r4 = 0;
    L_0x005b:
        r5 = r8.chats;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x0025;
    L_0x0063:
        r5 = r8.chats;
        r5 = r5.get(r4);
        r5 = (org.telegram.tgnet.TLRPC.Chat) r5;
        r6 = r5.id;
        r7 = r8.peer;
        r7 = r7.channel_id;
        if (r6 != r7) goto L_0x007c;
    L_0x0073:
        r4 = r5.kicked;
        if (r4 != 0) goto L_0x0054;
    L_0x0077:
        r4 = r5.restricted;
        if (r4 == 0) goto L_0x0025;
    L_0x007b:
        goto L_0x0054;
    L_0x007c:
        r4 = r4 + 1;
        goto L_0x005b;
    L_0x007f:
        r10.proxyDialogId = r6;
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r12);
        r14.append(r13);
        r12 = r14.toString();
        r10.proxyDialogAddress = r12;
        r12 = getGlobalMainSettings();
        r12 = r12.edit();
        r13 = r10.proxyDialogId;
        r12 = r12.putLong(r1, r13);
        r13 = r10.proxyDialogAddress;
        r12 = r12.putString(r0, r13);
        r12.commit();
        r12 = r8.expires;
        r10.nextProxyInfoCheckTime = r12;
        if (r3 != 0) goto L_0x00c7;
    L_0x00af:
        r12 = new org.telegram.messenger.-$$Lambda$MessagesController$L5r9bTM6APmfaovjYsYdtSgCaaw;
        r4 = r12;
        r5 = r10;
        r9 = r11;
        r4.<init>(r5, r6, r8, r9);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r12);
        goto L_0x00c7;
    L_0x00bb:
        r11 = r10.getConnectionsManager();
        r11 = r11.getCurrentTime();
        r11 = r11 + 3600;
        r10.nextProxyInfoCheckTime = r11;
    L_0x00c7:
        if (r3 == 0) goto L_0x00ee;
    L_0x00c9:
        r11 = 0;
        r10.proxyDialogId = r11;
        r11 = getGlobalMainSettings();
        r11 = r11.edit();
        r12 = r10.proxyDialogId;
        r11 = r11.putLong(r1, r12);
        r11 = r11.remove(r0);
        r11.commit();
        r10.checkingProxyInfoRequestId = r2;
        r10.checkingProxyInfo = r2;
        r11 = new org.telegram.messenger.-$$Lambda$MessagesController$DBAgk1weV09qLtd-t72gEXkuPiA;
        r11.<init>(r10);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r11);
    L_0x00ee:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$checkProxyInfoInternal$110$MessagesController(int, java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$null$109$MessagesController(long j, TL_help_proxyDataPromo tL_help_proxyDataPromo, int i) {
        Dialog dialog = this.proxyDialog;
        if (!(dialog == null || j == dialog.id)) {
            removeProxyDialog();
        }
        this.proxyDialog = (Dialog) this.dialogs_dict.get(j);
        int i2 = 0;
        if (this.proxyDialog != null) {
            this.checkingProxyInfo = false;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
            return;
        }
        SparseArray sparseArray = new SparseArray();
        SparseArray sparseArray2 = new SparseArray();
        for (int i3 = 0; i3 < tL_help_proxyDataPromo.users.size(); i3++) {
            User user = (User) tL_help_proxyDataPromo.users.get(i3);
            sparseArray.put(user.id, user);
        }
        while (i2 < tL_help_proxyDataPromo.chats.size()) {
            Chat chat = (Chat) tL_help_proxyDataPromo.chats.get(i2);
            sparseArray2.put(chat.id, chat);
            i2++;
        }
        TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
        TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
        Peer peer = tL_help_proxyDataPromo.peer;
        int i4;
        InputPeer inputPeer;
        Chat chat2;
        if (peer.user_id != 0) {
            tL_inputDialogPeer.peer = new TL_inputPeerUser();
            InputPeer inputPeer2 = tL_inputDialogPeer.peer;
            i4 = tL_help_proxyDataPromo.peer.user_id;
            inputPeer2.user_id = i4;
            User user2 = (User) sparseArray.get(i4);
            if (user2 != null) {
                tL_inputDialogPeer.peer.access_hash = user2.access_hash;
            }
        } else if (peer.chat_id != 0) {
            tL_inputDialogPeer.peer = new TL_inputPeerChat();
            inputPeer = tL_inputDialogPeer.peer;
            i4 = tL_help_proxyDataPromo.peer.chat_id;
            inputPeer.chat_id = i4;
            chat2 = (Chat) sparseArray2.get(i4);
            if (chat2 != null) {
                tL_inputDialogPeer.peer.access_hash = chat2.access_hash;
            }
        } else {
            tL_inputDialogPeer.peer = new TL_inputPeerChannel();
            inputPeer = tL_inputDialogPeer.peer;
            i4 = tL_help_proxyDataPromo.peer.channel_id;
            inputPeer.channel_id = i4;
            chat2 = (Chat) sparseArray2.get(i4);
            if (chat2 != null) {
                tL_inputDialogPeer.peer.access_hash = chat2.access_hash;
            }
        }
        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
        this.checkingProxyInfoRequestId = getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$hGCePSjHKNEEZUYYa4wvKxoXCTs(this, i, tL_help_proxyDataPromo, j));
    }

    public /* synthetic */ void lambda$null$108$MessagesController(int i, TL_help_proxyDataPromo tL_help_proxyDataPromo, long j, TLObject tLObject, TL_error tL_error) {
        if (i == this.lastCheckProxyId) {
            this.checkingProxyInfoRequestId = 0;
            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
            if (tL_messages_peerDialogs == null || tL_messages_peerDialogs.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$93mnk-Nz7VdKR6oOzqL3mEfdUWY(this));
            } else {
                getMessagesStorage().putUsersAndChats(tL_help_proxyDataPromo.users, tL_help_proxyDataPromo.chats, true, true);
                TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
                tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
                tL_messages_dialogs.users = tL_messages_peerDialogs.users;
                tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
                tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
                getMessagesStorage().putDialogs(tL_messages_dialogs, 2);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$r00eBxvrVBisvd_A6PdgHzlHuc4(this, tL_help_proxyDataPromo, tL_messages_peerDialogs, j));
            }
            this.checkingProxyInfo = false;
        }
    }

    public /* synthetic */ void lambda$null$106$MessagesController(TL_help_proxyDataPromo tL_help_proxyDataPromo, TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        ArrayList arrayList = tL_help_proxyDataPromo.users;
        Integer valueOf = Integer.valueOf(0);
        putUsers(arrayList, false);
        putChats(tL_help_proxyDataPromo.chats, false);
        putUsers(tL_messages_peerDialogs.users, false);
        putChats(tL_messages_peerDialogs.chats, false);
        Dialog dialog = this.proxyDialog;
        if (dialog != null) {
            int i = (int) dialog.id;
            if (i < 0) {
                Chat chat = getChat(Integer.valueOf(-i));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.proxyDialog);
                }
            } else {
                removeDialog(dialog);
            }
        }
        this.proxyDialog = (Dialog) tL_messages_peerDialogs.dialogs.get(0);
        dialog = this.proxyDialog;
        dialog.id = j;
        dialog.folder_id = 0;
        if (DialogObject.isChannel(dialog)) {
            SparseIntArray sparseIntArray = this.channelsPts;
            Dialog dialog2 = this.proxyDialog;
            sparseIntArray.put(-((int) dialog2.id), dialog2.pts);
        }
        Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(this.proxyDialog.id));
        if (num == null) {
            num = valueOf;
        }
        this.dialogs_read_inbox_max.put(Long.valueOf(this.proxyDialog.id), Integer.valueOf(Math.max(num.intValue(), this.proxyDialog.read_inbox_max_id)));
        num = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(this.proxyDialog.id));
        if (num == null) {
            num = valueOf;
        }
        this.dialogs_read_outbox_max.put(Long.valueOf(this.proxyDialog.id), Integer.valueOf(Math.max(num.intValue(), this.proxyDialog.read_outbox_max_id)));
        this.dialogs_dict.put(j, this.proxyDialog);
        if (!tL_messages_peerDialogs.messages.isEmpty()) {
            int i2;
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (i2 = 0; i2 < tL_messages_peerDialogs.users.size(); i2++) {
                User user = (User) tL_messages_peerDialogs.users.get(i2);
                sparseArray.put(user.id, user);
            }
            for (i2 = 0; i2 < tL_messages_peerDialogs.chats.size(); i2++) {
                Chat chat2 = (Chat) tL_messages_peerDialogs.chats.get(i2);
                sparseArray2.put(chat2.id, chat2);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, (Message) tL_messages_peerDialogs.messages.get(0), sparseArray, sparseArray2, false);
            this.dialogMessage.put(j, messageObject);
            Dialog dialog3 = this.proxyDialog;
            if (dialog3.last_message_date == 0) {
                dialog3.last_message_date = messageObject.messageOwner.date;
            }
        }
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
    }

    public /* synthetic */ void lambda$null$107$MessagesController() {
        Dialog dialog = this.proxyDialog;
        if (dialog != null) {
            int i = (int) dialog.id;
            if (i < 0) {
                Chat chat = getChat(Integer.valueOf(-i));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.proxyDialog);
                }
            } else {
                removeDialog(dialog);
            }
            this.proxyDialog = null;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    private void removeProxyDialog() {
        Dialog dialog = this.proxyDialog;
        if (dialog != null) {
            int i = (int) dialog.id;
            if (i < 0) {
                Chat chat = getChat(Integer.valueOf(-i));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.proxyDialog);
                }
            } else {
                removeDialog(dialog);
            }
            this.proxyDialog = null;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public boolean isProxyDialog(long j, boolean z) {
        Dialog dialog = this.proxyDialog;
        return dialog != null && dialog.id == j && (!z || this.isLeftProxyChannel);
    }

    private String getUserNameForTyping(User user) {
        String str = "";
        if (user == null) {
            return str;
        }
        String str2 = user.first_name;
        if (str2 != null && str2.length() > 0) {
            return user.first_name;
        }
        str2 = user.last_name;
        return (str2 == null || str2.length() <= 0) ? str : user.last_name;
    }

    private void updatePrintingStrings() {
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (Entry entry : this.printingUsers.entrySet()) {
            long longValue = ((Long) entry.getKey()).longValue();
            ArrayList arrayList = (ArrayList) entry.getValue();
            int i = (int) longValue;
            String str = "IsTypingGroup";
            if (i > 0 || i == 0 || arrayList.size() == 1) {
                PrintingUser printingUser = (PrintingUser) arrayList.get(0);
                if (getUser(Integer.valueOf(printingUser.userId)) != null) {
                    SendMessageAction sendMessageAction = printingUser.action;
                    if (sendMessageAction instanceof TL_sendMessageRecordAudioAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsRecordingAudio", NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("RecordingAudio", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(1));
                    } else if ((sendMessageAction instanceof TL_sendMessageRecordRoundAction) || (sendMessageAction instanceof TL_sendMessageUploadRoundAction)) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsRecordingRound", NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("RecordingRound", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(4));
                    } else if (sendMessageAction instanceof TL_sendMessageUploadAudioAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingAudio", NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingAudio", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(2));
                    } else if ((sendMessageAction instanceof TL_sendMessageUploadVideoAction) || (sendMessageAction instanceof TL_sendMessageRecordVideoAction)) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingVideo", NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingVideoStatus", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(2));
                    } else if (sendMessageAction instanceof TL_sendMessageUploadDocumentAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingFile", NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingFile", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(2));
                    } else if (sendMessageAction instanceof TL_sendMessageUploadPhotoAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingPhoto", NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingPhoto", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(2));
                    } else if (sendMessageAction instanceof TL_sendMessageGamePlayAction) {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString("IsSendingGame", NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("SendingGame", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(3));
                    } else {
                        if (i < 0) {
                            longSparseArray.put(longValue, LocaleController.formatString(str, NUM, getUserNameForTyping(r8)));
                        } else {
                            longSparseArray.put(longValue, LocaleController.getString("Typing", NUM));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(0));
                    }
                }
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                Iterator it = arrayList.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    User user = getUser(Integer.valueOf(((PrintingUser) it.next()).userId));
                    if (user != null) {
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(", ");
                        }
                        stringBuilder.append(getUserNameForTyping(user));
                        i2++;
                        continue;
                    }
                    if (i2 == 2) {
                        break;
                    }
                }
                if (stringBuilder.length() != 0) {
                    if (i2 == 1) {
                        longSparseArray.put(longValue, LocaleController.formatString(str, NUM, stringBuilder.toString()));
                    } else if (arrayList.size() > 2) {
                        try {
                            longSparseArray.put(longValue, String.format(LocaleController.getPluralString("AndMoreTypingGroup", arrayList.size() - 2), new Object[]{stringBuilder.toString(), Integer.valueOf(arrayList.size() - 2)}));
                        } catch (Exception unused) {
                            longSparseArray.put(longValue, "LOC_ERR: AndMoreTypingGroup");
                        }
                    } else {
                        longSparseArray.put(longValue, LocaleController.formatString("AreTypingGroup", NUM, stringBuilder.toString()));
                    }
                    longSparseArray2.put(longValue, Integer.valueOf(0));
                }
            }
        }
        this.lastPrintingStringCount = longSparseArray.size();
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$iLPp_yBkE-A4gt0g9ezdFE0ctRI(this, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$updatePrintingStrings$111$MessagesController(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.printingStrings = longSparseArray;
        this.printingStringsTypes = longSparseArray2;
    }

    public void cancelTyping(int i, long j) {
        LongSparseArray longSparseArray = (LongSparseArray) this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    public void sendTyping(long j, int i, int i2) {
        if (j != 0) {
            LongSparseArray longSparseArray = (LongSparseArray) this.sendingTypings.get(i);
            if (longSparseArray == null || longSparseArray.get(j) == null) {
                if (longSparseArray == null) {
                    longSparseArray = new LongSparseArray();
                    this.sendingTypings.put(i, longSparseArray);
                }
                int i3 = (int) j;
                int i4 = (int) (j >> 32);
                int sendRequest;
                if (i3 != 0) {
                    TL_messages_setTyping tL_messages_setTyping = new TL_messages_setTyping();
                    tL_messages_setTyping.peer = getInputPeer(i3);
                    InputPeer inputPeer = tL_messages_setTyping.peer;
                    if (inputPeer instanceof TL_inputPeerChannel) {
                        Chat chat = getChat(Integer.valueOf(inputPeer.channel_id));
                        if (chat == null || !chat.megagroup) {
                            return;
                        }
                    }
                    if (tL_messages_setTyping.peer != null) {
                        if (i == 0) {
                            tL_messages_setTyping.action = new TL_sendMessageTypingAction();
                        } else if (i == 1) {
                            tL_messages_setTyping.action = new TL_sendMessageRecordAudioAction();
                        } else if (i == 2) {
                            tL_messages_setTyping.action = new TL_sendMessageCancelAction();
                        } else if (i == 3) {
                            tL_messages_setTyping.action = new TL_sendMessageUploadDocumentAction();
                        } else if (i == 4) {
                            tL_messages_setTyping.action = new TL_sendMessageUploadPhotoAction();
                        } else if (i == 5) {
                            tL_messages_setTyping.action = new TL_sendMessageUploadVideoAction();
                        } else if (i == 6) {
                            tL_messages_setTyping.action = new TL_sendMessageGamePlayAction();
                        } else if (i == 7) {
                            tL_messages_setTyping.action = new TL_sendMessageRecordRoundAction();
                        } else if (i == 8) {
                            tL_messages_setTyping.action = new TL_sendMessageUploadRoundAction();
                        } else if (i == 9) {
                            tL_messages_setTyping.action = new TL_sendMessageUploadAudioAction();
                        }
                        longSparseArray.put(j, Boolean.valueOf(true));
                        sendRequest = getConnectionsManager().sendRequest(tL_messages_setTyping, new -$$Lambda$MessagesController$RjYDQDDcuDAiorQIztSQGsS9lEI(this, i, j), 2);
                        if (i2 != 0) {
                            getConnectionsManager().bindRequestToGuid(sendRequest, i2);
                        }
                    }
                } else if (i == 0) {
                    EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i4));
                    byte[] bArr = encryptedChat.auth_key;
                    if (bArr != null && bArr.length > 1 && (encryptedChat instanceof TL_encryptedChat)) {
                        TL_messages_setEncryptedTyping tL_messages_setEncryptedTyping = new TL_messages_setEncryptedTyping();
                        tL_messages_setEncryptedTyping.peer = new TL_inputEncryptedChat();
                        TL_inputEncryptedChat tL_inputEncryptedChat = tL_messages_setEncryptedTyping.peer;
                        tL_inputEncryptedChat.chat_id = encryptedChat.id;
                        tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
                        tL_messages_setEncryptedTyping.typing = true;
                        longSparseArray.put(j, Boolean.valueOf(true));
                        sendRequest = getConnectionsManager().sendRequest(tL_messages_setEncryptedTyping, new -$$Lambda$MessagesController$MkxNDRCclze0zxnUPxUwBS8kS6E(this, i, j), 2);
                        if (i2 != 0) {
                            getConnectionsManager().bindRequestToGuid(sendRequest, i2);
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$sendTyping$113$MessagesController(int i, long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$JH5q5ddZepaC6s0Bbs-KnjtgJUc(this, i, j));
    }

    public /* synthetic */ void lambda$null$112$MessagesController(int i, long j) {
        LongSparseArray longSparseArray = (LongSparseArray) this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    public /* synthetic */ void lambda$sendTyping$115$MessagesController(int i, long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$4yHPvAiTt7rsoKgSwGpxRZ_1qkk(this, i, j));
    }

    public /* synthetic */ void lambda$null$114$MessagesController(int i, long j) {
        LongSparseArray longSparseArray = (LongSparseArray) this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    /* Access modifiers changed, original: protected */
    public void removeDeletedMessagesFromArray(long j, ArrayList<Message> arrayList) {
        int i = 0;
        int intValue = ((Integer) this.deletedHistory.get(j, Integer.valueOf(0))).intValue();
        if (intValue != 0) {
            int size = arrayList.size();
            while (i < size) {
                if (((Message) arrayList.get(i)).id <= intValue) {
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
        int i13;
        long j2 = j;
        int i14 = i;
        int i15 = i2;
        boolean z6 = z;
        int i16 = i4;
        int i17 = i5;
        int i18 = i6;
        int i19 = i7;
        boolean z7 = z3;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("load messages in chat ");
            stringBuilder.append(j2);
            stringBuilder.append(" count ");
            stringBuilder.append(i14);
            stringBuilder.append(" max_id ");
            stringBuilder.append(i15);
            stringBuilder.append(" cache ");
            stringBuilder.append(z6);
            stringBuilder.append(" mindate = ");
            stringBuilder.append(i16);
            stringBuilder.append(" guid ");
            stringBuilder.append(i17);
            stringBuilder.append(" load_type ");
            stringBuilder.append(i18);
            stringBuilder.append(" last_message_id ");
            stringBuilder.append(i19);
            stringBuilder.append(" scheduled ");
            stringBuilder.append(z7);
            stringBuilder.append(" index ");
            stringBuilder.append(i8);
            stringBuilder.append(" firstUnread ");
            stringBuilder.append(i9);
            stringBuilder.append(" unread_count ");
            stringBuilder.append(i10);
            stringBuilder.append(" last_date ");
            stringBuilder.append(i11);
            stringBuilder.append(" queryFromServer ");
            stringBuilder.append(z4);
            FileLog.d(stringBuilder.toString());
        } else {
            int i20 = i8;
            i13 = i9;
            int i21 = i10;
            boolean z8 = z4;
        }
        int i22 = (int) j2;
        if (z6 || i22 == 0) {
            i13 = i3;
            i19 = i17;
            getMessagesStorage().getMessages(j, i, i2, i3, i4, i5, i6, z2, z3, i8);
        } else if (z7) {
            TLObject tL_messages_getScheduledHistory = new TL_messages_getScheduledHistory();
            tL_messages_getScheduledHistory.peer = getInputPeer(i22);
            tL_messages_getScheduledHistory.hash = i16;
            -$$Lambda$MessagesController$S2RzKO37N-uNhyKD9C9FqwrKU48 -__lambda_messagescontroller_s2rzko37n-unhykd9c9fqwrku48 = r0;
            ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
            TLObject tLObject = tL_messages_getScheduledHistory;
            -$$Lambda$MessagesController$S2RzKO37N-uNhyKD9C9FqwrKU48 -__lambda_messagescontroller_s2rzko37n-unhykd9c9fqwrku482 = new -$$Lambda$MessagesController$S2RzKO37N-uNhyKD9C9FqwrKU48(this, i2, i3, j, i, i5, i9, i7, i10, i11, i6, z2, i8, z4, i12);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance.sendRequest(tLObject, -__lambda_messagescontroller_s2rzko37n-unhykd9c9fqwrku48), i5);
        } else if (z5 && ((i18 == 3 || i18 == 2) && i19 == 0)) {
            TLObject tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
            InputPeer inputPeer = getInputPeer(i22);
            TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
            tL_inputDialogPeer.peer = inputPeer;
            tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
            ConnectionsManager connectionsManager = getConnectionsManager();
            TLObject tLObject2 = tL_messages_getPeerDialogs;
            -$$Lambda$MessagesController$cbrFJ0ZBYE9hidDT5a7yVuESxuI -__lambda_messagescontroller_cbrfj0zbye9hiddt5a7yvuesxui = r0;
            -$$Lambda$MessagesController$cbrFJ0ZBYE9hidDT5a7yVuESxuI -__lambda_messagescontroller_cbrfj0zbye9hiddt5a7yvuesxui2 = new -$$Lambda$MessagesController$cbrFJ0ZBYE9hidDT5a7yVuESxuI(this, j, i, i2, i3, i4, i5, i6, z2, i8, i9, i11, z4);
            connectionsManager.sendRequest(tLObject2, -__lambda_messagescontroller_cbrfj0zbye9hiddt5a7yvuesxui);
        } else {
            TLObject tL_messages_getHistory = new TL_messages_getHistory();
            tL_messages_getHistory.peer = getInputPeer(i22);
            if (i18 == 4) {
                tL_messages_getHistory.add_offset = (-i14) + 5;
            } else if (i18 == 3) {
                tL_messages_getHistory.add_offset = (-i14) / 2;
            } else if (i18 == 1) {
                tL_messages_getHistory.add_offset = (-i14) - 1;
            } else if (i18 == 2 && i15 != 0) {
                tL_messages_getHistory.add_offset = (-i14) + 6;
            } else if (i22 < 0 && i15 != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-i22)))) {
                tL_messages_getHistory.add_offset = -1;
                tL_messages_getHistory.limit++;
            }
            tL_messages_getHistory.limit = i14;
            tL_messages_getHistory.offset_id = i15;
            tL_messages_getHistory.offset_date = i3;
            ConnectionsManager connectionsManager2 = getConnectionsManager();
            -$$Lambda$MessagesController$DTycE95B3vS8QWCwwNi_-eudjcQ -__lambda_messagescontroller_dtyce95b3vs8qwcwwni_-eudjcq = r0;
            TLObject tLObject3 = tL_messages_getHistory;
            -$$Lambda$MessagesController$DTycE95B3vS8QWCwwNi_-eudjcQ -__lambda_messagescontroller_dtyce95b3vs8qwcwwni_-eudjcq2 = new -$$Lambda$MessagesController$DTycE95B3vS8QWCwwNi_-eudjcQ(this, j, i, i2, i3, i5, i9, i7, i10, i11, i6, z2, i8, z4, i12);
            getConnectionsManager().bindRequestToGuid(connectionsManager2.sendRequest(tLObject3, -__lambda_messagescontroller_dtyce95b3vs8qwcwwni_-eudjcq), i5);
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$116$MessagesController(int i, int i2, long j, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, int i10, boolean z2, int i11, TLObject tLObject, TL_error tL_error) {
        int i12 = i2;
        if (tLObject != null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            if (!(messages_messages instanceof TL_messages_messagesNotModified)) {
                int i13;
                if (i12 == 0 || messages_messages.messages.isEmpty()) {
                    i13 = i;
                } else {
                    ArrayList arrayList = messages_messages.messages;
                    int i14 = ((Message) arrayList.get(arrayList.size() - 1)).id;
                    for (int size = messages_messages.messages.size() - 1; size >= 0; size--) {
                        Message message = (Message) messages_messages.messages.get(size);
                        if (message.date > i12) {
                            i14 = message.id;
                            break;
                        }
                    }
                    i13 = i14;
                }
                processLoadedMessages(messages_messages, j, i3, i13, i2, false, i4, i5, i6, i7, i8, i9, z, false, true, i10, z2, i11);
            }
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$117$MessagesController(long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, int i8, int i9, boolean z2, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty()) {
                Dialog dialog = (Dialog) tL_messages_peerDialogs.dialogs.get(0);
                if (dialog.top_message != 0) {
                    TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
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

    public /* synthetic */ void lambda$loadMessagesInternal$118$MessagesController(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, int i10, boolean z2, int i11, TLObject tLObject, TL_error tL_error) {
        int i12 = i3;
        if (tLObject != null) {
            int i13;
            messages_Messages messages_messages = (messages_Messages) tLObject;
            removeDeletedMessagesFromArray(j, messages_messages.messages);
            if (messages_messages.messages.size() > i) {
                messages_messages.messages.remove(0);
            }
            if (i12 == 0 || messages_messages.messages.isEmpty()) {
                i13 = i2;
            } else {
                ArrayList arrayList = messages_messages.messages;
                int i14 = ((Message) arrayList.get(arrayList.size() - 1)).id;
                for (i13 = messages_messages.messages.size() - 1; i13 >= 0; i13--) {
                    Message message = (Message) messages_messages.messages.get(i13);
                    if (message.date > i12) {
                        i14 = message.id;
                        break;
                    }
                }
                i13 = i14;
            }
            processLoadedMessages(messages_messages, j, i, i13, i3, false, i4, i5, i6, i7, i8, i9, z, false, false, i10, z2, i11);
        }
    }

    public void reloadWebPages(long j, HashMap<String, ArrayList<MessageObject>> hashMap, boolean z) {
        HashMap hashMap2 = z ? this.reloadingScheduledWebpages : this.reloadingWebpages;
        LongSparseArray longSparseArray = z ? this.reloadingScheduledWebpagesPending : this.reloadingWebpagesPending;
        for (Entry entry : hashMap.entrySet()) {
            String str = (String) entry.getKey();
            ArrayList arrayList = (ArrayList) entry.getValue();
            ArrayList arrayList2 = (ArrayList) hashMap2.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
                hashMap2.put(str, arrayList2);
            }
            arrayList2.addAll(arrayList);
            TL_messages_getWebPagePreview tL_messages_getWebPagePreview = new TL_messages_getWebPagePreview();
            tL_messages_getWebPagePreview.message = str;
            getConnectionsManager().sendRequest(tL_messages_getWebPagePreview, new -$$Lambda$MessagesController$hOrdLKzCHL7qE4UoG_vkHeiNYwY(this, hashMap2, str, longSparseArray, j, z));
        }
    }

    public /* synthetic */ void lambda$reloadWebPages$120$MessagesController(HashMap hashMap, String str, LongSparseArray longSparseArray, long j, boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$5ywQNuumt5vvqdnbYt-NCCuCv8U(this, hashMap, str, tLObject, longSparseArray, j, z));
    }

    public /* synthetic */ void lambda$null$119$MessagesController(HashMap hashMap, String str, TLObject tLObject, LongSparseArray longSparseArray, long j, boolean z) {
        TLObject tLObject2 = tLObject;
        ArrayList arrayList = (ArrayList) hashMap.remove(str);
        if (arrayList != null) {
            messages_Messages tL_messages_messages = new TL_messages_messages();
            if (tLObject2 instanceof TL_messageMediaWebPage) {
                TL_messageMediaWebPage tL_messageMediaWebPage = (TL_messageMediaWebPage) tLObject2;
                WebPage webPage = tL_messageMediaWebPage.webpage;
                if ((webPage instanceof TL_webPage) || (webPage instanceof TL_webPageEmpty)) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        ((MessageObject) arrayList.get(i)).messageOwner.media.webpage = tL_messageMediaWebPage.webpage;
                        if (i == 0) {
                            ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(i)).messageOwner);
                        }
                        tL_messages_messages.messages.add(((MessageObject) arrayList.get(i)).messageOwner);
                    }
                } else {
                    LongSparseArray longSparseArray2 = longSparseArray;
                    longSparseArray.put(webPage.id, arrayList);
                }
            } else {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    ((MessageObject) arrayList.get(i2)).messageOwner.media.webpage = new TL_webPageEmpty();
                    tL_messages_messages.messages.add(((MessageObject) arrayList.get(i2)).messageOwner);
                }
            }
            if (!tL_messages_messages.messages.isEmpty()) {
                getMessagesStorage().putMessages(tL_messages_messages, j, -2, 0, false, z);
                getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList);
            }
        }
    }

    public void processLoadedMessages(messages_Messages messages_messages, long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, int i8, int i9, boolean z2, boolean z3, boolean z4, int i10, boolean z5, int i11) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processLoadedMessages size ");
            stringBuilder.append(messages_messages.messages.size());
            stringBuilder.append(" in chat ");
            stringBuilder.append(j);
            stringBuilder.append(" count ");
            stringBuilder.append(i);
            stringBuilder.append(" max_id ");
            stringBuilder.append(i2);
            stringBuilder.append(" cache ");
            stringBuilder.append(z);
            stringBuilder.append(" guid ");
            stringBuilder.append(i4);
            stringBuilder.append(" load_type ");
            stringBuilder.append(i9);
            stringBuilder.append(" last_message_id ");
            stringBuilder.append(i6);
            stringBuilder.append(" isChannel ");
            stringBuilder.append(z2);
            stringBuilder.append(" index ");
            stringBuilder.append(i10);
            stringBuilder.append(" firstUnread ");
            stringBuilder.append(i5);
            stringBuilder.append(" unread_count ");
            stringBuilder.append(i7);
            stringBuilder.append(" last_date ");
            stringBuilder.append(i8);
            stringBuilder.append(" queryFromServer ");
            stringBuilder.append(z5);
            FileLog.d(stringBuilder.toString());
        } else {
            messages_Messages messages_messages2 = messages_messages;
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$u7NBKcciX4Bdp0eVOizI37kmoL4(this, messages_messages, j, z4, z, i, i9, z5, i5, i2, i3, i4, i6, z2, i10, i7, i8, i11, z3));
    }

    /* JADX WARNING: Removed duplicated region for block: B:152:0x030b A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x030b A:{SYNTHETIC} */
    /* JADX WARNING: Missing block: B:117:0x02b0, code skipped:
            if (r3[0] < (byte) 108) goto L_0x02b7;
     */
    public /* synthetic */ void lambda$processLoadedMessages$124$MessagesController(org.telegram.tgnet.TLRPC.messages_Messages r26, long r27, boolean r29, boolean r30, int r31, int r32, boolean r33, int r34, int r35, int r36, int r37, int r38, boolean r39, int r40, int r41, int r42, int r43, boolean r44) {
        /*
        r25 = this;
        r15 = r25;
        r14 = r26;
        r12 = r27;
        r11 = r29;
        r0 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
        r10 = 1;
        if (r0 == 0) goto L_0x006d;
    L_0x000d:
        r0 = (int) r12;
        r6 = -r0;
        if (r11 != 0) goto L_0x004a;
    L_0x0011:
        r0 = r15.channelsPts;
        r0 = r0.get(r6);
        if (r0 != 0) goto L_0x004a;
    L_0x0019:
        r0 = r25.getMessagesStorage();
        r0 = r0.getChannelPtsSync(r6);
        if (r0 != 0) goto L_0x004a;
    L_0x0023:
        r0 = r15.channelsPts;
        r1 = r14.pts;
        r0.put(r6, r1);
        r0 = r15.needShortPollChannels;
        r0 = r0.indexOfKey(r6);
        if (r0 < 0) goto L_0x0045;
    L_0x0032:
        r0 = r15.shortPollChannels;
        r0 = r0.indexOfKey(r6);
        if (r0 >= 0) goto L_0x0045;
    L_0x003a:
        r2 = 2;
        r3 = 0;
        r5 = 0;
        r0 = r25;
        r1 = r6;
        r0.getChannelDifference(r1, r2, r3, r5);
        goto L_0x0048;
    L_0x0045:
        r15.getChannelDifference(r6);
    L_0x0048:
        r0 = 1;
        goto L_0x004b;
    L_0x004a:
        r0 = 0;
    L_0x004b:
        r1 = 0;
    L_0x004c:
        r2 = r14.chats;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x006a;
    L_0x0054:
        r2 = r14.chats;
        r2 = r2.get(r1);
        r2 = (org.telegram.tgnet.TLRPC.Chat) r2;
        r3 = r2.id;
        if (r3 != r6) goto L_0x0067;
    L_0x0060:
        r1 = r2.megagroup;
        r19 = r0;
        r20 = r1;
        goto L_0x0071;
    L_0x0067:
        r1 = r1 + 1;
        goto L_0x004c;
    L_0x006a:
        r19 = r0;
        goto L_0x006f;
    L_0x006d:
        r19 = 0;
    L_0x006f:
        r20 = 0;
    L_0x0071:
        r0 = (int) r12;
        r1 = 32;
        r1 = r12 >> r1;
        r2 = (int) r1;
        if (r30 != 0) goto L_0x007e;
    L_0x0079:
        r1 = r14.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r1);
    L_0x007e:
        if (r2 == r10) goto L_0x0138;
    L_0x0080:
        if (r0 == 0) goto L_0x0138;
    L_0x0082:
        if (r30 == 0) goto L_0x0138;
    L_0x0084:
        r0 = r14.messages;
        r0 = r0.size();
        r1 = 0;
        if (r0 == 0) goto L_0x00ac;
    L_0x008e:
        if (r11 == 0) goto L_0x0138;
    L_0x0090:
        r3 = android.os.SystemClock.uptimeMillis();
        r0 = r15.lastScheduledServerQueryTime;
        r5 = java.lang.Long.valueOf(r1);
        r0 = r0.get(r12, r5);
        r0 = (java.lang.Long) r0;
        r5 = r0.longValue();
        r3 = r3 - r5;
        r5 = 60000; // 0xea60 float:8.4078E-41 double:2.9644E-319;
        r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r0 <= 0) goto L_0x0138;
    L_0x00ac:
        if (r11 == 0) goto L_0x00ff;
    L_0x00ae:
        r0 = r15.lastScheduledServerQueryTime;
        r3 = android.os.SystemClock.uptimeMillis();
        r3 = java.lang.Long.valueOf(r3);
        r0.put(r12, r3);
        r0 = r14.messages;
        r0 = r0.size();
        r2 = r1;
        r1 = 0;
    L_0x00c3:
        if (r1 >= r0) goto L_0x00f9;
    L_0x00c5:
        r4 = r14.messages;
        r4 = r4.get(r1);
        r4 = (org.telegram.tgnet.TLRPC.Message) r4;
        r5 = r4.id;
        if (r5 >= 0) goto L_0x00d2;
    L_0x00d1:
        goto L_0x00f5;
    L_0x00d2:
        r6 = 20261; // 0x4var_ float:2.8392E-41 double:1.00103E-319;
        r2 = r2 * r6;
        r16 = NUM; // 0x80000000 float:-0.0 double:1.0609978955E-314;
        r2 = r2 + r16;
        r9 = (long) r5;
        r2 = r2 + r9;
        r2 = r2 % r16;
        r2 = r2 * r6;
        r2 = r2 + r16;
        r5 = r4.edit_date;
        r8 = (long) r5;
        r2 = r2 + r8;
        r2 = r2 % r16;
        r2 = r2 * r6;
        r2 = r2 + r16;
        r4 = r4.date;
        r4 = (long) r4;
        r2 = r2 + r4;
        r2 = r2 % r16;
    L_0x00f5:
        r1 = r1 + 1;
        r10 = 1;
        goto L_0x00c3;
    L_0x00f9:
        r0 = (int) r2;
        r10 = 1;
        r0 = r0 - r10;
        r16 = r0;
        goto L_0x0101;
    L_0x00ff:
        r16 = 0;
    L_0x0101:
        r22 = new org.telegram.messenger.-$$Lambda$MessagesController$HgB8ehS8TV3c0okoQTq46nE_lUs;
        r0 = r22;
        r1 = r25;
        r2 = r27;
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r9 = r36;
        r10 = r16;
        r11 = r37;
        r12 = r38;
        r13 = r39;
        r14 = r29;
        r15 = r40;
        r16 = r41;
        r17 = r42;
        r18 = r43;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r22);
        r8 = r26;
        r0 = r8.messages;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0139;
    L_0x0137:
        return;
    L_0x0138:
        r8 = r14;
    L_0x0139:
        r9 = new android.util.SparseArray;
        r9.<init>();
        r10 = new android.util.SparseArray;
        r10.<init>();
        r0 = 0;
    L_0x0144:
        r1 = r8.users;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x015c;
    L_0x014c:
        r1 = r8.users;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r2 = r1.id;
        r9.put(r2, r1);
        r0 = r0 + 1;
        goto L_0x0144;
    L_0x015c:
        r0 = 0;
    L_0x015d:
        r1 = r8.chats;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x0175;
    L_0x0165:
        r1 = r8.chats;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Chat) r1;
        r2 = r1.id;
        r10.put(r2, r1);
        r0 = r0 + 1;
        goto L_0x015d;
    L_0x0175:
        r0 = r8.messages;
        r11 = r0.size();
        r15 = r25;
        if (r30 != 0) goto L_0x0246;
    L_0x017f:
        r0 = r15.dialogs_read_inbox_max;
        r1 = java.lang.Long.valueOf(r27);
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        if (r0 != 0) goto L_0x01a6;
    L_0x018d:
        r0 = r25.getMessagesStorage();
        r12 = r27;
        r14 = 0;
        r0 = r0.getDialogReadMax(r14, r12);
        r0 = java.lang.Integer.valueOf(r0);
        r1 = r15.dialogs_read_inbox_max;
        r2 = java.lang.Long.valueOf(r27);
        r1.put(r2, r0);
        goto L_0x01a9;
    L_0x01a6:
        r12 = r27;
        r14 = 0;
    L_0x01a9:
        r1 = r15.dialogs_read_outbox_max;
        r2 = java.lang.Long.valueOf(r27);
        r1 = r1.get(r2);
        r1 = (java.lang.Integer) r1;
        if (r1 != 0) goto L_0x01ce;
    L_0x01b7:
        r1 = r25.getMessagesStorage();
        r7 = 1;
        r1 = r1.getDialogReadMax(r7, r12);
        r1 = java.lang.Integer.valueOf(r1);
        r2 = r15.dialogs_read_outbox_max;
        r3 = java.lang.Long.valueOf(r27);
        r2.put(r3, r1);
        goto L_0x01cf;
    L_0x01ce:
        r7 = 1;
    L_0x01cf:
        r2 = 0;
    L_0x01d0:
        if (r2 >= r11) goto L_0x022f;
    L_0x01d2:
        r3 = r8.messages;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.Message) r3;
        if (r20 == 0) goto L_0x01e3;
    L_0x01dc:
        r4 = r3.flags;
        r5 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = r4 | r5;
        r3.flags = r4;
    L_0x01e3:
        r6 = r29;
        if (r6 != 0) goto L_0x022c;
    L_0x01e7:
        r4 = r3.action;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r5 == 0) goto L_0x0208;
    L_0x01ed:
        r4 = r4.user_id;
        r4 = r9.get(r4);
        r4 = (org.telegram.tgnet.TLRPC.User) r4;
        if (r4 == 0) goto L_0x0208;
    L_0x01f7:
        r4 = r4.bot;
        if (r4 == 0) goto L_0x0208;
    L_0x01fb:
        r4 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r4.<init>();
        r3.reply_markup = r4;
        r4 = r3.flags;
        r4 = r4 | 64;
        r3.flags = r4;
    L_0x0208:
        r4 = r3.action;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r5 != 0) goto L_0x0228;
    L_0x020e:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r4 == 0) goto L_0x0213;
    L_0x0212:
        goto L_0x0228;
    L_0x0213:
        r4 = r3.out;
        if (r4 == 0) goto L_0x0219;
    L_0x0217:
        r4 = r1;
        goto L_0x021a;
    L_0x0219:
        r4 = r0;
    L_0x021a:
        r4 = r4.intValue();
        r5 = r3.id;
        if (r4 >= r5) goto L_0x0224;
    L_0x0222:
        r4 = 1;
        goto L_0x0225;
    L_0x0224:
        r4 = 0;
    L_0x0225:
        r3.unread = r4;
        goto L_0x022c;
    L_0x0228:
        r3.unread = r14;
        r3.media_unread = r14;
    L_0x022c:
        r2 = r2 + 1;
        goto L_0x01d0;
    L_0x022f:
        r6 = r29;
        r0 = r25.getMessagesStorage();
        r1 = r26;
        r2 = r27;
        r4 = r32;
        r5 = r35;
        r14 = r6;
        r6 = r19;
        r7 = r29;
        r0.putMessages(r1, r2, r4, r5, r6, r7);
        goto L_0x024a;
    L_0x0246:
        r12 = r27;
        r14 = r29;
    L_0x024a:
        r7 = new java.util.ArrayList;
        r7.<init>();
        r0 = new java.util.ArrayList;
        r0.<init>();
        r6 = new java.util.HashMap;
        r6.<init>();
        r5 = 0;
    L_0x025a:
        if (r5 >= r11) goto L_0x0313;
    L_0x025c:
        r1 = r8.messages;
        r1 = r1.get(r5);
        r4 = r1;
        r4 = (org.telegram.tgnet.TLRPC.Message) r4;
        r4.dialog_id = r12;
        r3 = new org.telegram.messenger.MessageObject;
        r2 = r15.currentAccount;
        r16 = 1;
        r1 = r3;
        r23 = r3;
        r3 = r4;
        r24 = r4;
        r4 = r9;
        r17 = r5;
        r5 = r10;
        r15 = r6;
        r6 = r16;
        r1.<init>(r2, r3, r4, r5, r6);
        r1 = r23;
        r1.scheduled = r14;
        r7.add(r1);
        if (r30 == 0) goto L_0x0309;
    L_0x0286:
        r2 = r24;
        r3 = r2.legacy;
        r4 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r3 == 0) goto L_0x029c;
    L_0x028e:
        r3 = r2.layer;
        if (r3 >= r4) goto L_0x029c;
    L_0x0292:
        r3 = r2.id;
        r3 = java.lang.Integer.valueOf(r3);
        r0.add(r3);
        goto L_0x02c1;
    L_0x029c:
        r3 = r2.media;
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
        if (r5 == 0) goto L_0x02c1;
    L_0x02a2:
        r3 = r3.bytes;
        if (r3 == 0) goto L_0x02c1;
    L_0x02a6:
        r5 = r3.length;
        if (r5 == 0) goto L_0x02b5;
    L_0x02a9:
        r5 = r3.length;
        r6 = 1;
        if (r5 != r6) goto L_0x02b3;
    L_0x02ad:
        r5 = 0;
        r3 = r3[r5];
        if (r3 >= r4) goto L_0x02c3;
    L_0x02b2:
        goto L_0x02b7;
    L_0x02b3:
        r5 = 0;
        goto L_0x02c3;
    L_0x02b5:
        r5 = 0;
        r6 = 1;
    L_0x02b7:
        r3 = r2.id;
        r3 = java.lang.Integer.valueOf(r3);
        r0.add(r3);
        goto L_0x02c3;
    L_0x02c1:
        r5 = 0;
        r6 = 1;
    L_0x02c3:
        r3 = r2.media;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r4 == 0) goto L_0x030b;
    L_0x02c9:
        r3 = r3.webpage;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;
        if (r4 == 0) goto L_0x02e5;
    L_0x02cf:
        r3 = r3.date;
        r4 = r25.getConnectionsManager();
        r4 = r4.getCurrentTime();
        if (r3 > r4) goto L_0x02e5;
    L_0x02db:
        r1 = r2.id;
        r1 = java.lang.Integer.valueOf(r1);
        r0.add(r1);
        goto L_0x030b;
    L_0x02e5:
        r3 = r2.media;
        r3 = r3.webpage;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
        if (r4 == 0) goto L_0x030b;
    L_0x02ed:
        r3 = r3.url;
        r3 = r15.get(r3);
        r3 = (java.util.ArrayList) r3;
        if (r3 != 0) goto L_0x0305;
    L_0x02f7:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r2 = r2.media;
        r2 = r2.webpage;
        r2 = r2.url;
        r15.put(r2, r3);
    L_0x0305:
        r3.add(r1);
        goto L_0x030b;
    L_0x0309:
        r5 = 0;
        r6 = 1;
    L_0x030b:
        r1 = r17 + 1;
        r5 = r1;
        r6 = r15;
        r15 = r25;
        goto L_0x025a;
    L_0x0313:
        r15 = r6;
        r22 = new org.telegram.messenger.-$$Lambda$MessagesController$wg-aA8iCrSooaLRWqrgjxDJgHQs;
        r20 = r0;
        r0 = r22;
        r1 = r25;
        r2 = r26;
        r3 = r30;
        r4 = r29;
        r5 = r33;
        r6 = r32;
        r11 = r7;
        r7 = r34;
        r8 = r31;
        r9 = r27;
        r12 = r38;
        r13 = r41;
        r14 = r42;
        r21 = r15;
        r15 = r44;
        r16 = r37;
        r17 = r40;
        r18 = r35;
        r19 = r43;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r22);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedMessages$124$MessagesController(org.telegram.tgnet.TLRPC$messages_Messages, long, boolean, boolean, int, int, boolean, int, int, int, int, int, boolean, int, int, int, int, boolean):void");
    }

    public /* synthetic */ void lambda$null$121$MessagesController(long j, int i, int i2, boolean z, int i3, int i4, int i5, int i6, int i7, int i8, boolean z2, boolean z3, int i9, int i10, int i11, int i12) {
        int i13 = (i2 == 2 && z) ? i3 : i4;
        loadMessages(j, i, i13, i5, false, i6, i7, i2, i8, z2, z3, i9, i3, i10, i11, z, i12);
    }

    public /* synthetic */ void lambda$null$123$MessagesController(messages_Messages messages_messages, boolean z, boolean z2, boolean z3, int i, int i2, int i3, long j, ArrayList arrayList, int i4, int i5, int i6, boolean z4, int i7, int i8, int i9, int i10, ArrayList arrayList2, HashMap hashMap) {
        int i11;
        int i12;
        MessagesController messagesController;
        boolean z5;
        long j2;
        messages_Messages messages_messages2 = messages_messages;
        boolean z6 = z;
        boolean z7 = z2;
        long j3 = j;
        putUsers(messages_messages2.users, z6);
        putChats(messages_messages2.chats, z6);
        int i13;
        if (z7) {
            i13 = i;
            i11 = 0;
        } else {
            int i14;
            int i15;
            i13 = i;
            if (z3 && i13 == 2) {
                i14 = Integer.MAX_VALUE;
                for (i12 = 0; i12 < messages_messages2.messages.size(); i12++) {
                    Message message = (Message) messages_messages2.messages.get(i12);
                    if (!message.out || message.from_scheduled) {
                        i11 = message.id;
                        if (i11 > i2 && i11 < r5) {
                            i14 = i11;
                        }
                    } else {
                        i15 = i2;
                    }
                }
                i15 = i2;
            } else {
                i15 = i2;
                i14 = Integer.MAX_VALUE;
            }
            i11 = i14 == Integer.MAX_VALUE ? i15 : i14;
        }
        i12 = i3;
        if (z7 && i12 == 1) {
            getNotificationCenter().postNotificationName(NotificationCenter.scheduledMessagesUpdated, Long.valueOf(j), Integer.valueOf(arrayList.size()));
        }
        if (((int) j3) != 0) {
            getMediaDataController().loadReplyMessagesForMessages(arrayList, j, z2, new -$$Lambda$MessagesController$2oa59EuoFdVEjVtLd4W7MN2k9-g(this, j, i3, arrayList, z, i11, i4, i5, i6, i, z4, i7, i8, i9, i10, z2));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i3), arrayList, Boolean.valueOf(z), Integer.valueOf(i11), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i), Boolean.valueOf(z4), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i9), Integer.valueOf(i10), Boolean.valueOf(z2));
        }
        if (arrayList2.isEmpty()) {
            messagesController = this;
            z5 = z2;
            j2 = j;
        } else {
            messagesController = this;
            z5 = z2;
            j2 = j;
            messagesController.reloadMessages(arrayList2, j2, z5);
        }
        if (!hashMap.isEmpty()) {
            messagesController.reloadWebPages(j2, hashMap, z5);
        }
    }

    public /* synthetic */ void lambda$null$122$MessagesController(long j, int i, ArrayList arrayList, boolean z, int i2, int i3, int i4, int i5, int i6, boolean z2, int i7, int i8, int i9, int i10, boolean z3) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i), arrayList, Boolean.valueOf(z), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Boolean.valueOf(z2), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i9), Integer.valueOf(i10), Boolean.valueOf(z3));
    }

    public void loadHintDialogs() {
        if (this.hintDialogs.isEmpty() && !TextUtils.isEmpty(this.installReferer)) {
            TL_help_getRecentMeUrls tL_help_getRecentMeUrls = new TL_help_getRecentMeUrls();
            tL_help_getRecentMeUrls.referer = this.installReferer;
            getConnectionsManager().sendRequest(tL_help_getRecentMeUrls, new -$$Lambda$MessagesController$Vd0MyZ-XGsgi_hRvHOtKEvj4jaE(this));
        }
    }

    public /* synthetic */ void lambda$loadHintDialogs$126$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$fKtPxjUO0MO9_QVdDv94oKFhhHI(this, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$125$MessagesController(TLObject tLObject) {
        TL_help_recentMeUrls tL_help_recentMeUrls = (TL_help_recentMeUrls) tLObject;
        putUsers(tL_help_recentMeUrls.users, false);
        putChats(tL_help_recentMeUrls.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(tL_help_recentMeUrls.urls);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private TL_dialogFolder ensureFolderDialogExists(int i, boolean[] zArr) {
        if (i == 0) {
            return null;
        }
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        Dialog dialog = (Dialog) this.dialogs_dict.get(makeFolderDialogId);
        if (dialog instanceof TL_dialogFolder) {
            if (zArr != null) {
                zArr[0] = false;
            }
            return (TL_dialogFolder) dialog;
        }
        if (zArr != null) {
            zArr[0] = true;
        }
        TL_dialogFolder tL_dialogFolder = new TL_dialogFolder();
        tL_dialogFolder.id = makeFolderDialogId;
        tL_dialogFolder.peer = new TL_peerUser();
        tL_dialogFolder.folder = new TL_folder();
        TL_folder tL_folder = tL_dialogFolder.folder;
        tL_folder.id = i;
        tL_folder.title = LocaleController.getString("ArchivedChats", NUM);
        tL_dialogFolder.pinned = true;
        int i2 = 0;
        for (i = 0; i < this.allDialogs.size(); i++) {
            Dialog dialog2 = (Dialog) this.allDialogs.get(i);
            if (!dialog2.pinned) {
                break;
            }
            i2 = Math.max(dialog2.pinnedNum, i2);
        }
        tL_dialogFolder.pinnedNum = i2 + 1;
        TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
        tL_messages_dialogs.dialogs.add(tL_dialogFolder);
        getMessagesStorage().putDialogs(tL_messages_dialogs, 1);
        this.dialogs_dict.put(makeFolderDialogId, tL_dialogFolder);
        this.allDialogs.add(0, tL_dialogFolder);
        return tL_dialogFolder;
    }

    private void removeFolder(int i) {
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        Dialog dialog = (Dialog) this.dialogs_dict.get(makeFolderDialogId);
        if (dialog != null) {
            this.dialogs_dict.remove(makeFolderDialogId);
            this.allDialogs.remove(dialog);
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            getNotificationCenter().postNotificationName(NotificationCenter.folderBecomeEmpty, Integer.valueOf(i));
        }
    }

    /* Access modifiers changed, original: protected */
    public void onFolderEmpty(int i) {
        if (getUserConfig().getDialogLoadOffsets(i)[0] == Integer.MAX_VALUE) {
            removeFolder(i);
            return;
        }
        loadDialogs(i, 0, 10, false, new -$$Lambda$MessagesController$AV4EQ_8mh3ctt0OFIffEg5ZTyuU(this, i));
    }

    public /* synthetic */ void lambda$onFolderEmpty$127$MessagesController(int i) {
        removeFolder(i);
    }

    public void checkIfFolderEmpty(int i) {
        if (i != 0) {
            getMessagesStorage().checkIfFolderEmpty(i);
        }
    }

    public int addDialogToFolder(long j, int i, int i2, long j2) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(Long.valueOf(j));
        return addDialogToFolder(arrayList, i, i2, null, j2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0145  */
    public int addDialogToFolder(java.util.ArrayList<java.lang.Long> r22, int r23, int r24, java.util.ArrayList<org.telegram.tgnet.TLRPC.TL_inputFolderPeer> r25, long r26) {
        /*
        r21 = this;
        r1 = r21;
        r8 = r23;
        r0 = r24;
        r9 = new org.telegram.tgnet.TLRPC$TL_folders_editPeerFolders;
        r9.<init>();
        r10 = 0;
        r13 = 1;
        r14 = 0;
        r2 = (r26 > r10 ? 1 : (r26 == r10 ? 0 : -1));
        if (r2 != 0) goto L_0x0116;
    L_0x0013:
        r2 = r21.getUserConfig();
        r15 = r2.getClientUserId();
        r7 = r22.size();
        r2 = 0;
        r3 = 0;
        r5 = 0;
        r16 = 0;
    L_0x0024:
        if (r5 >= r7) goto L_0x00ba;
    L_0x0026:
        r6 = r22;
        r4 = r6.get(r5);
        r4 = (java.lang.Long) r4;
        r10 = r4.longValue();
        r4 = org.telegram.messenger.DialogObject.isPeerDialogId(r10);
        if (r4 != 0) goto L_0x003f;
    L_0x0038:
        r4 = org.telegram.messenger.DialogObject.isSecretDialogId(r10);
        if (r4 != 0) goto L_0x003f;
    L_0x003e:
        goto L_0x005e;
    L_0x003f:
        if (r8 != r13) goto L_0x0054;
    L_0x0041:
        r12 = (long) r15;
        r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x005e;
    L_0x0046:
        r12 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x005e;
    L_0x004d:
        r4 = r1.isProxyDialog(r10, r14);
        if (r4 == 0) goto L_0x0054;
    L_0x0053:
        goto L_0x005e;
    L_0x0054:
        r4 = r1.dialogs_dict;
        r4 = r4.get(r10);
        r4 = (org.telegram.tgnet.TLRPC.Dialog) r4;
        if (r4 != 0) goto L_0x0064;
    L_0x005e:
        r19 = r5;
        r20 = r7;
        r12 = 1;
        goto L_0x00b1;
    L_0x0064:
        r4.folder_id = r8;
        if (r0 <= 0) goto L_0x006e;
    L_0x0068:
        r12 = 1;
        r4.pinned = r12;
        r4.pinnedNum = r0;
        goto L_0x0073;
    L_0x006e:
        r12 = 1;
        r4.pinned = r14;
        r4.pinnedNum = r14;
    L_0x0073:
        if (r3 != 0) goto L_0x007a;
    L_0x0075:
        r3 = new boolean[r12];
        r1.ensureFolderDialogExists(r8, r3);
    L_0x007a:
        r13 = r3;
        r2 = org.telegram.messenger.DialogObject.isSecretDialogId(r10);
        if (r2 == 0) goto L_0x0092;
    L_0x0081:
        r2 = r21.getMessagesStorage();
        r3 = 0;
        r4 = 0;
        r19 = r5;
        r5 = r10;
        r20 = r7;
        r7 = r23;
        r2.setDialogsFolderId(r3, r4, r5, r7);
        goto L_0x00af;
    L_0x0092:
        r19 = r5;
        r20 = r7;
        r2 = new org.telegram.tgnet.TLRPC$TL_inputFolderPeer;
        r2.<init>();
        r2.folder_id = r8;
        r3 = (int) r10;
        r3 = r1.getInputPeer(r3);
        r2.peer = r3;
        r3 = r9.folder_peers;
        r3.add(r2);
        r2 = r2.getObjectSize();
        r16 = r16 + r2;
    L_0x00af:
        r3 = r13;
        r2 = 1;
    L_0x00b1:
        r5 = r19 + 1;
        r7 = r20;
        r10 = 0;
        r13 = 1;
        goto L_0x0024;
    L_0x00ba:
        r12 = 1;
        if (r2 != 0) goto L_0x00be;
    L_0x00bd:
        return r14;
    L_0x00be:
        r2 = 0;
        r1.sortDialogs(r2);
        r0 = r21.getNotificationCenter();
        r4 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r5 = new java.lang.Object[r14];
        r0.postNotificationName(r4, r5);
        if (r16 == 0) goto L_0x0110;
    L_0x00cf:
        r4 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0100 }
        r0 = r16 + 12;
        r4.<init>(r0);	 Catch:{ Exception -> 0x0100 }
        r0 = 17;
        r4.writeInt32(r0);	 Catch:{ Exception -> 0x00fe }
        r4.writeInt32(r8);	 Catch:{ Exception -> 0x00fe }
        r0 = r9.folder_peers;	 Catch:{ Exception -> 0x00fe }
        r0 = r0.size();	 Catch:{ Exception -> 0x00fe }
        r4.writeInt32(r0);	 Catch:{ Exception -> 0x00fe }
        r0 = r9.folder_peers;	 Catch:{ Exception -> 0x00fe }
        r0 = r0.size();	 Catch:{ Exception -> 0x00fe }
        r2 = 0;
    L_0x00ee:
        if (r2 >= r0) goto L_0x0105;
    L_0x00f0:
        r5 = r9.folder_peers;	 Catch:{ Exception -> 0x00fe }
        r5 = r5.get(r2);	 Catch:{ Exception -> 0x00fe }
        r5 = (org.telegram.tgnet.TLRPC.TL_inputFolderPeer) r5;	 Catch:{ Exception -> 0x00fe }
        r5.serializeToStream(r4);	 Catch:{ Exception -> 0x00fe }
        r2 = r2 + 1;
        goto L_0x00ee;
    L_0x00fe:
        r0 = move-exception;
        goto L_0x0102;
    L_0x0100:
        r0 = move-exception;
        r4 = r2;
    L_0x0102:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0105:
        r0 = r21.getMessagesStorage();
        r4 = r0.createPendingTask(r4);
        r17 = r4;
        goto L_0x0112;
    L_0x0110:
        r17 = 0;
    L_0x0112:
        r0 = r3;
        r2 = r17;
        goto L_0x011f;
    L_0x0116:
        r0 = r25;
        r2 = 0;
        r12 = 1;
        r9.folder_peers = r0;
        r0 = r2;
        r2 = r26;
    L_0x011f:
        r4 = r9.folder_peers;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0141;
    L_0x0127:
        r4 = r21.getConnectionsManager();
        r5 = new org.telegram.messenger.-$$Lambda$MessagesController$9CMnchx8bWEDM7ecBj10XAUeG70;
        r5.<init>(r1, r2);
        r4.sendRequest(r9, r5);
        r2 = r21.getMessagesStorage();
        r3 = 0;
        r4 = r9.folder_peers;
        r5 = 0;
        r7 = r23;
        r2.setDialogsFolderId(r3, r4, r5, r7);
    L_0x0141:
        if (r0 != 0) goto L_0x0145;
    L_0x0143:
        r12 = 0;
        goto L_0x014b;
    L_0x0145:
        r0 = r0[r14];
        if (r0 == 0) goto L_0x014b;
    L_0x0149:
        r13 = 2;
        r12 = 2;
    L_0x014b:
        return r12;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.addDialogToFolder(java.util.ArrayList, int, int, java.util.ArrayList, long):int");
    }

    public /* synthetic */ void lambda$addDialogToFolder$128$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadDialogs(int i, int i2, int i3, boolean z) {
        loadDialogs(i, i2, i3, z, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x015d  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x015d  */
    public void loadDialogs(int r11, int r12, int r13, boolean r14, java.lang.Runnable r15) {
        /*
        r10 = this;
        r0 = r10.loadingDialogs;
        r0 = r0.get(r11);
        if (r0 != 0) goto L_0x0170;
    L_0x0008:
        r0 = r10.resetingDialogs;
        if (r0 == 0) goto L_0x000e;
    L_0x000c:
        goto L_0x0170;
    L_0x000e:
        r0 = r10.loadingDialogs;
        r1 = 1;
        r0.put(r11, r1);
        r0 = r10.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r3 = 0;
        r4 = new java.lang.Object[r3];
        r0.postNotificationName(r2, r4);
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0050;
    L_0x0024:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "folderId = ";
        r0.append(r2);
        r0.append(r11);
        r2 = " load cacheOffset = ";
        r0.append(r2);
        r0.append(r12);
        r2 = " count = ";
        r0.append(r2);
        r0.append(r13);
        r2 = " cache = ";
        r0.append(r2);
        r0.append(r14);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0050:
        if (r14 == 0) goto L_0x0069;
    L_0x0052:
        r14 = r10.getMessagesStorage();
        if (r12 != 0) goto L_0x005a;
    L_0x0058:
        r15 = 0;
        goto L_0x0060;
    L_0x005a:
        r15 = r10.nextDialogsCacheOffset;
        r15 = r15.get(r11, r3);
    L_0x0060:
        if (r12 != 0) goto L_0x0063;
    L_0x0062:
        goto L_0x0064;
    L_0x0063:
        r1 = 0;
    L_0x0064:
        r14.getDialogs(r11, r15, r13, r1);
        goto L_0x0170;
    L_0x0069:
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_getDialogs;
        r12.<init>();
        r12.limit = r13;
        r12.exclude_pinned = r1;
        r14 = 2;
        if (r11 == 0) goto L_0x007c;
    L_0x0075:
        r0 = r12.flags;
        r0 = r0 | r14;
        r12.flags = r0;
        r12.folder_id = r11;
    L_0x007c:
        r0 = r10.getUserConfig();
        r0 = r0.getDialogLoadOffsets(r11);
        r2 = r0[r3];
        r4 = 32;
        r5 = -1;
        if (r2 == r5) goto L_0x0103;
    L_0x008b:
        r2 = r0[r3];
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r2 != r5) goto L_0x00ad;
    L_0x0092:
        r12 = r10.dialogsEndReached;
        r12.put(r11, r1);
        r12 = r10.serverDialogsEndReached;
        r12.put(r11, r1);
        r12 = r10.loadingDialogs;
        r12.put(r11, r3);
        r11 = r10.getNotificationCenter();
        r12 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r13 = new java.lang.Object[r3];
        r11.postNotificationName(r12, r13);
        return;
    L_0x00ad:
        r2 = r0[r3];
        r12.offset_id = r2;
        r1 = r0[r1];
        r12.offset_date = r1;
        r1 = r12.offset_id;
        if (r1 != 0) goto L_0x00c2;
    L_0x00b9:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r14.<init>();
        r12.offset_peer = r14;
        goto L_0x0164;
    L_0x00c2:
        r1 = 4;
        r2 = r0[r1];
        if (r2 == 0) goto L_0x00d5;
    L_0x00c7:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
        r14.<init>();
        r12.offset_peer = r14;
        r14 = r12.offset_peer;
        r1 = r0[r1];
        r14.channel_id = r1;
        goto L_0x00f5;
    L_0x00d5:
        r1 = r0[r14];
        if (r1 == 0) goto L_0x00e7;
    L_0x00d9:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerUser;
        r1.<init>();
        r12.offset_peer = r1;
        r1 = r12.offset_peer;
        r14 = r0[r14];
        r1.user_id = r14;
        goto L_0x00f5;
    L_0x00e7:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat;
        r14.<init>();
        r12.offset_peer = r14;
        r14 = r12.offset_peer;
        r1 = 3;
        r1 = r0[r1];
        r14.chat_id = r1;
    L_0x00f5:
        r14 = r12.offset_peer;
        r1 = 5;
        r2 = r0[r1];
        r2 = (long) r2;
        r0 = r0[r1];
        r0 = (long) r0;
        r0 = r0 << r4;
        r0 = r0 | r2;
        r14.access_hash = r0;
        goto L_0x0164;
    L_0x0103:
        r14 = r10.getDialogs(r11);
        r0 = r14.size();
        r0 = r0 - r1;
    L_0x010c:
        if (r0 < 0) goto L_0x015a;
    L_0x010e:
        r2 = r14.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Dialog) r2;
        r5 = r2.pinned;
        if (r5 == 0) goto L_0x0119;
    L_0x0118:
        goto L_0x0157;
    L_0x0119:
        r5 = r2.id;
        r7 = (int) r5;
        r8 = r5 >> r4;
        r9 = (int) r8;
        if (r7 == 0) goto L_0x0157;
    L_0x0121:
        if (r9 == r1) goto L_0x0157;
    L_0x0123:
        r2 = r2.top_message;
        if (r2 <= 0) goto L_0x0157;
    L_0x0127:
        r2 = r10.dialogMessage;
        r2 = r2.get(r5);
        r2 = (org.telegram.messenger.MessageObject) r2;
        if (r2 == 0) goto L_0x0157;
    L_0x0131:
        r5 = r2.getId();
        if (r5 <= 0) goto L_0x0157;
    L_0x0137:
        r14 = r2.messageOwner;
        r0 = r14.date;
        r12.offset_date = r0;
        r0 = r14.id;
        r12.offset_id = r0;
        r14 = r14.to_id;
        r0 = r14.channel_id;
        if (r0 == 0) goto L_0x0149;
    L_0x0147:
        r14 = -r0;
        goto L_0x0150;
    L_0x0149:
        r0 = r14.chat_id;
        if (r0 == 0) goto L_0x014e;
    L_0x014d:
        goto L_0x0147;
    L_0x014e:
        r14 = r14.user_id;
    L_0x0150:
        r14 = r10.getInputPeer(r14);
        r12.offset_peer = r14;
        goto L_0x015b;
    L_0x0157:
        r0 = r0 + -1;
        goto L_0x010c;
    L_0x015a:
        r1 = 0;
    L_0x015b:
        if (r1 != 0) goto L_0x0164;
    L_0x015d:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r14.<init>();
        r12.offset_peer = r14;
    L_0x0164:
        r14 = r10.getConnectionsManager();
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$INiSSH41hlqDqoaByzQ3Bgb39Ik;
        r0.<init>(r10, r11, r13, r15);
        r14.sendRequest(r12, r0);
    L_0x0170:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.loadDialogs(int, int, int, boolean, java.lang.Runnable):void");
    }

    public /* synthetic */ void lambda$loadDialogs$129$MessagesController(int i, int i2, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Dialogs messages_dialogs = (messages_Dialogs) tLObject;
            processLoadedDialogs(messages_dialogs, null, i, 0, i2, 0, false, false, false);
            if (runnable != null && messages_dialogs.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        }
    }

    public void loadGlobalNotificationsSettings() {
        if (this.loadingNotificationSettings == 0 && !getUserConfig().notificationsSettingsLoaded) {
            SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
            Editor editor = null;
            String str = "EnableGroup";
            if (notificationsSettings.contains(str)) {
                boolean z = notificationsSettings.getBoolean(str, true);
                Editor edit = notificationsSettings.edit();
                if (!z) {
                    edit.putInt("EnableGroup2", Integer.MAX_VALUE);
                    edit.putInt("EnableChannel2", Integer.MAX_VALUE);
                }
                edit.remove(str).commit();
                editor = edit;
            }
            str = "EnableAll";
            if (notificationsSettings.contains(str)) {
                boolean z2 = notificationsSettings.getBoolean(str, true);
                if (editor == null) {
                    editor = notificationsSettings.edit();
                }
                if (!z2) {
                    editor.putInt("EnableAll2", Integer.MAX_VALUE);
                }
                editor.remove(str).commit();
            }
            if (editor != null) {
                editor.commit();
            }
            this.loadingNotificationSettings = 3;
            for (int i = 0; i < 3; i++) {
                TL_account_getNotifySettings tL_account_getNotifySettings = new TL_account_getNotifySettings();
                if (i == 0) {
                    tL_account_getNotifySettings.peer = new TL_inputNotifyChats();
                } else if (i == 1) {
                    tL_account_getNotifySettings.peer = new TL_inputNotifyUsers();
                } else if (i == 2) {
                    tL_account_getNotifySettings.peer = new TL_inputNotifyBroadcasts();
                }
                getConnectionsManager().sendRequest(tL_account_getNotifySettings, new -$$Lambda$MessagesController$RkowpjYYcnodZj8wIt40IaTw_Yc(this, i));
            }
        }
        if (!getUserConfig().notificationsSignUpSettingsLoaded) {
            loadSignUpNotificationsSettings();
        }
    }

    public /* synthetic */ void lambda$loadGlobalNotificationsSettings$131$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$CLASSNAMEHCoSp-pBn8PC_7bTc9qJYfzo(this, tLObject, i));
    }

    public /* synthetic */ void lambda$null$130$MessagesController(TLObject tLObject, int i) {
        if (tLObject != null) {
            this.loadingNotificationSettings--;
            TL_peerNotifySettings tL_peerNotifySettings = (TL_peerNotifySettings) tLObject;
            Editor edit = this.notificationsPreferences.edit();
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
            getConnectionsManager().sendRequest(new TL_account_getContactSignUpNotification(), new -$$Lambda$MessagesController$4gQxOrHgxQ8Ex09epPhIw20QSsc(this));
        }
    }

    public /* synthetic */ void lambda$loadSignUpNotificationsSettings$133$MessagesController(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$j1nx6xmI11ctk3o-Ieu303nN-NM(this, tLObject));
    }

    public /* synthetic */ void lambda$null$132$MessagesController(TLObject tLObject) {
        this.loadingNotificationSignUpSettings = false;
        Editor edit = this.notificationsPreferences.edit();
        this.enableJoined = tLObject instanceof TL_boolFalse;
        edit.putBoolean("EnableContactJoined", this.enableJoined);
        edit.commit();
        getUserConfig().notificationsSignUpSettingsLoaded = true;
        getUserConfig().saveConfig(false);
    }

    public void forceResetDialogs() {
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        getNotificationsController().deleteAllNotificationChannels();
    }

    /* Access modifiers changed, original: protected */
    public void loadUnknownDialog(InputPeer inputPeer, long j) {
        Throwable e;
        if (inputPeer != null) {
            long peerDialogId = DialogObject.getPeerDialogId(inputPeer);
            if (this.gettingUnknownDialogs.indexOfKey(peerDialogId) < 0) {
                this.gettingUnknownDialogs.put(peerDialogId, Boolean.valueOf(true));
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("load unknown dialog ");
                    stringBuilder.append(peerDialogId);
                    FileLog.d(stringBuilder.toString());
                }
                TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
                TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                tL_inputDialogPeer.peer = inputPeer;
                tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                if (j == 0) {
                    NativeByteBuffer nativeByteBuffer;
                    try {
                        nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 4);
                        try {
                            nativeByteBuffer.writeInt32(15);
                            inputPeer.serializeToStream(nativeByteBuffer);
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$awovyvi5MjQ1qPBmM2TJlmCgHOA(this, j, peerDialogId));
                    }
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                }
                getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$awovyvi5MjQ1qPBmM2TJlmCgHOA(this, j, peerDialogId));
            }
        }
    }

    public /* synthetic */ void lambda$loadUnknownDialog$134$MessagesController(long j, long j2, TLObject tLObject, TL_error tL_error) {
        long j3 = j;
        if (tLObject != null) {
            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty()) {
                TL_dialog tL_dialog = (TL_dialog) tL_messages_peerDialogs.dialogs.get(0);
                messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                processLoadedDialogs(tL_messages_dialogs, null, tL_dialog.folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_UNKNOWN, false, false, false);
            }
        }
        if (j3 != 0) {
            getMessagesStorage().removePendingTask(j3);
        }
        this.gettingUnknownDialogs.delete(j2);
    }

    private void fetchFolderInLoadedPinnedDialogs(TL_messages_peerDialogs tL_messages_peerDialogs) {
        int size = tL_messages_peerDialogs.dialogs.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            Dialog dialog = (Dialog) tL_messages_peerDialogs.dialogs.get(i2);
            if (dialog instanceof TL_dialogFolder) {
                TL_dialogFolder tL_dialogFolder = (TL_dialogFolder) dialog;
                long peerDialogId = DialogObject.getPeerDialogId(dialog.peer);
                if (tL_dialogFolder.top_message == 0 || peerDialogId == 0) {
                    tL_messages_peerDialogs.dialogs.remove(tL_dialogFolder);
                } else {
                    size = tL_messages_peerDialogs.messages.size();
                    for (i2 = 0; i2 < size; i2++) {
                        Message message = (Message) tL_messages_peerDialogs.messages.get(i2);
                        if (peerDialogId == MessageObject.getDialogId(message) && dialog.top_message == message.id) {
                            InputPeer tL_inputPeerChannel;
                            TL_dialog tL_dialog = new TL_dialog();
                            tL_dialog.peer = dialog.peer;
                            tL_dialog.top_message = dialog.top_message;
                            tL_dialog.folder_id = tL_dialogFolder.folder.id;
                            tL_dialog.flags |= 16;
                            tL_messages_peerDialogs.dialogs.add(tL_dialog);
                            Peer peer = dialog.peer;
                            if (peer instanceof TL_peerChannel) {
                                tL_inputPeerChannel = new TL_inputPeerChannel();
                                tL_inputPeerChannel.channel_id = dialog.peer.channel_id;
                                i2 = tL_messages_peerDialogs.chats.size();
                                while (i < i2) {
                                    Chat chat = (Chat) tL_messages_peerDialogs.chats.get(i);
                                    if (chat.id == tL_inputPeerChannel.channel_id) {
                                        tL_inputPeerChannel.access_hash = chat.access_hash;
                                        break;
                                    }
                                    i++;
                                }
                            } else if (peer instanceof TL_peerChat) {
                                tL_inputPeerChannel = new TL_inputPeerChat();
                                tL_inputPeerChannel.chat_id = dialog.peer.chat_id;
                            } else {
                                tL_inputPeerChannel = new TL_inputPeerUser();
                                tL_inputPeerChannel.user_id = dialog.peer.user_id;
                                i2 = tL_messages_peerDialogs.users.size();
                                while (i < i2) {
                                    User user = (User) tL_messages_peerDialogs.users.get(i);
                                    if (user.id == tL_inputPeerChannel.user_id) {
                                        tL_inputPeerChannel.access_hash = user.access_hash;
                                        break;
                                    }
                                    i++;
                                }
                            }
                            loadUnknownDialog(tL_inputPeerChannel, 0);
                            return;
                        }
                    }
                    return;
                }
            }
        }
    }

    /* JADX WARNING: Missing block: B:68:0x01c4, code skipped:
            if (r11.migrated_to != null) goto L_0x017b;
     */
    private void resetDialogs(boolean r26, int r27, int r28, int r29, int r30) {
        /*
        r25 = this;
        r6 = r25;
        r7 = 1;
        r0 = 0;
        r1 = java.lang.Integer.valueOf(r0);
        if (r26 == 0) goto L_0x0055;
    L_0x000a:
        r1 = r6.resetingDialogs;
        if (r1 == 0) goto L_0x000f;
    L_0x000e:
        return;
    L_0x000f:
        r1 = r25.getUserConfig();
        r1.setPinnedDialogsLoaded(r7, r0);
        r6.resetingDialogs = r7;
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getPinnedDialogs;
        r8.<init>();
        r9 = r25.getConnectionsManager();
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$21H1u-9Jbe7vowKHvar_GydIFa3A;
        r0 = r10;
        r1 = r25;
        r2 = r27;
        r3 = r28;
        r4 = r29;
        r5 = r30;
        r0.<init>(r1, r2, r3, r4, r5);
        r9.sendRequest(r8, r10);
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getDialogs;
        r8.<init>();
        r0 = 100;
        r8.limit = r0;
        r8.exclude_pinned = r7;
        r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r0.<init>();
        r8.offset_peer = r0;
        r7 = r25.getConnectionsManager();
        r9 = new org.telegram.messenger.-$$Lambda$MessagesController$Xy2QmS0LrWUNcEUvdEjkSFNDIYU;
        r0 = r9;
        r0.<init>(r1, r2, r3, r4, r5);
        r7.sendRequest(r8, r9);
        goto L_0x02d2;
    L_0x0055:
        r2 = r6.resetDialogsPinned;
        if (r2 == 0) goto L_0x02d2;
    L_0x0059:
        r2 = r6.resetDialogsAll;
        if (r2 == 0) goto L_0x02d2;
    L_0x005d:
        r2 = r2.messages;
        r10 = r2.size();
        r2 = r6.resetDialogsAll;
        r2 = r2.dialogs;
        r18 = r2.size();
        r2 = r6.resetDialogsPinned;
        r6.fetchFolderInLoadedPinnedDialogs(r2);
        r2 = r6.resetDialogsAll;
        r2 = r2.dialogs;
        r3 = r6.resetDialogsPinned;
        r3 = r3.dialogs;
        r2.addAll(r3);
        r2 = r6.resetDialogsAll;
        r2 = r2.messages;
        r3 = r6.resetDialogsPinned;
        r3 = r3.messages;
        r2.addAll(r3);
        r2 = r6.resetDialogsAll;
        r2 = r2.users;
        r3 = r6.resetDialogsPinned;
        r3 = r3.users;
        r2.addAll(r3);
        r2 = r6.resetDialogsAll;
        r2 = r2.chats;
        r3 = r6.resetDialogsPinned;
        r3 = r3.chats;
        r2.addAll(r3);
        r15 = new android.util.LongSparseArray;
        r15.<init>();
        r2 = new android.util.LongSparseArray;
        r2.<init>();
        r3 = new android.util.SparseArray;
        r3.<init>();
        r4 = new android.util.SparseArray;
        r4.<init>();
        r5 = 0;
    L_0x00b1:
        r8 = r6.resetDialogsAll;
        r8 = r8.users;
        r8 = r8.size();
        if (r5 >= r8) goto L_0x00cd;
    L_0x00bb:
        r8 = r6.resetDialogsAll;
        r8 = r8.users;
        r8 = r8.get(r5);
        r8 = (org.telegram.tgnet.TLRPC.User) r8;
        r9 = r8.id;
        r3.put(r9, r8);
        r5 = r5 + 1;
        goto L_0x00b1;
    L_0x00cd:
        r5 = 0;
    L_0x00ce:
        r8 = r6.resetDialogsAll;
        r8 = r8.chats;
        r8 = r8.size();
        if (r5 >= r8) goto L_0x00ea;
    L_0x00d8:
        r8 = r6.resetDialogsAll;
        r8 = r8.chats;
        r8 = r8.get(r5);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        r9 = r8.id;
        r4.put(r9, r8);
        r5 = r5 + 1;
        goto L_0x00ce;
    L_0x00ea:
        r5 = 0;
        r14 = r5;
        r8 = 0;
    L_0x00ed:
        r9 = r6.resetDialogsAll;
        r9 = r9.messages;
        r9 = r9.size();
        if (r8 >= r9) goto L_0x015b;
    L_0x00f7:
        r9 = r6.resetDialogsAll;
        r9 = r9.messages;
        r9 = r9.get(r8);
        r9 = (org.telegram.tgnet.TLRPC.Message) r9;
        if (r8 >= r10) goto L_0x010c;
    L_0x0103:
        if (r14 == 0) goto L_0x010b;
    L_0x0105:
        r11 = r9.date;
        r12 = r14.date;
        if (r11 >= r12) goto L_0x010c;
    L_0x010b:
        r14 = r9;
    L_0x010c:
        r11 = r9.to_id;
        r12 = r11.channel_id;
        if (r12 == 0) goto L_0x012d;
    L_0x0112:
        r11 = r4.get(r12);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x011f;
    L_0x011a:
        r12 = r11.left;
        if (r12 == 0) goto L_0x011f;
    L_0x011e:
        goto L_0x0158;
    L_0x011f:
        if (r11 == 0) goto L_0x013e;
    L_0x0121:
        r11 = r11.megagroup;
        if (r11 == 0) goto L_0x013e;
    L_0x0125:
        r11 = r9.flags;
        r12 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r12;
        r9.flags = r11;
        goto L_0x013e;
    L_0x012d:
        r11 = r11.chat_id;
        if (r11 == 0) goto L_0x013e;
    L_0x0131:
        r11 = r4.get(r11);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x013e;
    L_0x0139:
        r11 = r11.migrated_to;
        if (r11 == 0) goto L_0x013e;
    L_0x013d:
        goto L_0x0158;
    L_0x013e:
        r11 = new org.telegram.messenger.MessageObject;
        r12 = r6.currentAccount;
        r24 = 0;
        r19 = r11;
        r20 = r12;
        r21 = r9;
        r22 = r3;
        r23 = r4;
        r19.<init>(r20, r21, r22, r23, r24);
        r12 = r11.getDialogId();
        r2.put(r12, r11);
    L_0x0158:
        r8 = r8 + 1;
        goto L_0x00ed;
    L_0x015b:
        r8 = 0;
    L_0x015c:
        r9 = r6.resetDialogsAll;
        r9 = r9.dialogs;
        r9 = r9.size();
        if (r8 >= r9) goto L_0x0228;
    L_0x0166:
        r9 = r6.resetDialogsAll;
        r9 = r9.dialogs;
        r9 = r9.get(r8);
        r9 = (org.telegram.tgnet.TLRPC.Dialog) r9;
        org.telegram.messenger.DialogObject.initDialog(r9);
        r11 = r9.id;
        r16 = 0;
        r13 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1));
        if (r13 != 0) goto L_0x017e;
    L_0x017b:
        r13 = r1;
        goto L_0x0222;
    L_0x017e:
        r13 = r9.last_message_date;
        if (r13 != 0) goto L_0x0190;
    L_0x0182:
        r11 = r2.get(r11);
        r11 = (org.telegram.messenger.MessageObject) r11;
        if (r11 == 0) goto L_0x0190;
    L_0x018a:
        r11 = r11.messageOwner;
        r11 = r11.date;
        r9.last_message_date = r11;
    L_0x0190:
        r11 = org.telegram.messenger.DialogObject.isChannel(r9);
        if (r11 == 0) goto L_0x01b3;
    L_0x0196:
        r11 = r9.id;
        r12 = (int) r11;
        r11 = -r12;
        r11 = r4.get(r11);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x01a7;
    L_0x01a2:
        r11 = r11.left;
        if (r11 == 0) goto L_0x01a7;
    L_0x01a6:
        goto L_0x017b;
    L_0x01a7:
        r11 = r6.channelsPts;
        r12 = r9.id;
        r13 = (int) r12;
        r12 = -r13;
        r13 = r9.pts;
        r11.put(r12, r13);
        goto L_0x01c7;
    L_0x01b3:
        r11 = r9.id;
        r13 = (int) r11;
        if (r13 >= 0) goto L_0x01c7;
    L_0x01b8:
        r12 = (int) r11;
        r11 = -r12;
        r11 = r4.get(r11);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x01c7;
    L_0x01c2:
        r11 = r11.migrated_to;
        if (r11 == 0) goto L_0x01c7;
    L_0x01c6:
        goto L_0x017b;
    L_0x01c7:
        r11 = r9.id;
        r15.put(r11, r9);
        r11 = r6.dialogs_read_inbox_max;
        r12 = r9.id;
        r12 = java.lang.Long.valueOf(r12);
        r11 = r11.get(r12);
        r11 = (java.lang.Integer) r11;
        if (r11 != 0) goto L_0x01dd;
    L_0x01dc:
        r11 = r1;
    L_0x01dd:
        r12 = r6.dialogs_read_inbox_max;
        r13 = r1;
        r0 = r9.id;
        r0 = java.lang.Long.valueOf(r0);
        r1 = r11.intValue();
        r11 = r9.read_inbox_max_id;
        r1 = java.lang.Math.max(r1, r11);
        r1 = java.lang.Integer.valueOf(r1);
        r12.put(r0, r1);
        r0 = r6.dialogs_read_outbox_max;
        r11 = r9.id;
        r1 = java.lang.Long.valueOf(r11);
        r0 = r0.get(r1);
        r1 = r0;
        r1 = (java.lang.Integer) r1;
        if (r1 != 0) goto L_0x0209;
    L_0x0208:
        r1 = r13;
    L_0x0209:
        r0 = r6.dialogs_read_outbox_max;
        r11 = r9.id;
        r11 = java.lang.Long.valueOf(r11);
        r1 = r1.intValue();
        r9 = r9.read_outbox_max_id;
        r1 = java.lang.Math.max(r1, r9);
        r1 = java.lang.Integer.valueOf(r1);
        r0.put(r11, r1);
    L_0x0222:
        r8 = r8 + 1;
        r1 = r13;
        r0 = 0;
        goto L_0x015c;
    L_0x0228:
        r0 = r6.resetDialogsAll;
        r0 = r0.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r0);
        r0 = 0;
    L_0x0230:
        r1 = r6.resetDialogsAll;
        r1 = r1.messages;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x02b8;
    L_0x023a:
        r1 = r6.resetDialogsAll;
        r1 = r1.messages;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;
        r4 = r1.action;
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r8 == 0) goto L_0x0265;
    L_0x024a:
        r4 = r4.user_id;
        r4 = r3.get(r4);
        r4 = (org.telegram.tgnet.TLRPC.User) r4;
        if (r4 == 0) goto L_0x0265;
    L_0x0254:
        r4 = r4.bot;
        if (r4 == 0) goto L_0x0265;
    L_0x0258:
        r4 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r4.<init>();
        r1.reply_markup = r4;
        r4 = r1.flags;
        r4 = r4 | 64;
        r1.flags = r4;
    L_0x0265:
        r4 = r1.action;
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r8 != 0) goto L_0x02af;
    L_0x026b:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r4 == 0) goto L_0x0270;
    L_0x026f:
        goto L_0x02af;
    L_0x0270:
        r4 = r1.out;
        if (r4 == 0) goto L_0x0277;
    L_0x0274:
        r4 = r6.dialogs_read_outbox_max;
        goto L_0x0279;
    L_0x0277:
        r4 = r6.dialogs_read_inbox_max;
    L_0x0279:
        r8 = r1.dialog_id;
        r8 = java.lang.Long.valueOf(r8);
        r8 = r4.get(r8);
        r8 = (java.lang.Integer) r8;
        if (r8 != 0) goto L_0x02a0;
    L_0x0287:
        r8 = r25.getMessagesStorage();
        r9 = r1.out;
        r11 = r1.dialog_id;
        r8 = r8.getDialogReadMax(r9, r11);
        r8 = java.lang.Integer.valueOf(r8);
        r11 = r1.dialog_id;
        r9 = java.lang.Long.valueOf(r11);
        r4.put(r9, r8);
    L_0x02a0:
        r4 = r8.intValue();
        r8 = r1.id;
        if (r4 >= r8) goto L_0x02aa;
    L_0x02a8:
        r4 = 1;
        goto L_0x02ab;
    L_0x02aa:
        r4 = 0;
    L_0x02ab:
        r1.unread = r4;
        r4 = 0;
        goto L_0x02b4;
    L_0x02af:
        r4 = 0;
        r1.unread = r4;
        r1.media_unread = r4;
    L_0x02b4:
        r0 = r0 + 1;
        goto L_0x0230;
    L_0x02b8:
        r8 = r25.getMessagesStorage();
        r9 = r6.resetDialogsAll;
        r11 = r27;
        r12 = r28;
        r13 = r29;
        r0 = r14;
        r14 = r30;
        r16 = r2;
        r17 = r0;
        r8.resetDialogs(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18);
        r6.resetDialogsPinned = r5;
        r6.resetDialogsAll = r5;
    L_0x02d2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.resetDialogs(boolean, int, int, int, int):void");
    }

    public /* synthetic */ void lambda$resetDialogs$135$MessagesController(int i, int i2, int i3, int i4, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            this.resetDialogsPinned = (TL_messages_peerDialogs) tLObject;
            for (int i5 = 0; i5 < this.resetDialogsPinned.dialogs.size(); i5++) {
                ((Dialog) this.resetDialogsPinned.dialogs.get(i5)).pinned = true;
            }
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    public /* synthetic */ void lambda$resetDialogs$136$MessagesController(int i, int i2, int i3, int i4, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            this.resetDialogsAll = (messages_Dialogs) tLObject;
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    /* Access modifiers changed, original: protected */
    public void completeDialogsReset(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, LongSparseArray<Dialog> longSparseArray, LongSparseArray<MessageObject> longSparseArray2, Message message) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$32iz76H0s2glYDHdx6oIz6RKn0E(this, i3, i4, i5, messages_dialogs, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$completeDialogsReset$138$MessagesController(int i, int i2, int i3, messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.gettingDifference = false;
        getMessagesStorage().setLastPtsValue(i);
        getMessagesStorage().setLastDateValue(i2);
        getMessagesStorage().setLastQtsValue(i3);
        getDifference();
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$1A7fFT93S3M-PzwriJpeVFK04wI(this, messages_dialogs, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$null$137$MessagesController(messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        int i;
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        LongSparseArray longSparseArray3 = longSparseArray;
        this.resetingDialogs = false;
        applyDialogsNotificationsSettings(messages_dialogs2.dialogs);
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearAllDrafts(false);
        mediaDataController.loadDraftsIfNeed();
        putUsers(messages_dialogs2.users, false);
        putChats(messages_dialogs2.chats, false);
        for (i = 0; i < this.allDialogs.size(); i++) {
            Dialog dialog = (Dialog) this.allDialogs.get(i);
            if (!DialogObject.isSecretDialogId(dialog.id)) {
                this.dialogs_dict.remove(dialog.id);
                MessageObject messageObject = (MessageObject) this.dialogMessage.get(dialog.id);
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
        for (i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray3.keyAt(i);
            Dialog dialog2 = (Dialog) longSparseArray3.valueAt(i);
            DraftMessage draftMessage = dialog2.draft;
            if (draftMessage instanceof TL_draftMessage) {
                mediaDataController.saveDraft(dialog2.id, draftMessage, null, false);
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
        i = this.dialogs_dict.size();
        for (int i2 = 0; i2 < i; i2++) {
            this.allDialogs.add(this.dialogs_dict.valueAt(i2));
        }
        sortDialogs(null);
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, false);
        this.dialogsEndReached.put(1, true);
        this.serverDialogsEndReached.put(1, false);
        i = getUserConfig().getTotalDialogsCount(0);
        int[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(0);
        if (!(i >= 400 || dialogLoadOffsets[0] == -1 || dialogLoadOffsets[0] == Integer.MAX_VALUE)) {
            loadDialogs(0, 0, 100, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private void migrateDialogs(int i, int i2, int i3, int i4, int i5, long j) {
        if (!this.migratingDialogs && i != -1) {
            this.migratingDialogs = true;
            TL_messages_getDialogs tL_messages_getDialogs = new TL_messages_getDialogs();
            tL_messages_getDialogs.exclude_pinned = true;
            tL_messages_getDialogs.limit = 100;
            tL_messages_getDialogs.offset_id = i;
            tL_messages_getDialogs.offset_date = i2;
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("start migrate with id ");
                stringBuilder.append(i);
                stringBuilder.append(" date ");
                stringBuilder.append(LocaleController.getInstance().formatterStats.format(((long) i2) * 1000));
                FileLog.d(stringBuilder.toString());
            }
            if (i == 0) {
                tL_messages_getDialogs.offset_peer = new TL_inputPeerEmpty();
            } else {
                if (i5 != 0) {
                    tL_messages_getDialogs.offset_peer = new TL_inputPeerChannel();
                    tL_messages_getDialogs.offset_peer.channel_id = i5;
                } else if (i3 != 0) {
                    tL_messages_getDialogs.offset_peer = new TL_inputPeerUser();
                    tL_messages_getDialogs.offset_peer.user_id = i3;
                } else {
                    tL_messages_getDialogs.offset_peer = new TL_inputPeerChat();
                    tL_messages_getDialogs.offset_peer.chat_id = i4;
                }
                tL_messages_getDialogs.offset_peer.access_hash = j;
            }
            getConnectionsManager().sendRequest(tL_messages_getDialogs, new -$$Lambda$MessagesController$72DV-O9Yw2rrUo6FOYwpQyg_MC8(this, i));
        }
    }

    public /* synthetic */ void lambda$migrateDialogs$142$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$y3CZ2c8ybp_53uKHi5ny6w7jDdM(this, (messages_Dialogs) tLObject, i));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$rdkMY26ook-WJ-1ozERnO4tC4II(this));
    }

    public /* synthetic */ void lambda$null$140$MessagesController(messages_Dialogs messages_dialogs, int i) {
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        int i2 = i;
        try {
            String str;
            int i3;
            SQLiteCursor sQLiteCursor;
            int i4;
            getUserConfig().setTotalDialogsCount(0, getUserConfig().getTotalDialogsCount(0) + messages_dialogs2.dialogs.size());
            Message message = null;
            int i5 = 0;
            while (true) {
                str = " date ";
                if (i5 >= messages_dialogs2.messages.size()) {
                    break;
                }
                Message message2 = (Message) messages_dialogs2.messages.get(i5);
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("search migrate id ");
                    stringBuilder.append(message2.id);
                    stringBuilder.append(str);
                    stringBuilder.append(LocaleController.getInstance().formatterStats.format(((long) message2.date) * 1000));
                    FileLog.d(stringBuilder.toString());
                }
                if (message == null || message2.date < message.date) {
                    message = message2;
                }
                i5++;
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("migrate step with id ");
                stringBuilder2.append(message.id);
                stringBuilder2.append(str);
                stringBuilder2.append(LocaleController.getInstance().formatterStats.format(((long) message.date) * 1000));
                FileLog.d(stringBuilder2.toString());
            }
            int i6 = 2;
            int i7 = -1;
            if (messages_dialogs2.dialogs.size() >= 100) {
                i5 = message.id;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("migrate stop due to not 100 dialogs");
                }
                for (i5 = 0; i5 < 2; i5++) {
                    getUserConfig().setDialogsLoadOffset(i5, Integer.MAX_VALUE, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
                }
                i5 = -1;
            }
            StringBuilder stringBuilder3 = new StringBuilder(messages_dialogs2.dialogs.size() * 12);
            LongSparseArray longSparseArray = new LongSparseArray();
            for (i3 = 0; i3 < messages_dialogs2.dialogs.size(); i3++) {
                Dialog dialog = (Dialog) messages_dialogs2.dialogs.get(i3);
                DialogObject.initDialog(dialog);
                if (stringBuilder3.length() > 0) {
                    stringBuilder3.append(",");
                }
                stringBuilder3.append(dialog.id);
                longSparseArray.put(dialog.id, dialog);
            }
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE did IN (%s)", new Object[]{stringBuilder3.toString()}), new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                Dialog dialog2 = (Dialog) longSparseArray.get(longValue);
                if (dialog2.folder_id == queryFinalized.intValue(1)) {
                    longSparseArray.remove(longValue);
                    if (dialog2 != null) {
                        messages_dialogs2.dialogs.remove(dialog2);
                        i3 = 0;
                        while (i3 < messages_dialogs2.messages.size()) {
                            Message message3 = (Message) messages_dialogs2.messages.get(i3);
                            if (MessageObject.getDialogId(message3) == longValue) {
                                messages_dialogs2.messages.remove(i3);
                                i3--;
                                if (message3.id == dialog2.top_message) {
                                    dialog2.top_message = 0;
                                    break;
                                }
                            }
                            i3++;
                        }
                    }
                }
            }
            queryFinalized.dispose();
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("migrate found missing dialogs ");
                stringBuilder3.append(messages_dialogs2.dialogs.size());
                FileLog.d(stringBuilder3.toString());
            }
            queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
            if (queryFinalized.next()) {
                String str2;
                Message message4;
                i3 = Math.max(NUM, queryFinalized.intValue(0));
                int i8 = i5;
                i5 = 0;
                while (true) {
                    str2 = "migrate stop due to reached loaded dialogs ";
                    if (i5 >= messages_dialogs2.messages.size()) {
                        break;
                    }
                    Message message5 = (Message) messages_dialogs2.messages.get(i5);
                    if (message5.date < i3) {
                        if (i2 != i7) {
                            i8 = 0;
                            for (i6 = 
/*
Method generation error in method: org.telegram.messenger.MessagesController.lambda$null$140$MessagesController(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r6_8 'i6' int) = (r6_7 'i6' int), (r6_13 'i6' int) binds: {(r6_7 'i6' int)=B:58:0x01cd, (r6_13 'i6' int)=B:78:0x028c} in method: org.telegram.messenger.MessagesController.lambda$null$140$MessagesController(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:175)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:280)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 44 more

*/

    public /* synthetic */ void lambda$null$139$MessagesController() {
        this.migratingDialogs = false;
    }

    public /* synthetic */ void lambda$null$141$MessagesController() {
        this.migratingDialogs = false;
    }

    public void processLoadedDialogs(messages_Dialogs messages_dialogs, ArrayList<EncryptedChat> arrayList, int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$Rbz1xGAbFVY31e5a-fVma9dqn5I(this, i, i4, messages_dialogs, z, i3, arrayList, i2, z3, z2));
    }

    /* JADX WARNING: Missing block: B:120:0x0289, code skipped:
            if (r2.get(r3) == null) goto L_0x028b;
     */
    /* JADX WARNING: Missing block: B:142:0x02d5, code skipped:
            if (r5 == r1.id) goto L_0x02d9;
     */
    /* JADX WARNING: Missing block: B:150:0x02f6, code skipped:
            if (r3.migrated_to != null) goto L_0x028b;
     */
    public /* synthetic */ void lambda$processLoadedDialogs$145$MessagesController(int r24, int r25, org.telegram.tgnet.TLRPC.messages_Dialogs r26, boolean r27, int r28, java.util.ArrayList r29, int r30, boolean r31, boolean r32) {
        /*
        r23 = this;
        r14 = r23;
        r9 = r24;
        r10 = r25;
        r11 = r26;
        r12 = r29;
        r0 = r14.firstGettingTask;
        r1 = 0;
        r13 = 1;
        r15 = 0;
        r16 = java.lang.Integer.valueOf(r15);
        if (r0 != 0) goto L_0x001a;
    L_0x0015:
        r14.getNewDeleteTask(r1, r15);
        r14.firstGettingTask = r13;
    L_0x001a:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0048;
    L_0x001e:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "loaded folderId ";
        r0.append(r2);
        r0.append(r9);
        r2 = " loadType ";
        r0.append(r2);
        r0.append(r10);
        r2 = " count ";
        r0.append(r2);
        r2 = r11.dialogs;
        r2 = r2.size();
        r0.append(r2);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0048:
        r0 = r23.getUserConfig();
        r5 = r0.getDialogLoadOffsets(r9);
        r0 = r14.DIALOGS_LOAD_TYPE_CACHE;
        if (r10 != r0) goto L_0x0070;
    L_0x0054:
        r0 = r11.dialogs;
        r0 = r0.size();
        if (r0 != 0) goto L_0x0070;
    L_0x005c:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesController$vOgC1dbJNkwBHosi0gHFLsn36x8;
        r0 = r7;
        r1 = r23;
        r2 = r26;
        r3 = r24;
        r4 = r27;
        r6 = r28;
        r0.<init>(r1, r2, r3, r4, r5, r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);
        return;
    L_0x0070:
        r7 = new android.util.LongSparseArray;
        r7.<init>();
        r8 = new android.util.LongSparseArray;
        r8.<init>();
        r6 = new android.util.SparseArray;
        r6.<init>();
        r4 = new android.util.SparseArray;
        r4.<init>();
        r0 = 0;
    L_0x0085:
        r2 = r11.users;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x009d;
    L_0x008d:
        r2 = r11.users;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        r3 = r2.id;
        r6.put(r3, r2);
        r0 = r0 + 1;
        goto L_0x0085;
    L_0x009d:
        r0 = 0;
    L_0x009e:
        r2 = r11.chats;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x00b6;
    L_0x00a6:
        r2 = r11.chats;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Chat) r2;
        r3 = r2.id;
        r4.put(r3, r2);
        r0 = r0 + 1;
        goto L_0x009e;
    L_0x00b6:
        if (r12 == 0) goto L_0x00d8;
    L_0x00b8:
        r0 = new android.util.SparseArray;
        r0.<init>();
        r2 = r29.size();
        r3 = 0;
    L_0x00c2:
        if (r3 >= r2) goto L_0x00d6;
    L_0x00c4:
        r17 = r12.get(r3);
        r1 = r17;
        r1 = (org.telegram.tgnet.TLRPC.EncryptedChat) r1;
        r13 = r1.id;
        r0.put(r13, r1);
        r3 = r3 + 1;
        r1 = 0;
        r13 = 1;
        goto L_0x00c2;
    L_0x00d6:
        r13 = r0;
        goto L_0x00d9;
    L_0x00d8:
        r13 = 0;
    L_0x00d9:
        r0 = r14.DIALOGS_LOAD_TYPE_CACHE;
        if (r10 != r0) goto L_0x00e4;
    L_0x00dd:
        r0 = r14.nextDialogsCacheOffset;
        r1 = r30 + r28;
        r0.put(r9, r1);
    L_0x00e4:
        r0 = 0;
        r1 = 0;
    L_0x00e6:
        r2 = r11.messages;
        r2 = r2.size();
        r17 = 0;
        if (r0 >= r2) goto L_0x016e;
    L_0x00f0:
        r2 = r11.messages;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Message) r2;
        if (r1 == 0) goto L_0x0100;
    L_0x00fa:
        r3 = r2.date;
        r15 = r1.date;
        if (r3 >= r15) goto L_0x0101;
    L_0x0100:
        r1 = r2;
    L_0x0101:
        r3 = r2.to_id;
        r15 = r3.channel_id;
        if (r15 == 0) goto L_0x0134;
    L_0x0107:
        r3 = r4.get(r15);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x0124;
    L_0x010f:
        r15 = r3.left;
        if (r15 == 0) goto L_0x0124;
    L_0x0113:
        r27 = r13;
        r12 = r14.proxyDialogId;
        r15 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1));
        if (r15 == 0) goto L_0x0161;
    L_0x011b:
        r15 = r3.id;
        r15 = -r15;
        r9 = (long) r15;
        r15 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1));
        if (r15 == 0) goto L_0x0126;
    L_0x0123:
        goto L_0x0161;
    L_0x0124:
        r27 = r13;
    L_0x0126:
        if (r3 == 0) goto L_0x0147;
    L_0x0128:
        r3 = r3.megagroup;
        if (r3 == 0) goto L_0x0147;
    L_0x012c:
        r3 = r2.flags;
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r9;
        r2.flags = r3;
        goto L_0x0147;
    L_0x0134:
        r27 = r13;
        r3 = r3.chat_id;
        if (r3 == 0) goto L_0x0147;
    L_0x013a:
        r3 = r4.get(r3);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x0147;
    L_0x0142:
        r3 = r3.migrated_to;
        if (r3 == 0) goto L_0x0147;
    L_0x0146:
        goto L_0x0161;
    L_0x0147:
        r3 = new org.telegram.messenger.MessageObject;
        r9 = r14.currentAccount;
        r22 = 0;
        r17 = r3;
        r18 = r9;
        r19 = r2;
        r20 = r6;
        r21 = r4;
        r17.<init>(r18, r19, r20, r21, r22);
        r9 = r3.getDialogId();
        r8.put(r9, r3);
    L_0x0161:
        r0 = r0 + 1;
        r9 = r24;
        r10 = r25;
        r13 = r27;
        r12 = r29;
        r15 = 0;
        goto L_0x00e6;
    L_0x016e:
        r27 = r13;
        if (r31 != 0) goto L_0x024e;
    L_0x0172:
        if (r32 != 0) goto L_0x024e;
    L_0x0174:
        r0 = 0;
        r2 = r5[r0];
        r3 = -1;
        if (r2 == r3) goto L_0x024e;
    L_0x017a:
        r9 = r25;
        if (r9 != 0) goto L_0x024e;
    L_0x017e:
        r2 = r23.getUserConfig();
        r10 = r24;
        r2 = r2.getTotalDialogsCount(r10);
        if (r1 == 0) goto L_0x021d;
    L_0x018a:
        r3 = r1.id;
        r5 = r5[r0];
        if (r3 == r5) goto L_0x021d;
    L_0x0190:
        r0 = r11.dialogs;
        r0 = r0.size();
        r2 = r2 + r0;
        r0 = r1.id;
        r3 = r1.date;
        r1 = r1.to_id;
        r5 = r1.channel_id;
        if (r5 == 0) goto L_0x01c7;
    L_0x01a1:
        r1 = 0;
    L_0x01a2:
        r12 = r11.chats;
        r12 = r12.size();
        if (r1 >= r12) goto L_0x01bc;
    L_0x01aa:
        r12 = r11.chats;
        r12 = r12.get(r1);
        r12 = (org.telegram.tgnet.TLRPC.Chat) r12;
        r13 = r12.id;
        if (r13 != r5) goto L_0x01b9;
    L_0x01b6:
        r12 = r12.access_hash;
        goto L_0x01be;
    L_0x01b9:
        r1 = r1 + 1;
        goto L_0x01a2;
    L_0x01bc:
        r12 = r17;
    L_0x01be:
        r15 = r2;
        r19 = r12;
        r12 = 0;
        r2 = r0;
        r13 = r5;
    L_0x01c4:
        r5 = 0;
        goto L_0x022a;
    L_0x01c7:
        r5 = r1.chat_id;
        if (r5 == 0) goto L_0x01ef;
    L_0x01cb:
        r1 = 0;
    L_0x01cc:
        r12 = r11.chats;
        r12 = r12.size();
        if (r1 >= r12) goto L_0x01e6;
    L_0x01d4:
        r12 = r11.chats;
        r12 = r12.get(r1);
        r12 = (org.telegram.tgnet.TLRPC.Chat) r12;
        r13 = r12.id;
        if (r13 != r5) goto L_0x01e3;
    L_0x01e0:
        r12 = r12.access_hash;
        goto L_0x01e8;
    L_0x01e3:
        r1 = r1 + 1;
        goto L_0x01cc;
    L_0x01e6:
        r12 = r17;
    L_0x01e8:
        r15 = r2;
        r19 = r12;
        r13 = 0;
        r2 = r0;
        r12 = r5;
        goto L_0x01c4;
    L_0x01ef:
        r1 = r1.user_id;
        if (r1 == 0) goto L_0x0215;
    L_0x01f3:
        r5 = 0;
    L_0x01f4:
        r12 = r11.users;
        r12 = r12.size();
        if (r5 >= r12) goto L_0x020e;
    L_0x01fc:
        r12 = r11.users;
        r12 = r12.get(r5);
        r12 = (org.telegram.tgnet.TLRPC.User) r12;
        r13 = r12.id;
        if (r13 != r1) goto L_0x020b;
    L_0x0208:
        r12 = r12.access_hash;
        goto L_0x0210;
    L_0x020b:
        r5 = r5 + 1;
        goto L_0x01f4;
    L_0x020e:
        r12 = r17;
    L_0x0210:
        r5 = r1;
        r15 = r2;
        r19 = r12;
        goto L_0x0219;
    L_0x0215:
        r15 = r2;
        r19 = r17;
        r5 = 0;
    L_0x0219:
        r12 = 0;
        r13 = 0;
        r2 = r0;
        goto L_0x022a;
    L_0x021d:
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r15 = r2;
        r19 = r17;
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r3 = 0;
        r5 = 0;
        r12 = 0;
        r13 = 0;
    L_0x022a:
        r0 = r23.getUserConfig();
        r1 = r24;
        r9 = r4;
        r4 = r5;
        r5 = r12;
        r12 = r6;
        r6 = r13;
        r13 = r7;
        r21 = r12;
        r12 = r8;
        r7 = r19;
        r0.setDialogsLoadOffset(r1, r2, r3, r4, r5, r6, r7);
        r0 = r23.getUserConfig();
        r0.setTotalDialogsCount(r10, r15);
        r0 = r23.getUserConfig();
        r1 = 0;
        r0.saveConfig(r1);
        goto L_0x0255;
    L_0x024e:
        r10 = r24;
        r9 = r4;
        r21 = r6;
        r13 = r7;
        r12 = r8;
    L_0x0255:
        r15 = new java.util.ArrayList;
        r15.<init>();
        r0 = 0;
    L_0x025b:
        r1 = r11.dialogs;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x0375;
    L_0x0263:
        r1 = r11.dialogs;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Dialog) r1;
        org.telegram.messenger.DialogObject.initDialog(r1);
        r2 = r1.id;
        r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1));
        if (r4 != 0) goto L_0x027a;
    L_0x0274:
        r4 = r25;
        r2 = r27;
        goto L_0x036f;
    L_0x027a:
        r4 = (int) r2;
        r5 = 32;
        r2 = r2 >> r5;
        r3 = (int) r2;
        if (r4 != 0) goto L_0x028f;
    L_0x0281:
        if (r27 == 0) goto L_0x028f;
    L_0x0283:
        r2 = r27;
        r3 = r2.get(r3);
        if (r3 != 0) goto L_0x0291;
    L_0x028b:
        r4 = r25;
        goto L_0x036f;
    L_0x028f:
        r2 = r27;
    L_0x0291:
        r3 = r14.proxyDialogId;
        r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1));
        if (r5 == 0) goto L_0x029f;
    L_0x0297:
        r5 = r1.id;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 != 0) goto L_0x029f;
    L_0x029d:
        r14.proxyDialog = r1;
    L_0x029f:
        r3 = r1.last_message_date;
        if (r3 != 0) goto L_0x02b3;
    L_0x02a3:
        r3 = r1.id;
        r3 = r12.get(r3);
        r3 = (org.telegram.messenger.MessageObject) r3;
        if (r3 == 0) goto L_0x02b3;
    L_0x02ad:
        r3 = r3.messageOwner;
        r3 = r3.date;
        r1.last_message_date = r3;
    L_0x02b3:
        r3 = org.telegram.messenger.DialogObject.isChannel(r1);
        if (r3 == 0) goto L_0x02e5;
    L_0x02b9:
        r3 = r1.id;
        r4 = (int) r3;
        r3 = -r4;
        r3 = r9.get(r3);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x02d8;
    L_0x02c5:
        r4 = r3.megagroup;
        r3 = r3.left;
        if (r3 == 0) goto L_0x02d9;
    L_0x02cb:
        r5 = r14.proxyDialogId;
        r3 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1));
        if (r3 == 0) goto L_0x028b;
    L_0x02d1:
        r7 = r1.id;
        r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r3 == 0) goto L_0x02d9;
    L_0x02d7:
        goto L_0x028b;
    L_0x02d8:
        r4 = 1;
    L_0x02d9:
        r3 = r14.channelsPts;
        r5 = r1.id;
        r6 = (int) r5;
        r5 = -r6;
        r6 = r1.pts;
        r3.put(r5, r6);
        goto L_0x02fa;
    L_0x02e5:
        r3 = r1.id;
        r5 = (int) r3;
        if (r5 >= 0) goto L_0x02f9;
    L_0x02ea:
        r4 = (int) r3;
        r3 = -r4;
        r3 = r9.get(r3);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x02f9;
    L_0x02f4:
        r3 = r3.migrated_to;
        if (r3 == 0) goto L_0x02f9;
    L_0x02f8:
        goto L_0x028b;
    L_0x02f9:
        r4 = 1;
    L_0x02fa:
        r5 = r1.id;
        r13.put(r5, r1);
        if (r4 == 0) goto L_0x0317;
    L_0x0301:
        r3 = r14.DIALOGS_LOAD_TYPE_CACHE;
        r4 = r25;
        if (r4 != r3) goto L_0x0319;
    L_0x0307:
        r3 = r1.read_outbox_max_id;
        if (r3 == 0) goto L_0x030f;
    L_0x030b:
        r3 = r1.read_inbox_max_id;
        if (r3 != 0) goto L_0x0319;
    L_0x030f:
        r3 = r1.top_message;
        if (r3 == 0) goto L_0x0319;
    L_0x0313:
        r15.add(r1);
        goto L_0x0319;
    L_0x0317:
        r4 = r25;
    L_0x0319:
        r3 = r14.dialogs_read_inbox_max;
        r5 = r1.id;
        r5 = java.lang.Long.valueOf(r5);
        r3 = r3.get(r5);
        r3 = (java.lang.Integer) r3;
        if (r3 != 0) goto L_0x032b;
    L_0x0329:
        r3 = r16;
    L_0x032b:
        r5 = r14.dialogs_read_inbox_max;
        r6 = r1.id;
        r6 = java.lang.Long.valueOf(r6);
        r3 = r3.intValue();
        r7 = r1.read_inbox_max_id;
        r3 = java.lang.Math.max(r3, r7);
        r3 = java.lang.Integer.valueOf(r3);
        r5.put(r6, r3);
        r3 = r14.dialogs_read_outbox_max;
        r5 = r1.id;
        r5 = java.lang.Long.valueOf(r5);
        r3 = r3.get(r5);
        r3 = (java.lang.Integer) r3;
        if (r3 != 0) goto L_0x0356;
    L_0x0354:
        r3 = r16;
    L_0x0356:
        r5 = r14.dialogs_read_outbox_max;
        r6 = r1.id;
        r6 = java.lang.Long.valueOf(r6);
        r3 = r3.intValue();
        r1 = r1.read_outbox_max_id;
        r1 = java.lang.Math.max(r3, r1);
        r1 = java.lang.Integer.valueOf(r1);
        r5.put(r6, r1);
    L_0x036f:
        r0 = r0 + 1;
        r27 = r2;
        goto L_0x025b;
    L_0x0375:
        r4 = r25;
        r0 = r14.DIALOGS_LOAD_TYPE_CACHE;
        if (r4 == r0) goto L_0x0415;
    L_0x037b:
        r0 = r11.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r0);
        r0 = 0;
    L_0x0381:
        r1 = r11.messages;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x040c;
    L_0x0389:
        r1 = r11.messages;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;
        r2 = r1.action;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r3 == 0) goto L_0x03b5;
    L_0x0397:
        r2 = r2.user_id;
        r3 = r21;
        r2 = r3.get(r2);
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        if (r2 == 0) goto L_0x03b7;
    L_0x03a3:
        r2 = r2.bot;
        if (r2 == 0) goto L_0x03b7;
    L_0x03a7:
        r2 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r2.<init>();
        r1.reply_markup = r2;
        r2 = r1.flags;
        r2 = r2 | 64;
        r1.flags = r2;
        goto L_0x03b7;
    L_0x03b5:
        r3 = r21;
    L_0x03b7:
        r2 = r1.action;
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r5 != 0) goto L_0x0401;
    L_0x03bd:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r2 == 0) goto L_0x03c2;
    L_0x03c1:
        goto L_0x0401;
    L_0x03c2:
        r2 = r1.out;
        if (r2 == 0) goto L_0x03c9;
    L_0x03c6:
        r2 = r14.dialogs_read_outbox_max;
        goto L_0x03cb;
    L_0x03c9:
        r2 = r14.dialogs_read_inbox_max;
    L_0x03cb:
        r5 = r1.dialog_id;
        r5 = java.lang.Long.valueOf(r5);
        r5 = r2.get(r5);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x03f2;
    L_0x03d9:
        r5 = r23.getMessagesStorage();
        r6 = r1.out;
        r7 = r1.dialog_id;
        r5 = r5.getDialogReadMax(r6, r7);
        r5 = java.lang.Integer.valueOf(r5);
        r6 = r1.dialog_id;
        r6 = java.lang.Long.valueOf(r6);
        r2.put(r6, r5);
    L_0x03f2:
        r2 = r5.intValue();
        r5 = r1.id;
        if (r2 >= r5) goto L_0x03fc;
    L_0x03fa:
        r2 = 1;
        goto L_0x03fd;
    L_0x03fc:
        r2 = 0;
    L_0x03fd:
        r1.unread = r2;
        r2 = 0;
        goto L_0x0406;
    L_0x0401:
        r2 = 0;
        r1.unread = r2;
        r1.media_unread = r2;
    L_0x0406:
        r0 = r0 + 1;
        r21 = r3;
        goto L_0x0381;
    L_0x040c:
        r2 = 0;
        r0 = r23.getMessagesStorage();
        r0.putDialogs(r11, r2);
        goto L_0x0416;
    L_0x0415:
        r2 = 0;
    L_0x0416:
        r0 = r14.DIALOGS_LOAD_TYPE_CHANNEL;
        if (r4 != r0) goto L_0x042c;
    L_0x041a:
        r0 = r11.chats;
        r0 = r0.get(r2);
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r1 = r0.id;
        r14.getChannelDifference(r1);
        r0 = r0.id;
        r14.checkChannelInviter(r0);
    L_0x042c:
        r16 = new org.telegram.messenger.-$$Lambda$MessagesController$Ako6wVf0S5HYHkh7BQByfSq2n88;
        r0 = r16;
        r1 = r23;
        r2 = r25;
        r3 = r26;
        r4 = r29;
        r5 = r32;
        r6 = r24;
        r7 = r13;
        r8 = r12;
        r10 = r28;
        r11 = r31;
        r12 = r30;
        r13 = r15;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogs$145$MessagesController(int, int, org.telegram.tgnet.TLRPC$messages_Dialogs, boolean, int, java.util.ArrayList, int, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$null$143$MessagesController(messages_Dialogs messages_dialogs, int i, boolean z, int[] iArr, int i2) {
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

    public /* synthetic */ void lambda$null$144$MessagesController(int i, messages_Dialogs messages_dialogs, ArrayList arrayList, boolean z, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, SparseArray sparseArray, int i3, boolean z2, int i4, ArrayList arrayList2) {
        int i5;
        int i6;
        Object obj;
        int i7 = i;
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        ArrayList arrayList3 = arrayList;
        int i8 = i2;
        LongSparseArray longSparseArray3 = longSparseArray;
        LongSparseArray longSparseArray4 = longSparseArray2;
        int i9 = i3;
        if (i7 != this.DIALOGS_LOAD_TYPE_CACHE) {
            applyDialogsNotificationsSettings(messages_dialogs2.dialogs);
            getMediaDataController().loadDraftsIfNeed();
        }
        putUsers(messages_dialogs2.users, i7 == this.DIALOGS_LOAD_TYPE_CACHE);
        putChats(messages_dialogs2.chats, i7 == this.DIALOGS_LOAD_TYPE_CACHE);
        if (arrayList3 != null) {
            for (i5 = 0; i5 < arrayList.size(); i5++) {
                EncryptedChat encryptedChat = (EncryptedChat) arrayList3.get(i5);
                if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 101) {
                    getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, null);
                }
                putEncryptedChat(encryptedChat, true);
            }
        }
        if (!(z || i7 == this.DIALOGS_LOAD_TYPE_UNKNOWN || i7 == this.DIALOGS_LOAD_TYPE_CHANNEL)) {
            this.loadingDialogs.put(i8, false);
        }
        this.dialogsLoaded = true;
        if (!z || this.allDialogs.isEmpty()) {
            i6 = 0;
        } else {
            arrayList3 = this.allDialogs;
            i6 = ((Dialog) arrayList3.get(arrayList3.size() - 1)).last_message_date;
        }
        i5 = 0;
        int i10 = 0;
        Object obj2 = null;
        while (i5 < longSparseArray.size()) {
            int i11;
            long keyAt = longSparseArray3.keyAt(i5);
            Dialog dialog = (Dialog) longSparseArray3.valueAt(i5);
            Dialog dialog2 = i7 != this.DIALOGS_LOAD_TYPE_UNKNOWN ? (Dialog) this.dialogs_dict.get(keyAt) : null;
            if (z && dialog2 != null) {
                dialog2.folder_id = dialog.folder_id;
            }
            if (!z || i6 == 0 || dialog.last_message_date >= i6) {
                if (i7 == this.DIALOGS_LOAD_TYPE_CACHE || !(dialog.draft instanceof TL_draftMessage)) {
                    i11 = i6;
                } else {
                    i11 = i6;
                    getMediaDataController().saveDraft(dialog.id, dialog.draft, null, false);
                }
                if (dialog.folder_id != i8) {
                    i10++;
                }
                MessageObject messageObject;
                if (dialog2 == null) {
                    this.dialogs_dict.put(keyAt, dialog);
                    messageObject = (MessageObject) longSparseArray4.get(dialog.id);
                    this.dialogMessage.put(keyAt, messageObject);
                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        keyAt = messageObject.messageOwner.random_id;
                        if (keyAt != 0) {
                            this.dialogMessagesByRandomIds.put(keyAt, messageObject);
                        }
                    }
                    obj2 = 1;
                } else {
                    if (i7 != this.DIALOGS_LOAD_TYPE_CACHE) {
                        dialog2.notify_settings = dialog.notify_settings;
                    }
                    dialog2.pinned = dialog.pinned;
                    dialog2.pinnedNum = dialog.pinnedNum;
                    messageObject = (MessageObject) this.dialogMessage.get(keyAt);
                    MessageObject messageObject2;
                    if ((messageObject == null || !messageObject.deleted) && messageObject != null && dialog2.top_message <= 0) {
                        i6 = i10;
                        obj = obj2;
                        messageObject2 = (MessageObject) longSparseArray4.get(dialog.id);
                        if (messageObject.deleted || messageObject2 == null || messageObject2.messageOwner.date > messageObject.messageOwner.date) {
                            this.dialogs_dict.put(keyAt, dialog);
                            this.dialogMessage.put(keyAt, messageObject2);
                            if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                                if (messageObject2 != null) {
                                    keyAt = messageObject2.messageOwner.random_id;
                                    if (keyAt != 0) {
                                        this.dialogMessagesByRandomIds.put(keyAt, messageObject2);
                                    }
                                }
                            }
                            this.dialogMessagesByIds.remove(messageObject.getId());
                            keyAt = messageObject.messageOwner.random_id;
                            if (keyAt != 0) {
                                this.dialogMessagesByRandomIds.remove(keyAt);
                            }
                        }
                    } else {
                        i6 = i10;
                        obj = obj2;
                        if (dialog.top_message >= dialog2.top_message) {
                            this.dialogs_dict.put(keyAt, dialog);
                            messageObject2 = (MessageObject) longSparseArray4.get(dialog.id);
                            this.dialogMessage.put(keyAt, messageObject2);
                            if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                                if (messageObject2 != null) {
                                    keyAt = messageObject2.messageOwner.random_id;
                                    if (keyAt != 0) {
                                        this.dialogMessagesByRandomIds.put(keyAt, messageObject2);
                                    }
                                }
                            }
                            if (messageObject != null) {
                                this.dialogMessagesByIds.remove(messageObject.getId());
                                keyAt = messageObject.messageOwner.random_id;
                                if (keyAt != 0) {
                                    this.dialogMessagesByRandomIds.remove(keyAt);
                                }
                            }
                        }
                    }
                    i10 = i6;
                    obj2 = obj;
                }
            } else {
                i11 = i6;
            }
            i5++;
            messages_dialogs2 = messages_dialogs;
            longSparseArray3 = longSparseArray;
            i6 = i11;
        }
        obj = obj2;
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (i6 = 0; i6 < size; i6++) {
            this.allDialogs.add(this.dialogs_dict.valueAt(i6));
        }
        sortDialogs(z ? sparseArray : null);
        putAllNeededDraftDialogs();
        if (!(i7 == this.DIALOGS_LOAD_TYPE_CHANNEL || i7 == this.DIALOGS_LOAD_TYPE_UNKNOWN || z)) {
            SparseBooleanArray sparseBooleanArray = this.dialogsEndReached;
            messages_Dialogs messages_dialogs3 = messages_dialogs;
            boolean z3 = (messages_dialogs3.dialogs.size() == 0 || messages_dialogs3.dialogs.size() != i9) && i7 == 0;
            sparseBooleanArray.put(i8, z3);
            if (i10 <= 0 || i10 >= 20 || i8 != 0) {
                z3 = true;
            } else {
                z3 = true;
                this.dialogsEndReached.put(1, true);
                if (getUserConfig().getDialogLoadOffsets(i8)[0] == Integer.MAX_VALUE) {
                    this.serverDialogsEndReached.put(1, true);
                }
            }
            if (!z2) {
                sparseBooleanArray = this.serverDialogsEndReached;
                if ((messages_dialogs3.dialogs.size() != 0 && messages_dialogs3.dialogs.size() == i9) || i7 != 0) {
                    z3 = false;
                }
                sparseBooleanArray.put(i8, z3);
            }
        }
        size = getUserConfig().getTotalDialogsCount(i8);
        int[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(i8);
        if (z2 || z || size >= 400) {
            size = 0;
        } else {
            size = 0;
            if (!(dialogLoadOffsets[0] == -1 || dialogLoadOffsets[0] == Integer.MAX_VALUE)) {
                loadDialogs(i8, 0, 100, false);
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[size]);
        if (z) {
            getUserConfig().migrateOffsetId = i4;
            getUserConfig().saveConfig(size);
            this.migratingDialogs = size;
            getNotificationCenter().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[size]);
        } else {
            generateUpdateMessage();
            if (obj == null && i7 == this.DIALOGS_LOAD_TYPE_CACHE) {
                loadDialogs(i8, size, i9, size);
            }
        }
        migrateDialogs(getUserConfig().migrateOffsetId, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
        if (!arrayList2.isEmpty()) {
            reloadDialogsReadValue(arrayList2, 0);
        }
        loadUnreadDialogs();
    }

    private void applyDialogNotificationsSettings(long j, PeerNotifySettings peerNotifySettings) {
        long j2 = j;
        PeerNotifySettings peerNotifySettings2 = peerNotifySettings;
        if (peerNotifySettings2 != null) {
            SharedPreferences sharedPreferences = this.notificationsPreferences;
            StringBuilder stringBuilder = new StringBuilder();
            String str = "notify2_";
            stringBuilder.append(str);
            stringBuilder.append(j2);
            int i = sharedPreferences.getInt(stringBuilder.toString(), -1);
            SharedPreferences sharedPreferences2 = this.notificationsPreferences;
            StringBuilder stringBuilder2 = new StringBuilder();
            String str2 = "notifyuntil_";
            stringBuilder2.append(str2);
            stringBuilder2.append(j2);
            int i2 = sharedPreferences2.getInt(stringBuilder2.toString(), 0);
            Editor edit = this.notificationsPreferences.edit();
            Dialog dialog = (Dialog) this.dialogs_dict.get(j2);
            if (dialog != null) {
                dialog.notify_settings = peerNotifySettings2;
            }
            String str3 = "silent_";
            StringBuilder stringBuilder3;
            if ((peerNotifySettings2.flags & 2) != 0) {
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str3);
                stringBuilder3.append(j2);
                edit.putBoolean(stringBuilder3.toString(), peerNotifySettings2.silent);
            } else {
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str3);
                stringBuilder3.append(j2);
                edit.remove(stringBuilder3.toString());
            }
            Object obj = 1;
            StringBuilder stringBuilder4;
            if ((peerNotifySettings2.flags & 4) == 0) {
                if (i != -1) {
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(str);
                    stringBuilder4.append(j2);
                    edit.remove(stringBuilder4.toString());
                } else {
                    obj = null;
                }
                getMessagesStorage().setDialogFlags(j2, 0);
            } else if (peerNotifySettings2.mute_until > getConnectionsManager().getCurrentTime()) {
                int i3;
                if (peerNotifySettings2.mute_until <= getConnectionsManager().getCurrentTime() + 31536000) {
                    if (i == 3 && i2 == peerNotifySettings2.mute_until) {
                        obj = null;
                    } else {
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(str);
                        stringBuilder5.append(j2);
                        edit.putInt(stringBuilder5.toString(), 3);
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(str2);
                        stringBuilder5.append(j2);
                        edit.putInt(stringBuilder5.toString(), peerNotifySettings2.mute_until);
                        if (dialog != null) {
                            dialog.notify_settings.mute_until = 0;
                        }
                    }
                    i3 = peerNotifySettings2.mute_until;
                } else if (i != 2) {
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(str);
                    stringBuilder4.append(j2);
                    edit.putInt(stringBuilder4.toString(), 2);
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                    }
                    i3 = 0;
                } else {
                    i3 = 0;
                    obj = null;
                }
                getMessagesStorage().setDialogFlags(j2, (((long) i3) << 32) | 1);
                getNotificationsController().removeNotificationsForDialog(j2);
            } else {
                if (i == 0 || i == 1) {
                    obj = null;
                } else {
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(str);
                    stringBuilder4.append(j2);
                    edit.putInt(stringBuilder4.toString(), 0);
                }
                getMessagesStorage().setDialogFlags(j2, 0);
            }
            edit.commit();
            if (obj != null) {
                getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
            }
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<Dialog> arrayList) {
        Editor editor = null;
        for (int i = 0; i < arrayList.size(); i++) {
            Dialog dialog = (Dialog) arrayList.get(i);
            if (dialog.peer != null && (dialog.notify_settings instanceof TL_peerNotifySettings)) {
                StringBuilder stringBuilder;
                if (editor == null) {
                    editor = this.notificationsPreferences.edit();
                }
                Peer peer = dialog.peer;
                int i2 = peer.user_id;
                if (i2 == 0) {
                    i2 = peer.chat_id;
                    if (i2 != 0) {
                        i2 = -i2;
                    } else {
                        i2 = -peer.channel_id;
                    }
                }
                String str = "silent_";
                if ((dialog.notify_settings.flags & 2) != 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(i2);
                    editor.putBoolean(stringBuilder.toString(), dialog.notify_settings.silent);
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(i2);
                    editor.remove(stringBuilder.toString());
                }
                PeerNotifySettings peerNotifySettings = dialog.notify_settings;
                String str2 = "notify2_";
                StringBuilder stringBuilder2;
                if ((peerNotifySettings.flags & 4) == 0) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str2);
                    stringBuilder2.append(i2);
                    editor.remove(stringBuilder2.toString());
                } else if (peerNotifySettings.mute_until <= getConnectionsManager().getCurrentTime()) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str2);
                    stringBuilder2.append(i2);
                    editor.putInt(stringBuilder2.toString(), 0);
                } else if (dialog.notify_settings.mute_until > getConnectionsManager().getCurrentTime() + 31536000) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append(i2);
                    editor.putInt(stringBuilder.toString(), 2);
                    dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append(i2);
                    editor.putInt(stringBuilder.toString(), 3);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notifyuntil_");
                    stringBuilder.append(i2);
                    editor.putInt(stringBuilder.toString(), dialog.notify_settings.mute_until);
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
    }

    public void reloadMentionsCountForChannels(ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$knnzDPoxWeJ_3JhzhjM0dfYX6Lg(this, arrayList));
    }

    public /* synthetic */ void lambda$reloadMentionsCountForChannels$148$MessagesController(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            long j = (long) (-((Integer) arrayList.get(i)).intValue());
            TL_messages_getUnreadMentions tL_messages_getUnreadMentions = new TL_messages_getUnreadMentions();
            tL_messages_getUnreadMentions.peer = getInputPeer((int) j);
            tL_messages_getUnreadMentions.limit = 1;
            getConnectionsManager().sendRequest(tL_messages_getUnreadMentions, new -$$Lambda$MessagesController$OaKPy0Z_yQq4h6ds5pvGsfUMiCs(this, j));
        }
    }

    public /* synthetic */ void lambda$null$147$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$6zprsSWWT0r7SIFUYKMS_HpuPeQ(this, tLObject, j));
    }

    public /* synthetic */ void lambda$null$146$MessagesController(TLObject tLObject, long j) {
        messages_Messages messages_messages = (messages_Messages) tLObject;
        if (messages_messages != null) {
            int i = messages_messages.count;
            if (i == 0) {
                i = messages_messages.messages.size();
            }
            getMessagesStorage().resetMentionsCount(j, i);
        }
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> longSparseArray, LongSparseArray<Integer> longSparseArray2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ZyC4F8DQNhvd-ST1nW3IC1RXFJI(this, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$149$MessagesController(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        Object obj;
        int i;
        if (longSparseArray != null) {
            obj = null;
            for (i = 0; i < longSparseArray.size(); i++) {
                long keyAt = longSparseArray.keyAt(i);
                Dialog dialog = (Dialog) this.dialogs_dict.get(keyAt);
                if (dialog != null) {
                    int i2 = dialog.unread_count;
                    dialog.unread_count = ((Integer) longSparseArray.valueAt(i)).intValue();
                    if (i2 != 0 && dialog.unread_count == 0 && !isDialogMuted(keyAt)) {
                        this.unreadUnmutedDialogs--;
                    } else if (!(i2 != 0 || dialog.unread_mark || dialog.unread_count == 0 || isDialogMuted(keyAt))) {
                        this.unreadUnmutedDialogs++;
                    }
                    if (dialog.folder_id == 1) {
                        obj = 1;
                    }
                }
            }
        } else {
            obj = null;
        }
        if (longSparseArray2 != null) {
            for (i = 0; i < longSparseArray2.size(); i++) {
                Dialog dialog2 = (Dialog) this.dialogs_dict.get(longSparseArray2.keyAt(i));
                if (dialog2 != null) {
                    dialog2.unread_mentions_count = ((Integer) longSparseArray2.valueAt(i)).intValue();
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(dialog2.id))) {
                        getNotificationCenter().postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(dialog2.id), Integer.valueOf(dialog2.unread_mentions_count));
                    }
                }
            }
        }
        if (obj != null) {
            updateFolderUnreadCounter();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        if (longSparseArray != null) {
            getNotificationsController().processDialogsUpdateRead(longSparseArray);
        }
    }

    private void updateFolderUnreadCounter() {
        Dialog dialog = (Dialog) this.dialogs_dict.get(DialogObject.makeFolderDialogId(1));
        if (dialog instanceof TL_dialogFolder) {
            int i = 0;
            dialog.unread_count = 0;
            dialog.unread_mentions_count = 0;
            ArrayList arrayList = (ArrayList) this.dialogsByFolder.get(1);
            if (arrayList != null) {
                boolean z = getNotificationsController().showBadgeMessages;
                int size = arrayList.size();
                while (i < size) {
                    Dialog dialog2 = (Dialog) arrayList.get(i);
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
                    i++;
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void checkLastDialogMessage(Dialog dialog, InputPeer inputPeer, long j) {
        Throwable e;
        int i = (int) dialog.id;
        if (i != 0 && this.checkingLastMessagesDialogs.indexOfKey(i) < 0) {
            TL_messages_getHistory tL_messages_getHistory = new TL_messages_getHistory();
            if (inputPeer == null) {
                inputPeer = getInputPeer(i);
            }
            tL_messages_getHistory.peer = inputPeer;
            if (tL_messages_getHistory.peer != null) {
                tL_messages_getHistory.limit = 1;
                this.checkingLastMessagesDialogs.put(i, true);
                if (j == 0) {
                    NativeByteBuffer nativeByteBuffer;
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
                        Throwable th = e3;
                        nativeByteBuffer = null;
                        e = th;
                        FileLog.e(e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_getHistory, new -$$Lambda$MessagesController$4Jwx_5HhsGiQxe4xTNN54kOTaDA(this, i, dialog, j));
                    }
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                }
                getConnectionsManager().sendRequest(tL_messages_getHistory, new -$$Lambda$MessagesController$4Jwx_5HhsGiQxe4xTNN54kOTaDA(this, i, dialog, j));
            }
        }
    }

    public /* synthetic */ void lambda$checkLastDialogMessage$152$MessagesController(int i, Dialog dialog, long j, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            removeDeletedMessagesFromArray((long) i, messages_messages.messages);
            if (messages_messages.messages.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ppNjsWd8EkGI25R6xpToCuUgugU(this, dialog));
            } else {
                TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
                Message message = (Message) messages_messages.messages.get(0);
                TL_dialog tL_dialog = new TL_dialog();
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
                processDialogsUpdate(tL_messages_dialogs, null);
                getMessagesStorage().putMessages(messages_messages.messages, true, true, false, getDownloadController().getAutodownloadMask(), true);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$0bKonyMt3MYMdfaGZLwlSU3meJc(this, i));
    }

    public /* synthetic */ void lambda$null$150$MessagesController(Dialog dialog) {
        Dialog dialog2 = (Dialog) this.dialogs_dict.get(dialog.id);
        if (dialog2 != null && dialog2.top_message == 0 && getMediaDataController().getDraft(dialog.id) == null) {
            deleteDialog(dialog.id, 3);
        }
    }

    public /* synthetic */ void lambda$null$151$MessagesController(int i) {
        this.checkingLastMessagesDialogs.delete(i);
    }

    public void processDialogsUpdate(messages_Dialogs messages_dialogs, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$a1aBMa6MoHu0lc3G2UwgqOGPRE0(this, messages_dialogs));
    }

    public /* synthetic */ void lambda$processDialogsUpdate$154$MessagesController(messages_Dialogs messages_dialogs) {
        int i;
        long j;
        Chat chat;
        MessageObject messageObject;
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        SparseArray sparseArray = new SparseArray(messages_dialogs2.users.size());
        SparseArray sparseArray2 = new SparseArray(messages_dialogs2.chats.size());
        LongSparseArray longSparseArray3 = new LongSparseArray();
        int i2 = 0;
        Integer valueOf = Integer.valueOf(0);
        for (i = 0; i < messages_dialogs2.users.size(); i++) {
            User user = (User) messages_dialogs2.users.get(i);
            sparseArray.put(user.id, user);
        }
        for (i = 0; i < messages_dialogs2.chats.size(); i++) {
            Chat chat2 = (Chat) messages_dialogs2.chats.get(i);
            sparseArray2.put(chat2.id, chat2);
        }
        int i3 = 0;
        while (true) {
            j = 0;
            if (i3 >= messages_dialogs2.messages.size()) {
                break;
            }
            Message message = (Message) messages_dialogs2.messages.get(i3);
            long j2 = this.proxyDialogId;
            if (j2 == 0 || j2 != message.dialog_id) {
                Peer peer = message.to_id;
                int i4 = peer.channel_id;
                if (i4 != 0) {
                    chat = (Chat) sparseArray2.get(i4);
                    if (chat != null && chat.left) {
                        i3++;
                    }
                } else {
                    i = peer.chat_id;
                    if (i != 0) {
                        chat = (Chat) sparseArray2.get(i);
                        if (!(chat == null || chat.migrated_to == null)) {
                            i3++;
                        }
                    }
                }
            }
            MessageObject messageObject2 = messageObject;
            messageObject = new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, false);
            longSparseArray2.put(messageObject2.getDialogId(), messageObject2);
            i3++;
        }
        while (i2 < messages_dialogs2.dialogs.size()) {
            Dialog dialog = (Dialog) messages_dialogs2.dialogs.get(i2);
            DialogObject.initDialog(dialog);
            long j3 = this.proxyDialogId;
            if (j3 == j || j3 != dialog.id) {
                if (DialogObject.isChannel(dialog)) {
                    chat = (Chat) sparseArray2.get(-((int) dialog.id));
                    if (chat != null && chat.left) {
                        i2++;
                        j = 0;
                    }
                } else {
                    long j4 = dialog.id;
                    if (((int) j4) < 0) {
                        chat = (Chat) sparseArray2.get(-((int) j4));
                        if (!(chat == null || chat.migrated_to == null)) {
                            i2++;
                            j = 0;
                        }
                    }
                }
            }
            if (dialog.last_message_date == 0) {
                messageObject = (MessageObject) longSparseArray2.get(dialog.id);
                if (messageObject != null) {
                    dialog.last_message_date = messageObject.messageOwner.date;
                }
            }
            longSparseArray.put(dialog.id, dialog);
            longSparseArray3.put(dialog.id, Integer.valueOf(dialog.unread_count));
            Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
            if (num == null) {
                num = valueOf;
            }
            this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num.intValue(), dialog.read_inbox_max_id)));
            num = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
            if (num == null) {
                num = valueOf;
            }
            this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num.intValue(), dialog.read_outbox_max_id)));
            i2++;
            j = 0;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$4ggpwfv3Bv60WmG67IQ4uiOonCY(this, messages_dialogs, longSparseArray, longSparseArray2, longSparseArray3));
    }

    public /* synthetic */ void lambda$null$153$MessagesController(messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        LongSparseArray longSparseArray4 = longSparseArray;
        LongSparseArray longSparseArray5 = longSparseArray2;
        putUsers(messages_dialogs2.users, true);
        putChats(messages_dialogs2.chats, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray4.keyAt(i);
            Dialog dialog = (Dialog) longSparseArray4.valueAt(i);
            Dialog dialog2 = (Dialog) this.dialogs_dict.get(keyAt);
            if (dialog2 == null) {
                this.nextDialogsCacheOffset.put(dialog.folder_id, this.nextDialogsCacheOffset.get(dialog.folder_id, 0) + 1);
                this.dialogs_dict.put(keyAt, dialog);
                MessageObject messageObject = (MessageObject) longSparseArray5.get(dialog.id);
                this.dialogMessage.put(keyAt, messageObject);
                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                    this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                    long j = messageObject.messageOwner.random_id;
                    if (j != 0) {
                        this.dialogMessagesByRandomIds.put(j, messageObject);
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
                MessageObject messageObject2 = (MessageObject) this.dialogMessage.get(keyAt);
                if (messageObject2 != null && dialog2.top_message <= 0) {
                    MessageObject messageObject3 = (MessageObject) longSparseArray5.get(dialog.id);
                    if (messageObject2.deleted || messageObject3 == null || messageObject3.messageOwner.date > messageObject2.messageOwner.date) {
                        this.dialogs_dict.put(keyAt, dialog);
                        this.dialogMessage.put(keyAt, messageObject3);
                        if (messageObject3 != null && messageObject3.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                            keyAt = messageObject3.messageOwner.random_id;
                            if (keyAt != 0) {
                                this.dialogMessagesByRandomIds.put(keyAt, messageObject3);
                            }
                        }
                        this.dialogMessagesByIds.remove(messageObject2.getId());
                        long j2 = messageObject2.messageOwner.random_id;
                        if (j2 != 0) {
                            this.dialogMessagesByRandomIds.remove(j2);
                        }
                    }
                } else if ((messageObject2 != null && messageObject2.deleted) || dialog.top_message > dialog2.top_message) {
                    long j3;
                    this.dialogs_dict.put(keyAt, dialog);
                    MessageObject messageObject4 = (MessageObject) longSparseArray5.get(dialog.id);
                    this.dialogMessage.put(keyAt, messageObject4);
                    if (messageObject4 != null && messageObject4.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject4.getId(), messageObject4);
                        keyAt = messageObject4.messageOwner.random_id;
                        if (keyAt != 0) {
                            this.dialogMessagesByRandomIds.put(keyAt, messageObject4);
                        }
                    }
                    if (messageObject2 != null) {
                        this.dialogMessagesByIds.remove(messageObject2.getId());
                        keyAt = messageObject2.messageOwner.random_id;
                        j3 = 0;
                        if (keyAt != 0) {
                            this.dialogMessagesByRandomIds.remove(keyAt);
                        }
                    } else {
                        j3 = 0;
                    }
                    if (messageObject4 == null) {
                        checkLastDialogMessage(dialog, null, j3);
                    }
                }
            }
        }
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.allDialogs.add(this.dialogs_dict.valueAt(i4));
        }
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationsController().processDialogsUpdateRead(longSparseArray3);
    }

    public void addToViewsQueue(MessageObject messageObject) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$hBv6ms07sgln27IYlqIEJB2IUyc(this, messageObject));
    }

    public /* synthetic */ void lambda$addToViewsQueue$155$MessagesController(MessageObject messageObject) {
        int dialogId = (int) messageObject.getDialogId();
        int id = messageObject.getId();
        ArrayList arrayList = (ArrayList) this.channelViewsToSend.get(dialogId);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.channelViewsToSend.put(dialogId, arrayList);
        }
        if (!arrayList.contains(Integer.valueOf(id))) {
            arrayList.add(Integer.valueOf(id));
        }
    }

    public void addToPollsQueue(long j, ArrayList<MessageObject> arrayList) {
        SparseArray sparseArray = (SparseArray) this.pollsToCheck.get(j);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            this.pollsToCheck.put(j, sparseArray);
            this.pollsToCheckSize++;
        }
        int size = sparseArray.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ((MessageObject) sparseArray.valueAt(i2)).pollVisibleOnScreen = false;
        }
        size = arrayList.size();
        while (i < size) {
            MessageObject messageObject = (MessageObject) arrayList.get(i);
            if (messageObject.type == 17) {
                int id = messageObject.getId();
                MessageObject messageObject2 = (MessageObject) sparseArray.get(id);
                if (messageObject2 != null) {
                    messageObject2.pollVisibleOnScreen = true;
                } else {
                    sparseArray.put(id, messageObject);
                }
            }
            i++;
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
                TL_channels_readMessageContents tL_channels_readMessageContents = new TL_channels_readMessageContents();
                tL_channels_readMessageContents.channel = getInputChannel(messageObject.messageOwner.to_id.channel_id);
                if (tL_channels_readMessageContents.channel != null) {
                    tL_channels_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
                    getConnectionsManager().sendRequest(tL_channels_readMessageContents, -$$Lambda$MessagesController$P3-y_yXlYAcq9BY9lnJVB8baeIk.INSTANCE);
                }
            } else {
                TL_messages_readMessageContents tL_messages_readMessageContents = new TL_messages_readMessageContents();
                tL_messages_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
                getConnectionsManager().sendRequest(tL_messages_readMessageContents, new -$$Lambda$MessagesController$lIb6mZS9ujMaDiVr0Qc2e8yAgtU(this));
            }
        }
    }

    public /* synthetic */ void lambda$markMessageContentAsRead$157$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public void markMentionMessageAsRead(int i, int i2, long j) {
        getMessagesStorage().markMentionMessageAsRead(i, i2, j);
        if (i2 != 0) {
            TL_channels_readMessageContents tL_channels_readMessageContents = new TL_channels_readMessageContents();
            tL_channels_readMessageContents.channel = getInputChannel(i2);
            if (tL_channels_readMessageContents.channel != null) {
                tL_channels_readMessageContents.id.add(Integer.valueOf(i));
                getConnectionsManager().sendRequest(tL_channels_readMessageContents, -$$Lambda$MessagesController$bKyWz4d9RqjIgwxMvWTMoX7LgpQ.INSTANCE);
            } else {
                return;
            }
        }
        TL_messages_readMessageContents tL_messages_readMessageContents = new TL_messages_readMessageContents();
        tL_messages_readMessageContents.id.add(Integer.valueOf(i));
        getConnectionsManager().sendRequest(tL_messages_readMessageContents, new -$$Lambda$MessagesController$CUgfRSEY4CyHmzYsBtb7aXCT5RU(this));
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$159$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0080  */
    public void markMessageAsRead(int r10, int r11, org.telegram.tgnet.TLRPC.InputChannel r12, int r13, long r14) {
        /*
        r9 = this;
        if (r10 == 0) goto L_0x009a;
    L_0x0002:
        if (r13 > 0) goto L_0x0006;
    L_0x0004:
        goto L_0x009a;
    L_0x0006:
        if (r11 == 0) goto L_0x0011;
    L_0x0008:
        if (r12 != 0) goto L_0x0011;
    L_0x000a:
        r12 = r9.getInputChannel(r11);
        if (r12 != 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        r0 = 0;
        r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x004d;
    L_0x0017:
        r14 = 0;
        r15 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x003e }
        r0 = 16;
        if (r12 == 0) goto L_0x0023;
    L_0x001e:
        r1 = r12.getObjectSize();	 Catch:{ Exception -> 0x003e }
        goto L_0x0024;
    L_0x0023:
        r1 = 0;
    L_0x0024:
        r0 = r0 + r1;
        r15.<init>(r0);	 Catch:{ Exception -> 0x003e }
        r14 = 11;
        r15.writeInt32(r14);	 Catch:{ Exception -> 0x003c }
        r15.writeInt32(r10);	 Catch:{ Exception -> 0x003c }
        r15.writeInt32(r11);	 Catch:{ Exception -> 0x003c }
        r15.writeInt32(r13);	 Catch:{ Exception -> 0x003c }
        if (r11 == 0) goto L_0x0045;
    L_0x0038:
        r12.serializeToStream(r15);	 Catch:{ Exception -> 0x003c }
        goto L_0x0045;
    L_0x003c:
        r14 = move-exception;
        goto L_0x0042;
    L_0x003e:
        r15 = move-exception;
        r8 = r15;
        r15 = r14;
        r14 = r8;
    L_0x0042:
        org.telegram.messenger.FileLog.e(r14);
    L_0x0045:
        r14 = r9.getMessagesStorage();
        r14 = r14.createPendingTask(r15);
    L_0x004d:
        r0 = r9.getConnectionsManager();
        r5 = r0.getCurrentTime();
        r1 = r9.getMessagesStorage();
        r7 = 0;
        r2 = r10;
        r3 = r11;
        r4 = r5;
        r6 = r13;
        r1.createTaskForMid(r2, r3, r4, r5, r6, r7);
        if (r11 == 0) goto L_0x0080;
    L_0x0063:
        r11 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents;
        r11.<init>();
        r11.channel = r12;
        r12 = r11.id;
        r10 = java.lang.Integer.valueOf(r10);
        r12.add(r10);
        r10 = r9.getConnectionsManager();
        r12 = new org.telegram.messenger.-$$Lambda$MessagesController$a8AfI5PchvJOW7BJMH5vX8bjCw0;
        r12.<init>(r9, r14);
        r10.sendRequest(r11, r12);
        goto L_0x009a;
    L_0x0080:
        r11 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents;
        r11.<init>();
        r12 = r11.id;
        r10 = java.lang.Integer.valueOf(r10);
        r12.add(r10);
        r10 = r9.getConnectionsManager();
        r12 = new org.telegram.messenger.-$$Lambda$MessagesController$L2Rh9QgSg-jXaF9a3gzsXr_ZEb0;
        r12.<init>(r9, r14);
        r10.sendRequest(r11, r12);
    L_0x009a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markMessageAsRead(int, int, org.telegram.tgnet.TLRPC$InputChannel, int, long):void");
    }

    public /* synthetic */ void lambda$markMessageAsRead$160$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$markMessageAsRead$161$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void markMessageAsRead(long j, long j2, int i) {
        if (!(j2 == 0 || j == 0 || (i <= 0 && i != Integer.MIN_VALUE))) {
            int i2 = (int) (j >> 32);
            if (((int) j) == 0) {
                EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i2));
                if (encryptedChat != null) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Long.valueOf(j2));
                    getSecretChatHelper().sendMessagesReadMessage(encryptedChat, arrayList, null);
                    if (i > 0) {
                        int currentTime = getConnectionsManager().getCurrentTime();
                        getMessagesStorage().createTaskForSecretChat(encryptedChat.id, currentTime, currentTime, 0, arrayList);
                    }
                }
            }
        }
    }

    private void completeReadTask(ReadTask readTask) {
        long j = readTask.dialogId;
        int i = (int) j;
        int i2 = (int) (j >> 32);
        if (i != 0) {
            TLObject tL_channels_readHistory;
            InputPeer inputPeer = getInputPeer(i);
            if (inputPeer instanceof TL_inputPeerChannel) {
                tL_channels_readHistory = new TL_channels_readHistory();
                tL_channels_readHistory.channel = getInputChannel(-i);
                tL_channels_readHistory.max_id = readTask.maxId;
            } else {
                TLObject tL_messages_readHistory = new TL_messages_readHistory();
                tL_messages_readHistory.peer = inputPeer;
                tL_messages_readHistory.max_id = readTask.maxId;
                tL_channels_readHistory = tL_messages_readHistory;
            }
            getConnectionsManager().sendRequest(tL_channels_readHistory, new -$$Lambda$MessagesController$mms-IBNoBdN67tH76zAO3xKUj68(this));
            return;
        }
        EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i2));
        byte[] bArr = encryptedChat.auth_key;
        if (bArr != null && bArr.length > 1 && (encryptedChat instanceof TL_encryptedChat)) {
            TL_messages_readEncryptedHistory tL_messages_readEncryptedHistory = new TL_messages_readEncryptedHistory();
            tL_messages_readEncryptedHistory.peer = new TL_inputEncryptedChat();
            TL_inputEncryptedChat tL_inputEncryptedChat = tL_messages_readEncryptedHistory.peer;
            tL_inputEncryptedChat.chat_id = encryptedChat.id;
            tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
            tL_messages_readEncryptedHistory.max_date = readTask.maxDate;
            getConnectionsManager().sendRequest(tL_messages_readEncryptedHistory, -$$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0.INSTANCE);
        }
    }

    public /* synthetic */ void lambda$completeReadTask$162$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null && (tLObject instanceof TL_messages_affectedMessages)) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    private void checkReadTasks() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int size = this.readTasks.size();
        int i = 0;
        while (i < size) {
            ReadTask readTask = (ReadTask) this.readTasks.get(i);
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$7Yx77W8GTCRBiWJmBfHwaxMs-rU(this, j));
    }

    public /* synthetic */ void lambda$markDialogAsReadNow$164$MessagesController(long j) {
        ReadTask readTask = (ReadTask) this.readTasksMap.get(j);
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
            TL_messages_readMentions tL_messages_readMentions = new TL_messages_readMentions();
            tL_messages_readMentions.peer = getInputPeer(i);
            getConnectionsManager().sendRequest(tL_messages_readMentions, -$$Lambda$MessagesController$WKod0JD8yhqRRb3q2XGhKTgK1JI.INSTANCE);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0092  */
    public void markDialogAsRead(long r18, int r20, int r21, int r22, boolean r23, int r24, boolean r25, int r26) {
        /*
        r17 = this;
        r9 = r17;
        r10 = r18;
        r12 = r20;
        r13 = r21;
        r14 = r22;
        r0 = (int) r10;
        r1 = 32;
        r2 = r10 >> r1;
        r3 = (int) r2;
        r2 = r17.getNotificationsController();
        r15 = r2.showBadgeMessages;
        r16 = 1;
        if (r0 == 0) goto L_0x0095;
    L_0x001a:
        if (r12 != 0) goto L_0x001d;
    L_0x001c:
        return;
    L_0x001d:
        r2 = (long) r12;
        r4 = (long) r13;
        r13 = 0;
        if (r0 >= 0) goto L_0x003a;
    L_0x0022:
        r0 = -r0;
        r6 = java.lang.Integer.valueOf(r0);
        r6 = r9.getChat(r6);
        r6 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x003a;
    L_0x0031:
        r6 = (long) r0;
        r0 = r6 << r1;
        r2 = r2 | r0;
        r0 = r0 | r4;
        r5 = r0;
        r3 = r2;
        r7 = 1;
        goto L_0x003d;
    L_0x003a:
        r5 = r4;
        r7 = 0;
        r3 = r2;
    L_0x003d:
        r0 = r9.dialogs_read_inbox_max;
        r1 = java.lang.Long.valueOf(r18);
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        if (r0 != 0) goto L_0x004f;
    L_0x004b:
        r0 = java.lang.Integer.valueOf(r13);
    L_0x004f:
        r1 = r9.dialogs_read_inbox_max;
        r2 = java.lang.Long.valueOf(r18);
        r0 = r0.intValue();
        r0 = java.lang.Math.max(r0, r12);
        r0 = java.lang.Integer.valueOf(r0);
        r1.put(r2, r0);
        r0 = r17.getMessagesStorage();
        r1 = r18;
        r8 = r26;
        r0.processPendingRead(r1, r3, r5, r7, r8);
        r0 = r17.getMessagesStorage();
        r8 = r0.getStorageQueue();
        r7 = new org.telegram.messenger.-$$Lambda$MessagesController$XwgyweEtDE07QZ0DyXMbifsq5Ak;
        r0 = r7;
        r1 = r17;
        r2 = r18;
        r4 = r24;
        r5 = r20;
        r6 = r15;
        r15 = r7;
        r7 = r23;
        r0.<init>(r1, r2, r4, r5, r6, r7);
        r8.postRunnable(r15);
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r12 == r0) goto L_0x0092;
    L_0x0091:
        goto L_0x00ee;
    L_0x0092:
        r16 = 0;
        goto L_0x00ee;
    L_0x0095:
        if (r14 != 0) goto L_0x0098;
    L_0x0097:
        return;
    L_0x0098:
        r0 = java.lang.Integer.valueOf(r3);
        r8 = r9.getEncryptedChat(r0);
        r0 = r17.getMessagesStorage();
        r3 = (long) r12;
        r5 = (long) r13;
        r7 = 0;
        r1 = r18;
        r9 = r8;
        r8 = r26;
        r0.processPendingRead(r1, r3, r5, r7, r8);
        r0 = r17.getMessagesStorage();
        r8 = r0.getStorageQueue();
        r7 = new org.telegram.messenger.-$$Lambda$MessagesController$_ziRmGsp8OtorSdwXBpuudGlD6c;
        r0 = r7;
        r1 = r17;
        r2 = r18;
        r4 = r22;
        r5 = r23;
        r6 = r24;
        r10 = r7;
        r7 = r21;
        r11 = r8;
        r8 = r15;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8);
        r11.postRunnable(r10);
        if (r9 == 0) goto L_0x00ee;
    L_0x00d1:
        r0 = r9.ttl;
        if (r0 <= 0) goto L_0x00ee;
    L_0x00d5:
        r0 = r17.getConnectionsManager();
        r0 = r0.getCurrentTime();
        r3 = java.lang.Math.max(r0, r14);
        r0 = r17.getMessagesStorage();
        r1 = r9.id;
        r4 = 0;
        r5 = 0;
        r2 = r22;
        r0.createTaskForSecretChat(r1, r2, r3, r4, r5);
    L_0x00ee:
        if (r16 == 0) goto L_0x0105;
    L_0x00f0:
        r7 = org.telegram.messenger.Utilities.stageQueue;
        r8 = new org.telegram.messenger.-$$Lambda$MessagesController$PG02LYk94FZTZxqn2NcjESCdKK0;
        r0 = r8;
        r1 = r17;
        r2 = r18;
        r4 = r25;
        r5 = r22;
        r6 = r20;
        r0.<init>(r1, r2, r4, r5, r6);
        r7.postRunnable(r8);
    L_0x0105:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markDialogAsRead(long, int, int, int, boolean, int, boolean, int):void");
    }

    public /* synthetic */ void lambda$markDialogAsRead$167$MessagesController(long j, int i, int i2, boolean z, boolean z2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$EdMKJdZCqb5YyMuGpKZ9c_04Zts(this, j, i, i2, z, z2));
    }

    public /* synthetic */ void lambda$null$166$MessagesController(long j, int i, int i2, boolean z, boolean z2) {
        long j2 = j;
        int i3 = i2;
        Dialog dialog = (Dialog) this.dialogs_dict.get(j2);
        if (dialog != null) {
            int i4;
            int i5 = dialog.unread_count;
            if (i == 0 || i3 >= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                dialog.unread_count = Math.max(i5 - i, 0);
                if (i3 != Integer.MIN_VALUE) {
                    i4 = dialog.unread_count;
                    int i6 = dialog.top_message;
                    if (i4 > i6 - i3) {
                        dialog.unread_count = i6 - i3;
                    }
                }
            }
            i4 = dialog.folder_id;
            if (i4 != 0) {
                Dialog dialog2 = (Dialog) this.dialogs_dict.get(DialogObject.makeFolderDialogId(i4));
                if (dialog2 != null) {
                    if (z) {
                        if (isDialogMuted(dialog.id)) {
                            dialog2.unread_count -= i5 - dialog.unread_count;
                        } else {
                            dialog2.unread_mentions_count -= i5 - dialog.unread_count;
                        }
                    } else if (dialog.unread_count == 0) {
                        if (isDialogMuted(dialog.id)) {
                            dialog2.unread_count--;
                        } else {
                            dialog2.unread_mentions_count--;
                        }
                    }
                }
            }
            if ((i5 != 0 || dialog.unread_mark) && dialog.unread_count == 0 && !isDialogMuted(j)) {
                this.unreadUnmutedDialogs--;
            }
            if (dialog.unread_mark) {
                dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog.id, false);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        }
        LongSparseArray longSparseArray;
        if (z2) {
            getNotificationsController().processReadMessages(null, j, 0, i2, true);
            longSparseArray = new LongSparseArray(1);
            longSparseArray.put(j2, Integer.valueOf(-1));
            getNotificationsController().processDialogsUpdateRead(longSparseArray);
            return;
        }
        getNotificationsController().processReadMessages(null, j, 0, i2, false);
        longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j2, Integer.valueOf(0));
        getNotificationsController().processDialogsUpdateRead(longSparseArray);
    }

    public /* synthetic */ void lambda$markDialogAsRead$169$MessagesController(long j, int i, boolean z, int i2, int i3, boolean z2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$JRlJDjJHdwLyJ8ZhgSOgXvzFvZY(this, j, i, z, i2, i3, z2));
    }

    public /* synthetic */ void lambda$null$168$MessagesController(long j, int i, boolean z, int i2, int i3, boolean z2) {
        getNotificationsController().processReadMessages(null, j, i, 0, z);
        Dialog dialog = (Dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            int i4 = dialog.unread_count;
            if (i2 == 0 || i3 <= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                dialog.unread_count = Math.max(i4 - i2, 0);
                if (i3 != Integer.MAX_VALUE) {
                    i2 = dialog.unread_count;
                    int i5 = dialog.top_message;
                    if (i2 > i3 - i5) {
                        dialog.unread_count = i3 - i5;
                    }
                }
            }
            i2 = dialog.folder_id;
            if (i2 != 0) {
                Dialog dialog2 = (Dialog) this.dialogs_dict.get(DialogObject.makeFolderDialogId(i2));
                if (dialog2 != null) {
                    if (z2) {
                        if (isDialogMuted(dialog.id)) {
                            dialog2.unread_count -= i4 - dialog.unread_count;
                        } else {
                            dialog2.unread_mentions_count -= i4 - dialog.unread_count;
                        }
                    } else if (dialog.unread_count == 0) {
                        if (isDialogMuted(dialog.id)) {
                            dialog2.unread_count--;
                        } else {
                            dialog2.unread_mentions_count--;
                        }
                    }
                }
            }
            if ((i4 != 0 || dialog.unread_mark) && dialog.unread_count == 0 && !isDialogMuted(j)) {
                this.unreadUnmutedDialogs--;
            }
            if (dialog.unread_mark) {
                dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog.id, false);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        }
        LongSparseArray longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j, Integer.valueOf(0));
        getNotificationsController().processDialogsUpdateRead(longSparseArray);
    }

    public /* synthetic */ void lambda$markDialogAsRead$170$MessagesController(long j, boolean z, int i, int i2) {
        ReadTask readTask = (ReadTask) this.readTasksMap.get(j);
        if (readTask == null) {
            readTask = new ReadTask(this, null);
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
        int i2 = 0;
        if (i == 0) {
            TL_messages_createChat tL_messages_createChat = new TL_messages_createChat();
            tL_messages_createChat.title = str;
            while (i2 < arrayList.size()) {
                User user = getUser((Integer) arrayList.get(i2));
                if (user != null) {
                    tL_messages_createChat.users.add(getInputUser(user));
                }
                i2++;
            }
            return getConnectionsManager().sendRequest(tL_messages_createChat, new -$$Lambda$MessagesController$r3wD9q76lxTAxcXy9dXig5NB57g(this, baseFragment, tL_messages_createChat), 2);
        } else if (i != 2 && i != 4) {
            return 0;
        } else {
            TL_channels_createChannel tL_channels_createChannel = new TL_channels_createChannel();
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
                tL_channels_createChannel.geo_point = new TL_inputGeoPoint();
                tL_channels_createChannel.geo_point.lat = location.getLatitude();
                tL_channels_createChannel.geo_point._long = location.getLongitude();
                tL_channels_createChannel.address = str3;
                tL_channels_createChannel.flags |= 4;
            }
            return getConnectionsManager().sendRequest(tL_channels_createChannel, new -$$Lambda$MessagesController$MZI51303j9kgOvMiGNoRK-3TYT8(this, baseFragment, tL_channels_createChannel), 2);
        }
    }

    public /* synthetic */ void lambda$createChat$173$MessagesController(BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$uV_uSmHhcqeLOfX2TVxl6Ikziuk(this, tL_error, baseFragment, tL_messages_createChat));
            return;
        }
        Updates updates = (Updates) tLObject;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$uGadu6X9bU7DjRjsZZR5X-1qsw4(this, updates));
    }

    public /* synthetic */ void lambda$null$171$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_createChat, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$null$172$MessagesController(Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        ArrayList arrayList = updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
    }

    public /* synthetic */ void lambda$createChat$176$MessagesController(BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$O4I7bv1C0H8usVqd1V74wInWygM(this, tL_error, baseFragment, tL_channels_createChannel));
            return;
        }
        Updates updates = (Updates) tLObject;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$wlPPOB5XUIJO9ihJQlC-B1OONNM(this, updates));
    }

    public /* synthetic */ void lambda$null$174$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_createChannel, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$null$175$MessagesController(Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        ArrayList arrayList = updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
    }

    public void convertToMegaGroup(Context context, int i, BaseFragment baseFragment, IntCallback intCallback) {
        TL_messages_migrateChat tL_messages_migrateChat = new TL_messages_migrateChat();
        tL_messages_migrateChat.chat_id = i;
        AlertDialog alertDialog = new AlertDialog(context, 3);
        alertDialog.setOnCancelListener(new -$$Lambda$MessagesController$oh6CukXQ8QlNochvMhltLF9gL7A(this, getConnectionsManager().sendRequest(tL_messages_migrateChat, new -$$Lambda$MessagesController$czc7XB56RIdWENUz2klB0eiLdOk(this, context, alertDialog, intCallback, baseFragment, tL_messages_migrateChat))));
        try {
            alertDialog.show();
        } catch (Exception unused) {
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$180$MessagesController(Context context, AlertDialog alertDialog, IntCallback intCallback, BaseFragment baseFragment, TL_messages_migrateChat tL_messages_migrateChat, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$n5_27WXa8J2JwW2tbYMRGnfcvck(context, alertDialog));
            Updates updates = (Updates) tLObject;
            processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$gyfvQkMWv4yBSfc0n_us4Q2AvCo(intCallback, updates));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$TbqqFW17Bay6UwXMq2HUYJTgBaE(this, intCallback, context, alertDialog, tL_error, baseFragment, tL_messages_migrateChat));
    }

    static /* synthetic */ void lambda$null$177(Context context, AlertDialog alertDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    static /* synthetic */ void lambda$null$178(IntCallback intCallback, Updates updates) {
        if (intCallback != null) {
            for (int i = 0; i < updates.chats.size(); i++) {
                Chat chat = (Chat) updates.chats.get(i);
                if (ChatObject.isChannel(chat)) {
                    intCallback.run(chat.id);
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$179$MessagesController(IntCallback intCallback, Context context, AlertDialog alertDialog, TL_error tL_error, BaseFragment baseFragment, TL_messages_migrateChat tL_messages_migrateChat) {
        if (intCallback != null) {
            intCallback.run(0);
        }
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_migrateChat, Boolean.valueOf(false));
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$181$MessagesController(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public void addUsersToChannel(int i, ArrayList<InputUser> arrayList, BaseFragment baseFragment) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TL_channels_inviteToChannel tL_channels_inviteToChannel = new TL_channels_inviteToChannel();
            tL_channels_inviteToChannel.channel = getInputChannel(i);
            tL_channels_inviteToChannel.users = arrayList;
            getConnectionsManager().sendRequest(tL_channels_inviteToChannel, new -$$Lambda$MessagesController$ke5Z5q29twLCLASSNAMEQsUOsql8ulQ9k(this, baseFragment, tL_channels_inviteToChannel));
        }
    }

    public /* synthetic */ void lambda$addUsersToChannel$183$MessagesController(BaseFragment baseFragment, TL_channels_inviteToChannel tL_channels_inviteToChannel, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$QZql8klvtXKdFpnIpfYEcWGlDzc(this, tL_error, baseFragment, tL_channels_inviteToChannel));
        } else {
            processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$182$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_inviteToChannel, Boolean.valueOf(true));
    }

    public void toogleChannelSignatures(int i, boolean z) {
        TL_channels_toggleSignatures tL_channels_toggleSignatures = new TL_channels_toggleSignatures();
        tL_channels_toggleSignatures.channel = getInputChannel(i);
        tL_channels_toggleSignatures.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_toggleSignatures, new -$$Lambda$MessagesController$RFCOvaflyGcJJWP-Rg3POoMtLh0(this), 64);
    }

    public /* synthetic */ void lambda$toogleChannelSignatures$185$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$SHco2piQnrrWB-Q26mvar_Mp3SCM(this));
        }
    }

    public /* synthetic */ void lambda$null$184$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void toogleChannelInvitesHistory(int i, boolean z) {
        TL_channels_togglePreHistoryHidden tL_channels_togglePreHistoryHidden = new TL_channels_togglePreHistoryHidden();
        tL_channels_togglePreHistoryHidden.channel = getInputChannel(i);
        tL_channels_togglePreHistoryHidden.enabled = z;
        getConnectionsManager().sendRequest(tL_channels_togglePreHistoryHidden, new -$$Lambda$MessagesController$vBdgJGjpntw4_Oz4XD5m_FNLCI0(this), 64);
    }

    public /* synthetic */ void lambda$toogleChannelInvitesHistory$187$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$Bo76FNkO_sGRMcv0x-fDF4EUsic(this));
        }
    }

    public /* synthetic */ void lambda$null$186$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void updateChatAbout(int i, String str, ChatFull chatFull) {
        if (chatFull != null) {
            TL_messages_editChatAbout tL_messages_editChatAbout = new TL_messages_editChatAbout();
            tL_messages_editChatAbout.peer = getInputPeer(-i);
            tL_messages_editChatAbout.about = str;
            getConnectionsManager().sendRequest(tL_messages_editChatAbout, new -$$Lambda$MessagesController$UaS8d69PVm6G7sNt_6Cl76DNyew(this, chatFull, str), 64);
        }
    }

    public /* synthetic */ void lambda$updateChatAbout$189$MessagesController(ChatFull chatFull, String str, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$xzpTlIAYH3JV8VeovjKP4cH_Lz0(this, chatFull, str));
        }
    }

    public /* synthetic */ void lambda$null$188$MessagesController(ChatFull chatFull, String str) {
        chatFull.about = str;
        getMessagesStorage().updateChatInfo(chatFull, false);
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void updateChannelUserName(int i, String str) {
        TL_channels_updateUsername tL_channels_updateUsername = new TL_channels_updateUsername();
        tL_channels_updateUsername.channel = getInputChannel(i);
        tL_channels_updateUsername.username = str;
        getConnectionsManager().sendRequest(tL_channels_updateUsername, new -$$Lambda$MessagesController$ZtYSn6QPfcnY5VXufJnW-lcLfiY(this, i, str), 64);
    }

    public /* synthetic */ void lambda$updateChannelUserName$191$MessagesController(int i, String str, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$8Illx-FQm-vdGMgA7pMKLgJHkyg(this, i, str));
        }
    }

    public /* synthetic */ void lambda$null$190$MessagesController(int i, String str) {
        Chat chat = getChat(Integer.valueOf(i));
        if (str.length() != 0) {
            chat.flags |= 64;
        } else {
            chat.flags &= -65;
        }
        chat.username = str;
        ArrayList arrayList = new ArrayList();
        arrayList.add(chat);
        getMessagesStorage().putUsersAndChats(null, arrayList, true, true);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void sendBotStart(User user, String str) {
        if (user != null) {
            TL_messages_startBot tL_messages_startBot = new TL_messages_startBot();
            tL_messages_startBot.bot = getInputUser(user);
            tL_messages_startBot.peer = getInputPeer(user.id);
            tL_messages_startBot.start_param = str;
            tL_messages_startBot.random_id = Utilities.random.nextLong();
            getConnectionsManager().sendRequest(tL_messages_startBot, new -$$Lambda$MessagesController$lyQxbb4WWrIYmuFcx85XLe0uaz4(this));
        }
    }

    public /* synthetic */ void lambda$sendBotStart$192$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public boolean isJoiningChannel(int i) {
        return this.joiningToChannels.contains(Integer.valueOf(i));
    }

    public void addUserToChat(int i, User user, ChatFull chatFull, int i2, String str, BaseFragment baseFragment, Runnable runnable) {
        int i3 = i;
        User user2 = user;
        ChatFull chatFull2 = chatFull;
        String str2 = str;
        if (user2 != null) {
            if (i3 > 0) {
                TLObject tL_messages_startBot;
                boolean isChannel = ChatObject.isChannel(i, this.currentAccount);
                boolean z = isChannel && getChat(Integer.valueOf(i)).megagroup;
                InputUser inputUser = getInputUser(user);
                if (str2 != null && (!isChannel || z)) {
                    tL_messages_startBot = new TL_messages_startBot();
                    tL_messages_startBot.bot = inputUser;
                    if (isChannel) {
                        tL_messages_startBot.peer = getInputPeer(-i3);
                    } else {
                        tL_messages_startBot.peer = new TL_inputPeerChat();
                        tL_messages_startBot.peer.chat_id = i3;
                    }
                    tL_messages_startBot.start_param = str2;
                    tL_messages_startBot.random_id = Utilities.random.nextLong();
                } else if (!isChannel) {
                    tL_messages_startBot = new TL_messages_addChatUser();
                    tL_messages_startBot.chat_id = i3;
                    tL_messages_startBot.fwd_limit = i2;
                    tL_messages_startBot.user_id = inputUser;
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    tL_messages_startBot = new TL_channels_inviteToChannel();
                    tL_messages_startBot.channel = getInputChannel(i);
                    tL_messages_startBot.users.add(inputUser);
                } else if (!this.joiningToChannels.contains(Integer.valueOf(i))) {
                    tL_messages_startBot = new TL_channels_joinChannel();
                    tL_messages_startBot.channel = getInputChannel(i);
                    this.joiningToChannels.add(Integer.valueOf(i));
                } else {
                    return;
                }
                TLObject tLObject = tL_messages_startBot;
                getConnectionsManager().sendRequest(tLObject, new -$$Lambda$MessagesController$VLQQD_L7rWydtqdkTepxBzSikLA(this, isChannel, inputUser, i, baseFragment, tLObject, z, runnable));
            } else if (chatFull2 instanceof TL_chatFull) {
                int i4 = 0;
                while (i4 < chatFull2.participants.participants.size()) {
                    if (((ChatParticipant) chatFull2.participants.participants.get(i4)).user_id != user2.id) {
                        i4++;
                    } else {
                        return;
                    }
                }
                Chat chat = getChat(Integer.valueOf(i));
                chat.participants_count++;
                ArrayList arrayList = new ArrayList();
                arrayList.add(chat);
                getMessagesStorage().putUsersAndChats(null, arrayList, true, true);
                TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                tL_chatParticipant.user_id = user2.id;
                tL_chatParticipant.inviter_id = getUserConfig().getClientUserId();
                tL_chatParticipant.date = getConnectionsManager().getCurrentTime();
                chatFull2.participants.participants.add(0, tL_chatParticipant);
                getMessagesStorage().updateChatInfo(chatFull2, true);
                getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull2, Integer.valueOf(0), Boolean.valueOf(false), null);
                getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public /* synthetic */ void lambda$addUserToChat$196$MessagesController(boolean z, InputUser inputUser, int i, BaseFragment baseFragment, TLObject tLObject, boolean z2, Runnable runnable, TLObject tLObject2, TL_error tL_error) {
        InputUser inputUser2 = inputUser;
        int i2 = i;
        if (z && (inputUser2 instanceof TL_inputUserSelf)) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ZhNCt-uNnHNeQsj7xtYhyJU8X04(this, i));
        }
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$OuJgh3-kXwqXZTZASvVy4G9EEb0(this, tL_error, baseFragment, tLObject, z, z2, inputUser));
            return;
        }
        Object obj;
        Updates updates = (Updates) tLObject2;
        for (int i3 = 0; i3 < updates.updates.size(); i3++) {
            Update update = (Update) updates.updates.get(i3);
            if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                obj = 1;
                break;
            }
        }
        obj = null;
        processUpdates(updates, false);
        if (z) {
            if (obj == null && (inputUser2 instanceof TL_inputUserSelf)) {
                generateJoinMessage(i, true);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$P9VGjaQBqi4tfYW7UT22Hf8vDSU(this, i), 1000);
        }
        if (z && (inputUser2 instanceof TL_inputUserSelf)) {
            getMessagesStorage().updateDialogsWithDeletedMessages(new ArrayList(), null, true, i);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$null$193$MessagesController(int i) {
        this.joiningToChannels.remove(Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$null$194$MessagesController(TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2, InputUser inputUser) {
        int i = this.currentAccount;
        Object[] objArr = new Object[1];
        z2 = z && !z2;
        objArr[0] = Boolean.valueOf(z2);
        AlertsCreator.processError(i, tL_error, baseFragment, tLObject, objArr);
        if (z && (inputUser instanceof TL_inputUserSelf)) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
        }
    }

    public /* synthetic */ void lambda$null$195$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public void deleteUserFromChat(int i, User user, ChatFull chatFull) {
        deleteUserFromChat(i, user, chatFull, false, false);
    }

    public void deleteUserFromChat(int i, User user, ChatFull chatFull, boolean z, boolean z2) {
        if (user != null) {
            if (i > 0) {
                TLObject tL_messages_deleteChatUser;
                InputUser inputUser = getInputUser(user);
                Chat chat = getChat(Integer.valueOf(i));
                boolean isChannel = ChatObject.isChannel(chat);
                if (!isChannel) {
                    tL_messages_deleteChatUser = new TL_messages_deleteChatUser();
                    tL_messages_deleteChatUser.chat_id = i;
                    tL_messages_deleteChatUser.user_id = getInputUser(user);
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    tL_messages_deleteChatUser = new TL_channels_editBanned();
                    tL_messages_deleteChatUser.channel = getInputChannel(chat);
                    tL_messages_deleteChatUser.user_id = inputUser;
                    tL_messages_deleteChatUser.banned_rights = new TL_chatBannedRights();
                    TL_chatBannedRights tL_chatBannedRights = tL_messages_deleteChatUser.banned_rights;
                    tL_chatBannedRights.view_messages = true;
                    tL_chatBannedRights.send_media = true;
                    tL_chatBannedRights.send_messages = true;
                    tL_chatBannedRights.send_stickers = true;
                    tL_chatBannedRights.send_gifs = true;
                    tL_chatBannedRights.send_games = true;
                    tL_chatBannedRights.send_inline = true;
                    tL_chatBannedRights.embed_links = true;
                    tL_chatBannedRights.pin_messages = true;
                    tL_chatBannedRights.send_polls = true;
                    tL_chatBannedRights.invite_users = true;
                    tL_chatBannedRights.change_info = true;
                } else if (chat.creator && z) {
                    tL_messages_deleteChatUser = new TL_channels_deleteChannel();
                    tL_messages_deleteChatUser.channel = getInputChannel(chat);
                } else {
                    tL_messages_deleteChatUser = new TL_channels_leaveChannel();
                    tL_messages_deleteChatUser.channel = getInputChannel(chat);
                }
                if (user.id == getUserConfig().getClientUserId()) {
                    deleteDialog((long) (-i), 0, z2);
                }
                getConnectionsManager().sendRequest(tL_messages_deleteChatUser, new -$$Lambda$MessagesController$P-liPLC_3IVRpqiHlrqdxeDAwTU(this, isChannel, inputUser, i), 64);
            } else if (chatFull instanceof TL_chatFull) {
                Object obj;
                Chat chat2 = getChat(Integer.valueOf(i));
                chat2.participants_count--;
                ArrayList arrayList = new ArrayList();
                arrayList.add(chat2);
                getMessagesStorage().putUsersAndChats(null, arrayList, true, true);
                for (i = 0; i < chatFull.participants.participants.size(); i++) {
                    if (((ChatParticipant) chatFull.participants.participants.get(i)).user_id == user.id) {
                        chatFull.participants.participants.remove(i);
                        obj = 1;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    getMessagesStorage().updateChatInfo(chatFull, true);
                    getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                }
                getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public /* synthetic */ void lambda$deleteUserFromChat$198$MessagesController(boolean z, InputUser inputUser, int i, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            if (z && !(inputUser instanceof TL_inputUserSelf)) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$WRXGiw2nH0NbeRvUB9l1WOb1I2o(this, i), 1000);
            }
        }
    }

    public /* synthetic */ void lambda$null$197$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public void changeChatTitle(int i, String str) {
        if (i > 0) {
            TLObject tL_channels_editTitle;
            if (ChatObject.isChannel(i, this.currentAccount)) {
                tL_channels_editTitle = new TL_channels_editTitle();
                tL_channels_editTitle.channel = getInputChannel(i);
                tL_channels_editTitle.title = str;
            } else {
                tL_channels_editTitle = new TL_messages_editChatTitle();
                tL_channels_editTitle.chat_id = i;
                tL_channels_editTitle.title = str;
            }
            getConnectionsManager().sendRequest(tL_channels_editTitle, new -$$Lambda$MessagesController$eeEoncUtImD3KY99RbahhTT8gY8(this), 64);
            return;
        }
        Chat chat = getChat(Integer.valueOf(i));
        chat.title = str;
        ArrayList arrayList = new ArrayList();
        arrayList.add(chat);
        getMessagesStorage().putUsersAndChats(null, arrayList, true, true);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(16));
    }

    public /* synthetic */ void lambda$changeChatTitle$199$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public void changeChatAvatar(int i, InputFile inputFile, FileLocation fileLocation, FileLocation fileLocation2) {
        TLObject tL_channels_editPhoto;
        if (ChatObject.isChannel(i, this.currentAccount)) {
            tL_channels_editPhoto = new TL_channels_editPhoto();
            tL_channels_editPhoto.channel = getInputChannel(i);
            if (inputFile != null) {
                tL_channels_editPhoto.photo = new TL_inputChatUploadedPhoto();
                tL_channels_editPhoto.photo.file = inputFile;
            } else {
                tL_channels_editPhoto.photo = new TL_inputChatPhotoEmpty();
            }
        } else {
            tL_channels_editPhoto = new TL_messages_editChatPhoto();
            tL_channels_editPhoto.chat_id = i;
            if (inputFile != null) {
                tL_channels_editPhoto.photo = new TL_inputChatUploadedPhoto();
                tL_channels_editPhoto.photo.file = inputFile;
            } else {
                tL_channels_editPhoto.photo = new TL_inputChatPhotoEmpty();
            }
        }
        getConnectionsManager().sendRequest(tL_channels_editPhoto, new -$$Lambda$MessagesController$wy09bZPmqKXvxXv6-hJzlanOWGw(this, fileLocation, fileLocation2), 64);
    }

    public /* synthetic */ void lambda$changeChatAvatar$200$MessagesController(FileLocation fileLocation, FileLocation fileLocation2, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            Photo photo;
            Updates updates = (Updates) tLObject;
            int size = updates.updates.size();
            for (int i = 0; i < size; i++) {
                Update update = (Update) updates.updates.get(i);
                MessageAction messageAction;
                if (update instanceof TL_updateNewChannelMessage) {
                    messageAction = ((TL_updateNewChannelMessage) update).message.action;
                    if (messageAction instanceof TL_messageActionChatEditPhoto) {
                        photo = messageAction.photo;
                        if (photo instanceof TL_photo) {
                            break;
                        }
                    } else {
                        continue;
                    }
                } else if (update instanceof TL_updateNewMessage) {
                    messageAction = ((TL_updateNewMessage) update).message.action;
                    if (messageAction instanceof TL_messageActionChatEditPhoto) {
                        photo = messageAction.photo;
                        if (photo instanceof TL_photo) {
                            break;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            photo = null;
            if (photo != null) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 150);
                if (!(closestPhotoSizeWithSize == null || fileLocation == null)) {
                    FileLoader.getPathToAttach(fileLocation, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize, true));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(fileLocation.volume_id);
                    String str = "_";
                    stringBuilder.append(str);
                    stringBuilder.append(fileLocation.local_id);
                    String str2 = "@50_50";
                    stringBuilder.append(str2);
                    String stringBuilder2 = stringBuilder.toString();
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(closestPhotoSizeWithSize.location.volume_id);
                    stringBuilder3.append(str);
                    stringBuilder3.append(closestPhotoSizeWithSize.location.local_id);
                    stringBuilder3.append(str2);
                    ImageLoader.getInstance().replaceImageInCache(stringBuilder2, stringBuilder3.toString(), ImageLocation.getForPhoto(closestPhotoSizeWithSize, photo), true);
                }
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                if (!(closestPhotoSizeWithSize2 == null || fileLocation2 == null)) {
                    FileLoader.getPathToAttach(fileLocation2, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize2, true));
                }
            }
            processUpdates(updates, false);
        }
    }

    public void unregistedPush() {
        if (getUserConfig().registeredForPush && SharedConfig.pushString.length() == 0) {
            TL_account_unregisterDevice tL_account_unregisterDevice = new TL_account_unregisterDevice();
            tL_account_unregisterDevice.token = SharedConfig.pushString;
            tL_account_unregisterDevice.token_type = 2;
            for (int i = 0; i < 3; i++) {
                UserConfig instance = UserConfig.getInstance(i);
                if (i != this.currentAccount && instance.isClientActivated()) {
                    tL_account_unregisterDevice.other_uids.add(Integer.valueOf(instance.getClientUserId()));
                }
            }
            getConnectionsManager().sendRequest(tL_account_unregisterDevice, -$$Lambda$MessagesController$Qu0A-rHMVMrsMl5O52igKgpwq98.INSTANCE);
        }
    }

    public void performLogout(int i) {
        boolean z = true;
        if (i == 1) {
            unregistedPush();
            getConnectionsManager().sendRequest(new TL_auth_logOut(), new -$$Lambda$MessagesController$M0pZMVLHuA4M7EZu9xjksMf4U_0(this));
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

    public /* synthetic */ void lambda$performLogout$202$MessagesController(TLObject tLObject, TL_error tL_error) {
        getConnectionsManager().cleanup(false);
    }

    public void generateUpdateMessage() {
        if (!BuildVars.DEBUG_VERSION) {
            String str = SharedConfig.lastUpdateVersion;
            if (str != null && !str.equals(BuildVars.BUILD_VERSION_STRING)) {
                TL_help_getAppChangelog tL_help_getAppChangelog = new TL_help_getAppChangelog();
                tL_help_getAppChangelog.prev_app_version = SharedConfig.lastUpdateVersion;
                getConnectionsManager().sendRequest(tL_help_getAppChangelog, new -$$Lambda$MessagesController$QeK7l_WXxzXocN29ig8T7oPOy24(this));
            }
        }
    }

    public /* synthetic */ void lambda$generateUpdateMessage$203$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
            SharedConfig.saveConfig();
        }
        if (tLObject instanceof Updates) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public void registerForPush(String str) {
        if (!TextUtils.isEmpty(str) && !this.registeringForPush && getUserConfig().getClientUserId() != 0 && (!getUserConfig().registeredForPush || !str.equals(SharedConfig.pushString))) {
            this.registeringForPush = true;
            this.lastPushRegisterSendTime = SystemClock.elapsedRealtime();
            if (SharedConfig.pushAuthKey == null) {
                SharedConfig.pushAuthKey = new byte[256];
                Utilities.random.nextBytes(SharedConfig.pushAuthKey);
                SharedConfig.saveConfig();
            }
            TL_account_registerDevice tL_account_registerDevice = new TL_account_registerDevice();
            tL_account_registerDevice.token_type = 2;
            tL_account_registerDevice.token = str;
            int i = 0;
            tL_account_registerDevice.no_muted = false;
            tL_account_registerDevice.secret = SharedConfig.pushAuthKey;
            while (i < 3) {
                UserConfig instance = UserConfig.getInstance(i);
                if (i != this.currentAccount && instance.isClientActivated()) {
                    int clientUserId = instance.getClientUserId();
                    tL_account_registerDevice.other_uids.add(Integer.valueOf(clientUserId));
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("add other uid = ");
                        stringBuilder.append(clientUserId);
                        stringBuilder.append(" for account ");
                        stringBuilder.append(this.currentAccount);
                        FileLog.d(stringBuilder.toString());
                    }
                }
                i++;
            }
            getConnectionsManager().sendRequest(tL_account_registerDevice, new -$$Lambda$MessagesController$J3fJIuCnmq3QhQ3RoOcIcK4I8PU(this, str));
        }
    }

    public /* synthetic */ void lambda$registerForPush$205$MessagesController(String str, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("account ");
                stringBuilder.append(this.currentAccount);
                stringBuilder.append(" registered for push");
                FileLog.d(stringBuilder.toString());
            }
            getUserConfig().registeredForPush = true;
            SharedConfig.pushString = str;
            getUserConfig().saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$zpDKEoH-qpL3YM5Wh2uHoUsFVMA(this));
    }

    public /* synthetic */ void lambda$null$204$MessagesController() {
        this.registeringForPush = false;
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            getConnectionsManager().sendRequest(new TL_updates_getState(), new -$$Lambda$MessagesController$lHXWXIfmxKsjzfeH5HmTsCyV03c(this));
        }
    }

    public /* synthetic */ void lambda$loadCurrentState$206$MessagesController(TLObject tLObject, TL_error tL_error) {
        int i = 0;
        this.updatingState = false;
        if (tL_error == null) {
            TL_updates_state tL_updates_state = (TL_updates_state) tLObject;
            getMessagesStorage().setLastDateValue(tL_updates_state.date);
            getMessagesStorage().setLastPtsValue(tL_updates_state.pts);
            getMessagesStorage().setLastSeqValue(tL_updates_state.seq);
            getMessagesStorage().setLastQtsValue(tL_updates_state.qts);
            while (i < 3) {
                processUpdatesQueue(i, 2);
                i++;
            }
            getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        } else if (tL_error.code != 401) {
            loadCurrentState();
        }
    }

    private int getUpdateSeq(Updates updates) {
        if (updates instanceof TL_updatesCombined) {
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
        return i == 2 ? this.updatesStartWaitTimeQts : 0;
    }

    private int isValidUpdate(Updates updates, int i) {
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
        ArrayList arrayList = (ArrayList) this.updatesQueueChannels.get(i);
        if (arrayList != null) {
            int i3 = this.channelsPts.get(i);
            if (arrayList.isEmpty() || i3 == 0) {
                this.updatesQueueChannels.remove(i);
                return;
            }
            StringBuilder stringBuilder;
            Collections.sort(arrayList, -$$Lambda$MessagesController$iWcS0cq_95jswayvvTaTSKnQLGc.INSTANCE);
            if (i2 == 2) {
                this.channelsPts.put(i, ((Updates) arrayList.get(0)).pts);
            }
            Object obj = null;
            while (arrayList.size() > 0) {
                Updates updates = (Updates) arrayList.get(0);
                int i4 = updates.pts;
                Object obj2 = i4 <= i3 ? 2 : updates.pts_count + i3 == i4 ? null : 1;
                if (obj2 == null) {
                    processUpdates(updates, true);
                    arrayList.remove(0);
                    obj = 1;
                } else if (obj2 == 1) {
                    long j = this.updatesStartWaitTimeChannels.get(i);
                    String str = "HOLE IN CHANNEL ";
                    if (j == 0 || (obj == null && Math.abs(System.currentTimeMillis() - j) > 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str);
                            stringBuilder.append(i);
                            stringBuilder.append(" UPDATES QUEUE - getChannelDifference ");
                            FileLog.d(stringBuilder.toString());
                        }
                        this.updatesStartWaitTimeChannels.delete(i);
                        this.updatesQueueChannels.remove(i);
                        getChannelDifference(i);
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str);
                        stringBuilder2.append(i);
                        stringBuilder2.append(" UPDATES QUEUE - will wait more time");
                        FileLog.d(stringBuilder2.toString());
                    }
                    if (obj != null) {
                        this.updatesStartWaitTimeChannels.put(i, System.currentTimeMillis());
                    }
                    return;
                } else {
                    arrayList.remove(0);
                }
            }
            this.updatesQueueChannels.remove(i);
            this.updatesStartWaitTimeChannels.delete(i);
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("UPDATES CHANNEL ");
                stringBuilder.append(i);
                stringBuilder.append(" QUEUE PROCEED - OK");
                FileLog.d(stringBuilder.toString());
            }
        }
    }

    private void processUpdatesQueue(int i, int i2) {
        ArrayList arrayList;
        if (i == 0) {
            arrayList = this.updatesQueueSeq;
            Collections.sort(arrayList, new -$$Lambda$MessagesController$8j3wq1ibflq2Rv7QdRqXJvvwxBc(this));
        } else if (i == 1) {
            arrayList = this.updatesQueuePts;
            Collections.sort(arrayList, -$$Lambda$MessagesController$hGUHFGlJkXICR41QitU5GeMfKbg.INSTANCE);
        } else if (i == 2) {
            arrayList = this.updatesQueueQts;
            Collections.sort(arrayList, -$$Lambda$MessagesController$r6viuXq_dpBF5FJFu8L7BQA0K6U.INSTANCE);
        } else {
            arrayList = null;
        }
        if (!(arrayList == null || arrayList.isEmpty())) {
            if (i2 == 2) {
                Updates updates = (Updates) arrayList.get(0);
                if (i == 0) {
                    getMessagesStorage().setLastSeqValue(getUpdateSeq(updates));
                } else if (i == 1) {
                    getMessagesStorage().setLastPtsValue(updates.pts);
                } else {
                    getMessagesStorage().setLastQtsValue(updates.pts);
                }
            }
            Object obj = null;
            while (arrayList.size() > 0) {
                Updates updates2 = (Updates) arrayList.get(0);
                int isValidUpdate = isValidUpdate(updates2, i);
                if (isValidUpdate == 0) {
                    processUpdates(updates2, true);
                    arrayList.remove(0);
                    obj = 1;
                } else if (isValidUpdate != 1) {
                    arrayList.remove(0);
                } else if (getUpdatesStartTime(i) == 0 || (obj == null && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(i)) > 1500)) {
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
                    if (obj != null) {
                        setUpdatesStartTime(i, System.currentTimeMillis());
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

    public /* synthetic */ int lambda$processUpdatesQueue$208$MessagesController(Updates updates, Updates updates2) {
        return AndroidUtilities.compare(getUpdateSeq(updates), getUpdateSeq(updates2));
    }

    /* Access modifiers changed, original: protected */
    public void loadUnknownChannel(Chat chat, long j) {
        Throwable e;
        if ((chat instanceof TL_channel) && this.gettingUnknownChannels.indexOfKey(chat.id) < 0) {
            if (chat.access_hash == 0) {
                if (j != 0) {
                    getMessagesStorage().removePendingTask(j);
                }
                return;
            }
            TL_inputPeerChannel tL_inputPeerChannel = new TL_inputPeerChannel();
            int i = chat.id;
            tL_inputPeerChannel.channel_id = i;
            tL_inputPeerChannel.access_hash = chat.access_hash;
            this.gettingUnknownChannels.put(i, true);
            TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
            TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
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
                    } catch (Exception e2) {
                        NativeByteBuffer nativeByteBuffer3 = nativeByteBuffer2;
                        e = e2;
                        nativeByteBuffer = nativeByteBuffer3;
                        FileLog.e(e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$ALfjuWLEEaIIRIil9HudCsfImpU(this, j, chat));
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$ALfjuWLEEaIIRIil9HudCsfImpU(this, j, chat));
                }
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$ALfjuWLEEaIIRIil9HudCsfImpU(this, j, chat));
        }
    }

    public /* synthetic */ void lambda$loadUnknownChannel$211$MessagesController(long j, Chat chat, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
            if (!(tL_messages_peerDialogs.dialogs.isEmpty() || tL_messages_peerDialogs.chats.isEmpty())) {
                TL_dialog tL_dialog = (TL_dialog) tL_messages_peerDialogs.dialogs.get(0);
                TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
                tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                processLoadedDialogs(tL_messages_dialogs, null, tL_dialog.folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_CHANNEL, false, false, false);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        this.gettingUnknownChannels.delete(chat.id);
    }

    public void startShortPoll(Chat chat, boolean z) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$HhZY7Xvm7NS06gDNX9yqgS8WW3E(this, z, chat));
    }

    public /* synthetic */ void lambda$startShortPoll$212$MessagesController(boolean z, Chat chat) {
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
            getChannelDifference(chat.id, 3, 0, null);
        }
        if (chat.megagroup) {
            this.needShortPollOnlines.put(chat.id, 0);
            if (this.shortPollOnlines.indexOfKey(chat.id) < 0) {
                this.shortPollOnlines.put(chat.id, 0);
            }
        }
    }

    private void getChannelDifference(int i) {
        getChannelDifference(i, 0, 0, null);
    }

    /* JADX WARNING: Missing block: B:60:0x0097, code skipped:
            if (r1 != 4298000) goto L_0x009b;
     */
    public static boolean isSupportUser(org.telegram.tgnet.TLRPC.User r1) {
        /*
        if (r1 == 0) goto L_0x009b;
    L_0x0002:
        r0 = r1.support;
        if (r0 != 0) goto L_0x0099;
    L_0x0006:
        r1 = r1.id;
        r0 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x000d:
        r0 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0012:
        r0 = 4240000; // 0x40b280 float:5.941505E-39 double:2.0948383E-317;
        if (r1 == r0) goto L_0x0099;
    L_0x0017:
        r0 = 4244000; // 0x40CLASSNAME float:5.94711E-39 double:2.0968146E-317;
        if (r1 == r0) goto L_0x0099;
    L_0x001c:
        r0 = 4245000; // 0x40CLASSNAME float:5.948512E-39 double:2.0973087E-317;
        if (r1 == r0) goto L_0x0099;
    L_0x0021:
        r0 = 4246000; // 0x40c9f0 float:5.949913E-39 double:2.0978027E-317;
        if (r1 == r0) goto L_0x0099;
    L_0x0026:
        r0 = 410000; // 0x64190 float:5.74532E-40 double:2.02567E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x002b:
        r0 = 420000; // 0x668a0 float:5.88545E-40 double:2.075076E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0030:
        r0 = 431000; // 0x69398 float:6.0396E-40 double:2.129423E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0035:
        r0 = NUM; // 0x19b6ded8 float:1.8908365E-23 double:2.131473306E-315;
        if (r1 == r0) goto L_0x0099;
    L_0x003a:
        r0 = 434000; // 0x69var_ float:6.08164E-40 double:2.144245E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x003f:
        r0 = 4243000; // 0x40be38 float:5.94571E-39 double:2.0963205E-317;
        if (r1 == r0) goto L_0x0099;
    L_0x0044:
        r0 = 439000; // 0x6b2d8 float:6.1517E-40 double:2.16895E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0049:
        r0 = 449000; // 0x6d9e8 float:6.29183E-40 double:2.218355E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x004e:
        r0 = 450000; // 0x6ddd0 float:6.30584E-40 double:2.223295E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0053:
        r0 = 452000; // 0x6e5a0 float:6.33387E-40 double:2.233177E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0058:
        r0 = 454000; // 0x6ed70 float:6.3619E-40 double:2.24306E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x005d:
        r0 = 4254000; // 0x40e930 float:5.961124E-39 double:2.1017553E-317;
        if (r1 == r0) goto L_0x0099;
    L_0x0062:
        r0 = 455000; // 0x6var_ float:6.37591E-40 double:2.248E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0067:
        r0 = 460000; // 0x704e0 float:6.44597E-40 double:2.2727E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x006c:
        r0 = 470000; // 0x72bf0 float:6.5861E-40 double:2.32211E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0071:
        r0 = 479000; // 0x74var_ float:6.71222E-40 double:2.366574E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0076:
        r0 = 796000; // 0xCLASSNAME float:1.115434E-39 double:3.932763E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x007b:
        r0 = 482000; // 0x75ad0 float:6.75426E-40 double:2.381396E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0080:
        r0 = 490000; // 0x77a10 float:6.86636E-40 double:2.42092E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0085:
        r0 = 496000; // 0x79180 float:6.95044E-40 double:2.450566E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x008a:
        r0 = 497000; // 0x79568 float:6.96445E-40 double:2.455506E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x008f:
        r0 = 498000; // 0x79950 float:6.97847E-40 double:2.460447E-318;
        if (r1 == r0) goto L_0x0099;
    L_0x0094:
        r0 = 4298000; // 0x419510 float:6.022781E-39 double:2.123494E-317;
        if (r1 != r0) goto L_0x009b;
    L_0x0099:
        r1 = 1;
        goto L_0x009c;
    L_0x009b:
        r1 = 0;
    L_0x009c:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.isSupportUser(org.telegram.tgnet.TLRPC$User):boolean");
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00bf  */
    public void getChannelDifference(int r16, int r17, long r18, org.telegram.tgnet.TLRPC.InputChannel r20) {
        /*
        r15 = this;
        r7 = r15;
        r3 = r16;
        r4 = r17;
        r0 = r18;
        r2 = r7.gettingDifferenceChannels;
        r2 = r2.get(r3);
        if (r2 == 0) goto L_0x0010;
    L_0x000f:
        return;
    L_0x0010:
        r2 = 100;
        r5 = 3;
        r6 = 1;
        if (r4 != r6) goto L_0x0022;
    L_0x0016:
        r2 = r7.channelsPts;
        r2 = r2.get(r3);
        if (r2 == 0) goto L_0x001f;
    L_0x001e:
        return;
    L_0x001f:
        r2 = 1;
        r8 = 1;
        goto L_0x0044;
    L_0x0022:
        r8 = r7.channelsPts;
        r8 = r8.get(r3);
        if (r8 != 0) goto L_0x0041;
    L_0x002a:
        r8 = r15.getMessagesStorage();
        r8 = r8.getChannelPtsSync(r3);
        if (r8 == 0) goto L_0x0039;
    L_0x0034:
        r9 = r7.channelsPts;
        r9.put(r3, r8);
    L_0x0039:
        if (r8 != 0) goto L_0x0041;
    L_0x003b:
        r9 = 2;
        if (r4 == r9) goto L_0x0040;
    L_0x003e:
        if (r4 != r5) goto L_0x0041;
    L_0x0040:
        return;
    L_0x0041:
        if (r8 != 0) goto L_0x0044;
    L_0x0043:
        return;
    L_0x0044:
        if (r20 != 0) goto L_0x0062;
    L_0x0046:
        r9 = java.lang.Integer.valueOf(r16);
        r9 = r15.getChat(r9);
        if (r9 != 0) goto L_0x005d;
    L_0x0050:
        r9 = r15.getMessagesStorage();
        r9 = r9.getChatSync(r3);
        if (r9 == 0) goto L_0x005d;
    L_0x005a:
        r15.putChat(r9, r6);
    L_0x005d:
        r9 = getInputChannel(r9);
        goto L_0x0064;
    L_0x0062:
        r9 = r20;
    L_0x0064:
        r10 = 0;
        if (r9 == 0) goto L_0x00ef;
    L_0x0068:
        r12 = r9.access_hash;
        r14 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
        if (r14 != 0) goto L_0x0070;
    L_0x006e:
        goto L_0x00ef;
    L_0x0070:
        r12 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x009d;
    L_0x0074:
        r1 = 0;
        r10 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0090 }
        r0 = r9.getObjectSize();	 Catch:{ Exception -> 0x0090 }
        r0 = r0 + 12;
        r10.<init>(r0);	 Catch:{ Exception -> 0x0090 }
        r0 = 6;
        r10.writeInt32(r0);	 Catch:{ Exception -> 0x008e }
        r10.writeInt32(r3);	 Catch:{ Exception -> 0x008e }
        r10.writeInt32(r4);	 Catch:{ Exception -> 0x008e }
        r9.serializeToStream(r10);	 Catch:{ Exception -> 0x008e }
        goto L_0x0095;
    L_0x008e:
        r0 = move-exception;
        goto L_0x0092;
    L_0x0090:
        r0 = move-exception;
        r10 = r1;
    L_0x0092:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0095:
        r0 = r15.getMessagesStorage();
        r0 = r0.createPendingTask(r10);
    L_0x009d:
        r10 = r0;
        r0 = r7.gettingDifferenceChannels;
        r0.put(r3, r6);
        r0 = new org.telegram.tgnet.TLRPC$TL_updates_getChannelDifference;
        r0.<init>();
        r0.channel = r9;
        r1 = new org.telegram.tgnet.TLRPC$TL_channelMessagesFilterEmpty;
        r1.<init>();
        r0.filter = r1;
        r0.pts = r8;
        r0.limit = r2;
        if (r4 == r5) goto L_0x00b8;
    L_0x00b7:
        goto L_0x00b9;
    L_0x00b8:
        r6 = 0;
    L_0x00b9:
        r0.force = r6;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x00db;
    L_0x00bf:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "start getChannelDifference with pts = ";
        r1.append(r2);
        r1.append(r8);
        r2 = " channelId = ";
        r1.append(r2);
        r1.append(r3);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x00db:
        r8 = r15.getConnectionsManager();
        r9 = new org.telegram.messenger.-$$Lambda$MessagesController$x5i0ne0KceS-W4h5GT2Mo-qkysQ;
        r1 = r9;
        r2 = r15;
        r3 = r16;
        r4 = r17;
        r5 = r10;
        r1.<init>(r2, r3, r4, r5);
        r8.sendRequest(r0, r9);
        return;
    L_0x00ef:
        r2 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x00fa;
    L_0x00f3:
        r2 = r15.getMessagesStorage();
        r2.removePendingTask(r0);
    L_0x00fa:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.getChannelDifference(int, int, long, org.telegram.tgnet.TLRPC$InputChannel):void");
    }

    public /* synthetic */ void lambda$getChannelDifference$221$MessagesController(int i, int i2, long j, TLObject tLObject, TL_error tL_error) {
        int i3 = i;
        long j2 = j;
        TL_error tL_error2 = tL_error;
        if (tLObject != null) {
            Chat chat;
            updates_ChannelDifference updates_channeldifference = (updates_ChannelDifference) tLObject;
            SparseArray sparseArray = new SparseArray();
            int i4 = 0;
            for (int i5 = 0; i5 < updates_channeldifference.users.size(); i5++) {
                User user = (User) updates_channeldifference.users.get(i5);
                sparseArray.put(user.id, user);
            }
            for (int i6 = 0; i6 < updates_channeldifference.chats.size(); i6++) {
                chat = (Chat) updates_channeldifference.chats.get(i6);
                if (chat.id == i3) {
                    break;
                }
            }
            chat = null;
            ArrayList arrayList = new ArrayList();
            if (!updates_channeldifference.other_updates.isEmpty()) {
                while (i4 < updates_channeldifference.other_updates.size()) {
                    Update update = (Update) updates_channeldifference.other_updates.get(i4);
                    if (update instanceof TL_updateMessageID) {
                        arrayList.add((TL_updateMessageID) update);
                        updates_channeldifference.other_updates.remove(i4);
                        i4--;
                    }
                    i4++;
                }
            }
            getMessagesStorage().putUsersAndChats(updates_channeldifference.users, updates_channeldifference.chats, true, true);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$hWWXVtytRP4kZnjctiKBgzZknGY(this, updates_channeldifference));
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$ndqtob1Ekjw6srKD0BkMfYTUr9Y(this, arrayList, i, updates_channeldifference, chat, sparseArray, i2, j));
        } else if (tL_error2 != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$hqshPJNycm3SZUGHbDrcQpKEKgY(this, tL_error2, i));
            this.gettingDifferenceChannels.delete(i);
            if (j2 != 0) {
                getMessagesStorage().removePendingTask(j2);
            }
        }
    }

    public /* synthetic */ void lambda$null$213$MessagesController(updates_ChannelDifference updates_channeldifference) {
        putUsers(updates_channeldifference.users, false);
        putChats(updates_channeldifference.chats, false);
    }

    public /* synthetic */ void lambda$null$219$MessagesController(ArrayList arrayList, int i, updates_ChannelDifference updates_channeldifference, Chat chat, SparseArray sparseArray, int i2, long j) {
        if (!arrayList.isEmpty()) {
            SparseArray sparseArray2 = new SparseArray();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) it.next();
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, i, -1);
                if (updateMessageStateAndId != null) {
                    sparseArray2.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray2.size() != 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$K0ZNRvyUXYTS8CICZPhAmiK2Qgs(this, sparseArray2));
                Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$JOpCQs3kpDZ8TlZPGIkUgIT2nAY(this, updates_channeldifference, i, chat, sparseArray, i2, j));
            }
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$JOpCQs3kpDZ8TlZPGIkUgIT2nAY(this, updates_channeldifference, i, chat, sparseArray, i2, j));
    }

    public /* synthetic */ void lambda$null$214$MessagesController(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            getSendMessagesHelper().processSentMessage((int) ((long[]) sparseArray.valueAt(i))[1]);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(r6), Integer.valueOf(keyAt), null, Long.valueOf(r3[0]), Long.valueOf(0), Integer.valueOf(-1), Boolean.valueOf(false));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x018f A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0187  */
    public /* synthetic */ void lambda$null$218$MessagesController(org.telegram.tgnet.TLRPC.updates_ChannelDifference r17, int r18, org.telegram.tgnet.TLRPC.Chat r19, android.util.SparseArray r20, int r21, long r22) {
        /*
        r16 = this;
        r6 = r16;
        r7 = r17;
        r8 = r18;
        r0 = r19;
        r9 = r22;
        r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifference;
        r2 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = 0;
        r11 = 1;
        if (r1 != 0) goto L_0x00b4;
    L_0x0012:
        r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
        if (r1 == 0) goto L_0x0018;
    L_0x0016:
        goto L_0x00b4;
    L_0x0018:
        r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
        if (r1 == 0) goto L_0x01d7;
    L_0x001c:
        r1 = -r8;
        r4 = (long) r1;
        r1 = r6.dialogs_read_inbox_max;
        r12 = java.lang.Long.valueOf(r4);
        r1 = r1.get(r12);
        r1 = (java.lang.Integer) r1;
        if (r1 != 0) goto L_0x0041;
    L_0x002c:
        r1 = r16.getMessagesStorage();
        r1 = r1.getDialogReadMax(r3, r4);
        r1 = java.lang.Integer.valueOf(r1);
        r12 = r6.dialogs_read_inbox_max;
        r13 = java.lang.Long.valueOf(r4);
        r12.put(r13, r1);
    L_0x0041:
        r12 = r6.dialogs_read_outbox_max;
        r13 = java.lang.Long.valueOf(r4);
        r12 = r12.get(r13);
        r12 = (java.lang.Integer) r12;
        if (r12 != 0) goto L_0x0064;
    L_0x004f:
        r12 = r16.getMessagesStorage();
        r12 = r12.getDialogReadMax(r11, r4);
        r12 = java.lang.Integer.valueOf(r12);
        r13 = r6.dialogs_read_outbox_max;
        r14 = java.lang.Long.valueOf(r4);
        r13.put(r14, r12);
    L_0x0064:
        r13 = 0;
    L_0x0065:
        r14 = r7.messages;
        r14 = r14.size();
        if (r13 >= r14) goto L_0x00a6;
    L_0x006d:
        r14 = r7.messages;
        r14 = r14.get(r13);
        r14 = (org.telegram.tgnet.TLRPC.Message) r14;
        r14.dialog_id = r4;
        r15 = r14.action;
        r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r15 != 0) goto L_0x0094;
    L_0x007d:
        if (r0 == 0) goto L_0x0083;
    L_0x007f:
        r15 = r0.left;
        if (r15 != 0) goto L_0x0094;
    L_0x0083:
        r15 = r14.out;
        if (r15 == 0) goto L_0x0089;
    L_0x0087:
        r15 = r12;
        goto L_0x008a;
    L_0x0089:
        r15 = r1;
    L_0x008a:
        r15 = r15.intValue();
        r11 = r14.id;
        if (r15 >= r11) goto L_0x0094;
    L_0x0092:
        r11 = 1;
        goto L_0x0095;
    L_0x0094:
        r11 = 0;
    L_0x0095:
        r14.unread = r11;
        if (r0 == 0) goto L_0x00a2;
    L_0x0099:
        r11 = r0.megagroup;
        if (r11 == 0) goto L_0x00a2;
    L_0x009d:
        r11 = r14.flags;
        r11 = r11 | r2;
        r14.flags = r11;
    L_0x00a2:
        r13 = r13 + 1;
        r11 = 1;
        goto L_0x0065;
    L_0x00a6:
        r0 = r16.getMessagesStorage();
        r1 = r7;
        r1 = (org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong) r1;
        r2 = r21;
        r0.overwriteChannel(r8, r1, r2);
        goto L_0x01d7;
    L_0x00b4:
        r1 = r7.new_messages;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x01b5;
    L_0x00bc:
        r1 = new android.util.LongSparseArray;
        r1.<init>();
        r4 = r7.new_messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r4);
        r4 = new java.util.ArrayList;
        r4.<init>();
        r5 = -r8;
        r11 = (long) r5;
        r5 = r6.dialogs_read_inbox_max;
        r13 = java.lang.Long.valueOf(r11);
        r5 = r5.get(r13);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x00f0;
    L_0x00db:
        r5 = r16.getMessagesStorage();
        r5 = r5.getDialogReadMax(r3, r11);
        r5 = java.lang.Integer.valueOf(r5);
        r13 = r6.dialogs_read_inbox_max;
        r14 = java.lang.Long.valueOf(r11);
        r13.put(r14, r5);
    L_0x00f0:
        r13 = r6.dialogs_read_outbox_max;
        r14 = java.lang.Long.valueOf(r11);
        r13 = r13.get(r14);
        r13 = (java.lang.Integer) r13;
        if (r13 != 0) goto L_0x0114;
    L_0x00fe:
        r13 = r16.getMessagesStorage();
        r14 = 1;
        r13 = r13.getDialogReadMax(r14, r11);
        r13 = java.lang.Integer.valueOf(r13);
        r14 = r6.dialogs_read_outbox_max;
        r15 = java.lang.Long.valueOf(r11);
        r14.put(r15, r13);
    L_0x0114:
        r14 = 0;
    L_0x0115:
        r15 = r7.new_messages;
        r15 = r15.size();
        if (r14 >= r15) goto L_0x019d;
    L_0x011d:
        r15 = r7.new_messages;
        r15 = r15.get(r14);
        r15 = (org.telegram.tgnet.TLRPC.Message) r15;
        if (r0 == 0) goto L_0x012b;
    L_0x0127:
        r3 = r0.left;
        if (r3 != 0) goto L_0x0142;
    L_0x012b:
        r3 = r15.out;
        if (r3 == 0) goto L_0x0131;
    L_0x012f:
        r3 = r13;
        goto L_0x0132;
    L_0x0131:
        r3 = r5;
    L_0x0132:
        r3 = r3.intValue();
        r2 = r15.id;
        if (r3 >= r2) goto L_0x0142;
    L_0x013a:
        r2 = r15.action;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r2 != 0) goto L_0x0142;
    L_0x0140:
        r2 = 1;
        goto L_0x0143;
    L_0x0142:
        r2 = 0;
    L_0x0143:
        r15.unread = r2;
        if (r0 == 0) goto L_0x0153;
    L_0x0147:
        r2 = r0.megagroup;
        if (r2 == 0) goto L_0x0153;
    L_0x014b:
        r2 = r15.flags;
        r3 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r2 = r2 | r3;
        r15.flags = r2;
        goto L_0x0155;
    L_0x0153:
        r3 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x0155:
        r2 = new org.telegram.messenger.MessageObject;
        r3 = r6.currentAccount;
        r0 = r6.createdDialogIds;
        r21 = r5;
        r5 = java.lang.Long.valueOf(r11);
        r0 = r0.contains(r5);
        r5 = r20;
        r2.<init>(r3, r15, r5, r0);
        r0 = r2.isOut();
        if (r0 == 0) goto L_0x0176;
    L_0x0170:
        r0 = r2.messageOwner;
        r0 = r0.from_scheduled;
        if (r0 == 0) goto L_0x017f;
    L_0x0176:
        r0 = r2.isUnread();
        if (r0 == 0) goto L_0x017f;
    L_0x017c:
        r4.add(r2);
    L_0x017f:
        r0 = r1.get(r11);
        r0 = (java.util.ArrayList) r0;
        if (r0 != 0) goto L_0x018f;
    L_0x0187:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1.put(r11, r0);
    L_0x018f:
        r0.add(r2);
        r14 = r14 + 1;
        r0 = r19;
        r5 = r21;
        r2 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = 0;
        goto L_0x0115;
    L_0x019d:
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$l0dMSZgmN0gECx0z-m67_Y-by64;
        r0.<init>(r6, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r0 = r16.getMessagesStorage();
        r0 = r0.getStorageQueue();
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$cgs0OFAJnqEUItiFpqM9coCMvSw;
        r1.<init>(r6, r4, r7);
        r0.postRunnable(r1);
    L_0x01b5:
        r0 = r7.other_updates;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x01ca;
    L_0x01bd:
        r1 = r7.other_updates;
        r2 = r7.users;
        r3 = r7.chats;
        r4 = 1;
        r5 = 0;
        r0 = r16;
        r0.processUpdateArray(r1, r2, r3, r4, r5);
    L_0x01ca:
        r0 = 1;
        r6.processChannelsUpdatesQueue(r8, r0);
        r0 = r16.getMessagesStorage();
        r1 = r7.pts;
        r0.saveChannelPts(r8, r1);
    L_0x01d7:
        r0 = r6.gettingDifferenceChannels;
        r0.delete(r8);
        r0 = r6.channelsPts;
        r1 = r7.pts;
        r0.put(r8, r1);
        r0 = r7.flags;
        r0 = r0 & 2;
        if (r0 == 0) goto L_0x01f9;
    L_0x01e9:
        r0 = r6.shortPollChannels;
        r1 = java.lang.System.currentTimeMillis();
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1 = r1 / r3;
        r2 = (int) r1;
        r1 = r7.timeout;
        r2 = r2 + r1;
        r0.put(r8, r2);
    L_0x01f9:
        r0 = r7.isFinal;
        if (r0 != 0) goto L_0x0200;
    L_0x01fd:
        r6.getChannelDifference(r8);
    L_0x0200:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0274;
    L_0x0204:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "received channel difference with pts = ";
        r0.append(r1);
        r1 = r7.pts;
        r0.append(r1);
        r1 = " channelId = ";
        r0.append(r1);
        r0.append(r8);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "new_messages = ";
        r0.append(r1);
        r1 = r7.new_messages;
        r1 = r1.size();
        r0.append(r1);
        r1 = " messages = ";
        r0.append(r1);
        r1 = r7.messages;
        r1 = r1.size();
        r0.append(r1);
        r1 = " users = ";
        r0.append(r1);
        r1 = r7.users;
        r1 = r1.size();
        r0.append(r1);
        r1 = " chats = ";
        r0.append(r1);
        r1 = r7.chats;
        r1 = r1.size();
        r0.append(r1);
        r1 = " other updates = ";
        r0.append(r1);
        r1 = r7.other_updates;
        r1 = r1.size();
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0274:
        r0 = 0;
        r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1));
        if (r2 == 0) goto L_0x0281;
    L_0x027a:
        r0 = r16.getMessagesStorage();
        r0.removePendingTask(r9);
    L_0x0281:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$null$218$MessagesController(org.telegram.tgnet.TLRPC$updates_ChannelDifference, int, org.telegram.tgnet.TLRPC$Chat, android.util.SparseArray, int, long):void");
    }

    public /* synthetic */ void lambda$null$215$MessagesController(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i), false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$217$MessagesController(ArrayList arrayList, updates_ChannelDifference updates_channeldifference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$4t4R22kvGWYTZlq64vMfnqYWtp4(this, arrayList));
        }
        getMessagesStorage().putMessages(updates_channeldifference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false);
    }

    public /* synthetic */ void lambda$null$216$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$null$220$MessagesController(TL_error tL_error, int i) {
        checkChannelError(tL_error.text, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    private void checkChannelError(java.lang.String r6, int r7) {
        /*
        r5 = this;
        r0 = r6.hashCode();
        r1 = -NUM; // 0xfffffffvar_b816 float:-8.417163E-27 double:NaN;
        r2 = 0;
        r3 = 2;
        r4 = 1;
        if (r0 == r1) goto L_0x002b;
    L_0x000c:
        r1 = -NUM; // 0xffffffffd099ce07 float:-2.064333E10 double:NaN;
        if (r0 == r1) goto L_0x0021;
    L_0x0011:
        r1 = -NUM; // 0xffffffffe3ebCLASSNAMEd float:-8.69898E21 double:NaN;
        if (r0 == r1) goto L_0x0017;
    L_0x0016:
        goto L_0x0035;
    L_0x0017:
        r0 = "CHANNEL_PUBLIC_GROUP_NA";
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x0035;
    L_0x001f:
        r6 = 1;
        goto L_0x0036;
    L_0x0021:
        r0 = "CHANNEL_PRIVATE";
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x0035;
    L_0x0029:
        r6 = 0;
        goto L_0x0036;
    L_0x002b:
        r0 = "USER_BANNED_IN_CHANNEL";
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x0035;
    L_0x0033:
        r6 = 2;
        goto L_0x0036;
    L_0x0035:
        r6 = -1;
    L_0x0036:
        if (r6 == 0) goto L_0x006d;
    L_0x0038:
        if (r6 == r4) goto L_0x0055;
    L_0x003a:
        if (r6 == r3) goto L_0x003d;
    L_0x003c:
        goto L_0x0084;
    L_0x003d:
        r6 = r5.getNotificationCenter();
        r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad;
        r1 = new java.lang.Object[r3];
        r7 = java.lang.Integer.valueOf(r7);
        r1[r2] = r7;
        r7 = java.lang.Integer.valueOf(r3);
        r1[r4] = r7;
        r6.postNotificationName(r0, r1);
        goto L_0x0084;
    L_0x0055:
        r6 = r5.getNotificationCenter();
        r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad;
        r1 = new java.lang.Object[r3];
        r7 = java.lang.Integer.valueOf(r7);
        r1[r2] = r7;
        r7 = java.lang.Integer.valueOf(r4);
        r1[r4] = r7;
        r6.postNotificationName(r0, r1);
        goto L_0x0084;
    L_0x006d:
        r6 = r5.getNotificationCenter();
        r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad;
        r1 = new java.lang.Object[r3];
        r7 = java.lang.Integer.valueOf(r7);
        r1[r2] = r7;
        r7 = java.lang.Integer.valueOf(r2);
        r1[r4] = r7;
        r6.postNotificationName(r0, r1);
    L_0x0084:
        return;
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
            TL_updates_getDifference tL_updates_getDifference = new TL_updates_getDifference();
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
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("start getDifference with date = ");
                stringBuilder.append(i2);
                stringBuilder.append(" pts = ");
                stringBuilder.append(i);
                stringBuilder.append(" qts = ");
                stringBuilder.append(i3);
                FileLog.d(stringBuilder.toString());
            }
            getConnectionsManager().setIsUpdating(true);
            getConnectionsManager().sendRequest(tL_updates_getDifference, new -$$Lambda$MessagesController$H_gKtly08Uv9YVoJa22dTqzjblY(this, i2, i3));
        }
    }

    public /* synthetic */ void lambda$getDifference$230$MessagesController(int i, int i2, TLObject tLObject, TL_error tL_error) {
        int i3 = 0;
        if (tL_error == null) {
            updates_Difference updates_difference = (updates_Difference) tLObject;
            if (updates_difference instanceof TL_updates_differenceTooLong) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$GkTZK_YKBghdm0zA46jQwiLTegc(this, updates_difference, i, i2));
                return;
            }
            if (updates_difference instanceof TL_updates_differenceSlice) {
                TL_updates_state tL_updates_state = updates_difference.intermediate_state;
                getDifference(tL_updates_state.pts, tL_updates_state.date, tL_updates_state.qts, true);
            }
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (i = 0; i < updates_difference.users.size(); i++) {
                User user = (User) updates_difference.users.get(i);
                sparseArray.put(user.id, user);
            }
            for (i = 0; i < updates_difference.chats.size(); i++) {
                Chat chat = (Chat) updates_difference.chats.get(i);
                sparseArray2.put(chat.id, chat);
            }
            ArrayList arrayList = new ArrayList();
            if (!updates_difference.other_updates.isEmpty()) {
                while (i3 < updates_difference.other_updates.size()) {
                    Update update = (Update) updates_difference.other_updates.get(i3);
                    if (update instanceof TL_updateMessageID) {
                        arrayList.add((TL_updateMessageID) update);
                        updates_difference.other_updates.remove(i3);
                    } else {
                        if (getUpdateType(update) == 2) {
                            int updateChannelId = getUpdateChannelId(update);
                            int i4 = this.channelsPts.get(updateChannelId);
                            if (i4 == 0) {
                                i4 = getMessagesStorage().getChannelPtsSync(updateChannelId);
                                if (i4 != 0) {
                                    this.channelsPts.put(updateChannelId, i4);
                                }
                            }
                            if (i4 != 0 && getUpdatePts(update) <= i4) {
                                updates_difference.other_updates.remove(i3);
                            }
                        }
                        i3++;
                    }
                    i3--;
                    i3++;
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$j30_vRokDUWjGxcUsZfUTuvLGXA(this, updates_difference));
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$JMCJfeum9hip-1YKndqXXJFRRks(this, updates_difference, arrayList, sparseArray, sparseArray2));
            return;
        }
        this.gettingDifference = false;
        getConnectionsManager().setIsUpdating(false);
    }

    public /* synthetic */ void lambda$null$222$MessagesController(updates_Difference updates_difference, int i, int i2) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), updates_difference.pts, i, i2);
    }

    public /* synthetic */ void lambda$null$223$MessagesController(updates_Difference updates_difference) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        putUsers(updates_difference.users, false);
        putChats(updates_difference.chats, false);
    }

    public /* synthetic */ void lambda$null$229$MessagesController(updates_Difference updates_difference, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2) {
        updates_Difference updates_difference2 = updates_difference;
        int i = 0;
        getMessagesStorage().putUsersAndChats(updates_difference2.users, updates_difference2.chats, true, false);
        if (!arrayList.isEmpty()) {
            SparseArray sparseArray3 = new SparseArray();
            while (i < arrayList.size()) {
                TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) arrayList.get(i);
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, 0, -1);
                if (updateMessageStateAndId != null) {
                    sparseArray3.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
                i++;
            }
            if (sparseArray3.size() != 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$4ZIkuJvP7jhBlT_0zccRGZ_7NBM(this, sparseArray3));
            }
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$V3pFgXlrywa1ZnDkAcixr5nAFN0(this, updates_difference2, sparseArray, sparseArray2));
    }

    public /* synthetic */ void lambda$null$224$MessagesController(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            getSendMessagesHelper().processSentMessage((int) ((long[]) sparseArray.valueAt(i))[1]);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(r6), Integer.valueOf(keyAt), null, Long.valueOf(r3[0]), Long.valueOf(0), Integer.valueOf(-1), Boolean.valueOf(false));
        }
    }

    public /* synthetic */ void lambda$null$228$MessagesController(updates_Difference updates_difference, SparseArray sparseArray, SparseArray sparseArray2) {
        updates_Difference updates_difference2 = updates_difference;
        int i = 0;
        if (!(updates_difference2.new_messages.isEmpty() && updates_difference2.new_encrypted_messages.isEmpty())) {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i2 = 0; i2 < updates_difference2.new_encrypted_messages.size(); i2++) {
                ArrayList decryptMessage = getSecretChatHelper().decryptMessage((EncryptedMessage) updates_difference2.new_encrypted_messages.get(i2));
                if (!(decryptMessage == null || decryptMessage.isEmpty())) {
                    updates_difference2.new_messages.addAll(decryptMessage);
                }
            }
            ImageLoader.saveMessagesThumbs(updates_difference2.new_messages);
            ArrayList arrayList = new ArrayList();
            int clientUserId = getUserConfig().getClientUserId();
            for (int i3 = 0; i3 < updates_difference2.new_messages.size(); i3++) {
                Message message = (Message) updates_difference2.new_messages.get(i3);
                if (message.dialog_id == 0) {
                    Peer peer = message.to_id;
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
                SparseArray sparseArray3;
                if (((int) message.dialog_id) != 0) {
                    MessageAction messageAction = message.action;
                    if (messageAction instanceof TL_messageActionChatDeleteUser) {
                        User user = (User) sparseArray.get(messageAction.user_id);
                        if (user != null && user.bot) {
                            message.reply_markup = new TL_replyKeyboardHide();
                            message.flags |= 64;
                        }
                    } else {
                        sparseArray3 = sparseArray;
                    }
                    messageAction = message.action;
                    if ((messageAction instanceof TL_messageActionChatMigrateTo) || (messageAction instanceof TL_messageActionChannelCreate)) {
                        message.unread = false;
                        message.media_unread = false;
                    } else {
                        ConcurrentHashMap concurrentHashMap = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                        Integer num = (Integer) concurrentHashMap.get(Long.valueOf(message.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message.out, message.dialog_id));
                            concurrentHashMap.put(Long.valueOf(message.dialog_id), num);
                        }
                        message.unread = num.intValue() < message.id;
                    }
                } else {
                    sparseArray3 = sparseArray;
                }
                if (message.dialog_id == ((long) clientUserId)) {
                    message.unread = false;
                    message.media_unread = false;
                    message.out = true;
                }
                MessageObject messageObject = new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$TT-ggbvUIZp2v4t2BF3J6hO_OpY(this, longSparseArray));
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$bREMH_Fm8aJlnszx3lWybcIEEb4(this, arrayList, updates_difference2));
            getSecretChatHelper().processPendingEncMessages();
        }
        if (!updates_difference2.other_updates.isEmpty()) {
            processUpdateArray(updates_difference2.other_updates, updates_difference2.users, updates_difference2.chats, true, 0);
        }
        if (updates_difference2 instanceof TL_updates_difference) {
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
        } else if (updates_difference2 instanceof TL_updates_differenceSlice) {
            getMessagesStorage().setLastDateValue(updates_difference2.intermediate_state.date);
            getMessagesStorage().setLastPtsValue(updates_difference2.intermediate_state.pts);
            getMessagesStorage().setLastQtsValue(updates_difference2.intermediate_state.qts);
        } else if (updates_difference2 instanceof TL_updates_differenceEmpty) {
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("received difference with date = ");
            stringBuilder.append(getMessagesStorage().getLastDateValue());
            stringBuilder.append(" pts = ");
            stringBuilder.append(getMessagesStorage().getLastPtsValue());
            stringBuilder.append(" seq = ");
            stringBuilder.append(getMessagesStorage().getLastSeqValue());
            stringBuilder.append(" messages = ");
            stringBuilder.append(updates_difference2.new_messages.size());
            stringBuilder.append(" users = ");
            stringBuilder.append(updates_difference2.users.size());
            stringBuilder.append(" chats = ");
            stringBuilder.append(updates_difference2.chats.size());
            stringBuilder.append(" other updates = ");
            stringBuilder.append(updates_difference2.other_updates.size());
            FileLog.d(stringBuilder.toString());
        }
    }

    public /* synthetic */ void lambda$null$225$MessagesController(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i), false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$227$MessagesController(ArrayList arrayList, updates_Difference updates_difference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$JW3N6j9iIKXcnkQAmRm405g_-lA(this, arrayList, updates_difference));
        }
        getMessagesStorage().putMessages(updates_difference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false);
    }

    public /* synthetic */ void lambda$null$226$MessagesController(ArrayList arrayList, updates_Difference updates_difference) {
        getNotificationsController().processNewMessages(arrayList, (updates_difference instanceof TL_updates_differenceSlice) ^ 1, false, null);
    }

    public void markDialogAsUnread(long j, InputPeer inputPeer, long j2) {
        Throwable e;
        Dialog dialog = (Dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            dialog.unread_mark = true;
            if (dialog.unread_count == 0 && !isDialogMuted(j)) {
                this.unreadUnmutedDialogs++;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
            getMessagesStorage().setDialogUnread(j, true);
        }
        int i = (int) j;
        if (i != 0) {
            TL_messages_markDialogUnread tL_messages_markDialogUnread = new TL_messages_markDialogUnread();
            tL_messages_markDialogUnread.unread = true;
            if (inputPeer == null) {
                inputPeer = getInputPeer(i);
            }
            if (!(inputPeer instanceof TL_inputPeerEmpty)) {
                TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                tL_inputDialogPeer.peer = inputPeer;
                tL_messages_markDialogUnread.peer = tL_inputDialogPeer;
                if (j2 == 0) {
                    NativeByteBuffer nativeByteBuffer;
                    try {
                        nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 12);
                        try {
                            nativeByteBuffer.writeInt32(9);
                            nativeByteBuffer.writeInt64(j);
                            inputPeer.serializeToStream(nativeByteBuffer);
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_markDialogUnread, new -$$Lambda$MessagesController$ogOlVqMSd80uly1J7n2CUQmKC1c(this, j2));
                    }
                    j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                }
                getConnectionsManager().sendRequest(tL_messages_markDialogUnread, new -$$Lambda$MessagesController$ogOlVqMSd80uly1J7n2CUQmKC1c(this, j2));
            }
        }
    }

    public /* synthetic */ void lambda$markDialogAsUnread$231$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadUnreadDialogs() {
        if (!this.loadingUnreadDialogs && !getUserConfig().unreadDialogsLoaded) {
            this.loadingUnreadDialogs = true;
            getConnectionsManager().sendRequest(new TL_messages_getDialogUnreadMarks(), new -$$Lambda$MessagesController$1GMsZef_kXIpzxVik9GSRjuDB04(this));
        }
    }

    public /* synthetic */ void lambda$loadUnreadDialogs$233$MessagesController(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$NPxxwP8Wb-UfA2cTQt0hr7yfKpM(this, tLObject));
    }

    public /* synthetic */ void lambda$null$232$MessagesController(TLObject tLObject) {
        if (tLObject != null) {
            Vector vector = (Vector) tLObject;
            int size = vector.objects.size();
            for (int i = 0; i < size; i++) {
                DialogPeer dialogPeer = (DialogPeer) vector.objects.get(i);
                if (dialogPeer instanceof TL_dialogPeer) {
                    long j;
                    Peer peer = ((TL_dialogPeer) dialogPeer).peer;
                    int i2 = peer.user_id;
                    if (i2 == 0) {
                        j = 0;
                    } else if (i2 != 0) {
                        j = (long) i2;
                    } else {
                        int i3;
                        i2 = peer.chat_id;
                        if (i2 != 0) {
                            i3 = -i2;
                        } else {
                            i3 = -peer.channel_id;
                        }
                        j = (long) i3;
                    }
                    getMessagesStorage().setDialogUnread(j, true);
                    Dialog dialog = (Dialog) this.dialogs_dict.get(j);
                    if (!(dialog == null || dialog.unread_mark)) {
                        dialog.unread_mark = true;
                        if (dialog.unread_count == 0 && !isDialogMuted(j)) {
                            this.unreadUnmutedDialogs++;
                        }
                    }
                }
            }
            getUserConfig().unreadDialogsLoaded = true;
            getUserConfig().saveConfig(false);
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
            this.loadingUnreadDialogs = false;
        }
    }

    public void reorderPinnedDialogs(int i, ArrayList<InputDialogPeer> arrayList, long j) {
        Throwable e;
        TL_messages_reorderPinnedDialogs tL_messages_reorderPinnedDialogs = new TL_messages_reorderPinnedDialogs();
        tL_messages_reorderPinnedDialogs.folder_id = i;
        tL_messages_reorderPinnedDialogs.force = true;
        if (j == 0) {
            ArrayList dialogs = getDialogs(i);
            if (!dialogs.isEmpty()) {
                NativeByteBuffer nativeByteBuffer;
                int size = dialogs.size();
                int i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    Dialog dialog = (Dialog) dialogs.get(i3);
                    if (!(dialog instanceof TL_dialogFolder)) {
                        if (!dialog.pinned) {
                            break;
                        }
                        getMessagesStorage().setDialogPinned(dialog.id, dialog.pinnedNum);
                        if (((int) dialog.id) != 0) {
                            InputPeer inputPeer = getInputPeer((int) ((Dialog) dialogs.get(i3)).id);
                            TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                            tL_inputDialogPeer.peer = inputPeer;
                            tL_messages_reorderPinnedDialogs.order.add(tL_inputDialogPeer);
                            i2 += tL_inputDialogPeer.getObjectSize();
                        }
                    }
                }
                try {
                    nativeByteBuffer = new NativeByteBuffer(i2 + 12);
                    try {
                        nativeByteBuffer.writeInt32(16);
                        nativeByteBuffer.writeInt32(i);
                        nativeByteBuffer.writeInt32(tL_messages_reorderPinnedDialogs.order.size());
                        i = tL_messages_reorderPinnedDialogs.order.size();
                        for (int i4 = 0; i4 < i; i4++) {
                            ((InputDialogPeer) tL_messages_reorderPinnedDialogs.order.get(i4)).serializeToStream(nativeByteBuffer);
                        }
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                        getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new -$$Lambda$MessagesController$2UxGM8_hLfdPLZYEYv2tbgKaXR8(this, j));
                    }
                } catch (Exception e3) {
                    e = e3;
                    nativeByteBuffer = null;
                    FileLog.e(e);
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new -$$Lambda$MessagesController$2UxGM8_hLfdPLZYEYv2tbgKaXR8(this, j));
                }
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
            } else {
                return;
            }
        }
        tL_messages_reorderPinnedDialogs.order = arrayList;
        getConnectionsManager().sendRequest(tL_messages_reorderPinnedDialogs, new -$$Lambda$MessagesController$2UxGM8_hLfdPLZYEYv2tbgKaXR8(this, j));
    }

    public /* synthetic */ void lambda$reorderPinnedDialogs$234$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public boolean pinDialog(long j, boolean z, InputPeer inputPeer, long j2) {
        Throwable e;
        long createPendingTask;
        long j3 = j;
        boolean z2 = z;
        int i = (int) j3;
        Dialog dialog = (Dialog) this.dialogs_dict.get(j3);
        boolean z3 = true;
        if (dialog == null || dialog.pinned == z2) {
            if (dialog == null) {
                z3 = false;
            }
            return z3;
        }
        int i2 = dialog.folder_id;
        ArrayList dialogs = getDialogs(i2);
        dialog.pinned = z2;
        if (z2) {
            int i3 = 0;
            for (int i4 = 0; i4 < dialogs.size(); i4++) {
                Dialog dialog2 = (Dialog) dialogs.get(i4);
                if (!(dialog2 instanceof TL_dialogFolder)) {
                    if (!dialog2.pinned) {
                        break;
                    }
                    i3 = Math.max(dialog2.pinnedNum, i3);
                }
            }
            dialog.pinnedNum = i3 + 1;
        } else {
            dialog.pinnedNum = 0;
        }
        sortDialogs(null);
        if (!(z2 || dialogs.get(dialogs.size() - 1) != dialog || this.dialogsEndReached.get(i2))) {
            dialogs.remove(dialogs.size() - 1);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        if (!(i == 0 || j2 == -1)) {
            TL_messages_toggleDialogPin tL_messages_toggleDialogPin = new TL_messages_toggleDialogPin();
            tL_messages_toggleDialogPin.pinned = z2;
            InputPeer inputPeer2 = inputPeer == null ? getInputPeer(i) : inputPeer;
            if (inputPeer2 instanceof TL_inputPeerEmpty) {
                return false;
            }
            TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
            tL_inputDialogPeer.peer = inputPeer2;
            tL_messages_toggleDialogPin.peer = tL_inputDialogPeer;
            if (j2 == 0) {
                NativeByteBuffer nativeByteBuffer;
                try {
                    nativeByteBuffer = new NativeByteBuffer(inputPeer2.getObjectSize() + 16);
                    try {
                        nativeByteBuffer.writeInt32(4);
                        nativeByteBuffer.writeInt64(j3);
                        nativeByteBuffer.writeBool(z2);
                        inputPeer2.serializeToStream(nativeByteBuffer);
                    } catch (Exception e2) {
                        e = e2;
                    }
                } catch (Exception e3) {
                    e = e3;
                    nativeByteBuffer = null;
                    FileLog.e(e);
                    createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_toggleDialogPin, new -$$Lambda$MessagesController$xuFb0M7mhEtv83d6hivGvz0xR9o(this, createPendingTask));
                    getMessagesStorage().setDialogPinned(j3, dialog.pinnedNum);
                    return true;
                }
                createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer);
            } else {
                createPendingTask = j2;
            }
            getConnectionsManager().sendRequest(tL_messages_toggleDialogPin, new -$$Lambda$MessagesController$xuFb0M7mhEtv83d6hivGvz0xR9o(this, createPendingTask));
        }
        getMessagesStorage().setDialogPinned(j3, dialog.pinnedNum);
        return true;
    }

    public /* synthetic */ void lambda$pinDialog$235$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadPinnedDialogs(int i, long j, ArrayList<Long> arrayList) {
        if (this.loadingPinnedDialogs.indexOfKey(i) < 0 && !getUserConfig().isPinnedDialogsLoaded(i)) {
            this.loadingPinnedDialogs.put(i, 1);
            TL_messages_getPinnedDialogs tL_messages_getPinnedDialogs = new TL_messages_getPinnedDialogs();
            tL_messages_getPinnedDialogs.folder_id = i;
            getConnectionsManager().sendRequest(tL_messages_getPinnedDialogs, new -$$Lambda$MessagesController$el7q39IJ2-GCPFy2Gfb1L8wQRnM(this, i));
        }
    }

    public /* synthetic */ void lambda$loadPinnedDialogs$238$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            int i2;
            Chat chat;
            int i3;
            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
            ArrayList arrayList = new ArrayList(tL_messages_peerDialogs.dialogs);
            fetchFolderInLoadedPinnedDialogs(tL_messages_peerDialogs);
            TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
            tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
            tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
            tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
            tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
            LongSparseArray longSparseArray = new LongSparseArray();
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (i2 = 0; i2 < tL_messages_peerDialogs.users.size(); i2++) {
                User user = (User) tL_messages_peerDialogs.users.get(i2);
                sparseArray.put(user.id, user);
            }
            for (i2 = 0; i2 < tL_messages_peerDialogs.chats.size(); i2++) {
                chat = (Chat) tL_messages_peerDialogs.chats.get(i2);
                sparseArray2.put(chat.id, chat);
            }
            for (i2 = 0; i2 < tL_messages_peerDialogs.messages.size(); i2++) {
                Message message = (Message) tL_messages_peerDialogs.messages.get(i2);
                Peer peer = message.to_id;
                i3 = peer.channel_id;
                if (i3 != 0) {
                    chat = (Chat) sparseArray2.get(i3);
                    if (chat != null && chat.left) {
                    }
                } else {
                    int i4 = peer.chat_id;
                    if (i4 != 0) {
                        chat = (Chat) sparseArray2.get(i4);
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                }
                MessageObject messageObject = new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, false);
                longSparseArray.put(messageObject.getDialogId(), messageObject);
            }
            boolean z = !arrayList.isEmpty() && (arrayList.get(0) instanceof TL_dialogFolder);
            int size = arrayList.size();
            for (i3 = 0; i3 < size; i3++) {
                Dialog dialog = (Dialog) arrayList.get(i3);
                dialog.pinned = true;
                DialogObject.initDialog(dialog);
                Chat chat2;
                if (DialogObject.isChannel(dialog)) {
                    chat2 = (Chat) sparseArray2.get(-((int) dialog.id));
                    if (chat2 != null && chat2.left) {
                    }
                } else {
                    long j = dialog.id;
                    if (((int) j) < 0) {
                        chat2 = (Chat) sparseArray2.get(-((int) j));
                        if (!(chat2 == null || chat2.migrated_to == null)) {
                        }
                    }
                }
                if (dialog.last_message_date == 0) {
                    MessageObject messageObject2 = (MessageObject) longSparseArray.get(dialog.id);
                    if (messageObject2 != null) {
                        dialog.last_message_date = messageObject2.messageOwner.date;
                    }
                }
                Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = Integer.valueOf(0);
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num.intValue(), dialog.read_inbox_max_id)));
                num = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = Integer.valueOf(0);
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(num.intValue(), dialog.read_outbox_max_id)));
            }
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$w4_lB0Qdt5ko3PiAjP1Zc2MNL1E(this, i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs));
        }
    }

    public /* synthetic */ void lambda$null$237$MessagesController(int i, ArrayList arrayList, boolean z, TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TL_messages_dialogs tL_messages_dialogs) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$uqeuJxZIblmq1Ywq8gDS_Zf1qIo(this, i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs));
    }

    public /* synthetic */ void lambda$null$236$MessagesController(int i, ArrayList arrayList, boolean z, TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TL_messages_dialogs tL_messages_dialogs) {
        int i2;
        Dialog dialog;
        Object obj;
        int size;
        int i3 = i;
        ArrayList arrayList2 = arrayList;
        TL_messages_peerDialogs tL_messages_peerDialogs2 = tL_messages_peerDialogs;
        this.loadingPinnedDialogs.delete(i3);
        applyDialogsNotificationsSettings(arrayList2);
        ArrayList dialogs = getDialogs(i);
        int i4 = z;
        int i5 = 0;
        Object obj2 = null;
        for (i2 = 0; i2 < dialogs.size(); i2++) {
            dialog = (Dialog) dialogs.get(i2);
            if (!(dialog instanceof TL_dialogFolder)) {
                if (((int) dialog.id) == 0) {
                    if (i4 < arrayList.size()) {
                        arrayList2.add(i4, dialog);
                    } else {
                        arrayList2.add(dialog);
                    }
                    i4++;
                } else if (!dialog.pinned) {
                    break;
                } else {
                    i5 = Math.max(dialog.pinnedNum, i5);
                    dialog.pinned = false;
                    dialog.pinnedNum = 0;
                    i4++;
                    obj2 = 1;
                }
            }
        }
        dialogs = new ArrayList();
        if (arrayList.isEmpty()) {
            obj = null;
        } else {
            putUsers(tL_messages_peerDialogs2.users, false);
            putChats(tL_messages_peerDialogs2.chats, false);
            size = arrayList.size();
            i2 = 0;
            obj = null;
            while (i2 < size) {
                Dialog dialog2 = (Dialog) arrayList2.get(i2);
                dialog2.pinnedNum = (size - i2) + i5;
                dialogs.add(Long.valueOf(dialog2.id));
                dialog = (Dialog) this.dialogs_dict.get(dialog2.id);
                if (dialog != null) {
                    dialog.pinned = true;
                    dialog.pinnedNum = dialog2.pinnedNum;
                    getMessagesStorage().setDialogPinned(dialog2.id, dialog2.pinnedNum);
                    LongSparseArray longSparseArray2 = longSparseArray;
                } else {
                    this.dialogs_dict.put(dialog2.id, dialog2);
                    MessageObject messageObject = (MessageObject) longSparseArray.get(dialog2.id);
                    this.dialogMessage.put(dialog2.id, messageObject);
                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        long j = messageObject.messageOwner.random_id;
                        if (j != 0) {
                            this.dialogMessagesByRandomIds.put(j, messageObject);
                        }
                    }
                    obj = 1;
                }
                i2++;
                obj2 = 1;
            }
        }
        if (obj2 != null) {
            if (obj != null) {
                this.allDialogs.clear();
                int size2 = this.dialogs_dict.size();
                for (size = 0; size < size2; size++) {
                    this.allDialogs.add(this.dialogs_dict.valueAt(size));
                }
            }
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getMessagesStorage().unpinAllDialogsExceptNew(dialogs, i3);
        getMessagesStorage().putDialogs(tL_messages_dialogs, 1);
        getUserConfig().setPinnedDialogsLoaded(i3, true);
        getUserConfig().saveConfig(false);
    }

    public void generateJoinMessage(int i, boolean z) {
        Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i, this.currentAccount)) {
            if ((!chat.left && !chat.kicked) || z) {
                TL_messageService tL_messageService = new TL_messageService();
                tL_messageService.flags = 256;
                int newMessageId = getUserConfig().getNewMessageId();
                tL_messageService.id = newMessageId;
                tL_messageService.local_id = newMessageId;
                tL_messageService.date = getConnectionsManager().getCurrentTime();
                tL_messageService.from_id = getUserConfig().getClientUserId();
                tL_messageService.to_id = new TL_peerChannel();
                tL_messageService.to_id.channel_id = i;
                tL_messageService.dialog_id = (long) (-i);
                tL_messageService.post = true;
                tL_messageService.action = new TL_messageActionChatAddUser();
                tL_messageService.action.users.add(Integer.valueOf(getUserConfig().getClientUserId()));
                if (chat.megagroup) {
                    tL_messageService.flags |= Integer.MIN_VALUE;
                }
                getUserConfig().saveConfig(false);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(tL_messageService);
                arrayList.add(new MessageObject(this.currentAccount, tL_messageService, true));
                getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$hzi_rdpISd9nft1780cpiel2Yxg(this, arrayList));
                getMessagesStorage().putMessages(arrayList2, true, true, false, 0, false);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$gxJvzQ3-UemGYE3KrbjJj4xnB58(this, i, arrayList));
            }
        }
    }

    public /* synthetic */ void lambda$generateJoinMessage$240$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$4tVE-BALjMccrqcV1uR_YeYp9k4(this, arrayList));
    }

    public /* synthetic */ void lambda$null$239$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$generateJoinMessage$241$MessagesController(int i, ArrayList arrayList) {
        updateInterfaceWithMessages((long) (-i), arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* Access modifiers changed, original: protected */
    public void deleteMessagesByPush(long j, ArrayList<Integer> arrayList, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$7KM21XZqEmZ2kaanTV07CK3A6zo(this, arrayList, i, j));
    }

    public /* synthetic */ void lambda$deleteMessagesByPush$243$MessagesController(ArrayList arrayList, int i, long j) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$FBa7l6ujLgWMIh3Cs8BHdrnxN48(this, arrayList, i));
        getMessagesStorage().deletePushMessages(j, arrayList);
        getMessagesStorage().updateDialogsWithDeletedMessages(arrayList, getMessagesStorage().markMessagesAsDeleted(arrayList, false, i, true, false), false, i);
    }

    public /* synthetic */ void lambda$null$242$MessagesController(ArrayList arrayList, int i) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i2 = NotificationCenter.messagesDeleted;
        r2 = new Object[3];
        int i3 = 0;
        r2[0] = arrayList;
        r2[1] = Integer.valueOf(i);
        r2[2] = Boolean.valueOf(false);
        notificationCenter.postNotificationName(i2, r2);
        if (i == 0) {
            i = arrayList.size();
            while (i3 < i) {
                MessageObject messageObject = (MessageObject) this.dialogMessagesByIds.get(((Integer) arrayList.get(i3)).intValue());
                if (messageObject != null) {
                    messageObject.deleted = true;
                }
                i3++;
            }
            return;
        }
        MessageObject messageObject2 = (MessageObject) this.dialogMessage.get((long) (-i));
        if (messageObject2 != null) {
            int size = arrayList.size();
            while (i3 < size) {
                if (messageObject2.getId() == ((Integer) arrayList.get(i3)).intValue()) {
                    messageObject2.deleted = true;
                    return;
                }
                i3++;
            }
        }
    }

    public void checkChannelInviter(int i) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$WUcsXG8lPvar_RZqxFka1Ha4dTL0(this, i));
    }

    public /* synthetic */ void lambda$checkChannelInviter$249$MessagesController(int i) {
        Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i, this.currentAccount) && !chat.creator) {
            TL_channels_getParticipant tL_channels_getParticipant = new TL_channels_getParticipant();
            tL_channels_getParticipant.channel = getInputChannel(i);
            tL_channels_getParticipant.user_id = new TL_inputUserSelf();
            getConnectionsManager().sendRequest(tL_channels_getParticipant, new -$$Lambda$MessagesController$w5ObUHWa_1lAGv3jG-dJAsJESDY(this, chat, i));
        }
    }

    public /* synthetic */ void lambda$null$248$MessagesController(Chat chat, int i, TLObject tLObject, TL_error tL_error) {
        TL_channels_channelParticipant tL_channels_channelParticipant = (TL_channels_channelParticipant) tLObject;
        if (tL_channels_channelParticipant != null) {
            ChannelParticipant channelParticipant = tL_channels_channelParticipant.participant;
            if ((channelParticipant instanceof TL_channelParticipantSelf) && channelParticipant.inviter_id != getUserConfig().getClientUserId() && (!chat.megagroup || !getMessagesStorage().isMigratedChat(chat.id))) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$yRdevvzPb5CiHCrpy0gu0nQ3OEA(this, tL_channels_channelParticipant));
                getMessagesStorage().putUsersAndChats(tL_channels_channelParticipant.users, null, true, true);
                Message tL_messageService = new TL_messageService();
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
                tL_messageService.action = new TL_messageActionChatAddUser();
                tL_messageService.from_id = tL_channels_channelParticipant.participant.inviter_id;
                tL_messageService.action.users.add(Integer.valueOf(getUserConfig().getClientUserId()));
                tL_messageService.to_id = new TL_peerChannel();
                tL_messageService.to_id.channel_id = i;
                tL_messageService.dialog_id = (long) (-i);
                int i2 = 0;
                getUserConfig().saveConfig(false);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                AbstractMap concurrentHashMap = new ConcurrentHashMap();
                while (i2 < tL_channels_channelParticipant.users.size()) {
                    User user = (User) tL_channels_channelParticipant.users.get(i2);
                    concurrentHashMap.put(Integer.valueOf(user.id), user);
                    i2++;
                }
                arrayList2.add(tL_messageService);
                arrayList.add(new MessageObject(this.currentAccount, tL_messageService, concurrentHashMap, true));
                getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MessagesController$wC7rqtVcAfymedoa-ZshP2BMxtc(this, arrayList));
                getMessagesStorage().putMessages(arrayList2, true, true, false, 0, false);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$1t-1GuhSm0vSvNxRqh_Y6FYgvEY(this, i, arrayList));
            }
        }
    }

    public /* synthetic */ void lambda$null$244$MessagesController(TL_channels_channelParticipant tL_channels_channelParticipant) {
        putUsers(tL_channels_channelParticipant.users, false);
    }

    public /* synthetic */ void lambda$null$245$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$null$246$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$n3Rm6l3qze3tvyBpL2P81kfLBOg(this, arrayList));
    }

    public /* synthetic */ void lambda$null$247$MessagesController(int i, ArrayList arrayList) {
        updateInterfaceWithMessages((long) (-i), arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private int getUpdateType(Update update) {
        if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateReadMessagesContents) || (update instanceof TL_updateReadHistoryInbox) || (update instanceof TL_updateReadHistoryOutbox) || (update instanceof TL_updateDeleteMessages) || (update instanceof TL_updateWebPage) || (update instanceof TL_updateEditMessage) || (update instanceof TL_updateFolderPeers)) {
            return 0;
        }
        if (update instanceof TL_updateNewEncryptedMessage) {
            return 1;
        }
        return ((update instanceof TL_updateNewChannelMessage) || (update instanceof TL_updateDeleteChannelMessages) || (update instanceof TL_updateEditChannelMessage) || (update instanceof TL_updateChannelWebPage)) ? 2 : 3;
    }

    private static int getUpdatePts(Update update) {
        if (update instanceof TL_updateDeleteMessages) {
            return ((TL_updateDeleteMessages) update).pts;
        }
        if (update instanceof TL_updateNewChannelMessage) {
            return ((TL_updateNewChannelMessage) update).pts;
        }
        if (update instanceof TL_updateReadHistoryOutbox) {
            return ((TL_updateReadHistoryOutbox) update).pts;
        }
        if (update instanceof TL_updateNewMessage) {
            return ((TL_updateNewMessage) update).pts;
        }
        if (update instanceof TL_updateEditMessage) {
            return ((TL_updateEditMessage) update).pts;
        }
        if (update instanceof TL_updateWebPage) {
            return ((TL_updateWebPage) update).pts;
        }
        if (update instanceof TL_updateReadHistoryInbox) {
            return ((TL_updateReadHistoryInbox) update).pts;
        }
        if (update instanceof TL_updateChannelWebPage) {
            return ((TL_updateChannelWebPage) update).pts;
        }
        if (update instanceof TL_updateDeleteChannelMessages) {
            return ((TL_updateDeleteChannelMessages) update).pts;
        }
        if (update instanceof TL_updateEditChannelMessage) {
            return ((TL_updateEditChannelMessage) update).pts;
        }
        if (update instanceof TL_updateReadMessagesContents) {
            return ((TL_updateReadMessagesContents) update).pts;
        }
        if (update instanceof TL_updateChannelTooLong) {
            return ((TL_updateChannelTooLong) update).pts;
        }
        return update instanceof TL_updateFolderPeers ? ((TL_updateFolderPeers) update).pts : 0;
    }

    private static int getUpdatePtsCount(Update update) {
        if (update instanceof TL_updateDeleteMessages) {
            return ((TL_updateDeleteMessages) update).pts_count;
        }
        if (update instanceof TL_updateNewChannelMessage) {
            return ((TL_updateNewChannelMessage) update).pts_count;
        }
        if (update instanceof TL_updateReadHistoryOutbox) {
            return ((TL_updateReadHistoryOutbox) update).pts_count;
        }
        if (update instanceof TL_updateNewMessage) {
            return ((TL_updateNewMessage) update).pts_count;
        }
        if (update instanceof TL_updateEditMessage) {
            return ((TL_updateEditMessage) update).pts_count;
        }
        if (update instanceof TL_updateWebPage) {
            return ((TL_updateWebPage) update).pts_count;
        }
        if (update instanceof TL_updateReadHistoryInbox) {
            return ((TL_updateReadHistoryInbox) update).pts_count;
        }
        if (update instanceof TL_updateChannelWebPage) {
            return ((TL_updateChannelWebPage) update).pts_count;
        }
        if (update instanceof TL_updateDeleteChannelMessages) {
            return ((TL_updateDeleteChannelMessages) update).pts_count;
        }
        if (update instanceof TL_updateEditChannelMessage) {
            return ((TL_updateEditChannelMessage) update).pts_count;
        }
        if (update instanceof TL_updateReadMessagesContents) {
            return ((TL_updateReadMessagesContents) update).pts_count;
        }
        return update instanceof TL_updateFolderPeers ? ((TL_updateFolderPeers) update).pts_count : 0;
    }

    private static int getUpdateQts(Update update) {
        return update instanceof TL_updateNewEncryptedMessage ? ((TL_updateNewEncryptedMessage) update).qts : 0;
    }

    private static int getUpdateChannelId(Update update) {
        if (update instanceof TL_updateNewChannelMessage) {
            return ((TL_updateNewChannelMessage) update).message.to_id.channel_id;
        }
        if (update instanceof TL_updateEditChannelMessage) {
            return ((TL_updateEditChannelMessage) update).message.to_id.channel_id;
        }
        if (update instanceof TL_updateReadChannelOutbox) {
            return ((TL_updateReadChannelOutbox) update).channel_id;
        }
        if (update instanceof TL_updateChannelMessageViews) {
            return ((TL_updateChannelMessageViews) update).channel_id;
        }
        if (update instanceof TL_updateChannelTooLong) {
            return ((TL_updateChannelTooLong) update).channel_id;
        }
        if (update instanceof TL_updateChannelPinnedMessage) {
            return ((TL_updateChannelPinnedMessage) update).channel_id;
        }
        if (update instanceof TL_updateChannelReadMessagesContents) {
            return ((TL_updateChannelReadMessagesContents) update).channel_id;
        }
        if (update instanceof TL_updateChannelAvailableMessages) {
            return ((TL_updateChannelAvailableMessages) update).channel_id;
        }
        if (update instanceof TL_updateChannel) {
            return ((TL_updateChannel) update).channel_id;
        }
        if (update instanceof TL_updateChannelWebPage) {
            return ((TL_updateChannelWebPage) update).channel_id;
        }
        if (update instanceof TL_updateDeleteChannelMessages) {
            return ((TL_updateDeleteChannelMessages) update).channel_id;
        }
        if (update instanceof TL_updateReadChannelInbox) {
            return ((TL_updateReadChannelInbox) update).channel_id;
        }
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("trying to get unknown update channel_id for ");
            stringBuilder.append(update);
            FileLog.e(stringBuilder.toString());
        }
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:341:0x076b  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0768  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x06f9  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0768  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x076b  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x06f9  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x076b  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0768  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x057e  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0554  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0554  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x057e  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x097c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0996  */
    /* JADX WARNING: Missing block: B:96:0x024c, code skipped:
            if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r6.updatesStartWaitTimePts) > 1500) goto L_0x01dc;
     */
    /* JADX WARNING: Missing block: B:130:0x0320, code skipped:
            if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r6.updatesStartWaitTimeQts) > 1500) goto L_0x01dc;
     */
    /* JADX WARNING: Missing block: B:213:0x0530, code skipped:
            if (getMessagesStorage().getLastSeqValue() != r7.seq_start) goto L_0x0533;
     */
    /* JADX WARNING: Missing block: B:220:0x054f, code skipped:
            if (r1 != getMessagesStorage().getLastSeqValue()) goto L_0x0533;
     */
    /* JADX WARNING: Missing block: B:292:0x06b2, code skipped:
            if (r3 != null) goto L_0x06b4;
     */
    /* JADX WARNING: Missing block: B:294:0x06b6, code skipped:
            if (r5 != null) goto L_0x06b9;
     */
    /* JADX WARNING: Missing block: B:304:0x06df, code skipped:
            if (r3 != null) goto L_0x06e1;
     */
    /* JADX WARNING: Missing block: B:306:0x06e3, code skipped:
            if (r5 != null) goto L_0x06b9;
     */
    public void processUpdates(org.telegram.tgnet.TLRPC.Updates r24, boolean r25) {
        /*
        r23 = this;
        r6 = r23;
        r7 = r24;
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShort;
        r8 = 0;
        r9 = 0;
        r10 = 1;
        if (r0 == 0) goto L_0x0020;
    L_0x000b:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r0 = r7.update;
        r1.add(r0);
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = r7.date;
        r0 = r23;
        r0.processUpdateArray(r1, r2, r3, r4, r5);
        goto L_0x007b;
    L_0x0020:
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
        r11 = " count = ";
        r12 = "add to queue";
        r15 = " ";
        if (r0 != 0) goto L_0x0612;
    L_0x002a:
        r1 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r1 == 0) goto L_0x0030;
    L_0x002e:
        goto L_0x0612;
    L_0x0030:
        r3 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r3 != 0) goto L_0x0080;
    L_0x0034:
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updates;
        if (r0 == 0) goto L_0x0039;
    L_0x0038:
        goto L_0x0080;
    L_0x0039:
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updatesTooLong;
        if (r0 == 0) goto L_0x0049;
    L_0x003d:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0046;
    L_0x0041:
        r0 = "need get diff TL_updatesTooLong";
        org.telegram.messenger.FileLog.d(r0);
    L_0x0046:
        r0 = 0;
        r9 = 1;
        goto L_0x007c;
    L_0x0049:
        r0 = r7 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesSeq;
        if (r0 == 0) goto L_0x0057;
    L_0x004d:
        r0 = r23.getMessagesStorage();
        r1 = r7.seq;
        r0.setLastSeqValue(r1);
        goto L_0x007b;
    L_0x0057:
        r0 = r7 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesPts;
        if (r0 == 0) goto L_0x007b;
    L_0x005b:
        r0 = r7.chat_id;
        if (r0 == 0) goto L_0x0072;
    L_0x005f:
        r1 = r6.channelsPts;
        r2 = r7.pts;
        r1.put(r0, r2);
        r0 = r23.getMessagesStorage();
        r1 = r7.chat_id;
        r2 = r7.pts;
        r0.saveChannelPts(r1, r2);
        goto L_0x007b;
    L_0x0072:
        r0 = r23.getMessagesStorage();
        r1 = r7.pts;
        r0.setLastPtsValue(r1);
    L_0x007b:
        r0 = 0;
    L_0x007c:
        r19 = 0;
        goto L_0x0939;
    L_0x0080:
        r1 = r8;
        r0 = 0;
    L_0x0082:
        r2 = r7.chats;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x00cc;
    L_0x008a:
        r2 = r7.chats;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Chat) r2;
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channel;
        if (r4 == 0) goto L_0x00c9;
    L_0x0096:
        r4 = r2.min;
        if (r4 == 0) goto L_0x00c9;
    L_0x009a:
        r4 = r2.id;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r6.getChat(r4);
        if (r4 == 0) goto L_0x00aa;
    L_0x00a6:
        r5 = r4.min;
        if (r5 == 0) goto L_0x00b7;
    L_0x00aa:
        r4 = r23.getMessagesStorage();
        r5 = r7.chat_id;
        r4 = r4.getChatSync(r5);
        r6.putChat(r4, r10);
    L_0x00b7:
        if (r4 == 0) goto L_0x00bd;
    L_0x00b9:
        r4 = r4.min;
        if (r4 == 0) goto L_0x00c9;
    L_0x00bd:
        if (r1 != 0) goto L_0x00c4;
    L_0x00bf:
        r1 = new android.util.SparseArray;
        r1.<init>();
    L_0x00c4:
        r4 = r2.id;
        r1.put(r4, r2);
    L_0x00c9:
        r0 = r0 + 1;
        goto L_0x0082;
    L_0x00cc:
        if (r1 == 0) goto L_0x010e;
    L_0x00ce:
        r0 = 0;
    L_0x00cf:
        r2 = r7.updates;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x010e;
    L_0x00d7:
        r2 = r7.updates;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Update) r2;
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r4 == 0) goto L_0x010b;
    L_0x00e3:
        r2 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r2;
        r2 = r2.message;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        r4 = r1.indexOfKey(r2);
        if (r4 < 0) goto L_0x010b;
    L_0x00f1:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0109;
    L_0x00f5:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "need get diff because of min channel ";
        r0.append(r1);
        r0.append(r2);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0109:
        r0 = 1;
        goto L_0x010f;
    L_0x010b:
        r0 = r0 + 1;
        goto L_0x00cf;
    L_0x010e:
        r0 = 0;
    L_0x010f:
        if (r0 != 0) goto L_0x060c;
    L_0x0111:
        r1 = r23.getMessagesStorage();
        r2 = r7.users;
        r4 = r7.chats;
        r1.putUsersAndChats(r2, r4, r10, r10);
        r1 = r7.updates;
        r2 = r6.updatesComparator;
        java.util.Collections.sort(r1, r2);
        r18 = r0;
        r19 = 0;
    L_0x0127:
        r0 = r7.updates;
        r0 = r0.size();
        if (r0 <= 0) goto L_0x0514;
    L_0x012f:
        r0 = r7.updates;
        r0 = r0.get(r9);
        r0 = (org.telegram.tgnet.TLRPC.Update) r0;
        r1 = r6.getUpdateType(r0);
        if (r1 != 0) goto L_0x0268;
    L_0x013d:
        r5 = new org.telegram.tgnet.TLRPC$TL_updates;
        r5.<init>();
        r1 = r5.updates;
        r1.add(r0);
        r1 = getUpdatePts(r0);
        r5.pts = r1;
        r1 = getUpdatePtsCount(r0);
        r5.pts_count = r1;
    L_0x0153:
        r1 = r7.updates;
        r1 = r1.size();
        if (r10 >= r1) goto L_0x0189;
    L_0x015b:
        r1 = r7.updates;
        r1 = r1.get(r10);
        r1 = (org.telegram.tgnet.TLRPC.Update) r1;
        r2 = getUpdatePts(r1);
        r4 = getUpdatePtsCount(r1);
        r20 = r6.getUpdateType(r1);
        if (r20 != 0) goto L_0x0189;
    L_0x0171:
        r9 = r5.pts;
        r9 = r9 + r4;
        if (r9 != r2) goto L_0x0189;
    L_0x0176:
        r9 = r5.updates;
        r9.add(r1);
        r5.pts = r2;
        r1 = r5.pts_count;
        r1 = r1 + r4;
        r5.pts_count = r1;
        r1 = r7.updates;
        r1.remove(r10);
        r9 = 0;
        goto L_0x0153;
    L_0x0189:
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastPtsValue();
        r2 = r5.pts_count;
        r1 = r1 + r2;
        r2 = r5.pts;
        if (r1 != r2) goto L_0x01eb;
    L_0x0198:
        r1 = r5.updates;
        r2 = r7.users;
        r4 = r7.chats;
        r9 = 0;
        r0 = r7.date;
        r21 = r0;
        r0 = r23;
        r22 = r3;
        r3 = r4;
        r13 = 0;
        r4 = r9;
        r9 = r5;
        r5 = r21;
        r0 = r0.processUpdateArray(r1, r2, r3, r4, r5);
        if (r0 != 0) goto L_0x01e0;
    L_0x01b4:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x01dc;
    L_0x01b8:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "need get diff inner TL_updates, pts: ";
        r0.append(r1);
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastPtsValue();
        r0.append(r1);
        r0.append(r15);
        r1 = r7.seq;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x01dc:
        r18 = 1;
        goto L_0x0508;
    L_0x01e0:
        r0 = r23.getMessagesStorage();
        r1 = r9.pts;
        r0.setLastPtsValue(r1);
        goto L_0x0508;
    L_0x01eb:
        r22 = r3;
        r9 = r5;
        r13 = 0;
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastPtsValue();
        r2 = r9.pts;
        if (r1 == r2) goto L_0x0508;
    L_0x01fc:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x022f;
    L_0x0200:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r0 = " need get diff, pts: ";
        r1.append(r0);
        r0 = r23.getMessagesStorage();
        r0 = r0.getLastPtsValue();
        r1.append(r0);
        r1.append(r15);
        r0 = r9.pts;
        r1.append(r0);
        r1.append(r11);
        r0 = r9.pts_count;
        r1.append(r0);
        r0 = r1.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x022f:
        r0 = r6.gettingDifference;
        if (r0 != 0) goto L_0x024e;
    L_0x0233:
        r0 = r6.updatesStartWaitTimePts;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 == 0) goto L_0x024e;
    L_0x0239:
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 == 0) goto L_0x01dc;
    L_0x023d:
        r0 = java.lang.System.currentTimeMillis();
        r2 = r6.updatesStartWaitTimePts;
        r0 = r0 - r2;
        r0 = java.lang.Math.abs(r0);
        r2 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 > 0) goto L_0x01dc;
    L_0x024e:
        r0 = r6.updatesStartWaitTimePts;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 != 0) goto L_0x025a;
    L_0x0254:
        r0 = java.lang.System.currentTimeMillis();
        r6.updatesStartWaitTimePts = r0;
    L_0x025a:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0261;
    L_0x025e:
        org.telegram.messenger.FileLog.d(r12);
    L_0x0261:
        r0 = r6.updatesQueuePts;
        r0.add(r9);
        goto L_0x0508;
    L_0x0268:
        r22 = r3;
        r13 = 0;
        r1 = r6.getUpdateType(r0);
        if (r1 != r10) goto L_0x0357;
    L_0x0272:
        r9 = new org.telegram.tgnet.TLRPC$TL_updates;
        r9.<init>();
        r1 = r9.updates;
        r1.add(r0);
        r1 = getUpdateQts(r0);
        r9.pts = r1;
    L_0x0282:
        r1 = r7.updates;
        r1 = r1.size();
        if (r10 >= r1) goto L_0x02ae;
    L_0x028a:
        r1 = r7.updates;
        r1 = r1.get(r10);
        r1 = (org.telegram.tgnet.TLRPC.Update) r1;
        r2 = getUpdateQts(r1);
        r3 = r6.getUpdateType(r1);
        if (r3 != r10) goto L_0x02ae;
    L_0x029c:
        r3 = r9.pts;
        r3 = r3 + r10;
        if (r3 != r2) goto L_0x02ae;
    L_0x02a1:
        r3 = r9.updates;
        r3.add(r1);
        r9.pts = r2;
        r1 = r7.updates;
        r1.remove(r10);
        goto L_0x0282;
    L_0x02ae:
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastQtsValue();
        if (r1 == 0) goto L_0x033c;
    L_0x02b8:
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastQtsValue();
        r2 = r9.updates;
        r2 = r2.size();
        r1 = r1 + r2;
        r2 = r9.pts;
        if (r1 != r2) goto L_0x02cc;
    L_0x02cb:
        goto L_0x033c;
    L_0x02cc:
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastPtsValue();
        r2 = r9.pts;
        if (r1 == r2) goto L_0x0508;
    L_0x02d8:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0303;
    L_0x02dc:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r0 = " need get diff, qts: ";
        r1.append(r0);
        r0 = r23.getMessagesStorage();
        r0 = r0.getLastQtsValue();
        r1.append(r0);
        r1.append(r15);
        r0 = r9.pts;
        r1.append(r0);
        r0 = r1.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0303:
        r0 = r6.gettingDifference;
        if (r0 != 0) goto L_0x0322;
    L_0x0307:
        r0 = r6.updatesStartWaitTimeQts;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 == 0) goto L_0x0322;
    L_0x030d:
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 == 0) goto L_0x01dc;
    L_0x0311:
        r0 = java.lang.System.currentTimeMillis();
        r2 = r6.updatesStartWaitTimeQts;
        r0 = r0 - r2;
        r0 = java.lang.Math.abs(r0);
        r2 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 > 0) goto L_0x01dc;
    L_0x0322:
        r0 = r6.updatesStartWaitTimeQts;
        r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r2 != 0) goto L_0x032e;
    L_0x0328:
        r0 = java.lang.System.currentTimeMillis();
        r6.updatesStartWaitTimeQts = r0;
    L_0x032e:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0335;
    L_0x0332:
        org.telegram.messenger.FileLog.d(r12);
    L_0x0335:
        r0 = r6.updatesQueueQts;
        r0.add(r9);
        goto L_0x0508;
    L_0x033c:
        r1 = r9.updates;
        r2 = r7.users;
        r3 = r7.chats;
        r4 = 0;
        r5 = r7.date;
        r0 = r23;
        r0.processUpdateArray(r1, r2, r3, r4, r5);
        r0 = r23.getMessagesStorage();
        r1 = r9.pts;
        r0.setLastQtsValue(r1);
        r19 = 1;
        goto L_0x0508;
    L_0x0357:
        r1 = r6.getUpdateType(r0);
        r2 = 2;
        if (r1 != r2) goto L_0x0516;
    L_0x035e:
        r9 = getUpdateChannelId(r0);
        r1 = r6.channelsPts;
        r1 = r1.get(r9);
        if (r1 != 0) goto L_0x0396;
    L_0x036a:
        r1 = r23.getMessagesStorage();
        r1 = r1.getChannelPtsSync(r9);
        if (r1 != 0) goto L_0x0391;
    L_0x0374:
        r3 = 0;
    L_0x0375:
        r4 = r7.chats;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x0396;
    L_0x037d:
        r4 = r7.chats;
        r4 = r4.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.Chat) r4;
        r5 = r4.id;
        if (r5 != r9) goto L_0x038e;
    L_0x0389:
        r6.loadUnknownChannel(r4, r13);
        r3 = 1;
        goto L_0x0397;
    L_0x038e:
        r3 = r3 + 1;
        goto L_0x0375;
    L_0x0391:
        r3 = r6.channelsPts;
        r3.put(r9, r1);
    L_0x0396:
        r3 = 0;
    L_0x0397:
        r5 = new org.telegram.tgnet.TLRPC$TL_updates;
        r5.<init>();
        r4 = r5.updates;
        r4.add(r0);
        r4 = getUpdatePts(r0);
        r5.pts = r4;
        r4 = getUpdatePtsCount(r0);
        r5.pts_count = r4;
    L_0x03ad:
        r4 = r7.updates;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x03eb;
    L_0x03b5:
        r4 = r7.updates;
        r4 = r4.get(r10);
        r4 = (org.telegram.tgnet.TLRPC.Update) r4;
        r13 = getUpdatePts(r4);
        r14 = getUpdatePtsCount(r4);
        r10 = r6.getUpdateType(r4);
        if (r10 != r2) goto L_0x03eb;
    L_0x03cb:
        r10 = getUpdateChannelId(r4);
        if (r9 != r10) goto L_0x03eb;
    L_0x03d1:
        r10 = r5.pts;
        r10 = r10 + r14;
        if (r10 != r13) goto L_0x03eb;
    L_0x03d6:
        r10 = r5.updates;
        r10.add(r4);
        r5.pts = r13;
        r4 = r5.pts_count;
        r4 = r4 + r14;
        r5.pts_count = r4;
        r4 = r7.updates;
        r10 = 1;
        r4.remove(r10);
        r13 = 0;
        goto L_0x03ad;
    L_0x03eb:
        if (r3 != 0) goto L_0x04f0;
    L_0x03ed:
        r2 = r5.pts_count;
        r2 = r2 + r1;
        r3 = r5.pts;
        if (r2 != r3) goto L_0x044e;
    L_0x03f4:
        r1 = r5.updates;
        r2 = r7.users;
        r3 = r7.chats;
        r4 = 0;
        r10 = r7.date;
        r0 = r23;
        r13 = r5;
        r5 = r10;
        r0 = r0.processUpdateArray(r1, r2, r3, r4, r5);
        if (r0 != 0) goto L_0x043c;
    L_0x0407:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x041f;
    L_0x040b:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "need get channel diff inner TL_updates, channel_id = ";
        r0.append(r1);
        r0.append(r9);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x041f:
        if (r8 != 0) goto L_0x0429;
    L_0x0421:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r8 = r0;
        goto L_0x0508;
    L_0x0429:
        r0 = java.lang.Integer.valueOf(r9);
        r0 = r8.contains(r0);
        if (r0 != 0) goto L_0x0508;
    L_0x0433:
        r0 = java.lang.Integer.valueOf(r9);
        r8.add(r0);
        goto L_0x0508;
    L_0x043c:
        r0 = r6.channelsPts;
        r1 = r13.pts;
        r0.put(r9, r1);
        r0 = r23.getMessagesStorage();
        r1 = r13.pts;
        r0.saveChannelPts(r9, r1);
        goto L_0x0508;
    L_0x044e:
        r13 = r5;
        if (r1 == r3) goto L_0x0508;
    L_0x0451:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0484;
    L_0x0455:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r0);
        r0 = " need get channel diff, pts: ";
        r2.append(r0);
        r2.append(r1);
        r2.append(r15);
        r0 = r13.pts;
        r2.append(r0);
        r2.append(r11);
        r0 = r13.pts_count;
        r2.append(r0);
        r0 = " channelId = ";
        r2.append(r0);
        r2.append(r9);
        r0 = r2.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0484:
        r0 = r6.updatesStartWaitTimeChannels;
        r0 = r0.get(r9);
        r2 = r6.gettingDifferenceChannels;
        r2 = r2.get(r9);
        if (r2 != 0) goto L_0x04c2;
    L_0x0492:
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x04c2;
    L_0x0498:
        r2 = java.lang.System.currentTimeMillis();
        r2 = r2 - r0;
        r2 = java.lang.Math.abs(r2);
        r4 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r10 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r10 > 0) goto L_0x04a8;
    L_0x04a7:
        goto L_0x04c2;
    L_0x04a8:
        if (r8 != 0) goto L_0x04b0;
    L_0x04aa:
        r8 = new java.util.ArrayList;
        r8.<init>();
        goto L_0x0508;
    L_0x04b0:
        r0 = java.lang.Integer.valueOf(r9);
        r0 = r8.contains(r0);
        if (r0 != 0) goto L_0x0508;
    L_0x04ba:
        r0 = java.lang.Integer.valueOf(r9);
        r8.add(r0);
        goto L_0x0508;
    L_0x04c2:
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 != 0) goto L_0x04d1;
    L_0x04c8:
        r0 = r6.updatesStartWaitTimeChannels;
        r1 = java.lang.System.currentTimeMillis();
        r0.put(r9, r1);
    L_0x04d1:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x04d8;
    L_0x04d5:
        org.telegram.messenger.FileLog.d(r12);
    L_0x04d8:
        r0 = r6.updatesQueueChannels;
        r0 = r0.get(r9);
        r0 = (java.util.ArrayList) r0;
        if (r0 != 0) goto L_0x04ec;
    L_0x04e2:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = r6.updatesQueueChannels;
        r1.put(r9, r0);
    L_0x04ec:
        r0.add(r13);
        goto L_0x0508;
    L_0x04f0:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0508;
    L_0x04f4:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "need load unknown channel = ";
        r0.append(r1);
        r0.append(r9);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0508:
        r0 = r7.updates;
        r1 = 0;
        r0.remove(r1);
        r3 = r22;
        r9 = 0;
        r10 = 1;
        goto L_0x0127;
    L_0x0514:
        r22 = r3;
    L_0x0516:
        if (r22 == 0) goto L_0x0537;
    L_0x0518:
        r0 = r23.getMessagesStorage();
        r0 = r0.getLastSeqValue();
        r1 = 1;
        r0 = r0 + r1;
        r1 = r7.seq_start;
        if (r0 == r1) goto L_0x0535;
    L_0x0526:
        r0 = r23.getMessagesStorage();
        r0 = r0.getLastSeqValue();
        r1 = r7.seq_start;
        if (r0 != r1) goto L_0x0533;
    L_0x0532:
        goto L_0x0535;
    L_0x0533:
        r0 = 0;
        goto L_0x0552;
    L_0x0535:
        r0 = 1;
        goto L_0x0552;
    L_0x0537:
        r0 = r23.getMessagesStorage();
        r0 = r0.getLastSeqValue();
        r1 = 1;
        r0 = r0 + r1;
        r1 = r7.seq;
        if (r0 == r1) goto L_0x0535;
    L_0x0545:
        if (r1 == 0) goto L_0x0535;
    L_0x0547:
        r0 = r23.getMessagesStorage();
        r0 = r0.getLastSeqValue();
        if (r1 != r0) goto L_0x0533;
    L_0x0551:
        goto L_0x0535;
    L_0x0552:
        if (r0 == 0) goto L_0x057e;
    L_0x0554:
        r1 = r7.updates;
        r2 = r7.users;
        r3 = r7.chats;
        r4 = 0;
        r5 = r7.date;
        r0 = r23;
        r0.processUpdateArray(r1, r2, r3, r4, r5);
        r0 = r7.seq;
        if (r0 == 0) goto L_0x0609;
    L_0x0566:
        r0 = r7.date;
        if (r0 == 0) goto L_0x0573;
    L_0x056a:
        r0 = r23.getMessagesStorage();
        r1 = r7.date;
        r0.setLastDateValue(r1);
    L_0x0573:
        r0 = r23.getMessagesStorage();
        r1 = r7.seq;
        r0.setLastSeqValue(r1);
        goto L_0x0609;
    L_0x057e:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x05cd;
    L_0x0582:
        if (r22 == 0) goto L_0x05a9;
    L_0x0584:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "need get diff TL_updatesCombined, seq: ";
        r0.append(r1);
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastSeqValue();
        r0.append(r1);
        r0.append(r15);
        r1 = r7.seq_start;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x05cd;
    L_0x05a9:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "need get diff TL_updates, seq: ";
        r0.append(r1);
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastSeqValue();
        r0.append(r1);
        r0.append(r15);
        r1 = r7.seq;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x05cd:
        r0 = r6.gettingDifference;
        if (r0 != 0) goto L_0x05ed;
    L_0x05d1:
        r0 = r6.updatesStartWaitTimeSeq;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x05ed;
    L_0x05d9:
        r0 = java.lang.System.currentTimeMillis();
        r2 = r6.updatesStartWaitTimeSeq;
        r0 = r0 - r2;
        r0 = java.lang.Math.abs(r0);
        r2 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 > 0) goto L_0x05eb;
    L_0x05ea:
        goto L_0x05ed;
    L_0x05eb:
        r9 = 1;
        goto L_0x060f;
    L_0x05ed:
        r0 = r6.updatesStartWaitTimeSeq;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 != 0) goto L_0x05fb;
    L_0x05f5:
        r0 = java.lang.System.currentTimeMillis();
        r6.updatesStartWaitTimeSeq = r0;
    L_0x05fb:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0604;
    L_0x05ff:
        r0 = "add TL_updates/Combined to queue";
        org.telegram.messenger.FileLog.d(r0);
    L_0x0604:
        r0 = r6.updatesQueueSeq;
        r0.add(r7);
    L_0x0609:
        r9 = r18;
        goto L_0x060f;
    L_0x060c:
        r9 = r0;
        r19 = 0;
    L_0x060f:
        r0 = 0;
        goto L_0x0939;
    L_0x0612:
        if (r0 == 0) goto L_0x0617;
    L_0x0614:
        r0 = r7.from_id;
        goto L_0x0619;
    L_0x0617:
        r0 = r7.user_id;
    L_0x0619:
        r1 = java.lang.Integer.valueOf(r0);
        r1 = r6.getUser(r1);
        if (r1 == 0) goto L_0x0627;
    L_0x0623:
        r2 = r1.min;
        if (r2 == 0) goto L_0x063a;
    L_0x0627:
        r1 = r23.getMessagesStorage();
        r1 = r1.getUserSync(r0);
        if (r1 == 0) goto L_0x0636;
    L_0x0631:
        r2 = r1.min;
        if (r2 == 0) goto L_0x0636;
    L_0x0635:
        r1 = r8;
    L_0x0636:
        r2 = 1;
        r6.putUser(r1, r2);
    L_0x063a:
        r2 = r7.fwd_from;
        if (r2 == 0) goto L_0x0684;
    L_0x063e:
        r2 = r2.from_id;
        if (r2 == 0) goto L_0x065e;
    L_0x0642:
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r6.getUser(r2);
        if (r2 != 0) goto L_0x065c;
    L_0x064c:
        r2 = r23.getMessagesStorage();
        r3 = r7.fwd_from;
        r3 = r3.from_id;
        r2 = r2.getUserSync(r3);
        r3 = 1;
        r6.putUser(r2, r3);
    L_0x065c:
        r9 = 1;
        goto L_0x0660;
    L_0x065e:
        r2 = r8;
        r9 = 0;
    L_0x0660:
        r3 = r7.fwd_from;
        r3 = r3.channel_id;
        if (r3 == 0) goto L_0x0682;
    L_0x0666:
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r6.getChat(r3);
        if (r3 != 0) goto L_0x0680;
    L_0x0670:
        r3 = r23.getMessagesStorage();
        r4 = r7.fwd_from;
        r4 = r4.channel_id;
        r3 = r3.getChatSync(r4);
        r4 = 1;
        r6.putChat(r3, r4);
    L_0x0680:
        r9 = 1;
        goto L_0x0687;
    L_0x0682:
        r3 = r8;
        goto L_0x0687;
    L_0x0684:
        r2 = r8;
        r3 = r2;
        r9 = 0;
    L_0x0687:
        r4 = r7.via_bot_id;
        if (r4 == 0) goto L_0x06a6;
    L_0x068b:
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r6.getUser(r4);
        if (r4 != 0) goto L_0x06a3;
    L_0x0695:
        r4 = r23.getMessagesStorage();
        r5 = r7.via_bot_id;
        r4 = r4.getUserSync(r5);
        r5 = 1;
        r6.putUser(r4, r5);
    L_0x06a3:
        r5 = r4;
        r4 = 1;
        goto L_0x06a8;
    L_0x06a6:
        r5 = r8;
        r4 = 0;
    L_0x06a8:
        r10 = r7 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r10 == 0) goto L_0x06bd;
    L_0x06ac:
        if (r1 == 0) goto L_0x06bb;
    L_0x06ae:
        if (r9 == 0) goto L_0x06b4;
    L_0x06b0:
        if (r2 != 0) goto L_0x06b4;
    L_0x06b2:
        if (r3 == 0) goto L_0x06bb;
    L_0x06b4:
        if (r4 == 0) goto L_0x06b9;
    L_0x06b6:
        if (r5 != 0) goto L_0x06b9;
    L_0x06b8:
        goto L_0x06bb;
    L_0x06b9:
        r2 = 0;
        goto L_0x06e6;
    L_0x06bb:
        r2 = 1;
        goto L_0x06e6;
    L_0x06bd:
        r13 = r7.chat_id;
        r13 = java.lang.Integer.valueOf(r13);
        r13 = r6.getChat(r13);
        if (r13 != 0) goto L_0x06d7;
    L_0x06c9:
        r13 = r23.getMessagesStorage();
        r14 = r7.chat_id;
        r13 = r13.getChatSync(r14);
        r14 = 1;
        r6.putChat(r13, r14);
    L_0x06d7:
        if (r13 == 0) goto L_0x06bb;
    L_0x06d9:
        if (r1 == 0) goto L_0x06bb;
    L_0x06db:
        if (r9 == 0) goto L_0x06e1;
    L_0x06dd:
        if (r2 != 0) goto L_0x06e1;
    L_0x06df:
        if (r3 == 0) goto L_0x06bb;
    L_0x06e1:
        if (r4 == 0) goto L_0x06b9;
    L_0x06e3:
        if (r5 != 0) goto L_0x06b9;
    L_0x06e5:
        goto L_0x06bb;
    L_0x06e6:
        if (r2 != 0) goto L_0x0731;
    L_0x06e8:
        r3 = r7.entities;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x0731;
    L_0x06f0:
        r3 = 0;
    L_0x06f1:
        r4 = r7.entities;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x0731;
    L_0x06f9:
        r4 = r7.entities;
        r4 = r4.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.MessageEntity) r4;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r5 == 0) goto L_0x072e;
    L_0x0705:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r4;
        r4 = r4.user_id;
        r5 = java.lang.Integer.valueOf(r4);
        r5 = r6.getUser(r5);
        if (r5 == 0) goto L_0x0717;
    L_0x0713:
        r5 = r5.min;
        if (r5 == 0) goto L_0x072e;
    L_0x0717:
        r5 = r23.getMessagesStorage();
        r4 = r5.getUserSync(r4);
        if (r4 == 0) goto L_0x0726;
    L_0x0721:
        r5 = r4.min;
        if (r5 == 0) goto L_0x0726;
    L_0x0725:
        r4 = r8;
    L_0x0726:
        if (r4 != 0) goto L_0x072a;
    L_0x0728:
        r2 = 1;
        goto L_0x0731;
    L_0x072a:
        r4 = 1;
        r6.putUser(r1, r4);
    L_0x072e:
        r3 = r3 + 1;
        goto L_0x06f1;
    L_0x0731:
        r3 = r7.out;
        if (r3 != 0) goto L_0x0765;
    L_0x0735:
        if (r1 == 0) goto L_0x0765;
    L_0x0737:
        r3 = r1.status;
        if (r3 == 0) goto L_0x0765;
    L_0x073b:
        r3 = r3.expires;
        if (r3 > 0) goto L_0x0765;
    L_0x073f:
        r3 = r23.getConnectionsManager();
        r3 = r3.getCurrentTime();
        r4 = r7.date;
        r3 = r3 - r4;
        r3 = java.lang.Math.abs(r3);
        r4 = 30;
        if (r3 >= r4) goto L_0x0765;
    L_0x0752:
        r3 = r6.onlinePrivacy;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r4 = r7.date;
        r4 = java.lang.Integer.valueOf(r4);
        r3.put(r1, r4);
        r9 = 1;
        goto L_0x0766;
    L_0x0765:
        r9 = 0;
    L_0x0766:
        if (r2 == 0) goto L_0x076b;
    L_0x0768:
        r2 = 1;
        goto L_0x0935;
    L_0x076b:
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastPtsValue();
        r2 = r7.pts_count;
        r1 = r1 + r2;
        r2 = r7.pts;
        if (r1 != r2) goto L_0x08c0;
    L_0x077a:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;
        r1.<init>();
        r2 = r7.id;
        r1.id = r2;
        r2 = r23.getUserConfig();
        r2 = r2.getClientUserId();
        if (r10 == 0) goto L_0x07a5;
    L_0x078d:
        r3 = r7.out;
        if (r3 == 0) goto L_0x0794;
    L_0x0791:
        r1.from_id = r2;
        goto L_0x0796;
    L_0x0794:
        r1.from_id = r0;
    L_0x0796:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r3.<init>();
        r1.to_id = r3;
        r3 = r1.to_id;
        r3.user_id = r0;
        r3 = (long) r0;
        r1.dialog_id = r3;
        goto L_0x07b8;
    L_0x07a5:
        r1.from_id = r0;
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat;
        r3.<init>();
        r1.to_id = r3;
        r3 = r1.to_id;
        r4 = r7.chat_id;
        r3.chat_id = r4;
        r3 = -r4;
        r3 = (long) r3;
        r1.dialog_id = r3;
    L_0x07b8:
        r3 = r7.fwd_from;
        r1.fwd_from = r3;
        r3 = r7.silent;
        r1.silent = r3;
        r3 = r7.out;
        r1.out = r3;
        r3 = r7.mentioned;
        r1.mentioned = r3;
        r3 = r7.media_unread;
        r1.media_unread = r3;
        r3 = r7.entities;
        r1.entities = r3;
        r3 = r7.message;
        r1.message = r3;
        r3 = r7.date;
        r1.date = r3;
        r3 = r7.via_bot_id;
        r1.via_bot_id = r3;
        r3 = r7.flags;
        r3 = r3 | 256;
        r1.flags = r3;
        r3 = r7.reply_to_msg_id;
        r1.reply_to_msg_id = r3;
        r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r3.<init>();
        r1.media = r3;
        r3 = r1.out;
        if (r3 == 0) goto L_0x07f4;
    L_0x07f1:
        r3 = r6.dialogs_read_outbox_max;
        goto L_0x07f6;
    L_0x07f4:
        r3 = r6.dialogs_read_inbox_max;
    L_0x07f6:
        r4 = r1.dialog_id;
        r4 = java.lang.Long.valueOf(r4);
        r4 = r3.get(r4);
        r4 = (java.lang.Integer) r4;
        if (r4 != 0) goto L_0x081d;
    L_0x0804:
        r4 = r23.getMessagesStorage();
        r5 = r1.out;
        r11 = r1.dialog_id;
        r4 = r4.getDialogReadMax(r5, r11);
        r4 = java.lang.Integer.valueOf(r4);
        r11 = r1.dialog_id;
        r5 = java.lang.Long.valueOf(r11);
        r3.put(r5, r4);
    L_0x081d:
        r3 = r4.intValue();
        r4 = r1.id;
        if (r3 >= r4) goto L_0x0827;
    L_0x0825:
        r3 = 1;
        goto L_0x0828;
    L_0x0827:
        r3 = 0;
    L_0x0828:
        r1.unread = r3;
        r3 = r1.dialog_id;
        r11 = (long) r2;
        r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r2 != 0) goto L_0x083a;
    L_0x0831:
        r2 = 0;
        r1.unread = r2;
        r1.media_unread = r2;
        r2 = 1;
        r1.out = r2;
        goto L_0x083b;
    L_0x083a:
        r2 = 1;
    L_0x083b:
        r3 = r23.getMessagesStorage();
        r4 = r7.pts;
        r3.setLastPtsValue(r4);
        r3 = new org.telegram.messenger.MessageObject;
        r4 = r6.currentAccount;
        r5 = r6.createdDialogIds;
        r11 = r1.dialog_id;
        r11 = java.lang.Long.valueOf(r11);
        r5 = r5.contains(r11);
        r3.<init>(r4, r1, r5);
        r4 = new java.util.ArrayList;
        r4.<init>();
        r4.add(r3);
        r12 = new java.util.ArrayList;
        r12.<init>();
        r12.add(r1);
        if (r10 == 0) goto L_0x0886;
    L_0x0869:
        r1 = r7.out;
        if (r1 != 0) goto L_0x0877;
    L_0x086d:
        r1 = r7.user_id;
        r10 = (long) r1;
        r1 = r6.updatePrintingUsersWithNewMessages(r10, r4);
        if (r1 == 0) goto L_0x0877;
    L_0x0876:
        goto L_0x0878;
    L_0x0877:
        r2 = 0;
    L_0x0878:
        if (r2 == 0) goto L_0x087d;
    L_0x087a:
        r23.updatePrintingStrings();
    L_0x087d:
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$eEjbksoOfkx6xTQV2_OzG5pnRpU;
        r1.<init>(r6, r2, r0, r4);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
        goto L_0x089b;
    L_0x0886:
        r0 = r7.chat_id;
        r0 = -r0;
        r0 = (long) r0;
        r0 = r6.updatePrintingUsersWithNewMessages(r0, r4);
        if (r0 == 0) goto L_0x0893;
    L_0x0890:
        r23.updatePrintingStrings();
    L_0x0893:
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$YKPZ7XgAhwef5KnSIvIkaDgL_A0;
        r1.<init>(r6, r0, r7, r4);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x089b:
        r0 = r3.isOut();
        if (r0 != 0) goto L_0x08b1;
    L_0x08a1:
        r0 = r23.getMessagesStorage();
        r0 = r0.getStorageQueue();
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$W3uVVOb0uoxODPQw9DMQq3gOXyM;
        r1.<init>(r6, r4);
        r0.postRunnable(r1);
    L_0x08b1:
        r11 = r23.getMessagesStorage();
        r13 = 0;
        r14 = 1;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r11.putMessages(r12, r13, r14, r15, r16, r17);
        goto L_0x0934;
    L_0x08c0:
        r2 = 1;
        r0 = r23.getMessagesStorage();
        r0 = r0.getLastPtsValue();
        r1 = r7.pts;
        if (r0 == r1) goto L_0x0934;
    L_0x08cd:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x08fd;
    L_0x08d1:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "need get diff short message, pts: ";
        r0.append(r1);
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastPtsValue();
        r0.append(r1);
        r0.append(r15);
        r1 = r7.pts;
        r0.append(r1);
        r0.append(r11);
        r1 = r7.pts_count;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x08fd:
        r0 = r6.gettingDifference;
        if (r0 != 0) goto L_0x091a;
    L_0x0901:
        r0 = r6.updatesStartWaitTimePts;
        r3 = 0;
        r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x091a;
    L_0x0909:
        r0 = java.lang.System.currentTimeMillis();
        r3 = r6.updatesStartWaitTimePts;
        r0 = r0 - r3;
        r0 = java.lang.Math.abs(r0);
        r3 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r5 > 0) goto L_0x0935;
    L_0x091a:
        r0 = r6.updatesStartWaitTimePts;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 != 0) goto L_0x0928;
    L_0x0922:
        r0 = java.lang.System.currentTimeMillis();
        r6.updatesStartWaitTimePts = r0;
    L_0x0928:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x092f;
    L_0x092c:
        org.telegram.messenger.FileLog.d(r12);
    L_0x092f:
        r0 = r6.updatesQueuePts;
        r0.add(r7);
    L_0x0934:
        r2 = 0;
    L_0x0935:
        r0 = r9;
        r19 = 0;
        r9 = r2;
    L_0x0939:
        r1 = r23.getSecretChatHelper();
        r1.processPendingEncMessages();
        if (r25 != 0) goto L_0x097a;
    L_0x0942:
        r1 = 0;
    L_0x0943:
        r2 = r6.updatesQueueChannels;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x0969;
    L_0x094b:
        r2 = r6.updatesQueueChannels;
        r2 = r2.keyAt(r1);
        if (r8 == 0) goto L_0x0962;
    L_0x0953:
        r3 = java.lang.Integer.valueOf(r2);
        r3 = r8.contains(r3);
        if (r3 == 0) goto L_0x0962;
    L_0x095d:
        r6.getChannelDifference(r2);
        r3 = 0;
        goto L_0x0966;
    L_0x0962:
        r3 = 0;
        r6.processChannelsUpdatesQueue(r2, r3);
    L_0x0966:
        r1 = r1 + 1;
        goto L_0x0943;
    L_0x0969:
        r3 = 0;
        if (r9 == 0) goto L_0x0970;
    L_0x096c:
        r23.getDifference();
        goto L_0x097a;
    L_0x0970:
        r1 = 0;
    L_0x0971:
        r2 = 3;
        if (r1 >= r2) goto L_0x097a;
    L_0x0974:
        r6.processUpdatesQueue(r1, r3);
        r1 = r1 + 1;
        goto L_0x0971;
    L_0x097a:
        if (r19 == 0) goto L_0x0994;
    L_0x097c:
        r1 = new org.telegram.tgnet.TLRPC$TL_messages_receivedQueue;
        r1.<init>();
        r2 = r23.getMessagesStorage();
        r2 = r2.getLastQtsValue();
        r1.max_qts = r2;
        r2 = r23.getConnectionsManager();
        r3 = org.telegram.messenger.-$$Lambda$MessagesController$41Nrry903aXeWspe_mkLTT84RF4.INSTANCE;
        r2.sendRequest(r1, r3);
    L_0x0994:
        if (r0 == 0) goto L_0x099e;
    L_0x0996:
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$guZRpI4v5zfmsZAbWkFbheWJ3nw;
        r0.<init>(r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x099e:
        r0 = r23.getMessagesStorage();
        r1 = r23.getMessagesStorage();
        r1 = r1.getLastSeqValue();
        r2 = r23.getMessagesStorage();
        r2 = r2.getLastPtsValue();
        r3 = r23.getMessagesStorage();
        r3 = r3.getLastDateValue();
        r4 = r23.getMessagesStorage();
        r4 = r4.getLastQtsValue();
        r0.saveDiffParams(r1, r2, r3, r4);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    public /* synthetic */ void lambda$processUpdates$250$MessagesController(boolean z, int i, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
        }
        updateInterfaceWithMessages((long) i, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdates$251$MessagesController(boolean z, Updates updates, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
        }
        updateInterfaceWithMessages((long) (-updates.chat_id), arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$252$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$processUpdates$253$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$MBeO8uG9BPtJw2AvXmlkxninBBk(this, arrayList));
    }

    public /* synthetic */ void lambda$processUpdates$255$MessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    /* JADX WARNING: Removed duplicated region for block: B:762:0x10ed  */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x1062  */
    /* JADX WARNING: Removed duplicated region for block: B:840:0x128f  */
    /* JADX WARNING: Removed duplicated region for block: B:836:0x1282  */
    /* JADX WARNING: Removed duplicated region for block: B:844:0x12b0  */
    /* JADX WARNING: Removed duplicated region for block: B:843:0x12ad  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x12be  */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x12b8  */
    /* JADX WARNING: Removed duplicated region for block: B:852:0x12c9  */
    /* JADX WARNING: Removed duplicated region for block: B:861:0x12f0  */
    /* JADX WARNING: Removed duplicated region for block: B:860:0x12ea  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x1000  */
    /* JADX WARNING: Removed duplicated region for block: B:692:0x0fdd  */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x1003  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x110e  */
    /* JADX WARNING: Removed duplicated region for block: B:786:0x117f  */
    /* JADX WARNING: Removed duplicated region for block: B:785:0x117a  */
    /* JADX WARNING: Removed duplicated region for block: B:799:0x11b3  */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x121c  */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x11c5  */
    /* JADX WARNING: Removed duplicated region for block: B:662:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0c1a  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0CLASSNAME  */
    /* JADX WARNING: Missing block: B:903:0x1474, code skipped:
            if (r5 != null) goto L_0x1479;
     */
    public boolean processUpdateArray(java.util.ArrayList<org.telegram.tgnet.TLRPC.Update> r55, java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r56, java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat> r57, boolean r58, int r59) {
        /*
        r54 = this;
        r13 = r54;
        r0 = r56;
        r1 = r57;
        r2 = r55.isEmpty();
        r14 = 1;
        if (r2 == 0) goto L_0x001a;
    L_0x000d:
        if (r0 != 0) goto L_0x0011;
    L_0x000f:
        if (r1 == 0) goto L_0x0019;
    L_0x0011:
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$96FGA5dNZDEhfxY_JPUPryeGAxo;
        r2.<init>(r13, r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x0019:
        return r14;
    L_0x001a:
        r2 = java.lang.System.currentTimeMillis();
        if (r0 == 0) goto L_0x0041;
    L_0x0020:
        r4 = new java.util.concurrent.ConcurrentHashMap;
        r4.<init>();
        r5 = r56.size();
        r6 = 0;
    L_0x002a:
        if (r6 >= r5) goto L_0x003e;
    L_0x002c:
        r7 = r0.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
        r8 = r7.id;
        r8 = java.lang.Integer.valueOf(r8);
        r4.put(r8, r7);
        r6 = r6 + 1;
        goto L_0x002a;
    L_0x003e:
        r11 = r4;
        r4 = 1;
        goto L_0x0045;
    L_0x0041:
        r4 = r13.users;
        r11 = r4;
        r4 = 0;
    L_0x0045:
        if (r1 == 0) goto L_0x0067;
    L_0x0047:
        r5 = new java.util.concurrent.ConcurrentHashMap;
        r5.<init>();
        r6 = r57.size();
        r7 = 0;
    L_0x0051:
        if (r7 >= r6) goto L_0x0065;
    L_0x0053:
        r8 = r1.get(r7);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        r9 = r8.id;
        r9 = java.lang.Integer.valueOf(r9);
        r5.put(r9, r8);
        r7 = r7 + 1;
        goto L_0x0051;
    L_0x0065:
        r12 = r5;
        goto L_0x006b;
    L_0x0067:
        r4 = r13.chats;
        r12 = r4;
        r4 = 0;
    L_0x006b:
        if (r58 == 0) goto L_0x006e;
    L_0x006d:
        r4 = 0;
    L_0x006e:
        if (r0 != 0) goto L_0x0072;
    L_0x0070:
        if (r1 == 0) goto L_0x007a;
    L_0x0072:
        r5 = new org.telegram.messenger.-$$Lambda$MessagesController$Rnq16UxMC9aNdrPpQ47iT5M3qV0;
        r5.<init>(r13, r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x007a:
        r0 = r55.size();
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r14 = 0;
        r16 = 0;
        r18 = 0;
        r24 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = 0;
        r32 = 0;
        r33 = 0;
        r34 = 0;
        r35 = 0;
        r36 = 0;
        r37 = 0;
        r38 = 0;
    L_0x00a5:
        if (r10 >= r0) goto L_0x131c;
    L_0x00a7:
        r1 = r55;
        r17 = r1.get(r10);
        r15 = r17;
        r15 = (org.telegram.tgnet.TLRPC.Update) r15;
        r17 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        r57 = r0;
        if (r17 == 0) goto L_0x00cb;
    L_0x00b7:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "process update ";
        r0.append(r1);
        r0.append(r15);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x00cb:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage;
        r20 = r2;
        r3 = " channelId = ";
        if (r0 != 0) goto L_0x0f5c;
    L_0x00d3:
        r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r1 != 0) goto L_0x0f5c;
    L_0x00d7:
        r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
        if (r1 == 0) goto L_0x00dd;
    L_0x00db:
        goto L_0x0f5c;
    L_0x00dd:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents;
        if (r0 == 0) goto L_0x0112;
    L_0x00e1:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents) r15;
        if (r5 != 0) goto L_0x00ea;
    L_0x00e5:
        r5 = new java.util.ArrayList;
        r5.<init>();
    L_0x00ea:
        r0 = r15.messages;
        r0 = r0.size();
        r1 = 0;
    L_0x00f1:
        if (r1 >= r0) goto L_0x010a;
    L_0x00f3:
        r2 = r15.messages;
        r2 = r2.get(r1);
        r2 = (java.lang.Integer) r2;
        r2 = r2.intValue();
        r2 = (long) r2;
        r2 = java.lang.Long.valueOf(r2);
        r5.add(r2);
        r1 = r1 + 1;
        goto L_0x00f1;
    L_0x010a:
        r25 = r4;
    L_0x010c:
        r43 = r10;
        r4 = r11;
    L_0x010f:
        r3 = 0;
        goto L_0x1311;
    L_0x0112:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents;
        if (r0 == 0) goto L_0x014d;
    L_0x0116:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents) r15;
        if (r5 != 0) goto L_0x011f;
    L_0x011a:
        r5 = new java.util.ArrayList;
        r5.<init>();
    L_0x011f:
        r0 = r15.messages;
        r0 = r0.size();
        r2 = 0;
    L_0x0126:
        if (r2 >= r0) goto L_0x010a;
    L_0x0128:
        r3 = r15.messages;
        r3 = r3.get(r2);
        r3 = (java.lang.Integer) r3;
        r3 = r3.intValue();
        r17 = r2;
        r1 = (long) r3;
        r3 = r15.channel_id;
        r25 = r4;
        r3 = (long) r3;
        r19 = 32;
        r3 = r3 << r19;
        r1 = r1 | r3;
        r1 = java.lang.Long.valueOf(r1);
        r5.add(r1);
        r2 = r17 + 1;
        r4 = r25;
        goto L_0x0126;
    L_0x014d:
        r25 = r4;
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
        if (r0 == 0) goto L_0x01b0;
    L_0x0153:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox) r15;
        if (r14 != 0) goto L_0x015c;
    L_0x0157:
        r14 = new org.telegram.messenger.support.SparseLongArray;
        r14.<init>();
    L_0x015c:
        r0 = r15.peer;
        r1 = r0.chat_id;
        if (r1 == 0) goto L_0x016f;
    L_0x0162:
        r0 = -r1;
        r1 = r15.max_id;
        r1 = (long) r1;
        r14.put(r0, r1);
        r0 = r15.peer;
        r0 = r0.chat_id;
        r0 = -r0;
        goto L_0x017b;
    L_0x016f:
        r0 = r0.user_id;
        r1 = r15.max_id;
        r1 = (long) r1;
        r14.put(r0, r1);
        r0 = r15.peer;
        r0 = r0.user_id;
    L_0x017b:
        r0 = (long) r0;
        r2 = r13.dialogs_read_inbox_max;
        r3 = java.lang.Long.valueOf(r0);
        r2 = r2.get(r3);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x0197;
    L_0x018a:
        r2 = r54.getMessagesStorage();
        r3 = 0;
        r2 = r2.getDialogReadMax(r3, r0);
        r2 = java.lang.Integer.valueOf(r2);
    L_0x0197:
        r3 = r13.dialogs_read_inbox_max;
        r0 = java.lang.Long.valueOf(r0);
        r1 = r2.intValue();
        r2 = r15.max_id;
        r1 = java.lang.Math.max(r1, r2);
        r1 = java.lang.Integer.valueOf(r1);
        r3.put(r0, r1);
        goto L_0x010c;
    L_0x01b0:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
        if (r0 == 0) goto L_0x0214;
    L_0x01b4:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox) r15;
        r4 = r27;
        if (r4 != 0) goto L_0x01c1;
    L_0x01ba:
        r27 = new org.telegram.messenger.support.SparseLongArray;
        r27.<init>();
        r4 = r27;
    L_0x01c1:
        r0 = r15.peer;
        r1 = r0.chat_id;
        if (r1 == 0) goto L_0x01d4;
    L_0x01c7:
        r0 = -r1;
        r1 = r15.max_id;
        r1 = (long) r1;
        r4.put(r0, r1);
        r0 = r15.peer;
        r0 = r0.chat_id;
        r0 = -r0;
        goto L_0x01e0;
    L_0x01d4:
        r0 = r0.user_id;
        r1 = r15.max_id;
        r1 = (long) r1;
        r4.put(r0, r1);
        r0 = r15.peer;
        r0 = r0.user_id;
    L_0x01e0:
        r0 = (long) r0;
        r2 = r13.dialogs_read_outbox_max;
        r3 = java.lang.Long.valueOf(r0);
        r2 = r2.get(r3);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x01fc;
    L_0x01ef:
        r2 = r54.getMessagesStorage();
        r3 = 1;
        r2 = r2.getDialogReadMax(r3, r0);
        r2 = java.lang.Integer.valueOf(r2);
    L_0x01fc:
        r3 = r13.dialogs_read_outbox_max;
        r0 = java.lang.Long.valueOf(r0);
        r1 = r2.intValue();
        r2 = r15.max_id;
        r1 = java.lang.Math.max(r1, r2);
        r1 = java.lang.Integer.valueOf(r1);
        r3.put(r0, r1);
        goto L_0x023f;
    L_0x0214:
        r4 = r27;
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
        if (r0 == 0) goto L_0x0243;
    L_0x021a:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateDeleteMessages) r15;
        r2 = r32;
        if (r2 != 0) goto L_0x0227;
    L_0x0220:
        r32 = new android.util.SparseArray;
        r32.<init>();
        r2 = r32;
    L_0x0227:
        r0 = 0;
        r1 = r2.get(r0);
        r1 = (java.util.ArrayList) r1;
        if (r1 != 0) goto L_0x0238;
    L_0x0230:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2.put(r0, r1);
    L_0x0238:
        r0 = r15.messages;
        r1.addAll(r0);
    L_0x023d:
        r32 = r2;
    L_0x023f:
        r27 = r4;
        goto L_0x010c;
    L_0x0243:
        r2 = r32;
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteScheduledMessages;
        if (r0 == 0) goto L_0x027f;
    L_0x0249:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateDeleteScheduledMessages) r15;
        r1 = r33;
        if (r1 != 0) goto L_0x0256;
    L_0x024f:
        r33 = new android.util.SparseArray;
        r33.<init>();
        r1 = r33;
    L_0x0256:
        r0 = r15.peer;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r3 == 0) goto L_0x0265;
    L_0x025c:
        r0 = r0.channel_id;
        r3 = r1.get(r0);
        r3 = (java.util.ArrayList) r3;
        goto L_0x026d;
    L_0x0265:
        r0 = 0;
        r3 = r1.get(r0);
        r3 = (java.util.ArrayList) r3;
        r0 = 0;
    L_0x026d:
        if (r3 != 0) goto L_0x0277;
    L_0x026f:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r1.put(r0, r3);
    L_0x0277:
        r0 = r15.messages;
        r3.addAll(r0);
        r33 = r1;
        goto L_0x023d;
    L_0x027f:
        r1 = r33;
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserTyping;
        r27 = r1;
        if (r0 != 0) goto L_0x0e35;
    L_0x0287:
        r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatUserTyping;
        if (r1 == 0) goto L_0x028d;
    L_0x028b:
        goto L_0x0e35;
    L_0x028d:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipants;
        if (r0 == 0) goto L_0x02b1;
    L_0x0291:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipants) r15;
        r8 = r8 | 32;
        if (r35 != 0) goto L_0x029c;
    L_0x0297:
        r35 = new java.util.ArrayList;
        r35.<init>();
    L_0x029c:
        r0 = r35;
        r1 = r15.participants;
        r0.add(r1);
        r35 = r0;
    L_0x02a5:
        r32 = r2;
        r43 = r10;
        r33 = r27;
        r3 = 0;
    L_0x02ac:
        r27 = r4;
        r4 = r11;
        goto L_0x1311;
    L_0x02b1:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserStatus;
        if (r0 == 0) goto L_0x02c6;
    L_0x02b5:
        r8 = r8 | 4;
        if (r36 != 0) goto L_0x02be;
    L_0x02b9:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x02be:
        r0 = r36;
        r0.add(r15);
    L_0x02c3:
        r36 = r0;
        goto L_0x02a5;
    L_0x02c6:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r0 == 0) goto L_0x02d9;
    L_0x02ca:
        r8 = r8 | 1;
        if (r36 != 0) goto L_0x02d3;
    L_0x02ce:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x02d3:
        r0 = r36;
        r0.add(r15);
        goto L_0x02c3;
    L_0x02d9:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
        if (r0 == 0) goto L_0x02f8;
    L_0x02dd:
        r0 = r15;
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhoto) r0;
        r8 = r8 | 2;
        r1 = r54.getMessagesStorage();
        r0 = r0.user_id;
        r1.clearUserPhotos(r0);
        if (r36 != 0) goto L_0x02f2;
    L_0x02ed:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x02f2:
        r0 = r36;
        r0.add(r15);
        goto L_0x02c3;
    L_0x02f8:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhone;
        if (r0 == 0) goto L_0x030b;
    L_0x02fc:
        r8 = r8 | 1024;
        if (r36 != 0) goto L_0x0305;
    L_0x0300:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0305:
        r0 = r36;
        r0.add(r15);
        goto L_0x02c3;
    L_0x030b:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerSettings;
        if (r0 == 0) goto L_0x0397;
    L_0x030f:
        r0 = r15;
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePeerSettings) r0;
        if (r7 != 0) goto L_0x0319;
    L_0x0314:
        r7 = new java.util.ArrayList;
        r7.<init>();
    L_0x0319:
        r1 = r0.peer;
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerUser;
        if (r3 == 0) goto L_0x0389;
    L_0x031f:
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r11.get(r1);
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        if (r1 == 0) goto L_0x0389;
    L_0x032d:
        r1 = r1.contact;
        r3 = -1;
        if (r1 == 0) goto L_0x035e;
    L_0x0332:
        r1 = r0.peer;
        r1 = r1.user_id;
        r1 = -r1;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r7.indexOf(r1);
        if (r1 == r3) goto L_0x0344;
    L_0x0341:
        r7.remove(r1);
    L_0x0344:
        r1 = r0.peer;
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r7.contains(r1);
        if (r1 != 0) goto L_0x0389;
    L_0x0352:
        r0 = r0.peer;
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r7.add(r0);
        goto L_0x0389;
    L_0x035e:
        r1 = r0.peer;
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r7.indexOf(r1);
        if (r1 == r3) goto L_0x036f;
    L_0x036c:
        r7.remove(r1);
    L_0x036f:
        r1 = r0.peer;
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r7.contains(r1);
        if (r1 != 0) goto L_0x0389;
    L_0x037d:
        r0 = r0.peer;
        r0 = r0.user_id;
        r0 = -r0;
        r0 = java.lang.Integer.valueOf(r0);
        r7.add(r0);
    L_0x0389:
        if (r36 != 0) goto L_0x0390;
    L_0x038b:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0390:
        r0 = r36;
        r0.add(r15);
        goto L_0x02c3;
    L_0x0397:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
        if (r0 == 0) goto L_0x0472;
    L_0x039b:
        r0 = r54.getSecretChatHelper();
        r15 = (org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage) r15;
        r1 = r15.message;
        r0 = r0.decryptMessage(r1);
        if (r0 == 0) goto L_0x045b;
    L_0x03a9:
        r1 = r0.isEmpty();
        if (r1 != 0) goto L_0x045b;
    L_0x03af:
        r1 = r15.message;
        r1 = r1.chat_id;
        r33 = r5;
        r32 = r6;
        r5 = (long) r1;
        r1 = 32;
        r5 = r5 << r1;
        if (r9 != 0) goto L_0x03c2;
    L_0x03bd:
        r9 = new android.util.LongSparseArray;
        r9.<init>();
    L_0x03c2:
        r1 = r9;
        r3 = r1.get(r5);
        r3 = (java.util.ArrayList) r3;
        if (r3 != 0) goto L_0x03d3;
    L_0x03cb:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r1.put(r5, r3);
    L_0x03d3:
        r15 = r0.size();
        r9 = 0;
    L_0x03d8:
        if (r9 >= r15) goto L_0x044f;
    L_0x03da:
        r17 = r0.get(r9);
        r19 = r0;
        r0 = r17;
        r0 = (org.telegram.tgnet.TLRPC.Message) r0;
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r0);
        if (r24 != 0) goto L_0x03f5;
    L_0x03e9:
        r17 = new java.util.ArrayList;
        r17.<init>();
        r53 = r17;
        r17 = r1;
        r1 = r53;
        goto L_0x03f9;
    L_0x03f5:
        r17 = r1;
        r1 = r24;
    L_0x03f9:
        r1.add(r0);
        r22 = r1;
        r1 = new org.telegram.messenger.MessageObject;
        r23 = r7;
        r7 = r13.currentAccount;
        r40 = r8;
        r8 = r13.createdDialogIds;
        r39 = r9;
        r9 = java.lang.Long.valueOf(r5);
        r24 = r8.contains(r9);
        r41 = r5;
        r9 = r33;
        r5 = r1;
        r6 = r7;
        r8 = r23;
        r7 = r0;
        r0 = r8;
        r33 = r40;
        r8 = r11;
        r40 = r9;
        r23 = r39;
        r9 = r12;
        r43 = r10;
        r10 = r24;
        r5.<init>(r6, r7, r8, r9, r10);
        r3.add(r1);
        if (r32 != 0) goto L_0x0436;
    L_0x0430:
        r5 = new java.util.ArrayList;
        r5.<init>();
        goto L_0x0438;
    L_0x0436:
        r5 = r32;
    L_0x0438:
        r5.add(r1);
        r9 = r23 + 1;
        r7 = r0;
        r32 = r5;
        r1 = r17;
        r0 = r19;
        r24 = r22;
        r8 = r33;
        r33 = r40;
        r5 = r41;
        r10 = r43;
        goto L_0x03d8;
    L_0x044f:
        r17 = r1;
        r0 = r7;
        r43 = r10;
        r40 = r33;
        r33 = r8;
        r9 = r17;
        goto L_0x0464;
    L_0x045b:
        r40 = r5;
        r32 = r6;
        r0 = r7;
        r33 = r8;
        r43 = r10;
    L_0x0464:
        r7 = r0;
        r6 = r32;
        r8 = r33;
        r5 = r40;
    L_0x046b:
        r3 = 0;
        r32 = r2;
        r33 = r27;
        goto L_0x02ac;
    L_0x0472:
        r40 = r5;
        r32 = r6;
        r0 = r7;
        r33 = r8;
        r43 = r10;
        r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
        if (r1 == 0) goto L_0x0528;
    L_0x047f:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping) r15;
        r1 = r15.chat_id;
        r3 = 1;
        r1 = r13.getEncryptedChatDB(r1, r3);
        if (r1 == 0) goto L_0x0517;
    L_0x048a:
        r3 = r15.chat_id;
        r5 = (long) r3;
        r3 = 32;
        r5 = r5 << r3;
        r3 = r13.printingUsers;
        r7 = java.lang.Long.valueOf(r5);
        r3 = r3.get(r7);
        r3 = (java.util.ArrayList) r3;
        if (r3 != 0) goto L_0x04ac;
    L_0x049e:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r7 = r13.printingUsers;
        r5 = java.lang.Long.valueOf(r5);
        r7.put(r5, r3);
    L_0x04ac:
        r5 = r3.size();
        r6 = 0;
    L_0x04b1:
        if (r6 >= r5) goto L_0x04d7;
    L_0x04b3:
        r7 = r3.get(r6);
        r7 = (org.telegram.messenger.MessagesController.PrintingUser) r7;
        r8 = r7.userId;
        r10 = r1.user_id;
        if (r8 != r10) goto L_0x04ce;
    L_0x04bf:
        r41 = r9;
        r9 = r20;
        r7.lastTime = r9;
        r5 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction;
        r5.<init>();
        r7.action = r5;
        r5 = 1;
        goto L_0x04dc;
    L_0x04ce:
        r41 = r9;
        r9 = r20;
        r6 = r6 + 1;
        r9 = r41;
        goto L_0x04b1;
    L_0x04d7:
        r41 = r9;
        r9 = r20;
        r5 = 0;
    L_0x04dc:
        if (r5 != 0) goto L_0x04f5;
    L_0x04de:
        r5 = new org.telegram.messenger.MessagesController$PrintingUser;
        r5.<init>();
        r6 = r1.user_id;
        r5.userId = r6;
        r5.lastTime = r9;
        r6 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction;
        r6.<init>();
        r5.action = r6;
        r3.add(r5);
        r16 = 1;
    L_0x04f5:
        r3 = r54.getConnectionsManager();
        r3 = r3.getCurrentTime();
        r3 = r3 - r59;
        r3 = java.lang.Math.abs(r3);
        r5 = 30;
        if (r3 >= r5) goto L_0x051b;
    L_0x0507:
        r3 = r13.onlinePrivacy;
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r5 = java.lang.Integer.valueOf(r59);
        r3.put(r1, r5);
        goto L_0x051b;
    L_0x0517:
        r41 = r9;
        r9 = r20;
    L_0x051b:
        r7 = r0;
    L_0x051c:
        r20 = r9;
        r6 = r32;
        r8 = r33;
        r5 = r40;
        r9 = r41;
        goto L_0x046b;
    L_0x0528:
        r41 = r9;
        r9 = r20;
        r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
        if (r1 == 0) goto L_0x0558;
    L_0x0530:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r15;
        r1 = r31;
        if (r1 != 0) goto L_0x053d;
    L_0x0536:
        r31 = new android.util.SparseIntArray;
        r31.<init>();
        r1 = r31;
    L_0x053d:
        r3 = r15.chat_id;
        r5 = r15.max_date;
        r1.put(r3, r5);
        r8 = r38;
        if (r8 != 0) goto L_0x054f;
    L_0x0548:
        r38 = new java.util.ArrayList;
        r38.<init>();
        r8 = r38;
    L_0x054f:
        r8.add(r15);
        r7 = r0;
        r31 = r1;
    L_0x0555:
        r38 = r8;
        goto L_0x051c;
    L_0x0558:
        r1 = r31;
        r8 = r38;
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd;
        if (r5 == 0) goto L_0x0598;
    L_0x0560:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd) r15;
        r44 = r54.getMessagesStorage();
        r3 = r15.chat_id;
        r5 = r15.user_id;
        r47 = 0;
        r6 = r15.inviter_id;
        r7 = r15.version;
        r45 = r3;
        r46 = r5;
        r48 = r6;
        r49 = r7;
        r44.updateChatInfo(r45, r46, r47, r48, r49);
    L_0x057b:
        r19 = r0;
        r31 = r1;
        r50 = r8;
        r51 = r9;
        r15 = r11;
        r10 = r26;
        r0 = r29;
        r1 = r32;
        r17 = r34;
        r11 = r41;
        r29 = r2;
    L_0x0590:
        r53 = r30;
        r30 = r14;
        r14 = r53;
        goto L_0x0e19;
    L_0x0598:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete;
        if (r5 == 0) goto L_0x05b6;
    L_0x059c:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete) r15;
        r44 = r54.getMessagesStorage();
        r3 = r15.chat_id;
        r5 = r15.user_id;
        r47 = 1;
        r48 = 0;
        r6 = r15.version;
        r45 = r3;
        r46 = r5;
        r49 = r6;
        r44.updateChatInfo(r45, r46, r47, r48, r49);
        goto L_0x057b;
    L_0x05b6:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDcOptions;
        if (r5 != 0) goto L_0x0df7;
    L_0x05ba:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateConfig;
        if (r5 == 0) goto L_0x05c0;
    L_0x05be:
        goto L_0x0df7;
    L_0x05c0:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryption;
        if (r5 == 0) goto L_0x05ce;
    L_0x05c4:
        r3 = r54.getSecretChatHelper();
        r15 = (org.telegram.tgnet.TLRPC.TL_updateEncryption) r15;
        r3.processUpdateEncryption(r15, r11);
        goto L_0x057b;
    L_0x05ce:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
        if (r5 == 0) goto L_0x05e5;
    L_0x05d2:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateUserBlocked) r15;
        r3 = r54.getMessagesStorage();
        r3 = r3.getStorageQueue();
        r5 = new org.telegram.messenger.-$$Lambda$MessagesController$Gnr81CP5OzSDaKQVyhrL4AOWsFU;
        r5.<init>(r13, r15);
        r3.postRunnable(r5);
        goto L_0x057b;
    L_0x05e5:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
        if (r5 == 0) goto L_0x05fc;
    L_0x05e9:
        if (r36 != 0) goto L_0x05f0;
    L_0x05eb:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x05f0:
        r3 = r36;
        r3.add(r15);
        r7 = r0;
        r31 = r1;
        r36 = r3;
        goto L_0x0555;
    L_0x05fc:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
        if (r5 == 0) goto L_0x0704;
    L_0x0600:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateServiceNotification) r15;
        r3 = r15.popup;
        if (r3 == 0) goto L_0x0618;
    L_0x0606:
        r3 = r15.message;
        if (r3 == 0) goto L_0x0618;
    L_0x060a:
        r3 = r3.length();
        if (r3 <= 0) goto L_0x0618;
    L_0x0610:
        r3 = new org.telegram.messenger.-$$Lambda$MessagesController$u9vf8aXfatYjuDXmDkbXgB2qhzw;
        r3.<init>(r13, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r3);
    L_0x0618:
        r3 = r15.flags;
        r5 = 2;
        r3 = r3 & r5;
        if (r3 == 0) goto L_0x06ed;
    L_0x061e:
        r3 = new org.telegram.tgnet.TLRPC$TL_message;
        r3.<init>();
        r5 = r54.getUserConfig();
        r5 = r5.getNewMessageId();
        r3.id = r5;
        r3.local_id = r5;
        r5 = r54.getUserConfig();
        r6 = 0;
        r5.saveConfig(r6);
        r5 = 1;
        r3.unread = r5;
        r5 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        r3.flags = r5;
        r5 = r15.inbox_date;
        if (r5 == 0) goto L_0x0645;
    L_0x0642:
        r3.date = r5;
        goto L_0x0650;
    L_0x0645:
        r5 = java.lang.System.currentTimeMillis();
        r19 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r19;
        r6 = (int) r5;
        r3.date = r6;
    L_0x0650:
        r5 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r3.from_id = r5;
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r5.<init>();
        r3.to_id = r5;
        r5 = r3.to_id;
        r6 = r54.getUserConfig();
        r6 = r6.getClientUserId();
        r5.user_id = r6;
        r5 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r3.dialog_id = r5;
        r5 = r15.media;
        if (r5 == 0) goto L_0x0679;
    L_0x0671:
        r3.media = r5;
        r5 = r3.flags;
        r5 = r5 | 512;
        r3.flags = r5;
    L_0x0679:
        r5 = r15.message;
        r3.message = r5;
        r5 = r15.entities;
        if (r5 == 0) goto L_0x0689;
    L_0x0681:
        r3.entities = r5;
        r5 = r3.flags;
        r5 = r5 | 128;
        r3.flags = r5;
    L_0x0689:
        if (r24 != 0) goto L_0x0690;
    L_0x068b:
        r24 = new java.util.ArrayList;
        r24.<init>();
    L_0x0690:
        r15 = r24;
        r15.add(r3);
        r7 = new org.telegram.messenger.MessageObject;
        r6 = r13.currentAccount;
        r5 = r13.createdDialogIds;
        r19 = r0;
        r31 = r1;
        r0 = r3.dialog_id;
        r0 = java.lang.Long.valueOf(r0);
        r0 = r5.contains(r0);
        r5 = r7;
        r1 = r32;
        r17 = r15;
        r15 = r7;
        r7 = r3;
        r50 = r8;
        r8 = r11;
        r51 = r9;
        r10 = r41;
        r9 = r12;
        r20 = r11;
        r11 = r10;
        r10 = r0;
        r5.<init>(r6, r7, r8, r9, r10);
        if (r11 != 0) goto L_0x06c7;
    L_0x06c1:
        r9 = new android.util.LongSparseArray;
        r9.<init>();
        goto L_0x06c8;
    L_0x06c7:
        r9 = r11;
    L_0x06c8:
        r5 = r3.dialog_id;
        r0 = r9.get(r5);
        r0 = (java.util.ArrayList) r0;
        if (r0 != 0) goto L_0x06dc;
    L_0x06d2:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r5 = r3.dialog_id;
        r9.put(r5, r0);
    L_0x06dc:
        r0.add(r15);
        if (r1 != 0) goto L_0x06e7;
    L_0x06e1:
        r6 = new java.util.ArrayList;
        r6.<init>();
        goto L_0x06e8;
    L_0x06e7:
        r6 = r1;
    L_0x06e8:
        r6.add(r15);
        r1 = r6;
        goto L_0x06fe;
    L_0x06ed:
        r19 = r0;
        r31 = r1;
        r50 = r8;
        r51 = r9;
        r20 = r11;
        r1 = r32;
        r11 = r41;
        r9 = r11;
        r17 = r24;
    L_0x06fe:
        r6 = r1;
        r32 = r2;
        r24 = r17;
        goto L_0x0728;
    L_0x0704:
        r19 = r0;
        r31 = r1;
        r50 = r8;
        r51 = r9;
        r20 = r11;
        r1 = r32;
        r11 = r41;
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
        if (r0 == 0) goto L_0x073b;
    L_0x0716:
        if (r36 != 0) goto L_0x071d;
    L_0x0718:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x071d:
        r0 = r36;
        r0.add(r15);
    L_0x0722:
        r36 = r0;
    L_0x0724:
        r6 = r1;
        r32 = r2;
    L_0x0727:
        r9 = r11;
    L_0x0728:
        r7 = r19;
        r8 = r33;
        r5 = r40;
        r38 = r50;
        r3 = 0;
        r33 = r27;
        r27 = r4;
        r4 = r20;
        r20 = r51;
        goto L_0x1311;
    L_0x073b:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs;
        if (r0 == 0) goto L_0x074c;
    L_0x073f:
        if (r36 != 0) goto L_0x0746;
    L_0x0741:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0746:
        r0 = r36;
        r0.add(r15);
        goto L_0x0722;
    L_0x074c:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateFolderPeers;
        if (r0 == 0) goto L_0x076c;
    L_0x0750:
        if (r36 != 0) goto L_0x0757;
    L_0x0752:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0757:
        r0 = r36;
        r0.add(r15);
        r15 = (org.telegram.tgnet.TLRPC.TL_updateFolderPeers) r15;
        r5 = r54.getMessagesStorage();
        r6 = r15.folder_peers;
        r7 = 0;
        r8 = 0;
        r10 = 0;
        r5.setDialogsFolderId(r6, r7, r8, r10);
        goto L_0x0722;
    L_0x076c:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePrivacy;
        if (r0 == 0) goto L_0x077d;
    L_0x0770:
        if (r36 != 0) goto L_0x0777;
    L_0x0772:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0777:
        r0 = r36;
        r0.add(r15);
        goto L_0x0722;
    L_0x077d:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateWebPage;
        if (r0 == 0) goto L_0x0798;
    L_0x0781:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateWebPage) r15;
        r0 = r29;
        if (r0 != 0) goto L_0x078e;
    L_0x0787:
        r29 = new android.util.LongSparseArray;
        r29.<init>();
        r0 = r29;
    L_0x078e:
        r3 = r15.webpage;
        r5 = r3.id;
        r0.put(r5, r3);
    L_0x0795:
        r29 = r0;
        goto L_0x0724;
    L_0x0798:
        r0 = r29;
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelWebPage;
        if (r5 == 0) goto L_0x07b1;
    L_0x079e:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChannelWebPage) r15;
        if (r0 != 0) goto L_0x07a9;
    L_0x07a2:
        r29 = new android.util.LongSparseArray;
        r29.<init>();
        r0 = r29;
    L_0x07a9:
        r3 = r15.webpage;
        r5 = r3.id;
        r0.put(r5, r3);
        goto L_0x0795;
    L_0x07b1:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelTooLong;
        if (r5 == 0) goto L_0x084d;
    L_0x07b5:
        r5 = r15;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelTooLong) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x07d3;
    L_0x07bc:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r15);
        r6.append(r3);
        r3 = r5.channel_id;
        r6.append(r3);
        r3 = r6.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x07d3:
        r3 = r13.channelsPts;
        r6 = r5.channel_id;
        r3 = r3.get(r6);
        if (r3 != 0) goto L_0x082c;
    L_0x07dd:
        r3 = r54.getMessagesStorage();
        r6 = r5.channel_id;
        r3 = r3.getChannelPtsSync(r6);
        if (r3 != 0) goto L_0x0825;
    L_0x07e9:
        r6 = r5.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r12.get(r6);
        r6 = (org.telegram.tgnet.TLRPC.Chat) r6;
        if (r6 == 0) goto L_0x07fb;
    L_0x07f7:
        r7 = r6.min;
        if (r7 == 0) goto L_0x0805;
    L_0x07fb:
        r6 = r5.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r13.getChat(r6);
    L_0x0805:
        if (r6 == 0) goto L_0x080b;
    L_0x0807:
        r7 = r6.min;
        if (r7 == 0) goto L_0x0819;
    L_0x080b:
        r6 = r54.getMessagesStorage();
        r7 = r5.channel_id;
        r6 = r6.getChatSync(r7);
        r7 = 1;
        r13.putChat(r6, r7);
    L_0x0819:
        if (r6 == 0) goto L_0x082c;
    L_0x081b:
        r7 = r6.min;
        if (r7 != 0) goto L_0x082c;
    L_0x081f:
        r7 = 0;
        r13.loadUnknownChannel(r6, r7);
        goto L_0x082c;
    L_0x0825:
        r6 = r13.channelsPts;
        r7 = r5.channel_id;
        r6.put(r7, r3);
    L_0x082c:
        if (r3 == 0) goto L_0x0843;
    L_0x082e:
        r6 = r5.flags;
        r7 = 1;
        r6 = r6 & r7;
        if (r6 == 0) goto L_0x083e;
    L_0x0834:
        r6 = r5.pts;
        if (r6 <= r3) goto L_0x0843;
    L_0x0838:
        r3 = r5.channel_id;
        r13.getChannelDifference(r3);
        goto L_0x0843;
    L_0x083e:
        r3 = r5.channel_id;
        r13.getChannelDifference(r3);
    L_0x0843:
        r29 = r2;
        r15 = r20;
        r10 = r26;
        r17 = r34;
        goto L_0x0590;
    L_0x084d:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
        if (r5 == 0) goto L_0x08a0;
    L_0x0851:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox) r15;
        r3 = r15.max_id;
        r5 = (long) r3;
        r3 = r15.channel_id;
        r7 = (long) r3;
        r9 = 32;
        r7 = r7 << r9;
        r5 = r5 | r7;
        r3 = -r3;
        r7 = (long) r3;
        if (r14 != 0) goto L_0x0866;
    L_0x0861:
        r14 = new org.telegram.messenger.support.SparseLongArray;
        r14.<init>();
    L_0x0866:
        r3 = r15.channel_id;
        r3 = -r3;
        r14.put(r3, r5);
        r3 = r13.dialogs_read_inbox_max;
        r5 = java.lang.Long.valueOf(r7);
        r3 = r3.get(r5);
        r3 = (java.lang.Integer) r3;
        if (r3 != 0) goto L_0x0887;
    L_0x087a:
        r3 = r54.getMessagesStorage();
        r5 = 0;
        r3 = r3.getDialogReadMax(r5, r7);
        r3 = java.lang.Integer.valueOf(r3);
    L_0x0887:
        r5 = r13.dialogs_read_inbox_max;
        r6 = java.lang.Long.valueOf(r7);
        r3 = r3.intValue();
        r7 = r15.max_id;
        r3 = java.lang.Math.max(r3, r7);
        r3 = java.lang.Integer.valueOf(r3);
        r5.put(r6, r3);
        goto L_0x0795;
    L_0x08a0:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox;
        if (r5 == 0) goto L_0x090b;
    L_0x08a4:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox) r15;
        r3 = r15.max_id;
        r5 = (long) r3;
        r3 = r15.channel_id;
        r7 = (long) r3;
        r9 = 32;
        r7 = r7 << r9;
        r5 = r5 | r7;
        r3 = -r3;
        r7 = (long) r3;
        if (r4 != 0) goto L_0x08ba;
    L_0x08b4:
        r3 = new org.telegram.messenger.support.SparseLongArray;
        r3.<init>();
        goto L_0x08bb;
    L_0x08ba:
        r3 = r4;
    L_0x08bb:
        r4 = r15.channel_id;
        r4 = -r4;
        r3.put(r4, r5);
        r4 = r13.dialogs_read_outbox_max;
        r5 = java.lang.Long.valueOf(r7);
        r4 = r4.get(r5);
        r4 = (java.lang.Integer) r4;
        if (r4 != 0) goto L_0x08dc;
    L_0x08cf:
        r4 = r54.getMessagesStorage();
        r5 = 1;
        r4 = r4.getDialogReadMax(r5, r7);
        r4 = java.lang.Integer.valueOf(r4);
    L_0x08dc:
        r5 = r13.dialogs_read_outbox_max;
        r6 = java.lang.Long.valueOf(r7);
        r4 = r4.intValue();
        r7 = r15.max_id;
        r4 = java.lang.Math.max(r4, r7);
        r4 = java.lang.Integer.valueOf(r4);
        r5.put(r6, r4);
        r29 = r0;
        r6 = r1;
        r32 = r2;
        r9 = r11;
        r7 = r19;
        r4 = r20;
        r8 = r33;
        r5 = r40;
        r38 = r50;
        r20 = r51;
        r33 = r27;
        r27 = r3;
        goto L_0x010f;
    L_0x090b:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
        if (r5 == 0) goto L_0x0951;
    L_0x090f:
        r5 = r15;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x092d;
    L_0x0916:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r15);
        r6.append(r3);
        r3 = r5.channel_id;
        r6.append(r3);
        r3 = r6.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x092d:
        if (r2 != 0) goto L_0x0936;
    L_0x092f:
        r32 = new android.util.SparseArray;
        r32.<init>();
        r2 = r32;
    L_0x0936:
        r3 = r5.channel_id;
        r3 = r2.get(r3);
        r3 = (java.util.ArrayList) r3;
        if (r3 != 0) goto L_0x094a;
    L_0x0940:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r6 = r5.channel_id;
        r2.put(r6, r3);
    L_0x094a:
        r5 = r5.messages;
        r3.addAll(r5);
        goto L_0x0795;
    L_0x0951:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannel;
        if (r5 == 0) goto L_0x0988;
    L_0x0955:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0973;
    L_0x0959:
        r5 = r15;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannel) r5;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r15);
        r6.append(r3);
        r3 = r5.channel_id;
        r6.append(r3);
        r3 = r6.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x0973:
        if (r36 != 0) goto L_0x097a;
    L_0x0975:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x097a:
        r3 = r36;
        r3.add(r15);
        r29 = r0;
        r6 = r1;
        r32 = r2;
        r36 = r3;
        goto L_0x0727;
    L_0x0988:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews;
        if (r5 == 0) goto L_0x09d9;
    L_0x098c:
        r5 = r15;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x09aa;
    L_0x0993:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r15);
        r6.append(r3);
        r3 = r5.channel_id;
        r6.append(r3);
        r3 = r6.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x09aa:
        r10 = r30;
        if (r10 != 0) goto L_0x09b5;
    L_0x09ae:
        r30 = new android.util.SparseArray;
        r30.<init>();
        r10 = r30;
    L_0x09b5:
        r3 = r5.channel_id;
        r3 = r10.get(r3);
        r3 = (android.util.SparseIntArray) r3;
        if (r3 != 0) goto L_0x09c9;
    L_0x09bf:
        r3 = new android.util.SparseIntArray;
        r3.<init>();
        r6 = r5.channel_id;
        r10.put(r6, r3);
    L_0x09c9:
        r6 = r5.id;
        r5 = r5.views;
        r3.put(r6, r5);
        r29 = r0;
        r6 = r1;
        r32 = r2;
    L_0x09d5:
        r30 = r10;
        goto L_0x0727;
    L_0x09d9:
        r10 = r30;
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
        if (r5 == 0) goto L_0x0a07;
    L_0x09df:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin) r15;
        r44 = r54.getMessagesStorage();
        r3 = r15.chat_id;
        r5 = r15.user_id;
        r47 = 2;
        r6 = r15.is_admin;
        r7 = r15.version;
        r45 = r3;
        r46 = r5;
        r48 = r6;
        r49 = r7;
        r44.updateChatInfo(r45, r46, r47, r48, r49);
    L_0x09fa:
        r29 = r2;
        r30 = r14;
        r15 = r20;
        r17 = r34;
    L_0x0a02:
        r14 = r10;
        r10 = r26;
        goto L_0x0e19;
    L_0x0a07:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights;
        if (r5 == 0) goto L_0x0a2f;
    L_0x0a0b:
        r3 = r15;
        r3 = (org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights) r3;
        r5 = r3.peer;
        r6 = r5.channel_id;
        if (r6 == 0) goto L_0x0a15;
    L_0x0a14:
        goto L_0x0a17;
    L_0x0a15:
        r6 = r5.chat_id;
    L_0x0a17:
        r5 = r54.getMessagesStorage();
        r7 = r3.default_banned_rights;
        r3 = r3.version;
        r5.updateChatDefaultBannedRights(r6, r7, r3);
        if (r36 != 0) goto L_0x0a29;
    L_0x0a24:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0a29:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0a2f:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSets;
        if (r5 == 0) goto L_0x0a47;
    L_0x0a33:
        if (r36 != 0) goto L_0x0a3a;
    L_0x0a35:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0a3a:
        r3 = r36;
        r3.add(r15);
    L_0x0a3f:
        r29 = r0;
        r6 = r1;
        r32 = r2;
        r36 = r3;
        goto L_0x09d5;
    L_0x0a47:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
        if (r5 == 0) goto L_0x0a58;
    L_0x0a4b:
        if (r36 != 0) goto L_0x0a52;
    L_0x0a4d:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0a52:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0a58:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
        if (r5 == 0) goto L_0x0a69;
    L_0x0a5c:
        if (r36 != 0) goto L_0x0a63;
    L_0x0a5e:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0a63:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0a69:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
        if (r5 == 0) goto L_0x0a7a;
    L_0x0a6d:
        if (r36 != 0) goto L_0x0a74;
    L_0x0a6f:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0a74:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0a7a:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
        if (r5 == 0) goto L_0x0a8b;
    L_0x0a7e:
        if (r36 != 0) goto L_0x0a85;
    L_0x0a80:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0a85:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0a8b:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
        if (r5 != 0) goto L_0x0CLASSNAME;
    L_0x0a8f:
        r6 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateEditMessage;
        if (r6 == 0) goto L_0x0a95;
    L_0x0a93:
        goto L_0x0CLASSNAME;
    L_0x0a95:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
        if (r5 == 0) goto L_0x0ac4;
    L_0x0a99:
        r5 = r15;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0ab7;
    L_0x0aa0:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r15);
        r6.append(r3);
        r3 = r5.channel_id;
        r6.append(r3);
        r3 = r6.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x0ab7:
        r3 = r54.getMessagesStorage();
        r6 = r5.channel_id;
        r5 = r5.id;
        r3.updateChatPinnedMessage(r6, r5);
        goto L_0x09fa;
    L_0x0ac4:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage;
        if (r3 == 0) goto L_0x0ad7;
    L_0x0ac8:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage) r15;
        r3 = r54.getMessagesStorage();
        r5 = r15.chat_id;
        r6 = r15.id;
        r3.updateChatPinnedMessage(r5, r6);
        goto L_0x09fa;
    L_0x0ad7:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage;
        if (r3 == 0) goto L_0x0aea;
    L_0x0adb:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage) r15;
        r3 = r54.getMessagesStorage();
        r5 = r15.user_id;
        r6 = r15.id;
        r3.updateUserPinnedMessage(r5, r6);
        goto L_0x09fa;
    L_0x0aea:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
        if (r3 == 0) goto L_0x0afc;
    L_0x0aee:
        if (r36 != 0) goto L_0x0af5;
    L_0x0af0:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0af5:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0afc:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePhoneCall;
        if (r3 == 0) goto L_0x0b0e;
    L_0x0b00:
        if (r36 != 0) goto L_0x0b07;
    L_0x0b02:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0b07:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0b0e:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPack;
        if (r3 == 0) goto L_0x0b1e;
    L_0x0b12:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateLangPack) r15;
        r3 = new org.telegram.messenger.-$$Lambda$MessagesController$TGaV3j0rMT2kXtICENm7gz6gqZw;
        r3.<init>(r13, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r3);
        goto L_0x09fa;
    L_0x0b1e:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong;
        if (r3 == 0) goto L_0x0b31;
    L_0x0b22:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong) r15;
        r3 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r13.currentAccount;
        r6 = r15.lang_code;
        r3.reloadCurrentRemoteLocale(r5, r6);
        goto L_0x09fa;
    L_0x0b31:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
        if (r3 == 0) goto L_0x0b43;
    L_0x0b35:
        if (r36 != 0) goto L_0x0b3c;
    L_0x0b37:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0b3c:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0b43:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateContactsReset;
        if (r3 == 0) goto L_0x0b55;
    L_0x0b47:
        if (r36 != 0) goto L_0x0b4e;
    L_0x0b49:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0b4e:
        r3 = r36;
        r3.add(r15);
        goto L_0x0a3f;
    L_0x0b55:
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages;
        if (r3 == 0) goto L_0x0b82;
    L_0x0b59:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages) r15;
        r3 = r34;
        if (r3 != 0) goto L_0x0b66;
    L_0x0b5f:
        r34 = new android.util.SparseIntArray;
        r34.<init>();
        r3 = r34;
    L_0x0b66:
        r5 = r15.channel_id;
        r5 = r3.get(r5);
        if (r5 == 0) goto L_0x0b72;
    L_0x0b6e:
        r6 = r15.available_min_id;
        if (r5 >= r6) goto L_0x0b79;
    L_0x0b72:
        r5 = r15.channel_id;
        r6 = r15.available_min_id;
        r3.put(r5, r6);
    L_0x0b79:
        r29 = r0;
        r6 = r1;
        r32 = r2;
        r34 = r3;
        goto L_0x09d5;
    L_0x0b82:
        r3 = r34;
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark;
        if (r5 == 0) goto L_0x0b9f;
    L_0x0b88:
        if (r36 != 0) goto L_0x0b8f;
    L_0x0b8a:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0b8f:
        r5 = r36;
        r5.add(r15);
    L_0x0b94:
        r29 = r0;
        r6 = r1;
        r32 = r2;
        r34 = r3;
        r36 = r5;
        goto L_0x09d5;
    L_0x0b9f:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateMessagePoll;
        if (r5 == 0) goto L_0x0be3;
    L_0x0ba3:
        r5 = r15;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateMessagePoll) r5;
        r6 = r54.getSendMessagesHelper();
        r7 = r5.poll_id;
        r6 = r6.getVoteSendTime(r7);
        r8 = android.os.SystemClock.uptimeMillis();
        r8 = r8 - r6;
        r6 = java.lang.Math.abs(r8);
        r8 = 600; // 0x258 float:8.41E-43 double:2.964E-321;
        r17 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r17 >= 0) goto L_0x0bc9;
    L_0x0bbf:
        r29 = r2;
        r17 = r3;
        r30 = r14;
        r15 = r20;
        goto L_0x0a02;
    L_0x0bc9:
        r6 = r54.getMessagesStorage();
        r7 = r5.poll_id;
        r9 = r5.poll;
        r5 = r5.results;
        r6.updateMessagePollResults(r7, r9, r5);
        if (r36 != 0) goto L_0x0bdd;
    L_0x0bd8:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0bdd:
        r5 = r36;
        r5.add(r15);
        goto L_0x0b94;
    L_0x0be3:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateMessageReactions;
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0be7:
        r5 = r15;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateMessageReactions) r5;
        r6 = r5.peer;
        r7 = r6.chat_id;
        if (r7 == 0) goto L_0x0bf5;
    L_0x0bf0:
        r6 = -r7;
    L_0x0bf1:
        r6 = (long) r6;
        r45 = r6;
        goto L_0x0bfd;
    L_0x0bf5:
        r7 = r6.channel_id;
        if (r7 == 0) goto L_0x0bfa;
    L_0x0bf9:
        goto L_0x0bf0;
    L_0x0bfa:
        r6 = r6.user_id;
        goto L_0x0bf1;
    L_0x0bfd:
        r44 = r54.getMessagesStorage();
        r6 = r5.msg_id;
        r7 = r5.peer;
        r7 = r7.channel_id;
        r5 = r5.reactions;
        r47 = r6;
        r48 = r7;
        r49 = r5;
        r44.updateMessageReactions(r45, r47, r48, r49);
        if (r36 != 0) goto L_0x0c1a;
    L_0x0CLASSNAME:
        r5 = new java.util.ArrayList;
        r5.<init>();
        goto L_0x0c1c;
    L_0x0c1a:
        r5 = r36;
    L_0x0c1c:
        r5.add(r15);
        goto L_0x0b94;
    L_0x0CLASSNAME:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerLocated;
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r36 != 0) goto L_0x0c2c;
    L_0x0CLASSNAME:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0c2c:
        r5 = r36;
        r5.add(r15);
        goto L_0x0b94;
    L_0x0CLASSNAME:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateTheme;
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r36 != 0) goto L_0x0c3e;
    L_0x0CLASSNAME:
        r36 = new java.util.ArrayList;
        r36.<init>();
    L_0x0c3e:
        r5 = r36;
        r5.add(r15);
        goto L_0x0b94;
    L_0x0CLASSNAME:
        r5 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateGeoLiveViewed;
        if (r5 == 0) goto L_0x0bbf;
    L_0x0CLASSNAME:
        r5 = r54.getLocationController();
        r5.setNewLocationEndWatchTime();
        goto L_0x0bbf;
    L_0x0CLASSNAME:
        r3 = r34;
        r6 = r54.getUserConfig();
        r6 = r6.getClientUserId();
        if (r5 == 0) goto L_0x0ca4;
    L_0x0c5e:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage) r15;
        r5 = r15.message;
        r7 = r5.to_id;
        r7 = r7.channel_id;
        r7 = java.lang.Integer.valueOf(r7);
        r7 = r12.get(r7);
        r7 = (org.telegram.tgnet.TLRPC.Chat) r7;
        if (r7 != 0) goto L_0x0c7e;
    L_0x0CLASSNAME:
        r7 = r5.to_id;
        r7 = r7.channel_id;
        r7 = java.lang.Integer.valueOf(r7);
        r7 = r13.getChat(r7);
    L_0x0c7e:
        if (r7 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r7 = r54.getMessagesStorage();
        r8 = r5.to_id;
        r8 = r8.channel_id;
        r7 = r7.getChatSync(r8);
        r8 = 1;
        r13.putChat(r7, r8);
    L_0x0CLASSNAME:
        if (r7 == 0) goto L_0x0c9d;
    L_0x0CLASSNAME:
        r7 = r7.megagroup;
        if (r7 == 0) goto L_0x0c9d;
    L_0x0CLASSNAME:
        r7 = r5.flags;
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r7 = r7 | r8;
        r5.flags = r7;
    L_0x0c9d:
        r29 = r2;
        r17 = r3;
        r3 = r5;
        r2 = 1;
        goto L_0x0cbe;
    L_0x0ca4:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateEditMessage) r15;
        r5 = r15.message;
        r7 = r5.dialog_id;
        r29 = r2;
        r17 = r3;
        r2 = (long) r6;
        r9 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r9 != 0) goto L_0x0cbc;
    L_0x0cb3:
        r2 = 0;
        r5.unread = r2;
        r5.media_unread = r2;
        r2 = 1;
        r5.out = r2;
        goto L_0x0cbd;
    L_0x0cbc:
        r2 = 1;
    L_0x0cbd:
        r3 = r5;
    L_0x0cbe:
        r5 = r3.out;
        if (r5 != 0) goto L_0x0cd0;
    L_0x0cc2:
        r5 = r3.from_id;
        r7 = r54.getUserConfig();
        r7 = r7.getClientUserId();
        if (r5 != r7) goto L_0x0cd0;
    L_0x0cce:
        r3.out = r2;
    L_0x0cd0:
        if (r58 != 0) goto L_0x0d2c;
    L_0x0cd2:
        r2 = r3.entities;
        r2 = r2.size();
        r5 = 0;
    L_0x0cd9:
        if (r5 >= r2) goto L_0x0d2c;
    L_0x0cdb:
        r7 = r3.entities;
        r7 = r7.get(r5);
        r7 = (org.telegram.tgnet.TLRPC.MessageEntity) r7;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r8 == 0) goto L_0x0d25;
    L_0x0ce7:
        r7 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r7;
        r7 = r7.user_id;
        r8 = java.lang.Integer.valueOf(r7);
        r15 = r20;
        r8 = r15.get(r8);
        r8 = (org.telegram.tgnet.TLRPC.User) r8;
        if (r8 == 0) goto L_0x0cfd;
    L_0x0cf9:
        r9 = r8.min;
        if (r9 == 0) goto L_0x0d05;
    L_0x0cfd:
        r8 = java.lang.Integer.valueOf(r7);
        r8 = r13.getUser(r8);
    L_0x0d05:
        if (r8 == 0) goto L_0x0d0b;
    L_0x0d07:
        r9 = r8.min;
        if (r9 == 0) goto L_0x0d21;
    L_0x0d0b:
        r8 = r54.getMessagesStorage();
        r7 = r8.getUserSync(r7);
        if (r7 == 0) goto L_0x0d1c;
    L_0x0d15:
        r8 = r7.min;
        if (r8 == 0) goto L_0x0d1c;
    L_0x0d19:
        r7 = 1;
        r8 = 0;
        goto L_0x0d1e;
    L_0x0d1c:
        r8 = r7;
        r7 = 1;
    L_0x0d1e:
        r13.putUser(r8, r7);
    L_0x0d21:
        if (r8 != 0) goto L_0x0d27;
    L_0x0d23:
        r7 = 0;
        return r7;
    L_0x0d25:
        r15 = r20;
    L_0x0d27:
        r5 = r5 + 1;
        r20 = r15;
        goto L_0x0cd9;
    L_0x0d2c:
        r15 = r20;
        r2 = r3.to_id;
        r5 = r2.chat_id;
        if (r5 == 0) goto L_0x0d39;
    L_0x0d34:
        r2 = -r5;
        r7 = (long) r2;
        r3.dialog_id = r7;
        goto L_0x0d5b;
    L_0x0d39:
        r5 = r2.channel_id;
        if (r5 == 0) goto L_0x0d42;
    L_0x0d3d:
        r2 = -r5;
        r7 = (long) r2;
        r3.dialog_id = r7;
        goto L_0x0d5b;
    L_0x0d42:
        r2 = r2.user_id;
        r5 = r54.getUserConfig();
        r5 = r5.getClientUserId();
        if (r2 != r5) goto L_0x0d54;
    L_0x0d4e:
        r2 = r3.to_id;
        r5 = r3.from_id;
        r2.user_id = r5;
    L_0x0d54:
        r2 = r3.to_id;
        r2 = r2.user_id;
        r7 = (long) r2;
        r3.dialog_id = r7;
    L_0x0d5b:
        r2 = r3.out;
        if (r2 == 0) goto L_0x0d62;
    L_0x0d5f:
        r2 = r13.dialogs_read_outbox_max;
        goto L_0x0d64;
    L_0x0d62:
        r2 = r13.dialogs_read_inbox_max;
    L_0x0d64:
        r7 = r3.dialog_id;
        r5 = java.lang.Long.valueOf(r7);
        r5 = r2.get(r5);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x0d8b;
    L_0x0d72:
        r5 = r54.getMessagesStorage();
        r7 = r3.out;
        r8 = r3.dialog_id;
        r5 = r5.getDialogReadMax(r7, r8);
        r5 = java.lang.Integer.valueOf(r5);
        r7 = r3.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r2.put(r7, r5);
    L_0x0d8b:
        r2 = r5.intValue();
        r5 = r3.id;
        if (r2 >= r5) goto L_0x0d95;
    L_0x0d93:
        r2 = 1;
        goto L_0x0d96;
    L_0x0d95:
        r2 = 0;
    L_0x0d96:
        r3.unread = r2;
        r7 = r3.dialog_id;
        r5 = (long) r6;
        r2 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1));
        if (r2 != 0) goto L_0x0da7;
    L_0x0d9f:
        r2 = 1;
        r3.out = r2;
        r2 = 0;
        r3.unread = r2;
        r3.media_unread = r2;
    L_0x0da7:
        r2 = r3.out;
        if (r2 == 0) goto L_0x0db5;
    L_0x0dab:
        r2 = r3.message;
        if (r2 != 0) goto L_0x0db5;
    L_0x0daf:
        r2 = "";
        r3.message = r2;
        r3.attachPath = r2;
    L_0x0db5:
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r3);
        r2 = new org.telegram.messenger.MessageObject;
        r6 = r13.currentAccount;
        r5 = r13.createdDialogIds;
        r7 = r3.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r20 = r5.contains(r7);
        r5 = r2;
        r7 = r3;
        r8 = r15;
        r9 = r12;
        r30 = r14;
        r14 = r10;
        r10 = r20;
        r5.<init>(r6, r7, r8, r9, r10);
        r10 = r26;
        if (r10 != 0) goto L_0x0ddf;
    L_0x0dd8:
        r26 = new android.util.LongSparseArray;
        r26.<init>();
        r10 = r26;
    L_0x0ddf:
        r5 = r3.dialog_id;
        r5 = r10.get(r5);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x0df3;
    L_0x0de9:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r6 = r3.dialog_id;
        r10.put(r6, r5);
    L_0x0df3:
        r5.add(r2);
        goto L_0x0e19;
    L_0x0df7:
        r19 = r0;
        r31 = r1;
        r50 = r8;
        r51 = r9;
        r15 = r11;
        r10 = r26;
        r0 = r29;
        r1 = r32;
        r17 = r34;
        r11 = r41;
        r29 = r2;
        r53 = r30;
        r30 = r14;
        r14 = r53;
        r2 = r54.getConnectionsManager();
        r2.updateDcSettings();
    L_0x0e19:
        r6 = r1;
        r26 = r10;
        r9 = r11;
        r34 = r17;
        r7 = r19;
        r32 = r29;
        r8 = r33;
        r5 = r40;
        r38 = r50;
        r20 = r51;
        r3 = 0;
        r29 = r0;
        r33 = r27;
        r27 = r4;
        r4 = r15;
        goto L_0x1161;
    L_0x0e35:
        r40 = r5;
        r1 = r6;
        r33 = r8;
        r43 = r10;
        r8 = r11;
        r51 = r20;
        r10 = r26;
        r17 = r34;
        r50 = r38;
        r11 = r9;
        r9 = r29;
        r29 = r2;
        r2 = r7;
        r53 = r30;
        r30 = r14;
        r14 = r53;
        if (r0 == 0) goto L_0x0e5c;
    L_0x0e53:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateUserTyping) r15;
        r0 = r15.user_id;
        r3 = r15.action;
        r5 = r3;
        r3 = 0;
        goto L_0x0e69;
    L_0x0e5c:
        r15 = (org.telegram.tgnet.TLRPC.TL_updateChatUserTyping) r15;
        r0 = r15.chat_id;
        r3 = r15.user_id;
        r5 = r15.action;
        r53 = r3;
        r3 = r0;
        r0 = r53;
    L_0x0e69:
        r6 = r54.getUserConfig();
        r6 = r6.getClientUserId();
        if (r0 == r6) goto L_0x0var_;
    L_0x0e73:
        r3 = -r3;
        r6 = (long) r3;
        r20 = 0;
        r3 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1));
        if (r3 != 0) goto L_0x0e7c;
    L_0x0e7b:
        r6 = (long) r0;
    L_0x0e7c:
        r3 = r13.printingUsers;
        r15 = java.lang.Long.valueOf(r6);
        r3 = r3.get(r15);
        r3 = (java.util.ArrayList) r3;
        r15 = r5 instanceof org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction;
        if (r15 == 0) goto L_0x0ec9;
    L_0x0e8c:
        if (r3 == 0) goto L_0x0ec0;
    L_0x0e8e:
        r5 = r3.size();
        r15 = 0;
    L_0x0e93:
        if (r15 >= r5) goto L_0x0eae;
    L_0x0e95:
        r20 = r3.get(r15);
        r26 = r4;
        r4 = r20;
        r4 = (org.telegram.messenger.MessagesController.PrintingUser) r4;
        r4 = r4.userId;
        if (r4 != r0) goto L_0x0ea9;
    L_0x0ea3:
        r3.remove(r15);
        r16 = 1;
        goto L_0x0eb0;
    L_0x0ea9:
        r15 = r15 + 1;
        r4 = r26;
        goto L_0x0e93;
    L_0x0eae:
        r26 = r4;
    L_0x0eb0:
        r3 = r3.isEmpty();
        if (r3 == 0) goto L_0x0ec2;
    L_0x0eb6:
        r3 = r13.printingUsers;
        r4 = java.lang.Long.valueOf(r6);
        r3.remove(r4);
        goto L_0x0ec2;
    L_0x0ec0:
        r26 = r4;
    L_0x0ec2:
        r32 = r1;
        r34 = r2;
        r1 = r51;
        goto L_0x0var_;
    L_0x0ec9:
        r26 = r4;
        if (r3 != 0) goto L_0x0edb;
    L_0x0ecd:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = r13.printingUsers;
        r6 = java.lang.Long.valueOf(r6);
        r4.put(r6, r3);
    L_0x0edb:
        r4 = r3.iterator();
    L_0x0edf:
        r6 = r4.hasNext();
        if (r6 == 0) goto L_0x0var_;
    L_0x0ee5:
        r6 = r4.next();
        r6 = (org.telegram.messenger.MessagesController.PrintingUser) r6;
        r7 = r6.userId;
        if (r7 != r0) goto L_0x0edf;
    L_0x0eef:
        r32 = r1;
        r34 = r2;
        r1 = r51;
        r6.lastTime = r1;
        r4 = r6.action;
        r4 = r4.getClass();
        r7 = r5.getClass();
        if (r4 == r7) goto L_0x0var_;
    L_0x0var_:
        r16 = 1;
    L_0x0var_:
        r6.action = r5;
        r4 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r32 = r1;
        r34 = r2;
        r1 = r51;
        r4 = 0;
    L_0x0var_:
        if (r4 != 0) goto L_0x0var_;
    L_0x0var_:
        r4 = new org.telegram.messenger.MessagesController$PrintingUser;
        r4.<init>();
        r4.userId = r0;
        r4.lastTime = r1;
        r4.action = r5;
        r3.add(r4);
        r16 = 1;
    L_0x0var_:
        r3 = r54.getConnectionsManager();
        r3 = r3.getCurrentTime();
        r3 = r3 - r59;
        r3 = java.lang.Math.abs(r3);
        r4 = 30;
        if (r3 >= r4) goto L_0x0f4a;
    L_0x0var_:
        r3 = r13.onlinePrivacy;
        r0 = java.lang.Integer.valueOf(r0);
        r4 = java.lang.Integer.valueOf(r59);
        r3.put(r0, r4);
        goto L_0x0f4a;
    L_0x0var_:
        r32 = r1;
        r34 = r2;
        r26 = r4;
        r1 = r51;
    L_0x0f4a:
        r20 = r1;
        r4 = r8;
        r6 = r32;
        r8 = r33;
        r7 = r34;
        r5 = r40;
        r38 = r50;
        r3 = 0;
        r34 = r17;
        goto L_0x1156;
    L_0x0f5c:
        r25 = r4;
        r40 = r5;
        r43 = r10;
        r1 = r20;
        r10 = r26;
        r26 = r27;
        r27 = r33;
        r4 = r34;
        r50 = r38;
        r34 = r7;
        r33 = r8;
        r8 = r11;
        r11 = r9;
        r9 = r29;
        r29 = r32;
        r32 = r6;
        r53 = r30;
        r30 = r14;
        r14 = r53;
        if (r0 == 0) goto L_0x0var_;
    L_0x0var_:
        r0 = r15;
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNewMessage) r0;
        r0 = r0.message;
        goto L_0x0fc7;
    L_0x0var_:
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
        if (r0 == 0) goto L_0x0var_;
    L_0x0f8c:
        r0 = r15;
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage) r0;
        r0 = r0.message;
        goto L_0x0fc7;
    L_0x0var_:
        r0 = r15;
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r0;
        r0 = r0.message;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0fb4;
    L_0x0f9b:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r15);
        r6.append(r3);
        r3 = r0.to_id;
        r3 = r3.channel_id;
        r6.append(r3);
        r3 = r6.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x0fb4:
        r3 = r0.out;
        if (r3 != 0) goto L_0x0fc7;
    L_0x0fb8:
        r3 = r0.from_id;
        r6 = r54.getUserConfig();
        r6 = r6.getClientUserId();
        if (r3 != r6) goto L_0x0fc7;
    L_0x0fc4:
        r3 = 1;
        r0.out = r3;
    L_0x0fc7:
        r3 = r0.to_id;
        r6 = r3.channel_id;
        if (r6 == 0) goto L_0x0fcf;
    L_0x0fcd:
        r3 = 0;
        goto L_0x0fdb;
    L_0x0fcf:
        r6 = r3.chat_id;
        if (r6 == 0) goto L_0x0fd4;
    L_0x0fd3:
        goto L_0x0fcd;
    L_0x0fd4:
        r3 = r3.user_id;
        if (r3 == 0) goto L_0x0fd9;
    L_0x0fd8:
        goto L_0x0fda;
    L_0x0fd9:
        r3 = 0;
    L_0x0fda:
        r6 = 0;
    L_0x0fdb:
        if (r6 == 0) goto L_0x1000;
    L_0x0fdd:
        r7 = java.lang.Integer.valueOf(r6);
        r7 = r12.get(r7);
        r7 = (org.telegram.tgnet.TLRPC.Chat) r7;
        if (r7 != 0) goto L_0x0ff1;
    L_0x0fe9:
        r7 = java.lang.Integer.valueOf(r6);
        r7 = r13.getChat(r7);
    L_0x0ff1:
        if (r7 != 0) goto L_0x1001;
    L_0x0ff3:
        r7 = r54.getMessagesStorage();
        r7 = r7.getChatSync(r6);
        r5 = 1;
        r13.putChat(r7, r5);
        goto L_0x1001;
    L_0x1000:
        r7 = 0;
    L_0x1001:
        if (r25 == 0) goto L_0x10f7;
    L_0x1003:
        if (r6 == 0) goto L_0x1021;
    L_0x1005:
        if (r7 != 0) goto L_0x1021;
    L_0x1007:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x101f;
    L_0x100b:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "not found chat ";
        r0.append(r1);
        r0.append(r6);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x101f:
        r0 = 0;
        return r0;
    L_0x1021:
        r5 = r0.entities;
        r5 = r5.size();
        r5 = r5 + 3;
        r6 = r3;
        r3 = 0;
    L_0x102b:
        if (r3 >= r5) goto L_0x10f7;
    L_0x102d:
        r20 = r1;
        if (r3 == 0) goto L_0x105e;
    L_0x1031:
        r1 = 1;
        if (r3 != r1) goto L_0x103e;
    L_0x1034:
        r1 = r0.from_id;
        r2 = r0.post;
        r6 = r1;
        r1 = 2;
        if (r2 == 0) goto L_0x105f;
    L_0x103c:
        r2 = 1;
        goto L_0x1060;
    L_0x103e:
        r1 = 2;
        if (r3 != r1) goto L_0x1048;
    L_0x1041:
        r2 = r0.fwd_from;
        if (r2 == 0) goto L_0x105b;
    L_0x1045:
        r2 = r2.from_id;
        goto L_0x105c;
    L_0x1048:
        r2 = r0.entities;
        r6 = r3 + -3;
        r2 = r2.get(r6);
        r2 = (org.telegram.tgnet.TLRPC.MessageEntity) r2;
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r6 == 0) goto L_0x105b;
    L_0x1056:
        r2 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r2;
        r2 = r2.user_id;
        goto L_0x105c;
    L_0x105b:
        r2 = 0;
    L_0x105c:
        r6 = r2;
        goto L_0x105f;
    L_0x105e:
        r1 = 2;
    L_0x105f:
        r2 = 0;
    L_0x1060:
        if (r6 <= 0) goto L_0x10ed;
    L_0x1062:
        r1 = java.lang.Integer.valueOf(r6);
        r1 = r8.get(r1);
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r38 = r4;
        if (r1 == 0) goto L_0x1076;
    L_0x1070:
        if (r2 != 0) goto L_0x107e;
    L_0x1072:
        r4 = r1.min;
        if (r4 == 0) goto L_0x107e;
    L_0x1076:
        r1 = java.lang.Integer.valueOf(r6);
        r1 = r13.getUser(r1);
    L_0x107e:
        if (r1 == 0) goto L_0x1086;
    L_0x1080:
        if (r2 != 0) goto L_0x109b;
    L_0x1082:
        r4 = r1.min;
        if (r4 == 0) goto L_0x109b;
    L_0x1086:
        r1 = r54.getMessagesStorage();
        r1 = r1.getUserSync(r6);
        if (r1 == 0) goto L_0x1097;
    L_0x1090:
        if (r2 != 0) goto L_0x1097;
    L_0x1092:
        r2 = r1.min;
        if (r2 == 0) goto L_0x1097;
    L_0x1096:
        r1 = 0;
    L_0x1097:
        r2 = 1;
        r13.putUser(r1, r2);
    L_0x109b:
        if (r1 != 0) goto L_0x10b7;
    L_0x109d:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x10b5;
    L_0x10a1:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "not found user ";
        r0.append(r1);
        r0.append(r6);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x10b5:
        r0 = 0;
        return r0;
    L_0x10b7:
        r2 = r0.out;
        if (r2 != 0) goto L_0x10ef;
    L_0x10bb:
        r2 = 1;
        if (r3 != r2) goto L_0x10ef;
    L_0x10be:
        r1 = r1.status;
        if (r1 == 0) goto L_0x10ef;
    L_0x10c2:
        r1 = r1.expires;
        if (r1 > 0) goto L_0x10ef;
    L_0x10c6:
        r1 = r54.getConnectionsManager();
        r1 = r1.getCurrentTime();
        r2 = r0.date;
        r1 = r1 - r2;
        r1 = java.lang.Math.abs(r1);
        r2 = 30;
        if (r1 >= r2) goto L_0x10ef;
    L_0x10d9:
        r1 = r13.onlinePrivacy;
        r4 = java.lang.Integer.valueOf(r6);
        r2 = r0.date;
        r2 = java.lang.Integer.valueOf(r2);
        r1.put(r4, r2);
        r1 = r33 | 4;
        r33 = r1;
        goto L_0x10ef;
    L_0x10ed:
        r38 = r4;
    L_0x10ef:
        r3 = r3 + 1;
        r1 = r20;
        r4 = r38;
        goto L_0x102b;
    L_0x10f7:
        r20 = r1;
        r38 = r4;
        if (r7 == 0) goto L_0x1108;
    L_0x10fd:
        r1 = r7.megagroup;
        if (r1 == 0) goto L_0x1108;
    L_0x1101:
        r1 = r0.flags;
        r2 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r1 = r1 | r2;
        r0.flags = r1;
    L_0x1108:
        r1 = r0.action;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r2 == 0) goto L_0x1169;
    L_0x110e:
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r8.get(r1);
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        if (r1 == 0) goto L_0x112e;
    L_0x111c:
        r1 = r1.bot;
        if (r1 == 0) goto L_0x112e;
    L_0x1120:
        r1 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r1.<init>();
        r0.reply_markup = r1;
        r1 = r0.flags;
        r1 = r1 | 64;
        r0.flags = r1;
        goto L_0x1169;
    L_0x112e:
        r1 = r0.from_id;
        r2 = r54.getUserConfig();
        r2 = r2.getClientUserId();
        if (r1 != r2) goto L_0x1169;
    L_0x113a:
        r1 = r0.action;
        r1 = r1.user_id;
        r2 = r54.getUserConfig();
        r2 = r2.getClientUserId();
        if (r1 != r2) goto L_0x1169;
    L_0x1148:
        r4 = r8;
        r6 = r32;
        r8 = r33;
        r7 = r34;
        r34 = r38;
        r5 = r40;
        r38 = r50;
        r3 = 0;
    L_0x1156:
        r33 = r27;
        r32 = r29;
        r29 = r9;
        r9 = r11;
        r27 = r26;
        r26 = r10;
    L_0x1161:
        r53 = r30;
        r30 = r14;
        r14 = r53;
        goto L_0x1311;
    L_0x1169:
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r0);
        r1 = r54.getUserConfig();
        r1 = r1.getClientUserId();
        r2 = r0.to_id;
        r3 = r2.chat_id;
        if (r3 == 0) goto L_0x117f;
    L_0x117a:
        r2 = -r3;
        r2 = (long) r2;
        r0.dialog_id = r2;
        goto L_0x1197;
    L_0x117f:
        r3 = r2.channel_id;
        if (r3 == 0) goto L_0x1188;
    L_0x1183:
        r2 = -r3;
        r2 = (long) r2;
        r0.dialog_id = r2;
        goto L_0x1197;
    L_0x1188:
        r3 = r2.user_id;
        if (r3 != r1) goto L_0x1190;
    L_0x118c:
        r3 = r0.from_id;
        r2.user_id = r3;
    L_0x1190:
        r2 = r0.to_id;
        r2 = r2.user_id;
        r2 = (long) r2;
        r0.dialog_id = r2;
    L_0x1197:
        r2 = r13.createdDialogIds;
        r3 = r0.dialog_id;
        r3 = java.lang.Long.valueOf(r3);
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x11c1;
    L_0x11a5:
        r2 = r0.grouped_id;
        r4 = 0;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x11c1;
    L_0x11ad:
        r2 = org.telegram.messenger.ImageLoader.generateMessageThumb(r0);
        if (r2 == 0) goto L_0x11c1;
    L_0x11b3:
        if (r37 != 0) goto L_0x11ba;
    L_0x11b5:
        r37 = new java.util.ArrayList;
        r37.<init>();
    L_0x11ba:
        r3 = r37;
        r3.add(r2);
        r37 = r3;
    L_0x11c1:
        r2 = r15 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
        if (r2 == 0) goto L_0x121c;
    L_0x11c5:
        if (r18 != 0) goto L_0x11cc;
    L_0x11c7:
        r18 = new java.util.ArrayList;
        r18.<init>();
    L_0x11cc:
        r1 = r18;
        r1.add(r0);
        r2 = new org.telegram.messenger.MessageObject;
        r6 = r13.currentAccount;
        r3 = r13.createdScheduledDialogIds;
        r4 = r0.dialog_id;
        r4 = java.lang.Long.valueOf(r4);
        r3 = r3.contains(r4);
        r5 = r2;
        r7 = r0;
        r4 = r8;
        r15 = r9;
        r9 = r12;
        r39 = r15;
        r15 = r10;
        r10 = r3;
        r5.<init>(r6, r7, r8, r9, r10);
        r3 = 1;
        r2.scheduled = r3;
        if (r28 != 0) goto L_0x11f7;
    L_0x11f2:
        r28 = new android.util.LongSparseArray;
        r28.<init>();
    L_0x11f7:
        r3 = r28;
        r5 = r0.dialog_id;
        r5 = r3.get(r5);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x120d;
    L_0x1203:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r6 = r0.dialog_id;
        r3.put(r6, r5);
    L_0x120d:
        r5.add(r2);
        r18 = r1;
        r28 = r3;
        r9 = r11;
        r2 = r24;
        r8 = r33;
        r3 = 0;
        goto L_0x12f9;
    L_0x121c:
        r4 = r8;
        r39 = r9;
        r15 = r10;
        if (r24 != 0) goto L_0x1227;
    L_0x1222:
        r24 = new java.util.ArrayList;
        r24.<init>();
    L_0x1227:
        r2 = r24;
        r2.add(r0);
        r3 = r0.out;
        if (r3 == 0) goto L_0x1233;
    L_0x1230:
        r3 = r13.dialogs_read_outbox_max;
        goto L_0x1235;
    L_0x1233:
        r3 = r13.dialogs_read_inbox_max;
    L_0x1235:
        r5 = r0.dialog_id;
        r5 = java.lang.Long.valueOf(r5);
        r5 = r3.get(r5);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x125c;
    L_0x1243:
        r5 = r54.getMessagesStorage();
        r6 = r0.out;
        r8 = r0.dialog_id;
        r5 = r5.getDialogReadMax(r6, r8);
        r5 = java.lang.Integer.valueOf(r5);
        r8 = r0.dialog_id;
        r6 = java.lang.Long.valueOf(r8);
        r3.put(r6, r5);
    L_0x125c:
        r3 = r5.intValue();
        r5 = r0.id;
        if (r3 >= r5) goto L_0x1278;
    L_0x1264:
        if (r7 == 0) goto L_0x126c;
    L_0x1266:
        r3 = org.telegram.messenger.ChatObject.isNotInChat(r7);
        if (r3 != 0) goto L_0x1278;
    L_0x126c:
        r3 = r0.action;
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r5 != 0) goto L_0x1278;
    L_0x1272:
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r3 != 0) goto L_0x1278;
    L_0x1276:
        r3 = 1;
        goto L_0x1279;
    L_0x1278:
        r3 = 0;
    L_0x1279:
        r0.unread = r3;
        r5 = r0.dialog_id;
        r7 = (long) r1;
        r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r1 != 0) goto L_0x128f;
    L_0x1282:
        r1 = r0.from_scheduled;
        r3 = 0;
        if (r1 != 0) goto L_0x1289;
    L_0x1287:
        r0.unread = r3;
    L_0x1289:
        r0.media_unread = r3;
        r1 = 1;
        r0.out = r1;
        goto L_0x1290;
    L_0x128f:
        r3 = 0;
    L_0x1290:
        r1 = new org.telegram.messenger.MessageObject;
        r6 = r13.currentAccount;
        r5 = r13.createdDialogIds;
        r7 = r0.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r10 = r5.contains(r7);
        r5 = r1;
        r7 = r0;
        r8 = r4;
        r9 = r12;
        r5.<init>(r6, r7, r8, r9, r10);
        r5 = r1.type;
        r6 = 11;
        if (r5 != r6) goto L_0x12b0;
    L_0x12ad:
        r33 = r33 | 8;
        goto L_0x12b6;
    L_0x12b0:
        r6 = 10;
        if (r5 != r6) goto L_0x12b6;
    L_0x12b4:
        r33 = r33 | 16;
    L_0x12b6:
        if (r11 != 0) goto L_0x12be;
    L_0x12b8:
        r9 = new android.util.LongSparseArray;
        r9.<init>();
        goto L_0x12bf;
    L_0x12be:
        r9 = r11;
    L_0x12bf:
        r5 = r0.dialog_id;
        r5 = r9.get(r5);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x12d3;
    L_0x12c9:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r6 = r0.dialog_id;
        r9.put(r6, r5);
    L_0x12d3:
        r5.add(r1);
        r0 = r1.isOut();
        if (r0 == 0) goto L_0x12e2;
    L_0x12dc:
        r0 = r1.messageOwner;
        r0 = r0.from_scheduled;
        if (r0 == 0) goto L_0x12f7;
    L_0x12e2:
        r0 = r1.isUnread();
        if (r0 == 0) goto L_0x12f7;
    L_0x12e8:
        if (r32 != 0) goto L_0x12f0;
    L_0x12ea:
        r6 = new java.util.ArrayList;
        r6.<init>();
        goto L_0x12f2;
    L_0x12f0:
        r6 = r32;
    L_0x12f2:
        r6.add(r1);
        r32 = r6;
    L_0x12f7:
        r8 = r33;
    L_0x12f9:
        r24 = r2;
        r33 = r27;
        r6 = r32;
        r7 = r34;
        r34 = r38;
        r5 = r40;
        r38 = r50;
        r27 = r26;
        r32 = r29;
        r29 = r39;
        r26 = r15;
        goto L_0x1161;
    L_0x1311:
        r10 = r43 + 1;
        r0 = r57;
        r11 = r4;
        r2 = r20;
        r4 = r25;
        goto L_0x00a5;
    L_0x131c:
        r40 = r5;
        r4 = r11;
        r15 = r26;
        r26 = r27;
        r39 = r29;
        r29 = r32;
        r27 = r33;
        r50 = r38;
        r3 = 0;
        r32 = r6;
        r33 = r8;
        r11 = r9;
        r38 = r34;
        r34 = r7;
        r53 = r30;
        r30 = r14;
        r14 = r53;
        if (r11 == 0) goto L_0x1359;
    L_0x133d:
        r0 = r11.size();
        r1 = 0;
    L_0x1342:
        if (r1 >= r0) goto L_0x1359;
    L_0x1344:
        r5 = r11.keyAt(r1);
        r2 = r11.valueAt(r1);
        r2 = (java.util.ArrayList) r2;
        r2 = r13.updatePrintingUsersWithNewMessages(r5, r2);
        if (r2 == 0) goto L_0x1356;
    L_0x1354:
        r16 = 1;
    L_0x1356:
        r1 = r1 + 1;
        goto L_0x1342;
    L_0x1359:
        r8 = r16;
        if (r8 == 0) goto L_0x1360;
    L_0x135d:
        r54.updatePrintingStrings();
    L_0x1360:
        if (r34 == 0) goto L_0x136c;
    L_0x1362:
        r0 = r54.getContactsController();
        r7 = r34;
        r0.processContactsUpdates(r7, r4);
        goto L_0x136e;
    L_0x136c:
        r7 = r34;
    L_0x136e:
        if (r32 == 0) goto L_0x1382;
    L_0x1370:
        r0 = r54.getMessagesStorage();
        r0 = r0.getStorageQueue();
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$Zv5FRAVG-J53wGTHAwMM5uE87Wo;
        r6 = r32;
        r1.<init>(r13, r6);
        r0.postRunnable(r1);
    L_0x1382:
        if (r18 == 0) goto L_0x139b;
    L_0x1384:
        r17 = r54.getMessagesStorage();
        r19 = 1;
        r20 = 1;
        r21 = 0;
        r0 = r54.getDownloadController();
        r22 = r0.getAutodownloadMask();
        r23 = 1;
        r17.putMessages(r18, r19, r20, r21, r22, r23);
    L_0x139b:
        if (r24 == 0) goto L_0x13c8;
    L_0x139d:
        r0 = r54.getStatsController();
        r1 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType();
        r2 = r24.size();
        r4 = 1;
        r0.incrementReceivedItemsCount(r1, r4, r2);
        r19 = r54.getMessagesStorage();
        r21 = 1;
        r22 = 1;
        r23 = 0;
        r0 = r54.getDownloadController();
        r0 = r0.getAutodownloadMask();
        r25 = 0;
        r20 = r24;
        r24 = r0;
        r19.putMessages(r20, r21, r22, r23, r24, r25);
    L_0x13c8:
        if (r15 == 0) goto L_0x140b;
    L_0x13ca:
        r0 = r15.size();
        r1 = 0;
    L_0x13cf:
        if (r1 >= r0) goto L_0x140b;
    L_0x13d1:
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
        r2.<init>();
        r4 = r15.valueAt(r1);
        r4 = (java.util.ArrayList) r4;
        r5 = r4.size();
        r6 = 0;
    L_0x13e1:
        if (r6 >= r5) goto L_0x13f3;
    L_0x13e3:
        r9 = r2.messages;
        r10 = r4.get(r6);
        r10 = (org.telegram.messenger.MessageObject) r10;
        r10 = r10.messageOwner;
        r9.add(r10);
        r6 = r6 + 1;
        goto L_0x13e1;
    L_0x13f3:
        r16 = r54.getMessagesStorage();
        r18 = r15.keyAt(r1);
        r20 = -2;
        r21 = 0;
        r22 = 0;
        r23 = 0;
        r17 = r2;
        r16.putMessages(r17, r18, r20, r21, r22, r23);
        r1 = r1 + 1;
        goto L_0x13cf;
    L_0x140b:
        if (r14 == 0) goto L_0x1415;
    L_0x140d:
        r0 = r54.getMessagesStorage();
        r1 = 1;
        r0.putChannelViews(r14, r1);
    L_0x1415:
        r16 = new org.telegram.messenger.-$$Lambda$MessagesController$JGthECDkZYjTCuTa_DusXO85MSY;
        r0 = r16;
        r10 = r27;
        r12 = r31;
        r1 = r54;
        r9 = r7;
        r7 = r29;
        r2 = r33;
        r17 = 0;
        r3 = r36;
        r6 = r26;
        r5 = r38;
        r4 = r39;
        r5 = r11;
        r11 = r6;
        r6 = r28;
        r7 = r15;
        r15 = r10;
        r10 = r35;
        r26 = r11;
        r11 = r14;
        r14 = r12;
        r12 = r37;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
        r0 = r54.getMessagesStorage();
        r9 = r0.getStorageQueue();
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$xcy_je40mQkkPrT8kyD_ok5QRUE;
        r0 = r10;
        r2 = r30;
        r3 = r26;
        r4 = r14;
        r5 = r40;
        r6 = r29;
        r7 = r15;
        r8 = r38;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8);
        r9.postRunnable(r10);
        if (r39 == 0) goto L_0x146a;
    L_0x1461:
        r0 = r54.getMessagesStorage();
        r1 = r39;
        r0.putWebPages(r1);
    L_0x146a:
        r1 = r26;
        if (r30 != 0) goto L_0x1477;
    L_0x146e:
        if (r1 != 0) goto L_0x1477;
    L_0x1470:
        if (r14 != 0) goto L_0x1477;
    L_0x1472:
        r5 = r40;
        if (r5 == 0) goto L_0x1493;
    L_0x1476:
        goto L_0x1479;
    L_0x1477:
        r5 = r40;
    L_0x1479:
        if (r30 != 0) goto L_0x1482;
    L_0x147b:
        if (r5 == 0) goto L_0x147e;
    L_0x147d:
        goto L_0x1482;
    L_0x147e:
        r2 = r30;
        r3 = 1;
        goto L_0x148c;
    L_0x1482:
        r0 = r54.getMessagesStorage();
        r2 = r30;
        r3 = 1;
        r0.updateDialogsWithReadMessages(r2, r1, r5, r3);
    L_0x148c:
        r0 = r54.getMessagesStorage();
        r0.markMessagesAsRead(r2, r1, r14, r3);
    L_0x1493:
        if (r5 == 0) goto L_0x14a4;
    L_0x1495:
        r0 = r54.getMessagesStorage();
        r1 = r54.getConnectionsManager();
        r1 = r1.getCurrentTime();
        r0.markMessagesContentAsRead(r5, r1);
    L_0x14a4:
        r1 = r29;
        if (r1 == 0) goto L_0x14cc;
    L_0x14a8:
        r0 = r1.size();
        r2 = 0;
    L_0x14ad:
        if (r2 >= r0) goto L_0x14cc;
    L_0x14af:
        r3 = r1.keyAt(r2);
        r4 = r1.valueAt(r2);
        r4 = (java.util.ArrayList) r4;
        r5 = r54.getMessagesStorage();
        r5 = r5.getStorageQueue();
        r6 = new org.telegram.messenger.-$$Lambda$MessagesController$RP3wwVIGzm9hBA1N9dWlKNUkaFQ;
        r6.<init>(r13, r4, r3);
        r5.postRunnable(r6);
        r2 = r2 + 1;
        goto L_0x14ad;
    L_0x14cc:
        if (r15 == 0) goto L_0x14ef;
    L_0x14ce:
        r0 = r15.size();
        r1 = 0;
    L_0x14d3:
        if (r1 >= r0) goto L_0x14ef;
    L_0x14d5:
        r5 = r15.keyAt(r1);
        r2 = r15.valueAt(r1);
        r3 = r2;
        r3 = (java.util.ArrayList) r3;
        r2 = r13.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r4 = 1;
        r6 = 0;
        r7 = 1;
        r2.markMessagesAsDeleted(r3, r4, r5, r6, r7);
        r1 = r1 + 1;
        goto L_0x14d3;
    L_0x14ef:
        r1 = r38;
        if (r1 == 0) goto L_0x1515;
    L_0x14f3:
        r0 = r1.size();
        r2 = 0;
    L_0x14f8:
        if (r2 >= r0) goto L_0x1515;
    L_0x14fa:
        r3 = r1.keyAt(r2);
        r4 = r1.valueAt(r2);
        r5 = r54.getMessagesStorage();
        r5 = r5.getStorageQueue();
        r6 = new org.telegram.messenger.-$$Lambda$MessagesController$YpS3d6nStkAeVus78dbY1P4pSWY;
        r6.<init>(r13, r3, r4);
        r5.postRunnable(r6);
        r2 = r2 + 1;
        goto L_0x14f8;
    L_0x1515:
        r1 = r50;
        if (r1 == 0) goto L_0x1538;
    L_0x1519:
        r0 = r1.size();
        r2 = 0;
    L_0x151e:
        if (r2 >= r0) goto L_0x1538;
    L_0x1520:
        r3 = r1.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r3;
        r4 = r54.getMessagesStorage();
        r5 = r3.chat_id;
        r6 = r3.max_date;
        r7 = r3.date;
        r8 = 1;
        r9 = 0;
        r4.createTaskForSecretChat(r5, r6, r7, r8, r9);
        r2 = r2 + 1;
        goto L_0x151e;
    L_0x1538:
        r0 = 1;
        return r0;
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

    public /* synthetic */ void lambda$processUpdateArray$259$MessagesController(TL_updateUserBlocked tL_updateUserBlocked) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$EcJBpmISndhLJkR5Fk6vuEZQdPY(this, tL_updateUserBlocked));
    }

    public /* synthetic */ void lambda$null$258$MessagesController(TL_updateUserBlocked tL_updateUserBlocked) {
        if (!tL_updateUserBlocked.blocked) {
            this.blockedUsers.delete(tL_updateUserBlocked.user_id);
        } else if (this.blockedUsers.indexOfKey(tL_updateUserBlocked.user_id) < 0) {
            this.blockedUsers.put(tL_updateUserBlocked.user_id, 1);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdateArray$260$MessagesController(TL_updateServiceNotification tL_updateServiceNotification) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(2), tL_updateServiceNotification.message, tL_updateServiceNotification.type);
    }

    public /* synthetic */ void lambda$processUpdateArray$261$MessagesController(TL_updateLangPack tL_updateLangPack) {
        LocaleController.getInstance().saveRemoteLocaleStringsForCurrentLocale(tL_updateLangPack.difference, this.currentAccount);
    }

    public /* synthetic */ void lambda$null$262$MessagesController(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$processUpdateArray$263$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$uO7I-mlxQHl5Ek93CeE8oQ77KXo(this, arrayList));
    }

    /* JADX WARNING: Removed duplicated region for block: B:417:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x09a2  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0a54  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0a61  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0a65  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0a94  */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0a84  */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0a98  */
    /* JADX WARNING: Removed duplicated region for block: B:503:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0aad  */
    public /* synthetic */ void lambda$processUpdateArray$268$MessagesController(int r33, java.util.ArrayList r34, android.util.LongSparseArray r35, android.util.LongSparseArray r36, android.util.LongSparseArray r37, android.util.LongSparseArray r38, boolean r39, java.util.ArrayList r40, java.util.ArrayList r41, android.util.SparseArray r42, java.util.ArrayList r43) {
        /*
        r32 = this;
        r8 = r32;
        r9 = r34;
        r10 = r35;
        r11 = r36;
        r12 = r37;
        r13 = r38;
        r14 = r41;
        r15 = r43;
        r3 = 0;
        if (r9 == 0) goto L_0x0880;
    L_0x0013:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r6 = r34.size();
        r22 = r33;
        r7 = 0;
        r20 = 0;
        r21 = 0;
    L_0x0028:
        if (r7 >= r6) goto L_0x0856;
    L_0x002a:
        r0 = r9.get(r7);
        r0 = (org.telegram.tgnet.TLRPC.Update) r0;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePrivacy;
        if (r2 == 0) goto L_0x00c4;
    L_0x0034:
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePrivacy) r0;
        r2 = r0.key;
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
        if (r1 == 0) goto L_0x004b;
    L_0x003c:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r1.setPrivacyRules(r0, r3);
    L_0x0045:
        r18 = 3;
        r19 = 2;
        goto L_0x00b9;
    L_0x004b:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite;
        if (r1 == 0) goto L_0x005a;
    L_0x004f:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r2 = 1;
        r1.setPrivacyRules(r0, r2);
        goto L_0x0045;
    L_0x005a:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneCall;
        if (r1 == 0) goto L_0x0069;
    L_0x005e:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r2 = 2;
        r1.setPrivacyRules(r0, r2);
        goto L_0x0045;
    L_0x0069:
        r19 = 2;
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneP2P;
        if (r1 == 0) goto L_0x007c;
    L_0x006f:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r2 = 3;
        r1.setPrivacyRules(r0, r2);
        r18 = 3;
        goto L_0x00b9;
    L_0x007c:
        r18 = 3;
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyProfilePhoto;
        if (r1 == 0) goto L_0x008d;
    L_0x0082:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r2 = 4;
        r1.setPrivacyRules(r0, r2);
        goto L_0x00b9;
    L_0x008d:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyForwards;
        if (r1 == 0) goto L_0x009c;
    L_0x0091:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r2 = 5;
        r1.setPrivacyRules(r0, r2);
        goto L_0x00b9;
    L_0x009c:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneNumber;
        if (r1 == 0) goto L_0x00ab;
    L_0x00a0:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r2 = 6;
        r1.setPrivacyRules(r0, r2);
        goto L_0x00b9;
    L_0x00ab:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyAddedByPhone;
        if (r1 == 0) goto L_0x00b9;
    L_0x00af:
        r1 = r32.getContactsController();
        r0 = r0.rules;
        r2 = 7;
        r1.setPrivacyRules(r0, r2);
    L_0x00b9:
        r31 = r4;
        r9 = r5;
        r16 = r6;
        r17 = r7;
    L_0x00c0:
        r1 = r22;
        goto L_0x0846;
    L_0x00c4:
        r18 = 3;
        r19 = 2;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserStatus;
        if (r1 == 0) goto L_0x0126;
    L_0x00cc:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserStatus) r0;
        r1 = r0.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r8.getUser(r1);
        r2 = r0.status;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_userStatusRecently;
        if (r3 == 0) goto L_0x00e3;
    L_0x00de:
        r3 = -100;
        r2.expires = r3;
        goto L_0x00f4;
    L_0x00e3:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
        if (r3 == 0) goto L_0x00ec;
    L_0x00e7:
        r3 = -101; // 0xffffffffffffff9b float:NaN double:NaN;
        r2.expires = r3;
        goto L_0x00f4;
    L_0x00ec:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
        if (r3 == 0) goto L_0x00f4;
    L_0x00f0:
        r3 = -102; // 0xffffffffffffff9a float:NaN double:NaN;
        r2.expires = r3;
    L_0x00f4:
        if (r1 == 0) goto L_0x00fe;
    L_0x00f6:
        r2 = r0.user_id;
        r1.id = r2;
        r2 = r0.status;
        r1.status = r2;
    L_0x00fe:
        r1 = new org.telegram.tgnet.TLRPC$TL_user;
        r1.<init>();
        r2 = r0.user_id;
        r1.id = r2;
        r2 = r0.status;
        r1.status = r2;
        r4.add(r1);
        r1 = r0.user_id;
        r2 = r32.getUserConfig();
        r2 = r2.getClientUserId();
        if (r1 != r2) goto L_0x00b9;
    L_0x011a:
        r1 = r32.getNotificationsController();
        r0 = r0.status;
        r0 = r0.expires;
        r1.setLastOnlineFromOtherDevice(r0);
        goto L_0x00b9;
    L_0x0126:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r1 == 0) goto L_0x0182;
    L_0x012a:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserName) r0;
        r1 = r0.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r8.getUser(r1);
        if (r1 == 0) goto L_0x0168;
    L_0x0138:
        r2 = org.telegram.messenger.UserObject.isContact(r1);
        if (r2 != 0) goto L_0x0146;
    L_0x013e:
        r2 = r0.first_name;
        r1.first_name = r2;
        r2 = r0.last_name;
        r1.last_name = r2;
    L_0x0146:
        r2 = r1.username;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0155;
    L_0x014e:
        r2 = r8.objectsByUsernames;
        r3 = r1.username;
        r2.remove(r3);
    L_0x0155:
        r2 = r0.username;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x0164;
    L_0x015d:
        r2 = r8.objectsByUsernames;
        r3 = r0.username;
        r2.put(r3, r1);
    L_0x0164:
        r2 = r0.username;
        r1.username = r2;
    L_0x0168:
        r1 = new org.telegram.tgnet.TLRPC$TL_user;
        r1.<init>();
        r2 = r0.user_id;
        r1.id = r2;
        r2 = r0.first_name;
        r1.first_name = r2;
        r2 = r0.last_name;
        r1.last_name = r2;
        r0 = r0.username;
        r1.username = r0;
        r5.add(r1);
        goto L_0x00b9;
    L_0x0182:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
        if (r1 == 0) goto L_0x01d9;
    L_0x0186:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateDialogPinned) r0;
        r1 = r0.peer;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer;
        if (r2 == 0) goto L_0x0198;
    L_0x018e:
        r1 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r1;
        r1 = r1.peer;
        r1 = org.telegram.messenger.DialogObject.getPeerDialogId(r1);
        r2 = r1;
        goto L_0x019a;
    L_0x0198:
        r2 = 0;
    L_0x019a:
        r1 = r0.pinned;
        r26 = 0;
        r27 = -1;
        r24 = r1;
        r1 = r32;
        r29 = r2;
        r31 = r4;
        r4 = r24;
        r9 = r5;
        r5 = r26;
        r16 = r6;
        r17 = r7;
        r15 = 2;
        r6 = r27;
        r1 = r1.pinDialog(r2, r4, r5, r6);
        if (r1 != 0) goto L_0x01d5;
    L_0x01ba:
        r1 = r32.getUserConfig();
        r2 = r0.folder_id;
        r3 = 0;
        r1.setPinnedDialogsLoaded(r2, r3);
        r1 = r32.getUserConfig();
        r1.saveConfig(r3);
        r0 = r0.folder_id;
        r1 = r29;
        r4 = 0;
        r8.loadPinnedDialogs(r0, r1, r4);
        goto L_0x00c0;
    L_0x01d5:
        r3 = 0;
        r4 = 0;
        goto L_0x00c0;
    L_0x01d9:
        r31 = r4;
        r9 = r5;
        r16 = r6;
        r17 = r7;
        r3 = 0;
        r4 = 0;
        r15 = 2;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs;
        if (r1 == 0) goto L_0x024f;
    L_0x01e7:
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs) r0;
        r1 = r32.getUserConfig();
        r2 = r0.folder_id;
        r1.setPinnedDialogsLoaded(r2, r3);
        r1 = r32.getUserConfig();
        r1.saveConfig(r3);
        r1 = r0.flags;
        r2 = 1;
        r1 = r1 & r2;
        if (r1 == 0) goto L_0x0245;
    L_0x01ff:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r5 = r0.order;
        r6 = r5.size();
        r7 = 0;
    L_0x020b:
        if (r7 >= r6) goto L_0x0246;
    L_0x020d:
        r19 = r5.get(r7);
        r2 = r19;
        r2 = (org.telegram.tgnet.TLRPC.DialogPeer) r2;
        r15 = r2 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer;
        if (r15 == 0) goto L_0x0232;
    L_0x0219:
        r2 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r2;
        r2 = r2.peer;
        r15 = r2.user_id;
        if (r15 == 0) goto L_0x0225;
    L_0x0221:
        r23 = r5;
        r4 = (long) r15;
        goto L_0x0236;
    L_0x0225:
        r23 = r5;
        r4 = r2.chat_id;
        if (r4 == 0) goto L_0x022d;
    L_0x022b:
        r2 = -r4;
        goto L_0x0230;
    L_0x022d:
        r2 = r2.channel_id;
        r2 = -r2;
    L_0x0230:
        r4 = (long) r2;
        goto L_0x0236;
    L_0x0232:
        r23 = r5;
        r4 = 0;
    L_0x0236:
        r2 = java.lang.Long.valueOf(r4);
        r1.add(r2);
        r7 = r7 + 1;
        r5 = r23;
        r2 = 1;
        r4 = 0;
        r15 = 2;
        goto L_0x020b;
    L_0x0245:
        r1 = 0;
    L_0x0246:
        r0 = r0.folder_id;
        r4 = 0;
        r8.loadPinnedDialogs(r0, r4, r1);
        goto L_0x00c0;
    L_0x024f:
        r4 = 0;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateFolderPeers;
        if (r1 == 0) goto L_0x0292;
    L_0x0255:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateFolderPeers) r0;
        r1 = r0.folder_peers;
        r1 = r1.size();
        r2 = 0;
    L_0x025e:
        if (r2 >= r1) goto L_0x028e;
    L_0x0260:
        r6 = r0.folder_peers;
        r6 = r6.get(r2);
        r6 = (org.telegram.tgnet.TLRPC.TL_folderPeer) r6;
        r7 = r6.peer;
        r4 = org.telegram.messenger.DialogObject.getPeerDialogId(r7);
        r7 = r8.dialogs_dict;
        r4 = r7.get(r4);
        r4 = (org.telegram.tgnet.TLRPC.Dialog) r4;
        if (r4 != 0) goto L_0x0279;
    L_0x0278:
        goto L_0x0289;
    L_0x0279:
        r5 = r4.folder_id;
        r6 = r6.folder_id;
        if (r5 == r6) goto L_0x0289;
    L_0x027f:
        r4.pinned = r3;
        r4.pinnedNum = r3;
        r4.folder_id = r6;
        r4 = 0;
        r8.ensureFolderDialogExists(r6, r4);
    L_0x0289:
        r2 = r2 + 1;
        r4 = 0;
        goto L_0x025e;
    L_0x028e:
        r21 = 1;
        goto L_0x0848;
    L_0x0292:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
        if (r1 == 0) goto L_0x02ba;
    L_0x0296:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhoto) r0;
        r1 = r0.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r8.getUser(r1);
        if (r1 == 0) goto L_0x02a8;
    L_0x02a4:
        r2 = r0.photo;
        r1.photo = r2;
    L_0x02a8:
        r1 = new org.telegram.tgnet.TLRPC$TL_user;
        r1.<init>();
        r2 = r0.user_id;
        r1.id = r2;
        r0 = r0.photo;
        r1.photo = r0;
        r9.add(r1);
        goto L_0x00c0;
    L_0x02ba:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhone;
        if (r1 == 0) goto L_0x02fd;
    L_0x02be:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhone) r0;
        r1 = r0.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r8.getUser(r1);
        if (r1 == 0) goto L_0x02eb;
    L_0x02cc:
        r2 = r0.phone;
        r1.phone = r2;
        r2 = org.telegram.messenger.Utilities.phoneBookQueue;
        r4 = new org.telegram.messenger.-$$Lambda$MessagesController$k7hEf2Sqkmp6V-chvj_ni0sdttw;
        r4.<init>(r8, r1);
        r2.postRunnable(r4);
        r1 = org.telegram.messenger.UserObject.isUserSelf(r1);
        if (r1 == 0) goto L_0x02eb;
    L_0x02e0:
        r1 = r32.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged;
        r4 = new java.lang.Object[r3];
        r1.postNotificationName(r2, r4);
    L_0x02eb:
        r1 = new org.telegram.tgnet.TLRPC$TL_user;
        r1.<init>();
        r2 = r0.user_id;
        r1.id = r2;
        r0 = r0.phone;
        r1.phone = r0;
        r9.add(r1);
        goto L_0x00c0;
    L_0x02fd:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
        if (r1 == 0) goto L_0x04a1;
    L_0x0301:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNotifySettings) r0;
        r1 = r0.notify_settings;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
        if (r1 == 0) goto L_0x0848;
    L_0x0309:
        if (r20 != 0) goto L_0x0311;
    L_0x030b:
        r1 = r8.notificationsPreferences;
        r20 = r1.edit();
    L_0x0311:
        r1 = r20;
        r2 = r32.getConnectionsManager();
        r2 = r2.getCurrentTime();
        r4 = r0.peer;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyPeer;
        if (r5 == 0) goto L_0x0435;
    L_0x0321:
        r4 = (org.telegram.tgnet.TLRPC.TL_notifyPeer) r4;
        r4 = r4.peer;
        r5 = r4.user_id;
        if (r5 == 0) goto L_0x032b;
    L_0x0329:
        r4 = (long) r5;
        goto L_0x0335;
    L_0x032b:
        r5 = r4.chat_id;
        if (r5 == 0) goto L_0x0331;
    L_0x032f:
        r4 = -r5;
        goto L_0x0334;
    L_0x0331:
        r4 = r4.channel_id;
        r4 = -r4;
    L_0x0334:
        r4 = (long) r4;
    L_0x0335:
        r6 = r8.dialogs_dict;
        r6 = r6.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.Dialog) r6;
        if (r6 == 0) goto L_0x0343;
    L_0x033f:
        r7 = r0.notify_settings;
        r6.notify_settings = r7;
    L_0x0343:
        r7 = r0.notify_settings;
        r7 = r7.flags;
        r15 = 2;
        r7 = r7 & r15;
        r15 = "silent_";
        if (r7 == 0) goto L_0x0364;
    L_0x034d:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r15);
        r7.append(r4);
        r7 = r7.toString();
        r15 = r0.notify_settings;
        r15 = r15.silent;
        r1.putBoolean(r7, r15);
        goto L_0x0376;
    L_0x0364:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r15);
        r7.append(r4);
        r7 = r7.toString();
        r1.remove(r7);
    L_0x0376:
        r7 = r0.notify_settings;
        r15 = r7.flags;
        r20 = 4;
        r15 = r15 & 4;
        r3 = "notify2_";
        if (r15 == 0) goto L_0x0414;
    L_0x0382:
        r15 = r7.mute_until;
        if (r15 <= r2) goto L_0x03f2;
    L_0x0386:
        r7 = 31536000; // 0x1e13380 float:8.2725845E-38 double:1.5580854E-316;
        r2 = r2 + r7;
        if (r15 <= r2) goto L_0x03aa;
    L_0x038c:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r3);
        r2.append(r4);
        r2 = r2.toString();
        r3 = 2;
        r1.putInt(r2, r3);
        if (r6 == 0) goto L_0x03a8;
    L_0x03a1:
        r0 = r0.notify_settings;
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0.mute_until = r2;
    L_0x03a8:
        r15 = 0;
        goto L_0x03db;
    L_0x03aa:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r3);
        r2.append(r4);
        r2 = r2.toString();
        r3 = 3;
        r1.putInt(r2, r3);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "notifyuntil_";
        r2.append(r3);
        r2.append(r4);
        r2 = r2.toString();
        r3 = r0.notify_settings;
        r3 = r3.mute_until;
        r1.putInt(r2, r3);
        if (r6 == 0) goto L_0x03db;
    L_0x03d7:
        r0 = r0.notify_settings;
        r0.mute_until = r15;
    L_0x03db:
        r0 = r32.getMessagesStorage();
        r2 = (long) r15;
        r6 = 32;
        r2 = r2 << r6;
        r6 = 1;
        r2 = r2 | r6;
        r0.setDialogFlags(r4, r2);
        r0 = r32.getNotificationsController();
        r0.removeNotificationsForDialog(r4);
        goto L_0x049d;
    L_0x03f2:
        r2 = 0;
        if (r6 == 0) goto L_0x03f7;
    L_0x03f5:
        r7.mute_until = r2;
    L_0x03f7:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r0.append(r4);
        r0 = r0.toString();
        r1.putInt(r0, r2);
        r0 = r32.getMessagesStorage();
        r6 = 0;
        r0.setDialogFlags(r4, r6);
        goto L_0x049d;
    L_0x0414:
        r2 = 0;
        if (r6 == 0) goto L_0x0419;
    L_0x0417:
        r7.mute_until = r2;
    L_0x0419:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r0.append(r4);
        r0 = r0.toString();
        r1.remove(r0);
        r0 = r32.getMessagesStorage();
        r2 = 0;
        r0.setDialogFlags(r4, r2);
        goto L_0x049d;
    L_0x0435:
        r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyChats;
        if (r2 == 0) goto L_0x0458;
    L_0x0439:
        r2 = r0.notify_settings;
        r3 = r2.flags;
        r4 = 1;
        r3 = r3 & r4;
        if (r3 == 0) goto L_0x0448;
    L_0x0441:
        r2 = r2.show_previews;
        r3 = "EnablePreviewGroup";
        r1.putBoolean(r3, r2);
    L_0x0448:
        r0 = r0.notify_settings;
        r2 = r0.flags;
        r3 = 4;
        r2 = r2 & r3;
        if (r2 == 0) goto L_0x049d;
    L_0x0450:
        r0 = r0.mute_until;
        r2 = "EnableGroup2";
        r1.putInt(r2, r0);
        goto L_0x049d;
    L_0x0458:
        r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyUsers;
        if (r2 == 0) goto L_0x047b;
    L_0x045c:
        r2 = r0.notify_settings;
        r3 = r2.flags;
        r4 = 1;
        r3 = r3 & r4;
        if (r3 == 0) goto L_0x046b;
    L_0x0464:
        r2 = r2.show_previews;
        r3 = "EnablePreviewAll";
        r1.putBoolean(r3, r2);
    L_0x046b:
        r0 = r0.notify_settings;
        r2 = r0.flags;
        r3 = 4;
        r2 = r2 & r3;
        if (r2 == 0) goto L_0x049d;
    L_0x0473:
        r0 = r0.mute_until;
        r2 = "EnableAll2";
        r1.putInt(r2, r0);
        goto L_0x049d;
    L_0x047b:
        r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyBroadcasts;
        if (r2 == 0) goto L_0x049d;
    L_0x047f:
        r2 = r0.notify_settings;
        r3 = r2.flags;
        r4 = 1;
        r3 = r3 & r4;
        if (r3 == 0) goto L_0x048e;
    L_0x0487:
        r2 = r2.show_previews;
        r3 = "EnablePreviewChannel";
        r1.putBoolean(r3, r2);
    L_0x048e:
        r0 = r0.notify_settings;
        r2 = r0.flags;
        r3 = 4;
        r2 = r2 & r3;
        if (r2 == 0) goto L_0x049d;
    L_0x0496:
        r0 = r0.mute_until;
        r2 = "EnableChannel2";
        r1.putInt(r2, r0);
    L_0x049d:
        r20 = r1;
        goto L_0x0848;
    L_0x04a1:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannel;
        if (r1 == 0) goto L_0x04fa;
    L_0x04a5:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateChannel) r0;
        r1 = r8.dialogs_dict;
        r2 = r0.channel_id;
        r2 = (long) r2;
        r2 = -r2;
        r1 = r1.get(r2);
        r1 = (org.telegram.tgnet.TLRPC.Dialog) r1;
        r2 = r0.channel_id;
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r8.getChat(r2);
        if (r2 == 0) goto L_0x04ed;
    L_0x04bf:
        if (r1 != 0) goto L_0x04d4;
    L_0x04c1:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channel;
        if (r3 == 0) goto L_0x04d4;
    L_0x04c5:
        r3 = r2.left;
        if (r3 != 0) goto L_0x04d4;
    L_0x04c9:
        r1 = org.telegram.messenger.Utilities.stageQueue;
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$wJ5JalVVW9yaNMkgKt1IKwZClNY;
        r2.<init>(r8, r0);
        r1.postRunnable(r2);
        goto L_0x04ed;
    L_0x04d4:
        r2 = r2.left;
        if (r2 == 0) goto L_0x04ed;
    L_0x04d8:
        if (r1 == 0) goto L_0x04ed;
    L_0x04da:
        r2 = r8.proxyDialog;
        if (r2 == 0) goto L_0x04e6;
    L_0x04de:
        r2 = r2.id;
        r4 = r1.id;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 == 0) goto L_0x04ed;
    L_0x04e6:
        r1 = r1.id;
        r3 = 0;
        r8.deleteDialog(r1, r3);
        goto L_0x04ee;
    L_0x04ed:
        r3 = 0;
    L_0x04ee:
        r1 = r22;
        r1 = r1 | 8192;
        r0 = r0.channel_id;
        r2 = 1;
        r8.loadFullChat(r0, r3, r2);
        goto L_0x0846;
    L_0x04fa:
        r1 = r22;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights;
        if (r2 == 0) goto L_0x0523;
    L_0x0500:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights) r0;
        r2 = r0.peer;
        r3 = r2.channel_id;
        if (r3 == 0) goto L_0x0509;
    L_0x0508:
        goto L_0x050b;
    L_0x0509:
        r3 = r2.chat_id;
    L_0x050b:
        r2 = java.lang.Integer.valueOf(r3);
        r2 = r8.getChat(r2);
        if (r2 == 0) goto L_0x0846;
    L_0x0515:
        r0 = r0.default_banned_rights;
        r2.default_banned_rights = r0;
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$-_A-zPdho0WiN66Q5g4H70UkDlI;
        r0.<init>(r8, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        goto L_0x0846;
    L_0x0523:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSets;
        if (r2 == 0) goto L_0x0534;
    L_0x0527:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateStickerSets) r0;
        r0 = r32.getMediaDataController();
        r2 = 0;
        r3 = 1;
        r0.loadStickers(r2, r2, r3);
        goto L_0x0846;
    L_0x0534:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
        if (r2 == 0) goto L_0x0547;
    L_0x0538:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder) r0;
        r2 = r32.getMediaDataController();
        r3 = r0.masks;
        r0 = r0.order;
        r2.reorderStickers(r3, r0);
        goto L_0x0846;
    L_0x0547:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
        if (r2 == 0) goto L_0x0557;
    L_0x054b:
        r0 = r32.getMediaDataController();
        r2 = 2;
        r3 = 0;
        r4 = 1;
        r0.loadRecents(r2, r3, r3, r4);
        goto L_0x0846;
    L_0x0557:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateContactsReset;
        if (r2 == 0) goto L_0x0564;
    L_0x055b:
        r0 = r32.getContactsController();
        r0.forceImportContacts();
        goto L_0x0846;
    L_0x0564:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
        if (r2 == 0) goto L_0x0575;
    L_0x0568:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNewStickerSet) r0;
        r2 = r32.getMediaDataController();
        r0 = r0.stickerset;
        r2.addNewStickerSet(r0);
        goto L_0x0846;
    L_0x0575:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
        if (r2 == 0) goto L_0x058c;
    L_0x0579:
        r0 = r8.emojiPreferences;
        r0 = r0.edit();
        r2 = "lastGifLoadTime";
        r6 = 0;
        r0 = r0.putLong(r2, r6);
        r0.commit();
        goto L_0x0846;
    L_0x058c:
        r6 = 0;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateRecentStickers;
        if (r2 == 0) goto L_0x05a3;
    L_0x0592:
        r0 = r8.emojiPreferences;
        r0 = r0.edit();
        r2 = "lastStickersLoadTime";
        r0 = r0.putLong(r2, r6);
        r0.commit();
        goto L_0x0846;
    L_0x05a3:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
        if (r2 == 0) goto L_0x05d1;
    L_0x05a7:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateDraftMessage) r0;
        r2 = r0.peer;
        r3 = r2.user_id;
        if (r3 == 0) goto L_0x05b3;
    L_0x05af:
        r2 = (long) r3;
    L_0x05b0:
        r22 = r2;
        goto L_0x05be;
    L_0x05b3:
        r3 = r2.channel_id;
        if (r3 == 0) goto L_0x05b9;
    L_0x05b7:
        r2 = -r3;
        goto L_0x05bc;
    L_0x05b9:
        r2 = r2.chat_id;
        r2 = -r2;
    L_0x05bc:
        r2 = (long) r2;
        goto L_0x05b0;
    L_0x05be:
        r21 = r32.getMediaDataController();
        r0 = r0.draft;
        r25 = 0;
        r26 = 1;
        r24 = r0;
        r21.saveDraft(r22, r24, r25, r26);
        r22 = r1;
        goto L_0x028e;
    L_0x05d1:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
        if (r2 == 0) goto L_0x05df;
    L_0x05d5:
        r0 = r32.getMediaDataController();
        r2 = 0;
        r0.markFaturedStickersAsRead(r2);
        goto L_0x0846;
    L_0x05df:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePhoneCall;
        if (r2 == 0) goto L_0x073e;
    L_0x05e3:
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePhoneCall) r0;
        r0 = r0.phone_call;
        r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance();
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x0619;
    L_0x05ef:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Received call in update: ";
        r3.append(r4);
        r3.append(r0);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.d(r3);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "call id ";
        r3.append(r4);
        r4 = r0.id;
        r3.append(r4);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x0619:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallRequested;
        if (r3 == 0) goto L_0x071a;
    L_0x061d:
        r3 = r0.date;
        r4 = r8.callRingTimeout;
        r4 = r4 / 1000;
        r3 = r3 + r4;
        r4 = r32.getConnectionsManager();
        r4 = r4.getCurrentTime();
        if (r3 >= r4) goto L_0x0639;
    L_0x062e:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0846;
    L_0x0632:
        r0 = "ignoring too old call";
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x0846;
    L_0x0639:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 21;
        if (r3 < r4) goto L_0x0656;
    L_0x063f:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = androidx.core.app.NotificationManagerCompat.from(r3);
        r3 = r3.areNotificationsEnabled();
        if (r3 != 0) goto L_0x0656;
    L_0x064b:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0846;
    L_0x064f:
        r0 = "Ignoring incoming call because notifications are disabled in system";
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x0846;
    L_0x0656:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = "phone";
        r3 = r3.getSystemService(r4);
        r3 = (android.telephony.TelephonyManager) r3;
        if (r2 != 0) goto L_0x06d0;
    L_0x0662:
        r2 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent;
        if (r2 != 0) goto L_0x06d0;
    L_0x0666:
        r2 = r3.getCallState();
        if (r2 == 0) goto L_0x066d;
    L_0x066c:
        goto L_0x06d0;
    L_0x066d:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0687;
    L_0x0671:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Starting service for call ";
        r2.append(r3);
        r3 = r0.id;
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x0687:
        org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent = r0;
        r2 = new android.content.Intent;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = org.telegram.messenger.voip.VoIPService.class;
        r2.<init>(r3, r4);
        r3 = "is_outgoing";
        r4 = 0;
        r2.putExtra(r3, r4);
        r3 = r0.participant_id;
        r4 = r32.getUserConfig();
        r4 = r4.getClientUserId();
        if (r3 != r4) goto L_0x06a7;
    L_0x06a4:
        r0 = r0.admin_id;
        goto L_0x06a9;
    L_0x06a7:
        r0 = r0.participant_id;
    L_0x06a9:
        r3 = "user_id";
        r2.putExtra(r3, r0);
        r0 = r8.currentAccount;
        r3 = "account";
        r2.putExtra(r3, r0);
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x06ca }
        r3 = 26;
        if (r0 < r3) goto L_0x06c3;
    L_0x06bc:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x06ca }
        r0.startForegroundService(r2);	 Catch:{ all -> 0x06ca }
        goto L_0x0846;
    L_0x06c3:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x06ca }
        r0.startService(r2);	 Catch:{ all -> 0x06ca }
        goto L_0x0846;
    L_0x06ca:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0846;
    L_0x06d0:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x06ef;
    L_0x06d4:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Auto-declining call ";
        r2.append(r3);
        r3 = r0.id;
        r2.append(r3);
        r3 = " because there's already active one";
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x06ef:
        r2 = new org.telegram.tgnet.TLRPC$TL_phone_discardCall;
        r2.<init>();
        r3 = new org.telegram.tgnet.TLRPC$TL_inputPhoneCall;
        r3.<init>();
        r2.peer = r3;
        r3 = r2.peer;
        r4 = r0.access_hash;
        r3.access_hash = r4;
        r4 = r0.id;
        r3.id = r4;
        r0 = new org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
        r0.<init>();
        r2.reason = r0;
        r0 = r32.getConnectionsManager();
        r3 = new org.telegram.messenger.-$$Lambda$MessagesController$KwSrP312VOB27NH519srqu8bVm0;
        r3.<init>(r8);
        r0.sendRequest(r2, r3);
        goto L_0x0846;
    L_0x071a:
        if (r2 == 0) goto L_0x0723;
    L_0x071c:
        if (r0 == 0) goto L_0x0723;
    L_0x071e:
        r2.onCallUpdated(r0);
        goto L_0x0846;
    L_0x0723:
        r2 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent;
        if (r2 == 0) goto L_0x0846;
    L_0x0727:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0730;
    L_0x072b:
        r2 = "Updated the call while the service is starting";
        org.telegram.messenger.FileLog.d(r2);
    L_0x0730:
        r2 = r0.id;
        r4 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent;
        r4 = r4.id;
        r15 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r15 != 0) goto L_0x0846;
    L_0x073a:
        org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent = r0;
        goto L_0x0846;
    L_0x073e:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark;
        if (r2 == 0) goto L_0x079f;
    L_0x0742:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark) r0;
        r2 = r0.peer;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer;
        if (r3 == 0) goto L_0x0760;
    L_0x074a:
        r2 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r2;
        r2 = r2.peer;
        r3 = r2.user_id;
        if (r3 == 0) goto L_0x0755;
    L_0x0752:
        r2 = (long) r3;
    L_0x0753:
        r4 = r2;
        goto L_0x0761;
    L_0x0755:
        r3 = r2.chat_id;
        if (r3 == 0) goto L_0x075b;
    L_0x0759:
        r2 = -r3;
        goto L_0x075e;
    L_0x075b:
        r2 = r2.channel_id;
        r2 = -r2;
    L_0x075e:
        r2 = (long) r2;
        goto L_0x0753;
    L_0x0760:
        r4 = r6;
    L_0x0761:
        r2 = r32.getMessagesStorage();
        r3 = r0.unread;
        r2.setDialogUnread(r4, r3);
        r2 = r8.dialogs_dict;
        r2 = r2.get(r4);
        r2 = (org.telegram.tgnet.TLRPC.Dialog) r2;
        if (r2 == 0) goto L_0x079a;
    L_0x0774:
        r3 = r2.unread_mark;
        r0 = r0.unread;
        if (r3 == r0) goto L_0x079a;
    L_0x077a:
        r2.unread_mark = r0;
        r0 = r2.unread_count;
        if (r0 != 0) goto L_0x0797;
    L_0x0780:
        r0 = r8.isDialogMuted(r4);
        if (r0 != 0) goto L_0x0797;
    L_0x0786:
        r0 = r2.unread_mark;
        if (r0 == 0) goto L_0x0791;
    L_0x078a:
        r0 = r8.unreadUnmutedDialogs;
        r2 = 1;
        r0 = r0 + r2;
        r8.unreadUnmutedDialogs = r0;
        goto L_0x0797;
    L_0x0791:
        r2 = 1;
        r0 = r8.unreadUnmutedDialogs;
        r0 = r0 - r2;
        r8.unreadUnmutedDialogs = r0;
    L_0x0797:
        r0 = r1 | 256;
        goto L_0x079b;
    L_0x079a:
        r0 = r1;
    L_0x079b:
        r22 = r0;
        goto L_0x0848;
    L_0x079f:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateMessagePoll;
        if (r2 == 0) goto L_0x07c6;
    L_0x07a3:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateMessagePoll) r0;
        r2 = r32.getNotificationCenter();
        r3 = org.telegram.messenger.NotificationCenter.didUpdatePollResults;
        r4 = 3;
        r5 = new java.lang.Object[r4];
        r6 = r0.poll_id;
        r4 = java.lang.Long.valueOf(r6);
        r6 = 0;
        r5[r6] = r4;
        r4 = r0.poll;
        r6 = 1;
        r5[r6] = r4;
        r0 = r0.results;
        r4 = 2;
        r5[r4] = r0;
        r2.postNotificationName(r3, r5);
        goto L_0x0846;
    L_0x07c6:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerSettings;
        if (r2 == 0) goto L_0x07e8;
    L_0x07ca:
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePeerSettings) r0;
        r2 = r0.peer;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_peerUser;
        if (r3 == 0) goto L_0x07d6;
    L_0x07d2:
        r2 = r2.user_id;
    L_0x07d4:
        r2 = (long) r2;
        goto L_0x07e1;
    L_0x07d6:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_peerChat;
        if (r3 == 0) goto L_0x07dd;
    L_0x07da:
        r2 = r2.chat_id;
        goto L_0x07df;
    L_0x07dd:
        r2 = r2.channel_id;
    L_0x07df:
        r2 = -r2;
        goto L_0x07d4;
    L_0x07e1:
        r0 = r0.settings;
        r4 = 1;
        r8.savePeerSettings(r2, r0, r4);
        goto L_0x0846;
    L_0x07e8:
        r4 = 1;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePeerLocated;
        if (r2 == 0) goto L_0x07fc;
    L_0x07ed:
        r2 = r32.getNotificationCenter();
        r3 = org.telegram.messenger.NotificationCenter.newPeopleNearbyAvailable;
        r5 = new java.lang.Object[r4];
        r4 = 0;
        r5[r4] = r0;
        r2.postNotificationName(r3, r5);
        goto L_0x0846;
    L_0x07fc:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateMessageReactions;
        if (r2 == 0) goto L_0x0835;
    L_0x0800:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateMessageReactions) r0;
        r2 = r0.peer;
        r3 = r2.chat_id;
        if (r3 == 0) goto L_0x080b;
    L_0x0808:
        r2 = -r3;
    L_0x0809:
        r2 = (long) r2;
        goto L_0x0813;
    L_0x080b:
        r3 = r2.channel_id;
        if (r3 == 0) goto L_0x0810;
    L_0x080f:
        goto L_0x0808;
    L_0x0810:
        r2 = r2.user_id;
        goto L_0x0809;
    L_0x0813:
        r4 = r32.getNotificationCenter();
        r5 = org.telegram.messenger.NotificationCenter.didUpdateReactions;
        r6 = 3;
        r7 = new java.lang.Object[r6];
        r2 = java.lang.Long.valueOf(r2);
        r3 = 0;
        r7[r3] = r2;
        r2 = r0.msg_id;
        r2 = java.lang.Integer.valueOf(r2);
        r3 = 1;
        r7[r3] = r2;
        r0 = r0.reactions;
        r2 = 2;
        r7[r2] = r0;
        r4.postNotificationName(r5, r7);
        goto L_0x0846;
    L_0x0835:
        r3 = 1;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateTheme;
        if (r2 == 0) goto L_0x0846;
    L_0x083a:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateTheme) r0;
        r0 = r0.theme;
        r0 = (org.telegram.tgnet.TLRPC.TL_theme) r0;
        r2 = r8.currentAccount;
        r4 = 0;
        org.telegram.ui.ActionBar.Theme.setThemeUploadInfo(r4, r4, r0, r2, r3);
    L_0x0846:
        r22 = r1;
    L_0x0848:
        r7 = r17 + 1;
        r15 = r43;
        r5 = r9;
        r6 = r16;
        r4 = r31;
        r3 = 0;
        r9 = r34;
        goto L_0x0028;
    L_0x0856:
        r31 = r4;
        r9 = r5;
        r1 = r22;
        if (r20 == 0) goto L_0x086d;
    L_0x085d:
        r20.commit();
        r0 = r32.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.notificationsSettingsUpdated;
        r3 = 0;
        r4 = new java.lang.Object[r3];
        r0.postNotificationName(r2, r4);
        goto L_0x086e;
    L_0x086d:
        r3 = 0;
    L_0x086e:
        r0 = r32.getMessagesStorage();
        r2 = r31;
        r4 = 1;
        r0.updateUsers(r2, r4, r4, r4);
        r0 = r32.getMessagesStorage();
        r0.updateUsers(r9, r3, r4, r4);
        goto L_0x0885;
    L_0x0880:
        r4 = 1;
        r1 = r33;
        r21 = 0;
    L_0x0885:
        if (r10 == 0) goto L_0x0963;
    L_0x0887:
        r0 = r32.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.didReceivedWebpagesInUpdates;
        r5 = new java.lang.Object[r4];
        r5[r3] = r10;
        r0.postNotificationName(r2, r5);
        r0 = 0;
        r2 = 2;
    L_0x0896:
        if (r0 >= r2) goto L_0x0963;
    L_0x0898:
        if (r0 != r4) goto L_0x089d;
    L_0x089a:
        r2 = r8.reloadingScheduledWebpagesPending;
        goto L_0x089f;
    L_0x089d:
        r2 = r8.reloadingWebpagesPending;
    L_0x089f:
        r3 = r35.size();
        r4 = 0;
    L_0x08a4:
        if (r4 >= r3) goto L_0x095b;
    L_0x08a6:
        r5 = r10.keyAt(r4);
        r7 = r2.get(r5);
        r7 = (java.util.ArrayList) r7;
        r2.remove(r5);
        if (r7 == 0) goto L_0x0951;
    L_0x08b5:
        r5 = r10.valueAt(r4);
        r5 = (org.telegram.tgnet.TLRPC.WebPage) r5;
        r6 = new java.util.ArrayList;
        r6.<init>();
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r9 != 0) goto L_0x08d3;
    L_0x08c4:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPageEmpty;
        if (r9 == 0) goto L_0x08c9;
    L_0x08c8:
        goto L_0x08d3;
    L_0x08c9:
        r9 = r5.id;
        r2.put(r9, r7);
        r20 = r2;
        r15 = 0;
        goto L_0x0915;
    L_0x08d3:
        r9 = r7.size();
        r10 = 0;
        r15 = 0;
    L_0x08da:
        if (r10 >= r9) goto L_0x0913;
    L_0x08dc:
        r17 = r7.get(r10);
        r20 = r2;
        r2 = r17;
        r2 = (org.telegram.messenger.MessageObject) r2;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2.webpage = r5;
        if (r10 != 0) goto L_0x0903;
    L_0x08ee:
        r2 = r7.get(r10);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r15 = r2.getDialogId();
        r2 = r7.get(r10);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r2 = r2.messageOwner;
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r2);
    L_0x0903:
        r2 = r7.get(r10);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r2 = r2.messageOwner;
        r6.add(r2);
        r10 = r10 + 1;
        r2 = r20;
        goto L_0x08da;
    L_0x0913:
        r20 = r2;
    L_0x0915:
        r2 = r6.isEmpty();
        if (r2 != 0) goto L_0x0953;
    L_0x091b:
        r22 = r32.getMessagesStorage();
        r24 = 1;
        r25 = 1;
        r26 = 0;
        r2 = r32.getDownloadController();
        r27 = r2.getAutodownloadMask();
        r2 = 1;
        if (r0 != r2) goto L_0x0933;
    L_0x0930:
        r28 = 1;
        goto L_0x0935;
    L_0x0933:
        r28 = 0;
    L_0x0935:
        r23 = r6;
        r22.putMessages(r23, r24, r25, r26, r27, r28);
        r2 = r32.getNotificationCenter();
        r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;
        r6 = 2;
        r9 = new java.lang.Object[r6];
        r6 = java.lang.Long.valueOf(r15);
        r10 = 0;
        r9[r10] = r6;
        r6 = 1;
        r9[r6] = r7;
        r2.postNotificationName(r5, r9);
        goto L_0x0953;
    L_0x0951:
        r20 = r2;
    L_0x0953:
        r4 = r4 + 1;
        r10 = r35;
        r2 = r20;
        goto L_0x08a4;
    L_0x095b:
        r0 = r0 + 1;
        r10 = r35;
        r2 = 2;
        r4 = 1;
        goto L_0x0896;
    L_0x0963:
        if (r11 == 0) goto L_0x097d;
    L_0x0965:
        r0 = r36.size();
        r2 = 0;
    L_0x096a:
        if (r2 >= r0) goto L_0x0983;
    L_0x096c:
        r3 = r11.keyAt(r2);
        r5 = r11.valueAt(r2);
        r5 = (java.util.ArrayList) r5;
        r6 = 0;
        r8.updateInterfaceWithMessages(r3, r5, r6);
        r2 = r2 + 1;
        goto L_0x096a;
    L_0x097d:
        if (r21 == 0) goto L_0x0985;
    L_0x097f:
        r2 = 0;
        r8.sortDialogs(r2);
    L_0x0983:
        r0 = 1;
        goto L_0x0986;
    L_0x0985:
        r0 = 0;
    L_0x0986:
        if (r12 == 0) goto L_0x09a0;
    L_0x0988:
        r2 = r37.size();
        r3 = 0;
    L_0x098d:
        if (r3 >= r2) goto L_0x09a0;
    L_0x098f:
        r4 = r12.keyAt(r3);
        r6 = r12.valueAt(r3);
        r6 = (java.util.ArrayList) r6;
        r7 = 1;
        r8.updateInterfaceWithMessages(r4, r6, r7);
        r3 = r3 + 1;
        goto L_0x098d;
    L_0x09a0:
        if (r13 == 0) goto L_0x0a51;
    L_0x09a2:
        r2 = r38.size();
        r3 = r0;
        r0 = 0;
    L_0x09a8:
        if (r0 >= r2) goto L_0x0a4e;
    L_0x09aa:
        r4 = r13.keyAt(r0);
        r6 = r13.valueAt(r0);
        r6 = (java.util.ArrayList) r6;
        r7 = r8.dialogMessage;
        r7 = r7.get(r4);
        r7 = (org.telegram.messenger.MessageObject) r7;
        if (r7 == 0) goto L_0x09ee;
    L_0x09be:
        r9 = r6.size();
        r10 = 0;
    L_0x09c3:
        if (r10 >= r9) goto L_0x09ee;
    L_0x09c5:
        r11 = r6.get(r10);
        r11 = (org.telegram.messenger.MessageObject) r11;
        r12 = r7.getId();
        r15 = r11.getId();
        if (r12 != r15) goto L_0x09f0;
    L_0x09d5:
        r3 = r8.dialogMessage;
        r3.put(r4, r11);
        r3 = r11.messageOwner;
        r3 = r3.to_id;
        if (r3 == 0) goto L_0x09ed;
    L_0x09e0:
        r3 = r3.channel_id;
        if (r3 != 0) goto L_0x09ed;
    L_0x09e4:
        r3 = r8.dialogMessagesByIds;
        r7 = r11.getId();
        r3.put(r7, r11);
    L_0x09ed:
        r3 = 1;
    L_0x09ee:
        r11 = 0;
        goto L_0x0a1e;
    L_0x09f0:
        r15 = r7.getDialogId();
        r20 = r11.getDialogId();
        r12 = (r15 > r20 ? 1 : (r15 == r20 ? 0 : -1));
        if (r12 != 0) goto L_0x0a1a;
    L_0x09fc:
        r12 = r7.messageOwner;
        r12 = r12.action;
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r12 == 0) goto L_0x0a1a;
    L_0x0a04:
        r12 = r7.replyMessageObject;
        if (r12 == 0) goto L_0x0a1a;
    L_0x0a08:
        r12 = r12.getId();
        r15 = r11.getId();
        if (r12 != r15) goto L_0x0a1a;
    L_0x0a12:
        r7.replyMessageObject = r11;
        r11 = 0;
        r7.generatePinMessageText(r11, r11);
        r3 = 1;
        goto L_0x0a1e;
    L_0x0a1a:
        r11 = 0;
        r10 = r10 + 1;
        goto L_0x09c3;
    L_0x0a1e:
        r20 = r32.getMediaDataController();
        r24 = 0;
        r25 = 0;
        r21 = r6;
        r22 = r4;
        r20.loadReplyMessagesForMessages(r21, r22, r24, r25);
        r7 = r32.getNotificationCenter();
        r9 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;
        r10 = 3;
        r12 = new java.lang.Object[r10];
        r4 = java.lang.Long.valueOf(r4);
        r5 = 0;
        r12[r5] = r4;
        r4 = 1;
        r12[r4] = r6;
        r4 = java.lang.Boolean.valueOf(r5);
        r6 = 2;
        r12[r6] = r4;
        r7.postNotificationName(r9, r12);
        r0 = r0 + 1;
        goto L_0x09a8;
    L_0x0a4e:
        r5 = 0;
        r0 = r3;
        goto L_0x0a52;
    L_0x0a51:
        r5 = 0;
    L_0x0a52:
        if (r0 == 0) goto L_0x0a5f;
    L_0x0a54:
        r0 = r32.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r3 = new java.lang.Object[r5];
        r0.postNotificationName(r2, r3);
    L_0x0a5f:
        if (r39 == 0) goto L_0x0a63;
    L_0x0a61:
        r1 = r1 | 64;
    L_0x0a63:
        if (r40 == 0) goto L_0x0a69;
    L_0x0a65:
        r0 = r1 | 1;
        r1 = r0 | 128;
    L_0x0a69:
        if (r14 == 0) goto L_0x0a82;
    L_0x0a6b:
        r0 = r41.size();
        r2 = 0;
    L_0x0a70:
        if (r2 >= r0) goto L_0x0a82;
    L_0x0a72:
        r3 = r14.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.ChatParticipants) r3;
        r4 = r32.getMessagesStorage();
        r4.updateChatParticipants(r3);
        r2 = r2 + 1;
        goto L_0x0a70;
    L_0x0a82:
        if (r42 == 0) goto L_0x0a94;
    L_0x0a84:
        r0 = r32.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.didUpdatedMessagesViews;
        r3 = 1;
        r4 = new java.lang.Object[r3];
        r5 = 0;
        r4[r5] = r42;
        r0.postNotificationName(r2, r4);
        goto L_0x0a96;
    L_0x0a94:
        r3 = 1;
        r5 = 0;
    L_0x0a96:
        if (r1 == 0) goto L_0x0aa9;
    L_0x0a98:
        r0 = r32.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.updateInterfaces;
        r3 = new java.lang.Object[r3];
        r1 = java.lang.Integer.valueOf(r1);
        r3[r5] = r1;
        r0.postNotificationName(r2, r3);
    L_0x0aa9:
        r1 = r43;
        if (r1 == 0) goto L_0x0ab4;
    L_0x0aad:
        r0 = org.telegram.messenger.ImageLoader.getInstance();
        r0.putThumbsToCache(r1);
    L_0x0ab4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processUpdateArray$268$MessagesController(int, java.util.ArrayList, android.util.LongSparseArray, android.util.LongSparseArray, android.util.LongSparseArray, android.util.LongSparseArray, boolean, java.util.ArrayList, java.util.ArrayList, android.util.SparseArray, java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$null$264$MessagesController(User user) {
        getContactsController().addContactToPhoneBook(user, true);
    }

    public /* synthetic */ void lambda$null$265$MessagesController(TL_updateChannel tL_updateChannel) {
        getChannelDifference(tL_updateChannel.channel_id, 1, 0, null);
    }

    public /* synthetic */ void lambda$null$266$MessagesController(Chat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    public /* synthetic */ void lambda$null$267$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$processUpdateArray$270$MessagesController(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2, SparseIntArray sparseIntArray2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$TFMHyEQhvK3azU3au9tRhwnpNmA(this, sparseLongArray, sparseLongArray2, sparseIntArray, arrayList, sparseArray, sparseArray2, sparseIntArray2));
    }

    public /* synthetic */ void lambda$null$269$MessagesController(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2, SparseIntArray sparseIntArray2) {
        int size;
        int i;
        int i2;
        int valueAt;
        int size2;
        MessageObject messageObject;
        long j;
        ArrayList arrayList2;
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        SparseIntArray sparseIntArray3 = sparseIntArray;
        SparseArray sparseArray3 = sparseArray;
        SparseArray sparseArray4 = sparseArray2;
        SparseIntArray sparseIntArray4 = sparseIntArray2;
        int i3 = 0;
        if (!(sparseLongArray3 == null && sparseLongArray4 == null)) {
            int keyAt;
            getNotificationCenter().postNotificationName(NotificationCenter.messagesRead, sparseLongArray3, sparseLongArray4);
            if (sparseLongArray3 != null) {
                getNotificationsController().processReadMessages(sparseLongArray, 0, 0, 0, false);
                Editor edit = this.notificationsPreferences.edit();
                size = sparseLongArray.size();
                i = 0;
                for (i2 = 0; i2 < size; i2++) {
                    keyAt = sparseLongArray3.keyAt(i2);
                    valueAt = (int) sparseLongArray3.valueAt(i2);
                    Dialog dialog = (Dialog) this.dialogs_dict.get((long) keyAt);
                    if (dialog != null) {
                        int i4 = dialog.top_message;
                        if (i4 > 0 && i4 <= valueAt) {
                            MessageObject messageObject2 = (MessageObject) this.dialogMessage.get(dialog.id);
                            if (!(messageObject2 == null || messageObject2.isOut())) {
                                messageObject2.setIsRead();
                                i |= 256;
                            }
                        }
                    }
                    if (keyAt != getUserConfig().getClientUserId()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("diditem");
                        stringBuilder.append(keyAt);
                        edit.remove(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("diditemo");
                        stringBuilder.append(keyAt);
                        edit.remove(stringBuilder.toString());
                    }
                }
                edit.commit();
                i3 = i;
            } else {
                i3 = 0;
            }
            if (sparseLongArray4 != null) {
                size2 = sparseLongArray2.size();
                for (size = 0; size < size2; size++) {
                    keyAt = (int) sparseLongArray4.valueAt(size);
                    Dialog dialog2 = (Dialog) this.dialogs_dict.get((long) sparseLongArray4.keyAt(size));
                    if (dialog2 != null) {
                        i = dialog2.top_message;
                        if (i > 0 && i <= keyAt) {
                            messageObject = (MessageObject) this.dialogMessage.get(dialog2.id);
                            if (messageObject != null && messageObject.isOut()) {
                                messageObject.setIsRead();
                                i3 |= 256;
                            }
                        }
                    }
                }
            }
        }
        if (sparseIntArray3 != null) {
            size2 = sparseIntArray.size();
            for (size = 0; size < size2; size++) {
                i2 = sparseIntArray3.keyAt(size);
                i = sparseIntArray3.valueAt(size);
                getNotificationCenter().postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(i2), Integer.valueOf(i));
                j = ((long) i2) << 32;
                if (((Dialog) this.dialogs_dict.get(j)) != null) {
                    messageObject = (MessageObject) this.dialogMessage.get(j);
                    if (messageObject != null && messageObject.messageOwner.date <= i) {
                        messageObject.setIsRead();
                        i3 |= 256;
                    }
                }
            }
        }
        if (arrayList != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, arrayList);
        }
        if (sparseArray3 != null) {
            size = sparseArray.size();
            for (i2 = 0; i2 < size; i2++) {
                i = sparseArray3.keyAt(i2);
                arrayList2 = (ArrayList) sparseArray3.valueAt(i2);
                if (arrayList2 != null) {
                    getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList2, Integer.valueOf(i), Boolean.valueOf(false));
                    int i5;
                    if (i == 0) {
                        i = arrayList2.size();
                        for (i5 = 0; i5 < i; i5++) {
                            MessageObject messageObject3 = (MessageObject) this.dialogMessagesByIds.get(((Integer) arrayList2.get(i5)).intValue());
                            if (messageObject3 != null) {
                                messageObject3.deleted = true;
                            }
                        }
                    } else {
                        MessageObject messageObject4 = (MessageObject) this.dialogMessage.get((long) (-i));
                        if (messageObject4 != null) {
                            i5 = arrayList2.size();
                            for (valueAt = 0; valueAt < i5; valueAt++) {
                                if (messageObject4.getId() == ((Integer) arrayList2.get(valueAt)).intValue()) {
                                    messageObject4.deleted = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            getNotificationsController().removeDeletedMessagesFromNotifications(sparseArray3);
        }
        if (sparseArray4 != null) {
            size = sparseArray2.size();
            for (i2 = 0; i2 < size; i2++) {
                i = sparseArray4.keyAt(i2);
                if (((ArrayList) sparseArray4.valueAt(i2)) != null) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList2, Integer.valueOf(i), Boolean.valueOf(true));
                }
            }
        }
        if (sparseIntArray4 != null) {
            size2 = sparseIntArray2.size();
            for (size = 0; size < size2; size++) {
                i2 = sparseIntArray4.keyAt(size);
                i = sparseIntArray4.valueAt(size);
                j = (long) (-i2);
                getNotificationCenter().postNotificationName(NotificationCenter.historyCleared, Long.valueOf(j), Integer.valueOf(i));
                messageObject = (MessageObject) this.dialogMessage.get(j);
                if (messageObject != null && messageObject.getId() <= i) {
                    messageObject.deleted = true;
                    break;
                }
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(j);
        int i = sharedPreferences.getInt(stringBuilder.toString(), -1);
        if (i == -1) {
            return getNotificationsController().isGlobalNotificationsEnabled(j) ^ 1;
        }
        if (i == 2) {
            return true;
        }
        if (i == 3) {
            sharedPreferences = this.notificationsPreferences;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("notifyuntil_");
            stringBuilder2.append(j);
            if (sharedPreferences.getInt(stringBuilder2.toString(), 0) >= getConnectionsManager().getCurrentTime()) {
                return true;
            }
        }
        return false;
    }

    private boolean updatePrintingUsersWithNewMessages(long j, ArrayList<MessageObject> arrayList) {
        if (j > 0) {
            if (((ArrayList) this.printingUsers.get(Long.valueOf(j))) != null) {
                this.printingUsers.remove(Long.valueOf(j));
                return true;
            }
        } else if (j < 0) {
            Object obj;
            ArrayList arrayList2 = new ArrayList();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject messageObject = (MessageObject) it.next();
                if (!arrayList2.contains(Integer.valueOf(messageObject.messageOwner.from_id))) {
                    arrayList2.add(Integer.valueOf(messageObject.messageOwner.from_id));
                }
            }
            ArrayList arrayList3 = (ArrayList) this.printingUsers.get(Long.valueOf(j));
            if (arrayList3 != null) {
                int i = 0;
                obj = null;
                while (i < arrayList3.size()) {
                    if (arrayList2.contains(Integer.valueOf(((PrintingUser) arrayList3.get(i)).userId))) {
                        arrayList3.remove(i);
                        i--;
                        if (arrayList3.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(j));
                        }
                        obj = 1;
                    }
                    i++;
                }
            } else {
                obj = null;
            }
            if (obj != null) {
                return true;
            }
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0315  */
    public void updateInterfaceWithMessages(long r27, java.util.ArrayList<org.telegram.messenger.MessageObject> r29, boolean r30) {
        /*
        r26 = this;
        r0 = r26;
        r7 = r27;
        r9 = r29;
        if (r9 == 0) goto L_0x031c;
    L_0x0008:
        r1 = r29.isEmpty();
        if (r1 == 0) goto L_0x0010;
    L_0x000e:
        goto L_0x031c;
    L_0x0010:
        r1 = (int) r7;
        r11 = 0;
        if (r1 != 0) goto L_0x0016;
    L_0x0014:
        r1 = 1;
        goto L_0x0017;
    L_0x0016:
        r1 = 0;
    L_0x0017:
        if (r30 != 0) goto L_0x00d5;
    L_0x0019:
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
    L_0x001e:
        r13 = r29.size();
        if (r2 >= r13) goto L_0x00d0;
    L_0x0024:
        r13 = r9.get(r2);
        r13 = (org.telegram.messenger.MessageObject) r13;
        if (r3 == 0) goto L_0x005a;
    L_0x002c:
        if (r1 != 0) goto L_0x0038;
    L_0x002e:
        r14 = r13.getId();
        r15 = r3.getId();
        if (r14 > r15) goto L_0x005a;
    L_0x0038:
        if (r1 != 0) goto L_0x0046;
    L_0x003a:
        r14 = r13.getId();
        if (r14 >= 0) goto L_0x0050;
    L_0x0040:
        r14 = r3.getId();
        if (r14 >= 0) goto L_0x0050;
    L_0x0046:
        r14 = r13.getId();
        r15 = r3.getId();
        if (r14 < r15) goto L_0x005a;
    L_0x0050:
        r14 = r13.messageOwner;
        r14 = r14.date;
        r15 = r3.messageOwner;
        r15 = r15.date;
        if (r14 <= r15) goto L_0x0064;
    L_0x005a:
        r3 = r13.messageOwner;
        r3 = r3.to_id;
        r3 = r3.channel_id;
        if (r3 == 0) goto L_0x0063;
    L_0x0062:
        r5 = r3;
    L_0x0063:
        r3 = r13;
    L_0x0064:
        if (r4 != 0) goto L_0x006d;
    L_0x0066:
        r14 = r13.isOut();
        if (r14 != 0) goto L_0x006d;
    L_0x006c:
        r4 = 1;
    L_0x006d:
        r14 = r13.isOut();
        if (r14 == 0) goto L_0x00bf;
    L_0x0073:
        r14 = r13.isSending();
        if (r14 != 0) goto L_0x00bf;
    L_0x0079:
        r14 = r13.isForwarded();
        if (r14 != 0) goto L_0x00bf;
    L_0x007f:
        r14 = r13.isNewGif();
        if (r14 == 0) goto L_0x0095;
    L_0x0085:
        r14 = r26.getMediaDataController();
        r15 = r13.messageOwner;
        r12 = r15.media;
        r12 = r12.document;
        r15 = r15.date;
        r14.addRecentGif(r12, r15);
        goto L_0x00bf;
    L_0x0095:
        r12 = r13.isAnimatedEmoji();
        if (r12 != 0) goto L_0x00bf;
    L_0x009b:
        r12 = r13.isSticker();
        if (r12 != 0) goto L_0x00a7;
    L_0x00a1:
        r12 = r13.isAnimatedSticker();
        if (r12 == 0) goto L_0x00bf;
    L_0x00a7:
        r14 = r26.getMediaDataController();
        r15 = 0;
        r12 = r13.messageOwner;
        r10 = r12.media;
        r10 = r10.document;
        r12 = r12.date;
        r19 = 0;
        r16 = r13;
        r17 = r10;
        r18 = r12;
        r14.addRecentSticker(r15, r16, r17, r18, r19);
    L_0x00bf:
        r10 = r13.isOut();
        if (r10 == 0) goto L_0x00cc;
    L_0x00c5:
        r10 = r13.isSent();
        if (r10 == 0) goto L_0x00cc;
    L_0x00cb:
        r6 = 1;
    L_0x00cc:
        r2 = r2 + 1;
        goto L_0x001e;
    L_0x00d0:
        r10 = r3;
        r12 = r4;
        r13 = r5;
        r14 = r6;
        goto L_0x00d9;
    L_0x00d5:
        r10 = 0;
        r12 = 0;
        r13 = 0;
        r14 = 0;
    L_0x00d9:
        r1 = r26.getMediaDataController();
        r6 = 0;
        r2 = r29;
        r3 = r27;
        r5 = r30;
        r1.loadReplyMessagesForMessages(r2, r3, r5, r6);
        r1 = r26.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages;
        r3 = 3;
        r3 = new java.lang.Object[r3];
        r4 = java.lang.Long.valueOf(r27);
        r3[r11] = r4;
        r4 = 1;
        r3[r4] = r9;
        r4 = 2;
        r5 = java.lang.Boolean.valueOf(r30);
        r3[r4] = r5;
        r1.postNotificationName(r2, r3);
        if (r10 == 0) goto L_0x031c;
    L_0x0105:
        if (r30 == 0) goto L_0x0109;
    L_0x0107:
        goto L_0x031c;
    L_0x0109:
        r1 = r0.dialogs_dict;
        r1 = r1.get(r7);
        r1 = (org.telegram.tgnet.TLRPC.TL_dialog) r1;
        r2 = r10.messageOwner;
        r2 = r2.action;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        r3 = 0;
        if (r2 == 0) goto L_0x01bd;
    L_0x011b:
        if (r1 == 0) goto L_0x01bc;
    L_0x011d:
        r2 = r0.allDialogs;
        r2.remove(r1);
        r2 = r0.dialogsServerOnly;
        r2.remove(r1);
        r2 = r0.dialogsCanAddUsers;
        r2.remove(r1);
        r2 = r0.dialogsChannelsOnly;
        r2.remove(r1);
        r2 = r0.dialogsGroupsOnly;
        r2.remove(r1);
        r2 = r0.dialogsUsersOnly;
        r2.remove(r1);
        r2 = r0.dialogsForward;
        r2.remove(r1);
        r2 = r0.dialogs_dict;
        r5 = r1.id;
        r2.remove(r5);
        r2 = r0.dialogs_read_inbox_max;
        r5 = r1.id;
        r5 = java.lang.Long.valueOf(r5);
        r2.remove(r5);
        r2 = r0.dialogs_read_outbox_max;
        r5 = r1.id;
        r5 = java.lang.Long.valueOf(r5);
        r2.remove(r5);
        r2 = r0.nextDialogsCacheOffset;
        r5 = r1.folder_id;
        r2 = r2.get(r5, r11);
        if (r2 <= 0) goto L_0x0170;
    L_0x0167:
        r5 = r0.nextDialogsCacheOffset;
        r6 = r1.folder_id;
        r7 = 1;
        r2 = r2 - r7;
        r5.put(r6, r2);
    L_0x0170:
        r2 = r0.dialogMessage;
        r5 = r1.id;
        r2.remove(r5);
        r2 = r0.dialogsByFolder;
        r5 = r1.folder_id;
        r2 = r2.get(r5);
        r2 = (java.util.ArrayList) r2;
        if (r2 == 0) goto L_0x0186;
    L_0x0183:
        r2.remove(r1);
    L_0x0186:
        r2 = r0.dialogMessagesByIds;
        r5 = r1.top_message;
        r2 = r2.get(r5);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r5 = r0.dialogMessagesByIds;
        r6 = r1.top_message;
        r5.remove(r6);
        if (r2 == 0) goto L_0x01a6;
    L_0x0199:
        r2 = r2.messageOwner;
        r5 = r2.random_id;
        r2 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
        if (r2 == 0) goto L_0x01a6;
    L_0x01a1:
        r2 = r0.dialogMessagesByRandomIds;
        r2.remove(r5);
    L_0x01a6:
        r1.top_message = r11;
        r2 = r26.getNotificationsController();
        r3 = r1.id;
        r2.removeNotificationsForDialog(r3);
        r1 = r26.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.needReloadRecentDialogsSearch;
        r3 = new java.lang.Object[r11];
        r1.postNotificationName(r2, r3);
    L_0x01bc:
        return;
    L_0x01bd:
        if (r1 != 0) goto L_0x025c;
    L_0x01bf:
        r1 = java.lang.Integer.valueOf(r13);
        r1 = r0.getChat(r1);
        if (r13 == 0) goto L_0x01cb;
    L_0x01c9:
        if (r1 == 0) goto L_0x01d3;
    L_0x01cb:
        if (r1 == 0) goto L_0x01d4;
    L_0x01cd:
        r2 = org.telegram.messenger.ChatObject.isNotInChat(r1);
        if (r2 == 0) goto L_0x01d4;
    L_0x01d3:
        return;
    L_0x01d4:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0208;
    L_0x01d8:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r5 = "not found dialog with id ";
        r2.append(r5);
        r2.append(r7);
        r5 = " dictCount = ";
        r2.append(r5);
        r5 = r0.dialogs_dict;
        r5 = r5.size();
        r2.append(r5);
        r5 = " allCount = ";
        r2.append(r5);
        r5 = r0.allDialogs;
        r5 = r5.size();
        r2.append(r5);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x0208:
        r2 = new org.telegram.tgnet.TLRPC$TL_dialog;
        r2.<init>();
        r2.id = r7;
        r5 = r10.getId();
        r2.top_message = r5;
        r5 = r10.messageOwner;
        r5 = r5.date;
        r2.last_message_date = r5;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        r2.flags = r1;
        r1 = r0.dialogs_dict;
        r1.put(r7, r2);
        r1 = r0.allDialogs;
        r1.add(r2);
        r1 = r0.dialogMessage;
        r1.put(r7, r10);
        r1 = r10.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 != 0) goto L_0x024e;
    L_0x0238:
        r1 = r0.dialogMessagesByIds;
        r5 = r10.getId();
        r1.put(r5, r10);
        r1 = r10.messageOwner;
        r5 = r1.random_id;
        r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
        if (r1 == 0) goto L_0x024e;
    L_0x0249:
        r1 = r0.dialogMessagesByRandomIds;
        r1.put(r5, r10);
    L_0x024e:
        r1 = r26.getMessagesStorage();
        r3 = new org.telegram.messenger.-$$Lambda$MessagesController$Ji4MTx4hnSHDwbZ4kj_JP_rllDU;
        r3.<init>(r0, r2, r7);
        r1.getDialogFolderId(r7, r3);
        goto L_0x030c;
    L_0x025c:
        if (r12 == 0) goto L_0x0284;
    L_0x025e:
        r2 = r1.folder_id;
        r5 = 1;
        if (r2 != r5) goto L_0x0285;
    L_0x0263:
        r12 = r1.id;
        r2 = r0.isDialogMuted(r12);
        if (r2 != 0) goto L_0x0285;
    L_0x026b:
        r1.folder_id = r11;
        r1.pinned = r11;
        r1.pinnedNum = r11;
        r20 = r26.getMessagesStorage();
        r21 = 0;
        r22 = 0;
        r11 = r1.id;
        r25 = 0;
        r23 = r11;
        r20.setDialogsFolderId(r21, r22, r23, r25);
        r11 = 1;
        goto L_0x0285;
    L_0x0284:
        r5 = 1;
    L_0x0285:
        r2 = r1.top_message;
        if (r2 <= 0) goto L_0x0297;
    L_0x0289:
        r2 = r10.getId();
        if (r2 <= 0) goto L_0x0297;
    L_0x028f:
        r2 = r10.getId();
        r6 = r1.top_message;
        if (r2 > r6) goto L_0x02bd;
    L_0x0297:
        r2 = r1.top_message;
        if (r2 >= 0) goto L_0x02a9;
    L_0x029b:
        r2 = r10.getId();
        if (r2 >= 0) goto L_0x02a9;
    L_0x02a1:
        r2 = r10.getId();
        r6 = r1.top_message;
        if (r2 < r6) goto L_0x02bd;
    L_0x02a9:
        r2 = r0.dialogMessage;
        r2 = r2.indexOfKey(r7);
        if (r2 < 0) goto L_0x02bd;
    L_0x02b1:
        r2 = r1.top_message;
        if (r2 < 0) goto L_0x02bd;
    L_0x02b5:
        r2 = r1.last_message_date;
        r6 = r10.messageOwner;
        r6 = r6.date;
        if (r2 > r6) goto L_0x030d;
    L_0x02bd:
        r2 = r0.dialogMessagesByIds;
        r6 = r1.top_message;
        r2 = r2.get(r6);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r6 = r0.dialogMessagesByIds;
        r9 = r1.top_message;
        r6.remove(r9);
        if (r2 == 0) goto L_0x02dd;
    L_0x02d0:
        r2 = r2.messageOwner;
        r11 = r2.random_id;
        r2 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1));
        if (r2 == 0) goto L_0x02dd;
    L_0x02d8:
        r2 = r0.dialogMessagesByRandomIds;
        r2.remove(r11);
    L_0x02dd:
        r2 = r10.getId();
        r1.top_message = r2;
        r2 = r10.messageOwner;
        r2 = r2.date;
        r1.last_message_date = r2;
        r1 = r0.dialogMessage;
        r1.put(r7, r10);
        r1 = r10.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 != 0) goto L_0x030c;
    L_0x02f6:
        r1 = r0.dialogMessagesByIds;
        r2 = r10.getId();
        r1.put(r2, r10);
        r1 = r10.messageOwner;
        r1 = r1.random_id;
        r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r6 == 0) goto L_0x030c;
    L_0x0307:
        r3 = r0.dialogMessagesByRandomIds;
        r3.put(r1, r10);
    L_0x030c:
        r11 = 1;
    L_0x030d:
        if (r11 == 0) goto L_0x0313;
    L_0x030f:
        r1 = 0;
        r0.sortDialogs(r1);
    L_0x0313:
        if (r14 == 0) goto L_0x031c;
    L_0x0315:
        r1 = r26.getMediaDataController();
        r1.increasePeerRaiting(r7);
    L_0x031c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updateInterfaceWithMessages(long, java.util.ArrayList, boolean):void");
    }

    public /* synthetic */ void lambda$updateInterfaceWithMessages$273$MessagesController(Dialog dialog, long j, int i) {
        if (i == -1) {
            int i2 = (int) j;
            if (i2 != 0) {
                loadUnknownDialog(getInputPeer(i2), 0);
            }
        } else if (i != 0) {
            dialog.folder_id = i;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
        }
    }

    public void addDialogAction(long j, boolean z) {
        Dialog dialog = (Dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            if (z) {
                this.clearingHistoryDialogs.put(j, dialog);
            } else {
                this.deletingDialogs.put(j, dialog);
                this.allDialogs.remove(dialog);
                sortDialogs(null);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
        }
    }

    public void removeDialogAction(long j, boolean z, boolean z2) {
        Dialog dialog = (Dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            if (z) {
                this.clearingHistoryDialogs.remove(j);
            } else {
                this.deletingDialogs.remove(j);
                if (!z2) {
                    this.allDialogs.add(dialog);
                    sortDialogs(null);
                }
            }
            if (!z2) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
            }
        }
    }

    public boolean isClearingDialog(long j) {
        return this.clearingHistoryDialogs.get(j) != null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0116  */
    /* JADX WARNING: Missing block: B:33:0x00b7, code skipped:
            if (r10.add_admins != false) goto L_0x00bd;
     */
    /* JADX WARNING: Missing block: B:35:0x00bb, code skipped:
            if (r8.creator != false) goto L_0x00bd;
     */
    public void sortDialogs(android.util.SparseArray<org.telegram.tgnet.TLRPC.Chat> r14) {
        /*
        r13 = this;
        r0 = r13.dialogsServerOnly;
        r0.clear();
        r0 = r13.dialogsCanAddUsers;
        r0.clear();
        r0 = r13.dialogsChannelsOnly;
        r0.clear();
        r0 = r13.dialogsGroupsOnly;
        r0.clear();
        r0 = r13.dialogsUsersOnly;
        r0.clear();
        r0 = r13.dialogsForward;
        r0.clear();
        r0 = 0;
        r1 = 0;
    L_0x0020:
        r2 = r13.dialogsByFolder;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x0038;
    L_0x0028:
        r2 = r13.dialogsByFolder;
        r2 = r2.valueAt(r1);
        r2 = (java.util.ArrayList) r2;
        if (r2 == 0) goto L_0x0035;
    L_0x0032:
        r2.clear();
    L_0x0035:
        r1 = r1 + 1;
        goto L_0x0020;
    L_0x0038:
        r13.unreadUnmutedDialogs = r0;
        r1 = r13.getUserConfig();
        r1 = r1.getClientUserId();
        r2 = r13.allDialogs;
        r3 = r13.dialogComparator;
        java.util.Collections.sort(r2, r3);
        r2 = 1;
        r13.isLeftProxyChannel = r2;
        r3 = r13.proxyDialog;
        if (r3 == 0) goto L_0x006a;
    L_0x0050:
        r3 = r3.id;
        r5 = 0;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 >= 0) goto L_0x006a;
    L_0x0058:
        r4 = (int) r3;
        r3 = -r4;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r13.getChat(r3);
        if (r3 == 0) goto L_0x006a;
    L_0x0064:
        r3 = r3.left;
        if (r3 != 0) goto L_0x006a;
    L_0x0068:
        r13.isLeftProxyChannel = r0;
    L_0x006a:
        r3 = r13.getNotificationsController();
        r3 = r3.showBadgeMessages;
        r4 = r13.allDialogs;
        r4 = r4.size();
        r5 = r4;
        r4 = 0;
        r6 = 0;
    L_0x0079:
        if (r4 >= r5) goto L_0x0158;
    L_0x007b:
        r7 = r13.allDialogs;
        r7 = r7.get(r4);
        r7 = (org.telegram.tgnet.TLRPC.Dialog) r7;
        r8 = r7.id;
        r10 = 32;
        r10 = r8 >> r10;
        r11 = (int) r10;
        r9 = (int) r8;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_dialog;
        if (r8 == 0) goto L_0x0122;
    L_0x008f:
        if (r9 == 0) goto L_0x010d;
    L_0x0091:
        if (r11 == r2) goto L_0x010d;
    L_0x0093:
        r8 = r13.dialogsServerOnly;
        r8.add(r7);
        r8 = org.telegram.messenger.DialogObject.isChannel(r7);
        if (r8 == 0) goto L_0x00e2;
    L_0x009e:
        r8 = -r9;
        r8 = java.lang.Integer.valueOf(r8);
        r8 = r13.getChat(r8);
        if (r8 == 0) goto L_0x00c2;
    L_0x00a9:
        r10 = r8.megagroup;
        if (r10 == 0) goto L_0x00b9;
    L_0x00ad:
        r10 = r8.admin_rights;
        if (r10 == 0) goto L_0x00b9;
    L_0x00b1:
        r11 = r10.post_messages;
        if (r11 != 0) goto L_0x00bd;
    L_0x00b5:
        r10 = r10.add_admins;
        if (r10 != 0) goto L_0x00bd;
    L_0x00b9:
        r10 = r8.creator;
        if (r10 == 0) goto L_0x00c2;
    L_0x00bd:
        r10 = r13.dialogsCanAddUsers;
        r10.add(r7);
    L_0x00c2:
        if (r8 == 0) goto L_0x00ce;
    L_0x00c4:
        r10 = r8.megagroup;
        if (r10 == 0) goto L_0x00ce;
    L_0x00c8:
        r8 = r13.dialogsGroupsOnly;
        r8.add(r7);
        goto L_0x010d;
    L_0x00ce:
        r10 = r13.dialogsChannelsOnly;
        r10.add(r7);
        r10 = org.telegram.messenger.ChatObject.hasAdminRights(r8);
        if (r10 == 0) goto L_0x00e0;
    L_0x00d9:
        r8 = org.telegram.messenger.ChatObject.canPost(r8);
        if (r8 == 0) goto L_0x00e0;
    L_0x00df:
        goto L_0x010d;
    L_0x00e0:
        r8 = 0;
        goto L_0x010e;
    L_0x00e2:
        if (r9 >= 0) goto L_0x0104;
    L_0x00e4:
        if (r14 == 0) goto L_0x00f9;
    L_0x00e6:
        r8 = -r9;
        r8 = r14.get(r8);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        if (r8 == 0) goto L_0x00f9;
    L_0x00ef:
        r8 = r8.migrated_to;
        if (r8 == 0) goto L_0x00f9;
    L_0x00f3:
        r7 = r13.allDialogs;
        r7.remove(r4);
        goto L_0x014c;
    L_0x00f9:
        r8 = r13.dialogsCanAddUsers;
        r8.add(r7);
        r8 = r13.dialogsGroupsOnly;
        r8.add(r7);
        goto L_0x010d;
    L_0x0104:
        if (r9 <= 0) goto L_0x010d;
    L_0x0106:
        if (r9 == r1) goto L_0x010d;
    L_0x0108:
        r8 = r13.dialogsUsersOnly;
        r8.add(r7);
    L_0x010d:
        r8 = 1;
    L_0x010e:
        if (r8 == 0) goto L_0x0122;
    L_0x0110:
        r8 = r7.folder_id;
        if (r8 != 0) goto L_0x0122;
    L_0x0114:
        if (r9 != r1) goto L_0x011d;
    L_0x0116:
        r6 = r13.dialogsForward;
        r6.add(r0, r7);
        r6 = 1;
        goto L_0x0122;
    L_0x011d:
        r8 = r13.dialogsForward;
        r8.add(r7);
    L_0x0122:
        r8 = r7.unread_count;
        if (r8 != 0) goto L_0x012a;
    L_0x0126:
        r8 = r7.unread_mark;
        if (r8 == 0) goto L_0x0137;
    L_0x012a:
        r8 = r7.id;
        r8 = r13.isDialogMuted(r8);
        if (r8 != 0) goto L_0x0137;
    L_0x0132:
        r8 = r13.unreadUnmutedDialogs;
        r8 = r8 + r2;
        r13.unreadUnmutedDialogs = r8;
    L_0x0137:
        r8 = r13.proxyDialog;
        if (r8 == 0) goto L_0x0151;
    L_0x013b:
        r9 = r7.id;
        r11 = r8.id;
        r8 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r8 != 0) goto L_0x0151;
    L_0x0143:
        r8 = r13.isLeftProxyChannel;
        if (r8 == 0) goto L_0x0151;
    L_0x0147:
        r7 = r13.allDialogs;
        r7.remove(r4);
    L_0x014c:
        r4 = r4 + -1;
        r5 = r5 + -1;
        goto L_0x0155;
    L_0x0151:
        r8 = -1;
        r13.addDialogToItsFolder(r8, r7, r3);
    L_0x0155:
        r4 = r4 + r2;
        goto L_0x0079;
    L_0x0158:
        r14 = r13.proxyDialog;
        if (r14 == 0) goto L_0x016b;
    L_0x015c:
        r1 = r13.isLeftProxyChannel;
        if (r1 == 0) goto L_0x016b;
    L_0x0160:
        r1 = r13.allDialogs;
        r1.add(r0, r14);
        r14 = -2;
        r1 = r13.proxyDialog;
        r13.addDialogToItsFolder(r14, r1, r3);
    L_0x016b:
        if (r6 != 0) goto L_0x019a;
    L_0x016d:
        r14 = r13.getUserConfig();
        r14 = r14.getCurrentUser();
        if (r14 == 0) goto L_0x019a;
    L_0x0177:
        r1 = new org.telegram.tgnet.TLRPC$TL_dialog;
        r1.<init>();
        r2 = r14.id;
        r2 = (long) r2;
        r1.id = r2;
        r2 = new org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
        r2.<init>();
        r1.notify_settings = r2;
        r2 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r2.<init>();
        r1.peer = r2;
        r2 = r1.peer;
        r14 = r14.id;
        r2.user_id = r14;
        r14 = r13.dialogsForward;
        r14.add(r0, r1);
    L_0x019a:
        r14 = r13.dialogsByFolder;
        r14 = r14.size();
        if (r0 >= r14) goto L_0x01be;
    L_0x01a2:
        r14 = r13.dialogsByFolder;
        r14 = r14.keyAt(r0);
        r1 = r13.dialogsByFolder;
        r1 = r1.valueAt(r0);
        r1 = (java.util.ArrayList) r1;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x01bb;
    L_0x01b6:
        r1 = r13.dialogsByFolder;
        r1.remove(r14);
    L_0x01bb:
        r0 = r0 + 1;
        goto L_0x019a;
    L_0x01be:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.sortDialogs(android.util.SparseArray):void");
    }

    private void addDialogToItsFolder(int i, Dialog dialog, boolean z) {
        int i2;
        if (dialog instanceof TL_dialogFolder) {
            dialog.unread_count = 0;
            dialog.unread_mentions_count = 0;
            i2 = 0;
        } else {
            i2 = dialog.folder_id;
        }
        ArrayList arrayList = (ArrayList) this.dialogsByFolder.get(i2);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.dialogsByFolder.put(i2, arrayList);
        }
        if (!(i2 == 0 || dialog.unread_count == 0)) {
            Dialog dialog2 = (Dialog) this.dialogs_dict.get(DialogObject.makeFolderDialogId(i2));
            if (dialog2 != null) {
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
        }
        if (i == -1) {
            arrayList.add(dialog);
        } else if (i != -2) {
            arrayList.add(i, dialog);
        } else if (arrayList.isEmpty() || !(arrayList.get(0) instanceof TL_dialogFolder)) {
            arrayList.add(0, dialog);
        } else {
            arrayList.add(1, dialog);
        }
    }

    public static String getRestrictionReason(ArrayList<TL_restrictionReason> arrayList) {
        if (arrayList.isEmpty()) {
            return null;
        }
        int i = 0;
        int size = arrayList.size();
        while (i < size) {
            TL_restrictionReason tL_restrictionReason = (TL_restrictionReason) arrayList.get(i);
            if (!"all".equals(tL_restrictionReason.platform)) {
                if (!"android".equals(tL_restrictionReason.platform)) {
                    i++;
                }
            }
            return tL_restrictionReason.text;
        }
        return null;
    }

    private static void showCantOpenAlert(BaseFragment baseFragment, String str) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.setMessage(str);
            baseFragment.showDialog(builder.create());
        }
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment) {
        return checkCanOpenChat(bundle, baseFragment, null);
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment, MessageObject messageObject) {
        if (!(bundle == null || baseFragment == null)) {
            User user;
            Chat chat;
            int i = bundle.getInt("user_id", 0);
            int i2 = bundle.getInt("chat_id", 0);
            int i3 = bundle.getInt("message_id", 0);
            String str = null;
            if (i != 0) {
                user = getUser(Integer.valueOf(i));
                chat = null;
            } else if (i2 != 0) {
                chat = getChat(Integer.valueOf(i2));
                user = null;
            } else {
                user = null;
                chat = user;
            }
            if (user == null && chat == null) {
                return true;
            }
            if (chat != null) {
                str = getRestrictionReason(chat.restriction_reason);
            } else if (user != null) {
                str = getRestrictionReason(user.restriction_reason);
            }
            if (str != null) {
                showCantOpenAlert(baseFragment, str);
                return false;
            } else if (!(i3 == 0 || messageObject == null || chat == null || chat.access_hash != 0)) {
                i = (int) messageObject.getDialogId();
                if (i != 0) {
                    TLObject tL_messages_getMessages;
                    AlertDialog alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
                    if (i < 0) {
                        chat = getChat(Integer.valueOf(-i));
                    }
                    if (i > 0 || !ChatObject.isChannel(r3)) {
                        tL_messages_getMessages = new TL_messages_getMessages();
                        tL_messages_getMessages.id.add(Integer.valueOf(messageObject.getId()));
                    } else {
                        Chat chat2 = getChat(Integer.valueOf(-i));
                        tL_messages_getMessages = new TL_channels_getMessages();
                        tL_messages_getMessages.channel = getInputChannel(chat2);
                        tL_messages_getMessages.id.add(Integer.valueOf(messageObject.getId()));
                    }
                    alertDialog.setOnCancelListener(new -$$Lambda$MessagesController$Q3gE4D6e8Y_f7KoqBc8WY4mTi3Y(this, getConnectionsManager().sendRequest(tL_messages_getMessages, new -$$Lambda$MessagesController$Fxut3hB2IXEcZhR2QyK02YJccCA(this, alertDialog, baseFragment, bundle)), baseFragment));
                    baseFragment.setVisibleDialog(alertDialog);
                    alertDialog.show();
                    return false;
                }
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$checkCanOpenChat$275$MessagesController(AlertDialog alertDialog, BaseFragment baseFragment, Bundle bundle, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$2-Bhk_UWyZArYWu-nj4FX6qA7uw(this, alertDialog, tLObject, baseFragment, bundle));
        }
    }

    public /* synthetic */ void lambda$null$274$MessagesController(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        messages_Messages messages_messages = (messages_Messages) tLObject;
        putUsers(messages_messages.users, false);
        putChats(messages_messages.chats, false);
        getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
        baseFragment.presentFragment(new ChatActivity(bundle), true);
    }

    public /* synthetic */ void lambda$checkCanOpenChat$276$MessagesController(int i, BaseFragment baseFragment, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
        if (baseFragment != null) {
            baseFragment.setVisibleDialog(null);
        }
    }

    public static void openChatOrProfileWith(User user, Chat chat, BaseFragment baseFragment, int i, boolean z) {
        if ((user != null || chat != null) && baseFragment != null) {
            String str = null;
            if (chat != null) {
                str = getRestrictionReason(chat.restriction_reason);
            } else if (user != null) {
                str = getRestrictionReason(user.restriction_reason);
                if (user.bot) {
                    i = 1;
                    z = true;
                }
            }
            if (str != null) {
                showCantOpenAlert(baseFragment, str);
            } else {
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
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x002f  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002f  */
    public void openByUserName(java.lang.String r6, org.telegram.ui.ActionBar.BaseFragment r7, int r8) {
        /*
        r5 = this;
        if (r6 == 0) goto L_0x0069;
    L_0x0002:
        if (r7 != 0) goto L_0x0006;
    L_0x0004:
        goto L_0x0069;
    L_0x0006:
        r0 = r5.getUserOrChat(r6);
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.User;
        r2 = 0;
        if (r1 == 0) goto L_0x0018;
    L_0x000f:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r1 = r0.min;
        if (r1 == 0) goto L_0x0016;
    L_0x0015:
        goto L_0x0026;
    L_0x0016:
        r1 = r2;
        goto L_0x0028;
    L_0x0018:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r1 == 0) goto L_0x0026;
    L_0x001c:
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r1 = r0.min;
        if (r1 == 0) goto L_0x0023;
    L_0x0022:
        goto L_0x0026;
    L_0x0023:
        r1 = r0;
        r0 = r2;
        goto L_0x0028;
    L_0x0026:
        r0 = r2;
        r1 = r0;
    L_0x0028:
        r3 = 0;
        if (r0 == 0) goto L_0x002f;
    L_0x002b:
        openChatOrProfileWith(r0, r2, r7, r8, r3);
        goto L_0x0069;
    L_0x002f:
        r0 = 1;
        if (r1 == 0) goto L_0x0036;
    L_0x0032:
        openChatOrProfileWith(r2, r1, r7, r0, r3);
        goto L_0x0069;
    L_0x0036:
        r1 = r7.getParentActivity();
        if (r1 != 0) goto L_0x003d;
    L_0x003c:
        return;
    L_0x003d:
        r0 = new org.telegram.ui.ActionBar.AlertDialog[r0];
        r1 = new org.telegram.ui.ActionBar.AlertDialog;
        r2 = r7.getParentActivity();
        r4 = 3;
        r1.<init>(r2, r4);
        r0[r3] = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
        r1.<init>();
        r1.username = r6;
        r6 = r5.getConnectionsManager();
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$E9I-rOP2XDEloKCu7sFp3jhnG60;
        r2.<init>(r5, r0, r7, r8);
        r6 = r6.sendRequest(r1, r2);
        r8 = new org.telegram.messenger.-$$Lambda$MessagesController$g4XyDsaZinAOpJNBuk94VuSsZ40;
        r8.<init>(r5, r0, r6, r7);
        r6 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r6);
    L_0x0069:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.openByUserName(java.lang.String, org.telegram.ui.ActionBar.BaseFragment, int):void");
    }

    public /* synthetic */ void lambda$openByUserName$278$MessagesController(AlertDialog[] alertDialogArr, BaseFragment baseFragment, int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$EcDsojm7HZX6hAL2a4ylImFm3OM(this, alertDialogArr, baseFragment, tL_error, tLObject, i));
    }

    public /* synthetic */ void lambda$null$277$MessagesController(AlertDialog[] alertDialogArr, BaseFragment baseFragment, TL_error tL_error, TLObject tLObject, int i) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Exception unused) {
        }
        alertDialogArr[0] = null;
        baseFragment.setVisibleDialog(null);
        if (tL_error == null) {
            TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
            putUsers(tL_contacts_resolvedPeer.users, false);
            putChats(tL_contacts_resolvedPeer.chats, false);
            getMessagesStorage().putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
            if (!tL_contacts_resolvedPeer.chats.isEmpty()) {
                openChatOrProfileWith(null, (Chat) tL_contacts_resolvedPeer.chats.get(0), baseFragment, 1, false);
            } else if (!tL_contacts_resolvedPeer.users.isEmpty()) {
                openChatOrProfileWith((User) tL_contacts_resolvedPeer.users.get(0), null, baseFragment, i, false);
            }
        } else if (baseFragment != null && baseFragment.getParentActivity() != null) {
            try {
                Toast.makeText(baseFragment.getParentActivity(), LocaleController.getString("NoUsernameFound", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$openByUserName$280$MessagesController(AlertDialog[] alertDialogArr, int i, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new -$$Lambda$MessagesController$TVBsyBlQ-_w0esFlKEWtcE8iKzA(this, i));
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("diditem");
            stringBuilder.append(j2);
            i2 = notificationsSettings.getInt(stringBuilder.toString(), 0);
        } else {
            i2 = i;
        }
        if (i2 == 0 || !MessagesStorage.getInstance(this.currentAccount).checkMessageId(j2, z, i2)) {
            int generateClassGuid = ConnectionsManager.generateClassGuid();
            final int i3 = generateClassGuid;
            final long j3 = j;
            final Runnable runnable2 = runnable;
            getNotificationCenter().addObserver(new NotificationCenterDelegate() {
                public void didReceivedNotification(int i, int i2, Object... objArr) {
                    if (i == NotificationCenter.messagesDidLoad && ((Integer) objArr[10]).intValue() == i3) {
                        ArrayList arrayList = (ArrayList) objArr[2];
                        boolean booleanValue = ((Boolean) objArr[3]).booleanValue();
                        if (arrayList.isEmpty() && booleanValue) {
                            MessagesController.this.loadMessages(j3, 20, 3, 0, false, 0, i3, 3, 0, false, false, 0);
                            return;
                        }
                        MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
                        runnable2.run();
                    }
                }
            }, NotificationCenter.messagesDidLoad);
            loadMessages(j, 1, i2, 0, true, 0, generateClassGuid, 3, 0, false, false, 0);
            return;
        }
        runnable.run();
    }
}
