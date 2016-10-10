package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.query.BotQuery;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.query.MessagesQuery;
import org.telegram.messenger.query.MessagesSearchQuery;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_botCommand;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channels_reportSpam;
import org.telegram.tgnet.TLRPC.TL_chatForbidden;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_getMessageEditData;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardForceReply;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.Holder;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Adapters.StickersAdapter;
import org.telegram.ui.Adapters.StickersAdapter.StickersAdapterDelegate;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.ExtendedGridLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.StickersAlert.StickersAlertDelegate;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.WebFrameLayout;

public class ChatActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int attach_audio = 3;
  private static final int attach_contact = 5;
  private static final int attach_document = 4;
  private static final int attach_gallery = 1;
  private static final int attach_location = 6;
  private static final int attach_photo = 0;
  private static final int attach_video = 2;
  private static final int bot_help = 30;
  private static final int bot_settings = 31;
  private static final int chat_enc_timer = 13;
  private static final int chat_menu_attach = 14;
  private static final int clear_history = 15;
  private static final int copy = 10;
  private static final int delete = 12;
  private static final int delete_chat = 16;
  private static final int edit_done = 20;
  private static final int forward = 11;
  private static final int id_chat_compose_panel = 1000;
  private static final int mute = 18;
  private static final int reply = 19;
  private static final int report = 21;
  private static final int search = 40;
  private static final int share_contact = 17;
  private SimpleTextView actionModeSubTextView;
  private SimpleTextView actionModeTextView;
  private FrameLayout actionModeTitleContainer;
  private ArrayList<View> actionModeViews = new ArrayList();
  private TextView addContactItem;
  private TextView addToContactsButton;
  private TextView alertNameTextView;
  private TextView alertTextView;
  private FrameLayout alertView;
  private AnimatorSet alertViewAnimator;
  private boolean allowContextBotPanel;
  private boolean allowContextBotPanelSecond = true;
  private boolean allowStickersPanel;
  private ActionBarMenuItem attachItem;
  private ChatAvatarContainer avatarContainer;
  private ChatBigEmptyView bigEmptyView;
  private MessageObject botButtons;
  private PhotoViewer.PhotoViewerProvider botContextProvider = new PhotoViewer.PhotoViewerProvider()
  {
    public boolean allowCaption()
    {
      return true;
    }
    
    public boolean cancelButtonPressed()
    {
      return false;
    }
    
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      if ((paramAnonymousInt < 0) || (paramAnonymousInt >= ChatActivity.this.botContextResults.size())) {}
      for (;;)
      {
        return null;
        int i = ChatActivity.this.mentionListView.getChildCount();
        Object localObject2 = ChatActivity.this.botContextResults.get(paramAnonymousInt);
        paramAnonymousInt = 0;
        while (paramAnonymousInt < i)
        {
          paramAnonymousFileLocation = null;
          Object localObject1 = ChatActivity.this.mentionListView.getChildAt(paramAnonymousInt);
          paramAnonymousMessageObject = paramAnonymousFileLocation;
          if ((localObject1 instanceof ContextLinkCell))
          {
            ContextLinkCell localContextLinkCell = (ContextLinkCell)localObject1;
            paramAnonymousMessageObject = paramAnonymousFileLocation;
            if (localContextLinkCell.getResult() == localObject2) {
              paramAnonymousMessageObject = localContextLinkCell.getPhotoImage();
            }
          }
          if (paramAnonymousMessageObject != null)
          {
            paramAnonymousFileLocation = new int[2];
            ((View)localObject1).getLocationInWindow(paramAnonymousFileLocation);
            localObject1 = new PhotoViewer.PlaceProviderObject();
            ((PhotoViewer.PlaceProviderObject)localObject1).viewX = paramAnonymousFileLocation[0];
            ((PhotoViewer.PlaceProviderObject)localObject1).viewY = (paramAnonymousFileLocation[1] - AndroidUtilities.statusBarHeight);
            ((PhotoViewer.PlaceProviderObject)localObject1).parentView = ChatActivity.this.mentionListView;
            ((PhotoViewer.PlaceProviderObject)localObject1).imageReceiver = paramAnonymousMessageObject;
            ((PhotoViewer.PlaceProviderObject)localObject1).thumb = paramAnonymousMessageObject.getBitmap();
            ((PhotoViewer.PlaceProviderObject)localObject1).radius = paramAnonymousMessageObject.getRoundRadius();
            return (PhotoViewer.PlaceProviderObject)localObject1;
          }
          paramAnonymousInt += 1;
        }
      }
    }
    
    public int getSelectedCount()
    {
      return 0;
    }
    
    public Bitmap getThumbForPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      return null;
    }
    
    public boolean isPhotoChecked(int paramAnonymousInt)
    {
      return false;
    }
    
    public void sendButtonPressed(int paramAnonymousInt)
    {
      if ((paramAnonymousInt < 0) || (paramAnonymousInt >= ChatActivity.this.botContextResults.size())) {
        return;
      }
      ChatActivity.this.sendBotInlineResult((TLRPC.BotInlineResult)ChatActivity.this.botContextResults.get(paramAnonymousInt));
    }
    
    public void setPhotoChecked(int paramAnonymousInt) {}
    
    public void updatePhotoAtIndex(int paramAnonymousInt) {}
    
    public void willHidePhotoViewer() {}
    
    public void willSwitchFromPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt) {}
  };
  private ArrayList<Object> botContextResults;
  private HashMap<Integer, TLRPC.BotInfo> botInfo = new HashMap();
  private MessageObject botReplyButtons;
  private String botUser;
  private int botsCount;
  private FrameLayout bottomOverlay;
  private FrameLayout bottomOverlayChat;
  private TextView bottomOverlayChatText;
  private TextView bottomOverlayText;
  private boolean[] cacheEndReached = new boolean[2];
  private int cantDeleteMessagesCount;
  protected ChatActivityEnterView chatActivityEnterView;
  private ChatActivityAdapter chatAdapter;
  private ChatAttachAlert chatAttachAlert;
  private long chatEnterTime = 0L;
  private LinearLayoutManager chatLayoutManager;
  private long chatLeaveTime = 0L;
  private RecyclerListView chatListView;
  private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList();
  private Dialog closeChatDialog;
  protected TLRPC.Chat currentChat;
  protected TLRPC.EncryptedChat currentEncryptedChat;
  private String currentPicturePath;
  protected TLRPC.User currentUser;
  private long dialog_id;
  private ActionBarMenuItem editDoneItem;
  private AnimatorSet editDoneItemAnimation;
  private ContextProgressView editDoneItemProgress;
  private int editingMessageObjectReqId;
  private View emojiButtonRed;
  private FrameLayout emptyViewContainer;
  private boolean[] endReached = new boolean[2];
  private boolean first = true;
  private boolean firstLoading = true;
  private int first_unread_id;
  private boolean forceScrollToTop;
  private boolean[] forwardEndReached = { 1, 1 };
  private ArrayList<MessageObject> forwardingMessages;
  private MessageObject forwaringMessage;
  private ArrayList<CharSequence> foundUrls;
  private TLRPC.WebPage foundWebPage;
  private TextView gifHintTextView;
  private boolean hasBotsCommands;
  private ActionBarMenuItem headerItem;
  private Runnable hideAlertViewRunnable;
  private int highlightMessageId = Integer.MAX_VALUE;
  protected TLRPC.ChatFull info = null;
  private long inlineReturn;
  private boolean isBroadcast;
  private int lastLoadIndex;
  private int last_message_id = 0;
  private int linkSearchRequestId;
  private boolean loading;
  private boolean loadingForward;
  private int loadingPinnedMessage;
  private int loadsCount;
  private int[] maxDate = { Integer.MIN_VALUE, Integer.MIN_VALUE };
  private int[] maxMessageId = { Integer.MAX_VALUE, Integer.MAX_VALUE };
  private FrameLayout mentionContainer;
  private ExtendedGridLayoutManager mentionGridLayoutManager;
  private LinearLayoutManager mentionLayoutManager;
  private AnimatorSet mentionListAnimation;
  private RecyclerListView mentionListView;
  private boolean mentionListViewIgnoreLayout;
  private boolean mentionListViewIsScrolling;
  private int mentionListViewLastViewPosition;
  private int mentionListViewLastViewTop;
  private int mentionListViewScrollOffsetY;
  private MentionsAdapter mentionsAdapter;
  private RecyclerListView.OnItemClickListener mentionsOnItemClickListener;
  private ActionBarMenuItem menuItem;
  private long mergeDialogId;
  protected ArrayList<MessageObject> messages = new ArrayList();
  private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap();
  private HashMap<Integer, MessageObject>[] messagesDict = { new HashMap(), new HashMap() };
  private int[] minDate = new int[2];
  private int[] minMessageId = { Integer.MIN_VALUE, Integer.MIN_VALUE };
  private TextView muteItem;
  private boolean needSelectFromMessageId;
  private int newUnreadMessageCount;
  RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener()
  {
    public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
    {
      if (ChatActivity.this.actionBar.isActionModeShowed())
      {
        ChatActivity.this.processRowSelect(paramAnonymousView);
        return;
      }
      ChatActivity.this.createMenu(paramAnonymousView, true);
    }
  };
  RecyclerListView.OnItemLongClickListener onItemLongClickListener = new RecyclerListView.OnItemLongClickListener()
  {
    public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
    {
      boolean bool = false;
      if (!ChatActivity.this.actionBar.isActionModeShowed())
      {
        ChatActivity.this.createMenu(paramAnonymousView, false);
        bool = true;
      }
      return bool;
    }
  };
  private boolean openAnimationEnded;
  private boolean openSearchKeyboard;
  private Runnable openSecretPhotoRunnable = null;
  private FrameLayout pagedownButton;
  private ObjectAnimator pagedownButtonAnimation;
  private TextView pagedownButtonCounter;
  private boolean pagedownButtonShowedByScroll;
  private boolean paused = true;
  private String pendingLinkSearchString;
  private Runnable pendingWebPageTimeoutRunnable;
  private TLRPC.FileLocation pinnedImageLocation;
  private BackupImageView pinnedMessageImageView;
  private SimpleTextView pinnedMessageNameTextView;
  private MessageObject pinnedMessageObject;
  private SimpleTextView pinnedMessageTextView;
  private FrameLayout pinnedMessageView;
  private AnimatorSet pinnedMessageViewAnimator;
  private PlayerView playerView;
  private FrameLayout progressView;
  private boolean readWhenResume = false;
  private int readWithDate;
  private int readWithMid;
  private AnimatorSet replyButtonAnimation;
  private ImageView replyIconImageView;
  private TLRPC.FileLocation replyImageLocation;
  private BackupImageView replyImageView;
  private SimpleTextView replyNameTextView;
  private SimpleTextView replyObjectTextView;
  private MessageObject replyingMessageObject;
  private TextView reportSpamButton;
  private FrameLayout reportSpamContainer;
  private LinearLayout reportSpamView;
  private AnimatorSet reportSpamViewAnimator;
  private int returnToLoadIndex;
  private int returnToMessageId;
  private AnimatorSet runningAnimation;
  private MessageObject scrollToMessage;
  private int scrollToMessagePosition = 55536;
  private boolean scrollToTopOnResume;
  private boolean scrollToTopUnReadOnResume;
  private FrameLayout searchContainer;
  private SimpleTextView searchCountText;
  private ImageView searchDownButton;
  private ActionBarMenuItem searchItem;
  private ImageView searchUpButton;
  private HashMap<Integer, MessageObject>[] selectedMessagesCanCopyIds = { new HashMap(), new HashMap() };
  private NumberTextView selectedMessagesCountTextView;
  private HashMap<Integer, MessageObject>[] selectedMessagesIds = { new HashMap(), new HashMap() };
  private MessageObject selectedObject;
  private int startLoadFromMessageId;
  private String startVideoEdit = null;
  private float startX = 0.0F;
  private float startY = 0.0F;
  private StickersAdapter stickersAdapter;
  private RecyclerListView stickersListView;
  private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
  private FrameLayout stickersPanel;
  private View timeItem2;
  private MessageObject unreadMessageObject;
  private int unread_to_load;
  private boolean userBlocked = false;
  private Runnable waitingForCharaterEnterRunnable;
  private ArrayList<Integer> waitingForLoad = new ArrayList();
  private boolean waitingForReplyMessageLoad;
  private boolean wasPaused = false;
  
  public ChatActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void addToSelectedMessages(final MessageObject paramMessageObject)
  {
    if (paramMessageObject.getDialogId() == this.dialog_id)
    {
      i = 0;
      if (!this.selectedMessagesIds[i].containsKey(Integer.valueOf(paramMessageObject.getId()))) {
        break label154;
      }
      this.selectedMessagesIds[i].remove(Integer.valueOf(paramMessageObject.getId()));
      if ((paramMessageObject.type == 0) || (paramMessageObject.caption != null)) {
        this.selectedMessagesCanCopyIds[i].remove(Integer.valueOf(paramMessageObject.getId()));
      }
      if (!paramMessageObject.canDeleteMessage(this.currentChat)) {
        this.cantDeleteMessagesCount -= 1;
      }
      label102:
      if (this.actionBar.isActionModeShowed())
      {
        if ((!this.selectedMessagesIds[0].isEmpty()) || (!this.selectedMessagesIds[1].isEmpty())) {
          break label228;
        }
        this.actionBar.hideActionMode();
        updatePinnedMessageView(true);
      }
    }
    label154:
    label228:
    int k;
    label282:
    int m;
    label326:
    do
    {
      return;
      i = 1;
      break;
      this.selectedMessagesIds[i].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
      if ((paramMessageObject.type == 0) || (paramMessageObject.caption != null)) {
        this.selectedMessagesCanCopyIds[i].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
      }
      if (paramMessageObject.canDeleteMessage(this.currentChat)) {
        break label102;
      }
      this.cantDeleteMessagesCount += 1;
      break label102;
      k = this.actionBar.createActionMode().getItem(10).getVisibility();
      paramMessageObject = this.actionBar.createActionMode().getItem(10);
      if (this.selectedMessagesCanCopyIds[0].size() + this.selectedMessagesCanCopyIds[1].size() == 0) {
        break label526;
      }
      i = 0;
      paramMessageObject.setVisibility(i);
      m = this.actionBar.createActionMode().getItem(10).getVisibility();
      paramMessageObject = this.actionBar.createActionMode().getItem(12);
      if (this.cantDeleteMessagesCount != 0) {
        break label532;
      }
      i = 0;
      paramMessageObject.setVisibility(i);
      paramMessageObject = this.actionBar.createActionMode().getItem(19);
    } while (paramMessageObject == null);
    int j = 1;
    if (((this.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) && (!this.isBroadcast))
    {
      i = j;
      if (this.currentChat == null) {
        break label448;
      }
      if (!ChatObject.isNotInChat(this.currentChat))
      {
        i = j;
        if (!ChatObject.isChannel(this.currentChat)) {
          break label448;
        }
        i = j;
        if (this.currentChat.creator) {
          break label448;
        }
        i = j;
        if (this.currentChat.editor) {
          break label448;
        }
        i = j;
        if (this.currentChat.megagroup) {
          break label448;
        }
      }
    }
    final int i = 0;
    label448:
    if ((i != 0) && (this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() == 1))
    {
      i = 0;
      label477:
      if (paramMessageObject.getVisibility() == i) {
        break label542;
      }
      if (this.replyButtonAnimation != null) {
        this.replyButtonAnimation.cancel();
      }
      if (k == m) {
        break label557;
      }
      if (i != 0) {
        break label544;
      }
      paramMessageObject.setAlpha(1.0F);
      paramMessageObject.setScaleX(1.0F);
    }
    for (;;)
    {
      paramMessageObject.setVisibility(i);
      return;
      label526:
      i = 8;
      break label282;
      label532:
      i = 8;
      break label326;
      i = 8;
      break label477;
      label542:
      break;
      label544:
      paramMessageObject.setAlpha(0.0F);
      paramMessageObject.setScaleX(0.0F);
    }
    label557:
    this.replyButtonAnimation = new AnimatorSet();
    paramMessageObject.setPivotX(AndroidUtilities.dp(54.0F));
    if (i == 0)
    {
      paramMessageObject.setVisibility(i);
      this.replyButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(paramMessageObject, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(paramMessageObject, "scaleX", new float[] { 1.0F }) });
    }
    for (;;)
    {
      this.replyButtonAnimation.setDuration(100L);
      this.replyButtonAnimation.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ChatActivity.this.replyButtonAnimation != null) && (ChatActivity.this.replyButtonAnimation.equals(paramAnonymousAnimator))) {
            ChatActivity.access$12202(ChatActivity.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ChatActivity.this.replyButtonAnimation != null) && (ChatActivity.this.replyButtonAnimation.equals(paramAnonymousAnimator)) && (i == 8)) {
            paramMessageObject.setVisibility(8);
          }
        }
      });
      this.replyButtonAnimation.start();
      return;
      this.replyButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(paramMessageObject, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(paramMessageObject, "scaleX", new float[] { 0.0F }) });
    }
  }
  
  private void alertUserOpenError(MessageObject paramMessageObject)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
    if (paramMessageObject.type == 3) {
      localBuilder.setMessage(LocaleController.getString("NoPlayerInstalled", 2131165944));
    }
    for (;;)
    {
      showDialog(localBuilder.create());
      return;
      localBuilder.setMessage(LocaleController.formatString("NoHandleAppInstalled", 2131165934, new Object[] { paramMessageObject.getDocument().mime_type }));
    }
  }
  
  private void applyDraftMaybe(boolean paramBoolean)
  {
    if (this.chatActivityEnterView == null) {}
    label70:
    label111:
    label173:
    label306:
    label311:
    label323:
    label439:
    label495:
    label541:
    label551:
    label581:
    for (;;)
    {
      return;
      TLRPC.DraftMessage localDraftMessage = DraftQuery.getDraft(this.dialog_id);
      TLRPC.Message localMessage;
      Object localObject;
      int k;
      int j;
      TLRPC.MessageEntity localMessageEntity;
      int i;
      if ((localDraftMessage != null) && (localDraftMessage.reply_to_msg_id != 0))
      {
        localMessage = DraftQuery.getDraftMessage(this.dialog_id);
        if (this.chatActivityEnterView.getFieldText() != null) {
          break label551;
        }
        if (localDraftMessage == null) {
          break label495;
        }
        localObject = this.chatActivityEnterView;
        if (localDraftMessage.no_webpage) {
          break label306;
        }
        paramBoolean = true;
        ((ChatActivityEnterView)localObject).setWebPage(null, paramBoolean);
        if (localDraftMessage.entities.isEmpty()) {
          break label541;
        }
        localObject = SpannableStringBuilder.valueOf(localDraftMessage.message);
        MessagesQuery.sortEntities(localDraftMessage.entities);
        k = 0;
        j = 0;
        if (j >= localDraftMessage.entities.size()) {
          break label439;
        }
        localMessageEntity = (TLRPC.MessageEntity)localDraftMessage.entities.get(j);
        if ((!(localMessageEntity instanceof TLRPC.TL_inputMessageEntityMentionName)) && (!(localMessageEntity instanceof TLRPC.TL_messageEntityMentionName))) {
          break label323;
        }
        if (!(localMessageEntity instanceof TLRPC.TL_inputMessageEntityMentionName)) {
          break label311;
        }
        i = ((TLRPC.TL_inputMessageEntityMentionName)localMessageEntity).user_id.user_id;
        if ((localMessageEntity.offset + k + localMessageEntity.length < ((SpannableStringBuilder)localObject).length()) && (((SpannableStringBuilder)localObject).charAt(localMessageEntity.offset + k + localMessageEntity.length) == ' ')) {
          localMessageEntity.length += 1;
        }
        ((SpannableStringBuilder)localObject).setSpan(new URLSpanUserMention("" + i), localMessageEntity.offset + k, localMessageEntity.offset + k + localMessageEntity.length, 33);
        i = k;
      }
      for (;;)
      {
        j += 1;
        k = i;
        break label111;
        localMessage = null;
        break;
        paramBoolean = false;
        break label70;
        i = ((TLRPC.TL_messageEntityMentionName)localMessageEntity).user_id;
        break label173;
        if ((localMessageEntity instanceof TLRPC.TL_messageEntityCode))
        {
          ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + localMessageEntity.length + k, "`");
          ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + k, "`");
          i = k + 2;
        }
        else
        {
          i = k;
          if ((localMessageEntity instanceof TLRPC.TL_messageEntityPre))
          {
            ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + localMessageEntity.length + k, "```");
            ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + k, "```");
            i = k + 6;
          }
        }
      }
      this.chatActivityEnterView.setFieldText((CharSequence)localObject);
      if (getArguments().getBoolean("hasUrl", false))
      {
        this.chatActivityEnterView.setSelection(localDraftMessage.message.indexOf('\n') + 1);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (ChatActivity.this.chatActivityEnterView != null)
            {
              ChatActivity.this.chatActivityEnterView.setFieldFocused(true);
              ChatActivity.this.chatActivityEnterView.openKeyboard();
            }
          }
        }, 700L);
      }
      for (;;)
      {
        if ((this.replyingMessageObject != null) || (localMessage == null)) {
          break label581;
        }
        this.replyingMessageObject = new MessageObject(localMessage, MessagesController.getInstance().getUsers(), false);
        showReplyPanel(true, this.replyingMessageObject, null, null, false, false);
        return;
        localObject = localDraftMessage.message;
        break;
        if ((paramBoolean) && (localDraftMessage == null))
        {
          this.chatActivityEnterView.setFieldText("");
          showReplyPanel(false, null, null, null, false, true);
        }
      }
    }
  }
  
  private void checkActionBarMenu()
  {
    if (((this.currentEncryptedChat != null) && (!(this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat))) || ((this.currentChat != null) && (ChatObject.isNotInChat(this.currentChat))) || ((this.currentUser != null) && (UserObject.isDeleted(this.currentUser))))
    {
      if (this.menuItem != null) {
        this.menuItem.setVisibility(8);
      }
      if (this.timeItem2 != null) {
        this.timeItem2.setVisibility(8);
      }
      if (this.avatarContainer != null) {
        this.avatarContainer.hideTimeItem();
      }
    }
    for (;;)
    {
      if ((this.avatarContainer != null) && (this.currentEncryptedChat != null)) {
        this.avatarContainer.setTime(this.currentEncryptedChat.ttl);
      }
      checkAndUpdateAvatar();
      return;
      if (this.menuItem != null) {
        this.menuItem.setVisibility(0);
      }
      if (this.timeItem2 != null) {
        this.timeItem2.setVisibility(0);
      }
      if (this.avatarContainer != null) {
        this.avatarContainer.showTimeItem();
      }
    }
  }
  
  private void checkAndUpdateAvatar()
  {
    Object localObject;
    if (this.currentUser != null)
    {
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
      if (localObject != null) {}
    }
    label83:
    for (;;)
    {
      return;
      this.currentUser = ((TLRPC.User)localObject);
      for (;;)
      {
        if (this.avatarContainer == null) {
          break label83;
        }
        this.avatarContainer.checkAndUpdateAvatar();
        return;
        if (this.currentChat != null)
        {
          localObject = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
          if (localObject == null) {
            break;
          }
          this.currentChat = ((TLRPC.Chat)localObject);
        }
      }
    }
  }
  
  private void checkBotCommands()
  {
    boolean bool = true;
    URLSpanBotCommand.enabled = false;
    if ((this.currentUser != null) && (this.currentUser.bot)) {
      URLSpanBotCommand.enabled = true;
    }
    do
    {
      for (;;)
      {
        return;
        if (!(this.info instanceof TLRPC.TL_chatFull)) {
          break;
        }
        int i = 0;
        while (i < this.info.participants.participants.size())
        {
          Object localObject = (TLRPC.ChatParticipant)this.info.participants.participants.get(i);
          localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
          if ((localObject != null) && (((TLRPC.User)localObject).bot))
          {
            URLSpanBotCommand.enabled = true;
            return;
          }
          i += 1;
        }
      }
    } while (!(this.info instanceof TLRPC.TL_channelFull));
    if (!this.info.bot_info.isEmpty()) {}
    for (;;)
    {
      URLSpanBotCommand.enabled = bool;
      return;
      bool = false;
    }
  }
  
  private void checkContextBotPanel()
  {
    if ((this.allowStickersPanel) && (this.mentionsAdapter != null) && (this.mentionsAdapter.isBotContext()))
    {
      if ((this.allowContextBotPanel) || (this.allowContextBotPanelSecond)) {
        break label159;
      }
      if ((this.mentionContainer.getVisibility() == 0) && (this.mentionContainer.getTag() == null))
      {
        if (this.mentionListAnimation != null) {
          this.mentionListAnimation.cancel();
        }
        this.mentionContainer.setTag(Integer.valueOf(1));
        this.mentionListAnimation = new AnimatorSet();
        this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.mentionContainer, "alpha", new float[] { 0.0F }) });
        this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymousAnimator))) {
              ChatActivity.access$8002(ChatActivity.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymousAnimator)))
            {
              ChatActivity.this.mentionContainer.setVisibility(4);
              ChatActivity.access$8002(ChatActivity.this, null);
            }
          }
        });
        this.mentionListAnimation.setDuration(200L);
        this.mentionListAnimation.start();
      }
    }
    label159:
    while ((this.mentionContainer.getVisibility() != 4) && (this.mentionContainer.getTag() == null)) {
      return;
    }
    if (this.mentionListAnimation != null) {
      this.mentionListAnimation.cancel();
    }
    this.mentionContainer.setTag(null);
    this.mentionContainer.setVisibility(0);
    this.mentionListAnimation = new AnimatorSet();
    this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.mentionContainer, "alpha", new float[] { 0.0F, 1.0F }) });
    this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymousAnimator))) {
          ChatActivity.access$8002(ChatActivity.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymousAnimator))) {
          ChatActivity.access$8002(ChatActivity.this, null);
        }
      }
    });
    this.mentionListAnimation.setDuration(200L);
    this.mentionListAnimation.start();
  }
  
  private void checkEditTimer()
  {
    if (this.chatActivityEnterView == null) {}
    MessageObject localMessageObject;
    do
    {
      do
      {
        return;
        localMessageObject = this.chatActivityEnterView.getEditingMessageObject();
      } while (localMessageObject == null);
      if ((this.currentUser == null) || (!this.currentUser.self)) {
        break;
      }
    } while (this.actionModeSubTextView.getVisibility() == 8);
    this.actionModeSubTextView.setVisibility(8);
    return;
    int i = MessagesController.getInstance().maxEditTime + 300 - Math.abs(ConnectionsManager.getInstance().getCurrentTime() - localMessageObject.messageOwner.date);
    if (i > 0)
    {
      if (i > 300) {
        if (this.actionModeSubTextView.getVisibility() != 8) {
          this.actionModeSubTextView.setVisibility(8);
        }
      }
      for (;;)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            ChatActivity.this.checkEditTimer();
          }
        }, 1000L);
        return;
        if (this.actionModeSubTextView.getVisibility() != 0) {
          this.actionModeSubTextView.setVisibility(0);
        }
        this.actionModeSubTextView.setText(LocaleController.formatString("TimeToEdit", 2131166332, new Object[] { String.format("%d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60) }) }));
      }
    }
    this.editDoneItem.setVisibility(8);
    this.actionModeSubTextView.setText(LocaleController.formatString("TimeToEditExpired", 2131166333, new Object[0]));
  }
  
  private void checkListViewPaddings()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int k = 0;
        try
        {
          int j = ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition();
          int i = 0;
          View localView;
          if (j != -1)
          {
            localView = ChatActivity.this.chatLayoutManager.findViewByPosition(j);
            if (localView == null)
            {
              i = k;
              i -= ChatActivity.this.chatListView.getPaddingTop();
            }
          }
          else
          {
            if ((ChatActivity.this.chatListView.getPaddingTop() == AndroidUtilities.dp(52.0F)) || (((ChatActivity.this.pinnedMessageView == null) || (ChatActivity.this.pinnedMessageView.getTag() != null)) && ((ChatActivity.this.reportSpamView == null) || (ChatActivity.this.reportSpamView.getTag() != null)))) {
              break label189;
            }
            ChatActivity.this.chatListView.setPadding(0, AndroidUtilities.dp(52.0F), 0, AndroidUtilities.dp(3.0F));
            ChatActivity.this.chatListView.setTopGlowOffset(AndroidUtilities.dp(48.0F));
            i -= AndroidUtilities.dp(48.0F);
          }
          for (;;)
          {
            if (j == -1) {
              return;
            }
            ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(j, i);
            return;
            i = localView.getTop();
            break;
            label189:
            if ((ChatActivity.this.chatListView.getPaddingTop() != AndroidUtilities.dp(4.0F)) && ((ChatActivity.this.pinnedMessageView == null) || (ChatActivity.this.pinnedMessageView.getTag() != null)) && ((ChatActivity.this.reportSpamView == null) || (ChatActivity.this.reportSpamView.getTag() != null)))
            {
              ChatActivity.this.chatListView.setPadding(0, AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(3.0F));
              ChatActivity.this.chatListView.setTopGlowOffset(0);
              k = AndroidUtilities.dp(48.0F);
              i += k;
            }
            else
            {
              j = -1;
            }
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  private void checkRaiseSensors()
  {
    if ((!ApplicationLoader.mainInterfacePaused) && ((this.bottomOverlayChat == null) || (this.bottomOverlayChat.getVisibility() != 0)) && ((this.bottomOverlay == null) || (this.bottomOverlay.getVisibility() != 0)) && ((this.searchContainer == null) || (this.searchContainer.getVisibility() != 0)))
    {
      MediaController.getInstance().setAllowStartRecord(true);
      return;
    }
    MediaController.getInstance().setAllowStartRecord(false);
  }
  
  private void checkScrollForLoad(boolean paramBoolean)
  {
    if ((this.chatLayoutManager == null) || (this.paused)) {}
    label32:
    label52:
    label133:
    label191:
    label338:
    label340:
    label345:
    label350:
    label436:
    label564:
    label569:
    do
    {
      return;
      k = this.chatLayoutManager.findFirstVisibleItemPosition();
      int n;
      int i1;
      boolean bool;
      int i2;
      if (k == -1)
      {
        i = 0;
        if (i <= 0) {
          break label338;
        }
        m = this.chatAdapter.getItemCount();
        if (!paramBoolean) {
          break label340;
        }
        j = 25;
        if ((k <= j) && (!this.loading))
        {
          if (this.endReached[0] != 0) {
            break label436;
          }
          this.loading = true;
          this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
          if (this.messagesByDays.size() == 0) {
            break label350;
          }
          localMessagesController = MessagesController.getInstance();
          l = this.dialog_id;
          j = this.maxMessageId[0];
          if (this.cacheEndReached[0] != 0) {
            break label345;
          }
          paramBoolean = true;
          n = this.minDate[0];
          i1 = this.classGuid;
          bool = ChatObject.isChannel(this.currentChat);
          i2 = this.lastLoadIndex;
          this.lastLoadIndex = (i2 + 1);
          localMessagesController.loadMessages(l, 50, j, paramBoolean, n, i1, 0, 0, bool, i2);
        }
      }
      for (;;)
      {
        if ((!this.loadingForward) && (k + i >= m - 10))
        {
          if ((this.mergeDialogId == 0L) || (this.forwardEndReached[1] != 0)) {
            break label569;
          }
          this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
          localMessagesController = MessagesController.getInstance();
          l = this.mergeDialogId;
          i = this.minMessageId[1];
          j = this.maxDate[1];
          k = this.classGuid;
          paramBoolean = ChatObject.isChannel(this.currentChat);
          m = this.lastLoadIndex;
          this.lastLoadIndex = (m + 1);
          localMessagesController.loadMessages(l, 50, i, true, j, k, 1, 0, paramBoolean, m);
          this.loadingForward = true;
          return;
          i = Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - k) + 1;
          break label32;
          break;
          j = 5;
          break label52;
          paramBoolean = false;
          break label133;
          localMessagesController = MessagesController.getInstance();
          l = this.dialog_id;
          if (this.cacheEndReached[0] == 0) {}
          for (paramBoolean = true;; paramBoolean = false)
          {
            j = this.minDate[0];
            n = this.classGuid;
            bool = ChatObject.isChannel(this.currentChat);
            i1 = this.lastLoadIndex;
            this.lastLoadIndex = (i1 + 1);
            localMessagesController.loadMessages(l, 50, 0, paramBoolean, j, n, 0, 0, bool, i1);
            break;
          }
          if ((this.mergeDialogId != 0L) && (this.endReached[1] == 0))
          {
            this.loading = true;
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            localMessagesController = MessagesController.getInstance();
            l = this.mergeDialogId;
            j = this.maxMessageId[1];
            if (this.cacheEndReached[1] != 0) {
              break label564;
            }
          }
        }
      }
      for (paramBoolean = true;; paramBoolean = false)
      {
        n = this.minDate[1];
        i1 = this.classGuid;
        bool = ChatObject.isChannel(this.currentChat);
        i2 = this.lastLoadIndex;
        this.lastLoadIndex = (i2 + 1);
        localMessagesController.loadMessages(l, 50, j, paramBoolean, n, i1, 0, 0, bool, i2);
        break label191;
        break;
      }
    } while (this.forwardEndReached[0] != 0);
    this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
    MessagesController localMessagesController = MessagesController.getInstance();
    long l = this.dialog_id;
    int i = this.minMessageId[0];
    int j = this.maxDate[0];
    int k = this.classGuid;
    paramBoolean = ChatObject.isChannel(this.currentChat);
    int m = this.lastLoadIndex;
    this.lastLoadIndex = (m + 1);
    localMessagesController.loadMessages(l, 50, i, true, j, k, 1, 0, paramBoolean, m);
    this.loadingForward = true;
  }
  
  private void clearChatData()
  {
    this.messages.clear();
    this.messagesByDays.clear();
    this.waitingForLoad.clear();
    FrameLayout localFrameLayout = this.progressView;
    int i;
    if (this.chatAdapter.botInfoRow == -1)
    {
      i = 0;
      localFrameLayout.setVisibility(i);
      this.chatListView.setEmptyView(null);
      i = 0;
      label54:
      if (i >= 2) {
        break label163;
      }
      this.messagesDict[i].clear();
      if (this.currentEncryptedChat != null) {
        break label142;
      }
      this.maxMessageId[i] = Integer.MAX_VALUE;
      this.minMessageId[i] = Integer.MIN_VALUE;
    }
    for (;;)
    {
      this.maxDate[i] = Integer.MIN_VALUE;
      this.minDate[i] = 0;
      this.endReached[i] = false;
      this.cacheEndReached[i] = false;
      this.forwardEndReached[i] = true;
      i += 1;
      break label54;
      i = 4;
      break;
      label142:
      this.maxMessageId[i] = Integer.MIN_VALUE;
      this.minMessageId[i] = Integer.MAX_VALUE;
    }
    label163:
    this.first = true;
    this.firstLoading = true;
    this.loading = true;
    this.loadingForward = false;
    this.waitingForReplyMessageLoad = false;
    this.startLoadFromMessageId = 0;
    this.last_message_id = 0;
    this.needSelectFromMessageId = false;
    this.chatAdapter.notifyDataSetChanged();
  }
  
  private void createChatAttachView()
  {
    if (getParentActivity() == null) {}
    while (this.chatAttachAlert != null) {
      return;
    }
    this.chatAttachAlert = new ChatAttachAlert(getParentActivity(), this);
    this.chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate()
    {
      public void didPressedButton(int paramAnonymousInt)
      {
        if (ChatActivity.this.getParentActivity() == null) {}
        Object localObject;
        do
        {
          return;
          if (paramAnonymousInt != 7) {
            break;
          }
          ChatActivity.this.chatAttachAlert.dismiss();
          localObject = ChatActivity.this.chatAttachAlert.getSelectedPhotos();
        } while (((HashMap)localObject).isEmpty());
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        ArrayList localArrayList3 = new ArrayList();
        Iterator localIterator = ((HashMap)localObject).entrySet().iterator();
        if (localIterator.hasNext())
        {
          MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)((Map.Entry)localIterator.next()).getValue();
          if (localPhotoEntry.imagePath != null)
          {
            localArrayList1.add(localPhotoEntry.imagePath);
            if (localPhotoEntry.caption != null)
            {
              localObject = localPhotoEntry.caption.toString();
              localArrayList2.add(localObject);
              if (localPhotoEntry.stickers.isEmpty()) {
                break label221;
              }
              localObject = new ArrayList(localPhotoEntry.stickers);
              localArrayList3.add(localObject);
            }
          }
          label221:
          while (localPhotoEntry.path == null) {
            for (;;)
            {
              localPhotoEntry.imagePath = null;
              localPhotoEntry.thumbPath = null;
              localPhotoEntry.caption = null;
              localPhotoEntry.stickers.clear();
              break;
              localObject = null;
              continue;
              localObject = null;
            }
          }
          localArrayList1.add(localPhotoEntry.path);
          if (localPhotoEntry.caption != null)
          {
            localObject = localPhotoEntry.caption.toString();
            label263:
            localArrayList2.add(localObject);
            if (localPhotoEntry.stickers.isEmpty()) {
              break label309;
            }
          }
          label309:
          for (localObject = new ArrayList(localPhotoEntry.stickers);; localObject = null)
          {
            localArrayList3.add(localObject);
            break;
            localObject = null;
            break label263;
          }
        }
        SendMessagesHelper.prepareSendingPhotos(localArrayList1, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, localArrayList2, localArrayList3);
        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
        return;
        if (ChatActivity.this.chatAttachAlert != null) {
          ChatActivity.this.chatAttachAlert.dismissWithButtonClick(paramAnonymousInt);
        }
        ChatActivity.this.processSelectedAttach(paramAnonymousInt);
      }
      
      public void didSelectBot(TLRPC.User paramAnonymousUser)
      {
        if ((ChatActivity.this.chatActivityEnterView == null) || (paramAnonymousUser.username == null) || (paramAnonymousUser.username.length() == 0)) {
          return;
        }
        ChatActivity.this.chatActivityEnterView.setFieldText("@" + paramAnonymousUser.username + " ");
        ChatActivity.this.chatActivityEnterView.openKeyboard();
      }
      
      public View getRevealView()
      {
        return ChatActivity.this.menuItem;
      }
    });
  }
  
  private void createDeleteMessagesAlert(final MessageObject paramMessageObject)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    int i;
    final boolean[] arrayOfBoolean;
    Object localObject2;
    FrameLayout localFrameLayout;
    final Object localObject1;
    label201:
    label259:
    int j;
    if (paramMessageObject != null)
    {
      i = 1;
      localBuilder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", 2131165320, new Object[] { LocaleController.formatPluralString("messages", i) }));
      localBuilder.setTitle(LocaleController.getString("Message", 2131165871));
      arrayOfBoolean = new boolean[3];
      localObject2 = null;
      localFrameLayout = null;
      localObject1 = localObject2;
      if (this.currentChat == null) {
        break label603;
      }
      localObject1 = localObject2;
      if (!this.currentChat.megagroup) {
        break label603;
      }
      if (paramMessageObject == null) {
        break label364;
      }
      if (paramMessageObject.messageOwner.action != null)
      {
        localObject1 = localFrameLayout;
        if (!(paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)) {}
      }
      else
      {
        localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      }
      if ((localObject1 == null) || (((TLRPC.User)localObject1).id == UserConfig.getClientUserId())) {
        break label658;
      }
      localFrameLayout = new FrameLayout(getParentActivity());
      if (Build.VERSION.SDK_INT >= 21) {
        localFrameLayout.setPadding(0, AndroidUtilities.dp(8.0F), 0, 0);
      }
      i = 0;
      if (i >= 3) {
        break label595;
      }
      localObject2 = new CheckBoxCell(getParentActivity());
      ((CheckBoxCell)localObject2).setBackgroundResource(2130837796);
      ((CheckBoxCell)localObject2).setTag(Integer.valueOf(i));
      if (i != 0) {
        break label505;
      }
      ((CheckBoxCell)localObject2).setText(LocaleController.getString("DeleteBanUser", 2131165568), "", false, false);
      if (!LocaleController.isRTL) {
        break label579;
      }
      j = AndroidUtilities.dp(8.0F);
      label272:
      if (!LocaleController.isRTL) {
        break label584;
      }
    }
    label364:
    label464:
    label505:
    label579:
    label584:
    for (int k = 0;; k = AndroidUtilities.dp(8.0F))
    {
      ((CheckBoxCell)localObject2).setPadding(j, 0, k, 0);
      localFrameLayout.addView((View)localObject2, LayoutHelper.createFrame(-1, 48.0F, 51, 8.0F, i * 48, 8.0F, 0.0F));
      ((CheckBoxCell)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = (CheckBoxCell)paramAnonymousView;
          Integer localInteger = (Integer)paramAnonymousView.getTag();
          boolean[] arrayOfBoolean = arrayOfBoolean;
          int i = localInteger.intValue();
          if (arrayOfBoolean[localInteger.intValue()] == 0) {}
          for (int j = 1;; j = 0)
          {
            arrayOfBoolean[i] = j;
            paramAnonymousView.setChecked(arrayOfBoolean[localInteger.intValue()], true);
            return;
          }
        }
      });
      i += 1;
      break label201;
      i = this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size();
      break;
      i = -1;
      k = 1;
      for (;;)
      {
        j = i;
        if (k >= 0)
        {
          localObject1 = this.selectedMessagesIds[k].entrySet().iterator();
          j = i;
          do
          {
            i = j;
            if (!((Iterator)localObject1).hasNext()) {
              break label464;
            }
            localObject2 = (MessageObject)((Map.Entry)((Iterator)localObject1).next()).getValue();
            i = j;
            if (j == -1) {
              i = ((MessageObject)localObject2).messageOwner.from_id;
            }
            if (i < 0) {
              break;
            }
            j = i;
          } while (i == ((MessageObject)localObject2).messageOwner.from_id);
          i = -2;
          if (i == -2) {
            j = i;
          }
        }
        else
        {
          localObject1 = localFrameLayout;
          if (j == -1) {
            break;
          }
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(j));
          break;
        }
        k -= 1;
      }
      if (i == 1)
      {
        ((CheckBoxCell)localObject2).setText(LocaleController.getString("DeleteReportSpam", 2131165577), "", false, false);
        break label259;
      }
      if (i != 2) {
        break label259;
      }
      ((CheckBoxCell)localObject2).setText(LocaleController.formatString("DeleteAllFrom", 2131165565, new Object[] { ContactsController.formatName(((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).last_name) }), "", false, false);
      break label259;
      j = 0;
      break label272;
    }
    label595:
    localBuilder.setView(localFrameLayout);
    for (;;)
    {
      label603:
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = null;
          Object localObject1;
          Object localObject2;
          if (paramMessageObject != null)
          {
            localObject1 = new ArrayList();
            ((ArrayList)localObject1).add(Integer.valueOf(paramMessageObject.getId()));
            localObject2 = null;
            paramAnonymousDialogInterface = (DialogInterface)localObject2;
            if (ChatActivity.this.currentEncryptedChat != null)
            {
              paramAnonymousDialogInterface = (DialogInterface)localObject2;
              if (paramMessageObject.messageOwner.random_id != 0L)
              {
                paramAnonymousDialogInterface = (DialogInterface)localObject2;
                if (paramMessageObject.type != 10)
                {
                  paramAnonymousDialogInterface = new ArrayList();
                  paramAnonymousDialogInterface.add(Long.valueOf(paramMessageObject.messageOwner.random_id));
                }
              }
            }
            MessagesController.getInstance().deleteMessages((ArrayList)localObject1, paramAnonymousDialogInterface, ChatActivity.this.currentEncryptedChat, paramMessageObject.messageOwner.to_id.channel_id);
            paramAnonymousDialogInterface = (DialogInterface)localObject1;
          }
          for (;;)
          {
            if (localObject1 != null)
            {
              if (arrayOfBoolean[0] != 0) {
                MessagesController.getInstance().deleteUserFromChat(ChatActivity.this.currentChat.id, localObject1, ChatActivity.this.info);
              }
              if (arrayOfBoolean[1] != 0)
              {
                localObject1 = new TLRPC.TL_channels_reportSpam();
                ((TLRPC.TL_channels_reportSpam)localObject1).channel = MessagesController.getInputChannel(ChatActivity.this.currentChat);
                ((TLRPC.TL_channels_reportSpam)localObject1).user_id = MessagesController.getInputUser(localObject1);
                ((TLRPC.TL_channels_reportSpam)localObject1).id = paramAnonymousDialogInterface;
                ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
                {
                  public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error) {}
                });
              }
              if (arrayOfBoolean[2] != 0) {
                MessagesController.getInstance().deleteUserChannelHistory(ChatActivity.this.currentChat, localObject1, 0);
              }
            }
            return;
            paramAnonymousInt = 1;
            while (paramAnonymousInt >= 0)
            {
              localObject1 = new ArrayList(ChatActivity.this.selectedMessagesIds[paramAnonymousInt].keySet());
              paramAnonymousDialogInterface = null;
              int j = 0;
              int i = j;
              if (!((ArrayList)localObject1).isEmpty())
              {
                localObject2 = (MessageObject)ChatActivity.this.selectedMessagesIds[paramAnonymousInt].get(((ArrayList)localObject1).get(0));
                i = j;
                if (0 == 0)
                {
                  i = j;
                  if (((MessageObject)localObject2).messageOwner.to_id.channel_id != 0) {
                    i = ((MessageObject)localObject2).messageOwner.to_id.channel_id;
                  }
                }
              }
              if (ChatActivity.this.currentEncryptedChat != null)
              {
                localObject2 = new ArrayList();
                Iterator localIterator = ChatActivity.this.selectedMessagesIds[paramAnonymousInt].entrySet().iterator();
                for (;;)
                {
                  paramAnonymousDialogInterface = (DialogInterface)localObject2;
                  if (!localIterator.hasNext()) {
                    break;
                  }
                  paramAnonymousDialogInterface = (MessageObject)((Map.Entry)localIterator.next()).getValue();
                  if ((paramAnonymousDialogInterface.messageOwner.random_id != 0L) && (paramAnonymousDialogInterface.type != 10)) {
                    ((ArrayList)localObject2).add(Long.valueOf(paramAnonymousDialogInterface.messageOwner.random_id));
                  }
                }
              }
              MessagesController.getInstance().deleteMessages((ArrayList)localObject1, paramAnonymousDialogInterface, ChatActivity.this.currentEncryptedChat, i);
              paramAnonymousInt -= 1;
              paramAnonymousDialogInterface = (DialogInterface)localObject1;
            }
            ChatActivity.this.actionBar.hideActionMode();
            ChatActivity.this.updatePinnedMessageView(true);
          }
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
      showDialog(localBuilder.create());
      return;
      label658:
      localObject1 = null;
    }
  }
  
  private void createMenu(View paramView, boolean paramBoolean)
  {
    if (this.actionBar.isActionModeShowed()) {}
    MessageObject localMessageObject;
    int i1;
    label96:
    int j;
    label303:
    int k;
    label341:
    int m;
    label484:
    do
    {
      do
      {
        for (;;)
        {
          return;
          localMessageObject = null;
          if ((paramView instanceof ChatMessageCell)) {
            localMessageObject = ((ChatMessageCell)paramView).getMessageObject();
          }
          while (localMessageObject != null)
          {
            i1 = getMessageType(localMessageObject);
            if ((!paramBoolean) || (!(localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))) {
              break label96;
            }
            scrollToMessageId(localMessageObject.messageOwner.reply_to_msg_id, 0, true, 0);
            return;
            if ((paramView instanceof ChatActionCell)) {
              localMessageObject = ((ChatActionCell)paramView).getMessageObject();
            }
          }
        }
        this.selectedObject = null;
        this.forwaringMessage = null;
        i = 1;
        while (i >= 0)
        {
          this.selectedMessagesCanCopyIds[i].clear();
          this.selectedMessagesIds[i].clear();
          i -= 1;
        }
        this.cantDeleteMessagesCount = 0;
        this.actionBar.hideActionMode();
        updatePinnedMessageView(true);
        int n = 1;
        if ((localMessageObject.getDialogId() == this.mergeDialogId) || (localMessageObject.getId() <= 0) || (!ChatObject.isChannel(this.currentChat)) || (!this.currentChat.megagroup) || ((!this.currentChat.creator) && (!this.currentChat.editor)) || ((localMessageObject.messageOwner.action != null) && (!(localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)))) {
          break;
        }
        i = 1;
        if ((localMessageObject.getDialogId() == this.mergeDialogId) || (this.info == null) || (this.info.pinned_msg_id != localMessageObject.getId()) || ((!this.currentChat.creator) && (!this.currentChat.editor))) {
          break label672;
        }
        j = 1;
        if ((!localMessageObject.canEditMessage(this.currentChat)) || (this.chatActivityEnterView.hasAudioToSend()) || (localMessageObject.getDialogId() == this.mergeDialogId)) {
          break label678;
        }
        k = 1;
        if (((this.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) && ((i1 != 1) || (localMessageObject.getDialogId() != this.mergeDialogId)) && ((this.currentEncryptedChat != null) || (localMessageObject.getId() >= 0)) && (!this.isBroadcast))
        {
          m = n;
          if (this.currentChat == null) {
            break label484;
          }
          if (!ChatObject.isNotInChat(this.currentChat))
          {
            m = n;
            if (!ChatObject.isChannel(this.currentChat)) {
              break label484;
            }
            m = n;
            if (this.currentChat.creator) {
              break label484;
            }
            m = n;
            if (this.currentChat.editor) {
              break label484;
            }
            m = n;
            if (this.currentChat.megagroup) {
              break label484;
            }
          }
        }
        m = 0;
        if ((!paramBoolean) && (i1 >= 2) && (i1 != 20)) {
          break label2421;
        }
      } while (i1 < 0);
      this.selectedObject = localMessageObject;
    } while (getParentActivity() == null);
    paramView = new AlertDialog.Builder(getParentActivity());
    Object localObject1 = new ArrayList();
    final Object localObject2 = new ArrayList();
    if (i1 == 0)
    {
      ((ArrayList)localObject1).add(LocaleController.getString("Retry", 2131166189));
      ((ArrayList)localObject2).add(Integer.valueOf(0));
      ((ArrayList)localObject1).add(LocaleController.getString("Delete", 2131165560));
      ((ArrayList)localObject2).add(Integer.valueOf(1));
    }
    for (;;)
    {
      label604:
      if (!((ArrayList)localObject2).isEmpty())
      {
        paramView.setItems((CharSequence[])((ArrayList)localObject1).toArray(new CharSequence[((ArrayList)localObject1).size()]), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if ((ChatActivity.this.selectedObject == null) || (paramAnonymousInt < 0) || (paramAnonymousInt >= localObject2.size())) {
              return;
            }
            ChatActivity.this.processSelectedOption(((Integer)localObject2.get(paramAnonymousInt)).intValue());
          }
        });
        paramView.setTitle(LocaleController.getString("Message", 2131165871));
        showDialog(paramView.create());
        return;
        i = 0;
        break;
        label672:
        j = 0;
        break label303;
        label678:
        k = 0;
        break label341;
        if (i1 == 1)
        {
          if ((this.currentChat != null) && (!this.isBroadcast))
          {
            if (m != 0)
            {
              ((ArrayList)localObject1).add(LocaleController.getString("Reply", 2131166156));
              ((ArrayList)localObject2).add(Integer.valueOf(8));
            }
            if (j != 0)
            {
              ((ArrayList)localObject1).add(LocaleController.getString("UnpinMessage", 2131166356));
              ((ArrayList)localObject2).add(Integer.valueOf(14));
            }
            for (;;)
            {
              if (k != 0)
              {
                ((ArrayList)localObject1).add(LocaleController.getString("Edit", 2131165592));
                ((ArrayList)localObject2).add(Integer.valueOf(12));
              }
              if (!localMessageObject.canDeleteMessage(this.currentChat)) {
                break;
              }
              ((ArrayList)localObject1).add(LocaleController.getString("Delete", 2131165560));
              ((ArrayList)localObject2).add(Integer.valueOf(1));
              break;
              if (i != 0)
              {
                ((ArrayList)localObject1).add(LocaleController.getString("PinMessage", 2131166122));
                ((ArrayList)localObject2).add(Integer.valueOf(13));
              }
            }
          }
          if ((paramBoolean) && (this.selectedObject.getId() > 0) && (m != 0))
          {
            ((ArrayList)localObject1).add(LocaleController.getString("Reply", 2131166156));
            ((ArrayList)localObject2).add(Integer.valueOf(8));
          }
          if (localMessageObject.canDeleteMessage(this.currentChat))
          {
            ((ArrayList)localObject1).add(LocaleController.getString("Delete", 2131165560));
            ((ArrayList)localObject2).add(Integer.valueOf(1));
          }
        }
        else if (i1 == 20)
        {
          ((ArrayList)localObject1).add(LocaleController.getString("Retry", 2131166189));
          ((ArrayList)localObject2).add(Integer.valueOf(0));
          ((ArrayList)localObject1).add(LocaleController.getString("Copy", 2131165531));
          ((ArrayList)localObject2).add(Integer.valueOf(3));
          ((ArrayList)localObject1).add(LocaleController.getString("Delete", 2131165560));
          ((ArrayList)localObject2).add(Integer.valueOf(1));
        }
        else
        {
          if (this.currentEncryptedChat == null)
          {
            if (m != 0)
            {
              ((ArrayList)localObject1).add(LocaleController.getString("Reply", 2131166156));
              ((ArrayList)localObject2).add(Integer.valueOf(8));
            }
            if ((this.selectedObject.type == 0) || (this.selectedObject.caption != null))
            {
              ((ArrayList)localObject1).add(LocaleController.getString("Copy", 2131165531));
              ((ArrayList)localObject2).add(Integer.valueOf(3));
            }
            if (i1 == 3)
            {
              if (((this.selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)))
              {
                ((ArrayList)localObject1).add(LocaleController.getString("SaveToGIFs", 2131166201));
                ((ArrayList)localObject2).add(Integer.valueOf(11));
              }
              label1193:
              ((ArrayList)localObject1).add(LocaleController.getString("Forward", 2131165643));
              ((ArrayList)localObject2).add(Integer.valueOf(2));
              if (j == 0) {
                break label1979;
              }
              ((ArrayList)localObject1).add(LocaleController.getString("UnpinMessage", 2131166356));
              ((ArrayList)localObject2).add(Integer.valueOf(14));
            }
            for (;;)
            {
              if (k != 0)
              {
                ((ArrayList)localObject1).add(LocaleController.getString("Edit", 2131165592));
                ((ArrayList)localObject2).add(Integer.valueOf(12));
              }
              if (!localMessageObject.canDeleteMessage(this.currentChat)) {
                break;
              }
              ((ArrayList)localObject1).add(LocaleController.getString("Delete", 2131165560));
              ((ArrayList)localObject2).add(Integer.valueOf(1));
              break;
              if (i1 == 4)
              {
                if (this.selectedObject.isVideo())
                {
                  ((ArrayList)localObject1).add(LocaleController.getString("SaveToGallery", 2131166202));
                  ((ArrayList)localObject2).add(Integer.valueOf(4));
                  ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
                  ((ArrayList)localObject2).add(Integer.valueOf(6));
                  break label1193;
                }
                if (this.selectedObject.isMusic())
                {
                  ((ArrayList)localObject1).add(LocaleController.getString("SaveToMusic", 2131166204));
                  ((ArrayList)localObject2).add(Integer.valueOf(10));
                  ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
                  ((ArrayList)localObject2).add(Integer.valueOf(6));
                  break label1193;
                }
                if (this.selectedObject.getDocument() != null)
                {
                  if (MessageObject.isNewGifDocument(this.selectedObject.getDocument()))
                  {
                    ((ArrayList)localObject1).add(LocaleController.getString("SaveToGIFs", 2131166201));
                    ((ArrayList)localObject2).add(Integer.valueOf(11));
                  }
                  ((ArrayList)localObject1).add(LocaleController.getString("SaveToDownloads", 2131166200));
                  ((ArrayList)localObject2).add(Integer.valueOf(10));
                  ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
                  ((ArrayList)localObject2).add(Integer.valueOf(6));
                  break label1193;
                }
                ((ArrayList)localObject1).add(LocaleController.getString("SaveToGallery", 2131166202));
                ((ArrayList)localObject2).add(Integer.valueOf(4));
                break label1193;
              }
              if (i1 == 5)
              {
                ((ArrayList)localObject1).add(LocaleController.getString("ApplyLocalizationFile", 2131165301));
                ((ArrayList)localObject2).add(Integer.valueOf(5));
                ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
                ((ArrayList)localObject2).add(Integer.valueOf(6));
                break label1193;
              }
              if (i1 == 6)
              {
                ((ArrayList)localObject1).add(LocaleController.getString("SaveToGallery", 2131166202));
                ((ArrayList)localObject2).add(Integer.valueOf(7));
                ((ArrayList)localObject1).add(LocaleController.getString("SaveToDownloads", 2131166200));
                ((ArrayList)localObject2).add(Integer.valueOf(10));
                ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
                ((ArrayList)localObject2).add(Integer.valueOf(6));
                break label1193;
              }
              if (i1 == 7)
              {
                if (this.selectedObject.isMask()) {
                  ((ArrayList)localObject1).add(LocaleController.getString("AddToMasks", 2131165270));
                }
                for (;;)
                {
                  ((ArrayList)localObject2).add(Integer.valueOf(9));
                  break;
                  ((ArrayList)localObject1).add(LocaleController.getString("AddToStickers", 2131165271));
                }
              }
              if (i1 != 8) {
                break label1193;
              }
              TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
              if ((localUser != null) && (localUser.id != UserConfig.getClientUserId()) && (ContactsController.getInstance().contactsDict.get(localUser.id) == null))
              {
                ((ArrayList)localObject1).add(LocaleController.getString("AddContactTitle", 2131165258));
                ((ArrayList)localObject2).add(Integer.valueOf(15));
              }
              if ((this.selectedObject.messageOwner.media.phone_number == null) && (this.selectedObject.messageOwner.media.phone_number.length() == 0)) {
                break label1193;
              }
              ((ArrayList)localObject1).add(LocaleController.getString("Copy", 2131165531));
              ((ArrayList)localObject2).add(Integer.valueOf(16));
              ((ArrayList)localObject1).add(LocaleController.getString("Call", 2131165383));
              ((ArrayList)localObject2).add(Integer.valueOf(17));
              break label1193;
              label1979:
              if (i != 0)
              {
                ((ArrayList)localObject1).add(LocaleController.getString("PinMessage", 2131166122));
                ((ArrayList)localObject2).add(Integer.valueOf(13));
              }
            }
          }
          if (m != 0)
          {
            ((ArrayList)localObject1).add(LocaleController.getString("Reply", 2131166156));
            ((ArrayList)localObject2).add(Integer.valueOf(8));
          }
          if ((this.selectedObject.type == 0) || (this.selectedObject.caption != null))
          {
            ((ArrayList)localObject1).add(LocaleController.getString("Copy", 2131165531));
            ((ArrayList)localObject2).add(Integer.valueOf(3));
          }
          if (i1 != 4) {
            break label2351;
          }
          if (!this.selectedObject.isVideo()) {
            break label2183;
          }
          ((ArrayList)localObject1).add(LocaleController.getString("SaveToGallery", 2131166202));
          ((ArrayList)localObject2).add(Integer.valueOf(4));
          ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
          ((ArrayList)localObject2).add(Integer.valueOf(6));
        }
      }
    }
    for (;;)
    {
      ((ArrayList)localObject1).add(LocaleController.getString("Delete", 2131165560));
      ((ArrayList)localObject2).add(Integer.valueOf(1));
      break label604;
      break;
      label2183:
      if (this.selectedObject.isMusic())
      {
        ((ArrayList)localObject1).add(LocaleController.getString("SaveToMusic", 2131166204));
        ((ArrayList)localObject2).add(Integer.valueOf(10));
        ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
        ((ArrayList)localObject2).add(Integer.valueOf(6));
      }
      else if ((!this.selectedObject.isVideo()) && (this.selectedObject.getDocument() != null))
      {
        ((ArrayList)localObject1).add(LocaleController.getString("SaveToDownloads", 2131166200));
        ((ArrayList)localObject2).add(Integer.valueOf(10));
        ((ArrayList)localObject1).add(LocaleController.getString("ShareFile", 2131166274));
        ((ArrayList)localObject2).add(Integer.valueOf(6));
      }
      else
      {
        ((ArrayList)localObject1).add(LocaleController.getString("SaveToGallery", 2131166202));
        ((ArrayList)localObject2).add(Integer.valueOf(4));
        continue;
        label2351:
        if (i1 == 5)
        {
          ((ArrayList)localObject1).add(LocaleController.getString("ApplyLocalizationFile", 2131165301));
          ((ArrayList)localObject2).add(Integer.valueOf(5));
        }
        else if (i1 == 7)
        {
          ((ArrayList)localObject1).add(LocaleController.getString("AddToStickers", 2131165271));
          ((ArrayList)localObject2).add(Integer.valueOf(9));
        }
      }
    }
    label2421:
    paramView = this.actionBar.createActionMode();
    localObject1 = paramView.getItem(11);
    if (localObject1 != null) {
      ((View)localObject1).setVisibility(0);
    }
    paramView = paramView.getItem(12);
    if (paramView != null) {
      paramView.setVisibility(0);
    }
    if (this.editDoneItem != null) {
      this.editDoneItem.setVisibility(8);
    }
    this.actionBar.showActionMode();
    updatePinnedMessageView(true);
    paramView = new AnimatorSet();
    localObject1 = new ArrayList();
    int i = 0;
    while (i < this.actionModeViews.size())
    {
      localObject2 = (View)this.actionModeViews.get(i);
      AndroidUtilities.clearDrawableAnimation((View)localObject2);
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "scaleY", new float[] { 0.1F, 1.0F }));
      i += 1;
    }
    paramView.playTogether((Collection)localObject1);
    paramView.setDuration(250L);
    paramView.start();
    addToSelectedMessages(localMessageObject);
    this.selectedMessagesCountTextView.setNumber(1, false);
    updateVisibleRows();
  }
  
  private ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject paramMessageObject, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramMessageObject);
    int j = paramMessageObject.getId();
    if (j != 0)
    {
      int i = this.messages.size() - 1;
      while (i >= 0)
      {
        paramMessageObject = (MessageObject)this.messages.get(i);
        if (((this.currentEncryptedChat == null) && (paramMessageObject.getId() > j)) || ((this.currentEncryptedChat != null) && (paramMessageObject.getId() < j) && (paramMessageObject.isVoice()) && ((!paramBoolean) || ((paramMessageObject.isContentUnread()) && (!paramMessageObject.isOut()))))) {
          localArrayList.add(paramMessageObject);
        }
        i -= 1;
      }
    }
    return localArrayList;
  }
  
  private void fixLayout()
  {
    if (this.avatarContainer != null) {
      this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          if (ChatActivity.this.avatarContainer != null) {
            ChatActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
          }
          return ChatActivity.this.fixLayoutInternal();
        }
      });
    }
  }
  
  private boolean fixLayoutInternal()
  {
    boolean bool2 = true;
    if ((!AndroidUtilities.isTablet()) && (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2)) {
      this.selectedMessagesCountTextView.setTextSize(18);
    }
    while (AndroidUtilities.isTablet()) {
      if ((AndroidUtilities.isSmallTablet()) && (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 1))
      {
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        if ((this.playerView != null) && (this.playerView.getParent() == null)) {
          ((ViewGroup)this.fragmentView).addView(this.playerView, LayoutHelper.createFrame(-1, 39.0F, 51, 0.0F, -36.0F, 0.0F, 0.0F));
        }
        return false;
        this.selectedMessagesCountTextView.setTextSize(20);
      }
      else
      {
        ActionBar localActionBar = this.actionBar;
        boolean bool1 = bool2;
        if (this.parentLayout != null)
        {
          bool1 = bool2;
          if (!this.parentLayout.fragmentsStack.isEmpty())
          {
            bool1 = bool2;
            if (this.parentLayout.fragmentsStack.get(0) != this) {
              if (this.parentLayout.fragmentsStack.size() != 1) {
                break label254;
              }
            }
          }
        }
        label254:
        for (bool1 = bool2;; bool1 = false)
        {
          localActionBar.setBackButtonDrawable(new BackDrawable(bool1));
          if ((this.playerView == null) || (this.playerView.getParent() == null)) {
            break;
          }
          this.fragmentView.setPadding(0, 0, 0, 0);
          ((ViewGroup)this.fragmentView).removeView(this.playerView);
          return false;
        }
      }
    }
    return true;
  }
  
  private void forwardMessages(ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      if (!paramBoolean)
      {
        SendMessagesHelper.getInstance().sendMessage(paramArrayList, this.dialog_id);
        return;
      }
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext())
      {
        MessageObject localMessageObject = (MessageObject)paramArrayList.next();
        SendMessagesHelper.getInstance().processForwardFromMyName(localMessageObject, this.dialog_id);
      }
    }
  }
  
  private String getMessageContent(MessageObject paramMessageObject, int paramInt, boolean paramBoolean)
  {
    String str2 = "";
    String str1 = str2;
    Object localObject;
    if (paramBoolean)
    {
      str1 = str2;
      if (paramInt != paramMessageObject.messageOwner.from_id)
      {
        if (paramMessageObject.messageOwner.from_id <= 0) {
          break label142;
        }
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
        str1 = str2;
        if (localObject != null) {
          str1 = ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name) + ":\n";
        }
      }
    }
    while ((paramMessageObject.type == 0) && (paramMessageObject.messageOwner.message != null))
    {
      return str1 + paramMessageObject.messageOwner.message;
      label142:
      str1 = str2;
      if (paramMessageObject.messageOwner.from_id < 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(-paramMessageObject.messageOwner.from_id));
        str1 = str2;
        if (localObject != null) {
          str1 = ((TLRPC.Chat)localObject).title + ":\n";
        }
      }
    }
    if ((paramMessageObject.messageOwner.media != null) && (paramMessageObject.messageOwner.media.caption != null)) {
      return str1 + paramMessageObject.messageOwner.media.caption;
    }
    return str1 + paramMessageObject.messageText;
  }
  
  private int getMessageType(MessageObject paramMessageObject)
  {
    int j = 0;
    int i;
    if (paramMessageObject == null) {
      i = -1;
    }
    do
    {
      return i;
      if (this.currentEncryptedChat == null)
      {
        if ((this.isBroadcast) && (paramMessageObject.getId() <= 0) && (paramMessageObject.isSendError())) {
          i = 1;
        }
        for (;;)
        {
          if (((!this.isBroadcast) && (paramMessageObject.getId() <= 0) && (paramMessageObject.isOut())) || (i != 0))
          {
            if (paramMessageObject.isSendError())
            {
              i = j;
              if (!paramMessageObject.isMediaEmpty()) {
                break;
              }
              return 20;
              i = 0;
              continue;
            }
            return -1;
          }
        }
        if (paramMessageObject.type == 6) {
          return -1;
        }
        if ((paramMessageObject.type == 10) || (paramMessageObject.type == 11))
        {
          if (paramMessageObject.getId() == 0) {
            return -1;
          }
          return 1;
        }
        if (paramMessageObject.isVoice()) {
          return 2;
        }
        if (paramMessageObject.isSticker())
        {
          paramMessageObject = paramMessageObject.getInputStickerSet();
          if ((paramMessageObject instanceof TLRPC.TL_inputStickerSetID))
          {
            if (!StickersQuery.isStickerPackInstalled(paramMessageObject.id)) {
              return 7;
            }
          }
          else if (((paramMessageObject instanceof TLRPC.TL_inputStickerSetShortName)) && (!StickersQuery.isStickerPackInstalled(paramMessageObject.short_name))) {
            return 7;
          }
        }
        else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || (paramMessageObject.getDocument() != null) || (paramMessageObject.isMusic()) || (paramMessageObject.isVideo()))
        {
          j = 0;
          i = j;
          if (paramMessageObject.messageOwner.attachPath != null)
          {
            i = j;
            if (paramMessageObject.messageOwner.attachPath.length() != 0)
            {
              i = j;
              if (new File(paramMessageObject.messageOwner.attachPath).exists()) {
                i = 1;
              }
            }
          }
          j = i;
          if (i == 0)
          {
            j = i;
            if (FileLoader.getPathToMessage(paramMessageObject.messageOwner).exists()) {
              j = 1;
            }
          }
          if (j != 0)
          {
            if (paramMessageObject.getDocument() != null)
            {
              paramMessageObject = paramMessageObject.getDocument().mime_type;
              if (paramMessageObject != null)
              {
                if (paramMessageObject.endsWith("/xml")) {
                  return 5;
                }
                if ((paramMessageObject.endsWith("/png")) || (paramMessageObject.endsWith("/jpg")) || (paramMessageObject.endsWith("/jpeg"))) {
                  return 6;
                }
              }
            }
            return 4;
          }
        }
        else
        {
          if (paramMessageObject.type == 12) {
            return 8;
          }
          if (paramMessageObject.isMediaEmpty()) {
            return 3;
          }
        }
        return 2;
      }
      if (paramMessageObject.isSending()) {
        return -1;
      }
      if (paramMessageObject.type == 6) {
        return -1;
      }
      if (!paramMessageObject.isSendError()) {
        break;
      }
      i = j;
    } while (!paramMessageObject.isMediaEmpty());
    return 20;
    if ((paramMessageObject.type == 10) || (paramMessageObject.type == 11))
    {
      if ((paramMessageObject.getId() == 0) || (paramMessageObject.isSending())) {
        return -1;
      }
      return 1;
    }
    if (paramMessageObject.isVoice()) {
      return 2;
    }
    if (paramMessageObject.isSticker())
    {
      paramMessageObject = paramMessageObject.getInputStickerSet();
      if (((paramMessageObject instanceof TLRPC.TL_inputStickerSetShortName)) && (!StickersQuery.isStickerPackInstalled(paramMessageObject.short_name))) {
        return 7;
      }
    }
    else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || (paramMessageObject.getDocument() != null) || (paramMessageObject.isMusic()) || (paramMessageObject.isVideo()))
    {
      j = 0;
      i = j;
      if (paramMessageObject.messageOwner.attachPath != null)
      {
        i = j;
        if (paramMessageObject.messageOwner.attachPath.length() != 0)
        {
          i = j;
          if (new File(paramMessageObject.messageOwner.attachPath).exists()) {
            i = 1;
          }
        }
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if (FileLoader.getPathToMessage(paramMessageObject.messageOwner).exists()) {
          j = 1;
        }
      }
      if (j != 0)
      {
        if (paramMessageObject.getDocument() != null)
        {
          String str = paramMessageObject.getDocument().mime_type;
          if ((str != null) && (str.endsWith("text/xml"))) {
            return 5;
          }
        }
        if (paramMessageObject.messageOwner.ttl <= 0) {
          return 4;
        }
      }
    }
    else
    {
      if (paramMessageObject.type == 12) {
        return 8;
      }
      if (paramMessageObject.isMediaEmpty()) {
        return 3;
      }
    }
    return 2;
  }
  
  private void hidePinnedMessageView(boolean paramBoolean)
  {
    if (this.pinnedMessageView.getTag() == null)
    {
      this.pinnedMessageView.setTag(Integer.valueOf(1));
      if (this.pinnedMessageViewAnimator != null)
      {
        this.pinnedMessageViewAnimator.cancel();
        this.pinnedMessageViewAnimator = null;
      }
      if (paramBoolean)
      {
        this.pinnedMessageViewAnimator = new AnimatorSet();
        this.pinnedMessageViewAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", new float[] { -AndroidUtilities.dp(50.0F) }) });
        this.pinnedMessageViewAnimator.setDuration(200L);
        this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.pinnedMessageViewAnimator != null) && (ChatActivity.this.pinnedMessageViewAnimator.equals(paramAnonymousAnimator))) {
              ChatActivity.access$13302(ChatActivity.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.pinnedMessageViewAnimator != null) && (ChatActivity.this.pinnedMessageViewAnimator.equals(paramAnonymousAnimator)))
            {
              ChatActivity.this.pinnedMessageView.setVisibility(8);
              ChatActivity.access$13302(ChatActivity.this, null);
            }
          }
        });
        this.pinnedMessageViewAnimator.start();
      }
    }
    else
    {
      return;
    }
    this.pinnedMessageView.setTranslationY(-AndroidUtilities.dp(50.0F));
    this.pinnedMessageView.setVisibility(8);
  }
  
  private void initStickers()
  {
    if ((this.chatActivityEnterView == null) || (getParentActivity() == null) || (this.stickersAdapter != null) || ((this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 23))) {
      return;
    }
    if (this.stickersAdapter != null) {
      this.stickersAdapter.onDestroy();
    }
    this.stickersListView.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    RecyclerListView localRecyclerListView = this.stickersListView;
    Object localObject = new StickersAdapter(getParentActivity(), new StickersAdapter.StickersAdapterDelegate()
    {
      public void needChangePanelVisibility(final boolean paramAnonymousBoolean)
      {
        float f2 = 1.0F;
        if (((paramAnonymousBoolean) && (ChatActivity.this.stickersPanel.getVisibility() == 0)) || ((!paramAnonymousBoolean) && (ChatActivity.this.stickersPanel.getVisibility() == 8))) {}
        label165:
        label249:
        label254:
        do
        {
          return;
          Object localObject;
          int i;
          FrameLayout localFrameLayout;
          float f1;
          if (paramAnonymousBoolean)
          {
            ChatActivity.this.stickersListView.scrollToPosition(0);
            localObject = ChatActivity.this.stickersPanel;
            if (ChatActivity.this.allowStickersPanel)
            {
              i = 0;
              ((FrameLayout)localObject).setVisibility(i);
            }
          }
          else
          {
            if (ChatActivity.this.runningAnimation != null)
            {
              ChatActivity.this.runningAnimation.cancel();
              ChatActivity.access$11302(ChatActivity.this, null);
            }
            if (ChatActivity.this.stickersPanel.getVisibility() == 4) {
              continue;
            }
            ChatActivity.access$11302(ChatActivity.this, new AnimatorSet());
            localObject = ChatActivity.this.runningAnimation;
            localFrameLayout = ChatActivity.this.stickersPanel;
            if (!paramAnonymousBoolean) {
              break label249;
            }
            f1 = 0.0F;
            if (!paramAnonymousBoolean) {
              break label254;
            }
          }
          for (;;)
          {
            ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(localFrameLayout, "alpha", new float[] { f1, f2 }) });
            ChatActivity.this.runningAnimation.setDuration(150L);
            ChatActivity.this.runningAnimation.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationCancel(Animator paramAnonymous2Animator)
              {
                if ((ChatActivity.this.runningAnimation != null) && (ChatActivity.this.runningAnimation.equals(paramAnonymous2Animator))) {
                  ChatActivity.access$11302(ChatActivity.this, null);
                }
              }
              
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                if ((ChatActivity.this.runningAnimation != null) && (ChatActivity.this.runningAnimation.equals(paramAnonymous2Animator)))
                {
                  if (!paramAnonymousBoolean)
                  {
                    ChatActivity.this.stickersAdapter.clearStickers();
                    ChatActivity.this.stickersPanel.setVisibility(8);
                    if (StickerPreviewViewer.getInstance().isVisible()) {
                      StickerPreviewViewer.getInstance().close();
                    }
                    StickerPreviewViewer.getInstance().reset();
                  }
                  ChatActivity.access$11302(ChatActivity.this, null);
                }
              }
            });
            ChatActivity.this.runningAnimation.start();
            return;
            i = 4;
            break;
            f1 = 1.0F;
            break label165;
            f2 = 0.0F;
          }
        } while (paramAnonymousBoolean);
        ChatActivity.this.stickersPanel.setVisibility(8);
      }
    });
    this.stickersAdapter = ((StickersAdapter)localObject);
    localRecyclerListView.setAdapter((RecyclerView.Adapter)localObject);
    localRecyclerListView = this.stickersListView;
    localObject = new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        paramAnonymousView = ChatActivity.this.stickersAdapter.getItem(paramAnonymousInt);
        if ((paramAnonymousView instanceof TLRPC.TL_document))
        {
          SendMessagesHelper.getInstance().sendSticker(paramAnonymousView, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
          ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
          ChatActivity.this.chatActivityEnterView.addStickerToRecent(paramAnonymousView);
        }
        ChatActivity.this.chatActivityEnterView.setFieldText("");
      }
    };
    this.stickersOnItemClickListener = ((RecyclerListView.OnItemClickListener)localObject);
    localRecyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener)localObject);
  }
  
  private void mentionListViewUpdateLayout()
  {
    int j = 0;
    if (this.mentionListView.getChildCount() <= 0)
    {
      this.mentionListViewScrollOffsetY = 0;
      this.mentionListViewLastViewPosition = -1;
      return;
    }
    Object localObject = this.mentionListView.getChildAt(this.mentionListView.getChildCount() - 1);
    MentionsAdapter.Holder localHolder = (MentionsAdapter.Holder)this.mentionListView.findContainingViewHolder((View)localObject);
    if (localHolder != null)
    {
      this.mentionListViewLastViewPosition = localHolder.getAdapterPosition();
      this.mentionListViewLastViewTop = ((View)localObject).getTop();
    }
    for (;;)
    {
      localObject = this.mentionListView.getChildAt(0);
      localHolder = (MentionsAdapter.Holder)this.mentionListView.findContainingViewHolder((View)localObject);
      int i = j;
      if (((View)localObject).getTop() > 0)
      {
        i = j;
        if (localHolder != null)
        {
          i = j;
          if (localHolder.getAdapterPosition() == 0) {
            i = ((View)localObject).getTop();
          }
        }
      }
      if (this.mentionListViewScrollOffsetY == i) {
        break;
      }
      localObject = this.mentionListView;
      this.mentionListViewScrollOffsetY = i;
      ((RecyclerListView)localObject).setTopGlowOffset(i);
      this.mentionListView.invalidate();
      this.mentionContainer.invalidate();
      return;
      this.mentionListViewLastViewPosition = -1;
    }
  }
  
  private void moveScrollToLastMessage()
  {
    if ((this.chatListView != null) && (!this.messages.isEmpty())) {
      this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
    }
  }
  
  private void openSearchWithText(String paramString)
  {
    this.avatarContainer.setVisibility(8);
    this.headerItem.setVisibility(8);
    this.attachItem.setVisibility(8);
    this.searchItem.setVisibility(0);
    updateSearchButtons(0, 0, 0);
    updateBottomOverlay();
    if (paramString == null) {}
    for (boolean bool = true;; bool = false)
    {
      this.openSearchKeyboard = bool;
      this.searchItem.openSearch(this.openSearchKeyboard);
      if (paramString != null)
      {
        this.searchItem.getSearchField().setText(paramString);
        this.searchItem.getSearchField().setSelection(this.searchItem.getSearchField().length());
        MessagesSearchQuery.searchMessagesInChat(paramString, this.dialog_id, this.mergeDialogId, this.classGuid, 0);
      }
      return;
    }
  }
  
  private void processRowSelect(View paramView)
  {
    MessageObject localMessageObject = null;
    if ((paramView instanceof ChatMessageCell)) {
      localMessageObject = ((ChatMessageCell)paramView).getMessageObject();
    }
    for (;;)
    {
      int i = getMessageType(localMessageObject);
      if ((i >= 2) && (i != 20)) {
        break;
      }
      return;
      if ((paramView instanceof ChatActionCell)) {
        localMessageObject = ((ChatActionCell)paramView).getMessageObject();
      }
    }
    addToSelectedMessages(localMessageObject);
    updateActionModeTitle();
    updateVisibleRows();
  }
  
  private void processSelectedAttach(int paramInt)
  {
    Object localObject1;
    if ((paramInt == 0) || (paramInt == 1) || (paramInt == 4) || (paramInt == 2)) {
      if (this.currentChat != null) {
        if (this.currentChat.participants_count > MessagesController.getInstance().groupBigSize) {
          if ((paramInt == 0) || (paramInt == 1))
          {
            localObject1 = "bigchat_upload_photo";
            if (MessagesController.isFeatureEnabled((String)localObject1, this)) {
              break label117;
            }
          }
        }
      }
    }
    label117:
    Object localObject3;
    label576:
    do
    {
      do
      {
        return;
        localObject1 = "bigchat_upload_document";
        break;
        if ((paramInt == 0) || (paramInt == 1))
        {
          localObject1 = "chat_upload_photo";
          break;
        }
        localObject1 = "chat_upload_document";
        break;
        if ((paramInt == 0) || (paramInt == 1))
        {
          localObject1 = "pm_upload_photo";
          break;
        }
        localObject1 = "pm_upload_document";
        break;
        File localFile;
        if (paramInt == 0)
        {
          if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0))
          {
            getParentActivity().requestPermissions(new String[] { "android.permission.CAMERA" }, 19);
            return;
          }
          for (;;)
          {
            try
            {
              localObject1 = new Intent("android.media.action.IMAGE_CAPTURE");
              localFile = AndroidUtilities.generatePicturePath();
              if (localFile != null)
              {
                if (Build.VERSION.SDK_INT >= 24)
                {
                  ((Intent)localObject1).putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", localFile));
                  ((Intent)localObject1).addFlags(2);
                  ((Intent)localObject1).addFlags(1);
                  this.currentPicturePath = localFile.getAbsolutePath();
                }
              }
              else
              {
                startActivityForResult((Intent)localObject1, 0);
                return;
              }
            }
            catch (Exception localException1)
            {
              FileLog.e("tmessages", localException1);
              return;
            }
            localException1.putExtra("output", Uri.fromFile(localFile));
          }
        }
        Object localObject2;
        if (paramInt == 1)
        {
          if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
          {
            getParentActivity().requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 4);
            return;
          }
          if ((this.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) {}
          for (boolean bool = true;; bool = false)
          {
            localObject2 = new PhotoAlbumPickerActivity(false, bool, true, this);
            ((PhotoAlbumPickerActivity)localObject2).setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate()
            {
              public void didSelectPhotos(ArrayList<String> paramAnonymousArrayList1, ArrayList<String> paramAnonymousArrayList2, ArrayList<ArrayList<TLRPC.InputDocument>> paramAnonymousArrayList, ArrayList<MediaController.SearchImage> paramAnonymousArrayList3)
              {
                SendMessagesHelper.prepareSendingPhotos(paramAnonymousArrayList1, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, paramAnonymousArrayList2, paramAnonymousArrayList);
                SendMessagesHelper.prepareSendingPhotosSearch(paramAnonymousArrayList3, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
              }
              
              public boolean didSelectVideo(String paramAnonymousString)
              {
                boolean bool = false;
                if (Build.VERSION.SDK_INT >= 16)
                {
                  if (!ChatActivity.this.openVideoEditor(paramAnonymousString, true, true)) {
                    bool = true;
                  }
                  return bool;
                }
                SendMessagesHelper.prepareSendingVideo(paramAnonymousString, 0L, 0L, 0, 0, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                return true;
              }
              
              public void startPhotoSelectActivity()
              {
                try
                {
                  Intent localIntent1 = new Intent();
                  localIntent1.setType("video/*");
                  localIntent1.setAction("android.intent.action.GET_CONTENT");
                  localIntent1.putExtra("android.intent.extra.sizeLimit", 1610612736L);
                  Intent localIntent2 = new Intent("android.intent.action.PICK");
                  localIntent2.setType("image/*");
                  localIntent2 = Intent.createChooser(localIntent2, null);
                  localIntent2.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[] { localIntent1 });
                  ChatActivity.this.startActivityForResult(localIntent2, 1);
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                }
              }
            });
            presentFragment((BaseFragment)localObject2);
            return;
          }
        }
        if (paramInt == 2)
        {
          if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0))
          {
            getParentActivity().requestPermissions(new String[] { "android.permission.CAMERA" }, 20);
            return;
          }
          for (;;)
          {
            try
            {
              localObject2 = new Intent("android.media.action.VIDEO_CAPTURE");
              localFile = AndroidUtilities.generateVideoPath();
              if (localFile != null)
              {
                if (Build.VERSION.SDK_INT >= 24)
                {
                  ((Intent)localObject2).putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", localFile));
                  ((Intent)localObject2).addFlags(2);
                  ((Intent)localObject2).addFlags(1);
                  ((Intent)localObject2).putExtra("android.intent.extra.sizeLimit", 1610612736L);
                  this.currentPicturePath = localFile.getAbsolutePath();
                }
              }
              else
              {
                startActivityForResult((Intent)localObject2, 2);
                return;
              }
            }
            catch (Exception localException2)
            {
              FileLog.e("tmessages", localException2);
              return;
            }
            if (Build.VERSION.SDK_INT >= 18) {
              localException2.putExtra("output", Uri.fromFile(localFile));
            }
          }
        }
        if (paramInt != 6) {
          break label576;
        }
      } while (!AndroidUtilities.isGoogleMapsInstalled(this));
      localObject3 = new LocationActivity();
      ((LocationActivity)localObject3).setDelegate(new LocationActivity.LocationActivityDelegate()
      {
        public void didSelectLocation(TLRPC.MessageMedia paramAnonymousMessageMedia)
        {
          SendMessagesHelper.getInstance().sendMessage(paramAnonymousMessageMedia, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, null);
          ChatActivity.this.moveScrollToLastMessage();
          ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
          DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
          if (ChatActivity.this.paused) {
            ChatActivity.access$11502(ChatActivity.this, true);
          }
        }
      });
      presentFragment((BaseFragment)localObject3);
      return;
      if (paramInt == 4)
      {
        if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
        {
          getParentActivity().requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 4);
          return;
        }
        localObject3 = new DocumentSelectActivity();
        ((DocumentSelectActivity)localObject3).setDelegate(new DocumentSelectActivity.DocumentSelectActivityDelegate()
        {
          public void didSelectFiles(DocumentSelectActivity paramAnonymousDocumentSelectActivity, ArrayList<String> paramAnonymousArrayList)
          {
            paramAnonymousDocumentSelectActivity.finishFragment();
            SendMessagesHelper.prepareSendingDocuments(paramAnonymousArrayList, paramAnonymousArrayList, null, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
          }
          
          public void startDocumentSelectActivity()
          {
            try
            {
              Intent localIntent = new Intent("android.intent.action.PICK");
              localIntent.setType("*/*");
              ChatActivity.this.startActivityForResult(localIntent, 21);
              return;
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
            }
          }
        });
        presentFragment((BaseFragment)localObject3);
        return;
      }
      if (paramInt == 3)
      {
        if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
        {
          getParentActivity().requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 4);
          return;
        }
        localObject3 = new AudioSelectActivity();
        ((AudioSelectActivity)localObject3).setDelegate(new AudioSelectActivity.AudioSelectActivityDelegate()
        {
          public void didSelectAudio(ArrayList<MessageObject> paramAnonymousArrayList)
          {
            SendMessagesHelper.prepareSendingAudioDocuments(paramAnonymousArrayList, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
          }
        });
        presentFragment((BaseFragment)localObject3);
        return;
      }
    } while (paramInt != 5);
    if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") != 0))
    {
      getParentActivity().requestPermissions(new String[] { "android.permission.READ_CONTACTS" }, 5);
      return;
    }
    try
    {
      localObject3 = new Intent("android.intent.action.PICK", ContactsContract.Contacts.CONTENT_URI);
      ((Intent)localObject3).setType("vnd.android.cursor.dir/phone_v2");
      startActivityForResult((Intent)localObject3, 31);
      return;
    }
    catch (Exception localException3)
    {
      FileLog.e("tmessages", localException3);
    }
  }
  
  private void processSelectedOption(int paramInt)
  {
    if (this.selectedObject == null) {
      return;
    }
    switch (paramInt)
    {
    }
    for (;;)
    {
      this.selectedObject = null;
      return;
      if (SendMessagesHelper.getInstance().retrySendMessage(this.selectedObject, false))
      {
        moveScrollToLastMessage();
        continue;
        if (getParentActivity() == null)
        {
          this.selectedObject = null;
          return;
        }
        createDeleteMessagesAlert(this.selectedObject);
        continue;
        this.forwaringMessage = this.selectedObject;
        Object localObject1 = new Bundle();
        ((Bundle)localObject1).putBoolean("onlySelect", true);
        ((Bundle)localObject1).putInt("dialogsType", 1);
        localObject1 = new DialogsActivity((Bundle)localObject1);
        ((DialogsActivity)localObject1).setDelegate(this);
        presentFragment((BaseFragment)localObject1);
        continue;
        AndroidUtilities.addToClipboard(getMessageContent(this.selectedObject, 0, false));
        continue;
        final Object localObject2 = this.selectedObject.messageOwner.attachPath;
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (((String)localObject2).length() > 0)
          {
            localObject1 = localObject2;
            if (!new File((String)localObject2).exists()) {
              localObject1 = null;
            }
          }
        }
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          if (((String)localObject1).length() != 0) {}
        }
        else
        {
          localObject2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
        }
        if ((this.selectedObject.type == 3) || (this.selectedObject.type == 1))
        {
          if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
          {
            getParentActivity().requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
            this.selectedObject = null;
            return;
          }
          localObject1 = getParentActivity();
          if (this.selectedObject.type == 3) {}
          for (paramInt = 1;; paramInt = 0)
          {
            MediaController.saveFile((String)localObject2, (Context)localObject1, paramInt, null, null);
            break;
          }
          localObject2 = null;
          localObject1 = localObject2;
          Object localObject3;
          if (this.selectedObject.messageOwner.attachPath != null)
          {
            localObject1 = localObject2;
            if (this.selectedObject.messageOwner.attachPath.length() != 0)
            {
              localObject3 = new File(this.selectedObject.messageOwner.attachPath);
              localObject1 = localObject2;
              if (((File)localObject3).exists()) {
                localObject1 = localObject3;
              }
            }
          }
          localObject2 = localObject1;
          if (localObject1 == null)
          {
            localObject3 = FileLoader.getPathToMessage(this.selectedObject.messageOwner);
            localObject2 = localObject1;
            if (((File)localObject3).exists()) {
              localObject2 = localObject3;
            }
          }
          if (localObject2 != null) {
            if (LocaleController.getInstance().applyLanguageFile((File)localObject2))
            {
              presentFragment(new LanguageSelectActivity());
            }
            else
            {
              if (getParentActivity() == null)
              {
                this.selectedObject = null;
                return;
              }
              localObject1 = new AlertDialog.Builder(getParentActivity());
              ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
              ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("IncorrectLocalization", 2131165751));
              ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
              showDialog(((AlertDialog.Builder)localObject1).create());
              continue;
              localObject2 = this.selectedObject.messageOwner.attachPath;
              localObject1 = localObject2;
              if (localObject2 != null)
              {
                localObject1 = localObject2;
                if (((String)localObject2).length() > 0)
                {
                  localObject1 = localObject2;
                  if (!new File((String)localObject2).exists()) {
                    localObject1 = null;
                  }
                }
              }
              if (localObject1 != null)
              {
                localObject2 = localObject1;
                if (((String)localObject1).length() != 0) {}
              }
              else
              {
                localObject2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
              }
              localObject1 = new Intent("android.intent.action.SEND");
              ((Intent)localObject1).setType(this.selectedObject.getDocument().mime_type);
              ((Intent)localObject1).putExtra("android.intent.extra.STREAM", Uri.fromFile(new File((String)localObject2)));
              getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject1, LocaleController.getString("ShareFile", 2131166274)), 500);
              continue;
              localObject2 = this.selectedObject.messageOwner.attachPath;
              localObject1 = localObject2;
              if (localObject2 != null)
              {
                localObject1 = localObject2;
                if (((String)localObject2).length() > 0)
                {
                  localObject1 = localObject2;
                  if (!new File((String)localObject2).exists()) {
                    localObject1 = null;
                  }
                }
              }
              if (localObject1 != null)
              {
                localObject2 = localObject1;
                if (((String)localObject1).length() != 0) {}
              }
              else
              {
                localObject2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
              }
              if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
              {
                getParentActivity().requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
                this.selectedObject = null;
                return;
              }
              MediaController.saveFile((String)localObject2, getParentActivity(), 0, null, null);
              continue;
              showReplyPanel(true, this.selectedObject, null, null, false, true);
              continue;
              localObject2 = getParentActivity();
              localObject3 = this.selectedObject.getInputStickerSet();
              if (this.bottomOverlayChat.getVisibility() != 0) {}
              for (localObject1 = this.chatActivityEnterView;; localObject1 = null)
              {
                showDialog(new StickersAlert((Context)localObject2, this, (TLRPC.InputStickerSet)localObject3, null, (StickersAlert.StickersAlertDelegate)localObject1));
                break;
              }
              if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
              {
                getParentActivity().requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
                this.selectedObject = null;
                return;
              }
              localObject1 = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
              if (localObject1 != null)
              {
                localObject2 = localObject1;
                if (((String)localObject1).length() != 0) {}
              }
              else
              {
                localObject2 = this.selectedObject.getFileName();
              }
              localObject3 = this.selectedObject.messageOwner.attachPath;
              localObject1 = localObject3;
              if (localObject3 != null)
              {
                localObject1 = localObject3;
                if (((String)localObject3).length() > 0)
                {
                  localObject1 = localObject3;
                  if (!new File((String)localObject3).exists()) {
                    localObject1 = null;
                  }
                }
              }
              if (localObject1 != null)
              {
                localObject3 = localObject1;
                if (((String)localObject1).length() != 0) {}
              }
              else
              {
                localObject3 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
              }
              Object localObject4 = getParentActivity();
              if (this.selectedObject.isMusic())
              {
                paramInt = 3;
                label1218:
                if (this.selectedObject.getDocument() == null) {
                  break label1260;
                }
              }
              label1260:
              for (localObject1 = this.selectedObject.getDocument().mime_type;; localObject1 = "")
              {
                MediaController.saveFile((String)localObject3, (Context)localObject4, paramInt, (String)localObject2, (String)localObject1);
                break;
                paramInt = 2;
                break label1218;
              }
              localObject1 = this.selectedObject.getDocument();
              MessagesController.getInstance().saveGif((TLRPC.Document)localObject1);
              showGifHint();
              this.chatActivityEnterView.addRecentGif((TLRPC.Document)localObject1);
              continue;
              if (getParentActivity() == null)
              {
                this.selectedObject = null;
                return;
              }
              if ((this.searchItem != null) && (this.actionBar.isSearchFieldVisible()))
              {
                this.actionBar.closeSearchField();
                this.chatActivityEnterView.setFieldFocused();
              }
              this.mentionsAdapter.setNeedBotContext(false);
              this.chatListView.setOnItemLongClickListener(null);
              this.chatListView.setOnItemClickListener(null);
              this.chatListView.setClickable(false);
              this.chatListView.setLongClickable(false);
              localObject1 = this.chatActivityEnterView;
              localObject2 = this.selectedObject;
              if (!this.selectedObject.isMediaEmpty()) {}
              for (boolean bool = true;; bool = false)
              {
                ((ChatActivityEnterView)localObject1).setEditingMessageObject((MessageObject)localObject2, bool);
                if (this.chatActivityEnterView.isEditingCaption()) {
                  this.mentionsAdapter.setAllowNewMentions(false);
                }
                this.actionModeTitleContainer.setVisibility(0);
                this.selectedMessagesCountTextView.setVisibility(8);
                checkEditTimer();
                this.chatActivityEnterView.setAllowStickersAndGifs(false, false);
                localObject1 = this.actionBar.createActionMode();
                ((ActionBarMenu)localObject1).getItem(19).setVisibility(8);
                ((ActionBarMenu)localObject1).getItem(10).setVisibility(8);
                ((ActionBarMenu)localObject1).getItem(11).setVisibility(8);
                ((ActionBarMenu)localObject1).getItem(12).setVisibility(8);
                if (this.editDoneItemAnimation != null)
                {
                  this.editDoneItemAnimation.cancel();
                  this.editDoneItemAnimation = null;
                }
                this.editDoneItem.setVisibility(0);
                showEditDoneProgress(true, false);
                this.actionBar.showActionMode();
                updatePinnedMessageView(true);
                updateVisibleRows();
                localObject1 = new TLRPC.TL_messages_getMessageEditData();
                ((TLRPC.TL_messages_getMessageEditData)localObject1).peer = MessagesController.getInputPeer((int)this.dialog_id);
                ((TLRPC.TL_messages_getMessageEditData)localObject1).id = this.selectedObject.getId();
                this.editingMessageObjectReqId = ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
                {
                  public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        ChatActivity.access$9702(ChatActivity.this, 0);
                        if (paramAnonymousTLObject == null)
                        {
                          AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                          localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                          localBuilder.setMessage(LocaleController.getString("EditMessageError", 2131165595));
                          localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                          ChatActivity.this.showDialog(localBuilder.create());
                          if (ChatActivity.this.chatActivityEnterView != null) {
                            ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
                          }
                          return;
                        }
                        ChatActivity.this.showEditDoneProgress(false, true);
                      }
                    });
                  }
                });
                break;
              }
              final int j = this.selectedObject.getId();
              localObject1 = new AlertDialog.Builder(getParentActivity());
              ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("PinMessageAlert", 2131166123));
              localObject2 = new boolean[1];
              localObject2[0] = 1;
              localObject3 = new FrameLayout(getParentActivity());
              if (Build.VERSION.SDK_INT >= 21) {
                ((FrameLayout)localObject3).setPadding(0, AndroidUtilities.dp(8.0F), 0, 0);
              }
              localObject4 = new CheckBoxCell(getParentActivity());
              ((CheckBoxCell)localObject4).setBackgroundResource(2130837796);
              ((CheckBoxCell)localObject4).setText(LocaleController.getString("PinNotify", 2131166124), "", true, false);
              if (LocaleController.isRTL)
              {
                paramInt = AndroidUtilities.dp(8.0F);
                label1770:
                if (!LocaleController.isRTL) {
                  break label1909;
                }
              }
              label1909:
              for (int i = 0;; i = AndroidUtilities.dp(8.0F))
              {
                ((CheckBoxCell)localObject4).setPadding(paramInt, 0, i, 0);
                ((FrameLayout)localObject3).addView((View)localObject4, LayoutHelper.createFrame(-1, 48.0F, 51, 8.0F, 0.0F, 8.0F, 0.0F));
                ((CheckBoxCell)localObject4).setOnClickListener(new View.OnClickListener()
                {
                  public void onClick(View paramAnonymousView)
                  {
                    paramAnonymousView = (CheckBoxCell)paramAnonymousView;
                    boolean[] arrayOfBoolean = localObject2;
                    if (localObject2[0] == 0) {}
                    for (int i = 1;; i = 0)
                    {
                      arrayOfBoolean[0] = i;
                      paramAnonymousView.setChecked(localObject2[0], true);
                      return;
                    }
                  }
                });
                ((AlertDialog.Builder)localObject1).setView((View)localObject3);
                ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                  {
                    MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, j, localObject2[0]);
                  }
                });
                ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
                ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                showDialog(((AlertDialog.Builder)localObject1).create());
                break;
                paramInt = 0;
                break label1770;
              }
              localObject1 = new AlertDialog.Builder(getParentActivity());
              ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("UnpinMessageAlert", 2131166357));
              ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                {
                  MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, 0, false);
                }
              });
              ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
              ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
              showDialog(((AlertDialog.Builder)localObject1).create());
              continue;
              localObject1 = new Bundle();
              ((Bundle)localObject1).putInt("user_id", this.selectedObject.messageOwner.media.user_id);
              ((Bundle)localObject1).putString("phone", this.selectedObject.messageOwner.media.phone_number);
              ((Bundle)localObject1).putBoolean("addContact", true);
              presentFragment(new ContactAddActivity((Bundle)localObject1));
              continue;
              AndroidUtilities.addToClipboard(this.selectedObject.messageOwner.media.phone_number);
              continue;
              try
              {
                localObject1 = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                ((Intent)localObject1).addFlags(268435456);
                getParentActivity().startActivityForResult((Intent)localObject1, 500);
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          }
        }
      }
    }
  }
  
  private void removeMessageObject(MessageObject paramMessageObject)
  {
    int i = this.messages.indexOf(paramMessageObject);
    if (i == -1) {}
    do
    {
      return;
      this.messages.remove(i);
    } while (this.chatAdapter == null);
    this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + this.messages.size() - i - 1);
  }
  
  private void removeUnreadPlane()
  {
    if (this.unreadMessageObject != null)
    {
      boolean[] arrayOfBoolean = this.forwardEndReached;
      this.forwardEndReached[1] = true;
      arrayOfBoolean[0] = true;
      this.first_unread_id = 0;
      this.last_message_id = 0;
      this.unread_to_load = 0;
      removeMessageObject(this.unreadMessageObject);
      this.unreadMessageObject = null;
    }
  }
  
  private void scrollToLastMessage(boolean paramBoolean)
  {
    if ((this.forwardEndReached[0] != 0) && (this.first_unread_id == 0) && (this.startLoadFromMessageId == 0))
    {
      if ((paramBoolean) && (this.chatLayoutManager.findLastCompletelyVisibleItemPosition() == this.chatAdapter.getItemCount() - 1))
      {
        showPagedownButton(false, true);
        this.highlightMessageId = Integer.MAX_VALUE;
        updateVisibleRows();
        return;
      }
      this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
      return;
    }
    clearChatData();
    this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
    MessagesController localMessagesController = MessagesController.getInstance();
    long l = this.dialog_id;
    int i = this.classGuid;
    paramBoolean = ChatObject.isChannel(this.currentChat);
    int j = this.lastLoadIndex;
    this.lastLoadIndex = (j + 1);
    localMessagesController.loadMessages(l, 30, 0, true, 0, i, 0, 0, paramBoolean, j);
  }
  
  private void scrollToMessageId(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    Object localObject1 = (MessageObject)this.messagesDict[paramInt3].get(Integer.valueOf(paramInt1));
    int k = 0;
    int i;
    label117:
    int j;
    label136:
    Object localObject2;
    if (localObject1 != null) {
      if (this.messages.indexOf(localObject1) != -1) {
        if (paramBoolean)
        {
          this.highlightMessageId = paramInt1;
          i = Math.max(0, (this.chatListView.getHeight() - ((MessageObject)localObject1).getApproximateHeight()) / 2);
          if (this.messages.get(this.messages.size() - 1) != localObject1) {
            break label256;
          }
          this.chatLayoutManager.scrollToPositionWithOffset(0, -this.chatListView.getPaddingTop() - AndroidUtilities.dp(7.0F) + i);
          updateVisibleRows();
          int m = 0;
          int n = this.chatListView.getChildCount();
          j = 0;
          i = m;
          if (j < n)
          {
            localObject2 = this.chatListView.getChildAt(j);
            if (!(localObject2 instanceof ChatMessageCell)) {
              break label311;
            }
            localObject2 = ((ChatMessageCell)localObject2).getMessageObject();
            if ((localObject2 == null) || (((MessageObject)localObject2).getId() != ((MessageObject)localObject1).getId())) {
              break label353;
            }
            i = 1;
          }
          label197:
          j = k;
          if (i == 0)
          {
            showPagedownButton(true, true);
            j = k;
          }
        }
      }
    }
    label256:
    label311:
    label353:
    long l;
    for (;;)
    {
      if (j != 0)
      {
        if ((this.currentEncryptedChat != null) && (!MessagesStorage.getInstance().checkMessageId(this.dialog_id, this.startLoadFromMessageId)))
        {
          return;
          this.highlightMessageId = Integer.MAX_VALUE;
          break;
          this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.messagesStartRow + this.messages.size() - this.messages.indexOf(localObject1) - 1, -this.chatListView.getPaddingTop() - AndroidUtilities.dp(7.0F) + i);
          break label117;
          if ((localObject2 instanceof ChatActionCell))
          {
            localObject2 = ((ChatActionCell)localObject2).getMessageObject();
            if ((localObject2 != null) && (((MessageObject)localObject2).getId() == ((MessageObject)localObject1).getId()))
            {
              i = 1;
              break label197;
            }
          }
          j += 1;
          break label136;
          j = 1;
          continue;
          j = 1;
          continue;
        }
        this.waitingForLoad.clear();
        this.waitingForReplyMessageLoad = true;
        this.highlightMessageId = Integer.MAX_VALUE;
        this.scrollToMessagePosition = 55536;
        this.startLoadFromMessageId = paramInt1;
        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
        localObject1 = MessagesController.getInstance();
        if (paramInt3 != 0) {
          break label517;
        }
        l = this.dialog_id;
        if (!AndroidUtilities.isTablet()) {
          break label526;
        }
      }
    }
    label517:
    label526:
    for (paramInt1 = 30;; paramInt1 = 20)
    {
      i = this.startLoadFromMessageId;
      j = this.classGuid;
      boolean bool = ChatObject.isChannel(this.currentChat);
      k = this.lastLoadIndex;
      this.lastLoadIndex = (k + 1);
      ((MessagesController)localObject1).loadMessages(l, paramInt1, i, true, 0, j, 3, 0, bool, k);
      this.returnToMessageId = paramInt2;
      this.returnToLoadIndex = paramInt3;
      this.needSelectFromMessageId = paramBoolean;
      return;
      l = this.mergeDialogId;
      break;
    }
  }
  
  private void searchLinks(final CharSequence paramCharSequence, final boolean paramBoolean)
  {
    if ((this.currentEncryptedChat != null) && ((MessagesController.getInstance().secretWebpagePreview == 0) || (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46))) {
      return;
    }
    int k;
    int j;
    if ((paramBoolean) && (this.foundWebPage != null)) {
      if (this.foundWebPage.url != null)
      {
        k = TextUtils.indexOf(paramCharSequence, this.foundWebPage.url);
        i = 0;
        j = 0;
        if (k != -1) {
          break label249;
        }
        if (this.foundWebPage.display_url != null)
        {
          k = TextUtils.indexOf(paramCharSequence, this.foundWebPage.display_url);
          if ((k == -1) || (this.foundWebPage.display_url.length() + k != paramCharSequence.length())) {
            break label238;
          }
          j = 1;
          if ((k == -1) || (j != 0)) {
            break label244;
          }
        }
      }
    }
    label162:
    label238:
    label244:
    for (int i = paramCharSequence.charAt(this.foundWebPage.display_url.length() + k);; i = 0)
    {
      if ((k != -1) && ((j != 0) || (i == 32) || (i == 44) || (i == 46) || (i == 33) || (i == 47))) {
        break label300;
      }
      this.pendingLinkSearchString = null;
      showReplyPanel(false, null, null, this.foundWebPage, false, true);
      Utilities.searchQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (ChatActivity.this.linkSearchRequestId != 0)
          {
            ConnectionsManager.getInstance().cancelRequest(ChatActivity.this.linkSearchRequestId, true);
            ChatActivity.access$11602(ChatActivity.this, 0);
          }
          int j;
          int i;
          for (;;)
          {
            try
            {
              localObject3 = AndroidUtilities.WEB_URL.matcher(paramCharSequence);
              localObject1 = null;
            }
            catch (Exception localException1)
            {
              Object localObject1;
              FileLog.e("tmessages", localException1);
              Object localObject2 = paramCharSequence.toString().toLowerCase();
              if ((paramCharSequence.length() < 13) || ((!((String)localObject2).contains("http://")) && (!((String)localObject2).contains("https://"))))
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    if (ChatActivity.this.foundWebPage != null)
                    {
                      ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                      ChatActivity.access$10302(ChatActivity.this, null);
                    }
                  }
                });
                return;
              }
              localObject2 = paramCharSequence;
              continue;
              final Object localObject3 = new TLRPC.TL_messages_getWebPagePreview();
              if ((localObject2 instanceof String))
              {
                ((TLRPC.TL_messages_getWebPagePreview)localObject3).message = ((String)localObject2);
                ChatActivity.access$11602(ChatActivity.this, ConnectionsManager.getInstance().sendRequest((TLObject)localObject3, new RequestDelegate()
                {
                  public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        ChatActivity.access$11602(ChatActivity.this, 0);
                        if (paramAnonymous2TL_error == null)
                        {
                          if (!(paramAnonymous2TLObject instanceof TLRPC.TL_messageMediaWebPage)) {
                            break label293;
                          }
                          ChatActivity.access$10302(ChatActivity.this, ((TLRPC.TL_messageMediaWebPage)paramAnonymous2TLObject).webpage);
                          if ((!(ChatActivity.this.foundWebPage instanceof TLRPC.TL_webPage)) && (!(ChatActivity.this.foundWebPage instanceof TLRPC.TL_webPagePending))) {
                            break label230;
                          }
                          if ((ChatActivity.this.foundWebPage instanceof TLRPC.TL_webPagePending)) {
                            ChatActivity.access$11802(ChatActivity.this, ChatActivity.56.4.this.val$req.message);
                          }
                          if ((ChatActivity.this.currentEncryptedChat != null) && ((ChatActivity.this.foundWebPage instanceof TLRPC.TL_webPagePending))) {
                            ChatActivity.this.foundWebPage.url = ChatActivity.56.4.this.val$req.message;
                          }
                          ChatActivity.this.showReplyPanel(true, null, null, ChatActivity.this.foundWebPage, false, true);
                        }
                        label230:
                        label293:
                        while (ChatActivity.this.foundWebPage == null)
                        {
                          do
                          {
                            return;
                          } while (ChatActivity.this.foundWebPage == null);
                          ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                          ChatActivity.access$10302(ChatActivity.this, null);
                          return;
                        }
                        ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                        ChatActivity.access$10302(ChatActivity.this, null);
                      }
                    });
                  }
                }));
                ConnectionsManager.getInstance().bindRequestToGuid(ChatActivity.this.linkSearchRequestId, ChatActivity.this.classGuid);
                return;
              }
              ((TLRPC.TL_messages_getWebPagePreview)localObject3).message = ((CharSequence)localObject2).toString();
              continue;
            }
            try
            {
              if (!((Matcher)localObject3).find()) {
                continue;
              }
              if ((((Matcher)localObject3).start() > 0) && (paramCharSequence.charAt(((Matcher)localObject3).start() - 1) == '@')) {
                continue;
              }
              if (localObject1 != null) {
                break label425;
              }
              localObject1 = new ArrayList();
            }
            catch (Exception localException2)
            {
              continue;
              continue;
              i += 1;
              continue;
              if (j == 0) {
                continue;
              }
            }
            ((ArrayList)localObject1).add(paramCharSequence.subSequence(((Matcher)localObject3).start(), ((Matcher)localObject3).end()));
            continue;
            if ((localObject1 != null) && (ChatActivity.this.foundUrls != null) && (((ArrayList)localObject1).size() == ChatActivity.this.foundUrls.size()))
            {
              j = 1;
              i = 0;
              if (i >= ((ArrayList)localObject1).size()) {
                break label435;
              }
              if (TextUtils.equals((CharSequence)((ArrayList)localObject1).get(i), (CharSequence)ChatActivity.this.foundUrls.get(i))) {
                break label428;
              }
              j = 0;
              break label428;
            }
            ChatActivity.access$11702(ChatActivity.this, (ArrayList)localObject1);
            if (localObject1 == null)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (ChatActivity.this.foundWebPage != null)
                  {
                    ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                    ChatActivity.access$10302(ChatActivity.this, null);
                  }
                }
              });
              return;
            }
            localObject1 = TextUtils.join(" ", (Iterable)localObject1);
            if ((ChatActivity.this.currentEncryptedChat != null) && (MessagesController.getInstance().secretWebpagePreview == 2))
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                  localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                  localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                    {
                      MessagesController.getInstance().secretWebpagePreview = 1;
                      ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                      ChatActivity.access$11702(ChatActivity.this, null);
                      ChatActivity.this.searchLinks(ChatActivity.56.this.val$charSequence, ChatActivity.56.this.val$force);
                    }
                  });
                  localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                  localBuilder.setMessage(LocaleController.getString("SecretLinkPreviewAlert", 2131166227));
                  ChatActivity.this.showDialog(localBuilder.create());
                  MessagesController.getInstance().secretWebpagePreview = 0;
                  ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                }
              });
              return;
            }
          }
          label425:
          label428:
          label435:
          return;
        }
      });
      return;
      j = 0;
      break;
    }
    label249:
    if (this.foundWebPage.url.length() + k == paramCharSequence.length())
    {
      j = 1;
      label274:
      if (j != 0) {
        break label308;
      }
    }
    label300:
    label308:
    for (i = paramCharSequence.charAt(this.foundWebPage.url.length() + k);; i = 0)
    {
      break label162;
      break;
      j = 0;
      break label274;
    }
  }
  
  private void sendBotInlineResult(TLRPC.BotInlineResult paramBotInlineResult)
  {
    int i = this.mentionsAdapter.getContextBotId();
    HashMap localHashMap = new HashMap();
    localHashMap.put("id", paramBotInlineResult.id);
    localHashMap.put("query_id", "" + paramBotInlineResult.query_id);
    localHashMap.put("bot", "" + i);
    localHashMap.put("bot_name", this.mentionsAdapter.getContextBotName());
    SendMessagesHelper.prepareSendingBotContextResult(paramBotInlineResult, localHashMap, this.dialog_id, this.replyingMessageObject);
    this.chatActivityEnterView.setFieldText("");
    showReplyPanel(false, null, null, null, false, true);
    SearchQuery.increaseInlineRaiting(i);
  }
  
  private boolean sendSecretMessageRead(MessageObject paramMessageObject)
  {
    if ((paramMessageObject == null) || (paramMessageObject.isOut()) || (!paramMessageObject.isSecretMedia()) || (paramMessageObject.messageOwner.destroyTime != 0) || (paramMessageObject.messageOwner.ttl <= 0)) {
      return false;
    }
    MessagesController.getInstance().markMessageAsRead(this.dialog_id, paramMessageObject.messageOwner.random_id, paramMessageObject.messageOwner.ttl);
    paramMessageObject.messageOwner.destroyTime = (paramMessageObject.messageOwner.ttl + ConnectionsManager.getInstance().getCurrentTime());
    return true;
  }
  
  private void showAttachmentError()
  {
    if (getParentActivity() == null) {
      return;
    }
    Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", 2131166358), 0).show();
  }
  
  private void showEditDoneProgress(final boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.editDoneItemAnimation != null) {
      this.editDoneItemAnimation.cancel();
    }
    if (!paramBoolean2)
    {
      if (paramBoolean1)
      {
        this.editDoneItem.getImageView().setScaleX(0.1F);
        this.editDoneItem.getImageView().setScaleY(0.1F);
        this.editDoneItem.getImageView().setAlpha(0.0F);
        this.editDoneItemProgress.setScaleX(1.0F);
        this.editDoneItemProgress.setScaleY(1.0F);
        this.editDoneItemProgress.setAlpha(1.0F);
        this.editDoneItem.getImageView().setVisibility(4);
        this.editDoneItemProgress.setVisibility(0);
        this.editDoneItem.setEnabled(false);
        return;
      }
      this.editDoneItemProgress.setScaleX(0.1F);
      this.editDoneItemProgress.setScaleY(0.1F);
      this.editDoneItemProgress.setAlpha(0.0F);
      this.editDoneItem.getImageView().setScaleX(1.0F);
      this.editDoneItem.getImageView().setScaleY(1.0F);
      this.editDoneItem.getImageView().setAlpha(1.0F);
      this.editDoneItem.getImageView().setVisibility(0);
      this.editDoneItemProgress.setVisibility(4);
      this.editDoneItem.setEnabled(true);
      return;
    }
    this.editDoneItemAnimation = new AnimatorSet();
    if (paramBoolean1)
    {
      this.editDoneItemProgress.setVisibility(0);
      this.editDoneItem.setEnabled(false);
      this.editDoneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[] { 1.0F }) });
    }
    for (;;)
    {
      this.editDoneItemAnimation.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ChatActivity.this.editDoneItemAnimation != null) && (ChatActivity.this.editDoneItemAnimation.equals(paramAnonymousAnimator))) {
            ChatActivity.access$14202(ChatActivity.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ChatActivity.this.editDoneItemAnimation != null) && (ChatActivity.this.editDoneItemAnimation.equals(paramAnonymousAnimator)))
          {
            if (!paramBoolean1) {
              ChatActivity.this.editDoneItemProgress.setVisibility(4);
            }
          }
          else {
            return;
          }
          ChatActivity.this.editDoneItem.getImageView().setVisibility(4);
        }
      });
      this.editDoneItemAnimation.setDuration(150L);
      this.editDoneItemAnimation.start();
      return;
      this.editDoneItem.getImageView().setVisibility(0);
      this.editDoneItem.setEnabled(true);
      this.editDoneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", new float[] { 1.0F }) });
    }
  }
  
  private void showGifHint()
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    if (((SharedPreferences)localObject).getBoolean("gifhint", false)) {}
    int i;
    do
    {
      do
      {
        do
        {
          return;
          ((SharedPreferences)localObject).edit().putBoolean("gifhint", true).commit();
        } while ((getParentActivity() == null) || (this.fragmentView == null) || (this.gifHintTextView != null));
        if (this.allowContextBotPanelSecond) {
          break;
        }
      } while (this.chatActivityEnterView == null);
      this.chatActivityEnterView.setOpenGifsTabFirst();
      return;
      localObject = (SizeNotifierFrameLayout)this.fragmentView;
      i = ((SizeNotifierFrameLayout)localObject).indexOfChild(this.chatActivityEnterView);
    } while (i == -1);
    this.chatActivityEnterView.setOpenGifsTabFirst();
    this.emojiButtonRed = new View(getParentActivity());
    this.emojiButtonRed.setBackgroundResource(2130837954);
    ((SizeNotifierFrameLayout)localObject).addView(this.emojiButtonRed, i + 1, LayoutHelper.createFrame(10, 10.0F, 83, 30.0F, 0.0F, 0.0F, 27.0F));
    this.gifHintTextView = new TextView(getParentActivity());
    this.gifHintTextView.setBackgroundResource(2130838022);
    this.gifHintTextView.setTextColor(-1);
    this.gifHintTextView.setTextSize(1, 14.0F);
    this.gifHintTextView.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.gifHintTextView.setText(LocaleController.getString("TapHereGifs", 2131166325));
    this.gifHintTextView.setGravity(16);
    ((SizeNotifierFrameLayout)localObject).addView(this.gifHintTextView, i + 1, LayoutHelper.createFrame(-2, 32.0F, 83, 5.0F, 0.0F, 0.0F, 3.0F));
    localObject = new AnimatorSet();
    ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.gifHintTextView, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.emojiButtonRed, "alpha", new float[] { 0.0F, 1.0F }) });
    ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (ChatActivity.this.gifHintTextView == null) {
              return;
            }
            AnimatorSet localAnimatorSet = new AnimatorSet();
            localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(ChatActivity.this.gifHintTextView, "alpha", new float[] { 0.0F }) });
            localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationEnd(Animator paramAnonymous3Animator)
              {
                if (ChatActivity.this.gifHintTextView != null) {
                  ChatActivity.this.gifHintTextView.setVisibility(8);
                }
              }
            });
            localAnimatorSet.setDuration(300L);
            localAnimatorSet.start();
          }
        }, 2000L);
      }
    });
    ((AnimatorSet)localObject).setDuration(300L);
    ((AnimatorSet)localObject).start();
  }
  
  private void showPagedownButton(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.pagedownButton == null) {}
    do
    {
      do
      {
        return;
        if (!paramBoolean1) {
          break;
        }
        this.pagedownButtonShowedByScroll = false;
      } while (this.pagedownButton.getTag() != null);
      if (this.pagedownButtonAnimation != null)
      {
        this.pagedownButtonAnimation.cancel();
        this.pagedownButtonAnimation = null;
      }
      if (paramBoolean2)
      {
        if (this.pagedownButton.getTranslationY() == 0.0F) {
          this.pagedownButton.setTranslationY(AndroidUtilities.dp(100.0F));
        }
        this.pagedownButton.setVisibility(0);
        this.pagedownButton.setTag(Integer.valueOf(1));
        this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[] { 0.0F }).setDuration(200L);
        this.pagedownButtonAnimation.start();
        return;
      }
      this.pagedownButton.setVisibility(0);
      return;
      this.returnToMessageId = 0;
      this.newUnreadMessageCount = 0;
    } while (this.pagedownButton.getTag() == null);
    this.pagedownButton.setTag(null);
    if (this.pagedownButtonAnimation != null)
    {
      this.pagedownButtonAnimation.cancel();
      this.pagedownButtonAnimation = null;
    }
    if (paramBoolean2)
    {
      this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[] { AndroidUtilities.dp(100.0F) }).setDuration(200L);
      this.pagedownButtonAnimation.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ChatActivity.this.pagedownButtonCounter.setVisibility(4);
          ChatActivity.this.pagedownButton.setVisibility(4);
        }
      });
      this.pagedownButtonAnimation.start();
      return;
    }
    this.pagedownButton.setVisibility(4);
  }
  
  private void toggleMute(boolean paramBoolean)
  {
    if (!MessagesController.getInstance().isDialogMuted(this.dialog_id))
    {
      if (paramBoolean)
      {
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
        ((SharedPreferences.Editor)localObject).putInt("notify2_" + this.dialog_id, 2);
        MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 1L);
        ((SharedPreferences.Editor)localObject).commit();
        localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
        if (localObject != null)
        {
          ((TLRPC.TL_dialog)localObject).notify_settings = new TLRPC.TL_peerNotifySettings();
          ((TLRPC.TL_dialog)localObject).notify_settings.mute_until = Integer.MAX_VALUE;
        }
        NotificationsController.updateServerNotificationsSettings(this.dialog_id);
        NotificationsController.getInstance().removeNotificationsForDialog(this.dialog_id);
        return;
      }
      showDialog(AlertsCreator.createMuteAlert(getParentActivity(), this.dialog_id));
      return;
    }
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
    ((SharedPreferences.Editor)localObject).putInt("notify2_" + this.dialog_id, 0);
    MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 0L);
    ((SharedPreferences.Editor)localObject).commit();
    localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
    if (localObject != null) {
      ((TLRPC.TL_dialog)localObject).notify_settings = new TLRPC.TL_peerNotifySettings();
    }
    NotificationsController.updateServerNotificationsSettings(this.dialog_id);
  }
  
  private void updateActionModeTitle()
  {
    if (!this.actionBar.isActionModeShowed()) {}
    while ((this.selectedMessagesIds[0].isEmpty()) && (this.selectedMessagesIds[1].isEmpty())) {
      return;
    }
    this.selectedMessagesCountTextView.setNumber(this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size(), true);
  }
  
  private void updateBotButtons()
  {
    if ((this.headerItem == null) || (this.currentUser == null) || (this.currentEncryptedChat != null) || (!this.currentUser.bot)) {
      return;
    }
    int k = 0;
    int j = 0;
    int m = 0;
    int i = 0;
    if (!this.botInfo.isEmpty())
    {
      Iterator localIterator = this.botInfo.entrySet().iterator();
      TLRPC.BotInfo localBotInfo;
      int n;
      int i1;
      label109:
      do
      {
        k = j;
        m = i;
        if (!localIterator.hasNext()) {
          break;
        }
        localBotInfo = (TLRPC.BotInfo)((Map.Entry)localIterator.next()).getValue();
        n = 0;
        m = i;
        i1 = j;
        j = i1;
        i = m;
      } while (n >= localBotInfo.commands.size());
      TLRPC.TL_botCommand localTL_botCommand = (TLRPC.TL_botCommand)localBotInfo.commands.get(n);
      if (localTL_botCommand.command.toLowerCase().equals("help")) {
        k = 1;
      }
      for (;;)
      {
        if (m != 0)
        {
          j = k;
          i = m;
          if (k != 0) {
            break;
          }
        }
        n += 1;
        i1 = k;
        break label109;
        k = i1;
        if (localTL_botCommand.command.toLowerCase().equals("settings"))
        {
          m = 1;
          k = i1;
        }
      }
    }
    if (k != 0) {
      this.headerItem.showSubItem(30);
    }
    while (m != 0)
    {
      this.headerItem.showSubItem(31);
      return;
      this.headerItem.hideSubItem(30);
    }
    this.headerItem.hideSubItem(31);
  }
  
  private void updateBottomOverlay()
  {
    if (this.bottomOverlayChatText == null) {
      return;
    }
    if (this.currentChat != null) {
      if ((ChatObject.isChannel(this.currentChat)) && (!(this.currentChat instanceof TLRPC.TL_channelForbidden))) {
        if (ChatObject.isNotInChat(this.currentChat))
        {
          this.bottomOverlayChatText.setText(LocaleController.getString("ChannelJoin", 2131165429));
          if ((this.searchItem == null) || (this.searchItem.getVisibility() != 0)) {
            break label384;
          }
          this.searchContainer.setVisibility(0);
          this.bottomOverlayChat.setVisibility(4);
          this.chatActivityEnterView.setFieldFocused(false);
          this.chatActivityEnterView.setVisibility(4);
        }
      }
    }
    label302:
    label384:
    label571:
    for (;;)
    {
      checkRaiseSensors();
      return;
      if (!MessagesController.getInstance().isDialogMuted(this.dialog_id))
      {
        this.bottomOverlayChatText.setText(LocaleController.getString("ChannelMute", 2131165461));
        break;
      }
      this.bottomOverlayChatText.setText(LocaleController.getString("ChannelUnmute", 2131165480));
      break;
      this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisGroup", 2131165579));
      break;
      if (this.userBlocked)
      {
        if (this.currentUser.bot) {
          this.bottomOverlayChatText.setText(LocaleController.getString("BotUnblock", 2131165378));
        }
        for (;;)
        {
          if (this.botButtons == null) {
            break label302;
          }
          this.botButtons = null;
          if (this.chatActivityEnterView == null) {
            break;
          }
          if ((this.replyingMessageObject != null) && (this.botReplyButtons == this.replyingMessageObject))
          {
            this.botReplyButtons = null;
            showReplyPanel(false, null, null, null, false, true);
          }
          this.chatActivityEnterView.setButtons(this.botButtons, false);
          break;
          this.bottomOverlayChatText.setText(LocaleController.getString("Unblock", 2131166349));
        }
        break;
      }
      if ((this.botUser != null) && (this.currentUser.bot))
      {
        this.bottomOverlayChatText.setText(LocaleController.getString("BotStart", 2131165374));
        this.chatActivityEnterView.hidePopup(false);
        if (getParentActivity() == null) {
          break;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        break;
      }
      this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisChat", 2131165578));
      break;
      this.searchContainer.setVisibility(4);
      if (((this.currentChat != null) && ((ChatObject.isNotInChat(this.currentChat)) || (!ChatObject.canWriteToChat(this.currentChat)))) || ((this.currentUser != null) && ((UserObject.isDeleted(this.currentUser)) || (this.userBlocked))))
      {
        this.bottomOverlayChat.setVisibility(0);
        if (this.muteItem != null) {
          this.muteItem.setVisibility(8);
        }
        this.chatActivityEnterView.setFieldFocused(false);
        this.chatActivityEnterView.setVisibility(4);
        this.attachItem.setVisibility(8);
        this.headerItem.setVisibility(0);
      }
      else
      {
        if ((this.botUser != null) && (this.currentUser.bot))
        {
          this.bottomOverlayChat.setVisibility(0);
          this.chatActivityEnterView.setVisibility(4);
        }
        for (;;)
        {
          if (this.muteItem == null) {
            break label571;
          }
          this.muteItem.setVisibility(0);
          break;
          this.chatActivityEnterView.setVisibility(0);
          this.bottomOverlayChat.setVisibility(4);
        }
      }
    }
  }
  
  private void updateContactStatus()
  {
    if (this.addContactItem == null) {
      return;
    }
    if (this.currentUser == null) {
      this.addContactItem.setVisibility(8);
    }
    for (;;)
    {
      checkListViewPaddings();
      return;
      TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
      if (localUser != null) {
        this.currentUser = localUser;
      }
      if (((this.currentEncryptedChat != null) && (!(this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat))) || (this.currentUser.id / 1000 == 333) || (this.currentUser.id / 1000 == 777) || (UserObject.isDeleted(this.currentUser)) || (ContactsController.getInstance().isLoadingContacts()) || ((this.currentUser.phone != null) && (this.currentUser.phone.length() != 0) && (ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null) && ((ContactsController.getInstance().contactsDict.size() != 0) || (!ContactsController.getInstance().isLoadingContacts()))))
      {
        this.addContactItem.setVisibility(8);
      }
      else
      {
        this.addContactItem.setVisibility(0);
        if ((this.currentUser.phone != null) && (this.currentUser.phone.length() != 0))
        {
          this.addContactItem.setText(LocaleController.getString("AddToContacts", 2131165269));
          this.reportSpamButton.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(50.0F), 0);
          this.addToContactsButton.setVisibility(0);
          this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 0.5F, 51, 0, 0, 0, AndroidUtilities.dp(1.0F)));
        }
        else
        {
          this.addContactItem.setText(LocaleController.getString("ShareMyContactInfo", 2131166277));
          this.addToContactsButton.setVisibility(8);
          this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0F), 0, AndroidUtilities.dp(50.0F), 0);
          this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 1.0F, 51, 0, 0, 0, AndroidUtilities.dp(1.0F)));
        }
      }
    }
  }
  
  private void updateInformationForScreenshotDetector()
  {
    if (this.currentEncryptedChat == null) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    if (this.chatListView != null)
    {
      int j = this.chatListView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.chatListView.getChildAt(i);
        MessageObject localMessageObject = null;
        if ((localView instanceof ChatMessageCell)) {
          localMessageObject = ((ChatMessageCell)localView).getMessageObject();
        }
        if ((localMessageObject != null) && (localMessageObject.getId() < 0) && (localMessageObject.messageOwner.random_id != 0L)) {
          localArrayList.add(Long.valueOf(localMessageObject.messageOwner.random_id));
        }
        i += 1;
      }
    }
    MediaController.getInstance().setLastEncryptedChatParams(this.chatEnterTime, this.chatLeaveTime, this.currentEncryptedChat, localArrayList);
  }
  
  private void updateMessagesVisisblePart()
  {
    if (this.chatListView == null) {
      return;
    }
    int n = this.chatListView.getChildCount();
    label34:
    int i1;
    int i;
    label45:
    Object localObject;
    if (this.chatActivityEnterView.isTopViewVisible())
    {
      AndroidUtilities.dp(48.0F);
      i1 = this.chatListView.getMeasuredHeight();
      i = 0;
      if (i < n)
      {
        localObject = this.chatListView.getChildAt(i);
        if ((localObject instanceof ChatMessageCell))
        {
          localObject = (ChatMessageCell)localObject;
          j = ((ChatMessageCell)localObject).getTop();
          ((ChatMessageCell)localObject).getBottom();
          if (j < 0) {
            break label135;
          }
        }
      }
    }
    label135:
    for (int j = 0;; j = -j)
    {
      int m = ((ChatMessageCell)localObject).getMeasuredHeight();
      int k = m;
      if (m > i1) {
        k = j + i1;
      }
      ((ChatMessageCell)localObject).setVisiblePart(j, k - j);
      i += 1;
      break label45;
      break;
      break label34;
    }
  }
  
  private void updatePinnedMessageView(boolean paramBoolean)
  {
    if (this.pinnedMessageView == null) {
      return;
    }
    if (this.info != null)
    {
      if ((this.pinnedMessageObject != null) && (this.info.pinned_msg_id != this.pinnedMessageObject.getId())) {
        this.pinnedMessageObject = null;
      }
      if ((this.info.pinned_msg_id != 0) && (this.pinnedMessageObject == null)) {
        this.pinnedMessageObject = ((MessageObject)this.messagesDict[0].get(Integer.valueOf(this.info.pinned_msg_id)));
      }
    }
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    if ((this.info == null) || (this.info.pinned_msg_id == 0) || (this.info.pinned_msg_id == ((SharedPreferences)localObject1).getInt("pin_" + this.dialog_id, 0)) || ((this.actionBar != null) && (this.actionBar.isActionModeShowed()))) {
      hidePinnedMessageView(paramBoolean);
    }
    for (;;)
    {
      checkListViewPaddings();
      return;
      if (this.pinnedMessageObject != null)
      {
        label313:
        FrameLayout.LayoutParams localLayoutParams1;
        FrameLayout.LayoutParams localLayoutParams2;
        Object localObject2;
        int i;
        if (this.pinnedMessageView.getTag() != null)
        {
          this.pinnedMessageView.setTag(null);
          if (this.pinnedMessageViewAnimator != null)
          {
            this.pinnedMessageViewAnimator.cancel();
            this.pinnedMessageViewAnimator = null;
          }
          if (paramBoolean)
          {
            this.pinnedMessageView.setVisibility(0);
            this.pinnedMessageViewAnimator = new AnimatorSet();
            this.pinnedMessageViewAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", new float[] { 0.0F }) });
            this.pinnedMessageViewAnimator.setDuration(200L);
            this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationCancel(Animator paramAnonymousAnimator)
              {
                if ((ChatActivity.this.pinnedMessageViewAnimator != null) && (ChatActivity.this.pinnedMessageViewAnimator.equals(paramAnonymousAnimator))) {
                  ChatActivity.access$13302(ChatActivity.this, null);
                }
              }
              
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                if ((ChatActivity.this.pinnedMessageViewAnimator != null) && (ChatActivity.this.pinnedMessageViewAnimator.equals(paramAnonymousAnimator))) {
                  ChatActivity.access$13302(ChatActivity.this, null);
                }
              }
            });
            this.pinnedMessageViewAnimator.start();
          }
        }
        else
        {
          localLayoutParams1 = (FrameLayout.LayoutParams)this.pinnedMessageNameTextView.getLayoutParams();
          localLayoutParams2 = (FrameLayout.LayoutParams)this.pinnedMessageTextView.getLayoutParams();
          localObject2 = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs2, AndroidUtilities.dp(50.0F));
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs, AndroidUtilities.dp(50.0F));
          }
          if ((localObject1 != null) && (!(localObject1 instanceof TLRPC.TL_photoSizeEmpty)) && (!(((TLRPC.PhotoSize)localObject1).location instanceof TLRPC.TL_fileLocationUnavailable)) && (this.pinnedMessageObject.type != 13)) {
            break label558;
          }
          this.pinnedMessageImageView.setImageBitmap(null);
          this.pinnedImageLocation = null;
          this.pinnedMessageImageView.setVisibility(4);
          i = AndroidUtilities.dp(18.0F);
          localLayoutParams2.leftMargin = i;
        }
        for (localLayoutParams1.leftMargin = i;; localLayoutParams1.leftMargin = i)
        {
          this.pinnedMessageNameTextView.setLayoutParams(localLayoutParams1);
          this.pinnedMessageTextView.setLayoutParams(localLayoutParams2);
          this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedMessage", 2131166125));
          if (this.pinnedMessageObject.type != 14) {
            break label614;
          }
          this.pinnedMessageTextView.setText(String.format("%s - %s", new Object[] { this.pinnedMessageObject.getMusicAuthor(), this.pinnedMessageObject.getMusicTitle() }));
          break;
          this.pinnedMessageView.setTranslationY(0.0F);
          this.pinnedMessageView.setVisibility(0);
          break label313;
          label558:
          this.pinnedImageLocation = ((TLRPC.PhotoSize)localObject1).location;
          this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable)null);
          this.pinnedMessageImageView.setVisibility(0);
          i = AndroidUtilities.dp(55.0F);
          localLayoutParams2.leftMargin = i;
        }
        label614:
        if ((this.pinnedMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
        {
          this.pinnedMessageTextView.setText(Emoji.replaceEmoji(this.pinnedMessageObject.messageOwner.media.game.title, this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
        }
        else if (this.pinnedMessageObject.messageText != null)
        {
          localObject2 = this.pinnedMessageObject.messageText.toString();
          localObject1 = localObject2;
          if (((String)localObject2).length() > 150) {
            localObject1 = ((String)localObject2).substring(0, 150);
          }
          localObject1 = ((String)localObject1).replace('\n', ' ');
          this.pinnedMessageTextView.setText(Emoji.replaceEmoji((CharSequence)localObject1, this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
        }
      }
      else
      {
        this.pinnedImageLocation = null;
        hidePinnedMessageView(paramBoolean);
        if (this.loadingPinnedMessage != this.info.pinned_msg_id)
        {
          this.loadingPinnedMessage = this.info.pinned_msg_id;
          MessagesQuery.loadPinnedMessage(this.currentChat.id, this.info.pinned_msg_id, true);
        }
      }
    }
  }
  
  private void updateSearchButtons(int paramInt1, int paramInt2, int paramInt3)
  {
    float f2 = 1.0F;
    ImageView localImageView;
    boolean bool;
    if (this.searchUpButton != null)
    {
      localImageView = this.searchUpButton;
      if ((paramInt1 & 0x1) == 0) {
        break label122;
      }
      bool = true;
      localImageView.setEnabled(bool);
      localImageView = this.searchDownButton;
      if ((paramInt1 & 0x2) == 0) {
        break label128;
      }
      bool = true;
      label47:
      localImageView.setEnabled(bool);
      localImageView = this.searchUpButton;
      if (!this.searchUpButton.isEnabled()) {
        break label134;
      }
      f1 = 1.0F;
      label73:
      localImageView.setAlpha(f1);
      localImageView = this.searchDownButton;
      if (!this.searchDownButton.isEnabled()) {
        break label142;
      }
    }
    label122:
    label128:
    label134:
    label142:
    for (float f1 = f2;; f1 = 0.5F)
    {
      localImageView.setAlpha(f1);
      if (paramInt3 != 0) {
        break label150;
      }
      this.searchCountText.setText("");
      return;
      bool = false;
      break;
      bool = false;
      break label47;
      f1 = 0.5F;
      break label73;
    }
    label150:
    this.searchCountText.setText(LocaleController.formatString("Of", 2131166045, new Object[] { Integer.valueOf(paramInt2 + 1), Integer.valueOf(paramInt3) }));
  }
  
  private void updateSecretStatus()
  {
    if (this.bottomOverlay == null) {
      return;
    }
    if ((this.currentEncryptedChat == null) || (this.bigEmptyView == null))
    {
      this.bottomOverlay.setVisibility(4);
      return;
    }
    int j = 0;
    int i;
    if ((this.currentEncryptedChat instanceof TLRPC.TL_encryptedChatRequested))
    {
      this.bottomOverlayText.setText(LocaleController.getString("EncryptionProcessing", 2131165616));
      this.bottomOverlay.setVisibility(0);
      i = 1;
    }
    for (;;)
    {
      checkRaiseSensors();
      if (i != 0)
      {
        this.chatActivityEnterView.hidePopup(false);
        if (getParentActivity() != null) {
          AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        }
      }
      checkActionBarMenu();
      return;
      if ((this.currentEncryptedChat instanceof TLRPC.TL_encryptedChatWaiting))
      {
        this.bottomOverlayText.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AwaitingEncryption", 2131165355, new Object[] { "<b>" + this.currentUser.first_name + "</b>" })));
        this.bottomOverlay.setVisibility(0);
        i = 1;
      }
      else if ((this.currentEncryptedChat instanceof TLRPC.TL_encryptedChatDiscarded))
      {
        this.bottomOverlayText.setText(LocaleController.getString("EncryptionRejected", 2131165617));
        this.bottomOverlay.setVisibility(0);
        this.chatActivityEnterView.setFieldText("");
        DraftQuery.cleanDraft(this.dialog_id, false);
        i = 1;
      }
      else
      {
        i = j;
        if ((this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat))
        {
          this.bottomOverlay.setVisibility(4);
          i = j;
        }
      }
    }
  }
  
  private void updateSpamView()
  {
    if (this.reportSpamView == null) {
      return;
    }
    int i;
    int j;
    if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("spam3_" + this.dialog_id, 0) == 2)
    {
      i = 1;
      j = i;
      if (i != 0)
      {
        if (!this.messages.isEmpty()) {
          break label202;
        }
        j = 0;
      }
      if (j != 0) {
        break label260;
      }
      if (this.reportSpamView.getTag() == null)
      {
        this.reportSpamView.setTag(Integer.valueOf(1));
        if (this.reportSpamViewAnimator != null) {
          this.reportSpamViewAnimator.cancel();
        }
        this.reportSpamViewAnimator = new AnimatorSet();
        this.reportSpamViewAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.reportSpamView, "translationY", new float[] { -AndroidUtilities.dp(50.0F) }) });
        this.reportSpamViewAnimator.setDuration(200L);
        this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.reportSpamViewAnimator != null) && (ChatActivity.this.reportSpamViewAnimator.equals(paramAnonymousAnimator))) {
              ChatActivity.access$13502(ChatActivity.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.reportSpamViewAnimator != null) && (ChatActivity.this.reportSpamViewAnimator.equals(paramAnonymousAnimator)))
            {
              ChatActivity.this.reportSpamView.setVisibility(8);
              ChatActivity.access$13502(ChatActivity.this, null);
            }
          }
        });
        this.reportSpamViewAnimator.start();
      }
    }
    for (;;)
    {
      checkListViewPaddings();
      return;
      i = 0;
      break;
      label202:
      int m = this.messages.size() - 1;
      int k = m;
      for (;;)
      {
        j = i;
        if (k < Math.max(m - 50, 0)) {
          break;
        }
        if (((MessageObject)this.messages.get(k)).isOut())
        {
          j = 0;
          break;
        }
        k -= 1;
      }
      label260:
      if (this.reportSpamView.getTag() != null)
      {
        this.reportSpamView.setTag(null);
        this.reportSpamView.setVisibility(0);
        if (this.reportSpamViewAnimator != null) {
          this.reportSpamViewAnimator.cancel();
        }
        this.reportSpamViewAnimator = new AnimatorSet();
        this.reportSpamViewAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.reportSpamView, "translationY", new float[] { 0.0F }) });
        this.reportSpamViewAnimator.setDuration(200L);
        this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.reportSpamViewAnimator != null) && (ChatActivity.this.reportSpamViewAnimator.equals(paramAnonymousAnimator))) {
              ChatActivity.access$13502(ChatActivity.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivity.this.reportSpamViewAnimator != null) && (ChatActivity.this.reportSpamViewAnimator.equals(paramAnonymousAnimator))) {
              ChatActivity.access$13502(ChatActivity.this, null);
            }
          }
        });
        this.reportSpamViewAnimator.start();
      }
    }
  }
  
  private void updateTitle()
  {
    if (this.avatarContainer == null) {}
    do
    {
      return;
      if (this.currentChat != null)
      {
        this.avatarContainer.setTitle(this.currentChat.title);
        return;
      }
    } while (this.currentUser == null);
    if (this.currentUser.self)
    {
      this.avatarContainer.setTitle(LocaleController.getString("ChatYourSelfName", 2131165503));
      return;
    }
    if ((this.currentUser.id / 1000 != 777) && (this.currentUser.id / 1000 != 333) && (ContactsController.getInstance().contactsDict.get(this.currentUser.id) == null) && ((ContactsController.getInstance().contactsDict.size() != 0) || (!ContactsController.getInstance().isLoadingContacts())))
    {
      if ((this.currentUser.phone != null) && (this.currentUser.phone.length() != 0))
      {
        this.avatarContainer.setTitle(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
        return;
      }
      this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
      return;
    }
    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
  }
  
  private void updateTitleIcons()
  {
    int j = 0;
    if (this.avatarContainer == null) {
      return;
    }
    if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {}
    for (int i = 2130837850;; i = 0)
    {
      ChatAvatarContainer localChatAvatarContainer = this.avatarContainer;
      if (this.currentEncryptedChat != null) {
        j = 2130837737;
      }
      localChatAvatarContainer.setTitleIcons(j, i);
      if (this.muteItem == null) {
        break;
      }
      if (i == 0) {
        break label82;
      }
      this.muteItem.setText(LocaleController.getString("UnmuteNotifications", 2131166354));
      return;
    }
    label82:
    this.muteItem.setText(LocaleController.getString("MuteNotifications", 2131165908));
  }
  
  private void updateVisibleRows()
  {
    if (this.chatListView == null) {
      return;
    }
    int n = this.chatListView.getChildCount();
    MessageObject localMessageObject1;
    label33:
    int j;
    label35:
    Object localObject;
    ChatMessageCell localChatMessageCell;
    int i;
    int m;
    int k;
    label118:
    label146:
    label154:
    boolean bool1;
    label171:
    boolean bool2;
    if (this.chatActivityEnterView != null)
    {
      localMessageObject1 = this.chatActivityEnterView.getEditingMessageObject();
      j = 0;
      if (j < n)
      {
        localObject = this.chatListView.getChildAt(j);
        if (!(localObject instanceof ChatMessageCell)) {
          break label333;
        }
        localChatMessageCell = (ChatMessageCell)localObject;
        i = 0;
        m = 0;
        k = 0;
        if (!this.actionBar.isActionModeShowed()) {
          break label294;
        }
        MessageObject localMessageObject2 = localChatMessageCell.getMessageObject();
        if (localMessageObject2 != localMessageObject1)
        {
          HashMap[] arrayOfHashMap = this.selectedMessagesIds;
          if (localMessageObject2.getDialogId() != this.dialog_id) {
            break label278;
          }
          i = 0;
          if (!arrayOfHashMap[i].containsKey(Integer.valueOf(localMessageObject2.getId()))) {
            break label283;
          }
        }
        ((View)localObject).setBackgroundColor(1714664933);
        i = 1;
        m = 1;
        k = i;
        i = m;
        localChatMessageCell.setMessageObject(localChatMessageCell.getMessageObject());
        if (i != 0) {
          break label306;
        }
        bool1 = true;
        if ((i == 0) || (k == 0)) {
          break label312;
        }
        bool2 = true;
        label182:
        localChatMessageCell.setCheckPressed(bool1, bool2);
        if ((this.highlightMessageId == Integer.MAX_VALUE) || (localChatMessageCell.getMessageObject() == null) || (localChatMessageCell.getMessageObject().getId() != this.highlightMessageId)) {
          break label318;
        }
        bool1 = true;
        label227:
        localChatMessageCell.setHighlighted(bool1);
        if ((this.searchContainer == null) || (this.searchContainer.getVisibility() != 0) || (MessagesSearchQuery.getLastSearchQuery() == null)) {
          break label324;
        }
        localChatMessageCell.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
      }
    }
    for (;;)
    {
      j += 1;
      break label35;
      break;
      localMessageObject1 = null;
      break label33;
      label278:
      i = 1;
      break label118;
      label283:
      ((View)localObject).setBackgroundColor(0);
      i = k;
      break label146;
      label294:
      ((View)localObject).setBackgroundColor(0);
      k = m;
      break label154;
      label306:
      bool1 = false;
      break label171;
      label312:
      bool2 = false;
      break label182;
      label318:
      bool1 = false;
      break label227;
      label324:
      localChatMessageCell.setHighlightedText(null);
      continue;
      label333:
      if ((localObject instanceof ChatActionCell))
      {
        localObject = (ChatActionCell)localObject;
        ((ChatActionCell)localObject).setMessageObject(((ChatActionCell)localObject).getMessageObject());
      }
    }
  }
  
  public boolean allowCaption()
  {
    return true;
  }
  
  public boolean cancelButtonPressed()
  {
    return true;
  }
  
  public View createView(Context paramContext)
  {
    if (this.chatMessageCellsCache.isEmpty())
    {
      i = 0;
      while (i < 8)
      {
        this.chatMessageCellsCache.add(new ChatMessageCell(paramContext));
        i += 1;
      }
    }
    int i = 1;
    while (i >= 0)
    {
      this.selectedMessagesIds[i].clear();
      this.selectedMessagesCanCopyIds[i].clear();
      i -= 1;
    }
    this.cantDeleteMessagesCount = 0;
    this.hasOwnBackground = true;
    if (this.chatAttachAlert != null)
    {
      this.chatAttachAlert.onDestroy();
      this.chatAttachAlert = null;
    }
    Theme.loadRecources(paramContext);
    Theme.loadChatResources(paramContext);
    this.actionBar.setAddToContainer(false);
    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(final int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          if (ChatActivity.this.actionBar.isActionModeShowed())
          {
            paramAnonymousInt = 1;
            while (paramAnonymousInt >= 0)
            {
              ChatActivity.this.selectedMessagesIds[paramAnonymousInt].clear();
              ChatActivity.this.selectedMessagesCanCopyIds[paramAnonymousInt].clear();
              paramAnonymousInt -= 1;
            }
            ChatActivity.access$1002(ChatActivity.this, 0);
            if (ChatActivity.this.chatActivityEnterView.isEditingMessage())
            {
              ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
              ChatActivity.this.updateVisibleRows();
            }
          }
        }
        label458:
        label483:
        label590:
        label749:
        label790:
        label914:
        label1259:
        do
        {
          do
          {
            Object localObject1;
            Object localObject2;
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      return;
                      ChatActivity.this.actionBar.hideActionMode();
                      ChatActivity.this.updatePinnedMessageView(true);
                      break;
                      ChatActivity.this.finishFragment();
                      return;
                      if (paramAnonymousInt == 10)
                      {
                        localObject1 = "";
                        int i = 0;
                        paramAnonymousInt = 1;
                        while (paramAnonymousInt >= 0)
                        {
                          ArrayList localArrayList = new ArrayList(ChatActivity.this.selectedMessagesCanCopyIds[paramAnonymousInt].keySet());
                          if (ChatActivity.this.currentEncryptedChat == null) {
                            Collections.sort(localArrayList);
                          }
                          for (;;)
                          {
                            int j = 0;
                            while (j < localArrayList.size())
                            {
                              localObject2 = (Integer)localArrayList.get(j);
                              MessageObject localMessageObject = (MessageObject)ChatActivity.this.selectedMessagesCanCopyIds[paramAnonymousInt].get(localObject2);
                              localObject2 = localObject1;
                              if (((String)localObject1).length() != 0) {
                                localObject2 = (String)localObject1 + "\n\n";
                              }
                              localObject1 = (String)localObject2 + ChatActivity.this.getMessageContent(localMessageObject, i, true);
                              i = localMessageObject.messageOwner.from_id;
                              j += 1;
                            }
                            Collections.sort(localArrayList, Collections.reverseOrder());
                          }
                          paramAnonymousInt -= 1;
                        }
                        if (((String)localObject1).length() != 0) {
                          AndroidUtilities.addToClipboard((CharSequence)localObject1);
                        }
                        paramAnonymousInt = 1;
                        while (paramAnonymousInt >= 0)
                        {
                          ChatActivity.this.selectedMessagesIds[paramAnonymousInt].clear();
                          ChatActivity.this.selectedMessagesCanCopyIds[paramAnonymousInt].clear();
                          paramAnonymousInt -= 1;
                        }
                        ChatActivity.access$1002(ChatActivity.this, 0);
                        ChatActivity.this.actionBar.hideActionMode();
                        ChatActivity.this.updatePinnedMessageView(true);
                        ChatActivity.this.updateVisibleRows();
                        return;
                      }
                      if (paramAnonymousInt != 20) {
                        break label458;
                      }
                    } while ((ChatActivity.this.chatActivityEnterView == null) || ((!ChatActivity.this.chatActivityEnterView.isEditingCaption()) && (!ChatActivity.this.chatActivityEnterView.hasText())));
                    ChatActivity.this.chatActivityEnterView.doneEditingMessage();
                    return;
                    if (paramAnonymousInt != 12) {
                      break label483;
                    }
                  } while (ChatActivity.this.getParentActivity() == null);
                  ChatActivity.this.createDeleteMessagesAlert(null);
                  return;
                  if (paramAnonymousInt == 11)
                  {
                    localObject1 = new Bundle();
                    ((Bundle)localObject1).putBoolean("onlySelect", true);
                    ((Bundle)localObject1).putInt("dialogsType", 1);
                    localObject1 = new DialogsActivity((Bundle)localObject1);
                    ((DialogsActivity)localObject1).setDelegate(ChatActivity.this);
                    ChatActivity.this.presentFragment((BaseFragment)localObject1);
                    return;
                  }
                  if (paramAnonymousInt != 13) {
                    break label590;
                  }
                } while (ChatActivity.this.getParentActivity() == null);
                ChatActivity.this.showDialog(AndroidUtilities.buildTTLAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.currentEncryptedChat).create());
                return;
                if ((paramAnonymousInt != 15) && (paramAnonymousInt != 16)) {
                  break label790;
                }
              } while (ChatActivity.this.getParentActivity() == null);
              final boolean bool;
              if (((int)ChatActivity.this.dialog_id < 0) && ((int)(ChatActivity.this.dialog_id >> 32) != 1))
              {
                bool = true;
                localObject1 = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
                if (paramAnonymousInt != 15) {
                  break label749;
                }
                ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureClearHistory", 2131165315));
              }
              for (;;)
              {
                ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    if (paramAnonymousInt != 15)
                    {
                      if (bool) {
                        if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                          MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                        }
                      }
                      for (;;)
                      {
                        ChatActivity.this.finishFragment();
                        return;
                        MessagesController.getInstance().deleteUserFromChat((int)-ChatActivity.this.dialog_id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                        continue;
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                      }
                    }
                    MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 1);
                  }
                });
                ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                ChatActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                return;
                bool = false;
                break;
                if (bool) {
                  ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureDeleteAndExit", 2131165318));
                } else {
                  ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureDeleteThisChat", 2131165322));
                }
              }
              if (paramAnonymousInt != 17) {
                break label914;
              }
            } while ((ChatActivity.this.currentUser == null) || (ChatActivity.this.getParentActivity() == null));
            if ((ChatActivity.this.currentUser.phone != null) && (ChatActivity.this.currentUser.phone.length() != 0))
            {
              localObject1 = new Bundle();
              ((Bundle)localObject1).putInt("user_id", ChatActivity.this.currentUser.id);
              ((Bundle)localObject1).putBoolean("addContact", true);
              ChatActivity.this.presentFragment(new ContactAddActivity((Bundle)localObject1));
              return;
            }
            ChatActivity.this.shareMyContact(ChatActivity.this.replyingMessageObject);
            return;
            if (paramAnonymousInt == 18)
            {
              ChatActivity.this.toggleMute(false);
              return;
            }
            if (paramAnonymousInt == 21)
            {
              ChatActivity.this.showDialog(AlertsCreator.createReportAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.dialog_id, ChatActivity.this));
              return;
            }
            if (paramAnonymousInt == 19)
            {
              localObject1 = null;
              paramAnonymousInt = 1;
              while (paramAnonymousInt >= 0)
              {
                localObject2 = localObject1;
                if (localObject1 == null)
                {
                  localObject2 = localObject1;
                  if (ChatActivity.this.selectedMessagesIds[paramAnonymousInt].size() == 1)
                  {
                    localObject1 = new ArrayList(ChatActivity.this.selectedMessagesIds[paramAnonymousInt].keySet());
                    localObject2 = (MessageObject)ChatActivity.this.messagesDict[paramAnonymousInt].get(((ArrayList)localObject1).get(0));
                  }
                }
                ChatActivity.this.selectedMessagesIds[paramAnonymousInt].clear();
                ChatActivity.this.selectedMessagesCanCopyIds[paramAnonymousInt].clear();
                paramAnonymousInt -= 1;
                localObject1 = localObject2;
              }
              if ((localObject1 != null) && ((((MessageObject)localObject1).messageOwner.id > 0) || ((((MessageObject)localObject1).messageOwner.id < 0) && (ChatActivity.this.currentEncryptedChat != null)))) {
                ChatActivity.this.showReplyPanel(true, (MessageObject)localObject1, null, null, false, true);
              }
              ChatActivity.access$1002(ChatActivity.this, 0);
              ChatActivity.this.actionBar.hideActionMode();
              ChatActivity.this.updatePinnedMessageView(true);
              ChatActivity.this.updateVisibleRows();
              return;
            }
            if (paramAnonymousInt != 14) {
              break label1259;
            }
          } while (ChatActivity.this.getParentActivity() == null);
          ChatActivity.this.createChatAttachView();
          ChatActivity.this.chatAttachAlert.loadGalleryPhotos();
          if ((Build.VERSION.SDK_INT == 21) || (Build.VERSION.SDK_INT == 22)) {
            ChatActivity.this.chatActivityEnterView.closeKeyboard();
          }
          ChatActivity.this.chatAttachAlert.init();
          ChatActivity.this.showDialog(ChatActivity.this.chatAttachAlert);
          return;
          if (paramAnonymousInt == 30)
          {
            SendMessagesHelper.getInstance().sendMessage("/help", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            return;
          }
          if (paramAnonymousInt == 31)
          {
            SendMessagesHelper.getInstance().sendMessage("/settings", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            return;
          }
        } while (paramAnonymousInt != 40);
        ChatActivity.this.openSearchWithText(null);
      }
    });
    boolean bool1;
    if (this.currentEncryptedChat != null) {
      bool1 = true;
    }
    for (;;)
    {
      this.avatarContainer = new ChatAvatarContainer(paramContext, this, bool1);
      this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0F, 51, 56.0F, 0.0F, 40.0F, 0.0F));
      if ((this.currentChat != null) && (!ChatObject.isChannel(this.currentChat)))
      {
        i = this.currentChat.participants_count;
        if (this.info != null) {
          i = this.info.participants.participants.size();
        }
        if ((i == 0) || (this.currentChat.deactivated) || (this.currentChat.left) || ((this.currentChat instanceof TLRPC.TL_chatForbidden)) || ((this.info != null) && ((this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)))) {
          this.avatarContainer.setEnabled(false);
        }
      }
      Object localObject1 = this.actionBar.createMenu();
      if ((this.currentEncryptedChat == null) && (!this.isBroadcast))
      {
        this.searchItem = ((ActionBarMenu)localObject1).addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
        {
          public void onSearchCollapse()
          {
            ChatActivity.this.avatarContainer.setVisibility(0);
            if (ChatActivity.this.chatActivityEnterView.hasText())
            {
              if (ChatActivity.this.headerItem != null) {
                ChatActivity.this.headerItem.setVisibility(8);
              }
              if (ChatActivity.this.attachItem != null) {
                ChatActivity.this.attachItem.setVisibility(0);
              }
            }
            for (;;)
            {
              ChatActivity.this.searchItem.setVisibility(8);
              ChatActivity.access$2902(ChatActivity.this, Integer.MAX_VALUE);
              ChatActivity.this.updateVisibleRows();
              ChatActivity.this.scrollToLastMessage(false);
              ChatActivity.this.updateBottomOverlay();
              return;
              if (ChatActivity.this.headerItem != null) {
                ChatActivity.this.headerItem.setVisibility(0);
              }
              if (ChatActivity.this.attachItem != null) {
                ChatActivity.this.attachItem.setVisibility(8);
              }
            }
          }
          
          public void onSearchExpand()
          {
            if (!ChatActivity.this.openSearchKeyboard) {
              return;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChatActivity.this.searchItem.getSearchField().requestFocus();
                AndroidUtilities.showKeyboard(ChatActivity.this.searchItem.getSearchField());
              }
            }, 300L);
          }
          
          public void onSearchPressed(EditText paramAnonymousEditText)
          {
            ChatActivity.this.updateSearchButtons(0, 0, 0);
            MessagesSearchQuery.searchMessagesInChat(paramAnonymousEditText.getText().toString(), ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, 0);
          }
        });
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", 2131166206));
        this.searchItem.setVisibility(8);
      }
      this.headerItem = ((ActionBarMenu)localObject1).addItem(0, 2130837708);
      if (this.searchItem != null) {
        this.headerItem.addSubItem(40, LocaleController.getString("Search", 2131166206), 0);
      }
      if ((ChatObject.isChannel(this.currentChat)) && (!this.currentChat.creator) && ((!this.currentChat.megagroup) || ((this.currentChat.username != null) && (this.currentChat.username.length() > 0)))) {
        this.headerItem.addSubItem(21, LocaleController.getString("ReportChat", 2131166159), 0);
      }
      if (this.currentUser != null) {
        this.addContactItem = this.headerItem.addSubItem(17, "", 0);
      }
      if (this.currentEncryptedChat != null) {
        this.timeItem2 = this.headerItem.addSubItem(13, LocaleController.getString("SetTimer", 2131166269), 0);
      }
      label618:
      Object localObject2;
      Object localObject3;
      if (!ChatObject.isChannel(this.currentChat))
      {
        this.headerItem.addSubItem(15, LocaleController.getString("ClearHistory", 2131165509), 0);
        if ((this.currentChat != null) && (!this.isBroadcast)) {
          this.headerItem.addSubItem(16, LocaleController.getString("DeleteAndExit", 2131165566), 0);
        }
      }
      else
      {
        if ((this.currentUser == null) || (!this.currentUser.self)) {
          this.muteItem = this.headerItem.addSubItem(18, null, 0);
        }
        if ((this.currentUser != null) && (this.currentEncryptedChat == null) && (this.currentUser.bot))
        {
          this.headerItem.addSubItem(31, LocaleController.getString("BotSettings", 2131165372), 0);
          this.headerItem.addSubItem(30, LocaleController.getString("BotHelp", 2131165367), 0);
          updateBotButtons();
        }
        updateTitle();
        this.avatarContainer.updateOnlineCount();
        this.avatarContainer.updateSubtitle();
        updateTitleIcons();
        this.attachItem = ((ActionBarMenu)localObject1).addItem(14, 2130837708).setOverrideMenuClick(true).setAllowCloseAnimation(false);
        this.attachItem.setVisibility(8);
        this.menuItem = ((ActionBarMenu)localObject1).addItem(14, 2130837698).setAllowCloseAnimation(false);
        this.menuItem.setBackgroundDrawable(null);
        this.actionModeViews.clear();
        localObject1 = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(((ActionBarMenu)localObject1).getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(-9211021);
        ((ActionBarMenu)localObject1).addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0F, 65, 0, 0, 0));
        this.selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        this.actionModeTitleContainer = new FrameLayout(paramContext)
        {
          protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
          {
            paramAnonymousInt2 = paramAnonymousInt4 - paramAnonymousInt2;
            float f;
            if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8)
            {
              paramAnonymousInt1 = (paramAnonymousInt2 / 2 - ChatActivity.this.actionModeTextView.getTextHeight()) / 2;
              if ((!AndroidUtilities.isTablet()) && (getResources().getConfiguration().orientation == 2))
              {
                f = 2.0F;
                paramAnonymousInt1 += AndroidUtilities.dp(f);
              }
            }
            for (;;)
            {
              ChatActivity.this.actionModeTextView.layout(0, paramAnonymousInt1, ChatActivity.this.actionModeTextView.getMeasuredWidth(), ChatActivity.this.actionModeTextView.getTextHeight() + paramAnonymousInt1);
              if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8)
              {
                paramAnonymousInt1 = paramAnonymousInt2 / 2;
                paramAnonymousInt2 = (paramAnonymousInt2 / 2 - ChatActivity.this.actionModeSubTextView.getTextHeight()) / 2;
                if ((!AndroidUtilities.isTablet()) && (getResources().getConfiguration().orientation == 2)) {}
                paramAnonymousInt1 = paramAnonymousInt1 + paramAnonymousInt2 - AndroidUtilities.dp(1.0F);
                ChatActivity.this.actionModeSubTextView.layout(0, paramAnonymousInt1, ChatActivity.this.actionModeSubTextView.getMeasuredWidth(), ChatActivity.this.actionModeSubTextView.getTextHeight() + paramAnonymousInt1);
              }
              return;
              f = 3.0F;
              break;
              paramAnonymousInt1 = (paramAnonymousInt2 - ChatActivity.this.actionModeTextView.getTextHeight()) / 2;
            }
          }
          
          protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            int i = View.MeasureSpec.getSize(paramAnonymousInt1);
            setMeasuredDimension(i, View.MeasureSpec.getSize(paramAnonymousInt2));
            SimpleTextView localSimpleTextView = ChatActivity.this.actionModeTextView;
            if ((!AndroidUtilities.isTablet()) && (getResources().getConfiguration().orientation == 2))
            {
              paramAnonymousInt1 = 18;
              localSimpleTextView.setTextSize(paramAnonymousInt1);
              ChatActivity.this.actionModeTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), Integer.MIN_VALUE));
              if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8)
              {
                localSimpleTextView = ChatActivity.this.actionModeSubTextView;
                if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
                  break label164;
                }
              }
            }
            label164:
            for (paramAnonymousInt1 = 14;; paramAnonymousInt1 = 16)
            {
              localSimpleTextView.setTextSize(paramAnonymousInt1);
              ChatActivity.this.actionModeSubTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), Integer.MIN_VALUE));
              return;
              paramAnonymousInt1 = 20;
              break;
            }
          }
        };
        ((ActionBarMenu)localObject1).addView(this.actionModeTitleContainer, LayoutHelper.createLinear(0, -1, 1.0F, 65, 0, 0, 0));
        this.actionModeTitleContainer.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        this.actionModeTitleContainer.setVisibility(8);
        this.actionModeTextView = new SimpleTextView(paramContext);
        this.actionModeTextView.setTextSize(18);
        this.actionModeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.actionModeTextView.setTextColor(-9211021);
        this.actionModeTextView.setText(LocaleController.getString("Edit", 2131165592));
        this.actionModeTitleContainer.addView(this.actionModeTextView, LayoutHelper.createFrame(-1, -1.0F));
        this.actionModeSubTextView = new SimpleTextView(paramContext);
        this.actionModeSubTextView.setGravity(3);
        this.actionModeSubTextView.setTextColor(-9211021);
        this.actionModeTitleContainer.addView(this.actionModeSubTextView, LayoutHelper.createFrame(-1, -1.0F));
        if (this.currentEncryptedChat != null) {
          break label5243;
        }
        if (!this.isBroadcast) {
          this.actionModeViews.add(((ActionBarMenu)localObject1).addItem(19, 2130837709, -986896, null, AndroidUtilities.dp(54.0F)));
        }
        this.actionModeViews.add(((ActionBarMenu)localObject1).addItem(10, 2130837704, -986896, null, AndroidUtilities.dp(54.0F)));
        this.actionModeViews.add(((ActionBarMenu)localObject1).addItem(11, 2130837706, -986896, null, AndroidUtilities.dp(54.0F)));
        this.actionModeViews.add(((ActionBarMenu)localObject1).addItem(12, 2130837705, -986896, null, AndroidUtilities.dp(54.0F)));
        localObject2 = this.actionModeViews;
        localObject3 = ((ActionBarMenu)localObject1).addItem(20, 2130837591, -986896, null, AndroidUtilities.dp(54.0F));
        this.editDoneItem = ((ActionBarMenuItem)localObject3);
        ((ArrayList)localObject2).add(localObject3);
        this.editDoneItem.setVisibility(8);
        this.editDoneItemProgress = new ContextProgressView(paramContext, 0);
        this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, -1.0F));
        this.editDoneItemProgress.setVisibility(4);
        label1295:
        localObject2 = ((ActionBarMenu)localObject1).getItem(10);
        if (this.selectedMessagesCanCopyIds[0].size() + this.selectedMessagesCanCopyIds[1].size() == 0) {
          break label5330;
        }
        i = 0;
        label1328:
        ((ActionBarMenuItem)localObject2).setVisibility(i);
        localObject1 = ((ActionBarMenu)localObject1).getItem(12);
        if (this.cantDeleteMessagesCount != 0) {
          break label5336;
        }
        i = 0;
        label1352:
        ((ActionBarMenuItem)localObject1).setVisibility(i);
        checkActionBarMenu();
        this.fragmentView = new SizeNotifierFrameLayout(paramContext)
        {
          int inputFieldHeight = 0;
          
          protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
          {
            boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
            if (paramAnonymousView == ChatActivity.this.actionBar) {
              ChatActivity.this.parentLayout.drawHeaderShadow(paramAnonymousCanvas, ChatActivity.this.actionBar.getMeasuredHeight());
            }
            return bool;
          }
          
          protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
          {
            int i1 = getChildCount();
            if ((getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) && (!AndroidUtilities.isInMultiwindow)) {}
            View localView;
            for (int m = ChatActivity.this.chatActivityEnterView.getEmojiPadding();; m = 0)
            {
              setBottomClip(m);
              int n = 0;
              for (;;)
              {
                if (n >= i1) {
                  break label648;
                }
                localView = getChildAt(n);
                if (localView.getVisibility() != 8) {
                  break;
                }
                n += 1;
              }
            }
            FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
            int i2 = localView.getMeasuredWidth();
            int i3 = localView.getMeasuredHeight();
            int j = localLayoutParams.gravity;
            int i = j;
            if (j == -1) {
              i = 51;
            }
            int k;
            switch (i & 0x7 & 0x7)
            {
            default: 
              k = localLayoutParams.leftMargin;
              label171:
              switch (i & 0x70)
              {
              default: 
                j = localLayoutParams.topMargin;
                label219:
                if (localView == ChatActivity.this.mentionContainer) {
                  i = j - (ChatActivity.this.chatActivityEnterView.getMeasuredHeight() - AndroidUtilities.dp(2.0F));
                }
                break;
              }
              break;
            }
            for (;;)
            {
              localView.layout(k, i, k + i2, i + i3);
              break;
              k = (paramAnonymousInt3 - paramAnonymousInt1 - i2) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
              break label171;
              k = paramAnonymousInt3 - i2 - localLayoutParams.rightMargin;
              break label171;
              i = localLayoutParams.topMargin + getPaddingTop();
              j = i;
              if (localView == ChatActivity.this.actionBar) {
                break label219;
              }
              j = i + ChatActivity.this.actionBar.getMeasuredHeight();
              break label219;
              j = (paramAnonymousInt4 - m - paramAnonymousInt2 - i3) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
              break label219;
              j = paramAnonymousInt4 - m - paramAnonymousInt2 - i3 - localLayoutParams.bottomMargin;
              break label219;
              if (localView == ChatActivity.this.pagedownButton)
              {
                i = j - ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
              }
              else if (localView == ChatActivity.this.emptyViewContainer)
              {
                i = j - (this.inputFieldHeight / 2 - ChatActivity.this.actionBar.getMeasuredHeight() / 2);
              }
              else if (ChatActivity.this.chatActivityEnterView.isPopupView(localView))
              {
                if (AndroidUtilities.isInMultiwindow) {
                  i = ChatActivity.this.chatActivityEnterView.getTop() - localView.getMeasuredHeight() + AndroidUtilities.dp(1.0F);
                } else {
                  i = ChatActivity.this.chatActivityEnterView.getBottom();
                }
              }
              else if (localView == ChatActivity.this.gifHintTextView)
              {
                i = j - this.inputFieldHeight;
              }
              else if ((localView == ChatActivity.this.chatListView) || (localView == ChatActivity.this.progressView))
              {
                i = j;
                if (ChatActivity.this.chatActivityEnterView.isTopViewVisible()) {
                  i = j - AndroidUtilities.dp(48.0F);
                }
              }
              else
              {
                i = j;
                if (localView == ChatActivity.this.actionBar) {
                  i = j - getPaddingTop();
                }
              }
            }
            label648:
            ChatActivity.this.updateMessagesVisisblePart();
            notifyHeightChanged();
          }
          
          protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            int i1 = View.MeasureSpec.getSize(paramAnonymousInt1);
            int i = View.MeasureSpec.getSize(paramAnonymousInt2);
            setMeasuredDimension(i1, i);
            int j = getPaddingTop();
            measureChildWithMargins(ChatActivity.this.actionBar, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
            int i2 = ChatActivity.this.actionBar.getMeasuredHeight();
            j = i - j - i2;
            i = j;
            if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F))
            {
              i = j;
              if (!AndroidUtilities.isInMultiwindow) {
                i = j - ChatActivity.this.chatActivityEnterView.getEmojiPadding();
              }
            }
            int i3 = getChildCount();
            measureChildWithMargins(ChatActivity.this.chatActivityEnterView, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
            this.inputFieldHeight = ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
            j = 0;
            if (j < i3)
            {
              View localView = getChildAt(j);
              if ((localView == null) || (localView.getVisibility() == 8) || (localView == ChatActivity.this.chatActivityEnterView) || (localView == ChatActivity.this.actionBar)) {}
              for (;;)
              {
                j += 1;
                break;
                int m;
                int n;
                int k;
                if ((localView == ChatActivity.this.chatListView) || (localView == ChatActivity.this.progressView))
                {
                  m = View.MeasureSpec.makeMeasureSpec(i1, 1073741824);
                  n = AndroidUtilities.dp(10.0F);
                  int i4 = this.inputFieldHeight;
                  if (ChatActivity.this.chatActivityEnterView.isTopViewVisible()) {}
                  for (k = 48;; k = 0)
                  {
                    localView.measure(m, View.MeasureSpec.makeMeasureSpec(Math.max(n, AndroidUtilities.dp(k + 2) + (i - i4)), 1073741824));
                    break;
                  }
                }
                if (localView == ChatActivity.this.emptyViewContainer)
                {
                  localView.measure(View.MeasureSpec.makeMeasureSpec(i1, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
                }
                else if (ChatActivity.this.chatActivityEnterView.isPopupView(localView))
                {
                  if (AndroidUtilities.isInMultiwindow)
                  {
                    if (AndroidUtilities.isTablet()) {
                      localView.measure(View.MeasureSpec.makeMeasureSpec(i1, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0F), i - this.inputFieldHeight + i2 - AndroidUtilities.statusBarHeight + getPaddingTop()), 1073741824));
                    } else {
                      localView.measure(View.MeasureSpec.makeMeasureSpec(i1, 1073741824), View.MeasureSpec.makeMeasureSpec(i - this.inputFieldHeight + i2 - AndroidUtilities.statusBarHeight + getPaddingTop(), 1073741824));
                    }
                  }
                  else {
                    localView.measure(View.MeasureSpec.makeMeasureSpec(i1, 1073741824), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, 1073741824));
                  }
                }
                else
                {
                  if (localView == ChatActivity.this.mentionContainer)
                  {
                    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)ChatActivity.this.mentionContainer.getLayoutParams();
                    ChatActivity.access$4802(ChatActivity.this, true);
                    if ((ChatActivity.this.mentionsAdapter.isBotContext()) && (ChatActivity.this.mentionsAdapter.isMediaLayout()))
                    {
                      m = ChatActivity.this.mentionGridLayoutManager.getRowsCount(i1) * 102;
                      k = m;
                      if (ChatActivity.this.mentionsAdapter.isBotContext())
                      {
                        k = m;
                        if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                          k = m + 34;
                        }
                      }
                      n = ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                      if (k != 0) {}
                      for (m = AndroidUtilities.dp(2.0F);; m = 0)
                      {
                        m = i - n + m;
                        ChatActivity.this.mentionListView.setPadding(0, Math.max(0, m - AndroidUtilities.dp(Math.min(k, 122.399994F))), 0, 0);
                        k = m;
                        localLayoutParams.height = k;
                        localLayoutParams.topMargin = 0;
                        ChatActivity.access$4802(ChatActivity.this, false);
                        localView.measure(View.MeasureSpec.makeMeasureSpec(i1, 1073741824), View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824));
                        break;
                      }
                    }
                    n = ChatActivity.this.mentionsAdapter.getItemCount();
                    m = 0;
                    if (ChatActivity.this.mentionsAdapter.isBotContext())
                    {
                      k = n;
                      if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null)
                      {
                        m = 0 + 36;
                        k = n - 1;
                      }
                      k = m + k * 68;
                      label781:
                      n = ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                      if (k == 0) {
                        break label864;
                      }
                    }
                    label864:
                    for (m = AndroidUtilities.dp(2.0F);; m = 0)
                    {
                      m = i - n + m;
                      ChatActivity.this.mentionListView.setPadding(0, Math.max(0, m - AndroidUtilities.dp(Math.min(k, 122.399994F))), 0, 0);
                      k = m;
                      break;
                      k = 0 + n * 36;
                      break label781;
                    }
                  }
                  measureChildWithMargins(localView, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
                }
              }
            }
          }
        };
        localObject2 = (SizeNotifierFrameLayout)this.fragmentView;
        ((SizeNotifierFrameLayout)localObject2).setBackgroundImage(ApplicationLoader.getCachedWallpaper());
        this.emptyViewContainer = new FrameLayout(paramContext);
        this.emptyViewContainer.setVisibility(4);
        ((SizeNotifierFrameLayout)localObject2).addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        if (this.currentEncryptedChat != null) {
          break label5551;
        }
        if ((this.currentUser == null) || (!this.currentUser.self)) {
          break label5342;
        }
        this.bigEmptyView = new ChatBigEmptyView(paramContext, false);
        this.emptyViewContainer.addView(this.bigEmptyView, new FrameLayout.LayoutParams(-2, -2, 17));
        if (this.chatActivityEnterView != null) {
          this.chatActivityEnterView.onDestroy();
        }
        if (this.mentionsAdapter != null) {
          this.mentionsAdapter.onDestroy();
        }
        this.chatListView = new RecyclerListView(paramContext)
        {
          protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
          {
            super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
            ChatActivity.access$5802(ChatActivity.this, false);
            int j;
            int i;
            if (ChatActivity.ChatActivityAdapter.access$6000(ChatActivity.this.chatAdapter))
            {
              j = getChildCount();
              i = 0;
            }
            for (;;)
            {
              if (i < j)
              {
                View localView = getChildAt(i);
                if (!(localView instanceof BotHelpCell)) {
                  break label108;
                }
                paramAnonymousInt2 = (paramAnonymousInt4 - paramAnonymousInt2) / 2 - localView.getMeasuredHeight() / 2;
                if (localView.getTop() > paramAnonymousInt2) {
                  localView.layout(0, paramAnonymousInt2, paramAnonymousInt3 - paramAnonymousInt1, localView.getMeasuredHeight() + paramAnonymousInt2);
                }
              }
              return;
              label108:
              i += 1;
            }
          }
        };
        this.chatListView.setTag(Integer.valueOf(1));
        this.chatListView.setVerticalScrollBarEnabled(true);
        localObject1 = this.chatListView;
        localObject3 = new ChatActivityAdapter(paramContext);
        this.chatAdapter = ((ChatActivityAdapter)localObject3);
        ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject3);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(3.0F));
        this.chatListView.setItemAnimator(null);
        this.chatListView.setLayoutAnimation(null);
        this.chatLayoutManager = new LinearLayoutManager(paramContext)
        {
          public boolean supportsPredictiveItemAnimations()
          {
            return false;
          }
        };
        this.chatLayoutManager.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        ((SizeNotifierFrameLayout)localObject2).addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0F));
        this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.chatListView.setOnItemClickListener(this.onItemClickListener);
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          private final int scrollValue = AndroidUtilities.dp(100.0F);
          private float totalDy = 0.0F;
          
          public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
          {
            if ((paramAnonymousInt == 1) && (ChatActivity.this.highlightMessageId != Integer.MAX_VALUE))
            {
              ChatActivity.access$2902(ChatActivity.this, Integer.MAX_VALUE);
              ChatActivity.this.updateVisibleRows();
            }
          }
          
          public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            ChatActivity.this.checkScrollForLoad(true);
            int i = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
            if (i == -1)
            {
              paramAnonymousInt1 = 0;
              if (paramAnonymousInt1 > 0)
              {
                if ((i + paramAnonymousInt1 != ChatActivity.this.chatAdapter.getItemCount()) || (ChatActivity.this.forwardEndReached[0] == 0)) {
                  break label100;
                }
                ChatActivity.this.showPagedownButton(false, true);
              }
            }
            for (;;)
            {
              ChatActivity.this.updateMessagesVisisblePart();
              return;
              paramAnonymousInt1 = Math.abs(ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition() - i) + 1;
              break;
              label100:
              if (paramAnonymousInt2 > 0)
              {
                if (ChatActivity.this.pagedownButton.getTag() == null)
                {
                  this.totalDy += paramAnonymousInt2;
                  if (this.totalDy > this.scrollValue)
                  {
                    this.totalDy = 0.0F;
                    ChatActivity.this.showPagedownButton(true, true);
                    ChatActivity.access$6502(ChatActivity.this, true);
                  }
                }
              }
              else if ((ChatActivity.this.pagedownButtonShowedByScroll) && (ChatActivity.this.pagedownButton.getTag() != null))
              {
                this.totalDy += paramAnonymousInt2;
                if (this.totalDy < -this.scrollValue)
                {
                  ChatActivity.this.showPagedownButton(false, true);
                  this.totalDy = 0.0F;
                }
              }
            }
          }
        });
        this.chatListView.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            boolean bool = true;
            if ((ChatActivity.this.openSecretPhotoRunnable != null) || (SecretPhotoViewer.getInstance().isVisible()))
            {
              if ((paramAnonymousMotionEvent.getAction() != 1) && (paramAnonymousMotionEvent.getAction() != 3) && (paramAnonymousMotionEvent.getAction() != 6)) {
                break label150;
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                }
              }, 150L);
              if (ChatActivity.this.openSecretPhotoRunnable == null) {
                break label121;
              }
              AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
              ChatActivity.access$6602(ChatActivity.this, null);
            }
            label121:
            label150:
            do
            {
              do
              {
                do
                {
                  for (;;)
                  {
                    try
                    {
                      Toast.makeText(paramAnonymousView.getContext(), LocaleController.getString("PhotoTip", 2131166113), 0).show();
                      bool = false;
                      return bool;
                    }
                    catch (Exception paramAnonymousView)
                    {
                      FileLog.e("tmessages", paramAnonymousView);
                      continue;
                    }
                    if (SecretPhotoViewer.getInstance().isVisible())
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                          ChatActivity.this.chatListView.setLongClickable(true);
                        }
                      });
                      SecretPhotoViewer.getInstance().closePhoto();
                    }
                  }
                } while (paramAnonymousMotionEvent.getAction() == 0);
              } while (SecretPhotoViewer.getInstance().isVisible());
            } while (ChatActivity.this.openSecretPhotoRunnable == null);
            if (paramAnonymousMotionEvent.getAction() == 2) {
              if (Math.hypot(ChatActivity.this.startX - paramAnonymousMotionEvent.getX(), ChatActivity.this.startY - paramAnonymousMotionEvent.getY()) > AndroidUtilities.dp(5.0F))
              {
                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                ChatActivity.access$6602(ChatActivity.this, null);
              }
            }
            for (;;)
            {
              ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
              ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
              ChatActivity.this.chatListView.setLongClickable(true);
              break;
              AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
              ChatActivity.access$6602(ChatActivity.this, null);
            }
          }
        });
        this.chatListView.setOnInterceptTouchListener(new RecyclerListView.OnInterceptTouchListener()
        {
          public boolean onInterceptTouchEvent(final MotionEvent paramAnonymousMotionEvent)
          {
            if ((ChatActivity.this.chatActivityEnterView != null) && (ChatActivity.this.chatActivityEnterView.isEditingMessage())) {
              return true;
            }
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
              return false;
            }
            int j;
            int k;
            int n;
            if (paramAnonymousMotionEvent.getAction() == 0)
            {
              j = (int)paramAnonymousMotionEvent.getX();
              k = (int)paramAnonymousMotionEvent.getY();
              int m = ChatActivity.this.chatListView.getChildCount();
              int i = 0;
              while (i < m)
              {
                paramAnonymousMotionEvent = ChatActivity.this.chatListView.getChildAt(i);
                n = paramAnonymousMotionEvent.getTop();
                int i1 = paramAnonymousMotionEvent.getBottom();
                if ((n > k) || (i1 < k)) {
                  i += 1;
                } else {
                  if ((paramAnonymousMotionEvent instanceof ChatMessageCell)) {
                    break label134;
                  }
                }
              }
            }
            label134:
            final MessageObject localMessageObject;
            do
            {
              return false;
              paramAnonymousMotionEvent = (ChatMessageCell)paramAnonymousMotionEvent;
              localMessageObject = paramAnonymousMotionEvent.getMessageObject();
            } while ((localMessageObject == null) || (localMessageObject.isSending()) || (!localMessageObject.isSecretPhoto()) || (!paramAnonymousMotionEvent.getPhotoImage().isInsideImage(j, k - n)) || (!FileLoader.getPathToMessage(localMessageObject.messageOwner).exists()));
            ChatActivity.access$6702(ChatActivity.this, j);
            ChatActivity.access$6802(ChatActivity.this, k);
            ChatActivity.this.chatListView.setOnItemClickListener(null);
            ChatActivity.access$6602(ChatActivity.this, new Runnable()
            {
              public void run()
              {
                if (ChatActivity.this.openSecretPhotoRunnable == null) {
                  return;
                }
                ChatActivity.this.chatListView.requestDisallowInterceptTouchEvent(true);
                ChatActivity.this.chatListView.setOnItemLongClickListener(null);
                ChatActivity.this.chatListView.setLongClickable(false);
                ChatActivity.access$6602(ChatActivity.this, null);
                if (ChatActivity.this.sendSecretMessageRead(localMessageObject)) {
                  paramAnonymousMotionEvent.invalidate();
                }
                SecretPhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                SecretPhotoViewer.getInstance().openPhoto(localMessageObject);
              }
            });
            AndroidUtilities.runOnUIThread(ChatActivity.this.openSecretPhotoRunnable, 100L);
            return true;
          }
        });
        this.progressView = new FrameLayout(paramContext);
        this.progressView.setVisibility(4);
        ((SizeNotifierFrameLayout)localObject2).addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        localObject1 = new View(paramContext);
        ((View)localObject1).setBackgroundResource(2130838001);
        ((View)localObject1).getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView((View)localObject1, LayoutHelper.createFrame(36, 36, 17));
        localObject1 = new ProgressBar(paramContext);
      }
      try
      {
        ((ProgressBar)localObject1).setIndeterminateDrawable(paramContext.getResources().getDrawable(2130837801));
        ((ProgressBar)localObject1).setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration((ProgressBar)localObject1, 1500);
        this.progressView.addView((View)localObject1, LayoutHelper.createFrame(32, 32, 17));
        if (ChatObject.isChannel(this.currentChat))
        {
          this.pinnedMessageView = new FrameLayout(paramContext);
          this.pinnedMessageView.setTag(Integer.valueOf(1));
          this.pinnedMessageView.setTranslationY(-AndroidUtilities.dp(50.0F));
          this.pinnedMessageView.setVisibility(8);
          this.pinnedMessageView.setBackgroundResource(2130837549);
          ((SizeNotifierFrameLayout)localObject2).addView(this.pinnedMessageView, LayoutHelper.createFrame(-1, 50, 51));
          this.pinnedMessageView.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              ChatActivity.this.scrollToMessageId(ChatActivity.this.info.pinned_msg_id, 0, true, 0);
            }
          });
          localObject1 = new View(paramContext);
          ((View)localObject1).setBackgroundColor(-9658414);
          this.pinnedMessageView.addView((View)localObject1, LayoutHelper.createFrame(2, 32.0F, 51, 8.0F, 8.0F, 0.0F, 0.0F));
          this.pinnedMessageImageView = new BackupImageView(paramContext);
          this.pinnedMessageView.addView(this.pinnedMessageImageView, LayoutHelper.createFrame(32, 32.0F, 51, 17.0F, 8.0F, 0.0F, 0.0F));
          this.pinnedMessageNameTextView = new SimpleTextView(paramContext);
          this.pinnedMessageNameTextView.setTextSize(14);
          this.pinnedMessageNameTextView.setTextColor(-12940081);
          this.pinnedMessageNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.pinnedMessageView.addView(this.pinnedMessageNameTextView, LayoutHelper.createFrame(-1, AndroidUtilities.dp(18.0F), 51, 18.0F, 7.3F, 52.0F, 0.0F));
          this.pinnedMessageTextView = new SimpleTextView(paramContext);
          this.pinnedMessageTextView.setTextSize(14);
          this.pinnedMessageTextView.setTextColor(-6710887);
          this.pinnedMessageView.addView(this.pinnedMessageTextView, LayoutHelper.createFrame(-1, AndroidUtilities.dp(18.0F), 51, 18.0F, 25.3F, 52.0F, 0.0F));
          localObject1 = new ImageView(paramContext);
          ((ImageView)localObject1).setImageResource(2130837829);
          ((ImageView)localObject1).setScaleType(ImageView.ScaleType.CENTER);
          this.pinnedMessageView.addView((View)localObject1, LayoutHelper.createFrame(48, 48, 53));
          ((ImageView)localObject1).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChatActivity.this.getParentActivity() == null) {
                return;
              }
              if ((ChatActivity.this.currentChat.creator) || (ChatActivity.this.currentChat.editor))
              {
                paramAnonymousView = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                paramAnonymousView.setMessage(LocaleController.getString("UnpinMessageAlert", 2131166357));
                paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, 0, false);
                  }
                });
                paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
                paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                ChatActivity.this.showDialog(paramAnonymousView.create());
                return;
              }
              ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("pin_" + ChatActivity.this.dialog_id, ChatActivity.this.info.pinned_msg_id).commit();
              ChatActivity.this.updatePinnedMessageView(true);
            }
          });
        }
        this.reportSpamView = new LinearLayout(paramContext);
        this.reportSpamView.setTag(Integer.valueOf(1));
        this.reportSpamView.setTranslationY(-AndroidUtilities.dp(50.0F));
        this.reportSpamView.setVisibility(8);
        this.reportSpamView.setBackgroundResource(2130837549);
        ((SizeNotifierFrameLayout)localObject2).addView(this.reportSpamView, LayoutHelper.createFrame(-1, 50, 51));
        this.addToContactsButton = new TextView(paramContext);
        this.addToContactsButton.setTextColor(-11894091);
        this.addToContactsButton.setVisibility(8);
        this.addToContactsButton.setTextSize(1, 14.0F);
        this.addToContactsButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addToContactsButton.setSingleLine(true);
        this.addToContactsButton.setMaxLines(1);
        this.addToContactsButton.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
        this.addToContactsButton.setGravity(17);
        this.addToContactsButton.setText(LocaleController.getString("AddContactChat", 2131165257));
        this.reportSpamView.addView(this.addToContactsButton, LayoutHelper.createLinear(-1, -1, 0.5F, 51, 0, 0, 0, AndroidUtilities.dp(1.0F)));
        this.addToContactsButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new Bundle();
            paramAnonymousView.putInt("user_id", ChatActivity.this.currentUser.id);
            paramAnonymousView.putBoolean("addContact", true);
            ChatActivity.this.presentFragment(new ContactAddActivity(paramAnonymousView));
          }
        });
        this.reportSpamContainer = new FrameLayout(paramContext);
        this.reportSpamView.addView(this.reportSpamContainer, LayoutHelper.createLinear(-1, -1, 1.0F, 51, 0, 0, 0, AndroidUtilities.dp(1.0F)));
        this.reportSpamButton = new TextView(paramContext);
        this.reportSpamButton.setTextColor(-3188393);
        this.reportSpamButton.setTextSize(1, 14.0F);
        this.reportSpamButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.reportSpamButton.setSingleLine(true);
        this.reportSpamButton.setMaxLines(1);
        label2644:
        label3375:
        label3411:
        label3435:
        label3822:
        boolean bool2;
        if (this.currentChat != null)
        {
          this.reportSpamButton.setText(LocaleController.getString("ReportSpamAndLeave", 2131166169));
          this.reportSpamButton.setGravity(17);
          this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0F), 0, AndroidUtilities.dp(50.0F), 0);
          this.reportSpamContainer.addView(this.reportSpamButton, LayoutHelper.createFrame(-1, -1, 51));
          this.reportSpamButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChatActivity.this.getParentActivity() == null) {
                return;
              }
              paramAnonymousView = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
              if ((ChatObject.isChannel(ChatActivity.this.currentChat)) && (!ChatActivity.this.currentChat.megagroup)) {
                paramAnonymousView.setMessage(LocaleController.getString("ReportSpamAlertChannel", 2131166167));
              }
              for (;;)
              {
                paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
                paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    if (ChatActivity.this.currentUser != null) {
                      MessagesController.getInstance().blockUser(ChatActivity.this.currentUser.id);
                    }
                    MessagesController.getInstance().reportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat);
                    ChatActivity.this.updateSpamView();
                    if (ChatActivity.this.currentChat != null) {
                      if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                      }
                    }
                    for (;;)
                    {
                      ChatActivity.this.finishFragment();
                      return;
                      MessagesController.getInstance().deleteUserFromChat((int)-ChatActivity.this.dialog_id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                      continue;
                      MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                    }
                  }
                });
                paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                ChatActivity.this.showDialog(paramAnonymousView.create());
                return;
                if (ChatActivity.this.currentChat != null) {
                  paramAnonymousView.setMessage(LocaleController.getString("ReportSpamAlertGroup", 2131166168));
                } else {
                  paramAnonymousView.setMessage(LocaleController.getString("ReportSpamAlert", 2131166166));
                }
              }
            }
          });
          localObject1 = new ImageView(paramContext);
          ((ImageView)localObject1).setImageResource(2130837829);
          ((ImageView)localObject1).setScaleType(ImageView.ScaleType.CENTER);
          this.reportSpamContainer.addView((View)localObject1, LayoutHelper.createFrame(48, 48, 53));
          ((ImageView)localObject1).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              MessagesController.getInstance().hideReportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat);
              ChatActivity.this.updateSpamView();
            }
          });
          this.alertView = new FrameLayout(paramContext);
          this.alertView.setTag(Integer.valueOf(1));
          this.alertView.setTranslationY(-AndroidUtilities.dp(50.0F));
          this.alertView.setVisibility(8);
          this.alertView.setBackgroundResource(2130837549);
          ((SizeNotifierFrameLayout)localObject2).addView(this.alertView, LayoutHelper.createFrame(-1, 50, 51));
          this.alertNameTextView = new TextView(paramContext);
          this.alertNameTextView.setTextSize(1, 14.0F);
          this.alertNameTextView.setTextColor(-12940081);
          this.alertNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.alertNameTextView.setSingleLine(true);
          this.alertNameTextView.setEllipsize(TextUtils.TruncateAt.END);
          this.alertNameTextView.setMaxLines(1);
          this.alertView.addView(this.alertNameTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 8.0F, 5.0F, 8.0F, 0.0F));
          this.alertTextView = new TextView(paramContext);
          this.alertTextView.setTextSize(1, 14.0F);
          this.alertTextView.setTextColor(-6710887);
          this.alertTextView.setSingleLine(true);
          this.alertTextView.setEllipsize(TextUtils.TruncateAt.END);
          this.alertTextView.setMaxLines(1);
          this.alertView.addView(this.alertTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 8.0F, 23.0F, 8.0F, 0.0F));
          if (!this.isBroadcast)
          {
            this.mentionContainer = new FrameLayout(paramContext)
            {
              private Drawable background;
              
              public void onDraw(Canvas paramAnonymousCanvas)
              {
                if (ChatActivity.this.mentionListView.getChildCount() <= 0) {
                  return;
                }
                if ((ChatActivity.this.mentionsAdapter.isBotContext()) && (ChatActivity.this.mentionsAdapter.isMediaLayout()) && (ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null)) {
                  this.background.setBounds(0, ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(4.0F), getMeasuredWidth(), getMeasuredHeight());
                }
                for (;;)
                {
                  this.background.draw(paramAnonymousCanvas);
                  return;
                  this.background.setBounds(0, ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(2.0F), getMeasuredWidth(), getMeasuredHeight());
                }
              }
              
              public void requestLayout()
              {
                if (ChatActivity.this.mentionListViewIgnoreLayout) {
                  return;
                }
                super.requestLayout();
              }
              
              public void setBackgroundResource(int paramAnonymousInt)
              {
                this.background = getContext().getResources().getDrawable(paramAnonymousInt);
              }
            };
            this.mentionContainer.setBackgroundResource(2130837623);
            this.mentionContainer.setVisibility(8);
            this.mentionContainer.setWillNotDraw(false);
            ((SizeNotifierFrameLayout)localObject2).addView(this.mentionContainer, LayoutHelper.createFrame(-1, 110, 83));
            this.mentionListView = new RecyclerListView(paramContext)
            {
              private int lastHeight;
              private int lastWidth;
              
              public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
              {
                if ((!ChatActivity.this.mentionListViewIsScrolling) && (ChatActivity.this.mentionListViewScrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < ChatActivity.this.mentionListViewScrollOffsetY)) {}
                boolean bool;
                do
                {
                  return false;
                  bool = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramAnonymousMotionEvent, ChatActivity.this.mentionListView, 0);
                } while ((!super.onInterceptTouchEvent(paramAnonymousMotionEvent)) && (!bool));
                return true;
              }
              
              protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
              {
                int n = paramAnonymousInt3 - paramAnonymousInt1;
                int i1 = paramAnonymousInt4 - paramAnonymousInt2;
                int k = -1;
                int m = 0;
                int j = k;
                int i = m;
                if (ChatActivity.this.mentionListView != null)
                {
                  j = k;
                  i = m;
                  if (ChatActivity.this.mentionListViewLastViewPosition >= 0)
                  {
                    j = k;
                    i = m;
                    if (n == this.lastWidth)
                    {
                      j = k;
                      i = m;
                      if (i1 - this.lastHeight != 0)
                      {
                        j = ChatActivity.this.mentionListViewLastViewPosition;
                        i = ChatActivity.this.mentionListViewLastViewTop + i1 - this.lastHeight - getPaddingTop();
                      }
                    }
                  }
                }
                super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
                if (j != -1)
                {
                  ChatActivity.access$4802(ChatActivity.this, true);
                  if ((!ChatActivity.this.mentionsAdapter.isBotContext()) || (!ChatActivity.this.mentionsAdapter.isMediaLayout())) {
                    break label226;
                  }
                  ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(j, i);
                }
                for (;;)
                {
                  super.onLayout(false, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
                  ChatActivity.access$4802(ChatActivity.this, false);
                  this.lastHeight = i1;
                  this.lastWidth = n;
                  ChatActivity.this.mentionListViewUpdateLayout();
                  return;
                  label226:
                  ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(j, i);
                }
              }
              
              public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
              {
                if ((!ChatActivity.this.mentionListViewIsScrolling) && (ChatActivity.this.mentionListViewScrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < ChatActivity.this.mentionListViewScrollOffsetY)) {
                  return false;
                }
                return super.onTouchEvent(paramAnonymousMotionEvent);
              }
              
              public void requestLayout()
              {
                if (ChatActivity.this.mentionListViewIgnoreLayout) {
                  return;
                }
                super.requestLayout();
              }
            };
            this.mentionListView.setOnTouchListener(new View.OnTouchListener()
            {
              public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
              {
                return StickerPreviewViewer.getInstance().onTouch(paramAnonymousMotionEvent, ChatActivity.this.mentionListView, 0, ChatActivity.this.mentionsOnItemClickListener);
              }
            });
            this.mentionListView.setTag(Integer.valueOf(2));
            this.mentionLayoutManager = new LinearLayoutManager(paramContext)
            {
              public boolean supportsPredictiveItemAnimations()
              {
                return false;
              }
            };
            this.mentionLayoutManager.setOrientation(1);
            this.mentionGridLayoutManager = new ExtendedGridLayoutManager(paramContext, 100)
            {
              private Size size = new Size();
              
              protected int getFlowItemCount()
              {
                if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                  return getItemCount() - 1;
                }
                return super.getFlowItemCount();
              }
              
              protected Size getSizeForItem(int paramAnonymousInt)
              {
                float f2 = 100.0F;
                int i = paramAnonymousInt;
                if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                  i = paramAnonymousInt + 1;
                }
                Object localObject1 = ChatActivity.this.mentionsAdapter.getItem(i);
                Object localObject2;
                float f1;
                if ((localObject1 instanceof TLRPC.BotInlineResult))
                {
                  localObject1 = (TLRPC.BotInlineResult)localObject1;
                  if (((TLRPC.BotInlineResult)localObject1).document == null) {
                    break label229;
                  }
                  localObject2 = this.size;
                  if (((TLRPC.BotInlineResult)localObject1).document.thumb == null) {
                    break label216;
                  }
                  f1 = ((TLRPC.BotInlineResult)localObject1).document.thumb.w;
                  ((Size)localObject2).width = f1;
                  localObject2 = this.size;
                  f1 = f2;
                  if (((TLRPC.BotInlineResult)localObject1).document.thumb != null) {
                    f1 = ((TLRPC.BotInlineResult)localObject1).document.thumb.h;
                  }
                  ((Size)localObject2).height = f1;
                  paramAnonymousInt = 0;
                  label137:
                  if (paramAnonymousInt < ((TLRPC.BotInlineResult)localObject1).document.attributes.size())
                  {
                    localObject2 = (TLRPC.DocumentAttribute)((TLRPC.BotInlineResult)localObject1).document.attributes.get(paramAnonymousInt);
                    if ((!(localObject2 instanceof TLRPC.TL_documentAttributeImageSize)) && (!(localObject2 instanceof TLRPC.TL_documentAttributeVideo))) {
                      break label222;
                    }
                    this.size.width = ((TLRPC.DocumentAttribute)localObject2).w;
                  }
                }
                for (this.size.height = ((TLRPC.DocumentAttribute)localObject2).h;; this.size.height = ((TLRPC.BotInlineResult)localObject1).h)
                {
                  return this.size;
                  label216:
                  f1 = 100.0F;
                  break;
                  label222:
                  paramAnonymousInt += 1;
                  break label137;
                  label229:
                  this.size.width = ((TLRPC.BotInlineResult)localObject1).w;
                }
              }
            };
            this.mentionGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
            {
              public int getSpanSize(int paramAnonymousInt)
              {
                if ((ChatActivity.this.mentionsAdapter.getItem(paramAnonymousInt) instanceof TLRPC.TL_inlineBotSwitchPM)) {
                  return 100;
                }
                int i = paramAnonymousInt;
                if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                  i = paramAnonymousInt - 1;
                }
                return ChatActivity.this.mentionGridLayoutManager.getSpanSizeForItem(i);
              }
            });
            this.mentionListView.addItemDecoration(new RecyclerView.ItemDecoration()
            {
              public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
              {
                int j = 0;
                paramAnonymousRect.left = 0;
                paramAnonymousRect.right = 0;
                paramAnonymousRect.top = 0;
                paramAnonymousRect.bottom = 0;
                if (paramAnonymousRecyclerView.getLayoutManager() == ChatActivity.this.mentionGridLayoutManager)
                {
                  i = paramAnonymousRecyclerView.getChildAdapterPosition(paramAnonymousView);
                  if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                    break label126;
                  }
                  if (i != 0) {}
                }
                else
                {
                  return;
                }
                int k = i - 1;
                int i = k;
                if (!ChatActivity.this.mentionGridLayoutManager.isFirstRow(k))
                {
                  paramAnonymousRect.top = AndroidUtilities.dp(2.0F);
                  i = k;
                }
                if (ChatActivity.this.mentionGridLayoutManager.isLastInRow(i)) {}
                for (i = j;; i = AndroidUtilities.dp(2.0F))
                {
                  paramAnonymousRect.right = i;
                  return;
                  label126:
                  paramAnonymousRect.top = AndroidUtilities.dp(2.0F);
                  break;
                }
              }
            });
            this.mentionListView.setItemAnimator(null);
            this.mentionListView.setLayoutAnimation(null);
            this.mentionListView.setClipToPadding(false);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setOverScrollMode(2);
            this.mentionContainer.addView(this.mentionListView, LayoutHelper.createFrame(-1, -1.0F));
            localObject1 = this.mentionListView;
            localObject3 = new MentionsAdapter(paramContext, false, this.dialog_id, new MentionsAdapter.MentionsAdapterDelegate()
            {
              public void needChangePanelVisibility(boolean paramAnonymousBoolean)
              {
                if ((ChatActivity.this.mentionsAdapter.isBotContext()) && (ChatActivity.this.mentionsAdapter.isMediaLayout()))
                {
                  ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionGridLayoutManager);
                  if (!paramAnonymousBoolean) {
                    break label493;
                  }
                  if (ChatActivity.this.mentionListAnimation != null)
                  {
                    ChatActivity.this.mentionListAnimation.cancel();
                    ChatActivity.access$8002(ChatActivity.this, null);
                  }
                  if (ChatActivity.this.mentionContainer.getVisibility() != 0) {
                    break label121;
                  }
                  ChatActivity.this.mentionContainer.setAlpha(1.0F);
                }
                label121:
                label493:
                do
                {
                  return;
                  ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionLayoutManager);
                  break;
                  if ((ChatActivity.this.mentionsAdapter.isBotContext()) && (ChatActivity.this.mentionsAdapter.isMediaLayout())) {
                    ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(0, 10000);
                  }
                  while ((ChatActivity.this.allowStickersPanel) && ((!ChatActivity.this.mentionsAdapter.isBotContext()) || (ChatActivity.this.allowContextBotPanel) || (ChatActivity.this.allowContextBotPanelSecond)))
                  {
                    if ((ChatActivity.this.currentEncryptedChat != null) && (ChatActivity.this.mentionsAdapter.isBotContext()))
                    {
                      SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                      if (!localSharedPreferences.getBoolean("secretbot", false))
                      {
                        AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                        localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                        localBuilder.setMessage(LocaleController.getString("SecretChatContextBotAlert", 2131166226));
                        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                        ChatActivity.this.showDialog(localBuilder.create());
                        localSharedPreferences.edit().putBoolean("secretbot", true).commit();
                      }
                    }
                    ChatActivity.this.mentionContainer.setVisibility(0);
                    ChatActivity.this.mentionContainer.setTag(null);
                    ChatActivity.access$8002(ChatActivity.this, new AnimatorSet());
                    ChatActivity.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", new float[] { 0.0F, 1.0F }) });
                    ChatActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy()
                    {
                      public void onAnimationCancel(Animator paramAnonymous2Animator)
                      {
                        if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymous2Animator))) {
                          ChatActivity.access$8002(ChatActivity.this, null);
                        }
                      }
                      
                      public void onAnimationEnd(Animator paramAnonymous2Animator)
                      {
                        if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymous2Animator))) {
                          ChatActivity.access$8002(ChatActivity.this, null);
                        }
                      }
                    });
                    ChatActivity.this.mentionListAnimation.setDuration(200L);
                    ChatActivity.this.mentionListAnimation.start();
                    return;
                    ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                  }
                  ChatActivity.this.mentionContainer.setAlpha(1.0F);
                  ChatActivity.this.mentionContainer.setVisibility(4);
                  return;
                  if (ChatActivity.this.mentionListAnimation != null)
                  {
                    ChatActivity.this.mentionListAnimation.cancel();
                    ChatActivity.access$8002(ChatActivity.this, null);
                  }
                } while (ChatActivity.this.mentionContainer.getVisibility() == 8);
                if (ChatActivity.this.allowStickersPanel)
                {
                  ChatActivity.access$8002(ChatActivity.this, new AnimatorSet());
                  ChatActivity.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", new float[] { 0.0F }) });
                  ChatActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy()
                  {
                    public void onAnimationCancel(Animator paramAnonymous2Animator)
                    {
                      if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymous2Animator))) {
                        ChatActivity.access$8002(ChatActivity.this, null);
                      }
                    }
                    
                    public void onAnimationEnd(Animator paramAnonymous2Animator)
                    {
                      if ((ChatActivity.this.mentionListAnimation != null) && (ChatActivity.this.mentionListAnimation.equals(paramAnonymous2Animator)))
                      {
                        ChatActivity.this.mentionContainer.setVisibility(8);
                        ChatActivity.this.mentionContainer.setTag(null);
                        ChatActivity.access$8002(ChatActivity.this, null);
                      }
                    }
                  });
                  ChatActivity.this.mentionListAnimation.setDuration(200L);
                  ChatActivity.this.mentionListAnimation.start();
                  return;
                }
                ChatActivity.this.mentionContainer.setTag(null);
                ChatActivity.this.mentionContainer.setVisibility(8);
              }
              
              public void onContextClick(TLRPC.BotInlineResult paramAnonymousBotInlineResult)
              {
                if ((ChatActivity.this.getParentActivity() == null) || (paramAnonymousBotInlineResult.content_url == null)) {
                  return;
                }
                if ((paramAnonymousBotInlineResult.type.equals("video")) || (paramAnonymousBotInlineResult.type.equals("web_player_video")))
                {
                  BottomSheet.Builder localBuilder = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                  Activity localActivity = ChatActivity.this.getParentActivity();
                  BottomSheet localBottomSheet = localBuilder.create();
                  if (paramAnonymousBotInlineResult.title != null) {}
                  for (String str = paramAnonymousBotInlineResult.title;; str = "")
                  {
                    localBuilder.setCustomView(new WebFrameLayout(localActivity, localBottomSheet, str, paramAnonymousBotInlineResult.description, paramAnonymousBotInlineResult.content_url, paramAnonymousBotInlineResult.content_url, paramAnonymousBotInlineResult.w, paramAnonymousBotInlineResult.h));
                    localBuilder.setUseFullWidth(true);
                    ChatActivity.this.showDialog(localBuilder.create());
                    return;
                  }
                }
                Browser.openUrl(ChatActivity.this.getParentActivity(), paramAnonymousBotInlineResult.content_url);
              }
              
              public void onContextSearch(boolean paramAnonymousBoolean)
              {
                if (ChatActivity.this.chatActivityEnterView != null)
                {
                  ChatActivity.this.chatActivityEnterView.setCaption(ChatActivity.this.mentionsAdapter.getBotCaption());
                  ChatActivity.this.chatActivityEnterView.showContextProgress(paramAnonymousBoolean);
                }
              }
            });
            this.mentionsAdapter = ((MentionsAdapter)localObject3);
            ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject3);
            if ((!ChatObject.isChannel(this.currentChat)) || ((this.currentChat != null) && (this.currentChat.megagroup))) {
              this.mentionsAdapter.setBotInfo(this.botInfo);
            }
            this.mentionsAdapter.setParentFragment(this);
            this.mentionsAdapter.setChatInfo(this.info);
            localObject1 = this.mentionsAdapter;
            if (this.currentChat == null) {
              break label5686;
            }
            bool1 = true;
            ((MentionsAdapter)localObject1).setNeedUsernames(bool1);
            localObject1 = this.mentionsAdapter;
            if ((this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46)) {
              break label5691;
            }
            bool1 = true;
            ((MentionsAdapter)localObject1).setNeedBotContext(bool1);
            localObject1 = this.mentionsAdapter;
            if (this.currentChat == null) {
              break label5696;
            }
            i = this.botsCount;
            ((MentionsAdapter)localObject1).setBotsCount(i);
            localObject1 = this.mentionListView;
            localObject3 = new RecyclerListView.OnItemClickListener()
            {
              public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
              {
                paramAnonymousView = ChatActivity.this.mentionsAdapter.getItem(paramAnonymousInt);
                int i = ChatActivity.this.mentionsAdapter.getResultStartPosition();
                int j = ChatActivity.this.mentionsAdapter.getResultLength();
                TLRPC.User localUser;
                if ((paramAnonymousView instanceof TLRPC.User))
                {
                  localUser = (TLRPC.User)paramAnonymousView;
                  if (localUser != null)
                  {
                    if (localUser.username == null) {
                      break label103;
                    }
                    ChatActivity.this.chatActivityEnterView.replaceWithText(i, j, "@" + localUser.username + " ");
                  }
                }
                label103:
                do
                {
                  do
                  {
                    return;
                    String str = localUser.first_name;
                    if (str != null)
                    {
                      paramAnonymousView = str;
                      if (str.length() != 0) {}
                    }
                    else
                    {
                      paramAnonymousView = localUser.last_name;
                    }
                    paramAnonymousView = new SpannableString(paramAnonymousView + " ");
                    paramAnonymousView.setSpan(new URLSpanUserMention("" + localUser.id), 0, paramAnonymousView.length(), 33);
                    ChatActivity.this.chatActivityEnterView.replaceWithText(i, j, paramAnonymousView);
                    return;
                    if ((paramAnonymousView instanceof String))
                    {
                      if (ChatActivity.this.mentionsAdapter.isBotCommands())
                      {
                        SendMessagesHelper.getInstance().sendMessage((String)paramAnonymousView, ChatActivity.this.dialog_id, null, null, false, null, null, null);
                        ChatActivity.this.chatActivityEnterView.setFieldText("");
                        return;
                      }
                      ChatActivity.this.chatActivityEnterView.replaceWithText(i, j, paramAnonymousView + " ");
                      return;
                    }
                    if (!(paramAnonymousView instanceof TLRPC.BotInlineResult)) {
                      break;
                    }
                  } while (ChatActivity.this.chatActivityEnterView.getFieldText() == null);
                  paramAnonymousView = (TLRPC.BotInlineResult)paramAnonymousView;
                  if ((Build.VERSION.SDK_INT >= 16) && (((paramAnonymousView.type.equals("photo")) && ((paramAnonymousView.photo != null) || (paramAnonymousView.content_url != null))) || ((paramAnonymousView.type.equals("gif")) && ((paramAnonymousView.document != null) || (paramAnonymousView.content_url != null))) || ((paramAnonymousView.type.equals("video")) && (paramAnonymousView.document != null))))
                  {
                    paramAnonymousView = ChatActivity.access$002(ChatActivity.this, new ArrayList(ChatActivity.this.mentionsAdapter.getSearchResultBotContext()));
                    PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhotoForSelect(paramAnonymousView, ChatActivity.this.mentionsAdapter.getItemPosition(paramAnonymousInt), 3, ChatActivity.this.botContextProvider, null);
                    return;
                  }
                  ChatActivity.this.sendBotInlineResult(paramAnonymousView);
                  return;
                } while (!(paramAnonymousView instanceof TLRPC.TL_inlineBotSwitchPM));
                ChatActivity.this.processInlineBotContextPM((TLRPC.TL_inlineBotSwitchPM)paramAnonymousView);
              }
            };
            this.mentionsOnItemClickListener = ((RecyclerListView.OnItemClickListener)localObject3);
            ((RecyclerListView)localObject1).setOnItemClickListener((RecyclerListView.OnItemClickListener)localObject3);
            this.mentionListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
            {
              public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
              {
                boolean bool2 = false;
                if ((ChatActivity.this.getParentActivity() == null) || (!ChatActivity.this.mentionsAdapter.isLongClickEnabled())) {}
                do
                {
                  do
                  {
                    return false;
                    localObject = ChatActivity.this.mentionsAdapter.getItem(paramAnonymousInt);
                  } while (!(localObject instanceof String));
                  if (!ChatActivity.this.mentionsAdapter.isBotCommands()) {
                    break;
                  }
                } while (!URLSpanBotCommand.enabled);
                ChatActivity.this.chatActivityEnterView.setFieldText("");
                paramAnonymousView = ChatActivity.this.chatActivityEnterView;
                Object localObject = (String)localObject;
                boolean bool1 = bool2;
                if (ChatActivity.this.currentChat != null)
                {
                  bool1 = bool2;
                  if (ChatActivity.this.currentChat.megagroup) {
                    bool1 = true;
                  }
                }
                paramAnonymousView.setCommand(null, (String)localObject, true, bool1);
                return true;
                paramAnonymousView = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
                paramAnonymousView.setMessage(LocaleController.getString("ClearSearch", 2131165514));
                paramAnonymousView.setPositiveButton(LocaleController.getString("ClearButton", 2131165508).toUpperCase(), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    ChatActivity.this.mentionsAdapter.clearRecentHashtags();
                  }
                });
                paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                ChatActivity.this.showDialog(paramAnonymousView.create());
                return true;
              }
            });
            this.mentionListView.setOnScrollListener(new RecyclerView.OnScrollListener()
            {
              public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
              {
                boolean bool = true;
                paramAnonymousRecyclerView = ChatActivity.this;
                if (paramAnonymousInt == 1) {}
                for (;;)
                {
                  ChatActivity.access$7402(paramAnonymousRecyclerView, bool);
                  return;
                  bool = false;
                }
              }
              
              public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
              {
                if ((ChatActivity.this.mentionsAdapter.isBotContext()) && (ChatActivity.this.mentionsAdapter.isMediaLayout()))
                {
                  paramAnonymousInt1 = ChatActivity.this.mentionGridLayoutManager.findLastVisibleItemPosition();
                  if (paramAnonymousInt1 != -1) {
                    break label96;
                  }
                }
                label96:
                for (paramAnonymousInt2 = 0;; paramAnonymousInt2 = paramAnonymousInt1)
                {
                  if ((paramAnonymousInt2 > 0) && (paramAnonymousInt1 > ChatActivity.this.mentionsAdapter.getItemCount() - 5)) {
                    ChatActivity.this.mentionsAdapter.searchForContextBotForNextOffset();
                  }
                  ChatActivity.this.mentionListViewUpdateLayout();
                  return;
                  paramAnonymousInt1 = ChatActivity.this.mentionLayoutManager.findLastVisibleItemPosition();
                  break;
                }
              }
            });
          }
          this.pagedownButton = new FrameLayout(paramContext);
          this.pagedownButton.setVisibility(4);
          ((SizeNotifierFrameLayout)localObject2).addView(this.pagedownButton, LayoutHelper.createFrame(46, 59.0F, 85, 0.0F, 0.0F, 7.0F, 5.0F));
          this.pagedownButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChatActivity.this.returnToMessageId > 0)
              {
                ChatActivity.this.scrollToMessageId(ChatActivity.this.returnToMessageId, 0, true, ChatActivity.this.returnToLoadIndex);
                return;
              }
              ChatActivity.this.scrollToLastMessage(true);
            }
          });
          localObject1 = new ImageView(paramContext);
          ((ImageView)localObject1).setImageResource(2130837863);
          this.pagedownButton.addView((View)localObject1, LayoutHelper.createFrame(46, 46, 83));
          this.pagedownButtonCounter = new TextView(paramContext);
          this.pagedownButtonCounter.setVisibility(4);
          this.pagedownButtonCounter.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.pagedownButtonCounter.setTextSize(1, 13.0F);
          this.pagedownButtonCounter.setTextColor(-1);
          this.pagedownButtonCounter.setGravity(17);
          this.pagedownButtonCounter.setBackgroundResource(2130837590);
          this.pagedownButtonCounter.setMinWidth(AndroidUtilities.dp(23.0F));
          this.pagedownButtonCounter.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
          this.pagedownButton.addView(this.pagedownButtonCounter, LayoutHelper.createFrame(-2, 23, 49));
          this.chatActivityEnterView = new ChatActivityEnterView(getParentActivity(), (SizeNotifierFrameLayout)localObject2, this, true);
          this.chatActivityEnterView.setDialogId(this.dialog_id);
          this.chatActivityEnterView.addToAttachLayout(this.menuItem);
          this.chatActivityEnterView.setId(1000);
          this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
          localObject1 = this.chatActivityEnterView;
          if ((this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 23)) {
            break label5701;
          }
          bool1 = true;
          if ((this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46)) {
            break label5706;
          }
          bool2 = true;
          label3847:
          ((ChatActivityEnterView)localObject1).setAllowStickersAndGifs(bool1, bool2);
          ((SizeNotifierFrameLayout)localObject2).addView(this.chatActivityEnterView, ((SizeNotifierFrameLayout)localObject2).getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
          this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate()
          {
            public void needSendTyping()
            {
              MessagesController.getInstance().sendTyping(ChatActivity.this.dialog_id, 0, ChatActivity.this.classGuid);
            }
            
            public void onAttachButtonHidden()
            {
              if (ChatActivity.this.actionBar.isSearchFieldVisible()) {}
              do
              {
                return;
                if (ChatActivity.this.attachItem != null) {
                  ChatActivity.this.attachItem.setVisibility(0);
                }
              } while (ChatActivity.this.headerItem == null);
              ChatActivity.this.headerItem.setVisibility(8);
            }
            
            public void onAttachButtonShow()
            {
              if (ChatActivity.this.actionBar.isSearchFieldVisible()) {}
              do
              {
                return;
                if (ChatActivity.this.attachItem != null) {
                  ChatActivity.this.attachItem.setVisibility(8);
                }
              } while (ChatActivity.this.headerItem == null);
              ChatActivity.this.headerItem.setVisibility(0);
            }
            
            public void onMessageEditEnd(boolean paramAnonymousBoolean)
            {
              if (paramAnonymousBoolean)
              {
                ChatActivity.this.showEditDoneProgress(true, true);
                return;
              }
              Object localObject = ChatActivity.this.mentionsAdapter;
              if ((ChatActivity.this.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46))
              {
                paramAnonymousBoolean = true;
                ((MentionsAdapter)localObject).setNeedBotContext(paramAnonymousBoolean);
                ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                ChatActivity.this.chatListView.setClickable(true);
                ChatActivity.this.chatListView.setLongClickable(true);
                ChatActivity.this.mentionsAdapter.setAllowNewMentions(true);
                ChatActivity.this.actionModeTitleContainer.setVisibility(8);
                ChatActivity.this.selectedMessagesCountTextView.setVisibility(0);
                localObject = ChatActivity.this.chatActivityEnterView;
                if ((ChatActivity.this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) < 23)) {
                  break label285;
                }
                paramAnonymousBoolean = true;
                label185:
                if ((ChatActivity.this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) < 46)) {
                  break label290;
                }
              }
              label285:
              label290:
              for (boolean bool = true;; bool = false)
              {
                ((ChatActivityEnterView)localObject).setAllowStickersAndGifs(paramAnonymousBoolean, bool);
                if (ChatActivity.this.editingMessageObjectReqId != 0)
                {
                  ConnectionsManager.getInstance().cancelRequest(ChatActivity.this.editingMessageObjectReqId, true);
                  ChatActivity.access$9702(ChatActivity.this, 0);
                }
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
                return;
                paramAnonymousBoolean = false;
                break;
                paramAnonymousBoolean = false;
                break label185;
              }
            }
            
            public void onMessageSend(CharSequence paramAnonymousCharSequence)
            {
              ChatActivity.this.moveScrollToLastMessage();
              ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
              if (ChatActivity.this.mentionsAdapter != null) {
                ChatActivity.this.mentionsAdapter.addHashtagsFromMessage(paramAnonymousCharSequence);
              }
            }
            
            public void onStickersTab(boolean paramAnonymousBoolean)
            {
              if (ChatActivity.this.emojiButtonRed != null) {
                ChatActivity.this.emojiButtonRed.setVisibility(8);
              }
              ChatActivity localChatActivity = ChatActivity.this;
              if (!paramAnonymousBoolean) {}
              for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
              {
                ChatActivity.access$8302(localChatActivity, paramAnonymousBoolean);
                ChatActivity.this.checkContextBotPanel();
                return;
              }
            }
            
            public void onTextChanged(final CharSequence paramAnonymousCharSequence, boolean paramAnonymousBoolean)
            {
              MediaController localMediaController = MediaController.getInstance();
              if (((paramAnonymousCharSequence != null) && (paramAnonymousCharSequence.length() != 0)) || (ChatActivity.this.chatActivityEnterView.isEditingMessage())) {}
              for (boolean bool = true;; bool = false)
              {
                localMediaController.setInputFieldHasText(bool);
                if ((ChatActivity.this.stickersAdapter != null) && (!ChatActivity.this.chatActivityEnterView.isEditingMessage())) {
                  ChatActivity.this.stickersAdapter.loadStikersForEmoji(paramAnonymousCharSequence);
                }
                if (ChatActivity.this.mentionsAdapter != null) {
                  ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag(paramAnonymousCharSequence.toString(), ChatActivity.this.chatActivityEnterView.getCursorPosition(), ChatActivity.this.messages);
                }
                if (ChatActivity.this.waitingForCharaterEnterRunnable != null)
                {
                  AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable);
                  ChatActivity.access$8902(ChatActivity.this, null);
                }
                if ((ChatActivity.this.chatActivityEnterView.isMessageWebPageSearchEnabled()) && ((!ChatActivity.this.chatActivityEnterView.isEditingMessage()) || (!ChatActivity.this.chatActivityEnterView.isEditingCaption())))
                {
                  if (!paramAnonymousBoolean) {
                    break;
                  }
                  ChatActivity.this.searchLinks(paramAnonymousCharSequence, true);
                }
                return;
              }
              ChatActivity.access$8902(ChatActivity.this, new Runnable()
              {
                public void run()
                {
                  if (this == ChatActivity.this.waitingForCharaterEnterRunnable)
                  {
                    ChatActivity.this.searchLinks(paramAnonymousCharSequence, false);
                    ChatActivity.access$8902(ChatActivity.this, null);
                  }
                }
              });
              paramAnonymousCharSequence = ChatActivity.this.waitingForCharaterEnterRunnable;
              if (AndroidUtilities.WEB_URL == null) {}
              for (long l = 3000L;; l = 1000L)
              {
                AndroidUtilities.runOnUIThread(paramAnonymousCharSequence, l);
                return;
              }
            }
            
            public void onWindowSizeChanged(int paramAnonymousInt)
            {
              boolean bool = true;
              ChatActivity localChatActivity;
              if (paramAnonymousInt < AndroidUtilities.dp(72.0F) + ActionBar.getCurrentActionBarHeight())
              {
                ChatActivity.access$8102(ChatActivity.this, false);
                if (ChatActivity.this.stickersPanel.getVisibility() == 0) {
                  ChatActivity.this.stickersPanel.setVisibility(4);
                }
                if ((ChatActivity.this.mentionContainer != null) && (ChatActivity.this.mentionContainer.getVisibility() == 0)) {
                  ChatActivity.this.mentionContainer.setVisibility(4);
                }
                localChatActivity = ChatActivity.this;
                if (ChatActivity.this.chatActivityEnterView.isPopupShowing()) {
                  break label231;
                }
              }
              for (;;)
              {
                ChatActivity.access$8202(localChatActivity, bool);
                ChatActivity.this.checkContextBotPanel();
                return;
                ChatActivity.access$8102(ChatActivity.this, true);
                if (ChatActivity.this.stickersPanel.getVisibility() == 4) {
                  ChatActivity.this.stickersPanel.setVisibility(0);
                }
                if ((ChatActivity.this.mentionContainer == null) || (ChatActivity.this.mentionContainer.getVisibility() != 4) || ((ChatActivity.this.mentionsAdapter.isBotContext()) && (!ChatActivity.this.allowContextBotPanel) && (!ChatActivity.this.allowContextBotPanelSecond))) {
                  break;
                }
                ChatActivity.this.mentionContainer.setVisibility(0);
                ChatActivity.this.mentionContainer.setTag(null);
                break;
                label231:
                bool = false;
              }
            }
          });
          localObject1 = new FrameLayout(paramContext)
          {
            public boolean hasOverlappingRendering()
            {
              return false;
            }
            
            public void setTranslationY(float paramAnonymousFloat)
            {
              super.setTranslationY(paramAnonymousFloat);
              if (ChatActivity.this.chatActivityEnterView != null) {
                ChatActivity.this.chatActivityEnterView.invalidate();
              }
              if (getVisibility() != 8)
              {
                int i = getLayoutParams().height;
                if (ChatActivity.this.chatListView != null) {
                  ChatActivity.this.chatListView.setTranslationY(paramAnonymousFloat);
                }
                if (ChatActivity.this.progressView != null) {
                  ChatActivity.this.progressView.setTranslationY(paramAnonymousFloat);
                }
                if (ChatActivity.this.mentionContainer != null) {
                  ChatActivity.this.mentionContainer.setTranslationY(paramAnonymousFloat);
                }
                if (ChatActivity.this.pagedownButton != null) {
                  ChatActivity.this.pagedownButton.setTranslationY(paramAnonymousFloat);
                }
              }
            }
            
            public void setVisibility(int paramAnonymousInt)
            {
              float f = 0.0F;
              super.setVisibility(paramAnonymousInt);
              if (paramAnonymousInt == 8)
              {
                if (ChatActivity.this.chatListView != null) {
                  ChatActivity.this.chatListView.setTranslationY(0.0F);
                }
                if (ChatActivity.this.progressView != null) {
                  ChatActivity.this.progressView.setTranslationY(0.0F);
                }
                if (ChatActivity.this.mentionContainer != null) {
                  ChatActivity.this.mentionContainer.setTranslationY(0.0F);
                }
                if (ChatActivity.this.pagedownButton != null)
                {
                  FrameLayout localFrameLayout = ChatActivity.this.pagedownButton;
                  if (ChatActivity.this.pagedownButton.getTag() == null) {
                    f = AndroidUtilities.dp(100.0F);
                  }
                  localFrameLayout.setTranslationY(f);
                }
              }
            }
          };
          ((FrameLayout)localObject1).setClickable(true);
          this.chatActivityEnterView.addTopView((View)localObject1, 48);
          localObject3 = new View(paramContext);
          ((View)localObject3).setBackgroundColor(-1513240);
          ((FrameLayout)localObject1).addView((View)localObject3, LayoutHelper.createFrame(-1, 1, 83));
          this.replyIconImageView = new ImageView(paramContext);
          this.replyIconImageView.setScaleType(ImageView.ScaleType.CENTER);
          ((FrameLayout)localObject1).addView(this.replyIconImageView, LayoutHelper.createFrame(52, 46, 51));
          localObject3 = new ImageView(paramContext);
          ((ImageView)localObject3).setImageResource(2130837637);
          ((ImageView)localObject3).setScaleType(ImageView.ScaleType.CENTER);
          ((FrameLayout)localObject1).addView((View)localObject3, LayoutHelper.createFrame(52, 46.0F, 53, 0.0F, 0.5F, 0.0F, 0.0F));
          ((ImageView)localObject3).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChatActivity.this.forwardingMessages != null) {
                ChatActivity.this.forwardingMessages.clear();
              }
              ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, true, true);
            }
          });
          this.replyNameTextView = new SimpleTextView(paramContext);
          this.replyNameTextView.setTextSize(14);
          this.replyNameTextView.setTextColor(-12940081);
          this.replyNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          ((FrameLayout)localObject1).addView(this.replyNameTextView, LayoutHelper.createFrame(-1, 18.0F, 51, 52.0F, 6.0F, 52.0F, 0.0F));
          this.replyObjectTextView = new SimpleTextView(paramContext);
          this.replyObjectTextView.setTextSize(14);
          this.replyObjectTextView.setTextColor(-14540254);
          ((FrameLayout)localObject1).addView(this.replyObjectTextView, LayoutHelper.createFrame(-1, 18.0F, 51, 52.0F, 24.0F, 52.0F, 0.0F));
          this.replyImageView = new BackupImageView(paramContext);
          ((FrameLayout)localObject1).addView(this.replyImageView, LayoutHelper.createFrame(34, 34.0F, 51, 52.0F, 6.0F, 0.0F, 0.0F));
          this.stickersPanel = new FrameLayout(paramContext);
          this.stickersPanel.setVisibility(8);
          ((SizeNotifierFrameLayout)localObject2).addView(this.stickersPanel, LayoutHelper.createFrame(-2, 81.5F, 83, 0.0F, 0.0F, 0.0F, 38.0F));
          this.stickersListView = new RecyclerListView(paramContext)
          {
            public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
            {
              boolean bool1 = false;
              boolean bool2 = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramAnonymousMotionEvent, ChatActivity.this.stickersListView, 0);
              if ((super.onInterceptTouchEvent(paramAnonymousMotionEvent)) || (bool2)) {
                bool1 = true;
              }
              return bool1;
            }
          };
          this.stickersListView.setTag(Integer.valueOf(3));
          this.stickersListView.setOnTouchListener(new View.OnTouchListener()
          {
            public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
            {
              return StickerPreviewViewer.getInstance().onTouch(paramAnonymousMotionEvent, ChatActivity.this.stickersListView, 0, ChatActivity.this.stickersOnItemClickListener);
            }
          });
          this.stickersListView.setDisallowInterceptTouchEvents(true);
          localObject1 = new LinearLayoutManager(paramContext);
          ((LinearLayoutManager)localObject1).setOrientation(0);
          this.stickersListView.setLayoutManager((RecyclerView.LayoutManager)localObject1);
          this.stickersListView.setClipToPadding(false);
          this.stickersListView.setOverScrollMode(2);
          this.stickersPanel.addView(this.stickersListView, LayoutHelper.createFrame(-1, 78.0F));
          initStickers();
          localObject1 = new ImageView(paramContext);
          ((ImageView)localObject1).setImageResource(2130837992);
          this.stickersPanel.addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 83, 53.0F, 0.0F, 0.0F, 0.0F));
          this.searchContainer = new FrameLayout(paramContext);
          this.searchContainer.setBackgroundResource(2130837623);
          this.searchContainer.setVisibility(4);
          this.searchContainer.setFocusable(true);
          this.searchContainer.setFocusableInTouchMode(true);
          this.searchContainer.setClickable(true);
          this.searchContainer.setBackgroundResource(2130837623);
          this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
          ((SizeNotifierFrameLayout)localObject2).addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
          this.searchUpButton = new ImageView(paramContext);
          this.searchUpButton.setScaleType(ImageView.ScaleType.CENTER);
          this.searchUpButton.setImageResource(2130837976);
          this.searchContainer.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0F));
          this.searchUpButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, 1);
            }
          });
          this.searchDownButton = new ImageView(paramContext);
          this.searchDownButton.setScaleType(ImageView.ScaleType.CENTER);
          this.searchDownButton.setImageResource(2130837971);
          this.searchContainer.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0F, 51, 48.0F, 0.0F, 0.0F, 0.0F));
          this.searchDownButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, 2);
            }
          });
          this.searchCountText = new SimpleTextView(paramContext);
          this.searchCountText.setTextColor(-11625772);
          this.searchCountText.setTextSize(15);
          this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0F, 19, 108.0F, 0.0F, 0.0F, 0.0F));
          this.bottomOverlay = new FrameLayout(paramContext);
          this.bottomOverlay.setVisibility(4);
          this.bottomOverlay.setFocusable(true);
          this.bottomOverlay.setFocusableInTouchMode(true);
          this.bottomOverlay.setClickable(true);
          this.bottomOverlay.setBackgroundResource(2130837623);
          this.bottomOverlay.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
          ((SizeNotifierFrameLayout)localObject2).addView(this.bottomOverlay, LayoutHelper.createFrame(-1, 51, 80));
          this.bottomOverlayText = new TextView(paramContext);
          this.bottomOverlayText.setTextSize(1, 16.0F);
          this.bottomOverlayText.setTextColor(-8421505);
          this.bottomOverlay.addView(this.bottomOverlayText, LayoutHelper.createFrame(-2, -2, 17));
          this.bottomOverlayChat = new FrameLayout(paramContext);
          this.bottomOverlayChat.setBackgroundResource(2130837623);
          this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
          this.bottomOverlayChat.setVisibility(4);
          ((SizeNotifierFrameLayout)localObject2).addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
          this.bottomOverlayChat.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChatActivity.this.getParentActivity() == null) {}
              for (;;)
              {
                return;
                paramAnonymousView = null;
                if ((ChatActivity.this.currentUser != null) && (ChatActivity.this.userBlocked)) {
                  if (ChatActivity.this.currentUser.bot)
                  {
                    String str = ChatActivity.this.botUser;
                    ChatActivity.access$10902(ChatActivity.this, null);
                    MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                    if ((str != null) && (str.length() != 0)) {
                      MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, str);
                    }
                  }
                }
                while (paramAnonymousView != null)
                {
                  paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
                  paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                  ChatActivity.this.showDialog(paramAnonymousView.create());
                  return;
                  SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                  continue;
                  paramAnonymousView = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                  paramAnonymousView.setMessage(LocaleController.getString("AreYouSureUnblockContact", 2131165331));
                  paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                      MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                    }
                  });
                  continue;
                  if ((ChatActivity.this.currentUser != null) && (ChatActivity.this.currentUser.bot) && (ChatActivity.this.botUser != null))
                  {
                    if (ChatActivity.this.botUser.length() != 0) {
                      MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, ChatActivity.this.botUser);
                    }
                    for (;;)
                    {
                      ChatActivity.access$10902(ChatActivity.this, null);
                      ChatActivity.this.updateBottomOverlay();
                      break;
                      SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                    }
                  }
                  if ((ChatObject.isChannel(ChatActivity.this.currentChat)) && (!(ChatActivity.this.currentChat instanceof TLRPC.TL_channelForbidden)))
                  {
                    if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                      MessagesController.getInstance().addUserToChat(ChatActivity.this.currentChat.id, UserConfig.getCurrentUser(), null, 0, null, null);
                    } else {
                      ChatActivity.this.toggleMute(true);
                    }
                  }
                  else
                  {
                    paramAnonymousView = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                    paramAnonymousView.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", 2131165322));
                    paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                      {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                        ChatActivity.this.finishFragment();
                      }
                    });
                  }
                }
              }
            }
          });
          this.bottomOverlayChatText = new TextView(paramContext);
          this.bottomOverlayChatText.setTextSize(1, 15.0F);
          this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.bottomOverlayChatText.setTextColor(-12940081);
          this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
          this.chatAdapter.updateRows();
          if ((!this.loading) || (!this.messages.isEmpty())) {
            break label5717;
          }
          localObject1 = this.progressView;
          if (this.chatAdapter.botInfoRow != -1) {
            break label5712;
          }
          i = 0;
          label5059:
          ((FrameLayout)localObject1).setVisibility(i);
          this.chatListView.setEmptyView(null);
          label5073:
          localObject3 = this.chatActivityEnterView;
          if (!this.userBlocked) {
            break label5739;
          }
          localObject1 = null;
          ((ChatActivityEnterView)localObject3).setButtons((MessageObject)localObject1);
          if ((!AndroidUtilities.isTablet()) || (AndroidUtilities.isSmallTablet()))
          {
            paramContext = new PlayerView(paramContext, this);
            this.playerView = paramContext;
            ((SizeNotifierFrameLayout)localObject2).addView(paramContext, LayoutHelper.createFrame(-1, 39.0F, 51, 0.0F, -36.0F, 0.0F, 0.0F));
          }
          updateContactStatus();
          updateBottomOverlay();
          updateSecretStatus();
          updateSpamView();
          updatePinnedMessageView(true);
        }
        try
        {
          if ((this.currentEncryptedChat != null) && (Build.VERSION.SDK_INT >= 23)) {
            getParentActivity().getWindow().setFlags(8192, 8192);
          }
          fixLayoutInternal();
          ((SizeNotifierFrameLayout)localObject2).addView(this.actionBar);
          return this.fragmentView;
          bool1 = false;
          continue;
          this.headerItem.addSubItem(16, LocaleController.getString("DeleteChatUser", 2131165570), 0);
          break label618;
          label5243:
          this.actionModeViews.add(((ActionBarMenu)localObject1).addItem(19, 2130837709, -986896, null, AndroidUtilities.dp(54.0F)));
          this.actionModeViews.add(((ActionBarMenu)localObject1).addItem(10, 2130837704, -986896, null, AndroidUtilities.dp(54.0F)));
          this.actionModeViews.add(((ActionBarMenu)localObject1).addItem(12, 2130837705, -986896, null, AndroidUtilities.dp(54.0F)));
          break label1295;
          label5330:
          i = 8;
          break label1328;
          label5336:
          i = 8;
          break label1352;
          label5342:
          localObject1 = new TextView(paramContext);
          if ((this.currentUser != null) && (this.currentUser.id != 777000) && (this.currentUser.id != 429000) && ((this.currentUser.id / 1000 == 333) || (this.currentUser.id % 1000 == 0))) {
            ((TextView)localObject1).setText(LocaleController.getString("GotAQuestion", 2131165716));
          }
          for (;;)
          {
            ((TextView)localObject1).setTextSize(1, 14.0F);
            ((TextView)localObject1).setGravity(17);
            ((TextView)localObject1).setTextColor(-1);
            ((TextView)localObject1).setBackgroundResource(2130838000);
            ((TextView)localObject1).getBackground().setColorFilter(Theme.colorFilter);
            ((TextView)localObject1).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            ((TextView)localObject1).setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(2.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(3.0F));
            this.emptyViewContainer.addView((View)localObject1, new FrameLayout.LayoutParams(-2, -2, 17));
            break;
            ((TextView)localObject1).setText(LocaleController.getString("NoMessages", 2131165940));
          }
          label5551:
          this.bigEmptyView = new ChatBigEmptyView(paramContext, true);
          if (this.currentEncryptedChat.admin_id == UserConfig.getClientUserId()) {
            this.bigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleOutgoing", 2131165612, new Object[] { UserObject.getFirstName(this.currentUser) }));
          }
          for (;;)
          {
            this.emptyViewContainer.addView(this.bigEmptyView, new FrameLayout.LayoutParams(-2, -2, 17));
            break;
            this.bigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleIncoming", 2131165611, new Object[] { UserObject.getFirstName(this.currentUser) }));
          }
          this.reportSpamButton.setText(LocaleController.getString("ReportSpam", 2131166165));
          break label2644;
          label5686:
          bool1 = false;
          break label3375;
          label5691:
          bool1 = false;
          break label3411;
          label5696:
          i = 1;
          break label3435;
          label5701:
          bool1 = false;
          break label3822;
          label5706:
          bool2 = false;
          break label3847;
          label5712:
          i = 4;
          break label5059;
          label5717:
          this.progressView.setVisibility(4);
          this.chatListView.setEmptyView(this.emptyViewContainer);
          break label5073;
          label5739:
          localObject1 = this.botButtons;
        }
        catch (Throwable paramContext)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramContext);
          }
        }
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
  }
  
  public void didReceivedNotification(int paramInt, final Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.messagesDidLoaded) {
      if (((Integer)paramVarArgs[10]).intValue() == this.classGuid)
      {
        if (!this.openAnimationEnded) {
          NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.botKeyboardDidLoaded });
        }
        paramInt = ((Integer)paramVarArgs[11]).intValue();
        paramInt = this.waitingForLoad.indexOf(Integer.valueOf(paramInt));
        if (paramInt != -1) {}
      }
      else
      {
        return;
        break label11725;
      }
    }
    label92:
    final Object localObject2;
    int j;
    int n;
    int i1;
    label323:
    label347:
    label462:
    label530:
    label559:
    label565:
    label586:
    label602:
    int m;
    int k;
    Object localObject3;
    label867:
    label986:
    final Object localObject1;
    label1388:
    label1442:
    label1455:
    label1767:
    label1939:
    label2041:
    label2125:
    label2199:
    label2222:
    label2325:
    label2365:
    label2371:
    label2497:
    label2532:
    label2548:
    label2573:
    label2580:
    label2587:
    label2635:
    label2640:
    label2649:
    label2655:
    label2661:
    label2667:
    label2678:
    label2762:
    label3117:
    label3675:
    label4446:
    label4864:
    label5003:
    label5028:
    label5040:
    label6166:
    label6324:
    label6336:
    label6394:
    label6402:
    long l;
    label6651:
    label6711:
    label6792:
    label6809:
    label7305:
    label7389:
    label7432:
    label7438:
    label7509:
    do
    {
      do
      {
        do
        {
          for (;;)
          {
            this.waitingForLoad.remove(paramInt);
            localObject2 = (ArrayList)paramVarArgs[2];
            if (this.waitingForReplyMessageLoad)
            {
              j = 0;
              paramInt = 0;
              for (;;)
              {
                i = j;
                if (paramInt < ((ArrayList)localObject2).size())
                {
                  if (((MessageObject)((ArrayList)localObject2).get(paramInt)).getId() == this.startLoadFromMessageId) {
                    i = 1;
                  }
                }
                else
                {
                  if (i != 0) {
                    break;
                  }
                  this.startLoadFromMessageId = 0;
                  return;
                }
                paramInt += 1;
              }
              paramInt = this.startLoadFromMessageId;
              bool1 = this.needSelectFromMessageId;
              clearChatData();
              this.startLoadFromMessageId = paramInt;
              this.needSelectFromMessageId = bool1;
            }
            this.loadsCount += 1;
            boolean bool4;
            final int i2;
            if (((Long)paramVarArgs[0]).longValue() == this.dialog_id)
            {
              j = 0;
              n = ((Integer)paramVarArgs[1]).intValue();
              bool4 = ((Boolean)paramVarArgs[3]).booleanValue();
              paramInt = ((Integer)paramVarArgs[4]).intValue();
              i2 = ((Integer)paramVarArgs[7]).intValue();
              i1 = ((Integer)paramVarArgs[8]).intValue();
              bool2 = false;
              if (paramInt == 0) {
                break label530;
              }
              this.first_unread_id = paramInt;
              this.last_message_id = ((Integer)paramVarArgs[5]).intValue();
              this.unread_to_load = ((Integer)paramVarArgs[6]).intValue();
              i = 0;
              paramVarArgs = this.forwardEndReached;
              if ((this.startLoadFromMessageId != 0) || (this.last_message_id != 0)) {
                break label559;
              }
              bool1 = true;
              paramVarArgs[j] = bool1;
              if (((i1 == 1) || (i1 == 3)) && (j == 1))
              {
                paramVarArgs = this.endReached;
                this.cacheEndReached[0] = true;
                paramVarArgs[0] = 1;
                this.forwardEndReached[0] = false;
                this.minMessageId[0] = 0;
              }
              if ((this.loadsCount == 1) && (((ArrayList)localObject2).size() > 20)) {
                this.loadsCount += 1;
              }
              if (!this.firstLoading) {
                break label602;
              }
              if (this.forwardEndReached[j] != 0) {
                break label586;
              }
              this.messages.clear();
              this.messagesByDays.clear();
              paramInt = 0;
              if (paramInt >= 2) {
                break label586;
              }
              this.messagesDict[paramInt].clear();
              if (this.currentEncryptedChat != null) {
                break label565;
              }
              this.maxMessageId[paramInt] = Integer.MAX_VALUE;
              this.minMessageId[paramInt] = Integer.MIN_VALUE;
            }
            for (;;)
            {
              this.maxDate[paramInt] = Integer.MIN_VALUE;
              this.minDate[paramInt] = 0;
              paramInt += 1;
              break label462;
              j = 1;
              break;
              if ((this.startLoadFromMessageId == 0) || (i1 != 3)) {
                break label323;
              }
              this.last_message_id = ((Integer)paramVarArgs[5]).intValue();
              break label323;
              bool1 = false;
              break label347;
              this.maxMessageId[paramInt] = Integer.MIN_VALUE;
              this.minMessageId[paramInt] = Integer.MAX_VALUE;
            }
            this.firstLoading = false;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (ChatActivity.this.parentLayout != null) {
                  ChatActivity.this.parentLayout.resumeDelayedFragmentAnimation();
                }
              }
            });
            if (i1 == 1) {
              Collections.reverse((List)localObject2);
            }
            if (this.currentEncryptedChat == null) {
              MessagesQuery.loadReplyMessagesForMessages((ArrayList)localObject2, this.dialog_id);
            }
            m = 0;
            k = 0;
            bool1 = bool2;
            paramInt = i;
            if (k < ((ArrayList)localObject2).size())
            {
              localObject3 = (MessageObject)((ArrayList)localObject2).get(k);
              m += ((MessageObject)localObject3).getApproximateHeight();
              if (this.currentUser != null)
              {
                if (this.currentUser.self) {
                  ((MessageObject)localObject3).messageOwner.out = true;
                }
                if ((this.currentUser.bot) && (((MessageObject)localObject3).isOut())) {
                  ((MessageObject)localObject3).setIsRead();
                }
              }
              boolean bool3;
              if (this.messagesDict[j].containsKey(Integer.valueOf(((MessageObject)localObject3).getId())))
              {
                bool3 = bool1;
                i = paramInt;
              }
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      k += 1;
                      paramInt = i;
                      bool1 = bool3;
                      break;
                      if (j == 1) {
                        ((MessageObject)localObject3).setIsRead();
                      }
                      if ((j == 0) && (ChatObject.isChannel(this.currentChat)) && (((MessageObject)localObject3).getId() == 1))
                      {
                        this.endReached[j] = true;
                        this.cacheEndReached[j] = true;
                      }
                      if (((MessageObject)localObject3).getId() <= 0) {
                        break label1388;
                      }
                      this.maxMessageId[j] = Math.min(((MessageObject)localObject3).getId(), this.maxMessageId[j]);
                      this.minMessageId[j] = Math.max(((MessageObject)localObject3).getId(), this.minMessageId[j]);
                      if (((MessageObject)localObject3).messageOwner.date != 0)
                      {
                        this.maxDate[j] = Math.max(this.maxDate[j], ((MessageObject)localObject3).messageOwner.date);
                        if ((this.minDate[j] == 0) || (((MessageObject)localObject3).messageOwner.date < this.minDate[j])) {
                          this.minDate[j] = ((MessageObject)localObject3).messageOwner.date;
                        }
                      }
                      i = paramInt;
                      bool3 = bool1;
                    } while (((MessageObject)localObject3).type < 0);
                    if (j != 1) {
                      break label986;
                    }
                    i = paramInt;
                    bool3 = bool1;
                  } while ((((MessageObject)localObject3).messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo));
                  bool2 = bool1;
                  if (!((MessageObject)localObject3).isOut())
                  {
                    bool2 = bool1;
                    if (((MessageObject)localObject3).isUnread()) {
                      bool2 = true;
                    }
                  }
                  this.messagesDict[j].put(Integer.valueOf(((MessageObject)localObject3).getId()), localObject3);
                  localObject1 = (ArrayList)this.messagesByDays.get(((MessageObject)localObject3).dateKey);
                  paramVarArgs = (Object[])localObject1;
                  i = paramInt;
                  if (localObject1 == null)
                  {
                    paramVarArgs = new ArrayList();
                    this.messagesByDays.put(((MessageObject)localObject3).dateKey, paramVarArgs);
                    localObject1 = new TLRPC.Message();
                    ((TLRPC.Message)localObject1).message = LocaleController.formatDateChat(((MessageObject)localObject3).messageOwner.date);
                    ((TLRPC.Message)localObject1).id = 0;
                    ((TLRPC.Message)localObject1).date = ((MessageObject)localObject3).messageOwner.date;
                    localObject1 = new MessageObject((TLRPC.Message)localObject1, null, false);
                    ((MessageObject)localObject1).type = 10;
                    ((MessageObject)localObject1).contentType = 1;
                    if (i1 != 1) {
                      break label1442;
                    }
                    this.messages.add(0, localObject1);
                  }
                  for (;;)
                  {
                    i = paramInt + 1;
                    paramInt = i + 1;
                    if (i1 == 1)
                    {
                      paramVarArgs.add(localObject3);
                      this.messages.add(0, localObject3);
                    }
                    if (i1 != 1)
                    {
                      paramVarArgs.add(localObject3);
                      this.messages.add(this.messages.size() - 1, localObject3);
                    }
                    if (((MessageObject)localObject3).getId() == this.last_message_id) {
                      this.forwardEndReached[j] = true;
                    }
                    if ((i1 != 2) || (((MessageObject)localObject3).getId() != this.first_unread_id)) {
                      break label1455;
                    }
                    if (m <= AndroidUtilities.displaySize.y / 2)
                    {
                      i = paramInt;
                      bool3 = bool2;
                      if (this.forwardEndReached[0] != 0) {
                        break;
                      }
                    }
                    paramVarArgs = new TLRPC.Message();
                    paramVarArgs.message = "";
                    paramVarArgs.id = 0;
                    paramVarArgs = new MessageObject(paramVarArgs, null, false);
                    paramVarArgs.type = 6;
                    paramVarArgs.contentType = 2;
                    this.messages.add(this.messages.size() - 1, paramVarArgs);
                    this.unreadMessageObject = paramVarArgs;
                    this.scrollToMessage = this.unreadMessageObject;
                    this.scrollToMessagePosition = 55536;
                    i = paramInt + 1;
                    bool3 = bool2;
                    break;
                    if (this.currentEncryptedChat == null) {
                      break label867;
                    }
                    this.maxMessageId[j] = Math.max(((MessageObject)localObject3).getId(), this.maxMessageId[j]);
                    this.minMessageId[j] = Math.min(((MessageObject)localObject3).getId(), this.minMessageId[j]);
                    break label867;
                    this.messages.add(localObject1);
                  }
                  i = paramInt;
                  bool3 = bool2;
                } while (i1 != 3);
                i = paramInt;
                bool3 = bool2;
              } while (((MessageObject)localObject3).getId() != this.startLoadFromMessageId);
              if (this.needSelectFromMessageId) {}
              for (this.highlightMessageId = ((MessageObject)localObject3).getId();; this.highlightMessageId = Integer.MAX_VALUE)
              {
                this.scrollToMessage = ((MessageObject)localObject3);
                this.startLoadFromMessageId = 0;
                i = paramInt;
                bool3 = bool2;
                if (this.scrollToMessagePosition != 55536) {
                  break;
                }
                this.scrollToMessagePosition = 56536;
                i = paramInt;
                bool3 = bool2;
                break;
              }
            }
            if ((i1 == 0) && (paramInt == 0)) {
              this.loadsCount -= 1;
            }
            if ((this.forwardEndReached[j] != 0) && (j != 1))
            {
              this.first_unread_id = 0;
              this.last_message_id = 0;
            }
            if ((this.loadsCount <= 2) && (!bool4)) {
              updateSpamView();
            }
            if (i1 == 1)
            {
              i = paramInt;
              if (((ArrayList)localObject2).size() != n)
              {
                i = paramInt;
                if (!bool4)
                {
                  this.forwardEndReached[j] = true;
                  i = paramInt;
                  if (j != 1)
                  {
                    this.first_unread_id = 0;
                    this.last_message_id = 0;
                    this.chatAdapter.notifyItemRemoved(this.chatAdapter.getItemCount() - 1);
                    i = paramInt - 1;
                  }
                  this.startLoadFromMessageId = 0;
                }
              }
              if (i > 0)
              {
                k = this.chatLayoutManager.findLastVisibleItemPosition();
                paramInt = 0;
                if (k == this.chatLayoutManager.getItemCount() - 1) {
                  break label1939;
                }
                m = -1;
                k = paramInt;
                paramInt = m;
                this.chatAdapter.notifyItemRangeInserted(this.chatAdapter.getItemCount() - 1, i);
                if (paramInt != -1) {
                  this.chatLayoutManager.scrollToPositionWithOffset(paramInt, k);
                }
              }
              this.loadingForward = false;
              if ((this.first) && (this.messages.size() > 0))
              {
                if (j == 0) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      if (ChatActivity.this.last_message_id != 0)
                      {
                        MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, this.val$lastid, ChatActivity.this.last_message_id, i2, bool1, false);
                        return;
                      }
                      MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, this.val$lastid, ChatActivity.this.minMessageId[0], ChatActivity.this.maxDate[0], bool1, false);
                    }
                  }, 700L);
                }
                this.first = false;
              }
              if ((this.messages.isEmpty()) && (this.currentEncryptedChat == null) && (this.currentUser != null) && (this.currentUser.bot) && (this.botUser == null))
              {
                this.botUser = "";
                updateBottomOverlay();
              }
              if ((i != 0) || (this.currentEncryptedChat == null) || (this.endReached[0] != 0)) {
                break label2762;
              }
              this.first = true;
              if (this.chatListView != null) {
                this.chatListView.setEmptyView(null);
              }
              if (this.emptyViewContainer != null) {
                this.emptyViewContainer.setVisibility(4);
              }
            }
            int i3;
            int i4;
            for (;;)
            {
              checkScrollForLoad(false);
              return;
              paramVarArgs = this.chatLayoutManager.findViewByPosition(k);
              if (paramVarArgs == null) {}
              for (paramInt = 0;; paramInt = paramVarArgs.getTop())
              {
                m = paramInt - this.chatListView.getPaddingTop();
                paramInt = k;
                k = m;
                break;
              }
              if ((((ArrayList)localObject2).size() < n) && (i1 != 3))
              {
                if (!bool4) {
                  break label2325;
                }
                if ((this.currentEncryptedChat != null) || (this.isBroadcast)) {
                  this.endReached[j] = true;
                }
                if (i1 != 2) {
                  this.cacheEndReached[j] = true;
                }
              }
              this.loading = false;
              if (this.chatListView != null)
              {
                if ((this.first) || (this.scrollToTopOnResume) || (this.forceScrollToTop))
                {
                  this.forceScrollToTop = false;
                  this.chatAdapter.notifyDataSetChanged();
                  if (this.scrollToMessage != null) {
                    if (this.scrollToMessagePosition == 56536)
                    {
                      i = Math.max(0, (this.chatListView.getHeight() - this.scrollToMessage.getApproximateHeight()) / 2);
                      if (!this.messages.isEmpty())
                      {
                        if ((this.messages.get(this.messages.size() - 1) != this.scrollToMessage) && (this.messages.get(this.messages.size() - 2) != this.scrollToMessage)) {
                          break label2371;
                        }
                        paramVarArgs = this.chatLayoutManager;
                        if (!this.chatAdapter.isBot) {
                          break label2365;
                        }
                        k = 1;
                        paramVarArgs.scrollToPositionWithOffset(k, -this.chatListView.getPaddingTop() - AndroidUtilities.dp(7.0F) + i);
                      }
                      this.chatListView.invalidate();
                      if ((this.scrollToMessagePosition == 55536) || (this.scrollToMessagePosition == 56536)) {
                        showPagedownButton(true, true);
                      }
                      this.scrollToMessagePosition = 55536;
                      this.scrollToMessage = null;
                    }
                  }
                }
                do
                {
                  do
                  {
                    for (;;)
                    {
                      if (this.paused)
                      {
                        this.scrollToTopOnResume = true;
                        if (this.scrollToMessage != null) {
                          this.scrollToTopUnReadOnResume = true;
                        }
                      }
                      i = paramInt;
                      if (!this.first) {
                        break;
                      }
                      i = paramInt;
                      if (this.chatListView == null) {
                        break;
                      }
                      this.chatListView.setEmptyView(this.emptyViewContainer);
                      i = paramInt;
                      break;
                      if (i1 == 2) {
                        break label2041;
                      }
                      this.endReached[j] = true;
                      break label2041;
                      if (this.scrollToMessagePosition == 55536)
                      {
                        i = 0;
                        break label2125;
                      }
                      i = this.scrollToMessagePosition;
                      break label2125;
                      k = 0;
                      break label2199;
                      this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.messagesStartRow + this.messages.size() - this.messages.indexOf(this.scrollToMessage) - 1, -this.chatListView.getPaddingTop() - AndroidUtilities.dp(7.0F) + i);
                      break label2222;
                      moveScrollToLastMessage();
                    }
                    if (paramInt == 0) {
                      break label2678;
                    }
                    k = 0;
                    i = k;
                    if (this.endReached[j] != 0) {
                      if ((j != 0) || (this.mergeDialogId != 0L))
                      {
                        i = k;
                        if (j != 1) {}
                      }
                      else
                      {
                        k = 1;
                        paramVarArgs = this.chatAdapter;
                        if (!this.chatAdapter.isBot) {
                          break label2635;
                        }
                        i = 1;
                        paramVarArgs.notifyItemRangeChanged(i, 2);
                        i = k;
                      }
                    }
                    i3 = this.chatLayoutManager.findLastVisibleItemPosition();
                    paramVarArgs = this.chatLayoutManager.findViewByPosition(i3);
                    if (paramVarArgs != null) {
                      break label2640;
                    }
                    k = 0;
                    i4 = this.chatListView.getPaddingTop();
                    if (i == 0) {
                      break label2649;
                    }
                    m = 1;
                    if (paramInt - m > 0)
                    {
                      paramVarArgs = this.chatAdapter;
                      if (!this.chatAdapter.isBot) {
                        break label2655;
                      }
                      m = 2;
                      if (i == 0) {
                        break label2661;
                      }
                      n = 0;
                      if (i == 0) {
                        break label2667;
                      }
                      i1 = 1;
                      paramVarArgs.notifyItemRangeInserted(m + n, paramInt - i1);
                    }
                  } while (i3 == -1);
                  paramVarArgs = this.chatLayoutManager;
                  if (i != 0) {}
                  for (i = 1;; i = 0)
                  {
                    paramVarArgs.scrollToPositionWithOffset(i3 + paramInt - i, k - i4);
                    break;
                    i = 0;
                    break label2497;
                    k = paramVarArgs.getTop();
                    break label2532;
                    m = 0;
                    break label2548;
                    m = 1;
                    break label2573;
                    n = 1;
                    break label2580;
                    i1 = 0;
                    break label2587;
                  }
                } while ((this.endReached[j] == 0) || (((j != 0) || (this.mergeDialogId != 0L)) && (j != 1)));
                paramVarArgs = this.chatAdapter;
                if (this.chatAdapter.isBot) {}
                for (i = 1;; i = 0)
                {
                  paramVarArgs.notifyItemRemoved(i);
                  break;
                }
              }
              this.scrollToTopOnResume = true;
              i = paramInt;
              if (this.scrollToMessage == null) {
                break label1767;
              }
              this.scrollToTopUnReadOnResume = true;
              i = paramInt;
              break label1767;
              if (this.progressView != null) {
                this.progressView.setVisibility(4);
              }
            }
            if (paramInt == NotificationCenter.emojiDidLoaded)
            {
              if (this.chatListView != null) {
                this.chatListView.invalidateViews();
              }
              if (this.replyObjectTextView != null) {
                this.replyObjectTextView.invalidate();
              }
              if (this.alertTextView != null) {
                this.alertTextView.invalidate();
              }
              if (this.pinnedMessageTextView != null) {
                this.pinnedMessageTextView.invalidate();
              }
              if (this.mentionListView != null) {
                this.mentionListView.invalidateViews();
              }
            }
            else
            {
              if (paramInt == NotificationCenter.updateInterfaces)
              {
                j = ((Integer)paramVarArgs[0]).intValue();
                if (((j & 0x1) != 0) || ((j & 0x10) != 0))
                {
                  if (this.currentChat == null) {
                    break label3117;
                  }
                  paramVarArgs = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                  if (paramVarArgs != null) {
                    this.currentChat = paramVarArgs;
                  }
                }
                for (;;)
                {
                  updateTitle();
                  paramInt = 0;
                  if (((j & 0x20) != 0) || ((j & 0x4) != 0))
                  {
                    if ((this.currentChat != null) && (this.avatarContainer != null)) {
                      this.avatarContainer.updateOnlineCount();
                    }
                    paramInt = 1;
                  }
                  if (((j & 0x2) != 0) || ((j & 0x8) != 0) || ((j & 0x1) != 0))
                  {
                    checkAndUpdateAvatar();
                    updateVisibleRows();
                  }
                  if ((j & 0x40) != 0) {
                    paramInt = 1;
                  }
                  i = paramInt;
                  if ((j & 0x2000) != 0)
                  {
                    i = paramInt;
                    if (ChatObject.isChannel(this.currentChat))
                    {
                      paramVarArgs = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                      if (paramVarArgs == null) {
                        break;
                      }
                      this.currentChat = paramVarArgs;
                      paramInt = 1;
                      updateBottomOverlay();
                      i = paramInt;
                      if (this.chatActivityEnterView != null)
                      {
                        this.chatActivityEnterView.setDialogId(this.dialog_id);
                        i = paramInt;
                      }
                    }
                  }
                  if ((this.avatarContainer != null) && (i != 0)) {
                    this.avatarContainer.updateSubtitle();
                  }
                  if ((j & 0x80) == 0) {
                    break;
                  }
                  updateContactStatus();
                  return;
                  if (this.currentUser != null)
                  {
                    paramVarArgs = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                    if (paramVarArgs != null) {
                      this.currentUser = paramVarArgs;
                    }
                  }
                }
              }
              if (paramInt == NotificationCenter.didReceivedNewMessages)
              {
                if (((Long)paramVarArgs[0]).longValue() == this.dialog_id)
                {
                  n = 0;
                  m = 0;
                  i2 = 0;
                  localObject3 = (ArrayList)paramVarArgs[1];
                  if ((this.currentEncryptedChat != null) && (((ArrayList)localObject3).size() == 1))
                  {
                    paramVarArgs = (MessageObject)((ArrayList)localObject3).get(0);
                    if ((this.currentEncryptedChat != null) && (paramVarArgs.isOut()) && (paramVarArgs.messageOwner.action != null) && ((paramVarArgs.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction)) && ((paramVarArgs.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) && (getParentActivity() != null) && (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 17) && (this.currentEncryptedChat.ttl > 0) && (this.currentEncryptedChat.ttl <= 60))
                    {
                      paramVarArgs = new AlertDialog.Builder(getParentActivity());
                      paramVarArgs.setTitle(LocaleController.getString("AppName", 2131165299));
                      paramVarArgs.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                      paramVarArgs.setMessage(LocaleController.formatString("CompatibilityChat", 2131165518, new Object[] { this.currentUser.first_name, this.currentUser.first_name }));
                      showDialog(paramVarArgs.create());
                    }
                  }
                  if ((this.currentChat != null) || (this.inlineReturn != 0L))
                  {
                    paramInt = 0;
                    if (paramInt < ((ArrayList)localObject3).size())
                    {
                      paramVarArgs = (MessageObject)((ArrayList)localObject3).get(paramInt);
                      if (this.currentChat != null) {
                        if ((((paramVarArgs.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser)) && (paramVarArgs.messageOwner.action.user_id == UserConfig.getClientUserId())) || (((paramVarArgs.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser)) && (paramVarArgs.messageOwner.action.users.contains(Integer.valueOf(UserConfig.getClientUserId())))))
                        {
                          paramVarArgs = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                          if (paramVarArgs != null)
                          {
                            this.currentChat = paramVarArgs;
                            checkActionBarMenu();
                            updateBottomOverlay();
                            if (this.avatarContainer != null) {
                              this.avatarContainer.updateSubtitle();
                            }
                          }
                        }
                      }
                      while ((this.inlineReturn == 0L) || (paramVarArgs.messageOwner.reply_markup == null)) {
                        for (;;)
                        {
                          paramInt += 1;
                          break;
                          if ((paramVarArgs.messageOwner.reply_to_msg_id != 0) && (paramVarArgs.replyMessageObject == null))
                          {
                            paramVarArgs.replyMessageObject = ((MessageObject)this.messagesDict[0].get(Integer.valueOf(paramVarArgs.messageOwner.reply_to_msg_id)));
                            if ((paramVarArgs.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) {
                              paramVarArgs.generatePinMessageText(null, null);
                            } else if ((paramVarArgs.messageOwner.action instanceof TLRPC.TL_messageActionGameScore)) {
                              paramVarArgs.generateGameMessageText(null);
                            }
                          }
                        }
                      }
                      i = 0;
                      if (i < paramVarArgs.messageOwner.reply_markup.rows.size())
                      {
                        localObject1 = (TLRPC.TL_keyboardButtonRow)paramVarArgs.messageOwner.reply_markup.rows.get(i);
                        j = 0;
                      }
                      for (;;)
                      {
                        if (j < ((TLRPC.TL_keyboardButtonRow)localObject1).buttons.size())
                        {
                          localObject2 = (TLRPC.KeyboardButton)((TLRPC.TL_keyboardButtonRow)localObject1).buttons.get(j);
                          if ((localObject2 instanceof TLRPC.TL_keyboardButtonSwitchInline)) {
                            processSwitchButton((TLRPC.TL_keyboardButtonSwitchInline)localObject2);
                          }
                        }
                        else
                        {
                          i += 1;
                          break label3675;
                          break;
                        }
                        j += 1;
                      }
                    }
                  }
                  j = 0;
                  paramInt = 0;
                  int i5;
                  if (this.forwardEndReached[0] == 0)
                  {
                    i5 = Integer.MIN_VALUE;
                    k = Integer.MIN_VALUE;
                    if (this.currentEncryptedChat != null) {
                      k = Integer.MAX_VALUE;
                    }
                    j = 0;
                    n = 0;
                    i = m;
                    m = n;
                    while (m < ((ArrayList)localObject3).size())
                    {
                      localObject1 = (MessageObject)((ArrayList)localObject3).get(m);
                      if ((this.currentUser != null) && (this.currentUser.bot) && (((MessageObject)localObject1).isOut())) {
                        ((MessageObject)localObject1).setIsRead();
                      }
                      if ((this.avatarContainer != null) && (this.currentEncryptedChat != null) && (((MessageObject)localObject1).messageOwner.action != null) && ((((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageEncryptedAction)) && ((((MessageObject)localObject1).messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))) {
                        this.avatarContainer.setTime(((TLRPC.TL_decryptedMessageActionSetMessageTTL)((MessageObject)localObject1).messageOwner.action.encryptedAction).ttl_seconds);
                      }
                      if ((((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo))
                      {
                        localObject2 = new Bundle();
                        ((Bundle)localObject2).putInt("chat_id", ((MessageObject)localObject1).messageOwner.action.channel_id);
                        if (this.parentLayout.fragmentsStack.size() > 0) {}
                        for (paramVarArgs = (BaseFragment)this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);; paramVarArgs = null)
                        {
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              ActionBarLayout localActionBarLayout = ChatActivity.this.parentLayout;
                              if (paramVarArgs != null) {
                                NotificationCenter.getInstance().removeObserver(paramVarArgs, NotificationCenter.closeChats);
                              }
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                              localActionBarLayout.presentFragment(new ChatActivity(localObject2), true);
                              AndroidUtilities.runOnUIThread(new Runnable()
                              {
                                public void run()
                                {
                                  MessagesController.getInstance().loadFullChat(ChatActivity.63.this.val$channel_id, 0, true);
                                }
                              }, 1000L);
                            }
                          });
                          return;
                        }
                      }
                      n = paramInt;
                      if (this.currentChat != null)
                      {
                        n = paramInt;
                        if (this.currentChat.megagroup) {
                          if (!(((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser))
                          {
                            n = paramInt;
                            if (!(((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser)) {}
                          }
                          else
                          {
                            n = 1;
                          }
                        }
                      }
                      if ((((MessageObject)localObject1).isOut()) && (((MessageObject)localObject1).isSending()))
                      {
                        scrollToLastMessage(false);
                        return;
                      }
                      i1 = k;
                      i2 = i5;
                      i3 = j;
                      i4 = i;
                      if (((MessageObject)localObject1).type >= 0)
                      {
                        if (this.messagesDict[0].containsKey(Integer.valueOf(((MessageObject)localObject1).getId())))
                        {
                          i4 = i;
                          i3 = j;
                          i2 = i5;
                          i1 = k;
                        }
                      }
                      else
                      {
                        m += 1;
                        k = i1;
                        i5 = i2;
                        j = i3;
                        paramInt = n;
                        i = i4;
                        continue;
                      }
                      ((MessageObject)localObject1).checkLayout();
                      i5 = Math.max(i5, ((MessageObject)localObject1).messageOwner.date);
                      if (((MessageObject)localObject1).getId() > 0)
                      {
                        paramInt = Math.max(((MessageObject)localObject1).getId(), k);
                        this.last_message_id = Math.max(this.last_message_id, ((MessageObject)localObject1).getId());
                      }
                      for (;;)
                      {
                        k = j;
                        if (!((MessageObject)localObject1).isOut())
                        {
                          k = j;
                          if (((MessageObject)localObject1).isUnread())
                          {
                            this.unread_to_load += 1;
                            k = 1;
                          }
                        }
                        if (((MessageObject)localObject1).type != 10)
                        {
                          i1 = paramInt;
                          i2 = i5;
                          i3 = k;
                          i4 = i;
                          if (((MessageObject)localObject1).type != 11) {
                            break;
                          }
                        }
                        i4 = 1;
                        i1 = paramInt;
                        i2 = i5;
                        i3 = k;
                        break;
                        paramInt = k;
                        if (this.currentEncryptedChat != null)
                        {
                          paramInt = Math.min(((MessageObject)localObject1).getId(), k);
                          this.last_message_id = Math.min(this.last_message_id, ((MessageObject)localObject1).getId());
                        }
                      }
                    }
                    if (j != 0)
                    {
                      if (this.paused)
                      {
                        this.readWhenResume = true;
                        this.readWithDate = i5;
                        this.readWithMid = k;
                      }
                    }
                    else {
                      updateVisibleRows();
                    }
                  }
                  for (;;)
                  {
                    if ((!this.messages.isEmpty()) && (this.botUser != null) && (this.botUser.length() == 0))
                    {
                      this.botUser = null;
                      updateBottomOverlay();
                    }
                    if (i != 0)
                    {
                      updateTitle();
                      checkAndUpdateAvatar();
                    }
                    if (paramInt == 0) {
                      break;
                    }
                    MessagesController.getInstance().loadFullChat(this.currentChat.id, 0, true);
                    return;
                    if (this.messages.size() <= 0) {
                      break label4446;
                    }
                    MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject)this.messages.get(0)).getId(), k, i5, true, false);
                    break label4446;
                    i1 = 0;
                    i = 1;
                    int i13 = this.messages.size();
                    i3 = 0;
                    paramVarArgs = null;
                    k = -1;
                    int i7 = 0;
                    if (i7 < ((ArrayList)localObject3).size())
                    {
                      MessageObject localMessageObject = (MessageObject)((ArrayList)localObject3).get(i7);
                      paramInt = k;
                      if (i7 == 0)
                      {
                        if (localMessageObject.messageOwner.id >= 0) {
                          break label4864;
                        }
                        paramInt = 0;
                      }
                      int i9;
                      int i10;
                      int i11;
                      int i6;
                      int i8;
                      int i12;
                      for (;;)
                      {
                        if ((this.currentUser != null) && (this.currentUser.bot) && (localMessageObject.isOut())) {
                          localMessageObject.setIsRead();
                        }
                        if ((this.avatarContainer != null) && (this.currentEncryptedChat != null) && (localMessageObject.messageOwner.action != null) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction)) && ((localMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))) {
                          this.avatarContainer.setTime(((TLRPC.TL_decryptedMessageActionSetMessageTTL)localMessageObject.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        i9 = i3;
                        i10 = i2;
                        i11 = i1;
                        i5 = paramInt;
                        i6 = j;
                        i8 = i;
                        i12 = n;
                        localObject2 = paramVarArgs;
                        if (localMessageObject.type >= 0)
                        {
                          if (!this.messagesDict[0].containsKey(Integer.valueOf(localMessageObject.getId()))) {
                            break label5040;
                          }
                          localObject2 = paramVarArgs;
                          i12 = n;
                          i8 = i;
                          i6 = j;
                          i5 = paramInt;
                          i11 = i1;
                          i10 = i2;
                          i9 = i3;
                        }
                        i7 += 1;
                        i3 = i9;
                        i2 = i10;
                        i1 = i11;
                        k = i5;
                        j = i6;
                        i = i8;
                        n = i12;
                        paramVarArgs = (Object[])localObject2;
                        break;
                        if (!this.messages.isEmpty())
                        {
                          i4 = this.messages.size();
                          paramInt = 0;
                          for (;;)
                          {
                            m = k;
                            if (paramInt < i4)
                            {
                              localObject1 = (MessageObject)this.messages.get(paramInt);
                              if ((((MessageObject)localObject1).type < 0) || (((MessageObject)localObject1).messageOwner.date <= 0)) {
                                break label5028;
                              }
                              if ((((MessageObject)localObject1).messageOwner.id <= 0) || (localMessageObject.messageOwner.id <= 0)) {
                                break label5003;
                              }
                              if (((MessageObject)localObject1).messageOwner.id >= localMessageObject.messageOwner.id) {
                                break label5028;
                              }
                            }
                            for (m = paramInt;; m = paramInt)
                            {
                              if (m != -1)
                              {
                                paramInt = m;
                                if (m <= this.messages.size()) {
                                  break;
                                }
                              }
                              paramInt = this.messages.size();
                              break;
                              if (((MessageObject)localObject1).messageOwner.date >= localMessageObject.messageOwner.date) {
                                break label5028;
                              }
                            }
                            paramInt += 1;
                          }
                        }
                        paramInt = 0;
                      }
                      localObject1 = paramVarArgs;
                      if (this.currentEncryptedChat != null)
                      {
                        localObject1 = paramVarArgs;
                        if ((localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
                        {
                          localObject1 = paramVarArgs;
                          if ((localMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPageUrlPending))
                          {
                            localObject1 = paramVarArgs;
                            if (paramVarArgs == null) {
                              localObject1 = new HashMap();
                            }
                            localObject2 = (ArrayList)((HashMap)localObject1).get(localMessageObject.messageOwner.media.webpage.url);
                            paramVarArgs = (Object[])localObject2;
                            if (localObject2 == null)
                            {
                              paramVarArgs = new ArrayList();
                              ((HashMap)localObject1).put(localMessageObject.messageOwner.media.webpage.url, paramVarArgs);
                            }
                            paramVarArgs.add(localMessageObject);
                          }
                        }
                      }
                      localMessageObject.checkLayout();
                      if ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo))
                      {
                        localObject1 = new Bundle();
                        ((Bundle)localObject1).putInt("chat_id", localMessageObject.messageOwner.action.channel_id);
                        if (this.parentLayout.fragmentsStack.size() > 0) {}
                        for (paramVarArgs = (BaseFragment)this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);; paramVarArgs = null)
                        {
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              ActionBarLayout localActionBarLayout = ChatActivity.this.parentLayout;
                              if (paramVarArgs != null) {
                                NotificationCenter.getInstance().removeObserver(paramVarArgs, NotificationCenter.closeChats);
                              }
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                              localActionBarLayout.presentFragment(new ChatActivity(localObject1), true);
                              AndroidUtilities.runOnUIThread(new Runnable()
                              {
                                public void run()
                                {
                                  MessagesController.getInstance().loadFullChat(ChatActivity.64.this.val$channel_id, 0, true);
                                }
                              }, 1000L);
                            }
                          });
                          return;
                        }
                      }
                      k = j;
                      if (this.currentChat != null)
                      {
                        k = j;
                        if (this.currentChat.megagroup) {
                          if (!(localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser))
                          {
                            k = j;
                            if (!(localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser)) {}
                          }
                          else
                          {
                            k = 1;
                          }
                        }
                      }
                      if ((this.minDate[0] == 0) || (localMessageObject.messageOwner.date < this.minDate[0])) {
                        this.minDate[0] = localMessageObject.messageOwner.date;
                      }
                      if (localMessageObject.isOut())
                      {
                        removeUnreadPlane();
                        i2 = 1;
                      }
                      if (localMessageObject.getId() > 0)
                      {
                        this.maxMessageId[0] = Math.min(localMessageObject.getId(), this.maxMessageId[0]);
                        this.minMessageId[0] = Math.max(localMessageObject.getId(), this.minMessageId[0]);
                      }
                      for (;;)
                      {
                        this.maxDate[0] = Math.max(this.maxDate[0], localMessageObject.messageOwner.date);
                        this.messagesDict[0].put(Integer.valueOf(localMessageObject.getId()), localMessageObject);
                        localObject2 = (ArrayList)this.messagesByDays.get(localMessageObject.dateKey);
                        j = i3;
                        paramVarArgs = (Object[])localObject2;
                        if (localObject2 == null)
                        {
                          paramVarArgs = new ArrayList();
                          this.messagesByDays.put(localMessageObject.dateKey, paramVarArgs);
                          localObject2 = new TLRPC.Message();
                          ((TLRPC.Message)localObject2).message = LocaleController.formatDateChat(localMessageObject.messageOwner.date);
                          ((TLRPC.Message)localObject2).id = 0;
                          ((TLRPC.Message)localObject2).date = localMessageObject.messageOwner.date;
                          localObject2 = new MessageObject((TLRPC.Message)localObject2, null, false);
                          ((MessageObject)localObject2).type = 10;
                          ((MessageObject)localObject2).contentType = 1;
                          this.messages.add(paramInt, localObject2);
                          j = i3 + 1;
                        }
                        i8 = j;
                        i4 = i1;
                        m = paramInt;
                        i3 = i;
                        if (!localMessageObject.isOut())
                        {
                          i6 = j;
                          i5 = paramInt;
                          i3 = i;
                          if (this.paused)
                          {
                            i6 = j;
                            i5 = paramInt;
                            i3 = i;
                            if (paramInt == 0)
                            {
                              m = paramInt;
                              if (!this.scrollToTopUnReadOnResume)
                              {
                                m = paramInt;
                                if (this.unreadMessageObject != null)
                                {
                                  removeMessageObject(this.unreadMessageObject);
                                  m = paramInt;
                                  if (paramInt > 0) {
                                    m = paramInt - 1;
                                  }
                                  this.unreadMessageObject = null;
                                }
                              }
                              i6 = j;
                              i5 = m;
                              i3 = i;
                              if (this.unreadMessageObject == null)
                              {
                                localObject2 = new TLRPC.Message();
                                ((TLRPC.Message)localObject2).message = "";
                                ((TLRPC.Message)localObject2).id = 0;
                                localObject2 = new MessageObject((TLRPC.Message)localObject2, null, false);
                                ((MessageObject)localObject2).type = 6;
                                ((MessageObject)localObject2).contentType = 2;
                                this.messages.add(0, localObject2);
                                this.unreadMessageObject = ((MessageObject)localObject2);
                                this.scrollToMessage = this.unreadMessageObject;
                                this.scrollToMessagePosition = 55536;
                                i3 = 0;
                                this.unread_to_load = 0;
                                this.scrollToTopUnReadOnResume = true;
                                i6 = j + 1;
                                i5 = m;
                              }
                            }
                          }
                          paramInt = i3;
                          if (this.unreadMessageObject != null)
                          {
                            this.unread_to_load += 1;
                            paramInt = 1;
                          }
                          i8 = i6;
                          i4 = i1;
                          m = i5;
                          i3 = paramInt;
                          if (localMessageObject.isUnread())
                          {
                            if (!this.paused) {
                              localMessageObject.setIsRead();
                            }
                            i4 = 1;
                            i3 = paramInt;
                            m = i5;
                            i8 = i6;
                          }
                        }
                        paramVarArgs.add(0, localMessageObject);
                        this.messages.add(m, localMessageObject);
                        paramInt = i8 + 1;
                        this.newUnreadMessageCount += 1;
                        if (localMessageObject.type != 10)
                        {
                          i9 = paramInt;
                          i10 = i2;
                          i11 = i4;
                          i5 = m;
                          i6 = k;
                          i8 = i3;
                          i12 = n;
                          localObject2 = localObject1;
                          if (localMessageObject.type != 11) {
                            break;
                          }
                        }
                        i12 = 1;
                        i9 = paramInt;
                        i10 = i2;
                        i11 = i4;
                        i5 = m;
                        i6 = k;
                        i8 = i3;
                        localObject2 = localObject1;
                        break;
                        if (this.currentEncryptedChat != null)
                        {
                          this.maxMessageId[0] = Math.max(localMessageObject.getId(), this.maxMessageId[0]);
                          this.minMessageId[0] = Math.min(localMessageObject.getId(), this.minMessageId[0]);
                        }
                      }
                    }
                    if (paramVarArgs != null) {
                      MessagesController.getInstance().reloadWebPages(this.dialog_id, paramVarArgs);
                    }
                    if (this.progressView != null) {
                      this.progressView.setVisibility(4);
                    }
                    if (this.chatAdapter != null)
                    {
                      if (i != 0) {
                        this.chatAdapter.updateRowWithMessageObject(this.unreadMessageObject);
                      }
                      if (i3 != 0) {
                        this.chatAdapter.notifyItemRangeInserted(this.chatAdapter.getItemCount() - k, i3);
                      }
                      if ((this.chatListView == null) || (this.chatAdapter == null)) {
                        break label6394;
                      }
                      i = this.chatLayoutManager.findLastVisibleItemPosition();
                      paramInt = i;
                      if (i == -1) {
                        paramInt = 0;
                      }
                      i = paramInt;
                      if (this.endReached[0] != 0) {
                        i = paramInt + 1;
                      }
                      paramInt = i13;
                      if (this.chatAdapter.isBot) {
                        paramInt = i13 + 1;
                      }
                      if ((i < paramInt) && (i2 == 0)) {
                        break label6336;
                      }
                      this.newUnreadMessageCount = 0;
                      if (!this.firstLoading)
                      {
                        if (!this.paused) {
                          break label6324;
                        }
                        this.scrollToTopOnResume = true;
                      }
                    }
                    for (;;)
                    {
                      paramInt = j;
                      i = n;
                      if (i1 == 0) {
                        break;
                      }
                      if (!this.paused) {
                        break label6402;
                      }
                      this.readWhenResume = true;
                      this.readWithDate = this.maxDate[0];
                      this.readWithMid = this.minMessageId[0];
                      paramInt = j;
                      i = n;
                      break;
                      this.scrollToTopOnResume = true;
                      break label6166;
                      this.forceScrollToTop = true;
                      moveScrollToLastMessage();
                      continue;
                      if ((this.newUnreadMessageCount != 0) && (this.pagedownButtonCounter != null))
                      {
                        this.pagedownButtonCounter.setVisibility(0);
                        this.pagedownButtonCounter.setText(String.format("%d", new Object[] { Integer.valueOf(this.newUnreadMessageCount) }));
                      }
                      showPagedownButton(true, true);
                      continue;
                      this.scrollToTopOnResume = true;
                    }
                    MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject)this.messages.get(0)).getId(), this.minMessageId[0], this.maxDate[0], true, false);
                    paramInt = j;
                    i = n;
                  }
                }
              }
              else if (paramInt == NotificationCenter.closeChats)
              {
                if ((paramVarArgs != null) && (paramVarArgs.length > 0))
                {
                  if (((Long)paramVarArgs[0]).longValue() == this.dialog_id) {
                    finishFragment();
                  }
                }
                else {
                  removeSelfFromStack();
                }
              }
              else
              {
                if (paramInt == NotificationCenter.messagesRead)
                {
                  localObject1 = (SparseArray)paramVarArgs[0];
                  paramVarArgs = (SparseArray)paramVarArgs[1];
                  k = 0;
                  j = 0;
                  i = 0;
                  for (;;)
                  {
                    paramInt = k;
                    if (i >= ((SparseArray)localObject1).size()) {
                      break label6651;
                    }
                    paramInt = ((SparseArray)localObject1).keyAt(i);
                    l = ((Long)((SparseArray)localObject1).get(paramInt)).longValue();
                    if (paramInt == this.dialog_id) {
                      break;
                    }
                    i += 1;
                  }
                  paramInt = 0;
                  i = j;
                  j = paramInt;
                  for (;;)
                  {
                    paramInt = i;
                    if (j < this.messages.size())
                    {
                      localObject1 = (MessageObject)this.messages.get(j);
                      paramInt = i;
                      if (((MessageObject)localObject1).isOut()) {
                        break label6711;
                      }
                      paramInt = i;
                      if (((MessageObject)localObject1).getId() <= 0) {
                        break label6711;
                      }
                      paramInt = i;
                      if (((MessageObject)localObject1).getId() > (int)l) {
                        break label6711;
                      }
                      if (!((MessageObject)localObject1).isUnread()) {
                        paramInt = i;
                      }
                    }
                    else
                    {
                      i = 0;
                      for (;;)
                      {
                        j = paramInt;
                        if (i >= paramVarArgs.size()) {
                          break label6792;
                        }
                        j = paramVarArgs.keyAt(i);
                        k = (int)((Long)paramVarArgs.get(j)).longValue();
                        if (j == this.dialog_id) {
                          break;
                        }
                        i += 1;
                      }
                    }
                    ((MessageObject)localObject1).setIsRead();
                    paramInt = 1;
                    j += 1;
                    i = paramInt;
                  }
                  i = 0;
                  for (;;)
                  {
                    j = paramInt;
                    if (i < this.messages.size())
                    {
                      paramVarArgs = (MessageObject)this.messages.get(i);
                      j = paramInt;
                      if (!paramVarArgs.isOut()) {
                        break label6809;
                      }
                      j = paramInt;
                      if (paramVarArgs.getId() <= 0) {
                        break label6809;
                      }
                      j = paramInt;
                      if (paramVarArgs.getId() > k) {
                        break label6809;
                      }
                      if (!paramVarArgs.isUnread()) {
                        j = paramInt;
                      }
                    }
                    else
                    {
                      if (j == 0) {
                        break;
                      }
                      updateVisibleRows();
                      return;
                    }
                    paramVarArgs.setIsRead();
                    j = 1;
                    i += 1;
                    paramInt = j;
                  }
                }
                if (paramInt != NotificationCenter.messagesDeleted) {
                  break;
                }
                localObject1 = (ArrayList)paramVarArgs[0];
                m = ((Integer)paramVarArgs[1]).intValue();
                paramInt = 0;
                if (ChatObject.isChannel(this.currentChat)) {
                  if ((m == 0) && (this.mergeDialogId != 0L)) {
                    paramInt = 1;
                  }
                }
                while (m == 0) {
                  for (;;)
                  {
                    i = 0;
                    j = 0;
                    while (j < ((ArrayList)localObject1).size())
                    {
                      localObject2 = (Integer)((ArrayList)localObject1).get(j);
                      paramVarArgs = (MessageObject)this.messagesDict[paramInt].get(localObject2);
                      if ((paramInt == 0) && (this.info != null) && (this.info.pinned_msg_id == ((Integer)localObject2).intValue()))
                      {
                        this.pinnedMessageObject = null;
                        this.info.pinned_msg_id = 0;
                        MessagesStorage.getInstance().updateChannelPinnedMessage(m, 0);
                        updatePinnedMessageView(true);
                      }
                      k = i;
                      if (paramVarArgs != null)
                      {
                        n = this.messages.indexOf(paramVarArgs);
                        k = i;
                        if (n != -1)
                        {
                          this.messages.remove(n);
                          this.messagesDict[paramInt].remove(localObject2);
                          localObject2 = (ArrayList)this.messagesByDays.get(paramVarArgs.dateKey);
                          if (localObject2 != null)
                          {
                            ((ArrayList)localObject2).remove(paramVarArgs);
                            if (((ArrayList)localObject2).isEmpty())
                            {
                              this.messagesByDays.remove(paramVarArgs.dateKey);
                              if ((n >= 0) && (n < this.messages.size())) {
                                this.messages.remove(n);
                              }
                            }
                          }
                          k = 1;
                        }
                      }
                      j += 1;
                      i = k;
                    }
                    if (m != this.currentChat.id) {
                      break;
                    }
                    paramInt = 0;
                  }
                }
                return;
                if (this.messages.isEmpty())
                {
                  if ((this.endReached[0] != 0) || (this.loading)) {
                    break label7438;
                  }
                  if (this.progressView != null) {
                    this.progressView.setVisibility(4);
                  }
                  if (this.chatListView != null) {
                    this.chatListView.setEmptyView(null);
                  }
                  if (this.currentEncryptedChat != null) {
                    break label7389;
                  }
                  paramVarArgs = this.maxMessageId;
                  this.maxMessageId[1] = Integer.MAX_VALUE;
                  paramVarArgs[0] = Integer.MAX_VALUE;
                  paramVarArgs = this.minMessageId;
                  this.minMessageId[1] = Integer.MIN_VALUE;
                  paramVarArgs[0] = Integer.MIN_VALUE;
                  paramVarArgs = this.maxDate;
                  this.maxDate[1] = Integer.MIN_VALUE;
                  paramVarArgs[0] = Integer.MIN_VALUE;
                  paramVarArgs = this.minDate;
                  this.minDate[1] = 0;
                  paramVarArgs[0] = 0;
                  this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                  paramVarArgs = MessagesController.getInstance();
                  l = this.dialog_id;
                  if (this.cacheEndReached[0] != 0) {
                    break label7432;
                  }
                  bool1 = true;
                  paramInt = this.minDate[0];
                  j = this.classGuid;
                  bool2 = ChatObject.isChannel(this.currentChat);
                  k = this.lastLoadIndex;
                  this.lastLoadIndex = (k + 1);
                  paramVarArgs.loadMessages(l, 30, 0, bool1, paramInt, j, 0, 0, bool2, k);
                  this.loading = true;
                }
                for (;;)
                {
                  if ((i == 0) || (this.chatAdapter == null)) {
                    break label7509;
                  }
                  removeUnreadPlane();
                  this.chatAdapter.notifyDataSetChanged();
                  return;
                  paramVarArgs = this.maxMessageId;
                  this.maxMessageId[1] = Integer.MIN_VALUE;
                  paramVarArgs[0] = Integer.MIN_VALUE;
                  paramVarArgs = this.minMessageId;
                  this.minMessageId[1] = Integer.MAX_VALUE;
                  paramVarArgs[0] = Integer.MAX_VALUE;
                  break;
                  bool1 = false;
                  break label7305;
                  if (this.botButtons != null)
                  {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                      this.chatActivityEnterView.setButtons(null, false);
                    }
                  }
                  if ((this.currentEncryptedChat == null) && (this.currentUser != null) && (this.currentUser.bot) && (this.botUser == null))
                  {
                    this.botUser = "";
                    updateBottomOverlay();
                  }
                }
              }
            }
          }
          if (paramInt != NotificationCenter.messageReceivedByServer) {
            break;
          }
          localObject2 = (Integer)paramVarArgs[0];
          localObject1 = (MessageObject)this.messagesDict[0].get(localObject2);
        } while (localObject1 == null);
        localObject3 = (Integer)paramVarArgs[1];
        if ((((Integer)localObject3).equals(localObject2)) || (!this.messagesDict[0].containsKey(localObject3))) {
          break;
        }
        paramVarArgs = (MessageObject)this.messagesDict[0].remove(localObject2);
      } while (paramVarArgs == null);
      paramInt = this.messages.indexOf(paramVarArgs);
      this.messages.remove(paramInt);
      paramVarArgs = (ArrayList)this.messagesByDays.get(paramVarArgs.dateKey);
      paramVarArgs.remove(localObject1);
      if (paramVarArgs.isEmpty())
      {
        this.messagesByDays.remove(((MessageObject)localObject1).dateKey);
        if ((paramInt >= 0) && (paramInt < this.messages.size())) {
          this.messages.remove(paramInt);
        }
      }
    } while (this.chatAdapter == null);
    this.chatAdapter.notifyDataSetChanged();
    return;
    paramVarArgs = (TLRPC.Message)paramVarArgs[2];
    boolean bool2 = false;
    final boolean bool1 = false;
    paramInt = 0;
    int i = 0;
    if (paramVarArgs != null) {
      i = paramInt;
    }
    for (;;)
    {
      try
      {
        if (!((MessageObject)localObject1).isForwarded()) {
          continue;
        }
        i = paramInt;
        if (((MessageObject)localObject1).messageOwner.reply_markup == null)
        {
          i = paramInt;
          if (paramVarArgs.reply_markup != null) {}
        }
        else
        {
          i = paramInt;
          if (((MessageObject)localObject1).messageOwner.message.equals(paramVarArgs.message)) {}
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        paramInt = i;
        continue;
      }
      if (paramInt == 0)
      {
        i = paramInt;
        if (((MessageObject)localObject1).messageOwner.params != null)
        {
          i = paramInt;
          if (((MessageObject)localObject1).messageOwner.params.containsKey("query_id")) {}
        }
        else
        {
          i = paramInt;
          if (paramVarArgs.media == null) {
            continue;
          }
          i = paramInt;
          if (((MessageObject)localObject1).messageOwner.media == null) {
            continue;
          }
          i = paramInt;
          bool2 = paramVarArgs.media.getClass().equals(((MessageObject)localObject1).messageOwner.media.getClass());
          if (bool2) {
            continue;
          }
        }
      }
      bool1 = true;
      ((MessageObject)localObject1).messageOwner = paramVarArgs;
      ((MessageObject)localObject1).generateThumbs(true);
      ((MessageObject)localObject1).setType();
      bool2 = bool1;
      i = paramInt;
      if ((paramVarArgs.media instanceof TLRPC.TL_messageMediaGame))
      {
        ((MessageObject)localObject1).applyNewText();
        i = paramInt;
        bool2 = bool1;
      }
      if (i != 0) {
        ((MessageObject)localObject1).measureInlineBotButtons();
      }
      this.messagesDict[0].remove(localObject2);
      this.messagesDict[0].put(localObject3, localObject1);
      ((MessageObject)localObject1).messageOwner.id = ((Integer)localObject3).intValue();
      ((MessageObject)localObject1).messageOwner.send_state = 0;
      ((MessageObject)localObject1).forceUpdate = bool2;
      paramVarArgs = new ArrayList();
      paramVarArgs.add(localObject1);
      if (this.currentEncryptedChat == null) {
        MessagesQuery.loadReplyMessagesForMessages(paramVarArgs, this.dialog_id);
      }
      if (this.chatAdapter != null) {
        this.chatAdapter.updateRowWithMessageObject((MessageObject)localObject1);
      }
      if ((this.chatLayoutManager != null) && (bool2) && (this.chatLayoutManager.findLastVisibleItemPosition() >= this.messages.size() - 1)) {
        moveScrollToLastMessage();
      }
      NotificationsController.getInstance().playOutChatSound();
      return;
      paramInt = 0;
      continue;
      bool1 = false;
      continue;
      if (paramInt == NotificationCenter.messageReceivedByAck)
      {
        paramVarArgs = (Integer)paramVarArgs[0];
        paramVarArgs = (MessageObject)this.messagesDict[0].get(paramVarArgs);
        if (paramVarArgs == null) {
          break label92;
        }
        paramVarArgs.messageOwner.send_state = 0;
        if (this.chatAdapter == null) {
          break label92;
        }
        this.chatAdapter.updateRowWithMessageObject(paramVarArgs);
        return;
      }
      if (paramInt == NotificationCenter.messageSendError)
      {
        paramVarArgs = (Integer)paramVarArgs[0];
        paramVarArgs = (MessageObject)this.messagesDict[0].get(paramVarArgs);
        if (paramVarArgs == null) {
          break label92;
        }
        paramVarArgs.messageOwner.send_state = 2;
        updateVisibleRows();
        return;
      }
      if (paramInt == NotificationCenter.chatInfoDidLoaded)
      {
        localObject1 = (TLRPC.ChatFull)paramVarArgs[0];
        if ((this.currentChat == null) || (((TLRPC.ChatFull)localObject1).id != this.currentChat.id)) {
          break label92;
        }
        if ((localObject1 instanceof TLRPC.TL_channelFull))
        {
          if (this.currentChat.megagroup)
          {
            j = 0;
            paramInt = 0;
            if (((TLRPC.ChatFull)localObject1).participants != null)
            {
              i = 0;
              for (;;)
              {
                j = paramInt;
                if (i >= ((TLRPC.ChatFull)localObject1).participants.participants.size()) {
                  break;
                }
                paramInt = Math.max(((TLRPC.ChatParticipant)((TLRPC.ChatFull)localObject1).participants.participants.get(i)).date, paramInt);
                i += 1;
              }
            }
            if ((j == 0) || (Math.abs(System.currentTimeMillis() / 1000L - j) > 3600L)) {
              MessagesController.getInstance().loadChannelParticipants(Integer.valueOf(this.currentChat.id));
            }
          }
          if ((((TLRPC.ChatFull)localObject1).participants == null) && (this.info != null)) {
            ((TLRPC.ChatFull)localObject1).participants = this.info.participants;
          }
        }
        this.info = ((TLRPC.ChatFull)localObject1);
        if (this.mentionsAdapter != null) {
          this.mentionsAdapter.setChatInfo(this.info);
        }
        if ((paramVarArgs[3] instanceof MessageObject))
        {
          this.pinnedMessageObject = ((MessageObject)paramVarArgs[3]);
          updatePinnedMessageView(false);
        }
        for (;;)
        {
          if (this.avatarContainer != null)
          {
            this.avatarContainer.updateOnlineCount();
            this.avatarContainer.updateSubtitle();
          }
          if (this.isBroadcast) {
            SendMessagesHelper.getInstance().setCurrentChatInfo(this.info);
          }
          if (!(this.info instanceof TLRPC.TL_chatFull)) {
            break label8718;
          }
          this.hasBotsCommands = false;
          this.botInfo.clear();
          this.botsCount = 0;
          URLSpanBotCommand.enabled = false;
          paramInt = 0;
          while (paramInt < this.info.participants.participants.size())
          {
            paramVarArgs = (TLRPC.ChatParticipant)this.info.participants.participants.get(paramInt);
            paramVarArgs = MessagesController.getInstance().getUser(Integer.valueOf(paramVarArgs.user_id));
            if ((paramVarArgs != null) && (paramVarArgs.bot))
            {
              URLSpanBotCommand.enabled = true;
              this.botsCount += 1;
              BotQuery.loadBotInfo(paramVarArgs.id, true, this.classGuid);
            }
            paramInt += 1;
          }
          updatePinnedMessageView(true);
        }
        if (this.chatListView != null) {
          this.chatListView.invalidateViews();
        }
        for (;;)
        {
          if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
          }
          if (this.mentionsAdapter != null) {
            this.mentionsAdapter.setBotsCount(this.botsCount);
          }
          if ((!ChatObject.isChannel(this.currentChat)) || (this.mergeDialogId != 0L) || (this.info.migrated_from_chat_id == 0)) {
            break;
          }
          this.mergeDialogId = (-this.info.migrated_from_chat_id);
          this.maxMessageId[1] = this.info.migrated_from_max_id;
          if (this.chatAdapter == null) {
            break;
          }
          this.chatAdapter.notifyDataSetChanged();
          return;
          label8718:
          if ((this.info instanceof TLRPC.TL_channelFull))
          {
            this.hasBotsCommands = false;
            this.botInfo.clear();
            this.botsCount = 0;
            if (!this.info.bot_info.isEmpty()) {}
            for (bool1 = true;; bool1 = false)
            {
              URLSpanBotCommand.enabled = bool1;
              this.botsCount = this.info.bot_info.size();
              paramInt = 0;
              while (paramInt < this.info.bot_info.size())
              {
                paramVarArgs = (TLRPC.BotInfo)this.info.bot_info.get(paramInt);
                if ((!paramVarArgs.commands.isEmpty()) && ((!ChatObject.isChannel(this.currentChat)) || ((this.currentChat != null) && (this.currentChat.megagroup)))) {
                  this.hasBotsCommands = true;
                }
                this.botInfo.put(Integer.valueOf(paramVarArgs.user_id), paramVarArgs);
                paramInt += 1;
              }
            }
            if (this.chatListView != null) {
              this.chatListView.invalidateViews();
            }
            if ((this.mentionsAdapter != null) && ((!ChatObject.isChannel(this.currentChat)) || ((this.currentChat != null) && (this.currentChat.megagroup)))) {
              this.mentionsAdapter.setBotInfo(this.botInfo);
            }
          }
        }
      }
      if (paramInt == NotificationCenter.chatInfoCantLoad)
      {
        paramInt = ((Integer)paramVarArgs[0]).intValue();
        if ((this.currentChat == null) || (this.currentChat.id != paramInt)) {
          break label92;
        }
        paramInt = ((Integer)paramVarArgs[1]).intValue();
        if ((getParentActivity() == null) || (this.closeChatDialog != null)) {
          break label92;
        }
        paramVarArgs = new AlertDialog.Builder(getParentActivity());
        paramVarArgs.setTitle(LocaleController.getString("AppName", 2131165299));
        if (paramInt == 0) {
          paramVarArgs.setMessage(LocaleController.getString("ChannelCantOpenPrivate", 2131165418));
        }
        for (;;)
        {
          paramVarArgs.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
          paramVarArgs = paramVarArgs.create();
          this.closeChatDialog = paramVarArgs;
          showDialog(paramVarArgs);
          this.loading = false;
          if (this.progressView != null) {
            this.progressView.setVisibility(4);
          }
          if (this.chatAdapter == null) {
            break;
          }
          this.chatAdapter.notifyDataSetChanged();
          return;
          if (paramInt == 1) {
            paramVarArgs.setMessage(LocaleController.getString("ChannelCantOpenNa", 2131165417));
          } else if (paramInt == 2) {
            paramVarArgs.setMessage(LocaleController.getString("ChannelCantOpenBanned", 2131165416));
          }
        }
      }
      if (paramInt == NotificationCenter.contactsDidLoaded)
      {
        updateContactStatus();
        if (this.avatarContainer == null) {
          break label92;
        }
        this.avatarContainer.updateSubtitle();
        return;
      }
      if (paramInt == NotificationCenter.encryptedChatUpdated)
      {
        paramVarArgs = (TLRPC.EncryptedChat)paramVarArgs[0];
        if ((this.currentEncryptedChat == null) || (paramVarArgs.id != this.currentEncryptedChat.id)) {
          break label92;
        }
        this.currentEncryptedChat = paramVarArgs;
        updateContactStatus();
        updateSecretStatus();
        initStickers();
        if (this.chatActivityEnterView != null)
        {
          paramVarArgs = this.chatActivityEnterView;
          if ((this.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23))
          {
            bool1 = true;
            if ((this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46)) {
              break label9365;
            }
            bool2 = true;
            label9297:
            paramVarArgs.setAllowStickersAndGifs(bool1, bool2);
          }
        }
        else
        {
          if (this.mentionsAdapter == null) {
            break label92;
          }
          paramVarArgs = this.mentionsAdapter;
          if ((this.chatActivityEnterView.isEditingMessage()) || ((this.currentEncryptedChat != null) && (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46))) {
            break label9371;
          }
        }
        label9365:
        label9371:
        for (bool1 = true;; bool1 = false)
        {
          paramVarArgs.setNeedBotContext(bool1);
          return;
          bool1 = false;
          break;
          bool2 = false;
          break label9297;
        }
      }
      if (paramInt == NotificationCenter.messagesReadEncrypted)
      {
        paramInt = ((Integer)paramVarArgs[0]).intValue();
        if ((this.currentEncryptedChat == null) || (this.currentEncryptedChat.id != paramInt)) {
          break label92;
        }
        paramInt = ((Integer)paramVarArgs[1]).intValue();
        paramVarArgs = this.messages.iterator();
        for (;;)
        {
          if (paramVarArgs.hasNext())
          {
            localObject1 = (MessageObject)paramVarArgs.next();
            if (!((MessageObject)localObject1).isOut()) {
              continue;
            }
            if ((!((MessageObject)localObject1).isOut()) || (((MessageObject)localObject1).isUnread())) {}
          }
          else
          {
            updateVisibleRows();
            return;
          }
          if (((MessageObject)localObject1).messageOwner.date - 1 <= paramInt) {
            ((MessageObject)localObject1).setIsRead();
          }
        }
      }
      if ((paramInt == NotificationCenter.audioDidReset) || (paramInt == NotificationCenter.audioPlayStateChanged))
      {
        if (this.chatListView == null) {
          break label92;
        }
        i = this.chatListView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          paramVarArgs = this.chatListView.getChildAt(paramInt);
          if ((paramVarArgs instanceof ChatMessageCell))
          {
            paramVarArgs = (ChatMessageCell)paramVarArgs;
            localObject1 = paramVarArgs.getMessageObject();
            if ((localObject1 != null) && ((((MessageObject)localObject1).isVoice()) || (((MessageObject)localObject1).isMusic()))) {
              paramVarArgs.updateButtonState(false);
            }
          }
          paramInt += 1;
        }
        i = this.mentionListView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          paramVarArgs = this.mentionListView.getChildAt(paramInt);
          if ((paramVarArgs instanceof ContextLinkCell))
          {
            paramVarArgs = (ContextLinkCell)paramVarArgs;
            localObject1 = paramVarArgs.getMessageObject();
            if ((localObject1 != null) && ((((MessageObject)localObject1).isVoice()) || (((MessageObject)localObject1).isMusic()))) {
              paramVarArgs.updateButtonState(false);
            }
          }
          paramInt += 1;
        }
        break label92;
      }
      if (paramInt == NotificationCenter.audioProgressDidChanged)
      {
        localObject1 = (Integer)paramVarArgs[0];
        if (this.chatListView == null) {
          break label92;
        }
        i = this.chatListView.getChildCount();
        paramInt = 0;
        for (;;)
        {
          if (paramInt >= i) {
            break label9801;
          }
          paramVarArgs = this.chatListView.getChildAt(paramInt);
          if ((paramVarArgs instanceof ChatMessageCell))
          {
            paramVarArgs = (ChatMessageCell)paramVarArgs;
            if ((paramVarArgs.getMessageObject() != null) && (paramVarArgs.getMessageObject().getId() == ((Integer)localObject1).intValue()))
            {
              localObject1 = paramVarArgs.getMessageObject();
              localObject2 = MediaController.getInstance().getPlayingMessageObject();
              if (localObject2 == null) {
                break;
              }
              ((MessageObject)localObject1).audioProgress = ((MessageObject)localObject2).audioProgress;
              ((MessageObject)localObject1).audioProgressSec = ((MessageObject)localObject2).audioProgressSec;
              paramVarArgs.updateAudioProgress();
              return;
            }
          }
          paramInt += 1;
        }
        label9801:
        break label92;
      }
      if (paramInt == NotificationCenter.removeAllMessagesFromDialog)
      {
        l = ((Long)paramVarArgs[0]).longValue();
        if (this.dialog_id != l) {
          break label92;
        }
        this.messages.clear();
        this.waitingForLoad.clear();
        this.messagesByDays.clear();
        paramInt = 1;
        if (paramInt >= 0)
        {
          this.messagesDict[paramInt].clear();
          if (this.currentEncryptedChat == null)
          {
            this.maxMessageId[paramInt] = Integer.MAX_VALUE;
            this.minMessageId[paramInt] = Integer.MIN_VALUE;
          }
          for (;;)
          {
            this.maxDate[paramInt] = Integer.MIN_VALUE;
            this.minDate[paramInt] = 0;
            this.selectedMessagesIds[paramInt].clear();
            this.selectedMessagesCanCopyIds[paramInt].clear();
            paramInt -= 1;
            break;
            this.maxMessageId[paramInt] = Integer.MIN_VALUE;
            this.minMessageId[paramInt] = Integer.MAX_VALUE;
          }
        }
        this.cantDeleteMessagesCount = 0;
        this.actionBar.hideActionMode();
        updatePinnedMessageView(true);
        if (this.botButtons != null)
        {
          this.botButtons = null;
          if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setButtons(null, false);
          }
        }
        if ((this.currentEncryptedChat == null) && (this.currentUser != null) && (this.currentUser.bot) && (this.botUser == null))
        {
          this.botUser = "";
          updateBottomOverlay();
        }
        if (((Boolean)paramVarArgs[1]).booleanValue())
        {
          if (this.chatAdapter != null)
          {
            paramVarArgs = this.progressView;
            if (this.chatAdapter.botInfoRow != -1) {
              break label10126;
            }
          }
          label10126:
          for (paramInt = 0;; paramInt = 4)
          {
            paramVarArgs.setVisibility(paramInt);
            this.chatListView.setEmptyView(null);
            paramInt = 0;
            while (paramInt < 2)
            {
              this.endReached[paramInt] = false;
              this.cacheEndReached[paramInt] = false;
              this.forwardEndReached[paramInt] = true;
              paramInt += 1;
            }
          }
          this.first = true;
          this.firstLoading = true;
          this.loading = true;
          this.startLoadFromMessageId = 0;
          this.needSelectFromMessageId = false;
          this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
          paramVarArgs = MessagesController.getInstance();
          l = this.dialog_id;
          if (AndroidUtilities.isTablet())
          {
            paramInt = 30;
            i = this.classGuid;
            bool1 = ChatObject.isChannel(this.currentChat);
            j = this.lastLoadIndex;
            this.lastLoadIndex = (j + 1);
            paramVarArgs.loadMessages(l, paramInt, 0, true, 0, i, 2, 0, bool1, j);
          }
        }
        for (;;)
        {
          if (this.chatAdapter == null) {
            break label10283;
          }
          this.chatAdapter.notifyDataSetChanged();
          return;
          paramInt = 20;
          break;
          if (this.progressView != null)
          {
            this.progressView.setVisibility(4);
            this.chatListView.setEmptyView(this.emptyViewContainer);
          }
        }
        label10283:
        break label92;
      }
      if (paramInt == NotificationCenter.screenshotTook)
      {
        updateInformationForScreenshotDetector();
        return;
      }
      if (paramInt == NotificationCenter.blockedUsersDidLoaded)
      {
        if (this.currentUser == null) {
          break label92;
        }
        bool1 = this.userBlocked;
        this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.currentUser.id));
        if (bool1 == this.userBlocked) {
          break label92;
        }
        updateBottomOverlay();
        return;
      }
      if (paramInt == NotificationCenter.FileNewChunkAvailable)
      {
        localObject1 = (MessageObject)paramVarArgs[0];
        l = ((Long)paramVarArgs[2]).longValue();
        if ((l == 0L) || (this.dialog_id != ((MessageObject)localObject1).getDialogId())) {
          break label92;
        }
        paramVarArgs = (MessageObject)this.messagesDict[0].get(Integer.valueOf(((MessageObject)localObject1).getId()));
        if (paramVarArgs == null) {
          break label92;
        }
        paramVarArgs.messageOwner.media.document.size = ((int)l);
        updateVisibleRows();
        return;
      }
      if (paramInt == NotificationCenter.didCreatedNewDeleteTask)
      {
        paramVarArgs = (SparseArray)paramVarArgs[0];
        i = 0;
        paramInt = 0;
        while (paramInt < paramVarArgs.size())
        {
          j = paramVarArgs.keyAt(paramInt);
          localObject1 = ((ArrayList)paramVarArgs.get(j)).iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (Integer)((Iterator)localObject1).next();
            localObject2 = (MessageObject)this.messagesDict[0].get(localObject2);
            if (localObject2 != null)
            {
              ((MessageObject)localObject2).messageOwner.destroyTime = j;
              i = 1;
            }
          }
          paramInt += 1;
        }
        if (i == 0) {
          break label92;
        }
        updateVisibleRows();
        return;
      }
      if (paramInt == NotificationCenter.audioDidStarted)
      {
        sendSecretMessageRead((MessageObject)paramVarArgs[0]);
        if (this.chatListView == null) {
          break label92;
        }
        i = this.chatListView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          paramVarArgs = this.chatListView.getChildAt(paramInt);
          if ((paramVarArgs instanceof ChatMessageCell))
          {
            paramVarArgs = (ChatMessageCell)paramVarArgs;
            localObject1 = paramVarArgs.getMessageObject();
            if ((localObject1 != null) && ((((MessageObject)localObject1).isVoice()) || (((MessageObject)localObject1).isMusic()))) {
              paramVarArgs.updateButtonState(false);
            }
          }
          paramInt += 1;
        }
        break label92;
      }
      if (paramInt == NotificationCenter.updateMessageMedia)
      {
        paramVarArgs = (MessageObject)paramVarArgs[0];
        localObject1 = (MessageObject)this.messagesDict[0].get(Integer.valueOf(paramVarArgs.getId()));
        if (localObject1 != null)
        {
          ((MessageObject)localObject1).messageOwner.media = paramVarArgs.messageOwner.media;
          ((MessageObject)localObject1).messageOwner.attachPath = paramVarArgs.messageOwner.attachPath;
          ((MessageObject)localObject1).generateThumbs(false);
        }
        updateVisibleRows();
        return;
      }
      if (paramInt == NotificationCenter.replaceMessagesObjects)
      {
        l = ((Long)paramVarArgs[0]).longValue();
        if ((l != this.dialog_id) && (l != this.mergeDialogId)) {
          break label92;
        }
        if (l == this.dialog_id)
        {
          j = 0;
          m = 0;
          paramInt = 0;
          paramVarArgs = (ArrayList)paramVarArgs[1];
          k = 0;
          label10814:
          if (k >= paramVarArgs.size()) {
            break label11310;
          }
          localObject1 = (MessageObject)paramVarArgs.get(k);
          localObject2 = (MessageObject)this.messagesDict[j].get(Integer.valueOf(((MessageObject)localObject1).getId()));
          if ((this.pinnedMessageObject != null) && (this.pinnedMessageObject.getId() == ((MessageObject)localObject1).getId()))
          {
            this.pinnedMessageObject = ((MessageObject)localObject1);
            updatePinnedMessageView(true);
          }
          n = m;
          i = paramInt;
          if (localObject2 != null)
          {
            if (((MessageObject)localObject1).type < 0) {
              break label11177;
            }
            i = paramInt;
            if (paramInt == 0)
            {
              i = paramInt;
              if ((((MessageObject)localObject1).messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
                i = 1;
              }
            }
            if (((MessageObject)localObject2).replyMessageObject != null)
            {
              ((MessageObject)localObject1).replyMessageObject = ((MessageObject)localObject2).replyMessageObject;
              if ((((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageActionGameScore)) {
                ((MessageObject)localObject1).generateGameMessageText(null);
              }
            }
            ((MessageObject)localObject1).messageOwner.attachPath = ((MessageObject)localObject2).messageOwner.attachPath;
            ((MessageObject)localObject1).attachPathExists = ((MessageObject)localObject2).attachPathExists;
            ((MessageObject)localObject1).mediaExists = ((MessageObject)localObject2).mediaExists;
            this.messagesDict[j].put(Integer.valueOf(((MessageObject)localObject2).getId()), localObject1);
            paramInt = i;
            label11030:
            i1 = this.messages.indexOf(localObject2);
            n = m;
            i = paramInt;
            if (i1 >= 0)
            {
              localObject3 = (ArrayList)this.messagesByDays.get(((MessageObject)localObject2).dateKey);
              i = -1;
              if (localObject3 != null) {
                i = ((ArrayList)localObject3).indexOf(localObject2);
              }
              if (((MessageObject)localObject1).type < 0) {
                break label11199;
              }
              this.messages.set(i1, localObject1);
              if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemChanged(this.chatAdapter.messagesStartRow + this.messages.size() - i1 - 1);
              }
              if (i >= 0) {
                ((ArrayList)localObject3).set(i, localObject1);
              }
            }
          }
        }
        for (;;)
        {
          n = 1;
          i = paramInt;
          k += 1;
          m = n;
          paramInt = i;
          break label10814;
          j = 1;
          break;
          label11177:
          this.messagesDict[j].remove(Integer.valueOf(((MessageObject)localObject2).getId()));
          break label11030;
          label11199:
          this.messages.remove(i1);
          if (this.chatAdapter != null) {
            this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + this.messages.size() - i1 - 1);
          }
          if (i >= 0)
          {
            ((ArrayList)localObject3).remove(i);
            if (((ArrayList)localObject3).isEmpty())
            {
              this.messagesByDays.remove(((MessageObject)localObject2).dateKey);
              this.messages.remove(i1);
              this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + this.messages.size());
            }
          }
        }
        label11310:
        if ((m == 0) || (this.chatLayoutManager == null) || (paramInt == 0)) {
          break label92;
        }
        i = this.chatLayoutManager.findLastVisibleItemPosition();
        j = this.messages.size();
        if (this.chatAdapter.isBot) {}
        for (paramInt = 2; i >= j - paramInt; paramInt = 1)
        {
          moveScrollToLastMessage();
          return;
        }
        break label92;
      }
      if (paramInt == NotificationCenter.notificationsSettingsUpdated)
      {
        updateTitleIcons();
        if (!ChatObject.isChannel(this.currentChat)) {
          break label92;
        }
        updateBottomOverlay();
        return;
      }
      if (paramInt == NotificationCenter.didLoadedReplyMessages)
      {
        if (((Long)paramVarArgs[0]).longValue() != this.dialog_id) {
          break label92;
        }
        updateVisibleRows();
        return;
      }
      if (paramInt == NotificationCenter.didLoadedPinnedMessage)
      {
        paramVarArgs = (MessageObject)paramVarArgs[0];
        if ((paramVarArgs.getDialogId() != this.dialog_id) || (this.info == null) || (this.info.pinned_msg_id != paramVarArgs.getId())) {
          break label92;
        }
        this.pinnedMessageObject = paramVarArgs;
        this.loadingPinnedMessage = 0;
        updatePinnedMessageView(true);
        return;
      }
      if (paramInt == NotificationCenter.didReceivedWebpages)
      {
        paramVarArgs = (ArrayList)paramVarArgs[0];
        i = 0;
        paramInt = 0;
        while (paramInt < paramVarArgs.size())
        {
          localObject1 = (TLRPC.Message)paramVarArgs.get(paramInt);
          l = MessageObject.getDialogId((TLRPC.Message)localObject1);
          if ((l != this.dialog_id) && (l != this.mergeDialogId))
          {
            paramInt += 1;
          }
          else
          {
            localObject2 = this.messagesDict;
            if (l == this.dialog_id) {}
            for (j = 0;; j = 1)
            {
              localObject2 = (MessageObject)localObject2[j].get(Integer.valueOf(((TLRPC.Message)localObject1).id));
              if (localObject2 == null) {
                break;
              }
              ((MessageObject)localObject2).messageOwner.media = new TLRPC.TL_messageMediaWebPage();
              ((MessageObject)localObject2).messageOwner.media.webpage = ((TLRPC.Message)localObject1).media.webpage;
              ((MessageObject)localObject2).generateThumbs(true);
              i = 1;
              break;
            }
          }
        }
        if (i == 0) {
          break label92;
        }
        updateVisibleRows();
        if ((this.chatLayoutManager == null) || (this.chatLayoutManager.findLastVisibleItemPosition() < this.messages.size() - 1)) {
          break label92;
        }
        moveScrollToLastMessage();
        return;
      }
      if (paramInt == NotificationCenter.didReceivedWebpagesInUpdates)
      {
        if (this.foundWebPage == null) {
          break label92;
        }
        paramVarArgs = ((HashMap)paramVarArgs[0]).values().iterator();
        label11725:
        if (!paramVarArgs.hasNext()) {
          break label92;
        }
        localObject1 = (TLRPC.WebPage)paramVarArgs.next();
        if (((TLRPC.WebPage)localObject1).id != this.foundWebPage.id) {
          break;
        }
        if (!(localObject1 instanceof TLRPC.TL_webPageEmpty)) {}
        for (bool1 = true;; bool1 = false)
        {
          showReplyPanel(bool1, null, null, (TLRPC.WebPage)localObject1, false, true);
          return;
        }
      }
      if (paramInt == NotificationCenter.messagesReadContent)
      {
        paramVarArgs = (ArrayList)paramVarArgs[0];
        i = 0;
        paramInt = 0;
        if (paramInt < paramVarArgs.size())
        {
          l = ((Long)paramVarArgs.get(paramInt)).longValue();
          localObject1 = this.messagesDict;
          if (this.mergeDialogId == 0L) {}
          for (j = 0;; j = 1)
          {
            localObject1 = (MessageObject)localObject1[j].get(Integer.valueOf((int)l));
            if (localObject1 != null)
            {
              ((MessageObject)localObject1).setContentIsRead();
              i = 1;
            }
            paramInt += 1;
            break;
          }
        }
        if (i == 0) {
          break label92;
        }
        updateVisibleRows();
        return;
      }
      if (paramInt == NotificationCenter.botInfoDidLoaded)
      {
        paramInt = ((Integer)paramVarArgs[1]).intValue();
        if (this.classGuid != paramInt) {
          break label92;
        }
        paramVarArgs = (TLRPC.BotInfo)paramVarArgs[0];
        if (this.currentEncryptedChat == null)
        {
          if ((!paramVarArgs.commands.isEmpty()) && (!ChatObject.isChannel(this.currentChat))) {
            this.hasBotsCommands = true;
          }
          this.botInfo.put(Integer.valueOf(paramVarArgs.user_id), paramVarArgs);
          if (this.chatAdapter != null) {
            this.chatAdapter.notifyItemChanged(0);
          }
          if ((this.mentionsAdapter != null) && ((!ChatObject.isChannel(this.currentChat)) || ((this.currentChat != null) && (this.currentChat.megagroup)))) {
            this.mentionsAdapter.setBotInfo(this.botInfo);
          }
          if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
          }
        }
        updateBotButtons();
        return;
      }
      if (paramInt == NotificationCenter.botKeyboardDidLoaded)
      {
        if (this.dialog_id != ((Long)paramVarArgs[1]).longValue()) {
          break label92;
        }
        paramVarArgs = (TLRPC.Message)paramVarArgs[0];
        if ((paramVarArgs != null) && (!this.userBlocked))
        {
          this.botButtons = new MessageObject(paramVarArgs, null, false);
          if (this.chatActivityEnterView == null) {
            break label92;
          }
          if ((this.botButtons.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardForceReply))
          {
            if ((ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("answered_" + this.dialog_id, 0) == this.botButtons.getId()) || ((this.replyingMessageObject != null) && (this.chatActivityEnterView.getFieldText() != null))) {
              break label92;
            }
            this.botReplyButtons = this.botButtons;
            this.chatActivityEnterView.setButtons(this.botButtons);
            showReplyPanel(true, this.botButtons, null, null, false, true);
            return;
          }
          if ((this.replyingMessageObject != null) && (this.botReplyButtons == this.replyingMessageObject))
          {
            this.botReplyButtons = null;
            showReplyPanel(false, null, null, null, false, true);
          }
          this.chatActivityEnterView.setButtons(this.botButtons);
          return;
        }
        this.botButtons = null;
        if (this.chatActivityEnterView == null) {
          break label92;
        }
        if ((this.replyingMessageObject != null) && (this.botReplyButtons == this.replyingMessageObject))
        {
          this.botReplyButtons = null;
          showReplyPanel(false, null, null, null, false, true);
        }
        this.chatActivityEnterView.setButtons(this.botButtons);
        return;
      }
      if (paramInt == NotificationCenter.chatSearchResultsAvailable)
      {
        if (this.classGuid != ((Integer)paramVarArgs[0]).intValue()) {
          break label92;
        }
        i = ((Integer)paramVarArgs[1]).intValue();
        l = ((Long)paramVarArgs[3]).longValue();
        if (i != 0) {
          if (l != this.dialog_id) {
            break label12448;
          }
        }
        label12448:
        for (paramInt = 0;; paramInt = 1)
        {
          scrollToMessageId(i, 0, true, paramInt);
          updateSearchButtons(((Integer)paramVarArgs[2]).intValue(), ((Integer)paramVarArgs[4]).intValue(), ((Integer)paramVarArgs[5]).intValue());
          return;
        }
      }
      if (paramInt == NotificationCenter.didUpdatedMessagesViews)
      {
        paramVarArgs = (SparseIntArray)((SparseArray)paramVarArgs[0]).get((int)this.dialog_id);
        if (paramVarArgs == null) {
          break label92;
        }
        i = 0;
        paramInt = 0;
        while (paramInt < paramVarArgs.size())
        {
          k = paramVarArgs.keyAt(paramInt);
          localObject1 = (MessageObject)this.messagesDict[0].get(Integer.valueOf(k));
          j = i;
          if (localObject1 != null)
          {
            k = paramVarArgs.get(k);
            j = i;
            if (k > ((MessageObject)localObject1).messageOwner.views)
            {
              ((MessageObject)localObject1).messageOwner.views = k;
              j = 1;
            }
          }
          paramInt += 1;
          i = j;
        }
        if (i == 0) {
          break label92;
        }
        updateVisibleRows();
        return;
      }
      if (paramInt == NotificationCenter.peerSettingsDidLoaded)
      {
        if (((Long)paramVarArgs[0]).longValue() != this.dialog_id) {
          break label92;
        }
        updateSpamView();
        return;
      }
      if ((paramInt != NotificationCenter.newDraftReceived) || (((Long)paramVarArgs[0]).longValue() != this.dialog_id)) {
        break label92;
      }
      applyDraftMaybe(true);
      return;
      paramInt = 1;
    }
  }
  
  public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
  {
    ArrayList localArrayList;
    int i;
    Object localObject;
    if ((this.dialog_id != 0L) && ((this.forwaringMessage != null) || (!this.selectedMessagesIds[0].isEmpty()) || (!this.selectedMessagesIds[1].isEmpty())))
    {
      localArrayList = new ArrayList();
      if (this.forwaringMessage == null) {
        break label135;
      }
      localArrayList.add(this.forwaringMessage);
      this.forwaringMessage = null;
      if (paramLong == this.dialog_id) {
        break label361;
      }
      i = (int)paramLong;
      if (i == 0) {
        break label356;
      }
      localObject = new Bundle();
      ((Bundle)localObject).putBoolean("scrollToTopOnResume", this.scrollToTopOnResume);
      if (i <= 0) {
        break label288;
      }
      ((Bundle)localObject).putInt("user_id", i);
      label125:
      if (MessagesController.checkCanOpenChat((Bundle)localObject, paramDialogsActivity)) {
        break label307;
      }
    }
    label135:
    label288:
    label307:
    do
    {
      return;
      i = 1;
      while (i >= 0)
      {
        localObject = new ArrayList(this.selectedMessagesIds[i].keySet());
        Collections.sort((List)localObject);
        int j = 0;
        while (j < ((ArrayList)localObject).size())
        {
          Integer localInteger = (Integer)((ArrayList)localObject).get(j);
          MessageObject localMessageObject = (MessageObject)this.selectedMessagesIds[i].get(localInteger);
          if ((localMessageObject != null) && (localInteger.intValue() > 0)) {
            localArrayList.add(localMessageObject);
          }
          j += 1;
        }
        this.selectedMessagesCanCopyIds[i].clear();
        this.selectedMessagesIds[i].clear();
        i -= 1;
      }
      this.cantDeleteMessagesCount = 0;
      this.actionBar.hideActionMode();
      updatePinnedMessageView(true);
      break;
      if (i >= 0) {
        break label125;
      }
      ((Bundle)localObject).putInt("chat_id", -i);
      break label125;
      localObject = new ChatActivity((Bundle)localObject);
      if (!presentFragment((BaseFragment)localObject, true)) {
        break label351;
      }
      ((ChatActivity)localObject).showReplyPanel(true, null, localArrayList, null, false, false);
    } while (AndroidUtilities.isTablet());
    removeSelfFromStack();
    return;
    label351:
    paramDialogsActivity.finishFragment();
    return;
    label356:
    paramDialogsActivity.finishFragment();
    return;
    label361:
    paramDialogsActivity.finishFragment();
    moveScrollToLastMessage();
    showReplyPanel(true, null, localArrayList, null, false, AndroidUtilities.isTablet());
    if (AndroidUtilities.isTablet())
    {
      this.actionBar.hideActionMode();
      updatePinnedMessageView(true);
    }
    updateVisibleRows();
  }
  
  public void dismissCurrentDialig()
  {
    if ((this.chatAttachAlert != null) && (this.visibleDialog == this.chatAttachAlert))
    {
      this.chatAttachAlert.closeCamera(false);
      this.chatAttachAlert.dismissInternal();
      this.chatAttachAlert.hideCamera(true);
      return;
    }
    super.dismissCurrentDialig();
  }
  
  public boolean dismissDialogOnPause(Dialog paramDialog)
  {
    return (paramDialog != this.chatAttachAlert) && (super.dismissDialogOnPause(paramDialog));
  }
  
  public TLRPC.Chat getCurrentChat()
  {
    return this.currentChat;
  }
  
  public TLRPC.ChatFull getCurrentChatInfo()
  {
    return this.info;
  }
  
  public TLRPC.EncryptedChat getCurrentEncryptedChat()
  {
    return this.currentEncryptedChat;
  }
  
  public TLRPC.User getCurrentUser()
  {
    return this.currentUser;
  }
  
  public long getDialogId()
  {
    return this.dialog_id;
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    int j = this.chatListView.getChildCount();
    paramInt = 0;
    while (paramInt < j)
    {
      Object localObject2 = null;
      View localView = this.chatListView.getChildAt(paramInt);
      Object localObject1;
      Object localObject3;
      MessageObject localMessageObject;
      if ((localView instanceof ChatMessageCell))
      {
        localObject1 = localObject2;
        if (paramMessageObject != null)
        {
          localObject3 = (ChatMessageCell)localView;
          localMessageObject = ((ChatMessageCell)localObject3).getMessageObject();
          localObject1 = localObject2;
          if (localMessageObject != null)
          {
            localObject1 = localObject2;
            if (localMessageObject.getId() == paramMessageObject.getId()) {
              localObject1 = ((ChatMessageCell)localObject3).getPhotoImage();
            }
          }
        }
      }
      while (localObject1 != null)
      {
        paramMessageObject = new int[2];
        localView.getLocationInWindow(paramMessageObject);
        paramFileLocation = new PhotoViewer.PlaceProviderObject();
        paramFileLocation.viewX = paramMessageObject[0];
        paramFileLocation.viewY = (paramMessageObject[1] - AndroidUtilities.statusBarHeight);
        paramFileLocation.parentView = this.chatListView;
        paramFileLocation.imageReceiver = ((ImageReceiver)localObject1);
        paramFileLocation.thumb = ((ImageReceiver)localObject1).getBitmap();
        paramFileLocation.radius = ((ImageReceiver)localObject1).getRoundRadius();
        if (((localView instanceof ChatActionCell)) && (this.currentChat != null)) {
          paramFileLocation.dialogId = (-this.currentChat.id);
        }
        if (((this.pinnedMessageView != null) && (this.pinnedMessageView.getTag() == null)) || ((this.reportSpamView != null) && (this.reportSpamView.getTag() == null))) {
          paramFileLocation.clipTopAddition = AndroidUtilities.dp(48.0F);
        }
        return paramFileLocation;
        localObject1 = localObject2;
        if ((localView instanceof ChatActionCell))
        {
          localObject3 = (ChatActionCell)localView;
          localMessageObject = ((ChatActionCell)localObject3).getMessageObject();
          localObject1 = localObject2;
          if (localMessageObject != null) {
            if (paramMessageObject != null)
            {
              localObject1 = localObject2;
              if (localMessageObject.getId() == paramMessageObject.getId()) {
                localObject1 = ((ChatActionCell)localObject3).getPhotoImage();
              }
            }
            else
            {
              localObject1 = localObject2;
              if (paramFileLocation != null)
              {
                localObject1 = localObject2;
                if (localMessageObject.photoThumbs != null)
                {
                  int i = 0;
                  for (;;)
                  {
                    localObject1 = localObject2;
                    if (i >= localMessageObject.photoThumbs.size()) {
                      break;
                    }
                    localObject1 = (TLRPC.PhotoSize)localMessageObject.photoThumbs.get(i);
                    if ((((TLRPC.PhotoSize)localObject1).location.volume_id == paramFileLocation.volume_id) && (((TLRPC.PhotoSize)localObject1).location.local_id == paramFileLocation.local_id))
                    {
                      localObject1 = ((ChatActionCell)localObject3).getPhotoImage();
                      break;
                    }
                    i += 1;
                  }
                }
              }
            }
          }
        }
      }
      paramInt += 1;
    }
    return null;
  }
  
  public int getSelectedCount()
  {
    return 0;
  }
  
  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    return null;
  }
  
  public boolean isPhotoChecked(int paramInt)
  {
    return false;
  }
  
  public boolean isSecretChat()
  {
    return this.currentEncryptedChat != null;
  }
  
  public boolean needDelayOpenAnimation()
  {
    return this.firstLoading;
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, final Intent paramIntent)
  {
    if (paramInt2 == -1)
    {
      if (paramInt1 == 0)
      {
        PhotoViewer.getInstance().setParentActivity(getParentActivity());
        paramIntent = new ArrayList();
        paramInt2 = 0;
      }
    }
    else
    {
      try
      {
        int i = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
        paramInt1 = paramInt2;
        switch (i)
        {
        default: 
          paramInt1 = paramInt2;
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException1);
          paramInt1 = paramInt2;
        }
      }
      paramIntent.add(new MediaController.PhotoEntry(0, 0, 0L, this.currentPicturePath, paramInt1, false));
      PhotoViewer.getInstance().openPhotoForSelect(paramIntent, 0, 2, new PhotoViewer.EmptyPhotoViewerProvider()
      {
        public void sendButtonPressed(int paramAnonymousInt)
        {
          ChatActivity.this.sendMedia((MediaController.PhotoEntry)paramIntent.get(0), false);
        }
      }, this);
      AndroidUtilities.addMediaToGallery(this.currentPicturePath);
      this.currentPicturePath = null;
    }
    for (;;)
    {
      return;
      paramInt1 = 90;
      break;
      paramInt1 = 180;
      break;
      paramInt1 = 270;
      break;
      if (paramInt1 == 1)
      {
        if ((paramIntent == null) || (paramIntent.getData() == null))
        {
          showAttachmentError();
          return;
        }
        Object localObject1 = paramIntent.getData();
        if (((Uri)localObject1).toString().contains("video"))
        {
          paramIntent = null;
          try
          {
            localObject1 = AndroidUtilities.getPath((Uri)localObject1);
            paramIntent = (Intent)localObject1;
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException2);
              continue;
              openVideoEditor(paramIntent, false, false);
              continue;
              SendMessagesHelper.prepareSendingVideo(paramIntent, 0L, 0L, 0, 0, null, this.dialog_id, this.replyingMessageObject);
            }
          }
          if (paramIntent == null) {
            showAttachmentError();
          }
          if (Build.VERSION.SDK_INT >= 16) {
            if (this.paused) {
              this.startVideoEdit = paramIntent;
            }
          }
        }
        for (;;)
        {
          showReplyPanel(false, null, null, null, false, true);
          DraftQuery.cleanDraft(this.dialog_id, true);
          return;
          SendMessagesHelper.prepareSendingPhoto(null, localException2, this.dialog_id, this.replyingMessageObject, null, null);
        }
      }
      Object localObject5;
      Object localObject2;
      if (paramInt1 == 2)
      {
        localObject5 = null;
        FileLog.d("tmessages", "pic path " + this.currentPicturePath);
        localObject2 = paramIntent;
        if (paramIntent != null)
        {
          localObject2 = paramIntent;
          if (this.currentPicturePath != null)
          {
            localObject2 = paramIntent;
            if (new File(this.currentPicturePath).exists()) {
              localObject2 = null;
            }
          }
        }
        paramIntent = (Intent)localObject5;
        if (localObject2 != null)
        {
          paramIntent = ((Intent)localObject2).getData();
          if (paramIntent == null) {
            break label607;
          }
          FileLog.d("tmessages", "video record uri " + paramIntent.toString());
          localObject2 = AndroidUtilities.getPath(paramIntent);
          FileLog.d("tmessages", "resolved path = " + (String)localObject2);
          paramIntent = (Intent)localObject2;
          if (new File((String)localObject2).exists()) {}
        }
        label607:
        for (paramIntent = this.currentPicturePath;; paramIntent = this.currentPicturePath)
        {
          AndroidUtilities.addMediaToGallery(this.currentPicturePath);
          this.currentPicturePath = null;
          localObject2 = paramIntent;
          if (paramIntent == null)
          {
            localObject2 = paramIntent;
            if (this.currentPicturePath != null)
            {
              if (new File(this.currentPicturePath).exists()) {
                paramIntent = this.currentPicturePath;
              }
              this.currentPicturePath = null;
              localObject2 = paramIntent;
            }
          }
          if (Build.VERSION.SDK_INT < 16) {
            break label625;
          }
          if (!this.paused) {
            break;
          }
          this.startVideoEdit = ((String)localObject2);
          return;
        }
        openVideoEditor((String)localObject2, false, false);
        return;
        label625:
        SendMessagesHelper.prepareSendingVideo((String)localObject2, 0L, 0L, 0, 0, null, this.dialog_id, this.replyingMessageObject);
        showReplyPanel(false, null, null, null, false, true);
        DraftQuery.cleanDraft(this.dialog_id, true);
        return;
      }
      Object localObject6;
      if (paramInt1 == 21)
      {
        if ((paramIntent == null) || (paramIntent.getData() == null))
        {
          showAttachmentError();
          return;
        }
        localObject5 = paramIntent.getData();
        localObject6 = ((Uri)localObject5).toString();
        localObject2 = localObject5;
        if (((String)localObject6).contains("com.google.android.apps.photos.contentprovider")) {}
        try
        {
          localObject6 = localObject6.split("/1/")[1];
          paramInt1 = ((String)localObject6).indexOf("/ACTUAL");
          localObject2 = localObject5;
          if (paramInt1 != -1) {
            localObject2 = Uri.parse(URLDecoder.decode(((String)localObject6).substring(0, paramInt1), "UTF-8"));
          }
        }
        catch (Exception localException3)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException3);
            localObject3 = localObject5;
          }
          SendMessagesHelper.prepareSendingDocument((String)localObject5, (String)localObject6, null, null, this.dialog_id, this.replyingMessageObject);
          showReplyPanel(false, null, null, null, false, true);
          DraftQuery.cleanDraft(this.dialog_id, true);
          return;
        }
        localObject2 = AndroidUtilities.getPath((Uri)localObject2);
        localObject6 = localObject2;
        localObject5 = localObject2;
        if (localObject2 == null)
        {
          localObject6 = paramIntent.toString();
          localObject5 = MediaController.copyFileToCache(paramIntent.getData(), "file");
        }
        if (localObject5 == null)
        {
          showAttachmentError();
          return;
        }
      }
      if (paramInt1 != 31) {
        continue;
      }
      if ((paramIntent == null) || (paramIntent.getData() == null))
      {
        showAttachmentError();
        return;
      }
      Object localObject3 = paramIntent.getData();
      paramIntent = null;
      try
      {
        localObject3 = getParentActivity().getContentResolver().query((Uri)localObject3, new String[] { "display_name", "data1" }, null, null, null);
        if (localObject3 != null)
        {
          paramInt1 = 0;
          for (;;)
          {
            paramIntent = (Intent)localObject3;
            if (!((Cursor)localObject3).moveToNext()) {
              break;
            }
            paramInt1 = 1;
            paramIntent = (Intent)localObject3;
            localObject5 = ((Cursor)localObject3).getString(0);
            paramIntent = (Intent)localObject3;
            localObject6 = ((Cursor)localObject3).getString(1);
            paramIntent = (Intent)localObject3;
            TLRPC.User localUser = new TLRPC.User();
            paramIntent = (Intent)localObject3;
            localUser.first_name = ((String)localObject5);
            paramIntent = (Intent)localObject3;
            localUser.last_name = "";
            paramIntent = (Intent)localObject3;
            localUser.phone = ((String)localObject6);
            paramIntent = (Intent)localObject3;
            SendMessagesHelper.getInstance().sendMessage(localUser, this.dialog_id, this.replyingMessageObject, null, null);
          }
        }
        try
        {
          if (!paramIntent.isClosed()) {
            paramIntent.close();
          }
          throw ((Throwable)localObject4);
          if (paramInt1 != 0)
          {
            paramIntent = (Intent)localObject4;
            showReplyPanel(false, null, null, null, false, true);
            paramIntent = (Intent)localObject4;
            DraftQuery.cleanDraft(this.dialog_id, true);
          }
          if (localObject4 == null) {
            continue;
          }
          try
          {
            if (((Cursor)localObject4).isClosed()) {
              continue;
            }
            ((Cursor)localObject4).close();
            return;
          }
          catch (Exception paramIntent)
          {
            FileLog.e("tmessages", paramIntent);
            return;
          }
        }
        catch (Exception paramIntent)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramIntent);
          }
        }
      }
      finally
      {
        if (paramIntent == null) {}
      }
    }
  }
  
  public boolean onBackPressed()
  {
    if (this.actionBar.isActionModeShowed())
    {
      int i = 1;
      while (i >= 0)
      {
        this.selectedMessagesIds[i].clear();
        this.selectedMessagesCanCopyIds[i].clear();
        i -= 1;
      }
      this.chatActivityEnterView.setEditingMessageObject(null, false);
      this.actionBar.hideActionMode();
      updatePinnedMessageView(true);
      this.cantDeleteMessagesCount = 0;
      updateVisibleRows();
      return false;
    }
    if (this.chatActivityEnterView.isPopupShowing())
    {
      this.chatActivityEnterView.hidePopup(true);
      return false;
    }
    return true;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    fixLayout();
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    if ((this.closeChatDialog != null) && (paramDialog == this.closeChatDialog))
    {
      MessagesController.getInstance().deleteDialog(this.dialog_id, 0);
      if ((this.parentLayout != null) && (!this.parentLayout.fragmentsStack.isEmpty()) && (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) != this))
      {
        paramDialog = (BaseFragment)this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);
        removeSelfFromStack();
        paramDialog.finishFragment();
      }
    }
    else
    {
      return;
    }
    finishFragment();
  }
  
  /* Error */
  public boolean onFragmentCreate()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   4: ldc_w 3961
    //   7: iconst_0
    //   8: invokevirtual 4519	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   11: istore_1
    //   12: aload_0
    //   13: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   16: ldc_w 2586
    //   19: iconst_0
    //   20: invokevirtual 4519	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   23: istore_2
    //   24: aload_0
    //   25: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   28: ldc_w 4521
    //   31: iconst_0
    //   32: invokevirtual 4519	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   35: istore_3
    //   36: aload_0
    //   37: aload_0
    //   38: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   41: ldc_w 4523
    //   44: lconst_0
    //   45: invokevirtual 4527	android/os/Bundle:getLong	(Ljava/lang/String;J)J
    //   48: putfield 797	org/telegram/ui/ChatActivity:inlineReturn	J
    //   51: aload_0
    //   52: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   55: ldc_w 4529
    //   58: invokevirtual 4531	android/os/Bundle:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   61: astore 9
    //   63: aload_0
    //   64: aload_0
    //   65: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   68: ldc_w 4533
    //   71: iconst_0
    //   72: invokevirtual 4519	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   75: putfield 1717	org/telegram/ui/ChatActivity:startLoadFromMessageId	I
    //   78: aload_0
    //   79: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   82: ldc_w 4535
    //   85: iconst_0
    //   86: invokevirtual 4519	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   89: istore 4
    //   91: aload_0
    //   92: aload_0
    //   93: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   96: ldc_w 4257
    //   99: iconst_0
    //   100: invokevirtual 1472	android/os/Bundle:getBoolean	(Ljava/lang/String;Z)Z
    //   103: putfield 686	org/telegram/ui/ChatActivity:scrollToTopOnResume	Z
    //   106: iload_1
    //   107: ifeq +879 -> 986
    //   110: aload_0
    //   111: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   114: iload_1
    //   115: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   118: invokevirtual 1552	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   121: putfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   124: aload_0
    //   125: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   128: ifnonnull +56 -> 184
    //   131: new 4537	java/util/concurrent/Semaphore
    //   134: dup
    //   135: iconst_0
    //   136: invokespecial 4539	java/util/concurrent/Semaphore:<init>	(I)V
    //   139: astore 9
    //   141: invokestatic 2648	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   144: invokevirtual 4543	org/telegram/messenger/MessagesStorage:getStorageQueue	()Lorg/telegram/messenger/DispatchQueue;
    //   147: new 96	org/telegram/ui/ChatActivity$4
    //   150: dup
    //   151: aload_0
    //   152: iload_1
    //   153: aload 9
    //   155: invokespecial 4546	org/telegram/ui/ChatActivity$4:<init>	(Lorg/telegram/ui/ChatActivity;ILjava/util/concurrent/Semaphore;)V
    //   158: invokevirtual 2681	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   161: aload 9
    //   163: invokevirtual 4549	java/util/concurrent/Semaphore:acquire	()V
    //   166: aload_0
    //   167: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   170: ifnull +798 -> 968
    //   173: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   176: aload_0
    //   177: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   180: iconst_1
    //   181: invokevirtual 4553	org/telegram/messenger/MessagesController:putChat	(Lorg/telegram/tgnet/TLRPC$Chat;Z)V
    //   184: iload_1
    //   185: ifle +785 -> 970
    //   188: aload_0
    //   189: iload_1
    //   190: ineg
    //   191: i2l
    //   192: putfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   195: aload_0
    //   196: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   199: invokestatic 1214	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   202: ifeq +11 -> 213
    //   205: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   208: iload_1
    //   209: iconst_0
    //   210: invokevirtual 4556	org/telegram/messenger/MessagesController:startShortPoll	(IZ)V
    //   213: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   216: aload_0
    //   217: getstatic 3782	org/telegram/messenger/NotificationCenter:messagesDidLoaded	I
    //   220: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   223: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   226: aload_0
    //   227: getstatic 3886	org/telegram/messenger/NotificationCenter:emojiDidLoaded	I
    //   230: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   233: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   236: aload_0
    //   237: getstatic 3894	org/telegram/messenger/NotificationCenter:updateInterfaces	I
    //   240: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   243: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   246: aload_0
    //   247: getstatic 3897	org/telegram/messenger/NotificationCenter:didReceivedNewMessages	I
    //   250: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   253: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   256: aload_0
    //   257: getstatic 3799	org/telegram/messenger/NotificationCenter:closeChats	I
    //   260: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   263: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   266: aload_0
    //   267: getstatic 4005	org/telegram/messenger/NotificationCenter:messagesRead	I
    //   270: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   273: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   276: aload_0
    //   277: getstatic 4011	org/telegram/messenger/NotificationCenter:messagesDeleted	I
    //   280: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   283: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   286: aload_0
    //   287: getstatic 4019	org/telegram/messenger/NotificationCenter:messageReceivedByServer	I
    //   290: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   293: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   296: aload_0
    //   297: getstatic 4054	org/telegram/messenger/NotificationCenter:messageReceivedByAck	I
    //   300: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   303: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   306: aload_0
    //   307: getstatic 4057	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   310: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   313: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   316: aload_0
    //   317: getstatic 3793	org/telegram/messenger/NotificationCenter:chatInfoDidLoaded	I
    //   320: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   323: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   326: aload_0
    //   327: getstatic 4106	org/telegram/messenger/NotificationCenter:contactsDidLoaded	I
    //   330: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   333: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   336: aload_0
    //   337: getstatic 4109	org/telegram/messenger/NotificationCenter:encryptedChatUpdated	I
    //   340: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   343: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   346: aload_0
    //   347: getstatic 4116	org/telegram/messenger/NotificationCenter:messagesReadEncrypted	I
    //   350: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   353: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   356: aload_0
    //   357: getstatic 4147	org/telegram/messenger/NotificationCenter:removeAllMessagesFromDialog	I
    //   360: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   363: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   366: aload_0
    //   367: getstatic 4132	org/telegram/messenger/NotificationCenter:audioProgressDidChanged	I
    //   370: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   373: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   376: aload_0
    //   377: getstatic 4119	org/telegram/messenger/NotificationCenter:audioDidReset	I
    //   380: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   383: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   386: aload_0
    //   387: getstatic 4122	org/telegram/messenger/NotificationCenter:audioPlayStateChanged	I
    //   390: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   393: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   396: aload_0
    //   397: getstatic 4150	org/telegram/messenger/NotificationCenter:screenshotTook	I
    //   400: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   403: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   406: aload_0
    //   407: getstatic 4155	org/telegram/messenger/NotificationCenter:blockedUsersDidLoaded	I
    //   410: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   413: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   416: aload_0
    //   417: getstatic 4161	org/telegram/messenger/NotificationCenter:FileNewChunkAvailable	I
    //   420: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   423: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   426: aload_0
    //   427: getstatic 4167	org/telegram/messenger/NotificationCenter:didCreatedNewDeleteTask	I
    //   430: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   433: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   436: aload_0
    //   437: getstatic 4170	org/telegram/messenger/NotificationCenter:audioDidStarted	I
    //   440: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   443: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   446: aload_0
    //   447: getstatic 4173	org/telegram/messenger/NotificationCenter:updateMessageMedia	I
    //   450: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   453: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   456: aload_0
    //   457: getstatic 4176	org/telegram/messenger/NotificationCenter:replaceMessagesObjects	I
    //   460: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   463: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   466: aload_0
    //   467: getstatic 4192	org/telegram/messenger/NotificationCenter:notificationsSettingsUpdated	I
    //   470: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   473: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   476: aload_0
    //   477: getstatic 4195	org/telegram/messenger/NotificationCenter:didLoadedReplyMessages	I
    //   480: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   483: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   486: aload_0
    //   487: getstatic 4201	org/telegram/messenger/NotificationCenter:didReceivedWebpages	I
    //   490: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   493: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   496: aload_0
    //   497: getstatic 4208	org/telegram/messenger/NotificationCenter:didReceivedWebpagesInUpdates	I
    //   500: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   503: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   506: aload_0
    //   507: getstatic 4221	org/telegram/messenger/NotificationCenter:messagesReadContent	I
    //   510: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   513: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   516: aload_0
    //   517: getstatic 4227	org/telegram/messenger/NotificationCenter:botInfoDidLoaded	I
    //   520: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   523: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   526: aload_0
    //   527: getstatic 3802	org/telegram/messenger/NotificationCenter:botKeyboardDidLoaded	I
    //   530: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   533: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   536: aload_0
    //   537: getstatic 4234	org/telegram/messenger/NotificationCenter:chatSearchResultsAvailable	I
    //   540: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   543: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   546: aload_0
    //   547: getstatic 4237	org/telegram/messenger/NotificationCenter:didUpdatedMessagesViews	I
    //   550: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   553: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   556: aload_0
    //   557: getstatic 4092	org/telegram/messenger/NotificationCenter:chatInfoCantLoad	I
    //   560: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   563: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   566: aload_0
    //   567: getstatic 4198	org/telegram/messenger/NotificationCenter:didLoadedPinnedMessage	I
    //   570: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   573: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   576: aload_0
    //   577: getstatic 4249	org/telegram/messenger/NotificationCenter:peerSettingsDidLoaded	I
    //   580: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   583: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   586: aload_0
    //   587: getstatic 4252	org/telegram/messenger/NotificationCenter:newDraftReceived	I
    //   590: invokevirtual 4560	org/telegram/messenger/NotificationCenter:addObserver	(Ljava/lang/Object;I)V
    //   593: aload_0
    //   594: invokespecial 4562	org/telegram/ui/ActionBar/BaseFragment:onFragmentCreate	()Z
    //   597: pop
    //   598: aload_0
    //   599: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   602: ifnonnull +17 -> 619
    //   605: aload_0
    //   606: getfield 1206	org/telegram/ui/ChatActivity:isBroadcast	Z
    //   609: ifne +10 -> 619
    //   612: aload_0
    //   613: getfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   616: invokestatic 4565	org/telegram/messenger/query/BotQuery:loadBotKeyboard	(J)V
    //   619: aload_0
    //   620: iconst_1
    //   621: putfield 1685	org/telegram/ui/ChatActivity:loading	Z
    //   624: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   627: aload_0
    //   628: getfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   631: aload_0
    //   632: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   635: aload_0
    //   636: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   639: invokevirtual 4569	org/telegram/messenger/MessagesController:loadPeerSettings	(JLorg/telegram/tgnet/TLRPC$User;Lorg/telegram/tgnet/TLRPC$Chat;)V
    //   642: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   645: aload_0
    //   646: getfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   649: iconst_1
    //   650: invokevirtual 4572	org/telegram/messenger/MessagesController:setLastCreatedDialogId	(JZ)V
    //   653: aload_0
    //   654: getfield 1717	org/telegram/ui/ChatActivity:startLoadFromMessageId	I
    //   657: ifeq +803 -> 1460
    //   660: aload_0
    //   661: iconst_1
    //   662: putfield 1719	org/telegram/ui/ChatActivity:needSelectFromMessageId	Z
    //   665: aload_0
    //   666: getfield 547	org/telegram/ui/ChatActivity:waitingForLoad	Ljava/util/ArrayList;
    //   669: aload_0
    //   670: getfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   673: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   676: invokevirtual 1690	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   679: pop
    //   680: iload 4
    //   682: ifeq +696 -> 1378
    //   685: aload_0
    //   686: iload 4
    //   688: i2l
    //   689: putfield 912	org/telegram/ui/ChatActivity:mergeDialogId	J
    //   692: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   695: astore 9
    //   697: aload_0
    //   698: getfield 912	org/telegram/ui/ChatActivity:mergeDialogId	J
    //   701: lstore 6
    //   703: invokestatic 2045	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   706: ifeq +666 -> 1372
    //   709: bipush 30
    //   711: istore_1
    //   712: aload_0
    //   713: getfield 1717	org/telegram/ui/ChatActivity:startLoadFromMessageId	I
    //   716: istore_3
    //   717: aload_0
    //   718: getfield 650	org/telegram/ui/ChatActivity:classGuid	I
    //   721: istore 4
    //   723: aload_0
    //   724: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   727: invokestatic 1214	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   730: istore 8
    //   732: aload_0
    //   733: getfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   736: istore 5
    //   738: aload_0
    //   739: iload 5
    //   741: iconst_1
    //   742: iadd
    //   743: putfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   746: aload 9
    //   748: lload 6
    //   750: iload_1
    //   751: iload_3
    //   752: iconst_1
    //   753: iconst_0
    //   754: iload 4
    //   756: iconst_3
    //   757: iconst_0
    //   758: iload 8
    //   760: iload 5
    //   762: invokevirtual 1694	org/telegram/messenger/MessagesController:loadMessages	(JIIZIIIIZI)V
    //   765: aload_0
    //   766: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   769: ifnull +62 -> 831
    //   772: aconst_null
    //   773: astore 9
    //   775: aload_0
    //   776: getfield 1206	org/telegram/ui/ChatActivity:isBroadcast	Z
    //   779: ifeq +13 -> 792
    //   782: new 4537	java/util/concurrent/Semaphore
    //   785: dup
    //   786: iconst_0
    //   787: invokespecial 4539	java/util/concurrent/Semaphore:<init>	(I)V
    //   790: astore 9
    //   792: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   795: aload_0
    //   796: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   799: getfield 1548	org/telegram/tgnet/TLRPC$Chat:id	I
    //   802: aload 9
    //   804: aload_0
    //   805: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   808: invokestatic 1214	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   811: invokevirtual 4576	org/telegram/messenger/MessagesController:loadChatInfo	(ILjava/util/concurrent/Semaphore;Z)V
    //   814: aload_0
    //   815: getfield 1206	org/telegram/ui/ChatActivity:isBroadcast	Z
    //   818: ifeq +13 -> 831
    //   821: aload 9
    //   823: ifnull +8 -> 831
    //   826: aload 9
    //   828: invokevirtual 4549	java/util/concurrent/Semaphore:acquire	()V
    //   831: iload_2
    //   832: ifeq +731 -> 1563
    //   835: aload_0
    //   836: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   839: getfield 1561	org/telegram/tgnet/TLRPC$User:bot	Z
    //   842: ifeq +721 -> 1563
    //   845: iload_2
    //   846: iconst_1
    //   847: aload_0
    //   848: getfield 650	org/telegram/ui/ChatActivity:classGuid	I
    //   851: invokestatic 4082	org/telegram/messenger/query/BotQuery:loadBotInfo	(IZI)V
    //   854: aload_0
    //   855: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   858: ifnull +26 -> 884
    //   861: aload_0
    //   862: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   865: getfield 4158	org/telegram/messenger/MessagesController:blockedUsers	Ljava/util/ArrayList;
    //   868: aload_0
    //   869: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   872: getfield 1542	org/telegram/tgnet/TLRPC$User:id	I
    //   875: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   878: invokevirtual 3921	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   881: putfield 521	org/telegram/ui/ChatActivity:userBlocked	Z
    //   884: invokestatic 2045	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   887: ifeq +33 -> 920
    //   890: invokestatic 3790	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   893: getstatic 4579	org/telegram/messenger/NotificationCenter:openedChatChanged	I
    //   896: iconst_2
    //   897: anewarray 1326	java/lang/Object
    //   900: dup
    //   901: iconst_0
    //   902: aload_0
    //   903: getfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   906: invokestatic 2868	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   909: aastore
    //   910: dup
    //   911: iconst_1
    //   912: iconst_0
    //   913: invokestatic 4582	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   916: aastore
    //   917: invokevirtual 4585	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   920: aload_0
    //   921: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   924: ifnull +29 -> 953
    //   927: aload_0
    //   928: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   931: getfield 1198	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   934: invokestatic 4588	org/telegram/messenger/AndroidUtilities:getMyLayerVersion	(I)I
    //   937: bipush 46
    //   939: if_icmpeq +14 -> 953
    //   942: invokestatic 4593	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   945: aload_0
    //   946: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   949: aconst_null
    //   950: invokevirtual 4597	org/telegram/messenger/SecretChatHelper:sendNotifyLayerMessage	(Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$Message;)V
    //   953: iconst_1
    //   954: ireturn
    //   955: astore 9
    //   957: ldc_w 2359
    //   960: aload 9
    //   962: invokestatic 2365	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   965: goto -799 -> 166
    //   968: iconst_0
    //   969: ireturn
    //   970: aload_0
    //   971: iconst_1
    //   972: putfield 1206	org/telegram/ui/ChatActivity:isBroadcast	Z
    //   975: aload_0
    //   976: iload_1
    //   977: invokestatic 4601	org/telegram/messenger/AndroidUtilities:makeBroadcastId	(I)J
    //   980: putfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   983: goto -788 -> 195
    //   986: iload_2
    //   987: ifeq +133 -> 1120
    //   990: aload_0
    //   991: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   994: iload_2
    //   995: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   998: invokevirtual 1546	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   1001: putfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1004: aload_0
    //   1005: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1008: ifnonnull +57 -> 1065
    //   1011: new 4537	java/util/concurrent/Semaphore
    //   1014: dup
    //   1015: iconst_0
    //   1016: invokespecial 4539	java/util/concurrent/Semaphore:<init>	(I)V
    //   1019: astore 10
    //   1021: invokestatic 2648	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   1024: invokevirtual 4543	org/telegram/messenger/MessagesStorage:getStorageQueue	()Lorg/telegram/messenger/DispatchQueue;
    //   1027: new 128	org/telegram/ui/ChatActivity$5
    //   1030: dup
    //   1031: aload_0
    //   1032: iload_2
    //   1033: aload 10
    //   1035: invokespecial 4602	org/telegram/ui/ChatActivity$5:<init>	(Lorg/telegram/ui/ChatActivity;ILjava/util/concurrent/Semaphore;)V
    //   1038: invokevirtual 2681	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1041: aload 10
    //   1043: invokevirtual 4549	java/util/concurrent/Semaphore:acquire	()V
    //   1046: aload_0
    //   1047: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1050: ifnull +68 -> 1118
    //   1053: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1056: aload_0
    //   1057: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1060: iconst_1
    //   1061: invokevirtual 4606	org/telegram/messenger/MessagesController:putUser	(Lorg/telegram/tgnet/TLRPC$User;Z)Z
    //   1064: pop
    //   1065: aload_0
    //   1066: iload_2
    //   1067: i2l
    //   1068: putfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   1071: aload_0
    //   1072: aload_0
    //   1073: getfield 4518	org/telegram/ui/ChatActivity:arguments	Landroid/os/Bundle;
    //   1076: ldc_w 4607
    //   1079: invokevirtual 4531	android/os/Bundle:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   1082: putfield 657	org/telegram/ui/ChatActivity:botUser	Ljava/lang/String;
    //   1085: aload 9
    //   1087: ifnull -874 -> 213
    //   1090: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1093: aload_0
    //   1094: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1097: aload 9
    //   1099: invokevirtual 4611	org/telegram/messenger/MessagesController:sendBotStart	(Lorg/telegram/tgnet/TLRPC$User;Ljava/lang/String;)V
    //   1102: goto -889 -> 213
    //   1105: astore 10
    //   1107: ldc_w 2359
    //   1110: aload 10
    //   1112: invokestatic 2365	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1115: goto -69 -> 1046
    //   1118: iconst_0
    //   1119: ireturn
    //   1120: iload_3
    //   1121: ifeq +249 -> 1370
    //   1124: aload_0
    //   1125: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1128: iload_3
    //   1129: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1132: invokevirtual 4615	org/telegram/messenger/MessagesController:getEncryptedChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   1135: putfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   1138: aload_0
    //   1139: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   1142: ifnonnull +56 -> 1198
    //   1145: new 4537	java/util/concurrent/Semaphore
    //   1148: dup
    //   1149: iconst_0
    //   1150: invokespecial 4539	java/util/concurrent/Semaphore:<init>	(I)V
    //   1153: astore 9
    //   1155: invokestatic 2648	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   1158: invokevirtual 4543	org/telegram/messenger/MessagesStorage:getStorageQueue	()Lorg/telegram/messenger/DispatchQueue;
    //   1161: new 162	org/telegram/ui/ChatActivity$6
    //   1164: dup
    //   1165: aload_0
    //   1166: iload_3
    //   1167: aload 9
    //   1169: invokespecial 4616	org/telegram/ui/ChatActivity$6:<init>	(Lorg/telegram/ui/ChatActivity;ILjava/util/concurrent/Semaphore;)V
    //   1172: invokevirtual 2681	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1175: aload 9
    //   1177: invokevirtual 4549	java/util/concurrent/Semaphore:acquire	()V
    //   1180: aload_0
    //   1181: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   1184: ifnull +169 -> 1353
    //   1187: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1190: aload_0
    //   1191: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   1194: iconst_1
    //   1195: invokevirtual 4620	org/telegram/messenger/MessagesController:putEncryptedChat	(Lorg/telegram/tgnet/TLRPC$EncryptedChat;Z)V
    //   1198: aload_0
    //   1199: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1202: aload_0
    //   1203: getfield 1193	org/telegram/ui/ChatActivity:currentEncryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   1206: getfield 4621	org/telegram/tgnet/TLRPC$EncryptedChat:user_id	I
    //   1209: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1212: invokevirtual 1546	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   1215: putfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1218: aload_0
    //   1219: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1222: ifnonnull +56 -> 1278
    //   1225: new 4537	java/util/concurrent/Semaphore
    //   1228: dup
    //   1229: iconst_0
    //   1230: invokespecial 4539	java/util/concurrent/Semaphore:<init>	(I)V
    //   1233: astore 9
    //   1235: invokestatic 2648	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   1238: invokevirtual 4543	org/telegram/messenger/MessagesStorage:getStorageQueue	()Lorg/telegram/messenger/DispatchQueue;
    //   1241: new 190	org/telegram/ui/ChatActivity$7
    //   1244: dup
    //   1245: aload_0
    //   1246: aload 9
    //   1248: invokespecial 4624	org/telegram/ui/ChatActivity$7:<init>	(Lorg/telegram/ui/ChatActivity;Ljava/util/concurrent/Semaphore;)V
    //   1251: invokevirtual 2681	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1254: aload 9
    //   1256: invokevirtual 4549	java/util/concurrent/Semaphore:acquire	()V
    //   1259: aload_0
    //   1260: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1263: ifnull +105 -> 1368
    //   1266: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1269: aload_0
    //   1270: getfield 1509	org/telegram/ui/ChatActivity:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1273: iconst_1
    //   1274: invokevirtual 4606	org/telegram/messenger/MessagesController:putUser	(Lorg/telegram/tgnet/TLRPC$User;Z)Z
    //   1277: pop
    //   1278: aload_0
    //   1279: iload_3
    //   1280: i2l
    //   1281: bipush 32
    //   1283: lshl
    //   1284: putfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   1287: aload_0
    //   1288: getfield 556	org/telegram/ui/ChatActivity:maxMessageId	[I
    //   1291: astore 9
    //   1293: aload_0
    //   1294: getfield 556	org/telegram/ui/ChatActivity:maxMessageId	[I
    //   1297: iconst_1
    //   1298: ldc_w 557
    //   1301: iastore
    //   1302: aload 9
    //   1304: iconst_0
    //   1305: ldc_w 557
    //   1308: iastore
    //   1309: aload_0
    //   1310: getfield 559	org/telegram/ui/ChatActivity:minMessageId	[I
    //   1313: astore 9
    //   1315: aload_0
    //   1316: getfield 559	org/telegram/ui/ChatActivity:minMessageId	[I
    //   1319: iconst_1
    //   1320: ldc_w 554
    //   1323: iastore
    //   1324: aload 9
    //   1326: iconst_0
    //   1327: ldc_w 554
    //   1330: iastore
    //   1331: invokestatic 1672	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   1334: invokevirtual 4627	org/telegram/messenger/MediaController:startMediaObserver	()V
    //   1337: goto -1124 -> 213
    //   1340: astore 9
    //   1342: ldc_w 2359
    //   1345: aload 9
    //   1347: invokestatic 2365	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1350: goto -170 -> 1180
    //   1353: iconst_0
    //   1354: ireturn
    //   1355: astore 9
    //   1357: ldc_w 2359
    //   1360: aload 9
    //   1362: invokestatic 2365	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1365: goto -106 -> 1259
    //   1368: iconst_0
    //   1369: ireturn
    //   1370: iconst_0
    //   1371: ireturn
    //   1372: bipush 20
    //   1374: istore_1
    //   1375: goto -663 -> 712
    //   1378: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1381: astore 9
    //   1383: aload_0
    //   1384: getfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   1387: lstore 6
    //   1389: invokestatic 2045	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1392: ifeq +62 -> 1454
    //   1395: bipush 30
    //   1397: istore_1
    //   1398: aload_0
    //   1399: getfield 1717	org/telegram/ui/ChatActivity:startLoadFromMessageId	I
    //   1402: istore_3
    //   1403: aload_0
    //   1404: getfield 650	org/telegram/ui/ChatActivity:classGuid	I
    //   1407: istore 4
    //   1409: aload_0
    //   1410: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1413: invokestatic 1214	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   1416: istore 8
    //   1418: aload_0
    //   1419: getfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   1422: istore 5
    //   1424: aload_0
    //   1425: iload 5
    //   1427: iconst_1
    //   1428: iadd
    //   1429: putfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   1432: aload 9
    //   1434: lload 6
    //   1436: iload_1
    //   1437: iload_3
    //   1438: iconst_1
    //   1439: iconst_0
    //   1440: iload 4
    //   1442: iconst_3
    //   1443: iconst_0
    //   1444: iload 8
    //   1446: iload 5
    //   1448: invokevirtual 1694	org/telegram/messenger/MessagesController:loadMessages	(JIIZIIIIZI)V
    //   1451: goto -686 -> 765
    //   1454: bipush 20
    //   1456: istore_1
    //   1457: goto -59 -> 1398
    //   1460: aload_0
    //   1461: getfield 547	org/telegram/ui/ChatActivity:waitingForLoad	Ljava/util/ArrayList;
    //   1464: aload_0
    //   1465: getfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   1468: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1471: invokevirtual 1690	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1474: pop
    //   1475: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1478: astore 9
    //   1480: aload_0
    //   1481: getfield 846	org/telegram/ui/ChatActivity:dialog_id	J
    //   1484: lstore 6
    //   1486: invokestatic 2045	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1489: ifeq +55 -> 1544
    //   1492: bipush 30
    //   1494: istore_1
    //   1495: aload_0
    //   1496: getfield 650	org/telegram/ui/ChatActivity:classGuid	I
    //   1499: istore_3
    //   1500: aload_0
    //   1501: getfield 1151	org/telegram/ui/ChatActivity:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1504: invokestatic 1214	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   1507: istore 8
    //   1509: aload_0
    //   1510: getfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   1513: istore 4
    //   1515: aload_0
    //   1516: iload 4
    //   1518: iconst_1
    //   1519: iadd
    //   1520: putfield 1687	org/telegram/ui/ChatActivity:lastLoadIndex	I
    //   1523: aload 9
    //   1525: lload 6
    //   1527: iload_1
    //   1528: iconst_0
    //   1529: iconst_1
    //   1530: iconst_0
    //   1531: iload_3
    //   1532: iconst_2
    //   1533: iconst_0
    //   1534: iload 8
    //   1536: iload 4
    //   1538: invokevirtual 1694	org/telegram/messenger/MessagesController:loadMessages	(JIIZIIIIZI)V
    //   1541: goto -776 -> 765
    //   1544: bipush 20
    //   1546: istore_1
    //   1547: goto -52 -> 1495
    //   1550: astore 9
    //   1552: ldc_w 2359
    //   1555: aload 9
    //   1557: invokestatic 2365	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1560: goto -729 -> 831
    //   1563: aload_0
    //   1564: getfield 581	org/telegram/ui/ChatActivity:info	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   1567: instanceof 1563
    //   1570: ifeq -716 -> 854
    //   1573: iconst_0
    //   1574: istore_1
    //   1575: iload_1
    //   1576: aload_0
    //   1577: getfield 581	org/telegram/ui/ChatActivity:info	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   1580: getfield 1569	org/telegram/tgnet/TLRPC$ChatFull:participants	Lorg/telegram/tgnet/TLRPC$ChatParticipants;
    //   1583: getfield 1573	org/telegram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
    //   1586: invokevirtual 1389	java/util/ArrayList:size	()I
    //   1589: if_icmpge -735 -> 854
    //   1592: aload_0
    //   1593: getfield 581	org/telegram/ui/ChatActivity:info	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   1596: getfield 1569	org/telegram/tgnet/TLRPC$ChatFull:participants	Lorg/telegram/tgnet/TLRPC$ChatParticipants;
    //   1599: getfield 1573	org/telegram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
    //   1602: iload_1
    //   1603: invokevirtual 1393	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1606: checkcast 1575	org/telegram/tgnet/TLRPC$ChatParticipant
    //   1609: astore 9
    //   1611: invokestatic 1493	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1614: aload 9
    //   1616: getfield 1576	org/telegram/tgnet/TLRPC$ChatParticipant:user_id	I
    //   1619: invokestatic 1134	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1622: invokevirtual 1546	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   1625: astore 9
    //   1627: aload 9
    //   1629: ifnull +24 -> 1653
    //   1632: aload 9
    //   1634: getfield 1561	org/telegram/tgnet/TLRPC$User:bot	Z
    //   1637: ifeq +16 -> 1653
    //   1640: aload 9
    //   1642: getfield 1542	org/telegram/tgnet/TLRPC$User:id	I
    //   1645: iconst_1
    //   1646: aload_0
    //   1647: getfield 650	org/telegram/ui/ChatActivity:classGuid	I
    //   1650: invokestatic 4082	org/telegram/messenger/query/BotQuery:loadBotInfo	(IZI)V
    //   1653: iload_1
    //   1654: iconst_1
    //   1655: iadd
    //   1656: istore_1
    //   1657: goto -82 -> 1575
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1660	0	this	ChatActivity
    //   11	1646	1	i	int
    //   23	1044	2	j	int
    //   35	1497	3	k	int
    //   89	1448	4	m	int
    //   736	711	5	n	int
    //   701	825	6	l	long
    //   730	805	8	bool	boolean
    //   61	766	9	localObject1	Object
    //   955	143	9	localException1	Exception
    //   1153	172	9	localObject2	Object
    //   1340	6	9	localException2	Exception
    //   1355	6	9	localException3	Exception
    //   1381	143	9	localMessagesController	MessagesController
    //   1550	6	9	localException4	Exception
    //   1609	32	9	localObject3	Object
    //   1019	23	10	localSemaphore	Semaphore
    //   1105	6	10	localException5	Exception
    // Exception table:
    //   from	to	target	type
    //   161	166	955	java/lang/Exception
    //   1041	1046	1105	java/lang/Exception
    //   1175	1180	1340	java/lang/Exception
    //   1254	1259	1355	java/lang/Exception
    //   826	831	1550	java/lang/Exception
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.chatActivityEnterView != null) {
      this.chatActivityEnterView.onDestroy();
    }
    if (this.mentionsAdapter != null) {
      this.mentionsAdapter.onDestroy();
    }
    MessagesController.getInstance().setLastCreatedDialogId(this.dialog_id, false);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesRead);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesReadEncrypted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.screenshotTook);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileNewChunkAvailable);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateMessageMedia);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.replaceMessagesObjects);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedWebpages);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesReadContent);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botInfoDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botKeyboardDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatSearchResultsAvailable);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedMessagesViews);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoCantLoad);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedPinnedMessage);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.peerSettingsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.newDraftReceived);
    if (AndroidUtilities.isTablet()) {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.openedChatChanged, new Object[] { Long.valueOf(this.dialog_id), Boolean.valueOf(true) });
    }
    if (this.currentEncryptedChat != null) {
      MediaController.getInstance().stopMediaObserver();
    }
    try
    {
      if (Build.VERSION.SDK_INT >= 23) {
        getParentActivity().getWindow().clearFlags(8192);
      }
      if (this.currentUser != null) {
        MessagesController.getInstance().cancelLoadFullUser(this.currentUser.id);
      }
      AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
      if (this.stickersAdapter != null) {
        this.stickersAdapter.onDestroy();
      }
      if (this.chatAttachAlert != null) {
        this.chatAttachAlert.onDestroy();
      }
      AndroidUtilities.unlockOrientation(getParentActivity());
      if (ChatObject.isChannel(this.currentChat)) {
        MessagesController.getInstance().startShortPoll(this.currentChat.id, true);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        FileLog.e("tmessages", localThrowable);
      }
    }
  }
  
  public void onPause()
  {
    boolean bool2 = true;
    super.onPause();
    MediaController.getInstance().stopRaiseToEarSensors(this);
    if (this.menuItem != null) {
      this.menuItem.closeSubMenu();
    }
    if (this.chatAttachAlert != null) {
      this.chatAttachAlert.onPause();
    }
    this.paused = true;
    this.wasPaused = true;
    NotificationsController.getInstance().setOpenedDialogId(0L);
    Object localObject1 = null;
    ArrayList localArrayList = null;
    boolean bool1 = true;
    if (this.chatActivityEnterView != null)
    {
      this.chatActivityEnterView.onPause();
      localObject1 = localArrayList;
      if (!this.chatActivityEnterView.isEditingMessage())
      {
        localObject2 = AndroidUtilities.getTrimmedString(this.chatActivityEnterView.getFieldText());
        localObject1 = localArrayList;
        if (!TextUtils.isEmpty((CharSequence)localObject2))
        {
          localObject1 = localArrayList;
          if (!TextUtils.equals((CharSequence)localObject2, "@gif")) {
            localObject1 = localObject2;
          }
        }
      }
      bool1 = this.chatActivityEnterView.isMessageWebPageSearchEnabled();
      this.chatActivityEnterView.setFieldFocused(false);
    }
    Object localObject2 = new CharSequence[1];
    localObject2[0] = localObject1;
    localArrayList = MessagesQuery.getEntities((CharSequence[])localObject2);
    long l = this.dialog_id;
    localObject2 = localObject2[0];
    if (this.replyingMessageObject != null)
    {
      localObject1 = this.replyingMessageObject.messageOwner;
      if (bool1) {
        break label252;
      }
    }
    label252:
    for (bool1 = bool2;; bool1 = false)
    {
      DraftQuery.saveDraft(l, (CharSequence)localObject2, localArrayList, (TLRPC.Message)localObject1, bool1);
      MessagesController.getInstance().cancelTyping(0, this.dialog_id);
      if (this.currentEncryptedChat != null)
      {
        this.chatLeaveTime = System.currentTimeMillis();
        updateInformationForScreenshotDetector();
      }
      return;
      localObject1 = null;
      break;
    }
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (this.chatActivityEnterView != null) {
      this.chatActivityEnterView.onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
    }
    if (this.mentionsAdapter != null) {
      this.mentionsAdapter.onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
    }
    if ((paramInt == 17) && (this.chatAttachAlert != null)) {
      this.chatAttachAlert.checkCamera(false);
    }
    do
    {
      return;
      if ((paramInt == 19) && (paramArrayOfInt != null) && (paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
      {
        processSelectedAttach(0);
        return;
      }
    } while ((paramInt != 20) || (paramArrayOfInt == null) || (paramArrayOfInt.length <= 0) || (paramArrayOfInt[0] != 0));
    processSelectedAttach(2);
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    MediaController.getInstance().startRaiseToEarSensors(this);
    checkRaiseSensors();
    checkActionBarMenu();
    if ((this.replyImageLocation != null) && (this.replyImageView != null)) {
      this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable)null);
    }
    if ((this.pinnedImageLocation != null) && (this.pinnedMessageImageView != null)) {
      this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable)null);
    }
    NotificationsController.getInstance().setOpenedDialogId(this.dialog_id);
    int i;
    label207:
    Iterator localIterator;
    if (this.scrollToTopOnResume)
    {
      if ((!this.scrollToTopUnReadOnResume) || (this.scrollToMessage == null)) {
        break label522;
      }
      if (this.chatListView != null)
      {
        if (this.scrollToMessagePosition == 56536)
        {
          i = Math.max(0, (this.chatListView.getHeight() - this.scrollToMessage.getApproximateHeight()) / 2);
          this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - this.messages.indexOf(this.scrollToMessage), -this.chatListView.getPaddingTop() - AndroidUtilities.dp(7.0F) + i);
        }
      }
      else
      {
        this.scrollToTopUnReadOnResume = false;
        this.scrollToTopOnResume = false;
        this.scrollToMessage = null;
      }
    }
    else
    {
      this.paused = false;
      if ((this.readWhenResume) && (!this.messages.isEmpty())) {
        localIterator = this.messages.iterator();
      }
    }
    for (;;)
    {
      MessageObject localMessageObject;
      if (localIterator.hasNext())
      {
        localMessageObject = (MessageObject)localIterator.next();
        if ((localMessageObject.isUnread()) || (localMessageObject.isOut())) {}
      }
      else
      {
        this.readWhenResume = false;
        MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject)this.messages.get(0)).getId(), this.readWithMid, this.readWithDate, true, false);
        checkScrollForLoad(false);
        if (this.wasPaused)
        {
          this.wasPaused = false;
          if (this.chatAdapter != null) {
            this.chatAdapter.notifyDataSetChanged();
          }
        }
        fixLayout();
        applyDraftMaybe(false);
        if ((this.bottomOverlayChat != null) && (this.bottomOverlayChat.getVisibility() != 0)) {
          this.chatActivityEnterView.setFieldFocused(true);
        }
        if (this.chatActivityEnterView != null) {
          this.chatActivityEnterView.onResume();
        }
        if (this.currentEncryptedChat != null)
        {
          this.chatEnterTime = System.currentTimeMillis();
          this.chatLeaveTime = 0L;
        }
        if (this.startVideoEdit != null) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ChatActivity.this.openVideoEditor(ChatActivity.this.startVideoEdit, false, false);
              ChatActivity.access$13702(ChatActivity.this, null);
            }
          });
        }
        if ((this.chatListView != null) && ((this.chatActivityEnterView == null) || (!this.chatActivityEnterView.isEditingMessage())))
        {
          this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
          this.chatListView.setOnItemClickListener(this.onItemClickListener);
          this.chatListView.setLongClickable(true);
        }
        checkBotCommands();
        return;
        if (this.scrollToMessagePosition == 55536)
        {
          i = 0;
          break;
        }
        i = this.scrollToMessagePosition;
        break;
        label522:
        moveScrollToLastMessage();
        break label207;
      }
      if (!localMessageObject.isOut()) {
        localMessageObject.setIsRead();
      }
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    NotificationCenter.getInstance().setAnimationInProgress(false);
    if (paramBoolean1)
    {
      this.openAnimationEnded = true;
      if (this.currentUser != null) {
        MessagesController.getInstance().loadFullUser(this.currentUser, this.classGuid, false);
      }
      if (Build.VERSION.SDK_INT >= 21) {
        createChatAttachView();
      }
    }
  }
  
  public void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoaded, NotificationCenter.botKeyboardDidLoaded });
    NotificationCenter.getInstance().setAnimationInProgress(true);
    if (paramBoolean1) {
      this.openAnimationEnded = false;
    }
  }
  
  public boolean openVideoEditor(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject = new Bundle();
    ((Bundle)localObject).putString("videoPath", paramString);
    localObject = new VideoEditorActivity((Bundle)localObject);
    ((VideoEditorActivity)localObject).setDelegate(new VideoEditorActivity.VideoEditorActivityDelegate()
    {
      public void didFinishEditVideo(String paramAnonymousString, long paramAnonymousLong1, long paramAnonymousLong2, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, long paramAnonymousLong3, long paramAnonymousLong4)
      {
        VideoEditedInfo localVideoEditedInfo = new VideoEditedInfo();
        localVideoEditedInfo.startTime = paramAnonymousLong1;
        localVideoEditedInfo.endTime = paramAnonymousLong2;
        localVideoEditedInfo.rotationValue = paramAnonymousInt3;
        localVideoEditedInfo.originalWidth = paramAnonymousInt4;
        localVideoEditedInfo.originalHeight = paramAnonymousInt5;
        localVideoEditedInfo.bitrate = paramAnonymousInt6;
        localVideoEditedInfo.resultWidth = paramAnonymousInt1;
        localVideoEditedInfo.resultHeight = paramAnonymousInt2;
        localVideoEditedInfo.originalPath = paramAnonymousString;
        SendMessagesHelper.prepareSendingVideo(paramAnonymousString, paramAnonymousLong3, paramAnonymousLong4, paramAnonymousInt1, paramAnonymousInt2, localVideoEditedInfo, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
      }
    });
    if ((this.parentLayout == null) || (!((VideoEditorActivity)localObject).onFragmentCreate()))
    {
      SendMessagesHelper.prepareSendingVideo(paramString, 0L, 0L, 0, 0, null, this.dialog_id, this.replyingMessageObject);
      showReplyPanel(false, null, null, null, false, true);
      DraftQuery.cleanDraft(this.dialog_id, true);
      return false;
    }
    paramString = this.parentLayout;
    if (!paramBoolean2) {}
    for (paramBoolean2 = true;; paramBoolean2 = false)
    {
      paramString.presentFragment((BaseFragment)localObject, paramBoolean1, paramBoolean2, true);
      return true;
    }
  }
  
  public boolean playFirstUnreadVoiceMessage()
  {
    boolean bool2 = false;
    int i = this.messages.size() - 1;
    Object localObject;
    label92:
    boolean bool1;
    if (i >= 0)
    {
      localObject = (MessageObject)this.messages.get(i);
      if ((((MessageObject)localObject).isVoice()) && (((MessageObject)localObject).isContentUnread()) && (!((MessageObject)localObject).isOut()) && (((MessageObject)localObject).messageOwner.to_id.channel_id == 0))
      {
        MediaController localMediaController = MediaController.getInstance();
        if (MediaController.getInstance().playAudio((MessageObject)localObject))
        {
          localObject = createVoiceMessagesPlaylist((MessageObject)localObject, true);
          localMediaController.setVoiceMessagesPlaylist((ArrayList)localObject, true);
          bool1 = true;
        }
      }
    }
    do
    {
      do
      {
        do
        {
          return bool1;
          localObject = null;
          break label92;
          i -= 1;
          break;
          bool1 = bool2;
        } while (Build.VERSION.SDK_INT < 23);
        bool1 = bool2;
      } while (getParentActivity() == null);
      bool1 = bool2;
    } while (getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0);
    getParentActivity().requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 3);
    return true;
  }
  
  public void processInlineBotContextPM(TLRPC.TL_inlineBotSwitchPM paramTL_inlineBotSwitchPM)
  {
    if (paramTL_inlineBotSwitchPM == null) {}
    Bundle localBundle;
    do
    {
      TLRPC.User localUser;
      do
      {
        return;
        localUser = this.mentionsAdapter.getContextBotUser();
      } while (localUser == null);
      this.chatActivityEnterView.setFieldText("");
      if (this.dialog_id == localUser.id)
      {
        this.inlineReturn = this.dialog_id;
        MessagesController.getInstance().sendBotStart(this.currentUser, paramTL_inlineBotSwitchPM.start_param);
        return;
      }
      localBundle = new Bundle();
      localBundle.putInt("user_id", localUser.id);
      localBundle.putString("inline_query", paramTL_inlineBotSwitchPM.start_param);
      localBundle.putLong("inline_return", this.dialog_id);
    } while (!MessagesController.checkCanOpenChat(localBundle, this));
    presentFragment(new ChatActivity(localBundle));
  }
  
  public boolean processSendingText(String paramString)
  {
    return this.chatActivityEnterView.processSendingText(paramString);
  }
  
  public boolean processSwitchButton(TLRPC.TL_keyboardButtonSwitchInline paramTL_keyboardButtonSwitchInline)
  {
    if ((this.inlineReturn == 0L) || (paramTL_keyboardButtonSwitchInline.same_peer)) {
      return false;
    }
    paramTL_keyboardButtonSwitchInline = "@" + this.currentUser.username + " " + paramTL_keyboardButtonSwitchInline.query;
    if (this.inlineReturn == this.dialog_id)
    {
      this.inlineReturn = 0L;
      this.chatActivityEnterView.setFieldText(paramTL_keyboardButtonSwitchInline);
    }
    for (;;)
    {
      return true;
      DraftQuery.saveDraft(this.inlineReturn, paramTL_keyboardButtonSwitchInline, null, null, false);
      if (this.parentLayout.fragmentsStack.size() > 1)
      {
        paramTL_keyboardButtonSwitchInline = (BaseFragment)this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
        if ((!(paramTL_keyboardButtonSwitchInline instanceof ChatActivity)) || (((ChatActivity)paramTL_keyboardButtonSwitchInline).dialog_id != this.inlineReturn)) {
          break;
        }
        finishFragment();
      }
    }
    paramTL_keyboardButtonSwitchInline = new Bundle();
    int i = (int)this.inlineReturn;
    int j = (int)(this.inlineReturn >> 32);
    if (i != 0) {
      if (i > 0) {
        paramTL_keyboardButtonSwitchInline.putInt("user_id", i);
      }
    }
    for (;;)
    {
      presentFragment(new ChatActivity(paramTL_keyboardButtonSwitchInline), true);
      break;
      if (i < 0)
      {
        paramTL_keyboardButtonSwitchInline.putInt("chat_id", -i);
        continue;
        paramTL_keyboardButtonSwitchInline.putInt("enc_id", j);
      }
    }
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    this.currentPicturePath = paramBundle.getString("path");
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if (this.currentPicturePath != null) {
      paramBundle.putString("path", this.currentPicturePath);
    }
  }
  
  public void sendButtonPressed(int paramInt) {}
  
  public void sendMedia(MediaController.PhotoEntry paramPhotoEntry, boolean paramBoolean)
  {
    if (paramPhotoEntry.isVideo)
    {
      VideoEditedInfo localVideoEditedInfo = null;
      long l = 0L;
      if (paramBoolean)
      {
        localVideoEditedInfo = new VideoEditedInfo();
        localVideoEditedInfo.bitrate = -1;
        localVideoEditedInfo.originalPath = paramPhotoEntry.path;
        localVideoEditedInfo.endTime = -1L;
        localVideoEditedInfo.startTime = -1L;
        l = new File(paramPhotoEntry.path).length();
      }
      SendMessagesHelper.prepareSendingVideo(paramPhotoEntry.path, l, 0L, 0, 0, localVideoEditedInfo, this.dialog_id, this.replyingMessageObject);
    }
    do
    {
      return;
      if (paramPhotoEntry.imagePath != null)
      {
        SendMessagesHelper.prepareSendingPhoto(paramPhotoEntry.imagePath, null, this.dialog_id, this.replyingMessageObject, paramPhotoEntry.caption, paramPhotoEntry.stickers);
        showReplyPanel(false, null, null, null, false, true);
        DraftQuery.cleanDraft(this.dialog_id, true);
        return;
      }
    } while (paramPhotoEntry.path == null);
    SendMessagesHelper.prepareSendingPhoto(paramPhotoEntry.path, null, this.dialog_id, this.replyingMessageObject, paramPhotoEntry.caption, paramPhotoEntry.stickers);
    showReplyPanel(false, null, null, null, false, true);
    DraftQuery.cleanDraft(this.dialog_id, true);
  }
  
  public void setBotUser(String paramString)
  {
    if (this.inlineReturn != 0L)
    {
      MessagesController.getInstance().sendBotStart(this.currentUser, paramString);
      return;
    }
    this.botUser = paramString;
    updateBottomOverlay();
  }
  
  public void setPhotoChecked(int paramInt) {}
  
  public void shareMyContact(final MessageObject paramMessageObject)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("ShareYouPhoneNumberTitle", 2131166284));
    if (this.currentUser != null) {
      if (this.currentUser.bot) {
        localBuilder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfoBot", 2131165329));
      }
    }
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          SendMessagesHelper.getInstance().sendMessage(UserConfig.getCurrentUser(), ChatActivity.this.dialog_id, paramMessageObject, null, null);
          ChatActivity.this.moveScrollToLastMessage();
          ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
      showDialog(localBuilder.create());
      return;
      localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureShareMyContactInfoUser", 2131165330, new Object[] { PhoneFormat.getInstance().format("+" + UserConfig.getCurrentUser().phone), ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name) })));
      continue;
      localBuilder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfo", 2131165328));
    }
  }
  
  public void showAlert(TLRPC.User paramUser, String paramString)
  {
    if ((this.alertView == null) || (paramUser == null) || (paramString == null)) {
      return;
    }
    if (this.alertView.getTag() != null)
    {
      this.alertView.setTag(null);
      if (this.alertViewAnimator != null)
      {
        this.alertViewAnimator.cancel();
        this.alertViewAnimator = null;
      }
      this.alertView.setVisibility(0);
      this.alertViewAnimator = new AnimatorSet();
      this.alertViewAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.alertView, "translationY", new float[] { 0.0F }) });
      this.alertViewAnimator.setDuration(200L);
      this.alertViewAnimator.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ChatActivity.this.alertViewAnimator != null) && (ChatActivity.this.alertViewAnimator.equals(paramAnonymousAnimator))) {
            ChatActivity.access$13002(ChatActivity.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ChatActivity.this.alertViewAnimator != null) && (ChatActivity.this.alertViewAnimator.equals(paramAnonymousAnimator))) {
            ChatActivity.access$13002(ChatActivity.this, null);
          }
        }
      });
      this.alertViewAnimator.start();
    }
    this.alertNameTextView.setText(ContactsController.formatName(paramUser.first_name, paramUser.last_name));
    this.alertTextView.setText(Emoji.replaceEmoji(paramString.replace('\n', ' '), this.alertTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
    if (this.hideAlertViewRunnable != null) {
      AndroidUtilities.cancelRunOnUIThread(this.hideAlertViewRunnable);
    }
    paramUser = new Runnable()
    {
      public void run()
      {
        if (ChatActivity.this.hideAlertViewRunnable != this) {}
        while (ChatActivity.this.alertView.getTag() != null) {
          return;
        }
        ChatActivity.this.alertView.setTag(Integer.valueOf(1));
        if (ChatActivity.this.alertViewAnimator != null)
        {
          ChatActivity.this.alertViewAnimator.cancel();
          ChatActivity.access$13002(ChatActivity.this, null);
        }
        ChatActivity.access$13002(ChatActivity.this, new AnimatorSet());
        ChatActivity.this.alertViewAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(ChatActivity.this.alertView, "translationY", new float[] { -AndroidUtilities.dp(50.0F) }) });
        ChatActivity.this.alertViewAnimator.setDuration(200L);
        ChatActivity.this.alertViewAnimator.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymous2Animator)
          {
            if ((ChatActivity.this.alertViewAnimator != null) && (ChatActivity.this.alertViewAnimator.equals(paramAnonymous2Animator))) {
              ChatActivity.access$13002(ChatActivity.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymous2Animator)
          {
            if ((ChatActivity.this.alertViewAnimator != null) && (ChatActivity.this.alertViewAnimator.equals(paramAnonymous2Animator)))
            {
              ChatActivity.this.alertView.setVisibility(8);
              ChatActivity.access$13002(ChatActivity.this, null);
            }
          }
        });
        ChatActivity.this.alertViewAnimator.start();
      }
    };
    this.hideAlertViewRunnable = paramUser;
    AndroidUtilities.runOnUIThread(paramUser, 3000L);
  }
  
  public void showOpenGameAlert(final TLRPC.TL_game paramTL_game, final MessageObject paramMessageObject, final String paramString, boolean paramBoolean, final int paramInt)
  {
    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
    if (paramBoolean)
    {
      localObject2 = new AlertDialog.Builder(getParentActivity());
      ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", 2131165299));
      if (localUser != null)
      {
        localObject1 = ContactsController.formatName(localUser.first_name, localUser.last_name);
        ((AlertDialog.Builder)localObject2).setMessage(LocaleController.formatString("BotPermissionGameAlert", 2131165370, new Object[] { localObject1 }));
        ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ChatActivity.this.showOpenGameAlert(paramTL_game, paramMessageObject, paramString, false, paramInt);
            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("askgame_" + paramInt, false).commit();
          }
        });
        ((AlertDialog.Builder)localObject2).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
        showDialog(((AlertDialog.Builder)localObject2).create());
      }
    }
    do
    {
      return;
      localObject1 = "";
      break;
      if ((Build.VERSION.SDK_INT < 21) || (AndroidUtilities.isTablet()) || (!WebviewActivity.supportWebview())) {
        break label255;
      }
    } while (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) != this);
    if ((localUser != null) && (!TextUtils.isEmpty(localUser.username))) {}
    for (Object localObject1 = localUser.username;; localObject1 = "")
    {
      presentFragment(new WebviewActivity(paramString, (String)localObject1, paramTL_game.title, paramTL_game.short_name, paramMessageObject));
      return;
    }
    label255:
    localObject1 = getParentActivity();
    Object localObject2 = paramTL_game.short_name;
    if ((localUser != null) && (localUser.username != null)) {}
    for (paramTL_game = localUser.username;; paramTL_game = "")
    {
      WebviewActivity.openGameInBrowser(paramString, paramMessageObject, (Activity)localObject1, (String)localObject2, paramTL_game);
      return;
    }
  }
  
  public void showOpenUrlAlert(final String paramString, boolean paramBoolean)
  {
    boolean bool = true;
    if ((Browser.isInternalUrl(paramString)) || (!paramBoolean))
    {
      localObject = getParentActivity();
      if (this.inlineReturn == 0L) {}
      for (paramBoolean = bool;; paramBoolean = false)
      {
        Browser.openUrl((Context)localObject, paramString, paramBoolean);
        return;
      }
    }
    Object localObject = new AlertDialog.Builder(getParentActivity());
    ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
    ((AlertDialog.Builder)localObject).setMessage(LocaleController.formatString("OpenUrlAlert", 2131166059, new Object[] { paramString }));
    ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("Open", 2131166056), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = ChatActivity.this.getParentActivity();
        String str = paramString;
        if (ChatActivity.this.inlineReturn == 0L) {}
        for (boolean bool = true;; bool = false)
        {
          Browser.openUrl(paramAnonymousDialogInterface, str, bool);
          return;
        }
      }
    });
    ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
    showDialog(((AlertDialog.Builder)localObject).create());
  }
  
  public void showReplyPanel(boolean paramBoolean1, MessageObject paramMessageObject, ArrayList<MessageObject> paramArrayList, TLRPC.WebPage paramWebPage, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (this.chatActivityEnterView == null) {}
    label181:
    label257:
    label666:
    label782:
    label800:
    label855:
    label891:
    label1053:
    label1751:
    label1829:
    label1898:
    do
    {
      Object localObject1;
      Object localObject2;
      do
      {
        do
        {
          do
          {
            return;
            if (!paramBoolean1) {
              break;
            }
          } while ((paramMessageObject == null) && (paramArrayList == null) && (paramWebPage == null));
          if ((this.searchItem != null) && (this.actionBar.isSearchFieldVisible()))
          {
            this.actionBar.closeSearchField();
            this.chatActivityEnterView.setFieldFocused();
          }
          paramBoolean2 = false;
          paramBoolean1 = paramBoolean2;
          localObject1 = paramMessageObject;
          localObject2 = paramArrayList;
          if (paramMessageObject != null)
          {
            paramBoolean1 = paramBoolean2;
            localObject1 = paramMessageObject;
            localObject2 = paramArrayList;
            if (paramMessageObject.getDialogId() != this.dialog_id)
            {
              localObject2 = new ArrayList();
              ((ArrayList)localObject2).add(paramMessageObject);
              localObject1 = null;
              paramBoolean1 = true;
            }
          }
          if (localObject1 == null) {
            break;
          }
          this.forwardingMessages = null;
          this.replyingMessageObject = ((MessageObject)localObject1);
          this.chatActivityEnterView.setReplyingMessageObject((MessageObject)localObject1);
        } while (this.foundWebPage != null);
        if (!((MessageObject)localObject1).isFromUser()) {
          break;
        }
        paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(((MessageObject)localObject1).messageOwner.from_id));
      } while (paramMessageObject == null);
      paramMessageObject = UserObject.getUserName(paramMessageObject);
      this.replyIconImageView.setImageResource(2130837958);
      this.replyNameTextView.setText(paramMessageObject);
      int i;
      if ((((MessageObject)localObject1).messageOwner.media instanceof TLRPC.TL_messageMediaGame))
      {
        this.replyObjectTextView.setText(Emoji.replaceEmoji(((MessageObject)localObject1).messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
        paramMessageObject = (MessageObject)localObject1;
        localObject1 = (FrameLayout.LayoutParams)this.replyNameTextView.getLayoutParams();
        localObject2 = (FrameLayout.LayoutParams)this.replyObjectTextView.getLayoutParams();
        paramArrayList = null;
        if (paramMessageObject != null)
        {
          paramWebPage = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs2, 80);
          paramArrayList = paramWebPage;
          if (paramWebPage == null) {
            paramArrayList = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs, 80);
          }
        }
        if ((paramArrayList != null) && (!(paramArrayList instanceof TLRPC.TL_photoSizeEmpty)) && (!(paramArrayList.location instanceof TLRPC.TL_fileLocationUnavailable)) && (paramMessageObject.type != 13) && ((paramMessageObject == null) || (!paramMessageObject.isSecretMedia()))) {
          break label1898;
        }
        this.replyImageView.setImageBitmap(null);
        this.replyImageLocation = null;
        this.replyImageView.setVisibility(4);
        i = AndroidUtilities.dp(52.0F);
        ((FrameLayout.LayoutParams)localObject2).leftMargin = i;
      }
      for (((FrameLayout.LayoutParams)localObject1).leftMargin = i;; ((FrameLayout.LayoutParams)localObject1).leftMargin = i)
      {
        this.replyNameTextView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
        this.replyObjectTextView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        this.chatActivityEnterView.showTopView(paramBoolean3, paramBoolean1);
        return;
        paramMessageObject = MessagesController.getInstance().getChat(Integer.valueOf(((MessageObject)localObject1).messageOwner.to_id.channel_id));
        if (paramMessageObject == null) {
          break;
        }
        paramMessageObject = paramMessageObject.title;
        break label181;
        paramMessageObject = (MessageObject)localObject1;
        if (((MessageObject)localObject1).messageText == null) {
          break label257;
        }
        paramArrayList = ((MessageObject)localObject1).messageText.toString();
        paramMessageObject = paramArrayList;
        if (paramArrayList.length() > 150) {
          paramMessageObject = paramArrayList.substring(0, 150);
        }
        paramMessageObject = paramMessageObject.replace('\n', ' ');
        this.replyObjectTextView.setText(Emoji.replaceEmoji(paramMessageObject, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
        paramMessageObject = (MessageObject)localObject1;
        break label257;
        if (localObject2 != null)
        {
          if (((ArrayList)localObject2).isEmpty()) {
            break;
          }
          this.replyingMessageObject = null;
          this.chatActivityEnterView.setReplyingMessageObject(null);
          this.forwardingMessages = ((ArrayList)localObject2);
          if (this.foundWebPage != null) {
            break;
          }
          this.chatActivityEnterView.setForceShowSendButton(true, paramBoolean3);
          paramWebPage = new ArrayList();
          this.replyIconImageView.setImageResource(2130837686);
          paramMessageObject = (MessageObject)((ArrayList)localObject2).get(0);
          if (paramMessageObject.isFromUser())
          {
            paramWebPage.add(Integer.valueOf(paramMessageObject.messageOwner.from_id));
            i = ((MessageObject)((ArrayList)localObject2).get(0)).type;
            j = 1;
            if (j >= ((ArrayList)localObject2).size()) {
              break label800;
            }
            paramMessageObject = (MessageObject)((ArrayList)localObject2).get(j);
            if (!paramMessageObject.isFromUser()) {
              break label782;
            }
          }
          for (paramMessageObject = Integer.valueOf(paramMessageObject.messageOwner.from_id);; paramMessageObject = Integer.valueOf(-paramMessageObject.messageOwner.to_id.channel_id))
          {
            if (!paramWebPage.contains(paramMessageObject)) {
              paramWebPage.add(paramMessageObject);
            }
            int k = i;
            if (((MessageObject)((ArrayList)localObject2).get(j)).type != i) {
              k = -1;
            }
            j += 1;
            i = k;
            break label666;
            paramWebPage.add(Integer.valueOf(-paramMessageObject.messageOwner.to_id.channel_id));
            break;
          }
          StringBuilder localStringBuilder = new StringBuilder();
          int j = 0;
          if (j < paramWebPage.size())
          {
            Integer localInteger = (Integer)paramWebPage.get(j);
            paramMessageObject = null;
            paramArrayList = null;
            if (localInteger.intValue() > 0)
            {
              paramArrayList = MessagesController.getInstance().getUser(localInteger);
              if ((paramArrayList != null) || (paramMessageObject != null)) {
                break label891;
              }
            }
            for (;;)
            {
              j += 1;
              break;
              paramMessageObject = MessagesController.getInstance().getChat(Integer.valueOf(-localInteger.intValue()));
              break label855;
              if (paramWebPage.size() == 1)
              {
                if (paramArrayList != null) {
                  localStringBuilder.append(UserObject.getUserName(paramArrayList));
                } else {
                  localStringBuilder.append(paramMessageObject.title);
                }
              }
              else
              {
                if ((paramWebPage.size() != 2) && (localStringBuilder.length() != 0)) {
                  break label1053;
                }
                if (localStringBuilder.length() > 0) {
                  localStringBuilder.append(", ");
                }
                if (paramArrayList != null)
                {
                  if ((paramArrayList.first_name != null) && (paramArrayList.first_name.length() > 0)) {
                    localStringBuilder.append(paramArrayList.first_name);
                  } else if ((paramArrayList.last_name != null) && (paramArrayList.last_name.length() > 0)) {
                    localStringBuilder.append(paramArrayList.last_name);
                  } else {
                    localStringBuilder.append(" ");
                  }
                }
                else {
                  localStringBuilder.append(paramMessageObject.title);
                }
              }
            }
            localStringBuilder.append(" ");
            localStringBuilder.append(LocaleController.formatPluralString("AndOther", paramWebPage.size() - 1));
          }
          this.replyNameTextView.setText(localStringBuilder);
          if ((i == -1) || (i == 0) || (i == 10) || (i == 11))
          {
            if ((((ArrayList)localObject2).size() == 1) && (((MessageObject)((ArrayList)localObject2).get(0)).messageText != null))
            {
              paramMessageObject = (MessageObject)((ArrayList)localObject2).get(0);
              if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
              {
                this.replyObjectTextView.setText(Emoji.replaceEmoji(paramMessageObject.messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
                paramMessageObject = (MessageObject)localObject1;
                break label257;
              }
              paramArrayList = paramMessageObject.messageText.toString();
              paramMessageObject = paramArrayList;
              if (paramArrayList.length() > 150) {
                paramMessageObject = paramArrayList.substring(0, 150);
              }
              paramMessageObject = paramMessageObject.replace('\n', ' ');
              this.replyObjectTextView.setText(Emoji.replaceEmoji(paramMessageObject, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
              paramMessageObject = (MessageObject)localObject1;
              break label257;
            }
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMessage", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            break label257;
          }
          if (i == 1)
          {
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedPhoto", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            if (((ArrayList)localObject2).size() != 1) {
              break label257;
            }
            paramMessageObject = (MessageObject)((ArrayList)localObject2).get(0);
            break label257;
          }
          if (i == 4)
          {
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedLocation", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            break label257;
          }
          if (i == 3)
          {
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedVideo", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            if (((ArrayList)localObject2).size() != 1) {
              break label257;
            }
            paramMessageObject = (MessageObject)((ArrayList)localObject2).get(0);
            break label257;
          }
          if (i == 12)
          {
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedContact", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            break label257;
          }
          if (i == 2)
          {
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedAudio", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            break label257;
          }
          if (i == 14)
          {
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMusic", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            break label257;
          }
          if (i == 13)
          {
            this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedSticker", ((ArrayList)localObject2).size()));
            paramMessageObject = (MessageObject)localObject1;
            break label257;
          }
          if (i != 8)
          {
            paramMessageObject = (MessageObject)localObject1;
            if (i != 9) {
              break label257;
            }
          }
          if (((ArrayList)localObject2).size() == 1)
          {
            if (i == 8)
            {
              this.replyObjectTextView.setText(LocaleController.getString("AttachGif", 2131165340));
              paramMessageObject = (MessageObject)localObject1;
              break label257;
            }
            paramMessageObject = FileLoader.getDocumentFileName(((MessageObject)((ArrayList)localObject2).get(0)).getDocument());
            if (paramMessageObject.length() != 0) {
              this.replyObjectTextView.setText(paramMessageObject);
            }
            paramMessageObject = (MessageObject)((ArrayList)localObject2).get(0);
            break label257;
          }
          this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedFile", ((ArrayList)localObject2).size()));
          paramMessageObject = (MessageObject)localObject1;
          break label257;
        }
        this.replyIconImageView.setImageResource(2130837788);
        if ((paramWebPage instanceof TLRPC.TL_webPagePending))
        {
          this.replyNameTextView.setText(LocaleController.getString("GettingLinkInfo", 2131165714));
          this.replyObjectTextView.setText(this.pendingLinkSearchString);
          paramMessageObject = (MessageObject)localObject1;
          break label257;
        }
        if (paramWebPage.site_name != null)
        {
          this.replyNameTextView.setText(paramWebPage.site_name);
          if (paramWebPage.description == null) {
            break label1829;
          }
          this.replyObjectTextView.setText(paramWebPage.description);
        }
        for (;;)
        {
          this.chatActivityEnterView.setWebPage(paramWebPage, true);
          paramMessageObject = (MessageObject)localObject1;
          break;
          if (paramWebPage.title != null)
          {
            this.replyNameTextView.setText(paramWebPage.title);
            break label1751;
          }
          this.replyNameTextView.setText(LocaleController.getString("LinkPreview", 2131165832));
          break label1751;
          if ((paramWebPage.title != null) && (paramWebPage.site_name != null)) {
            this.replyObjectTextView.setText(paramWebPage.title);
          } else if (paramWebPage.author != null) {
            this.replyObjectTextView.setText(paramWebPage.author);
          } else {
            this.replyObjectTextView.setText(paramWebPage.display_url);
          }
        }
        this.replyImageLocation = paramArrayList.location;
        this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable)null);
        this.replyImageView.setVisibility(0);
        i = AndroidUtilities.dp(96.0F);
        ((FrameLayout.LayoutParams)localObject2).leftMargin = i;
      }
    } while ((this.replyingMessageObject == null) && (this.forwardingMessages == null) && (this.foundWebPage == null));
    if ((this.replyingMessageObject != null) && ((this.replyingMessageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardForceReply))) {
      ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("answered_" + this.dialog_id, this.replyingMessageObject.getId()).commit();
    }
    if (this.foundWebPage != null)
    {
      this.foundWebPage = null;
      paramMessageObject = this.chatActivityEnterView;
      if (!paramBoolean2) {}
      for (paramBoolean1 = true;; paramBoolean1 = false)
      {
        paramMessageObject.setWebPage(null, paramBoolean1);
        if ((paramWebPage == null) || ((this.replyingMessageObject == null) && (this.forwardingMessages == null))) {
          break;
        }
        showReplyPanel(true, this.replyingMessageObject, this.forwardingMessages, null, false, true);
        return;
      }
    }
    if (this.forwardingMessages != null) {
      forwardMessages(this.forwardingMessages, false);
    }
    this.chatActivityEnterView.setForceShowSendButton(false, paramBoolean3);
    this.chatActivityEnterView.hideTopView(paramBoolean3);
    this.chatActivityEnterView.setReplyingMessageObject(null);
    this.replyingMessageObject = null;
    this.forwardingMessages = null;
    this.replyImageLocation = null;
  }
  
  public void updatePhotoAtIndex(int paramInt) {}
  
  public void willHidePhotoViewer() {}
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt) {}
  
  public class ChatActivityAdapter
    extends RecyclerView.Adapter
  {
    private int botInfoRow = -1;
    private boolean isBot;
    private int loadingDownRow;
    private int loadingUpRow;
    private Context mContext;
    private int messagesEndRow;
    private int messagesStartRow;
    private int rowCount;
    
    public ChatActivityAdapter(Context paramContext)
    {
      this.mContext = paramContext;
      if ((ChatActivity.this.currentUser != null) && (ChatActivity.this.currentUser.bot)) {}
      for (boolean bool = true;; bool = false)
      {
        this.isBot = bool;
        return;
      }
    }
    
    public int getItemCount()
    {
      return this.rowCount;
    }
    
    public long getItemId(int paramInt)
    {
      return -1L;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= this.messagesStartRow) && (paramInt < this.messagesEndRow)) {
        return ((MessageObject)ChatActivity.this.messages.get(ChatActivity.this.messages.size() - (paramInt - this.messagesStartRow) - 1)).contentType;
      }
      if (paramInt == this.botInfoRow) {
        return 3;
      }
      return 4;
    }
    
    public void notifyDataSetChanged()
    {
      updateRows();
      try
      {
        super.notifyDataSetChanged();
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void notifyItemChanged(int paramInt)
    {
      updateRows();
      try
      {
        super.notifyItemChanged(paramInt);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void notifyItemInserted(int paramInt)
    {
      updateRows();
      try
      {
        super.notifyItemInserted(paramInt);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemMoved(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemRangeChanged(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemRangeInserted(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemRangeRemoved(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void notifyItemRemoved(int paramInt)
    {
      updateRows();
      try
      {
        super.notifyItemRemoved(paramInt);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Object localObject;
      if (paramInt == this.botInfoRow)
      {
        localObject = (BotHelpCell)paramViewHolder.itemView;
        if (!ChatActivity.this.botInfo.isEmpty())
        {
          paramViewHolder = ((TLRPC.BotInfo)ChatActivity.this.botInfo.get(Integer.valueOf(ChatActivity.this.currentUser.id))).description;
          ((BotHelpCell)localObject).setText(paramViewHolder);
        }
      }
      View localView;
      label252:
      label278:
      label286:
      label313:
      label332:
      label343:
      label456:
      label461:
      label484:
      label490:
      label496:
      label502:
      label508:
      label514:
      do
      {
        do
        {
          return;
          paramViewHolder = null;
          break;
          if ((paramInt == this.loadingDownRow) || (paramInt == this.loadingUpRow))
          {
            paramViewHolder = (ChatLoadingCell)paramViewHolder.itemView;
            if (ChatActivity.this.loadsCount > 1) {}
            for (bool1 = true;; bool1 = false)
            {
              paramViewHolder.setProgressVisible(bool1);
              return;
            }
          }
        } while ((paramInt < this.messagesStartRow) || (paramInt >= this.messagesEndRow));
        localObject = (MessageObject)ChatActivity.this.messages.get(ChatActivity.this.messages.size() - (paramInt - this.messagesStartRow) - 1);
        localView = paramViewHolder.itemView;
        int j = 0;
        int i = 0;
        paramInt = 0;
        boolean bool2;
        if (ChatActivity.this.actionBar.isActionModeShowed()) {
          if (ChatActivity.this.chatActivityEnterView != null)
          {
            paramViewHolder = ChatActivity.this.chatActivityEnterView.getEditingMessageObject();
            if (paramViewHolder != localObject)
            {
              paramViewHolder = ChatActivity.this.selectedMessagesIds;
              if (((MessageObject)localObject).getDialogId() != ChatActivity.this.dialog_id) {
                break label456;
              }
              paramInt = 0;
              if (!paramViewHolder[paramInt].containsKey(Integer.valueOf(((MessageObject)localObject).getId()))) {
                break label461;
              }
            }
            localView.setBackgroundColor(1714664933);
            paramInt = 1;
            j = 1;
            i = paramInt;
            paramInt = j;
            if (!(localView instanceof ChatMessageCell)) {
              break label514;
            }
            paramViewHolder = (ChatMessageCell)localView;
            if (ChatActivity.this.currentChat == null) {
              break label484;
            }
            bool1 = true;
            paramViewHolder.isChat = bool1;
            paramViewHolder.setMessageObject((MessageObject)localObject);
            if (paramInt != 0) {
              break label490;
            }
            bool1 = true;
            if ((paramInt == 0) || (i == 0)) {
              break label496;
            }
            bool2 = true;
            paramViewHolder.setCheckPressed(bool1, bool2);
            if (((localView instanceof ChatMessageCell)) && (MediaController.getInstance().canDownloadMedia(2))) {
              ((ChatMessageCell)localView).downloadAudioIfNeed();
            }
            if ((ChatActivity.this.highlightMessageId == Integer.MAX_VALUE) || (((MessageObject)localObject).getId() != ChatActivity.this.highlightMessageId)) {
              break label502;
            }
          }
        }
        for (boolean bool1 = true;; bool1 = false)
        {
          paramViewHolder.setHighlighted(bool1);
          if ((ChatActivity.this.searchContainer == null) || (ChatActivity.this.searchContainer.getVisibility() != 0) || (MessagesSearchQuery.getLastSearchQuery() == null)) {
            break label508;
          }
          paramViewHolder.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
          return;
          paramViewHolder = null;
          break;
          paramInt = 1;
          break label252;
          localView.setBackgroundColor(0);
          paramInt = i;
          break label278;
          localView.setBackgroundColor(0);
          i = j;
          break label286;
          bool1 = false;
          break label313;
          bool1 = false;
          break label332;
          bool2 = false;
          break label343;
        }
        paramViewHolder.setHighlightedText(null);
        return;
        if ((localView instanceof ChatActionCell))
        {
          ((ChatActionCell)localView).setMessageObject((MessageObject)localObject);
          return;
        }
      } while (!(localView instanceof ChatUnreadCell));
      ((ChatUnreadCell)localView).setText(LocaleController.formatPluralString("NewMessages", ChatActivity.this.unread_to_load));
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      Object localObject;
      if (paramInt == 0) {
        if (!ChatActivity.this.chatMessageCellsCache.isEmpty())
        {
          localObject = (View)ChatActivity.this.chatMessageCellsCache.get(0);
          ChatActivity.this.chatMessageCellsCache.remove(0);
          ChatMessageCell localChatMessageCell = (ChatMessageCell)localObject;
          localChatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate()
          {
            public boolean canPerformActions()
            {
              return (ChatActivity.this.actionBar != null) && (!ChatActivity.this.actionBar.isActionModeShowed());
            }
            
            public void didLongPressed(ChatMessageCell paramAnonymousChatMessageCell)
            {
              ChatActivity.this.createMenu(paramAnonymousChatMessageCell, false);
            }
            
            public void didPressedBotButton(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.KeyboardButton paramAnonymousKeyboardButton)
            {
              if ((ChatActivity.this.getParentActivity() == null) || ((ChatActivity.this.bottomOverlayChat.getVisibility() == 0) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline)) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonCallback)) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonGame)) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonUrl)))) {
                return;
              }
              ChatActivity.this.chatActivityEnterView.didPressedBotButton(paramAnonymousKeyboardButton, paramAnonymousChatMessageCell.getMessageObject(), paramAnonymousChatMessageCell.getMessageObject());
            }
            
            public void didPressedCancelSendButton(ChatMessageCell paramAnonymousChatMessageCell)
            {
              paramAnonymousChatMessageCell = paramAnonymousChatMessageCell.getMessageObject();
              if (paramAnonymousChatMessageCell.messageOwner.send_state != 0) {
                SendMessagesHelper.getInstance().cancelSendingMessage(paramAnonymousChatMessageCell);
              }
            }
            
            public void didPressedChannelAvatar(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.Chat paramAnonymousChat, int paramAnonymousInt)
            {
              if (ChatActivity.this.actionBar.isActionModeShowed()) {
                ChatActivity.this.processRowSelect(paramAnonymousChatMessageCell);
              }
              do
              {
                do
                {
                  return;
                } while ((paramAnonymousChat == null) || (paramAnonymousChat == ChatActivity.this.currentChat));
                paramAnonymousChatMessageCell = new Bundle();
                paramAnonymousChatMessageCell.putInt("chat_id", paramAnonymousChat.id);
                if (paramAnonymousInt != 0) {
                  paramAnonymousChatMessageCell.putInt("message_id", paramAnonymousInt);
                }
              } while (!MessagesController.checkCanOpenChat(paramAnonymousChatMessageCell, ChatActivity.this));
              ChatActivity.this.presentFragment(new ChatActivity(paramAnonymousChatMessageCell), true);
            }
            
            public void didPressedImage(ChatMessageCell paramAnonymousChatMessageCell)
            {
              Object localObject2 = paramAnonymousChatMessageCell.getMessageObject();
              if (((MessageObject)localObject2).isSendError()) {
                ChatActivity.this.createMenu(paramAnonymousChatMessageCell, false);
              }
              label282:
              label286:
              do
              {
                do
                {
                  Object localObject1;
                  for (;;)
                  {
                    return;
                    if (!((MessageObject)localObject2).isSending())
                    {
                      if (((MessageObject)localObject2).type == 13)
                      {
                        localObject1 = ChatActivity.this;
                        Activity localActivity = ChatActivity.this.getParentActivity();
                        ChatActivity localChatActivity = ChatActivity.this;
                        localObject2 = ((MessageObject)localObject2).getInputStickerSet();
                        if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0) {}
                        for (paramAnonymousChatMessageCell = ChatActivity.this.chatActivityEnterView;; paramAnonymousChatMessageCell = null)
                        {
                          ((ChatActivity)localObject1).showDialog(new StickersAlert(localActivity, localChatActivity, (TLRPC.InputStickerSet)localObject2, null, paramAnonymousChatMessageCell));
                          return;
                        }
                      }
                      if (((Build.VERSION.SDK_INT < 16) || (!((MessageObject)localObject2).isVideo())) && (((MessageObject)localObject2).type != 1) && ((((MessageObject)localObject2).type != 0) || (((MessageObject)localObject2).isWebpageDocument())) && (!((MessageObject)localObject2).isGif())) {
                        break;
                      }
                      PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                      paramAnonymousChatMessageCell = PhotoViewer.getInstance();
                      long l1;
                      if (((MessageObject)localObject2).type != 0)
                      {
                        l1 = ChatActivity.this.dialog_id;
                        if (((MessageObject)localObject2).type == 0) {
                          break label282;
                        }
                      }
                      for (long l2 = ChatActivity.this.mergeDialogId;; l2 = 0L)
                      {
                        if (!paramAnonymousChatMessageCell.openPhoto((MessageObject)localObject2, l1, l2, ChatActivity.this)) {
                          break label286;
                        }
                        PhotoViewer.getInstance().setParentChatActivity(ChatActivity.this);
                        return;
                        l1 = 0L;
                        break;
                      }
                    }
                  }
                  if (((MessageObject)localObject2).type == 3)
                  {
                    ChatActivity.this.sendSecretMessageRead((MessageObject)localObject2);
                    localObject1 = null;
                    paramAnonymousChatMessageCell = (ChatMessageCell)localObject1;
                    for (;;)
                    {
                      try
                      {
                        if (((MessageObject)localObject2).messageOwner.attachPath != null)
                        {
                          paramAnonymousChatMessageCell = (ChatMessageCell)localObject1;
                          if (((MessageObject)localObject2).messageOwner.attachPath.length() != 0) {
                            paramAnonymousChatMessageCell = new File(((MessageObject)localObject2).messageOwner.attachPath);
                          }
                        }
                        if (paramAnonymousChatMessageCell != null)
                        {
                          localObject1 = paramAnonymousChatMessageCell;
                          if (paramAnonymousChatMessageCell.exists()) {}
                        }
                        else
                        {
                          localObject1 = FileLoader.getPathToMessage(((MessageObject)localObject2).messageOwner);
                        }
                        paramAnonymousChatMessageCell = new Intent("android.intent.action.VIEW");
                        if (Build.VERSION.SDK_INT >= 24)
                        {
                          paramAnonymousChatMessageCell.setFlags(1);
                          paramAnonymousChatMessageCell.setDataAndType(FileProvider.getUriForFile(ChatActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", (File)localObject1), "video/mp4");
                          ChatActivity.this.getParentActivity().startActivityForResult(paramAnonymousChatMessageCell, 500);
                          return;
                        }
                      }
                      catch (Exception paramAnonymousChatMessageCell)
                      {
                        ChatActivity.this.alertUserOpenError((MessageObject)localObject2);
                        return;
                      }
                      paramAnonymousChatMessageCell.setDataAndType(Uri.fromFile((File)localObject1), "video/mp4");
                    }
                  }
                  if (((MessageObject)localObject2).type != 4) {
                    break;
                  }
                } while (!AndroidUtilities.isGoogleMapsInstalled(ChatActivity.this));
                paramAnonymousChatMessageCell = new LocationActivity();
                paramAnonymousChatMessageCell.setMessageObject((MessageObject)localObject2);
                ChatActivity.this.presentFragment(paramAnonymousChatMessageCell);
                return;
              } while ((((MessageObject)localObject2).type != 9) && (((MessageObject)localObject2).type != 0));
              try
              {
                AndroidUtilities.openForView((MessageObject)localObject2, ChatActivity.this.getParentActivity());
                return;
              }
              catch (Exception paramAnonymousChatMessageCell)
              {
                ChatActivity.this.alertUserOpenError((MessageObject)localObject2);
              }
            }
            
            public void didPressedOther(ChatMessageCell paramAnonymousChatMessageCell)
            {
              ChatActivity.this.createMenu(paramAnonymousChatMessageCell, true);
            }
            
            public void didPressedReplyMessage(ChatMessageCell paramAnonymousChatMessageCell, int paramAnonymousInt)
            {
              paramAnonymousChatMessageCell = paramAnonymousChatMessageCell.getMessageObject();
              ChatActivity localChatActivity = ChatActivity.this;
              int j = paramAnonymousChatMessageCell.getId();
              if (paramAnonymousChatMessageCell.getDialogId() == ChatActivity.this.mergeDialogId) {}
              for (int i = 1;; i = 0)
              {
                localChatActivity.scrollToMessageId(paramAnonymousInt, j, true, i);
                return;
              }
            }
            
            public void didPressedShare(ChatMessageCell paramAnonymousChatMessageCell)
            {
              if (ChatActivity.this.getParentActivity() == null) {
                return;
              }
              if (ChatActivity.this.chatActivityEnterView != null) {
                ChatActivity.this.chatActivityEnterView.closeKeyboard();
              }
              ChatActivity localChatActivity = ChatActivity.this;
              Context localContext = ChatActivity.ChatActivityAdapter.this.mContext;
              paramAnonymousChatMessageCell = paramAnonymousChatMessageCell.getMessageObject();
              if ((ChatObject.isChannel(ChatActivity.this.currentChat)) && (!ChatActivity.this.currentChat.megagroup) && (ChatActivity.this.currentChat.username != null) && (ChatActivity.this.currentChat.username.length() > 0)) {}
              for (boolean bool = true;; bool = false)
              {
                localChatActivity.showDialog(new ShareAlert(localContext, paramAnonymousChatMessageCell, null, bool, null));
                return;
              }
            }
            
            public void didPressedUrl(final MessageObject paramAnonymousMessageObject, ClickableSpan paramAnonymousClickableSpan, boolean paramAnonymousBoolean)
            {
              boolean bool = true;
              if (paramAnonymousClickableSpan == null) {}
              do
              {
                do
                {
                  do
                  {
                    return;
                    if (!(paramAnonymousClickableSpan instanceof URLSpanUserMention)) {
                      break;
                    }
                    paramAnonymousMessageObject = MessagesController.getInstance().getUser(Utilities.parseInt(((URLSpanUserMention)paramAnonymousClickableSpan).getURL()));
                  } while (paramAnonymousMessageObject == null);
                  MessagesController.openChatOrProfileWith(paramAnonymousMessageObject, null, ChatActivity.this, 0, false);
                  return;
                  if (!(paramAnonymousClickableSpan instanceof URLSpanNoUnderline)) {
                    break;
                  }
                  paramAnonymousClickableSpan = ((URLSpanNoUnderline)paramAnonymousClickableSpan).getURL();
                  if (paramAnonymousClickableSpan.startsWith("@"))
                  {
                    MessagesController.openByUserName(paramAnonymousClickableSpan.substring(1), ChatActivity.this, 0);
                    return;
                  }
                  if (paramAnonymousClickableSpan.startsWith("#"))
                  {
                    if (ChatObject.isChannel(ChatActivity.this.currentChat))
                    {
                      ChatActivity.this.openSearchWithText(paramAnonymousClickableSpan);
                      return;
                    }
                    paramAnonymousMessageObject = new DialogsActivity(null);
                    paramAnonymousMessageObject.setSearchString(paramAnonymousClickableSpan);
                    ChatActivity.this.presentFragment(paramAnonymousMessageObject);
                    return;
                  }
                } while ((!paramAnonymousClickableSpan.startsWith("/")) || (!URLSpanBotCommand.enabled));
                Object localObject = ChatActivity.this.chatActivityEnterView;
                if ((ChatActivity.this.currentChat != null) && (ChatActivity.this.currentChat.megagroup)) {}
                for (bool = true;; bool = false)
                {
                  ((ChatActivityEnterView)localObject).setCommand(paramAnonymousMessageObject, paramAnonymousClickableSpan, paramAnonymousBoolean, bool);
                  return;
                }
                paramAnonymousMessageObject = ((URLSpan)paramAnonymousClickableSpan).getURL();
                if (paramAnonymousBoolean)
                {
                  paramAnonymousClickableSpan = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                  paramAnonymousClickableSpan.setTitle(paramAnonymousMessageObject);
                  localObject = LocaleController.getString("Open", 2131166056);
                  String str = LocaleController.getString("Copy", 2131165531);
                  paramAnonymousMessageObject = new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                      boolean bool = true;
                      if (paramAnonymous2Int == 0)
                      {
                        paramAnonymous2DialogInterface = ChatActivity.this.getParentActivity();
                        str = paramAnonymousMessageObject;
                        if (ChatActivity.this.inlineReturn == 0L) {
                          Browser.openUrl(paramAnonymous2DialogInterface, str, bool);
                        }
                      }
                      while (paramAnonymous2Int != 1) {
                        for (;;)
                        {
                          return;
                          bool = false;
                        }
                      }
                      String str = paramAnonymousMessageObject;
                      if (str.startsWith("mailto:")) {
                        paramAnonymous2DialogInterface = str.substring(7);
                      }
                      for (;;)
                      {
                        AndroidUtilities.addToClipboard(paramAnonymous2DialogInterface);
                        return;
                        paramAnonymous2DialogInterface = str;
                        if (str.startsWith("tel:")) {
                          paramAnonymous2DialogInterface = str.substring(4);
                        }
                      }
                    }
                  };
                  paramAnonymousClickableSpan.setItems(new CharSequence[] { localObject, str }, paramAnonymousMessageObject);
                  ChatActivity.this.showDialog(paramAnonymousClickableSpan.create());
                  return;
                }
              } while (!((URLSpan)paramAnonymousClickableSpan).getURL().contains(""));
              if ((paramAnonymousClickableSpan instanceof URLSpanReplacement))
              {
                ChatActivity.this.showOpenUrlAlert(((URLSpanReplacement)paramAnonymousClickableSpan).getURL(), true);
                return;
              }
              if ((paramAnonymousClickableSpan instanceof URLSpan))
              {
                paramAnonymousClickableSpan = ChatActivity.this.getParentActivity();
                if (ChatActivity.this.inlineReturn == 0L) {}
                for (paramAnonymousBoolean = bool;; paramAnonymousBoolean = false)
                {
                  Browser.openUrl(paramAnonymousClickableSpan, paramAnonymousMessageObject, paramAnonymousBoolean);
                  return;
                }
              }
              paramAnonymousClickableSpan.onClick(ChatActivity.this.fragmentView);
            }
            
            public void didPressedUserAvatar(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.User paramAnonymousUser)
            {
              if (ChatActivity.this.actionBar.isActionModeShowed()) {
                ChatActivity.this.processRowSelect(paramAnonymousChatMessageCell);
              }
              while ((paramAnonymousUser == null) || (paramAnonymousUser.id == UserConfig.getClientUserId())) {
                return;
              }
              paramAnonymousChatMessageCell = new Bundle();
              paramAnonymousChatMessageCell.putInt("user_id", paramAnonymousUser.id);
              paramAnonymousChatMessageCell = new ProfileActivity(paramAnonymousChatMessageCell);
              if ((ChatActivity.this.currentUser != null) && (ChatActivity.this.currentUser.id == paramAnonymousUser.id)) {}
              for (boolean bool = true;; bool = false)
              {
                paramAnonymousChatMessageCell.setPlayProfileAnimation(bool);
                ChatActivity.this.presentFragment(paramAnonymousChatMessageCell);
                return;
              }
            }
            
            public void didPressedViaBot(ChatMessageCell paramAnonymousChatMessageCell, String paramAnonymousString)
            {
              if (((ChatActivity.this.bottomOverlayChat != null) && (ChatActivity.this.bottomOverlayChat.getVisibility() == 0)) || ((ChatActivity.this.bottomOverlay != null) && (ChatActivity.this.bottomOverlay.getVisibility() == 0))) {}
              while ((ChatActivity.this.chatActivityEnterView == null) || (paramAnonymousString == null) || (paramAnonymousString.length() <= 0)) {
                return;
              }
              ChatActivity.this.chatActivityEnterView.setFieldText("@" + paramAnonymousString + " ");
              ChatActivity.this.chatActivityEnterView.openKeyboard();
            }
            
            public void needOpenWebView(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4, int paramAnonymousInt1, int paramAnonymousInt2)
            {
              BottomSheet.Builder localBuilder = new BottomSheet.Builder(ChatActivity.ChatActivityAdapter.this.mContext);
              localBuilder.setCustomView(new WebFrameLayout(ChatActivity.ChatActivityAdapter.this.mContext, localBuilder.create(), paramAnonymousString2, paramAnonymousString3, paramAnonymousString4, paramAnonymousString1, paramAnonymousInt1, paramAnonymousInt2));
              localBuilder.setUseFullWidth(true);
              ChatActivity.this.showDialog(localBuilder.create());
            }
            
            public boolean needPlayAudio(MessageObject paramAnonymousMessageObject)
            {
              if (paramAnonymousMessageObject.isVoice())
              {
                boolean bool = MediaController.getInstance().playAudio(paramAnonymousMessageObject);
                MediaController localMediaController = MediaController.getInstance();
                if (bool) {}
                for (paramAnonymousMessageObject = ChatActivity.this.createVoiceMessagesPlaylist(paramAnonymousMessageObject, false);; paramAnonymousMessageObject = null)
                {
                  localMediaController.setVoiceMessagesPlaylist(paramAnonymousMessageObject, false);
                  return bool;
                }
              }
              if (paramAnonymousMessageObject.isMusic()) {
                return MediaController.getInstance().setPlaylist(ChatActivity.this.messages, paramAnonymousMessageObject);
              }
              return false;
            }
          });
          paramViewGroup = (ViewGroup)localObject;
          if (ChatActivity.this.currentEncryptedChat == null)
          {
            localChatMessageCell.setAllowAssistant(true);
            paramViewGroup = (ViewGroup)localObject;
          }
        }
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new Holder(paramViewGroup);
        localObject = new ChatMessageCell(this.mContext);
        break;
        if (paramInt == 1)
        {
          paramViewGroup = new ChatActionCell(this.mContext);
          ((ChatActionCell)paramViewGroup).setDelegate(new ChatActionCell.ChatActionCellDelegate()
          {
            public void didClickedImage(ChatActionCell paramAnonymousChatActionCell)
            {
              paramAnonymousChatActionCell = paramAnonymousChatActionCell.getMessageObject();
              PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
              TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(paramAnonymousChatActionCell.photoThumbs, 640);
              if (localPhotoSize != null)
              {
                PhotoViewer.getInstance().openPhoto(localPhotoSize.location, ChatActivity.this);
                return;
              }
              PhotoViewer.getInstance().openPhoto(paramAnonymousChatActionCell, 0L, 0L, ChatActivity.this);
            }
            
            public void didLongPressed(ChatActionCell paramAnonymousChatActionCell)
            {
              ChatActivity.this.createMenu(paramAnonymousChatActionCell, false);
            }
            
            public void didPressedBotButton(MessageObject paramAnonymousMessageObject, TLRPC.KeyboardButton paramAnonymousKeyboardButton)
            {
              if ((ChatActivity.this.getParentActivity() == null) || ((ChatActivity.this.bottomOverlayChat.getVisibility() == 0) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline)) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonCallback)) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonGame)) && (!(paramAnonymousKeyboardButton instanceof TLRPC.TL_keyboardButtonUrl)))) {
                return;
              }
              ChatActivity.this.chatActivityEnterView.didPressedBotButton(paramAnonymousKeyboardButton, paramAnonymousMessageObject, paramAnonymousMessageObject);
            }
            
            public void didPressedReplyMessage(ChatActionCell paramAnonymousChatActionCell, int paramAnonymousInt)
            {
              paramAnonymousChatActionCell = paramAnonymousChatActionCell.getMessageObject();
              ChatActivity localChatActivity = ChatActivity.this;
              int j = paramAnonymousChatActionCell.getId();
              if (paramAnonymousChatActionCell.getDialogId() == ChatActivity.this.mergeDialogId) {}
              for (int i = 1;; i = 0)
              {
                localChatActivity.scrollToMessageId(paramAnonymousInt, j, true, i);
                return;
              }
            }
            
            public void needOpenUserProfile(int paramAnonymousInt)
            {
              boolean bool = true;
              if (paramAnonymousInt < 0)
              {
                localObject = new Bundle();
                ((Bundle)localObject).putInt("chat_id", -paramAnonymousInt);
                if (MessagesController.checkCanOpenChat((Bundle)localObject, ChatActivity.this)) {
                  ChatActivity.this.presentFragment(new ChatActivity((Bundle)localObject), true);
                }
              }
              while (paramAnonymousInt == UserConfig.getClientUserId()) {
                return;
              }
              Object localObject = new Bundle();
              ((Bundle)localObject).putInt("user_id", paramAnonymousInt);
              if ((ChatActivity.this.currentEncryptedChat != null) && (paramAnonymousInt == ChatActivity.this.currentUser.id)) {
                ((Bundle)localObject).putLong("dialog_id", ChatActivity.this.dialog_id);
              }
              localObject = new ProfileActivity((Bundle)localObject);
              if ((ChatActivity.this.currentUser != null) && (ChatActivity.this.currentUser.id == paramAnonymousInt)) {}
              for (;;)
              {
                ((ProfileActivity)localObject).setPlayProfileAnimation(bool);
                ChatActivity.this.presentFragment((BaseFragment)localObject);
                return;
                bool = false;
              }
            }
          });
        }
        else if (paramInt == 2)
        {
          paramViewGroup = new ChatUnreadCell(this.mContext);
        }
        else if (paramInt == 3)
        {
          paramViewGroup = new BotHelpCell(this.mContext);
          ((BotHelpCell)paramViewGroup).setDelegate(new BotHelpCell.BotHelpCellDelegate()
          {
            public void didPressUrl(String paramAnonymousString)
            {
              if (paramAnonymousString.startsWith("@")) {
                MessagesController.openByUserName(paramAnonymousString.substring(1), ChatActivity.this, 0);
              }
              do
              {
                return;
                if (paramAnonymousString.startsWith("#"))
                {
                  DialogsActivity localDialogsActivity = new DialogsActivity(null);
                  localDialogsActivity.setSearchString(paramAnonymousString);
                  ChatActivity.this.presentFragment(localDialogsActivity);
                  return;
                }
              } while (!paramAnonymousString.startsWith("/"));
              ChatActivity.this.chatActivityEnterView.setCommand(null, paramAnonymousString, false, false);
            }
          });
        }
        else if (paramInt == 4)
        {
          paramViewGroup = new ChatLoadingCell(this.mContext);
        }
      }
    }
    
    public void onViewAttachedToWindow(final RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ChatMessageCell))
      {
        paramViewHolder = (ChatMessageCell)paramViewHolder.itemView;
        paramViewHolder.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            paramViewHolder.getViewTreeObserver().removeOnPreDrawListener(this);
            int m = ChatActivity.this.chatListView.getMeasuredHeight();
            int i = paramViewHolder.getTop();
            paramViewHolder.getBottom();
            if (i >= 0) {}
            for (i = 0;; i = -i)
            {
              int k = paramViewHolder.getMeasuredHeight();
              int j = k;
              if (k > m) {
                j = i + m;
              }
              paramViewHolder.setVisiblePart(i, j - i);
              return true;
            }
          }
        });
        if ((ChatActivity.this.highlightMessageId == Integer.MAX_VALUE) || (paramViewHolder.getMessageObject().getId() != ChatActivity.this.highlightMessageId)) {
          break label72;
        }
      }
      label72:
      for (boolean bool = true;; bool = false)
      {
        paramViewHolder.setHighlighted(bool);
        return;
      }
    }
    
    public void updateRowWithMessageObject(MessageObject paramMessageObject)
    {
      int i = ChatActivity.this.messages.indexOf(paramMessageObject);
      if (i == -1) {
        return;
      }
      notifyItemChanged(this.messagesStartRow + ChatActivity.this.messages.size() - i - 1);
    }
    
    public void updateRows()
    {
      this.rowCount = 0;
      int i;
      if ((ChatActivity.this.currentUser != null) && (ChatActivity.this.currentUser.bot))
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.botInfoRow = i;
        if (ChatActivity.this.messages.isEmpty()) {
          break label222;
        }
        if ((ChatActivity.this.endReached[0] != 0) && ((ChatActivity.this.mergeDialogId == 0L) || (ChatActivity.this.endReached[1] != 0))) {
          break label208;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
      }
      label208:
      for (this.loadingUpRow = i;; this.loadingUpRow = -1)
      {
        this.messagesStartRow = this.rowCount;
        this.rowCount += ChatActivity.this.messages.size();
        this.messagesEndRow = this.rowCount;
        if ((ChatActivity.this.forwardEndReached[0] != 0) && ((ChatActivity.this.mergeDialogId == 0L) || (ChatActivity.this.forwardEndReached[1] != 0))) {
          break label216;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.loadingDownRow = i;
        return;
        this.botInfoRow = -1;
        break;
      }
      label216:
      this.loadingDownRow = -1;
      return;
      label222:
      this.loadingUpRow = -1;
      this.loadingDownRow = -1;
      this.messagesStartRow = -1;
      this.messagesEndRow = -1;
    }
    
    private class Holder
      extends RecyclerView.ViewHolder
    {
      public Holder(View paramView)
      {
        super();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChatActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */