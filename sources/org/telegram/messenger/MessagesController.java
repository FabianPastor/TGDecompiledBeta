package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.widget.Toast;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.voip.VoIPService;
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
import org.telegram.tgnet.TLRPC.DialogPeer;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.EncryptedMessage;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.SendMessageAction;
import org.telegram.tgnet.TLRPC.TL_account_registerDevice;
import org.telegram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC.TL_account_updateStatus;
import org.telegram.tgnet.TLRPC.TL_auth_logOut;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_botInfo;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteHistory;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import org.telegram.tgnet.TLRPC.TL_channels_editAbout;
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
import org.telegram.tgnet.TLRPC.TL_channels_toggleInvites;
import org.telegram.tgnet.TLRPC.TL_channels_togglePreHistoryHidden;
import org.telegram.tgnet.TLRPC.TL_channels_toggleSignatures;
import org.telegram.tgnet.TLRPC.TL_channels_updatePinnedMessage;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chat;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_contactBlocked;
import org.telegram.tgnet.TLRPC.TL_contactLinkContact;
import org.telegram.tgnet.TLRPC.TL_contacts_block;
import org.telegram.tgnet.TLRPC.TL_contacts_getBlocked;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_contacts_unblock;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_dialogPeer;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getAppChangelog;
import org.telegram.tgnet.TLRPC.TL_help_getRecentMeUrls;
import org.telegram.tgnet.TLRPC.TL_help_recentMeUrls;
import org.telegram.tgnet.TLRPC.TL_inputChannel;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputDialogPeer;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUser;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUserSelf;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC.TL_messages_chatFull;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_deleteChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_deleteHistory;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC.TL_messages_editChatPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_editChatTitle;
import org.telegram.tgnet.TLRPC.TL_messages_getDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC.TL_messages_getHistory;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getMessagesViews;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerSettings;
import org.telegram.tgnet.TLRPC.TL_messages_getPinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getUnreadMentions;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_messages_hideReportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_messages_receivedQueue;
import org.telegram.tgnet.TLRPC.TL_messages_reportEncryptedSpam;
import org.telegram.tgnet.TLRPC.TL_messages_reportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import org.telegram.tgnet.TLRPC.TL_messages_setTyping;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_messages_toggleChatAdmins;
import org.telegram.tgnet.TLRPC.TL_messages_toggleDialogPin;
import org.telegram.tgnet.TLRPC.TL_notifyPeer;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.telegram.tgnet.TLRPC.TL_peerSettings;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallRequested;
import org.telegram.tgnet.TLRPC.TL_phone_discardCall;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_deletePhotos;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
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
import org.telegram.tgnet.TLRPC.TL_updateChatAdmins;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipants;
import org.telegram.tgnet.TLRPC.TL_updateChatUserTyping;
import org.telegram.tgnet.TLRPC.TL_updateConfig;
import org.telegram.tgnet.TLRPC.TL_updateContactLink;
import org.telegram.tgnet.TLRPC.TL_updateContactRegistered;
import org.telegram.tgnet.TLRPC.TL_updateContactsReset;
import org.telegram.tgnet.TLRPC.TL_updateDcOptions;
import org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
import org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
import org.telegram.tgnet.TLRPC.TL_updateEncryption;
import org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
import org.telegram.tgnet.TLRPC.TL_updateGroupCall;
import org.telegram.tgnet.TLRPC.TL_updateGroupCallParticipant;
import org.telegram.tgnet.TLRPC.TL_updateLangPack;
import org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
import org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_updatePhoneCall;
import org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_updatePrivacy;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents;
import org.telegram.tgnet.TLRPC.TL_updateRecentStickers;
import org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updateShort;
import org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortMessage;
import org.telegram.tgnet.TLRPC.TL_updateStickerSets;
import org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
import org.telegram.tgnet.TLRPC.TL_updateUserName;
import org.telegram.tgnet.TLRPC.TL_updateUserPhone;
import org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
import org.telegram.tgnet.TLRPC.TL_updateUserStatus;
import org.telegram.tgnet.TLRPC.TL_updateUserTyping;
import org.telegram.tgnet.TLRPC.TL_updateWebPage;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.TL_updatesCombined;
import org.telegram.tgnet.TLRPC.TL_updatesTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifference;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_difference;
import org.telegram.tgnet.TLRPC.TL_updates_differenceEmpty;
import org.telegram.tgnet.TLRPC.TL_updates_differenceSlice;
import org.telegram.tgnet.TLRPC.TL_updates_differenceTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_getChannelDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getState;
import org.telegram.tgnet.TLRPC.TL_updates_state;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userForeign_old2;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.TL_users_getFullUser;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
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
    public static final int UPDATE_MASK_CHANNEL = 8192;
    public static final int UPDATE_MASK_CHAT_ADMINS = 16384;
    public static final int UPDATE_MASK_CHAT_AVATAR = 8;
    public static final int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static final int UPDATE_MASK_CHAT_NAME = 16;
    public static final int UPDATE_MASK_MESSAGE_TEXT = 32768;
    public static final int UPDATE_MASK_NAME = 1;
    public static final int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static final int UPDATE_MASK_PHONE = 1024;
    public static final int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static final int UPDATE_MASK_SELECT_DIALOG = 512;
    public static final int UPDATE_MASK_SEND_STATE = 4096;
    public static final int UPDATE_MASK_STATUS = 4;
    public static final int UPDATE_MASK_USER_PHONE = 128;
    public static final int UPDATE_MASK_USER_PRINT = 64;
    private static volatile long lastThemeCheckTime;
    public ArrayList<Integer> blockedUsers = new ArrayList();
    public int callConnectTimeout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
    public int callPacketTimeout = 10000;
    public int callReceiveTimeout = 20000;
    public int callRingTimeout = 90000;
    public boolean canRevokePmInbox;
    private SparseArray<ArrayList<Integer>> channelAdmins = new SparseArray();
    private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray();
    private SparseIntArray channelsPts = new SparseIntArray();
    private ConcurrentHashMap<Integer, Chat> chats = new ConcurrentHashMap(100, 1.0f, 2);
    private SparseBooleanArray checkingLastMessagesDialogs = new SparseBooleanArray();
    private ArrayList<Long> createdDialogIds = new ArrayList();
    private ArrayList<Long> createdDialogMainThreadIds = new ArrayList();
    private int currentAccount;
    private Runnable currentDeleteTaskRunnable;
    private int currentDeletingTaskChannelId;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public boolean defaultP2pContacts = false;
    private final Comparator<TL_dialog> dialogComparator = new C03272();
    public LongSparseArray<MessageObject> dialogMessage = new LongSparseArray();
    public SparseArray<MessageObject> dialogMessagesByIds = new SparseArray();
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds = new LongSparseArray();
    public ArrayList<TL_dialog> dialogs = new ArrayList();
    public boolean dialogsEndReached;
    public ArrayList<TL_dialog> dialogsForward = new ArrayList();
    public ArrayList<TL_dialog> dialogsGroupsOnly = new ArrayList();
    public ArrayList<TL_dialog> dialogsServerOnly = new ArrayList();
    public LongSparseArray<TL_dialog> dialogs_dict = new LongSparseArray();
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    private SharedPreferences emojiPreferences;
    public boolean enableJoined = true;
    private ConcurrentHashMap<Integer, EncryptedChat> encryptedChats = new ConcurrentHashMap(10, 1.0f, 2);
    private SparseArray<ExportedChatInvite> exportedChats = new SparseArray();
    public boolean firstGettingTask;
    private SparseArray<TL_userFull> fullUsers = new SparseArray();
    private boolean getDifferenceFirstSync = true;
    public boolean gettingDifference;
    private SparseBooleanArray gettingDifferenceChannels = new SparseBooleanArray();
    private boolean gettingNewDeleteTask;
    private SparseBooleanArray gettingUnknownChannels = new SparseBooleanArray();
    public ArrayList<RecentMeUrl> hintDialogs = new ArrayList();
    private String installReferer;
    private ArrayList<Integer> joiningToChannels = new ArrayList();
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix = "t.me";
    private ArrayList<Integer> loadedFullChats = new ArrayList();
    private ArrayList<Integer> loadedFullParticipants = new ArrayList();
    private ArrayList<Integer> loadedFullUsers = new ArrayList();
    public boolean loadingBlockedUsers = false;
    private SparseIntArray loadingChannelAdmins = new SparseIntArray();
    public boolean loadingDialogs;
    private ArrayList<Integer> loadingFullChats = new ArrayList();
    private ArrayList<Integer> loadingFullParticipants = new ArrayList();
    private ArrayList<Integer> loadingFullUsers = new ArrayList();
    private LongSparseArray<Boolean> loadingPeerSettings = new LongSparseArray();
    private SharedPreferences mainPreferences;
    public int maxBroadcastCount = 100;
    public int maxEditTime = 172800;
    public int maxFaveStickersCount = 5;
    public int maxGroupCount = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    public int maxMegagroupCount = 10000;
    public int maxPinnedDialogsCount = 5;
    public int maxRecentGifsCount = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    public int maxRecentStickersCount = 30;
    private boolean migratingDialogs;
    public int minGroupConvertSize = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    private SparseIntArray needShortPollChannels = new SparseIntArray();
    public int nextDialogsCacheOffset;
    private SharedPreferences notificationsPreferences;
    private ConcurrentHashMap<String, TLObject> objectsByUsernames = new ConcurrentHashMap(100, 1.0f, 2);
    private boolean offlineSent;
    public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap(20, 1.0f, 2);
    public boolean preloadFeaturedStickers;
    public LongSparseArray<CharSequence> printingStrings = new LongSparseArray();
    public LongSparseArray<Integer> printingStringsTypes = new LongSparseArray();
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0f, 2);
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
    public int revokeTimeLimit = 172800;
    public int revokeTimePmLimit = 172800;
    public int secretWebpagePreview = 2;
    public SparseArray<LongSparseArray<Boolean>> sendingTypings = new SparseArray();
    public boolean serverDialogsEndReached;
    private SparseIntArray shortPollChannels = new SparseIntArray();
    private int statusRequest;
    private int statusSettingState;
    private Runnable themeCheckRunnable = new C03221();
    private final Comparator<Update> updatesComparator = new C03333();
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
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap(100, 1.0f, 2);

    /* renamed from: org.telegram.messenger.MessagesController$1 */
    class C03221 implements Runnable {
        C03221() {
        }

        public void run() {
            Theme.checkAutoNightThemeConditions();
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$2 */
    class C03272 implements Comparator<TL_dialog> {
        C03272() {
        }

        public int compare(TL_dialog dialog1, TL_dialog dialog2) {
            if (!dialog1.pinned && dialog2.pinned) {
                return 1;
            }
            if (dialog1.pinned && !dialog2.pinned) {
                return -1;
            }
            if (!dialog1.pinned || !dialog2.pinned) {
                DraftMessage draftMessage = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(dialog1.id);
                int date1 = (draftMessage == null || draftMessage.date < dialog1.last_message_date) ? dialog1.last_message_date : draftMessage.date;
                draftMessage = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(dialog2.id);
                int date2 = (draftMessage == null || draftMessage.date < dialog2.last_message_date) ? dialog2.last_message_date : draftMessage.date;
                if (date1 < date2) {
                    return 1;
                }
                if (date1 > date2) {
                    return -1;
                }
                return 0;
            } else if (dialog1.pinnedNum < dialog2.pinnedNum) {
                return 1;
            } else {
                if (dialog1.pinnedNum > dialog2.pinnedNum) {
                    return -1;
                }
                return 0;
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$3 */
    class C03333 implements Comparator<Update> {
        C03333() {
        }

        public int compare(Update lhs, Update rhs) {
            int ltype = MessagesController.this.getUpdateType(lhs);
            int rtype = MessagesController.this.getUpdateType(rhs);
            if (ltype != rtype) {
                return AndroidUtilities.compare(ltype, rtype);
            }
            if (ltype == 0) {
                return AndroidUtilities.compare(MessagesController.getUpdatePts(lhs), MessagesController.getUpdatePts(rhs));
            }
            if (ltype == 1) {
                return AndroidUtilities.compare(MessagesController.getUpdateQts(lhs), MessagesController.getUpdateQts(rhs));
            }
            if (ltype != 2) {
                return 0;
            }
            int lChannel = MessagesController.getUpdateChannelId(lhs);
            int rChannel = MessagesController.getUpdateChannelId(rhs);
            if (lChannel == rChannel) {
                return AndroidUtilities.compare(MessagesController.getUpdatePts(lhs), MessagesController.getUpdatePts(rhs));
            }
            return AndroidUtilities.compare(lChannel, rChannel);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$4 */
    class C03364 implements Runnable {
        C03364() {
        }

        public void run() {
            MessagesController messagesController = MessagesController.getInstance(MessagesController.this.currentAccount);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidUpload);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidFailUpload);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.updateMessageMedia);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$7 */
    class C03567 implements Runnable {
        C03567() {
        }

        public void run() {
            MessagesController.this.readTasks.clear();
            MessagesController.this.readTasksMap.clear();
            MessagesController.this.updatesQueueSeq.clear();
            MessagesController.this.updatesQueuePts.clear();
            MessagesController.this.updatesQueueQts.clear();
            MessagesController.this.gettingUnknownChannels.clear();
            MessagesController.this.updatesStartWaitTimeSeq = 0;
            MessagesController.this.updatesStartWaitTimePts = 0;
            MessagesController.this.updatesStartWaitTimeQts = 0;
            MessagesController.this.createdDialogIds.clear();
            MessagesController.this.gettingDifference = false;
            MessagesController.this.resetDialogsPinned = null;
            MessagesController.this.resetDialogsAll = null;
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$8 */
    class C03658 implements Runnable {
        C03658() {
        }

        public void run() {
            ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
            MessagesController.this.updatesQueueChannels.clear();
            MessagesController.this.updatesStartWaitTimeChannels.clear();
            MessagesController.this.gettingDifferenceChannels.clear();
            MessagesController.this.channelsPts.clear();
            MessagesController.this.shortPollChannels.clear();
            MessagesController.this.needShortPollChannels.clear();
        }
    }

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
    }

    /* renamed from: org.telegram.messenger.MessagesController$6 */
    class C18156 implements RequestDelegate {

        /* renamed from: org.telegram.messenger.MessagesController$6$1 */
        class C03411 implements Runnable {
            C03411() {
            }

            public void run() {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(2));
                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(true);
            }
        }

        C18156() {
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
                    MessagesController.this.putUser(user, true);
                } else {
                    UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(user);
                }
                if (user != null) {
                    TL_photos_photo photo = (TL_photos_photo) response;
                    ArrayList<PhotoSize> sizes = photo.photo.sizes;
                    PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 100);
                    PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 1000);
                    user.photo = new TL_userProfilePhoto();
                    user.photo.photo_id = photo.photo.id;
                    if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    if (bigSize != null) {
                        user.photo.photo_big = bigSize.location;
                    } else if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(user.id);
                    ArrayList<User> users = new ArrayList();
                    users.add(user);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(users, null, false, true);
                    AndroidUtilities.runOnUIThread(new C03411());
                }
            }
        }
    }

    private class UserActionUpdatesPts extends Updates {
        private UserActionUpdatesPts() {
        }
    }

    private class UserActionUpdatesSeq extends Updates {
        private UserActionUpdatesSeq() {
        }
    }

    public static MessagesController getInstance(int num) {
        MessagesController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessagesController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    MessagesController[] messagesControllerArr = Instance;
                    MessagesController messagesController = new MessagesController(num);
                    localInstance = messagesController;
                    messagesControllerArr[num] = messagesController;
                }
            }
        }
        return localInstance;
    }

    public static SharedPreferences getNotificationsSettings(int account) {
        return getInstance(account).notificationsPreferences;
    }

    public static SharedPreferences getGlobalNotificationsSettings() {
        return getInstance(0).notificationsPreferences;
    }

    public static SharedPreferences getMainSettings(int account) {
        return getInstance(account).mainPreferences;
    }

    public static SharedPreferences getGlobalMainSettings() {
        return getInstance(0).mainPreferences;
    }

    public static SharedPreferences getEmojiSettings(int account) {
        return getInstance(account).emojiPreferences;
    }

    public static SharedPreferences getGlobalEmojiSettings() {
        return getInstance(0).emojiPreferences;
    }

    public MessagesController(int num) {
        this.currentAccount = num;
        ImageLoader.getInstance();
        MessagesStorage.getInstance(this.currentAccount);
        LocationController.getInstance(this.currentAccount);
        AndroidUtilities.runOnUIThread(new C03364());
        addSupportUser();
        if (this.currentAccount == 0) {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Notifications");
            stringBuilder.append(this.currentAccount);
            this.notificationsPreferences = context.getSharedPreferences(stringBuilder.toString(), 0);
            context = ApplicationLoader.applicationContext;
            stringBuilder = new StringBuilder();
            stringBuilder.append("mainconfig");
            stringBuilder.append(this.currentAccount);
            this.mainPreferences = context.getSharedPreferences(stringBuilder.toString(), 0);
            context = ApplicationLoader.applicationContext;
            stringBuilder = new StringBuilder();
            stringBuilder.append("emoji");
            stringBuilder.append(this.currentAccount);
            this.emojiPreferences = context.getSharedPreferences(stringBuilder.toString(), 0);
        }
        this.enableJoined = this.notificationsPreferences.getBoolean("EnableContactJoined", true);
        this.secretWebpagePreview = this.mainPreferences.getInt("secretWebpage2", 2);
        this.maxGroupCount = this.mainPreferences.getInt("maxGroupCount", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.maxMegagroupCount = this.mainPreferences.getInt("maxMegagroupCount", 10000);
        this.maxRecentGifsCount = this.mainPreferences.getInt("maxRecentGifsCount", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.maxRecentStickersCount = this.mainPreferences.getInt("maxRecentStickersCount", 30);
        this.maxFaveStickersCount = this.mainPreferences.getInt("maxFaveStickersCount", 5);
        this.maxEditTime = this.mainPreferences.getInt("maxEditTime", 3600);
        this.ratingDecay = this.mainPreferences.getInt("ratingDecay", 2419200);
        this.linkPrefix = this.mainPreferences.getString("linkPrefix", "t.me");
        this.callReceiveTimeout = this.mainPreferences.getInt("callReceiveTimeout", 20000);
        this.callRingTimeout = this.mainPreferences.getInt("callRingTimeout", 90000);
        this.callConnectTimeout = this.mainPreferences.getInt("callConnectTimeout", DefaultLoadControl.DEFAULT_MAX_BUFFER_MS);
        this.callPacketTimeout = this.mainPreferences.getInt("callPacketTimeout", 10000);
        this.maxPinnedDialogsCount = this.mainPreferences.getInt("maxPinnedDialogsCount", 5);
        this.installReferer = this.mainPreferences.getString("installReferer", null);
        this.defaultP2pContacts = this.mainPreferences.getBoolean("defaultP2pContacts", false);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
    }

    public void updateConfig(final TL_config config) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                LocaleController.getInstance().loadRemoteLanguages(MessagesController.this.currentAccount);
                MessagesController.this.maxMegagroupCount = config.megagroup_size_max;
                MessagesController.this.maxGroupCount = config.chat_size_max;
                MessagesController.this.maxEditTime = config.edit_time_limit;
                MessagesController.this.ratingDecay = config.rating_e_decay;
                MessagesController.this.maxRecentGifsCount = config.saved_gifs_limit;
                MessagesController.this.maxRecentStickersCount = config.stickers_recent_limit;
                MessagesController.this.maxFaveStickersCount = config.stickers_faved_limit;
                MessagesController.this.revokeTimeLimit = config.revoke_time_limit;
                MessagesController.this.revokeTimePmLimit = config.revoke_pm_time_limit;
                MessagesController.this.canRevokePmInbox = config.revoke_pm_inbox;
                MessagesController.this.linkPrefix = config.me_url_prefix;
                if (MessagesController.this.linkPrefix.endsWith("/")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(0, MessagesController.this.linkPrefix.length() - 1);
                }
                if (MessagesController.this.linkPrefix.startsWith("https://")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(8);
                } else if (MessagesController.this.linkPrefix.startsWith("http://")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(7);
                }
                MessagesController.this.callReceiveTimeout = config.call_receive_timeout_ms;
                MessagesController.this.callRingTimeout = config.call_ring_timeout_ms;
                MessagesController.this.callConnectTimeout = config.call_connect_timeout_ms;
                MessagesController.this.callPacketTimeout = config.call_packet_timeout_ms;
                MessagesController.this.maxPinnedDialogsCount = config.pinned_dialogs_count_max;
                MessagesController.this.defaultP2pContacts = config.default_p2p_contacts;
                MessagesController.this.preloadFeaturedStickers = config.preload_featured_stickers;
                Editor editor = MessagesController.this.mainPreferences.edit();
                editor.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
                editor.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
                editor.putInt("maxEditTime", MessagesController.this.maxEditTime);
                editor.putInt("ratingDecay", MessagesController.this.ratingDecay);
                editor.putInt("maxRecentGifsCount", MessagesController.this.maxRecentGifsCount);
                editor.putInt("maxRecentStickersCount", MessagesController.this.maxRecentStickersCount);
                editor.putInt("maxFaveStickersCount", MessagesController.this.maxFaveStickersCount);
                editor.putInt("callReceiveTimeout", MessagesController.this.callReceiveTimeout);
                editor.putInt("callRingTimeout", MessagesController.this.callRingTimeout);
                editor.putInt("callConnectTimeout", MessagesController.this.callConnectTimeout);
                editor.putInt("callPacketTimeout", MessagesController.this.callPacketTimeout);
                editor.putString("linkPrefix", MessagesController.this.linkPrefix);
                editor.putInt("maxPinnedDialogsCount", MessagesController.this.maxPinnedDialogsCount);
                editor.putBoolean("defaultP2pContacts", MessagesController.this.defaultP2pContacts);
                editor.putBoolean("preloadFeaturedStickers", MessagesController.this.preloadFeaturedStickers);
                editor.putInt("revokeTimeLimit", MessagesController.this.revokeTimeLimit);
                editor.putInt("revokeTimePmLimit", MessagesController.this.revokeTimePmLimit);
                editor.putBoolean("canRevokePmInbox", MessagesController.this.canRevokePmInbox);
                editor.commit();
                LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(MessagesController.this.currentAccount, config.lang_pack_version);
            }
        });
    }

    public void addSupportUser() {
        TL_userForeign_old2 user = new TL_userForeign_old2();
        user.phone = "333";
        user.id = 333000;
        user.first_name = "Telegram";
        user.last_name = TtmlNode.ANONYMOUS_REGION_ID;
        user.status = null;
        user.photo = new TL_userProfilePhotoEmpty();
        putUser(user, true);
        user = new TL_userForeign_old2();
        user.phone = "42777";
        user.id = 777000;
        user.first_name = "Telegram";
        user.last_name = "Notifications";
        user.status = null;
        user.photo = new TL_userProfilePhotoEmpty();
        putUser(user, true);
    }

    public InputUser getInputUser(User user) {
        if (user == null) {
            return new TL_inputUserEmpty();
        }
        InputUser inputUser;
        if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            inputUser = new TL_inputUserSelf();
        } else {
            inputUser = new TL_inputUser();
            inputUser.user_id = user.id;
            inputUser.access_hash = user.access_hash;
        }
        return inputUser;
    }

    public InputUser getInputUser(int user_id) {
        return getInputUser(getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(user_id)));
    }

    public static InputChannel getInputChannel(Chat chat) {
        if (!(chat instanceof TL_channel)) {
            if (!(chat instanceof TL_channelForbidden)) {
                return new TL_inputChannelEmpty();
            }
        }
        InputChannel inputChat = new TL_inputChannel();
        inputChat.channel_id = chat.id;
        inputChat.access_hash = chat.access_hash;
        return inputChat;
    }

    public InputChannel getInputChannel(int chatId) {
        return getInputChannel(getChat(Integer.valueOf(chatId)));
    }

    public InputPeer getInputPeer(int id) {
        InputPeer tL_inputPeerChannel;
        if (id < 0) {
            Chat chat = getChat(Integer.valueOf(-id));
            if (ChatObject.isChannel(chat)) {
                tL_inputPeerChannel = new TL_inputPeerChannel();
                tL_inputPeerChannel.channel_id = -id;
                tL_inputPeerChannel.access_hash = chat.access_hash;
            } else {
                tL_inputPeerChannel = new TL_inputPeerChat();
                tL_inputPeerChannel.chat_id = -id;
            }
        } else {
            User user = getUser(Integer.valueOf(id));
            tL_inputPeerChannel = new TL_inputPeerUser();
            tL_inputPeerChannel.user_id = id;
            if (user != null) {
                tL_inputPeerChannel.access_hash = user.access_hash;
            }
        }
        return tL_inputPeerChannel;
    }

    public Peer getPeer(int id) {
        Peer inputPeer;
        if (id < 0) {
            Chat chat = getChat(Integer.valueOf(-id));
            if (!(chat instanceof TL_channel)) {
                if (!(chat instanceof TL_channelForbidden)) {
                    inputPeer = new TL_peerChat();
                    inputPeer.chat_id = -id;
                    return inputPeer;
                }
            }
            inputPeer = new TL_peerChannel();
            inputPeer.channel_id = -id;
            return inputPeer;
        }
        User user = getUser(Integer.valueOf(id));
        inputPeer = new TL_peerUser();
        inputPeer.user_id = id;
        return inputPeer;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        if (id == NotificationCenter.FileDidUpload) {
            location = args[0];
            InputFile file = args[1];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(location)) {
                TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
                req.file = file;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C18156());
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(location)) {
                this.uploadingAvatar = null;
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[0];
            Integer newMsgId = args[1];
            Long did = args[3];
            MessageObject obj = (MessageObject) this.dialogMessage.get(did.longValue());
            if (obj != null && (obj.getId() == msgId.intValue() || obj.messageOwner.local_id == msgId.intValue())) {
                obj.messageOwner.id = newMsgId.intValue();
                obj.messageOwner.send_state = 0;
            }
            TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did.longValue());
            if (dialog != null && dialog.top_message == msgId.intValue()) {
                dialog.top_message = newMsgId.intValue();
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            MessageObject obj2 = (MessageObject) this.dialogMessagesByIds.get(msgId.intValue());
            this.dialogMessagesByIds.remove(msgId.intValue());
            if (obj2 != null) {
                this.dialogMessagesByIds.put(newMsgId.intValue(), obj2);
            }
        } else if (id == NotificationCenter.updateMessageMedia) {
            Message message = args[0];
            MessageObject existMessageObject = (MessageObject) this.dialogMessagesByIds.get(message.id);
            if (existMessageObject != null) {
                existMessageObject.messageOwner.media = message.media;
                if (message.media.ttl_seconds == 0) {
                    return;
                }
                if ((message.media.photo instanceof TL_photoEmpty) || (message.media.document instanceof TL_documentEmpty)) {
                    existMessageObject.setType();
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                }
            }
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
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsers.clear();
        this.dialogs.clear();
        this.joiningToChannels.clear();
        this.channelViewsToSend.clear();
        this.dialogsServerOnly.clear();
        this.dialogsForward.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogMessagesByIds.clear();
        this.dialogMessagesByRandomIds.clear();
        this.channelAdmins.clear();
        this.loadingChannelAdmins.clear();
        this.users.clear();
        this.objectsByUsernames.clear();
        this.chats.clear();
        this.dialogMessage.clear();
        this.printingUsers.clear();
        this.printingStrings.clear();
        this.printingStringsTypes.clear();
        this.onlinePrivacy.clear();
        this.loadingPeerSettings.clear();
        this.lastPrintingStringCount = 0;
        this.nextDialogsCacheOffset = 0;
        Utilities.stageQueue.postRunnable(new C03567());
        this.createdDialogMainThreadIds.clear();
        this.blockedUsers.clear();
        this.sendingTypings.clear();
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskChannelId = 0;
        this.gettingNewDeleteTask = false;
        this.loadingDialogs = false;
        this.dialogsEndReached = false;
        this.serverDialogsEndReached = false;
        this.loadingBlockedUsers = false;
        this.firstGettingTask = false;
        this.updatingState = false;
        this.resetingDialogs = false;
        this.lastStatusUpdateTime = 0;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new C03658());
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public User getUser(Integer id) {
        return (User) this.users.get(id);
    }

    public TLObject getUserOrChat(String username) {
        if (username != null) {
            if (username.length() != 0) {
                return (TLObject) this.objectsByUsernames.get(username.toLowerCase());
            }
        }
        return null;
    }

    public ConcurrentHashMap<Integer, User> getUsers() {
        return this.users;
    }

    public Chat getChat(Integer id) {
        return (Chat) this.chats.get(id);
    }

    public EncryptedChat getEncryptedChat(Integer id) {
        return (EncryptedChat) this.encryptedChats.get(id);
    }

    public EncryptedChat getEncryptedChatDB(int chat_id, boolean created) {
        EncryptedChat chat = (EncryptedChat) this.encryptedChats.get(Integer.valueOf(chat_id));
        if (chat != null) {
            if (!created) {
                return chat;
            }
            if (!((chat instanceof TL_encryptedChatWaiting) || (chat instanceof TL_encryptedChatRequested))) {
                return chat;
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList<TLObject> result = new ArrayList();
        MessagesStorage.getInstance(this.currentAccount).getEncryptedChat(chat_id, countDownLatch, result);
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        if (result.size() != 2) {
            return chat;
        }
        chat = (EncryptedChat) result.get(0);
        User user = (User) result.get(1);
        putEncryptedChat(chat, false);
        putUser(user, true);
        return chat;
    }

    public boolean isDialogCreated(long dialog_id) {
        return this.createdDialogMainThreadIds.contains(Long.valueOf(dialog_id));
    }

    public void setLastCreatedDialogId(final long dialog_id, final boolean set) {
        if (set) {
            this.createdDialogMainThreadIds.add(Long.valueOf(dialog_id));
        } else {
            this.createdDialogMainThreadIds.remove(Long.valueOf(dialog_id));
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (set) {
                    MessagesController.this.createdDialogIds.add(Long.valueOf(dialog_id));
                } else {
                    MessagesController.this.createdDialogIds.remove(Long.valueOf(dialog_id));
                }
            }
        });
    }

    public ExportedChatInvite getExportedInvite(int chat_id) {
        return (ExportedChatInvite) this.exportedChats.get(chat_id);
    }

    public boolean putUser(User user, boolean fromCache) {
        if (user == null) {
            return false;
        }
        boolean z = (!fromCache || user.id / 1000 == 333 || user.id == 777000) ? false : true;
        fromCache = z;
        User oldUser = (User) this.users.get(Integer.valueOf(user.id));
        if (oldUser == user) {
            return false;
        }
        if (!(oldUser == null || TextUtils.isEmpty(oldUser.username))) {
            this.objectsByUsernames.remove(oldUser.username.toLowerCase());
        }
        if (!TextUtils.isEmpty(user.username)) {
            this.objectsByUsernames.put(user.username.toLowerCase(), user);
        }
        if (user.min) {
            if (oldUser == null) {
                this.users.put(Integer.valueOf(user.id), user);
            } else if (!fromCache) {
                if (user.bot) {
                    if (user.username != null) {
                        oldUser.username = user.username;
                        oldUser.flags |= 8;
                    } else {
                        oldUser.flags &= -9;
                        oldUser.username = null;
                    }
                }
                if (user.photo != null) {
                    oldUser.photo = user.photo;
                    oldUser.flags |= 32;
                } else {
                    oldUser.flags &= -33;
                    oldUser.photo = null;
                }
            }
        } else if (!fromCache) {
            this.users.put(Integer.valueOf(user.id), user);
            if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
                UserConfig.getInstance(this.currentAccount).saveConfig(true);
            }
            if (oldUser == null || user.status == null || oldUser.status == null || user.status.expires == oldUser.status.expires) {
                return false;
            }
            return true;
        } else if (oldUser == null) {
            this.users.put(Integer.valueOf(user.id), user);
        } else if (oldUser.min) {
            user.min = false;
            if (oldUser.bot) {
                if (oldUser.username != null) {
                    user.username = oldUser.username;
                    user.flags |= 8;
                } else {
                    user.flags &= -9;
                    user.username = null;
                }
            }
            if (oldUser.photo != null) {
                user.photo = oldUser.photo;
                user.flags |= 32;
            } else {
                user.flags &= -33;
                user.photo = null;
            }
            this.users.put(Integer.valueOf(user.id), user);
        }
        return false;
    }

    public void putUsers(ArrayList<User> users, boolean fromCache) {
        if (users != null) {
            if (!users.isEmpty()) {
                boolean updateStatus = false;
                int count = users.size();
                for (int a = 0; a < count; a++) {
                    if (putUser((User) users.get(a), fromCache)) {
                        updateStatus = true;
                    }
                }
                if (updateStatus) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                        }
                    });
                }
            }
        }
    }

    public void putChat(final Chat chat, boolean fromCache) {
        if (chat != null) {
            Chat oldChat = (Chat) this.chats.get(Integer.valueOf(chat.id));
            if (oldChat != chat) {
                if (!(oldChat == null || TextUtils.isEmpty(oldChat.username))) {
                    this.objectsByUsernames.remove(oldChat.username.toLowerCase());
                }
                if (!TextUtils.isEmpty(chat.username)) {
                    this.objectsByUsernames.put(chat.username.toLowerCase(), chat);
                }
                if (!chat.min) {
                    boolean z = false;
                    if (!fromCache) {
                        if (oldChat != null) {
                            if (chat.version != oldChat.version) {
                                this.loadedFullChats.remove(Integer.valueOf(chat.id));
                            }
                            if (oldChat.participants_count != 0 && chat.participants_count == 0) {
                                chat.participants_count = oldChat.participants_count;
                                chat.flags = 131072 | chat.flags;
                            }
                            boolean oldFlags = oldChat.banned_rights != null ? oldChat.banned_rights.flags : false;
                            if (chat.banned_rights != null) {
                                z = chat.banned_rights.flags;
                            }
                            if (oldFlags != z) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, chat);
                                    }
                                });
                            }
                        }
                        this.chats.put(Integer.valueOf(chat.id), chat);
                    } else if (oldChat == null) {
                        this.chats.put(Integer.valueOf(chat.id), chat);
                    } else if (oldChat.min) {
                        chat.min = false;
                        chat.title = oldChat.title;
                        chat.photo = oldChat.photo;
                        chat.broadcast = oldChat.broadcast;
                        chat.verified = oldChat.verified;
                        chat.megagroup = oldChat.megagroup;
                        chat.democracy = oldChat.democracy;
                        if (oldChat.username != null) {
                            chat.username = oldChat.username;
                            chat.flags |= 64;
                        } else {
                            chat.flags &= -65;
                            chat.username = null;
                        }
                        if (oldChat.participants_count != 0 && chat.participants_count == 0) {
                            chat.participants_count = oldChat.participants_count;
                            chat.flags = 131072 | chat.flags;
                        }
                        this.chats.put(Integer.valueOf(chat.id), chat);
                    }
                } else if (oldChat == null) {
                    this.chats.put(Integer.valueOf(chat.id), chat);
                } else if (!fromCache) {
                    oldChat.title = chat.title;
                    oldChat.photo = chat.photo;
                    oldChat.broadcast = chat.broadcast;
                    oldChat.verified = chat.verified;
                    oldChat.megagroup = chat.megagroup;
                    oldChat.democracy = chat.democracy;
                    if (chat.username != null) {
                        oldChat.username = chat.username;
                        oldChat.flags |= 64;
                    } else {
                        oldChat.flags &= -65;
                        oldChat.username = null;
                    }
                    if (chat.participants_count != 0) {
                        oldChat.participants_count = chat.participants_count;
                    }
                }
            }
        }
    }

    public void putChats(ArrayList<Chat> chats, boolean fromCache) {
        if (chats != null) {
            if (!chats.isEmpty()) {
                int count = chats.size();
                for (int a = 0; a < count; a++) {
                    putChat((Chat) chats.get(a), fromCache);
                }
            }
        }
    }

    public void setReferer(String referer) {
        if (referer != null) {
            this.installReferer = referer;
            this.mainPreferences.edit().putString("installReferer", referer).commit();
        }
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, boolean fromCache) {
        if (encryptedChat != null) {
            if (fromCache) {
                this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
            } else {
                this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            }
        }
    }

    public void putEncryptedChats(ArrayList<EncryptedChat> encryptedChats, boolean fromCache) {
        if (encryptedChats != null) {
            if (!encryptedChats.isEmpty()) {
                int count = encryptedChats.size();
                for (int a = 0; a < count; a++) {
                    putEncryptedChat((EncryptedChat) encryptedChats.get(a), fromCache);
                }
            }
        }
    }

    public TL_userFull getUserFull(int uid) {
        return (TL_userFull) this.fullUsers.get(uid);
    }

    public void cancelLoadFullUser(int uid) {
        this.loadingFullUsers.remove(Integer.valueOf(uid));
    }

    public void cancelLoadFullChat(int cid) {
        this.loadingFullChats.remove(Integer.valueOf(cid));
    }

    protected void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<TL_dialog> dialogs, long did) {
        if (did != 0 || (dialogs != null && !dialogs.isEmpty())) {
            TL_messages_getPeerDialogs req = new TL_messages_getPeerDialogs();
            if (dialogs != null) {
                for (int a = 0; a < dialogs.size(); a++) {
                    InputPeer inputPeer = getInputPeer((int) ((TL_dialog) dialogs.get(a)).id);
                    if (!(inputPeer instanceof TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                        TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
                        inputDialogPeer.peer = inputPeer;
                        req.peers.add(inputDialogPeer);
                    }
                }
            } else {
                InputPeer inputPeer2 = getInputPeer((int) did);
                if (!(inputPeer2 instanceof TL_inputPeerChannel) || inputPeer2.access_hash != 0) {
                    TL_inputDialogPeer inputDialogPeer2 = new TL_inputDialogPeer();
                    inputDialogPeer2.peer = inputPeer2;
                    req.peers.add(inputDialogPeer2);
                } else {
                    return;
                }
            }
            if (!req.peers.isEmpty()) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (response != null) {
                            TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                            ArrayList<Update> arrayList = new ArrayList();
                            for (int a = 0; a < res.dialogs.size(); a++) {
                                TL_dialog dialog = (TL_dialog) res.dialogs.get(a);
                                if (dialog.read_inbox_max_id == 0) {
                                    dialog.read_inbox_max_id = 1;
                                }
                                if (dialog.read_outbox_max_id == 0) {
                                    dialog.read_outbox_max_id = 1;
                                }
                                if (dialog.id == 0 && dialog.peer != null) {
                                    if (dialog.peer.user_id != 0) {
                                        dialog.id = (long) dialog.peer.user_id;
                                    } else if (dialog.peer.chat_id != 0) {
                                        dialog.id = (long) (-dialog.peer.chat_id);
                                    } else if (dialog.peer.channel_id != 0) {
                                        dialog.id = (long) (-dialog.peer.channel_id);
                                    }
                                }
                                Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, value.intValue())));
                                if (value.intValue() == 0) {
                                    if (dialog.peer.channel_id != 0) {
                                        TL_updateReadChannelInbox update = new TL_updateReadChannelInbox();
                                        update.channel_id = dialog.peer.channel_id;
                                        update.max_id = dialog.read_inbox_max_id;
                                        arrayList.add(update);
                                    } else {
                                        TL_updateReadHistoryInbox update2 = new TL_updateReadHistoryInbox();
                                        update2.peer = dialog.peer;
                                        update2.max_id = dialog.read_inbox_max_id;
                                        arrayList.add(update2);
                                    }
                                }
                                value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, value.intValue())));
                                if (value.intValue() == 0) {
                                    if (dialog.peer.channel_id != 0) {
                                        TL_updateReadChannelOutbox update3 = new TL_updateReadChannelOutbox();
                                        update3.channel_id = dialog.peer.channel_id;
                                        update3.max_id = dialog.read_outbox_max_id;
                                        arrayList.add(update3);
                                    } else {
                                        TL_updateReadHistoryOutbox update4 = new TL_updateReadHistoryOutbox();
                                        update4.peer = dialog.peer;
                                        update4.max_id = dialog.read_outbox_max_id;
                                        arrayList.add(update4);
                                    }
                                }
                            }
                            if (!arrayList.isEmpty()) {
                                MessagesController.this.processUpdateArray(arrayList, null, null, false);
                            }
                        }
                    }
                });
            }
        }
    }

    public boolean isChannelAdmin(int chatId, int uid) {
        ArrayList<Integer> array = (ArrayList) this.channelAdmins.get(chatId);
        return array != null && array.indexOf(Integer.valueOf(uid)) >= 0;
    }

    public void loadChannelAdmins(final int chatId, boolean cache) {
        if (this.loadingChannelAdmins.indexOfKey(chatId) < 0) {
            int a = 0;
            this.loadingChannelAdmins.put(chatId, 0);
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).loadChannelAdmins(chatId);
            } else {
                TL_channels_getParticipants req = new TL_channels_getParticipants();
                ArrayList<Integer> array = (ArrayList) this.channelAdmins.get(chatId);
                if (array != null) {
                    long acc = 0;
                    while (a < array.size()) {
                        acc = (((20261 * acc) + 2147483648L) + ((long) ((Integer) array.get(a)).intValue())) % 2147483648L;
                        a++;
                    }
                    req.hash = (int) acc;
                }
                req.channel = getInputChannel(chatId);
                req.limit = 100;
                req.filter = new TL_channelParticipantsAdmins();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (response instanceof TL_channels_channelParticipants) {
                            TL_channels_channelParticipants participants = (TL_channels_channelParticipants) response;
                            ArrayList<Integer> array = new ArrayList(participants.participants.size());
                            for (int a = 0; a < participants.participants.size(); a++) {
                                array.add(Integer.valueOf(((ChannelParticipant) participants.participants.get(a)).user_id));
                            }
                            MessagesController.this.processLoadedChannelAdmins(array, chatId, false);
                        }
                    }
                });
            }
        }
    }

    public void processLoadedChannelAdmins(final ArrayList<Integer> array, final int chatId, final boolean cache) {
        Collections.sort(array);
        if (!cache) {
            MessagesStorage.getInstance(this.currentAccount).putChannelAdmins(chatId, array);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.loadingChannelAdmins.delete(chatId);
                MessagesController.this.channelAdmins.put(chatId, array);
                if (cache) {
                    MessagesController.this.loadChannelAdmins(chatId, false);
                }
            }
        });
    }

    public void loadFullChat(int chat_id, int classGuid, boolean force) {
        int i = chat_id;
        int i2 = classGuid;
        boolean loaded = this.loadedFullChats.contains(Integer.valueOf(chat_id));
        if (this.loadingFullChats.contains(Integer.valueOf(chat_id))) {
        } else if (force || !loaded) {
            TLObject request;
            r7.loadingFullChats.add(Integer.valueOf(chat_id));
            long dialog_id = (long) (-i);
            Chat chat = getChat(Integer.valueOf(chat_id));
            TLObject req;
            if (ChatObject.isChannel(chat)) {
                req = new TL_channels_getFullChannel();
                req.channel = getInputChannel(chat);
                request = req;
                if (chat.megagroup) {
                    loadChannelAdmins(i, loaded ^ 1);
                }
            } else {
                req = new TL_messages_getFullChat();
                req.chat_id = i;
                request = req;
                if (r7.dialogs_read_inbox_max.get(Long.valueOf(dialog_id)) == null || r7.dialogs_read_outbox_max.get(Long.valueOf(dialog_id)) == null) {
                    reloadDialogsReadValue(null, dialog_id);
                }
            }
            TLObject request2 = request;
            final Chat chat2 = chat;
            final long j = dialog_id;
            AnonymousClass15 anonymousClass15 = r0;
            final int i3 = i;
            ConnectionsManager instance = ConnectionsManager.getInstance(r7.currentAccount);
            final int i4 = i2;
            AnonymousClass15 anonymousClass152 = new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error == null) {
                        final TL_messages_chatFull res = (TL_messages_chatFull) response;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(res.full_chat, false);
                        if (ChatObject.isChannel(chat2)) {
                            ArrayList<Update> arrayList;
                            Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                            if (value == null) {
                                value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j));
                            }
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(res.full_chat.read_inbox_max_id, value.intValue())));
                            if (value.intValue() == 0) {
                                arrayList = new ArrayList();
                                TL_updateReadChannelInbox update = new TL_updateReadChannelInbox();
                                update.channel_id = i3;
                                update.max_id = res.full_chat.read_inbox_max_id;
                                arrayList.add(update);
                                MessagesController.this.processUpdateArray(arrayList, null, null, false);
                            }
                            value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                            if (value == null) {
                                value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j));
                            }
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(res.full_chat.read_outbox_max_id, value.intValue())));
                            if (value.intValue() == 0) {
                                arrayList = new ArrayList();
                                TL_updateReadChannelOutbox update2 = new TL_updateReadChannelOutbox();
                                update2.channel_id = i3;
                                update2.max_id = res.full_chat.read_outbox_max_id;
                                arrayList.add(update2);
                                MessagesController.this.processUpdateArray(arrayList, null, null, false);
                            }
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.applyDialogNotificationsSettings((long) (-i3), res.full_chat.notify_settings);
                                for (int a = 0; a < res.full_chat.bot_info.size(); a++) {
                                    DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo((BotInfo) res.full_chat.bot_info.get(a));
                                }
                                MessagesController.this.exportedChats.put(i3, res.full_chat.exported_invite);
                                MessagesController.this.loadingFullChats.remove(Integer.valueOf(i3));
                                MessagesController.this.loadedFullChats.add(Integer.valueOf(i3));
                                MessagesController.this.putUsers(res.users, false);
                                MessagesController.this.putChats(res.chats, false);
                                if (res.full_chat.stickerset != null) {
                                    DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(res.full_chat.stickerset);
                                }
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, res.full_chat, Integer.valueOf(i4), Boolean.valueOf(false), null);
                            }
                        });
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.this.checkChannelError(error.text, i3);
                            MessagesController.this.loadingFullChats.remove(Integer.valueOf(i3));
                        }
                    });
                }
            };
            int reqId = instance.sendRequest(request2, anonymousClass15);
            if (i2 != 0) {
                ConnectionsManager.getInstance(r7.currentAccount).bindRequestToGuid(reqId, i2);
            }
        } else {
            boolean z = loaded;
        }
    }

    public void loadFullUser(final User user, final int classGuid, boolean force) {
        if (!(user == null || this.loadingFullUsers.contains(Integer.valueOf(user.id)))) {
            if (force || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                TL_users_getFullUser req = new TL_users_getFullUser();
                req.id = getInputUser(user);
                long dialog_id = (long) user.id;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id)) == null) {
                    reloadDialogsReadValue(null, dialog_id);
                }
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$16$2 */
                    class C03202 implements Runnable {
                        C03202() {
                        }

                        public void run() {
                            MessagesController.this.loadingFullUsers.remove(Integer.valueOf(user.id));
                        }
                    }

                    public void run(final TLObject response, TL_error error) {
                        if (error == null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TL_userFull userFull = response;
                                    MessagesController.this.applyDialogNotificationsSettings((long) user.id, userFull.notify_settings);
                                    if (userFull.bot_info instanceof TL_botInfo) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo(userFull.bot_info);
                                    }
                                    MessagesController.this.fullUsers.put(user.id, userFull);
                                    MessagesController.this.loadingFullUsers.remove(Integer.valueOf(user.id));
                                    MessagesController.this.loadedFullUsers.add(Integer.valueOf(user.id));
                                    String names = new StringBuilder();
                                    names.append(user.first_name);
                                    names.append(user.last_name);
                                    names.append(user.username);
                                    names = names.toString();
                                    ArrayList<User> users = new ArrayList();
                                    users.add(userFull.user);
                                    MessagesController.this.putUsers(users, false);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(users, null, false, true);
                                    if (names != null) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(userFull.user.first_name);
                                        stringBuilder.append(userFull.user.last_name);
                                        stringBuilder.append(userFull.user.username);
                                        if (!names.equals(stringBuilder.toString())) {
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                                        }
                                    }
                                    if (userFull.bot_info instanceof TL_botInfo) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, userFull.bot_info, Integer.valueOf(classGuid));
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, Integer.valueOf(user.id), userFull);
                                }
                            });
                        } else {
                            AndroidUtilities.runOnUIThread(new C03202());
                        }
                    }
                }), classGuid);
            }
        }
    }

    private void reloadMessages(ArrayList<Integer> mids, long dialog_id) {
        if (!mids.isEmpty()) {
            TLObject request;
            ArrayList<Integer> result = new ArrayList();
            Chat chat = ChatObject.getChatByDialog(dialog_id, this.currentAccount);
            if (ChatObject.isChannel(chat)) {
                request = new TL_channels_getMessages();
                request.channel = getInputChannel(chat);
                request.id = result;
            } else {
                request = new TL_messages_getMessages();
                request.id = result;
            }
            TLObject request2 = request;
            ArrayList<Integer> arrayList = (ArrayList) this.reloadingMessages.get(dialog_id);
            for (int a = 0; a < mids.size(); a++) {
                Integer mid = (Integer) mids.get(a);
                if (arrayList == null || !arrayList.contains(mid)) {
                    result.add(mid);
                }
            }
            if (!result.isEmpty()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.reloadingMessages.put(dialog_id, arrayList);
                }
                arrayList.addAll(result);
                final long j = dialog_id;
                final Chat chat2 = chat;
                final ArrayList<Integer> arrayList2 = result;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request2, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        AnonymousClass17 anonymousClass17 = this;
                        if (error == null) {
                            int a;
                            messages_Messages messagesRes = (messages_Messages) response;
                            SparseArray<User> usersLocal = new SparseArray();
                            boolean z = false;
                            for (a = 0; a < messagesRes.users.size(); a++) {
                                User u = (User) messagesRes.users.get(a);
                                usersLocal.put(u.id, u);
                            }
                            SparseArray<Chat> chatsLocal = new SparseArray();
                            for (a = 0; a < messagesRes.chats.size(); a++) {
                                Chat c = (Chat) messagesRes.chats.get(a);
                                chatsLocal.put(c.id, c);
                            }
                            Integer inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                            if (inboxValue == null) {
                                inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j));
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), inboxValue);
                            }
                            Integer inboxValue2 = inboxValue;
                            inboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                            boolean z2 = true;
                            if (inboxValue == null) {
                                inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j));
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), inboxValue);
                            }
                            Integer outboxValue = inboxValue;
                            ArrayList<MessageObject> objects = new ArrayList();
                            a = 0;
                            while (true) {
                                int a2 = a;
                                ArrayList<MessageObject> objects2;
                                if (a2 < messagesRes.messages.size()) {
                                    Message message = (Message) messagesRes.messages.get(a2);
                                    if (chat2 != null && chat2.megagroup) {
                                        message.flags |= Integer.MIN_VALUE;
                                    }
                                    message.dialog_id = j;
                                    message.unread = (message.out ? outboxValue : inboxValue2).intValue() < message.id ? z2 : z;
                                    MessageObject messageObject = r2;
                                    int a3 = a2;
                                    objects2 = objects;
                                    MessageObject messageObject2 = new MessageObject(MessagesController.this.currentAccount, message, (SparseArray) usersLocal, (SparseArray) chatsLocal, true);
                                    objects2.add(messageObject);
                                    a = a3 + 1;
                                    objects = objects2;
                                    z = false;
                                    z2 = true;
                                } else {
                                    objects2 = objects;
                                    ImageLoader.saveMessagesThumbs(messagesRes.messages);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messagesRes, j, -1, 0, false);
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            ArrayList<Integer> arrayList = (ArrayList) MessagesController.this.reloadingMessages.get(j);
                                            if (arrayList != null) {
                                                arrayList.removeAll(arrayList2);
                                                if (arrayList.isEmpty()) {
                                                    MessagesController.this.reloadingMessages.remove(j);
                                                }
                                            }
                                            MessageObject dialogObj = (MessageObject) MessagesController.this.dialogMessage.get(j);
                                            if (dialogObj != null) {
                                                int a = 0;
                                                while (a < objects2.size()) {
                                                    MessageObject obj = (MessageObject) objects2.get(a);
                                                    if (dialogObj == null || dialogObj.getId() != obj.getId()) {
                                                        a++;
                                                    } else {
                                                        MessagesController.this.dialogMessage.put(j, obj);
                                                        if (obj.messageOwner.to_id.channel_id == 0) {
                                                            MessageObject obj2 = (MessageObject) MessagesController.this.dialogMessagesByIds.get(obj.getId());
                                                            MessagesController.this.dialogMessagesByIds.remove(obj.getId());
                                                            if (obj2 != null) {
                                                                MessagesController.this.dialogMessagesByIds.put(obj2.getId(), obj2);
                                                            }
                                                        }
                                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                                    }
                                                }
                                            }
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), objects2);
                                        }
                                    });
                                    return;
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public void hideReportSpam(long dialogId, User currentUser, Chat currentChat) {
        if (currentUser != null || currentChat != null) {
            Editor editor = this.notificationsPreferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("spam3_");
            stringBuilder.append(dialogId);
            editor.putInt(stringBuilder.toString(), 1);
            editor.commit();
            if (((int) dialogId) != 0) {
                TL_messages_hideReportSpam req = new TL_messages_hideReportSpam();
                if (currentUser != null) {
                    req.peer = getInputPeer(currentUser.id);
                } else if (currentChat != null) {
                    req.peer = getInputPeer(-currentChat.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            }
        }
    }

    public void reportSpam(long dialogId, User currentUser, Chat currentChat, EncryptedChat currentEncryptedChat) {
        if (currentUser != null || currentChat != null || currentEncryptedChat != null) {
            Editor editor = this.notificationsPreferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("spam3_");
            stringBuilder.append(dialogId);
            editor.putInt(stringBuilder.toString(), 1);
            editor.commit();
            if (((int) dialogId) == 0) {
                if (currentEncryptedChat != null) {
                    if (currentEncryptedChat.access_hash != 0) {
                        TL_messages_reportEncryptedSpam req = new TL_messages_reportEncryptedSpam();
                        req.peer = new TL_inputEncryptedChat();
                        req.peer.chat_id = currentEncryptedChat.id;
                        req.peer.access_hash = currentEncryptedChat.access_hash;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                            }
                        }, 2);
                    }
                }
                return;
            }
            TL_messages_reportSpam req2 = new TL_messages_reportSpam();
            if (currentChat != null) {
                req2.peer = getInputPeer(-currentChat.id);
            } else if (currentUser != null) {
                req2.peer = getInputPeer(currentUser.id);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            }, 2);
        }
    }

    public void loadPeerSettings(User currentUser, Chat currentChat) {
        if (currentUser != null || currentChat != null) {
            long dialogId;
            if (currentUser != null) {
                dialogId = (long) currentUser.id;
            } else {
                dialogId = (long) (-currentChat.id);
            }
            if (this.loadingPeerSettings.indexOfKey(dialogId) < 0) {
                StringBuilder stringBuilder;
                this.loadingPeerSettings.put(dialogId, Boolean.valueOf(true));
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("request spam button for ");
                    stringBuilder.append(dialogId);
                    FileLog.m0d(stringBuilder.toString());
                }
                SharedPreferences sharedPreferences = this.notificationsPreferences;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("spam3_");
                stringBuilder2.append(dialogId);
                if (sharedPreferences.getInt(stringBuilder2.toString(), 0) == 1) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("spam button already hidden for ");
                        stringBuilder.append(dialogId);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    return;
                }
                boolean hidden = this.notificationsPreferences;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("spam_");
                stringBuilder3.append(dialogId);
                if (hidden.getBoolean(stringBuilder3.toString(), false)) {
                    TL_messages_hideReportSpam req = new TL_messages_hideReportSpam();
                    if (currentUser != null) {
                        req.peer = getInputPeer(currentUser.id);
                    } else if (currentChat != null) {
                        req.peer = getInputPeer(-currentChat.id);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                        /* renamed from: org.telegram.messenger.MessagesController$21$1 */
                        class C03231 implements Runnable {
                            C03231() {
                            }

                            public void run() {
                                MessagesController.this.loadingPeerSettings.remove(dialogId);
                                Editor editor = MessagesController.this.notificationsPreferences.edit();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("spam_");
                                stringBuilder.append(dialogId);
                                editor.remove(stringBuilder.toString());
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("spam3_");
                                stringBuilder.append(dialogId);
                                editor.putInt(stringBuilder.toString(), 1);
                                editor.commit();
                            }
                        }

                        public void run(TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new C03231());
                        }
                    });
                    return;
                }
                TL_messages_getPeerSettings req2 = new TL_messages_getPeerSettings();
                if (currentUser != null) {
                    req2.peer = getInputPeer(currentUser.id);
                } else if (currentChat != null) {
                    req2.peer = getInputPeer(-currentChat.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.loadingPeerSettings.remove(dialogId);
                                if (response != null) {
                                    TL_peerSettings res = response;
                                    Editor editor = MessagesController.this.notificationsPreferences.edit();
                                    StringBuilder stringBuilder;
                                    if (res.report_spam) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("show spam button for ");
                                            stringBuilder.append(dialogId);
                                            FileLog.m0d(stringBuilder.toString());
                                        }
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("spam3_");
                                        stringBuilder.append(dialogId);
                                        editor.putInt(stringBuilder.toString(), 2);
                                        editor.commit();
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoaded, Long.valueOf(dialogId));
                                        return;
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("don't show spam button for ");
                                        stringBuilder.append(dialogId);
                                        FileLog.m0d(stringBuilder.toString());
                                    }
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("spam3_");
                                    stringBuilder.append(dialogId);
                                    editor.putInt(stringBuilder.toString(), 1);
                                    editor.commit();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    protected void processNewChannelDifferenceParams(int pts, int pts_count, int channelId) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processNewChannelDifferenceParams pts = ");
            stringBuilder.append(pts);
            stringBuilder.append(" pts_count = ");
            stringBuilder.append(pts_count);
            stringBuilder.append(" channeldId = ");
            stringBuilder.append(channelId);
            FileLog.m0d(stringBuilder.toString());
        }
        int channelPts = this.channelsPts.get(channelId);
        if (channelPts == 0) {
            channelPts = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(channelId);
            if (channelPts == 0) {
                channelPts = 1;
            }
            this.channelsPts.put(channelId, channelPts);
        }
        if (channelPts + pts_count == pts) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(channelId, pts);
            MessagesStorage.getInstance(this.currentAccount).saveChannelPts(channelId, pts);
        } else if (channelPts != pts) {
            long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
            if (!(this.gettingDifferenceChannels.get(channelId) || updatesStartWaitTime == 0)) {
                if (Math.abs(System.currentTimeMillis() - updatesStartWaitTime) > 1500) {
                    getChannelDifference(channelId);
                    return;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("ADD CHANNEL UPDATE TO QUEUE pts = ");
                stringBuilder2.append(pts);
                stringBuilder2.append(" pts_count = ");
                stringBuilder2.append(pts_count);
                FileLog.m0d(stringBuilder2.toString());
            }
            if (updatesStartWaitTime == 0) {
                this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
            }
            UserActionUpdatesPts updates = new UserActionUpdatesPts();
            updates.pts = pts;
            updates.pts_count = pts_count;
            updates.chat_id = channelId;
            ArrayList<Updates> arrayList = (ArrayList) this.updatesQueueChannels.get(channelId);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.updatesQueueChannels.put(channelId, arrayList);
            }
            arrayList.add(updates);
        }
    }

    protected void processNewDifferenceParams(int seq, int pts, int date, int pts_count) {
        MessagesController messagesController = this;
        int i = seq;
        int i2 = pts;
        int i3 = date;
        int i4 = pts_count;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processNewDifferenceParams seq = ");
            stringBuilder.append(i);
            stringBuilder.append(" pts = ");
            stringBuilder.append(i2);
            stringBuilder.append(" date = ");
            stringBuilder.append(i3);
            stringBuilder.append(" pts_count = ");
            stringBuilder.append(i4);
            FileLog.m0d(stringBuilder.toString());
        }
        if (i2 != -1) {
            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + i4 == i2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("APPLY PTS");
                }
                MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(i2);
                MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
            } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != i2) {
                if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimePts == 0)) {
                    if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) > 1500) {
                        getDifference();
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("ADD UPDATE TO QUEUE pts = ");
                    stringBuilder2.append(i2);
                    stringBuilder2.append(" pts_count = ");
                    stringBuilder2.append(i4);
                    FileLog.m0d(stringBuilder2.toString());
                }
                if (messagesController.updatesStartWaitTimePts == 0) {
                    messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                }
                UserActionUpdatesPts updates = new UserActionUpdatesPts();
                updates.pts = i2;
                updates.pts_count = i4;
                messagesController.updatesQueuePts.add(updates);
            }
        }
        if (i == -1) {
            return;
        }
        if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 == i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("APPLY SEQ");
            }
            MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(i);
            if (i3 != -1) {
                MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(i3);
            }
            MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
        } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() != i) {
            if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimeSeq == 0)) {
                if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) > 1500) {
                    getDifference();
                    return;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("ADD UPDATE TO QUEUE seq = ");
                stringBuilder3.append(i);
                FileLog.m0d(stringBuilder3.toString());
            }
            if (messagesController.updatesStartWaitTimeSeq == 0) {
                messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
            }
            UserActionUpdatesSeq updates2 = new UserActionUpdatesSeq();
            updates2.seq = i;
            messagesController.updatesQueueSeq.add(updates2);
        }
    }

    public void didAddedNewTask(final int minDate, final SparseArray<ArrayList<Long>> mids) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if ((MessagesController.this.currentDeletingTaskMids == null && !MessagesController.this.gettingNewDeleteTask) || (MessagesController.this.currentDeletingTaskTime != 0 && minDate < MessagesController.this.currentDeletingTaskTime)) {
                    MessagesController.this.getNewDeleteTask(null, 0);
                }
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didCreatedNewDeleteTask, mids);
            }
        });
    }

    public void getNewDeleteTask(final ArrayList<Integer> oldTask, final int channelId) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.this.gettingNewDeleteTask = true;
                MessagesStorage.getInstance(MessagesController.this.currentAccount).getNewTask(oldTask, channelId);
            }
        });
    }

    private boolean checkDeletingTask(boolean runnable) {
        int currentServerTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (this.currentDeletingTaskMids == null || (!runnable && (this.currentDeletingTaskTime == 0 || this.currentDeletingTaskTime > currentServerTime))) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (!(this.currentDeleteTaskRunnable == null || runnable)) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        final ArrayList<Integer> mids = new ArrayList(this.currentDeletingTaskMids);
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$26$1 */
            class C03251 implements Runnable {
                C03251() {
                }

                public void run() {
                    MessagesController.this.getNewDeleteTask(mids, MessagesController.this.currentDeletingTaskChannelId);
                    MessagesController.this.currentDeletingTaskTime = 0;
                    MessagesController.this.currentDeletingTaskMids = null;
                }
            }

            public void run() {
                if (mids.isEmpty() || ((Integer) mids.get(0)).intValue() <= 0) {
                    MessagesController.this.deleteMessages(mids, null, null, 0, false);
                } else {
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).emptyMessagesMedia(mids);
                }
                Utilities.stageQueue.postRunnable(new C03251());
            }
        });
        return true;
    }

    public void processLoadedDeleteTask(final int taskTime, final ArrayList<Integer> messages, int channelId) {
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$27$1 */
            class C03261 implements Runnable {
                C03261() {
                }

                public void run() {
                    MessagesController.this.checkDeletingTask(true);
                }
            }

            public void run() {
                MessagesController.this.gettingNewDeleteTask = false;
                if (messages != null) {
                    MessagesController.this.currentDeletingTaskTime = taskTime;
                    MessagesController.this.currentDeletingTaskMids = messages;
                    if (MessagesController.this.currentDeleteTaskRunnable != null) {
                        Utilities.stageQueue.cancelRunnable(MessagesController.this.currentDeleteTaskRunnable);
                        MessagesController.this.currentDeleteTaskRunnable = null;
                    }
                    if (!MessagesController.this.checkDeletingTask(false)) {
                        MessagesController.this.currentDeleteTaskRunnable = new C03261();
                        Utilities.stageQueue.postRunnable(MessagesController.this.currentDeleteTaskRunnable, ((long) Math.abs(ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime() - MessagesController.this.currentDeletingTaskTime)) * 1000);
                        return;
                    }
                    return;
                }
                MessagesController.this.currentDeletingTaskTime = 0;
                MessagesController.this.currentDeletingTaskMids = null;
            }
        });
    }

    public void loadDialogPhotos(int did, int count, long max_id, boolean fromCache, int classGuid) {
        MessagesController messagesController = this;
        int i = count;
        long j = max_id;
        int i2 = classGuid;
        if (fromCache) {
            MessagesStorage.getInstance(messagesController.currentAccount).getDialogPhotos(did, i, j, i2);
        } else if (did > 0) {
            User user = getUser(Integer.valueOf(did));
            if (user != null) {
                TL_photos_getUserPhotos req = new TL_photos_getUserPhotos();
                req.limit = i;
                req.offset = 0;
                req.max_id = (long) ((int) j);
                req.user_id = getInputUser(user);
                r2 = did;
                r3 = i;
                AnonymousClass28 anonymousClass28 = r0;
                r4 = j;
                ConnectionsManager instance = ConnectionsManager.getInstance(messagesController.currentAccount);
                r6 = i2;
                AnonymousClass28 anonymousClass282 = new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesController.this.processLoadedUserPhotos((photos_Photos) response, r2, r3, r4, false, r6);
                        }
                    }
                };
                ConnectionsManager.getInstance(messagesController.currentAccount).bindRequestToGuid(instance.sendRequest(req, anonymousClass28), i2);
            }
        } else if (did < 0) {
            TL_messages_search req2 = new TL_messages_search();
            req2.filter = new TL_inputMessagesFilterChatPhotos();
            req2.limit = i;
            req2.offset_id = (int) j;
            req2.f49q = TtmlNode.ANONYMOUS_REGION_ID;
            req2.peer = getInputPeer(did);
            r2 = did;
            r3 = i;
            r4 = j;
            r6 = i2;
            ConnectionsManager.getInstance(messagesController.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        messages_Messages messages = (messages_Messages) response;
                        photos_Photos res = new TL_photos_photos();
                        res.count = messages.count;
                        res.users.addAll(messages.users);
                        for (int a = 0; a < messages.messages.size(); a++) {
                            Message message = (Message) messages.messages.get(a);
                            if (message.action != null) {
                                if (message.action.photo != null) {
                                    res.photos.add(message.action.photo);
                                }
                            }
                        }
                        MessagesController.this.processLoadedUserPhotos(res, r2, r3, r4, false, r6);
                    }
                }
            }), i2);
        }
    }

    public void blockUser(int user_id) {
        final User user = getUser(Integer.valueOf(user_id));
        if (user != null) {
            if (!this.blockedUsers.contains(Integer.valueOf(user_id))) {
                this.blockedUsers.add(Integer.valueOf(user_id));
                if (user.bot) {
                    DataQuery.getInstance(this.currentAccount).removeInline(user_id);
                } else {
                    DataQuery.getInstance(this.currentAccount).removePeer(user_id);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                TL_contacts_block req = new TL_contacts_block();
                req.id = getInputUser(user);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            ArrayList<Integer> ids = new ArrayList();
                            ids.add(Integer.valueOf(user.id));
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(ids, false);
                        }
                    }
                });
            }
        }
    }

    public void setUserBannedRole(int chatId, User user, TL_channelBannedRights rights, boolean isMegagroup, BaseFragment parentFragment) {
        if (user != null) {
            if (rights != null) {
                TL_channels_editBanned req = new TL_channels_editBanned();
                req.channel = getInputChannel(chatId);
                req.user_id = getInputUser(user);
                req.banned_rights = rights;
                final int i = chatId;
                final BaseFragment baseFragment = parentFragment;
                final TL_channels_editBanned tL_channels_editBanned = req;
                final boolean z = isMegagroup;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$31$1 */
                    class C03281 implements Runnable {
                        C03281() {
                        }

                        public void run() {
                            MessagesController.this.loadFullChat(i, 0, true);
                        }
                    }

                    public void run(TLObject response, final TL_error error) {
                        if (error == null) {
                            MessagesController.this.processUpdates((Updates) response, false);
                            AndroidUtilities.runOnUIThread(new C03281(), 1000);
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, error, baseFragment, tL_channels_editBanned, Boolean.valueOf(true ^ z));
                            }
                        });
                    }
                });
            }
        }
    }

    public void setUserAdminRole(int chatId, User user, TL_channelAdminRights rights, boolean isMegagroup, BaseFragment parentFragment) {
        if (user != null) {
            if (rights != null) {
                TL_channels_editAdmin req = new TL_channels_editAdmin();
                req.channel = getInputChannel(chatId);
                req.user_id = getInputUser(user);
                req.admin_rights = rights;
                final int i = chatId;
                final BaseFragment baseFragment = parentFragment;
                final TL_channels_editAdmin tL_channels_editAdmin = req;
                final boolean z = isMegagroup;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$32$1 */
                    class C03301 implements Runnable {
                        C03301() {
                        }

                        public void run() {
                            MessagesController.this.loadFullChat(i, 0, true);
                        }
                    }

                    public void run(TLObject response, final TL_error error) {
                        if (error == null) {
                            MessagesController.this.processUpdates((Updates) response, false);
                            AndroidUtilities.runOnUIThread(new C03301(), 1000);
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, error, baseFragment, tL_channels_editAdmin, Boolean.valueOf(true ^ z));
                            }
                        });
                    }
                });
            }
        }
    }

    public void unblockUser(int user_id) {
        TL_contacts_unblock req = new TL_contacts_unblock();
        final User user = getUser(Integer.valueOf(user_id));
        if (user != null) {
            this.blockedUsers.remove(Integer.valueOf(user.id));
            req.id = getInputUser(user);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).deleteBlockedUser(user.id);
                }
            });
        }
    }

    public void getBlockedUsers(boolean cache) {
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            if (!this.loadingBlockedUsers) {
                this.loadingBlockedUsers = true;
                if (cache) {
                    MessagesStorage.getInstance(this.currentAccount).getBlockedUsers();
                } else {
                    TL_contacts_getBlocked req = new TL_contacts_getBlocked();
                    req.offset = 0;
                    req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            ArrayList<Integer> blocked = new ArrayList();
                            ArrayList<User> users = null;
                            if (error == null) {
                                contacts_Blocked res = (contacts_Blocked) response;
                                Iterator it = res.blocked.iterator();
                                while (it.hasNext()) {
                                    blocked.add(Integer.valueOf(((TL_contactBlocked) it.next()).user_id));
                                }
                                users = res.users;
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(blocked, true);
                            }
                            MessagesController.this.processLoadedBlockedUsers(blocked, users, false);
                        }
                    });
                }
            }
        }
    }

    public void processLoadedBlockedUsers(final ArrayList<Integer> ids, final ArrayList<User> users, final boolean cache) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (users != null) {
                    MessagesController.this.putUsers(users, cache);
                }
                MessagesController.this.loadingBlockedUsers = false;
                if (ids.isEmpty() && cache && !UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded) {
                    MessagesController.this.getBlockedUsers(false);
                    return;
                }
                if (!cache) {
                    UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded = true;
                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                }
                MessagesController.this.blockedUsers = ids;
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            }
        });
    }

    public void deleteUserPhoto(InputPhoto photo) {
        if (photo == null) {
            TL_photos_updateProfilePhoto req = new TL_photos_updateProfilePhoto();
            req.id = new TL_inputPhotoEmpty();
            UserConfig.getInstance(this.currentAccount).getCurrentUser().photo = new TL_userProfilePhotoEmpty();
            User user = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            }
            if (user != null) {
                user.photo = UserConfig.getInstance(this.currentAccount).getCurrentUser().photo;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$36$1 */
                    class C03321 implements Runnable {
                        C03321() {
                        }

                        public void run() {
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(true);
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                            if (user == null) {
                                user = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
                                MessagesController.this.putUser(user, false);
                            } else {
                                UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(user);
                            }
                            if (user != null) {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(user.id);
                                ArrayList<User> users = new ArrayList();
                                users.add(user);
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(users, null, false, true);
                                user.photo = (UserProfilePhoto) response;
                                AndroidUtilities.runOnUIThread(new C03321());
                            }
                        }
                    }
                });
            } else {
                return;
            }
        }
        TL_photos_deletePhotos req2 = new TL_photos_deletePhotos();
        req2.id.add(photo);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void processLoadedUserPhotos(photos_Photos res, int did, int count, long max_id, boolean fromCache, int classGuid) {
        if (fromCache) {
            if (res != null) {
                if (res.photos.isEmpty()) {
                }
            }
            loadDialogPhotos(did, count, max_id, false, classGuid);
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
        MessagesStorage.getInstance(this.currentAccount).putDialogPhotos(did, res);
        final photos_Photos photos_photos = res;
        final boolean z = fromCache;
        final int i = did;
        final int i2 = count;
        final int i3 = classGuid;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.putUsers(photos_photos.users, z);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z), Integer.valueOf(i3), photos_photos.photos);
            }
        });
    }

    public void uploadAndApplyUserAvatar(PhotoSize bigPhoto) {
        if (bigPhoto != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(FileLoader.getDirectory(4));
            stringBuilder.append("/");
            stringBuilder.append(bigPhoto.location.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(bigPhoto.location.local_id);
            stringBuilder.append(".jpg");
            this.uploadingAvatar = stringBuilder.toString();
            FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
        }
    }

    public void markChannelDialogMessageAsDeleted(ArrayList<Integer> messages, int channelId) {
        MessageObject obj = (MessageObject) this.dialogMessage.get((long) (-channelId));
        if (obj != null) {
            for (int a = 0; a < messages.size(); a++) {
                if (obj.getId() == ((Integer) messages.get(a)).intValue()) {
                    obj.deleted = true;
                    return;
                }
            }
        }
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, EncryptedChat encryptedChat, int channelId, boolean forAll) {
        deleteMessages(messages, randoms, encryptedChat, channelId, forAll, 0, null);
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, EncryptedChat encryptedChat, int channelId, boolean forAll, long taskId, TLObject taskRequest) {
        NativeByteBuffer data;
        MessagesController messagesController = this;
        ArrayList arrayList = messages;
        ArrayList<Long> arrayList2 = randoms;
        EncryptedChat encryptedChat2 = encryptedChat;
        final int i = channelId;
        if ((arrayList != null && !messages.isEmpty()) || taskRequest != null) {
            ArrayList<Integer> toSend = null;
            NativeByteBuffer data2 = null;
            if (taskId == 0) {
                if (i == 0) {
                    for (int a = 0; a < messages.size(); a++) {
                        MessageObject obj = (MessageObject) messagesController.dialogMessagesByIds.get(((Integer) arrayList.get(a)).intValue());
                        if (obj != null) {
                            obj.deleted = true;
                        }
                    }
                } else {
                    markChannelDialogMessageAsDeleted(arrayList, i);
                }
                toSend = new ArrayList();
                for (int a2 = 0; a2 < messages.size(); a2++) {
                    Integer mid = (Integer) arrayList.get(a2);
                    if (mid.intValue() > 0) {
                        toSend.add(mid);
                    }
                }
                MessagesStorage.getInstance(messagesController.currentAccount).markMessagesAsDeleted(arrayList, true, i);
                MessagesStorage.getInstance(messagesController.currentAccount).updateDialogsWithDeletedMessages(arrayList, null, true, i);
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(channelId));
            }
            long newTaskId;
            boolean z;
            if (i != 0) {
                TL_channels_deleteMessages req;
                if (taskRequest != null) {
                    req = (TL_channels_deleteMessages) taskRequest;
                    newTaskId = taskId;
                } else {
                    req = new TL_channels_deleteMessages();
                    req.id = toSend;
                    req.channel = getInputChannel(i);
                    try {
                        newTaskId = new NativeByteBuffer(8 + req.getObjectSize());
                        newTaskId.writeInt32(7);
                        newTaskId.writeInt32(i);
                        req.serializeToStream(newTaskId);
                    } catch (Throwable e) {
                        data = data2;
                        FileLog.m3e(e);
                        newTaskId = data;
                    }
                    newTaskId = MessagesStorage.getInstance(messagesController.currentAccount).createPendingTask(newTaskId);
                }
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                            MessagesController.this.processNewChannelDifferenceParams(res.pts, res.pts_count, i);
                        }
                        if (newTaskId != 0) {
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                        }
                    }
                });
                z = forAll;
            } else {
                TL_messages_deleteMessages req2;
                if (!(arrayList2 == null || encryptedChat2 == null || randoms.isEmpty())) {
                    SecretChatHelper.getInstance(messagesController.currentAccount).sendMessagesDeleteMessage(encryptedChat2, arrayList2, null);
                }
                if (taskRequest != null) {
                    z = forAll;
                    req2 = (TL_messages_deleteMessages) taskRequest;
                    newTaskId = taskId;
                } else {
                    req2 = new TL_messages_deleteMessages();
                    req2.id = toSend;
                    req2.revoke = forAll;
                    try {
                        newTaskId = new NativeByteBuffer(8 + req2.getObjectSize());
                        newTaskId.writeInt32(7);
                        newTaskId.writeInt32(i);
                        req2.serializeToStream(newTaskId);
                    } catch (Throwable e2) {
                        data = data2;
                        FileLog.m3e(e2);
                        newTaskId = data;
                    }
                    newTaskId = MessagesStorage.getInstance(messagesController.currentAccount).createPendingTask(newTaskId);
                }
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                            MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                        }
                        if (newTaskId != 0) {
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                        }
                    }
                });
            }
        }
    }

    public void pinChannelMessage(Chat chat, int id, boolean notify) {
        TL_channels_updatePinnedMessage req = new TL_channels_updatePinnedMessage();
        req.channel = getInputChannel(chat);
        req.id = id;
        req.silent = notify ^ 1;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        });
    }

    public void deleteUserChannelHistory(final Chat chat, final User user, int offset) {
        if (offset == 0) {
            MessagesStorage.getInstance(this.currentAccount).deleteUserChannelHistory(chat.id, user.id);
        }
        TL_channels_deleteUserHistory req = new TL_channels_deleteUserHistory();
        req.channel = getInputChannel(chat);
        req.user_id = getInputUser(user);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
                    if (res.offset > 0) {
                        MessagesController.this.deleteUserChannelHistory(chat, user, res.offset);
                    }
                    MessagesController.this.processNewChannelDifferenceParams(res.pts, res.pts_count, chat.id);
                }
            }
        });
    }

    public void deleteDialog(long did, int onlyHistory) {
        deleteDialog(did, true, onlyHistory, 0);
    }

    private void deleteDialog(long did, boolean first, int onlyHistory, int max_id) {
        MessagesController messagesController = this;
        final long j = did;
        int i = onlyHistory;
        int lower_part = (int) j;
        int high_id = (int) (j >> 32);
        int max_id_delete = max_id;
        if (i == 2) {
            MessagesStorage.getInstance(messagesController.currentAccount).deleteDialog(j, i);
            return;
        }
        int lastMessageId;
        boolean z;
        if (i == 0 || i == 3) {
            DataQuery.getInstance(messagesController.currentAccount).uninstallShortcut(j);
        }
        if (first) {
            MessagesStorage.getInstance(messagesController.currentAccount).deleteDialog(j, i);
            TL_dialog dialog = (TL_dialog) messagesController.dialogs_dict.get(j);
            TL_dialog tL_dialog;
            if (dialog != null) {
                MessageObject object;
                if (max_id_delete == 0) {
                    max_id_delete = Math.max(0, dialog.top_message);
                }
                if (i != 0) {
                    if (i != 3) {
                        dialog.unread_count = 0;
                        object = (MessageObject) messagesController.dialogMessage.get(dialog.id);
                        messagesController.dialogMessage.remove(dialog.id);
                        if (object == null) {
                            lastMessageId = object.getId();
                            messagesController.dialogMessagesByIds.remove(object.getId());
                        } else {
                            lastMessageId = dialog.top_message;
                            object = (MessageObject) messagesController.dialogMessagesByIds.get(dialog.top_message);
                            messagesController.dialogMessagesByIds.remove(dialog.top_message);
                        }
                        if (!(object == null || object.messageOwner.random_id == 0)) {
                            messagesController.dialogMessagesByRandomIds.remove(object.messageOwner.random_id);
                        }
                        if (i == 1 || lower_part == 0 || lastMessageId <= 0) {
                            z = false;
                            dialog.top_message = 0;
                        } else {
                            TL_messageService message = new TL_messageService();
                            message.id = dialog.top_message;
                            message.out = ((long) UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) == j;
                            message.from_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                            message.flags |= 256;
                            message.action = new TL_messageActionHistoryClear();
                            message.date = dialog.last_message_date;
                            if (lower_part > 0) {
                                message.to_id = new TL_peerUser();
                                message.to_id.user_id = lower_part;
                            } else if (ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                                message.to_id = new TL_peerChannel();
                                message.to_id.channel_id = -lower_part;
                            } else {
                                message.to_id = new TL_peerChat();
                                message.to_id.chat_id = -lower_part;
                            }
                            TL_dialog dialog2 = dialog;
                            MessageObject obj = new MessageObject(messagesController.currentAccount, message, messagesController.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                            ArrayList<MessageObject> objArr = new ArrayList();
                            objArr.add(obj);
                            dialog = new ArrayList();
                            dialog.add(message);
                            updateInterfaceWithMessages(j, objArr);
                            MessagesStorage.getInstance(messagesController.currentAccount).putMessages((ArrayList) dialog, false, true, false, 0);
                            tL_dialog = dialog2;
                            z = false;
                        }
                    }
                }
                messagesController.dialogs.remove(dialog);
                if (messagesController.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            MessagesController.this.channelsPts.delete(-((int) j));
                            MessagesController.this.shortPollChannels.delete(-((int) j));
                            MessagesController.this.needShortPollChannels.delete(-((int) j));
                        }
                    });
                }
                messagesController.dialogsGroupsOnly.remove(dialog);
                messagesController.dialogs_dict.remove(j);
                messagesController.dialogs_read_inbox_max.remove(Long.valueOf(did));
                messagesController.dialogs_read_outbox_max.remove(Long.valueOf(did));
                messagesController.nextDialogsCacheOffset--;
                object = (MessageObject) messagesController.dialogMessage.get(dialog.id);
                messagesController.dialogMessage.remove(dialog.id);
                if (object == null) {
                    lastMessageId = dialog.top_message;
                    object = (MessageObject) messagesController.dialogMessagesByIds.get(dialog.top_message);
                    messagesController.dialogMessagesByIds.remove(dialog.top_message);
                } else {
                    lastMessageId = object.getId();
                    messagesController.dialogMessagesByIds.remove(object.getId());
                }
                messagesController.dialogMessagesByRandomIds.remove(object.messageOwner.random_id);
                if (i == 1) {
                }
                z = false;
                dialog.top_message = 0;
            } else {
                z = false;
                tL_dialog = dialog;
            }
            NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[z]);
            NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), Boolean.valueOf(z));
            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$44$1 */
                class C03341 implements Runnable {
                    C03341() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(j);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03341());
                }
            });
        } else {
            z = false;
        }
        int max_id_delete2 = max_id_delete;
        if (high_id == 1) {
        } else if (i == 3) {
            r24 = lower_part;
        } else {
            if (lower_part != 0) {
                InputPeer peer = getInputPeer(lower_part);
                if (peer != null) {
                    boolean z2 = peer instanceof TL_inputPeerChannel;
                    lastMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    if (!z2) {
                        TL_messages_deleteHistory req = new TL_messages_deleteHistory();
                        req.peer = peer;
                        if (i != 0) {
                            lastMessageId = max_id_delete2;
                        }
                        req.max_id = lastMessageId;
                        if (i != 0) {
                            z = true;
                        }
                        req.just_clear = z;
                        final int max_id_delete_final = max_id_delete2;
                        ConnectionsManager instance = ConnectionsManager.getInstance(messagesController.currentAccount);
                        AnonymousClass46 anonymousClass46 = r0;
                        final long j2 = j;
                        TL_messages_deleteHistory req2 = req;
                        req = i;
                        AnonymousClass46 anonymousClass462 = new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                if (error == null) {
                                    TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
                                    if (res.offset > 0) {
                                        MessagesController.this.deleteDialog(j2, false, req, max_id_delete_final);
                                    }
                                    MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                                }
                            }
                        };
                        instance.sendRequest(req2, anonymousClass46, 64);
                    } else if (i != 0) {
                        TL_channels_deleteHistory req3 = new TL_channels_deleteHistory();
                        req3.channel = new TL_inputChannel();
                        req3.channel.channel_id = peer.channel_id;
                        req3.channel.access_hash = peer.access_hash;
                        if (max_id_delete2 > 0) {
                            lastMessageId = max_id_delete2;
                        }
                        req3.max_id = lastMessageId;
                        ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req3, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                            }
                        }, 64);
                        r24 = lower_part;
                    } else {
                        return;
                    }
                }
                return;
            }
            if (i == 1) {
                SecretChatHelper.getInstance(messagesController.currentAccount).sendClearHistoryMessage(getEncryptedChat(Integer.valueOf(high_id)), null);
            } else {
                SecretChatHelper.getInstance(messagesController.currentAccount).declineSecretChat(high_id);
            }
        }
    }

    public void saveGif(Document document) {
        TL_messages_saveGif req = new TL_messages_saveGif();
        req.id = new TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.unsave = false;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void saveRecentSticker(Document document, boolean asMask) {
        TL_messages_saveRecentSticker req = new TL_messages_saveRecentSticker();
        req.id = new TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.unsave = false;
        req.attached = asMask;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void loadChannelParticipants(final Integer chat_id) {
        if (!this.loadingFullParticipants.contains(chat_id)) {
            if (!this.loadedFullParticipants.contains(chat_id)) {
                this.loadingFullParticipants.add(chat_id);
                TL_channels_getParticipants req = new TL_channels_getParticipants();
                req.channel = getInputChannel(chat_id.intValue());
                req.filter = new TL_channelParticipantsRecent();
                req.offset = 0;
                req.limit = 32;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (error == null) {
                                    TL_channels_channelParticipants res = response;
                                    MessagesController.this.putUsers(res.users, false);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChannelUsers(chat_id.intValue(), res.participants);
                                    MessagesController.this.loadedFullParticipants.add(chat_id);
                                }
                                MessagesController.this.loadingFullParticipants.remove(chat_id);
                            }
                        });
                    }
                });
            }
        }
    }

    public void loadChatInfo(int chat_id, CountDownLatch countDownLatch, boolean force) {
        MessagesStorage.getInstance(this.currentAccount).loadChatInfo(chat_id, countDownLatch, force, false);
    }

    public void processChatInfo(int chat_id, ChatFull info, ArrayList<User> usersArr, boolean fromCache, boolean force, boolean byChannelUsers, MessageObject pinnedMessageObject) {
        if (fromCache && chat_id > 0 && !byChannelUsers) {
            loadFullChat(chat_id, 0, force);
        }
        if (info != null) {
            final ArrayList<User> arrayList = usersArr;
            final boolean z = fromCache;
            final ChatFull chatFull = info;
            final boolean z2 = byChannelUsers;
            final MessageObject messageObject = pinnedMessageObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.this.putUsers(arrayList, z);
                    if (chatFull.stickerset != null) {
                        DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(z2), messageObject);
                }
            });
        }
    }

    public void updateTimerProc() {
        int a;
        long currentTime = System.currentTimeMillis();
        boolean z = false;
        checkDeletingTask(false);
        checkReadTasks();
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            TL_account_updateStatus req;
            if (ConnectionsManager.getInstance(r0.currentAccount).getPauseTime() == 0 && ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePausedStageQueue) {
                if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && r0.statusSettingState != 1 && (r0.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - r0.lastStatusUpdateTime) >= 55000 || r0.offlineSent)) {
                    r0.statusSettingState = 1;
                    if (r0.statusRequest != 0) {
                        ConnectionsManager.getInstance(r0.currentAccount).cancelRequest(r0.statusRequest, true);
                    }
                    req = new TL_account_updateStatus();
                    req.offline = false;
                    r0.statusRequest = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (error == null) {
                                MessagesController.this.lastStatusUpdateTime = System.currentTimeMillis();
                                MessagesController.this.offlineSent = false;
                                MessagesController.this.statusSettingState = 0;
                            } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                                MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                            }
                            MessagesController.this.statusRequest = 0;
                        }
                    });
                }
            } else if (!(r0.statusSettingState == 2 || r0.offlineSent || Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance(r0.currentAccount).getPauseTime()) < AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                r0.statusSettingState = 2;
                if (r0.statusRequest != 0) {
                    ConnectionsManager.getInstance(r0.currentAccount).cancelRequest(r0.statusRequest, true);
                }
                req = new TL_account_updateStatus();
                req.offline = true;
                r0.statusRequest = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesController.this.offlineSent = true;
                        } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                            MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                        }
                        MessagesController.this.statusRequest = 0;
                    }
                });
            }
            if (r0.updatesQueueChannels.size() != 0) {
                for (a = 0; a < r0.updatesQueueChannels.size(); a++) {
                    int key = r0.updatesQueueChannels.keyAt(a);
                    if (r0.updatesStartWaitTimeChannels.valueAt(a) + 1500 < currentTime) {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("QUEUE CHANNEL ");
                            stringBuilder.append(key);
                            stringBuilder.append(" UPDATES WAIT TIMEOUT - CHECK QUEUE");
                            FileLog.m0d(stringBuilder.toString());
                        }
                        processChannelsUpdatesQueue(key, 0);
                    }
                }
            }
            a = 0;
            while (a < 3) {
                if (getUpdatesStartTime(a) != 0 && getUpdatesStartTime(a) + 1500 < currentTime) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(a);
                        stringBuilder2.append(" QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        FileLog.m0d(stringBuilder2.toString());
                    }
                    processUpdatesQueue(a, 0);
                }
                a++;
            }
        }
        if (r0.channelViewsToSend.size() != 0 && Math.abs(System.currentTimeMillis() - r0.lastViewsCheckTime) >= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            r0.lastViewsCheckTime = System.currentTimeMillis();
            a = 0;
            while (a < r0.channelViewsToSend.size()) {
                final int key2 = r0.channelViewsToSend.keyAt(a);
                final TL_messages_getMessagesViews req2 = new TL_messages_getMessagesViews();
                req2.peer = getInputPeer(key2);
                req2.id = (ArrayList) r0.channelViewsToSend.valueAt(a);
                req2.increment = a == 0;
                ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            Vector vector = (Vector) response;
                            final SparseArray<SparseIntArray> channelViews = new SparseArray();
                            SparseIntArray array = (SparseIntArray) channelViews.get(key2);
                            if (array == null) {
                                array = new SparseIntArray();
                                channelViews.put(key2, array);
                            }
                            for (int a = 0; a < req2.id.size(); a++) {
                                if (a >= vector.objects.size()) {
                                    break;
                                }
                                array.put(((Integer) req2.id.get(a)).intValue(), ((Integer) vector.objects.get(a)).intValue());
                            }
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putChannelViews(channelViews, req2.peer instanceof TL_inputPeerChannel);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, channelViews);
                                }
                            });
                        }
                    }
                });
                a++;
            }
            r0.channelViewsToSend.clear();
        }
        if (!r0.onlinePrivacy.isEmpty()) {
            Iterator it;
            ArrayList<Integer> toRemove = null;
            key2 = ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime();
            for (Entry<Integer, Integer> entry : r0.onlinePrivacy.entrySet()) {
                if (((Integer) entry.getValue()).intValue() < key2 - 30) {
                    if (toRemove == null) {
                        toRemove = new ArrayList();
                    }
                    toRemove.add(entry.getKey());
                }
            }
            if (toRemove != null) {
                it = toRemove.iterator();
                while (it.hasNext()) {
                    r0.onlinePrivacy.remove((Integer) it.next());
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                    }
                });
            }
        }
        if (r0.shortPollChannels.size() != 0) {
            for (a = 0; a < r0.shortPollChannels.size(); a++) {
                key2 = r0.shortPollChannels.keyAt(a);
                if (((long) r0.shortPollChannels.valueAt(a)) < System.currentTimeMillis() / 1000) {
                    r0.shortPollChannels.delete(key2);
                    if (r0.needShortPollChannels.indexOfKey(key2) >= 0) {
                        getChannelDifference(key2);
                    }
                }
            }
        }
        if (!(r0.printingUsers.isEmpty() && r0.lastPrintingStringCount == r0.printingUsers.size())) {
            ArrayList<Long> keys = new ArrayList(r0.printingUsers.keySet());
            boolean updated = false;
            a = 0;
            while (a < keys.size()) {
                int b;
                long key3 = ((Long) keys.get(a)).longValue();
                ArrayList<PrintingUser> arr = (ArrayList) r0.printingUsers.get(Long.valueOf(key3));
                if (arr != null) {
                    boolean updated2 = updated;
                    int a2 = z;
                    while (a2 < arr.size()) {
                        int timeToRemove;
                        PrintingUser user = (PrintingUser) arr.get(a2);
                        if (user.action instanceof TL_sendMessageGamePlayAction) {
                            timeToRemove = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
                        } else {
                            timeToRemove = 5900;
                        }
                        b = a;
                        if (user.lastTime + ((long) timeToRemove) < currentTime) {
                            arr.remove(user);
                            a2--;
                            updated2 = true;
                        }
                        a2++;
                        a = b;
                    }
                    b = a;
                    updated = updated2;
                } else {
                    b = a;
                }
                if (arr != null) {
                    if (!arr.isEmpty()) {
                        a = b;
                        a++;
                        z = false;
                    }
                }
                r0.printingUsers.remove(Long.valueOf(key3));
                int b2 = b;
                keys.remove(b2);
                a = b2 - 1;
                a++;
                z = false;
            }
            updatePrintingStrings();
            if (updated) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                    }
                });
            }
        }
        if (Theme.selectedAutoNightType == 1 && Math.abs(currentTime - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(r0.themeCheckRunnable);
            lastThemeCheckTime = currentTime;
        }
        if (r0.lastPushRegisterSendTime != 0 && Math.abs(SystemClock.uptimeMillis() - r0.lastPushRegisterSendTime) >= 10800000) {
            GcmInstanceIDListenerService.sendRegistrationToServer(SharedConfig.pushString);
        }
        LocationController.getInstance(r0.currentAccount).update();
    }

    private String getUserNameForTyping(User user) {
        if (user == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        if (user.first_name != null && user.first_name.length() > 0) {
            return user.first_name;
        }
        if (user.last_name == null || user.last_name.length() <= 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        return user.last_name;
    }

    private void updatePrintingStrings() {
        final LongSparseArray<CharSequence> newPrintingStrings = new LongSparseArray();
        final LongSparseArray<Integer> newPrintingStringsTypes = new LongSparseArray();
        ArrayList<Long> keys = new ArrayList(this.printingUsers.keySet());
        for (Entry<Long, ArrayList<PrintingUser>> entry : this.printingUsers.entrySet()) {
            ArrayList<Long> arrayList;
            int count;
            long key = ((Long) entry.getKey()).longValue();
            ArrayList<PrintingUser> arr = (ArrayList) entry.getValue();
            int lower_id = (int) key;
            if (lower_id > 0 || lower_id == 0) {
                arrayList = keys;
                keys = null;
            } else if (arr.size() == 1) {
                arrayList = keys;
                keys = null;
            } else {
                count = 0;
                StringBuilder label = new StringBuilder();
                Iterator it = arr.iterator();
                while (it.hasNext()) {
                    User user = getUser(Integer.valueOf(((PrintingUser) it.next()).userId));
                    if (user != null) {
                        if (label.length() != 0) {
                            label.append(", ");
                        }
                        label.append(getUserNameForTyping(user));
                        count++;
                    }
                    if (count == 2) {
                        break;
                    }
                }
                if (label.length() != 0) {
                    if (count == 1) {
                        newPrintingStrings.put(key, LocaleController.formatString("IsTypingGroup", R.string.IsTypingGroup, label.toString()));
                    } else if (arr.size() > 2) {
                        newPrintingStrings.put(key, String.format(LocaleController.getPluralString("AndMoreTypingGroup", arr.size() - 2), new Object[]{label.toString(), Integer.valueOf(arr.size() - 2)}));
                    } else {
                        Object[] objArr = new Object[1];
                        arrayList = keys;
                        keys = null;
                        objArr[0] = label.toString();
                        newPrintingStrings.put(key, LocaleController.formatString("AreTypingGroup", R.string.AreTypingGroup, objArr));
                        newPrintingStringsTypes.put(key, Integer.valueOf(keys));
                    }
                    arrayList = keys;
                    keys = null;
                    newPrintingStringsTypes.put(key, Integer.valueOf(keys));
                } else {
                    arrayList = keys;
                }
                keys = arrayList;
            }
            PrintingUser keys2 = (PrintingUser) arr.get(keys);
            User user2 = getUser(Integer.valueOf(keys2.userId));
            if (user2 == null) {
                keys = arrayList;
            } else {
                if (keys2.action instanceof TL_sendMessageRecordAudioAction) {
                    if (lower_id < 0) {
                        newPrintingStrings.put(key, LocaleController.formatString("IsRecordingAudio", R.string.IsRecordingAudio, getUserNameForTyping(user2)));
                    } else {
                        newPrintingStrings.put(key, LocaleController.getString("RecordingAudio", R.string.RecordingAudio));
                    }
                    newPrintingStringsTypes.put(key, Integer.valueOf(1));
                } else {
                    if (!(keys2.action instanceof TL_sendMessageRecordRoundAction)) {
                        if (!(keys2.action instanceof TL_sendMessageUploadRoundAction)) {
                            if (keys2.action instanceof TL_sendMessageUploadAudioAction) {
                                if (lower_id < 0) {
                                    newPrintingStrings.put(key, LocaleController.formatString("IsSendingAudio", R.string.IsSendingAudio, getUserNameForTyping(user2)));
                                } else {
                                    newPrintingStrings.put(key, LocaleController.getString("SendingAudio", R.string.SendingAudio));
                                }
                                newPrintingStringsTypes.put(key, Integer.valueOf(2));
                            } else {
                                if (!(keys2.action instanceof TL_sendMessageUploadVideoAction)) {
                                    if (!(keys2.action instanceof TL_sendMessageRecordVideoAction)) {
                                        if (keys2.action instanceof TL_sendMessageUploadDocumentAction) {
                                            if (lower_id < 0) {
                                                newPrintingStrings.put(key, LocaleController.formatString("IsSendingFile", R.string.IsSendingFile, getUserNameForTyping(user2)));
                                            } else {
                                                newPrintingStrings.put(key, LocaleController.getString("SendingFile", R.string.SendingFile));
                                            }
                                            newPrintingStringsTypes.put(key, Integer.valueOf(2));
                                        } else if (keys2.action instanceof TL_sendMessageUploadPhotoAction) {
                                            if (lower_id < 0) {
                                                newPrintingStrings.put(key, LocaleController.formatString("IsSendingPhoto", R.string.IsSendingPhoto, getUserNameForTyping(user2)));
                                            } else {
                                                newPrintingStrings.put(key, LocaleController.getString("SendingPhoto", R.string.SendingPhoto));
                                            }
                                            newPrintingStringsTypes.put(key, Integer.valueOf(2));
                                        } else if (keys2.action instanceof TL_sendMessageGamePlayAction) {
                                            if (lower_id < 0) {
                                                newPrintingStrings.put(key, LocaleController.formatString("IsSendingGame", R.string.IsSendingGame, getUserNameForTyping(user2)));
                                            } else {
                                                newPrintingStrings.put(key, LocaleController.getString("SendingGame", R.string.SendingGame));
                                            }
                                            newPrintingStringsTypes.put(key, Integer.valueOf(3));
                                        } else {
                                            if (lower_id < 0) {
                                                objArr = new Object[1];
                                                count = 0;
                                                objArr[0] = getUserNameForTyping(user2);
                                                newPrintingStrings.put(key, LocaleController.formatString("IsTypingGroup", R.string.IsTypingGroup, objArr));
                                            } else {
                                                count = 0;
                                                newPrintingStrings.put(key, LocaleController.getString("Typing", R.string.Typing));
                                            }
                                            newPrintingStringsTypes.put(key, Integer.valueOf(count));
                                        }
                                    }
                                }
                                if (lower_id < 0) {
                                    newPrintingStrings.put(key, LocaleController.formatString("IsSendingVideo", R.string.IsSendingVideo, getUserNameForTyping(user2)));
                                } else {
                                    newPrintingStrings.put(key, LocaleController.getString("SendingVideoStatus", R.string.SendingVideoStatus));
                                }
                                newPrintingStringsTypes.put(key, Integer.valueOf(2));
                            }
                        }
                    }
                    if (lower_id < 0) {
                        newPrintingStrings.put(key, LocaleController.formatString("IsRecordingRound", R.string.IsRecordingRound, getUserNameForTyping(user2)));
                    } else {
                        newPrintingStrings.put(key, LocaleController.getString("RecordingRound", R.string.RecordingRound));
                    }
                    newPrintingStringsTypes.put(key, Integer.valueOf(4));
                }
                keys = arrayList;
            }
        }
        r0.lastPrintingStringCount = newPrintingStrings.size();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.printingStrings = newPrintingStrings;
                MessagesController.this.printingStringsTypes = newPrintingStringsTypes;
            }
        });
    }

    public void cancelTyping(int action, long dialog_id) {
        LongSparseArray<Boolean> typings = (LongSparseArray) this.sendingTypings.get(action);
        if (typings != null) {
            typings.remove(dialog_id);
        }
    }

    public void sendTyping(final long dialog_id, final int action, int classGuid) {
        if (dialog_id != 0) {
            LongSparseArray<Boolean> typings = (LongSparseArray) this.sendingTypings.get(action);
            if (typings == null || typings.get(dialog_id) == null) {
                if (typings == null) {
                    typings = new LongSparseArray();
                    this.sendingTypings.put(action, typings);
                }
                int lower_part = (int) dialog_id;
                int high_id = (int) (dialog_id >> 32);
                int reqId;
                if (lower_part != 0) {
                    if (high_id != 1) {
                        TL_messages_setTyping req = new TL_messages_setTyping();
                        req.peer = getInputPeer(lower_part);
                        if (req.peer instanceof TL_inputPeerChannel) {
                            Chat chat = getChat(Integer.valueOf(req.peer.channel_id));
                            if (chat == null || !chat.megagroup) {
                                return;
                            }
                        }
                        if (req.peer != null) {
                            if (action == 0) {
                                req.action = new TL_sendMessageTypingAction();
                            } else if (action == 1) {
                                req.action = new TL_sendMessageRecordAudioAction();
                            } else if (action == 2) {
                                req.action = new TL_sendMessageCancelAction();
                            } else if (action == 3) {
                                req.action = new TL_sendMessageUploadDocumentAction();
                            } else if (action == 4) {
                                req.action = new TL_sendMessageUploadPhotoAction();
                            } else if (action == 5) {
                                req.action = new TL_sendMessageUploadVideoAction();
                            } else if (action == 6) {
                                req.action = new TL_sendMessageGamePlayAction();
                            } else if (action == 7) {
                                req.action = new TL_sendMessageRecordRoundAction();
                            } else if (action == 8) {
                                req.action = new TL_sendMessageUploadRoundAction();
                            } else if (action == 9) {
                                req.action = new TL_sendMessageUploadAudioAction();
                            }
                            typings.put(dialog_id, Boolean.valueOf(true));
                            reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                                /* renamed from: org.telegram.messenger.MessagesController$57$1 */
                                class C03381 implements Runnable {
                                    C03381() {
                                    }

                                    public void run() {
                                        LongSparseArray<Boolean> typings = (LongSparseArray) MessagesController.this.sendingTypings.get(action);
                                        if (typings != null) {
                                            typings.remove(dialog_id);
                                        }
                                    }
                                }

                                public void run(TLObject response, TL_error error) {
                                    AndroidUtilities.runOnUIThread(new C03381());
                                }
                            }, 2);
                            if (classGuid != 0) {
                                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, classGuid);
                            }
                        }
                    }
                } else if (action == 0) {
                    EncryptedChat chat2 = getEncryptedChat(Integer.valueOf(high_id));
                    if (chat2.auth_key != null && chat2.auth_key.length > 1 && (chat2 instanceof TL_encryptedChat)) {
                        TL_messages_setEncryptedTyping req2 = new TL_messages_setEncryptedTyping();
                        req2.peer = new TL_inputEncryptedChat();
                        req2.peer.chat_id = chat2.id;
                        req2.peer.access_hash = chat2.access_hash;
                        req2.typing = true;
                        typings.put(dialog_id, Boolean.valueOf(true));
                        reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {

                            /* renamed from: org.telegram.messenger.MessagesController$58$1 */
                            class C03391 implements Runnable {
                                C03391() {
                                }

                                public void run() {
                                    LongSparseArray<Boolean> typings = (LongSparseArray) MessagesController.this.sendingTypings.get(action);
                                    if (typings != null) {
                                        typings.remove(dialog_id);
                                    }
                                }
                            }

                            public void run(TLObject response, TL_error error) {
                                AndroidUtilities.runOnUIThread(new C03391());
                            }
                        }, 2);
                        if (classGuid != 0) {
                            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, classGuid);
                        }
                    }
                }
            }
        }
    }

    public void loadMessages(long dialog_id, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex) {
        loadMessages(dialog_id, count, max_id, offset_date, fromCache, midDate, classGuid, load_type, last_message_id, isChannel, loadIndex, 0, 0, 0, false, 0);
    }

    public void loadMessages(long dialog_id, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex, int first_unread, int unread_count, int last_date, boolean queryFromServer, int mentionsCount) {
        loadMessagesInternal(dialog_id, count, max_id, offset_date, fromCache, midDate, classGuid, load_type, last_message_id, isChannel, loadIndex, first_unread, unread_count, last_date, queryFromServer, mentionsCount, true);
    }

    private void loadMessagesInternal(long dialog_id, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex, int first_unread, int unread_count, int last_date, boolean queryFromServer, int mentionsCount, boolean loadDialog) {
        MessagesController messagesController;
        MessagesController messagesController2 = this;
        long j = dialog_id;
        int i = count;
        int i2 = max_id;
        boolean z = fromCache;
        int i3 = classGuid;
        int i4 = load_type;
        int i5 = last_message_id;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("load messages in chat ");
            stringBuilder.append(j);
            stringBuilder.append(" count ");
            stringBuilder.append(i);
            stringBuilder.append(" max_id ");
            stringBuilder.append(i2);
            stringBuilder.append(" cache ");
            stringBuilder.append(z);
            stringBuilder.append(" mindate = ");
            stringBuilder.append(midDate);
            stringBuilder.append(" guid ");
            stringBuilder.append(i3);
            stringBuilder.append(" load_type ");
            stringBuilder.append(i4);
            stringBuilder.append(" last_message_id ");
            stringBuilder.append(i5);
            stringBuilder.append(" index ");
            stringBuilder.append(loadIndex);
            stringBuilder.append(" firstUnread ");
            stringBuilder.append(first_unread);
            stringBuilder.append(" unread_count ");
            stringBuilder.append(unread_count);
            stringBuilder.append(" last_date ");
            stringBuilder.append(last_date);
            stringBuilder.append(" queryFromServer ");
            stringBuilder.append(queryFromServer);
            FileLog.m0d(stringBuilder.toString());
        } else {
            int i6 = midDate;
            int i7 = loadIndex;
            int i8 = first_unread;
            int i9 = unread_count;
            int i10 = last_date;
            boolean z2 = queryFromServer;
        }
        int lower_part = (int) j;
        if (z) {
            i = i3;
            messagesController = messagesController2;
        } else if (lower_part == 0) {
            r17 = lower_part;
            i = i3;
            messagesController = messagesController2;
        } else {
            int lower_part2;
            TL_messages_getHistory req;
            int lower_part3;
            int i11;
            final long j2;
            AnonymousClass60 anonymousClass60;
            ConnectionsManager instance;
            final boolean z3;
            final int i12;
            TL_messages_getHistory req2;
            final boolean z4;
            final int i13;
            r17 = lower_part;
            if (loadDialog) {
                if (i4 != 3) {
                    if (i4 != 2) {
                        lower_part2 = r17;
                        req = new TL_messages_getHistory();
                        lower_part3 = lower_part2;
                        req.peer = getInputPeer(lower_part3);
                        i = load_type;
                        if (i != 4) {
                            i2 = count;
                            req.add_offset = (-i2) + 5;
                        } else {
                            i2 = count;
                            if (i == 3) {
                                req.add_offset = (-i2) / 2;
                            } else if (i != 1) {
                                req.add_offset = (-i2) - 1;
                            } else {
                                if (i != 2) {
                                    i11 = max_id;
                                    if (i11 != 0) {
                                        req.add_offset = (-i2) + 6;
                                        req.limit = i2;
                                        req.offset_id = i11;
                                        i3 = offset_date;
                                        req.offset_date = i3;
                                        i9 = i2;
                                        i10 = i11;
                                        i8 = i3;
                                        j2 = dialog_id;
                                        anonymousClass60 = lower_part;
                                        i5 = classGuid;
                                        instance = ConnectionsManager.getInstance(r14.currentAccount);
                                        i4 = first_unread;
                                        i3 = last_message_id;
                                        i11 = unread_count;
                                        i2 = last_date;
                                        i = load_type;
                                        r17 = lower_part3;
                                        z3 = isChannel;
                                        i12 = loadIndex;
                                        req2 = req;
                                        z4 = queryFromServer;
                                        i13 = mentionsCount;
                                        lower_part = new RequestDelegate() {
                                            public void run(TLObject response, TL_error error) {
                                                AnonymousClass60 anonymousClass60 = this;
                                                if (response != null) {
                                                    messages_Messages res = (messages_Messages) response;
                                                    if (res.messages.size() > i9) {
                                                        res.messages.remove(0);
                                                    }
                                                    int mid = i10;
                                                    if (i8 != 0 && !res.messages.isEmpty()) {
                                                        mid = ((Message) res.messages.get(res.messages.size() - 1)).id;
                                                        for (int a = res.messages.size() - 1; a >= 0; a--) {
                                                            Message message = (Message) res.messages.get(a);
                                                            if (message.date > i8) {
                                                                mid = message.id;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    int mid2 = mid;
                                                    MessagesController messagesController = MessagesController.this;
                                                    long j = j2;
                                                    int i = i9;
                                                    int i2 = i8;
                                                    int i3 = i5;
                                                    int i4 = i4;
                                                    int i5 = i3;
                                                    int i6 = i11;
                                                    int i7 = i2;
                                                    int i8 = i;
                                                    boolean z = z3;
                                                    int i9 = i12;
                                                    boolean z2 = z;
                                                    int i10 = i8;
                                                    int i11 = i9;
                                                    messagesController.processLoadedMessages(res, j, i, mid2, i2, false, i3, i4, i5, i6, i7, i10, z2, false, i11, z4, i13);
                                                }
                                            }
                                        };
                                        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance.sendRequest(req2, anonymousClass60), classGuid);
                                    }
                                }
                                i11 = max_id;
                                if (lower_part3 < 0 && r10 != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-lower_part3)))) {
                                    req.add_offset = -1;
                                    req.limit++;
                                }
                                req.limit = i2;
                                req.offset_id = i11;
                                i3 = offset_date;
                                req.offset_date = i3;
                                i9 = i2;
                                i10 = i11;
                                i8 = i3;
                                j2 = dialog_id;
                                anonymousClass60 = lower_part;
                                i5 = classGuid;
                                instance = ConnectionsManager.getInstance(r14.currentAccount);
                                i4 = first_unread;
                                i3 = last_message_id;
                                i11 = unread_count;
                                i2 = last_date;
                                i = load_type;
                                r17 = lower_part3;
                                z3 = isChannel;
                                i12 = loadIndex;
                                req2 = req;
                                z4 = queryFromServer;
                                i13 = mentionsCount;
                                lower_part = /* anonymous class already generated */;
                                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance.sendRequest(req2, anonymousClass60), classGuid);
                            }
                        }
                        i11 = max_id;
                        req.limit = i2;
                        req.offset_id = i11;
                        i3 = offset_date;
                        req.offset_date = i3;
                        i9 = i2;
                        i10 = i11;
                        i8 = i3;
                        j2 = dialog_id;
                        anonymousClass60 = lower_part;
                        i5 = classGuid;
                        instance = ConnectionsManager.getInstance(r14.currentAccount);
                        i4 = first_unread;
                        i3 = last_message_id;
                        i11 = unread_count;
                        i2 = last_date;
                        i = load_type;
                        r17 = lower_part3;
                        z3 = isChannel;
                        i12 = loadIndex;
                        req2 = req;
                        z4 = queryFromServer;
                        i13 = mentionsCount;
                        lower_part = /* anonymous class already generated */;
                        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance.sendRequest(req2, anonymousClass60), classGuid);
                    }
                }
                if (i5 == 0) {
                    lower_part = new TL_messages_getPeerDialogs();
                    InputPeer inputPeer = getInputPeer((int) j);
                    TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
                    inputDialogPeer.peer = inputPeer;
                    InputPeer inputPeer2 = inputPeer;
                    lower_part.peers.add(inputDialogPeer);
                    TL_messages_getPeerDialogs req3 = lower_part;
                    ConnectionsManager instance2 = ConnectionsManager.getInstance(messagesController2.currentAccount);
                    r17 = inputDialogPeer;
                    final long j3 = j;
                    i8 = i;
                    i7 = i2;
                    i6 = offset_date;
                    i5 = midDate;
                    i4 = i3;
                    i3 = load_type;
                    AnonymousClass59 anonymousClass59 = lower_part;
                    z = isChannel;
                    i2 = loadIndex;
                    i = first_unread;
                    lower_part3 = last_date;
                    final boolean z5 = queryFromServer;
                    lower_part = new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            AnonymousClass59 anonymousClass59 = this;
                            if (response != null) {
                                TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                                if (!res.dialogs.isEmpty()) {
                                    TL_dialog dialog = (TL_dialog) res.dialogs.get(0);
                                    if (dialog.top_message != 0) {
                                        TL_messages_dialogs dialogs = new TL_messages_dialogs();
                                        dialogs.chats = res.chats;
                                        dialogs.users = res.users;
                                        dialogs.dialogs = res.dialogs;
                                        dialogs.messages = res.messages;
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(dialogs, false);
                                    }
                                    MessagesController messagesController = MessagesController.this;
                                    long j = j3;
                                    int i = i8;
                                    int i2 = i7;
                                    int i3 = i6;
                                    int i4 = i5;
                                    int i5 = i4;
                                    int i6 = i3;
                                    int i7 = dialog.top_message;
                                    boolean z = z;
                                    int i8 = i2;
                                    int i9 = i;
                                    int i10 = dialog.unread_count;
                                    int i11 = lower_part3;
                                    int i12 = i6;
                                    int i13 = i7;
                                    boolean z2 = z;
                                    messagesController.loadMessagesInternal(j, i, i2, i3, false, i4, i5, i12, i13, z2, i8, i9, i10, i11, z5, dialog.unread_mentions_count, false);
                                }
                            }
                        }
                    };
                    instance2.sendRequest(req3, anonymousClass59);
                    return;
                }
            }
            lower_part2 = r17;
            req = new TL_messages_getHistory();
            lower_part3 = lower_part2;
            req.peer = getInputPeer(lower_part3);
            i = load_type;
            if (i != 4) {
                i2 = count;
                if (i == 3) {
                    req.add_offset = (-i2) / 2;
                } else if (i != 1) {
                    if (i != 2) {
                        i11 = max_id;
                    } else {
                        i11 = max_id;
                        if (i11 != 0) {
                            req.add_offset = (-i2) + 6;
                            req.limit = i2;
                            req.offset_id = i11;
                            i3 = offset_date;
                            req.offset_date = i3;
                            i9 = i2;
                            i10 = i11;
                            i8 = i3;
                            j2 = dialog_id;
                            anonymousClass60 = lower_part;
                            i5 = classGuid;
                            instance = ConnectionsManager.getInstance(r14.currentAccount);
                            i4 = first_unread;
                            i3 = last_message_id;
                            i11 = unread_count;
                            i2 = last_date;
                            i = load_type;
                            r17 = lower_part3;
                            z3 = isChannel;
                            i12 = loadIndex;
                            req2 = req;
                            z4 = queryFromServer;
                            i13 = mentionsCount;
                            lower_part = /* anonymous class already generated */;
                            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance.sendRequest(req2, anonymousClass60), classGuid);
                        }
                    }
                    req.add_offset = -1;
                    req.limit++;
                    req.limit = i2;
                    req.offset_id = i11;
                    i3 = offset_date;
                    req.offset_date = i3;
                    i9 = i2;
                    i10 = i11;
                    i8 = i3;
                    j2 = dialog_id;
                    anonymousClass60 = lower_part;
                    i5 = classGuid;
                    instance = ConnectionsManager.getInstance(r14.currentAccount);
                    i4 = first_unread;
                    i3 = last_message_id;
                    i11 = unread_count;
                    i2 = last_date;
                    i = load_type;
                    r17 = lower_part3;
                    z3 = isChannel;
                    i12 = loadIndex;
                    req2 = req;
                    z4 = queryFromServer;
                    i13 = mentionsCount;
                    lower_part = /* anonymous class already generated */;
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance.sendRequest(req2, anonymousClass60), classGuid);
                } else {
                    req.add_offset = (-i2) - 1;
                }
            } else {
                i2 = count;
                req.add_offset = (-i2) + 5;
            }
            i11 = max_id;
            req.limit = i2;
            req.offset_id = i11;
            i3 = offset_date;
            req.offset_date = i3;
            i9 = i2;
            i10 = i11;
            i8 = i3;
            j2 = dialog_id;
            anonymousClass60 = lower_part;
            i5 = classGuid;
            instance = ConnectionsManager.getInstance(r14.currentAccount);
            i4 = first_unread;
            i3 = last_message_id;
            i11 = unread_count;
            i2 = last_date;
            i = load_type;
            r17 = lower_part3;
            z3 = isChannel;
            i12 = loadIndex;
            req2 = req;
            z4 = queryFromServer;
            i13 = mentionsCount;
            lower_part = /* anonymous class already generated */;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(instance.sendRequest(req2, anonymousClass60), classGuid);
        }
        MessagesStorage.getInstance(messagesController.currentAccount).getMessages(dialog_id, count, max_id, offset_date, midDate, i, load_type, isChannel, loadIndex);
    }

    public void reloadWebPages(final long dialog_id, HashMap<String, ArrayList<MessageObject>> webpagesToReload) {
        for (Entry<String, ArrayList<MessageObject>> entry : webpagesToReload.entrySet()) {
            final String url = (String) entry.getKey();
            ArrayList<MessageObject> messages = (ArrayList) entry.getValue();
            ArrayList<MessageObject> arrayList = (ArrayList) this.reloadingWebpages.get(url);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.reloadingWebpages.put(url, arrayList);
            }
            arrayList.addAll(messages);
            TL_messages_getWebPagePreview req = new TL_messages_getWebPagePreview();
            req.message = url;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ArrayList<MessageObject> arrayList = (ArrayList) MessagesController.this.reloadingWebpages.remove(url);
                            if (arrayList != null) {
                                messages_Messages messagesRes = new TL_messages_messages();
                                if (response instanceof TL_messageMediaWebPage) {
                                    TL_messageMediaWebPage media = response;
                                    if (!(media.webpage instanceof TL_webPage)) {
                                        if (!(media.webpage instanceof TL_webPageEmpty)) {
                                            MessagesController.this.reloadingWebpagesPending.put(media.webpage.id, arrayList);
                                        }
                                    }
                                    for (int a = 0; a < arrayList.size(); a++) {
                                        ((MessageObject) arrayList.get(a)).messageOwner.media.webpage = media.webpage;
                                        if (a == 0) {
                                            ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(a)).messageOwner);
                                        }
                                        messagesRes.messages.add(((MessageObject) arrayList.get(a)).messageOwner);
                                    }
                                } else {
                                    for (int a2 = 0; a2 < arrayList.size(); a2++) {
                                        ((MessageObject) arrayList.get(a2)).messageOwner.media.webpage = new TL_webPageEmpty();
                                        messagesRes.messages.add(((MessageObject) arrayList.get(a2)).messageOwner);
                                    }
                                }
                                if (!messagesRes.messages.isEmpty()) {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messagesRes, dialog_id, -2, 0, false);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    public void processLoadedMessages(messages_Messages messagesRes, long dialog_id, int count, int max_id, int offset_date, boolean isCache, int classGuid, int first_unread, int last_message_id, int unread_count, int last_date, int load_type, boolean isChannel, boolean isEnd, int loadIndex, boolean queryFromServer, int mentionsCount) {
        messages_Messages messages_messages;
        long j;
        int i;
        boolean z;
        int i2;
        int i3;
        int i4;
        boolean z2;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processLoadedMessages size ");
            messages_messages = messagesRes;
            stringBuilder.append(messages_messages.messages.size());
            stringBuilder.append(" in chat ");
            j = dialog_id;
            stringBuilder.append(j);
            stringBuilder.append(" count ");
            i = count;
            stringBuilder.append(i);
            stringBuilder.append(" max_id ");
            stringBuilder.append(max_id);
            stringBuilder.append(" cache ");
            z = isCache;
            stringBuilder.append(z);
            stringBuilder.append(" guid ");
            stringBuilder.append(classGuid);
            stringBuilder.append(" load_type ");
            stringBuilder.append(load_type);
            stringBuilder.append(" last_message_id ");
            stringBuilder.append(last_message_id);
            stringBuilder.append(" isChannel ");
            stringBuilder.append(isChannel);
            stringBuilder.append(" index ");
            stringBuilder.append(loadIndex);
            stringBuilder.append(" firstUnread ");
            stringBuilder.append(first_unread);
            stringBuilder.append(" unread_count ");
            stringBuilder.append(unread_count);
            stringBuilder.append(" last_date ");
            stringBuilder.append(last_date);
            stringBuilder.append(" queryFromServer ");
            stringBuilder.append(queryFromServer);
            FileLog.m0d(stringBuilder.toString());
        } else {
            messages_messages = messagesRes;
            j = dialog_id;
            i = count;
            i2 = max_id;
            z = isCache;
            int i5 = classGuid;
            int i6 = first_unread;
            i3 = last_message_id;
            int i7 = unread_count;
            int i8 = last_date;
            i4 = load_type;
            z2 = isChannel;
            int i9 = loadIndex;
            boolean z3 = queryFromServer;
        }
        final messages_Messages messages_messages2 = messages_messages;
        final long j2 = j;
        z2 = z;
        i3 = i;
        i4 = load_type;
        final boolean z4 = queryFromServer;
        final int i10 = first_unread;
        i2 = max_id;
        i = offset_date;
        final int i11 = classGuid;
        final int i12 = last_message_id;
        final boolean z5 = isChannel;
        final int i13 = loadIndex;
        final int i14 = unread_count;
        final int i15 = last_date;
        final int i16 = mentionsCount;
        final boolean z6 = isEnd;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$62$1 */
            class C03431 implements Runnable {
                C03431() {
                }

                public void run() {
                    MessagesController messagesController = MessagesController.this;
                    long j = j2;
                    int i = i3;
                    int i2 = (i4 == 2 && z4) ? i10 : i2;
                    int i3 = i2;
                    int i4 = i;
                    int i5 = i11;
                    int i6 = i4;
                    int i7 = i12;
                    boolean z = z5;
                    int i8 = i13;
                    int i9 = i10;
                    i2 = i14;
                    int i10 = i2;
                    messagesController.loadMessages(j, i, i3, i4, false, 0, i5, i6, i7, z, i8, i9, i10, i15, z4, i16);
                }
            }

            public void run() {
                int channelId;
                int channelPts;
                boolean createDialog = false;
                boolean isMegagroup = false;
                if (messages_messages2 instanceof TL_messages_channelMessages) {
                    channelId = -((int) j2);
                    channelPts = MessagesController.this.channelsPts.get(channelId);
                    if (channelPts != 0) {
                    } else if (MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(channelId) == 0) {
                        MessagesController.this.channelsPts.put(channelId, messages_messages2.pts);
                        createDialog = true;
                        if (MessagesController.this.needShortPollChannels.indexOfKey(channelId) < 0 || MessagesController.this.shortPollChannels.indexOfKey(channelId) >= 0) {
                            MessagesController.this.getChannelDifference(channelId);
                        } else {
                            MessagesController.this.getChannelDifference(channelId, 2, 0, null);
                        }
                    }
                    for (channelPts = 0; channelPts < messages_messages2.chats.size(); channelPts++) {
                        Chat chat = (Chat) messages_messages2.chats.get(channelPts);
                        if (chat.id == channelId) {
                            isMegagroup = chat.megagroup;
                            break;
                        }
                    }
                }
                channelId = (int) j2;
                channelPts = (int) (j2 >> 32);
                if (!z2) {
                    ImageLoader.saveMessagesThumbs(messages_messages2.messages);
                }
                if (channelPts == 1 || channelId == 0 || !z2 || messages_messages2.messages.size() != 0) {
                    int a;
                    SparseArray<User> usersDict = new SparseArray();
                    SparseArray<Chat> chatsDict = new SparseArray();
                    for (a = 0; a < messages_messages2.users.size(); a++) {
                        User u = (User) messages_messages2.users.get(a);
                        usersDict.put(u.id, u);
                    }
                    for (a = 0; a < messages_messages2.chats.size(); a++) {
                        Chat c = (Chat) messages_messages2.chats.get(a);
                        chatsDict.put(c.id, c);
                    }
                    int size = messages_messages2.messages.size();
                    if (!z2) {
                        Integer inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j2));
                        if (inboxValue == null) {
                            inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j2));
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j2), inboxValue);
                        }
                        Integer outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j2));
                        if (outboxValue == null) {
                            outboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j2));
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j2), outboxValue);
                        }
                        for (int a2 = 0; a2 < size; a2++) {
                            Message message = (Message) messages_messages2.messages.get(a2);
                            if (isMegagroup) {
                                message.flags |= Integer.MIN_VALUE;
                            }
                            if (message.action instanceof TL_messageActionChatDeleteUser) {
                                User user = (User) usersDict.get(message.action.user_id);
                                if (user != null && user.bot) {
                                    message.reply_markup = new TL_replyKeyboardHide();
                                    message.flags |= 64;
                                }
                            }
                            if (!(message.action instanceof TL_messageActionChatMigrateTo)) {
                                if (!(message.action instanceof TL_messageActionChannelCreate)) {
                                    message.unread = (message.out ? outboxValue : inboxValue).intValue() < message.id;
                                }
                            }
                            message.unread = false;
                            message.media_unread = false;
                        }
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messages_messages2, j2, i4, i2, createDialog);
                    }
                    final ArrayList<MessageObject> objects = new ArrayList();
                    final ArrayList<Integer> messagesToReload = new ArrayList();
                    final HashMap<String, ArrayList<MessageObject>> webpagesToReload = new HashMap();
                    a = 0;
                    while (true) {
                        int a3 = a;
                        int size2;
                        SparseArray<Chat> chatsDict2;
                        if (a3 < size) {
                            Message message2 = (Message) messages_messages2.messages.get(a3);
                            message2.dialog_id = j2;
                            size2 = size;
                            chatsDict2 = chatsDict;
                            boolean createDialog2 = createDialog;
                            createDialog = message2;
                            int a4 = a3;
                            MessageObject messageObject = new MessageObject(MessagesController.this.currentAccount, message2, (SparseArray) usersDict, (SparseArray) chatsDict2, true);
                            objects.add(messageObject);
                            if (z2) {
                                if (createDialog.media instanceof TL_messageMediaUnsupported) {
                                    if (createDialog.media.bytes != null) {
                                        if (createDialog.media.bytes.length != 0) {
                                            if (createDialog.media.bytes.length == 1) {
                                                if (createDialog.media.bytes[0] < (byte) 76) {
                                                }
                                            }
                                        }
                                        messagesToReload.add(Integer.valueOf(createDialog.id));
                                    }
                                } else if (createDialog.media instanceof TL_messageMediaWebPage) {
                                    if ((createDialog.media.webpage instanceof TL_webPagePending) && createDialog.media.webpage.date <= ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime()) {
                                        messagesToReload.add(Integer.valueOf(createDialog.id));
                                    } else if (createDialog.media.webpage instanceof TL_webPageUrlPending) {
                                        ArrayList<MessageObject> arrayList = (ArrayList) webpagesToReload.get(createDialog.media.webpage.url);
                                        if (arrayList == null) {
                                            arrayList = new ArrayList();
                                            webpagesToReload.put(createDialog.media.webpage.url, arrayList);
                                        }
                                        arrayList.add(messageObject);
                                    }
                                }
                                a = a4 + 1;
                                size = size2;
                                chatsDict = chatsDict2;
                                createDialog = createDialog2;
                            }
                            a = a4 + 1;
                            size = size2;
                            chatsDict = chatsDict2;
                            createDialog = createDialog2;
                        } else {
                            size2 = size;
                            chatsDict2 = chatsDict;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.putUsers(messages_messages2.users, z2);
                                    MessagesController.this.putChats(messages_messages2.chats, z2);
                                    int first_unread_final = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    if (z4 && i4 == 2) {
                                        int first_unread_final2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                        for (first_unread_final = 0; first_unread_final < messages_messages2.messages.size(); first_unread_final++) {
                                            Message message = (Message) messages_messages2.messages.get(first_unread_final);
                                            if (!message.out && message.id > i10 && message.id < first_unread_final2) {
                                                first_unread_final2 = message.id;
                                            }
                                        }
                                        first_unread_final = first_unread_final2;
                                    }
                                    if (first_unread_final == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        first_unread_final = i10;
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDidLoaded, Long.valueOf(j2), Integer.valueOf(i3), objects, Boolean.valueOf(z2), Integer.valueOf(first_unread_final), Integer.valueOf(i12), Integer.valueOf(i14), Integer.valueOf(i15), Integer.valueOf(i4), Boolean.valueOf(z6), Integer.valueOf(i11), Integer.valueOf(i13), Integer.valueOf(i2), Integer.valueOf(i16));
                                    if (!messagesToReload.isEmpty()) {
                                        MessagesController.this.reloadMessages(messagesToReload, j2);
                                    }
                                    if (!webpagesToReload.isEmpty()) {
                                        MessagesController.this.reloadWebPages(j2, webpagesToReload);
                                    }
                                }
                            });
                            return;
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C03431());
            }
        });
    }

    public void loadHintDialogs() {
        if (this.hintDialogs.isEmpty()) {
            if (!TextUtils.isEmpty(this.installReferer)) {
                TL_help_getRecentMeUrls req = new TL_help_getRecentMeUrls();
                req.referer = this.installReferer;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        if (error == null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TL_help_recentMeUrls res = response;
                                    MessagesController.this.putUsers(res.users, false);
                                    MessagesController.this.putChats(res.chats, false);
                                    MessagesController.this.hintDialogs.clear();
                                    MessagesController.this.hintDialogs.addAll(res.urls);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    public void loadDialogs(int offset, final int count, boolean fromCache) {
        if (!this.loadingDialogs) {
            if (!this.resetingDialogs) {
                this.loadingDialogs = true;
                int i = 0;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("load cacheOffset = ");
                    stringBuilder.append(offset);
                    stringBuilder.append(" count = ");
                    stringBuilder.append(count);
                    stringBuilder.append(" cache = ");
                    stringBuilder.append(fromCache);
                    FileLog.m0d(stringBuilder.toString());
                }
                if (fromCache) {
                    MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
                    if (offset != 0) {
                        i = this.nextDialogsCacheOffset;
                    }
                    instance.getDialogs(i, count);
                } else {
                    TL_messages_getDialogs req = new TL_messages_getDialogs();
                    req.limit = count;
                    req.exclude_pinned = true;
                    if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == -1) {
                        boolean found = false;
                        for (i = this.dialogs.size() - 1; i >= 0; i--) {
                            TL_dialog dialog = (TL_dialog) this.dialogs.get(i);
                            if (!dialog.pinned) {
                                int high_id = (int) (dialog.id >> 32);
                                if (!(((int) dialog.id) == 0 || high_id == 1 || dialog.top_message <= 0)) {
                                    MessageObject message = (MessageObject) this.dialogMessage.get(dialog.id);
                                    if (message != null && message.getId() > 0) {
                                        int id;
                                        req.offset_date = message.messageOwner.date;
                                        req.offset_id = message.messageOwner.id;
                                        if (message.messageOwner.to_id.channel_id != 0) {
                                            id = -message.messageOwner.to_id.channel_id;
                                        } else if (message.messageOwner.to_id.chat_id != 0) {
                                            id = -message.messageOwner.to_id.chat_id;
                                        } else {
                                            id = message.messageOwner.to_id.user_id;
                                            req.offset_peer = getInputPeer(id);
                                            found = true;
                                            if (!found) {
                                                req.offset_peer = new TL_inputPeerEmpty();
                                            }
                                        }
                                        req.offset_peer = getInputPeer(id);
                                        found = true;
                                        if (found) {
                                            req.offset_peer = new TL_inputPeerEmpty();
                                        }
                                    }
                                }
                            }
                        }
                        if (found) {
                            req.offset_peer = new TL_inputPeerEmpty();
                        }
                    } else if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        this.dialogsEndReached = true;
                        this.serverDialogsEndReached = true;
                        this.loadingDialogs = false;
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        return;
                    } else {
                        req.offset_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId;
                        req.offset_date = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate;
                        if (req.offset_id == 0) {
                            req.offset_peer = new TL_inputPeerEmpty();
                        } else {
                            if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId != 0) {
                                req.offset_peer = new TL_inputPeerChannel();
                                req.offset_peer.channel_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId;
                            } else if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId != 0) {
                                req.offset_peer = new TL_inputPeerUser();
                                req.offset_peer.user_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId;
                            } else {
                                req.offset_peer = new TL_inputPeerChat();
                                req.offset_peer.chat_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId;
                            }
                            req.offset_peer.access_hash = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess;
                        }
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (error == null) {
                                MessagesController.this.processLoadedDialogs((messages_Dialogs) response, null, 0, count, 0, false, false, false);
                            }
                        }
                    });
                }
            }
        }
    }

    public void forceResetDialogs() {
        resetDialogs(true, MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
    }

    private void resetDialogs(boolean query, int seq, int newPts, int date, int qts) {
        MessagesController messagesController = this;
        if (query) {
            if (!messagesController.resetingDialogs) {
                messagesController.resetingDialogs = true;
                final int i = seq;
                final int i2 = newPts;
                final int i3 = date;
                final int i4 = qts;
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (response != null) {
                            MessagesController.this.resetDialogsPinned = (TL_messages_peerDialogs) response;
                            MessagesController.this.resetDialogs(false, i, i2, i3, i4);
                        }
                    }
                });
                TL_messages_getDialogs req2 = new TL_messages_getDialogs();
                req2.limit = 100;
                req2.exclude_pinned = true;
                req2.offset_peer = new TL_inputPeerEmpty();
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesController.this.resetDialogsAll = (messages_Dialogs) response;
                            MessagesController.this.resetDialogs(false, i, i2, i3, i4);
                        }
                    }
                });
            }
        } else if (!(messagesController.resetDialogsPinned == null || messagesController.resetDialogsAll == null)) {
            int a;
            SparseArray<Chat> chatsDict;
            int messagesCount = messagesController.resetDialogsAll.messages.size();
            int dialogsCount = messagesController.resetDialogsAll.dialogs.size();
            messagesController.resetDialogsAll.dialogs.addAll(messagesController.resetDialogsPinned.dialogs);
            messagesController.resetDialogsAll.messages.addAll(messagesController.resetDialogsPinned.messages);
            messagesController.resetDialogsAll.users.addAll(messagesController.resetDialogsPinned.users);
            messagesController.resetDialogsAll.chats.addAll(messagesController.resetDialogsPinned.chats);
            LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
            LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
            SparseArray<User> usersDict = new SparseArray();
            SparseArray<Chat> chatsDict2 = new SparseArray();
            for (a = 0; a < messagesController.resetDialogsAll.users.size(); a++) {
                User u = (User) messagesController.resetDialogsAll.users.get(a);
                usersDict.put(u.id, u);
            }
            for (a = 0; a < messagesController.resetDialogsAll.chats.size(); a++) {
                Chat c = (Chat) messagesController.resetDialogsAll.chats.get(a);
                chatsDict2.put(c.id, c);
            }
            Message lastMessage = null;
            a = 0;
            while (true) {
                int a2 = a;
                if (a2 >= messagesController.resetDialogsAll.messages.size()) {
                    break;
                }
                int a3;
                Message message = (Message) messagesController.resetDialogsAll.messages.get(a2);
                if (a2 < messagesCount && (lastMessage == null || message.date < lastMessage.date)) {
                    lastMessage = message;
                }
                Message lastMessage2 = lastMessage;
                Chat chat;
                MessageObject messageObject;
                if (message.to_id.channel_id != 0) {
                    chat = (Chat) chatsDict2.get(message.to_id.channel_id);
                    if (chat == null || !chat.left) {
                        if (chat != null && chat.megagroup) {
                            message.flags |= Integer.MIN_VALUE;
                        }
                        a3 = a2;
                        messageObject = new MessageObject(messagesController.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict2, false);
                        new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                        a = a3 + 1;
                        lastMessage = lastMessage2;
                    }
                } else {
                    if (message.to_id.chat_id != 0) {
                        chat = (Chat) chatsDict2.get(message.to_id.chat_id);
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    a3 = a2;
                    messageObject = new MessageObject(messagesController.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict2, false);
                    new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                    a = a3 + 1;
                    lastMessage = lastMessage2;
                }
                a3 = a2;
                a = a3 + 1;
                lastMessage = lastMessage2;
            }
            for (a = 0; a < messagesController.resetDialogsAll.dialogs.size(); a++) {
                TL_dialog d = (TL_dialog) messagesController.resetDialogsAll.dialogs.get(a);
                if (d.id == 0 && d.peer != null) {
                    if (d.peer.user_id != 0) {
                        d.id = (long) d.peer.user_id;
                    } else if (d.peer.chat_id != 0) {
                        d.id = (long) (-d.peer.chat_id);
                    } else if (d.peer.channel_id != 0) {
                        d.id = (long) (-d.peer.channel_id);
                    }
                }
                if (d.id != 0) {
                    if (d.last_message_date == 0) {
                        MessageObject mess = (MessageObject) new_dialogMessage.get(d.id);
                        if (mess != null) {
                            d.last_message_date = mess.messageOwner.date;
                        }
                    }
                    if (DialogObject.isChannel(d)) {
                        Chat chat2 = (Chat) chatsDict2.get(-((int) d.id));
                        if (chat2 == null || !chat2.left) {
                            messagesController.channelsPts.put(-((int) d.id), d.pts);
                        }
                    } else if (((int) d.id) < 0) {
                        Chat chat3 = (Chat) chatsDict2.get(-((int) d.id));
                        if (!(chat3 == null || chat3.migrated_to == null)) {
                        }
                    }
                    new_dialogs_dict.put(d.id, d);
                    Integer value = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    messagesController.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                    value = (Integer) messagesController.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    messagesController.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                }
            }
            ImageLoader.saveMessagesThumbs(messagesController.resetDialogsAll.messages);
            int a4 = 0;
            while (a4 < messagesController.resetDialogsAll.messages.size()) {
                SparseArray<User> usersDict2;
                Message message2 = (Message) messagesController.resetDialogsAll.messages.get(a4);
                if (message2.action instanceof TL_messageActionChatDeleteUser) {
                    User user = (User) usersDict.get(message2.action.user_id);
                    if (user != null && user.bot) {
                        message2.reply_markup = new TL_replyKeyboardHide();
                        message2.flags |= 64;
                    }
                }
                if (message2.action instanceof TL_messageActionChatMigrateTo) {
                    usersDict2 = usersDict;
                    chatsDict = chatsDict2;
                } else if (message2.action instanceof TL_messageActionChannelCreate) {
                    usersDict2 = usersDict;
                    chatsDict = chatsDict2;
                } else {
                    ConcurrentHashMap<Long, Integer> read_max = message2.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                    Integer value2 = (Integer) read_max.get(Long.valueOf(message2.dialog_id));
                    if (value2 == null) {
                        usersDict2 = usersDict;
                        chatsDict = chatsDict2;
                        value2 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message2.out, message2.dialog_id));
                        read_max.put(Long.valueOf(message2.dialog_id), value2);
                    } else {
                        usersDict2 = usersDict;
                        chatsDict = chatsDict2;
                    }
                    message2.unread = value2.intValue() < message2.id;
                    a4++;
                    usersDict = usersDict2;
                    chatsDict2 = chatsDict;
                }
                message2.unread = false;
                message2.media_unread = false;
                a4++;
                usersDict = usersDict2;
                chatsDict2 = chatsDict;
            }
            chatsDict = chatsDict2;
            MessagesStorage.getInstance(messagesController.currentAccount).resetDialogs(messagesController.resetDialogsAll, messagesCount, seq, newPts, date, qts, new_dialogs_dict, new_dialogMessage, lastMessage, dialogsCount);
            messagesController.resetDialogsPinned = null;
            messagesController.resetDialogsAll = null;
        }
    }

    protected void completeDialogsReset(messages_Dialogs dialogsRes, int messagesCount, int seq, int newPts, int date, int qts, LongSparseArray<TL_dialog> new_dialogs_dict, LongSparseArray<MessageObject> new_dialogMessage, Message lastMessage) {
        final int i = newPts;
        final int i2 = date;
        final int i3 = qts;
        final messages_Dialogs messages_dialogs = dialogsRes;
        final LongSparseArray<TL_dialog> longSparseArray = new_dialogs_dict;
        final LongSparseArray<MessageObject> longSparseArray2 = new_dialogMessage;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$67$1 */
            class C03461 implements Runnable {
                C03461() {
                }

                public void run() {
                    int a;
                    MessagesController.this.resetingDialogs = false;
                    MessagesController.this.applyDialogsNotificationsSettings(messages_dialogs.dialogs);
                    if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
                        DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
                    }
                    MessagesController.this.putUsers(messages_dialogs.users, false);
                    MessagesController.this.putChats(messages_dialogs.chats, false);
                    for (a = 0; a < MessagesController.this.dialogs.size(); a++) {
                        TL_dialog oldDialog = (TL_dialog) MessagesController.this.dialogs.get(a);
                        if (((int) oldDialog.id) != 0) {
                            MessagesController.this.dialogs_dict.remove(oldDialog.id);
                            MessageObject messageObject = (MessageObject) MessagesController.this.dialogMessage.get(oldDialog.id);
                            MessagesController.this.dialogMessage.remove(oldDialog.id);
                            if (messageObject != null) {
                                MessagesController.this.dialogMessagesByIds.remove(messageObject.getId());
                                if (messageObject.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                                }
                            }
                        }
                    }
                    for (a = 0; a < longSparseArray.size(); a++) {
                        long key = longSparseArray.keyAt(a);
                        oldDialog = (TL_dialog) longSparseArray.valueAt(a);
                        if (oldDialog.draft instanceof TL_draftMessage) {
                            DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(oldDialog.id, oldDialog.draft, null, false);
                        }
                        MessagesController.this.dialogs_dict.put(key, oldDialog);
                        MessageObject messageObject2 = (MessageObject) longSparseArray2.get(oldDialog.id);
                        MessagesController.this.dialogMessage.put(key, messageObject2);
                        if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                            MessagesController.this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                            if (messageObject2.messageOwner.random_id != 0) {
                                MessagesController.this.dialogMessagesByRandomIds.put(messageObject2.messageOwner.random_id, messageObject2);
                            }
                        }
                    }
                    MessagesController.this.dialogs.clear();
                    int size = MessagesController.this.dialogs_dict.size();
                    for (a = 0; a < size; a++) {
                        MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(a));
                    }
                    MessagesController.this.sortDialogs(null);
                    MessagesController.this.dialogsEndReached = true;
                    MessagesController.this.serverDialogsEndReached = false;
                    if (!(UserConfig.getInstance(MessagesController.this.currentAccount).totalDialogsLoadCount >= 400 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID)) {
                        MessagesController.this.loadDialogs(0, 100, false);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }

            public void run() {
                MessagesController.this.gettingDifference = false;
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(i);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(i2);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(i3);
                MessagesController.this.getDifference();
                AndroidUtilities.runOnUIThread(new C03461());
            }
        });
    }

    private void migrateDialogs(final int offset, int offsetDate, int offsetUser, int offsetChat, int offsetChannel, long accessPeer) {
        if (!this.migratingDialogs) {
            if (offset != -1) {
                this.migratingDialogs = true;
                TL_messages_getDialogs req = new TL_messages_getDialogs();
                req.exclude_pinned = true;
                req.limit = 100;
                req.offset_id = offset;
                req.offset_date = offsetDate;
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("start migrate with id ");
                    stringBuilder.append(offset);
                    stringBuilder.append(" date ");
                    stringBuilder.append(LocaleController.getInstance().formatterStats.format(((long) offsetDate) * 1000));
                    FileLog.m0d(stringBuilder.toString());
                }
                if (offset == 0) {
                    req.offset_peer = new TL_inputPeerEmpty();
                } else {
                    if (offsetChannel != 0) {
                        req.offset_peer = new TL_inputPeerChannel();
                        req.offset_peer.channel_id = offsetChannel;
                    } else if (offsetUser != 0) {
                        req.offset_peer = new TL_inputPeerUser();
                        req.offset_peer.user_id = offsetUser;
                    } else {
                        req.offset_peer = new TL_inputPeerChat();
                        req.offset_peer.chat_id = offsetChat;
                    }
                    req.offset_peer.access_hash = accessPeer;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$68$2 */
                    class C03492 implements Runnable {
                        C03492() {
                        }

                        public void run() {
                            MessagesController.this.migratingDialogs = false;
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            final messages_Dialogs dialogsRes = (messages_Dialogs) response;
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.MessagesController$68$1$1 */
                                class C03471 implements Runnable {
                                    C03471() {
                                    }

                                    public void run() {
                                        MessagesController.this.migratingDialogs = false;
                                    }
                                }

                                public void run() {
                                    try {
                                        int a;
                                        StringBuilder stringBuilder;
                                        long did;
                                        TL_dialog dialog;
                                        UserConfig instance = UserConfig.getInstance(MessagesController.this.currentAccount);
                                        instance.totalDialogsLoadCount += dialogsRes.dialogs.size();
                                        int a2 = 0;
                                        Message lastMessage = null;
                                        for (a = 0; a < dialogsRes.messages.size(); a++) {
                                            Message message = (Message) dialogsRes.messages.get(a);
                                            if (BuildVars.LOGS_ENABLED) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("search migrate id ");
                                                stringBuilder.append(message.id);
                                                stringBuilder.append(" date ");
                                                stringBuilder.append(LocaleController.getInstance().formatterStats.format(((long) message.date) * 1000));
                                                FileLog.m0d(stringBuilder.toString());
                                            }
                                            if (lastMessage == null || message.date < lastMessage.date) {
                                                lastMessage = message;
                                            }
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("migrate step with id ");
                                            stringBuilder2.append(lastMessage.id);
                                            stringBuilder2.append(" date ");
                                            stringBuilder2.append(LocaleController.getInstance().formatterStats.format(((long) lastMessage.date) * 1000));
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                        if (dialogsRes.dialogs.size() >= 100) {
                                            a = lastMessage.id;
                                        } else {
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m0d("migrate stop due to not 100 dialogs");
                                            }
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                                            a = -1;
                                        }
                                        StringBuilder dids = new StringBuilder(dialogsRes.dialogs.size() * 12);
                                        LongSparseArray<TL_dialog> dialogHashMap = new LongSparseArray();
                                        for (int a3 = 0; a3 < dialogsRes.dialogs.size(); a3++) {
                                            TL_dialog dialog2 = (TL_dialog) dialogsRes.dialogs.get(a3);
                                            if (dialog2.peer.channel_id != 0) {
                                                dialog2.id = (long) (-dialog2.peer.channel_id);
                                            } else if (dialog2.peer.chat_id != 0) {
                                                dialog2.id = (long) (-dialog2.peer.chat_id);
                                            } else {
                                                dialog2.id = (long) dialog2.peer.user_id;
                                            }
                                            if (dids.length() > 0) {
                                                dids.append(",");
                                            }
                                            dids.append(dialog2.id);
                                            dialogHashMap.put(dialog2.id, dialog2);
                                        }
                                        SQLiteCursor cursor = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", new Object[]{dids.toString()}), new Object[0]);
                                        while (cursor.next()) {
                                            did = cursor.longValue(0);
                                            dialog = (TL_dialog) dialogHashMap.get(did);
                                            dialogHashMap.remove(did);
                                            if (dialog != null) {
                                                dialogsRes.dialogs.remove(dialog);
                                                int a4 = 0;
                                                while (a4 < dialogsRes.messages.size()) {
                                                    Message message2 = (Message) dialogsRes.messages.get(a4);
                                                    if (MessageObject.getDialogId(message2) == did) {
                                                        dialogsRes.messages.remove(a4);
                                                        a4--;
                                                        if (message2.id == dialog.top_message) {
                                                            dialog.top_message = 0;
                                                            break;
                                                        }
                                                    }
                                                    a4++;
                                                }
                                            }
                                        }
                                        cursor.dispose();
                                        if (BuildVars.LOGS_ENABLED) {
                                            StringBuilder stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("migrate found missing dialogs ");
                                            stringBuilder3.append(dialogsRes.dialogs.size());
                                            FileLog.m0d(stringBuilder3.toString());
                                        }
                                        SQLiteCursor cursor2 = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                                        if (cursor2.next()) {
                                            int date = Math.max(NUM, cursor2.intValue(0));
                                            int offsetId = a;
                                            a = 0;
                                            while (a < dialogsRes.messages.size()) {
                                                Message message3 = (Message) dialogsRes.messages.get(a);
                                                if (message3.date < date) {
                                                    if (offset != -1) {
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                                                        offsetId = -1;
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            StringBuilder stringBuilder4 = new StringBuilder();
                                                            stringBuilder4.append("migrate stop due to reached loaded dialogs ");
                                                            stringBuilder4.append(LocaleController.getInstance().formatterStats.format(((long) date) * 1000));
                                                            FileLog.m0d(stringBuilder4.toString());
                                                        }
                                                    }
                                                    dialogsRes.messages.remove(a);
                                                    a--;
                                                    did = MessageObject.getDialogId(message3);
                                                    dialog = (TL_dialog) dialogHashMap.get(did);
                                                    dialogHashMap.remove(did);
                                                    if (dialog != null) {
                                                        dialogsRes.dialogs.remove(dialog);
                                                    }
                                                }
                                                a++;
                                            }
                                            if (lastMessage == null || lastMessage.date >= date || offset == -1) {
                                                a = offsetId;
                                            } else {
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                                                a = -1;
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("migrate stop due to reached loaded dialogs ");
                                                    stringBuilder.append(LocaleController.getInstance().formatterStats.format(((long) date) * 1000));
                                                    FileLog.m0d(stringBuilder.toString());
                                                }
                                            }
                                        }
                                        cursor2.dispose();
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate = lastMessage.date;
                                        Chat chat;
                                        if (lastMessage.to_id.channel_id != 0) {
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = lastMessage.to_id.channel_id;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                                            while (a2 < dialogsRes.chats.size()) {
                                                chat = (Chat) dialogsRes.chats.get(a2);
                                                if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId) {
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = chat.access_hash;
                                                    break;
                                                }
                                                a2++;
                                            }
                                        } else if (lastMessage.to_id.chat_id != 0) {
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = lastMessage.to_id.chat_id;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                                            while (a2 < dialogsRes.chats.size()) {
                                                chat = (Chat) dialogsRes.chats.get(a2);
                                                if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId) {
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = chat.access_hash;
                                                    break;
                                                }
                                                a2++;
                                            }
                                        } else if (lastMessage.to_id.user_id != 0) {
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = lastMessage.to_id.user_id;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                                            while (a2 < dialogsRes.users.size()) {
                                                User user = (User) dialogsRes.users.get(a2);
                                                if (user.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId) {
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = user.access_hash;
                                                    break;
                                                }
                                                a2++;
                                            }
                                        }
                                        MessagesController.this.processLoadedDialogs(dialogsRes, null, a, 0, 0, false, true, false);
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                        AndroidUtilities.runOnUIThread(new C03471());
                                    }
                                }
                            });
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new C03492());
                    }
                });
            }
        }
    }

    public void processLoadedDialogs(messages_Dialogs dialogsRes, ArrayList<EncryptedChat> encChats, int offset, int count, int loadType, boolean resetEnd, boolean migrate, boolean fromCache) {
        final int i = loadType;
        final messages_Dialogs messages_dialogs = dialogsRes;
        final boolean z = resetEnd;
        final int i2 = count;
        final int i3 = offset;
        final boolean z2 = fromCache;
        final boolean z3 = migrate;
        final ArrayList<EncryptedChat> arrayList = encChats;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$69$1 */
            class C03501 implements Runnable {
                C03501() {
                }

                public void run() {
                    MessagesController.this.putUsers(messages_dialogs.users, true);
                    MessagesController.this.loadingDialogs = false;
                    if (z) {
                        MessagesController.this.dialogsEndReached = false;
                        MessagesController.this.serverDialogsEndReached = false;
                    } else if (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        MessagesController.this.dialogsEndReached = true;
                        MessagesController.this.serverDialogsEndReached = true;
                    } else {
                        MessagesController.this.loadDialogs(0, i2, false);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }

            public void run() {
                boolean z = true;
                boolean z2 = false;
                if (!MessagesController.this.firstGettingTask) {
                    MessagesController.this.getNewDeleteTask(null, 0);
                    MessagesController.this.firstGettingTask = true;
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("loaded loadType ");
                    stringBuilder.append(i);
                    stringBuilder.append(" count ");
                    stringBuilder.append(messages_dialogs.dialogs.size());
                    FileLog.m0d(stringBuilder.toString());
                }
                if (i == 1 && messages_dialogs.dialogs.size() == 0) {
                    AndroidUtilities.runOnUIThread(new C03501());
                    return;
                }
                int a;
                int i;
                LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
                LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
                SparseArray<User> usersDict = new SparseArray();
                SparseArray<Chat> chatsDict = new SparseArray();
                for (a = 0; a < messages_dialogs.users.size(); a++) {
                    User u = (User) messages_dialogs.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (a = 0; a < messages_dialogs.chats.size(); a++) {
                    Chat c = (Chat) messages_dialogs.chats.get(a);
                    chatsDict.put(c.id, c);
                }
                if (i == 1) {
                    MessagesController.this.nextDialogsCacheOffset = i3 + i2;
                }
                Message lastMessage = null;
                a = 0;
                while (a < messages_dialogs.messages.size()) {
                    Message lastMessage2;
                    Chat chat;
                    MessageObject messageObject;
                    Message message = (Message) messages_dialogs.messages.get(a);
                    if (lastMessage != null) {
                        if (message.date >= lastMessage.date) {
                            lastMessage2 = lastMessage;
                            if (message.to_id.channel_id != 0) {
                                chat = (Chat) chatsDict.get(message.to_id.channel_id);
                                if (chat == null && chat.left) {
                                    a++;
                                    lastMessage = lastMessage2;
                                } else if (chat != null && chat.megagroup) {
                                    message.flags |= Integer.MIN_VALUE;
                                }
                            } else if (message.to_id.chat_id != 0) {
                                chat = (Chat) chatsDict.get(message.to_id.chat_id);
                                if (!(chat == null || chat.migrated_to == null)) {
                                    a++;
                                    lastMessage = lastMessage2;
                                }
                            }
                            messageObject = new MessageObject(MessagesController.this.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict, false);
                            new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                            a++;
                            lastMessage = lastMessage2;
                        }
                    }
                    lastMessage2 = message;
                    if (message.to_id.channel_id != 0) {
                        chat = (Chat) chatsDict.get(message.to_id.channel_id);
                        if (chat == null) {
                        }
                        message.flags |= Integer.MIN_VALUE;
                    } else if (message.to_id.chat_id != 0) {
                        chat = (Chat) chatsDict.get(message.to_id.chat_id);
                        a++;
                        lastMessage = lastMessage2;
                    }
                    messageObject = new MessageObject(MessagesController.this.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict, false);
                    new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                    a++;
                    lastMessage = lastMessage2;
                }
                if (!(z2 || z3 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || i != 0)) {
                    if (lastMessage == null || lastMessage.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId) {
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    } else {
                        UserConfig instance = UserConfig.getInstance(MessagesController.this.currentAccount);
                        instance.totalDialogsLoadCount += messages_dialogs.dialogs.size();
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = lastMessage.id;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = lastMessage.date;
                        if (lastMessage.to_id.channel_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = lastMessage.to_id.channel_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (a = 0; a < messages_dialogs.chats.size(); a++) {
                                c = (Chat) messages_dialogs.chats.get(a);
                                if (c.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = c.access_hash;
                                    break;
                                }
                            }
                        } else if (lastMessage.to_id.chat_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = lastMessage.to_id.chat_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (a = 0; a < messages_dialogs.chats.size(); a++) {
                                c = (Chat) messages_dialogs.chats.get(a);
                                if (c.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = c.access_hash;
                                    break;
                                }
                            }
                        } else if (lastMessage.to_id.user_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = lastMessage.to_id.user_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            for (a = 0; a < messages_dialogs.users.size(); a++) {
                                u = (User) messages_dialogs.users.get(a);
                                if (u.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = u.access_hash;
                                    break;
                                }
                            }
                        }
                    }
                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                }
                ArrayList<TL_dialog> dialogsToReload = new ArrayList();
                a = 0;
                while (a < messages_dialogs.dialogs.size()) {
                    Integer value;
                    TL_dialog d = (TL_dialog) messages_dialogs.dialogs.get(a);
                    if (d.id == 0 && d.peer != null) {
                        if (d.peer.user_id != 0) {
                            d.id = (long) d.peer.user_id;
                        } else if (d.peer.chat_id != 0) {
                            d.id = (long) (-d.peer.chat_id);
                        } else if (d.peer.channel_id != 0) {
                            d.id = (long) (-d.peer.channel_id);
                        }
                    }
                    if (d.id != 0) {
                        if (d.last_message_date == 0) {
                            MessageObject mess = (MessageObject) new_dialogMessage.get(d.id);
                            if (mess != null) {
                                d.last_message_date = mess.messageOwner.date;
                            }
                        }
                        boolean allowCheck = true;
                        Chat chat2;
                        if (DialogObject.isChannel(d)) {
                            chat2 = (Chat) chatsDict.get(-((int) d.id));
                            if (chat2 != null) {
                                if (!chat2.megagroup) {
                                    allowCheck = false;
                                }
                                if (chat2.left) {
                                }
                            }
                            MessagesController.this.channelsPts.put(-((int) d.id), d.pts);
                        } else if (((int) d.id) < 0) {
                            chat2 = (Chat) chatsDict.get(-((int) d.id));
                            if (!(chat2 == null || chat2.migrated_to == null)) {
                            }
                        }
                        new_dialogs_dict.put(d.id, d);
                        if (allowCheck && i == r1 && ((d.read_outbox_max_id == 0 || d.read_inbox_max_id == 0) && d.top_message != 0)) {
                            dialogsToReload.add(d);
                        }
                        value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                        Integer value2 = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                        if (value2 == null) {
                            value2 = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value2.intValue(), d.read_outbox_max_id)));
                    }
                    a++;
                    z = true;
                }
                if (i != 1) {
                    ImageLoader.saveMessagesThumbs(messages_dialogs.messages);
                    a = 0;
                    while (a < messages_dialogs.messages.size()) {
                        message = (Message) messages_dialogs.messages.get(a);
                        if (message.action instanceof TL_messageActionChatDeleteUser) {
                            User user = (User) usersDict.get(message.action.user_id);
                            if (user != null && user.bot) {
                                message.reply_markup = new TL_replyKeyboardHide();
                                message.flags |= 64;
                            }
                        }
                        if (!(message.action instanceof TL_messageActionChatMigrateTo)) {
                            if (!(message.action instanceof TL_messageActionChannelCreate)) {
                                ConcurrentHashMap<Long, Integer> read_max = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                                value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                                if (value == null) {
                                    value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                                    read_max.put(Long.valueOf(message.dialog_id), value);
                                }
                                message.unread = value.intValue() < message.id;
                                z = false;
                                a++;
                                z2 = z;
                            }
                        }
                        z = false;
                        message.unread = false;
                        message.media_unread = false;
                        a++;
                        z2 = z;
                    }
                    i = z2;
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(messages_dialogs, i);
                } else {
                    i = 0;
                }
                if (i == 2) {
                    Chat chat3 = (Chat) messages_dialogs.chats.get(i);
                    MessagesController.this.getChannelDifference(chat3.id);
                    MessagesController.this.checkChannelInviter(chat3.id);
                }
                final LongSparseArray<TL_dialog> longSparseArray = new_dialogs_dict;
                final LongSparseArray<MessageObject> longSparseArray2 = new_dialogMessage;
                final SparseArray<Chat> sparseArray = chatsDict;
                final ArrayList<TL_dialog> chatsDict2 = dialogsToReload;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int a;
                        if (i != 1) {
                            MessagesController.this.applyDialogsNotificationsSettings(messages_dialogs.dialogs);
                            if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
                                DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
                            }
                        }
                        MessagesController.this.putUsers(messages_dialogs.users, i == 1);
                        MessagesController.this.putChats(messages_dialogs.chats, i == 1);
                        if (arrayList != null) {
                            for (a = 0; a < arrayList.size(); a++) {
                                EncryptedChat encryptedChat = (EncryptedChat) arrayList.get(a);
                                if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 73) {
                                    SecretChatHelper.getInstance(MessagesController.this.currentAccount).sendNotifyLayerMessage(encryptedChat, null);
                                }
                                MessagesController.this.putEncryptedChat(encryptedChat, true);
                            }
                        }
                        if (!z3) {
                            MessagesController.this.loadingDialogs = false;
                        }
                        int lastDialogDate = (!z3 || MessagesController.this.dialogs.isEmpty()) ? 0 : ((TL_dialog) MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
                        boolean added = false;
                        a = 0;
                        while (a < longSparseArray.size()) {
                            int lastDialogDate2;
                            long key = longSparseArray.keyAt(a);
                            TL_dialog value = (TL_dialog) longSparseArray.valueAt(a);
                            if (!z3 || lastDialogDate == 0 || value.last_message_date >= lastDialogDate) {
                                TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(key);
                                if (i == 1 || !(value.draft instanceof TL_draftMessage)) {
                                    lastDialogDate2 = lastDialogDate;
                                } else {
                                    lastDialogDate2 = lastDialogDate;
                                    DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(value.id, value.draft, null, false);
                                }
                                MessageObject lastDialogDate3;
                                if (currentDialog == null) {
                                    MessagesController.this.dialogs_dict.put(key, value);
                                    lastDialogDate3 = (MessageObject) longSparseArray2.get(value.id);
                                    MessagesController.this.dialogMessage.put(key, lastDialogDate3);
                                    if (lastDialogDate3 != null && lastDialogDate3.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(lastDialogDate3.getId(), lastDialogDate3);
                                        if (lastDialogDate3.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(lastDialogDate3.messageOwner.random_id, lastDialogDate3);
                                        }
                                    }
                                    added = true;
                                } else {
                                    if (i != 1) {
                                        currentDialog.notify_settings = value.notify_settings;
                                    }
                                    currentDialog.pinned = value.pinned;
                                    currentDialog.pinnedNum = value.pinnedNum;
                                    MessageObject oldMsg = (MessageObject) MessagesController.this.dialogMessage.get(key);
                                    if ((oldMsg == null || oldMsg.deleted == 0) && oldMsg != null) {
                                        if (currentDialog.top_message <= 0) {
                                            lastDialogDate3 = (MessageObject) longSparseArray2.get(value.id);
                                            if (oldMsg.deleted || lastDialogDate3 == null || lastDialogDate3.messageOwner.date > oldMsg.messageOwner.date) {
                                                MessagesController.this.dialogs_dict.put(key, value);
                                                MessagesController.this.dialogMessage.put(key, lastDialogDate3);
                                                if (lastDialogDate3 != null && lastDialogDate3.messageOwner.to_id.channel_id == 0) {
                                                    MessagesController.this.dialogMessagesByIds.put(lastDialogDate3.getId(), lastDialogDate3);
                                                    if (!(lastDialogDate3 == null || lastDialogDate3.messageOwner.random_id == 0)) {
                                                        MessagesController.this.dialogMessagesByRandomIds.put(lastDialogDate3.messageOwner.random_id, lastDialogDate3);
                                                    }
                                                }
                                                MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                                if (oldMsg.messageOwner.random_id != 0) {
                                                    MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                                }
                                            }
                                        }
                                    }
                                    if (value.top_message >= currentDialog.top_message) {
                                        MessagesController.this.dialogs_dict.put(key, value);
                                        lastDialogDate3 = (MessageObject) longSparseArray2.get(value.id);
                                        MessagesController.this.dialogMessage.put(key, lastDialogDate3);
                                        if (lastDialogDate3 != null && lastDialogDate3.messageOwner.to_id.channel_id == 0) {
                                            MessagesController.this.dialogMessagesByIds.put(lastDialogDate3.getId(), lastDialogDate3);
                                            if (!(lastDialogDate3 == null || lastDialogDate3.messageOwner.random_id == 0)) {
                                                MessagesController.this.dialogMessagesByRandomIds.put(lastDialogDate3.messageOwner.random_id, lastDialogDate3);
                                            }
                                        }
                                        if (oldMsg != null) {
                                            MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                            if (oldMsg.messageOwner.random_id != 0) {
                                                MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                            }
                                        }
                                    }
                                }
                            } else {
                                lastDialogDate2 = lastDialogDate;
                            }
                            a++;
                            lastDialogDate = lastDialogDate2;
                        }
                        MessagesController.this.dialogs.clear();
                        int size = MessagesController.this.dialogs_dict.size();
                        for (a = 0; a < size; a++) {
                            MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(a));
                        }
                        MessagesController.this.sortDialogs(z3 ? sparseArray : null);
                        if (!(i == 2 || z3)) {
                            MessagesController messagesController = MessagesController.this;
                            boolean z = (messages_dialogs.dialogs.size() == 0 || messages_dialogs.dialogs.size() != i2) && i == 0;
                            messagesController.dialogsEndReached = z;
                            if (!z2) {
                                messagesController = MessagesController.this;
                                z = (messages_dialogs.dialogs.size() == 0 || messages_dialogs.dialogs.size() != i2) && i == 0;
                                messagesController.serverDialogsEndReached = z;
                            }
                        }
                        if (!(z2 || z3 || UserConfig.getInstance(MessagesController.this.currentAccount).totalDialogsLoadCount >= 400 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID)) {
                            MessagesController.this.loadDialogs(0, 100, false);
                        }
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        if (z3) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId = i3;
                            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                            MessagesController.this.migratingDialogs = false;
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                        } else {
                            MessagesController.this.generateUpdateMessage();
                            if (!added && i == 1) {
                                MessagesController.this.loadDialogs(0, i2, false);
                            }
                        }
                        MessagesController.this.migrateDialogs(UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess);
                        if (!chatsDict2.isEmpty()) {
                            MessagesController.this.reloadDialogsReadValue(chatsDict2, 0);
                        }
                    }
                });
            }
        });
    }

    private void applyDialogNotificationsSettings(long dialog_id, PeerNotifySettings notify_settings) {
        long j = dialog_id;
        PeerNotifySettings peerNotifySettings = notify_settings;
        int currentValue = this.notificationsPreferences;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(j);
        currentValue = currentValue.getInt(stringBuilder.toString(), 0);
        int currentValue2 = this.notificationsPreferences;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("notifyuntil_");
        stringBuilder2.append(j);
        currentValue2 = currentValue2.getInt(stringBuilder2.toString(), 0);
        Editor editor = this.notificationsPreferences.edit();
        boolean updated = false;
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(j);
        if (dialog != null) {
            dialog.notify_settings = peerNotifySettings;
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("silent_");
        stringBuilder3.append(j);
        editor.putBoolean(stringBuilder3.toString(), peerNotifySettings.silent);
        Editor editor2;
        if (peerNotifySettings.mute_until > ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime()) {
            int until = 0;
            StringBuilder stringBuilder4;
            if (peerNotifySettings.mute_until <= ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime() + 31536000) {
                if (!(currentValue == 3 && currentValue2 == peerNotifySettings.mute_until)) {
                    updated = true;
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("notify2_");
                    stringBuilder4.append(j);
                    editor.putInt(stringBuilder4.toString(), 3);
                    StringBuilder stringBuilder5 = new StringBuilder();
                    stringBuilder5.append("notifyuntil_");
                    stringBuilder5.append(j);
                    editor.putInt(stringBuilder5.toString(), peerNotifySettings.mute_until);
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                }
                until = peerNotifySettings.mute_until;
            } else if (currentValue != 2) {
                updated = true;
                stringBuilder4 = new StringBuilder();
                stringBuilder4.append("notify2_");
                stringBuilder4.append(j);
                editor.putInt(stringBuilder4.toString(), 2);
                if (dialog != null) {
                    dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                }
            }
            editor2 = editor;
            MessagesStorage.getInstance(r0.currentAccount).setDialogFlags(j, (((long) until) << 32) | 1);
            NotificationsController.getInstance(r0.currentAccount).removeNotificationsForDialog(j);
            editor = editor2;
        } else {
            editor2 = editor;
            if (currentValue == 0 || currentValue == 1) {
                editor = editor2;
            } else {
                updated = true;
                if (dialog != null) {
                    dialog.notify_settings.mute_until = 0;
                }
                StringBuilder stringBuilder6 = new StringBuilder();
                stringBuilder6.append("notify2_");
                stringBuilder6.append(j);
                editor = editor2;
                editor.remove(stringBuilder6.toString());
            }
            MessagesStorage.getInstance(r0.currentAccount).setDialogFlags(j, 0);
        }
        editor.commit();
        if (updated) {
            NotificationCenter.getInstance(r0.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<TL_dialog> dialogs) {
        Editor editor = null;
        for (int a = 0; a < dialogs.size(); a++) {
            TL_dialog dialog = (TL_dialog) dialogs.get(a);
            if (dialog.peer != null && (dialog.notify_settings instanceof TL_peerNotifySettings)) {
                int dialog_id;
                StringBuilder stringBuilder;
                if (editor == null) {
                    editor = this.notificationsPreferences.edit();
                }
                if (dialog.peer.user_id != 0) {
                    dialog_id = dialog.peer.user_id;
                } else if (dialog.peer.chat_id != 0) {
                    dialog_id = -dialog.peer.chat_id;
                } else {
                    dialog_id = -dialog.peer.channel_id;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("silent_");
                    stringBuilder.append(dialog_id);
                    editor.putBoolean(stringBuilder.toString(), dialog.notify_settings.silent);
                    if (dialog.notify_settings.mute_until != 0) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("notify2_");
                        stringBuilder.append(dialog_id);
                        editor.remove(stringBuilder.toString());
                    } else if (dialog.notify_settings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("notify2_");
                        stringBuilder.append(dialog_id);
                        editor.putInt(stringBuilder.toString(), 2);
                        dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    } else {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("notify2_");
                        stringBuilder.append(dialog_id);
                        editor.putInt(stringBuilder.toString(), 3);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("notifyuntil_");
                        stringBuilder.append(dialog_id);
                        editor.putInt(stringBuilder.toString(), dialog.notify_settings.mute_until);
                    }
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("silent_");
                stringBuilder.append(dialog_id);
                editor.putBoolean(stringBuilder.toString(), dialog.notify_settings.silent);
                if (dialog.notify_settings.mute_until != 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(dialog_id);
                    editor.remove(stringBuilder.toString());
                } else if (dialog.notify_settings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(dialog_id);
                    editor.putInt(stringBuilder.toString(), 3);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notifyuntil_");
                    stringBuilder.append(dialog_id);
                    editor.putInt(stringBuilder.toString(), dialog.notify_settings.mute_until);
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(dialog_id);
                    editor.putInt(stringBuilder.toString(), 2);
                    dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
    }

    public void reloadMentionsCountForChannels(final ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                for (int a = 0; a < arrayList.size(); a++) {
                    final long dialog_id = (long) (-((Integer) arrayList.get(a)).intValue());
                    TL_messages_getUnreadMentions req = new TL_messages_getUnreadMentions();
                    req.peer = MessagesController.this.getInputPeer((int) dialog_id);
                    req.limit = 1;
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    messages_Messages res = response;
                                    if (res != null) {
                                        int newCount;
                                        if (res.count != 0) {
                                            newCount = res.count;
                                        } else {
                                            newCount = res.messages.size();
                                        }
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).resetMentionsCount(dialog_id, newCount);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void processDialogsUpdateRead(final LongSparseArray<Integer> dialogsToUpdate, final LongSparseArray<Integer> dialogsMentionsToUpdate) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int a;
                if (dialogsToUpdate != null) {
                    for (a = 0; a < dialogsToUpdate.size(); a++) {
                        TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(dialogsToUpdate.keyAt(a));
                        if (currentDialog != null) {
                            currentDialog.unread_count = ((Integer) dialogsToUpdate.valueAt(a)).intValue();
                        }
                    }
                }
                if (dialogsMentionsToUpdate != null) {
                    for (a = 0; a < dialogsMentionsToUpdate.size(); a++) {
                        TL_dialog currentDialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(dialogsMentionsToUpdate.keyAt(a));
                        if (currentDialog2 != null) {
                            currentDialog2.unread_mentions_count = ((Integer) dialogsMentionsToUpdate.valueAt(a)).intValue();
                            if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(currentDialog2.id))) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(currentDialog2.id), Integer.valueOf(currentDialog2.unread_mentions_count));
                            }
                        }
                    }
                }
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                if (dialogsToUpdate != null) {
                    NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                }
            }
        });
    }

    protected void checkLastDialogMessage(TL_dialog dialog, InputPeer peer, long taskId) {
        int lower_id = (int) dialog.id;
        if (lower_id != 0) {
            if (this.checkingLastMessagesDialogs.indexOfKey(lower_id) < 0) {
                TL_messages_getHistory req = new TL_messages_getHistory();
                req.peer = peer == null ? getInputPeer(lower_id) : peer;
                if (req.peer != null) {
                    if (!(req.peer instanceof TL_inputPeerChannel)) {
                        long newTaskId;
                        req.limit = 1;
                        this.checkingLastMessagesDialogs.put(lower_id, true);
                        if (taskId == 0) {
                            long data = null;
                            try {
                                data = new NativeByteBuffer(48 + req.peer.getObjectSize());
                                data.writeInt32(8);
                                data.writeInt64(dialog.id);
                                data.writeInt32(dialog.top_message);
                                data.writeInt32(dialog.read_inbox_max_id);
                                data.writeInt32(dialog.read_outbox_max_id);
                                data.writeInt32(dialog.unread_count);
                                data.writeInt32(dialog.last_message_date);
                                data.writeInt32(dialog.pts);
                                data.writeInt32(dialog.flags);
                                data.writeBool(dialog.pinned);
                                data.writeInt32(dialog.pinnedNum);
                                data.writeInt32(dialog.unread_mentions_count);
                                peer.serializeToStream(data);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        } else {
                            newTaskId = taskId;
                        }
                        final TL_dialog tL_dialog = dialog;
                        final int i = lower_id;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                            /* renamed from: org.telegram.messenger.MessagesController$72$1 */
                            class C03531 implements Runnable {
                                C03531() {
                                }

                                public void run() {
                                    TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(tL_dialog.id);
                                    if (currentDialog != null && currentDialog.top_message == 0) {
                                        MessagesController.this.deleteDialog(tL_dialog.id, 3);
                                    }
                                }
                            }

                            /* renamed from: org.telegram.messenger.MessagesController$72$2 */
                            class C03542 implements Runnable {
                                C03542() {
                                }

                                public void run() {
                                    MessagesController.this.checkingLastMessagesDialogs.delete(i);
                                }
                            }

                            public void run(TLObject response, TL_error error) {
                                if (response != null) {
                                    messages_Messages res = (messages_Messages) response;
                                    if (res.messages.isEmpty()) {
                                        AndroidUtilities.runOnUIThread(new C03531());
                                    } else {
                                        TL_messages_dialogs dialogs = new TL_messages_dialogs();
                                        Message newMessage = (Message) res.messages.get(0);
                                        TL_dialog newDialog = new TL_dialog();
                                        newDialog.flags = tL_dialog.flags;
                                        newDialog.top_message = newMessage.id;
                                        newDialog.last_message_date = newMessage.date;
                                        newDialog.notify_settings = tL_dialog.notify_settings;
                                        newDialog.pts = tL_dialog.pts;
                                        newDialog.unread_count = tL_dialog.unread_count;
                                        newDialog.unread_mentions_count = tL_dialog.unread_mentions_count;
                                        newDialog.read_inbox_max_id = tL_dialog.read_inbox_max_id;
                                        newDialog.read_outbox_max_id = tL_dialog.read_outbox_max_id;
                                        newDialog.pinned = tL_dialog.pinned;
                                        newDialog.pinnedNum = tL_dialog.pinnedNum;
                                        long j = tL_dialog.id;
                                        newDialog.id = j;
                                        newMessage.dialog_id = j;
                                        dialogs.users.addAll(res.users);
                                        dialogs.chats.addAll(res.chats);
                                        dialogs.dialogs.add(newDialog);
                                        dialogs.messages.addAll(res.messages);
                                        dialogs.count = 1;
                                        MessagesController.this.processDialogsUpdate(dialogs, null);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(res.messages, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask(), true);
                                    }
                                }
                                if (newTaskId != 0) {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                }
                                AndroidUtilities.runOnUIThread(new C03542());
                            }
                        });
                    }
                }
            }
        }
    }

    public void processDialogsUpdate(final messages_Dialogs dialogsRes, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                int a;
                final LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
                final LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
                SparseArray<User> usersDict = new SparseArray(dialogsRes.users.size());
                SparseArray<Chat> chatsDict = new SparseArray(dialogsRes.chats.size());
                final LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray();
                for (a = 0; a < dialogsRes.users.size(); a++) {
                    User u = (User) dialogsRes.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (a = 0; a < dialogsRes.chats.size(); a++) {
                    Chat c = (Chat) dialogsRes.chats.get(a);
                    chatsDict.put(c.id, c);
                }
                a = 0;
                while (true) {
                    int a2 = a;
                    if (a2 >= dialogsRes.messages.size()) {
                        break;
                    }
                    Message message = (Message) dialogsRes.messages.get(a2);
                    Chat chat;
                    if (message.to_id.channel_id != 0) {
                        chat = (Chat) chatsDict.get(message.to_id.channel_id);
                        if (chat != null && chat.left) {
                            a = a2 + 1;
                        }
                    } else if (message.to_id.chat_id != 0) {
                        chat = (Chat) chatsDict.get(message.to_id.chat_id);
                        if (!(chat == null || chat.migrated_to == null)) {
                            a = a2 + 1;
                        }
                    }
                    MessageObject messageObject = new MessageObject(MessagesController.this.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict, false);
                    new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                    a = a2 + 1;
                }
                for (a = 0; a < dialogsRes.dialogs.size(); a++) {
                    TL_dialog d = (TL_dialog) dialogsRes.dialogs.get(a);
                    if (d.id == 0) {
                        if (d.peer.user_id != 0) {
                            d.id = (long) d.peer.user_id;
                        } else if (d.peer.chat_id != 0) {
                            d.id = (long) (-d.peer.chat_id);
                        } else if (d.peer.channel_id != 0) {
                            d.id = (long) (-d.peer.channel_id);
                        }
                    }
                    Chat chat2;
                    if (DialogObject.isChannel(d)) {
                        chat2 = (Chat) chatsDict.get(-((int) d.id));
                        if (chat2 != null && chat2.left) {
                        }
                    } else if (((int) d.id) < 0) {
                        chat2 = (Chat) chatsDict.get(-((int) d.id));
                        if (!(chat2 == null || chat2.migrated_to == null)) {
                        }
                    }
                    if (d.last_message_date == 0) {
                        MessageObject mess = (MessageObject) new_dialogMessage.get(d.id);
                        if (mess != null) {
                            d.last_message_date = mess.messageOwner.date;
                        }
                    }
                    new_dialogs_dict.put(d.id, d);
                    dialogsToUpdate.put(d.id, Integer.valueOf(d.unread_count));
                    Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                    value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int a;
                        MessagesController.this.putUsers(dialogsRes.users, true);
                        MessagesController.this.putChats(dialogsRes.chats, true);
                        for (a = 0; a < new_dialogs_dict.size(); a++) {
                            long key = new_dialogs_dict.keyAt(a);
                            TL_dialog value = (TL_dialog) new_dialogs_dict.valueAt(a);
                            TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(key);
                            MessageObject messageObject;
                            if (currentDialog == null) {
                                MessagesController messagesController = MessagesController.this;
                                messagesController.nextDialogsCacheOffset++;
                                MessagesController.this.dialogs_dict.put(key, value);
                                messageObject = (MessageObject) new_dialogMessage.get(value.id);
                                MessagesController.this.dialogMessage.put(key, messageObject);
                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                    if (messageObject.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                    }
                                }
                            } else {
                                currentDialog.unread_count = value.unread_count;
                                if (currentDialog.unread_mentions_count != value.unread_mentions_count) {
                                    currentDialog.unread_mentions_count = value.unread_mentions_count;
                                    if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(currentDialog.id))) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(currentDialog.id), Integer.valueOf(currentDialog.unread_mentions_count));
                                    }
                                }
                                MessageObject oldMsg = (MessageObject) MessagesController.this.dialogMessage.get(key);
                                if (oldMsg != null) {
                                    if (currentDialog.top_message <= 0) {
                                        messageObject = (MessageObject) new_dialogMessage.get(value.id);
                                        if (oldMsg.deleted || messageObject == null || messageObject.messageOwner.date > oldMsg.messageOwner.date) {
                                            MessagesController.this.dialogs_dict.put(key, value);
                                            MessagesController.this.dialogMessage.put(key, messageObject);
                                            if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                                MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                                if (messageObject.messageOwner.random_id != 0) {
                                                    MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                                }
                                            }
                                            MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                            if (oldMsg.messageOwner.random_id != 0) {
                                                MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                            }
                                        }
                                    }
                                }
                                if ((oldMsg != null && oldMsg.deleted) || value.top_message > currentDialog.top_message) {
                                    MessagesController.this.dialogs_dict.put(key, value);
                                    MessageObject messageObject2 = (MessageObject) new_dialogMessage.get(value.id);
                                    MessagesController.this.dialogMessage.put(key, messageObject2);
                                    if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                                        if (messageObject2.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(messageObject2.messageOwner.random_id, messageObject2);
                                        }
                                    }
                                    if (oldMsg != null) {
                                        MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                        if (oldMsg.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                        }
                                    }
                                    if (messageObject2 == null) {
                                        MessagesController.this.checkLastDialogMessage(value, null, 0);
                                    }
                                }
                            }
                        }
                        MessagesController.this.dialogs.clear();
                        int size = MessagesController.this.dialogs_dict.size();
                        for (a = 0; a < size; a++) {
                            MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(a));
                        }
                        MessagesController.this.sortDialogs(null);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                    }
                });
            }
        });
    }

    public void addToViewsQueue(final Message message) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                int peer;
                ArrayList<Integer> ids;
                if (message.to_id.channel_id != 0) {
                    peer = -message.to_id.channel_id;
                } else if (message.to_id.chat_id != 0) {
                    peer = -message.to_id.chat_id;
                } else {
                    peer = message.to_id.user_id;
                    ids = (ArrayList) MessagesController.this.channelViewsToSend.get(peer);
                    if (ids == null) {
                        ids = new ArrayList();
                        MessagesController.this.channelViewsToSend.put(peer, ids);
                    }
                    if (!ids.contains(Integer.valueOf(message.id))) {
                        ids.add(Integer.valueOf(message.id));
                    }
                }
                ids = (ArrayList) MessagesController.this.channelViewsToSend.get(peer);
                if (ids == null) {
                    ids = new ArrayList();
                    MessagesController.this.channelViewsToSend.put(peer, ids);
                }
                if (!ids.contains(Integer.valueOf(message.id))) {
                    ids.add(Integer.valueOf(message.id));
                }
            }
        });
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        ArrayList<Long> arrayList = new ArrayList();
        long messageId = (long) messageObject.getId();
        if (messageObject.messageOwner.to_id.channel_id != 0) {
            messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
        }
        if (messageObject.messageOwner.mentioned) {
            MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(messageObject.getId(), messageObject.messageOwner.to_id.channel_id, messageObject.getDialogId());
        }
        arrayList.add(Long.valueOf(messageId));
        MessagesStorage.getInstance(this.currentAccount).markMessagesContentAsRead(arrayList, 0);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, Integer.MIN_VALUE);
        } else if (messageObject.messageOwner.to_id.channel_id != 0) {
            TL_channels_readMessageContents req = new TL_channels_readMessageContents();
            req.channel = getInputChannel(messageObject.messageOwner.to_id.channel_id);
            if (req.channel != null) {
                req.id.add(Integer.valueOf(messageObject.getId()));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            }
        } else {
            TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
            req2.id.add(Integer.valueOf(messageObject.getId()));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                        MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                    }
                }
            });
        }
    }

    public void markMentionMessageAsRead(int mid, int channelId, long did) {
        MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(mid, channelId, did);
        if (channelId != 0) {
            TL_channels_readMessageContents req = new TL_channels_readMessageContents();
            req.channel = getInputChannel(channelId);
            if (req.channel != null) {
                req.id.add(Integer.valueOf(mid));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            } else {
                return;
            }
        }
        TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
        req2.id.add(Integer.valueOf(mid));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                    MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                }
            }
        });
    }

    public void markMessageAsRead(int mid, int channelId, int ttl) {
        if (mid != 0) {
            if (ttl > 0) {
                int time = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                MessagesStorage.getInstance(this.currentAccount).createTaskForMid(mid, channelId, time, time, ttl, false);
                if (channelId != 0) {
                    TL_channels_readMessageContents req = new TL_channels_readMessageContents();
                    req.channel = getInputChannel(channelId);
                    req.id.add(Integer.valueOf(mid));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                        }
                    });
                } else {
                    TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
                    req2.id.add(Integer.valueOf(mid));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (error == null) {
                                TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                                MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                            }
                        }
                    });
                }
            }
        }
    }

    public void markMessageAsRead(long dialog_id, long random_id, int ttl) {
        MessagesController messagesController = this;
        long j = dialog_id;
        int i = ttl;
        if (!(random_id == 0 || j == 0)) {
            if (i > 0 || i == Integer.MIN_VALUE) {
                int high_id = (int) (j >> 32);
                if (((int) j) == 0) {
                    EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
                    if (chat != null) {
                        ArrayList<Long> random_ids = new ArrayList();
                        random_ids.add(Long.valueOf(random_id));
                        SecretChatHelper.getInstance(messagesController.currentAccount).sendMessagesReadMessage(chat, random_ids, null);
                        if (i > 0) {
                            int time = ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime();
                            MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(chat.id, time, time, 0, random_ids);
                        }
                    }
                }
            }
        }
    }

    private void completeReadTask(ReadTask task) {
        int lower_part = (int) task.dialogId;
        int high_id = (int) (task.dialogId >> 32);
        if (lower_part != 0) {
            TLObject req;
            InputPeer inputPeer = getInputPeer(lower_part);
            if (inputPeer instanceof TL_inputPeerChannel) {
                req = new TL_channels_readHistory();
                req.channel = getInputChannel(-lower_part);
                req.max_id = task.maxId;
            } else {
                req = new TL_messages_readHistory();
                req.peer = inputPeer;
                req.max_id = task.maxId;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null && (response instanceof TL_messages_affectedMessages)) {
                        TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                        MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                    }
                }
            });
            return;
        }
        EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
        if (chat.auth_key != null && chat.auth_key.length > 1 && (chat instanceof TL_encryptedChat)) {
            TL_messages_readEncryptedHistory req2 = new TL_messages_readEncryptedHistory();
            req2.peer = new TL_inputEncryptedChat();
            req2.peer.chat_id = chat.id;
            req2.peer.access_hash = chat.access_hash;
            req2.max_date = task.maxDate;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }

    private void checkReadTasks() {
        long time = SystemClock.uptimeMillis();
        int a = 0;
        int size = this.readTasks.size();
        while (a < size) {
            ReadTask task = (ReadTask) this.readTasks.get(a);
            if (task.sendRequestTime <= time) {
                completeReadTask(task);
                this.readTasks.remove(a);
                this.readTasksMap.remove(task.dialogId);
                a--;
                size--;
            }
            a++;
        }
    }

    public void markDialogAsReadNow(final long dialogId) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                ReadTask currentReadTask = (ReadTask) MessagesController.this.readTasksMap.get(dialogId);
                if (currentReadTask != null) {
                    MessagesController.this.completeReadTask(currentReadTask);
                    MessagesController.this.readTasks.remove(currentReadTask);
                    MessagesController.this.readTasksMap.remove(dialogId);
                }
            }
        });
    }

    public void markDialogAsRead(long dialogId, int maxPositiveId, int maxNegativeId, int maxDate, boolean popup, int countDiff, boolean readNow) {
        MessagesController messagesController = this;
        long j = dialogId;
        final int i = maxPositiveId;
        int i2 = maxNegativeId;
        int i3 = maxDate;
        int lower_part = (int) j;
        int high_id = (int) (j >> 32);
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        if (lower_part != 0) {
            if (i == 0) {
                i4 = i3;
                i5 = lower_part;
                i6 = i2;
                i7 = i;
            } else if (high_id == 1) {
                i8 = high_id;
                i4 = i3;
                i5 = lower_part;
                i6 = i2;
                i7 = i;
            } else {
                long minMessageId;
                boolean isChannel;
                long maxMessageId;
                long maxMessageId2 = (long) i;
                long minMessageId2 = (long) i2;
                if (lower_part >= 0 || !ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                    minMessageId = minMessageId2;
                    isChannel = false;
                    maxMessageId = maxMessageId2;
                } else {
                    boolean isChannel2 = false;
                    isChannel = true;
                    minMessageId = minMessageId2 | (((long) (-lower_part)) << 32);
                    maxMessageId = maxMessageId2 | (((long) (-lower_part)) << true);
                }
                Integer value = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(dialogId));
                if (value == null) {
                    value = Integer.valueOf(0);
                }
                messagesController.dialogs_read_inbox_max.put(Long.valueOf(dialogId), Integer.valueOf(Math.max(value.intValue(), i)));
                boolean z = false;
                MessagesStorage.getInstance(messagesController.currentAccount).processPendingRead(j, maxMessageId, minMessageId, i3, isChannel);
                i8 = high_id;
                i4 = i3;
                final long j2 = j;
                i6 = i2;
                i2 = countDiff;
                i7 = i;
                final boolean z2 = popup;
                MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.MessagesController$84$1 */
                    class C03571 implements Runnable {
                        C03571() {
                        }

                        public void run() {
                            TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(j2);
                            if (dialog != null) {
                                if (i2 != 0) {
                                    if (i < dialog.top_message) {
                                        dialog.unread_count = Math.max(dialog.unread_count - i2, 0);
                                        if (i != Integer.MIN_VALUE && dialog.unread_count > dialog.top_message - i) {
                                            dialog.unread_count = dialog.top_message - i;
                                        }
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                                    }
                                }
                                dialog.unread_count = 0;
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                            }
                            if (z2) {
                                NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j2, 0, i, true);
                                LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
                                dialogsToUpdate.put(j2, Integer.valueOf(-1));
                                NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                                return;
                            }
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j2, 0, i, false);
                            dialogsToUpdate = new LongSparseArray(1);
                            dialogsToUpdate.put(j2, Integer.valueOf(0));
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                        }
                    }

                    public void run() {
                        AndroidUtilities.runOnUIThread(new C03571());
                    }
                });
                if (i7 != Integer.MAX_VALUE) {
                    z = true;
                }
                boolean createReadTask = z;
                int i9 = i8;
                int i10 = i4;
            }
            return;
        }
        i8 = high_id;
        i4 = i3;
        i5 = lower_part;
        i6 = i2;
        i7 = i;
        if (i4 != 0) {
            createReadTask = true;
            EncryptedChat chat = getEncryptedChat(Integer.valueOf(i8));
            int i11 = i4;
            MessagesStorage.getInstance(messagesController.currentAccount).processPendingRead(dialogId, (long) i7, (long) i6, i11, false);
            j2 = dialogId;
            i2 = i11;
            final boolean z3 = popup;
            i10 = i11;
            i11 = countDiff;
            EncryptedChat chat2 = chat;
            chat = maxNegativeId;
            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$85$1 */
                class C03581 implements Runnable {
                    C03581() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j2, i2, 0, z3);
                        TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(j2);
                        if (dialog != null) {
                            if (i11 != 0) {
                                if (chat > dialog.top_message) {
                                    dialog.unread_count = Math.max(dialog.unread_count - i11, 0);
                                    if (chat != ConnectionsManager.DEFAULT_DATACENTER_ID && dialog.unread_count > chat - dialog.top_message) {
                                        dialog.unread_count = chat - dialog.top_message;
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                                }
                            }
                            dialog.unread_count = 0;
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                        }
                        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
                        dialogsToUpdate.put(j2, Integer.valueOf(0));
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03581());
                }
            });
            if (chat2.ttl > 0) {
                int serverTime = Math.max(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime(), i10);
                MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(chat2.id, serverTime, serverTime, 0, null);
            }
        } else {
            return;
        }
        if (createReadTask) {
            j2 = dialogId;
            final boolean z4 = readNow;
            i = i10;
            i11 = maxPositiveId;
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    ReadTask currentReadTask = (ReadTask) MessagesController.this.readTasksMap.get(j2);
                    if (currentReadTask == null) {
                        currentReadTask = new ReadTask();
                        currentReadTask.dialogId = j2;
                        currentReadTask.sendRequestTime = SystemClock.uptimeMillis() + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                        if (!z4) {
                            MessagesController.this.readTasksMap.put(j2, currentReadTask);
                            MessagesController.this.readTasks.add(currentReadTask);
                        }
                    }
                    currentReadTask.maxDate = i;
                    currentReadTask.maxId = i11;
                    if (z4) {
                        MessagesController.this.completeReadTask(currentReadTask);
                    }
                }
            });
        }
    }

    public int createChat(String title, ArrayList<Integer> selectedContacts, String about, int type, BaseFragment fragment) {
        MessagesController messagesController = this;
        String str = title;
        ArrayList<Integer> arrayList = selectedContacts;
        int i = type;
        final BaseFragment baseFragment = fragment;
        int a = 0;
        if (i == 1) {
            TL_chat chat = new TL_chat();
            chat.id = UserConfig.getInstance(messagesController.currentAccount).lastBroadcastId;
            chat.title = str;
            chat.photo = new TL_chatPhotoEmpty();
            chat.participants_count = selectedContacts.size();
            chat.date = (int) (System.currentTimeMillis() / 1000);
            chat.version = 1;
            UserConfig instance = UserConfig.getInstance(messagesController.currentAccount);
            instance.lastBroadcastId--;
            putChat(chat, false);
            ArrayList<Chat> chatsArrays = new ArrayList();
            chatsArrays.add(chat);
            MessagesStorage.getInstance(messagesController.currentAccount).putUsersAndChats(null, chatsArrays, true, true);
            TL_chatFull chatFull = new TL_chatFull();
            chatFull.id = chat.id;
            chatFull.chat_photo = new TL_photoEmpty();
            chatFull.notify_settings = new TL_peerNotifySettingsEmpty();
            chatFull.exported_invite = new TL_chatInviteEmpty();
            chatFull.participants = new TL_chatParticipants();
            chatFull.participants.chat_id = chat.id;
            chatFull.participants.admin_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
            chatFull.participants.version = 1;
            for (int a2 = 0; a2 < selectedContacts.size(); a2++) {
                TL_chatParticipant participant = new TL_chatParticipant();
                participant.user_id = ((Integer) arrayList.get(a2)).intValue();
                participant.inviter_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                participant.date = (int) (System.currentTimeMillis() / 1000);
                chatFull.participants.participants.add(participant);
            }
            MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(chatFull, false);
            Message newMsg = new TL_messageService();
            newMsg.action = new TL_messageActionCreatedBroadcastList();
            int newMessageId = UserConfig.getInstance(messagesController.currentAccount).getNewMessageId();
            newMsg.id = newMessageId;
            newMsg.local_id = newMessageId;
            newMsg.from_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
            newMsg.dialog_id = AndroidUtilities.makeBroadcastId(chat.id);
            newMsg.to_id = new TL_peerChat();
            newMsg.to_id.chat_id = chat.id;
            newMsg.date = ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime();
            newMsg.random_id = 0;
            newMsg.flags |= 256;
            UserConfig.getInstance(messagesController.currentAccount).saveConfig(false);
            MessageObject newMsgObj = new MessageObject(messagesController.currentAccount, newMsg, messagesController.users, true);
            newMsgObj.messageOwner.send_state = 0;
            ArrayList<MessageObject> objArr = new ArrayList();
            objArr.add(newMsgObj);
            ArrayList<Message> arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance(messagesController.currentAccount).putMessages((ArrayList) arr, false, true, false, 0);
            updateInterfaceWithMessages(newMsg.dialog_id, objArr);
            NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter instance2 = NotificationCenter.getInstance(messagesController.currentAccount);
            int i2 = NotificationCenter.chatDidCreated;
            Object[] objArr2 = new Object[1];
            objArr2[0] = Integer.valueOf(chat.id);
            instance2.postNotificationName(i2, objArr2);
            return 0;
        } else if (i == 0) {
            final TL_messages_createChat req = new TL_messages_createChat();
            req.title = str;
            while (a < selectedContacts.size()) {
                User user = getUser((Integer) arrayList.get(a));
                if (user != null) {
                    req.users.add(getInputUser(user));
                }
                a++;
            }
            return ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, error, baseFragment, req, new Object[0]);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                            }
                        });
                        return;
                    }
                    final Updates updates = (Updates) response;
                    MessagesController.this.processUpdates(updates, false);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.this.putUsers(updates.users, false);
                            MessagesController.this.putChats(updates.chats, false);
                            if (updates.chats == null || updates.chats.isEmpty()) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                                return;
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
                        }
                    });
                }
            }, 2);
        } else {
            if (i != 2) {
                if (i != 4) {
                    return 0;
                }
            }
            final TL_channels_createChannel req2 = new TL_channels_createChannel();
            req2.title = str;
            req2.about = about;
            if (i == 4) {
                req2.megagroup = true;
            } else {
                req2.broadcast = true;
            }
            return ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, error, baseFragment, req2, new Object[0]);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                            }
                        });
                        return;
                    }
                    final Updates updates = (Updates) response;
                    MessagesController.this.processUpdates(updates, false);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.this.putUsers(updates.users, false);
                            MessagesController.this.putChats(updates.chats, false);
                            if (updates.chats == null || updates.chats.isEmpty()) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                                return;
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
                        }
                    });
                }
            }, 2);
        }
    }

    public void convertToMegaGroup(final Context context, int chat_id) {
        TL_messages_migrateChat req = new TL_messages_migrateChat();
        req.chat_id = chat_id;
        final AlertDialog progressDialog = new AlertDialog(context, 1);
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$89$1 */
            class C03631 implements Runnable {
                C03631() {
                }

                public void run() {
                    if (!((Activity) context).isFinishing()) {
                        try {
                            progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            }

            /* renamed from: org.telegram.messenger.MessagesController$89$2 */
            class C03642 implements Runnable {
                C03642() {
                }

                public void run() {
                    if (!((Activity) context).isFinishing()) {
                        try {
                            progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        Builder builder = new Builder(context);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        builder.show().setCanceledOnTouchOutside(true);
                    }
                }
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    AndroidUtilities.runOnUIThread(new C03631());
                    Updates updates = (Updates) response;
                    MessagesController.this.processUpdates((Updates) response, false);
                    return;
                }
                AndroidUtilities.runOnUIThread(new C03642());
            }
        });
        progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(reqId, true);
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

    public void addUsersToChannel(int chat_id, ArrayList<InputUser> users, final BaseFragment fragment) {
        if (users != null) {
            if (!users.isEmpty()) {
                final TL_channels_inviteToChannel req = new TL_channels_inviteToChannel();
                req.channel = getInputChannel(chat_id);
                req.users = users;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        if (error != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    AlertsCreator.processError(MessagesController.this.currentAccount, error, fragment, req, Boolean.valueOf(true));
                                }
                            });
                        } else {
                            MessagesController.this.processUpdates((Updates) response, false);
                        }
                    }
                });
            }
        }
    }

    public void toogleChannelInvites(int chat_id, boolean enabled) {
        TL_channels_toggleInvites req = new TL_channels_toggleInvites();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        }, 64);
    }

    public void toogleChannelSignatures(int chat_id, boolean enabled) {
        TL_channels_toggleSignatures req = new TL_channels_toggleSignatures();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$93$1 */
            class C03671 implements Runnable {
                C03671() {
                }

                public void run() {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                    AndroidUtilities.runOnUIThread(new C03671());
                }
            }
        }, 64);
    }

    public void toogleChannelInvitesHistory(int chat_id, boolean enabled) {
        TL_channels_togglePreHistoryHidden req = new TL_channels_togglePreHistoryHidden();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$94$1 */
            class C03681 implements Runnable {
                C03681() {
                }

                public void run() {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                    AndroidUtilities.runOnUIThread(new C03681());
                }
            }
        }, 64);
    }

    public void updateChannelAbout(int chat_id, final String about, final ChatFull info) {
        if (info != null) {
            TL_channels_editAbout req = new TL_channels_editAbout();
            req.channel = getInputChannel(chat_id);
            req.about = about;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController$95$1 */
                class C03691 implements Runnable {
                    C03691() {
                    }

                    public void run() {
                        info.about = about;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(info, false);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, info, Integer.valueOf(0), Boolean.valueOf(false), null);
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (response instanceof TL_boolTrue) {
                        AndroidUtilities.runOnUIThread(new C03691());
                    }
                }
            }, 64);
        }
    }

    public void updateChannelUserName(final int chat_id, final String userName) {
        TL_channels_updateUsername req = new TL_channels_updateUsername();
        req.channel = getInputChannel(chat_id);
        req.username = userName;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$96$1 */
            class C03701 implements Runnable {
                C03701() {
                }

                public void run() {
                    Chat chat = MessagesController.this.getChat(Integer.valueOf(chat_id));
                    if (userName.length() != 0) {
                        chat.flags |= 64;
                    } else {
                        chat.flags &= -65;
                    }
                    chat.username = userName;
                    ArrayList<Chat> arrayList = new ArrayList();
                    arrayList.add(chat);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(null, arrayList, true, true);
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject response, TL_error error) {
                if (response instanceof TL_boolTrue) {
                    AndroidUtilities.runOnUIThread(new C03701());
                }
            }
        }, 64);
    }

    public void sendBotStart(User user, String botHash) {
        if (user != null) {
            TL_messages_startBot req = new TL_messages_startBot();
            req.bot = getInputUser(user);
            req.peer = getInputPeer(user.id);
            req.start_param = botHash;
            req.random_id = Utilities.random.nextLong();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            });
        }
    }

    public void toggleAdminMode(final int chat_id, boolean enabled) {
        TL_messages_toggleChatAdmins req = new TL_messages_toggleChatAdmins();
        req.chat_id = chat_id;
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                    MessagesController.this.loadFullChat(chat_id, 0, true);
                }
            }
        });
    }

    public void toggleUserAdmin(int chat_id, int user_id, boolean admin) {
        TL_messages_editChatAdmin req = new TL_messages_editChatAdmin();
        req.chat_id = chat_id;
        req.user_id = getInputUser(user_id);
        req.is_admin = admin;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void addUserToChat(int chat_id, User user, ChatFull info, int count_fwd, String botHash, BaseFragment fragment) {
        MessagesController messagesController = this;
        int i = chat_id;
        User user2 = user;
        ChatFull chatFull = info;
        String str = botHash;
        if (user2 != null) {
            boolean z = false;
            if (i > 0) {
                TLObject tL_messages_startBot;
                int i2;
                final TLObject tLObject;
                final boolean z2;
                final InputUser inputUser;
                AnonymousClass100 anonymousClass100;
                AnonymousClass100 anonymousClass1002;
                final int i3;
                ConnectionsManager instance;
                final BaseFragment baseFragment;
                TLObject request;
                final boolean z3;
                boolean isChannel = ChatObject.isChannel(i, messagesController.currentAccount);
                if (isChannel && getChat(Integer.valueOf(chat_id)).megagroup) {
                    z = true;
                }
                boolean isMegagroup = z;
                InputUser inputUser2 = getInputUser(user2);
                if (str != null) {
                    if (!isChannel || isMegagroup) {
                        tL_messages_startBot = new TL_messages_startBot();
                        tL_messages_startBot.bot = inputUser2;
                        if (isChannel) {
                            tL_messages_startBot.peer = getInputPeer(-i);
                        } else {
                            tL_messages_startBot.peer = new TL_inputPeerChat();
                            tL_messages_startBot.peer.chat_id = i;
                        }
                        tL_messages_startBot.start_param = str;
                        tL_messages_startBot.random_id = Utilities.random.nextLong();
                        i2 = count_fwd;
                        tLObject = tL_messages_startBot;
                        z2 = isChannel;
                        inputUser = inputUser2;
                        anonymousClass100 = anonymousClass1002;
                        i3 = i;
                        instance = ConnectionsManager.getInstance(messagesController.currentAccount);
                        baseFragment = fragment;
                        request = tLObject;
                        z3 = isMegagroup;
                        anonymousClass1002 = new RequestDelegate() {

                            /* renamed from: org.telegram.messenger.MessagesController$100$1 */
                            class C02791 implements Runnable {
                                C02791() {
                                }

                                public void run() {
                                    MessagesController.this.joiningToChannels.remove(Integer.valueOf(i3));
                                }
                            }

                            /* renamed from: org.telegram.messenger.MessagesController$100$3 */
                            class C02813 implements Runnable {
                                C02813() {
                                }

                                public void run() {
                                    MessagesController.this.loadFullChat(i3, 0, true);
                                }
                            }

                            public void run(TLObject response, final TL_error error) {
                                if (z2 && (inputUser instanceof TL_inputUserSelf)) {
                                    AndroidUtilities.runOnUIThread(new C02791());
                                }
                                if (error != null) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            int access$000 = MessagesController.this.currentAccount;
                                            TL_error tL_error = error;
                                            BaseFragment baseFragment = baseFragment;
                                            TLObject tLObject = tLObject;
                                            boolean z = true;
                                            Object[] objArr = new Object[1];
                                            if (!z2 || z3) {
                                                z = false;
                                            }
                                            objArr[0] = Boolean.valueOf(z);
                                            AlertsCreator.processError(access$000, tL_error, baseFragment, tLObject, objArr);
                                        }
                                    });
                                    return;
                                }
                                boolean hasJoinMessage = false;
                                Updates updates = (Updates) response;
                                for (int a = 0; a < updates.updates.size(); a++) {
                                    Update update = (Update) updates.updates.get(a);
                                    if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                                        hasJoinMessage = true;
                                        break;
                                    }
                                }
                                MessagesController.this.processUpdates(updates, false);
                                if (z2) {
                                    if (!hasJoinMessage && (inputUser instanceof TL_inputUserSelf)) {
                                        MessagesController.this.generateJoinMessage(i3, true);
                                    }
                                    AndroidUtilities.runOnUIThread(new C02813(), 1000);
                                }
                                if (z2 && (inputUser instanceof TL_inputUserSelf)) {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, i3);
                                }
                            }
                        };
                        instance.sendRequest(request, anonymousClass100);
                    }
                }
                if (isChannel) {
                    if (!(inputUser2 instanceof TL_inputUserSelf)) {
                        tL_messages_startBot = new TL_channels_inviteToChannel();
                        tL_messages_startBot.channel = getInputChannel(chat_id);
                        tL_messages_startBot.users.add(inputUser2);
                    } else if (!messagesController.joiningToChannels.contains(Integer.valueOf(chat_id))) {
                        tL_messages_startBot = new TL_channels_joinChannel();
                        tL_messages_startBot.channel = getInputChannel(chat_id);
                        TLObject request2 = tL_messages_startBot;
                        messagesController.joiningToChannels.add(Integer.valueOf(chat_id));
                    } else {
                        return;
                    }
                    i2 = count_fwd;
                    tLObject = tL_messages_startBot;
                    z2 = isChannel;
                    inputUser = inputUser2;
                    anonymousClass100 = anonymousClass1002;
                    i3 = i;
                    instance = ConnectionsManager.getInstance(messagesController.currentAccount);
                    baseFragment = fragment;
                    request = tLObject;
                    z3 = isMegagroup;
                    anonymousClass1002 = /* anonymous class already generated */;
                    instance.sendRequest(request, anonymousClass100);
                } else {
                    tL_messages_startBot = new TL_messages_addChatUser();
                    tL_messages_startBot.chat_id = i;
                    tL_messages_startBot.fwd_limit = count_fwd;
                    tL_messages_startBot.user_id = inputUser2;
                    tLObject = tL_messages_startBot;
                    z2 = isChannel;
                    inputUser = inputUser2;
                    anonymousClass100 = anonymousClass1002;
                    i3 = i;
                    instance = ConnectionsManager.getInstance(messagesController.currentAccount);
                    baseFragment = fragment;
                    request = tLObject;
                    z3 = isMegagroup;
                    anonymousClass1002 = /* anonymous class already generated */;
                    instance.sendRequest(request, anonymousClass100);
                }
            } else if (chatFull instanceof TL_chatFull) {
                int a = 0;
                while (a < chatFull.participants.participants.size()) {
                    if (((ChatParticipant) chatFull.participants.participants.get(a)).user_id != user2.id) {
                        a++;
                    } else {
                        return;
                    }
                }
                Chat chat = getChat(Integer.valueOf(chat_id));
                chat.participants_count++;
                ArrayList<Chat> chatArrayList = new ArrayList();
                chatArrayList.add(chat);
                MessagesStorage.getInstance(messagesController.currentAccount).putUsersAndChats(null, chatArrayList, true, true);
                TL_chatParticipant newPart = new TL_chatParticipant();
                newPart.user_id = user2.id;
                newPart.inviter_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                newPart.date = ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime();
                chatFull.participants.participants.add(0, newPart);
                MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(chatFull, true);
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public void deleteUserFromChat(int chat_id, User user, ChatFull info) {
        deleteUserFromChat(chat_id, user, info, false);
    }

    public void deleteUserFromChat(int chat_id, User user, ChatFull info, boolean forceDelete) {
        MessagesController messagesController = this;
        int i = chat_id;
        User user2 = user;
        ChatFull chatFull = info;
        if (user2 != null) {
            if (i > 0) {
                TLObject request;
                InputUser inputUser = getInputUser(user2);
                Chat chat = getChat(Integer.valueOf(chat_id));
                boolean isChannel = ChatObject.isChannel(chat);
                if (!isChannel) {
                    request = new TL_messages_deleteChatUser();
                    request.chat_id = i;
                    request.user_id = getInputUser(user2);
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    TLObject req = new TL_channels_editBanned();
                    req.channel = getInputChannel(chat);
                    req.user_id = inputUser;
                    req.banned_rights = new TL_channelBannedRights();
                    req.banned_rights.view_messages = true;
                    req.banned_rights.send_media = true;
                    req.banned_rights.send_messages = true;
                    req.banned_rights.send_stickers = true;
                    req.banned_rights.send_gifs = true;
                    req.banned_rights.send_games = true;
                    req.banned_rights.send_inline = true;
                    req.banned_rights.embed_links = true;
                    request = req;
                } else if (chat.creator && forceDelete) {
                    request = new TL_channels_deleteChannel();
                    request.channel = getInputChannel(chat);
                } else {
                    request = new TL_channels_leaveChannel();
                    request.channel = getInputChannel(chat);
                }
                TLObject request2 = request;
                ConnectionsManager instance = ConnectionsManager.getInstance(messagesController.currentAccount);
                final User user3 = user2;
                final int i2 = i;
                final boolean z = isChannel;
                AnonymousClass101 anonymousClass101 = r0;
                final InputUser inputUser2 = inputUser;
                AnonymousClass101 anonymousClass1012 = new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$101$1 */
                    class C02821 implements Runnable {
                        C02821() {
                        }

                        public void run() {
                            MessagesController.this.deleteDialog((long) (-i2), 0);
                        }
                    }

                    /* renamed from: org.telegram.messenger.MessagesController$101$2 */
                    class C02832 implements Runnable {
                        C02832() {
                        }

                        public void run() {
                            MessagesController.this.loadFullChat(i2, 0, true);
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (user3.id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                            AndroidUtilities.runOnUIThread(new C02821());
                        }
                        if (error == null) {
                            MessagesController.this.processUpdates((Updates) response, false);
                            if (z && !(inputUser2 instanceof TL_inputUserSelf)) {
                                AndroidUtilities.runOnUIThread(new C02832(), 1000);
                            }
                        }
                    }
                };
                instance.sendRequest(request2, anonymousClass101, 64);
            } else if (chatFull instanceof TL_chatFull) {
                Chat chat2 = getChat(Integer.valueOf(chat_id));
                chat2.participants_count--;
                ArrayList<Chat> chatArrayList = new ArrayList();
                chatArrayList.add(chat2);
                MessagesStorage.getInstance(messagesController.currentAccount).putUsersAndChats(null, chatArrayList, true, true);
                boolean changed = false;
                for (int a = 0; a < chatFull.participants.participants.size(); a++) {
                    if (((ChatParticipant) chatFull.participants.participants.get(a)).user_id == user2.id) {
                        chatFull.participants.participants.remove(a);
                        changed = true;
                        break;
                    }
                }
                if (changed) {
                    MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(chatFull, true);
                    NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                }
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public void changeChatTitle(int chat_id, String title) {
        if (chat_id > 0) {
            TLObject request;
            if (ChatObject.isChannel(chat_id, this.currentAccount)) {
                request = new TL_channels_editTitle();
                request.channel = getInputChannel(chat_id);
                request.title = title;
            } else {
                request = new TL_messages_editChatTitle();
                request.chat_id = chat_id;
                request.title = title;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            }, 64);
            return;
        }
        Chat chat = getChat(Integer.valueOf(chat_id));
        chat.title = title;
        ArrayList<Chat> chatArrayList = new ArrayList();
        chatArrayList.add(chat);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, chatArrayList, true, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(16));
    }

    public void changeChatAvatar(int chat_id, InputFile uploadedAvatar) {
        TLObject request;
        if (ChatObject.isChannel(chat_id, this.currentAccount)) {
            request = new TL_channels_editPhoto();
            request.channel = getInputChannel(chat_id);
            if (uploadedAvatar != null) {
                request.photo = new TL_inputChatUploadedPhoto();
                request.photo.file = uploadedAvatar;
            } else {
                request.photo = new TL_inputChatPhotoEmpty();
            }
        } else {
            request = new TL_messages_editChatPhoto();
            request.chat_id = chat_id;
            if (uploadedAvatar != null) {
                request.photo = new TL_inputChatUploadedPhoto();
                request.photo.file = uploadedAvatar;
            } else {
                request.photo = new TL_inputChatPhotoEmpty();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        }, 64);
    }

    public void unregistedPush() {
        if (UserConfig.getInstance(this.currentAccount).registeredForPush && SharedConfig.pushString.length() == 0) {
            TL_account_unregisterDevice req = new TL_account_unregisterDevice();
            req.token = SharedConfig.pushString;
            req.token_type = 2;
            for (int a = 0; a < 3; a++) {
                UserConfig userConfig = UserConfig.getInstance(a);
                if (a != this.currentAccount && userConfig.isClientActivated()) {
                    req.other_uids.add(Integer.valueOf(userConfig.getClientUserId()));
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }

    public void performLogout(boolean byUser) {
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).putLong("lastStickersLoadTimeMask", 0).putLong("lastStickersLoadTimeFavs", 0).commit();
        this.mainPreferences.edit().remove("gifhint").commit();
        if (byUser) {
            unregistedPush();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_logOut(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).cleanup();
                }
            });
        } else {
            ConnectionsManager.getInstance(this.currentAccount).cleanup();
        }
        UserConfig.getInstance(this.currentAccount).clearConfig();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        MessagesStorage.getInstance(this.currentAccount).cleanup(false);
        cleanup();
        ContactsController.getInstance(this.currentAccount).deleteUnknownAppAccounts();
    }

    public void generateUpdateMessage() {
        if (!(BuildVars.DEBUG_VERSION || SharedConfig.lastUpdateVersion == null)) {
            if (!SharedConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING)) {
                TL_help_getAppChangelog req = new TL_help_getAppChangelog();
                req.prev_app_version = SharedConfig.lastUpdateVersion;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
                            SharedConfig.saveConfig();
                        }
                        if (response instanceof Updates) {
                            MessagesController.this.processUpdates((Updates) response, false);
                        }
                    }
                });
            }
        }
    }

    public void registerForPush(final String regid) {
        if (!(TextUtils.isEmpty(regid) || this.registeringForPush)) {
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != 0) {
                if (!UserConfig.getInstance(this.currentAccount).registeredForPush || !regid.equals(SharedConfig.pushString)) {
                    this.registeringForPush = true;
                    this.lastPushRegisterSendTime = SystemClock.uptimeMillis();
                    if (SharedConfig.pushAuthKey == null) {
                        SharedConfig.pushAuthKey = new byte[256];
                        Utilities.random.nextBytes(SharedConfig.pushAuthKey);
                        SharedConfig.saveConfig();
                    }
                    TL_account_registerDevice req = new TL_account_registerDevice();
                    req.token_type = 2;
                    req.token = regid;
                    req.secret = SharedConfig.pushAuthKey;
                    for (int a = 0; a < 3; a++) {
                        UserConfig userConfig = UserConfig.getInstance(a);
                        if (a != this.currentAccount && userConfig.isClientActivated()) {
                            req.other_uids.add(Integer.valueOf(userConfig.getClientUserId()));
                        }
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                        /* renamed from: org.telegram.messenger.MessagesController$107$1 */
                        class C02841 implements Runnable {
                            C02841() {
                            }

                            public void run() {
                                MessagesController.this.registeringForPush = false;
                            }
                        }

                        public void run(TLObject response, TL_error error) {
                            if (response instanceof TL_boolTrue) {
                                if (BuildVars.LOGS_ENABLED) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("account ");
                                    stringBuilder.append(MessagesController.this.currentAccount);
                                    stringBuilder.append(" registered for push");
                                    FileLog.m0d(stringBuilder.toString());
                                }
                                UserConfig.getInstance(MessagesController.this.currentAccount).registeredForPush = true;
                                SharedConfig.pushString = regid;
                                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                            }
                            AndroidUtilities.runOnUIThread(new C02841());
                        }
                    });
                }
            }
        }
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_updates_getState(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    int a = 0;
                    MessagesController.this.updatingState = false;
                    if (error == null) {
                        TL_updates_state res = (TL_updates_state) response;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(res.date);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(res.pts);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(res.seq);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(res.qts);
                        while (a < 3) {
                            MessagesController.this.processUpdatesQueue(a, 2);
                            a++;
                        }
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).saveDiffParams(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastQtsValue());
                    } else if (error.code != 401) {
                        MessagesController.this.loadCurrentState();
                    }
                }
            });
        }
    }

    private int getUpdateSeq(Updates updates) {
        if (updates instanceof TL_updatesCombined) {
            return updates.seq_start;
        }
        return updates.seq;
    }

    private void setUpdatesStartTime(int type, long time) {
        if (type == 0) {
            this.updatesStartWaitTimeSeq = time;
        } else if (type == 1) {
            this.updatesStartWaitTimePts = time;
        } else if (type == 2) {
            this.updatesStartWaitTimeQts = time;
        }
    }

    public long getUpdatesStartTime(int type) {
        if (type == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (type == 1) {
            return this.updatesStartWaitTimePts;
        }
        if (type == 2) {
            return this.updatesStartWaitTimeQts;
        }
        return 0;
    }

    private int isValidUpdate(Updates updates, int type) {
        if (type == 0) {
            int seq = getUpdateSeq(updates);
            if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 != seq) {
                if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() != seq) {
                    return MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() < seq ? 1 : 2;
                }
            }
            return 0;
        } else if (type == 1) {
            if (updates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastPtsValue()) {
                return 2;
            }
            return MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + updates.pts_count == updates.pts ? 0 : 1;
        } else if (type != 2) {
            return 0;
        } else {
            if (updates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastQtsValue()) {
                return 2;
            }
            return MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + updates.updates.size() == updates.pts ? 0 : 1;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processChannelsUpdatesQueue(int channelId, int state) {
        ArrayList<Updates> updatesQueue = (ArrayList) this.updatesQueueChannels.get(channelId);
        if (updatesQueue != null) {
            int channelPts = this.channelsPts.get(channelId);
            if (!updatesQueue.isEmpty()) {
                if (channelPts != 0) {
                    Collections.sort(updatesQueue, new Comparator<Updates>() {
                        public int compare(Updates updates, Updates updates2) {
                            return AndroidUtilities.compare(updates.pts, updates2.pts);
                        }
                    });
                    boolean anyProceed = false;
                    int a = 0;
                    if (state == 2) {
                        this.channelsPts.put(channelId, ((Updates) updatesQueue.get(0)).pts);
                    }
                    while (true) {
                        int a2 = a;
                        if (a2 >= updatesQueue.size()) {
                            break;
                        }
                        int updateState;
                        Updates updates = (Updates) updatesQueue.get(a2);
                        if (updates.pts <= channelPts) {
                            updateState = 2;
                        } else if (updates.pts_count + channelPts == updates.pts) {
                            updateState = 0;
                        } else {
                            updateState = 1;
                            if (updateState != 0) {
                                processUpdates(updates, true);
                                anyProceed = true;
                                updatesQueue.remove(a2);
                                a2--;
                            } else if (updateState == 1) {
                                break;
                            } else {
                                updatesQueue.remove(a2);
                                a2--;
                            }
                            a = a2 + 1;
                        }
                        if (updateState != 0) {
                            if (updateState == 1) {
                                break;
                            }
                            updatesQueue.remove(a2);
                            a2--;
                        } else {
                            processUpdates(updates, true);
                            anyProceed = true;
                            updatesQueue.remove(a2);
                            a2--;
                        }
                        a = a2 + 1;
                    }
                    this.updatesQueueChannels.remove(channelId);
                    this.updatesStartWaitTimeChannels.delete(channelId);
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("UPDATES CHANNEL ");
                        stringBuilder.append(channelId);
                        stringBuilder.append(" QUEUE PROCEED - OK");
                        FileLog.m0d(stringBuilder.toString());
                    }
                    return;
                }
            }
            this.updatesQueueChannels.remove(channelId);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processUpdatesQueue(int type, int state) {
        MessagesController messagesController = this;
        int i = type;
        ArrayList<Updates> updatesQueue = null;
        if (i == 0) {
            updatesQueue = messagesController.updatesQueueSeq;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(updates), MessagesController.this.getUpdateSeq(updates2));
                }
            });
        } else if (i == 1) {
            updatesQueue = messagesController.updatesQueuePts;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
        } else if (i == 2) {
            updatesQueue = messagesController.updatesQueueQts;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
        }
        long j = 0;
        if (updatesQueue == null || updatesQueue.isEmpty()) {
            int i2 = state;
        } else {
            boolean anyProceed = false;
            int a = 0;
            if (state == 2) {
                Updates updates = (Updates) updatesQueue.get(0);
                if (i == 0) {
                    MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(getUpdateSeq(updates));
                } else if (i == 1) {
                    MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(updates.pts);
                } else {
                    MessagesStorage.getInstance(messagesController.currentAccount).setLastQtsValue(updates.pts);
                }
            }
            while (true) {
                int a2 = a;
                if (a2 >= updatesQueue.size()) {
                    break;
                }
                Updates updates2 = (Updates) updatesQueue.get(a2);
                int updateState = isValidUpdate(updates2, i);
                if (updateState == 0) {
                    processUpdates(updates2, true);
                    anyProceed = true;
                    updatesQueue.remove(a2);
                    a2--;
                } else if (updateState == 1) {
                    break;
                } else {
                    updatesQueue.remove(a2);
                    a2--;
                }
                a = a2 + 1;
                j = 0;
            }
            if (getUpdatesStartTime(type) == j || (!anyProceed && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(type)) > 1500)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("HOLE IN UPDATES QUEUE - getDifference");
                }
                setUpdatesStartTime(i, 0);
                updatesQueue.clear();
                getDifference();
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("HOLE IN UPDATES QUEUE - will wait more time");
            }
            if (anyProceed) {
                setUpdatesStartTime(i, System.currentTimeMillis());
            }
            return;
        }
        setUpdatesStartTime(i, 0);
    }

    protected void loadUnknownChannel(final Chat channel, long taskId) {
        if (channel instanceof TL_channel) {
            if (this.gettingUnknownChannels.indexOfKey(channel.id) < 0) {
                if (channel.access_hash == 0) {
                    if (taskId != 0) {
                        MessagesStorage.getInstance(this.currentAccount).removePendingTask(taskId);
                    }
                    return;
                }
                long data;
                TL_inputPeerChannel inputPeer = new TL_inputPeerChannel();
                inputPeer.channel_id = channel.id;
                inputPeer.access_hash = channel.access_hash;
                this.gettingUnknownChannels.put(channel.id, true);
                TL_messages_getPeerDialogs req = new TL_messages_getPeerDialogs();
                TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
                inputDialogPeer.peer = inputPeer;
                req.peers.add(inputDialogPeer);
                if (taskId == 0) {
                    data = null;
                    try {
                        data = new NativeByteBuffer(4 + channel.getObjectSize());
                        data.writeInt32(0);
                        channel.serializeToStream(data);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    data = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    data = taskId;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (response != null) {
                            TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                            if (!(res.dialogs.isEmpty() || res.chats.isEmpty())) {
                                messages_Dialogs dialogs = new TL_messages_dialogs();
                                dialogs.dialogs.addAll(res.dialogs);
                                dialogs.messages.addAll(res.messages);
                                dialogs.users.addAll(res.users);
                                dialogs.chats.addAll(res.chats);
                                MessagesController.this.processLoadedDialogs(dialogs, null, 0, 1, 2, false, false, false);
                            }
                        }
                        if (data != 0) {
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(data);
                        }
                        MessagesController.this.gettingUnknownChannels.delete(channel.id);
                    }
                });
            }
        }
    }

    public void startShortPoll(final int channelId, final boolean stop) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (stop) {
                    MessagesController.this.needShortPollChannels.delete(channelId);
                    return;
                }
                MessagesController.this.needShortPollChannels.put(channelId, 0);
                if (MessagesController.this.shortPollChannels.indexOfKey(channelId) < 0) {
                    MessagesController.this.getChannelDifference(channelId, 3, 0, null);
                }
            }
        });
    }

    private void getChannelDifference(int channelId) {
        getChannelDifference(channelId, 0, 0, null);
    }

    public static boolean isSupportId(int id) {
        if (!(id / 1000 == 777 || id == 333000 || id == 4240000 || id == 4240000 || id == 4244000 || id == 4245000 || id == 4246000 || id == 410000 || id == 420000 || id == 431000 || id == 431415000 || id == 434000 || id == 4243000 || id == 439000 || id == 449000 || id == 450000 || id == 452000 || id == 454000 || id == 4254000 || id == 455000 || id == 460000 || id == 470000 || id == 479000 || id == 796000 || id == 482000 || id == 490000 || id == 496000 || id == 497000 || id == 498000)) {
            if (id != 4298000) {
                return false;
            }
        }
        return true;
    }

    protected void getChannelDifference(int channelId, int newDialogType, long taskId, InputChannel inputChannel) {
        int i = channelId;
        int i2 = newDialogType;
        long j = taskId;
        boolean gettingDifferenceChannel = this.gettingDifferenceChannels.get(i);
        if (!gettingDifferenceChannel) {
            int channelPts;
            InputChannel inputChannel2;
            int limit = 100;
            if (i2 != 1) {
                channelPts = r7.channelsPts.get(i);
                if (channelPts == 0) {
                    channelPts = MessagesStorage.getInstance(r7.currentAccount).getChannelPtsSync(i);
                    if (channelPts != 0) {
                        r7.channelsPts.put(i, channelPts);
                    }
                    if (channelPts == 0 && (i2 == 2 || i2 == 3)) {
                        return;
                    }
                }
                if (channelPts == 0) {
                    return;
                }
            } else if (r7.channelsPts.get(i) == 0) {
                channelPts = 1;
                limit = 1;
            } else {
                return;
            }
            int limit2 = limit;
            int channelPts2 = channelPts;
            if (inputChannel == null) {
                Chat chat = getChat(Integer.valueOf(channelId));
                if (chat == null) {
                    chat = MessagesStorage.getInstance(r7.currentAccount).getChatSync(i);
                    if (chat != null) {
                        putChat(chat, true);
                    }
                }
                inputChannel2 = getInputChannel(chat);
            } else {
                inputChannel2 = inputChannel;
            }
            int i3;
            if (inputChannel2 == null) {
                i3 = limit2;
            } else if (inputChannel2.access_hash == 0) {
                boolean z = gettingDifferenceChannel;
                i3 = limit2;
            } else {
                long newTaskId;
                if (j == 0) {
                    long newTaskId2;
                    NativeByteBuffer data = null;
                    try {
                        newTaskId2 = new NativeByteBuffer(12 + inputChannel2.getObjectSize());
                        newTaskId2.writeInt32(6);
                        newTaskId2.writeInt32(i);
                        newTaskId2.writeInt32(i2);
                        inputChannel2.serializeToStream(newTaskId2);
                    } catch (Throwable e) {
                        NativeByteBuffer data2 = data;
                        FileLog.m3e(e);
                        newTaskId2 = data2;
                    }
                    newTaskId = MessagesStorage.getInstance(r7.currentAccount).createPendingTask(newTaskId2);
                } else {
                    newTaskId = j;
                }
                boolean z2 = true;
                r7.gettingDifferenceChannels.put(i, true);
                TL_updates_getChannelDifference req = new TL_updates_getChannelDifference();
                req.channel = inputChannel2;
                req.filter = new TL_channelMessagesFilterEmpty();
                req.pts = channelPts2;
                req.limit = limit2;
                if (i2 == 3) {
                    z2 = false;
                }
                req.force = z2;
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("start getChannelDifference with pts = ");
                    stringBuilder.append(channelPts2);
                    stringBuilder.append(" channelId = ");
                    stringBuilder.append(i);
                    FileLog.m0d(stringBuilder.toString());
                }
                AnonymousClass115 anonymousClass115 = r1;
                ConnectionsManager instance = ConnectionsManager.getInstance(r7.currentAccount);
                final int i4 = i;
                TL_updates_getChannelDifference req2 = req;
                req = i2;
                AnonymousClass115 anonymousClass1152 = new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        if (error == null) {
                            final updates_ChannelDifference res = (updates_ChannelDifference) response;
                            SparseArray<User> usersDict = new SparseArray();
                            int a = 0;
                            for (int a2 = 0; a2 < res.users.size(); a2++) {
                                User user = (User) res.users.get(a2);
                                usersDict.put(user.id, user);
                            }
                            Chat channel = null;
                            for (int a3 = 0; a3 < res.chats.size(); a3++) {
                                Chat chat = (Chat) res.chats.get(a3);
                                if (chat.id == i4) {
                                    channel = chat;
                                    break;
                                }
                            }
                            final Chat channelFinal = channel;
                            ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
                            if (!res.other_updates.isEmpty()) {
                                while (a < res.other_updates.size()) {
                                    Update upd = (Update) res.other_updates.get(a);
                                    if (upd instanceof TL_updateMessageID) {
                                        msgUpdates.add((TL_updateMessageID) upd);
                                        res.other_updates.remove(a);
                                        a--;
                                    }
                                    a++;
                                }
                            }
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.putUsers(res.users, false);
                                    MessagesController.this.putChats(res.chats, false);
                                }
                            });
                            final ArrayList<TL_updateMessageID> arrayList = msgUpdates;
                            final updates_ChannelDifference updates_channeldifference = res;
                            final SparseArray<User> sparseArray = usersDict;
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.MessagesController$115$2$2 */
                                class C02902 implements Runnable {
                                    C02902() {
                                    }

                                    public void run() {
                                        int i = Integer.MIN_VALUE;
                                        if (!(updates_channeldifference instanceof TL_updates_channelDifference)) {
                                            if (!(updates_channeldifference instanceof TL_updates_channelDifferenceEmpty)) {
                                                if (updates_channeldifference instanceof TL_updates_channelDifferenceTooLong) {
                                                    long dialog_id = (long) (-i4);
                                                    Integer inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                    if (inboxValue == null) {
                                                        inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, dialog_id));
                                                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), inboxValue);
                                                    }
                                                    Integer outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                                                    if (outboxValue == null) {
                                                        outboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, dialog_id));
                                                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), outboxValue);
                                                    }
                                                    for (int a = 0; a < updates_channeldifference.messages.size(); a++) {
                                                        boolean z;
                                                        Message message = (Message) updates_channeldifference.messages.get(a);
                                                        message.dialog_id = (long) (-i4);
                                                        if (!(message.action instanceof TL_messageActionChannelCreate) && (channelFinal == null || !channelFinal.left)) {
                                                            if ((message.out ? outboxValue : inboxValue).intValue() < message.id) {
                                                                z = true;
                                                                message.unread = z;
                                                                if (channelFinal != null && channelFinal.megagroup) {
                                                                    message.flags |= Integer.MIN_VALUE;
                                                                }
                                                            }
                                                        }
                                                        z = false;
                                                        message.unread = z;
                                                        message.flags |= Integer.MIN_VALUE;
                                                    }
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).overwriteChannel(i4, (TL_updates_channelDifferenceTooLong) updates_channeldifference, req);
                                                }
                                                MessagesController.this.gettingDifferenceChannels.delete(i4);
                                                MessagesController.this.channelsPts.put(i4, updates_channeldifference.pts);
                                                if ((updates_channeldifference.flags & 2) != 0) {
                                                    MessagesController.this.shortPollChannels.put(i4, ((int) (System.currentTimeMillis() / 1000)) + updates_channeldifference.timeout);
                                                }
                                                if (!updates_channeldifference.isFinal) {
                                                    MessagesController.this.getChannelDifference(i4);
                                                }
                                                if (BuildVars.LOGS_ENABLED) {
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    stringBuilder.append("received channel difference with pts = ");
                                                    stringBuilder.append(updates_channeldifference.pts);
                                                    stringBuilder.append(" channelId = ");
                                                    stringBuilder.append(i4);
                                                    FileLog.m0d(stringBuilder.toString());
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("new_messages = ");
                                                    stringBuilder.append(updates_channeldifference.new_messages.size());
                                                    stringBuilder.append(" messages = ");
                                                    stringBuilder.append(updates_channeldifference.messages.size());
                                                    stringBuilder.append(" users = ");
                                                    stringBuilder.append(updates_channeldifference.users.size());
                                                    stringBuilder.append(" chats = ");
                                                    stringBuilder.append(updates_channeldifference.chats.size());
                                                    stringBuilder.append(" other updates = ");
                                                    stringBuilder.append(updates_channeldifference.other_updates.size());
                                                    FileLog.m0d(stringBuilder.toString());
                                                }
                                                if (newTaskId != 0) {
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                                }
                                            }
                                        }
                                        if (!updates_channeldifference.new_messages.isEmpty()) {
                                            final LongSparseArray<ArrayList<MessageObject>> messages = new LongSparseArray();
                                            ImageLoader.saveMessagesThumbs(updates_channeldifference.new_messages);
                                            final ArrayList<MessageObject> pushMessages = new ArrayList();
                                            long dialog_id2 = (long) (-i4);
                                            Integer inboxValue2 = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id2));
                                            if (inboxValue2 == null) {
                                                inboxValue2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, dialog_id2));
                                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id2), inboxValue2);
                                            }
                                            Integer outboxValue2 = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id2));
                                            if (outboxValue2 == null) {
                                                outboxValue2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, dialog_id2));
                                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id2), outboxValue2);
                                            }
                                            int a2 = 0;
                                            while (a2 < updates_channeldifference.new_messages.size()) {
                                                boolean z2;
                                                MessageObject obj;
                                                long uid;
                                                ArrayList<MessageObject> arr;
                                                Message message2 = (Message) updates_channeldifference.new_messages.get(a2);
                                                if (channelFinal == null || !channelFinal.left) {
                                                    if ((message2.out ? outboxValue2 : inboxValue2).intValue() < message2.id && !(message2.action instanceof TL_messageActionChannelCreate)) {
                                                        z2 = true;
                                                        message2.unread = z2;
                                                        if (channelFinal != null && channelFinal.megagroup) {
                                                            message2.flags |= i;
                                                        }
                                                        obj = new MessageObject(MessagesController.this.currentAccount, message2, sparseArray, MessagesController.this.createdDialogIds.contains(Long.valueOf(dialog_id2)));
                                                        if (!obj.isOut() && obj.isUnread()) {
                                                            pushMessages.add(obj);
                                                        }
                                                        uid = (long) (-i4);
                                                        arr = (ArrayList) messages.get(uid);
                                                        if (arr == null) {
                                                            arr = new ArrayList();
                                                            messages.put(uid, arr);
                                                        }
                                                        arr.add(obj);
                                                        a2++;
                                                        i = Integer.MIN_VALUE;
                                                    }
                                                }
                                                z2 = false;
                                                message2.unread = z2;
                                                message2.flags |= i;
                                                obj = new MessageObject(MessagesController.this.currentAccount, message2, sparseArray, MessagesController.this.createdDialogIds.contains(Long.valueOf(dialog_id2)));
                                                pushMessages.add(obj);
                                                uid = (long) (-i4);
                                                arr = (ArrayList) messages.get(uid);
                                                if (arr == null) {
                                                    arr = new ArrayList();
                                                    messages.put(uid, arr);
                                                }
                                                arr.add(obj);
                                                a2++;
                                                i = Integer.MIN_VALUE;
                                            }
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    for (int a = 0; a < messages.size(); a++) {
                                                        MessagesController.this.updateInterfaceWithMessages(messages.keyAt(a), (ArrayList) messages.valueAt(a));
                                                    }
                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                                }
                                            });
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                                /* renamed from: org.telegram.messenger.MessagesController$115$2$2$2$1 */
                                                class C02881 implements Runnable {
                                                    C02881() {
                                                    }

                                                    public void run() {
                                                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(pushMessages, true, false);
                                                    }
                                                }

                                                public void run() {
                                                    if (!pushMessages.isEmpty()) {
                                                        AndroidUtilities.runOnUIThread(new C02881());
                                                    }
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(updates_channeldifference.new_messages, true, false, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                                }
                                            });
                                        }
                                        if (!updates_channeldifference.other_updates.isEmpty()) {
                                            MessagesController.this.processUpdateArray(updates_channeldifference.other_updates, updates_channeldifference.users, updates_channeldifference.chats, true);
                                        }
                                        MessagesController.this.processChannelsUpdatesQueue(i4, 1);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).saveChannelPts(i4, updates_channeldifference.pts);
                                        MessagesController.this.gettingDifferenceChannels.delete(i4);
                                        MessagesController.this.channelsPts.put(i4, updates_channeldifference.pts);
                                        if ((updates_channeldifference.flags & 2) != 0) {
                                            MessagesController.this.shortPollChannels.put(i4, ((int) (System.currentTimeMillis() / 1000)) + updates_channeldifference.timeout);
                                        }
                                        if (updates_channeldifference.isFinal) {
                                            MessagesController.this.getChannelDifference(i4);
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("received channel difference with pts = ");
                                            stringBuilder2.append(updates_channeldifference.pts);
                                            stringBuilder2.append(" channelId = ");
                                            stringBuilder2.append(i4);
                                            FileLog.m0d(stringBuilder2.toString());
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("new_messages = ");
                                            stringBuilder2.append(updates_channeldifference.new_messages.size());
                                            stringBuilder2.append(" messages = ");
                                            stringBuilder2.append(updates_channeldifference.messages.size());
                                            stringBuilder2.append(" users = ");
                                            stringBuilder2.append(updates_channeldifference.users.size());
                                            stringBuilder2.append(" chats = ");
                                            stringBuilder2.append(updates_channeldifference.chats.size());
                                            stringBuilder2.append(" other updates = ");
                                            stringBuilder2.append(updates_channeldifference.other_updates.size());
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                        if (newTaskId != 0) {
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                        }
                                    }
                                }

                                public void run() {
                                    if (!arrayList.isEmpty()) {
                                        final SparseArray<long[]> corrected = new SparseArray();
                                        Iterator it = arrayList.iterator();
                                        while (it.hasNext()) {
                                            TL_updateMessageID update = (TL_updateMessageID) it.next();
                                            long[] ids = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(update.random_id, null, update.id, 0, false, i4);
                                            if (ids != null) {
                                                corrected.put(update.id, ids);
                                            }
                                        }
                                        if (corrected.size() != 0) {
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    for (int a = 0; a < corrected.size(); a++) {
                                                        int newId = corrected.keyAt(a);
                                                        SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage((int) ((long[]) corrected.valueAt(a))[1]);
                                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newId), null, Long.valueOf(ids[0]));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    Utilities.stageQueue.postRunnable(new C02902());
                                }
                            });
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.checkChannelError(error.text, i4);
                            }
                        });
                        MessagesController.this.gettingDifferenceChannels.delete(i4);
                        if (newTaskId != 0) {
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                        }
                    }
                };
                instance.sendRequest(req2, anonymousClass115);
                return;
            }
            if (j != 0) {
                MessagesStorage.getInstance(r7.currentAccount).removePendingTask(j);
            }
        }
    }

    private void checkChannelError(String text, int channelId) {
        int hashCode = text.hashCode();
        if (hashCode != -NUM) {
            if (hashCode != -795226617) {
                if (hashCode == -471086771) {
                    if (text.equals("CHANNEL_PUBLIC_GROUP_NA")) {
                        hashCode = 1;
                        switch (hashCode) {
                            case 0:
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(0));
                                return;
                            case 1:
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(1));
                                return;
                            case 2:
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(2));
                                return;
                            default:
                                return;
                        }
                    }
                }
            } else if (text.equals("CHANNEL_PRIVATE")) {
                hashCode = 0;
                switch (hashCode) {
                    case 0:
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(0));
                        return;
                    case 1:
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(1));
                        return;
                    case 2:
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(2));
                        return;
                    default:
                        return;
                }
            }
        } else if (text.equals("USER_BANNED_IN_CHANNEL")) {
            hashCode = 2;
            switch (hashCode) {
                case 0:
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(0));
                    return;
                case 1:
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(1));
                    return;
                case 2:
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(2));
                    return;
                default:
                    return;
            }
        }
        hashCode = -1;
        switch (hashCode) {
            case 0:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(0));
                return;
            case 1:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(1));
                return;
            case 2:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(2));
                return;
            default:
                return;
        }
    }

    public void getDifference() {
        getDifference(MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue(), false);
    }

    public void getDifference(int pts, final int date, final int qts, boolean slice) {
        registerForPush(SharedConfig.pushString);
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() == 0) {
            loadCurrentState();
        } else if (slice || !this.gettingDifference) {
            this.gettingDifference = true;
            TL_updates_getDifference req = new TL_updates_getDifference();
            req.pts = pts;
            req.date = date;
            req.qts = qts;
            if (this.getDifferenceFirstSync) {
                req.flags |= 1;
                if (ConnectionsManager.isConnectedOrConnectingToWiFi()) {
                    req.pts_total_limit = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
                } else {
                    req.pts_total_limit = 1000;
                }
                this.getDifferenceFirstSync = false;
            }
            if (req.date == 0) {
                req.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("start getDifference with date = ");
                stringBuilder.append(date);
                stringBuilder.append(" pts = ");
                stringBuilder.append(pts);
                stringBuilder.append(" qts = ");
                stringBuilder.append(qts);
                FileLog.m0d(stringBuilder.toString());
            }
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    int a = 0;
                    if (error == null) {
                        final updates_Difference res = (updates_Difference) response;
                        if (res instanceof TL_updates_differenceTooLong) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.loadedFullUsers.clear();
                                    MessagesController.this.loadedFullChats.clear();
                                    MessagesController.this.resetDialogs(true, MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), res.pts, date, qts);
                                }
                            });
                        } else {
                            int a2;
                            if (res instanceof TL_updates_differenceSlice) {
                                MessagesController.this.getDifference(res.intermediate_state.pts, res.intermediate_state.date, res.intermediate_state.qts, true);
                            }
                            SparseArray<User> usersDict = new SparseArray();
                            SparseArray<Chat> chatsDict = new SparseArray();
                            for (a2 = 0; a2 < res.users.size(); a2++) {
                                User user = (User) res.users.get(a2);
                                usersDict.put(user.id, user);
                            }
                            for (a2 = 0; a2 < res.chats.size(); a2++) {
                                Chat chat = (Chat) res.chats.get(a2);
                                chatsDict.put(chat.id, chat);
                            }
                            ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
                            if (!res.other_updates.isEmpty()) {
                                while (a < res.other_updates.size()) {
                                    Update upd = (Update) res.other_updates.get(a);
                                    if (upd instanceof TL_updateMessageID) {
                                        msgUpdates.add((TL_updateMessageID) upd);
                                        res.other_updates.remove(a);
                                        a--;
                                    } else if (MessagesController.this.getUpdateType(upd) == 2) {
                                        int channelId = MessagesController.getUpdateChannelId(upd);
                                        int channelPts = MessagesController.this.channelsPts.get(channelId);
                                        if (channelPts == 0) {
                                            channelPts = MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(channelId);
                                            if (channelPts != 0) {
                                                MessagesController.this.channelsPts.put(channelId, channelPts);
                                            }
                                        }
                                        if (channelPts != 0 && MessagesController.getUpdatePts(upd) <= channelPts) {
                                            res.other_updates.remove(a);
                                            a--;
                                        }
                                    }
                                    a++;
                                }
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.loadedFullUsers.clear();
                                    MessagesController.this.loadedFullChats.clear();
                                    MessagesController.this.putUsers(res.users, false);
                                    MessagesController.this.putChats(res.chats, false);
                                }
                            });
                            final updates_Difference updates_difference = res;
                            final ArrayList<TL_updateMessageID> arrayList = msgUpdates;
                            final SparseArray<User> sparseArray = usersDict;
                            final SparseArray<Chat> sparseArray2 = chatsDict;
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.MessagesController$116$3$2 */
                                class C02992 implements Runnable {
                                    C02992() {
                                    }

                                    public void run() {
                                        boolean z = true;
                                        boolean z2 = false;
                                        if (!(updates_difference.new_messages.isEmpty() && updates_difference.new_encrypted_messages.isEmpty())) {
                                            final LongSparseArray<ArrayList<MessageObject>> messages = new LongSparseArray();
                                            for (int b = 0; b < updates_difference.new_encrypted_messages.size(); b++) {
                                                ArrayList<Message> decryptedMessages = SecretChatHelper.getInstance(MessagesController.this.currentAccount).decryptMessage((EncryptedMessage) updates_difference.new_encrypted_messages.get(b));
                                                if (!(decryptedMessages == null || decryptedMessages.isEmpty())) {
                                                    updates_difference.new_messages.addAll(decryptedMessages);
                                                }
                                            }
                                            ImageLoader.saveMessagesThumbs(updates_difference.new_messages);
                                            final ArrayList<MessageObject> pushMessages = new ArrayList();
                                            int clientUserId = UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId();
                                            int a = 0;
                                            while (a < updates_difference.new_messages.size()) {
                                                Message message = (Message) updates_difference.new_messages.get(a);
                                                if (message.dialog_id == 0) {
                                                    if (message.to_id.chat_id != 0) {
                                                        message.dialog_id = (long) (-message.to_id.chat_id);
                                                    } else {
                                                        if (message.to_id.user_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                                                            message.to_id.user_id = message.from_id;
                                                        }
                                                        message.dialog_id = (long) message.to_id.user_id;
                                                    }
                                                }
                                                if (((int) message.dialog_id) != 0) {
                                                    if (message.action instanceof TL_messageActionChatDeleteUser) {
                                                        User user = (User) sparseArray.get(message.action.user_id);
                                                        if (user != null && user.bot) {
                                                            message.reply_markup = new TL_replyKeyboardHide();
                                                            message.flags |= 64;
                                                        }
                                                    }
                                                    if (!(message.action instanceof TL_messageActionChatMigrateTo)) {
                                                        if (!(message.action instanceof TL_messageActionChannelCreate)) {
                                                            ConcurrentHashMap<Long, Integer> read_max = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                                                            Integer value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                                                            if (value == null) {
                                                                value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                                                                read_max.put(Long.valueOf(message.dialog_id), value);
                                                            }
                                                            message.unread = value.intValue() < message.id ? z : z2;
                                                        }
                                                    }
                                                    message.unread = z2;
                                                    message.media_unread = z2;
                                                }
                                                if (message.dialog_id == ((long) clientUserId)) {
                                                    message.unread = z2;
                                                    message.media_unread = z2;
                                                    message.out = z;
                                                }
                                                MessageObject obj = new MessageObject(MessagesController.this.currentAccount, message, sparseArray, sparseArray2, MessagesController.this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                                                if (!obj.isOut() && obj.isUnread()) {
                                                    pushMessages.add(obj);
                                                }
                                                ArrayList<MessageObject> arr = (ArrayList) messages.get(message.dialog_id);
                                                if (arr == null) {
                                                    arr = new ArrayList();
                                                    messages.put(message.dialog_id, arr);
                                                }
                                                arr.add(obj);
                                                a++;
                                                z = true;
                                                z2 = false;
                                            }
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    for (int a = 0; a < messages.size(); a++) {
                                                        MessagesController.this.updateInterfaceWithMessages(messages.keyAt(a), (ArrayList) messages.valueAt(a));
                                                    }
                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                                }
                                            });
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                                /* renamed from: org.telegram.messenger.MessagesController$116$3$2$2$1 */
                                                class C02971 implements Runnable {
                                                    C02971() {
                                                    }

                                                    public void run() {
                                                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(pushMessages, !(updates_difference instanceof TL_updates_differenceSlice), false);
                                                    }
                                                }

                                                public void run() {
                                                    if (!pushMessages.isEmpty()) {
                                                        AndroidUtilities.runOnUIThread(new C02971());
                                                    }
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(updates_difference.new_messages, true, false, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                                }
                                            });
                                            SecretChatHelper.getInstance(MessagesController.this.currentAccount).processPendingEncMessages();
                                        }
                                        if (!updates_difference.other_updates.isEmpty()) {
                                            MessagesController.this.processUpdateArray(updates_difference.other_updates, updates_difference.users, updates_difference.chats, true);
                                        }
                                        int a2;
                                        if (updates_difference instanceof TL_updates_difference) {
                                            MessagesController.this.gettingDifference = false;
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(updates_difference.state.seq);
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(updates_difference.state.date);
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(updates_difference.state.pts);
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(updates_difference.state.qts);
                                            ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                                            int a3 = 0;
                                            while (true) {
                                                a2 = a3;
                                                if (a2 >= 3) {
                                                    break;
                                                }
                                                MessagesController.this.processUpdatesQueue(a2, 1);
                                                a3 = a2 + 1;
                                            }
                                        } else if (updates_difference instanceof TL_updates_differenceSlice) {
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(updates_difference.intermediate_state.date);
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(updates_difference.intermediate_state.pts);
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(updates_difference.intermediate_state.qts);
                                        } else if (updates_difference instanceof TL_updates_differenceEmpty) {
                                            MessagesController.this.gettingDifference = false;
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(updates_difference.seq);
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(updates_difference.date);
                                            int a4 = 0;
                                            ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                                            while (true) {
                                                a2 = a4;
                                                if (a2 >= 3) {
                                                    break;
                                                }
                                                MessagesController.this.processUpdatesQueue(a2, 1);
                                                a4 = a2 + 1;
                                            }
                                        }
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).saveDiffParams(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastQtsValue());
                                        if (BuildVars.LOGS_ENABLED) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append("received difference with date = ");
                                            stringBuilder.append(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue());
                                            stringBuilder.append(" pts = ");
                                            stringBuilder.append(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue());
                                            stringBuilder.append(" seq = ");
                                            stringBuilder.append(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue());
                                            stringBuilder.append(" messages = ");
                                            stringBuilder.append(updates_difference.new_messages.size());
                                            stringBuilder.append(" users = ");
                                            stringBuilder.append(updates_difference.users.size());
                                            stringBuilder.append(" chats = ");
                                            stringBuilder.append(updates_difference.chats.size());
                                            stringBuilder.append(" other updates = ");
                                            stringBuilder.append(updates_difference.other_updates.size());
                                            FileLog.m0d(stringBuilder.toString());
                                        }
                                    }
                                }

                                public void run() {
                                    int a = 0;
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(updates_difference.users, updates_difference.chats, true, false);
                                    if (!arrayList.isEmpty()) {
                                        final SparseArray<long[]> corrected = new SparseArray();
                                        while (true) {
                                            int a2 = a;
                                            if (a2 >= arrayList.size()) {
                                                break;
                                            }
                                            TL_updateMessageID update = (TL_updateMessageID) arrayList.get(a2);
                                            long[] ids = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(update.random_id, null, update.id, 0, false, 0);
                                            if (ids != null) {
                                                corrected.put(update.id, ids);
                                            }
                                            a = a2 + 1;
                                        }
                                        if (corrected.size() != 0) {
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    for (int a = 0; a < corrected.size(); a++) {
                                                        int newId = corrected.keyAt(a);
                                                        SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage((int) ((long[]) corrected.valueAt(a))[1]);
                                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newId), null, Long.valueOf(ids[0]));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    Utilities.stageQueue.postRunnable(new C02992());
                                }
                            });
                        }
                        return;
                    }
                    MessagesController.this.gettingDifference = false;
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                }
            });
        }
    }

    public boolean canPinDialog(boolean secret) {
        int count = 0;
        for (int a = 0; a < this.dialogs.size(); a++) {
            TL_dialog dialog = (TL_dialog) this.dialogs.get(a);
            int lower_id = (int) dialog.id;
            if (!secret || lower_id == 0) {
                if (secret || lower_id != 0) {
                    if (dialog.pinned) {
                        count++;
                    }
                }
            }
        }
        if (count < this.maxPinnedDialogsCount) {
            return true;
        }
        return false;
    }

    public boolean pinDialog(long did, boolean pin, InputPeer peer, long taskId) {
        int lower_id = (int) did;
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
        boolean z = false;
        if (dialog != null) {
            if (dialog.pinned != pin) {
                dialog.pinned = pin;
                if (pin) {
                    int maxPinnedNum = 0;
                    for (int a = 0; a < this.dialogs.size(); a++) {
                        TL_dialog d = (TL_dialog) this.dialogs.get(a);
                        if (!d.pinned) {
                            break;
                        }
                        maxPinnedNum = Math.max(d.pinnedNum, maxPinnedNum);
                    }
                    dialog.pinnedNum = maxPinnedNum + 1;
                } else {
                    dialog.pinnedNum = 0;
                }
                NativeByteBuffer data = null;
                sortDialogs(null);
                if (!pin && this.dialogs.get(this.dialogs.size() - 1) == dialog) {
                    this.dialogs.remove(this.dialogs.size() - 1);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (!(lower_id == 0 || taskId == -1)) {
                    TL_messages_toggleDialogPin req = new TL_messages_toggleDialogPin();
                    req.pinned = pin;
                    if (peer == null) {
                        peer = getInputPeer(lower_id);
                    }
                    if (peer instanceof TL_inputPeerEmpty) {
                        return false;
                    }
                    long newTaskId;
                    TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
                    inputDialogPeer.peer = peer;
                    req.peer = inputDialogPeer;
                    if (taskId == 0) {
                        try {
                            data = new NativeByteBuffer(16 + peer.getObjectSize());
                            data.writeInt32(1);
                            data.writeInt64(did);
                            data.writeBool(pin);
                            peer.serializeToStream(data);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                    } else {
                        newTaskId = taskId;
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (newTaskId != 0) {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                            }
                        }
                    });
                }
                MessagesStorage.getInstance(this.currentAccount).setDialogPinned(did, dialog.pinnedNum);
                return true;
            }
        }
        if (dialog != null) {
            z = true;
        }
        return z;
    }

    public void loadPinnedDialogs(final long newDialogId, final ArrayList<Long> order) {
        if (!UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    AnonymousClass118 anonymousClass118 = this;
                    if (response != null) {
                        int a;
                        Chat chat;
                        MessageObject messageObject;
                        TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                        TL_messages_dialogs toCache = new TL_messages_dialogs();
                        toCache.users.addAll(res.users);
                        toCache.chats.addAll(res.chats);
                        toCache.dialogs.addAll(res.dialogs);
                        toCache.messages.addAll(res.messages);
                        LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
                        SparseArray<User> usersDict = new SparseArray();
                        SparseArray<Chat> chatsDict = new SparseArray();
                        final ArrayList<Long> arrayList = new ArrayList();
                        for (a = 0; a < res.users.size(); a++) {
                            User u = (User) res.users.get(a);
                            usersDict.put(u.id, u);
                        }
                        for (a = 0; a < res.chats.size(); a++) {
                            Chat c = (Chat) res.chats.get(a);
                            chatsDict.put(c.id, c);
                        }
                        for (a = 0; a < res.messages.size(); a++) {
                            Message message = (Message) res.messages.get(a);
                            if (message.to_id.channel_id != 0) {
                                chat = (Chat) chatsDict.get(message.to_id.channel_id);
                                if (chat != null && chat.left) {
                                }
                            } else if (message.to_id.chat_id != 0) {
                                chat = (Chat) chatsDict.get(message.to_id.chat_id);
                                if (!(chat == null || chat.migrated_to == null)) {
                                }
                            }
                            messageObject = new MessageObject(MessagesController.this.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict, false);
                            new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                        }
                        for (a = 0; a < res.dialogs.size(); a++) {
                            TL_dialog d = (TL_dialog) res.dialogs.get(a);
                            if (d.id == 0) {
                                if (d.peer.user_id != 0) {
                                    d.id = (long) d.peer.user_id;
                                } else if (d.peer.chat_id != 0) {
                                    d.id = (long) (-d.peer.chat_id);
                                } else if (d.peer.channel_id != 0) {
                                    d.id = (long) (-d.peer.channel_id);
                                }
                            }
                            arrayList.add(Long.valueOf(d.id));
                            if (DialogObject.isChannel(d)) {
                                chat = (Chat) chatsDict.get(-((int) d.id));
                                if (chat != null && chat.left) {
                                }
                            } else if (((int) d.id) < 0) {
                                chat = (Chat) chatsDict.get(-((int) d.id));
                                if (!(chat == null || chat.migrated_to == null)) {
                                }
                            }
                            if (d.last_message_date == 0) {
                                messageObject = (MessageObject) new_dialogMessage.get(d.id);
                                if (messageObject != null) {
                                    d.last_message_date = messageObject.messageOwner.date;
                                }
                            }
                            Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                            if (value == null) {
                                value = Integer.valueOf(0);
                            }
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                            value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                            if (value == null) {
                                value = Integer.valueOf(0);
                            }
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                        }
                        final TL_messages_peerDialogs tL_messages_peerDialogs = res;
                        ArrayList<Long> newPinnedOrder = arrayList;
                        final LongSparseArray<MessageObject> longSparseArray = new_dialogMessage;
                        final TL_messages_dialogs usersDict2 = toCache;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MessagesController$118$1$1 */
                            class C03011 implements Runnable {
                                C03011() {
                                }

                                public void run() {
                                    boolean changed;
                                    int newIdx;
                                    MessagesController.this.applyDialogsNotificationsSettings(tL_messages_peerDialogs.dialogs);
                                    boolean added = false;
                                    int maxPinnedNum = 0;
                                    LongSparseArray<Integer> oldPinnedDialogNums = new LongSparseArray();
                                    ArrayList<Long> oldPinnedOrder = new ArrayList();
                                    boolean changed2 = false;
                                    for (int a = 0; a < MessagesController.this.dialogs.size(); a++) {
                                        TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs.get(a);
                                        if (((int) dialog.id) != 0) {
                                            if (!dialog.pinned) {
                                                break;
                                            }
                                            maxPinnedNum = Math.max(dialog.pinnedNum, maxPinnedNum);
                                            oldPinnedDialogNums.put(dialog.id, Integer.valueOf(dialog.pinnedNum));
                                            oldPinnedOrder.add(Long.valueOf(dialog.id));
                                            dialog.pinned = false;
                                            dialog.pinnedNum = 0;
                                            changed2 = true;
                                        }
                                    }
                                    ArrayList<Long> pinnedDialogs = new ArrayList();
                                    ArrayList<Long> orderArrayList = order != null ? order : arrayList;
                                    long j = 0;
                                    if (orderArrayList.size() < oldPinnedOrder.size()) {
                                        orderArrayList.add(Long.valueOf(0));
                                    }
                                    while (oldPinnedOrder.size() < orderArrayList.size()) {
                                        oldPinnedOrder.add(0, Long.valueOf(0));
                                    }
                                    if (tL_messages_peerDialogs.dialogs.isEmpty()) {
                                        changed = changed2;
                                    } else {
                                        MessagesController.this.putUsers(tL_messages_peerDialogs.users, false);
                                        MessagesController.this.putChats(tL_messages_peerDialogs.chats, false);
                                        boolean added2 = false;
                                        int a2 = 0;
                                        while (a2 < tL_messages_peerDialogs.dialogs.size()) {
                                            TL_dialog dialog2 = (TL_dialog) tL_messages_peerDialogs.dialogs.get(a2);
                                            if (newDialogId != j) {
                                                Integer oldNum = (Integer) oldPinnedDialogNums.get(dialog2.id);
                                                if (oldNum != null) {
                                                    dialog2.pinnedNum = oldNum.intValue();
                                                }
                                                changed = changed2;
                                            } else {
                                                int oldIdx = oldPinnedOrder.indexOf(Long.valueOf(dialog2.id));
                                                newIdx = orderArrayList.indexOf(Long.valueOf(dialog2.id));
                                                if (!(oldIdx == -1 || newIdx == -1)) {
                                                    Integer oldNum2;
                                                    if (oldIdx == newIdx) {
                                                        oldNum2 = (Integer) oldPinnedDialogNums.get(dialog2.id);
                                                        if (oldNum2 != null) {
                                                            dialog2.pinnedNum = oldNum2.intValue();
                                                        }
                                                    } else {
                                                        oldNum2 = (Integer) oldPinnedDialogNums.get(((Long) oldPinnedOrder.get(newIdx)).longValue());
                                                        if (oldNum2 != null) {
                                                            dialog2.pinnedNum = oldNum2.intValue();
                                                        }
                                                    }
                                                }
                                            }
                                            if (dialog2.pinnedNum == 0) {
                                                dialog2.pinnedNum = (tL_messages_peerDialogs.dialogs.size() - a2) + maxPinnedNum;
                                            }
                                            pinnedDialogs.add(Long.valueOf(dialog2.id));
                                            TL_dialog d = (TL_dialog) MessagesController.this.dialogs_dict.get(dialog2.id);
                                            if (d != null) {
                                                d.pinned = true;
                                                d.pinnedNum = dialog2.pinnedNum;
                                                MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogPinned(dialog2.id, dialog2.pinnedNum);
                                            } else {
                                                added2 = true;
                                                MessagesController.this.dialogs_dict.put(dialog2.id, dialog2);
                                                MessageObject messageObject = (MessageObject) longSparseArray.get(dialog2.id);
                                                MessagesController.this.dialogMessage.put(dialog2.id, messageObject);
                                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                                    MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                                    if (messageObject.messageOwner.random_id != 0) {
                                                        MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                                    }
                                                }
                                            }
                                            changed2 = true;
                                            a2++;
                                            j = 0;
                                        }
                                        changed = changed2;
                                        added = added2;
                                    }
                                    if (changed) {
                                        if (added) {
                                            MessagesController.this.dialogs.clear();
                                            int size = MessagesController.this.dialogs_dict.size();
                                            for (newIdx = 0; newIdx < size; newIdx++) {
                                                MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(newIdx));
                                            }
                                        }
                                        MessagesController.this.sortDialogs(null);
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                    }
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).unpinAllDialogsExceptNew(pinnedDialogs);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(usersDict2, true);
                                    UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = true;
                                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                                }
                            }

                            public void run() {
                                AndroidUtilities.runOnUIThread(new C03011());
                            }
                        });
                    }
                }
            });
        }
    }

    public void generateJoinMessage(final int chat_id, boolean ignoreLeft) {
        Chat chat = getChat(Integer.valueOf(chat_id));
        if (chat != null && ChatObject.isChannel(chat_id, this.currentAccount)) {
            if ((!chat.left && !chat.kicked) || ignoreLeft) {
                TL_messageService message = new TL_messageService();
                message.flags = 256;
                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                message.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                message.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                message.to_id = new TL_peerChannel();
                message.to_id.channel_id = chat_id;
                message.dialog_id = (long) (-chat_id);
                message.post = true;
                message.action = new TL_messageActionChatAddUser();
                message.action.users.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (chat.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                final ArrayList<MessageObject> pushMessages = new ArrayList();
                ArrayList<Message> messagesArr = new ArrayList();
                messagesArr.add(message);
                pushMessages.add(new MessageObject(this.currentAccount, message, true));
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.MessagesController$119$1 */
                    class C03031 implements Runnable {
                        C03031() {
                        }

                        public void run() {
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(pushMessages, true, false);
                        }
                    }

                    public void run() {
                        AndroidUtilities.runOnUIThread(new C03031());
                    }
                });
                MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList) messagesArr, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MessagesController.this.updateInterfaceWithMessages((long) (-chat_id), pushMessages);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                });
            }
        }
    }

    public void checkChannelInviter(final int chat_id) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                final Chat chat = MessagesController.this.getChat(Integer.valueOf(chat_id));
                if (chat != null && ChatObject.isChannel(chat_id, MessagesController.this.currentAccount)) {
                    if (!chat.creator) {
                        TL_channels_getParticipant req = new TL_channels_getParticipant();
                        req.channel = MessagesController.this.getInputChannel(chat_id);
                        req.user_id = new TL_inputUserSelf();
                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                final TL_channels_channelParticipant res = (TL_channels_channelParticipant) response;
                                if (res != null && (res.participant instanceof TL_channelParticipantSelf) && res.participant.inviter_id != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId() && (!chat.megagroup || !MessagesStorage.getInstance(MessagesController.this.currentAccount).isMigratedChat(chat.id))) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.this.putUsers(res.users, false);
                                        }
                                    });
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                                    Message message = new TL_messageService();
                                    message.media_unread = true;
                                    message.unread = true;
                                    message.flags = 256;
                                    message.post = true;
                                    if (chat.megagroup) {
                                        message.flags |= Integer.MIN_VALUE;
                                    }
                                    int newMessageId = UserConfig.getInstance(MessagesController.this.currentAccount).getNewMessageId();
                                    message.id = newMessageId;
                                    message.local_id = newMessageId;
                                    message.date = res.participant.date;
                                    message.action = new TL_messageActionChatAddUser();
                                    message.from_id = res.participant.inviter_id;
                                    message.action.users.add(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                                    message.to_id = new TL_peerChannel();
                                    message.to_id.channel_id = chat_id;
                                    message.dialog_id = (long) (-chat_id);
                                    int a = 0;
                                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                                    final ArrayList<MessageObject> pushMessages = new ArrayList();
                                    ArrayList<Message> messagesArr = new ArrayList();
                                    AbstractMap usersDict = new ConcurrentHashMap();
                                    while (a < res.users.size()) {
                                        User user = (User) res.users.get(a);
                                        usersDict.put(Integer.valueOf(user.id), user);
                                        a++;
                                    }
                                    messagesArr.add(message);
                                    pushMessages.add(new MessageObject(MessagesController.this.currentAccount, message, usersDict, true));
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                        /* renamed from: org.telegram.messenger.MessagesController$121$1$2$1 */
                                        class C03051 implements Runnable {
                                            C03051() {
                                            }

                                            public void run() {
                                                NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(pushMessages, true, false);
                                            }
                                        }

                                        public void run() {
                                            AndroidUtilities.runOnUIThread(new C03051());
                                        }
                                    });
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages((ArrayList) messagesArr, true, true, false, 0);
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.this.updateInterfaceWithMessages((long) (-chat_id), pushMessages);
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private int getUpdateType(Update update) {
        if (!((update instanceof TL_updateNewMessage) || (update instanceof TL_updateReadMessagesContents) || (update instanceof TL_updateReadHistoryInbox) || (update instanceof TL_updateReadHistoryOutbox) || (update instanceof TL_updateDeleteMessages) || (update instanceof TL_updateWebPage))) {
            if (!(update instanceof TL_updateEditMessage)) {
                if (update instanceof TL_updateNewEncryptedMessage) {
                    return 1;
                }
                if (!((update instanceof TL_updateNewChannelMessage) || (update instanceof TL_updateDeleteChannelMessages) || (update instanceof TL_updateEditChannelMessage))) {
                    if (!(update instanceof TL_updateChannelWebPage)) {
                        return 3;
                    }
                }
                return 2;
            }
        }
        return 0;
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
        return 0;
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
        return 0;
    }

    private static int getUpdateQts(Update update) {
        if (update instanceof TL_updateNewEncryptedMessage) {
            return ((TL_updateNewEncryptedMessage) update).qts;
        }
        return 0;
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
            FileLog.m1e(stringBuilder.toString());
        }
        return 0;
    }

    public void processUpdates(Updates updates, boolean fromQueue) {
        boolean z;
        int a;
        int channelId;
        TL_messages_receivedQueue req;
        MessagesController messagesController = this;
        final Updates updates2 = updates;
        ArrayList<Integer> needGetChannelsDiff = null;
        boolean needGetDiff = false;
        boolean needReceivedQueue = false;
        boolean updateStatus = false;
        if (updates2 instanceof TL_updateShort) {
            ArrayList<Update> arr = new ArrayList();
            arr.add(updates2.update);
            processUpdateArray(arr, null, null, false);
        } else {
            int i;
            boolean skipUpdate;
            int b;
            int pts2;
            boolean z2;
            ArrayList<Integer> needGetChannelsDiff2;
            boolean needGetDiff2;
            boolean needReceivedQueue2;
            int i2 = 1;
            if (updates2 instanceof TL_updateShortChatMessage) {
                z = false;
            } else if (updates2 instanceof TL_updateShortMessage) {
                z = false;
            } else {
                Update update;
                int channelId2;
                if (!(updates2 instanceof TL_updatesCombined)) {
                    if (!(updates2 instanceof TL_updates)) {
                        if (updates2 instanceof TL_updatesTooLong) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("need get diff TL_updatesTooLong");
                            }
                            needGetDiff = true;
                        } else if (updates2 instanceof UserActionUpdatesSeq) {
                            MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                        } else if (updates2 instanceof UserActionUpdatesPts) {
                            if (updates2.chat_id != 0) {
                                messagesController.channelsPts.put(updates2.chat_id, updates2.pts);
                                MessagesStorage.getInstance(messagesController.currentAccount).saveChannelPts(updates2.chat_id, updates2.pts);
                            } else {
                                MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(updates2.pts);
                            }
                        }
                    }
                }
                SparseArray<Chat> minChannels = null;
                for (a = 0; a < updates2.chats.size(); a++) {
                    Chat chat = (Chat) updates2.chats.get(a);
                    if ((chat instanceof TL_channel) && chat.min) {
                        Chat existChat = getChat(Integer.valueOf(chat.id));
                        if (existChat == null || existChat.min) {
                            Chat cacheChat = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(updates2.chat_id);
                            putChat(cacheChat, true);
                            existChat = cacheChat;
                        }
                        if (existChat == null || existChat.min) {
                            if (minChannels == null) {
                                minChannels = new SparseArray();
                            }
                            minChannels.put(chat.id, chat);
                        }
                    }
                }
                if (minChannels != null) {
                    for (a = 0; a < updates2.updates.size(); a++) {
                        update = (Update) updates2.updates.get(a);
                        if (update instanceof TL_updateNewChannelMessage) {
                            channelId2 = ((TL_updateNewChannelMessage) update).message.to_id.channel_id;
                            if (minChannels.indexOfKey(channelId2) >= 0) {
                                if (BuildVars.LOGS_ENABLED) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("need get diff because of min channel ");
                                    stringBuilder.append(channelId2);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                                needGetDiff = true;
                            }
                        }
                    }
                }
                if (needGetDiff) {
                    z = false;
                } else {
                    SparseArray<Chat> minChannels2;
                    boolean needGetDiff3;
                    boolean needReceivedQueue3;
                    StringBuilder stringBuilder2;
                    boolean processUpdate;
                    MessagesStorage.getInstance(messagesController.currentAccount).putUsersAndChats(updates2.users, updates2.chats, true, true);
                    Collections.sort(updates2.updates, messagesController.updatesComparator);
                    ArrayList<Integer> needGetChannelsDiff3 = null;
                    int a2 = 0;
                    while (a2 < updates2.updates.size()) {
                        update = (Update) updates2.updates.get(a2);
                        if (getUpdateType(update) != 0) {
                            minChannels2 = minChannels;
                            int channelPts;
                            if (getUpdateType(update) != 1) {
                                if (getUpdateType(update) != 2) {
                                    needGetDiff3 = needGetDiff;
                                    needReceivedQueue3 = needReceivedQueue;
                                    z = updateStatus;
                                    i = 1;
                                    break;
                                }
                                channelId = getUpdateChannelId(update);
                                skipUpdate = false;
                                channelPts = messagesController.channelsPts.get(channelId);
                                if (channelPts == 0) {
                                    channelPts = MessagesStorage.getInstance(messagesController.currentAccount).getChannelPtsSync(channelId);
                                    if (channelPts == 0) {
                                        for (i2 = 0; i2 < updates2.chats.size(); i2++) {
                                            Chat chat2 = (Chat) updates2.chats.get(i2);
                                            if (chat2.id == channelId) {
                                                loadUnknownChannel(chat2, 0);
                                                skipUpdate = true;
                                                break;
                                            }
                                        }
                                    } else {
                                        messagesController.channelsPts.put(channelId, channelPts);
                                    }
                                }
                                TL_updates updatesNew = new TL_updates();
                                updatesNew.updates.add(update);
                                updatesNew.pts = getUpdatePts(update);
                                updatesNew.pts_count = getUpdatePtsCount(update);
                                b = a2 + 1;
                                while (b < updates2.updates.size()) {
                                    Update update2 = (Update) updates2.updates.get(b);
                                    pts2 = getUpdatePts(update2);
                                    int count2 = getUpdatePtsCount(update2);
                                    needGetDiff3 = needGetDiff;
                                    if (!getUpdateType(update2) || channelId != getUpdateChannelId(update2) || updatesNew.pts + count2 != pts2) {
                                        break;
                                    }
                                    updatesNew.updates.add(update2);
                                    updatesNew.pts = pts2;
                                    updatesNew.pts_count += count2;
                                    updates2.updates.remove(b);
                                    b = (b - 1) + 1;
                                    needGetDiff = needGetDiff3;
                                }
                                needGetDiff3 = needGetDiff;
                                if (skipUpdate) {
                                    needReceivedQueue3 = needReceivedQueue;
                                    z = updateStatus;
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append("need load unknown channel = ");
                                        stringBuilder2.append(channelId);
                                        FileLog.m0d(stringBuilder2.toString());
                                    }
                                } else if (updatesNew.pts_count + channelPts == updatesNew.pts) {
                                    if (processUpdateArray(updatesNew.updates, updates2.users, updates2.chats, false)) {
                                        messagesController.channelsPts.put(channelId, updatesNew.pts);
                                        MessagesStorage.getInstance(messagesController.currentAccount).saveChannelPts(channelId, updatesNew.pts);
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("need get channel diff inner TL_updates, channel_id = ");
                                            stringBuilder2.append(channelId);
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                        if (needGetChannelsDiff3 == null) {
                                            needGetChannelsDiff3 = new ArrayList();
                                            z = updateStatus;
                                            needGetDiff = needGetDiff3;
                                            updates2.updates.remove(a2);
                                            a2 = (a2 - 1) + 1;
                                            i2 = 1;
                                            minChannels = minChannels2;
                                            updateStatus = z;
                                        } else if (!needGetChannelsDiff3.contains(Integer.valueOf(channelId))) {
                                            needGetChannelsDiff3.add(Integer.valueOf(channelId));
                                        }
                                    }
                                    needReceivedQueue3 = needReceivedQueue;
                                    z = updateStatus;
                                } else if (channelPts != updatesNew.pts) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(update);
                                        stringBuilder2.append(" need get channel diff, pts: ");
                                        stringBuilder2.append(channelPts);
                                        stringBuilder2.append(" ");
                                        stringBuilder2.append(updatesNew.pts);
                                        stringBuilder2.append(" count = ");
                                        stringBuilder2.append(updatesNew.pts_count);
                                        stringBuilder2.append(" channelId = ");
                                        stringBuilder2.append(channelId);
                                        FileLog.m0d(stringBuilder2.toString());
                                    }
                                    long updatesStartWaitTime = messagesController.updatesStartWaitTimeChannels.get(channelId);
                                    needGetDiff = messagesController.gettingDifferenceChannels.get(channelId);
                                    if (needGetDiff || updatesStartWaitTime == 0) {
                                        boolean z3 = needGetDiff;
                                        needReceivedQueue3 = needReceivedQueue;
                                    } else {
                                        needReceivedQueue3 = needReceivedQueue;
                                        if (Math.abs(System.currentTimeMillis() - updatesStartWaitTime) > 1500) {
                                            if (needGetChannelsDiff3 == null) {
                                                needGetChannelsDiff3 = new ArrayList();
                                            } else if (!needGetChannelsDiff3.contains(Integer.valueOf(channelId))) {
                                                needGetChannelsDiff3.add(Integer.valueOf(channelId));
                                            }
                                            z = updateStatus;
                                        }
                                    }
                                    if (updatesStartWaitTime == 0) {
                                        z = updateStatus;
                                        messagesController.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                                    } else {
                                        z = updateStatus;
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("add to queue");
                                    }
                                    ArrayList<Updates> arrayList = (ArrayList) messagesController.updatesQueueChannels.get(channelId);
                                    if (arrayList == null) {
                                        arrayList = new ArrayList();
                                        messagesController.updatesQueueChannels.put(channelId, arrayList);
                                    }
                                    arrayList.add(updatesNew);
                                } else {
                                    needReceivedQueue3 = needReceivedQueue;
                                    z = updateStatus;
                                }
                                needGetDiff = needGetDiff3;
                                needReceivedQueue = needReceivedQueue3;
                                updates2.updates.remove(a2);
                                a2 = (a2 - 1) + 1;
                                i2 = 1;
                                minChannels = minChannels2;
                                updateStatus = z;
                            } else {
                                TL_updates updatesNew2 = new TL_updates();
                                updatesNew2.updates.add(update);
                                updatesNew2.pts = getUpdateQts(update);
                                for (int b2 = a2 + 1; b2 < updates2.updates.size(); b2 = (b2 - 1) + 1) {
                                    Update update22 = (Update) updates2.updates.get(b2);
                                    channelPts = getUpdateQts(update22);
                                    if (getUpdateType(update22) != 1 || updatesNew2.pts + 1 != channelPts) {
                                        break;
                                    }
                                    updatesNew2.updates.add(update22);
                                    updatesNew2.pts = channelPts;
                                    updates2.updates.remove(b2);
                                }
                                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue() != 0) {
                                    if (MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue() + updatesNew2.updates.size() != updatesNew2.pts) {
                                        if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != updatesNew2.pts) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                StringBuilder stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append(update);
                                                stringBuilder3.append(" need get diff, qts: ");
                                                stringBuilder3.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
                                                stringBuilder3.append(" ");
                                                stringBuilder3.append(updatesNew2.pts);
                                                FileLog.m0d(stringBuilder3.toString());
                                            }
                                            if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimeQts == 0)) {
                                                if (messagesController.updatesStartWaitTimeQts == 0 || Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeQts) > 1500) {
                                                    needGetDiff = true;
                                                }
                                            }
                                            if (messagesController.updatesStartWaitTimeQts == 0) {
                                                messagesController.updatesStartWaitTimeQts = System.currentTimeMillis();
                                            }
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m0d("add to queue");
                                            }
                                            messagesController.updatesQueueQts.add(updatesNew2);
                                        }
                                    }
                                }
                                processUpdateArray(updatesNew2.updates, updates2.users, updates2.chats, false);
                                MessagesStorage.getInstance(messagesController.currentAccount).setLastQtsValue(updatesNew2.pts);
                                needReceivedQueue = true;
                            }
                        } else {
                            TL_updates updatesNew3 = new TL_updates();
                            updatesNew3.updates.add(update);
                            updatesNew3.pts = getUpdatePts(update);
                            updatesNew3.pts_count = getUpdatePtsCount(update);
                            for (int b3 = a2 + 1; b3 < updates2.updates.size(); b3 = (b3 - 1) + i2) {
                                Update update23 = (Update) updates2.updates.get(b3);
                                channelId2 = getUpdatePts(update23);
                                pts2 = getUpdatePtsCount(update23);
                                if (getUpdateType(update23) != 0 || updatesNew3.pts + pts2 != channelId2) {
                                    break;
                                }
                                updatesNew3.updates.add(update23);
                                updatesNew3.pts = channelId2;
                                updatesNew3.pts_count += pts2;
                                updates2.updates.remove(b3);
                            }
                            StringBuilder stringBuilder4;
                            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + updatesNew3.pts_count == updatesNew3.pts) {
                                if (processUpdateArray(updatesNew3.updates, updates2.users, updates2.chats, false)) {
                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(updatesNew3.pts);
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append("need get diff inner TL_updates, pts: ");
                                        stringBuilder4.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                                        stringBuilder4.append(" ");
                                        stringBuilder4.append(updates2.seq);
                                        FileLog.m0d(stringBuilder4.toString());
                                    }
                                    needGetDiff = true;
                                }
                                minChannels2 = minChannels;
                            } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != updatesNew3.pts) {
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append(update);
                                    stringBuilder4.append(" need get diff, pts: ");
                                    stringBuilder4.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                                    stringBuilder4.append(" ");
                                    stringBuilder4.append(updatesNew3.pts);
                                    stringBuilder4.append(" count = ");
                                    stringBuilder4.append(updatesNew3.pts_count);
                                    FileLog.m0d(stringBuilder4.toString());
                                }
                                if (messagesController.gettingDifference || messagesController.updatesStartWaitTimePts == 0) {
                                    minChannels2 = minChannels;
                                } else {
                                    if (messagesController.updatesStartWaitTimePts != 0) {
                                        minChannels2 = minChannels;
                                        if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) <= 1500) {
                                        }
                                    } else {
                                        minChannels2 = minChannels;
                                    }
                                    needGetDiff = true;
                                }
                                if (messagesController.updatesStartWaitTimePts == 0) {
                                    messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("add to queue");
                                }
                                messagesController.updatesQueuePts.add(updatesNew3);
                            } else {
                                minChannels2 = minChannels;
                            }
                        }
                        z = updateStatus;
                        updates2.updates.remove(a2);
                        a2 = (a2 - 1) + 1;
                        i2 = 1;
                        minChannels = minChannels2;
                        updateStatus = z;
                    }
                    needGetDiff3 = needGetDiff;
                    needReceivedQueue3 = needReceivedQueue;
                    z = updateStatus;
                    i = i2;
                    minChannels2 = minChannels;
                    if (updates2 instanceof TL_updatesCombined) {
                        boolean z4;
                        if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + i != updates2.seq_start) {
                            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() != updates2.seq_start) {
                                z4 = false;
                                processUpdate = z4;
                            }
                        }
                        z4 = true;
                        processUpdate = z4;
                    } else {
                        if (!(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 == updates2.seq || updates2.seq == 0)) {
                            if (updates2.seq != MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue()) {
                                processUpdate = false;
                            }
                        }
                        processUpdate = true;
                    }
                    if (processUpdate) {
                        processUpdateArray(updates2.updates, updates2.users, updates2.chats, false);
                        if (updates2.seq != 0) {
                            if (updates2.date != 0) {
                                MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(updates2.date);
                            }
                            MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                        }
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            if (updates2 instanceof TL_updatesCombined) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("need get diff TL_updatesCombined, seq: ");
                                stringBuilder2.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                stringBuilder2.append(" ");
                                stringBuilder2.append(updates2.seq_start);
                                FileLog.m0d(stringBuilder2.toString());
                            } else {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("need get diff TL_updates, seq: ");
                                stringBuilder2.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                stringBuilder2.append(" ");
                                stringBuilder2.append(updates2.seq);
                                FileLog.m0d(stringBuilder2.toString());
                            }
                        }
                        if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimeSeq == 0)) {
                            if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) > 1500) {
                                needGetDiff = true;
                                needGetChannelsDiff = needGetChannelsDiff3;
                                needReceivedQueue = needReceivedQueue3;
                            }
                        }
                        if (messagesController.updatesStartWaitTimeSeq == 0) {
                            messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("add TL_updates/Combined to queue");
                        }
                        messagesController.updatesQueueSeq.add(updates2);
                    }
                    needGetChannelsDiff = needGetChannelsDiff3;
                    needGetDiff = needGetDiff3;
                    needReceivedQueue = needReceivedQueue3;
                }
                SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
                if (!fromQueue) {
                    for (a = 0; a < messagesController.updatesQueueChannels.size(); a++) {
                        channelId = messagesController.updatesQueueChannels.keyAt(a);
                        if (needGetChannelsDiff == null && needGetChannelsDiff.contains(Integer.valueOf(channelId))) {
                            getChannelDifference(channelId);
                        } else {
                            processChannelsUpdatesQueue(channelId, 0);
                        }
                    }
                    if (needGetDiff) {
                        getDifference();
                    } else {
                        for (a = 0; a < 3; a++) {
                            processUpdatesQueue(a, 0);
                        }
                    }
                }
                if (needReceivedQueue) {
                    req = new TL_messages_receivedQueue();
                    req.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                    ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                        }
                    });
                }
                if (z) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                        }
                    });
                }
                MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
            }
            i = updates2 instanceof TL_updateShortChatMessage ? updates2.from_id : updates2.user_id;
            User user = getUser(Integer.valueOf(i));
            User user2 = null;
            User user3 = null;
            Chat channel = null;
            if (user == null || user.min) {
                user = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i);
                if (user != null && user.min) {
                    user = null;
                }
                putUser(user, true);
            }
            skipUpdate = false;
            if (updates2.fwd_from != null) {
                if (updates2.fwd_from.from_id != 0) {
                    user2 = getUser(Integer.valueOf(updates2.fwd_from.from_id));
                    if (user2 == null) {
                        user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(updates2.fwd_from.from_id);
                        putUser(user2, true);
                    }
                    skipUpdate = true;
                }
                if (updates2.fwd_from.channel_id != 0) {
                    channel = getChat(Integer.valueOf(updates2.fwd_from.channel_id));
                    if (channel == null) {
                        channel = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(updates2.fwd_from.channel_id);
                        putChat(channel, true);
                    }
                    skipUpdate = true;
                }
            }
            boolean needBotUser = false;
            if (updates2.via_bot_id != 0) {
                user3 = getUser(Integer.valueOf(updates2.via_bot_id));
                if (user3 == null) {
                    user3 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(updates2.via_bot_id);
                    putUser(user3, true);
                }
                needBotUser = true;
            }
            if (updates2 instanceof TL_updateShortMessage) {
                if (!(user == null || (skipUpdate && user2 == null && channel == null))) {
                    if (!needBotUser || user3 != null) {
                        z2 = false;
                    }
                }
                z2 = true;
            } else {
                boolean z5;
                Chat chat3 = getChat(Integer.valueOf(updates2.chat_id));
                if (chat3 == null) {
                    chat3 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(updates2.chat_id);
                    putChat(chat3, true);
                }
                if (!(chat3 == null || user == null || (skipUpdate && user2 == null && channel == null))) {
                    if (!needBotUser || user3 != null) {
                        z5 = false;
                        z2 = z5;
                    }
                }
                z5 = true;
                z2 = z5;
            }
            if (!(z2 || updates2.entities.isEmpty())) {
                b = 0;
                while (b < updates2.entities.size()) {
                    MessageEntity entity = (MessageEntity) updates2.entities.get(b);
                    if (entity instanceof TL_messageEntityMentionName) {
                        pts2 = ((TL_messageEntityMentionName) entity).user_id;
                        needGetChannelsDiff2 = needGetChannelsDiff;
                        needGetChannelsDiff = getUser(Integer.valueOf(pts2));
                        if (needGetChannelsDiff != null) {
                            needGetDiff2 = needGetDiff;
                            if (!needGetChannelsDiff.min) {
                                continue;
                            }
                        } else {
                            needGetDiff2 = needGetDiff;
                        }
                        needGetChannelsDiff = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(pts2);
                        if (needGetChannelsDiff != null && needGetChannelsDiff.min) {
                            needGetChannelsDiff = null;
                        }
                        if (needGetChannelsDiff == null) {
                            z2 = true;
                            break;
                        }
                        putUser(user, true);
                    } else {
                        needGetChannelsDiff2 = needGetChannelsDiff;
                        needGetDiff2 = needGetDiff;
                    }
                    b++;
                    needGetChannelsDiff = needGetChannelsDiff2;
                    needGetDiff = needGetDiff2;
                }
            }
            needGetChannelsDiff2 = needGetChannelsDiff;
            needGetDiff2 = needGetDiff;
            if (!(user == null || user.status == null || user.status.expires > 0)) {
                messagesController.onlinePrivacy.put(Integer.valueOf(user.id), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                z = true;
            }
            if (z2) {
                needGetDiff = true;
                needReceivedQueue2 = false;
            } else {
                User user4;
                User user5;
                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + updates2.pts_count == updates2.pts) {
                    TL_message message = new TL_message();
                    message.id = updates2.id;
                    int clientUserId = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                    if (updates2 instanceof TL_updateShortMessage) {
                        if (updates2.out) {
                            message.from_id = clientUserId;
                        } else {
                            message.from_id = i;
                        }
                        message.to_id = new TL_peerUser();
                        message.to_id.user_id = i;
                        message.dialog_id = (long) i;
                    } else {
                        message.from_id = i;
                        message.to_id = new TL_peerChat();
                        message.to_id.chat_id = updates2.chat_id;
                        message.dialog_id = (long) (-updates2.chat_id);
                    }
                    message.fwd_from = updates2.fwd_from;
                    message.silent = updates2.silent;
                    message.out = updates2.out;
                    message.mentioned = updates2.mentioned;
                    message.media_unread = updates2.media_unread;
                    message.entities = updates2.entities;
                    message.message = updates2.message;
                    message.date = updates2.date;
                    message.via_bot_id = updates2.via_bot_id;
                    message.flags = updates2.flags | 256;
                    message.reply_to_msg_id = updates2.reply_to_msg_id;
                    message.media = new TL_messageMediaEmpty();
                    ConcurrentHashMap<Long, Integer> read_max = message.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                    Integer value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                    if (value == null) {
                        needReceivedQueue2 = false;
                        value = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                        read_max.put(Long.valueOf(message.dialog_id), value);
                    } else {
                        needReceivedQueue2 = false;
                        user4 = user;
                        user5 = user2;
                    }
                    message.unread = value.intValue() < message.id;
                    if (message.dialog_id == ((long) clientUserId)) {
                        message.unread = false;
                        message.media_unread = false;
                        message.out = true;
                    }
                    MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(updates2.pts);
                    needReceivedQueue = new MessageObject(messagesController.currentAccount, message, messagesController.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                    user = new ArrayList();
                    user.add(needReceivedQueue);
                    user2 = new ArrayList();
                    user2.add(message);
                    final boolean printUpdate;
                    if (updates2 instanceof TL_updateShortMessage) {
                        boolean z6 = !updates2.out && updatePrintingUsersWithNewMessages((long) updates2.user_id, user);
                        printUpdate = z6;
                        if (printUpdate) {
                            updatePrintingStrings();
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (printUpdate) {
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                                }
                                MessagesController.this.updateInterfaceWithMessages((long) i, user);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                        });
                    } else {
                        printUpdate = updatePrintingUsersWithNewMessages((long) (-updates2.chat_id), user);
                        if (printUpdate) {
                            updatePrintingStrings();
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (printUpdate) {
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                                }
                                MessagesController.this.updateInterfaceWithMessages((long) (-updates2.chat_id), user);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                        });
                    }
                    if (!needReceivedQueue.isOut()) {
                        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MessagesController$124$1 */
                            class C03081 implements Runnable {
                                C03081() {
                                }

                                public void run() {
                                    NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(user, true, false);
                                }
                            }

                            public void run() {
                                AndroidUtilities.runOnUIThread(new C03081());
                            }
                        });
                    }
                    MessagesStorage.getInstance(messagesController.currentAccount).putMessages((ArrayList) user2, false, true, false, 0);
                } else {
                    needReceivedQueue2 = false;
                    user4 = user;
                    user5 = user2;
                    User user6 = user3;
                    Chat chat4 = channel;
                    boolean z7 = skipUpdate;
                    if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != updates2.pts) {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder5 = new StringBuilder();
                            stringBuilder5.append("need get diff short message, pts: ");
                            stringBuilder5.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                            stringBuilder5.append(" ");
                            stringBuilder5.append(updates2.pts);
                            stringBuilder5.append(" count = ");
                            stringBuilder5.append(updates2.pts_count);
                            FileLog.m0d(stringBuilder5.toString());
                        }
                        if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimePts == 0)) {
                            if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) > 1500) {
                                needGetDiff = true;
                            }
                        }
                        if (messagesController.updatesStartWaitTimePts == 0) {
                            messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("add to queue");
                        }
                        messagesController.updatesQueuePts.add(updates2);
                    }
                }
                needGetDiff = needGetDiff2;
            }
            needGetChannelsDiff = needGetChannelsDiff2;
            needReceivedQueue = needReceivedQueue2;
            SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
            if (fromQueue) {
                for (a = 0; a < messagesController.updatesQueueChannels.size(); a++) {
                    channelId = messagesController.updatesQueueChannels.keyAt(a);
                    if (needGetChannelsDiff == null) {
                    }
                    processChannelsUpdatesQueue(channelId, 0);
                }
                if (needGetDiff) {
                    for (a = 0; a < 3; a++) {
                        processUpdatesQueue(a, 0);
                    }
                } else {
                    getDifference();
                }
            }
            if (needReceivedQueue) {
                req = new TL_messages_receivedQueue();
                req.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req, /* anonymous class already generated */);
            }
            if (z) {
                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
            }
            MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
        }
        z = false;
        SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
        if (fromQueue) {
            for (a = 0; a < messagesController.updatesQueueChannels.size(); a++) {
                channelId = messagesController.updatesQueueChannels.keyAt(a);
                if (needGetChannelsDiff == null) {
                }
                processChannelsUpdatesQueue(channelId, 0);
            }
            if (needGetDiff) {
                getDifference();
            } else {
                for (a = 0; a < 3; a++) {
                    processUpdatesQueue(a, 0);
                }
            }
        }
        if (needReceivedQueue) {
            req = new TL_messages_receivedQueue();
            req.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
            ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req, /* anonymous class already generated */);
        }
        if (z) {
            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
        }
        MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
    }

    public boolean processUpdateArray(ArrayList<Update> updates, ArrayList<User> usersArr, ArrayList<Chat> chatsArr, boolean fromGetDifference) {
        MessagesController messagesController = this;
        final ArrayList<User> arrayList = usersArr;
        final ArrayList<Chat> arrayList2 = chatsArr;
        if (updates.isEmpty()) {
            if (!(arrayList == null && arrayList2 == null)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MessagesController.this.putUsers(arrayList, false);
                        MessagesController.this.putChats(arrayList2, false);
                    }
                });
            }
            return true;
        }
        ConcurrentHashMap<Integer, User> concurrentHashMap;
        boolean z;
        boolean printChanged;
        LongSparseArray<ArrayList<MessageObject>> messages;
        LongSparseArray<WebPage> webPages;
        AbstractMap chatsDict;
        int a;
        int size;
        ArrayList<MessageObject> pushMessages;
        SparseIntArray markAsReadEncrypted;
        SparseLongArray markAsReadMessagesInbox;
        SparseArray<ArrayList<Integer>> deletedMessages;
        int i;
        ArrayList<Integer> contactsIds;
        ArrayList<Long> markAsReadMessages;
        SparseLongArray markAsReadMessagesOutbox;
        LongSparseArray<ArrayList<MessageObject>> messages2;
        int chat_id;
        boolean allowMin;
        long currentTime;
        boolean z2;
        LongSparseArray<ArrayList<MessageObject>> messages3;
        ConcurrentHashMap<Integer, Chat> chatsDict2;
        ArrayList<TL_updateEncryptedMessagesRead> tasks;
        LongSparseArray<WebPage> webPages2;
        LongSparseArray<ArrayList<MessageObject>> messages4;
        ArrayList<Integer> contactsIds2;
        SparseLongArray markAsReadMessagesInbox2;
        ArrayList<Integer> contactsIds3;
        ArrayList<Message> messagesArr;
        int size2;
        ConcurrentHashMap<Integer, Chat> chatsDict3;
        SparseArray<SparseIntArray> channelViews;
        MessagesController messagesController2;
        SparseIntArray markAsReadEncrypted2;
        ArrayList<Long> markAsReadMessages2;
        SparseArray<ArrayList<Integer>> deletedMessages2;
        int size3;
        SparseLongArray markAsReadMessagesOutbox2;
        ArrayList<Long> markAsReadMessages3;
        LongSparseArray<ArrayList<MessageObject>> editingMessagesFinal;
        SparseIntArray clearHistoryMessages;
        long currentTime2 = System.currentTimeMillis();
        LongSparseArray<ArrayList<MessageObject>> messages5 = null;
        LongSparseArray<WebPage> webPages3 = null;
        ArrayList<MessageObject> pushMessages2 = null;
        ArrayList<Message> messagesArr2 = null;
        ArrayList<ChatParticipants> chatInfoToUpdate = null;
        ArrayList<Update> updatesOnMainThread = null;
        boolean checkForUsers = true;
        if (arrayList != null) {
            concurrentHashMap = new ConcurrentHashMap();
            boolean size4 = usersArr.size();
            z = false;
            printChanged = false;
            while (true) {
                messages = messages5;
                boolean size5 = size4;
                if (printChanged >= size5) {
                    break;
                }
                boolean size6 = size5;
                User user = (User) arrayList.get(printChanged);
                webPages = webPages3;
                concurrentHashMap.put(Integer.valueOf(user.id), user);
                printChanged++;
                messages5 = messages;
                size4 = size6;
                webPages3 = webPages;
            }
            webPages = webPages3;
        } else {
            z = false;
            messages = null;
            webPages = null;
            checkForUsers = false;
            concurrentHashMap = messagesController.users;
        }
        if (arrayList2 != null) {
            chatsDict = new ConcurrentHashMap();
            a = 0;
            size = chatsArr.size();
            while (a < size) {
                int size7 = size;
                Chat chat = (Chat) arrayList2.get(a);
                pushMessages = pushMessages2;
                chatsDict.put(Integer.valueOf(chat.id), chat);
                a++;
                size = size7;
                pushMessages2 = pushMessages;
            }
            pushMessages = pushMessages2;
        } else {
            pushMessages = null;
            checkForUsers = false;
            chatsDict = messagesController.chats;
        }
        AbstractMap chatsDict4 = chatsDict;
        if (fromGetDifference) {
            checkForUsers = false;
        }
        if (!(arrayList == null && arrayList2 == null)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.this.putUsers(arrayList, false);
                    MessagesController.this.putChats(arrayList2, false);
                }
            });
        }
        size = 0;
        int size32 = updates.size();
        LongSparseArray<ArrayList<MessageObject>> editingMessages = null;
        SparseArray<SparseIntArray> channelViews2 = null;
        SparseLongArray markAsReadMessagesOutbox3 = null;
        ArrayList<Long> markAsReadMessages4 = null;
        SparseIntArray markAsReadEncrypted3 = null;
        SparseArray<ArrayList<Integer>> deletedMessages3 = null;
        SparseIntArray clearHistoryMessages2 = null;
        ArrayList<TL_updateEncryptedMessagesRead> tasks2 = null;
        LongSparseArray<ArrayList<MessageObject>> messages6 = messages;
        LongSparseArray<WebPage> webPages4 = webPages;
        int interfaceUpdateMask = 0;
        SparseLongArray markAsReadMessagesInbox3 = null;
        ArrayList<Integer> contactsIds4 = null;
        while (size < size32) {
            int c;
            AbstractMap chatsDict5;
            ArrayList<MessageObject> arr;
            int currentValue;
            int size33 = size32;
            Update baseUpdate = (Update) updates.get(size);
            if (BuildVars.LOGS_ENABLED) {
                c = size;
                StringBuilder stringBuilder = new StringBuilder();
                markAsReadEncrypted = markAsReadEncrypted3;
                stringBuilder.append("process update ");
                stringBuilder.append(baseUpdate);
                FileLog.m0d(stringBuilder.toString());
            } else {
                c = size;
                markAsReadEncrypted = markAsReadEncrypted3;
            }
            StringBuilder stringBuilder2;
            Chat chat2;
            int count;
            int chat_id2;
            int i2;
            MessageEntity chat_id3;
            Update baseUpdate2;
            int count2;
            StringBuilder stringBuilder3;
            User user2;
            User user3;
            ConcurrentHashMap<Long, Integer> read_max;
            Integer value;
            MessageObject messageObject;
            ArrayList<MessageObject> arr2;
            ArrayList<MessageObject> pushMessages3;
            if (baseUpdate instanceof TL_updateNewMessage) {
                markAsReadMessagesInbox = markAsReadMessagesInbox3;
                deletedMessages = deletedMessages3;
                i = interfaceUpdateMask;
                contactsIds = contactsIds4;
                markAsReadMessages = markAsReadMessages4;
                markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                messages2 = messages6;
                markAsReadEncrypted3 = clearHistoryMessages2;
                messages6 = tasks2;
                contactsIds4 = currentTime2;
                chatsDict5 = chatsDict4;
                if ((baseUpdate instanceof TL_updateNewMessage) == null) {
                    currentTime2 = ((TL_updateNewChannelMessage) baseUpdate).message;
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(baseUpdate);
                        stringBuilder2.append(" channelId = ");
                        stringBuilder2.append(currentTime2.to_id.channel_id);
                        FileLog.m0d(stringBuilder2.toString());
                    }
                    currentTime2.out = true;
                } else {
                    currentTime2 = ((TL_updateNewMessage) baseUpdate).message;
                }
                chat2 = null;
                chat_id = 0;
                a = 0;
                if (currentTime2.to_id.channel_id != 0) {
                    chat_id = currentTime2.to_id.channel_id;
                } else if (currentTime2.to_id.chat_id != 0) {
                    chat_id = currentTime2.to_id.chat_id;
                } else if (currentTime2.to_id.user_id != 0) {
                    a = currentTime2.to_id.user_id;
                }
                if (chat_id != 0) {
                    chat2 = (Chat) chatsDict5.get(Integer.valueOf(chat_id));
                    if (chat2 == null) {
                        chat2 = getChat(Integer.valueOf(chat_id));
                    }
                    if (chat2 == null) {
                        chat2 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(chat_id);
                        putChat(chat2, true);
                    }
                }
                if (!checkForUsers) {
                    if (chat_id == 0) {
                    }
                    count = 3 + currentTime2.entities.size();
                    size = a;
                    a = 0;
                    while (a < count) {
                        allowMin = false;
                        if (a == 0) {
                            chat_id2 = chat_id;
                            i2 = size;
                        } else {
                            chat_id2 = chat_id;
                            if (a == 1) {
                                chat_id = currentTime2.from_id;
                                if (currentTime2.post) {
                                    allowMin = true;
                                }
                            } else if (a != 2) {
                                chat_id3 = (MessageEntity) currentTime2.entities.get(a - 3);
                                if (chat_id3 instanceof TL_messageEntityMentionName) {
                                }
                                chat_id = size;
                            } else if (currentTime2.fwd_from == 0) {
                            }
                            size = chat_id;
                        }
                        if (size <= 0) {
                            baseUpdate2 = baseUpdate;
                            count2 = count;
                        } else {
                            chat_id = (User) concurrentHashMap.get(Integer.valueOf(size));
                            if (chat_id == null) {
                                baseUpdate2 = baseUpdate;
                            } else {
                                if (allowMin) {
                                    baseUpdate2 = baseUpdate;
                                } else {
                                    baseUpdate2 = baseUpdate;
                                    if (chat_id.min != null) {
                                    }
                                }
                                chat_id = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                                chat_id = 0;
                                putUser(chat_id, true);
                                if (chat_id != 0) {
                                    if (BuildVars.LOGS_ENABLED) {
                                    } else {
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append("not found user ");
                                        stringBuilder3.append(size);
                                        FileLog.m0d(stringBuilder3.toString());
                                    }
                                    return false;
                                }
                                count2 = count;
                                user2 = chat_id;
                                messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                i |= 4;
                            }
                            chat_id = getUser(Integer.valueOf(size));
                            chat_id = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                            chat_id = 0;
                            putUser(chat_id, true);
                            if (chat_id != 0) {
                                count2 = count;
                                user2 = chat_id;
                                messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                i |= 4;
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                } else {
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("not found user ");
                                    stringBuilder3.append(size);
                                    FileLog.m0d(stringBuilder3.toString());
                                }
                                return false;
                            }
                        }
                        a++;
                        chat_id = chat_id2;
                        baseUpdate = baseUpdate2;
                        count = count2;
                    }
                    i2 = size;
                    baseUpdate2 = baseUpdate;
                } else {
                    baseUpdate2 = baseUpdate;
                    i2 = a;
                }
                currentTime2.flags |= Integer.MIN_VALUE;
                if (currentTime2.action instanceof TL_messageActionChatDeleteUser) {
                    user3 = (User) concurrentHashMap.get(Integer.valueOf(currentTime2.action.user_id));
                    if (user3 == null) {
                    }
                }
                if (messagesArr2 == null) {
                    messagesArr2 = new ArrayList();
                }
                messagesArr2.add(currentTime2);
                ImageLoader.saveMessageThumbs(currentTime2);
                chat_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                if (currentTime2.to_id.chat_id != 0) {
                    currentTime2.dialog_id = (long) (-currentTime2.to_id.chat_id);
                } else if (currentTime2.to_id.channel_id == 0) {
                    if (currentTime2.to_id.user_id == chat_id) {
                        currentTime2.to_id.user_id = currentTime2.from_id;
                    }
                    currentTime2.dialog_id = (long) currentTime2.to_id.user_id;
                } else {
                    currentTime2.dialog_id = (long) (-currentTime2.to_id.channel_id);
                }
                if (currentTime2.out) {
                }
                value = (Integer) read_max.get(Long.valueOf(currentTime2.dialog_id));
                if (value != null) {
                    currentTime = contactsIds4;
                } else {
                    currentTime = contactsIds4;
                    value = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(currentTime2.out, currentTime2.dialog_id));
                    read_max.put(Long.valueOf(currentTime2.dialog_id), value);
                }
                if (value.intValue() >= currentTime2.id) {
                }
                currentTime2.unread = z2;
                if (currentTime2.dialog_id == ((long) chat_id)) {
                    currentTime2.unread = false;
                    currentTime2.media_unread = false;
                    currentTime2.out = true;
                }
                messageObject = new MessageObject(messagesController.currentAccount, (Message) currentTime2, (AbstractMap) concurrentHashMap, chatsDict5, messagesController.createdDialogIds.contains(Long.valueOf(currentTime2.dialog_id)));
                if (messageObject.type == 11) {
                    size = i | 8;
                } else if (messageObject.type != 10) {
                    size = i;
                    if (messages2 != null) {
                        messages3 = new LongSparseArray();
                    } else {
                        messages3 = messages2;
                    }
                    arr2 = (ArrayList) messages3.get(currentTime2.dialog_id);
                    if (arr2 == null) {
                        arr2 = new ArrayList();
                        messages3.put(currentTime2.dialog_id, arr2);
                    }
                    arr2.add(messageObject);
                    if (!messageObject.isOut()) {
                    }
                    pushMessages3 = pushMessages;
                    i = size;
                    pushMessages = pushMessages3;
                    tasks2 = messages6;
                    clearHistoryMessages2 = markAsReadEncrypted3;
                    markAsReadEncrypted3 = markAsReadEncrypted;
                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                    contactsIds4 = contactsIds;
                    markAsReadMessages4 = markAsReadMessages;
                    markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                    deletedMessages3 = deletedMessages;
                    messages6 = messages3;
                    size = c + 1;
                    chatsDict4 = chatsDict5;
                    size32 = size33;
                    interfaceUpdateMask = i;
                    currentTime2 = currentTime;
                } else {
                    size = i | 16;
                }
                if (messages2 != null) {
                    messages3 = messages2;
                } else {
                    messages3 = new LongSparseArray();
                }
                arr2 = (ArrayList) messages3.get(currentTime2.dialog_id);
                if (arr2 == null) {
                    arr2 = new ArrayList();
                    messages3.put(currentTime2.dialog_id, arr2);
                }
                arr2.add(messageObject);
                if (messageObject.isOut()) {
                }
                pushMessages3 = pushMessages;
                i = size;
                pushMessages = pushMessages3;
                tasks2 = messages6;
                clearHistoryMessages2 = markAsReadEncrypted3;
                markAsReadEncrypted3 = markAsReadEncrypted;
                markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                contactsIds4 = contactsIds;
                markAsReadMessages4 = markAsReadMessages;
                markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                deletedMessages3 = deletedMessages;
                messages6 = messages3;
                size = c + 1;
                chatsDict4 = chatsDict5;
                size32 = size33;
                interfaceUpdateMask = i;
                currentTime2 = currentTime;
            } else if (baseUpdate instanceof TL_updateNewChannelMessage) {
                markAsReadMessagesInbox = markAsReadMessagesInbox3;
                deletedMessages = deletedMessages3;
                i = interfaceUpdateMask;
                contactsIds = contactsIds4;
                markAsReadMessages = markAsReadMessages4;
                markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                messages2 = messages6;
                markAsReadEncrypted3 = clearHistoryMessages2;
                messages6 = tasks2;
                contactsIds4 = currentTime2;
                chatsDict5 = chatsDict4;
                if ((baseUpdate instanceof TL_updateNewMessage) == null) {
                    currentTime2 = ((TL_updateNewMessage) baseUpdate).message;
                } else {
                    currentTime2 = ((TL_updateNewChannelMessage) baseUpdate).message;
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(baseUpdate);
                        stringBuilder2.append(" channelId = ");
                        stringBuilder2.append(currentTime2.to_id.channel_id);
                        FileLog.m0d(stringBuilder2.toString());
                    }
                    if (!currentTime2.out && currentTime2.from_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                        currentTime2.out = true;
                    }
                }
                chat2 = null;
                chat_id = 0;
                a = 0;
                if (currentTime2.to_id.channel_id != 0) {
                    chat_id = currentTime2.to_id.channel_id;
                } else if (currentTime2.to_id.chat_id != 0) {
                    chat_id = currentTime2.to_id.chat_id;
                } else if (currentTime2.to_id.user_id != 0) {
                    a = currentTime2.to_id.user_id;
                }
                if (chat_id != 0) {
                    chat2 = (Chat) chatsDict5.get(Integer.valueOf(chat_id));
                    if (chat2 == null) {
                        chat2 = getChat(Integer.valueOf(chat_id));
                    }
                    if (chat2 == null) {
                        chat2 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(chat_id);
                        putChat(chat2, true);
                    }
                }
                if (!checkForUsers) {
                    baseUpdate2 = baseUpdate;
                    i2 = a;
                } else if (chat_id == 0 && chat2 == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("not found chat ");
                        stringBuilder.append(chat_id);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    return false;
                } else {
                    count = 3 + currentTime2.entities.size();
                    size = a;
                    a = 0;
                    while (a < count) {
                        allowMin = false;
                        if (a == 0) {
                            chat_id2 = chat_id;
                            if (a == 1) {
                                chat_id = currentTime2.from_id;
                                if (currentTime2.post) {
                                    allowMin = true;
                                }
                            } else if (a != 2) {
                                chat_id = currentTime2.fwd_from == 0 ? currentTime2.fwd_from.from_id : 0;
                            } else {
                                chat_id3 = (MessageEntity) currentTime2.entities.get(a - 3);
                                size = chat_id3 instanceof TL_messageEntityMentionName ? ((TL_messageEntityMentionName) chat_id3).user_id : 0;
                                chat_id = size;
                            }
                            size = chat_id;
                        } else {
                            chat_id2 = chat_id;
                            i2 = size;
                        }
                        if (size <= 0) {
                            chat_id = (User) concurrentHashMap.get(Integer.valueOf(size));
                            if (chat_id == null) {
                                if (allowMin) {
                                    baseUpdate2 = baseUpdate;
                                    if (chat_id.min != null) {
                                    }
                                } else {
                                    baseUpdate2 = baseUpdate;
                                }
                                if (chat_id == 0 || (!allowMin && chat_id.min)) {
                                    chat_id = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                                    if (!(chat_id == 0 || allowMin || !chat_id.min)) {
                                        chat_id = 0;
                                    }
                                    putUser(chat_id, true);
                                }
                                if (chat_id != 0) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append("not found user ");
                                        stringBuilder3.append(size);
                                        FileLog.m0d(stringBuilder3.toString());
                                    }
                                    return false;
                                }
                                count2 = count;
                                if (a == 1 && chat_id.status != null && chat_id.status.expires <= 0) {
                                    user2 = chat_id;
                                    messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                    i |= 4;
                                }
                            } else {
                                baseUpdate2 = baseUpdate;
                            }
                            chat_id = getUser(Integer.valueOf(size));
                            chat_id = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                            chat_id = 0;
                            putUser(chat_id, true);
                            if (chat_id != 0) {
                                count2 = count;
                                user2 = chat_id;
                                messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                i |= 4;
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                } else {
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("not found user ");
                                    stringBuilder3.append(size);
                                    FileLog.m0d(stringBuilder3.toString());
                                }
                                return false;
                            }
                        }
                        baseUpdate2 = baseUpdate;
                        count2 = count;
                        a++;
                        chat_id = chat_id2;
                        baseUpdate = baseUpdate2;
                        count = count2;
                    }
                    i2 = size;
                    baseUpdate2 = baseUpdate;
                }
                if (chat2 != null && chat2.megagroup) {
                    currentTime2.flags |= Integer.MIN_VALUE;
                }
                if (currentTime2.action instanceof TL_messageActionChatDeleteUser) {
                    user3 = (User) concurrentHashMap.get(Integer.valueOf(currentTime2.action.user_id));
                    if (user3 == null && user3.bot) {
                        currentTime2.reply_markup = new TL_replyKeyboardHide();
                        currentTime2.flags |= 64;
                    } else if (currentTime2.from_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId() && currentTime2.action.user_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                    }
                }
                if (messagesArr2 == null) {
                    messagesArr2 = new ArrayList();
                }
                messagesArr2.add(currentTime2);
                ImageLoader.saveMessageThumbs(currentTime2);
                chat_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                if (currentTime2.to_id.chat_id != 0) {
                    currentTime2.dialog_id = (long) (-currentTime2.to_id.chat_id);
                } else if (currentTime2.to_id.channel_id == 0) {
                    currentTime2.dialog_id = (long) (-currentTime2.to_id.channel_id);
                } else {
                    if (currentTime2.to_id.user_id == chat_id) {
                        currentTime2.to_id.user_id = currentTime2.from_id;
                    }
                    currentTime2.dialog_id = (long) currentTime2.to_id.user_id;
                }
                read_max = currentTime2.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                value = (Integer) read_max.get(Long.valueOf(currentTime2.dialog_id));
                if (value != null) {
                    currentTime = contactsIds4;
                    value = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(currentTime2.out, currentTime2.dialog_id));
                    read_max.put(Long.valueOf(currentTime2.dialog_id), value);
                } else {
                    currentTime = contactsIds4;
                }
                z2 = value.intValue() >= currentTime2.id && !((chat2 != null && ChatObject.isNotInChat(chat2)) || (currentTime2.action instanceof TL_messageActionChatMigrateTo) || (currentTime2.action instanceof TL_messageActionChannelCreate));
                currentTime2.unread = z2;
                if (currentTime2.dialog_id == ((long) chat_id)) {
                    currentTime2.unread = false;
                    currentTime2.media_unread = false;
                    currentTime2.out = true;
                }
                messageObject = new MessageObject(messagesController.currentAccount, (Message) currentTime2, (AbstractMap) concurrentHashMap, chatsDict5, messagesController.createdDialogIds.contains(Long.valueOf(currentTime2.dialog_id)));
                if (messageObject.type == 11) {
                    size = i | 8;
                } else if (messageObject.type != 10) {
                    size = i | 16;
                } else {
                    size = i;
                    if (messages2 != null) {
                        messages3 = new LongSparseArray();
                    } else {
                        messages3 = messages2;
                    }
                    arr2 = (ArrayList) messages3.get(currentTime2.dialog_id);
                    if (arr2 == null) {
                        arr2 = new ArrayList();
                        messages3.put(currentTime2.dialog_id, arr2);
                    }
                    arr2.add(messageObject);
                    if (messageObject.isOut() || !messageObject.isUnread()) {
                        pushMessages3 = pushMessages;
                    } else {
                        if (pushMessages == null) {
                            pushMessages3 = new ArrayList();
                            pushMessages = pushMessages3;
                        } else {
                            pushMessages3 = pushMessages;
                        }
                        pushMessages3.add(messageObject);
                    }
                    i = size;
                    pushMessages = pushMessages3;
                    tasks2 = messages6;
                    clearHistoryMessages2 = markAsReadEncrypted3;
                    markAsReadEncrypted3 = markAsReadEncrypted;
                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                    contactsIds4 = contactsIds;
                    markAsReadMessages4 = markAsReadMessages;
                    markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                    deletedMessages3 = deletedMessages;
                    messages6 = messages3;
                    size = c + 1;
                    chatsDict4 = chatsDict5;
                    size32 = size33;
                    interfaceUpdateMask = i;
                    currentTime2 = currentTime;
                }
                if (messages2 != null) {
                    messages3 = messages2;
                } else {
                    messages3 = new LongSparseArray();
                }
                arr2 = (ArrayList) messages3.get(currentTime2.dialog_id);
                if (arr2 == null) {
                    arr2 = new ArrayList();
                    messages3.put(currentTime2.dialog_id, arr2);
                }
                arr2.add(messageObject);
                if (messageObject.isOut()) {
                }
                pushMessages3 = pushMessages;
                i = size;
                pushMessages = pushMessages3;
                tasks2 = messages6;
                clearHistoryMessages2 = markAsReadEncrypted3;
                markAsReadEncrypted3 = markAsReadEncrypted;
                markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                contactsIds4 = contactsIds;
                markAsReadMessages4 = markAsReadMessages;
                markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                deletedMessages3 = deletedMessages;
                messages6 = messages3;
                size = c + 1;
                chatsDict4 = chatsDict5;
                size32 = size33;
                interfaceUpdateMask = i;
                currentTime2 = currentTime;
            } else {
                long currentTime3;
                AbstractMap chatsDict6;
                if (baseUpdate instanceof TL_updateReadMessagesContents) {
                    TL_updateReadMessagesContents update = (TL_updateReadMessagesContents) baseUpdate;
                    if (markAsReadMessages4 == null) {
                        markAsReadMessages4 = new ArrayList();
                    }
                    int a2 = 0;
                    int size8 = update.messages.size();
                    currentTime3 = currentTime2;
                    currentTime2 = a2;
                    while (currentTime2 < size8) {
                        chatsDict6 = chatsDict4;
                        TL_updateReadMessagesContents update2 = update;
                        markAsReadMessages4.add(Long.valueOf((long) ((Integer) update.messages.get(currentTime2)).intValue()));
                        currentTime2++;
                        chatsDict4 = chatsDict6;
                        update = update2;
                    }
                    i = interfaceUpdateMask;
                    markAsReadEncrypted3 = markAsReadEncrypted;
                    currentTime = currentTime3;
                    chatsDict5 = chatsDict4;
                } else {
                    currentTime3 = currentTime2;
                    chatsDict6 = chatsDict4;
                    ConcurrentHashMap<Integer, User> usersDict;
                    int interfaceUpdateMask2;
                    if ((baseUpdate instanceof TL_updateChannelReadMessagesContents) != null) {
                        TL_updateChannelReadMessagesContents currentTime4 = (TL_updateChannelReadMessagesContents) baseUpdate;
                        if (markAsReadMessages4 == null) {
                            markAsReadMessages4 = new ArrayList();
                        }
                        a = 0;
                        size = currentTime4.messages.size();
                        while (a < size) {
                            markAsReadMessagesInbox = markAsReadMessagesInbox3;
                            messages2 = messages6;
                            usersDict = concurrentHashMap;
                            interfaceUpdateMask2 = interfaceUpdateMask;
                            contactsIds = contactsIds4;
                            markAsReadMessages4.add(Long.valueOf(((long) ((Integer) currentTime4.messages.get(a)).intValue()) | (((long) currentTime4.channel_id) << 32)));
                            a++;
                            markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                            concurrentHashMap = usersDict;
                            messages6 = messages2;
                            interfaceUpdateMask = interfaceUpdateMask2;
                            contactsIds4 = contactsIds;
                        }
                        interfaceUpdateMask2 = interfaceUpdateMask;
                        contactsIds = contactsIds4;
                        messages2 = messages6;
                        usersDict = concurrentHashMap;
                        markAsReadEncrypted3 = markAsReadEncrypted;
                        currentTime = currentTime3;
                        chatsDict5 = chatsDict6;
                        i = interfaceUpdateMask2;
                    } else {
                        markAsReadMessagesInbox = markAsReadMessagesInbox3;
                        interfaceUpdateMask2 = interfaceUpdateMask;
                        contactsIds = contactsIds4;
                        messages2 = messages6;
                        usersDict = concurrentHashMap;
                        long dialog_id;
                        if ((baseUpdate instanceof TL_updateReadHistoryInbox) != null) {
                            TL_updateReadHistoryInbox currentTime5 = (TL_updateReadHistoryInbox) baseUpdate;
                            if (markAsReadMessagesInbox == null) {
                                markAsReadMessagesInbox3 = new SparseLongArray();
                            } else {
                                markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                            }
                            if (currentTime5.peer.chat_id != 0) {
                                markAsReadMessagesInbox3.put(-currentTime5.peer.chat_id, (long) currentTime5.max_id);
                                dialog_id = (long) (-currentTime5.peer.chat_id);
                            } else {
                                markAsReadMessagesInbox3.put(currentTime5.peer.user_id, (long) currentTime5.max_id);
                                dialog_id = (long) currentTime5.peer.user_id;
                            }
                            Integer value2 = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                            if (value2 == null) {
                                value2 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(false, dialog_id));
                            }
                            messagesController.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value2.intValue(), currentTime5.max_id)));
                            markAsReadEncrypted3 = markAsReadEncrypted;
                            currentTime = currentTime3;
                            chatsDict5 = chatsDict6;
                        } else {
                            long dialog_id2;
                            Integer value3;
                            if ((baseUpdate instanceof TL_updateReadHistoryOutbox) != null) {
                                TL_updateReadHistoryOutbox currentTime6 = (TL_updateReadHistoryOutbox) baseUpdate;
                                if (markAsReadMessagesOutbox3 == null) {
                                    markAsReadMessagesOutbox3 = new SparseLongArray();
                                }
                                if (currentTime6.peer.chat_id != 0) {
                                    markAsReadMessagesOutbox3.put(-currentTime6.peer.chat_id, (long) currentTime6.max_id);
                                    dialog_id2 = (long) (-currentTime6.peer.chat_id);
                                } else {
                                    markAsReadMessagesOutbox3.put(currentTime6.peer.user_id, (long) currentTime6.max_id);
                                    dialog_id2 = (long) currentTime6.peer.user_id;
                                }
                                value3 = (Integer) messagesController.dialogs_read_outbox_max.get(Long.valueOf(dialog_id2));
                                if (value3 == null) {
                                    value3 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(true, dialog_id2));
                                }
                                messagesController.dialogs_read_outbox_max.put(Long.valueOf(dialog_id2), Integer.valueOf(Math.max(value3.intValue(), currentTime6.max_id)));
                            } else if ((baseUpdate instanceof TL_updateDeleteMessages) != null) {
                                TL_updateDeleteMessages currentTime7 = (TL_updateDeleteMessages) baseUpdate;
                                if (deletedMessages3 == null) {
                                    deletedMessages3 = new SparseArray();
                                }
                                arrayList = (ArrayList) deletedMessages3.get(0);
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    deletedMessages3.put(0, arrayList);
                                }
                                arrayList.addAll(currentTime7.messages);
                            } else {
                                long currentTime8;
                                long message_id;
                                SparseArray<ArrayList<Integer>> chatsDict7;
                                if ((baseUpdate instanceof TL_updateUserTyping) != null) {
                                    markAsReadMessages = markAsReadMessages4;
                                    markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                    messages6 = tasks2;
                                    currentTime8 = currentTime3;
                                    chatsDict5 = chatsDict6;
                                    concurrentHashMap = usersDict;
                                    i = interfaceUpdateMask2;
                                } else if ((baseUpdate instanceof TL_updateChatUserTyping) != null) {
                                    markAsReadMessages = markAsReadMessages4;
                                    markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                    messages6 = tasks2;
                                    currentTime8 = currentTime3;
                                    chatsDict5 = chatsDict6;
                                    concurrentHashMap = usersDict;
                                    i = interfaceUpdateMask2;
                                } else {
                                    if ((baseUpdate instanceof TL_updateChatParticipants) != null) {
                                        ArrayList<ChatParticipants> chatInfoToUpdate2;
                                        TL_updateChatParticipants currentTime9 = (TL_updateChatParticipants) baseUpdate;
                                        chatsDict2 = interfaceUpdateMask2 | 32;
                                        if (chatInfoToUpdate == null) {
                                            chatInfoToUpdate2 = new ArrayList();
                                        } else {
                                            chatInfoToUpdate2 = chatInfoToUpdate;
                                        }
                                        chatInfoToUpdate2.add(currentTime9.participants);
                                        chatInfoToUpdate = chatInfoToUpdate2;
                                    } else {
                                        if ((baseUpdate instanceof TL_updateUserStatus) != null) {
                                            chatsDict2 = interfaceUpdateMask2 | 4;
                                            if (updatesOnMainThread == null) {
                                                currentTime2 = new ArrayList();
                                                updatesOnMainThread = currentTime2;
                                            } else {
                                                currentTime2 = updatesOnMainThread;
                                            }
                                            currentTime2.add(baseUpdate);
                                        } else if ((baseUpdate instanceof TL_updateUserName) != null) {
                                            chatsDict2 = interfaceUpdateMask2 | 1;
                                            if (updatesOnMainThread == null) {
                                                currentTime2 = new ArrayList();
                                                updatesOnMainThread = currentTime2;
                                            } else {
                                                currentTime2 = updatesOnMainThread;
                                            }
                                            currentTime2.add(baseUpdate);
                                        } else if ((baseUpdate instanceof TL_updateUserPhoto) != null) {
                                            chatsDict2 = interfaceUpdateMask2 | 2;
                                            MessagesStorage.getInstance(messagesController.currentAccount).clearUserPhotos(((TL_updateUserPhoto) baseUpdate).user_id);
                                            if (updatesOnMainThread == null) {
                                                updatesOnMainThread = new ArrayList();
                                            } else {
                                                updatesOnMainThread = updatesOnMainThread;
                                            }
                                            updatesOnMainThread.add(baseUpdate);
                                            updatesOnMainThread = updatesOnMainThread;
                                        } else if ((baseUpdate instanceof TL_updateUserPhone) != null) {
                                            chatsDict2 = interfaceUpdateMask2 | 1024;
                                            if (updatesOnMainThread == null) {
                                                currentTime2 = new ArrayList();
                                                updatesOnMainThread = currentTime2;
                                            } else {
                                                currentTime2 = updatesOnMainThread;
                                            }
                                            currentTime2.add(baseUpdate);
                                        } else {
                                            currentTime2 = interfaceUpdateMask2;
                                            if (baseUpdate instanceof TL_updateContactRegistered) {
                                                TL_updateContactRegistered update3 = (TL_updateContactRegistered) baseUpdate;
                                                if (messagesController.enableJoined) {
                                                    concurrentHashMap = usersDict;
                                                    if (!concurrentHashMap.containsKey(Integer.valueOf(update3.user_id)) || MessagesStorage.getInstance(messagesController.currentAccount).isDialogHasMessages((long) update3.user_id)) {
                                                        markAsReadMessages = markAsReadMessages4;
                                                    } else {
                                                        markAsReadMessagesInbox3 = new TL_messageService();
                                                        markAsReadMessagesInbox3.action = new TL_messageActionUserJoined();
                                                        a = UserConfig.getInstance(messagesController.currentAccount).getNewMessageId();
                                                        markAsReadMessagesInbox3.id = a;
                                                        markAsReadMessagesInbox3.local_id = a;
                                                        UserConfig.getInstance(messagesController.currentAccount).saveConfig(false);
                                                        markAsReadMessagesInbox3.unread = false;
                                                        markAsReadMessagesInbox3.flags = 256;
                                                        markAsReadMessagesInbox3.date = update3.date;
                                                        markAsReadMessagesInbox3.from_id = update3.user_id;
                                                        markAsReadMessagesInbox3.to_id = new TL_peerUser();
                                                        markAsReadMessagesInbox3.to_id.user_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                                                        markAsReadMessagesInbox3.dialog_id = (long) update3.user_id;
                                                        if (messagesArr2 == null) {
                                                            messagesArr2 = new ArrayList();
                                                        }
                                                        messagesArr2.add(markAsReadMessagesInbox3);
                                                        markAsReadMessages = markAsReadMessages4;
                                                        messageObject = new MessageObject(messagesController.currentAccount, (Message) markAsReadMessagesInbox3, (AbstractMap) concurrentHashMap, chatsDict6, messagesController.createdDialogIds.contains(Long.valueOf(markAsReadMessagesInbox3.dialog_id)));
                                                        if (messages2 == null) {
                                                            messages6 = new LongSparseArray();
                                                        } else {
                                                            messages6 = messages2;
                                                        }
                                                        arr = (ArrayList) messages6.get(markAsReadMessagesInbox3.dialog_id);
                                                        if (arr == null) {
                                                            arr = new ArrayList();
                                                            messages6.put(markAsReadMessagesInbox3.dialog_id, arr);
                                                        }
                                                        arr.add(messageObject);
                                                    }
                                                } else {
                                                    markAsReadMessages = markAsReadMessages4;
                                                    concurrentHashMap = usersDict;
                                                }
                                                messages6 = messages2;
                                            } else {
                                                markAsReadMessages = markAsReadMessages4;
                                                concurrentHashMap = usersDict;
                                                if (baseUpdate instanceof TL_updateContactLink) {
                                                    TL_updateContactLink update4 = (TL_updateContactLink) baseUpdate;
                                                    if (contactsIds == null) {
                                                        contactsIds4 = new ArrayList();
                                                    } else {
                                                        contactsIds4 = contactsIds;
                                                    }
                                                    if (update4.my_link instanceof TL_contactLinkContact) {
                                                        chat_id = contactsIds4.indexOf(Integer.valueOf(-update4.user_id));
                                                        if (chat_id != -1) {
                                                            contactsIds4.remove(chat_id);
                                                        }
                                                        if (!contactsIds4.contains(Integer.valueOf(update4.user_id))) {
                                                            contactsIds4.add(Integer.valueOf(update4.user_id));
                                                        }
                                                    } else {
                                                        chat_id = contactsIds4.indexOf(Integer.valueOf(update4.user_id));
                                                        if (chat_id != -1) {
                                                            contactsIds4.remove(chat_id);
                                                        }
                                                        if (!contactsIds4.contains(Integer.valueOf(update4.user_id))) {
                                                            contactsIds4.add(Integer.valueOf(-update4.user_id));
                                                        }
                                                    }
                                                    i = currentTime2;
                                                    markAsReadEncrypted3 = markAsReadEncrypted;
                                                    currentTime = currentTime3;
                                                    chatsDict5 = chatsDict6;
                                                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                    messages6 = messages2;
                                                    markAsReadMessages4 = markAsReadMessages;
                                                } else if (baseUpdate instanceof TL_updateNewEncryptedMessage) {
                                                    ArrayList<Message> decryptedMessages = SecretChatHelper.getInstance(messagesController.currentAccount).decryptMessage(((TL_updateNewEncryptedMessage) baseUpdate).message);
                                                    if (decryptedMessages == null || decryptedMessages.isEmpty()) {
                                                        messages6 = messages2;
                                                    } else {
                                                        chat_id = ((TL_updateNewEncryptedMessage) baseUpdate).message.chat_id;
                                                        dialog_id = ((long) chat_id) << 32;
                                                        if (messages2 == null) {
                                                            messages6 = new LongSparseArray();
                                                        } else {
                                                            messages6 = messages2;
                                                        }
                                                        ArrayList<MessageObject> arr3 = (ArrayList) messages6.get(dialog_id);
                                                        if (arr3 == null) {
                                                            arr3 = new ArrayList();
                                                            messages6.put(dialog_id, arr3);
                                                        }
                                                        a = 0;
                                                        markAsReadMessages4 = decryptedMessages.size();
                                                        while (a < markAsReadMessages4) {
                                                            ArrayList<Message> decryptedMessages2;
                                                            ArrayList<MessageObject> pushMessages4;
                                                            Message message = (Message) decryptedMessages.get(a);
                                                            ImageLoader.saveMessageThumbs(message);
                                                            if (messagesArr2 == null) {
                                                                decryptedMessages2 = decryptedMessages;
                                                                messagesArr2 = new ArrayList();
                                                            } else {
                                                                decryptedMessages2 = decryptedMessages;
                                                            }
                                                            messagesArr2.add(message);
                                                            int cid = chat_id;
                                                            ArrayList<Message> messagesArr3 = messagesArr2;
                                                            int size9 = markAsReadMessages4;
                                                            messageObject = new MessageObject(messagesController.currentAccount, message, (AbstractMap) concurrentHashMap, chatsDict6, messagesController.createdDialogIds.contains(Long.valueOf(dialog_id)));
                                                            arr3.add(messageObject);
                                                            if (pushMessages == null) {
                                                                pushMessages4 = new ArrayList();
                                                            } else {
                                                                pushMessages4 = pushMessages;
                                                            }
                                                            pushMessages4.add(messageObject);
                                                            a++;
                                                            pushMessages = pushMessages4;
                                                            decryptedMessages = decryptedMessages2;
                                                            chat_id = cid;
                                                            messagesArr2 = messagesArr3;
                                                            markAsReadMessages4 = size9;
                                                        }
                                                    }
                                                } else {
                                                    if (baseUpdate instanceof TL_updateEncryptedChatTyping) {
                                                        TL_updateEncryptedChatTyping update5 = (TL_updateEncryptedChatTyping) baseUpdate;
                                                        markAsReadMessagesInbox3 = getEncryptedChatDB(update5.chat_id, true);
                                                        if (markAsReadMessagesInbox3 != null) {
                                                            long uid;
                                                            dialog_id = ((long) update5.chat_id) << 32;
                                                            chatsDict2 = (ArrayList) messagesController.printingUsers.get(Long.valueOf(dialog_id));
                                                            if (chatsDict2 == null) {
                                                                chatsDict2 = new ArrayList();
                                                                messagesController.printingUsers.put(Long.valueOf(dialog_id), chatsDict2);
                                                            }
                                                            contactsIds4 = null;
                                                            markAsReadMessages4 = null;
                                                            int size10 = chatsDict2.size();
                                                            while (markAsReadMessages4 < size10) {
                                                                PrintingUser u = (PrintingUser) chatsDict2.get(markAsReadMessages4);
                                                                TL_updateEncryptedChatTyping update6 = update5;
                                                                uid = dialog_id;
                                                                if (u.userId == markAsReadMessagesInbox3.user_id) {
                                                                    contactsIds4 = true;
                                                                    dialog_id = currentTime3;
                                                                    u.lastTime = dialog_id;
                                                                    u.action = new TL_sendMessageTypingAction();
                                                                    break;
                                                                }
                                                                markAsReadMessages4++;
                                                                update5 = update6;
                                                                dialog_id = uid;
                                                            }
                                                            uid = dialog_id;
                                                            dialog_id = currentTime3;
                                                            if (contactsIds4 == null) {
                                                                PrintingUser newUser = new PrintingUser();
                                                                newUser.userId = markAsReadMessagesInbox3.user_id;
                                                                newUser.lastTime = dialog_id;
                                                                newUser.action = new TL_sendMessageTypingAction();
                                                                chatsDict2.add(newUser);
                                                                z = true;
                                                            }
                                                            messagesController.onlinePrivacy.put(Integer.valueOf(markAsReadMessagesInbox3.user_id), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                                        } else {
                                                            dialog_id = currentTime3;
                                                        }
                                                        i = currentTime2;
                                                        currentTime = dialog_id;
                                                    } else {
                                                        long currentTime10 = currentTime3;
                                                        if (baseUpdate instanceof TL_updateEncryptedMessagesRead) {
                                                            TL_updateEncryptedMessagesRead update7 = (TL_updateEncryptedMessagesRead) baseUpdate;
                                                            if (markAsReadEncrypted == null) {
                                                                markAsReadEncrypted3 = new SparseIntArray();
                                                            } else {
                                                                markAsReadEncrypted3 = markAsReadEncrypted;
                                                            }
                                                            markAsReadEncrypted3.put(update7.chat_id, Math.max(update7.max_date, update7.date));
                                                            tasks = tasks2;
                                                            if (tasks == null) {
                                                                tasks = new ArrayList();
                                                            }
                                                            tasks.add((TL_updateEncryptedMessagesRead) baseUpdate);
                                                            i = currentTime2;
                                                            currentTime = currentTime10;
                                                            tasks2 = tasks;
                                                            chatsDict5 = chatsDict6;
                                                            markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                            messages6 = messages2;
                                                            contactsIds4 = contactsIds;
                                                            markAsReadMessages4 = markAsReadMessages;
                                                        } else {
                                                            tasks = tasks2;
                                                            if (baseUpdate instanceof TL_updateChatParticipantAdd) {
                                                                TL_updateChatParticipantAdd update8 = (TL_updateChatParticipantAdd) baseUpdate;
                                                                MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(update8.chat_id, update8.user_id, 0, update8.inviter_id, update8.version);
                                                            } else if (baseUpdate instanceof TL_updateChatParticipantDelete) {
                                                                TL_updateChatParticipantDelete update9 = (TL_updateChatParticipantDelete) baseUpdate;
                                                                MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(update9.chat_id, update9.user_id, 1, 0, update9.version);
                                                            } else {
                                                                if (baseUpdate instanceof TL_updateDcOptions) {
                                                                    i = currentTime2;
                                                                    currentTime8 = currentTime10;
                                                                    markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                                                    chatsDict5 = chatsDict6;
                                                                } else if (baseUpdate instanceof TL_updateConfig) {
                                                                    i = currentTime2;
                                                                    currentTime8 = currentTime10;
                                                                    markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                                                    chatsDict5 = chatsDict6;
                                                                } else if (baseUpdate instanceof TL_updateEncryption) {
                                                                    SecretChatHelper.getInstance(messagesController.currentAccount).processUpdateEncryption((TL_updateEncryption) baseUpdate, concurrentHashMap);
                                                                } else if (baseUpdate instanceof TL_updateUserBlocked) {
                                                                    final TL_updateUserBlocked finalUpdate = (TL_updateUserBlocked) baseUpdate;
                                                                    if (finalUpdate.blocked) {
                                                                        ArrayList<Integer> ids = new ArrayList();
                                                                        ids.add(Integer.valueOf(finalUpdate.user_id));
                                                                        MessagesStorage.getInstance(messagesController.currentAccount).putBlockedUsers(ids, false);
                                                                    } else {
                                                                        MessagesStorage.getInstance(messagesController.currentAccount).deleteBlockedUser(finalUpdate.user_id);
                                                                    }
                                                                    MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                                                        /* renamed from: org.telegram.messenger.MessagesController$129$1 */
                                                                        class C03091 implements Runnable {
                                                                            C03091() {
                                                                            }

                                                                            public void run() {
                                                                                if (!finalUpdate.blocked) {
                                                                                    MessagesController.this.blockedUsers.remove(Integer.valueOf(finalUpdate.user_id));
                                                                                } else if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(finalUpdate.user_id))) {
                                                                                    MessagesController.this.blockedUsers.add(Integer.valueOf(finalUpdate.user_id));
                                                                                }
                                                                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                                                                            }
                                                                        }

                                                                        public void run() {
                                                                            AndroidUtilities.runOnUIThread(new C03091());
                                                                        }
                                                                    });
                                                                } else if (baseUpdate instanceof TL_updateNotifySettings) {
                                                                    if (updatesOnMainThread == null) {
                                                                        updatesOnMainThread = new ArrayList();
                                                                    } else {
                                                                        updatesOnMainThread = updatesOnMainThread;
                                                                    }
                                                                    updatesOnMainThread.add(baseUpdate);
                                                                    i = currentTime2;
                                                                    updatesOnMainThread = updatesOnMainThread;
                                                                    currentTime = currentTime10;
                                                                    tasks2 = tasks;
                                                                } else {
                                                                    if (baseUpdate instanceof TL_updateServiceNotification) {
                                                                        final TL_updateServiceNotification update10 = (TL_updateServiceNotification) baseUpdate;
                                                                        if (update10.popup && update10.message != null && update10.message.length() > 0) {
                                                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                                                public void run() {
                                                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(2), update10.message);
                                                                                }
                                                                            });
                                                                        }
                                                                        if ((update10.flags & 2) != 0) {
                                                                            LongSparseArray<ArrayList<MessageObject>> messages7;
                                                                            markAsReadMessagesInbox3 = new TL_message();
                                                                            a = UserConfig.getInstance(messagesController.currentAccount).getNewMessageId();
                                                                            markAsReadMessagesInbox3.id = a;
                                                                            markAsReadMessagesInbox3.local_id = a;
                                                                            UserConfig.getInstance(messagesController.currentAccount).saveConfig(false);
                                                                            markAsReadMessagesInbox3.unread = true;
                                                                            markAsReadMessagesInbox3.flags = 256;
                                                                            if (update10.inbox_date != 0) {
                                                                                markAsReadMessagesInbox3.date = update10.inbox_date;
                                                                            } else {
                                                                                markAsReadMessagesInbox3.date = (int) (System.currentTimeMillis() / 1000);
                                                                            }
                                                                            markAsReadMessagesInbox3.from_id = 777000;
                                                                            markAsReadMessagesInbox3.to_id = new TL_peerUser();
                                                                            markAsReadMessagesInbox3.to_id.user_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                                                                            markAsReadMessagesInbox3.dialog_id = 777000;
                                                                            if (update10.media != null) {
                                                                                markAsReadMessagesInbox3.media = update10.media;
                                                                                markAsReadMessagesInbox3.flags |= 512;
                                                                            }
                                                                            markAsReadMessagesInbox3.message = update10.message;
                                                                            if (update10.entities != null) {
                                                                                markAsReadMessagesInbox3.entities = update10.entities;
                                                                            }
                                                                            if (messagesArr2 == null) {
                                                                                messagesArr2 = new ArrayList();
                                                                            }
                                                                            messagesArr2.add(markAsReadMessagesInbox3);
                                                                            i = currentTime2;
                                                                            currentTime2 = new MessageObject(messagesController.currentAccount, (Message) markAsReadMessagesInbox3, (AbstractMap) concurrentHashMap, chatsDict6, messagesController.createdDialogIds.contains(Long.valueOf(markAsReadMessagesInbox3.dialog_id)));
                                                                            if (messages2 == null) {
                                                                                messages7 = new LongSparseArray();
                                                                            } else {
                                                                                messages7 = messages2;
                                                                            }
                                                                            ArrayList<MessageObject> arr4 = (ArrayList) messages7.get(markAsReadMessagesInbox3.dialog_id);
                                                                            if (arr4 == null) {
                                                                                arr4 = new ArrayList();
                                                                                currentTime8 = currentTime10;
                                                                                messages7.put(markAsReadMessagesInbox3.dialog_id, arr4);
                                                                            } else {
                                                                                currentTime8 = currentTime10;
                                                                            }
                                                                            arr4.add(currentTime2);
                                                                            if (pushMessages == null) {
                                                                                arr = new ArrayList();
                                                                                pushMessages = arr;
                                                                            } else {
                                                                                arr = pushMessages;
                                                                            }
                                                                            arr.add(currentTime2);
                                                                            messages2 = messages7;
                                                                        } else {
                                                                            i = currentTime2;
                                                                            currentTime8 = currentTime10;
                                                                            arr = pushMessages;
                                                                        }
                                                                        pushMessages = arr;
                                                                    } else {
                                                                        i = currentTime2;
                                                                        currentTime8 = currentTime10;
                                                                        if ((baseUpdate instanceof TL_updateDialogPinned) != null) {
                                                                            if (updatesOnMainThread == null) {
                                                                                currentTime2 = new ArrayList();
                                                                                updatesOnMainThread = currentTime2;
                                                                            } else {
                                                                                currentTime2 = updatesOnMainThread;
                                                                            }
                                                                            currentTime2.add(baseUpdate);
                                                                        } else if ((baseUpdate instanceof TL_updatePinnedDialogs) != null) {
                                                                            if (updatesOnMainThread == null) {
                                                                                currentTime2 = new ArrayList();
                                                                                updatesOnMainThread = currentTime2;
                                                                            } else {
                                                                                currentTime2 = updatesOnMainThread;
                                                                            }
                                                                            currentTime2.add(baseUpdate);
                                                                        } else if ((baseUpdate instanceof TL_updatePrivacy) != null) {
                                                                            if (updatesOnMainThread == null) {
                                                                                currentTime2 = new ArrayList();
                                                                                updatesOnMainThread = currentTime2;
                                                                            } else {
                                                                                currentTime2 = updatesOnMainThread;
                                                                            }
                                                                            currentTime2.add(baseUpdate);
                                                                        } else {
                                                                            if ((baseUpdate instanceof TL_updateWebPage) != null) {
                                                                                TL_updateWebPage currentTime11 = (TL_updateWebPage) baseUpdate;
                                                                                if (webPages4 == null) {
                                                                                    webPages2 = new LongSparseArray();
                                                                                } else {
                                                                                    webPages2 = webPages4;
                                                                                }
                                                                                webPages2.put(currentTime11.webpage.id, currentTime11.webpage);
                                                                            } else if ((baseUpdate instanceof TL_updateChannelWebPage) != null) {
                                                                                TL_updateChannelWebPage currentTime12 = (TL_updateChannelWebPage) baseUpdate;
                                                                                if (webPages4 == null) {
                                                                                    webPages2 = new LongSparseArray();
                                                                                } else {
                                                                                    webPages2 = webPages4;
                                                                                }
                                                                                webPages2.put(currentTime12.webpage.id, currentTime12.webpage);
                                                                            } else {
                                                                                if ((baseUpdate instanceof TL_updateChannelTooLong) != null) {
                                                                                    TL_updateChannelTooLong currentTime13 = (TL_updateChannelTooLong) baseUpdate;
                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                        StringBuilder stringBuilder4 = new StringBuilder();
                                                                                        stringBuilder4.append(baseUpdate);
                                                                                        stringBuilder4.append(" channelId = ");
                                                                                        stringBuilder4.append(currentTime13.channel_id);
                                                                                        FileLog.m0d(stringBuilder4.toString());
                                                                                    }
                                                                                    a = messagesController.channelsPts.get(currentTime13.channel_id);
                                                                                    if (a == 0) {
                                                                                        a = MessagesStorage.getInstance(messagesController.currentAccount).getChannelPtsSync(currentTime13.channel_id);
                                                                                        if (a == 0) {
                                                                                            chatsDict5 = chatsDict6;
                                                                                            chat = (Chat) chatsDict5.get(Integer.valueOf(currentTime13.channel_id));
                                                                                            if (chat == null || chat.min) {
                                                                                                chat = getChat(Integer.valueOf(currentTime13.channel_id));
                                                                                            }
                                                                                            if (chat == null || chat.min) {
                                                                                                chat = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(currentTime13.channel_id);
                                                                                                putChat(chat, true);
                                                                                            }
                                                                                            if (!(chat == null || chat.min)) {
                                                                                                loadUnknownChannel(chat, 0);
                                                                                            }
                                                                                        } else {
                                                                                            chatsDict5 = chatsDict6;
                                                                                            messagesController.channelsPts.put(currentTime13.channel_id, a);
                                                                                        }
                                                                                    } else {
                                                                                        chatsDict5 = chatsDict6;
                                                                                    }
                                                                                    if (a != 0) {
                                                                                        if ((currentTime13.flags & 1) == 0) {
                                                                                            getChannelDifference(currentTime13.channel_id);
                                                                                        } else if (currentTime13.pts > a) {
                                                                                            getChannelDifference(currentTime13.channel_id);
                                                                                        }
                                                                                    }
                                                                                } else {
                                                                                    chatsDict5 = chatsDict6;
                                                                                    if ((baseUpdate instanceof TL_updateReadChannelInbox) != null) {
                                                                                        SparseLongArray markAsReadMessagesInbox4;
                                                                                        SparseLongArray markAsReadMessagesInbox5;
                                                                                        TL_updateReadChannelInbox currentTime14 = (TL_updateReadChannelInbox) baseUpdate;
                                                                                        message_id = ((long) currentTime14.max_id) | (((long) currentTime14.channel_id) << 32);
                                                                                        dialog_id2 = (long) (-currentTime14.channel_id);
                                                                                        if (markAsReadMessagesInbox == null) {
                                                                                            markAsReadMessagesInbox4 = new SparseLongArray();
                                                                                        } else {
                                                                                            markAsReadMessagesInbox4 = markAsReadMessagesInbox;
                                                                                        }
                                                                                        markAsReadMessagesInbox4.put(-currentTime14.channel_id, message_id);
                                                                                        value = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(dialog_id2));
                                                                                        if (value == null) {
                                                                                            markAsReadMessagesInbox5 = markAsReadMessagesInbox4;
                                                                                            value = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(null, dialog_id2));
                                                                                        } else {
                                                                                            markAsReadMessagesInbox5 = markAsReadMessagesInbox4;
                                                                                        }
                                                                                        messagesController.dialogs_read_inbox_max.put(Long.valueOf(dialog_id2), Integer.valueOf(Math.max(value.intValue(), currentTime14.max_id)));
                                                                                        tasks2 = tasks;
                                                                                        markAsReadEncrypted3 = markAsReadEncrypted;
                                                                                        messages6 = messages2;
                                                                                        contactsIds4 = contactsIds;
                                                                                        markAsReadMessages4 = markAsReadMessages;
                                                                                        currentTime = currentTime8;
                                                                                        markAsReadMessagesInbox3 = markAsReadMessagesInbox5;
                                                                                    } else {
                                                                                        if ((baseUpdate instanceof TL_updateReadChannelOutbox) != null) {
                                                                                            TL_updateReadChannelOutbox currentTime15 = (TL_updateReadChannelOutbox) baseUpdate;
                                                                                            contactsIds4 = ((long) currentTime15.max_id) | (((long) currentTime15.channel_id) << 32);
                                                                                            dialog_id2 = (long) (-currentTime15.channel_id);
                                                                                            if (markAsReadMessagesOutbox3 == null) {
                                                                                                markAsReadMessagesOutbox3 = new SparseLongArray();
                                                                                            }
                                                                                            markAsReadMessagesOutbox3.put(-currentTime15.channel_id, contactsIds4);
                                                                                            value3 = (Integer) messagesController.dialogs_read_outbox_max.get(Long.valueOf(dialog_id2));
                                                                                            if (value3 == null) {
                                                                                                value3 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(true, dialog_id2));
                                                                                            }
                                                                                            messagesController.dialogs_read_outbox_max.put(Long.valueOf(dialog_id2), Integer.valueOf(Math.max(value3.intValue(), currentTime15.max_id)));
                                                                                        } else if ((baseUpdate instanceof TL_updateDeleteChannelMessages) != null) {
                                                                                            TL_updateDeleteChannelMessages currentTime16 = (TL_updateDeleteChannelMessages) baseUpdate;
                                                                                            if (BuildVars.LOGS_ENABLED) {
                                                                                                stringBuilder2 = new StringBuilder();
                                                                                                stringBuilder2.append(baseUpdate);
                                                                                                stringBuilder2.append(" channelId = ");
                                                                                                stringBuilder2.append(currentTime16.channel_id);
                                                                                                FileLog.m0d(stringBuilder2.toString());
                                                                                            }
                                                                                            if (deletedMessages3 == null) {
                                                                                                deletedMessages3 = new SparseArray();
                                                                                            }
                                                                                            arrayList = (ArrayList) deletedMessages3.get(currentTime16.channel_id);
                                                                                            if (arrayList == null) {
                                                                                                arrayList = new ArrayList();
                                                                                                deletedMessages3.put(currentTime16.channel_id, arrayList);
                                                                                            }
                                                                                            arrayList.addAll(currentTime16.messages);
                                                                                        } else {
                                                                                            if ((baseUpdate instanceof TL_updateChannel) != null) {
                                                                                                if (BuildVars.LOGS_ENABLED != null) {
                                                                                                    TL_updateChannel currentTime17 = (TL_updateChannel) baseUpdate;
                                                                                                    stringBuilder2 = new StringBuilder();
                                                                                                    stringBuilder2.append(baseUpdate);
                                                                                                    stringBuilder2.append(" channelId = ");
                                                                                                    stringBuilder2.append(currentTime17.channel_id);
                                                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                                                }
                                                                                                if (updatesOnMainThread == null) {
                                                                                                    currentTime2 = new ArrayList();
                                                                                                    updatesOnMainThread = currentTime2;
                                                                                                } else {
                                                                                                    currentTime2 = updatesOnMainThread;
                                                                                                }
                                                                                                currentTime2.add(baseUpdate);
                                                                                            } else if ((baseUpdate instanceof TL_updateChannelMessageViews) != null) {
                                                                                                SparseArray<SparseIntArray> channelViews3;
                                                                                                TL_updateChannelMessageViews currentTime18 = (TL_updateChannelMessageViews) baseUpdate;
                                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                                    stringBuilder2 = new StringBuilder();
                                                                                                    stringBuilder2.append(baseUpdate);
                                                                                                    stringBuilder2.append(" channelId = ");
                                                                                                    stringBuilder2.append(currentTime18.channel_id);
                                                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                                                }
                                                                                                if (channelViews2 == null) {
                                                                                                    channelViews3 = new SparseArray();
                                                                                                } else {
                                                                                                    channelViews3 = channelViews2;
                                                                                                }
                                                                                                SparseIntArray array = (SparseIntArray) channelViews3.get(currentTime18.channel_id);
                                                                                                if (array == null) {
                                                                                                    array = new SparseIntArray();
                                                                                                    channelViews3.put(currentTime18.channel_id, array);
                                                                                                }
                                                                                                array.put(currentTime18.id, currentTime18.views);
                                                                                                channelViews2 = channelViews3;
                                                                                            } else if ((baseUpdate instanceof TL_updateChatParticipantAdmin) != null) {
                                                                                                TL_updateChatParticipantAdmin currentTime19 = (TL_updateChatParticipantAdmin) baseUpdate;
                                                                                                MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(currentTime19.chat_id, currentTime19.user_id, 2, currentTime19.is_admin, currentTime19.version);
                                                                                            } else if ((baseUpdate instanceof TL_updateChatAdmins) != null) {
                                                                                                if (updatesOnMainThread == null) {
                                                                                                    currentTime2 = new ArrayList();
                                                                                                    updatesOnMainThread = currentTime2;
                                                                                                } else {
                                                                                                    currentTime2 = updatesOnMainThread;
                                                                                                }
                                                                                                currentTime2.add(baseUpdate);
                                                                                            } else if ((baseUpdate instanceof TL_updateStickerSets) != null) {
                                                                                                if (updatesOnMainThread == null) {
                                                                                                    currentTime2 = new ArrayList();
                                                                                                    updatesOnMainThread = currentTime2;
                                                                                                } else {
                                                                                                    currentTime2 = updatesOnMainThread;
                                                                                                }
                                                                                                currentTime2.add(baseUpdate);
                                                                                            } else if ((baseUpdate instanceof TL_updateStickerSetsOrder) != null) {
                                                                                                if (updatesOnMainThread == null) {
                                                                                                    currentTime2 = new ArrayList();
                                                                                                    updatesOnMainThread = currentTime2;
                                                                                                } else {
                                                                                                    currentTime2 = updatesOnMainThread;
                                                                                                }
                                                                                                currentTime2.add(baseUpdate);
                                                                                            } else if ((baseUpdate instanceof TL_updateNewStickerSet) != null) {
                                                                                                if (updatesOnMainThread == null) {
                                                                                                    currentTime2 = new ArrayList();
                                                                                                    updatesOnMainThread = currentTime2;
                                                                                                } else {
                                                                                                    currentTime2 = updatesOnMainThread;
                                                                                                }
                                                                                                currentTime2.add(baseUpdate);
                                                                                            } else if ((baseUpdate instanceof TL_updateDraftMessage) != null) {
                                                                                                if (updatesOnMainThread == null) {
                                                                                                    currentTime2 = new ArrayList();
                                                                                                    updatesOnMainThread = currentTime2;
                                                                                                } else {
                                                                                                    currentTime2 = updatesOnMainThread;
                                                                                                }
                                                                                                currentTime2.add(baseUpdate);
                                                                                            } else if ((baseUpdate instanceof TL_updateSavedGifs) != null) {
                                                                                                if (updatesOnMainThread == null) {
                                                                                                    currentTime2 = new ArrayList();
                                                                                                    updatesOnMainThread = currentTime2;
                                                                                                } else {
                                                                                                    currentTime2 = updatesOnMainThread;
                                                                                                }
                                                                                                currentTime2.add(baseUpdate);
                                                                                            } else {
                                                                                                Message message2;
                                                                                                if ((baseUpdate instanceof TL_updateEditChannelMessage) != null) {
                                                                                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                                                                                } else if ((baseUpdate instanceof TL_updateEditMessage) != null) {
                                                                                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                                                                                } else if ((baseUpdate instanceof TL_updateChannelPinnedMessage) != null) {
                                                                                                    TL_updateChannelPinnedMessage currentTime20 = (TL_updateChannelPinnedMessage) baseUpdate;
                                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                                        stringBuilder2 = new StringBuilder();
                                                                                                        stringBuilder2.append(baseUpdate);
                                                                                                        stringBuilder2.append(" channelId = ");
                                                                                                        stringBuilder2.append(currentTime20.channel_id);
                                                                                                        FileLog.m0d(stringBuilder2.toString());
                                                                                                    }
                                                                                                    MessagesStorage.getInstance(messagesController.currentAccount).updateChannelPinnedMessage(currentTime20.channel_id, currentTime20.id);
                                                                                                } else if ((baseUpdate instanceof TL_updateReadFeaturedStickers) != null) {
                                                                                                    if (updatesOnMainThread == null) {
                                                                                                        currentTime2 = new ArrayList();
                                                                                                        updatesOnMainThread = currentTime2;
                                                                                                    } else {
                                                                                                        currentTime2 = updatesOnMainThread;
                                                                                                    }
                                                                                                    currentTime2.add(baseUpdate);
                                                                                                } else if ((baseUpdate instanceof TL_updatePhoneCall) != null) {
                                                                                                    if (updatesOnMainThread == null) {
                                                                                                        currentTime2 = new ArrayList();
                                                                                                        updatesOnMainThread = currentTime2;
                                                                                                    } else {
                                                                                                        currentTime2 = updatesOnMainThread;
                                                                                                    }
                                                                                                    currentTime2.add(baseUpdate);
                                                                                                } else if ((baseUpdate instanceof TL_updateLangPack) != null) {
                                                                                                    LocaleController.getInstance().saveRemoteLocaleStrings(((TL_updateLangPack) baseUpdate).difference, messagesController.currentAccount);
                                                                                                } else if ((baseUpdate instanceof TL_updateLangPackTooLong) != null) {
                                                                                                    LocaleController.getInstance().reloadCurrentRemoteLocale(messagesController.currentAccount);
                                                                                                } else if ((baseUpdate instanceof TL_updateFavedStickers) != null) {
                                                                                                    if (updatesOnMainThread == null) {
                                                                                                        currentTime2 = new ArrayList();
                                                                                                        updatesOnMainThread = currentTime2;
                                                                                                    } else {
                                                                                                        currentTime2 = updatesOnMainThread;
                                                                                                    }
                                                                                                    currentTime2.add(baseUpdate);
                                                                                                } else if ((baseUpdate instanceof TL_updateContactsReset) != null) {
                                                                                                    if (updatesOnMainThread == null) {
                                                                                                        currentTime2 = new ArrayList();
                                                                                                        updatesOnMainThread = currentTime2;
                                                                                                    } else {
                                                                                                        currentTime2 = updatesOnMainThread;
                                                                                                    }
                                                                                                    currentTime2.add(baseUpdate);
                                                                                                } else if ((baseUpdate instanceof TL_updateChannelAvailableMessages) != null) {
                                                                                                    TL_updateChannelAvailableMessages currentTime21 = (TL_updateChannelAvailableMessages) baseUpdate;
                                                                                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                                                                                    if (markAsReadEncrypted3 == null) {
                                                                                                        markAsReadEncrypted3 = new SparseIntArray();
                                                                                                    }
                                                                                                    currentValue = markAsReadEncrypted3.get(currentTime21.channel_id);
                                                                                                    if (currentValue == 0 || currentValue < currentTime21.available_min_id) {
                                                                                                        markAsReadEncrypted3.put(currentTime21.channel_id, currentTime21.available_min_id);
                                                                                                    }
                                                                                                    tasks2 = tasks;
                                                                                                    clearHistoryMessages2 = markAsReadEncrypted3;
                                                                                                    markAsReadEncrypted3 = markAsReadEncrypted;
                                                                                                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                                                                    messages6 = messages2;
                                                                                                    contactsIds4 = contactsIds;
                                                                                                    markAsReadMessages4 = markAsReadMessages;
                                                                                                    currentTime = currentTime8;
                                                                                                } else {
                                                                                                    markAsReadEncrypted3 = clearHistoryMessages2;
                                                                                                    markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                                                                                    tasks2 = tasks;
                                                                                                    clearHistoryMessages2 = markAsReadEncrypted3;
                                                                                                    markAsReadEncrypted3 = markAsReadEncrypted;
                                                                                                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                                                                    messages6 = messages2;
                                                                                                    contactsIds4 = contactsIds;
                                                                                                    markAsReadMessages4 = markAsReadMessages;
                                                                                                    currentTime = currentTime8;
                                                                                                    markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                                                                                                }
                                                                                                currentTime2 = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                                                                                                if (baseUpdate instanceof TL_updateEditChannelMessage) {
                                                                                                    message2 = ((TL_updateEditChannelMessage) baseUpdate).message;
                                                                                                    Chat chat3 = (Chat) chatsDict5.get(Integer.valueOf(message2.to_id.channel_id));
                                                                                                    if (chat3 == null) {
                                                                                                        chat3 = getChat(Integer.valueOf(message2.to_id.channel_id));
                                                                                                    }
                                                                                                    if (chat3 == null) {
                                                                                                        chat3 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(message2.to_id.channel_id);
                                                                                                        putChat(chat3, true);
                                                                                                    }
                                                                                                    if (chat3 != null && chat3.megagroup) {
                                                                                                        message2.flags |= Integer.MIN_VALUE;
                                                                                                    }
                                                                                                } else {
                                                                                                    message2 = ((TL_updateEditMessage) baseUpdate).message;
                                                                                                    if (message2.dialog_id == ((long) currentTime2)) {
                                                                                                        message2.unread = false;
                                                                                                        message2.media_unread = false;
                                                                                                        message2.out = true;
                                                                                                    }
                                                                                                }
                                                                                                if (!message2.out && message2.from_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                                                                                                    message2.out = true;
                                                                                                }
                                                                                                if (!fromGetDifference) {
                                                                                                    chat_id = 0;
                                                                                                    a = message2.entities.size();
                                                                                                    while (chat_id < a) {
                                                                                                        int i3;
                                                                                                        MessageEntity entity = (MessageEntity) message2.entities.get(chat_id);
                                                                                                        if (entity instanceof TL_messageEntityMentionName) {
                                                                                                            a = ((TL_messageEntityMentionName) entity).user_id;
                                                                                                            markAsReadMessages4 = (User) concurrentHashMap.get(Integer.valueOf(a));
                                                                                                            if (markAsReadMessages4 != null) {
                                                                                                                i3 = a;
                                                                                                                if (markAsReadMessages4.min != 0) {
                                                                                                                }
                                                                                                                if (markAsReadMessages4 == null || markAsReadMessages4.min != 0) {
                                                                                                                    a = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(a);
                                                                                                                    if (!(a == 0 || a.min == null)) {
                                                                                                                        a = 0;
                                                                                                                    }
                                                                                                                    markAsReadMessages4 = a;
                                                                                                                    putUser(markAsReadMessages4, 1);
                                                                                                                }
                                                                                                                if (markAsReadMessages4 == null) {
                                                                                                                    return false;
                                                                                                                }
                                                                                                            } else {
                                                                                                                i3 = a;
                                                                                                            }
                                                                                                            markAsReadMessages4 = getUser(Integer.valueOf(a));
                                                                                                            a = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(a);
                                                                                                            a = 0;
                                                                                                            markAsReadMessages4 = a;
                                                                                                            putUser(markAsReadMessages4, 1);
                                                                                                            if (markAsReadMessages4 == null) {
                                                                                                                return false;
                                                                                                            }
                                                                                                        } else {
                                                                                                            i3 = a;
                                                                                                        }
                                                                                                        chat_id++;
                                                                                                        a = i3;
                                                                                                    }
                                                                                                }
                                                                                                if (message2.to_id.chat_id != 0) {
                                                                                                    message2.dialog_id = (long) (-message2.to_id.chat_id);
                                                                                                } else if (message2.to_id.channel_id != 0) {
                                                                                                    message2.dialog_id = (long) (-message2.to_id.channel_id);
                                                                                                } else {
                                                                                                    if (message2.to_id.user_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                                                                                                        message2.to_id.user_id = message2.from_id;
                                                                                                    }
                                                                                                    message2.dialog_id = (long) message2.to_id.user_id;
                                                                                                }
                                                                                                ConcurrentHashMap<Long, Integer> read_max2 = message2.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                                                                                                value3 = (Integer) read_max2.get(Long.valueOf(message2.dialog_id));
                                                                                                if (value3 == null) {
                                                                                                    markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                                                                                    value3 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message2.out, message2.dialog_id));
                                                                                                    read_max2.put(Long.valueOf(message2.dialog_id), value3);
                                                                                                } else {
                                                                                                    markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                                                                                }
                                                                                                message2.unread = value3.intValue() < message2.id;
                                                                                                if (message2.dialog_id == ((long) currentTime2)) {
                                                                                                    message2.out = true;
                                                                                                    message2.unread = false;
                                                                                                    message2.media_unread = false;
                                                                                                }
                                                                                                if (message2.out && message2.message == null) {
                                                                                                    message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                                                                                                    message2.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                                                                                                }
                                                                                                ImageLoader.saveMessageThumbs(message2);
                                                                                                messageObject = new MessageObject(messagesController.currentAccount, message2, (AbstractMap) concurrentHashMap, chatsDict5, messagesController.createdDialogIds.contains(Long.valueOf(message2.dialog_id)));
                                                                                                if (editingMessages == null) {
                                                                                                    messages5 = new LongSparseArray();
                                                                                                } else {
                                                                                                    messages5 = editingMessages;
                                                                                                }
                                                                                                arr = (ArrayList) messages5.get(message2.dialog_id);
                                                                                                if (arr == null) {
                                                                                                    arr = new ArrayList();
                                                                                                    messages5.put(message2.dialog_id, arr);
                                                                                                }
                                                                                                arr.add(messageObject);
                                                                                                editingMessages = messages5;
                                                                                                tasks2 = tasks;
                                                                                                clearHistoryMessages2 = markAsReadEncrypted3;
                                                                                                markAsReadEncrypted3 = markAsReadEncrypted;
                                                                                                markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                                                                messages6 = messages2;
                                                                                                contactsIds4 = contactsIds;
                                                                                                markAsReadMessages4 = markAsReadMessages;
                                                                                                currentTime = currentTime8;
                                                                                                markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                                                                                            }
                                                                                            updatesOnMainThread = currentTime2;
                                                                                        }
                                                                                        tasks2 = tasks;
                                                                                        markAsReadEncrypted3 = markAsReadEncrypted;
                                                                                        markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                                                        messages6 = messages2;
                                                                                        contactsIds4 = contactsIds;
                                                                                        markAsReadMessages4 = markAsReadMessages;
                                                                                        currentTime = currentTime8;
                                                                                    }
                                                                                }
                                                                                markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                                                                markAsReadEncrypted3 = clearHistoryMessages2;
                                                                                tasks2 = tasks;
                                                                                clearHistoryMessages2 = markAsReadEncrypted3;
                                                                                markAsReadEncrypted3 = markAsReadEncrypted;
                                                                                markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                                                messages6 = messages2;
                                                                                contactsIds4 = contactsIds;
                                                                                markAsReadMessages4 = markAsReadMessages;
                                                                                currentTime = currentTime8;
                                                                                markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                                                                            }
                                                                            webPages4 = webPages2;
                                                                        }
                                                                        updatesOnMainThread = currentTime2;
                                                                    }
                                                                    tasks2 = tasks;
                                                                    markAsReadEncrypted3 = markAsReadEncrypted;
                                                                    chatsDict5 = chatsDict6;
                                                                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                                    messages6 = messages2;
                                                                    contactsIds4 = contactsIds;
                                                                    markAsReadMessages4 = markAsReadMessages;
                                                                    currentTime = currentTime8;
                                                                }
                                                                ConnectionsManager.getInstance(messagesController.currentAccount).updateDcSettings();
                                                                tasks2 = tasks;
                                                                clearHistoryMessages2 = markAsReadEncrypted3;
                                                                markAsReadEncrypted3 = markAsReadEncrypted;
                                                                markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                                messages6 = messages2;
                                                                contactsIds4 = contactsIds;
                                                                markAsReadMessages4 = markAsReadMessages;
                                                                currentTime = currentTime8;
                                                                markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                                                            }
                                                            i = currentTime2;
                                                            currentTime8 = currentTime10;
                                                            markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
                                                            markAsReadEncrypted3 = clearHistoryMessages2;
                                                            chatsDict5 = chatsDict6;
                                                            tasks2 = tasks;
                                                            clearHistoryMessages2 = markAsReadEncrypted3;
                                                            markAsReadEncrypted3 = markAsReadEncrypted;
                                                            markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                            messages6 = messages2;
                                                            contactsIds4 = contactsIds;
                                                            markAsReadMessages4 = markAsReadMessages;
                                                            currentTime = currentTime8;
                                                            markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
                                                        }
                                                    }
                                                    markAsReadEncrypted3 = markAsReadEncrypted;
                                                    chatsDict5 = chatsDict6;
                                                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                                    messages6 = messages2;
                                                    contactsIds4 = contactsIds;
                                                    markAsReadMessages4 = markAsReadMessages;
                                                }
                                            }
                                            i = currentTime2;
                                            markAsReadEncrypted3 = markAsReadEncrypted;
                                            currentTime = currentTime3;
                                            chatsDict5 = chatsDict6;
                                            markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                            contactsIds4 = contactsIds;
                                            markAsReadMessages4 = markAsReadMessages;
                                        }
                                        updatesOnMainThread = currentTime2;
                                    }
                                    i = chatsDict2;
                                    markAsReadEncrypted3 = markAsReadEncrypted;
                                    currentTime = currentTime3;
                                    chatsDict5 = chatsDict6;
                                    markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                                    concurrentHashMap = usersDict;
                                    messages6 = messages2;
                                    contactsIds4 = contactsIds;
                                }
                                if ((baseUpdate instanceof TL_updateUserTyping) != null) {
                                    TL_updateUserTyping currentTime22 = (TL_updateUserTyping) baseUpdate;
                                    chatsDict7 = currentTime22.user_id;
                                    size = 0;
                                    currentTime2 = currentTime22.action;
                                } else {
                                    TL_updateChatUserTyping currentTime23 = (TL_updateChatUserTyping) baseUpdate;
                                    a = currentTime23.chat_id;
                                    size = currentTime23.user_id;
                                    currentTime2 = currentTime23.action;
                                    int i4 = size;
                                    size = a;
                                    chatsDict7 = i4;
                                }
                                if (chatsDict7 != UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                                    message_id = (long) (-size);
                                    if (message_id == 0) {
                                        message_id = (long) chatsDict7;
                                    }
                                    ArrayList<PrintingUser> arr5 = (ArrayList) messagesController.printingUsers.get(Long.valueOf(message_id));
                                    long uid2;
                                    if (!(currentTime2 instanceof TL_sendMessageCancelAction)) {
                                        PrintingUser newUser2;
                                        deletedMessages = deletedMessages3;
                                        if (arr5 == null) {
                                            arr5 = new ArrayList();
                                            messagesController.printingUsers.put(Long.valueOf(message_id), arr5);
                                        }
                                        printChanged = false;
                                        Iterator it = arr5.iterator();
                                        while (it.hasNext() != null) {
                                            PrintingUser deletedMessages4 = (PrintingUser) it.next();
                                            if (deletedMessages4.userId == chatsDict7) {
                                                printChanged = true;
                                                contactsIds4 = currentTime8;
                                                deletedMessages4.lastTime = contactsIds4;
                                                if (deletedMessages4.action.getClass() != currentTime2.getClass()) {
                                                    z = true;
                                                }
                                                deletedMessages4.action = currentTime2;
                                                if (!printChanged) {
                                                    newUser2 = new PrintingUser();
                                                    newUser2.userId = chatsDict7;
                                                    newUser2.lastTime = contactsIds4;
                                                    newUser2.action = currentTime2;
                                                    arr5.add(newUser2);
                                                    z = true;
                                                }
                                            } else {
                                                uid2 = message_id;
                                                message_id = currentTime8;
                                                message_id = uid2;
                                            }
                                        }
                                        contactsIds4 = currentTime8;
                                        if (printChanged) {
                                            newUser2 = new PrintingUser();
                                            newUser2.userId = chatsDict7;
                                            newUser2.lastTime = contactsIds4;
                                            newUser2.action = currentTime2;
                                            arr5.add(newUser2);
                                            z = true;
                                        }
                                    } else if (arr5 != null) {
                                        chat_id = 0;
                                        int size11 = arr5.size();
                                        while (chat_id < size11) {
                                            int chat_id4 = size;
                                            deletedMessages = deletedMessages3;
                                            if (((PrintingUser) arr5.get(chat_id)).userId == chatsDict7) {
                                                arr5.remove(chat_id);
                                                z = true;
                                                break;
                                            }
                                            chat_id++;
                                            size = chat_id4;
                                            deletedMessages3 = deletedMessages;
                                        }
                                        deletedMessages = deletedMessages3;
                                        if (arr5.isEmpty()) {
                                            messagesController.printingUsers.remove(Long.valueOf(message_id));
                                        }
                                        uid2 = message_id;
                                        contactsIds4 = currentTime8;
                                    } else {
                                        deletedMessages = deletedMessages3;
                                        uid2 = message_id;
                                        contactsIds4 = currentTime8;
                                    }
                                    messagesController.onlinePrivacy.put(Integer.valueOf(chatsDict7), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                } else {
                                    deletedMessages = deletedMessages3;
                                    contactsIds4 = currentTime8;
                                }
                            }
                            markAsReadEncrypted3 = markAsReadEncrypted;
                            currentTime = currentTime3;
                            chatsDict5 = chatsDict6;
                            markAsReadMessagesInbox3 = markAsReadMessagesInbox;
                        }
                        concurrentHashMap = usersDict;
                        messages6 = messages2;
                        i = interfaceUpdateMask2;
                        contactsIds4 = contactsIds;
                    }
                }
                size = c + 1;
                chatsDict4 = chatsDict5;
                size32 = size33;
                interfaceUpdateMask = i;
                currentTime2 = currentTime;
            }
            currentTime = contactsIds4;
            tasks2 = messages6;
            clearHistoryMessages2 = markAsReadEncrypted3;
            markAsReadEncrypted3 = markAsReadEncrypted;
            markAsReadMessagesInbox3 = markAsReadMessagesInbox;
            messages6 = messages2;
            contactsIds4 = contactsIds;
            markAsReadMessages4 = markAsReadMessages;
            markAsReadMessagesOutbox3 = markAsReadMessagesOutbox;
            deletedMessages3 = deletedMessages;
            size = c + 1;
            chatsDict4 = chatsDict5;
            size32 = size33;
            interfaceUpdateMask = i;
            currentTime2 = currentTime;
        }
        currentTime = currentTime2;
        markAsReadMessagesInbox = markAsReadMessagesInbox3;
        deletedMessages = deletedMessages3;
        i = interfaceUpdateMask;
        contactsIds = contactsIds4;
        markAsReadMessages = markAsReadMessages4;
        markAsReadMessagesOutbox = markAsReadMessagesOutbox3;
        messages2 = messages6;
        markAsReadEncrypted = markAsReadEncrypted3;
        markAsReadEncrypted3 = clearHistoryMessages2;
        tasks = tasks2;
        chatsDict2 = chatsDict4;
        if (messages2 != null) {
            messages4 = messages2;
            currentValue = messages4.size();
            for (int a3 = 0; a3 < currentValue; a3++) {
                if (updatePrintingUsersWithNewMessages(messages4.keyAt(a3), (ArrayList) messages4.valueAt(a3))) {
                    z = true;
                }
            }
        } else {
            messages4 = messages2;
        }
        if (z) {
            updatePrintingStrings();
        }
        SparseLongArray markAsReadMessagesInbox6 = markAsReadMessagesInbox;
        chat_id = i;
        LongSparseArray<ArrayList<MessageObject>> editingMessages2 = editingMessages;
        LongSparseArray<WebPage> webPages5 = webPages4;
        SparseArray<ArrayList<Integer>> deletedMessages5 = deletedMessages;
        final boolean printChangedArg = z;
        if (contactsIds != null) {
            contactsIds2 = contactsIds;
            ContactsController.getInstance(messagesController.currentAccount).processContactsUpdates(contactsIds2, concurrentHashMap);
        } else {
            contactsIds2 = contactsIds;
        }
        if (pushMessages != null) {
            arr = pushMessages;
            markAsReadMessagesInbox2 = markAsReadMessagesInbox6;
            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$131$1 */
                class C03101 implements Runnable {
                    C03101() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arr, true, false);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03101());
                }
            });
        } else {
            markAsReadMessagesInbox2 = markAsReadMessagesInbox6;
        }
        if (messagesArr2 != null) {
            contactsIds3 = contactsIds2;
            StatsController.getInstance(messagesController.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, messagesArr2.size());
            MessagesStorage.getInstance(messagesController.currentAccount).putMessages((ArrayList) messagesArr2, true, true, false, DownloadController.getInstance(messagesController.currentAccount).getAutodownloadMask());
        } else {
            contactsIds3 = contactsIds2;
        }
        if (editingMessages2 != null) {
            currentValue = 0;
            size = editingMessages2.size();
            while (currentValue < size) {
                messages_Messages messagesRes = new TL_messages_messages();
                ArrayList<MessageObject> messageObjects = (ArrayList) editingMessages2.valueAt(currentValue);
                int size22 = messageObjects.size();
                int size12 = size;
                size = 0;
                while (true) {
                    messagesArr = messagesArr2;
                    size2 = size22;
                    if (size >= size2) {
                        break;
                    }
                    int size23 = size2;
                    chatsDict3 = chatsDict2;
                    messagesRes.messages.add(((MessageObject) messageObjects.get(size)).messageOwner);
                    size++;
                    messagesArr2 = messagesArr;
                    size22 = size23;
                    chatsDict2 = chatsDict3;
                }
                MessagesStorage.getInstance(messagesController.currentAccount).putMessages(messagesRes, editingMessages2.keyAt(currentValue), -2, 0, false);
                currentValue++;
                size = size12;
                messagesArr2 = messagesArr;
            }
        }
        messagesArr = messagesArr2;
        chatsDict3 = chatsDict2;
        if (channelViews2 != null) {
            channelViews = channelViews2;
            z2 = true;
            MessagesStorage.getInstance(messagesController.currentAccount).putChannelViews(channelViews, true);
        } else {
            channelViews = channelViews2;
            z2 = true;
        }
        final LongSparseArray<ArrayList<MessageObject>> editingMessagesFinal2 = editingMessages2;
        final SparseArray<SparseIntArray> channelViewsFinal = channelViews;
        webPages3 = webPages5;
        boolean z3 = z2;
        messages3 = messages4;
        SparseArray<ArrayList<Integer>> deletedMessages6 = deletedMessages5;
        final ArrayList<ChatParticipants> chatInfoToUpdateFinal = chatInfoToUpdate;
        channelViews2 = channelViews;
        final ArrayList<Integer> contactsIdsFinal = contactsIds3;
        LongSparseArray<WebPage> webPages6 = webPages5;
        final ArrayList<Update> updatesOnMainThreadFinal = updatesOnMainThread;
        LongSparseArray<ArrayList<MessageObject>> messages8 = messages4;
        editingMessages = editingMessages2;
        ArrayList<Long> markAsReadMessages5 = markAsReadMessages;
        SparseLongArray markAsReadMessagesInbox7 = markAsReadMessagesInbox2;
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$132$3 */
            class C18143 implements RequestDelegate {
                C18143() {
                }

                public void run(TLObject response, TL_error error) {
                    if (response != null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            }

            public void run() {
                int a;
                int size;
                long did;
                boolean hasDraftUpdates;
                long dialog_id;
                long j;
                int i;
                int updateMask = chat_id;
                boolean hasDraftUpdates2 = false;
                int i2 = 2;
                boolean z = false;
                int i3 = 1;
                if (updatesOnMainThreadFinal != null) {
                    ArrayList<User> arrayList;
                    ArrayList<User> dbUsers = new ArrayList();
                    ArrayList<User> dbUsersStatus = new ArrayList();
                    Editor editor = null;
                    a = 0;
                    size = updatesOnMainThreadFinal.size();
                    while (a < size) {
                        int i4;
                        Update baseUpdate = (Update) updatesOnMainThreadFinal.get(a);
                        if (baseUpdate instanceof TL_updatePrivacy) {
                            TL_updatePrivacy update = (TL_updatePrivacy) baseUpdate;
                            if (update.key instanceof TL_privacyKeyStatusTimestamp) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(update.rules, z);
                            } else if (update.key instanceof TL_privacyKeyChatInvite) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(update.rules, i3);
                            } else if (update.key instanceof TL_privacyKeyPhoneCall) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(update.rules, i2);
                            }
                        } else if (baseUpdate instanceof TL_updateUserStatus) {
                            TL_updateUserStatus update2 = (TL_updateUserStatus) baseUpdate;
                            User currentUser = MessagesController.this.getUser(Integer.valueOf(update2.user_id));
                            if (update2.status instanceof TL_userStatusRecently) {
                                update2.status.expires = -100;
                            } else if (update2.status instanceof TL_userStatusLastWeek) {
                                update2.status.expires = -101;
                            } else if (update2.status instanceof TL_userStatusLastMonth) {
                                update2.status.expires = -102;
                            }
                            if (currentUser != null) {
                                currentUser.id = update2.user_id;
                                currentUser.status = update2.status;
                            }
                            toDbUser = new TL_user();
                            toDbUser.id = update2.user_id;
                            toDbUser.status = update2.status;
                            dbUsersStatus.add(toDbUser);
                            if (update2.user_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                                NotificationsController.getInstance(MessagesController.this.currentAccount).setLastOnlineFromOtherDevice(update2.status.expires);
                            }
                        } else if (baseUpdate instanceof TL_updateUserName) {
                            TL_updateUserName update3 = (TL_updateUserName) baseUpdate;
                            toDbUser = MessagesController.this.getUser(Integer.valueOf(update3.user_id));
                            if (toDbUser != null) {
                                if (!UserObject.isContact(toDbUser)) {
                                    toDbUser.first_name = update3.first_name;
                                    toDbUser.last_name = update3.last_name;
                                }
                                if (!TextUtils.isEmpty(toDbUser.username)) {
                                    MessagesController.this.objectsByUsernames.remove(toDbUser.username);
                                }
                                if (TextUtils.isEmpty(update3.username)) {
                                    MessagesController.this.objectsByUsernames.put(update3.username, toDbUser);
                                }
                                toDbUser.username = update3.username;
                            }
                            User toDbUser = new TL_user();
                            toDbUser.id = update3.user_id;
                            toDbUser.first_name = update3.first_name;
                            toDbUser.last_name = update3.last_name;
                            toDbUser.username = update3.username;
                            dbUsers.add(toDbUser);
                        } else if (baseUpdate instanceof TL_updateDialogPinned) {
                            TL_updateDialogPinned updateDialogPinned = (TL_updateDialogPinned) baseUpdate;
                            if (updateDialogPinned.peer instanceof TL_dialogPeer) {
                                Peer peer = ((TL_dialogPeer) updateDialogPinned.peer).peer;
                                if (peer instanceof TL_peerUser) {
                                    did = (long) peer.user_id;
                                } else if (peer instanceof TL_peerChat) {
                                    did = (long) (-peer.chat_id);
                                } else {
                                    did = (long) (-peer.channel_id);
                                }
                            } else {
                                did = 0;
                            }
                            if (!MessagesController.this.pinDialog(did, updateDialogPinned.pinned, null, -1)) {
                                UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = z;
                                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(z);
                                MessagesController.this.loadPinnedDialogs(did, null);
                            }
                        } else {
                            int size2;
                            if (baseUpdate instanceof TL_updatePinnedDialogs) {
                                ArrayList<Long> order;
                                TL_updatePinnedDialogs update4 = (TL_updatePinnedDialogs) baseUpdate;
                                UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = z;
                                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(z);
                                TL_updatePinnedDialogs update5;
                                if ((update4.flags & 1) != 0) {
                                    order = new ArrayList();
                                    ArrayList<DialogPeer> peers = ((TL_updatePinnedDialogs) baseUpdate).order;
                                    int size22 = peers.size();
                                    int b = 0;
                                    while (true) {
                                        size2 = size22;
                                        if (b >= size2) {
                                            break;
                                        }
                                        int i5;
                                        DialogPeer dialogPeer = (DialogPeer) peers.get(b);
                                        hasDraftUpdates = hasDraftUpdates2;
                                        DialogPeer dialogPeer2;
                                        if (dialogPeer instanceof TL_dialogPeer) {
                                            hasDraftUpdates2 = ((TL_dialogPeer) dialogPeer).peer;
                                            update5 = update4;
                                            if (hasDraftUpdates2.user_id != null) {
                                                i5 = size2;
                                                did = (long) hasDraftUpdates2.user_id;
                                            } else {
                                                i5 = size2;
                                                dialogPeer2 = dialogPeer;
                                                if (hasDraftUpdates2.chat_id != 0) {
                                                    did = (long) (-hasDraftUpdates2.chat_id);
                                                } else {
                                                    did = (long) (-hasDraftUpdates2.channel_id);
                                                }
                                            }
                                        } else {
                                            update5 = update4;
                                            i5 = size2;
                                            dialogPeer2 = dialogPeer;
                                            did = 0;
                                        }
                                        order.add(Long.valueOf(did));
                                        b++;
                                        hasDraftUpdates2 = hasDraftUpdates;
                                        update4 = update5;
                                        size22 = i5;
                                    }
                                    hasDraftUpdates = hasDraftUpdates2;
                                    update5 = update4;
                                } else {
                                    hasDraftUpdates = hasDraftUpdates2;
                                    update5 = update4;
                                    order = null;
                                }
                                MessagesController.this.loadPinnedDialogs(0, order);
                            } else {
                                hasDraftUpdates = hasDraftUpdates2;
                                User currentUser2;
                                if (baseUpdate instanceof TL_updateUserPhoto) {
                                    TL_updateUserPhoto update6 = (TL_updateUserPhoto) baseUpdate;
                                    currentUser2 = MessagesController.this.getUser(Integer.valueOf(update6.user_id));
                                    if (currentUser2 != null) {
                                        currentUser2.photo = update6.photo;
                                    }
                                    toDbUser = new TL_user();
                                    toDbUser.id = update6.user_id;
                                    toDbUser.photo = update6.photo;
                                    dbUsers.add(toDbUser);
                                } else if (baseUpdate instanceof TL_updateUserPhone) {
                                    TL_updateUserPhone update7 = (TL_updateUserPhone) baseUpdate;
                                    currentUser2 = MessagesController.this.getUser(Integer.valueOf(update7.user_id));
                                    if (currentUser2 != null) {
                                        currentUser2.phone = update7.phone;
                                        Utilities.phoneBookQueue.postRunnable(new Runnable() {
                                            public void run() {
                                                ContactsController.getInstance(MessagesController.this.currentAccount).addContactToPhoneBook(currentUser2, true);
                                            }
                                        });
                                    }
                                    toDbUser = new TL_user();
                                    toDbUser.id = update7.user_id;
                                    toDbUser.phone = update7.phone;
                                    dbUsers.add(toDbUser);
                                } else {
                                    if (baseUpdate instanceof TL_updateNotifySettings) {
                                        TL_updateNotifySettings update8 = (TL_updateNotifySettings) baseUpdate;
                                        if ((update8.notify_settings instanceof TL_peerNotifySettings) && (update8.peer instanceof TL_notifyPeer)) {
                                            TL_dialog dialog;
                                            StringBuilder stringBuilder;
                                            StringBuilder stringBuilder2;
                                            StringBuilder stringBuilder3;
                                            if (editor == null) {
                                                editor = MessagesController.this.notificationsPreferences.edit();
                                            }
                                            if (update8.peer.peer.user_id != 0) {
                                                dialog_id = (long) update8.peer.peer.user_id;
                                            } else if (update8.peer.peer.chat_id != 0) {
                                                dialog_id = (long) (-update8.peer.peer.chat_id);
                                            } else {
                                                dialog_id = (long) (-update8.peer.peer.channel_id);
                                                dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(dialog_id);
                                                if (dialog != null) {
                                                    dialog.notify_settings = update8.notify_settings;
                                                }
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("silent_");
                                                stringBuilder.append(dialog_id);
                                                editor.putBoolean(stringBuilder.toString(), update8.notify_settings.silent);
                                                i3 = ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime();
                                                if (update8.notify_settings.mute_until <= i3) {
                                                    size2 = 0;
                                                    i4 = size;
                                                    if (update8.notify_settings.mute_until <= i3 + 31536000) {
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append("notify2_");
                                                        stringBuilder2.append(dialog_id);
                                                        editor.putInt(stringBuilder2.toString(), 2);
                                                        if (dialog != null) {
                                                            dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                                        }
                                                    } else {
                                                        size2 = update8.notify_settings.mute_until;
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append("notify2_");
                                                        stringBuilder2.append(dialog_id);
                                                        editor.putInt(stringBuilder2.toString(), 3);
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append("notifyuntil_");
                                                        stringBuilder2.append(dialog_id);
                                                        editor.putInt(stringBuilder2.toString(), update8.notify_settings.mute_until);
                                                        if (dialog != null) {
                                                            dialog.notify_settings.mute_until = size2;
                                                        }
                                                    }
                                                    arrayList = dbUsers;
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(dialog_id, (((long) size2) << 32) | 1);
                                                    NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(dialog_id);
                                                } else {
                                                    arrayList = dbUsers;
                                                    i4 = size;
                                                    if (dialog != null) {
                                                        dialog.notify_settings.mute_until = 0;
                                                    }
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append("notify2_");
                                                    stringBuilder3.append(dialog_id);
                                                    editor.remove(stringBuilder3.toString());
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(dialog_id, 0);
                                                }
                                            }
                                            dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(dialog_id);
                                            if (dialog != null) {
                                                dialog.notify_settings = update8.notify_settings;
                                            }
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("silent_");
                                            stringBuilder.append(dialog_id);
                                            editor.putBoolean(stringBuilder.toString(), update8.notify_settings.silent);
                                            i3 = ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime();
                                            if (update8.notify_settings.mute_until <= i3) {
                                                arrayList = dbUsers;
                                                i4 = size;
                                                if (dialog != null) {
                                                    dialog.notify_settings.mute_until = 0;
                                                }
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append("notify2_");
                                                stringBuilder3.append(dialog_id);
                                                editor.remove(stringBuilder3.toString());
                                                MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(dialog_id, 0);
                                            } else {
                                                size2 = 0;
                                                i4 = size;
                                                if (update8.notify_settings.mute_until <= i3 + 31536000) {
                                                    size2 = update8.notify_settings.mute_until;
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("notify2_");
                                                    stringBuilder2.append(dialog_id);
                                                    editor.putInt(stringBuilder2.toString(), 3);
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("notifyuntil_");
                                                    stringBuilder2.append(dialog_id);
                                                    editor.putInt(stringBuilder2.toString(), update8.notify_settings.mute_until);
                                                    if (dialog != null) {
                                                        dialog.notify_settings.mute_until = size2;
                                                    }
                                                } else {
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("notify2_");
                                                    stringBuilder2.append(dialog_id);
                                                    editor.putInt(stringBuilder2.toString(), 2);
                                                    if (dialog != null) {
                                                        dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                                    }
                                                }
                                                arrayList = dbUsers;
                                                MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(dialog_id, (((long) size2) << 32) | 1);
                                                NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(dialog_id);
                                            }
                                        } else {
                                            arrayList = dbUsers;
                                            i4 = size;
                                        }
                                    } else {
                                        arrayList = dbUsers;
                                        i4 = size;
                                        if (baseUpdate instanceof TL_updateChannel) {
                                            final TL_updateChannel update9 = (TL_updateChannel) baseUpdate;
                                            TL_dialog dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(-((long) update9.channel_id));
                                            Chat chat = MessagesController.this.getChat(Integer.valueOf(update9.channel_id));
                                            if (chat != null) {
                                                if (dialog2 == null && (chat instanceof TL_channel) && !chat.left) {
                                                    Utilities.stageQueue.postRunnable(new Runnable() {
                                                        public void run() {
                                                            MessagesController.this.getChannelDifference(update9.channel_id, 1, 0, null);
                                                        }
                                                    });
                                                } else if (chat.left && dialog2 != null) {
                                                    size = 0;
                                                    MessagesController.this.deleteDialog(dialog2.id, 0);
                                                    updateMask |= MessagesController.UPDATE_MASK_CHANNEL;
                                                    MessagesController.this.loadFullChat(update9.channel_id, size, true);
                                                }
                                            }
                                            size = 0;
                                            updateMask |= MessagesController.UPDATE_MASK_CHANNEL;
                                            MessagesController.this.loadFullChat(update9.channel_id, size, true);
                                        } else if (baseUpdate instanceof TL_updateChatAdmins) {
                                            updateMask |= MessagesController.UPDATE_MASK_CHAT_ADMINS;
                                        } else {
                                            if (baseUpdate instanceof TL_updateStickerSets) {
                                                TL_updateStickerSets update10 = (TL_updateStickerSets) baseUpdate;
                                                DataQuery.getInstance(MessagesController.this.currentAccount).loadStickers(0, false, true);
                                            } else if (baseUpdate instanceof TL_updateStickerSetsOrder) {
                                                DataQuery.getInstance(MessagesController.this.currentAccount).reorderStickers(((TL_updateStickerSetsOrder) baseUpdate).masks, ((TL_updateStickerSetsOrder) baseUpdate).order);
                                            } else if (baseUpdate instanceof TL_updateFavedStickers) {
                                                DataQuery.getInstance(MessagesController.this.currentAccount).loadRecents(2, false, false, true);
                                            } else if (baseUpdate instanceof TL_updateContactsReset) {
                                                ContactsController.getInstance(MessagesController.this.currentAccount).forceImportContacts();
                                            } else if (baseUpdate instanceof TL_updateNewStickerSet) {
                                                DataQuery.getInstance(MessagesController.this.currentAccount).addNewStickerSet(((TL_updateNewStickerSet) baseUpdate).stickerset);
                                            } else if (baseUpdate instanceof TL_updateSavedGifs) {
                                                MessagesController.this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).commit();
                                            } else if (baseUpdate instanceof TL_updateRecentStickers) {
                                                MessagesController.this.emojiPreferences.edit().putLong("lastStickersLoadTime", 0).commit();
                                            } else if (baseUpdate instanceof TL_updateDraftMessage) {
                                                TL_updateDraftMessage update11 = (TL_updateDraftMessage) baseUpdate;
                                                Peer peer2 = ((TL_updateDraftMessage) baseUpdate).peer;
                                                if (peer2.user_id != 0) {
                                                    j = (long) peer2.user_id;
                                                } else if (peer2.channel_id != 0) {
                                                    j = (long) (-peer2.channel_id);
                                                } else {
                                                    j = (long) (-peer2.chat_id);
                                                }
                                                DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(j, update11.draft, null, true);
                                                hasDraftUpdates2 = true;
                                                a++;
                                                size = i4;
                                                dbUsers = arrayList;
                                                i2 = 2;
                                                z = false;
                                                i3 = 1;
                                            } else if (baseUpdate instanceof TL_updateReadFeaturedStickers) {
                                                DataQuery.getInstance(MessagesController.this.currentAccount).markFaturedStickersAsRead(false);
                                            } else if (baseUpdate instanceof TL_updatePhoneCall) {
                                                dbUsers = ((TL_updatePhoneCall) baseUpdate).phone_call;
                                                VoIPService svc = VoIPService.getSharedInstance();
                                                if (BuildVars.LOGS_ENABLED) {
                                                    StringBuilder stringBuilder4 = new StringBuilder();
                                                    stringBuilder4.append("Received call in update: ");
                                                    stringBuilder4.append(dbUsers);
                                                    FileLog.m0d(stringBuilder4.toString());
                                                    stringBuilder4 = new StringBuilder();
                                                    stringBuilder4.append("call id ");
                                                    stringBuilder4.append(dbUsers.id);
                                                    FileLog.m0d(stringBuilder4.toString());
                                                }
                                                if (dbUsers instanceof TL_phoneCallRequested) {
                                                    if (dbUsers.date + (MessagesController.this.callRingTimeout / 1000) >= ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime()) {
                                                        StringBuilder stringBuilder5;
                                                        TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                                                        if (svc == null && VoIPService.callIShouldHavePutIntoIntent == null) {
                                                            if (tm.getCallState() == 0) {
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    stringBuilder5 = new StringBuilder();
                                                                    stringBuilder5.append("Starting service for call ");
                                                                    stringBuilder5.append(dbUsers.id);
                                                                    FileLog.m0d(stringBuilder5.toString());
                                                                }
                                                                VoIPService.callIShouldHavePutIntoIntent = dbUsers;
                                                                Intent intent = new Intent(ApplicationLoader.applicationContext, VoIPService.class);
                                                                intent.putExtra("is_outgoing", false);
                                                                intent.putExtra("user_id", dbUsers.participant_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId() ? dbUsers.admin_id : dbUsers.participant_id);
                                                                intent.putExtra("account", MessagesController.this.currentAccount);
                                                                try {
                                                                    if (VERSION.SDK_INT >= 26) {
                                                                        ApplicationLoader.applicationContext.startForegroundService(intent);
                                                                    } else {
                                                                        ApplicationLoader.applicationContext.startService(intent);
                                                                    }
                                                                } catch (Throwable th) {
                                                                    FileLog.m3e(th);
                                                                }
                                                            }
                                                        }
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            stringBuilder5 = new StringBuilder();
                                                            stringBuilder5.append("Auto-declining call ");
                                                            stringBuilder5.append(dbUsers.id);
                                                            stringBuilder5.append(" because there's already active one");
                                                            FileLog.m0d(stringBuilder5.toString());
                                                        }
                                                        TL_phone_discardCall req = new TL_phone_discardCall();
                                                        req.peer = new TL_inputPhoneCall();
                                                        req.peer.access_hash = dbUsers.access_hash;
                                                        req.peer.id = dbUsers.id;
                                                        req.reason = new TL_phoneCallDiscardReasonBusy();
                                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(req, new C18143());
                                                    } else if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("ignoring too old call");
                                                    }
                                                } else if (svc != null && dbUsers != null) {
                                                    svc.onCallUpdated(dbUsers);
                                                } else if (VoIPService.callIShouldHavePutIntoIntent != null) {
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("Updated the call while the service is starting");
                                                    }
                                                    if (dbUsers.id == VoIPService.callIShouldHavePutIntoIntent.id) {
                                                        VoIPService.callIShouldHavePutIntoIntent = dbUsers;
                                                    }
                                                }
                                            } else if (!(baseUpdate instanceof TL_updateGroupCall)) {
                                                hasDraftUpdates2 = baseUpdate instanceof TL_updateGroupCallParticipant;
                                            }
                                            hasDraftUpdates2 = hasDraftUpdates;
                                            a++;
                                            size = i4;
                                            dbUsers = arrayList;
                                            i2 = 2;
                                            z = false;
                                            i3 = 1;
                                        }
                                    }
                                    hasDraftUpdates2 = hasDraftUpdates;
                                    a++;
                                    size = i4;
                                    dbUsers = arrayList;
                                    i2 = 2;
                                    z = false;
                                    i3 = 1;
                                }
                            }
                            arrayList = dbUsers;
                            i4 = size;
                            hasDraftUpdates2 = hasDraftUpdates;
                            a++;
                            size = i4;
                            dbUsers = arrayList;
                            i2 = 2;
                            z = false;
                            i3 = 1;
                        }
                        hasDraftUpdates = hasDraftUpdates2;
                        arrayList = dbUsers;
                        i4 = size;
                        hasDraftUpdates2 = hasDraftUpdates;
                        a++;
                        size = i4;
                        dbUsers = arrayList;
                        i2 = 2;
                        z = false;
                        i3 = 1;
                    }
                    hasDraftUpdates = hasDraftUpdates2;
                    arrayList = dbUsers;
                    if (editor != null) {
                        editor.commit();
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                    }
                    i = 1;
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(dbUsersStatus, true, true, true);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(arrayList, false, true, true);
                } else {
                    i = 1;
                    hasDraftUpdates = false;
                }
                if (webPages3 != null) {
                    NotificationCenter instance = NotificationCenter.getInstance(MessagesController.this.currentAccount);
                    int i6 = NotificationCenter.didReceivedWebpagesInUpdates;
                    Object[] objArr = new Object[i];
                    objArr[0] = webPages3;
                    instance.postNotificationName(i6, objArr);
                    i = webPages3.size();
                    for (int b2 = 0; b2 < i; b2++) {
                        dialog_id = webPages3.keyAt(b2);
                        ArrayList<MessageObject> arrayList2 = (ArrayList) MessagesController.this.reloadingWebpagesPending.get(dialog_id);
                        MessagesController.this.reloadingWebpagesPending.remove(dialog_id);
                        if (arrayList2 != null) {
                            WebPage webpage = (WebPage) webPages3.valueAt(b2);
                            ArrayList<Message> arr = new ArrayList();
                            long dialog_id2 = 0;
                            if (!(webpage instanceof TL_webPage)) {
                                if (!(webpage instanceof TL_webPageEmpty)) {
                                    MessagesController.this.reloadingWebpagesPending.put(webpage.id, arrayList2);
                                    did = dialog_id2;
                                    if (!arr.isEmpty()) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages((ArrayList) arr, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList2);
                                    }
                                }
                            }
                            int size23 = arrayList2.size();
                            for (size = 0; size < size23; size++) {
                                ((MessageObject) arrayList2.get(size)).messageOwner.media.webpage = webpage;
                                if (size == 0) {
                                    dialog_id2 = ((MessageObject) arrayList2.get(size)).getDialogId();
                                    ImageLoader.saveMessageThumbs(((MessageObject) arrayList2.get(size)).messageOwner);
                                }
                                arr.add(((MessageObject) arrayList2.get(size)).messageOwner);
                            }
                            did = dialog_id2;
                            if (!arr.isEmpty()) {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages((ArrayList) arr, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList2);
                            }
                        }
                    }
                }
                hasDraftUpdates2 = false;
                if (messages3 != null) {
                    i6 = messages3.size();
                    for (i = 0; i < i6; i++) {
                        MessagesController.this.updateInterfaceWithMessages(messages3.keyAt(i), (ArrayList) messages3.valueAt(i));
                    }
                    hasDraftUpdates2 = true;
                } else if (hasDraftUpdates) {
                    MessagesController.this.sortDialogs(null);
                    hasDraftUpdates2 = true;
                }
                if (editingMessagesFinal2 != null) {
                    i6 = editingMessagesFinal2.size();
                    for (i = 0; i < i6; i++) {
                        j = editingMessagesFinal2.keyAt(i);
                        ArrayList<MessageObject> arrayList3 = (ArrayList) editingMessagesFinal2.valueAt(i);
                        MessageObject oldObject = (MessageObject) MessagesController.this.dialogMessage.get(j);
                        if (oldObject != null) {
                            int a2 = 0;
                            a = arrayList3.size();
                            while (a2 < a) {
                                MessageObject newMessage = (MessageObject) arrayList3.get(a2);
                                if (oldObject.getId() == newMessage.getId()) {
                                    MessagesController.this.dialogMessage.put(j, newMessage);
                                    if (newMessage.messageOwner.to_id != null && newMessage.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(newMessage.getId(), newMessage);
                                    }
                                    hasDraftUpdates2 = true;
                                } else {
                                    a2++;
                                }
                            }
                        }
                        DataQuery.getInstance(MessagesController.this.currentAccount).loadReplyMessagesForMessages(arrayList3, j);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList3);
                    }
                }
                if (hasDraftUpdates2) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
                if (printChangedArg) {
                    updateMask |= 64;
                }
                if (contactsIdsFinal != null) {
                    updateMask = (updateMask | 1) | 128;
                }
                if (chatInfoToUpdateFinal != null) {
                    i6 = chatInfoToUpdateFinal.size();
                    for (i = 0; i < i6; i++) {
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatParticipants((ChatParticipants) chatInfoToUpdateFinal.get(i));
                    }
                }
                if (channelViewsFinal != null) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, channelViewsFinal);
                }
                if (updateMask != 0) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(updateMask));
                }
            }
        });
        ArrayList<TL_updateEncryptedMessagesRead> tasks3 = tasks;
        messages = messages8;
        final SparseLongArray markAsReadMessagesInboxFinal = markAsReadMessagesInbox7;
        ConcurrentHashMap<Integer, User> usersDict2 = concurrentHashMap;
        allowMin = z3;
        final SparseLongArray markAsReadMessagesOutboxFinal = markAsReadMessagesOutbox;
        final ArrayList<Long> markAsReadMessagesFinal = markAsReadMessages5;
        SparseIntArray clearHistoryMessages3 = markAsReadEncrypted3;
        SparseIntArray markAsReadEncrypted4 = markAsReadEncrypted;
        markAsReadEncrypted3 = markAsReadEncrypted4;
        final SparseArray<ArrayList<Integer>> deletedMessagesFinal = deletedMessages6;
        final SparseIntArray clearHistoryMessagesFinal = clearHistoryMessages3;
        markAsReadMessagesInbox3 = markAsReadMessagesOutbox;
        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$133$1 */
            class C03131 implements Runnable {
                C03131() {
                }

                public void run() {
                    int size;
                    int b;
                    int key;
                    MessageObject obj;
                    int b2;
                    long dialog_id;
                    MessageObject message;
                    int updateMask = 0;
                    if (!(markAsReadMessagesInboxFinal == null && markAsReadMessagesOutboxFinal == null)) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesRead, markAsReadMessagesInboxFinal, markAsReadMessagesOutboxFinal);
                        if (markAsReadMessagesInboxFinal != null) {
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(markAsReadMessagesInboxFinal, 0, 0, 0, false);
                            Editor editor = MessagesController.this.notificationsPreferences.edit();
                            size = markAsReadMessagesInboxFinal.size();
                            for (b = 0; b < size; b++) {
                                key = markAsReadMessagesInboxFinal.keyAt(b);
                                int messageId = (int) markAsReadMessagesInboxFinal.valueAt(b);
                                TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs_dict.get((long) key);
                                if (dialog != null && dialog.top_message > 0 && dialog.top_message <= messageId) {
                                    obj = (MessageObject) MessagesController.this.dialogMessage.get(dialog.id);
                                    if (!(obj == null || obj.isOut())) {
                                        obj.setIsRead();
                                        updateMask |= 256;
                                    }
                                }
                                if (key != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("diditem");
                                    stringBuilder.append(key);
                                    editor.remove(stringBuilder.toString());
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("diditemo");
                                    stringBuilder.append(key);
                                    editor.remove(stringBuilder.toString());
                                }
                            }
                            editor.commit();
                        }
                        if (markAsReadMessagesOutboxFinal != null) {
                            b = markAsReadMessagesOutboxFinal.size();
                            for (b2 = 0; b2 < b; b2++) {
                                key = (int) markAsReadMessagesOutboxFinal.valueAt(b2);
                                TL_dialog dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get((long) markAsReadMessagesOutboxFinal.keyAt(b2));
                                if (dialog2 != null && dialog2.top_message > 0 && dialog2.top_message <= key) {
                                    MessageObject obj2 = (MessageObject) MessagesController.this.dialogMessage.get(dialog2.id);
                                    if (obj2 != null && obj2.isOut()) {
                                        obj2.setIsRead();
                                        updateMask |= 256;
                                    }
                                }
                            }
                        }
                    }
                    if (markAsReadEncrypted3 != null) {
                        b = markAsReadEncrypted3.size();
                        for (b2 = 0; b2 < b; b2++) {
                            size = markAsReadEncrypted3.keyAt(b2);
                            key = markAsReadEncrypted3.valueAt(b2);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(size), Integer.valueOf(key));
                            dialog_id = ((long) size) << 32;
                            if (((TL_dialog) MessagesController.this.dialogs_dict.get(dialog_id)) != null) {
                                message = (MessageObject) MessagesController.this.dialogMessage.get(dialog_id);
                                if (message != null && message.messageOwner.date <= key) {
                                    message.setIsRead();
                                    updateMask |= 256;
                                }
                            }
                        }
                    }
                    if (markAsReadMessagesFinal != null) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, markAsReadMessagesFinal);
                    }
                    if (deletedMessagesFinal != null) {
                        b = deletedMessagesFinal.size();
                        for (b2 = 0; b2 < b; b2++) {
                            size = deletedMessagesFinal.keyAt(b2);
                            ArrayList<Integer> arrayList = (ArrayList) deletedMessagesFinal.valueAt(b2);
                            if (arrayList != null) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(size));
                                int size2;
                                if (size == 0) {
                                    size2 = arrayList.size();
                                    for (messageId = 0; messageId < size2; messageId++) {
                                        message = (MessageObject) MessagesController.this.dialogMessagesByIds.get(((Integer) arrayList.get(messageId)).intValue());
                                        if (message != null) {
                                            message.deleted = true;
                                        }
                                    }
                                } else {
                                    MessageObject obj3 = (MessageObject) MessagesController.this.dialogMessage.get((long) (-size));
                                    if (obj3 != null) {
                                        int size22 = arrayList.size();
                                        for (size2 = 0; size2 < size22; size2++) {
                                            if (obj3.getId() == ((Integer) arrayList.get(size2)).intValue()) {
                                                obj3.deleted = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedMessagesFromNotifications(deletedMessagesFinal);
                    }
                    if (clearHistoryMessagesFinal != null) {
                        b = clearHistoryMessagesFinal.size();
                        for (b2 = 0; b2 < b; b2++) {
                            size = clearHistoryMessagesFinal.keyAt(b2);
                            key = clearHistoryMessagesFinal.valueAt(b2);
                            dialog_id = (long) (-size);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.historyCleared, Long.valueOf(dialog_id), Integer.valueOf(key));
                            obj = (MessageObject) MessagesController.this.dialogMessage.get(dialog_id);
                            if (obj != null && obj.getId() <= key) {
                                obj.deleted = true;
                                break;
                            }
                        }
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedHisoryFromNotifications(clearHistoryMessagesFinal);
                    }
                    if (updateMask != 0) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(updateMask));
                    }
                }
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C03131());
            }
        });
        webPages2 = webPages6;
        if (webPages2 != null) {
            MessagesStorage.getInstance(this.currentAccount).putWebPages(webPages2);
        } else {
            messagesController2 = this;
        }
        markAsReadMessagesOutbox3 = markAsReadMessagesInbox7;
        if (markAsReadMessagesOutbox3 == null && markAsReadMessagesInbox3 == null) {
            markAsReadEncrypted2 = markAsReadEncrypted4;
            if (markAsReadEncrypted2 == null) {
                markAsReadMessages2 = markAsReadMessages5;
                if (markAsReadMessages2 == null) {
                    LongSparseArray<WebPage> longSparseArray = webPages2;
                    LongSparseArray<ArrayList<MessageObject>> longSparseArray2 = messages3;
                    if (markAsReadMessages2 != null) {
                        MessagesStorage.getInstance(messagesController2.currentAccount).markMessagesContentAsRead(markAsReadMessages2, ConnectionsManager.getInstance(messagesController2.currentAccount).getCurrentTime());
                    }
                    deletedMessages2 = deletedMessages6;
                    if (deletedMessages2 != null) {
                        size32 = 0;
                        size3 = deletedMessages2.size();
                        while (true) {
                            markAsReadMessagesOutbox2 = markAsReadMessagesInbox3;
                            chat_id = size3;
                            if (size32 < chat_id) {
                                break;
                            }
                            int size13 = chat_id;
                            chat_id = deletedMessages2.keyAt(size32);
                            SparseArray<ArrayList<Integer>> deletedMessages7 = deletedMessages2;
                            final ArrayList deletedMessages8 = (ArrayList) deletedMessages2.valueAt(size32);
                            markAsReadMessages3 = markAsReadMessages2;
                            editingMessagesFinal = editingMessagesFinal2;
                            MessagesStorage.getInstance(messagesController2.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                                public void run() {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(deletedMessages8, MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(deletedMessages8, false, chat_id), false, chat_id);
                                }
                            });
                            size32++;
                            markAsReadMessagesInbox3 = markAsReadMessagesOutbox2;
                            size3 = size13;
                            deletedMessages2 = deletedMessages7;
                            markAsReadMessages2 = markAsReadMessages3;
                            editingMessagesFinal2 = editingMessagesFinal;
                        }
                        markAsReadMessages3 = markAsReadMessages2;
                        editingMessagesFinal = editingMessagesFinal2;
                    } else {
                        markAsReadMessagesOutbox2 = markAsReadMessagesInbox3;
                        markAsReadMessages3 = markAsReadMessages2;
                        editingMessagesFinal = editingMessagesFinal2;
                    }
                    clearHistoryMessages = clearHistoryMessages3;
                    if (clearHistoryMessages != null) {
                        chat_id = 0;
                        size = clearHistoryMessages.size();
                        while (chat_id < size) {
                            size32 = clearHistoryMessages.keyAt(chat_id);
                            size2 = clearHistoryMessages.valueAt(chat_id);
                            SparseIntArray clearHistoryMessages4 = clearHistoryMessages;
                            int size14 = size;
                            MessagesStorage.getInstance(messagesController2.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                                public void run() {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(size32, size2, false), false, size32);
                                }
                            });
                            chat_id++;
                            clearHistoryMessages = clearHistoryMessages4;
                            size = size14;
                        }
                    }
                    if (tasks3 != null) {
                        currentValue = 0;
                        chat_id = tasks3.size();
                        while (currentValue < chat_id) {
                            TL_updateEncryptedMessagesRead update11 = (TL_updateEncryptedMessagesRead) tasks3.get(currentValue);
                            ArrayList<TL_updateEncryptedMessagesRead> tasks4 = tasks3;
                            MessagesStorage.getInstance(messagesController2.currentAccount).createTaskForSecretChat(update11.chat_id, update11.max_date, update11.date, 1, null);
                            currentValue++;
                            tasks3 = tasks4;
                        }
                    }
                    return true;
                }
            } else {
                markAsReadMessages2 = markAsReadMessages5;
            }
        } else {
            markAsReadMessages2 = markAsReadMessages5;
            markAsReadEncrypted2 = markAsReadEncrypted4;
        }
        if (markAsReadMessagesOutbox3 == null) {
            if (markAsReadMessages2 == null) {
                longSparseArray = webPages2;
                longSparseArray2 = messages3;
                z2 = true;
                MessagesStorage.getInstance(messagesController2.currentAccount).markMessagesAsRead(markAsReadMessagesOutbox3, markAsReadMessagesInbox3, markAsReadEncrypted2, z2);
                if (markAsReadMessages2 != null) {
                    MessagesStorage.getInstance(messagesController2.currentAccount).markMessagesContentAsRead(markAsReadMessages2, ConnectionsManager.getInstance(messagesController2.currentAccount).getCurrentTime());
                }
                deletedMessages2 = deletedMessages6;
                if (deletedMessages2 != null) {
                    markAsReadMessagesOutbox2 = markAsReadMessagesInbox3;
                    markAsReadMessages3 = markAsReadMessages2;
                    editingMessagesFinal = editingMessagesFinal2;
                } else {
                    size32 = 0;
                    size3 = deletedMessages2.size();
                    while (true) {
                        markAsReadMessagesOutbox2 = markAsReadMessagesInbox3;
                        chat_id = size3;
                        if (size32 < chat_id) {
                            break;
                        }
                        int size132 = chat_id;
                        chat_id = deletedMessages2.keyAt(size32);
                        SparseArray<ArrayList<Integer>> deletedMessages72 = deletedMessages2;
                        final ArrayList deletedMessages82 = (ArrayList) deletedMessages2.valueAt(size32);
                        markAsReadMessages3 = markAsReadMessages2;
                        editingMessagesFinal = editingMessagesFinal2;
                        MessagesStorage.getInstance(messagesController2.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                        size32++;
                        markAsReadMessagesInbox3 = markAsReadMessagesOutbox2;
                        size3 = size132;
                        deletedMessages2 = deletedMessages72;
                        markAsReadMessages2 = markAsReadMessages3;
                        editingMessagesFinal2 = editingMessagesFinal;
                    }
                    markAsReadMessages3 = markAsReadMessages2;
                    editingMessagesFinal = editingMessagesFinal2;
                }
                clearHistoryMessages = clearHistoryMessages3;
                if (clearHistoryMessages != null) {
                    chat_id = 0;
                    size = clearHistoryMessages.size();
                    while (chat_id < size) {
                        size32 = clearHistoryMessages.keyAt(chat_id);
                        size2 = clearHistoryMessages.valueAt(chat_id);
                        SparseIntArray clearHistoryMessages42 = clearHistoryMessages;
                        int size142 = size;
                        MessagesStorage.getInstance(messagesController2.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                        chat_id++;
                        clearHistoryMessages = clearHistoryMessages42;
                        size = size142;
                    }
                }
                if (tasks3 != null) {
                    currentValue = 0;
                    chat_id = tasks3.size();
                    while (currentValue < chat_id) {
                        TL_updateEncryptedMessagesRead update112 = (TL_updateEncryptedMessagesRead) tasks3.get(currentValue);
                        ArrayList<TL_updateEncryptedMessagesRead> tasks42 = tasks3;
                        MessagesStorage.getInstance(messagesController2.currentAccount).createTaskForSecretChat(update112.chat_id, update112.max_date, update112.date, 1, null);
                        currentValue++;
                        tasks3 = tasks42;
                    }
                }
                return true;
            }
        }
        z2 = true;
        MessagesStorage.getInstance(messagesController2.currentAccount).updateDialogsWithReadMessages(markAsReadMessagesOutbox3, markAsReadMessagesInbox3, markAsReadMessages2, true);
        MessagesStorage.getInstance(messagesController2.currentAccount).markMessagesAsRead(markAsReadMessagesOutbox3, markAsReadMessagesInbox3, markAsReadEncrypted2, z2);
        if (markAsReadMessages2 != null) {
            MessagesStorage.getInstance(messagesController2.currentAccount).markMessagesContentAsRead(markAsReadMessages2, ConnectionsManager.getInstance(messagesController2.currentAccount).getCurrentTime());
        }
        deletedMessages2 = deletedMessages6;
        if (deletedMessages2 != null) {
            size32 = 0;
            size3 = deletedMessages2.size();
            while (true) {
                markAsReadMessagesOutbox2 = markAsReadMessagesInbox3;
                chat_id = size3;
                if (size32 < chat_id) {
                    break;
                }
                int size1322 = chat_id;
                chat_id = deletedMessages2.keyAt(size32);
                SparseArray<ArrayList<Integer>> deletedMessages722 = deletedMessages2;
                final ArrayList deletedMessages822 = (ArrayList) deletedMessages2.valueAt(size32);
                markAsReadMessages3 = markAsReadMessages2;
                editingMessagesFinal = editingMessagesFinal2;
                MessagesStorage.getInstance(messagesController2.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                size32++;
                markAsReadMessagesInbox3 = markAsReadMessagesOutbox2;
                size3 = size1322;
                deletedMessages2 = deletedMessages722;
                markAsReadMessages2 = markAsReadMessages3;
                editingMessagesFinal2 = editingMessagesFinal;
            }
            markAsReadMessages3 = markAsReadMessages2;
            editingMessagesFinal = editingMessagesFinal2;
        } else {
            markAsReadMessagesOutbox2 = markAsReadMessagesInbox3;
            markAsReadMessages3 = markAsReadMessages2;
            editingMessagesFinal = editingMessagesFinal2;
        }
        clearHistoryMessages = clearHistoryMessages3;
        if (clearHistoryMessages != null) {
            chat_id = 0;
            size = clearHistoryMessages.size();
            while (chat_id < size) {
                size32 = clearHistoryMessages.keyAt(chat_id);
                size2 = clearHistoryMessages.valueAt(chat_id);
                SparseIntArray clearHistoryMessages422 = clearHistoryMessages;
                int size1422 = size;
                MessagesStorage.getInstance(messagesController2.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                chat_id++;
                clearHistoryMessages = clearHistoryMessages422;
                size = size1422;
            }
        }
        if (tasks3 != null) {
            currentValue = 0;
            chat_id = tasks3.size();
            while (currentValue < chat_id) {
                TL_updateEncryptedMessagesRead update1122 = (TL_updateEncryptedMessagesRead) tasks3.get(currentValue);
                ArrayList<TL_updateEncryptedMessagesRead> tasks422 = tasks3;
                MessagesStorage.getInstance(messagesController2.currentAccount).createTaskForSecretChat(update1122.chat_id, update1122.max_date, update1122.date, 1, null);
                currentValue++;
                tasks3 = tasks422;
            }
        }
        return true;
    }

    private boolean isNotifySettingsMuted(PeerNotifySettings settings) {
        return (settings instanceof TL_peerNotifySettings) && settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    }

    public boolean isDialogMuted(long dialog_id) {
        int mute_type = this.notificationsPreferences;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(dialog_id);
        mute_type = mute_type.getInt(stringBuilder.toString(), 0);
        if (mute_type == 2) {
            return true;
        }
        if (mute_type == 3) {
            int mute_until = this.notificationsPreferences;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("notifyuntil_");
            stringBuilder2.append(dialog_id);
            if (mute_until.getInt(stringBuilder2.toString(), 0) >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                return true;
            }
        }
        return false;
    }

    private boolean updatePrintingUsersWithNewMessages(long uid, ArrayList<MessageObject> messages) {
        if (uid > 0) {
            if (((ArrayList) this.printingUsers.get(Long.valueOf(uid))) != null) {
                this.printingUsers.remove(Long.valueOf(uid));
                return true;
            }
        } else if (uid < 0) {
            ArrayList<Integer> messagesUsers = new ArrayList();
            Iterator it = messages.iterator();
            while (it.hasNext()) {
                MessageObject message = (MessageObject) it.next();
                if (!messagesUsers.contains(Integer.valueOf(message.messageOwner.from_id))) {
                    messagesUsers.add(Integer.valueOf(message.messageOwner.from_id));
                }
            }
            ArrayList<PrintingUser> arr = (ArrayList) this.printingUsers.get(Long.valueOf(uid));
            boolean changed = false;
            if (arr != null) {
                boolean changed2 = false;
                int a = 0;
                while (a < arr.size()) {
                    if (messagesUsers.contains(Integer.valueOf(((PrintingUser) arr.get(a)).userId))) {
                        arr.remove(a);
                        a--;
                        if (arr.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(uid));
                        }
                        changed2 = true;
                    }
                    a++;
                }
                changed = changed2;
            }
            if (changed) {
                return true;
            }
        }
        return false;
    }

    protected void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages) {
        updateInterfaceWithMessages(uid, messages, false);
    }

    protected void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages, boolean isBroadcast) {
        MessagesController messagesController = this;
        long j = uid;
        ArrayList<MessageObject> arrayList = messages;
        if (arrayList != null) {
            if (!messages.isEmpty()) {
                boolean isEncryptedChat = ((int) j) == 0;
                boolean updateRating = false;
                int channelId = 0;
                MessageObject lastMessage = null;
                for (int a = 0; a < messages.size(); a++) {
                    MessageObject message = (MessageObject) arrayList.get(a);
                    if (lastMessage == null || ((!isEncryptedChat && message.getId() > lastMessage.getId()) || (((isEncryptedChat || (message.getId() < 0 && lastMessage.getId() < 0)) && message.getId() < lastMessage.getId()) || message.messageOwner.date > lastMessage.messageOwner.date))) {
                        lastMessage = message;
                        if (message.messageOwner.to_id.channel_id != 0) {
                            channelId = message.messageOwner.to_id.channel_id;
                        }
                    }
                    if (!(!message.isOut() || message.isSending() || message.isForwarded())) {
                        if (message.isNewGif()) {
                            DataQuery.getInstance(messagesController.currentAccount).addRecentGif(message.messageOwner.media.document, message.messageOwner.date);
                        } else if (message.isSticker()) {
                            DataQuery.getInstance(messagesController.currentAccount).addRecentSticker(0, message.messageOwner.media.document, message.messageOwner.date, false);
                        }
                    }
                    if (message.isOut() && message.isSent()) {
                        updateRating = true;
                    }
                }
                DataQuery.getInstance(messagesController.currentAccount).loadReplyMessagesForMessages(arrayList, j);
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.didReceivedNewMessages, Long.valueOf(uid), arrayList);
                if (lastMessage != null) {
                    TL_dialog dialog = (TL_dialog) messagesController.dialogs_dict.get(j);
                    MessageObject object;
                    if (lastMessage.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                        if (dialog != null) {
                            messagesController.dialogs.remove(dialog);
                            messagesController.dialogsServerOnly.remove(dialog);
                            messagesController.dialogsGroupsOnly.remove(dialog);
                            messagesController.dialogs_dict.remove(dialog.id);
                            messagesController.dialogs_read_inbox_max.remove(Long.valueOf(dialog.id));
                            messagesController.dialogs_read_outbox_max.remove(Long.valueOf(dialog.id));
                            messagesController.nextDialogsCacheOffset--;
                            messagesController.dialogMessage.remove(dialog.id);
                            object = (MessageObject) messagesController.dialogMessagesByIds.get(dialog.top_message);
                            messagesController.dialogMessagesByIds.remove(dialog.top_message);
                            if (!(object == null || object.messageOwner.random_id == 0)) {
                                messagesController.dialogMessagesByRandomIds.remove(object.messageOwner.random_id);
                            }
                            dialog.top_message = 0;
                            NotificationsController.getInstance(messagesController.currentAccount).removeNotificationsForDialog(dialog.id);
                            NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                        }
                        return;
                    }
                    boolean changed = false;
                    if (dialog == null) {
                        if (!isBroadcast) {
                            Chat chat = getChat(Integer.valueOf(channelId));
                            if ((channelId == 0 || chat != null) && (chat == null || !chat.left)) {
                                dialog = new TL_dialog();
                                dialog.id = j;
                                dialog.unread_count = 0;
                                dialog.top_message = lastMessage.getId();
                                dialog.last_message_date = lastMessage.messageOwner.date;
                                dialog.flags = ChatObject.isChannel(chat);
                                messagesController.dialogs_dict.put(j, dialog);
                                messagesController.dialogs.add(dialog);
                                messagesController.dialogMessage.put(j, lastMessage);
                                if (lastMessage.messageOwner.to_id.channel_id == 0) {
                                    messagesController.dialogMessagesByIds.put(lastMessage.getId(), lastMessage);
                                    if (lastMessage.messageOwner.random_id != 0) {
                                        messagesController.dialogMessagesByRandomIds.put(lastMessage.messageOwner.random_id, lastMessage);
                                    }
                                }
                                messagesController.nextDialogsCacheOffset++;
                                changed = true;
                            } else {
                                return;
                            }
                        }
                    } else if ((dialog.top_message > 0 && lastMessage.getId() > 0 && lastMessage.getId() > dialog.top_message) || ((dialog.top_message < 0 && lastMessage.getId() < 0 && lastMessage.getId() < dialog.top_message) || messagesController.dialogMessage.indexOfKey(j) < 0 || dialog.top_message < 0 || dialog.last_message_date <= lastMessage.messageOwner.date)) {
                        object = (MessageObject) messagesController.dialogMessagesByIds.get(dialog.top_message);
                        messagesController.dialogMessagesByIds.remove(dialog.top_message);
                        if (!(object == null || object.messageOwner.random_id == 0)) {
                            messagesController.dialogMessagesByRandomIds.remove(object.messageOwner.random_id);
                        }
                        dialog.top_message = lastMessage.getId();
                        if (!isBroadcast) {
                            dialog.last_message_date = lastMessage.messageOwner.date;
                            changed = true;
                        }
                        messagesController.dialogMessage.put(j, lastMessage);
                        if (lastMessage.messageOwner.to_id.channel_id == 0) {
                            messagesController.dialogMessagesByIds.put(lastMessage.getId(), lastMessage);
                            if (lastMessage.messageOwner.random_id != 0) {
                                messagesController.dialogMessagesByRandomIds.put(lastMessage.messageOwner.random_id, lastMessage);
                            }
                        }
                    }
                    if (changed) {
                        sortDialogs(null);
                    }
                    if (updateRating) {
                        DataQuery.getInstance(messagesController.currentAccount).increasePeerRaiting(j);
                    }
                }
            }
        }
    }

    public void sortDialogs(SparseArray<Chat> chatsDict) {
        this.dialogsServerOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsForward.clear();
        int selfId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        Collections.sort(this.dialogs, this.dialogComparator);
        boolean selfAdded = false;
        int a = 0;
        while (a < this.dialogs.size()) {
            TL_dialog d = (TL_dialog) this.dialogs.get(a);
            int high_id = (int) (d.id >> 32);
            int lower_id = (int) d.id;
            if (lower_id == selfId) {
                this.dialogsForward.add(0, d);
                selfAdded = true;
            } else {
                this.dialogsForward.add(d);
            }
            if (!(lower_id == 0 || high_id == 1)) {
                this.dialogsServerOnly.add(d);
                Chat chat;
                if (DialogObject.isChannel(d)) {
                    chat = getChat(Integer.valueOf(-lower_id));
                    if (chat != null && ((chat.megagroup && chat.admin_rights != null && (chat.admin_rights.post_messages || chat.admin_rights.add_admins)) || chat.creator)) {
                        this.dialogsGroupsOnly.add(d);
                    }
                } else if (lower_id < 0) {
                    if (chatsDict != null) {
                        chat = (Chat) chatsDict.get(-lower_id);
                        if (!(chat == null || chat.migrated_to == null)) {
                            this.dialogs.remove(a);
                            a--;
                        }
                    }
                    this.dialogsGroupsOnly.add(d);
                }
            }
            a++;
        }
        if (!selfAdded) {
            User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (user != null) {
                d = new TL_dialog();
                d.id = (long) user.id;
                d.notify_settings = new TL_peerNotifySettings();
                d.peer = new TL_peerUser();
                d.peer.user_id = user.id;
                this.dialogsForward.add(0, d);
            }
        }
    }

    private static String getRestrictionReason(String reason) {
        if (reason != null) {
            if (reason.length() != 0) {
                int index = reason.indexOf(": ");
                if (index > 0) {
                    String type = reason.substring(null, index);
                    if (type.contains("-all") || type.contains("-android")) {
                        return reason.substring(index + 2);
                    }
                }
                return null;
            }
        }
        return null;
    }

    private static void showCantOpenAlert(BaseFragment fragment, String reason) {
        if (fragment != null) {
            if (fragment.getParentActivity() != null) {
                Builder builder = new Builder(fragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.setMessage(reason);
                fragment.showDialog(builder.create());
            }
        }
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment fragment) {
        return checkCanOpenChat(bundle, fragment, null);
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment fragment, MessageObject originalMessage) {
        MessagesController messagesController = this;
        final Bundle bundle2 = bundle;
        final BaseFragment baseFragment = fragment;
        if (bundle2 != null) {
            if (baseFragment != null) {
                User user = null;
                Chat chat = null;
                int user_id = bundle2.getInt("user_id", 0);
                int chat_id = bundle2.getInt("chat_id", 0);
                int messageId = bundle2.getInt("message_id", 0);
                if (user_id != 0) {
                    user = getUser(Integer.valueOf(user_id));
                } else if (chat_id != 0) {
                    chat = getChat(Integer.valueOf(chat_id));
                }
                if (user == null && chat == null) {
                    return true;
                }
                String reason = null;
                if (chat != null) {
                    reason = getRestrictionReason(chat.restriction_reason);
                } else if (user != null) {
                    reason = getRestrictionReason(user.restriction_reason);
                }
                if (reason != null) {
                    showCantOpenAlert(baseFragment, reason);
                    return false;
                }
                if (!(messageId == 0 || originalMessage == null || chat == null || chat.access_hash != 0)) {
                    int did = (int) originalMessage.getDialogId();
                    if (did != 0) {
                        TLObject req;
                        final int reqId;
                        final AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 1);
                        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        if (did < 0) {
                            chat = getChat(Integer.valueOf(-did));
                        }
                        if (did <= 0) {
                            if (ChatObject.isChannel(chat)) {
                                chat = getChat(Integer.valueOf(-did));
                                req = new TL_channels_getMessages();
                                req.channel = getInputChannel(chat);
                                req.id.add(Integer.valueOf(originalMessage.getId()));
                                reqId = ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req, new RequestDelegate() {
                                    public void run(final TLObject response, TL_error error) {
                                        if (response != null) {
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    try {
                                                        progressDialog.dismiss();
                                                    } catch (Throwable e) {
                                                        FileLog.m3e(e);
                                                    }
                                                    messages_Messages res = response;
                                                    MessagesController.this.putUsers(res.users, false);
                                                    MessagesController.this.putChats(res.chats, false);
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                                    baseFragment.presentFragment(new ChatActivity(bundle2), true);
                                                }
                                            });
                                        }
                                    }
                                });
                                progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(reqId, true);
                                        try {
                                            dialog.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                        if (baseFragment != null) {
                                            baseFragment.setVisibleDialog(null);
                                        }
                                    }
                                });
                                baseFragment.setVisibleDialog(progressDialog);
                                progressDialog.show();
                                return false;
                            }
                        }
                        req = new TL_messages_getMessages();
                        req.id.add(Integer.valueOf(originalMessage.getId()));
                        reqId = ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(req, /* anonymous class already generated */);
                        progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), /* anonymous class already generated */);
                        baseFragment.setVisibleDialog(progressDialog);
                        progressDialog.show();
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

    public static void openChatOrProfileWith(User user, Chat chat, BaseFragment fragment, int type, boolean closeLast) {
        if ((user != null || chat != null) && fragment != null) {
            String reason = null;
            if (chat != null) {
                reason = getRestrictionReason(chat.restriction_reason);
            } else if (user != null) {
                reason = getRestrictionReason(user.restriction_reason);
                if (user.bot) {
                    type = 1;
                    closeLast = true;
                }
            }
            if (reason != null) {
                showCantOpenAlert(fragment, reason);
            } else {
                Bundle args = new Bundle();
                if (chat != null) {
                    args.putInt("chat_id", chat.id);
                } else {
                    args.putInt("user_id", user.id);
                }
                if (type == 0) {
                    fragment.presentFragment(new ProfileActivity(args));
                } else if (type == 2) {
                    fragment.presentFragment(new ChatActivity(args), true, true);
                } else {
                    fragment.presentFragment(new ChatActivity(args), closeLast);
                }
            }
        }
    }

    public void openByUserName(String username, final BaseFragment fragment, final int type) {
        if (username != null) {
            if (fragment != null) {
                TLObject object = getUserOrChat(username);
                User user = null;
                Chat chat = null;
                if (object instanceof User) {
                    user = (User) object;
                    if (user.min) {
                        user = null;
                    }
                } else if (object instanceof Chat) {
                    chat = (Chat) object;
                    if (chat.min) {
                        chat = null;
                    }
                }
                if (user != null) {
                    openChatOrProfileWith(user, null, fragment, type, false);
                } else if (chat != null) {
                    openChatOrProfileWith(null, chat, fragment, 1, false);
                } else if (fragment.getParentActivity() != null) {
                    final AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(fragment.getParentActivity(), 1)};
                    TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                    req.username = username;
                    final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        progressDialog[0].dismiss();
                                    } catch (Exception e) {
                                    }
                                    progressDialog[0] = null;
                                    fragment.setVisibleDialog(null);
                                    if (error == null) {
                                        TL_contacts_resolvedPeer res = response;
                                        MessagesController.this.putUsers(res.users, false);
                                        MessagesController.this.putChats(res.chats, false);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, false, true);
                                        if (!res.chats.isEmpty()) {
                                            MessagesController.openChatOrProfileWith(null, (Chat) res.chats.get(0), fragment, 1, false);
                                        } else if (!res.users.isEmpty()) {
                                            MessagesController.openChatOrProfileWith((User) res.users.get(0), null, fragment, type, false);
                                        }
                                    } else if (fragment != null && fragment.getParentActivity() != null) {
                                        try {
                                            Toast.makeText(fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound), 0).show();
                                        } catch (Throwable e2) {
                                            FileLog.m3e(e2);
                                        }
                                    }
                                }
                            });
                        }
                    });
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.messenger.MessagesController$139$1 */
                        class C03161 implements OnClickListener {
                            C03161() {
                            }

                            public void onClick(DialogInterface dialog, int which) {
                                ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(reqId, true);
                                try {
                                    dialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }

                        public void run() {
                            if (progressDialog[0] != null) {
                                progressDialog[0].setMessage(LocaleController.getString("Loading", R.string.Loading));
                                progressDialog[0].setCanceledOnTouchOutside(false);
                                progressDialog[0].setCancelable(false);
                                progressDialog[0].setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C03161());
                                fragment.showDialog(progressDialog[0]);
                            }
                        }
                    }, 500);
                }
            }
        }
    }
}
