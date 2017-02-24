package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.content.FileProvider;
import android.support.v4.view.InputDeviceCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
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
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
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
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
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
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
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
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_getMessageEditData;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardForceReply;
import org.telegram.tgnet.TLRPC.TL_userFull;
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
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Adapters.StickersAdapter;
import org.telegram.ui.Adapters.StickersAdapter.StickersAdapterDelegate;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.ExtendedGridLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;
import org.telegram.ui.VideoEditorActivity.VideoEditorActivityDelegate;

public class ChatActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate, PhotoViewerProvider {
    private static final int attach_audio = 3;
    private static final int attach_contact = 5;
    private static final int attach_document = 4;
    private static final int attach_gallery = 1;
    private static final int attach_location = 6;
    private static final int attach_photo = 0;
    private static final int attach_video = 2;
    private static final int bot_help = 30;
    private static final int bot_settings = 31;
    private static final int call = 32;
    private static final int chat_enc_timer = 13;
    private static final int chat_menu_attach = 14;
    private static final int clear_history = 15;
    private static final int copy = 10;
    private static final int delete = 12;
    private static final int delete_chat = 16;
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
    private PhotoViewerProvider botContextProvider = new PhotoViewerProvider() {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PlaceProviderObject placeProviderObject = null;
            int i = 0;
            if (index >= 0 && index < ChatActivity.this.botContextResults.size()) {
                int count = ChatActivity.this.mentionListView.getChildCount();
                BotInlineResult result = ChatActivity.this.botContextResults.get(index);
                int a = 0;
                while (a < count) {
                    ImageReceiver imageReceiver = null;
                    View view = ChatActivity.this.mentionListView.getChildAt(a);
                    if (view instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) view;
                        if (cell.getResult() == result) {
                            imageReceiver = cell.getPhotoImage();
                        }
                    }
                    if (imageReceiver != null) {
                        int[] coords = new int[2];
                        view.getLocationInWindow(coords);
                        placeProviderObject = new PlaceProviderObject();
                        placeProviderObject.viewX = coords[0];
                        int i2 = coords[1];
                        if (VERSION.SDK_INT < 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        placeProviderObject.viewY = i2 - i;
                        placeProviderObject.parentView = ChatActivity.this.mentionListView;
                        placeProviderObject.imageReceiver = imageReceiver;
                        placeProviderObject.thumb = imageReceiver.getBitmap();
                        placeProviderObject.radius = imageReceiver.getRoundRadius();
                    } else {
                        a++;
                    }
                }
            }
            return placeProviderObject;
        }

        public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        }

        public void willHidePhotoViewer() {
        }

        public boolean isPhotoChecked(int index) {
            return false;
        }

        public void setPhotoChecked(int index) {
        }

        public boolean cancelButtonPressed() {
            return false;
        }

        public void sendButtonPressed(int index) {
            if (index >= 0 && index < ChatActivity.this.botContextResults.size()) {
                ChatActivity.this.sendBotInlineResult((BotInlineResult) ChatActivity.this.botContextResults.get(index));
            }
        }

        public int getSelectedCount() {
            return 0;
        }

        public void updatePhotoAtIndex(int index) {
        }

        public boolean scaleToFill() {
            return false;
        }

        public boolean allowCaption() {
            return true;
        }
    };
    private ArrayList<Object> botContextResults;
    private HashMap<Integer, BotInfo> botInfo = new HashMap();
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
    private long chatEnterTime = 0;
    private LinearLayoutManager chatLayoutManager;
    private long chatLeaveTime = 0;
    private RecyclerListView chatListView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList();
    private Dialog closeChatDialog;
    private ImageView closePinned;
    private ImageView closeReportSpam;
    private int createUnreadMessageAfterId;
    protected Chat currentChat;
    protected EncryptedChat currentEncryptedChat;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private String currentPicturePath;
    protected User currentUser;
    private long dialog_id;
    private int editingMessageObjectReqId;
    private View emojiButtonRed;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean[] endReached = new boolean[2];
    private boolean first = true;
    private boolean firstLoading = true;
    private int first_unread_id;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private boolean forceScrollToTop;
    private boolean[] forwardEndReached = new boolean[]{true, true};
    private ArrayList<MessageObject> forwardingMessages;
    private MessageObject forwaringMessage;
    private ArrayList<CharSequence> foundUrls;
    private WebPage foundWebPage;
    private TextView gifHintTextView;
    private boolean hasBotsCommands;
    private ActionBarMenuItem headerItem;
    private Runnable hideAlertViewRunnable;
    private int highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private boolean ignoreAttachOnPause;
    protected ChatFull info = null;
    private long inlineReturn;
    private boolean isBroadcast;
    private int lastLoadIndex;
    private int last_message_id = 0;
    private int linkSearchRequestId;
    private boolean loading;
    private boolean loadingForward;
    private boolean loadingFromOldPosition;
    private int loadingPinnedMessage;
    private int loadsCount;
    private int[] maxDate = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private int[] maxMessageId = new int[]{ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID};
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
    private OnItemClickListener mentionsOnItemClickListener;
    private long mergeDialogId;
    protected ArrayList<MessageObject> messages = new ArrayList();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap();
    private HashMap<Integer, MessageObject>[] messagesDict = new HashMap[]{new HashMap(), new HashMap()};
    private int[] minDate = new int[2];
    private int[] minMessageId = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private TextView muteItem;
    private boolean needSelectFromMessageId;
    private int newUnreadMessageCount;
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        public void onItemClick(View view, int position) {
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
                ChatActivity.this.processRowSelect(view);
            } else {
                ChatActivity.this.createMenu(view, true);
            }
        }
    };
    OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
        public boolean onItemClick(View view, int position) {
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
                return false;
            }
            ChatActivity.this.createMenu(view, false);
            return true;
        }
    };
    private boolean openAnimationEnded;
    private boolean openSearchKeyboard;
    private Runnable openSecretPhotoRunnable = null;
    private FrameLayout pagedownButton;
    private ObjectAnimator pagedownButtonAnimation;
    private TextView pagedownButtonCounter;
    private ImageView pagedownButtonImage;
    private boolean pagedownButtonShowedByScroll;
    private boolean paused = true;
    private String pendingLinkSearchString;
    private Runnable pendingWebPageTimeoutRunnable;
    private FileLocation pinnedImageLocation;
    private View pinnedLineView;
    private BackupImageView pinnedMessageImageView;
    private SimpleTextView pinnedMessageNameTextView;
    private MessageObject pinnedMessageObject;
    private SimpleTextView pinnedMessageTextView;
    private FrameLayout pinnedMessageView;
    private AnimatorSet pinnedMessageViewAnimator;
    private PlayerView playerView;
    private RadialProgressView progressBar;
    private FrameLayout progressView;
    private View progressView2;
    private boolean readWhenResume = false;
    private int readWithDate;
    private int readWithMid;
    private AnimatorSet replyButtonAnimation;
    private ImageView replyCloseImageView;
    private ImageView replyIconImageView;
    private FileLocation replyImageLocation;
    private BackupImageView replyImageView;
    private View replyLineView;
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
    private int scrollToMessagePosition = -10000;
    private boolean scrollToTopOnResume;
    private boolean scrollToTopUnReadOnResume;
    private boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    private ImageView searchUpButton;
    private HashMap<Integer, MessageObject>[] selectedMessagesCanCopyIds = new HashMap[]{new HashMap(), new HashMap()};
    private NumberTextView selectedMessagesCountTextView;
    private HashMap<Integer, MessageObject>[] selectedMessagesIds = new HashMap[]{new HashMap(), new HashMap()};
    private MessageObject selectedObject;
    private int startLoadFromMessageId;
    private int startLoadFromMessageOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private String startVideoEdit = null;
    private float startX = 0.0f;
    private float startY = 0.0f;
    private StickersAdapter stickersAdapter;
    private RecyclerListView stickersListView;
    private OnItemClickListener stickersOnItemClickListener;
    private FrameLayout stickersPanel;
    private ImageView stickersPanelArrow;
    private View timeItem2;
    private int topViewWasVisible;
    private MessageObject unreadMessageObject;
    private int unread_to_load;
    private boolean userBlocked = false;
    private Runnable waitingForCharaterEnterRunnable;
    private ArrayList<Integer> waitingForLoad = new ArrayList();
    private boolean waitingForReplyMessageLoad;
    private boolean wasPaused = false;

    public class ChatActivityAdapter extends Adapter {
        private int botInfoRow = -1;
        private boolean isBot;
        private int loadingDownRow;
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
            boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.bot;
            this.isBot = z;
        }

        public void updateRows() {
            this.rowCount = 0;
            if (ChatActivity.this.currentUser == null || !ChatActivity.this.currentUser.bot) {
                this.botInfoRow = -1;
            } else {
                int i = this.rowCount;
                this.rowCount = i + 1;
                this.botInfoRow = i;
            }
            if (ChatActivity.this.messages.isEmpty()) {
                this.loadingUpRow = -1;
                this.loadingDownRow = -1;
                this.messagesStartRow = -1;
                this.messagesEndRow = -1;
                return;
            }
            if (ChatActivity.this.endReached[0] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.endReached[1])) {
                this.loadingUpRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.loadingUpRow = i;
            }
            this.messagesStartRow = this.rowCount;
            this.rowCount += ChatActivity.this.messages.size();
            this.messagesEndRow = this.rowCount;
            if (ChatActivity.this.forwardEndReached[0] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.forwardEndReached[1])) {
                this.loadingDownRow = -1;
                return;
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.loadingDownRow = i;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public long getItemId(int i) {
            return -1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                if (ChatActivity.this.chatMessageCellsCache.isEmpty()) {
                    view = new ChatMessageCell(this.mContext);
                } else {
                    view = (View) ChatActivity.this.chatMessageCellsCache.get(0);
                    ChatActivity.this.chatMessageCellsCache.remove(0);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new ChatMessageCellDelegate() {
                    public void didPressedShare(ChatMessageCell cell) {
                        if (ChatActivity.this.getParentActivity() != null) {
                            if (ChatActivity.this.chatActivityEnterView != null) {
                                ChatActivity.this.chatActivityEnterView.closeKeyboard();
                            }
                            ChatActivity chatActivity = ChatActivity.this;
                            Context access$16100 = ChatActivityAdapter.this.mContext;
                            MessageObject messageObject = cell.getMessageObject();
                            boolean z = ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup && ChatActivity.this.currentChat.username != null && ChatActivity.this.currentChat.username.length() > 0;
                            chatActivity.showDialog(new ShareAlert(access$16100, messageObject, null, z, null, false));
                        }
                    }

                    public boolean needPlayAudio(MessageObject messageObject) {
                        if (!messageObject.isVoice()) {
                            return messageObject.isMusic() ? MediaController.getInstance().setPlaylist(ChatActivity.this.messages, messageObject) : false;
                        } else {
                            boolean result = MediaController.getInstance().playAudio(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? ChatActivity.this.createVoiceMessagesPlaylist(messageObject, false) : null, false);
                            return result;
                        }
                    }

                    public void didPressedChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
                        if (ChatActivity.this.actionBar.isActionModeShowed()) {
                            ChatActivity.this.processRowSelect(cell);
                        } else if (chat != null && chat != ChatActivity.this.currentChat) {
                            Bundle args = new Bundle();
                            args.putInt("chat_id", chat.id);
                            if (postId != 0) {
                                args.putInt("message_id", postId);
                            }
                            if (MessagesController.checkCanOpenChat(args, ChatActivity.this)) {
                                ChatActivity.this.presentFragment(new ChatActivity(args), true);
                            }
                        }
                    }

                    public void didPressedOther(ChatMessageCell cell) {
                        ChatActivity.this.createMenu(cell, true);
                    }

                    public void didPressedUserAvatar(ChatMessageCell cell, User user) {
                        if (ChatActivity.this.actionBar.isActionModeShowed()) {
                            ChatActivity.this.processRowSelect(cell);
                        } else if (user != null && user.id != UserConfig.getClientUserId()) {
                            Bundle args = new Bundle();
                            args.putInt("user_id", user.id);
                            ProfileActivity fragment = new ProfileActivity(args);
                            boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.id == user.id;
                            fragment.setPlayProfileAnimation(z);
                            ChatActivity.this.presentFragment(fragment);
                        }
                    }

                    public void didPressedBotButton(ChatMessageCell cell, KeyboardButton button) {
                        if (ChatActivity.this.getParentActivity() == null) {
                            return;
                        }
                        if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (button instanceof TL_keyboardButtonSwitchInline) || (button instanceof TL_keyboardButtonCallback) || (button instanceof TL_keyboardButtonGame) || (button instanceof TL_keyboardButtonUrl) || (button instanceof TL_keyboardButtonBuy)) {
                            ChatActivity.this.chatActivityEnterView.didPressedBotButton(button, cell.getMessageObject(), cell.getMessageObject());
                        }
                    }

                    public void didPressedCancelSendButton(ChatMessageCell cell) {
                        MessageObject message = cell.getMessageObject();
                        if (message.messageOwner.send_state != 0) {
                            SendMessagesHelper.getInstance().cancelSendingMessage(message);
                        }
                    }

                    public void didLongPressed(ChatMessageCell cell) {
                        ChatActivity.this.createMenu(cell, false);
                    }

                    public boolean canPerformActions() {
                        return (ChatActivity.this.actionBar == null || ChatActivity.this.actionBar.isActionModeShowed()) ? false : true;
                    }

                    public void didPressedUrl(MessageObject messageObject, CharacterStyle url, boolean longPress) {
                        if (url != null) {
                            if (url instanceof URLSpanMono) {
                                ((URLSpanMono) url).copyToClipboard();
                                Toast.makeText(ChatActivity.this.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                            } else if (url instanceof URLSpanUserMention) {
                                User user = MessagesController.getInstance().getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
                                if (user != null) {
                                    MessagesController.openChatOrProfileWith(user, null, ChatActivity.this, 0, false);
                                }
                            } else if (url instanceof URLSpanNoUnderline) {
                                String str = ((URLSpanNoUnderline) url).getURL();
                                if (str.startsWith("@")) {
                                    MessagesController.openByUserName(str.substring(1), ChatActivity.this, 0);
                                } else if (str.startsWith("#")) {
                                    if (ChatObject.isChannel(ChatActivity.this.currentChat)) {
                                        ChatActivity.this.openSearchWithText(str);
                                        return;
                                    }
                                    DialogsActivity fragment = new DialogsActivity(null);
                                    fragment.setSearchString(str);
                                    ChatActivity.this.presentFragment(fragment);
                                } else if (str.startsWith("/") && URLSpanBotCommand.enabled) {
                                    ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                                    boolean z = ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup;
                                    chatActivityEnterView.setCommand(messageObject, str, longPress, z);
                                }
                            } else {
                                final String urlFinal = ((URLSpan) url).getURL();
                                if (longPress) {
                                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                                    builder.setTitle(urlFinal);
                                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            boolean z = true;
                                            if (which == 0) {
                                                Context parentActivity = ChatActivity.this.getParentActivity();
                                                String str = urlFinal;
                                                if (ChatActivity.this.inlineReturn != 0) {
                                                    z = false;
                                                }
                                                Browser.openUrl(parentActivity, str, z);
                                            } else if (which == 1) {
                                                String url = urlFinal;
                                                if (url.startsWith("mailto:")) {
                                                    url = url.substring(7);
                                                } else if (url.startsWith("tel:")) {
                                                    url = url.substring(4);
                                                }
                                                AndroidUtilities.addToClipboard(url);
                                            }
                                        }
                                    });
                                    ChatActivity.this.showDialog(builder.create());
                                } else if (url instanceof URLSpanReplacement) {
                                    ChatActivity.this.showOpenUrlAlert(((URLSpanReplacement) url).getURL(), true);
                                } else if (url instanceof URLSpan) {
                                    if (!(!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.cached_page == null)) {
                                        String lowerUrl = urlFinal.toLowerCase();
                                        String lowerUrl2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                        if (lowerUrl.contains("telegra.ph") && (lowerUrl.contains(lowerUrl2) || lowerUrl2.contains(lowerUrl))) {
                                            ArticleViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                                            ArticleViewer.getInstance().open(messageObject);
                                            return;
                                        }
                                    }
                                    Browser.openUrl(ChatActivity.this.getParentActivity(), urlFinal, ChatActivity.this.inlineReturn == 0);
                                } else if (url instanceof ClickableSpan) {
                                    ((ClickableSpan) url).onClick(ChatActivity.this.fragmentView);
                                }
                            }
                        }
                    }

                    public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
                        EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, title, description, originalUrl, url, w, h);
                    }

                    public void didPressedReplyMessage(ChatMessageCell cell, int id) {
                        MessageObject messageObject = cell.getMessageObject();
                        ChatActivity.this.scrollToMessageId(id, messageObject.getId(), true, messageObject.getDialogId() == ChatActivity.this.mergeDialogId ? 1 : 0);
                    }

                    public void didPressedViaBot(ChatMessageCell cell, String username) {
                        if (ChatActivity.this.bottomOverlayChat != null && ChatActivity.this.bottomOverlayChat.getVisibility() == 0) {
                            return;
                        }
                        if ((ChatActivity.this.bottomOverlay == null || ChatActivity.this.bottomOverlay.getVisibility() != 0) && ChatActivity.this.chatActivityEnterView != null && username != null && username.length() > 0) {
                            ChatActivity.this.chatActivityEnterView.setFieldText("@" + username + " ");
                            ChatActivity.this.chatActivityEnterView.openKeyboard();
                        }
                    }

                    public void didPressedImage(ChatMessageCell cell) {
                        MessageObject message = cell.getMessageObject();
                        if (message.isSendError()) {
                            ChatActivity.this.createMenu(cell, false);
                        } else if (!message.isSending()) {
                            if (message.type == 13) {
                                ChatActivity.this.showDialog(new StickersAlert(ChatActivity.this.getParentActivity(), ChatActivity.this, message.getInputStickerSet(), null, ChatActivity.this.bottomOverlayChat.getVisibility() != 0 ? ChatActivity.this.chatActivityEnterView : null));
                            } else if ((VERSION.SDK_INT >= 16 && message.isVideo()) || message.type == 1 || ((message.type == 0 && !message.isWebpageDocument()) || message.isGif())) {
                                if (message.isVideo()) {
                                    ChatActivity.this.sendSecretMessageRead(message);
                                }
                                PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                                if (PhotoViewer.getInstance().openPhoto(message, message.type != 0 ? ChatActivity.this.dialog_id : 0, message.type != 0 ? ChatActivity.this.mergeDialogId : 0, ChatActivity.this)) {
                                    PhotoViewer.getInstance().setParentChatActivity(ChatActivity.this);
                                }
                            } else if (message.type == 3) {
                                ChatActivity.this.sendSecretMessageRead(message);
                                f = null;
                                try {
                                    if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                        f = new File(message.messageOwner.attachPath);
                                    }
                                    if (f == null || !f.exists()) {
                                        f = FileLoader.getPathToMessage(message.messageOwner);
                                    }
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    if (VERSION.SDK_INT >= 24) {
                                        intent.setFlags(1);
                                        intent.setDataAndType(FileProvider.getUriForFile(ChatActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", f), MimeTypes.VIDEO_MP4);
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(f), MimeTypes.VIDEO_MP4);
                                    }
                                    ChatActivity.this.getParentActivity().startActivityForResult(intent, 500);
                                } catch (Exception e) {
                                    ChatActivity.this.alertUserOpenError(message);
                                }
                            } else if (message.type == 4) {
                                if (AndroidUtilities.isGoogleMapsInstalled(ChatActivity.this)) {
                                    LocationActivity fragment = new LocationActivity();
                                    fragment.setMessageObject(message);
                                    ChatActivity.this.presentFragment(fragment);
                                }
                            } else if (message.type == 9 || message.type == 0) {
                                if (message.getDocumentName().endsWith("attheme")) {
                                    File locFile = null;
                                    if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                        f = new File(message.messageOwner.attachPath);
                                        if (f.exists()) {
                                            locFile = f;
                                        }
                                    }
                                    if (locFile == null) {
                                        f = FileLoader.getPathToMessage(message.messageOwner);
                                        if (f.exists()) {
                                            locFile = f;
                                        }
                                    }
                                    ThemeInfo themeInfo = Theme.applyThemeFile(locFile, message.getDocumentName(), true);
                                    if (themeInfo != null) {
                                        ChatActivity.this.presentFragment(new ThemePreviewActivity(locFile, themeInfo));
                                        return;
                                    }
                                }
                                try {
                                    AndroidUtilities.openForView(message, ChatActivity.this.getParentActivity());
                                } catch (Exception e2) {
                                    ChatActivity.this.alertUserOpenError(message);
                                }
                            }
                        }
                    }

                    public void didPressedInstantButton(ChatMessageCell cell) {
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                            ArticleViewer.getInstance().open(messageObject);
                        }
                    }
                });
                if (ChatActivity.this.currentEncryptedChat == null) {
                    chatMessageCell.setAllowAssistant(true);
                }
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new ChatActionCellDelegate() {
                    public void didClickedImage(ChatActionCell cell) {
                        MessageObject message = cell.getMessageObject();
                        PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640);
                        if (photoSize != null) {
                            PhotoViewer.getInstance().openPhoto(photoSize.location, ChatActivity.this);
                            return;
                        }
                        PhotoViewer.getInstance().openPhoto(message, 0, 0, ChatActivity.this);
                    }

                    public void didLongPressed(ChatActionCell cell) {
                        ChatActivity.this.createMenu(cell, false);
                    }

                    public void needOpenUserProfile(int uid) {
                        boolean z = true;
                        Bundle args;
                        if (uid < 0) {
                            args = new Bundle();
                            args.putInt("chat_id", -uid);
                            if (MessagesController.checkCanOpenChat(args, ChatActivity.this)) {
                                ChatActivity.this.presentFragment(new ChatActivity(args), true);
                            }
                        } else if (uid != UserConfig.getClientUserId()) {
                            args = new Bundle();
                            args.putInt("user_id", uid);
                            if (ChatActivity.this.currentEncryptedChat != null && uid == ChatActivity.this.currentUser.id) {
                                args.putLong("dialog_id", ChatActivity.this.dialog_id);
                            }
                            ProfileActivity fragment = new ProfileActivity(args);
                            if (ChatActivity.this.currentUser == null || ChatActivity.this.currentUser.id != uid) {
                                z = false;
                            }
                            fragment.setPlayProfileAnimation(z);
                            ChatActivity.this.presentFragment(fragment);
                        }
                    }

                    public void didPressedReplyMessage(ChatActionCell cell, int id) {
                        MessageObject messageObject = cell.getMessageObject();
                        ChatActivity.this.scrollToMessageId(id, messageObject.getId(), true, messageObject.getDialogId() == ChatActivity.this.mergeDialogId ? 1 : 0);
                    }

                    public void didPressedBotButton(MessageObject messageObject, KeyboardButton button) {
                        if (ChatActivity.this.getParentActivity() == null) {
                            return;
                        }
                        if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (button instanceof TL_keyboardButtonSwitchInline) || (button instanceof TL_keyboardButtonCallback) || (button instanceof TL_keyboardButtonGame) || (button instanceof TL_keyboardButtonUrl) || (button instanceof TL_keyboardButtonBuy)) {
                            ChatActivity.this.chatActivityEnterView.didPressedBotButton(button, messageObject, messageObject);
                        }
                    }
                });
            } else if (viewType == 2) {
                view = new ChatUnreadCell(this.mContext);
            } else if (viewType == 3) {
                view = new BotHelpCell(this.mContext);
                ((BotHelpCell) view).setDelegate(new BotHelpCellDelegate() {
                    public void didPressUrl(String url) {
                        if (url.startsWith("@")) {
                            MessagesController.openByUserName(url.substring(1), ChatActivity.this, 0);
                        } else if (url.startsWith("#")) {
                            DialogsActivity fragment = new DialogsActivity(null);
                            fragment.setSearchString(url);
                            ChatActivity.this.presentFragment(fragment);
                        } else if (url.startsWith("/")) {
                            ChatActivity.this.chatActivityEnterView.setCommand(null, url, false, false);
                        }
                    }
                });
            } else if (viewType == 4) {
                view = new ChatLoadingCell(this.mContext);
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == this.botInfoRow) {
                String str;
                BotHelpCell helpView = holder.itemView;
                if (ChatActivity.this.botInfo.isEmpty()) {
                    str = null;
                } else {
                    str = ((BotInfo) ChatActivity.this.botInfo.get(Integer.valueOf(ChatActivity.this.currentUser.id))).description;
                }
                helpView.setText(str);
            } else if (position == this.loadingDownRow || position == this.loadingUpRow) {
                holder.itemView.setProgressVisible(ChatActivity.this.loadsCount > 1);
            } else if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                MessageObject message = (MessageObject) ChatActivity.this.messages.get((ChatActivity.this.messages.size() - (position - this.messagesStartRow)) - 1);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    boolean pinnedBotton;
                    boolean pinnedTop;
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = ChatActivity.this.currentChat != null;
                    int nextType = getItemViewType(position + 1);
                    int prevType = getItemViewType(position - 1);
                    if ((message.messageOwner.reply_markup instanceof TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                        pinnedBotton = false;
                    } else {
                        MessageObject nextMessage = (MessageObject) ChatActivity.this.messages.get((ChatActivity.this.messages.size() - ((position + 1) - this.messagesStartRow)) - 1);
                        pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && (((ChatActivity.this.currentChat != null && nextMessage.messageOwner.from_id == message.messageOwner.from_id) || ChatActivity.this.currentChat == null) && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300);
                    }
                    if (prevType == holder.getItemViewType()) {
                        MessageObject prevMessage = (MessageObject) ChatActivity.this.messages.get(ChatActivity.this.messages.size() - (position - this.messagesStartRow));
                        pinnedTop = !(prevMessage.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && (((ChatActivity.this.currentChat != null && prevMessage.messageOwner.from_id == message.messageOwner.from_id) || ChatActivity.this.currentChat == null) && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300);
                    } else {
                        pinnedTop = false;
                    }
                    messageCell.setMessageObject(message, pinnedBotton, pinnedTop);
                    if ((view instanceof ChatMessageCell) && MediaController.getInstance().canDownloadMedia(2)) {
                        ((ChatMessageCell) view).downloadAudioIfNeed();
                    }
                    boolean z = ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID && message.getId() == ChatActivity.this.highlightMessageId;
                    messageCell.setHighlighted(z);
                    if (ChatActivity.this.searchContainer == null || ChatActivity.this.searchContainer.getVisibility() != 0 || MessagesSearchQuery.getLastSearchQuery() == null) {
                        messageCell.setHighlightedText(null);
                    } else {
                        messageCell.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
                    }
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                } else if (view instanceof ChatUnreadCell) {
                    ((ChatUnreadCell) view).setText(LocaleController.formatPluralString("NewMessages", ChatActivity.this.unread_to_load));
                    if (ChatActivity.this.createUnreadMessageAfterId != 0) {
                        ChatActivity.this.createUnreadMessageAfterId = 0;
                    }
                }
            }
        }

        public int getItemViewType(int position) {
            if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                return ((MessageObject) ChatActivity.this.messages.get((ChatActivity.this.messages.size() - (position - this.messagesStartRow)) - 1)).contentType;
            }
            if (position == this.botInfoRow) {
                return 3;
            }
            return 4;
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            boolean z = true;
            if (holder.itemView instanceof ChatMessageCell) {
                boolean z2;
                boolean z3;
                final ChatMessageCell messageCell = holder.itemView;
                MessageObject message = messageCell.getMessageObject();
                boolean selected = false;
                boolean disableSelection = false;
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    MessageObject messageObject;
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        messageObject = ChatActivity.this.chatActivityEnterView.getEditingMessageObject();
                    } else {
                        messageObject = null;
                    }
                    if (messageObject != message) {
                        int i;
                        HashMap[] access$800 = ChatActivity.this.selectedMessagesIds;
                        if (message.getDialogId() == ChatActivity.this.dialog_id) {
                            i = 0;
                        } else {
                            i = 1;
                        }
                        if (!access$800[i].containsKey(Integer.valueOf(message.getId()))) {
                            messageCell.setBackgroundDrawable(null);
                            disableSelection = true;
                        }
                    }
                    messageCell.setBackgroundColor(Theme.getColor(Theme.key_chat_selectedBackground));
                    selected = true;
                    disableSelection = true;
                } else {
                    messageCell.setBackgroundDrawable(null);
                }
                if (disableSelection) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                if (disableSelection && selected) {
                    z3 = true;
                } else {
                    z3 = false;
                }
                messageCell.setCheckPressed(z2, z3);
                messageCell.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        messageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                        int height = ChatActivity.this.chatListView.getMeasuredHeight();
                        int top = messageCell.getTop();
                        int bottom = messageCell.getBottom();
                        int viewTop = top >= 0 ? 0 : -top;
                        int viewBottom = messageCell.getMeasuredHeight();
                        if (viewBottom > height) {
                            viewBottom = viewTop + height;
                        }
                        messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                        return true;
                    }
                });
                if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID || messageCell.getMessageObject().getId() != ChatActivity.this.highlightMessageId) {
                    z = false;
                }
                messageCell.setHighlighted(z);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = ChatActivity.this.messages.indexOf(messageObject);
            if (index != -1) {
                notifyItemChanged(((this.messagesStartRow + ChatActivity.this.messages.size()) - index) - 1);
            }
        }

        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public ChatActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        Semaphore semaphore;
        int chatId = this.arguments.getInt("chat_id", 0);
        int userId = this.arguments.getInt("user_id", 0);
        int encId = this.arguments.getInt("enc_id", 0);
        this.inlineReturn = this.arguments.getLong("inline_return", 0);
        String inlineQuery = this.arguments.getString("inline_query");
        this.startLoadFromMessageId = this.arguments.getInt("message_id", 0);
        int migrated_to = this.arguments.getInt("migrated_to", 0);
        this.scrollToTopOnResume = this.arguments.getBoolean("scrollToTopOnResume", false);
        final int i;
        final Semaphore semaphore2;
        if (chatId != 0) {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(chatId));
            if (this.currentChat == null) {
                semaphore = new Semaphore(0);
                i = chatId;
                semaphore2 = semaphore;
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentChat = MessagesStorage.getInstance().getChat(i);
                        semaphore2.release();
                    }
                });
                try {
                    semaphore.acquire();
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                MessagesController.getInstance().putChat(this.currentChat, true);
            }
            if (chatId > 0) {
                this.dialog_id = (long) (-chatId);
            } else {
                this.isBroadcast = true;
                this.dialog_id = AndroidUtilities.makeBroadcastId(chatId);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance().startShortPoll(chatId, false);
            }
        } else if (userId != 0) {
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(userId));
            if (this.currentUser == null) {
                semaphore = new Semaphore(0);
                i = userId;
                semaphore2 = semaphore;
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentUser = MessagesStorage.getInstance().getUser(i);
                        semaphore2.release();
                    }
                });
                try {
                    semaphore.acquire();
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance().putUser(this.currentUser, true);
            }
            this.dialog_id = (long) userId;
            this.botUser = this.arguments.getString("botUser");
            if (inlineQuery != null) {
                MessagesController.getInstance().sendBotStart(this.currentUser, inlineQuery);
            }
        } else if (encId == 0) {
            return false;
        } else {
            this.currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(encId));
            if (this.currentEncryptedChat == null) {
                semaphore = new Semaphore(0);
                i = encId;
                semaphore2 = semaphore;
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentEncryptedChat = MessagesStorage.getInstance().getEncryptedChat(i);
                        semaphore2.release();
                    }
                });
                try {
                    semaphore.acquire();
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
                if (this.currentEncryptedChat == null) {
                    return false;
                }
                MessagesController.getInstance().putEncryptedChat(this.currentEncryptedChat, true);
            }
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentEncryptedChat.user_id));
            if (this.currentUser == null) {
                semaphore = new Semaphore(0);
                final Semaphore semaphore3 = semaphore;
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentUser = MessagesStorage.getInstance().getUser(ChatActivity.this.currentEncryptedChat.user_id);
                        semaphore3.release();
                    }
                });
                try {
                    semaphore.acquire();
                } catch (Throwable e222) {
                    FileLog.e(e222);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance().putUser(this.currentUser, true);
            }
            this.dialog_id = ((long) encId) << 32;
            int[] iArr = this.maxMessageId;
            this.maxMessageId[1] = Integer.MIN_VALUE;
            iArr[0] = Integer.MIN_VALUE;
            iArr = this.minMessageId;
            this.minMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            iArr[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            MediaController.getInstance().startMediaObserver();
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.botInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.botKeyboardDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedPinnedMessage);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.peerSettingsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.newDraftReceived);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        super.onFragmentCreate();
        if (this.currentEncryptedChat == null && !this.isBroadcast) {
            BotQuery.loadBotKeyboard(this.dialog_id);
        }
        this.loading = true;
        MessagesController.getInstance().loadPeerSettings(this.currentUser, this.currentChat);
        MessagesController.getInstance().setLastCreatedDialogId(this.dialog_id, true);
        if (this.startLoadFromMessageId == 0) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            int messageId = sharedPreferences.getInt("diditem" + this.dialog_id, 0);
            if (messageId != 0) {
                this.loadingFromOldPosition = true;
                this.startLoadFromMessageOffset = sharedPreferences.getInt("diditemo" + this.dialog_id, 0);
                this.startLoadFromMessageId = messageId;
            }
        } else {
            this.needSelectFromMessageId = true;
        }
        MessagesController instance;
        long j;
        int i2;
        int i3;
        boolean isChannel;
        int i4;
        if (this.startLoadFromMessageId != 0) {
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            int i5;
            if (migrated_to != 0) {
                this.mergeDialogId = (long) migrated_to;
                instance = MessagesController.getInstance();
                j = this.mergeDialogId;
                i2 = this.loadingFromOldPosition ? 50 : AndroidUtilities.isTablet() ? 30 : 20;
                i5 = this.startLoadFromMessageId;
                i3 = this.classGuid;
                isChannel = ChatObject.isChannel(this.currentChat);
                i4 = this.lastLoadIndex;
                this.lastLoadIndex = i4 + 1;
                instance.loadMessages(j, i2, i5, 0, true, 0, i3, 3, 0, isChannel, i4);
            } else {
                instance = MessagesController.getInstance();
                j = this.dialog_id;
                i2 = this.loadingFromOldPosition ? 50 : AndroidUtilities.isTablet() ? 30 : 20;
                i5 = this.startLoadFromMessageId;
                i3 = this.classGuid;
                isChannel = ChatObject.isChannel(this.currentChat);
                i4 = this.lastLoadIndex;
                this.lastLoadIndex = i4 + 1;
                instance.loadMessages(j, i2, i5, 0, true, 0, i3, 3, 0, isChannel, i4);
            }
        } else {
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            instance = MessagesController.getInstance();
            j = this.dialog_id;
            i2 = AndroidUtilities.isTablet() ? 30 : 20;
            i3 = this.classGuid;
            isChannel = ChatObject.isChannel(this.currentChat);
            i4 = this.lastLoadIndex;
            this.lastLoadIndex = i4 + 1;
            instance.loadMessages(j, i2, 0, 0, true, 0, i3, 2, 0, isChannel, i4);
        }
        if (this.currentChat != null) {
            Semaphore semaphore4 = null;
            if (this.isBroadcast) {
                semaphore = new Semaphore(0);
            }
            MessagesController.getInstance().loadChatInfo(this.currentChat.id, semaphore4, ChatObject.isChannel(this.currentChat));
            if (this.isBroadcast && semaphore4 != null) {
                try {
                    semaphore4.acquire();
                } catch (Throwable e2222) {
                    FileLog.e(e2222);
                }
            }
        }
        if (userId != 0 && this.currentUser.bot) {
            BotQuery.loadBotInfo(userId, true, this.classGuid);
        } else if (this.info instanceof TL_chatFull) {
            for (int a = 0; a < this.info.participants.participants.size(); a++) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                if (user != null && user.bot) {
                    BotQuery.loadBotInfo(user.id, true, this.classGuid);
                }
            }
        }
        if (this.currentUser != null) {
            this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.currentUser.id));
        }
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(this.dialog_id), Boolean.valueOf(false));
        }
        if (!(this.currentEncryptedChat == null || AndroidUtilities.getMyLayerVersion(this.currentEncryptedChat.layer) == 46)) {
            SecretChatHelper.getInstance().sendNotifyLayerMessage(this.currentEncryptedChat, null);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onDestroy();
        }
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.dismissInternal();
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
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(this.dialog_id), Boolean.valueOf(true));
        }
        if (this.currentEncryptedChat != null) {
            MediaController.getInstance().stopMediaObserver();
            try {
                if (VERSION.SDK_INT >= 23 && (UserConfig.passcodeHash.length() == 0 || UserConfig.allowScreenCapture)) {
                    getParentActivity().getWindow().clearFlags(8192);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
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
    }

    public View createView(Context context) {
        int a;
        boolean z;
        boolean z2;
        MessageObject messageObject;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (a = 0; a < 8; a++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        for (a = 1; a >= 0; a--) {
            this.selectedMessagesIds[a].clear();
            this.selectedMessagesCanCopyIds[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.hasOwnBackground = true;
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onDestroy();
            this.chatAttachAlert = null;
        }
        Theme.createChatResources(context, false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                int a;
                if (id == -1) {
                    if (ChatActivity.this.actionBar.isActionModeShowed()) {
                        for (a = 1; a >= 0; a--) {
                            ChatActivity.this.selectedMessagesIds[a].clear();
                            ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                        }
                        ChatActivity.this.cantDeleteMessagesCount = 0;
                        if (ChatActivity.this.chatActivityEnterView.isEditingMessage()) {
                            ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
                        } else {
                            ChatActivity.this.actionBar.hideActionMode();
                            ChatActivity.this.updatePinnedMessageView(true);
                        }
                        ChatActivity.this.updateVisibleRows();
                        return;
                    }
                    ChatActivity.this.finishFragment();
                } else if (id == 10) {
                    String str = "";
                    int previousUid = 0;
                    for (a = 1; a >= 0; a--) {
                        ArrayList<Integer> arrayList = new ArrayList(ChatActivity.this.selectedMessagesCanCopyIds[a].keySet());
                        if (ChatActivity.this.currentEncryptedChat == null) {
                            Collections.sort(arrayList);
                        } else {
                            Collections.sort(arrayList, Collections.reverseOrder());
                        }
                        for (int b = 0; b < arrayList.size(); b++) {
                            messageObject = (MessageObject) ChatActivity.this.selectedMessagesCanCopyIds[a].get((Integer) arrayList.get(b));
                            if (str.length() != 0) {
                                str = str + "\n\n";
                            }
                            str = str + ChatActivity.this.getMessageContent(messageObject, previousUid, true);
                            previousUid = messageObject.messageOwner.from_id;
                        }
                    }
                    if (str.length() != 0) {
                        AndroidUtilities.addToClipboard(str);
                    }
                    for (a = 1; a >= 0; a--) {
                        ChatActivity.this.selectedMessagesIds[a].clear();
                        ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                    }
                    ChatActivity.this.cantDeleteMessagesCount = 0;
                    ChatActivity.this.actionBar.hideActionMode();
                    ChatActivity.this.updatePinnedMessageView(true);
                    ChatActivity.this.updateVisibleRows();
                } else if (id == 12) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        ChatActivity.this.createDeleteMessagesAlert(null);
                    }
                } else if (id == 11) {
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    BaseFragment dialogsActivity = new DialogsActivity(args);
                    dialogsActivity.setDelegate(ChatActivity.this);
                    ChatActivity.this.presentFragment(dialogsActivity);
                } else if (id == 13) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        ChatActivity.this.showDialog(AlertsCreator.createTTLAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.currentEncryptedChat).create());
                    }
                } else if (id == 15 || id == 16) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        boolean isChat = ((int) ChatActivity.this.dialog_id) < 0 && ((int) (ChatActivity.this.dialog_id >> 32)) != 1;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        if (id == 15) {
                            builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                        } else if (isChat) {
                            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                        } else {
                            builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                        }
                        final int i = id;
                        final boolean z = isChat;
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i != 15) {
                                    if (!z) {
                                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                                    } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                                    } else {
                                        MessagesController.getInstance().deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                                    }
                                    ChatActivity.this.finishFragment();
                                    return;
                                }
                                MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 1);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ChatActivity.this.showDialog(builder.create());
                    }
                } else if (id == 17) {
                    if (ChatActivity.this.currentUser != null && ChatActivity.this.getParentActivity() != null) {
                        if (ChatActivity.this.currentUser.phone == null || ChatActivity.this.currentUser.phone.length() == 0) {
                            ChatActivity.this.shareMyContact(ChatActivity.this.replyingMessageObject);
                            return;
                        }
                        args = new Bundle();
                        args.putInt("user_id", ChatActivity.this.currentUser.id);
                        args.putBoolean("addContact", true);
                        ChatActivity.this.presentFragment(new ContactAddActivity(args));
                    }
                } else if (id == 18) {
                    ChatActivity.this.toggleMute(false);
                } else if (id == 21) {
                    ChatActivity.this.showDialog(AlertsCreator.createReportAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.dialog_id, ChatActivity.this));
                } else if (id == 19) {
                    messageObject = null;
                    a = 1;
                    while (a >= 0) {
                        if (messageObject == null && ChatActivity.this.selectedMessagesIds[a].size() == 1) {
                            messageObject = (MessageObject) ChatActivity.this.messagesDict[a].get(new ArrayList(ChatActivity.this.selectedMessagesIds[a].keySet()).get(0));
                        }
                        ChatActivity.this.selectedMessagesIds[a].clear();
                        ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                        a--;
                    }
                    if (messageObject != null && (messageObject.messageOwner.id > 0 || (messageObject.messageOwner.id < 0 && ChatActivity.this.currentEncryptedChat != null))) {
                        ChatActivity.this.showReplyPanel(true, messageObject, null, null, false);
                    }
                    ChatActivity.this.cantDeleteMessagesCount = 0;
                    ChatActivity.this.actionBar.hideActionMode();
                    ChatActivity.this.updatePinnedMessageView(true);
                    ChatActivity.this.updateVisibleRows();
                } else if (id == 14) {
                    ChatActivity.this.openAttachMenu();
                } else if (id == 30) {
                    SendMessagesHelper.getInstance().sendMessage("/help", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                } else if (id == 31) {
                    SendMessagesHelper.getInstance().sendMessage("/settings", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                } else if (id == 40) {
                    ChatActivity.this.openSearchWithText(null);
                } else if (id == 32) {
                    VoIPHelper.startCall(ChatActivity.this.currentUser, ChatActivity.this.getParentActivity());
                }
            }
        });
        this.avatarContainer = new ChatAvatarContainer(context, this, this.currentEncryptedChat != null);
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        if (!(this.currentChat == null || ChatObject.isChannel(this.currentChat))) {
            int count = this.currentChat.participants_count;
            if (this.info != null) {
                count = this.info.participants.participants.size();
            }
            if (count == 0 || this.currentChat.deactivated || this.currentChat.left || (this.currentChat instanceof TL_chatForbidden) || (this.info != null && (this.info.participants instanceof TL_chatParticipantsForbidden))) {
                this.avatarContainer.setEnabled(false);
            }
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.currentEncryptedChat == null && !this.isBroadcast) {
            this.searchItem = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchCollapse() {
                    ChatActivity.this.avatarContainer.setVisibility(0);
                    if (ChatActivity.this.chatActivityEnterView.hasText()) {
                        if (ChatActivity.this.headerItem != null) {
                            ChatActivity.this.headerItem.setVisibility(8);
                        }
                        if (ChatActivity.this.attachItem != null) {
                            ChatActivity.this.attachItem.setVisibility(0);
                        }
                    } else {
                        if (ChatActivity.this.headerItem != null) {
                            ChatActivity.this.headerItem.setVisibility(0);
                        }
                        if (ChatActivity.this.attachItem != null) {
                            ChatActivity.this.attachItem.setVisibility(8);
                        }
                    }
                    ChatActivity.this.searchItem.setVisibility(8);
                    ChatActivity.this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    ChatActivity.this.updateVisibleRows();
                    ChatActivity.this.scrollToLastMessage(false);
                    ChatActivity.this.updateBottomOverlay();
                }

                public void onSearchExpand() {
                    if (ChatActivity.this.openSearchKeyboard) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                ChatActivity.this.searchItem.getSearchField().requestFocus();
                                AndroidUtilities.showKeyboard(ChatActivity.this.searchItem.getSearchField());
                            }
                        }, 300);
                    }
                }

                public void onSearchPressed(EditText editText) {
                    ChatActivity.this.updateSearchButtons(0, 0, 0);
                    MessagesSearchQuery.searchMessagesInChat(editText.getText().toString(), ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, 0);
                }
            });
            this.searchItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
            this.searchItem.setVisibility(8);
        }
        this.headerItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        if (this.currentUser != null && MessagesController.getInstance().callsEnabled) {
            this.headerItem.addSubItem(32, LocaleController.getString("Call", R.string.Call));
            TL_userFull userFull = MessagesController.getInstance().getUserFull(this.currentUser.id);
            if (userFull == null || !userFull.phone_calls_available) {
                this.headerItem.hideSubItem(32);
            } else {
                this.headerItem.showSubItem(32);
            }
        }
        if (this.searchItem != null) {
            this.headerItem.addSubItem(40, LocaleController.getString("Search", R.string.Search));
        }
        if (ChatObject.isChannel(this.currentChat) && !this.currentChat.creator && (!this.currentChat.megagroup || (this.currentChat.username != null && this.currentChat.username.length() > 0))) {
            this.headerItem.addSubItem(21, LocaleController.getString("ReportChat", R.string.ReportChat));
        }
        if (this.currentUser != null) {
            this.addContactItem = this.headerItem.addSubItem(17, "");
        }
        if (this.currentEncryptedChat != null) {
            this.timeItem2 = this.headerItem.addSubItem(13, LocaleController.getString("SetTimer", R.string.SetTimer));
        }
        if (!ChatObject.isChannel(this.currentChat)) {
            this.headerItem.addSubItem(15, LocaleController.getString("ClearHistory", R.string.ClearHistory));
            if (this.currentChat == null || this.isBroadcast) {
                this.headerItem.addSubItem(16, LocaleController.getString("DeleteChatUser", R.string.DeleteChatUser));
            } else {
                this.headerItem.addSubItem(16, LocaleController.getString("DeleteAndExit", R.string.DeleteAndExit));
            }
        }
        if (this.currentUser == null || !this.currentUser.self) {
            this.muteItem = this.headerItem.addSubItem(18, null);
        }
        if (this.currentUser != null && this.currentEncryptedChat == null && this.currentUser.bot) {
            this.headerItem.addSubItem(31, LocaleController.getString("BotSettings", R.string.BotSettings));
            this.headerItem.addSubItem(30, LocaleController.getString("BotHelp", R.string.BotHelp));
            updateBotButtons();
        }
        updateTitle();
        this.avatarContainer.updateOnlineCount();
        this.avatarContainer.updateSubtitle();
        updateTitleIcons();
        this.attachItem = menu.addItem(14, (int) R.drawable.ic_ab_other).setOverrideMenuClick(true).setAllowCloseAnimation(false);
        this.attachItem.setVisibility(8);
        this.actionModeViews.clear();
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        this.selectedMessagesCountTextView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.actionModeTitleContainer = new FrameLayout(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
                SimpleTextView access$3500 = ChatActivity.this.actionModeTextView;
                int i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18;
                access$3500.setTextSize(i);
                ChatActivity.this.actionModeTextView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
                if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                    access$3500 = ChatActivity.this.actionModeSubTextView;
                    i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14;
                    access$3500.setTextSize(i);
                    ChatActivity.this.actionModeSubTextView.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int textTop;
                int height = bottom - top;
                if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                    int textHeight = ((height / 2) - ChatActivity.this.actionModeTextView.getTextHeight()) / 2;
                    float f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 3.0f : 2.0f;
                    textTop = textHeight + AndroidUtilities.dp(f);
                } else {
                    textTop = (height - ChatActivity.this.actionModeTextView.getTextHeight()) / 2;
                }
                ChatActivity.this.actionModeTextView.layout(0, textTop, ChatActivity.this.actionModeTextView.getMeasuredWidth(), ChatActivity.this.actionModeTextView.getTextHeight() + textTop);
                if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                    int textHeight2 = (height / 2) + (((height / 2) - ChatActivity.this.actionModeSubTextView.getTextHeight()) / 2);
                    if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation == 2) {
                        textTop = textHeight2 - AndroidUtilities.dp(1.0f);
                        ChatActivity.this.actionModeSubTextView.layout(0, textTop, ChatActivity.this.actionModeSubTextView.getMeasuredWidth(), ChatActivity.this.actionModeSubTextView.getTextHeight() + textTop);
                    } else {
                        textTop = textHeight2 - AndroidUtilities.dp(1.0f);
                        ChatActivity.this.actionModeSubTextView.layout(0, textTop, ChatActivity.this.actionModeSubTextView.getMeasuredWidth(), ChatActivity.this.actionModeSubTextView.getTextHeight() + textTop);
                    }
                }
            }
        };
        actionMode.addView(this.actionModeTitleContainer, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        this.actionModeTitleContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.actionModeTitleContainer.setVisibility(8);
        this.actionModeTextView = new SimpleTextView(context);
        this.actionModeTextView.setTextSize(18);
        this.actionModeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.actionModeTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        this.actionModeTextView.setText(LocaleController.getString("Edit", R.string.Edit));
        this.actionModeTitleContainer.addView(this.actionModeTextView, LayoutHelper.createFrame(-1, -1.0f));
        this.actionModeSubTextView = new SimpleTextView(context);
        this.actionModeSubTextView.setGravity(3);
        this.actionModeSubTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        this.actionModeTitleContainer.addView(this.actionModeSubTextView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.currentEncryptedChat == null) {
            if (!this.isBroadcast) {
                this.actionModeViews.add(actionMode.addItemWithWidth(19, R.drawable.ic_ab_reply, AndroidUtilities.dp(54.0f)));
            }
            this.actionModeViews.add(actionMode.addItemWithWidth(10, R.drawable.ic_ab_copy, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(11, R.drawable.ic_ab_forward, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(12, R.drawable.ic_ab_delete, AndroidUtilities.dp(54.0f)));
        } else {
            this.actionModeViews.add(actionMode.addItemWithWidth(19, R.drawable.ic_ab_reply, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(10, R.drawable.ic_ab_copy, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(12, R.drawable.ic_ab_delete, AndroidUtilities.dp(54.0f)));
        }
        actionMode.getItem(10).setVisibility(this.selectedMessagesCanCopyIds[0].size() + this.selectedMessagesCanCopyIds[1].size() != 0 ? 0 : 8);
        actionMode.getItem(12).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        checkActionBarMenu();
        this.fragmentView = new SizeNotifierFrameLayout(context) {
            int inputFieldHeight = 0;

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ChatActivity.this.actionBar && ChatActivity.this.parentLayout != null) {
                    ChatActivity.this.parentLayout.drawHeaderShadow(canvas, ChatActivity.this.actionBar.getVisibility() == 0 ? ChatActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return result;
            }

            protected boolean isActionBarVisible() {
                return ChatActivity.this.actionBar.getVisibility() == 0;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(ChatActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ChatActivity.this.actionBar.getMeasuredHeight();
                if (ChatActivity.this.actionBar.getVisibility() == 0) {
                    heightSize -= actionBarHeight;
                }
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                    heightSize -= ChatActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                measureChildWithMargins(ChatActivity.this.chatActivityEnterView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                this.inputFieldHeight = ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChatActivity.this.chatActivityEnterView || child == ChatActivity.this.actionBar)) {
                        if (child == ChatActivity.this.chatListView || child == ChatActivity.this.progressView) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp((float) ((ChatActivity.this.chatActivityEnterView.isTopViewVisible() ? 48 : 0) + 2)) + (heightSize - this.inputFieldHeight)), NUM));
                        } else if (child == ChatActivity.this.emptyViewContainer) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
                        } else if (ChatActivity.this.chatActivityEnterView.isPopupView(child)) {
                            if (!AndroidUtilities.isInMultiwindow) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                            } else if (AndroidUtilities.isTablet()) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (((heightSize - this.inputFieldHeight) + actionBarHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                            } else {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((((heightSize - this.inputFieldHeight) + actionBarHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                            }
                        } else if (child == ChatActivity.this.mentionContainer) {
                            int height;
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ChatActivity.this.mentionContainer.getLayoutParams();
                            ChatActivity.this.mentionListViewIgnoreLayout = true;
                            int maxHeight;
                            if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                                maxHeight = ChatActivity.this.mentionGridLayoutManager.getRowsCount(widthSize) * 102;
                                if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                                    maxHeight += 34;
                                }
                                height = (heightSize - ChatActivity.this.chatActivityEnterView.getMeasuredHeight()) + (maxHeight != 0 ? AndroidUtilities.dp(2.0f) : 0);
                                ChatActivity.this.mentionListView.setPadding(0, Math.max(0, height - AndroidUtilities.dp(Math.min((float) maxHeight, 122.399994f))), 0, 0);
                            } else {
                                int size = ChatActivity.this.mentionsAdapter.getItemCount();
                                maxHeight = 0;
                                if (ChatActivity.this.mentionsAdapter.isBotContext()) {
                                    if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                                        maxHeight = 0 + 36;
                                        size--;
                                    }
                                    maxHeight += size * 68;
                                } else {
                                    maxHeight = 0 + (size * 36);
                                }
                                height = (heightSize - ChatActivity.this.chatActivityEnterView.getMeasuredHeight()) + (maxHeight != 0 ? AndroidUtilities.dp(2.0f) : 0);
                                ChatActivity.this.mentionListView.setPadding(0, Math.max(0, height - AndroidUtilities.dp(Math.min((float) maxHeight, 122.399994f))), 0, 0);
                            }
                            layoutParams.height = height;
                            layoutParams.topMargin = 0;
                            ChatActivity.this.mentionListViewIgnoreLayout = false;
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(layoutParams.height, NUM));
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : ChatActivity.this.chatActivityEnterView.getEmojiPadding();
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        int childLeft;
                        int childTop;
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch ((gravity & 7) & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                if (child != ChatActivity.this.actionBar && ChatActivity.this.actionBar.getVisibility() == 0) {
                                    childTop += ChatActivity.this.actionBar.getMeasuredHeight();
                                    break;
                                }
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (child == ChatActivity.this.mentionContainer) {
                            childTop -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight() - AndroidUtilities.dp(2.0f);
                        } else if (child == ChatActivity.this.pagedownButton) {
                            childTop -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                        } else if (child == ChatActivity.this.emptyViewContainer) {
                            childTop -= (this.inputFieldHeight / 2) - (ChatActivity.this.actionBar.getVisibility() == 0 ? ChatActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (ChatActivity.this.chatActivityEnterView.isPopupView(child)) {
                            if (AndroidUtilities.isInMultiwindow) {
                                childTop = (ChatActivity.this.chatActivityEnterView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                            } else {
                                childTop = ChatActivity.this.chatActivityEnterView.getBottom();
                            }
                        } else if (child == ChatActivity.this.gifHintTextView) {
                            childTop -= this.inputFieldHeight;
                        } else if (child == ChatActivity.this.chatListView || child == ChatActivity.this.progressView) {
                            if (ChatActivity.this.chatActivityEnterView.isTopViewVisible()) {
                                childTop -= AndroidUtilities.dp(48.0f);
                            }
                        } else if (child == ChatActivity.this.actionBar) {
                            childTop -= getPaddingTop();
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                ChatActivity.this.updateMessagesVisisblePart();
                notifyHeightChanged();
            }
        };
        SizeNotifierFrameLayout contentView = this.fragmentView;
        contentView.setBackgroundImage(Theme.getCachedWallpaper());
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setVisibility(4);
        contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (this.currentEncryptedChat != null) {
            this.bigEmptyView = new ChatBigEmptyView(context, true);
            if (this.currentEncryptedChat.admin_id == UserConfig.getClientUserId()) {
                this.bigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleOutgoing", R.string.EncryptedPlaceholderTitleOutgoing, UserObject.getFirstName(this.currentUser)));
            } else {
                this.bigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleIncoming", R.string.EncryptedPlaceholderTitleIncoming, UserObject.getFirstName(this.currentUser)));
            }
            this.emptyViewContainer.addView(this.bigEmptyView, new FrameLayout.LayoutParams(-2, -2, 17));
        } else if (this.currentUser == null || !this.currentUser.self) {
            this.emptyView = new TextView(context);
            if (this.currentUser == null || this.currentUser.id == 777000 || this.currentUser.id == 429000 || this.currentUser.id == 4244000 || !(this.currentUser.id / 1000 == 333 || this.currentUser.id % 1000 == 0)) {
                this.emptyView.setText(LocaleController.getString("NoMessages", R.string.NoMessages));
            } else {
                this.emptyView.setText(LocaleController.getString("GotAQuestion", R.string.GotAQuestion));
            }
            this.emptyView.setTextSize(1, 14.0f);
            this.emptyView.setGravity(17);
            this.emptyView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            this.emptyView.setBackgroundResource(R.drawable.system);
            this.emptyView.getBackground().setColorFilter(Theme.colorFilter);
            this.emptyView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.emptyView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(3.0f));
            this.emptyViewContainer.addView(this.emptyView, new FrameLayout.LayoutParams(-2, -2, 17));
        } else {
            this.bigEmptyView = new ChatBigEmptyView(context, false);
            this.emptyViewContainer.addView(this.bigEmptyView, new FrameLayout.LayoutParams(-2, -2, 17));
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onDestroy();
        }
        int scrollToPositionOnRecreate = -1;
        int scrollToOffsetOnRecreate = 0;
        if (this.chatLayoutManager != null) {
            scrollToPositionOnRecreate = this.chatLayoutManager.findFirstVisibleItemPosition();
            if (scrollToPositionOnRecreate != this.chatLayoutManager.getItemCount() - 1) {
                Holder holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate);
                if (holder != null) {
                    scrollToOffsetOnRecreate = holder.itemView.getTop();
                } else {
                    scrollToPositionOnRecreate = -1;
                }
            } else {
                scrollToPositionOnRecreate = -1;
            }
        }
        this.chatListView = new RecyclerListView(context) {
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                ChatActivity.this.forceScrollToTop = false;
                if (ChatActivity.this.chatAdapter.isBot) {
                    int childCount = getChildCount();
                    for (int a = 0; a < childCount; a++) {
                        View child = getChildAt(a);
                        if (child instanceof BotHelpCell) {
                            int top = ((b - t) / 2) - (child.getMeasuredHeight() / 2);
                            if (child.getTop() > top) {
                                child.layout(0, top, r - l, child.getMeasuredHeight() + top);
                                return;
                            }
                            return;
                        }
                    }
                }
            }

            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) child;
                    ImageReceiver imageReceiver = chatMessageCell.getAvatarImage();
                    if (imageReceiver != null) {
                        ViewHolder holder;
                        int top = child.getTop();
                        if (chatMessageCell.isPinnedBottom()) {
                            holder = ChatActivity.this.chatListView.getChildViewHolder(child);
                            if (!(holder == null || ChatActivity.this.chatListView.findViewHolderForAdapterPosition(holder.getAdapterPosition() + 1) == null)) {
                                imageReceiver.setImageY(-AndroidUtilities.dp(1000.0f));
                                imageReceiver.draw(canvas);
                            }
                        }
                        if (chatMessageCell.isPinnedTop()) {
                            holder = ChatActivity.this.chatListView.getChildViewHolder(child);
                            if (holder != null) {
                                do {
                                    holder = ChatActivity.this.chatListView.findViewHolderForAdapterPosition(holder.getAdapterPosition() - 1);
                                    if (holder == null) {
                                        break;
                                    }
                                    top = holder.itemView.getTop();
                                    if (!(holder.itemView instanceof ChatMessageCell)) {
                                        break;
                                    }
                                } while (((ChatMessageCell) holder.itemView).isPinnedTop());
                            }
                        }
                        int y = child.getTop() + chatMessageCell.getLayoutHeight();
                        int maxY = ChatActivity.this.chatListView.getHeight() - ChatActivity.this.chatListView.getPaddingBottom();
                        if (y > maxY) {
                            y = maxY;
                        }
                        if (y - AndroidUtilities.dp(48.0f) < top) {
                            y = top + AndroidUtilities.dp(48.0f);
                        }
                        imageReceiver.setImageY(y - AndroidUtilities.dp(44.0f));
                        imageReceiver.draw(canvas);
                    }
                }
                return result;
            }
        };
        this.chatListView.setTag(Integer.valueOf(1));
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        Adapter chatActivityAdapter = new ChatActivityAdapter(context);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        this.chatListView.setItemAnimator(null);
        this.chatListView.setLayoutAnimation(null);
        this.chatLayoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.chatLayoutManager.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.chatListView.setOnItemClickListener(this.onItemClickListener);
        this.chatListView.setOnScrollListener(new OnScrollListener() {
            private final int scrollValue = AndroidUtilities.dp(100.0f);
            private float totalDy = 0.0f;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    ChatActivity.this.scrollingFloatingDate = true;
                } else if (newState == 0) {
                    ChatActivity.this.scrollingFloatingDate = false;
                    ChatActivity.this.hideFloatingDateView(true);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatActivity.this.chatListView.invalidate();
                if (!(dy == 0 || !ChatActivity.this.scrollingFloatingDate || ChatActivity.this.currentFloatingTopIsNotMessage)) {
                    if (ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        ChatActivity.this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        ChatActivity.this.updateVisibleRows();
                    }
                    if (ChatActivity.this.floatingDateView.getTag() == null) {
                        if (ChatActivity.this.floatingDateAnimation != null) {
                            ChatActivity.this.floatingDateAnimation.cancel();
                        }
                        ChatActivity.this.floatingDateView.setTag(Integer.valueOf(1));
                        ChatActivity.this.floatingDateAnimation = new AnimatorSet();
                        ChatActivity.this.floatingDateAnimation.setDuration(150);
                        AnimatorSet access$7000 = ChatActivity.this.floatingDateAnimation;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.floatingDateView, "alpha", new float[]{1.0f});
                        access$7000.playTogether(animatorArr);
                        ChatActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivity.this.floatingDateAnimation)) {
                                    ChatActivity.this.floatingDateAnimation = null;
                                }
                            }
                        });
                        ChatActivity.this.floatingDateAnimation.start();
                    }
                }
                ChatActivity.this.checkScrollForLoad(true);
                int firstVisibleItem = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    if (firstVisibleItem + visibleItemCount == ChatActivity.this.chatAdapter.getItemCount() && ChatActivity.this.forwardEndReached[0]) {
                        ChatActivity.this.showPagedownButton(false, true);
                    } else if (dy > 0) {
                        if (ChatActivity.this.pagedownButton.getTag() == null) {
                            this.totalDy += (float) dy;
                            if (this.totalDy > ((float) this.scrollValue)) {
                                this.totalDy = 0.0f;
                                ChatActivity.this.showPagedownButton(true, true);
                                ChatActivity.this.pagedownButtonShowedByScroll = true;
                            }
                        }
                    } else if (ChatActivity.this.pagedownButtonShowedByScroll && ChatActivity.this.pagedownButton.getTag() != null) {
                        this.totalDy += (float) dy;
                        if (this.totalDy < ((float) (-this.scrollValue))) {
                            ChatActivity.this.showPagedownButton(false, true);
                            this.totalDy = 0.0f;
                        }
                    }
                }
                ChatActivity.this.updateMessagesVisisblePart();
            }
        });
        this.chatListView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (ChatActivity.this.openSecretPhotoRunnable != null || SecretPhotoViewer.getInstance().isVisible()) {
                    if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                            }
                        }, 150);
                        if (ChatActivity.this.openSecretPhotoRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                            ChatActivity.this.openSecretPhotoRunnable = null;
                            try {
                                Toast.makeText(v.getContext(), LocaleController.getString("PhotoTip", R.string.PhotoTip), 0).show();
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        } else if (SecretPhotoViewer.getInstance().isVisible()) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                                    ChatActivity.this.chatListView.setLongClickable(true);
                                }
                            });
                            SecretPhotoViewer.getInstance().closePhoto();
                        }
                    } else if (event.getAction() != 0) {
                        if (SecretPhotoViewer.getInstance().isVisible()) {
                            return true;
                        }
                        if (ChatActivity.this.openSecretPhotoRunnable != null) {
                            if (event.getAction() != 2) {
                                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                                ChatActivity.this.openSecretPhotoRunnable = null;
                            } else if (Math.hypot((double) (ChatActivity.this.startX - event.getX()), (double) (ChatActivity.this.startY - event.getY())) > ((double) AndroidUtilities.dp(5.0f))) {
                                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                                ChatActivity.this.openSecretPhotoRunnable = null;
                            }
                            ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                            ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                            ChatActivity.this.chatListView.setLongClickable(true);
                        }
                    }
                }
                return false;
            }
        });
        this.chatListView.setOnInterceptTouchListener(new OnInterceptTouchListener() {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                if (ChatActivity.this.chatActivityEnterView != null && ChatActivity.this.chatActivityEnterView.isEditingMessage()) {
                    return true;
                }
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    return false;
                }
                if (event.getAction() == 0) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int count = ChatActivity.this.chatListView.getChildCount();
                    int a = 0;
                    while (a < count) {
                        View view = ChatActivity.this.chatListView.getChildAt(a);
                        int top = view.getTop();
                        int bottom = view.getBottom();
                        if (top > y || bottom < y) {
                            a++;
                        } else if (view instanceof ChatMessageCell) {
                            final ChatMessageCell cell = (ChatMessageCell) view;
                            final MessageObject messageObject = cell.getMessageObject();
                            if (messageObject != null && !messageObject.isSending() && messageObject.isSecretPhoto() && cell.getPhotoImage().isInsideImage((float) x, (float) (y - top)) && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                                ChatActivity.this.startX = (float) x;
                                ChatActivity.this.startY = (float) y;
                                ChatActivity.this.chatListView.setOnItemClickListener(null);
                                ChatActivity.this.openSecretPhotoRunnable = new Runnable() {
                                    public void run() {
                                        if (ChatActivity.this.openSecretPhotoRunnable != null) {
                                            ChatActivity.this.chatListView.requestDisallowInterceptTouchEvent(true);
                                            ChatActivity.this.chatListView.setOnItemLongClickListener(null);
                                            ChatActivity.this.chatListView.setLongClickable(false);
                                            ChatActivity.this.openSecretPhotoRunnable = null;
                                            if (ChatActivity.this.sendSecretMessageRead(messageObject)) {
                                                cell.invalidate();
                                            }
                                            SecretPhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                                            SecretPhotoViewer.getInstance().openPhoto(messageObject);
                                        }
                                    }
                                };
                                AndroidUtilities.runOnUIThread(ChatActivity.this.openSecretPhotoRunnable, 100);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        if (scrollToPositionOnRecreate != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
        }
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(4);
        contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        this.progressView2 = new View(context);
        this.progressView2.setBackgroundResource(R.drawable.system_loader);
        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        this.progressBar = new RadialProgressView(context);
        this.progressBar.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        this.floatingDateView = new ChatActionCell(context);
        this.floatingDateView.setAlpha(0.0f);
        contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.floatingDateView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.floatingDateView.getAlpha() != 0.0f) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(((long) ChatActivity.this.floatingDateView.getCustomDate()) * 1000);
                    int year = calendar.get(1);
                    int monthOfYear = calendar.get(2);
                    int dayOfMonth = calendar.get(5);
                    calendar.clear();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    ChatActivity.this.jumpToDate((int) (calendar.getTime().getTime() / 1000));
                }
            }
        });
        if (ChatObject.isChannel(this.currentChat)) {
            this.pinnedMessageView = new FrameLayout(context);
            this.pinnedMessageView.setTag(Integer.valueOf(1));
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
            this.pinnedMessageView.setBackgroundResource(R.drawable.blockpanel);
            this.pinnedMessageView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelBackground), Mode.MULTIPLY));
            contentView.addView(this.pinnedMessageView, LayoutHelper.createFrame(-1, 50, 51));
            this.pinnedMessageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChatActivity.this.scrollToMessageId(ChatActivity.this.info.pinned_msg_id, 0, true, 0);
                }
            });
            this.pinnedLineView = new View(context);
            this.pinnedLineView.setBackgroundColor(Theme.getColor(Theme.key_chat_topPanelLine));
            this.pinnedMessageView.addView(this.pinnedLineView, LayoutHelper.createFrame(2, 32.0f, 51, 8.0f, 8.0f, 0.0f, 0.0f));
            this.pinnedMessageImageView = new BackupImageView(context);
            this.pinnedMessageView.addView(this.pinnedMessageImageView, LayoutHelper.createFrame(32, 32.0f, 51, 17.0f, 8.0f, 0.0f, 0.0f));
            this.pinnedMessageNameTextView = new SimpleTextView(context);
            this.pinnedMessageNameTextView.setTextSize(14);
            this.pinnedMessageNameTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelTitle));
            this.pinnedMessageNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.pinnedMessageView.addView(this.pinnedMessageNameTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(18.0f), 51, 18.0f, 7.3f, 52.0f, 0.0f));
            this.pinnedMessageTextView = new SimpleTextView(context);
            this.pinnedMessageTextView.setTextSize(14);
            this.pinnedMessageTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelMessage));
            this.pinnedMessageView.addView(this.pinnedMessageTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(18.0f), 51, 18.0f, 25.3f, 52.0f, 0.0f));
            this.closePinned = new ImageView(context);
            this.closePinned.setImageResource(R.drawable.miniplayer_close);
            this.closePinned.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelClose), Mode.MULTIPLY));
            this.closePinned.setScaleType(ScaleType.CENTER);
            this.pinnedMessageView.addView(this.closePinned, LayoutHelper.createFrame(48, 48, 53));
            this.closePinned.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        if (ChatActivity.this.currentChat.creator || ChatActivity.this.currentChat.editor) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("UnpinMessageAlert", R.string.UnpinMessageAlert));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, 0, false);
                                }
                            });
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            ChatActivity.this.showDialog(builder.create());
                            return;
                        }
                        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("pin_" + ChatActivity.this.dialog_id, ChatActivity.this.info.pinned_msg_id).commit();
                        ChatActivity.this.updatePinnedMessageView(true);
                    }
                }
            });
        }
        this.reportSpamView = new LinearLayout(context);
        this.reportSpamView.setTag(Integer.valueOf(1));
        this.reportSpamView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
        this.reportSpamView.setVisibility(8);
        this.reportSpamView.setBackgroundResource(R.drawable.blockpanel);
        this.reportSpamView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelBackground), Mode.MULTIPLY));
        contentView.addView(this.reportSpamView, LayoutHelper.createFrame(-1, 50, 51));
        this.addToContactsButton = new TextView(context);
        this.addToContactsButton.setTextColor(Theme.getColor(Theme.key_chat_addContact));
        this.addToContactsButton.setVisibility(8);
        this.addToContactsButton.setTextSize(1, 14.0f);
        this.addToContactsButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addToContactsButton.setSingleLine(true);
        this.addToContactsButton.setMaxLines(1);
        this.addToContactsButton.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.addToContactsButton.setGravity(17);
        this.addToContactsButton.setText(LocaleController.getString("AddContactChat", R.string.AddContactChat));
        this.reportSpamView.addView(this.addToContactsButton, LayoutHelper.createLinear(-1, -1, 0.5f, 51, 0, 0, 0, AndroidUtilities.dp(1.0f)));
        this.addToContactsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("user_id", ChatActivity.this.currentUser.id);
                args.putBoolean("addContact", true);
                ChatActivity.this.presentFragment(new ContactAddActivity(args));
            }
        });
        this.reportSpamContainer = new FrameLayout(context);
        this.reportSpamView.addView(this.reportSpamContainer, LayoutHelper.createLinear(-1, -1, 1.0f, 51, 0, 0, 0, AndroidUtilities.dp(1.0f)));
        this.reportSpamButton = new TextView(context);
        this.reportSpamButton.setTextColor(Theme.getColor(Theme.key_chat_reportSpam));
        this.reportSpamButton.setTextSize(1, 14.0f);
        this.reportSpamButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.reportSpamButton.setSingleLine(true);
        this.reportSpamButton.setMaxLines(1);
        if (this.currentChat != null) {
            this.reportSpamButton.setText(LocaleController.getString("ReportSpamAndLeave", R.string.ReportSpamAndLeave));
        } else {
            this.reportSpamButton.setText(LocaleController.getString("ReportSpam", R.string.ReportSpam));
        }
        this.reportSpamButton.setGravity(17);
        this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0f), 0, AndroidUtilities.dp(50.0f), 0);
        this.reportSpamContainer.addView(this.reportSpamButton, LayoutHelper.createFrame(-1, -1, 51));
        this.reportSpamButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ChatActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                    if (ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup) {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertChannel", R.string.ReportSpamAlertChannel));
                    } else if (ChatActivity.this.currentChat != null) {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertGroup", R.string.ReportSpamAlertGroup));
                    } else {
                        builder.setMessage(LocaleController.getString("ReportSpamAlert", R.string.ReportSpamAlert));
                    }
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ChatActivity.this.currentUser != null) {
                                MessagesController.getInstance().blockUser(ChatActivity.this.currentUser.id);
                            }
                            MessagesController.getInstance().reportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat, ChatActivity.this.currentEncryptedChat);
                            ChatActivity.this.updateSpamView();
                            if (ChatActivity.this.currentChat == null) {
                                MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                            } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                                MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                            } else {
                                MessagesController.getInstance().deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                            }
                            ChatActivity.this.finishFragment();
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    ChatActivity.this.showDialog(builder.create());
                }
            }
        });
        this.closeReportSpam = new ImageView(context);
        this.closeReportSpam.setImageResource(R.drawable.miniplayer_close);
        this.closeReportSpam.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelClose), Mode.MULTIPLY));
        this.closeReportSpam.setScaleType(ScaleType.CENTER);
        this.reportSpamContainer.addView(this.closeReportSpam, LayoutHelper.createFrame(48, 48, 53));
        this.closeReportSpam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MessagesController.getInstance().hideReportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat);
                ChatActivity.this.updateSpamView();
            }
        });
        this.alertView = new FrameLayout(context);
        this.alertView.setTag(Integer.valueOf(1));
        this.alertView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
        this.alertView.setVisibility(8);
        this.alertView.setBackgroundResource(R.drawable.blockpanel);
        this.alertView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelBackground), Mode.MULTIPLY));
        contentView.addView(this.alertView, LayoutHelper.createFrame(-1, 50, 51));
        this.alertNameTextView = new TextView(context);
        this.alertNameTextView.setTextSize(1, 14.0f);
        this.alertNameTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelTitle));
        this.alertNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.alertNameTextView.setSingleLine(true);
        this.alertNameTextView.setEllipsize(TruncateAt.END);
        this.alertNameTextView.setMaxLines(1);
        this.alertView.addView(this.alertNameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 8.0f, 5.0f, 8.0f, 0.0f));
        this.alertTextView = new TextView(context);
        this.alertTextView.setTextSize(1, 14.0f);
        this.alertTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelMessage));
        this.alertTextView.setSingleLine(true);
        this.alertTextView.setEllipsize(TruncateAt.END);
        this.alertTextView.setMaxLines(1);
        this.alertView.addView(this.alertTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 8.0f, 23.0f, 8.0f, 0.0f));
        if (!this.isBroadcast) {
            this.mentionContainer = new FrameLayout(context) {
                public void onDraw(Canvas canvas) {
                    if (ChatActivity.this.mentionListView.getChildCount() > 0) {
                        int top;
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                            top = ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(4.0f);
                        } else {
                            top = ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(2.0f);
                        }
                        int bottom = top + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        Theme.chat_composeShadowDrawable.setBounds(0, top, getMeasuredWidth(), bottom);
                        Theme.chat_composeShadowDrawable.draw(canvas);
                        canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                    }
                }

                public void requestLayout() {
                    if (!ChatActivity.this.mentionListViewIgnoreLayout) {
                        super.requestLayout();
                    }
                }
            };
            this.mentionContainer.setVisibility(8);
            this.mentionContainer.setWillNotDraw(false);
            contentView.addView(this.mentionContainer, LayoutHelper.createFrame(-1, 110, 83));
            this.mentionListView = new RecyclerListView(context) {
                private int lastHeight;
                private int lastWidth;

                public boolean onInterceptTouchEvent(MotionEvent event) {
                    if (!ChatActivity.this.mentionListViewIsScrolling && ChatActivity.this.mentionListViewScrollOffsetY != 0 && event.getY() < ((float) ChatActivity.this.mentionListViewScrollOffsetY)) {
                        return false;
                    }
                    boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, ChatActivity.this.mentionListView, 0, null);
                    if (super.onInterceptTouchEvent(event) || result) {
                        return true;
                    }
                    return false;
                }

                public boolean onTouchEvent(MotionEvent event) {
                    if (ChatActivity.this.mentionListViewIsScrolling || ChatActivity.this.mentionListViewScrollOffsetY == 0 || event.getY() >= ((float) ChatActivity.this.mentionListViewScrollOffsetY)) {
                        return super.onTouchEvent(event);
                    }
                    return false;
                }

                public void requestLayout() {
                    if (!ChatActivity.this.mentionListViewIgnoreLayout) {
                        super.requestLayout();
                    }
                }

                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    int width = r - l;
                    int height = b - t;
                    int newPosition = -1;
                    int newTop = 0;
                    if (ChatActivity.this.mentionListView != null && ChatActivity.this.mentionListViewLastViewPosition >= 0 && width == this.lastWidth && height - this.lastHeight != 0) {
                        newPosition = ChatActivity.this.mentionListViewLastViewPosition;
                        newTop = ((ChatActivity.this.mentionListViewLastViewTop + height) - this.lastHeight) - getPaddingTop();
                    }
                    super.onLayout(changed, l, t, r, b);
                    if (newPosition != -1) {
                        ChatActivity.this.mentionListViewIgnoreLayout = true;
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                            ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(newPosition, newTop);
                        } else {
                            ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(newPosition, newTop);
                        }
                        super.onLayout(false, l, t, r, b);
                        ChatActivity.this.mentionListViewIgnoreLayout = false;
                    }
                    this.lastHeight = height;
                    this.lastWidth = width;
                    ChatActivity.this.mentionListViewUpdateLayout();
                }
            };
            this.mentionListView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return StickerPreviewViewer.getInstance().onTouch(event, ChatActivity.this.mentionListView, 0, ChatActivity.this.mentionsOnItemClickListener, null);
                }
            });
            this.mentionListView.setTag(Integer.valueOf(2));
            this.mentionLayoutManager = new LinearLayoutManager(context) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.mentionLayoutManager.setOrientation(1);
            this.mentionGridLayoutManager = new ExtendedGridLayoutManager(context, 100) {
                private Size size = new Size();

                protected Size getSizeForItem(int i) {
                    float f = 100.0f;
                    if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                        i++;
                    }
                    BotInlineResult object = ChatActivity.this.mentionsAdapter.getItem(i);
                    if (object instanceof BotInlineResult) {
                        BotInlineResult inlineResult = object;
                        if (inlineResult.document != null) {
                            float f2;
                            Size size = this.size;
                            if (inlineResult.document.thumb != null) {
                                f2 = (float) inlineResult.document.thumb.w;
                            } else {
                                f2 = 100.0f;
                            }
                            size.width = f2;
                            Size size2 = this.size;
                            if (inlineResult.document.thumb != null) {
                                f = (float) inlineResult.document.thumb.h;
                            }
                            size2.height = f;
                            for (int b = 0; b < inlineResult.document.attributes.size(); b++) {
                                DocumentAttribute attribute = (DocumentAttribute) inlineResult.document.attributes.get(b);
                                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                    this.size.width = (float) attribute.w;
                                    this.size.height = (float) attribute.h;
                                    break;
                                }
                            }
                        } else {
                            this.size.width = (float) inlineResult.w;
                            this.size.height = (float) inlineResult.h;
                        }
                    }
                    return this.size;
                }

                protected int getFlowItemCount() {
                    if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                        return getItemCount() - 1;
                    }
                    return super.getFlowItemCount();
                }
            };
            this.mentionGridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int position) {
                    if (ChatActivity.this.mentionsAdapter.getItem(position) instanceof TL_inlineBotSwitchPM) {
                        return 100;
                    }
                    if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                        position--;
                    }
                    return ChatActivity.this.mentionGridLayoutManager.getSpanSizeForItem(position);
                }
            });
            this.mentionListView.addItemDecoration(new ItemDecoration() {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                    int i = 0;
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    if (parent.getLayoutManager() == ChatActivity.this.mentionGridLayoutManager) {
                        int position = parent.getChildAdapterPosition(view);
                        if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                            outRect.top = AndroidUtilities.dp(2.0f);
                        } else if (position != 0) {
                            position--;
                            if (!ChatActivity.this.mentionGridLayoutManager.isFirstRow(position)) {
                                outRect.top = AndroidUtilities.dp(2.0f);
                            }
                        } else {
                            return;
                        }
                        if (!ChatActivity.this.mentionGridLayoutManager.isLastInRow(position)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        outRect.right = i;
                    }
                }
            });
            this.mentionListView.setItemAnimator(null);
            this.mentionListView.setLayoutAnimation(null);
            this.mentionListView.setClipToPadding(false);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setOverScrollMode(2);
            this.mentionContainer.addView(this.mentionListView, LayoutHelper.createFrame(-1, -1.0f));
            recyclerListView = this.mentionListView;
            chatActivityAdapter = new MentionsAdapter(context, false, this.dialog_id, new MentionsAdapterDelegate() {
                public void needChangePanelVisibility(boolean show) {
                    if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                        ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionGridLayoutManager);
                    } else {
                        ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionLayoutManager);
                    }
                    if (show) {
                        if (ChatActivity.this.mentionListAnimation != null) {
                            ChatActivity.this.mentionListAnimation.cancel();
                            ChatActivity.this.mentionListAnimation = null;
                        }
                        if (ChatActivity.this.mentionContainer.getVisibility() == 0) {
                            ChatActivity.this.mentionContainer.setAlpha(1.0f);
                            return;
                        }
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                            ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(0, 10000);
                        } else {
                            ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                        }
                        if (ChatActivity.this.allowStickersPanel && (!ChatActivity.this.mentionsAdapter.isBotContext() || ChatActivity.this.allowContextBotPanel || ChatActivity.this.allowContextBotPanelSecond)) {
                            if (ChatActivity.this.currentEncryptedChat != null && ChatActivity.this.mentionsAdapter.isBotContext()) {
                                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                                if (!preferences.getBoolean("secretbot", false)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setMessage(LocaleController.getString("SecretChatContextBotAlert", R.string.SecretChatContextBotAlert));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                    ChatActivity.this.showDialog(builder.create());
                                    preferences.edit().putBoolean("secretbot", true).commit();
                                }
                            }
                            ChatActivity.this.mentionContainer.setVisibility(0);
                            ChatActivity.this.mentionContainer.setTag(null);
                            ChatActivity.this.mentionListAnimation = new AnimatorSet();
                            ChatActivity.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", new float[]{0.0f, 1.0f})});
                            ChatActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                                        ChatActivity.this.mentionListAnimation = null;
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                                        ChatActivity.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            ChatActivity.this.mentionListAnimation.setDuration(200);
                            ChatActivity.this.mentionListAnimation.start();
                            return;
                        }
                        ChatActivity.this.mentionContainer.setAlpha(1.0f);
                        ChatActivity.this.mentionContainer.setVisibility(4);
                        return;
                    }
                    if (ChatActivity.this.mentionListAnimation != null) {
                        ChatActivity.this.mentionListAnimation.cancel();
                        ChatActivity.this.mentionListAnimation = null;
                    }
                    if (ChatActivity.this.mentionContainer.getVisibility() == 8) {
                        return;
                    }
                    if (ChatActivity.this.allowStickersPanel) {
                        ChatActivity.this.mentionListAnimation = new AnimatorSet();
                        AnimatorSet access$9100 = ChatActivity.this.mentionListAnimation;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", new float[]{0.0f});
                        access$9100.playTogether(animatorArr);
                        ChatActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                                    ChatActivity.this.mentionContainer.setVisibility(8);
                                    ChatActivity.this.mentionContainer.setTag(null);
                                    ChatActivity.this.mentionListAnimation = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                                    ChatActivity.this.mentionListAnimation = null;
                                }
                            }
                        });
                        ChatActivity.this.mentionListAnimation.setDuration(200);
                        ChatActivity.this.mentionListAnimation.start();
                        return;
                    }
                    ChatActivity.this.mentionContainer.setTag(null);
                    ChatActivity.this.mentionContainer.setVisibility(8);
                }

                public void onContextSearch(boolean searching) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        ChatActivity.this.chatActivityEnterView.setCaption(ChatActivity.this.mentionsAdapter.getBotCaption());
                        ChatActivity.this.chatActivityEnterView.showContextProgress(searching);
                    }
                }

                public void onContextClick(BotInlineResult result) {
                    if (ChatActivity.this.getParentActivity() != null && result.content_url != null) {
                        if (result.type.equals(MimeTypes.BASE_TYPE_VIDEO) || result.type.equals("web_player_video")) {
                            EmbedBottomSheet.show(ChatActivity.this.getParentActivity(), result.title != null ? result.title : "", result.description, result.content_url, result.content_url, result.w, result.h);
                        } else {
                            Browser.openUrl(ChatActivity.this.getParentActivity(), result.content_url);
                        }
                    }
                }
            });
            this.mentionsAdapter = chatActivityAdapter;
            recyclerListView.setAdapter(chatActivityAdapter);
            if (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup)) {
                this.mentionsAdapter.setBotInfo(this.botInfo);
            }
            this.mentionsAdapter.setParentFragment(this);
            this.mentionsAdapter.setChatInfo(this.info);
            this.mentionsAdapter.setNeedUsernames(this.currentChat != null);
            MentionsAdapter mentionsAdapter = this.mentionsAdapter;
            z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
            mentionsAdapter.setNeedBotContext(z);
            this.mentionsAdapter.setBotsCount(this.currentChat != null ? this.botsCount : 1);
            recyclerListView = this.mentionListView;
            OnItemClickListener anonymousClass34 = new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    TLObject object = ChatActivity.this.mentionsAdapter.getItem(position);
                    int start = ChatActivity.this.mentionsAdapter.getResultStartPosition();
                    int len = ChatActivity.this.mentionsAdapter.getResultLength();
                    if (object instanceof User) {
                        User user = (User) object;
                        if (user == null) {
                            return;
                        }
                        if (user.username != null) {
                            ChatActivity.this.chatActivityEnterView.replaceWithText(start, len, "@" + user.username + " ");
                            return;
                        }
                        String name = user.first_name;
                        if (name == null || name.length() == 0) {
                            name = user.last_name;
                        }
                        Spannable spannableString = new SpannableString(name + " ");
                        spannableString.setSpan(new URLSpanUserMention("" + user.id, true), 0, spannableString.length(), 33);
                        ChatActivity.this.chatActivityEnterView.replaceWithText(start, len, spannableString);
                    } else if (object instanceof String) {
                        if (ChatActivity.this.mentionsAdapter.isBotCommands()) {
                            SendMessagesHelper.getInstance().sendMessage((String) object, ChatActivity.this.dialog_id, null, null, false, null, null, null);
                            ChatActivity.this.chatActivityEnterView.setFieldText("");
                            return;
                        }
                        ChatActivity.this.chatActivityEnterView.replaceWithText(start, len, object + " ");
                    } else if (object instanceof BotInlineResult) {
                        if (ChatActivity.this.chatActivityEnterView.getFieldText() != null) {
                            BotInlineResult result = (BotInlineResult) object;
                            if (VERSION.SDK_INT < 16 || ((!result.type.equals("photo") || (result.photo == null && result.content_url == null)) && ((!result.type.equals("gif") || (result.document == null && result.content_url == null)) && (!result.type.equals(MimeTypes.BASE_TYPE_VIDEO) || result.document == null)))) {
                                ChatActivity.this.sendBotInlineResult(result);
                                return;
                            }
                            ArrayList<Object> arrayList = ChatActivity.this.botContextResults = new ArrayList(ChatActivity.this.mentionsAdapter.getSearchResultBotContext());
                            PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                            PhotoViewer.getInstance().openPhotoForSelect(arrayList, ChatActivity.this.mentionsAdapter.getItemPosition(position), 3, ChatActivity.this.botContextProvider, null);
                        }
                    } else if (object instanceof TL_inlineBotSwitchPM) {
                        ChatActivity.this.processInlineBotContextPM((TL_inlineBotSwitchPM) object);
                    }
                }
            };
            this.mentionsOnItemClickListener = anonymousClass34;
            recyclerListView.setOnItemClickListener(anonymousClass34);
            this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemClick(View view, int position) {
                    boolean z = false;
                    if (ChatActivity.this.getParentActivity() == null || !ChatActivity.this.mentionsAdapter.isLongClickEnabled()) {
                        return false;
                    }
                    Object object = ChatActivity.this.mentionsAdapter.getItem(position);
                    if (!(object instanceof String)) {
                        return false;
                    }
                    if (!ChatActivity.this.mentionsAdapter.isBotCommands()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ChatActivity.this.mentionsAdapter.clearRecentHashtags();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ChatActivity.this.showDialog(builder.create());
                        return true;
                    } else if (!URLSpanBotCommand.enabled) {
                        return false;
                    } else {
                        ChatActivity.this.chatActivityEnterView.setFieldText("");
                        ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                        String str = (String) object;
                        if (ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup) {
                            z = true;
                        }
                        chatActivityEnterView.setCommand(null, str, true, z);
                        return true;
                    }
                }
            });
            this.mentionListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    boolean z = true;
                    ChatActivity chatActivity = ChatActivity.this;
                    if (newState != 1) {
                        z = false;
                    }
                    chatActivity.mentionListViewIsScrolling = z;
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int lastVisibleItem;
                    int visibleItemCount;
                    if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                        lastVisibleItem = ChatActivity.this.mentionGridLayoutManager.findLastVisibleItemPosition();
                    } else {
                        lastVisibleItem = ChatActivity.this.mentionLayoutManager.findLastVisibleItemPosition();
                    }
                    if (lastVisibleItem == -1) {
                        visibleItemCount = 0;
                    } else {
                        visibleItemCount = lastVisibleItem;
                    }
                    if (visibleItemCount > 0 && lastVisibleItem > ChatActivity.this.mentionsAdapter.getItemCount() - 5) {
                        ChatActivity.this.mentionsAdapter.searchForContextBotForNextOffset();
                    }
                    ChatActivity.this.mentionListViewUpdateLayout();
                }
            });
        }
        this.pagedownButton = new FrameLayout(context);
        this.pagedownButton.setVisibility(4);
        contentView.addView(this.pagedownButton, LayoutHelper.createFrame(46, 59.0f, 85, 0.0f, 0.0f, 7.0f, 5.0f));
        this.pagedownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.createUnreadMessageAfterId != 0) {
                    ChatActivity.this.scrollToMessageId(ChatActivity.this.createUnreadMessageAfterId, 0, false, ChatActivity.this.returnToLoadIndex);
                } else if (ChatActivity.this.returnToMessageId > 0) {
                    ChatActivity.this.scrollToMessageId(ChatActivity.this.returnToMessageId, 0, true, ChatActivity.this.returnToLoadIndex);
                } else {
                    ChatActivity.this.scrollToLastMessage(true);
                }
            }
        });
        this.pagedownButtonImage = new ImageView(context);
        this.pagedownButtonImage.setImageResource(R.drawable.pagedown);
        this.pagedownButtonImage.setScaleType(ScaleType.CENTER);
        this.pagedownButtonImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_goDownButtonIcon), Mode.MULTIPLY));
        this.pagedownButtonImage.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        Drawable drawable = Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(Theme.key_chat_goDownButton));
        Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.pagedown_shadow).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_goDownButtonShadow), Mode.MULTIPLY));
        Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
        this.pagedownButtonImage.setBackgroundDrawable(combinedDrawable);
        this.pagedownButton.addView(this.pagedownButtonImage, LayoutHelper.createFrame(46, 46, 83));
        this.pagedownButtonCounter = new TextView(context);
        this.pagedownButtonCounter.setVisibility(4);
        this.pagedownButtonCounter.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pagedownButtonCounter.setTextSize(1, 13.0f);
        this.pagedownButtonCounter.setTextColor(Theme.getColor(Theme.key_chat_goDownButtonCounter));
        this.pagedownButtonCounter.setGravity(17);
        this.pagedownButtonCounter.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(11.5f), Theme.getColor(Theme.key_chat_goDownButtonCounterBackground)));
        this.pagedownButtonCounter.setMinWidth(AndroidUtilities.dp(23.0f));
        this.pagedownButtonCounter.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        this.pagedownButton.addView(this.pagedownButtonCounter, LayoutHelper.createFrame(-2, 23, 49));
        this.chatActivityEnterView = new ChatActivityEnterView(getParentActivity(), contentView, this, true);
        this.chatActivityEnterView.setDialogId(this.dialog_id);
        this.chatActivityEnterView.setId(1000);
        this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23;
        if (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46) {
            z2 = true;
        } else {
            z2 = false;
        }
        chatActivityEnterView.setAllowStickersAndGifs(z, z2);
        contentView.addView(this.chatActivityEnterView, contentView.getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterViewDelegate() {
            public void onMessageSend(CharSequence message) {
                ChatActivity.this.moveScrollToLastMessage();
                ChatActivity.this.showReplyPanel(false, null, null, null, false);
                if (ChatActivity.this.mentionsAdapter != null) {
                    ChatActivity.this.mentionsAdapter.addHashtagsFromMessage(message);
                }
            }

            public void onTextChanged(final CharSequence text, boolean bigChange) {
                MediaController instance = MediaController.getInstance();
                boolean z = !(text == null || text.length() == 0) || ChatActivity.this.chatActivityEnterView.isEditingMessage();
                instance.setInputFieldHasText(z);
                if (!(ChatActivity.this.stickersAdapter == null || ChatActivity.this.chatActivityEnterView.isEditingMessage())) {
                    ChatActivity.this.stickersAdapter.loadStikersForEmoji(text);
                }
                if (ChatActivity.this.mentionsAdapter != null) {
                    ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), ChatActivity.this.chatActivityEnterView.getCursorPosition(), ChatActivity.this.messages);
                }
                if (ChatActivity.this.waitingForCharaterEnterRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable);
                    ChatActivity.this.waitingForCharaterEnterRunnable = null;
                }
                if (!ChatActivity.this.chatActivityEnterView.isMessageWebPageSearchEnabled()) {
                    return;
                }
                if (!ChatActivity.this.chatActivityEnterView.isEditingMessage() || !ChatActivity.this.chatActivityEnterView.isEditingCaption()) {
                    if (bigChange) {
                        ChatActivity.this.searchLinks(text, true);
                        return;
                    }
                    ChatActivity.this.waitingForCharaterEnterRunnable = new Runnable() {
                        public void run() {
                            if (this == ChatActivity.this.waitingForCharaterEnterRunnable) {
                                ChatActivity.this.searchLinks(text, false);
                                ChatActivity.this.waitingForCharaterEnterRunnable = null;
                            }
                        }
                    };
                    AndroidUtilities.runOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable, AndroidUtilities.WEB_URL == null ? 3000 : 1000);
                }
            }

            public void needSendTyping() {
                MessagesController.getInstance().sendTyping(ChatActivity.this.dialog_id, 0, ChatActivity.this.classGuid);
            }

            public void onAttachButtonHidden() {
                if (!ChatActivity.this.actionBar.isSearchFieldVisible()) {
                    if (ChatActivity.this.attachItem != null) {
                        ChatActivity.this.attachItem.setVisibility(0);
                    }
                    if (ChatActivity.this.headerItem != null) {
                        ChatActivity.this.headerItem.setVisibility(8);
                    }
                }
            }

            public void onAttachButtonShow() {
                if (!ChatActivity.this.actionBar.isSearchFieldVisible()) {
                    if (ChatActivity.this.attachItem != null) {
                        ChatActivity.this.attachItem.setVisibility(8);
                    }
                    if (ChatActivity.this.headerItem != null) {
                        ChatActivity.this.headerItem.setVisibility(0);
                    }
                }
            }

            public void onMessageEditEnd(boolean loading) {
                if (!loading) {
                    MentionsAdapter access$5200 = ChatActivity.this.mentionsAdapter;
                    boolean z = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                    access$5200.setNeedBotContext(z);
                    ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                    ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                    ChatActivity.this.chatListView.setClickable(true);
                    ChatActivity.this.chatListView.setLongClickable(true);
                    ChatActivity.this.mentionsAdapter.setAllowNewMentions(true);
                    ChatActivity.this.actionModeTitleContainer.setVisibility(8);
                    ChatActivity.this.selectedMessagesCountTextView.setVisibility(0);
                    ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                    if (ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 23) {
                        z = true;
                    } else {
                        z = false;
                    }
                    boolean z2 = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                    chatActivityEnterView.setAllowStickersAndGifs(z, z2);
                    if (ChatActivity.this.editingMessageObjectReqId != 0) {
                        ConnectionsManager.getInstance().cancelRequest(ChatActivity.this.editingMessageObjectReqId, true);
                        ChatActivity.this.editingMessageObjectReqId = 0;
                    }
                    ChatActivity.this.actionBar.hideActionMode();
                    ChatActivity.this.updatePinnedMessageView(true);
                    ChatActivity.this.updateVisibleRows();
                }
            }

            public void onWindowSizeChanged(int size) {
                boolean z = true;
                if (size < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
                    ChatActivity.this.allowStickersPanel = false;
                    if (ChatActivity.this.stickersPanel.getVisibility() == 0) {
                        ChatActivity.this.stickersPanel.setVisibility(4);
                    }
                    if (ChatActivity.this.mentionContainer != null && ChatActivity.this.mentionContainer.getVisibility() == 0) {
                        ChatActivity.this.mentionContainer.setVisibility(4);
                    }
                } else {
                    ChatActivity.this.allowStickersPanel = true;
                    if (ChatActivity.this.stickersPanel.getVisibility() == 4) {
                        ChatActivity.this.stickersPanel.setVisibility(0);
                    }
                    if (ChatActivity.this.mentionContainer != null && ChatActivity.this.mentionContainer.getVisibility() == 4 && (!ChatActivity.this.mentionsAdapter.isBotContext() || ChatActivity.this.allowContextBotPanel || ChatActivity.this.allowContextBotPanelSecond)) {
                        ChatActivity.this.mentionContainer.setVisibility(0);
                        ChatActivity.this.mentionContainer.setTag(null);
                    }
                }
                ChatActivity chatActivity = ChatActivity.this;
                if (ChatActivity.this.chatActivityEnterView.isPopupShowing()) {
                    z = false;
                }
                chatActivity.allowContextBotPanel = z;
                ChatActivity.this.checkContextBotPanel();
            }

            public void onStickersTab(boolean opened) {
                if (ChatActivity.this.emojiButtonRed != null) {
                    ChatActivity.this.emojiButtonRed.setVisibility(8);
                }
                ChatActivity.this.allowContextBotPanelSecond = !opened;
                ChatActivity.this.checkContextBotPanel();
            }

            public void didPressedAttachButton() {
                ChatActivity.this.openAttachMenu();
            }
        });
        View anonymousClass39 = new FrameLayout(context) {
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (ChatActivity.this.chatActivityEnterView != null) {
                    ChatActivity.this.chatActivityEnterView.invalidate();
                }
                if (getVisibility() != 8) {
                    int height = getLayoutParams().height;
                    if (ChatActivity.this.chatListView != null) {
                        ChatActivity.this.chatListView.setTranslationY(translationY);
                    }
                    if (ChatActivity.this.progressView != null) {
                        ChatActivity.this.progressView.setTranslationY(translationY);
                    }
                    if (ChatActivity.this.mentionContainer != null) {
                        ChatActivity.this.mentionContainer.setTranslationY(translationY);
                    }
                    if (ChatActivity.this.pagedownButton != null) {
                        ChatActivity.this.pagedownButton.setTranslationY(translationY);
                    }
                }
            }

            public boolean hasOverlappingRendering() {
                return false;
            }

            public void setVisibility(int visibility) {
                float f = 0.0f;
                super.setVisibility(visibility);
                if (visibility == 8) {
                    if (ChatActivity.this.chatListView != null) {
                        ChatActivity.this.chatListView.setTranslationY(0.0f);
                    }
                    if (ChatActivity.this.progressView != null) {
                        ChatActivity.this.progressView.setTranslationY(0.0f);
                    }
                    if (ChatActivity.this.mentionContainer != null) {
                        ChatActivity.this.mentionContainer.setTranslationY(0.0f);
                    }
                    if (ChatActivity.this.pagedownButton != null) {
                        FrameLayout access$5700 = ChatActivity.this.pagedownButton;
                        if (ChatActivity.this.pagedownButton.getTag() == null) {
                            f = (float) AndroidUtilities.dp(100.0f);
                        }
                        access$5700.setTranslationY(f);
                    }
                }
            }
        };
        anonymousClass39.setClickable(true);
        this.chatActivityEnterView.addTopView(anonymousClass39, 48);
        this.replyLineView = new View(context);
        this.replyLineView.setBackgroundColor(Theme.getColor(Theme.key_chat_replyPanelLine));
        anonymousClass39.addView(this.replyLineView, LayoutHelper.createFrame(-1, 1, 83));
        this.replyIconImageView = new ImageView(context);
        this.replyIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_replyPanelIcons), Mode.MULTIPLY));
        this.replyIconImageView.setScaleType(ScaleType.CENTER);
        anonymousClass39.addView(this.replyIconImageView, LayoutHelper.createFrame(52, 46, 51));
        this.replyCloseImageView = new ImageView(context);
        this.replyCloseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_replyPanelClose), Mode.MULTIPLY));
        this.replyCloseImageView.setImageResource(R.drawable.msg_panel_clear);
        this.replyCloseImageView.setScaleType(ScaleType.CENTER);
        anonymousClass39.addView(this.replyCloseImageView, LayoutHelper.createFrame(52, 46.0f, 53, 0.0f, 0.5f, 0.0f, 0.0f));
        this.replyCloseImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ChatActivity.this.forwardingMessages != null) {
                    ChatActivity.this.forwardingMessages.clear();
                }
                ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, true);
            }
        });
        this.replyNameTextView = new SimpleTextView(context);
        this.replyNameTextView.setTextSize(14);
        this.replyNameTextView.setTextColor(Theme.getColor(Theme.key_chat_replyPanelName));
        this.replyNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        anonymousClass39.addView(this.replyNameTextView, LayoutHelper.createFrame(-1, 18.0f, 51, 52.0f, 6.0f, 52.0f, 0.0f));
        this.replyObjectTextView = new SimpleTextView(context);
        this.replyObjectTextView.setTextSize(14);
        this.replyObjectTextView.setTextColor(Theme.getColor(Theme.key_chat_replyPanelMessage));
        anonymousClass39.addView(this.replyObjectTextView, LayoutHelper.createFrame(-1, 18.0f, 51, 52.0f, 24.0f, 52.0f, 0.0f));
        this.replyImageView = new BackupImageView(context);
        anonymousClass39.addView(this.replyImageView, LayoutHelper.createFrame(34, 34.0f, 51, 52.0f, 6.0f, 0.0f, 0.0f));
        this.stickersPanel = new FrameLayout(context);
        this.stickersPanel.setVisibility(8);
        contentView.addView(this.stickersPanel, LayoutHelper.createFrame(-2, 81.5f, 83, 0.0f, 0.0f, 0.0f, 38.0f));
        this.stickersListView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, ChatActivity.this.stickersListView, 0, null);
                if (super.onInterceptTouchEvent(event) || result) {
                    return true;
                }
                return false;
            }
        };
        this.stickersListView.setTag(Integer.valueOf(3));
        this.stickersListView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return StickerPreviewViewer.getInstance().onTouch(event, ChatActivity.this.stickersListView, 0, ChatActivity.this.stickersOnItemClickListener, null);
            }
        });
        this.stickersListView.setDisallowInterceptTouchEvents(true);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(0);
        this.stickersListView.setLayoutManager(linearLayoutManager);
        this.stickersListView.setClipToPadding(false);
        this.stickersListView.setOverScrollMode(2);
        this.stickersPanel.addView(this.stickersListView, LayoutHelper.createFrame(-1, 78.0f));
        initStickers();
        this.stickersPanelArrow = new ImageView(context);
        this.stickersPanelArrow.setImageResource(R.drawable.stickers_back_arrow);
        this.stickersPanelArrow.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_stickersHintPanel), Mode.MULTIPLY));
        this.stickersPanel.addView(this.stickersPanelArrow, LayoutHelper.createFrame(-2, -2.0f, 83, 53.0f, 0.0f, 0.0f, 0.0f));
        this.searchContainer = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        this.searchUpButton = new ImageView(context);
        this.searchUpButton.setScaleType(ScaleType.CENTER);
        this.searchUpButton.setImageResource(R.drawable.search_up);
        this.searchUpButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0f));
        this.searchUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, 1);
            }
        });
        this.searchDownButton = new ImageView(context);
        this.searchDownButton.setScaleType(ScaleType.CENTER);
        this.searchDownButton.setImageResource(R.drawable.search_down);
        this.searchDownButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 51, 48.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, 2);
            }
        });
        this.searchCalendarButton = new ImageView(context);
        this.searchCalendarButton.setScaleType(ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(R.drawable.search_calendar);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        this.searchCalendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(ChatActivity.this.searchItem.getSearchField());
                    Calendar calendar = Calendar.getInstance();
                    try {
                        DatePickerDialog dialog = new DatePickerDialog(ChatActivity.this.getParentActivity(), new OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.clear();
                                calendar.set(year, month, dayOfMonth);
                                int date = (int) (calendar.getTime().getTime() / 1000);
                                ChatActivity.this.clearChatData();
                                ChatActivity.this.waitingForLoad.add(Integer.valueOf(ChatActivity.this.lastLoadIndex));
                                MessagesController.getInstance().loadMessages(ChatActivity.this.dialog_id, 30, 0, date, true, 0, ChatActivity.this.classGuid, 4, 0, ChatObject.isChannel(ChatActivity.this.currentChat), ChatActivity.this.lastLoadIndex = ChatActivity.this.lastLoadIndex + 1);
                            }
                        }, calendar.get(1), calendar.get(2), calendar.get(5));
                        final DatePicker datePicker = dialog.getDatePicker();
                        datePicker.setMinDate(1375315200000L);
                        datePicker.setMaxDate(System.currentTimeMillis());
                        dialog.setButton(-1, LocaleController.getString("JumpToDate", R.string.JumpToDate), dialog);
                        dialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        if (VERSION.SDK_INT >= 21) {
                            dialog.setOnShowListener(new OnShowListener() {
                                public void onShow(DialogInterface dialog) {
                                    int count = datePicker.getChildCount();
                                    for (int a = 0; a < count; a++) {
                                        View child = datePicker.getChildAt(a);
                                        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                                        layoutParams.width = -1;
                                        child.setLayoutParams(layoutParams);
                                    }
                                    FileLog.e("");
                                }
                            });
                        }
                        ChatActivity.this.showDialog(dialog);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        });
        this.searchCountText = new SimpleTextView(context);
        this.searchCountText.setTextColor(Theme.getColor(Theme.key_chat_searchPanelText));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlay = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlay.setWillNotDraw(false);
        this.bottomOverlay.setVisibility(4);
        this.bottomOverlay.setFocusable(true);
        this.bottomOverlay.setFocusableInTouchMode(true);
        this.bottomOverlay.setClickable(true);
        this.bottomOverlay.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        contentView.addView(this.bottomOverlay, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayText = new TextView(context);
        this.bottomOverlayText.setTextSize(1, 16.0f);
        this.bottomOverlayText.setTextColor(Theme.getColor(Theme.key_chat_secretChatStatusText));
        this.bottomOverlay.addView(this.bottomOverlayText, LayoutHelper.createFrame(-2, -2, 17));
        this.bottomOverlayChat = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.bottomOverlayChat.setVisibility(4);
        contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = null;
                    if (ChatActivity.this.currentUser == null || !ChatActivity.this.userBlocked) {
                        if (ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.bot && ChatActivity.this.botUser != null) {
                            if (ChatActivity.this.botUser.length() != 0) {
                                MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, ChatActivity.this.botUser);
                            } else {
                                SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                            }
                            ChatActivity.this.botUser = null;
                            ChatActivity.this.updateBottomOverlay();
                        } else if (!ChatObject.isChannel(ChatActivity.this.currentChat) || (ChatActivity.this.currentChat instanceof TL_channelForbidden)) {
                            builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, 0);
                                    ChatActivity.this.finishFragment();
                                }
                            });
                        } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                            MessagesController.getInstance().addUserToChat(ChatActivity.this.currentChat.id, UserConfig.getCurrentUser(), null, 0, null, null);
                        } else {
                            ChatActivity.this.toggleMute(true);
                        }
                    } else if (ChatActivity.this.currentUser.bot) {
                        String botUserLast = ChatActivity.this.botUser;
                        ChatActivity.this.botUser = null;
                        MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                        if (botUserLast == null || botUserLast.length() == 0) {
                            SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                        } else {
                            MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, botUserLast);
                        }
                    } else {
                        builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", R.string.AreYouSureUnblockContact));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                            }
                        });
                    }
                    if (builder != null) {
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ChatActivity.this.showDialog(builder.create());
                    }
                }
            }
        });
        this.bottomOverlayChatText = new TextView(context);
        this.bottomOverlayChatText.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        this.chatAdapter.updateRows();
        if (this.loading && this.messages.isEmpty()) {
            this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? 0 : 4);
            this.chatListView.setEmptyView(null);
        } else {
            this.progressView.setVisibility(4);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        }
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        if (this.userBlocked) {
            messageObject = null;
        } else {
            messageObject = this.botButtons;
        }
        chatActivityEnterView2.setButtons(messageObject);
        if (!AndroidUtilities.isTablet() || AndroidUtilities.isSmallTablet()) {
            View playerView = new PlayerView(context, this);
            this.playerView = playerView;
            contentView.addView(playerView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        updateContactStatus();
        updateBottomOverlay();
        updateSecretStatus();
        updateSpamView();
        updatePinnedMessageView(true);
        try {
            if (this.currentEncryptedChat != null && VERSION.SDK_INT >= 23 && (UserConfig.passcodeHash.length() == 0 || UserConfig.allowScreenCapture)) {
                getParentActivity().getWindow().setFlags(8192, 8192);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        fixLayoutInternal();
        contentView.addView(this.actionBar);
        return this.fragmentView;
    }

    private void sendBotInlineResult(BotInlineResult result) {
        int uid = this.mentionsAdapter.getContextBotId();
        HashMap<String, String> params = new HashMap();
        params.put(TtmlNode.ATTR_ID, result.id);
        params.put("query_id", "" + result.query_id);
        params.put("bot", "" + uid);
        params.put("bot_name", this.mentionsAdapter.getContextBotName());
        SendMessagesHelper.prepareSendingBotContextResult(result, params, this.dialog_id, this.replyingMessageObject);
        this.chatActivityEnterView.setFieldText("");
        showReplyPanel(false, null, null, null, false);
        SearchQuery.increaseInlineRaiting(uid);
    }

    private void mentionListViewUpdateLayout() {
        int newOffset = 0;
        if (this.mentionListView.getChildCount() <= 0) {
            this.mentionListViewScrollOffsetY = 0;
            this.mentionListViewLastViewPosition = -1;
            return;
        }
        View child = this.mentionListView.getChildAt(this.mentionListView.getChildCount() - 1);
        Holder holder = (Holder) this.mentionListView.findContainingViewHolder(child);
        if (holder != null) {
            this.mentionListViewLastViewPosition = holder.getAdapterPosition();
            this.mentionListViewLastViewTop = child.getTop();
        } else {
            this.mentionListViewLastViewPosition = -1;
        }
        child = this.mentionListView.getChildAt(0);
        holder = (Holder) this.mentionListView.findContainingViewHolder(child);
        if (child.getTop() > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = child.getTop();
        }
        if (this.mentionListViewScrollOffsetY != newOffset) {
            RecyclerListView recyclerListView = this.mentionListView;
            this.mentionListViewScrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            this.mentionListView.invalidate();
            this.mentionContainer.invalidate();
        }
    }

    private void checkBotCommands() {
        boolean z = true;
        URLSpanBotCommand.enabled = false;
        if (this.currentUser != null && this.currentUser.bot) {
            URLSpanBotCommand.enabled = true;
        } else if (this.info instanceof TL_chatFull) {
            int a = 0;
            while (a < this.info.participants.participants.size()) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                if (user == null || !user.bot) {
                    a++;
                } else {
                    URLSpanBotCommand.enabled = true;
                    return;
                }
            }
        } else if (this.info instanceof TL_channelFull) {
            if (this.info.bot_info.isEmpty() || this.currentChat == null || !this.currentChat.megagroup) {
                z = false;
            }
            URLSpanBotCommand.enabled = z;
        }
    }

    private void jumpToDate(int date) {
        if (!this.messages.isEmpty()) {
            MessageObject lastMessage = (MessageObject) this.messages.get(this.messages.size() - 1);
            if (((MessageObject) this.messages.get(0)).messageOwner.date >= date && lastMessage.messageOwner.date <= date) {
                int a = this.messages.size() - 1;
                while (a >= 0) {
                    MessageObject message = (MessageObject) this.messages.get(a);
                    if (message.messageOwner.date < date || message.getId() == 0) {
                        a--;
                    } else {
                        scrollToMessageId(message.getId(), 0, false, message.getDialogId() == this.mergeDialogId ? 1 : 0);
                        return;
                    }
                }
            } else if (((int) this.dialog_id) != 0) {
                clearChatData();
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController instance = MessagesController.getInstance();
                long j = this.dialog_id;
                int i = this.classGuid;
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                int i2 = this.lastLoadIndex;
                this.lastLoadIndex = i2 + 1;
                instance.loadMessages(j, 30, 0, date, true, 0, i, 4, 0, isChannel, i2);
                this.floatingDateView.setAlpha(0.0f);
                this.floatingDateView.setTag(null);
            }
        }
    }

    public void processInlineBotContextPM(TL_inlineBotSwitchPM object) {
        if (object != null) {
            User user = this.mentionsAdapter.getContextBotUser();
            if (user != null) {
                this.chatActivityEnterView.setFieldText("");
                if (this.dialog_id == ((long) user.id)) {
                    this.inlineReturn = this.dialog_id;
                    MessagesController.getInstance().sendBotStart(this.currentUser, object.start_param);
                    return;
                }
                Bundle args = new Bundle();
                args.putInt("user_id", user.id);
                args.putString("inline_query", object.start_param);
                args.putLong("inline_return", this.dialog_id);
                if (MessagesController.checkCanOpenChat(args, this)) {
                    presentFragment(new ChatActivity(args));
                }
            }
        }
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            this.chatAttachAlert = new ChatAttachAlert(getParentActivity(), this);
            this.chatAttachAlert.setDelegate(new ChatAttachViewDelegate() {
                public void didPressedButton(int button) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        if (button == 7) {
                            ChatActivity.this.chatAttachAlert.dismiss();
                            HashMap<Integer, PhotoEntry> selectedPhotos = ChatActivity.this.chatAttachAlert.getSelectedPhotos();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList<String> photos = new ArrayList();
                                ArrayList<String> captions = new ArrayList();
                                ArrayList<ArrayList<InputDocument>> masks = new ArrayList();
                                for (Entry<Integer, PhotoEntry> entry : selectedPhotos.entrySet()) {
                                    PhotoEntry photoEntry = (PhotoEntry) entry.getValue();
                                    if (photoEntry.imagePath != null) {
                                        photos.add(photoEntry.imagePath);
                                        captions.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                                        masks.add(!photoEntry.stickers.isEmpty() ? new ArrayList(photoEntry.stickers) : null);
                                    } else if (photoEntry.path != null) {
                                        photos.add(photoEntry.path);
                                        captions.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                                        masks.add(!photoEntry.stickers.isEmpty() ? new ArrayList(photoEntry.stickers) : null);
                                    }
                                    photoEntry.imagePath = null;
                                    photoEntry.thumbPath = null;
                                    photoEntry.caption = null;
                                    photoEntry.stickers.clear();
                                }
                                SendMessagesHelper.prepareSendingPhotos(photos, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, captions, masks, null);
                                ChatActivity.this.showReplyPanel(false, null, null, null, false);
                                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                                return;
                            }
                            return;
                        }
                        if (ChatActivity.this.chatAttachAlert != null) {
                            ChatActivity.this.chatAttachAlert.dismissWithButtonClick(button);
                        }
                        ChatActivity.this.processSelectedAttach(button);
                    }
                }

                public View getRevealView() {
                    return ChatActivity.this.chatActivityEnterView.getAttachButton();
                }

                public void didSelectBot(User user) {
                    if (ChatActivity.this.chatActivityEnterView != null && user.username != null && user.username.length() != 0) {
                        ChatActivity.this.chatActivityEnterView.setFieldText("@" + user.username + " ");
                        ChatActivity.this.chatActivityEnterView.openKeyboard();
                    }
                }

                public void onCameraOpened() {
                    ChatActivity.this.chatActivityEnterView.closeKeyboard();
                }
            });
        }
    }

    public long getDialogId() {
        return this.dialog_id;
    }

    public void setBotUser(String value) {
        if (this.inlineReturn != 0) {
            MessagesController.getInstance().sendBotStart(this.currentUser, value);
            return;
        }
        this.botUser = value;
        updateBottomOverlay();
    }

    public boolean playFirstUnreadVoiceMessage() {
        for (int a = this.messages.size() - 1; a >= 0; a--) {
            MessageObject messageObject = (MessageObject) this.messages.get(a);
            if (messageObject.isVoice() && messageObject.isContentUnread() && !messageObject.isOut() && messageObject.messageOwner.to_id.channel_id == 0) {
                MediaController.getInstance().setVoiceMessagesPlaylist(MediaController.getInstance().playAudio(messageObject) ? createVoiceMessagesPlaylist(messageObject, true) : null, true);
                return true;
            }
        }
        if (VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            return false;
        }
        getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
        return true;
    }

    private void initStickers() {
        if (this.chatActivityEnterView != null && getParentActivity() != null && this.stickersAdapter == null) {
            if (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23) {
                if (this.stickersAdapter != null) {
                    this.stickersAdapter.onDestroy();
                }
                this.stickersListView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
                RecyclerListView recyclerListView = this.stickersListView;
                Adapter stickersAdapter = new StickersAdapter(getParentActivity(), new StickersAdapterDelegate() {
                    public void needChangePanelVisibility(final boolean show) {
                        float f = 1.0f;
                        if (!show || ChatActivity.this.stickersPanel.getVisibility() != 0) {
                            if (show || ChatActivity.this.stickersPanel.getVisibility() != 8) {
                                if (show) {
                                    ChatActivity.this.stickersListView.scrollToPosition(0);
                                    ChatActivity.this.stickersPanel.setVisibility(ChatActivity.this.allowStickersPanel ? 0 : 4);
                                }
                                if (ChatActivity.this.runningAnimation != null) {
                                    ChatActivity.this.runningAnimation.cancel();
                                    ChatActivity.this.runningAnimation = null;
                                }
                                if (ChatActivity.this.stickersPanel.getVisibility() != 4) {
                                    float f2;
                                    ChatActivity.this.runningAnimation = new AnimatorSet();
                                    AnimatorSet access$12800 = ChatActivity.this.runningAnimation;
                                    Animator[] animatorArr = new Animator[1];
                                    FrameLayout access$11000 = ChatActivity.this.stickersPanel;
                                    String str = "alpha";
                                    float[] fArr = new float[2];
                                    if (show) {
                                        f2 = 0.0f;
                                    } else {
                                        f2 = 1.0f;
                                    }
                                    fArr[0] = f2;
                                    if (!show) {
                                        f = 0.0f;
                                    }
                                    fArr[1] = f;
                                    animatorArr[0] = ObjectAnimator.ofFloat(access$11000, str, fArr);
                                    access$12800.playTogether(animatorArr);
                                    ChatActivity.this.runningAnimation.setDuration(150);
                                    ChatActivity.this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animation) {
                                            if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animation)) {
                                                if (!show) {
                                                    ChatActivity.this.stickersAdapter.clearStickers();
                                                    ChatActivity.this.stickersPanel.setVisibility(8);
                                                    if (StickerPreviewViewer.getInstance().isVisible()) {
                                                        StickerPreviewViewer.getInstance().close();
                                                    }
                                                    StickerPreviewViewer.getInstance().reset();
                                                }
                                                ChatActivity.this.runningAnimation = null;
                                            }
                                        }

                                        public void onAnimationCancel(Animator animation) {
                                            if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animation)) {
                                                ChatActivity.this.runningAnimation = null;
                                            }
                                        }
                                    });
                                    ChatActivity.this.runningAnimation.start();
                                } else if (!show) {
                                    ChatActivity.this.stickersPanel.setVisibility(8);
                                }
                            }
                        }
                    }
                });
                this.stickersAdapter = stickersAdapter;
                recyclerListView.setAdapter(stickersAdapter);
                recyclerListView = this.stickersListView;
                OnItemClickListener anonymousClass52 = new OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        Document document = ChatActivity.this.stickersAdapter.getItem(position);
                        if (document instanceof TL_document) {
                            SendMessagesHelper.getInstance().sendSticker(document, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                            ChatActivity.this.showReplyPanel(false, null, null, null, false);
                            ChatActivity.this.chatActivityEnterView.addStickerToRecent(document);
                        }
                        ChatActivity.this.chatActivityEnterView.setFieldText("");
                    }
                };
                this.stickersOnItemClickListener = anonymousClass52;
                recyclerListView.setOnItemClickListener(anonymousClass52);
            }
        }
    }

    public void shareMyContact(final MessageObject messageObject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ShareYouPhoneNumberTitle", R.string.ShareYouPhoneNumberTitle));
        if (this.currentUser == null) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfo", R.string.AreYouSureShareMyContactInfo));
        } else if (this.currentUser.bot) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfoBot", R.string.AreYouSureShareMyContactInfoBot));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureShareMyContactInfoUser", R.string.AreYouSureShareMyContactInfoUser, PhoneFormat.getInstance().format("+" + UserConfig.getCurrentUser().phone), ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name))));
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SendMessagesHelper.getInstance().sendMessage(UserConfig.getCurrentUser(), ChatActivity.this.dialog_id, messageObject, null, null);
                ChatActivity.this.moveScrollToLastMessage();
                ChatActivity.this.showReplyPanel(false, null, null, null, false);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void showGifHint() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (!preferences.getBoolean("gifhint", false)) {
            preferences.edit().putBoolean("gifhint", true).commit();
            if (getParentActivity() != null && this.fragmentView != null && this.gifHintTextView == null) {
                if (this.allowContextBotPanelSecond) {
                    SizeNotifierFrameLayout frameLayout = this.fragmentView;
                    int index = frameLayout.indexOfChild(this.chatActivityEnterView);
                    if (index != -1) {
                        this.chatActivityEnterView.setOpenGifsTabFirst();
                        this.emojiButtonRed = new View(getParentActivity());
                        this.emojiButtonRed.setBackgroundResource(R.drawable.redcircle);
                        frameLayout.addView(this.emojiButtonRed, index + 1, LayoutHelper.createFrame(10, 10.0f, 83, BitmapDescriptorFactory.HUE_ORANGE, 0.0f, 0.0f, 27.0f));
                        this.gifHintTextView = new TextView(getParentActivity());
                        this.gifHintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                        this.gifHintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                        this.gifHintTextView.setTextSize(1, 14.0f);
                        this.gifHintTextView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        this.gifHintTextView.setText(LocaleController.getString("TapHereGifs", R.string.TapHereGifs));
                        this.gifHintTextView.setGravity(16);
                        frameLayout.addView(this.gifHintTextView, index + 1, LayoutHelper.createFrame(-2, 32.0f, 83, 5.0f, 0.0f, 0.0f, 3.0f));
                        AnimatorSet AnimatorSet = new AnimatorSet();
                        AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.gifHintTextView, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.emojiButtonRed, "alpha", new float[]{0.0f, 1.0f})});
                        AnimatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (ChatActivity.this.gifHintTextView != null) {
                                            AnimatorSet AnimatorSet = new AnimatorSet();
                                            Animator[] animatorArr = new Animator[1];
                                            animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.gifHintTextView, "alpha", new float[]{0.0f});
                                            AnimatorSet.playTogether(animatorArr);
                                            AnimatorSet.addListener(new AnimatorListenerAdapter() {
                                                public void onAnimationEnd(Animator animation) {
                                                    if (ChatActivity.this.gifHintTextView != null) {
                                                        ChatActivity.this.gifHintTextView.setVisibility(8);
                                                    }
                                                }
                                            });
                                            AnimatorSet.setDuration(300);
                                            AnimatorSet.start();
                                        }
                                    }
                                }, 2000);
                            }
                        });
                        AnimatorSet.setDuration(300);
                        AnimatorSet.start();
                    }
                } else if (this.chatActivityEnterView != null) {
                    this.chatActivityEnterView.setOpenGifsTabFirst();
                }
            }
        }
    }

    private void openAttachMenu() {
        if (getParentActivity() != null) {
            createChatAttachView();
            this.chatAttachAlert.loadGalleryPhotos();
            if (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22) {
                this.chatActivityEnterView.closeKeyboard();
            }
            this.chatAttachAlert.init();
            showDialog(this.chatAttachAlert);
        }
    }

    private void checkContextBotPanel() {
        if (!this.allowStickersPanel || this.mentionsAdapter == null || !this.mentionsAdapter.isBotContext()) {
            return;
        }
        if (this.allowContextBotPanel || this.allowContextBotPanelSecond) {
            if (this.mentionContainer.getVisibility() == 4 || this.mentionContainer.getTag() != null) {
                if (this.mentionListAnimation != null) {
                    this.mentionListAnimation.cancel();
                }
                this.mentionContainer.setTag(null);
                this.mentionContainer.setVisibility(0);
                this.mentionListAnimation = new AnimatorSet();
                this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mentionContainer, "alpha", new float[]{0.0f, 1.0f})});
                this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }
                });
                this.mentionListAnimation.setDuration(200);
                this.mentionListAnimation.start();
            }
        } else if (this.mentionContainer.getVisibility() == 0 && this.mentionContainer.getTag() == null) {
            if (this.mentionListAnimation != null) {
                this.mentionListAnimation.cancel();
            }
            this.mentionContainer.setTag(Integer.valueOf(1));
            this.mentionListAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.mentionListAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.mentionContainer, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                        ChatActivity.this.mentionContainer.setVisibility(4);
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }
            });
            this.mentionListAnimation.setDuration(200);
            this.mentionListAnimation.start();
        }
    }

    private void hideFloatingDateView(boolean animated) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag(null);
                if (animated) {
                    this.floatingDateAnimation = new AnimatorSet();
                    this.floatingDateAnimation.setDuration(150);
                    AnimatorSet animatorSet = this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChatActivity.this.floatingDateAnimation)) {
                                ChatActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    this.floatingDateAnimation.setStartDelay(500);
                    this.floatingDateAnimation.start();
                    return;
                }
                if (this.floatingDateAnimation != null) {
                    this.floatingDateAnimation.cancel();
                    this.floatingDateAnimation = null;
                }
                this.floatingDateView.setAlpha(0.0f);
            }
        }
    }

    protected void setIgnoreAttachOnPause(boolean value) {
        this.ignoreAttachOnPause = value;
    }

    private void checkScrollForLoad(boolean scroll) {
        if (this.chatLayoutManager != null && !this.paused) {
            int firstVisibleItem = this.chatLayoutManager.findFirstVisibleItemPosition();
            int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
            if (visibleItemCount > 0) {
                int checkLoadCount;
                MessagesController instance;
                long j;
                int i;
                int i2;
                int i3;
                boolean isChannel;
                int i4;
                int totalItemCount = this.chatAdapter.getItemCount();
                if (scroll) {
                    checkLoadCount = 25;
                } else {
                    checkLoadCount = 5;
                }
                if (firstVisibleItem <= checkLoadCount && !this.loading) {
                    boolean z;
                    if (!this.endReached[0]) {
                        this.loading = true;
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        if (this.messagesByDays.size() != 0) {
                            instance = MessagesController.getInstance();
                            j = this.dialog_id;
                            i = this.maxMessageId[0];
                            z = !this.cacheEndReached[0];
                            i2 = this.minDate[0];
                            i3 = this.classGuid;
                            isChannel = ChatObject.isChannel(this.currentChat);
                            i4 = this.lastLoadIndex;
                            this.lastLoadIndex = i4 + 1;
                            instance.loadMessages(j, 50, i, 0, z, i2, i3, 0, 0, isChannel, i4);
                        } else {
                            instance = MessagesController.getInstance();
                            j = this.dialog_id;
                            z = !this.cacheEndReached[0];
                            i2 = this.minDate[0];
                            i3 = this.classGuid;
                            isChannel = ChatObject.isChannel(this.currentChat);
                            i4 = this.lastLoadIndex;
                            this.lastLoadIndex = i4 + 1;
                            instance.loadMessages(j, 50, 0, 0, z, i2, i3, 0, 0, isChannel, i4);
                        }
                    } else if (!(this.mergeDialogId == 0 || this.endReached[1])) {
                        this.loading = true;
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.mergeDialogId;
                        i = this.maxMessageId[1];
                        z = !this.cacheEndReached[1];
                        i2 = this.minDate[1];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + 1;
                        instance.loadMessages(j, 50, i, 0, z, i2, i3, 0, 0, isChannel, i4);
                    }
                }
                if (!this.loadingForward && firstVisibleItem + visibleItemCount >= totalItemCount - 10) {
                    if (this.mergeDialogId != 0 && !this.forwardEndReached[1]) {
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.mergeDialogId;
                        i = this.minMessageId[1];
                        i2 = this.maxDate[1];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + 1;
                        instance.loadMessages(j, 50, i, 0, true, i2, i3, 1, 0, isChannel, i4);
                        this.loadingForward = true;
                    } else if (!this.forwardEndReached[0]) {
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.dialog_id;
                        i = this.minMessageId[0];
                        i2 = this.maxDate[0];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + 1;
                        instance.loadMessages(j, 50, i, 0, true, i2, i3, 1, 0, isChannel, i4);
                        this.loadingForward = true;
                    }
                }
            }
        }
    }

    private void processSelectedAttach(int which) {
        if (which == 0 || which == 1 || which == 4 || which == 2) {
            String action;
            if (this.currentChat != null) {
                if (this.currentChat.participants_count > MessagesController.getInstance().groupBigSize) {
                    if (which == 0 || which == 1) {
                        action = "bigchat_upload_photo";
                    } else {
                        action = "bigchat_upload_document";
                    }
                } else if (which == 0 || which == 1) {
                    action = "chat_upload_photo";
                } else {
                    action = "chat_upload_document";
                }
            } else if (which == 0 || which == 1) {
                action = "pm_upload_photo";
            } else {
                action = "pm_upload_document";
            }
            if (!MessagesController.isFeatureEnabled(action, this)) {
                return;
            }
        }
        if (which == 0) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", image));
                            takePictureIntent.addFlags(2);
                            takePictureIntent.addFlags(1);
                        } else {
                            takePictureIntent.putExtra("output", Uri.fromFile(image));
                        }
                        this.currentPicturePath = image.getAbsolutePath();
                    }
                    startActivityForResult(takePictureIntent, 0);
                    return;
                } catch (Throwable e) {
                    FileLog.e(e);
                    return;
                }
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
        } else if (which == 1) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                boolean z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(false, z, true, this);
                fragment.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<String> photos, ArrayList<String> captions, ArrayList<ArrayList<InputDocument>> masks, ArrayList<SearchImage> webPhotos) {
                        SendMessagesHelper.prepareSendingPhotos(photos, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, captions, masks, null);
                        SendMessagesHelper.prepareSendingPhotosSearch(webPhotos, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent videoPickerIntent = new Intent();
                            videoPickerIntent.setType("video/*");
                            videoPickerIntent.setAction("android.intent.action.GET_CONTENT");
                            videoPickerIntent.putExtra("android.intent.extra.sizeLimit", NUM);
                            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                            photoPickerIntent.setType("image/*");
                            Intent chooserIntent = Intent.createChooser(photoPickerIntent, null);
                            chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{videoPickerIntent});
                            ChatActivity.this.startActivityForResult(chooserIntent, 1);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }

                    public void didSelectVideo(String path, VideoEditedInfo info, long estimatedSize, long estimatedDuration, String caption) {
                        if (info != null) {
                            SendMessagesHelper.prepareSendingVideo(path, estimatedSize, estimatedDuration, info.resultWidth, info.resultHeight, info, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, caption);
                        } else {
                            SendMessagesHelper.prepareSendingVideo(path, 0, 0, 0, 0, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null);
                        }
                        ChatActivity.this.showReplyPanel(false, null, null, null, false);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }
                });
                presentFragment(fragment);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        } else if (which == 2) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    Intent takeVideoIntent = new Intent("android.media.action.VIDEO_CAPTURE");
                    File video = AndroidUtilities.generateVideoPath();
                    if (video != null) {
                        if (VERSION.SDK_INT >= 24) {
                            takeVideoIntent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", video));
                            takeVideoIntent.addFlags(2);
                            takeVideoIntent.addFlags(1);
                        } else if (VERSION.SDK_INT >= 18) {
                            takeVideoIntent.putExtra("output", Uri.fromFile(video));
                        }
                        takeVideoIntent.putExtra("android.intent.extra.sizeLimit", NUM);
                        this.currentPicturePath = video.getAbsolutePath();
                    }
                    startActivityForResult(takeVideoIntent, 2);
                    return;
                } catch (Throwable e2) {
                    FileLog.e(e2);
                    return;
                }
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
        } else if (which == 6) {
            if (AndroidUtilities.isGoogleMapsInstalled(this)) {
                LocationActivity fragment2 = new LocationActivity();
                fragment2.setDelegate(new LocationActivityDelegate() {
                    public void didSelectLocation(MessageMedia location) {
                        SendMessagesHelper.getInstance().sendMessage(location, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, null);
                        ChatActivity.this.moveScrollToLastMessage();
                        ChatActivity.this.showReplyPanel(false, null, null, null, false);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                        if (ChatActivity.this.paused) {
                            ChatActivity.this.scrollToTopOnResume = true;
                        }
                    }
                });
                presentFragment(fragment2);
            }
        } else if (which == 4) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                DocumentSelectActivity fragment3 = new DocumentSelectActivity();
                fragment3.setDelegate(new DocumentSelectActivityDelegate() {
                    public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files) {
                        activity.finishFragment();
                        SendMessagesHelper.prepareSendingDocuments(files, files, null, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }

                    public void startDocumentSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                            photoPickerIntent.setType("*/*");
                            ChatActivity.this.startActivityForResult(photoPickerIntent, 21);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                });
                presentFragment(fragment3);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        } else if (which == 3) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                AudioSelectActivity fragment4 = new AudioSelectActivity();
                fragment4.setDelegate(new AudioSelectActivityDelegate() {
                    public void didSelectAudio(ArrayList<MessageObject> audios) {
                        SendMessagesHelper.prepareSendingAudioDocuments(audios, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }
                });
                presentFragment(fragment4);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        } else if (which != 5) {
        } else {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                try {
                    Intent intent = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
                    intent.setType("vnd.android.cursor.dir/phone_v2");
                    startActivityForResult(intent, 31);
                    return;
                } catch (Throwable e22) {
                    FileLog.e(e22);
                    return;
                }
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 5);
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    private void searchLinks(final CharSequence charSequence, final boolean force) {
        if (this.currentEncryptedChat == null || (MessagesController.getInstance().secretWebpagePreview != 0 && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) {
            if (force && this.foundWebPage != null) {
                if (this.foundWebPage.url != null) {
                    int index = TextUtils.indexOf(charSequence, this.foundWebPage.url);
                    char lastChar = '\u0000';
                    boolean lenEqual = false;
                    if (index != -1) {
                        if (this.foundWebPage.url.length() + index == charSequence.length()) {
                            lenEqual = true;
                        } else {
                            lenEqual = false;
                        }
                        if (lenEqual) {
                            lastChar = '\u0000';
                        } else {
                            lastChar = charSequence.charAt(this.foundWebPage.url.length() + index);
                        }
                    } else if (this.foundWebPage.display_url != null) {
                        index = TextUtils.indexOf(charSequence, this.foundWebPage.display_url);
                        if (index == -1 || this.foundWebPage.display_url.length() + index != charSequence.length()) {
                            lenEqual = false;
                        } else {
                            lenEqual = true;
                        }
                        if (index == -1 || lenEqual) {
                            lastChar = '\u0000';
                        } else {
                            lastChar = charSequence.charAt(this.foundWebPage.display_url.length() + index);
                        }
                    }
                    if (index != -1 && (lenEqual || lastChar == ' ' || lastChar == ',' || lastChar == '.' || lastChar == '!' || lastChar == '/')) {
                        return;
                    }
                }
                this.pendingLinkSearchString = null;
                showReplyPanel(false, null, null, this.foundWebPage, false);
            }
            Utilities.searchQueue.postRunnable(new Runnable() {
                public void run() {
                    Throwable e;
                    CharSequence textToCheck;
                    final TL_messages_getWebPagePreview req;
                    if (ChatActivity.this.linkSearchRequestId != 0) {
                        ConnectionsManager.getInstance().cancelRequest(ChatActivity.this.linkSearchRequestId, true);
                        ChatActivity.this.linkSearchRequestId = 0;
                    }
                    try {
                        ArrayList<CharSequence> urls;
                        Matcher m = AndroidUtilities.WEB_URL.matcher(charSequence);
                        ArrayList<CharSequence> urls2 = null;
                        while (m.find()) {
                            try {
                                if (m.start() <= 0 || charSequence.charAt(m.start() - 1) != '@') {
                                    if (urls2 == null) {
                                        urls = new ArrayList();
                                    } else {
                                        urls = urls2;
                                    }
                                    urls.add(charSequence.subSequence(m.start(), m.end()));
                                    urls2 = urls;
                                }
                            } catch (Exception e2) {
                                e = e2;
                                urls = urls2;
                            }
                        }
                        if (urls2 != null) {
                            if (ChatActivity.this.foundUrls != null && urls2.size() == ChatActivity.this.foundUrls.size()) {
                                boolean clear = true;
                                for (int a = 0; a < urls2.size(); a++) {
                                    if (!TextUtils.equals((CharSequence) urls2.get(a), (CharSequence) ChatActivity.this.foundUrls.get(a))) {
                                        clear = false;
                                    }
                                }
                                if (clear) {
                                    urls = urls2;
                                    return;
                                }
                            }
                        }
                        ChatActivity.this.foundUrls = urls2;
                        if (urls2 == null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (ChatActivity.this.foundWebPage != null) {
                                        ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                                        ChatActivity.this.foundWebPage = null;
                                    }
                                }
                            });
                            urls = urls2;
                            return;
                        }
                        textToCheck = TextUtils.join(" ", urls2);
                        urls = urls2;
                        if (ChatActivity.this.currentEncryptedChat == null && MessagesController.getInstance().secretWebpagePreview == 2) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            MessagesController.getInstance().secretWebpagePreview = 1;
                                            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                                            ChatActivity.this.foundUrls = null;
                                            ChatActivity.this.searchLinks(charSequence, force);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    builder.setMessage(LocaleController.getString("SecretLinkPreviewAlert", R.string.SecretLinkPreviewAlert));
                                    ChatActivity.this.showDialog(builder.create());
                                    MessagesController.getInstance().secretWebpagePreview = 0;
                                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                                }
                            });
                            return;
                        }
                        req = new TL_messages_getWebPagePreview();
                        if (textToCheck instanceof String) {
                            req.message = textToCheck.toString();
                        } else {
                            req.message = (String) textToCheck;
                        }
                        ChatActivity.this.linkSearchRequestId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                            public void run(final TLObject response, final TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        ChatActivity.this.linkSearchRequestId = 0;
                                        if (error != null) {
                                            return;
                                        }
                                        if (response instanceof TL_messageMediaWebPage) {
                                            ChatActivity.this.foundWebPage = ((TL_messageMediaWebPage) response).webpage;
                                            if ((ChatActivity.this.foundWebPage instanceof TL_webPage) || (ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                                                if (ChatActivity.this.foundWebPage instanceof TL_webPagePending) {
                                                    ChatActivity.this.pendingLinkSearchString = req.message;
                                                }
                                                if (ChatActivity.this.currentEncryptedChat != null && (ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                                                    ChatActivity.this.foundWebPage.url = req.message;
                                                }
                                                ChatActivity.this.showReplyPanel(true, null, null, ChatActivity.this.foundWebPage, false);
                                            } else if (ChatActivity.this.foundWebPage != null) {
                                                ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                                                ChatActivity.this.foundWebPage = null;
                                            }
                                        } else if (ChatActivity.this.foundWebPage != null) {
                                            ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                                            ChatActivity.this.foundWebPage = null;
                                        }
                                    }
                                });
                            }
                        });
                        ConnectionsManager.getInstance().bindRequestToGuid(ChatActivity.this.linkSearchRequestId, ChatActivity.this.classGuid);
                    } catch (Exception e3) {
                        e = e3;
                    }
                    FileLog.e(e);
                    String text = charSequence.toString().toLowerCase();
                    if (charSequence.length() < 13 || !(text.contains("http://") || text.contains("https://"))) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ChatActivity.this.foundWebPage != null) {
                                    ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                                    ChatActivity.this.foundWebPage = null;
                                }
                            }
                        });
                        return;
                    }
                    textToCheck = charSequence;
                    if (ChatActivity.this.currentEncryptedChat == null) {
                    }
                    req = new TL_messages_getWebPagePreview();
                    if (textToCheck instanceof String) {
                        req.message = textToCheck.toString();
                    } else {
                        req.message = (String) textToCheck;
                    }
                    ChatActivity.this.linkSearchRequestId = ConnectionsManager.getInstance().sendRequest(req, /* anonymous class already generated */);
                    ConnectionsManager.getInstance().bindRequestToGuid(ChatActivity.this.linkSearchRequestId, ChatActivity.this.classGuid);
                }
            });
        }
    }

    private void forwardMessages(ArrayList<MessageObject> arrayList, boolean fromMyName) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (fromMyName) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    SendMessagesHelper.getInstance().processForwardFromMyName((MessageObject) it.next(), this.dialog_id);
                }
                return;
            }
            SendMessagesHelper.getInstance().sendMessage(arrayList, this.dialog_id);
        }
    }

    public void showReplyPanel(boolean show, MessageObject messageObjectToReply, ArrayList<MessageObject> messageObjectsToForward, WebPage webPage, boolean cancel) {
        if (this.chatActivityEnterView != null) {
            if (show) {
                if (messageObjectToReply != null || messageObjectsToForward != null || webPage != null) {
                    if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                        this.actionBar.closeSearchField();
                        this.chatActivityEnterView.setFieldFocused();
                    }
                    boolean openKeyboard = false;
                    if (!(messageObjectToReply == null || messageObjectToReply.getDialogId() == this.dialog_id)) {
                        messageObjectsToForward = new ArrayList();
                        messageObjectsToForward.add(messageObjectToReply);
                        messageObjectToReply = null;
                        openKeyboard = true;
                    }
                    User user;
                    String name;
                    Chat chat;
                    String mess;
                    if (messageObjectToReply != null) {
                        this.forwardingMessages = null;
                        this.replyingMessageObject = messageObjectToReply;
                        this.chatActivityEnterView.setReplyingMessageObject(messageObjectToReply);
                        if (this.foundWebPage == null) {
                            if (messageObjectToReply.isFromUser()) {
                                user = MessagesController.getInstance().getUser(Integer.valueOf(messageObjectToReply.messageOwner.from_id));
                                if (user != null) {
                                    name = UserObject.getUserName(user);
                                } else {
                                    return;
                                }
                            }
                            chat = MessagesController.getInstance().getChat(Integer.valueOf(messageObjectToReply.messageOwner.to_id.channel_id));
                            if (chat != null) {
                                name = chat.title;
                            } else {
                                return;
                            }
                            this.replyIconImageView.setImageResource(R.drawable.msg_panel_reply);
                            this.replyNameTextView.setText(name);
                            if (messageObjectToReply.messageOwner.media instanceof TL_messageMediaGame) {
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(messageObjectToReply.messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            } else if (messageObjectToReply.messageText != null) {
                                mess = messageObjectToReply.messageText.toString();
                                if (mess.length() > 150) {
                                    mess = mess.substring(0, 150);
                                }
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(mess.replace('\n', ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            }
                        } else {
                            return;
                        }
                    } else if (messageObjectsToForward == null) {
                        this.replyIconImageView.setImageResource(R.drawable.msg_panel_link);
                        if (webPage instanceof TL_webPagePending) {
                            this.replyNameTextView.setText(LocaleController.getString("GettingLinkInfo", R.string.GettingLinkInfo));
                            this.replyObjectTextView.setText(this.pendingLinkSearchString);
                        } else {
                            if (webPage.site_name != null) {
                                this.replyNameTextView.setText(webPage.site_name);
                            } else if (webPage.title != null) {
                                this.replyNameTextView.setText(webPage.title);
                            } else {
                                this.replyNameTextView.setText(LocaleController.getString("LinkPreview", R.string.LinkPreview));
                            }
                            if (webPage.title != null) {
                                this.replyObjectTextView.setText(webPage.title);
                            } else if (webPage.description != null) {
                                this.replyObjectTextView.setText(webPage.description);
                            } else if (webPage.author != null) {
                                this.replyObjectTextView.setText(webPage.author);
                            } else {
                                this.replyObjectTextView.setText(webPage.display_url);
                            }
                            this.chatActivityEnterView.setWebPage(webPage, true);
                        }
                    } else if (!messageObjectsToForward.isEmpty()) {
                        this.replyingMessageObject = null;
                        this.chatActivityEnterView.setReplyingMessageObject(null);
                        this.forwardingMessages = messageObjectsToForward;
                        if (this.foundWebPage == null) {
                            int a;
                            Integer uid;
                            this.chatActivityEnterView.setForceShowSendButton(true, false);
                            ArrayList<Integer> uids = new ArrayList();
                            this.replyIconImageView.setImageResource(R.drawable.msg_panel_forward);
                            MessageObject object = (MessageObject) messageObjectsToForward.get(0);
                            if (object.isFromUser()) {
                                uids.add(Integer.valueOf(object.messageOwner.from_id));
                            } else {
                                uids.add(Integer.valueOf(-object.messageOwner.to_id.channel_id));
                            }
                            int type = ((MessageObject) messageObjectsToForward.get(0)).type;
                            for (a = 1; a < messageObjectsToForward.size(); a++) {
                                object = (MessageObject) messageObjectsToForward.get(a);
                                if (object.isFromUser()) {
                                    uid = Integer.valueOf(object.messageOwner.from_id);
                                } else {
                                    uid = Integer.valueOf(-object.messageOwner.to_id.channel_id);
                                }
                                if (!uids.contains(uid)) {
                                    uids.add(uid);
                                }
                                if (((MessageObject) messageObjectsToForward.get(a)).type != type) {
                                    type = -1;
                                }
                            }
                            StringBuilder userNames = new StringBuilder();
                            for (a = 0; a < uids.size(); a++) {
                                uid = (Integer) uids.get(a);
                                chat = null;
                                user = null;
                                if (uid.intValue() > 0) {
                                    user = MessagesController.getInstance().getUser(uid);
                                } else {
                                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-uid.intValue()));
                                }
                                if (user != null || chat != null) {
                                    if (uids.size() != 1) {
                                        if (uids.size() != 2 && userNames.length() != 0) {
                                            userNames.append(" ");
                                            userNames.append(LocaleController.formatPluralString("AndOther", uids.size() - 1));
                                            break;
                                        }
                                        if (userNames.length() > 0) {
                                            userNames.append(", ");
                                        }
                                        if (user == null) {
                                            userNames.append(chat.title);
                                        } else if (user.first_name != null && user.first_name.length() > 0) {
                                            userNames.append(user.first_name);
                                        } else if (user.last_name == null || user.last_name.length() <= 0) {
                                            userNames.append(" ");
                                        } else {
                                            userNames.append(user.last_name);
                                        }
                                    } else if (user != null) {
                                        userNames.append(UserObject.getUserName(user));
                                    } else {
                                        userNames.append(chat.title);
                                    }
                                }
                            }
                            this.replyNameTextView.setText(userNames);
                            if (type == -1 || type == 0 || type == 10 || type == 11) {
                                if (messageObjectsToForward.size() != 1 || ((MessageObject) messageObjectsToForward.get(0)).messageText == null) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMessage", messageObjectsToForward.size()));
                                } else {
                                    MessageObject messageObject = (MessageObject) messageObjectsToForward.get(0);
                                    if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                                        this.replyObjectTextView.setText(Emoji.replaceEmoji(messageObject.messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                    } else {
                                        mess = messageObject.messageText.toString();
                                        if (mess.length() > 150) {
                                            mess = mess.substring(0, 150);
                                        }
                                        this.replyObjectTextView.setText(Emoji.replaceEmoji(mess.replace('\n', ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                    }
                                }
                            } else if (type == 1) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedPhoto", messageObjectsToForward.size()));
                                if (messageObjectsToForward.size() == 1) {
                                    messageObjectToReply = (MessageObject) messageObjectsToForward.get(0);
                                }
                            } else if (type == 4) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedLocation", messageObjectsToForward.size()));
                            } else if (type == 3) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedVideo", messageObjectsToForward.size()));
                                if (messageObjectsToForward.size() == 1) {
                                    messageObjectToReply = (MessageObject) messageObjectsToForward.get(0);
                                }
                            } else if (type == 12) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedContact", messageObjectsToForward.size()));
                            } else if (type == 2) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedAudio", messageObjectsToForward.size()));
                            } else if (type == 14) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMusic", messageObjectsToForward.size()));
                            } else if (type == 13) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedSticker", messageObjectsToForward.size()));
                            } else if (type == 8 || type == 9) {
                                if (messageObjectsToForward.size() != 1) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedFile", messageObjectsToForward.size()));
                                } else if (type == 8) {
                                    this.replyObjectTextView.setText(LocaleController.getString("AttachGif", R.string.AttachGif));
                                } else {
                                    name = FileLoader.getDocumentFileName(((MessageObject) messageObjectsToForward.get(0)).getDocument());
                                    if (name.length() != 0) {
                                        this.replyObjectTextView.setText(name);
                                    }
                                    messageObjectToReply = (MessageObject) messageObjectsToForward.get(0);
                                }
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                    FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.replyNameTextView.getLayoutParams();
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.replyObjectTextView.getLayoutParams();
                    PhotoSize photoSize = null;
                    if (messageObjectToReply != null) {
                        photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObjectToReply.photoThumbs2, 80);
                        if (photoSize == null) {
                            photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObjectToReply.photoThumbs, 80);
                        }
                    }
                    int dp;
                    if (photoSize == null || (photoSize instanceof TL_photoSizeEmpty) || (photoSize.location instanceof TL_fileLocationUnavailable) || messageObjectToReply.type == 13 || (messageObjectToReply != null && messageObjectToReply.isSecretMedia())) {
                        this.replyImageView.setImageBitmap(null);
                        this.replyImageLocation = null;
                        this.replyImageView.setVisibility(4);
                        dp = AndroidUtilities.dp(52.0f);
                        layoutParams2.leftMargin = dp;
                        layoutParams1.leftMargin = dp;
                    } else {
                        this.replyImageLocation = photoSize.location;
                        this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null);
                        this.replyImageView.setVisibility(0);
                        dp = AndroidUtilities.dp(96.0f);
                        layoutParams2.leftMargin = dp;
                        layoutParams1.leftMargin = dp;
                    }
                    this.replyNameTextView.setLayoutParams(layoutParams1);
                    this.replyObjectTextView.setLayoutParams(layoutParams2);
                    this.chatActivityEnterView.showTopView(false, openKeyboard);
                }
            } else if (this.replyingMessageObject != null || this.forwardingMessages != null || this.foundWebPage != null) {
                if (this.replyingMessageObject != null && (this.replyingMessageObject.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply)) {
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("answered_" + this.dialog_id, this.replyingMessageObject.getId()).commit();
                }
                if (this.foundWebPage != null) {
                    this.foundWebPage = null;
                    this.chatActivityEnterView.setWebPage(null, !cancel);
                    if (!(webPage == null || (this.replyingMessageObject == null && this.forwardingMessages == null))) {
                        showReplyPanel(true, this.replyingMessageObject, this.forwardingMessages, null, false);
                        return;
                    }
                }
                if (this.forwardingMessages != null) {
                    forwardMessages(this.forwardingMessages, false);
                }
                this.chatActivityEnterView.setForceShowSendButton(false, false);
                this.chatActivityEnterView.hideTopView(false);
                this.chatActivityEnterView.setReplyingMessageObject(null);
                this.replyingMessageObject = null;
                this.forwardingMessages = null;
                this.replyImageLocation = null;
            }
        }
    }

    private void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private boolean sendSecretMessageRead(MessageObject messageObject) {
        if (messageObject == null || messageObject.isOut() || !messageObject.isSecretMedia() || messageObject.messageOwner.destroyTime != 0 || messageObject.messageOwner.ttl <= 0) {
            return false;
        }
        MessagesController.getInstance().markMessageAsRead(this.dialog_id, messageObject.messageOwner.random_id, messageObject.messageOwner.ttl);
        messageObject.messageOwner.destroyTime = messageObject.messageOwner.ttl + ConnectionsManager.getInstance().getCurrentTime();
        return true;
    }

    private void clearChatData() {
        this.messages.clear();
        this.messagesByDays.clear();
        this.waitingForLoad.clear();
        this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? 0 : 4);
        this.chatListView.setEmptyView(null);
        for (int a = 0; a < 2; a++) {
            this.messagesDict[a].clear();
            if (this.currentEncryptedChat == null) {
                this.maxMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.minMessageId[a] = Integer.MIN_VALUE;
            } else {
                this.maxMessageId[a] = Integer.MIN_VALUE;
                this.minMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            this.maxDate[a] = Integer.MIN_VALUE;
            this.minDate[a] = 0;
            this.endReached[a] = false;
            this.cacheEndReached[a] = false;
            this.forwardEndReached[a] = true;
        }
        this.first = true;
        this.firstLoading = true;
        this.loading = true;
        this.loadingForward = false;
        this.waitingForReplyMessageLoad = false;
        this.startLoadFromMessageId = 0;
        this.last_message_id = 0;
        this.createUnreadMessageAfterId = 0;
        this.needSelectFromMessageId = false;
        this.chatAdapter.notifyDataSetChanged();
    }

    private void scrollToLastMessage(boolean pagedown) {
        if (!this.forwardEndReached[0] || this.first_unread_id != 0 || this.startLoadFromMessageId != 0) {
            clearChatData();
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            MessagesController instance = MessagesController.getInstance();
            long j = this.dialog_id;
            int i = this.classGuid;
            boolean isChannel = ChatObject.isChannel(this.currentChat);
            int i2 = this.lastLoadIndex;
            this.lastLoadIndex = i2 + 1;
            instance.loadMessages(j, 30, 0, 0, true, 0, i, 0, 0, isChannel, i2);
        } else if (pagedown && this.chatLayoutManager.findLastCompletelyVisibleItemPosition() == this.chatAdapter.getItemCount() - 1) {
            showPagedownButton(false, true);
            this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            updateVisibleRows();
        } else {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private void updateMessagesVisisblePart() {
        if (this.chatListView != null) {
            int count = this.chatListView.getChildCount();
            if (this.chatActivityEnterView.isTopViewVisible()) {
                int additionalTop = AndroidUtilities.dp(48.0f);
            }
            int height = this.chatListView.getMeasuredHeight();
            int minPositionHolder = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int minPositionDateHolder = ConnectionsManager.DEFAULT_DATACENTER_ID;
            View minDateChild = null;
            View minChild = null;
            View minMessageChild = null;
            for (int a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    int top = messageCell.getTop();
                    int bottom = messageCell.getBottom();
                    int viewTop = top >= 0 ? 0 : -top;
                    int viewBottom = messageCell.getMeasuredHeight();
                    if (viewBottom > height) {
                        viewBottom = viewTop + height;
                    }
                    messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                }
                if (view.getBottom() > this.chatListView.getPaddingTop()) {
                    int position = view.getBottom();
                    if (position < minPositionHolder) {
                        minPositionHolder = position;
                        if ((view instanceof ChatMessageCell) || (view instanceof ChatActionCell)) {
                            minMessageChild = view;
                        }
                        minChild = view;
                    }
                    if ((view instanceof ChatActionCell) && ((ChatActionCell) view).getMessageObject().isDateObject) {
                        if (view.getAlpha() != 1.0f) {
                            view.setAlpha(1.0f);
                        }
                        if (position < minPositionDateHolder) {
                            minPositionDateHolder = position;
                            minDateChild = view;
                        }
                    }
                }
            }
            if (minMessageChild != null) {
                MessageObject messageObject;
                if (minMessageChild instanceof ChatMessageCell) {
                    messageObject = ((ChatMessageCell) minMessageChild).getMessageObject();
                } else {
                    messageObject = ((ChatActionCell) minMessageChild).getMessageObject();
                }
                this.floatingDateView.setCustomDate(messageObject.messageOwner.date);
            }
            this.currentFloatingDateOnScreen = false;
            boolean z = ((minChild instanceof ChatMessageCell) || (minChild instanceof ChatActionCell)) ? false : true;
            this.currentFloatingTopIsNotMessage = z;
            if (minDateChild != null) {
                if (minDateChild.getTop() > this.chatListView.getPaddingTop() || this.currentFloatingTopIsNotMessage) {
                    if (minDateChild.getAlpha() != 1.0f) {
                        minDateChild.setAlpha(1.0f);
                    }
                    if (this.currentFloatingTopIsNotMessage) {
                        z = false;
                    } else {
                        z = true;
                    }
                    hideFloatingDateView(z);
                } else {
                    if (minDateChild.getAlpha() != 0.0f) {
                        minDateChild.setAlpha(0.0f);
                    }
                    if (this.floatingDateAnimation != null) {
                        this.floatingDateAnimation.cancel();
                        this.floatingDateAnimation = null;
                    }
                    if (this.floatingDateView.getTag() == null) {
                        this.floatingDateView.setTag(Integer.valueOf(1));
                    }
                    if (this.floatingDateView.getAlpha() != 1.0f) {
                        this.floatingDateView.setAlpha(1.0f);
                    }
                    this.currentFloatingDateOnScreen = true;
                }
                int offset = minDateChild.getBottom() - this.chatListView.getPaddingTop();
                if (offset <= this.floatingDateView.getMeasuredHeight() || offset >= this.floatingDateView.getMeasuredHeight() * 2) {
                    this.floatingDateView.setTranslationY(0.0f);
                    return;
                } else {
                    this.floatingDateView.setTranslationY((float) (((-this.floatingDateView.getMeasuredHeight()) * 2) + offset));
                    return;
                }
            }
            hideFloatingDateView(true);
            this.floatingDateView.setTranslationY(0.0f);
        }
    }

    private void toggleMute(boolean instant) {
        Editor editor;
        TL_dialog dialog;
        if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
            editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            editor.putInt("notify2_" + this.dialog_id, 0);
            MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 0);
            editor.commit();
            dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
            }
            NotificationsController.updateServerNotificationsSettings(this.dialog_id);
        } else if (instant) {
            editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            editor.putInt("notify2_" + this.dialog_id, 2);
            MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 1);
            editor.commit();
            dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
                dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            NotificationsController.updateServerNotificationsSettings(this.dialog_id);
            NotificationsController.getInstance().removeNotificationsForDialog(this.dialog_id);
        } else {
            showDialog(AlertsCreator.createMuteAlert(getParentActivity(), this.dialog_id));
        }
    }

    private void scrollToMessageId(int id, int fromMessageId, boolean select, int loadIndex) {
        MessageObject object = (MessageObject) this.messagesDict[loadIndex].get(Integer.valueOf(id));
        boolean query = false;
        if (object == null) {
            query = true;
        } else if (this.messages.indexOf(object) != -1) {
            if (select) {
                this.highlightMessageId = id;
            } else {
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            int yOffset = Math.max(0, (this.chatListView.getHeight() - object.getApproximateHeight()) / 2);
            if (this.messages.get(this.messages.size() - 1) == object) {
                this.chatLayoutManager.scrollToPositionWithOffset(0, ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)) + yOffset);
            } else {
                this.chatLayoutManager.scrollToPositionWithOffset(((this.chatAdapter.messagesStartRow + this.messages.size()) - this.messages.indexOf(object)) - 1, (-AndroidUtilities.dp(7.0f)) + yOffset);
            }
            updateVisibleRows();
            boolean found = false;
            int count = this.chatListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                MessageObject messageObject;
                if (view instanceof ChatMessageCell) {
                    messageObject = ((ChatMessageCell) view).getMessageObject();
                    if (messageObject != null && messageObject.getId() == object.getId()) {
                        found = true;
                        break;
                    }
                } else if (view instanceof ChatActionCell) {
                    messageObject = ((ChatActionCell) view).getMessageObject();
                    if (messageObject != null && messageObject.getId() == object.getId()) {
                        found = true;
                        break;
                    }
                } else {
                    continue;
                }
            }
            if (!found) {
                showPagedownButton(true, true);
            }
        } else {
            query = true;
        }
        if (query) {
            if (this.currentEncryptedChat == null || MessagesStorage.getInstance().checkMessageId(this.dialog_id, this.startLoadFromMessageId)) {
                this.waitingForLoad.clear();
                this.waitingForReplyMessageLoad = true;
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.scrollToMessagePosition = -10000;
                this.startLoadFromMessageId = id;
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController instance = MessagesController.getInstance();
                long j = loadIndex == 0 ? this.dialog_id : this.mergeDialogId;
                int i = AndroidUtilities.isTablet() ? 30 : 20;
                int i2 = this.startLoadFromMessageId;
                int i3 = this.classGuid;
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                int i4 = this.lastLoadIndex;
                this.lastLoadIndex = i4 + 1;
                instance.loadMessages(j, i, i2, 0, true, 0, i3, 3, 0, isChannel, i4);
            } else {
                return;
            }
        }
        this.returnToMessageId = fromMessageId;
        this.returnToLoadIndex = loadIndex;
        this.needSelectFromMessageId = select;
    }

    private void showPagedownButton(boolean show, boolean animated) {
        if (this.pagedownButton != null) {
            if (show) {
                this.pagedownButtonShowedByScroll = false;
                if (this.pagedownButton.getTag() == null) {
                    if (this.pagedownButtonAnimation != null) {
                        this.pagedownButtonAnimation.cancel();
                        this.pagedownButtonAnimation = null;
                    }
                    if (animated) {
                        if (this.pagedownButton.getTranslationY() == 0.0f) {
                            this.pagedownButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
                        }
                        this.pagedownButton.setVisibility(0);
                        this.pagedownButton.setTag(Integer.valueOf(1));
                        this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{0.0f}).setDuration(200);
                        this.pagedownButtonAnimation.start();
                        return;
                    }
                    this.pagedownButton.setVisibility(0);
                    return;
                }
                return;
            }
            this.returnToMessageId = 0;
            this.newUnreadMessageCount = 0;
            if (this.pagedownButton.getTag() != null) {
                this.pagedownButton.setTag(null);
                if (this.pagedownButtonAnimation != null) {
                    this.pagedownButtonAnimation.cancel();
                    this.pagedownButtonAnimation = null;
                }
                if (animated) {
                    this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{(float) AndroidUtilities.dp(100.0f)}).setDuration(200);
                    this.pagedownButtonAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            ChatActivity.this.pagedownButtonCounter.setVisibility(4);
                            ChatActivity.this.pagedownButton.setVisibility(4);
                        }
                    });
                    this.pagedownButtonAnimation.start();
                    return;
                }
                this.pagedownButton.setVisibility(4);
            }
        }
    }

    private void updateSecretStatus() {
        if (this.bottomOverlay != null) {
            if (this.currentEncryptedChat == null || this.bigEmptyView == null) {
                this.bottomOverlay.setVisibility(4);
                return;
            }
            boolean hideKeyboard = false;
            if (this.currentEncryptedChat instanceof TL_encryptedChatRequested) {
                this.bottomOverlayText.setText(LocaleController.getString("EncryptionProcessing", R.string.EncryptionProcessing));
                this.bottomOverlay.setVisibility(0);
                hideKeyboard = true;
            } else if (this.currentEncryptedChat instanceof TL_encryptedChatWaiting) {
                this.bottomOverlayText.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, "<b>" + this.currentUser.first_name + "</b>")));
                this.bottomOverlay.setVisibility(0);
                hideKeyboard = true;
            } else if (this.currentEncryptedChat instanceof TL_encryptedChatDiscarded) {
                this.bottomOverlayText.setText(LocaleController.getString("EncryptionRejected", R.string.EncryptionRejected));
                this.bottomOverlay.setVisibility(0);
                this.chatActivityEnterView.setFieldText("");
                DraftQuery.cleanDraft(this.dialog_id, false);
                hideKeyboard = true;
            } else if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                this.bottomOverlay.setVisibility(4);
            }
            checkRaiseSensors();
            if (hideKeyboard) {
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
            checkActionBarMenu();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (requestCode == 17 && this.chatAttachAlert != null) {
            this.chatAttachAlert.checkCamera(false);
        } else if (requestCode == 21) {
            if (getParentActivity() != null && grantResults != null && grantResults.length != 0 && grantResults[0] != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("PermissionNoAudioVideo", R.string.PermissionNoAudioVideo));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new OnClickListener() {
                    @TargetApi(9)
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            ChatActivity.this.getParentActivity().startActivity(intent);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                });
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.show();
            }
        } else if (requestCode == 19 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
            processSelectedAttach(0);
        } else if (requestCode == 20 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
            processSelectedAttach(2);
        } else if (requestCode != 101) {
        } else {
            if (grantResults[0] == 0) {
                VoIPHelper.startCall(this.currentUser, getParentActivity());
            } else {
                VoIPHelper.permissionDenied(getParentActivity(), null);
            }
        }
    }

    private void checkActionBarMenu() {
        if ((this.currentEncryptedChat == null || (this.currentEncryptedChat instanceof TL_encryptedChat)) && ((this.currentChat == null || !ChatObject.isNotInChat(this.currentChat)) && (this.currentUser == null || !UserObject.isDeleted(this.currentUser)))) {
            if (this.timeItem2 != null) {
                this.timeItem2.setVisibility(0);
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.showTimeItem();
            }
        } else {
            if (this.timeItem2 != null) {
                this.timeItem2.setVisibility(8);
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.hideTimeItem();
            }
        }
        if (!(this.avatarContainer == null || this.currentEncryptedChat == null)) {
            this.avatarContainer.setTime(this.currentEncryptedChat.ttl);
        }
        checkAndUpdateAvatar();
    }

    private int getMessageType(MessageObject messageObject) {
        if (messageObject == null) {
            return -1;
        }
        InputStickerSet inputStickerSet;
        boolean canSave;
        String mime;
        if (this.currentEncryptedChat == null) {
            boolean isBroadcastError;
            if (this.isBroadcast && messageObject.getId() <= 0 && messageObject.isSendError()) {
                isBroadcastError = true;
            } else {
                isBroadcastError = false;
            }
            if ((this.isBroadcast || messageObject.getId() > 0 || !messageObject.isOut()) && !isBroadcastError) {
                if (messageObject.type == 6) {
                    return -1;
                }
                if (messageObject.type == 10 || messageObject.type == 11) {
                    if (messageObject.getId() == 0) {
                        return -1;
                    }
                    return 1;
                } else if (messageObject.isVoice()) {
                    return 2;
                } else {
                    if (messageObject.isSticker()) {
                        inputStickerSet = messageObject.getInputStickerSet();
                        if (inputStickerSet instanceof TL_inputStickerSetID) {
                            if (!StickersQuery.isStickerPackInstalled(inputStickerSet.id)) {
                                return 7;
                            }
                        } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                            return 7;
                        }
                    } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo()) {
                        canSave = false;
                        if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                            canSave = true;
                        }
                        if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                            canSave = true;
                        }
                        if (canSave) {
                            if (messageObject.getDocument() != null) {
                                mime = messageObject.getDocument().mime_type;
                                if (mime != null) {
                                    if (messageObject.getDocumentName().endsWith("attheme")) {
                                        return 10;
                                    }
                                    if (mime.endsWith("/xml")) {
                                        return 5;
                                    }
                                    if (mime.endsWith("/png") || mime.endsWith("/jpg") || mime.endsWith("/jpeg")) {
                                        return 6;
                                    }
                                }
                            }
                            return 4;
                        }
                    } else if (messageObject.type == 12) {
                        return 8;
                    } else {
                        if (messageObject.isMediaEmpty()) {
                            return 3;
                        }
                    }
                    return 2;
                }
            } else if (!messageObject.isSendError()) {
                return -1;
            } else {
                if (messageObject.isMediaEmpty()) {
                    return 20;
                }
                return 0;
            }
        } else if (messageObject.isSending()) {
            return -1;
        } else {
            if (messageObject.type == 6) {
                return -1;
            }
            if (messageObject.isSendError()) {
                if (messageObject.isMediaEmpty()) {
                    return 20;
                }
                return 0;
            } else if (messageObject.type == 10 || messageObject.type == 11) {
                if (messageObject.getId() == 0 || messageObject.isSending()) {
                    return -1;
                }
                return 1;
            } else if (messageObject.isVoice()) {
                return 2;
            } else {
                if (messageObject.isSticker()) {
                    inputStickerSet = messageObject.getInputStickerSet();
                    if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                        return 7;
                    }
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo()) {
                    canSave = false;
                    if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                        canSave = true;
                    }
                    if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        canSave = true;
                    }
                    if (canSave) {
                        if (messageObject.getDocument() != null) {
                            mime = messageObject.getDocument().mime_type;
                            if (mime != null && mime.endsWith("text/xml")) {
                                return 5;
                            }
                        }
                        if (messageObject.messageOwner.ttl <= 0) {
                            return 4;
                        }
                    }
                } else if (messageObject.type == 12) {
                    return 8;
                } else {
                    if (messageObject.isMediaEmpty()) {
                        return 3;
                    }
                }
                return 2;
            }
        }
    }

    private void addToSelectedMessages(MessageObject messageObject) {
        int index = messageObject.getDialogId() == this.dialog_id ? 0 : 1;
        if (this.selectedMessagesIds[index].containsKey(Integer.valueOf(messageObject.getId()))) {
            this.selectedMessagesIds[index].remove(Integer.valueOf(messageObject.getId()));
            if (messageObject.type == 0 || messageObject.caption != null) {
                this.selectedMessagesCanCopyIds[index].remove(Integer.valueOf(messageObject.getId()));
            }
            if (!messageObject.canDeleteMessage(this.currentChat)) {
                this.cantDeleteMessagesCount--;
            }
        } else if (this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() < 100) {
            this.selectedMessagesIds[index].put(Integer.valueOf(messageObject.getId()), messageObject);
            if (messageObject.type == 0 || messageObject.caption != null) {
                this.selectedMessagesCanCopyIds[index].put(Integer.valueOf(messageObject.getId()), messageObject);
            }
            if (!messageObject.canDeleteMessage(this.currentChat)) {
                this.cantDeleteMessagesCount++;
            }
        } else {
            return;
        }
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (this.selectedMessagesIds[0].isEmpty() && this.selectedMessagesIds[1].isEmpty()) {
            this.actionBar.hideActionMode();
            updatePinnedMessageView(true);
            return;
        }
        int copyVisible = this.actionBar.createActionMode().getItem(10).getVisibility();
        this.actionBar.createActionMode().getItem(10).setVisibility(this.selectedMessagesCanCopyIds[0].size() + this.selectedMessagesCanCopyIds[1].size() != 0 ? 0 : 8);
        int newCopyVisible = this.actionBar.createActionMode().getItem(10).getVisibility();
        this.actionBar.createActionMode().getItem(12).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        final ActionBarMenuItem replyItem = this.actionBar.createActionMode().getItem(19);
        if (replyItem != null) {
            boolean allowChatActions = true;
            if ((this.currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46) || this.isBroadcast || (this.currentChat != null && (ChatObject.isNotInChat(this.currentChat) || !(!ChatObject.isChannel(this.currentChat) || this.currentChat.creator || this.currentChat.editor || this.currentChat.megagroup)))) {
                allowChatActions = false;
            }
            final int newVisibility = (allowChatActions && this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() == 1) ? 0 : 8;
            if (replyItem.getVisibility() != newVisibility) {
                if (this.replyButtonAnimation != null) {
                    this.replyButtonAnimation.cancel();
                }
                if (copyVisible != newCopyVisible) {
                    if (newVisibility == 0) {
                        replyItem.setAlpha(1.0f);
                        replyItem.setScaleX(1.0f);
                    } else {
                        replyItem.setAlpha(0.0f);
                        replyItem.setScaleX(0.0f);
                    }
                    replyItem.setVisibility(newVisibility);
                    return;
                }
                this.replyButtonAnimation = new AnimatorSet();
                replyItem.setPivotX((float) AndroidUtilities.dp(54.0f));
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (newVisibility == 0) {
                    replyItem.setVisibility(newVisibility);
                    animatorSet = this.replyButtonAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(replyItem, "alpha", new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(replyItem, "scaleX", new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    animatorSet = this.replyButtonAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(replyItem, "alpha", new float[]{0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(replyItem, "scaleX", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.replyButtonAnimation.setDuration(100);
                this.replyButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animation) && newVisibility == 8) {
                            replyItem.setVisibility(8);
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animation)) {
                            ChatActivity.this.replyButtonAnimation = null;
                        }
                    }
                });
                this.replyButtonAnimation.start();
            }
        }
    }

    private void processRowSelect(View view) {
        MessageObject message = null;
        if (view instanceof ChatMessageCell) {
            message = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            message = ((ChatActionCell) view).getMessageObject();
        }
        int type = getMessageType(message);
        if (type >= 2 && type != 20) {
            addToSelectedMessages(message);
            updateActionModeTitle();
            updateVisibleRows();
        }
    }

    private void updateActionModeTitle() {
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (!this.selectedMessagesIds[0].isEmpty() || !this.selectedMessagesIds[1].isEmpty()) {
            this.selectedMessagesCountTextView.setNumber(this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size(), true);
        }
    }

    private void updateTitle() {
        if (this.avatarContainer != null) {
            if (this.currentChat != null) {
                this.avatarContainer.setTitle(this.currentChat.title);
            } else if (this.currentUser == null) {
            } else {
                if (this.currentUser.self) {
                    this.avatarContainer.setTitle(LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName));
                } else if (this.currentUser.id == 4244000 || this.currentUser.id / 1000 == 777 || this.currentUser.id / 1000 == 333 || ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null || (ContactsController.getInstance().contactsDict.size() == 0 && ContactsController.getInstance().isLoadingContacts())) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else {
                    this.avatarContainer.setTitle(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
                }
            }
        }
    }

    private void updateBotButtons() {
        if (this.headerItem != null && this.currentUser != null && this.currentEncryptedChat == null && this.currentUser.bot) {
            boolean hasHelp = false;
            boolean hasSettings = false;
            if (!this.botInfo.isEmpty()) {
                for (Entry<Integer, BotInfo> entry : this.botInfo.entrySet()) {
                    BotInfo info = (BotInfo) entry.getValue();
                    for (int a = 0; a < info.commands.size(); a++) {
                        TL_botCommand command = (TL_botCommand) info.commands.get(a);
                        if (command.command.toLowerCase().equals("help")) {
                            hasHelp = true;
                        } else if (command.command.toLowerCase().equals("settings")) {
                            hasSettings = true;
                        }
                        if (hasSettings && hasHelp) {
                            break;
                        }
                    }
                }
            }
            if (hasHelp) {
                this.headerItem.showSubItem(30);
            } else {
                this.headerItem.hideSubItem(30);
            }
            if (hasSettings) {
                this.headerItem.showSubItem(31);
            } else {
                this.headerItem.hideSubItem(31);
            }
        }
    }

    private void updateTitleIcons() {
        Drawable drawable = null;
        if (this.avatarContainer != null) {
            Drawable rightIcon;
            if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                rightIcon = Theme.chat_muteIconDrawable;
            } else {
                rightIcon = null;
            }
            ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
            if (this.currentEncryptedChat != null) {
                drawable = Theme.chat_lockIconDrawable;
            }
            chatAvatarContainer.setTitleIcons(drawable, rightIcon);
            if (this.muteItem == null) {
                return;
            }
            if (rightIcon != null) {
                this.muteItem.setText(LocaleController.getString("UnmuteNotifications", R.string.UnmuteNotifications));
            } else {
                this.muteItem.setText(LocaleController.getString("MuteNotifications", R.string.MuteNotifications));
            }
        }
    }

    private void checkAndUpdateAvatar() {
        if (this.currentUser != null) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
            if (user != null) {
                this.currentUser = user;
            } else {
                return;
            }
        } else if (this.currentChat != null) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
            if (chat != null) {
                this.currentChat = chat;
            } else {
                return;
            }
        }
        if (this.avatarContainer != null) {
            this.avatarContainer.checkAndUpdateAvatar();
        }
    }

    public boolean openVideoEditor(String videoPath, boolean removeLast, boolean animated) {
        Bundle args = new Bundle();
        args.putString("videoPath", videoPath);
        BaseFragment videoEditorActivity = new VideoEditorActivity(args);
        videoEditorActivity.setDelegate(new VideoEditorActivityDelegate() {
            public void didFinishEditVideo(String videoPath, long startTime, long endTime, int resultWidth, int resultHeight, int rotationValue, int originalWidth, int originalHeight, int bitrate, long estimatedSize, long estimatedDuration, String caption) {
                VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                videoEditedInfo.startTime = startTime;
                videoEditedInfo.endTime = endTime;
                videoEditedInfo.rotationValue = rotationValue;
                videoEditedInfo.originalWidth = originalWidth;
                videoEditedInfo.originalHeight = originalHeight;
                videoEditedInfo.bitrate = bitrate;
                videoEditedInfo.resultWidth = resultWidth;
                videoEditedInfo.resultHeight = resultHeight;
                videoEditedInfo.originalPath = videoPath;
                SendMessagesHelper.prepareSendingVideo(videoPath, estimatedSize, estimatedDuration, resultWidth, resultHeight, videoEditedInfo, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, caption);
                ChatActivity.this.showReplyPanel(false, null, null, null, false);
                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
            }
        });
        if (this.parentLayout == null || !videoEditorActivity.onFragmentCreate()) {
            SendMessagesHelper.prepareSendingVideo(videoPath, 0, 0, 0, 0, null, this.dialog_id, this.replyingMessageObject, null);
            showReplyPanel(false, null, null, null, false);
            DraftQuery.cleanDraft(this.dialog_id, true);
            return false;
        }
        if (this.parentLayout.presentFragment(videoEditorActivity, removeLast, !animated, true)) {
            videoEditorActivity.setParentChatActivity(this);
        }
        return true;
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0).show();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 0) {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            ArrayList<Object> arrayList = new ArrayList();
            int orientation = 0;
            try {
                switch (new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1)) {
                    case 3:
                        orientation = 180;
                        break;
                    case 6:
                        orientation = 90;
                        break;
                    case 8:
                        orientation = 270;
                        break;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            arrayList.add(new PhotoEntry(0, 0, 0, this.currentPicturePath, orientation, false));
            final ArrayList<Object> arrayList2 = arrayList;
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 2, new EmptyPhotoViewerProvider() {
                public void sendButtonPressed(int index) {
                    ChatActivity.this.sendMedia((PhotoEntry) arrayList2.get(0), false);
                }
            }, this);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (requestCode == 1) {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            uri = data.getData();
            if (uri.toString().contains(MimeTypes.BASE_TYPE_VIDEO)) {
                videoPath = null;
                try {
                    videoPath = AndroidUtilities.getPath(uri);
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                if (videoPath == null) {
                    showAttachmentError();
                }
                if (VERSION.SDK_INT < 16) {
                    SendMessagesHelper.prepareSendingVideo(videoPath, 0, 0, 0, 0, null, this.dialog_id, this.replyingMessageObject, null);
                } else if (this.paused) {
                    this.startVideoEdit = videoPath;
                } else {
                    openVideoEditor(videoPath, false, false);
                }
            } else {
                SendMessagesHelper.prepareSendingPhoto(null, uri, this.dialog_id, this.replyingMessageObject, null, null, null);
            }
            showReplyPanel(false, null, null, null, false);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (requestCode == 2) {
            videoPath = null;
            FileLog.d("pic path " + this.currentPicturePath);
            if (!(data == null || this.currentPicturePath == null || !new File(this.currentPicturePath).exists())) {
                data = null;
            }
            if (data != null) {
                uri = data.getData();
                if (uri != null) {
                    FileLog.d("video record uri " + uri.toString());
                    videoPath = AndroidUtilities.getPath(uri);
                    FileLog.d("resolved path = " + videoPath);
                    if (!new File(videoPath).exists()) {
                        videoPath = this.currentPicturePath;
                    }
                } else {
                    videoPath = this.currentPicturePath;
                }
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                this.currentPicturePath = null;
            }
            if (videoPath == null && this.currentPicturePath != null) {
                if (new File(this.currentPicturePath).exists()) {
                    videoPath = this.currentPicturePath;
                }
                this.currentPicturePath = null;
            }
            if (VERSION.SDK_INT < 16) {
                SendMessagesHelper.prepareSendingVideo(videoPath, 0, 0, 0, 0, null, this.dialog_id, this.replyingMessageObject, null);
                showReplyPanel(false, null, null, null, false);
                DraftQuery.cleanDraft(this.dialog_id, true);
            } else if (this.paused) {
                this.startVideoEdit = videoPath;
            } else {
                openVideoEditor(videoPath, false, false);
            }
        } else if (requestCode == 21) {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            uri = data.getData();
            String extractUriFrom = uri.toString();
            if (extractUriFrom.contains("com.google.android.apps.photos.contentprovider")) {
                try {
                    String firstExtraction = extractUriFrom.split("/1/")[1];
                    int index = firstExtraction.indexOf("/ACTUAL");
                    if (index != -1) {
                        uri = Uri.parse(URLDecoder.decode(firstExtraction.substring(0, index), "UTF-8"));
                    }
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
            }
            String tempPath = AndroidUtilities.getPath(uri);
            String originalPath = tempPath;
            if (tempPath == null) {
                originalPath = data.toString();
                tempPath = MediaController.copyFileToCache(data.getData(), "file");
            }
            if (tempPath == null) {
                showAttachmentError();
                return;
            }
            SendMessagesHelper.prepareSendingDocument(tempPath, originalPath, null, null, this.dialog_id, this.replyingMessageObject, null);
            showReplyPanel(false, null, null, null, false);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (requestCode != 31) {
        } else {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            uri = data.getData();
            Cursor c = null;
            try {
                c = getParentActivity().getContentResolver().query(uri, new String[]{"display_name", "data1"}, null, null, null);
                if (c != null) {
                    boolean sent = false;
                    while (c.moveToNext()) {
                        sent = true;
                        String name = c.getString(0);
                        String number = c.getString(1);
                        User user = new User();
                        user.first_name = name;
                        user.last_name = "";
                        user.phone = number;
                        SendMessagesHelper.getInstance().sendMessage(user, this.dialog_id, this.replyingMessageObject, null, null);
                    }
                    if (sent) {
                        showReplyPanel(false, null, null, null, false);
                        DraftQuery.cleanDraft(this.dialog_id, true);
                    }
                }
                if (c != null) {
                    try {
                        if (!c.isClosed()) {
                            c.close();
                        }
                    } catch (Throwable e222) {
                        FileLog.e(e222);
                    }
                }
            } catch (Throwable th) {
                if (c != null) {
                    try {
                        if (!c.isClosed()) {
                            c.close();
                        }
                    } catch (Throwable e2222) {
                        FileLog.e(e2222);
                    }
                }
            }
        }
    }

    public void saveSelfArgs(Bundle args) {
        if (this.currentPicturePath != null) {
            args.putString("path", this.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        this.currentPicturePath = args.getString("path");
    }

    private void removeUnreadPlane() {
        if (this.unreadMessageObject != null) {
            boolean[] zArr = this.forwardEndReached;
            this.forwardEndReached[1] = true;
            zArr[0] = true;
            this.first_unread_id = 0;
            this.last_message_id = 0;
            this.createUnreadMessageAfterId = 0;
            this.unread_to_load = 0;
            removeMessageObject(this.unreadMessageObject);
            this.unreadMessageObject = null;
        }
    }

    public boolean processSendingText(String text) {
        return this.chatActivityEnterView.processSendingText(text);
    }

    public void didReceivedNotification(int id, Object... args) {
        int index;
        ArrayList<MessageObject> messArr;
        int a;
        MessageObject obj;
        int loadIndex;
        int count;
        boolean z;
        ArrayList<MessageObject> dayArray;
        Message dateMsg;
        MessageObject messageObject;
        int insertStart;
        if (id == NotificationCenter.messagesDidLoaded) {
            if (((Integer) args[10]).intValue() == this.classGuid) {
                if (!this.openAnimationEnded) {
                    NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.botKeyboardDidLoaded});
                }
                index = this.waitingForLoad.indexOf(Integer.valueOf(((Integer) args[11]).intValue()));
                if (index != -1) {
                    this.waitingForLoad.remove(index);
                    messArr = args[2];
                    if (this.waitingForReplyMessageLoad) {
                        boolean found = false;
                        for (a = 0; a < messArr.size(); a++) {
                            obj = (MessageObject) messArr.get(a);
                            if (obj.getId() == this.startLoadFromMessageId) {
                                found = true;
                                break;
                            }
                            if (a + 1 < messArr.size()) {
                                MessageObject obj2 = (MessageObject) messArr.get(a + 1);
                                if (obj.getId() >= this.startLoadFromMessageId && obj2.getId() < this.startLoadFromMessageId) {
                                    this.startLoadFromMessageId = obj.getId();
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found) {
                            int startLoadFrom = this.startLoadFromMessageId;
                            boolean needSelect = this.needSelectFromMessageId;
                            int unreadAfterId = this.createUnreadMessageAfterId;
                            clearChatData();
                            this.createUnreadMessageAfterId = unreadAfterId;
                            this.startLoadFromMessageId = startLoadFrom;
                            this.needSelectFromMessageId = needSelect;
                        } else {
                            this.startLoadFromMessageId = 0;
                            return;
                        }
                    }
                    this.loadsCount++;
                    loadIndex = ((Long) args[0]).longValue() == this.dialog_id ? 0 : 1;
                    count = ((Integer) args[1]).intValue();
                    boolean isCache = ((Boolean) args[3]).booleanValue();
                    int fnid = ((Integer) args[4]).intValue();
                    int last_unread_date = ((Integer) args[7]).intValue();
                    int load_type = ((Integer) args[8]).intValue();
                    int loaded_max_id = ((Integer) args[12]).intValue();
                    if (load_type == 4) {
                        this.startLoadFromMessageId = loaded_max_id;
                        for (a = messArr.size() - 1; a > 0; a--) {
                            obj = (MessageObject) messArr.get(a);
                            if (obj.type < 0 && obj.getId() == this.startLoadFromMessageId) {
                                this.startLoadFromMessageId = ((MessageObject) messArr.get(a - 1)).getId();
                                break;
                            }
                        }
                    }
                    boolean wasUnread = false;
                    if (fnid != 0) {
                        this.last_message_id = ((Integer) args[5]).intValue();
                        if (load_type == 3) {
                            if (this.loadingFromOldPosition) {
                                this.unread_to_load = ((Integer) args[6]).intValue();
                                if (this.unread_to_load != 0) {
                                    this.createUnreadMessageAfterId = fnid;
                                }
                                this.loadingFromOldPosition = false;
                            }
                            this.first_unread_id = 0;
                        } else {
                            this.first_unread_id = fnid;
                            this.unread_to_load = ((Integer) args[6]).intValue();
                        }
                    } else if (this.startLoadFromMessageId != 0 && (load_type == 3 || load_type == 4)) {
                        this.last_message_id = ((Integer) args[5]).intValue();
                    }
                    int newRowsCount = 0;
                    boolean[] zArr = this.forwardEndReached;
                    z = this.startLoadFromMessageId == 0 && this.last_message_id == 0;
                    zArr[loadIndex] = z;
                    if ((load_type == 1 || load_type == 3) && loadIndex == 1) {
                        boolean[] zArr2 = this.endReached;
                        this.cacheEndReached[0] = true;
                        zArr2[0] = true;
                        this.forwardEndReached[0] = false;
                        this.minMessageId[0] = 0;
                    }
                    if (this.loadsCount == 1 && messArr.size() > 20) {
                        this.loadsCount++;
                    }
                    if (this.firstLoading) {
                        if (!this.forwardEndReached[loadIndex]) {
                            this.messages.clear();
                            this.messagesByDays.clear();
                            for (a = 0; a < 2; a++) {
                                this.messagesDict[a].clear();
                                if (this.currentEncryptedChat == null) {
                                    this.maxMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    this.minMessageId[a] = Integer.MIN_VALUE;
                                } else {
                                    this.maxMessageId[a] = Integer.MIN_VALUE;
                                    this.minMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                }
                                this.maxDate[a] = Integer.MIN_VALUE;
                                this.minDate[a] = 0;
                            }
                        }
                        this.firstLoading = false;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ChatActivity.this.parentLayout != null) {
                                    ChatActivity.this.parentLayout.resumeDelayedFragmentAnimation();
                                }
                            }
                        });
                    }
                    if (load_type == 1) {
                        Collections.reverse(messArr);
                    }
                    if (this.currentEncryptedChat == null) {
                        MessagesQuery.loadReplyMessagesForMessages(messArr, this.dialog_id);
                    }
                    int approximateHeightSum = 0;
                    a = 0;
                    while (a < messArr.size()) {
                        obj = (MessageObject) messArr.get(a);
                        approximateHeightSum += obj.getApproximateHeight();
                        if (this.currentUser != null) {
                            if (this.currentUser.self) {
                                obj.messageOwner.out = true;
                            }
                            if (this.currentUser.bot && obj.isOut()) {
                                obj.setIsRead();
                            }
                        }
                        if (!this.messagesDict[loadIndex].containsKey(Integer.valueOf(obj.getId()))) {
                            if (loadIndex == 1) {
                                obj.setIsRead();
                            }
                            if (loadIndex == 0 && ChatObject.isChannel(this.currentChat) && obj.getId() == 1) {
                                this.endReached[loadIndex] = true;
                                this.cacheEndReached[loadIndex] = true;
                            }
                            if (obj.getId() > 0) {
                                this.maxMessageId[loadIndex] = Math.min(obj.getId(), this.maxMessageId[loadIndex]);
                                this.minMessageId[loadIndex] = Math.max(obj.getId(), this.minMessageId[loadIndex]);
                            } else if (this.currentEncryptedChat != null) {
                                this.maxMessageId[loadIndex] = Math.max(obj.getId(), this.maxMessageId[loadIndex]);
                                this.minMessageId[loadIndex] = Math.min(obj.getId(), this.minMessageId[loadIndex]);
                            }
                            if (obj.messageOwner.date != 0) {
                                this.maxDate[loadIndex] = Math.max(this.maxDate[loadIndex], obj.messageOwner.date);
                                if (this.minDate[loadIndex] == 0 || obj.messageOwner.date < this.minDate[loadIndex]) {
                                    this.minDate[loadIndex] = obj.messageOwner.date;
                                }
                            }
                            if (obj.type >= 0 && !(loadIndex == 1 && (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo))) {
                                if (!obj.isOut() && obj.isUnread()) {
                                    wasUnread = true;
                                }
                                this.messagesDict[loadIndex].put(Integer.valueOf(obj.getId()), obj);
                                dayArray = (ArrayList) this.messagesByDays.get(obj.dateKey);
                                if (dayArray == null) {
                                    dayArray = new ArrayList();
                                    this.messagesByDays.put(obj.dateKey, dayArray);
                                    dateMsg = new Message();
                                    dateMsg.message = LocaleController.formatDateChat((long) obj.messageOwner.date);
                                    dateMsg.id = 0;
                                    dateMsg.date = obj.messageOwner.date;
                                    messageObject = new MessageObject(dateMsg, null, false);
                                    messageObject.type = 10;
                                    messageObject.contentType = 1;
                                    messageObject.isDateObject = true;
                                    if (load_type == 1) {
                                        this.messages.add(0, messageObject);
                                    } else {
                                        this.messages.add(messageObject);
                                    }
                                    newRowsCount++;
                                }
                                newRowsCount++;
                                if (load_type == 1) {
                                    dayArray.add(obj);
                                    this.messages.add(0, obj);
                                }
                                if (load_type != 1) {
                                    dayArray.add(obj);
                                    this.messages.add(this.messages.size() - 1, obj);
                                }
                                if (obj.getId() == this.last_message_id) {
                                    this.forwardEndReached[loadIndex] = true;
                                }
                                MessageObject prevObj;
                                if (this.currentEncryptedChat == null) {
                                    if (this.createUnreadMessageAfterId == 0 || load_type == 1 || a + 1 >= messArr.size()) {
                                        prevObj = null;
                                    } else {
                                        prevObj = (MessageObject) messArr.get(a + 1);
                                        if (obj.isOut() || prevObj.getId() >= this.createUnreadMessageAfterId) {
                                            prevObj = null;
                                        }
                                    }
                                } else if (this.createUnreadMessageAfterId == 0 || load_type == 1 || a - 1 < 0) {
                                    prevObj = null;
                                } else {
                                    prevObj = (MessageObject) messArr.get(a - 1);
                                    if (obj.isOut() || prevObj.getId() >= this.createUnreadMessageAfterId) {
                                        prevObj = null;
                                    }
                                }
                                if (load_type == 2 && obj.getId() == this.first_unread_id) {
                                    if (approximateHeightSum > AndroidUtilities.displaySize.y / 2 || !this.forwardEndReached[0]) {
                                        dateMsg = new Message();
                                        dateMsg.message = "";
                                        dateMsg.id = 0;
                                        messageObject = new MessageObject(dateMsg, null, false);
                                        messageObject.type = 6;
                                        messageObject.contentType = 2;
                                        this.messages.add(this.messages.size() - 1, messageObject);
                                        this.unreadMessageObject = messageObject;
                                        this.scrollToMessage = this.unreadMessageObject;
                                        this.scrollToMessagePosition = -10000;
                                        newRowsCount++;
                                    }
                                } else if ((load_type == 3 || load_type == 4) && obj.getId() == this.startLoadFromMessageId) {
                                    if (this.needSelectFromMessageId) {
                                        this.highlightMessageId = obj.getId();
                                    } else {
                                        this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                    this.scrollToMessage = obj;
                                    this.startLoadFromMessageId = 0;
                                    if (this.scrollToMessagePosition == -10000) {
                                        this.scrollToMessagePosition = -9000;
                                    }
                                }
                                if (load_type != 2 && this.unreadMessageObject == null && this.createUnreadMessageAfterId != 0 && (((this.currentEncryptedChat == null && !obj.isOut() && obj.getId() >= this.createUnreadMessageAfterId) || !(this.currentEncryptedChat == null || obj.isOut() || obj.getId() > this.createUnreadMessageAfterId)) && (load_type == 1 || prevObj != null))) {
                                    dateMsg = new Message();
                                    dateMsg.message = "";
                                    dateMsg.id = 0;
                                    messageObject = new MessageObject(dateMsg, null, false);
                                    messageObject.type = 6;
                                    messageObject.contentType = 2;
                                    if (load_type == 1) {
                                        this.messages.add(1, messageObject);
                                    } else {
                                        this.messages.add(this.messages.size() - 1, messageObject);
                                    }
                                    this.unreadMessageObject = messageObject;
                                    newRowsCount++;
                                }
                            }
                        }
                        a++;
                    }
                    if (load_type == 0 && newRowsCount == 0) {
                        this.loadsCount--;
                    }
                    if (this.forwardEndReached[loadIndex] && loadIndex != 1) {
                        this.first_unread_id = 0;
                        this.last_message_id = 0;
                        this.createUnreadMessageAfterId = 0;
                    }
                    int firstVisPos;
                    int top;
                    View firstVisView;
                    if (load_type == 1) {
                        if (!(messArr.size() == count || isCache)) {
                            this.forwardEndReached[loadIndex] = true;
                            if (loadIndex != 1) {
                                this.first_unread_id = 0;
                                this.last_message_id = 0;
                                this.createUnreadMessageAfterId = 0;
                                this.chatAdapter.notifyItemRemoved(this.chatAdapter.getItemCount() - 1);
                                newRowsCount--;
                            }
                            this.startLoadFromMessageId = 0;
                        }
                        if (newRowsCount > 0) {
                            firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
                            top = 0;
                            if (firstVisPos != this.chatLayoutManager.getItemCount() - 1) {
                                firstVisPos = -1;
                            } else {
                                firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
                                top = (firstVisView == null ? 0 : firstVisView.getTop()) - this.chatListView.getPaddingTop();
                            }
                            insertStart = this.chatAdapter.getItemCount() - 1;
                            this.chatAdapter.notifyItemChanged(insertStart);
                            this.chatAdapter.notifyItemRangeInserted(insertStart, newRowsCount);
                            if (firstVisPos != -1) {
                                this.chatLayoutManager.scrollToPositionWithOffset(firstVisPos, top);
                            }
                        }
                        this.loadingForward = false;
                    } else {
                        if (!(messArr.size() >= count || load_type == 3 || load_type == 4)) {
                            if (isCache) {
                                if (this.currentEncryptedChat != null || this.isBroadcast) {
                                    this.endReached[loadIndex] = true;
                                }
                                if (load_type != 2) {
                                    this.cacheEndReached[loadIndex] = true;
                                }
                            } else if (load_type != 2 || (messArr.size() == 0 && this.messages.isEmpty())) {
                                this.endReached[loadIndex] = true;
                            }
                        }
                        this.loading = false;
                        if (this.chatListView != null) {
                            if (this.first || this.scrollToTopOnResume || this.forceScrollToTop) {
                                this.forceScrollToTop = false;
                                this.chatAdapter.notifyDataSetChanged();
                                if (this.scrollToMessage != null) {
                                    int yOffset;
                                    if (this.startLoadFromMessageOffset != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        yOffset = (this.chatListView.getHeight() - this.startLoadFromMessageOffset) + AndroidUtilities.dp(4.0f);
                                        this.startLoadFromMessageOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    } else if (this.scrollToMessagePosition == -9000) {
                                        yOffset = Math.max(0, (this.chatListView.getHeight() - this.scrollToMessage.getApproximateHeight()) / 2);
                                    } else if (this.scrollToMessagePosition == -10000) {
                                        yOffset = 0;
                                    } else {
                                        yOffset = this.scrollToMessagePosition;
                                    }
                                    if (!this.messages.isEmpty()) {
                                        if (this.messages.get(this.messages.size() - 1) == this.scrollToMessage || this.messages.get(this.messages.size() - 2) == this.scrollToMessage) {
                                            this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.isBot ? 1 : 0, ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)) + yOffset);
                                        } else {
                                            this.chatLayoutManager.scrollToPositionWithOffset(((this.chatAdapter.messagesStartRow + this.messages.size()) - this.messages.indexOf(this.scrollToMessage)) - 1, ((-AndroidUtilities.dp(7.0f)) + yOffset) - (this.scrollToMessage == this.unreadMessageObject ? this.chatListView.getPaddingTop() : 0));
                                        }
                                    }
                                    this.chatListView.invalidate();
                                    if (this.scrollToMessagePosition == -10000 || this.scrollToMessagePosition == -9000) {
                                        showPagedownButton(true, true);
                                        if (load_type == 3 && this.unread_to_load != 0) {
                                            this.pagedownButtonCounter.setVisibility(0);
                                            TextView textView = this.pagedownButtonCounter;
                                            Object[] objArr = new Object[1];
                                            int i = this.unread_to_load;
                                            this.newUnreadMessageCount = i;
                                            objArr[0] = Integer.valueOf(i);
                                            textView.setText(String.format("%d", objArr));
                                        }
                                    }
                                    this.scrollToMessagePosition = -10000;
                                    this.scrollToMessage = null;
                                } else {
                                    moveScrollToLastMessage();
                                }
                            } else if (newRowsCount != 0) {
                                boolean end = false;
                                if (this.endReached[loadIndex] && ((loadIndex == 0 && this.mergeDialogId == 0) || loadIndex == 1)) {
                                    end = true;
                                    this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.isBot ? 1 : 0, 2);
                                }
                                firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
                                firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
                                top = (firstVisView == null ? 0 : firstVisView.getTop()) - this.chatListView.getPaddingTop();
                                if (newRowsCount - (end ? 1 : 0) > 0) {
                                    insertStart = (this.chatAdapter.isBot ? 2 : 1) + (end ? 0 : 1);
                                    this.chatAdapter.notifyItemChanged(insertStart);
                                    this.chatAdapter.notifyItemRangeInserted(insertStart, newRowsCount - (end ? 1 : 0));
                                }
                                if (firstVisPos != -1) {
                                    int i2;
                                    LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
                                    int i3 = firstVisPos + newRowsCount;
                                    if (end) {
                                        i2 = 1;
                                    } else {
                                        i2 = 0;
                                    }
                                    linearLayoutManager.scrollToPositionWithOffset(i3 - i2, top);
                                }
                            } else if (this.endReached[loadIndex] && ((loadIndex == 0 && this.mergeDialogId == 0) || loadIndex == 1)) {
                                this.chatAdapter.notifyItemRemoved(this.chatAdapter.isBot ? 1 : 0);
                            }
                            if (this.paused) {
                                this.scrollToTopOnResume = true;
                                if (this.scrollToMessage != null) {
                                    this.scrollToTopUnReadOnResume = true;
                                }
                            }
                            if (this.first && this.chatListView != null) {
                                this.chatListView.setEmptyView(this.emptyViewContainer);
                            }
                        } else {
                            this.scrollToTopOnResume = true;
                            if (this.scrollToMessage != null) {
                                this.scrollToTopUnReadOnResume = true;
                            }
                        }
                    }
                    if (this.first && this.messages.size() > 0) {
                        if (loadIndex == 0) {
                            boolean wasUnreadFinal = wasUnread;
                            int last_unread_date_final = last_unread_date;
                            final int id2 = ((MessageObject) this.messages.get(0)).getId();
                            final int i4 = last_unread_date_final;
                            final boolean z2 = wasUnreadFinal;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (ChatActivity.this.last_message_id != 0) {
                                        MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, id2, ChatActivity.this.last_message_id, i4, z2, false);
                                    } else {
                                        MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, id2, ChatActivity.this.minMessageId[0], ChatActivity.this.maxDate[0], z2, false);
                                    }
                                }
                            }, 700);
                        }
                        this.first = false;
                    }
                    if (this.messages.isEmpty() && this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                        this.botUser = "";
                        updateBottomOverlay();
                    }
                    if (newRowsCount == 0 && this.currentEncryptedChat != null && !this.endReached[0]) {
                        this.first = true;
                        if (this.chatListView != null) {
                            this.chatListView.setEmptyView(null);
                        }
                        if (this.emptyViewContainer != null) {
                            this.emptyViewContainer.setVisibility(4);
                        }
                    } else if (this.progressView != null) {
                        this.progressView.setVisibility(4);
                    }
                    checkScrollForLoad(false);
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
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
        } else if (id == NotificationCenter.updateInterfaces) {
            Chat chat;
            int updateMask = ((Integer) args[0]).intValue();
            if (!((updateMask & 1) == 0 && (updateMask & 16) == 0)) {
                if (this.currentChat != null) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                    if (chat != null) {
                        this.currentChat = chat;
                    }
                } else if (this.currentUser != null) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                    if (user != null) {
                        this.currentUser = user;
                    }
                }
                updateTitle();
            }
            boolean updateSubtitle = false;
            if (!((updateMask & 32) == 0 && (updateMask & 4) == 0)) {
                if (!(this.currentChat == null || this.avatarContainer == null)) {
                    this.avatarContainer.updateOnlineCount();
                }
                updateSubtitle = true;
            }
            if (!((updateMask & 2) == 0 && (updateMask & 8) == 0 && (updateMask & 1) == 0)) {
                checkAndUpdateAvatar();
                updateVisibleRows();
            }
            if ((updateMask & 64) != 0) {
                updateSubtitle = true;
            }
            if ((updateMask & 8192) != 0 && ChatObject.isChannel(this.currentChat)) {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                if (chat != null) {
                    this.currentChat = chat;
                    updateSubtitle = true;
                    updateBottomOverlay();
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setDialogId(this.dialog_id);
                    }
                } else {
                    return;
                }
            }
            if (this.avatarContainer != null && updateSubtitle) {
                this.avatarContainer.updateSubtitle();
            }
            if ((updateMask & 128) != 0) {
                updateContactStatus();
            }
        } else if (id == NotificationCenter.didReceivedNewMessages) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                boolean updateChat = false;
                boolean hasFromMe = false;
                ArrayList<MessageObject> arr = args[1];
                if (this.currentEncryptedChat != null && arr.size() == 1) {
                    obj = (MessageObject) arr.get(0);
                    if (this.currentEncryptedChat != null && obj.isOut() && obj.messageOwner.action != null && (obj.messageOwner.action instanceof TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) && getParentActivity() != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 17 && this.currentEncryptedChat.ttl > 0 && this.currentEncryptedChat.ttl <= 60) {
                        r0 = new AlertDialog.Builder(getParentActivity());
                        r0.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        r0.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        r0.setMessage(LocaleController.formatString("CompatibilityChat", R.string.CompatibilityChat, this.currentUser.first_name, this.currentUser.first_name));
                        showDialog(r0.create());
                    }
                }
                if (!(this.currentChat == null && this.inlineReturn == 0)) {
                    for (a = 0; a < arr.size(); a++) {
                        messageObject = (MessageObject) arr.get(a);
                        if (this.currentChat != null) {
                            if (((messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) && messageObject.messageOwner.action.user_id == UserConfig.getClientUserId()) || ((messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) && messageObject.messageOwner.action.users.contains(Integer.valueOf(UserConfig.getClientUserId())))) {
                                Chat newChat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                                if (newChat != null) {
                                    this.currentChat = newChat;
                                    checkActionBarMenu();
                                    updateBottomOverlay();
                                    if (this.avatarContainer != null) {
                                        this.avatarContainer.updateSubtitle();
                                    }
                                }
                            } else if (messageObject.messageOwner.reply_to_msg_id != 0 && messageObject.replyMessageObject == null) {
                                messageObject.replyMessageObject = (MessageObject) this.messagesDict[0].get(Integer.valueOf(messageObject.messageOwner.reply_to_msg_id));
                                if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                                    messageObject.generatePinMessageText(null, null);
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                                    messageObject.generateGameMessageText(null);
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionPaymentSent) {
                                    messageObject.generatePaymentSentMessageText(null);
                                }
                            }
                        } else if (!(this.inlineReturn == 0 || messageObject.messageOwner.reply_markup == null)) {
                            for (b = 0; b < messageObject.messageOwner.reply_markup.rows.size(); b++) {
                                TL_keyboardButtonRow row = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(b);
                                for (int c = 0; c < row.buttons.size(); c++) {
                                    KeyboardButton button = (KeyboardButton) row.buttons.get(c);
                                    if (button instanceof TL_keyboardButtonSwitchInline) {
                                        processSwitchButton((TL_keyboardButtonSwitchInline) button);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                boolean reloadMegagroup = false;
                Bundle bundle;
                final BaseFragment baseFragment;
                final Bundle bundle2;
                final int i5;
                if (this.forwardEndReached[0]) {
                    boolean markAsRead = false;
                    boolean unreadUpdated = true;
                    int oldCount = this.messages.size();
                    int addedCount = 0;
                    HashMap<String, ArrayList<MessageObject>> webpagesToReload = null;
                    int placeToPaste = -1;
                    for (a = 0; a < arr.size(); a++) {
                        obj = (MessageObject) arr.get(a);
                        if (a == 0) {
                            if (obj.messageOwner.id < 0) {
                                placeToPaste = 0;
                            } else if (this.messages.isEmpty()) {
                                placeToPaste = 0;
                            } else {
                                int size = this.messages.size();
                                for (b = 0; b < size; b++) {
                                    MessageObject lastMessage = (MessageObject) this.messages.get(b);
                                    if (lastMessage.type >= 0 && lastMessage.messageOwner.date > 0) {
                                        if (lastMessage.messageOwner.id <= 0 || obj.messageOwner.id <= 0) {
                                            if (lastMessage.messageOwner.date < obj.messageOwner.date) {
                                                placeToPaste = b;
                                                break;
                                            }
                                        } else if (lastMessage.messageOwner.id < obj.messageOwner.id) {
                                            placeToPaste = b;
                                            break;
                                        }
                                    }
                                }
                                if (placeToPaste == -1 || placeToPaste > this.messages.size()) {
                                    placeToPaste = this.messages.size();
                                }
                            }
                        }
                        if (this.currentUser != null && this.currentUser.bot && obj.isOut()) {
                            obj.setIsRead();
                        }
                        if (!(this.avatarContainer == null || this.currentEncryptedChat == null || obj.messageOwner.action == null || !(obj.messageOwner.action instanceof TL_messageEncryptedAction) || !(obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            this.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (obj.type >= 0 && !this.messagesDict[0].containsKey(Integer.valueOf(obj.getId()))) {
                            if (this.currentEncryptedChat != null && (obj.messageOwner.media instanceof TL_messageMediaWebPage) && (obj.messageOwner.media.webpage instanceof TL_webPageUrlPending)) {
                                if (webpagesToReload == null) {
                                    webpagesToReload = new HashMap();
                                }
                                ArrayList<MessageObject> arrayList = (ArrayList) webpagesToReload.get(obj.messageOwner.media.webpage.url);
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    webpagesToReload.put(obj.messageOwner.media.webpage.url, arrayList);
                                }
                                arrayList.add(obj);
                            }
                            obj.checkLayout();
                            if (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                                bundle = new Bundle();
                                bundle.putInt("chat_id", obj.messageOwner.action.channel_id);
                                baseFragment = this.parentLayout.fragmentsStack.size() > 0 ? (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) : null;
                                bundle2 = bundle;
                                i5 = obj.messageOwner.action.channel_id;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        ActionBarLayout parentLayout = ChatActivity.this.parentLayout;
                                        if (baseFragment != null) {
                                            NotificationCenter.getInstance().removeObserver(baseFragment, NotificationCenter.closeChats);
                                        }
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                        parentLayout.presentFragment(new ChatActivity(bundle2), true);
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                MessagesController.getInstance().loadFullChat(i5, 0, true);
                                            }
                                        }, 1000);
                                    }
                                });
                                return;
                            }
                            if (this.currentChat != null && this.currentChat.megagroup && ((obj.messageOwner.action instanceof TL_messageActionChatAddUser) || (obj.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                                reloadMegagroup = true;
                            }
                            if (this.minDate[0] == 0 || obj.messageOwner.date < this.minDate[0]) {
                                this.minDate[0] = obj.messageOwner.date;
                            }
                            if (obj.isOut()) {
                                removeUnreadPlane();
                                hasFromMe = true;
                            }
                            if (obj.getId() > 0) {
                                this.maxMessageId[0] = Math.min(obj.getId(), this.maxMessageId[0]);
                                this.minMessageId[0] = Math.max(obj.getId(), this.minMessageId[0]);
                            } else if (this.currentEncryptedChat != null) {
                                this.maxMessageId[0] = Math.max(obj.getId(), this.maxMessageId[0]);
                                this.minMessageId[0] = Math.min(obj.getId(), this.minMessageId[0]);
                            }
                            this.maxDate[0] = Math.max(this.maxDate[0], obj.messageOwner.date);
                            this.messagesDict[0].put(Integer.valueOf(obj.getId()), obj);
                            dayArray = (ArrayList) this.messagesByDays.get(obj.dateKey);
                            if (dayArray == null) {
                                dayArray = new ArrayList();
                                this.messagesByDays.put(obj.dateKey, dayArray);
                                dateMsg = new Message();
                                dateMsg.message = LocaleController.formatDateChat((long) obj.messageOwner.date);
                                dateMsg.id = 0;
                                dateMsg.date = obj.messageOwner.date;
                                messageObject = new MessageObject(dateMsg, null, false);
                                messageObject.type = 10;
                                messageObject.contentType = 1;
                                messageObject.isDateObject = true;
                                this.messages.add(placeToPaste, messageObject);
                                addedCount++;
                            }
                            if (!obj.isOut()) {
                                if (this.paused && placeToPaste == 0) {
                                    if (!(this.scrollToTopUnReadOnResume || this.unreadMessageObject == null)) {
                                        removeMessageObject(this.unreadMessageObject);
                                        if (placeToPaste > 0) {
                                            placeToPaste--;
                                        }
                                        this.unreadMessageObject = null;
                                    }
                                    if (this.unreadMessageObject == null) {
                                        dateMsg = new Message();
                                        dateMsg.message = "";
                                        dateMsg.id = 0;
                                        messageObject = new MessageObject(dateMsg, null, false);
                                        messageObject.type = 6;
                                        messageObject.contentType = 2;
                                        this.messages.add(0, messageObject);
                                        this.unreadMessageObject = messageObject;
                                        this.scrollToMessage = this.unreadMessageObject;
                                        this.scrollToMessagePosition = -10000;
                                        unreadUpdated = false;
                                        this.unread_to_load = 0;
                                        this.scrollToTopUnReadOnResume = true;
                                        addedCount++;
                                    }
                                }
                                if (this.unreadMessageObject != null) {
                                    this.unread_to_load++;
                                    unreadUpdated = true;
                                }
                                if (obj.isUnread()) {
                                    if (!this.paused) {
                                        obj.setIsRead();
                                    }
                                    markAsRead = true;
                                }
                            }
                            dayArray.add(0, obj);
                            if (placeToPaste > this.messages.size()) {
                                placeToPaste = this.messages.size();
                            }
                            this.messages.add(placeToPaste, obj);
                            addedCount++;
                            this.newUnreadMessageCount++;
                            if (obj.type == 10 || obj.type == 11) {
                                updateChat = true;
                            }
                        }
                    }
                    if (webpagesToReload != null) {
                        MessagesController.getInstance().reloadWebPages(this.dialog_id, webpagesToReload);
                    }
                    if (this.progressView != null) {
                        this.progressView.setVisibility(4);
                    }
                    if (this.chatAdapter != null) {
                        if (unreadUpdated) {
                            this.chatAdapter.updateRowWithMessageObject(this.unreadMessageObject);
                        }
                        if (addedCount != 0) {
                            insertStart = this.chatAdapter.getItemCount() - placeToPaste;
                            this.chatAdapter.notifyItemChanged(insertStart - 1);
                            this.chatAdapter.notifyItemRangeInserted(insertStart, addedCount);
                        }
                    } else {
                        this.scrollToTopOnResume = true;
                    }
                    if (this.chatListView == null || this.chatAdapter == null) {
                        this.scrollToTopOnResume = true;
                    } else {
                        int lastVisible = this.chatLayoutManager.findLastVisibleItemPosition();
                        if (lastVisible == -1) {
                            lastVisible = 0;
                        }
                        if (this.endReached[0]) {
                            lastVisible++;
                        }
                        if (this.chatAdapter.isBot) {
                            oldCount++;
                        }
                        if (lastVisible >= oldCount || hasFromMe) {
                            this.newUnreadMessageCount = 0;
                            if (!this.firstLoading) {
                                if (this.paused) {
                                    this.scrollToTopOnResume = true;
                                } else {
                                    this.forceScrollToTop = true;
                                    moveScrollToLastMessage();
                                }
                            }
                        } else {
                            if (!(this.newUnreadMessageCount == 0 || this.pagedownButtonCounter == null)) {
                                this.pagedownButtonCounter.setVisibility(0);
                                this.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newUnreadMessageCount)}));
                            }
                            showPagedownButton(true, true);
                        }
                    }
                    if (markAsRead) {
                        if (this.paused) {
                            this.readWhenResume = true;
                            this.readWithDate = this.maxDate[0];
                            this.readWithMid = this.minMessageId[0];
                        } else {
                            MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject) this.messages.get(0)).getId(), this.minMessageId[0], this.maxDate[0], true, false);
                        }
                    }
                } else {
                    int currentMaxDate = Integer.MIN_VALUE;
                    int currentMinMsgId = Integer.MIN_VALUE;
                    if (this.currentEncryptedChat != null) {
                        currentMinMsgId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    boolean currentMarkAsRead = false;
                    for (a = 0; a < arr.size(); a++) {
                        obj = (MessageObject) arr.get(a);
                        if (this.currentUser != null && this.currentUser.bot && obj.isOut()) {
                            obj.setIsRead();
                        }
                        if (!(this.avatarContainer == null || this.currentEncryptedChat == null || obj.messageOwner.action == null || !(obj.messageOwner.action instanceof TL_messageEncryptedAction) || !(obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            this.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                            bundle = new Bundle();
                            bundle.putInt("chat_id", obj.messageOwner.action.channel_id);
                            baseFragment = this.parentLayout.fragmentsStack.size() > 0 ? (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) : null;
                            bundle2 = bundle;
                            i5 = obj.messageOwner.action.channel_id;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ActionBarLayout parentLayout = ChatActivity.this.parentLayout;
                                    if (baseFragment != null) {
                                        NotificationCenter.getInstance().removeObserver(baseFragment, NotificationCenter.closeChats);
                                    }
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    parentLayout.presentFragment(new ChatActivity(bundle2), true);
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.getInstance().loadFullChat(i5, 0, true);
                                        }
                                    }, 1000);
                                }
                            });
                            return;
                        }
                        if (this.currentChat != null && this.currentChat.megagroup && ((obj.messageOwner.action instanceof TL_messageActionChatAddUser) || (obj.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                            reloadMegagroup = true;
                        }
                        if (obj.isOut() && obj.isSending()) {
                            scrollToLastMessage(false);
                            return;
                        }
                        if (obj.type >= 0 && !this.messagesDict[0].containsKey(Integer.valueOf(obj.getId()))) {
                            obj.checkLayout();
                            currentMaxDate = Math.max(currentMaxDate, obj.messageOwner.date);
                            if (obj.getId() > 0) {
                                currentMinMsgId = Math.max(obj.getId(), currentMinMsgId);
                                this.last_message_id = Math.max(this.last_message_id, obj.getId());
                            } else if (this.currentEncryptedChat != null) {
                                currentMinMsgId = Math.min(obj.getId(), currentMinMsgId);
                                this.last_message_id = Math.min(this.last_message_id, obj.getId());
                            }
                            if (!obj.isOut() && obj.isUnread()) {
                                this.unread_to_load++;
                                currentMarkAsRead = true;
                            }
                            this.newUnreadMessageCount++;
                            if (obj.type == 10 || obj.type == 11) {
                                updateChat = true;
                            }
                        }
                    }
                    if (!(this.newUnreadMessageCount == 0 || this.pagedownButtonCounter == null)) {
                        this.pagedownButtonCounter.setVisibility(0);
                        this.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newUnreadMessageCount)}));
                    }
                    if (currentMarkAsRead) {
                        if (this.paused) {
                            this.readWhenResume = true;
                            this.readWithDate = currentMaxDate;
                            this.readWithMid = currentMinMsgId;
                        } else if (this.messages.size() > 0) {
                            MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject) this.messages.get(0)).getId(), currentMinMsgId, currentMaxDate, true, false);
                        }
                    }
                    updateVisibleRows();
                }
                if (!(this.messages.isEmpty() || this.botUser == null || this.botUser.length() != 0)) {
                    this.botUser = null;
                    updateBottomOverlay();
                }
                if (updateChat) {
                    updateTitle();
                    checkAndUpdateAvatar();
                }
                if (reloadMegagroup) {
                    MessagesController.getInstance().loadFullChat(this.currentChat.id, 0, true);
                }
            }
        } else if (id == NotificationCenter.closeChats) {
            if (args == null || args.length <= 0) {
                removeSelfFromStack();
            } else if (((Long) args[0]).longValue() == this.dialog_id) {
                finishFragment();
            }
        } else if (id == NotificationCenter.messagesRead) {
            SparseArray<Long> inbox = args[0];
            SparseArray<Long> outbox = args[1];
            updated = false;
            b = 0;
            while (b < inbox.size()) {
                key = inbox.keyAt(b);
                long messageId = ((Long) inbox.get(key)).longValue();
                if (((long) key) != this.dialog_id) {
                    b++;
                } else {
                    for (a = 0; a < this.messages.size(); a++) {
                        obj = (MessageObject) this.messages.get(a);
                        if (!obj.isOut() && obj.getId() > 0 && obj.getId() <= ((int) messageId)) {
                            if (!obj.isUnread()) {
                                break;
                            }
                            obj.setIsRead();
                            updated = true;
                        }
                    }
                    while (b < outbox.size()) {
                        key = outbox.keyAt(b);
                        messageId = (int) ((Long) outbox.get(key)).longValue();
                        if (((long) key) == this.dialog_id) {
                        } else {
                            for (a = 0; a < this.messages.size(); a++) {
                                obj = (MessageObject) this.messages.get(a);
                                if (obj.isOut() && obj.getId() > 0 && obj.getId() <= messageId) {
                                    if (!obj.isUnread()) {
                                        break;
                                    }
                                    obj.setIsRead();
                                    updated = true;
                                }
                            }
                            if (!updated) {
                                updateVisibleRows();
                            }
                        }
                    }
                    if (!updated) {
                        updateVisibleRows();
                    }
                }
            }
            for (b = 0; b < outbox.size(); b++) {
                key = outbox.keyAt(b);
                messageId = (int) ((Long) outbox.get(key)).longValue();
                if (((long) key) == this.dialog_id) {
                    for (a = 0; a < this.messages.size(); a++) {
                        obj = (MessageObject) this.messages.get(a);
                        if (!obj.isUnread()) {
                            break;
                        }
                        obj.setIsRead();
                        updated = true;
                    }
                    if (!updated) {
                        updateVisibleRows();
                    }
                }
            }
            if (!updated) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            ArrayList<Integer> markAsDeletedMessages = args[0];
            int channelId = ((Integer) args[1]).intValue();
            loadIndex = 0;
            if (ChatObject.isChannel(this.currentChat)) {
                if (channelId == 0 && this.mergeDialogId != 0) {
                    loadIndex = 1;
                } else if (channelId == this.currentChat.id) {
                    loadIndex = 0;
                } else {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            updated = false;
            for (a = 0; a < markAsDeletedMessages.size(); a++) {
                Integer ids = (Integer) markAsDeletedMessages.get(a);
                obj = (MessageObject) this.messagesDict[loadIndex].get(ids);
                if (loadIndex == 0 && this.info != null && this.info.pinned_msg_id == ids.intValue()) {
                    this.pinnedMessageObject = null;
                    this.info.pinned_msg_id = 0;
                    MessagesStorage.getInstance().updateChannelPinnedMessage(channelId, 0);
                    updatePinnedMessageView(true);
                }
                if (obj != null) {
                    index = this.messages.indexOf(obj);
                    if (index != -1) {
                        this.messages.remove(index);
                        this.messagesDict[loadIndex].remove(ids);
                        dayArr = (ArrayList) this.messagesByDays.get(obj.dateKey);
                        if (dayArr != null) {
                            dayArr.remove(obj);
                            if (dayArr.isEmpty()) {
                                this.messagesByDays.remove(obj.dateKey);
                                if (index >= 0 && index < this.messages.size()) {
                                    this.messages.remove(index);
                                }
                            }
                        }
                        updated = true;
                    }
                }
            }
            if (this.messages.isEmpty()) {
                if (this.endReached[0] || this.loading) {
                    if (this.botButtons != null) {
                        this.botButtons = null;
                        if (this.chatActivityEnterView != null) {
                            this.chatActivityEnterView.setButtons(null, false);
                        }
                    }
                    if (this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                        this.botUser = "";
                        updateBottomOverlay();
                    }
                } else {
                    int[] iArr;
                    if (this.progressView != null) {
                        this.progressView.setVisibility(4);
                    }
                    if (this.chatListView != null) {
                        this.chatListView.setEmptyView(null);
                    }
                    if (this.currentEncryptedChat == null) {
                        iArr = this.maxMessageId;
                        this.maxMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        iArr[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        iArr = this.minMessageId;
                        this.minMessageId[1] = Integer.MIN_VALUE;
                        iArr[0] = Integer.MIN_VALUE;
                    } else {
                        iArr = this.maxMessageId;
                        this.maxMessageId[1] = Integer.MIN_VALUE;
                        iArr[0] = Integer.MIN_VALUE;
                        iArr = this.minMessageId;
                        this.minMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        iArr[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    iArr = this.maxDate;
                    this.maxDate[1] = Integer.MIN_VALUE;
                    iArr[0] = Integer.MIN_VALUE;
                    iArr = this.minDate;
                    this.minDate[1] = 0;
                    iArr[0] = 0;
                    this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                    r11 = MessagesController.getInstance();
                    r12 = this.dialog_id;
                    boolean z3 = !this.cacheEndReached[0];
                    int i6 = this.minDate[0];
                    r19 = this.classGuid;
                    r22 = ChatObject.isChannel(this.currentChat);
                    r23 = this.lastLoadIndex;
                    this.lastLoadIndex = r23 + 1;
                    r11.loadMessages(r12, 30, 0, 0, z3, i6, r19, 0, 0, r22, r23);
                    this.loading = true;
                }
            }
            if (updated && this.chatAdapter != null) {
                removeUnreadPlane();
                this.chatAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[0];
            obj = (MessageObject) this.messagesDict[0].get(msgId);
            if (obj != null) {
                Integer newMsgId = args[1];
                if (newMsgId.equals(msgId) || !this.messagesDict[0].containsKey(newMsgId)) {
                    Message newMsgObj = args[2];
                    mediaUpdated = false;
                    boolean updatedForward = false;
                    if (newMsgObj != null) {
                        try {
                            updatedForward = obj.isForwarded() && ((obj.messageOwner.reply_markup == null && newMsgObj.reply_markup != null) || !obj.messageOwner.message.equals(newMsgObj.message));
                            mediaUpdated = updatedForward || ((obj.messageOwner.params != null && obj.messageOwner.params.containsKey("query_id")) || !(newMsgObj.media == null || obj.messageOwner.media == null || newMsgObj.media.getClass().equals(obj.messageOwner.media.getClass())));
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                        obj.messageOwner = newMsgObj;
                        obj.generateThumbs(true);
                        obj.setType();
                        if (newMsgObj.media instanceof TL_messageMediaGame) {
                            obj.applyNewText();
                        }
                    }
                    if (updatedForward) {
                        obj.measureInlineBotButtons();
                    }
                    this.messagesDict[0].remove(msgId);
                    this.messagesDict[0].put(newMsgId, obj);
                    obj.messageOwner.id = newMsgId.intValue();
                    obj.messageOwner.send_state = 0;
                    obj.forceUpdate = mediaUpdated;
                    messArr = new ArrayList();
                    messArr.add(obj);
                    if (this.currentEncryptedChat == null) {
                        MessagesQuery.loadReplyMessagesForMessages(messArr, this.dialog_id);
                    }
                    if (this.chatAdapter != null) {
                        this.chatAdapter.updateRowWithMessageObject(obj);
                    }
                    if (this.chatLayoutManager != null && mediaUpdated && this.chatLayoutManager.findLastVisibleItemPosition() >= this.messages.size() - 1) {
                        moveScrollToLastMessage();
                    }
                    NotificationsController.getInstance().playOutChatSound();
                    return;
                }
                MessageObject removed = (MessageObject) this.messagesDict[0].remove(msgId);
                if (removed != null) {
                    index = this.messages.indexOf(removed);
                    this.messages.remove(index);
                    dayArr = (ArrayList) this.messagesByDays.get(removed.dateKey);
                    dayArr.remove(obj);
                    if (dayArr.isEmpty()) {
                        this.messagesByDays.remove(obj.dateKey);
                        if (index >= 0 && index < this.messages.size()) {
                            this.messages.remove(index);
                        }
                    }
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (id == NotificationCenter.messageReceivedByAck) {
            obj = (MessageObject) this.messagesDict[0].get((Integer) args[0]);
            if (obj != null) {
                obj.messageOwner.send_state = 0;
                if (this.chatAdapter != null) {
                    this.chatAdapter.updateRowWithMessageObject(obj);
                }
            }
        } else if (id == NotificationCenter.messageSendError) {
            obj = (MessageObject) this.messagesDict[0].get((Integer) args[0]);
            if (obj != null) {
                obj.messageOwner.send_state = 2;
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (this.currentChat != null && chatFull.id == this.currentChat.id) {
                if (chatFull instanceof TL_channelFull) {
                    if (this.currentChat.megagroup) {
                        int lastDate = 0;
                        if (chatFull.participants != null) {
                            for (a = 0; a < chatFull.participants.participants.size(); a++) {
                                lastDate = Math.max(((ChatParticipant) chatFull.participants.participants.get(a)).date, lastDate);
                            }
                        }
                        if (lastDate == 0 || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastDate)) > 3600) {
                            MessagesController.getInstance().loadChannelParticipants(Integer.valueOf(this.currentChat.id));
                        }
                    }
                    if (chatFull.participants == null && this.info != null) {
                        chatFull.participants = this.info.participants;
                    }
                }
                this.info = chatFull;
                if (this.mentionsAdapter != null) {
                    this.mentionsAdapter.setChatInfo(this.info);
                }
                if (args[3] instanceof MessageObject) {
                    this.pinnedMessageObject = (MessageObject) args[3];
                    updatePinnedMessageView(false);
                } else {
                    updatePinnedMessageView(true);
                }
                if (this.avatarContainer != null) {
                    this.avatarContainer.updateOnlineCount();
                    this.avatarContainer.updateSubtitle();
                }
                if (this.isBroadcast) {
                    SendMessagesHelper.getInstance().setCurrentChatInfo(this.info);
                }
                if (this.info instanceof TL_chatFull) {
                    this.hasBotsCommands = false;
                    this.botInfo.clear();
                    this.botsCount = 0;
                    URLSpanBotCommand.enabled = false;
                    for (a = 0; a < this.info.participants.participants.size(); a++) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                        if (user != null && user.bot) {
                            URLSpanBotCommand.enabled = true;
                            this.botsCount++;
                            BotQuery.loadBotInfo(user.id, true, this.classGuid);
                        }
                    }
                    if (this.chatListView != null) {
                        this.chatListView.invalidateViews();
                    }
                } else if (this.info instanceof TL_channelFull) {
                    this.hasBotsCommands = false;
                    this.botInfo.clear();
                    this.botsCount = 0;
                    z = (this.info.bot_info.isEmpty() || this.currentChat == null || !this.currentChat.megagroup) ? false : true;
                    URLSpanBotCommand.enabled = z;
                    this.botsCount = this.info.bot_info.size();
                    for (a = 0; a < this.info.bot_info.size(); a++) {
                        BotInfo bot = (BotInfo) this.info.bot_info.get(a);
                        if (!bot.commands.isEmpty() && (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup))) {
                            this.hasBotsCommands = true;
                        }
                        this.botInfo.put(Integer.valueOf(bot.user_id), bot);
                    }
                    if (this.chatListView != null) {
                        this.chatListView.invalidateViews();
                    }
                    if (this.mentionsAdapter != null && (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup))) {
                        this.mentionsAdapter.setBotInfo(this.botInfo);
                    }
                }
                if (this.chatActivityEnterView != null) {
                    this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
                }
                if (this.mentionsAdapter != null) {
                    this.mentionsAdapter.setBotsCount(this.botsCount);
                }
                if (ChatObject.isChannel(this.currentChat) && this.mergeDialogId == 0 && this.info.migrated_from_chat_id != 0) {
                    this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
                    this.maxMessageId[1] = this.info.migrated_from_max_id;
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (id == NotificationCenter.chatInfoCantLoad) {
            int chatId = ((Integer) args[0]).intValue();
            if (this.currentChat != null && this.currentChat.id == chatId) {
                int reason = ((Integer) args[1]).intValue();
                if (getParentActivity() != null && this.closeChatDialog == null) {
                    r0 = new AlertDialog.Builder(getParentActivity());
                    r0.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    if (reason == 0) {
                        r0.setMessage(LocaleController.getString("ChannelCantOpenPrivate", R.string.ChannelCantOpenPrivate));
                    } else if (reason == 1) {
                        r0.setMessage(LocaleController.getString("ChannelCantOpenNa", R.string.ChannelCantOpenNa));
                    } else if (reason == 2) {
                        r0.setMessage(LocaleController.getString("ChannelCantOpenBanned", R.string.ChannelCantOpenBanned));
                    }
                    r0.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    Dialog create = r0.create();
                    this.closeChatDialog = create;
                    showDialog(create);
                    this.loading = false;
                    if (this.progressView != null) {
                        this.progressView.setVisibility(4);
                    }
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateContactStatus();
            if (this.currentEncryptedChat != null) {
                updateSpamView();
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.updateSubtitle();
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat chat2 = args[0];
            if (this.currentEncryptedChat != null && chat2.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = chat2;
                updateContactStatus();
                updateSecretStatus();
                initStickers();
                if (this.chatActivityEnterView != null) {
                    ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
                    z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23;
                    boolean z4 = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
                    chatActivityEnterView.setAllowStickersAndGifs(z, z4);
                }
                if (this.mentionsAdapter != null) {
                    MentionsAdapter mentionsAdapter = this.mentionsAdapter;
                    z = !this.chatActivityEnterView.isEditingMessage() && (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46);
                    mentionsAdapter.setNeedBotContext(z);
                }
            }
        } else if (id == NotificationCenter.messagesReadEncrypted) {
            int encId = ((Integer) args[0]).intValue();
            if (this.currentEncryptedChat != null && this.currentEncryptedChat.id == encId) {
                int date = ((Integer) args[1]).intValue();
                r5 = this.messages.iterator();
                while (r5.hasNext()) {
                    obj = (MessageObject) r5.next();
                    if (obj.isOut()) {
                        if (obj.isOut() && !obj.isUnread()) {
                            break;
                        } else if (obj.messageOwner.date - 1 <= date) {
                            obj.setIsRead();
                        }
                    }
                }
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.audioDidReset || id == NotificationCenter.audioPlayStateChanged) {
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        messageObject = cell.getMessageObject();
                        if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                            cell.updateButtonState(false);
                        }
                    }
                }
                count = this.mentionListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.mentionListView.getChildAt(a);
                    if (view instanceof ContextLinkCell) {
                        cell = (ContextLinkCell) view;
                        messageObject = cell.getMessageObject();
                        if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                            cell.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.audioProgressDidChanged) {
            Integer mid = args[0];
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        if (cell.getMessageObject() != null && cell.getMessageObject().getId() == mid.intValue()) {
                            MessageObject playing = cell.getMessageObject();
                            MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                            if (player != null) {
                                playing.audioProgress = player.audioProgress;
                                playing.audioProgressSec = player.audioProgressSec;
                                cell.updateAudioProgress();
                                return;
                            }
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            if (this.dialog_id == ((Long) args[0]).longValue()) {
                this.messages.clear();
                this.waitingForLoad.clear();
                this.messagesByDays.clear();
                for (a = 1; a >= 0; a--) {
                    this.messagesDict[a].clear();
                    if (this.currentEncryptedChat == null) {
                        this.maxMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        this.minMessageId[a] = Integer.MIN_VALUE;
                    } else {
                        this.maxMessageId[a] = Integer.MIN_VALUE;
                        this.minMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    this.maxDate[a] = Integer.MIN_VALUE;
                    this.minDate[a] = 0;
                    this.selectedMessagesIds[a].clear();
                    this.selectedMessagesCanCopyIds[a].clear();
                }
                this.cantDeleteMessagesCount = 0;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                if (this.botButtons != null) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setButtons(null, false);
                    }
                }
                if (this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                    this.botUser = "";
                    updateBottomOverlay();
                }
                if (((Boolean) args[1]).booleanValue()) {
                    if (this.chatAdapter != null) {
                        this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? 0 : 4);
                        this.chatListView.setEmptyView(null);
                    }
                    for (a = 0; a < 2; a++) {
                        this.endReached[a] = false;
                        this.cacheEndReached[a] = false;
                        this.forwardEndReached[a] = true;
                    }
                    this.first = true;
                    this.firstLoading = true;
                    this.loading = true;
                    this.startLoadFromMessageId = 0;
                    this.needSelectFromMessageId = false;
                    this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                    r11 = MessagesController.getInstance();
                    r12 = this.dialog_id;
                    int i7 = AndroidUtilities.isTablet() ? 30 : 20;
                    r19 = this.classGuid;
                    r22 = ChatObject.isChannel(this.currentChat);
                    r23 = this.lastLoadIndex;
                    this.lastLoadIndex = r23 + 1;
                    r11.loadMessages(r12, i7, 0, 0, true, 0, r19, 2, 0, r22, r23);
                } else if (this.progressView != null) {
                    this.progressView.setVisibility(4);
                    this.chatListView.setEmptyView(this.emptyViewContainer);
                }
                if (this.chatAdapter != null) {
                    this.chatAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.screenshotTook) {
            updateInformationForScreenshotDetector();
        } else if (id == NotificationCenter.blockedUsersDidLoaded) {
            if (this.currentUser != null) {
                boolean oldValue = this.userBlocked;
                this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.currentUser.id));
                if (oldValue != this.userBlocked) {
                    updateBottomOverlay();
                }
            }
        } else if (id == NotificationCenter.FileNewChunkAvailable) {
            messageObject = (MessageObject) args[0];
            long finalSize = ((Long) args[2]).longValue();
            if (finalSize != 0 && this.dialog_id == messageObject.getDialogId()) {
                MessageObject currentObject = (MessageObject) this.messagesDict[0].get(Integer.valueOf(messageObject.getId()));
                if (currentObject != null) {
                    currentObject.messageOwner.media.document.size = (int) finalSize;
                    updateVisibleRows();
                }
            }
        } else if (id == NotificationCenter.didCreatedNewDeleteTask) {
            SparseArray<ArrayList<Integer>> mids = args[0];
            changed = false;
            for (int i8 = 0; i8 < mids.size(); i8++) {
                key = mids.keyAt(i8);
                r5 = ((ArrayList) mids.get(key)).iterator();
                while (r5.hasNext()) {
                    messageObject = (MessageObject) this.messagesDict[0].get((Integer) r5.next());
                    if (messageObject != null) {
                        messageObject.messageOwner.destroyTime = key;
                        changed = true;
                    }
                }
            }
            if (changed) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.audioDidStarted) {
            sendSecretMessageRead((MessageObject) args[0]);
            if (this.chatListView != null) {
                MessageObject messageObject1;
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        messageObject1 = cell.getMessageObject();
                        if (messageObject1 != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                            cell.updateButtonState(false);
                        }
                    }
                }
                count = this.mentionListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.mentionListView.getChildAt(a);
                    if (view instanceof ContextLinkCell) {
                        cell = (ContextLinkCell) view;
                        messageObject1 = cell.getMessageObject();
                        if (messageObject1 != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                            cell.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.updateMessageMedia) {
            messageObject = (MessageObject) args[0];
            MessageObject existMessageObject = (MessageObject) this.messagesDict[0].get(Integer.valueOf(messageObject.getId()));
            if (existMessageObject != null) {
                existMessageObject.messageOwner.media = messageObject.messageOwner.media;
                existMessageObject.messageOwner.attachPath = messageObject.messageOwner.attachPath;
                existMessageObject.generateThumbs(false);
            }
            updateVisibleRows();
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            did = ((Long) args[0]).longValue();
            if (did == this.dialog_id || did == this.mergeDialogId) {
                loadIndex = did == this.dialog_id ? 0 : 1;
                changed = false;
                mediaUpdated = false;
                ArrayList<MessageObject> messageObjects = args[1];
                for (a = 0; a < messageObjects.size(); a++) {
                    messageObject = (MessageObject) messageObjects.get(a);
                    MessageObject old = (MessageObject) this.messagesDict[loadIndex].get(Integer.valueOf(messageObject.getId()));
                    if (this.pinnedMessageObject != null && this.pinnedMessageObject.getId() == messageObject.getId()) {
                        this.pinnedMessageObject = messageObject;
                        updatePinnedMessageView(true);
                    }
                    if (old != null) {
                        if (messageObject.type >= 0) {
                            if (!mediaUpdated && (messageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                                mediaUpdated = true;
                            }
                            if (old.replyMessageObject != null) {
                                messageObject.replyMessageObject = old.replyMessageObject;
                                if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                                    messageObject.generateGameMessageText(null);
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionPaymentSent) {
                                    messageObject.generatePaymentSentMessageText(null);
                                }
                            }
                            messageObject.messageOwner.attachPath = old.messageOwner.attachPath;
                            messageObject.attachPathExists = old.attachPathExists;
                            messageObject.mediaExists = old.mediaExists;
                            this.messagesDict[loadIndex].put(Integer.valueOf(old.getId()), messageObject);
                        } else {
                            this.messagesDict[loadIndex].remove(Integer.valueOf(old.getId()));
                        }
                        index = this.messages.indexOf(old);
                        if (index >= 0) {
                            dayArr = (ArrayList) this.messagesByDays.get(old.dateKey);
                            int index2 = -1;
                            if (dayArr != null) {
                                index2 = dayArr.indexOf(old);
                            }
                            if (messageObject.type >= 0) {
                                this.messages.set(index, messageObject);
                                if (this.chatAdapter != null) {
                                    this.chatAdapter.notifyItemChanged(((this.chatAdapter.messagesStartRow + this.messages.size()) - index) - 1);
                                }
                                if (index2 >= 0) {
                                    dayArr.set(index2, messageObject);
                                }
                            } else {
                                this.messages.remove(index);
                                if (this.chatAdapter != null) {
                                    this.chatAdapter.notifyItemRemoved(((this.chatAdapter.messagesStartRow + this.messages.size()) - index) - 1);
                                }
                                if (index2 >= 0) {
                                    dayArr.remove(index2);
                                    if (dayArr.isEmpty()) {
                                        this.messagesByDays.remove(old.dateKey);
                                        this.messages.remove(index);
                                        this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + this.messages.size());
                                    }
                                }
                            }
                            changed = true;
                        }
                    }
                }
                if (changed && this.chatLayoutManager != null && mediaUpdated) {
                    if (this.chatLayoutManager.findLastVisibleItemPosition() >= this.messages.size() - (this.chatAdapter.isBot ? 2 : 1)) {
                        moveScrollToLastMessage();
                    }
                }
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateTitleIcons();
            if (ChatObject.isChannel(this.currentChat)) {
                updateBottomOverlay();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.didLoadedPinnedMessage) {
            MessageObject message = args[0];
            if (message.getDialogId() == this.dialog_id && this.info != null && this.info.pinned_msg_id == message.getId()) {
                this.pinnedMessageObject = message;
                this.loadingPinnedMessage = 0;
                updatePinnedMessageView(true);
            }
        } else if (id == NotificationCenter.didReceivedWebpages) {
            ArrayList<Message> arrayList2 = args[0];
            updated = false;
            for (a = 0; a < arrayList2.size(); a++) {
                message = (Message) arrayList2.get(a);
                did = MessageObject.getDialogId(message);
                if (did == this.dialog_id || did == this.mergeDialogId) {
                    currentMessage = (MessageObject) this.messagesDict[did == this.dialog_id ? 0 : 1].get(Integer.valueOf(message.id));
                    if (currentMessage != null) {
                        currentMessage.messageOwner.media = new TL_messageMediaWebPage();
                        currentMessage.messageOwner.media.webpage = message.media.webpage;
                        currentMessage.generateThumbs(true);
                        updated = true;
                    }
                }
            }
            if (updated) {
                updateVisibleRows();
                if (this.chatLayoutManager != null && this.chatLayoutManager.findLastVisibleItemPosition() >= this.messages.size() - 1) {
                    moveScrollToLastMessage();
                }
            }
        } else if (id == NotificationCenter.didReceivedWebpagesInUpdates) {
            if (this.foundWebPage != null) {
                for (WebPage webPage : args[0].values()) {
                    if (webPage.id == this.foundWebPage.id) {
                        showReplyPanel(!(webPage instanceof TL_webPageEmpty), null, null, webPage, false);
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.messagesReadContent) {
            ArrayList<Long> arrayList3 = args[0];
            updated = false;
            for (a = 0; a < arrayList3.size(); a++) {
                currentMessage = (MessageObject) this.messagesDict[this.mergeDialogId == 0 ? 0 : 1].get(Integer.valueOf((int) ((Long) arrayList3.get(a)).longValue()));
                if (currentMessage != null) {
                    currentMessage.setContentIsRead();
                    updated = true;
                }
            }
            if (updated) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.botInfoDidLoaded) {
            if (this.classGuid == ((Integer) args[1]).intValue()) {
                BotInfo info = args[0];
                if (this.currentEncryptedChat == null) {
                    if (!(info.commands.isEmpty() || ChatObject.isChannel(this.currentChat))) {
                        this.hasBotsCommands = true;
                    }
                    this.botInfo.put(Integer.valueOf(info.user_id), info);
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyItemChanged(0);
                    }
                    if (this.mentionsAdapter != null && (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup))) {
                        this.mentionsAdapter.setBotInfo(this.botInfo);
                    }
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
                    }
                }
                updateBotButtons();
            }
        } else if (id == NotificationCenter.botKeyboardDidLoaded) {
            if (this.dialog_id == ((Long) args[1]).longValue()) {
                message = (Message) args[0];
                if (message == null || this.userBlocked) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                            this.botReplyButtons = null;
                            showReplyPanel(false, null, null, null, false);
                        }
                        this.chatActivityEnterView.setButtons(this.botButtons);
                        return;
                    }
                    return;
                }
                this.botButtons = new MessageObject(message, null, false);
                if (this.chatActivityEnterView == null) {
                    return;
                }
                if (this.botButtons.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply) {
                    if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("answered_" + this.dialog_id, 0) == this.botButtons.getId()) {
                        return;
                    }
                    if (this.replyingMessageObject == null || this.chatActivityEnterView.getFieldText() == null) {
                        this.botReplyButtons = this.botButtons;
                        this.chatActivityEnterView.setButtons(this.botButtons);
                        showReplyPanel(true, this.botButtons, null, null, false);
                        return;
                    }
                    return;
                }
                if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                    this.botReplyButtons = null;
                    showReplyPanel(false, null, null, null, false);
                }
                this.chatActivityEnterView.setButtons(this.botButtons);
            }
        } else if (id == NotificationCenter.chatSearchResultsAvailable) {
            if (this.classGuid == ((Integer) args[0]).intValue()) {
                messageId = ((Integer) args[1]).intValue();
                did = ((Long) args[3]).longValue();
                if (messageId != 0) {
                    scrollToMessageId(messageId, 0, true, did == this.dialog_id ? 0 : 1);
                }
                updateSearchButtons(((Integer) args[2]).intValue(), ((Integer) args[4]).intValue(), ((Integer) args[5]).intValue());
            }
        } else if (id == NotificationCenter.didUpdatedMessagesViews) {
            SparseIntArray array = (SparseIntArray) args[0].get((int) this.dialog_id);
            if (array != null) {
                updated = false;
                for (a = 0; a < array.size(); a++) {
                    messageId = array.keyAt(a);
                    messageObject = (MessageObject) this.messagesDict[0].get(Integer.valueOf(messageId));
                    if (messageObject != null) {
                        int newValue = array.get(messageId);
                        if (newValue > messageObject.messageOwner.views) {
                            messageObject.messageOwner.views = newValue;
                            updated = true;
                        }
                    }
                }
                if (updated) {
                    updateVisibleRows();
                }
            }
        } else if (id == NotificationCenter.peerSettingsDidLoaded) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                updateSpamView();
            }
        } else if (id == NotificationCenter.newDraftReceived) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                applyDraftMaybe(true);
            }
        } else if (id == NotificationCenter.userInfoDidLoaded) {
            Integer uid = args[0];
            if (this.currentUser != null && this.currentUser.id == uid.intValue()) {
                TL_userFull userFull = args[1];
                if (this.headerItem == null) {
                    return;
                }
                if (userFull.phone_calls_available) {
                    this.headerItem.showSubItem(32);
                } else {
                    this.headerItem.hideSubItem(32);
                }
            }
        } else if (id == NotificationCenter.didSetNewWallpapper && this.fragmentView != null) {
            ((SizeNotifierFrameLayout) this.fragmentView).setBackgroundImage(Theme.getCachedWallpaper());
            this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
            if (this.emptyView != null) {
                this.emptyView.getBackground().setColorFilter(Theme.colorFilter);
            }
            if (this.bigEmptyView != null) {
                this.bigEmptyView.getBackground().setColorFilter(Theme.colorFilter);
            }
            this.chatListView.invalidateViews();
        }
    }

    public boolean processSwitchButton(TL_keyboardButtonSwitchInline button) {
        if (this.inlineReturn == 0 || button.same_peer) {
            return false;
        }
        String query = "@" + this.currentUser.username + " " + button.query;
        if (this.inlineReturn == this.dialog_id) {
            this.inlineReturn = 0;
            this.chatActivityEnterView.setFieldText(query);
        } else {
            DraftQuery.saveDraft(this.inlineReturn, query, null, null, false);
            if (this.parentLayout.fragmentsStack.size() > 1) {
                BaseFragment prevFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
                if ((prevFragment instanceof ChatActivity) && ((ChatActivity) prevFragment).dialog_id == this.inlineReturn) {
                    finishFragment();
                } else {
                    Bundle bundle = new Bundle();
                    int lower_part = (int) this.inlineReturn;
                    int high_part = (int) (this.inlineReturn >> 32);
                    if (lower_part == 0) {
                        bundle.putInt("enc_id", high_part);
                    } else if (lower_part > 0) {
                        bundle.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        bundle.putInt("chat_id", -lower_part);
                    }
                    presentFragment(new ChatActivity(bundle), true);
                }
            }
        }
        return true;
    }

    private void updateSearchButtons(int mask, int num, int count) {
        float f = 1.0f;
        if (this.searchUpButton != null) {
            boolean z;
            float f2;
            this.searchUpButton.setEnabled((mask & 1) != 0);
            ImageView imageView = this.searchDownButton;
            if ((mask & 2) != 0) {
                z = true;
            } else {
                z = false;
            }
            imageView.setEnabled(z);
            imageView = this.searchUpButton;
            if (this.searchUpButton.isEnabled()) {
                f2 = 1.0f;
            } else {
                f2 = 0.5f;
            }
            imageView.setAlpha(f2);
            ImageView imageView2 = this.searchDownButton;
            if (!this.searchDownButton.isEnabled()) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
            if (count == 0) {
                this.searchCountText.setText("");
            } else {
                this.searchCountText.setText(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(num + 1), Integer.valueOf(count)));
            }
        }
    }

    public boolean needDelayOpenAnimation() {
        return this.firstLoading;
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoaded, NotificationCenter.botKeyboardDidLoaded});
        NotificationCenter.getInstance().setAnimationInProgress(true);
        if (isOpen) {
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance().setAnimationInProgress(false);
        if (isOpen) {
            this.openAnimationEnded = true;
            if (this.currentUser != null) {
                MessagesController.getInstance().loadFullUser(this.currentUser, this.classGuid, false);
            }
            if (VERSION.SDK_INT >= 21) {
                createChatAttachView();
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.closeChatDialog != null && dialog == this.closeChatDialog) {
            MessagesController.getInstance().deleteDialog(this.dialog_id, 0);
            if (this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
                finishFragment();
                return;
            }
            BaseFragment fragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);
            removeSelfFromStack();
            fragment.finishFragment();
        }
    }

    public boolean extendActionMode(Menu menu) {
        if (!(this.chatActivityEnterView.getSelectionLength() == 0 || menu.findItem(16908321) == null)) {
            if (VERSION.SDK_INT >= 23) {
                menu.removeItem(16908341);
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(LocaleController.getString("Bold", R.string.Bold));
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, stringBuilder.length(), 33);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_bold, 6, stringBuilder);
            stringBuilder = new SpannableStringBuilder(LocaleController.getString("Italic", R.string.Italic));
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), 0, stringBuilder.length(), 33);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_italic, 7, stringBuilder);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_regular, 8, LocaleController.getString("Regular", R.string.Regular));
        }
        return true;
    }

    private void updateBottomOverlay() {
        if (this.bottomOverlayChatText != null) {
            if (this.currentChat != null) {
                if (!ChatObject.isChannel(this.currentChat) || (this.currentChat instanceof TL_channelForbidden)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisGroup", R.string.DeleteThisGroup));
                } else if (ChatObject.isNotInChat(this.currentChat)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelJoin", R.string.ChannelJoin));
                } else if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelUnmute", R.string.ChannelUnmute));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelMute", R.string.ChannelMute));
                }
            } else if (this.userBlocked) {
                if (this.currentUser.bot) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("BotUnblock", R.string.BotUnblock));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("Unblock", R.string.Unblock));
                }
                if (this.botButtons != null) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                            this.botReplyButtons = null;
                            showReplyPanel(false, null, null, null, false);
                        }
                        this.chatActivityEnterView.setButtons(this.botButtons, false);
                    }
                }
            } else if (this.botUser == null || !this.currentUser.bot) {
                this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisChat", R.string.DeleteThisChat));
            } else {
                this.bottomOverlayChatText.setText(LocaleController.getString("BotStart", R.string.BotStart));
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
            if (this.searchItem == null || this.searchItem.getVisibility() != 0) {
                this.searchContainer.setVisibility(4);
                if ((this.currentChat == null || (!ChatObject.isNotInChat(this.currentChat) && ChatObject.canWriteToChat(this.currentChat))) && (this.currentUser == null || !(UserObject.isDeleted(this.currentUser) || this.userBlocked))) {
                    if (this.botUser == null || !this.currentUser.bot) {
                        this.chatActivityEnterView.setVisibility(0);
                        this.bottomOverlayChat.setVisibility(4);
                    } else {
                        this.bottomOverlayChat.setVisibility(0);
                        this.chatActivityEnterView.setVisibility(4);
                    }
                    if (this.muteItem != null) {
                        this.muteItem.setVisibility(0);
                    }
                } else {
                    this.bottomOverlayChat.setVisibility(0);
                    if (this.muteItem != null) {
                        this.muteItem.setVisibility(8);
                    }
                    this.chatActivityEnterView.setFieldFocused(false);
                    this.chatActivityEnterView.setVisibility(4);
                    this.attachItem.setVisibility(8);
                    this.headerItem.setVisibility(0);
                }
                if (this.topViewWasVisible == 1) {
                    this.chatActivityEnterView.showTopView(false, false);
                    this.topViewWasVisible = 0;
                }
            } else {
                this.searchContainer.setVisibility(0);
                this.bottomOverlayChat.setVisibility(4);
                this.chatActivityEnterView.setFieldFocused(false);
                this.chatActivityEnterView.setVisibility(4);
                if (this.chatActivityEnterView.isTopViewVisible()) {
                    this.topViewWasVisible = 1;
                    this.chatActivityEnterView.hideTopView(false);
                } else {
                    this.topViewWasVisible = 2;
                }
            }
            checkRaiseSensors();
        }
    }

    public void showAlert(String name, String message) {
        if (this.alertView != null && name != null && message != null) {
            if (this.alertView.getTag() != null) {
                this.alertView.setTag(null);
                if (this.alertViewAnimator != null) {
                    this.alertViewAnimator.cancel();
                    this.alertViewAnimator = null;
                }
                this.alertView.setVisibility(0);
                this.alertViewAnimator = new AnimatorSet();
                AnimatorSet animatorSet = this.alertViewAnimator;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.alertView, "translationY", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.alertViewAnimator.setDuration(200);
                this.alertViewAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }
                });
                this.alertViewAnimator.start();
            }
            this.alertNameTextView.setText(name);
            this.alertTextView.setText(Emoji.replaceEmoji(message.replace('\n', ' '), this.alertTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            if (this.hideAlertViewRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.hideAlertViewRunnable);
            }
            Runnable anonymousClass73 = new Runnable() {
                public void run() {
                    if (ChatActivity.this.hideAlertViewRunnable == this && ChatActivity.this.alertView.getTag() == null) {
                        ChatActivity.this.alertView.setTag(Integer.valueOf(1));
                        if (ChatActivity.this.alertViewAnimator != null) {
                            ChatActivity.this.alertViewAnimator.cancel();
                            ChatActivity.this.alertViewAnimator = null;
                        }
                        ChatActivity.this.alertViewAnimator = new AnimatorSet();
                        AnimatorSet access$14500 = ChatActivity.this.alertViewAnimator;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.alertView, "translationY", new float[]{(float) (-AndroidUtilities.dp(50.0f))});
                        access$14500.playTogether(animatorArr);
                        ChatActivity.this.alertViewAnimator.setDuration(200);
                        ChatActivity.this.alertViewAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                                    ChatActivity.this.alertView.setVisibility(8);
                                    ChatActivity.this.alertViewAnimator = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                                    ChatActivity.this.alertViewAnimator = null;
                                }
                            }
                        });
                        ChatActivity.this.alertViewAnimator.start();
                    }
                }
            };
            this.hideAlertViewRunnable = anonymousClass73;
            AndroidUtilities.runOnUIThread(anonymousClass73, 3000);
        }
    }

    private void hidePinnedMessageView(boolean animated) {
        if (this.pinnedMessageView.getTag() == null) {
            this.pinnedMessageView.setTag(Integer.valueOf(1));
            if (this.pinnedMessageViewAnimator != null) {
                this.pinnedMessageViewAnimator.cancel();
                this.pinnedMessageViewAnimator = null;
            }
            if (animated) {
                this.pinnedMessageViewAnimator = new AnimatorSet();
                AnimatorSet animatorSet = this.pinnedMessageViewAnimator;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", new float[]{(float) (-AndroidUtilities.dp(50.0f))});
                animatorSet.playTogether(animatorArr);
                this.pinnedMessageViewAnimator.setDuration(200);
                this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                            ChatActivity.this.pinnedMessageView.setVisibility(8);
                            ChatActivity.this.pinnedMessageViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                            ChatActivity.this.pinnedMessageViewAnimator = null;
                        }
                    }
                });
                this.pinnedMessageViewAnimator.start();
                return;
            }
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
        }
    }

    private void updatePinnedMessageView(boolean animated) {
        if (this.pinnedMessageView != null) {
            if (this.info != null) {
                if (!(this.pinnedMessageObject == null || this.info.pinned_msg_id == this.pinnedMessageObject.getId())) {
                    this.pinnedMessageObject = null;
                }
                if (this.info.pinned_msg_id != 0 && this.pinnedMessageObject == null) {
                    this.pinnedMessageObject = (MessageObject) this.messagesDict[0].get(Integer.valueOf(this.info.pinned_msg_id));
                }
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            if (this.info == null || this.info.pinned_msg_id == 0 || this.info.pinned_msg_id == preferences.getInt("pin_" + this.dialog_id, 0) || (this.actionBar != null && this.actionBar.isActionModeShowed())) {
                hidePinnedMessageView(animated);
            } else if (this.pinnedMessageObject != null) {
                if (this.pinnedMessageView.getTag() != null) {
                    this.pinnedMessageView.setTag(null);
                    if (this.pinnedMessageViewAnimator != null) {
                        this.pinnedMessageViewAnimator.cancel();
                        this.pinnedMessageViewAnimator = null;
                    }
                    if (animated) {
                        this.pinnedMessageView.setVisibility(0);
                        this.pinnedMessageViewAnimator = new AnimatorSet();
                        AnimatorSet animatorSet = this.pinnedMessageViewAnimator;
                        Animator[] animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                        this.pinnedMessageViewAnimator.setDuration(200);
                        this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                                    ChatActivity.this.pinnedMessageViewAnimator = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                                    ChatActivity.this.pinnedMessageViewAnimator = null;
                                }
                            }
                        });
                        this.pinnedMessageViewAnimator.start();
                    } else {
                        this.pinnedMessageView.setTranslationY(0.0f);
                        this.pinnedMessageView.setVisibility(0);
                    }
                }
                FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.pinnedMessageNameTextView.getLayoutParams();
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.pinnedMessageTextView.getLayoutParams();
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs2, AndroidUtilities.dp(50.0f));
                if (photoSize == null) {
                    photoSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs, AndroidUtilities.dp(50.0f));
                }
                int dp;
                if (photoSize == null || (photoSize instanceof TL_photoSizeEmpty) || (photoSize.location instanceof TL_fileLocationUnavailable) || this.pinnedMessageObject.type == 13) {
                    this.pinnedMessageImageView.setImageBitmap(null);
                    this.pinnedImageLocation = null;
                    this.pinnedMessageImageView.setVisibility(4);
                    dp = AndroidUtilities.dp(18.0f);
                    layoutParams2.leftMargin = dp;
                    layoutParams1.leftMargin = dp;
                } else {
                    this.pinnedImageLocation = photoSize.location;
                    this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable) null);
                    this.pinnedMessageImageView.setVisibility(0);
                    dp = AndroidUtilities.dp(55.0f);
                    layoutParams2.leftMargin = dp;
                    layoutParams1.leftMargin = dp;
                }
                this.pinnedMessageNameTextView.setLayoutParams(layoutParams1);
                this.pinnedMessageTextView.setLayoutParams(layoutParams2);
                this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedMessage", R.string.PinnedMessage));
                if (this.pinnedMessageObject.type == 14) {
                    this.pinnedMessageTextView.setText(String.format("%s - %s", new Object[]{this.pinnedMessageObject.getMusicAuthor(), this.pinnedMessageObject.getMusicTitle()}));
                } else if (this.pinnedMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(this.pinnedMessageObject.messageOwner.media.game.title, this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                } else if (this.pinnedMessageObject.messageText != null) {
                    String mess = this.pinnedMessageObject.messageText.toString();
                    if (mess.length() > 150) {
                        mess = mess.substring(0, 150);
                    }
                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(mess.replace('\n', ' '), this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                }
            } else {
                this.pinnedImageLocation = null;
                hidePinnedMessageView(animated);
                if (this.loadingPinnedMessage != this.info.pinned_msg_id) {
                    this.loadingPinnedMessage = this.info.pinned_msg_id;
                    MessagesQuery.loadPinnedMessage(this.currentChat.id, this.info.pinned_msg_id, true);
                }
            }
            checkListViewPaddings();
        }
    }

    private void updateSpamView() {
        if (this.reportSpamView != null) {
            boolean show;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            if (this.currentEncryptedChat != null) {
                if (this.currentEncryptedChat.admin_id == UserConfig.getClientUserId() || ContactsController.getInstance().isLoadingContacts() || ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null) {
                    show = false;
                } else {
                    show = true;
                }
                if (show && preferences.getInt("spam3_" + this.dialog_id, 0) == 1) {
                    show = false;
                }
            } else {
                show = preferences.getInt(new StringBuilder().append("spam3_").append(this.dialog_id).toString(), 0) == 2;
            }
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (show) {
                if (this.reportSpamView.getTag() != null) {
                    this.reportSpamView.setTag(null);
                    this.reportSpamView.setVisibility(0);
                    if (this.reportSpamViewAnimator != null) {
                        this.reportSpamViewAnimator.cancel();
                    }
                    this.reportSpamViewAnimator = new AnimatorSet();
                    animatorSet = this.reportSpamViewAnimator;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.reportSpamViewAnimator.setDuration(200);
                    this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }
                    });
                    this.reportSpamViewAnimator.start();
                }
            } else if (this.reportSpamView.getTag() == null) {
                this.reportSpamView.setTag(Integer.valueOf(1));
                if (this.reportSpamViewAnimator != null) {
                    this.reportSpamViewAnimator.cancel();
                }
                this.reportSpamViewAnimator = new AnimatorSet();
                animatorSet = this.reportSpamViewAnimator;
                animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", new float[]{(float) (-AndroidUtilities.dp(50.0f))});
                animatorSet.playTogether(animatorArr);
                this.reportSpamViewAnimator.setDuration(200);
                this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                            ChatActivity.this.reportSpamView.setVisibility(8);
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }
                });
                this.reportSpamViewAnimator.start();
            }
            checkListViewPaddings();
        }
    }

    private void updateContactStatus() {
        if (this.addContactItem != null) {
            if (this.currentUser == null) {
                this.addContactItem.setVisibility(8);
            } else {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                if (user != null) {
                    this.currentUser = user;
                }
                if ((this.currentEncryptedChat != null && !(this.currentEncryptedChat instanceof TL_encryptedChat)) || this.currentUser.id == 4244000 || this.currentUser.id / 1000 == 333 || this.currentUser.id / 1000 == 777 || UserObject.isDeleted(this.currentUser) || ContactsController.getInstance().isLoadingContacts() || (this.currentUser.phone != null && this.currentUser.phone.length() != 0 && ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null && (ContactsController.getInstance().contactsDict.size() != 0 || !ContactsController.getInstance().isLoadingContacts()))) {
                    this.addContactItem.setVisibility(8);
                } else {
                    this.addContactItem.setVisibility(0);
                    if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                        this.addContactItem.setText(LocaleController.getString("ShareMyContactInfo", R.string.ShareMyContactInfo));
                        this.addToContactsButton.setVisibility(8);
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0f), 0, AndroidUtilities.dp(50.0f), 0);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 1.0f, 51, 0, 0, 0, AndroidUtilities.dp(1.0f)));
                    } else {
                        this.addContactItem.setText(LocaleController.getString("AddToContacts", R.string.AddToContacts));
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(50.0f), 0);
                        this.addToContactsButton.setVisibility(0);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 0.5f, 51, 0, 0, 0, AndroidUtilities.dp(1.0f)));
                    }
                }
            }
            checkListViewPaddings();
        }
    }

    private void checkListViewPaddings() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = 0;
                try {
                    int firstVisPos = ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                    int top = 0;
                    if (firstVisPos != -1) {
                        View firstVisView = ChatActivity.this.chatLayoutManager.findViewByPosition(firstVisPos);
                        if (firstVisView != null) {
                            i = firstVisView.getTop();
                        }
                        top = i - ChatActivity.this.chatListView.getPaddingTop();
                    }
                    FrameLayout.LayoutParams layoutParams;
                    if (ChatActivity.this.chatListView.getPaddingTop() != AndroidUtilities.dp(52.0f) && ((ChatActivity.this.pinnedMessageView != null && ChatActivity.this.pinnedMessageView.getTag() == null) || (ChatActivity.this.reportSpamView != null && ChatActivity.this.reportSpamView.getTag() == null))) {
                        ChatActivity.this.chatListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(3.0f));
                        layoutParams = (FrameLayout.LayoutParams) ChatActivity.this.floatingDateView.getLayoutParams();
                        layoutParams.topMargin = AndroidUtilities.dp(52.0f);
                        ChatActivity.this.floatingDateView.setLayoutParams(layoutParams);
                        ChatActivity.this.chatListView.setTopGlowOffset(AndroidUtilities.dp(48.0f));
                        top -= AndroidUtilities.dp(48.0f);
                    } else if (ChatActivity.this.chatListView.getPaddingTop() == AndroidUtilities.dp(4.0f) || ((ChatActivity.this.pinnedMessageView != null && ChatActivity.this.pinnedMessageView.getTag() == null) || (ChatActivity.this.reportSpamView != null && ChatActivity.this.reportSpamView.getTag() == null))) {
                        firstVisPos = -1;
                    } else {
                        ChatActivity.this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
                        layoutParams = (FrameLayout.LayoutParams) ChatActivity.this.floatingDateView.getLayoutParams();
                        layoutParams.topMargin = AndroidUtilities.dp(4.0f);
                        ChatActivity.this.floatingDateView.setLayoutParams(layoutParams);
                        ChatActivity.this.chatListView.setTopGlowOffset(0);
                        top += AndroidUtilities.dp(48.0f);
                    }
                    if (firstVisPos != -1) {
                        ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(firstVisPos, top);
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
    }

    private void checkRaiseSensors() {
        if (ApplicationLoader.mainInterfacePaused || ((this.bottomOverlayChat != null && this.bottomOverlayChat.getVisibility() == 0) || ((this.bottomOverlay != null && this.bottomOverlay.getVisibility() == 0) || (this.searchContainer != null && this.searchContainer.getVisibility() == 0)))) {
            MediaController.getInstance().setAllowStartRecord(false);
        } else {
            MediaController.getInstance().setAllowStartRecord(true);
        }
    }

    public void dismissCurrentDialig() {
        if (this.chatAttachAlert == null || this.visibleDialog != this.chatAttachAlert) {
            super.dismissCurrentDialig();
            return;
        }
        this.chatAttachAlert.closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.hideCamera(true);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        MediaController.getInstance().startRaiseToEarSensors(this);
        checkRaiseSensors();
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onResume();
        }
        checkActionBarMenu();
        if (!(this.replyImageLocation == null || this.replyImageView == null)) {
            this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null);
        }
        if (!(this.pinnedImageLocation == null || this.pinnedMessageImageView == null)) {
            this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable) null);
        }
        NotificationsController.getInstance().setOpenedDialogId(this.dialog_id);
        if (this.scrollToTopOnResume) {
            if (!this.scrollToTopUnReadOnResume || this.scrollToMessage == null) {
                moveScrollToLastMessage();
            } else if (this.chatListView != null) {
                int yOffset;
                int paddingTop;
                if (this.scrollToMessagePosition == -9000) {
                    yOffset = Math.max(0, (this.chatListView.getHeight() - this.scrollToMessage.getApproximateHeight()) / 2);
                } else if (this.scrollToMessagePosition == -10000) {
                    yOffset = 0;
                } else {
                    yOffset = this.scrollToMessagePosition;
                }
                LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
                int size = this.messages.size() - this.messages.indexOf(this.scrollToMessage);
                int i = (-AndroidUtilities.dp(7.0f)) + yOffset;
                if (this.scrollToMessage == this.unreadMessageObject) {
                    paddingTop = this.chatListView.getPaddingTop();
                } else {
                    paddingTop = 0;
                }
                linearLayoutManager.scrollToPositionWithOffset(size, i - paddingTop);
            }
            this.scrollToTopUnReadOnResume = false;
            this.scrollToTopOnResume = false;
            this.scrollToMessage = null;
        }
        this.paused = false;
        if (this.readWhenResume && !this.messages.isEmpty()) {
            Iterator it = this.messages.iterator();
            while (it.hasNext()) {
                MessageObject messageObject = (MessageObject) it.next();
                if (!messageObject.isUnread() && !messageObject.isOut()) {
                    break;
                } else if (!messageObject.isOut()) {
                    messageObject.setIsRead();
                }
            }
            this.readWhenResume = false;
            MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject) this.messages.get(0)).getId(), this.readWithMid, this.readWithDate, true, false);
        }
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
        fixLayout();
        applyDraftMaybe(false);
        if (!(this.bottomOverlayChat == null || this.bottomOverlayChat.getVisibility() == 0)) {
            this.chatActivityEnterView.setFieldFocused(true);
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onResume();
        }
        if (this.currentEncryptedChat != null) {
            this.chatEnterTime = System.currentTimeMillis();
            this.chatLeaveTime = 0;
        }
        if (this.startVideoEdit != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ChatActivity.this.openVideoEditor(ChatActivity.this.startVideoEdit, false, false);
                    ChatActivity.this.startVideoEdit = null;
                }
            });
        }
        if (this.chatListView != null && (this.chatActivityEnterView == null || !this.chatActivityEnterView.isEditingMessage())) {
            this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
            this.chatListView.setOnItemClickListener(this.onItemClickListener);
            this.chatListView.setLongClickable(true);
        }
        checkBotCommands();
    }

    public void onPause() {
        boolean z;
        super.onPause();
        MediaController.getInstance().stopRaiseToEarSensors(this);
        if (this.chatAttachAlert != null) {
            if (this.ignoreAttachOnPause) {
                this.ignoreAttachOnPause = false;
            } else {
                this.chatAttachAlert.onPause();
            }
        }
        this.paused = true;
        this.wasPaused = true;
        NotificationsController.getInstance().setOpenedDialogId(0);
        CharSequence draftMessage = null;
        boolean searchWebpage = true;
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onPause();
            if (!this.chatActivityEnterView.isEditingMessage()) {
                CharSequence text = AndroidUtilities.getTrimmedString(this.chatActivityEnterView.getFieldText());
                if (!(TextUtils.isEmpty(text) || TextUtils.equals(text, "@gif"))) {
                    draftMessage = text;
                }
            }
            searchWebpage = this.chatActivityEnterView.isMessageWebPageSearchEnabled();
            this.chatActivityEnterView.setFieldFocused(false);
        }
        CharSequence[] message = new CharSequence[]{draftMessage};
        ArrayList<MessageEntity> entities = MessagesQuery.getEntities(message);
        long j = this.dialog_id;
        CharSequence charSequence = message[0];
        Message message2 = this.replyingMessageObject != null ? this.replyingMessageObject.messageOwner : null;
        if (searchWebpage) {
            z = false;
        } else {
            z = true;
        }
        DraftQuery.saveDraft(j, charSequence, entities, message2, z);
        MessagesController.getInstance().cancelTyping(0, this.dialog_id);
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
        int messageId = 0;
        int offset = 0;
        if (this.chatLayoutManager != null) {
            int position = this.chatLayoutManager.findLastVisibleItemPosition();
            if (position != this.chatLayoutManager.getItemCount() - 1) {
                Holder holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(position);
                if (holder != null) {
                    if (holder.itemView instanceof ChatMessageCell) {
                        messageId = ((ChatMessageCell) holder.itemView).getMessageObject().getId();
                    } else if (holder.itemView instanceof ChatActionCell) {
                        messageId = ((ChatActionCell) holder.itemView).getMessageObject().getId();
                    }
                    if (messageId != 0) {
                        offset = holder.itemView.getMeasuredHeight() - (holder.itemView.getBottom() - this.chatListView.getMeasuredHeight());
                    }
                }
            }
        }
        if (messageId != 0) {
            editor.putInt("diditem" + this.dialog_id, messageId);
            editor.putInt("diditemo" + this.dialog_id, offset);
        } else {
            editor.remove("diditem" + this.dialog_id);
            editor.remove("diditemo" + this.dialog_id);
        }
        editor.commit();
        if (this.currentEncryptedChat != null) {
            this.chatLeaveTime = System.currentTimeMillis();
            updateInformationForScreenshotDetector();
        }
    }

    private void applyDraftMaybe(boolean canClear) {
        if (this.chatActivityEnterView != null) {
            DraftMessage draftMessage = DraftQuery.getDraft(this.dialog_id);
            Message draftReplyMessage = (draftMessage == null || draftMessage.reply_to_msg_id == 0) ? null : DraftQuery.getDraftMessage(this.dialog_id);
            if (this.chatActivityEnterView.getFieldText() == null) {
                if (draftMessage != null) {
                    CharSequence message;
                    this.chatActivityEnterView.setWebPage(null, !draftMessage.no_webpage);
                    if (draftMessage.entities.isEmpty()) {
                        message = draftMessage.message;
                    } else {
                        SpannableStringBuilder stringBuilder = SpannableStringBuilder.valueOf(draftMessage.message);
                        MessagesQuery.sortEntities(draftMessage.entities);
                        int addToOffset = 0;
                        for (int a = 0; a < draftMessage.entities.size(); a++) {
                            MessageEntity entity = (MessageEntity) draftMessage.entities.get(a);
                            if ((entity instanceof TL_inputMessageEntityMentionName) || (entity instanceof TL_messageEntityMentionName)) {
                                int user_id;
                                if (entity instanceof TL_inputMessageEntityMentionName) {
                                    user_id = ((TL_inputMessageEntityMentionName) entity).user_id.user_id;
                                } else {
                                    user_id = ((TL_messageEntityMentionName) entity).user_id;
                                }
                                if ((entity.offset + addToOffset) + entity.length < stringBuilder.length() && stringBuilder.charAt((entity.offset + addToOffset) + entity.length) == ' ') {
                                    entity.length++;
                                }
                                stringBuilder.setSpan(new URLSpanUserMention("" + user_id, true), entity.offset + addToOffset, (entity.offset + addToOffset) + entity.length, 33);
                            } else if (entity instanceof TL_messageEntityCode) {
                                stringBuilder.insert((entity.offset + entity.length) + addToOffset, "`");
                                stringBuilder.insert(entity.offset + addToOffset, "`");
                                addToOffset += 2;
                            } else if (entity instanceof TL_messageEntityPre) {
                                stringBuilder.insert((entity.offset + entity.length) + addToOffset, "```");
                                stringBuilder.insert(entity.offset + addToOffset, "```");
                                addToOffset += 6;
                            } else if (entity instanceof TL_messageEntityBold) {
                                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), entity.offset + addToOffset, (entity.offset + entity.length) + addToOffset, 33);
                            } else if (entity instanceof TL_messageEntityItalic) {
                                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), entity.offset + addToOffset, (entity.offset + entity.length) + addToOffset, 33);
                            }
                        }
                        message = stringBuilder;
                    }
                    this.chatActivityEnterView.setFieldText(message);
                    if (getArguments().getBoolean("hasUrl", false)) {
                        this.chatActivityEnterView.setSelection(draftMessage.message.indexOf(10) + 1);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ChatActivity.this.chatActivityEnterView != null) {
                                    ChatActivity.this.chatActivityEnterView.setFieldFocused(true);
                                    ChatActivity.this.chatActivityEnterView.openKeyboard();
                                }
                            }
                        }, 700);
                    }
                }
            } else if (canClear && draftMessage == null) {
                this.chatActivityEnterView.setFieldText("");
                showReplyPanel(false, null, null, null, false);
            }
            if (this.replyingMessageObject == null && draftReplyMessage != null) {
                this.replyingMessageObject = new MessageObject(draftReplyMessage, MessagesController.getInstance().getUsers(), false);
                showReplyPanel(true, this.replyingMessageObject, null, null, false);
            }
        }
    }

    private void updateInformationForScreenshotDetector() {
        if (this.currentEncryptedChat != null) {
            ArrayList<Long> visibleMessages = new ArrayList();
            if (this.chatListView != null) {
                int count = this.chatListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = this.chatListView.getChildAt(a);
                    MessageObject object = null;
                    if (view instanceof ChatMessageCell) {
                        object = ((ChatMessageCell) view).getMessageObject();
                    }
                    if (!(object == null || object.getId() >= 0 || object.messageOwner.random_id == 0)) {
                        visibleMessages.add(Long.valueOf(object.messageOwner.random_id));
                    }
                }
            }
            MediaController.getInstance().setLastEncryptedChatParams(this.chatEnterTime, this.chatLeaveTime, this.currentEncryptedChat, visibleMessages);
        }
    }

    private boolean fixLayoutInternal() {
        boolean z = true;
        if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
            this.selectedMessagesCountTextView.setTextSize(20);
        } else {
            this.selectedMessagesCountTextView.setTextSize(18);
        }
        if (!AndroidUtilities.isTablet()) {
            return true;
        }
        if (AndroidUtilities.isSmallTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 1) {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
            if (this.playerView == null || this.playerView.getParent() != null) {
                return false;
            }
            ((ViewGroup) this.fragmentView).addView(this.playerView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            return false;
        }
        ActionBar actionBar = this.actionBar;
        if (!(this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(0) == this || this.parentLayout.fragmentsStack.size() == 1)) {
            z = false;
        }
        actionBar.setBackButtonDrawable(new BackDrawable(z));
        if (this.playerView == null || this.playerView.getParent() == null) {
            return false;
        }
        this.fragmentView.setPadding(0, 0, 0, 0);
        ((ViewGroup) this.fragmentView).removeView(this.playerView);
        return false;
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ChatActivity.this.avatarContainer != null) {
                        ChatActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return ChatActivity.this.fixLayoutInternal();
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        fixLayout();
        if (this.visibleDialog instanceof DatePickerDialog) {
            this.visibleDialog.dismiss();
        }
    }

    private void createDeleteMessagesAlert(MessageObject finalSelectedObject) {
        int i;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        String str = "AreYouSureDeleteMessages";
        Object[] objArr = new Object[1];
        String str2 = "messages";
        if (finalSelectedObject != null) {
            i = 1;
        } else {
            i = this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size();
        }
        objArr[0] = LocaleController.formatPluralString(str2, i);
        builder.setMessage(LocaleController.formatString(str, R.string.AreYouSureDeleteMessages, objArr));
        builder.setTitle(LocaleController.getString("Message", R.string.Message));
        final boolean[] checks = new boolean[3];
        final boolean[] deleteForAll = new boolean[1];
        User user = null;
        boolean z;
        int currentDate;
        int a;
        MessageObject msg;
        boolean exit;
        View frameLayout;
        CheckBoxCell cell;
        if (this.currentChat != null && this.currentChat.megagroup) {
            z = false;
            currentDate = ConnectionsManager.getInstance().getCurrentTime();
            if (finalSelectedObject != null) {
                if (finalSelectedObject.messageOwner.action == null || (finalSelectedObject.messageOwner.action instanceof TL_messageActionEmpty)) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(finalSelectedObject.messageOwner.from_id));
                }
                if (finalSelectedObject.getDialogId() == this.mergeDialogId && ((finalSelectedObject.messageOwner.action == null || (finalSelectedObject.messageOwner.action instanceof TL_messageActionEmpty)) && finalSelectedObject.isOut() && currentDate - finalSelectedObject.messageOwner.date <= 172800)) {
                    z = true;
                } else {
                    z = false;
                }
            } else {
                int from_id = -1;
                for (a = 1; a >= 0; a--) {
                    for (Entry<Integer, MessageObject> entry : this.selectedMessagesIds[a].entrySet()) {
                        msg = (MessageObject) entry.getValue();
                        if (from_id == -1) {
                            from_id = msg.messageOwner.from_id;
                        }
                        if (from_id >= 0) {
                            if (from_id != msg.messageOwner.from_id) {
                            }
                        }
                        from_id = -2;
                    }
                    if (from_id == -2) {
                        break;
                    }
                }
                exit = false;
                for (a = 1; a >= 0; a--) {
                    for (Entry<Integer, MessageObject> entry2 : this.selectedMessagesIds[a].entrySet()) {
                        msg = (MessageObject) entry2.getValue();
                        if (a != 1) {
                            if (a == 0 && !msg.isOut()) {
                                z = false;
                                exit = true;
                                break;
                            }
                        } else if (!msg.isOut() || msg.messageOwner.action != null) {
                            z = false;
                            exit = true;
                            break;
                        } else if (currentDate - msg.messageOwner.date <= 172800) {
                            z = true;
                        }
                    }
                    if (exit) {
                        break;
                    }
                }
                if (from_id != -1) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(from_id));
                }
            }
            int dp;
            if (user != null && user.id != UserConfig.getClientUserId()) {
                frameLayout = new FrameLayout(getParentActivity());
                if (VERSION.SDK_INT >= 21) {
                    frameLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                }
                for (a = 0; a < 3; a++) {
                    cell = new CheckBoxCell(getParentActivity(), true);
                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cell.setTag(Integer.valueOf(a));
                    if (a == 0) {
                        cell.setText(LocaleController.getString("DeleteBanUser", R.string.DeleteBanUser), "", false, false);
                    } else if (a == 1) {
                        cell.setText(LocaleController.getString("DeleteReportSpam", R.string.DeleteReportSpam), "", false, false);
                    } else if (a == 2) {
                        cell.setText(LocaleController.formatString("DeleteAllFrom", R.string.DeleteAllFrom, ContactsController.formatName(user.first_name, user.last_name)), "", false, false);
                    }
                    if (LocaleController.isRTL) {
                        i = AndroidUtilities.dp(16.0f);
                    } else {
                        i = AndroidUtilities.dp(8.0f);
                    }
                    if (LocaleController.isRTL) {
                        dp = AndroidUtilities.dp(8.0f);
                    } else {
                        dp = AndroidUtilities.dp(16.0f);
                    }
                    cell.setPadding(i, 0, dp, 0);
                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) (a * 48), 0.0f, 0.0f));
                    cell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CheckBoxCell cell = (CheckBoxCell) v;
                            Integer num = (Integer) cell.getTag();
                            checks[num.intValue()] = !checks[num.intValue()];
                            cell.setChecked(checks[num.intValue()], true);
                        }
                    });
                }
                builder.setView(frameLayout);
            } else if (z) {
                frameLayout = new FrameLayout(getParentActivity());
                if (VERSION.SDK_INT >= 21) {
                    frameLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                }
                cell = new CheckBoxCell(getParentActivity(), true);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                if (this.currentChat != null) {
                    cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), "", false, false);
                } else {
                    cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(this.currentUser)), "", false, false);
                }
                if (LocaleController.isRTL) {
                    i = AndroidUtilities.dp(16.0f);
                } else {
                    i = AndroidUtilities.dp(8.0f);
                }
                if (LocaleController.isRTL) {
                    dp = AndroidUtilities.dp(8.0f);
                } else {
                    dp = AndroidUtilities.dp(16.0f);
                }
                cell.setPadding(i, 0, dp, 0);
                frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                cell.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        boolean z;
                        CheckBoxCell cell = (CheckBoxCell) v;
                        boolean[] zArr = deleteForAll;
                        if (deleteForAll[0]) {
                            z = false;
                        } else {
                            z = true;
                        }
                        zArr[0] = z;
                        cell.setChecked(deleteForAll[0], true);
                    }
                });
                builder.setView(frameLayout);
            } else {
                user = null;
            }
        } else if (!ChatObject.isChannel(this.currentChat) && this.currentEncryptedChat == null) {
            z = false;
            currentDate = ConnectionsManager.getInstance().getCurrentTime();
            if ((this.currentUser != null && this.currentUser.id != UserConfig.getClientUserId()) || this.currentChat != null) {
                if (finalSelectedObject == null) {
                    exit = false;
                    for (a = 1; a >= 0; a--) {
                        for (Entry<Integer, MessageObject> entry22 : this.selectedMessagesIds[a].entrySet()) {
                            msg = (MessageObject) entry22.getValue();
                            if (msg.messageOwner.action == null) {
                                if (!msg.isOut()) {
                                    z = false;
                                    exit = true;
                                    break;
                                } else if (currentDate - msg.messageOwner.date <= 172800) {
                                    z = true;
                                }
                            }
                        }
                        if (exit) {
                            break;
                        }
                    }
                } else if ((finalSelectedObject.messageOwner.action == null || (finalSelectedObject.messageOwner.action instanceof TL_messageActionEmpty)) && finalSelectedObject.isOut() && currentDate - finalSelectedObject.messageOwner.date <= 172800) {
                    z = true;
                } else {
                    z = false;
                }
            }
            if (z) {
                frameLayout = new FrameLayout(getParentActivity());
                if (VERSION.SDK_INT >= 21) {
                    frameLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                }
                cell = new CheckBoxCell(getParentActivity(), true);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                if (this.currentChat != null) {
                    cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), "", false, false);
                } else {
                    cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(this.currentUser)), "", false, false);
                }
                cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                cell.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        boolean z;
                        CheckBoxCell cell = (CheckBoxCell) v;
                        boolean[] zArr = deleteForAll;
                        if (deleteForAll[0]) {
                            z = false;
                        } else {
                            z = true;
                        }
                        zArr[0] = z;
                        cell.setChecked(deleteForAll[0], true);
                    }
                });
                builder.setView(frameLayout);
            }
        }
        final User userFinal = user;
        final MessageObject messageObject = finalSelectedObject;
        final boolean[] zArr = deleteForAll;
        final boolean[] zArr2 = checks;
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<Integer> ids = null;
                ArrayList<Long> random_ids;
                if (messageObject != null) {
                    ids = new ArrayList();
                    ids.add(Integer.valueOf(messageObject.getId()));
                    random_ids = null;
                    if (!(ChatActivity.this.currentEncryptedChat == null || messageObject.messageOwner.random_id == 0 || messageObject.type == 10)) {
                        random_ids = new ArrayList();
                        random_ids.add(Long.valueOf(messageObject.messageOwner.random_id));
                    }
                    MessagesController.getInstance().deleteMessages(ids, random_ids, ChatActivity.this.currentEncryptedChat, messageObject.messageOwner.to_id.channel_id, zArr[0]);
                } else {
                    for (int a = 1; a >= 0; a--) {
                        MessageObject msg;
                        ids = new ArrayList(ChatActivity.this.selectedMessagesIds[a].keySet());
                        random_ids = null;
                        int channelId = 0;
                        if (!ids.isEmpty()) {
                            msg = (MessageObject) ChatActivity.this.selectedMessagesIds[a].get(ids.get(0));
                            if (null == null && msg.messageOwner.to_id.channel_id != 0) {
                                channelId = msg.messageOwner.to_id.channel_id;
                            }
                        }
                        if (ChatActivity.this.currentEncryptedChat != null) {
                            random_ids = new ArrayList();
                            for (Entry<Integer, MessageObject> entry : ChatActivity.this.selectedMessagesIds[a].entrySet()) {
                                msg = (MessageObject) entry.getValue();
                                if (!(msg.messageOwner.random_id == 0 || msg.type == 10)) {
                                    random_ids.add(Long.valueOf(msg.messageOwner.random_id));
                                }
                            }
                        }
                        MessagesController.getInstance().deleteMessages(ids, random_ids, ChatActivity.this.currentEncryptedChat, channelId, zArr[0]);
                    }
                    ChatActivity.this.actionBar.hideActionMode();
                    ChatActivity.this.updatePinnedMessageView(true);
                }
                if (userFinal != null) {
                    if (zArr2[0]) {
                        MessagesController.getInstance().deleteUserFromChat(ChatActivity.this.currentChat.id, userFinal, ChatActivity.this.info);
                    }
                    if (zArr2[1]) {
                        TL_channels_reportSpam req = new TL_channels_reportSpam();
                        req.channel = MessagesController.getInputChannel(ChatActivity.this.currentChat);
                        req.user_id = MessagesController.getInputUser(userFinal);
                        req.id = ids;
                        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                            }
                        });
                    }
                    if (zArr2[2]) {
                        MessagesController.getInstance().deleteUserChannelHistory(ChatActivity.this.currentChat, userFinal, 0);
                    }
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void createMenu(View v, boolean single) {
        if (!this.actionBar.isActionModeShowed()) {
            MessageObject message = null;
            if (v instanceof ChatMessageCell) {
                message = ((ChatMessageCell) v).getMessageObject();
            } else if (v instanceof ChatActionCell) {
                message = ((ChatActionCell) v).getMessageObject();
            }
            if (message != null) {
                int type = getMessageType(message);
                if (single && (message.messageOwner.action instanceof TL_messageActionPinMessage)) {
                    scrollToMessageId(message.messageOwner.reply_to_msg_id, message.messageOwner.id, true, 0);
                    return;
                }
                int a;
                this.selectedObject = null;
                this.forwaringMessage = null;
                for (a = 1; a >= 0; a--) {
                    this.selectedMessagesCanCopyIds[a].clear();
                    this.selectedMessagesIds[a].clear();
                }
                this.cantDeleteMessagesCount = 0;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                boolean allowChatActions = true;
                boolean allowPin = message.getDialogId() != this.mergeDialogId && message.getId() > 0 && ChatObject.isChannel(this.currentChat) && this.currentChat.megagroup && ((this.currentChat.creator || this.currentChat.editor) && (message.messageOwner.action == null || (message.messageOwner.action instanceof TL_messageActionEmpty)));
                boolean allowUnpin = message.getDialogId() != this.mergeDialogId && this.info != null && this.info.pinned_msg_id == message.getId() && (this.currentChat.creator || this.currentChat.editor);
                boolean allowEdit = (!message.canEditMessage(this.currentChat) || this.chatActivityEnterView.hasAudioToSend() || message.getDialogId() == this.mergeDialogId) ? false : true;
                if ((this.currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46) || ((type == 1 && message.getDialogId() == this.mergeDialogId) || ((this.currentEncryptedChat == null && message.getId() < 0) || this.isBroadcast || (this.currentChat != null && (ChatObject.isNotInChat(this.currentChat) || !(!ChatObject.isChannel(this.currentChat) || this.currentChat.creator || this.currentChat.editor || this.currentChat.megagroup)))))) {
                    allowChatActions = false;
                }
                if (!single && type >= 2 && type != 20) {
                    ActionBarMenu actionMode = this.actionBar.createActionMode();
                    View item = actionMode.getItem(11);
                    if (item != null) {
                        item.setVisibility(0);
                    }
                    item = actionMode.getItem(12);
                    if (item != null) {
                        item.setVisibility(0);
                    }
                    this.actionBar.showActionMode();
                    updatePinnedMessageView(true);
                    AnimatorSet animatorSet = new AnimatorSet();
                    ArrayList<Animator> animators = new ArrayList();
                    for (a = 0; a < this.actionModeViews.size(); a++) {
                        View view = (View) this.actionModeViews.get(a);
                        AndroidUtilities.clearDrawableAnimation(view);
                        float[] fArr = new float[2];
                        animators.add(ObjectAnimator.ofFloat(view, "scaleY", new float[]{0.1f, 1.0f}));
                    }
                    animatorSet.playTogether(animators);
                    animatorSet.setDuration(250);
                    animatorSet.start();
                    addToSelectedMessages(message);
                    this.selectedMessagesCountTextView.setNumber(1, false);
                    updateVisibleRows();
                } else if (type >= 0) {
                    this.selectedObject = message;
                    if (getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        ArrayList<CharSequence> items = new ArrayList();
                        ArrayList<Integer> options = new ArrayList();
                        if (type == 0) {
                            items.add(LocaleController.getString("Retry", R.string.Retry));
                            options.add(Integer.valueOf(0));
                            items.add(LocaleController.getString("Delete", R.string.Delete));
                            options.add(Integer.valueOf(1));
                        } else if (type == 1) {
                            if (this.currentChat == null || this.isBroadcast) {
                                if (message.messageOwner.action != null && (message.messageOwner.action instanceof TL_messageActionPhoneCall)) {
                                    String string;
                                    if (message.messageOwner.action.reason instanceof TL_phoneCallDiscardReasonMissed) {
                                        string = LocaleController.getString("CallBack", R.string.CallBack);
                                    } else {
                                        string = LocaleController.getString("CallAgain", R.string.CallAgain);
                                    }
                                    items.add(string);
                                    options.add(Integer.valueOf(18));
                                }
                                if (single && this.selectedObject.getId() > 0 && allowChatActions) {
                                    items.add(LocaleController.getString("Reply", R.string.Reply));
                                    options.add(Integer.valueOf(8));
                                }
                                if (message.canDeleteMessage(this.currentChat)) {
                                    items.add(LocaleController.getString("Delete", R.string.Delete));
                                    options.add(Integer.valueOf(1));
                                }
                            } else {
                                if (allowChatActions) {
                                    items.add(LocaleController.getString("Reply", R.string.Reply));
                                    options.add(Integer.valueOf(8));
                                }
                                if (allowUnpin) {
                                    items.add(LocaleController.getString("UnpinMessage", R.string.UnpinMessage));
                                    options.add(Integer.valueOf(14));
                                } else if (allowPin) {
                                    items.add(LocaleController.getString("PinMessage", R.string.PinMessage));
                                    options.add(Integer.valueOf(13));
                                }
                                if (allowEdit) {
                                    items.add(LocaleController.getString("Edit", R.string.Edit));
                                    options.add(Integer.valueOf(12));
                                }
                                if (message.canDeleteMessage(this.currentChat)) {
                                    items.add(LocaleController.getString("Delete", R.string.Delete));
                                    options.add(Integer.valueOf(1));
                                }
                            }
                        } else if (type == 20) {
                            items.add(LocaleController.getString("Retry", R.string.Retry));
                            options.add(Integer.valueOf(0));
                            items.add(LocaleController.getString("Copy", R.string.Copy));
                            options.add(Integer.valueOf(3));
                            items.add(LocaleController.getString("Delete", R.string.Delete));
                            options.add(Integer.valueOf(1));
                        } else if (this.currentEncryptedChat == null) {
                            if (allowChatActions) {
                                items.add(LocaleController.getString("Reply", R.string.Reply));
                                options.add(Integer.valueOf(8));
                            }
                            if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                                items.add(LocaleController.getString("Copy", R.string.Copy));
                                options.add(Integer.valueOf(3));
                            }
                            if (type == 3) {
                                if ((this.selectedObject.messageOwner.media instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                                    items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                                    options.add(Integer.valueOf(11));
                                }
                            } else if (type == 4) {
                                if (this.selectedObject.isVideo()) {
                                    items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                    options.add(Integer.valueOf(4));
                                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                    options.add(Integer.valueOf(6));
                                } else if (this.selectedObject.isMusic()) {
                                    items.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                                    options.add(Integer.valueOf(10));
                                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                    options.add(Integer.valueOf(6));
                                } else if (this.selectedObject.getDocument() != null) {
                                    if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                                        items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                                        options.add(Integer.valueOf(11));
                                    }
                                    items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                                    options.add(Integer.valueOf(10));
                                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                    options.add(Integer.valueOf(6));
                                } else {
                                    items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                    options.add(Integer.valueOf(4));
                                }
                            } else if (type == 5) {
                                items.add(LocaleController.getString("ApplyLocalizationFile", R.string.ApplyLocalizationFile));
                                options.add(Integer.valueOf(5));
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                options.add(Integer.valueOf(6));
                            } else if (type == 10) {
                                items.add(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
                                options.add(Integer.valueOf(5));
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                options.add(Integer.valueOf(6));
                            } else if (type == 6) {
                                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                options.add(Integer.valueOf(7));
                                items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                                options.add(Integer.valueOf(10));
                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                options.add(Integer.valueOf(6));
                            } else if (type == 7) {
                                if (this.selectedObject.isMask()) {
                                    items.add(LocaleController.getString("AddToMasks", R.string.AddToMasks));
                                } else {
                                    items.add(LocaleController.getString("AddToStickers", R.string.AddToStickers));
                                }
                                options.add(Integer.valueOf(9));
                            } else if (type == 8) {
                                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                                if (!(user == null || user.id == UserConfig.getClientUserId() || ContactsController.getInstance().contactsDict.get(user.id) != null)) {
                                    items.add(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                                    options.add(Integer.valueOf(15));
                                }
                                if (!(this.selectedObject.messageOwner.media.phone_number == null && this.selectedObject.messageOwner.media.phone_number.length() == 0)) {
                                    items.add(LocaleController.getString("Copy", R.string.Copy));
                                    options.add(Integer.valueOf(16));
                                    items.add(LocaleController.getString("Call", R.string.Call));
                                    options.add(Integer.valueOf(17));
                                }
                            }
                            items.add(LocaleController.getString("Forward", R.string.Forward));
                            options.add(Integer.valueOf(2));
                            if (allowUnpin) {
                                items.add(LocaleController.getString("UnpinMessage", R.string.UnpinMessage));
                                options.add(Integer.valueOf(14));
                            } else if (allowPin) {
                                items.add(LocaleController.getString("PinMessage", R.string.PinMessage));
                                options.add(Integer.valueOf(13));
                            }
                            if (allowEdit) {
                                items.add(LocaleController.getString("Edit", R.string.Edit));
                                options.add(Integer.valueOf(12));
                            }
                            if (message.canDeleteMessage(this.currentChat)) {
                                items.add(LocaleController.getString("Delete", R.string.Delete));
                                options.add(Integer.valueOf(1));
                            }
                        } else {
                            if (allowChatActions) {
                                items.add(LocaleController.getString("Reply", R.string.Reply));
                                options.add(Integer.valueOf(8));
                            }
                            if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                                items.add(LocaleController.getString("Copy", R.string.Copy));
                                options.add(Integer.valueOf(3));
                            }
                            if (type == 4) {
                                if (this.selectedObject.isVideo()) {
                                    items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                    options.add(Integer.valueOf(4));
                                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                    options.add(Integer.valueOf(6));
                                } else if (this.selectedObject.isMusic()) {
                                    items.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                                    options.add(Integer.valueOf(10));
                                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                    options.add(Integer.valueOf(6));
                                } else if (this.selectedObject.isVideo() || this.selectedObject.getDocument() == null) {
                                    items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                    options.add(Integer.valueOf(4));
                                } else {
                                    items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                                    options.add(Integer.valueOf(10));
                                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                    options.add(Integer.valueOf(6));
                                }
                            } else if (type == 5) {
                                items.add(LocaleController.getString("ApplyLocalizationFile", R.string.ApplyLocalizationFile));
                                options.add(Integer.valueOf(5));
                            } else if (type == 10) {
                                items.add(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
                                options.add(Integer.valueOf(5));
                            } else if (type == 7) {
                                items.add(LocaleController.getString("AddToStickers", R.string.AddToStickers));
                                options.add(Integer.valueOf(9));
                            }
                            items.add(LocaleController.getString("Delete", R.string.Delete));
                            options.add(Integer.valueOf(1));
                        }
                        if (!options.isEmpty()) {
                            final ArrayList<Integer> arrayList = options;
                            builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (ChatActivity.this.selectedObject != null && i >= 0 && i < arrayList.size()) {
                                        ChatActivity.this.processSelectedOption(((Integer) arrayList.get(i)).intValue());
                                    }
                                }
                            });
                            builder.setTitle(LocaleController.getString("Message", R.string.Message));
                            showDialog(builder.create());
                        }
                    }
                }
            }
        }
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        String str = "";
        if (name && previousUid != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (chat != null) {
                    str = chat.title + ":\n";
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        }
        if (messageObject.messageOwner.media == null || messageObject.messageOwner.media.caption == null) {
            return str + messageObject.messageText;
        }
        return str + messageObject.messageOwner.media.caption;
    }

    private void processSelectedOption(int option) {
        if (this.selectedObject != null) {
            Bundle args;
            String path;
            AlertDialog.Builder builder;
            Intent intent;
            switch (option) {
                case 0:
                    if (SendMessagesHelper.getInstance().retrySendMessage(this.selectedObject, false)) {
                        moveScrollToLastMessage();
                        break;
                    }
                    break;
                case 1:
                    if (getParentActivity() != null) {
                        createDeleteMessagesAlert(this.selectedObject);
                        break;
                    } else {
                        this.selectedObject = null;
                        return;
                    }
                case 2:
                    this.forwaringMessage = this.selectedObject;
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    BaseFragment dialogsActivity = new DialogsActivity(args);
                    dialogsActivity.setDelegate(this);
                    presentFragment(dialogsActivity);
                    break;
                case 3:
                    AndroidUtilities.addToClipboard(getMessageContent(this.selectedObject, 0, false));
                    break;
                case 4:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (this.selectedObject.type == 3 || this.selectedObject.type == 1) {
                        if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            MediaController.saveFile(path, getParentActivity(), this.selectedObject.type == 3 ? 1 : 0, null, null);
                            break;
                        }
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                    break;
                case 5:
                    File locFile = null;
                    if (!(this.selectedObject.messageOwner.attachPath == null || this.selectedObject.messageOwner.attachPath.length() == 0)) {
                        File file = new File(this.selectedObject.messageOwner.attachPath);
                        if (file.exists()) {
                            locFile = file;
                        }
                    }
                    if (locFile == null) {
                        File f = FileLoader.getPathToMessage(this.selectedObject.messageOwner);
                        if (f.exists()) {
                            locFile = f;
                        }
                    }
                    if (locFile != null) {
                        if (!locFile.getName().endsWith("attheme")) {
                            if (!LocaleController.getInstance().applyLanguageFile(locFile)) {
                                if (getParentActivity() != null) {
                                    builder = new AlertDialog.Builder(getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setMessage(LocaleController.getString("IncorrectLocalization", R.string.IncorrectLocalization));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                    showDialog(builder.create());
                                    break;
                                }
                                this.selectedObject = null;
                                return;
                            }
                            presentFragment(new LanguageSelectActivity());
                            break;
                        }
                        ThemeInfo themeInfo = Theme.applyThemeFile(locFile, this.selectedObject.getDocumentName(), true);
                        if (themeInfo == null) {
                            if (getParentActivity() != null) {
                                builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("IncorrectTheme", R.string.IncorrectTheme));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                showDialog(builder.create());
                                break;
                            }
                            this.selectedObject = null;
                            return;
                        }
                        presentFragment(new ThemePreviewActivity(locFile, themeInfo));
                        break;
                    }
                    break;
                case 6:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    intent = new Intent("android.intent.action.SEND");
                    intent.setType(this.selectedObject.getDocument().mime_type);
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                    break;
                case 7:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        MediaController.saveFile(path, getParentActivity(), 0, null, null);
                        break;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                case 8:
                    showReplyPanel(true, this.selectedObject, null, null, false);
                    break;
                case 9:
                    showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), null, this.bottomOverlayChat.getVisibility() != 0 ? this.chatActivityEnterView : null));
                    break;
                case 10:
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        String fileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                        if (TextUtils.isEmpty(fileName)) {
                            fileName = this.selectedObject.getFileName();
                        }
                        path = this.selectedObject.messageOwner.attachPath;
                        if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                            path = null;
                        }
                        if (path == null || path.length() == 0) {
                            path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                        }
                        MediaController.saveFile(path, getParentActivity(), this.selectedObject.isMusic() ? 3 : 2, fileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : "");
                        break;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                case 11:
                    Document document = this.selectedObject.getDocument();
                    MessagesController.getInstance().saveGif(document);
                    showGifHint();
                    this.chatActivityEnterView.addRecentGif(document);
                    break;
                case 12:
                    if (getParentActivity() != null) {
                        if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                            this.actionBar.closeSearchField();
                            this.chatActivityEnterView.setFieldFocused();
                        }
                        this.mentionsAdapter.setNeedBotContext(false);
                        this.chatListView.setOnItemLongClickListener(null);
                        this.chatListView.setOnItemClickListener(null);
                        this.chatListView.setClickable(false);
                        this.chatListView.setLongClickable(false);
                        this.chatActivityEnterView.setEditingMessageObject(this.selectedObject, !this.selectedObject.isMediaEmpty());
                        if (this.chatActivityEnterView.isEditingCaption()) {
                            this.mentionsAdapter.setAllowNewMentions(false);
                        }
                        this.actionModeTitleContainer.setVisibility(0);
                        this.selectedMessagesCountTextView.setVisibility(8);
                        checkEditTimer();
                        this.chatActivityEnterView.setAllowStickersAndGifs(false, false);
                        ActionBarMenu actionMode = this.actionBar.createActionMode();
                        actionMode.getItem(19).setVisibility(8);
                        actionMode.getItem(10).setVisibility(8);
                        actionMode.getItem(11).setVisibility(8);
                        actionMode.getItem(12).setVisibility(8);
                        this.actionBar.showActionMode();
                        updatePinnedMessageView(true);
                        updateVisibleRows();
                        TLObject req = new TL_messages_getMessageEditData();
                        req.peer = MessagesController.getInputPeer((int) this.dialog_id);
                        req.id = this.selectedObject.getId();
                        this.editingMessageObjectReqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                            public void run(final TLObject response, TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        ChatActivity.this.editingMessageObjectReqId = 0;
                                        if (response == null) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            builder.setMessage(LocaleController.getString("EditMessageError", R.string.EditMessageError));
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                            ChatActivity.this.showDialog(builder.create());
                                            if (ChatActivity.this.chatActivityEnterView != null) {
                                                ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
                                            }
                                        } else if (ChatActivity.this.chatActivityEnterView != null) {
                                            ChatActivity.this.chatActivityEnterView.showEditDoneProgress(false, true);
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    }
                    this.selectedObject = null;
                    return;
                case 13:
                    int i;
                    int mid = this.selectedObject.getId();
                    builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("PinMessageAlert", R.string.PinMessageAlert));
                    final boolean[] checks = new boolean[]{true};
                    View frameLayout = new FrameLayout(getParentActivity());
                    if (VERSION.SDK_INT >= 21) {
                        frameLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                    }
                    CheckBoxCell cell = new CheckBoxCell(getParentActivity(), true);
                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cell.setText(LocaleController.getString("PinNotify", R.string.PinNotify), "", true, false);
                    int dp = LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : 0;
                    if (LocaleController.isRTL) {
                        i = 0;
                    } else {
                        i = AndroidUtilities.dp(8.0f);
                    }
                    cell.setPadding(dp, 0, i, 0);
                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                    cell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            boolean z;
                            CheckBoxCell cell = (CheckBoxCell) v;
                            boolean[] zArr = checks;
                            if (checks[0]) {
                                z = false;
                            } else {
                                z = true;
                            }
                            zArr[0] = z;
                            cell.setChecked(checks[0], true);
                        }
                    });
                    builder.setView(frameLayout);
                    final int i2 = mid;
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, i2, checks[0]);
                        }
                    });
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    break;
                case 14:
                    builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("UnpinMessageAlert", R.string.UnpinMessageAlert));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, 0, false);
                        }
                    });
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    break;
                case 15:
                    args = new Bundle();
                    args.putInt("user_id", this.selectedObject.messageOwner.media.user_id);
                    args.putString("phone", this.selectedObject.messageOwner.media.phone_number);
                    args.putBoolean("addContact", true);
                    presentFragment(new ContactAddActivity(args));
                    break;
                case 16:
                    AndroidUtilities.addToClipboard(this.selectedObject.messageOwner.media.phone_number);
                    break;
                case 17:
                    try {
                        intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                        intent.addFlags(268435456);
                        getParentActivity().startActivityForResult(intent, 500);
                        break;
                    } catch (Throwable e) {
                        FileLog.e(e);
                        break;
                    }
                case 18:
                    VoIPHelper.startCall(this.currentUser, getParentActivity());
                    break;
            }
            this.selectedObject = null;
        }
    }

    public void didSelectDialog(DialogsActivity activity, long did, boolean param) {
        if (this.dialog_id == 0) {
            return;
        }
        if (this.forwaringMessage != null || !this.selectedMessagesIds[0].isEmpty() || !this.selectedMessagesIds[1].isEmpty()) {
            ArrayList<MessageObject> fmessages = new ArrayList();
            if (this.forwaringMessage != null) {
                fmessages.add(this.forwaringMessage);
                this.forwaringMessage = null;
            } else {
                for (int a = 1; a >= 0; a--) {
                    ArrayList<Integer> arrayList = new ArrayList(this.selectedMessagesIds[a].keySet());
                    Collections.sort(arrayList);
                    for (int b = 0; b < arrayList.size(); b++) {
                        Integer id = (Integer) arrayList.get(b);
                        MessageObject message = (MessageObject) this.selectedMessagesIds[a].get(id);
                        if (message != null && id.intValue() > 0) {
                            fmessages.add(message);
                        }
                    }
                    this.selectedMessagesCanCopyIds[a].clear();
                    this.selectedMessagesIds[a].clear();
                }
                this.cantDeleteMessagesCount = 0;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            if (did != this.dialog_id) {
                int lower_part = (int) did;
                int high_part = (int) (did >> 32);
                Bundle args = new Bundle();
                args.putBoolean("scrollToTopOnResume", this.scrollToTopOnResume);
                if (lower_part == 0) {
                    args.putInt("enc_id", high_part);
                } else if (lower_part > 0) {
                    args.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    args.putInt("chat_id", -lower_part);
                }
                if (lower_part == 0 || MessagesController.checkCanOpenChat(args, activity)) {
                    ChatActivity chatActivity = new ChatActivity(args);
                    if (presentFragment(chatActivity, true)) {
                        chatActivity.showReplyPanel(true, null, fmessages, null, false);
                        if (!AndroidUtilities.isTablet()) {
                            removeSelfFromStack();
                            return;
                        }
                        return;
                    }
                    activity.finishFragment();
                    return;
                }
                return;
            }
            activity.finishFragment();
            moveScrollToLastMessage();
            showReplyPanel(true, null, fmessages, null, false);
            if (AndroidUtilities.isTablet()) {
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            updateVisibleRows();
        }
    }

    public boolean onBackPressed() {
        if (this.actionBar != null && this.actionBar.isActionModeShowed()) {
            for (int a = 1; a >= 0; a--) {
                this.selectedMessagesIds[a].clear();
                this.selectedMessagesCanCopyIds[a].clear();
            }
            this.chatActivityEnterView.setEditingMessageObject(null, false);
            this.actionBar.hideActionMode();
            updatePinnedMessageView(true);
            this.cantDeleteMessagesCount = 0;
            updateVisibleRows();
            return false;
        } else if (this.chatActivityEnterView == null || !this.chatActivityEnterView.isPopupShowing()) {
            return true;
        } else {
            this.chatActivityEnterView.hidePopup(true);
            return false;
        }
    }

    private void updateVisibleRows() {
        if (this.chatListView != null) {
            int count = this.chatListView.getChildCount();
            MessageObject editingMessageObject = this.chatActivityEnterView != null ? this.chatActivityEnterView.getEditingMessageObject() : null;
            for (int a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    boolean disableSelection = false;
                    boolean selected = false;
                    if (this.actionBar.isActionModeShowed()) {
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject != editingMessageObject) {
                            if (!this.selectedMessagesIds[messageObject.getDialogId() == this.dialog_id ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId()))) {
                                view.setBackgroundDrawable(null);
                                disableSelection = true;
                            }
                        }
                        view.setBackgroundColor(Theme.getColor(Theme.key_chat_selectedBackground));
                        selected = true;
                        disableSelection = true;
                    } else {
                        view.setBackgroundDrawable(null);
                    }
                    cell.setMessageObject(cell.getMessageObject(), cell.isPinnedBottom(), cell.isPinnedTop());
                    boolean z = !disableSelection;
                    boolean z2 = disableSelection && selected;
                    cell.setCheckPressed(z, z2);
                    z2 = (this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID || cell.getMessageObject() == null || cell.getMessageObject().getId() != this.highlightMessageId) ? false : true;
                    cell.setHighlighted(z2);
                    if (this.searchContainer == null || this.searchContainer.getVisibility() != 0 || MessagesSearchQuery.getLastSearchQuery() == null) {
                        cell.setHighlightedText(null);
                    } else {
                        cell.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
                    }
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell cell2 = (ChatActionCell) view;
                    cell2.setMessageObject(cell2.getMessageObject());
                }
            }
        }
    }

    private void checkEditTimer() {
        if (this.chatActivityEnterView != null) {
            MessageObject messageObject = this.chatActivityEnterView.getEditingMessageObject();
            if (messageObject == null) {
                return;
            }
            if (this.currentUser == null || !this.currentUser.self) {
                int dt = (MessagesController.getInstance().maxEditTime + 300) - Math.abs(ConnectionsManager.getInstance().getCurrentTime() - messageObject.messageOwner.date);
                if (dt > 0) {
                    if (dt <= 300) {
                        if (this.actionModeSubTextView.getVisibility() != 0) {
                            this.actionModeSubTextView.setVisibility(0);
                        }
                        SimpleTextView simpleTextView = this.actionModeSubTextView;
                        Object[] objArr = new Object[1];
                        objArr[0] = String.format("%d:%02d", new Object[]{Integer.valueOf(dt / 60), Integer.valueOf(dt % 60)});
                        simpleTextView.setText(LocaleController.formatString("TimeToEdit", R.string.TimeToEdit, objArr));
                    } else if (this.actionModeSubTextView.getVisibility() != 8) {
                        this.actionModeSubTextView.setVisibility(8);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ChatActivity.this.checkEditTimer();
                        }
                    }, 1000);
                    return;
                }
                this.chatActivityEnterView.onEditTimeExpired();
                this.actionModeSubTextView.setText(LocaleController.formatString("TimeToEditExpired", R.string.TimeToEditExpired, new Object[0]));
            } else if (this.actionModeSubTextView.getVisibility() != 8) {
                this.actionModeSubTextView.setVisibility(8);
            }
        }
    }

    private ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject startMessageObject, boolean playingUnreadMedia) {
        ArrayList<MessageObject> messageObjects = new ArrayList();
        messageObjects.add(startMessageObject);
        int messageId = startMessageObject.getId();
        if (messageId != 0) {
            for (int a = this.messages.size() - 1; a >= 0; a--) {
                MessageObject messageObject = (MessageObject) this.messages.get(a);
                if (((this.currentEncryptedChat == null && messageObject.getId() > messageId) || (this.currentEncryptedChat != null && messageObject.getId() < messageId)) && messageObject.isVoice() && (!playingUnreadMedia || (messageObject.isContentUnread() && !messageObject.isOut()))) {
                    messageObjects.add(messageObject);
                }
            }
        }
        return messageObjects;
    }

    private void alertUserOpenError(MessageObject message) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (message.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", R.string.NoPlayerInstalled));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, message.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    private void openSearchWithText(String text) {
        this.avatarContainer.setVisibility(8);
        this.headerItem.setVisibility(8);
        this.attachItem.setVisibility(8);
        this.searchItem.setVisibility(0);
        updateSearchButtons(0, 0, 0);
        updateBottomOverlay();
        this.openSearchKeyboard = text == null;
        this.searchItem.openSearch(this.openSearchKeyboard);
        if (text != null) {
            this.searchItem.getSearchField().setText(text);
            this.searchItem.getSearchField().setSelection(this.searchItem.getSearchField().length());
            MessagesSearchQuery.searchMessagesInChat(text, this.dialog_id, this.mergeDialogId, this.classGuid, 0);
        }
    }

    public void updatePhotoAtIndex(int index) {
    }

    public boolean isSecretChat() {
        return this.currentEncryptedChat != null;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    public EncryptedChat getCurrentEncryptedChat() {
        return this.currentEncryptedChat;
    }

    public ChatFull getCurrentChatInfo() {
        return this.info;
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean scaleToFill() {
        return false;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        int count = this.chatListView.getChildCount();
        for (int a = 0; a < count; a++) {
            ImageReceiver imageReceiver = null;
            View view = this.chatListView.getChildAt(a);
            MessageObject message;
            if (view instanceof ChatMessageCell) {
                if (messageObject != null) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    message = cell.getMessageObject();
                    if (message != null && message.getId() == messageObject.getId()) {
                        imageReceiver = cell.getPhotoImage();
                    }
                }
            } else if (view instanceof ChatActionCell) {
                ChatActionCell cell2 = (ChatActionCell) view;
                message = cell2.getMessageObject();
                if (message != null) {
                    if (messageObject != null) {
                        if (message.getId() == messageObject.getId()) {
                            imageReceiver = cell2.getPhotoImage();
                        }
                    } else if (fileLocation != null && message.photoThumbs != null) {
                        for (int b = 0; b < message.photoThumbs.size(); b++) {
                            PhotoSize photoSize = (PhotoSize) message.photoThumbs.get(b);
                            if (photoSize.location.volume_id == fileLocation.volume_id && photoSize.location.local_id == fileLocation.local_id) {
                                imageReceiver = cell2.getPhotoImage();
                                break;
                            }
                        }
                    }
                }
            }
            if (imageReceiver != null) {
                int[] coords = new int[2];
                view.getLocationInWindow(coords);
                PlaceProviderObject object = new PlaceProviderObject();
                object.viewX = coords[0];
                object.viewY = coords[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                object.parentView = this.chatListView;
                object.imageReceiver = imageReceiver;
                object.thumb = imageReceiver.getBitmap();
                object.radius = imageReceiver.getRoundRadius();
                if ((view instanceof ChatActionCell) && this.currentChat != null) {
                    object.dialogId = -this.currentChat.id;
                }
                if ((this.pinnedMessageView == null || this.pinnedMessageView.getTag() != null) && (this.reportSpamView == null || this.reportSpamView.getTag() != null)) {
                    return object;
                }
                object.clipTopAddition = AndroidUtilities.dp(48.0f);
                return object;
            }
        }
        return null;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
    }

    public void willHidePhotoViewer() {
    }

    public boolean isPhotoChecked(int index) {
        return false;
    }

    public void setPhotoChecked(int index) {
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public void sendButtonPressed(int index) {
    }

    public int getSelectedCount() {
        return 0;
    }

    public void sendMedia(PhotoEntry photoEntry, boolean mutedVideo) {
        if (photoEntry.isVideo) {
            VideoEditedInfo videoEditedInfo = null;
            long size = 0;
            if (mutedVideo) {
                videoEditedInfo = new VideoEditedInfo();
                videoEditedInfo.bitrate = -1;
                videoEditedInfo.originalPath = photoEntry.path;
                videoEditedInfo.endTime = -1;
                videoEditedInfo.startTime = -1;
                size = new File(photoEntry.path).length();
            }
            SendMessagesHelper.prepareSendingVideo(photoEntry.path, size, 0, 0, 0, videoEditedInfo, this.dialog_id, this.replyingMessageObject, photoEntry.caption != null ? photoEntry.caption.toString() : null);
            showReplyPanel(false, null, null, null, false);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (photoEntry.imagePath != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry.imagePath, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.stickers, null);
            showReplyPanel(false, null, null, null, false);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (photoEntry.path != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry.path, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.stickers, null);
            showReplyPanel(false, null, null, null, false);
            DraftQuery.cleanDraft(this.dialog_id, true);
        }
    }

    public void showOpenGameAlert(TL_game game, MessageObject messageObject, String urlStr, boolean ask, int uid) {
        User user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
        if (ask) {
            String name;
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (user != null) {
                name = ContactsController.formatName(user.first_name, user.last_name);
            } else {
                name = "";
            }
            builder.setMessage(LocaleController.formatString("BotPermissionGameAlert", R.string.BotPermissionGameAlert, name));
            final TL_game tL_game = game;
            final MessageObject messageObject2 = messageObject;
            final String str = urlStr;
            final int i = uid;
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ChatActivity.this.showOpenGameAlert(tL_game, messageObject2, str, false, i);
                    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("askgame_" + i, false).commit();
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else if (VERSION.SDK_INT < 21 || AndroidUtilities.isTablet() || !WebviewActivity.supportWebview()) {
            Activity parentActivity = getParentActivity();
            r2 = game.short_name;
            String str2 = (user == null || user.username == null) ? "" : user.username;
            WebviewActivity.openGameInBrowser(urlStr, messageObject, parentActivity, r2, str2);
        } else if (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
            r2 = (user == null || TextUtils.isEmpty(user.username)) ? "" : user.username;
            presentFragment(new WebviewActivity(urlStr, r2, game.title, game.short_name, messageObject));
        }
    }

    public void showOpenUrlAlert(final String url, boolean ask) {
        boolean z = true;
        if (Browser.isInternalUrl(url) || !ask) {
            Context parentActivity = getParentActivity();
            if (this.inlineReturn != 0) {
                z = false;
            }
            Browser.openUrl(parentActivity, url, z);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert", R.string.OpenUrlAlert, url));
        builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Browser.openUrl(ChatActivity.this.getParentActivity(), url, ChatActivity.this.inlineReturn == 0);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = this.messages.indexOf(messageObject);
        if (index != -1) {
            this.messages.remove(index);
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemRemoved(((this.chatAdapter.messagesStartRow + this.messages.size()) - index) - 1);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate selectedBackgroundDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor(int color) {
                ChatActivity.this.updateVisibleRows();
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[313];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_chat_wallpaper);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[6] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[8] = new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[9] = new ThemeDescription(this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, null, null, Theme.key_actionBarDefaultSubtitle, null);
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[13] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[14] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault);
        themeDescriptionArr[15] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop);
        themeDescriptionArr[16] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector);
        themeDescriptionArr[17] = new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[18] = new ThemeDescription(this.actionModeTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[19] = new ThemeDescription(this.actionModeSubTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[20] = new ThemeDescription(this.avatarContainer.getTitleTextView(), 0, null, null, new Drawable[]{Theme.chat_muteIconDrawable}, null, Theme.key_chat_muteIcon);
        themeDescriptionArr[21] = new ThemeDescription(this.avatarContainer.getTitleTextView(), 0, null, null, new Drawable[]{Theme.chat_lockIconDrawable}, null, Theme.key_chat_lockIcon);
        themeDescriptionArr[22] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[23] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[24] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[25] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[26] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[27] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[28] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[29] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[30] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageRed);
        themeDescriptionArr[31] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageOrange);
        themeDescriptionArr[32] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageViolet);
        themeDescriptionArr[33] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageGreen);
        themeDescriptionArr[34] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageCyan);
        themeDescriptionArr[35] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageBlue);
        themeDescriptionArr[36] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessagePink);
        themeDescriptionArr[37] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble);
        themeDescriptionArr[38] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected);
        themeDescriptionArr[39] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, Theme.key_chat_inBubbleShadow);
        themeDescriptionArr[40] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble);
        themeDescriptionArr[41] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected);
        themeDescriptionArr[42] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, Theme.key_chat_outBubbleShadow);
        themeDescriptionArr[43] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[44] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceLink);
        themeDescriptionArr[45] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[46] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackgroundSelected);
        themeDescriptionArr[47] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextIn);
        themeDescriptionArr[48] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut);
        themeDescriptionArr[49] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageLinkIn, null);
        themeDescriptionArr[50] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageLinkOut, null);
        themeDescriptionArr[51] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheck);
        themeDescriptionArr[52] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected);
        themeDescriptionArr[53] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutClockDrawable}, null, Theme.key_chat_outSentClock);
        themeDescriptionArr[54] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, null, Theme.key_chat_outSentClockSelected);
        themeDescriptionArr[55] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInClockDrawable}, null, Theme.key_chat_inSentClock);
        themeDescriptionArr[56] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, null, Theme.key_chat_inSentClockSelected);
        themeDescriptionArr[57] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck);
        themeDescriptionArr[58] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, null, Theme.key_chat_mediaSentClock);
        themeDescriptionArr[59] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, null, Theme.key_chat_outViews);
        themeDescriptionArr[60] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, null, Theme.key_chat_outViewsSelected);
        themeDescriptionArr[61] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable}, null, Theme.key_chat_inViews);
        themeDescriptionArr[62] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, null, Theme.key_chat_inViewsSelected);
        themeDescriptionArr[63] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, null, Theme.key_chat_mediaViews);
        themeDescriptionArr[64] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, Theme.key_chat_outMenu);
        themeDescriptionArr[65] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, Theme.key_chat_outMenuSelected);
        themeDescriptionArr[66] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, Theme.key_chat_inMenu);
        themeDescriptionArr[67] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, Theme.key_chat_inMenuSelected);
        themeDescriptionArr[68] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, Theme.key_chat_mediaMenu);
        themeDescriptionArr[69] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable}, null, Theme.key_chat_outInstant);
        themeDescriptionArr[70] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantSelectedDrawable}, null, Theme.key_chat_outInstantSelected);
        themeDescriptionArr[71] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable}, null, Theme.key_chat_inInstant);
        themeDescriptionArr[72] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantSelectedDrawable}, null, Theme.key_chat_inInstantSelected);
        themeDescriptionArr[73] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, Theme.key_chat_sentError);
        themeDescriptionArr[74] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, Theme.key_chat_sentErrorIcon);
        themeDescriptionArr[75] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, selectedBackgroundDelegate, Theme.key_chat_selectedBackground);
        themeDescriptionArr[76] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, Theme.key_chat_previewDurationText);
        themeDescriptionArr[77] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, Theme.key_chat_previewGameText);
        themeDescriptionArr[78] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantText);
        themeDescriptionArr[79] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantText);
        themeDescriptionArr[80] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantSelectedText);
        themeDescriptionArr[81] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantSelectedText);
        themeDescriptionArr[82] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, Theme.key_chat_secretTimeText);
        themeDescriptionArr[83] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerNameText);
        themeDescriptionArr[84] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, Theme.key_chat_botButtonText);
        themeDescriptionArr[85] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, Theme.key_chat_botProgress);
        themeDescriptionArr[86] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inForwardedNameText);
        themeDescriptionArr[87] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outForwardedNameText);
        themeDescriptionArr[88] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inViaBotNameText);
        themeDescriptionArr[89] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outViaBotNameText);
        themeDescriptionArr[90] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerViaBotNameText);
        themeDescriptionArr[91] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine);
        themeDescriptionArr[92] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine);
        themeDescriptionArr[93] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyLine);
        themeDescriptionArr[94] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText);
        themeDescriptionArr[95] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText);
        themeDescriptionArr[96] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyNameText);
        themeDescriptionArr[97] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText);
        themeDescriptionArr[98] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText);
        themeDescriptionArr[99] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageText);
        themeDescriptionArr[100] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageText);
        themeDescriptionArr[101] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText);
        themeDescriptionArr[102] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText);
        themeDescriptionArr[103] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyMessageText);
        themeDescriptionArr[104] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewLine);
        themeDescriptionArr[105] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewLine);
        themeDescriptionArr[106] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSiteNameText);
        themeDescriptionArr[107] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSiteNameText);
        themeDescriptionArr[108] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactNameText);
        themeDescriptionArr[109] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactNameText);
        themeDescriptionArr[110] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneText);
        themeDescriptionArr[111] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneText);
        themeDescriptionArr[112] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaProgress);
        themeDescriptionArr[113] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioProgress);
        themeDescriptionArr[114] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioProgress);
        themeDescriptionArr[115] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress);
        themeDescriptionArr[116] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSelectedProgress);
        themeDescriptionArr[117] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaTimeText);
        themeDescriptionArr[118] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText);
        themeDescriptionArr[119] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText);
        themeDescriptionArr[120] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText);
        themeDescriptionArr[121] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText);
        themeDescriptionArr[122] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerfomerText);
        themeDescriptionArr[123] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerfomerText);
        themeDescriptionArr[124] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioTitleText);
        themeDescriptionArr[125] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioTitleText);
        themeDescriptionArr[126] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationText);
        themeDescriptionArr[127] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationText);
        themeDescriptionArr[128] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationSelectedText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_AC3] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationSelectedText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_HDMV_DTS] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbar);
        themeDescriptionArr[131] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbar);
        themeDescriptionArr[132] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarSelected);
        themeDescriptionArr[133] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarSelected);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_SPLICE_INFO] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarFill);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_E_AC3] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarFill);
        themeDescriptionArr[136] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbar);
        themeDescriptionArr[137] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbar);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_DTS] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarSelected);
        themeDescriptionArr[139] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarSelected);
        themeDescriptionArr[140] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarFill);
        themeDescriptionArr[141] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarFill);
        themeDescriptionArr[142] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgress);
        themeDescriptionArr[143] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgress);
        themeDescriptionArr[144] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgressSelected);
        themeDescriptionArr[145] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgressSelected);
        themeDescriptionArr[146] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileNameText);
        themeDescriptionArr[147] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileNameText);
        themeDescriptionArr[148] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoText);
        themeDescriptionArr[149] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoText);
        themeDescriptionArr[150] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoSelectedText);
        themeDescriptionArr[151] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoSelectedText);
        themeDescriptionArr[152] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackground);
        themeDescriptionArr[153] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackground);
        themeDescriptionArr[154] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackgroundSelected);
        themeDescriptionArr[155] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackgroundSelected);
        themeDescriptionArr[156] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueNameText);
        themeDescriptionArr[157] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueNameText);
        themeDescriptionArr[158] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoText);
        themeDescriptionArr[159] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoText);
        themeDescriptionArr[160] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoSelectedText);
        themeDescriptionArr[161] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoSelectedText);
        themeDescriptionArr[162] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaInfoText);
        themeDescriptionArr[163] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, Theme.key_chat_linkSelectBackground);
        themeDescriptionArr[164] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, Theme.key_chat_textSelectBackground);
        themeDescriptionArr[165] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outLoader);
        themeDescriptionArr[166] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outBubble);
        themeDescriptionArr[167] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outLoaderSelected);
        themeDescriptionArr[168] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outBubbleSelected);
        themeDescriptionArr[169] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inLoader);
        themeDescriptionArr[170] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inBubble);
        themeDescriptionArr[171] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inLoaderSelected);
        themeDescriptionArr[172] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inBubbleSelected);
        themeDescriptionArr[173] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhoto);
        themeDescriptionArr[174] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhotoIcon);
        themeDescriptionArr[175] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoSelected);
        themeDescriptionArr[176] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoIconSelected);
        themeDescriptionArr[177] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhoto);
        themeDescriptionArr[178] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhotoIcon);
        themeDescriptionArr[179] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoSelected);
        themeDescriptionArr[180] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoIconSelected);
        themeDescriptionArr[181] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhoto);
        themeDescriptionArr[182] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhotoIcon);
        themeDescriptionArr[183] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoSelected);
        themeDescriptionArr[184] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoIconSelected);
        themeDescriptionArr[185] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, null, Theme.key_chat_outFileIcon);
        themeDescriptionArr[186] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, null, Theme.key_chat_outFileSelectedIcon);
        themeDescriptionArr[187] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, null, Theme.key_chat_inFileIcon);
        themeDescriptionArr[188] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, null, Theme.key_chat_inFileSelectedIcon);
        themeDescriptionArr[PsExtractor.PRIVATE_STREAM_1] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactBackground);
        themeDescriptionArr[190] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactIcon);
        themeDescriptionArr[191] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactBackground);
        themeDescriptionArr[PsExtractor.AUDIO_STREAM] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactIcon);
        themeDescriptionArr[193] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationBackground);
        themeDescriptionArr[194] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationIcon);
        themeDescriptionArr[195] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationBackground);
        themeDescriptionArr[196] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationIcon);
        themeDescriptionArr[197] = new ThemeDescription(this.mentionContainer, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[198] = new ThemeDescription(this.mentionContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[199] = new ThemeDescription(this.searchContainer, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[Callback.DEFAULT_DRAG_ANIMATION_DURATION] = new ThemeDescription(this.searchContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[201] = new ThemeDescription(this.bottomOverlay, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[202] = new ThemeDescription(this.bottomOverlay, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[203] = new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[204] = new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[205] = new ThemeDescription(this.chatActivityEnterView, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[206] = new ThemeDescription(this.chatActivityEnterView, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[207] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"audioSendButton"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[208] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelText);
        themeDescriptionArr[209] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelHint);
        themeDescriptionArr[210] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, null, null, null, Theme.key_chat_messagePanelSend);
        themeDescriptionArr[211] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"emojiButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[212] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"botButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[213] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"notifyButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[214] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"attachButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[215] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"audioSendButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[216] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonImage"}, null, null, null, Theme.key_chat_editDoneIcon);
        themeDescriptionArr[217] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioPanel"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[218] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"micDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed);
        themeDescriptionArr[219] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordDeleteImageView"}, null, null, null, Theme.key_chat_messagePanelVoiceDelete);
        themeDescriptionArr[220] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioBackground"}, null, null, null, Theme.key_chat_recordedVoiceBackground);
        themeDescriptionArr[221] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordTimeText"}, null, null, null, Theme.key_chat_recordTime);
        themeDescriptionArr[222] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordTimeContainer"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[223] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordCancelText"}, null, null, null, Theme.key_chat_recordVoiceCancel);
        themeDescriptionArr[224] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordPanel"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[225] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioTimeTextView"}, null, null, null, Theme.key_chat_messagePanelVoiceDuration);
        themeDescriptionArr[226] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordCancelImage"}, null, null, null, Theme.key_chat_recordVoiceCancel);
        themeDescriptionArr[227] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonProgress"}, null, null, null, Theme.key_contextProgressInner1);
        themeDescriptionArr[228] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonProgress"}, null, null, null, Theme.key_contextProgressOuter1);
        themeDescriptionArr[229] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"cancelBotButton"}, null, null, null, Theme.key_chat_messagePanelCancelInlineBot);
        themeDescriptionArr[230] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"redDotPaint"}, null, null, null, Theme.key_chat_recordedVoiceDot);
        themeDescriptionArr[231] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paint"}, null, null, null, Theme.key_chat_messagePanelVoiceBackground);
        themeDescriptionArr[232] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paintRecord"}, null, null, null, Theme.key_chat_messagePanelVoiceShadow);
        themeDescriptionArr[233] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"seekBarWaveform"}, null, null, null, Theme.key_chat_recordedVoiceProgress);
        themeDescriptionArr[234] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"seekBarWaveform"}, null, null, null, Theme.key_chat_recordedVoiceProgressInner);
        themeDescriptionArr[235] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"playDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPause);
        themeDescriptionArr[236] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"pauseDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPause);
        themeDescriptionArr[237] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"dotPaint"}, null, null, null, Theme.key_chat_emojiPanelNewTrending);
        themeDescriptionArr[238] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"playDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPausePressed);
        themeDescriptionArr[239] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"pauseDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPausePressed);
        themeDescriptionArr[PsExtractor.VIDEO_STREAM_MASK] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelBackground);
        themeDescriptionArr[241] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelShadowLine);
        themeDescriptionArr[242] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelEmptyText);
        themeDescriptionArr[243] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelIcon);
        themeDescriptionArr[244] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelIconSelected);
        themeDescriptionArr[245] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelStickerPackSelector);
        themeDescriptionArr[246] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelIconSelector);
        themeDescriptionArr[247] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelBackspace);
        themeDescriptionArr[248] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelTrendingTitle);
        themeDescriptionArr[249] = new ThemeDescription(this.chatActivityEnterView.getEmojiView(), 0, new Class[]{EmojiView.class}, new String[]{""}, null, null, null, Theme.key_chat_emojiPanelTrendingDescription);
        themeDescriptionArr[Callback.DEFAULT_SWIPE_ANIMATION_DURATION] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonText);
        themeDescriptionArr[251] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackground);
        themeDescriptionArr[252] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackgroundPressed);
        themeDescriptionArr[253] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{PlayerView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        themeDescriptionArr[254] = new ThemeDescription(this.playerView, 0, new Class[]{PlayerView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        themeDescriptionArr[255] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PlayerView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        themeDescriptionArr[256] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PlayerView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerPerformer);
        themeDescriptionArr[InputDeviceCompat.SOURCE_KEYBOARD] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{PlayerView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        themeDescriptionArr[258] = new ThemeDescription(this.pinnedLineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chat_topPanelLine);
        themeDescriptionArr[259] = new ThemeDescription(this.pinnedMessageNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle);
        themeDescriptionArr[260] = new ThemeDescription(this.pinnedMessageTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage);
        themeDescriptionArr[261] = new ThemeDescription(this.alertNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle);
        themeDescriptionArr[262] = new ThemeDescription(this.alertTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage);
        themeDescriptionArr[263] = new ThemeDescription(this.closePinned, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose);
        themeDescriptionArr[264] = new ThemeDescription(this.closeReportSpam, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose);
        themeDescriptionArr[265] = new ThemeDescription(this.reportSpamView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[266] = new ThemeDescription(this.alertView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[267] = new ThemeDescription(this.pinnedMessageView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[268] = new ThemeDescription(this.addToContactsButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_addContact);
        themeDescriptionArr[269] = new ThemeDescription(this.reportSpamButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_reportSpam);
        themeDescriptionArr[270] = new ThemeDescription(this.replyLineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chat_replyPanelLine);
        themeDescriptionArr[271] = new ThemeDescription(this.replyNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_replyPanelName);
        themeDescriptionArr[272] = new ThemeDescription(this.replyObjectTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_replyPanelMessage);
        themeDescriptionArr[273] = new ThemeDescription(this.replyIconImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_replyPanelIcons);
        themeDescriptionArr[274] = new ThemeDescription(this.replyCloseImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_replyPanelClose);
        themeDescriptionArr[275] = new ThemeDescription(this.searchUpButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[276] = new ThemeDescription(this.searchDownButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[277] = new ThemeDescription(this.searchCalendarButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[278] = new ThemeDescription(this.searchCountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_searchPanelText);
        themeDescriptionArr[279] = new ThemeDescription(this.bottomOverlayText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_secretChatStatusText);
        themeDescriptionArr[280] = new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText);
        themeDescriptionArr[281] = new ThemeDescription(this.bigEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[282] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[283] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[284] = new ThemeDescription(this.stickersPanelArrow, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_stickersHintPanel);
        themeDescriptionArr[285] = new ThemeDescription(this.stickersListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{StickerCell.class}, null, null, null, Theme.key_chat_stickersHintPanel);
        themeDescriptionArr[286] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, null, null, null, Theme.key_chat_unreadMessagesStartBackground);
        themeDescriptionArr[287] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_chat_unreadMessagesStartArrowIcon);
        themeDescriptionArr[288] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_unreadMessagesStartText);
        themeDescriptionArr[289] = new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[290] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[291] = new ThemeDescription(this.bigEmptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[292] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[293] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[294] = new ThemeDescription(this.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotSwitchCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_botSwitchToInlineText);
        themeDescriptionArr[295] = new ThemeDescription(this.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[296] = new ThemeDescription(this.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"usernameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[297] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, new Drawable[]{Theme.chat_inlineResultFile, Theme.chat_inlineResultAudio, Theme.chat_inlineResultLocation}, null, Theme.key_chat_inlineResultIcon);
        themeDescriptionArr[298] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[299] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[300] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[301] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioProgress);
        themeDescriptionArr[302] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress);
        themeDescriptionArr[303] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_divider);
        themeDescriptionArr[304] = new ThemeDescription(this.gifHintTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_gifSaveHintBackground);
        themeDescriptionArr[305] = new ThemeDescription(this.gifHintTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_gifSaveHintText);
        themeDescriptionArr[306] = new ThemeDescription(this.pagedownButtonCounter, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground);
        themeDescriptionArr[307] = new ThemeDescription(this.pagedownButtonCounter, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_goDownButtonCounter);
        themeDescriptionArr[308] = new ThemeDescription(this.pagedownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButton);
        themeDescriptionArr[309] = new ThemeDescription(this.pagedownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chat_goDownButtonShadow);
        themeDescriptionArr[310] = new ThemeDescription(this.pagedownButtonImage, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_goDownButtonIcon);
        themeDescriptionArr[311] = new ThemeDescription(this.avatarContainer.getTimeItem(), 0, null, null, null, null, Theme.key_chat_secretTimerBackground);
        themeDescriptionArr[312] = new ThemeDescription(this.avatarContainer.getTimeItem(), 0, null, null, null, null, Theme.key_chat_secretTimerText);
        return themeDescriptionArr;
    }
}
