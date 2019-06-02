package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.DialogPeer;
import org.telegram.tgnet.TLRPC.Document;
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
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.SendMessageAction;
import org.telegram.tgnet.TLRPC.TL_account_getContactSignUpNotification;
import org.telegram.tgnet.TLRPC.TL_account_getNotifySettings;
import org.telegram.tgnet.TLRPC.TL_account_installWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_registerDevice;
import org.telegram.tgnet.TLRPC.TL_account_saveWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC.TL_account_updateStatus;
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
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chat;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatOnlines;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
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
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC.TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC.TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUser;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUserSelf;
import org.telegram.tgnet.TLRPC.TL_inputWallPaper;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC.TL_messages_chatFull;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_deleteChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
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
import org.telegram.tgnet.TLRPC.TL_messages_getUnreadMentions;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_messages_hideReportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_markDialogUnread;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readMentions;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_messages_reorderPinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_reportEncryptedSpam;
import org.telegram.tgnet.TLRPC.TL_messages_reportSpam;
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
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty_layer77;
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
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;

public class MessagesController implements NotificationCenterDelegate {
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
    public int availableMapProviders;
    public boolean blockedCountry;
    public SparseIntArray blockedUsers = new SparseIntArray();
    public int callConnectTimeout;
    public int callPacketTimeout;
    public int callReceiveTimeout;
    public int callRingTimeout;
    public boolean canRevokePmInbox;
    private SparseArray<ArrayList<Integer>> channelAdmins = new SparseArray();
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
    private int currentAccount;
    private Runnable currentDeleteTaskRunnable;
    private int currentDeletingTaskChannelId;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public String dcDomainName;
    public boolean defaultP2pContacts;
    public LongSparseArray<Integer> deletedHistory = new LongSparseArray();
    private LongSparseArray<Dialog> deletingDialogs = new LongSparseArray();
    private final Comparator<Dialog> dialogComparator = new Comparator<Dialog>() {
        /* JADX WARNING: Missing block: B:33:0x004e, code skipped:
            if (r0 >= r7.last_message_date) goto L_0x0053;
     */
        /* JADX WARNING: Missing block: B:38:0x0069, code skipped:
            if (r7 >= r8.last_message_date) goto L_0x006e;
     */
        public int compare(org.telegram.tgnet.TLRPC.Dialog r7, org.telegram.tgnet.TLRPC.Dialog r8) {
            /*
            r6 = this;
            r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;
            r1 = -1;
            if (r0 == 0) goto L_0x000a;
        L_0x0005:
            r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;
            if (r2 != 0) goto L_0x000a;
        L_0x0009:
            return r1;
        L_0x000a:
            r2 = 1;
            if (r0 != 0) goto L_0x0012;
        L_0x000d:
            r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;
            if (r0 == 0) goto L_0x0012;
        L_0x0011:
            return r2;
        L_0x0012:
            r0 = r7.pinned;
            if (r0 != 0) goto L_0x001b;
        L_0x0016:
            r0 = r8.pinned;
            if (r0 == 0) goto L_0x001b;
        L_0x001a:
            return r2;
        L_0x001b:
            r0 = r7.pinned;
            if (r0 == 0) goto L_0x0024;
        L_0x001f:
            r0 = r8.pinned;
            if (r0 != 0) goto L_0x0024;
        L_0x0023:
            return r1;
        L_0x0024:
            r0 = r7.pinned;
            r3 = 0;
            if (r0 == 0) goto L_0x0038;
        L_0x0029:
            r0 = r8.pinned;
            if (r0 == 0) goto L_0x0038;
        L_0x002d:
            r7 = r7.pinnedNum;
            r8 = r8.pinnedNum;
            if (r7 >= r8) goto L_0x0034;
        L_0x0033:
            return r2;
        L_0x0034:
            if (r7 <= r8) goto L_0x0037;
        L_0x0036:
            return r1;
        L_0x0037:
            return r3;
        L_0x0038:
            r0 = org.telegram.messenger.MessagesController.this;
            r0 = r0.currentAccount;
            r0 = org.telegram.messenger.DataQuery.getInstance(r0);
            r4 = r7.id;
            r0 = r0.getDraft(r4);
            if (r0 == 0) goto L_0x0051;
        L_0x004a:
            r0 = r0.date;
            r4 = r7.last_message_date;
            if (r0 < r4) goto L_0x0051;
        L_0x0050:
            goto L_0x0053;
        L_0x0051:
            r0 = r7.last_message_date;
        L_0x0053:
            r7 = org.telegram.messenger.MessagesController.this;
            r7 = r7.currentAccount;
            r7 = org.telegram.messenger.DataQuery.getInstance(r7);
            r4 = r8.id;
            r7 = r7.getDraft(r4);
            if (r7 == 0) goto L_0x006c;
        L_0x0065:
            r7 = r7.date;
            r4 = r8.last_message_date;
            if (r7 < r4) goto L_0x006c;
        L_0x006b:
            goto L_0x006e;
        L_0x006c:
            r7 = r8.last_message_date;
        L_0x006e:
            if (r0 >= r7) goto L_0x0071;
        L_0x0070:
            return r2;
        L_0x0071:
            if (r0 <= r7) goto L_0x0074;
        L_0x0073:
            return r1;
        L_0x0074:
            return r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController$AnonymousClass2.compare(org.telegram.tgnet.TLRPC$Dialog, org.telegram.tgnet.TLRPC$Dialog):int");
        }
    };
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
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private ArrayList<Integer> loadedFullChats = new ArrayList();
    private ArrayList<Integer> loadedFullParticipants = new ArrayList();
    private ArrayList<Integer> loadedFullUsers = new ArrayList();
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
    private Runnable passwordCheckRunnable = new Runnable() {
        public void run() {
            UserConfig.getInstance(MessagesController.this.currentAccount).checkSavedPassword();
        }
    };
    private LongSparseArray<SparseArray<MessageObject>> pollsToCheck = new LongSparseArray();
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public LongSparseArray<CharSequence> printingStrings = new LongSparseArray();
    public LongSparseArray<Integer> printingStringsTypes = new LongSparseArray();
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0f, 2);
    private Dialog proxyDialog;
    private String proxyDialogAddress;
    private long proxyDialogId;
    public int ratingDecay;
    private ArrayList<ReadTask> readTasks = new ArrayList();
    private LongSparseArray<ReadTask> readTasksMap = new LongSparseArray();
    public boolean registeringForPush;
    private LongSparseArray<ArrayList<Integer>> reloadingMessages = new LongSparseArray();
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
    public int unreadUnmutedDialogs;
    private final Comparator<Update> updatesComparator = new -$$Lambda$MessagesController$jdY-gZP_eY5WAh_nSc2UhUqpq4M(this);
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
    private String uploadingWallpaper;
    private boolean uploadingWallpaperBlurred;
    private boolean uploadingWallpaperMotion;
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap(100, 1.0f, 2);
    public String venueSearchBot;
    private ArrayList<Long> visibleDialogMainThreadIds = new ArrayList();
    public int webFileDatacenterId;

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

    static /* synthetic */ void lambda$completeReadTask$147(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$deleteUserPhoto$58(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$hideReportSpam$23(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMentionMessageAsRead$142(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMentionsAsRead$149(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markMessageContentAsRead$140(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$processUpdates$236(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$reportSpam$24(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$reportSpam$25(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$unregistedPush$185(TLObject tLObject, TL_error tL_error) {
    }

    public /* synthetic */ int lambda$new$0$MessagesController(Update update, Update update2) {
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
        int i2 = 2;
        this.currentAccount = i;
        ImageLoader.getInstance();
        MessagesStorage.getInstance(this.currentAccount);
        LocationController.getInstance(this.currentAccount);
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$y42O_-dKOKyzNi5k5aHwMy0RjOs(this));
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
        this.defaultP2pContacts = this.mainPreferences.getBoolean("defaultP2pContacts", false);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
        this.proxyDialogId = this.mainPreferences.getLong("proxy_dialog", 0);
        this.proxyDialogAddress = this.mainPreferences.getString("proxyDialogAddress", null);
        this.nextTosCheckTime = this.notificationsPreferences.getInt("nextTosCheckTime", 0);
        this.venueSearchBot = this.mainPreferences.getString("venueSearchBot", "foursquare");
        this.gifSearchBot = this.mainPreferences.getString("gifSearchBot", "gif");
        this.imageSearchBot = this.mainPreferences.getString("imageSearchBot", "pic");
        this.blockedCountry = this.mainPreferences.getBoolean("blockedCountry", false);
        this.dcDomainName = this.mainPreferences.getString("dcDomainName", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv2.stel.com" : "apv2.stel.com");
        SharedPreferences sharedPreferences = this.mainPreferences;
        if (ConnectionsManager.native_isTestBackend(this.currentAccount) == 0) {
            i2 = 4;
        }
        this.webFileDatacenterId = sharedPreferences.getInt("webFileDatacenterId", i2);
        this.suggestedLangCode = this.mainPreferences.getString("suggestedLangCode", "en");
    }

    public /* synthetic */ void lambda$new$1$MessagesController() {
        MessagesController instance = getInstance(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.fileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.updateMessageMedia);
    }

    public void updateConfig(TL_config tL_config) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$E_B3T1gXoq4eD739GgHdBDLPpBs(this, tL_config));
    }

    public /* synthetic */ void lambda$updateConfig$2$MessagesController(TL_config tL_config) {
        String str;
        DownloadController.getInstance(this.currentAccount).loadAutoDownloadConfig(false);
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
        this.defaultP2pContacts = tL_config.default_p2p_contacts;
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
        str = this.suggestedLangCode;
        if (str == null || !str.equals(tL_config.suggested_lang_code)) {
            this.suggestedLangCode = tL_config.suggested_lang_code;
            LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        }
        String str2 = "google";
        if (tL_config.static_maps_provider == null) {
            tL_config.static_maps_provider = str2;
        }
        this.mapKey = null;
        this.mapProvider = 0;
        this.availableMapProviders = 0;
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
                    } else if (str2.equals(split3[0])) {
                        if (i == 0 && split2.length > 1) {
                            this.mapProvider = 4;
                        }
                        this.availableMapProviders |= 1;
                    } else {
                        if ("telegram".equals(split3[0])) {
                            if (i == 0) {
                                this.mapProvider = 2;
                            }
                            this.availableMapProviders = 2 | this.availableMapProviders;
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
        edit.putBoolean("defaultP2pContacts", this.defaultP2pContacts);
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
        edit.putString("dcDomainName", this.dcDomainName);
        edit.putInt("webFileDatacenterId", this.webFileDatacenterId);
        edit.putString("suggestedLangCode", this.suggestedLangCode);
        edit.commit();
        LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(this.currentAccount, tL_config.lang_pack_version, tL_config.base_lang_pack_version);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.configLoaded, new Object[0]);
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
        if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
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
        String str;
        if (i == NotificationCenter.FileDidUpload) {
            str = (String) objArr[0];
            InputFile inputFile = (InputFile) objArr[1];
            String str2 = this.uploadingAvatar;
            if (str2 == null || !str2.equals(str)) {
                str2 = this.uploadingWallpaper;
                if (str2 != null && str2.equals(str)) {
                    TL_account_uploadWallPaper tL_account_uploadWallPaper = new TL_account_uploadWallPaper();
                    tL_account_uploadWallPaper.file = inputFile;
                    tL_account_uploadWallPaper.mime_type = "image/jpeg";
                    TL_wallPaperSettings tL_wallPaperSettings = new TL_wallPaperSettings();
                    tL_wallPaperSettings.blur = this.uploadingWallpaperBlurred;
                    tL_wallPaperSettings.motion = this.uploadingWallpaperMotion;
                    tL_account_uploadWallPaper.settings = tL_wallPaperSettings;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_uploadWallPaper, new -$$Lambda$MessagesController$G2pKRuboIkVmyKgcxHs4KK8Bkn4(this, tL_wallPaperSettings));
                    return;
                }
                return;
            }
            TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TL_photos_uploadProfilePhoto();
            tL_photos_uploadProfilePhoto.file = inputFile;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_uploadProfilePhoto, new -$$Lambda$MessagesController$avFJzHcfb0E8c4wljuXOjwoiTOA(this));
        } else if (i == NotificationCenter.FileDidFailUpload) {
            str = (String) objArr[0];
            String str3 = this.uploadingAvatar;
            if (str3 == null || !str3.equals(str)) {
                str3 = this.uploadingWallpaper;
                if (str3 != null && str3.equals(str)) {
                    this.uploadingWallpaper = null;
                    return;
                }
                return;
            }
            this.uploadingAvatar = null;
        } else if (i == NotificationCenter.messageReceivedByServer) {
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
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            MessageObject messageObject2 = (MessageObject) this.dialogMessagesByIds.get(num.intValue());
            this.dialogMessagesByIds.remove(num.intValue());
            if (messageObject2 != null) {
                this.dialogMessagesByIds.put(num2.intValue(), messageObject2);
            }
        } else if (i == NotificationCenter.updateMessageMedia) {
            Message message = (Message) objArr[0];
            MessageObject messageObject3 = (MessageObject) this.dialogMessagesByIds.get(message.id);
            if (messageObject3 != null) {
                messageObject3.messageOwner.media = message.media;
                MessageMedia messageMedia = message.media;
                if (messageMedia.ttl_seconds == 0) {
                    return;
                }
                if ((messageMedia.photo instanceof TL_photoEmpty) || (messageMedia.document instanceof TL_documentEmpty)) {
                    messageObject3.setType();
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                }
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$4$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            User user = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                putUser(user, true);
            } else {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
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
                MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(user);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList2, null, false, true);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$VkYWRm7oeBHADU-2qscNSGE0Axs(this));
            }
        }
    }

    public /* synthetic */ void lambda$null$3$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(2));
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    public /* synthetic */ void lambda$didReceivedNotification$6$MessagesController(TL_wallPaperSettings tL_wallPaperSettings, TLObject tLObject, TL_error tL_error) {
        TL_wallPaper tL_wallPaper = (TL_wallPaper) tLObject;
        File file = new File(ApplicationLoader.getFilesDirFixed(), this.uploadingWallpaperBlurred ? "wallpaper_original.jpg" : "wallpaper.jpg");
        if (tL_wallPaper != null) {
            try {
                AndroidUtilities.copyFile(file, FileLoader.getPathToAttach(tL_wallPaper.document, true));
            } catch (Exception unused) {
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$RwdHkk3sx1H7HCQZHIZAoujW4QQ(this, tL_wallPaper, tL_wallPaperSettings, file));
    }

    public /* synthetic */ void lambda$null$5$MessagesController(TL_wallPaper tL_wallPaper, TL_wallPaperSettings tL_wallPaperSettings, File file) {
        if (this.uploadingWallpaper != null && tL_wallPaper != null) {
            tL_wallPaper.settings = tL_wallPaperSettings;
            tL_wallPaper.flags |= 4;
            Editor edit = getGlobalMainSettings().edit();
            edit.putLong("selectedBackground2", tL_wallPaper.id);
            edit.commit();
            ArrayList arrayList = new ArrayList();
            arrayList.add(tL_wallPaper);
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(arrayList, 2);
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
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersNeedReload, Long.valueOf(tL_wallPaper.id));
        }
    }

    public void cleanup() {
        ContactsController.getInstance(this.currentAccount).cleanup();
        MediaController.getInstance().cleanup();
        NotificationsController.getInstance(this.currentAccount).cleanup();
        SendMessagesHelper.getInstance(this.currentAccount).cleanup();
        SecretChatHelper.getInstance(this.currentAccount).cleanup();
        LocationController.getInstance(this.currentAccount).cleanup();
        DataQuery.getInstance(this.currentAccount).cleanup();
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).putLong("lastStickersLoadTimeMask", 0).putLong("lastStickersLoadTimeFavs", 0).commit();
        this.mainPreferences.edit().remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").remove("dcDomainName").remove("webFileDatacenterId").commit();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$cyTTwmVBJXbVI4kVZKxLSy4FE3s(this));
        this.createdDialogMainThreadIds.clear();
        this.visibleDialogMainThreadIds.clear();
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
        this.firstGettingTask = false;
        this.updatingState = false;
        this.resetingDialogs = false;
        this.lastStatusUpdateTime = 0;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.uploadingWallpaper = null;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$6czMCwAPM6zONbsB9qHk4xGgZKk(this));
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$cleanup$7$MessagesController() {
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
        this.gettingDifference = false;
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
    }

    public /* synthetic */ void lambda$cleanup$8$MessagesController() {
        ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
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
        MessagesStorage.getInstance(this.currentAccount).getEncryptedChat(i, countDownLatch, arrayList);
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

    public boolean isDialogCreated(long j) {
        return this.createdDialogMainThreadIds.contains(Long.valueOf(j));
    }

    public boolean isDialogVisible(long j) {
        return this.visibleDialogMainThreadIds.contains(Long.valueOf(j));
    }

    public void setLastVisibleDialogId(long j, boolean z) {
        if (!z) {
            this.visibleDialogMainThreadIds.remove(Long.valueOf(j));
        } else if (!this.visibleDialogMainThreadIds.contains(Long.valueOf(j))) {
            this.visibleDialogMainThreadIds.add(Long.valueOf(j));
        }
    }

    public void setLastCreatedDialogId(long j, boolean z) {
        if (!z) {
            this.createdDialogMainThreadIds.remove(Long.valueOf(j));
            SparseArray sparseArray = (SparseArray) this.pollsToCheck.get(j);
            if (sparseArray != null) {
                int size = sparseArray.size();
                for (int i = 0; i < size; i++) {
                    ((MessageObject) sparseArray.valueAt(i)).pollVisibleOnScreen = false;
                }
            }
        } else if (!this.createdDialogMainThreadIds.contains(Long.valueOf(j))) {
            this.createdDialogMainThreadIds.add(Long.valueOf(j));
        } else {
            return;
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$GClBurUFA5765ROku5c8uOseY60(this, z, j));
    }

    public /* synthetic */ void lambda$setLastCreatedDialogId$9$MessagesController(boolean z, long j) {
        if (!z) {
            this.createdDialogIds.remove(Long.valueOf(j));
        } else if (!this.createdDialogIds.contains(Long.valueOf(j))) {
            this.createdDialogIds.add(Long.valueOf(j));
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
        if (r7 != 0) goto L_0x0122;
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
        goto L_0x0122;
    L_0x0080:
        r6 = r2.flags;
        r6 = r6 & -33;
        r2.flags = r6;
        r2.photo = r4;
        goto L_0x0122;
    L_0x008a:
        r7 = r5.users;
        r1 = r6.id;
        r1 = java.lang.Integer.valueOf(r1);
        r7.put(r1, r6);
        goto L_0x0122;
    L_0x0097:
        if (r7 != 0) goto L_0x00d5;
    L_0x0099:
        r7 = r5.users;
        r3 = r6.id;
        r3 = java.lang.Integer.valueOf(r3);
        r7.put(r3, r6);
        r7 = r6.id;
        r3 = r5.currentAccount;
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);
        r3 = r3.getClientUserId();
        if (r7 != r3) goto L_0x00c4;
    L_0x00b2:
        r7 = r5.currentAccount;
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);
        r7.setCurrentUser(r6);
        r7 = r5.currentAccount;
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);
        r7.saveConfig(r1);
    L_0x00c4:
        if (r2 == 0) goto L_0x0122;
    L_0x00c6:
        r6 = r6.status;
        if (r6 == 0) goto L_0x0122;
    L_0x00ca:
        r7 = r2.status;
        if (r7 == 0) goto L_0x0122;
    L_0x00ce:
        r6 = r6.expires;
        r7 = r7.expires;
        if (r6 == r7) goto L_0x0122;
    L_0x00d4:
        return r1;
    L_0x00d5:
        if (r2 != 0) goto L_0x00e3;
    L_0x00d7:
        r7 = r5.users;
        r1 = r6.id;
        r1 = java.lang.Integer.valueOf(r1);
        r7.put(r1, r6);
        goto L_0x0122;
    L_0x00e3:
        r7 = r2.min;
        if (r7 == 0) goto L_0x0122;
    L_0x00e7:
        r6.min = r0;
        r7 = r2.bot;
        if (r7 == 0) goto L_0x0102;
    L_0x00ed:
        r7 = r2.username;
        if (r7 == 0) goto L_0x00fa;
    L_0x00f1:
        r6.username = r7;
        r7 = r6.flags;
        r7 = r7 | 8;
        r6.flags = r7;
        goto L_0x0102;
    L_0x00fa:
        r7 = r6.flags;
        r7 = r7 & -9;
        r6.flags = r7;
        r6.username = r4;
    L_0x0102:
        r7 = r2.photo;
        if (r7 == 0) goto L_0x010f;
    L_0x0106:
        r6.photo = r7;
        r7 = r6.flags;
        r7 = r7 | 32;
        r6.flags = r7;
        goto L_0x0117;
    L_0x010f:
        r7 = r6.flags;
        r7 = r7 & -33;
        r6.flags = r7;
        r6.photo = r4;
    L_0x0117:
        r7 = r5.users;
        r1 = r6.id;
        r1 = java.lang.Integer.valueOf(r1);
        r7.put(r1, r6);
    L_0x0122:
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$MHasUaMkg9dD-kjYrwSO8zJY7Bs(this));
            }
        }
    }

    public /* synthetic */ void lambda$putUsers$10$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
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
                                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$JQg5AlfE18YOamojW_v4DYkKyIA(this, chat));
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

    public /* synthetic */ void lambda$putChat$11$MessagesController(Chat chat) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, chat);
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$YrH4Mr-BWV-6GLj5qWieGm840u8(this));
            }
        }
    }

    public /* synthetic */ void lambda$reloadDialogsReadValue$12$MessagesController(TLObject tLObject, TL_error tL_error) {
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
                processUpdateArray(arrayList, null, null, false);
            }
        }
    }

    public boolean isChannelAdmin(int i, int i2) {
        ArrayList arrayList = (ArrayList) this.channelAdmins.get(i);
        return arrayList != null && arrayList.indexOf(Integer.valueOf(i2)) >= 0;
    }

    public void loadChannelAdmins(int i, boolean z) {
        if (this.loadingChannelAdmins.indexOfKey(i) < 0) {
            int i2 = 0;
            this.loadingChannelAdmins.put(i, 0);
            if (z) {
                MessagesStorage.getInstance(this.currentAccount).loadChannelAdmins(i);
            } else {
                TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
                ArrayList arrayList = (ArrayList) this.channelAdmins.get(i);
                if (arrayList != null) {
                    long j = 0;
                    while (i2 < arrayList.size()) {
                        j = (((j * 20261) + 2147483648L) + ((long) ((Integer) arrayList.get(i2)).intValue())) % 2147483648L;
                        i2++;
                    }
                    tL_channels_getParticipants.hash = (int) j;
                }
                tL_channels_getParticipants.channel = getInputChannel(i);
                tL_channels_getParticipants.limit = 100;
                tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new -$$Lambda$MessagesController$5NQ9MFRgqgfYWG2HdrbXnmbFM_o(this, i));
            }
        }
    }

    public /* synthetic */ void lambda$loadChannelAdmins$13$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_channels_channelParticipants) {
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            ArrayList arrayList = new ArrayList(tL_channels_channelParticipants.participants.size());
            for (int i2 = 0; i2 < tL_channels_channelParticipants.participants.size(); i2++) {
                arrayList.add(Integer.valueOf(((ChannelParticipant) tL_channels_channelParticipants.participants.get(i2)).user_id));
            }
            processLoadedChannelAdmins(arrayList, i, false);
        }
    }

    public void processLoadedChannelAdmins(ArrayList<Integer> arrayList, int i, boolean z) {
        Collections.sort(arrayList);
        if (!z) {
            MessagesStorage.getInstance(this.currentAccount).putChannelAdmins(i, arrayList);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$qaVxMxZZgT13ZZ7VOgNBw4U51uM(this, i, arrayList, z));
    }

    public /* synthetic */ void lambda$processLoadedChannelAdmins$14$MessagesController(int i, ArrayList arrayList, boolean z) {
        this.loadingChannelAdmins.delete(i);
        this.channelAdmins.put(i, arrayList);
        if (z) {
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
                i = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getFullChannel, new -$$Lambda$MessagesController$ebifWTnrFPKK5b_9D1xusLXGK7Q(this, chat, j, i, i2));
                if (i2 != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, i2);
                }
            }
        }
    }

    public /* synthetic */ void lambda$loadFullChat$17$MessagesController(Chat chat, long j, int i, int i2, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_chatFull tL_messages_chatFull = (TL_messages_chatFull) tLObject;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_messages_chatFull.users, tL_messages_chatFull.chats, true, true);
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(tL_messages_chatFull.full_chat, false);
            if (ChatObject.isChannel(chat)) {
                ArrayList arrayList;
                Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, j));
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    arrayList = new ArrayList();
                    TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                    tL_updateReadChannelInbox.channel_id = i;
                    tL_updateReadChannelInbox.max_id = tL_messages_chatFull.full_chat.read_inbox_max_id;
                    arrayList.add(tL_updateReadChannelInbox);
                    processUpdateArray(arrayList, null, null, false);
                }
                num = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, j));
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_outbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    arrayList = new ArrayList();
                    TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_updateReadChannelOutbox();
                    tL_updateReadChannelOutbox.channel_id = i;
                    tL_updateReadChannelOutbox.max_id = tL_messages_chatFull.full_chat.read_outbox_max_id;
                    arrayList.add(tL_updateReadChannelOutbox);
                    processUpdateArray(arrayList, null, null, false);
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$yEPLjvar_Lbxq_PAfwQRKxAmq0Lc(this, i, tL_messages_chatFull, i2));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$mp8VRdAuIDk5ecDgkI3J_hxLXEM(this, tL_error, i));
    }

    public /* synthetic */ void lambda$null$15$MessagesController(int i, TL_messages_chatFull tL_messages_chatFull, int i2) {
        this.fullChats.put(i, tL_messages_chatFull.full_chat);
        applyDialogNotificationsSettings((long) (-i), tL_messages_chatFull.full_chat.notify_settings);
        for (int i3 = 0; i3 < tL_messages_chatFull.full_chat.bot_info.size(); i3++) {
            DataQuery.getInstance(this.currentAccount).putBotInfo((BotInfo) tL_messages_chatFull.full_chat.bot_info.get(i3));
        }
        this.exportedChats.put(i, tL_messages_chatFull.full_chat.exported_invite);
        this.loadingFullChats.remove(Integer.valueOf(i));
        this.loadedFullChats.add(Integer.valueOf(i));
        putUsers(tL_messages_chatFull.users, false);
        putChats(tL_messages_chatFull.chats, false);
        if (tL_messages_chatFull.full_chat.stickerset != null) {
            DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(tL_messages_chatFull.full_chat.stickerset);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, tL_messages_chatFull.full_chat, Integer.valueOf(i2), Boolean.valueOf(false), null);
    }

    public /* synthetic */ void lambda$null$16$MessagesController(TL_error tL_error, int i) {
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
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_users_getFullUser, new -$$Lambda$MessagesController$sHevIZGEf9jGeQ0PmpgZfE-2SOQ(this, user, i)), i);
            }
        }
    }

    public /* synthetic */ void lambda$loadFullUser$20$MessagesController(User user, int i, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            UserFull userFull = (UserFull) tLObject;
            MessagesStorage.getInstance(this.currentAccount).updateUserInfo(userFull, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$11fT0i1GEqsG3tRPr2wtXLuwL70(this, user, userFull, i));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$HIj5jF1rkzKmEGzAia-8fTi8vbE(this, user));
    }

    public /* synthetic */ void lambda$null$18$MessagesController(User user, UserFull userFull, int i) {
        applyDialogNotificationsSettings((long) user.id, userFull.notify_settings);
        if (userFull.bot_info instanceof TL_botInfo) {
            DataQuery.getInstance(this.currentAccount).putBotInfo(userFull.bot_info);
        }
        int indexOfKey = this.blockedUsers.indexOfKey(user.id);
        if (userFull.blocked) {
            if (indexOfKey < 0) {
                SparseIntArray sparseIntArray = new SparseIntArray();
                sparseIntArray.put(user.id, 1);
                MessagesStorage.getInstance(this.currentAccount).putBlockedUsers(sparseIntArray, false);
                this.blockedUsers.put(user.id, 1);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (indexOfKey >= 0) {
            MessagesStorage.getInstance(this.currentAccount).deleteBlockedUser(user.id);
            this.blockedUsers.removeAt(indexOfKey);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
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
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, false, true);
        if (stringBuilder2 != null) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(userFull.user.first_name);
            stringBuilder3.append(userFull.user.last_name);
            stringBuilder3.append(userFull.user.username);
            if (!stringBuilder2.equals(stringBuilder3.toString())) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
            }
        }
        if (userFull.bot_info instanceof TL_botInfo) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoad, userFull.bot_info, Integer.valueOf(i));
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.id), userFull, null);
    }

    public /* synthetic */ void lambda$null$19$MessagesController(User user) {
        this.loadingFullUsers.remove(Integer.valueOf(user.id));
    }

    private void reloadMessages(ArrayList<Integer> arrayList, long j) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new -$$Lambda$MessagesController$rxB6ZCv-wieivWsHR6TFAEUSbcc(this, j, chatByDialog, arrayList2));
            }
        }
    }

    public /* synthetic */ void lambda$reloadMessages$22$MessagesController(long j, Chat chat, ArrayList arrayList, TLObject tLObject, TL_error tL_error) {
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
                num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, j2));
                this.dialogs_read_inbox_max.put(Long.valueOf(j), num);
            }
            Integer num2 = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num2 == null) {
                num2 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, j2));
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
                message.unread = (message.out ? num3 : num).intValue() < message.id;
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
            MessagesStorage.getInstance(this.currentAccount).putMessages(messages_messages, j, -1, 0, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$SnNO4Xza4siBfE3j_QycdBx_L6U(this, j, arrayList, arrayList2));
        }
    }

    public /* synthetic */ void lambda$null$21$MessagesController(long j, ArrayList arrayList, ArrayList arrayList2) {
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
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList2);
    }

    public void hideReportSpam(long j, User user, Chat chat) {
        if (user != null || chat != null) {
            Editor edit = this.notificationsPreferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("spam3_");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), 1);
            edit.commit();
            if (((int) j) != 0) {
                TL_messages_hideReportSpam tL_messages_hideReportSpam = new TL_messages_hideReportSpam();
                if (user != null) {
                    tL_messages_hideReportSpam.peer = getInputPeer(user.id);
                } else if (chat != null) {
                    tL_messages_hideReportSpam.peer = getInputPeer(-chat.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_hideReportSpam, -$$Lambda$MessagesController$48y_t5lk6J6veA_TARwt8n5wq_Y.INSTANCE);
            }
        }
    }

    public void reportSpam(long j, User user, Chat chat, EncryptedChat encryptedChat) {
        if (user != null || chat != null || encryptedChat != null) {
            Editor edit = this.notificationsPreferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("spam3_");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), 1);
            edit.commit();
            if (((int) j) != 0) {
                TL_messages_reportSpam tL_messages_reportSpam = new TL_messages_reportSpam();
                if (chat != null) {
                    tL_messages_reportSpam.peer = getInputPeer(-chat.id);
                } else if (user != null) {
                    tL_messages_reportSpam.peer = getInputPeer(user.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reportSpam, -$$Lambda$MessagesController$A-WQ0vAsaYkwbum0RRlzdSDsvGo.INSTANCE, 2);
            } else if (encryptedChat != null && encryptedChat.access_hash != 0) {
                TL_messages_reportEncryptedSpam tL_messages_reportEncryptedSpam = new TL_messages_reportEncryptedSpam();
                tL_messages_reportEncryptedSpam.peer = new TL_inputEncryptedChat();
                TL_inputEncryptedChat tL_inputEncryptedChat = tL_messages_reportEncryptedSpam.peer;
                tL_inputEncryptedChat.chat_id = encryptedChat.id;
                tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reportEncryptedSpam, -$$Lambda$MessagesController$z4dPdFyhgWJPX_RIzHIHAuPr2cc.INSTANCE, 2);
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
                stringBuilder2.append("spam3_");
                stringBuilder2.append(j);
                if (sharedPreferences.getInt(stringBuilder2.toString(), 0) == 1) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("spam button already hidden for ");
                        stringBuilder3.append(j);
                        FileLog.d(stringBuilder3.toString());
                    }
                    return;
                }
                sharedPreferences = this.notificationsPreferences;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("spam_");
                stringBuilder4.append(j);
                if (sharedPreferences.getBoolean(stringBuilder4.toString(), false)) {
                    TL_messages_hideReportSpam tL_messages_hideReportSpam = new TL_messages_hideReportSpam();
                    if (user != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(user.id);
                    } else if (chat != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(-chat.id);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_hideReportSpam, new -$$Lambda$MessagesController$earKZXcnSlbNSw2ZGT5Q1z7WPUw(this, j));
                    return;
                }
                TL_messages_getPeerSettings tL_messages_getPeerSettings = new TL_messages_getPeerSettings();
                if (user != null) {
                    tL_messages_getPeerSettings.peer = getInputPeer(user.id);
                } else if (chat != null) {
                    tL_messages_getPeerSettings.peer = getInputPeer(-chat.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerSettings, new -$$Lambda$MessagesController$Akjl4BU34QO3ZqaL4fYbP0pvGoA(this, j));
            }
        }
    }

    public /* synthetic */ void lambda$loadPeerSettings$27$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$T_4uY71RM8CIJ1_vMFx7lbM6TkI(this, j));
    }

    public /* synthetic */ void lambda$null$26$MessagesController(long j) {
        this.loadingPeerSettings.remove(j);
        Editor edit = this.notificationsPreferences.edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("spam_");
        stringBuilder.append(j);
        edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("spam3_");
        stringBuilder.append(j);
        edit.putInt(stringBuilder.toString(), 1);
        edit.commit();
    }

    public /* synthetic */ void lambda$loadPeerSettings$29$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$vHup36gKZnehVbIgi6EYZgekiCc(this, j, tLObject));
    }

    public /* synthetic */ void lambda$null$28$MessagesController(long j, TLObject tLObject) {
        this.loadingPeerSettings.remove(j);
        if (tLObject != null) {
            TL_peerSettings tL_peerSettings = (TL_peerSettings) tLObject;
            Editor edit = this.notificationsPreferences.edit();
            String str = "spam3_";
            StringBuilder stringBuilder;
            if (tL_peerSettings.report_spam) {
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("show spam button for ");
                    stringBuilder.append(j);
                    FileLog.d(stringBuilder.toString());
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 2);
                edit.commit();
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(j));
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("don't show spam button for ");
                stringBuilder.append(j);
                FileLog.d(stringBuilder.toString());
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), 1);
            edit.commit();
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
            i4 = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(i3);
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
            MessagesStorage.getInstance(this.currentAccount).saveChannelPts(i3, i);
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
            if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + i4 == i2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY PTS");
                }
                MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(i2);
                MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
            } else if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != i2) {
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
        if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY SEQ");
            }
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(i);
            if (i3 != -1) {
                MessagesStorage.getInstance(this.currentAccount).setLastDateValue(i3);
            }
            MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
        } else if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() == i) {
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$LoBRzobgnJFkXmFzseR6vqsooQs(this, i));
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$bHd9VuzZp_-o4GfCjtn5be2-y4I(this, sparseArray));
    }

    public /* synthetic */ void lambda$didAddedNewTask$30$MessagesController(int i) {
        if (this.currentDeletingTaskMids != null || this.gettingNewDeleteTask) {
            int i2 = this.currentDeletingTaskTime;
            if (i2 == 0 || i >= i2) {
                return;
            }
        }
        getNewDeleteTask(null, 0);
    }

    public /* synthetic */ void lambda$didAddedNewTask$31$MessagesController(SparseArray sparseArray) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didCreatedNewDeleteTask, sparseArray);
    }

    public void getNewDeleteTask(ArrayList<Integer> arrayList, int i) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$uJw9E53v9xDGKUuX24rMZpaODgA(this, arrayList, i));
    }

    public /* synthetic */ void lambda$getNewDeleteTask$32$MessagesController(ArrayList arrayList, int i) {
        this.gettingNewDeleteTask = true;
        MessagesStorage.getInstance(this.currentAccount).getNewTask(arrayList, i);
    }

    /* JADX WARNING: Missing block: B:5:0x0015, code skipped:
            if (r1 <= r0) goto L_0x0017;
     */
    private boolean checkDeletingTask(boolean r4) {
        /*
        r3 = this;
        r0 = r3.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r0 = r0.getCurrentTime();
        r1 = r3.currentDeletingTaskMids;
        r2 = 0;
        if (r1 == 0) goto L_0x003a;
    L_0x000f:
        if (r4 != 0) goto L_0x0017;
    L_0x0011:
        r1 = r3.currentDeletingTaskTime;
        if (r1 == 0) goto L_0x003a;
    L_0x0015:
        if (r1 > r0) goto L_0x003a;
    L_0x0017:
        r3.currentDeletingTaskTime = r2;
        r0 = r3.currentDeleteTaskRunnable;
        if (r0 == 0) goto L_0x0026;
    L_0x001d:
        if (r4 != 0) goto L_0x0026;
    L_0x001f:
        r4 = org.telegram.messenger.Utilities.stageQueue;
        r0 = r3.currentDeleteTaskRunnable;
        r4.cancelRunnable(r0);
    L_0x0026:
        r4 = 0;
        r3.currentDeleteTaskRunnable = r4;
        r4 = new java.util.ArrayList;
        r0 = r3.currentDeletingTaskMids;
        r4.<init>(r0);
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$znYc9uVm8VzO6BhZCUvwobX7s2A;
        r0.<init>(r3, r4);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r4 = 1;
        return r4;
    L_0x003a:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.checkDeletingTask(boolean):boolean");
    }

    public /* synthetic */ void lambda$checkDeletingTask$34$MessagesController(ArrayList arrayList) {
        if (arrayList.isEmpty() || ((Integer) arrayList.get(0)).intValue() <= 0) {
            deleteMessages(arrayList, null, null, 0, false);
        } else {
            MessagesStorage.getInstance(this.currentAccount).emptyMessagesMedia(arrayList);
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$Ipa0eWaVXBEreH61uO9jqrPCC0g(this, arrayList));
    }

    public /* synthetic */ void lambda$null$33$MessagesController(ArrayList arrayList) {
        getNewDeleteTask(arrayList, this.currentDeletingTaskChannelId);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
    }

    public void processLoadedDeleteTask(int i, ArrayList<Integer> arrayList, int i2) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$5rDlON84Ua92gPbhSWYw9Sv_2ig(this, arrayList, i));
    }

    public /* synthetic */ void lambda$processLoadedDeleteTask$36$MessagesController(ArrayList arrayList, int i) {
        this.gettingNewDeleteTask = false;
        if (arrayList != null) {
            this.currentDeletingTaskTime = i;
            this.currentDeletingTaskMids = arrayList;
            if (this.currentDeleteTaskRunnable != null) {
                Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
                this.currentDeleteTaskRunnable = null;
            }
            if (!checkDeletingTask(false)) {
                this.currentDeleteTaskRunnable = new -$$Lambda$MessagesController$yKVMzLBuY9JjdZe9ImZ-PuELipE(this);
                Utilities.stageQueue.postRunnable(this.currentDeleteTaskRunnable, ((long) Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentDeletingTaskTime)) * 1000);
                return;
            }
            return;
        }
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
    }

    public /* synthetic */ void lambda$null$35$MessagesController() {
        checkDeletingTask(true);
    }

    public void loadDialogPhotos(int i, int i2, long j, boolean z, int i3) {
        if (z) {
            MessagesStorage.getInstance(this.currentAccount).getDialogPhotos(i, i2, j, i3);
        } else if (i > 0) {
            User user = getUser(Integer.valueOf(i));
            if (user != null) {
                TL_photos_getUserPhotos tL_photos_getUserPhotos = new TL_photos_getUserPhotos();
                tL_photos_getUserPhotos.limit = i2;
                tL_photos_getUserPhotos.offset = 0;
                tL_photos_getUserPhotos.max_id = (long) ((int) j);
                tL_photos_getUserPhotos.user_id = getInputUser(user);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_getUserPhotos, new -$$Lambda$MessagesController$WZMD1Tmg9CzkfKN6c7nkyjvpJFA(this, i, i2, j, i3)), i3);
            }
        } else if (i < 0) {
            TL_messages_search tL_messages_search = new TL_messages_search();
            tL_messages_search.filter = new TL_inputMessagesFilterChatPhotos();
            tL_messages_search.limit = i2;
            tL_messages_search.offset_id = (int) j;
            tL_messages_search.q = "";
            tL_messages_search.peer = getInputPeer(i);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_search, new -$$Lambda$MessagesController$mljRdiyVSAoNeixnSx85I7LZjOE(this, i, i2, j, i3)), i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$37$MessagesController(int i, int i2, long j, int i3, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processLoadedUserPhotos((photos_Photos) tLObject, i, i2, j, false, i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$38$MessagesController(int i, int i2, long j, int i3, TLObject tLObject, TL_error tL_error) {
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
                DataQuery.getInstance(this.currentAccount).removeInline(i);
            } else {
                DataQuery.getInstance(this.currentAccount).removePeer(i);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            TL_contacts_block tL_contacts_block = new TL_contacts_block();
            tL_contacts_block.id = getInputUser(user);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_block, new -$$Lambda$MessagesController$UR0KC8eSIVhCkdT-PUu4ASoxIZg(this, user));
        }
    }

    public /* synthetic */ void lambda$blockUser$39$MessagesController(User user, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            SparseIntArray sparseIntArray = new SparseIntArray();
            sparseIntArray.put(user.id, 1);
            MessagesStorage.getInstance(this.currentAccount).putBlockedUsers(sparseIntArray, false);
        }
    }

    public void setUserBannedRole(int i, User user, TL_chatBannedRights tL_chatBannedRights, boolean z, BaseFragment baseFragment) {
        if (user != null && tL_chatBannedRights != null) {
            TL_channels_editBanned tL_channels_editBanned = new TL_channels_editBanned();
            tL_channels_editBanned.channel = getInputChannel(i);
            tL_channels_editBanned.user_id = getInputUser(user);
            tL_channels_editBanned.banned_rights = tL_chatBannedRights;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editBanned, new -$$Lambda$MessagesController$PTatBcwlbxdtRxQAlB57_yJjqhU(this, i, baseFragment, tL_channels_editBanned, z));
        }
    }

    public /* synthetic */ void lambda$setUserBannedRole$42$MessagesController(int i, BaseFragment baseFragment, TL_channels_editBanned tL_channels_editBanned, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$1dVEAugRY-eNBjCuCw7fZ-d6SjQ(this, i), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$uqMzh59_UK2vW6XHpekt8li6T30(this, tL_error, baseFragment, tL_channels_editBanned, z));
    }

    public /* synthetic */ void lambda$null$40$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$41$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_editBanned tL_channels_editBanned, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editBanned, Boolean.valueOf(z));
    }

    public void setDefaultBannedRole(int i, TL_chatBannedRights tL_chatBannedRights, boolean z, BaseFragment baseFragment) {
        if (tL_chatBannedRights != null) {
            TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights = new TL_messages_editChatDefaultBannedRights();
            tL_messages_editChatDefaultBannedRights.peer = getInputPeer(-i);
            tL_messages_editChatDefaultBannedRights.banned_rights = tL_chatBannedRights;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editChatDefaultBannedRights, new -$$Lambda$MessagesController$1P_GCY-PWvevMznLENn0Y1ly8wA(this, i, baseFragment, tL_messages_editChatDefaultBannedRights, z));
        }
    }

    public /* synthetic */ void lambda$setDefaultBannedRole$45$MessagesController(int i, BaseFragment baseFragment, TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$nf_CcZzYILO5CfS-8KQCWMOSEPI(this, i), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$jTlCLASSNAMELjZXj4VFuUMm4RDeppjZs(this, tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, z));
    }

    public /* synthetic */ void lambda$null$43$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$44$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatDefaultBannedRights, Boolean.valueOf(z));
    }

    public void setUserAdminRole(int i, User user, TL_chatAdminRights tL_chatAdminRights, boolean z, BaseFragment baseFragment, boolean z2) {
        if (user != null && tL_chatAdminRights != null) {
            Chat chat = getChat(Integer.valueOf(i));
            if (ChatObject.isChannel(chat)) {
                TL_channels_editAdmin tL_channels_editAdmin = new TL_channels_editAdmin();
                tL_channels_editAdmin.channel = getInputChannel(chat);
                tL_channels_editAdmin.user_id = getInputUser(user);
                tL_channels_editAdmin.admin_rights = tL_chatAdminRights;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editAdmin, new -$$Lambda$MessagesController$ruvO2GOVq6vr5RbeSeoQf9j2-3I(this, i, baseFragment, tL_channels_editAdmin, z));
                return;
            }
            TL_messages_editChatAdmin tL_messages_editChatAdmin = new TL_messages_editChatAdmin();
            tL_messages_editChatAdmin.chat_id = i;
            tL_messages_editChatAdmin.user_id = getInputUser(user);
            boolean z3 = tL_chatAdminRights.change_info || tL_chatAdminRights.delete_messages || tL_chatAdminRights.ban_users || tL_chatAdminRights.invite_users || tL_chatAdminRights.pin_messages || tL_chatAdminRights.add_admins;
            tL_messages_editChatAdmin.is_admin = z3;
            -$$Lambda$MessagesController$oInAanuig7UbxR7DhBxjsIzAZu4 -__lambda_messagescontroller_oinaanuig7ubxr7dhbxjsizazu4 = new -$$Lambda$MessagesController$oInAanuig7UbxR7DhBxjsIzAZu4(this, i, baseFragment, tL_messages_editChatAdmin);
            if (tL_messages_editChatAdmin.is_admin && z2) {
                addUserToChat(i, user, null, 0, null, baseFragment, new -$$Lambda$MessagesController$re3lVjnmONchziB0xgSqaVhxV5Q(this, tL_messages_editChatAdmin, -__lambda_messagescontroller_oinaanuig7ubxr7dhbxjsizazu4));
            } else {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editChatAdmin, -__lambda_messagescontroller_oinaanuig7ubxr7dhbxjsizazu4);
            }
        }
    }

    public /* synthetic */ void lambda$setUserAdminRole$48$MessagesController(int i, BaseFragment baseFragment, TL_channels_editAdmin tL_channels_editAdmin, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$1jQ0-MBw6KZOFlWcz0l4GlwUKWE(this, i), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$xQRkDsNdc7E8A8vOKZnj4SaYK-I(this, tL_error, baseFragment, tL_channels_editAdmin, z));
    }

    public /* synthetic */ void lambda$null$46$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$null$47$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_editAdmin tL_channels_editAdmin, boolean z) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_editAdmin, Boolean.valueOf(z));
    }

    public /* synthetic */ void lambda$null$49$MessagesController(int i) {
        loadFullChat(i, 0, true);
    }

    public /* synthetic */ void lambda$setUserAdminRole$51$MessagesController(int i, BaseFragment baseFragment, TL_messages_editChatAdmin tL_messages_editChatAdmin, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$G__d657sEgRw5eIUK22CxKPabk8(this, i), 1000);
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$lgHhjVycSWp0O6n3iqFJOy3XH1A(this, tL_error, baseFragment, tL_messages_editChatAdmin));
        }
    }

    public /* synthetic */ void lambda$null$50$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_messages_editChatAdmin tL_messages_editChatAdmin) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editChatAdmin, Boolean.valueOf(false));
    }

    public /* synthetic */ void lambda$setUserAdminRole$52$MessagesController(TL_messages_editChatAdmin tL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editChatAdmin, requestDelegate);
    }

    public void unblockUser(int i) {
        TL_contacts_unblock tL_contacts_unblock = new TL_contacts_unblock();
        User user = getUser(Integer.valueOf(i));
        if (user != null) {
            this.blockedUsers.delete(user.id);
            tL_contacts_unblock.id = getInputUser(user);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_unblock, new -$$Lambda$MessagesController$opwePRyqFhALjQxPzCei2q6RZPU(this, user));
        }
    }

    public /* synthetic */ void lambda$unblockUser$53$MessagesController(User user, TLObject tLObject, TL_error tL_error) {
        MessagesStorage.getInstance(this.currentAccount).deleteBlockedUser(user.id);
    }

    public void getBlockedUsers(boolean z) {
        if (UserConfig.getInstance(this.currentAccount).isClientActivated() && !this.loadingBlockedUsers) {
            this.loadingBlockedUsers = true;
            if (z) {
                MessagesStorage.getInstance(this.currentAccount).getBlockedUsers();
                return;
            }
            TL_contacts_getBlocked tL_contacts_getBlocked = new TL_contacts_getBlocked();
            tL_contacts_getBlocked.offset = 0;
            tL_contacts_getBlocked.limit = 200;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_getBlocked, new -$$Lambda$MessagesController$R5tZYjWVTciy1Jz1GsIoNOmiXf8(this));
        }
    }

    public /* synthetic */ void lambda$getBlockedUsers$54$MessagesController(TLObject tLObject, TL_error tL_error) {
        ArrayList arrayList;
        SparseIntArray sparseIntArray = new SparseIntArray();
        if (tL_error == null) {
            contacts_Blocked contacts_blocked = (contacts_Blocked) tLObject;
            Iterator it = contacts_blocked.blocked.iterator();
            while (it.hasNext()) {
                sparseIntArray.put(((TL_contactBlocked) it.next()).user_id, 1);
            }
            arrayList = contacts_blocked.users;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(contacts_blocked.users, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).putBlockedUsers(sparseIntArray, true);
        } else {
            arrayList = null;
        }
        processLoadedBlockedUsers(sparseIntArray, arrayList, false);
    }

    public void processLoadedBlockedUsers(SparseIntArray sparseIntArray, ArrayList<User> arrayList, boolean z) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$JPDY_4SRGvZvjZ1pDyebULwuKFA(this, arrayList, z, sparseIntArray));
    }

    public /* synthetic */ void lambda$processLoadedBlockedUsers$55$MessagesController(ArrayList arrayList, boolean z, SparseIntArray sparseIntArray) {
        if (arrayList != null) {
            putUsers(arrayList, z);
        }
        this.loadingBlockedUsers = false;
        if (sparseIntArray.size() == 0 && z && !UserConfig.getInstance(this.currentAccount).blockedUsersLoaded) {
            getBlockedUsers(false);
            return;
        }
        if (!z) {
            UserConfig.getInstance(this.currentAccount).blockedUsersLoaded = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        this.blockedUsers = sparseIntArray;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    public void deleteUserPhoto(InputPhoto inputPhoto) {
        if (inputPhoto == null) {
            TL_photos_updateProfilePhoto tL_photos_updateProfilePhoto = new TL_photos_updateProfilePhoto();
            tL_photos_updateProfilePhoto.id = new TL_inputPhotoEmpty();
            UserConfig.getInstance(this.currentAccount).getCurrentUser().photo = new TL_userProfilePhotoEmpty();
            User user = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            }
            if (user != null) {
                user.photo = UserConfig.getInstance(this.currentAccount).getCurrentUser().photo;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_updateProfilePhoto, new -$$Lambda$MessagesController$3uRiNEdYGV34lhCOa9clmeKBj1M(this));
            } else {
                return;
            }
        }
        TL_photos_deletePhotos tL_photos_deletePhotos = new TL_photos_deletePhotos();
        tL_photos_deletePhotos.id.add(inputPhoto);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_deletePhotos, -$$Lambda$MessagesController$lnX7i9eGTDuJovXhCLpz1QuqmLE.INSTANCE);
    }

    public /* synthetic */ void lambda$deleteUserPhoto$57$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            User user = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                putUser(user, false);
            } else {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
            }
            if (user != null) {
                MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
                ArrayList arrayList = new ArrayList();
                arrayList.add(user);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, false, true);
                user.photo = (UserProfilePhoto) tLObject;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$FS6UDAATQ6vB2PUbj-Uk45N_XpU(this));
            }
        }
    }

    public /* synthetic */ void lambda$null$56$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    public void processLoadedUserPhotos(photos_Photos photos_photos, int i, int i2, long j, boolean z, int i3) {
        if (!z) {
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(photos_photos.users, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).putDialogPhotos(i, photos_photos);
        } else if (photos_photos == null || photos_photos.photos.isEmpty()) {
            loadDialogPhotos(i, i2, j, false, i3);
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$NAWdcL37DBZ4gWGx1x8HuDp86gI(this, photos_photos, z, i, i2, i3));
    }

    public /* synthetic */ void lambda$processLoadedUserPhotos$59$MessagesController(photos_Photos photos_photos, boolean z, int i, int i2, int i3) {
        putUsers(photos_photos.users, z);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z), Integer.valueOf(i3), photos_photos.photos);
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
            FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
        }
    }

    public void saveWallpaperToServer(File file, long j, long j2, boolean z, boolean z2, int i, float f, boolean z3, long j3) {
        long j4;
        Throwable e;
        File file2 = file;
        long j5 = j;
        long j6 = j2;
        boolean z4 = z;
        boolean z5 = z2;
        int i2 = i;
        float f2 = f;
        boolean z6 = z3;
        if (this.uploadingWallpaper != null) {
            File file3 = new File(ApplicationLoader.getFilesDirFixed(), this.uploadingWallpaperBlurred ? "wallpaper_original.jpg" : "wallpaper.jpg");
            if (file2 == null || !(file.getAbsolutePath().equals(this.uploadingWallpaper) || file2.equals(file3))) {
                FileLoader.getInstance(this.currentAccount).cancelUploadFile(this.uploadingWallpaper, false);
                this.uploadingWallpaper = null;
            } else {
                this.uploadingWallpaperMotion = z5;
                this.uploadingWallpaperBlurred = z4;
                return;
            }
        }
        if (file2 != null) {
            this.uploadingWallpaper = file.getAbsolutePath();
            this.uploadingWallpaperMotion = z5;
            this.uploadingWallpaperBlurred = z4;
            FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingWallpaper, false, true, 16777216);
        } else if (j6 != 0) {
            TLObject tL_account_installWallPaper;
            TL_inputWallPaper tL_inputWallPaper = new TL_inputWallPaper();
            tL_inputWallPaper.id = j5;
            tL_inputWallPaper.access_hash = j6;
            TL_wallPaperSettings tL_wallPaperSettings = new TL_wallPaperSettings();
            tL_wallPaperSettings.blur = z4;
            tL_wallPaperSettings.motion = z5;
            if (i2 != 0) {
                tL_wallPaperSettings.background_color = i2;
                tL_wallPaperSettings.flags = 1 | tL_wallPaperSettings.flags;
                tL_wallPaperSettings.intensity = (int) (100.0f * f2);
                tL_wallPaperSettings.flags |= 8;
            }
            if (z6) {
                tL_account_installWallPaper = new TL_account_installWallPaper();
                tL_account_installWallPaper.wallpaper = tL_inputWallPaper;
                tL_account_installWallPaper.settings = tL_wallPaperSettings;
            } else {
                tL_account_installWallPaper = new TL_account_saveWallPaper();
                tL_account_installWallPaper.wallpaper = tL_inputWallPaper;
                tL_account_installWallPaper.settings = tL_wallPaperSettings;
            }
            if (j3 != 0) {
                j4 = j3;
            } else {
                NativeByteBuffer nativeByteBuffer;
                try {
                    nativeByteBuffer = new NativeByteBuffer(44);
                    try {
                        nativeByteBuffer.writeInt32(12);
                        nativeByteBuffer.writeInt64(j5);
                        nativeByteBuffer.writeInt64(j6);
                        nativeByteBuffer.writeBool(z4);
                        nativeByteBuffer.writeBool(z5);
                        nativeByteBuffer.writeInt32(i2);
                        nativeByteBuffer.writeDouble((double) f2);
                        nativeByteBuffer.writeBool(z6);
                    } catch (Exception e2) {
                        e = e2;
                    }
                } catch (Exception e3) {
                    e = e3;
                    nativeByteBuffer = null;
                    FileLog.e(e);
                    j4 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_installWallPaper, new -$$Lambda$MessagesController$YmKMKppARt1var_D8216H1-tmM(this, j4, z3, j));
                }
                j4 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_installWallPaper, new -$$Lambda$MessagesController$YmKMKppARt1var_D8216H1-tmM(this, j4, z3, j));
        }
    }

    public /* synthetic */ void lambda$saveWallpaperToServer$60$MessagesController(long j, boolean z, long j2, TLObject tLObject, TL_error tL_error) {
        MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        if (!z && this.uploadingWallpaper != null) {
            Editor edit = getGlobalMainSettings().edit();
            edit.putLong("selectedBackground2", j2);
            edit.commit();
        }
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

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, EncryptedChat encryptedChat, int i, boolean z) {
        deleteMessages(arrayList, arrayList2, encryptedChat, i, z, 0, null);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, EncryptedChat encryptedChat, int i, boolean z, long j, TLObject tLObject) {
        Throwable e;
        if ((arrayList != null && !arrayList.isEmpty()) || tLObject != null) {
            ArrayList arrayList3;
            if (j == 0) {
                if (i == 0) {
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        MessageObject messageObject = (MessageObject) this.dialogMessagesByIds.get(((Integer) arrayList.get(i2)).intValue());
                        if (messageObject != null) {
                            messageObject.deleted = true;
                        }
                    }
                } else {
                    markChannelDialogMessageAsDeleted(arrayList, i);
                }
                arrayList3 = new ArrayList();
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    Integer num = (Integer) arrayList.get(i3);
                    if (num.intValue() > 0) {
                        arrayList3.add(num);
                    }
                }
                MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeleted((ArrayList) arrayList, true, i);
                MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(arrayList, null, true, i);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(i));
            } else {
                arrayList3 = null;
            }
            NativeByteBuffer nativeByteBuffer;
            if (i != 0) {
                if (tLObject != null) {
                    tLObject = (TL_channels_deleteMessages) tLObject;
                } else {
                    tLObject = new TL_channels_deleteMessages();
                    tLObject.id = arrayList3;
                    tLObject.channel = getInputChannel(i);
                    try {
                        nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize() + 8);
                        try {
                            nativeByteBuffer.writeInt32(7);
                            nativeByteBuffer.writeInt32(i);
                            tLObject.serializeToStream(nativeByteBuffer);
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new -$$Lambda$MessagesController$9B6lLHcMl9ABhpIDa9aeiv_K8oA(this, i, j));
                    }
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new -$$Lambda$MessagesController$9B6lLHcMl9ABhpIDa9aeiv_K8oA(this, i, j));
            } else {
                if (!(arrayList2 == null || encryptedChat == null || arrayList2.isEmpty())) {
                    SecretChatHelper.getInstance(this.currentAccount).sendMessagesDeleteMessage(encryptedChat, arrayList2, null);
                }
                if (tLObject != null) {
                    tLObject = (TL_messages_deleteMessages) tLObject;
                } else {
                    tLObject = new TL_messages_deleteMessages();
                    tLObject.id = arrayList3;
                    tLObject.revoke = z;
                    try {
                        nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize() + 8);
                        try {
                            nativeByteBuffer.writeInt32(7);
                            nativeByteBuffer.writeInt32(i);
                            tLObject.serializeToStream(nativeByteBuffer);
                        } catch (Exception e4) {
                            e = e4;
                        }
                    } catch (Exception e5) {
                        e = e5;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new -$$Lambda$MessagesController$py2_IIRHD2uCa7xVMrMM8SVKTaA(this, j));
                    }
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new -$$Lambda$MessagesController$py2_IIRHD2uCa7xVMrMM8SVKTaA(this, j));
            }
        }
    }

    public /* synthetic */ void lambda$deleteMessages$61$MessagesController(int i, long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewChannelDifferenceParams(tL_messages_affectedMessages.pts, tL_messages_affectedMessages.pts_count, i);
        }
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$deleteMessages$62$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
    }

    public void pinMessage(Chat chat, User user, int i, boolean z) {
        if (chat != null || user != null) {
            TL_messages_updatePinnedMessage tL_messages_updatePinnedMessage = new TL_messages_updatePinnedMessage();
            tL_messages_updatePinnedMessage.peer = getInputPeer(chat != null ? -chat.id : user.id);
            tL_messages_updatePinnedMessage.id = i;
            tL_messages_updatePinnedMessage.silent = z ^ 1;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_updatePinnedMessage, new -$$Lambda$MessagesController$zi8soJuISiGh3kdE6Q_S1wPr31c(this));
        }
    }

    public /* synthetic */ void lambda$pinMessage$63$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public void deleteUserChannelHistory(Chat chat, User user, int i) {
        if (i == 0) {
            MessagesStorage.getInstance(this.currentAccount).deleteUserChannelHistory(chat.id, user.id);
        }
        TL_channels_deleteUserHistory tL_channels_deleteUserHistory = new TL_channels_deleteUserHistory();
        tL_channels_deleteUserHistory.channel = getInputChannel(chat);
        tL_channels_deleteUserHistory.user_id = getInputUser(user);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_deleteUserHistory, new -$$Lambda$MessagesController$-lPnTSKn3lxlWS4m8gq50UHxeAw(this, chat, user));
    }

    public /* synthetic */ void lambda$deleteUserChannelHistory$64$MessagesController(Chat chat, User user, TLObject tLObject, TL_error tL_error) {
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

    private void removeDialog(Dialog dialog) {
        if (dialog != null) {
            long j = dialog.id;
            if (this.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
                Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$vWlNj3xTvw31mmNI0cEnDFi4DZ8(this, j));
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

    public /* synthetic */ void lambda$removeDialog$65$MessagesController(long j) {
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
        }
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x028d  */
    public void deleteDialog(long r27, boolean r29, int r30, int r31, boolean r32, org.telegram.tgnet.TLRPC.InputPeer r33, long r34) {
        /*
        r26 = this;
        r11 = r26;
        r5 = r27;
        r0 = r29;
        r7 = r30;
        r9 = r32;
        r1 = 2;
        if (r7 != r1) goto L_0x0017;
    L_0x000d:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.deleteDialog(r5, r7);
        return;
    L_0x0017:
        r2 = 3;
        if (r7 == 0) goto L_0x001c;
    L_0x001a:
        if (r7 != r2) goto L_0x0025;
    L_0x001c:
        r3 = r11.currentAccount;
        r3 = org.telegram.messenger.DataQuery.getInstance(r3);
        r3.uninstallShortcut(r5);
    L_0x0025:
        r3 = (int) r5;
        r4 = 32;
        r12 = r5 >> r4;
        r4 = (int) r12;
        r12 = 0;
        r10 = 0;
        r14 = 1;
        if (r0 == 0) goto L_0x0210;
    L_0x0031:
        r15 = r11.currentAccount;
        r15 = org.telegram.messenger.MessagesStorage.getInstance(r15);
        r15.deleteDialog(r5, r7);
        r15 = r11.dialogs_dict;
        r15 = r15.get(r5);
        r15 = (org.telegram.tgnet.TLRPC.Dialog) r15;
        if (r15 == 0) goto L_0x01b5;
    L_0x0044:
        if (r31 != 0) goto L_0x0059;
    L_0x0046:
        r1 = r15.top_message;
        r1 = java.lang.Math.max(r10, r1);
        r8 = r15.read_inbox_max_id;
        r1 = java.lang.Math.max(r1, r8);
        r8 = r15.read_outbox_max_id;
        r1 = java.lang.Math.max(r1, r8);
        goto L_0x005b;
    L_0x0059:
        r1 = r31;
    L_0x005b:
        if (r7 == 0) goto L_0x0066;
    L_0x005d:
        if (r7 != r2) goto L_0x0060;
    L_0x005f:
        goto L_0x0066;
    L_0x0060:
        r15.unread_count = r10;
        r16 = r3;
        r2 = 0;
        goto L_0x00b3;
    L_0x0066:
        r8 = r11.proxyDialog;
        if (r8 == 0) goto L_0x0074;
    L_0x006a:
        r16 = r3;
        r2 = r8.id;
        r8 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r8 != 0) goto L_0x0076;
    L_0x0072:
        r2 = 1;
        goto L_0x0077;
    L_0x0074:
        r16 = r3;
    L_0x0076:
        r2 = 0;
    L_0x0077:
        if (r2 == 0) goto L_0x009b;
    L_0x0079:
        r11.isLeftProxyChannel = r14;
        r3 = r11.proxyDialog;
        r17 = r15;
        r14 = r3.id;
        r3 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));
        if (r3 >= 0) goto L_0x0094;
    L_0x0085:
        r3 = (int) r14;
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r11.getChat(r3);
        if (r3 == 0) goto L_0x0094;
    L_0x0091:
        r8 = 1;
        r3.left = r8;
    L_0x0094:
        r3 = 0;
        r11.sortDialogs(r3);
        r15 = r17;
        goto L_0x00b3;
    L_0x009b:
        r11.removeDialog(r15);
        r3 = r11.nextDialogsCacheOffset;
        r14 = r15.folder_id;
        r3 = r3.get(r14, r10);
        if (r3 <= 0) goto L_0x00b3;
    L_0x00a8:
        r14 = r11.nextDialogsCacheOffset;
        r8 = r15.folder_id;
        r17 = 1;
        r3 = r3 + -1;
        r14.put(r8, r3);
    L_0x00b3:
        if (r2 != 0) goto L_0x01b2;
    L_0x00b5:
        r3 = r11.dialogMessage;
        r12 = r15.id;
        r3 = r3.get(r12);
        r3 = (org.telegram.messenger.MessageObject) r3;
        r8 = r11.dialogMessage;
        r12 = r15.id;
        r8.remove(r12);
        if (r3 == 0) goto L_0x00d6;
    L_0x00c8:
        r8 = r3.getId();
        r12 = r11.dialogMessagesByIds;
        r13 = r3.getId();
        r12.remove(r13);
        goto L_0x00e7;
    L_0x00d6:
        r8 = r15.top_message;
        r3 = r11.dialogMessagesByIds;
        r3 = r3.get(r8);
        r3 = (org.telegram.messenger.MessageObject) r3;
        r12 = r11.dialogMessagesByIds;
        r13 = r15.top_message;
        r12.remove(r13);
    L_0x00e7:
        r12 = r8;
        if (r3 == 0) goto L_0x00f9;
    L_0x00ea:
        r3 = r3.messageOwner;
        r13 = r3.random_id;
        r18 = 0;
        r3 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1));
        if (r3 == 0) goto L_0x00f9;
    L_0x00f4:
        r3 = r11.dialogMessagesByRandomIds;
        r3.remove(r13);
    L_0x00f9:
        r3 = 1;
        if (r7 != r3) goto L_0x01ad;
    L_0x00fc:
        if (r16 == 0) goto L_0x01ad;
    L_0x00fe:
        if (r12 <= 0) goto L_0x01ad;
    L_0x0100:
        r3 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r3.<init>();
        r12 = r15.top_message;
        r3.id = r12;
        r12 = r11.currentAccount;
        r12 = org.telegram.messenger.UserConfig.getInstance(r12);
        r12 = r12.getClientUserId();
        r12 = (long) r12;
        r14 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1));
        if (r14 != 0) goto L_0x011a;
    L_0x0118:
        r12 = 1;
        goto L_0x011b;
    L_0x011a:
        r12 = 0;
    L_0x011b:
        r3.out = r12;
        r12 = r11.currentAccount;
        r12 = org.telegram.messenger.UserConfig.getInstance(r12);
        r12 = r12.getClientUserId();
        r3.from_id = r12;
        r12 = r3.flags;
        r12 = r12 | 256;
        r3.flags = r12;
        r12 = new org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
        r12.<init>();
        r3.action = r12;
        r12 = r15.last_message_date;
        r3.date = r12;
        r12 = r16;
        r13 = (long) r12;
        r3.dialog_id = r13;
        if (r12 <= 0) goto L_0x014d;
    L_0x0141:
        r13 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r13.<init>();
        r3.to_id = r13;
        r13 = r3.to_id;
        r13.user_id = r12;
        goto L_0x0173;
    L_0x014d:
        r13 = -r12;
        r14 = java.lang.Integer.valueOf(r13);
        r14 = r11.getChat(r14);
        r14 = org.telegram.messenger.ChatObject.isChannel(r14);
        if (r14 == 0) goto L_0x0168;
    L_0x015c:
        r14 = new org.telegram.tgnet.TLRPC$TL_peerChannel;
        r14.<init>();
        r3.to_id = r14;
        r14 = r3.to_id;
        r14.channel_id = r13;
        goto L_0x0173;
    L_0x0168:
        r14 = new org.telegram.tgnet.TLRPC$TL_peerChat;
        r14.<init>();
        r3.to_id = r14;
        r14 = r3.to_id;
        r14.chat_id = r13;
    L_0x0173:
        r13 = new org.telegram.messenger.MessageObject;
        r14 = r11.currentAccount;
        r15 = r11.createdDialogIds;
        r8 = r3.dialog_id;
        r8 = java.lang.Long.valueOf(r8);
        r8 = r15.contains(r8);
        r13.<init>(r14, r3, r8);
        r8 = new java.util.ArrayList;
        r8.<init>();
        r8.add(r13);
        r9 = new java.util.ArrayList;
        r9.<init>();
        r9.add(r3);
        r11.updateInterfaceWithMessages(r5, r8);
        r3 = r11.currentAccount;
        r20 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r22 = 0;
        r23 = 1;
        r24 = 0;
        r25 = 0;
        r21 = r9;
        r20.putMessages(r21, r22, r23, r24, r25);
        goto L_0x01b9;
    L_0x01ad:
        r12 = r16;
        r15.top_message = r10;
        goto L_0x01b9;
    L_0x01b2:
        r12 = r16;
        goto L_0x01b9;
    L_0x01b5:
        r12 = r3;
        r1 = r31;
        r2 = 0;
    L_0x01b9:
        r3 = r11.dialogsInTransaction;
        if (r3 != 0) goto L_0x01fc;
    L_0x01bd:
        if (r2 == 0) goto L_0x01d4;
    L_0x01bf:
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r8 = 1;
        r9 = new java.lang.Object[r8];
        r13 = java.lang.Boolean.valueOf(r8);
        r9[r10] = r13;
        r2.postNotificationName(r3, r9);
        goto L_0x01fc;
    L_0x01d4:
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r9 = new java.lang.Object[r10];
        r2.postNotificationName(r3, r9);
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.removeAllMessagesFromDialog;
        r9 = 2;
        r9 = new java.lang.Object[r9];
        r13 = java.lang.Long.valueOf(r27);
        r9[r10] = r13;
        r13 = java.lang.Boolean.valueOf(r10);
        r8 = 1;
        r9[r8] = r13;
        r2.postNotificationName(r3, r9);
    L_0x01fc:
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r2 = r2.getStorageQueue();
        r3 = new org.telegram.messenger.-$$Lambda$MessagesController$fi2heANmHg4iJXLk9Zkj_J0ni7w;
        r3.<init>(r11, r5);
        r2.postRunnable(r3);
        r9 = r1;
        goto L_0x0213;
    L_0x0210:
        r12 = r3;
        r9 = r31;
    L_0x0213:
        r1 = 1;
        if (r4 == r1) goto L_0x0324;
    L_0x0216:
        r1 = 3;
        if (r7 != r1) goto L_0x021b;
    L_0x0219:
        goto L_0x0324;
    L_0x021b:
        if (r12 == 0) goto L_0x0305;
    L_0x021d:
        if (r33 != 0) goto L_0x0225;
    L_0x021f:
        r1 = r11.getInputPeer(r12);
        r12 = r1;
        goto L_0x0227;
    L_0x0225:
        r12 = r33;
    L_0x0227:
        if (r12 != 0) goto L_0x022a;
    L_0x0229:
        return;
    L_0x022a:
        r1 = r12 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r1 == 0) goto L_0x0237;
    L_0x0231:
        if (r7 == 0) goto L_0x0234;
    L_0x0233:
        goto L_0x0237;
    L_0x0234:
        r13 = r32;
        goto L_0x0287;
    L_0x0237:
        if (r9 <= 0) goto L_0x0244;
    L_0x0239:
        if (r9 == r2) goto L_0x0244;
    L_0x023b:
        r3 = r11.deletedHistory;
        r4 = java.lang.Integer.valueOf(r9);
        r3.put(r5, r4);
    L_0x0244:
        r3 = 0;
        r13 = (r34 > r3 ? 1 : (r34 == r3 ? 0 : -1));
        if (r13 != 0) goto L_0x0234;
    L_0x024a:
        r3 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0275 }
        r4 = r12.getObjectSize();	 Catch:{ Exception -> 0x0275 }
        r4 = r4 + 28;
        r3.<init>(r4);	 Catch:{ Exception -> 0x0275 }
        r4 = 13;
        r3.writeInt32(r4);	 Catch:{ Exception -> 0x0271 }
        r3.writeInt64(r5);	 Catch:{ Exception -> 0x0271 }
        r3.writeBool(r0);	 Catch:{ Exception -> 0x0271 }
        r3.writeInt32(r7);	 Catch:{ Exception -> 0x0271 }
        r3.writeInt32(r9);	 Catch:{ Exception -> 0x0271 }
        r13 = r32;
        r3.writeBool(r13);	 Catch:{ Exception -> 0x026f }
        r12.serializeToStream(r3);	 Catch:{ Exception -> 0x026f }
        goto L_0x027c;
    L_0x026f:
        r0 = move-exception;
        goto L_0x0279;
    L_0x0271:
        r0 = move-exception;
        r13 = r32;
        goto L_0x0279;
    L_0x0275:
        r0 = move-exception;
        r13 = r32;
        r3 = 0;
    L_0x0279:
        org.telegram.messenger.FileLog.e(r0);
    L_0x027c:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r3 = r0.createPendingTask(r3);
        goto L_0x0289;
    L_0x0287:
        r3 = r34;
    L_0x0289:
        r0 = 64;
        if (r1 == 0) goto L_0x02d4;
    L_0x028d:
        if (r7 != 0) goto L_0x029f;
    L_0x028f:
        r7 = 0;
        r0 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
        if (r0 == 0) goto L_0x029e;
    L_0x0295:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.removePendingTask(r3);
    L_0x029e:
        return;
    L_0x029f:
        r1 = new org.telegram.tgnet.TLRPC$TL_channels_deleteHistory;
        r1.<init>();
        r7 = new org.telegram.tgnet.TLRPC$TL_inputChannel;
        r7.<init>();
        r1.channel = r7;
        r7 = r1.channel;
        r8 = r12.channel_id;
        r7.channel_id = r8;
        r12 = r12.access_hash;
        r7.access_hash = r12;
        if (r9 <= 0) goto L_0x02b8;
    L_0x02b7:
        goto L_0x02bb;
    L_0x02b8:
        r9 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x02bb:
        r1.max_id = r9;
        r2 = r11.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r7 = new org.telegram.messenger.-$$Lambda$MessagesController$yR0Wl_-tkImGpY86P4U53sAMzc8;
        r29 = r7;
        r30 = r26;
        r31 = r3;
        r33 = r27;
        r29.<init>(r30, r31, r33);
        r2.sendRequest(r1, r7, r0);
        goto L_0x0324;
    L_0x02d4:
        r14 = new org.telegram.tgnet.TLRPC$TL_messages_deleteHistory;
        r14.<init>();
        r14.peer = r12;
        if (r7 != 0) goto L_0x02de;
    L_0x02dd:
        goto L_0x02df;
    L_0x02de:
        r2 = r9;
    L_0x02df:
        r14.max_id = r2;
        if (r7 == 0) goto L_0x02e5;
    L_0x02e3:
        r8 = 1;
        goto L_0x02e6;
    L_0x02e5:
        r8 = 0;
    L_0x02e6:
        r14.just_clear = r8;
        r14.revoke = r13;
        r1 = r11.currentAccount;
        r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$GgsMvhuFaAXbZ2H0PLOXM_0uOo4;
        r1 = r10;
        r2 = r26;
        r5 = r27;
        r7 = r30;
        r8 = r9;
        r9 = r32;
        r13 = r10;
        r10 = r12;
        r1.<init>(r2, r3, r5, r7, r8, r9, r10);
        r15.sendRequest(r14, r13, r0);
        goto L_0x0324;
    L_0x0305:
        r1 = 1;
        if (r7 != r1) goto L_0x031b;
    L_0x0308:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.SecretChatHelper.getInstance(r0);
        r1 = java.lang.Integer.valueOf(r4);
        r1 = r11.getEncryptedChat(r1);
        r2 = 0;
        r0.sendClearHistoryMessage(r1, r2);
        goto L_0x0324;
    L_0x031b:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.SecretChatHelper.getInstance(r0);
        r0.declineSecretChat(r4);
    L_0x0324:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteDialog(long, boolean, int, int, boolean, org.telegram.tgnet.TLRPC$InputPeer, long):void");
    }

    public /* synthetic */ void lambda$deleteDialog$67$MessagesController(long j) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$KpbyllFi8c4LuRHme4BUUg14Dek(this, j));
    }

    public /* synthetic */ void lambda$null$66$MessagesController(long j) {
        NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(j);
    }

    public /* synthetic */ void lambda$deleteDialog$69$MessagesController(long j, long j2, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$DwkK6UVHlAwcTaxAil0Ui6ghGR0(this, j2));
    }

    public /* synthetic */ void lambda$null$68$MessagesController(long j) {
        this.deletedHistory.remove(j);
    }

    public /* synthetic */ void lambda$deleteDialog$70$MessagesController(long j, long j2, int i, int i2, boolean z, InputPeer inputPeer, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
        if (tL_error == null) {
            TL_messages_affectedHistory tL_messages_affectedHistory = (TL_messages_affectedHistory) tLObject;
            if (tL_messages_affectedHistory.offset > 0) {
                deleteDialog(j2, false, i, i2, z, inputPeer, 0);
            }
            processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            MessagesStorage.getInstance(this.currentAccount).onDeleteQueryComplete(j2);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveGif, new -$$Lambda$MessagesController$AJRaVVVB2-z__jsu2wGba-x0X0k(this, obj, tL_messages_saveGif));
        }
    }

    public /* synthetic */ void lambda$saveGif$71$MessagesController(Object obj, TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            FileRefController.getInstance(this.currentAccount).requestReference(obj, tL_messages_saveGif);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveRecentSticker, new -$$Lambda$MessagesController$Ac6iMOZ3v5XiuSoBQXhRvLk2mds(this, obj, tL_messages_saveRecentSticker));
        }
    }

    public /* synthetic */ void lambda$saveRecentSticker$72$MessagesController(Object obj, TL_messages_saveRecentSticker tL_messages_saveRecentSticker, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            FileRefController.getInstance(this.currentAccount).requestReference(obj, tL_messages_saveRecentSticker);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new -$$Lambda$MessagesController$2gEZaEhNLvSnEOT8iEFZtRspSU0(this, num));
        }
    }

    public /* synthetic */ void lambda$loadChannelParticipants$74$MessagesController(Integer num, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$atBLO-w4l-c1A3YUDnxbyiiuEDs(this, tL_error, tLObject, num));
    }

    public /* synthetic */ void lambda$null$73$MessagesController(TL_error tL_error, TLObject tLObject, Integer num) {
        if (tL_error == null) {
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            putUsers(tL_channels_channelParticipants.users, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(num.intValue(), tL_channels_channelParticipants.participants);
            this.loadedFullParticipants.add(num);
        }
        this.loadingFullParticipants.remove(num);
    }

    public void loadChatInfo(int i, CountDownLatch countDownLatch, boolean z) {
        MessagesStorage.getInstance(this.currentAccount).loadChatInfo(i, countDownLatch, z, false);
    }

    public void processChatInfo(int i, ChatFull chatFull, ArrayList<User> arrayList, boolean z, boolean z2, boolean z3, MessageObject messageObject) {
        if (z && i > 0 && !z3) {
            loadFullChat(i, 0, z2);
        }
        if (chatFull != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$aj9gT3RqpNCMmoGiSwlydWLbsP8(this, arrayList, z, chatFull, z3, messageObject));
        }
    }

    public /* synthetic */ void lambda$processChatInfo$75$MessagesController(ArrayList arrayList, boolean z, ChatFull chatFull, boolean z2, MessageObject messageObject) {
        putUsers(arrayList, z);
        if (chatFull.stickerset != null) {
            DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(z2), messageObject);
    }

    public void loadUserInfo(User user, boolean z, int i) {
        MessagesStorage.getInstance(this.currentAccount).loadUserInfo(user, z, i);
    }

    public void processUserInfo(User user, UserFull userFull, boolean z, boolean z2, MessageObject messageObject, int i) {
        if (z) {
            loadFullUser(user, i, z2);
        }
        if (userFull != null) {
            if (this.fullUsers.get(user.id) == null) {
                this.fullUsers.put(user.id, userFull);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$vvar_kQiTAupYlV5PFZpkpSFLz4(this, user, userFull, messageObject));
        }
    }

    public /* synthetic */ void lambda$processUserInfo$76$MessagesController(User user, UserFull userFull, MessageObject messageObject) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.id), userFull, messageObject);
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
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            TL_account_updateStatus tL_account_updateStatus;
            if (ConnectionsManager.getInstance(this.currentAccount).getPauseTime() == 0 && ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePausedStageQueue) {
                if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && this.statusSettingState != 1 && (this.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000 || this.offlineSent)) {
                    this.statusSettingState = 1;
                    if (this.statusRequest != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.statusRequest, true);
                    }
                    tL_account_updateStatus = new TL_account_updateStatus();
                    tL_account_updateStatus.offline = false;
                    this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateStatus, new -$$Lambda$MessagesController$cfQiiK2gKJLj20bAsG92QXAhxfU(this));
                }
            } else if (!(this.statusSettingState == 2 || this.offlineSent || Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance(this.currentAccount).getPauseTime()) < 2000)) {
                this.statusSettingState = 2;
                if (this.statusRequest != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.statusRequest, true);
                }
                tL_account_updateStatus = new TL_account_updateStatus();
                tL_account_updateStatus.offline = true;
                this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateStatus, new -$$Lambda$MessagesController$ueghtSvHFDbkkKRlzH3zhB7vPCY(this));
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
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessagesViews, new -$$Lambda$MessagesController$zcPVttRNdPhRMvGP9lvTiB9rsK8(this, keyAt, tL_messages_getMessagesViews));
                    i++;
                }
                this.channelViewsToSend.clear();
            }
            if (this.pollsToCheckSize > 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$EvGoS6VQaul799anwg65luJP3kM(this));
            }
        }
        if (!this.onlinePrivacy.isEmpty()) {
            arrayList = null;
            keyAt = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$tRF6Ij6nWDh57hakeYO-dBj2yNg(this));
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
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getOnlines, new -$$Lambda$MessagesController$sjofkBeH5gGrYjjT8WNKFtTfESE(this, keyAt2));
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$NCgFAuLO8OXQTI8IHmqXB1TzYHE(this));
            }
        }
        if (Theme.selectedAutoNightType == 1 && Math.abs(currentTimeMillis - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.themeCheckRunnable);
            lastThemeCheckTime = currentTimeMillis;
        }
        if (UserConfig.getInstance(this.currentAccount).savedPasswordHash != null && Math.abs(currentTimeMillis - lastPasswordCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.passwordCheckRunnable);
            lastPasswordCheckTime = currentTimeMillis;
        }
        if (this.lastPushRegisterSendTime != 0 && Math.abs(SystemClock.elapsedRealtime() - this.lastPushRegisterSendTime) >= 10800000) {
            GcmPushListenerService.sendRegistrationToServer(SharedConfig.pushString);
        }
        LocationController.getInstance(this.currentAccount).update();
        checkProxyInfoInternal(false);
        checkTosUpdate();
    }

    public /* synthetic */ void lambda$updateTimerProc$77$MessagesController(TLObject tLObject, TL_error tL_error) {
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

    public /* synthetic */ void lambda$updateTimerProc$78$MessagesController(TLObject tLObject, TL_error tL_error) {
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

    public /* synthetic */ void lambda$updateTimerProc$80$MessagesController(int i, TL_messages_getMessagesViews tL_messages_getMessagesViews, TLObject tLObject, TL_error tL_error) {
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
            MessagesStorage.getInstance(this.currentAccount).putChannelViews(sparseArray, tL_messages_getMessagesViews.peer instanceof TL_inputPeerChannel);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$8xLr-ghSkkpTTqlNVF_KzHO2X20(this, sparseArray));
        }
    }

    public /* synthetic */ void lambda$null$79$MessagesController(SparseArray sparseArray) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, sparseArray);
    }

    public /* synthetic */ void lambda$updateTimerProc$82$MessagesController() {
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
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPollResults, new -$$Lambda$MessagesController$WAhoxMlOe9rKGiImOpgvJIHyQtk(this));
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

    public /* synthetic */ void lambda$null$81$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$updateTimerProc$83$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    public /* synthetic */ void lambda$updateTimerProc$85$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            TL_chatOnlines tL_chatOnlines = (TL_chatOnlines) tLObject;
            MessagesStorage.getInstance(this.currentAccount).updateChatOnlineCount(i, tL_chatOnlines.onlines);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$OmsxlAaybMmJyhzcSO6qE2vdkAg(this, i, tL_chatOnlines));
        }
    }

    public /* synthetic */ void lambda$null$84$MessagesController(int i, TL_chatOnlines tL_chatOnlines) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatOnlineCountDidLoad, Integer.valueOf(i), Integer.valueOf(tL_chatOnlines.onlines));
    }

    public /* synthetic */ void lambda$updateTimerProc$86$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
    }

    private void checkTosUpdate() {
        if (this.nextTosCheckTime <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() && !this.checkingTosUpdate && UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            this.checkingTosUpdate = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getTermsOfServiceUpdate(), new -$$Lambda$MessagesController$5GO5q-JFS4BEFNcWBCNsIVVD9gY(this));
        }
    }

    public /* synthetic */ void lambda$checkTosUpdate$88$MessagesController(TLObject tLObject, TL_error tL_error) {
        this.checkingTosUpdate = false;
        if (tLObject instanceof TL_help_termsOfServiceUpdateEmpty) {
            this.nextTosCheckTime = ((TL_help_termsOfServiceUpdateEmpty) tLObject).expires;
        } else if (tLObject instanceof TL_help_termsOfServiceUpdate) {
            TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate = (TL_help_termsOfServiceUpdate) tLObject;
            this.nextTosCheckTime = tL_help_termsOfServiceUpdate.expires;
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$fH6gNCG5Tk-fGc0NroG1SVH_0DA(this, tL_help_termsOfServiceUpdate));
        } else {
            this.nextTosCheckTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 3600;
        }
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
    }

    public /* synthetic */ void lambda$null$87$MessagesController(TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(4), tL_help_termsOfServiceUpdate.terms_of_service);
    }

    public void checkProxyInfo(boolean z) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$UmwP8okyEt8khTz852DsKDuGJ70(this, z));
    }

    public /* synthetic */ void lambda$checkProxyInfo$89$MessagesController(boolean z) {
        checkProxyInfoInternal(z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0097  */
    private void checkProxyInfoInternal(boolean r11) {
        /*
        r10 = this;
        r0 = 0;
        if (r11 == 0) goto L_0x0009;
    L_0x0003:
        r1 = r10.checkingProxyInfo;
        if (r1 == 0) goto L_0x0009;
    L_0x0007:
        r10.checkingProxyInfo = r0;
    L_0x0009:
        if (r11 != 0) goto L_0x0019;
    L_0x000b:
        r11 = r10.nextProxyInfoCheckTime;
        r1 = r10.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        if (r11 > r1) goto L_0x001d;
    L_0x0019:
        r11 = r10.checkingProxyInfo;
        if (r11 == 0) goto L_0x001e;
    L_0x001d:
        return;
    L_0x001e:
        r11 = r10.checkingProxyInfoRequestId;
        r1 = 1;
        if (r11 == 0) goto L_0x0030;
    L_0x0023:
        r11 = r10.currentAccount;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11);
        r2 = r10.checkingProxyInfoRequestId;
        r11.cancelRequest(r2, r1);
        r10.checkingProxyInfoRequestId = r0;
    L_0x0030:
        r11 = getGlobalMainSettings();
        r2 = "proxy_enabled";
        r2 = r11.getBoolean(r2, r0);
        r3 = "";
        r4 = "proxy_ip";
        r4 = r11.getString(r4, r3);
        r5 = "proxy_secret";
        r11 = r11.getString(r5, r3);
        r5 = r10.proxyDialogId;
        r7 = 0;
        r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r3 == 0) goto L_0x006b;
    L_0x0050:
        r3 = r10.proxyDialogAddress;
        if (r3 == 0) goto L_0x006b;
    L_0x0054:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r4);
        r5.append(r11);
        r5 = r5.toString();
        r3 = r3.equals(r5);
        if (r3 != 0) goto L_0x006b;
    L_0x0069:
        r3 = 1;
        goto L_0x006c;
    L_0x006b:
        r3 = 0;
    L_0x006c:
        r5 = 2;
        if (r2 == 0) goto L_0x0094;
    L_0x006f:
        r2 = android.text.TextUtils.isEmpty(r4);
        if (r2 != 0) goto L_0x0094;
    L_0x0075:
        r2 = android.text.TextUtils.isEmpty(r11);
        if (r2 != 0) goto L_0x0094;
    L_0x007b:
        r10.checkingProxyInfo = r1;
        r2 = new org.telegram.tgnet.TLRPC$TL_help_getProxyData;
        r2.<init>();
        r6 = r10.currentAccount;
        r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6);
        r9 = new org.telegram.messenger.-$$Lambda$MessagesController$SaWsznydC5Y7TDBVTp_FLPmMJQE;
        r9.<init>(r10, r4, r11);
        r11 = r6.sendRequest(r2, r9);
        r10.checkingProxyInfoRequestId = r11;
        goto L_0x0095;
    L_0x0094:
        r3 = 2;
    L_0x0095:
        if (r3 == 0) goto L_0x00e0;
    L_0x0097:
        r10.proxyDialogId = r7;
        r11 = 0;
        r10.proxyDialogAddress = r11;
        r11 = getGlobalMainSettings();
        r11 = r11.edit();
        r6 = r10.proxyDialogId;
        r2 = "proxy_dialog";
        r11 = r11.putLong(r2, r6);
        r2 = "proxyDialogAddress";
        r11 = r11.remove(r2);
        r11.commit();
        r11 = r10.currentAccount;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11);
        r11 = r11.getCurrentTime();
        r11 = r11 + 3600;
        r10.nextProxyInfoCheckTime = r11;
        if (r3 != r5) goto L_0x00d8;
    L_0x00c5:
        r10.checkingProxyInfo = r0;
        r11 = r10.checkingProxyInfoRequestId;
        if (r11 == 0) goto L_0x00d8;
    L_0x00cb:
        r11 = r10.currentAccount;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11);
        r2 = r10.checkingProxyInfoRequestId;
        r11.cancelRequest(r2, r1);
        r10.checkingProxyInfoRequestId = r0;
    L_0x00d8:
        r11 = new org.telegram.messenger.-$$Lambda$MessagesController$FvadbQtD8d2glGqiRUodW_Fj5b8;
        r11.<init>(r10);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r11);
    L_0x00e0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.checkProxyInfoInternal(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ab  */
    /* JADX WARNING: Missing block: B:22:0x004c, code skipped:
            if (r6.restricted == false) goto L_0x0025;
     */
    /* JADX WARNING: Missing block: B:32:0x0075, code skipped:
            if (r6.restricted == false) goto L_0x0025;
     */
    public /* synthetic */ void lambda$checkProxyInfoInternal$95$MessagesController(java.lang.String r10, java.lang.String r11, org.telegram.tgnet.TLObject r12, org.telegram.tgnet.TLRPC.TL_error r13) {
        /*
        r9 = this;
        r13 = r9.checkingProxyInfoRequestId;
        if (r13 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_help_proxyDataEmpty;
        r0 = "proxyDialogAddress";
        r1 = "proxy_dialog";
        r2 = 0;
        r3 = 1;
        if (r13 == 0) goto L_0x0017;
    L_0x000f:
        r12 = (org.telegram.tgnet.TLRPC.TL_help_proxyDataEmpty) r12;
        r10 = r12.expires;
        r9.nextProxyInfoCheckTime = r10;
        goto L_0x00c2;
    L_0x0017:
        r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;
        if (r13 == 0) goto L_0x00b4;
    L_0x001b:
        r12 = (org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo) r12;
        r13 = r12.peer;
        r4 = r13.user_id;
        if (r4 == 0) goto L_0x0027;
    L_0x0023:
        r3 = (long) r4;
        r4 = r3;
    L_0x0025:
        r3 = 0;
        goto L_0x007b;
    L_0x0027:
        r4 = r13.chat_id;
        if (r4 == 0) goto L_0x0052;
    L_0x002b:
        r13 = -r4;
        r4 = (long) r13;
        r13 = 0;
    L_0x002e:
        r6 = r12.chats;
        r6 = r6.size();
        if (r13 >= r6) goto L_0x0025;
    L_0x0036:
        r6 = r12.chats;
        r6 = r6.get(r13);
        r6 = (org.telegram.tgnet.TLRPC.Chat) r6;
        r7 = r6.id;
        r8 = r12.peer;
        r8 = r8.chat_id;
        if (r7 != r8) goto L_0x004f;
    L_0x0046:
        r13 = r6.kicked;
        if (r13 != 0) goto L_0x007b;
    L_0x004a:
        r13 = r6.restricted;
        if (r13 == 0) goto L_0x0025;
    L_0x004e:
        goto L_0x007b;
    L_0x004f:
        r13 = r13 + 1;
        goto L_0x002e;
    L_0x0052:
        r13 = r13.channel_id;
        r13 = -r13;
        r4 = (long) r13;
        r13 = 0;
    L_0x0057:
        r6 = r12.chats;
        r6 = r6.size();
        if (r13 >= r6) goto L_0x0025;
    L_0x005f:
        r6 = r12.chats;
        r6 = r6.get(r13);
        r6 = (org.telegram.tgnet.TLRPC.Chat) r6;
        r7 = r6.id;
        r8 = r12.peer;
        r8 = r8.channel_id;
        if (r7 != r8) goto L_0x0078;
    L_0x006f:
        r13 = r6.kicked;
        if (r13 != 0) goto L_0x007b;
    L_0x0073:
        r13 = r6.restricted;
        if (r13 == 0) goto L_0x0025;
    L_0x0077:
        goto L_0x007b;
    L_0x0078:
        r13 = r13 + 1;
        goto L_0x0057;
    L_0x007b:
        r9.proxyDialogId = r4;
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r10);
        r13.append(r11);
        r10 = r13.toString();
        r9.proxyDialogAddress = r10;
        r10 = getGlobalMainSettings();
        r10 = r10.edit();
        r6 = r9.proxyDialogId;
        r10 = r10.putLong(r1, r6);
        r11 = r9.proxyDialogAddress;
        r10 = r10.putString(r0, r11);
        r10.commit();
        r10 = r12.expires;
        r9.nextProxyInfoCheckTime = r10;
        if (r3 != 0) goto L_0x00c2;
    L_0x00ab:
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$0mxeOwB8KketmB2b8T1pp9iml0o;
        r10.<init>(r9, r4, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r10);
        goto L_0x00c2;
    L_0x00b4:
        r10 = r9.currentAccount;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r10 = r10.getCurrentTime();
        r10 = r10 + 3600;
        r9.nextProxyInfoCheckTime = r10;
    L_0x00c2:
        if (r3 == 0) goto L_0x00e9;
    L_0x00c4:
        r10 = 0;
        r9.proxyDialogId = r10;
        r10 = getGlobalMainSettings();
        r10 = r10.edit();
        r11 = r9.proxyDialogId;
        r10 = r10.putLong(r1, r11);
        r10 = r10.remove(r0);
        r10.commit();
        r9.checkingProxyInfoRequestId = r2;
        r9.checkingProxyInfo = r2;
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$Kx39oNX6WpYn1825Y_0aDSwCf7M;
        r10.<init>(r9);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r10);
    L_0x00e9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$checkProxyInfoInternal$95$MessagesController(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$null$93$MessagesController(long j, TL_help_proxyDataPromo tL_help_proxyDataPromo) {
        this.proxyDialog = (Dialog) this.dialogs_dict.get(j);
        int i = 0;
        if (this.proxyDialog != null) {
            this.checkingProxyInfo = false;
            sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
            return;
        }
        SparseArray sparseArray = new SparseArray();
        SparseArray sparseArray2 = new SparseArray();
        for (int i2 = 0; i2 < tL_help_proxyDataPromo.users.size(); i2++) {
            User user = (User) tL_help_proxyDataPromo.users.get(i2);
            sparseArray.put(user.id, user);
        }
        while (i < tL_help_proxyDataPromo.chats.size()) {
            Chat chat = (Chat) tL_help_proxyDataPromo.chats.get(i);
            sparseArray2.put(chat.id, chat);
            i++;
        }
        TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
        TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
        Peer peer = tL_help_proxyDataPromo.peer;
        int i3;
        InputPeer inputPeer;
        Chat chat2;
        if (peer.user_id != 0) {
            tL_inputDialogPeer.peer = new TL_inputPeerUser();
            InputPeer inputPeer2 = tL_inputDialogPeer.peer;
            i3 = tL_help_proxyDataPromo.peer.user_id;
            inputPeer2.user_id = i3;
            User user2 = (User) sparseArray.get(i3);
            if (user2 != null) {
                tL_inputDialogPeer.peer.access_hash = user2.access_hash;
            }
        } else if (peer.chat_id != 0) {
            tL_inputDialogPeer.peer = new TL_inputPeerChat();
            inputPeer = tL_inputDialogPeer.peer;
            i3 = tL_help_proxyDataPromo.peer.chat_id;
            inputPeer.chat_id = i3;
            chat2 = (Chat) sparseArray2.get(i3);
            if (chat2 != null) {
                tL_inputDialogPeer.peer.access_hash = chat2.access_hash;
            }
        } else {
            tL_inputDialogPeer.peer = new TL_inputPeerChannel();
            inputPeer = tL_inputDialogPeer.peer;
            i3 = tL_help_proxyDataPromo.peer.channel_id;
            inputPeer.channel_id = i3;
            chat2 = (Chat) sparseArray2.get(i3);
            if (chat2 != null) {
                tL_inputDialogPeer.peer.access_hash = chat2.access_hash;
            }
        }
        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$jGzHg4Vpn20r2Dvt16XQexkQnM8(this, tL_help_proxyDataPromo, j));
    }

    public /* synthetic */ void lambda$null$92$MessagesController(TL_help_proxyDataPromo tL_help_proxyDataPromo, long j, TLObject tLObject, TL_error tL_error) {
        if (this.checkingProxyInfoRequestId != 0) {
            this.checkingProxyInfoRequestId = 0;
            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
            if (tL_messages_peerDialogs == null || tL_messages_peerDialogs.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$__dEtAqUXm-ipyTn-k8IK_xJUDw(this));
            } else {
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_help_proxyDataPromo.users, tL_help_proxyDataPromo.chats, true, true);
                TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
                tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
                tL_messages_dialogs.users = tL_messages_peerDialogs.users;
                tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
                tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
                MessagesStorage.getInstance(this.currentAccount).putDialogs(tL_messages_dialogs, 2);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$0hzwDCgQaQ7-pO9ojVbt_LU0UiE(this, tL_help_proxyDataPromo, tL_messages_peerDialogs, j));
            }
            this.checkingProxyInfo = false;
        }
    }

    public /* synthetic */ void lambda$null$90$MessagesController(TL_help_proxyDataPromo tL_help_proxyDataPromo, TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        ArrayList arrayList = tL_help_proxyDataPromo.users;
        Integer valueOf = Integer.valueOf(0);
        putUsers(arrayList, false);
        putChats(tL_help_proxyDataPromo.chats, false);
        putUsers(tL_messages_peerDialogs.users, false);
        putChats(tL_messages_peerDialogs.chats, false);
        this.proxyDialog = (Dialog) tL_messages_peerDialogs.dialogs.get(0);
        Dialog dialog = this.proxyDialog;
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
            int i;
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (i = 0; i < tL_messages_peerDialogs.users.size(); i++) {
                User user = (User) tL_messages_peerDialogs.users.get(i);
                sparseArray.put(user.id, user);
            }
            for (i = 0; i < tL_messages_peerDialogs.chats.size(); i++) {
                Chat chat = (Chat) tL_messages_peerDialogs.chats.get(i);
                sparseArray2.put(chat.id, chat);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, (Message) tL_messages_peerDialogs.messages.get(0), sparseArray, sparseArray2, false);
            this.dialogMessage.put(j, messageObject);
            Dialog dialog3 = this.proxyDialog;
            if (dialog3.last_message_date == 0) {
                dialog3.last_message_date = messageObject.messageOwner.date;
            }
        }
        sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
    }

    public /* synthetic */ void lambda$null$91$MessagesController() {
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public /* synthetic */ void lambda$null$94$MessagesController() {
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public /* synthetic */ void lambda$checkProxyInfoInternal$96$MessagesController() {
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$Gpj0Tfm4ESrlwMoiHamza4rhl1c(this, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$updatePrintingStrings$97$MessagesController(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
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
                    if (i4 != 1) {
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
                            sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_setTyping, new -$$Lambda$MessagesController$0RUkjN1o4ur0BN0-Uwykda9jP9E(this, i, j), 2);
                            if (i2 != 0) {
                                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, i2);
                            }
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
                        sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_setEncryptedTyping, new -$$Lambda$MessagesController$46d8KXzv97zrAkyG5uwxOcxtLc4(this, i, j), 2);
                        if (i2 != 0) {
                            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, i2);
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$sendTyping$99$MessagesController(int i, long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$wKGVCgj8ovsTOS0jCqWZ3uZqdEI(this, i, j));
    }

    public /* synthetic */ void lambda$null$98$MessagesController(int i, long j) {
        LongSparseArray longSparseArray = (LongSparseArray) this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    public /* synthetic */ void lambda$sendTyping$101$MessagesController(int i, long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$8jlYAGfWz85ggA903BPszKwwO84(this, i, j));
    }

    public /* synthetic */ void lambda$null$100$MessagesController(int i, long j) {
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

    public void loadMessages(long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, boolean z2, int i8) {
        loadMessages(j, i, i2, i3, z, i4, i5, i6, i7, z2, i8, 0, 0, 0, false, 0);
    }

    public void loadMessages(long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, boolean z2, int i8, int i9, int i10, int i11, boolean z3, int i12) {
        loadMessagesInternal(j, i, i2, i3, z, i4, i5, i6, i7, z2, i8, i9, i10, i11, z3, i12, true);
    }

    private void loadMessagesInternal(long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, boolean z2, int i8, int i9, int i10, int i11, boolean z3, int i12, boolean z4) {
        int i13;
        long j2 = j;
        int i14 = i;
        int i15 = i2;
        boolean z5 = z;
        int i16 = i5;
        int i17 = i6;
        int i18 = i7;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("load messages in chat ");
            stringBuilder.append(j2);
            stringBuilder.append(" count ");
            stringBuilder.append(i14);
            stringBuilder.append(" max_id ");
            stringBuilder.append(i15);
            stringBuilder.append(" cache ");
            stringBuilder.append(z5);
            stringBuilder.append(" mindate = ");
            stringBuilder.append(i4);
            stringBuilder.append(" guid ");
            stringBuilder.append(i16);
            stringBuilder.append(" load_type ");
            stringBuilder.append(i17);
            stringBuilder.append(" last_message_id ");
            stringBuilder.append(i18);
            stringBuilder.append(" index ");
            stringBuilder.append(i8);
            stringBuilder.append(" firstUnread ");
            stringBuilder.append(i9);
            stringBuilder.append(" unread_count ");
            stringBuilder.append(i10);
            stringBuilder.append(" last_date ");
            stringBuilder.append(i11);
            stringBuilder.append(" queryFromServer ");
            stringBuilder.append(z3);
            FileLog.d(stringBuilder.toString());
        } else {
            int i19 = i4;
            int i20 = i8;
            int i21 = i9;
            int i22 = i10;
            i13 = i11;
            boolean z6 = z3;
        }
        int i23 = (int) j2;
        int i24;
        if (z5 || i23 == 0) {
            i24 = i3;
            i13 = i16;
            MessagesStorage.getInstance(this.currentAccount).getMessages(j, i, i2, i3, i4, i5, i6, z2, i8);
        } else if (z4 && ((i17 == 3 || i17 == 2) && i18 == 0)) {
            TLObject tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
            InputPeer inputPeer = getInputPeer(i23);
            TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
            tL_inputDialogPeer.peer = inputPeer;
            tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
            ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
            -$$Lambda$MessagesController$TN49v1ka-VM8bKqcQJdVLHWMje8 -__lambda_messagescontroller_tn49v1ka-vm8bkqcqjdvlhwmje8 = r0;
            TLObject tLObject = tL_messages_getPeerDialogs;
            ConnectionsManager connectionsManager = instance;
            -$$Lambda$MessagesController$TN49v1ka-VM8bKqcQJdVLHWMje8 -__lambda_messagescontroller_tn49v1ka-vm8bkqcqjdvlhwmje82 = new -$$Lambda$MessagesController$TN49v1ka-VM8bKqcQJdVLHWMje8(this, j, i, i2, i3, i4, i5, i6, z2, i8, i9, i11, z3);
            connectionsManager.sendRequest(tLObject, -__lambda_messagescontroller_tn49v1ka-vm8bkqcqjdvlhwmje8);
        } else {
            TLObject tL_messages_getHistory = new TL_messages_getHistory();
            tL_messages_getHistory.peer = getInputPeer(i23);
            if (i17 == 4) {
                tL_messages_getHistory.add_offset = (-i14) + 5;
            } else if (i17 == 3) {
                tL_messages_getHistory.add_offset = (-i14) / 2;
            } else if (i17 == 1) {
                tL_messages_getHistory.add_offset = (-i14) - 1;
            } else if (i17 == 2 && i15 != 0) {
                tL_messages_getHistory.add_offset = (-i14) + 6;
            } else if (i23 < 0 && i15 != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-i23)))) {
                tL_messages_getHistory.add_offset = -1;
                tL_messages_getHistory.limit++;
            }
            tL_messages_getHistory.limit = i14;
            tL_messages_getHistory.offset_id = i15;
            i24 = i3;
            tL_messages_getHistory.offset_date = i24;
            -$$Lambda$MessagesController$PBgpLRl3hHYMA8ENpUjkzO9CiK0 -__lambda_messagescontroller_pbgplrl3hhyma8enpujkzo9cik0 = r0;
            ConnectionsManager instance2 = ConnectionsManager.getInstance(this.currentAccount);
            TLObject tLObject2 = tL_messages_getHistory;
            -$$Lambda$MessagesController$PBgpLRl3hHYMA8ENpUjkzO9CiK0 -__lambda_messagescontroller_pbgplrl3hhyma8enpujkzo9cik02 = new -$$Lambda$MessagesController$PBgpLRl3hHYMA8ENpUjkzO9CiK0(this, j, i, i2, i24, i5, i9, i7, i10, i11, i6, z2, i8, z3, i12);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance2.sendRequest(tLObject2, -__lambda_messagescontroller_pbgplrl3hhyma8enpujkzo9cik0), i5);
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$102$MessagesController(long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, int i8, int i9, boolean z2, TLObject tLObject, TL_error tL_error) {
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
                    MessagesStorage.getInstance(this.currentAccount).putDialogs(tL_messages_dialogs, 0);
                }
                loadMessagesInternal(j, i, i2, i3, false, i4, i5, i6, dialog.top_message, z, i7, i8, dialog.unread_count, i9, z2, dialog.unread_mentions_count, false);
                return;
            }
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$103$MessagesController(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, int i10, boolean z2, int i11, TLObject tLObject, TL_error tL_error) {
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
            processLoadedMessages(messages_messages, j, i, i13, i3, false, i4, i5, i6, i7, i8, i9, z, false, i10, z2, i11);
        }
    }

    public void reloadWebPages(long j, HashMap<String, ArrayList<MessageObject>> hashMap) {
        for (Entry entry : hashMap.entrySet()) {
            String str = (String) entry.getKey();
            ArrayList arrayList = (ArrayList) entry.getValue();
            ArrayList arrayList2 = (ArrayList) this.reloadingWebpages.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
                this.reloadingWebpages.put(str, arrayList2);
            }
            arrayList2.addAll(arrayList);
            TL_messages_getWebPagePreview tL_messages_getWebPagePreview = new TL_messages_getWebPagePreview();
            tL_messages_getWebPagePreview.message = str;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getWebPagePreview, new -$$Lambda$MessagesController$4nBTlb9eBU7C4QavllU2UZiopks(this, str, j));
        }
    }

    public /* synthetic */ void lambda$reloadWebPages$105$MessagesController(String str, long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$CG95xW3CPVaIHZyuw5jMlgk_hAs(this, str, tLObject, j));
    }

    public /* synthetic */ void lambda$null$104$MessagesController(String str, TLObject tLObject, long j) {
        ArrayList arrayList = (ArrayList) this.reloadingWebpages.remove(str);
        if (arrayList != null) {
            messages_Messages tL_messages_messages = new TL_messages_messages();
            if (tLObject instanceof TL_messageMediaWebPage) {
                TL_messageMediaWebPage tL_messageMediaWebPage = (TL_messageMediaWebPage) tLObject;
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
                    this.reloadingWebpagesPending.put(webPage.id, arrayList);
                }
            } else {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    ((MessageObject) arrayList.get(i2)).messageOwner.media.webpage = new TL_webPageEmpty();
                    tL_messages_messages.messages.add(((MessageObject) arrayList.get(i2)).messageOwner);
                }
            }
            if (!tL_messages_messages.messages.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).putMessages(tL_messages_messages, j, -2, 0, false);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList);
            }
        }
    }

    public void processLoadedMessages(messages_Messages messages_messages, long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, int i8, int i9, boolean z2, boolean z3, int i10, boolean z4, int i11) {
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
            stringBuilder.append(z4);
            FileLog.d(stringBuilder.toString());
        } else {
            messages_Messages messages_messages2 = messages_messages;
            long j2 = j;
            int i12 = i;
            int i13 = i2;
            boolean z5 = z;
            int i14 = i4;
            int i15 = i5;
            int i16 = i6;
            int i17 = i7;
            int i18 = i8;
            int i19 = i9;
            boolean z6 = z2;
            int i20 = i10;
            boolean z7 = z4;
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$B3eeeWzbYDkKoJj9YxFxBPuwloE(this, messages_messages, j, z, i, i9, z4, i5, i2, i3, i4, i6, z2, i10, i7, i8, i11, z3));
    }

    /* JADX WARNING: Removed duplicated region for block: B:126:0x0266 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0223  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0223  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0266 A:{SYNTHETIC} */
    /* JADX WARNING: Missing block: B:96:0x020e, code skipped:
            if (r3[0] < (byte) 100) goto L_0x0212;
     */
    public /* synthetic */ void lambda$processLoadedMessages$108$MessagesController(org.telegram.tgnet.TLRPC.messages_Messages r25, long r26, boolean r28, int r29, int r30, boolean r31, int r32, int r33, int r34, int r35, int r36, boolean r37, int r38, int r39, int r40, int r41, boolean r42) {
        /*
        r24 = this;
        r15 = r24;
        r7 = r25;
        r8 = r26;
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
        r10 = 1;
        r11 = 0;
        if (r0 == 0) goto L_0x0069;
    L_0x000c:
        r0 = (int) r8;
        r6 = -r0;
        r0 = r15.channelsPts;
        r0 = r0.get(r6);
        if (r0 != 0) goto L_0x0049;
    L_0x0016:
        r0 = r15.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0 = r0.getChannelPtsSync(r6);
        if (r0 != 0) goto L_0x0049;
    L_0x0022:
        r0 = r15.channelsPts;
        r1 = r7.pts;
        r0.put(r6, r1);
        r0 = r15.needShortPollChannels;
        r0 = r0.indexOfKey(r6);
        if (r0 < 0) goto L_0x0044;
    L_0x0031:
        r0 = r15.shortPollChannels;
        r0 = r0.indexOfKey(r6);
        if (r0 >= 0) goto L_0x0044;
    L_0x0039:
        r2 = 2;
        r3 = 0;
        r5 = 0;
        r0 = r24;
        r1 = r6;
        r0.getChannelDifference(r1, r2, r3, r5);
        goto L_0x0047;
    L_0x0044:
        r15.getChannelDifference(r6);
    L_0x0047:
        r0 = 1;
        goto L_0x004a;
    L_0x0049:
        r0 = 0;
    L_0x004a:
        r1 = 0;
    L_0x004b:
        r2 = r7.chats;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x0066;
    L_0x0053:
        r2 = r7.chats;
        r2 = r2.get(r1);
        r2 = (org.telegram.tgnet.TLRPC.Chat) r2;
        r3 = r2.id;
        if (r3 != r6) goto L_0x0063;
    L_0x005f:
        r1 = r2.megagroup;
        r6 = r0;
        goto L_0x006b;
    L_0x0063:
        r1 = r1 + 1;
        goto L_0x004b;
    L_0x0066:
        r6 = r0;
        r1 = 0;
        goto L_0x006b;
    L_0x0069:
        r1 = 0;
        r6 = 0;
    L_0x006b:
        r0 = (int) r8;
        r2 = 32;
        r2 = r8 >> r2;
        r3 = (int) r2;
        if (r28 != 0) goto L_0x0078;
    L_0x0073:
        r2 = r7.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r2);
    L_0x0078:
        if (r3 == r10) goto L_0x00af;
    L_0x007a:
        if (r0 == 0) goto L_0x00af;
    L_0x007c:
        if (r28 == 0) goto L_0x00af;
    L_0x007e:
        r0 = r7.messages;
        r0 = r0.size();
        if (r0 != 0) goto L_0x00af;
    L_0x0086:
        r17 = new org.telegram.messenger.-$$Lambda$MessagesController$L2__57wpUETyhq6XVWbkuE1LFZE;
        r0 = r17;
        r1 = r24;
        r2 = r26;
        r4 = r29;
        r5 = r30;
        r6 = r31;
        r7 = r32;
        r8 = r33;
        r9 = r34;
        r10 = r35;
        r11 = r36;
        r12 = r37;
        r13 = r38;
        r14 = r39;
        r15 = r40;
        r16 = r41;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r17);
        return;
    L_0x00af:
        r12 = new android.util.SparseArray;
        r12.<init>();
        r13 = new android.util.SparseArray;
        r13.<init>();
        r0 = 0;
    L_0x00ba:
        r2 = r7.users;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x00d2;
    L_0x00c2:
        r2 = r7.users;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        r3 = r2.id;
        r12.put(r3, r2);
        r0 = r0 + 1;
        goto L_0x00ba;
    L_0x00d2:
        r0 = 0;
    L_0x00d3:
        r2 = r7.chats;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x00eb;
    L_0x00db:
        r2 = r7.chats;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Chat) r2;
        r3 = r2.id;
        r13.put(r3, r2);
        r0 = r0 + 1;
        goto L_0x00d3;
    L_0x00eb:
        r0 = r7.messages;
        r14 = r0.size();
        if (r28 != 0) goto L_0x01af;
    L_0x00f3:
        r15 = r24;
        r0 = r15.dialogs_read_inbox_max;
        r2 = java.lang.Long.valueOf(r26);
        r0 = r0.get(r2);
        r0 = (java.lang.Integer) r0;
        if (r0 != 0) goto L_0x011a;
    L_0x0103:
        r0 = r15.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0 = r0.getDialogReadMax(r11, r8);
        r0 = java.lang.Integer.valueOf(r0);
        r2 = r15.dialogs_read_inbox_max;
        r3 = java.lang.Long.valueOf(r26);
        r2.put(r3, r0);
    L_0x011a:
        r2 = r15.dialogs_read_outbox_max;
        r3 = java.lang.Long.valueOf(r26);
        r2 = r2.get(r3);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x013f;
    L_0x0128:
        r2 = r15.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r2 = r2.getDialogReadMax(r10, r8);
        r2 = java.lang.Integer.valueOf(r2);
        r3 = r15.dialogs_read_outbox_max;
        r4 = java.lang.Long.valueOf(r26);
        r3.put(r4, r2);
    L_0x013f:
        r3 = 0;
    L_0x0140:
        if (r3 >= r14) goto L_0x019d;
    L_0x0142:
        r4 = r7.messages;
        r4 = r4.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.Message) r4;
        if (r1 == 0) goto L_0x0154;
    L_0x014c:
        r5 = r4.flags;
        r16 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r5 = r5 | r16;
        r4.flags = r5;
    L_0x0154:
        r5 = r4.action;
        r10 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r10 == 0) goto L_0x0175;
    L_0x015a:
        r5 = r5.user_id;
        r5 = r12.get(r5);
        r5 = (org.telegram.tgnet.TLRPC.User) r5;
        if (r5 == 0) goto L_0x0175;
    L_0x0164:
        r5 = r5.bot;
        if (r5 == 0) goto L_0x0175;
    L_0x0168:
        r5 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r5.<init>();
        r4.reply_markup = r5;
        r5 = r4.flags;
        r5 = r5 | 64;
        r4.flags = r5;
    L_0x0175:
        r5 = r4.action;
        r10 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r10 != 0) goto L_0x0195;
    L_0x017b:
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r5 == 0) goto L_0x0180;
    L_0x017f:
        goto L_0x0195;
    L_0x0180:
        r5 = r4.out;
        if (r5 == 0) goto L_0x0186;
    L_0x0184:
        r5 = r2;
        goto L_0x0187;
    L_0x0186:
        r5 = r0;
    L_0x0187:
        r5 = r5.intValue();
        r10 = r4.id;
        if (r5 >= r10) goto L_0x0191;
    L_0x018f:
        r5 = 1;
        goto L_0x0192;
    L_0x0191:
        r5 = 0;
    L_0x0192:
        r4.unread = r5;
        goto L_0x0199;
    L_0x0195:
        r4.unread = r11;
        r4.media_unread = r11;
    L_0x0199:
        r3 = r3 + 1;
        r10 = 1;
        goto L_0x0140;
    L_0x019d:
        r0 = r15.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r1 = r25;
        r2 = r26;
        r4 = r30;
        r5 = r33;
        r0.putMessages(r1, r2, r4, r5, r6);
        goto L_0x01b1;
    L_0x01af:
        r15 = r24;
    L_0x01b1:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r6 = new java.util.ArrayList;
        r6.<init>();
        r5 = new java.util.HashMap;
        r5.<init>();
        r0 = 0;
    L_0x01c1:
        if (r0 >= r14) goto L_0x026c;
    L_0x01c3:
        r1 = r7.messages;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;
        r1.dialog_id = r8;
        r2 = new org.telegram.messenger.MessageObject;
        r3 = r15.currentAccount;
        r23 = 1;
        r18 = r2;
        r19 = r3;
        r20 = r1;
        r21 = r12;
        r22 = r13;
        r18.<init>(r19, r20, r21, r22, r23);
        r10.add(r2);
        if (r28 == 0) goto L_0x0265;
    L_0x01e5:
        r3 = r1.legacy;
        r4 = 100;
        if (r3 == 0) goto L_0x01f9;
    L_0x01eb:
        r3 = r1.layer;
        if (r3 >= r4) goto L_0x01f9;
    L_0x01ef:
        r3 = r1.id;
        r3 = java.lang.Integer.valueOf(r3);
        r6.add(r3);
        goto L_0x021c;
    L_0x01f9:
        r3 = r1.media;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
        if (r4 == 0) goto L_0x021c;
    L_0x01ff:
        r3 = r3.bytes;
        if (r3 == 0) goto L_0x021c;
    L_0x0203:
        r4 = r3.length;
        if (r4 == 0) goto L_0x0211;
    L_0x0206:
        r4 = r3.length;
        r7 = 1;
        if (r4 != r7) goto L_0x021d;
    L_0x020a:
        r3 = r3[r11];
        r4 = 100;
        if (r3 >= r4) goto L_0x021d;
    L_0x0210:
        goto L_0x0212;
    L_0x0211:
        r7 = 1;
    L_0x0212:
        r3 = r1.id;
        r3 = java.lang.Integer.valueOf(r3);
        r6.add(r3);
        goto L_0x021d;
    L_0x021c:
        r7 = 1;
    L_0x021d:
        r3 = r1.media;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r4 == 0) goto L_0x0266;
    L_0x0223:
        r3 = r3.webpage;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;
        if (r4 == 0) goto L_0x0241;
    L_0x0229:
        r3 = r3.date;
        r4 = r15.currentAccount;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r4 = r4.getCurrentTime();
        if (r3 > r4) goto L_0x0241;
    L_0x0237:
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r6.add(r1);
        goto L_0x0266;
    L_0x0241:
        r3 = r1.media;
        r3 = r3.webpage;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
        if (r4 == 0) goto L_0x0266;
    L_0x0249:
        r3 = r3.url;
        r3 = r5.get(r3);
        r3 = (java.util.ArrayList) r3;
        if (r3 != 0) goto L_0x0261;
    L_0x0253:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r1 = r1.media;
        r1 = r1.webpage;
        r1 = r1.url;
        r5.put(r1, r3);
    L_0x0261:
        r3.add(r2);
        goto L_0x0266;
    L_0x0265:
        r7 = 1;
    L_0x0266:
        r0 = r0 + 1;
        r7 = r25;
        goto L_0x01c1;
    L_0x026c:
        r21 = new org.telegram.messenger.-$$Lambda$MessagesController$Z4Bd5ZrkcBpI903-24hDafvqXGk;
        r0 = r21;
        r1 = r24;
        r2 = r25;
        r3 = r28;
        r4 = r31;
        r20 = r5;
        r5 = r30;
        r19 = r6;
        r6 = r32;
        r7 = r26;
        r9 = r29;
        r11 = r36;
        r12 = r39;
        r13 = r40;
        r14 = r42;
        r15 = r35;
        r16 = r38;
        r17 = r33;
        r18 = r41;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r21);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedMessages$108$MessagesController(org.telegram.tgnet.TLRPC$messages_Messages, long, boolean, int, int, boolean, int, int, int, int, int, boolean, int, int, int, int, boolean):void");
    }

    public /* synthetic */ void lambda$null$106$MessagesController(long j, int i, int i2, boolean z, int i3, int i4, int i5, int i6, int i7, boolean z2, int i8, int i9, int i10, int i11) {
        int i12 = (i2 == 2 && z) ? i3 : i4;
        loadMessages(j, i, i12, i5, false, 0, i6, i2, i7, z2, i8, i3, i9, i10, z, i11);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00d1  */
    public /* synthetic */ void lambda$null$107$MessagesController(org.telegram.tgnet.TLRPC.messages_Messages r14, boolean r15, boolean r16, int r17, int r18, long r19, int r21, java.util.ArrayList r22, int r23, int r24, int r25, boolean r26, int r27, int r28, int r29, int r30, java.util.ArrayList r31, java.util.HashMap r32) {
        /*
        r13 = this;
        r0 = r13;
        r1 = r14;
        r2 = r15;
        r3 = r19;
        r5 = r1.users;
        r13.putUsers(r5, r15);
        r5 = r1.chats;
        r13.putChats(r5, r15);
        r5 = 0;
        r6 = 2;
        r7 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r16 == 0) goto L_0x0044;
    L_0x0016:
        r8 = r17;
        if (r8 != r6) goto L_0x0046;
    L_0x001a:
        r9 = 0;
        r10 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x001e:
        r11 = r1.messages;
        r11 = r11.size();
        if (r9 >= r11) goto L_0x0041;
    L_0x0026:
        r11 = r1.messages;
        r11 = r11.get(r9);
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;
        r12 = r11.out;
        if (r12 != 0) goto L_0x003c;
    L_0x0032:
        r11 = r11.id;
        r12 = r18;
        if (r11 <= r12) goto L_0x003e;
    L_0x0038:
        if (r11 >= r10) goto L_0x003e;
    L_0x003a:
        r10 = r11;
        goto L_0x003e;
    L_0x003c:
        r12 = r18;
    L_0x003e:
        r9 = r9 + 1;
        goto L_0x001e;
    L_0x0041:
        r12 = r18;
        goto L_0x004b;
    L_0x0044:
        r8 = r17;
    L_0x0046:
        r12 = r18;
        r10 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x004b:
        if (r10 != r7) goto L_0x004e;
    L_0x004d:
        goto L_0x004f;
    L_0x004e:
        r12 = r10;
    L_0x004f:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r7 = org.telegram.messenger.NotificationCenter.messagesDidLoad;
        r9 = 14;
        r9 = new java.lang.Object[r9];
        r10 = java.lang.Long.valueOf(r19);
        r9[r5] = r10;
        r5 = java.lang.Integer.valueOf(r21);
        r10 = 1;
        r9[r10] = r5;
        r9[r6] = r22;
        r5 = 3;
        r2 = java.lang.Boolean.valueOf(r15);
        r9[r5] = r2;
        r2 = 4;
        r5 = java.lang.Integer.valueOf(r12);
        r9[r2] = r5;
        r2 = 5;
        r5 = java.lang.Integer.valueOf(r23);
        r9[r2] = r5;
        r2 = 6;
        r5 = java.lang.Integer.valueOf(r24);
        r9[r2] = r5;
        r2 = 7;
        r5 = java.lang.Integer.valueOf(r25);
        r9[r2] = r5;
        r2 = 8;
        r5 = java.lang.Integer.valueOf(r17);
        r9[r2] = r5;
        r2 = 9;
        r5 = java.lang.Boolean.valueOf(r26);
        r9[r2] = r5;
        r2 = 10;
        r5 = java.lang.Integer.valueOf(r27);
        r9[r2] = r5;
        r2 = 11;
        r5 = java.lang.Integer.valueOf(r28);
        r9[r2] = r5;
        r2 = 12;
        r5 = java.lang.Integer.valueOf(r29);
        r9[r2] = r5;
        r2 = 13;
        r5 = java.lang.Integer.valueOf(r30);
        r9[r2] = r5;
        r1.postNotificationName(r7, r9);
        r1 = r31.isEmpty();
        if (r1 != 0) goto L_0x00cb;
    L_0x00c6:
        r1 = r31;
        r13.reloadMessages(r1, r3);
    L_0x00cb:
        r1 = r32.isEmpty();
        if (r1 != 0) goto L_0x00d6;
    L_0x00d1:
        r1 = r32;
        r13.reloadWebPages(r3, r1);
    L_0x00d6:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$null$107$MessagesController(org.telegram.tgnet.TLRPC$messages_Messages, boolean, boolean, int, int, long, int, java.util.ArrayList, int, int, int, boolean, int, int, int, int, java.util.ArrayList, java.util.HashMap):void");
    }

    public void loadHintDialogs() {
        if (this.hintDialogs.isEmpty() && !TextUtils.isEmpty(this.installReferer)) {
            TL_help_getRecentMeUrls tL_help_getRecentMeUrls = new TL_help_getRecentMeUrls();
            tL_help_getRecentMeUrls.referer = this.installReferer;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_getRecentMeUrls, new -$$Lambda$MessagesController$S17IW-TdnLou1yG0GrX54gGAuDE(this));
        }
    }

    public /* synthetic */ void lambda$loadHintDialogs$110$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$OHE7-yHc7AWZbJtLN81PZkwCp4o(this, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$109$MessagesController(TLObject tLObject) {
        TL_help_recentMeUrls tL_help_recentMeUrls = (TL_help_recentMeUrls) tLObject;
        putUsers(tL_help_recentMeUrls.users, false);
        putChats(tL_help_recentMeUrls.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(tL_help_recentMeUrls.urls);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
        MessagesStorage.getInstance(this.currentAccount).putDialogs(tL_messages_dialogs, 1);
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.folderBecomeEmpty, Integer.valueOf(i));
        }
    }

    /* Access modifiers changed, original: protected */
    public void onFolderEmpty(int i) {
        if (UserConfig.getInstance(this.currentAccount).getDialogLoadOffsets(i)[0] == Integer.MAX_VALUE) {
            removeFolder(i);
            return;
        }
        loadDialogs(i, 0, 10, false, new -$$Lambda$MessagesController$jEkulIUjpo2aQz9ir2LF9vSzFoc(this, i));
    }

    public /* synthetic */ void lambda$onFolderEmpty$111$MessagesController(int i) {
        removeFolder(i);
    }

    public void checkIfFolderEmpty(int i) {
        if (i != 0) {
            MessagesStorage.getInstance(this.currentAccount).checkIfFolderEmpty(i);
        }
    }

    public int addDialogToFolder(long j, int i, int i2, long j2) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(Long.valueOf(j));
        return addDialogToFolder(arrayList, i, i2, null, j2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0151  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0151  */
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
        if (r2 != 0) goto L_0x011e;
    L_0x0013:
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r15 = r2.getClientUserId();
        r7 = r22.size();
        r2 = 0;
        r3 = 0;
        r5 = 0;
        r16 = 0;
    L_0x0026:
        if (r5 >= r7) goto L_0x00be;
    L_0x0028:
        r6 = r22;
        r4 = r6.get(r5);
        r4 = (java.lang.Long) r4;
        r10 = r4.longValue();
        r4 = org.telegram.messenger.DialogObject.isPeerDialogId(r10);
        if (r4 != 0) goto L_0x0041;
    L_0x003a:
        r4 = org.telegram.messenger.DialogObject.isSecretDialogId(r10);
        if (r4 != 0) goto L_0x0041;
    L_0x0040:
        goto L_0x0060;
    L_0x0041:
        if (r8 != r13) goto L_0x0056;
    L_0x0043:
        r12 = (long) r15;
        r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x0060;
    L_0x0048:
        r12 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x0060;
    L_0x004f:
        r4 = r1.isProxyDialog(r10, r14);
        if (r4 == 0) goto L_0x0056;
    L_0x0055:
        goto L_0x0060;
    L_0x0056:
        r4 = r1.dialogs_dict;
        r4 = r4.get(r10);
        r4 = (org.telegram.tgnet.TLRPC.Dialog) r4;
        if (r4 != 0) goto L_0x0066;
    L_0x0060:
        r19 = r5;
        r20 = r7;
        r12 = 1;
        goto L_0x00b5;
    L_0x0066:
        r4.folder_id = r8;
        if (r0 <= 0) goto L_0x0070;
    L_0x006a:
        r12 = 1;
        r4.pinned = r12;
        r4.pinnedNum = r0;
        goto L_0x0075;
    L_0x0070:
        r12 = 1;
        r4.pinned = r14;
        r4.pinnedNum = r14;
    L_0x0075:
        if (r3 != 0) goto L_0x007c;
    L_0x0077:
        r3 = new boolean[r12];
        r1.ensureFolderDialogExists(r8, r3);
    L_0x007c:
        r13 = r3;
        r2 = org.telegram.messenger.DialogObject.isSecretDialogId(r10);
        if (r2 == 0) goto L_0x0096;
    L_0x0083:
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r3 = 0;
        r4 = 0;
        r19 = r5;
        r5 = r10;
        r20 = r7;
        r7 = r23;
        r2.setDialogsFolderId(r3, r4, r5, r7);
        goto L_0x00b3;
    L_0x0096:
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
    L_0x00b3:
        r3 = r13;
        r2 = 1;
    L_0x00b5:
        r5 = r19 + 1;
        r7 = r20;
        r10 = 0;
        r13 = 1;
        goto L_0x0026;
    L_0x00be:
        r12 = 1;
        if (r2 != 0) goto L_0x00c2;
    L_0x00c1:
        return r14;
    L_0x00c2:
        r2 = 0;
        r1.sortDialogs(r2);
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r4 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r5 = new java.lang.Object[r14];
        r0.postNotificationName(r4, r5);
        if (r16 == 0) goto L_0x0118;
    L_0x00d5:
        r4 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0106 }
        r0 = r16 + 12;
        r4.<init>(r0);	 Catch:{ Exception -> 0x0106 }
        r0 = 17;
        r4.writeInt32(r0);	 Catch:{ Exception -> 0x0104 }
        r4.writeInt32(r8);	 Catch:{ Exception -> 0x0104 }
        r0 = r9.folder_peers;	 Catch:{ Exception -> 0x0104 }
        r0 = r0.size();	 Catch:{ Exception -> 0x0104 }
        r4.writeInt32(r0);	 Catch:{ Exception -> 0x0104 }
        r0 = r9.folder_peers;	 Catch:{ Exception -> 0x0104 }
        r0 = r0.size();	 Catch:{ Exception -> 0x0104 }
        r2 = 0;
    L_0x00f4:
        if (r2 >= r0) goto L_0x010b;
    L_0x00f6:
        r5 = r9.folder_peers;	 Catch:{ Exception -> 0x0104 }
        r5 = r5.get(r2);	 Catch:{ Exception -> 0x0104 }
        r5 = (org.telegram.tgnet.TLRPC.TL_inputFolderPeer) r5;	 Catch:{ Exception -> 0x0104 }
        r5.serializeToStream(r4);	 Catch:{ Exception -> 0x0104 }
        r2 = r2 + 1;
        goto L_0x00f4;
    L_0x0104:
        r0 = move-exception;
        goto L_0x0108;
    L_0x0106:
        r0 = move-exception;
        r4 = r2;
    L_0x0108:
        org.telegram.messenger.FileLog.e(r0);
    L_0x010b:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r4 = r0.createPendingTask(r4);
        r17 = r4;
        goto L_0x011a;
    L_0x0118:
        r17 = 0;
    L_0x011a:
        r0 = r3;
        r2 = r17;
        goto L_0x0127;
    L_0x011e:
        r0 = r25;
        r2 = 0;
        r12 = 1;
        r9.folder_peers = r0;
        r0 = r2;
        r2 = r26;
    L_0x0127:
        r4 = r9.folder_peers;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x014d;
    L_0x012f:
        r4 = r1.currentAccount;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r5 = new org.telegram.messenger.-$$Lambda$MessagesController$6NDlWW4LaPRt1E9GG1k86kJK_WU;
        r5.<init>(r1, r2);
        r4.sendRequest(r9, r5);
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r3 = 0;
        r4 = r9.folder_peers;
        r5 = 0;
        r7 = r23;
        r2.setDialogsFolderId(r3, r4, r5, r7);
    L_0x014d:
        if (r0 != 0) goto L_0x0151;
    L_0x014f:
        r12 = 0;
        goto L_0x0157;
    L_0x0151:
        r0 = r0[r14];
        if (r0 == 0) goto L_0x0157;
    L_0x0155:
        r13 = 2;
        r12 = 2;
    L_0x0157:
        return r12;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.addDialogToFolder(java.util.ArrayList, int, int, java.util.ArrayList, long):int");
    }

    public /* synthetic */ void lambda$addDialogToFolder$112$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
        }
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
    }

    public void loadDialogs(int i, int i2, int i3, boolean z) {
        loadDialogs(i, i2, i3, z, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0160  */
    public void loadDialogs(int r11, int r12, int r13, boolean r14, java.lang.Runnable r15) {
        /*
        r10 = this;
        r0 = r10.loadingDialogs;
        r0 = r0.get(r11);
        if (r0 != 0) goto L_0x0175;
    L_0x0008:
        r0 = r10.resetingDialogs;
        if (r0 == 0) goto L_0x000e;
    L_0x000c:
        goto L_0x0175;
    L_0x000e:
        r0 = r10.loadingDialogs;
        r1 = 1;
        r0.put(r11, r1);
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r3 = 0;
        r4 = new java.lang.Object[r3];
        r0.postNotificationName(r2, r4);
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0052;
    L_0x0026:
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
    L_0x0052:
        if (r14 == 0) goto L_0x0068;
    L_0x0054:
        r14 = r10.currentAccount;
        r14 = org.telegram.messenger.MessagesStorage.getInstance(r14);
        if (r12 != 0) goto L_0x005d;
    L_0x005c:
        goto L_0x0063;
    L_0x005d:
        r12 = r10.nextDialogsCacheOffset;
        r3 = r12.get(r11, r3);
    L_0x0063:
        r14.getDialogs(r11, r3, r13);
        goto L_0x0175;
    L_0x0068:
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_getDialogs;
        r12.<init>();
        r12.limit = r13;
        r12.exclude_pinned = r1;
        r14 = 2;
        if (r11 == 0) goto L_0x007b;
    L_0x0074:
        r0 = r12.flags;
        r0 = r0 | r14;
        r12.flags = r0;
        r12.folder_id = r11;
    L_0x007b:
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.getDialogLoadOffsets(r11);
        r2 = r0[r3];
        r4 = 32;
        r5 = -1;
        if (r2 == r5) goto L_0x0106;
    L_0x008c:
        r2 = r0[r3];
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r2 != r5) goto L_0x00b0;
    L_0x0093:
        r12 = r10.dialogsEndReached;
        r12.put(r11, r1);
        r12 = r10.serverDialogsEndReached;
        r12.put(r11, r1);
        r12 = r10.loadingDialogs;
        r12.put(r11, r3);
        r11 = r10.currentAccount;
        r11 = org.telegram.messenger.NotificationCenter.getInstance(r11);
        r12 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r13 = new java.lang.Object[r3];
        r11.postNotificationName(r12, r13);
        return;
    L_0x00b0:
        r2 = r0[r3];
        r12.offset_id = r2;
        r1 = r0[r1];
        r12.offset_date = r1;
        r1 = r12.offset_id;
        if (r1 != 0) goto L_0x00c5;
    L_0x00bc:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r14.<init>();
        r12.offset_peer = r14;
        goto L_0x0167;
    L_0x00c5:
        r1 = 4;
        r2 = r0[r1];
        if (r2 == 0) goto L_0x00d8;
    L_0x00ca:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
        r14.<init>();
        r12.offset_peer = r14;
        r14 = r12.offset_peer;
        r1 = r0[r1];
        r14.channel_id = r1;
        goto L_0x00f8;
    L_0x00d8:
        r1 = r0[r14];
        if (r1 == 0) goto L_0x00ea;
    L_0x00dc:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerUser;
        r1.<init>();
        r12.offset_peer = r1;
        r1 = r12.offset_peer;
        r14 = r0[r14];
        r1.user_id = r14;
        goto L_0x00f8;
    L_0x00ea:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat;
        r14.<init>();
        r12.offset_peer = r14;
        r14 = r12.offset_peer;
        r1 = 3;
        r1 = r0[r1];
        r14.chat_id = r1;
    L_0x00f8:
        r14 = r12.offset_peer;
        r1 = 5;
        r2 = r0[r1];
        r2 = (long) r2;
        r0 = r0[r1];
        r0 = (long) r0;
        r0 = r0 << r4;
        r0 = r0 | r2;
        r14.access_hash = r0;
        goto L_0x0167;
    L_0x0106:
        r14 = r10.getDialogs(r11);
        r0 = r14.size();
        r0 = r0 - r1;
    L_0x010f:
        if (r0 < 0) goto L_0x015d;
    L_0x0111:
        r2 = r14.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Dialog) r2;
        r5 = r2.pinned;
        if (r5 == 0) goto L_0x011c;
    L_0x011b:
        goto L_0x015a;
    L_0x011c:
        r5 = r2.id;
        r7 = (int) r5;
        r8 = r5 >> r4;
        r9 = (int) r8;
        if (r7 == 0) goto L_0x015a;
    L_0x0124:
        if (r9 == r1) goto L_0x015a;
    L_0x0126:
        r2 = r2.top_message;
        if (r2 <= 0) goto L_0x015a;
    L_0x012a:
        r2 = r10.dialogMessage;
        r2 = r2.get(r5);
        r2 = (org.telegram.messenger.MessageObject) r2;
        if (r2 == 0) goto L_0x015a;
    L_0x0134:
        r5 = r2.getId();
        if (r5 <= 0) goto L_0x015a;
    L_0x013a:
        r14 = r2.messageOwner;
        r0 = r14.date;
        r12.offset_date = r0;
        r0 = r14.id;
        r12.offset_id = r0;
        r14 = r14.to_id;
        r0 = r14.channel_id;
        if (r0 == 0) goto L_0x014c;
    L_0x014a:
        r14 = -r0;
        goto L_0x0153;
    L_0x014c:
        r0 = r14.chat_id;
        if (r0 == 0) goto L_0x0151;
    L_0x0150:
        goto L_0x014a;
    L_0x0151:
        r14 = r14.user_id;
    L_0x0153:
        r14 = r10.getInputPeer(r14);
        r12.offset_peer = r14;
        goto L_0x015e;
    L_0x015a:
        r0 = r0 + -1;
        goto L_0x010f;
    L_0x015d:
        r1 = 0;
    L_0x015e:
        if (r1 != 0) goto L_0x0167;
    L_0x0160:
        r14 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r14.<init>();
        r12.offset_peer = r14;
    L_0x0167:
        r14 = r10.currentAccount;
        r14 = org.telegram.tgnet.ConnectionsManager.getInstance(r14);
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$7uQARORTbE9n3vBAnDQr0-O75Rk;
        r0.<init>(r10, r11, r13, r15);
        r14.sendRequest(r12, r0);
    L_0x0175:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.loadDialogs(int, int, int, boolean, java.lang.Runnable):void");
    }

    public /* synthetic */ void lambda$loadDialogs$113$MessagesController(int i, int i2, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Dialogs messages_dialogs = (messages_Dialogs) tLObject;
            processLoadedDialogs(messages_dialogs, null, i, 0, i2, 0, false, false, false);
            if (runnable != null && messages_dialogs.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        }
    }

    public void loadGlobalNotificationsSettings() {
        if (this.loadingNotificationSettings == 0 && !UserConfig.getInstance(this.currentAccount).notificationsSettingsLoaded) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getNotifySettings, new -$$Lambda$MessagesController$Kw60WHVImMJ5bL_qjfLao1O7Ey8(this, i));
            }
        }
        if (!UserConfig.getInstance(this.currentAccount).notificationsSignUpSettingsLoaded) {
            loadSignUpNotificationsSettings();
        }
    }

    public /* synthetic */ void lambda$loadGlobalNotificationsSettings$115$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$rNUlr7DAfpCKy3vrWYUfF6QWZEw(this, tLObject, i));
    }

    public /* synthetic */ void lambda$null$114$MessagesController(TLObject tLObject, int i) {
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
                UserConfig.getInstance(this.currentAccount).notificationsSettingsLoaded = true;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
            }
        }
    }

    public void loadSignUpNotificationsSettings() {
        if (!this.loadingNotificationSignUpSettings) {
            this.loadingNotificationSignUpSettings = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getContactSignUpNotification(), new -$$Lambda$MessagesController$rOfsCqpWEwvJI16XzaGq8LurqHg(this));
        }
    }

    public /* synthetic */ void lambda$loadSignUpNotificationsSettings$117$MessagesController(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$mxqgqoLhYjlwTctiq5kQ_FZ7lvE(this, tLObject));
    }

    public /* synthetic */ void lambda$null$116$MessagesController(TLObject tLObject) {
        this.loadingNotificationSignUpSettings = false;
        Editor edit = this.notificationsPreferences.edit();
        this.enableJoined = tLObject instanceof TL_boolFalse;
        edit.putBoolean("EnableContactJoined", this.enableJoined);
        edit.commit();
        UserConfig.getInstance(this.currentAccount).notificationsSignUpSettingsLoaded = true;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void forceResetDialogs() {
        resetDialogs(true, MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
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
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$-qOB0YtC-RNY0KjHwuxhfdX_Ckg(this, j, peerDialogId));
                    }
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$-qOB0YtC-RNY0KjHwuxhfdX_Ckg(this, j, peerDialogId));
            }
        }
    }

    public /* synthetic */ void lambda$loadUnknownDialog$118$MessagesController(long j, long j2, TLObject tLObject, TL_error tL_error) {
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
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j3);
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

    /* JADX WARNING: Missing block: B:68:0x01ca, code skipped:
            if (r11.migrated_to != null) goto L_0x0181;
     */
    private void resetDialogs(boolean r26, int r27, int r28, int r29, int r30) {
        /*
        r25 = this;
        r6 = r25;
        r7 = 1;
        r0 = 0;
        r1 = java.lang.Integer.valueOf(r0);
        if (r26 == 0) goto L_0x005b;
    L_0x000a:
        r1 = r6.resetingDialogs;
        if (r1 == 0) goto L_0x000f;
    L_0x000e:
        return;
    L_0x000f:
        r1 = r6.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1.setPinnedDialogsLoaded(r7, r0);
        r6.resetingDialogs = r7;
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getPinnedDialogs;
        r8.<init>();
        r0 = r6.currentAccount;
        r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r10 = new org.telegram.messenger.-$$Lambda$MessagesController$gH22liOUbHjBW_HkIeQEsiEIQPo;
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
        r0 = r6.currentAccount;
        r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r9 = new org.telegram.messenger.-$$Lambda$MessagesController$Y6E_L9N-2KXUoxEcct5cWMRh9hc;
        r0 = r9;
        r0.<init>(r1, r2, r3, r4, r5);
        r7.sendRequest(r8, r9);
        goto L_0x02dc;
    L_0x005b:
        r2 = r6.resetDialogsPinned;
        if (r2 == 0) goto L_0x02dc;
    L_0x005f:
        r2 = r6.resetDialogsAll;
        if (r2 == 0) goto L_0x02dc;
    L_0x0063:
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
    L_0x00b7:
        r8 = r6.resetDialogsAll;
        r8 = r8.users;
        r8 = r8.size();
        if (r5 >= r8) goto L_0x00d3;
    L_0x00c1:
        r8 = r6.resetDialogsAll;
        r8 = r8.users;
        r8 = r8.get(r5);
        r8 = (org.telegram.tgnet.TLRPC.User) r8;
        r9 = r8.id;
        r3.put(r9, r8);
        r5 = r5 + 1;
        goto L_0x00b7;
    L_0x00d3:
        r5 = 0;
    L_0x00d4:
        r8 = r6.resetDialogsAll;
        r8 = r8.chats;
        r8 = r8.size();
        if (r5 >= r8) goto L_0x00f0;
    L_0x00de:
        r8 = r6.resetDialogsAll;
        r8 = r8.chats;
        r8 = r8.get(r5);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        r9 = r8.id;
        r4.put(r9, r8);
        r5 = r5 + 1;
        goto L_0x00d4;
    L_0x00f0:
        r5 = 0;
        r14 = r5;
        r8 = 0;
    L_0x00f3:
        r9 = r6.resetDialogsAll;
        r9 = r9.messages;
        r9 = r9.size();
        if (r8 >= r9) goto L_0x0161;
    L_0x00fd:
        r9 = r6.resetDialogsAll;
        r9 = r9.messages;
        r9 = r9.get(r8);
        r9 = (org.telegram.tgnet.TLRPC.Message) r9;
        if (r8 >= r10) goto L_0x0112;
    L_0x0109:
        if (r14 == 0) goto L_0x0111;
    L_0x010b:
        r11 = r9.date;
        r12 = r14.date;
        if (r11 >= r12) goto L_0x0112;
    L_0x0111:
        r14 = r9;
    L_0x0112:
        r11 = r9.to_id;
        r12 = r11.channel_id;
        if (r12 == 0) goto L_0x0133;
    L_0x0118:
        r11 = r4.get(r12);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x0125;
    L_0x0120:
        r12 = r11.left;
        if (r12 == 0) goto L_0x0125;
    L_0x0124:
        goto L_0x015e;
    L_0x0125:
        if (r11 == 0) goto L_0x0144;
    L_0x0127:
        r11 = r11.megagroup;
        if (r11 == 0) goto L_0x0144;
    L_0x012b:
        r11 = r9.flags;
        r12 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r12;
        r9.flags = r11;
        goto L_0x0144;
    L_0x0133:
        r11 = r11.chat_id;
        if (r11 == 0) goto L_0x0144;
    L_0x0137:
        r11 = r4.get(r11);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x0144;
    L_0x013f:
        r11 = r11.migrated_to;
        if (r11 == 0) goto L_0x0144;
    L_0x0143:
        goto L_0x015e;
    L_0x0144:
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
    L_0x015e:
        r8 = r8 + 1;
        goto L_0x00f3;
    L_0x0161:
        r8 = 0;
    L_0x0162:
        r9 = r6.resetDialogsAll;
        r9 = r9.dialogs;
        r9 = r9.size();
        if (r8 >= r9) goto L_0x022e;
    L_0x016c:
        r9 = r6.resetDialogsAll;
        r9 = r9.dialogs;
        r9 = r9.get(r8);
        r9 = (org.telegram.tgnet.TLRPC.Dialog) r9;
        org.telegram.messenger.DialogObject.initDialog(r9);
        r11 = r9.id;
        r16 = 0;
        r13 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1));
        if (r13 != 0) goto L_0x0184;
    L_0x0181:
        r13 = r1;
        goto L_0x0228;
    L_0x0184:
        r13 = r9.last_message_date;
        if (r13 != 0) goto L_0x0196;
    L_0x0188:
        r11 = r2.get(r11);
        r11 = (org.telegram.messenger.MessageObject) r11;
        if (r11 == 0) goto L_0x0196;
    L_0x0190:
        r11 = r11.messageOwner;
        r11 = r11.date;
        r9.last_message_date = r11;
    L_0x0196:
        r11 = org.telegram.messenger.DialogObject.isChannel(r9);
        if (r11 == 0) goto L_0x01b9;
    L_0x019c:
        r11 = r9.id;
        r12 = (int) r11;
        r11 = -r12;
        r11 = r4.get(r11);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x01ad;
    L_0x01a8:
        r11 = r11.left;
        if (r11 == 0) goto L_0x01ad;
    L_0x01ac:
        goto L_0x0181;
    L_0x01ad:
        r11 = r6.channelsPts;
        r12 = r9.id;
        r13 = (int) r12;
        r12 = -r13;
        r13 = r9.pts;
        r11.put(r12, r13);
        goto L_0x01cd;
    L_0x01b9:
        r11 = r9.id;
        r13 = (int) r11;
        if (r13 >= 0) goto L_0x01cd;
    L_0x01be:
        r12 = (int) r11;
        r11 = -r12;
        r11 = r4.get(r11);
        r11 = (org.telegram.tgnet.TLRPC.Chat) r11;
        if (r11 == 0) goto L_0x01cd;
    L_0x01c8:
        r11 = r11.migrated_to;
        if (r11 == 0) goto L_0x01cd;
    L_0x01cc:
        goto L_0x0181;
    L_0x01cd:
        r11 = r9.id;
        r15.put(r11, r9);
        r11 = r6.dialogs_read_inbox_max;
        r12 = r9.id;
        r12 = java.lang.Long.valueOf(r12);
        r11 = r11.get(r12);
        r11 = (java.lang.Integer) r11;
        if (r11 != 0) goto L_0x01e3;
    L_0x01e2:
        r11 = r1;
    L_0x01e3:
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
        if (r1 != 0) goto L_0x020f;
    L_0x020e:
        r1 = r13;
    L_0x020f:
        r0 = r6.dialogs_read_outbox_max;
        r11 = r9.id;
        r11 = java.lang.Long.valueOf(r11);
        r1 = r1.intValue();
        r9 = r9.read_outbox_max_id;
        r1 = java.lang.Math.max(r1, r9);
        r1 = java.lang.Integer.valueOf(r1);
        r0.put(r11, r1);
    L_0x0228:
        r8 = r8 + 1;
        r1 = r13;
        r0 = 0;
        goto L_0x0162;
    L_0x022e:
        r0 = r6.resetDialogsAll;
        r0 = r0.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r0);
        r0 = 0;
    L_0x0236:
        r1 = r6.resetDialogsAll;
        r1 = r1.messages;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x02c0;
    L_0x0240:
        r1 = r6.resetDialogsAll;
        r1 = r1.messages;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;
        r4 = r1.action;
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r8 == 0) goto L_0x026b;
    L_0x0250:
        r4 = r4.user_id;
        r4 = r3.get(r4);
        r4 = (org.telegram.tgnet.TLRPC.User) r4;
        if (r4 == 0) goto L_0x026b;
    L_0x025a:
        r4 = r4.bot;
        if (r4 == 0) goto L_0x026b;
    L_0x025e:
        r4 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r4.<init>();
        r1.reply_markup = r4;
        r4 = r1.flags;
        r4 = r4 | 64;
        r1.flags = r4;
    L_0x026b:
        r4 = r1.action;
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r8 != 0) goto L_0x02b7;
    L_0x0271:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r4 == 0) goto L_0x0276;
    L_0x0275:
        goto L_0x02b7;
    L_0x0276:
        r4 = r1.out;
        if (r4 == 0) goto L_0x027d;
    L_0x027a:
        r4 = r6.dialogs_read_outbox_max;
        goto L_0x027f;
    L_0x027d:
        r4 = r6.dialogs_read_inbox_max;
    L_0x027f:
        r8 = r1.dialog_id;
        r8 = java.lang.Long.valueOf(r8);
        r8 = r4.get(r8);
        r8 = (java.lang.Integer) r8;
        if (r8 != 0) goto L_0x02a8;
    L_0x028d:
        r8 = r6.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r9 = r1.out;
        r11 = r1.dialog_id;
        r8 = r8.getDialogReadMax(r9, r11);
        r8 = java.lang.Integer.valueOf(r8);
        r11 = r1.dialog_id;
        r9 = java.lang.Long.valueOf(r11);
        r4.put(r9, r8);
    L_0x02a8:
        r4 = r8.intValue();
        r8 = r1.id;
        if (r4 >= r8) goto L_0x02b2;
    L_0x02b0:
        r4 = 1;
        goto L_0x02b3;
    L_0x02b2:
        r4 = 0;
    L_0x02b3:
        r1.unread = r4;
        r4 = 0;
        goto L_0x02bc;
    L_0x02b7:
        r4 = 0;
        r1.unread = r4;
        r1.media_unread = r4;
    L_0x02bc:
        r0 = r0 + 1;
        goto L_0x0236;
    L_0x02c0:
        r0 = r6.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r0);
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
    L_0x02dc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.resetDialogs(boolean, int, int, int, int):void");
    }

    public /* synthetic */ void lambda$resetDialogs$119$MessagesController(int i, int i2, int i3, int i4, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            this.resetDialogsPinned = (TL_messages_peerDialogs) tLObject;
            for (int i5 = 0; i5 < this.resetDialogsPinned.dialogs.size(); i5++) {
                ((Dialog) this.resetDialogsPinned.dialogs.get(i5)).pinned = true;
            }
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    public /* synthetic */ void lambda$resetDialogs$120$MessagesController(int i, int i2, int i3, int i4, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            this.resetDialogsAll = (messages_Dialogs) tLObject;
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    /* Access modifiers changed, original: protected */
    public void completeDialogsReset(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, LongSparseArray<Dialog> longSparseArray, LongSparseArray<MessageObject> longSparseArray2, Message message) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$yD6NgtKkjxhJizLM4eoCQwHgn60(this, i3, i4, i5, messages_dialogs, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$completeDialogsReset$122$MessagesController(int i, int i2, int i3, messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.gettingDifference = false;
        MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(i);
        MessagesStorage.getInstance(this.currentAccount).setLastDateValue(i2);
        MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(i3);
        getDifference();
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$-JxxcnOrF9NEl3xKvCXKO-45t_Q(this, messages_dialogs, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$null$121$MessagesController(messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        int i;
        Dialog dialog;
        long j;
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        LongSparseArray longSparseArray3 = longSparseArray;
        this.resetingDialogs = false;
        applyDialogsNotificationsSettings(messages_dialogs2.dialogs);
        if (!UserConfig.getInstance(this.currentAccount).draftsLoaded) {
            DataQuery.getInstance(this.currentAccount).loadDrafts();
        }
        putUsers(messages_dialogs2.users, false);
        putChats(messages_dialogs2.chats, false);
        for (i = 0; i < this.allDialogs.size(); i++) {
            dialog = (Dialog) this.allDialogs.get(i);
            if (!DialogObject.isSecretDialogId(dialog.id)) {
                this.dialogs_dict.remove(dialog.id);
                MessageObject messageObject = (MessageObject) this.dialogMessage.get(dialog.id);
                this.dialogMessage.remove(dialog.id);
                if (messageObject != null) {
                    this.dialogMessagesByIds.remove(messageObject.getId());
                    j = messageObject.messageOwner.random_id;
                    if (j != 0) {
                        this.dialogMessagesByRandomIds.remove(j);
                    }
                }
            }
        }
        for (i = 0; i < longSparseArray.size(); i++) {
            j = longSparseArray3.keyAt(i);
            dialog = (Dialog) longSparseArray3.valueAt(i);
            if (dialog.draft instanceof TL_draftMessage) {
                DataQuery.getInstance(this.currentAccount).saveDraft(dialog.id, dialog.draft, null, false);
            }
            this.dialogs_dict.put(j, dialog);
            MessageObject messageObject2 = (MessageObject) longSparseArray2.get(dialog.id);
            this.dialogMessage.put(j, messageObject2);
            if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                j = messageObject2.messageOwner.random_id;
                if (j != 0) {
                    this.dialogMessagesByRandomIds.put(j, messageObject2);
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
        i = UserConfig.getInstance(this.currentAccount).getTotalDialogsCount(0);
        int[] dialogLoadOffsets = UserConfig.getInstance(this.currentAccount).getDialogLoadOffsets(0);
        if (!(i >= 400 || dialogLoadOffsets[0] == -1 || dialogLoadOffsets[0] == Integer.MAX_VALUE)) {
            loadDialogs(0, 100, 0, false);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getDialogs, new -$$Lambda$MessagesController$tmAPAmyX_HLSBIR7TwEc-6LXn_I(this, i));
        }
    }

    public /* synthetic */ void lambda$migrateDialogs$126$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$MessagesController$N9vCK0oGVwwS8iSsLW2lhMHnKHI(this, (messages_Dialogs) tLObject, i));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$PNWG6_UzeZ94xJHGN3qJ4RNxOm0(this));
    }

    public /* synthetic */ void lambda$null$124$MessagesController(messages_Dialogs messages_dialogs, int i) {
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        int i2 = i;
        try {
            String str;
            int i3;
            Dialog dialog;
            SQLiteCursor sQLiteCursor;
            int i4;
            UserConfig.getInstance(this.currentAccount).setTotalDialogsCount(0, UserConfig.getInstance(this.currentAccount).getTotalDialogsCount(0) + messages_dialogs2.dialogs.size());
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
            int i6 = -1;
            if (messages_dialogs2.dialogs.size() >= 100) {
                i5 = message.id;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("migrate stop due to not 100 dialogs");
                }
                for (i5 = 0; i5 < 2; i5++) {
                    UserConfig.getInstance(this.currentAccount).setDialogsLoadOffset(i5, Integer.MAX_VALUE, UserConfig.getInstance(this.currentAccount).migrateOffsetDate, UserConfig.getInstance(this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(this.currentAccount).migrateOffsetAccess);
                }
                i5 = -1;
            }
            StringBuilder stringBuilder3 = new StringBuilder(messages_dialogs2.dialogs.size() * 12);
            LongSparseArray longSparseArray = new LongSparseArray();
            for (i3 = 0; i3 < messages_dialogs2.dialogs.size(); i3++) {
                dialog = (Dialog) messages_dialogs2.dialogs.get(i3);
                DialogObject.initDialog(dialog);
                if (stringBuilder3.length() > 0) {
                    stringBuilder3.append(",");
                }
                stringBuilder3.append(dialog.id);
                longSparseArray.put(dialog.id, dialog);
            }
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE did IN (%s)", new Object[]{stringBuilder3.toString()}), new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                dialog = (Dialog) longSparseArray.get(longValue);
                if (dialog.folder_id == queryFinalized.intValue(1)) {
                    longSparseArray.remove(longValue);
                    if (dialog != null) {
                        messages_dialogs2.dialogs.remove(dialog);
                        i3 = 0;
                        while (i3 < messages_dialogs2.messages.size()) {
                            Message message3 = (Message) messages_dialogs2.messages.get(i3);
                            if (MessageObject.getDialogId(message3) == longValue) {
                                messages_dialogs2.messages.remove(i3);
                                i3--;
                                if (message3.id == dialog.top_message) {
                                    dialog.top_message = 0;
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
            queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
            if (queryFinalized.next()) {
                String str2;
                Message message4;
                int max = Math.max(NUM, queryFinalized.intValue(0));
                i3 = i5;
                i5 = 0;
                while (true) {
                    str2 = "migrate stop due to reached loaded dialogs ";
                    if (i5 >= messages_dialogs2.messages.size()) {
                        break;
                    }
                    Message message5 = (Message) messages_dialogs2.messages.get(i5);
                    if (message5.date < max) {
                        if (i2 != i6) {
                            i3 = 0;
                            while (i3 < 2) {
                                sQLiteCursor = queryFinalized;
                                message4 = message;
                                UserConfig.getInstance(this.currentAccount).setDialogsLoadOffset(i3, UserConfig.getInstance(this.currentAccount).migrateOffsetId, UserConfig.getInstance(this.currentAccount).migrateOffsetDate, UserConfig.getInstance(this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(this.currentAccount).migrateOffsetAccess);
                                i3++;
                                queryFinalized = sQLiteCursor;
                                message = message4;
                            }
                            message4 = message;
                            sQLiteCursor = queryFinalized;
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(str2);
                                stringBuilder4.append(LocaleController.getInstance().formatterStats.format(((long) max) * 1000));
                                FileLog.d(stringBuilder4.toString());
                            }
                            i3 = -1;
                        } else {
                            message4 = message;
                            sQLiteCursor = queryFinalized;
                        }
                        messages_dialogs2.messages.remove(i5);
                        i5--;
                        long dialogId = MessageObject.getDialogId(message5);
                        Dialog dialog2 = (Dialog) longSparseArray.get(dialogId);
                        longSparseArray.remove(dialogId);
                        if (dialog2 != null) {
                            messages_dialogs2.dialogs.remove(dialog2);
                        }
                    } else {
                        message4 = message;
                        sQLiteCursor = queryFinalized;
                    }
                    i5++;
                    queryFinalized = sQLiteCursor;
                    message = message4;
                    i6 = -1;
                }
                message4 = message;
                sQLiteCursor = queryFinalized;
                if (message4 != null) {
                    message = message4;
                    if (message.date < max && i2 != -1) {
                        for (i2 = 0; i2 < 2; i2++) {
                            UserConfig.getInstance(this.currentAccount).setDialogsLoadOffset(i2, UserConfig.getInstance(this.currentAccount).migrateOffsetId, UserConfig.getInstance(this.currentAccount).migrateOffsetDate, UserConfig.getInstance(this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(this.currentAccount).migrateOffsetAccess);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(str2);
                            stringBuilder5.append(LocaleController.getInstance().formatterStats.format(((long) max) * 1000));
                            FileLog.d(stringBuilder5.toString());
                        }
                        i4 = -1;
                    }
                } else {
                    message = message4;
                }
                i4 = i3;
            } else {
                sQLiteCursor = queryFinalized;
                i4 = i5;
            }
            sQLiteCursor.dispose();
            UserConfig.getInstance(this.currentAccount).migrateOffsetDate = message.date;
            Chat chat;
            if (message.to_id.channel_id != 0) {
                UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId = message.to_id.channel_id;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChatId = 0;
                UserConfig.getInstance(this.currentAccount).migrateOffsetUserId = 0;
                for (i2 = 0; i2 < messages_dialogs2.chats.size(); i2++) {
                    chat = (Chat) messages_dialogs2.chats.get(i2);
                    if (chat.id == UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId) {
                        UserConfig.getInstance(this.currentAccount).migrateOffsetAccess = chat.access_hash;
                        break;
                    }
                }
            } else if (message.to_id.chat_id != 0) {
                UserConfig.getInstance(this.currentAccount).migrateOffsetChatId = message.to_id.chat_id;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId = 0;
                UserConfig.getInstance(this.currentAccount).migrateOffsetUserId = 0;
                for (i2 = 0; i2 < messages_dialogs2.chats.size(); i2++) {
                    chat = (Chat) messages_dialogs2.chats.get(i2);
                    if (chat.id == UserConfig.getInstance(this.currentAccount).migrateOffsetChatId) {
                        UserConfig.getInstance(this.currentAccount).migrateOffsetAccess = chat.access_hash;
                        break;
                    }
                }
            } else if (message.to_id.user_id != 0) {
                UserConfig.getInstance(this.currentAccount).migrateOffsetUserId = message.to_id.user_id;
                i5 = 0;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChatId = 0;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId = 0;
                while (i5 < messages_dialogs2.users.size()) {
                    User user = (User) messages_dialogs2.users.get(i5);
                    if (user.id == UserConfig.getInstance(this.currentAccount).migrateOffsetUserId) {
                        UserConfig.getInstance(this.currentAccount).migrateOffsetAccess = user.access_hash;
                        break;
                    }
                    i5++;
                }
            }
            processLoadedDialogs(messages_dialogs, null, 0, i4, 0, 0, false, true, false);
        } catch (Exception e) {
            FileLog.e(e);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$n72oPYeD0dDCBaP1ov8Q-eP6egE(this));
        }
    }

    public /* synthetic */ void lambda$null$123$MessagesController() {
        this.migratingDialogs = false;
    }

    public /* synthetic */ void lambda$null$125$MessagesController() {
        this.migratingDialogs = false;
    }

    public void processLoadedDialogs(messages_Dialogs messages_dialogs, ArrayList<EncryptedChat> arrayList, int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$shj4tQAJ8i7NH8eiXBAGt48jDcs(this, i, i4, messages_dialogs, z, i3, arrayList, i2, z3, z2));
    }

    /* JADX WARNING: Missing block: B:120:0x0293, code skipped:
            if (r2.get(r3) == null) goto L_0x0295;
     */
    /* JADX WARNING: Missing block: B:142:0x02df, code skipped:
            if (r5 == r1.id) goto L_0x02e3;
     */
    /* JADX WARNING: Missing block: B:150:0x0300, code skipped:
            if (r3.migrated_to != null) goto L_0x0295;
     */
    public /* synthetic */ void lambda$processLoadedDialogs$129$MessagesController(int r24, int r25, org.telegram.tgnet.TLRPC.messages_Dialogs r26, boolean r27, int r28, java.util.ArrayList r29, int r30, boolean r31, boolean r32) {
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
        r0 = r14.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r5 = r0.getDialogLoadOffsets(r9);
        r0 = r14.DIALOGS_LOAD_TYPE_CACHE;
        if (r10 != r0) goto L_0x0072;
    L_0x0056:
        r0 = r11.dialogs;
        r0 = r0.size();
        if (r0 != 0) goto L_0x0072;
    L_0x005e:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesController$vf0a3gbyExIJKu8Fv0SfXfbDaBc;
        r0 = r7;
        r1 = r23;
        r2 = r26;
        r3 = r24;
        r4 = r27;
        r6 = r28;
        r0.<init>(r1, r2, r3, r4, r5, r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);
        return;
    L_0x0072:
        r7 = new android.util.LongSparseArray;
        r7.<init>();
        r8 = new android.util.LongSparseArray;
        r8.<init>();
        r6 = new android.util.SparseArray;
        r6.<init>();
        r4 = new android.util.SparseArray;
        r4.<init>();
        r0 = 0;
    L_0x0087:
        r2 = r11.users;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x009f;
    L_0x008f:
        r2 = r11.users;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        r3 = r2.id;
        r6.put(r3, r2);
        r0 = r0 + 1;
        goto L_0x0087;
    L_0x009f:
        r0 = 0;
    L_0x00a0:
        r2 = r11.chats;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x00b8;
    L_0x00a8:
        r2 = r11.chats;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Chat) r2;
        r3 = r2.id;
        r4.put(r3, r2);
        r0 = r0 + 1;
        goto L_0x00a0;
    L_0x00b8:
        if (r12 == 0) goto L_0x00da;
    L_0x00ba:
        r0 = new android.util.SparseArray;
        r0.<init>();
        r2 = r29.size();
        r3 = 0;
    L_0x00c4:
        if (r3 >= r2) goto L_0x00d8;
    L_0x00c6:
        r17 = r12.get(r3);
        r1 = r17;
        r1 = (org.telegram.tgnet.TLRPC.EncryptedChat) r1;
        r13 = r1.id;
        r0.put(r13, r1);
        r3 = r3 + 1;
        r1 = 0;
        r13 = 1;
        goto L_0x00c4;
    L_0x00d8:
        r13 = r0;
        goto L_0x00db;
    L_0x00da:
        r13 = 0;
    L_0x00db:
        r0 = r14.DIALOGS_LOAD_TYPE_CACHE;
        if (r10 != r0) goto L_0x00e6;
    L_0x00df:
        r0 = r14.nextDialogsCacheOffset;
        r1 = r30 + r28;
        r0.put(r9, r1);
    L_0x00e6:
        r0 = 0;
        r1 = 0;
    L_0x00e8:
        r2 = r11.messages;
        r2 = r2.size();
        r17 = 0;
        if (r0 >= r2) goto L_0x0170;
    L_0x00f2:
        r2 = r11.messages;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.Message) r2;
        if (r1 == 0) goto L_0x0102;
    L_0x00fc:
        r3 = r2.date;
        r15 = r1.date;
        if (r3 >= r15) goto L_0x0103;
    L_0x0102:
        r1 = r2;
    L_0x0103:
        r3 = r2.to_id;
        r15 = r3.channel_id;
        if (r15 == 0) goto L_0x0136;
    L_0x0109:
        r3 = r4.get(r15);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x0126;
    L_0x0111:
        r15 = r3.left;
        if (r15 == 0) goto L_0x0126;
    L_0x0115:
        r27 = r13;
        r12 = r14.proxyDialogId;
        r15 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1));
        if (r15 == 0) goto L_0x0163;
    L_0x011d:
        r15 = r3.id;
        r15 = -r15;
        r9 = (long) r15;
        r15 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1));
        if (r15 == 0) goto L_0x0128;
    L_0x0125:
        goto L_0x0163;
    L_0x0126:
        r27 = r13;
    L_0x0128:
        if (r3 == 0) goto L_0x0149;
    L_0x012a:
        r3 = r3.megagroup;
        if (r3 == 0) goto L_0x0149;
    L_0x012e:
        r3 = r2.flags;
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r9;
        r2.flags = r3;
        goto L_0x0149;
    L_0x0136:
        r27 = r13;
        r3 = r3.chat_id;
        if (r3 == 0) goto L_0x0149;
    L_0x013c:
        r3 = r4.get(r3);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x0149;
    L_0x0144:
        r3 = r3.migrated_to;
        if (r3 == 0) goto L_0x0149;
    L_0x0148:
        goto L_0x0163;
    L_0x0149:
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
    L_0x0163:
        r0 = r0 + 1;
        r9 = r24;
        r10 = r25;
        r13 = r27;
        r12 = r29;
        r15 = 0;
        goto L_0x00e8;
    L_0x0170:
        r27 = r13;
        if (r31 != 0) goto L_0x0258;
    L_0x0174:
        if (r32 != 0) goto L_0x0258;
    L_0x0176:
        r0 = 0;
        r2 = r5[r0];
        r3 = -1;
        if (r2 == r3) goto L_0x0258;
    L_0x017c:
        r9 = r25;
        if (r9 != 0) goto L_0x0258;
    L_0x0180:
        r2 = r14.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r10 = r24;
        r2 = r2.getTotalDialogsCount(r10);
        if (r1 == 0) goto L_0x0221;
    L_0x018e:
        r3 = r1.id;
        r5 = r5[r0];
        if (r3 == r5) goto L_0x0221;
    L_0x0194:
        r0 = r11.dialogs;
        r0 = r0.size();
        r2 = r2 + r0;
        r0 = r1.id;
        r3 = r1.date;
        r1 = r1.to_id;
        r5 = r1.channel_id;
        if (r5 == 0) goto L_0x01cb;
    L_0x01a5:
        r1 = 0;
    L_0x01a6:
        r12 = r11.chats;
        r12 = r12.size();
        if (r1 >= r12) goto L_0x01c0;
    L_0x01ae:
        r12 = r11.chats;
        r12 = r12.get(r1);
        r12 = (org.telegram.tgnet.TLRPC.Chat) r12;
        r13 = r12.id;
        if (r13 != r5) goto L_0x01bd;
    L_0x01ba:
        r12 = r12.access_hash;
        goto L_0x01c2;
    L_0x01bd:
        r1 = r1 + 1;
        goto L_0x01a6;
    L_0x01c0:
        r12 = r17;
    L_0x01c2:
        r15 = r2;
        r19 = r12;
        r12 = 0;
        r2 = r0;
        r13 = r5;
    L_0x01c8:
        r5 = 0;
        goto L_0x022e;
    L_0x01cb:
        r5 = r1.chat_id;
        if (r5 == 0) goto L_0x01f3;
    L_0x01cf:
        r1 = 0;
    L_0x01d0:
        r12 = r11.chats;
        r12 = r12.size();
        if (r1 >= r12) goto L_0x01ea;
    L_0x01d8:
        r12 = r11.chats;
        r12 = r12.get(r1);
        r12 = (org.telegram.tgnet.TLRPC.Chat) r12;
        r13 = r12.id;
        if (r13 != r5) goto L_0x01e7;
    L_0x01e4:
        r12 = r12.access_hash;
        goto L_0x01ec;
    L_0x01e7:
        r1 = r1 + 1;
        goto L_0x01d0;
    L_0x01ea:
        r12 = r17;
    L_0x01ec:
        r15 = r2;
        r19 = r12;
        r13 = 0;
        r2 = r0;
        r12 = r5;
        goto L_0x01c8;
    L_0x01f3:
        r1 = r1.user_id;
        if (r1 == 0) goto L_0x0219;
    L_0x01f7:
        r5 = 0;
    L_0x01f8:
        r12 = r11.users;
        r12 = r12.size();
        if (r5 >= r12) goto L_0x0212;
    L_0x0200:
        r12 = r11.users;
        r12 = r12.get(r5);
        r12 = (org.telegram.tgnet.TLRPC.User) r12;
        r13 = r12.id;
        if (r13 != r1) goto L_0x020f;
    L_0x020c:
        r12 = r12.access_hash;
        goto L_0x0214;
    L_0x020f:
        r5 = r5 + 1;
        goto L_0x01f8;
    L_0x0212:
        r12 = r17;
    L_0x0214:
        r5 = r1;
        r15 = r2;
        r19 = r12;
        goto L_0x021d;
    L_0x0219:
        r15 = r2;
        r19 = r17;
        r5 = 0;
    L_0x021d:
        r12 = 0;
        r13 = 0;
        r2 = r0;
        goto L_0x022e;
    L_0x0221:
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r15 = r2;
        r19 = r17;
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r3 = 0;
        r5 = 0;
        r12 = 0;
        r13 = 0;
    L_0x022e:
        r0 = r14.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
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
        r0 = r14.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0.setTotalDialogsCount(r10, r15);
        r0 = r14.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r1 = 0;
        r0.saveConfig(r1);
        goto L_0x025f;
    L_0x0258:
        r10 = r24;
        r9 = r4;
        r21 = r6;
        r13 = r7;
        r12 = r8;
    L_0x025f:
        r15 = new java.util.ArrayList;
        r15.<init>();
        r0 = 0;
    L_0x0265:
        r1 = r11.dialogs;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x037f;
    L_0x026d:
        r1 = r11.dialogs;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Dialog) r1;
        org.telegram.messenger.DialogObject.initDialog(r1);
        r2 = r1.id;
        r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1));
        if (r4 != 0) goto L_0x0284;
    L_0x027e:
        r4 = r25;
        r2 = r27;
        goto L_0x0379;
    L_0x0284:
        r4 = (int) r2;
        r5 = 32;
        r2 = r2 >> r5;
        r3 = (int) r2;
        if (r4 != 0) goto L_0x0299;
    L_0x028b:
        if (r27 == 0) goto L_0x0299;
    L_0x028d:
        r2 = r27;
        r3 = r2.get(r3);
        if (r3 != 0) goto L_0x029b;
    L_0x0295:
        r4 = r25;
        goto L_0x0379;
    L_0x0299:
        r2 = r27;
    L_0x029b:
        r3 = r14.proxyDialogId;
        r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1));
        if (r5 == 0) goto L_0x02a9;
    L_0x02a1:
        r5 = r1.id;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 != 0) goto L_0x02a9;
    L_0x02a7:
        r14.proxyDialog = r1;
    L_0x02a9:
        r3 = r1.last_message_date;
        if (r3 != 0) goto L_0x02bd;
    L_0x02ad:
        r3 = r1.id;
        r3 = r12.get(r3);
        r3 = (org.telegram.messenger.MessageObject) r3;
        if (r3 == 0) goto L_0x02bd;
    L_0x02b7:
        r3 = r3.messageOwner;
        r3 = r3.date;
        r1.last_message_date = r3;
    L_0x02bd:
        r3 = org.telegram.messenger.DialogObject.isChannel(r1);
        if (r3 == 0) goto L_0x02ef;
    L_0x02c3:
        r3 = r1.id;
        r4 = (int) r3;
        r3 = -r4;
        r3 = r9.get(r3);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x02e2;
    L_0x02cf:
        r4 = r3.megagroup;
        r3 = r3.left;
        if (r3 == 0) goto L_0x02e3;
    L_0x02d5:
        r5 = r14.proxyDialogId;
        r3 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1));
        if (r3 == 0) goto L_0x0295;
    L_0x02db:
        r7 = r1.id;
        r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r3 == 0) goto L_0x02e3;
    L_0x02e1:
        goto L_0x0295;
    L_0x02e2:
        r4 = 1;
    L_0x02e3:
        r3 = r14.channelsPts;
        r5 = r1.id;
        r6 = (int) r5;
        r5 = -r6;
        r6 = r1.pts;
        r3.put(r5, r6);
        goto L_0x0304;
    L_0x02ef:
        r3 = r1.id;
        r5 = (int) r3;
        if (r5 >= 0) goto L_0x0303;
    L_0x02f4:
        r4 = (int) r3;
        r3 = -r4;
        r3 = r9.get(r3);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        if (r3 == 0) goto L_0x0303;
    L_0x02fe:
        r3 = r3.migrated_to;
        if (r3 == 0) goto L_0x0303;
    L_0x0302:
        goto L_0x0295;
    L_0x0303:
        r4 = 1;
    L_0x0304:
        r5 = r1.id;
        r13.put(r5, r1);
        if (r4 == 0) goto L_0x0321;
    L_0x030b:
        r3 = r14.DIALOGS_LOAD_TYPE_CACHE;
        r4 = r25;
        if (r4 != r3) goto L_0x0323;
    L_0x0311:
        r3 = r1.read_outbox_max_id;
        if (r3 == 0) goto L_0x0319;
    L_0x0315:
        r3 = r1.read_inbox_max_id;
        if (r3 != 0) goto L_0x0323;
    L_0x0319:
        r3 = r1.top_message;
        if (r3 == 0) goto L_0x0323;
    L_0x031d:
        r15.add(r1);
        goto L_0x0323;
    L_0x0321:
        r4 = r25;
    L_0x0323:
        r3 = r14.dialogs_read_inbox_max;
        r5 = r1.id;
        r5 = java.lang.Long.valueOf(r5);
        r3 = r3.get(r5);
        r3 = (java.lang.Integer) r3;
        if (r3 != 0) goto L_0x0335;
    L_0x0333:
        r3 = r16;
    L_0x0335:
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
        if (r3 != 0) goto L_0x0360;
    L_0x035e:
        r3 = r16;
    L_0x0360:
        r5 = r14.dialogs_read_outbox_max;
        r6 = r1.id;
        r6 = java.lang.Long.valueOf(r6);
        r3 = r3.intValue();
        r1 = r1.read_outbox_max_id;
        r1 = java.lang.Math.max(r3, r1);
        r1 = java.lang.Integer.valueOf(r1);
        r5.put(r6, r1);
    L_0x0379:
        r0 = r0 + 1;
        r27 = r2;
        goto L_0x0265;
    L_0x037f:
        r4 = r25;
        r0 = r14.DIALOGS_LOAD_TYPE_CACHE;
        if (r4 == r0) goto L_0x0423;
    L_0x0385:
        r0 = r11.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r0);
        r0 = 0;
    L_0x038b:
        r1 = r11.messages;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x0418;
    L_0x0393:
        r1 = r11.messages;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;
        r2 = r1.action;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r3 == 0) goto L_0x03bf;
    L_0x03a1:
        r2 = r2.user_id;
        r3 = r21;
        r2 = r3.get(r2);
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        if (r2 == 0) goto L_0x03c1;
    L_0x03ad:
        r2 = r2.bot;
        if (r2 == 0) goto L_0x03c1;
    L_0x03b1:
        r2 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r2.<init>();
        r1.reply_markup = r2;
        r2 = r1.flags;
        r2 = r2 | 64;
        r1.flags = r2;
        goto L_0x03c1;
    L_0x03bf:
        r3 = r21;
    L_0x03c1:
        r2 = r1.action;
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r5 != 0) goto L_0x040d;
    L_0x03c7:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r2 == 0) goto L_0x03cc;
    L_0x03cb:
        goto L_0x040d;
    L_0x03cc:
        r2 = r1.out;
        if (r2 == 0) goto L_0x03d3;
    L_0x03d0:
        r2 = r14.dialogs_read_outbox_max;
        goto L_0x03d5;
    L_0x03d3:
        r2 = r14.dialogs_read_inbox_max;
    L_0x03d5:
        r5 = r1.dialog_id;
        r5 = java.lang.Long.valueOf(r5);
        r5 = r2.get(r5);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x03fe;
    L_0x03e3:
        r5 = r14.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r6 = r1.out;
        r7 = r1.dialog_id;
        r5 = r5.getDialogReadMax(r6, r7);
        r5 = java.lang.Integer.valueOf(r5);
        r6 = r1.dialog_id;
        r6 = java.lang.Long.valueOf(r6);
        r2.put(r6, r5);
    L_0x03fe:
        r2 = r5.intValue();
        r5 = r1.id;
        if (r2 >= r5) goto L_0x0408;
    L_0x0406:
        r2 = 1;
        goto L_0x0409;
    L_0x0408:
        r2 = 0;
    L_0x0409:
        r1.unread = r2;
        r2 = 0;
        goto L_0x0412;
    L_0x040d:
        r2 = 0;
        r1.unread = r2;
        r1.media_unread = r2;
    L_0x0412:
        r0 = r0 + 1;
        r21 = r3;
        goto L_0x038b;
    L_0x0418:
        r2 = 0;
        r0 = r14.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.putDialogs(r11, r2);
        goto L_0x0424;
    L_0x0423:
        r2 = 0;
    L_0x0424:
        r0 = r14.DIALOGS_LOAD_TYPE_CHANNEL;
        if (r4 != r0) goto L_0x043a;
    L_0x0428:
        r0 = r11.chats;
        r0 = r0.get(r2);
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r1 = r0.id;
        r14.getChannelDifference(r1);
        r0 = r0.id;
        r14.checkChannelInviter(r0);
    L_0x043a:
        r16 = new org.telegram.messenger.-$$Lambda$MessagesController$OMZI4TzcbS75iT0wAGlu_LDNynw;
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogs$129$MessagesController(int, int, org.telegram.tgnet.TLRPC$messages_Dialogs, boolean, int, java.util.ArrayList, int, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$null$127$MessagesController(messages_Dialogs messages_dialogs, int i, boolean z, int[] iArr, int i2) {
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$128$MessagesController(int i, messages_Dialogs messages_dialogs, ArrayList arrayList, boolean z, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, SparseArray sparseArray, int i3, boolean z2, int i4, ArrayList arrayList2) {
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
            if (!UserConfig.getInstance(this.currentAccount).draftsLoaded) {
                DataQuery.getInstance(this.currentAccount).loadDrafts();
            }
        }
        putUsers(messages_dialogs2.users, i7 == this.DIALOGS_LOAD_TYPE_CACHE);
        putChats(messages_dialogs2.chats, i7 == this.DIALOGS_LOAD_TYPE_CACHE);
        if (arrayList3 != null) {
            for (i5 = 0; i5 < arrayList.size(); i5++) {
                EncryptedChat encryptedChat = (EncryptedChat) arrayList3.get(i5);
                if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 73) {
                    SecretChatHelper.getInstance(this.currentAccount).sendNotifyLayerMessage(encryptedChat, null);
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
                    DataQuery.getInstance(this.currentAccount).saveDraft(dialog.id, dialog.draft, null, false);
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
                if (UserConfig.getInstance(this.currentAccount).getDialogLoadOffsets(i8)[0] == Integer.MAX_VALUE) {
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
        size = UserConfig.getInstance(this.currentAccount).getTotalDialogsCount(i8);
        int[] dialogLoadOffsets = UserConfig.getInstance(this.currentAccount).getDialogLoadOffsets(i8);
        if (z2 || z || size >= 400) {
            size = 0;
        } else {
            size = 0;
            if (!(dialogLoadOffsets[0] == -1 || dialogLoadOffsets[0] == Integer.MAX_VALUE)) {
                loadDialogs(0, 100, i8, false);
            }
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[size]);
        if (z) {
            UserConfig.getInstance(this.currentAccount).migrateOffsetId = i4;
            UserConfig.getInstance(this.currentAccount).saveConfig(size);
            this.migratingDialogs = size;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[size]);
        } else {
            generateUpdateMessage();
            if (obj == null && i7 == this.DIALOGS_LOAD_TYPE_CACHE) {
                loadDialogs(i8, size, i9, size);
            }
        }
        migrateDialogs(UserConfig.getInstance(this.currentAccount).migrateOffsetId, UserConfig.getInstance(this.currentAccount).migrateOffsetDate, UserConfig.getInstance(this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(this.currentAccount).migrateOffsetAccess);
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
                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j2, 0);
            } else if (peerNotifySettings2.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                int i3;
                if (peerNotifySettings2.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
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
                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j2, (((long) i3) << 32) | 1);
                NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(j2);
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
                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j2, 0);
            }
            edit.commit();
            if (obj != null) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
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
                } else if (peerNotifySettings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str2);
                    stringBuilder2.append(i2);
                    editor.putInt(stringBuilder2.toString(), 0);
                } else if (dialog.notify_settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$Wb5lXI806u6uYMpUfHZLtqjdAzg(this, arrayList));
    }

    public /* synthetic */ void lambda$reloadMentionsCountForChannels$132$MessagesController(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            long j = (long) (-((Integer) arrayList.get(i)).intValue());
            TL_messages_getUnreadMentions tL_messages_getUnreadMentions = new TL_messages_getUnreadMentions();
            tL_messages_getUnreadMentions.peer = getInputPeer((int) j);
            tL_messages_getUnreadMentions.limit = 1;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getUnreadMentions, new -$$Lambda$MessagesController$XzymKbdqXufr7jm1iFFTz7X7ruw(this, j));
        }
    }

    public /* synthetic */ void lambda$null$131$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$pgmXc5wuX4vuUELeAuqKQqje9q8(this, tLObject, j));
    }

    public /* synthetic */ void lambda$null$130$MessagesController(TLObject tLObject, long j) {
        messages_Messages messages_messages = (messages_Messages) tLObject;
        if (messages_messages != null) {
            int i = messages_messages.count;
            if (i == 0) {
                i = messages_messages.messages.size();
            }
            MessagesStorage.getInstance(this.currentAccount).resetMentionsCount(j, i);
        }
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> longSparseArray, LongSparseArray<Integer> longSparseArray2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$_YsSBCaL_qbcH96s3TWAMj3YbZI(this, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$133$MessagesController(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        int i;
        if (longSparseArray != null) {
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
                }
            }
        }
        if (longSparseArray2 != null) {
            for (i = 0; i < longSparseArray2.size(); i++) {
                Dialog dialog2 = (Dialog) this.dialogs_dict.get(longSparseArray2.keyAt(i));
                if (dialog2 != null) {
                    dialog2.unread_mentions_count = ((Integer) longSparseArray2.valueAt(i)).intValue();
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(dialog2.id))) {
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(dialog2.id), Integer.valueOf(dialog2.unread_mentions_count));
                    }
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        if (longSparseArray != null) {
            NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(longSparseArray);
        }
    }

    /* Access modifiers changed, original: protected */
    public void checkLastDialogMessage(Dialog dialog, InputPeer inputPeer, long j) {
        Throwable e;
        int i = (int) dialog.id;
        if (i != 0 && this.checkingLastMessagesDialogs.indexOfKey(i) < 0) {
            TL_messages_getHistory tL_messages_getHistory = new TL_messages_getHistory();
            tL_messages_getHistory.peer = inputPeer == null ? getInputPeer(i) : inputPeer;
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
                            inputPeer.serializeToStream(nativeByteBuffer);
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        nativeByteBuffer = null;
                        FileLog.e(e);
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getHistory, new -$$Lambda$MessagesController$LzTCLk4PcU0AIVN_3fmrcJA4k9I(this, i, dialog, j));
                    }
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getHistory, new -$$Lambda$MessagesController$LzTCLk4PcU0AIVN_3fmrcJA4k9I(this, i, dialog, j));
            }
        }
    }

    public /* synthetic */ void lambda$checkLastDialogMessage$136$MessagesController(int i, Dialog dialog, long j, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            removeDeletedMessagesFromArray((long) i, messages_messages.messages);
            if (messages_messages.messages.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$B2hd0oVj2x9ARog9ivfgDWOv-vg(this, dialog));
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
                MessagesStorage.getInstance(this.currentAccount).putMessages(messages_messages.messages, true, true, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask(), true);
            }
        }
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$Vi0DJPkGrsNPy-GOeNVBnJWDwnE(this, i));
    }

    public /* synthetic */ void lambda$null$134$MessagesController(Dialog dialog) {
        Dialog dialog2 = (Dialog) this.dialogs_dict.get(dialog.id);
        if (dialog2 != null && dialog2.top_message == 0) {
            deleteDialog(dialog.id, 3);
        }
    }

    public /* synthetic */ void lambda$null$135$MessagesController(int i) {
        this.checkingLastMessagesDialogs.delete(i);
    }

    public void processDialogsUpdate(messages_Dialogs messages_dialogs, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$q64E9hWX3Xly3JADViQSa2zMJVo(this, messages_dialogs));
    }

    public /* synthetic */ void lambda$processDialogsUpdate$138$MessagesController(messages_Dialogs messages_dialogs) {
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$oWzxev3PBWOpBqcrFUkL5QjXPQo(this, messages_dialogs, longSparseArray, longSparseArray2, longSparseArray3));
    }

    public /* synthetic */ void lambda$null$137$MessagesController(messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
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
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(dialog2.id), Integer.valueOf(dialog2.unread_mentions_count));
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(longSparseArray3);
    }

    public void addToViewsQueue(MessageObject messageObject) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$cWol_41rOBrStFY3FKcjDkmBodE(this, messageObject));
    }

    public /* synthetic */ void lambda$addToViewsQueue$139$MessagesController(MessageObject messageObject) {
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
        ArrayList arrayList = new ArrayList();
        long id = (long) messageObject.getId();
        int i = messageObject.messageOwner.to_id.channel_id;
        if (i != 0) {
            id |= ((long) i) << 32;
        }
        if (messageObject.messageOwner.mentioned) {
            MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(messageObject.getId(), messageObject.messageOwner.to_id.channel_id, messageObject.getDialogId());
        }
        arrayList.add(Long.valueOf(id));
        MessagesStorage.getInstance(this.currentAccount).markMessagesContentAsRead(arrayList, 0);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, Integer.MIN_VALUE);
        } else if (messageObject.messageOwner.to_id.channel_id != 0) {
            TL_channels_readMessageContents tL_channels_readMessageContents = new TL_channels_readMessageContents();
            tL_channels_readMessageContents.channel = getInputChannel(messageObject.messageOwner.to_id.channel_id);
            if (tL_channels_readMessageContents.channel != null) {
                tL_channels_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_readMessageContents, -$$Lambda$MessagesController$GZZLBgVSubugJqEDeBHWQk3eSTE.INSTANCE);
            }
        } else {
            TL_messages_readMessageContents tL_messages_readMessageContents = new TL_messages_readMessageContents();
            tL_messages_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_readMessageContents, new -$$Lambda$MessagesController$gAx8uSIsyHdT1Tx6VIiWEu0UYbQ(this));
        }
    }

    public /* synthetic */ void lambda$markMessageContentAsRead$141$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public void markMentionMessageAsRead(int i, int i2, long j) {
        MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(i, i2, j);
        if (i2 != 0) {
            TL_channels_readMessageContents tL_channels_readMessageContents = new TL_channels_readMessageContents();
            tL_channels_readMessageContents.channel = getInputChannel(i2);
            if (tL_channels_readMessageContents.channel != null) {
                tL_channels_readMessageContents.id.add(Integer.valueOf(i));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_readMessageContents, -$$Lambda$MessagesController$APiRxvKDu_B4Za4CATjRgQ8eiYM.INSTANCE);
            } else {
                return;
            }
        }
        TL_messages_readMessageContents tL_messages_readMessageContents = new TL_messages_readMessageContents();
        tL_messages_readMessageContents.id.add(Integer.valueOf(i));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_readMessageContents, new -$$Lambda$MessagesController$HGjYxt4hoLR6qJGd10EuSLrAkII(this));
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$143$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0088  */
    public void markMessageAsRead(int r10, int r11, org.telegram.tgnet.TLRPC.InputChannel r12, int r13, long r14) {
        /*
        r9 = this;
        if (r10 == 0) goto L_0x00a4;
    L_0x0002:
        if (r13 > 0) goto L_0x0006;
    L_0x0004:
        goto L_0x00a4;
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
        if (r2 != 0) goto L_0x004f;
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
        r14 = r9.currentAccount;
        r14 = org.telegram.messenger.MessagesStorage.getInstance(r14);
        r14 = r14.createPendingTask(r15);
    L_0x004f:
        r0 = r9.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r5 = r0.getCurrentTime();
        r0 = r9.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r7 = 0;
        r2 = r10;
        r3 = r11;
        r4 = r5;
        r6 = r13;
        r1.createTaskForMid(r2, r3, r4, r5, r6, r7);
        if (r11 == 0) goto L_0x0088;
    L_0x0069:
        r11 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents;
        r11.<init>();
        r11.channel = r12;
        r12 = r11.id;
        r10 = java.lang.Integer.valueOf(r10);
        r12.add(r10);
        r10 = r9.currentAccount;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r12 = new org.telegram.messenger.-$$Lambda$MessagesController$U0WOqTBPKmEhdWNNNHXKPpg45io;
        r12.<init>(r9, r14);
        r10.sendRequest(r11, r12);
        goto L_0x00a4;
    L_0x0088:
        r11 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents;
        r11.<init>();
        r12 = r11.id;
        r10 = java.lang.Integer.valueOf(r10);
        r12.add(r10);
        r10 = r9.currentAccount;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r12 = new org.telegram.messenger.-$$Lambda$MessagesController$jXQeCffIHFZ5lMnVQBUWXIuIwZ4;
        r12.<init>(r9, r14);
        r10.sendRequest(r11, r12);
    L_0x00a4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markMessageAsRead(int, int, org.telegram.tgnet.TLRPC$InputChannel, int, long):void");
    }

    public /* synthetic */ void lambda$markMessageAsRead$144$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$markMessageAsRead$145$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
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
                    SecretChatHelper.getInstance(this.currentAccount).sendMessagesReadMessage(encryptedChat, arrayList, null);
                    if (i > 0) {
                        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                        MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(encryptedChat.id, currentTime, currentTime, 0, arrayList);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_readHistory, new -$$Lambda$MessagesController$f6Fg5cePsPVR4IQG1UUiIG6Ywco(this));
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_readEncryptedHistory, -$$Lambda$MessagesController$mkj69nAVhIC1gtqhKztiCLASSNAMEPgeU.INSTANCE);
        }
    }

    public /* synthetic */ void lambda$completeReadTask$146$MessagesController(TLObject tLObject, TL_error tL_error) {
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$5qY48mZcpUp58ZVQMwppSQnDlKQ(this, j));
    }

    public /* synthetic */ void lambda$markDialogAsReadNow$148$MessagesController(long j) {
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
            MessagesStorage.getInstance(this.currentAccount).resetMentionsCount(j, 0);
            TL_messages_readMentions tL_messages_readMentions = new TL_messages_readMentions();
            tL_messages_readMentions.peer = getInstance(this.currentAccount).getInputPeer(i);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_readMentions, -$$Lambda$MessagesController$eUPv0EVHJrau5dp5Vk20Bf_j8L4.INSTANCE);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x010b  */
    public void markDialogAsRead(long r19, int r21, int r22, int r23, boolean r24, int r25, boolean r26) {
        /*
        r18 = this;
        r9 = r18;
        r10 = r19;
        r12 = r21;
        r13 = r22;
        r14 = r23;
        r0 = (int) r10;
        r1 = 32;
        r2 = r10 >> r1;
        r3 = (int) r2;
        r2 = r9.currentAccount;
        r2 = org.telegram.messenger.NotificationsController.getInstance(r2);
        r15 = r2.showBadgeMessages;
        r8 = 1;
        if (r0 == 0) goto L_0x00a4;
    L_0x001b:
        if (r12 == 0) goto L_0x00a3;
    L_0x001d:
        if (r3 != r8) goto L_0x0021;
    L_0x001f:
        goto L_0x00a3;
    L_0x0021:
        r2 = (long) r12;
        r4 = (long) r13;
        r13 = 0;
        if (r0 >= 0) goto L_0x003f;
    L_0x0026:
        r0 = -r0;
        r6 = java.lang.Integer.valueOf(r0);
        r6 = r9.getChat(r6);
        r6 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x003f;
    L_0x0035:
        r6 = (long) r0;
        r0 = r6 << r1;
        r2 = r2 | r0;
        r0 = r0 | r4;
        r5 = r0;
        r3 = r2;
        r16 = 1;
        goto L_0x0043;
    L_0x003f:
        r5 = r4;
        r16 = 0;
        r3 = r2;
    L_0x0043:
        r0 = r9.dialogs_read_inbox_max;
        r1 = java.lang.Long.valueOf(r19);
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        if (r0 != 0) goto L_0x0055;
    L_0x0051:
        r0 = java.lang.Integer.valueOf(r13);
    L_0x0055:
        r1 = r9.dialogs_read_inbox_max;
        r2 = java.lang.Long.valueOf(r19);
        r0 = r0.intValue();
        r0 = java.lang.Math.max(r0, r12);
        r0 = java.lang.Integer.valueOf(r0);
        r1.put(r2, r0);
        r0 = r9.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r1 = r19;
        r7 = r23;
        r17 = 1;
        r8 = r16;
        r0.processPendingRead(r1, r3, r5, r7, r8);
        r0 = r9.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r8 = r0.getStorageQueue();
        r7 = new org.telegram.messenger.-$$Lambda$MessagesController$hn-SvAXVeuxCYnxgaaX_Rgf7tNs;
        r0 = r7;
        r1 = r18;
        r2 = r19;
        r4 = r25;
        r5 = r21;
        r6 = r15;
        r15 = r7;
        r7 = r24;
        r0.<init>(r1, r2, r4, r5, r6, r7);
        r8.postRunnable(r15);
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r12 == r0) goto L_0x00a1;
    L_0x009f:
        goto L_0x0108;
    L_0x00a1:
        r8 = 0;
        goto L_0x0109;
    L_0x00a3:
        return;
    L_0x00a4:
        r17 = 1;
        if (r14 != 0) goto L_0x00a9;
    L_0x00a8:
        return;
    L_0x00a9:
        r0 = java.lang.Integer.valueOf(r3);
        r8 = r9.getEncryptedChat(r0);
        r0 = r9.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r3 = (long) r12;
        r5 = (long) r13;
        r16 = 0;
        r1 = r19;
        r7 = r23;
        r10 = r8;
        r8 = r16;
        r0.processPendingRead(r1, r3, r5, r7, r8);
        r0 = r9.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r11 = r0.getStorageQueue();
        r8 = new org.telegram.messenger.-$$Lambda$MessagesController$w9XQvAOdl78XSvAXCJDSbdLvKsQ;
        r0 = r8;
        r1 = r18;
        r2 = r19;
        r4 = r23;
        r5 = r24;
        r6 = r25;
        r7 = r22;
        r13 = r8;
        r8 = r15;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8);
        r11.postRunnable(r13);
        if (r10 == 0) goto L_0x0108;
    L_0x00e8:
        r0 = r10.ttl;
        if (r0 <= 0) goto L_0x0108;
    L_0x00ec:
        r0 = r9.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r0 = r0.getCurrentTime();
        r4 = java.lang.Math.max(r0, r14);
        r0 = r9.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r2 = r10.id;
        r5 = 0;
        r6 = 0;
        r3 = r4;
        r1.createTaskForSecretChat(r2, r3, r4, r5, r6);
    L_0x0108:
        r8 = 1;
    L_0x0109:
        if (r8 == 0) goto L_0x0120;
    L_0x010b:
        r7 = org.telegram.messenger.Utilities.stageQueue;
        r8 = new org.telegram.messenger.-$$Lambda$MessagesController$_VEgWywMVWfxr_9Ssi-lPDOcdtc;
        r0 = r8;
        r1 = r18;
        r2 = r19;
        r4 = r26;
        r5 = r23;
        r6 = r21;
        r0.<init>(r1, r2, r4, r5, r6);
        r7.postRunnable(r8);
    L_0x0120:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markDialogAsRead(long, int, int, int, boolean, int, boolean):void");
    }

    public /* synthetic */ void lambda$markDialogAsRead$151$MessagesController(long j, int i, int i2, boolean z, boolean z2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$QJU8iYKsFf9hbJ-ao3ktQ9SZw_c(this, j, i, i2, z, z2));
    }

    public /* synthetic */ void lambda$null$150$MessagesController(long j, int i, int i2, boolean z, boolean z2) {
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
                MessagesStorage.getInstance(this.currentAccount).setDialogUnread(dialog.id, false);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        }
        LongSparseArray longSparseArray;
        if (z2) {
            NotificationsController.getInstance(this.currentAccount).processReadMessages(null, j, 0, i2, true);
            longSparseArray = new LongSparseArray(1);
            longSparseArray.put(j2, Integer.valueOf(-1));
            NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(longSparseArray);
            return;
        }
        NotificationsController.getInstance(this.currentAccount).processReadMessages(null, j, 0, i2, false);
        longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j2, Integer.valueOf(0));
        NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(longSparseArray);
    }

    public /* synthetic */ void lambda$markDialogAsRead$153$MessagesController(long j, int i, boolean z, int i2, int i3, boolean z2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$3aeD82p4JOknKdKlPd_JqRJlTgE(this, j, i, z, i2, i3, z2));
    }

    public /* synthetic */ void lambda$null$152$MessagesController(long j, int i, boolean z, int i2, int i3, boolean z2) {
        NotificationsController.getInstance(this.currentAccount).processReadMessages(null, j, i, 0, z);
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
                MessagesStorage.getInstance(this.currentAccount).setDialogUnread(dialog.id, false);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        }
        LongSparseArray longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j, Integer.valueOf(0));
        NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(longSparseArray);
    }

    public /* synthetic */ void lambda$markDialogAsRead$154$MessagesController(long j, boolean z, int i, int i2) {
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

    public int createChat(String str, ArrayList<Integer> arrayList, String str2, int i, BaseFragment baseFragment) {
        int i2 = 0;
        if (i == 1) {
            TL_chat tL_chat = new TL_chat();
            tL_chat.id = UserConfig.getInstance(this.currentAccount).lastBroadcastId;
            tL_chat.title = str;
            tL_chat.photo = new TL_chatPhotoEmpty();
            tL_chat.participants_count = arrayList.size();
            tL_chat.date = (int) (System.currentTimeMillis() / 1000);
            tL_chat.version = 1;
            UserConfig instance = UserConfig.getInstance(this.currentAccount);
            instance.lastBroadcastId--;
            putChat(tL_chat, false);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(tL_chat);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList2, true, true);
            TL_chatFull tL_chatFull = new TL_chatFull();
            tL_chatFull.id = tL_chat.id;
            tL_chatFull.chat_photo = new TL_photoEmpty();
            tL_chatFull.notify_settings = new TL_peerNotifySettingsEmpty_layer77();
            tL_chatFull.exported_invite = new TL_chatInviteEmpty();
            tL_chatFull.participants = new TL_chatParticipants();
            ChatParticipants chatParticipants = tL_chatFull.participants;
            chatParticipants.chat_id = tL_chat.id;
            chatParticipants.admin_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tL_chatFull.participants.version = 1;
            for (i = 0; i < arrayList.size(); i++) {
                TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                tL_chatParticipant.user_id = ((Integer) arrayList.get(i)).intValue();
                tL_chatParticipant.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                tL_chatParticipant.date = (int) (System.currentTimeMillis() / 1000);
                tL_chatFull.participants.participants.add(tL_chatParticipant);
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(tL_chatFull, false);
            Message tL_messageService = new TL_messageService();
            tL_messageService.action = new TL_messageActionCreatedBroadcastList();
            int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
            tL_messageService.id = newMessageId;
            tL_messageService.local_id = newMessageId;
            tL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tL_messageService.dialog_id = AndroidUtilities.makeBroadcastId(tL_chat.id);
            tL_messageService.to_id = new TL_peerChat();
            tL_messageService.to_id.chat_id = tL_chat.id;
            tL_messageService.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            tL_messageService.random_id = 0;
            tL_messageService.flags |= 256;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            MessageObject messageObject = new MessageObject(this.currentAccount, tL_messageService, this.users, true);
            messageObject.messageOwner.send_state = 0;
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(messageObject);
            ArrayList arrayList4 = new ArrayList();
            arrayList4.add(tL_messageService);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList4, false, true, false, 0);
            updateInterfaceWithMessages(tL_messageService.dialog_id, arrayList3);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(tL_chat.id));
            return 0;
        } else if (i == 0) {
            TL_messages_createChat tL_messages_createChat = new TL_messages_createChat();
            tL_messages_createChat.title = str;
            while (i2 < arrayList.size()) {
                User user = getUser((Integer) arrayList.get(i2));
                if (user != null) {
                    tL_messages_createChat.users.add(getInputUser(user));
                }
                i2++;
            }
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_createChat, new -$$Lambda$MessagesController$BwCRJLOHKlBHZCobPU1cBtbarBc(this, baseFragment, tL_messages_createChat), 2);
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
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_createChannel, new -$$Lambda$MessagesController$0bxY0b0iZuUAKIyUkrQ28a0391w(this, baseFragment, tL_channels_createChannel), 2);
        }
    }

    public /* synthetic */ void lambda$createChat$157$MessagesController(BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$WsumHWD0PnGJZt7IOqYp5KdvEJ0(this, tL_error, baseFragment, tL_messages_createChat));
            return;
        }
        Updates updates = (Updates) tLObject;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$eAZO9_fJVDn41rVP_qwqU45S3MA(this, updates));
    }

    public /* synthetic */ void lambda$null$155$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_createChat, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$null$156$MessagesController(Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        ArrayList arrayList = updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
    }

    public /* synthetic */ void lambda$createChat$160$MessagesController(BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$1QEtWqNTIuN7FfcY0dyqyX9a0nc(this, tL_error, baseFragment, tL_channels_createChannel));
            return;
        }
        Updates updates = (Updates) tLObject;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$wCKcwOawGryKMm_2e04hmPYVRmA(this, updates));
    }

    public /* synthetic */ void lambda$null$158$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_createChannel, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$null$159$MessagesController(Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        ArrayList arrayList = updates.chats;
        if (arrayList == null || arrayList.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
    }

    public void convertToMegaGroup(Context context, int i, IntCallback intCallback) {
        TL_messages_migrateChat tL_messages_migrateChat = new TL_messages_migrateChat();
        tL_messages_migrateChat.chat_id = i;
        AlertDialog alertDialog = new AlertDialog(context, 3);
        alertDialog.setOnCancelListener(new -$$Lambda$MessagesController$SIGUP1wZNKHgQo2aKPuAgpDjucg(this, ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_migrateChat, new -$$Lambda$MessagesController$EkRPBwXKrGufP9yBw39xD6g-xjo(this, context, alertDialog, intCallback))));
        try {
            alertDialog.show();
        } catch (Exception unused) {
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$164$MessagesController(Context context, AlertDialog alertDialog, IntCallback intCallback, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$LMu-cf6mwPFEIl5BHsSN1A4tDck(context, alertDialog));
            Updates updates = (Updates) tLObject;
            processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ccOtwUqv0B2ZPbEC4t1iqZYvu2U(intCallback, updates));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$04Zo7VbxddrCSSpeIC1emxiw6QE(context, alertDialog));
    }

    static /* synthetic */ void lambda$null$161(Context context, AlertDialog alertDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    static /* synthetic */ void lambda$null$162(IntCallback intCallback, Updates updates) {
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

    static /* synthetic */ void lambda$null$163(Context context, AlertDialog alertDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            Builder builder = new Builder(context);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("ErrorOccurred", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.show().setCanceledOnTouchOutside(true);
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$165$MessagesController(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void addUsersToChannel(int i, ArrayList<InputUser> arrayList, BaseFragment baseFragment) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TL_channels_inviteToChannel tL_channels_inviteToChannel = new TL_channels_inviteToChannel();
            tL_channels_inviteToChannel.channel = getInputChannel(i);
            tL_channels_inviteToChannel.users = arrayList;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_inviteToChannel, new -$$Lambda$MessagesController$TfiNuUXCjyH-kDhosYCjp7Cg5Gk(this, baseFragment, tL_channels_inviteToChannel));
        }
    }

    public /* synthetic */ void lambda$addUsersToChannel$167$MessagesController(BaseFragment baseFragment, TL_channels_inviteToChannel tL_channels_inviteToChannel, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$JvtxNBYrSDi77SdNL-OBKU4hIZo(this, tL_error, baseFragment, tL_channels_inviteToChannel));
        } else {
            processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$166$MessagesController(TL_error tL_error, BaseFragment baseFragment, TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_inviteToChannel, Boolean.valueOf(true));
    }

    public void toogleChannelSignatures(int i, boolean z) {
        TL_channels_toggleSignatures tL_channels_toggleSignatures = new TL_channels_toggleSignatures();
        tL_channels_toggleSignatures.channel = getInputChannel(i);
        tL_channels_toggleSignatures.enabled = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_toggleSignatures, new -$$Lambda$MessagesController$aeXtWX5NT1IBK6iBhloNZ1bs-M4(this), 64);
    }

    public /* synthetic */ void lambda$toogleChannelSignatures$169$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$3BIrPkpsnU56wDIO-4y6LdEZG1o(this));
        }
    }

    public /* synthetic */ void lambda$null$168$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void toogleChannelInvitesHistory(int i, boolean z) {
        TL_channels_togglePreHistoryHidden tL_channels_togglePreHistoryHidden = new TL_channels_togglePreHistoryHidden();
        tL_channels_togglePreHistoryHidden.channel = getInputChannel(i);
        tL_channels_togglePreHistoryHidden.enabled = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_togglePreHistoryHidden, new -$$Lambda$MessagesController$YebMv5xtYdxqizkIFhqWdfrEUcI(this), 64);
    }

    public /* synthetic */ void lambda$toogleChannelInvitesHistory$171$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$MQt495ngo0PePemjE4vQFdgondE(this));
        }
    }

    public /* synthetic */ void lambda$null$170$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void updateChatAbout(int i, String str, ChatFull chatFull) {
        if (chatFull != null) {
            TL_messages_editChatAbout tL_messages_editChatAbout = new TL_messages_editChatAbout();
            tL_messages_editChatAbout.peer = getInputPeer(-i);
            tL_messages_editChatAbout.about = str;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editChatAbout, new -$$Lambda$MessagesController$7X_q4bvD4zimEhOUjsZ_PwkItoo(this, chatFull, str), 64);
        }
    }

    public /* synthetic */ void lambda$updateChatAbout$173$MessagesController(ChatFull chatFull, String str, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$HY_Xxx1aYrQjlxZz1_f8hnkFqxY(this, chatFull, str));
        }
    }

    public /* synthetic */ void lambda$null$172$MessagesController(ChatFull chatFull, String str) {
        chatFull.about = str;
        MessagesStorage.getInstance(this.currentAccount).updateChatInfo(chatFull, false);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void updateChannelUserName(int i, String str) {
        TL_channels_updateUsername tL_channels_updateUsername = new TL_channels_updateUsername();
        tL_channels_updateUsername.channel = getInputChannel(i);
        tL_channels_updateUsername.username = str;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_updateUsername, new -$$Lambda$MessagesController$Ov1XVh0U9G1mRr1zQgjy3_Hz5dQ(this, i, str), 64);
    }

    public /* synthetic */ void lambda$updateChannelUserName$175$MessagesController(int i, String str, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$a0Xdl6BGzzSwlj898G_QllCxUyU(this, i, str));
        }
    }

    public /* synthetic */ void lambda$null$174$MessagesController(int i, String str) {
        Chat chat = getChat(Integer.valueOf(i));
        if (str.length() != 0) {
            chat.flags |= 64;
        } else {
            chat.flags &= -65;
        }
        chat.username = str;
        ArrayList arrayList = new ArrayList();
        arrayList.add(chat);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList, true, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void sendBotStart(User user, String str) {
        if (user != null) {
            TL_messages_startBot tL_messages_startBot = new TL_messages_startBot();
            tL_messages_startBot.bot = getInputUser(user);
            tL_messages_startBot.peer = getInputPeer(user.id);
            tL_messages_startBot.start_param = str;
            tL_messages_startBot.random_id = Utilities.random.nextLong();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_startBot, new -$$Lambda$MessagesController$dxMathd1bP5-uSwd4Gt7ZvEOFtw(this));
        }
    }

    public /* synthetic */ void lambda$sendBotStart$176$MessagesController(TLObject tLObject, TL_error tL_error) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new -$$Lambda$MessagesController$h5a2RdtziFUaSy5oo3g6ZX4SUu4(this, isChannel, inputUser, i, baseFragment, tLObject, z, runnable));
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
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList, true, true);
                TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                tL_chatParticipant.user_id = user2.id;
                tL_chatParticipant.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                tL_chatParticipant.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                chatFull2.participants.participants.add(0, tL_chatParticipant);
                MessagesStorage.getInstance(this.currentAccount).updateChatInfo(chatFull2, true);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull2, Integer.valueOf(0), Boolean.valueOf(false), null);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public /* synthetic */ void lambda$addUserToChat$180$MessagesController(boolean z, InputUser inputUser, int i, BaseFragment baseFragment, TLObject tLObject, boolean z2, Runnable runnable, TLObject tLObject2, TL_error tL_error) {
        InputUser inputUser2 = inputUser;
        int i2 = i;
        if (z && (inputUser2 instanceof TL_inputUserSelf)) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$gHAV7k8HUui7jwavTT5ub-YwyHk(this, i));
        }
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$FzKpDwgMKXFwcQpN6PW-r4fPJ0A(this, tL_error, baseFragment, tLObject, z, z2, inputUser));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$8h8cgPEI6kzZTDRtvYPddJzervw(this, i), 1000);
        }
        if (z && (inputUser2 instanceof TL_inputUserSelf)) {
            MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, i);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$null$177$MessagesController(int i) {
        this.joiningToChannels.remove(Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$null$178$MessagesController(TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2, InputUser inputUser) {
        int i = this.currentAccount;
        Object[] objArr = new Object[1];
        z2 = z && !z2;
        objArr[0] = Boolean.valueOf(z2);
        AlertsCreator.processError(i, tL_error, baseFragment, tLObject, objArr);
        if (z && (inputUser instanceof TL_inputUserSelf)) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
        }
    }

    public /* synthetic */ void lambda$null$179$MessagesController(int i) {
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
                if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    deleteDialog((long) (-i), 0, z2);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_deleteChatUser, new -$$Lambda$MessagesController$IE4i2TewkP8XTc8wTYcnuzt-GP0(this, isChannel, inputUser, i), 64);
            } else if (chatFull instanceof TL_chatFull) {
                Object obj;
                Chat chat2 = getChat(Integer.valueOf(i));
                chat2.participants_count--;
                ArrayList arrayList = new ArrayList();
                arrayList.add(chat2);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList, true, true);
                for (i = 0; i < chatFull.participants.participants.size(); i++) {
                    if (((ChatParticipant) chatFull.participants.participants.get(i)).user_id == user.id) {
                        chatFull.participants.participants.remove(i);
                        obj = 1;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    MessagesStorage.getInstance(this.currentAccount).updateChatInfo(chatFull, true);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public /* synthetic */ void lambda$deleteUserFromChat$182$MessagesController(boolean z, InputUser inputUser, int i, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((Updates) tLObject, false);
            if (z && !(inputUser instanceof TL_inputUserSelf)) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$xTU7cEe4T3HaWaI7TTVa9Ed-zvk(this, i), 1000);
            }
        }
    }

    public /* synthetic */ void lambda$null$181$MessagesController(int i) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editTitle, new -$$Lambda$MessagesController$40Sp7FeVOZVQXMLSFubFHvar_OI8(this), 64);
            return;
        }
        Chat chat = getChat(Integer.valueOf(i));
        chat.title = str;
        ArrayList arrayList = new ArrayList();
        arrayList.add(chat);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList, true, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(16));
    }

    public /* synthetic */ void lambda$changeChatTitle$183$MessagesController(TLObject tLObject, TL_error tL_error) {
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editPhoto, new -$$Lambda$MessagesController$xhFZ44TZl-2-TmtDUCclv1zCK1g(this, fileLocation, fileLocation2), 64);
    }

    public /* synthetic */ void lambda$changeChatAvatar$184$MessagesController(FileLocation fileLocation, FileLocation fileLocation2, TLObject tLObject, TL_error tL_error) {
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
        if (UserConfig.getInstance(this.currentAccount).registeredForPush && SharedConfig.pushString.length() == 0) {
            TL_account_unregisterDevice tL_account_unregisterDevice = new TL_account_unregisterDevice();
            tL_account_unregisterDevice.token = SharedConfig.pushString;
            tL_account_unregisterDevice.token_type = 2;
            for (int i = 0; i < 3; i++) {
                UserConfig instance = UserConfig.getInstance(i);
                if (i != this.currentAccount && instance.isClientActivated()) {
                    tL_account_unregisterDevice.other_uids.add(Integer.valueOf(instance.getClientUserId()));
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_unregisterDevice, -$$Lambda$MessagesController$8PliyHoMopJhuU4eiI2hN2lPvKs.INSTANCE);
        }
    }

    public void performLogout(int i) {
        boolean z = true;
        if (i == 1) {
            unregistedPush();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_logOut(), new -$$Lambda$MessagesController$iSBguIqFt9uYneQ2bb0JRjCTxlQ(this));
        } else {
            ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
            if (i != 2) {
                z = false;
            }
            instance.cleanup(z);
        }
        UserConfig.getInstance(this.currentAccount).clearConfig();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        MessagesStorage.getInstance(this.currentAccount).cleanup(false);
        cleanup();
        ContactsController.getInstance(this.currentAccount).deleteUnknownAppAccounts();
    }

    public /* synthetic */ void lambda$performLogout$186$MessagesController(TLObject tLObject, TL_error tL_error) {
        ConnectionsManager.getInstance(this.currentAccount).cleanup(false);
    }

    public void generateUpdateMessage() {
        if (!BuildVars.DEBUG_VERSION) {
            String str = SharedConfig.lastUpdateVersion;
            if (str != null && !str.equals(BuildVars.BUILD_VERSION_STRING)) {
                TL_help_getAppChangelog tL_help_getAppChangelog = new TL_help_getAppChangelog();
                tL_help_getAppChangelog.prev_app_version = SharedConfig.lastUpdateVersion;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_getAppChangelog, new -$$Lambda$MessagesController$IayzOkIFtocnVFRbLM-8qmLr-wI(this));
            }
        }
    }

    public /* synthetic */ void lambda$generateUpdateMessage$187$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
            SharedConfig.saveConfig();
        }
        if (tLObject instanceof Updates) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public void registerForPush(String str) {
        if (!TextUtils.isEmpty(str) && !this.registeringForPush && UserConfig.getInstance(this.currentAccount).getClientUserId() != 0 && (!UserConfig.getInstance(this.currentAccount).registeredForPush || !str.equals(SharedConfig.pushString))) {
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
            tL_account_registerDevice.secret = SharedConfig.pushAuthKey;
            for (int i = 0; i < 3; i++) {
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
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_registerDevice, new -$$Lambda$MessagesController$RQIUdGcrH5D0HSeyADRBCxVETRg(this, str));
        }
    }

    public /* synthetic */ void lambda$registerForPush$189$MessagesController(String str, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("account ");
                stringBuilder.append(this.currentAccount);
                stringBuilder.append(" registered for push");
                FileLog.d(stringBuilder.toString());
            }
            UserConfig.getInstance(this.currentAccount).registeredForPush = true;
            SharedConfig.pushString = str;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$SwAlAjR_w1esOUP73MiMXDofIVc(this));
    }

    public /* synthetic */ void lambda$null$188$MessagesController() {
        this.registeringForPush = false;
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_updates_getState(), new -$$Lambda$MessagesController$cVyFjAxO3BTy-3AQHVKKSH7bs_0(this));
        }
    }

    public /* synthetic */ void lambda$loadCurrentState$190$MessagesController(TLObject tLObject, TL_error tL_error) {
        int i = 0;
        this.updatingState = false;
        if (tL_error == null) {
            TL_updates_state tL_updates_state = (TL_updates_state) tLObject;
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(tL_updates_state.date);
            MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(tL_updates_state.pts);
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(tL_updates_state.seq);
            MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(tL_updates_state.qts);
            while (i < 3) {
                processUpdatesQueue(i, 2);
                i++;
            }
            MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
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
            if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == updateSeq || MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() == updateSeq) {
                return 0;
            }
            return MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() < updateSeq ? 1 : 2;
        } else if (i == 1) {
            if (updates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastPtsValue()) {
                return 2;
            }
            return MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + updates.pts_count == updates.pts ? 0 : 1;
        } else if (i != 2) {
            return 0;
        } else {
            if (updates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastQtsValue()) {
                return 2;
            }
            return MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + updates.updates.size() == updates.pts ? 0 : 1;
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
            Collections.sort(arrayList, -$$Lambda$MessagesController$JGJclbw8cDS2wcbI-Tj2zlng-g0.INSTANCE);
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
            Collections.sort(arrayList, new -$$Lambda$MessagesController$ZJInZNgn5fe5N3COP4HchbUkZ4o(this));
        } else if (i == 1) {
            arrayList = this.updatesQueuePts;
            Collections.sort(arrayList, -$$Lambda$MessagesController$CXfuZJPIy1bgOC7pFoaz-w6qqDM.INSTANCE);
        } else if (i == 2) {
            arrayList = this.updatesQueueQts;
            Collections.sort(arrayList, -$$Lambda$MessagesController$CpLCaAXrUWrrraDtT2_grVns0kE.INSTANCE);
        } else {
            arrayList = null;
        }
        if (!(arrayList == null || arrayList.isEmpty())) {
            if (i2 == 2) {
                Updates updates = (Updates) arrayList.get(0);
                if (i == 0) {
                    MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(getUpdateSeq(updates));
                } else if (i == 1) {
                    MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(updates.pts);
                } else {
                    MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(updates.pts);
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

    public /* synthetic */ int lambda$processUpdatesQueue$192$MessagesController(Updates updates, Updates updates2) {
        return AndroidUtilities.compare(getUpdateSeq(updates), getUpdateSeq(updates2));
    }

    /* Access modifiers changed, original: protected */
    public void loadUnknownChannel(Chat chat, long j) {
        Throwable e;
        if ((chat instanceof TL_channel) && this.gettingUnknownChannels.indexOfKey(chat.id) < 0) {
            if (chat.access_hash == 0) {
                if (j != 0) {
                    MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
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
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$gfG4ToHa5Z2rjZdIfshLWaUs_Ms(this, j, chat));
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$gfG4ToHa5Z2rjZdIfshLWaUs_Ms(this, j, chat));
                }
                j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new -$$Lambda$MessagesController$gfG4ToHa5Z2rjZdIfshLWaUs_Ms(this, j, chat));
        }
    }

    public /* synthetic */ void lambda$loadUnknownChannel$195$MessagesController(long j, Chat chat, TLObject tLObject, TL_error tL_error) {
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
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
        this.gettingUnknownChannels.delete(chat.id);
    }

    public void startShortPoll(Chat chat, boolean z) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$ChpY-h3KplD8DdHCKUKN4FRi5P8(this, z, chat));
    }

    public /* synthetic */ void lambda$startShortPoll$196$MessagesController(boolean z, Chat chat) {
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
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00c5  */
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
        goto L_0x0046;
    L_0x0022:
        r8 = r7.channelsPts;
        r8 = r8.get(r3);
        if (r8 != 0) goto L_0x0043;
    L_0x002a:
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r8 = r8.getChannelPtsSync(r3);
        if (r8 == 0) goto L_0x003b;
    L_0x0036:
        r9 = r7.channelsPts;
        r9.put(r3, r8);
    L_0x003b:
        if (r8 != 0) goto L_0x0043;
    L_0x003d:
        r9 = 2;
        if (r4 == r9) goto L_0x0042;
    L_0x0040:
        if (r4 != r5) goto L_0x0043;
    L_0x0042:
        return;
    L_0x0043:
        if (r8 != 0) goto L_0x0046;
    L_0x0045:
        return;
    L_0x0046:
        if (r20 != 0) goto L_0x0066;
    L_0x0048:
        r9 = java.lang.Integer.valueOf(r16);
        r9 = r15.getChat(r9);
        if (r9 != 0) goto L_0x0061;
    L_0x0052:
        r9 = r7.currentAccount;
        r9 = org.telegram.messenger.MessagesStorage.getInstance(r9);
        r9 = r9.getChatSync(r3);
        if (r9 == 0) goto L_0x0061;
    L_0x005e:
        r15.putChat(r9, r6);
    L_0x0061:
        r9 = getInputChannel(r9);
        goto L_0x0068;
    L_0x0066:
        r9 = r20;
    L_0x0068:
        r10 = 0;
        if (r9 == 0) goto L_0x00f7;
    L_0x006c:
        r12 = r9.access_hash;
        r14 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
        if (r14 != 0) goto L_0x0074;
    L_0x0072:
        goto L_0x00f7;
    L_0x0074:
        r12 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x00a3;
    L_0x0078:
        r1 = 0;
        r10 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0094 }
        r0 = r9.getObjectSize();	 Catch:{ Exception -> 0x0094 }
        r0 = r0 + 12;
        r10.<init>(r0);	 Catch:{ Exception -> 0x0094 }
        r0 = 6;
        r10.writeInt32(r0);	 Catch:{ Exception -> 0x0092 }
        r10.writeInt32(r3);	 Catch:{ Exception -> 0x0092 }
        r10.writeInt32(r4);	 Catch:{ Exception -> 0x0092 }
        r9.serializeToStream(r10);	 Catch:{ Exception -> 0x0092 }
        goto L_0x0099;
    L_0x0092:
        r0 = move-exception;
        goto L_0x0096;
    L_0x0094:
        r0 = move-exception;
        r10 = r1;
    L_0x0096:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0099:
        r0 = r7.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0 = r0.createPendingTask(r10);
    L_0x00a3:
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
        if (r4 == r5) goto L_0x00be;
    L_0x00bd:
        goto L_0x00bf;
    L_0x00be:
        r6 = 0;
    L_0x00bf:
        r0.force = r6;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x00e1;
    L_0x00c5:
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
    L_0x00e1:
        r1 = r7.currentAccount;
        r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r9 = new org.telegram.messenger.-$$Lambda$MessagesController$x3OtyFEBR3d2oz5IdcBDlqWOR_4;
        r1 = r9;
        r2 = r15;
        r3 = r16;
        r4 = r17;
        r5 = r10;
        r1.<init>(r2, r3, r4, r5);
        r8.sendRequest(r0, r9);
        return;
    L_0x00f7:
        r2 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x0104;
    L_0x00fb:
        r2 = r7.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r2.removePendingTask(r0);
    L_0x0104:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.getChannelDifference(int, int, long, org.telegram.tgnet.TLRPC$InputChannel):void");
    }

    public /* synthetic */ void lambda$getChannelDifference$205$MessagesController(int i, int i2, long j, TLObject tLObject, TL_error tL_error) {
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
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(updates_channeldifference.users, updates_channeldifference.chats, true, true);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$28A9t6uw6zcK-zdsc5pJbUtacP4(this, updates_channeldifference));
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$MessagesController$lCXOkGLPvyvcFRmbZ7785J1HE74(this, arrayList, i, updates_channeldifference, chat, sparseArray, i2, j));
        } else if (tL_error2 != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$xuR4Dv4llnFxbNDnZ9xFUOdZuww(this, tL_error2, i));
            this.gettingDifferenceChannels.delete(i);
            if (j2 != 0) {
                MessagesStorage.getInstance(this.currentAccount).removePendingTask(j2);
            }
        }
    }

    public /* synthetic */ void lambda$null$197$MessagesController(updates_ChannelDifference updates_channeldifference) {
        putUsers(updates_channeldifference.users, false);
        putChats(updates_channeldifference.chats, false);
    }

    public /* synthetic */ void lambda$null$203$MessagesController(ArrayList arrayList, int i, updates_ChannelDifference updates_channeldifference, Chat chat, SparseArray sparseArray, int i2, long j) {
        if (!arrayList.isEmpty()) {
            SparseArray sparseArray2 = new SparseArray();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) it.next();
                long[] updateMessageStateAndId = MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, i);
                if (updateMessageStateAndId != null) {
                    sparseArray2.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray2.size() != 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$knZtwzjIKj6DQF9JLJKyqk4QKIg(this, sparseArray2));
            }
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$vDE_ZP4HWcci4r3xUMBi6xf1Wh0(this, updates_channeldifference, i, chat, sparseArray, i2, j));
    }

    public /* synthetic */ void lambda$null$198$MessagesController(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            SendMessagesHelper.getInstance(this.currentAccount).processSentMessage((int) ((long[]) sparseArray.valueAt(i))[1]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(r6), Integer.valueOf(keyAt), null, Long.valueOf(r3[0]), Long.valueOf(0), Integer.valueOf(-1));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:99:0x0193 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x018b  */
    public /* synthetic */ void lambda$null$202$MessagesController(org.telegram.tgnet.TLRPC.updates_ChannelDifference r17, int r18, org.telegram.tgnet.TLRPC.Chat r19, android.util.SparseArray r20, int r21, long r22) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = r18;
        r3 = r19;
        r4 = r22;
        r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifference;
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r8 = 0;
        r9 = 1;
        if (r6 != 0) goto L_0x00ba;
    L_0x0012:
        r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
        if (r6 == 0) goto L_0x0018;
    L_0x0016:
        goto L_0x00ba;
    L_0x0018:
        r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
        if (r6 == 0) goto L_0x01dd;
    L_0x001c:
        r6 = -r2;
        r10 = (long) r6;
        r6 = r0.dialogs_read_inbox_max;
        r12 = java.lang.Long.valueOf(r10);
        r6 = r6.get(r12);
        r6 = (java.lang.Integer) r6;
        if (r6 != 0) goto L_0x0043;
    L_0x002c:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getDialogReadMax(r8, r10);
        r6 = java.lang.Integer.valueOf(r6);
        r12 = r0.dialogs_read_inbox_max;
        r13 = java.lang.Long.valueOf(r10);
        r12.put(r13, r6);
    L_0x0043:
        r12 = r0.dialogs_read_outbox_max;
        r13 = java.lang.Long.valueOf(r10);
        r12 = r12.get(r13);
        r12 = (java.lang.Integer) r12;
        if (r12 != 0) goto L_0x0068;
    L_0x0051:
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesStorage.getInstance(r12);
        r12 = r12.getDialogReadMax(r9, r10);
        r12 = java.lang.Integer.valueOf(r12);
        r13 = r0.dialogs_read_outbox_max;
        r14 = java.lang.Long.valueOf(r10);
        r13.put(r14, r12);
    L_0x0068:
        r13 = 0;
    L_0x0069:
        r14 = r1.messages;
        r14 = r14.size();
        if (r13 >= r14) goto L_0x00aa;
    L_0x0071:
        r14 = r1.messages;
        r14 = r14.get(r13);
        r14 = (org.telegram.tgnet.TLRPC.Message) r14;
        r14.dialog_id = r10;
        r15 = r14.action;
        r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r15 != 0) goto L_0x0098;
    L_0x0081:
        if (r3 == 0) goto L_0x0087;
    L_0x0083:
        r15 = r3.left;
        if (r15 != 0) goto L_0x0098;
    L_0x0087:
        r15 = r14.out;
        if (r15 == 0) goto L_0x008d;
    L_0x008b:
        r15 = r12;
        goto L_0x008e;
    L_0x008d:
        r15 = r6;
    L_0x008e:
        r15 = r15.intValue();
        r9 = r14.id;
        if (r15 >= r9) goto L_0x0098;
    L_0x0096:
        r9 = 1;
        goto L_0x0099;
    L_0x0098:
        r9 = 0;
    L_0x0099:
        r14.unread = r9;
        if (r3 == 0) goto L_0x00a6;
    L_0x009d:
        r9 = r3.megagroup;
        if (r9 == 0) goto L_0x00a6;
    L_0x00a1:
        r9 = r14.flags;
        r9 = r9 | r7;
        r14.flags = r9;
    L_0x00a6:
        r13 = r13 + 1;
        r9 = 1;
        goto L_0x0069;
    L_0x00aa:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r6 = r1;
        r6 = (org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong) r6;
        r7 = r21;
        r3.overwriteChannel(r2, r6, r7);
        goto L_0x01dd;
    L_0x00ba:
        r6 = r1.new_messages;
        r6 = r6.isEmpty();
        if (r6 != 0) goto L_0x01bb;
    L_0x00c2:
        r6 = new android.util.LongSparseArray;
        r6.<init>();
        r9 = r1.new_messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r9);
        r9 = new java.util.ArrayList;
        r9.<init>();
        r10 = -r2;
        r10 = (long) r10;
        r12 = r0.dialogs_read_inbox_max;
        r13 = java.lang.Long.valueOf(r10);
        r12 = r12.get(r13);
        r12 = (java.lang.Integer) r12;
        if (r12 != 0) goto L_0x00f8;
    L_0x00e1:
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesStorage.getInstance(r12);
        r12 = r12.getDialogReadMax(r8, r10);
        r12 = java.lang.Integer.valueOf(r12);
        r13 = r0.dialogs_read_inbox_max;
        r14 = java.lang.Long.valueOf(r10);
        r13.put(r14, r12);
    L_0x00f8:
        r13 = r0.dialogs_read_outbox_max;
        r14 = java.lang.Long.valueOf(r10);
        r13 = r13.get(r14);
        r13 = (java.lang.Integer) r13;
        if (r13 != 0) goto L_0x011e;
    L_0x0106:
        r13 = r0.currentAccount;
        r13 = org.telegram.messenger.MessagesStorage.getInstance(r13);
        r14 = 1;
        r13 = r13.getDialogReadMax(r14, r10);
        r13 = java.lang.Integer.valueOf(r13);
        r14 = r0.dialogs_read_outbox_max;
        r15 = java.lang.Long.valueOf(r10);
        r14.put(r15, r13);
    L_0x011e:
        r14 = 0;
    L_0x011f:
        r15 = r1.new_messages;
        r15 = r15.size();
        if (r14 >= r15) goto L_0x01a1;
    L_0x0127:
        r15 = r1.new_messages;
        r15 = r15.get(r14);
        r15 = (org.telegram.tgnet.TLRPC.Message) r15;
        if (r3 == 0) goto L_0x0135;
    L_0x0131:
        r8 = r3.left;
        if (r8 != 0) goto L_0x014c;
    L_0x0135:
        r8 = r15.out;
        if (r8 == 0) goto L_0x013b;
    L_0x0139:
        r8 = r13;
        goto L_0x013c;
    L_0x013b:
        r8 = r12;
    L_0x013c:
        r8 = r8.intValue();
        r7 = r15.id;
        if (r8 >= r7) goto L_0x014c;
    L_0x0144:
        r7 = r15.action;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r7 != 0) goto L_0x014c;
    L_0x014a:
        r7 = 1;
        goto L_0x014d;
    L_0x014c:
        r7 = 0;
    L_0x014d:
        r15.unread = r7;
        if (r3 == 0) goto L_0x015d;
    L_0x0151:
        r7 = r3.megagroup;
        if (r7 == 0) goto L_0x015d;
    L_0x0155:
        r7 = r15.flags;
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r7 = r7 | r8;
        r15.flags = r7;
        goto L_0x015f;
    L_0x015d:
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x015f:
        r7 = new org.telegram.messenger.MessageObject;
        r8 = r0.currentAccount;
        r3 = r0.createdDialogIds;
        r21 = r12;
        r12 = java.lang.Long.valueOf(r10);
        r3 = r3.contains(r12);
        r12 = r20;
        r7.<init>(r8, r15, r12, r3);
        r3 = r7.isOut();
        if (r3 != 0) goto L_0x0183;
    L_0x017a:
        r3 = r7.isUnread();
        if (r3 == 0) goto L_0x0183;
    L_0x0180:
        r9.add(r7);
    L_0x0183:
        r3 = r6.get(r10);
        r3 = (java.util.ArrayList) r3;
        if (r3 != 0) goto L_0x0193;
    L_0x018b:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r6.put(r10, r3);
    L_0x0193:
        r3.add(r7);
        r14 = r14 + 1;
        r3 = r19;
        r12 = r21;
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r8 = 0;
        goto L_0x011f;
    L_0x01a1:
        r3 = new org.telegram.messenger.-$$Lambda$MessagesController$unrGLtAr3G8guGu4ob0bX3amrwg;
        r3.<init>(r0, r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r3);
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r3 = r3.getStorageQueue();
        r6 = new org.telegram.messenger.-$$Lambda$MessagesController$ljkOL0yOoU2gtEo0YqV4Xl_c3YY;
        r6.<init>(r0, r9, r1);
        r3.postRunnable(r6);
    L_0x01bb:
        r3 = r1.other_updates;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x01ce;
    L_0x01c3:
        r3 = r1.other_updates;
        r6 = r1.users;
        r7 = r1.chats;
        r8 = 1;
        r0.processUpdateArray(r3, r6, r7, r8);
        goto L_0x01cf;
    L_0x01ce:
        r8 = 1;
    L_0x01cf:
        r0.processChannelsUpdatesQueue(r2, r8);
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r6 = r1.pts;
        r3.saveChannelPts(r2, r6);
    L_0x01dd:
        r3 = r0.gettingDifferenceChannels;
        r3.delete(r2);
        r3 = r0.channelsPts;
        r6 = r1.pts;
        r3.put(r2, r6);
        r3 = r1.flags;
        r3 = r3 & 2;
        if (r3 == 0) goto L_0x01ff;
    L_0x01ef:
        r3 = r0.shortPollChannels;
        r6 = java.lang.System.currentTimeMillis();
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r6 / r8;
        r7 = (int) r6;
        r6 = r1.timeout;
        r7 = r7 + r6;
        r3.put(r2, r7);
    L_0x01ff:
        r3 = r1.isFinal;
        if (r3 != 0) goto L_0x0206;
    L_0x0203:
        r0.getChannelDifference(r2);
    L_0x0206:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x027a;
    L_0x020a:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "received channel difference with pts = ";
        r3.append(r6);
        r6 = r1.pts;
        r3.append(r6);
        r6 = " channelId = ";
        r3.append(r6);
        r3.append(r2);
        r2 = r3.toString();
        org.telegram.messenger.FileLog.d(r2);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "new_messages = ";
        r2.append(r3);
        r3 = r1.new_messages;
        r3 = r3.size();
        r2.append(r3);
        r3 = " messages = ";
        r2.append(r3);
        r3 = r1.messages;
        r3 = r3.size();
        r2.append(r3);
        r3 = " users = ";
        r2.append(r3);
        r3 = r1.users;
        r3 = r3.size();
        r2.append(r3);
        r3 = " chats = ";
        r2.append(r3);
        r3 = r1.chats;
        r3 = r3.size();
        r2.append(r3);
        r3 = " other updates = ";
        r2.append(r3);
        r1 = r1.other_updates;
        r1 = r1.size();
        r2.append(r1);
        r1 = r2.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x027a:
        r1 = 0;
        r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r3 == 0) goto L_0x0289;
    L_0x0280:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1.removePendingTask(r4);
    L_0x0289:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$null$202$MessagesController(org.telegram.tgnet.TLRPC$updates_ChannelDifference, int, org.telegram.tgnet.TLRPC$Chat, android.util.SparseArray, int, long):void");
    }

    public /* synthetic */ void lambda$null$199$MessagesController(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i));
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$201$MessagesController(ArrayList arrayList, updates_ChannelDifference updates_channeldifference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$BmJZ4Zdfo6BIVJxBYMn2TEeaaC0(this, arrayList));
        }
        MessagesStorage.getInstance(this.currentAccount).putMessages(updates_channeldifference.new_messages, true, false, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask());
    }

    public /* synthetic */ void lambda$null$200$MessagesController(ArrayList arrayList) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$null$204$MessagesController(TL_error tL_error, int i) {
        checkChannelError(tL_error.text, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0071  */
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
        if (r6 == 0) goto L_0x0071;
    L_0x0038:
        if (r6 == r4) goto L_0x0057;
    L_0x003a:
        if (r6 == r3) goto L_0x003d;
    L_0x003c:
        goto L_0x008a;
    L_0x003d:
        r6 = r5.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad;
        r1 = new java.lang.Object[r3];
        r7 = java.lang.Integer.valueOf(r7);
        r1[r2] = r7;
        r7 = java.lang.Integer.valueOf(r3);
        r1[r4] = r7;
        r6.postNotificationName(r0, r1);
        goto L_0x008a;
    L_0x0057:
        r6 = r5.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad;
        r1 = new java.lang.Object[r3];
        r7 = java.lang.Integer.valueOf(r7);
        r1[r2] = r7;
        r7 = java.lang.Integer.valueOf(r4);
        r1[r4] = r7;
        r6.postNotificationName(r0, r1);
        goto L_0x008a;
    L_0x0071:
        r6 = r5.currentAccount;
        r6 = org.telegram.messenger.NotificationCenter.getInstance(r6);
        r0 = org.telegram.messenger.NotificationCenter.chatInfoCantLoad;
        r1 = new java.lang.Object[r3];
        r7 = java.lang.Integer.valueOf(r7);
        r1[r2] = r7;
        r7 = java.lang.Integer.valueOf(r2);
        r1[r4] = r7;
        r6.postNotificationName(r0, r1);
    L_0x008a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.checkChannelError(java.lang.String, int):void");
    }

    public void getDifference() {
        getDifference(MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue(), false);
    }

    public void getDifference(int i, int i2, int i3, boolean z) {
        registerForPush(SharedConfig.pushString);
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() == 0) {
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
                tL_updates_getDifference.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
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
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_updates_getDifference, new -$$Lambda$MessagesController$TiSv3h40ZHze2ul5Mr-eNiZvHcc(this, i2, i3));
        }
    }

    public /* synthetic */ void lambda$getDifference$214$MessagesController(int i, int i2, TLObject tLObject, TL_error tL_error) {
        int i3 = 0;
        if (tL_error == null) {
            updates_Difference updates_difference = (updates_Difference) tLObject;
            if (updates_difference instanceof TL_updates_differenceTooLong) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$T4klBi4vmBoUx4jRc3cN4SM0ygE(this, updates_difference, i, i2));
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
                                i4 = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(updateChannelId);
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$uHgsA63Vu83OxWhkgJ5VeBJ4ndU(this, updates_difference));
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$MessagesController$ACN99JV2xbYO8r_lQdJPW0MPsd4(this, updates_difference, arrayList, sparseArray, sparseArray2));
            return;
        }
        this.gettingDifference = false;
        ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
    }

    public /* synthetic */ void lambda$null$206$MessagesController(updates_Difference updates_difference, int i, int i2) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        resetDialogs(true, MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), updates_difference.pts, i, i2);
    }

    public /* synthetic */ void lambda$null$207$MessagesController(updates_Difference updates_difference) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        putUsers(updates_difference.users, false);
        putChats(updates_difference.chats, false);
    }

    public /* synthetic */ void lambda$null$213$MessagesController(updates_Difference updates_difference, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2) {
        updates_Difference updates_difference2 = updates_difference;
        int i = 0;
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(updates_difference2.users, updates_difference2.chats, true, false);
        if (!arrayList.isEmpty()) {
            SparseArray sparseArray3 = new SparseArray();
            while (i < arrayList.size()) {
                TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) arrayList.get(i);
                long[] updateMessageStateAndId = MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, 0);
                if (updateMessageStateAndId != null) {
                    sparseArray3.put(tL_updateMessageID.id, updateMessageStateAndId);
                }
                i++;
            }
            if (sparseArray3.size() != 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ITmJA6kLHBukKSXRGmYt1a3rPug(this, sparseArray3));
            }
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesController$Ey750lTvkjqFS34n7sagq8Medx8(this, updates_difference2, sparseArray, sparseArray2));
    }

    public /* synthetic */ void lambda$null$208$MessagesController(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            SendMessagesHelper.getInstance(this.currentAccount).processSentMessage((int) ((long[]) sparseArray.valueAt(i))[1]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(r6), Integer.valueOf(keyAt), null, Long.valueOf(r3[0]), Long.valueOf(0), Integer.valueOf(-1));
        }
    }

    public /* synthetic */ void lambda$null$212$MessagesController(updates_Difference updates_difference, SparseArray sparseArray, SparseArray sparseArray2) {
        updates_Difference updates_difference2 = updates_difference;
        int i = 0;
        if (!(updates_difference2.new_messages.isEmpty() && updates_difference2.new_encrypted_messages.isEmpty())) {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i2 = 0; i2 < updates_difference2.new_encrypted_messages.size(); i2++) {
                ArrayList decryptMessage = SecretChatHelper.getInstance(this.currentAccount).decryptMessage((EncryptedMessage) updates_difference2.new_encrypted_messages.get(i2));
                if (!(decryptMessage == null || decryptMessage.isEmpty())) {
                    updates_difference2.new_messages.addAll(decryptMessage);
                }
            }
            ImageLoader.saveMessagesThumbs(updates_difference2.new_messages);
            ArrayList arrayList = new ArrayList();
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            for (int i3 = 0; i3 < updates_difference2.new_messages.size(); i3++) {
                Object obj;
                Message message = (Message) updates_difference2.new_messages.get(i3);
                if (message.dialog_id == 0) {
                    Peer peer = message.to_id;
                    int i4 = peer.chat_id;
                    if (i4 != 0) {
                        message.dialog_id = (long) (-i4);
                    } else {
                        if (peer.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
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
                            num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
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
                MessageObject messageObject = r9;
                MessageObject messageObject2 = new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                if (messageObject.isOut() || !messageObject.isUnread()) {
                    obj = messageObject;
                } else {
                    obj = messageObject;
                    arrayList.add(obj);
                }
                ArrayList arrayList2 = (ArrayList) longSparseArray.get(message.dialog_id);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    longSparseArray.put(message.dialog_id, arrayList2);
                }
                arrayList2.add(obj);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$7U_DZAtefOGyLnbtJ_TzAYloz70(this, longSparseArray));
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$MessagesController$CTc5p-O22dGYncyM4gxumAZNVws(this, arrayList, updates_difference2));
            SecretChatHelper.getInstance(this.currentAccount).processPendingEncMessages();
        }
        if (!updates_difference2.other_updates.isEmpty()) {
            processUpdateArray(updates_difference2.other_updates, updates_difference2.users, updates_difference2.chats, true);
        }
        if (updates_difference2 instanceof TL_updates_difference) {
            this.gettingDifference = false;
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(updates_difference2.state.seq);
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(updates_difference2.state.date);
            MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(updates_difference2.state.pts);
            MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(updates_difference2.state.qts);
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
            while (i < 3) {
                processUpdatesQueue(i, 1);
                i++;
            }
        } else if (updates_difference2 instanceof TL_updates_differenceSlice) {
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(updates_difference2.intermediate_state.date);
            MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(updates_difference2.intermediate_state.pts);
            MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(updates_difference2.intermediate_state.qts);
        } else if (updates_difference2 instanceof TL_updates_differenceEmpty) {
            this.gettingDifference = false;
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(updates_difference2.seq);
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(updates_difference2.date);
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
            while (i < 3) {
                processUpdatesQueue(i, 1);
                i++;
            }
        }
        MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("received difference with date = ");
            stringBuilder.append(MessagesStorage.getInstance(this.currentAccount).getLastDateValue());
            stringBuilder.append(" pts = ");
            stringBuilder.append(MessagesStorage.getInstance(this.currentAccount).getLastPtsValue());
            stringBuilder.append(" seq = ");
            stringBuilder.append(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue());
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

    public /* synthetic */ void lambda$null$209$MessagesController(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i));
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$211$MessagesController(ArrayList arrayList, updates_Difference updates_difference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$ecwYtn1RRyD-7_xCcWPT8y9SkzY(this, arrayList, updates_difference));
        }
        MessagesStorage.getInstance(this.currentAccount).putMessages(updates_difference.new_messages, true, false, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask());
    }

    public /* synthetic */ void lambda$null$210$MessagesController(ArrayList arrayList, updates_Difference updates_difference) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(arrayList, !(updates_difference instanceof TL_updates_differenceSlice), false, null);
    }

    public void markDialogAsUnread(long j, InputPeer inputPeer, long j2) {
        Throwable e;
        Dialog dialog = (Dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            dialog.unread_mark = true;
            if (dialog.unread_count == 0 && !isDialogMuted(j)) {
                this.unreadUnmutedDialogs++;
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
            MessagesStorage.getInstance(this.currentAccount).setDialogUnread(j, true);
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
                        j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_markDialogUnread, new -$$Lambda$MessagesController$vdUYw660VQTUH5B-NPbzLsGZ09M(this, j2));
                    }
                    j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_markDialogUnread, new -$$Lambda$MessagesController$vdUYw660VQTUH5B-NPbzLsGZ09M(this, j2));
            }
        }
    }

    public /* synthetic */ void lambda$markDialogAsUnread$215$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
    }

    public void loadUnreadDialogs() {
        if (!this.loadingUnreadDialogs && !UserConfig.getInstance(this.currentAccount).unreadDialogsLoaded) {
            this.loadingUnreadDialogs = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getDialogUnreadMarks(), new -$$Lambda$MessagesController$A7Jim0c_hwW7n1Xp7g9CGcD85b0(this));
        }
    }

    public /* synthetic */ void lambda$loadUnreadDialogs$217$MessagesController(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$DRgtzkEskOqTf7dqRkfnjux7IBI(this, tLObject));
    }

    public /* synthetic */ void lambda$null$216$MessagesController(TLObject tLObject) {
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
                    MessagesStorage.getInstance(this.currentAccount).setDialogUnread(j, true);
                    Dialog dialog = (Dialog) this.dialogs_dict.get(j);
                    if (!(dialog == null || dialog.unread_mark)) {
                        dialog.unread_mark = true;
                        if (dialog.unread_count == 0 && !isDialogMuted(j)) {
                            this.unreadUnmutedDialogs++;
                        }
                    }
                }
            }
            UserConfig.getInstance(this.currentAccount).unreadDialogsLoaded = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
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
                        MessagesStorage.getInstance(this.currentAccount).setDialogPinned(dialog.id, dialog.pinnedNum);
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
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderPinnedDialogs, new -$$Lambda$MessagesController$iDjQfJMpGMx6ueasQejBNbv1luw(this, j));
                    }
                } catch (Exception e3) {
                    e = e3;
                    nativeByteBuffer = null;
                    FileLog.e(e);
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderPinnedDialogs, new -$$Lambda$MessagesController$iDjQfJMpGMx6ueasQejBNbv1luw(this, j));
                }
                j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
            } else {
                return;
            }
        }
        tL_messages_reorderPinnedDialogs.order = arrayList;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderPinnedDialogs, new -$$Lambda$MessagesController$iDjQfJMpGMx6ueasQejBNbv1luw(this, j));
    }

    public /* synthetic */ void lambda$reorderPinnedDialogs$218$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
                    createPendingTask = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleDialogPin, new -$$Lambda$MessagesController$T-HrG7SvNN3seY_Zr_dkkRoKYHQ(this, createPendingTask));
                    MessagesStorage.getInstance(this.currentAccount).setDialogPinned(j3, dialog.pinnedNum);
                    return true;
                }
                createPendingTask = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
            } else {
                createPendingTask = j2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleDialogPin, new -$$Lambda$MessagesController$T-HrG7SvNN3seY_Zr_dkkRoKYHQ(this, createPendingTask));
        }
        MessagesStorage.getInstance(this.currentAccount).setDialogPinned(j3, dialog.pinnedNum);
        return true;
    }

    public /* synthetic */ void lambda$pinDialog$219$MessagesController(long j, TLObject tLObject, TL_error tL_error) {
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
        }
    }

    public void loadPinnedDialogs(int i, long j, ArrayList<Long> arrayList) {
        if (this.loadingPinnedDialogs.indexOfKey(i) < 0 && !UserConfig.getInstance(this.currentAccount).isPinnedDialogsLoaded(i)) {
            this.loadingPinnedDialogs.put(i, 1);
            TL_messages_getPinnedDialogs tL_messages_getPinnedDialogs = new TL_messages_getPinnedDialogs();
            tL_messages_getPinnedDialogs.folder_id = i;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPinnedDialogs, new -$$Lambda$MessagesController$5ms5SUOMLniJAFeiQgpTUwbDtSY(this, i));
        }
    }

    public /* synthetic */ void lambda$loadPinnedDialogs$222$MessagesController(int i, TLObject tLObject, TL_error tL_error) {
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$MessagesController$2xwawfxy77A7uYEfX2S3k-suXFE(this, i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs));
        }
    }

    public /* synthetic */ void lambda$null$221$MessagesController(int i, ArrayList arrayList, boolean z, TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TL_messages_dialogs tL_messages_dialogs) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$LYiV_HiXwANpNMOPEqVG43Un0Dg(this, i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs));
    }

    public /* synthetic */ void lambda$null$220$MessagesController(int i, ArrayList arrayList, boolean z, TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TL_messages_dialogs tL_messages_dialogs) {
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
                    MessagesStorage.getInstance(this.currentAccount).setDialogPinned(dialog2.id, dialog2.pinnedNum);
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        MessagesStorage.getInstance(this.currentAccount).unpinAllDialogsExceptNew(dialogs, i3);
        MessagesStorage.getInstance(this.currentAccount).putDialogs(tL_messages_dialogs, 1);
        UserConfig.getInstance(this.currentAccount).setPinnedDialogsLoaded(i3, true);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void generateJoinMessage(int i, boolean z) {
        Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i, this.currentAccount)) {
            if ((!chat.left && !chat.kicked) || z) {
                TL_messageService tL_messageService = new TL_messageService();
                tL_messageService.flags = 256;
                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                tL_messageService.id = newMessageId;
                tL_messageService.local_id = newMessageId;
                tL_messageService.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                tL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                tL_messageService.to_id = new TL_peerChannel();
                tL_messageService.to_id.channel_id = i;
                tL_messageService.dialog_id = (long) (-i);
                tL_messageService.post = true;
                tL_messageService.action = new TL_messageActionChatAddUser();
                tL_messageService.action.users.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (chat.megagroup) {
                    tL_messageService.flags |= Integer.MIN_VALUE;
                }
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(tL_messageService);
                arrayList.add(new MessageObject(this.currentAccount, tL_messageService, true));
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$MessagesController$goN3mCs2oD94EPYohjkaDpROs2E(this, arrayList));
                MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList2, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$OUR20mf5_d9gsY1zs0m8SWbYyH8(this, i, arrayList));
            }
        }
    }

    public /* synthetic */ void lambda$generateJoinMessage$224$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$-9gXGASI7A4n8RAC9ZRRZOYw_fA(this, arrayList));
    }

    public /* synthetic */ void lambda$null$223$MessagesController(ArrayList arrayList) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$generateJoinMessage$225$MessagesController(int i, ArrayList arrayList) {
        updateInterfaceWithMessages((long) (-i), arrayList);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void checkChannelInviter(int i) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$1DIYKuDA2QelETVd6XOA-sCNt2w(this, i));
    }

    public /* synthetic */ void lambda$checkChannelInviter$231$MessagesController(int i) {
        Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i, this.currentAccount) && !chat.creator) {
            TL_channels_getParticipant tL_channels_getParticipant = new TL_channels_getParticipant();
            tL_channels_getParticipant.channel = getInputChannel(i);
            tL_channels_getParticipant.user_id = new TL_inputUserSelf();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipant, new -$$Lambda$MessagesController$8UBdkj2QNJAC0y4BPGNDaiXhz9c(this, chat, i));
        }
    }

    public /* synthetic */ void lambda$null$230$MessagesController(Chat chat, int i, TLObject tLObject, TL_error tL_error) {
        TL_channels_channelParticipant tL_channels_channelParticipant = (TL_channels_channelParticipant) tLObject;
        if (tL_channels_channelParticipant != null) {
            ChannelParticipant channelParticipant = tL_channels_channelParticipant.participant;
            if ((channelParticipant instanceof TL_channelParticipantSelf) && channelParticipant.inviter_id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (!chat.megagroup || !MessagesStorage.getInstance(this.currentAccount).isMigratedChat(chat.id))) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$Nh66UA3TNkn3k9orw-Ewox9RITw(this, tL_channels_channelParticipant));
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_channels_channelParticipant.users, null, true, true);
                Message tL_messageService = new TL_messageService();
                tL_messageService.media_unread = true;
                tL_messageService.unread = true;
                tL_messageService.flags = 256;
                tL_messageService.post = true;
                if (chat.megagroup) {
                    tL_messageService.flags |= Integer.MIN_VALUE;
                }
                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                tL_messageService.id = newMessageId;
                tL_messageService.local_id = newMessageId;
                tL_messageService.date = tL_channels_channelParticipant.participant.date;
                tL_messageService.action = new TL_messageActionChatAddUser();
                tL_messageService.from_id = tL_channels_channelParticipant.participant.inviter_id;
                tL_messageService.action.users.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                tL_messageService.to_id = new TL_peerChannel();
                tL_messageService.to_id.channel_id = i;
                tL_messageService.dialog_id = (long) (-i);
                int i2 = 0;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
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
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$MessagesController$BtccFZ3tf0NlzJfs-5gQlx4EP8M(this, arrayList));
                MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList2, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$LcotUNrqwp_WpEp6G2YIbDReBNg(this, i, arrayList));
            }
        }
    }

    public /* synthetic */ void lambda$null$226$MessagesController(TL_channels_channelParticipant tL_channels_channelParticipant) {
        putUsers(tL_channels_channelParticipant.users, false);
    }

    public /* synthetic */ void lambda$null$227$MessagesController(ArrayList arrayList) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$null$228$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$w_u2qeXpmHAUSajG7KA1I1Awfes(this, arrayList));
    }

    public /* synthetic */ void lambda$null$229$MessagesController(int i, ArrayList arrayList) {
        updateInterfaceWithMessages((long) (-i), arrayList);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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

    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0795  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0730  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x076c  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0795  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0730  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x076c  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0795  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x06db  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x06f2  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x06e1  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0730  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x076c  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0795  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x050b  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x05a4  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x057a  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x057a  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x05a4  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0636  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x09da  */
    /* JADX WARNING: Missing block: B:97:0x0247, code skipped:
            if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r0.updatesStartWaitTimePts) <= 1500) goto L_0x024b;
     */
    /* JADX WARNING: Missing block: B:134:0x032c, code skipped:
            if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r0.updatesStartWaitTimeQts) > 1500) goto L_0x01d4;
     */
    /* JADX WARNING: Missing block: B:219:0x0552, code skipped:
            if (org.telegram.messenger.MessagesStorage.getInstance(r0.currentAccount).getLastSeqValue() != r1.seq_start) goto L_0x0555;
     */
    /* JADX WARNING: Missing block: B:226:0x0575, code skipped:
            if (r5 != org.telegram.messenger.MessagesStorage.getInstance(r0.currentAccount).getLastSeqValue()) goto L_0x0555;
     */
    /* JADX WARNING: Missing block: B:299:0x06e7, code skipped:
            if (r7 != null) goto L_0x06e9;
     */
    /* JADX WARNING: Missing block: B:301:0x06eb, code skipped:
            if (r9 != null) goto L_0x06ee;
     */
    /* JADX WARNING: Missing block: B:311:0x0716, code skipped:
            if (r7 != null) goto L_0x0718;
     */
    /* JADX WARNING: Missing block: B:313:0x071a, code skipped:
            if (r9 != null) goto L_0x06ee;
     */
    public void processUpdates(org.telegram.tgnet.TLRPC.Updates r25, boolean r26) {
        /*
        r24 = this;
        r0 = r24;
        r1 = r25;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateShort;
        r3 = 0;
        r4 = 0;
        r5 = 1;
        if (r2 == 0) goto L_0x0019;
    L_0x000b:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r1 = r1.update;
        r2.add(r1);
        r0.processUpdateArray(r2, r3, r3, r4);
        goto L_0x007a;
    L_0x0019:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
        r6 = " count = ";
        r7 = "add to queue";
        r10 = " ";
        if (r2 != 0) goto L_0x063c;
    L_0x0023:
        r13 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r13 == 0) goto L_0x0029;
    L_0x0027:
        goto L_0x063c;
    L_0x0029:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r2 != 0) goto L_0x007e;
    L_0x002d:
        r13 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updates;
        if (r13 == 0) goto L_0x0032;
    L_0x0031:
        goto L_0x007e;
    L_0x0032:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updatesTooLong;
        if (r2 == 0) goto L_0x0042;
    L_0x0036:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x003f;
    L_0x003a:
        r1 = "need get diff TL_updatesTooLong";
        org.telegram.messenger.FileLog.d(r1);
    L_0x003f:
        r1 = 0;
        r4 = 1;
        goto L_0x007b;
    L_0x0042:
        r2 = r1 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesSeq;
        if (r2 == 0) goto L_0x0052;
    L_0x0046:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r1 = r1.seq;
        r2.setLastSeqValue(r1);
        goto L_0x007a;
    L_0x0052:
        r2 = r1 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesPts;
        if (r2 == 0) goto L_0x007a;
    L_0x0056:
        r2 = r1.chat_id;
        if (r2 == 0) goto L_0x006f;
    L_0x005a:
        r5 = r0.channelsPts;
        r6 = r1.pts;
        r5.put(r2, r6);
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r5 = r1.chat_id;
        r1 = r1.pts;
        r2.saveChannelPts(r5, r1);
        goto L_0x007a;
    L_0x006f:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r1 = r1.pts;
        r2.setLastPtsValue(r1);
    L_0x007a:
        r1 = 0;
    L_0x007b:
        r9 = 0;
        goto L_0x0977;
    L_0x007e:
        r14 = r3;
        r13 = 0;
    L_0x0080:
        r15 = r1.chats;
        r15 = r15.size();
        if (r13 >= r15) goto L_0x00cd;
    L_0x0088:
        r15 = r1.chats;
        r15 = r15.get(r13);
        r15 = (org.telegram.tgnet.TLRPC.Chat) r15;
        r3 = r15 instanceof org.telegram.tgnet.TLRPC.TL_channel;
        if (r3 == 0) goto L_0x00c9;
    L_0x0094:
        r3 = r15.min;
        if (r3 == 0) goto L_0x00c9;
    L_0x0098:
        r3 = r15.id;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r0.getChat(r3);
        if (r3 == 0) goto L_0x00a8;
    L_0x00a4:
        r8 = r3.min;
        if (r8 == 0) goto L_0x00b7;
    L_0x00a8:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r8 = r1.chat_id;
        r3 = r3.getChatSync(r8);
        r0.putChat(r3, r5);
    L_0x00b7:
        if (r3 == 0) goto L_0x00bd;
    L_0x00b9:
        r3 = r3.min;
        if (r3 == 0) goto L_0x00c9;
    L_0x00bd:
        if (r14 != 0) goto L_0x00c4;
    L_0x00bf:
        r14 = new android.util.SparseArray;
        r14.<init>();
    L_0x00c4:
        r3 = r15.id;
        r14.put(r3, r15);
    L_0x00c9:
        r13 = r13 + 1;
        r3 = 0;
        goto L_0x0080;
    L_0x00cd:
        if (r14 == 0) goto L_0x010f;
    L_0x00cf:
        r3 = 0;
    L_0x00d0:
        r8 = r1.updates;
        r8 = r8.size();
        if (r3 >= r8) goto L_0x010f;
    L_0x00d8:
        r8 = r1.updates;
        r8 = r8.get(r3);
        r8 = (org.telegram.tgnet.TLRPC.Update) r8;
        r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r9 == 0) goto L_0x010c;
    L_0x00e4:
        r8 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r8;
        r8 = r8.message;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        r9 = r14.indexOfKey(r8);
        if (r9 < 0) goto L_0x010c;
    L_0x00f2:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x010a;
    L_0x00f6:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r9 = "need get diff because of min channel ";
        r3.append(r9);
        r3.append(r8);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x010a:
        r3 = 1;
        goto L_0x0110;
    L_0x010c:
        r3 = r3 + 1;
        goto L_0x00d0;
    L_0x010f:
        r3 = 0;
    L_0x0110:
        if (r3 != 0) goto L_0x0636;
    L_0x0112:
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r9 = r1.users;
        r13 = r1.chats;
        r8.putUsersAndChats(r9, r13, r5, r5);
        r8 = r1.updates;
        r9 = r0.updatesComparator;
        java.util.Collections.sort(r8, r9);
        r8 = r3;
        r3 = 0;
        r9 = 0;
    L_0x0129:
        r13 = r1.updates;
        r13 = r13.size();
        if (r13 <= 0) goto L_0x0532;
    L_0x0131:
        r13 = r1.updates;
        r13 = r13.get(r4);
        r13 = (org.telegram.tgnet.TLRPC.Update) r13;
        r14 = r0.getUpdateType(r13);
        if (r14 != 0) goto L_0x026b;
    L_0x013f:
        r14 = new org.telegram.tgnet.TLRPC$TL_updates;
        r14.<init>();
        r15 = r14.updates;
        r15.add(r13);
        r15 = getUpdatePts(r13);
        r14.pts = r15;
        r15 = getUpdatePtsCount(r13);
        r14.pts_count = r15;
    L_0x0155:
        r15 = r1.updates;
        r15 = r15.size();
        if (r5 >= r15) goto L_0x018b;
    L_0x015d:
        r15 = r1.updates;
        r15 = r15.get(r5);
        r15 = (org.telegram.tgnet.TLRPC.Update) r15;
        r11 = getUpdatePts(r15);
        r12 = getUpdatePtsCount(r15);
        r16 = r0.getUpdateType(r15);
        if (r16 != 0) goto L_0x018b;
    L_0x0173:
        r4 = r14.pts;
        r4 = r4 + r12;
        if (r4 != r11) goto L_0x018b;
    L_0x0178:
        r4 = r14.updates;
        r4.add(r15);
        r14.pts = r11;
        r4 = r14.pts_count;
        r4 = r4 + r12;
        r14.pts_count = r4;
        r4 = r1.updates;
        r4.remove(r5);
        r4 = 0;
        goto L_0x0155;
    L_0x018b:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastPtsValue();
        r11 = r14.pts_count;
        r4 = r4 + r11;
        r11 = r14.pts;
        if (r4 != r11) goto L_0x01e4;
    L_0x019c:
        r4 = r14.updates;
        r11 = r1.users;
        r12 = r1.chats;
        r13 = 0;
        r4 = r0.processUpdateArray(r4, r11, r12, r13);
        if (r4 != 0) goto L_0x01d7;
    L_0x01a9:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x01d3;
    L_0x01ad:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r8 = "need get diff inner TL_updates, pts: ";
        r4.append(r8);
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r8 = r8.getLastPtsValue();
        r4.append(r8);
        r4.append(r10);
        r8 = r1.seq;
        r4.append(r8);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x01d3:
        r4 = r6;
    L_0x01d4:
        r8 = 1;
        goto L_0x0267;
    L_0x01d7:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r11 = r14.pts;
        r4.setLastPtsValue(r11);
        goto L_0x0266;
    L_0x01e4:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastPtsValue();
        r11 = r14.pts;
        if (r4 == r11) goto L_0x0266;
    L_0x01f2:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0227;
    L_0x01f6:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r13);
        r11 = " need get diff, pts: ";
        r4.append(r11);
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.MessagesStorage.getInstance(r11);
        r11 = r11.getLastPtsValue();
        r4.append(r11);
        r4.append(r10);
        r11 = r14.pts;
        r4.append(r11);
        r4.append(r6);
        r11 = r14.pts_count;
        r4.append(r11);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x0227:
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x024a;
    L_0x022b:
        r11 = r0.updatesStartWaitTimePts;
        r15 = 0;
        r4 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1));
        if (r4 == 0) goto L_0x024a;
    L_0x0233:
        r4 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1));
        if (r4 == 0) goto L_0x01d3;
    L_0x0237:
        r11 = java.lang.System.currentTimeMillis();
        r4 = r6;
        r5 = r0.updatesStartWaitTimePts;
        r11 = r11 - r5;
        r5 = java.lang.Math.abs(r11);
        r11 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
        if (r13 > 0) goto L_0x01d4;
    L_0x0249:
        goto L_0x024b;
    L_0x024a:
        r4 = r6;
    L_0x024b:
        r5 = r0.updatesStartWaitTimePts;
        r11 = 0;
        r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
        if (r13 != 0) goto L_0x0259;
    L_0x0253:
        r5 = java.lang.System.currentTimeMillis();
        r0.updatesStartWaitTimePts = r5;
    L_0x0259:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0260;
    L_0x025d:
        org.telegram.messenger.FileLog.d(r7);
    L_0x0260:
        r5 = r0.updatesQueuePts;
        r5.add(r14);
        goto L_0x0267;
    L_0x0266:
        r4 = r6;
    L_0x0267:
        r18 = r7;
        goto L_0x0525;
    L_0x026b:
        r4 = r6;
        r5 = r0.getUpdateType(r13);
        r6 = 1;
        if (r5 != r6) goto L_0x0362;
    L_0x0273:
        r5 = new org.telegram.tgnet.TLRPC$TL_updates;
        r5.<init>();
        r11 = r5.updates;
        r11.add(r13);
        r11 = getUpdateQts(r13);
        r5.pts = r11;
    L_0x0283:
        r11 = r1.updates;
        r11 = r11.size();
        if (r6 >= r11) goto L_0x02af;
    L_0x028b:
        r11 = r1.updates;
        r11 = r11.get(r6);
        r11 = (org.telegram.tgnet.TLRPC.Update) r11;
        r12 = getUpdateQts(r11);
        r14 = r0.getUpdateType(r11);
        if (r14 != r6) goto L_0x02af;
    L_0x029d:
        r14 = r5.pts;
        r14 = r14 + r6;
        if (r14 != r12) goto L_0x02af;
    L_0x02a2:
        r14 = r5.updates;
        r14.add(r11);
        r5.pts = r12;
        r11 = r1.updates;
        r11.remove(r6);
        goto L_0x0283;
    L_0x02af:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastQtsValue();
        if (r6 == 0) goto L_0x034a;
    L_0x02bb:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastQtsValue();
        r11 = r5.updates;
        r11 = r11.size();
        r6 = r6 + r11;
        r11 = r5.pts;
        if (r6 != r11) goto L_0x02d2;
    L_0x02d0:
        goto L_0x034a;
    L_0x02d2:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastPtsValue();
        r11 = r5.pts;
        if (r6 == r11) goto L_0x0267;
    L_0x02e0:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x030d;
    L_0x02e4:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r11 = " need get diff, qts: ";
        r6.append(r11);
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.MessagesStorage.getInstance(r11);
        r11 = r11.getLastQtsValue();
        r6.append(r11);
        r6.append(r10);
        r11 = r5.pts;
        r6.append(r11);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.d(r6);
    L_0x030d:
        r6 = r0.gettingDifference;
        if (r6 != 0) goto L_0x032e;
    L_0x0311:
        r11 = r0.updatesStartWaitTimeQts;
        r13 = 0;
        r6 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r6 == 0) goto L_0x032e;
    L_0x0319:
        r6 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r6 == 0) goto L_0x01d4;
    L_0x031d:
        r11 = java.lang.System.currentTimeMillis();
        r13 = r0.updatesStartWaitTimeQts;
        r11 = r11 - r13;
        r11 = java.lang.Math.abs(r11);
        r13 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r6 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r6 > 0) goto L_0x01d4;
    L_0x032e:
        r11 = r0.updatesStartWaitTimeQts;
        r13 = 0;
        r6 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r6 != 0) goto L_0x033c;
    L_0x0336:
        r11 = java.lang.System.currentTimeMillis();
        r0.updatesStartWaitTimeQts = r11;
    L_0x033c:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0343;
    L_0x0340:
        org.telegram.messenger.FileLog.d(r7);
    L_0x0343:
        r6 = r0.updatesQueueQts;
        r6.add(r5);
        goto L_0x0267;
    L_0x034a:
        r6 = r5.updates;
        r9 = r1.users;
        r11 = r1.chats;
        r12 = 0;
        r0.processUpdateArray(r6, r9, r11, r12);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r5 = r5.pts;
        r6.setLastQtsValue(r5);
        r9 = 1;
        goto L_0x0267;
    L_0x0362:
        r5 = r0.getUpdateType(r13);
        r6 = 2;
        if (r5 != r6) goto L_0x0532;
    L_0x0369:
        r5 = getUpdateChannelId(r13);
        r11 = r0.channelsPts;
        r11 = r11.get(r5);
        if (r11 != 0) goto L_0x03ad;
    L_0x0375:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.MessagesStorage.getInstance(r11);
        r11 = r11.getChannelPtsSync(r5);
        if (r11 != 0) goto L_0x03a5;
    L_0x0381:
        r12 = 0;
    L_0x0382:
        r14 = r1.chats;
        r14 = r14.size();
        if (r12 >= r14) goto L_0x03ad;
    L_0x038a:
        r14 = r1.chats;
        r14 = r14.get(r12);
        r14 = (org.telegram.tgnet.TLRPC.Chat) r14;
        r15 = r14.id;
        if (r15 != r5) goto L_0x039f;
    L_0x0396:
        r18 = r7;
        r6 = 0;
        r0.loadUnknownChannel(r14, r6);
        r6 = 1;
        goto L_0x03b0;
    L_0x039f:
        r18 = r7;
        r12 = r12 + 1;
        r6 = 2;
        goto L_0x0382;
    L_0x03a5:
        r18 = r7;
        r6 = r0.channelsPts;
        r6.put(r5, r11);
        goto L_0x03af;
    L_0x03ad:
        r18 = r7;
    L_0x03af:
        r6 = 0;
    L_0x03b0:
        r7 = new org.telegram.tgnet.TLRPC$TL_updates;
        r7.<init>();
        r12 = r7.updates;
        r12.add(r13);
        r12 = getUpdatePts(r13);
        r7.pts = r12;
        r12 = getUpdatePtsCount(r13);
        r7.pts_count = r12;
    L_0x03c6:
        r12 = r1.updates;
        r12 = r12.size();
        r14 = 1;
        if (r14 >= r12) goto L_0x040a;
    L_0x03cf:
        r12 = r1.updates;
        r12 = r12.get(r14);
        r12 = (org.telegram.tgnet.TLRPC.Update) r12;
        r14 = getUpdatePts(r12);
        r16 = getUpdatePtsCount(r12);
        r15 = r0.getUpdateType(r12);
        r21 = r8;
        r8 = 2;
        if (r15 != r8) goto L_0x040c;
    L_0x03e8:
        r15 = getUpdateChannelId(r12);
        if (r5 != r15) goto L_0x040c;
    L_0x03ee:
        r15 = r7.pts;
        r15 = r15 + r16;
        if (r15 != r14) goto L_0x040c;
    L_0x03f4:
        r15 = r7.updates;
        r15.add(r12);
        r7.pts = r14;
        r12 = r7.pts_count;
        r12 = r12 + r16;
        r7.pts_count = r12;
        r12 = r1.updates;
        r14 = 1;
        r12.remove(r14);
        r8 = r21;
        goto L_0x03c6;
    L_0x040a:
        r21 = r8;
    L_0x040c:
        if (r6 != 0) goto L_0x050b;
    L_0x040e:
        r6 = r7.pts_count;
        r6 = r6 + r11;
        r8 = r7.pts;
        if (r6 != r8) goto L_0x046a;
    L_0x0415:
        r6 = r7.updates;
        r8 = r1.users;
        r11 = r1.chats;
        r12 = 0;
        r6 = r0.processUpdateArray(r6, r8, r11, r12);
        if (r6 != 0) goto L_0x0456;
    L_0x0422:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x043a;
    L_0x0426:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need get channel diff inner TL_updates, channel_id = ";
        r6.append(r7);
        r6.append(r5);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.d(r6);
    L_0x043a:
        if (r3 != 0) goto L_0x0443;
    L_0x043c:
        r3 = new java.util.ArrayList;
        r3.<init>();
        goto L_0x0523;
    L_0x0443:
        r6 = java.lang.Integer.valueOf(r5);
        r6 = r3.contains(r6);
        if (r6 != 0) goto L_0x0523;
    L_0x044d:
        r5 = java.lang.Integer.valueOf(r5);
        r3.add(r5);
        goto L_0x0523;
    L_0x0456:
        r6 = r0.channelsPts;
        r8 = r7.pts;
        r6.put(r5, r8);
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r7 = r7.pts;
        r6.saveChannelPts(r5, r7);
        goto L_0x0523;
    L_0x046a:
        if (r11 == r8) goto L_0x0523;
    L_0x046c:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x049f;
    L_0x0470:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r8 = " need get channel diff, pts: ";
        r6.append(r8);
        r6.append(r11);
        r6.append(r10);
        r8 = r7.pts;
        r6.append(r8);
        r6.append(r4);
        r8 = r7.pts_count;
        r6.append(r8);
        r8 = " channelId = ";
        r6.append(r8);
        r6.append(r5);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.d(r6);
    L_0x049f:
        r6 = r0.updatesStartWaitTimeChannels;
        r11 = r6.get(r5);
        r6 = r0.gettingDifferenceChannels;
        r6 = r6.get(r5);
        if (r6 != 0) goto L_0x04dd;
    L_0x04ad:
        r13 = 0;
        r6 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r6 == 0) goto L_0x04dd;
    L_0x04b3:
        r13 = java.lang.System.currentTimeMillis();
        r13 = r13 - r11;
        r13 = java.lang.Math.abs(r13);
        r16 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r6 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r6 > 0) goto L_0x04c3;
    L_0x04c2:
        goto L_0x04dd;
    L_0x04c3:
        if (r3 != 0) goto L_0x04cb;
    L_0x04c5:
        r3 = new java.util.ArrayList;
        r3.<init>();
        goto L_0x0523;
    L_0x04cb:
        r6 = java.lang.Integer.valueOf(r5);
        r6 = r3.contains(r6);
        if (r6 != 0) goto L_0x0523;
    L_0x04d5:
        r5 = java.lang.Integer.valueOf(r5);
        r3.add(r5);
        goto L_0x0523;
    L_0x04dd:
        r13 = 0;
        r6 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r6 != 0) goto L_0x04ec;
    L_0x04e3:
        r6 = r0.updatesStartWaitTimeChannels;
        r11 = java.lang.System.currentTimeMillis();
        r6.put(r5, r11);
    L_0x04ec:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x04f3;
    L_0x04f0:
        org.telegram.messenger.FileLog.d(r18);
    L_0x04f3:
        r6 = r0.updatesQueueChannels;
        r6 = r6.get(r5);
        r6 = (java.util.ArrayList) r6;
        if (r6 != 0) goto L_0x0507;
    L_0x04fd:
        r6 = new java.util.ArrayList;
        r6.<init>();
        r8 = r0.updatesQueueChannels;
        r8.put(r5, r6);
    L_0x0507:
        r6.add(r7);
        goto L_0x0523;
    L_0x050b:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0523;
    L_0x050f:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need load unknown channel = ";
        r6.append(r7);
        r6.append(r5);
        r5 = r6.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0523:
        r8 = r21;
    L_0x0525:
        r5 = r1.updates;
        r6 = 0;
        r5.remove(r6);
        r6 = r4;
        r7 = r18;
        r4 = 0;
        r5 = 1;
        goto L_0x0129;
    L_0x0532:
        r21 = r8;
        if (r2 == 0) goto L_0x0559;
    L_0x0536:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r5 = 1;
        r4 = r4 + r5;
        r5 = r1.seq_start;
        if (r4 == r5) goto L_0x0557;
    L_0x0546:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r5 = r1.seq_start;
        if (r4 != r5) goto L_0x0555;
    L_0x0554:
        goto L_0x0557;
    L_0x0555:
        r4 = 0;
        goto L_0x0578;
    L_0x0557:
        r4 = 1;
        goto L_0x0578;
    L_0x0559:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r5 = 1;
        r4 = r4 + r5;
        r5 = r1.seq;
        if (r4 == r5) goto L_0x0557;
    L_0x0569:
        if (r5 == 0) goto L_0x0557;
    L_0x056b:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        if (r5 != r4) goto L_0x0555;
    L_0x0577:
        goto L_0x0557;
    L_0x0578:
        if (r4 == 0) goto L_0x05a4;
    L_0x057a:
        r2 = r1.updates;
        r4 = r1.users;
        r5 = r1.chats;
        r6 = 0;
        r0.processUpdateArray(r2, r4, r5, r6);
        r2 = r1.seq;
        if (r2 == 0) goto L_0x0633;
    L_0x0588:
        r2 = r1.date;
        if (r2 == 0) goto L_0x0597;
    L_0x058c:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r4 = r1.date;
        r2.setLastDateValue(r4);
    L_0x0597:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r1 = r1.seq;
        r2.setLastSeqValue(r1);
        goto L_0x0633;
    L_0x05a4:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x05f7;
    L_0x05a8:
        if (r2 == 0) goto L_0x05d1;
    L_0x05aa:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "need get diff TL_updatesCombined, seq: ";
        r2.append(r4);
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r2.append(r4);
        r2.append(r10);
        r4 = r1.seq_start;
        r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
        goto L_0x05f7;
    L_0x05d1:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "need get diff TL_updates, seq: ";
        r2.append(r4);
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r2.append(r4);
        r2.append(r10);
        r4 = r1.seq;
        r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x05f7:
        r2 = r0.gettingDifference;
        if (r2 != 0) goto L_0x0617;
    L_0x05fb:
        r4 = r0.updatesStartWaitTimeSeq;
        r6 = 0;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 == 0) goto L_0x0617;
    L_0x0603:
        r4 = java.lang.System.currentTimeMillis();
        r6 = r0.updatesStartWaitTimeSeq;
        r4 = r4 - r6;
        r4 = java.lang.Math.abs(r4);
        r6 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 > 0) goto L_0x0615;
    L_0x0614:
        goto L_0x0617;
    L_0x0615:
        r4 = 1;
        goto L_0x0639;
    L_0x0617:
        r4 = r0.updatesStartWaitTimeSeq;
        r6 = 0;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 != 0) goto L_0x0625;
    L_0x061f:
        r4 = java.lang.System.currentTimeMillis();
        r0.updatesStartWaitTimeSeq = r4;
    L_0x0625:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x062e;
    L_0x0629:
        r2 = "add TL_updates/Combined to queue";
        org.telegram.messenger.FileLog.d(r2);
    L_0x062e:
        r2 = r0.updatesQueueSeq;
        r2.add(r1);
    L_0x0633:
        r4 = r21;
        goto L_0x0639;
    L_0x0636:
        r4 = r3;
        r3 = 0;
        r9 = 0;
    L_0x0639:
        r1 = 0;
        goto L_0x0977;
    L_0x063c:
        r4 = r6;
        r18 = r7;
        if (r2 == 0) goto L_0x0644;
    L_0x0641:
        r2 = r1.from_id;
        goto L_0x0646;
    L_0x0644:
        r2 = r1.user_id;
    L_0x0646:
        r3 = java.lang.Integer.valueOf(r2);
        r3 = r0.getUser(r3);
        if (r3 == 0) goto L_0x0654;
    L_0x0650:
        r5 = r3.min;
        if (r5 == 0) goto L_0x0669;
    L_0x0654:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r3 = r3.getUserSync(r2);
        if (r3 == 0) goto L_0x0665;
    L_0x0660:
        r5 = r3.min;
        if (r5 == 0) goto L_0x0665;
    L_0x0664:
        r3 = 0;
    L_0x0665:
        r5 = 1;
        r0.putUser(r3, r5);
    L_0x0669:
        r5 = r1.fwd_from;
        if (r5 == 0) goto L_0x06b7;
    L_0x066d:
        r5 = r5.from_id;
        if (r5 == 0) goto L_0x0690;
    L_0x0671:
        r5 = java.lang.Integer.valueOf(r5);
        r5 = r0.getUser(r5);
        if (r5 != 0) goto L_0x068d;
    L_0x067b:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r6 = r1.fwd_from;
        r6 = r6.from_id;
        r5 = r5.getUserSync(r6);
        r6 = 1;
        r0.putUser(r5, r6);
    L_0x068d:
        r6 = r5;
        r5 = 1;
        goto L_0x0692;
    L_0x0690:
        r5 = 0;
        r6 = 0;
    L_0x0692:
        r7 = r1.fwd_from;
        r7 = r7.channel_id;
        if (r7 == 0) goto L_0x06b9;
    L_0x0698:
        r5 = java.lang.Integer.valueOf(r7);
        r5 = r0.getChat(r5);
        if (r5 != 0) goto L_0x06b4;
    L_0x06a2:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r7 = r1.fwd_from;
        r7 = r7.channel_id;
        r5 = r5.getChatSync(r7);
        r7 = 1;
        r0.putChat(r5, r7);
    L_0x06b4:
        r7 = r5;
        r5 = 1;
        goto L_0x06ba;
    L_0x06b7:
        r5 = 0;
        r6 = 0;
    L_0x06b9:
        r7 = 0;
    L_0x06ba:
        r8 = r1.via_bot_id;
        if (r8 == 0) goto L_0x06db;
    L_0x06be:
        r8 = java.lang.Integer.valueOf(r8);
        r8 = r0.getUser(r8);
        if (r8 != 0) goto L_0x06d8;
    L_0x06c8:
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r9 = r1.via_bot_id;
        r8 = r8.getUserSync(r9);
        r9 = 1;
        r0.putUser(r8, r9);
    L_0x06d8:
        r9 = r8;
        r8 = 1;
        goto L_0x06dd;
    L_0x06db:
        r8 = 0;
        r9 = 0;
    L_0x06dd:
        r11 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r11 == 0) goto L_0x06f2;
    L_0x06e1:
        if (r3 == 0) goto L_0x06f0;
    L_0x06e3:
        if (r5 == 0) goto L_0x06e9;
    L_0x06e5:
        if (r6 != 0) goto L_0x06e9;
    L_0x06e7:
        if (r7 == 0) goto L_0x06f0;
    L_0x06e9:
        if (r8 == 0) goto L_0x06ee;
    L_0x06eb:
        if (r9 != 0) goto L_0x06ee;
    L_0x06ed:
        goto L_0x06f0;
    L_0x06ee:
        r5 = 0;
        goto L_0x071d;
    L_0x06f0:
        r5 = 1;
        goto L_0x071d;
    L_0x06f2:
        r12 = r1.chat_id;
        r12 = java.lang.Integer.valueOf(r12);
        r12 = r0.getChat(r12);
        if (r12 != 0) goto L_0x070e;
    L_0x06fe:
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesStorage.getInstance(r12);
        r13 = r1.chat_id;
        r12 = r12.getChatSync(r13);
        r13 = 1;
        r0.putChat(r12, r13);
    L_0x070e:
        if (r12 == 0) goto L_0x06f0;
    L_0x0710:
        if (r3 == 0) goto L_0x06f0;
    L_0x0712:
        if (r5 == 0) goto L_0x0718;
    L_0x0714:
        if (r6 != 0) goto L_0x0718;
    L_0x0716:
        if (r7 == 0) goto L_0x06f0;
    L_0x0718:
        if (r8 == 0) goto L_0x06ee;
    L_0x071a:
        if (r9 != 0) goto L_0x06ee;
    L_0x071c:
        goto L_0x06f0;
    L_0x071d:
        if (r5 != 0) goto L_0x076a;
    L_0x071f:
        r6 = r1.entities;
        r6 = r6.isEmpty();
        if (r6 != 0) goto L_0x076a;
    L_0x0727:
        r6 = 0;
    L_0x0728:
        r7 = r1.entities;
        r7 = r7.size();
        if (r6 >= r7) goto L_0x076a;
    L_0x0730:
        r7 = r1.entities;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.MessageEntity) r7;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r8 == 0) goto L_0x0767;
    L_0x073c:
        r7 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r7;
        r7 = r7.user_id;
        r8 = java.lang.Integer.valueOf(r7);
        r8 = r0.getUser(r8);
        if (r8 == 0) goto L_0x074e;
    L_0x074a:
        r8 = r8.min;
        if (r8 == 0) goto L_0x0767;
    L_0x074e:
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r7 = r8.getUserSync(r7);
        if (r7 == 0) goto L_0x075f;
    L_0x075a:
        r8 = r7.min;
        if (r8 == 0) goto L_0x075f;
    L_0x075e:
        r7 = 0;
    L_0x075f:
        if (r7 != 0) goto L_0x0763;
    L_0x0761:
        r5 = 1;
        goto L_0x076a;
    L_0x0763:
        r7 = 1;
        r0.putUser(r3, r7);
    L_0x0767:
        r6 = r6 + 1;
        goto L_0x0728;
    L_0x076a:
        if (r3 == 0) goto L_0x078f;
    L_0x076c:
        r6 = r3.status;
        if (r6 == 0) goto L_0x078f;
    L_0x0770:
        r6 = r6.expires;
        if (r6 > 0) goto L_0x078f;
    L_0x0774:
        r6 = r0.onlinePrivacy;
        r3 = r3.id;
        r3 = java.lang.Integer.valueOf(r3);
        r7 = r0.currentAccount;
        r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7);
        r7 = r7.getCurrentTime();
        r7 = java.lang.Integer.valueOf(r7);
        r6.put(r3, r7);
        r3 = 1;
        goto L_0x0790;
    L_0x078f:
        r3 = 0;
    L_0x0790:
        if (r5 == 0) goto L_0x0795;
    L_0x0792:
        r4 = 1;
        goto L_0x0973;
    L_0x0795:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getLastPtsValue();
        r6 = r1.pts_count;
        r5 = r5 + r6;
        r6 = r1.pts;
        if (r5 != r6) goto L_0x08fa;
    L_0x07a6:
        r4 = new org.telegram.tgnet.TLRPC$TL_message;
        r4.<init>();
        r5 = r1.id;
        r4.id = r5;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getClientUserId();
        if (r11 == 0) goto L_0x07d3;
    L_0x07bb:
        r6 = r1.out;
        if (r6 == 0) goto L_0x07c2;
    L_0x07bf:
        r4.from_id = r5;
        goto L_0x07c4;
    L_0x07c2:
        r4.from_id = r2;
    L_0x07c4:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r6.<init>();
        r4.to_id = r6;
        r6 = r4.to_id;
        r6.user_id = r2;
        r6 = (long) r2;
        r4.dialog_id = r6;
        goto L_0x07e6;
    L_0x07d3:
        r4.from_id = r2;
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChat;
        r6.<init>();
        r4.to_id = r6;
        r6 = r4.to_id;
        r7 = r1.chat_id;
        r6.chat_id = r7;
        r6 = -r7;
        r6 = (long) r6;
        r4.dialog_id = r6;
    L_0x07e6:
        r6 = r1.fwd_from;
        r4.fwd_from = r6;
        r6 = r1.silent;
        r4.silent = r6;
        r6 = r1.out;
        r4.out = r6;
        r6 = r1.mentioned;
        r4.mentioned = r6;
        r6 = r1.media_unread;
        r4.media_unread = r6;
        r6 = r1.entities;
        r4.entities = r6;
        r6 = r1.message;
        r4.message = r6;
        r6 = r1.date;
        r4.date = r6;
        r6 = r1.via_bot_id;
        r4.via_bot_id = r6;
        r6 = r1.flags;
        r6 = r6 | 256;
        r4.flags = r6;
        r6 = r1.reply_to_msg_id;
        r4.reply_to_msg_id = r6;
        r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r6.<init>();
        r4.media = r6;
        r6 = r4.out;
        if (r6 == 0) goto L_0x0822;
    L_0x081f:
        r6 = r0.dialogs_read_outbox_max;
        goto L_0x0824;
    L_0x0822:
        r6 = r0.dialogs_read_inbox_max;
    L_0x0824:
        r7 = r4.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r7 = r6.get(r7);
        r7 = (java.lang.Integer) r7;
        if (r7 != 0) goto L_0x084d;
    L_0x0832:
        r7 = r0.currentAccount;
        r7 = org.telegram.messenger.MessagesStorage.getInstance(r7);
        r8 = r4.out;
        r9 = r4.dialog_id;
        r7 = r7.getDialogReadMax(r8, r9);
        r7 = java.lang.Integer.valueOf(r7);
        r8 = r4.dialog_id;
        r8 = java.lang.Long.valueOf(r8);
        r6.put(r8, r7);
    L_0x084d:
        r6 = r7.intValue();
        r7 = r4.id;
        if (r6 >= r7) goto L_0x0857;
    L_0x0855:
        r6 = 1;
        goto L_0x0858;
    L_0x0857:
        r6 = 0;
    L_0x0858:
        r4.unread = r6;
        r6 = r4.dialog_id;
        r8 = (long) r5;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x086a;
    L_0x0861:
        r5 = 0;
        r4.unread = r5;
        r4.media_unread = r5;
        r6 = 1;
        r4.out = r6;
        goto L_0x086b;
    L_0x086a:
        r6 = 1;
    L_0x086b:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r7 = r1.pts;
        r5.setLastPtsValue(r7);
        r5 = new org.telegram.messenger.MessageObject;
        r7 = r0.currentAccount;
        r8 = r0.createdDialogIds;
        r9 = r4.dialog_id;
        r9 = java.lang.Long.valueOf(r9);
        r8 = r8.contains(r9);
        r5.<init>(r7, r4, r8);
        r7 = new java.util.ArrayList;
        r7.<init>();
        r7.add(r5);
        r8 = new java.util.ArrayList;
        r8.<init>();
        r8.add(r4);
        if (r11 == 0) goto L_0x08b8;
    L_0x089b:
        r4 = r1.out;
        if (r4 != 0) goto L_0x08a9;
    L_0x089f:
        r1 = r1.user_id;
        r9 = (long) r1;
        r1 = r0.updatePrintingUsersWithNewMessages(r9, r7);
        if (r1 == 0) goto L_0x08a9;
    L_0x08a8:
        goto L_0x08aa;
    L_0x08a9:
        r6 = 0;
    L_0x08aa:
        if (r6 == 0) goto L_0x08af;
    L_0x08ac:
        r24.updatePrintingStrings();
    L_0x08af:
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$fPbr3UYq21LGzjTkEfox5kOy2UM;
        r1.<init>(r0, r6, r2, r7);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
        goto L_0x08cd;
    L_0x08b8:
        r2 = r1.chat_id;
        r2 = -r2;
        r9 = (long) r2;
        r2 = r0.updatePrintingUsersWithNewMessages(r9, r7);
        if (r2 == 0) goto L_0x08c5;
    L_0x08c2:
        r24.updatePrintingStrings();
    L_0x08c5:
        r4 = new org.telegram.messenger.-$$Lambda$MessagesController$s0GgcDgh5GZ9Riv31NjWgQrzb_w;
        r4.<init>(r0, r2, r1, r7);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x08cd:
        r1 = r5.isOut();
        if (r1 != 0) goto L_0x08e5;
    L_0x08d3:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1 = r1.getStorageQueue();
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$iAn5VSEQcHcofX2azl7-Y4mcgGM;
        r2.<init>(r0, r7);
        r1.postRunnable(r2);
    L_0x08e5:
        r1 = r0.currentAccount;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r20 = 0;
        r21 = 1;
        r22 = 0;
        r23 = 0;
        r19 = r8;
        r18.putMessages(r19, r20, r21, r22, r23);
        goto L_0x0972;
    L_0x08fa:
        r6 = 1;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r2 = r2.getLastPtsValue();
        r5 = r1.pts;
        if (r2 == r5) goto L_0x0972;
    L_0x0909:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x093b;
    L_0x090d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r5 = "need get diff short message, pts: ";
        r2.append(r5);
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getLastPtsValue();
        r2.append(r5);
        r2.append(r10);
        r5 = r1.pts;
        r2.append(r5);
        r2.append(r4);
        r4 = r1.pts_count;
        r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x093b:
        r2 = r0.gettingDifference;
        if (r2 != 0) goto L_0x0958;
    L_0x093f:
        r4 = r0.updatesStartWaitTimePts;
        r7 = 0;
        r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
        if (r2 == 0) goto L_0x0958;
    L_0x0947:
        r4 = java.lang.System.currentTimeMillis();
        r7 = r0.updatesStartWaitTimePts;
        r4 = r4 - r7;
        r4 = java.lang.Math.abs(r4);
        r7 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
        if (r2 > 0) goto L_0x0792;
    L_0x0958:
        r4 = r0.updatesStartWaitTimePts;
        r6 = 0;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 != 0) goto L_0x0966;
    L_0x0960:
        r4 = java.lang.System.currentTimeMillis();
        r0.updatesStartWaitTimePts = r4;
    L_0x0966:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x096d;
    L_0x096a:
        org.telegram.messenger.FileLog.d(r18);
    L_0x096d:
        r2 = r0.updatesQueuePts;
        r2.add(r1);
    L_0x0972:
        r4 = 0;
    L_0x0973:
        r1 = r3;
        r3 = 0;
        goto L_0x007b;
    L_0x0977:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.SecretChatHelper.getInstance(r2);
        r2.processPendingEncMessages();
        if (r26 != 0) goto L_0x09ba;
    L_0x0982:
        r2 = 0;
    L_0x0983:
        r5 = r0.updatesQueueChannels;
        r5 = r5.size();
        if (r2 >= r5) goto L_0x09a9;
    L_0x098b:
        r5 = r0.updatesQueueChannels;
        r5 = r5.keyAt(r2);
        if (r3 == 0) goto L_0x09a2;
    L_0x0993:
        r6 = java.lang.Integer.valueOf(r5);
        r6 = r3.contains(r6);
        if (r6 == 0) goto L_0x09a2;
    L_0x099d:
        r0.getChannelDifference(r5);
        r13 = 0;
        goto L_0x09a6;
    L_0x09a2:
        r13 = 0;
        r0.processChannelsUpdatesQueue(r5, r13);
    L_0x09a6:
        r2 = r2 + 1;
        goto L_0x0983;
    L_0x09a9:
        r13 = 0;
        if (r4 == 0) goto L_0x09b0;
    L_0x09ac:
        r24.getDifference();
        goto L_0x09ba;
    L_0x09b0:
        r2 = 0;
    L_0x09b1:
        r3 = 3;
        if (r2 >= r3) goto L_0x09ba;
    L_0x09b4:
        r0.processUpdatesQueue(r2, r13);
        r2 = r2 + 1;
        goto L_0x09b1;
    L_0x09ba:
        if (r9 == 0) goto L_0x09d8;
    L_0x09bc:
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_receivedQueue;
        r2.<init>();
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r3 = r3.getLastQtsValue();
        r2.max_qts = r3;
        r3 = r0.currentAccount;
        r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3);
        r4 = org.telegram.messenger.-$$Lambda$MessagesController$9PuTSEkyzwdd31UYsaKLfGdsGkM.INSTANCE;
        r3.sendRequest(r2, r4);
    L_0x09d8:
        if (r1 == 0) goto L_0x09e2;
    L_0x09da:
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$U7UNWGF7e0N8D-YCbxsC0l6X5Iw;
        r1.<init>(r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x09e2:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r2 = r2.getLastSeqValue();
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r3 = r3.getLastPtsValue();
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastDateValue();
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getLastQtsValue();
        r1.saveDiffParams(r2, r3, r4, r5);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    public /* synthetic */ void lambda$processUpdates$232$MessagesController(boolean z, int i, ArrayList arrayList) {
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
        }
        updateInterfaceWithMessages((long) i, arrayList);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdates$233$MessagesController(boolean z, Updates updates, ArrayList arrayList) {
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
        }
        updateInterfaceWithMessages((long) (-updates.chat_id), arrayList);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$null$234$MessagesController(ArrayList arrayList) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$processUpdates$235$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$XG76p4T0ljFB2rIL7TEvl_qU3ss(this, arrayList));
    }

    public /* synthetic */ void lambda$processUpdates$237$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    /* JADX WARNING: Removed duplicated region for block: B:706:0x102e  */
    /* JADX WARNING: Removed duplicated region for block: B:853:0x1014 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x1055  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0fcf  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x11ad  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x11a7  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x11c5  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x11b9  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x116c  */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x1163  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x119c  */
    /* JADX WARNING: Removed duplicated region for block: B:769:0x1198  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x11a7  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x11ad  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x11b9  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x11c5  */
    /* JADX WARNING: Removed duplicated region for block: B:784:0x11cf  */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0f6f  */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x1076  */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x10ea  */
    /* JADX WARNING: Removed duplicated region for block: B:736:0x10e5  */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x1109  */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x1106  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x113a  */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x111b  */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x1163  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x116c  */
    /* JADX WARNING: Removed duplicated region for block: B:769:0x1198  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x119c  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x11ad  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x11a7  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x11c5  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x11b9  */
    /* JADX WARNING: Removed duplicated region for block: B:784:0x11cf  */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0e8e  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0efa  */
    /* JADX WARNING: Removed duplicated region for block: B:626:0x0ef5  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0f6f  */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x1076  */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:736:0x10e5  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x10ea  */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x1106  */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x1109  */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x111b  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x113a  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x116c  */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x1163  */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x119c  */
    /* JADX WARNING: Removed duplicated region for block: B:769:0x1198  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x11a7  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x11ad  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x11b9  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x11c5  */
    /* JADX WARNING: Removed duplicated region for block: B:784:0x11cf  */
    /* JADX WARNING: Missing block: B:685:0x0fe3, code skipped:
            if (r3.min != false) goto L_0x0feb;
     */
    /* JADX WARNING: Missing block: B:827:0x1334, code skipped:
            if (r1 != null) goto L_0x1339;
     */
    public boolean processUpdateArray(java.util.ArrayList<org.telegram.tgnet.TLRPC.Update> r49, java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r50, java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat> r51, boolean r52) {
        /*
        r48 = this;
        r11 = r48;
        r0 = r50;
        r1 = r51;
        r2 = r49.isEmpty();
        r12 = 1;
        if (r2 == 0) goto L_0x001a;
    L_0x000d:
        if (r0 != 0) goto L_0x0011;
    L_0x000f:
        if (r1 == 0) goto L_0x0019;
    L_0x0011:
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$ez4IR3lOLDGJ8ytrBmZ7ykTpLLU;
        r2.<init>(r11, r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x0019:
        return r12;
    L_0x001a:
        r2 = java.lang.System.currentTimeMillis();
        if (r0 == 0) goto L_0x0040;
    L_0x0020:
        r4 = new java.util.concurrent.ConcurrentHashMap;
        r4.<init>();
        r5 = r50.size();
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
        r5 = 1;
        goto L_0x0043;
    L_0x0040:
        r4 = r11.users;
        r5 = 0;
    L_0x0043:
        if (r1 == 0) goto L_0x0065;
    L_0x0045:
        r6 = new java.util.concurrent.ConcurrentHashMap;
        r6.<init>();
        r7 = r51.size();
        r8 = 0;
    L_0x004f:
        if (r8 >= r7) goto L_0x0063;
    L_0x0051:
        r9 = r1.get(r8);
        r9 = (org.telegram.tgnet.TLRPC.Chat) r9;
        r10 = r9.id;
        r10 = java.lang.Integer.valueOf(r10);
        r6.put(r10, r9);
        r8 = r8 + 1;
        goto L_0x004f;
    L_0x0063:
        r14 = r6;
        goto L_0x0069;
    L_0x0065:
        r5 = r11.chats;
        r14 = r5;
        r5 = 0;
    L_0x0069:
        if (r52 == 0) goto L_0x006d;
    L_0x006b:
        r15 = 0;
        goto L_0x006e;
    L_0x006d:
        r15 = r5;
    L_0x006e:
        if (r0 != 0) goto L_0x0072;
    L_0x0070:
        if (r1 == 0) goto L_0x007a;
    L_0x0072:
        r5 = new org.telegram.messenger.-$$Lambda$MessagesController$geLuQrjkrfEFbaaNVcTD1Sit87o;
        r5.<init>(r11, r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x007a:
        r0 = r49.size();
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r16 = 0;
        r18 = 0;
        r23 = 0;
        r24 = 0;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r31 = 0;
        r32 = 0;
    L_0x009d:
        if (r10 >= r0) goto L_0x1205;
    L_0x009f:
        r1 = r49;
        r17 = r1.get(r10);
        r13 = r17;
        r13 = (org.telegram.tgnet.TLRPC.Update) r13;
        r17 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r17 == 0) goto L_0x00c4;
    L_0x00ad:
        r51 = r0;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "process update ";
        r0.append(r1);
        r0.append(r13);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x00c6;
    L_0x00c4:
        r51 = r0;
    L_0x00c6:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage;
        r1 = " channelId = ";
        if (r0 != 0) goto L_0x0ed6;
    L_0x00cc:
        r20 = r6;
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r6 == 0) goto L_0x00f6;
    L_0x00d2:
        r35 = r5;
        r38 = r10;
        r10 = r23;
        r36 = r24;
        r23 = r26;
        r45 = r32;
        r24 = r15;
        r26 = r25;
        r25 = r27;
        r27 = r9;
        r9 = r8;
        r46 = r28;
        r28 = r7;
        r47 = r20;
        r20 = r14;
        r14 = r2;
        r2 = r47;
        r3 = r46;
        goto L_0x0ef3;
    L_0x00f6:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents;
        if (r0 == 0) goto L_0x0136;
    L_0x00fa:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents) r13;
        if (r5 != 0) goto L_0x0103;
    L_0x00fe:
        r5 = new java.util.ArrayList;
        r5.<init>();
    L_0x0103:
        r0 = r13.messages;
        r0 = r0.size();
        r1 = 0;
    L_0x010a:
        if (r1 >= r0) goto L_0x0127;
    L_0x010c:
        r6 = r13.messages;
        r6 = r6.get(r1);
        r6 = (java.lang.Integer) r6;
        r6 = r6.intValue();
        r21 = r2;
        r2 = (long) r6;
        r2 = java.lang.Long.valueOf(r2);
        r5.add(r2);
        r1 = r1 + 1;
        r2 = r21;
        goto L_0x010a;
    L_0x0127:
        r21 = r2;
    L_0x0129:
        r6 = r4;
        r38 = r10;
        r19 = r14;
        r2 = r20;
        r36 = r24;
    L_0x0132:
        r24 = r15;
        goto L_0x11f5;
    L_0x0136:
        r21 = r2;
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents;
        if (r0 == 0) goto L_0x0174;
    L_0x013c:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents) r13;
        if (r5 != 0) goto L_0x0145;
    L_0x0140:
        r5 = new java.util.ArrayList;
        r5.<init>();
    L_0x0145:
        r0 = r13.messages;
        r0 = r0.size();
        r1 = 0;
    L_0x014c:
        if (r1 >= r0) goto L_0x0129;
    L_0x014e:
        r3 = r13.messages;
        r3 = r3.get(r1);
        r3 = (java.lang.Integer) r3;
        r3 = r3.intValue();
        r2 = (long) r3;
        r6 = r13.channel_id;
        r19 = r13;
        r33 = r14;
        r13 = (long) r6;
        r6 = 32;
        r13 = r13 << r6;
        r2 = r2 | r13;
        r2 = java.lang.Long.valueOf(r2);
        r5.add(r2);
        r1 = r1 + 1;
        r13 = r19;
        r14 = r33;
        goto L_0x014c;
    L_0x0174:
        r33 = r14;
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
        if (r0 == 0) goto L_0x01e2;
    L_0x017a:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox) r13;
        if (r12 != 0) goto L_0x0183;
    L_0x017e:
        r12 = new org.telegram.messenger.support.SparseLongArray;
        r12.<init>();
    L_0x0183:
        r0 = r13.peer;
        r1 = r0.chat_id;
        if (r1 == 0) goto L_0x0196;
    L_0x0189:
        r0 = -r1;
        r1 = r13.max_id;
        r1 = (long) r1;
        r12.put(r0, r1);
        r0 = r13.peer;
        r0 = r0.chat_id;
        r0 = -r0;
        goto L_0x01a2;
    L_0x0196:
        r0 = r0.user_id;
        r1 = r13.max_id;
        r1 = (long) r1;
        r12.put(r0, r1);
        r0 = r13.peer;
        r0 = r0.user_id;
    L_0x01a2:
        r0 = (long) r0;
        r2 = r11.dialogs_read_inbox_max;
        r3 = java.lang.Long.valueOf(r0);
        r2 = r2.get(r3);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x01c0;
    L_0x01b1:
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r3 = 0;
        r2 = r2.getDialogReadMax(r3, r0);
        r2 = java.lang.Integer.valueOf(r2);
    L_0x01c0:
        r3 = r11.dialogs_read_inbox_max;
        r0 = java.lang.Long.valueOf(r0);
        r1 = r2.intValue();
        r2 = r13.max_id;
        r1 = java.lang.Math.max(r1, r2);
        r1 = java.lang.Integer.valueOf(r1);
        r3.put(r0, r1);
        r6 = r4;
        r38 = r10;
        r2 = r20;
        r36 = r24;
        r19 = r33;
        goto L_0x0132;
    L_0x01e2:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
        if (r0 == 0) goto L_0x0248;
    L_0x01e6:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox) r13;
        r14 = r24;
        if (r14 != 0) goto L_0x01f3;
    L_0x01ec:
        r24 = new org.telegram.messenger.support.SparseLongArray;
        r24.<init>();
        r14 = r24;
    L_0x01f3:
        r0 = r13.peer;
        r1 = r0.chat_id;
        if (r1 == 0) goto L_0x0206;
    L_0x01f9:
        r0 = -r1;
        r1 = r13.max_id;
        r1 = (long) r1;
        r14.put(r0, r1);
        r0 = r13.peer;
        r0 = r0.chat_id;
        r0 = -r0;
        goto L_0x0212;
    L_0x0206:
        r0 = r0.user_id;
        r1 = r13.max_id;
        r1 = (long) r1;
        r14.put(r0, r1);
        r0 = r13.peer;
        r0 = r0.user_id;
    L_0x0212:
        r0 = (long) r0;
        r2 = r11.dialogs_read_outbox_max;
        r3 = java.lang.Long.valueOf(r0);
        r2 = r2.get(r3);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x0230;
    L_0x0221:
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r3 = 1;
        r2 = r2.getDialogReadMax(r3, r0);
        r2 = java.lang.Integer.valueOf(r2);
    L_0x0230:
        r3 = r11.dialogs_read_outbox_max;
        r0 = java.lang.Long.valueOf(r0);
        r1 = r2.intValue();
        r2 = r13.max_id;
        r1 = java.lang.Math.max(r1, r2);
        r1 = java.lang.Integer.valueOf(r1);
        r3.put(r0, r1);
        goto L_0x0273;
    L_0x0248:
        r14 = r24;
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
        if (r0 == 0) goto L_0x0280;
    L_0x024e:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateDeleteMessages) r13;
        r3 = r28;
        if (r3 != 0) goto L_0x025b;
    L_0x0254:
        r28 = new android.util.SparseArray;
        r28.<init>();
        r3 = r28;
    L_0x025b:
        r0 = 0;
        r1 = r3.get(r0);
        r1 = (java.util.ArrayList) r1;
        if (r1 != 0) goto L_0x026c;
    L_0x0264:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r3.put(r0, r1);
    L_0x026c:
        r0 = r13.messages;
        r1.addAll(r0);
    L_0x0271:
        r28 = r3;
    L_0x0273:
        r6 = r4;
        r38 = r10;
        r36 = r14;
        r24 = r15;
        r2 = r20;
        r19 = r33;
        goto L_0x11f5;
    L_0x0280:
        r3 = r28;
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateUserTyping;
        r2 = r7;
        if (r0 != 0) goto L_0x0dbb;
    L_0x0287:
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChatUserTyping;
        if (r6 == 0) goto L_0x028d;
    L_0x028b:
        goto L_0x0dbb;
    L_0x028d:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipants;
        if (r0 == 0) goto L_0x02a6;
    L_0x0291:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipants) r13;
        r7 = r2 | 32;
        if (r30 != 0) goto L_0x029c;
    L_0x0297:
        r30 = new java.util.ArrayList;
        r30.<init>();
    L_0x029c:
        r0 = r30;
        r1 = r13.participants;
        r0.add(r1);
        r30 = r0;
        goto L_0x0271;
    L_0x02a6:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateUserStatus;
        if (r0 == 0) goto L_0x02bb;
    L_0x02aa:
        r7 = r2 | 4;
        if (r31 != 0) goto L_0x02b3;
    L_0x02ae:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x02b3:
        r0 = r31;
        r0.add(r13);
    L_0x02b8:
        r31 = r0;
        goto L_0x0271;
    L_0x02bb:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r0 == 0) goto L_0x02ce;
    L_0x02bf:
        r7 = r2 | 1;
        if (r31 != 0) goto L_0x02c8;
    L_0x02c3:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x02c8:
        r0 = r31;
        r0.add(r13);
        goto L_0x02b8;
    L_0x02ce:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
        if (r0 == 0) goto L_0x02ef;
    L_0x02d2:
        r0 = r13;
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhoto) r0;
        r7 = r2 | 2;
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r0 = r0.user_id;
        r1.clearUserPhotos(r0);
        if (r31 != 0) goto L_0x02e9;
    L_0x02e4:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x02e9:
        r0 = r31;
        r0.add(r13);
        goto L_0x02b8;
    L_0x02ef:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhone;
        if (r0 == 0) goto L_0x0302;
    L_0x02f3:
        r7 = r2 | 1024;
        if (r31 != 0) goto L_0x02fc;
    L_0x02f7:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x02fc:
        r0 = r31;
        r0.add(r13);
        goto L_0x02b8;
    L_0x0302:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateContactLink;
        if (r0 == 0) goto L_0x0364;
    L_0x0306:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateContactLink) r13;
        if (r8 != 0) goto L_0x030f;
    L_0x030a:
        r8 = new java.util.ArrayList;
        r8.<init>();
    L_0x030f:
        r0 = r13.my_link;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_contactLinkContact;
        r1 = -1;
        if (r0 == 0) goto L_0x033c;
    L_0x0316:
        r0 = r13.user_id;
        r0 = -r0;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r8.indexOf(r0);
        if (r0 == r1) goto L_0x0326;
    L_0x0323:
        r8.remove(r0);
    L_0x0326:
        r0 = r13.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r8.contains(r0);
        if (r0 != 0) goto L_0x0361;
    L_0x0332:
        r0 = r13.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r8.add(r0);
        goto L_0x0361;
    L_0x033c:
        r0 = r13.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r8.indexOf(r0);
        if (r0 == r1) goto L_0x034b;
    L_0x0348:
        r8.remove(r0);
    L_0x034b:
        r0 = r13.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r8.contains(r0);
        if (r0 != 0) goto L_0x0361;
    L_0x0357:
        r0 = r13.user_id;
        r0 = -r0;
        r0 = java.lang.Integer.valueOf(r0);
        r8.add(r0);
    L_0x0361:
        r7 = r2;
        goto L_0x0271;
    L_0x0364:
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
        if (r0 == 0) goto L_0x0433;
    L_0x0368:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.SecretChatHelper.getInstance(r0);
        r13 = (org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage) r13;
        r1 = r13.message;
        r0 = r0.decryptMessage(r1);
        if (r0 == 0) goto L_0x041a;
    L_0x0378:
        r1 = r0.isEmpty();
        if (r1 != 0) goto L_0x041a;
    L_0x037e:
        r1 = r13.message;
        r1 = r1.chat_id;
        r6 = (long) r1;
        r1 = 32;
        r6 = r6 << r1;
        if (r9 != 0) goto L_0x038d;
    L_0x0388:
        r9 = new android.util.LongSparseArray;
        r9.<init>();
    L_0x038d:
        r1 = r9;
        r9 = r1.get(r6);
        r9 = (java.util.ArrayList) r9;
        if (r9 != 0) goto L_0x039e;
    L_0x0396:
        r9 = new java.util.ArrayList;
        r9.<init>();
        r1.put(r6, r9);
    L_0x039e:
        r13 = r9;
        r9 = r0.size();
        r17 = r1;
        r1 = 0;
    L_0x03a6:
        if (r1 >= r9) goto L_0x0410;
    L_0x03a8:
        r19 = r0.get(r1);
        r24 = r0;
        r0 = r19;
        r0 = (org.telegram.tgnet.TLRPC.Message) r0;
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r0);
        if (r18 != 0) goto L_0x03bc;
    L_0x03b7:
        r18 = new java.util.ArrayList;
        r18.<init>();
    L_0x03bc:
        r28 = r2;
        r2 = r18;
        r2.add(r0);
        r18 = r2;
        r2 = new org.telegram.messenger.MessageObject;
        r35 = r8;
        r8 = r11.currentAccount;
        r36 = r5;
        r5 = r11.createdDialogIds;
        r19 = r9;
        r9 = java.lang.Long.valueOf(r6);
        r34 = r5.contains(r9);
        r9 = r36;
        r5 = r2;
        r36 = r6;
        r6 = r8;
        r7 = r0;
        r0 = r35;
        r8 = r4;
        r35 = r9;
        r9 = r33;
        r38 = r10;
        r10 = r34;
        r5.<init>(r6, r7, r8, r9, r10);
        r13.add(r2);
        if (r20 != 0) goto L_0x03f9;
    L_0x03f3:
        r5 = new java.util.ArrayList;
        r5.<init>();
        goto L_0x03fb;
    L_0x03f9:
        r5 = r20;
    L_0x03fb:
        r5.add(r2);
        r1 = r1 + 1;
        r8 = r0;
        r20 = r5;
        r9 = r19;
        r0 = r24;
        r2 = r28;
        r5 = r35;
        r6 = r36;
        r10 = r38;
        goto L_0x03a6;
    L_0x0410:
        r28 = r2;
        r35 = r5;
        r0 = r8;
        r38 = r10;
        r9 = r17;
        goto L_0x0421;
    L_0x041a:
        r28 = r2;
        r35 = r5;
        r0 = r8;
        r38 = r10;
    L_0x0421:
        r8 = r0;
        r6 = r4;
        r36 = r14;
        r24 = r15;
    L_0x0427:
        r2 = r20;
        r7 = r28;
        r19 = r33;
        r5 = r35;
    L_0x042f:
        r28 = r3;
        goto L_0x11f5;
    L_0x0433:
        r28 = r2;
        r35 = r5;
        r0 = r8;
        r38 = r10;
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
        if (r5 == 0) goto L_0x04e2;
    L_0x043e:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping) r13;
        r1 = r13.chat_id;
        r5 = 1;
        r1 = r11.getEncryptedChatDB(r1, r5);
        if (r1 == 0) goto L_0x04d6;
    L_0x0449:
        r5 = r13.chat_id;
        r5 = (long) r5;
        r2 = 32;
        r5 = r5 << r2;
        r2 = r11.printingUsers;
        r7 = java.lang.Long.valueOf(r5);
        r2 = r2.get(r7);
        r2 = (java.util.ArrayList) r2;
        if (r2 != 0) goto L_0x046b;
    L_0x045d:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r7 = r11.printingUsers;
        r5 = java.lang.Long.valueOf(r5);
        r7.put(r5, r2);
    L_0x046b:
        r5 = r2.size();
        r6 = 0;
    L_0x0470:
        if (r6 >= r5) goto L_0x049c;
    L_0x0472:
        r7 = r2.get(r6);
        r7 = (org.telegram.messenger.MessagesController.PrintingUser) r7;
        r8 = r7.userId;
        r10 = r1.user_id;
        if (r8 != r10) goto L_0x048f;
    L_0x047e:
        r36 = r14;
        r24 = r15;
        r14 = r21;
        r7.lastTime = r14;
        r5 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction;
        r5.<init>();
        r7.action = r5;
        r5 = 1;
        goto L_0x04a3;
    L_0x048f:
        r36 = r14;
        r24 = r15;
        r14 = r21;
        r6 = r6 + 1;
        r15 = r24;
        r14 = r36;
        goto L_0x0470;
    L_0x049c:
        r36 = r14;
        r24 = r15;
        r14 = r21;
        r5 = 0;
    L_0x04a3:
        if (r5 != 0) goto L_0x04bc;
    L_0x04a5:
        r5 = new org.telegram.messenger.MessagesController$PrintingUser;
        r5.<init>();
        r6 = r1.user_id;
        r5.userId = r6;
        r5.lastTime = r14;
        r6 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction;
        r6.<init>();
        r5.action = r6;
        r2.add(r5);
        r16 = 1;
    L_0x04bc:
        r2 = r11.onlinePrivacy;
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r5 = r11.currentAccount;
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5);
        r5 = r5.getCurrentTime();
        r5 = java.lang.Integer.valueOf(r5);
        r2.put(r1, r5);
        goto L_0x04dc;
    L_0x04d6:
        r36 = r14;
        r24 = r15;
        r14 = r21;
    L_0x04dc:
        r8 = r0;
        r6 = r4;
        r21 = r14;
        goto L_0x0427;
    L_0x04e2:
        r36 = r14;
        r24 = r15;
        r14 = r21;
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
        if (r5 == 0) goto L_0x0520;
    L_0x04ec:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r13;
        r10 = r27;
        if (r10 != 0) goto L_0x04f9;
    L_0x04f2:
        r27 = new android.util.SparseIntArray;
        r27.<init>();
        r10 = r27;
    L_0x04f9:
        r1 = r13.chat_id;
        r2 = r13.max_date;
        r10.put(r1, r2);
        r8 = r32;
        if (r8 != 0) goto L_0x050b;
    L_0x0504:
        r32 = new java.util.ArrayList;
        r32.<init>();
        r8 = r32;
    L_0x050b:
        r8.add(r13);
    L_0x050e:
        r6 = r4;
        r32 = r8;
        r27 = r10;
        r21 = r14;
        r2 = r20;
        r7 = r28;
        r19 = r33;
        r5 = r35;
        r8 = r0;
        goto L_0x042f;
    L_0x0520:
        r10 = r27;
        r8 = r32;
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd;
        if (r5 == 0) goto L_0x055b;
    L_0x0528:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd) r13;
        r1 = r11.currentAccount;
        r39 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1 = r13.chat_id;
        r2 = r13.user_id;
        r42 = 0;
        r5 = r13.inviter_id;
        r6 = r13.version;
        r40 = r1;
        r41 = r2;
        r43 = r5;
        r44 = r6;
        r39.updateChatInfo(r40, r41, r42, r43, r44);
    L_0x0545:
        r45 = r8;
        r27 = r9;
        r21 = r14;
        r2 = r20;
        r14 = r26;
        r15 = r29;
        r20 = r33;
        r26 = r25;
        r25 = r10;
    L_0x0557:
        r10 = r23;
        goto L_0x0da1;
    L_0x055b:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete;
        if (r5 == 0) goto L_0x057b;
    L_0x055f:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete) r13;
        r1 = r11.currentAccount;
        r39 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1 = r13.chat_id;
        r2 = r13.user_id;
        r42 = 1;
        r43 = 0;
        r5 = r13.version;
        r40 = r1;
        r41 = r2;
        r44 = r5;
        r39.updateChatInfo(r40, r41, r42, r43, r44);
        goto L_0x0545;
    L_0x057b:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateDcOptions;
        if (r5 != 0) goto L_0x0d84;
    L_0x057f:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateConfig;
        if (r5 == 0) goto L_0x0585;
    L_0x0583:
        goto L_0x0d84;
    L_0x0585:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryption;
        if (r5 == 0) goto L_0x0595;
    L_0x0589:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.SecretChatHelper.getInstance(r1);
        r13 = (org.telegram.tgnet.TLRPC.TL_updateEncryption) r13;
        r1.processUpdateEncryption(r13, r4);
        goto L_0x0545;
    L_0x0595:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
        if (r5 == 0) goto L_0x05d4;
    L_0x0599:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateUserBlocked) r13;
        r1 = r13.blocked;
        if (r1 == 0) goto L_0x05b5;
    L_0x059f:
        r1 = new android.util.SparseIntArray;
        r1.<init>();
        r2 = r13.user_id;
        r5 = 1;
        r1.put(r2, r5);
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r5 = 0;
        r2.putBlockedUsers(r1, r5);
        goto L_0x05c0;
    L_0x05b5:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r2 = r13.user_id;
        r1.deleteBlockedUser(r2);
    L_0x05c0:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1 = r1.getStorageQueue();
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$0Px26KfS0c0TW2u9ZjYkQVOvQKA;
        r2.<init>(r11, r13);
        r1.postRunnable(r2);
        goto L_0x0545;
    L_0x05d4:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
        if (r5 == 0) goto L_0x05e8;
    L_0x05d8:
        if (r31 != 0) goto L_0x05df;
    L_0x05da:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x05df:
        r1 = r31;
        r1.add(r13);
        r31 = r1;
        goto L_0x050e;
    L_0x05e8:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
        if (r5 == 0) goto L_0x06e1;
    L_0x05ec:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateServiceNotification) r13;
        r1 = r13.popup;
        if (r1 == 0) goto L_0x0604;
    L_0x05f2:
        r1 = r13.message;
        if (r1 == 0) goto L_0x0604;
    L_0x05f6:
        r1 = r1.length();
        if (r1 <= 0) goto L_0x0604;
    L_0x05fc:
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$jWG8NvZquHH3NCs4zu_-Fuu7gvw;
        r1.<init>(r11, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x0604:
        r1 = r13.flags;
        r2 = 2;
        r1 = r1 & r2;
        if (r1 == 0) goto L_0x06d6;
    L_0x060a:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;
        r1.<init>();
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.getNewMessageId();
        r1.id = r2;
        r1.local_id = r2;
        r2 = r11.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r5 = 0;
        r2.saveConfig(r5);
        r2 = 1;
        r1.unread = r2;
        r2 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        r1.flags = r2;
        r2 = r13.inbox_date;
        if (r2 == 0) goto L_0x0635;
    L_0x0632:
        r1.date = r2;
        goto L_0x0640;
    L_0x0635:
        r5 = java.lang.System.currentTimeMillis();
        r21 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r21;
        r2 = (int) r5;
        r1.date = r2;
    L_0x0640:
        r2 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r1.from_id = r2;
        r2 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r2.<init>();
        r1.to_id = r2;
        r2 = r1.to_id;
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getClientUserId();
        r2.user_id = r5;
        r5 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r1.dialog_id = r5;
        r2 = r13.media;
        if (r2 == 0) goto L_0x066b;
    L_0x0663:
        r1.media = r2;
        r2 = r1.flags;
        r2 = r2 | 512;
        r1.flags = r2;
    L_0x066b:
        r2 = r13.message;
        r1.message = r2;
        r2 = r13.entities;
        if (r2 == 0) goto L_0x0675;
    L_0x0673:
        r1.entities = r2;
    L_0x0675:
        if (r18 != 0) goto L_0x067c;
    L_0x0677:
        r18 = new java.util.ArrayList;
        r18.<init>();
    L_0x067c:
        r2 = r18;
        r2.add(r1);
        r13 = new org.telegram.messenger.MessageObject;
        r6 = r11.currentAccount;
        r5 = r11.createdDialogIds;
        r19 = r8;
        r7 = r1.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r17 = r5.contains(r7);
        r5 = r13;
        r8 = r20;
        r7 = r1;
        r18 = r2;
        r2 = r8;
        r45 = r19;
        r8 = r4;
        r21 = r14;
        r14 = r9;
        r9 = r33;
        r15 = r10;
        r10 = r17;
        r5.<init>(r6, r7, r8, r9, r10);
        if (r14 != 0) goto L_0x06b0;
    L_0x06aa:
        r9 = new android.util.LongSparseArray;
        r9.<init>();
        goto L_0x06b1;
    L_0x06b0:
        r9 = r14;
    L_0x06b1:
        r5 = r1.dialog_id;
        r5 = r9.get(r5);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x06c5;
    L_0x06bb:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r6 = r1.dialog_id;
        r9.put(r6, r5);
    L_0x06c5:
        r5.add(r13);
        if (r2 != 0) goto L_0x06d0;
    L_0x06ca:
        r6 = new java.util.ArrayList;
        r6.<init>();
        goto L_0x06d1;
    L_0x06d0:
        r6 = r2;
    L_0x06d1:
        r6.add(r13);
        r2 = r6;
        goto L_0x06de;
    L_0x06d6:
        r45 = r8;
        r21 = r14;
        r2 = r20;
        r14 = r9;
        r15 = r10;
    L_0x06de:
        r8 = r0;
        r6 = r4;
        goto L_0x0700;
    L_0x06e1:
        r45 = r8;
        r21 = r14;
        r2 = r20;
        r5 = 32;
        r14 = r9;
        r15 = r10;
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
        if (r6 == 0) goto L_0x070c;
    L_0x06ef:
        if (r31 != 0) goto L_0x06f6;
    L_0x06f1:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x06f6:
        r1 = r31;
        r1.add(r13);
    L_0x06fb:
        r8 = r0;
        r31 = r1;
        r6 = r4;
    L_0x06ff:
        r9 = r14;
    L_0x0700:
        r27 = r15;
        r7 = r28;
        r19 = r33;
        r5 = r35;
        r32 = r45;
        goto L_0x042f;
    L_0x070c:
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs;
        if (r6 == 0) goto L_0x071d;
    L_0x0710:
        if (r31 != 0) goto L_0x0717;
    L_0x0712:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0717:
        r1 = r31;
        r1.add(r13);
        goto L_0x06fb;
    L_0x071d:
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateFolderPeers;
        if (r6 == 0) goto L_0x0743;
    L_0x0721:
        if (r31 != 0) goto L_0x0728;
    L_0x0723:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0728:
        r1 = r31;
        r1.add(r13);
        r13 = (org.telegram.tgnet.TLRPC.TL_updateFolderPeers) r13;
        r5 = r11.currentAccount;
        r39 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r13.folder_peers;
        r41 = 0;
        r42 = 0;
        r44 = 0;
        r40 = r5;
        r39.setDialogsFolderId(r40, r41, r42, r44);
        goto L_0x06fb;
    L_0x0743:
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updatePrivacy;
        if (r6 == 0) goto L_0x0754;
    L_0x0747:
        if (r31 != 0) goto L_0x074e;
    L_0x0749:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x074e:
        r1 = r31;
        r1.add(r13);
        goto L_0x06fb;
    L_0x0754:
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateWebPage;
        if (r6 == 0) goto L_0x0771;
    L_0x0758:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateWebPage) r13;
        r10 = r25;
        if (r10 != 0) goto L_0x0765;
    L_0x075e:
        r25 = new android.util.LongSparseArray;
        r25.<init>();
        r10 = r25;
    L_0x0765:
        r1 = r13.webpage;
        r5 = r1.id;
        r10.put(r5, r1);
    L_0x076c:
        r8 = r0;
        r6 = r4;
        r25 = r10;
        goto L_0x06ff;
    L_0x0771:
        r10 = r25;
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelWebPage;
        if (r6 == 0) goto L_0x078a;
    L_0x0777:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChannelWebPage) r13;
        if (r10 != 0) goto L_0x0782;
    L_0x077b:
        r25 = new android.util.LongSparseArray;
        r25.<init>();
        r10 = r25;
    L_0x0782:
        r1 = r13.webpage;
        r5 = r1.id;
        r10.put(r5, r1);
        goto L_0x076c;
    L_0x078a:
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelTooLong;
        if (r6 == 0) goto L_0x0835;
    L_0x078e:
        r5 = r13;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelTooLong) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x07ac;
    L_0x0795:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r6.append(r1);
        r1 = r5.channel_id;
        r6.append(r1);
        r1 = r6.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x07ac:
        r1 = r11.channelsPts;
        r6 = r5.channel_id;
        r1 = r1.get(r6);
        if (r1 != 0) goto L_0x080e;
    L_0x07b6:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r6 = r5.channel_id;
        r1 = r1.getChannelPtsSync(r6);
        if (r1 != 0) goto L_0x0804;
    L_0x07c4:
        r6 = r5.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r9 = r33;
        r6 = r9.get(r6);
        r6 = (org.telegram.tgnet.TLRPC.Chat) r6;
        if (r6 == 0) goto L_0x07d8;
    L_0x07d4:
        r7 = r6.min;
        if (r7 == 0) goto L_0x07e2;
    L_0x07d8:
        r6 = r5.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r11.getChat(r6);
    L_0x07e2:
        if (r6 == 0) goto L_0x07e8;
    L_0x07e4:
        r7 = r6.min;
        if (r7 == 0) goto L_0x07f8;
    L_0x07e8:
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r7 = r5.channel_id;
        r6 = r6.getChatSync(r7);
        r7 = 1;
        r11.putChat(r6, r7);
    L_0x07f8:
        if (r6 == 0) goto L_0x0810;
    L_0x07fa:
        r7 = r6.min;
        if (r7 != 0) goto L_0x0810;
    L_0x07fe:
        r7 = 0;
        r11.loadUnknownChannel(r6, r7);
        goto L_0x0810;
    L_0x0804:
        r9 = r33;
        r6 = r11.channelsPts;
        r7 = r5.channel_id;
        r6.put(r7, r1);
        goto L_0x0810;
    L_0x080e:
        r9 = r33;
    L_0x0810:
        if (r1 == 0) goto L_0x0827;
    L_0x0812:
        r6 = r5.flags;
        r7 = 1;
        r6 = r6 & r7;
        if (r6 == 0) goto L_0x0822;
    L_0x0818:
        r6 = r5.pts;
        if (r6 <= r1) goto L_0x0827;
    L_0x081c:
        r1 = r5.channel_id;
        r11.getChannelDifference(r1);
        goto L_0x0827;
    L_0x0822:
        r1 = r5.channel_id;
        r11.getChannelDifference(r1);
    L_0x0827:
        r20 = r9;
        r27 = r14;
        r25 = r15;
        r14 = r26;
        r15 = r29;
    L_0x0831:
        r26 = r10;
        goto L_0x0557;
    L_0x0835:
        r9 = r33;
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
        if (r6 == 0) goto L_0x08a0;
    L_0x083b:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox) r13;
        r1 = r13.max_id;
        r6 = (long) r1;
        r1 = r13.channel_id;
        r27 = r14;
        r25 = r15;
        r14 = (long) r1;
        r14 = r14 << r5;
        r6 = r6 | r14;
        r1 = -r1;
        r14 = (long) r1;
        if (r12 != 0) goto L_0x0852;
    L_0x084d:
        r12 = new org.telegram.messenger.support.SparseLongArray;
        r12.<init>();
    L_0x0852:
        r1 = r13.channel_id;
        r1 = -r1;
        r12.put(r1, r6);
        r1 = r11.dialogs_read_inbox_max;
        r5 = java.lang.Long.valueOf(r14);
        r1 = r1.get(r5);
        r1 = (java.lang.Integer) r1;
        if (r1 != 0) goto L_0x0875;
    L_0x0866:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r5 = 0;
        r1 = r1.getDialogReadMax(r5, r14);
        r1 = java.lang.Integer.valueOf(r1);
    L_0x0875:
        r5 = r11.dialogs_read_inbox_max;
        r6 = java.lang.Long.valueOf(r14);
        r1 = r1.intValue();
        r7 = r13.max_id;
        r1 = java.lang.Math.max(r1, r7);
        r1 = java.lang.Integer.valueOf(r1);
        r5.put(r6, r1);
        r8 = r0;
    L_0x088d:
        r6 = r4;
        r19 = r9;
    L_0x0890:
        r9 = r27;
        r7 = r28;
        r5 = r35;
        r32 = r45;
        r28 = r3;
    L_0x089a:
        r27 = r25;
        r25 = r10;
        goto L_0x11f5;
    L_0x08a0:
        r27 = r14;
        r25 = r15;
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox;
        if (r5 == 0) goto L_0x08ff;
    L_0x08a8:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox) r13;
        r1 = r13.max_id;
        r7 = (long) r1;
        r1 = r13.channel_id;
        r14 = (long) r1;
        r5 = 32;
        r5 = r14 << r5;
        r5 = r5 | r7;
        r1 = -r1;
        r7 = (long) r1;
        if (r36 != 0) goto L_0x08bf;
    L_0x08b9:
        r1 = new org.telegram.messenger.support.SparseLongArray;
        r1.<init>();
        goto L_0x08c1;
    L_0x08bf:
        r1 = r36;
    L_0x08c1:
        r14 = r13.channel_id;
        r14 = -r14;
        r1.put(r14, r5);
        r5 = r11.dialogs_read_outbox_max;
        r6 = java.lang.Long.valueOf(r7);
        r5 = r5.get(r6);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x08e4;
    L_0x08d5:
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r6 = 1;
        r5 = r5.getDialogReadMax(r6, r7);
        r5 = java.lang.Integer.valueOf(r5);
    L_0x08e4:
        r6 = r11.dialogs_read_outbox_max;
        r7 = java.lang.Long.valueOf(r7);
        r5 = r5.intValue();
        r8 = r13.max_id;
        r5 = java.lang.Math.max(r5, r8);
        r5 = java.lang.Integer.valueOf(r5);
        r6.put(r7, r5);
        r8 = r0;
        r36 = r1;
        goto L_0x088d;
    L_0x08ff:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
        if (r5 == 0) goto L_0x0953;
    L_0x0903:
        r5 = r13;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0921;
    L_0x090a:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r6.append(r1);
        r1 = r5.channel_id;
        r6.append(r1);
        r1 = r6.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0921:
        if (r3 != 0) goto L_0x0929;
    L_0x0923:
        r1 = new android.util.SparseArray;
        r1.<init>();
        goto L_0x092a;
    L_0x0929:
        r1 = r3;
    L_0x092a:
        r3 = r5.channel_id;
        r3 = r1.get(r3);
        r3 = (java.util.ArrayList) r3;
        if (r3 != 0) goto L_0x093e;
    L_0x0934:
        r3 = new java.util.ArrayList;
        r3.<init>();
        r6 = r5.channel_id;
        r1.put(r6, r3);
    L_0x093e:
        r5 = r5.messages;
        r3.addAll(r5);
        r8 = r0;
        r6 = r4;
        r19 = r9;
        r9 = r27;
        r7 = r28;
        r5 = r35;
        r32 = r45;
        r28 = r1;
        goto L_0x089a;
    L_0x0953:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChannel;
        if (r5 == 0) goto L_0x0986;
    L_0x0957:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0975;
    L_0x095b:
        r5 = r13;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannel) r5;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r6.append(r1);
        r1 = r5.channel_id;
        r6.append(r1);
        r1 = r6.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0975:
        if (r31 != 0) goto L_0x097c;
    L_0x0977:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x097c:
        r1 = r31;
        r1.add(r13);
        r8 = r0;
        r31 = r1;
        goto L_0x088d;
    L_0x0986:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews;
        if (r5 == 0) goto L_0x09d6;
    L_0x098a:
        r5 = r13;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x09a8;
    L_0x0991:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r6.append(r1);
        r1 = r5.channel_id;
        r6.append(r1);
        r1 = r6.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x09a8:
        r14 = r26;
        if (r14 != 0) goto L_0x09b3;
    L_0x09ac:
        r26 = new android.util.SparseArray;
        r26.<init>();
        r14 = r26;
    L_0x09b3:
        r1 = r5.channel_id;
        r1 = r14.get(r1);
        r1 = (android.util.SparseIntArray) r1;
        if (r1 != 0) goto L_0x09c7;
    L_0x09bd:
        r1 = new android.util.SparseIntArray;
        r1.<init>();
        r6 = r5.channel_id;
        r14.put(r6, r1);
    L_0x09c7:
        r6 = r5.id;
        r5 = r5.views;
        r1.put(r6, r5);
        r8 = r0;
    L_0x09cf:
        r6 = r4;
        r19 = r9;
        r26 = r14;
        goto L_0x0890;
    L_0x09d6:
        r14 = r26;
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
        if (r5 == 0) goto L_0x0a03;
    L_0x09dc:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin) r13;
        r1 = r11.currentAccount;
        r39 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1 = r13.chat_id;
        r5 = r13.user_id;
        r42 = 2;
        r6 = r13.is_admin;
        r7 = r13.version;
        r40 = r1;
        r41 = r5;
        r43 = r6;
        r44 = r7;
        r39.updateChatInfo(r40, r41, r42, r43, r44);
    L_0x09f9:
        r20 = r9;
        r26 = r10;
        r10 = r23;
        r15 = r29;
        goto L_0x0da1;
    L_0x0a03:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights;
        if (r5 == 0) goto L_0x0a2d;
    L_0x0a07:
        r1 = r13;
        r1 = (org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights) r1;
        r5 = r1.peer;
        r6 = r5.channel_id;
        if (r6 == 0) goto L_0x0a11;
    L_0x0a10:
        goto L_0x0a13;
    L_0x0a11:
        r6 = r5.chat_id;
    L_0x0a13:
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r7 = r1.default_banned_rights;
        r1 = r1.version;
        r5.updateChatDefaultBannedRights(r6, r7, r1);
        if (r31 != 0) goto L_0x0a27;
    L_0x0a22:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0a27:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0a2d:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSets;
        if (r5 == 0) goto L_0x0a41;
    L_0x0a31:
        if (r31 != 0) goto L_0x0a38;
    L_0x0a33:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0a38:
        r1 = r31;
        r1.add(r13);
    L_0x0a3d:
        r8 = r0;
        r31 = r1;
        goto L_0x09cf;
    L_0x0a41:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
        if (r5 == 0) goto L_0x0a52;
    L_0x0a45:
        if (r31 != 0) goto L_0x0a4c;
    L_0x0a47:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0a4c:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0a52:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
        if (r5 == 0) goto L_0x0a63;
    L_0x0a56:
        if (r31 != 0) goto L_0x0a5d;
    L_0x0a58:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0a5d:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0a63:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
        if (r5 == 0) goto L_0x0a74;
    L_0x0a67:
        if (r31 != 0) goto L_0x0a6e;
    L_0x0a69:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0a6e:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0a74:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
        if (r5 == 0) goto L_0x0a85;
    L_0x0a78:
        if (r31 != 0) goto L_0x0a7f;
    L_0x0a7a:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0a7f:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0a85:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
        if (r5 != 0) goto L_0x0bdd;
    L_0x0a89:
        r6 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateEditMessage;
        if (r6 == 0) goto L_0x0a8f;
    L_0x0a8d:
        goto L_0x0bdd;
    L_0x0a8f:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
        if (r5 == 0) goto L_0x0ac0;
    L_0x0a93:
        r5 = r13;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage) r5;
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r6 == 0) goto L_0x0ab1;
    L_0x0a9a:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r6.append(r1);
        r1 = r5.channel_id;
        r6.append(r1);
        r1 = r6.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0ab1:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r6 = r5.channel_id;
        r5 = r5.id;
        r1.updateChatPinnedMessage(r6, r5);
        goto L_0x09f9;
    L_0x0ac0:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage;
        if (r1 == 0) goto L_0x0ad5;
    L_0x0ac4:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage) r13;
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r5 = r13.chat_id;
        r6 = r13.id;
        r1.updateChatPinnedMessage(r5, r6);
        goto L_0x09f9;
    L_0x0ad5:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage;
        if (r1 == 0) goto L_0x0aea;
    L_0x0ad9:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage) r13;
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r5 = r13.user_id;
        r6 = r13.id;
        r1.updateUserPinnedMessage(r5, r6);
        goto L_0x09f9;
    L_0x0aea:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
        if (r1 == 0) goto L_0x0afc;
    L_0x0aee:
        if (r31 != 0) goto L_0x0af5;
    L_0x0af0:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0af5:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0afc:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updatePhoneCall;
        if (r1 == 0) goto L_0x0b0e;
    L_0x0b00:
        if (r31 != 0) goto L_0x0b07;
    L_0x0b02:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0b07:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0b0e:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPack;
        if (r1 == 0) goto L_0x0b1e;
    L_0x0b12:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateLangPack) r13;
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$JHJ-M64u5jSxCVF9v5FzZPiyZZ4;
        r1.<init>(r11, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
        goto L_0x09f9;
    L_0x0b1e:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong;
        if (r1 == 0) goto L_0x0b31;
    L_0x0b22:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong) r13;
        r1 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r11.currentAccount;
        r6 = r13.lang_code;
        r1.reloadCurrentRemoteLocale(r5, r6);
        goto L_0x09f9;
    L_0x0b31:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
        if (r1 == 0) goto L_0x0b43;
    L_0x0b35:
        if (r31 != 0) goto L_0x0b3c;
    L_0x0b37:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0b3c:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0b43:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateContactsReset;
        if (r1 == 0) goto L_0x0b55;
    L_0x0b47:
        if (r31 != 0) goto L_0x0b4e;
    L_0x0b49:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0b4e:
        r1 = r31;
        r1.add(r13);
        goto L_0x0a3d;
    L_0x0b55:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages;
        if (r1 == 0) goto L_0x0b7b;
    L_0x0b59:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages) r13;
        r15 = r29;
        if (r15 != 0) goto L_0x0b66;
    L_0x0b5f:
        r29 = new android.util.SparseIntArray;
        r29.<init>();
        r15 = r29;
    L_0x0b66:
        r1 = r13.channel_id;
        r1 = r15.get(r1);
        if (r1 == 0) goto L_0x0b72;
    L_0x0b6e:
        r5 = r13.available_min_id;
        if (r1 >= r5) goto L_0x0b79;
    L_0x0b72:
        r1 = r13.channel_id;
        r5 = r13.available_min_id;
        r15.put(r1, r5);
    L_0x0b79:
        r8 = r0;
        goto L_0x0b90;
    L_0x0b7b:
        r15 = r29;
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark;
        if (r1 == 0) goto L_0x0b99;
    L_0x0b81:
        if (r31 != 0) goto L_0x0b88;
    L_0x0b83:
        r31 = new java.util.ArrayList;
        r31.<init>();
    L_0x0b88:
        r1 = r31;
        r1.add(r13);
    L_0x0b8d:
        r8 = r0;
        r31 = r1;
    L_0x0b90:
        r6 = r4;
        r19 = r9;
        r26 = r14;
        r29 = r15;
        goto L_0x0890;
    L_0x0b99:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_updateMessagePoll;
        if (r1 == 0) goto L_0x0bd9;
    L_0x0b9d:
        r1 = r13;
        r1 = (org.telegram.tgnet.TLRPC.TL_updateMessagePoll) r1;
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.SendMessagesHelper.getInstance(r5);
        r6 = r1.poll_id;
        r5 = r5.getVoteSendTime(r6);
        r7 = android.os.SystemClock.uptimeMillis();
        r7 = r7 - r5;
        r5 = java.lang.Math.abs(r7);
        r7 = 600; // 0x258 float:8.41E-43 double:2.964E-321;
        r17 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r17 >= 0) goto L_0x0bbc;
    L_0x0bbb:
        goto L_0x0bd9;
    L_0x0bbc:
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r6 = r1.poll_id;
        r8 = r1.poll;
        r1 = r1.results;
        r5.updateMessagePollResults(r6, r8, r1);
        if (r31 != 0) goto L_0x0bd3;
    L_0x0bcd:
        r1 = new java.util.ArrayList;
        r1.<init>();
        goto L_0x0bd5;
    L_0x0bd3:
        r1 = r31;
    L_0x0bd5:
        r1.add(r13);
        goto L_0x0b8d;
    L_0x0bd9:
        r20 = r9;
        goto L_0x0831;
    L_0x0bdd:
        r15 = r29;
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0beb:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage) r13;
        r5 = r13.message;
        r6 = r5.to_id;
        r6 = r6.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r9.get(r6);
        r6 = (org.telegram.tgnet.TLRPC.Chat) r6;
        if (r6 != 0) goto L_0x0c0b;
    L_0x0bff:
        r6 = r5.to_id;
        r6 = r6.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r11.getChat(r6);
    L_0x0c0b:
        if (r6 != 0) goto L_0x0c1f;
    L_0x0c0d:
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r7 = r5.to_id;
        r7 = r7.channel_id;
        r6 = r6.getChatSync(r7);
        r7 = 1;
        r11.putChat(r6, r7);
    L_0x0c1f:
        if (r6 == 0) goto L_0x0c2c;
    L_0x0CLASSNAME:
        r6 = r6.megagroup;
        if (r6 == 0) goto L_0x0c2c;
    L_0x0CLASSNAME:
        r6 = r5.flags;
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r6 = r6 | r7;
        r5.flags = r6;
    L_0x0c2c:
        r13 = r5;
        r33 = r9;
        r6 = 1;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateEditMessage) r13;
        r5 = r13.message;
        r6 = r5.dialog_id;
        r33 = r9;
        r8 = (long) r1;
        r13 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r13 != 0) goto L_0x0CLASSNAME;
    L_0x0c3e:
        r6 = 0;
        r5.unread = r6;
        r5.media_unread = r6;
        r6 = 1;
        r5.out = r6;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = 1;
    L_0x0CLASSNAME:
        r13 = r5;
    L_0x0CLASSNAME:
        r5 = r13.out;
        if (r5 != 0) goto L_0x0c5d;
    L_0x0c4d:
        r5 = r13.from_id;
        r7 = r11.currentAccount;
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);
        r7 = r7.getClientUserId();
        if (r5 != r7) goto L_0x0c5d;
    L_0x0c5b:
        r13.out = r6;
    L_0x0c5d:
        if (r52 != 0) goto L_0x0cb5;
    L_0x0c5f:
        r5 = r13.entities;
        r5 = r5.size();
        r6 = 0;
    L_0x0CLASSNAME:
        if (r6 >= r5) goto L_0x0cb5;
    L_0x0CLASSNAME:
        r7 = r13.entities;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.MessageEntity) r7;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r8 == 0) goto L_0x0cb2;
    L_0x0CLASSNAME:
        r7 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r7;
        r7 = r7.user_id;
        r8 = java.lang.Integer.valueOf(r7);
        r8 = r4.get(r8);
        r8 = (org.telegram.tgnet.TLRPC.User) r8;
        if (r8 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = r8.min;
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r8 = java.lang.Integer.valueOf(r7);
        r8 = r11.getUser(r8);
    L_0x0CLASSNAME:
        if (r8 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = r8.min;
        if (r9 == 0) goto L_0x0cae;
    L_0x0CLASSNAME:
        r8 = r11.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r7 = r8.getUserSync(r7);
        if (r7 == 0) goto L_0x0ca9;
    L_0x0ca2:
        r8 = r7.min;
        if (r8 == 0) goto L_0x0ca9;
    L_0x0ca6:
        r7 = 1;
        r8 = 0;
        goto L_0x0cab;
    L_0x0ca9:
        r8 = r7;
        r7 = 1;
    L_0x0cab:
        r11.putUser(r8, r7);
    L_0x0cae:
        if (r8 != 0) goto L_0x0cb2;
    L_0x0cb0:
        r7 = 0;
        return r7;
    L_0x0cb2:
        r6 = r6 + 1;
        goto L_0x0CLASSNAME;
    L_0x0cb5:
        r5 = r13.to_id;
        r6 = r5.chat_id;
        if (r6 == 0) goto L_0x0cc0;
    L_0x0cbb:
        r5 = -r6;
        r5 = (long) r5;
        r13.dialog_id = r5;
        goto L_0x0ce4;
    L_0x0cc0:
        r6 = r5.channel_id;
        if (r6 == 0) goto L_0x0cc9;
    L_0x0cc4:
        r5 = -r6;
        r5 = (long) r5;
        r13.dialog_id = r5;
        goto L_0x0ce4;
    L_0x0cc9:
        r5 = r5.user_id;
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        if (r5 != r6) goto L_0x0cdd;
    L_0x0cd7:
        r5 = r13.to_id;
        r6 = r13.from_id;
        r5.user_id = r6;
    L_0x0cdd:
        r5 = r13.to_id;
        r5 = r5.user_id;
        r5 = (long) r5;
        r13.dialog_id = r5;
    L_0x0ce4:
        r5 = r13.out;
        if (r5 == 0) goto L_0x0ceb;
    L_0x0ce8:
        r5 = r11.dialogs_read_outbox_max;
        goto L_0x0ced;
    L_0x0ceb:
        r5 = r11.dialogs_read_inbox_max;
    L_0x0ced:
        r6 = r13.dialog_id;
        r6 = java.lang.Long.valueOf(r6);
        r6 = r5.get(r6);
        r6 = (java.lang.Integer) r6;
        if (r6 != 0) goto L_0x0d16;
    L_0x0cfb:
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r7 = r13.out;
        r8 = r13.dialog_id;
        r6 = r6.getDialogReadMax(r7, r8);
        r6 = java.lang.Integer.valueOf(r6);
        r7 = r13.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r5.put(r7, r6);
    L_0x0d16:
        r5 = r6.intValue();
        r6 = r13.id;
        if (r5 >= r6) goto L_0x0d20;
    L_0x0d1e:
        r5 = 1;
        goto L_0x0d21;
    L_0x0d20:
        r5 = 0;
    L_0x0d21:
        r13.unread = r5;
        r5 = r13.dialog_id;
        r7 = (long) r1;
        r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r1 != 0) goto L_0x0d32;
    L_0x0d2a:
        r1 = 1;
        r13.out = r1;
        r1 = 0;
        r13.unread = r1;
        r13.media_unread = r1;
    L_0x0d32:
        r1 = r13.out;
        if (r1 == 0) goto L_0x0d40;
    L_0x0d36:
        r1 = r13.message;
        if (r1 != 0) goto L_0x0d40;
    L_0x0d3a:
        r1 = "";
        r13.message = r1;
        r13.attachPath = r1;
    L_0x0d40:
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r13);
        r1 = new org.telegram.messenger.MessageObject;
        r6 = r11.currentAccount;
        r5 = r11.createdDialogIds;
        r7 = r13.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r17 = r5.contains(r7);
        r5 = r1;
        r7 = r13;
        r8 = r4;
        r20 = r33;
        r9 = r20;
        r26 = r10;
        r10 = r17;
        r5.<init>(r6, r7, r8, r9, r10);
        r10 = r23;
        if (r10 != 0) goto L_0x0d6c;
    L_0x0d65:
        r23 = new android.util.LongSparseArray;
        r23.<init>();
        r10 = r23;
    L_0x0d6c:
        r5 = r13.dialog_id;
        r5 = r10.get(r5);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x0d80;
    L_0x0d76:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r6 = r13.dialog_id;
        r10.put(r6, r5);
    L_0x0d80:
        r5.add(r1);
        goto L_0x0da1;
    L_0x0d84:
        r45 = r8;
        r27 = r9;
        r21 = r14;
        r2 = r20;
        r14 = r26;
        r15 = r29;
        r20 = r33;
        r26 = r25;
        r25 = r10;
        r10 = r23;
        r1 = r11.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1.updateDcSettings();
    L_0x0da1:
        r8 = r0;
        r6 = r4;
        r23 = r10;
        r29 = r15;
        r19 = r20;
        r9 = r27;
        r7 = r28;
        r5 = r35;
        r32 = r45;
        r28 = r3;
        r27 = r25;
        r25 = r26;
        r26 = r14;
        goto L_0x11f5;
    L_0x0dbb:
        r28 = r2;
        r35 = r5;
        r38 = r10;
        r36 = r14;
        r24 = r15;
        r2 = r20;
        r10 = r23;
        r14 = r26;
        r15 = r29;
        r45 = r32;
        r20 = r33;
        r26 = r25;
        r25 = r27;
        r27 = r9;
        r9 = r8;
        if (r0 == 0) goto L_0x0de3;
    L_0x0dda:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateUserTyping) r13;
        r0 = r13.user_id;
        r1 = r13.action;
        r5 = r1;
        r1 = 0;
        goto L_0x0df0;
    L_0x0de3:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateChatUserTyping) r13;
        r0 = r13.chat_id;
        r1 = r13.user_id;
        r5 = r13.action;
        r46 = r1;
        r1 = r0;
        r0 = r46;
    L_0x0df0:
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        if (r0 == r6) goto L_0x0eb6;
    L_0x0dfc:
        r1 = -r1;
        r6 = (long) r1;
        r33 = 0;
        r1 = (r6 > r33 ? 1 : (r6 == r33 ? 0 : -1));
        if (r1 != 0) goto L_0x0e05;
    L_0x0e04:
        r6 = (long) r0;
    L_0x0e05:
        r1 = r11.printingUsers;
        r8 = java.lang.Long.valueOf(r6);
        r1 = r1.get(r8);
        r1 = (java.util.ArrayList) r1;
        r8 = r5 instanceof org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction;
        if (r8 == 0) goto L_0x0e47;
    L_0x0e15:
        if (r1 == 0) goto L_0x0e40;
    L_0x0e17:
        r5 = r1.size();
        r8 = 0;
    L_0x0e1c:
        if (r8 >= r5) goto L_0x0e31;
    L_0x0e1e:
        r13 = r1.get(r8);
        r13 = (org.telegram.messenger.MessagesController.PrintingUser) r13;
        r13 = r13.userId;
        if (r13 != r0) goto L_0x0e2e;
    L_0x0e28:
        r1.remove(r8);
        r16 = 1;
        goto L_0x0e31;
    L_0x0e2e:
        r8 = r8 + 1;
        goto L_0x0e1c;
    L_0x0e31:
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x0e40;
    L_0x0e37:
        r1 = r11.printingUsers;
        r5 = java.lang.Long.valueOf(r6);
        r1.remove(r5);
    L_0x0e40:
        r23 = r14;
        r29 = r15;
        r14 = r21;
        goto L_0x0e9e;
    L_0x0e47:
        if (r1 != 0) goto L_0x0e57;
    L_0x0e49:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r8 = r11.printingUsers;
        r6 = java.lang.Long.valueOf(r6);
        r8.put(r6, r1);
    L_0x0e57:
        r6 = r1.iterator();
    L_0x0e5b:
        r7 = r6.hasNext();
        if (r7 == 0) goto L_0x0e85;
    L_0x0e61:
        r7 = r6.next();
        r7 = (org.telegram.messenger.MessagesController.PrintingUser) r7;
        r8 = r7.userId;
        if (r8 != r0) goto L_0x0e5b;
    L_0x0e6b:
        r23 = r14;
        r29 = r15;
        r14 = r21;
        r7.lastTime = r14;
        r6 = r7.action;
        r6 = r6.getClass();
        r8 = r5.getClass();
        if (r6 == r8) goto L_0x0e81;
    L_0x0e7f:
        r16 = 1;
    L_0x0e81:
        r7.action = r5;
        r6 = 1;
        goto L_0x0e8c;
    L_0x0e85:
        r23 = r14;
        r29 = r15;
        r14 = r21;
        r6 = 0;
    L_0x0e8c:
        if (r6 != 0) goto L_0x0e9e;
    L_0x0e8e:
        r6 = new org.telegram.messenger.MessagesController$PrintingUser;
        r6.<init>();
        r6.userId = r0;
        r6.lastTime = r14;
        r6.action = r5;
        r1.add(r6);
        r16 = 1;
    L_0x0e9e:
        r1 = r11.onlinePrivacy;
        r0 = java.lang.Integer.valueOf(r0);
        r5 = r11.currentAccount;
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5);
        r5 = r5.getCurrentTime();
        r5 = java.lang.Integer.valueOf(r5);
        r1.put(r0, r5);
        goto L_0x0ebc;
    L_0x0eb6:
        r23 = r14;
        r29 = r15;
        r14 = r21;
    L_0x0ebc:
        r6 = r4;
        r8 = r9;
        r21 = r14;
        r19 = r20;
        r9 = r27;
        r7 = r28;
        r5 = r35;
        r32 = r45;
        r28 = r3;
    L_0x0ecc:
        r27 = r25;
        r25 = r26;
        r26 = r23;
        r23 = r10;
        goto L_0x11f5;
    L_0x0ed6:
        r35 = r5;
        r38 = r10;
        r20 = r14;
        r10 = r23;
        r36 = r24;
        r23 = r26;
        r45 = r32;
        r24 = r15;
        r26 = r25;
        r25 = r27;
        r14 = r2;
        r2 = r6;
        r27 = r9;
        r3 = r28;
        r28 = r7;
        r9 = r8;
    L_0x0ef3:
        if (r0 == 0) goto L_0x0efa;
    L_0x0ef5:
        r13 = (org.telegram.tgnet.TLRPC.TL_updateNewMessage) r13;
        r0 = r13.message;
        goto L_0x0var_;
    L_0x0efa:
        r0 = r13;
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r0;
        r0 = r0.message;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0f1c;
    L_0x0var_:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r13);
        r5.append(r1);
        r1 = r0.to_id;
        r1 = r1.channel_id;
        r5.append(r1);
        r1 = r5.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0f1c:
        r1 = r0.out;
        if (r1 != 0) goto L_0x0var_;
    L_0x0var_:
        r1 = r0.from_id;
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getClientUserId();
        if (r1 != r5) goto L_0x0var_;
    L_0x0f2e:
        r1 = 1;
        r0.out = r1;
    L_0x0var_:
        r1 = r0.to_id;
        r13 = r1.channel_id;
        if (r13 == 0) goto L_0x0var_;
    L_0x0var_:
        r1 = 0;
        goto L_0x0var_;
    L_0x0var_:
        r13 = r1.chat_id;
        if (r13 == 0) goto L_0x0f3e;
    L_0x0f3d:
        goto L_0x0var_;
    L_0x0f3e:
        r13 = r1.user_id;
        if (r13 == 0) goto L_0x0var_;
    L_0x0var_:
        r1 = r13;
        goto L_0x0var_;
    L_0x0var_:
        r1 = 0;
    L_0x0var_:
        r13 = 0;
    L_0x0var_:
        if (r13 == 0) goto L_0x0f6f;
    L_0x0var_:
        r5 = java.lang.Integer.valueOf(r13);
        r8 = r20;
        r5 = r8.get(r5);
        r5 = (org.telegram.tgnet.TLRPC.Chat) r5;
        if (r5 != 0) goto L_0x0f5e;
    L_0x0var_:
        r5 = java.lang.Integer.valueOf(r13);
        r5 = r11.getChat(r5);
    L_0x0f5e:
        if (r5 != 0) goto L_0x0var_;
    L_0x0var_:
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getChatSync(r13);
        r6 = 1;
        r11.putChat(r5, r6);
        goto L_0x0var_;
    L_0x0f6f:
        r8 = r20;
        r5 = 0;
    L_0x0var_:
        if (r24 == 0) goto L_0x1061;
    L_0x0var_:
        if (r13 == 0) goto L_0x0var_;
    L_0x0var_:
        if (r5 != 0) goto L_0x0var_;
    L_0x0var_:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0var_;
    L_0x0f7c:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "not found chat ";
        r0.append(r1);
        r0.append(r13);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0var_:
        r0 = 0;
        return r0;
    L_0x0var_:
        r6 = r0.entities;
        r6 = r6.size();
        r6 = r6 + 3;
        r7 = r1;
        r1 = 0;
    L_0x0f9c:
        if (r1 >= r6) goto L_0x1061;
    L_0x0f9e:
        if (r1 == 0) goto L_0x0fcc;
    L_0x0fa0:
        r13 = 1;
        if (r1 != r13) goto L_0x0fab;
    L_0x0fa3:
        r7 = r0.from_id;
        r13 = r0.post;
        if (r13 == 0) goto L_0x0fcc;
    L_0x0fa9:
        r13 = 1;
        goto L_0x0fcd;
    L_0x0fab:
        r13 = 2;
        if (r1 != r13) goto L_0x0fb7;
    L_0x0fae:
        r7 = r0.fwd_from;
        if (r7 == 0) goto L_0x0fb5;
    L_0x0fb2:
        r7 = r7.from_id;
        goto L_0x0fcc;
    L_0x0fb5:
        r7 = 0;
        goto L_0x0fcc;
    L_0x0fb7:
        r7 = r0.entities;
        r13 = r1 + -3;
        r7 = r7.get(r13);
        r7 = (org.telegram.tgnet.TLRPC.MessageEntity) r7;
        r13 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r13 == 0) goto L_0x0fca;
    L_0x0fc5:
        r7 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r7;
        r13 = r7.user_id;
        goto L_0x0fcb;
    L_0x0fca:
        r13 = 0;
    L_0x0fcb:
        r7 = r13;
    L_0x0fcc:
        r13 = 0;
    L_0x0fcd:
        if (r7 <= 0) goto L_0x1055;
    L_0x0fcf:
        r32 = r3;
        r3 = java.lang.Integer.valueOf(r7);
        r3 = r4.get(r3);
        r3 = (org.telegram.tgnet.TLRPC.User) r3;
        if (r3 == 0) goto L_0x0fe9;
    L_0x0fdd:
        if (r13 != 0) goto L_0x0fe6;
    L_0x0fdf:
        r20 = r6;
        r6 = r3.min;
        if (r6 == 0) goto L_0x0ff3;
    L_0x0fe5:
        goto L_0x0feb;
    L_0x0fe6:
        r20 = r6;
        goto L_0x0ff3;
    L_0x0fe9:
        r20 = r6;
    L_0x0feb:
        r3 = java.lang.Integer.valueOf(r7);
        r3 = r11.getUser(r3);
    L_0x0ff3:
        if (r3 == 0) goto L_0x0ffb;
    L_0x0ff5:
        if (r13 != 0) goto L_0x1012;
    L_0x0ff7:
        r6 = r3.min;
        if (r6 == 0) goto L_0x1012;
    L_0x0ffb:
        r3 = r11.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r3 = r3.getUserSync(r7);
        if (r3 == 0) goto L_0x100e;
    L_0x1007:
        if (r13 != 0) goto L_0x100e;
    L_0x1009:
        r6 = r3.min;
        if (r6 == 0) goto L_0x100e;
    L_0x100d:
        r3 = 0;
    L_0x100e:
        r6 = 1;
        r11.putUser(r3, r6);
    L_0x1012:
        if (r3 != 0) goto L_0x102e;
    L_0x1014:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x102c;
    L_0x1018:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "not found user ";
        r0.append(r1);
        r0.append(r7);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x102c:
        r0 = 0;
        return r0;
    L_0x102e:
        r6 = 1;
        if (r1 != r6) goto L_0x1059;
    L_0x1031:
        r3 = r3.status;
        if (r3 == 0) goto L_0x1059;
    L_0x1035:
        r3 = r3.expires;
        if (r3 > 0) goto L_0x1059;
    L_0x1039:
        r3 = r11.onlinePrivacy;
        r6 = java.lang.Integer.valueOf(r7);
        r13 = r11.currentAccount;
        r13 = org.telegram.tgnet.ConnectionsManager.getInstance(r13);
        r13 = r13.getCurrentTime();
        r13 = java.lang.Integer.valueOf(r13);
        r3.put(r6, r13);
        r3 = r28 | 4;
        r28 = r3;
        goto L_0x1059;
    L_0x1055:
        r32 = r3;
        r20 = r6;
    L_0x1059:
        r1 = r1 + 1;
        r6 = r20;
        r3 = r32;
        goto L_0x0f9c;
    L_0x1061:
        r32 = r3;
        if (r5 == 0) goto L_0x1070;
    L_0x1065:
        r1 = r5.megagroup;
        if (r1 == 0) goto L_0x1070;
    L_0x1069:
        r1 = r0.flags;
        r3 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r1 = r1 | r3;
        r0.flags = r1;
    L_0x1070:
        r1 = r0.action;
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r3 == 0) goto L_0x10c6;
    L_0x1076:
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r4.get(r1);
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        if (r1 == 0) goto L_0x1096;
    L_0x1084:
        r1 = r1.bot;
        if (r1 == 0) goto L_0x1096;
    L_0x1088:
        r1 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r1.<init>();
        r0.reply_markup = r1;
        r1 = r0.flags;
        r1 = r1 | 64;
        r0.flags = r1;
        goto L_0x10c6;
    L_0x1096:
        r1 = r0.from_id;
        r3 = r11.currentAccount;
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);
        r3 = r3.getClientUserId();
        if (r1 != r3) goto L_0x10c6;
    L_0x10a4:
        r1 = r0.action;
        r1 = r1.user_id;
        r3 = r11.currentAccount;
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);
        r3 = r3.getClientUserId();
        if (r1 != r3) goto L_0x10c6;
    L_0x10b4:
        r6 = r4;
        r19 = r8;
        r8 = r9;
        r21 = r14;
        r9 = r27;
        r7 = r28;
        r28 = r32;
        r5 = r35;
        r32 = r45;
        goto L_0x0ecc;
    L_0x10c6:
        if (r18 != 0) goto L_0x10cd;
    L_0x10c8:
        r18 = new java.util.ArrayList;
        r18.<init>();
    L_0x10cd:
        r1 = r18;
        r1.add(r0);
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r0);
        r3 = r11.currentAccount;
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);
        r3 = r3.getClientUserId();
        r6 = r0.to_id;
        r7 = r6.chat_id;
        if (r7 == 0) goto L_0x10ea;
    L_0x10e5:
        r6 = -r7;
        r6 = (long) r6;
        r0.dialog_id = r6;
        goto L_0x1102;
    L_0x10ea:
        r7 = r6.channel_id;
        if (r7 == 0) goto L_0x10f3;
    L_0x10ee:
        r6 = -r7;
        r6 = (long) r6;
        r0.dialog_id = r6;
        goto L_0x1102;
    L_0x10f3:
        r7 = r6.user_id;
        if (r7 != r3) goto L_0x10fb;
    L_0x10f7:
        r7 = r0.from_id;
        r6.user_id = r7;
    L_0x10fb:
        r6 = r0.to_id;
        r6 = r6.user_id;
        r6 = (long) r6;
        r0.dialog_id = r6;
    L_0x1102:
        r6 = r0.out;
        if (r6 == 0) goto L_0x1109;
    L_0x1106:
        r6 = r11.dialogs_read_outbox_max;
        goto L_0x110b;
    L_0x1109:
        r6 = r11.dialogs_read_inbox_max;
    L_0x110b:
        r33 = r8;
        r7 = r0.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r7 = r6.get(r7);
        r7 = (java.lang.Integer) r7;
        if (r7 != 0) goto L_0x113a;
    L_0x111b:
        r7 = r11.currentAccount;
        r7 = org.telegram.messenger.MessagesStorage.getInstance(r7);
        r8 = r0.out;
        r17 = r9;
        r13 = r10;
        r9 = r0.dialog_id;
        r7 = r7.getDialogReadMax(r8, r9);
        r7 = java.lang.Integer.valueOf(r7);
        r8 = r0.dialog_id;
        r8 = java.lang.Long.valueOf(r8);
        r6.put(r8, r7);
        goto L_0x113d;
    L_0x113a:
        r17 = r9;
        r13 = r10;
    L_0x113d:
        r6 = r7.intValue();
        r7 = r0.id;
        if (r6 >= r7) goto L_0x1159;
    L_0x1145:
        if (r5 == 0) goto L_0x114d;
    L_0x1147:
        r5 = org.telegram.messenger.ChatObject.isNotInChat(r5);
        if (r5 != 0) goto L_0x1159;
    L_0x114d:
        r5 = r0.action;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r6 != 0) goto L_0x1159;
    L_0x1153:
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r5 != 0) goto L_0x1159;
    L_0x1157:
        r5 = 1;
        goto L_0x115a;
    L_0x1159:
        r5 = 0;
    L_0x115a:
        r0.unread = r5;
        r5 = r0.dialog_id;
        r7 = (long) r3;
        r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r3 != 0) goto L_0x116c;
    L_0x1163:
        r3 = 0;
        r0.unread = r3;
        r0.media_unread = r3;
        r5 = 1;
        r0.out = r5;
        goto L_0x116d;
    L_0x116c:
        r3 = 0;
    L_0x116d:
        r10 = new org.telegram.messenger.MessageObject;
        r6 = r11.currentAccount;
        r5 = r11.createdDialogIds;
        r7 = r0.dialog_id;
        r7 = java.lang.Long.valueOf(r7);
        r18 = r5.contains(r7);
        r5 = r10;
        r7 = r0;
        r19 = r33;
        r8 = r4;
        r20 = r13;
        r13 = r17;
        r9 = r19;
        r21 = r14;
        r14 = r20;
        r15 = r10;
        r10 = r18;
        r5.<init>(r6, r7, r8, r9, r10);
        r5 = r15.type;
        r6 = 11;
        if (r5 != r6) goto L_0x119c;
    L_0x1198:
        r5 = r28 | 8;
    L_0x119a:
        r7 = r5;
        goto L_0x11a5;
    L_0x119c:
        r6 = 10;
        if (r5 != r6) goto L_0x11a3;
    L_0x11a0:
        r5 = r28 | 16;
        goto L_0x119a;
    L_0x11a3:
        r7 = r28;
    L_0x11a5:
        if (r27 != 0) goto L_0x11ad;
    L_0x11a7:
        r9 = new android.util.LongSparseArray;
        r9.<init>();
        goto L_0x11af;
    L_0x11ad:
        r9 = r27;
    L_0x11af:
        r5 = r0.dialog_id;
        r5 = r9.get(r5);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x11c5;
    L_0x11b9:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r6 = r4;
        r3 = r0.dialog_id;
        r9.put(r3, r5);
        goto L_0x11c6;
    L_0x11c5:
        r6 = r4;
    L_0x11c6:
        r5.add(r15);
        r0 = r15.isOut();
        if (r0 != 0) goto L_0x11e2;
    L_0x11cf:
        r0 = r15.isUnread();
        if (r0 == 0) goto L_0x11e2;
    L_0x11d5:
        if (r2 != 0) goto L_0x11dd;
    L_0x11d7:
        r0 = new java.util.ArrayList;
        r0.<init>();
        goto L_0x11de;
    L_0x11dd:
        r0 = r2;
    L_0x11de:
        r0.add(r15);
        goto L_0x11e3;
    L_0x11e2:
        r0 = r2;
    L_0x11e3:
        r2 = r0;
        r18 = r1;
        r8 = r13;
        r27 = r25;
        r25 = r26;
        r28 = r32;
        r5 = r35;
        r32 = r45;
        r26 = r23;
        r23 = r14;
    L_0x11f5:
        r10 = r38 + 1;
        r0 = r51;
        r4 = r6;
        r14 = r19;
        r15 = r24;
        r24 = r36;
        r6 = r2;
        r2 = r21;
        goto L_0x009d;
    L_0x1205:
        r35 = r5;
        r2 = r6;
        r13 = r8;
        r14 = r23;
        r36 = r24;
        r23 = r26;
        r45 = r32;
        r6 = r4;
        r26 = r25;
        r25 = r27;
        r32 = r28;
        r28 = r7;
        r27 = r9;
        if (r27 == 0) goto L_0x123e;
    L_0x121e:
        r0 = r27.size();
        r1 = 0;
    L_0x1223:
        if (r1 >= r0) goto L_0x123e;
    L_0x1225:
        r9 = r27;
        r3 = r9.keyAt(r1);
        r5 = r9.valueAt(r1);
        r5 = (java.util.ArrayList) r5;
        r3 = r11.updatePrintingUsersWithNewMessages(r3, r5);
        if (r3 == 0) goto L_0x1239;
    L_0x1237:
        r16 = 1;
    L_0x1239:
        r1 = r1 + 1;
        r27 = r9;
        goto L_0x1223;
    L_0x123e:
        r9 = r27;
        r7 = r16;
        if (r7 == 0) goto L_0x1247;
    L_0x1244:
        r48.updatePrintingStrings();
    L_0x1247:
        if (r13 == 0) goto L_0x1252;
    L_0x1249:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.ContactsController.getInstance(r0);
        r0.processContactsUpdates(r13, r6);
    L_0x1252:
        if (r2 == 0) goto L_0x1266;
    L_0x1254:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0 = r0.getStorageQueue();
        r1 = new org.telegram.messenger.-$$Lambda$MessagesController$rmpWNkBwNMY39XhMVE_pMAwyvyA;
        r1.<init>(r11, r2);
        r0.postRunnable(r1);
    L_0x1266:
        if (r18 == 0) goto L_0x1293;
    L_0x1268:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.StatsController.getInstance(r0);
        r1 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType();
        r2 = r18.size();
        r3 = 1;
        r0.incrementReceivedItemsCount(r1, r3, r2);
        r0 = r11.currentAccount;
        r17 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r19 = 1;
        r20 = 1;
        r21 = 0;
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r22 = r0.getAutodownloadMask();
        r17.putMessages(r18, r19, r20, r21, r22);
    L_0x1293:
        if (r14 == 0) goto L_0x12d6;
    L_0x1295:
        r0 = r14.size();
        r1 = 0;
    L_0x129a:
        if (r1 >= r0) goto L_0x12d6;
    L_0x129c:
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
        r2.<init>();
        r3 = r14.valueAt(r1);
        r3 = (java.util.ArrayList) r3;
        r4 = r3.size();
        r5 = 0;
    L_0x12ac:
        if (r5 >= r4) goto L_0x12be;
    L_0x12ae:
        r6 = r2.messages;
        r8 = r3.get(r5);
        r8 = (org.telegram.messenger.MessageObject) r8;
        r8 = r8.messageOwner;
        r6.add(r8);
        r5 = r5 + 1;
        goto L_0x12ac;
    L_0x12be:
        r3 = r11.currentAccount;
        r15 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r17 = r14.keyAt(r1);
        r19 = -2;
        r20 = 0;
        r21 = 0;
        r16 = r2;
        r15.putMessages(r16, r17, r19, r20, r21);
        r1 = r1 + 1;
        goto L_0x129a;
    L_0x12d6:
        if (r23 == 0) goto L_0x12e5;
    L_0x12d8:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r10 = r23;
        r1 = 1;
        r0.putChannelViews(r10, r1);
        goto L_0x12e7;
    L_0x12e5:
        r10 = r23;
    L_0x12e7:
        r15 = new org.telegram.messenger.-$$Lambda$MessagesController$aIzxVN1fNX7ISomIdQom833zn_c;
        r0 = r15;
        r1 = r48;
        r2 = r28;
        r8 = r32;
        r16 = 0;
        r3 = r31;
        r4 = r26;
        r5 = r9;
        r6 = r14;
        r14 = r8;
        r8 = r13;
        r9 = r30;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r15);
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r8 = r0.getStorageQueue();
        r9 = new org.telegram.messenger.-$$Lambda$MessagesController$ZCTDymAyINyEW9FoTNxhmaMSvzk;
        r0 = r9;
        r2 = r12;
        r3 = r36;
        r4 = r25;
        r5 = r35;
        r6 = r14;
        r7 = r29;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        r8.postRunnable(r9);
        r1 = r26;
        if (r1 == 0) goto L_0x132c;
    L_0x1323:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.putWebPages(r1);
    L_0x132c:
        if (r12 != 0) goto L_0x1337;
    L_0x132e:
        if (r36 != 0) goto L_0x1337;
    L_0x1330:
        if (r25 != 0) goto L_0x1337;
    L_0x1332:
        r1 = r35;
        if (r1 == 0) goto L_0x1359;
    L_0x1336:
        goto L_0x1339;
    L_0x1337:
        r1 = r35;
    L_0x1339:
        if (r12 != 0) goto L_0x1342;
    L_0x133b:
        if (r1 == 0) goto L_0x133e;
    L_0x133d:
        goto L_0x1342;
    L_0x133e:
        r2 = r36;
        r3 = 1;
        goto L_0x134e;
    L_0x1342:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r2 = r36;
        r3 = 1;
        r0.updateDialogsWithReadMessages(r12, r2, r1, r3);
    L_0x134e:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r4 = r25;
        r0.markMessagesAsRead(r12, r2, r4, r3);
    L_0x1359:
        if (r1 == 0) goto L_0x136e;
    L_0x135b:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r2 = r11.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        r0.markMessagesContentAsRead(r1, r2);
    L_0x136e:
        if (r14 == 0) goto L_0x1396;
    L_0x1370:
        r0 = r14.size();
        r1 = 0;
    L_0x1375:
        if (r1 >= r0) goto L_0x1396;
    L_0x1377:
        r2 = r14.keyAt(r1);
        r3 = r14.valueAt(r1);
        r3 = (java.util.ArrayList) r3;
        r4 = r11.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getStorageQueue();
        r5 = new org.telegram.messenger.-$$Lambda$MessagesController$eIE8R8MYjUQYKP1gLWpaSncXEb8;
        r5.<init>(r11, r3, r2);
        r4.postRunnable(r5);
        r1 = r1 + 1;
        goto L_0x1375;
    L_0x1396:
        if (r29 == 0) goto L_0x13be;
    L_0x1398:
        r0 = r29.size();
        r1 = 0;
    L_0x139d:
        if (r1 >= r0) goto L_0x13be;
    L_0x139f:
        r2 = r29;
        r3 = r2.keyAt(r1);
        r4 = r2.valueAt(r1);
        r5 = r11.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getStorageQueue();
        r6 = new org.telegram.messenger.-$$Lambda$MessagesController$H4wHvQqPFocrQ-yPWuW6Ehm2l3E;
        r6.<init>(r11, r3, r4);
        r5.postRunnable(r6);
        r1 = r1 + 1;
        goto L_0x139d;
    L_0x13be:
        r1 = r45;
        if (r1 == 0) goto L_0x13e3;
    L_0x13c2:
        r0 = r1.size();
        r2 = 0;
    L_0x13c7:
        if (r2 >= r0) goto L_0x13e3;
    L_0x13c9:
        r3 = r1.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r3;
        r4 = r11.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r6 = r3.chat_id;
        r7 = r3.max_date;
        r8 = r3.date;
        r9 = 1;
        r10 = 0;
        r5.createTaskForSecretChat(r6, r7, r8, r9, r10);
        r2 = r2 + 1;
        goto L_0x13c7;
    L_0x13e3:
        r0 = 1;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdateArray(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, boolean):boolean");
    }

    public /* synthetic */ void lambda$processUpdateArray$238$MessagesController(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public /* synthetic */ void lambda$processUpdateArray$239$MessagesController(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public /* synthetic */ void lambda$processUpdateArray$241$MessagesController(TL_updateUserBlocked tL_updateUserBlocked) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$IE61P-jfCib0uHF9uB4VvmEoPuE(this, tL_updateUserBlocked));
    }

    public /* synthetic */ void lambda$null$240$MessagesController(TL_updateUserBlocked tL_updateUserBlocked) {
        if (!tL_updateUserBlocked.blocked) {
            this.blockedUsers.delete(tL_updateUserBlocked.user_id);
        } else if (this.blockedUsers.indexOfKey(tL_updateUserBlocked.user_id) < 0) {
            this.blockedUsers.put(tL_updateUserBlocked.user_id, 1);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdateArray$242$MessagesController(TL_updateServiceNotification tL_updateServiceNotification) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(2), tL_updateServiceNotification.message, tL_updateServiceNotification.type);
    }

    public /* synthetic */ void lambda$processUpdateArray$243$MessagesController(TL_updateLangPack tL_updateLangPack) {
        LocaleController.getInstance().saveRemoteLocaleStringsForCurrentLocale(tL_updateLangPack.difference, this.currentAccount);
    }

    public /* synthetic */ void lambda$null$244$MessagesController(ArrayList arrayList) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$processUpdateArray$245$MessagesController(ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$C4rRzL3XbhcTq3487s-diR-40t4(this, arrayList));
    }

    /* JADX WARNING: Removed duplicated region for block: B:358:0x08a4  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x0947  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0956  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x095a  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0962  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x098e  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x097d  */
    /* JADX WARNING: Removed duplicated region for block: B:434:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0991  */
    public /* synthetic */ void lambda$processUpdateArray$250$MessagesController(int r31, java.util.ArrayList r32, android.util.LongSparseArray r33, android.util.LongSparseArray r34, android.util.LongSparseArray r35, boolean r36, java.util.ArrayList r37, java.util.ArrayList r38, android.util.SparseArray r39) {
        /*
        r30 = this;
        r8 = r30;
        r9 = r32;
        r10 = r33;
        r11 = r34;
        r12 = r35;
        r13 = r38;
        r14 = 2;
        r5 = 1;
        r4 = 0;
        if (r9 == 0) goto L_0x07ae;
    L_0x0011:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r1 = r32.size();
        r18 = r31;
        r15 = 0;
        r16 = 0;
        r17 = 0;
    L_0x0026:
        if (r15 >= r1) goto L_0x077b;
    L_0x0028:
        r0 = r9.get(r15);
        r0 = (org.telegram.tgnet.TLRPC.Update) r0;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePrivacy;
        if (r6 == 0) goto L_0x0082;
    L_0x0032:
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePrivacy) r0;
        r6 = r0.key;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
        if (r7 == 0) goto L_0x0046;
    L_0x003a:
        r6 = r8.currentAccount;
        r6 = org.telegram.messenger.ContactsController.getInstance(r6);
        r0 = r0.rules;
        r6.setPrivacyRules(r0, r4);
        goto L_0x0076;
    L_0x0046:
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite;
        if (r7 == 0) goto L_0x0056;
    L_0x004a:
        r6 = r8.currentAccount;
        r6 = org.telegram.messenger.ContactsController.getInstance(r6);
        r0 = r0.rules;
        r6.setPrivacyRules(r0, r5);
        goto L_0x0076;
    L_0x0056:
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneCall;
        if (r7 == 0) goto L_0x0066;
    L_0x005a:
        r6 = r8.currentAccount;
        r6 = org.telegram.messenger.ContactsController.getInstance(r6);
        r0 = r0.rules;
        r6.setPrivacyRules(r0, r14);
        goto L_0x0076;
    L_0x0066:
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneP2P;
        if (r6 == 0) goto L_0x0076;
    L_0x006a:
        r6 = r8.currentAccount;
        r6 = org.telegram.messenger.ContactsController.getInstance(r6);
        r0 = r0.rules;
        r7 = 3;
        r6.setPrivacyRules(r0, r7);
    L_0x0076:
        r22 = r1;
        r14 = r2;
        r23 = r3;
        r1 = r18;
        r3 = 0;
        r12 = 0;
        goto L_0x0765;
    L_0x0082:
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserStatus;
        if (r6 == 0) goto L_0x00e4;
    L_0x0086:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserStatus) r0;
        r6 = r0.user_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r8.getUser(r6);
        r7 = r0.status;
        r4 = r7 instanceof org.telegram.tgnet.TLRPC.TL_userStatusRecently;
        if (r4 == 0) goto L_0x009d;
    L_0x0098:
        r4 = -100;
        r7.expires = r4;
        goto L_0x00ae;
    L_0x009d:
        r4 = r7 instanceof org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
        if (r4 == 0) goto L_0x00a6;
    L_0x00a1:
        r4 = -101; // 0xffffffffffffff9b float:NaN double:NaN;
        r7.expires = r4;
        goto L_0x00ae;
    L_0x00a6:
        r4 = r7 instanceof org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
        if (r4 == 0) goto L_0x00ae;
    L_0x00aa:
        r4 = -102; // 0xffffffffffffff9a float:NaN double:NaN;
        r7.expires = r4;
    L_0x00ae:
        if (r6 == 0) goto L_0x00b8;
    L_0x00b0:
        r4 = r0.user_id;
        r6.id = r4;
        r4 = r0.status;
        r6.status = r4;
    L_0x00b8:
        r4 = new org.telegram.tgnet.TLRPC$TL_user;
        r4.<init>();
        r6 = r0.user_id;
        r4.id = r6;
        r6 = r0.status;
        r4.status = r6;
        r3.add(r4);
        r4 = r0.user_id;
        r6 = r8.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        if (r4 != r6) goto L_0x0076;
    L_0x00d6:
        r4 = r8.currentAccount;
        r4 = org.telegram.messenger.NotificationsController.getInstance(r4);
        r0 = r0.status;
        r0 = r0.expires;
        r4.setLastOnlineFromOtherDevice(r0);
        goto L_0x0076;
    L_0x00e4:
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r4 == 0) goto L_0x0140;
    L_0x00e8:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserName) r0;
        r4 = r0.user_id;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r8.getUser(r4);
        if (r4 == 0) goto L_0x0126;
    L_0x00f6:
        r6 = org.telegram.messenger.UserObject.isContact(r4);
        if (r6 != 0) goto L_0x0104;
    L_0x00fc:
        r6 = r0.first_name;
        r4.first_name = r6;
        r6 = r0.last_name;
        r4.last_name = r6;
    L_0x0104:
        r6 = r4.username;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 != 0) goto L_0x0113;
    L_0x010c:
        r6 = r8.objectsByUsernames;
        r7 = r4.username;
        r6.remove(r7);
    L_0x0113:
        r6 = r0.username;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 == 0) goto L_0x0122;
    L_0x011b:
        r6 = r8.objectsByUsernames;
        r7 = r0.username;
        r6.put(r7, r4);
    L_0x0122:
        r6 = r0.username;
        r4.username = r6;
    L_0x0126:
        r4 = new org.telegram.tgnet.TLRPC$TL_user;
        r4.<init>();
        r6 = r0.user_id;
        r4.id = r6;
        r6 = r0.first_name;
        r4.first_name = r6;
        r6 = r0.last_name;
        r4.last_name = r6;
        r0 = r0.username;
        r4.username = r0;
        r2.add(r4);
        goto L_0x0076;
    L_0x0140:
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
        if (r4 == 0) goto L_0x0192;
    L_0x0144:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateDialogPinned) r0;
        r4 = r0.peer;
        r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer;
        if (r6 == 0) goto L_0x0155;
    L_0x014c:
        r4 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r4;
        r4 = r4.peer;
        r6 = org.telegram.messenger.DialogObject.getPeerDialogId(r4);
        goto L_0x0157;
    L_0x0155:
        r6 = 0;
    L_0x0157:
        r4 = r0.pinned;
        r19 = 0;
        r20 = -1;
        r22 = r1;
        r1 = r30;
        r14 = r2;
        r23 = r3;
        r2 = r6;
        r9 = 1;
        r5 = r19;
        r9 = r6;
        r12 = 0;
        r6 = r20;
        r1 = r1.pinDialog(r2, r4, r5, r6);
        if (r1 != 0) goto L_0x0190;
    L_0x0173:
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r2 = r0.folder_id;
        r3 = 0;
        r1.setPinnedDialogsLoaded(r2, r3);
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1.saveConfig(r3);
        r0 = r0.folder_id;
        r1 = 0;
        r8.loadPinnedDialogs(r0, r9, r1);
        goto L_0x01f8;
    L_0x0190:
        r3 = 0;
        goto L_0x01f8;
    L_0x0192:
        r22 = r1;
        r14 = r2;
        r23 = r3;
        r3 = 0;
        r12 = 0;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs;
        if (r1 == 0) goto L_0x01fc;
    L_0x019e:
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs) r0;
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r2 = r0.folder_id;
        r1.setPinnedDialogsLoaded(r2, r3);
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1.saveConfig(r3);
        r1 = r0.flags;
        r2 = 1;
        r1 = r1 & r2;
        if (r1 == 0) goto L_0x01f2;
    L_0x01ba:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.order;
        r4 = r2.size();
        r5 = 0;
    L_0x01c6:
        if (r5 >= r4) goto L_0x01f3;
    L_0x01c8:
        r6 = r2.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.DialogPeer) r6;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer;
        if (r7 == 0) goto L_0x01e7;
    L_0x01d2:
        r6 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r6;
        r6 = r6.peer;
        r7 = r6.user_id;
        if (r7 == 0) goto L_0x01dc;
    L_0x01da:
        r6 = (long) r7;
        goto L_0x01e8;
    L_0x01dc:
        r7 = r6.chat_id;
        if (r7 == 0) goto L_0x01e2;
    L_0x01e0:
        r6 = -r7;
        goto L_0x01e5;
    L_0x01e2:
        r6 = r6.channel_id;
        r6 = -r6;
    L_0x01e5:
        r6 = (long) r6;
        goto L_0x01e8;
    L_0x01e7:
        r6 = r12;
    L_0x01e8:
        r6 = java.lang.Long.valueOf(r6);
        r1.add(r6);
        r5 = r5 + 1;
        goto L_0x01c6;
    L_0x01f2:
        r1 = 0;
    L_0x01f3:
        r0 = r0.folder_id;
        r8.loadPinnedDialogs(r0, r12, r1);
    L_0x01f8:
        r1 = r18;
        goto L_0x0765;
    L_0x01fc:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateFolderPeers;
        if (r1 == 0) goto L_0x0233;
    L_0x0200:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateFolderPeers) r0;
        r1 = r0.folder_peers;
        r1 = r1.size();
        r2 = 0;
    L_0x0209:
        if (r2 >= r1) goto L_0x022f;
    L_0x020b:
        r4 = r0.folder_peers;
        r4 = r4.get(r2);
        r4 = (org.telegram.tgnet.TLRPC.TL_folderPeer) r4;
        r5 = r4.peer;
        r5 = org.telegram.messenger.DialogObject.getPeerDialogId(r5);
        r7 = r8.dialogs_dict;
        r5 = r7.get(r5);
        r5 = (org.telegram.tgnet.TLRPC.Dialog) r5;
        if (r5 != 0) goto L_0x0224;
    L_0x0223:
        goto L_0x022c;
    L_0x0224:
        r4 = r4.folder_id;
        r5.folder_id = r4;
        r5 = 0;
        r8.ensureFolderDialogExists(r4, r5);
    L_0x022c:
        r2 = r2 + 1;
        goto L_0x0209;
    L_0x022f:
        r17 = 1;
        goto L_0x0767;
    L_0x0233:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
        if (r1 == 0) goto L_0x025a;
    L_0x0237:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhoto) r0;
        r1 = r0.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r8.getUser(r1);
        if (r1 == 0) goto L_0x0249;
    L_0x0245:
        r2 = r0.photo;
        r1.photo = r2;
    L_0x0249:
        r1 = new org.telegram.tgnet.TLRPC$TL_user;
        r1.<init>();
        r2 = r0.user_id;
        r1.id = r2;
        r0 = r0.photo;
        r1.photo = r0;
        r14.add(r1);
        goto L_0x01f8;
    L_0x025a:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhone;
        if (r1 == 0) goto L_0x028c;
    L_0x025e:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateUserPhone) r0;
        r1 = r0.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r8.getUser(r1);
        if (r1 == 0) goto L_0x027a;
    L_0x026c:
        r2 = r0.phone;
        r1.phone = r2;
        r2 = org.telegram.messenger.Utilities.phoneBookQueue;
        r4 = new org.telegram.messenger.-$$Lambda$MessagesController$OhbqgzlD12qA4K124X1JhSsf9bA;
        r4.<init>(r8, r1);
        r2.postRunnable(r4);
    L_0x027a:
        r1 = new org.telegram.tgnet.TLRPC$TL_user;
        r1.<init>();
        r2 = r0.user_id;
        r1.id = r2;
        r0 = r0.phone;
        r1.phone = r0;
        r14.add(r1);
        goto L_0x01f8;
    L_0x028c:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
        if (r1 == 0) goto L_0x0432;
    L_0x0290:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNotifySettings) r0;
        r1 = r0.notify_settings;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
        if (r1 == 0) goto L_0x0767;
    L_0x0298:
        if (r16 != 0) goto L_0x02a0;
    L_0x029a:
        r1 = r8.notificationsPreferences;
        r16 = r1.edit();
    L_0x02a0:
        r1 = r16;
        r2 = r8.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        r4 = r0.peer;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyPeer;
        if (r5 == 0) goto L_0x03c6;
    L_0x02b2:
        r4 = (org.telegram.tgnet.TLRPC.TL_notifyPeer) r4;
        r4 = r4.peer;
        r5 = r4.user_id;
        if (r5 == 0) goto L_0x02bc;
    L_0x02ba:
        r4 = (long) r5;
        goto L_0x02c6;
    L_0x02bc:
        r5 = r4.chat_id;
        if (r5 == 0) goto L_0x02c2;
    L_0x02c0:
        r4 = -r5;
        goto L_0x02c5;
    L_0x02c2:
        r4 = r4.channel_id;
        r4 = -r4;
    L_0x02c5:
        r4 = (long) r4;
    L_0x02c6:
        r6 = r8.dialogs_dict;
        r6 = r6.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.Dialog) r6;
        if (r6 == 0) goto L_0x02d4;
    L_0x02d0:
        r7 = r0.notify_settings;
        r6.notify_settings = r7;
    L_0x02d4:
        r7 = r0.notify_settings;
        r7 = r7.flags;
        r9 = 2;
        r7 = r7 & r9;
        r9 = "silent_";
        if (r7 == 0) goto L_0x02f5;
    L_0x02de:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r9);
        r7.append(r4);
        r7 = r7.toString();
        r9 = r0.notify_settings;
        r9 = r9.silent;
        r1.putBoolean(r7, r9);
        goto L_0x0307;
    L_0x02f5:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r9);
        r7.append(r4);
        r7 = r7.toString();
        r1.remove(r7);
    L_0x0307:
        r7 = r0.notify_settings;
        r9 = r7.flags;
        r9 = r9 & 4;
        r10 = "notify2_";
        if (r9 == 0) goto L_0x03a6;
    L_0x0311:
        r9 = r7.mute_until;
        if (r9 <= r2) goto L_0x0385;
    L_0x0315:
        r7 = 31536000; // 0x1e13380 float:8.2725845E-38 double:1.5580854E-316;
        r2 = r2 + r7;
        if (r9 <= r2) goto L_0x0339;
    L_0x031b:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r10);
        r2.append(r4);
        r2 = r2.toString();
        r7 = 2;
        r1.putInt(r2, r7);
        if (r6 == 0) goto L_0x0337;
    L_0x0330:
        r0 = r0.notify_settings;
        r2 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0.mute_until = r2;
    L_0x0337:
        r9 = 0;
        goto L_0x036a;
    L_0x0339:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r10);
        r2.append(r4);
        r2 = r2.toString();
        r7 = 3;
        r1.putInt(r2, r7);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r7 = "notifyuntil_";
        r2.append(r7);
        r2.append(r4);
        r2 = r2.toString();
        r7 = r0.notify_settings;
        r7 = r7.mute_until;
        r1.putInt(r2, r7);
        if (r6 == 0) goto L_0x036a;
    L_0x0366:
        r0 = r0.notify_settings;
        r0.mute_until = r9;
    L_0x036a:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r6 = (long) r9;
        r2 = 32;
        r6 = r6 << r2;
        r9 = 1;
        r6 = r6 | r9;
        r0.setDialogFlags(r4, r6);
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.NotificationsController.getInstance(r0);
        r0.removeNotificationsForDialog(r4);
        goto L_0x042e;
    L_0x0385:
        if (r6 == 0) goto L_0x0389;
    L_0x0387:
        r7.mute_until = r3;
    L_0x0389:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r10);
        r0.append(r4);
        r0 = r0.toString();
        r1.putInt(r0, r3);
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.setDialogFlags(r4, r12);
        goto L_0x042e;
    L_0x03a6:
        if (r6 == 0) goto L_0x03aa;
    L_0x03a8:
        r7.mute_until = r3;
    L_0x03aa:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r10);
        r0.append(r4);
        r0 = r0.toString();
        r1.remove(r0);
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.setDialogFlags(r4, r12);
        goto L_0x042e;
    L_0x03c6:
        r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyChats;
        if (r2 == 0) goto L_0x03e9;
    L_0x03ca:
        r2 = r0.notify_settings;
        r4 = r2.flags;
        r5 = 1;
        r4 = r4 & r5;
        if (r4 == 0) goto L_0x03d9;
    L_0x03d2:
        r2 = r2.show_previews;
        r4 = "EnablePreviewGroup";
        r1.putBoolean(r4, r2);
    L_0x03d9:
        r0 = r0.notify_settings;
        r2 = r0.flags;
        r2 = r2 & 4;
        if (r2 == 0) goto L_0x042e;
    L_0x03e1:
        r0 = r0.mute_until;
        r2 = "EnableGroup2";
        r1.putInt(r2, r0);
        goto L_0x042e;
    L_0x03e9:
        r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyUsers;
        if (r2 == 0) goto L_0x040c;
    L_0x03ed:
        r2 = r0.notify_settings;
        r4 = r2.flags;
        r5 = 1;
        r4 = r4 & r5;
        if (r4 == 0) goto L_0x03fc;
    L_0x03f5:
        r2 = r2.show_previews;
        r4 = "EnablePreviewAll";
        r1.putBoolean(r4, r2);
    L_0x03fc:
        r0 = r0.notify_settings;
        r2 = r0.flags;
        r2 = r2 & 4;
        if (r2 == 0) goto L_0x042e;
    L_0x0404:
        r0 = r0.mute_until;
        r2 = "EnableAll2";
        r1.putInt(r2, r0);
        goto L_0x042e;
    L_0x040c:
        r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_notifyBroadcasts;
        if (r2 == 0) goto L_0x042e;
    L_0x0410:
        r2 = r0.notify_settings;
        r4 = r2.flags;
        r5 = 1;
        r4 = r4 & r5;
        if (r4 == 0) goto L_0x041f;
    L_0x0418:
        r2 = r2.show_previews;
        r4 = "EnablePreviewChannel";
        r1.putBoolean(r4, r2);
    L_0x041f:
        r0 = r0.notify_settings;
        r2 = r0.flags;
        r2 = r2 & 4;
        if (r2 == 0) goto L_0x042e;
    L_0x0427:
        r0 = r0.mute_until;
        r2 = "EnableChannel2";
        r1.putInt(r2, r0);
    L_0x042e:
        r16 = r1;
        goto L_0x0767;
    L_0x0432:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannel;
        if (r1 == 0) goto L_0x0488;
    L_0x0436:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateChannel) r0;
        r1 = r8.dialogs_dict;
        r2 = r0.channel_id;
        r4 = (long) r2;
        r4 = -r4;
        r1 = r1.get(r4);
        r1 = (org.telegram.tgnet.TLRPC.Dialog) r1;
        r2 = r0.channel_id;
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r8.getChat(r2);
        if (r2 == 0) goto L_0x047c;
    L_0x0450:
        if (r1 != 0) goto L_0x0465;
    L_0x0452:
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channel;
        if (r4 == 0) goto L_0x0465;
    L_0x0456:
        r4 = r2.left;
        if (r4 != 0) goto L_0x0465;
    L_0x045a:
        r1 = org.telegram.messenger.Utilities.stageQueue;
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$gMiG6MCVxnRxisSC8QI4gCOOFM8;
        r2.<init>(r8, r0);
        r1.postRunnable(r2);
        goto L_0x047c;
    L_0x0465:
        r2 = r2.left;
        if (r2 == 0) goto L_0x047c;
    L_0x0469:
        if (r1 == 0) goto L_0x047c;
    L_0x046b:
        r2 = r8.proxyDialog;
        if (r2 == 0) goto L_0x0477;
    L_0x046f:
        r4 = r2.id;
        r6 = r1.id;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 == 0) goto L_0x047c;
    L_0x0477:
        r1 = r1.id;
        r8.deleteDialog(r1, r3);
    L_0x047c:
        r1 = r18;
        r1 = r1 | 8192;
        r0 = r0.channel_id;
        r2 = 1;
        r8.loadFullChat(r0, r3, r2);
        goto L_0x0765;
    L_0x0488:
        r1 = r18;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights;
        if (r2 == 0) goto L_0x04b1;
    L_0x048e:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights) r0;
        r2 = r0.peer;
        r4 = r2.channel_id;
        if (r4 == 0) goto L_0x0497;
    L_0x0496:
        goto L_0x0499;
    L_0x0497:
        r4 = r2.chat_id;
    L_0x0499:
        r2 = java.lang.Integer.valueOf(r4);
        r2 = r8.getChat(r2);
        if (r2 == 0) goto L_0x0765;
    L_0x04a3:
        r0 = r0.default_banned_rights;
        r2.default_banned_rights = r0;
        r0 = new org.telegram.messenger.-$$Lambda$MessagesController$GABj5DmLp4L1Zi8c9sUorNKjQvk;
        r0.<init>(r8, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        goto L_0x0765;
    L_0x04b1:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSets;
        if (r2 == 0) goto L_0x04c3;
    L_0x04b5:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateStickerSets) r0;
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);
        r2 = 1;
        r0.loadStickers(r3, r3, r2);
        goto L_0x0765;
    L_0x04c3:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
        if (r2 == 0) goto L_0x04d8;
    L_0x04c7:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder) r0;
        r2 = r8.currentAccount;
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);
        r4 = r0.masks;
        r0 = r0.order;
        r2.reorderStickers(r4, r0);
        goto L_0x0765;
    L_0x04d8:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
        if (r2 == 0) goto L_0x04e9;
    L_0x04dc:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);
        r2 = 2;
        r4 = 1;
        r0.loadRecents(r2, r3, r3, r4);
        goto L_0x0765;
    L_0x04e9:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateContactsReset;
        if (r2 == 0) goto L_0x04f8;
    L_0x04ed:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.ContactsController.getInstance(r0);
        r0.forceImportContacts();
        goto L_0x0765;
    L_0x04f8:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
        if (r2 == 0) goto L_0x050b;
    L_0x04fc:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateNewStickerSet) r0;
        r2 = r8.currentAccount;
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);
        r0 = r0.stickerset;
        r2.addNewStickerSet(r0);
        goto L_0x0765;
    L_0x050b:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
        if (r2 == 0) goto L_0x0520;
    L_0x050f:
        r0 = r8.emojiPreferences;
        r0 = r0.edit();
        r2 = "lastGifLoadTime";
        r0 = r0.putLong(r2, r12);
        r0.commit();
        goto L_0x0765;
    L_0x0520:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateRecentStickers;
        if (r2 == 0) goto L_0x0535;
    L_0x0524:
        r0 = r8.emojiPreferences;
        r0 = r0.edit();
        r2 = "lastStickersLoadTime";
        r0 = r0.putLong(r2, r12);
        r0.commit();
        goto L_0x0765;
    L_0x0535:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
        if (r2 == 0) goto L_0x0565;
    L_0x0539:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateDraftMessage) r0;
        r2 = r0.peer;
        r4 = r2.user_id;
        if (r4 == 0) goto L_0x0545;
    L_0x0541:
        r4 = (long) r4;
    L_0x0542:
        r25 = r4;
        goto L_0x0550;
    L_0x0545:
        r4 = r2.channel_id;
        if (r4 == 0) goto L_0x054b;
    L_0x0549:
        r2 = -r4;
        goto L_0x054e;
    L_0x054b:
        r2 = r2.chat_id;
        r2 = -r2;
    L_0x054e:
        r4 = (long) r2;
        goto L_0x0542;
    L_0x0550:
        r2 = r8.currentAccount;
        r24 = org.telegram.messenger.DataQuery.getInstance(r2);
        r0 = r0.draft;
        r28 = 0;
        r29 = 1;
        r27 = r0;
        r24.saveDraft(r25, r27, r28, r29);
        r18 = r1;
        goto L_0x022f;
    L_0x0565:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
        if (r2 == 0) goto L_0x0574;
    L_0x0569:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);
        r0.markFaturedStickersAsRead(r3);
        goto L_0x0765;
    L_0x0574:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePhoneCall;
        if (r2 == 0) goto L_0x06d7;
    L_0x0578:
        r0 = (org.telegram.tgnet.TLRPC.TL_updatePhoneCall) r0;
        r0 = r0.phone_call;
        r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance();
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x05ae;
    L_0x0584:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Received call in update: ";
        r4.append(r5);
        r4.append(r0);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "call id ";
        r4.append(r5);
        r5 = r0.id;
        r4.append(r5);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x05ae:
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallRequested;
        if (r4 == 0) goto L_0x06b3;
    L_0x05b2:
        r4 = r0.date;
        r5 = r8.callRingTimeout;
        r5 = r5 / 1000;
        r4 = r4 + r5;
        r5 = r8.currentAccount;
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5);
        r5 = r5.getCurrentTime();
        if (r4 >= r5) goto L_0x05d0;
    L_0x05c5:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0765;
    L_0x05c9:
        r0 = "ignoring too old call";
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x0765;
    L_0x05d0:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x05ed;
    L_0x05d6:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = androidx.core.app.NotificationManagerCompat.from(r4);
        r4 = r4.areNotificationsEnabled();
        if (r4 != 0) goto L_0x05ed;
    L_0x05e2:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0765;
    L_0x05e6:
        r0 = "Ignoring incoming call because notifications are disabled in system";
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x0765;
    L_0x05ed:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = "phone";
        r4 = r4.getSystemService(r5);
        r4 = (android.telephony.TelephonyManager) r4;
        if (r2 != 0) goto L_0x0667;
    L_0x05f9:
        r2 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent;
        if (r2 != 0) goto L_0x0667;
    L_0x05fd:
        r2 = r4.getCallState();
        if (r2 == 0) goto L_0x0604;
    L_0x0603:
        goto L_0x0667;
    L_0x0604:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x061e;
    L_0x0608:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "Starting service for call ";
        r2.append(r4);
        r4 = r0.id;
        r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x061e:
        org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent = r0;
        r2 = new android.content.Intent;
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = org.telegram.messenger.voip.VoIPService.class;
        r2.<init>(r4, r5);
        r4 = "is_outgoing";
        r2.putExtra(r4, r3);
        r4 = r0.participant_id;
        r5 = r8.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getClientUserId();
        if (r4 != r5) goto L_0x063f;
    L_0x063c:
        r0 = r0.admin_id;
        goto L_0x0641;
    L_0x063f:
        r0 = r0.participant_id;
    L_0x0641:
        r4 = "user_id";
        r2.putExtra(r4, r0);
        r0 = r8.currentAccount;
        r4 = "account";
        r2.putExtra(r4, r0);
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0661 }
        r4 = 26;
        if (r0 < r4) goto L_0x065a;
    L_0x0653:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0661 }
        r0.startForegroundService(r2);	 Catch:{ Throwable -> 0x0661 }
        goto L_0x0765;
    L_0x065a:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0661 }
        r0.startService(r2);	 Catch:{ Throwable -> 0x0661 }
        goto L_0x0765;
    L_0x0661:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0765;
    L_0x0667:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0686;
    L_0x066b:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "Auto-declining call ";
        r2.append(r4);
        r4 = r0.id;
        r2.append(r4);
        r4 = " because there's already active one";
        r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x0686:
        r2 = new org.telegram.tgnet.TLRPC$TL_phone_discardCall;
        r2.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPhoneCall;
        r4.<init>();
        r2.peer = r4;
        r4 = r2.peer;
        r5 = r0.access_hash;
        r4.access_hash = r5;
        r5 = r0.id;
        r4.id = r5;
        r0 = new org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
        r0.<init>();
        r2.reason = r0;
        r0 = r8.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r4 = new org.telegram.messenger.-$$Lambda$MessagesController$dvxVbHYq_pwVIer86eRbP3aeXAg;
        r4.<init>(r8);
        r0.sendRequest(r2, r4);
        goto L_0x0765;
    L_0x06b3:
        if (r2 == 0) goto L_0x06bc;
    L_0x06b5:
        if (r0 == 0) goto L_0x06bc;
    L_0x06b7:
        r2.onCallUpdated(r0);
        goto L_0x0765;
    L_0x06bc:
        r2 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent;
        if (r2 == 0) goto L_0x0765;
    L_0x06c0:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x06c9;
    L_0x06c4:
        r2 = "Updated the call while the service is starting";
        org.telegram.messenger.FileLog.d(r2);
    L_0x06c9:
        r4 = r0.id;
        r2 = org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent;
        r6 = r2.id;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 != 0) goto L_0x0765;
    L_0x06d3:
        org.telegram.messenger.voip.VoIPService.callIShouldHavePutIntoIntent = r0;
        goto L_0x0765;
    L_0x06d7:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark;
        if (r2 == 0) goto L_0x0737;
    L_0x06db:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark) r0;
        r2 = r0.peer;
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_dialogPeer;
        if (r4 == 0) goto L_0x06f9;
    L_0x06e3:
        r2 = (org.telegram.tgnet.TLRPC.TL_dialogPeer) r2;
        r2 = r2.peer;
        r4 = r2.user_id;
        if (r4 == 0) goto L_0x06ee;
    L_0x06eb:
        r4 = (long) r4;
    L_0x06ec:
        r6 = r4;
        goto L_0x06fa;
    L_0x06ee:
        r4 = r2.chat_id;
        if (r4 == 0) goto L_0x06f4;
    L_0x06f2:
        r2 = -r4;
        goto L_0x06f7;
    L_0x06f4:
        r2 = r2.channel_id;
        r2 = -r2;
    L_0x06f7:
        r4 = (long) r2;
        goto L_0x06ec;
    L_0x06f9:
        r6 = r12;
    L_0x06fa:
        r2 = r8.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r4 = r0.unread;
        r2.setDialogUnread(r6, r4);
        r2 = r8.dialogs_dict;
        r2 = r2.get(r6);
        r2 = (org.telegram.tgnet.TLRPC.Dialog) r2;
        if (r2 == 0) goto L_0x0765;
    L_0x070f:
        r4 = r2.unread_mark;
        r0 = r0.unread;
        if (r4 == r0) goto L_0x0765;
    L_0x0715:
        r2.unread_mark = r0;
        r0 = r2.unread_count;
        if (r0 != 0) goto L_0x0732;
    L_0x071b:
        r0 = r8.isDialogMuted(r6);
        if (r0 != 0) goto L_0x0732;
    L_0x0721:
        r0 = r2.unread_mark;
        if (r0 == 0) goto L_0x072c;
    L_0x0725:
        r0 = r8.unreadUnmutedDialogs;
        r2 = 1;
        r0 = r0 + r2;
        r8.unreadUnmutedDialogs = r0;
        goto L_0x0732;
    L_0x072c:
        r2 = 1;
        r0 = r8.unreadUnmutedDialogs;
        r0 = r0 - r2;
        r8.unreadUnmutedDialogs = r0;
    L_0x0732:
        r0 = r1 | 256;
        r18 = r0;
        goto L_0x0767;
    L_0x0737:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateMessagePoll;
        if (r2 == 0) goto L_0x075e;
    L_0x073b:
        r0 = (org.telegram.tgnet.TLRPC.TL_updateMessagePoll) r0;
        r2 = r8.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r4 = org.telegram.messenger.NotificationCenter.didUpdatePollResults;
        r5 = 3;
        r5 = new java.lang.Object[r5];
        r6 = r0.poll_id;
        r6 = java.lang.Long.valueOf(r6);
        r5[r3] = r6;
        r6 = r0.poll;
        r7 = 1;
        r5[r7] = r6;
        r0 = r0.results;
        r6 = 2;
        r5[r6] = r0;
        r2.postNotificationName(r4, r5);
        goto L_0x0765;
    L_0x075e:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateGroupCall;
        if (r2 == 0) goto L_0x0763;
    L_0x0762:
        goto L_0x0765;
    L_0x0763:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateGroupCallParticipant;
    L_0x0765:
        r18 = r1;
    L_0x0767:
        r15 = r15 + 1;
        r9 = r32;
        r10 = r33;
        r12 = r35;
        r13 = r38;
        r2 = r14;
        r1 = r22;
        r3 = r23;
        r4 = 0;
        r5 = 1;
        r14 = 2;
        goto L_0x0026;
    L_0x077b:
        r14 = r2;
        r23 = r3;
        r1 = r18;
        r3 = 0;
        r12 = 0;
        if (r16 == 0) goto L_0x0795;
    L_0x0785:
        r16.commit();
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r2 = org.telegram.messenger.NotificationCenter.notificationsSettingsUpdated;
        r4 = new java.lang.Object[r3];
        r0.postNotificationName(r2, r4);
    L_0x0795:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r2 = r23;
        r4 = 1;
        r0.updateUsers(r2, r4, r4, r4);
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.updateUsers(r14, r3, r4, r4);
        r0 = r1;
        r1 = r33;
        goto L_0x07b8;
    L_0x07ae:
        r3 = 0;
        r4 = 1;
        r12 = 0;
        r0 = r31;
        r1 = r33;
        r17 = 0;
    L_0x07b8:
        if (r1 == 0) goto L_0x087c;
    L_0x07ba:
        r2 = r8.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r5 = org.telegram.messenger.NotificationCenter.didReceivedWebpagesInUpdates;
        r6 = new java.lang.Object[r4];
        r6[r3] = r1;
        r2.postNotificationName(r5, r6);
        r2 = r33.size();
        r4 = 0;
    L_0x07ce:
        if (r4 >= r2) goto L_0x087c;
    L_0x07d0:
        r5 = r1.keyAt(r4);
        r7 = r8.reloadingWebpagesPending;
        r7 = r7.get(r5);
        r7 = (java.util.ArrayList) r7;
        r9 = r8.reloadingWebpagesPending;
        r9.remove(r5);
        if (r7 == 0) goto L_0x0876;
    L_0x07e3:
        r5 = r1.valueAt(r4);
        r5 = (org.telegram.tgnet.TLRPC.WebPage) r5;
        r6 = new java.util.ArrayList;
        r6.<init>();
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r9 != 0) goto L_0x07ff;
    L_0x07f2:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPageEmpty;
        if (r9 == 0) goto L_0x07f7;
    L_0x07f6:
        goto L_0x07ff;
    L_0x07f7:
        r9 = r8.reloadingWebpagesPending;
        r14 = r5.id;
        r9.put(r14, r7);
        goto L_0x083e;
    L_0x07ff:
        r9 = r7.size();
        r14 = r12;
        r10 = 0;
    L_0x0805:
        if (r10 >= r9) goto L_0x083d;
    L_0x0807:
        r16 = r7.get(r10);
        r12 = r16;
        r12 = (org.telegram.messenger.MessageObject) r12;
        r12 = r12.messageOwner;
        r12 = r12.media;
        r12.webpage = r5;
        if (r10 != 0) goto L_0x082d;
    L_0x0817:
        r12 = r7.get(r10);
        r12 = (org.telegram.messenger.MessageObject) r12;
        r12 = r12.getDialogId();
        r14 = r7.get(r10);
        r14 = (org.telegram.messenger.MessageObject) r14;
        r14 = r14.messageOwner;
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r14);
        r14 = r12;
    L_0x082d:
        r12 = r7.get(r10);
        r12 = (org.telegram.messenger.MessageObject) r12;
        r12 = r12.messageOwner;
        r6.add(r12);
        r10 = r10 + 1;
        r12 = 0;
        goto L_0x0805;
    L_0x083d:
        r12 = r14;
    L_0x083e:
        r5 = r6.isEmpty();
        if (r5 != 0) goto L_0x0876;
    L_0x0844:
        r5 = r8.currentAccount;
        r23 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r25 = 1;
        r26 = 1;
        r27 = 0;
        r5 = r8.currentAccount;
        r5 = org.telegram.messenger.DownloadController.getInstance(r5);
        r28 = r5.getAutodownloadMask();
        r24 = r6;
        r23.putMessages(r24, r25, r26, r27, r28);
        r5 = r8.currentAccount;
        r5 = org.telegram.messenger.NotificationCenter.getInstance(r5);
        r6 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;
        r9 = 2;
        r10 = new java.lang.Object[r9];
        r9 = java.lang.Long.valueOf(r12);
        r10[r3] = r9;
        r9 = 1;
        r10[r9] = r7;
        r5.postNotificationName(r6, r10);
    L_0x0876:
        r4 = r4 + 1;
        r12 = 0;
        goto L_0x07ce;
    L_0x087c:
        if (r11 == 0) goto L_0x0895;
    L_0x087e:
        r1 = r34.size();
        r2 = 0;
    L_0x0883:
        if (r2 >= r1) goto L_0x089b;
    L_0x0885:
        r4 = r11.keyAt(r2);
        r6 = r11.valueAt(r2);
        r6 = (java.util.ArrayList) r6;
        r8.updateInterfaceWithMessages(r4, r6);
        r2 = r2 + 1;
        goto L_0x0883;
    L_0x0895:
        if (r17 == 0) goto L_0x089f;
    L_0x0897:
        r1 = 0;
        r8.sortDialogs(r1);
    L_0x089b:
        r1 = r35;
        r2 = 1;
        goto L_0x08a2;
    L_0x089f:
        r1 = r35;
        r2 = 0;
    L_0x08a2:
        if (r1 == 0) goto L_0x0945;
    L_0x08a4:
        r4 = r35.size();
        r5 = r2;
        r2 = 0;
    L_0x08aa:
        if (r2 >= r4) goto L_0x0944;
    L_0x08ac:
        r6 = r1.keyAt(r2);
        r9 = r1.valueAt(r2);
        r9 = (java.util.ArrayList) r9;
        r10 = r8.dialogMessage;
        r10 = r10.get(r6);
        r10 = (org.telegram.messenger.MessageObject) r10;
        if (r10 == 0) goto L_0x08f0;
    L_0x08c0:
        r11 = r9.size();
        r12 = 0;
    L_0x08c5:
        if (r12 >= r11) goto L_0x08f0;
    L_0x08c7:
        r13 = r9.get(r12);
        r13 = (org.telegram.messenger.MessageObject) r13;
        r14 = r10.getId();
        r15 = r13.getId();
        if (r14 != r15) goto L_0x08f2;
    L_0x08d7:
        r5 = r8.dialogMessage;
        r5.put(r6, r13);
        r5 = r13.messageOwner;
        r5 = r5.to_id;
        if (r5 == 0) goto L_0x08ef;
    L_0x08e2:
        r5 = r5.channel_id;
        if (r5 != 0) goto L_0x08ef;
    L_0x08e6:
        r5 = r8.dialogMessagesByIds;
        r10 = r13.getId();
        r5.put(r10, r13);
    L_0x08ef:
        r5 = 1;
    L_0x08f0:
        r13 = 0;
        goto L_0x0920;
    L_0x08f2:
        r14 = r10.getDialogId();
        r16 = r13.getDialogId();
        r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r18 != 0) goto L_0x091c;
    L_0x08fe:
        r14 = r10.messageOwner;
        r14 = r14.action;
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r14 == 0) goto L_0x091c;
    L_0x0906:
        r14 = r10.replyMessageObject;
        if (r14 == 0) goto L_0x091c;
    L_0x090a:
        r14 = r14.getId();
        r15 = r13.getId();
        if (r14 != r15) goto L_0x091c;
    L_0x0914:
        r10.replyMessageObject = r13;
        r13 = 0;
        r10.generatePinMessageText(r13, r13);
        r5 = 1;
        goto L_0x0920;
    L_0x091c:
        r13 = 0;
        r12 = r12 + 1;
        goto L_0x08c5;
    L_0x0920:
        r10 = r8.currentAccount;
        r10 = org.telegram.messenger.DataQuery.getInstance(r10);
        r10.loadReplyMessagesForMessages(r9, r6);
        r10 = r8.currentAccount;
        r10 = org.telegram.messenger.NotificationCenter.getInstance(r10);
        r11 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;
        r12 = 2;
        r14 = new java.lang.Object[r12];
        r6 = java.lang.Long.valueOf(r6);
        r14[r3] = r6;
        r6 = 1;
        r14[r6] = r9;
        r10.postNotificationName(r11, r14);
        r2 = r2 + 1;
        goto L_0x08aa;
    L_0x0944:
        r2 = r5;
    L_0x0945:
        if (r2 == 0) goto L_0x0954;
    L_0x0947:
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r4 = new java.lang.Object[r3];
        r1.postNotificationName(r2, r4);
    L_0x0954:
        if (r36 == 0) goto L_0x0958;
    L_0x0956:
        r0 = r0 | 64;
    L_0x0958:
        if (r37 == 0) goto L_0x095e;
    L_0x095a:
        r0 = r0 | 1;
        r0 = r0 | 128;
    L_0x095e:
        r1 = r38;
        if (r1 == 0) goto L_0x097b;
    L_0x0962:
        r2 = r38.size();
        r4 = 0;
    L_0x0967:
        if (r4 >= r2) goto L_0x097b;
    L_0x0969:
        r5 = r1.get(r4);
        r5 = (org.telegram.tgnet.TLRPC.ChatParticipants) r5;
        r6 = r8.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6.updateChatParticipants(r5);
        r4 = r4 + 1;
        goto L_0x0967;
    L_0x097b:
        if (r39 == 0) goto L_0x098e;
    L_0x097d:
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.didUpdatedMessagesViews;
        r4 = 1;
        r5 = new java.lang.Object[r4];
        r5[r3] = r39;
        r1.postNotificationName(r2, r5);
        goto L_0x098f;
    L_0x098e:
        r4 = 1;
    L_0x098f:
        if (r0 == 0) goto L_0x09a4;
    L_0x0991:
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.updateInterfaces;
        r4 = new java.lang.Object[r4];
        r0 = java.lang.Integer.valueOf(r0);
        r4[r3] = r0;
        r1.postNotificationName(r2, r4);
    L_0x09a4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processUpdateArray$250$MessagesController(int, java.util.ArrayList, android.util.LongSparseArray, android.util.LongSparseArray, android.util.LongSparseArray, boolean, java.util.ArrayList, java.util.ArrayList, android.util.SparseArray):void");
    }

    public /* synthetic */ void lambda$null$246$MessagesController(User user) {
        ContactsController.getInstance(this.currentAccount).addContactToPhoneBook(user, true);
    }

    public /* synthetic */ void lambda$null$247$MessagesController(TL_updateChannel tL_updateChannel) {
        getChannelDifference(tL_updateChannel.channel_id, 1, 0, null);
    }

    public /* synthetic */ void lambda$null$248$MessagesController(Chat chat) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    public /* synthetic */ void lambda$null$249$MessagesController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$processUpdateArray$252$MessagesController(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseIntArray sparseIntArray2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$xLr6cfkOLJahxhWtfNWAwSwDpgA(this, sparseLongArray, sparseLongArray2, sparseIntArray, arrayList, sparseArray, sparseIntArray2));
    }

    public /* synthetic */ void lambda$null$251$MessagesController(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseIntArray sparseIntArray2) {
        int size;
        int i;
        int i2;
        int keyAt;
        MessageObject messageObject;
        int size2;
        MessageObject messageObject2;
        long j;
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        SparseIntArray sparseIntArray3 = sparseIntArray;
        SparseArray sparseArray2 = sparseArray;
        SparseIntArray sparseIntArray4 = sparseIntArray2;
        int i3 = 0;
        if (!(sparseLongArray3 == null && sparseLongArray4 == null)) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesRead, sparseLongArray3, sparseLongArray4);
            if (sparseLongArray3 != null) {
                NotificationsController.getInstance(this.currentAccount).processReadMessages(sparseLongArray, 0, 0, 0, false);
                Editor edit = this.notificationsPreferences.edit();
                size = sparseLongArray.size();
                i = 0;
                for (i2 = 0; i2 < size; i2++) {
                    keyAt = sparseLongArray3.keyAt(i2);
                    int valueAt = (int) sparseLongArray3.valueAt(i2);
                    Dialog dialog = (Dialog) this.dialogs_dict.get((long) keyAt);
                    if (dialog != null) {
                        i3 = dialog.top_message;
                        if (i3 > 0 && i3 <= valueAt) {
                            messageObject = (MessageObject) this.dialogMessage.get(dialog.id);
                            if (!(messageObject == null || messageObject.isOut())) {
                                messageObject.setIsRead();
                                i |= 256;
                            }
                        }
                    }
                    if (keyAt != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
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
                            messageObject2 = (MessageObject) this.dialogMessage.get(dialog2.id);
                            if (messageObject2 != null && messageObject2.isOut()) {
                                messageObject2.setIsRead();
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
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(i2), Integer.valueOf(i));
                j = ((long) i2) << 32;
                if (((Dialog) this.dialogs_dict.get(j)) != null) {
                    messageObject2 = (MessageObject) this.dialogMessage.get(j);
                    if (messageObject2 != null && messageObject2.messageOwner.date <= i) {
                        messageObject2.setIsRead();
                        i3 |= 256;
                    }
                }
            }
        }
        if (arrayList != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
        }
        if (sparseArray2 != null) {
            size2 = sparseArray.size();
            for (size = 0; size < size2; size++) {
                i2 = sparseArray2.keyAt(size);
                ArrayList arrayList2 = (ArrayList) sparseArray2.valueAt(size);
                if (arrayList2 != null) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList2, Integer.valueOf(i2));
                    if (i2 == 0) {
                        i2 = arrayList2.size();
                        for (keyAt = 0; keyAt < i2; keyAt++) {
                            messageObject = (MessageObject) this.dialogMessagesByIds.get(((Integer) arrayList2.get(keyAt)).intValue());
                            if (messageObject != null) {
                                messageObject.deleted = true;
                            }
                        }
                    } else {
                        messageObject2 = (MessageObject) this.dialogMessage.get((long) (-i2));
                        if (messageObject2 != null) {
                            keyAt = arrayList2.size();
                            for (int i4 = 0; i4 < keyAt; i4++) {
                                if (messageObject2.getId() == ((Integer) arrayList2.get(i4)).intValue()) {
                                    messageObject2.deleted = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            NotificationsController.getInstance(this.currentAccount).removeDeletedMessagesFromNotifications(sparseArray2);
        }
        if (sparseIntArray4 != null) {
            size2 = sparseIntArray2.size();
            for (size = 0; size < size2; size++) {
                i2 = sparseIntArray4.keyAt(size);
                i = sparseIntArray4.valueAt(size);
                j = (long) (-i2);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.historyCleared, Long.valueOf(j), Integer.valueOf(i));
                messageObject2 = (MessageObject) this.dialogMessage.get(j);
                if (messageObject2 != null && messageObject2.getId() <= i) {
                    messageObject2.deleted = true;
                    break;
                }
            }
            NotificationsController.getInstance(this.currentAccount).removeDeletedHisoryFromNotifications(sparseIntArray4);
        }
        if (i3 != 0) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(i3));
        }
    }

    public /* synthetic */ void lambda$processUpdateArray$253$MessagesController(ArrayList arrayList, int i) {
        MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(arrayList, MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeleted(arrayList, false, i), false, i);
    }

    public /* synthetic */ void lambda$processUpdateArray$254$MessagesController(int i, int i2) {
        MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeleted(i, i2, false), false, i);
    }

    public boolean isDialogMuted(long j) {
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(j);
        int i = sharedPreferences.getInt(stringBuilder.toString(), -1);
        if (i == -1) {
            return NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(j) ^ 1;
        }
        if (i == 2) {
            return true;
        }
        if (i == 3) {
            sharedPreferences = this.notificationsPreferences;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("notifyuntil_");
            stringBuilder2.append(j);
            if (sharedPreferences.getInt(stringBuilder2.toString(), 0) >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
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
    public void updateInterfaceWithMessages(long j, ArrayList<MessageObject> arrayList) {
        updateInterfaceWithMessages(j, arrayList, false);
    }

    /* Access modifiers changed, original: protected */
    public void updateInterfaceWithMessages(long j, ArrayList<MessageObject> arrayList, boolean z) {
        long j2 = j;
        ArrayList<MessageObject> arrayList2 = arrayList;
        if (!(arrayList2 == null || arrayList.isEmpty())) {
            int i = 0;
            Object obj = ((int) j2) == 0 ? 1 : null;
            MessageObject messageObject = null;
            Object obj2 = null;
            int i2 = 0;
            Object obj3 = null;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                MessageObject messageObject2 = (MessageObject) arrayList2.get(i3);
                if (messageObject == null || ((obj == null && messageObject2.getId() > messageObject.getId()) || (((obj != null || (messageObject2.getId() < 0 && messageObject.getId() < 0)) && messageObject2.getId() < messageObject.getId()) || messageObject2.messageOwner.date > messageObject.messageOwner.date))) {
                    int i4 = messageObject2.messageOwner.to_id.channel_id;
                    if (i4 != 0) {
                        i2 = i4;
                    }
                    messageObject = messageObject2;
                }
                if (obj2 == null && !messageObject2.isOut()) {
                    obj2 = 1;
                }
                if (!(!messageObject2.isOut() || messageObject2.isSending() || messageObject2.isForwarded())) {
                    DataQuery instance;
                    if (messageObject2.isNewGif()) {
                        instance = DataQuery.getInstance(this.currentAccount);
                        Message message = messageObject2.messageOwner;
                        instance.addRecentGif(message.media.document, message.date);
                    } else if (messageObject2.isSticker()) {
                        instance = DataQuery.getInstance(this.currentAccount);
                        Message message2 = messageObject2.messageOwner;
                        instance.addRecentSticker(0, messageObject2, message2.media.document, message2.date, false);
                    }
                }
                if (messageObject2.isOut() && messageObject2.isSent()) {
                    obj3 = 1;
                }
            }
            DataQuery.getInstance(this.currentAccount).loadReplyMessagesForMessages(arrayList2, j2);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didReceiveNewMessages, Long.valueOf(j), arrayList2);
            if (messageObject != null) {
                TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(j2);
                if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                    if (tL_dialog != null) {
                        this.allDialogs.remove(tL_dialog);
                        this.dialogsServerOnly.remove(tL_dialog);
                        this.dialogsCanAddUsers.remove(tL_dialog);
                        this.dialogsChannelsOnly.remove(tL_dialog);
                        this.dialogsGroupsOnly.remove(tL_dialog);
                        this.dialogsUsersOnly.remove(tL_dialog);
                        this.dialogsForward.remove(tL_dialog);
                        this.dialogs_dict.remove(tL_dialog.id);
                        this.dialogs_read_inbox_max.remove(Long.valueOf(tL_dialog.id));
                        this.dialogs_read_outbox_max.remove(Long.valueOf(tL_dialog.id));
                        int i5 = this.nextDialogsCacheOffset.get(tL_dialog.folder_id, 0);
                        if (i5 > 0) {
                            this.nextDialogsCacheOffset.put(tL_dialog.folder_id, i5 - 1);
                        }
                        this.dialogMessage.remove(tL_dialog.id);
                        ArrayList arrayList3 = (ArrayList) this.dialogsByFolder.get(tL_dialog.folder_id);
                        if (arrayList3 != null) {
                            arrayList3.remove(tL_dialog);
                        }
                        MessageObject messageObject3 = (MessageObject) this.dialogMessagesByIds.get(tL_dialog.top_message);
                        this.dialogMessagesByIds.remove(tL_dialog.top_message);
                        if (messageObject3 != null) {
                            j2 = messageObject3.messageOwner.random_id;
                            if (j2 != 0) {
                                this.dialogMessagesByRandomIds.remove(j2);
                            }
                        }
                        tL_dialog.top_message = 0;
                        NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(tL_dialog.id);
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                    }
                    return;
                }
                if (tL_dialog != null) {
                    int i6;
                    if (obj2 != null) {
                        i6 = 1;
                        if (tL_dialog.folder_id == 1 && !isDialogMuted(tL_dialog.id)) {
                            tL_dialog.folder_id = 0;
                            tL_dialog.pinned = false;
                            tL_dialog.pinnedNum = 0;
                            MessagesStorage.getInstance(this.currentAccount).setDialogsFolderId(null, null, tL_dialog.id, 0);
                            i = 1;
                        }
                    } else {
                        i6 = 1;
                    }
                    if ((tL_dialog.top_message > 0 && messageObject.getId() > 0 && messageObject.getId() > tL_dialog.top_message) || ((tL_dialog.top_message < 0 && messageObject.getId() < 0 && messageObject.getId() < tL_dialog.top_message) || this.dialogMessage.indexOfKey(j2) < 0 || tL_dialog.top_message < 0 || tL_dialog.last_message_date <= messageObject.messageOwner.date)) {
                        MessageObject messageObject4 = (MessageObject) this.dialogMessagesByIds.get(tL_dialog.top_message);
                        this.dialogMessagesByIds.remove(tL_dialog.top_message);
                        if (messageObject4 != null) {
                            long j3 = messageObject4.messageOwner.random_id;
                            if (j3 != 0) {
                                this.dialogMessagesByRandomIds.remove(j3);
                            }
                        }
                        tL_dialog.top_message = messageObject.getId();
                        if (z) {
                            i6 = i;
                        } else {
                            tL_dialog.last_message_date = messageObject.messageOwner.date;
                        }
                        this.dialogMessage.put(j2, messageObject);
                        if (messageObject.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                            long j4 = messageObject.messageOwner.random_id;
                            if (j4 != 0) {
                                this.dialogMessagesByRandomIds.put(j4, messageObject);
                            }
                        }
                        i = i6;
                    }
                } else if (!z) {
                    Chat chat = getChat(Integer.valueOf(i2));
                    if ((i2 == 0 || chat != null) && (chat == null || !chat.left)) {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("not found dialog with id ");
                            stringBuilder.append(j2);
                            stringBuilder.append(" dictCount = ");
                            stringBuilder.append(this.dialogs_dict.size());
                            stringBuilder.append(" allCount = ");
                            stringBuilder.append(this.allDialogs.size());
                            FileLog.d(stringBuilder.toString());
                        }
                        TL_dialog tL_dialog2 = new TL_dialog();
                        tL_dialog2.id = j2;
                        tL_dialog2.unread_count = 0;
                        tL_dialog2.top_message = messageObject.getId();
                        tL_dialog2.last_message_date = messageObject.messageOwner.date;
                        tL_dialog2.flags = ChatObject.isChannel(chat);
                        this.dialogs_dict.put(j2, tL_dialog2);
                        this.allDialogs.add(tL_dialog2);
                        this.dialogMessage.put(j2, messageObject);
                        if (messageObject.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                            long j5 = messageObject.messageOwner.random_id;
                            if (j5 != 0) {
                                this.dialogMessagesByRandomIds.put(j5, messageObject);
                            }
                        }
                        MessagesStorage.getInstance(this.currentAccount).getDialogFolderId(j2, new -$$Lambda$MessagesController$UGzhyEr_7mVrPzKWG0QmrzUROSg(this, tL_dialog2, j2));
                        i = 1;
                    } else {
                        return;
                    }
                }
                if (i != 0) {
                    sortDialogs(null);
                }
                if (obj3 != null) {
                    DataQuery.getInstance(this.currentAccount).increasePeerRaiting(j2);
                }
            }
        }
    }

    public /* synthetic */ void lambda$updateInterfaceWithMessages$255$MessagesController(Dialog dialog, long j, int i) {
        if (i == -1) {
            int i2 = (int) j;
            if (i2 != 0) {
                loadUnknownDialog(getInputPeer(i2), 0);
            }
        } else if (i != 0) {
            dialog.folder_id = i;
            sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
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
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
            }
        }
    }

    public boolean isClearingDialog(long j) {
        return this.clearingHistoryDialogs.get(j) != null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x0112  */
    /* JADX WARNING: Missing block: B:33:0x00bb, code skipped:
            if (r10.add_admins != false) goto L_0x00c1;
     */
    /* JADX WARNING: Missing block: B:35:0x00bf, code skipped:
            if (r8.creator != false) goto L_0x00c1;
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
        r2 = r2.get(r1);
        r2 = (java.util.ArrayList) r2;
        if (r2 == 0) goto L_0x0035;
    L_0x0032:
        r2.clear();
    L_0x0035:
        r1 = r1 + 1;
        goto L_0x0020;
    L_0x0038:
        r13.unreadUnmutedDialogs = r0;
        r1 = r13.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        r2 = r13.allDialogs;
        r3 = r13.dialogComparator;
        java.util.Collections.sort(r2, r3);
        r2 = 1;
        r13.isLeftProxyChannel = r2;
        r3 = r13.proxyDialog;
        if (r3 == 0) goto L_0x006c;
    L_0x0052:
        r3 = r3.id;
        r5 = 0;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 >= 0) goto L_0x006c;
    L_0x005a:
        r4 = (int) r3;
        r3 = -r4;
        r3 = java.lang.Integer.valueOf(r3);
        r3 = r13.getChat(r3);
        if (r3 == 0) goto L_0x006c;
    L_0x0066:
        r3 = r3.left;
        if (r3 != 0) goto L_0x006c;
    L_0x006a:
        r13.isLeftProxyChannel = r0;
    L_0x006c:
        r3 = r13.currentAccount;
        r3 = org.telegram.messenger.NotificationsController.getInstance(r3);
        r3 = r3.showBadgeMessages;
        r4 = r13.allDialogs;
        r4 = r4.size();
        r5 = r4;
        r4 = 0;
        r6 = 0;
    L_0x007d:
        if (r4 >= r5) goto L_0x0156;
    L_0x007f:
        r7 = r13.allDialogs;
        r7 = r7.get(r4);
        r7 = (org.telegram.tgnet.TLRPC.Dialog) r7;
        r8 = r7.id;
        r10 = 32;
        r10 = r8 >> r10;
        r11 = (int) r10;
        r9 = (int) r8;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_dialog;
        if (r8 == 0) goto L_0x0120;
    L_0x0093:
        if (r9 == 0) goto L_0x010f;
    L_0x0095:
        if (r11 == r2) goto L_0x010f;
    L_0x0097:
        r8 = r13.dialogsServerOnly;
        r8.add(r7);
        r8 = org.telegram.messenger.DialogObject.isChannel(r7);
        if (r8 == 0) goto L_0x00e6;
    L_0x00a2:
        r8 = -r9;
        r8 = java.lang.Integer.valueOf(r8);
        r8 = r13.getChat(r8);
        if (r8 == 0) goto L_0x00c6;
    L_0x00ad:
        r10 = r8.megagroup;
        if (r10 == 0) goto L_0x00bd;
    L_0x00b1:
        r10 = r8.admin_rights;
        if (r10 == 0) goto L_0x00bd;
    L_0x00b5:
        r11 = r10.post_messages;
        if (r11 != 0) goto L_0x00c1;
    L_0x00b9:
        r10 = r10.add_admins;
        if (r10 != 0) goto L_0x00c1;
    L_0x00bd:
        r10 = r8.creator;
        if (r10 == 0) goto L_0x00c6;
    L_0x00c1:
        r10 = r13.dialogsCanAddUsers;
        r10.add(r7);
    L_0x00c6:
        if (r8 == 0) goto L_0x00d2;
    L_0x00c8:
        r10 = r8.megagroup;
        if (r10 == 0) goto L_0x00d2;
    L_0x00cc:
        r8 = r13.dialogsGroupsOnly;
        r8.add(r7);
        goto L_0x010f;
    L_0x00d2:
        r10 = r13.dialogsChannelsOnly;
        r10.add(r7);
        r10 = org.telegram.messenger.ChatObject.hasAdminRights(r8);
        if (r10 == 0) goto L_0x00e4;
    L_0x00dd:
        r8 = org.telegram.messenger.ChatObject.canPost(r8);
        if (r8 == 0) goto L_0x00e4;
    L_0x00e3:
        goto L_0x010f;
    L_0x00e4:
        r8 = 0;
        goto L_0x0110;
    L_0x00e6:
        if (r9 >= 0) goto L_0x0108;
    L_0x00e8:
        if (r14 == 0) goto L_0x00fd;
    L_0x00ea:
        r8 = -r9;
        r8 = r14.get(r8);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        if (r8 == 0) goto L_0x00fd;
    L_0x00f3:
        r8 = r8.migrated_to;
        if (r8 == 0) goto L_0x00fd;
    L_0x00f7:
        r7 = r13.allDialogs;
        r7.remove(r4);
        goto L_0x014a;
    L_0x00fd:
        r8 = r13.dialogsCanAddUsers;
        r8.add(r7);
        r8 = r13.dialogsGroupsOnly;
        r8.add(r7);
        goto L_0x010f;
    L_0x0108:
        if (r9 <= 0) goto L_0x010f;
    L_0x010a:
        r8 = r13.dialogsUsersOnly;
        r8.add(r7);
    L_0x010f:
        r8 = 1;
    L_0x0110:
        if (r8 == 0) goto L_0x0120;
    L_0x0112:
        if (r9 != r1) goto L_0x011b;
    L_0x0114:
        r6 = r13.dialogsForward;
        r6.add(r0, r7);
        r6 = 1;
        goto L_0x0120;
    L_0x011b:
        r8 = r13.dialogsForward;
        r8.add(r7);
    L_0x0120:
        r8 = r7.unread_count;
        if (r8 != 0) goto L_0x0128;
    L_0x0124:
        r8 = r7.unread_mark;
        if (r8 == 0) goto L_0x0135;
    L_0x0128:
        r8 = r7.id;
        r8 = r13.isDialogMuted(r8);
        if (r8 != 0) goto L_0x0135;
    L_0x0130:
        r8 = r13.unreadUnmutedDialogs;
        r8 = r8 + r2;
        r13.unreadUnmutedDialogs = r8;
    L_0x0135:
        r8 = r13.proxyDialog;
        if (r8 == 0) goto L_0x014f;
    L_0x0139:
        r9 = r7.id;
        r11 = r8.id;
        r8 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r8 != 0) goto L_0x014f;
    L_0x0141:
        r8 = r13.isLeftProxyChannel;
        if (r8 == 0) goto L_0x014f;
    L_0x0145:
        r7 = r13.allDialogs;
        r7.remove(r4);
    L_0x014a:
        r4 = r4 + -1;
        r5 = r5 + -1;
        goto L_0x0153;
    L_0x014f:
        r8 = -1;
        r13.addDialogToItsFolder(r8, r7, r3);
    L_0x0153:
        r4 = r4 + r2;
        goto L_0x007d;
    L_0x0156:
        r14 = r13.proxyDialog;
        if (r14 == 0) goto L_0x0169;
    L_0x015a:
        r1 = r13.isLeftProxyChannel;
        if (r1 == 0) goto L_0x0169;
    L_0x015e:
        r1 = r13.allDialogs;
        r1.add(r0, r14);
        r14 = -2;
        r1 = r13.proxyDialog;
        r13.addDialogToItsFolder(r14, r1, r3);
    L_0x0169:
        if (r6 != 0) goto L_0x019a;
    L_0x016b:
        r14 = r13.currentAccount;
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);
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

    private static String getRestrictionReason(String str) {
        if (!(str == null || str.length() == 0)) {
            int indexOf = str.indexOf(": ");
            if (indexOf > 0) {
                String substring = str.substring(0, indexOf);
                if (substring.contains("-all") || substring.contains("-android")) {
                    return str.substring(indexOf + 2);
                }
            }
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
                    alertDialog.setOnCancelListener(new -$$Lambda$MessagesController$Uy5HFVAD0pdKo9Ipvs8fE566HbU(this, ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessages, new -$$Lambda$MessagesController$6GNHdOHS4ayBESoK8wFIXPmSlYs(this, alertDialog, baseFragment, bundle)), baseFragment));
                    baseFragment.setVisibleDialog(alertDialog);
                    alertDialog.show();
                    return false;
                }
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$checkCanOpenChat$257$MessagesController(AlertDialog alertDialog, BaseFragment baseFragment, Bundle bundle, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$khcet7XZs3bzMGRlSwPxErmYGcs(this, alertDialog, tLObject, baseFragment, bundle));
        }
    }

    public /* synthetic */ void lambda$null$256$MessagesController(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        messages_Messages messages_messages = (messages_Messages) tLObject;
        putUsers(messages_messages.users, false);
        putChats(messages_messages.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
        baseFragment.presentFragment(new ChatActivity(bundle), true);
    }

    public /* synthetic */ void lambda$checkCanOpenChat$258$MessagesController(int i, BaseFragment baseFragment, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
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
        if (r6 == 0) goto L_0x006b;
    L_0x0002:
        if (r7 != 0) goto L_0x0006;
    L_0x0004:
        goto L_0x006b;
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
        goto L_0x006b;
    L_0x002f:
        r0 = 1;
        if (r1 == 0) goto L_0x0036;
    L_0x0032:
        openChatOrProfileWith(r2, r1, r7, r0, r3);
        goto L_0x006b;
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
        r6 = r5.currentAccount;
        r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6);
        r2 = new org.telegram.messenger.-$$Lambda$MessagesController$2QxSvtTZ9U28CTPZ6uf2mJLKqt0;
        r2.<init>(r5, r0, r7, r8);
        r6 = r6.sendRequest(r1, r2);
        r8 = new org.telegram.messenger.-$$Lambda$MessagesController$D332AVh7nCXc7vu_l1allSBL4LE;
        r8.<init>(r5, r0, r6, r7);
        r6 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r6);
    L_0x006b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.openByUserName(java.lang.String, org.telegram.ui.ActionBar.BaseFragment, int):void");
    }

    public /* synthetic */ void lambda$openByUserName$260$MessagesController(AlertDialog[] alertDialogArr, BaseFragment baseFragment, int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesController$PtlR0pP29l9GCXkUdVv_AI_Hdug(this, alertDialogArr, baseFragment, tL_error, tLObject, i));
    }

    public /* synthetic */ void lambda$null$259$MessagesController(AlertDialog[] alertDialogArr, BaseFragment baseFragment, TL_error tL_error, TLObject tLObject, int i) {
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
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
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

    public /* synthetic */ void lambda$openByUserName$262$MessagesController(AlertDialog[] alertDialogArr, int i, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new -$$Lambda$MessagesController$TMnAL0Zt--AXB8RK3P6tM9ad784(this, i));
            baseFragment.showDialog(alertDialogArr[0]);
        }
    }

    public /* synthetic */ void lambda$null$261$MessagesController(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }
}
