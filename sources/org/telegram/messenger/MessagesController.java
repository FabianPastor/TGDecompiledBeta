package org.telegram.messenger;

import android.app.Dialog;
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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
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
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.AbstractSerializedData;
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
import org.telegram.tgnet.TLRPC.InputDialogPeer;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.PhoneCall;
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

        public int compare(TL_dialog tL_dialog, TL_dialog tL_dialog2) {
            if (!tL_dialog.pinned && tL_dialog2.pinned) {
                return 1;
            }
            if (tL_dialog.pinned && !tL_dialog2.pinned) {
                return -1;
            }
            if (!tL_dialog.pinned || !tL_dialog2.pinned) {
                DraftMessage draft = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(tL_dialog.id);
                tL_dialog = (draft == null || draft.date < tL_dialog.last_message_date) ? tL_dialog.last_message_date : draft.date;
                draft = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(tL_dialog2.id);
                tL_dialog2 = (draft == null || draft.date < tL_dialog2.last_message_date) ? tL_dialog2.last_message_date : draft.date;
                if (tL_dialog < tL_dialog2) {
                    return 1;
                }
                if (tL_dialog > tL_dialog2) {
                    return -1;
                }
                return 0;
            } else if (tL_dialog.pinnedNum < tL_dialog2.pinnedNum) {
                return 1;
            } else {
                if (tL_dialog.pinnedNum > tL_dialog2.pinnedNum) {
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

        public int compare(Update update, Update update2) {
            int access$100 = MessagesController.this.getUpdateType(update);
            int access$1002 = MessagesController.this.getUpdateType(update2);
            if (access$100 != access$1002) {
                return AndroidUtilities.compare(access$100, access$1002);
            }
            if (access$100 == 0) {
                return AndroidUtilities.compare(MessagesController.getUpdatePts(update), MessagesController.getUpdatePts(update2));
            }
            if (access$100 == 1) {
                return AndroidUtilities.compare(MessagesController.getUpdateQts(update), MessagesController.getUpdateQts(update2));
            }
            if (access$100 != 2) {
                return null;
            }
            access$100 = MessagesController.getUpdateChannelId(update);
            access$1002 = MessagesController.getUpdateChannelId(update2);
            if (access$100 == access$1002) {
                return AndroidUtilities.compare(MessagesController.getUpdatePts(update), MessagesController.getUpdatePts(update2));
            }
            return AndroidUtilities.compare(access$100, access$1002);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$4 */
    class C03364 implements Runnable {
        C03364() {
        }

        public void run() {
            MessagesController instance = MessagesController.getInstance(MessagesController.this.currentAccount);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(instance, NotificationCenter.FileDidUpload);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(instance, NotificationCenter.FileDidFailUpload);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(instance, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(instance, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(instance, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(instance, NotificationCenter.updateMessageMedia);
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

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                tL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                if (tL_error == null) {
                    tL_error = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
                    MessagesController.this.putUser(tL_error, true);
                } else {
                    UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(tL_error);
                }
                if (tL_error != null) {
                    TL_photos_photo tL_photos_photo = (TL_photos_photo) tLObject;
                    ArrayList arrayList = tL_photos_photo.photo.sizes;
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
                    PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
                    tL_error.photo = new TL_userProfilePhoto();
                    tL_error.photo.photo_id = tL_photos_photo.photo.id;
                    if (closestPhotoSizeWithSize != null) {
                        tL_error.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    if (closestPhotoSizeWithSize2 != null) {
                        tL_error.photo.photo_big = closestPhotoSizeWithSize2.location;
                    } else if (closestPhotoSizeWithSize != null) {
                        tL_error.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(tL_error.id);
                    tLObject = new ArrayList();
                    tLObject.add(tL_error);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(tLObject, null, false, true);
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
        this.currentAccount = i;
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
            i = ApplicationLoader.applicationContext;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Notifications");
            stringBuilder.append(this.currentAccount);
            this.notificationsPreferences = i.getSharedPreferences(stringBuilder.toString(), 0);
            i = ApplicationLoader.applicationContext;
            stringBuilder = new StringBuilder();
            stringBuilder.append("mainconfig");
            stringBuilder.append(this.currentAccount);
            this.mainPreferences = i.getSharedPreferences(stringBuilder.toString(), 0);
            i = ApplicationLoader.applicationContext;
            stringBuilder = new StringBuilder();
            stringBuilder.append("emoji");
            stringBuilder.append(this.currentAccount);
            this.emojiPreferences = i.getSharedPreferences(stringBuilder.toString(), 0);
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

    public void updateConfig(final TL_config tL_config) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                LocaleController.getInstance().loadRemoteLanguages(MessagesController.this.currentAccount);
                MessagesController.this.maxMegagroupCount = tL_config.megagroup_size_max;
                MessagesController.this.maxGroupCount = tL_config.chat_size_max;
                MessagesController.this.maxEditTime = tL_config.edit_time_limit;
                MessagesController.this.ratingDecay = tL_config.rating_e_decay;
                MessagesController.this.maxRecentGifsCount = tL_config.saved_gifs_limit;
                MessagesController.this.maxRecentStickersCount = tL_config.stickers_recent_limit;
                MessagesController.this.maxFaveStickersCount = tL_config.stickers_faved_limit;
                MessagesController.this.revokeTimeLimit = tL_config.revoke_time_limit;
                MessagesController.this.revokeTimePmLimit = tL_config.revoke_pm_time_limit;
                MessagesController.this.canRevokePmInbox = tL_config.revoke_pm_inbox;
                MessagesController.this.linkPrefix = tL_config.me_url_prefix;
                if (MessagesController.this.linkPrefix.endsWith("/")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(0, MessagesController.this.linkPrefix.length() - 1);
                }
                if (MessagesController.this.linkPrefix.startsWith("https://")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(8);
                } else if (MessagesController.this.linkPrefix.startsWith("http://")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(7);
                }
                MessagesController.this.callReceiveTimeout = tL_config.call_receive_timeout_ms;
                MessagesController.this.callRingTimeout = tL_config.call_ring_timeout_ms;
                MessagesController.this.callConnectTimeout = tL_config.call_connect_timeout_ms;
                MessagesController.this.callPacketTimeout = tL_config.call_packet_timeout_ms;
                MessagesController.this.maxPinnedDialogsCount = tL_config.pinned_dialogs_count_max;
                MessagesController.this.defaultP2pContacts = tL_config.default_p2p_contacts;
                MessagesController.this.preloadFeaturedStickers = tL_config.preload_featured_stickers;
                Editor edit = MessagesController.this.mainPreferences.edit();
                edit.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
                edit.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
                edit.putInt("maxEditTime", MessagesController.this.maxEditTime);
                edit.putInt("ratingDecay", MessagesController.this.ratingDecay);
                edit.putInt("maxRecentGifsCount", MessagesController.this.maxRecentGifsCount);
                edit.putInt("maxRecentStickersCount", MessagesController.this.maxRecentStickersCount);
                edit.putInt("maxFaveStickersCount", MessagesController.this.maxFaveStickersCount);
                edit.putInt("callReceiveTimeout", MessagesController.this.callReceiveTimeout);
                edit.putInt("callRingTimeout", MessagesController.this.callRingTimeout);
                edit.putInt("callConnectTimeout", MessagesController.this.callConnectTimeout);
                edit.putInt("callPacketTimeout", MessagesController.this.callPacketTimeout);
                edit.putString("linkPrefix", MessagesController.this.linkPrefix);
                edit.putInt("maxPinnedDialogsCount", MessagesController.this.maxPinnedDialogsCount);
                edit.putBoolean("defaultP2pContacts", MessagesController.this.defaultP2pContacts);
                edit.putBoolean("preloadFeaturedStickers", MessagesController.this.preloadFeaturedStickers);
                edit.putInt("revokeTimeLimit", MessagesController.this.revokeTimeLimit);
                edit.putInt("revokeTimePmLimit", MessagesController.this.revokeTimePmLimit);
                edit.putBoolean("canRevokePmInbox", MessagesController.this.canRevokePmInbox);
                edit.commit();
                LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(MessagesController.this.currentAccount, tL_config.lang_pack_version);
            }
        });
    }

    public void addSupportUser() {
        User tL_userForeign_old2 = new TL_userForeign_old2();
        tL_userForeign_old2.phone = "333";
        tL_userForeign_old2.id = 333000;
        tL_userForeign_old2.first_name = "Telegram";
        tL_userForeign_old2.last_name = TtmlNode.ANONYMOUS_REGION_ID;
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
        tL_userForeign_old2 = new TL_userForeign_old2();
        tL_userForeign_old2.phone = "42777";
        tL_userForeign_old2.id = 777000;
        tL_userForeign_old2.first_name = "Telegram";
        tL_userForeign_old2.last_name = "Notifications";
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
    }

    public InputUser getInputUser(User user) {
        if (user == null) {
            return new TL_inputUserEmpty();
        }
        if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            user = new TL_inputUserSelf();
        } else {
            TLObject tL_inputUser = new TL_inputUser();
            tL_inputUser.user_id = user.id;
            tL_inputUser.access_hash = user.access_hash;
            user = tL_inputUser;
        }
        return user;
    }

    public InputUser getInputUser(int i) {
        return getInputUser(getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(i)));
    }

    public static InputChannel getInputChannel(Chat chat) {
        if (!(chat instanceof TL_channel)) {
            if (!(chat instanceof TL_channelForbidden)) {
                return new TL_inputChannelEmpty();
            }
        }
        InputChannel tL_inputChannel = new TL_inputChannel();
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
        Peer tL_peerChat;
        if (i < 0) {
            i = -i;
            Chat chat = getChat(Integer.valueOf(i));
            if (!(chat instanceof TL_channel)) {
                if (!(chat instanceof TL_channelForbidden)) {
                    tL_peerChat = new TL_peerChat();
                    tL_peerChat.chat_id = i;
                    return tL_peerChat;
                }
            }
            tL_peerChat = new TL_peerChannel();
            tL_peerChat.channel_id = i;
            return tL_peerChat;
        }
        getUser(Integer.valueOf(i));
        tL_peerChat = new TL_peerUser();
        tL_peerChat.user_id = i;
        return tL_peerChat;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        if (i == NotificationCenter.FileDidUpload) {
            str = (String) objArr[0];
            InputFile inputFile = (InputFile) objArr[1];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(str) != 0) {
                i = new TL_photos_uploadProfilePhoto();
                i.file = inputFile;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(i, new C18156());
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            str = (String) objArr[0];
            if (this.uploadingAvatar != 0 && this.uploadingAvatar.equals(str) != 0) {
                this.uploadingAvatar = 0;
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            Integer num = (Integer) objArr[0];
            Integer num2 = (Integer) objArr[1];
            Long l = (Long) objArr[3];
            MessageObject messageObject = (MessageObject) this.dialogMessage.get(l.longValue());
            if (messageObject != null && (messageObject.getId() == num.intValue() || messageObject.messageOwner.local_id == num.intValue())) {
                messageObject.messageOwner.id = num2.intValue();
                messageObject.messageOwner.send_state = 0;
            }
            TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(l.longValue());
            if (tL_dialog != null && tL_dialog.top_message == num.intValue()) {
                tL_dialog.top_message = num2.intValue();
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
                if (message.media.ttl_seconds == null) {
                    return;
                }
                if ((message.media.photo instanceof TL_photoEmpty) != null || (message.media.document instanceof TL_documentEmpty) != 0) {
                    messageObject3.setType();
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

    public User getUser(Integer num) {
        return (User) this.users.get(num);
    }

    public TLObject getUserOrChat(String str) {
        if (str != null) {
            if (str.length() != 0) {
                return (TLObject) this.objectsByUsernames.get(str.toLowerCase());
            }
        }
        return null;
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
        z = new CountDownLatch(1);
        ArrayList arrayList = new ArrayList();
        MessagesStorage.getInstance(this.currentAccount).getEncryptedChat(i, z, arrayList);
        try {
            z.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        if (!arrayList.size()) {
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

    public void setLastCreatedDialogId(final long j, final boolean z) {
        if (z) {
            this.createdDialogMainThreadIds.add(Long.valueOf(j));
        } else {
            this.createdDialogMainThreadIds.remove(Long.valueOf(j));
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (z) {
                    MessagesController.this.createdDialogIds.add(Long.valueOf(j));
                } else {
                    MessagesController.this.createdDialogIds.remove(Long.valueOf(j));
                }
            }
        });
    }

    public ExportedChatInvite getExportedInvite(int i) {
        return (ExportedChatInvite) this.exportedChats.get(i);
    }

    public boolean putUser(User user, boolean z) {
        if (user == null) {
            return false;
        }
        z = (!z || user.id / 1000 || user.id) ? false : true;
        User user2 = (User) this.users.get(Integer.valueOf(user.id));
        if (user2 == user) {
            return false;
        }
        if (!(user2 == null || TextUtils.isEmpty(user2.username))) {
            this.objectsByUsernames.remove(user2.username.toLowerCase());
        }
        if (!TextUtils.isEmpty(user.username)) {
            this.objectsByUsernames.put(user.username.toLowerCase(), user);
        }
        if (user.min) {
            if (user2 == null) {
                this.users.put(Integer.valueOf(user.id), user);
            } else if (!z) {
                if (user.bot) {
                    if (user.username) {
                        user2.username = user.username;
                        user2.flags |= 8;
                    } else {
                        user2.flags &= -9;
                        user2.username = null;
                    }
                }
                if (user.photo) {
                    user2.photo = user.photo;
                    user2.flags |= 32;
                } else {
                    user2.flags &= -33;
                    user2.photo = null;
                }
            }
        } else if (!z) {
            this.users.put(Integer.valueOf(user.id), user);
            if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
                UserConfig.getInstance(this.currentAccount).saveConfig(true);
            }
            return user2 != null && user.status && user2.status && user.status.expires != user2.status.expires;
        } else if (user2 == null) {
            this.users.put(Integer.valueOf(user.id), user);
        } else if (user2.min) {
            user.min = false;
            if (user2.bot) {
                if (user2.username) {
                    user.username = user2.username;
                    user.flags |= 8;
                } else {
                    user.flags &= -9;
                    user.username = null;
                }
            }
            if (user2.photo) {
                user.photo = user2.photo;
                user.flags |= 32;
            } else {
                user.flags &= -33;
                user.photo = null;
            }
            this.users.put(Integer.valueOf(user.id), user);
        }
    }

    public void putUsers(ArrayList<User> arrayList, boolean z) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                int size = arrayList.size();
                int i = 0;
                int i2 = 0;
                while (i < size) {
                    if (putUser((User) arrayList.get(i), z)) {
                        i2 = 1;
                    }
                    i++;
                }
                if (i2 != 0) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                        }
                    });
                }
            }
        }
    }

    public void putChat(final Chat chat, boolean z) {
        if (chat != null) {
            Chat chat2 = (Chat) this.chats.get(Integer.valueOf(chat.id));
            if (chat2 != chat) {
                if (!(chat2 == null || TextUtils.isEmpty(chat2.username))) {
                    this.objectsByUsernames.remove(chat2.username.toLowerCase());
                }
                if (!TextUtils.isEmpty(chat.username)) {
                    this.objectsByUsernames.put(chat.username.toLowerCase(), chat);
                }
                if (!chat.min) {
                    boolean z2 = false;
                    if (!z) {
                        if (chat2 != null) {
                            if (chat.version != chat2.version) {
                                this.loadedFullChats.remove(Integer.valueOf(chat.id));
                            }
                            if (chat2.participants_count && !chat.participants_count) {
                                chat.participants_count = chat2.participants_count;
                                chat.flags |= true;
                            }
                            z = chat2.banned_rights ? chat2.banned_rights.flags : false;
                            if (chat.banned_rights != null) {
                                z2 = chat.banned_rights.flags;
                            }
                            if (z != z2) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, chat);
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
                        chat.democracy = chat2.democracy;
                        if (chat2.username) {
                            chat.username = chat2.username;
                            chat.flags |= 64;
                        } else {
                            chat.flags &= -65;
                            chat.username = null;
                        }
                        if (chat2.participants_count && !chat.participants_count) {
                            chat.participants_count = chat2.participants_count;
                            chat.flags |= true;
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
                    chat2.democracy = chat.democracy;
                    if (chat.username) {
                        chat2.username = chat.username;
                        chat2.flags |= 64;
                    } else {
                        chat2.flags &= -65;
                        chat2.username = null;
                    }
                    if (chat.participants_count) {
                        chat2.participants_count = chat.participants_count;
                    }
                }
            }
        }
    }

    public void putChats(ArrayList<Chat> arrayList, boolean z) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    putChat((Chat) arrayList.get(i), z);
                }
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
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    putEncryptedChat((EncryptedChat) arrayList.get(i), z);
                }
            }
        }
    }

    public TL_userFull getUserFull(int i) {
        return (TL_userFull) this.fullUsers.get(i);
    }

    public void cancelLoadFullUser(int i) {
        this.loadingFullUsers.remove(Integer.valueOf(i));
    }

    public void cancelLoadFullChat(int i) {
        this.loadingFullChats.remove(Integer.valueOf(i));
    }

    protected void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<TL_dialog> arrayList, long j) {
        if (j != 0 || (arrayList != null && !arrayList.isEmpty())) {
            TLObject tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
            if (arrayList != null) {
                for (j = null; j < arrayList.size(); j++) {
                    InputPeer inputPeer = getInputPeer((int) ((TL_dialog) arrayList.get(j)).id);
                    if (!(inputPeer instanceof TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                        TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                        tL_inputDialogPeer.peer = inputPeer;
                        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                    }
                }
            } else {
                arrayList = getInputPeer((int) j);
                if ((arrayList instanceof TL_inputPeerChannel) == null || arrayList.access_hash != 0) {
                    j = new TL_inputDialogPeer();
                    j.peer = arrayList;
                    tL_messages_getPeerDialogs.peers.add(j);
                } else {
                    return;
                }
            }
            if (tL_messages_getPeerDialogs.peers.isEmpty() == null) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tLObject != null) {
                            TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
                            tL_error = new ArrayList();
                            for (int i = 0; i < tL_messages_peerDialogs.dialogs.size(); i++) {
                                TL_dialog tL_dialog = (TL_dialog) tL_messages_peerDialogs.dialogs.get(i);
                                if (tL_dialog.read_inbox_max_id == 0) {
                                    tL_dialog.read_inbox_max_id = 1;
                                }
                                if (tL_dialog.read_outbox_max_id == 0) {
                                    tL_dialog.read_outbox_max_id = 1;
                                }
                                if (tL_dialog.id == 0 && tL_dialog.peer != null) {
                                    if (tL_dialog.peer.user_id != 0) {
                                        tL_dialog.id = (long) tL_dialog.peer.user_id;
                                    } else if (tL_dialog.peer.chat_id != 0) {
                                        tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                                    } else if (tL_dialog.peer.channel_id != 0) {
                                        tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                                    }
                                }
                                Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                                if (num == null) {
                                    num = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(tL_dialog.read_inbox_max_id, num.intValue())));
                                if (num.intValue() == 0) {
                                    if (tL_dialog.peer.channel_id != 0) {
                                        TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                                        tL_updateReadChannelInbox.channel_id = tL_dialog.peer.channel_id;
                                        tL_updateReadChannelInbox.max_id = tL_dialog.read_inbox_max_id;
                                        tL_error.add(tL_updateReadChannelInbox);
                                    } else {
                                        TL_updateReadHistoryInbox tL_updateReadHistoryInbox = new TL_updateReadHistoryInbox();
                                        tL_updateReadHistoryInbox.peer = tL_dialog.peer;
                                        tL_updateReadHistoryInbox.max_id = tL_dialog.read_inbox_max_id;
                                        tL_error.add(tL_updateReadHistoryInbox);
                                    }
                                }
                                num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                                if (num == null) {
                                    num = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(tL_dialog.read_outbox_max_id, num.intValue())));
                                if (num.intValue() == 0) {
                                    if (tL_dialog.peer.channel_id != 0) {
                                        TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_updateReadChannelOutbox();
                                        tL_updateReadChannelOutbox.channel_id = tL_dialog.peer.channel_id;
                                        tL_updateReadChannelOutbox.max_id = tL_dialog.read_outbox_max_id;
                                        tL_error.add(tL_updateReadChannelOutbox);
                                    } else {
                                        TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox = new TL_updateReadHistoryOutbox();
                                        tL_updateReadHistoryOutbox.peer = tL_dialog.peer;
                                        tL_updateReadHistoryOutbox.max_id = tL_dialog.read_outbox_max_id;
                                        tL_error.add(tL_updateReadHistoryOutbox);
                                    }
                                }
                            }
                            if (tL_error.isEmpty() == null) {
                                MessagesController.this.processUpdateArray(tL_error, null, null, false);
                            }
                        }
                    }
                });
            }
        }
    }

    public boolean isChannelAdmin(int i, int i2) {
        ArrayList arrayList = (ArrayList) this.channelAdmins.get(i);
        return arrayList != null && arrayList.indexOf(Integer.valueOf(i2)) >= 0;
    }

    public void loadChannelAdmins(final int i, boolean z) {
        if (this.loadingChannelAdmins.indexOfKey(i) < 0) {
            int i2 = 0;
            this.loadingChannelAdmins.put(i, 0);
            if (z) {
                MessagesStorage.getInstance(this.currentAccount).loadChannelAdmins(i);
            } else {
                z = new TL_channels_getParticipants();
                ArrayList arrayList = (ArrayList) this.channelAdmins.get(i);
                if (arrayList != null) {
                    long j = 0;
                    while (i2 < arrayList.size()) {
                        j = (((j * 20261) + 2147483648L) + ((long) ((Integer) arrayList.get(i2)).intValue())) % 2147483648L;
                        i2++;
                    }
                    z.hash = (int) j;
                }
                z.channel = getInputChannel(i);
                z.limit = 100;
                z.filter = new TL_channelParticipantsAdmins();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if ((tLObject instanceof TL_channels_channelParticipants) != null) {
                            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                            tL_error = new ArrayList(tL_channels_channelParticipants.participants.size());
                            for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                                tL_error.add(Integer.valueOf(((ChannelParticipant) tL_channels_channelParticipants.participants.get(i)).user_id));
                            }
                            MessagesController.this.processLoadedChannelAdmins(tL_error, i, false);
                        }
                    }
                });
            }
        }
    }

    public void processLoadedChannelAdmins(final ArrayList<Integer> arrayList, final int i, final boolean z) {
        Collections.sort(arrayList);
        if (!z) {
            MessagesStorage.getInstance(this.currentAccount).putChannelAdmins(i, arrayList);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.loadingChannelAdmins.delete(i);
                MessagesController.this.channelAdmins.put(i, arrayList);
                if (z) {
                    MessagesController.this.loadChannelAdmins(i, false);
                }
            }
        });
    }

    public void loadFullChat(int i, int i2, boolean z) {
        boolean contains = this.loadedFullChats.contains(Integer.valueOf(i));
        if (!this.loadingFullChats.contains(Integer.valueOf(i))) {
            if (z || !contains) {
                this.loadingFullChats.add(Integer.valueOf(i));
                final long j = (long) (-i);
                final Chat chat = getChat(Integer.valueOf(i));
                if (ChatObject.isChannel(chat)) {
                    z = new TL_channels_getFullChannel();
                    z.channel = getInputChannel(chat);
                    if (chat.megagroup) {
                        loadChannelAdmins(i, contains ^ 1);
                    }
                } else {
                    z = new TL_messages_getFullChat();
                    z.chat_id = i;
                    if (this.dialogs_read_inbox_max.get(Long.valueOf(j)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j)) == null) {
                        reloadDialogsReadValue(null, j);
                    }
                }
                final int i3 = i;
                final int i4 = i2;
                i = ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
                    public void run(TLObject tLObject, final TL_error tL_error) {
                        if (tL_error == null) {
                            final TL_messages_chatFull tL_messages_chatFull = (TL_messages_chatFull) tLObject;
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(tL_messages_chatFull.users, tL_messages_chatFull.chats, true, true);
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(tL_messages_chatFull.full_chat, false);
                            if (ChatObject.isChannel(chat) != null) {
                                tL_error = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                                if (tL_error == null) {
                                    tL_error = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j));
                                }
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_inbox_max_id, tL_error.intValue())));
                                if (tL_error.intValue() == null) {
                                    tL_error = new ArrayList();
                                    TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                                    tL_updateReadChannelInbox.channel_id = i3;
                                    tL_updateReadChannelInbox.max_id = tL_messages_chatFull.full_chat.read_inbox_max_id;
                                    tL_error.add(tL_updateReadChannelInbox);
                                    MessagesController.this.processUpdateArray(tL_error, null, null, false);
                                }
                                tL_error = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                                if (tL_error == null) {
                                    tL_error = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j));
                                }
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_outbox_max_id, tL_error.intValue())));
                                if (tL_error.intValue() == null) {
                                    tL_error = new ArrayList();
                                    TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_updateReadChannelOutbox();
                                    tL_updateReadChannelOutbox.channel_id = i3;
                                    tL_updateReadChannelOutbox.max_id = tL_messages_chatFull.full_chat.read_outbox_max_id;
                                    tL_error.add(tL_updateReadChannelOutbox);
                                    MessagesController.this.processUpdateArray(tL_error, null, null, false);
                                }
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.applyDialogNotificationsSettings((long) (-i3), tL_messages_chatFull.full_chat.notify_settings);
                                    for (int i = 0; i < tL_messages_chatFull.full_chat.bot_info.size(); i++) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo((BotInfo) tL_messages_chatFull.full_chat.bot_info.get(i));
                                    }
                                    MessagesController.this.exportedChats.put(i3, tL_messages_chatFull.full_chat.exported_invite);
                                    MessagesController.this.loadingFullChats.remove(Integer.valueOf(i3));
                                    MessagesController.this.loadedFullChats.add(Integer.valueOf(i3));
                                    MessagesController.this.putUsers(tL_messages_chatFull.users, false);
                                    MessagesController.this.putChats(tL_messages_chatFull.chats, false);
                                    if (tL_messages_chatFull.full_chat.stickerset != null) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(tL_messages_chatFull.full_chat.stickerset);
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, tL_messages_chatFull.full_chat, Integer.valueOf(i4), Boolean.valueOf(false), null);
                                }
                            });
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.checkChannelError(tL_error.text, i3);
                                MessagesController.this.loadingFullChats.remove(Integer.valueOf(i3));
                            }
                        });
                    }
                });
                if (i2 != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, i2);
                }
            }
        }
    }

    public void loadFullUser(final User user, final int i, boolean z) {
        if (!(user == null || this.loadingFullUsers.contains(Integer.valueOf(user.id)))) {
            if (z || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                z = new TL_users_getFullUser();
                z.id = getInputUser(user);
                long j = (long) user.id;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(j)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j)) == null) {
                    reloadDialogsReadValue(null, j);
                }
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$16$2 */
                    class C03202 implements Runnable {
                        C03202() {
                        }

                        public void run() {
                            MessagesController.this.loadingFullUsers.remove(Integer.valueOf(user.id));
                        }
                    }

                    public void run(final TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TL_userFull tL_userFull = (TL_userFull) tLObject;
                                    MessagesController.this.applyDialogNotificationsSettings((long) user.id, tL_userFull.notify_settings);
                                    if (tL_userFull.bot_info instanceof TL_botInfo) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo(tL_userFull.bot_info);
                                    }
                                    MessagesController.this.fullUsers.put(user.id, tL_userFull);
                                    MessagesController.this.loadingFullUsers.remove(Integer.valueOf(user.id));
                                    MessagesController.this.loadedFullUsers.add(Integer.valueOf(user.id));
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(user.first_name);
                                    stringBuilder.append(user.last_name);
                                    stringBuilder.append(user.username);
                                    String stringBuilder2 = stringBuilder.toString();
                                    ArrayList arrayList = new ArrayList();
                                    arrayList.add(tL_userFull.user);
                                    MessagesController.this.putUsers(arrayList, false);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(arrayList, null, false, true);
                                    if (stringBuilder2 != null) {
                                        StringBuilder stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(tL_userFull.user.first_name);
                                        stringBuilder3.append(tL_userFull.user.last_name);
                                        stringBuilder3.append(tL_userFull.user.username);
                                        if (!stringBuilder2.equals(stringBuilder3.toString())) {
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                                        }
                                    }
                                    if (tL_userFull.bot_info instanceof TL_botInfo) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, tL_userFull.bot_info, Integer.valueOf(i));
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, Integer.valueOf(user.id), tL_userFull);
                                }
                            });
                        } else {
                            AndroidUtilities.runOnUIThread(new C03202());
                        }
                    }
                }), i);
            }
        }
    }

    private void reloadMessages(ArrayList<Integer> arrayList, long j) {
        if (!arrayList.isEmpty()) {
            TLObject tL_channels_getMessages;
            final Object arrayList2 = new ArrayList();
            final Chat chatByDialog = ChatObject.getChatByDialog(j, this.currentAccount);
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
            if (arrayList2.isEmpty() == null) {
                if (arrayList3 == null) {
                    arrayList3 = new ArrayList();
                    this.reloadingMessages.put(j, arrayList3);
                }
                arrayList3.addAll(arrayList2);
                final long j2 = j;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        AnonymousClass17 anonymousClass17 = this;
                        if (tL_error == null) {
                            int i;
                            messages_Messages messages_messages = (messages_Messages) tLObject;
                            SparseArray sparseArray = new SparseArray();
                            boolean z = false;
                            for (i = 0; i < messages_messages.users.size(); i++) {
                                User user = (User) messages_messages.users.get(i);
                                sparseArray.put(user.id, user);
                            }
                            SparseArray sparseArray2 = new SparseArray();
                            for (i = 0; i < messages_messages.chats.size(); i++) {
                                Chat chat = (Chat) messages_messages.chats.get(i);
                                sparseArray2.put(chat.id, chat);
                            }
                            Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j2));
                            if (num == null) {
                                num = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j2));
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j2), num);
                            }
                            Integer num2 = num;
                            num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j2));
                            if (num == null) {
                                num = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j2));
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j2), num);
                            }
                            Integer num3 = num;
                            final ArrayList arrayList = new ArrayList();
                            int i2 = 0;
                            while (i2 < messages_messages.messages.size()) {
                                Message message = (Message) messages_messages.messages.get(i2);
                                if (chatByDialog != null && chatByDialog.megagroup) {
                                    message.flags |= Integer.MIN_VALUE;
                                }
                                message.dialog_id = j2;
                                message.unread = (message.out ? num3 : num2).intValue() < message.id ? true : z;
                                MessageObject messageObject = r3;
                                MessageObject messageObject2 = new MessageObject(MessagesController.this.currentAccount, message, sparseArray, sparseArray2, true);
                                arrayList.add(messageObject);
                                i2++;
                                z = false;
                            }
                            ImageLoader.saveMessagesThumbs(messages_messages.messages);
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messages_messages, j2, -1, 0, false);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ArrayList arrayList = (ArrayList) MessagesController.this.reloadingMessages.get(j2);
                                    if (arrayList != null) {
                                        arrayList.removeAll(arrayList2);
                                        if (arrayList.isEmpty()) {
                                            MessagesController.this.reloadingMessages.remove(j2);
                                        }
                                    }
                                    MessageObject messageObject = (MessageObject) MessagesController.this.dialogMessage.get(j2);
                                    if (messageObject != null) {
                                        int i = 0;
                                        while (i < arrayList.size()) {
                                            MessageObject messageObject2 = (MessageObject) arrayList.get(i);
                                            if (messageObject == null || messageObject.getId() != messageObject2.getId()) {
                                                i++;
                                            } else {
                                                MessagesController.this.dialogMessage.put(j2, messageObject2);
                                                if (messageObject2.messageOwner.to_id.channel_id == 0) {
                                                    messageObject = (MessageObject) MessagesController.this.dialogMessagesByIds.get(messageObject2.getId());
                                                    MessagesController.this.dialogMessagesByIds.remove(messageObject2.getId());
                                                    if (messageObject != null) {
                                                        MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                                    }
                                                }
                                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                            }
                                        }
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j2), arrayList);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    public void hideReportSpam(long j, User user, Chat chat) {
        if (user != null || chat != null) {
            Editor edit = this.notificationsPreferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("spam3_");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), 1);
            edit.commit();
            if (((int) j) != null) {
                j = new TL_messages_hideReportSpam();
                if (user != null) {
                    j.peer = getInputPeer(user.id);
                } else if (chat != null) {
                    j.peer = getInputPeer(-chat.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(j, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
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
            if (((int) j) == null) {
                if (encryptedChat != null) {
                    if (encryptedChat.access_hash != 0) {
                        j = new TL_messages_reportEncryptedSpam();
                        j.peer = new TL_inputEncryptedChat();
                        j.peer.chat_id = encryptedChat.id;
                        j.peer.access_hash = encryptedChat.access_hash;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(j, new RequestDelegate() {
                            public void run(TLObject tLObject, TL_error tL_error) {
                            }
                        }, 2);
                    }
                }
                return;
            }
            j = new TL_messages_reportSpam();
            if (chat != null) {
                j.peer = getInputPeer(-chat.id);
            } else if (user != null) {
                j.peer = getInputPeer(user.id);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(j, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            }, 2);
        }
    }

    public void loadPeerSettings(User user, Chat chat) {
        if (user != null || chat != null) {
            long j;
            if (user != null) {
                j = (long) user.id;
            } else {
                j = (long) (-chat.id);
            }
            if (this.loadingPeerSettings.indexOfKey(j) < 0) {
                this.loadingPeerSettings.put(j, Boolean.valueOf(true));
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("request spam button for ");
                    stringBuilder.append(j);
                    FileLog.m0d(stringBuilder.toString());
                }
                SharedPreferences sharedPreferences = this.notificationsPreferences;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("spam3_");
                stringBuilder2.append(j);
                if (sharedPreferences.getInt(stringBuilder2.toString(), 0) == 1) {
                    if (BuildVars.LOGS_ENABLED != null) {
                        user = new StringBuilder();
                        user.append("spam button already hidden for ");
                        user.append(j);
                        FileLog.m0d(user.toString());
                    }
                    return;
                }
                sharedPreferences = this.notificationsPreferences;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("spam_");
                stringBuilder3.append(j);
                TLObject tL_messages_hideReportSpam;
                if (sharedPreferences.getBoolean(stringBuilder3.toString(), false)) {
                    tL_messages_hideReportSpam = new TL_messages_hideReportSpam();
                    if (user != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(user.id);
                    } else if (chat != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(-chat.id);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_hideReportSpam, new RequestDelegate() {

                        /* renamed from: org.telegram.messenger.MessagesController$21$1 */
                        class C03231 implements Runnable {
                            C03231() {
                            }

                            public void run() {
                                MessagesController.this.loadingPeerSettings.remove(j);
                                Editor edit = MessagesController.this.notificationsPreferences.edit();
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
                        }

                        public void run(TLObject tLObject, TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new C03231());
                        }
                    });
                    return;
                }
                tL_messages_hideReportSpam = new TL_messages_getPeerSettings();
                if (user != null) {
                    tL_messages_hideReportSpam.peer = getInputPeer(user.id);
                } else if (chat != null) {
                    tL_messages_hideReportSpam.peer = getInputPeer(-chat.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_hideReportSpam, new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.loadingPeerSettings.remove(j);
                                if (tLObject != null) {
                                    TL_peerSettings tL_peerSettings = (TL_peerSettings) tLObject;
                                    Editor edit = MessagesController.this.notificationsPreferences.edit();
                                    StringBuilder stringBuilder;
                                    if (tL_peerSettings.report_spam) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("show spam button for ");
                                            stringBuilder.append(j);
                                            FileLog.m0d(stringBuilder.toString());
                                        }
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("spam3_");
                                        stringBuilder.append(j);
                                        edit.putInt(stringBuilder.toString(), 2);
                                        edit.commit();
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoaded, Long.valueOf(j));
                                        return;
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("don't show spam button for ");
                                        stringBuilder.append(j);
                                        FileLog.m0d(stringBuilder.toString());
                                    }
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("spam3_");
                                    stringBuilder.append(j);
                                    edit.putInt(stringBuilder.toString(), 1);
                                    edit.commit();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    protected void processNewChannelDifferenceParams(int i, int i2, int i3) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processNewChannelDifferenceParams pts = ");
            stringBuilder.append(i);
            stringBuilder.append(" pts_count = ");
            stringBuilder.append(i2);
            stringBuilder.append(" channeldId = ");
            stringBuilder.append(i3);
            FileLog.m0d(stringBuilder.toString());
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
            if (BuildVars.LOGS_ENABLED != 0) {
                FileLog.m0d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(i3, i);
            MessagesStorage.getInstance(this.currentAccount).saveChannelPts(i3, i);
        } else if (i4 != i) {
            long j = this.updatesStartWaitTimeChannels.get(i3);
            if (!(this.gettingDifferenceChannels.get(i3) || j == 0)) {
                if (Math.abs(System.currentTimeMillis() - j) > 1500) {
                    getChannelDifference(i3);
                    return;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("ADD CHANNEL UPDATE TO QUEUE pts = ");
                stringBuilder2.append(i);
                stringBuilder2.append(" pts_count = ");
                stringBuilder2.append(i2);
                FileLog.m0d(stringBuilder2.toString());
            }
            if (j == 0) {
                this.updatesStartWaitTimeChannels.put(i3, System.currentTimeMillis());
            }
            UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
            userActionUpdatesPts.pts = i;
            userActionUpdatesPts.pts_count = i2;
            userActionUpdatesPts.chat_id = i3;
            i = (ArrayList) this.updatesQueueChannels.get(i3);
            if (i == null) {
                i = new ArrayList();
                this.updatesQueueChannels.put(i3, i);
            }
            i.add(userActionUpdatesPts);
        }
    }

    protected void processNewDifferenceParams(int i, int i2, int i3, int i4) {
        StringBuilder stringBuilder;
        UserActionUpdatesSeq userActionUpdatesSeq;
        MessagesController messagesController = this;
        int i5 = i;
        int i6 = i2;
        int i7 = i3;
        int i8 = i4;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("processNewDifferenceParams seq = ");
            stringBuilder2.append(i5);
            stringBuilder2.append(" pts = ");
            stringBuilder2.append(i6);
            stringBuilder2.append(" date = ");
            stringBuilder2.append(i7);
            stringBuilder2.append(" pts_count = ");
            stringBuilder2.append(i8);
            FileLog.m0d(stringBuilder2.toString());
        }
        if (i6 != -1) {
            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + i8 == i6) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("APPLY PTS");
                }
                MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(i6);
                MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
            } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != i6) {
                if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimePts == 0)) {
                    if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) > 1500) {
                        getDifference();
                        i6 = -1;
                        if (i5 == i6) {
                            return;
                        }
                        if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 != i5) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("APPLY SEQ");
                            }
                            MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(i5);
                            if (i7 != -1) {
                                MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(i7);
                            }
                            MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
                        } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() != i5) {
                            if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimeSeq == 0)) {
                                if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) > 1500) {
                                    getDifference();
                                }
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("ADD UPDATE TO QUEUE seq = ");
                                stringBuilder.append(i5);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            if (messagesController.updatesStartWaitTimeSeq == 0) {
                                messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                            }
                            userActionUpdatesSeq = new UserActionUpdatesSeq();
                            userActionUpdatesSeq.seq = i5;
                            messagesController.updatesQueueSeq.add(userActionUpdatesSeq);
                            return;
                        }
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("ADD UPDATE TO QUEUE pts = ");
                    stringBuilder3.append(i6);
                    stringBuilder3.append(" pts_count = ");
                    stringBuilder3.append(i8);
                    FileLog.m0d(stringBuilder3.toString());
                }
                if (messagesController.updatesStartWaitTimePts == 0) {
                    messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                }
                UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
                userActionUpdatesPts.pts = i6;
                userActionUpdatesPts.pts_count = i8;
                messagesController.updatesQueuePts.add(userActionUpdatesPts);
                i6 = -1;
                if (i5 == i6) {
                    if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 != i5) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("APPLY SEQ");
                        }
                        MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(i5);
                        if (i7 != -1) {
                            MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(i7);
                        }
                        MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
                    } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() != i5) {
                        if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) > 1500) {
                            getDifference();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("ADD UPDATE TO QUEUE seq = ");
                            stringBuilder.append(i5);
                            FileLog.m0d(stringBuilder.toString());
                        }
                        if (messagesController.updatesStartWaitTimeSeq == 0) {
                            messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                        }
                        userActionUpdatesSeq = new UserActionUpdatesSeq();
                        userActionUpdatesSeq.seq = i5;
                        messagesController.updatesQueueSeq.add(userActionUpdatesSeq);
                        return;
                    }
                }
                return;
            }
        }
        i6 = -1;
        if (i5 == i6) {
            return;
        }
        if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 != i5) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("APPLY SEQ");
            }
            MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(i5);
            if (i7 != -1) {
                MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(i7);
            }
            MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
        } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() != i5) {
        } else {
            if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) > 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ADD UPDATE TO QUEUE seq = ");
                    stringBuilder.append(i5);
                    FileLog.m0d(stringBuilder.toString());
                }
                if (messagesController.updatesStartWaitTimeSeq == 0) {
                    messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                userActionUpdatesSeq = new UserActionUpdatesSeq();
                userActionUpdatesSeq.seq = i5;
                messagesController.updatesQueueSeq.add(userActionUpdatesSeq);
                return;
            }
            getDifference();
        }
    }

    public void didAddedNewTask(final int i, final SparseArray<ArrayList<Long>> sparseArray) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if ((MessagesController.this.currentDeletingTaskMids == null && !MessagesController.this.gettingNewDeleteTask) || (MessagesController.this.currentDeletingTaskTime != 0 && i < MessagesController.this.currentDeletingTaskTime)) {
                    MessagesController.this.getNewDeleteTask(null, 0);
                }
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didCreatedNewDeleteTask, sparseArray);
            }
        });
    }

    public void getNewDeleteTask(final ArrayList<Integer> arrayList, final int i) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.this.gettingNewDeleteTask = true;
                MessagesStorage.getInstance(MessagesController.this.currentAccount).getNewTask(arrayList, i);
            }
        });
    }

    private boolean checkDeletingTask(boolean z) {
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (this.currentDeletingTaskMids == null || (!z && (this.currentDeletingTaskTime == 0 || this.currentDeletingTaskTime > currentTime))) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (!(this.currentDeleteTaskRunnable == null || z)) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = false;
        z = new ArrayList(this.currentDeletingTaskMids);
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$26$1 */
            class C03251 implements Runnable {
                C03251() {
                }

                public void run() {
                    MessagesController.this.getNewDeleteTask(z, MessagesController.this.currentDeletingTaskChannelId);
                    MessagesController.this.currentDeletingTaskTime = 0;
                    MessagesController.this.currentDeletingTaskMids = null;
                }
            }

            public void run() {
                if (z.isEmpty() || ((Integer) z.get(0)).intValue() <= 0) {
                    MessagesController.this.deleteMessages(z, null, null, 0, false);
                } else {
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).emptyMessagesMedia(z);
                }
                Utilities.stageQueue.postRunnable(new C03251());
            }
        });
        return true;
    }

    public void processLoadedDeleteTask(final int i, final ArrayList<Integer> arrayList, int i2) {
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
                if (arrayList != null) {
                    MessagesController.this.currentDeletingTaskTime = i;
                    MessagesController.this.currentDeletingTaskMids = arrayList;
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

    public void loadDialogPhotos(int i, int i2, long j, boolean z, int i3) {
        if (z) {
            MessagesStorage.getInstance(this.currentAccount).getDialogPhotos(i, i2, j, i3);
        } else if (i > 0) {
            User user = getUser(Integer.valueOf(i));
            if (user != false) {
                TLObject tL_photos_getUserPhotos = new TL_photos_getUserPhotos();
                tL_photos_getUserPhotos.limit = i2;
                tL_photos_getUserPhotos.offset = 0;
                tL_photos_getUserPhotos.max_id = (long) ((int) j);
                tL_photos_getUserPhotos.user_id = getInputUser(user);
                r3 = i;
                r4 = i2;
                r5 = j;
                r7 = i3;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_getUserPhotos, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            MessagesController.this.processLoadedUserPhotos((photos_Photos) tLObject, r3, r4, r5, false, r7);
                        }
                    }
                }), i3);
            }
        } else if (i < 0) {
            z = new TL_messages_search();
            z.filter = new TL_inputMessagesFilterChatPhotos();
            z.limit = i2;
            z.offset_id = (int) j;
            z.f49q = TtmlNode.ANONYMOUS_REGION_ID;
            z.peer = getInputPeer(i);
            r3 = i;
            r4 = i2;
            r5 = j;
            r7 = i3;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        messages_Messages messages_messages = (messages_Messages) tLObject;
                        photos_Photos tL_photos_photos = new TL_photos_photos();
                        tL_photos_photos.count = messages_messages.count;
                        tL_photos_photos.users.addAll(messages_messages.users);
                        for (tL_error = null; tL_error < messages_messages.messages.size(); tL_error++) {
                            Message message = (Message) messages_messages.messages.get(tL_error);
                            if (message.action != null) {
                                if (message.action.photo != null) {
                                    tL_photos_photos.photos.add(message.action.photo);
                                }
                            }
                        }
                        MessagesController.this.processLoadedUserPhotos(tL_photos_photos, r3, r4, r5, false, r7);
                    }
                }
            }), i3);
        }
    }

    public void blockUser(int i) {
        final User user = getUser(Integer.valueOf(i));
        if (user != null) {
            if (!this.blockedUsers.contains(Integer.valueOf(i))) {
                this.blockedUsers.add(Integer.valueOf(i));
                if (user.bot) {
                    DataQuery.getInstance(this.currentAccount).removeInline(i);
                } else {
                    DataQuery.getInstance(this.currentAccount).removePeer(i);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                i = new TL_contacts_block();
                i.id = getInputUser(user);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(i, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            tLObject = new ArrayList();
                            tLObject.add(Integer.valueOf(user.id));
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(tLObject, false);
                        }
                    }
                });
            }
        }
    }

    public void setUserBannedRole(int i, User user, TL_channelBannedRights tL_channelBannedRights, boolean z, BaseFragment baseFragment) {
        if (user != null) {
            if (tL_channelBannedRights != null) {
                TLObject tL_channels_editBanned = new TL_channels_editBanned();
                tL_channels_editBanned.channel = getInputChannel(i);
                tL_channels_editBanned.user_id = getInputUser(user);
                tL_channels_editBanned.banned_rights = tL_channelBannedRights;
                final int i2 = i;
                final BaseFragment baseFragment2 = baseFragment;
                final TLObject tLObject = tL_channels_editBanned;
                final boolean z2 = z;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editBanned, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$31$1 */
                    class C03281 implements Runnable {
                        C03281() {
                        }

                        public void run() {
                            MessagesController.this.loadFullChat(i2, 0, true);
                        }
                    }

                    public void run(TLObject tLObject, final TL_error tL_error) {
                        if (tL_error == null) {
                            MessagesController.this.processUpdates((Updates) tLObject, false);
                            AndroidUtilities.runOnUIThread(new C03281(), 1000);
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, tL_error, baseFragment2, tLObject, Boolean.valueOf(true ^ z2));
                            }
                        });
                    }
                });
            }
        }
    }

    public void setUserAdminRole(int i, User user, TL_channelAdminRights tL_channelAdminRights, boolean z, BaseFragment baseFragment) {
        if (user != null) {
            if (tL_channelAdminRights != null) {
                TLObject tL_channels_editAdmin = new TL_channels_editAdmin();
                tL_channels_editAdmin.channel = getInputChannel(i);
                tL_channels_editAdmin.user_id = getInputUser(user);
                tL_channels_editAdmin.admin_rights = tL_channelAdminRights;
                final int i2 = i;
                final BaseFragment baseFragment2 = baseFragment;
                final TLObject tLObject = tL_channels_editAdmin;
                final boolean z2 = z;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editAdmin, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$32$1 */
                    class C03301 implements Runnable {
                        C03301() {
                        }

                        public void run() {
                            MessagesController.this.loadFullChat(i2, 0, true);
                        }
                    }

                    public void run(TLObject tLObject, final TL_error tL_error) {
                        if (tL_error == null) {
                            MessagesController.this.processUpdates((Updates) tLObject, false);
                            AndroidUtilities.runOnUIThread(new C03301(), 1000);
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, tL_error, baseFragment2, tLObject, Boolean.valueOf(true ^ z2));
                            }
                        });
                    }
                });
            }
        }
    }

    public void unblockUser(int i) {
        TLObject tL_contacts_unblock = new TL_contacts_unblock();
        final User user = getUser(Integer.valueOf(i));
        if (user != 0) {
            this.blockedUsers.remove(Integer.valueOf(user.id));
            tL_contacts_unblock.id = getInputUser(user);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_unblock, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).deleteBlockedUser(user.id);
                }
            });
        }
    }

    public void getBlockedUsers(boolean z) {
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            if (!this.loadingBlockedUsers) {
                this.loadingBlockedUsers = true;
                if (z) {
                    MessagesStorage.getInstance(this.currentAccount).getBlockedUsers();
                } else {
                    z = new TL_contacts_getBlocked();
                    z.offset = 0;
                    z.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            ArrayList arrayList = new ArrayList();
                            if (tL_error == null) {
                                contacts_Blocked contacts_blocked = (contacts_Blocked) tLObject;
                                tL_error = contacts_blocked.blocked.iterator();
                                while (tL_error.hasNext()) {
                                    arrayList.add(Integer.valueOf(((TL_contactBlocked) tL_error.next()).user_id));
                                }
                                tL_error = contacts_blocked.users;
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(contacts_blocked.users, null, true, true);
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(arrayList, true);
                            } else {
                                tL_error = null;
                            }
                            MessagesController.this.processLoadedBlockedUsers(arrayList, tL_error, false);
                        }
                    });
                }
            }
        }
    }

    public void processLoadedBlockedUsers(final ArrayList<Integer> arrayList, final ArrayList<User> arrayList2, final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (arrayList2 != null) {
                    MessagesController.this.putUsers(arrayList2, z);
                }
                MessagesController.this.loadingBlockedUsers = false;
                if (arrayList.isEmpty() && z && !UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded) {
                    MessagesController.this.getBlockedUsers(false);
                    return;
                }
                if (!z) {
                    UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded = true;
                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                }
                MessagesController.this.blockedUsers = arrayList;
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            }
        });
    }

    public void deleteUserPhoto(InputPhoto inputPhoto) {
        if (inputPhoto == null) {
            inputPhoto = new TL_photos_updateProfilePhoto();
            inputPhoto.id = new TL_inputPhotoEmpty();
            UserConfig.getInstance(this.currentAccount).getCurrentUser().photo = new TL_userProfilePhotoEmpty();
            User user = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            }
            if (user != null) {
                user.photo = UserConfig.getInstance(this.currentAccount).getCurrentUser().photo;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(inputPhoto, new RequestDelegate() {

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

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            tL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                            if (tL_error == null) {
                                tL_error = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
                                MessagesController.this.putUser(tL_error, false);
                            } else {
                                UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(tL_error);
                            }
                            if (tL_error != null) {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(tL_error.id);
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(tL_error);
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(arrayList, null, false, true);
                                tL_error.photo = (UserProfilePhoto) tLObject;
                                AndroidUtilities.runOnUIThread(new C03321());
                            }
                        }
                    }
                });
            } else {
                return;
            }
        }
        TLObject tL_photos_deletePhotos = new TL_photos_deletePhotos();
        tL_photos_deletePhotos.id.add(inputPhoto);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_deletePhotos, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void processLoadedUserPhotos(photos_Photos photos_photos, int i, int i2, long j, boolean z, int i3) {
        if (z) {
            if (photos_photos != null) {
                if (photos_photos.photos.isEmpty()) {
                }
            }
            loadDialogPhotos(i, i2, j, false, i3);
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(photos_photos.users, null, true, true);
        MessagesStorage.getInstance(this.currentAccount).putDialogPhotos(i, photos_photos);
        final photos_Photos photos_photos2 = photos_photos;
        final boolean z2 = z;
        final int i4 = i;
        final int i5 = i2;
        final int i6 = i3;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.putUsers(photos_photos2.users, z2);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(i4), Integer.valueOf(i5), Boolean.valueOf(z2), Integer.valueOf(i6), photos_photos2.photos);
            }
        });
    }

    public void uploadAndApplyUserAvatar(PhotoSize photoSize) {
        if (photoSize != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(FileLoader.getDirectory(4));
            stringBuilder.append("/");
            stringBuilder.append(photoSize.location.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(photoSize.location.local_id);
            stringBuilder.append(".jpg");
            this.uploadingAvatar = stringBuilder.toString();
            FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
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

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, EncryptedChat encryptedChat, final int i, boolean z, long j, TLObject tLObject) {
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
            if (i != 0) {
                if (tLObject != null) {
                    tLObject = (TL_channels_deleteMessages) tLObject;
                } else {
                    tLObject = new TL_channels_deleteMessages();
                    tLObject.id = arrayList3;
                    tLObject.channel = getInputChannel(i);
                    try {
                        arrayList2 = new NativeByteBuffer(8 + tLObject.getObjectSize());
                        try {
                            arrayList2.writeInt32(7);
                            arrayList2.writeInt32(i);
                            tLObject.serializeToStream(arrayList2);
                        } catch (Exception e2) {
                            e = e2;
                            FileLog.m3e(e);
                            j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(arrayList2);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    if (tL_error == null) {
                                        TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                                        MessagesController.this.processNewChannelDifferenceParams(tL_messages_affectedMessages.pts, tL_messages_affectedMessages.pts_count, i);
                                    }
                                    if (j != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j);
                                    }
                                }
                            });
                        }
                    } catch (Exception e3) {
                        e = e3;
                        arrayList2 = null;
                        FileLog.m3e(e);
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(arrayList2);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, /* anonymous class already generated */);
                    }
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(arrayList2);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, /* anonymous class already generated */);
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
                        arrayList2 = new NativeByteBuffer(8 + tLObject.getObjectSize());
                        try {
                            arrayList2.writeInt32(7);
                            arrayList2.writeInt32(i);
                            tLObject.serializeToStream(arrayList2);
                        } catch (Exception e4) {
                            e = e4;
                            FileLog.m3e(e);
                            j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(arrayList2);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    if (tL_error == null) {
                                        TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                                        MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                                    }
                                    if (j != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j);
                                    }
                                }
                            });
                        }
                    } catch (Exception e5) {
                        e = e5;
                        arrayList2 = null;
                        FileLog.m3e(e);
                        j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(arrayList2);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, /* anonymous class already generated */);
                    }
                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(arrayList2);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, /* anonymous class already generated */);
            }
        }
    }

    public void pinChannelMessage(Chat chat, int i, boolean z) {
        TLObject tL_channels_updatePinnedMessage = new TL_channels_updatePinnedMessage();
        tL_channels_updatePinnedMessage.channel = getInputChannel(chat);
        tL_channels_updatePinnedMessage.id = i;
        tL_channels_updatePinnedMessage.silent = z ^ 1;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_updatePinnedMessage, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                }
            }
        });
    }

    public void deleteUserChannelHistory(final Chat chat, final User user, int i) {
        if (i == 0) {
            MessagesStorage.getInstance(this.currentAccount).deleteUserChannelHistory(chat.id, user.id);
        }
        i = new TL_channels_deleteUserHistory();
        i.channel = getInputChannel(chat);
        i.user_id = getInputUser(user);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(i, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    TL_messages_affectedHistory tL_messages_affectedHistory = (TL_messages_affectedHistory) tLObject;
                    if (tL_messages_affectedHistory.offset > null) {
                        MessagesController.this.deleteUserChannelHistory(chat, user, tL_messages_affectedHistory.offset);
                    }
                    MessagesController.this.processNewChannelDifferenceParams(tL_messages_affectedHistory.pts, tL_messages_affectedHistory.pts_count, chat.id);
                }
            }
        });
    }

    public void deleteDialog(long j, int i) {
        deleteDialog(j, true, i, 0);
    }

    private void deleteDialog(long j, boolean z, int i, int i2) {
        MessagesController messagesController = this;
        final long j2 = j;
        final int i3 = i;
        int i4 = (int) j2;
        int i5 = (int) (j2 >> 32);
        if (i3 == 2) {
            MessagesStorage.getInstance(messagesController.currentAccount).deleteDialog(j2, i3);
            return;
        }
        int i6;
        int i7;
        if (i3 == 0 || i3 == 3) {
            DataQuery.getInstance(messagesController.currentAccount).uninstallShortcut(j2);
        }
        boolean z2 = true;
        if (z) {
            int max;
            MessagesStorage.getInstance(messagesController.currentAccount).deleteDialog(j2, i3);
            TL_dialog tL_dialog = (TL_dialog) messagesController.dialogs_dict.get(j2);
            if (tL_dialog != null) {
                MessageObject messageObject;
                int id;
                max = i2 == 0 ? Math.max(0, tL_dialog.top_message) : i2;
                if (i3 != 0) {
                    if (i3 != 3) {
                        tL_dialog.unread_count = 0;
                        messageObject = (MessageObject) messagesController.dialogMessage.get(tL_dialog.id);
                        messagesController.dialogMessage.remove(tL_dialog.id);
                        if (messageObject == null) {
                            id = messageObject.getId();
                            messagesController.dialogMessagesByIds.remove(messageObject.getId());
                        } else {
                            id = tL_dialog.top_message;
                            messageObject = (MessageObject) messagesController.dialogMessagesByIds.get(tL_dialog.top_message);
                            messagesController.dialogMessagesByIds.remove(tL_dialog.top_message);
                        }
                        if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                            messagesController.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                        }
                        if (i3 == 1 || i4 == 0 || r13 <= 0) {
                            messagesController = this;
                            tL_dialog.top_message = 0;
                        } else {
                            Message tL_messageService = new TL_messageService();
                            tL_messageService.id = tL_dialog.top_message;
                            tL_messageService.out = ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) == j2;
                            tL_messageService.from_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                            tL_messageService.flags |= 256;
                            tL_messageService.action = new TL_messageActionHistoryClear();
                            tL_messageService.date = tL_dialog.last_message_date;
                            if (i4 > 0) {
                                tL_messageService.to_id = new TL_peerUser();
                                tL_messageService.to_id.user_id = i4;
                            } else {
                                i6 = -i4;
                                if (ChatObject.isChannel(getChat(Integer.valueOf(i6)))) {
                                    tL_messageService.to_id = new TL_peerChannel();
                                    tL_messageService.to_id.channel_id = i6;
                                } else {
                                    tL_messageService.to_id = new TL_peerChat();
                                    tL_messageService.to_id.chat_id = i6;
                                }
                            }
                            MessageObject messageObject2 = new MessageObject(messagesController.currentAccount, tL_messageService, messagesController.createdDialogIds.contains(Long.valueOf(tL_messageService.dialog_id)));
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(messageObject2);
                            ArrayList arrayList2 = new ArrayList();
                            arrayList2.add(tL_messageService);
                            updateInterfaceWithMessages(j2, arrayList);
                            MessagesStorage.getInstance(messagesController.currentAccount).putMessages(arrayList2, false, true, false, 0);
                        }
                    }
                }
                messagesController.dialogs.remove(tL_dialog);
                if (messagesController.dialogsServerOnly.remove(tL_dialog) && DialogObject.isChannel(tL_dialog)) {
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            MessagesController.this.channelsPts.delete(-((int) j2));
                            MessagesController.this.shortPollChannels.delete(-((int) j2));
                            MessagesController.this.needShortPollChannels.delete(-((int) j2));
                        }
                    });
                }
                messagesController.dialogsGroupsOnly.remove(tL_dialog);
                messagesController.dialogs_dict.remove(j2);
                messagesController.dialogs_read_inbox_max.remove(Long.valueOf(j));
                messagesController.dialogs_read_outbox_max.remove(Long.valueOf(j));
                messagesController.nextDialogsCacheOffset--;
                messageObject = (MessageObject) messagesController.dialogMessage.get(tL_dialog.id);
                messagesController.dialogMessage.remove(tL_dialog.id);
                if (messageObject == null) {
                    id = tL_dialog.top_message;
                    messageObject = (MessageObject) messagesController.dialogMessagesByIds.get(tL_dialog.top_message);
                    messagesController.dialogMessagesByIds.remove(tL_dialog.top_message);
                } else {
                    id = messageObject.getId();
                    messagesController.dialogMessagesByIds.remove(messageObject.getId());
                }
                messagesController.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                if (i3 == 1) {
                }
                messagesController = this;
                tL_dialog.top_message = 0;
            } else {
                max = i2;
            }
            NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.valueOf(false));
            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$44$1 */
                class C03341 implements Runnable {
                    C03341() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(j2);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03341());
                }
            });
            i7 = max;
        } else {
            i7 = i2;
        }
        if (i5 != 1) {
            if (i3 != 3) {
                if (i4 != 0) {
                    InputPeer inputPeer = messagesController.getInputPeer(i4);
                    if (inputPeer != null) {
                        boolean z3 = inputPeer instanceof TL_inputPeerChannel;
                        i6 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        if (!z3) {
                            TLObject tL_messages_deleteHistory = new TL_messages_deleteHistory();
                            tL_messages_deleteHistory.peer = inputPeer;
                            if (i3 != 0) {
                                i6 = i7;
                            }
                            tL_messages_deleteHistory.max_id = i6;
                            if (i3 == 0) {
                                z2 = false;
                            }
                            tL_messages_deleteHistory.just_clear = z2;
                            ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_deleteHistory, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    if (tL_error == null) {
                                        TL_messages_affectedHistory tL_messages_affectedHistory = (TL_messages_affectedHistory) tLObject;
                                        if (tL_messages_affectedHistory.offset > null) {
                                            MessagesController.this.deleteDialog(j2, false, i3, i7);
                                        }
                                        MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
                                    }
                                }
                            }, 64);
                        } else if (i3 != 0) {
                            TLObject tL_channels_deleteHistory = new TL_channels_deleteHistory();
                            tL_channels_deleteHistory.channel = new TL_inputChannel();
                            tL_channels_deleteHistory.channel.channel_id = inputPeer.channel_id;
                            tL_channels_deleteHistory.channel.access_hash = inputPeer.access_hash;
                            if (i7 > 0) {
                                i6 = i7;
                            }
                            tL_channels_deleteHistory.max_id = i6;
                            ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_channels_deleteHistory, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                }
                            }, 64);
                        }
                    }
                } else if (i3 == 1) {
                    SecretChatHelper.getInstance(messagesController.currentAccount).sendClearHistoryMessage(messagesController.getEncryptedChat(Integer.valueOf(i5)), null);
                } else {
                    SecretChatHelper.getInstance(messagesController.currentAccount).declineSecretChat(i5);
                }
            }
        }
    }

    public void saveGif(Document document) {
        TLObject tL_messages_saveGif = new TL_messages_saveGif();
        tL_messages_saveGif.id = new TL_inputDocument();
        tL_messages_saveGif.id.id = document.id;
        tL_messages_saveGif.id.access_hash = document.access_hash;
        tL_messages_saveGif.unsave = null;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveGif, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void saveRecentSticker(Document document, boolean z) {
        TLObject tL_messages_saveRecentSticker = new TL_messages_saveRecentSticker();
        tL_messages_saveRecentSticker.id = new TL_inputDocument();
        tL_messages_saveRecentSticker.id.id = document.id;
        tL_messages_saveRecentSticker.id.access_hash = document.access_hash;
        tL_messages_saveRecentSticker.unsave = null;
        tL_messages_saveRecentSticker.attached = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveRecentSticker, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void loadChannelParticipants(final Integer num) {
        if (!this.loadingFullParticipants.contains(num)) {
            if (!this.loadedFullParticipants.contains(num)) {
                this.loadingFullParticipants.add(num);
                TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
                tL_channels_getParticipants.channel = getInputChannel(num.intValue());
                tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
                tL_channels_getParticipants.offset = 0;
                tL_channels_getParticipants.limit = 32;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (tL_error == null) {
                                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                                    MessagesController.this.putUsers(tL_channels_channelParticipants.users, false);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChannelUsers(num.intValue(), tL_channels_channelParticipants.participants);
                                    MessagesController.this.loadedFullParticipants.add(num);
                                }
                                MessagesController.this.loadingFullParticipants.remove(num);
                            }
                        });
                    }
                });
            }
        }
    }

    public void loadChatInfo(int i, CountDownLatch countDownLatch, boolean z) {
        MessagesStorage.getInstance(this.currentAccount).loadChatInfo(i, countDownLatch, z, false);
    }

    public void processChatInfo(int i, ChatFull chatFull, ArrayList<User> arrayList, boolean z, boolean z2, boolean z3, MessageObject messageObject) {
        if (z && i > 0 && !z3) {
            loadFullChat(i, 0, z2);
        }
        if (chatFull != null) {
            final ArrayList<User> arrayList2 = arrayList;
            final boolean z4 = z;
            final ChatFull chatFull2 = chatFull;
            final boolean z5 = z3;
            final MessageObject messageObject2 = messageObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.this.putUsers(arrayList2, z4);
                    if (chatFull2.stickerset != null) {
                        DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(chatFull2.stickerset);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull2, Integer.valueOf(0), Boolean.valueOf(z5), messageObject2);
                }
            });
        }
    }

    public void updateTimerProc() {
        int i;
        ArrayList arrayList;
        long currentTimeMillis = System.currentTimeMillis();
        boolean z = false;
        checkDeletingTask(false);
        checkReadTasks();
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            TLObject tL_account_updateStatus;
            if (ConnectionsManager.getInstance(r0.currentAccount).getPauseTime() == 0 && ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePausedStageQueue) {
                if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && r0.statusSettingState != 1 && (r0.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - r0.lastStatusUpdateTime) >= 55000 || r0.offlineSent)) {
                    r0.statusSettingState = 1;
                    if (r0.statusRequest != 0) {
                        ConnectionsManager.getInstance(r0.currentAccount).cancelRequest(r0.statusRequest, true);
                    }
                    tL_account_updateStatus = new TL_account_updateStatus();
                    tL_account_updateStatus.offline = false;
                    r0.statusRequest = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tL_account_updateStatus, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
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
                tL_account_updateStatus = new TL_account_updateStatus();
                tL_account_updateStatus.offline = true;
                r0.statusRequest = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tL_account_updateStatus, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            MessagesController.this.offlineSent = true;
                        } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                            MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                        }
                        MessagesController.this.statusRequest = null;
                    }
                });
            }
            if (r0.updatesQueueChannels.size() != 0) {
                for (i = 0; i < r0.updatesQueueChannels.size(); i++) {
                    int keyAt = r0.updatesQueueChannels.keyAt(i);
                    if (r0.updatesStartWaitTimeChannels.valueAt(i) + 1500 < currentTimeMillis) {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("QUEUE CHANNEL ");
                            stringBuilder.append(keyAt);
                            stringBuilder.append(" UPDATES WAIT TIMEOUT - CHECK QUEUE");
                            FileLog.m0d(stringBuilder.toString());
                        }
                        processChannelsUpdatesQueue(keyAt, 0);
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
                        FileLog.m0d(stringBuilder2.toString());
                    }
                    processUpdatesQueue(i, 0);
                }
                i++;
            }
        }
        if (r0.channelViewsToSend.size() != 0 && Math.abs(System.currentTimeMillis() - r0.lastViewsCheckTime) >= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            r0.lastViewsCheckTime = System.currentTimeMillis();
            i = 0;
            while (i < r0.channelViewsToSend.size()) {
                final int keyAt2 = r0.channelViewsToSend.keyAt(i);
                final TLObject tL_messages_getMessagesViews = new TL_messages_getMessagesViews();
                tL_messages_getMessagesViews.peer = getInputPeer(keyAt2);
                tL_messages_getMessagesViews.id = (ArrayList) r0.channelViewsToSend.valueAt(i);
                tL_messages_getMessagesViews.increment = i == 0;
                ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tL_messages_getMessagesViews, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            Vector vector = (Vector) tLObject;
                            tL_error = new SparseArray();
                            SparseIntArray sparseIntArray = (SparseIntArray) tL_error.get(keyAt2);
                            if (sparseIntArray == null) {
                                sparseIntArray = new SparseIntArray();
                                tL_error.put(keyAt2, sparseIntArray);
                            }
                            for (int i = 0; i < tL_messages_getMessagesViews.id.size(); i++) {
                                if (i >= vector.objects.size()) {
                                    break;
                                }
                                sparseIntArray.put(((Integer) tL_messages_getMessagesViews.id.get(i)).intValue(), ((Integer) vector.objects.get(i)).intValue());
                            }
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putChannelViews(tL_error, tL_messages_getMessagesViews.peer instanceof TL_inputPeerChannel);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, tL_error);
                                }
                            });
                        }
                    }
                });
                i++;
            }
            r0.channelViewsToSend.clear();
        }
        if (!r0.onlinePrivacy.isEmpty()) {
            arrayList = null;
            keyAt2 = ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime();
            for (Entry entry : r0.onlinePrivacy.entrySet()) {
                if (((Integer) entry.getValue()).intValue() < keyAt2 - 30) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(entry.getKey());
                }
            }
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
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
            for (i = 0; i < r0.shortPollChannels.size(); i++) {
                keyAt2 = r0.shortPollChannels.keyAt(i);
                if (((long) r0.shortPollChannels.valueAt(i)) < System.currentTimeMillis() / 1000) {
                    r0.shortPollChannels.delete(keyAt2);
                    if (r0.needShortPollChannels.indexOfKey(keyAt2) >= 0) {
                        getChannelDifference(keyAt2);
                    }
                }
            }
        }
        if (!(r0.printingUsers.isEmpty() && r0.lastPrintingStringCount == r0.printingUsers.size())) {
            arrayList = new ArrayList(r0.printingUsers.keySet());
            keyAt2 = 0;
            int i2 = keyAt2;
            while (keyAt2 < arrayList.size()) {
                ArrayList arrayList2;
                ArrayList arrayList3;
                long longValue = ((Long) arrayList.get(keyAt2)).longValue();
                ArrayList arrayList4 = (ArrayList) r0.printingUsers.get(Long.valueOf(longValue));
                if (arrayList4 != null) {
                    int i3 = i2;
                    i2 = z;
                    while (i2 < arrayList4.size()) {
                        PrintingUser printingUser = (PrintingUser) arrayList4.get(i2);
                        arrayList2 = arrayList;
                        if (printingUser.lastTime + ((long) (printingUser.action instanceof TL_sendMessageGamePlayAction ? DefaultLoadControl.DEFAULT_MAX_BUFFER_MS : 5900)) < currentTimeMillis) {
                            arrayList4.remove(printingUser);
                            i2--;
                            i3 = true;
                        }
                        i2++;
                        arrayList = arrayList2;
                    }
                    arrayList2 = arrayList;
                    i2 = i3;
                } else {
                    arrayList2 = arrayList;
                }
                if (arrayList4 != null) {
                    if (!arrayList4.isEmpty()) {
                        arrayList3 = arrayList2;
                        keyAt2++;
                        arrayList = arrayList3;
                        z = false;
                    }
                }
                r0.printingUsers.remove(Long.valueOf(longValue));
                arrayList3 = arrayList2;
                arrayList3.remove(keyAt2);
                keyAt2--;
                keyAt2++;
                arrayList = arrayList3;
                z = false;
            }
            updatePrintingStrings();
            if (i2 != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                    }
                });
            }
        }
        if (Theme.selectedAutoNightType == 1 && Math.abs(currentTimeMillis - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(r0.themeCheckRunnable);
            lastThemeCheckTime = currentTimeMillis;
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
        if (user.first_name == null || user.first_name.length() <= 0) {
            return (user.last_name == null || user.last_name.length() <= 0) ? TtmlNode.ANONYMOUS_REGION_ID : user.last_name;
        } else {
            return user.first_name;
        }
    }

    private void updatePrintingStrings() {
        final LongSparseArray longSparseArray = new LongSparseArray();
        final LongSparseArray longSparseArray2 = new LongSparseArray();
        ArrayList arrayList = new ArrayList(this.printingUsers.keySet());
        for (Entry entry : this.printingUsers.entrySet()) {
            long longValue = ((Long) entry.getKey()).longValue();
            ArrayList arrayList2 = (ArrayList) entry.getValue();
            int i = (int) longValue;
            if (i <= 0 && i != 0) {
                if (arrayList2.size() != 1) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Iterator it = arrayList2.iterator();
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
                            longSparseArray.put(longValue, LocaleController.formatString("IsTypingGroup", C0446R.string.IsTypingGroup, stringBuilder.toString()));
                        } else if (arrayList2.size() > 2) {
                            longSparseArray.put(longValue, String.format(LocaleController.getPluralString("AndMoreTypingGroup", arrayList2.size() - 2), new Object[]{stringBuilder.toString(), Integer.valueOf(arrayList2.size() - 2)}));
                        } else {
                            longSparseArray.put(longValue, LocaleController.formatString("AreTypingGroup", C0446R.string.AreTypingGroup, stringBuilder.toString()));
                        }
                        longSparseArray2.put(longValue, Integer.valueOf(0));
                    }
                }
            }
            PrintingUser printingUser = (PrintingUser) arrayList2.get(0);
            if (getUser(Integer.valueOf(printingUser.userId)) != null) {
                if (printingUser.action instanceof TL_sendMessageRecordAudioAction) {
                    if (i < 0) {
                        longSparseArray.put(longValue, LocaleController.formatString("IsRecordingAudio", C0446R.string.IsRecordingAudio, getUserNameForTyping(r11)));
                    } else {
                        longSparseArray.put(longValue, LocaleController.getString("RecordingAudio", C0446R.string.RecordingAudio));
                    }
                    longSparseArray2.put(longValue, Integer.valueOf(1));
                } else {
                    if (!(printingUser.action instanceof TL_sendMessageRecordRoundAction)) {
                        if (!(printingUser.action instanceof TL_sendMessageUploadRoundAction)) {
                            if (printingUser.action instanceof TL_sendMessageUploadAudioAction) {
                                if (i < 0) {
                                    longSparseArray.put(longValue, LocaleController.formatString("IsSendingAudio", C0446R.string.IsSendingAudio, getUserNameForTyping(r11)));
                                } else {
                                    longSparseArray.put(longValue, LocaleController.getString("SendingAudio", C0446R.string.SendingAudio));
                                }
                                longSparseArray2.put(longValue, Integer.valueOf(2));
                            } else {
                                if (!(printingUser.action instanceof TL_sendMessageUploadVideoAction)) {
                                    if (!(printingUser.action instanceof TL_sendMessageRecordVideoAction)) {
                                        if (printingUser.action instanceof TL_sendMessageUploadDocumentAction) {
                                            if (i < 0) {
                                                longSparseArray.put(longValue, LocaleController.formatString("IsSendingFile", C0446R.string.IsSendingFile, getUserNameForTyping(r11)));
                                            } else {
                                                longSparseArray.put(longValue, LocaleController.getString("SendingFile", C0446R.string.SendingFile));
                                            }
                                            longSparseArray2.put(longValue, Integer.valueOf(2));
                                        } else if (printingUser.action instanceof TL_sendMessageUploadPhotoAction) {
                                            if (i < 0) {
                                                longSparseArray.put(longValue, LocaleController.formatString("IsSendingPhoto", C0446R.string.IsSendingPhoto, getUserNameForTyping(r11)));
                                            } else {
                                                longSparseArray.put(longValue, LocaleController.getString("SendingPhoto", C0446R.string.SendingPhoto));
                                            }
                                            longSparseArray2.put(longValue, Integer.valueOf(2));
                                        } else if (printingUser.action instanceof TL_sendMessageGamePlayAction) {
                                            if (i < 0) {
                                                longSparseArray.put(longValue, LocaleController.formatString("IsSendingGame", C0446R.string.IsSendingGame, getUserNameForTyping(r11)));
                                            } else {
                                                longSparseArray.put(longValue, LocaleController.getString("SendingGame", C0446R.string.SendingGame));
                                            }
                                            longSparseArray2.put(longValue, Integer.valueOf(3));
                                        } else {
                                            if (i < 0) {
                                                longSparseArray.put(longValue, LocaleController.formatString("IsTypingGroup", C0446R.string.IsTypingGroup, getUserNameForTyping(r11)));
                                            } else {
                                                longSparseArray.put(longValue, LocaleController.getString("Typing", C0446R.string.Typing));
                                            }
                                            longSparseArray2.put(longValue, Integer.valueOf(0));
                                        }
                                    }
                                }
                                if (i < 0) {
                                    longSparseArray.put(longValue, LocaleController.formatString("IsSendingVideo", C0446R.string.IsSendingVideo, getUserNameForTyping(r11)));
                                } else {
                                    longSparseArray.put(longValue, LocaleController.getString("SendingVideoStatus", C0446R.string.SendingVideoStatus));
                                }
                                longSparseArray2.put(longValue, Integer.valueOf(2));
                            }
                        }
                    }
                    if (i < 0) {
                        longSparseArray.put(longValue, LocaleController.formatString("IsRecordingRound", C0446R.string.IsRecordingRound, getUserNameForTyping(r11)));
                    } else {
                        longSparseArray.put(longValue, LocaleController.getString("RecordingRound", C0446R.string.RecordingRound));
                    }
                    longSparseArray2.put(longValue, Integer.valueOf(4));
                }
            }
        }
        this.lastPrintingStringCount = longSparseArray.size();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.printingStrings = longSparseArray;
                MessagesController.this.printingStringsTypes = longSparseArray2;
            }
        });
    }

    public void cancelTyping(int i, long j) {
        LongSparseArray longSparseArray = (LongSparseArray) this.sendingTypings.get(i);
        if (longSparseArray != null) {
            longSparseArray.remove(j);
        }
    }

    public void sendTyping(final long j, final int i, int i2) {
        if (j != 0) {
            LongSparseArray longSparseArray = (LongSparseArray) this.sendingTypings.get(i);
            if (longSparseArray == null || longSparseArray.get(j) == null) {
                if (longSparseArray == null) {
                    longSparseArray = new LongSparseArray();
                    this.sendingTypings.put(i, longSparseArray);
                }
                int i3 = (int) j;
                int i4 = (int) (j >> 32);
                TLObject tL_messages_setTyping;
                if (i3 != 0) {
                    if (i4 != 1) {
                        tL_messages_setTyping = new TL_messages_setTyping();
                        tL_messages_setTyping.peer = getInputPeer(i3);
                        if (tL_messages_setTyping.peer instanceof TL_inputPeerChannel) {
                            Chat chat = getChat(Integer.valueOf(tL_messages_setTyping.peer.channel_id));
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
                            j = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_setTyping, new RequestDelegate() {

                                /* renamed from: org.telegram.messenger.MessagesController$57$1 */
                                class C03381 implements Runnable {
                                    C03381() {
                                    }

                                    public void run() {
                                        LongSparseArray longSparseArray = (LongSparseArray) MessagesController.this.sendingTypings.get(i);
                                        if (longSparseArray != null) {
                                            longSparseArray.remove(j);
                                        }
                                    }
                                }

                                public void run(TLObject tLObject, TL_error tL_error) {
                                    AndroidUtilities.runOnUIThread(new C03381());
                                }
                            }, 2);
                            if (i2 != 0) {
                                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(j, i2);
                            }
                        }
                    }
                } else if (i == 0) {
                    EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i4));
                    if (encryptedChat.auth_key != null && encryptedChat.auth_key.length > 1 && (encryptedChat instanceof TL_encryptedChat)) {
                        tL_messages_setTyping = new TL_messages_setEncryptedTyping();
                        tL_messages_setTyping.peer = new TL_inputEncryptedChat();
                        tL_messages_setTyping.peer.chat_id = encryptedChat.id;
                        tL_messages_setTyping.peer.access_hash = encryptedChat.access_hash;
                        tL_messages_setTyping.typing = true;
                        longSparseArray.put(j, Boolean.valueOf(true));
                        j = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_setTyping, new RequestDelegate() {

                            /* renamed from: org.telegram.messenger.MessagesController$58$1 */
                            class C03391 implements Runnable {
                                C03391() {
                                }

                                public void run() {
                                    LongSparseArray longSparseArray = (LongSparseArray) MessagesController.this.sendingTypings.get(i);
                                    if (longSparseArray != null) {
                                        longSparseArray.remove(j);
                                    }
                                }
                            }

                            public void run(TLObject tLObject, TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new C03391());
                            }
                        }, 2);
                        if (i2 != 0) {
                            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(j, i2);
                        }
                    }
                }
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
        int i14;
        int i15;
        MessagesController messagesController = this;
        final long j2 = j;
        final int i16 = i;
        int i17 = i2;
        boolean z5 = z;
        int i18 = i5;
        final int i19 = i6;
        final int i20 = i7;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("load messages in chat ");
            stringBuilder.append(j2);
            stringBuilder.append(" count ");
            stringBuilder.append(i16);
            stringBuilder.append(" max_id ");
            stringBuilder.append(i17);
            stringBuilder.append(" cache ");
            stringBuilder.append(z5);
            stringBuilder.append(" mindate = ");
            i13 = i4;
            stringBuilder.append(i13);
            stringBuilder.append(" guid ");
            stringBuilder.append(i18);
            stringBuilder.append(" load_type ");
            stringBuilder.append(i19);
            stringBuilder.append(" last_message_id ");
            stringBuilder.append(i20);
            stringBuilder.append(" index ");
            i14 = i8;
            stringBuilder.append(i14);
            stringBuilder.append(" firstUnread ");
            stringBuilder.append(i9);
            stringBuilder.append(" unread_count ");
            i15 = i10;
            stringBuilder.append(i15);
            stringBuilder.append(" last_date ");
            stringBuilder.append(i11);
            stringBuilder.append(" queryFromServer ");
            stringBuilder.append(z3);
            FileLog.m0d(stringBuilder.toString());
        } else {
            i13 = i4;
            i14 = i8;
            int i21 = i9;
            i15 = i10;
            int i22 = i11;
            boolean z6 = z3;
        }
        int i23 = (int) j2;
        if (!z5) {
            if (i23 != 0) {
                TLObject tLObject;
                if (z4 && ((i19 == 3 || i19 == 2) && i20 == 0)) {
                    TLObject tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
                    InputPeer inputPeer = getInputPeer(i23);
                    TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                    tL_inputDialogPeer.peer = inputPeer;
                    tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                    final long j3 = j2;
                    final int i24 = i17;
                    final int i25 = i3;
                    i17 = i13;
                    i13 = i18;
                    ConnectionsManager instance = ConnectionsManager.getInstance(messagesController.currentAccount);
                    i20 = i19;
                    TLObject tLObject2 = tL_messages_getPeerDialogs;
                    final boolean z7 = z2;
                    i21 = i14;
                    TLObject tLObject3 = tLObject2;
                    i19 = i9;
                    AnonymousClass59 anonymousClass59 = r0;
                    tLObject = tLObject3;
                    i14 = i11;
                    TLObject tLObject4 = tLObject;
                    ConnectionsManager connectionsManager = instance;
                    final boolean z8 = z3;
                    AnonymousClass59 anonymousClass592 = new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            AnonymousClass59 anonymousClass59 = this;
                            if (tLObject != null) {
                                TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
                                if (!tL_messages_peerDialogs.dialogs.isEmpty()) {
                                    TL_dialog tL_dialog = (TL_dialog) tL_messages_peerDialogs.dialogs.get(0);
                                    if (tL_dialog.top_message != 0) {
                                        messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                                        tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
                                        tL_messages_dialogs.users = tL_messages_peerDialogs.users;
                                        tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
                                        tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(tL_messages_dialogs, false);
                                    }
                                    MessagesController messagesController = MessagesController.this;
                                    long j = j3;
                                    int i = i16;
                                    int i2 = i24;
                                    int i3 = i25;
                                    int i4 = i17;
                                    int i5 = i13;
                                    int i6 = i20;
                                    int i7 = tL_dialog.top_message;
                                    boolean z = z7;
                                    int i8 = i21;
                                    int i9 = i19;
                                    int i10 = tL_dialog.unread_count;
                                    int i11 = i14;
                                    int i12 = i10;
                                    int i13 = i11;
                                    boolean z2 = z;
                                    int i14 = i8;
                                    int i15 = i9;
                                    messagesController.loadMessagesInternal(j, i, i2, i3, false, i4, i5, i6, i7, z2, i14, i15, i12, i13, z8, tL_dialog.unread_mentions_count, false);
                                }
                            }
                        }
                    };
                    connectionsManager.sendRequest(tLObject4, anonymousClass59);
                    return;
                }
                tLObject = new TL_messages_getHistory();
                tLObject.peer = getInputPeer(i23);
                if (i19 == 4) {
                    tLObject.add_offset = (-i16) + 5;
                } else if (i19 == 3) {
                    tLObject.add_offset = (-i16) / 2;
                } else if (i19 == 1) {
                    tLObject.add_offset = (-i16) - 1;
                } else if (i19 == 2 && i17 != 0) {
                    tLObject.add_offset = (-i16) + 6;
                } else if (i23 < 0 && i17 != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-i23)))) {
                    tLObject.add_offset = -1;
                    tLObject.limit++;
                }
                tLObject.limit = i16;
                tLObject.offset_id = i17;
                i13 = i3;
                tLObject.offset_date = i13;
                AnonymousClass60 anonymousClass60 = r0;
                i22 = i16;
                final int i26 = i17;
                ConnectionsManager instance2 = ConnectionsManager.getInstance(r11.currentAccount);
                i16 = i13;
                ConnectionsManager connectionsManager2 = instance2;
                i17 = i18;
                ConnectionsManager connectionsManager3 = connectionsManager2;
                i13 = i9;
                i21 = i11;
                final boolean z9 = z2;
                i18 = i8;
                TLObject tLObject5 = tLObject;
                final boolean z10 = z3;
                final int i27 = i12;
                AnonymousClass60 anonymousClass602 = new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        AnonymousClass60 anonymousClass60 = this;
                        if (tLObject != null) {
                            messages_Messages messages_messages = (messages_Messages) tLObject;
                            if (messages_messages.messages.size() > i22) {
                                messages_messages.messages.remove(0);
                            }
                            int i = i26;
                            if (i16 != 0 && !messages_messages.messages.isEmpty()) {
                                i = ((Message) messages_messages.messages.get(messages_messages.messages.size() - 1)).id;
                                for (int size = messages_messages.messages.size() - 1; size >= 0; size--) {
                                    Message message = (Message) messages_messages.messages.get(size);
                                    if (message.date > i16) {
                                        i = message.id;
                                        break;
                                    }
                                }
                            }
                            int i2 = i;
                            MessagesController messagesController = MessagesController.this;
                            long j = j2;
                            int i3 = i22;
                            int i4 = i16;
                            int i5 = i17;
                            int i6 = i13;
                            int i7 = i20;
                            int i8 = i15;
                            int i9 = i21;
                            int i10 = i19;
                            boolean z = z9;
                            int i11 = i18;
                            messagesController.processLoadedMessages(messages_messages, j, i3, i2, i4, false, i5, i6, i7, i8, i9, i10, z, false, i11, z10, i27);
                        }
                    }
                };
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(connectionsManager3.sendRequest(tLObject5, anonymousClass60), i5);
            }
        }
        MessagesStorage.getInstance(messagesController.currentAccount).getMessages(j2, i16, i17, i3, i13, i18, i19, z2, i8);
    }

    public void reloadWebPages(final long j, HashMap<String, ArrayList<MessageObject>> hashMap) {
        hashMap = hashMap.entrySet().iterator();
        while (hashMap.hasNext()) {
            Entry entry = (Entry) hashMap.next();
            final String str = (String) entry.getKey();
            ArrayList arrayList = (ArrayList) entry.getValue();
            ArrayList arrayList2 = (ArrayList) this.reloadingWebpages.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
                this.reloadingWebpages.put(str, arrayList2);
            }
            arrayList2.addAll(arrayList);
            TLObject tL_messages_getWebPagePreview = new TL_messages_getWebPagePreview();
            tL_messages_getWebPagePreview.message = str;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getWebPagePreview, new RequestDelegate() {
                public void run(final TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ArrayList arrayList = (ArrayList) MessagesController.this.reloadingWebpages.remove(str);
                            if (arrayList != null) {
                                messages_Messages tL_messages_messages = new TL_messages_messages();
                                if (tLObject instanceof TL_messageMediaWebPage) {
                                    TL_messageMediaWebPage tL_messageMediaWebPage = (TL_messageMediaWebPage) tLObject;
                                    if (!(tL_messageMediaWebPage.webpage instanceof TL_webPage)) {
                                        if (!(tL_messageMediaWebPage.webpage instanceof TL_webPageEmpty)) {
                                            MessagesController.this.reloadingWebpagesPending.put(tL_messageMediaWebPage.webpage.id, arrayList);
                                        }
                                    }
                                    for (int i = 0; i < arrayList.size(); i++) {
                                        ((MessageObject) arrayList.get(i)).messageOwner.media.webpage = tL_messageMediaWebPage.webpage;
                                        if (i == 0) {
                                            ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(i)).messageOwner);
                                        }
                                        tL_messages_messages.messages.add(((MessageObject) arrayList.get(i)).messageOwner);
                                    }
                                } else {
                                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                                        ((MessageObject) arrayList.get(i2)).messageOwner.media.webpage = new TL_webPageEmpty();
                                        tL_messages_messages.messages.add(((MessageObject) arrayList.get(i2)).messageOwner);
                                    }
                                }
                                if (!tL_messages_messages.messages.isEmpty()) {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(tL_messages_messages, j, -2, 0, false);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    public void processLoadedMessages(messages_Messages messages_messages, long j, int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7, int i8, int i9, boolean z2, boolean z3, int i10, boolean z4, int i11) {
        messages_Messages messages_messages2;
        long j2;
        int i12;
        int i13;
        boolean z5;
        int i14;
        int i15;
        int i16;
        int i17;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processLoadedMessages size ");
            messages_messages2 = messages_messages;
            stringBuilder.append(messages_messages2.messages.size());
            stringBuilder.append(" in chat ");
            j2 = j;
            stringBuilder.append(j2);
            stringBuilder.append(" count ");
            i12 = i;
            stringBuilder.append(i12);
            stringBuilder.append(" max_id ");
            i13 = i2;
            stringBuilder.append(i13);
            stringBuilder.append(" cache ");
            z5 = z;
            stringBuilder.append(z5);
            stringBuilder.append(" guid ");
            i14 = i4;
            stringBuilder.append(i14);
            stringBuilder.append(" load_type ");
            i15 = i9;
            stringBuilder.append(i15);
            stringBuilder.append(" last_message_id ");
            i16 = i6;
            stringBuilder.append(i16);
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
            FileLog.m0d(stringBuilder.toString());
        } else {
            messages_messages2 = messages_messages;
            j2 = j;
            i12 = i;
            i13 = i2;
            z5 = z;
            i14 = i4;
            int i18 = i5;
            i16 = i6;
            int i19 = i7;
            int i20 = i8;
            i15 = i9;
            boolean z6 = z2;
            i17 = i10;
            boolean z7 = z4;
        }
        final messages_Messages messages_messages3 = messages_messages2;
        final boolean z8 = z4;
        i17 = i5;
        final int i21 = i3;
        final boolean z9 = z2;
        final int i22 = i10;
        final int i23 = i7;
        final int i24 = i8;
        final int i25 = i11;
        final boolean z10 = z3;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$62$1 */
            class C03431 implements Runnable {
                C03431() {
                }

                public void run() {
                    MessagesController messagesController = MessagesController.this;
                    long j = j2;
                    int i = i12;
                    int i2 = (i15 == 2 && z8) ? i17 : i13;
                    int i3 = i2;
                    int i4 = i21;
                    int i5 = i14;
                    int i6 = i15;
                    int i7 = i16;
                    boolean z = z9;
                    int i8 = i22;
                    int i9 = i17;
                    i2 = i23;
                    int i10 = i2;
                    messagesController.loadMessages(j, i, i3, i4, false, 0, i5, i6, i7, z, i8, i9, i10, i24, z8, i25);
                }
            }

            public void run() {
                int i;
                boolean z;
                boolean z2;
                if (messages_messages3 instanceof TL_messages_channelMessages) {
                    boolean z3;
                    int i2 = -((int) j2);
                    if (MessagesController.this.channelsPts.get(i2) == 0 && MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(i2) == 0) {
                        MessagesController.this.channelsPts.put(i2, messages_messages3.pts);
                        if (MessagesController.this.needShortPollChannels.indexOfKey(i2) < 0 || MessagesController.this.shortPollChannels.indexOfKey(i2) >= 0) {
                            MessagesController.this.getChannelDifference(i2);
                        } else {
                            MessagesController.this.getChannelDifference(i2, 2, 0, null);
                        }
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    for (i = 0; i < messages_messages3.chats.size(); i++) {
                        Chat chat = (Chat) messages_messages3.chats.get(i);
                        if (chat.id == i2) {
                            z = chat.megagroup;
                            break;
                        }
                    }
                    z = false;
                    z2 = z3;
                } else {
                    z = false;
                    z2 = z;
                }
                int i3 = (int) j2;
                i = (int) (j2 >> 32);
                if (!z5) {
                    ImageLoader.saveMessagesThumbs(messages_messages3.messages);
                }
                if (i == 1 || i3 == 0 || !z5 || messages_messages3.messages.size() != 0) {
                    SparseArray sparseArray = new SparseArray();
                    SparseArray sparseArray2 = new SparseArray();
                    for (i3 = 0; i3 < messages_messages3.users.size(); i3++) {
                        User user = (User) messages_messages3.users.get(i3);
                        sparseArray.put(user.id, user);
                    }
                    for (i3 = 0; i3 < messages_messages3.chats.size(); i3++) {
                        Chat chat2 = (Chat) messages_messages3.chats.get(i3);
                        sparseArray2.put(chat2.id, chat2);
                    }
                    int size = messages_messages3.messages.size();
                    if (!z5) {
                        Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j2));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j2));
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j2), num);
                        }
                        Integer num2 = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j2));
                        if (num2 == null) {
                            num2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j2));
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j2), num2);
                        }
                        for (int i4 = 0; i4 < size; i4++) {
                            Message message = (Message) messages_messages3.messages.get(i4);
                            if (z) {
                                message.flags |= Integer.MIN_VALUE;
                            }
                            if (message.action instanceof TL_messageActionChatDeleteUser) {
                                User user2 = (User) sparseArray.get(message.action.user_id);
                                if (user2 != null && user2.bot) {
                                    message.reply_markup = new TL_replyKeyboardHide();
                                    message.flags |= 64;
                                }
                            }
                            if (!(message.action instanceof TL_messageActionChatMigrateTo)) {
                                if (!(message.action instanceof TL_messageActionChannelCreate)) {
                                    message.unread = (message.out ? num2 : num).intValue() < message.id;
                                }
                            }
                            message.unread = false;
                            message.media_unread = false;
                        }
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messages_messages3, j2, i15, i13, z2);
                    }
                    final ArrayList arrayList = new ArrayList();
                    final ArrayList arrayList2 = new ArrayList();
                    final HashMap hashMap = new HashMap();
                    int i5 = 0;
                    while (i5 < size) {
                        Object obj;
                        Object obj2;
                        int i6;
                        Message message2 = (Message) messages_messages3.messages.get(i5);
                        message2.dialog_id = j2;
                        MessageObject messageObject = r4;
                        Message message3 = message2;
                        MessageObject messageObject2 = new MessageObject(MessagesController.this.currentAccount, message2, sparseArray, sparseArray2, true);
                        arrayList.add(messageObject);
                        if (z5) {
                            if (!(message3.media instanceof TL_messageMediaUnsupported)) {
                                obj = 1;
                                i = 0;
                                if (message3.media instanceof TL_messageMediaWebPage) {
                                    if ((message3.media.webpage instanceof TL_webPagePending) && message3.media.webpage.date <= ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime()) {
                                        arrayList2.add(Integer.valueOf(message3.id));
                                    } else if (message3.media.webpage instanceof TL_webPageUrlPending) {
                                        ArrayList arrayList3 = (ArrayList) hashMap.get(message3.media.webpage.url);
                                        if (arrayList3 == null) {
                                            arrayList3 = new ArrayList();
                                            hashMap.put(message3.media.webpage.url, arrayList3);
                                        }
                                        arrayList3.add(messageObject);
                                    }
                                }
                            } else if (message3.media.bytes != null) {
                                if (message3.media.bytes.length != 0) {
                                    obj = 1;
                                    if (message3.media.bytes.length == 1) {
                                        i = 0;
                                        if (message3.media.bytes[0] < (byte) 76) {
                                        }
                                    }
                                    i = 0;
                                } else {
                                    obj = 1;
                                    i = 0;
                                }
                                arrayList2.add(Integer.valueOf(message3.id));
                            }
                            i5++;
                            obj2 = obj;
                            i6 = i;
                        }
                        obj = 1;
                        i = 0;
                        i5++;
                        obj2 = obj;
                        i6 = i;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            int i;
                            MessagesController.this.putUsers(messages_messages3.users, z5);
                            MessagesController.this.putChats(messages_messages3.chats, z5);
                            if (z8 && i15 == 2) {
                                i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                for (int i2 = 0; i2 < messages_messages3.messages.size(); i2++) {
                                    Message message = (Message) messages_messages3.messages.get(i2);
                                    if (!message.out && message.id > i17 && message.id < r4) {
                                        i = message.id;
                                    }
                                }
                            } else {
                                i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            }
                            if (i == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                i = i17;
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDidLoaded, Long.valueOf(j2), Integer.valueOf(i12), arrayList, Boolean.valueOf(z5), Integer.valueOf(i), Integer.valueOf(i16), Integer.valueOf(i23), Integer.valueOf(i24), Integer.valueOf(i15), Boolean.valueOf(z10), Integer.valueOf(i14), Integer.valueOf(i22), Integer.valueOf(i13), Integer.valueOf(i25));
                            if (!arrayList2.isEmpty()) {
                                MessagesController.this.reloadMessages(arrayList2, j2);
                            }
                            if (!hashMap.isEmpty()) {
                                MessagesController.this.reloadWebPages(j2, hashMap);
                            }
                        }
                    });
                    return;
                }
                AndroidUtilities.runOnUIThread(new C03431());
            }
        });
    }

    public void loadHintDialogs() {
        if (this.hintDialogs.isEmpty()) {
            if (!TextUtils.isEmpty(this.installReferer)) {
                TLObject tL_help_getRecentMeUrls = new TL_help_getRecentMeUrls();
                tL_help_getRecentMeUrls.referer = this.installReferer;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_getRecentMeUrls, new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TL_help_recentMeUrls tL_help_recentMeUrls = (TL_help_recentMeUrls) tLObject;
                                    MessagesController.this.putUsers(tL_help_recentMeUrls.users, false);
                                    MessagesController.this.putChats(tL_help_recentMeUrls.chats, false);
                                    MessagesController.this.hintDialogs.clear();
                                    MessagesController.this.hintDialogs.addAll(tL_help_recentMeUrls.urls);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    public void loadDialogs(int i, final int i2, boolean z) {
        if (!this.loadingDialogs) {
            if (!this.resetingDialogs) {
                boolean z2 = true;
                this.loadingDialogs = true;
                int i3 = 0;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("load cacheOffset = ");
                    stringBuilder.append(i);
                    stringBuilder.append(" count = ");
                    stringBuilder.append(i2);
                    stringBuilder.append(" cache = ");
                    stringBuilder.append(z);
                    FileLog.m0d(stringBuilder.toString());
                }
                if (z) {
                    z = MessagesStorage.getInstance(this.currentAccount);
                    if (i != 0) {
                        i3 = this.nextDialogsCacheOffset;
                    }
                    z.getDialogs(i3, i2);
                } else {
                    i = new TL_messages_getDialogs();
                    i.limit = i2;
                    i.exclude_pinned = true;
                    if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId) {
                        for (z = this.dialogs.size() - true; z < false; z--) {
                            TL_dialog tL_dialog = (TL_dialog) this.dialogs.get(z);
                            if (!tL_dialog.pinned) {
                                int i4 = (int) (tL_dialog.id >> 32);
                                if (!(((int) tL_dialog.id) == 0 || i4 == 1 || tL_dialog.top_message <= 0)) {
                                    MessageObject messageObject = (MessageObject) this.dialogMessage.get(tL_dialog.id);
                                    if (messageObject != null && messageObject.getId() > 0) {
                                        i.offset_date = messageObject.messageOwner.date;
                                        i.offset_id = messageObject.messageOwner.id;
                                        if (messageObject.messageOwner.to_id.channel_id) {
                                            z = -messageObject.messageOwner.to_id.channel_id;
                                        } else if (messageObject.messageOwner.to_id.chat_id) {
                                            z = -messageObject.messageOwner.to_id.chat_id;
                                        } else {
                                            z = messageObject.messageOwner.to_id.user_id;
                                        }
                                        i.offset_peer = getInputPeer(z);
                                        if (!z2) {
                                            i.offset_peer = new TL_inputPeerEmpty();
                                        }
                                    }
                                }
                            }
                        }
                        z2 = false;
                        if (z2) {
                            i.offset_peer = new TL_inputPeerEmpty();
                        }
                    } else if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId) {
                        this.dialogsEndReached = true;
                        this.serverDialogsEndReached = true;
                        this.loadingDialogs = false;
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        return;
                    } else {
                        i.offset_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId;
                        i.offset_date = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate;
                        if (i.offset_id) {
                            if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId) {
                                i.offset_peer = new TL_inputPeerChannel();
                                i.offset_peer.channel_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId;
                            } else if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId) {
                                i.offset_peer = new TL_inputPeerUser();
                                i.offset_peer.user_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId;
                            } else {
                                i.offset_peer = new TL_inputPeerChat();
                                i.offset_peer.chat_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId;
                            }
                            i.offset_peer.access_hash = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess;
                        } else {
                            i.offset_peer = new TL_inputPeerEmpty();
                        }
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(i, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
                                MessagesController.this.processLoadedDialogs((messages_Dialogs) tLObject, null, 0, i2, 0, false, false, false);
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

    private void resetDialogs(boolean z, int i, int i2, int i3, int i4) {
        MessagesController messagesController = this;
        final int i5;
        final int i6;
        final int i7;
        if (z) {
            if (!messagesController.resetingDialogs) {
                messagesController.resetingDialogs = true;
                i5 = i;
                final int i8 = i2;
                i6 = i3;
                i7 = i4;
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tLObject != null) {
                            MessagesController.this.resetDialogsPinned = (TL_messages_peerDialogs) tLObject;
                            MessagesController.this.resetDialogs(false, i5, i8, i6, i7);
                        }
                    }
                });
                TLObject tL_messages_getDialogs = new TL_messages_getDialogs();
                tL_messages_getDialogs.limit = 100;
                tL_messages_getDialogs.exclude_pinned = true;
                tL_messages_getDialogs.offset_peer = new TL_inputPeerEmpty();
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_getDialogs, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            MessagesController.this.resetDialogsAll = (messages_Dialogs) tLObject;
                            MessagesController.this.resetDialogs(false, i5, i8, i6, i7);
                        }
                    }
                });
            }
        } else if (!(messagesController.resetDialogsPinned == null || messagesController.resetDialogsAll == null)) {
            Chat chat;
            Integer num;
            int size = messagesController.resetDialogsAll.messages.size();
            int size2 = messagesController.resetDialogsAll.dialogs.size();
            messagesController.resetDialogsAll.dialogs.addAll(messagesController.resetDialogsPinned.dialogs);
            messagesController.resetDialogsAll.messages.addAll(messagesController.resetDialogsPinned.messages);
            messagesController.resetDialogsAll.users.addAll(messagesController.resetDialogsPinned.users);
            messagesController.resetDialogsAll.chats.addAll(messagesController.resetDialogsPinned.chats);
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (i6 = 0; i6 < messagesController.resetDialogsAll.users.size(); i6++) {
                User user = (User) messagesController.resetDialogsAll.users.get(i6);
                sparseArray.put(user.id, user);
            }
            for (i6 = 0; i6 < messagesController.resetDialogsAll.chats.size(); i6++) {
                Chat chat2 = (Chat) messagesController.resetDialogsAll.chats.get(i6);
                sparseArray2.put(chat2.id, chat2);
            }
            Message message = null;
            for (i7 = 0; i7 < messagesController.resetDialogsAll.messages.size(); i7++) {
                Message message2 = (Message) messagesController.resetDialogsAll.messages.get(i7);
                if (i7 < size && (message == null || message2.date < message.date)) {
                    message = message2;
                }
                if (message2.to_id.channel_id != 0) {
                    chat = (Chat) sparseArray2.get(message2.to_id.channel_id);
                    if (chat != null && chat.left) {
                    } else if (chat != null && chat.megagroup) {
                        message2.flags |= Integer.MIN_VALUE;
                    }
                } else if (message2.to_id.chat_id != 0) {
                    chat = (Chat) sparseArray2.get(message2.to_id.chat_id);
                    if (!(chat == null || chat.migrated_to == null)) {
                    }
                }
                MessageObject messageObject = new MessageObject(messagesController.currentAccount, message2, sparseArray, sparseArray2, false);
                longSparseArray2.put(messageObject.getDialogId(), messageObject);
            }
            for (i7 = 0; i7 < messagesController.resetDialogsAll.dialogs.size(); i7++) {
                TL_dialog tL_dialog = (TL_dialog) messagesController.resetDialogsAll.dialogs.get(i7);
                if (tL_dialog.id == 0 && tL_dialog.peer != null) {
                    if (tL_dialog.peer.user_id != 0) {
                        tL_dialog.id = (long) tL_dialog.peer.user_id;
                    } else if (tL_dialog.peer.chat_id != 0) {
                        tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                    } else if (tL_dialog.peer.channel_id != 0) {
                        tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                    }
                }
                if (tL_dialog.id != 0) {
                    if (tL_dialog.last_message_date == 0) {
                        MessageObject messageObject2 = (MessageObject) longSparseArray2.get(tL_dialog.id);
                        if (messageObject2 != null) {
                            tL_dialog.last_message_date = messageObject2.messageOwner.date;
                        }
                    }
                    if (DialogObject.isChannel(tL_dialog)) {
                        chat = (Chat) sparseArray2.get(-((int) tL_dialog.id));
                        if (chat == null || !chat.left) {
                            messagesController.channelsPts.put(-((int) tL_dialog.id), tL_dialog.pts);
                        }
                    } else if (((int) tL_dialog.id) < 0) {
                        chat = (Chat) sparseArray2.get(-((int) tL_dialog.id));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    longSparseArray.put(tL_dialog.id, tL_dialog);
                    num = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    messagesController.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_inbox_max_id)));
                    num = (Integer) messagesController.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    messagesController.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_outbox_max_id)));
                }
            }
            ImageLoader.saveMessagesThumbs(messagesController.resetDialogsAll.messages);
            for (i5 = 0; i5 < messagesController.resetDialogsAll.messages.size(); i5++) {
                Message message3 = (Message) messagesController.resetDialogsAll.messages.get(i5);
                if (message3.action instanceof TL_messageActionChatDeleteUser) {
                    User user2 = (User) sparseArray.get(message3.action.user_id);
                    if (user2 != null && user2.bot) {
                        message3.reply_markup = new TL_replyKeyboardHide();
                        message3.flags |= 64;
                    }
                }
                if (!(message3.action instanceof TL_messageActionChatMigrateTo)) {
                    if (!(message3.action instanceof TL_messageActionChannelCreate)) {
                        ConcurrentHashMap concurrentHashMap = message3.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                        num = (Integer) concurrentHashMap.get(Long.valueOf(message3.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message3.out, message3.dialog_id));
                            concurrentHashMap.put(Long.valueOf(message3.dialog_id), num);
                        }
                        message3.unread = num.intValue() < message3.id;
                    }
                }
                message3.unread = false;
                message3.media_unread = false;
            }
            MessagesStorage.getInstance(messagesController.currentAccount).resetDialogs(messagesController.resetDialogsAll, size, i, i2, i3, i4, longSparseArray, longSparseArray2, message, size2);
            messagesController.resetDialogsPinned = null;
            messagesController.resetDialogsAll = null;
        }
    }

    protected void completeDialogsReset(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, LongSparseArray<TL_dialog> longSparseArray, LongSparseArray<MessageObject> longSparseArray2, Message message) {
        final int i6 = i3;
        final int i7 = i4;
        final int i8 = i5;
        final messages_Dialogs messages_dialogs2 = messages_dialogs;
        final LongSparseArray<TL_dialog> longSparseArray3 = longSparseArray;
        final LongSparseArray<MessageObject> longSparseArray4 = longSparseArray2;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$67$1 */
            class C03461 implements Runnable {
                C03461() {
                }

                public void run() {
                    int i;
                    MessagesController.this.resetingDialogs = false;
                    MessagesController.this.applyDialogsNotificationsSettings(messages_dialogs2.dialogs);
                    if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
                        DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
                    }
                    MessagesController.this.putUsers(messages_dialogs2.users, false);
                    MessagesController.this.putChats(messages_dialogs2.chats, false);
                    for (i = 0; i < MessagesController.this.dialogs.size(); i++) {
                        TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs.get(i);
                        if (((int) tL_dialog.id) != 0) {
                            MessagesController.this.dialogs_dict.remove(tL_dialog.id);
                            MessageObject messageObject = (MessageObject) MessagesController.this.dialogMessage.get(tL_dialog.id);
                            MessagesController.this.dialogMessage.remove(tL_dialog.id);
                            if (messageObject != null) {
                                MessagesController.this.dialogMessagesByIds.remove(messageObject.getId());
                                if (messageObject.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                                }
                            }
                        }
                    }
                    for (i = 0; i < longSparseArray3.size(); i++) {
                        long keyAt = longSparseArray3.keyAt(i);
                        tL_dialog = (TL_dialog) longSparseArray3.valueAt(i);
                        if (tL_dialog.draft instanceof TL_draftMessage) {
                            DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(tL_dialog.id, tL_dialog.draft, null, false);
                        }
                        MessagesController.this.dialogs_dict.put(keyAt, tL_dialog);
                        MessageObject messageObject2 = (MessageObject) longSparseArray4.get(tL_dialog.id);
                        MessagesController.this.dialogMessage.put(keyAt, messageObject2);
                        if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                            MessagesController.this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                            if (messageObject2.messageOwner.random_id != 0) {
                                MessagesController.this.dialogMessagesByRandomIds.put(messageObject2.messageOwner.random_id, messageObject2);
                            }
                        }
                    }
                    MessagesController.this.dialogs.clear();
                    i = MessagesController.this.dialogs_dict.size();
                    for (int i2 = 0; i2 < i; i2++) {
                        MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(i2));
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
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(i6);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(i7);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(i8);
                MessagesController.this.getDifference();
                AndroidUtilities.runOnUIThread(new C03461());
            }
        });
    }

    private void migrateDialogs(final int i, int i2, int i3, int i4, int i5, long j) {
        if (!this.migratingDialogs) {
            if (i != -1) {
                this.migratingDialogs = true;
                TLObject tL_messages_getDialogs = new TL_messages_getDialogs();
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
                    FileLog.m0d(stringBuilder.toString());
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getDialogs, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$68$2 */
                    class C03492 implements Runnable {
                        C03492() {
                        }

                        public void run() {
                            MessagesController.this.migratingDialogs = false;
                        }
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            final messages_Dialogs messages_dialogs = (messages_Dialogs) tLObject;
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
                                        int i;
                                        StringBuilder stringBuilder;
                                        long longValue;
                                        TL_dialog tL_dialog;
                                        int i2;
                                        UserConfig instance = UserConfig.getInstance(MessagesController.this.currentAccount);
                                        instance.totalDialogsLoadCount += messages_dialogs.dialogs.size();
                                        int i3 = 0;
                                        Message message = null;
                                        for (i = 0; i < messages_dialogs.messages.size(); i++) {
                                            Message message2 = (Message) messages_dialogs.messages.get(i);
                                            if (BuildVars.LOGS_ENABLED) {
                                                StringBuilder stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append("search migrate id ");
                                                stringBuilder2.append(message2.id);
                                                stringBuilder2.append(" date ");
                                                stringBuilder2.append(LocaleController.getInstance().formatterStats.format(((long) message2.date) * 1000));
                                                FileLog.m0d(stringBuilder2.toString());
                                            }
                                            if (message == null || message2.date < message.date) {
                                                message = message2;
                                            }
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("migrate step with id ");
                                            stringBuilder.append(message.id);
                                            stringBuilder.append(" date ");
                                            stringBuilder.append(LocaleController.getInstance().formatterStats.format(((long) message.date) * 1000));
                                            FileLog.m0d(stringBuilder.toString());
                                        }
                                        if (messages_dialogs.dialogs.size() >= 100) {
                                            i = message.id;
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
                                            i = -1;
                                        }
                                        StringBuilder stringBuilder3 = new StringBuilder(messages_dialogs.dialogs.size() * 12);
                                        LongSparseArray longSparseArray = new LongSparseArray();
                                        for (int i4 = 0; i4 < messages_dialogs.dialogs.size(); i4++) {
                                            TL_dialog tL_dialog2 = (TL_dialog) messages_dialogs.dialogs.get(i4);
                                            if (tL_dialog2.peer.channel_id != 0) {
                                                tL_dialog2.id = (long) (-tL_dialog2.peer.channel_id);
                                            } else if (tL_dialog2.peer.chat_id != 0) {
                                                tL_dialog2.id = (long) (-tL_dialog2.peer.chat_id);
                                            } else {
                                                tL_dialog2.id = (long) tL_dialog2.peer.user_id;
                                            }
                                            if (stringBuilder3.length() > 0) {
                                                stringBuilder3.append(",");
                                            }
                                            stringBuilder3.append(tL_dialog2.id);
                                            longSparseArray.put(tL_dialog2.id, tL_dialog2);
                                        }
                                        SQLiteCursor queryFinalized = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", new Object[]{stringBuilder3.toString()}), new Object[0]);
                                        while (queryFinalized.next()) {
                                            longValue = queryFinalized.longValue(0);
                                            tL_dialog = (TL_dialog) longSparseArray.get(longValue);
                                            longSparseArray.remove(longValue);
                                            if (tL_dialog != null) {
                                                messages_dialogs.dialogs.remove(tL_dialog);
                                                int i5 = 0;
                                                while (i5 < messages_dialogs.messages.size()) {
                                                    Message message3 = (Message) messages_dialogs.messages.get(i5);
                                                    if (MessageObject.getDialogId(message3) == longValue) {
                                                        messages_dialogs.messages.remove(i5);
                                                        i5--;
                                                        if (message3.id == tL_dialog.top_message) {
                                                            tL_dialog.top_message = 0;
                                                            break;
                                                        }
                                                    }
                                                    i5++;
                                                }
                                            }
                                        }
                                        queryFinalized.dispose();
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("migrate found missing dialogs ");
                                            stringBuilder3.append(messages_dialogs.dialogs.size());
                                            FileLog.m0d(stringBuilder3.toString());
                                        }
                                        queryFinalized = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                                        if (queryFinalized.next()) {
                                            int max = Math.max(NUM, queryFinalized.intValue(0));
                                            int i6 = i;
                                            i = 0;
                                            while (i < messages_dialogs.messages.size()) {
                                                Message message4 = (Message) messages_dialogs.messages.get(i);
                                                if (message4.date < max) {
                                                    if (i != -1) {
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            StringBuilder stringBuilder4 = new StringBuilder();
                                                            stringBuilder4.append("migrate stop due to reached loaded dialogs ");
                                                            stringBuilder4.append(LocaleController.getInstance().formatterStats.format(((long) max) * 1000));
                                                            FileLog.m0d(stringBuilder4.toString());
                                                        }
                                                        i6 = -1;
                                                    }
                                                    messages_dialogs.messages.remove(i);
                                                    i--;
                                                    longValue = MessageObject.getDialogId(message4);
                                                    tL_dialog = (TL_dialog) longSparseArray.get(longValue);
                                                    longSparseArray.remove(longValue);
                                                    if (tL_dialog != null) {
                                                        messages_dialogs.dialogs.remove(tL_dialog);
                                                    }
                                                }
                                                i++;
                                            }
                                            if (message == null || message.date >= max || i == -1) {
                                                i2 = i6;
                                            } else {
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("migrate stop due to reached loaded dialogs ");
                                                    stringBuilder.append(LocaleController.getInstance().formatterStats.format(((long) max) * 1000));
                                                    FileLog.m0d(stringBuilder.toString());
                                                }
                                                i2 = -1;
                                            }
                                        } else {
                                            i2 = i;
                                        }
                                        queryFinalized.dispose();
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate = message.date;
                                        Chat chat;
                                        if (message.to_id.channel_id != 0) {
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = message.to_id.channel_id;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                                            while (i3 < messages_dialogs.chats.size()) {
                                                chat = (Chat) messages_dialogs.chats.get(i3);
                                                if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId) {
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = chat.access_hash;
                                                    break;
                                                }
                                                i3++;
                                            }
                                        } else if (message.to_id.chat_id != 0) {
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = message.to_id.chat_id;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                                            while (i3 < messages_dialogs.chats.size()) {
                                                chat = (Chat) messages_dialogs.chats.get(i3);
                                                if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId) {
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = chat.access_hash;
                                                    break;
                                                }
                                                i3++;
                                            }
                                        } else if (message.to_id.user_id != 0) {
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = message.to_id.user_id;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                                            while (i3 < messages_dialogs.users.size()) {
                                                User user = (User) messages_dialogs.users.get(i3);
                                                if (user.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId) {
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = user.access_hash;
                                                    break;
                                                }
                                                i3++;
                                            }
                                        }
                                        MessagesController.this.processLoadedDialogs(messages_dialogs, null, i2, 0, 0, false, true, false);
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

    public void processLoadedDialogs(messages_Dialogs messages_dialogs, ArrayList<EncryptedChat> arrayList, int i, int i2, int i3, boolean z, boolean z2, boolean z3) {
        final int i4 = i3;
        final messages_Dialogs messages_dialogs2 = messages_dialogs;
        final boolean z4 = z;
        final int i5 = i2;
        final int i6 = i;
        final boolean z5 = z3;
        final boolean z6 = z2;
        final ArrayList<EncryptedChat> arrayList2 = arrayList;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$69$1 */
            class C03501 implements Runnable {
                C03501() {
                }

                public void run() {
                    MessagesController.this.putUsers(messages_dialogs2.users, true);
                    MessagesController.this.loadingDialogs = false;
                    if (z4) {
                        MessagesController.this.dialogsEndReached = false;
                        MessagesController.this.serverDialogsEndReached = false;
                    } else if (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        MessagesController.this.dialogsEndReached = true;
                        MessagesController.this.serverDialogsEndReached = true;
                    } else {
                        MessagesController.this.loadDialogs(0, i5, false);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }

            public void run() {
                if (!MessagesController.this.firstGettingTask) {
                    MessagesController.this.getNewDeleteTask(null, 0);
                    MessagesController.this.firstGettingTask = true;
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("loaded loadType ");
                    stringBuilder.append(i4);
                    stringBuilder.append(" count ");
                    stringBuilder.append(messages_dialogs2.dialogs.size());
                    FileLog.m0d(stringBuilder.toString());
                }
                if (i4 == 1 && messages_dialogs2.dialogs.size() == 0) {
                    AndroidUtilities.runOnUIThread(new C03501());
                    return;
                }
                int i;
                LongSparseArray longSparseArray = new LongSparseArray();
                final LongSparseArray longSparseArray2 = new LongSparseArray();
                SparseArray sparseArray = new SparseArray();
                SparseArray sparseArray2 = new SparseArray();
                for (i = 0; i < messages_dialogs2.users.size(); i++) {
                    User user = (User) messages_dialogs2.users.get(i);
                    sparseArray.put(user.id, user);
                }
                for (i = 0; i < messages_dialogs2.chats.size(); i++) {
                    Chat chat = (Chat) messages_dialogs2.chats.get(i);
                    sparseArray2.put(chat.id, chat);
                }
                if (i4 == 1) {
                    MessagesController.this.nextDialogsCacheOffset = i6 + i5;
                }
                Message message = null;
                int i2 = 0;
                while (i2 < messages_dialogs2.messages.size()) {
                    Message message2;
                    Chat chat2;
                    MessageObject messageObject;
                    Message message3 = (Message) messages_dialogs2.messages.get(i2);
                    if (message != null) {
                        if (message3.date >= message.date) {
                            message2 = message;
                            if (message3.to_id.channel_id != 0) {
                                chat2 = (Chat) sparseArray2.get(message3.to_id.channel_id);
                                if (chat2 == null && chat2.left) {
                                    i2++;
                                    message = message2;
                                } else if (chat2 != null && chat2.megagroup) {
                                    message3.flags |= Integer.MIN_VALUE;
                                }
                            } else if (message3.to_id.chat_id != 0) {
                                chat2 = (Chat) sparseArray2.get(message3.to_id.chat_id);
                                if (!(chat2 == null || chat2.migrated_to == null)) {
                                    i2++;
                                    message = message2;
                                }
                            }
                            messageObject = new MessageObject(MessagesController.this.currentAccount, message3, sparseArray, sparseArray2, false);
                            longSparseArray2.put(messageObject.getDialogId(), messageObject);
                            i2++;
                            message = message2;
                        }
                    }
                    message2 = message3;
                    if (message3.to_id.channel_id != 0) {
                        chat2 = (Chat) sparseArray2.get(message3.to_id.channel_id);
                        if (chat2 == null) {
                        }
                        message3.flags |= Integer.MIN_VALUE;
                    } else if (message3.to_id.chat_id != 0) {
                        chat2 = (Chat) sparseArray2.get(message3.to_id.chat_id);
                        i2++;
                        message = message2;
                    }
                    messageObject = new MessageObject(MessagesController.this.currentAccount, message3, sparseArray, sparseArray2, false);
                    longSparseArray2.put(messageObject.getDialogId(), messageObject);
                    i2++;
                    message = message2;
                }
                if (!(z5 || z6 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || i4 != 0)) {
                    if (message == null || message.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId) {
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    } else {
                        UserConfig instance = UserConfig.getInstance(MessagesController.this.currentAccount);
                        instance.totalDialogsLoadCount += messages_dialogs2.dialogs.size();
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = message.id;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = message.date;
                        if (message.to_id.channel_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = message.to_id.channel_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (i2 = 0; i2 < messages_dialogs2.chats.size(); i2++) {
                                chat2 = (Chat) messages_dialogs2.chats.get(i2);
                                if (chat2.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = chat2.access_hash;
                                    break;
                                }
                            }
                        } else if (message.to_id.chat_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = message.to_id.chat_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (i2 = 0; i2 < messages_dialogs2.chats.size(); i2++) {
                                chat2 = (Chat) messages_dialogs2.chats.get(i2);
                                if (chat2.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = chat2.access_hash;
                                    break;
                                }
                            }
                        } else if (message.to_id.user_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = message.to_id.user_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            for (i2 = 0; i2 < messages_dialogs2.users.size(); i2++) {
                                User user2 = (User) messages_dialogs2.users.get(i2);
                                if (user2.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = user2.access_hash;
                                    break;
                                }
                            }
                        }
                    }
                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                }
                final ArrayList arrayList = new ArrayList();
                for (i2 = 0; i2 < messages_dialogs2.dialogs.size(); i2++) {
                    TL_dialog tL_dialog = (TL_dialog) messages_dialogs2.dialogs.get(i2);
                    if (tL_dialog.id == 0 && tL_dialog.peer != null) {
                        if (tL_dialog.peer.user_id != 0) {
                            tL_dialog.id = (long) tL_dialog.peer.user_id;
                        } else if (tL_dialog.peer.chat_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                        } else if (tL_dialog.peer.channel_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                        }
                    }
                    if (tL_dialog.id != 0) {
                        boolean z;
                        if (tL_dialog.last_message_date == 0) {
                            MessageObject messageObject2 = (MessageObject) longSparseArray2.get(tL_dialog.id);
                            if (messageObject2 != null) {
                                tL_dialog.last_message_date = messageObject2.messageOwner.date;
                            }
                        }
                        Chat chat3;
                        if (DialogObject.isChannel(tL_dialog)) {
                            chat3 = (Chat) sparseArray2.get(-((int) tL_dialog.id));
                            if (chat3 != null) {
                                z = chat3.megagroup;
                                if (chat3.left) {
                                }
                            } else {
                                z = true;
                            }
                            MessagesController.this.channelsPts.put(-((int) tL_dialog.id), tL_dialog.pts);
                        } else {
                            if (((int) tL_dialog.id) < 0) {
                                chat3 = (Chat) sparseArray2.get(-((int) tL_dialog.id));
                                if (!(chat3 == null || chat3.migrated_to == null)) {
                                }
                            }
                            z = true;
                        }
                        longSparseArray.put(tL_dialog.id, tL_dialog);
                        if (z && i4 == 1 && ((tL_dialog.read_outbox_max_id == 0 || tL_dialog.read_inbox_max_id == 0) && tL_dialog.top_message != 0)) {
                            arrayList.add(tL_dialog);
                        }
                        Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                        if (num == null) {
                            num = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_inbox_max_id)));
                        num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                        if (num == null) {
                            num = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_outbox_max_id)));
                    }
                }
                if (i4 != 1) {
                    ImageLoader.saveMessagesThumbs(messages_dialogs2.messages);
                    for (i2 = 0; i2 < messages_dialogs2.messages.size(); i2++) {
                        message = (Message) messages_dialogs2.messages.get(i2);
                        if (message.action instanceof TL_messageActionChatDeleteUser) {
                            User user3 = (User) sparseArray.get(message.action.user_id);
                            if (user3 != null && user3.bot) {
                                message.reply_markup = new TL_replyKeyboardHide();
                                message.flags |= 64;
                            }
                        }
                        if (!(message.action instanceof TL_messageActionChatMigrateTo)) {
                            if (!(message.action instanceof TL_messageActionChannelCreate)) {
                                ConcurrentHashMap concurrentHashMap = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                                Integer num2 = (Integer) concurrentHashMap.get(Long.valueOf(message.dialog_id));
                                if (num2 == null) {
                                    num2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                                    concurrentHashMap.put(Long.valueOf(message.dialog_id), num2);
                                }
                                message.unread = num2.intValue() < message.id;
                            }
                        }
                        message.unread = false;
                        message.media_unread = false;
                    }
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(messages_dialogs2, false);
                }
                if (i4 == 2) {
                    Chat chat4 = (Chat) messages_dialogs2.chats.get(0);
                    MessagesController.this.getChannelDifference(chat4.id);
                    MessagesController.this.checkChannelInviter(chat4.id);
                }
                final LongSparseArray longSparseArray3 = longSparseArray;
                final SparseArray sparseArray3 = sparseArray2;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int i;
                        int i2;
                        if (i4 != 1) {
                            MessagesController.this.applyDialogsNotificationsSettings(messages_dialogs2.dialogs);
                            if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
                                DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
                            }
                        }
                        MessagesController.this.putUsers(messages_dialogs2.users, i4 == 1);
                        MessagesController.this.putChats(messages_dialogs2.chats, i4 == 1);
                        SparseArray sparseArray = null;
                        if (arrayList2 != null) {
                            for (i = 0; i < arrayList2.size(); i++) {
                                EncryptedChat encryptedChat = (EncryptedChat) arrayList2.get(i);
                                if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 73) {
                                    SecretChatHelper.getInstance(MessagesController.this.currentAccount).sendNotifyLayerMessage(encryptedChat, null);
                                }
                                MessagesController.this.putEncryptedChat(encryptedChat, true);
                            }
                        }
                        if (!z6) {
                            MessagesController.this.loadingDialogs = false;
                        }
                        i = (!z6 || MessagesController.this.dialogs.isEmpty()) ? 0 : ((TL_dialog) MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
                        int i3 = 0;
                        int i4 = i3;
                        while (i3 < longSparseArray3.size()) {
                            long keyAt = longSparseArray3.keyAt(i3);
                            TL_dialog tL_dialog = (TL_dialog) longSparseArray3.valueAt(i3);
                            if (!z6 || i == 0 || tL_dialog.last_message_date >= i) {
                                TL_dialog tL_dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(keyAt);
                                if (i4 == 1 || !(tL_dialog.draft instanceof TL_draftMessage)) {
                                    i2 = i4;
                                } else {
                                    i2 = i4;
                                    DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(tL_dialog.id, tL_dialog.draft, null, false);
                                }
                                MessageObject messageObject;
                                if (tL_dialog2 == null) {
                                    MessagesController.this.dialogs_dict.put(keyAt, tL_dialog);
                                    messageObject = (MessageObject) longSparseArray2.get(tL_dialog.id);
                                    MessagesController.this.dialogMessage.put(keyAt, messageObject);
                                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                        if (messageObject.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                        }
                                    }
                                    i4 = true;
                                    i3++;
                                } else {
                                    MessageObject messageObject2;
                                    if (i4 != 1) {
                                        tL_dialog2.notify_settings = tL_dialog.notify_settings;
                                    }
                                    tL_dialog2.pinned = tL_dialog.pinned;
                                    tL_dialog2.pinnedNum = tL_dialog.pinnedNum;
                                    messageObject = (MessageObject) MessagesController.this.dialogMessage.get(keyAt);
                                    if ((messageObject == null || !messageObject.deleted) && messageObject != null) {
                                        if (tL_dialog2.top_message <= 0) {
                                            messageObject2 = (MessageObject) longSparseArray2.get(tL_dialog.id);
                                            if (messageObject.deleted || messageObject2 == null || messageObject2.messageOwner.date > messageObject.messageOwner.date) {
                                                MessagesController.this.dialogs_dict.put(keyAt, tL_dialog);
                                                MessagesController.this.dialogMessage.put(keyAt, messageObject2);
                                                if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                                                    MessagesController.this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                                                    if (!(messageObject2 == null || messageObject2.messageOwner.random_id == 0)) {
                                                        MessagesController.this.dialogMessagesByRandomIds.put(messageObject2.messageOwner.random_id, messageObject2);
                                                    }
                                                }
                                                MessagesController.this.dialogMessagesByIds.remove(messageObject.getId());
                                                if (messageObject.messageOwner.random_id != 0) {
                                                    MessagesController.this.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                                                }
                                            }
                                        }
                                    }
                                    if (tL_dialog.top_message >= tL_dialog2.top_message) {
                                        MessagesController.this.dialogs_dict.put(keyAt, tL_dialog);
                                        messageObject2 = (MessageObject) longSparseArray2.get(tL_dialog.id);
                                        MessagesController.this.dialogMessage.put(keyAt, messageObject2);
                                        if (messageObject2 != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                                            MessagesController.this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                                            if (!(messageObject2 == null || messageObject2.messageOwner.random_id == 0)) {
                                                MessagesController.this.dialogMessagesByRandomIds.put(messageObject2.messageOwner.random_id, messageObject2);
                                            }
                                        }
                                        if (messageObject != null) {
                                            MessagesController.this.dialogMessagesByIds.remove(messageObject.getId());
                                            if (messageObject.messageOwner.random_id != 0) {
                                                MessagesController.this.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                                            }
                                        }
                                    }
                                }
                            } else {
                                i2 = i4;
                            }
                            i4 = i2;
                            i3++;
                        }
                        i2 = i4;
                        MessagesController.this.dialogs.clear();
                        i = MessagesController.this.dialogs_dict.size();
                        for (i3 = 0; i3 < i; i3++) {
                            MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(i3));
                        }
                        MessagesController messagesController = MessagesController.this;
                        if (z6) {
                            sparseArray = sparseArray3;
                        }
                        messagesController.sortDialogs(sparseArray);
                        if (!(i4 == 2 || z6)) {
                            messagesController = MessagesController.this;
                            boolean z = (messages_dialogs2.dialogs.size() == 0 || messages_dialogs2.dialogs.size() != i5) && i4 == 0;
                            messagesController.dialogsEndReached = z;
                            if (!z5) {
                                messagesController = MessagesController.this;
                                z = (messages_dialogs2.dialogs.size() == 0 || messages_dialogs2.dialogs.size() != i5) && i4 == 0;
                                messagesController.serverDialogsEndReached = z;
                            }
                        }
                        if (!(z5 || z6 || UserConfig.getInstance(MessagesController.this.currentAccount).totalDialogsLoadCount >= 400 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID)) {
                            MessagesController.this.loadDialogs(0, 100, false);
                        }
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        if (z6) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId = i6;
                            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                            MessagesController.this.migratingDialogs = false;
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                        } else {
                            MessagesController.this.generateUpdateMessage();
                            if (i2 == 0 && i4 == 1) {
                                MessagesController.this.loadDialogs(0, i5, false);
                            }
                        }
                        MessagesController.this.migrateDialogs(UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess);
                        if (!arrayList.isEmpty()) {
                            MessagesController.this.reloadDialogsReadValue(arrayList, 0);
                        }
                    }
                });
            }
        });
    }

    private void applyDialogNotificationsSettings(long j, PeerNotifySettings peerNotifySettings) {
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(j);
        int i = sharedPreferences.getInt(stringBuilder.toString(), 0);
        SharedPreferences sharedPreferences2 = this.notificationsPreferences;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("notifyuntil_");
        stringBuilder2.append(j);
        int i2 = sharedPreferences2.getInt(stringBuilder2.toString(), 0);
        Editor edit = this.notificationsPreferences.edit();
        TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(j);
        if (tL_dialog != null) {
            tL_dialog.notify_settings = peerNotifySettings;
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("silent_");
        stringBuilder3.append(j);
        edit.putBoolean(stringBuilder3.toString(), peerNotifySettings.silent);
        int i3 = 1;
        if (peerNotifySettings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
            StringBuilder stringBuilder4;
            if (peerNotifySettings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
                if (i == 3) {
                    if (i2 == peerNotifySettings.mute_until) {
                        i3 = 0;
                        peerNotifySettings = peerNotifySettings.mute_until;
                    }
                }
                stringBuilder4 = new StringBuilder();
                stringBuilder4.append("notify2_");
                stringBuilder4.append(j);
                edit.putInt(stringBuilder4.toString(), 3);
                stringBuilder4 = new StringBuilder();
                stringBuilder4.append("notifyuntil_");
                stringBuilder4.append(j);
                edit.putInt(stringBuilder4.toString(), peerNotifySettings.mute_until);
                if (tL_dialog != null) {
                    tL_dialog.notify_settings.mute_until = 0;
                }
                peerNotifySettings = peerNotifySettings.mute_until;
            } else if (i != 2) {
                stringBuilder4 = new StringBuilder();
                stringBuilder4.append("notify2_");
                stringBuilder4.append(j);
                edit.putInt(stringBuilder4.toString(), 2);
                if (tL_dialog != null) {
                    tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                }
                peerNotifySettings = null;
            } else {
                peerNotifySettings = null;
                i3 = peerNotifySettings;
            }
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, (((long) peerNotifySettings) << 32) | 1);
            NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(j);
        } else {
            if (i == 0 || i == 1) {
                i3 = 0;
            } else {
                if (tL_dialog != null) {
                    tL_dialog.notify_settings.mute_until = 0;
                }
                peerNotifySettings = new StringBuilder();
                peerNotifySettings.append("notify2_");
                peerNotifySettings.append(j);
                edit.remove(peerNotifySettings.toString());
            }
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, 0);
        }
        edit.commit();
        if (i3 != 0) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<TL_dialog> arrayList) {
        Editor editor = null;
        for (int i = 0; i < arrayList.size(); i++) {
            TL_dialog tL_dialog = (TL_dialog) arrayList.get(i);
            if (tL_dialog.peer != null && (tL_dialog.notify_settings instanceof TL_peerNotifySettings)) {
                int i2;
                if (editor == null) {
                    editor = this.notificationsPreferences.edit();
                }
                if (tL_dialog.peer.user_id != 0) {
                    i2 = tL_dialog.peer.user_id;
                } else if (tL_dialog.peer.chat_id != 0) {
                    i2 = -tL_dialog.peer.chat_id;
                } else {
                    i2 = -tL_dialog.peer.channel_id;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("silent_");
                stringBuilder.append(i2);
                editor.putBoolean(stringBuilder.toString(), tL_dialog.notify_settings.silent);
                if (tL_dialog.notify_settings.mute_until == 0) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("notify2_");
                    stringBuilder2.append(i2);
                    editor.remove(stringBuilder2.toString());
                } else if (tL_dialog.notify_settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(i2);
                    editor.putInt(stringBuilder.toString(), 2);
                    tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(i2);
                    editor.putInt(stringBuilder.toString(), 3);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("notifyuntil_");
                    stringBuilder.append(i2);
                    editor.putInt(stringBuilder.toString(), tL_dialog.notify_settings.mute_until);
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
                for (int i = 0; i < arrayList.size(); i++) {
                    final long j = (long) (-((Integer) arrayList.get(i)).intValue());
                    TLObject tL_messages_getUnreadMentions = new TL_messages_getUnreadMentions();
                    tL_messages_getUnreadMentions.peer = MessagesController.this.getInputPeer((int) j);
                    tL_messages_getUnreadMentions.limit = 1;
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(tL_messages_getUnreadMentions, new RequestDelegate() {
                        public void run(final TLObject tLObject, TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    messages_Messages messages_messages = (messages_Messages) tLObject;
                                    if (messages_messages != null) {
                                        int i;
                                        if (messages_messages.count != 0) {
                                            i = messages_messages.count;
                                        } else {
                                            i = messages_messages.messages.size();
                                        }
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).resetMentionsCount(j, i);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void processDialogsUpdateRead(final LongSparseArray<Integer> longSparseArray, final LongSparseArray<Integer> longSparseArray2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i;
                if (longSparseArray != null) {
                    for (i = 0; i < longSparseArray.size(); i++) {
                        TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(longSparseArray.keyAt(i));
                        if (tL_dialog != null) {
                            tL_dialog.unread_count = ((Integer) longSparseArray.valueAt(i)).intValue();
                        }
                    }
                }
                if (longSparseArray2 != null) {
                    for (i = 0; i < longSparseArray2.size(); i++) {
                        TL_dialog tL_dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(longSparseArray2.keyAt(i));
                        if (tL_dialog2 != null) {
                            tL_dialog2.unread_mentions_count = ((Integer) longSparseArray2.valueAt(i)).intValue();
                            if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(tL_dialog2.id))) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(tL_dialog2.id), Integer.valueOf(tL_dialog2.unread_mentions_count));
                            }
                        }
                    }
                }
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                if (longSparseArray != null) {
                    NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(longSparseArray);
                }
            }
        });
    }

    protected void checkLastDialogMessage(TL_dialog tL_dialog, InputPeer inputPeer, long j) {
        Throwable e;
        final long j2;
        final TL_dialog tL_dialog2;
        final int i = (int) tL_dialog.id;
        if (i != 0) {
            if (this.checkingLastMessagesDialogs.indexOfKey(i) < 0) {
                TLObject tL_messages_getHistory = new TL_messages_getHistory();
                tL_messages_getHistory.peer = inputPeer == null ? getInputPeer(i) : inputPeer;
                if (tL_messages_getHistory.peer != null) {
                    if (!(tL_messages_getHistory.peer instanceof TL_inputPeerChannel)) {
                        tL_messages_getHistory.limit = 1;
                        this.checkingLastMessagesDialogs.put(i, true);
                        if (j == 0) {
                            NativeByteBuffer nativeByteBuffer;
                            try {
                                nativeByteBuffer = new NativeByteBuffer(48 + tL_messages_getHistory.peer.getObjectSize());
                                try {
                                    nativeByteBuffer.writeInt32(8);
                                    nativeByteBuffer.writeInt64(tL_dialog.id);
                                    nativeByteBuffer.writeInt32(tL_dialog.top_message);
                                    nativeByteBuffer.writeInt32(tL_dialog.read_inbox_max_id);
                                    nativeByteBuffer.writeInt32(tL_dialog.read_outbox_max_id);
                                    nativeByteBuffer.writeInt32(tL_dialog.unread_count);
                                    nativeByteBuffer.writeInt32(tL_dialog.last_message_date);
                                    nativeByteBuffer.writeInt32(tL_dialog.pts);
                                    nativeByteBuffer.writeInt32(tL_dialog.flags);
                                    nativeByteBuffer.writeBool(tL_dialog.pinned);
                                    nativeByteBuffer.writeInt32(tL_dialog.pinnedNum);
                                    nativeByteBuffer.writeInt32(tL_dialog.unread_mentions_count);
                                    inputPeer.serializeToStream(nativeByteBuffer);
                                } catch (Exception e2) {
                                    e = e2;
                                    FileLog.m3e(e);
                                    j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                                    j2 = j;
                                    tL_dialog2 = tL_dialog;
                                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getHistory, new RequestDelegate() {

                                        /* renamed from: org.telegram.messenger.MessagesController$72$1 */
                                        class C03531 implements Runnable {
                                            C03531() {
                                            }

                                            public void run() {
                                                TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(tL_dialog2.id);
                                                if (tL_dialog != null && tL_dialog.top_message == 0) {
                                                    MessagesController.this.deleteDialog(tL_dialog2.id, 3);
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

                                        public void run(TLObject tLObject, TL_error tL_error) {
                                            if (tLObject != null) {
                                                messages_Messages messages_messages = (messages_Messages) tLObject;
                                                if (messages_messages.messages.isEmpty() == null) {
                                                    tL_error = new TL_messages_dialogs();
                                                    Message message = (Message) messages_messages.messages.get(0);
                                                    TL_dialog tL_dialog = new TL_dialog();
                                                    tL_dialog.flags = tL_dialog2.flags;
                                                    tL_dialog.top_message = message.id;
                                                    tL_dialog.last_message_date = message.date;
                                                    tL_dialog.notify_settings = tL_dialog2.notify_settings;
                                                    tL_dialog.pts = tL_dialog2.pts;
                                                    tL_dialog.unread_count = tL_dialog2.unread_count;
                                                    tL_dialog.unread_mentions_count = tL_dialog2.unread_mentions_count;
                                                    tL_dialog.read_inbox_max_id = tL_dialog2.read_inbox_max_id;
                                                    tL_dialog.read_outbox_max_id = tL_dialog2.read_outbox_max_id;
                                                    tL_dialog.pinned = tL_dialog2.pinned;
                                                    tL_dialog.pinnedNum = tL_dialog2.pinnedNum;
                                                    long j = tL_dialog2.id;
                                                    tL_dialog.id = j;
                                                    message.dialog_id = j;
                                                    tL_error.users.addAll(messages_messages.users);
                                                    tL_error.chats.addAll(messages_messages.chats);
                                                    tL_error.dialogs.add(tL_dialog);
                                                    tL_error.messages.addAll(messages_messages.messages);
                                                    tL_error.count = 1;
                                                    MessagesController.this.processDialogsUpdate(tL_error, null);
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messages_messages.messages, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask(), true);
                                                } else {
                                                    AndroidUtilities.runOnUIThread(new C03531());
                                                }
                                            }
                                            if (j2 != 0) {
                                                MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j2);
                                            }
                                            AndroidUtilities.runOnUIThread(new C03542());
                                        }
                                    });
                                }
                            } catch (Exception e3) {
                                e = e3;
                                nativeByteBuffer = 0;
                                FileLog.m3e(e);
                                j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                                j2 = j;
                                tL_dialog2 = tL_dialog;
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getHistory, /* anonymous class already generated */);
                            }
                            j = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                        }
                        j2 = j;
                        tL_dialog2 = tL_dialog;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getHistory, /* anonymous class already generated */);
                    }
                }
            }
        }
    }

    public void processDialogsUpdate(final messages_Dialogs messages_dialogs, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                int i;
                final LongSparseArray longSparseArray = new LongSparseArray();
                final LongSparseArray longSparseArray2 = new LongSparseArray();
                SparseArray sparseArray = new SparseArray(messages_dialogs.users.size());
                SparseArray sparseArray2 = new SparseArray(messages_dialogs.chats.size());
                final LongSparseArray longSparseArray3 = new LongSparseArray();
                for (i = 0; i < messages_dialogs.users.size(); i++) {
                    User user = (User) messages_dialogs.users.get(i);
                    sparseArray.put(user.id, user);
                }
                for (i = 0; i < messages_dialogs.chats.size(); i++) {
                    Chat chat = (Chat) messages_dialogs.chats.get(i);
                    sparseArray2.put(chat.id, chat);
                }
                for (int i2 = 0; i2 < messages_dialogs.messages.size(); i2++) {
                    Message message = (Message) messages_dialogs.messages.get(i2);
                    Chat chat2;
                    if (message.to_id.channel_id != 0) {
                        chat2 = (Chat) sparseArray2.get(message.to_id.channel_id);
                        if (chat2 != null && chat2.left) {
                        }
                    } else if (message.to_id.chat_id != 0) {
                        chat2 = (Chat) sparseArray2.get(message.to_id.chat_id);
                        if (!(chat2 == null || chat2.migrated_to == null)) {
                        }
                    }
                    MessageObject messageObject = new MessageObject(MessagesController.this.currentAccount, message, sparseArray, sparseArray2, false);
                    longSparseArray2.put(messageObject.getDialogId(), messageObject);
                }
                for (i = 0; i < messages_dialogs.dialogs.size(); i++) {
                    TL_dialog tL_dialog = (TL_dialog) messages_dialogs.dialogs.get(i);
                    if (tL_dialog.id == 0) {
                        if (tL_dialog.peer.user_id != 0) {
                            tL_dialog.id = (long) tL_dialog.peer.user_id;
                        } else if (tL_dialog.peer.chat_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                        } else if (tL_dialog.peer.channel_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                        }
                    }
                    Chat chat3;
                    if (DialogObject.isChannel(tL_dialog)) {
                        chat3 = (Chat) sparseArray2.get(-((int) tL_dialog.id));
                        if (chat3 != null && chat3.left) {
                        }
                    } else if (((int) tL_dialog.id) < 0) {
                        chat3 = (Chat) sparseArray2.get(-((int) tL_dialog.id));
                        if (!(chat3 == null || chat3.migrated_to == null)) {
                        }
                    }
                    if (tL_dialog.last_message_date == 0) {
                        MessageObject messageObject2 = (MessageObject) longSparseArray2.get(tL_dialog.id);
                        if (messageObject2 != null) {
                            tL_dialog.last_message_date = messageObject2.messageOwner.date;
                        }
                    }
                    longSparseArray.put(tL_dialog.id, tL_dialog);
                    longSparseArray3.put(tL_dialog.id, Integer.valueOf(tL_dialog.unread_count));
                    Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_inbox_max_id)));
                    num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_outbox_max_id)));
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int i;
                        MessagesController.this.putUsers(messages_dialogs.users, true);
                        MessagesController.this.putChats(messages_dialogs.chats, true);
                        for (i = 0; i < longSparseArray.size(); i++) {
                            long keyAt = longSparseArray.keyAt(i);
                            TL_dialog tL_dialog = (TL_dialog) longSparseArray.valueAt(i);
                            TL_dialog tL_dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(keyAt);
                            if (tL_dialog2 == null) {
                                MessagesController messagesController = MessagesController.this;
                                messagesController.nextDialogsCacheOffset++;
                                MessagesController.this.dialogs_dict.put(keyAt, tL_dialog);
                                MessageObject messageObject = (MessageObject) longSparseArray2.get(tL_dialog.id);
                                MessagesController.this.dialogMessage.put(keyAt, messageObject);
                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                    if (messageObject.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                    }
                                }
                            } else {
                                tL_dialog2.unread_count = tL_dialog.unread_count;
                                if (tL_dialog2.unread_mentions_count != tL_dialog.unread_mentions_count) {
                                    tL_dialog2.unread_mentions_count = tL_dialog.unread_mentions_count;
                                    if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(tL_dialog2.id))) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(tL_dialog2.id), Integer.valueOf(tL_dialog2.unread_mentions_count));
                                    }
                                }
                                MessageObject messageObject2 = (MessageObject) MessagesController.this.dialogMessage.get(keyAt);
                                if (messageObject2 != null) {
                                    if (tL_dialog2.top_message <= 0) {
                                        MessageObject messageObject3 = (MessageObject) longSparseArray2.get(tL_dialog.id);
                                        if (messageObject2.deleted || messageObject3 == null || messageObject3.messageOwner.date > messageObject2.messageOwner.date) {
                                            MessagesController.this.dialogs_dict.put(keyAt, tL_dialog);
                                            MessagesController.this.dialogMessage.put(keyAt, messageObject3);
                                            if (messageObject3 != null && messageObject3.messageOwner.to_id.channel_id == 0) {
                                                MessagesController.this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                                                if (messageObject3.messageOwner.random_id != 0) {
                                                    MessagesController.this.dialogMessagesByRandomIds.put(messageObject3.messageOwner.random_id, messageObject3);
                                                }
                                            }
                                            MessagesController.this.dialogMessagesByIds.remove(messageObject2.getId());
                                            if (messageObject2.messageOwner.random_id != 0) {
                                                MessagesController.this.dialogMessagesByRandomIds.remove(messageObject2.messageOwner.random_id);
                                            }
                                        }
                                    }
                                }
                                if ((messageObject2 != null && messageObject2.deleted) || tL_dialog.top_message > tL_dialog2.top_message) {
                                    MessagesController.this.dialogs_dict.put(keyAt, tL_dialog);
                                    MessageObject messageObject4 = (MessageObject) longSparseArray2.get(tL_dialog.id);
                                    MessagesController.this.dialogMessage.put(keyAt, messageObject4);
                                    if (messageObject4 != null && messageObject4.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(messageObject4.getId(), messageObject4);
                                        if (messageObject4.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(messageObject4.messageOwner.random_id, messageObject4);
                                        }
                                    }
                                    if (messageObject2 != null) {
                                        MessagesController.this.dialogMessagesByIds.remove(messageObject2.getId());
                                        if (messageObject2.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.remove(messageObject2.messageOwner.random_id);
                                        }
                                    }
                                    if (messageObject4 == null) {
                                        MessagesController.this.checkLastDialogMessage(tL_dialog, null, 0);
                                    }
                                }
                            }
                        }
                        MessagesController.this.dialogs.clear();
                        i = MessagesController.this.dialogs_dict.size();
                        for (int i2 = 0; i2 < i; i2++) {
                            MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(i2));
                        }
                        MessagesController.this.sortDialogs(null);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(longSparseArray3);
                    }
                });
            }
        });
    }

    public void addToViewsQueue(final Message message) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                int i;
                if (message.to_id.channel_id != 0) {
                    i = -message.to_id.channel_id;
                } else if (message.to_id.chat_id != 0) {
                    i = -message.to_id.chat_id;
                } else {
                    i = message.to_id.user_id;
                }
                ArrayList arrayList = (ArrayList) MessagesController.this.channelViewsToSend.get(i);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    MessagesController.this.channelViewsToSend.put(i, arrayList);
                }
                if (!arrayList.contains(Integer.valueOf(message.id))) {
                    arrayList.add(Integer.valueOf(message.id));
                }
            }
        });
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        ArrayList arrayList = new ArrayList();
        long id = (long) messageObject.getId();
        if (messageObject.messageOwner.to_id.channel_id != 0) {
            id |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
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
            r0 = new TL_channels_readMessageContents();
            r0.channel = getInputChannel(messageObject.messageOwner.to_id.channel_id);
            if (r0.channel != null) {
                r0.id.add(Integer.valueOf(messageObject.getId()));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(r0, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
            }
        } else {
            r0 = new TL_messages_readMessageContents();
            r0.id.add(Integer.valueOf(messageObject.getId()));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(r0, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                        MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                    }
                }
            });
        }
    }

    public void markMentionMessageAsRead(int i, int i2, long j) {
        MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(i, i2, j);
        if (i2 != 0) {
            j = new TL_channels_readMessageContents();
            j.channel = getInputChannel(i2);
            if (j.channel != 0) {
                j.id.add(Integer.valueOf(i));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(j, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
            } else {
                return;
            }
        }
        i2 = new TL_messages_readMessageContents();
        i2.id.add(Integer.valueOf(i));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(i2, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                    MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                }
            }
        });
    }

    public void markMessageAsRead(int i, int i2, int i3) {
        if (i != 0) {
            if (i3 > 0) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                MessagesStorage.getInstance(this.currentAccount).createTaskForMid(i, i2, currentTime, currentTime, i3, false);
                if (i2 != 0) {
                    i3 = new TL_channels_readMessageContents();
                    i3.channel = getInputChannel(i2);
                    i3.id.add(Integer.valueOf(i));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(i3, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                        }
                    });
                } else {
                    i2 = new TL_messages_readMessageContents();
                    i2.id.add(Integer.valueOf(i));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(i2, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
                                TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                                MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                            }
                        }
                    });
                }
            }
        }
    }

    public void markMessageAsRead(long j, long j2, int i) {
        if (!(j2 == 0 || j == 0)) {
            if (i > 0 || i == Integer.MIN_VALUE) {
                int i2 = (int) j;
                j = (int) (j >> 32);
                if (i2 == 0) {
                    j = getEncryptedChat(Integer.valueOf(j));
                    if (j != null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(Long.valueOf(j2));
                        SecretChatHelper.getInstance(this.currentAccount).sendMessagesReadMessage(j, arrayList, 0);
                        if (i > 0) {
                            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                            MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(j.id, currentTime, currentTime, 0, arrayList);
                        }
                    }
                }
            }
        }
    }

    private void completeReadTask(ReadTask readTask) {
        int i = (int) readTask.dialogId;
        int i2 = (int) (readTask.dialogId >> 32);
        if (i != 0) {
            TLObject tLObject;
            InputPeer inputPeer = getInputPeer(i);
            if (inputPeer instanceof TL_inputPeerChannel) {
                TLObject tL_channels_readHistory = new TL_channels_readHistory();
                tL_channels_readHistory.channel = getInputChannel(-i);
                tL_channels_readHistory.max_id = readTask.maxId;
                tLObject = tL_channels_readHistory;
            } else {
                tLObject = new TL_messages_readHistory();
                tLObject.peer = inputPeer;
                tLObject.max_id = readTask.maxId;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null && (tLObject instanceof TL_messages_affectedMessages) != null) {
                        TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                        MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                    }
                }
            });
            return;
        }
        EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i2));
        if (encryptedChat.auth_key != null && encryptedChat.auth_key.length > 1 && (encryptedChat instanceof TL_encryptedChat)) {
            tL_channels_readHistory = new TL_messages_readEncryptedHistory();
            tL_channels_readHistory.peer = new TL_inputEncryptedChat();
            tL_channels_readHistory.peer.chat_id = encryptedChat.id;
            tL_channels_readHistory.peer.access_hash = encryptedChat.access_hash;
            tL_channels_readHistory.max_date = readTask.maxDate;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_readHistory, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            });
        }
    }

    private void checkReadTasks() {
        long uptimeMillis = SystemClock.uptimeMillis();
        int size = this.readTasks.size();
        int i = 0;
        while (i < size) {
            ReadTask readTask = (ReadTask) this.readTasks.get(i);
            if (readTask.sendRequestTime <= uptimeMillis) {
                completeReadTask(readTask);
                this.readTasks.remove(i);
                this.readTasksMap.remove(readTask.dialogId);
                i--;
                size--;
            }
            i++;
        }
    }

    public void markDialogAsReadNow(final long j) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                ReadTask readTask = (ReadTask) MessagesController.this.readTasksMap.get(j);
                if (readTask != null) {
                    MessagesController.this.completeReadTask(readTask);
                    MessagesController.this.readTasks.remove(readTask);
                    MessagesController.this.readTasksMap.remove(j);
                }
            }
        });
    }

    public void markDialogAsRead(long j, int i, int i2, int i3, boolean z, int i4, boolean z2) {
        int i5;
        MessagesController messagesController = this;
        long j2 = j;
        final int i6 = i;
        int i7 = i2;
        int i8 = i3;
        int i9 = (int) j2;
        int i10 = (int) (j2 >> 32);
        int i11;
        final long j3;
        int i12;
        int i13;
        if (i9 != 0) {
            if (i6 != 0) {
                if (i10 != 1) {
                    long j4;
                    boolean z3;
                    Integer num;
                    int i14;
                    final boolean z4;
                    long j5 = (long) i6;
                    long j6 = (long) i7;
                    if (i9 < 0) {
                        i9 = -i9;
                        if (ChatObject.isChannel(getChat(Integer.valueOf(i9)))) {
                            long j7 = ((long) i9) << 32;
                            j6 |= j7;
                            j4 = j5 | j7;
                            z3 = true;
                            num = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(j));
                            if (num == null) {
                                num = Integer.valueOf(0);
                            }
                            messagesController.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(num.intValue(), i6)));
                            i5 = 1;
                            i14 = 0;
                            MessagesStorage.getInstance(messagesController.currentAccount).processPendingRead(j2, j4, j6, i8, z3);
                            i11 = i8;
                            j3 = j2;
                            i7 = i4;
                            i12 = i6;
                            z4 = z;
                            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.MessagesController$84$1 */
                                class C03571 implements Runnable {
                                    C03571() {
                                    }

                                    public void run() {
                                        TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(j3);
                                        if (tL_dialog != null) {
                                            if (i7 != 0) {
                                                if (i6 < tL_dialog.top_message) {
                                                    tL_dialog.unread_count = Math.max(tL_dialog.unread_count - i7, 0);
                                                    if (i6 != Integer.MIN_VALUE && tL_dialog.unread_count > tL_dialog.top_message - i6) {
                                                        tL_dialog.unread_count = tL_dialog.top_message - i6;
                                                    }
                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                                                }
                                            }
                                            tL_dialog.unread_count = 0;
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                                        }
                                        if (z4) {
                                            NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j3, 0, i6, true);
                                            LongSparseArray longSparseArray = new LongSparseArray(1);
                                            longSparseArray.put(j3, Integer.valueOf(-1));
                                            NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(longSparseArray);
                                            return;
                                        }
                                        NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j3, 0, i6, false);
                                        longSparseArray = new LongSparseArray(1);
                                        longSparseArray.put(j3, Integer.valueOf(0));
                                        NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(longSparseArray);
                                    }
                                }

                                public void run() {
                                    AndroidUtilities.runOnUIThread(new C03571());
                                }
                            });
                            if (i12 != Integer.MAX_VALUE) {
                                i5 = i14;
                            }
                            i13 = i11;
                        }
                    }
                    j4 = j5;
                    z3 = false;
                    num = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(j));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    messagesController.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(num.intValue(), i6)));
                    i5 = 1;
                    i14 = 0;
                    MessagesStorage.getInstance(messagesController.currentAccount).processPendingRead(j2, j4, j6, i8, z3);
                    i11 = i8;
                    j3 = j2;
                    i7 = i4;
                    i12 = i6;
                    z4 = z;
                    MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                    if (i12 != Integer.MAX_VALUE) {
                        i5 = i14;
                    }
                    i13 = i11;
                }
            }
            return;
        }
        i12 = i6;
        i5 = 1;
        i11 = i8;
        if (i11 != 0) {
            EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i10));
            int i15 = i11;
            MessagesStorage.getInstance(messagesController.currentAccount).processPendingRead(j, (long) i12, (long) i7, i15, false);
            j3 = j;
            i7 = i15;
            final boolean z5 = z;
            i13 = i15;
            i15 = i4;
            EncryptedChat encryptedChat2 = encryptedChat;
            final int i16 = i2;
            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$85$1 */
                class C03581 implements Runnable {
                    C03581() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j3, i7, 0, z5);
                        TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(j3);
                        if (tL_dialog != null) {
                            if (i15 != 0) {
                                if (i16 > tL_dialog.top_message) {
                                    tL_dialog.unread_count = Math.max(tL_dialog.unread_count - i15, 0);
                                    if (i16 != ConnectionsManager.DEFAULT_DATACENTER_ID && tL_dialog.unread_count > i16 - tL_dialog.top_message) {
                                        tL_dialog.unread_count = i16 - tL_dialog.top_message;
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                                }
                            }
                            tL_dialog.unread_count = 0;
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                        }
                        LongSparseArray longSparseArray = new LongSparseArray(1);
                        longSparseArray.put(j3, Integer.valueOf(0));
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(longSparseArray);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03581());
                }
            });
            if (encryptedChat2.ttl > 0) {
                i7 = Math.max(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime(), i13);
                MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(encryptedChat2.id, i7, i7, 0, null);
            }
        } else {
            return;
        }
        if (i5 != 0) {
            j3 = j;
            final boolean z6 = z2;
            i6 = i13;
            i15 = i;
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    ReadTask readTask = (ReadTask) MessagesController.this.readTasksMap.get(j3);
                    if (readTask == null) {
                        readTask = new ReadTask();
                        readTask.dialogId = j3;
                        readTask.sendRequestTime = SystemClock.uptimeMillis() + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                        if (!z6) {
                            MessagesController.this.readTasksMap.put(j3, readTask);
                            MessagesController.this.readTasks.add(readTask);
                        }
                    }
                    readTask.maxDate = i6;
                    readTask.maxId = i15;
                    if (z6) {
                        MessagesController.this.completeReadTask(readTask);
                    }
                }
            });
        }
    }

    public int createChat(String str, ArrayList<Integer> arrayList, String str2, int i, final BaseFragment baseFragment) {
        int i2 = 0;
        if (i == 1) {
            str2 = new TL_chat();
            str2.id = UserConfig.getInstance(this.currentAccount).lastBroadcastId;
            str2.title = str;
            str2.photo = new TL_chatPhotoEmpty();
            str2.participants_count = arrayList.size();
            str2.date = (int) (System.currentTimeMillis() / 1000);
            str2.version = 1;
            str = UserConfig.getInstance(this.currentAccount);
            str.lastBroadcastId--;
            putChat(str2, false);
            str = new ArrayList();
            str.add(str2);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, str, true, true);
            str = new TL_chatFull();
            str.id = str2.id;
            str.chat_photo = new TL_photoEmpty();
            str.notify_settings = new TL_peerNotifySettingsEmpty();
            str.exported_invite = new TL_chatInviteEmpty();
            str.participants = new TL_chatParticipants();
            str.participants.chat_id = str2.id;
            str.participants.admin_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            str.participants.version = 1;
            for (i = 0; i < arrayList.size(); i++) {
                baseFragment = new TL_chatParticipant();
                baseFragment.user_id = ((Integer) arrayList.get(i)).intValue();
                baseFragment.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                baseFragment.date = (int) (System.currentTimeMillis() / 1000);
                str.participants.participants.add(baseFragment);
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(str, false);
            Message tL_messageService = new TL_messageService();
            tL_messageService.action = new TL_messageActionCreatedBroadcastList();
            arrayList = UserConfig.getInstance(this.currentAccount).getNewMessageId();
            tL_messageService.id = arrayList;
            tL_messageService.local_id = arrayList;
            tL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tL_messageService.dialog_id = AndroidUtilities.makeBroadcastId(str2.id);
            tL_messageService.to_id = new TL_peerChat();
            tL_messageService.to_id.chat_id = str2.id;
            tL_messageService.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            tL_messageService.random_id = 0;
            tL_messageService.flags |= 256;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            arrayList = new MessageObject(this.currentAccount, tL_messageService, this.users, true);
            arrayList.messageOwner.send_state = 0;
            i = new ArrayList();
            i.add(arrayList);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(tL_messageService);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList2, false, true, false, 0);
            updateInterfaceWithMessages(tL_messageService.dialog_id, i);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, new Object[]{Integer.valueOf(str2.id)});
            return 0;
        } else if (i == 0) {
            str2 = new TL_messages_createChat();
            str2.title = str;
            while (i2 < arrayList.size()) {
                User user = getUser((Integer) arrayList.get(i2));
                if (user != null) {
                    str2.users.add(getInputUser(user));
                }
                i2++;
            }
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(str2, new RequestDelegate() {
                public void run(TLObject tLObject, final TL_error tL_error) {
                    if (tL_error != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, tL_error, baseFragment, str2, new Object[0]);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                            }
                        });
                        return;
                    }
                    final Updates updates = (Updates) tLObject;
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
            final TLObject tL_channels_createChannel = new TL_channels_createChannel();
            tL_channels_createChannel.title = str;
            tL_channels_createChannel.about = str2;
            if (i == 4) {
                tL_channels_createChannel.megagroup = true;
            } else {
                tL_channels_createChannel.broadcast = true;
            }
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_createChannel, new RequestDelegate() {
                public void run(TLObject tLObject, final TL_error tL_error) {
                    if (tL_error != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, tL_error, baseFragment, tL_channels_createChannel, new Object[0]);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                            }
                        });
                        return;
                    }
                    final Updates updates = (Updates) tLObject;
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

    public void convertToMegaGroup(final android.content.Context r4, int r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
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
        r3 = this;
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_migrateChat;
        r0.<init>();
        r0.chat_id = r5;
        r5 = new org.telegram.ui.ActionBar.AlertDialog;
        r1 = 1;
        r5.<init>(r4, r1);
        r1 = "Loading";
        r2 = NUM; // 0x7f0c0382 float:1.8611013E38 double:1.053097842E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r5.setMessage(r1);
        r1 = 0;
        r5.setCanceledOnTouchOutside(r1);
        r5.setCancelable(r1);
        r1 = r3.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r2 = new org.telegram.messenger.MessagesController$89;
        r2.<init>(r4, r5);
        r4 = r1.sendRequest(r0, r2);
        r0 = "Cancel";
        r1 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r1 = new org.telegram.messenger.MessagesController$90;
        r1.<init>(r4);
        r4 = -2;
        r5.setButton(r4, r0, r1);
        r5.show();	 Catch:{ Exception -> 0x0044 }
    L_0x0044:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.convertToMegaGroup(android.content.Context, int):void");
    }

    public void addUsersToChannel(int i, ArrayList<InputUser> arrayList, final BaseFragment baseFragment) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                final TLObject tL_channels_inviteToChannel = new TL_channels_inviteToChannel();
                tL_channels_inviteToChannel.channel = getInputChannel(i);
                tL_channels_inviteToChannel.users = arrayList;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_inviteToChannel, new RequestDelegate() {
                    public void run(TLObject tLObject, final TL_error tL_error) {
                        if (tL_error != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    AlertsCreator.processError(MessagesController.this.currentAccount, tL_error, baseFragment, tL_channels_inviteToChannel, Boolean.valueOf(true));
                                }
                            });
                        } else {
                            MessagesController.this.processUpdates((Updates) tLObject, false);
                        }
                    }
                });
            }
        }
    }

    public void toogleChannelInvites(int i, boolean z) {
        TLObject tL_channels_toggleInvites = new TL_channels_toggleInvites();
        tL_channels_toggleInvites.channel = getInputChannel(i);
        tL_channels_toggleInvites.enabled = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_toggleInvites, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tLObject != null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                }
            }
        }, 64);
    }

    public void toogleChannelSignatures(int i, boolean z) {
        TLObject tL_channels_toggleSignatures = new TL_channels_toggleSignatures();
        tL_channels_toggleSignatures.channel = getInputChannel(i);
        tL_channels_toggleSignatures.enabled = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_toggleSignatures, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$93$1 */
            class C03671 implements Runnable {
                C03671() {
                }

                public void run() {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tLObject != null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                    AndroidUtilities.runOnUIThread(new C03671());
                }
            }
        }, 64);
    }

    public void toogleChannelInvitesHistory(int i, boolean z) {
        TLObject tL_channels_togglePreHistoryHidden = new TL_channels_togglePreHistoryHidden();
        tL_channels_togglePreHistoryHidden.channel = getInputChannel(i);
        tL_channels_togglePreHistoryHidden.enabled = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_togglePreHistoryHidden, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$94$1 */
            class C03681 implements Runnable {
                C03681() {
                }

                public void run() {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tLObject != null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                    AndroidUtilities.runOnUIThread(new C03681());
                }
            }
        }, 64);
    }

    public void updateChannelAbout(int i, final String str, final ChatFull chatFull) {
        if (chatFull != null) {
            TLObject tL_channels_editAbout = new TL_channels_editAbout();
            tL_channels_editAbout.channel = getInputChannel(i);
            tL_channels_editAbout.about = str;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editAbout, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController$95$1 */
                class C03691 implements Runnable {
                    C03691() {
                    }

                    public void run() {
                        chatFull.about = str;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(chatFull, false);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if ((tLObject instanceof TL_boolTrue) != null) {
                        AndroidUtilities.runOnUIThread(new C03691());
                    }
                }
            }, 64);
        }
    }

    public void updateChannelUserName(final int i, final String str) {
        TLObject tL_channels_updateUsername = new TL_channels_updateUsername();
        tL_channels_updateUsername.channel = getInputChannel(i);
        tL_channels_updateUsername.username = str;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_updateUsername, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$96$1 */
            class C03701 implements Runnable {
                C03701() {
                }

                public void run() {
                    Chat chat = MessagesController.this.getChat(Integer.valueOf(i));
                    if (str.length() != 0) {
                        chat.flags |= 64;
                    } else {
                        chat.flags &= -65;
                    }
                    chat.username = str;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(chat);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(null, arrayList, true, true);
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if ((tLObject instanceof TL_boolTrue) != null) {
                    AndroidUtilities.runOnUIThread(new C03701());
                }
            }
        }, 64);
    }

    public void sendBotStart(User user, String str) {
        if (user != null) {
            TLObject tL_messages_startBot = new TL_messages_startBot();
            tL_messages_startBot.bot = getInputUser(user);
            tL_messages_startBot.peer = getInputPeer(user.id);
            tL_messages_startBot.start_param = str;
            tL_messages_startBot.random_id = Utilities.random.nextLong();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_startBot, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.this.processUpdates((Updates) tLObject, false);
                    }
                }
            });
        }
    }

    public void toggleAdminMode(final int i, boolean z) {
        TLObject tL_messages_toggleChatAdmins = new TL_messages_toggleChatAdmins();
        tL_messages_toggleChatAdmins.chat_id = i;
        tL_messages_toggleChatAdmins.enabled = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleChatAdmins, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                    MessagesController.this.loadFullChat(i, 0, true);
                }
            }
        });
    }

    public void toggleUserAdmin(int i, int i2, boolean z) {
        TLObject tL_messages_editChatAdmin = new TL_messages_editChatAdmin();
        tL_messages_editChatAdmin.chat_id = i;
        tL_messages_editChatAdmin.user_id = getInputUser(i2);
        tL_messages_editChatAdmin.is_admin = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editChatAdmin, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void addUserToChat(int i, User user, ChatFull chatFull, int i2, String str, BaseFragment baseFragment) {
        MessagesController messagesController = this;
        final int i3 = i;
        User user2 = user;
        ChatFull chatFull2 = chatFull;
        String str2 = str;
        if (user2 != null) {
            if (i3 > 0) {
                TLObject tL_messages_startBot;
                TLObject tLObject;
                final boolean z;
                final BaseFragment baseFragment2;
                final TLObject tLObject2;
                boolean isChannel = ChatObject.isChannel(i3, messagesController.currentAccount);
                final boolean z2 = isChannel && getChat(Integer.valueOf(i3)).megagroup;
                final InputUser inputUser = getInputUser(user2);
                if (str2 != null) {
                    if (!isChannel || z2) {
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
                        tLObject = tL_messages_startBot;
                        z = isChannel;
                        baseFragment2 = baseFragment;
                        tLObject2 = tLObject;
                        ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tLObject, new RequestDelegate() {

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

                            public void run(TLObject tLObject, final TL_error tL_error) {
                                if (z && (inputUser instanceof TL_inputUserSelf)) {
                                    AndroidUtilities.runOnUIThread(new C02791());
                                }
                                if (tL_error != null) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            int access$000 = MessagesController.this.currentAccount;
                                            TL_error tL_error = tL_error;
                                            BaseFragment baseFragment = baseFragment2;
                                            TLObject tLObject = tLObject2;
                                            boolean z = true;
                                            Object[] objArr = new Object[1];
                                            if (!z || z2) {
                                                z = false;
                                            }
                                            objArr[0] = Boolean.valueOf(z);
                                            AlertsCreator.processError(access$000, tL_error, baseFragment, tLObject, objArr);
                                        }
                                    });
                                    return;
                                }
                                boolean z;
                                Updates updates = (Updates) tLObject;
                                for (int i = 0; i < updates.updates.size(); i++) {
                                    Update update = (Update) updates.updates.get(i);
                                    if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                                        z = true;
                                        break;
                                    }
                                }
                                z = null;
                                MessagesController.this.processUpdates(updates, false);
                                if (z != null) {
                                    if (!(z || (inputUser instanceof TL_inputUserSelf) == null)) {
                                        MessagesController.this.generateJoinMessage(i3, true);
                                    }
                                    AndroidUtilities.runOnUIThread(new C02813(), 1000);
                                }
                                if (!(z == null || (inputUser instanceof TL_inputUserSelf) == null)) {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, i3);
                                }
                            }
                        });
                    }
                }
                if (!isChannel) {
                    tL_messages_startBot = new TL_messages_addChatUser();
                    tL_messages_startBot.chat_id = i3;
                    tL_messages_startBot.fwd_limit = i2;
                    tL_messages_startBot.user_id = inputUser;
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    tL_messages_startBot = new TL_channels_inviteToChannel();
                    tL_messages_startBot.channel = getInputChannel(i3);
                    tL_messages_startBot.users.add(inputUser);
                } else if (!messagesController.joiningToChannels.contains(Integer.valueOf(i3))) {
                    tL_messages_startBot = new TL_channels_joinChannel();
                    tL_messages_startBot.channel = getInputChannel(i3);
                    messagesController.joiningToChannels.add(Integer.valueOf(i3));
                } else {
                    return;
                }
                tLObject = tL_messages_startBot;
                z = isChannel;
                baseFragment2 = baseFragment;
                tLObject2 = tLObject;
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tLObject, /* anonymous class already generated */);
            } else if (chatFull2 instanceof TL_chatFull) {
                int i4 = 0;
                while (i4 < chatFull2.participants.participants.size()) {
                    if (((ChatParticipant) chatFull2.participants.participants.get(i4)).user_id != user2.id) {
                        i4++;
                    } else {
                        return;
                    }
                }
                Chat chat = getChat(Integer.valueOf(i3));
                chat.participants_count++;
                ArrayList arrayList = new ArrayList();
                arrayList.add(chat);
                MessagesStorage.getInstance(messagesController.currentAccount).putUsersAndChats(null, arrayList, true, true);
                TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                tL_chatParticipant.user_id = user2.id;
                tL_chatParticipant.inviter_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                tL_chatParticipant.date = ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime();
                chatFull2.participants.participants.add(0, tL_chatParticipant);
                MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(chatFull2, true);
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull2, Integer.valueOf(0), Boolean.valueOf(false), null);
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public void deleteUserFromChat(int i, User user, ChatFull chatFull) {
        deleteUserFromChat(i, user, chatFull, false);
    }

    public void deleteUserFromChat(int i, User user, ChatFull chatFull, boolean z) {
        if (user != null) {
            if (i > 0) {
                final InputUser inputUser = getInputUser(user);
                Chat chat = getChat(Integer.valueOf(i));
                final boolean isChannel = ChatObject.isChannel(chat);
                if (!isChannel) {
                    z = new TL_messages_deleteChatUser();
                    z.chat_id = i;
                    z.user_id = getInputUser(user);
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    z = new TL_channels_editBanned();
                    z.channel = getInputChannel(chat);
                    z.user_id = inputUser;
                    z.banned_rights = new TL_channelBannedRights();
                    z.banned_rights.view_messages = true;
                    z.banned_rights.send_media = true;
                    z.banned_rights.send_messages = true;
                    z.banned_rights.send_stickers = true;
                    z.banned_rights.send_gifs = true;
                    z.banned_rights.send_games = true;
                    z.banned_rights.send_inline = true;
                    z.banned_rights.embed_links = true;
                } else if (chat.creator && z) {
                    z = new TL_channels_deleteChannel();
                    z.channel = getInputChannel(chat);
                } else {
                    z = new TL_channels_leaveChannel();
                    z.channel = getInputChannel(chat);
                }
                final User user2 = user;
                final int i2 = i;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {

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

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (user2.id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                            AndroidUtilities.runOnUIThread(new C02821());
                        }
                        if (tL_error == null) {
                            MessagesController.this.processUpdates((Updates) tLObject, false);
                            if (isChannel != null && (inputUser instanceof TL_inputUserSelf) == null) {
                                AndroidUtilities.runOnUIThread(new C02832(), 1000);
                            }
                        }
                    }
                }, 64);
            } else if (chatFull instanceof TL_chatFull) {
                i = getChat(Integer.valueOf(i));
                i.participants_count -= true;
                z = new ArrayList();
                z.add(i);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, z, true, true);
                for (z = false; z < chatFull.participants.participants.size(); z++) {
                    if (((ChatParticipant) chatFull.participants.participants.get(z)).user_id == user.id) {
                        chatFull.participants.participants.remove(z);
                        user = 1;
                        break;
                    }
                }
                user = null;
                if (user != null) {
                    MessagesStorage.getInstance(this.currentAccount).updateChatInfo(chatFull, true);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[]{Integer.valueOf(32)});
            }
        }
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editTitle, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.this.processUpdates((Updates) tLObject, false);
                    }
                }
            }, 64);
            return;
        }
        i = getChat(Integer.valueOf(i));
        i.title = str;
        str = new ArrayList();
        str.add(i);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, str, true, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(16));
    }

    public void changeChatAvatar(int i, InputFile inputFile) {
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editPhoto, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                }
            }
        }, 64);
    }

    public void unregistedPush() {
        if (UserConfig.getInstance(this.currentAccount).registeredForPush && SharedConfig.pushString.length() == 0) {
            TLObject tL_account_unregisterDevice = new TL_account_unregisterDevice();
            tL_account_unregisterDevice.token = SharedConfig.pushString;
            tL_account_unregisterDevice.token_type = 2;
            for (int i = 0; i < 3; i++) {
                UserConfig instance = UserConfig.getInstance(i);
                if (i != this.currentAccount && instance.isClientActivated()) {
                    tL_account_unregisterDevice.other_uids.add(Integer.valueOf(instance.getClientUserId()));
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_unregisterDevice, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            });
        }
    }

    public void performLogout(boolean z) {
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).putLong("lastStickersLoadTimeMask", 0).putLong("lastStickersLoadTimeFavs", 0).commit();
        this.mainPreferences.edit().remove("gifhint").commit();
        if (z) {
            unregistedPush();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_logOut(), new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
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
                TLObject tL_help_getAppChangelog = new TL_help_getAppChangelog();
                tL_help_getAppChangelog.prev_app_version = SharedConfig.lastUpdateVersion;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_getAppChangelog, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
                            SharedConfig.saveConfig();
                        }
                        if ((tLObject instanceof Updates) != null) {
                            MessagesController.this.processUpdates((Updates) tLObject, false);
                        }
                    }
                });
            }
        }
    }

    public void registerForPush(final String str) {
        if (!(TextUtils.isEmpty(str) || this.registeringForPush)) {
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != 0) {
                if (!UserConfig.getInstance(this.currentAccount).registeredForPush || !str.equals(SharedConfig.pushString)) {
                    this.registeringForPush = true;
                    this.lastPushRegisterSendTime = SystemClock.uptimeMillis();
                    if (SharedConfig.pushAuthKey == null) {
                        SharedConfig.pushAuthKey = new byte[256];
                        Utilities.random.nextBytes(SharedConfig.pushAuthKey);
                        SharedConfig.saveConfig();
                    }
                    TLObject tL_account_registerDevice = new TL_account_registerDevice();
                    tL_account_registerDevice.token_type = 2;
                    tL_account_registerDevice.token = str;
                    tL_account_registerDevice.secret = SharedConfig.pushAuthKey;
                    for (int i = 0; i < 3; i++) {
                        UserConfig instance = UserConfig.getInstance(i);
                        if (i != this.currentAccount && instance.isClientActivated()) {
                            tL_account_registerDevice.other_uids.add(Integer.valueOf(instance.getClientUserId()));
                        }
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_registerDevice, new RequestDelegate() {

                        /* renamed from: org.telegram.messenger.MessagesController$107$1 */
                        class C02841 implements Runnable {
                            C02841() {
                            }

                            public void run() {
                                MessagesController.this.registeringForPush = false;
                            }
                        }

                        public void run(TLObject tLObject, TL_error tL_error) {
                            if ((tLObject instanceof TL_boolTrue) != null) {
                                if (BuildVars.LOGS_ENABLED != null) {
                                    tLObject = new StringBuilder();
                                    tLObject.append("account ");
                                    tLObject.append(MessagesController.this.currentAccount);
                                    tLObject.append(" registered for push");
                                    FileLog.m0d(tLObject.toString());
                                }
                                UserConfig.getInstance(MessagesController.this.currentAccount).registeredForPush = true;
                                SharedConfig.pushString = str;
                                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(null);
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
                public void run(TLObject tLObject, TL_error tL_error) {
                    int i = 0;
                    MessagesController.this.updatingState = false;
                    if (tL_error == null) {
                        TL_updates_state tL_updates_state = (TL_updates_state) tLObject;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(tL_updates_state.date);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(tL_updates_state.pts);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(tL_updates_state.seq);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(tL_updates_state.qts);
                        while (i < 3) {
                            MessagesController.this.processUpdatesQueue(i, 2);
                            i++;
                        }
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).saveDiffParams(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastQtsValue());
                    } else if (tL_error.code != 401) {
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
            updates = getUpdateSeq(updates);
            if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 != updates) {
                if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() != updates) {
                    return MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() < updates ? 1 : 2;
                }
            }
            return 0;
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
            if (!arrayList.isEmpty()) {
                if (i3 != 0) {
                    Collections.sort(arrayList, new Comparator<Updates>() {
                        public int compare(Updates updates, Updates updates2) {
                            return AndroidUtilities.compare(updates.pts, updates2.pts);
                        }
                    });
                    if (i2 == 2) {
                        this.channelsPts.put(i, ((Updates) arrayList.get(0)).pts);
                    }
                    i2 = 0;
                    while (arrayList.size() > 0) {
                        Updates updates = (Updates) arrayList.get(0);
                        int i4 = updates.pts <= i3 ? 2 : updates.pts_count + i3 == updates.pts ? 0 : true;
                        if (i4 == 0) {
                            processUpdates(updates, true);
                            arrayList.remove(0);
                            i2 = 1;
                        } else if (i4 == 1) {
                            long j = this.updatesStartWaitTimeChannels.get(i);
                            if (j == 0 || (i2 == 0 && Math.abs(System.currentTimeMillis() - j) > 1500)) {
                                if (BuildVars.LOGS_ENABLED != 0) {
                                    i2 = new StringBuilder();
                                    i2.append("HOLE IN CHANNEL ");
                                    i2.append(i);
                                    i2.append(" UPDATES QUEUE - getChannelDifference ");
                                    FileLog.m0d(i2.toString());
                                }
                                this.updatesStartWaitTimeChannels.delete(i);
                                this.updatesQueueChannels.remove(i);
                                getChannelDifference(i);
                                return;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("HOLE IN CHANNEL ");
                                stringBuilder.append(i);
                                stringBuilder.append(" UPDATES QUEUE - will wait more time");
                                FileLog.m0d(stringBuilder.toString());
                            }
                            if (i2 != 0) {
                                this.updatesStartWaitTimeChannels.put(i, System.currentTimeMillis());
                            }
                            return;
                        } else {
                            arrayList.remove(0);
                        }
                    }
                    this.updatesQueueChannels.remove(i);
                    this.updatesStartWaitTimeChannels.delete(i);
                    if (BuildVars.LOGS_ENABLED != 0) {
                        i2 = new StringBuilder();
                        i2.append("UPDATES CHANNEL ");
                        i2.append(i);
                        i2.append(" QUEUE PROCEED - OK");
                        FileLog.m0d(i2.toString());
                    }
                    return;
                }
            }
            this.updatesQueueChannels.remove(i);
        }
    }

    private void processUpdatesQueue(int i, int i2) {
        ArrayList arrayList;
        if (i == 0) {
            arrayList = this.updatesQueueSeq;
            Collections.sort(arrayList, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(updates), MessagesController.this.getUpdateSeq(updates2));
                }
            });
        } else if (i == 1) {
            arrayList = this.updatesQueuePts;
            Collections.sort(arrayList, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
        } else if (i == 2) {
            arrayList = this.updatesQueueQts;
            Collections.sort(arrayList, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
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
            i2 = 0;
            while (arrayList.size() > 0) {
                Updates updates2 = (Updates) arrayList.get(0);
                int isValidUpdate = isValidUpdate(updates2, i);
                if (isValidUpdate == 0) {
                    processUpdates(updates2, true);
                    arrayList.remove(0);
                    i2 = 1;
                } else if (isValidUpdate != 1) {
                    arrayList.remove(0);
                } else if (getUpdatesStartTime(i) == 0 || (i2 == 0 && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(i)) > 1500)) {
                    if (BuildVars.LOGS_ENABLED != 0) {
                        FileLog.m0d("HOLE IN UPDATES QUEUE - getDifference");
                    }
                    setUpdatesStartTime(i, 0);
                    arrayList.clear();
                    getDifference();
                    return;
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("HOLE IN UPDATES QUEUE - will wait more time");
                    }
                    if (i2 != 0) {
                        setUpdatesStartTime(i, System.currentTimeMillis());
                    }
                    return;
                }
            }
            arrayList.clear();
            if (BuildVars.LOGS_ENABLED != 0) {
                FileLog.m0d("UPDATES QUEUE PROCEED - OK");
            }
        }
        setUpdatesStartTime(i, 0);
    }

    protected void loadUnknownChannel(final Chat chat, long j) {
        Throwable th;
        if (chat instanceof TL_channel) {
            if (this.gettingUnknownChannels.indexOfKey(chat.id) < 0) {
                if (chat.access_hash == 0) {
                    if (j != 0) {
                        MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
                    }
                    return;
                }
                InputPeer tL_inputPeerChannel = new TL_inputPeerChannel();
                tL_inputPeerChannel.channel_id = chat.id;
                tL_inputPeerChannel.access_hash = chat.access_hash;
                this.gettingUnknownChannels.put(chat.id, true);
                TLObject tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
                TL_inputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                tL_inputDialogPeer.peer = tL_inputPeerChannel;
                tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                if (j == 0) {
                    j = null;
                    try {
                        AbstractSerializedData nativeByteBuffer = new NativeByteBuffer(4 + chat.getObjectSize());
                        try {
                            nativeByteBuffer.writeInt32(0);
                            chat.serializeToStream(nativeByteBuffer);
                            j = nativeByteBuffer;
                        } catch (long j2) {
                            AbstractSerializedData abstractSerializedData = nativeByteBuffer;
                            th = j2;
                            j2 = abstractSerializedData;
                            FileLog.m3e(th);
                            j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(j2);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    if (tLObject != null) {
                                        TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
                                        if (tL_messages_peerDialogs.dialogs.isEmpty() == null && tL_messages_peerDialogs.chats.isEmpty() == null) {
                                            messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                                            tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                                            tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                                            tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                                            tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                                            MessagesController.this.processLoadedDialogs(tL_messages_dialogs, null, 0, 1, 2, false, false, false);
                                        }
                                    }
                                    if (j2 != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j2);
                                    }
                                    MessagesController.this.gettingUnknownChannels.delete(chat.id);
                                }
                            });
                        }
                    } catch (Exception e) {
                        th = e;
                        FileLog.m3e(th);
                        j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(j2);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, /* anonymous class already generated */);
                    }
                    j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(j2);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getPeerDialogs, /* anonymous class already generated */);
            }
        }
    }

    public void startShortPoll(final int i, final boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (z) {
                    MessagesController.this.needShortPollChannels.delete(i);
                    return;
                }
                MessagesController.this.needShortPollChannels.put(i, 0);
                if (MessagesController.this.shortPollChannels.indexOfKey(i) < 0) {
                    MessagesController.this.getChannelDifference(i, 3, 0, null);
                }
            }
        });
    }

    private void getChannelDifference(int i) {
        getChannelDifference(i, 0, 0, null);
    }

    public static boolean isSupportId(int i) {
        if (!(i / 1000 == 777 || i == 333000 || i == 4240000 || i == 4240000 || i == 4244000 || i == 4245000 || i == 4246000 || i == 410000 || i == 420000 || i == 431000 || i == 431415000 || i == 434000 || i == 4243000 || i == 439000 || i == 449000 || i == 450000 || i == 452000 || i == 454000 || i == 4254000 || i == 455000 || i == 460000 || i == 470000 || i == 479000 || i == 796000 || i == 482000 || i == 490000 || i == 496000 || i == 497000 || i == 498000)) {
            if (i != 4298000) {
                return false;
            }
        }
        return true;
    }

    protected void getChannelDifference(int i, int i2, long j, InputChannel inputChannel) {
        int i3;
        Throwable e;
        long j2;
        TLObject tL_updates_getChannelDifference;
        StringBuilder stringBuilder;
        final long j3;
        final int i4 = i;
        final int i5 = i2;
        long j4 = j;
        if (!this.gettingDifferenceChannels.get(i4)) {
            int i6;
            InputChannel inputChannel2;
            boolean z = true;
            if (i5 != 1) {
                i6 = r7.channelsPts.get(i4);
                if (i6 == 0) {
                    i6 = MessagesStorage.getInstance(r7.currentAccount).getChannelPtsSync(i4);
                    if (i6 != 0) {
                        r7.channelsPts.put(i4, i6);
                    }
                    if (i6 == 0 && (i5 == 2 || i5 == 3)) {
                        return;
                    }
                }
                if (i6 != 0) {
                    i3 = i6;
                    i6 = 100;
                } else {
                    return;
                }
            } else if (r7.channelsPts.get(i4) == 0) {
                i6 = 1;
                i3 = i6;
            } else {
                return;
            }
            if (inputChannel == null) {
                Chat chat = getChat(Integer.valueOf(i));
                if (chat == null) {
                    chat = MessagesStorage.getInstance(r7.currentAccount).getChatSync(i4);
                    if (chat != null) {
                        putChat(chat, true);
                    }
                }
                inputChannel2 = getInputChannel(chat);
            } else {
                inputChannel2 = inputChannel;
            }
            if (inputChannel2 != null) {
                if (inputChannel2.access_hash != 0) {
                    if (j4 == 0) {
                        NativeByteBuffer nativeByteBuffer;
                        try {
                            nativeByteBuffer = new NativeByteBuffer(12 + inputChannel2.getObjectSize());
                            try {
                                nativeByteBuffer.writeInt32(6);
                                nativeByteBuffer.writeInt32(i4);
                                nativeByteBuffer.writeInt32(i5);
                                inputChannel2.serializeToStream(nativeByteBuffer);
                            } catch (Exception e2) {
                                e = e2;
                                FileLog.m3e(e);
                                j4 = MessagesStorage.getInstance(r7.currentAccount).createPendingTask(nativeByteBuffer);
                                j2 = j4;
                                r7.gettingDifferenceChannels.put(i4, true);
                                tL_updates_getChannelDifference = new TL_updates_getChannelDifference();
                                tL_updates_getChannelDifference.channel = inputChannel2;
                                tL_updates_getChannelDifference.filter = new TL_channelMessagesFilterEmpty();
                                tL_updates_getChannelDifference.pts = i3;
                                tL_updates_getChannelDifference.limit = i6;
                                if (i5 != 3) {
                                    z = false;
                                }
                                tL_updates_getChannelDifference.force = z;
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("start getChannelDifference with pts = ");
                                    stringBuilder.append(i3);
                                    stringBuilder.append(" channelId = ");
                                    stringBuilder.append(i4);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                                j3 = j2;
                                ConnectionsManager.getInstance(r7.currentAccount).sendRequest(tL_updates_getChannelDifference, new RequestDelegate() {
                                    public void run(TLObject tLObject, final TL_error tL_error) {
                                        if (tL_error == null) {
                                            Chat chat;
                                            final updates_ChannelDifference updates_channeldifference = (updates_ChannelDifference) tLObject;
                                            final SparseArray sparseArray = new SparseArray();
                                            tLObject = null;
                                            for (tL_error = null; tL_error < updates_channeldifference.users.size(); tL_error++) {
                                                User user = (User) updates_channeldifference.users.get(tL_error);
                                                sparseArray.put(user.id, user);
                                            }
                                            for (int i = 0; i < updates_channeldifference.chats.size(); i++) {
                                                Chat chat2 = (Chat) updates_channeldifference.chats.get(i);
                                                if (chat2.id == i4) {
                                                    chat = chat2;
                                                    break;
                                                }
                                            }
                                            chat = null;
                                            final ArrayList arrayList = new ArrayList();
                                            if (updates_channeldifference.other_updates.isEmpty() == null) {
                                                while (tLObject < updates_channeldifference.other_updates.size()) {
                                                    Update update = (Update) updates_channeldifference.other_updates.get(tLObject);
                                                    if (update instanceof TL_updateMessageID) {
                                                        arrayList.add((TL_updateMessageID) update);
                                                        updates_channeldifference.other_updates.remove(tLObject);
                                                        tLObject--;
                                                    }
                                                    tLObject += 1;
                                                }
                                            }
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(updates_channeldifference.users, updates_channeldifference.chats, true, true);
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    MessagesController.this.putUsers(updates_channeldifference.users, false);
                                                    MessagesController.this.putChats(updates_channeldifference.chats, false);
                                                }
                                            });
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
                                                                    long j = (long) (-i4);
                                                                    Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                                                                    if (num == null) {
                                                                        num = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j));
                                                                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), num);
                                                                    }
                                                                    Integer num2 = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                                                                    if (num2 == null) {
                                                                        num2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j));
                                                                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), num2);
                                                                    }
                                                                    for (int i2 = 0; i2 < updates_channeldifference.messages.size(); i2++) {
                                                                        boolean z;
                                                                        Message message = (Message) updates_channeldifference.messages.get(i2);
                                                                        message.dialog_id = (long) (-i4);
                                                                        if (!(message.action instanceof TL_messageActionChannelCreate) && (chat == null || !chat.left)) {
                                                                            if ((message.out ? num2 : num).intValue() < message.id) {
                                                                                z = true;
                                                                                message.unread = z;
                                                                                if (chat != null && chat.megagroup) {
                                                                                    message.flags |= Integer.MIN_VALUE;
                                                                                }
                                                                            }
                                                                        }
                                                                        z = false;
                                                                        message.unread = z;
                                                                        message.flags |= Integer.MIN_VALUE;
                                                                    }
                                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).overwriteChannel(i4, (TL_updates_channelDifferenceTooLong) updates_channeldifference, i5);
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
                                                                if (j3 != 0) {
                                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j3);
                                                                }
                                                            }
                                                        }
                                                        if (!updates_channeldifference.new_messages.isEmpty()) {
                                                            final LongSparseArray longSparseArray = new LongSparseArray();
                                                            ImageLoader.saveMessagesThumbs(updates_channeldifference.new_messages);
                                                            final ArrayList arrayList = new ArrayList();
                                                            long j2 = (long) (-i4);
                                                            Integer num3 = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j2));
                                                            if (num3 == null) {
                                                                num3 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j2));
                                                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j2), num3);
                                                            }
                                                            Integer num4 = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j2));
                                                            if (num4 == null) {
                                                                num4 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j2));
                                                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j2), num4);
                                                            }
                                                            int i3 = 0;
                                                            while (i3 < updates_channeldifference.new_messages.size()) {
                                                                boolean z2;
                                                                MessageObject messageObject;
                                                                long j3;
                                                                ArrayList arrayList2;
                                                                Message message2 = (Message) updates_channeldifference.new_messages.get(i3);
                                                                if (chat == null || !chat.left) {
                                                                    if ((message2.out ? num4 : num3).intValue() < message2.id && !(message2.action instanceof TL_messageActionChannelCreate)) {
                                                                        z2 = true;
                                                                        message2.unread = z2;
                                                                        if (chat != null && chat.megagroup) {
                                                                            message2.flags |= i;
                                                                        }
                                                                        messageObject = new MessageObject(MessagesController.this.currentAccount, message2, sparseArray, MessagesController.this.createdDialogIds.contains(Long.valueOf(j2)));
                                                                        if (!messageObject.isOut() && messageObject.isUnread()) {
                                                                            arrayList.add(messageObject);
                                                                        }
                                                                        j3 = (long) (-i4);
                                                                        arrayList2 = (ArrayList) longSparseArray.get(j3);
                                                                        if (arrayList2 == null) {
                                                                            arrayList2 = new ArrayList();
                                                                            longSparseArray.put(j3, arrayList2);
                                                                        }
                                                                        arrayList2.add(messageObject);
                                                                        i3++;
                                                                        i = Integer.MIN_VALUE;
                                                                    }
                                                                }
                                                                z2 = false;
                                                                message2.unread = z2;
                                                                message2.flags |= i;
                                                                messageObject = new MessageObject(MessagesController.this.currentAccount, message2, sparseArray, MessagesController.this.createdDialogIds.contains(Long.valueOf(j2)));
                                                                arrayList.add(messageObject);
                                                                j3 = (long) (-i4);
                                                                arrayList2 = (ArrayList) longSparseArray.get(j3);
                                                                if (arrayList2 == null) {
                                                                    arrayList2 = new ArrayList();
                                                                    longSparseArray.put(j3, arrayList2);
                                                                }
                                                                arrayList2.add(messageObject);
                                                                i3++;
                                                                i = Integer.MIN_VALUE;
                                                            }
                                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                                public void run() {
                                                                    for (int i = 0; i < longSparseArray.size(); i++) {
                                                                        MessagesController.this.updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i));
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
                                                                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList, true, false);
                                                                    }
                                                                }

                                                                public void run() {
                                                                    if (!arrayList.isEmpty()) {
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
                                                        if (j3 != 0) {
                                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j3);
                                                        }
                                                    }
                                                }

                                                public void run() {
                                                    if (!arrayList.isEmpty()) {
                                                        final SparseArray sparseArray = new SparseArray();
                                                        Iterator it = arrayList.iterator();
                                                        while (it.hasNext()) {
                                                            TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) it.next();
                                                            Object updateMessageStateAndId = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, i4);
                                                            if (updateMessageStateAndId != null) {
                                                                sparseArray.put(tL_updateMessageID.id, updateMessageStateAndId);
                                                            }
                                                        }
                                                        if (sparseArray.size() != 0) {
                                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                                public void run() {
                                                                    for (int i = 0; i < sparseArray.size(); i++) {
                                                                        int keyAt = sparseArray.keyAt(i);
                                                                        SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage((int) ((long[]) sparseArray.valueAt(i))[1]);
                                                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(r5), Integer.valueOf(keyAt), null, Long.valueOf(r3[0]));
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
                                                MessagesController.this.checkChannelError(tL_error.text, i4);
                                            }
                                        });
                                        MessagesController.this.gettingDifferenceChannels.delete(i4);
                                        if (j3 != 0) {
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j3);
                                        }
                                    }
                                });
                                return;
                            }
                        } catch (Exception e3) {
                            e = e3;
                            nativeByteBuffer = null;
                            FileLog.m3e(e);
                            j4 = MessagesStorage.getInstance(r7.currentAccount).createPendingTask(nativeByteBuffer);
                            j2 = j4;
                            r7.gettingDifferenceChannels.put(i4, true);
                            tL_updates_getChannelDifference = new TL_updates_getChannelDifference();
                            tL_updates_getChannelDifference.channel = inputChannel2;
                            tL_updates_getChannelDifference.filter = new TL_channelMessagesFilterEmpty();
                            tL_updates_getChannelDifference.pts = i3;
                            tL_updates_getChannelDifference.limit = i6;
                            if (i5 != 3) {
                                z = false;
                            }
                            tL_updates_getChannelDifference.force = z;
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("start getChannelDifference with pts = ");
                                stringBuilder.append(i3);
                                stringBuilder.append(" channelId = ");
                                stringBuilder.append(i4);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            j3 = j2;
                            ConnectionsManager.getInstance(r7.currentAccount).sendRequest(tL_updates_getChannelDifference, /* anonymous class already generated */);
                            return;
                        }
                        j4 = MessagesStorage.getInstance(r7.currentAccount).createPendingTask(nativeByteBuffer);
                    }
                    j2 = j4;
                    r7.gettingDifferenceChannels.put(i4, true);
                    tL_updates_getChannelDifference = new TL_updates_getChannelDifference();
                    tL_updates_getChannelDifference.channel = inputChannel2;
                    tL_updates_getChannelDifference.filter = new TL_channelMessagesFilterEmpty();
                    tL_updates_getChannelDifference.pts = i3;
                    tL_updates_getChannelDifference.limit = i6;
                    if (i5 != 3) {
                        z = false;
                    }
                    tL_updates_getChannelDifference.force = z;
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("start getChannelDifference with pts = ");
                        stringBuilder.append(i3);
                        stringBuilder.append(" channelId = ");
                        stringBuilder.append(i4);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    j3 = j2;
                    ConnectionsManager.getInstance(r7.currentAccount).sendRequest(tL_updates_getChannelDifference, /* anonymous class already generated */);
                    return;
                }
            }
            if (j4 != 0) {
                MessagesStorage.getInstance(r7.currentAccount).removePendingTask(j4);
            }
        }
    }

    private void checkChannelError(String str, int i) {
        int hashCode = str.hashCode();
        if (hashCode != -NUM) {
            if (hashCode != -795226617) {
                if (hashCode == -471086771) {
                    if (str.equals("CHANNEL_PUBLIC_GROUP_NA") != null) {
                        str = 1;
                        switch (str) {
                            case null:
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(0));
                                return;
                            case 1:
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(1));
                                return;
                            case 2:
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(2));
                                return;
                            default:
                                return;
                        }
                    }
                }
            } else if (str.equals("CHANNEL_PRIVATE") != null) {
                str = null;
                switch (str) {
                    case null:
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(0));
                        return;
                    case 1:
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(1));
                        return;
                    case 2:
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(2));
                        return;
                    default:
                        return;
                }
            }
        } else if (str.equals("USER_BANNED_IN_CHANNEL") != null) {
            str = 2;
            switch (str) {
                case null:
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(0));
                    return;
                case 1:
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(1));
                    return;
                case 2:
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(2));
                    return;
                default:
                    return;
            }
        }
        str = -1;
        switch (str) {
            case null:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(0));
                return;
            case 1:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(1));
                return;
            case 2:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(i), Integer.valueOf(2));
                return;
            default:
                return;
        }
    }

    public void getDifference() {
        getDifference(MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue(), false);
    }

    public void getDifference(int i, final int i2, final int i3, boolean z) {
        registerForPush(SharedConfig.pushString);
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() == 0) {
            loadCurrentState();
        } else if (z || !this.gettingDifference) {
            this.gettingDifference = true;
            TLObject tL_updates_getDifference = new TL_updates_getDifference();
            tL_updates_getDifference.pts = i;
            tL_updates_getDifference.date = i2;
            tL_updates_getDifference.qts = i3;
            if (this.getDifferenceFirstSync) {
                tL_updates_getDifference.flags |= 1;
                if (ConnectionsManager.isConnectedOrConnectingToWiFi()) {
                    tL_updates_getDifference.pts_total_limit = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
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
                FileLog.m0d(stringBuilder.toString());
            }
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_updates_getDifference, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    int i = 0;
                    if (tL_error == null) {
                        final updates_Difference updates_difference = (updates_Difference) tLObject;
                        if ((updates_difference instanceof TL_updates_differenceTooLong) != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.loadedFullUsers.clear();
                                    MessagesController.this.loadedFullChats.clear();
                                    MessagesController.this.resetDialogs(true, MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), updates_difference.pts, i2, i3);
                                }
                            });
                            return;
                        }
                        if ((updates_difference instanceof TL_updates_differenceSlice) != null) {
                            MessagesController.this.getDifference(updates_difference.intermediate_state.pts, updates_difference.intermediate_state.date, updates_difference.intermediate_state.qts, true);
                        }
                        final SparseArray sparseArray = new SparseArray();
                        final SparseArray sparseArray2 = new SparseArray();
                        for (tLObject = null; tLObject < updates_difference.users.size(); tLObject++) {
                            User user = (User) updates_difference.users.get(tLObject);
                            sparseArray.put(user.id, user);
                        }
                        for (tLObject = null; tLObject < updates_difference.chats.size(); tLObject++) {
                            Chat chat = (Chat) updates_difference.chats.get(tLObject);
                            sparseArray2.put(chat.id, chat);
                        }
                        final ArrayList arrayList = new ArrayList();
                        if (updates_difference.other_updates.isEmpty() == null) {
                            while (i < updates_difference.other_updates.size()) {
                                Update update = (Update) updates_difference.other_updates.get(i);
                                if (update instanceof TL_updateMessageID) {
                                    arrayList.add((TL_updateMessageID) update);
                                    updates_difference.other_updates.remove(i);
                                    i--;
                                } else if (MessagesController.this.getUpdateType(update) == 2) {
                                    int access$400 = MessagesController.getUpdateChannelId(update);
                                    int i2 = MessagesController.this.channelsPts.get(access$400);
                                    if (i2 == 0) {
                                        i2 = MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(access$400);
                                        if (i2 != 0) {
                                            MessagesController.this.channelsPts.put(access$400, i2);
                                        }
                                    }
                                    if (i2 != 0 && MessagesController.getUpdatePts(update) <= i2) {
                                        updates_difference.other_updates.remove(i);
                                        i--;
                                    }
                                }
                                i++;
                            }
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.loadedFullUsers.clear();
                                MessagesController.this.loadedFullChats.clear();
                                MessagesController.this.putUsers(updates_difference.users, false);
                                MessagesController.this.putChats(updates_difference.chats, false);
                            }
                        });
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MessagesController$116$3$2 */
                            class C02992 implements Runnable {
                                C02992() {
                                }

                                public void run() {
                                    boolean z = true;
                                    boolean z2 = false;
                                    if (!(updates_difference.new_messages.isEmpty() && updates_difference.new_encrypted_messages.isEmpty())) {
                                        final LongSparseArray longSparseArray = new LongSparseArray();
                                        for (int i = 0; i < updates_difference.new_encrypted_messages.size(); i++) {
                                            Collection decryptMessage = SecretChatHelper.getInstance(MessagesController.this.currentAccount).decryptMessage((EncryptedMessage) updates_difference.new_encrypted_messages.get(i));
                                            if (!(decryptMessage == null || decryptMessage.isEmpty())) {
                                                updates_difference.new_messages.addAll(decryptMessage);
                                            }
                                        }
                                        ImageLoader.saveMessagesThumbs(updates_difference.new_messages);
                                        final ArrayList arrayList = new ArrayList();
                                        int clientUserId = UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId();
                                        int i2 = 0;
                                        while (i2 < updates_difference.new_messages.size()) {
                                            Message message = (Message) updates_difference.new_messages.get(i2);
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
                                                        ConcurrentHashMap concurrentHashMap = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                                                        Integer num = (Integer) concurrentHashMap.get(Long.valueOf(message.dialog_id));
                                                        if (num == null) {
                                                            num = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                                                            concurrentHashMap.put(Long.valueOf(message.dialog_id), num);
                                                        }
                                                        message.unread = num.intValue() < message.id ? z : z2;
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
                                            MessageObject messageObject = new MessageObject(MessagesController.this.currentAccount, message, sparseArray, sparseArray2, MessagesController.this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                                            if (!messageObject.isOut() && messageObject.isUnread()) {
                                                arrayList.add(messageObject);
                                            }
                                            ArrayList arrayList2 = (ArrayList) longSparseArray.get(message.dialog_id);
                                            if (arrayList2 == null) {
                                                arrayList2 = new ArrayList();
                                                longSparseArray.put(message.dialog_id, arrayList2);
                                            }
                                            arrayList2.add(messageObject);
                                            i2++;
                                            z = true;
                                            z2 = false;
                                        }
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                for (int i = 0; i < longSparseArray.size(); i++) {
                                                    MessagesController.this.updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i));
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
                                                    NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList, !(updates_difference instanceof TL_updates_differenceSlice), false);
                                                }
                                            }

                                            public void run() {
                                                if (!arrayList.isEmpty()) {
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
                                    if (updates_difference instanceof TL_updates_difference) {
                                        MessagesController.this.gettingDifference = false;
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(updates_difference.state.seq);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(updates_difference.state.date);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(updates_difference.state.pts);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(updates_difference.state.qts);
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                                        for (int i3 = 0; i3 < 3; i3++) {
                                            MessagesController.this.processUpdatesQueue(i3, 1);
                                        }
                                    } else if (updates_difference instanceof TL_updates_differenceSlice) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(updates_difference.intermediate_state.date);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(updates_difference.intermediate_state.pts);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(updates_difference.intermediate_state.qts);
                                    } else if (updates_difference instanceof TL_updates_differenceEmpty) {
                                        MessagesController.this.gettingDifference = false;
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(updates_difference.seq);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(updates_difference.date);
                                        int i4 = 0;
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                                        while (i4 < 3) {
                                            MessagesController.this.processUpdatesQueue(i4, 1);
                                            i4++;
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
                                int i = 0;
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(updates_difference.users, updates_difference.chats, true, false);
                                if (!arrayList.isEmpty()) {
                                    final SparseArray sparseArray = new SparseArray();
                                    while (i < arrayList.size()) {
                                        TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) arrayList.get(i);
                                        Object updateMessageStateAndId = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, 0);
                                        if (updateMessageStateAndId != null) {
                                            sparseArray.put(tL_updateMessageID.id, updateMessageStateAndId);
                                        }
                                        i++;
                                    }
                                    if (sparseArray.size() != 0) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                for (int i = 0; i < sparseArray.size(); i++) {
                                                    int keyAt = sparseArray.keyAt(i);
                                                    SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage((int) ((long[]) sparseArray.valueAt(i))[1]);
                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(r5), Integer.valueOf(keyAt), null, Long.valueOf(r3[0]));
                                                }
                                            }
                                        });
                                    }
                                }
                                Utilities.stageQueue.postRunnable(new C02992());
                            }
                        });
                        return;
                    }
                    MessagesController.this.gettingDifference = false;
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                }
            });
        }
    }

    public boolean canPinDialog(boolean z) {
        int i = 0;
        boolean z2 = i;
        while (i < this.dialogs.size()) {
            TL_dialog tL_dialog = (TL_dialog) this.dialogs.get(i);
            int i2 = (int) tL_dialog.id;
            if (!z || i2 == 0) {
                if (z || i2 != 0) {
                    if (tL_dialog.pinned) {
                        z2++;
                    }
                }
            }
            i++;
        }
        if (z2 < this.maxPinnedDialogsCount) {
            return true;
        }
        return false;
    }

    public boolean pinDialog(long j, boolean z, InputPeer inputPeer, long j2) {
        Throwable e;
        int i = (int) j;
        TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(j);
        boolean z2 = false;
        if (tL_dialog != null) {
            if (tL_dialog.pinned != z) {
                tL_dialog.pinned = z;
                if (z) {
                    int i2 = 0;
                    int i3 = i2;
                    while (i2 < this.dialogs.size()) {
                        TL_dialog tL_dialog2 = (TL_dialog) this.dialogs.get(i2);
                        if (!tL_dialog2.pinned) {
                            break;
                        }
                        i3 = Math.max(tL_dialog2.pinnedNum, i3);
                        i2++;
                    }
                    tL_dialog.pinnedNum = i3 + 1;
                } else {
                    tL_dialog.pinnedNum = 0;
                }
                sortDialogs(null);
                if (!z && this.dialogs.get(this.dialogs.size() - 1) == tL_dialog) {
                    this.dialogs.remove(this.dialogs.size() - 1);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (!(i == 0 || j2 == -1)) {
                    TLObject tL_messages_toggleDialogPin = new TL_messages_toggleDialogPin();
                    tL_messages_toggleDialogPin.pinned = z;
                    if (inputPeer == null) {
                        inputPeer = getInputPeer(i);
                    }
                    if (inputPeer instanceof TL_inputPeerEmpty) {
                        return false;
                    }
                    InputDialogPeer tL_inputDialogPeer = new TL_inputDialogPeer();
                    tL_inputDialogPeer.peer = inputPeer;
                    tL_messages_toggleDialogPin.peer = tL_inputDialogPeer;
                    if (j2 == 0) {
                        try {
                            j2 = new NativeByteBuffer(16 + inputPeer.getObjectSize());
                            try {
                                j2.writeInt32(1);
                                j2.writeInt64(j);
                                j2.writeBool(z);
                                inputPeer.serializeToStream(j2);
                            } catch (Exception e2) {
                                e = e2;
                                FileLog.m3e(e);
                                j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(j2);
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleDialogPin, new RequestDelegate() {
                                    public void run(TLObject tLObject, TL_error tL_error) {
                                        if (j2 != 0) {
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(j2);
                                        }
                                    }
                                });
                                MessagesStorage.getInstance(this.currentAccount).setDialogPinned(j, tL_dialog.pinnedNum);
                                return true;
                            }
                        } catch (Exception e3) {
                            e = e3;
                            j2 = 0;
                            FileLog.m3e(e);
                            j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(j2);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleDialogPin, /* anonymous class already generated */);
                            MessagesStorage.getInstance(this.currentAccount).setDialogPinned(j, tL_dialog.pinnedNum);
                            return true;
                        }
                        j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(j2);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleDialogPin, /* anonymous class already generated */);
                }
                MessagesStorage.getInstance(this.currentAccount).setDialogPinned(j, tL_dialog.pinnedNum);
                return true;
            }
        }
        if (tL_dialog != null) {
            z2 = true;
        }
        return z2;
    }

    public void loadPinnedDialogs(final long j, final ArrayList<Long> arrayList) {
        if (!UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tLObject != null) {
                        int i;
                        Chat chat;
                        MessageObject messageObject;
                        final TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
                        final TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
                        tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                        tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                        tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                        tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                        final LongSparseArray longSparseArray = new LongSparseArray();
                        tLObject = new SparseArray();
                        tL_error = new SparseArray();
                        final ArrayList arrayList = new ArrayList();
                        for (i = 0; i < tL_messages_peerDialogs.users.size(); i++) {
                            User user = (User) tL_messages_peerDialogs.users.get(i);
                            tLObject.put(user.id, user);
                        }
                        for (i = 0; i < tL_messages_peerDialogs.chats.size(); i++) {
                            chat = (Chat) tL_messages_peerDialogs.chats.get(i);
                            tL_error.put(chat.id, chat);
                        }
                        for (i = 0; i < tL_messages_peerDialogs.messages.size(); i++) {
                            Message message = (Message) tL_messages_peerDialogs.messages.get(i);
                            if (message.to_id.channel_id != 0) {
                                chat = (Chat) tL_error.get(message.to_id.channel_id);
                                if (chat != null && chat.left) {
                                }
                            } else if (message.to_id.chat_id != 0) {
                                chat = (Chat) tL_error.get(message.to_id.chat_id);
                                if (!(chat == null || chat.migrated_to == null)) {
                                }
                            }
                            messageObject = new MessageObject(MessagesController.this.currentAccount, message, (SparseArray) tLObject, (SparseArray) tL_error, false);
                            longSparseArray.put(messageObject.getDialogId(), messageObject);
                        }
                        for (tLObject = null; tLObject < tL_messages_peerDialogs.dialogs.size(); tLObject++) {
                            TL_dialog tL_dialog = (TL_dialog) tL_messages_peerDialogs.dialogs.get(tLObject);
                            if (tL_dialog.id == 0) {
                                if (tL_dialog.peer.user_id != 0) {
                                    tL_dialog.id = (long) tL_dialog.peer.user_id;
                                } else if (tL_dialog.peer.chat_id != 0) {
                                    tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                                } else if (tL_dialog.peer.channel_id != 0) {
                                    tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                                }
                            }
                            arrayList.add(Long.valueOf(tL_dialog.id));
                            if (DialogObject.isChannel(tL_dialog)) {
                                chat = (Chat) tL_error.get(-((int) tL_dialog.id));
                                if (chat != null && chat.left) {
                                }
                            } else if (((int) tL_dialog.id) < 0) {
                                chat = (Chat) tL_error.get(-((int) tL_dialog.id));
                                if (!(chat == null || chat.migrated_to == null)) {
                                }
                            }
                            if (tL_dialog.last_message_date == 0) {
                                messageObject = (MessageObject) longSparseArray.get(tL_dialog.id);
                                if (messageObject != null) {
                                    tL_dialog.last_message_date = messageObject.messageOwner.date;
                                }
                            }
                            Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                            if (num == null) {
                                num = Integer.valueOf(0);
                            }
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_inbox_max_id)));
                            num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                            if (num == null) {
                                num = Integer.valueOf(0);
                            }
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_outbox_max_id)));
                        }
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MessagesController$118$1$1 */
                            class C03011 implements Runnable {
                                C03011() {
                                }

                                public void run() {
                                    boolean z;
                                    MessagesController.this.applyDialogsNotificationsSettings(tL_messages_peerDialogs.dialogs);
                                    LongSparseArray longSparseArray = new LongSparseArray();
                                    ArrayList arrayList = new ArrayList();
                                    int i = 0;
                                    int i2 = i;
                                    int i3 = i2;
                                    while (i < MessagesController.this.dialogs.size()) {
                                        TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs.get(i);
                                        if (((int) tL_dialog.id) != 0) {
                                            if (!tL_dialog.pinned) {
                                                break;
                                            }
                                            i2 = Math.max(tL_dialog.pinnedNum, i2);
                                            longSparseArray.put(tL_dialog.id, Integer.valueOf(tL_dialog.pinnedNum));
                                            arrayList.add(Long.valueOf(tL_dialog.id));
                                            tL_dialog.pinned = false;
                                            tL_dialog.pinnedNum = 0;
                                            i3 = true;
                                        }
                                        i++;
                                    }
                                    ArrayList arrayList2 = new ArrayList();
                                    ArrayList arrayList3 = arrayList != null ? arrayList : arrayList;
                                    if (arrayList3.size() < arrayList.size()) {
                                        arrayList3.add(Long.valueOf(0));
                                    }
                                    while (arrayList.size() < arrayList3.size()) {
                                        arrayList.add(0, Long.valueOf(0));
                                    }
                                    if (tL_messages_peerDialogs.dialogs.isEmpty()) {
                                        z = false;
                                    } else {
                                        MessagesController.this.putUsers(tL_messages_peerDialogs.users, false);
                                        MessagesController.this.putChats(tL_messages_peerDialogs.chats, false);
                                        z = false;
                                        int i4 = i3;
                                        i3 = z;
                                        while (i3 < tL_messages_peerDialogs.dialogs.size()) {
                                            TL_dialog tL_dialog2 = (TL_dialog) tL_messages_peerDialogs.dialogs.get(i3);
                                            Integer num;
                                            if (j != 0) {
                                                num = (Integer) longSparseArray.get(tL_dialog2.id);
                                                if (num != null) {
                                                    tL_dialog2.pinnedNum = num.intValue();
                                                }
                                            } else {
                                                int indexOf = arrayList.indexOf(Long.valueOf(tL_dialog2.id));
                                                int indexOf2 = arrayList3.indexOf(Long.valueOf(tL_dialog2.id));
                                                if (!(indexOf == -1 || indexOf2 == -1)) {
                                                    if (indexOf == indexOf2) {
                                                        num = (Integer) longSparseArray.get(tL_dialog2.id);
                                                        if (num != null) {
                                                            tL_dialog2.pinnedNum = num.intValue();
                                                        }
                                                    } else {
                                                        num = (Integer) longSparseArray.get(((Long) arrayList.get(indexOf2)).longValue());
                                                        if (num != null) {
                                                            tL_dialog2.pinnedNum = num.intValue();
                                                        }
                                                    }
                                                }
                                            }
                                            if (tL_dialog2.pinnedNum == 0) {
                                                tL_dialog2.pinnedNum = (tL_messages_peerDialogs.dialogs.size() - i3) + i2;
                                            }
                                            arrayList2.add(Long.valueOf(tL_dialog2.id));
                                            TL_dialog tL_dialog3 = (TL_dialog) MessagesController.this.dialogs_dict.get(tL_dialog2.id);
                                            if (tL_dialog3 != null) {
                                                tL_dialog3.pinned = true;
                                                tL_dialog3.pinnedNum = tL_dialog2.pinnedNum;
                                                MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogPinned(tL_dialog2.id, tL_dialog2.pinnedNum);
                                            } else {
                                                MessagesController.this.dialogs_dict.put(tL_dialog2.id, tL_dialog2);
                                                MessageObject messageObject = (MessageObject) longSparseArray.get(tL_dialog2.id);
                                                MessagesController.this.dialogMessage.put(tL_dialog2.id, messageObject);
                                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                                    MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                                    if (messageObject.messageOwner.random_id != 0) {
                                                        MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                                    }
                                                }
                                                z = true;
                                            }
                                            i3++;
                                            boolean z2 = true;
                                        }
                                        i3 = i4;
                                    }
                                    if (i3 != 0) {
                                        if (z) {
                                            MessagesController.this.dialogs.clear();
                                            int size = MessagesController.this.dialogs_dict.size();
                                            for (int i5 = 0; i5 < size; i5++) {
                                                MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(i5));
                                            }
                                        }
                                        MessagesController.this.sortDialogs(null);
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                    }
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).unpinAllDialogsExceptNew(arrayList2);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(tL_messages_dialogs, true);
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

    public void generateJoinMessage(final int i, boolean z) {
        Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i, this.currentAccount)) {
            if ((!chat.left && !chat.kicked) || z) {
                z = new TL_messageService();
                z.flags = 256;
                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                z.id = newMessageId;
                z.local_id = newMessageId;
                z.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                z.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                z.to_id = new TL_peerChannel();
                z.to_id.channel_id = i;
                z.dialog_id = (long) (-i);
                z.post = true;
                z.action = new TL_messageActionChatAddUser();
                z.action.users.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (chat.megagroup) {
                    z.flags |= Integer.MIN_VALUE;
                }
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                final ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(z);
                arrayList.add(new MessageObject(this.currentAccount, z, true));
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.MessagesController$119$1 */
                    class C03031 implements Runnable {
                        C03031() {
                        }

                        public void run() {
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList, true, false);
                        }
                    }

                    public void run() {
                        AndroidUtilities.runOnUIThread(new C03031());
                    }
                });
                MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList2, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MessagesController.this.updateInterfaceWithMessages((long) (-i), arrayList);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                });
            }
        }
    }

    public void checkChannelInviter(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                final Chat chat = MessagesController.this.getChat(Integer.valueOf(i));
                if (chat != null && ChatObject.isChannel(i, MessagesController.this.currentAccount)) {
                    if (!chat.creator) {
                        TLObject tL_channels_getParticipant = new TL_channels_getParticipant();
                        tL_channels_getParticipant.channel = MessagesController.this.getInputChannel(i);
                        tL_channels_getParticipant.user_id = new TL_inputUserSelf();
                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(tL_channels_getParticipant, new RequestDelegate() {
                            public void run(TLObject tLObject, TL_error tL_error) {
                                final TL_channels_channelParticipant tL_channels_channelParticipant = (TL_channels_channelParticipant) tLObject;
                                if (tL_channels_channelParticipant != null && (tL_channels_channelParticipant.participant instanceof TL_channelParticipantSelf) != null && tL_channels_channelParticipant.participant.inviter_id != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId() && (chat.megagroup == null || MessagesStorage.getInstance(MessagesController.this.currentAccount).isMigratedChat(chat.id) == null)) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.this.putUsers(tL_channels_channelParticipant.users, false);
                                        }
                                    });
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(tL_channels_channelParticipant.users, null, true, true);
                                    Message tL_messageService = new TL_messageService();
                                    tL_messageService.media_unread = true;
                                    tL_messageService.unread = true;
                                    tL_messageService.flags = 256;
                                    tL_messageService.post = true;
                                    if (chat.megagroup) {
                                        tL_messageService.flags |= Integer.MIN_VALUE;
                                    }
                                    int newMessageId = UserConfig.getInstance(MessagesController.this.currentAccount).getNewMessageId();
                                    tL_messageService.id = newMessageId;
                                    tL_messageService.local_id = newMessageId;
                                    tL_messageService.date = tL_channels_channelParticipant.participant.date;
                                    tL_messageService.action = new TL_messageActionChatAddUser();
                                    tL_messageService.from_id = tL_channels_channelParticipant.participant.inviter_id;
                                    tL_messageService.action.users.add(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                                    tL_messageService.to_id = new TL_peerChannel();
                                    tL_messageService.to_id.channel_id = i;
                                    tL_messageService.dialog_id = (long) (-i);
                                    int i = 0;
                                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                                    final ArrayList arrayList = new ArrayList();
                                    ArrayList arrayList2 = new ArrayList();
                                    AbstractMap concurrentHashMap = new ConcurrentHashMap();
                                    while (i < tL_channels_channelParticipant.users.size()) {
                                        User user = (User) tL_channels_channelParticipant.users.get(i);
                                        concurrentHashMap.put(Integer.valueOf(user.id), user);
                                        i++;
                                    }
                                    arrayList2.add(tL_messageService);
                                    arrayList.add(new MessageObject(MessagesController.this.currentAccount, tL_messageService, concurrentHashMap, true));
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                        /* renamed from: org.telegram.messenger.MessagesController$121$1$2$1 */
                                        class C03051 implements Runnable {
                                            C03051() {
                                            }

                                            public void run() {
                                                NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList, true, false);
                                            }
                                        }

                                        public void run() {
                                            AndroidUtilities.runOnUIThread(new C03051());
                                        }
                                    });
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(arrayList2, true, true, false, 0);
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.this.updateInterfaceWithMessages((long) (-i), arrayList);
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
                    if ((update instanceof TL_updateChannelWebPage) == null) {
                        return 3;
                    }
                }
                return 2;
            }
        }
        return null;
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
        return update instanceof TL_updateChannelTooLong ? ((TL_updateChannelTooLong) update).pts : null;
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
        return update instanceof TL_updateReadMessagesContents ? ((TL_updateReadMessagesContents) update).pts_count : null;
    }

    private static int getUpdateQts(Update update) {
        return update instanceof TL_updateNewEncryptedMessage ? ((TL_updateNewEncryptedMessage) update).qts : null;
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
        return null;
    }

    public void processUpdates(Updates updates, boolean z) {
        boolean z2;
        boolean z3;
        int i;
        int keyAt;
        TLObject tL_messages_receivedQueue;
        MessagesController messagesController = this;
        final Updates updates2 = updates;
        ArrayList arrayList = null;
        boolean z4 = false;
        boolean z5 = true;
        if (updates2 instanceof TL_updateShort) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(updates2.update);
            processUpdateArray(arrayList2, null, null, false);
        } else {
            boolean z6;
            int i2;
            int i3;
            StringBuilder stringBuilder;
            Object obj;
            User user;
            Object obj2;
            MessageEntity messageEntity;
            User userSync;
            boolean z7;
            Message tL_message;
            ConcurrentHashMap concurrentHashMap;
            Integer num;
            boolean z8;
            MessageObject messageObject;
            final ArrayList arrayList3;
            ArrayList arrayList4;
            boolean z9 = updates2 instanceof TL_updateShortChatMessage;
            long j = 0;
            if (!z9) {
                if (!(updates2 instanceof TL_updateShortMessage)) {
                    int i4;
                    Chat chat;
                    Update update;
                    TL_updates tL_updates;
                    Update update2;
                    int updatePts;
                    int updatePtsCount;
                    StringBuilder stringBuilder2;
                    int updateChannelId;
                    Object obj3;
                    StringBuilder stringBuilder3;
                    long j2;
                    ArrayList arrayList5;
                    TL_updates tL_updates2;
                    Update update3;
                    Object obj4;
                    z9 = updates2 instanceof TL_updatesCombined;
                    if (!z9) {
                        if (!(updates2 instanceof TL_updates)) {
                            if (updates2 instanceof TL_updatesTooLong) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("need get diff TL_updatesTooLong");
                                }
                                z2 = false;
                                z3 = z2;
                                z4 = true;
                                SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
                                if (!z) {
                                    for (i = 0; i < messagesController.updatesQueueChannels.size(); i++) {
                                        keyAt = messagesController.updatesQueueChannels.keyAt(i);
                                        if (arrayList == null && arrayList.contains(Integer.valueOf(keyAt))) {
                                            getChannelDifference(keyAt);
                                        } else {
                                            processChannelsUpdatesQueue(keyAt, 0);
                                        }
                                    }
                                    if (z4) {
                                        getDifference();
                                    } else {
                                        for (i = 0; i < 3; i++) {
                                            processUpdatesQueue(i, 0);
                                        }
                                    }
                                }
                                if (z3) {
                                    tL_messages_receivedQueue = new TL_messages_receivedQueue();
                                    tL_messages_receivedQueue.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                                    ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_receivedQueue, new RequestDelegate() {
                                        public void run(TLObject tLObject, TL_error tL_error) {
                                        }
                                    });
                                }
                                if (z2) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                                        }
                                    });
                                }
                                MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
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
                    SparseArray sparseArray = null;
                    for (i4 = 0; i4 < updates2.chats.size(); i4++) {
                        Chat chat2 = (Chat) updates2.chats.get(i4);
                        if ((chat2 instanceof TL_channel) && chat2.min) {
                            chat = getChat(Integer.valueOf(chat2.id));
                            if (chat == null || chat.min) {
                                chat = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(updates2.chat_id);
                                putChat(chat, true);
                            }
                            if (chat == null || chat.min) {
                                if (sparseArray == null) {
                                    sparseArray = new SparseArray();
                                }
                                sparseArray.put(chat2.id, chat2);
                            }
                        }
                    }
                    if (sparseArray != null) {
                        for (i4 = 0; i4 < updates2.updates.size(); i4++) {
                            update = (Update) updates2.updates.get(i4);
                            if (update instanceof TL_updateNewChannelMessage) {
                                int i5 = ((TL_updateNewChannelMessage) update).message.to_id.channel_id;
                                if (sparseArray.indexOfKey(i5) >= 0) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        StringBuilder stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append("need get diff because of min channel ");
                                        stringBuilder4.append(i5);
                                        FileLog.m0d(stringBuilder4.toString());
                                    }
                                    z6 = true;
                                    if (z6) {
                                        MessagesStorage.getInstance(messagesController.currentAccount).putUsersAndChats(updates2.users, updates2.chats, true, true);
                                        Collections.sort(updates2.updates, messagesController.updatesComparator);
                                        z3 = false;
                                        while (updates2.updates.size() > 0) {
                                            update = (Update) updates2.updates.get(z4);
                                            if (getUpdateType(update) != 0) {
                                                tL_updates = new TL_updates();
                                                tL_updates.updates.add(update);
                                                tL_updates.pts = getUpdatePts(update);
                                                tL_updates.pts_count = getUpdatePtsCount(update);
                                                while (z5 < updates2.updates.size()) {
                                                    update2 = (Update) updates2.updates.get(z5);
                                                    updatePts = getUpdatePts(update2);
                                                    updatePtsCount = getUpdatePtsCount(update2);
                                                    if (getUpdateType(update2) != 0 || tL_updates.pts + updatePtsCount != updatePts) {
                                                        break;
                                                    }
                                                    tL_updates.updates.add(update2);
                                                    tL_updates.pts = updatePts;
                                                    tL_updates.pts_count += updatePtsCount;
                                                    updates2.updates.remove(z5);
                                                }
                                                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + tL_updates.pts_count != tL_updates.pts) {
                                                    if (processUpdateArray(tL_updates.updates, updates2.users, updates2.chats, z4)) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            stringBuilder2 = new StringBuilder();
                                                            stringBuilder2.append("need get diff inner TL_updates, pts: ");
                                                            stringBuilder2.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                                                            stringBuilder2.append(" ");
                                                            stringBuilder2.append(updates2.seq);
                                                            FileLog.m0d(stringBuilder2.toString());
                                                        }
                                                        z6 = z5;
                                                    } else {
                                                        MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(tL_updates.pts);
                                                    }
                                                } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() == tL_updates.pts) {
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append(update);
                                                        stringBuilder2.append(" need get diff, pts: ");
                                                        stringBuilder2.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                                                        stringBuilder2.append(" ");
                                                        stringBuilder2.append(tL_updates.pts);
                                                        stringBuilder2.append(" count = ");
                                                        stringBuilder2.append(tL_updates.pts_count);
                                                        FileLog.m0d(stringBuilder2.toString());
                                                    }
                                                    if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimePts == j)) {
                                                        if (messagesController.updatesStartWaitTimePts != j && Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) <= 1500) {
                                                        }
                                                    }
                                                    if (messagesController.updatesStartWaitTimePts == j) {
                                                        messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                                                    }
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("add to queue");
                                                    }
                                                    messagesController.updatesQueuePts.add(tL_updates);
                                                }
                                                updates2.updates.remove(0);
                                                z4 = false;
                                                z5 = true;
                                                j = 0;
                                            } else {
                                                if (getUpdateType(update) != 1) {
                                                    if (getUpdateType(update) == 2) {
                                                        break;
                                                    }
                                                    updateChannelId = getUpdateChannelId(update);
                                                    i2 = messagesController.channelsPts.get(updateChannelId);
                                                    if (i2 == 0) {
                                                        i2 = MessagesStorage.getInstance(messagesController.currentAccount).getChannelPtsSync(updateChannelId);
                                                        if (i2 == 0) {
                                                            for (i3 = 0; i3 < updates2.chats.size(); i3++) {
                                                                chat = (Chat) updates2.chats.get(i3);
                                                                if (chat.id == updateChannelId) {
                                                                    loadUnknownChannel(chat, j);
                                                                    obj3 = 1;
                                                                    break;
                                                                }
                                                            }
                                                        } else {
                                                            messagesController.channelsPts.put(updateChannelId, i2);
                                                        }
                                                    }
                                                    obj3 = null;
                                                    tL_updates = new TL_updates();
                                                    tL_updates.updates.add(update);
                                                    tL_updates.pts = getUpdatePts(update);
                                                    tL_updates.pts_count = getUpdatePtsCount(update);
                                                    while (1 < updates2.updates.size()) {
                                                        update2 = (Update) updates2.updates.get(1);
                                                        updatePts = getUpdatePts(update2);
                                                        updatePtsCount = getUpdatePtsCount(update2);
                                                        if (getUpdateType(update2) != 2 || updateChannelId != getUpdateChannelId(update2) || tL_updates.pts + updatePtsCount != updatePts) {
                                                            break;
                                                        }
                                                        tL_updates.updates.add(update2);
                                                        tL_updates.pts = updatePts;
                                                        tL_updates.pts_count += updatePtsCount;
                                                        updates2.updates.remove(1);
                                                    }
                                                    if (obj3 != null) {
                                                        if (tL_updates.pts_count + i2 != tL_updates.pts) {
                                                            if (processUpdateArray(tL_updates.updates, updates2.users, updates2.chats, false)) {
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    stringBuilder3 = new StringBuilder();
                                                                    stringBuilder3.append("need get channel diff inner TL_updates, channel_id = ");
                                                                    stringBuilder3.append(updateChannelId);
                                                                    FileLog.m0d(stringBuilder3.toString());
                                                                }
                                                                if (arrayList == null) {
                                                                    arrayList = new ArrayList();
                                                                } else if (!arrayList.contains(Integer.valueOf(updateChannelId))) {
                                                                    arrayList.add(Integer.valueOf(updateChannelId));
                                                                }
                                                            } else {
                                                                messagesController.channelsPts.put(updateChannelId, tL_updates.pts);
                                                                MessagesStorage.getInstance(messagesController.currentAccount).saveChannelPts(updateChannelId, tL_updates.pts);
                                                            }
                                                        } else if (i2 == tL_updates.pts) {
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                stringBuilder3 = new StringBuilder();
                                                                stringBuilder3.append(update);
                                                                stringBuilder3.append(" need get channel diff, pts: ");
                                                                stringBuilder3.append(i2);
                                                                stringBuilder3.append(" ");
                                                                stringBuilder3.append(tL_updates.pts);
                                                                stringBuilder3.append(" count = ");
                                                                stringBuilder3.append(tL_updates.pts_count);
                                                                stringBuilder3.append(" channelId = ");
                                                                stringBuilder3.append(updateChannelId);
                                                                FileLog.m0d(stringBuilder3.toString());
                                                            }
                                                            j2 = messagesController.updatesStartWaitTimeChannels.get(updateChannelId);
                                                            if (!(messagesController.gettingDifferenceChannels.get(updateChannelId) || j2 == 0)) {
                                                                if (Math.abs(System.currentTimeMillis() - j2) > 1500) {
                                                                    if (arrayList == null) {
                                                                        arrayList = new ArrayList();
                                                                    } else if (!arrayList.contains(Integer.valueOf(updateChannelId))) {
                                                                        arrayList.add(Integer.valueOf(updateChannelId));
                                                                    }
                                                                }
                                                            }
                                                            if (j2 == 0) {
                                                                messagesController.updatesStartWaitTimeChannels.put(updateChannelId, System.currentTimeMillis());
                                                            }
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                FileLog.m0d("add to queue");
                                                            }
                                                            arrayList5 = (ArrayList) messagesController.updatesQueueChannels.get(updateChannelId);
                                                            if (arrayList5 == null) {
                                                                arrayList5 = new ArrayList();
                                                                messagesController.updatesQueueChannels.put(updateChannelId, arrayList5);
                                                            }
                                                            arrayList5.add(tL_updates);
                                                        }
                                                    } else if (BuildVars.LOGS_ENABLED) {
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append("need load unknown channel = ");
                                                        stringBuilder3.append(updateChannelId);
                                                        FileLog.m0d(stringBuilder3.toString());
                                                    }
                                                } else {
                                                    tL_updates2 = new TL_updates();
                                                    tL_updates2.updates.add(update);
                                                    tL_updates2.pts = getUpdateQts(update);
                                                    while (1 < updates2.updates.size()) {
                                                        update3 = (Update) updates2.updates.get(1);
                                                        i3 = getUpdateQts(update3);
                                                        if (getUpdateType(update3) != 1 || tL_updates2.pts + 1 != i3) {
                                                            break;
                                                        }
                                                        tL_updates2.updates.add(update3);
                                                        tL_updates2.pts = i3;
                                                        updates2.updates.remove(1);
                                                    }
                                                    if (MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue() != 0) {
                                                        if (MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue() + tL_updates2.updates.size() == tL_updates2.pts) {
                                                            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() == tL_updates2.pts) {
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    stringBuilder3 = new StringBuilder();
                                                                    stringBuilder3.append(update);
                                                                    stringBuilder3.append(" need get diff, qts: ");
                                                                    stringBuilder3.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
                                                                    stringBuilder3.append(" ");
                                                                    stringBuilder3.append(tL_updates2.pts);
                                                                    FileLog.m0d(stringBuilder3.toString());
                                                                }
                                                                if (messagesController.gettingDifference || messagesController.updatesStartWaitTimeQts == j || (messagesController.updatesStartWaitTimeQts != j && Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeQts) <= 1500)) {
                                                                    if (messagesController.updatesStartWaitTimeQts == j) {
                                                                        messagesController.updatesStartWaitTimeQts = System.currentTimeMillis();
                                                                    }
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                        FileLog.m0d("add to queue");
                                                                    }
                                                                    messagesController.updatesQueueQts.add(tL_updates2);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    processUpdateArray(tL_updates2.updates, updates2.users, updates2.chats, false);
                                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastQtsValue(tL_updates2.pts);
                                                    z3 = true;
                                                }
                                                updates2.updates.remove(0);
                                                z4 = false;
                                                z5 = true;
                                                j = 0;
                                            }
                                            z6 = true;
                                            updates2.updates.remove(0);
                                            z4 = false;
                                            z5 = true;
                                            j = 0;
                                        }
                                        if (!z9) {
                                            if (!(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 == updates2.seq || updates2.seq == 0)) {
                                                if (updates2.seq == MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue()) {
                                                }
                                            }
                                            obj4 = 1;
                                            if (obj4 == null) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    if (z9) {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("need get diff TL_updatesCombined, seq: ");
                                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                                        stringBuilder.append(" ");
                                                        stringBuilder.append(updates2.seq_start);
                                                        FileLog.m0d(stringBuilder.toString());
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("need get diff TL_updates, seq: ");
                                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                                        stringBuilder.append(" ");
                                                        stringBuilder.append(updates2.seq);
                                                        FileLog.m0d(stringBuilder.toString());
                                                    }
                                                }
                                                if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) <= 1500) {
                                                    z4 = true;
                                                } else {
                                                    if (messagesController.updatesStartWaitTimeSeq == 0) {
                                                        messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                                                    }
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("add TL_updates/Combined to queue");
                                                    }
                                                    messagesController.updatesQueueSeq.add(updates2);
                                                }
                                            } else {
                                                processUpdateArray(updates2.updates, updates2.users, updates2.chats, false);
                                                if (updates2.seq != 0) {
                                                    if (updates2.date != 0) {
                                                        MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(updates2.date);
                                                    }
                                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                                                }
                                            }
                                            z4 = z6;
                                        } else {
                                            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 != updates2.seq_start) {
                                                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() == updates2.seq_start) {
                                                }
                                            }
                                            obj4 = 1;
                                            if (obj4 == null) {
                                                processUpdateArray(updates2.updates, updates2.users, updates2.chats, false);
                                                if (updates2.seq != 0) {
                                                    if (updates2.date != 0) {
                                                        MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(updates2.date);
                                                    }
                                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                                                }
                                            } else {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    if (z9) {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("need get diff TL_updatesCombined, seq: ");
                                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                                        stringBuilder.append(" ");
                                                        stringBuilder.append(updates2.seq_start);
                                                        FileLog.m0d(stringBuilder.toString());
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("need get diff TL_updates, seq: ");
                                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                                        stringBuilder.append(" ");
                                                        stringBuilder.append(updates2.seq);
                                                        FileLog.m0d(stringBuilder.toString());
                                                    }
                                                }
                                                if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimeSeq == 0)) {
                                                    if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) <= 1500) {
                                                        z4 = true;
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
                                            z4 = z6;
                                        }
                                        obj4 = null;
                                        if (obj4 == null) {
                                            processUpdateArray(updates2.updates, updates2.users, updates2.chats, false);
                                            if (updates2.seq != 0) {
                                                if (updates2.date != 0) {
                                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(updates2.date);
                                                }
                                                MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                                            }
                                        } else {
                                            if (BuildVars.LOGS_ENABLED) {
                                                if (z9) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("need get diff TL_updatesCombined, seq: ");
                                                    stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                                    stringBuilder.append(" ");
                                                    stringBuilder.append(updates2.seq_start);
                                                    FileLog.m0d(stringBuilder.toString());
                                                } else {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("need get diff TL_updates, seq: ");
                                                    stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                                    stringBuilder.append(" ");
                                                    stringBuilder.append(updates2.seq);
                                                    FileLog.m0d(stringBuilder.toString());
                                                }
                                            }
                                            if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) <= 1500) {
                                                if (messagesController.updatesStartWaitTimeSeq == 0) {
                                                    messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                                                }
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.m0d("add TL_updates/Combined to queue");
                                                }
                                                messagesController.updatesQueueSeq.add(updates2);
                                            } else {
                                                z4 = true;
                                            }
                                        }
                                        z4 = z6;
                                    } else {
                                        z4 = z6;
                                        z3 = false;
                                    }
                                    z2 = false;
                                    SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
                                    if (z) {
                                        for (i = 0; i < messagesController.updatesQueueChannels.size(); i++) {
                                            keyAt = messagesController.updatesQueueChannels.keyAt(i);
                                            if (arrayList == null) {
                                            }
                                            processChannelsUpdatesQueue(keyAt, 0);
                                        }
                                        if (z4) {
                                            for (i = 0; i < 3; i++) {
                                                processUpdatesQueue(i, 0);
                                            }
                                        } else {
                                            getDifference();
                                        }
                                    }
                                    if (z3) {
                                        tL_messages_receivedQueue = new TL_messages_receivedQueue();
                                        tL_messages_receivedQueue.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                                        ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_receivedQueue, /* anonymous class already generated */);
                                    }
                                    if (z2) {
                                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                    }
                                    MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
                                }
                            }
                        }
                    }
                    z6 = false;
                    if (z6) {
                        z4 = z6;
                        z3 = false;
                    } else {
                        MessagesStorage.getInstance(messagesController.currentAccount).putUsersAndChats(updates2.users, updates2.chats, true, true);
                        Collections.sort(updates2.updates, messagesController.updatesComparator);
                        z3 = false;
                        while (updates2.updates.size() > 0) {
                            update = (Update) updates2.updates.get(z4);
                            if (getUpdateType(update) != 0) {
                                if (getUpdateType(update) != 1) {
                                    tL_updates2 = new TL_updates();
                                    tL_updates2.updates.add(update);
                                    tL_updates2.pts = getUpdateQts(update);
                                    while (1 < updates2.updates.size()) {
                                        update3 = (Update) updates2.updates.get(1);
                                        i3 = getUpdateQts(update3);
                                        tL_updates2.updates.add(update3);
                                        tL_updates2.pts = i3;
                                        updates2.updates.remove(1);
                                    }
                                    if (MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue() != 0) {
                                        if (MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue() + tL_updates2.updates.size() == tL_updates2.pts) {
                                            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() == tL_updates2.pts) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append(update);
                                                    stringBuilder3.append(" need get diff, qts: ");
                                                    stringBuilder3.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
                                                    stringBuilder3.append(" ");
                                                    stringBuilder3.append(tL_updates2.pts);
                                                    FileLog.m0d(stringBuilder3.toString());
                                                }
                                                if (messagesController.updatesStartWaitTimeQts == j) {
                                                    messagesController.updatesStartWaitTimeQts = System.currentTimeMillis();
                                                }
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.m0d("add to queue");
                                                }
                                                messagesController.updatesQueueQts.add(tL_updates2);
                                            }
                                        }
                                    }
                                    processUpdateArray(tL_updates2.updates, updates2.users, updates2.chats, false);
                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastQtsValue(tL_updates2.pts);
                                    z3 = true;
                                } else if (getUpdateType(update) == 2) {
                                    break;
                                } else {
                                    updateChannelId = getUpdateChannelId(update);
                                    i2 = messagesController.channelsPts.get(updateChannelId);
                                    if (i2 == 0) {
                                        i2 = MessagesStorage.getInstance(messagesController.currentAccount).getChannelPtsSync(updateChannelId);
                                        if (i2 == 0) {
                                            for (i3 = 0; i3 < updates2.chats.size(); i3++) {
                                                chat = (Chat) updates2.chats.get(i3);
                                                if (chat.id == updateChannelId) {
                                                    loadUnknownChannel(chat, j);
                                                    obj3 = 1;
                                                    break;
                                                }
                                            }
                                        } else {
                                            messagesController.channelsPts.put(updateChannelId, i2);
                                        }
                                    }
                                    obj3 = null;
                                    tL_updates = new TL_updates();
                                    tL_updates.updates.add(update);
                                    tL_updates.pts = getUpdatePts(update);
                                    tL_updates.pts_count = getUpdatePtsCount(update);
                                    while (1 < updates2.updates.size()) {
                                        update2 = (Update) updates2.updates.get(1);
                                        updatePts = getUpdatePts(update2);
                                        updatePtsCount = getUpdatePtsCount(update2);
                                        tL_updates.updates.add(update2);
                                        tL_updates.pts = updatePts;
                                        tL_updates.pts_count += updatePtsCount;
                                        updates2.updates.remove(1);
                                    }
                                    if (obj3 != null) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("need load unknown channel = ");
                                            stringBuilder3.append(updateChannelId);
                                            FileLog.m0d(stringBuilder3.toString());
                                        }
                                    } else if (tL_updates.pts_count + i2 != tL_updates.pts) {
                                        if (i2 == tL_updates.pts) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append(update);
                                                stringBuilder3.append(" need get channel diff, pts: ");
                                                stringBuilder3.append(i2);
                                                stringBuilder3.append(" ");
                                                stringBuilder3.append(tL_updates.pts);
                                                stringBuilder3.append(" count = ");
                                                stringBuilder3.append(tL_updates.pts_count);
                                                stringBuilder3.append(" channelId = ");
                                                stringBuilder3.append(updateChannelId);
                                                FileLog.m0d(stringBuilder3.toString());
                                            }
                                            j2 = messagesController.updatesStartWaitTimeChannels.get(updateChannelId);
                                            if (Math.abs(System.currentTimeMillis() - j2) > 1500) {
                                                if (j2 == 0) {
                                                    messagesController.updatesStartWaitTimeChannels.put(updateChannelId, System.currentTimeMillis());
                                                }
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.m0d("add to queue");
                                                }
                                                arrayList5 = (ArrayList) messagesController.updatesQueueChannels.get(updateChannelId);
                                                if (arrayList5 == null) {
                                                    arrayList5 = new ArrayList();
                                                    messagesController.updatesQueueChannels.put(updateChannelId, arrayList5);
                                                }
                                                arrayList5.add(tL_updates);
                                            } else if (arrayList == null) {
                                                arrayList = new ArrayList();
                                            } else if (!arrayList.contains(Integer.valueOf(updateChannelId))) {
                                                arrayList.add(Integer.valueOf(updateChannelId));
                                            }
                                        }
                                    } else if (processUpdateArray(tL_updates.updates, updates2.users, updates2.chats, false)) {
                                        messagesController.channelsPts.put(updateChannelId, tL_updates.pts);
                                        MessagesStorage.getInstance(messagesController.currentAccount).saveChannelPts(updateChannelId, tL_updates.pts);
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("need get channel diff inner TL_updates, channel_id = ");
                                            stringBuilder3.append(updateChannelId);
                                            FileLog.m0d(stringBuilder3.toString());
                                        }
                                        if (arrayList == null) {
                                            arrayList = new ArrayList();
                                        } else if (!arrayList.contains(Integer.valueOf(updateChannelId))) {
                                            arrayList.add(Integer.valueOf(updateChannelId));
                                        }
                                    }
                                }
                                updates2.updates.remove(0);
                                z4 = false;
                                z5 = true;
                                j = 0;
                            } else {
                                tL_updates = new TL_updates();
                                tL_updates.updates.add(update);
                                tL_updates.pts = getUpdatePts(update);
                                tL_updates.pts_count = getUpdatePtsCount(update);
                                while (z5 < updates2.updates.size()) {
                                    update2 = (Update) updates2.updates.get(z5);
                                    updatePts = getUpdatePts(update2);
                                    updatePtsCount = getUpdatePtsCount(update2);
                                    tL_updates.updates.add(update2);
                                    tL_updates.pts = updatePts;
                                    tL_updates.pts_count += updatePtsCount;
                                    updates2.updates.remove(z5);
                                }
                                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + tL_updates.pts_count != tL_updates.pts) {
                                    if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() == tL_updates.pts) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(update);
                                            stringBuilder2.append(" need get diff, pts: ");
                                            stringBuilder2.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                                            stringBuilder2.append(" ");
                                            stringBuilder2.append(tL_updates.pts);
                                            stringBuilder2.append(" count = ");
                                            stringBuilder2.append(tL_updates.pts_count);
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                        if (messagesController.updatesStartWaitTimePts == j) {
                                            messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("add to queue");
                                        }
                                        messagesController.updatesQueuePts.add(tL_updates);
                                    }
                                } else if (processUpdateArray(tL_updates.updates, updates2.users, updates2.chats, z4)) {
                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(tL_updates.pts);
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append("need get diff inner TL_updates, pts: ");
                                        stringBuilder2.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                                        stringBuilder2.append(" ");
                                        stringBuilder2.append(updates2.seq);
                                        FileLog.m0d(stringBuilder2.toString());
                                    }
                                    z6 = z5;
                                }
                                updates2.updates.remove(0);
                                z4 = false;
                                z5 = true;
                                j = 0;
                            }
                            z6 = true;
                            updates2.updates.remove(0);
                            z4 = false;
                            z5 = true;
                            j = 0;
                        }
                        if (!z9) {
                            if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() + 1 != updates2.seq_start) {
                                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue() == updates2.seq_start) {
                                }
                            }
                            obj4 = 1;
                            if (obj4 == null) {
                                processUpdateArray(updates2.updates, updates2.users, updates2.chats, false);
                                if (updates2.seq != 0) {
                                    if (updates2.date != 0) {
                                        MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(updates2.date);
                                    }
                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                                }
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                    if (z9) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("need get diff TL_updatesCombined, seq: ");
                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                        stringBuilder.append(" ");
                                        stringBuilder.append(updates2.seq_start);
                                        FileLog.m0d(stringBuilder.toString());
                                    } else {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("need get diff TL_updates, seq: ");
                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                        stringBuilder.append(" ");
                                        stringBuilder.append(updates2.seq);
                                        FileLog.m0d(stringBuilder.toString());
                                    }
                                }
                                if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) <= 1500) {
                                    if (messagesController.updatesStartWaitTimeSeq == 0) {
                                        messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("add TL_updates/Combined to queue");
                                    }
                                    messagesController.updatesQueueSeq.add(updates2);
                                } else {
                                    z4 = true;
                                }
                            }
                            z4 = z6;
                        } else if (updates2.seq == MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue()) {
                            obj4 = 1;
                            if (obj4 == null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    if (z9) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("need get diff TL_updates, seq: ");
                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                        stringBuilder.append(" ");
                                        stringBuilder.append(updates2.seq);
                                        FileLog.m0d(stringBuilder.toString());
                                    } else {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("need get diff TL_updatesCombined, seq: ");
                                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                        stringBuilder.append(" ");
                                        stringBuilder.append(updates2.seq_start);
                                        FileLog.m0d(stringBuilder.toString());
                                    }
                                }
                                if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) <= 1500) {
                                    z4 = true;
                                } else {
                                    if (messagesController.updatesStartWaitTimeSeq == 0) {
                                        messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("add TL_updates/Combined to queue");
                                    }
                                    messagesController.updatesQueueSeq.add(updates2);
                                }
                            } else {
                                processUpdateArray(updates2.updates, updates2.users, updates2.chats, false);
                                if (updates2.seq != 0) {
                                    if (updates2.date != 0) {
                                        MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(updates2.date);
                                    }
                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                                }
                            }
                            z4 = z6;
                        }
                        obj4 = null;
                        if (obj4 == null) {
                            if (BuildVars.LOGS_ENABLED) {
                                if (z9) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("need get diff TL_updates, seq: ");
                                    stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                    stringBuilder.append(" ");
                                    stringBuilder.append(updates2.seq);
                                    FileLog.m0d(stringBuilder.toString());
                                } else {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("need get diff TL_updatesCombined, seq: ");
                                    stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue());
                                    stringBuilder.append(" ");
                                    stringBuilder.append(updates2.seq_start);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                            }
                            if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimeSeq) <= 1500) {
                                z4 = true;
                            } else {
                                if (messagesController.updatesStartWaitTimeSeq == 0) {
                                    messagesController.updatesStartWaitTimeSeq = System.currentTimeMillis();
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("add TL_updates/Combined to queue");
                                }
                                messagesController.updatesQueueSeq.add(updates2);
                            }
                        } else {
                            processUpdateArray(updates2.updates, updates2.users, updates2.chats, false);
                            if (updates2.seq != 0) {
                                if (updates2.date != 0) {
                                    MessagesStorage.getInstance(messagesController.currentAccount).setLastDateValue(updates2.date);
                                }
                                MessagesStorage.getInstance(messagesController.currentAccount).setLastSeqValue(updates2.seq);
                            }
                        }
                        z4 = z6;
                    }
                    z2 = false;
                    SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
                    if (z) {
                        for (i = 0; i < messagesController.updatesQueueChannels.size(); i++) {
                            keyAt = messagesController.updatesQueueChannels.keyAt(i);
                            if (arrayList == null) {
                            }
                            processChannelsUpdatesQueue(keyAt, 0);
                        }
                        if (z4) {
                            getDifference();
                        } else {
                            for (i = 0; i < 3; i++) {
                                processUpdatesQueue(i, 0);
                            }
                        }
                    }
                    if (z3) {
                        tL_messages_receivedQueue = new TL_messages_receivedQueue();
                        tL_messages_receivedQueue.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                        ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_receivedQueue, /* anonymous class already generated */);
                    }
                    if (z2) {
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    }
                    MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
                }
            }
            i = z9 ? updates2.from_id : updates2.user_id;
            User user2 = getUser(Integer.valueOf(i));
            if (user2 == null || user2.min) {
                user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i);
                if (user2 != null && user2.min) {
                    user2 = null;
                }
                putUser(user2, true);
            }
            User user3;
            Chat chat3;
            if (updates2.fwd_from != null) {
                if (updates2.fwd_from.from_id != 0) {
                    User user4 = getUser(Integer.valueOf(updates2.fwd_from.from_id));
                    if (user4 == null) {
                        user4 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(updates2.fwd_from.from_id);
                        putUser(user4, true);
                    }
                    user3 = user4;
                    obj = 1;
                } else {
                    user3 = null;
                    obj = null;
                }
                if (updates2.fwd_from.channel_id != 0) {
                    Chat chat4 = getChat(Integer.valueOf(updates2.fwd_from.channel_id));
                    if (chat4 == null) {
                        chat4 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(updates2.fwd_from.channel_id);
                        putChat(chat4, true);
                    }
                    chat3 = chat4;
                    obj = 1;
                } else {
                    chat3 = null;
                }
            } else {
                user3 = null;
                chat3 = user3;
                obj = null;
            }
            User user5;
            if (updates2.via_bot_id != 0) {
                user = getUser(Integer.valueOf(updates2.via_bot_id));
                if (user == null) {
                    user = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(updates2.via_bot_id);
                    putUser(user, true);
                }
                user5 = user;
                obj2 = 1;
            } else {
                user5 = null;
                obj2 = null;
            }
            z6 = updates2 instanceof TL_updateShortMessage;
            if (z6) {
                if (!(user2 == null || (r5 != null && r6 == null && r7 == null))) {
                    if (obj2 != null && r9 == null) {
                    }
                }
                obj = 1;
                if (obj == null && !updates2.entities.isEmpty()) {
                    for (i2 = 0; i2 < updates2.entities.size(); i2++) {
                        messageEntity = (MessageEntity) updates2.entities.get(i2);
                        if (!(messageEntity instanceof TL_messageEntityMentionName)) {
                            i3 = ((TL_messageEntityMentionName) messageEntity).user_id;
                            user = getUser(Integer.valueOf(i3));
                            if (user != null || user.min) {
                                userSync = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i3);
                                if (userSync != null && userSync.min) {
                                    userSync = null;
                                }
                                if (userSync != null) {
                                    obj = 1;
                                    break;
                                }
                                putUser(user2, true);
                            }
                        }
                    }
                }
                if (user2 != null || user2.status == null || user2.status.expires > 0) {
                    z4 = false;
                } else {
                    messagesController.onlinePrivacy.put(Integer.valueOf(user2.id), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                    z4 = true;
                }
                if (obj != null) {
                    z7 = true;
                } else {
                    if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + updates2.pts_count != updates2.pts) {
                        tL_message = new TL_message();
                        tL_message.id = updates2.id;
                        i2 = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                        if (z6) {
                            if (updates2.out) {
                                tL_message.from_id = i2;
                            } else {
                                tL_message.from_id = i;
                            }
                            tL_message.to_id = new TL_peerUser();
                            tL_message.to_id.user_id = i;
                            tL_message.dialog_id = (long) i;
                        } else {
                            tL_message.from_id = i;
                            tL_message.to_id = new TL_peerChat();
                            tL_message.to_id.chat_id = updates2.chat_id;
                            tL_message.dialog_id = (long) (-updates2.chat_id);
                        }
                        tL_message.fwd_from = updates2.fwd_from;
                        tL_message.silent = updates2.silent;
                        tL_message.out = updates2.out;
                        tL_message.mentioned = updates2.mentioned;
                        tL_message.media_unread = updates2.media_unread;
                        tL_message.entities = updates2.entities;
                        tL_message.message = updates2.message;
                        tL_message.date = updates2.date;
                        tL_message.via_bot_id = updates2.via_bot_id;
                        tL_message.flags = updates2.flags | 256;
                        tL_message.reply_to_msg_id = updates2.reply_to_msg_id;
                        tL_message.media = new TL_messageMediaEmpty();
                        concurrentHashMap = tL_message.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                        num = (Integer) concurrentHashMap.get(Long.valueOf(tL_message.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(tL_message.out, tL_message.dialog_id));
                            concurrentHashMap.put(Long.valueOf(tL_message.dialog_id), num);
                        }
                        tL_message.unread = num.intValue() < tL_message.id;
                        if (tL_message.dialog_id == ((long) i2)) {
                            tL_message.unread = false;
                            tL_message.media_unread = false;
                            z8 = true;
                            tL_message.out = true;
                        } else {
                            z8 = true;
                        }
                        MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(updates2.pts);
                        messageObject = new MessageObject(messagesController.currentAccount, tL_message, messagesController.createdDialogIds.contains(Long.valueOf(tL_message.dialog_id)));
                        arrayList3 = new ArrayList();
                        arrayList3.add(messageObject);
                        arrayList4 = new ArrayList();
                        arrayList4.add(tL_message);
                        if (z6) {
                            if (updates2.out || !updatePrintingUsersWithNewMessages((long) updates2.user_id, arrayList3)) {
                                z8 = false;
                            }
                            if (z8) {
                                updatePrintingStrings();
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (z8) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                                    }
                                    MessagesController.this.updateInterfaceWithMessages((long) i, arrayList3);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                }
                            });
                        } else {
                            z9 = updatePrintingUsersWithNewMessages((long) (-updates2.chat_id), arrayList3);
                            if (z9) {
                                updatePrintingStrings();
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (z9) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                                    }
                                    MessagesController.this.updateInterfaceWithMessages((long) (-updates2.chat_id), arrayList3);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                }
                            });
                        }
                        if (!messageObject.isOut()) {
                            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.MessagesController$124$1 */
                                class C03081 implements Runnable {
                                    C03081() {
                                    }

                                    public void run() {
                                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList3, true, false);
                                    }
                                }

                                public void run() {
                                    AndroidUtilities.runOnUIThread(new C03081());
                                }
                            });
                        }
                        MessagesStorage.getInstance(messagesController.currentAccount).putMessages(arrayList4, false, true, false, 0);
                    } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != updates2.pts) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("need get diff short message, pts: ");
                            stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                            stringBuilder.append(" ");
                            stringBuilder.append(updates2.pts);
                            stringBuilder.append(" count = ");
                            stringBuilder.append(updates2.pts_count);
                            FileLog.m0d(stringBuilder.toString());
                        }
                        if (!(messagesController.gettingDifference || messagesController.updatesStartWaitTimePts == 0)) {
                            if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) > 1500) {
                                z7 = true;
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
                    z7 = false;
                }
                z2 = z4;
                z4 = z7;
                z3 = false;
                SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
                if (z) {
                    for (i = 0; i < messagesController.updatesQueueChannels.size(); i++) {
                        keyAt = messagesController.updatesQueueChannels.keyAt(i);
                        if (arrayList == null) {
                        }
                        processChannelsUpdatesQueue(keyAt, 0);
                    }
                    if (z4) {
                        for (i = 0; i < 3; i++) {
                            processUpdatesQueue(i, 0);
                        }
                    } else {
                        getDifference();
                    }
                }
                if (z3) {
                    tL_messages_receivedQueue = new TL_messages_receivedQueue();
                    tL_messages_receivedQueue.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                    ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_receivedQueue, /* anonymous class already generated */);
                }
                if (z2) {
                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                }
                MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
            }
            Chat chat5 = getChat(Integer.valueOf(updates2.chat_id));
            if (chat5 == null) {
                chat5 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(updates2.chat_id);
                putChat(chat5, true);
            }
            if (!(chat5 == null || user2 == null || (r5 != null && r6 == null && r7 == null))) {
                if (obj2 != null && r9 == null) {
                }
            }
            obj = 1;
            for (i2 = 0; i2 < updates2.entities.size(); i2++) {
                messageEntity = (MessageEntity) updates2.entities.get(i2);
                if (!(messageEntity instanceof TL_messageEntityMentionName)) {
                    i3 = ((TL_messageEntityMentionName) messageEntity).user_id;
                    user = getUser(Integer.valueOf(i3));
                    if (user != null) {
                    }
                    userSync = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i3);
                    userSync = null;
                    if (userSync != null) {
                        obj = 1;
                        break;
                    }
                    putUser(user2, true);
                }
            }
            if (user2 != null) {
            }
            z4 = false;
            if (obj != null) {
                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + updates2.pts_count != updates2.pts) {
                    tL_message = new TL_message();
                    tL_message.id = updates2.id;
                    i2 = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                    if (z6) {
                        tL_message.from_id = i;
                        tL_message.to_id = new TL_peerChat();
                        tL_message.to_id.chat_id = updates2.chat_id;
                        tL_message.dialog_id = (long) (-updates2.chat_id);
                    } else {
                        if (updates2.out) {
                            tL_message.from_id = i;
                        } else {
                            tL_message.from_id = i2;
                        }
                        tL_message.to_id = new TL_peerUser();
                        tL_message.to_id.user_id = i;
                        tL_message.dialog_id = (long) i;
                    }
                    tL_message.fwd_from = updates2.fwd_from;
                    tL_message.silent = updates2.silent;
                    tL_message.out = updates2.out;
                    tL_message.mentioned = updates2.mentioned;
                    tL_message.media_unread = updates2.media_unread;
                    tL_message.entities = updates2.entities;
                    tL_message.message = updates2.message;
                    tL_message.date = updates2.date;
                    tL_message.via_bot_id = updates2.via_bot_id;
                    tL_message.flags = updates2.flags | 256;
                    tL_message.reply_to_msg_id = updates2.reply_to_msg_id;
                    tL_message.media = new TL_messageMediaEmpty();
                    if (tL_message.out) {
                    }
                    num = (Integer) concurrentHashMap.get(Long.valueOf(tL_message.dialog_id));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(tL_message.out, tL_message.dialog_id));
                        concurrentHashMap.put(Long.valueOf(tL_message.dialog_id), num);
                    }
                    if (num.intValue() < tL_message.id) {
                    }
                    tL_message.unread = num.intValue() < tL_message.id;
                    if (tL_message.dialog_id == ((long) i2)) {
                        z8 = true;
                    } else {
                        tL_message.unread = false;
                        tL_message.media_unread = false;
                        z8 = true;
                        tL_message.out = true;
                    }
                    MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(updates2.pts);
                    messageObject = new MessageObject(messagesController.currentAccount, tL_message, messagesController.createdDialogIds.contains(Long.valueOf(tL_message.dialog_id)));
                    arrayList3 = new ArrayList();
                    arrayList3.add(messageObject);
                    arrayList4 = new ArrayList();
                    arrayList4.add(tL_message);
                    if (z6) {
                        z9 = updatePrintingUsersWithNewMessages((long) (-updates2.chat_id), arrayList3);
                        if (z9) {
                            updatePrintingStrings();
                        }
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    } else {
                        if (!updates2.out) {
                        }
                        z8 = false;
                        if (z8) {
                            updatePrintingStrings();
                        }
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    }
                    if (messageObject.isOut()) {
                        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                    }
                    MessagesStorage.getInstance(messagesController.currentAccount).putMessages(arrayList4, false, true, false, 0);
                } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != updates2.pts) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("need get diff short message, pts: ");
                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                        stringBuilder.append(" ");
                        stringBuilder.append(updates2.pts);
                        stringBuilder.append(" count = ");
                        stringBuilder.append(updates2.pts_count);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) > 1500) {
                        z7 = true;
                    } else {
                        if (messagesController.updatesStartWaitTimePts == 0) {
                            messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("add to queue");
                        }
                        messagesController.updatesQueuePts.add(updates2);
                    }
                }
                z7 = false;
            } else {
                z7 = true;
            }
            z2 = z4;
            z4 = z7;
            z3 = false;
            SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
            if (z) {
                for (i = 0; i < messagesController.updatesQueueChannels.size(); i++) {
                    keyAt = messagesController.updatesQueueChannels.keyAt(i);
                    if (arrayList == null) {
                    }
                    processChannelsUpdatesQueue(keyAt, 0);
                }
                if (z4) {
                    getDifference();
                } else {
                    for (i = 0; i < 3; i++) {
                        processUpdatesQueue(i, 0);
                    }
                }
            }
            if (z3) {
                tL_messages_receivedQueue = new TL_messages_receivedQueue();
                tL_messages_receivedQueue.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_receivedQueue, /* anonymous class already generated */);
            }
            if (z2) {
                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
            }
            MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
            obj = null;
            for (i2 = 0; i2 < updates2.entities.size(); i2++) {
                messageEntity = (MessageEntity) updates2.entities.get(i2);
                if (!(messageEntity instanceof TL_messageEntityMentionName)) {
                    i3 = ((TL_messageEntityMentionName) messageEntity).user_id;
                    user = getUser(Integer.valueOf(i3));
                    if (user != null) {
                    }
                    userSync = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i3);
                    userSync = null;
                    if (userSync != null) {
                        obj = 1;
                        break;
                    }
                    putUser(user2, true);
                }
            }
            if (user2 != null) {
            }
            z4 = false;
            if (obj != null) {
                z7 = true;
            } else {
                if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() + updates2.pts_count != updates2.pts) {
                    tL_message = new TL_message();
                    tL_message.id = updates2.id;
                    i2 = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                    if (z6) {
                        if (updates2.out) {
                            tL_message.from_id = i2;
                        } else {
                            tL_message.from_id = i;
                        }
                        tL_message.to_id = new TL_peerUser();
                        tL_message.to_id.user_id = i;
                        tL_message.dialog_id = (long) i;
                    } else {
                        tL_message.from_id = i;
                        tL_message.to_id = new TL_peerChat();
                        tL_message.to_id.chat_id = updates2.chat_id;
                        tL_message.dialog_id = (long) (-updates2.chat_id);
                    }
                    tL_message.fwd_from = updates2.fwd_from;
                    tL_message.silent = updates2.silent;
                    tL_message.out = updates2.out;
                    tL_message.mentioned = updates2.mentioned;
                    tL_message.media_unread = updates2.media_unread;
                    tL_message.entities = updates2.entities;
                    tL_message.message = updates2.message;
                    tL_message.date = updates2.date;
                    tL_message.via_bot_id = updates2.via_bot_id;
                    tL_message.flags = updates2.flags | 256;
                    tL_message.reply_to_msg_id = updates2.reply_to_msg_id;
                    tL_message.media = new TL_messageMediaEmpty();
                    if (tL_message.out) {
                    }
                    num = (Integer) concurrentHashMap.get(Long.valueOf(tL_message.dialog_id));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(tL_message.out, tL_message.dialog_id));
                        concurrentHashMap.put(Long.valueOf(tL_message.dialog_id), num);
                    }
                    if (num.intValue() < tL_message.id) {
                    }
                    tL_message.unread = num.intValue() < tL_message.id;
                    if (tL_message.dialog_id == ((long) i2)) {
                        tL_message.unread = false;
                        tL_message.media_unread = false;
                        z8 = true;
                        tL_message.out = true;
                    } else {
                        z8 = true;
                    }
                    MessagesStorage.getInstance(messagesController.currentAccount).setLastPtsValue(updates2.pts);
                    messageObject = new MessageObject(messagesController.currentAccount, tL_message, messagesController.createdDialogIds.contains(Long.valueOf(tL_message.dialog_id)));
                    arrayList3 = new ArrayList();
                    arrayList3.add(messageObject);
                    arrayList4 = new ArrayList();
                    arrayList4.add(tL_message);
                    if (z6) {
                        if (updates2.out) {
                        }
                        z8 = false;
                        if (z8) {
                            updatePrintingStrings();
                        }
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    } else {
                        z9 = updatePrintingUsersWithNewMessages((long) (-updates2.chat_id), arrayList3);
                        if (z9) {
                            updatePrintingStrings();
                        }
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    }
                    if (messageObject.isOut()) {
                        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                    }
                    MessagesStorage.getInstance(messagesController.currentAccount).putMessages(arrayList4, false, true, false, 0);
                } else if (MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue() != updates2.pts) {
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("need get diff short message, pts: ");
                        stringBuilder.append(MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue());
                        stringBuilder.append(" ");
                        stringBuilder.append(updates2.pts);
                        stringBuilder.append(" count = ");
                        stringBuilder.append(updates2.pts_count);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (Math.abs(System.currentTimeMillis() - messagesController.updatesStartWaitTimePts) > 1500) {
                        if (messagesController.updatesStartWaitTimePts == 0) {
                            messagesController.updatesStartWaitTimePts = System.currentTimeMillis();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("add to queue");
                        }
                        messagesController.updatesQueuePts.add(updates2);
                    } else {
                        z7 = true;
                    }
                }
                z7 = false;
            }
            z2 = z4;
            z4 = z7;
            z3 = false;
            SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
            if (z) {
                for (i = 0; i < messagesController.updatesQueueChannels.size(); i++) {
                    keyAt = messagesController.updatesQueueChannels.keyAt(i);
                    if (arrayList == null) {
                    }
                    processChannelsUpdatesQueue(keyAt, 0);
                }
                if (z4) {
                    for (i = 0; i < 3; i++) {
                        processUpdatesQueue(i, 0);
                    }
                } else {
                    getDifference();
                }
            }
            if (z3) {
                tL_messages_receivedQueue = new TL_messages_receivedQueue();
                tL_messages_receivedQueue.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
                ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_receivedQueue, /* anonymous class already generated */);
            }
            if (z2) {
                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
            }
            MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
        }
        z2 = false;
        z3 = z2;
        SecretChatHelper.getInstance(messagesController.currentAccount).processPendingEncMessages();
        if (z) {
            for (i = 0; i < messagesController.updatesQueueChannels.size(); i++) {
                keyAt = messagesController.updatesQueueChannels.keyAt(i);
                if (arrayList == null) {
                }
                processChannelsUpdatesQueue(keyAt, 0);
            }
            if (z4) {
                getDifference();
            } else {
                for (i = 0; i < 3; i++) {
                    processUpdatesQueue(i, 0);
                }
            }
        }
        if (z3) {
            tL_messages_receivedQueue = new TL_messages_receivedQueue();
            tL_messages_receivedQueue.max_qts = MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue();
            ConnectionsManager.getInstance(messagesController.currentAccount).sendRequest(tL_messages_receivedQueue, /* anonymous class already generated */);
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
        }
        MessagesStorage.getInstance(messagesController.currentAccount).saveDiffParams(MessagesStorage.getInstance(messagesController.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastDateValue(), MessagesStorage.getInstance(messagesController.currentAccount).getLastQtsValue());
    }

    public boolean processUpdateArray(ArrayList<Update> arrayList, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, boolean z) {
        MessagesController messagesController = this;
        final ArrayList<User> arrayList4 = arrayList2;
        final ArrayList<Chat> arrayList5 = arrayList3;
        if (arrayList.isEmpty()) {
            if (!(arrayList4 == null && arrayList5 == null)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MessagesController.this.putUsers(arrayList4, false);
                        MessagesController.this.putChats(arrayList5, false);
                    }
                });
            }
            return true;
        }
        AbstractMap concurrentHashMap;
        int i;
        boolean z2;
        int size;
        boolean z3;
        AbstractMap abstractMap;
        ArrayList arrayList6;
        ArrayList arrayList7;
        SparseIntArray sparseIntArray;
        ArrayList arrayList8;
        ArrayList arrayList9;
        int i2;
        SparseIntArray sparseIntArray2;
        SparseArray sparseArray;
        SparseLongArray sparseLongArray;
        LongSparseArray longSparseArray;
        LongSparseArray longSparseArray2;
        int size2;
        LongSparseArray longSparseArray3;
        SparseIntArray sparseIntArray3;
        SparseLongArray sparseLongArray2;
        ArrayList arrayList10;
        SparseArray sparseArray2;
        boolean z4;
        long currentTimeMillis = System.currentTimeMillis();
        if (arrayList4 != null) {
            concurrentHashMap = new ConcurrentHashMap();
            int size3 = arrayList2.size();
            for (i = 0; i < size3; i++) {
                User user = (User) arrayList4.get(i);
                concurrentHashMap.put(Integer.valueOf(user.id), user);
            }
            z2 = true;
        } else {
            concurrentHashMap = messagesController.users;
            z2 = false;
        }
        if (arrayList5 != null) {
            AbstractMap concurrentHashMap2 = new ConcurrentHashMap();
            size = arrayList3.size();
            for (int i3 = 0; i3 < size; i3++) {
                Chat chat = (Chat) arrayList5.get(i3);
                concurrentHashMap2.put(Integer.valueOf(chat.id), chat);
            }
            AbstractMap abstractMap2 = concurrentHashMap2;
            z3 = z2;
            abstractMap = abstractMap2;
        } else {
            abstractMap = messagesController.chats;
            z3 = false;
        }
        if (z) {
            z3 = false;
        }
        if (!(arrayList4 == null && arrayList5 == null)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.this.putUsers(arrayList4, false);
                    MessagesController.this.putChats(arrayList5, false);
                }
            });
        }
        int size4 = arrayList.size();
        boolean z5 = z3;
        ArrayList arrayList11 = null;
        SparseArray sparseArray3 = null;
        SparseIntArray sparseIntArray4 = null;
        size = 0;
        LongSparseArray longSparseArray4 = null;
        SparseLongArray sparseLongArray3 = null;
        ArrayList arrayList12 = null;
        SparseLongArray sparseLongArray4 = null;
        ArrayList arrayList13 = null;
        int i4 = 0;
        ArrayList arrayList14 = null;
        LongSparseArray longSparseArray5 = null;
        boolean z6 = false;
        ArrayList arrayList15 = null;
        ArrayList arrayList16 = null;
        SparseArray sparseArray4 = null;
        LongSparseArray longSparseArray6 = null;
        SparseIntArray sparseIntArray5 = null;
        ArrayList arrayList17 = null;
        while (size < size4) {
            ArrayList arrayList18;
            int i5;
            long j;
            int size5;
            int newMessageId;
            ArrayList arrayList19;
            MessageObject messageObject;
            ArrayList arrayList20;
            int size6;
            long j2;
            Message message;
            ArrayList arrayList21;
            Chat chat2;
            StringBuilder stringBuilder;
            Object obj;
            User user2;
            boolean z7;
            User user3;
            ConcurrentHashMap concurrentHashMap3;
            Integer num;
            int i6 = size4;
            Update update = (Update) arrayList.get(size);
            if (BuildVars.LOGS_ENABLED) {
                arrayList18 = arrayList13;
                StringBuilder stringBuilder2 = new StringBuilder();
                i5 = size;
                stringBuilder2.append("process update ");
                stringBuilder2.append(update);
                FileLog.m0d(stringBuilder2.toString());
            } else {
                i5 = size;
                arrayList18 = arrayList13;
            }
            boolean z8 = update instanceof TL_updateNewMessage;
            if (!z8) {
                if (!(update instanceof TL_updateNewChannelMessage)) {
                    int i7;
                    if (update instanceof TL_updateReadMessagesContents) {
                        TL_updateReadMessagesContents tL_updateReadMessagesContents = (TL_updateReadMessagesContents) update;
                        if (arrayList12 == null) {
                            arrayList12 = new ArrayList();
                        }
                        size = tL_updateReadMessagesContents.messages.size();
                        i7 = 0;
                        while (i7 < size) {
                            int i8 = size;
                            TL_updateReadMessagesContents tL_updateReadMessagesContents2 = tL_updateReadMessagesContents;
                            arrayList6 = arrayList11;
                            arrayList12.add(Long.valueOf((long) ((Integer) tL_updateReadMessagesContents.messages.get(i7)).intValue()));
                            i7++;
                            size = i8;
                            arrayList11 = arrayList6;
                            tL_updateReadMessagesContents = tL_updateReadMessagesContents2;
                        }
                        arrayList6 = arrayList11;
                        j = currentTimeMillis;
                        arrayList7 = arrayList12;
                        arrayList13 = arrayList18;
                    } else {
                        arrayList6 = arrayList11;
                        long j3;
                        if (update instanceof TL_updateChannelReadMessagesContents) {
                            TL_updateChannelReadMessagesContents tL_updateChannelReadMessagesContents = (TL_updateChannelReadMessagesContents) update;
                            if (arrayList12 == null) {
                                arrayList12 = new ArrayList();
                            }
                            size5 = tL_updateChannelReadMessagesContents.messages.size();
                            i7 = 0;
                            while (i7 < size5) {
                                sparseIntArray = sparseIntArray4;
                                int i9 = size5;
                                TL_updateChannelReadMessagesContents tL_updateChannelReadMessagesContents2 = tL_updateChannelReadMessagesContents;
                                j3 = currentTimeMillis;
                                arrayList12.add(Long.valueOf(((long) ((Integer) tL_updateChannelReadMessagesContents.messages.get(i7)).intValue()) | (((long) tL_updateChannelReadMessagesContents.channel_id) << 32)));
                                i7++;
                                sparseIntArray4 = sparseIntArray;
                                size5 = i9;
                                tL_updateChannelReadMessagesContents = tL_updateChannelReadMessagesContents2;
                                currentTimeMillis = j3;
                            }
                            sparseIntArray = sparseIntArray4;
                            j = currentTimeMillis;
                            arrayList7 = arrayList12;
                            arrayList13 = arrayList18;
                            arrayList11 = arrayList6;
                        } else {
                            j3 = currentTimeMillis;
                            sparseIntArray = sparseIntArray4;
                            Integer num2;
                            if (update instanceof TL_updateReadHistoryInbox) {
                                TL_updateReadHistoryInbox tL_updateReadHistoryInbox = (TL_updateReadHistoryInbox) update;
                                if (sparseLongArray4 == null) {
                                    sparseLongArray4 = new SparseLongArray();
                                }
                                if (tL_updateReadHistoryInbox.peer.chat_id != 0) {
                                    sparseLongArray4.put(-tL_updateReadHistoryInbox.peer.chat_id, (long) tL_updateReadHistoryInbox.max_id);
                                    currentTimeMillis = (long) (-tL_updateReadHistoryInbox.peer.chat_id);
                                } else {
                                    sparseLongArray4.put(tL_updateReadHistoryInbox.peer.user_id, (long) tL_updateReadHistoryInbox.max_id);
                                    currentTimeMillis = (long) tL_updateReadHistoryInbox.peer.user_id;
                                }
                                num2 = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(currentTimeMillis));
                                if (num2 == null) {
                                    num2 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(false, currentTimeMillis));
                                }
                                messagesController.dialogs_read_inbox_max.put(Long.valueOf(currentTimeMillis), Integer.valueOf(Math.max(num2.intValue(), tL_updateReadHistoryInbox.max_id)));
                            } else if (update instanceof TL_updateReadHistoryOutbox) {
                                TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox = (TL_updateReadHistoryOutbox) update;
                                if (sparseLongArray3 == null) {
                                    sparseLongArray3 = new SparseLongArray();
                                }
                                if (tL_updateReadHistoryOutbox.peer.chat_id != 0) {
                                    sparseLongArray3.put(-tL_updateReadHistoryOutbox.peer.chat_id, (long) tL_updateReadHistoryOutbox.max_id);
                                    currentTimeMillis = (long) (-tL_updateReadHistoryOutbox.peer.chat_id);
                                } else {
                                    sparseLongArray3.put(tL_updateReadHistoryOutbox.peer.user_id, (long) tL_updateReadHistoryOutbox.max_id);
                                    currentTimeMillis = (long) tL_updateReadHistoryOutbox.peer.user_id;
                                }
                                num2 = (Integer) messagesController.dialogs_read_outbox_max.get(Long.valueOf(currentTimeMillis));
                                if (num2 == null) {
                                    num2 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(true, currentTimeMillis));
                                }
                                messagesController.dialogs_read_outbox_max.put(Long.valueOf(currentTimeMillis), Integer.valueOf(Math.max(num2.intValue(), tL_updateReadHistoryOutbox.max_id)));
                            } else if (update instanceof TL_updateDeleteMessages) {
                                TL_updateDeleteMessages tL_updateDeleteMessages = (TL_updateDeleteMessages) update;
                                if (sparseArray3 == null) {
                                    sparseArray3 = new SparseArray();
                                }
                                arrayList8 = (ArrayList) sparseArray3.get(0);
                                if (arrayList8 == null) {
                                    arrayList8 = new ArrayList();
                                    sparseArray3.put(0, arrayList8);
                                }
                                arrayList8.addAll(tL_updateDeleteMessages.messages);
                            } else {
                                long j4;
                                ArrayList arrayList22;
                                SendMessageAction sendMessageAction;
                                boolean z9 = update instanceof TL_updateUserTyping;
                                if (!z9) {
                                    if (!(update instanceof TL_updateChatUserTyping)) {
                                        if (update instanceof TL_updateChatParticipants) {
                                            TL_updateChatParticipants tL_updateChatParticipants = (TL_updateChatParticipants) update;
                                            i4 |= 32;
                                            arrayList11 = arrayList15 == null ? new ArrayList() : arrayList15;
                                            arrayList11.add(tL_updateChatParticipants.participants);
                                            arrayList15 = arrayList11;
                                        } else {
                                            if (update instanceof TL_updateUserStatus) {
                                                i4 |= 4;
                                                arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                arrayList11.add(update);
                                            } else if (update instanceof TL_updateUserName) {
                                                i4 |= 1;
                                                arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                arrayList11.add(update);
                                            } else if (update instanceof TL_updateUserPhoto) {
                                                i4 |= 2;
                                                MessagesStorage.getInstance(messagesController.currentAccount).clearUserPhotos(((TL_updateUserPhoto) update).user_id);
                                                arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                arrayList11.add(update);
                                            } else if (update instanceof TL_updateUserPhone) {
                                                i4 |= 1024;
                                                arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                arrayList11.add(update);
                                            } else {
                                                Message tL_messageService;
                                                if (update instanceof TL_updateContactRegistered) {
                                                    ArrayList arrayList23;
                                                    TL_updateContactRegistered tL_updateContactRegistered = (TL_updateContactRegistered) update;
                                                    if (messagesController.enableJoined && concurrentHashMap.containsKey(Integer.valueOf(tL_updateContactRegistered.user_id)) && !MessagesStorage.getInstance(messagesController.currentAccount).isDialogHasMessages((long) tL_updateContactRegistered.user_id)) {
                                                        tL_messageService = new TL_messageService();
                                                        tL_messageService.action = new TL_messageActionUserJoined();
                                                        newMessageId = UserConfig.getInstance(messagesController.currentAccount).getNewMessageId();
                                                        tL_messageService.id = newMessageId;
                                                        tL_messageService.local_id = newMessageId;
                                                        UserConfig.getInstance(messagesController.currentAccount).saveConfig(false);
                                                        tL_messageService.unread = false;
                                                        tL_messageService.flags = 256;
                                                        tL_messageService.date = tL_updateContactRegistered.date;
                                                        tL_messageService.from_id = tL_updateContactRegistered.user_id;
                                                        tL_messageService.to_id = new TL_peerUser();
                                                        tL_messageService.to_id.user_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                                                        tL_messageService.dialog_id = (long) tL_updateContactRegistered.user_id;
                                                        arrayList9 = arrayList14;
                                                        arrayList19 = arrayList9 == null ? new ArrayList() : arrayList9;
                                                        arrayList19.add(tL_messageService);
                                                        arrayList23 = arrayList18;
                                                        i2 = i4;
                                                        messageObject = new MessageObject(messagesController.currentAccount, tL_messageService, concurrentHashMap, abstractMap, messagesController.createdDialogIds.contains(Long.valueOf(tL_messageService.dialog_id)));
                                                        if (longSparseArray4 == null) {
                                                            longSparseArray4 = new LongSparseArray();
                                                        }
                                                        arrayList20 = (ArrayList) longSparseArray4.get(tL_messageService.dialog_id);
                                                        if (arrayList20 == null) {
                                                            arrayList20 = new ArrayList();
                                                            longSparseArray4.put(tL_messageService.dialog_id, arrayList20);
                                                        }
                                                        arrayList20.add(messageObject);
                                                    } else {
                                                        arrayList9 = arrayList14;
                                                        arrayList23 = arrayList18;
                                                        i2 = i4;
                                                        arrayList19 = arrayList9;
                                                    }
                                                    arrayList13 = arrayList23;
                                                    arrayList7 = arrayList12;
                                                    i4 = i2;
                                                    arrayList11 = arrayList6;
                                                    sparseIntArray4 = sparseIntArray;
                                                    j = j3;
                                                } else {
                                                    arrayList9 = arrayList14;
                                                    arrayList11 = arrayList18;
                                                    i2 = i4;
                                                    if (update instanceof TL_updateContactLink) {
                                                        TL_updateContactLink tL_updateContactLink = (TL_updateContactLink) update;
                                                        arrayList13 = arrayList11 == null ? new ArrayList() : arrayList11;
                                                        if (tL_updateContactLink.my_link instanceof TL_contactLinkContact) {
                                                            size5 = arrayList13.indexOf(Integer.valueOf(-tL_updateContactLink.user_id));
                                                            if (size5 != -1) {
                                                                arrayList13.remove(size5);
                                                            }
                                                            if (!arrayList13.contains(Integer.valueOf(tL_updateContactLink.user_id))) {
                                                                arrayList13.add(Integer.valueOf(tL_updateContactLink.user_id));
                                                            }
                                                        } else {
                                                            size5 = arrayList13.indexOf(Integer.valueOf(tL_updateContactLink.user_id));
                                                            if (size5 != -1) {
                                                                arrayList13.remove(size5);
                                                            }
                                                            if (!arrayList13.contains(Integer.valueOf(tL_updateContactLink.user_id))) {
                                                                arrayList13.add(Integer.valueOf(-tL_updateContactLink.user_id));
                                                            }
                                                        }
                                                        arrayList7 = arrayList12;
                                                        i4 = i2;
                                                        arrayList11 = arrayList6;
                                                        sparseIntArray4 = sparseIntArray;
                                                        j = j3;
                                                    } else {
                                                        ArrayList arrayList24;
                                                        if (update instanceof TL_updateNewEncryptedMessage) {
                                                            TL_updateNewEncryptedMessage tL_updateNewEncryptedMessage = (TL_updateNewEncryptedMessage) update;
                                                            arrayList8 = SecretChatHelper.getInstance(messagesController.currentAccount).decryptMessage(tL_updateNewEncryptedMessage.message);
                                                            if (arrayList8 == null || arrayList8.isEmpty()) {
                                                                arrayList24 = arrayList11;
                                                            } else {
                                                                LongSparseArray longSparseArray7;
                                                                j4 = ((long) tL_updateNewEncryptedMessage.message.chat_id) << 32;
                                                                if (longSparseArray4 == null) {
                                                                    longSparseArray4 = new LongSparseArray();
                                                                }
                                                                arrayList19 = (ArrayList) longSparseArray4.get(j4);
                                                                if (arrayList19 == null) {
                                                                    arrayList19 = new ArrayList();
                                                                    longSparseArray4.put(j4, arrayList19);
                                                                }
                                                                size6 = arrayList8.size();
                                                                i = 0;
                                                                while (i < size6) {
                                                                    arrayList24 = arrayList11;
                                                                    tL_messageService = (Message) arrayList8.get(i);
                                                                    ImageLoader.saveMessageThumbs(tL_messageService);
                                                                    if (arrayList9 == null) {
                                                                        arrayList9 = new ArrayList();
                                                                    }
                                                                    arrayList9.add(tL_messageService);
                                                                    ArrayList arrayList25 = arrayList8;
                                                                    int i10 = size6;
                                                                    ArrayList arrayList26 = arrayList9;
                                                                    longSparseArray7 = longSparseArray4;
                                                                    long j5 = j4;
                                                                    messageObject = new MessageObject(messagesController.currentAccount, tL_messageService, concurrentHashMap, abstractMap, messagesController.createdDialogIds.contains(Long.valueOf(j4)));
                                                                    arrayList19.add(messageObject);
                                                                    arrayList11 = arrayList17 == null ? new ArrayList() : arrayList17;
                                                                    arrayList11.add(messageObject);
                                                                    i++;
                                                                    arrayList17 = arrayList11;
                                                                    j4 = j5;
                                                                    arrayList11 = arrayList24;
                                                                    arrayList8 = arrayList25;
                                                                    size6 = i10;
                                                                    arrayList9 = arrayList26;
                                                                    longSparseArray4 = longSparseArray7;
                                                                }
                                                                arrayList24 = arrayList11;
                                                                longSparseArray7 = longSparseArray4;
                                                            }
                                                            arrayList7 = arrayList12;
                                                            i4 = i2;
                                                            arrayList11 = arrayList6;
                                                            sparseIntArray4 = sparseIntArray;
                                                            j = j3;
                                                        } else {
                                                            arrayList24 = arrayList11;
                                                            if (update instanceof TL_updateEncryptedChatTyping) {
                                                                TL_updateEncryptedChatTyping tL_updateEncryptedChatTyping = (TL_updateEncryptedChatTyping) update;
                                                                EncryptedChat encryptedChatDB = getEncryptedChatDB(tL_updateEncryptedChatTyping.chat_id, true);
                                                                if (encryptedChatDB != null) {
                                                                    Object obj2;
                                                                    currentTimeMillis = ((long) tL_updateEncryptedChatTyping.chat_id) << 32;
                                                                    arrayList19 = (ArrayList) messagesController.printingUsers.get(Long.valueOf(currentTimeMillis));
                                                                    if (arrayList19 == null) {
                                                                        arrayList19 = new ArrayList();
                                                                        messagesController.printingUsers.put(Long.valueOf(currentTimeMillis), arrayList19);
                                                                    }
                                                                    newMessageId = arrayList19.size();
                                                                    for (size6 = 0; size6 < newMessageId; size6++) {
                                                                        PrintingUser printingUser = (PrintingUser) arrayList19.get(size6);
                                                                        if (printingUser.userId == encryptedChatDB.user_id) {
                                                                            j4 = j3;
                                                                            printingUser.lastTime = j4;
                                                                            printingUser.action = new TL_sendMessageTypingAction();
                                                                            obj2 = 1;
                                                                            break;
                                                                        }
                                                                        j4 = j3;
                                                                    }
                                                                    j4 = j3;
                                                                    obj2 = null;
                                                                    if (obj2 == null) {
                                                                        PrintingUser printingUser2 = new PrintingUser();
                                                                        printingUser2.userId = encryptedChatDB.user_id;
                                                                        printingUser2.lastTime = j4;
                                                                        printingUser2.action = new TL_sendMessageTypingAction();
                                                                        arrayList19.add(printingUser2);
                                                                        z6 = true;
                                                                    }
                                                                    messagesController.onlinePrivacy.put(Integer.valueOf(encryptedChatDB.user_id), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                                                } else {
                                                                    j4 = j3;
                                                                }
                                                            } else {
                                                                j4 = j3;
                                                                if (update instanceof TL_updateEncryptedMessagesRead) {
                                                                    TL_updateEncryptedMessagesRead tL_updateEncryptedMessagesRead = (TL_updateEncryptedMessagesRead) update;
                                                                    sparseIntArray4 = sparseIntArray == null ? new SparseIntArray() : sparseIntArray;
                                                                    sparseIntArray4.put(tL_updateEncryptedMessagesRead.chat_id, Math.max(tL_updateEncryptedMessagesRead.max_date, tL_updateEncryptedMessagesRead.date));
                                                                    arrayList11 = arrayList6 == null ? new ArrayList() : arrayList6;
                                                                    arrayList11.add(tL_updateEncryptedMessagesRead);
                                                                    arrayList7 = arrayList12;
                                                                    j = j4;
                                                                    i4 = i2;
                                                                } else {
                                                                    if (update instanceof TL_updateChatParticipantAdd) {
                                                                        TL_updateChatParticipantAdd tL_updateChatParticipantAdd = (TL_updateChatParticipantAdd) update;
                                                                        MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(tL_updateChatParticipantAdd.chat_id, tL_updateChatParticipantAdd.user_id, 0, tL_updateChatParticipantAdd.inviter_id, tL_updateChatParticipantAdd.version);
                                                                    } else if (update instanceof TL_updateChatParticipantDelete) {
                                                                        TL_updateChatParticipantDelete tL_updateChatParticipantDelete = (TL_updateChatParticipantDelete) update;
                                                                        MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(tL_updateChatParticipantDelete.chat_id, tL_updateChatParticipantDelete.user_id, 1, 0, tL_updateChatParticipantDelete.version);
                                                                    } else {
                                                                        if (!(update instanceof TL_updateDcOptions)) {
                                                                            if (!(update instanceof TL_updateConfig)) {
                                                                                if (update instanceof TL_updateEncryption) {
                                                                                    SecretChatHelper.getInstance(messagesController.currentAccount).processUpdateEncryption((TL_updateEncryption) update, concurrentHashMap);
                                                                                } else if (update instanceof TL_updateUserBlocked) {
                                                                                    final TL_updateUserBlocked tL_updateUserBlocked = (TL_updateUserBlocked) update;
                                                                                    if (tL_updateUserBlocked.blocked) {
                                                                                        arrayList11 = new ArrayList();
                                                                                        arrayList11.add(Integer.valueOf(tL_updateUserBlocked.user_id));
                                                                                        MessagesStorage.getInstance(messagesController.currentAccount).putBlockedUsers(arrayList11, false);
                                                                                    } else {
                                                                                        MessagesStorage.getInstance(messagesController.currentAccount).deleteBlockedUser(tL_updateUserBlocked.user_id);
                                                                                    }
                                                                                    MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                                                                        /* renamed from: org.telegram.messenger.MessagesController$129$1 */
                                                                                        class C03091 implements Runnable {
                                                                                            C03091() {
                                                                                            }

                                                                                            public void run() {
                                                                                                if (!tL_updateUserBlocked.blocked) {
                                                                                                    MessagesController.this.blockedUsers.remove(Integer.valueOf(tL_updateUserBlocked.user_id));
                                                                                                } else if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(tL_updateUserBlocked.user_id))) {
                                                                                                    MessagesController.this.blockedUsers.add(Integer.valueOf(tL_updateUserBlocked.user_id));
                                                                                                }
                                                                                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                                                                                            }
                                                                                        }

                                                                                        public void run() {
                                                                                            AndroidUtilities.runOnUIThread(new C03091());
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    if (update instanceof TL_updateNotifySettings) {
                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                        arrayList11.add(update);
                                                                                    } else if (update instanceof TL_updateServiceNotification) {
                                                                                        final TL_updateServiceNotification tL_updateServiceNotification = (TL_updateServiceNotification) update;
                                                                                        if (tL_updateServiceNotification.popup && tL_updateServiceNotification.message != null && tL_updateServiceNotification.message.length() > 0) {
                                                                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                                                                public void run() {
                                                                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(2), tL_updateServiceNotification.message);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                        if ((tL_updateServiceNotification.flags & 2) != 0) {
                                                                                            tL_messageService = new TL_message();
                                                                                            newMessageId = UserConfig.getInstance(messagesController.currentAccount).getNewMessageId();
                                                                                            tL_messageService.id = newMessageId;
                                                                                            tL_messageService.local_id = newMessageId;
                                                                                            UserConfig.getInstance(messagesController.currentAccount).saveConfig(false);
                                                                                            tL_messageService.unread = true;
                                                                                            tL_messageService.flags = 256;
                                                                                            if (tL_updateServiceNotification.inbox_date != 0) {
                                                                                                tL_messageService.date = tL_updateServiceNotification.inbox_date;
                                                                                            } else {
                                                                                                tL_messageService.date = (int) (System.currentTimeMillis() / 1000);
                                                                                            }
                                                                                            tL_messageService.from_id = 777000;
                                                                                            tL_messageService.to_id = new TL_peerUser();
                                                                                            tL_messageService.to_id.user_id = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                                                                                            tL_messageService.dialog_id = 777000;
                                                                                            if (tL_updateServiceNotification.media != null) {
                                                                                                tL_messageService.media = tL_updateServiceNotification.media;
                                                                                                tL_messageService.flags |= 512;
                                                                                            }
                                                                                            tL_messageService.message = tL_updateServiceNotification.message;
                                                                                            if (tL_updateServiceNotification.entities != null) {
                                                                                                tL_messageService.entities = tL_updateServiceNotification.entities;
                                                                                            }
                                                                                            arrayList19 = arrayList9 == null ? new ArrayList() : arrayList9;
                                                                                            arrayList19.add(tL_messageService);
                                                                                            long j6 = j4;
                                                                                            messageObject = new MessageObject(messagesController.currentAccount, tL_messageService, concurrentHashMap, abstractMap, messagesController.createdDialogIds.contains(Long.valueOf(tL_messageService.dialog_id)));
                                                                                            if (longSparseArray4 == null) {
                                                                                                longSparseArray4 = new LongSparseArray();
                                                                                            }
                                                                                            arrayList20 = (ArrayList) longSparseArray4.get(tL_messageService.dialog_id);
                                                                                            if (arrayList20 == null) {
                                                                                                arrayList20 = new ArrayList();
                                                                                                longSparseArray4.put(tL_messageService.dialog_id, arrayList20);
                                                                                            }
                                                                                            arrayList20.add(messageObject);
                                                                                            arrayList11 = arrayList17 == null ? new ArrayList() : arrayList17;
                                                                                            arrayList11.add(messageObject);
                                                                                            j4 = j6;
                                                                                        } else {
                                                                                            arrayList19 = arrayList9;
                                                                                            arrayList11 = arrayList17;
                                                                                        }
                                                                                        arrayList17 = arrayList11;
                                                                                        arrayList7 = arrayList12;
                                                                                        j = j4;
                                                                                        i4 = i2;
                                                                                        arrayList11 = arrayList6;
                                                                                        sparseIntArray4 = sparseIntArray;
                                                                                        arrayList13 = arrayList24;
                                                                                    } else if (update instanceof TL_updateDialogPinned) {
                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                        arrayList11.add(update);
                                                                                    } else if (update instanceof TL_updatePinnedDialogs) {
                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                        arrayList11.add(update);
                                                                                    } else if (update instanceof TL_updatePrivacy) {
                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                        arrayList11.add(update);
                                                                                    } else {
                                                                                        LongSparseArray longSparseArray8;
                                                                                        if (update instanceof TL_updateWebPage) {
                                                                                            TL_updateWebPage tL_updateWebPage = (TL_updateWebPage) update;
                                                                                            longSparseArray8 = longSparseArray6 == null ? new LongSparseArray() : longSparseArray6;
                                                                                            longSparseArray8.put(tL_updateWebPage.webpage.id, tL_updateWebPage.webpage);
                                                                                        } else if (update instanceof TL_updateChannelWebPage) {
                                                                                            TL_updateChannelWebPage tL_updateChannelWebPage = (TL_updateChannelWebPage) update;
                                                                                            longSparseArray8 = longSparseArray6 == null ? new LongSparseArray() : longSparseArray6;
                                                                                            longSparseArray8.put(tL_updateChannelWebPage.webpage.id, tL_updateChannelWebPage.webpage);
                                                                                        } else if (update instanceof TL_updateChannelTooLong) {
                                                                                            TL_updateChannelTooLong tL_updateChannelTooLong = (TL_updateChannelTooLong) update;
                                                                                            if (BuildVars.LOGS_ENABLED) {
                                                                                                StringBuilder stringBuilder3 = new StringBuilder();
                                                                                                stringBuilder3.append(update);
                                                                                                stringBuilder3.append(" channelId = ");
                                                                                                stringBuilder3.append(tL_updateChannelTooLong.channel_id);
                                                                                                FileLog.m0d(stringBuilder3.toString());
                                                                                            }
                                                                                            size4 = messagesController.channelsPts.get(tL_updateChannelTooLong.channel_id);
                                                                                            if (size4 == 0) {
                                                                                                size4 = MessagesStorage.getInstance(messagesController.currentAccount).getChannelPtsSync(tL_updateChannelTooLong.channel_id);
                                                                                                if (size4 == 0) {
                                                                                                    Chat chat3 = (Chat) abstractMap.get(Integer.valueOf(tL_updateChannelTooLong.channel_id));
                                                                                                    if (chat3 == null || chat3.min) {
                                                                                                        chat3 = getChat(Integer.valueOf(tL_updateChannelTooLong.channel_id));
                                                                                                    }
                                                                                                    if (chat3 == null || chat3.min) {
                                                                                                        chat3 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(tL_updateChannelTooLong.channel_id);
                                                                                                        putChat(chat3, true);
                                                                                                    }
                                                                                                    if (!(chat3 == null || chat3.min)) {
                                                                                                        loadUnknownChannel(chat3, 0);
                                                                                                    }
                                                                                                } else {
                                                                                                    messagesController.channelsPts.put(tL_updateChannelTooLong.channel_id, size4);
                                                                                                }
                                                                                            }
                                                                                            if (size4 != 0) {
                                                                                                if ((tL_updateChannelTooLong.flags & 1) == 0) {
                                                                                                    getChannelDifference(tL_updateChannelTooLong.channel_id);
                                                                                                } else if (tL_updateChannelTooLong.pts > size4) {
                                                                                                    getChannelDifference(tL_updateChannelTooLong.channel_id);
                                                                                                }
                                                                                            }
                                                                                        } else {
                                                                                            long j7;
                                                                                            if (update instanceof TL_updateReadChannelInbox) {
                                                                                                TL_updateReadChannelInbox tL_updateReadChannelInbox = (TL_updateReadChannelInbox) update;
                                                                                                arrayList22 = arrayList9;
                                                                                                j7 = j4;
                                                                                                j4 = ((long) tL_updateReadChannelInbox.max_id) | (((long) tL_updateReadChannelInbox.channel_id) << 32);
                                                                                                currentTimeMillis = (long) (-tL_updateReadChannelInbox.channel_id);
                                                                                                if (sparseLongArray4 == null) {
                                                                                                    sparseLongArray4 = new SparseLongArray();
                                                                                                }
                                                                                                sparseLongArray4.put(-tL_updateReadChannelInbox.channel_id, j4);
                                                                                                num2 = (Integer) messagesController.dialogs_read_inbox_max.get(Long.valueOf(currentTimeMillis));
                                                                                                if (num2 == null) {
                                                                                                    num2 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(false, currentTimeMillis));
                                                                                                }
                                                                                                messagesController.dialogs_read_inbox_max.put(Long.valueOf(currentTimeMillis), Integer.valueOf(Math.max(num2.intValue(), tL_updateReadChannelInbox.max_id)));
                                                                                            } else {
                                                                                                arrayList22 = arrayList9;
                                                                                                j7 = j4;
                                                                                                if (update instanceof TL_updateReadChannelOutbox) {
                                                                                                    TL_updateReadChannelOutbox tL_updateReadChannelOutbox = (TL_updateReadChannelOutbox) update;
                                                                                                    j4 = ((long) tL_updateReadChannelOutbox.max_id) | (((long) tL_updateReadChannelOutbox.channel_id) << 32);
                                                                                                    currentTimeMillis = (long) (-tL_updateReadChannelOutbox.channel_id);
                                                                                                    if (sparseLongArray3 == null) {
                                                                                                        sparseLongArray3 = new SparseLongArray();
                                                                                                    }
                                                                                                    sparseLongArray3.put(-tL_updateReadChannelOutbox.channel_id, j4);
                                                                                                    num2 = (Integer) messagesController.dialogs_read_outbox_max.get(Long.valueOf(currentTimeMillis));
                                                                                                    if (num2 == null) {
                                                                                                        num2 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(true, currentTimeMillis));
                                                                                                    }
                                                                                                    messagesController.dialogs_read_outbox_max.put(Long.valueOf(currentTimeMillis), Integer.valueOf(Math.max(num2.intValue(), tL_updateReadChannelOutbox.max_id)));
                                                                                                } else if (update instanceof TL_updateDeleteChannelMessages) {
                                                                                                    TL_updateDeleteChannelMessages tL_updateDeleteChannelMessages = (TL_updateDeleteChannelMessages) update;
                                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                                        r3 = new StringBuilder();
                                                                                                        r3.append(update);
                                                                                                        r3.append(" channelId = ");
                                                                                                        r3.append(tL_updateDeleteChannelMessages.channel_id);
                                                                                                        FileLog.m0d(r3.toString());
                                                                                                    }
                                                                                                    if (sparseArray3 == null) {
                                                                                                        sparseArray3 = new SparseArray();
                                                                                                    }
                                                                                                    arrayList19 = (ArrayList) sparseArray3.get(tL_updateDeleteChannelMessages.channel_id);
                                                                                                    if (arrayList19 == null) {
                                                                                                        arrayList19 = new ArrayList();
                                                                                                        sparseArray3.put(tL_updateDeleteChannelMessages.channel_id, arrayList19);
                                                                                                    }
                                                                                                    arrayList19.addAll(tL_updateDeleteChannelMessages.messages);
                                                                                                } else {
                                                                                                    if (update instanceof TL_updateChannel) {
                                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                                            TL_updateChannel tL_updateChannel = (TL_updateChannel) update;
                                                                                                            r3 = new StringBuilder();
                                                                                                            r3.append(update);
                                                                                                            r3.append(" channelId = ");
                                                                                                            r3.append(tL_updateChannel.channel_id);
                                                                                                            FileLog.m0d(r3.toString());
                                                                                                        }
                                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                        arrayList11.add(update);
                                                                                                    } else if (update instanceof TL_updateChannelMessageViews) {
                                                                                                        TL_updateChannelMessageViews tL_updateChannelMessageViews = (TL_updateChannelMessageViews) update;
                                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                                            r3 = new StringBuilder();
                                                                                                            r3.append(update);
                                                                                                            r3.append(" channelId = ");
                                                                                                            r3.append(tL_updateChannelMessageViews.channel_id);
                                                                                                            FileLog.m0d(r3.toString());
                                                                                                        }
                                                                                                        SparseArray sparseArray5 = sparseArray4 == null ? new SparseArray() : sparseArray4;
                                                                                                        SparseIntArray sparseIntArray6 = (SparseIntArray) sparseArray5.get(tL_updateChannelMessageViews.channel_id);
                                                                                                        if (sparseIntArray6 == null) {
                                                                                                            sparseIntArray6 = new SparseIntArray();
                                                                                                            sparseArray5.put(tL_updateChannelMessageViews.channel_id, sparseIntArray6);
                                                                                                        }
                                                                                                        sparseIntArray6.put(tL_updateChannelMessageViews.id, tL_updateChannelMessageViews.views);
                                                                                                        sparseArray4 = sparseArray5;
                                                                                                    } else {
                                                                                                        if (update instanceof TL_updateChatParticipantAdmin) {
                                                                                                            TL_updateChatParticipantAdmin tL_updateChatParticipantAdmin = (TL_updateChatParticipantAdmin) update;
                                                                                                            MessagesStorage.getInstance(messagesController.currentAccount).updateChatInfo(tL_updateChatParticipantAdmin.chat_id, tL_updateChatParticipantAdmin.user_id, 2, tL_updateChatParticipantAdmin.is_admin, tL_updateChatParticipantAdmin.version);
                                                                                                        } else if (update instanceof TL_updateChatAdmins) {
                                                                                                            arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                            arrayList11.add(update);
                                                                                                        } else if (update instanceof TL_updateStickerSets) {
                                                                                                            arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                            arrayList11.add(update);
                                                                                                        } else if (update instanceof TL_updateStickerSetsOrder) {
                                                                                                            arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                            arrayList11.add(update);
                                                                                                        } else if (update instanceof TL_updateNewStickerSet) {
                                                                                                            arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                            arrayList11.add(update);
                                                                                                        } else if (update instanceof TL_updateDraftMessage) {
                                                                                                            arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                            arrayList11.add(update);
                                                                                                        } else if (update instanceof TL_updateSavedGifs) {
                                                                                                            arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                            arrayList11.add(update);
                                                                                                        } else {
                                                                                                            MessageEntity messageEntity;
                                                                                                            int i11;
                                                                                                            User user4;
                                                                                                            User userSync;
                                                                                                            ConcurrentHashMap concurrentHashMap4;
                                                                                                            Integer num3;
                                                                                                            z9 = update instanceof TL_updateEditChannelMessage;
                                                                                                            if (!z9) {
                                                                                                                if (!(update instanceof TL_updateEditMessage)) {
                                                                                                                    if (update instanceof TL_updateChannelPinnedMessage) {
                                                                                                                        TL_updateChannelPinnedMessage tL_updateChannelPinnedMessage = (TL_updateChannelPinnedMessage) update;
                                                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                                                            r3 = new StringBuilder();
                                                                                                                            r3.append(update);
                                                                                                                            r3.append(" channelId = ");
                                                                                                                            r3.append(tL_updateChannelPinnedMessage.channel_id);
                                                                                                                            FileLog.m0d(r3.toString());
                                                                                                                        }
                                                                                                                        MessagesStorage.getInstance(messagesController.currentAccount).updateChannelPinnedMessage(tL_updateChannelPinnedMessage.channel_id, tL_updateChannelPinnedMessage.id);
                                                                                                                    } else if (update instanceof TL_updateReadFeaturedStickers) {
                                                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                                        arrayList11.add(update);
                                                                                                                    } else if (update instanceof TL_updatePhoneCall) {
                                                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                                        arrayList11.add(update);
                                                                                                                    } else if (update instanceof TL_updateLangPack) {
                                                                                                                        LocaleController.getInstance().saveRemoteLocaleStrings(((TL_updateLangPack) update).difference, messagesController.currentAccount);
                                                                                                                    } else if (update instanceof TL_updateLangPackTooLong) {
                                                                                                                        LocaleController.getInstance().reloadCurrentRemoteLocale(messagesController.currentAccount);
                                                                                                                    } else if (update instanceof TL_updateFavedStickers) {
                                                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                                        arrayList11.add(update);
                                                                                                                    } else if (update instanceof TL_updateContactsReset) {
                                                                                                                        arrayList11 = arrayList16 == null ? new ArrayList() : arrayList16;
                                                                                                                        arrayList11.add(update);
                                                                                                                    } else if (update instanceof TL_updateChannelAvailableMessages) {
                                                                                                                        TL_updateChannelAvailableMessages tL_updateChannelAvailableMessages = (TL_updateChannelAvailableMessages) update;
                                                                                                                        sparseIntArray4 = sparseIntArray5;
                                                                                                                        sparseIntArray2 = sparseIntArray4 == null ? new SparseIntArray() : sparseIntArray4;
                                                                                                                        newMessageId = sparseIntArray2.get(tL_updateChannelAvailableMessages.channel_id);
                                                                                                                        if (newMessageId == 0 || newMessageId < tL_updateChannelAvailableMessages.available_min_id) {
                                                                                                                            sparseIntArray2.put(tL_updateChannelAvailableMessages.channel_id, tL_updateChannelAvailableMessages.available_min_id);
                                                                                                                        }
                                                                                                                        sparseIntArray5 = sparseIntArray2;
                                                                                                                    } else {
                                                                                                                        sparseIntArray4 = sparseIntArray5;
                                                                                                                        sparseArray = sparseArray3;
                                                                                                                        arrayList7 = arrayList12;
                                                                                                                        sparseLongArray = sparseLongArray4;
                                                                                                                        longSparseArray = longSparseArray5;
                                                                                                                        longSparseArray2 = longSparseArray6;
                                                                                                                        j2 = j7;
                                                                                                                        longSparseArray5 = longSparseArray;
                                                                                                                        longSparseArray6 = longSparseArray2;
                                                                                                                        sparseIntArray5 = sparseIntArray4;
                                                                                                                        j = j2;
                                                                                                                        i4 = i2;
                                                                                                                        arrayList11 = arrayList6;
                                                                                                                        sparseIntArray4 = sparseIntArray;
                                                                                                                        arrayList13 = arrayList24;
                                                                                                                        arrayList14 = arrayList22;
                                                                                                                        sparseArray3 = sparseArray;
                                                                                                                        sparseLongArray4 = sparseLongArray;
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                            sparseIntArray4 = sparseIntArray5;
                                                                                                            newMessageId = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                                                                                                            if (z9) {
                                                                                                                message = ((TL_updateEditChannelMessage) update).message;
                                                                                                                Chat chat4 = (Chat) abstractMap.get(Integer.valueOf(message.to_id.channel_id));
                                                                                                                if (chat4 == null) {
                                                                                                                    chat4 = getChat(Integer.valueOf(message.to_id.channel_id));
                                                                                                                }
                                                                                                                if (chat4 == null) {
                                                                                                                    chat4 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(message.to_id.channel_id);
                                                                                                                    putChat(chat4, true);
                                                                                                                }
                                                                                                                if (chat4 != null && chat4.megagroup) {
                                                                                                                    message.flags |= Integer.MIN_VALUE;
                                                                                                                }
                                                                                                                sparseArray = sparseArray3;
                                                                                                            } else {
                                                                                                                message = ((TL_updateEditMessage) update).message;
                                                                                                                sparseArray = sparseArray3;
                                                                                                                if (message.dialog_id == ((long) newMessageId)) {
                                                                                                                    message.unread = false;
                                                                                                                    message.media_unread = false;
                                                                                                                    z9 = true;
                                                                                                                    message.out = true;
                                                                                                                    if (!message.out && message.from_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                                                                                                                        message.out = z9;
                                                                                                                    }
                                                                                                                    longSparseArray2 = longSparseArray6;
                                                                                                                    if (!z) {
                                                                                                                        size = 0;
                                                                                                                        while (size < size5) {
                                                                                                                            messageEntity = (MessageEntity) message.entities.get(size);
                                                                                                                            if (messageEntity instanceof TL_messageEntityMentionName) {
                                                                                                                                i11 = size5;
                                                                                                                            } else {
                                                                                                                                i7 = ((TL_messageEntityMentionName) messageEntity).user_id;
                                                                                                                                user4 = (User) concurrentHashMap.get(Integer.valueOf(i7));
                                                                                                                                if (user4 == null) {
                                                                                                                                    i11 = size5;
                                                                                                                                    if (user4.min) {
                                                                                                                                    }
                                                                                                                                    if (user4 == null || user4.min) {
                                                                                                                                        userSync = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i7);
                                                                                                                                        if (userSync == null && userSync.min) {
                                                                                                                                            z9 = true;
                                                                                                                                            user4 = null;
                                                                                                                                        } else {
                                                                                                                                            user4 = userSync;
                                                                                                                                            z9 = true;
                                                                                                                                        }
                                                                                                                                        putUser(user4, z9);
                                                                                                                                    }
                                                                                                                                    if (user4 != null) {
                                                                                                                                        return false;
                                                                                                                                    }
                                                                                                                                } else {
                                                                                                                                    i11 = size5;
                                                                                                                                }
                                                                                                                                user4 = getUser(Integer.valueOf(i7));
                                                                                                                                userSync = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i7);
                                                                                                                                if (userSync == null) {
                                                                                                                                }
                                                                                                                                user4 = userSync;
                                                                                                                                z9 = true;
                                                                                                                                putUser(user4, z9);
                                                                                                                                if (user4 != null) {
                                                                                                                                    return false;
                                                                                                                                }
                                                                                                                            }
                                                                                                                            size++;
                                                                                                                        }
                                                                                                                    }
                                                                                                                    if (message.to_id.chat_id != 0) {
                                                                                                                        message.dialog_id = (long) (-message.to_id.chat_id);
                                                                                                                    } else if (message.to_id.channel_id == 0) {
                                                                                                                        message.dialog_id = (long) (-message.to_id.channel_id);
                                                                                                                    } else {
                                                                                                                        if (message.to_id.user_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                                                                                                                            message.to_id.user_id = message.from_id;
                                                                                                                        }
                                                                                                                        message.dialog_id = (long) message.to_id.user_id;
                                                                                                                    }
                                                                                                                    concurrentHashMap4 = message.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                                                                                                                    num3 = (Integer) concurrentHashMap4.get(Long.valueOf(message.dialog_id));
                                                                                                                    if (num3 != null) {
                                                                                                                        arrayList7 = arrayList12;
                                                                                                                        sparseLongArray = sparseLongArray4;
                                                                                                                        num3 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                                                                                                                        concurrentHashMap4.put(Long.valueOf(message.dialog_id), num3);
                                                                                                                    } else {
                                                                                                                        arrayList7 = arrayList12;
                                                                                                                        sparseLongArray = sparseLongArray4;
                                                                                                                    }
                                                                                                                    message.unread = num3.intValue() >= message.id;
                                                                                                                    if (message.dialog_id == ((long) newMessageId)) {
                                                                                                                        message.out = true;
                                                                                                                        message.unread = false;
                                                                                                                        message.media_unread = false;
                                                                                                                    }
                                                                                                                    if (message.out && message.message == null) {
                                                                                                                        message.message = TtmlNode.ANONYMOUS_REGION_ID;
                                                                                                                        message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                                                                                                                    }
                                                                                                                    ImageLoader.saveMessageThumbs(message);
                                                                                                                    j2 = j7;
                                                                                                                    messageObject = new MessageObject(messagesController.currentAccount, message, concurrentHashMap, abstractMap, messagesController.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                                                                                                                    longSparseArray = longSparseArray5;
                                                                                                                    if (longSparseArray == null) {
                                                                                                                        longSparseArray = new LongSparseArray();
                                                                                                                    }
                                                                                                                    arrayList9 = (ArrayList) longSparseArray.get(message.dialog_id);
                                                                                                                    if (arrayList9 == null) {
                                                                                                                        arrayList9 = new ArrayList();
                                                                                                                        longSparseArray.put(message.dialog_id, arrayList9);
                                                                                                                    }
                                                                                                                    arrayList9.add(messageObject);
                                                                                                                    longSparseArray5 = longSparseArray;
                                                                                                                    longSparseArray6 = longSparseArray2;
                                                                                                                    sparseIntArray5 = sparseIntArray4;
                                                                                                                    j = j2;
                                                                                                                    i4 = i2;
                                                                                                                    arrayList11 = arrayList6;
                                                                                                                    sparseIntArray4 = sparseIntArray;
                                                                                                                    arrayList13 = arrayList24;
                                                                                                                    arrayList14 = arrayList22;
                                                                                                                    sparseArray3 = sparseArray;
                                                                                                                    sparseLongArray4 = sparseLongArray;
                                                                                                                }
                                                                                                            }
                                                                                                            z9 = true;
                                                                                                            message.out = z9;
                                                                                                            longSparseArray2 = longSparseArray6;
                                                                                                            if (z) {
                                                                                                                size = 0;
                                                                                                                for (size5 = message.entities.size(); size < size5; size5 = i11) {
                                                                                                                    messageEntity = (MessageEntity) message.entities.get(size);
                                                                                                                    if (messageEntity instanceof TL_messageEntityMentionName) {
                                                                                                                        i11 = size5;
                                                                                                                    } else {
                                                                                                                        i7 = ((TL_messageEntityMentionName) messageEntity).user_id;
                                                                                                                        user4 = (User) concurrentHashMap.get(Integer.valueOf(i7));
                                                                                                                        if (user4 == null) {
                                                                                                                            i11 = size5;
                                                                                                                        } else {
                                                                                                                            i11 = size5;
                                                                                                                            if (user4.min) {
                                                                                                                            }
                                                                                                                            userSync = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i7);
                                                                                                                            if (userSync == null) {
                                                                                                                            }
                                                                                                                            user4 = userSync;
                                                                                                                            z9 = true;
                                                                                                                            putUser(user4, z9);
                                                                                                                            if (user4 != null) {
                                                                                                                                return false;
                                                                                                                            }
                                                                                                                        }
                                                                                                                        user4 = getUser(Integer.valueOf(i7));
                                                                                                                        userSync = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(i7);
                                                                                                                        if (userSync == null) {
                                                                                                                        }
                                                                                                                        user4 = userSync;
                                                                                                                        z9 = true;
                                                                                                                        putUser(user4, z9);
                                                                                                                        if (user4 != null) {
                                                                                                                            return false;
                                                                                                                        }
                                                                                                                    }
                                                                                                                    size++;
                                                                                                                }
                                                                                                            }
                                                                                                            if (message.to_id.chat_id != 0) {
                                                                                                                message.dialog_id = (long) (-message.to_id.chat_id);
                                                                                                            } else if (message.to_id.channel_id == 0) {
                                                                                                                if (message.to_id.user_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                                                                                                                    message.to_id.user_id = message.from_id;
                                                                                                                }
                                                                                                                message.dialog_id = (long) message.to_id.user_id;
                                                                                                            } else {
                                                                                                                message.dialog_id = (long) (-message.to_id.channel_id);
                                                                                                            }
                                                                                                            if (message.out) {
                                                                                                            }
                                                                                                            num3 = (Integer) concurrentHashMap4.get(Long.valueOf(message.dialog_id));
                                                                                                            if (num3 != null) {
                                                                                                                arrayList7 = arrayList12;
                                                                                                                sparseLongArray = sparseLongArray4;
                                                                                                            } else {
                                                                                                                arrayList7 = arrayList12;
                                                                                                                sparseLongArray = sparseLongArray4;
                                                                                                                num3 = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                                                                                                                concurrentHashMap4.put(Long.valueOf(message.dialog_id), num3);
                                                                                                            }
                                                                                                            if (num3.intValue() >= message.id) {
                                                                                                            }
                                                                                                            message.unread = num3.intValue() >= message.id;
                                                                                                            if (message.dialog_id == ((long) newMessageId)) {
                                                                                                                message.out = true;
                                                                                                                message.unread = false;
                                                                                                                message.media_unread = false;
                                                                                                            }
                                                                                                            message.message = TtmlNode.ANONYMOUS_REGION_ID;
                                                                                                            message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                                                                                                            ImageLoader.saveMessageThumbs(message);
                                                                                                            j2 = j7;
                                                                                                            messageObject = new MessageObject(messagesController.currentAccount, message, concurrentHashMap, abstractMap, messagesController.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                                                                                                            longSparseArray = longSparseArray5;
                                                                                                            if (longSparseArray == null) {
                                                                                                                longSparseArray = new LongSparseArray();
                                                                                                            }
                                                                                                            arrayList9 = (ArrayList) longSparseArray.get(message.dialog_id);
                                                                                                            if (arrayList9 == null) {
                                                                                                                arrayList9 = new ArrayList();
                                                                                                                longSparseArray.put(message.dialog_id, arrayList9);
                                                                                                            }
                                                                                                            arrayList9.add(messageObject);
                                                                                                            longSparseArray5 = longSparseArray;
                                                                                                            longSparseArray6 = longSparseArray2;
                                                                                                            sparseIntArray5 = sparseIntArray4;
                                                                                                            j = j2;
                                                                                                            i4 = i2;
                                                                                                            arrayList11 = arrayList6;
                                                                                                            sparseIntArray4 = sparseIntArray;
                                                                                                            arrayList13 = arrayList24;
                                                                                                            arrayList14 = arrayList22;
                                                                                                            sparseArray3 = sparseArray;
                                                                                                            sparseLongArray4 = sparseLongArray;
                                                                                                        }
                                                                                                        sparseArray = sparseArray3;
                                                                                                        arrayList7 = arrayList12;
                                                                                                        sparseLongArray = sparseLongArray4;
                                                                                                        longSparseArray = longSparseArray5;
                                                                                                        longSparseArray2 = longSparseArray6;
                                                                                                        sparseIntArray4 = sparseIntArray5;
                                                                                                        j2 = j7;
                                                                                                        longSparseArray5 = longSparseArray;
                                                                                                        longSparseArray6 = longSparseArray2;
                                                                                                        sparseIntArray5 = sparseIntArray4;
                                                                                                        j = j2;
                                                                                                        i4 = i2;
                                                                                                        arrayList11 = arrayList6;
                                                                                                        sparseIntArray4 = sparseIntArray;
                                                                                                        arrayList13 = arrayList24;
                                                                                                        arrayList14 = arrayList22;
                                                                                                        sparseArray3 = sparseArray;
                                                                                                        sparseLongArray4 = sparseLongArray;
                                                                                                    }
                                                                                                    arrayList16 = arrayList11;
                                                                                                }
                                                                                            }
                                                                                            arrayList7 = arrayList12;
                                                                                            i4 = i2;
                                                                                            arrayList11 = arrayList6;
                                                                                            sparseIntArray4 = sparseIntArray;
                                                                                            arrayList13 = arrayList24;
                                                                                            arrayList14 = arrayList22;
                                                                                            j = j7;
                                                                                        }
                                                                                        longSparseArray6 = longSparseArray8;
                                                                                    }
                                                                                    arrayList16 = arrayList11;
                                                                                }
                                                                            }
                                                                        }
                                                                        sparseArray = sparseArray3;
                                                                        arrayList22 = arrayList9;
                                                                        arrayList7 = arrayList12;
                                                                        sparseLongArray = sparseLongArray4;
                                                                        j2 = j4;
                                                                        longSparseArray = longSparseArray5;
                                                                        longSparseArray2 = longSparseArray6;
                                                                        sparseIntArray4 = sparseIntArray5;
                                                                        ConnectionsManager.getInstance(messagesController.currentAccount).updateDcSettings();
                                                                        longSparseArray5 = longSparseArray;
                                                                        longSparseArray6 = longSparseArray2;
                                                                        sparseIntArray5 = sparseIntArray4;
                                                                        j = j2;
                                                                        i4 = i2;
                                                                        arrayList11 = arrayList6;
                                                                        sparseIntArray4 = sparseIntArray;
                                                                        arrayList13 = arrayList24;
                                                                        arrayList14 = arrayList22;
                                                                        sparseArray3 = sparseArray;
                                                                        sparseLongArray4 = sparseLongArray;
                                                                    }
                                                                    sparseArray = sparseArray3;
                                                                    arrayList22 = arrayList9;
                                                                    arrayList7 = arrayList12;
                                                                    sparseLongArray = sparseLongArray4;
                                                                    j2 = j4;
                                                                    longSparseArray = longSparseArray5;
                                                                    longSparseArray2 = longSparseArray6;
                                                                    sparseIntArray4 = sparseIntArray5;
                                                                    longSparseArray5 = longSparseArray;
                                                                    longSparseArray6 = longSparseArray2;
                                                                    sparseIntArray5 = sparseIntArray4;
                                                                    j = j2;
                                                                    i4 = i2;
                                                                    arrayList11 = arrayList6;
                                                                    sparseIntArray4 = sparseIntArray;
                                                                    arrayList13 = arrayList24;
                                                                    arrayList14 = arrayList22;
                                                                    sparseArray3 = sparseArray;
                                                                    sparseLongArray4 = sparseLongArray;
                                                                }
                                                            }
                                                            arrayList7 = arrayList12;
                                                            j = j4;
                                                            i4 = i2;
                                                            arrayList11 = arrayList6;
                                                            sparseIntArray4 = sparseIntArray;
                                                        }
                                                        arrayList13 = arrayList24;
                                                    }
                                                    arrayList14 = arrayList9;
                                                    size = i5 + 1;
                                                    size4 = i6;
                                                    arrayList12 = arrayList7;
                                                    currentTimeMillis = j;
                                                }
                                                arrayList14 = arrayList19;
                                                size = i5 + 1;
                                                size4 = i6;
                                                arrayList12 = arrayList7;
                                                currentTimeMillis = j;
                                            }
                                            arrayList16 = arrayList11;
                                        }
                                    }
                                }
                                sparseArray = sparseArray3;
                                arrayList7 = arrayList12;
                                sparseLongArray = sparseLongArray4;
                                arrayList22 = arrayList14;
                                longSparseArray = longSparseArray5;
                                longSparseArray2 = longSparseArray6;
                                sparseIntArray4 = sparseIntArray5;
                                j2 = j3;
                                i2 = i4;
                                ArrayList arrayList27 = arrayList18;
                                if (z9) {
                                    TL_updateUserTyping tL_updateUserTyping = (TL_updateUserTyping) update;
                                    size5 = tL_updateUserTyping.user_id;
                                    sendMessageAction = tL_updateUserTyping.action;
                                    size4 = 0;
                                } else {
                                    TL_updateChatUserTyping tL_updateChatUserTyping = (TL_updateChatUserTyping) update;
                                    size5 = tL_updateChatUserTyping.chat_id;
                                    int i12 = tL_updateChatUserTyping.user_id;
                                    sendMessageAction = tL_updateChatUserTyping.action;
                                    size4 = size5;
                                    size5 = i12;
                                }
                                if (size5 != UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                                    arrayList21 = arrayList27;
                                    j4 = (long) (-size4);
                                    if (j4 == 0) {
                                        j4 = (long) size5;
                                    }
                                    arrayList19 = (ArrayList) messagesController.printingUsers.get(Long.valueOf(j4));
                                    if (!(sendMessageAction instanceof TL_sendMessageCancelAction)) {
                                        Object obj3;
                                        PrintingUser printingUser3;
                                        if (arrayList19 == null) {
                                            arrayList19 = new ArrayList();
                                            messagesController.printingUsers.put(Long.valueOf(j4), arrayList19);
                                        }
                                        Iterator it = arrayList19.iterator();
                                        while (it.hasNext()) {
                                            PrintingUser printingUser4 = (PrintingUser) it.next();
                                            if (printingUser4.userId == size5) {
                                                printingUser4.lastTime = j2;
                                                if (printingUser4.action.getClass() != sendMessageAction.getClass()) {
                                                    z6 = true;
                                                }
                                                printingUser4.action = sendMessageAction;
                                                obj3 = 1;
                                                if (obj3 == null) {
                                                    printingUser3 = new PrintingUser();
                                                    printingUser3.userId = size5;
                                                    printingUser3.lastTime = j2;
                                                    printingUser3.action = sendMessageAction;
                                                    arrayList19.add(printingUser3);
                                                    z6 = true;
                                                }
                                            }
                                        }
                                        obj3 = null;
                                        if (obj3 == null) {
                                            printingUser3 = new PrintingUser();
                                            printingUser3.userId = size5;
                                            printingUser3.lastTime = j2;
                                            printingUser3.action = sendMessageAction;
                                            arrayList19.add(printingUser3);
                                            z6 = true;
                                        }
                                    } else if (arrayList19 != null) {
                                        size2 = arrayList19.size();
                                        size = 0;
                                        while (size < size2) {
                                            int i13 = size2;
                                            if (((PrintingUser) arrayList19.get(size)).userId == size5) {
                                                arrayList19.remove(size);
                                                z6 = true;
                                                break;
                                            }
                                            size++;
                                            size2 = i13;
                                        }
                                        if (arrayList19.isEmpty()) {
                                            messagesController.printingUsers.remove(Long.valueOf(j4));
                                        }
                                    }
                                    messagesController.onlinePrivacy.put(Integer.valueOf(size5), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                } else {
                                    arrayList21 = arrayList27;
                                }
                                longSparseArray5 = longSparseArray;
                                longSparseArray6 = longSparseArray2;
                                sparseIntArray5 = sparseIntArray4;
                                j = j2;
                                i4 = i2;
                                arrayList11 = arrayList6;
                                sparseIntArray4 = sparseIntArray;
                                arrayList14 = arrayList22;
                                sparseArray3 = sparseArray;
                                sparseLongArray4 = sparseLongArray;
                                arrayList13 = arrayList21;
                            }
                            arrayList7 = arrayList12;
                            arrayList13 = arrayList18;
                            arrayList11 = arrayList6;
                            sparseIntArray4 = sparseIntArray;
                            j = j3;
                        }
                    }
                    size = i5 + 1;
                    size4 = i6;
                    arrayList12 = arrayList7;
                    currentTimeMillis = j;
                }
            }
            arrayList6 = arrayList11;
            sparseArray = sparseArray3;
            sparseIntArray = sparseIntArray4;
            arrayList7 = arrayList12;
            sparseLongArray = sparseLongArray4;
            arrayList11 = arrayList14;
            sparseIntArray4 = sparseIntArray5;
            arrayList21 = arrayList18;
            j2 = currentTimeMillis;
            i2 = i4;
            longSparseArray = longSparseArray5;
            longSparseArray2 = longSparseArray6;
            if (z8) {
                message = ((TL_updateNewMessage) update).message;
            } else {
                Message message2 = ((TL_updateNewChannelMessage) update).message;
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(update);
                    stringBuilder4.append(" channelId = ");
                    stringBuilder4.append(message2.to_id.channel_id);
                    FileLog.m0d(stringBuilder4.toString());
                }
                if (!message2.out && message2.from_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                    message2.out = true;
                }
                message = message2;
            }
            if (message.to_id.channel_id != 0) {
                size2 = message.to_id.channel_id;
            } else if (message.to_id.chat_id != 0) {
                size2 = message.to_id.chat_id;
            } else if (message.to_id.user_id != 0) {
                size = message.to_id.user_id;
                size2 = 0;
                if (size2 == 0) {
                    chat2 = (Chat) abstractMap.get(Integer.valueOf(size2));
                    if (chat2 == null) {
                        chat2 = getChat(Integer.valueOf(size2));
                    }
                    if (chat2 == null) {
                        chat2 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(size2);
                        putChat(chat2, true);
                    }
                } else {
                    chat2 = null;
                }
                if (z5) {
                    if (size2 == 0 && chat2 == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("not found chat ");
                            stringBuilder.append(size2);
                            FileLog.m0d(stringBuilder.toString());
                        }
                        return false;
                    }
                    i4 = 3 + message.entities.size();
                    size2 = 0;
                    while (size2 < i4) {
                        if (size2 == 0) {
                            longSparseArray3 = longSparseArray2;
                            if (size2 == 1) {
                                size6 = message.from_id;
                                if (message.post) {
                                    size = size6;
                                    obj = 1;
                                    if (size <= 0) {
                                        sparseIntArray3 = sparseIntArray4;
                                        user2 = (User) concurrentHashMap.get(Integer.valueOf(size));
                                        if (user2 == null) {
                                            if (obj != null) {
                                                sparseLongArray2 = sparseLongArray3;
                                                if (user2.min) {
                                                }
                                            } else {
                                                sparseLongArray2 = sparseLongArray3;
                                            }
                                            if (user2 == null || (obj == null && user2.min)) {
                                                user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                                                if (user2 == null && obj == null && user2.min) {
                                                    z7 = true;
                                                    user2 = null;
                                                } else {
                                                    z7 = true;
                                                }
                                                putUser(user2, z7);
                                            }
                                            if (user2 != null) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("not found user ");
                                                    stringBuilder.append(size);
                                                    FileLog.m0d(stringBuilder.toString());
                                                }
                                                return false;
                                            } else if (size2 == 1 && user2.status != null && user2.status.expires <= 0) {
                                                messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                                i2 |= 4;
                                            }
                                        } else {
                                            sparseLongArray2 = sparseLongArray3;
                                        }
                                        user2 = getUser(Integer.valueOf(size));
                                        user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                                        if (user2 == null) {
                                        }
                                        z7 = true;
                                        putUser(user2, z7);
                                        if (user2 != null) {
                                            messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                            i2 |= 4;
                                        } else {
                                            if (BuildVars.LOGS_ENABLED) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("not found user ");
                                                stringBuilder.append(size);
                                                FileLog.m0d(stringBuilder.toString());
                                            }
                                            return false;
                                        }
                                    }
                                    sparseIntArray3 = sparseIntArray4;
                                    sparseLongArray2 = sparseLongArray3;
                                    size2++;
                                    longSparseArray2 = longSparseArray3;
                                    sparseIntArray4 = sparseIntArray3;
                                    sparseLongArray3 = sparseLongArray2;
                                }
                            } else if (size2 != 2) {
                                size = message.fwd_from == null ? message.fwd_from.from_id : 0;
                            } else {
                                MessageEntity messageEntity2 = (MessageEntity) message.entities.get(size2 - 3);
                                size6 = messageEntity2 instanceof TL_messageEntityMentionName ? ((TL_messageEntityMentionName) messageEntity2).user_id : 0;
                            }
                            size = size6;
                        } else {
                            longSparseArray3 = longSparseArray2;
                        }
                        obj = null;
                        if (size <= 0) {
                            sparseIntArray3 = sparseIntArray4;
                            sparseLongArray2 = sparseLongArray3;
                        } else {
                            sparseIntArray3 = sparseIntArray4;
                            user2 = (User) concurrentHashMap.get(Integer.valueOf(size));
                            if (user2 == null) {
                                sparseLongArray2 = sparseLongArray3;
                            } else {
                                if (obj != null) {
                                    sparseLongArray2 = sparseLongArray3;
                                } else {
                                    sparseLongArray2 = sparseLongArray3;
                                    if (user2.min) {
                                    }
                                }
                                user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                                if (user2 == null) {
                                }
                                z7 = true;
                                putUser(user2, z7);
                                if (user2 != null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("not found user ");
                                        stringBuilder.append(size);
                                        FileLog.m0d(stringBuilder.toString());
                                    }
                                    return false;
                                }
                                messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                i2 |= 4;
                            }
                            user2 = getUser(Integer.valueOf(size));
                            user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                            if (user2 == null) {
                            }
                            z7 = true;
                            putUser(user2, z7);
                            if (user2 != null) {
                                messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                i2 |= 4;
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("not found user ");
                                    stringBuilder.append(size);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                                return false;
                            }
                        }
                        size2++;
                        longSparseArray2 = longSparseArray3;
                        sparseIntArray4 = sparseIntArray3;
                        sparseLongArray3 = sparseLongArray2;
                    }
                }
                longSparseArray3 = longSparseArray2;
                sparseIntArray3 = sparseIntArray4;
                sparseLongArray2 = sparseLongArray3;
                if (chat2 != null && chat2.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                if (message.action instanceof TL_messageActionChatDeleteUser) {
                    user3 = (User) concurrentHashMap.get(Integer.valueOf(message.action.user_id));
                    if (user3 == null && user3.bot) {
                        message.reply_markup = new TL_replyKeyboardHide();
                        message.flags |= 64;
                    } else if (message.from_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId() && message.action.user_id == UserConfig.getInstance(messagesController.currentAccount).getClientUserId()) {
                        longSparseArray5 = longSparseArray;
                        j = j2;
                        i4 = i2;
                        sparseIntArray4 = sparseIntArray;
                        sparseArray3 = sparseArray;
                        sparseLongArray4 = sparseLongArray;
                        arrayList13 = arrayList21;
                        longSparseArray6 = longSparseArray3;
                        sparseIntArray5 = sparseIntArray3;
                        sparseLongArray3 = sparseLongArray2;
                        arrayList14 = arrayList11;
                        arrayList11 = arrayList6;
                        size = i5 + 1;
                        size4 = i6;
                        arrayList12 = arrayList7;
                        currentTimeMillis = j;
                    }
                }
                if (arrayList11 == null) {
                    arrayList11 = new ArrayList();
                }
                arrayList11.add(message);
                ImageLoader.saveMessageThumbs(message);
                size2 = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
                if (message.to_id.chat_id != 0) {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                } else if (message.to_id.channel_id == 0) {
                    message.dialog_id = (long) (-message.to_id.channel_id);
                } else {
                    if (message.to_id.user_id == size2) {
                        message.to_id.user_id = message.from_id;
                    }
                    message.dialog_id = (long) message.to_id.user_id;
                }
                concurrentHashMap3 = message.out ? messagesController.dialogs_read_outbox_max : messagesController.dialogs_read_inbox_max;
                num = (Integer) concurrentHashMap3.get(Long.valueOf(message.dialog_id));
                if (num != null) {
                    j = j2;
                    num = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                    concurrentHashMap3.put(Long.valueOf(message.dialog_id), num);
                } else {
                    j = j2;
                }
                z7 = num.intValue() >= message.id && !((chat2 != null && ChatObject.isNotInChat(chat2)) || (message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate));
                message.unread = z7;
                if (message.dialog_id == ((long) size2)) {
                    message.unread = false;
                    message.media_unread = false;
                    message.out = true;
                }
                arrayList9 = arrayList21;
                messageObject = new MessageObject(messagesController.currentAccount, message, concurrentHashMap, abstractMap, messagesController.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                if (messageObject.type == 11) {
                    size6 = i2 | 8;
                } else if (messageObject.type != 10) {
                    size6 = i2 | 16;
                } else {
                    i4 = i2;
                    if (longSparseArray4 == null) {
                        longSparseArray4 = new LongSparseArray();
                    }
                    arrayList20 = (ArrayList) longSparseArray4.get(message.dialog_id);
                    if (arrayList20 == null) {
                        arrayList20 = new ArrayList();
                        longSparseArray4.put(message.dialog_id, arrayList20);
                    }
                    arrayList20.add(messageObject);
                    if (messageObject.isOut() && messageObject.isUnread()) {
                        arrayList19 = arrayList17 == null ? new ArrayList() : arrayList17;
                        arrayList19.add(messageObject);
                    } else {
                        arrayList19 = arrayList17;
                    }
                    arrayList17 = arrayList19;
                    arrayList14 = arrayList11;
                    longSparseArray5 = longSparseArray;
                    arrayList13 = arrayList9;
                    arrayList11 = arrayList6;
                    sparseIntArray4 = sparseIntArray;
                    sparseArray3 = sparseArray;
                    sparseLongArray4 = sparseLongArray;
                    longSparseArray6 = longSparseArray3;
                    sparseIntArray5 = sparseIntArray3;
                    sparseLongArray3 = sparseLongArray2;
                    size = i5 + 1;
                    size4 = i6;
                    arrayList12 = arrayList7;
                    currentTimeMillis = j;
                }
                i4 = size6;
                if (longSparseArray4 == null) {
                    longSparseArray4 = new LongSparseArray();
                }
                arrayList20 = (ArrayList) longSparseArray4.get(message.dialog_id);
                if (arrayList20 == null) {
                    arrayList20 = new ArrayList();
                    longSparseArray4.put(message.dialog_id, arrayList20);
                }
                arrayList20.add(messageObject);
                if (messageObject.isOut()) {
                }
                arrayList19 = arrayList17;
                arrayList17 = arrayList19;
                arrayList14 = arrayList11;
                longSparseArray5 = longSparseArray;
                arrayList13 = arrayList9;
                arrayList11 = arrayList6;
                sparseIntArray4 = sparseIntArray;
                sparseArray3 = sparseArray;
                sparseLongArray4 = sparseLongArray;
                longSparseArray6 = longSparseArray3;
                sparseIntArray5 = sparseIntArray3;
                sparseLongArray3 = sparseLongArray2;
                size = i5 + 1;
                size4 = i6;
                arrayList12 = arrayList7;
                currentTimeMillis = j;
            } else {
                size2 = 0;
            }
            size = 0;
            if (size2 == 0) {
                chat2 = null;
            } else {
                chat2 = (Chat) abstractMap.get(Integer.valueOf(size2));
                if (chat2 == null) {
                    chat2 = getChat(Integer.valueOf(size2));
                }
                if (chat2 == null) {
                    chat2 = MessagesStorage.getInstance(messagesController.currentAccount).getChatSync(size2);
                    putChat(chat2, true);
                }
            }
            if (z5) {
                if (size2 == 0) {
                }
                i4 = 3 + message.entities.size();
                size2 = 0;
                while (size2 < i4) {
                    if (size2 == 0) {
                        longSparseArray3 = longSparseArray2;
                    } else {
                        longSparseArray3 = longSparseArray2;
                        if (size2 == 1) {
                            size6 = message.from_id;
                            if (message.post) {
                                size = size6;
                                obj = 1;
                                if (size <= 0) {
                                    sparseIntArray3 = sparseIntArray4;
                                    user2 = (User) concurrentHashMap.get(Integer.valueOf(size));
                                    if (user2 == null) {
                                        if (obj != null) {
                                            sparseLongArray2 = sparseLongArray3;
                                            if (user2.min) {
                                            }
                                        } else {
                                            sparseLongArray2 = sparseLongArray3;
                                        }
                                        user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                                        if (user2 == null) {
                                        }
                                        z7 = true;
                                        putUser(user2, z7);
                                        if (user2 != null) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("not found user ");
                                                stringBuilder.append(size);
                                                FileLog.m0d(stringBuilder.toString());
                                            }
                                            return false;
                                        }
                                        messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                        i2 |= 4;
                                    } else {
                                        sparseLongArray2 = sparseLongArray3;
                                    }
                                    user2 = getUser(Integer.valueOf(size));
                                    user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                                    if (user2 == null) {
                                    }
                                    z7 = true;
                                    putUser(user2, z7);
                                    if (user2 != null) {
                                        messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                                        i2 |= 4;
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("not found user ");
                                            stringBuilder.append(size);
                                            FileLog.m0d(stringBuilder.toString());
                                        }
                                        return false;
                                    }
                                }
                                sparseIntArray3 = sparseIntArray4;
                                sparseLongArray2 = sparseLongArray3;
                                size2++;
                                longSparseArray2 = longSparseArray3;
                                sparseIntArray4 = sparseIntArray3;
                                sparseLongArray3 = sparseLongArray2;
                            }
                        } else if (size2 != 2) {
                            MessageEntity messageEntity22 = (MessageEntity) message.entities.get(size2 - 3);
                            if (messageEntity22 instanceof TL_messageEntityMentionName) {
                            }
                        } else if (message.fwd_from == null) {
                        }
                        size = size6;
                    }
                    obj = null;
                    if (size <= 0) {
                        sparseIntArray3 = sparseIntArray4;
                        sparseLongArray2 = sparseLongArray3;
                    } else {
                        sparseIntArray3 = sparseIntArray4;
                        user2 = (User) concurrentHashMap.get(Integer.valueOf(size));
                        if (user2 == null) {
                            sparseLongArray2 = sparseLongArray3;
                        } else {
                            if (obj != null) {
                                sparseLongArray2 = sparseLongArray3;
                            } else {
                                sparseLongArray2 = sparseLongArray3;
                                if (user2.min) {
                                }
                            }
                            user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                            if (user2 == null) {
                            }
                            z7 = true;
                            putUser(user2, z7);
                            if (user2 != null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("not found user ");
                                    stringBuilder.append(size);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                                return false;
                            }
                            messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                            i2 |= 4;
                        }
                        user2 = getUser(Integer.valueOf(size));
                        user2 = MessagesStorage.getInstance(messagesController.currentAccount).getUserSync(size);
                        if (user2 == null) {
                        }
                        z7 = true;
                        putUser(user2, z7);
                        if (user2 != null) {
                            messagesController.onlinePrivacy.put(Integer.valueOf(size), Integer.valueOf(ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime()));
                            i2 |= 4;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("not found user ");
                                stringBuilder.append(size);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            return false;
                        }
                    }
                    size2++;
                    longSparseArray2 = longSparseArray3;
                    sparseIntArray4 = sparseIntArray3;
                    sparseLongArray3 = sparseLongArray2;
                }
            }
            longSparseArray3 = longSparseArray2;
            sparseIntArray3 = sparseIntArray4;
            sparseLongArray2 = sparseLongArray3;
            message.flags |= Integer.MIN_VALUE;
            if (message.action instanceof TL_messageActionChatDeleteUser) {
                user3 = (User) concurrentHashMap.get(Integer.valueOf(message.action.user_id));
                if (user3 == null) {
                }
                longSparseArray5 = longSparseArray;
                j = j2;
                i4 = i2;
                sparseIntArray4 = sparseIntArray;
                sparseArray3 = sparseArray;
                sparseLongArray4 = sparseLongArray;
                arrayList13 = arrayList21;
                longSparseArray6 = longSparseArray3;
                sparseIntArray5 = sparseIntArray3;
                sparseLongArray3 = sparseLongArray2;
                arrayList14 = arrayList11;
                arrayList11 = arrayList6;
                size = i5 + 1;
                size4 = i6;
                arrayList12 = arrayList7;
                currentTimeMillis = j;
            }
            if (arrayList11 == null) {
                arrayList11 = new ArrayList();
            }
            arrayList11.add(message);
            ImageLoader.saveMessageThumbs(message);
            size2 = UserConfig.getInstance(messagesController.currentAccount).getClientUserId();
            if (message.to_id.chat_id != 0) {
                message.dialog_id = (long) (-message.to_id.chat_id);
            } else if (message.to_id.channel_id == 0) {
                if (message.to_id.user_id == size2) {
                    message.to_id.user_id = message.from_id;
                }
                message.dialog_id = (long) message.to_id.user_id;
            } else {
                message.dialog_id = (long) (-message.to_id.channel_id);
            }
            if (message.out) {
            }
            num = (Integer) concurrentHashMap3.get(Long.valueOf(message.dialog_id));
            if (num != null) {
                j = j2;
            } else {
                j = j2;
                num = Integer.valueOf(MessagesStorage.getInstance(messagesController.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                concurrentHashMap3.put(Long.valueOf(message.dialog_id), num);
            }
            if (num.intValue() >= message.id) {
            }
            message.unread = z7;
            if (message.dialog_id == ((long) size2)) {
                message.unread = false;
                message.media_unread = false;
                message.out = true;
            }
            arrayList9 = arrayList21;
            messageObject = new MessageObject(messagesController.currentAccount, message, concurrentHashMap, abstractMap, messagesController.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
            if (messageObject.type == 11) {
                size6 = i2 | 8;
            } else if (messageObject.type != 10) {
                i4 = i2;
                if (longSparseArray4 == null) {
                    longSparseArray4 = new LongSparseArray();
                }
                arrayList20 = (ArrayList) longSparseArray4.get(message.dialog_id);
                if (arrayList20 == null) {
                    arrayList20 = new ArrayList();
                    longSparseArray4.put(message.dialog_id, arrayList20);
                }
                arrayList20.add(messageObject);
                if (messageObject.isOut()) {
                }
                arrayList19 = arrayList17;
                arrayList17 = arrayList19;
                arrayList14 = arrayList11;
                longSparseArray5 = longSparseArray;
                arrayList13 = arrayList9;
                arrayList11 = arrayList6;
                sparseIntArray4 = sparseIntArray;
                sparseArray3 = sparseArray;
                sparseLongArray4 = sparseLongArray;
                longSparseArray6 = longSparseArray3;
                sparseIntArray5 = sparseIntArray3;
                sparseLongArray3 = sparseLongArray2;
                size = i5 + 1;
                size4 = i6;
                arrayList12 = arrayList7;
                currentTimeMillis = j;
            } else {
                size6 = i2 | 16;
            }
            i4 = size6;
            if (longSparseArray4 == null) {
                longSparseArray4 = new LongSparseArray();
            }
            arrayList20 = (ArrayList) longSparseArray4.get(message.dialog_id);
            if (arrayList20 == null) {
                arrayList20 = new ArrayList();
                longSparseArray4.put(message.dialog_id, arrayList20);
            }
            arrayList20.add(messageObject);
            if (messageObject.isOut()) {
            }
            arrayList19 = arrayList17;
            arrayList17 = arrayList19;
            arrayList14 = arrayList11;
            longSparseArray5 = longSparseArray;
            arrayList13 = arrayList9;
            arrayList11 = arrayList6;
            sparseIntArray4 = sparseIntArray;
            sparseArray3 = sparseArray;
            sparseLongArray4 = sparseLongArray;
            longSparseArray6 = longSparseArray3;
            sparseIntArray5 = sparseIntArray3;
            sparseLongArray3 = sparseLongArray2;
            size = i5 + 1;
            size4 = i6;
            arrayList12 = arrayList7;
            currentTimeMillis = j;
        }
        arrayList6 = arrayList11;
        sparseArray = sparseArray3;
        sparseIntArray = sparseIntArray4;
        sparseLongArray2 = sparseLongArray3;
        arrayList7 = arrayList12;
        sparseLongArray = sparseLongArray4;
        arrayList9 = arrayList13;
        arrayList11 = arrayList14;
        longSparseArray = longSparseArray5;
        longSparseArray3 = longSparseArray6;
        sparseIntArray3 = sparseIntArray5;
        i2 = i4;
        if (longSparseArray4 != null) {
            size4 = longSparseArray4.size();
            for (size2 = 0; size2 < size4; size2++) {
                if (updatePrintingUsersWithNewMessages(longSparseArray4.keyAt(size2), (ArrayList) longSparseArray4.valueAt(size2))) {
                    z6 = true;
                }
            }
        }
        z3 = z6;
        if (z3) {
            updatePrintingStrings();
        }
        if (arrayList9 != null) {
            ContactsController.getInstance(messagesController.currentAccount).processContactsUpdates(arrayList9, concurrentHashMap);
        }
        if (arrayList17 != null) {
            arrayList20 = arrayList17;
            MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$131$1 */
                class C03101 implements Runnable {
                    C03101() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList20, true, false);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03101());
                }
            });
        }
        if (arrayList11 != null) {
            StatsController.getInstance(messagesController.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, arrayList11.size());
            MessagesStorage.getInstance(messagesController.currentAccount).putMessages(arrayList11, true, true, false, DownloadController.getInstance(messagesController.currentAccount).getAutodownloadMask());
        }
        if (longSparseArray != null) {
            size4 = longSparseArray.size();
            for (size5 = 0; size5 < size4; size5++) {
                messages_Messages tL_messages_messages = new TL_messages_messages();
                arrayList10 = (ArrayList) longSparseArray.valueAt(size5);
                size6 = arrayList10.size();
                for (int i14 = 0; i14 < size6; i14++) {
                    tL_messages_messages.messages.add(((MessageObject) arrayList10.get(i14)).messageOwner);
                }
                MessagesStorage.getInstance(messagesController.currentAccount).putMessages(tL_messages_messages, longSparseArray.keyAt(size5), -2, 0, false);
            }
        }
        if (sparseArray4 != null) {
            sparseArray2 = sparseArray4;
            MessagesStorage.getInstance(messagesController.currentAccount).putChannelViews(sparseArray2, true);
        } else {
            sparseArray2 = sparseArray4;
        }
        arrayList13 = arrayList6;
        SparseArray sparseArray6 = sparseArray;
        size2 = i2;
        longSparseArray5 = longSparseArray;
        arrayList8 = arrayList16;
        LongSparseArray longSparseArray9 = longSparseArray3;
        longSparseArray2 = longSparseArray9;
        final LongSparseArray longSparseArray10 = longSparseArray4;
        longSparseArray4 = longSparseArray9;
        longSparseArray9 = longSparseArray5;
        SparseIntArray sparseIntArray7 = sparseIntArray3;
        LongSparseArray longSparseArray11 = longSparseArray4;
        final ArrayList arrayList28 = arrayList15;
        SparseIntArray sparseIntArray8 = sparseIntArray;
        SparseLongArray sparseLongArray5 = sparseLongArray2;
        sparseArray2 = sparseArray2;
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$132$3 */
            class C18143 implements RequestDelegate {
                C18143() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tLObject != null) {
                        MessagesController.this.processUpdates((Updates) tLObject, false);
                    }
                }
            }

            public void run() {
                int i;
                int i2;
                long j;
                ArrayList arrayList;
                ArrayList arrayList2;
                int size;
                long j2;
                int i3;
                int i4;
                int i5;
                int i6;
                Object obj;
                Object obj2;
                MessageObject messageObject;
                MessageObject messageObject2;
                int i7 = size2;
                ArrayList arrayList3 = null;
                int i8 = 2;
                int i9 = 1;
                boolean z = false;
                if (arrayList8 != null) {
                    ArrayList arrayList4;
                    ArrayList arrayList5;
                    ArrayList arrayList6 = new ArrayList();
                    ArrayList arrayList7 = new ArrayList();
                    int size2 = arrayList8.size();
                    i = i7;
                    Editor editor = null;
                    i7 = 0;
                    i2 = i7;
                    while (i7 < size2) {
                        int i10;
                        Update update = (Update) arrayList8.get(i7);
                        if (update instanceof TL_updatePrivacy) {
                            TL_updatePrivacy tL_updatePrivacy = (TL_updatePrivacy) update;
                            if (tL_updatePrivacy.key instanceof TL_privacyKeyStatusTimestamp) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(tL_updatePrivacy.rules, z);
                            } else if (tL_updatePrivacy.key instanceof TL_privacyKeyChatInvite) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(tL_updatePrivacy.rules, i9);
                            } else if (tL_updatePrivacy.key instanceof TL_privacyKeyPhoneCall) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(tL_updatePrivacy.rules, i8);
                            }
                        } else if (update instanceof TL_updateUserStatus) {
                            TL_updateUserStatus tL_updateUserStatus = (TL_updateUserStatus) update;
                            User user = MessagesController.this.getUser(Integer.valueOf(tL_updateUserStatus.user_id));
                            if (tL_updateUserStatus.status instanceof TL_userStatusRecently) {
                                tL_updateUserStatus.status.expires = -100;
                            } else if (tL_updateUserStatus.status instanceof TL_userStatusLastWeek) {
                                tL_updateUserStatus.status.expires = -101;
                            } else if (tL_updateUserStatus.status instanceof TL_userStatusLastMonth) {
                                tL_updateUserStatus.status.expires = -102;
                            }
                            if (user != null) {
                                user.id = tL_updateUserStatus.user_id;
                                user.status = tL_updateUserStatus.status;
                            }
                            r5 = new TL_user();
                            r5.id = tL_updateUserStatus.user_id;
                            r5.status = tL_updateUserStatus.status;
                            arrayList7.add(r5);
                            if (tL_updateUserStatus.user_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                                NotificationsController.getInstance(MessagesController.this.currentAccount).setLastOnlineFromOtherDevice(tL_updateUserStatus.status.expires);
                            }
                        } else if (update instanceof TL_updateUserName) {
                            TL_updateUserName tL_updateUserName = (TL_updateUserName) update;
                            r5 = MessagesController.this.getUser(Integer.valueOf(tL_updateUserName.user_id));
                            if (r5 != null) {
                                if (!UserObject.isContact(r5)) {
                                    r5.first_name = tL_updateUserName.first_name;
                                    r5.last_name = tL_updateUserName.last_name;
                                }
                                if (!TextUtils.isEmpty(r5.username)) {
                                    MessagesController.this.objectsByUsernames.remove(r5.username);
                                }
                                if (TextUtils.isEmpty(tL_updateUserName.username)) {
                                    MessagesController.this.objectsByUsernames.put(tL_updateUserName.username, r5);
                                }
                                r5.username = tL_updateUserName.username;
                            }
                            r5 = new TL_user();
                            r5.id = tL_updateUserName.user_id;
                            r5.first_name = tL_updateUserName.first_name;
                            r5.last_name = tL_updateUserName.last_name;
                            r5.username = tL_updateUserName.username;
                            arrayList6.add(r5);
                        } else if (update instanceof TL_updateDialogPinned) {
                            TL_updateDialogPinned tL_updateDialogPinned = (TL_updateDialogPinned) update;
                            if (tL_updateDialogPinned.peer instanceof TL_dialogPeer) {
                                long j3;
                                Peer peer = ((TL_dialogPeer) tL_updateDialogPinned.peer).peer;
                                if (peer instanceof TL_peerUser) {
                                    j3 = (long) peer.user_id;
                                } else if (peer instanceof TL_peerChat) {
                                    j3 = (long) (-peer.chat_id);
                                } else {
                                    j3 = (long) (-peer.channel_id);
                                }
                                j = j3;
                            } else {
                                j = 0;
                            }
                            if (!MessagesController.this.pinDialog(j, tL_updateDialogPinned.pinned, null, -1)) {
                                UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = z;
                                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(z);
                                MessagesController.this.loadPinnedDialogs(j, arrayList3);
                            }
                        } else {
                            if (update instanceof TL_updatePinnedDialogs) {
                                TL_updatePinnedDialogs tL_updatePinnedDialogs = (TL_updatePinnedDialogs) update;
                                UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = z;
                                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(z);
                                if ((tL_updatePinnedDialogs.flags & i9) != 0) {
                                    arrayList = new ArrayList();
                                    arrayList2 = tL_updatePinnedDialogs.order;
                                    size = arrayList2.size();
                                    int i11 = z;
                                    while (i11 < size) {
                                        long j4;
                                        DialogPeer dialogPeer = (DialogPeer) arrayList2.get(i11);
                                        if (dialogPeer instanceof TL_dialogPeer) {
                                            Peer peer2 = ((TL_dialogPeer) dialogPeer).peer;
                                            if (peer2.user_id != 0) {
                                                arrayList4 = arrayList7;
                                                j4 = (long) peer2.user_id;
                                            } else {
                                                arrayList4 = arrayList7;
                                                j4 = peer2.chat_id != 0 ? (long) (-peer2.chat_id) : (long) (-peer2.channel_id);
                                            }
                                        } else {
                                            arrayList4 = arrayList7;
                                            j4 = 0;
                                        }
                                        arrayList.add(Long.valueOf(j4));
                                        i11++;
                                        arrayList7 = arrayList4;
                                    }
                                    arrayList4 = arrayList7;
                                } else {
                                    arrayList4 = arrayList7;
                                    arrayList = null;
                                }
                                MessagesController.this.loadPinnedDialogs(0, arrayList);
                                i10 = i7;
                                arrayList5 = arrayList6;
                            } else {
                                arrayList4 = arrayList7;
                                User user2;
                                if (update instanceof TL_updateUserPhoto) {
                                    TL_updateUserPhoto tL_updateUserPhoto = (TL_updateUserPhoto) update;
                                    user2 = MessagesController.this.getUser(Integer.valueOf(tL_updateUserPhoto.user_id));
                                    if (user2 != null) {
                                        user2.photo = tL_updateUserPhoto.photo;
                                    }
                                    user2 = new TL_user();
                                    user2.id = tL_updateUserPhoto.user_id;
                                    user2.photo = tL_updateUserPhoto.photo;
                                    arrayList6.add(user2);
                                } else if (update instanceof TL_updateUserPhone) {
                                    TL_updateUserPhone tL_updateUserPhone = (TL_updateUserPhone) update;
                                    user2 = MessagesController.this.getUser(Integer.valueOf(tL_updateUserPhone.user_id));
                                    if (user2 != null) {
                                        user2.phone = tL_updateUserPhone.phone;
                                        Utilities.phoneBookQueue.postRunnable(new Runnable() {
                                            public void run() {
                                                ContactsController.getInstance(MessagesController.this.currentAccount).addContactToPhoneBook(user2, true);
                                            }
                                        });
                                    }
                                    user2 = new TL_user();
                                    user2.id = tL_updateUserPhone.user_id;
                                    user2.phone = tL_updateUserPhone.phone;
                                    arrayList6.add(user2);
                                } else if (update instanceof TL_updateNotifySettings) {
                                    TL_updateNotifySettings tL_updateNotifySettings = (TL_updateNotifySettings) update;
                                    if ((tL_updateNotifySettings.notify_settings instanceof TL_peerNotifySettings) && (tL_updateNotifySettings.peer instanceof TL_notifyPeer)) {
                                        if (editor == null) {
                                            editor = MessagesController.this.notificationsPreferences.edit();
                                        }
                                        if (tL_updateNotifySettings.peer.peer.user_id != 0) {
                                            j2 = (long) tL_updateNotifySettings.peer.peer.user_id;
                                        } else if (tL_updateNotifySettings.peer.peer.chat_id != 0) {
                                            j2 = (long) (-tL_updateNotifySettings.peer.peer.chat_id);
                                        } else {
                                            j2 = (long) (-tL_updateNotifySettings.peer.peer.channel_id);
                                        }
                                        TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(j2);
                                        if (tL_dialog != null) {
                                            tL_dialog.notify_settings = tL_updateNotifySettings.notify_settings;
                                        }
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("silent_");
                                        stringBuilder.append(j2);
                                        editor.putBoolean(stringBuilder.toString(), tL_updateNotifySettings.notify_settings.silent);
                                        size = ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime();
                                        if (tL_updateNotifySettings.notify_settings.mute_until > size) {
                                            if (tL_updateNotifySettings.notify_settings.mute_until > size + 31536000) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("notify2_");
                                                stringBuilder.append(j2);
                                                editor.putInt(stringBuilder.toString(), 2);
                                                if (tL_dialog != null) {
                                                    tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                                }
                                                i3 = 0;
                                            } else {
                                                i3 = tL_updateNotifySettings.notify_settings.mute_until;
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("notify2_");
                                                stringBuilder.append(j2);
                                                editor.putInt(stringBuilder.toString(), 3);
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("notifyuntil_");
                                                stringBuilder.append(j2);
                                                editor.putInt(stringBuilder.toString(), tL_updateNotifySettings.notify_settings.mute_until);
                                                if (tL_dialog != null) {
                                                    tL_dialog.notify_settings.mute_until = i3;
                                                }
                                            }
                                            i10 = i7;
                                            arrayList5 = arrayList6;
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(j2, (((long) i3) << 32) | 1);
                                            NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(j2);
                                        } else {
                                            i10 = i7;
                                            arrayList5 = arrayList6;
                                            if (tL_dialog != null) {
                                                tL_dialog.notify_settings.mute_until = 0;
                                            }
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("notify2_");
                                            stringBuilder2.append(j2);
                                            editor.remove(stringBuilder2.toString());
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(j2, 0);
                                        }
                                    }
                                } else {
                                    i10 = i7;
                                    arrayList5 = arrayList6;
                                    if (update instanceof TL_updateChannel) {
                                        final TL_updateChannel tL_updateChannel = (TL_updateChannel) update;
                                        TL_dialog tL_dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(-((long) tL_updateChannel.channel_id));
                                        Chat chat = MessagesController.this.getChat(Integer.valueOf(tL_updateChannel.channel_id));
                                        if (chat != null) {
                                            if (tL_dialog2 == null && (chat instanceof TL_channel) && !chat.left) {
                                                Utilities.stageQueue.postRunnable(new Runnable() {
                                                    public void run() {
                                                        MessagesController.this.getChannelDifference(tL_updateChannel.channel_id, 1, 0, null);
                                                    }
                                                });
                                            } else if (chat.left && tL_dialog2 != null) {
                                                MessagesController messagesController = MessagesController.this;
                                                j2 = tL_dialog2.id;
                                                i7 = 0;
                                                messagesController.deleteDialog(j2, 0);
                                                i |= MessagesController.UPDATE_MASK_CHANNEL;
                                                MessagesController.this.loadFullChat(tL_updateChannel.channel_id, i7, true);
                                            }
                                        }
                                        i7 = 0;
                                        i |= MessagesController.UPDATE_MASK_CHANNEL;
                                        MessagesController.this.loadFullChat(tL_updateChannel.channel_id, i7, true);
                                    } else if (update instanceof TL_updateChatAdmins) {
                                        i |= MessagesController.UPDATE_MASK_CHAT_ADMINS;
                                    } else if (update instanceof TL_updateStickerSets) {
                                        TL_updateStickerSets tL_updateStickerSets = (TL_updateStickerSets) update;
                                        DataQuery.getInstance(MessagesController.this.currentAccount).loadStickers(0, false, true);
                                    } else if (update instanceof TL_updateStickerSetsOrder) {
                                        TL_updateStickerSetsOrder tL_updateStickerSetsOrder = (TL_updateStickerSetsOrder) update;
                                        DataQuery.getInstance(MessagesController.this.currentAccount).reorderStickers(tL_updateStickerSetsOrder.masks, tL_updateStickerSetsOrder.order);
                                    } else if (update instanceof TL_updateFavedStickers) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).loadRecents(2, false, false, true);
                                    } else if (update instanceof TL_updateContactsReset) {
                                        ContactsController.getInstance(MessagesController.this.currentAccount).forceImportContacts();
                                    } else if (update instanceof TL_updateNewStickerSet) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).addNewStickerSet(((TL_updateNewStickerSet) update).stickerset);
                                    } else if (update instanceof TL_updateSavedGifs) {
                                        MessagesController.this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).commit();
                                    } else if (update instanceof TL_updateRecentStickers) {
                                        MessagesController.this.emojiPreferences.edit().putLong("lastStickersLoadTime", 0).commit();
                                    } else if (update instanceof TL_updateDraftMessage) {
                                        long j5;
                                        TL_updateDraftMessage tL_updateDraftMessage = (TL_updateDraftMessage) update;
                                        Peer peer3 = tL_updateDraftMessage.peer;
                                        if (peer3.user_id != 0) {
                                            j5 = (long) peer3.user_id;
                                        } else if (peer3.channel_id != 0) {
                                            j5 = (long) (-peer3.channel_id);
                                        } else {
                                            j5 = (long) (-peer3.chat_id);
                                        }
                                        DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(j5, tL_updateDraftMessage.draft, null, true);
                                        i2 = 1;
                                    } else if (update instanceof TL_updateReadFeaturedStickers) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).markFaturedStickersAsRead(false);
                                    } else if (update instanceof TL_updatePhoneCall) {
                                        PhoneCall phoneCall = ((TL_updatePhoneCall) update).phone_call;
                                        VoIPService sharedInstance = VoIPService.getSharedInstance();
                                        if (BuildVars.LOGS_ENABLED) {
                                            StringBuilder stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("Received call in update: ");
                                            stringBuilder3.append(phoneCall);
                                            FileLog.m0d(stringBuilder3.toString());
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("call id ");
                                            stringBuilder3.append(phoneCall.id);
                                            FileLog.m0d(stringBuilder3.toString());
                                        }
                                        if (phoneCall instanceof TL_phoneCallRequested) {
                                            if (phoneCall.date + (MessagesController.this.callRingTimeout / 1000) >= ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime()) {
                                                StringBuilder stringBuilder4;
                                                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                                                if (sharedInstance == null && VoIPService.callIShouldHavePutIntoIntent == null) {
                                                    if (telephonyManager.getCallState() == 0) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            stringBuilder4 = new StringBuilder();
                                                            stringBuilder4.append("Starting service for call ");
                                                            stringBuilder4.append(phoneCall.id);
                                                            FileLog.m0d(stringBuilder4.toString());
                                                        }
                                                        VoIPService.callIShouldHavePutIntoIntent = phoneCall;
                                                        Intent intent = new Intent(ApplicationLoader.applicationContext, VoIPService.class);
                                                        intent.putExtra("is_outgoing", false);
                                                        intent.putExtra("user_id", phoneCall.participant_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId() ? phoneCall.admin_id : phoneCall.participant_id);
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
                                                    stringBuilder4 = new StringBuilder();
                                                    stringBuilder4.append("Auto-declining call ");
                                                    stringBuilder4.append(phoneCall.id);
                                                    stringBuilder4.append(" because there's already active one");
                                                    FileLog.m0d(stringBuilder4.toString());
                                                }
                                                TLObject tL_phone_discardCall = new TL_phone_discardCall();
                                                tL_phone_discardCall.peer = new TL_inputPhoneCall();
                                                tL_phone_discardCall.peer.access_hash = phoneCall.access_hash;
                                                tL_phone_discardCall.peer.id = phoneCall.id;
                                                tL_phone_discardCall.reason = new TL_phoneCallDiscardReasonBusy();
                                                ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(tL_phone_discardCall, new C18143());
                                            } else if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m0d("ignoring too old call");
                                            }
                                        } else if (sharedInstance != null && phoneCall != null) {
                                            sharedInstance.onCallUpdated(phoneCall);
                                        } else if (VoIPService.callIShouldHavePutIntoIntent != null) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m0d("Updated the call while the service is starting");
                                            }
                                            if (phoneCall.id == VoIPService.callIShouldHavePutIntoIntent.id) {
                                                VoIPService.callIShouldHavePutIntoIntent = phoneCall;
                                            }
                                        }
                                    } else if (!(update instanceof TL_updateGroupCall)) {
                                        boolean z2 = update instanceof TL_updateGroupCallParticipant;
                                    }
                                }
                                i10 = i7;
                                arrayList5 = arrayList6;
                            }
                            i7 = i10 + 1;
                            arrayList7 = arrayList4;
                            arrayList6 = arrayList5;
                            arrayList3 = null;
                            i8 = 2;
                            i9 = 1;
                            z = false;
                        }
                        i10 = i7;
                        arrayList5 = arrayList6;
                        arrayList4 = arrayList7;
                        i7 = i10 + 1;
                        arrayList7 = arrayList4;
                        arrayList6 = arrayList5;
                        arrayList3 = null;
                        i8 = 2;
                        i9 = 1;
                        z = false;
                    }
                    arrayList5 = arrayList6;
                    arrayList4 = arrayList7;
                    j = 0;
                    if (editor != null) {
                        editor.commit();
                        i4 = false;
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                    } else {
                        i4 = false;
                    }
                    i8 = 1;
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(arrayList4, true, true, true);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(arrayList5, i4, true, true);
                } else {
                    i8 = 1;
                    i4 = 0;
                    j = 0;
                    i = i7;
                    i2 = i4;
                }
                if (longSparseArray2 != null) {
                    NotificationCenter instance = NotificationCenter.getInstance(MessagesController.this.currentAccount);
                    i5 = NotificationCenter.didReceivedWebpagesInUpdates;
                    Object[] objArr = new Object[i8];
                    objArr[i4] = longSparseArray2;
                    instance.postNotificationName(i5, objArr);
                    i7 = longSparseArray2.size();
                    for (i5 = 0; i5 < i7; i5++) {
                        j2 = longSparseArray2.keyAt(i5);
                        ArrayList arrayList8 = (ArrayList) MessagesController.this.reloadingWebpagesPending.get(j2);
                        MessagesController.this.reloadingWebpagesPending.remove(j2);
                        if (arrayList8 != null) {
                            long j6;
                            WebPage webPage = (WebPage) longSparseArray2.valueAt(i5);
                            arrayList = new ArrayList();
                            if (!(webPage instanceof TL_webPage)) {
                                if (!(webPage instanceof TL_webPageEmpty)) {
                                    MessagesController.this.reloadingWebpagesPending.put(webPage.id, arrayList8);
                                    j6 = j;
                                    if (!arrayList.isEmpty()) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(arrayList, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j6), arrayList8);
                                    }
                                }
                            }
                            i3 = arrayList8.size();
                            j6 = j;
                            for (i6 = 0; i6 < i3; i6++) {
                                ((MessageObject) arrayList8.get(i6)).messageOwner.media.webpage = webPage;
                                if (i6 == 0) {
                                    j6 = ((MessageObject) arrayList8.get(i6)).getDialogId();
                                    ImageLoader.saveMessageThumbs(((MessageObject) arrayList8.get(i6)).messageOwner);
                                }
                                arrayList.add(((MessageObject) arrayList8.get(i6)).messageOwner);
                            }
                            if (!arrayList.isEmpty()) {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(arrayList, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j6), arrayList8);
                            }
                        }
                    }
                }
                if (longSparseArray10 != null) {
                    i7 = longSparseArray10.size();
                    for (i5 = 0; i5 < i7; i5++) {
                        MessagesController.this.updateInterfaceWithMessages(longSparseArray10.keyAt(i5), (ArrayList) longSparseArray10.valueAt(i5));
                    }
                } else if (i2 != 0) {
                    MessagesController.this.sortDialogs(null);
                } else {
                    obj = null;
                    if (longSparseArray9 != null) {
                        i5 = longSparseArray9.size();
                        obj2 = obj;
                        for (i7 = 0; i7 < i5; i7++) {
                            j2 = longSparseArray9.keyAt(i7);
                            arrayList2 = (ArrayList) longSparseArray9.valueAt(i7);
                            messageObject = (MessageObject) MessagesController.this.dialogMessage.get(j2);
                            if (messageObject != null) {
                                i3 = arrayList2.size();
                                i6 = 0;
                                while (i6 < i3) {
                                    messageObject2 = (MessageObject) arrayList2.get(i6);
                                    if (messageObject.getId() != messageObject2.getId()) {
                                        MessagesController.this.dialogMessage.put(j2, messageObject2);
                                        if (messageObject2.messageOwner.to_id != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                                            MessagesController.this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                                        }
                                        obj2 = 1;
                                    } else {
                                        i6++;
                                    }
                                }
                            }
                            DataQuery.getInstance(MessagesController.this.currentAccount).loadReplyMessagesForMessages(arrayList2, j2);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j2), arrayList2);
                        }
                        obj = obj2;
                    }
                    if (obj != null) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                    if (z3) {
                        i |= 64;
                    }
                    if (arrayList9 != null) {
                        i = (i | 1) | 128;
                    }
                    if (arrayList28 != null) {
                        i7 = arrayList28.size();
                        for (i5 = 0; i5 < i7; i5++) {
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatParticipants((ChatParticipants) arrayList28.get(i5));
                        }
                    }
                    if (sparseArray2 == null) {
                        instance = NotificationCenter.getInstance(MessagesController.this.currentAccount);
                        i5 = NotificationCenter.didUpdatedMessagesViews;
                        i4 = 1;
                        Object[] objArr2 = new Object[1];
                        size = 0;
                        objArr2[0] = sparseArray2;
                        instance.postNotificationName(i5, objArr2);
                    } else {
                        i4 = 1;
                        size = 0;
                    }
                    if (i != 0) {
                        instance = NotificationCenter.getInstance(MessagesController.this.currentAccount);
                        i5 = NotificationCenter.updateInterfaces;
                        Object[] objArr3 = new Object[i4];
                        objArr3[size] = Integer.valueOf(i);
                        instance.postNotificationName(i5, objArr3);
                    }
                }
                obj = 1;
                if (longSparseArray9 != null) {
                    i5 = longSparseArray9.size();
                    obj2 = obj;
                    for (i7 = 0; i7 < i5; i7++) {
                        j2 = longSparseArray9.keyAt(i7);
                        arrayList2 = (ArrayList) longSparseArray9.valueAt(i7);
                        messageObject = (MessageObject) MessagesController.this.dialogMessage.get(j2);
                        if (messageObject != null) {
                            i3 = arrayList2.size();
                            i6 = 0;
                            while (i6 < i3) {
                                messageObject2 = (MessageObject) arrayList2.get(i6);
                                if (messageObject.getId() != messageObject2.getId()) {
                                    i6++;
                                } else {
                                    MessagesController.this.dialogMessage.put(j2, messageObject2);
                                    MessagesController.this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                                    obj2 = 1;
                                }
                            }
                        }
                        DataQuery.getInstance(MessagesController.this.currentAccount).loadReplyMessagesForMessages(arrayList2, j2);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j2), arrayList2);
                    }
                    obj = obj2;
                }
                if (obj != null) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
                if (z3) {
                    i |= 64;
                }
                if (arrayList9 != null) {
                    i = (i | 1) | 128;
                }
                if (arrayList28 != null) {
                    i7 = arrayList28.size();
                    for (i5 = 0; i5 < i7; i5++) {
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatParticipants((ChatParticipants) arrayList28.get(i5));
                    }
                }
                if (sparseArray2 == null) {
                    i4 = 1;
                    size = 0;
                } else {
                    instance = NotificationCenter.getInstance(MessagesController.this.currentAccount);
                    i5 = NotificationCenter.didUpdatedMessagesViews;
                    i4 = 1;
                    Object[] objArr22 = new Object[1];
                    size = 0;
                    objArr22[0] = sparseArray2;
                    instance.postNotificationName(i5, objArr22);
                }
                if (i != 0) {
                    instance = NotificationCenter.getInstance(MessagesController.this.currentAccount);
                    i5 = NotificationCenter.updateInterfaces;
                    Object[] objArr32 = new Object[i4];
                    objArr32[size] = Integer.valueOf(i);
                    instance.postNotificationName(i5, objArr32);
                }
            }
        });
        final SparseLongArray sparseLongArray6 = sparseLongArray;
        final SparseLongArray sparseLongArray7 = sparseLongArray5;
        final SparseIntArray sparseIntArray9 = sparseIntArray8;
        final ArrayList arrayList29 = arrayList7;
        final SparseArray sparseArray7 = sparseArray6;
        sparseIntArray4 = sparseIntArray7;
        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$133$1 */
            class C03131 implements Runnable {
                C03131() {
                }

                public void run() {
                    int i;
                    int size;
                    int i2;
                    int keyAt;
                    int valueAt;
                    long j;
                    MessageObject messageObject;
                    ArrayList arrayList;
                    int i3;
                    MessageObject messageObject2;
                    int i4;
                    if (sparseLongArray6 == null) {
                        if (sparseLongArray7 == null) {
                            i = 0;
                            if (sparseIntArray9 != null) {
                                size = sparseIntArray9.size();
                                for (i2 = 0; i2 < size; i2++) {
                                    keyAt = sparseIntArray9.keyAt(i2);
                                    valueAt = sparseIntArray9.valueAt(i2);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(keyAt), Integer.valueOf(valueAt));
                                    j = ((long) keyAt) << 32;
                                    if (((TL_dialog) MessagesController.this.dialogs_dict.get(j)) != null) {
                                        messageObject = (MessageObject) MessagesController.this.dialogMessage.get(j);
                                        if (messageObject != null && messageObject.messageOwner.date <= valueAt) {
                                            messageObject.setIsRead();
                                            i |= 256;
                                        }
                                    }
                                }
                            }
                            if (arrayList29 != null) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList29);
                            }
                            if (sparseArray7 != null) {
                                size = sparseArray7.size();
                                for (i2 = 0; i2 < size; i2++) {
                                    keyAt = sparseArray7.keyAt(i2);
                                    arrayList = (ArrayList) sparseArray7.valueAt(i2);
                                    if (arrayList == null) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(keyAt));
                                        if (keyAt != 0) {
                                            keyAt = arrayList.size();
                                            for (i3 = 0; i3 < keyAt; i3++) {
                                                messageObject2 = (MessageObject) MessagesController.this.dialogMessagesByIds.get(((Integer) arrayList.get(i3)).intValue());
                                                if (messageObject2 != null) {
                                                    messageObject2.deleted = true;
                                                }
                                            }
                                        } else {
                                            messageObject = (MessageObject) MessagesController.this.dialogMessage.get((long) (-keyAt));
                                            if (messageObject != null) {
                                                i3 = arrayList.size();
                                                for (i4 = 0; i4 < i3; i4++) {
                                                    if (messageObject.getId() == ((Integer) arrayList.get(i4)).intValue()) {
                                                        messageObject.deleted = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedMessagesFromNotifications(sparseArray7);
                            }
                            if (sparseIntArray4 != null) {
                                size = sparseIntArray4.size();
                                for (i2 = 0; i2 < size; i2++) {
                                    keyAt = sparseIntArray4.keyAt(i2);
                                    valueAt = sparseIntArray4.valueAt(i2);
                                    j = (long) (-keyAt);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.historyCleared, Long.valueOf(j), Integer.valueOf(valueAt));
                                    messageObject = (MessageObject) MessagesController.this.dialogMessage.get(j);
                                    if (messageObject == null && messageObject.getId() <= valueAt) {
                                        messageObject.deleted = true;
                                        break;
                                    }
                                }
                                NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedHisoryFromNotifications(sparseIntArray4);
                            }
                            if (i != 0) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(i));
                            }
                        }
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesRead, sparseLongArray6, sparseLongArray7);
                    if (sparseLongArray6 != null) {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(sparseLongArray6, 0, 0, 0, false);
                        Editor edit = MessagesController.this.notificationsPreferences.edit();
                        i2 = sparseLongArray6.size();
                        keyAt = 0;
                        i = keyAt;
                        while (keyAt < i2) {
                            valueAt = sparseLongArray6.keyAt(keyAt);
                            i3 = (int) sparseLongArray6.valueAt(keyAt);
                            TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get((long) valueAt);
                            if (tL_dialog != null && tL_dialog.top_message > 0 && tL_dialog.top_message <= i3) {
                                MessageObject messageObject3 = (MessageObject) MessagesController.this.dialogMessage.get(tL_dialog.id);
                                if (!(messageObject3 == null || messageObject3.isOut())) {
                                    messageObject3.setIsRead();
                                    i |= 256;
                                }
                            }
                            if (valueAt != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("diditem");
                                stringBuilder.append(valueAt);
                                edit.remove(stringBuilder.toString());
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("diditemo");
                                stringBuilder.append(valueAt);
                                edit.remove(stringBuilder.toString());
                            }
                            keyAt++;
                        }
                        edit.commit();
                    } else {
                        i = 0;
                    }
                    if (sparseLongArray7 != null) {
                        size = sparseLongArray7.size();
                        for (i2 = 0; i2 < size; i2++) {
                            valueAt = (int) sparseLongArray7.valueAt(i2);
                            TL_dialog tL_dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get((long) sparseLongArray7.keyAt(i2));
                            if (tL_dialog2 != null && tL_dialog2.top_message > 0 && tL_dialog2.top_message <= valueAt) {
                                messageObject = (MessageObject) MessagesController.this.dialogMessage.get(tL_dialog2.id);
                                if (messageObject != null && messageObject.isOut()) {
                                    messageObject.setIsRead();
                                    i |= 256;
                                }
                            }
                        }
                    }
                    if (sparseIntArray9 != null) {
                        size = sparseIntArray9.size();
                        for (i2 = 0; i2 < size; i2++) {
                            keyAt = sparseIntArray9.keyAt(i2);
                            valueAt = sparseIntArray9.valueAt(i2);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(keyAt), Integer.valueOf(valueAt));
                            j = ((long) keyAt) << 32;
                            if (((TL_dialog) MessagesController.this.dialogs_dict.get(j)) != null) {
                                messageObject = (MessageObject) MessagesController.this.dialogMessage.get(j);
                                messageObject.setIsRead();
                                i |= 256;
                            }
                        }
                    }
                    if (arrayList29 != null) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList29);
                    }
                    if (sparseArray7 != null) {
                        size = sparseArray7.size();
                        for (i2 = 0; i2 < size; i2++) {
                            keyAt = sparseArray7.keyAt(i2);
                            arrayList = (ArrayList) sparseArray7.valueAt(i2);
                            if (arrayList == null) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(keyAt));
                                if (keyAt != 0) {
                                    messageObject = (MessageObject) MessagesController.this.dialogMessage.get((long) (-keyAt));
                                    if (messageObject != null) {
                                        i3 = arrayList.size();
                                        for (i4 = 0; i4 < i3; i4++) {
                                            if (messageObject.getId() == ((Integer) arrayList.get(i4)).intValue()) {
                                                messageObject.deleted = true;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    keyAt = arrayList.size();
                                    for (i3 = 0; i3 < keyAt; i3++) {
                                        messageObject2 = (MessageObject) MessagesController.this.dialogMessagesByIds.get(((Integer) arrayList.get(i3)).intValue());
                                        if (messageObject2 != null) {
                                            messageObject2.deleted = true;
                                        }
                                    }
                                }
                            }
                        }
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedMessagesFromNotifications(sparseArray7);
                    }
                    if (sparseIntArray4 != null) {
                        size = sparseIntArray4.size();
                        for (i2 = 0; i2 < size; i2++) {
                            keyAt = sparseIntArray4.keyAt(i2);
                            valueAt = sparseIntArray4.valueAt(i2);
                            j = (long) (-keyAt);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.historyCleared, Long.valueOf(j), Integer.valueOf(valueAt));
                            messageObject = (MessageObject) MessagesController.this.dialogMessage.get(j);
                            if (messageObject == null) {
                            }
                        }
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedHisoryFromNotifications(sparseIntArray4);
                    }
                    if (i != 0) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(i));
                    }
                }
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C03131());
            }
        });
        if (longSparseArray11 != null) {
            MessagesStorage.getInstance(messagesController.currentAccount).putWebPages(longSparseArray11);
        }
        TL_updateEncryptedMessagesRead tL_updateEncryptedMessagesRead2;
        if (sparseLongArray == null) {
            sparseLongArray3 = sparseLongArray5;
            if (sparseLongArray3 == null) {
                sparseIntArray2 = sparseIntArray8;
                if (sparseIntArray2 == null) {
                    if (arrayList7 == null) {
                        arrayList10 = arrayList7;
                        if (arrayList10 != null) {
                            MessagesStorage.getInstance(messagesController.currentAccount).markMessagesContentAsRead(arrayList10, ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime());
                        }
                        if (sparseArray6 != null) {
                            size4 = sparseArray6.size();
                            for (size5 = 0; size5 < size4; size5++) {
                                size2 = sparseArray6.keyAt(size5);
                                arrayList8 = (ArrayList) sparseArray6.valueAt(size5);
                                MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(arrayList8, MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(arrayList8, false, size2), false, size2);
                                    }
                                });
                            }
                        }
                        sparseIntArray2 = sparseIntArray7;
                        if (sparseIntArray2 != null) {
                            size4 = sparseIntArray2.size();
                            for (size2 = 0; size2 < size4; size2++) {
                                newMessageId = sparseIntArray2.keyAt(size2);
                                size6 = sparseIntArray2.valueAt(size2);
                                MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(newMessageId, size6, false), false, newMessageId);
                                    }
                                });
                            }
                        }
                        if (arrayList13 != null) {
                            size4 = arrayList13.size();
                            for (size5 = 0; size5 < size4; size5++) {
                                tL_updateEncryptedMessagesRead2 = (TL_updateEncryptedMessagesRead) arrayList13.get(size5);
                                MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(tL_updateEncryptedMessagesRead2.chat_id, tL_updateEncryptedMessagesRead2.max_date, tL_updateEncryptedMessagesRead2.date, 1, null);
                            }
                        }
                        return true;
                    }
                }
                if (sparseLongArray == null) {
                    if (arrayList7 != null) {
                        arrayList10 = arrayList7;
                        sparseLongArray4 = sparseLongArray;
                        z4 = true;
                        MessagesStorage.getInstance(messagesController.currentAccount).markMessagesAsRead(sparseLongArray4, sparseLongArray3, sparseIntArray2, z4);
                        if (arrayList10 != null) {
                            MessagesStorage.getInstance(messagesController.currentAccount).markMessagesContentAsRead(arrayList10, ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime());
                        }
                        if (sparseArray6 != null) {
                            size4 = sparseArray6.size();
                            for (size5 = 0; size5 < size4; size5++) {
                                size2 = sparseArray6.keyAt(size5);
                                arrayList8 = (ArrayList) sparseArray6.valueAt(size5);
                                MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                            }
                        }
                        sparseIntArray2 = sparseIntArray7;
                        if (sparseIntArray2 != null) {
                            size4 = sparseIntArray2.size();
                            for (size2 = 0; size2 < size4; size2++) {
                                newMessageId = sparseIntArray2.keyAt(size2);
                                size6 = sparseIntArray2.valueAt(size2);
                                MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                            }
                        }
                        if (arrayList13 != null) {
                            size4 = arrayList13.size();
                            for (size5 = 0; size5 < size4; size5++) {
                                tL_updateEncryptedMessagesRead2 = (TL_updateEncryptedMessagesRead) arrayList13.get(size5);
                                MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(tL_updateEncryptedMessagesRead2.chat_id, tL_updateEncryptedMessagesRead2.max_date, tL_updateEncryptedMessagesRead2.date, 1, null);
                            }
                        }
                        return true;
                    }
                }
                arrayList10 = arrayList7;
                sparseLongArray4 = sparseLongArray;
                z4 = true;
                MessagesStorage.getInstance(messagesController.currentAccount).updateDialogsWithReadMessages(sparseLongArray4, sparseLongArray3, arrayList10, true);
                MessagesStorage.getInstance(messagesController.currentAccount).markMessagesAsRead(sparseLongArray4, sparseLongArray3, sparseIntArray2, z4);
                if (arrayList10 != null) {
                    MessagesStorage.getInstance(messagesController.currentAccount).markMessagesContentAsRead(arrayList10, ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime());
                }
                if (sparseArray6 != null) {
                    size4 = sparseArray6.size();
                    for (size5 = 0; size5 < size4; size5++) {
                        size2 = sparseArray6.keyAt(size5);
                        arrayList8 = (ArrayList) sparseArray6.valueAt(size5);
                        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                    }
                }
                sparseIntArray2 = sparseIntArray7;
                if (sparseIntArray2 != null) {
                    size4 = sparseIntArray2.size();
                    for (size2 = 0; size2 < size4; size2++) {
                        newMessageId = sparseIntArray2.keyAt(size2);
                        size6 = sparseIntArray2.valueAt(size2);
                        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                    }
                }
                if (arrayList13 != null) {
                    size4 = arrayList13.size();
                    for (size5 = 0; size5 < size4; size5++) {
                        tL_updateEncryptedMessagesRead2 = (TL_updateEncryptedMessagesRead) arrayList13.get(size5);
                        MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(tL_updateEncryptedMessagesRead2.chat_id, tL_updateEncryptedMessagesRead2.max_date, tL_updateEncryptedMessagesRead2.date, 1, null);
                    }
                }
                return true;
            }
        }
        sparseLongArray3 = sparseLongArray5;
        sparseIntArray2 = sparseIntArray8;
        if (sparseLongArray == null) {
            if (arrayList7 != null) {
                arrayList10 = arrayList7;
                sparseLongArray4 = sparseLongArray;
                z4 = true;
                MessagesStorage.getInstance(messagesController.currentAccount).markMessagesAsRead(sparseLongArray4, sparseLongArray3, sparseIntArray2, z4);
                if (arrayList10 != null) {
                    MessagesStorage.getInstance(messagesController.currentAccount).markMessagesContentAsRead(arrayList10, ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime());
                }
                if (sparseArray6 != null) {
                    size4 = sparseArray6.size();
                    for (size5 = 0; size5 < size4; size5++) {
                        size2 = sparseArray6.keyAt(size5);
                        arrayList8 = (ArrayList) sparseArray6.valueAt(size5);
                        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                    }
                }
                sparseIntArray2 = sparseIntArray7;
                if (sparseIntArray2 != null) {
                    size4 = sparseIntArray2.size();
                    for (size2 = 0; size2 < size4; size2++) {
                        newMessageId = sparseIntArray2.keyAt(size2);
                        size6 = sparseIntArray2.valueAt(size2);
                        MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
                    }
                }
                if (arrayList13 != null) {
                    size4 = arrayList13.size();
                    for (size5 = 0; size5 < size4; size5++) {
                        tL_updateEncryptedMessagesRead2 = (TL_updateEncryptedMessagesRead) arrayList13.get(size5);
                        MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(tL_updateEncryptedMessagesRead2.chat_id, tL_updateEncryptedMessagesRead2.max_date, tL_updateEncryptedMessagesRead2.date, 1, null);
                    }
                }
                return true;
            }
        }
        arrayList10 = arrayList7;
        sparseLongArray4 = sparseLongArray;
        z4 = true;
        MessagesStorage.getInstance(messagesController.currentAccount).updateDialogsWithReadMessages(sparseLongArray4, sparseLongArray3, arrayList10, true);
        MessagesStorage.getInstance(messagesController.currentAccount).markMessagesAsRead(sparseLongArray4, sparseLongArray3, sparseIntArray2, z4);
        if (arrayList10 != null) {
            MessagesStorage.getInstance(messagesController.currentAccount).markMessagesContentAsRead(arrayList10, ConnectionsManager.getInstance(messagesController.currentAccount).getCurrentTime());
        }
        if (sparseArray6 != null) {
            size4 = sparseArray6.size();
            for (size5 = 0; size5 < size4; size5++) {
                size2 = sparseArray6.keyAt(size5);
                arrayList8 = (ArrayList) sparseArray6.valueAt(size5);
                MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
            }
        }
        sparseIntArray2 = sparseIntArray7;
        if (sparseIntArray2 != null) {
            size4 = sparseIntArray2.size();
            for (size2 = 0; size2 < size4; size2++) {
                newMessageId = sparseIntArray2.keyAt(size2);
                size6 = sparseIntArray2.valueAt(size2);
                MessagesStorage.getInstance(messagesController.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
            }
        }
        if (arrayList13 != null) {
            size4 = arrayList13.size();
            for (size5 = 0; size5 < size4; size5++) {
                tL_updateEncryptedMessagesRead2 = (TL_updateEncryptedMessagesRead) arrayList13.get(size5);
                MessagesStorage.getInstance(messagesController.currentAccount).createTaskForSecretChat(tL_updateEncryptedMessagesRead2.chat_id, tL_updateEncryptedMessagesRead2.max_date, tL_updateEncryptedMessagesRead2.date, 1, null);
            }
        }
        return true;
    }

    private boolean isNotifySettingsMuted(PeerNotifySettings peerNotifySettings) {
        return (!(peerNotifySettings instanceof TL_peerNotifySettings) || peerNotifySettings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) ? null : true;
    }

    public boolean isDialogMuted(long j) {
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(j);
        int i = sharedPreferences.getInt(stringBuilder.toString(), 0);
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
            int i;
            ArrayList arrayList2 = new ArrayList();
            arrayList = arrayList.iterator();
            while (arrayList.hasNext()) {
                MessageObject messageObject = (MessageObject) arrayList.next();
                if (!arrayList2.contains(Integer.valueOf(messageObject.messageOwner.from_id))) {
                    arrayList2.add(Integer.valueOf(messageObject.messageOwner.from_id));
                }
            }
            arrayList = (ArrayList) this.printingUsers.get(Long.valueOf(j));
            if (arrayList != null) {
                int i2 = 0;
                i = i2;
                while (i2 < arrayList.size()) {
                    if (arrayList2.contains(Integer.valueOf(((PrintingUser) arrayList.get(i2)).userId))) {
                        arrayList.remove(i2);
                        i2--;
                        if (arrayList.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(j));
                        }
                        i = true;
                    }
                    i2++;
                }
            } else {
                i = false;
            }
            if (i != 0) {
                return true;
            }
        }
        return false;
    }

    protected void updateInterfaceWithMessages(long j, ArrayList<MessageObject> arrayList) {
        updateInterfaceWithMessages(j, arrayList, false);
    }

    protected void updateInterfaceWithMessages(long j, ArrayList<MessageObject> arrayList, boolean z) {
        MessagesController messagesController = this;
        long j2 = j;
        ArrayList<MessageObject> arrayList2 = arrayList;
        if (arrayList2 != null) {
            if (!arrayList.isEmpty()) {
                int i = 1;
                int i2 = ((int) j2) == 0 ? 1 : false;
                int i3 = 0;
                int i4 = i3;
                int i5 = i4;
                MessageObject messageObject = null;
                while (i3 < arrayList.size()) {
                    MessageObject messageObject2 = (MessageObject) arrayList2.get(i3);
                    if (messageObject == null || ((i2 == 0 && messageObject2.getId() > messageObject.getId()) || (((i2 != 0 || (messageObject2.getId() < 0 && messageObject.getId() < 0)) && messageObject2.getId() < messageObject.getId()) || messageObject2.messageOwner.date > messageObject.messageOwner.date))) {
                        if (messageObject2.messageOwner.to_id.channel_id != 0) {
                            i4 = messageObject2.messageOwner.to_id.channel_id;
                        }
                        messageObject = messageObject2;
                    }
                    if (!(!messageObject2.isOut() || messageObject2.isSending() || messageObject2.isForwarded())) {
                        if (messageObject2.isNewGif()) {
                            DataQuery.getInstance(messagesController.currentAccount).addRecentGif(messageObject2.messageOwner.media.document, messageObject2.messageOwner.date);
                        } else if (messageObject2.isSticker()) {
                            DataQuery.getInstance(messagesController.currentAccount).addRecentSticker(0, messageObject2.messageOwner.media.document, messageObject2.messageOwner.date, false);
                        }
                    }
                    if (messageObject2.isOut() && messageObject2.isSent()) {
                        i5 = 1;
                    }
                    i3++;
                }
                DataQuery.getInstance(messagesController.currentAccount).loadReplyMessagesForMessages(arrayList2, j2);
                NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.didReceivedNewMessages, Long.valueOf(j), arrayList2);
                if (messageObject != null) {
                    TL_dialog tL_dialog = (TL_dialog) messagesController.dialogs_dict.get(j2);
                    if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                        if (tL_dialog != null) {
                            messagesController.dialogs.remove(tL_dialog);
                            messagesController.dialogsServerOnly.remove(tL_dialog);
                            messagesController.dialogsGroupsOnly.remove(tL_dialog);
                            messagesController.dialogs_dict.remove(tL_dialog.id);
                            messagesController.dialogs_read_inbox_max.remove(Long.valueOf(tL_dialog.id));
                            messagesController.dialogs_read_outbox_max.remove(Long.valueOf(tL_dialog.id));
                            messagesController.nextDialogsCacheOffset--;
                            messagesController.dialogMessage.remove(tL_dialog.id);
                            MessageObject messageObject3 = (MessageObject) messagesController.dialogMessagesByIds.get(tL_dialog.top_message);
                            messagesController.dialogMessagesByIds.remove(tL_dialog.top_message);
                            if (!(messageObject3 == null || messageObject3.messageOwner.random_id == 0)) {
                                messagesController.dialogMessagesByRandomIds.remove(messageObject3.messageOwner.random_id);
                            }
                            tL_dialog.top_message = 0;
                            NotificationsController.getInstance(messagesController.currentAccount).removeNotificationsForDialog(tL_dialog.id);
                            NotificationCenter.getInstance(messagesController.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                        }
                        return;
                    }
                    if (tL_dialog != null) {
                        if ((tL_dialog.top_message <= 0 || messageObject.getId() <= 0 || messageObject.getId() <= tL_dialog.top_message) && ((tL_dialog.top_message >= 0 || messageObject.getId() >= 0 || messageObject.getId() >= tL_dialog.top_message) && messagesController.dialogMessage.indexOfKey(j2) >= 0 && tL_dialog.top_message >= 0)) {
                            if (tL_dialog.last_message_date <= messageObject.messageOwner.date) {
                            }
                        }
                        MessageObject messageObject4 = (MessageObject) messagesController.dialogMessagesByIds.get(tL_dialog.top_message);
                        messagesController.dialogMessagesByIds.remove(tL_dialog.top_message);
                        if (!(messageObject4 == null || messageObject4.messageOwner.random_id == 0)) {
                            messagesController.dialogMessagesByRandomIds.remove(messageObject4.messageOwner.random_id);
                        }
                        tL_dialog.top_message = messageObject.getId();
                        if (z) {
                            i = 0;
                        } else {
                            tL_dialog.last_message_date = messageObject.messageOwner.date;
                        }
                        messagesController.dialogMessage.put(j2, messageObject);
                        if (messageObject.messageOwner.to_id.channel_id == 0) {
                            messagesController.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                            if (messageObject.messageOwner.random_id != 0) {
                                messagesController.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                            }
                        }
                        if (i != 0) {
                            sortDialogs(null);
                        }
                        if (i5 != 0) {
                            DataQuery.getInstance(messagesController.currentAccount).increasePeerRaiting(j2);
                        }
                    } else if (!z) {
                        Chat chat = getChat(Integer.valueOf(i4));
                        if ((i4 == 0 || chat != null) && (chat == null || !chat.left)) {
                            TL_dialog tL_dialog2 = new TL_dialog();
                            tL_dialog2.id = j2;
                            tL_dialog2.unread_count = 0;
                            tL_dialog2.top_message = messageObject.getId();
                            tL_dialog2.last_message_date = messageObject.messageOwner.date;
                            tL_dialog2.flags = ChatObject.isChannel(chat);
                            messagesController.dialogs_dict.put(j2, tL_dialog2);
                            messagesController.dialogs.add(tL_dialog2);
                            messagesController.dialogMessage.put(j2, messageObject);
                            if (messageObject.messageOwner.to_id.channel_id == 0) {
                                messagesController.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                if (messageObject.messageOwner.random_id != 0) {
                                    messagesController.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                }
                            }
                            messagesController.nextDialogsCacheOffset++;
                            if (i != 0) {
                                sortDialogs(null);
                            }
                            if (i5 != 0) {
                                DataQuery.getInstance(messagesController.currentAccount).increasePeerRaiting(j2);
                            }
                        }
                        return;
                    }
                    i = 0;
                    if (i != 0) {
                        sortDialogs(null);
                    }
                    if (i5 != 0) {
                        DataQuery.getInstance(messagesController.currentAccount).increasePeerRaiting(j2);
                    }
                }
            }
        }
    }

    public void sortDialogs(SparseArray<Chat> sparseArray) {
        this.dialogsServerOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsForward.clear();
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        Collections.sort(this.dialogs, this.dialogComparator);
        int i = 0;
        int i2 = i;
        while (i < this.dialogs.size()) {
            TL_dialog tL_dialog = (TL_dialog) this.dialogs.get(i);
            int i3 = (int) (tL_dialog.id >> 32);
            int i4 = (int) tL_dialog.id;
            if (i4 == clientUserId) {
                this.dialogsForward.add(0, tL_dialog);
                i2 = 1;
            } else {
                this.dialogsForward.add(tL_dialog);
            }
            if (!(i4 == 0 || i3 == 1)) {
                this.dialogsServerOnly.add(tL_dialog);
                Chat chat;
                if (DialogObject.isChannel(tL_dialog)) {
                    chat = getChat(Integer.valueOf(-i4));
                    if (chat != null && ((chat.megagroup && chat.admin_rights != null && (chat.admin_rights.post_messages || chat.admin_rights.add_admins)) || chat.creator)) {
                        this.dialogsGroupsOnly.add(tL_dialog);
                    }
                } else if (i4 < 0) {
                    if (sparseArray != null) {
                        chat = (Chat) sparseArray.get(-i4);
                        if (!(chat == null || chat.migrated_to == null)) {
                            this.dialogs.remove(i);
                            i--;
                        }
                    }
                    this.dialogsGroupsOnly.add(tL_dialog);
                }
            }
            i++;
        }
        if (i2 == 0) {
            sparseArray = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (sparseArray != null) {
                TL_dialog tL_dialog2 = new TL_dialog();
                tL_dialog2.id = (long) sparseArray.id;
                tL_dialog2.notify_settings = new TL_peerNotifySettings();
                tL_dialog2.peer = new TL_peerUser();
                tL_dialog2.peer.user_id = sparseArray.id;
                this.dialogsForward.add(0, tL_dialog2);
            }
        }
    }

    private static String getRestrictionReason(String str) {
        if (str != null) {
            if (str.length() != 0) {
                int indexOf = str.indexOf(": ");
                if (indexOf > 0) {
                    String substring = str.substring(0, indexOf);
                    if (substring.contains("-all") || substring.contains("-android")) {
                        return str.substring(indexOf + 2);
                    }
                }
                return null;
            }
        }
        return null;
    }

    private static void showCantOpenAlert(BaseFragment baseFragment, String str) {
        if (baseFragment != null) {
            if (baseFragment.getParentActivity() != null) {
                Builder builder = new Builder(baseFragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                builder.setMessage(str);
                baseFragment.showDialog(builder.create());
            }
        }
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment) {
        return checkCanOpenChat(bundle, baseFragment, null);
    }

    public boolean checkCanOpenChat(final Bundle bundle, final BaseFragment baseFragment, MessageObject messageObject) {
        if (bundle != null) {
            if (baseFragment != null) {
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
                }
                if (!(i3 == 0 || messageObject == null || chat == null || chat.access_hash != 0)) {
                    i = (int) messageObject.getDialogId();
                    if (i != 0) {
                        TLObject tL_channels_getMessages;
                        final Dialog alertDialog = new AlertDialog(baseFragment.getParentActivity(), 1);
                        alertDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        if (i < 0) {
                            chat = getChat(Integer.valueOf(-i));
                        }
                        if (i <= 0) {
                            if (ChatObject.isChannel(chat)) {
                                Chat chat2 = getChat(Integer.valueOf(-i));
                                tL_channels_getMessages = new TL_channels_getMessages();
                                tL_channels_getMessages.channel = getInputChannel(chat2);
                                tL_channels_getMessages.id.add(Integer.valueOf(messageObject.getId()));
                                bundle = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new RequestDelegate() {
                                    public void run(final TLObject tLObject, TL_error tL_error) {
                                        if (tLObject != null) {
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    try {
                                                        alertDialog.dismiss();
                                                    } catch (Throwable e) {
                                                        FileLog.m3e(e);
                                                    }
                                                    messages_Messages messages_messages = (messages_Messages) tLObject;
                                                    MessagesController.this.putUsers(messages_messages.users, false);
                                                    MessagesController.this.putChats(messages_messages.chats, false);
                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                                                    baseFragment.presentFragment(new ChatActivity(bundle), true);
                                                }
                                            });
                                        }
                                    }
                                });
                                alertDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(bundle, true);
                                        try {
                                            dialogInterface.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                        if (baseFragment != null) {
                                            baseFragment.setVisibleDialog(0);
                                        }
                                    }
                                });
                                baseFragment.setVisibleDialog(alertDialog);
                                alertDialog.show();
                                return false;
                            }
                        }
                        tL_channels_getMessages = new TL_messages_getMessages();
                        tL_channels_getMessages.id.add(Integer.valueOf(messageObject.getId()));
                        bundle = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, /* anonymous class already generated */);
                        alertDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), /* anonymous class already generated */);
                        baseFragment.setVisibleDialog(alertDialog);
                        alertDialog.show();
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
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
                    z = i;
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

    public void openByUserName(String str, final BaseFragment baseFragment, final int i) {
        if (str != null) {
            if (baseFragment != null) {
                User user;
                Chat chat;
                final AlertDialog[] alertDialogArr;
                TLObject userOrChat = getUserOrChat(str);
                if (userOrChat instanceof User) {
                    user = (User) userOrChat;
                    if (!user.min) {
                        chat = null;
                        if (user == null) {
                            openChatOrProfileWith(user, null, baseFragment, i, false);
                        } else if (chat == null) {
                            openChatOrProfileWith(null, chat, baseFragment, 1, false);
                        } else if (baseFragment.getParentActivity() == null) {
                            alertDialogArr = new AlertDialog[]{new AlertDialog(baseFragment.getParentActivity(), 1)};
                            userOrChat = new TL_contacts_resolveUsername();
                            userOrChat.username = str;
                            str = ConnectionsManager.getInstance(this.currentAccount).sendRequest(userOrChat, new RequestDelegate() {
                                public void run(final TLObject tLObject, final TL_error tL_error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                                            /*
                                            r7 = this;
                                            r0 = 0;
                                            r1 = org.telegram.messenger.MessagesController.AnonymousClass138.this;	 Catch:{ Exception -> 0x000a }
                                            r1 = r1;	 Catch:{ Exception -> 0x000a }
                                            r1 = r1[r0];	 Catch:{ Exception -> 0x000a }
                                            r1.dismiss();	 Catch:{ Exception -> 0x000a }
                                        L_0x000a:
                                            r1 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r1 = r1;
                                            r2 = 0;
                                            r1[r0] = r2;
                                            r1 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r1 = r7;
                                            r1.setVisibleDialog(r2);
                                            r1 = r3;
                                            if (r1 != 0) goto L_0x007a;
                                        L_0x001c:
                                            r1 = r2;
                                            r1 = (org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer) r1;
                                            r3 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r3 = org.telegram.messenger.MessagesController.this;
                                            r4 = r1.users;
                                            r3.putUsers(r4, r0);
                                            r3 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r3 = org.telegram.messenger.MessagesController.this;
                                            r4 = r1.chats;
                                            r3.putChats(r4, r0);
                                            r3 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r3 = org.telegram.messenger.MessagesController.this;
                                            r3 = r3.currentAccount;
                                            r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
                                            r4 = r1.users;
                                            r5 = r1.chats;
                                            r6 = 1;
                                            r3.putUsersAndChats(r4, r5, r0, r6);
                                            r3 = r1.chats;
                                            r3 = r3.isEmpty();
                                            if (r3 != 0) goto L_0x005e;
                                        L_0x004e:
                                            r1 = r1.chats;
                                            r1 = r1.get(r0);
                                            r1 = (org.telegram.tgnet.TLRPC.Chat) r1;
                                            r3 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r3 = r7;
                                            org.telegram.messenger.MessagesController.openChatOrProfileWith(r2, r1, r3, r6, r0);
                                            goto L_0x00a7;
                                        L_0x005e:
                                            r3 = r1.users;
                                            r3 = r3.isEmpty();
                                            if (r3 != 0) goto L_0x00a7;
                                        L_0x0066:
                                            r1 = r1.users;
                                            r1 = r1.get(r0);
                                            r1 = (org.telegram.tgnet.TLRPC.User) r1;
                                            r3 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r3 = r7;
                                            r4 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r4 = r8;
                                            org.telegram.messenger.MessagesController.openChatOrProfileWith(r1, r2, r3, r4, r0);
                                            goto L_0x00a7;
                                        L_0x007a:
                                            r1 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r1 = r7;
                                            if (r1 == 0) goto L_0x00a7;
                                        L_0x0080:
                                            r1 = org.telegram.messenger.MessagesController.AnonymousClass138.this;
                                            r1 = r7;
                                            r1 = r1.getParentActivity();
                                            if (r1 == 0) goto L_0x00a7;
                                        L_0x008a:
                                            r1 = org.telegram.messenger.MessagesController.AnonymousClass138.this;	 Catch:{ Exception -> 0x00a3 }
                                            r1 = r7;	 Catch:{ Exception -> 0x00a3 }
                                            r1 = r1.getParentActivity();	 Catch:{ Exception -> 0x00a3 }
                                            r2 = "NoUsernameFound";	 Catch:{ Exception -> 0x00a3 }
                                            r3 = NUM; // 0x7f0c041c float:1.8611326E38 double:1.053097918E-314;	 Catch:{ Exception -> 0x00a3 }
                                            r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Exception -> 0x00a3 }
                                            r0 = android.widget.Toast.makeText(r1, r2, r0);	 Catch:{ Exception -> 0x00a3 }
                                            r0.show();	 Catch:{ Exception -> 0x00a3 }
                                            goto L_0x00a7;
                                        L_0x00a3:
                                            r0 = move-exception;
                                            org.telegram.messenger.FileLog.m3e(r0);
                                        L_0x00a7:
                                            return;
                                            */
                                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.138.1.run():void");
                                        }
                                    });
                                }
                            });
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.MessagesController$139$1 */
                                class C03161 implements OnClickListener {
                                    C03161() {
                                    }

                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(str, true);
                                        try {
                                            dialogInterface.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    }
                                }

                                public void run() {
                                    if (alertDialogArr[0] != null) {
                                        alertDialogArr[0].setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                                        alertDialogArr[0].setCanceledOnTouchOutside(false);
                                        alertDialogArr[0].setCancelable(false);
                                        alertDialogArr[0].setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new C03161());
                                        baseFragment.showDialog(alertDialogArr[0]);
                                    }
                                }
                            }, 500);
                        }
                    }
                } else if (userOrChat instanceof Chat) {
                    Chat chat2 = (Chat) userOrChat;
                    if (!chat2.min) {
                        chat = chat2;
                        user = null;
                        if (user == null) {
                            openChatOrProfileWith(user, null, baseFragment, i, false);
                        } else if (chat == null) {
                            openChatOrProfileWith(null, chat, baseFragment, 1, false);
                        } else if (baseFragment.getParentActivity() == null) {
                            alertDialogArr = new AlertDialog[]{new AlertDialog(baseFragment.getParentActivity(), 1)};
                            userOrChat = new TL_contacts_resolveUsername();
                            userOrChat.username = str;
                            str = ConnectionsManager.getInstance(this.currentAccount).sendRequest(userOrChat, /* anonymous class already generated */);
                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */, 500);
                        }
                    }
                }
                user = null;
                chat = user;
                if (user == null) {
                    openChatOrProfileWith(user, null, baseFragment, i, false);
                } else if (chat == null) {
                    openChatOrProfileWith(null, chat, baseFragment, 1, false);
                } else if (baseFragment.getParentActivity() == null) {
                    alertDialogArr = new AlertDialog[]{new AlertDialog(baseFragment.getParentActivity(), 1)};
                    userOrChat = new TL_contacts_resolveUsername();
                    userOrChat.username = str;
                    str = ConnectionsManager.getInstance(this.currentAccount).sendRequest(userOrChat, /* anonymous class already generated */);
                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */, 500);
                }
            }
        }
    }
}
