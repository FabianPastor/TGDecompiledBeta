package org.telegram.messenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.messenger.query.BotQuery;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.query.MessagesQuery;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.EncryptedMessage;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.NotifyPeer;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.SendMessageAction;
import org.telegram.tgnet.TLRPC.TL_account_registerDevice;
import org.telegram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC.TL_account_updateStatus;
import org.telegram.tgnet.TLRPC.TL_auth_logOut;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_botInfo;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channelRoleEditor;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import org.telegram.tgnet.TLRPC.TL_channels_editAbout;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editPhoto;
import org.telegram.tgnet.TLRPC.TL_channels_editTitle;
import org.telegram.tgnet.TLRPC.TL_channels_getFullChannel;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_channels_kickFromChannel;
import org.telegram.tgnet.TLRPC.TL_channels_leaveChannel;
import org.telegram.tgnet.TLRPC.TL_channels_readHistory;
import org.telegram.tgnet.TLRPC.TL_channels_toggleInvites;
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
import org.telegram.tgnet.TLRPC.TL_disabledFeature;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_appChangelog;
import org.telegram.tgnet.TLRPC.TL_help_getAppChangelog;
import org.telegram.tgnet.TLRPC.TL_inputChannel;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
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
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
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
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_messages_hideReportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_messages_receivedQueue;
import org.telegram.tgnet.TLRPC.TL_messages_reportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import org.telegram.tgnet.TLRPC.TL_messages_setTyping;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_messages_toggleChatAdmins;
import org.telegram.tgnet.TLRPC.TL_notifyPeer;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.telegram.tgnet.TLRPC.TL_peerSettings;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_deletePhotos;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardHide;
import org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageGamePlayAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordVideoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageTypingAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadDocumentAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadPhotoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadVideoAction;
import org.telegram.tgnet.TLRPC.TL_updateChannel;
import org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews;
import org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
import org.telegram.tgnet.TLRPC.TL_updateChannelTooLong;
import org.telegram.tgnet.TLRPC.TL_updateChatAdmins;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipants;
import org.telegram.tgnet.TLRPC.TL_updateChatUserTyping;
import org.telegram.tgnet.TLRPC.TL_updateContactLink;
import org.telegram.tgnet.TLRPC.TL_updateContactRegistered;
import org.telegram.tgnet.TLRPC.TL_updateDcOptions;
import org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
import org.telegram.tgnet.TLRPC.TL_updateEncryption;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewAuthorization;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewGeoChatMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
import org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
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
import org.telegram.tgnet.TLRPC.TL_updates_getChannelDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getState;
import org.telegram.tgnet.TLRPC.TL_updates_state;
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
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.contacts_Blocked;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;
import org.telegram.tgnet.TLRPC.updates_Difference;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.ProfileActivity;

public class MessagesController
  implements NotificationCenter.NotificationCenterDelegate
{
  private static volatile MessagesController Instance = null;
  public static final int UPDATE_MASK_ALL = 1535;
  public static final int UPDATE_MASK_AVATAR = 2;
  public static final int UPDATE_MASK_CHANNEL = 8192;
  public static final int UPDATE_MASK_CHAT_ADMINS = 16384;
  public static final int UPDATE_MASK_CHAT_AVATAR = 8;
  public static final int UPDATE_MASK_CHAT_MEMBERS = 32;
  public static final int UPDATE_MASK_CHAT_NAME = 16;
  public static final int UPDATE_MASK_NAME = 1;
  public static final int UPDATE_MASK_NEW_MESSAGE = 2048;
  public static final int UPDATE_MASK_PHONE = 1024;
  public static final int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
  public static final int UPDATE_MASK_SELECT_DIALOG = 512;
  public static final int UPDATE_MASK_SEND_STATE = 4096;
  public static final int UPDATE_MASK_STATUS = 4;
  public static final int UPDATE_MASK_USER_PHONE = 128;
  public static final int UPDATE_MASK_USER_PRINT = 64;
  public ArrayList<Integer> blockedUsers = new ArrayList();
  private SparseArray<ArrayList<Integer>> channelViewsToReload = new SparseArray();
  private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray();
  private HashMap<Integer, Integer> channelsPts = new HashMap();
  private ConcurrentHashMap<Integer, TLRPC.Chat> chats = new ConcurrentHashMap(100, 1.0F, 2);
  private HashMap<Integer, Boolean> checkingLastMessagesDialogs = new HashMap();
  private ArrayList<Long> createdDialogIds = new ArrayList();
  private Runnable currentDeleteTaskRunnable = null;
  private ArrayList<Integer> currentDeletingTaskMids = null;
  private int currentDeletingTaskTime = 0;
  private final Comparator<TLRPC.TL_dialog> dialogComparator = new Comparator()
  {
    public int compare(TLRPC.TL_dialog paramAnonymousTL_dialog1, TLRPC.TL_dialog paramAnonymousTL_dialog2)
    {
      TLRPC.DraftMessage localDraftMessage = DraftQuery.getDraft(paramAnonymousTL_dialog1.id);
      int i;
      if ((localDraftMessage != null) && (localDraftMessage.date >= paramAnonymousTL_dialog1.last_message_date))
      {
        i = localDraftMessage.date;
        paramAnonymousTL_dialog1 = DraftQuery.getDraft(paramAnonymousTL_dialog2.id);
        if ((paramAnonymousTL_dialog1 == null) || (paramAnonymousTL_dialog1.date < paramAnonymousTL_dialog2.last_message_date)) {
          break label77;
        }
      }
      label77:
      for (int j = paramAnonymousTL_dialog1.date;; j = paramAnonymousTL_dialog2.last_message_date)
      {
        if (i >= j) {
          break label86;
        }
        return 1;
        i = paramAnonymousTL_dialog1.last_message_date;
        break;
      }
      label86:
      if (i > j) {
        return -1;
      }
      return 0;
    }
  };
  public HashMap<Long, MessageObject> dialogMessage = new HashMap();
  public HashMap<Integer, MessageObject> dialogMessagesByIds = new HashMap();
  public HashMap<Long, MessageObject> dialogMessagesByRandomIds = new HashMap();
  public ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();
  public boolean dialogsEndReached = false;
  public ArrayList<TLRPC.TL_dialog> dialogsGroupsOnly = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsServerOnly = new ArrayList();
  public ConcurrentHashMap<Long, TLRPC.TL_dialog> dialogs_dict = new ConcurrentHashMap(100, 1.0F, 2);
  public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap(100, 1.0F, 2);
  public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap(100, 1.0F, 2);
  private ArrayList<TLRPC.TL_disabledFeature> disabledFeatures = new ArrayList();
  public boolean enableJoined = true;
  private ConcurrentHashMap<Integer, TLRPC.EncryptedChat> encryptedChats = new ConcurrentHashMap(10, 1.0F, 2);
  private HashMap<Integer, TLRPC.ExportedChatInvite> exportedChats = new HashMap();
  public boolean firstGettingTask = false;
  public int fontSize = AndroidUtilities.dp(16.0F);
  private HashMap<Integer, String> fullUsersAbout = new HashMap();
  public boolean gettingDifference = false;
  private HashMap<Integer, Boolean> gettingDifferenceChannels = new HashMap();
  private boolean gettingNewDeleteTask = false;
  private HashMap<Integer, Boolean> gettingUnknownChannels = new HashMap();
  public int groupBigSize;
  private ArrayList<Integer> joiningToChannels = new ArrayList();
  private int lastPrintingStringCount = 0;
  private long lastStatusUpdateTime = 0L;
  private long lastViewsCheckTime;
  private ArrayList<Integer> loadedFullChats = new ArrayList();
  private ArrayList<Integer> loadedFullParticipants = new ArrayList();
  private ArrayList<Integer> loadedFullUsers = new ArrayList();
  public boolean loadingBlockedUsers = false;
  public boolean loadingDialogs = false;
  private ArrayList<Integer> loadingFullChats = new ArrayList();
  private ArrayList<Integer> loadingFullParticipants = new ArrayList();
  private ArrayList<Integer> loadingFullUsers = new ArrayList();
  private HashMap<Long, Boolean> loadingPeerSettings = new HashMap();
  public int maxBroadcastCount = 100;
  public int maxEditTime = 172800;
  public int maxGroupCount = 200;
  public int maxMegagroupCount = 5000;
  public int maxRecentGifsCount = 200;
  public int maxRecentStickersCount = 30;
  private boolean migratingDialogs = false;
  public int minGroupConvertSize = 200;
  private SparseIntArray needShortPollChannels = new SparseIntArray();
  public int nextDialogsCacheOffset;
  private boolean offlineSent = false;
  public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap(20, 1.0F, 2);
  public HashMap<Long, CharSequence> printingStrings = new HashMap();
  public HashMap<Long, Integer> printingStringsTypes = new HashMap();
  public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0F, 2);
  public int ratingDecay;
  public boolean registeringForPush = false;
  private HashMap<Long, ArrayList<Integer>> reloadingMessages = new HashMap();
  private HashMap<String, ArrayList<MessageObject>> reloadingWebpages = new HashMap();
  private HashMap<Long, ArrayList<MessageObject>> reloadingWebpagesPending = new HashMap();
  public int secretWebpagePreview = 2;
  public HashMap<Integer, HashMap<Long, Boolean>> sendingTypings = new HashMap();
  private SparseIntArray shortPollChannels = new SparseIntArray();
  private int statusRequest = 0;
  private int statusSettingState = 0;
  private final Comparator<TLRPC.Update> updatesComparator = new Comparator()
  {
    public int compare(TLRPC.Update paramAnonymousUpdate1, TLRPC.Update paramAnonymousUpdate2)
    {
      int i = MessagesController.this.getUpdateType(paramAnonymousUpdate1);
      int j = MessagesController.this.getUpdateType(paramAnonymousUpdate2);
      if (i != j) {
        return AndroidUtilities.compare(i, j);
      }
      if (i == 0) {
        return AndroidUtilities.compare(paramAnonymousUpdate1.pts, paramAnonymousUpdate2.pts);
      }
      if (i == 1) {
        return AndroidUtilities.compare(paramAnonymousUpdate1.qts, paramAnonymousUpdate2.qts);
      }
      if (i == 2)
      {
        i = MessagesController.this.getUpdateChannelId(paramAnonymousUpdate1);
        j = MessagesController.this.getUpdateChannelId(paramAnonymousUpdate2);
        if (i == j) {
          return AndroidUtilities.compare(paramAnonymousUpdate1.pts, paramAnonymousUpdate2.pts);
        }
        return AndroidUtilities.compare(i, j);
      }
      return 0;
    }
  };
  private HashMap<Integer, ArrayList<TLRPC.Updates>> updatesQueueChannels = new HashMap();
  private ArrayList<TLRPC.Updates> updatesQueuePts = new ArrayList();
  private ArrayList<TLRPC.Updates> updatesQueueQts = new ArrayList();
  private ArrayList<TLRPC.Updates> updatesQueueSeq = new ArrayList();
  private HashMap<Integer, Long> updatesStartWaitTimeChannels = new HashMap();
  private long updatesStartWaitTimePts = 0L;
  private long updatesStartWaitTimeQts = 0L;
  private long updatesStartWaitTimeSeq = 0L;
  public boolean updatingState = false;
  private String uploadingAvatar = null;
  private ConcurrentHashMap<Integer, TLRPC.User> users = new ConcurrentHashMap(100, 1.0F, 2);
  private ConcurrentHashMap<String, TLRPC.User> usersByUsernames = new ConcurrentHashMap(100, 1.0F, 2);
  
  public MessagesController()
  {
    ImageLoader.getInstance();
    MessagesStorage.getInstance();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
    addSupportUser();
    this.enableJoined = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnableContactJoined", true);
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    this.secretWebpagePreview = ((SharedPreferences)localObject).getInt("secretWebpage2", 2);
    this.maxGroupCount = ((SharedPreferences)localObject).getInt("maxGroupCount", 200);
    this.maxMegagroupCount = ((SharedPreferences)localObject).getInt("maxMegagroupCount", 1000);
    this.maxRecentGifsCount = ((SharedPreferences)localObject).getInt("maxRecentGifsCount", 200);
    this.maxRecentStickersCount = ((SharedPreferences)localObject).getInt("maxRecentStickersCount", 30);
    this.maxEditTime = ((SharedPreferences)localObject).getInt("maxEditTime", 3600);
    this.groupBigSize = ((SharedPreferences)localObject).getInt("groupBigSize", 10);
    this.ratingDecay = ((SharedPreferences)localObject).getInt("ratingDecay", 2419200);
    int i;
    if (AndroidUtilities.isTablet()) {
      i = 18;
    }
    for (;;)
    {
      this.fontSize = ((SharedPreferences)localObject).getInt("fons_size", i);
      localObject = ((SharedPreferences)localObject).getString("disabledFeatures", null);
      if ((localObject != null) && (((String)localObject).length() != 0)) {
        try
        {
          localObject = Base64.decode((String)localObject, 0);
          if (localObject != null)
          {
            localObject = new SerializedData((byte[])localObject);
            int j = ((SerializedData)localObject).readInt32(false);
            i = 0;
            while (i < j)
            {
              TLRPC.TL_disabledFeature localTL_disabledFeature = TLRPC.TL_disabledFeature.TLdeserialize((AbstractSerializedData)localObject, ((SerializedData)localObject).readInt32(false), false);
              if ((localTL_disabledFeature != null) && (localTL_disabledFeature.feature != null) && (localTL_disabledFeature.description != null)) {
                this.disabledFeatures.add(localTL_disabledFeature);
              }
              i += 1;
              continue;
              i = 16;
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  private void applyDialogNotificationsSettings(long paramLong, TLRPC.PeerNotifySettings paramPeerNotifySettings)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int m = ((SharedPreferences)localObject).getInt("notify2_" + paramLong, 0);
    int n = ((SharedPreferences)localObject).getInt("notifyuntil_" + paramLong, 0);
    localObject = ((SharedPreferences)localObject).edit();
    int j = 0;
    int i = 0;
    TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(paramLong));
    if (localTL_dialog != null) {
      localTL_dialog.notify_settings = paramPeerNotifySettings;
    }
    ((SharedPreferences.Editor)localObject).putBoolean("silent_" + paramLong, paramPeerNotifySettings.silent);
    int k;
    if (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime())
    {
      k = 0;
      if (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000)
      {
        j = k;
        if (m != 2)
        {
          m = 1;
          ((SharedPreferences.Editor)localObject).putInt("notify2_" + paramLong, 2);
          j = k;
          i = m;
          if (localTL_dialog != null)
          {
            localTL_dialog.notify_settings.mute_until = Integer.MAX_VALUE;
            i = m;
            j = k;
          }
        }
        MessagesStorage.getInstance().setDialogFlags(paramLong, j << 32 | 1L);
        NotificationsController.getInstance().removeNotificationsForDialog(paramLong);
      }
    }
    for (;;)
    {
      ((SharedPreferences.Editor)localObject).commit();
      if (i != 0) {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
      }
      return;
      if (m == 3)
      {
        j = k;
        if (n == paramPeerNotifySettings.mute_until) {
          break;
        }
      }
      k = 1;
      m = paramPeerNotifySettings.mute_until;
      ((SharedPreferences.Editor)localObject).putInt("notify2_" + paramLong, 3);
      ((SharedPreferences.Editor)localObject).putInt("notifyuntil_" + paramLong, paramPeerNotifySettings.mute_until);
      j = m;
      i = k;
      if (localTL_dialog == null) {
        break;
      }
      localTL_dialog.notify_settings.mute_until = m;
      j = m;
      i = k;
      break;
      i = j;
      if (m != 0)
      {
        i = j;
        if (m != 1)
        {
          i = 1;
          if (localTL_dialog != null) {
            localTL_dialog.notify_settings.mute_until = 0;
          }
          ((SharedPreferences.Editor)localObject).remove("notify2_" + paramLong);
        }
      }
      MessagesStorage.getInstance().setDialogFlags(paramLong, 0L);
    }
  }
  
  private void applyDialogsNotificationsSettings(ArrayList<TLRPC.TL_dialog> paramArrayList)
  {
    Object localObject1 = null;
    int j = 0;
    if (j < paramArrayList.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)paramArrayList.get(j);
      Object localObject2 = localObject1;
      int i;
      if (localTL_dialog.peer != null)
      {
        localObject2 = localObject1;
        if ((localTL_dialog.notify_settings instanceof TLRPC.TL_peerNotifySettings))
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          }
          if (localTL_dialog.peer.user_id == 0) {
            break label215;
          }
          i = localTL_dialog.peer.user_id;
          label96:
          ((SharedPreferences.Editor)localObject2).putBoolean("silent_" + i, localTL_dialog.notify_settings.silent);
          if (localTL_dialog.notify_settings.mute_until == 0) {
            break label320;
          }
          if (localTL_dialog.notify_settings.mute_until <= ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
            break label252;
          }
          ((SharedPreferences.Editor)localObject2).putInt("notify2_" + i, 2);
          localTL_dialog.notify_settings.mute_until = Integer.MAX_VALUE;
        }
      }
      for (;;)
      {
        j += 1;
        localObject1 = localObject2;
        break;
        label215:
        if (localTL_dialog.peer.chat_id != 0)
        {
          i = -localTL_dialog.peer.chat_id;
          break label96;
        }
        i = -localTL_dialog.peer.channel_id;
        break label96;
        label252:
        ((SharedPreferences.Editor)localObject2).putInt("notify2_" + i, 3);
        ((SharedPreferences.Editor)localObject2).putInt("notifyuntil_" + i, localTL_dialog.notify_settings.mute_until);
        continue;
        label320:
        ((SharedPreferences.Editor)localObject2).remove("notify2_" + i);
      }
    }
    if (localObject1 != null) {
      ((SharedPreferences.Editor)localObject1).commit();
    }
  }
  
  public static boolean checkCanOpenChat(Bundle paramBundle, BaseFragment paramBaseFragment)
  {
    if ((paramBundle == null) || (paramBaseFragment == null)) {}
    label53:
    label115:
    label117:
    label135:
    for (;;)
    {
      return true;
      Object localObject2 = null;
      Object localObject3 = null;
      int i = paramBundle.getInt("user_id", 0);
      int j = paramBundle.getInt("chat_id", 0);
      Object localObject1;
      if (i != 0)
      {
        localObject1 = getInstance().getUser(Integer.valueOf(i));
        paramBundle = (Bundle)localObject3;
        if ((localObject1 == null) && (paramBundle == null)) {
          break label115;
        }
        localObject2 = null;
        if (paramBundle == null) {
          break label117;
        }
        paramBundle = getRestrictionReason(paramBundle.restriction_reason);
      }
      for (;;)
      {
        if (paramBundle == null) {
          break label135;
        }
        showCantOpenAlert(paramBaseFragment, paramBundle);
        return false;
        paramBundle = (Bundle)localObject3;
        localObject1 = localObject2;
        if (j == 0) {
          break label53;
        }
        paramBundle = getInstance().getChat(Integer.valueOf(j));
        localObject1 = localObject2;
        break label53;
        break;
        paramBundle = (Bundle)localObject2;
        if (localObject1 != null) {
          paramBundle = getRestrictionReason(((TLRPC.User)localObject1).restriction_reason);
        }
      }
    }
  }
  
  private void checkChannelError(String paramString, int paramInt)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        return;
        if (paramString.equals("CHANNEL_PRIVATE"))
        {
          i = 0;
          continue;
          if (paramString.equals("CHANNEL_PUBLIC_GROUP_NA"))
          {
            i = 1;
            continue;
            if (paramString.equals("USER_BANNED_IN_CHANNEL")) {
              i = 2;
            }
          }
        }
        break;
      }
    }
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(0) });
    return;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(1) });
    return;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(2) });
  }
  
  private boolean checkDeletingTask(boolean paramBoolean)
  {
    boolean bool2 = false;
    int i = ConnectionsManager.getInstance().getCurrentTime();
    boolean bool1 = bool2;
    if (this.currentDeletingTaskMids != null) {
      if (!paramBoolean)
      {
        bool1 = bool2;
        if (this.currentDeletingTaskTime != 0)
        {
          bool1 = bool2;
          if (this.currentDeletingTaskTime > i) {}
        }
      }
      else
      {
        this.currentDeletingTaskTime = 0;
        if ((this.currentDeleteTaskRunnable != null) && (!paramBoolean)) {
          Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.deleteMessages(MessagesController.this.currentDeletingTaskMids, null, null, 0);
            Utilities.stageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                MessagesController.this.getNewDeleteTask(MessagesController.this.currentDeletingTaskMids);
                MessagesController.access$3102(MessagesController.this, 0);
                MessagesController.access$2902(MessagesController.this, null);
              }
            });
          }
        });
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void deleteDialog(final long paramLong, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int j = (int)paramLong;
    int k = (int)(paramLong >> 32);
    if (paramInt1 == 2) {
      MessagesStorage.getInstance().deleteDialog(paramLong, paramInt1);
    }
    final int i;
    Object localObject1;
    label264:
    label413:
    label493:
    do
    {
      do
      {
        return;
        if ((paramInt1 == 0) || (paramInt1 == 3)) {
          AndroidUtilities.uninstallShortcut(paramLong);
        }
        i = paramInt2;
        if (paramBoolean)
        {
          MessagesStorage.getInstance().deleteDialog(paramLong, paramInt1);
          localObject2 = (TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(paramLong));
          i = paramInt2;
          if (localObject2 != null)
          {
            i = paramInt2;
            if (paramInt2 == 0) {
              i = Math.max(0, ((TLRPC.TL_dialog)localObject2).top_message);
            }
            if ((paramInt1 != 0) && (paramInt1 != 3)) {
              break;
            }
            this.dialogs.remove(localObject2);
            if ((this.dialogsServerOnly.remove(localObject2)) && (DialogObject.isChannel((TLRPC.TL_dialog)localObject2))) {
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.channelsPts.remove(Integer.valueOf(-(int)paramLong));
                  MessagesController.this.shortPollChannels.delete(-(int)paramLong);
                  MessagesController.this.needShortPollChannels.delete(-(int)paramLong);
                }
              });
            }
            this.dialogsGroupsOnly.remove(localObject2);
            this.dialogs_dict.remove(Long.valueOf(paramLong));
            this.dialogs_read_inbox_max.remove(Long.valueOf(paramLong));
            this.dialogs_read_outbox_max.remove(Long.valueOf(paramLong));
            this.nextDialogsCacheOffset -= 1;
            localObject1 = (MessageObject)this.dialogMessage.remove(Long.valueOf(((TLRPC.TL_dialog)localObject2).id));
            if (localObject1 == null) {
              break label668;
            }
            paramInt2 = ((MessageObject)localObject1).getId();
            this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject1).getId()));
            if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.random_id != 0L)) {
              this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id));
            }
            if ((paramInt1 != 1) || (j == 0) || (paramInt2 <= 0)) {
              break label766;
            }
            localObject1 = new TLRPC.TL_messageService();
            ((TLRPC.TL_messageService)localObject1).id = ((TLRPC.TL_dialog)localObject2).top_message;
            ((TLRPC.TL_messageService)localObject1).out = false;
            ((TLRPC.TL_messageService)localObject1).from_id = UserConfig.getClientUserId();
            ((TLRPC.TL_messageService)localObject1).flags |= 0x100;
            ((TLRPC.TL_messageService)localObject1).action = new TLRPC.TL_messageActionHistoryClear();
            ((TLRPC.TL_messageService)localObject1).date = ((TLRPC.TL_dialog)localObject2).last_message_date;
            if (j <= 0) {
              break label698;
            }
            ((TLRPC.TL_messageService)localObject1).to_id = new TLRPC.TL_peerUser();
            ((TLRPC.TL_messageService)localObject1).to_id.user_id = j;
            Object localObject3 = new MessageObject((TLRPC.Message)localObject1, null, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_messageService)localObject1).dialog_id)));
            localObject2 = new ArrayList();
            ((ArrayList)localObject2).add(localObject3);
            localObject3 = new ArrayList();
            ((ArrayList)localObject3).add(localObject1);
            updateInterfaceWithMessages(paramLong, (ArrayList)localObject2);
            MessagesStorage.getInstance().putMessages((ArrayList)localObject3, false, true, false, 0);
          }
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, new Object[] { Long.valueOf(paramLong), Boolean.valueOf(false) });
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
          {
            public void run()
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationsController.getInstance().removeNotificationsForDialog(MessagesController.36.this.val$did);
                }
              });
            }
          });
        }
      } while ((k == 1) || (paramInt1 == 3));
      if (j == 0) {
        break label787;
      }
      localObject1 = getInputPeer(j);
    } while ((localObject1 == null) || ((localObject1 instanceof TLRPC.TL_inputPeerChannel)));
    Object localObject2 = new TLRPC.TL_messages_deleteHistory();
    ((TLRPC.TL_messages_deleteHistory)localObject2).peer = ((TLRPC.InputPeer)localObject1);
    if (paramInt1 == 0)
    {
      paramInt2 = Integer.MAX_VALUE;
      label614:
      ((TLRPC.TL_messages_deleteHistory)localObject2).max_id = paramInt2;
      if (paramInt1 == 0) {
        break label782;
      }
    }
    label668:
    label698:
    label766:
    label782:
    for (paramBoolean = true;; paramBoolean = false)
    {
      ((TLRPC.TL_messages_deleteHistory)localObject2).just_clear = paramBoolean;
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.TL_messages_affectedHistory)paramAnonymousTLObject;
            if (paramAnonymousTLObject.offset > 0) {
              MessagesController.this.deleteDialog(paramLong, false, i, this.val$max_id_delete_final);
            }
            MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
          }
        }
      }, 64);
      return;
      ((TLRPC.TL_dialog)localObject2).unread_count = 0;
      break;
      paramInt2 = ((TLRPC.TL_dialog)localObject2).top_message;
      localObject1 = (MessageObject)this.dialogMessagesByIds.remove(Integer.valueOf(((TLRPC.TL_dialog)localObject2).top_message));
      break label264;
      if (ChatObject.isChannel(getChat(Integer.valueOf(-j))))
      {
        ((TLRPC.TL_messageService)localObject1).to_id = new TLRPC.TL_peerChannel();
        ((TLRPC.TL_messageService)localObject1).to_id.channel_id = (-j);
        break label413;
      }
      ((TLRPC.TL_messageService)localObject1).to_id = new TLRPC.TL_peerChat();
      ((TLRPC.TL_messageService)localObject1).to_id.chat_id = (-j);
      break label413;
      ((TLRPC.TL_dialog)localObject2).top_message = 0;
      break label493;
      paramInt2 = i;
      break label614;
    }
    label787:
    if (paramInt1 == 1)
    {
      SecretChatHelper.getInstance().sendClearHistoryMessage(getEncryptedChat(Integer.valueOf(k)), null);
      return;
    }
    SecretChatHelper.getInstance().declineSecretChat(k);
  }
  
  private void getChannelDifference(int paramInt)
  {
    getChannelDifference(paramInt, 0, 0L);
  }
  
  public static TLRPC.InputChannel getInputChannel(int paramInt)
  {
    return getInputChannel(getInstance().getChat(Integer.valueOf(paramInt)));
  }
  
  public static TLRPC.InputChannel getInputChannel(TLRPC.Chat paramChat)
  {
    if (((paramChat instanceof TLRPC.TL_channel)) || ((paramChat instanceof TLRPC.TL_channelForbidden)))
    {
      TLRPC.TL_inputChannel localTL_inputChannel = new TLRPC.TL_inputChannel();
      localTL_inputChannel.channel_id = paramChat.id;
      localTL_inputChannel.access_hash = paramChat.access_hash;
      return localTL_inputChannel;
    }
    return new TLRPC.TL_inputChannelEmpty();
  }
  
  public static TLRPC.InputPeer getInputPeer(int paramInt)
  {
    Object localObject2;
    Object localObject1;
    if (paramInt < 0)
    {
      localObject2 = getInstance().getChat(Integer.valueOf(-paramInt));
      if (ChatObject.isChannel((TLRPC.Chat)localObject2))
      {
        localObject1 = new TLRPC.TL_inputPeerChannel();
        ((TLRPC.InputPeer)localObject1).channel_id = (-paramInt);
        ((TLRPC.InputPeer)localObject1).access_hash = ((TLRPC.Chat)localObject2).access_hash;
      }
    }
    TLRPC.User localUser;
    do
    {
      return (TLRPC.InputPeer)localObject1;
      localObject1 = new TLRPC.TL_inputPeerChat();
      ((TLRPC.InputPeer)localObject1).chat_id = (-paramInt);
      return (TLRPC.InputPeer)localObject1;
      localUser = getInstance().getUser(Integer.valueOf(paramInt));
      localObject2 = new TLRPC.TL_inputPeerUser();
      ((TLRPC.InputPeer)localObject2).user_id = paramInt;
      localObject1 = localObject2;
    } while (localUser == null);
    ((TLRPC.InputPeer)localObject2).access_hash = localUser.access_hash;
    return (TLRPC.InputPeer)localObject2;
  }
  
  public static TLRPC.InputUser getInputUser(int paramInt)
  {
    return getInputUser(getInstance().getUser(Integer.valueOf(paramInt)));
  }
  
  public static TLRPC.InputUser getInputUser(TLRPC.User paramUser)
  {
    if (paramUser == null) {
      return new TLRPC.TL_inputUserEmpty();
    }
    if (paramUser.id == UserConfig.getClientUserId()) {
      return new TLRPC.TL_inputUserSelf();
    }
    TLRPC.TL_inputUser localTL_inputUser = new TLRPC.TL_inputUser();
    localTL_inputUser.user_id = paramUser.id;
    localTL_inputUser.access_hash = paramUser.access_hash;
    return localTL_inputUser;
  }
  
  public static MessagesController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          MessagesController localMessagesController2 = Instance;
          localObject1 = localMessagesController2;
          if (localMessagesController2 == null) {
            localObject1 = new MessagesController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (MessagesController)localObject1;
          return (MessagesController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localMessagesController1;
  }
  
  public static TLRPC.Peer getPeer(int paramInt)
  {
    if (paramInt < 0)
    {
      localObject = getInstance().getChat(Integer.valueOf(-paramInt));
      if (((localObject instanceof TLRPC.TL_channel)) || ((localObject instanceof TLRPC.TL_channelForbidden)))
      {
        localObject = new TLRPC.TL_peerChannel();
        ((TLRPC.Peer)localObject).channel_id = (-paramInt);
        return (TLRPC.Peer)localObject;
      }
      localObject = new TLRPC.TL_peerChat();
      ((TLRPC.Peer)localObject).chat_id = (-paramInt);
      return (TLRPC.Peer)localObject;
    }
    getInstance().getUser(Integer.valueOf(paramInt));
    Object localObject = new TLRPC.TL_peerUser();
    ((TLRPC.Peer)localObject).user_id = paramInt;
    return (TLRPC.Peer)localObject;
  }
  
  private static String getRestrictionReason(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {}
    int i;
    String str;
    do
    {
      do
      {
        return null;
        i = paramString.indexOf(": ");
      } while (i <= 0);
      str = paramString.substring(0, i);
    } while ((!str.contains("-all")) && (!str.contains("-android")));
    return paramString.substring(i + 2);
  }
  
  private int getUpdateChannelId(TLRPC.Update paramUpdate)
  {
    if ((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage)) {
      return ((TLRPC.TL_updateNewChannelMessage)paramUpdate).message.to_id.channel_id;
    }
    if ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage)) {
      return ((TLRPC.TL_updateEditChannelMessage)paramUpdate).message.to_id.channel_id;
    }
    return paramUpdate.channel_id;
  }
  
  private int getUpdateSeq(TLRPC.Updates paramUpdates)
  {
    if ((paramUpdates instanceof TLRPC.TL_updatesCombined)) {
      return paramUpdates.seq_start;
    }
    return paramUpdates.seq;
  }
  
  private int getUpdateType(TLRPC.Update paramUpdate)
  {
    if (((paramUpdate instanceof TLRPC.TL_updateNewMessage)) || ((paramUpdate instanceof TLRPC.TL_updateReadMessagesContents)) || ((paramUpdate instanceof TLRPC.TL_updateReadHistoryInbox)) || ((paramUpdate instanceof TLRPC.TL_updateReadHistoryOutbox)) || ((paramUpdate instanceof TLRPC.TL_updateDeleteMessages)) || ((paramUpdate instanceof TLRPC.TL_updateWebPage)) || ((paramUpdate instanceof TLRPC.TL_updateEditMessage))) {
      return 0;
    }
    if ((paramUpdate instanceof TLRPC.TL_updateNewEncryptedMessage)) {
      return 1;
    }
    if (((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage)) || ((paramUpdate instanceof TLRPC.TL_updateDeleteChannelMessages)) || ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage))) {
      return 2;
    }
    return 3;
  }
  
  private String getUserNameForTyping(TLRPC.User paramUser)
  {
    if (paramUser == null) {
      return "";
    }
    if ((paramUser.first_name != null) && (paramUser.first_name.length() > 0)) {
      return paramUser.first_name;
    }
    if ((paramUser.last_name != null) && (paramUser.last_name.length() > 0)) {
      return paramUser.last_name;
    }
    return "";
  }
  
  public static boolean isFeatureEnabled(String paramString, BaseFragment paramBaseFragment)
  {
    if ((paramString == null) || (paramString.length() == 0) || (getInstance().disabledFeatures.isEmpty()) || (paramBaseFragment == null)) {}
    TLRPC.TL_disabledFeature localTL_disabledFeature;
    do
    {
      Iterator localIterator;
      while (!localIterator.hasNext())
      {
        return true;
        localIterator = getInstance().disabledFeatures.iterator();
      }
      localTL_disabledFeature = (TLRPC.TL_disabledFeature)localIterator.next();
    } while (!localTL_disabledFeature.feature.equals(paramString));
    if (paramBaseFragment.getParentActivity() != null)
    {
      paramString = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
      paramString.setTitle("Oops!");
      paramString.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
      paramString.setMessage(localTL_disabledFeature.description);
      paramBaseFragment.showDialog(paramString.create());
    }
    return false;
  }
  
  private boolean isNotifySettingsMuted(TLRPC.PeerNotifySettings paramPeerNotifySettings)
  {
    return ((paramPeerNotifySettings instanceof TLRPC.TL_peerNotifySettings)) && (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime());
  }
  
  private int isValidUpdate(TLRPC.Updates paramUpdates, int paramInt)
  {
    int i = 1;
    int j;
    if (paramInt == 0)
    {
      j = getUpdateSeq(paramUpdates);
      if ((MessagesStorage.lastSeqValue + 1 == j) || (MessagesStorage.lastSeqValue == j)) {
        paramInt = 0;
      }
    }
    do
    {
      do
      {
        do
        {
          return paramInt;
          paramInt = i;
        } while (MessagesStorage.lastSeqValue < j);
        return 2;
        if (paramInt != 1) {
          break;
        }
        if (paramUpdates.pts <= MessagesStorage.lastPtsValue) {
          return 2;
        }
        paramInt = i;
      } while (MessagesStorage.lastPtsValue + paramUpdates.pts_count != paramUpdates.pts);
      return 0;
      if (paramInt != 2) {
        break;
      }
      if (paramUpdates.pts <= MessagesStorage.lastQtsValue) {
        return 2;
      }
      paramInt = i;
    } while (MessagesStorage.lastQtsValue + paramUpdates.updates.size() != paramUpdates.pts);
    return 0;
    return 0;
  }
  
  private void migrateDialogs(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong)
  {
    if ((this.migratingDialogs) || (paramInt1 == -1)) {
      return;
    }
    this.migratingDialogs = true;
    TLRPC.TL_messages_getDialogs localTL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
    localTL_messages_getDialogs.limit = 100;
    localTL_messages_getDialogs.offset_id = paramInt1;
    localTL_messages_getDialogs.offset_date = paramInt2;
    if (paramInt1 == 0)
    {
      localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
      ConnectionsManager.getInstance().sendRequest(localTL_messages_getDialogs, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Dialogs)paramAnonymousTLObject;
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
            {
              public void run()
              {
                int i;
                Object localObject4;
                label121:
                label174:
                label429:
                label463:
                Object localObject2;
                for (;;)
                {
                  try
                  {
                    if (paramAnonymousTLObject.dialogs.size() != 100) {
                      break label955;
                    }
                    Object localObject1 = null;
                    i = 0;
                    if (i < paramAnonymousTLObject.messages.size())
                    {
                      localObject4 = (TLRPC.Message)paramAnonymousTLObject.messages.get(i);
                      if (localObject1 == null) {
                        break label926;
                      }
                      localObject3 = localObject1;
                      if (((TLRPC.Message)localObject4).date >= ((TLRPC.Message)localObject1).date) {
                        break label930;
                      }
                      break label926;
                    }
                    k = ((TLRPC.Message)localObject1).id;
                    UserConfig.migrateOffsetDate = ((TLRPC.Message)localObject1).date;
                    if (((TLRPC.Message)localObject1).to_id.channel_id != 0)
                    {
                      UserConfig.migrateOffsetChannelId = ((TLRPC.Message)localObject1).to_id.channel_id;
                      UserConfig.migrateOffsetChatId = 0;
                      UserConfig.migrateOffsetUserId = 0;
                      j = 0;
                      i = k;
                      if (j < paramAnonymousTLObject.chats.size())
                      {
                        localObject1 = (TLRPC.Chat)paramAnonymousTLObject.chats.get(j);
                        if (((TLRPC.Chat)localObject1).id != UserConfig.migrateOffsetChannelId) {
                          break label941;
                        }
                        UserConfig.migrateOffsetAccess = ((TLRPC.Chat)localObject1).access_hash;
                        i = k;
                      }
                      localObject3 = new StringBuilder(paramAnonymousTLObject.dialogs.size() * 12);
                      localObject1 = new HashMap();
                      j = 0;
                      if (j >= paramAnonymousTLObject.dialogs.size()) {
                        break;
                      }
                      localObject4 = (TLRPC.TL_dialog)paramAnonymousTLObject.dialogs.get(j);
                      if (((TLRPC.TL_dialog)localObject4).peer.channel_id == 0) {
                        break label519;
                      }
                      ((TLRPC.TL_dialog)localObject4).id = (-((TLRPC.TL_dialog)localObject4).peer.channel_id);
                      if (((StringBuilder)localObject3).length() > 0) {
                        ((StringBuilder)localObject3).append(",");
                      }
                      ((StringBuilder)localObject3).append(((TLRPC.TL_dialog)localObject4).id);
                      ((HashMap)localObject1).put(Long.valueOf(((TLRPC.TL_dialog)localObject4).id), localObject4);
                      j += 1;
                      continue;
                    }
                    if (((TLRPC.Message)localObject1).to_id.chat_id == 0) {
                      break label429;
                    }
                    UserConfig.migrateOffsetChatId = ((TLRPC.Message)localObject1).to_id.chat_id;
                    UserConfig.migrateOffsetChannelId = 0;
                    UserConfig.migrateOffsetUserId = 0;
                    j = 0;
                    i = k;
                    if (j >= paramAnonymousTLObject.chats.size()) {
                      continue;
                    }
                    localObject1 = (TLRPC.Chat)paramAnonymousTLObject.chats.get(j);
                    if (((TLRPC.Chat)localObject1).id == UserConfig.migrateOffsetChatId)
                    {
                      UserConfig.migrateOffsetAccess = ((TLRPC.Chat)localObject1).access_hash;
                      i = k;
                      continue;
                    }
                    j += 1;
                  }
                  catch (Exception localException)
                  {
                    FileLog.e("tmessages", localException);
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        MessagesController.access$4502(MessagesController.this, false);
                      }
                    });
                    return;
                  }
                  continue;
                  i = k;
                  if (localException.to_id.user_id != 0)
                  {
                    UserConfig.migrateOffsetUserId = localException.to_id.user_id;
                    UserConfig.migrateOffsetChatId = 0;
                    UserConfig.migrateOffsetChannelId = 0;
                    j = 0;
                    i = k;
                    if (j < paramAnonymousTLObject.users.size())
                    {
                      localObject2 = (TLRPC.User)paramAnonymousTLObject.users.get(j);
                      if (((TLRPC.User)localObject2).id != UserConfig.migrateOffsetUserId) {
                        break label948;
                      }
                      UserConfig.migrateOffsetAccess = ((TLRPC.User)localObject2).access_hash;
                      i = k;
                      continue;
                      label519:
                      if (((TLRPC.TL_dialog)localObject4).peer.chat_id != 0) {
                        ((TLRPC.TL_dialog)localObject4).id = (-((TLRPC.TL_dialog)localObject4).peer.chat_id);
                      } else {
                        ((TLRPC.TL_dialog)localObject4).id = ((TLRPC.TL_dialog)localObject4).peer.user_id;
                      }
                    }
                  }
                }
                Object localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", new Object[] { ((StringBuilder)localObject3).toString() }), new Object[0]);
                label600:
                while (((SQLiteCursor)localObject3).next())
                {
                  long l = ((SQLiteCursor)localObject3).longValue(0);
                  localObject4 = (TLRPC.TL_dialog)((HashMap)localObject2).remove(Long.valueOf(l));
                  if (localObject4 != null)
                  {
                    paramAnonymousTLObject.dialogs.remove(localObject4);
                    j = 0;
                    label651:
                    if (j >= paramAnonymousTLObject.messages.size()) {
                      break label965;
                    }
                    TLRPC.Message localMessage = (TLRPC.Message)paramAnonymousTLObject.messages.get(j);
                    if (MessageObject.getDialogId(localMessage) != l) {
                      break label960;
                    }
                    paramAnonymousTLObject.messages.remove(j);
                    j -= 1;
                    if (localMessage.id == ((TLRPC.TL_dialog)localObject4).top_message) {
                      ((TLRPC.TL_dialog)localObject4).top_message = 0;
                    }
                    if (((TLRPC.TL_dialog)localObject4).top_message != 0) {
                      break label960;
                    }
                  }
                }
                ((SQLiteCursor)localObject3).dispose();
                localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                int k = i;
                int n;
                if (((SQLiteCursor)localObject3).next()) {
                  n = Math.max(1441062000, ((SQLiteCursor)localObject3).intValue(0));
                }
                for (int j = 0;; j = k + 1)
                {
                  k = i;
                  if (j < paramAnonymousTLObject.messages.size())
                  {
                    localObject4 = (TLRPC.Message)paramAnonymousTLObject.messages.get(j);
                    k = j;
                    if (((TLRPC.Message)localObject4).date < n)
                    {
                      int m = -1;
                      paramAnonymousTLObject.messages.remove(j);
                      j -= 1;
                      localObject4 = (TLRPC.TL_dialog)((HashMap)localObject2).remove(Long.valueOf(MessageObject.getDialogId((TLRPC.Message)localObject4)));
                      i = m;
                      k = j;
                      if (localObject4 != null)
                      {
                        paramAnonymousTLObject.dialogs.remove(localObject4);
                        i = m;
                        k = j;
                      }
                    }
                  }
                  else
                  {
                    ((SQLiteCursor)localObject3).dispose();
                    MessagesController.this.processLoadedDialogs(paramAnonymousTLObject, null, k, 0, 0, false, true);
                    return;
                    label926:
                    localObject3 = localObject4;
                    label930:
                    i += 1;
                    localObject2 = localObject3;
                    break;
                    label941:
                    j += 1;
                    break label121;
                    label948:
                    j += 1;
                    break label463;
                    label955:
                    i = -1;
                    break label174;
                    label960:
                    j += 1;
                    break label651;
                    label965:
                    break label600;
                  }
                }
              }
            });
            return;
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.access$4502(MessagesController.this, false);
            }
          });
        }
      });
      return;
    }
    if (paramInt5 != 0)
    {
      localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChannel();
      localTL_messages_getDialogs.offset_peer.channel_id = paramInt5;
    }
    for (;;)
    {
      localTL_messages_getDialogs.offset_peer.access_hash = paramLong;
      break;
      if (paramInt3 != 0)
      {
        localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerUser();
        localTL_messages_getDialogs.offset_peer.user_id = paramInt3;
      }
      else
      {
        localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChat();
        localTL_messages_getDialogs.offset_peer.chat_id = paramInt4;
      }
    }
  }
  
  public static void openByUserName(String paramString, final BaseFragment paramBaseFragment, final int paramInt)
  {
    if ((paramString == null) || (paramBaseFragment == null)) {}
    do
    {
      return;
      localObject = getInstance().getUser(paramString);
      if (localObject != null)
      {
        openChatOrProfileWith((TLRPC.User)localObject, null, paramBaseFragment, paramInt, false);
        return;
      }
    } while (paramBaseFragment.getParentActivity() == null);
    Object localObject = new ProgressDialog(paramBaseFragment.getParentActivity());
    ((ProgressDialog)localObject).setMessage(LocaleController.getString("Loading", 2131165834));
    ((ProgressDialog)localObject).setCanceledOnTouchOutside(false);
    ((ProgressDialog)localObject).setCancelable(false);
    TLRPC.TL_contacts_resolveUsername localTL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
    localTL_contacts_resolveUsername.username = paramString;
    paramInt = ConnectionsManager.getInstance().sendRequest(localTL_contacts_resolveUsername, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            do
            {
              try
              {
                MessagesController.109.this.val$progressDialog.dismiss();
                MessagesController.109.this.val$fragment.setVisibleDialog(null);
                if (paramAnonymousTL_error == null)
                {
                  TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)paramAnonymousTLObject;
                  MessagesController.getInstance().putUsers(localTL_contacts_resolvedPeer.users, false);
                  MessagesController.getInstance().putChats(localTL_contacts_resolvedPeer.chats, false);
                  MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
                  if (!localTL_contacts_resolvedPeer.chats.isEmpty())
                  {
                    MessagesController.openChatOrProfileWith(null, (TLRPC.Chat)localTL_contacts_resolvedPeer.chats.get(0), MessagesController.109.this.val$fragment, 1, false);
                    return;
                  }
                }
              }
              catch (Exception localException1)
              {
                do
                {
                  for (;;)
                  {
                    FileLog.e("tmessages", localException1);
                  }
                } while (localException1.users.isEmpty());
                MessagesController.openChatOrProfileWith((TLRPC.User)localException1.users.get(0), null, MessagesController.109.this.val$fragment, MessagesController.109.this.val$type, false);
                return;
              }
            } while ((MessagesController.109.this.val$fragment == null) || (MessagesController.109.this.val$fragment.getParentActivity() == null));
            try
            {
              Toast.makeText(MessagesController.109.this.val$fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", 2131165958), 0).show();
              return;
            }
            catch (Exception localException2)
            {
              FileLog.e("tmessages", localException2);
            }
          }
        });
      }
    });
    ((ProgressDialog)localObject).setButton(-2, LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
        try
        {
          paramAnonymousDialogInterface.dismiss();
          if (paramBaseFragment != null) {
            paramBaseFragment.setVisibleDialog(null);
          }
          return;
        }
        catch (Exception paramAnonymousDialogInterface)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramAnonymousDialogInterface);
          }
        }
      }
    });
    paramBaseFragment.setVisibleDialog((Dialog)localObject);
    ((ProgressDialog)localObject).show();
  }
  
  public static void openChatOrProfileWith(TLRPC.User paramUser, TLRPC.Chat paramChat, BaseFragment paramBaseFragment, int paramInt, boolean paramBoolean)
  {
    if (((paramUser == null) && (paramChat == null)) || (paramBaseFragment == null)) {
      return;
    }
    Object localObject = null;
    boolean bool;
    int i;
    if (paramChat != null)
    {
      localObject = getRestrictionReason(paramChat.restriction_reason);
      bool = paramBoolean;
      i = paramInt;
    }
    while (localObject != null)
    {
      showCantOpenAlert(paramBaseFragment, (String)localObject);
      return;
      i = paramInt;
      bool = paramBoolean;
      if (paramUser != null)
      {
        String str = getRestrictionReason(paramUser.restriction_reason);
        localObject = str;
        i = paramInt;
        bool = paramBoolean;
        if (paramUser.bot)
        {
          i = 1;
          bool = true;
          localObject = str;
        }
      }
    }
    localObject = new Bundle();
    if (paramChat != null) {
      ((Bundle)localObject).putInt("chat_id", paramChat.id);
    }
    while (i == 0)
    {
      paramBaseFragment.presentFragment(new ProfileActivity((Bundle)localObject));
      return;
      ((Bundle)localObject).putInt("user_id", paramUser.id);
    }
    paramBaseFragment.presentFragment(new ChatActivity((Bundle)localObject), bool);
  }
  
  private void processChannelsUpdatesQueue(int paramInt1, int paramInt2)
  {
    Object localObject = (ArrayList)this.updatesQueueChannels.get(Integer.valueOf(paramInt1));
    if (localObject == null) {
      return;
    }
    Integer localInteger = (Integer)this.channelsPts.get(Integer.valueOf(paramInt1));
    if ((((ArrayList)localObject).isEmpty()) || (localInteger == null))
    {
      this.updatesQueueChannels.remove(Integer.valueOf(paramInt1));
      return;
    }
    Collections.sort((List)localObject, new Comparator()
    {
      public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
      {
        return AndroidUtilities.compare(paramAnonymousUpdates1.pts, paramAnonymousUpdates2.pts);
      }
    });
    int i = 0;
    if (paramInt2 == 2) {
      this.channelsPts.put(Integer.valueOf(paramInt1), Integer.valueOf(((TLRPC.Updates)((ArrayList)localObject).get(0)).pts));
    }
    int j = 0;
    label114:
    if (((ArrayList)localObject).size() > 0)
    {
      TLRPC.Updates localUpdates = (TLRPC.Updates)((ArrayList)localObject).get(j);
      if (localUpdates.pts <= localInteger.intValue())
      {
        paramInt2 = 2;
        label149:
        if (paramInt2 != 0) {
          break label212;
        }
        processUpdates(localUpdates, true);
        i = 1;
        ((ArrayList)localObject).remove(j);
      }
      for (paramInt2 = j - 1;; paramInt2 = j - 1)
      {
        j = paramInt2 + 1;
        break label114;
        if (localInteger.intValue() + localUpdates.pts_count == localUpdates.pts)
        {
          paramInt2 = 0;
          break label149;
        }
        paramInt2 = 1;
        break label149;
        label212:
        if (paramInt2 == 1)
        {
          localObject = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(paramInt1));
          if ((localObject != null) && ((i != 0) || (Math.abs(System.currentTimeMillis() - ((Long)localObject).longValue()) <= 1500L)))
          {
            FileLog.e("tmessages", "HOLE IN CHANNEL " + paramInt1 + " UPDATES QUEUE - will wait more time");
            if (i == 0) {
              break;
            }
            this.updatesStartWaitTimeChannels.put(Integer.valueOf(paramInt1), Long.valueOf(System.currentTimeMillis()));
            return;
          }
          FileLog.e("tmessages", "HOLE IN CHANNEL " + paramInt1 + " UPDATES QUEUE - getChannelDifference ");
          this.updatesStartWaitTimeChannels.remove(Integer.valueOf(paramInt1));
          this.updatesQueueChannels.remove(Integer.valueOf(paramInt1));
          getChannelDifference(paramInt1);
          return;
        }
        ((ArrayList)localObject).remove(j);
      }
    }
    this.updatesQueueChannels.remove(Integer.valueOf(paramInt1));
    this.updatesStartWaitTimeChannels.remove(Integer.valueOf(paramInt1));
    FileLog.e("tmessages", "UPDATES CHANNEL " + paramInt1 + " QUEUE PROCEED - OK");
  }
  
  private void processUpdatesQueue(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = null;
    int i;
    TLRPC.Updates localUpdates;
    label70:
    int j;
    if (paramInt1 == 0)
    {
      localArrayList = this.updatesQueueSeq;
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
        {
          return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(paramAnonymousUpdates1), MessagesController.this.getUpdateSeq(paramAnonymousUpdates2));
        }
      });
      if ((localArrayList == null) || (localArrayList.isEmpty())) {
        break label333;
      }
      i = 0;
      if (paramInt2 == 2)
      {
        localUpdates = (TLRPC.Updates)localArrayList.get(0);
        if (paramInt1 != 0) {
          break label192;
        }
        MessagesStorage.lastSeqValue = getUpdateSeq(localUpdates);
      }
      j = 0;
      paramInt2 = i;
      i = j;
      label78:
      if (localArrayList.size() <= 0) {
        break label319;
      }
      localUpdates = (TLRPC.Updates)localArrayList.get(i);
      j = isValidUpdate(localUpdates, paramInt1);
      if (j != 0) {
        break label219;
      }
      processUpdates(localUpdates, true);
      paramInt2 = 1;
      localArrayList.remove(i);
      i -= 1;
    }
    for (;;)
    {
      i += 1;
      break label78;
      if (paramInt1 == 1)
      {
        localArrayList = this.updatesQueuePts;
        Collections.sort(localArrayList, new Comparator()
        {
          public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
          {
            return AndroidUtilities.compare(paramAnonymousUpdates1.pts, paramAnonymousUpdates2.pts);
          }
        });
        break;
      }
      if (paramInt1 != 2) {
        break;
      }
      localArrayList = this.updatesQueueQts;
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
        {
          return AndroidUtilities.compare(paramAnonymousUpdates1.pts, paramAnonymousUpdates2.pts);
        }
      });
      break;
      label192:
      if (paramInt1 == 1)
      {
        MessagesStorage.lastPtsValue = localUpdates.pts;
        break label70;
      }
      MessagesStorage.lastQtsValue = localUpdates.pts;
      break label70;
      label219:
      if (j == 1)
      {
        if ((getUpdatesStartTime(paramInt1) != 0L) && ((paramInt2 != 0) || (Math.abs(System.currentTimeMillis() - getUpdatesStartTime(paramInt1)) <= 1500L)))
        {
          FileLog.e("tmessages", "HOLE IN UPDATES QUEUE - will wait more time");
          if (paramInt2 != 0) {
            setUpdatesStartTime(paramInt1, System.currentTimeMillis());
          }
          return;
        }
        FileLog.e("tmessages", "HOLE IN UPDATES QUEUE - getDifference");
        setUpdatesStartTime(paramInt1, 0L);
        localArrayList.clear();
        getDifference();
        return;
      }
      localArrayList.remove(i);
      i -= 1;
    }
    label319:
    localArrayList.clear();
    FileLog.e("tmessages", "UPDATES QUEUE PROCEED - OK");
    label333:
    setUpdatesStartTime(paramInt1, 0L);
  }
  
  private void reloadDialogsReadValue(ArrayList<TLRPC.TL_dialog> paramArrayList, long paramLong)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    TLRPC.TL_messages_getPeerDialogs localTL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
    if (paramArrayList != null)
    {
      int i = 0;
      while (i < paramArrayList.size())
      {
        localTL_messages_getPeerDialogs.peers.add(getInputPeer((int)((TLRPC.TL_dialog)paramArrayList.get(i)).id));
        i += 1;
      }
    }
    localTL_messages_getPeerDialogs.peers.add(getInputPeer((int)paramLong));
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getPeerDialogs, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTLObject != null)
        {
          TLRPC.TL_messages_peerDialogs localTL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs)paramAnonymousTLObject;
          ArrayList localArrayList = new ArrayList();
          int i = 0;
          if (i < localTL_messages_peerDialogs.dialogs.size())
          {
            TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)localTL_messages_peerDialogs.dialogs.get(i);
            if (localTL_dialog.read_inbox_max_id == 0) {
              localTL_dialog.read_inbox_max_id = 1;
            }
            if (localTL_dialog.read_outbox_max_id == 0) {
              localTL_dialog.read_outbox_max_id = 1;
            }
            if ((localTL_dialog.id == 0L) && (localTL_dialog.peer != null))
            {
              if (localTL_dialog.peer.user_id != 0) {
                localTL_dialog.id = localTL_dialog.peer.user_id;
              }
            }
            else
            {
              label118:
              paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(localTL_dialog.id));
              paramAnonymousTLObject = paramAnonymousTL_error;
              if (paramAnonymousTL_error == null) {
                paramAnonymousTLObject = Integer.valueOf(0);
              }
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(localTL_dialog.id), Integer.valueOf(Math.max(localTL_dialog.read_inbox_max_id, paramAnonymousTLObject.intValue())));
              if (paramAnonymousTLObject.intValue() == 0)
              {
                if (localTL_dialog.peer.channel_id == 0) {
                  break label425;
                }
                paramAnonymousTLObject = new TLRPC.TL_updateReadChannelInbox();
                paramAnonymousTLObject.channel_id = localTL_dialog.peer.channel_id;
                paramAnonymousTLObject.max_id = localTL_dialog.read_inbox_max_id;
                localArrayList.add(paramAnonymousTLObject);
              }
              label239:
              paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(localTL_dialog.id));
              paramAnonymousTLObject = paramAnonymousTL_error;
              if (paramAnonymousTL_error == null) {
                paramAnonymousTLObject = Integer.valueOf(0);
              }
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(localTL_dialog.id), Integer.valueOf(Math.max(localTL_dialog.read_outbox_max_id, paramAnonymousTLObject.intValue())));
              if (paramAnonymousTLObject.intValue() == 0)
              {
                if (localTL_dialog.peer.channel_id == 0) {
                  break label461;
                }
                paramAnonymousTLObject = new TLRPC.TL_updateReadChannelOutbox();
                paramAnonymousTLObject.channel_id = localTL_dialog.peer.channel_id;
                paramAnonymousTLObject.max_id = localTL_dialog.read_outbox_max_id;
                localArrayList.add(paramAnonymousTLObject);
              }
            }
            for (;;)
            {
              i += 1;
              break;
              if (localTL_dialog.peer.chat_id != 0)
              {
                localTL_dialog.id = (-localTL_dialog.peer.chat_id);
                break label118;
              }
              if (localTL_dialog.peer.channel_id == 0) {
                break label118;
              }
              localTL_dialog.id = (-localTL_dialog.peer.channel_id);
              break label118;
              label425:
              paramAnonymousTLObject = new TLRPC.TL_updateReadHistoryInbox();
              paramAnonymousTLObject.peer = localTL_dialog.peer;
              paramAnonymousTLObject.max_id = localTL_dialog.read_inbox_max_id;
              localArrayList.add(paramAnonymousTLObject);
              break label239;
              label461:
              paramAnonymousTLObject = new TLRPC.TL_updateReadHistoryOutbox();
              paramAnonymousTLObject.peer = localTL_dialog.peer;
              paramAnonymousTLObject.max_id = localTL_dialog.read_outbox_max_id;
              localArrayList.add(paramAnonymousTLObject);
            }
          }
          if (!localArrayList.isEmpty()) {
            MessagesController.this.processUpdateArray(localArrayList, null, null, false);
          }
        }
      }
    });
  }
  
  private void reloadMessages(ArrayList<Integer> paramArrayList, final long paramLong)
  {
    if (paramArrayList.isEmpty()) {}
    final ArrayList localArrayList2;
    TLRPC.Chat localChat;
    Object localObject;
    ArrayList localArrayList1;
    label76:
    label139:
    do
    {
      return;
      localArrayList2 = new ArrayList();
      localChat = ChatObject.getChatByDialog(paramLong);
      int i;
      Integer localInteger;
      if (ChatObject.isChannel(localChat))
      {
        localObject = new TLRPC.TL_channels_getMessages();
        ((TLRPC.TL_channels_getMessages)localObject).channel = getInputChannel(localChat);
        ((TLRPC.TL_channels_getMessages)localObject).id = localArrayList2;
        localArrayList1 = (ArrayList)this.reloadingMessages.get(Long.valueOf(paramLong));
        i = 0;
        if (i >= paramArrayList.size()) {
          continue;
        }
        localInteger = (Integer)paramArrayList.get(i);
        if ((localArrayList1 == null) || (!localArrayList1.contains(localInteger))) {
          break label139;
        }
      }
      for (;;)
      {
        i += 1;
        break label76;
        localObject = new TLRPC.TL_messages_getMessages();
        ((TLRPC.TL_messages_getMessages)localObject).id = localArrayList2;
        break;
        localArrayList2.add(localInteger);
      }
    } while (localArrayList2.isEmpty());
    paramArrayList = localArrayList1;
    if (localArrayList1 == null)
    {
      paramArrayList = new ArrayList();
      this.reloadingMessages.put(Long.valueOf(paramLong), paramArrayList);
    }
    paramArrayList.addAll(localArrayList2);
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
          HashMap localHashMap1 = new HashMap();
          int i = 0;
          while (i < localmessages_Messages.users.size())
          {
            paramAnonymousTLObject = (TLRPC.User)localmessages_Messages.users.get(i);
            localHashMap1.put(Integer.valueOf(paramAnonymousTLObject.id), paramAnonymousTLObject);
            i += 1;
          }
          HashMap localHashMap2 = new HashMap();
          i = 0;
          while (i < localmessages_Messages.chats.size())
          {
            paramAnonymousTLObject = (TLRPC.Chat)localmessages_Messages.chats.get(i);
            localHashMap2.put(Integer.valueOf(paramAnonymousTLObject.id), paramAnonymousTLObject);
            i += 1;
          }
          paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(paramLong));
          paramAnonymousTLObject = paramAnonymousTL_error;
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, paramLong));
            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(paramLong), paramAnonymousTLObject);
          }
          Object localObject = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(paramLong));
          paramAnonymousTL_error = (TLRPC.TL_error)localObject;
          if (localObject == null)
          {
            paramAnonymousTL_error = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, paramLong));
            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(paramLong), paramAnonymousTL_error);
          }
          final ArrayList localArrayList = new ArrayList();
          i = 0;
          if (i < localmessages_Messages.messages.size())
          {
            TLRPC.Message localMessage = (TLRPC.Message)localmessages_Messages.messages.get(i);
            if ((localArrayList2 != null) && (localArrayList2.megagroup)) {
              localMessage.flags |= 0x80000000;
            }
            localMessage.dialog_id = paramLong;
            if (localMessage.out)
            {
              localObject = paramAnonymousTL_error;
              label336:
              if (((Integer)localObject).intValue() >= localMessage.id) {
                break label392;
              }
            }
            label392:
            for (boolean bool = true;; bool = false)
            {
              localMessage.unread = bool;
              localArrayList.add(new MessageObject(localMessage, localHashMap1, localHashMap2, true));
              i += 1;
              break;
              localObject = paramAnonymousTLObject;
              break label336;
            }
          }
          ImageLoader.saveMessagesThumbs(localmessages_Messages.messages);
          MessagesStorage.getInstance().putMessages(localmessages_Messages, paramLong, -1, 0, false);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              Object localObject = (ArrayList)MessagesController.this.reloadingMessages.get(Long.valueOf(MessagesController.12.this.val$dialog_id));
              if (localObject != null)
              {
                ((ArrayList)localObject).removeAll(MessagesController.12.this.val$result);
                if (((ArrayList)localObject).isEmpty()) {
                  MessagesController.this.reloadingMessages.remove(Long.valueOf(MessagesController.12.this.val$dialog_id));
                }
              }
              localObject = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(MessagesController.12.this.val$dialog_id));
              int i;
              if (localObject != null) {
                i = 0;
              }
              for (;;)
              {
                if (i < localArrayList.size())
                {
                  MessageObject localMessageObject = (MessageObject)localArrayList.get(i);
                  if ((localObject != null) && (((MessageObject)localObject).getId() == localMessageObject.getId()))
                  {
                    MessagesController.this.dialogMessage.put(Long.valueOf(MessagesController.12.this.val$dialog_id), localMessageObject);
                    if (localMessageObject.messageOwner.to_id.channel_id == 0)
                    {
                      localObject = (MessageObject)MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(localMessageObject.getId()));
                      if (localObject != null) {
                        MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject).getId()), localObject);
                      }
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                  }
                }
                else
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(MessagesController.12.this.val$dialog_id), localArrayList });
                  return;
                }
                i += 1;
              }
            }
          });
        }
      }
    });
  }
  
  private void setUpdatesStartTime(int paramInt, long paramLong)
  {
    if (paramInt == 0) {
      this.updatesStartWaitTimeSeq = paramLong;
    }
    do
    {
      return;
      if (paramInt == 1)
      {
        this.updatesStartWaitTimePts = paramLong;
        return;
      }
    } while (paramInt != 2);
    this.updatesStartWaitTimeQts = paramLong;
  }
  
  private static void showCantOpenAlert(BaseFragment paramBaseFragment, String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
    localBuilder.setMessage(paramString);
    paramBaseFragment.showDialog(localBuilder.create());
  }
  
  private void updatePrintingStrings()
  {
    final HashMap localHashMap1 = new HashMap();
    final HashMap localHashMap2 = new HashMap();
    new ArrayList(this.printingUsers.keySet());
    Iterator localIterator1 = this.printingUsers.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject1 = (Map.Entry)localIterator1.next();
      long l = ((Long)((Map.Entry)localObject1).getKey()).longValue();
      ArrayList localArrayList = (ArrayList)((Map.Entry)localObject1).getValue();
      int i = (int)l;
      Object localObject2;
      if ((i > 0) || (i == 0) || (localArrayList.size() == 1))
      {
        localObject1 = (PrintingUser)localArrayList.get(0);
        localObject2 = getUser(Integer.valueOf(((PrintingUser)localObject1).userId));
        if (localObject2 == null) {
          return;
        }
        if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageRecordAudioAction))
        {
          if (i < 0) {
            localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsRecordingAudio", 2131165768, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
          }
          for (;;)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(1));
            break;
            localHashMap1.put(Long.valueOf(l), LocaleController.getString("RecordingAudio", 2131166149));
          }
        }
        if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadAudioAction))
        {
          if (i < 0) {
            localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingAudio", 2131165769, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
          }
          for (;;)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
            break;
            localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingAudio", 2131166246));
          }
        }
        if (((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadVideoAction)) || ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageRecordVideoAction)))
        {
          if (i < 0) {
            localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingVideo", 2131165772, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
          }
          for (;;)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
            break;
            localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingVideoStatus", 2131166251));
          }
        }
        if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadDocumentAction))
        {
          if (i < 0) {
            localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingFile", 2131165770, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
          }
          for (;;)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
            break;
            localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingFile", 2131166247));
          }
        }
        if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadPhotoAction))
        {
          if (i < 0) {
            localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingPhoto", 2131165771, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
          }
          for (;;)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
            break;
            localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingPhoto", 2131166249));
          }
        }
        if (!(((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageGamePlayAction))
        {
          if (i < 0) {
            localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { getUserNameForTyping((TLRPC.User)localObject2), LocaleController.getString("IsTyping", 2131165773) }));
          }
          for (;;)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(0));
            break;
            localHashMap1.put(Long.valueOf(l), LocaleController.getString("Typing", 2131166348));
          }
        }
      }
      else
      {
        i = 0;
        localObject1 = "";
        Iterator localIterator2 = localArrayList.iterator();
        int j;
        do
        {
          j = i;
          localObject2 = localObject1;
          if (!localIterator2.hasNext()) {
            break;
          }
          TLRPC.User localUser = getUser(Integer.valueOf(((PrintingUser)localIterator2.next()).userId));
          j = i;
          localObject2 = localObject1;
          if (localUser != null)
          {
            localObject2 = localObject1;
            if (((String)localObject1).length() != 0) {
              localObject2 = (String)localObject1 + ", ";
            }
            localObject2 = (String)localObject2 + getUserNameForTyping(localUser);
            j = i + 1;
          }
          i = j;
          localObject1 = localObject2;
        } while (j != 2);
        if (((String)localObject2).length() != 0)
        {
          if (j == 1) {
            localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { localObject2, LocaleController.getString("IsTyping", 2131165773) }));
          }
          for (;;)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(0));
            break;
            if (localArrayList.size() > 2) {
              localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { localObject2, LocaleController.formatPluralString("AndMoreTyping", localArrayList.size() - 2) }));
            } else {
              localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { localObject2, LocaleController.getString("AreTyping", 2131165312) }));
            }
          }
        }
      }
    }
    this.lastPrintingStringCount = localHashMap1.size();
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.this.printingStrings = localHashMap1;
        MessagesController.this.printingStringsTypes = localHashMap2;
      }
    });
  }
  
  private boolean updatePrintingUsersWithNewMessages(long paramLong, ArrayList<MessageObject> paramArrayList)
  {
    if (paramLong > 0L)
    {
      if ((ArrayList)this.printingUsers.get(Long.valueOf(paramLong)) != null) {
        this.printingUsers.remove(Long.valueOf(paramLong));
      }
    }
    else
    {
      int k;
      do
      {
        return true;
        if (paramLong >= 0L) {
          break;
        }
        ArrayList localArrayList = new ArrayList();
        paramArrayList = paramArrayList.iterator();
        while (paramArrayList.hasNext())
        {
          MessageObject localMessageObject = (MessageObject)paramArrayList.next();
          if (!localArrayList.contains(Integer.valueOf(localMessageObject.messageOwner.from_id))) {
            localArrayList.add(Integer.valueOf(localMessageObject.messageOwner.from_id));
          }
        }
        paramArrayList = (ArrayList)this.printingUsers.get(Long.valueOf(paramLong));
        k = 0;
        int j = 0;
        if (paramArrayList != null) {
          for (int i = 0;; i = k + 1)
          {
            k = j;
            if (i >= paramArrayList.size()) {
              break;
            }
            k = i;
            if (localArrayList.contains(Integer.valueOf(((PrintingUser)paramArrayList.get(i)).userId)))
            {
              paramArrayList.remove(i);
              k = i - 1;
              if (paramArrayList.isEmpty()) {
                this.printingUsers.remove(Long.valueOf(paramLong));
              }
              j = 1;
            }
          }
        }
      } while (k != 0);
    }
    return false;
  }
  
  public void addSupportUser()
  {
    TLRPC.TL_userForeign_old2 localTL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
    localTL_userForeign_old2.phone = "333";
    localTL_userForeign_old2.id = 333000;
    localTL_userForeign_old2.first_name = "Telegram";
    localTL_userForeign_old2.last_name = "";
    localTL_userForeign_old2.status = null;
    localTL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
    putUser(localTL_userForeign_old2, true);
    localTL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
    localTL_userForeign_old2.phone = "42777";
    localTL_userForeign_old2.id = 777000;
    localTL_userForeign_old2.first_name = "Telegram";
    localTL_userForeign_old2.last_name = "Notifications";
    localTL_userForeign_old2.status = null;
    localTL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
    putUser(localTL_userForeign_old2, true);
  }
  
  public void addToViewsQueue(final TLRPC.Message paramMessage, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    long l2 = paramMessage.id;
    long l1 = l2;
    if (paramMessage.to_id.channel_id != 0) {
      l1 = l2 | paramMessage.to_id.channel_id << 32;
    }
    localArrayList.add(Long.valueOf(l1));
    MessagesStorage.getInstance().markMessagesContentAsRead(localArrayList);
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SparseArray localSparseArray = MessagesController.this.channelViewsToSend;
        int i;
        if (paramMessage.to_id.channel_id != 0) {
          i = -paramMessage.to_id.channel_id;
        }
        for (;;)
        {
          ArrayList localArrayList2 = (ArrayList)localSparseArray.get(i);
          ArrayList localArrayList1 = localArrayList2;
          if (localArrayList2 == null)
          {
            localArrayList1 = new ArrayList();
            localSparseArray.put(i, localArrayList1);
          }
          if (!localArrayList1.contains(Integer.valueOf(paramMessage.id))) {
            localArrayList1.add(Integer.valueOf(paramMessage.id));
          }
          return;
          if (paramMessage.to_id.chat_id != 0) {
            i = -paramMessage.to_id.chat_id;
          } else {
            i = paramMessage.to_id.user_id;
          }
        }
      }
    });
  }
  
  public void addUserToChat(final int paramInt1, TLRPC.User paramUser, TLRPC.ChatFull paramChatFull, int paramInt2, String paramString, final BaseFragment paramBaseFragment)
  {
    if (paramUser == null) {}
    label145:
    label226:
    label254:
    label323:
    do
    {
      final boolean bool2;
      final boolean bool1;
      final TLRPC.InputUser localInputUser;
      do
      {
        return;
        if (paramInt1 <= 0) {
          break label323;
        }
        bool2 = ChatObject.isChannel(paramInt1);
        if ((!bool2) || (!getChat(Integer.valueOf(paramInt1)).megagroup)) {
          break;
        }
        bool1 = true;
        localInputUser = getInputUser(paramUser);
        if ((paramString != null) && ((!bool2) || (bool1))) {
          break label254;
        }
        if (!bool2) {
          break label226;
        }
        if (!(localInputUser instanceof TLRPC.TL_inputUserSelf)) {
          break label145;
        }
      } while (this.joiningToChannels.contains(Integer.valueOf(paramInt1)));
      paramUser = new TLRPC.TL_channels_joinChannel();
      paramUser.channel = getInputChannel(paramInt1);
      this.joiningToChannels.add(Integer.valueOf(paramInt1));
      for (;;)
      {
        ConnectionsManager.getInstance().sendRequest(paramUser, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            if ((bool2) && ((localInputUser instanceof TLRPC.TL_inputUserSelf))) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.joiningToChannels.remove(Integer.valueOf(MessagesController.77.this.val$chat_id));
                }
              });
            }
            if (paramAnonymousTL_error != null)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  boolean bool = true;
                  if (MessagesController.77.this.val$fragment != null)
                  {
                    str = paramAnonymousTL_error.text;
                    localBaseFragment = MessagesController.77.this.val$fragment;
                    if ((MessagesController.77.this.val$isChannel) && (!MessagesController.77.this.val$isMegagroup)) {
                      AlertsCreator.showAddUserAlert(str, localBaseFragment, bool);
                    }
                  }
                  while (!paramAnonymousTL_error.text.equals("PEER_FLOOD")) {
                    for (;;)
                    {
                      String str;
                      BaseFragment localBaseFragment;
                      return;
                      bool = false;
                    }
                  }
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(1) });
                }
              });
              return;
            }
            int k = 0;
            paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
            int i = 0;
            for (;;)
            {
              int j = k;
              if (i < paramAnonymousTLObject.updates.size())
              {
                paramAnonymousTL_error = (TLRPC.Update)paramAnonymousTLObject.updates.get(i);
                if (((paramAnonymousTL_error instanceof TLRPC.TL_updateNewChannelMessage)) && ((((TLRPC.TL_updateNewChannelMessage)paramAnonymousTL_error).message.action instanceof TLRPC.TL_messageActionChatAddUser))) {
                  j = 1;
                }
              }
              else
              {
                MessagesController.this.processUpdates(paramAnonymousTLObject, false);
                if (bool2)
                {
                  if ((j == 0) && ((localInputUser instanceof TLRPC.TL_inputUserSelf))) {
                    MessagesController.this.generateJoinMessage(paramInt1, true);
                  }
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.this.loadFullChat(MessagesController.77.this.val$chat_id, 0, true);
                    }
                  }, 1000L);
                }
                if ((!bool2) || (!(localInputUser instanceof TLRPC.TL_inputUserSelf))) {
                  break;
                }
                MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), true, paramInt1);
                return;
              }
              i += 1;
            }
          }
        });
        return;
        bool1 = false;
        break;
        if ((paramUser.bot) && (!bool1))
        {
          paramChatFull = new TLRPC.TL_channels_editAdmin();
          paramChatFull.channel = getInputChannel(paramInt1);
          paramChatFull.user_id = getInputUser(paramUser);
          paramChatFull.role = new TLRPC.TL_channelRoleEditor();
          paramUser = paramChatFull;
        }
        else
        {
          paramUser = new TLRPC.TL_channels_inviteToChannel();
          paramUser.channel = getInputChannel(paramInt1);
          paramUser.users.add(localInputUser);
          continue;
          paramUser = new TLRPC.TL_messages_addChatUser();
          paramUser.chat_id = paramInt1;
          paramUser.fwd_limit = paramInt2;
          paramUser.user_id = localInputUser;
        }
      }
      paramUser = new TLRPC.TL_messages_startBot();
      paramUser.bot = localInputUser;
      if (bool2) {
        paramUser.peer = getInputPeer(-paramInt1);
      }
      for (;;)
      {
        paramUser.start_param = paramString;
        paramUser.random_id = Utilities.random.nextLong();
        break;
        paramUser.peer = new TLRPC.TL_inputPeerChat();
        paramUser.peer.chat_id = paramInt1;
      }
    } while (!(paramChatFull instanceof TLRPC.TL_chatFull));
    paramInt2 = 0;
    for (;;)
    {
      if (paramInt2 >= paramChatFull.participants.participants.size()) {
        break label382;
      }
      if (((TLRPC.ChatParticipant)paramChatFull.participants.participants.get(paramInt2)).user_id == paramUser.id) {
        break;
      }
      paramInt2 += 1;
    }
    label382:
    paramString = getChat(Integer.valueOf(paramInt1));
    paramString.participants_count += 1;
    paramBaseFragment = new ArrayList();
    paramBaseFragment.add(paramString);
    MessagesStorage.getInstance().putUsersAndChats(null, paramBaseFragment, true, true);
    paramString = new TLRPC.TL_chatParticipant();
    paramString.user_id = paramUser.id;
    paramString.inviter_id = UserConfig.getClientUserId();
    paramString.date = ConnectionsManager.getInstance().getCurrentTime();
    paramChatFull.participants.participants.add(0, paramString);
    MessagesStorage.getInstance().updateChatInfo(paramChatFull, true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(false), null });
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(32) });
  }
  
  public void addUsersToChannel(int paramInt, ArrayList<TLRPC.InputUser> paramArrayList, final BaseFragment paramBaseFragment)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    TLRPC.TL_channels_inviteToChannel localTL_channels_inviteToChannel = new TLRPC.TL_channels_inviteToChannel();
    localTL_channels_inviteToChannel.channel = getInputChannel(paramInt);
    localTL_channels_inviteToChannel.users = paramArrayList;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_inviteToChannel, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error != null)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (MessagesController.69.this.val$fragment != null) {
                AlertsCreator.showAddUserAlert(paramAnonymousTL_error.text, MessagesController.69.this.val$fragment, true);
              }
              while (!paramAnonymousTL_error.text.equals("PEER_FLOOD")) {
                return;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(1) });
            }
          });
          return;
        }
        MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
      }
    });
  }
  
  public void blockUser(int paramInt)
  {
    final TLRPC.User localUser = getUser(Integer.valueOf(paramInt));
    if ((localUser == null) || (this.blockedUsers.contains(Integer.valueOf(paramInt)))) {
      return;
    }
    this.blockedUsers.add(Integer.valueOf(paramInt));
    if (localUser.bot) {
      SearchQuery.removeInline(paramInt);
    }
    for (;;)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
      TLRPC.TL_contacts_block localTL_contacts_block = new TLRPC.TL_contacts_block();
      localTL_contacts_block.id = getInputUser(localUser);
      ConnectionsManager.getInstance().sendRequest(localTL_contacts_block, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = new ArrayList();
            paramAnonymousTLObject.add(Integer.valueOf(localUser.id));
            MessagesStorage.getInstance().putBlockedUsers(paramAnonymousTLObject, false);
          }
        }
      });
      return;
      SearchQuery.removePeer(paramInt);
    }
  }
  
  public void cancelLoadFullChat(int paramInt)
  {
    this.loadingFullChats.remove(Integer.valueOf(paramInt));
  }
  
  public void cancelLoadFullUser(int paramInt)
  {
    this.loadingFullUsers.remove(Integer.valueOf(paramInt));
  }
  
  public void cancelTyping(int paramInt, long paramLong)
  {
    HashMap localHashMap = (HashMap)this.sendingTypings.get(Integer.valueOf(paramInt));
    if (localHashMap != null) {
      localHashMap.remove(Long.valueOf(paramLong));
    }
  }
  
  public void changeChatAvatar(int paramInt, TLRPC.InputFile paramInputFile)
  {
    if (ChatObject.isChannel(paramInt))
    {
      localObject = new TLRPC.TL_channels_editPhoto();
      ((TLRPC.TL_channels_editPhoto)localObject).channel = getInputChannel(paramInt);
      if (paramInputFile != null)
      {
        ((TLRPC.TL_channels_editPhoto)localObject).photo = new TLRPC.TL_inputChatUploadedPhoto();
        ((TLRPC.TL_channels_editPhoto)localObject).photo.file = paramInputFile;
      }
      for (;;)
      {
        paramInputFile = (TLRPC.InputFile)localObject;
        ConnectionsManager.getInstance().sendRequest(paramInputFile, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error != null) {
              return;
            }
            MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          }
        }, 64);
        return;
        ((TLRPC.TL_channels_editPhoto)localObject).photo = new TLRPC.TL_inputChatPhotoEmpty();
      }
    }
    Object localObject = new TLRPC.TL_messages_editChatPhoto();
    ((TLRPC.TL_messages_editChatPhoto)localObject).chat_id = paramInt;
    if (paramInputFile != null)
    {
      ((TLRPC.TL_messages_editChatPhoto)localObject).photo = new TLRPC.TL_inputChatUploadedPhoto();
      ((TLRPC.TL_messages_editChatPhoto)localObject).photo.file = paramInputFile;
    }
    for (;;)
    {
      paramInputFile = (TLRPC.InputFile)localObject;
      break;
      ((TLRPC.TL_messages_editChatPhoto)localObject).photo = new TLRPC.TL_inputChatPhotoEmpty();
    }
  }
  
  public void changeChatTitle(int paramInt, String paramString)
  {
    if (paramInt > 0)
    {
      if (ChatObject.isChannel(paramInt))
      {
        localObject = new TLRPC.TL_channels_editTitle();
        ((TLRPC.TL_channels_editTitle)localObject).channel = getInputChannel(paramInt);
        ((TLRPC.TL_channels_editTitle)localObject).title = paramString;
      }
      for (paramString = (String)localObject;; paramString = (String)localObject)
      {
        ConnectionsManager.getInstance().sendRequest(paramString, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error != null) {
              return;
            }
            MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          }
        }, 64);
        return;
        localObject = new TLRPC.TL_messages_editChatTitle();
        ((TLRPC.TL_messages_editChatTitle)localObject).chat_id = paramInt;
        ((TLRPC.TL_messages_editChatTitle)localObject).title = paramString;
      }
    }
    Object localObject = getChat(Integer.valueOf(paramInt));
    ((TLRPC.Chat)localObject).title = paramString;
    paramString = new ArrayList();
    paramString.add(localObject);
    MessagesStorage.getInstance().putUsersAndChats(null, paramString, true, true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(16) });
  }
  
  public void checkChannelInviter(final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        final TLRPC.Chat localChat = MessagesController.this.getChat(Integer.valueOf(paramInt));
        if ((localChat == null) || (!ChatObject.isChannel(paramInt)) || (localChat.creator)) {
          return;
        }
        TLRPC.TL_channels_getParticipant localTL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
        localTL_channels_getParticipant.channel = MessagesController.getInputChannel(paramInt);
        localTL_channels_getParticipant.user_id = new TLRPC.TL_inputUserSelf();
        ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipant, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
          {
            paramAnonymous2TLObject = (TLRPC.TL_channels_channelParticipant)paramAnonymous2TLObject;
            if ((paramAnonymous2TLObject == null) || (!(paramAnonymous2TLObject.participant instanceof TLRPC.TL_channelParticipantSelf)) || (paramAnonymous2TLObject.participant.inviter_id == UserConfig.getClientUserId()) || ((localChat.megagroup) && (MessagesStorage.getInstance().isMigratedChat(localChat.id)))) {
              return;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.putUsers(paramAnonymous2TLObject.users, false);
              }
            });
            MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject.users, null, true, true);
            paramAnonymous2TL_error = new TLRPC.TL_messageService();
            paramAnonymous2TL_error.media_unread = true;
            paramAnonymous2TL_error.unread = true;
            paramAnonymous2TL_error.flags = 256;
            paramAnonymous2TL_error.post = true;
            if (localChat.megagroup) {
              paramAnonymous2TL_error.flags |= 0x80000000;
            }
            int i = UserConfig.getNewMessageId();
            paramAnonymous2TL_error.id = i;
            paramAnonymous2TL_error.local_id = i;
            paramAnonymous2TL_error.date = paramAnonymous2TLObject.participant.date;
            paramAnonymous2TL_error.action = new TLRPC.TL_messageActionChatAddUser();
            paramAnonymous2TL_error.from_id = paramAnonymous2TLObject.participant.inviter_id;
            paramAnonymous2TL_error.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
            paramAnonymous2TL_error.to_id = new TLRPC.TL_peerChannel();
            paramAnonymous2TL_error.to_id.channel_id = MessagesController.96.this.val$chat_id;
            paramAnonymous2TL_error.dialog_id = (-MessagesController.96.this.val$chat_id);
            UserConfig.saveConfig(false);
            final ArrayList localArrayList1 = new ArrayList();
            ArrayList localArrayList2 = new ArrayList();
            ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
            i = 0;
            while (i < paramAnonymous2TLObject.users.size())
            {
              TLRPC.User localUser = (TLRPC.User)paramAnonymous2TLObject.users.get(i);
              localConcurrentHashMap.put(Integer.valueOf(localUser.id), localUser);
              i += 1;
            }
            localArrayList2.add(paramAnonymous2TL_error);
            localArrayList1.add(new MessageObject(paramAnonymous2TL_error, localConcurrentHashMap, true));
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
            {
              public void run()
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationsController.getInstance().processNewMessages(MessagesController.96.1.2.this.val$pushMessages, true);
                  }
                });
              }
            });
            MessagesStorage.getInstance().putMessages(localArrayList2, true, true, false, MediaController.getInstance().getAutodownloadMask());
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.updateInterfaceWithMessages(-MessagesController.96.this.val$chat_id, localArrayList1);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              }
            });
          }
        });
      }
    });
  }
  
  protected void checkLastDialogMessage(final TLRPC.TL_dialog paramTL_dialog, TLRPC.InputPeer paramInputPeer, final long paramLong)
  {
    int i = (int)paramTL_dialog.id;
    if ((i == 0) || (this.checkingLastMessagesDialogs.containsKey(Integer.valueOf(i)))) {}
    TLRPC.TL_messages_getHistory localTL_messages_getHistory;
    Object localObject1;
    do
    {
      return;
      localTL_messages_getHistory = new TLRPC.TL_messages_getHistory();
      if (paramInputPeer != null) {
        break;
      }
      localObject1 = getInputPeer(i);
      localTL_messages_getHistory.peer = ((TLRPC.InputPeer)localObject1);
    } while (localTL_messages_getHistory.peer == null);
    localTL_messages_getHistory.limit = 1;
    this.checkingLastMessagesDialogs.put(Integer.valueOf(i), Boolean.valueOf(true));
    Object localObject3;
    if (paramLong == 0L) {
      localObject3 = null;
    }
    for (;;)
    {
      try
      {
        localObject1 = new NativeByteBuffer(paramInputPeer.getObjectSize() + 40);
      }
      catch (Exception localException1)
      {
        paramInputPeer = (TLRPC.InputPeer)localObject3;
      }
      try
      {
        ((NativeByteBuffer)localObject1).writeInt32(2);
        ((NativeByteBuffer)localObject1).writeInt64(paramTL_dialog.id);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.top_message);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.read_inbox_max_id);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.read_outbox_max_id);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.unread_count);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.last_message_date);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.pts);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.flags);
        paramInputPeer.serializeToStream((AbstractSerializedData)localObject1);
        paramInputPeer = (TLRPC.InputPeer)localObject1;
        paramLong = MessagesStorage.getInstance().createPendingTask(paramInputPeer);
        ConnectionsManager.getInstance().sendRequest(localTL_messages_getHistory, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTLObject != null)
            {
              paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
              if (paramAnonymousTLObject.messages.isEmpty()) {
                break label272;
              }
              paramAnonymousTL_error = new TLRPC.TL_messages_dialogs();
              TLRPC.Message localMessage = (TLRPC.Message)paramAnonymousTLObject.messages.get(0);
              TLRPC.TL_dialog localTL_dialog = new TLRPC.TL_dialog();
              localTL_dialog.flags = paramTL_dialog.flags;
              localTL_dialog.top_message = localMessage.id;
              localTL_dialog.last_message_date = localMessage.date;
              localTL_dialog.notify_settings = paramTL_dialog.notify_settings;
              localTL_dialog.pts = paramTL_dialog.pts;
              localTL_dialog.unread_count = paramTL_dialog.unread_count;
              localTL_dialog.read_inbox_max_id = paramTL_dialog.read_inbox_max_id;
              localTL_dialog.read_outbox_max_id = paramTL_dialog.read_outbox_max_id;
              long l = paramTL_dialog.id;
              localTL_dialog.id = l;
              localMessage.dialog_id = l;
              paramAnonymousTL_error.users.addAll(paramAnonymousTLObject.users);
              paramAnonymousTL_error.chats.addAll(paramAnonymousTLObject.chats);
              paramAnonymousTL_error.dialogs.add(localTL_dialog);
              paramAnonymousTL_error.messages.addAll(paramAnonymousTLObject.messages);
              paramAnonymousTL_error.count = 1;
              MessagesController.this.processDialogsUpdate(paramAnonymousTL_error, null);
              MessagesStorage.getInstance().putMessages(paramAnonymousTLObject.messages, true, true, false, MediaController.getInstance().getAutodownloadMask(), true);
            }
            for (;;)
            {
              if (paramLong != 0L) {
                MessagesStorage.getInstance().removePendingTask(paramLong);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.checkingLastMessagesDialogs.remove(Integer.valueOf(MessagesController.57.this.val$lower_id));
                }
              });
              return;
              label272:
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(MessagesController.57.this.val$dialog.id));
                  if ((localTL_dialog != null) && (localTL_dialog.top_message == 0)) {
                    MessagesController.this.deleteDialog(MessagesController.57.this.val$dialog.id, 3);
                  }
                }
              });
            }
          }
        });
        return;
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          paramInputPeer = localException1;
          Object localObject2 = localException2;
        }
      }
      localObject1 = paramInputPeer;
      break;
      FileLog.e("tmessages", localException1);
    }
  }
  
  public void cleanup()
  {
    ContactsController.getInstance().cleanup();
    MediaController.getInstance().cleanup();
    NotificationsController.getInstance().cleanup();
    SendMessagesHelper.getInstance().cleanup();
    SecretChatHelper.getInstance().cleanup();
    StickersQuery.cleanup();
    SearchQuery.cleanup();
    DraftQuery.cleanup();
    this.reloadingWebpages.clear();
    this.reloadingWebpagesPending.clear();
    this.dialogs_dict.clear();
    this.dialogs_read_inbox_max.clear();
    this.dialogs_read_outbox_max.clear();
    this.exportedChats.clear();
    this.fullUsersAbout.clear();
    this.dialogs.clear();
    this.joiningToChannels.clear();
    this.channelViewsToSend.clear();
    this.channelViewsToReload.clear();
    this.dialogsServerOnly.clear();
    this.dialogsGroupsOnly.clear();
    this.dialogMessagesByIds.clear();
    this.dialogMessagesByRandomIds.clear();
    this.users.clear();
    this.usersByUsernames.clear();
    this.chats.clear();
    this.dialogMessage.clear();
    this.printingUsers.clear();
    this.printingStrings.clear();
    this.printingStringsTypes.clear();
    this.onlinePrivacy.clear();
    this.loadingPeerSettings.clear();
    this.lastPrintingStringCount = 0;
    this.nextDialogsCacheOffset = 0;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.this.updatesQueueSeq.clear();
        MessagesController.this.updatesQueuePts.clear();
        MessagesController.this.updatesQueueQts.clear();
        MessagesController.this.gettingUnknownChannels.clear();
        MessagesController.access$702(MessagesController.this, 0L);
        MessagesController.access$802(MessagesController.this, 0L);
        MessagesController.access$902(MessagesController.this, 0L);
        MessagesController.this.createdDialogIds.clear();
        MessagesController.this.gettingDifference = false;
      }
    });
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
    this.gettingNewDeleteTask = false;
    this.loadingDialogs = false;
    this.dialogsEndReached = false;
    this.loadingBlockedUsers = false;
    this.firstGettingTask = false;
    this.updatingState = false;
    this.lastStatusUpdateTime = 0L;
    this.offlineSent = false;
    this.registeringForPush = false;
    this.uploadingAvatar = null;
    this.statusRequest = 0;
    this.statusSettingState = 0;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ConnectionsManager.getInstance().setIsUpdating(false);
        MessagesController.this.updatesQueueChannels.clear();
        MessagesController.this.updatesStartWaitTimeChannels.clear();
        MessagesController.this.gettingDifferenceChannels.clear();
        MessagesController.this.channelsPts.clear();
        MessagesController.this.shortPollChannels.clear();
        MessagesController.this.needShortPollChannels.clear();
      }
    });
    if (this.currentDeleteTaskRunnable != null)
    {
      Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
      this.currentDeleteTaskRunnable = null;
    }
    addSupportUser();
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
  }
  
  protected void clearFullUsers()
  {
    this.loadedFullUsers.clear();
    this.loadedFullChats.clear();
  }
  
  public void convertGroup() {}
  
  public void convertToMegaGroup(final Context paramContext, final int paramInt)
  {
    TLRPC.TL_messages_migrateChat localTL_messages_migrateChat = new TLRPC.TL_messages_migrateChat();
    localTL_messages_migrateChat.chat_id = paramInt;
    final ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
    localProgressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
    localProgressDialog.setCanceledOnTouchOutside(false);
    localProgressDialog.setCancelable(false);
    paramInt = ConnectionsManager.getInstance().sendRequest(localTL_messages_migrateChat, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!((Activity)MessagesController.67.this.val$context).isFinishing()) {}
              try
              {
                MessagesController.67.this.val$progressDialog.dismiss();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          });
          paramAnonymousTL_error = (TLRPC.Updates)paramAnonymousTLObject;
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (!((Activity)MessagesController.67.this.val$context).isFinishing()) {}
            try
            {
              MessagesController.67.this.val$progressDialog.dismiss();
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(MessagesController.67.this.val$context);
              localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
              localBuilder.setMessage(LocaleController.getString("ErrorOccurred", 2131165626));
              localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
              localBuilder.show().setCanceledOnTouchOutside(true);
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException);
              }
            }
          }
        });
      }
    });
    localProgressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ConnectionsManager.getInstance().cancelRequest(paramInt, true);
        try
        {
          paramAnonymousDialogInterface.dismiss();
          return;
        }
        catch (Exception paramAnonymousDialogInterface)
        {
          FileLog.e("tmessages", paramAnonymousDialogInterface);
        }
      }
    });
    try
    {
      localProgressDialog.show();
      return;
    }
    catch (Exception paramContext) {}
  }
  
  public int createChat(String paramString1, ArrayList<Integer> paramArrayList, String paramString2, int paramInt, final BaseFragment paramBaseFragment)
  {
    if (paramInt == 1)
    {
      paramString2 = new TLRPC.TL_chat();
      paramString2.id = UserConfig.lastBroadcastId;
      paramString2.title = paramString1;
      paramString2.photo = new TLRPC.TL_chatPhotoEmpty();
      paramString2.participants_count = paramArrayList.size();
      paramString2.date = ((int)(System.currentTimeMillis() / 1000L));
      paramString2.version = 1;
      UserConfig.lastBroadcastId -= 1;
      putChat(paramString2, false);
      paramString1 = new ArrayList();
      paramString1.add(paramString2);
      MessagesStorage.getInstance().putUsersAndChats(null, paramString1, true, true);
      paramString1 = new TLRPC.TL_chatFull();
      paramString1.id = paramString2.id;
      paramString1.chat_photo = new TLRPC.TL_photoEmpty();
      paramString1.notify_settings = new TLRPC.TL_peerNotifySettingsEmpty();
      paramString1.exported_invite = new TLRPC.TL_chatInviteEmpty();
      paramString1.participants = new TLRPC.TL_chatParticipants();
      paramString1.participants.chat_id = paramString2.id;
      paramString1.participants.admin_id = UserConfig.getClientUserId();
      paramString1.participants.version = 1;
      paramInt = 0;
      while (paramInt < paramArrayList.size())
      {
        paramBaseFragment = new TLRPC.TL_chatParticipant();
        paramBaseFragment.user_id = ((Integer)paramArrayList.get(paramInt)).intValue();
        paramBaseFragment.inviter_id = UserConfig.getClientUserId();
        paramBaseFragment.date = ((int)(System.currentTimeMillis() / 1000L));
        paramString1.participants.participants.add(paramBaseFragment);
        paramInt += 1;
      }
      MessagesStorage.getInstance().updateChatInfo(paramString1, false);
      paramString1 = new TLRPC.TL_messageService();
      paramString1.action = new TLRPC.TL_messageActionCreatedBroadcastList();
      paramInt = UserConfig.getNewMessageId();
      paramString1.id = paramInt;
      paramString1.local_id = paramInt;
      paramString1.from_id = UserConfig.getClientUserId();
      paramString1.dialog_id = AndroidUtilities.makeBroadcastId(paramString2.id);
      paramString1.to_id = new TLRPC.TL_peerChat();
      paramString1.to_id.chat_id = paramString2.id;
      paramString1.date = ConnectionsManager.getInstance().getCurrentTime();
      paramString1.random_id = 0L;
      paramString1.flags |= 0x100;
      UserConfig.saveConfig(false);
      paramBaseFragment = new MessageObject(paramString1, this.users, true);
      paramBaseFragment.messageOwner.send_state = 0;
      paramArrayList = new ArrayList();
      paramArrayList.add(paramBaseFragment);
      paramBaseFragment = new ArrayList();
      paramBaseFragment.add(paramString1);
      MessagesStorage.getInstance().putMessages(paramBaseFragment, false, true, false, 0);
      updateInterfaceWithMessages(paramString1.dialog_id, paramArrayList);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(paramString2.id) });
      return 0;
    }
    if (paramInt == 0)
    {
      paramString2 = new TLRPC.TL_messages_createChat();
      paramString2.title = paramString1;
      paramInt = 0;
      if (paramInt < paramArrayList.size())
      {
        paramString1 = getUser((Integer)paramArrayList.get(paramInt));
        if (paramString1 == null) {}
        for (;;)
        {
          paramInt += 1;
          break;
          paramString2.users.add(getInputUser(paramString1));
        }
      }
      ConnectionsManager.getInstance().sendRequest(paramString2, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                  AlertsCreator.showFloodWaitAlert(paramAnonymousTL_error.text, MessagesController.65.this.val$fragment);
                }
                for (;;)
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                  return;
                  AlertsCreator.showAddUserAlert(paramAnonymousTL_error.text, MessagesController.65.this.val$fragment, false);
                }
              }
            });
            return;
          }
          paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
          MessagesController.this.processUpdates(paramAnonymousTLObject, false);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.putUsers(paramAnonymousTLObject.users, false);
              MessagesController.this.putChats(paramAnonymousTLObject.chats, false);
              if ((paramAnonymousTLObject.chats != null) && (!paramAnonymousTLObject.chats.isEmpty()))
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(((TLRPC.Chat)paramAnonymousTLObject.chats.get(0)).id) });
                return;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            }
          });
        }
      }, 2);
    }
    if ((paramInt == 2) || (paramInt == 4))
    {
      paramArrayList = new TLRPC.TL_channels_createChannel();
      paramArrayList.title = paramString1;
      paramArrayList.about = paramString2;
      if (paramInt == 4) {
        paramArrayList.megagroup = true;
      }
      for (;;)
      {
        ConnectionsManager.getInstance().sendRequest(paramArrayList, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error != null)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                    AlertsCreator.showFloodWaitAlert(paramAnonymousTL_error.text, MessagesController.66.this.val$fragment);
                  }
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                }
              });
              return;
            }
            paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
            MessagesController.this.processUpdates(paramAnonymousTLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.putUsers(paramAnonymousTLObject.users, false);
                MessagesController.this.putChats(paramAnonymousTLObject.chats, false);
                if ((paramAnonymousTLObject.chats != null) && (!paramAnonymousTLObject.chats.isEmpty()))
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(((TLRPC.Chat)paramAnonymousTLObject.chats.get(0)).id) });
                  return;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
              }
            });
          }
        }, 2);
        paramArrayList.broadcast = true;
      }
    }
    return 0;
  }
  
  public void deleteDialog(long paramLong, int paramInt)
  {
    deleteDialog(paramLong, true, paramInt, 0);
  }
  
  public void deleteMessages(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, TLRPC.EncryptedChat paramEncryptedChat, final int paramInt)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    if (paramInt == 0)
    {
      i = 0;
      while (i < paramArrayList.size())
      {
        localObject = (Integer)paramArrayList.get(i);
        localObject = (MessageObject)this.dialogMessagesByIds.get(localObject);
        if (localObject != null) {
          ((MessageObject)localObject).deleted = true;
        }
        i += 1;
      }
    }
    markChannelDialogMessageAsDeleted(paramArrayList, paramInt);
    Object localObject = new ArrayList();
    int i = 0;
    while (i < paramArrayList.size())
    {
      Integer localInteger = (Integer)paramArrayList.get(i);
      if (localInteger.intValue() > 0) {
        ((ArrayList)localObject).add(localInteger);
      }
      i += 1;
    }
    MessagesStorage.getInstance().markMessagesAsDeleted(paramArrayList, true, paramInt);
    MessagesStorage.getInstance().updateDialogsWithDeletedMessages(paramArrayList, true, paramInt);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, new Object[] { paramArrayList, Integer.valueOf(paramInt) });
    if (paramInt != 0)
    {
      paramArrayList = new TLRPC.TL_channels_deleteMessages();
      paramArrayList.id = ((ArrayList)localObject);
      paramArrayList.channel = getInputChannel(paramInt);
      ConnectionsManager.getInstance().sendRequest(paramArrayList, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
            MessagesController.this.processNewChannelDifferenceParams(paramAnonymousTLObject.pts, paramAnonymousTLObject.pts_count, paramInt);
          }
        }
      });
      return;
    }
    if ((paramArrayList1 != null) && (paramEncryptedChat != null) && (!paramArrayList1.isEmpty())) {
      SecretChatHelper.getInstance().sendMessagesDeleteMessage(paramEncryptedChat, paramArrayList1, null);
    }
    paramArrayList = new TLRPC.TL_messages_deleteMessages();
    paramArrayList.id = ((ArrayList)localObject);
    ConnectionsManager.getInstance().sendRequest(paramArrayList, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
          MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
        }
      }
    });
  }
  
  public void deleteUserChannelHistory(final TLRPC.Chat paramChat, final TLRPC.User paramUser, int paramInt)
  {
    if (paramInt == 0) {
      MessagesStorage.getInstance().deleteUserChannelHistory(paramChat.id, paramUser.id);
    }
    TLRPC.TL_channels_deleteUserHistory localTL_channels_deleteUserHistory = new TLRPC.TL_channels_deleteUserHistory();
    localTL_channels_deleteUserHistory.channel = getInputChannel(paramChat);
    localTL_channels_deleteUserHistory.user_id = getInputUser(paramUser);
    ConnectionsManager.getInstance().sendRequest(localTL_channels_deleteUserHistory, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.TL_messages_affectedHistory)paramAnonymousTLObject;
          if (paramAnonymousTLObject.offset > 0) {
            MessagesController.this.deleteUserChannelHistory(paramChat, paramUser, paramAnonymousTLObject.offset);
          }
          MessagesController.this.processNewChannelDifferenceParams(paramAnonymousTLObject.pts, paramAnonymousTLObject.pts_count, paramChat.id);
        }
      }
    });
  }
  
  public void deleteUserFromChat(final int paramInt, final TLRPC.User paramUser, TLRPC.ChatFull paramChatFull)
  {
    if (paramUser == null) {}
    do
    {
      return;
      if (paramInt > 0)
      {
        localObject1 = getInputUser(paramUser);
        localObject2 = getChat(Integer.valueOf(paramInt));
        final boolean bool = ChatObject.isChannel((TLRPC.Chat)localObject2);
        if (bool) {
          if ((localObject1 instanceof TLRPC.TL_inputUserSelf)) {
            if (((TLRPC.Chat)localObject2).creator)
            {
              paramChatFull = new TLRPC.TL_channels_deleteChannel();
              paramChatFull.channel = getInputChannel((TLRPC.Chat)localObject2);
            }
          }
        }
        for (;;)
        {
          ConnectionsManager.getInstance().sendRequest(paramChatFull, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramUser.id == UserConfig.getClientUserId()) {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.deleteDialog(-MessagesController.78.this.val$chat_id, 0);
                  }
                });
              }
              if (paramAnonymousTL_error != null) {}
              do
              {
                return;
                paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
                MessagesController.this.processUpdates(paramAnonymousTLObject, false);
              } while ((!bool) || ((localObject1 instanceof TLRPC.TL_inputUserSelf)));
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.loadFullChat(MessagesController.78.this.val$chat_id, 0, true);
                }
              }, 1000L);
            }
          }, 64);
          return;
          paramChatFull = new TLRPC.TL_channels_leaveChannel();
          paramChatFull.channel = getInputChannel((TLRPC.Chat)localObject2);
          continue;
          paramChatFull = new TLRPC.TL_channels_kickFromChannel();
          paramChatFull.channel = getInputChannel((TLRPC.Chat)localObject2);
          paramChatFull.user_id = ((TLRPC.InputUser)localObject1);
          paramChatFull.kicked = true;
          continue;
          paramChatFull = new TLRPC.TL_messages_deleteChatUser();
          paramChatFull.chat_id = paramInt;
          paramChatFull.user_id = getInputUser(paramUser);
        }
      }
    } while (!(paramChatFull instanceof TLRPC.TL_chatFull));
    final Object localObject1 = getChat(Integer.valueOf(paramInt));
    ((TLRPC.Chat)localObject1).participants_count -= 1;
    Object localObject2 = new ArrayList();
    ((ArrayList)localObject2).add(localObject1);
    MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject2, true, true);
    int j = 0;
    paramInt = 0;
    for (;;)
    {
      int i = j;
      if (paramInt < paramChatFull.participants.participants.size())
      {
        if (((TLRPC.ChatParticipant)paramChatFull.participants.participants.get(paramInt)).user_id == paramUser.id)
        {
          paramChatFull.participants.participants.remove(paramInt);
          i = 1;
        }
      }
      else
      {
        if (i != 0)
        {
          MessagesStorage.getInstance().updateChatInfo(paramChatFull, true);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(false), null });
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(32) });
        return;
      }
      paramInt += 1;
    }
  }
  
  public void deleteUserPhoto(TLRPC.InputPhoto paramInputPhoto)
  {
    if (paramInputPhoto == null)
    {
      TLRPC.TL_photos_updateProfilePhoto localTL_photos_updateProfilePhoto = new TLRPC.TL_photos_updateProfilePhoto();
      localTL_photos_updateProfilePhoto.id = new TLRPC.TL_inputPhotoEmpty();
      UserConfig.getCurrentUser().photo = new TLRPC.TL_userProfilePhotoEmpty();
      localObject = getUser(Integer.valueOf(UserConfig.getClientUserId()));
      paramInputPhoto = (TLRPC.InputPhoto)localObject;
      if (localObject == null) {
        paramInputPhoto = UserConfig.getCurrentUser();
      }
      if (paramInputPhoto == null) {
        return;
      }
      paramInputPhoto.photo = UserConfig.getCurrentUser().photo;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
      ConnectionsManager.getInstance().sendRequest(localTL_photos_updateProfilePhoto, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
            if (paramAnonymousTL_error != null) {
              break label41;
            }
            paramAnonymousTL_error = UserConfig.getCurrentUser();
            MessagesController.this.putUser(paramAnonymousTL_error, false);
          }
          while (paramAnonymousTL_error == null)
          {
            return;
            label41:
            UserConfig.setCurrentUser(paramAnonymousTL_error);
          }
          MessagesStorage.getInstance().clearUserPhotos(paramAnonymousTL_error.id);
          ArrayList localArrayList = new ArrayList();
          localArrayList.add(paramAnonymousTL_error);
          MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, false, true);
          paramAnonymousTL_error.photo = ((TLRPC.UserProfilePhoto)paramAnonymousTLObject);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
              UserConfig.saveConfig(true);
            }
          });
        }
      });
      return;
    }
    Object localObject = new TLRPC.TL_photos_deletePhotos();
    ((TLRPC.TL_photos_deletePhotos)localObject).id.add(paramInputPhoto);
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void didAddedNewTask(final int paramInt, final SparseArray<ArrayList<Integer>> paramSparseArray)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (((MessagesController.this.currentDeletingTaskMids == null) && (!MessagesController.this.gettingNewDeleteTask)) || ((MessagesController.this.currentDeletingTaskTime != 0) && (paramInt < MessagesController.this.currentDeletingTaskTime))) {
          MessagesController.this.getNewDeleteTask(null);
        }
      }
    });
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didCreatedNewDeleteTask, new Object[] { paramSparseArray });
      }
    });
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    Object localObject;
    if (paramInt == NotificationCenter.FileDidUpload)
    {
      localObject = (String)paramVarArgs[0];
      paramVarArgs = (TLRPC.InputFile)paramVarArgs[1];
      if ((this.uploadingAvatar != null) && (this.uploadingAvatar.equals(localObject)))
      {
        localObject = new TLRPC.TL_photos_uploadProfilePhoto();
        ((TLRPC.TL_photos_uploadProfilePhoto)localObject).file = paramVarArgs;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
              if (paramAnonymousTL_error != null) {
                break label41;
              }
              paramAnonymousTL_error = UserConfig.getCurrentUser();
              MessagesController.this.putUser(paramAnonymousTL_error, true);
            }
            while (paramAnonymousTL_error == null)
            {
              return;
              label41:
              UserConfig.setCurrentUser(paramAnonymousTL_error);
            }
            paramAnonymousTLObject = (TLRPC.TL_photos_photo)paramAnonymousTLObject;
            Object localObject = paramAnonymousTLObject.photo.sizes;
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 100);
            localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 1000);
            paramAnonymousTL_error.photo = new TLRPC.TL_userProfilePhoto();
            paramAnonymousTL_error.photo.photo_id = paramAnonymousTLObject.photo.id;
            if (localPhotoSize != null) {
              paramAnonymousTL_error.photo.photo_small = localPhotoSize.location;
            }
            if (localObject != null) {
              paramAnonymousTL_error.photo.photo_big = ((TLRPC.PhotoSize)localObject).location;
            }
            for (;;)
            {
              MessagesStorage.getInstance().clearUserPhotos(paramAnonymousTL_error.id);
              paramAnonymousTLObject = new ArrayList();
              paramAnonymousTLObject.add(paramAnonymousTL_error);
              MessagesStorage.getInstance().putUsersAndChats(paramAnonymousTLObject, null, false, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(2) });
                  UserConfig.saveConfig(true);
                }
              });
              return;
              if (localPhotoSize != null) {
                paramAnonymousTL_error.photo.photo_small = localPhotoSize.location;
              }
            }
          }
        });
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (paramInt != NotificationCenter.FileDidFailUpload) {
            break;
          }
          paramVarArgs = (String)paramVarArgs[0];
        } while ((this.uploadingAvatar == null) || (!this.uploadingAvatar.equals(paramVarArgs)));
        this.uploadingAvatar = null;
        return;
      } while (paramInt != NotificationCenter.messageReceivedByServer);
      Integer localInteger = (Integer)paramVarArgs[0];
      localObject = (Integer)paramVarArgs[1];
      paramVarArgs = (Long)paramVarArgs[3];
      MessageObject localMessageObject = (MessageObject)this.dialogMessage.get(paramVarArgs);
      if ((localMessageObject != null) && (localMessageObject.getId() == localInteger.intValue()))
      {
        localMessageObject.messageOwner.id = ((Integer)localObject).intValue();
        localMessageObject.messageOwner.send_state = 0;
        paramVarArgs = (TLRPC.TL_dialog)this.dialogs_dict.get(paramVarArgs);
        if ((paramVarArgs != null) && (paramVarArgs.top_message == localInteger.intValue())) {
          paramVarArgs.top_message = ((Integer)localObject).intValue();
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      }
      paramVarArgs = (MessageObject)this.dialogMessagesByIds.remove(localInteger);
    } while (paramVarArgs == null);
    this.dialogMessagesByIds.put(localObject, paramVarArgs);
  }
  
  public void generateJoinMessage(final int paramInt, boolean paramBoolean)
  {
    final Object localObject = getChat(Integer.valueOf(paramInt));
    if ((localObject == null) || (!ChatObject.isChannel(paramInt)) || (((((TLRPC.Chat)localObject).left) || (((TLRPC.Chat)localObject).kicked)) && (!paramBoolean))) {
      return;
    }
    TLRPC.TL_messageService localTL_messageService = new TLRPC.TL_messageService();
    localTL_messageService.flags = 256;
    int i = UserConfig.getNewMessageId();
    localTL_messageService.id = i;
    localTL_messageService.local_id = i;
    localTL_messageService.date = ConnectionsManager.getInstance().getCurrentTime();
    localTL_messageService.from_id = UserConfig.getClientUserId();
    localTL_messageService.to_id = new TLRPC.TL_peerChannel();
    localTL_messageService.to_id.channel_id = paramInt;
    localTL_messageService.dialog_id = (-paramInt);
    localTL_messageService.post = true;
    localTL_messageService.action = new TLRPC.TL_messageActionChatAddUser();
    localTL_messageService.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
    if (((TLRPC.Chat)localObject).megagroup) {
      localTL_messageService.flags |= 0x80000000;
    }
    UserConfig.saveConfig(false);
    localObject = new ArrayList();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(localTL_messageService);
    ((ArrayList)localObject).add(new MessageObject(localTL_messageService, null, true));
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationsController.getInstance().processNewMessages(MessagesController.94.this.val$pushMessages, true);
          }
        });
      }
    });
    MessagesStorage.getInstance().putMessages(localArrayList, true, true, false, MediaController.getInstance().getAutodownloadMask());
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.this.updateInterfaceWithMessages(-paramInt, localObject);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      }
    });
  }
  
  public void generateUpdateMessage()
  {
    if ((BuildVars.DEBUG_VERSION) || (UserConfig.lastUpdateVersion == null) || (UserConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING))) {
      return;
    }
    TLRPC.TL_help_getAppChangelog localTL_help_getAppChangelog = new TLRPC.TL_help_getAppChangelog();
    ConnectionsManager.getInstance().sendRequest(localTL_help_getAppChangelog, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          UserConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
          UserConfig.saveConfig(false);
        }
        if ((paramAnonymousTLObject instanceof TLRPC.TL_help_appChangelog))
        {
          paramAnonymousTL_error = new TLRPC.TL_updateServiceNotification();
          paramAnonymousTL_error.message = ((TLRPC.TL_help_appChangelog)paramAnonymousTLObject).text;
          paramAnonymousTL_error.media = new TLRPC.TL_messageMediaEmpty();
          paramAnonymousTL_error.type = "update";
          paramAnonymousTL_error.popup = false;
          paramAnonymousTLObject = new ArrayList();
          paramAnonymousTLObject.add(paramAnonymousTL_error);
          MessagesController.this.processUpdateArray(paramAnonymousTLObject, null, null, false);
        }
      }
    });
  }
  
  public void getBlockedUsers(boolean paramBoolean)
  {
    if ((!UserConfig.isClientActivated()) || (this.loadingBlockedUsers)) {
      return;
    }
    this.loadingBlockedUsers = true;
    if (paramBoolean)
    {
      MessagesStorage.getInstance().getBlockedUsers();
      return;
    }
    TLRPC.TL_contacts_getBlocked localTL_contacts_getBlocked = new TLRPC.TL_contacts_getBlocked();
    localTL_contacts_getBlocked.offset = 0;
    localTL_contacts_getBlocked.limit = 200;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_getBlocked, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        ArrayList localArrayList2 = new ArrayList();
        ArrayList localArrayList1 = null;
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.contacts_Blocked)paramAnonymousTLObject;
          paramAnonymousTL_error = paramAnonymousTLObject.blocked.iterator();
          while (paramAnonymousTL_error.hasNext()) {
            localArrayList2.add(Integer.valueOf(((TLRPC.TL_contactBlocked)paramAnonymousTL_error.next()).user_id));
          }
          localArrayList1 = paramAnonymousTLObject.users;
          MessagesStorage.getInstance().putUsersAndChats(paramAnonymousTLObject.users, null, true, true);
          MessagesStorage.getInstance().putBlockedUsers(localArrayList2, true);
        }
        MessagesController.this.processLoadedBlockedUsers(localArrayList2, localArrayList1, false);
      }
    });
  }
  
  protected void getChannelDifference(final int paramInt1, final int paramInt2, final long paramLong)
  {
    Object localObject2 = (Boolean)this.gettingDifferenceChannels.get(Integer.valueOf(paramInt1));
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = Boolean.valueOf(false);
    }
    if (((Boolean)localObject1).booleanValue()) {}
    do
    {
      return;
      i = 100;
      if (paramInt2 != 1) {
        break;
      }
    } while ((Integer)this.channelsPts.get(Integer.valueOf(paramInt1)) != null);
    localObject2 = Integer.valueOf(1);
    int i = 1;
    label75:
    Object localObject3;
    if (paramLong == 0L) {
      localObject3 = null;
    }
    for (;;)
    {
      try
      {
        localObject1 = new NativeByteBuffer(12);
      }
      catch (Exception localException1)
      {
        localObject1 = localObject3;
      }
      try
      {
        ((NativeByteBuffer)localObject1).writeInt32(1);
        ((NativeByteBuffer)localObject1).writeInt32(paramInt1);
        ((NativeByteBuffer)localObject1).writeInt32(paramInt2);
        paramLong = MessagesStorage.getInstance().createPendingTask((NativeByteBuffer)localObject1);
        this.gettingDifferenceChannels.put(Integer.valueOf(paramInt1), Boolean.valueOf(true));
        localObject1 = new TLRPC.TL_updates_getChannelDifference();
        ((TLRPC.TL_updates_getChannelDifference)localObject1).channel = getInputChannel(paramInt1);
        ((TLRPC.TL_updates_getChannelDifference)localObject1).filter = new TLRPC.TL_channelMessagesFilterEmpty();
        ((TLRPC.TL_updates_getChannelDifference)localObject1).pts = ((Integer)localObject2).intValue();
        ((TLRPC.TL_updates_getChannelDifference)localObject1).limit = i;
        FileLog.e("tmessages", "start getChannelDifference with pts = " + localObject2 + " channelId = " + paramInt1);
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              final TLRPC.updates_ChannelDifference localupdates_ChannelDifference = (TLRPC.updates_ChannelDifference)paramAnonymousTLObject;
              final HashMap localHashMap = new HashMap();
              int i = 0;
              while (i < localupdates_ChannelDifference.users.size())
              {
                paramAnonymousTLObject = (TLRPC.User)localupdates_ChannelDifference.users.get(i);
                localHashMap.put(Integer.valueOf(paramAnonymousTLObject.id), paramAnonymousTLObject);
                i += 1;
              }
              paramAnonymousTL_error = null;
              i = 0;
              for (;;)
              {
                paramAnonymousTLObject = paramAnonymousTL_error;
                if (i < localupdates_ChannelDifference.chats.size())
                {
                  paramAnonymousTLObject = (TLRPC.Chat)localupdates_ChannelDifference.chats.get(i);
                  if (paramAnonymousTLObject.id != paramInt1) {}
                }
                else
                {
                  paramAnonymousTL_error = new ArrayList();
                  if (localupdates_ChannelDifference.other_updates.isEmpty()) {
                    break;
                  }
                  int j;
                  for (i = 0; i < localupdates_ChannelDifference.other_updates.size(); i = j + 1)
                  {
                    TLRPC.Update localUpdate = (TLRPC.Update)localupdates_ChannelDifference.other_updates.get(i);
                    j = i;
                    if ((localUpdate instanceof TLRPC.TL_updateMessageID))
                    {
                      paramAnonymousTL_error.add((TLRPC.TL_updateMessageID)localUpdate);
                      localupdates_ChannelDifference.other_updates.remove(i);
                      j = i - 1;
                    }
                  }
                }
                i += 1;
              }
              MessagesStorage.getInstance().putUsersAndChats(localupdates_ChannelDifference.users, localupdates_ChannelDifference.chats, true, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.putUsers(localupdates_ChannelDifference.users, false);
                  MessagesController.this.putChats(localupdates_ChannelDifference.chats, false);
                }
              });
              MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
              {
                public void run()
                {
                  if (!paramAnonymousTL_error.isEmpty())
                  {
                    final HashMap localHashMap = new HashMap();
                    Iterator localIterator = paramAnonymousTL_error.iterator();
                    while (localIterator.hasNext())
                    {
                      TLRPC.TL_updateMessageID localTL_updateMessageID = (TLRPC.TL_updateMessageID)localIterator.next();
                      long[] arrayOfLong = MessagesStorage.getInstance().updateMessageStateAndId(localTL_updateMessageID.random_id, null, localTL_updateMessageID.id, 0, false, MessagesController.92.this.val$channelId);
                      if (arrayOfLong != null) {
                        localHashMap.put(Integer.valueOf(localTL_updateMessageID.id), arrayOfLong);
                      }
                    }
                    if (!localHashMap.isEmpty()) {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          Iterator localIterator = localHashMap.entrySet().iterator();
                          while (localIterator.hasNext())
                          {
                            Object localObject = (Map.Entry)localIterator.next();
                            Integer localInteger1 = (Integer)((Map.Entry)localObject).getKey();
                            localObject = (long[])((Map.Entry)localObject).getValue();
                            Integer localInteger2 = Integer.valueOf((int)localObject[1]);
                            SendMessagesHelper.getInstance().processSentMessage(localInteger2.intValue());
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { localInteger2, localInteger1, null, Long.valueOf(localObject[0]) });
                          }
                        }
                      });
                    }
                  }
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      long l1;
                      Object localObject2;
                      Object localObject1;
                      Object localObject3;
                      int i;
                      Object localObject4;
                      label299:
                      boolean bool;
                      if (((MessagesController.92.2.this.val$res instanceof TLRPC.TL_updates_channelDifference)) || ((MessagesController.92.2.this.val$res instanceof TLRPC.TL_updates_channelDifferenceEmpty)))
                      {
                        if (!MessagesController.92.2.this.val$res.new_messages.isEmpty())
                        {
                          final HashMap localHashMap = new HashMap();
                          ImageLoader.saveMessagesThumbs(MessagesController.92.2.this.val$res.new_messages);
                          final ArrayList localArrayList = new ArrayList();
                          l1 = -MessagesController.92.this.val$channelId;
                          localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(l1));
                          localObject1 = localObject2;
                          if (localObject2 == null)
                          {
                            localObject1 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, l1));
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(l1), localObject1);
                          }
                          localObject3 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(l1));
                          localObject2 = localObject3;
                          if (localObject3 == null)
                          {
                            localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, l1));
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(l1), localObject2);
                          }
                          i = 0;
                          if (i < MessagesController.92.2.this.val$res.new_messages.size())
                          {
                            localObject4 = (TLRPC.Message)MessagesController.92.2.this.val$res.new_messages.get(i);
                            if ((MessagesController.92.2.this.val$channelFinal == null) || (!MessagesController.92.2.this.val$channelFinal.left)) {
                              if (((TLRPC.Message)localObject4).out)
                              {
                                localObject3 = localObject2;
                                if ((((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject4).id) || ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChannelCreate))) {
                                  break label513;
                                }
                              }
                            }
                            label513:
                            for (bool = true;; bool = false)
                            {
                              ((TLRPC.Message)localObject4).unread = bool;
                              if ((MessagesController.92.2.this.val$channelFinal != null) && (MessagesController.92.2.this.val$channelFinal.megagroup)) {
                                ((TLRPC.Message)localObject4).flags |= 0x80000000;
                              }
                              MessageObject localMessageObject = new MessageObject((TLRPC.Message)localObject4, MessagesController.92.2.this.val$usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(l1)));
                              if ((!localMessageObject.isOut()) && (localMessageObject.isUnread())) {
                                localArrayList.add(localMessageObject);
                              }
                              long l2 = -MessagesController.92.this.val$channelId;
                              localObject4 = (ArrayList)localHashMap.get(Long.valueOf(l2));
                              localObject3 = localObject4;
                              if (localObject4 == null)
                              {
                                localObject3 = new ArrayList();
                                localHashMap.put(Long.valueOf(l2), localObject3);
                              }
                              ((ArrayList)localObject3).add(localMessageObject);
                              i += 1;
                              break;
                              localObject3 = localObject1;
                              break label299;
                            }
                          }
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              Iterator localIterator = localHashMap.entrySet().iterator();
                              while (localIterator.hasNext())
                              {
                                Object localObject = (Map.Entry)localIterator.next();
                                Long localLong = (Long)((Map.Entry)localObject).getKey();
                                localObject = (ArrayList)((Map.Entry)localObject).getValue();
                                MessagesController.this.updateInterfaceWithMessages(localLong.longValue(), (ArrayList)localObject);
                              }
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                          });
                          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                          {
                            public void run()
                            {
                              if (!localArrayList.isEmpty()) {
                                AndroidUtilities.runOnUIThread(new Runnable()
                                {
                                  public void run()
                                  {
                                    NotificationsController.getInstance().processNewMessages(MessagesController.92.2.2.2.this.val$pushMessages, true);
                                  }
                                });
                              }
                              MessagesStorage.getInstance().putMessages(MessagesController.92.2.this.val$res.new_messages, true, false, false, MediaController.getInstance().getAutodownloadMask());
                            }
                          });
                        }
                        if (!MessagesController.92.2.this.val$res.other_updates.isEmpty()) {
                          MessagesController.this.processUpdateArray(MessagesController.92.2.this.val$res.other_updates, MessagesController.92.2.this.val$res.users, MessagesController.92.2.this.val$res.chats, true);
                        }
                        MessagesController.this.processChannelsUpdatesQueue(MessagesController.92.this.val$channelId, 1);
                        MessagesStorage.getInstance().saveChannelPts(MessagesController.92.this.val$channelId, MessagesController.92.2.this.val$res.pts);
                      }
                      for (;;)
                      {
                        MessagesController.this.gettingDifferenceChannels.remove(Integer.valueOf(MessagesController.92.this.val$channelId));
                        MessagesController.this.channelsPts.put(Integer.valueOf(MessagesController.92.this.val$channelId), Integer.valueOf(MessagesController.92.2.this.val$res.pts));
                        if ((MessagesController.92.2.this.val$res.flags & 0x2) != 0) {
                          MessagesController.this.shortPollChannels.put(MessagesController.92.this.val$channelId, (int)(System.currentTimeMillis() / 1000L) + MessagesController.92.2.this.val$res.timeout);
                        }
                        if (!MessagesController.92.2.this.val$res.isFinal) {
                          MessagesController.this.getChannelDifference(MessagesController.92.this.val$channelId);
                        }
                        FileLog.e("tmessages", "received channel difference with pts = " + MessagesController.92.2.this.val$res.pts + " channelId = " + MessagesController.92.this.val$channelId);
                        FileLog.e("tmessages", "new_messages = " + MessagesController.92.2.this.val$res.new_messages.size() + " messages = " + MessagesController.92.2.this.val$res.messages.size() + " users = " + MessagesController.92.2.this.val$res.users.size() + " chats = " + MessagesController.92.2.this.val$res.chats.size() + " other updates = " + MessagesController.92.2.this.val$res.other_updates.size());
                        if (MessagesController.92.this.val$newTaskId != 0L) {
                          MessagesStorage.getInstance().removePendingTask(MessagesController.92.this.val$newTaskId);
                        }
                        return;
                        if ((MessagesController.92.2.this.val$res instanceof TLRPC.TL_updates_channelDifferenceTooLong))
                        {
                          l1 = -MessagesController.92.this.val$channelId;
                          localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(l1));
                          localObject1 = localObject2;
                          if (localObject2 == null)
                          {
                            localObject1 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, l1));
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(l1), localObject1);
                          }
                          localObject3 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(l1));
                          localObject2 = localObject3;
                          if (localObject3 == null)
                          {
                            localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, l1));
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(l1), localObject2);
                          }
                          i = 0;
                          if (i < MessagesController.92.2.this.val$res.messages.size())
                          {
                            localObject4 = (TLRPC.Message)MessagesController.92.2.this.val$res.messages.get(i);
                            ((TLRPC.Message)localObject4).dialog_id = (-MessagesController.92.this.val$channelId);
                            if ((!(((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChannelCreate)) && ((MessagesController.92.2.this.val$channelFinal == null) || (!MessagesController.92.2.this.val$channelFinal.left))) {
                              if (((TLRPC.Message)localObject4).out)
                              {
                                localObject3 = localObject2;
                                label1310:
                                if (((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject4).id) {
                                  break label1383;
                                }
                              }
                            }
                            label1383:
                            for (bool = true;; bool = false)
                            {
                              ((TLRPC.Message)localObject4).unread = bool;
                              if ((MessagesController.92.2.this.val$channelFinal != null) && (MessagesController.92.2.this.val$channelFinal.megagroup)) {
                                ((TLRPC.Message)localObject4).flags |= 0x80000000;
                              }
                              i += 1;
                              break;
                              localObject3 = localObject1;
                              break label1310;
                            }
                          }
                          MessagesStorage.getInstance().overwriteChannel(MessagesController.92.this.val$channelId, (TLRPC.TL_updates_channelDifferenceTooLong)MessagesController.92.2.this.val$res, MessagesController.92.this.val$newDialogType);
                        }
                      }
                    }
                  });
                }
              });
            }
            do
            {
              return;
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.checkChannelError(paramAnonymousTL_error.text, MessagesController.92.this.val$channelId);
                }
              });
              MessagesController.this.gettingDifferenceChannels.remove(Integer.valueOf(paramInt1));
            } while (paramLong == 0L);
            MessagesStorage.getInstance().removePendingTask(paramLong);
          }
        });
        return;
      }
      catch (Exception localException2)
      {
        for (;;) {}
      }
      localObject2 = (Integer)this.channelsPts.get(Integer.valueOf(paramInt1));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(paramInt1));
        if (((Integer)localObject2).intValue() != 0) {
          this.channelsPts.put(Integer.valueOf(paramInt1), localObject2);
        }
        localObject1 = localObject2;
        if (((Integer)localObject2).intValue() == 0)
        {
          if (paramInt2 == 2) {
            break;
          }
          localObject1 = localObject2;
        }
      }
      localObject2 = localObject1;
      if (((Integer)localObject1).intValue() != 0) {
        break label75;
      }
      return;
      FileLog.e("tmessages", localException1);
    }
  }
  
  public TLRPC.Chat getChat(Integer paramInteger)
  {
    return (TLRPC.Chat)this.chats.get(paramInteger);
  }
  
  public void getDifference()
  {
    getDifference(MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue, false);
  }
  
  public void getDifference(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    registerForPush(UserConfig.pushString);
    if (MessagesStorage.lastPtsValue == 0) {
      loadCurrentState();
    }
    while ((!paramBoolean) && (this.gettingDifference)) {
      return;
    }
    if (!this.firstGettingTask)
    {
      getNewDeleteTask(null);
      this.firstGettingTask = true;
    }
    this.gettingDifference = true;
    TLRPC.TL_updates_getDifference localTL_updates_getDifference = new TLRPC.TL_updates_getDifference();
    localTL_updates_getDifference.pts = paramInt1;
    localTL_updates_getDifference.date = paramInt2;
    localTL_updates_getDifference.qts = paramInt3;
    if (localTL_updates_getDifference.date == 0) {
      localTL_updates_getDifference.date = ConnectionsManager.getInstance().getCurrentTime();
    }
    FileLog.e("tmessages", "start getDifference with date = " + MessagesStorage.lastDateValue + " pts = " + MessagesStorage.lastPtsValue + " seq = " + MessagesStorage.lastSeqValue);
    ConnectionsManager.getInstance().setIsUpdating(true);
    ConnectionsManager.getInstance().sendRequest(localTL_updates_getDifference, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.updates_Difference)paramAnonymousTLObject;
          if ((paramAnonymousTLObject instanceof TLRPC.TL_updates_differenceSlice)) {
            MessagesController.this.getDifference(paramAnonymousTLObject.intermediate_state.pts, paramAnonymousTLObject.intermediate_state.date, paramAnonymousTLObject.intermediate_state.qts, true);
          }
          paramAnonymousTL_error = new HashMap();
          final HashMap localHashMap = new HashMap();
          int i = 0;
          while (i < paramAnonymousTLObject.users.size())
          {
            localObject = (TLRPC.User)paramAnonymousTLObject.users.get(i);
            paramAnonymousTL_error.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
            i += 1;
          }
          i = 0;
          while (i < paramAnonymousTLObject.chats.size())
          {
            localObject = (TLRPC.Chat)paramAnonymousTLObject.chats.get(i);
            localHashMap.put(Integer.valueOf(((TLRPC.Chat)localObject).id), localObject);
            i += 1;
          }
          final Object localObject = new ArrayList();
          if (!paramAnonymousTLObject.other_updates.isEmpty())
          {
            int j;
            for (i = 0; i < paramAnonymousTLObject.other_updates.size(); i = j + 1)
            {
              TLRPC.Update localUpdate = (TLRPC.Update)paramAnonymousTLObject.other_updates.get(i);
              j = i;
              if ((localUpdate instanceof TLRPC.TL_updateMessageID))
              {
                ((ArrayList)localObject).add((TLRPC.TL_updateMessageID)localUpdate);
                paramAnonymousTLObject.other_updates.remove(i);
                j = i - 1;
              }
            }
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.putUsers(paramAnonymousTLObject.users, false);
              MessagesController.this.putChats(paramAnonymousTLObject.chats, false);
            }
          });
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
          {
            public void run()
            {
              MessagesStorage.getInstance().putUsersAndChats(paramAnonymousTLObject.users, paramAnonymousTLObject.chats, true, false);
              if (!localObject.isEmpty())
              {
                final HashMap localHashMap = new HashMap();
                int i = 0;
                while (i < localObject.size())
                {
                  TLRPC.TL_updateMessageID localTL_updateMessageID = (TLRPC.TL_updateMessageID)localObject.get(i);
                  long[] arrayOfLong = MessagesStorage.getInstance().updateMessageStateAndId(localTL_updateMessageID.random_id, null, localTL_updateMessageID.id, 0, false, 0);
                  if (arrayOfLong != null) {
                    localHashMap.put(Integer.valueOf(localTL_updateMessageID.id), arrayOfLong);
                  }
                  i += 1;
                }
                if (!localHashMap.isEmpty()) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      Iterator localIterator = localHashMap.entrySet().iterator();
                      while (localIterator.hasNext())
                      {
                        Object localObject = (Map.Entry)localIterator.next();
                        Integer localInteger1 = (Integer)((Map.Entry)localObject).getKey();
                        localObject = (long[])((Map.Entry)localObject).getValue();
                        Integer localInteger2 = Integer.valueOf((int)localObject[1]);
                        SendMessagesHelper.getInstance().processSentMessage(localInteger2.intValue());
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { localInteger2, localInteger1, null, Long.valueOf(localObject[0]) });
                      }
                    }
                  });
                }
              }
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  int i;
                  if ((!MessagesController.93.2.this.val$res.new_messages.isEmpty()) || (!MessagesController.93.2.this.val$res.new_encrypted_messages.isEmpty()))
                  {
                    final HashMap localHashMap = new HashMap();
                    i = 0;
                    Object localObject1;
                    Object localObject2;
                    while (i < MessagesController.93.2.this.val$res.new_encrypted_messages.size())
                    {
                      localObject1 = (TLRPC.EncryptedMessage)MessagesController.93.2.this.val$res.new_encrypted_messages.get(i);
                      localObject1 = SecretChatHelper.getInstance().decryptMessage((TLRPC.EncryptedMessage)localObject1);
                      if ((localObject1 != null) && (!((ArrayList)localObject1).isEmpty()))
                      {
                        j = 0;
                        while (j < ((ArrayList)localObject1).size())
                        {
                          localObject2 = (TLRPC.Message)((ArrayList)localObject1).get(j);
                          MessagesController.93.2.this.val$res.new_messages.add(localObject2);
                          j += 1;
                        }
                      }
                      i += 1;
                    }
                    ImageLoader.saveMessagesThumbs(MessagesController.93.2.this.val$res.new_messages);
                    final ArrayList localArrayList = new ArrayList();
                    int j = UserConfig.getClientUserId();
                    i = 0;
                    if (i < MessagesController.93.2.this.val$res.new_messages.size())
                    {
                      TLRPC.Message localMessage = (TLRPC.Message)MessagesController.93.2.this.val$res.new_messages.get(i);
                      if (localMessage.dialog_id == 0L) {
                        if (localMessage.to_id.chat_id == 0) {
                          break label528;
                        }
                      }
                      Object localObject3;
                      for (localMessage.dialog_id = (-localMessage.to_id.chat_id);; localMessage.dialog_id = localMessage.to_id.user_id)
                      {
                        if ((int)localMessage.dialog_id != 0)
                        {
                          if ((localMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
                          {
                            localObject1 = (TLRPC.User)MessagesController.93.2.this.val$usersDict.get(Integer.valueOf(localMessage.action.user_id));
                            if ((localObject1 != null) && (((TLRPC.User)localObject1).bot)) {
                              localMessage.reply_markup = new TLRPC.TL_replyKeyboardHide();
                            }
                          }
                          if ((!(localMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo)) && (!(localMessage.action instanceof TLRPC.TL_messageActionChannelCreate))) {
                            break label572;
                          }
                          localMessage.unread = false;
                          localMessage.media_unread = false;
                        }
                        if (localMessage.dialog_id == j)
                        {
                          localMessage.unread = false;
                          localMessage.media_unread = false;
                          localMessage.out = true;
                        }
                        localObject3 = new MessageObject(localMessage, MessagesController.93.2.this.val$usersDict, MessagesController.93.2.this.val$chatsDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(localMessage.dialog_id)));
                        if ((!((MessageObject)localObject3).isOut()) && (((MessageObject)localObject3).isUnread())) {
                          localArrayList.add(localObject3);
                        }
                        localObject2 = (ArrayList)localHashMap.get(Long.valueOf(localMessage.dialog_id));
                        localObject1 = localObject2;
                        if (localObject2 == null)
                        {
                          localObject1 = new ArrayList();
                          localHashMap.put(Long.valueOf(localMessage.dialog_id), localObject1);
                        }
                        ((ArrayList)localObject1).add(localObject3);
                        i += 1;
                        break;
                        label528:
                        if (localMessage.to_id.user_id == UserConfig.getClientUserId()) {
                          localMessage.to_id.user_id = localMessage.from_id;
                        }
                      }
                      label572:
                      if (localMessage.out)
                      {
                        localObject1 = MessagesController.this.dialogs_read_outbox_max;
                        label595:
                        localObject3 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(localMessage.dialog_id));
                        localObject2 = localObject3;
                        if (localObject3 == null)
                        {
                          localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localMessage.out, localMessage.dialog_id));
                          ((ConcurrentHashMap)localObject1).put(Long.valueOf(localMessage.dialog_id), localObject2);
                        }
                        if (((Integer)localObject2).intValue() >= localMessage.id) {
                          break label701;
                        }
                      }
                      label701:
                      for (boolean bool = true;; bool = false)
                      {
                        localMessage.unread = bool;
                        break;
                        localObject1 = MessagesController.this.dialogs_read_inbox_max;
                        break label595;
                      }
                    }
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        Iterator localIterator = localHashMap.entrySet().iterator();
                        while (localIterator.hasNext())
                        {
                          Object localObject = (Map.Entry)localIterator.next();
                          Long localLong = (Long)((Map.Entry)localObject).getKey();
                          localObject = (ArrayList)((Map.Entry)localObject).getValue();
                          MessagesController.this.updateInterfaceWithMessages(localLong.longValue(), (ArrayList)localObject);
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                      }
                    });
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        if (!localArrayList.isEmpty()) {
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              NotificationsController localNotificationsController = NotificationsController.getInstance();
                              ArrayList localArrayList = MessagesController.93.2.2.2.this.val$pushMessages;
                              if (!(MessagesController.93.2.this.val$res instanceof TLRPC.TL_updates_differenceSlice)) {}
                              for (boolean bool = true;; bool = false)
                              {
                                localNotificationsController.processNewMessages(localArrayList, bool);
                                return;
                              }
                            }
                          });
                        }
                        MessagesStorage.getInstance().putMessages(MessagesController.93.2.this.val$res.new_messages, true, false, false, MediaController.getInstance().getAutodownloadMask());
                      }
                    });
                    SecretChatHelper.getInstance().processPendingEncMessages();
                  }
                  if (!MessagesController.93.2.this.val$res.other_updates.isEmpty()) {
                    MessagesController.this.processUpdateArray(MessagesController.93.2.this.val$res.other_updates, MessagesController.93.2.this.val$res.users, MessagesController.93.2.this.val$res.chats, true);
                  }
                  if ((MessagesController.93.2.this.val$res instanceof TLRPC.TL_updates_difference))
                  {
                    MessagesController.this.gettingDifference = false;
                    MessagesStorage.lastSeqValue = MessagesController.93.2.this.val$res.state.seq;
                    MessagesStorage.lastDateValue = MessagesController.93.2.this.val$res.state.date;
                    MessagesStorage.lastPtsValue = MessagesController.93.2.this.val$res.state.pts;
                    MessagesStorage.lastQtsValue = MessagesController.93.2.this.val$res.state.qts;
                    ConnectionsManager.getInstance().setIsUpdating(false);
                    i = 0;
                    while (i < 3)
                    {
                      MessagesController.this.processUpdatesQueue(i, 1);
                      i += 1;
                    }
                  }
                  if ((MessagesController.93.2.this.val$res instanceof TLRPC.TL_updates_differenceSlice))
                  {
                    MessagesStorage.lastDateValue = MessagesController.93.2.this.val$res.intermediate_state.date;
                    MessagesStorage.lastPtsValue = MessagesController.93.2.this.val$res.intermediate_state.pts;
                    MessagesStorage.lastQtsValue = MessagesController.93.2.this.val$res.intermediate_state.qts;
                  }
                  for (;;)
                  {
                    MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
                    FileLog.e("tmessages", "received difference with date = " + MessagesStorage.lastDateValue + " pts = " + MessagesStorage.lastPtsValue + " seq = " + MessagesStorage.lastSeqValue + " messages = " + MessagesController.93.2.this.val$res.new_messages.size() + " users = " + MessagesController.93.2.this.val$res.users.size() + " chats = " + MessagesController.93.2.this.val$res.chats.size() + " other updates = " + MessagesController.93.2.this.val$res.other_updates.size());
                    return;
                    if ((MessagesController.93.2.this.val$res instanceof TLRPC.TL_updates_differenceEmpty))
                    {
                      MessagesController.this.gettingDifference = false;
                      MessagesStorage.lastSeqValue = MessagesController.93.2.this.val$res.seq;
                      MessagesStorage.lastDateValue = MessagesController.93.2.this.val$res.date;
                      ConnectionsManager.getInstance().setIsUpdating(false);
                      i = 0;
                      while (i < 3)
                      {
                        MessagesController.this.processUpdatesQueue(i, 1);
                        i += 1;
                      }
                    }
                  }
                }
              });
            }
          });
          return;
        }
        MessagesController.this.gettingDifference = false;
        ConnectionsManager.getInstance().setIsUpdating(false);
      }
    });
  }
  
  public TLRPC.EncryptedChat getEncryptedChat(Integer paramInteger)
  {
    return (TLRPC.EncryptedChat)this.encryptedChats.get(paramInteger);
  }
  
  public TLRPC.EncryptedChat getEncryptedChatDB(int paramInt)
  {
    Object localObject2 = (TLRPC.EncryptedChat)this.encryptedChats.get(Integer.valueOf(paramInt));
    Object localObject1;
    ArrayList localArrayList;
    if (localObject2 != null)
    {
      localObject1 = localObject2;
      if (((TLRPC.EncryptedChat)localObject2).auth_key != null) {}
    }
    else
    {
      localObject1 = new Semaphore(0);
      localArrayList = new ArrayList();
      MessagesStorage.getInstance().getEncryptedChat(paramInt, (Semaphore)localObject1, localArrayList);
    }
    try
    {
      ((Semaphore)localObject1).acquire();
      localObject1 = localObject2;
      if (localArrayList.size() == 2)
      {
        localObject1 = (TLRPC.EncryptedChat)localArrayList.get(0);
        localObject2 = (TLRPC.User)localArrayList.get(1);
        putEncryptedChat((TLRPC.EncryptedChat)localObject1, false);
        putUser((TLRPC.User)localObject2, true);
      }
      return (TLRPC.EncryptedChat)localObject1;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public TLRPC.ExportedChatInvite getExportedInvite(int paramInt)
  {
    return (TLRPC.ExportedChatInvite)this.exportedChats.get(Integer.valueOf(paramInt));
  }
  
  public void getNewDeleteTask(final ArrayList<Integer> paramArrayList)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.access$3002(MessagesController.this, true);
        MessagesStorage.getInstance().getNewTask(paramArrayList);
      }
    });
  }
  
  public long getUpdatesStartTime(int paramInt)
  {
    if (paramInt == 0) {
      return this.updatesStartWaitTimeSeq;
    }
    if (paramInt == 1) {
      return this.updatesStartWaitTimePts;
    }
    if (paramInt == 2) {
      return this.updatesStartWaitTimeQts;
    }
    return 0L;
  }
  
  public TLRPC.User getUser(Integer paramInteger)
  {
    return (TLRPC.User)this.users.get(paramInteger);
  }
  
  public TLRPC.User getUser(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    return (TLRPC.User)this.usersByUsernames.get(paramString.toLowerCase());
  }
  
  public String getUserAbout(int paramInt)
  {
    return (String)this.fullUsersAbout.get(Integer.valueOf(paramInt));
  }
  
  public ConcurrentHashMap<Integer, TLRPC.User> getUsers()
  {
    return this.users;
  }
  
  public void hideReportSpam(long paramLong, TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    if ((paramUser == null) && (paramChat == null)) {
      return;
    }
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
    ((SharedPreferences.Editor)localObject).putInt("spam3_" + paramLong, 1);
    ((SharedPreferences.Editor)localObject).commit();
    localObject = new TLRPC.TL_messages_hideReportSpam();
    if (paramUser != null) {
      ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(paramUser.id);
    }
    for (;;)
    {
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
      return;
      if (paramChat != null) {
        ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(-paramChat.id);
      }
    }
  }
  
  public boolean isDialogMuted(long paramLong)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int i = localSharedPreferences.getInt("notify2_" + paramLong, 0);
    if (i == 2) {}
    while ((i == 3) && (localSharedPreferences.getInt("notifyuntil_" + paramLong, 0) >= ConnectionsManager.getInstance().getCurrentTime())) {
      return true;
    }
    return false;
  }
  
  public void loadChannelParticipants(final Integer paramInteger)
  {
    if ((this.loadingFullParticipants.contains(paramInteger)) || (this.loadedFullParticipants.contains(paramInteger))) {
      return;
    }
    this.loadingFullParticipants.add(paramInteger);
    TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
    localTL_channels_getParticipants.channel = getInputChannel(paramInteger.intValue());
    localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
    localTL_channels_getParticipants.offset = 0;
    localTL_channels_getParticipants.limit = 32;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipants, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null)
            {
              TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)paramAnonymousTLObject;
              MessagesController.this.putUsers(localTL_channels_channelParticipants.users, false);
              MessagesStorage.getInstance().putUsersAndChats(localTL_channels_channelParticipants.users, null, true, true);
              MessagesStorage.getInstance().updateChannelUsers(MessagesController.40.this.val$chat_id.intValue(), localTL_channels_channelParticipants.participants);
              MessagesController.this.loadedFullParticipants.add(MessagesController.40.this.val$chat_id);
            }
            MessagesController.this.loadingFullParticipants.remove(MessagesController.40.this.val$chat_id);
          }
        });
      }
    });
  }
  
  public void loadChatInfo(int paramInt, Semaphore paramSemaphore, boolean paramBoolean)
  {
    MessagesStorage.getInstance().loadChatInfo(paramInt, paramSemaphore, paramBoolean, false);
  }
  
  public void loadCurrentState()
  {
    if (this.updatingState) {
      return;
    }
    this.updatingState = true;
    TLRPC.TL_updates_getState localTL_updates_getState = new TLRPC.TL_updates_getState();
    ConnectionsManager.getInstance().sendRequest(localTL_updates_getState, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        MessagesController.this.updatingState = false;
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.TL_updates_state)paramAnonymousTLObject;
          MessagesStorage.lastDateValue = paramAnonymousTLObject.date;
          MessagesStorage.lastPtsValue = paramAnonymousTLObject.pts;
          MessagesStorage.lastSeqValue = paramAnonymousTLObject.seq;
          MessagesStorage.lastQtsValue = paramAnonymousTLObject.qts;
          i = 0;
          while (i < 3)
          {
            MessagesController.this.processUpdatesQueue(i, 2);
            i += 1;
          }
          MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
        }
        while (paramAnonymousTL_error.code == 401)
        {
          int i;
          return;
        }
        MessagesController.this.loadCurrentState();
      }
    });
  }
  
  public void loadDialogPhotos(final int paramInt1, final int paramInt2, final int paramInt3, final long paramLong, boolean paramBoolean, int paramInt4)
  {
    if (paramBoolean) {
      MessagesStorage.getInstance().getDialogPhotos(paramInt1, paramInt2, paramInt3, paramLong, paramInt4);
    }
    do
    {
      do
      {
        return;
        if (paramInt1 <= 0) {
          break;
        }
        localObject = getUser(Integer.valueOf(paramInt1));
      } while (localObject == null);
      TLRPC.TL_photos_getUserPhotos localTL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
      localTL_photos_getUserPhotos.limit = paramInt3;
      localTL_photos_getUserPhotos.offset = paramInt2;
      localTL_photos_getUserPhotos.max_id = ((int)paramLong);
      localTL_photos_getUserPhotos.user_id = getInputUser((TLRPC.User)localObject);
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_photos_getUserPhotos, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.photos_Photos)paramAnonymousTLObject;
            MessagesController.this.processLoadedUserPhotos(paramAnonymousTLObject, paramInt1, paramInt2, paramInt3, paramLong, false, this.val$classGuid);
          }
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt4);
      return;
    } while (paramInt1 >= 0);
    Object localObject = new TLRPC.TL_messages_search();
    ((TLRPC.TL_messages_search)localObject).filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
    ((TLRPC.TL_messages_search)localObject).limit = paramInt3;
    ((TLRPC.TL_messages_search)localObject).offset = paramInt2;
    ((TLRPC.TL_messages_search)localObject).max_id = ((int)paramLong);
    ((TLRPC.TL_messages_search)localObject).q = "";
    ((TLRPC.TL_messages_search)localObject).peer = getInputPeer(paramInt1);
    paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
          paramAnonymousTL_error = new TLRPC.TL_photos_photos();
          paramAnonymousTL_error.count = paramAnonymousTLObject.count;
          paramAnonymousTL_error.users.addAll(paramAnonymousTLObject.users);
          int i = 0;
          if (i < paramAnonymousTLObject.messages.size())
          {
            TLRPC.Message localMessage = (TLRPC.Message)paramAnonymousTLObject.messages.get(i);
            if ((localMessage.action == null) || (localMessage.action.photo == null)) {}
            for (;;)
            {
              i += 1;
              break;
              paramAnonymousTL_error.photos.add(localMessage.action.photo);
            }
          }
          MessagesController.this.processLoadedUserPhotos(paramAnonymousTL_error, paramInt1, paramInt2, paramInt3, paramLong, false, this.val$classGuid);
        }
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt4);
  }
  
  public void loadDialogs(int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    if (this.loadingDialogs) {
      return;
    }
    this.loadingDialogs = true;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    FileLog.e("tmessages", "load cacheOffset = " + paramInt1 + " count = " + paramInt2 + " cache = " + paramBoolean);
    if (paramBoolean)
    {
      localObject1 = MessagesStorage.getInstance();
      if (paramInt1 == 0) {}
      for (paramInt1 = 0;; paramInt1 = this.nextDialogsCacheOffset)
      {
        ((MessagesStorage)localObject1).getDialogs(paramInt1, paramInt2);
        return;
      }
    }
    Object localObject1 = new TLRPC.TL_messages_getDialogs();
    ((TLRPC.TL_messages_getDialogs)localObject1).limit = paramInt2;
    int j = 0;
    paramInt1 = this.dialogs.size() - 1;
    for (;;)
    {
      int i = j;
      Object localObject2;
      if (paramInt1 >= 0)
      {
        localObject2 = (TLRPC.TL_dialog)this.dialogs.get(paramInt1);
        i = (int)((TLRPC.TL_dialog)localObject2).id;
        int k = (int)(((TLRPC.TL_dialog)localObject2).id >> 32);
        if ((i == 0) || (k == 1) || (((TLRPC.TL_dialog)localObject2).top_message <= 0)) {
          break label369;
        }
        localObject2 = (MessageObject)this.dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject2).id));
        if ((localObject2 == null) || (((MessageObject)localObject2).getId() <= 0)) {
          break label369;
        }
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_date = ((MessageObject)localObject2).messageOwner.date;
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_id = ((MessageObject)localObject2).messageOwner.id;
        if (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0) {
          break label324;
        }
        paramInt1 = -((MessageObject)localObject2).messageOwner.to_id.channel_id;
      }
      for (;;)
      {
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = getInputPeer(paramInt1);
        i = 1;
        if (i == 0) {
          ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerEmpty();
        }
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.messages_Dialogs)paramAnonymousTLObject;
              MessagesController.this.processLoadedDialogs(paramAnonymousTLObject, null, 0, paramInt2, 0, false, false);
            }
          }
        });
        return;
        label324:
        if (((MessageObject)localObject2).messageOwner.to_id.chat_id != 0) {
          paramInt1 = -((MessageObject)localObject2).messageOwner.to_id.chat_id;
        } else {
          paramInt1 = ((MessageObject)localObject2).messageOwner.to_id.user_id;
        }
      }
      label369:
      paramInt1 -= 1;
    }
  }
  
  public void loadFullChat(final int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    if ((this.loadingFullChats.contains(Integer.valueOf(paramInt1))) || ((!paramBoolean) && (this.loadedFullChats.contains(Integer.valueOf(paramInt1))))) {
      return;
    }
    this.loadingFullChats.add(Integer.valueOf(paramInt1));
    final TLRPC.Chat localChat = getChat(Integer.valueOf(paramInt1));
    Object localObject;
    if (ChatObject.isChannel(paramInt1))
    {
      localObject = new TLRPC.TL_channels_getFullChannel();
      ((TLRPC.TL_channels_getFullChannel)localObject).channel = getInputChannel(paramInt1);
    }
    for (;;)
    {
      paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            final TLRPC.TL_messages_chatFull localTL_messages_chatFull = (TLRPC.TL_messages_chatFull)paramAnonymousTLObject;
            MessagesStorage.getInstance().putUsersAndChats(localTL_messages_chatFull.users, localTL_messages_chatFull.chats, true, true);
            MessagesStorage.getInstance().updateChatInfo(localTL_messages_chatFull.full_chat, false);
            if (ChatObject.isChannel(localChat))
            {
              long l = -paramInt1;
              paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(l));
              paramAnonymousTLObject = paramAnonymousTL_error;
              if (paramAnonymousTL_error == null) {
                paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, l));
              }
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(l), Integer.valueOf(Math.max(localTL_messages_chatFull.full_chat.read_inbox_max_id, paramAnonymousTLObject.intValue())));
              if (paramAnonymousTLObject.intValue() == 0)
              {
                paramAnonymousTLObject = new ArrayList();
                paramAnonymousTL_error = new TLRPC.TL_updateReadChannelInbox();
                paramAnonymousTL_error.channel_id = paramInt1;
                paramAnonymousTL_error.max_id = localTL_messages_chatFull.full_chat.read_inbox_max_id;
                paramAnonymousTLObject.add(paramAnonymousTL_error);
                MessagesController.this.processUpdateArray(paramAnonymousTLObject, null, null, false);
              }
              paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(l));
              paramAnonymousTLObject = paramAnonymousTL_error;
              if (paramAnonymousTL_error == null) {
                paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, l));
              }
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(l), Integer.valueOf(Math.max(localTL_messages_chatFull.full_chat.read_outbox_max_id, paramAnonymousTLObject.intValue())));
              if (paramAnonymousTLObject.intValue() == 0)
              {
                paramAnonymousTLObject = new ArrayList();
                paramAnonymousTL_error = new TLRPC.TL_updateReadChannelOutbox();
                paramAnonymousTL_error.channel_id = paramInt1;
                paramAnonymousTL_error.max_id = localTL_messages_chatFull.full_chat.read_outbox_max_id;
                paramAnonymousTLObject.add(paramAnonymousTL_error);
                MessagesController.this.processUpdateArray(paramAnonymousTLObject, null, null, false);
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.applyDialogNotificationsSettings(-MessagesController.10.this.val$chat_id, localTL_messages_chatFull.full_chat.notify_settings);
                int i = 0;
                while (i < localTL_messages_chatFull.full_chat.bot_info.size())
                {
                  BotQuery.putBotInfo((TLRPC.BotInfo)localTL_messages_chatFull.full_chat.bot_info.get(i));
                  i += 1;
                }
                MessagesController.this.exportedChats.put(Integer.valueOf(MessagesController.10.this.val$chat_id), localTL_messages_chatFull.full_chat.exported_invite);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(MessagesController.10.this.val$chat_id));
                MessagesController.this.loadedFullChats.add(Integer.valueOf(MessagesController.10.this.val$chat_id));
                if (!localTL_messages_chatFull.chats.isEmpty()) {
                  ((TLRPC.Chat)localTL_messages_chatFull.chats.get(0)).address = localTL_messages_chatFull.full_chat.about;
                }
                MessagesController.this.putUsers(localTL_messages_chatFull.users, false);
                MessagesController.this.putChats(localTL_messages_chatFull.chats, false);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { localTL_messages_chatFull.full_chat, Integer.valueOf(MessagesController.10.this.val$classGuid), Boolean.valueOf(false), null });
              }
            });
            return;
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.checkChannelError(paramAnonymousTL_error.text, MessagesController.10.this.val$chat_id);
              MessagesController.this.loadingFullChats.remove(Integer.valueOf(MessagesController.10.this.val$chat_id));
            }
          });
        }
      });
      if (paramInt2 == 0) {
        break;
      }
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
      return;
      localObject = new TLRPC.TL_messages_getFullChat();
      ((TLRPC.TL_messages_getFullChat)localObject).chat_id = paramInt1;
    }
  }
  
  public void loadFullUser(final TLRPC.User paramUser, final int paramInt, boolean paramBoolean)
  {
    if ((paramUser == null) || (this.loadingFullUsers.contains(Integer.valueOf(paramUser.id))) || ((!paramBoolean) && (this.loadedFullUsers.contains(Integer.valueOf(paramUser.id))))) {
      return;
    }
    this.loadingFullUsers.add(Integer.valueOf(paramUser.id));
    TLRPC.TL_users_getFullUser localTL_users_getFullUser = new TLRPC.TL_users_getFullUser();
    localTL_users_getFullUser.id = getInputUser(paramUser);
    int i = ConnectionsManager.getInstance().sendRequest(localTL_users_getFullUser, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TLRPC.TL_userFull localTL_userFull = (TLRPC.TL_userFull)paramAnonymousTLObject;
              MessagesController.this.applyDialogNotificationsSettings(MessagesController.11.this.val$user.id, localTL_userFull.notify_settings);
              if ((localTL_userFull.bot_info instanceof TLRPC.TL_botInfo)) {
                BotQuery.putBotInfo(localTL_userFull.bot_info);
              }
              if ((localTL_userFull.about != null) && (localTL_userFull.about.length() > 0)) {
                MessagesController.this.fullUsersAbout.put(Integer.valueOf(MessagesController.11.this.val$user.id), localTL_userFull.about);
              }
              for (;;)
              {
                MessagesController.this.loadingFullUsers.remove(Integer.valueOf(MessagesController.11.this.val$user.id));
                MessagesController.this.loadedFullUsers.add(Integer.valueOf(MessagesController.11.this.val$user.id));
                String str = MessagesController.11.this.val$user.first_name + MessagesController.11.this.val$user.last_name + MessagesController.11.this.val$user.username;
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localTL_userFull.user);
                MessagesController.this.putUsers(localArrayList, false);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, false, true);
                if ((str != null) && (!str.equals(localTL_userFull.user.first_name + localTL_userFull.user.last_name + localTL_userFull.user.username))) {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
                }
                if ((localTL_userFull.bot_info instanceof TLRPC.TL_botInfo)) {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, new Object[] { localTL_userFull.bot_info, Integer.valueOf(MessagesController.11.this.val$classGuid) });
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.userInfoDidLoaded, new Object[] { Integer.valueOf(MessagesController.11.this.val$user.id) });
                return;
                MessagesController.this.fullUsersAbout.remove(Integer.valueOf(MessagesController.11.this.val$user.id));
              }
            }
          });
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.loadingFullUsers.remove(Integer.valueOf(MessagesController.11.this.val$user.id));
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(i, paramInt);
  }
  
  public void loadMessages(long paramLong, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean2, int paramInt7)
  {
    loadMessages(paramLong, paramInt1, paramInt2, paramBoolean1, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean2, paramInt7, 0, 0, 0, false);
  }
  
  public void loadMessages(final long paramLong, final int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, final int paramInt4, final int paramInt5, final int paramInt6, final boolean paramBoolean2, final int paramInt7, final int paramInt8, final int paramInt9, final int paramInt10, final boolean paramBoolean3)
  {
    FileLog.e("tmessages", "load messages in chat " + paramLong + " count " + paramInt1 + " max_id " + paramInt2 + " cache " + paramBoolean1 + " mindate = " + paramInt3 + " guid " + paramInt4 + " load_type " + paramInt5 + " last_message_id " + paramInt6 + " index " + paramInt7 + " firstUnread " + paramInt8 + " underad count " + paramInt9 + " last_date " + paramInt10 + " queryFromServer " + paramBoolean3);
    int i = (int)paramLong;
    if ((paramBoolean1) || (i == 0))
    {
      MessagesStorage.getInstance().getMessages(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean2, paramInt7);
      return;
    }
    TLRPC.TL_messages_getHistory localTL_messages_getHistory = new TLRPC.TL_messages_getHistory();
    localTL_messages_getHistory.peer = getInputPeer(i);
    if (paramInt5 == 3) {
      localTL_messages_getHistory.add_offset = (-paramInt1 / 2);
    }
    for (;;)
    {
      localTL_messages_getHistory.limit = paramInt1;
      localTL_messages_getHistory.offset_id = paramInt2;
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_getHistory, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
            if (paramAnonymousTLObject.messages.size() > paramInt1) {
              paramAnonymousTLObject.messages.remove(0);
            }
            MessagesController.this.processLoadedMessages(paramAnonymousTLObject, paramLong, paramInt1, paramInt4, false, paramInt8, paramInt6, paramInt9, paramInt10, paramInt5, paramBoolean2, paramInt7, false, paramBoolean3, this.val$queryFromServer);
          }
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt4);
      return;
      if (paramInt5 == 1)
      {
        localTL_messages_getHistory.add_offset = (-paramInt1 - 1);
      }
      else if ((paramInt5 == 2) && (paramInt2 != 0))
      {
        localTL_messages_getHistory.add_offset = (-paramInt1 + 6);
      }
      else if ((i < 0) && (paramInt2 != 0) && (ChatObject.isChannel(getChat(Integer.valueOf(-i)))))
      {
        localTL_messages_getHistory.add_offset = -1;
        localTL_messages_getHistory.limit += 1;
      }
    }
  }
  
  public void loadPeerSettings(final long paramLong, TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    if ((this.loadingPeerSettings.containsKey(Long.valueOf(paramLong))) || ((paramUser == null) && (paramChat == null))) {}
    do
    {
      return;
      this.loadingPeerSettings.put(Long.valueOf(paramLong), Boolean.valueOf(true));
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    } while (((SharedPreferences)localObject).getInt("spam3_" + paramLong, 0) == 1);
    if (((SharedPreferences)localObject).getBoolean("spam_" + paramLong, false))
    {
      localObject = new TLRPC.TL_messages_hideReportSpam();
      if (paramUser != null) {
        ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(paramUser.id);
      }
      for (;;)
      {
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.loadingPeerSettings.remove(Long.valueOf(MessagesController.15.this.val$dialogId));
                SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                localEditor.remove("spam_" + MessagesController.15.this.val$dialogId);
                localEditor.putInt("spam3_" + MessagesController.15.this.val$dialogId, 1);
                localEditor.commit();
              }
            });
          }
        });
        return;
        if (paramChat != null) {
          ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(-paramChat.id);
        }
      }
    }
    Object localObject = new TLRPC.TL_messages_getPeerSettings();
    if (paramUser != null) {
      ((TLRPC.TL_messages_getPeerSettings)localObject).peer = getInputPeer(paramUser.id);
    }
    for (;;)
    {
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.loadingPeerSettings.remove(Long.valueOf(MessagesController.16.this.val$dialogId));
              SharedPreferences.Editor localEditor;
              if (paramAnonymousTLObject != null)
              {
                TLRPC.TL_peerSettings localTL_peerSettings = (TLRPC.TL_peerSettings)paramAnonymousTLObject;
                localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                if (localTL_peerSettings.report_spam) {
                  break label102;
                }
                localEditor.putInt("spam3_" + MessagesController.16.this.val$dialogId, 1);
              }
              for (;;)
              {
                localEditor.commit();
                return;
                label102:
                localEditor.putInt("spam3_" + MessagesController.16.this.val$dialogId, 2);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.peerSettingsDidLoaded, new Object[] { Long.valueOf(MessagesController.16.this.val$dialogId) });
              }
            }
          });
        }
      });
      return;
      if (paramChat != null) {
        ((TLRPC.TL_messages_getPeerSettings)localObject).peer = getInputPeer(-paramChat.id);
      }
    }
  }
  
  protected void loadUnknownChannel(TLRPC.Chat paramChat, final long paramLong)
  {
    if ((!(paramChat instanceof TLRPC.TL_channel)) || (this.gettingUnknownChannels.containsKey(Integer.valueOf(paramChat.id)))) {
      return;
    }
    this.gettingUnknownChannels.put(Integer.valueOf(paramChat.id), Boolean.valueOf(true));
    Object localObject1 = new TLRPC.TL_inputPeerChannel();
    ((TLRPC.TL_inputPeerChannel)localObject1).channel_id = paramChat.id;
    ((TLRPC.TL_inputPeerChannel)localObject1).access_hash = paramChat.access_hash;
    TLRPC.TL_messages_getPeerDialogs localTL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
    localTL_messages_getPeerDialogs.peers.add(localObject1);
    Object localObject2;
    if (paramLong == 0L) {
      localObject2 = null;
    }
    for (;;)
    {
      try
      {
        localObject1 = new NativeByteBuffer(paramChat.getObjectSize() + 4);
        FileLog.e("tmessages", localException1);
      }
      catch (Exception localException1)
      {
        try
        {
          ((NativeByteBuffer)localObject1).writeInt32(0);
          paramChat.serializeToStream((AbstractSerializedData)localObject1);
          paramLong = MessagesStorage.getInstance().createPendingTask((NativeByteBuffer)localObject1);
          ConnectionsManager.getInstance().sendRequest(localTL_messages_getPeerDialogs, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTLObject != null)
              {
                paramAnonymousTLObject = (TLRPC.TL_messages_peerDialogs)paramAnonymousTLObject;
                if ((!paramAnonymousTLObject.dialogs.isEmpty()) && (!paramAnonymousTLObject.chats.isEmpty()))
                {
                  paramAnonymousTL_error = new TLRPC.TL_messages_dialogs();
                  paramAnonymousTL_error.dialogs.addAll(paramAnonymousTLObject.dialogs);
                  paramAnonymousTL_error.messages.addAll(paramAnonymousTLObject.messages);
                  paramAnonymousTL_error.users.addAll(paramAnonymousTLObject.users);
                  paramAnonymousTL_error.chats.addAll(paramAnonymousTLObject.chats);
                  MessagesController.this.processLoadedDialogs(paramAnonymousTL_error, null, 0, 1, 2, false, false);
                }
              }
              if (paramLong != 0L) {
                MessagesStorage.getInstance().removePendingTask(paramLong);
              }
              MessagesController.this.gettingUnknownChannels.remove(Integer.valueOf(this.val$channel.id));
            }
          });
          return;
        }
        catch (Exception localException2)
        {
          for (;;) {}
        }
        localException1 = localException1;
        localObject1 = localObject2;
      }
    }
  }
  
  public void markChannelDialogMessageAsDeleted(ArrayList<Integer> paramArrayList, int paramInt)
  {
    MessageObject localMessageObject = (MessageObject)this.dialogMessage.get(Long.valueOf(-paramInt));
    if (localMessageObject != null) {
      paramInt = 0;
    }
    for (;;)
    {
      if (paramInt < paramArrayList.size())
      {
        Integer localInteger = (Integer)paramArrayList.get(paramInt);
        if (localMessageObject.getId() == localInteger.intValue()) {
          localMessageObject.deleted = true;
        }
      }
      else
      {
        return;
      }
      paramInt += 1;
    }
  }
  
  public void markDialogAsRead(final long paramLong, int paramInt1, final int paramInt2, int paramInt3, boolean paramBoolean1, final boolean paramBoolean2)
  {
    int i = (int)paramLong;
    int j = (int)(paramLong >> 32);
    if (i != 0) {
      if ((paramInt2 != 0) && (j != 1)) {}
    }
    Object localObject1;
    do
    {
      Object localObject2;
      do
      {
        return;
        localObject2 = getInputPeer(i);
        long l = paramInt2;
        if ((localObject2 instanceof TLRPC.TL_inputPeerChannel))
        {
          localObject1 = new TLRPC.TL_channels_readHistory();
          ((TLRPC.TL_channels_readHistory)localObject1).channel = getInputChannel(-i);
          ((TLRPC.TL_channels_readHistory)localObject1).max_id = paramInt2;
          l |= -i << 32;
        }
        for (;;)
        {
          Integer localInteger = (Integer)this.dialogs_read_inbox_max.get(Long.valueOf(paramLong));
          localObject2 = localInteger;
          if (localInteger == null) {
            localObject2 = Integer.valueOf(0);
          }
          this.dialogs_read_inbox_max.put(Long.valueOf(paramLong), Integer.valueOf(Math.max(((Integer)localObject2).intValue(), paramInt2)));
          MessagesStorage.getInstance().processPendingRead(paramLong, l, paramInt3);
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
          {
            public void run()
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  Object localObject = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(MessagesController.61.this.val$dialog_id));
                  if (localObject != null)
                  {
                    ((TLRPC.TL_dialog)localObject).unread_count = 0;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
                  }
                  if (!MessagesController.61.this.val$popup)
                  {
                    NotificationsController.getInstance().processReadMessages(null, MessagesController.61.this.val$dialog_id, 0, MessagesController.61.this.val$max_positive_id, false);
                    localObject = new HashMap();
                    ((HashMap)localObject).put(Long.valueOf(MessagesController.61.this.val$dialog_id), Integer.valueOf(0));
                    NotificationsController.getInstance().processDialogsUpdateRead((HashMap)localObject);
                    return;
                  }
                  NotificationsController.getInstance().processReadMessages(null, MessagesController.61.this.val$dialog_id, 0, MessagesController.61.this.val$max_positive_id, true);
                  localObject = new HashMap();
                  ((HashMap)localObject).put(Long.valueOf(MessagesController.61.this.val$dialog_id), Integer.valueOf(-1));
                  NotificationsController.getInstance().processDialogsUpdateRead((HashMap)localObject);
                }
              });
            }
          });
          if (paramInt2 == Integer.MAX_VALUE) {
            break;
          }
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if ((paramAnonymousTL_error == null) && ((paramAnonymousTLObject instanceof TLRPC.TL_messages_affectedMessages)))
              {
                paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
                MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
              }
            }
          });
          return;
          localObject1 = new TLRPC.TL_messages_readHistory();
          ((TLRPC.TL_messages_readHistory)localObject1).peer = ((TLRPC.InputPeer)localObject2);
          ((TLRPC.TL_messages_readHistory)localObject1).max_id = paramInt2;
        }
      } while (paramInt3 == 0);
      localObject1 = getEncryptedChat(Integer.valueOf(j));
      if ((((TLRPC.EncryptedChat)localObject1).auth_key != null) && (((TLRPC.EncryptedChat)localObject1).auth_key.length > 1) && ((localObject1 instanceof TLRPC.TL_encryptedChat)))
      {
        localObject2 = new TLRPC.TL_messages_readEncryptedHistory();
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer = new TLRPC.TL_inputEncryptedChat();
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer.chat_id = ((TLRPC.EncryptedChat)localObject1).id;
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer.access_hash = ((TLRPC.EncryptedChat)localObject1).access_hash;
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).max_date = paramInt3;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        });
      }
      MessagesStorage.getInstance().processPendingRead(paramLong, paramInt1, paramInt3);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.getInstance().processReadMessages(null, MessagesController.64.this.val$dialog_id, MessagesController.64.this.val$max_date, 0, MessagesController.64.this.val$popup);
              Object localObject = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(MessagesController.64.this.val$dialog_id));
              if (localObject != null)
              {
                ((TLRPC.TL_dialog)localObject).unread_count = 0;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
              }
              localObject = new HashMap();
              ((HashMap)localObject).put(Long.valueOf(MessagesController.64.this.val$dialog_id), Integer.valueOf(0));
              NotificationsController.getInstance().processDialogsUpdateRead((HashMap)localObject);
            }
          });
        }
      });
    } while ((((TLRPC.EncryptedChat)localObject1).ttl <= 0) || (!paramBoolean1));
    paramInt1 = Math.max(ConnectionsManager.getInstance().getCurrentTime(), paramInt3);
    MessagesStorage.getInstance().createTaskForSecretChat(((TLRPC.EncryptedChat)localObject1).id, paramInt1, paramInt1, 0, null);
  }
  
  public void markMessageAsRead(long paramLong1, long paramLong2, int paramInt)
  {
    if ((paramLong2 == 0L) || (paramLong1 == 0L) || ((paramInt <= 0) && (paramInt != Integer.MIN_VALUE))) {}
    TLRPC.EncryptedChat localEncryptedChat;
    ArrayList localArrayList;
    do
    {
      do
      {
        int i;
        int j;
        do
        {
          return;
          i = (int)paramLong1;
          j = (int)(paramLong1 >> 32);
        } while (i != 0);
        localEncryptedChat = getEncryptedChat(Integer.valueOf(j));
      } while (localEncryptedChat == null);
      localArrayList = new ArrayList();
      localArrayList.add(Long.valueOf(paramLong2));
      SecretChatHelper.getInstance().sendMessagesReadMessage(localEncryptedChat, localArrayList, null);
    } while (paramInt <= 0);
    paramInt = ConnectionsManager.getInstance().getCurrentTime();
    MessagesStorage.getInstance().createTaskForSecretChat(localEncryptedChat.id, paramInt, paramInt, 0, localArrayList);
  }
  
  public void markMessageContentAsRead(MessageObject paramMessageObject)
  {
    Object localObject = new ArrayList();
    long l2 = paramMessageObject.getId();
    long l1 = l2;
    if (paramMessageObject.messageOwner.to_id.channel_id != 0) {
      l1 = l2 | paramMessageObject.messageOwner.to_id.channel_id << 32;
    }
    ((ArrayList)localObject).add(Long.valueOf(l1));
    MessagesStorage.getInstance().markMessagesContentAsRead((ArrayList)localObject);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadContent, new Object[] { localObject });
    if (paramMessageObject.getId() < 0)
    {
      markMessageAsRead(paramMessageObject.getDialogId(), paramMessageObject.messageOwner.random_id, Integer.MIN_VALUE);
      return;
    }
    localObject = new TLRPC.TL_messages_readMessageContents();
    ((TLRPC.TL_messages_readMessageContents)localObject).id.add(Integer.valueOf(paramMessageObject.getId()));
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
          MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
        }
      }
    });
  }
  
  public void performLogout(boolean paramBoolean)
  {
    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().clear().commit();
    ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0L).putLong("lastStickersLoadTime", 0L).commit();
    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("gifhint").commit();
    if (paramBoolean)
    {
      unregistedPush();
      TLRPC.TL_auth_logOut localTL_auth_logOut = new TLRPC.TL_auth_logOut();
      ConnectionsManager.getInstance().sendRequest(localTL_auth_logOut, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          ConnectionsManager.getInstance().cleanup();
        }
      });
    }
    for (;;)
    {
      UserConfig.clearConfig();
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
      MessagesStorage.getInstance().cleanup(false);
      cleanup();
      ContactsController.getInstance().deleteAllAppAccounts();
      return;
      ConnectionsManager.getInstance().cleanup();
    }
  }
  
  public void pinChannelMessage(TLRPC.Chat paramChat, int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_updatePinnedMessage localTL_channels_updatePinnedMessage = new TLRPC.TL_channels_updatePinnedMessage();
    localTL_channels_updatePinnedMessage.channel = getInputChannel(paramChat);
    localTL_channels_updatePinnedMessage.id = paramInt;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      localTL_channels_updatePinnedMessage.silent = paramBoolean;
      ConnectionsManager.getInstance().sendRequest(localTL_channels_updatePinnedMessage, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
            MessagesController.this.processUpdates(paramAnonymousTLObject, false);
          }
        }
      });
      return;
    }
  }
  
  public void processChatInfo(int paramInt, final TLRPC.ChatFull paramChatFull, final ArrayList<TLRPC.User> paramArrayList, final boolean paramBoolean1, boolean paramBoolean2, final boolean paramBoolean3, final MessageObject paramMessageObject)
  {
    if ((paramBoolean1) && (paramInt > 0) && (!paramBoolean3)) {
      loadFullChat(paramInt, 0, paramBoolean2);
    }
    if (paramChatFull != null) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.this.putUsers(paramArrayList, paramBoolean1);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(paramBoolean3), paramMessageObject });
        }
      });
    }
  }
  
  public void processDialogsUpdate(final TLRPC.messages_Dialogs parammessages_Dialogs, ArrayList<TLRPC.EncryptedChat> paramArrayList)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final HashMap localHashMap1 = new HashMap();
        final HashMap localHashMap2 = new HashMap();
        Object localObject1 = new HashMap();
        HashMap localHashMap3 = new HashMap();
        final HashMap localHashMap4 = new HashMap();
        int i = 0;
        Object localObject2;
        while (i < parammessages_Dialogs.users.size())
        {
          localObject2 = (TLRPC.User)parammessages_Dialogs.users.get(i);
          ((HashMap)localObject1).put(Integer.valueOf(((TLRPC.User)localObject2).id), localObject2);
          i += 1;
        }
        i = 0;
        while (i < parammessages_Dialogs.chats.size())
        {
          localObject2 = (TLRPC.Chat)parammessages_Dialogs.chats.get(i);
          localHashMap3.put(Integer.valueOf(((TLRPC.Chat)localObject2).id), localObject2);
          i += 1;
        }
        i = 0;
        Object localObject3;
        if (i < parammessages_Dialogs.messages.size())
        {
          localObject2 = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
          if (((TLRPC.Message)localObject2).to_id.channel_id != 0)
          {
            localObject3 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject2).to_id.channel_id));
            if ((localObject3 == null) || (!((TLRPC.Chat)localObject3).left)) {
              break label271;
            }
          }
          for (;;)
          {
            i += 1;
            break;
            if (((TLRPC.Message)localObject2).to_id.chat_id != 0)
            {
              localObject3 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject2).to_id.chat_id));
              if ((localObject3 != null) && (((TLRPC.Chat)localObject3).migrated_to != null)) {}
            }
            else
            {
              label271:
              localObject2 = new MessageObject((TLRPC.Message)localObject2, (AbstractMap)localObject1, localHashMap3, false);
              localHashMap2.put(Long.valueOf(((MessageObject)localObject2).getDialogId()), localObject2);
            }
          }
        }
        i = 0;
        if (i < parammessages_Dialogs.dialogs.size())
        {
          localObject3 = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(i);
          if (((TLRPC.TL_dialog)localObject3).id == 0L)
          {
            if (((TLRPC.TL_dialog)localObject3).peer.user_id != 0) {
              ((TLRPC.TL_dialog)localObject3).id = ((TLRPC.TL_dialog)localObject3).peer.user_id;
            }
          }
          else
          {
            label368:
            if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject3)) {
              break label471;
            }
            localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
            if ((localObject1 == null) || (!((TLRPC.Chat)localObject1).left)) {
              break label510;
            }
          }
          for (;;)
          {
            i += 1;
            break;
            if (((TLRPC.TL_dialog)localObject3).peer.chat_id != 0)
            {
              ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.chat_id);
              break label368;
            }
            if (((TLRPC.TL_dialog)localObject3).peer.channel_id == 0) {
              break label368;
            }
            ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.channel_id);
            break label368;
            label471:
            if ((int)((TLRPC.TL_dialog)localObject3).id < 0)
            {
              localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
              if ((localObject1 != null) && (((TLRPC.Chat)localObject1).migrated_to != null)) {}
            }
            else
            {
              label510:
              if (((TLRPC.TL_dialog)localObject3).last_message_date == 0)
              {
                localObject1 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
                if (localObject1 != null) {
                  ((TLRPC.TL_dialog)localObject3).last_message_date = ((MessageObject)localObject1).messageOwner.date;
                }
              }
              localHashMap1.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), localObject3);
              localHashMap4.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(((TLRPC.TL_dialog)localObject3).unread_count));
              localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
              localObject1 = localObject2;
              if (localObject2 == null) {
                localObject1 = Integer.valueOf(0);
              }
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_inbox_max_id)));
              localObject2 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
              localObject1 = localObject2;
              if (localObject2 == null) {
                localObject1 = Integer.valueOf(0);
              }
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_outbox_max_id)));
            }
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.putUsers(MessagesController.58.this.val$dialogsRes.users, true);
            MessagesController.this.putChats(MessagesController.58.this.val$dialogsRes.chats, true);
            Iterator localIterator = localHashMap1.entrySet().iterator();
            while (localIterator.hasNext())
            {
              Object localObject1 = (Map.Entry)localIterator.next();
              Long localLong = (Long)((Map.Entry)localObject1).getKey();
              localObject1 = (TLRPC.TL_dialog)((Map.Entry)localObject1).getValue();
              Object localObject3 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(localLong);
              Object localObject2;
              if (localObject3 == null)
              {
                localObject2 = MessagesController.this;
                ((MessagesController)localObject2).nextDialogsCacheOffset += 1;
                MessagesController.this.dialogs_dict.put(localLong, localObject1);
                localObject1 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                MessagesController.this.dialogMessage.put(localLong, localObject1);
                if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.to_id.channel_id == 0))
                {
                  MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
                  if (((MessageObject)localObject1).messageOwner.random_id != 0L) {
                    MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id), localObject1);
                  }
                }
              }
              else
              {
                ((TLRPC.TL_dialog)localObject3).unread_count = ((TLRPC.TL_dialog)localObject1).unread_count;
                localObject2 = (MessageObject)MessagesController.this.dialogMessage.get(localLong);
                if ((localObject2 == null) || (((TLRPC.TL_dialog)localObject3).top_message > 0))
                {
                  if (((localObject2 != null) && (((MessageObject)localObject2).deleted)) || (((TLRPC.TL_dialog)localObject1).top_message > ((TLRPC.TL_dialog)localObject3).top_message))
                  {
                    MessagesController.this.dialogs_dict.put(localLong, localObject1);
                    localObject3 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                    MessagesController.this.dialogMessage.put(localLong, localObject3);
                    if ((localObject3 != null) && (((MessageObject)localObject3).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject3).getId()), localObject3);
                      if (((MessageObject)localObject3).messageOwner.random_id != 0L) {
                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id), localObject3);
                      }
                    }
                    if (localObject2 != null)
                    {
                      MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject2).getId()));
                      if (((MessageObject)localObject2).messageOwner.random_id != 0L) {
                        MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject2).messageOwner.random_id));
                      }
                    }
                    if (localObject3 == null) {
                      MessagesController.this.checkLastDialogMessage((TLRPC.TL_dialog)localObject1, null, 0L);
                    }
                  }
                }
                else
                {
                  localObject3 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                  if ((((MessageObject)localObject2).deleted) || (localObject3 == null) || (((MessageObject)localObject3).messageOwner.date > ((MessageObject)localObject2).messageOwner.date))
                  {
                    MessagesController.this.dialogs_dict.put(localLong, localObject1);
                    MessagesController.this.dialogMessage.put(localLong, localObject3);
                    if ((localObject3 != null) && (((MessageObject)localObject3).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject3).getId()), localObject3);
                      if (((MessageObject)localObject3).messageOwner.random_id != 0L) {
                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id), localObject3);
                      }
                    }
                    MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject2).getId()));
                    if (((MessageObject)localObject2).messageOwner.random_id != 0L) {
                      MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject2).messageOwner.random_id));
                    }
                  }
                }
              }
            }
            MessagesController.this.dialogs.clear();
            MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
            MessagesController.this.sortDialogs(null);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationsController.getInstance().processDialogsUpdateRead(localHashMap4);
          }
        });
      }
    });
  }
  
  public void processDialogsUpdateRead(final HashMap<Long, Integer> paramHashMap)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        Iterator localIterator = paramHashMap.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(localEntry.getKey());
          if (localTL_dialog != null) {
            localTL_dialog.unread_count = ((Integer)localEntry.getValue()).intValue();
          }
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
        NotificationsController.getInstance().processDialogsUpdateRead(paramHashMap);
      }
    });
  }
  
  public void processLoadedBlockedUsers(final ArrayList<Integer> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramArrayList1 != null) {
          MessagesController.this.putUsers(paramArrayList1, paramBoolean);
        }
        MessagesController.this.loadingBlockedUsers = false;
        if ((paramArrayList.isEmpty()) && (paramBoolean) && (!UserConfig.blockedUsersLoaded))
        {
          MessagesController.this.getBlockedUsers(false);
          return;
        }
        if (!paramBoolean)
        {
          UserConfig.blockedUsersLoaded = true;
          UserConfig.saveConfig(false);
        }
        MessagesController.this.blockedUsers = paramArrayList;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
      }
    });
  }
  
  public void processLoadedDeleteTask(final int paramInt, final ArrayList<Integer> paramArrayList)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.access$3002(MessagesController.this, false);
        if (paramArrayList != null)
        {
          MessagesController.access$3102(MessagesController.this, paramInt);
          MessagesController.access$2902(MessagesController.this, paramArrayList);
          if (MessagesController.this.currentDeleteTaskRunnable != null)
          {
            Utilities.stageQueue.cancelRunnable(MessagesController.this.currentDeleteTaskRunnable);
            MessagesController.access$3202(MessagesController.this, null);
          }
          if (!MessagesController.this.checkDeletingTask(false))
          {
            MessagesController.access$3202(MessagesController.this, new Runnable()
            {
              public void run()
              {
                MessagesController.this.checkDeletingTask(true);
              }
            });
            int i = ConnectionsManager.getInstance().getCurrentTime();
            Utilities.stageQueue.postRunnable(MessagesController.this.currentDeleteTaskRunnable, Math.abs(i - MessagesController.this.currentDeletingTaskTime) * 1000L);
          }
          return;
        }
        MessagesController.access$3102(MessagesController.this, 0);
        MessagesController.access$2902(MessagesController.this, null);
      }
    });
  }
  
  public void processLoadedDialogs(final TLRPC.messages_Dialogs parammessages_Dialogs, final ArrayList<TLRPC.EncryptedChat> paramArrayList, final int paramInt1, final int paramInt2, final int paramInt3, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileLog.e("tmessages", "loaded loadType " + paramInt3 + " count " + parammessages_Dialogs.dialogs.size());
        if ((paramInt3 == 1) && (parammessages_Dialogs.dialogs.size() == 0))
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.putUsers(MessagesController.55.this.val$dialogsRes.users, true);
              MessagesController.this.loadingDialogs = false;
              if (MessagesController.55.this.val$resetEnd) {
                MessagesController.this.dialogsEndReached = false;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              MessagesController.this.loadDialogs(0, MessagesController.55.this.val$count, false);
            }
          });
          return;
        }
        final HashMap localHashMap1 = new HashMap();
        final HashMap localHashMap2 = new HashMap();
        HashMap localHashMap4 = new HashMap();
        final HashMap localHashMap3 = new HashMap();
        int i = 0;
        Object localObject1;
        while (i < parammessages_Dialogs.users.size())
        {
          localObject1 = (TLRPC.User)parammessages_Dialogs.users.get(i);
          localHashMap4.put(Integer.valueOf(((TLRPC.User)localObject1).id), localObject1);
          i += 1;
        }
        i = 0;
        while (i < parammessages_Dialogs.chats.size())
        {
          localObject1 = (TLRPC.Chat)parammessages_Dialogs.chats.get(i);
          localHashMap3.put(Integer.valueOf(((TLRPC.Chat)localObject1).id), localObject1);
          i += 1;
        }
        if (paramInt3 == 1) {
          MessagesController.this.nextDialogsCacheOffset = (paramInt1 + paramInt2);
        }
        i = 0;
        Object localObject2;
        if (i < parammessages_Dialogs.messages.size())
        {
          localObject1 = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
          if (((TLRPC.Message)localObject1).to_id.channel_id != 0)
          {
            localObject2 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject1).to_id.channel_id));
            if ((localObject2 == null) || (!((TLRPC.Chat)localObject2).left)) {}
          }
          for (;;)
          {
            i += 1;
            break;
            if ((localObject2 != null) && (((TLRPC.Chat)localObject2).megagroup)) {
              ((TLRPC.Message)localObject1).flags |= 0x80000000;
            }
            do
            {
              do
              {
                if ((paramInt3 != 1) && (((TLRPC.Message)localObject1).post) && (!((TLRPC.Message)localObject1).out)) {
                  ((TLRPC.Message)localObject1).media_unread = true;
                }
                localObject1 = new MessageObject((TLRPC.Message)localObject1, localHashMap4, localHashMap3, false);
                localHashMap2.put(Long.valueOf(((MessageObject)localObject1).getDialogId()), localObject1);
                break;
              } while (((TLRPC.Message)localObject1).to_id.chat_id == 0);
              localObject2 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject1).to_id.chat_id));
            } while ((localObject2 == null) || (((TLRPC.Chat)localObject2).migrated_to == null));
          }
        }
        final ArrayList localArrayList = new ArrayList();
        int j = 0;
        Object localObject3;
        if (j < parammessages_Dialogs.dialogs.size())
        {
          localObject3 = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(j);
          if ((((TLRPC.TL_dialog)localObject3).id == 0L) && (((TLRPC.TL_dialog)localObject3).peer != null))
          {
            if (((TLRPC.TL_dialog)localObject3).peer.user_id != 0) {
              ((TLRPC.TL_dialog)localObject3).id = ((TLRPC.TL_dialog)localObject3).peer.user_id;
            }
          }
          else {
            label555:
            if (((TLRPC.TL_dialog)localObject3).id != 0L) {
              break label630;
            }
          }
          for (;;)
          {
            j += 1;
            break;
            if (((TLRPC.TL_dialog)localObject3).peer.chat_id != 0)
            {
              ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.chat_id);
              break label555;
            }
            if (((TLRPC.TL_dialog)localObject3).peer.channel_id == 0) {
              break label555;
            }
            ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.channel_id);
            break label555;
            label630:
            if (((TLRPC.TL_dialog)localObject3).last_message_date == 0)
            {
              localObject1 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
              if (localObject1 != null) {
                ((TLRPC.TL_dialog)localObject3).last_message_date = ((MessageObject)localObject1).messageOwner.date;
              }
            }
            i = 1;
            int m = 1;
            int k = 1;
            if (DialogObject.isChannel((TLRPC.TL_dialog)localObject3))
            {
              localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
              if (localObject1 != null)
              {
                i = k;
                if (!((TLRPC.Chat)localObject1).megagroup) {
                  i = 0;
                }
                if (((TLRPC.Chat)localObject1).left) {}
              }
              else
              {
                MessagesController.this.channelsPts.put(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(((TLRPC.TL_dialog)localObject3).pts));
              }
            }
            else
            {
              do
              {
                do
                {
                  do
                  {
                    localHashMap1.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), localObject3);
                    if ((i != 0) && (paramInt3 == 1) && ((((TLRPC.TL_dialog)localObject3).read_outbox_max_id == 0) || (((TLRPC.TL_dialog)localObject3).read_inbox_max_id == 0)) && (((TLRPC.TL_dialog)localObject3).top_message != 0)) {
                      localArrayList.add(localObject3);
                    }
                    localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
                    localObject1 = localObject2;
                    if (localObject2 == null) {
                      localObject1 = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_inbox_max_id)));
                    localObject2 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
                    localObject1 = localObject2;
                    if (localObject2 == null) {
                      localObject1 = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_outbox_max_id)));
                    break;
                    i = m;
                  } while ((int)((TLRPC.TL_dialog)localObject3).id >= 0);
                  localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
                  i = m;
                } while (localObject1 == null);
                i = m;
              } while (((TLRPC.Chat)localObject1).migrated_to == null);
            }
          }
        }
        if (paramInt3 != 1)
        {
          ImageLoader.saveMessagesThumbs(parammessages_Dialogs.messages);
          i = 0;
          while (i < parammessages_Dialogs.messages.size())
          {
            TLRPC.Message localMessage = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
            if ((localMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
            {
              localObject1 = (TLRPC.User)localHashMap4.get(Integer.valueOf(localMessage.action.user_id));
              if ((localObject1 != null) && (((TLRPC.User)localObject1).bot)) {
                localMessage.reply_markup = new TLRPC.TL_replyKeyboardHide();
              }
            }
            if (((localMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((localMessage.action instanceof TLRPC.TL_messageActionChannelCreate)))
            {
              localMessage.unread = false;
              localMessage.media_unread = false;
              i += 1;
            }
            else
            {
              if (localMessage.out)
              {
                localObject1 = MessagesController.this.dialogs_read_outbox_max;
                label1191:
                localObject3 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(localMessage.dialog_id));
                localObject2 = localObject3;
                if (localObject3 == null)
                {
                  localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localMessage.out, localMessage.dialog_id));
                  ((ConcurrentHashMap)localObject1).put(Long.valueOf(localMessage.dialog_id), localObject2);
                }
                if (((Integer)localObject2).intValue() >= localMessage.id) {
                  break label1293;
                }
              }
              label1293:
              for (boolean bool = true;; bool = false)
              {
                localMessage.unread = bool;
                break;
                localObject1 = MessagesController.this.dialogs_read_inbox_max;
                break label1191;
              }
            }
          }
          MessagesStorage.getInstance().putDialogs(parammessages_Dialogs);
        }
        if (paramInt3 == 2)
        {
          localObject1 = (TLRPC.Chat)parammessages_Dialogs.chats.get(0);
          MessagesController.this.getChannelDifference(((TLRPC.Chat)localObject1).id);
          MessagesController.this.checkChannelInviter(((TLRPC.Chat)localObject1).id);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (MessagesController.55.this.val$loadType != 1)
            {
              MessagesController.this.applyDialogsNotificationsSettings(MessagesController.55.this.val$dialogsRes.dialogs);
              if (!UserConfig.draftsLoaded) {
                DraftQuery.loadDrafts();
              }
            }
            Object localObject1 = MessagesController.this;
            Object localObject2 = MessagesController.55.this.val$dialogsRes.users;
            if (MessagesController.55.this.val$loadType == 1)
            {
              bool = true;
              ((MessagesController)localObject1).putUsers((ArrayList)localObject2, bool);
              localObject1 = MessagesController.this;
              localObject2 = MessagesController.55.this.val$dialogsRes.chats;
              if (MessagesController.55.this.val$loadType != 1) {
                break label226;
              }
            }
            int i;
            label226:
            for (boolean bool = true;; bool = false)
            {
              ((MessagesController)localObject1).putChats((ArrayList)localObject2, bool);
              if (MessagesController.55.this.val$encChats == null) {
                break label232;
              }
              i = 0;
              while (i < MessagesController.55.this.val$encChats.size())
              {
                localObject1 = (TLRPC.EncryptedChat)MessagesController.55.this.val$encChats.get(i);
                if (((localObject1 instanceof TLRPC.TL_encryptedChat)) && (AndroidUtilities.getMyLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) < 46)) {
                  SecretChatHelper.getInstance().sendNotifyLayerMessage((TLRPC.EncryptedChat)localObject1, null);
                }
                MessagesController.this.putEncryptedChat((TLRPC.EncryptedChat)localObject1, true);
                i += 1;
              }
              bool = false;
              break;
            }
            label232:
            if (!MessagesController.55.this.val$migrate) {
              MessagesController.this.loadingDialogs = false;
            }
            int j = 0;
            if ((MessagesController.55.this.val$migrate) && (!MessagesController.this.dialogs.isEmpty()))
            {
              i = ((TLRPC.TL_dialog)MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
              localObject1 = localHashMap1.entrySet().iterator();
            }
            for (;;)
            {
              if (!((Iterator)localObject1).hasNext()) {
                break label1149;
              }
              Object localObject3 = (Map.Entry)((Iterator)localObject1).next();
              localObject2 = (Long)((Map.Entry)localObject3).getKey();
              Object localObject4 = (TLRPC.TL_dialog)((Map.Entry)localObject3).getValue();
              if ((!MessagesController.55.this.val$migrate) || (i == 0) || (((TLRPC.TL_dialog)localObject4).last_message_date >= i))
              {
                Object localObject5 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(localObject2);
                if ((MessagesController.55.this.val$loadType != 1) && ((((TLRPC.TL_dialog)localObject4).draft instanceof TLRPC.TL_draftMessage))) {
                  DraftQuery.saveDraft(((TLRPC.TL_dialog)localObject4).id, ((TLRPC.TL_dialog)localObject4).draft, null, false);
                }
                if (localObject5 == null)
                {
                  int k = 1;
                  MessagesController.this.dialogs_dict.put(localObject2, localObject4);
                  localObject3 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject4).id));
                  MessagesController.this.dialogMessage.put(localObject2, localObject3);
                  j = k;
                  if (localObject3 == null) {
                    continue;
                  }
                  j = k;
                  if (((MessageObject)localObject3).messageOwner.to_id.channel_id != 0) {
                    continue;
                  }
                  MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject3).getId()), localObject3);
                  j = k;
                  if (((MessageObject)localObject3).messageOwner.random_id == 0L) {
                    continue;
                  }
                  MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id), localObject3);
                  j = k;
                  continue;
                  i = 0;
                  break;
                }
                if (MessagesController.55.this.val$loadType != 1) {
                  ((TLRPC.TL_dialog)localObject5).notify_settings = ((TLRPC.TL_dialog)localObject4).notify_settings;
                }
                localObject3 = (MessageObject)MessagesController.this.dialogMessage.get(localObject2);
                if (((localObject3 != null) && (((MessageObject)localObject3).deleted)) || (localObject3 == null) || (((TLRPC.TL_dialog)localObject5).top_message > 0))
                {
                  if (((TLRPC.TL_dialog)localObject4).top_message >= ((TLRPC.TL_dialog)localObject5).top_message)
                  {
                    MessagesController.this.dialogs_dict.put(localObject2, localObject4);
                    localObject4 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject4).id));
                    MessagesController.this.dialogMessage.put(localObject2, localObject4);
                    if ((localObject4 != null) && (((MessageObject)localObject4).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject4).getId()), localObject4);
                      if ((localObject4 != null) && (((MessageObject)localObject4).messageOwner.random_id != 0L)) {
                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject4).messageOwner.random_id), localObject4);
                      }
                    }
                    if (localObject3 != null)
                    {
                      MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject3).getId()));
                      if (((MessageObject)localObject3).messageOwner.random_id != 0L) {
                        MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id));
                      }
                    }
                  }
                }
                else
                {
                  localObject5 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject4).id));
                  if ((((MessageObject)localObject3).deleted) || (localObject5 == null) || (((MessageObject)localObject5).messageOwner.date > ((MessageObject)localObject3).messageOwner.date))
                  {
                    MessagesController.this.dialogs_dict.put(localObject2, localObject4);
                    MessagesController.this.dialogMessage.put(localObject2, localObject5);
                    if ((localObject5 != null) && (((MessageObject)localObject5).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject5).getId()), localObject5);
                      if ((localObject5 != null) && (((MessageObject)localObject5).messageOwner.random_id != 0L)) {
                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject5).messageOwner.random_id), localObject5);
                      }
                    }
                    MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject3).getId()));
                    if (((MessageObject)localObject3).messageOwner.random_id != 0L) {
                      MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id));
                    }
                  }
                }
              }
            }
            label1149:
            MessagesController.this.dialogs.clear();
            MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
            localObject2 = MessagesController.this;
            if (MessagesController.55.this.val$migrate)
            {
              localObject1 = localHashMap3;
              ((MessagesController)localObject2).sortDialogs((HashMap)localObject1);
              if ((MessagesController.55.this.val$loadType != 2) && (!MessagesController.55.this.val$migrate))
              {
                localObject1 = MessagesController.this;
                if (((MessagesController.55.this.val$dialogsRes.dialogs.size() != 0) && (MessagesController.55.this.val$dialogsRes.dialogs.size() == MessagesController.55.this.val$count)) || (MessagesController.55.this.val$loadType != 0)) {
                  break label1432;
                }
                bool = true;
                label1303:
                ((MessagesController)localObject1).dialogsEndReached = bool;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              if (!MessagesController.55.this.val$migrate) {
                break label1438;
              }
              UserConfig.migrateOffsetId = MessagesController.55.this.val$offset;
              UserConfig.saveConfig(false);
              MessagesController.access$4502(MessagesController.this, false);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
            }
            for (;;)
            {
              MessagesController.this.migrateDialogs(UserConfig.migrateOffsetId, UserConfig.migrateOffsetDate, UserConfig.migrateOffsetUserId, UserConfig.migrateOffsetChatId, UserConfig.migrateOffsetChannelId, UserConfig.migrateOffsetAccess);
              if (!localArrayList.isEmpty()) {
                MessagesController.this.reloadDialogsReadValue(localArrayList, 0L);
              }
              return;
              localObject1 = null;
              break;
              label1432:
              bool = false;
              break label1303;
              label1438:
              MessagesController.this.generateUpdateMessage();
              if ((j == 0) && (MessagesController.55.this.val$loadType == 1)) {
                MessagesController.this.loadDialogs(0, MessagesController.55.this.val$count, false);
              }
            }
          }
        });
      }
    });
  }
  
  public void processLoadedMessages(final TLRPC.messages_Messages parammessages_Messages, final long paramLong, final int paramInt1, final int paramInt2, boolean paramBoolean1, final int paramInt3, final int paramInt4, final int paramInt5, final int paramInt6, final int paramInt7, final int paramInt8, final boolean paramBoolean2, final boolean paramBoolean3, final int paramInt9, final boolean paramBoolean4)
  {
    FileLog.e("tmessages", "processLoadedMessages size " + parammessages_Messages.messages.size() + " in chat " + paramLong + " count " + paramInt1 + " max_id " + paramInt2 + " cache " + paramBoolean1 + " guid " + paramInt3 + " load_type " + paramInt8 + " last_message_id " + paramInt5 + " isChannel " + paramBoolean2 + " index " + paramInt9 + " firstUnread " + paramInt4 + " underad count " + paramInt6 + " last_date " + paramInt7 + " queryFromServer " + paramBoolean4);
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        boolean bool2 = false;
        boolean bool5 = false;
        boolean bool4 = false;
        boolean bool3 = bool4;
        boolean bool1;
        if ((parammessages_Messages instanceof TLRPC.TL_messages_channelMessages))
        {
          j = -(int)paramLong;
          bool1 = bool5;
          if ((Integer)MessagesController.this.channelsPts.get(Integer.valueOf(j)) == null)
          {
            bool1 = bool5;
            if (Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(j)).intValue() == 0)
            {
              MessagesController.this.channelsPts.put(Integer.valueOf(j), Integer.valueOf(parammessages_Messages.pts));
              bool1 = true;
              if ((MessagesController.this.needShortPollChannels.indexOfKey(j) < 0) || (MessagesController.this.shortPollChannels.indexOfKey(j) >= 0)) {
                break label268;
              }
              MessagesController.this.getChannelDifference(j, 2, 0L);
            }
          }
          i = 0;
        }
        Object localObject1;
        for (;;)
        {
          bool2 = bool1;
          bool3 = bool4;
          if (i < parammessages_Messages.chats.size())
          {
            localObject1 = (TLRPC.Chat)parammessages_Messages.chats.get(i);
            if (((TLRPC.Chat)localObject1).id == j)
            {
              bool3 = ((TLRPC.Chat)localObject1).megagroup;
              bool2 = bool1;
            }
          }
          else
          {
            i = (int)paramLong;
            j = (int)(paramLong >> 32);
            if (!paramInt1) {
              ImageLoader.saveMessagesThumbs(parammessages_Messages.messages);
            }
            if ((j == 1) || (i == 0) || (!paramInt1) || (parammessages_Messages.messages.size() != 0)) {
              break label286;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController localMessagesController = MessagesController.this;
                long l = MessagesController.52.this.val$dialog_id;
                int j = MessagesController.52.this.val$count;
                if ((MessagesController.52.this.val$load_type == 2) && (MessagesController.52.this.val$queryFromServer)) {}
                for (int i = MessagesController.52.this.val$first_unread;; i = MessagesController.52.this.val$max_id)
                {
                  localMessagesController.loadMessages(l, j, i, false, 0, MessagesController.52.this.val$classGuid, MessagesController.52.this.val$load_type, MessagesController.52.this.val$last_message_id, MessagesController.52.this.val$isChannel, MessagesController.52.this.val$loadIndex, MessagesController.52.this.val$first_unread, MessagesController.52.this.val$unread_count, MessagesController.52.this.val$last_date, MessagesController.52.this.val$queryFromServer);
                  return;
                }
              }
            });
            return;
            label268:
            MessagesController.this.getChannelDifference(j);
            break;
          }
          i += 1;
        }
        label286:
        HashMap localHashMap1 = new HashMap();
        HashMap localHashMap2 = new HashMap();
        int i = 0;
        while (i < parammessages_Messages.users.size())
        {
          localObject1 = (TLRPC.User)parammessages_Messages.users.get(i);
          localHashMap1.put(Integer.valueOf(((TLRPC.User)localObject1).id), localObject1);
          i += 1;
        }
        i = 0;
        while (i < parammessages_Messages.chats.size())
        {
          localObject1 = (TLRPC.Chat)parammessages_Messages.chats.get(i);
          localHashMap2.put(Integer.valueOf(((TLRPC.Chat)localObject1).id), localObject1);
          i += 1;
        }
        int j = parammessages_Messages.messages.size();
        Object localObject2;
        if (!paramInt1)
        {
          localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(paramLong));
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject1 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, paramLong));
            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(paramLong), localObject1);
          }
          localObject3 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(paramLong));
          localObject2 = localObject3;
          if (localObject3 == null)
          {
            localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, paramLong));
            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(paramLong), localObject2);
          }
          i = 0;
          while (i < j)
          {
            localObject4 = (TLRPC.Message)parammessages_Messages.messages.get(i);
            if ((!paramInt1) && (((TLRPC.Message)localObject4).post) && (!((TLRPC.Message)localObject4).out)) {
              ((TLRPC.Message)localObject4).media_unread = true;
            }
            if (bool3) {
              ((TLRPC.Message)localObject4).flags |= 0x80000000;
            }
            if ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChatDeleteUser))
            {
              localObject3 = (TLRPC.User)localHashMap1.get(Integer.valueOf(((TLRPC.Message)localObject4).action.user_id));
              if ((localObject3 != null) && (((TLRPC.User)localObject3).bot)) {
                ((TLRPC.Message)localObject4).reply_markup = new TLRPC.TL_replyKeyboardHide();
              }
            }
            if (((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChannelCreate)))
            {
              ((TLRPC.Message)localObject4).unread = false;
              ((TLRPC.Message)localObject4).media_unread = false;
              i += 1;
            }
            else
            {
              if (((TLRPC.Message)localObject4).out)
              {
                localObject3 = localObject2;
                label746:
                if (((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject4).id) {
                  break label777;
                }
              }
              label777:
              for (bool1 = true;; bool1 = false)
              {
                ((TLRPC.Message)localObject4).unread = bool1;
                break;
                localObject3 = localObject1;
                break label746;
              }
            }
          }
          MessagesStorage.getInstance().putMessages(parammessages_Messages, paramLong, paramBoolean4, paramInt3, bool2);
        }
        final Object localObject3 = new ArrayList();
        final Object localObject4 = new ArrayList();
        final HashMap localHashMap3 = new HashMap();
        i = 0;
        if (i < j)
        {
          TLRPC.Message localMessage = (TLRPC.Message)parammessages_Messages.messages.get(i);
          localMessage.dialog_id = paramLong;
          MessageObject localMessageObject = new MessageObject(localMessage, localHashMap1, localHashMap2, true);
          ((ArrayList)localObject3).add(localMessageObject);
          if (paramInt1)
          {
            if (!(localMessage.media instanceof TLRPC.TL_messageMediaUnsupported)) {
              break label979;
            }
            if ((localMessage.media.bytes != null) && ((localMessage.media.bytes.length == 0) || ((localMessage.media.bytes.length == 1) && (localMessage.media.bytes[0] < 57)))) {
              ((ArrayList)localObject4).add(Integer.valueOf(localMessage.id));
            }
          }
          for (;;)
          {
            i += 1;
            break;
            label979:
            if ((localMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
              if (((localMessage.media.webpage instanceof TLRPC.TL_webPagePending)) && (localMessage.media.webpage.date <= ConnectionsManager.getInstance().getCurrentTime()))
              {
                ((ArrayList)localObject4).add(Integer.valueOf(localMessage.id));
              }
              else if ((localMessage.media.webpage instanceof TLRPC.TL_webPageUrlPending))
              {
                localObject2 = (ArrayList)localHashMap3.get(localMessage.media.webpage.url);
                localObject1 = localObject2;
                if (localObject2 == null)
                {
                  localObject1 = new ArrayList();
                  localHashMap3.put(localMessage.media.webpage.url, localObject1);
                }
                ((ArrayList)localObject1).add(localMessageObject);
              }
            }
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.putUsers(MessagesController.52.this.val$messagesRes.users, MessagesController.52.this.val$isCache);
            MessagesController.this.putChats(MessagesController.52.this.val$messagesRes.chats, MessagesController.52.this.val$isCache);
            int i = Integer.MAX_VALUE;
            int j = i;
            if (MessagesController.52.this.val$queryFromServer)
            {
              j = i;
              if (MessagesController.52.this.val$load_type == 2)
              {
                int k = 0;
                for (;;)
                {
                  j = i;
                  if (k >= MessagesController.52.this.val$messagesRes.messages.size()) {
                    break;
                  }
                  TLRPC.Message localMessage = (TLRPC.Message)MessagesController.52.this.val$messagesRes.messages.get(k);
                  j = i;
                  if (!localMessage.out)
                  {
                    j = i;
                    if (localMessage.id > MessagesController.52.this.val$first_unread)
                    {
                      j = i;
                      if (localMessage.id < i) {
                        j = localMessage.id;
                      }
                    }
                  }
                  k += 1;
                  i = j;
                }
              }
            }
            i = j;
            if (j == Integer.MAX_VALUE) {
              i = MessagesController.52.this.val$first_unread;
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDidLoaded, new Object[] { Long.valueOf(MessagesController.52.this.val$dialog_id), Integer.valueOf(MessagesController.52.this.val$count), localObject3, Boolean.valueOf(MessagesController.52.this.val$isCache), Integer.valueOf(i), Integer.valueOf(MessagesController.52.this.val$last_message_id), Integer.valueOf(MessagesController.52.this.val$unread_count), Integer.valueOf(MessagesController.52.this.val$last_date), Integer.valueOf(MessagesController.52.this.val$load_type), Boolean.valueOf(MessagesController.52.this.val$isEnd), Integer.valueOf(MessagesController.52.this.val$classGuid), Integer.valueOf(MessagesController.52.this.val$loadIndex) });
            if (!localObject4.isEmpty()) {
              MessagesController.this.reloadMessages(localObject4, MessagesController.52.this.val$dialog_id);
            }
            if (!localHashMap3.isEmpty()) {
              MessagesController.this.reloadWebPages(MessagesController.52.this.val$dialog_id, localHashMap3);
            }
          }
        });
      }
    });
  }
  
  public void processLoadedUserPhotos(final TLRPC.photos_Photos paramphotos_Photos, final int paramInt1, final int paramInt2, final int paramInt3, long paramLong, final boolean paramBoolean, final int paramInt4)
  {
    if (!paramBoolean)
    {
      MessagesStorage.getInstance().putUsersAndChats(paramphotos_Photos.users, null, true, true);
      MessagesStorage.getInstance().putDialogPhotos(paramInt1, paramphotos_Photos);
    }
    while ((paramphotos_Photos != null) && (!paramphotos_Photos.photos.isEmpty()))
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.this.putUsers(paramphotos_Photos.users, paramBoolean);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogPhotosLoaded, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt4), paramphotos_Photos.photos });
        }
      });
      return;
    }
    loadDialogPhotos(paramInt1, paramInt2, paramInt3, paramLong, false, paramInt4);
  }
  
  protected void processNewChannelDifferenceParams(int paramInt1, int paramInt2, int paramInt3)
  {
    FileLog.e("tmessages", "processNewChannelDifferenceParams pts = " + paramInt1 + " pts_count = " + paramInt2 + " channeldId = " + paramInt3);
    if (!DialogObject.isChannel((TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(-paramInt3)))) {}
    do
    {
      return;
      localObject2 = (Integer)this.channelsPts.get(Integer.valueOf(paramInt3));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(paramInt3));
        localObject1 = localObject2;
        if (((Integer)localObject2).intValue() == 0) {
          localObject1 = Integer.valueOf(1);
        }
        this.channelsPts.put(Integer.valueOf(paramInt3), localObject1);
      }
      if (((Integer)localObject1).intValue() + paramInt2 == paramInt1)
      {
        FileLog.e("tmessages", "APPLY CHANNEL PTS");
        this.channelsPts.put(Integer.valueOf(paramInt3), Integer.valueOf(paramInt1));
        MessagesStorage.getInstance().saveChannelPts(paramInt3, paramInt1);
        return;
      }
    } while (((Integer)localObject1).intValue() == paramInt1);
    Object localObject3 = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(paramInt3));
    Object localObject2 = (Boolean)this.gettingDifferenceChannels.get(Integer.valueOf(paramInt3));
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = Boolean.valueOf(false);
    }
    if ((((Boolean)localObject1).booleanValue()) || (localObject3 == null) || (Math.abs(System.currentTimeMillis() - ((Long)localObject3).longValue()) <= 1500L))
    {
      FileLog.e("tmessages", "ADD CHANNEL UPDATE TO QUEUE pts = " + paramInt1 + " pts_count = " + paramInt2);
      if (localObject3 == null) {
        this.updatesStartWaitTimeChannels.put(Integer.valueOf(paramInt3), Long.valueOf(System.currentTimeMillis()));
      }
      localObject3 = new UserActionUpdatesPts(null);
      ((UserActionUpdatesPts)localObject3).pts = paramInt1;
      ((UserActionUpdatesPts)localObject3).pts_count = paramInt2;
      ((UserActionUpdatesPts)localObject3).chat_id = paramInt3;
      localObject2 = (ArrayList)this.updatesQueueChannels.get(Integer.valueOf(paramInt3));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayList();
        this.updatesQueueChannels.put(Integer.valueOf(paramInt3), localObject1);
      }
      ((ArrayList)localObject1).add(localObject3);
      return;
    }
    getChannelDifference(paramInt3);
  }
  
  protected void processNewDifferenceParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    FileLog.e("tmessages", "processNewDifferenceParams seq = " + paramInt1 + " pts = " + paramInt2 + " date = " + paramInt3 + " pts_count = " + paramInt4);
    if (paramInt2 != -1)
    {
      if (MessagesStorage.lastPtsValue + paramInt4 != paramInt2) {
        break label158;
      }
      FileLog.e("tmessages", "APPLY PTS");
      MessagesStorage.lastPtsValue = paramInt2;
      MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
    }
    label158:
    Object localObject;
    do
    {
      for (;;)
      {
        if (paramInt1 != -1)
        {
          if (MessagesStorage.lastSeqValue + 1 != paramInt1) {
            break;
          }
          FileLog.e("tmessages", "APPLY SEQ");
          MessagesStorage.lastSeqValue = paramInt1;
          if (paramInt3 != -1) {
            MessagesStorage.lastDateValue = paramInt3;
          }
          MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
        }
        return;
        if (MessagesStorage.lastPtsValue != paramInt2) {
          if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L))
          {
            FileLog.e("tmessages", "ADD UPDATE TO QUEUE pts = " + paramInt2 + " pts_count = " + paramInt4);
            if (this.updatesStartWaitTimePts == 0L) {
              this.updatesStartWaitTimePts = System.currentTimeMillis();
            }
            localObject = new UserActionUpdatesPts(null);
            ((UserActionUpdatesPts)localObject).pts = paramInt2;
            ((UserActionUpdatesPts)localObject).pts_count = paramInt4;
            this.updatesQueuePts.add(localObject);
          }
          else
          {
            getDifference();
          }
        }
      }
    } while (MessagesStorage.lastSeqValue == paramInt1);
    if ((this.gettingDifference) || (this.updatesStartWaitTimeSeq == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500L))
    {
      FileLog.e("tmessages", "ADD UPDATE TO QUEUE seq = " + paramInt1);
      if (this.updatesStartWaitTimeSeq == 0L) {
        this.updatesStartWaitTimeSeq = System.currentTimeMillis();
      }
      localObject = new UserActionUpdatesSeq(null);
      ((UserActionUpdatesSeq)localObject).seq = paramInt1;
      this.updatesQueueSeq.add(localObject);
      return;
    }
    getDifference();
  }
  
  public boolean processUpdateArray(ArrayList<TLRPC.Update> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final ArrayList<TLRPC.Chat> paramArrayList2, final boolean paramBoolean)
  {
    if (paramArrayList.isEmpty())
    {
      if ((paramArrayList1 != null) || (paramArrayList2 != null)) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.putUsers(paramArrayList1, false);
            MessagesController.this.putChats(paramArrayList2, false);
          }
        });
      }
      return true;
    }
    long l3 = System.currentTimeMillis();
    final HashMap localHashMap1 = new HashMap();
    final HashMap localHashMap2 = new HashMap();
    final ArrayList localArrayList6 = new ArrayList();
    ArrayList localArrayList7 = new ArrayList();
    final HashMap localHashMap3 = new HashMap();
    final SparseArray localSparseArray2 = new SparseArray();
    final SparseArray localSparseArray3 = new SparseArray();
    final SparseArray localSparseArray4 = new SparseArray();
    final ArrayList localArrayList2 = new ArrayList();
    final HashMap localHashMap4 = new HashMap();
    final SparseArray localSparseArray1 = new SparseArray();
    boolean bool1 = false;
    final ArrayList localArrayList3 = new ArrayList();
    final ArrayList localArrayList4 = new ArrayList();
    ArrayList localArrayList1 = new ArrayList();
    final ArrayList localArrayList5 = new ArrayList();
    int k = 1;
    if (paramArrayList1 != null)
    {
      localObject2 = new ConcurrentHashMap();
      j = 0;
      for (;;)
      {
        i = k;
        localObject1 = localObject2;
        if (j >= paramArrayList1.size()) {
          break;
        }
        localObject1 = (TLRPC.User)paramArrayList1.get(j);
        ((ConcurrentHashMap)localObject2).put(Integer.valueOf(((TLRPC.User)localObject1).id), localObject1);
        j += 1;
      }
    }
    int i = 0;
    Object localObject1 = this.users;
    Object localObject3;
    if (paramArrayList2 != null)
    {
      localObject3 = new ConcurrentHashMap();
      k = 0;
      for (;;)
      {
        localObject2 = localObject3;
        j = i;
        if (k >= paramArrayList2.size()) {
          break;
        }
        localObject2 = (TLRPC.Chat)paramArrayList2.get(k);
        ((ConcurrentHashMap)localObject3).put(Integer.valueOf(((TLRPC.Chat)localObject2).id), localObject2);
        k += 1;
      }
    }
    final int j = 0;
    Object localObject2 = this.chats;
    int n = j;
    if (paramBoolean) {
      n = 0;
    }
    if ((paramArrayList1 != null) || (paramArrayList2 != null)) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.this.putUsers(paramArrayList1, false);
          MessagesController.this.putChats(paramArrayList2, false);
        }
      });
    }
    j = 0;
    int i1 = 0;
    if (i1 < paramArrayList.size())
    {
      Object localObject4 = (TLRPC.Update)paramArrayList.get(i1);
      FileLog.d("tmessages", "process update " + localObject4);
      int m;
      label670:
      label726:
      int i2;
      label975:
      label1139:
      label1218:
      label1262:
      label1275:
      boolean bool2;
      if (((localObject4 instanceof TLRPC.TL_updateNewMessage)) || ((localObject4 instanceof TLRPC.TL_updateNewChannelMessage)))
      {
        if ((localObject4 instanceof TLRPC.TL_updateNewMessage))
        {
          localObject3 = ((TLRPC.TL_updateNewMessage)localObject4).message;
          paramArrayList1 = null;
          i = 0;
          k = 0;
          if (((TLRPC.Message)localObject3).to_id.channel_id == 0) {
            break label670;
          }
          m = ((TLRPC.Message)localObject3).to_id.channel_id;
        }
        for (;;)
        {
          if (m != 0)
          {
            paramArrayList1 = (TLRPC.Chat)((ConcurrentHashMap)localObject2).get(Integer.valueOf(m));
            paramArrayList2 = paramArrayList1;
            if (paramArrayList1 == null) {
              paramArrayList2 = getChat(Integer.valueOf(m));
            }
            paramArrayList1 = paramArrayList2;
            if (paramArrayList2 == null)
            {
              paramArrayList1 = MessagesStorage.getInstance().getChatSync(m);
              putChat(paramArrayList1, true);
            }
          }
          i = j;
          if (n == 0) {
            break label1139;
          }
          if ((m == 0) || (paramArrayList1 != null)) {
            break label726;
          }
          FileLog.d("tmessages", "not found chat " + m);
          return false;
          paramArrayList1 = ((TLRPC.TL_updateNewChannelMessage)localObject4).message;
          if (BuildVars.DEBUG_VERSION) {
            FileLog.d("tmessages", localObject4 + " channelId = " + paramArrayList1.to_id.channel_id);
          }
          localObject3 = paramArrayList1;
          if (paramArrayList1.out) {
            break;
          }
          localObject3 = paramArrayList1;
          if (paramArrayList1.from_id != UserConfig.getClientUserId()) {
            break;
          }
          paramArrayList1.out = true;
          localObject3 = paramArrayList1;
          break;
          if (((TLRPC.Message)localObject3).to_id.chat_id != 0)
          {
            m = ((TLRPC.Message)localObject3).to_id.chat_id;
          }
          else
          {
            m = i;
            if (((TLRPC.Message)localObject3).to_id.user_id != 0)
            {
              k = ((TLRPC.Message)localObject3).to_id.user_id;
              m = i;
            }
          }
        }
        int i4 = ((TLRPC.Message)localObject3).entities.size();
        i = 0;
        i2 = k;
        k = i;
        for (;;)
        {
          i = j;
          if (k >= i4 + 3) {
            break;
          }
          int i3 = 0;
          m = i3;
          i = i2;
          if (k != 0)
          {
            if (k != 1) {
              break label975;
            }
            i2 = ((TLRPC.Message)localObject3).from_id;
            m = i3;
            i = i2;
            if (((TLRPC.Message)localObject3).post)
            {
              m = 1;
              i = i2;
            }
          }
          i2 = j;
          if (i > 0)
          {
            localObject4 = (TLRPC.User)((ConcurrentHashMap)localObject1).get(Integer.valueOf(i));
            if (localObject4 != null)
            {
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
              if (m == 0)
              {
                paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                if (!((TLRPC.User)localObject4).min) {}
              }
            }
            else
            {
              paramArrayList2 = getUser(Integer.valueOf(i));
            }
            if (paramArrayList2 != null)
            {
              localObject4 = paramArrayList2;
              if (m == 0)
              {
                localObject4 = paramArrayList2;
                if (!paramArrayList2.min) {}
              }
            }
            else
            {
              localObject4 = MessagesStorage.getInstance().getUserSync(i);
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
              if (localObject4 != null)
              {
                paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                if (m == 0)
                {
                  paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                  if (((TLRPC.User)localObject4).min) {
                    paramArrayList2 = null;
                  }
                }
              }
              putUser(paramArrayList2, true);
              localObject4 = paramArrayList2;
            }
            if (localObject4 == null)
            {
              FileLog.d("tmessages", "not found user " + i);
              return false;
              if (k == 2)
              {
                if (((TLRPC.Message)localObject3).fwd_from != null) {}
                for (i = ((TLRPC.Message)localObject3).fwd_from.from_id;; i = 0)
                {
                  m = i3;
                  break;
                }
              }
              paramArrayList2 = (TLRPC.MessageEntity)((TLRPC.Message)localObject3).entities.get(k - 3);
              if ((paramArrayList2 instanceof TLRPC.TL_messageEntityMentionName)) {}
              for (i = ((TLRPC.TL_messageEntityMentionName)paramArrayList2).user_id;; i = 0)
              {
                m = i3;
                break;
              }
            }
            i2 = j;
            if (k == 1)
            {
              i2 = j;
              if (((TLRPC.User)localObject4).status != null)
              {
                i2 = j;
                if (((TLRPC.User)localObject4).status.expires <= 0)
                {
                  this.onlinePrivacy.put(Integer.valueOf(i), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                  i2 = j | 0x4;
                }
              }
            }
          }
          k += 1;
          j = i2;
          i2 = i;
        }
        if ((paramArrayList1 != null) && (paramArrayList1.megagroup)) {
          ((TLRPC.Message)localObject3).flags |= 0x80000000;
        }
        if ((((TLRPC.Message)localObject3).action instanceof TLRPC.TL_messageActionChatDeleteUser))
        {
          paramArrayList2 = (TLRPC.User)((ConcurrentHashMap)localObject1).get(Integer.valueOf(((TLRPC.Message)localObject3).action.user_id));
          if ((paramArrayList2 != null) && (paramArrayList2.bot)) {
            ((TLRPC.Message)localObject3).reply_markup = new TLRPC.TL_replyKeyboardHide();
          }
        }
        else
        {
          localArrayList7.add(localObject3);
          ImageLoader.saveMessageThumbs((TLRPC.Message)localObject3);
          j = UserConfig.getClientUserId();
          if (((TLRPC.Message)localObject3).to_id.chat_id == 0) {
            break label1620;
          }
          ((TLRPC.Message)localObject3).dialog_id = (-((TLRPC.Message)localObject3).to_id.chat_id);
          if (!((TLRPC.Message)localObject3).out) {
            break label1692;
          }
          paramArrayList2 = this.dialogs_read_outbox_max;
          Integer localInteger = (Integer)paramArrayList2.get(Long.valueOf(((TLRPC.Message)localObject3).dialog_id));
          localObject4 = localInteger;
          if (localInteger == null)
          {
            localObject4 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(((TLRPC.Message)localObject3).out, ((TLRPC.Message)localObject3).dialog_id));
            paramArrayList2.put(Long.valueOf(((TLRPC.Message)localObject3).dialog_id), localObject4);
          }
          if ((((Integer)localObject4).intValue() >= ((TLRPC.Message)localObject3).id) || ((paramArrayList1 != null) && (ChatObject.isNotInChat(paramArrayList1))) || ((((TLRPC.Message)localObject3).action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((((TLRPC.Message)localObject3).action instanceof TLRPC.TL_messageActionChannelCreate))) {
            break label1700;
          }
          bool2 = true;
          label1386:
          ((TLRPC.Message)localObject3).unread = bool2;
          if (((TLRPC.Message)localObject3).dialog_id == j)
          {
            ((TLRPC.Message)localObject3).unread = false;
            ((TLRPC.Message)localObject3).media_unread = false;
            ((TLRPC.Message)localObject3).out = true;
          }
          localObject4 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.Message)localObject3).dialog_id)));
          if (((MessageObject)localObject4).type != 11) {
            break label1706;
          }
          j = i | 0x8;
          label1470:
          paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(((TLRPC.Message)localObject3).dialog_id));
          paramArrayList1 = paramArrayList2;
          if (paramArrayList2 == null)
          {
            paramArrayList1 = new ArrayList();
            localHashMap1.put(Long.valueOf(((TLRPC.Message)localObject3).dialog_id), paramArrayList1);
          }
          paramArrayList1.add(localObject4);
          i = j;
          bool2 = bool1;
          if (!((MessageObject)localObject4).isOut())
          {
            i = j;
            bool2 = bool1;
            if (((MessageObject)localObject4).isUnread())
            {
              localArrayList6.add(localObject4);
              bool2 = bool1;
              i = j;
            }
          }
        }
      }
      for (;;)
      {
        i1 += 1;
        j = i;
        bool1 = bool2;
        break;
        if ((((TLRPC.Message)localObject3).from_id != UserConfig.getClientUserId()) || (((TLRPC.Message)localObject3).action.user_id != UserConfig.getClientUserId())) {
          break label1218;
        }
        bool2 = bool1;
        continue;
        label1620:
        if (((TLRPC.Message)localObject3).to_id.channel_id != 0)
        {
          ((TLRPC.Message)localObject3).dialog_id = (-((TLRPC.Message)localObject3).to_id.channel_id);
          break label1262;
        }
        if (((TLRPC.Message)localObject3).to_id.user_id == j) {
          ((TLRPC.Message)localObject3).to_id.user_id = ((TLRPC.Message)localObject3).from_id;
        }
        ((TLRPC.Message)localObject3).dialog_id = ((TLRPC.Message)localObject3).to_id.user_id;
        break label1262;
        label1692:
        paramArrayList2 = this.dialogs_read_inbox_max;
        break label1275;
        label1700:
        bool2 = false;
        break label1386;
        label1706:
        j = i;
        if (((MessageObject)localObject4).type != 10) {
          break label1470;
        }
        j = i | 0x10;
        break label1470;
        if ((localObject4 instanceof TLRPC.TL_updateReadMessagesContents))
        {
          k = 0;
          for (;;)
          {
            i = j;
            bool2 = bool1;
            if (k >= ((TLRPC.Update)localObject4).messages.size()) {
              break;
            }
            localArrayList2.add(Long.valueOf(((Integer)((TLRPC.Update)localObject4).messages.get(k)).intValue()));
            k += 1;
          }
        }
        long l1;
        if (((localObject4 instanceof TLRPC.TL_updateReadHistoryInbox)) || ((localObject4 instanceof TLRPC.TL_updateReadHistoryOutbox)))
        {
          if ((localObject4 instanceof TLRPC.TL_updateReadHistoryInbox))
          {
            paramArrayList1 = ((TLRPC.TL_updateReadHistoryInbox)localObject4).peer;
            if (paramArrayList1.chat_id != 0) {
              localSparseArray3.put(-paramArrayList1.chat_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
            }
            for (l1 = -paramArrayList1.chat_id;; l1 = paramArrayList1.user_id)
            {
              paramArrayList1 = this.dialogs_read_inbox_max;
              localObject3 = (Integer)paramArrayList1.get(Long.valueOf(l1));
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject3;
              if (localObject3 == null) {
                paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localObject4 instanceof TLRPC.TL_updateReadHistoryOutbox, l1));
              }
              paramArrayList1.put(Long.valueOf(l1), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.Update)localObject4).max_id)));
              i = j;
              bool2 = bool1;
              break;
              localSparseArray3.put(paramArrayList1.user_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
            }
          }
          paramArrayList1 = ((TLRPC.TL_updateReadHistoryOutbox)localObject4).peer;
          if (paramArrayList1.chat_id != 0) {
            localSparseArray4.put(-paramArrayList1.chat_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
          }
          for (l1 = -paramArrayList1.chat_id;; l1 = paramArrayList1.user_id)
          {
            paramArrayList1 = this.dialogs_read_outbox_max;
            break;
            localSparseArray4.put(paramArrayList1.user_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
          }
        }
        if ((localObject4 instanceof TLRPC.TL_updateDeleteMessages))
        {
          paramArrayList2 = (ArrayList)localSparseArray1.get(0);
          paramArrayList1 = paramArrayList2;
          if (paramArrayList2 == null)
          {
            paramArrayList1 = new ArrayList();
            localSparseArray1.put(0, paramArrayList1);
          }
          paramArrayList1.addAll(((TLRPC.Update)localObject4).messages);
          i = j;
          bool2 = bool1;
        }
        else
        {
          long l2;
          if (((localObject4 instanceof TLRPC.TL_updateUserTyping)) || ((localObject4 instanceof TLRPC.TL_updateChatUserTyping)))
          {
            i = j;
            bool2 = bool1;
            if (((TLRPC.Update)localObject4).user_id != UserConfig.getClientUserId())
            {
              l2 = -((TLRPC.Update)localObject4).chat_id;
              l1 = l2;
              if (l2 == 0L) {
                l1 = ((TLRPC.Update)localObject4).user_id;
              }
              paramArrayList2 = (ArrayList)this.printingUsers.get(Long.valueOf(l1));
              if ((((TLRPC.Update)localObject4).action instanceof TLRPC.TL_sendMessageCancelAction))
              {
                bool2 = bool1;
                if (paramArrayList2 != null)
                {
                  i = 0;
                  label2212:
                  boolean bool3 = bool1;
                  if (i < paramArrayList2.size())
                  {
                    if (((PrintingUser)paramArrayList2.get(i)).userId != ((TLRPC.Update)localObject4).user_id) {
                      break label2315;
                    }
                    paramArrayList2.remove(i);
                    bool3 = true;
                  }
                  bool2 = bool3;
                  if (paramArrayList2.isEmpty())
                  {
                    this.printingUsers.remove(Long.valueOf(l1));
                    bool2 = bool3;
                  }
                }
              }
              for (;;)
              {
                this.onlinePrivacy.put(Integer.valueOf(((TLRPC.Update)localObject4).user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                i = j;
                break;
                label2315:
                i += 1;
                break label2212;
                paramArrayList1 = paramArrayList2;
                if (paramArrayList2 == null)
                {
                  paramArrayList1 = new ArrayList();
                  this.printingUsers.put(Long.valueOf(l1), paramArrayList1);
                }
                k = 0;
                paramArrayList2 = paramArrayList1.iterator();
                do
                {
                  i = k;
                  bool2 = bool1;
                  if (!paramArrayList2.hasNext()) {
                    break;
                  }
                  localObject3 = (PrintingUser)paramArrayList2.next();
                } while (((PrintingUser)localObject3).userId != ((TLRPC.Update)localObject4).user_id);
                i = 1;
                ((PrintingUser)localObject3).lastTime = l3;
                if (((PrintingUser)localObject3).action.getClass() != ((TLRPC.Update)localObject4).action.getClass()) {
                  bool1 = true;
                }
                ((PrintingUser)localObject3).action = ((TLRPC.Update)localObject4).action;
                bool2 = bool1;
                if (i == 0)
                {
                  paramArrayList2 = new PrintingUser();
                  paramArrayList2.userId = ((TLRPC.Update)localObject4).user_id;
                  paramArrayList2.lastTime = l3;
                  paramArrayList2.action = ((TLRPC.Update)localObject4).action;
                  paramArrayList1.add(paramArrayList2);
                  bool2 = true;
                }
              }
            }
          }
          else if ((localObject4 instanceof TLRPC.TL_updateChatParticipants))
          {
            i = j | 0x20;
            localArrayList3.add(((TLRPC.Update)localObject4).participants);
            bool2 = bool1;
          }
          else if ((localObject4 instanceof TLRPC.TL_updateUserStatus))
          {
            i = j | 0x4;
            localArrayList4.add(localObject4);
            bool2 = bool1;
          }
          else if ((localObject4 instanceof TLRPC.TL_updateUserName))
          {
            i = j | 0x1;
            localArrayList4.add(localObject4);
            bool2 = bool1;
          }
          else if ((localObject4 instanceof TLRPC.TL_updateUserPhoto))
          {
            i = j | 0x2;
            MessagesStorage.getInstance().clearUserPhotos(((TLRPC.Update)localObject4).user_id);
            localArrayList4.add(localObject4);
            bool2 = bool1;
          }
          else if ((localObject4 instanceof TLRPC.TL_updateUserPhone))
          {
            i = j | 0x400;
            localArrayList4.add(localObject4);
            bool2 = bool1;
          }
          else if ((localObject4 instanceof TLRPC.TL_updateContactRegistered))
          {
            i = j;
            bool2 = bool1;
            if (this.enableJoined)
            {
              i = j;
              bool2 = bool1;
              if (((ConcurrentHashMap)localObject1).containsKey(Integer.valueOf(((TLRPC.Update)localObject4).user_id)))
              {
                i = j;
                bool2 = bool1;
                if (!MessagesStorage.getInstance().isDialogHasMessages(((TLRPC.Update)localObject4).user_id))
                {
                  localObject3 = new TLRPC.TL_messageService();
                  ((TLRPC.TL_messageService)localObject3).action = new TLRPC.TL_messageActionUserJoined();
                  i = UserConfig.getNewMessageId();
                  ((TLRPC.TL_messageService)localObject3).id = i;
                  ((TLRPC.TL_messageService)localObject3).local_id = i;
                  UserConfig.saveConfig(false);
                  ((TLRPC.TL_messageService)localObject3).unread = false;
                  ((TLRPC.TL_messageService)localObject3).flags = 256;
                  ((TLRPC.TL_messageService)localObject3).date = ((TLRPC.Update)localObject4).date;
                  ((TLRPC.TL_messageService)localObject3).from_id = ((TLRPC.Update)localObject4).user_id;
                  ((TLRPC.TL_messageService)localObject3).to_id = new TLRPC.TL_peerUser();
                  ((TLRPC.TL_messageService)localObject3).to_id.user_id = UserConfig.getClientUserId();
                  ((TLRPC.TL_messageService)localObject3).dialog_id = ((TLRPC.Update)localObject4).user_id;
                  localArrayList7.add(localObject3);
                  localObject4 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id)));
                  paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id));
                  paramArrayList1 = paramArrayList2;
                  if (paramArrayList2 == null)
                  {
                    paramArrayList1 = new ArrayList();
                    localHashMap1.put(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id), paramArrayList1);
                  }
                  paramArrayList1.add(localObject4);
                  i = j;
                  bool2 = bool1;
                }
              }
            }
          }
          else if ((localObject4 instanceof TLRPC.TL_updateContactLink))
          {
            if ((((TLRPC.Update)localObject4).my_link instanceof TLRPC.TL_contactLinkContact))
            {
              i = localArrayList5.indexOf(Integer.valueOf(-((TLRPC.Update)localObject4).user_id));
              if (i != -1) {
                localArrayList5.remove(i);
              }
              i = j;
              bool2 = bool1;
              if (!localArrayList5.contains(Integer.valueOf(((TLRPC.Update)localObject4).user_id)))
              {
                localArrayList5.add(Integer.valueOf(((TLRPC.Update)localObject4).user_id));
                i = j;
                bool2 = bool1;
              }
            }
            else
            {
              i = localArrayList5.indexOf(Integer.valueOf(((TLRPC.Update)localObject4).user_id));
              if (i != -1) {
                localArrayList5.remove(i);
              }
              i = j;
              bool2 = bool1;
              if (!localArrayList5.contains(Integer.valueOf(((TLRPC.Update)localObject4).user_id)))
              {
                localArrayList5.add(Integer.valueOf(-((TLRPC.Update)localObject4).user_id));
                i = j;
                bool2 = bool1;
              }
            }
          }
          else if ((localObject4 instanceof TLRPC.TL_updateNewAuthorization))
          {
            i = j;
            bool2 = bool1;
            if (!MessagesStorage.getInstance().hasAuthMessage(((TLRPC.Update)localObject4).date))
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.newSessionReceived, new Object[0]);
                }
              });
              localObject3 = new TLRPC.TL_messageService();
              ((TLRPC.TL_messageService)localObject3).action = new TLRPC.TL_messageActionLoginUnknownLocation();
              ((TLRPC.TL_messageService)localObject3).action.title = ((TLRPC.Update)localObject4).device;
              ((TLRPC.TL_messageService)localObject3).action.address = ((TLRPC.Update)localObject4).location;
              i = UserConfig.getNewMessageId();
              ((TLRPC.TL_messageService)localObject3).id = i;
              ((TLRPC.TL_messageService)localObject3).local_id = i;
              UserConfig.saveConfig(false);
              ((TLRPC.TL_messageService)localObject3).unread = true;
              ((TLRPC.TL_messageService)localObject3).flags = 256;
              ((TLRPC.TL_messageService)localObject3).date = ((TLRPC.Update)localObject4).date;
              ((TLRPC.TL_messageService)localObject3).from_id = 777000;
              ((TLRPC.TL_messageService)localObject3).to_id = new TLRPC.TL_peerUser();
              ((TLRPC.TL_messageService)localObject3).to_id.user_id = UserConfig.getClientUserId();
              ((TLRPC.TL_messageService)localObject3).dialog_id = 777000L;
              localArrayList7.add(localObject3);
              localObject4 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id)));
              paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id));
              paramArrayList1 = paramArrayList2;
              if (paramArrayList2 == null)
              {
                paramArrayList1 = new ArrayList();
                localHashMap1.put(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id), paramArrayList1);
              }
              paramArrayList1.add(localObject4);
              localArrayList6.add(localObject4);
              i = j;
              bool2 = bool1;
            }
          }
          else
          {
            i = j;
            bool2 = bool1;
            if (!(localObject4 instanceof TLRPC.TL_updateNewGeoChatMessage)) {
              if ((localObject4 instanceof TLRPC.TL_updateNewEncryptedMessage))
              {
                localObject3 = SecretChatHelper.getInstance().decryptMessage(((TLRPC.TL_updateNewEncryptedMessage)localObject4).message);
                i = j;
                bool2 = bool1;
                if (localObject3 != null)
                {
                  i = j;
                  bool2 = bool1;
                  if (!((ArrayList)localObject3).isEmpty())
                  {
                    l1 = ((TLRPC.TL_updateNewEncryptedMessage)localObject4).message.chat_id << 32;
                    paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(l1));
                    paramArrayList1 = paramArrayList2;
                    if (paramArrayList2 == null)
                    {
                      paramArrayList1 = new ArrayList();
                      localHashMap1.put(Long.valueOf(l1), paramArrayList1);
                    }
                    k = 0;
                    for (;;)
                    {
                      i = j;
                      bool2 = bool1;
                      if (k >= ((ArrayList)localObject3).size()) {
                        break;
                      }
                      paramArrayList2 = (TLRPC.Message)((ArrayList)localObject3).get(k);
                      ImageLoader.saveMessageThumbs(paramArrayList2);
                      localArrayList7.add(paramArrayList2);
                      paramArrayList2 = new MessageObject(paramArrayList2, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(l1)));
                      paramArrayList1.add(paramArrayList2);
                      localArrayList6.add(paramArrayList2);
                      k += 1;
                    }
                  }
                }
              }
              else if ((localObject4 instanceof TLRPC.TL_updateEncryptedChatTyping))
              {
                paramArrayList1 = getEncryptedChatDB(((TLRPC.Update)localObject4).chat_id);
                i = j;
                bool2 = bool1;
                if (paramArrayList1 != null)
                {
                  ((TLRPC.Update)localObject4).user_id = paramArrayList1.user_id;
                  l1 = ((TLRPC.Update)localObject4).chat_id << 32;
                  paramArrayList2 = (ArrayList)this.printingUsers.get(Long.valueOf(l1));
                  paramArrayList1 = paramArrayList2;
                  if (paramArrayList2 == null)
                  {
                    paramArrayList1 = new ArrayList();
                    this.printingUsers.put(Long.valueOf(l1), paramArrayList1);
                  }
                  k = 0;
                  paramArrayList2 = paramArrayList1.iterator();
                  do
                  {
                    i = k;
                    if (!paramArrayList2.hasNext()) {
                      break;
                    }
                    localObject3 = (PrintingUser)paramArrayList2.next();
                  } while (((PrintingUser)localObject3).userId != ((TLRPC.Update)localObject4).user_id);
                  i = 1;
                  ((PrintingUser)localObject3).lastTime = l3;
                  ((PrintingUser)localObject3).action = new TLRPC.TL_sendMessageTypingAction();
                  if (i == 0)
                  {
                    paramArrayList2 = new PrintingUser();
                    paramArrayList2.userId = ((TLRPC.Update)localObject4).user_id;
                    paramArrayList2.lastTime = l3;
                    paramArrayList2.action = new TLRPC.TL_sendMessageTypingAction();
                    paramArrayList1.add(paramArrayList2);
                    bool1 = true;
                  }
                  this.onlinePrivacy.put(Integer.valueOf(((TLRPC.Update)localObject4).user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                  i = j;
                  bool2 = bool1;
                }
              }
              else if ((localObject4 instanceof TLRPC.TL_updateEncryptedMessagesRead))
              {
                localHashMap4.put(Integer.valueOf(((TLRPC.Update)localObject4).chat_id), Integer.valueOf(Math.max(((TLRPC.Update)localObject4).max_date, ((TLRPC.Update)localObject4).date)));
                localArrayList1.add((TLRPC.TL_updateEncryptedMessagesRead)localObject4);
                i = j;
                bool2 = bool1;
              }
              else if ((localObject4 instanceof TLRPC.TL_updateChatParticipantAdd))
              {
                MessagesStorage.getInstance().updateChatInfo(((TLRPC.Update)localObject4).chat_id, ((TLRPC.Update)localObject4).user_id, 0, ((TLRPC.Update)localObject4).inviter_id, ((TLRPC.Update)localObject4).version);
                i = j;
                bool2 = bool1;
              }
              else if ((localObject4 instanceof TLRPC.TL_updateChatParticipantDelete))
              {
                MessagesStorage.getInstance().updateChatInfo(((TLRPC.Update)localObject4).chat_id, ((TLRPC.Update)localObject4).user_id, 1, 0, ((TLRPC.Update)localObject4).version);
                i = j;
                bool2 = bool1;
              }
              else if ((localObject4 instanceof TLRPC.TL_updateDcOptions))
              {
                ConnectionsManager.getInstance().updateDcSettings();
                i = j;
                bool2 = bool1;
              }
              else if ((localObject4 instanceof TLRPC.TL_updateEncryption))
              {
                SecretChatHelper.getInstance().processUpdateEncryption((TLRPC.TL_updateEncryption)localObject4, (ConcurrentHashMap)localObject1);
                i = j;
                bool2 = bool1;
              }
              else
              {
                if ((localObject4 instanceof TLRPC.TL_updateUserBlocked))
                {
                  paramArrayList1 = (TLRPC.TL_updateUserBlocked)localObject4;
                  if (paramArrayList1.blocked)
                  {
                    paramArrayList2 = new ArrayList();
                    paramArrayList2.add(Integer.valueOf(paramArrayList1.user_id));
                    MessagesStorage.getInstance().putBlockedUsers(paramArrayList2, false);
                  }
                  for (;;)
                  {
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            if (MessagesController.105.this.val$finalUpdate.blocked) {
                              if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(MessagesController.105.this.val$finalUpdate.user_id))) {
                                MessagesController.this.blockedUsers.add(Integer.valueOf(MessagesController.105.this.val$finalUpdate.user_id));
                              }
                            }
                            for (;;)
                            {
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                              return;
                              MessagesController.this.blockedUsers.remove(Integer.valueOf(MessagesController.105.this.val$finalUpdate.user_id));
                            }
                          }
                        });
                      }
                    });
                    i = j;
                    bool2 = bool1;
                    break;
                    MessagesStorage.getInstance().deleteBlockedUser(paramArrayList1.user_id);
                  }
                }
                if ((localObject4 instanceof TLRPC.TL_updateNotifySettings))
                {
                  localArrayList4.add(localObject4);
                  i = j;
                  bool2 = bool1;
                }
                else if ((localObject4 instanceof TLRPC.TL_updateServiceNotification))
                {
                  paramArrayList1 = (TLRPC.TL_updateServiceNotification)localObject4;
                  if ((paramArrayList1.popup) && (paramArrayList1.message != null) && (paramArrayList1.message.length() > 0)) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(2), paramArrayList1.message });
                  }
                  localObject3 = new TLRPC.TL_message();
                  i = UserConfig.getNewMessageId();
                  ((TLRPC.TL_message)localObject3).id = i;
                  ((TLRPC.TL_message)localObject3).local_id = i;
                  UserConfig.saveConfig(false);
                  ((TLRPC.TL_message)localObject3).unread = true;
                  ((TLRPC.TL_message)localObject3).flags = 256;
                  ((TLRPC.TL_message)localObject3).date = ConnectionsManager.getInstance().getCurrentTime();
                  ((TLRPC.TL_message)localObject3).from_id = 777000;
                  ((TLRPC.TL_message)localObject3).to_id = new TLRPC.TL_peerUser();
                  ((TLRPC.TL_message)localObject3).to_id.user_id = UserConfig.getClientUserId();
                  ((TLRPC.TL_message)localObject3).dialog_id = 777000L;
                  ((TLRPC.TL_message)localObject3).media = ((TLRPC.Update)localObject4).media;
                  ((TLRPC.TL_message)localObject3).flags |= 0x200;
                  ((TLRPC.TL_message)localObject3).message = paramArrayList1.message;
                  localArrayList7.add(localObject3);
                  localObject4 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_message)localObject3).dialog_id)));
                  paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(((TLRPC.TL_message)localObject3).dialog_id));
                  paramArrayList1 = paramArrayList2;
                  if (paramArrayList2 == null)
                  {
                    paramArrayList1 = new ArrayList();
                    localHashMap1.put(Long.valueOf(((TLRPC.TL_message)localObject3).dialog_id), paramArrayList1);
                  }
                  paramArrayList1.add(localObject4);
                  localArrayList6.add(localObject4);
                  i = j;
                  bool2 = bool1;
                }
                else if ((localObject4 instanceof TLRPC.TL_updatePrivacy))
                {
                  localArrayList4.add(localObject4);
                  i = j;
                  bool2 = bool1;
                }
                else if ((localObject4 instanceof TLRPC.TL_updateWebPage))
                {
                  localHashMap2.put(Long.valueOf(((TLRPC.Update)localObject4).webpage.id), ((TLRPC.Update)localObject4).webpage);
                  i = j;
                  bool2 = bool1;
                }
                else if ((localObject4 instanceof TLRPC.TL_updateChannelTooLong))
                {
                  if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("tmessages", localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                  }
                  paramArrayList2 = (Integer)this.channelsPts.get(Integer.valueOf(((TLRPC.Update)localObject4).channel_id));
                  paramArrayList1 = paramArrayList2;
                  if (paramArrayList2 == null)
                  {
                    localObject3 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(((TLRPC.Update)localObject4).channel_id));
                    if (((Integer)localObject3).intValue() != 0) {
                      break label4822;
                    }
                    paramArrayList2 = (TLRPC.Chat)((ConcurrentHashMap)localObject2).get(Integer.valueOf(((TLRPC.Update)localObject4).channel_id));
                    if (paramArrayList2 != null)
                    {
                      paramArrayList1 = paramArrayList2;
                      if (!paramArrayList2.min) {}
                    }
                    else
                    {
                      paramArrayList1 = getChat(Integer.valueOf(((TLRPC.Update)localObject4).channel_id));
                    }
                    if (paramArrayList1 != null)
                    {
                      paramArrayList2 = paramArrayList1;
                      if (!paramArrayList1.min) {}
                    }
                    else
                    {
                      paramArrayList2 = MessagesStorage.getInstance().getChatSync(((TLRPC.Update)localObject4).channel_id);
                      putChat(paramArrayList2, true);
                    }
                    paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                    if (paramArrayList2 != null)
                    {
                      paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                      if (!paramArrayList2.min) {
                        loadUnknownChannel(paramArrayList2, 0L);
                      }
                    }
                  }
                  for (paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;; paramArrayList1 = (ArrayList<TLRPC.User>)localObject3)
                  {
                    i = j;
                    bool2 = bool1;
                    if (paramArrayList1.intValue() == 0) {
                      break;
                    }
                    if ((((TLRPC.Update)localObject4).flags & 0x1) == 0) {
                      break label4846;
                    }
                    i = j;
                    bool2 = bool1;
                    if (((TLRPC.Update)localObject4).pts <= paramArrayList1.intValue()) {
                      break;
                    }
                    getChannelDifference(((TLRPC.Update)localObject4).channel_id);
                    i = j;
                    bool2 = bool1;
                    break;
                    label4822:
                    this.channelsPts.put(Integer.valueOf(((TLRPC.Update)localObject4).channel_id), localObject3);
                  }
                  label4846:
                  getChannelDifference(((TLRPC.Update)localObject4).channel_id);
                  i = j;
                  bool2 = bool1;
                }
                else
                {
                  if (((localObject4 instanceof TLRPC.TL_updateReadChannelInbox)) || ((localObject4 instanceof TLRPC.TL_updateReadChannelOutbox)))
                  {
                    l1 = ((TLRPC.Update)localObject4).max_id | ((TLRPC.Update)localObject4).channel_id << 32;
                    l2 = -((TLRPC.Update)localObject4).channel_id;
                    if ((localObject4 instanceof TLRPC.TL_updateReadChannelInbox))
                    {
                      paramArrayList1 = this.dialogs_read_inbox_max;
                      localSparseArray3.put(-((TLRPC.Update)localObject4).channel_id, Long.valueOf(l1));
                    }
                    for (;;)
                    {
                      localObject3 = (Integer)paramArrayList1.get(Long.valueOf(l2));
                      paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject3;
                      if (localObject3 == null) {
                        paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localObject4 instanceof TLRPC.TL_updateReadChannelOutbox, l2));
                      }
                      paramArrayList1.put(Long.valueOf(l2), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.Update)localObject4).max_id)));
                      i = j;
                      bool2 = bool1;
                      break;
                      paramArrayList1 = this.dialogs_read_outbox_max;
                      localSparseArray4.put(-((TLRPC.Update)localObject4).channel_id, Long.valueOf(l1));
                    }
                  }
                  if ((localObject4 instanceof TLRPC.TL_updateDeleteChannelMessages))
                  {
                    if (BuildVars.DEBUG_VERSION) {
                      FileLog.d("tmessages", localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                    }
                    paramArrayList2 = (ArrayList)localSparseArray1.get(((TLRPC.Update)localObject4).channel_id);
                    paramArrayList1 = paramArrayList2;
                    if (paramArrayList2 == null)
                    {
                      paramArrayList1 = new ArrayList();
                      localSparseArray1.put(((TLRPC.Update)localObject4).channel_id, paramArrayList1);
                    }
                    paramArrayList1.addAll(((TLRPC.Update)localObject4).messages);
                    i = j;
                    bool2 = bool1;
                  }
                  else if ((localObject4 instanceof TLRPC.TL_updateChannel))
                  {
                    if (BuildVars.DEBUG_VERSION) {
                      FileLog.d("tmessages", localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                    }
                    localArrayList4.add(localObject4);
                    i = j;
                    bool2 = bool1;
                  }
                  else if ((localObject4 instanceof TLRPC.TL_updateChannelMessageViews))
                  {
                    if (BuildVars.DEBUG_VERSION) {
                      FileLog.d("tmessages", localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                    }
                    localObject3 = (TLRPC.TL_updateChannelMessageViews)localObject4;
                    paramArrayList2 = (SparseIntArray)localSparseArray2.get(((TLRPC.Update)localObject4).channel_id);
                    paramArrayList1 = paramArrayList2;
                    if (paramArrayList2 == null)
                    {
                      paramArrayList1 = new SparseIntArray();
                      localSparseArray2.put(((TLRPC.Update)localObject4).channel_id, paramArrayList1);
                    }
                    paramArrayList1.put(((TLRPC.TL_updateChannelMessageViews)localObject3).id, ((TLRPC.Update)localObject4).views);
                    i = j;
                    bool2 = bool1;
                  }
                  else
                  {
                    if ((localObject4 instanceof TLRPC.TL_updateChatParticipantAdmin))
                    {
                      paramArrayList1 = MessagesStorage.getInstance();
                      k = ((TLRPC.Update)localObject4).chat_id;
                      m = ((TLRPC.Update)localObject4).user_id;
                      if (((TLRPC.Update)localObject4).is_admin) {}
                      for (i = 1;; i = 0)
                      {
                        paramArrayList1.updateChatInfo(k, m, 2, i, ((TLRPC.Update)localObject4).version);
                        i = j;
                        bool2 = bool1;
                        break;
                      }
                    }
                    if ((localObject4 instanceof TLRPC.TL_updateChatAdmins))
                    {
                      localArrayList4.add(localObject4);
                      i = j;
                      bool2 = bool1;
                    }
                    else if ((localObject4 instanceof TLRPC.TL_updateStickerSets))
                    {
                      localArrayList4.add(localObject4);
                      i = j;
                      bool2 = bool1;
                    }
                    else if ((localObject4 instanceof TLRPC.TL_updateStickerSetsOrder))
                    {
                      localArrayList4.add(localObject4);
                      i = j;
                      bool2 = bool1;
                    }
                    else if ((localObject4 instanceof TLRPC.TL_updateNewStickerSet))
                    {
                      localArrayList4.add(localObject4);
                      i = j;
                      bool2 = bool1;
                    }
                    else if ((localObject4 instanceof TLRPC.TL_updateDraftMessage))
                    {
                      localArrayList4.add(localObject4);
                      i = j;
                      bool2 = bool1;
                    }
                    else if ((localObject4 instanceof TLRPC.TL_updateSavedGifs))
                    {
                      localArrayList4.add(localObject4);
                      i = j;
                      bool2 = bool1;
                    }
                    else
                    {
                      if (((localObject4 instanceof TLRPC.TL_updateEditChannelMessage)) || ((localObject4 instanceof TLRPC.TL_updateEditMessage)))
                      {
                        k = UserConfig.getClientUserId();
                        if ((localObject4 instanceof TLRPC.TL_updateEditChannelMessage))
                        {
                          localObject4 = ((TLRPC.TL_updateEditChannelMessage)localObject4).message;
                          paramArrayList2 = (TLRPC.Chat)((ConcurrentHashMap)localObject2).get(Integer.valueOf(((TLRPC.Message)localObject4).to_id.channel_id));
                          paramArrayList1 = paramArrayList2;
                          if (paramArrayList2 == null) {
                            paramArrayList1 = getChat(Integer.valueOf(((TLRPC.Message)localObject4).to_id.channel_id));
                          }
                          localObject3 = paramArrayList1;
                          if (paramArrayList1 == null)
                          {
                            localObject3 = MessagesStorage.getInstance().getChatSync(((TLRPC.Message)localObject4).to_id.channel_id);
                            putChat((TLRPC.Chat)localObject3, true);
                          }
                          paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                          if (localObject3 != null)
                          {
                            paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                            if (((TLRPC.Chat)localObject3).megagroup)
                            {
                              ((TLRPC.Message)localObject4).flags |= 0x80000000;
                              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                            }
                          }
                          if ((!paramArrayList2.out) && (paramArrayList2.from_id == UserConfig.getClientUserId())) {
                            paramArrayList2.out = true;
                          }
                          if (!paramBoolean)
                          {
                            m = paramArrayList2.entities.size();
                            i = 0;
                          }
                        }
                        else
                        {
                          for (;;)
                          {
                            if (i >= m) {
                              break label5942;
                            }
                            paramArrayList1 = (TLRPC.MessageEntity)paramArrayList2.entities.get(i);
                            if ((paramArrayList1 instanceof TLRPC.TL_messageEntityMentionName))
                            {
                              i2 = ((TLRPC.TL_messageEntityMentionName)paramArrayList1).user_id;
                              localObject3 = (TLRPC.User)((ConcurrentHashMap)localObject1).get(Integer.valueOf(i2));
                              if (localObject3 != null)
                              {
                                paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                                if (!((TLRPC.User)localObject3).min) {}
                              }
                              else
                              {
                                paramArrayList1 = getUser(Integer.valueOf(i2));
                              }
                              if (paramArrayList1 != null)
                              {
                                localObject3 = paramArrayList1;
                                if (!paramArrayList1.min) {}
                              }
                              else
                              {
                                localObject3 = MessagesStorage.getInstance().getUserSync(i2);
                                paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                                if (localObject3 != null)
                                {
                                  paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                                  if (((TLRPC.User)localObject3).min) {
                                    paramArrayList1 = null;
                                  }
                                }
                                putUser(paramArrayList1, true);
                                localObject3 = paramArrayList1;
                              }
                              if (localObject3 == null)
                              {
                                return false;
                                paramArrayList1 = ((TLRPC.TL_updateEditMessage)localObject4).message;
                                paramArrayList2 = paramArrayList1;
                                if (paramArrayList1.dialog_id != k) {
                                  break;
                                }
                                paramArrayList1.unread = false;
                                paramArrayList1.media_unread = false;
                                paramArrayList1.out = true;
                                paramArrayList2 = paramArrayList1;
                                break;
                              }
                            }
                            i += 1;
                          }
                        }
                        label5942:
                        if (paramArrayList2.to_id.chat_id != 0)
                        {
                          paramArrayList2.dialog_id = (-paramArrayList2.to_id.chat_id);
                          label5965:
                          if (!paramArrayList2.out) {
                            break label6282;
                          }
                          paramArrayList1 = this.dialogs_read_outbox_max;
                          label5977:
                          localObject4 = (Integer)paramArrayList1.get(Long.valueOf(paramArrayList2.dialog_id));
                          localObject3 = localObject4;
                          if (localObject4 == null)
                          {
                            localObject3 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(paramArrayList2.out, paramArrayList2.dialog_id));
                            paramArrayList1.put(Long.valueOf(paramArrayList2.dialog_id), localObject3);
                          }
                          if (((Integer)localObject3).intValue() >= paramArrayList2.id) {
                            break label6290;
                          }
                        }
                        label6282:
                        label6290:
                        for (bool2 = true;; bool2 = false)
                        {
                          paramArrayList2.unread = bool2;
                          if (paramArrayList2.dialog_id == k)
                          {
                            paramArrayList2.out = true;
                            paramArrayList2.unread = false;
                            paramArrayList2.media_unread = false;
                          }
                          if ((paramArrayList2.out) && ((paramArrayList2.message == null) || (paramArrayList2.message.length() == 0)))
                          {
                            paramArrayList2.message = "-1";
                            paramArrayList2.attachPath = "";
                          }
                          ImageLoader.saveMessageThumbs(paramArrayList2);
                          localObject4 = new MessageObject(paramArrayList2, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(paramArrayList2.dialog_id)));
                          localObject3 = (ArrayList)localHashMap3.get(Long.valueOf(paramArrayList2.dialog_id));
                          paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                          if (localObject3 == null)
                          {
                            paramArrayList1 = new ArrayList();
                            localHashMap3.put(Long.valueOf(paramArrayList2.dialog_id), paramArrayList1);
                          }
                          paramArrayList1.add(localObject4);
                          i = j;
                          bool2 = bool1;
                          break;
                          if (paramArrayList2.to_id.channel_id != 0)
                          {
                            paramArrayList2.dialog_id = (-paramArrayList2.to_id.channel_id);
                            break label5965;
                          }
                          if (paramArrayList2.to_id.user_id == UserConfig.getClientUserId()) {
                            paramArrayList2.to_id.user_id = paramArrayList2.from_id;
                          }
                          paramArrayList2.dialog_id = paramArrayList2.to_id.user_id;
                          break label5965;
                          paramArrayList1 = this.dialogs_read_inbox_max;
                          break label5977;
                        }
                      }
                      if ((localObject4 instanceof TLRPC.TL_updateChannelPinnedMessage))
                      {
                        if (BuildVars.DEBUG_VERSION) {
                          FileLog.d("tmessages", localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                        }
                        paramArrayList1 = (TLRPC.TL_updateChannelPinnedMessage)localObject4;
                        MessagesStorage.getInstance().updateChannelPinnedMessage(((TLRPC.Update)localObject4).channel_id, paramArrayList1.id);
                        i = j;
                        bool2 = bool1;
                      }
                      else
                      {
                        i = j;
                        bool2 = bool1;
                        if ((localObject4 instanceof TLRPC.TL_updateReadFeaturedStickers))
                        {
                          localArrayList4.add(localObject4);
                          i = j;
                          bool2 = bool1;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    paramBoolean = bool1;
    if (!localHashMap1.isEmpty())
    {
      paramArrayList = localHashMap1.entrySet().iterator();
      for (;;)
      {
        paramBoolean = bool1;
        if (!paramArrayList.hasNext()) {
          break;
        }
        paramArrayList2 = (Map.Entry)paramArrayList.next();
        paramArrayList1 = (Long)paramArrayList2.getKey();
        paramArrayList2 = (ArrayList)paramArrayList2.getValue();
        if (updatePrintingUsersWithNewMessages(paramArrayList1.longValue(), paramArrayList2)) {
          bool1 = true;
        }
      }
    }
    if (paramBoolean) {
      updatePrintingStrings();
    }
    if (!localArrayList5.isEmpty()) {
      ContactsController.getInstance().processContactsUpdates(localArrayList5, (ConcurrentHashMap)localObject1);
    }
    if (!localArrayList6.isEmpty()) {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.getInstance().processNewMessages(MessagesController.106.this.val$pushMessages, true);
            }
          });
        }
      });
    }
    if (!localArrayList7.isEmpty()) {
      MessagesStorage.getInstance().putMessages(localArrayList7, true, true, false, MediaController.getInstance().getAutodownloadMask());
    }
    if (!localHashMap3.isEmpty())
    {
      paramArrayList = localHashMap3.entrySet().iterator();
      while (paramArrayList.hasNext())
      {
        paramArrayList1 = (Map.Entry)paramArrayList.next();
        paramArrayList2 = new TLRPC.TL_messages_messages();
        localObject1 = (ArrayList)paramArrayList1.getValue();
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          paramArrayList2.messages.add(((MessageObject)((ArrayList)localObject1).get(i)).messageOwner);
          i += 1;
        }
        MessagesStorage.getInstance().putMessages(paramArrayList2, ((Long)paramArrayList1.getKey()).longValue(), -2, 0, false);
      }
    }
    if (localSparseArray2.size() != 0) {
      MessagesStorage.getInstance().putChannelViews(localSparseArray2, true);
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int i = j;
        int k = 0;
        int m = 0;
        int j = i;
        Object localObject3;
        Object localObject4;
        Object localObject1;
        final Object localObject5;
        Object localObject2;
        long l1;
        if (!localArrayList4.isEmpty())
        {
          localObject3 = new ArrayList();
          localObject4 = new ArrayList();
          localObject1 = null;
          k = 0;
          j = i;
          i = m;
          m = k;
          if (m < localArrayList4.size())
          {
            localObject5 = (TLRPC.Update)localArrayList4.get(m);
            localObject2 = new TLRPC.User();
            ((TLRPC.User)localObject2).id = ((TLRPC.Update)localObject5).user_id;
            final Object localObject6 = MessagesController.this.getUser(Integer.valueOf(((TLRPC.Update)localObject5).user_id));
            int n;
            if ((localObject5 instanceof TLRPC.TL_updatePrivacy)) {
              if ((((TLRPC.Update)localObject5).key instanceof TLRPC.TL_privacyKeyStatusTimestamp))
              {
                ContactsController.getInstance().setPrivacyRules(((TLRPC.Update)localObject5).rules, false);
                k = j;
                n = i;
                localObject2 = localObject1;
              }
            }
            for (;;)
            {
              m += 1;
              localObject1 = localObject2;
              i = n;
              j = k;
              break;
              localObject2 = localObject1;
              n = i;
              k = j;
              if ((((TLRPC.Update)localObject5).key instanceof TLRPC.TL_privacyKeyChatInvite))
              {
                ContactsController.getInstance().setPrivacyRules(((TLRPC.Update)localObject5).rules, true);
                localObject2 = localObject1;
                n = i;
                k = j;
                continue;
                if ((localObject5 instanceof TLRPC.TL_updateUserStatus))
                {
                  if ((((TLRPC.Update)localObject5).status instanceof TLRPC.TL_userStatusRecently)) {
                    ((TLRPC.Update)localObject5).status.expires = -100;
                  }
                  for (;;)
                  {
                    if (localObject6 != null)
                    {
                      ((TLRPC.User)localObject6).id = ((TLRPC.Update)localObject5).user_id;
                      ((TLRPC.User)localObject6).status = ((TLRPC.Update)localObject5).status;
                    }
                    ((TLRPC.User)localObject2).status = ((TLRPC.Update)localObject5).status;
                    ((ArrayList)localObject4).add(localObject2);
                    localObject2 = localObject1;
                    n = i;
                    k = j;
                    if (((TLRPC.Update)localObject5).user_id != UserConfig.getClientUserId()) {
                      break;
                    }
                    NotificationsController.getInstance().setLastOnlineFromOtherDevice(((TLRPC.Update)localObject5).status.expires);
                    localObject2 = localObject1;
                    n = i;
                    k = j;
                    break;
                    if ((((TLRPC.Update)localObject5).status instanceof TLRPC.TL_userStatusLastWeek)) {
                      ((TLRPC.Update)localObject5).status.expires = -101;
                    } else if ((((TLRPC.Update)localObject5).status instanceof TLRPC.TL_userStatusLastMonth)) {
                      ((TLRPC.Update)localObject5).status.expires = -102;
                    }
                  }
                }
                if ((localObject5 instanceof TLRPC.TL_updateUserName))
                {
                  if (localObject6 != null)
                  {
                    if (!UserObject.isContact((TLRPC.User)localObject6))
                    {
                      ((TLRPC.User)localObject6).first_name = ((TLRPC.Update)localObject5).first_name;
                      ((TLRPC.User)localObject6).last_name = ((TLRPC.Update)localObject5).last_name;
                    }
                    if ((((TLRPC.User)localObject6).username != null) && (((TLRPC.User)localObject6).username.length() > 0)) {
                      MessagesController.this.usersByUsernames.remove(((TLRPC.User)localObject6).username);
                    }
                    if ((((TLRPC.Update)localObject5).username != null) && (((TLRPC.Update)localObject5).username.length() > 0)) {
                      MessagesController.this.usersByUsernames.put(((TLRPC.Update)localObject5).username, localObject6);
                    }
                    ((TLRPC.User)localObject6).username = ((TLRPC.Update)localObject5).username;
                  }
                  ((TLRPC.User)localObject2).first_name = ((TLRPC.Update)localObject5).first_name;
                  ((TLRPC.User)localObject2).last_name = ((TLRPC.Update)localObject5).last_name;
                  ((TLRPC.User)localObject2).username = ((TLRPC.Update)localObject5).username;
                  ((ArrayList)localObject3).add(localObject2);
                  localObject2 = localObject1;
                  n = i;
                  k = j;
                }
                else if ((localObject5 instanceof TLRPC.TL_updateUserPhoto))
                {
                  if (localObject6 != null) {
                    ((TLRPC.User)localObject6).photo = ((TLRPC.Update)localObject5).photo;
                  }
                  ((TLRPC.User)localObject2).photo = ((TLRPC.Update)localObject5).photo;
                  ((ArrayList)localObject3).add(localObject2);
                  localObject2 = localObject1;
                  n = i;
                  k = j;
                }
                else if ((localObject5 instanceof TLRPC.TL_updateUserPhone))
                {
                  if (localObject6 != null)
                  {
                    ((TLRPC.User)localObject6).phone = ((TLRPC.Update)localObject5).phone;
                    Utilities.phoneBookQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        ContactsController.getInstance().addContactToPhoneBook(localObject6, true);
                      }
                    });
                  }
                  ((TLRPC.User)localObject2).phone = ((TLRPC.Update)localObject5).phone;
                  ((ArrayList)localObject3).add(localObject2);
                  localObject2 = localObject1;
                  n = i;
                  k = j;
                }
                else if ((localObject5 instanceof TLRPC.TL_updateNotifySettings))
                {
                  localObject6 = (TLRPC.TL_updateNotifySettings)localObject5;
                  localObject2 = localObject1;
                  n = i;
                  k = j;
                  if ((((TLRPC.Update)localObject5).notify_settings instanceof TLRPC.TL_peerNotifySettings))
                  {
                    localObject2 = localObject1;
                    n = i;
                    k = j;
                    if ((((TLRPC.TL_updateNotifySettings)localObject6).peer instanceof TLRPC.TL_notifyPeer))
                    {
                      localObject2 = localObject1;
                      if (localObject1 == null) {
                        localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                      }
                      if (((TLRPC.TL_updateNotifySettings)localObject6).peer.peer.user_id != 0)
                      {
                        l1 = ((TLRPC.TL_updateNotifySettings)localObject6).peer.peer.user_id;
                        label786:
                        localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(l1));
                        if (localObject1 != null) {
                          ((TLRPC.TL_dialog)localObject1).notify_settings = ((TLRPC.Update)localObject5).notify_settings;
                        }
                        ((SharedPreferences.Editor)localObject2).putBoolean("silent_" + l1, ((TLRPC.Update)localObject5).notify_settings.silent);
                        if (((TLRPC.Update)localObject5).notify_settings.mute_until <= ConnectionsManager.getInstance().getCurrentTime()) {
                          break label1133;
                        }
                        n = 0;
                        if (((TLRPC.Update)localObject5).notify_settings.mute_until <= ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
                          break label1032;
                        }
                        ((SharedPreferences.Editor)localObject2).putInt("notify2_" + l1, 2);
                        k = n;
                        if (localObject1 != null)
                        {
                          ((TLRPC.TL_dialog)localObject1).notify_settings.mute_until = Integer.MAX_VALUE;
                          k = n;
                        }
                      }
                      for (;;)
                      {
                        MessagesStorage.getInstance().setDialogFlags(l1, k << 32 | 1L);
                        NotificationsController.getInstance().removeNotificationsForDialog(l1);
                        n = i;
                        k = j;
                        break;
                        if (((TLRPC.TL_updateNotifySettings)localObject6).peer.peer.chat_id != 0)
                        {
                          l1 = -((TLRPC.TL_updateNotifySettings)localObject6).peer.peer.chat_id;
                          break label786;
                        }
                        l1 = -((TLRPC.TL_updateNotifySettings)localObject6).peer.peer.channel_id;
                        break label786;
                        label1032:
                        n = ((TLRPC.Update)localObject5).notify_settings.mute_until;
                        ((SharedPreferences.Editor)localObject2).putInt("notify2_" + l1, 3);
                        ((SharedPreferences.Editor)localObject2).putInt("notifyuntil_" + l1, ((TLRPC.Update)localObject5).notify_settings.mute_until);
                        k = n;
                        if (localObject1 != null)
                        {
                          ((TLRPC.TL_dialog)localObject1).notify_settings.mute_until = n;
                          k = n;
                        }
                      }
                      label1133:
                      if (localObject1 != null) {
                        ((TLRPC.TL_dialog)localObject1).notify_settings.mute_until = 0;
                      }
                      ((SharedPreferences.Editor)localObject2).remove("notify2_" + l1);
                      MessagesStorage.getInstance().setDialogFlags(l1, 0L);
                      n = i;
                      k = j;
                    }
                  }
                }
                else
                {
                  if ((localObject5 instanceof TLRPC.TL_updateChannel))
                  {
                    localObject2 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(-((TLRPC.Update)localObject5).channel_id));
                    localObject6 = MessagesController.this.getChat(Integer.valueOf(((TLRPC.Update)localObject5).channel_id));
                    if (localObject6 != null)
                    {
                      if ((localObject2 != null) || (!(localObject6 instanceof TLRPC.TL_channel)) || (((TLRPC.Chat)localObject6).left)) {
                        break label1315;
                      }
                      Utilities.stageQueue.postRunnable(new Runnable()
                      {
                        public void run()
                        {
                          MessagesController.this.getChannelDifference(localObject5.channel_id, 1, 0L);
                        }
                      });
                    }
                    for (;;)
                    {
                      k = j | 0x2000;
                      MessagesController.this.loadFullChat(((TLRPC.Update)localObject5).channel_id, 0, true);
                      localObject2 = localObject1;
                      n = i;
                      break;
                      label1315:
                      if ((((TLRPC.Chat)localObject6).left) && (localObject2 != null)) {
                        MessagesController.this.deleteDialog(((TLRPC.TL_dialog)localObject2).id, 0);
                      }
                    }
                  }
                  if ((localObject5 instanceof TLRPC.TL_updateChatAdmins))
                  {
                    k = j | 0x4000;
                    localObject2 = localObject1;
                    n = i;
                  }
                  else
                  {
                    if ((localObject5 instanceof TLRPC.TL_updateStickerSets))
                    {
                      if (((TLRPC.Update)localObject5).masks) {}
                      for (k = 1;; k = 0)
                      {
                        StickersQuery.loadStickers(k, false, true);
                        localObject2 = localObject1;
                        n = i;
                        k = j;
                        break;
                      }
                    }
                    if ((localObject5 instanceof TLRPC.TL_updateStickerSetsOrder))
                    {
                      if (((TLRPC.Update)localObject5).masks) {}
                      for (k = 1;; k = 0)
                      {
                        StickersQuery.reorderStickers(k, ((TLRPC.Update)localObject5).order);
                        localObject2 = localObject1;
                        n = i;
                        k = j;
                        break;
                      }
                    }
                    if ((localObject5 instanceof TLRPC.TL_updateNewStickerSet))
                    {
                      StickersQuery.addNewStickerSet(((TLRPC.Update)localObject5).stickerset);
                      localObject2 = localObject1;
                      n = i;
                      k = j;
                    }
                    else if ((localObject5 instanceof TLRPC.TL_updateSavedGifs))
                    {
                      ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0L).commit();
                      localObject2 = localObject1;
                      n = i;
                      k = j;
                    }
                    else if ((localObject5 instanceof TLRPC.TL_updateRecentStickers))
                    {
                      ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastStickersLoadTime", 0L).commit();
                      localObject2 = localObject1;
                      n = i;
                      k = j;
                    }
                    else
                    {
                      if ((localObject5 instanceof TLRPC.TL_updateDraftMessage))
                      {
                        n = 1;
                        localObject2 = ((TLRPC.TL_updateDraftMessage)localObject5).peer;
                        if (((TLRPC.Peer)localObject2).user_id != 0) {
                          l1 = ((TLRPC.Peer)localObject2).user_id;
                        }
                        for (;;)
                        {
                          DraftQuery.saveDraft(l1, ((TLRPC.Update)localObject5).draft, null, true);
                          localObject2 = localObject1;
                          k = j;
                          break;
                          if (((TLRPC.Peer)localObject2).channel_id != 0) {
                            l1 = -((TLRPC.Peer)localObject2).channel_id;
                          } else {
                            l1 = -((TLRPC.Peer)localObject2).chat_id;
                          }
                        }
                      }
                      localObject2 = localObject1;
                      n = i;
                      k = j;
                      if ((localObject5 instanceof TLRPC.TL_updateReadFeaturedStickers))
                      {
                        StickersQuery.markFaturedStickersAsRead(false);
                        localObject2 = localObject1;
                        n = i;
                        k = j;
                      }
                    }
                  }
                }
              }
            }
          }
          if (localObject1 != null)
          {
            ((SharedPreferences.Editor)localObject1).commit();
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
          }
          MessagesStorage.getInstance().updateUsers((ArrayList)localObject4, true, true, true);
          MessagesStorage.getInstance().updateUsers((ArrayList)localObject3, false, true, true);
          k = i;
        }
        if (!localHashMap2.isEmpty())
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceivedWebpagesInUpdates, new Object[] { localHashMap2 });
          localObject1 = localHashMap2.entrySet().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject3 = (Map.Entry)((Iterator)localObject1).next();
            localObject2 = (ArrayList)MessagesController.this.reloadingWebpagesPending.remove(((Map.Entry)localObject3).getKey());
            if (localObject2 != null)
            {
              localObject3 = (TLRPC.WebPage)((Map.Entry)localObject3).getValue();
              localObject4 = new ArrayList();
              l1 = 0L;
              if (((localObject3 instanceof TLRPC.TL_webPage)) || ((localObject3 instanceof TLRPC.TL_webPageEmpty)))
              {
                i = 0;
                for (;;)
                {
                  l2 = l1;
                  if (i >= ((ArrayList)localObject2).size()) {
                    break;
                  }
                  ((MessageObject)((ArrayList)localObject2).get(i)).messageOwner.media.webpage = ((TLRPC.WebPage)localObject3);
                  if (i == 0)
                  {
                    l1 = ((MessageObject)((ArrayList)localObject2).get(i)).getDialogId();
                    ImageLoader.saveMessageThumbs(((MessageObject)((ArrayList)localObject2).get(i)).messageOwner);
                  }
                  ((ArrayList)localObject4).add(((MessageObject)((ArrayList)localObject2).get(i)).messageOwner);
                  i += 1;
                }
              }
              MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(((TLRPC.WebPage)localObject3).id), localObject2);
              long l2 = l1;
              if (!((ArrayList)localObject4).isEmpty())
              {
                MessagesStorage.getInstance().putMessages((ArrayList)localObject4, true, true, false, MediaController.getInstance().getAutodownloadMask());
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(l2), localObject2 });
              }
            }
          }
        }
        i = 0;
        if (!localHashMap1.isEmpty())
        {
          localObject1 = localHashMap1.entrySet().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject3 = (Map.Entry)((Iterator)localObject1).next();
            localObject2 = (Long)((Map.Entry)localObject3).getKey();
            localObject3 = (ArrayList)((Map.Entry)localObject3).getValue();
            MessagesController.this.updateInterfaceWithMessages(((Long)localObject2).longValue(), (ArrayList)localObject3);
          }
          i = 1;
          k = i;
          if (localHashMap3.isEmpty()) {
            break label2414;
          }
          localObject1 = localHashMap3.entrySet().iterator();
          label2176:
          k = i;
          if (!((Iterator)localObject1).hasNext()) {
            break label2414;
          }
          localObject3 = (Map.Entry)((Iterator)localObject1).next();
          localObject2 = (Long)((Map.Entry)localObject3).getKey();
          localObject3 = (ArrayList)((Map.Entry)localObject3).getValue();
          localObject4 = (MessageObject)MessagesController.this.dialogMessage.get(localObject2);
          m = i;
          if (localObject4 != null) {
            k = 0;
          }
        }
        for (;;)
        {
          m = i;
          if (k < ((ArrayList)localObject3).size())
          {
            localObject5 = (MessageObject)((ArrayList)localObject3).get(k);
            if (((MessageObject)localObject4).getId() == ((MessageObject)localObject5).getId())
            {
              MessagesController.this.dialogMessage.put(localObject2, localObject5);
              if ((((MessageObject)localObject5).messageOwner.to_id != null) && (((MessageObject)localObject5).messageOwner.to_id.channel_id == 0)) {
                MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject5).getId()), localObject5);
              }
              m = 1;
            }
          }
          else
          {
            MessagesQuery.loadReplyMessagesForMessages((ArrayList)localObject3, ((Long)localObject2).longValue());
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { localObject2, localObject3 });
            i = m;
            break label2176;
            if (k == 0) {
              break;
            }
            MessagesController.this.sortDialogs(null);
            i = 1;
            break;
          }
          k += 1;
        }
        label2414:
        if (k != 0) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        i = j;
        if (paramBoolean) {
          i = j | 0x40;
        }
        j = i;
        if (!localArrayList5.isEmpty()) {
          j = i | 0x1 | 0x80;
        }
        if (!localArrayList3.isEmpty())
        {
          i = 0;
          while (i < localArrayList3.size())
          {
            localObject1 = (TLRPC.ChatParticipants)localArrayList3.get(i);
            MessagesStorage.getInstance().updateChatParticipants((TLRPC.ChatParticipants)localObject1);
            i += 1;
          }
        }
        if (localSparseArray2.size() != 0) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedMessagesViews, new Object[] { localSparseArray2 });
        }
        if (j != 0) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(j) });
        }
      }
    });
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i = 0;
            int j = 0;
            int k;
            Object localObject1;
            if ((MessagesController.108.this.val$markAsReadMessagesInbox.size() != 0) || (MessagesController.108.this.val$markAsReadMessagesOutbox.size() != 0))
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesRead, new Object[] { MessagesController.108.this.val$markAsReadMessagesInbox, MessagesController.108.this.val$markAsReadMessagesOutbox });
              NotificationsController.getInstance().processReadMessages(MessagesController.108.this.val$markAsReadMessagesInbox, 0L, 0, 0, false);
              k = 0;
              i = j;
              j = k;
              int m;
              while (j < MessagesController.108.this.val$markAsReadMessagesInbox.size())
              {
                k = MessagesController.108.this.val$markAsReadMessagesInbox.keyAt(j);
                m = (int)((Long)MessagesController.108.this.val$markAsReadMessagesInbox.get(k)).longValue();
                localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(k));
                k = i;
                if (localObject1 != null)
                {
                  k = i;
                  if (((TLRPC.TL_dialog)localObject1).top_message <= m)
                  {
                    localObject1 = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                    k = i;
                    if (localObject1 != null)
                    {
                      k = i;
                      if (!((MessageObject)localObject1).isOut())
                      {
                        ((MessageObject)localObject1).setIsRead();
                        k = i | 0x100;
                      }
                    }
                  }
                }
                j += 1;
                i = k;
              }
              k = 0;
              for (j = i;; j = i)
              {
                i = j;
                if (k >= MessagesController.108.this.val$markAsReadMessagesOutbox.size()) {
                  break;
                }
                i = MessagesController.108.this.val$markAsReadMessagesOutbox.keyAt(k);
                m = (int)((Long)MessagesController.108.this.val$markAsReadMessagesOutbox.get(i)).longValue();
                localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(i));
                i = j;
                if (localObject1 != null)
                {
                  i = j;
                  if (((TLRPC.TL_dialog)localObject1).top_message <= m)
                  {
                    localObject1 = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                    i = j;
                    if (localObject1 != null)
                    {
                      i = j;
                      if (((MessageObject)localObject1).isOut())
                      {
                        ((MessageObject)localObject1).setIsRead();
                        i = j | 0x100;
                      }
                    }
                  }
                }
                k += 1;
              }
            }
            j = i;
            Object localObject2;
            if (!MessagesController.108.this.val$markAsReadEncrypted.isEmpty())
            {
              localObject1 = MessagesController.108.this.val$markAsReadEncrypted.entrySet().iterator();
              for (;;)
              {
                j = i;
                if (!((Iterator)localObject1).hasNext()) {
                  break;
                }
                localObject2 = (Map.Entry)((Iterator)localObject1).next();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadEncrypted, new Object[] { ((Map.Entry)localObject2).getKey(), ((Map.Entry)localObject2).getValue() });
                long l = ((Integer)((Map.Entry)localObject2).getKey()).intValue() << 32;
                if ((TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(l)) != null)
                {
                  MessageObject localMessageObject = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(l));
                  if ((localMessageObject != null) && (localMessageObject.messageOwner.date <= ((Integer)((Map.Entry)localObject2).getValue()).intValue()))
                  {
                    localMessageObject.setIsRead();
                    i |= 0x100;
                  }
                }
              }
            }
            if (!MessagesController.108.this.val$markAsReadMessages.isEmpty()) {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadContent, new Object[] { MessagesController.108.this.val$markAsReadMessages });
            }
            if (MessagesController.108.this.val$deletedMessages.size() != 0)
            {
              i = 0;
              if (i < MessagesController.108.this.val$deletedMessages.size())
              {
                k = MessagesController.108.this.val$deletedMessages.keyAt(i);
                localObject1 = (ArrayList)MessagesController.108.this.val$deletedMessages.get(k);
                if (localObject1 == null) {}
                label860:
                for (;;)
                {
                  i += 1;
                  break;
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, new Object[] { localObject1, Integer.valueOf(k) });
                  if (k == 0)
                  {
                    k = 0;
                    while (k < ((ArrayList)localObject1).size())
                    {
                      localObject2 = (Integer)((ArrayList)localObject1).get(k);
                      localObject2 = (MessageObject)MessagesController.this.dialogMessagesByIds.get(localObject2);
                      if (localObject2 != null) {
                        ((MessageObject)localObject2).deleted = true;
                      }
                      k += 1;
                    }
                  }
                  else
                  {
                    localObject2 = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(-k));
                    if (localObject2 != null)
                    {
                      k = 0;
                      for (;;)
                      {
                        if (k >= ((ArrayList)localObject1).size()) {
                          break label860;
                        }
                        if (((MessageObject)localObject2).getId() == ((Integer)((ArrayList)localObject1).get(k)).intValue())
                        {
                          ((MessageObject)localObject2).deleted = true;
                          break;
                        }
                        k += 1;
                      }
                    }
                  }
                }
              }
              NotificationsController.getInstance().removeDeletedMessagesFromNotifications(MessagesController.108.this.val$deletedMessages);
            }
            if (j != 0) {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(j) });
            }
          }
        });
      }
    });
    if (!localHashMap2.isEmpty()) {
      MessagesStorage.getInstance().putWebPages(localHashMap2);
    }
    if ((localSparseArray3.size() != 0) || (localSparseArray4.size() != 0) || (!localHashMap4.isEmpty()))
    {
      if (localSparseArray3.size() != 0) {
        MessagesStorage.getInstance().updateDialogsWithReadMessages(localSparseArray3, localSparseArray4, true);
      }
      MessagesStorage.getInstance().markMessagesAsRead(localSparseArray3, localSparseArray4, localHashMap4, true);
    }
    if (!localArrayList2.isEmpty()) {
      MessagesStorage.getInstance().markMessagesContentAsRead(localArrayList2);
    }
    if (localSparseArray1.size() != 0)
    {
      i = 0;
      while (i < localSparseArray1.size())
      {
        j = localSparseArray1.keyAt(i);
        paramArrayList = (ArrayList)localSparseArray1.get(j);
        MessagesStorage.getInstance().markMessagesAsDeleted(paramArrayList, true, j);
        MessagesStorage.getInstance().updateDialogsWithDeletedMessages(paramArrayList, true, j);
        i += 1;
      }
    }
    if (!localArrayList1.isEmpty())
    {
      i = 0;
      while (i < localArrayList1.size())
      {
        paramArrayList = (TLRPC.TL_updateEncryptedMessagesRead)localArrayList1.get(i);
        MessagesStorage.getInstance().createTaskForSecretChat(paramArrayList.chat_id, paramArrayList.max_date, paramArrayList.date, 1, null);
        i += 1;
      }
    }
    return true;
  }
  
  public void processUpdates(final TLRPC.Updates paramUpdates, boolean paramBoolean)
  {
    Object localObject4 = null;
    Object localObject6 = null;
    int m = 0;
    int i3 = 0;
    final int k = 0;
    int i2 = 0;
    int n = 0;
    int i1 = 0;
    Object localObject1;
    int i;
    final Object localObject2;
    int j;
    if ((paramUpdates instanceof TLRPC.TL_updateShort))
    {
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(paramUpdates.update);
      processUpdateArray((ArrayList)localObject1, null, null, false);
      n = i1;
      m = i2;
      i = i3;
      localObject2 = localObject6;
      SecretChatHelper.getInstance().processPendingEncMessages();
      if (paramBoolean) {
        break label4384;
      }
      paramUpdates = new ArrayList(this.updatesQueueChannels.keySet());
      j = 0;
      label103:
      if (j >= paramUpdates.size()) {
        break label4376;
      }
      localObject1 = (Integer)paramUpdates.get(j);
      if ((localObject2 == null) || (!((ArrayList)localObject2).contains(localObject1))) {
        break label4363;
      }
      getChannelDifference(((Integer)localObject1).intValue());
    }
    for (;;)
    {
      j += 1;
      break label103;
      label183:
      Object localObject5;
      Object localObject3;
      if (((paramUpdates instanceof TLRPC.TL_updateShortChatMessage)) || ((paramUpdates instanceof TLRPC.TL_updateShortMessage)))
      {
        TLRPC.Chat localChat;
        if ((paramUpdates instanceof TLRPC.TL_updateShortChatMessage))
        {
          k = paramUpdates.from_id;
          localObject1 = getUser(Integer.valueOf(k));
          localObject4 = null;
          Object localObject7 = null;
          localObject5 = null;
          localChat = null;
          if (localObject1 != null)
          {
            localObject3 = localObject1;
            if (!((TLRPC.User)localObject1).min) {}
          }
          else
          {
            localObject2 = MessagesStorage.getInstance().getUserSync(k);
            localObject1 = localObject2;
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (((TLRPC.User)localObject2).min) {
                localObject1 = null;
              }
            }
            putUser((TLRPC.User)localObject1, true);
            localObject3 = localObject1;
          }
          i = 0;
          j = 0;
          localObject2 = localChat;
          if (paramUpdates.fwd_from != null)
          {
            i = j;
            localObject1 = localObject7;
            if (paramUpdates.fwd_from.from_id != 0)
            {
              localObject2 = getUser(Integer.valueOf(paramUpdates.fwd_from.from_id));
              localObject1 = localObject2;
              if (localObject2 == null)
              {
                localObject1 = MessagesStorage.getInstance().getUserSync(paramUpdates.fwd_from.from_id);
                putUser((TLRPC.User)localObject1, true);
              }
              i = 1;
            }
            localObject2 = localChat;
            localObject4 = localObject1;
            if (paramUpdates.fwd_from.channel_id != 0)
            {
              localObject4 = getChat(Integer.valueOf(paramUpdates.fwd_from.channel_id));
              localObject2 = localObject4;
              if (localObject4 == null)
              {
                localObject2 = MessagesStorage.getInstance().getChatSync(paramUpdates.fwd_from.channel_id);
                putChat((TLRPC.Chat)localObject2, true);
              }
              i = 1;
              localObject4 = localObject1;
            }
          }
          j = 0;
          localObject1 = localObject5;
          if (paramUpdates.via_bot_id != 0)
          {
            localObject5 = getUser(Integer.valueOf(paramUpdates.via_bot_id));
            localObject1 = localObject5;
            if (localObject5 == null)
            {
              localObject1 = MessagesStorage.getInstance().getUserSync(paramUpdates.via_bot_id);
              putUser((TLRPC.User)localObject1, true);
            }
            j = 1;
          }
          if (!(paramUpdates instanceof TLRPC.TL_updateShortMessage)) {
            break label756;
          }
          if ((localObject3 != null) && ((i == 0) || (localObject4 != null) || (localObject2 != null)) && ((j == 0) || (localObject1 != null))) {
            break label751;
          }
          i = 1;
          label520:
          m = i;
          if (i == 0)
          {
            m = i;
            if (!paramUpdates.entities.isEmpty()) {
              j = 0;
            }
          }
        }
        for (;;)
        {
          m = i;
          if (j < paramUpdates.entities.size())
          {
            localObject1 = (TLRPC.MessageEntity)paramUpdates.entities.get(j);
            if (!(localObject1 instanceof TLRPC.TL_messageEntityMentionName)) {
              break label849;
            }
            m = ((TLRPC.TL_messageEntityMentionName)localObject1).user_id;
            localObject1 = getUser(Integer.valueOf(m));
            if ((localObject1 != null) && (!((TLRPC.User)localObject1).min)) {
              break label849;
            }
            localObject2 = MessagesStorage.getInstance().getUserSync(m);
            localObject1 = localObject2;
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (((TLRPC.User)localObject2).min) {
                localObject1 = null;
              }
            }
            if (localObject1 == null) {
              m = 1;
            }
          }
          else
          {
            j = n;
            if (localObject3 != null)
            {
              j = n;
              if (((TLRPC.User)localObject3).status != null)
              {
                j = n;
                if (((TLRPC.User)localObject3).status.expires <= 0)
                {
                  this.onlinePrivacy.put(Integer.valueOf(((TLRPC.User)localObject3).id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                  j = 1;
                }
              }
            }
            if (m == 0) {
              break label858;
            }
            i = 1;
            localObject2 = localObject6;
            m = i2;
            n = j;
            break;
            k = paramUpdates.user_id;
            break label183;
            label751:
            i = 0;
            break label520;
            label756:
            localChat = getChat(Integer.valueOf(paramUpdates.chat_id));
            localObject5 = localChat;
            if (localChat == null)
            {
              localObject5 = MessagesStorage.getInstance().getChatSync(paramUpdates.chat_id);
              putChat((TLRPC.Chat)localObject5, true);
            }
            if ((localObject5 == null) || (localObject3 == null) || ((i != 0) && (localObject4 == null) && (localObject2 == null)) || ((j != 0) && (localObject1 == null))) {}
            for (i = 1;; i = 0) {
              break;
            }
          }
          putUser((TLRPC.User)localObject3, true);
          label849:
          j += 1;
        }
        label858:
        if (MessagesStorage.lastPtsValue + paramUpdates.pts_count == paramUpdates.pts)
        {
          localObject4 = new TLRPC.TL_message();
          ((TLRPC.TL_message)localObject4).id = paramUpdates.id;
          i = UserConfig.getClientUserId();
          label915:
          label945:
          label1074:
          final boolean bool;
          if ((paramUpdates instanceof TLRPC.TL_updateShortMessage)) {
            if (paramUpdates.out)
            {
              ((TLRPC.TL_message)localObject4).from_id = i;
              ((TLRPC.TL_message)localObject4).to_id = new TLRPC.TL_peerUser();
              ((TLRPC.TL_message)localObject4).to_id.user_id = k;
              ((TLRPC.TL_message)localObject4).dialog_id = k;
              ((TLRPC.TL_message)localObject4).fwd_from = paramUpdates.fwd_from;
              ((TLRPC.TL_message)localObject4).silent = paramUpdates.silent;
              ((TLRPC.TL_message)localObject4).out = paramUpdates.out;
              ((TLRPC.TL_message)localObject4).mentioned = paramUpdates.mentioned;
              ((TLRPC.TL_message)localObject4).media_unread = paramUpdates.media_unread;
              ((TLRPC.TL_message)localObject4).entities = paramUpdates.entities;
              ((TLRPC.TL_message)localObject4).message = paramUpdates.message;
              ((TLRPC.TL_message)localObject4).date = paramUpdates.date;
              ((TLRPC.TL_message)localObject4).via_bot_id = paramUpdates.via_bot_id;
              ((TLRPC.TL_message)localObject4).flags = (paramUpdates.flags | 0x100);
              ((TLRPC.TL_message)localObject4).reply_to_msg_id = paramUpdates.reply_to_msg_id;
              ((TLRPC.TL_message)localObject4).media = new TLRPC.TL_messageMediaEmpty();
              if (!((TLRPC.TL_message)localObject4).out) {
                break label1427;
              }
              localObject1 = this.dialogs_read_outbox_max;
              localObject3 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(((TLRPC.TL_message)localObject4).dialog_id));
              localObject2 = localObject3;
              if (localObject3 == null)
              {
                localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(((TLRPC.TL_message)localObject4).out, ((TLRPC.TL_message)localObject4).dialog_id));
                ((ConcurrentHashMap)localObject1).put(Long.valueOf(((TLRPC.TL_message)localObject4).dialog_id), localObject2);
              }
              if (((Integer)localObject2).intValue() >= ((TLRPC.TL_message)localObject4).id) {
                break label1436;
              }
              bool = true;
              label1154:
              ((TLRPC.TL_message)localObject4).unread = bool;
              if (((TLRPC.TL_message)localObject4).dialog_id == i)
              {
                ((TLRPC.TL_message)localObject4).unread = false;
                ((TLRPC.TL_message)localObject4).media_unread = false;
                ((TLRPC.TL_message)localObject4).out = true;
              }
              MessagesStorage.lastPtsValue = paramUpdates.pts;
              localObject1 = new MessageObject((TLRPC.Message)localObject4, null, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_message)localObject4).dialog_id)));
              localObject2 = new ArrayList();
              ((ArrayList)localObject2).add(localObject1);
              localObject3 = new ArrayList();
              ((ArrayList)localObject3).add(localObject4);
              if (!(paramUpdates instanceof TLRPC.TL_updateShortMessage)) {
                break label1448;
              }
              if ((paramUpdates.out) || (!updatePrintingUsersWithNewMessages(paramUpdates.user_id, (ArrayList)localObject2))) {
                break label1442;
              }
              bool = true;
              label1289:
              if (bool) {
                updatePrintingStrings();
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (bool) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
                  }
                  MessagesController.this.updateInterfaceWithMessages(k, localObject2);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
              });
            }
          }
          for (;;)
          {
            if (!((MessageObject)localObject1).isOut()) {
              MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
              {
                public void run()
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationsController.getInstance().processNewMessages(MessagesController.99.this.val$objArr, true);
                    }
                  });
                }
              });
            }
            MessagesStorage.getInstance().putMessages((ArrayList)localObject3, false, true, false, 0);
            localObject2 = localObject6;
            i = i3;
            m = i2;
            n = j;
            break;
            ((TLRPC.TL_message)localObject4).from_id = k;
            break label915;
            ((TLRPC.TL_message)localObject4).from_id = k;
            ((TLRPC.TL_message)localObject4).to_id = new TLRPC.TL_peerChat();
            ((TLRPC.TL_message)localObject4).to_id.chat_id = paramUpdates.chat_id;
            ((TLRPC.TL_message)localObject4).dialog_id = (-paramUpdates.chat_id);
            break label945;
            label1427:
            localObject1 = this.dialogs_read_inbox_max;
            break label1074;
            label1436:
            bool = false;
            break label1154;
            label1442:
            bool = false;
            break label1289;
            label1448:
            bool = updatePrintingUsersWithNewMessages(-paramUpdates.chat_id, (ArrayList)localObject2);
            if (bool) {
              updatePrintingStrings();
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (bool) {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
                }
                MessagesController.this.updateInterfaceWithMessages(-paramUpdates.chat_id, localObject2);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              }
            });
          }
        }
        localObject2 = localObject6;
        i = i3;
        m = i2;
        n = j;
        if (MessagesStorage.lastPtsValue == paramUpdates.pts) {
          break;
        }
        FileLog.e("tmessages", "need get diff short message, pts: " + MessagesStorage.lastPtsValue + " " + paramUpdates.pts + " count = " + paramUpdates.pts_count);
        if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L))
        {
          if (this.updatesStartWaitTimePts == 0L) {
            this.updatesStartWaitTimePts = System.currentTimeMillis();
          }
          FileLog.e("tmessages", "add to queue");
          this.updatesQueuePts.add(paramUpdates);
          localObject2 = localObject6;
          i = i3;
          m = i2;
          n = j;
          break;
        }
        i = 1;
        localObject2 = localObject6;
        m = i2;
        n = j;
        break;
      }
      if (((paramUpdates instanceof TLRPC.TL_updatesCombined)) || ((paramUpdates instanceof TLRPC.TL_updates)))
      {
        localObject1 = null;
        i = 0;
        while (i < paramUpdates.chats.size())
        {
          localObject5 = (TLRPC.Chat)paramUpdates.chats.get(i);
          localObject3 = localObject1;
          if ((localObject5 instanceof TLRPC.TL_channel))
          {
            localObject3 = localObject1;
            if (((TLRPC.Chat)localObject5).min)
            {
              localObject3 = getChat(Integer.valueOf(((TLRPC.Chat)localObject5).id));
              if (localObject3 != null)
              {
                localObject2 = localObject3;
                if (!((TLRPC.Chat)localObject3).min) {}
              }
              else
              {
                localObject2 = MessagesStorage.getInstance().getChatSync(paramUpdates.chat_id);
                if (localObject3 == null) {
                  putChat((TLRPC.Chat)localObject2, true);
                }
              }
              if (localObject2 != null)
              {
                localObject3 = localObject1;
                if (!((TLRPC.Chat)localObject2).min) {}
              }
              else
              {
                localObject2 = localObject1;
                if (localObject1 == null) {
                  localObject2 = new HashMap();
                }
                ((HashMap)localObject2).put(Integer.valueOf(((TLRPC.Chat)localObject5).id), localObject5);
                localObject3 = localObject2;
              }
            }
          }
          i += 1;
          localObject1 = localObject3;
        }
        j = m;
        if (localObject1 != null) {
          i = 0;
        }
        for (;;)
        {
          j = m;
          if (i < paramUpdates.updates.size())
          {
            localObject2 = (TLRPC.Update)paramUpdates.updates.get(i);
            if ((localObject2 instanceof TLRPC.TL_updateNewChannelMessage))
            {
              j = ((TLRPC.TL_updateNewChannelMessage)localObject2).message.to_id.channel_id;
              if (((HashMap)localObject1).containsKey(Integer.valueOf(j)))
              {
                FileLog.e("tmessages", "need get diff because of min channel " + j);
                j = 1;
              }
            }
          }
          else
          {
            localObject2 = localObject6;
            i = j;
            m = i2;
            n = i1;
            if (j != 0) {
              break;
            }
            MessagesStorage.getInstance().putUsersAndChats(paramUpdates.users, paramUpdates.chats, true, true);
            Collections.sort(paramUpdates.updates, this.updatesComparator);
            m = 0;
            localObject1 = localObject4;
            if (paramUpdates.updates.size() <= 0) {
              break label3837;
            }
            localObject4 = (TLRPC.Update)paramUpdates.updates.get(m);
            if (getUpdateType((TLRPC.Update)localObject4) != 0) {
              break label2543;
            }
            localObject3 = new TLRPC.TL_updates();
            ((TLRPC.TL_updates)localObject3).updates.add(localObject4);
            ((TLRPC.TL_updates)localObject3).pts = ((TLRPC.Update)localObject4).pts;
            ((TLRPC.TL_updates)localObject3).pts_count = ((TLRPC.Update)localObject4).pts_count;
            for (i = m + 1; i < paramUpdates.updates.size(); i = i - 1 + 1)
            {
              localObject2 = (TLRPC.Update)paramUpdates.updates.get(i);
              if ((getUpdateType((TLRPC.Update)localObject2) != 0) || (((TLRPC.TL_updates)localObject3).pts + ((TLRPC.Update)localObject2).pts_count != ((TLRPC.Update)localObject2).pts)) {
                break;
              }
              ((TLRPC.TL_updates)localObject3).updates.add(localObject2);
              ((TLRPC.TL_updates)localObject3).pts = ((TLRPC.Update)localObject2).pts;
              ((TLRPC.TL_updates)localObject3).pts_count += ((TLRPC.Update)localObject2).pts_count;
              paramUpdates.updates.remove(i);
            }
          }
          i += 1;
        }
        if (MessagesStorage.lastPtsValue + ((TLRPC.TL_updates)localObject3).pts_count == ((TLRPC.TL_updates)localObject3).pts) {
          if (!processUpdateArray(((TLRPC.TL_updates)localObject3).updates, paramUpdates.users, paramUpdates.chats, false))
          {
            FileLog.e("tmessages", "need get diff inner TL_updates, seq: " + MessagesStorage.lastSeqValue + " " + paramUpdates.seq);
            i = 1;
            n = k;
            localObject2 = localObject1;
          }
        }
        for (;;)
        {
          paramUpdates.updates.remove(m);
          m = m - 1 + 1;
          localObject1 = localObject2;
          j = i;
          k = n;
          break;
          MessagesStorage.lastPtsValue = ((TLRPC.TL_updates)localObject3).pts;
          localObject2 = localObject1;
          i = j;
          n = k;
          continue;
          localObject2 = localObject1;
          i = j;
          n = k;
          if (MessagesStorage.lastPtsValue != ((TLRPC.TL_updates)localObject3).pts)
          {
            FileLog.e("tmessages", localObject4 + " need get diff, pts: " + MessagesStorage.lastPtsValue + " " + ((TLRPC.TL_updates)localObject3).pts + " count = " + ((TLRPC.TL_updates)localObject3).pts_count);
            if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || ((this.updatesStartWaitTimePts != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L)))
            {
              if (this.updatesStartWaitTimePts == 0L) {
                this.updatesStartWaitTimePts = System.currentTimeMillis();
              }
              FileLog.e("tmessages", "add to queue");
              this.updatesQueuePts.add(localObject3);
              localObject2 = localObject1;
              i = j;
              n = k;
            }
            else
            {
              i = 1;
              localObject2 = localObject1;
              n = k;
              continue;
              label2543:
              if (getUpdateType((TLRPC.Update)localObject4) == 1)
              {
                localObject3 = new TLRPC.TL_updates();
                ((TLRPC.TL_updates)localObject3).updates.add(localObject4);
                ((TLRPC.TL_updates)localObject3).pts = ((TLRPC.Update)localObject4).qts;
                for (i = m + 1; i < paramUpdates.updates.size(); i = i - 1 + 1)
                {
                  localObject2 = (TLRPC.Update)paramUpdates.updates.get(i);
                  if ((getUpdateType((TLRPC.Update)localObject2) != 1) || (((TLRPC.TL_updates)localObject3).pts + 1 != ((TLRPC.Update)localObject2).qts)) {
                    break;
                  }
                  ((TLRPC.TL_updates)localObject3).updates.add(localObject2);
                  ((TLRPC.TL_updates)localObject3).pts = ((TLRPC.Update)localObject2).qts;
                  paramUpdates.updates.remove(i);
                }
                if ((MessagesStorage.lastQtsValue == 0) || (MessagesStorage.lastQtsValue + ((TLRPC.TL_updates)localObject3).updates.size() == ((TLRPC.TL_updates)localObject3).pts))
                {
                  processUpdateArray(((TLRPC.TL_updates)localObject3).updates, paramUpdates.users, paramUpdates.chats, false);
                  MessagesStorage.lastQtsValue = ((TLRPC.TL_updates)localObject3).pts;
                  n = 1;
                  localObject2 = localObject1;
                  i = j;
                }
                else
                {
                  localObject2 = localObject1;
                  i = j;
                  n = k;
                  if (MessagesStorage.lastPtsValue != ((TLRPC.TL_updates)localObject3).pts)
                  {
                    FileLog.e("tmessages", localObject4 + " need get diff, qts: " + MessagesStorage.lastQtsValue + " " + ((TLRPC.TL_updates)localObject3).pts);
                    if ((this.gettingDifference) || (this.updatesStartWaitTimeQts == 0L) || ((this.updatesStartWaitTimeQts != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeQts) <= 1500L)))
                    {
                      if (this.updatesStartWaitTimeQts == 0L) {
                        this.updatesStartWaitTimeQts = System.currentTimeMillis();
                      }
                      FileLog.e("tmessages", "add to queue");
                      this.updatesQueueQts.add(localObject3);
                      localObject2 = localObject1;
                      i = j;
                      n = k;
                    }
                    else
                    {
                      i = 1;
                      localObject2 = localObject1;
                      n = k;
                    }
                  }
                }
              }
              else
              {
                if (getUpdateType((TLRPC.Update)localObject4) != 2) {
                  break label3837;
                }
                i3 = getUpdateChannelId((TLRPC.Update)localObject4);
                i2 = 0;
                localObject2 = (Integer)this.channelsPts.get(Integer.valueOf(i3));
                localObject3 = localObject2;
                i = i2;
                if (localObject2 == null)
                {
                  localObject2 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(i3));
                  if (((Integer)localObject2).intValue() != 0) {
                    break label3225;
                  }
                  n = 0;
                  localObject3 = localObject2;
                  i = i2;
                  if (n < paramUpdates.chats.size())
                  {
                    localObject3 = (TLRPC.Chat)paramUpdates.chats.get(n);
                    if (((TLRPC.Chat)localObject3).id != i3) {
                      break label3216;
                    }
                    loadUnknownChannel((TLRPC.Chat)localObject3, 0L);
                    i = 1;
                    localObject3 = localObject2;
                  }
                }
                for (;;)
                {
                  localObject5 = new TLRPC.TL_updates();
                  ((TLRPC.TL_updates)localObject5).updates.add(localObject4);
                  ((TLRPC.TL_updates)localObject5).pts = ((TLRPC.Update)localObject4).pts;
                  ((TLRPC.TL_updates)localObject5).pts_count = ((TLRPC.Update)localObject4).pts_count;
                  for (n = m + 1; n < paramUpdates.updates.size(); n = n - 1 + 1)
                  {
                    localObject2 = (TLRPC.Update)paramUpdates.updates.get(n);
                    if ((getUpdateType((TLRPC.Update)localObject2) != 2) || (i3 != getUpdateChannelId((TLRPC.Update)localObject2)) || (((TLRPC.TL_updates)localObject5).pts + ((TLRPC.Update)localObject2).pts_count != ((TLRPC.Update)localObject2).pts)) {
                      break;
                    }
                    ((TLRPC.TL_updates)localObject5).updates.add(localObject2);
                    ((TLRPC.TL_updates)localObject5).pts = ((TLRPC.Update)localObject2).pts;
                    ((TLRPC.TL_updates)localObject5).pts_count += ((TLRPC.Update)localObject2).pts_count;
                    paramUpdates.updates.remove(n);
                  }
                  label3216:
                  n += 1;
                  break;
                  label3225:
                  this.channelsPts.put(Integer.valueOf(i3), localObject2);
                  localObject3 = localObject2;
                  i = i2;
                }
                if (i == 0)
                {
                  if (((Integer)localObject3).intValue() + ((TLRPC.TL_updates)localObject5).pts_count == ((TLRPC.TL_updates)localObject5).pts)
                  {
                    if (!processUpdateArray(((TLRPC.TL_updates)localObject5).updates, paramUpdates.users, paramUpdates.chats, false))
                    {
                      FileLog.e("tmessages", "need get channel diff inner TL_updates, channel_id = " + i3);
                      if (localObject1 == null)
                      {
                        localObject2 = new ArrayList();
                        i = j;
                        n = k;
                      }
                      else
                      {
                        localObject2 = localObject1;
                        i = j;
                        n = k;
                        if (!((ArrayList)localObject1).contains(Integer.valueOf(i3)))
                        {
                          ((ArrayList)localObject1).add(Integer.valueOf(i3));
                          localObject2 = localObject1;
                          i = j;
                          n = k;
                        }
                      }
                    }
                    else
                    {
                      this.channelsPts.put(Integer.valueOf(i3), Integer.valueOf(((TLRPC.TL_updates)localObject5).pts));
                      MessagesStorage.getInstance().saveChannelPts(i3, ((TLRPC.TL_updates)localObject5).pts);
                      localObject2 = localObject1;
                      i = j;
                      n = k;
                    }
                  }
                  else
                  {
                    localObject2 = localObject1;
                    i = j;
                    n = k;
                    if (((Integer)localObject3).intValue() != ((TLRPC.TL_updates)localObject5).pts)
                    {
                      FileLog.e("tmessages", localObject4 + " need get channel diff, pts: " + localObject3 + " " + ((TLRPC.TL_updates)localObject5).pts + " count = " + ((TLRPC.TL_updates)localObject5).pts_count + " channelId = " + i3);
                      localObject4 = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(i3));
                      localObject3 = (Boolean)this.gettingDifferenceChannels.get(Integer.valueOf(i3));
                      localObject2 = localObject3;
                      if (localObject3 == null) {
                        localObject2 = Boolean.valueOf(false);
                      }
                      if ((((Boolean)localObject2).booleanValue()) || (localObject4 == null) || (Math.abs(System.currentTimeMillis() - ((Long)localObject4).longValue()) <= 1500L))
                      {
                        if (localObject4 == null) {
                          this.updatesStartWaitTimeChannels.put(Integer.valueOf(i3), Long.valueOf(System.currentTimeMillis()));
                        }
                        FileLog.e("tmessages", "add to queue");
                        localObject3 = (ArrayList)this.updatesQueueChannels.get(Integer.valueOf(i3));
                        localObject2 = localObject3;
                        if (localObject3 == null)
                        {
                          localObject2 = new ArrayList();
                          this.updatesQueueChannels.put(Integer.valueOf(i3), localObject2);
                        }
                        ((ArrayList)localObject2).add(localObject5);
                        localObject2 = localObject1;
                        i = j;
                        n = k;
                      }
                      else if (localObject1 == null)
                      {
                        localObject2 = new ArrayList();
                        i = j;
                        n = k;
                      }
                      else
                      {
                        localObject2 = localObject1;
                        i = j;
                        n = k;
                        if (!((ArrayList)localObject1).contains(Integer.valueOf(i3)))
                        {
                          ((ArrayList)localObject1).add(Integer.valueOf(i3));
                          localObject2 = localObject1;
                          i = j;
                          n = k;
                        }
                      }
                    }
                  }
                }
                else
                {
                  FileLog.e("tmessages", "need load unknown channel = " + i3);
                  localObject2 = localObject1;
                  i = j;
                  n = k;
                }
              }
            }
          }
        }
        label3837:
        if ((paramUpdates instanceof TLRPC.TL_updatesCombined))
        {
          if ((MessagesStorage.lastSeqValue + 1 == paramUpdates.seq_start) || (MessagesStorage.lastSeqValue == paramUpdates.seq_start)) {}
          for (i = 1;; i = 0)
          {
            if (i == 0) {
              break label3995;
            }
            processUpdateArray(paramUpdates.updates, paramUpdates.users, paramUpdates.chats, false);
            if (paramUpdates.date != 0) {
              MessagesStorage.lastDateValue = paramUpdates.date;
            }
            localObject2 = localObject1;
            i = j;
            m = k;
            n = i1;
            if (paramUpdates.seq == 0) {
              break;
            }
            MessagesStorage.lastSeqValue = paramUpdates.seq;
            localObject2 = localObject1;
            i = j;
            m = k;
            n = i1;
            break;
          }
        }
        if ((MessagesStorage.lastSeqValue + 1 == paramUpdates.seq) || (paramUpdates.seq == 0) || (paramUpdates.seq == MessagesStorage.lastSeqValue)) {}
        for (i = 1;; i = 0) {
          break;
        }
        label3995:
        if ((paramUpdates instanceof TLRPC.TL_updatesCombined)) {
          FileLog.e("tmessages", "need get diff TL_updatesCombined, seq: " + MessagesStorage.lastSeqValue + " " + paramUpdates.seq_start);
        }
        for (;;)
        {
          if ((!this.gettingDifference) && (this.updatesStartWaitTimeSeq != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) > 1500L)) {
            break label4173;
          }
          if (this.updatesStartWaitTimeSeq == 0L) {
            this.updatesStartWaitTimeSeq = System.currentTimeMillis();
          }
          FileLog.e("tmessages", "add TL_updates/Combined to queue");
          this.updatesQueueSeq.add(paramUpdates);
          localObject2 = localObject1;
          i = j;
          m = k;
          n = i1;
          break;
          FileLog.e("tmessages", "need get diff TL_updates, seq: " + MessagesStorage.lastSeqValue + " " + paramUpdates.seq);
        }
        label4173:
        i = 1;
        localObject2 = localObject1;
        m = k;
        n = i1;
        break;
      }
      if ((paramUpdates instanceof TLRPC.TL_updatesTooLong))
      {
        FileLog.e("tmessages", "need get diff TL_updatesTooLong");
        i = 1;
        localObject2 = localObject6;
        m = i2;
        n = i1;
        break;
      }
      if ((paramUpdates instanceof UserActionUpdatesSeq))
      {
        MessagesStorage.lastSeqValue = paramUpdates.seq;
        localObject2 = localObject6;
        i = i3;
        m = i2;
        n = i1;
        break;
      }
      localObject2 = localObject6;
      i = i3;
      m = i2;
      n = i1;
      if (!(paramUpdates instanceof UserActionUpdatesPts)) {
        break;
      }
      if (paramUpdates.chat_id != 0)
      {
        this.channelsPts.put(Integer.valueOf(paramUpdates.chat_id), Integer.valueOf(paramUpdates.pts));
        MessagesStorage.getInstance().saveChannelPts(paramUpdates.chat_id, paramUpdates.pts);
        localObject2 = localObject6;
        i = i3;
        m = i2;
        n = i1;
        break;
      }
      MessagesStorage.lastPtsValue = paramUpdates.pts;
      localObject2 = localObject6;
      i = i3;
      m = i2;
      n = i1;
      break;
      label4363:
      processChannelsUpdatesQueue(((Integer)localObject1).intValue(), 0);
    }
    label4376:
    if (i != 0) {
      getDifference();
    }
    for (;;)
    {
      label4384:
      if (m != 0)
      {
        paramUpdates = new TLRPC.TL_messages_receivedQueue();
        paramUpdates.max_qts = MessagesStorage.lastQtsValue;
        ConnectionsManager.getInstance().sendRequest(paramUpdates, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        });
      }
      if (n != 0) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
          }
        });
      }
      MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
      return;
      i = 0;
      while (i < 3)
      {
        processUpdatesQueue(i, 0);
        i += 1;
      }
    }
  }
  
  public void putChat(TLRPC.Chat paramChat, boolean paramBoolean)
  {
    if (paramChat == null) {}
    TLRPC.Chat localChat;
    label147:
    do
    {
      do
      {
        return;
        localChat = (TLRPC.Chat)this.chats.get(Integer.valueOf(paramChat.id));
        if (!paramChat.min) {
          break label147;
        }
        if (localChat == null) {
          break;
        }
      } while (paramBoolean);
      localChat.title = paramChat.title;
      localChat.photo = paramChat.photo;
      localChat.broadcast = paramChat.broadcast;
      localChat.verified = paramChat.verified;
      localChat.megagroup = paramChat.megagroup;
      localChat.democracy = paramChat.democracy;
      if (paramChat.username != null)
      {
        localChat.username = paramChat.username;
        localChat.flags |= 0x40;
        return;
      }
      localChat.username = null;
      localChat.flags &= 0xFFFFFFBF;
      return;
      this.chats.put(Integer.valueOf(paramChat.id), paramChat);
      return;
      if (!paramBoolean)
      {
        if ((localChat != null) && (paramChat.version != localChat.version)) {
          this.loadedFullChats.remove(Integer.valueOf(paramChat.id));
        }
        this.chats.put(Integer.valueOf(paramChat.id), paramChat);
        return;
      }
      if (localChat == null)
      {
        this.chats.put(Integer.valueOf(paramChat.id), paramChat);
        return;
      }
    } while (!localChat.min);
    paramChat.min = false;
    paramChat.title = localChat.title;
    paramChat.photo = localChat.photo;
    paramChat.broadcast = localChat.broadcast;
    paramChat.verified = localChat.verified;
    paramChat.megagroup = localChat.megagroup;
    paramChat.democracy = localChat.democracy;
    if (localChat.username != null) {
      paramChat.username = localChat.username;
    }
    for (paramChat.flags |= 0x40;; paramChat.flags &= 0xFFFFFFBF)
    {
      this.chats.put(Integer.valueOf(paramChat.id), paramChat);
      return;
      paramChat.username = null;
    }
  }
  
  public void putChats(ArrayList<TLRPC.Chat> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        putChat((TLRPC.Chat)paramArrayList.get(i), paramBoolean);
        i += 1;
      }
    }
  }
  
  public void putEncryptedChat(TLRPC.EncryptedChat paramEncryptedChat, boolean paramBoolean)
  {
    if (paramEncryptedChat == null) {
      return;
    }
    if (paramBoolean)
    {
      this.encryptedChats.putIfAbsent(Integer.valueOf(paramEncryptedChat.id), paramEncryptedChat);
      return;
    }
    this.encryptedChats.put(Integer.valueOf(paramEncryptedChat.id), paramEncryptedChat);
  }
  
  public void putEncryptedChats(ArrayList<TLRPC.EncryptedChat> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        putEncryptedChat((TLRPC.EncryptedChat)paramArrayList.get(i), paramBoolean);
        i += 1;
      }
    }
  }
  
  public boolean putUser(TLRPC.User paramUser, boolean paramBoolean)
  {
    if (paramUser == null) {}
    TLRPC.User localUser;
    label228:
    label249:
    label267:
    do
    {
      do
      {
        int i;
        do
        {
          return false;
          if ((!paramBoolean) || (paramUser.id / 1000 == 333) || (paramUser.id == 777000)) {
            break;
          }
          i = 1;
          localUser = (TLRPC.User)this.users.get(Integer.valueOf(paramUser.id));
          if ((localUser != null) && (localUser.username != null) && (localUser.username.length() > 0)) {
            this.usersByUsernames.remove(localUser.username);
          }
          if ((paramUser.username != null) && (paramUser.username.length() > 0)) {
            this.usersByUsernames.put(paramUser.username.toLowerCase(), paramUser);
          }
          if (!paramUser.min) {
            break label267;
          }
          if (localUser == null) {
            break label249;
          }
        } while (i != 0);
        if (paramUser.username != null) {
          localUser.username = paramUser.username;
        }
        for (localUser.flags |= 0x8;; localUser.flags &= 0xFFFFFFF7)
        {
          if (paramUser.photo == null) {
            break label228;
          }
          localUser.photo = paramUser.photo;
          localUser.flags |= 0x20;
          return false;
          i = 0;
          break;
          localUser.username = null;
        }
        localUser.photo = null;
        localUser.flags &= 0xFFFFFFDF;
        return false;
        this.users.put(Integer.valueOf(paramUser.id), paramUser);
        return false;
        if (i != 0) {
          break;
        }
        this.users.put(Integer.valueOf(paramUser.id), paramUser);
        if (paramUser.id == UserConfig.getClientUserId())
        {
          UserConfig.setCurrentUser(paramUser);
          UserConfig.saveConfig(true);
        }
      } while ((localUser == null) || (paramUser.status == null) || (localUser.status == null) || (paramUser.status.expires == localUser.status.expires));
      return true;
      if (localUser == null)
      {
        this.users.put(Integer.valueOf(paramUser.id), paramUser);
        return false;
      }
    } while (!localUser.min);
    paramUser.min = false;
    if (localUser.username != null)
    {
      paramUser.username = localUser.username;
      paramUser.flags |= 0x8;
      if (localUser.photo == null) {
        break label474;
      }
      paramUser.photo = localUser.photo;
    }
    for (paramUser.flags |= 0x20;; paramUser.flags &= 0xFFFFFFDF)
    {
      this.users.put(Integer.valueOf(paramUser.id), paramUser);
      return false;
      paramUser.username = null;
      paramUser.flags &= 0xFFFFFFF7;
      break;
      label474:
      paramUser.photo = null;
    }
  }
  
  public void putUsers(ArrayList<TLRPC.User> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    int j;
    do
    {
      return;
      j = 0;
      int k = paramArrayList.size();
      int i = 0;
      while (i < k)
      {
        if (putUser((TLRPC.User)paramArrayList.get(i), paramBoolean)) {
          j = 1;
        }
        i += 1;
      }
    } while (j == 0);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
      }
    });
  }
  
  public void registerForPush(final String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0) || (this.registeringForPush) || (UserConfig.getClientUserId() == 0)) {}
    while ((UserConfig.registeredForPush) && (paramString.equals(UserConfig.pushString))) {
      return;
    }
    this.registeringForPush = true;
    TLRPC.TL_account_registerDevice localTL_account_registerDevice = new TLRPC.TL_account_registerDevice();
    localTL_account_registerDevice.token_type = 2;
    localTL_account_registerDevice.token = paramString;
    ConnectionsManager.getInstance().sendRequest(localTL_account_registerDevice, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue))
        {
          FileLog.e("tmessages", "registered for push");
          UserConfig.registeredForPush = true;
          UserConfig.pushString = paramString;
          UserConfig.saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.registeringForPush = false;
          }
        });
      }
    });
  }
  
  public void reloadWebPages(final long paramLong, HashMap<String, ArrayList<MessageObject>> paramHashMap)
  {
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      paramHashMap = (Map.Entry)localIterator.next();
      final String str = (String)paramHashMap.getKey();
      ArrayList localArrayList2 = (ArrayList)paramHashMap.getValue();
      ArrayList localArrayList1 = (ArrayList)this.reloadingWebpages.get(str);
      paramHashMap = localArrayList1;
      if (localArrayList1 == null)
      {
        paramHashMap = new ArrayList();
        this.reloadingWebpages.put(str, paramHashMap);
      }
      paramHashMap.addAll(localArrayList2);
      paramHashMap = new TLRPC.TL_messages_getWebPagePreview();
      paramHashMap.message = str;
      ConnectionsManager.getInstance().sendRequest(paramHashMap, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ArrayList localArrayList = (ArrayList)MessagesController.this.reloadingWebpages.remove(MessagesController.51.this.val$url);
              if (localArrayList == null) {}
              TLRPC.TL_messages_messages localTL_messages_messages;
              do
              {
                return;
                localTL_messages_messages = new TLRPC.TL_messages_messages();
                int i;
                if (!(paramAnonymousTLObject instanceof TLRPC.TL_messageMediaWebPage))
                {
                  i = 0;
                  while (i < localArrayList.size())
                  {
                    ((MessageObject)localArrayList.get(i)).messageOwner.media.webpage = new TLRPC.TL_webPageEmpty();
                    localTL_messages_messages.messages.add(((MessageObject)localArrayList.get(i)).messageOwner);
                    i += 1;
                  }
                }
                TLRPC.TL_messageMediaWebPage localTL_messageMediaWebPage = (TLRPC.TL_messageMediaWebPage)paramAnonymousTLObject;
                if (((localTL_messageMediaWebPage.webpage instanceof TLRPC.TL_webPage)) || ((localTL_messageMediaWebPage.webpage instanceof TLRPC.TL_webPageEmpty))) {
                  i = 0;
                }
                while (i < localArrayList.size())
                {
                  ((MessageObject)localArrayList.get(i)).messageOwner.media.webpage = localTL_messageMediaWebPage.webpage;
                  if (i == 0) {
                    ImageLoader.saveMessageThumbs(((MessageObject)localArrayList.get(i)).messageOwner);
                  }
                  localTL_messages_messages.messages.add(((MessageObject)localArrayList.get(i)).messageOwner);
                  i += 1;
                  continue;
                  MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(localTL_messageMediaWebPage.webpage.id), localArrayList);
                }
              } while (localTL_messages_messages.messages.isEmpty());
              MessagesStorage.getInstance().putMessages(localTL_messages_messages, MessagesController.51.this.val$dialog_id, -2, 0, false);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(MessagesController.51.this.val$dialog_id), localArrayList });
            }
          });
        }
      });
    }
  }
  
  public void reportSpam(long paramLong, TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    if ((paramUser == null) && (paramChat == null)) {
      return;
    }
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
    ((SharedPreferences.Editor)localObject).putInt("spam3_" + paramLong, 1);
    ((SharedPreferences.Editor)localObject).commit();
    localObject = new TLRPC.TL_messages_reportSpam();
    if (paramChat != null) {
      ((TLRPC.TL_messages_reportSpam)localObject).peer = getInputPeer(-paramChat.id);
    }
    for (;;)
    {
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      }, 2);
      return;
      if (paramUser != null) {
        ((TLRPC.TL_messages_reportSpam)localObject).peer = getInputPeer(paramUser.id);
      }
    }
  }
  
  public void saveGif(TLRPC.Document paramDocument)
  {
    TLRPC.TL_messages_saveGif localTL_messages_saveGif = new TLRPC.TL_messages_saveGif();
    localTL_messages_saveGif.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveGif.id.id = paramDocument.id;
    localTL_messages_saveGif.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveGif.unsave = false;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_saveGif, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void saveRecentSticker(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    TLRPC.TL_messages_saveRecentSticker localTL_messages_saveRecentSticker = new TLRPC.TL_messages_saveRecentSticker();
    localTL_messages_saveRecentSticker.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveRecentSticker.id.id = paramDocument.id;
    localTL_messages_saveRecentSticker.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveRecentSticker.unsave = false;
    localTL_messages_saveRecentSticker.attached = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_saveRecentSticker, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void sendBotStart(TLRPC.User paramUser, String paramString)
  {
    if (paramUser == null) {
      return;
    }
    TLRPC.TL_messages_startBot localTL_messages_startBot = new TLRPC.TL_messages_startBot();
    localTL_messages_startBot.bot = getInputUser(paramUser);
    localTL_messages_startBot.peer = getInputPeer(paramUser.id);
    localTL_messages_startBot.start_param = paramString;
    localTL_messages_startBot.random_id = Utilities.random.nextLong();
    ConnectionsManager.getInstance().sendRequest(localTL_messages_startBot, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error != null) {
          return;
        }
        MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
      }
    });
  }
  
  public void sendTyping(final long paramLong, final int paramInt1, int paramInt2)
  {
    if (paramLong == 0L) {}
    do
    {
      Object localObject2;
      Object localObject1;
      do
      {
        int j;
        do
        {
          do
          {
            do
            {
              int i;
              do
              {
                do
                {
                  return;
                  localObject2 = (HashMap)this.sendingTypings.get(Integer.valueOf(paramInt1));
                } while ((localObject2 != null) && (((HashMap)localObject2).get(Long.valueOf(paramLong)) != null));
                localObject1 = localObject2;
                if (localObject2 == null)
                {
                  localObject1 = new HashMap();
                  this.sendingTypings.put(Integer.valueOf(paramInt1), localObject1);
                }
                i = (int)paramLong;
                j = (int)(paramLong >> 32);
                if (i == 0) {
                  break;
                }
              } while (j == 1);
              localObject2 = new TLRPC.TL_messages_setTyping();
              ((TLRPC.TL_messages_setTyping)localObject2).peer = getInputPeer(i);
              if (!(((TLRPC.TL_messages_setTyping)localObject2).peer instanceof TLRPC.TL_inputPeerChannel)) {
                break;
              }
              localObject3 = getChat(Integer.valueOf(((TLRPC.TL_messages_setTyping)localObject2).peer.channel_id));
            } while ((localObject3 == null) || (!((TLRPC.Chat)localObject3).megagroup));
          } while (((TLRPC.TL_messages_setTyping)localObject2).peer == null);
          if (paramInt1 == 0) {
            ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageTypingAction();
          }
          for (;;)
          {
            ((HashMap)localObject1).put(Long.valueOf(paramLong), Boolean.valueOf(true));
            paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
            {
              public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    HashMap localHashMap = (HashMap)MessagesController.this.sendingTypings.get(Integer.valueOf(MessagesController.48.this.val$action));
                    if (localHashMap != null) {
                      localHashMap.remove(Long.valueOf(MessagesController.48.this.val$dialog_id));
                    }
                  }
                });
              }
            }, 2);
            if (paramInt2 == 0) {
              break;
            }
            ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
            return;
            if (paramInt1 == 1) {
              ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageRecordAudioAction();
            } else if (paramInt1 == 2) {
              ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageCancelAction();
            } else if (paramInt1 == 3) {
              ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageUploadDocumentAction();
            } else if (paramInt1 == 4) {
              ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageUploadPhotoAction();
            } else if (paramInt1 == 5) {
              ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageUploadVideoAction();
            }
          }
        } while (paramInt1 != 0);
        localObject2 = getEncryptedChat(Integer.valueOf(j));
      } while ((((TLRPC.EncryptedChat)localObject2).auth_key == null) || (((TLRPC.EncryptedChat)localObject2).auth_key.length <= 1) || (!(localObject2 instanceof TLRPC.TL_encryptedChat)));
      Object localObject3 = new TLRPC.TL_messages_setEncryptedTyping();
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer = new TLRPC.TL_inputEncryptedChat();
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer.chat_id = ((TLRPC.EncryptedChat)localObject2).id;
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer.access_hash = ((TLRPC.EncryptedChat)localObject2).access_hash;
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).typing = true;
      ((HashMap)localObject1).put(Long.valueOf(paramLong), Boolean.valueOf(true));
      paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject3, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              HashMap localHashMap = (HashMap)MessagesController.this.sendingTypings.get(Integer.valueOf(MessagesController.49.this.val$action));
              if (localHashMap != null) {
                localHashMap.remove(Long.valueOf(MessagesController.49.this.val$dialog_id));
              }
            }
          });
        }
      }, 2);
    } while (paramInt2 == 0);
    ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
  }
  
  public void setLastCreatedDialogId(final long paramLong, final boolean paramBoolean)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (paramBoolean)
        {
          MessagesController.this.createdDialogIds.add(Long.valueOf(paramLong));
          return;
        }
        MessagesController.this.createdDialogIds.remove(Long.valueOf(paramLong));
      }
    });
  }
  
  public void sortDialogs(HashMap<Integer, TLRPC.Chat> paramHashMap)
  {
    this.dialogsServerOnly.clear();
    this.dialogsGroupsOnly.clear();
    Collections.sort(this.dialogs, this.dialogComparator);
    int i = 0;
    if (i < this.dialogs.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs.get(i);
      int k = (int)(localTL_dialog.id >> 32);
      int m = (int)localTL_dialog.id;
      int j = i;
      TLRPC.Chat localChat;
      if (m != 0)
      {
        j = i;
        if (k != 1)
        {
          this.dialogsServerOnly.add(localTL_dialog);
          if (!DialogObject.isChannel(localTL_dialog)) {
            break label167;
          }
          localChat = getChat(Integer.valueOf(-m));
          j = i;
          if (localChat != null) {
            if ((!localChat.megagroup) || (!localChat.editor))
            {
              j = i;
              if (!localChat.creator) {}
            }
            else
            {
              this.dialogsGroupsOnly.add(localTL_dialog);
              j = i;
            }
          }
        }
      }
      for (;;)
      {
        i = j + 1;
        break;
        label167:
        j = i;
        if (m < 0)
        {
          if (paramHashMap != null)
          {
            localChat = (TLRPC.Chat)paramHashMap.get(Integer.valueOf(-m));
            if ((localChat != null) && (localChat.migrated_to != null))
            {
              this.dialogs.remove(i);
              j = i - 1;
              continue;
            }
          }
          this.dialogsGroupsOnly.add(localTL_dialog);
          j = i;
        }
      }
    }
  }
  
  public void startShortPoll(final int paramInt, final boolean paramBoolean)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (paramBoolean) {
          MessagesController.this.needShortPollChannels.delete(paramInt);
        }
        do
        {
          return;
          MessagesController.this.needShortPollChannels.put(paramInt, 0);
        } while (MessagesController.this.shortPollChannels.indexOfKey(paramInt) >= 0);
        MessagesController.this.getChannelDifference(paramInt, 2, 0L);
      }
    });
  }
  
  public void toggleAdminMode(final int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_messages_toggleChatAdmins localTL_messages_toggleChatAdmins = new TLRPC.TL_messages_toggleChatAdmins();
    localTL_messages_toggleChatAdmins.chat_id = paramInt;
    localTL_messages_toggleChatAdmins.enabled = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_toggleChatAdmins, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          MessagesController.this.loadFullChat(paramInt, 0, true);
        }
      }
    });
  }
  
  public void toggleUserAdmin(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    TLRPC.TL_messages_editChatAdmin localTL_messages_editChatAdmin = new TLRPC.TL_messages_editChatAdmin();
    localTL_messages_editChatAdmin.chat_id = paramInt1;
    localTL_messages_editChatAdmin.user_id = getInputUser(paramInt2);
    localTL_messages_editChatAdmin.is_admin = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_editChatAdmin, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void toogleChannelInvites(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_toggleInvites localTL_channels_toggleInvites = new TLRPC.TL_channels_toggleInvites();
    localTL_channels_toggleInvites.channel = getInputChannel(paramInt);
    localTL_channels_toggleInvites.enabled = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_toggleInvites, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTLObject != null) {
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
        }
      }
    }, 64);
  }
  
  public void toogleChannelSignatures(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_toggleSignatures localTL_channels_toggleSignatures = new TLRPC.TL_channels_toggleSignatures();
    localTL_channels_toggleSignatures.channel = getInputChannel(paramInt);
    localTL_channels_toggleSignatures.enabled = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_toggleSignatures, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTLObject != null)
        {
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(8192) });
            }
          });
        }
      }
    }, 64);
  }
  
  public void unblockUser(int paramInt)
  {
    TLRPC.TL_contacts_unblock localTL_contacts_unblock = new TLRPC.TL_contacts_unblock();
    final TLRPC.User localUser = getUser(Integer.valueOf(paramInt));
    if (localUser == null) {
      return;
    }
    this.blockedUsers.remove(Integer.valueOf(localUser.id));
    localTL_contacts_unblock.id = getInputUser(localUser);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_unblock, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        MessagesStorage.getInstance().deleteBlockedUser(localUser.id);
      }
    });
  }
  
  public void unregistedPush()
  {
    if ((UserConfig.registeredForPush) && (UserConfig.pushString.length() == 0))
    {
      TLRPC.TL_account_unregisterDevice localTL_account_unregisterDevice = new TLRPC.TL_account_unregisterDevice();
      localTL_account_unregisterDevice.token = UserConfig.pushString;
      localTL_account_unregisterDevice.token_type = 2;
      ConnectionsManager.getInstance().sendRequest(localTL_account_unregisterDevice, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
    }
  }
  
  public void updateChannelAbout(int paramInt, final String paramString, final TLRPC.ChatFull paramChatFull)
  {
    if (paramChatFull == null) {
      return;
    }
    TLRPC.TL_channels_editAbout localTL_channels_editAbout = new TLRPC.TL_channels_editAbout();
    localTL_channels_editAbout.channel = getInputChannel(paramInt);
    localTL_channels_editAbout.about = paramString;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_editAbout, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue)) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.72.this.val$info.about = MessagesController.72.this.val$about;
              MessagesStorage.getInstance().updateChatInfo(MessagesController.72.this.val$info, false);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { MessagesController.72.this.val$info, Integer.valueOf(0), Boolean.valueOf(false), null });
            }
          });
        }
      }
    }, 64);
  }
  
  public void updateChannelUserName(final int paramInt, final String paramString)
  {
    TLRPC.TL_channels_updateUsername localTL_channels_updateUsername = new TLRPC.TL_channels_updateUsername();
    localTL_channels_updateUsername.channel = getInputChannel(paramInt);
    localTL_channels_updateUsername.username = paramString;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_updateUsername, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue)) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TLRPC.Chat localChat = MessagesController.this.getChat(Integer.valueOf(MessagesController.73.this.val$chat_id));
              if (MessagesController.73.this.val$userName.length() != 0) {}
              for (localChat.flags |= 0x40;; localChat.flags &= 0xFFFFFFBF)
              {
                localChat.username = MessagesController.73.this.val$userName;
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localChat);
                MessagesStorage.getInstance().putUsersAndChats(null, localArrayList, true, true);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(8192) });
                return;
              }
            }
          });
        }
      }
    }, 64);
  }
  
  public void updateConfig(final TLRPC.TL_config paramTL_config)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.this.maxMegagroupCount = paramTL_config.megagroup_size_max;
        MessagesController.this.maxGroupCount = paramTL_config.chat_size_max;
        MessagesController.this.groupBigSize = paramTL_config.chat_big_size;
        MessagesController.access$202(MessagesController.this, paramTL_config.disabled_features);
        MessagesController.this.maxEditTime = paramTL_config.edit_time_limit;
        MessagesController.this.ratingDecay = paramTL_config.rating_e_decay;
        MessagesController.this.maxRecentGifsCount = paramTL_config.saved_gifs_limit;
        MessagesController.this.maxRecentStickersCount = paramTL_config.stickers_recent_limit;
        SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        localEditor.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
        localEditor.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
        localEditor.putInt("groupBigSize", MessagesController.this.groupBigSize);
        localEditor.putInt("maxEditTime", MessagesController.this.maxEditTime);
        localEditor.putInt("ratingDecay", MessagesController.this.ratingDecay);
        localEditor.putInt("maxRecentGifsCount", MessagesController.this.maxRecentGifsCount);
        localEditor.putInt("maxRecentStickersCount", MessagesController.this.maxRecentStickersCount);
        try
        {
          SerializedData localSerializedData = new SerializedData();
          localSerializedData.writeInt32(MessagesController.this.disabledFeatures.size());
          Iterator localIterator = MessagesController.this.disabledFeatures.iterator();
          while (localIterator.hasNext())
          {
            ((TLRPC.TL_disabledFeature)localIterator.next()).serializeToStream(localSerializedData);
            continue;
            localEditor.commit();
          }
        }
        catch (Exception localException)
        {
          localEditor.remove("disabledFeatures");
          FileLog.e("tmessages", localException);
        }
        for (;;)
        {
          return;
          String str = Base64.encodeToString(localException.toByteArray(), 0);
          if (str.length() != 0) {
            localEditor.putString("disabledFeatures", str);
          }
        }
      }
    });
  }
  
  protected void updateInterfaceWithMessages(long paramLong, ArrayList<MessageObject> paramArrayList)
  {
    updateInterfaceWithMessages(paramLong, paramArrayList, false);
  }
  
  protected void updateInterfaceWithMessages(long paramLong, ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    int i;
    Object localObject1;
    int j;
    int k;
    int m;
    label32:
    label287:
    label321:
    do
    {
      do
      {
        do
        {
          return;
          MessageObject localMessageObject;
          int n;
          if ((int)paramLong == 0)
          {
            i = 1;
            localObject1 = null;
            j = 0;
            k = 0;
            m = 0;
            if (m >= paramArrayList.size()) {
              break label321;
            }
            localMessageObject = (MessageObject)paramArrayList.get(m);
            if ((localObject1 != null) && ((i != 0) || (localMessageObject.getId() <= ((MessageObject)localObject1).getId())) && (((i == 0) && ((localMessageObject.getId() >= 0) || (((MessageObject)localObject1).getId() >= 0))) || (localMessageObject.getId() >= ((MessageObject)localObject1).getId())))
            {
              n = j;
              localObject2 = localObject1;
              if (localMessageObject.messageOwner.date <= ((MessageObject)localObject1).messageOwner.date) {}
            }
            else
            {
              localObject1 = localMessageObject;
              n = j;
              localObject2 = localObject1;
              if (localMessageObject.messageOwner.to_id.channel_id != 0)
              {
                n = localMessageObject.messageOwner.to_id.channel_id;
                localObject2 = localObject1;
              }
            }
            if ((localMessageObject.isOut()) && (!localMessageObject.isSending()) && (!localMessageObject.isForwarded()))
            {
              if (!localMessageObject.isNewGif()) {
                break label287;
              }
              StickersQuery.addRecentGif(localMessageObject.messageOwner.media.document, localMessageObject.messageOwner.date);
            }
          }
          for (;;)
          {
            int i1 = k;
            if (localMessageObject.isOut())
            {
              i1 = k;
              if (localMessageObject.isSent()) {
                i1 = 1;
              }
            }
            m += 1;
            j = n;
            localObject1 = localObject2;
            k = i1;
            break label32;
            i = 0;
            break;
            if (localMessageObject.isSticker()) {
              StickersQuery.addRecentSticker(0, localMessageObject.messageOwner.media.document, localMessageObject.messageOwner.date);
            }
          }
          MessagesQuery.loadReplyMessagesForMessages(paramArrayList, paramLong);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceivedNewMessages, new Object[] { Long.valueOf(paramLong), paramArrayList });
        } while (localObject1 == null);
        paramArrayList = (TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(paramLong));
        if (!(((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo)) {
          break;
        }
      } while (paramArrayList == null);
      this.dialogs.remove(paramArrayList);
      this.dialogsServerOnly.remove(paramArrayList);
      this.dialogsGroupsOnly.remove(paramArrayList);
      this.dialogs_dict.remove(Long.valueOf(paramArrayList.id));
      this.dialogs_read_inbox_max.remove(Long.valueOf(paramArrayList.id));
      this.dialogs_read_outbox_max.remove(Long.valueOf(paramArrayList.id));
      this.nextDialogsCacheOffset -= 1;
      this.dialogMessage.remove(Long.valueOf(paramArrayList.id));
      localObject1 = (MessageObject)this.dialogMessagesByIds.remove(Integer.valueOf(paramArrayList.top_message));
      if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.random_id != 0L)) {
        this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id));
      }
      paramArrayList.top_message = 0;
      NotificationsController.getInstance().removeNotificationsForDialog(paramArrayList.id);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
      return;
      m = 0;
      i = 0;
      if (paramArrayList != null) {
        break label819;
      }
      if (paramBoolean) {
        break;
      }
      paramArrayList = getChat(Integer.valueOf(j));
    } while (((j != 0) && (paramArrayList == null)) || ((paramArrayList != null) && (paramArrayList.left)));
    Object localObject2 = new TLRPC.TL_dialog();
    ((TLRPC.TL_dialog)localObject2).id = paramLong;
    ((TLRPC.TL_dialog)localObject2).unread_count = 0;
    ((TLRPC.TL_dialog)localObject2).top_message = ((MessageObject)localObject1).getId();
    ((TLRPC.TL_dialog)localObject2).last_message_date = ((MessageObject)localObject1).messageOwner.date;
    if (ChatObject.isChannel(paramArrayList))
    {
      i = 1;
      label669:
      ((TLRPC.TL_dialog)localObject2).flags = i;
      this.dialogs_dict.put(Long.valueOf(paramLong), localObject2);
      this.dialogs.add(localObject2);
      this.dialogMessage.put(Long.valueOf(paramLong), localObject1);
      if (((MessageObject)localObject1).messageOwner.to_id.channel_id == 0)
      {
        this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
        if (((MessageObject)localObject1).messageOwner.random_id != 0L) {
          this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id), localObject1);
        }
      }
      this.nextDialogsCacheOffset += 1;
      i = 1;
    }
    for (;;)
    {
      if (i != 0) {
        sortDialogs(null);
      }
      if (k == 0) {
        break;
      }
      SearchQuery.increasePeerRaiting(paramLong);
      return;
      i = 0;
      break label669;
      label819:
      if (((paramArrayList.top_message > 0) && (((MessageObject)localObject1).getId() > 0) && (((MessageObject)localObject1).getId() > paramArrayList.top_message)) || ((paramArrayList.top_message < 0) && (((MessageObject)localObject1).getId() < 0) && (((MessageObject)localObject1).getId() < paramArrayList.top_message)) || (!this.dialogMessage.containsKey(Long.valueOf(paramLong))) || (paramArrayList.top_message < 0) || (paramArrayList.last_message_date <= ((MessageObject)localObject1).messageOwner.date))
      {
        localObject2 = (MessageObject)this.dialogMessagesByIds.remove(Integer.valueOf(paramArrayList.top_message));
        if ((localObject2 != null) && (((MessageObject)localObject2).messageOwner.random_id != 0L)) {
          this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject2).messageOwner.random_id));
        }
        paramArrayList.top_message = ((MessageObject)localObject1).getId();
        j = m;
        if (!paramBoolean)
        {
          paramArrayList.last_message_date = ((MessageObject)localObject1).messageOwner.date;
          j = 1;
        }
        this.dialogMessage.put(Long.valueOf(paramLong), localObject1);
        i = j;
        if (((MessageObject)localObject1).messageOwner.to_id.channel_id == 0)
        {
          this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
          i = j;
          if (((MessageObject)localObject1).messageOwner.random_id != 0L)
          {
            this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id), localObject1);
            i = j;
          }
        }
      }
    }
  }
  
  public void updateTimerProc()
  {
    long l = System.currentTimeMillis();
    checkDeletingTask(false);
    Object localObject1;
    label140:
    int i;
    int j;
    final Object localObject2;
    if (UserConfig.isClientActivated())
    {
      if ((ConnectionsManager.getInstance().getPauseTime() == 0L) && (ApplicationLoader.isScreenOn) && (!ApplicationLoader.mainInterfacePaused)) {
        if ((this.statusSettingState != 1) && ((this.lastStatusUpdateTime == 0L) || (Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000L) || (this.offlineSent)))
        {
          this.statusSettingState = 1;
          if (this.statusRequest != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.statusRequest, true);
          }
          localObject1 = new TLRPC.TL_account_updateStatus();
          ((TLRPC.TL_account_updateStatus)localObject1).offline = false;
        }
      }
      for (this.statusRequest = ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null)
              {
                MessagesController.access$3702(MessagesController.this, System.currentTimeMillis());
                MessagesController.access$3802(MessagesController.this, false);
                MessagesController.access$3902(MessagesController.this, 0);
              }
              for (;;)
              {
                MessagesController.access$4002(MessagesController.this, 0);
                return;
                if (MessagesController.this.lastStatusUpdateTime != 0L) {
                  MessagesController.access$3702(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
                }
              }
            }
          }); !this.updatesQueueChannels.isEmpty(); this.statusRequest = ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null) {
                MessagesController.access$3802(MessagesController.this, true);
              }
              for (;;)
              {
                MessagesController.access$4002(MessagesController.this, 0);
                return;
                if (MessagesController.this.lastStatusUpdateTime != 0L) {
                  MessagesController.access$3702(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
                }
              }
            }
          }))
      {
        localObject1 = new ArrayList(this.updatesQueueChannels.keySet());
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          j = ((Integer)((ArrayList)localObject1).get(i)).intValue();
          localObject2 = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(j));
          if ((localObject2 != null) && (((Long)localObject2).longValue() + 1500L < l))
          {
            FileLog.e("tmessages", "QUEUE CHANNEL " + j + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
            processChannelsUpdatesQueue(j, 0);
          }
          i += 1;
        }
        if ((this.statusSettingState == 2) || (this.offlineSent) || (Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance().getPauseTime()) < 2000L)) {
          break label140;
        }
        this.statusSettingState = 2;
        if (this.statusRequest != 0) {
          ConnectionsManager.getInstance().cancelRequest(this.statusRequest, true);
        }
        localObject1 = new TLRPC.TL_account_updateStatus();
        ((TLRPC.TL_account_updateStatus)localObject1).offline = true;
      }
      i = 0;
      while (i < 3)
      {
        if ((getUpdatesStartTime(i) != 0L) && (getUpdatesStartTime(i) + 1500L < l))
        {
          FileLog.e("tmessages", i + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
          processUpdatesQueue(i, 0);
        }
        i += 1;
      }
    }
    label500:
    label524:
    final int k;
    if (((this.channelViewsToSend.size() != 0) || (this.channelViewsToReload.size() != 0)) && (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= 5000L))
    {
      this.lastViewsCheckTime = System.currentTimeMillis();
      i = 0;
      if (i < 2)
      {
        if (i == 0)
        {
          localObject1 = this.channelViewsToSend;
          if (((SparseArray)localObject1).size() != 0) {
            break label524;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          localObject1 = this.channelViewsToReload;
          break label500;
          j = 0;
          if (j < ((SparseArray)localObject1).size())
          {
            k = ((SparseArray)localObject1).keyAt(j);
            localObject2 = new TLRPC.TL_messages_getMessagesViews();
            ((TLRPC.TL_messages_getMessagesViews)localObject2).peer = getInputPeer(k);
            ((TLRPC.TL_messages_getMessagesViews)localObject2).id = ((ArrayList)((SparseArray)localObject1).get(k));
            if (j == 0) {}
            for (boolean bool = true;; bool = false)
            {
              ((TLRPC.TL_messages_getMessagesViews)localObject2).increment = bool;
              ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
              {
                public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
                {
                  TLRPC.Vector localVector;
                  final SparseArray localSparseArray;
                  int i;
                  if (paramAnonymousTL_error == null)
                  {
                    localVector = (TLRPC.Vector)paramAnonymousTLObject;
                    localSparseArray = new SparseArray();
                    paramAnonymousTL_error = (SparseIntArray)localSparseArray.get(k);
                    paramAnonymousTLObject = paramAnonymousTL_error;
                    if (paramAnonymousTL_error == null)
                    {
                      paramAnonymousTLObject = new SparseIntArray();
                      localSparseArray.put(k, paramAnonymousTLObject);
                    }
                    i = 0;
                  }
                  for (;;)
                  {
                    if ((i >= localObject2.id.size()) || (i >= localVector.objects.size()))
                    {
                      MessagesStorage.getInstance().putChannelViews(localSparseArray, localObject2.peer instanceof TLRPC.TL_inputPeerChannel);
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedMessagesViews, new Object[] { localSparseArray });
                        }
                      });
                      return;
                    }
                    paramAnonymousTLObject.put(((Integer)localObject2.id.get(i)).intValue(), ((Integer)localVector.objects.get(i)).intValue());
                    i += 1;
                  }
                }
              });
              j += 1;
              break;
            }
          }
          ((SparseArray)localObject1).clear();
        }
      }
    }
    Object localObject3;
    Object localObject4;
    if (!this.onlinePrivacy.isEmpty())
    {
      localObject1 = null;
      i = ConnectionsManager.getInstance().getCurrentTime();
      localObject3 = this.onlinePrivacy.entrySet().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (Map.Entry)((Iterator)localObject3).next();
        if (((Integer)((Map.Entry)localObject4).getValue()).intValue() < i - 30)
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((ArrayList)localObject2).add(((Map.Entry)localObject4).getKey());
          localObject1 = localObject2;
        }
      }
      if (localObject1 != null)
      {
        localObject1 = ((ArrayList)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Integer)((Iterator)localObject1).next();
          this.onlinePrivacy.remove(localObject2);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
          }
        });
      }
    }
    if (this.shortPollChannels.size() != 0)
    {
      i = 0;
      while (i < this.shortPollChannels.size())
      {
        j = this.shortPollChannels.keyAt(i);
        if (this.shortPollChannels.get(j) < System.currentTimeMillis() / 1000L)
        {
          this.shortPollChannels.delete(j);
          if (this.needShortPollChannels.indexOfKey(j) >= 0) {
            getChannelDifference(j);
          }
        }
        i += 1;
      }
    }
    if ((!this.printingUsers.isEmpty()) || (this.lastPrintingStringCount != this.printingUsers.size()))
    {
      k = 0;
      localObject1 = new ArrayList(this.printingUsers.keySet());
      for (i = 0; i < ((ArrayList)localObject1).size(); i = j + 1)
      {
        localObject2 = (Long)((ArrayList)localObject1).get(i);
        localObject3 = (ArrayList)this.printingUsers.get(localObject2);
        int m;
        for (j = 0; j < ((ArrayList)localObject3).size(); j = m + 1)
        {
          localObject4 = (PrintingUser)((ArrayList)localObject3).get(j);
          m = j;
          if (((PrintingUser)localObject4).lastTime + 5900L < l)
          {
            k = 1;
            ((ArrayList)localObject3).remove(localObject4);
            m = j - 1;
          }
        }
        j = i;
        if (((ArrayList)localObject3).isEmpty())
        {
          this.printingUsers.remove(localObject2);
          ((ArrayList)localObject1).remove(i);
          j = i - 1;
        }
      }
      updatePrintingStrings();
      if (k != 0) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
          }
        });
      }
    }
  }
  
  public void uploadAndApplyUserAvatar(TLRPC.PhotoSize paramPhotoSize)
  {
    if (paramPhotoSize != null)
    {
      this.uploadingAvatar = (FileLoader.getInstance().getDirectory(4) + "/" + paramPhotoSize.location.volume_id + "_" + paramPhotoSize.location.local_id + ".jpg");
      FileLoader.getInstance().uploadFile(this.uploadingAvatar, false, true);
    }
  }
  
  public static class PrintingUser
  {
    public TLRPC.SendMessageAction action;
    public long lastTime;
    public int userId;
  }
  
  private class UserActionUpdatesPts
    extends TLRPC.Updates
  {
    private UserActionUpdatesPts() {}
  }
  
  private class UserActionUpdatesSeq
    extends TLRPC.Updates
  {
    private UserActionUpdatesSeq() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MessagesController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */