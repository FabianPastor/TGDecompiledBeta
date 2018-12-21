package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.p000v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.FloatProperty;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.CLASSNAMEC;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.p003ts.PsExtractor;
import com.google.android.exoplayer2.extractor.p003ts.TsExtractor;
import com.google.android.exoplayer2.p004ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.util.MimeTypes;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import org.telegram.PhoneFormat.CLASSNAMEPhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiSuggestion;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessagePosition;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.GridLayoutManagerFixed;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerMiddle;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.p005ui.ActionBar.ActionBarLayout;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BackDrawable;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.Theme.ThemeInfo;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Adapters.MentionsAdapter;
import org.telegram.p005ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.p005ui.Adapters.StickersAdapter;
import org.telegram.p005ui.Cells.BotHelpCell;
import org.telegram.p005ui.Cells.BotSwitchCell;
import org.telegram.p005ui.Cells.ChatActionCell;
import org.telegram.p005ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.p005ui.Cells.ChatLoadingCell;
import org.telegram.p005ui.Cells.ChatMessageCell;
import org.telegram.p005ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.p005ui.Cells.ChatUnreadCell;
import org.telegram.p005ui.Cells.CheckBoxCell;
import org.telegram.p005ui.Cells.ContextLinkCell;
import org.telegram.p005ui.Cells.MentionCell;
import org.telegram.p005ui.Cells.StickerCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.ChatActivityEnterView;
import org.telegram.p005ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.p005ui.Components.ChatAttachAlert;
import org.telegram.p005ui.Components.ChatAttachAlert.ChatAttachViewDelegate;
import org.telegram.p005ui.Components.ChatAvatarContainer;
import org.telegram.p005ui.Components.ChatBigEmptyView;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.CorrectlyMeasuringTextView;
import org.telegram.p005ui.Components.EmbedBottomSheet;
import org.telegram.p005ui.Components.EmojiView;
import org.telegram.p005ui.Components.ExtendedGridLayoutManager;
import org.telegram.p005ui.Components.FragmentContextView;
import org.telegram.p005ui.Components.InstantCameraView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.NumberTextView;
import org.telegram.p005ui.Components.PipRoundVideoView;
import org.telegram.p005ui.Components.RadialProgressView;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListenerExtended;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListenerExtended;
import org.telegram.p005ui.Components.ShareAlert;
import org.telegram.p005ui.Components.Size;
import org.telegram.p005ui.Components.SizeNotifierFrameLayout;
import org.telegram.p005ui.Components.StickersAlert;
import org.telegram.p005ui.Components.StickersAlert.StickersAlertDelegate;
import org.telegram.p005ui.Components.TypefaceSpan;
import org.telegram.p005ui.Components.URLSpanBotCommand;
import org.telegram.p005ui.Components.URLSpanMono;
import org.telegram.p005ui.Components.URLSpanNoUnderline;
import org.telegram.p005ui.Components.URLSpanReplacement;
import org.telegram.p005ui.Components.URLSpanUserMention;
import org.telegram.p005ui.Components.voip.VoIPHelper;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.p005ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import org.telegram.p005ui.LocationActivity.LocationActivityDelegate;
import org.telegram.p005ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.p005ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;
import org.telegram.p005ui.StickerPreviewViewer.StickerPreviewViewerDelegate;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botCommand;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_reportSpam;
import org.telegram.tgnet.TLRPC.TL_chatForbidden;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC.TL_inputMediaPoll;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionSecureValuesSent;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_getMessageEditData;
import org.telegram.tgnet.TLRPC.TL_messages_getUnreadMentions;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_pollResults;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardForceReply;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;

/* renamed from: org.telegram.ui.ChatActivity */
public class ChatActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate, LocationActivityDelegate {
    private static final int add_shortcut = 24;
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
    private static final int edit = 23;
    private static final int forward = 11;
    private static final int id_chat_compose_panel = 1000;
    private static final int mute = 18;
    private static final int reply = 19;
    private static final int report = 21;
    private static final int search = 40;
    private static final int share_contact = 17;
    private static final int star = 22;
    private static final int text_bold = 50;
    private static final int text_italic = 51;
    private static final int text_link = 53;
    private static final int text_mono = 52;
    private static final int text_regular = 54;
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
    private ArrayList<MessageObject> animatingMessageObjects = new ArrayList();
    private Paint aspectPaint;
    private Path aspectPath;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ActionBarMenuItem attachItem;
    private ChatAvatarContainer avatarContainer;
    private ChatBigEmptyView bigEmptyView;
    private MessageObject botButtons;
    private PhotoViewerProvider botContextProvider = new CLASSNAME();
    private ArrayList<Object> botContextResults;
    private SparseArray<BotInfo> botInfo = new SparseArray();
    private MessageObject botReplyButtons;
    private String botUser;
    private int botsCount;
    private FrameLayout bottomOverlay;
    private AnimatorSet bottomOverlayAnimation;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private RadialProgressView bottomOverlayProgress;
    private TextView bottomOverlayText;
    private boolean[] cacheEndReached = new boolean[2];
    private int canEditMessagesCount;
    private int cantDeleteMessagesCount;
    protected ChatActivityEnterView chatActivityEnterView;
    private ChatActivityAdapter chatAdapter;
    private ChatAttachAlert chatAttachAlert;
    private long chatEnterTime;
    protected ChatFull chatInfo;
    private GridLayoutManagerFixed chatLayoutManager;
    private long chatLeaveTime;
    private RecyclerListView chatListView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList();
    private boolean checkTextureViewPosition;
    private Dialog closeChatDialog;
    private ImageView closePinned;
    private ImageView closeReportSpam;
    private SizeNotifierFrameLayout contentView;
    private int createUnreadMessageAfterId;
    private boolean createUnreadMessageAfterIdLoading;
    protected Chat currentChat;
    protected EncryptedChat currentEncryptedChat;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private String currentPicturePath;
    protected User currentUser;
    private long dialog_id;
    private ChatMessageCell drawLaterRoundProgressCell;
    private int editTextEnd;
    private ActionBarMenuItem editTextItem;
    private int editTextStart;
    private MessageObject editingMessageObject;
    private int editingMessageObjectReqId;
    private View emojiButtonRed;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean[] endReached = new boolean[2];
    private boolean first = true;
    private boolean firstLoading = true;
    boolean firstOpen = true;
    private boolean firstUnreadSent = false;
    private int first_unread_id;
    private boolean fixPaddingsInLayout;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private boolean forceScrollToTop;
    private boolean[] forwardEndReached = new boolean[]{true, true};
    private MessageObject forwardingMessage;
    private GroupedMessages forwardingMessageGroup;
    private ArrayList<MessageObject> forwardingMessages;
    private ArrayList<CharSequence> foundUrls;
    private WebPage foundWebPage;
    private FragmentContextView fragmentContextView;
    private TextView gifHintTextView;
    private boolean globalIgnoreLayout;
    private LongSparseArray<GroupedMessages> groupedMessagesMap = new LongSparseArray();
    private boolean hasAllMentionsLocal;
    private boolean hasBotsCommands;
    private boolean hasUnfavedSelected;
    private ActionBarMenuItem headerItem;
    private Runnable hideAlertViewRunnable;
    private int highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private boolean ignoreAttachOnPause;
    private long inlineReturn;
    private InstantCameraView instantCameraView;
    private boolean isBroadcast;
    private int lastLoadIndex;
    private int last_message_id = 0;
    private int linkSearchRequestId;
    private boolean loading;
    private boolean loadingForward;
    private boolean loadingFromOldPosition;
    private int loadingPinnedMessage;
    private int loadsCount;
    private boolean locationAlertShown;
    private int[] maxDate = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private int[] maxMessageId = new int[]{ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID};
    private TextView mediaBanTooltip;
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
    private FrameLayout mentiondownButton;
    private ObjectAnimator mentiondownButtonAnimation;
    private TextView mentiondownButtonCounter;
    private ImageView mentiondownButtonImage;
    private MentionsAdapter mentionsAdapter;
    private OnItemClickListener mentionsOnItemClickListener;
    private long mergeDialogId;
    protected ArrayList<MessageObject> messages = new ArrayList();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap();
    private SparseArray<MessageObject>[] messagesDict = new SparseArray[]{new SparseArray(), new SparseArray()};
    private int[] minDate = new int[2];
    private int[] minMessageId = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
    private TextView muteItem;
    private MessageObject needAnimateToMessage;
    private boolean needSelectFromMessageId;
    private int newMentionsCount;
    private int newUnreadMessageCount;
    OnItemClickListenerExtended onItemClickListener = new CLASSNAME();
    OnItemLongClickListenerExtended onItemLongClickListener = new CLASSNAME();
    private boolean openAnimationEnded;
    private boolean openSearchKeyboard;
    private View overlayView;
    private FrameLayout pagedownButton;
    private AnimatorSet pagedownButtonAnimation;
    private TextView pagedownButtonCounter;
    private ImageView pagedownButtonImage;
    private boolean pagedownButtonShowedByScroll;
    private boolean paused = true;
    private boolean pausedOnLastMessage;
    private String pendingLinkSearchString;
    private Runnable pendingWebPageTimeoutRunnable;
    private PhotoViewerProvider photoViewerProvider = new CLASSNAME();
    private FileLocation pinnedImageLocation;
    private View pinnedLineView;
    private BackupImageView pinnedMessageImageView;
    private SimpleTextView pinnedMessageNameTextView;
    private MessageObject pinnedMessageObject;
    private SimpleTextView pinnedMessageTextView;
    private FrameLayout pinnedMessageView;
    private AnimatorSet pinnedMessageViewAnimator;
    private LongSparseArray<ArrayList<MessageObject>> polls = new LongSparseArray();
    ArrayList<MessageObject> pollsToCheck = new ArrayList(10);
    private int prevSetUnreadCount = Integer.MIN_VALUE;
    private RadialProgressView progressBar;
    private FrameLayout progressView;
    private View progressView2;
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
    private FrameLayout roundVideoContainer;
    private AnimatorSet runningAnimation;
    private MessageObject scrollToMessage;
    private int scrollToMessagePosition = -10000;
    private int scrollToOffsetOnRecreate = 0;
    private int scrollToPositionOnRecreate = -1;
    private boolean scrollToTopOnResume;
    private boolean scrollToTopUnReadOnResume;
    private boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    private ImageView searchUpButton;
    private ImageView searchUserButton;
    private boolean searchingForUser;
    private User searchingUserMessages;
    private SparseArray<MessageObject>[] selectedMessagesCanCopyIds = new SparseArray[]{new SparseArray(), new SparseArray()};
    private SparseArray<MessageObject>[] selectedMessagesCanStarIds = new SparseArray[]{new SparseArray(), new SparseArray()};
    private NumberTextView selectedMessagesCountTextView;
    private SparseArray<MessageObject>[] selectedMessagesIds = new SparseArray[]{new SparseArray(), new SparseArray()};
    private MessageObject selectedObject;
    private GroupedMessages selectedObjectGroup;
    private int startLoadFromMessageId;
    private int startLoadFromMessageOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private boolean startReplyOnTextChange;
    private String startVideoEdit;
    private StickersAdapter stickersAdapter;
    private RecyclerListView stickersListView;
    private OnItemClickListener stickersOnItemClickListener;
    private FrameLayout stickersPanel;
    private ImageView stickersPanelArrow;
    private View timeItem2;
    private int topViewWasVisible;
    private MessageObject unreadMessageObject;
    private boolean userBlocked = false;
    protected TL_userFull userInfo;
    private TextureView videoTextureView;
    private AnimatorSet voiceHintAnimation;
    private Runnable voiceHintHideRunnable;
    private TextView voiceHintTextView;
    private Runnable waitingForCharaterEnterRunnable;
    private ArrayList<Integer> waitingForLoad = new ArrayList();
    private boolean waitingForReplyMessageLoad;
    private boolean wasManualScroll;
    private boolean wasPaused;

    /* renamed from: org.telegram.ui.ChatActivity$10 */
    class CLASSNAME extends SpanSizeLookup {
        CLASSNAME() {
        }

        public int getSpanSize(int position) {
            if (position >= ChatActivity.this.chatAdapter.messagesStartRow && position < ChatActivity.this.chatAdapter.messagesEndRow) {
                int idx = position - ChatActivity.this.chatAdapter.messagesStartRow;
                if (idx >= 0 && idx < ChatActivity.this.messages.size()) {
                    MessageObject message = (MessageObject) ChatActivity.this.messages.get(idx);
                    GroupedMessages groupedMessages = ChatActivity.this.getValidGroupedMessage(message);
                    if (groupedMessages != null) {
                        return ((GroupedMessagePosition) groupedMessages.positions.get(message)).spanSize;
                    }
                }
            }
            return ChatActivity.id_chat_compose_panel;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$11 */
    class CLASSNAME extends ItemDecoration {
        CLASSNAME() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            outRect.bottom = 0;
            if (view instanceof ChatMessageCell) {
                ChatMessageCell cell = (ChatMessageCell) view;
                GroupedMessages group = cell.getCurrentMessagesGroup();
                if (group != null) {
                    GroupedMessagePosition position = cell.getCurrentPosition();
                    if (position != null && position.siblingHeights != null) {
                        int a;
                        float maxHeight = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
                        int h = cell.getCaptionHeight();
                        for (float f : position.siblingHeights) {
                            h += (int) Math.ceil((double) (f * maxHeight));
                        }
                        h += (position.maxY - position.minY) * AndroidUtilities.dp2(11.0f);
                        int count = group.posArray.size();
                        for (a = 0; a < count; a++) {
                            GroupedMessagePosition pos = (GroupedMessagePosition) group.posArray.get(a);
                            if (pos.minY == position.minY && ((pos.minX != position.minX || pos.maxX != position.maxX || pos.minY != position.minY || pos.maxY != position.maxY) && pos.minY == position.minY)) {
                                h -= ((int) Math.ceil((double) (pos.var_ph * maxHeight))) - AndroidUtilities.m9dp(4.0f);
                                break;
                            }
                        }
                        outRect.bottom = -h;
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$12 */
    class CLASSNAME extends OnScrollListener {
        private final int scrollValue = AndroidUtilities.m9dp(100.0f);
        private float totalDy = 0.0f;

        /* renamed from: org.telegram.ui.ChatActivity$12$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animation) {
                if (animation.equals(ChatActivity.this.floatingDateAnimation)) {
                    ChatActivity.this.floatingDateAnimation = null;
                }
            }
        }

        CLASSNAME() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 2) {
                ChatActivity.this.wasManualScroll = true;
            } else if (newState == 1) {
                ChatActivity.this.wasManualScroll = true;
                ChatActivity.this.scrollingFloatingDate = true;
                ChatActivity.this.checkTextureViewPosition = true;
            } else if (newState == 0) {
                ChatActivity.this.scrollingFloatingDate = false;
                ChatActivity.this.checkTextureViewPosition = false;
                ChatActivity.this.hideFloatingDateView(true);
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ChatActivity.this.chatListView.invalidate();
            if (!(ChatActivity.this.wasManualScroll || dy == 0)) {
                ChatActivity.this.wasManualScroll = true;
            }
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
                    AnimatorSet access$13600 = ChatActivity.this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.floatingDateView, "alpha", new float[]{1.0f});
                    access$13600.playTogether(animatorArr);
                    ChatActivity.this.floatingDateAnimation.addListener(new CLASSNAME());
                    ChatActivity.this.floatingDateAnimation.start();
                }
            }
            ChatActivity.this.checkScrollForLoad(true);
            int firstVisibleItem = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItem != -1) {
                int totalItemCount = ChatActivity.this.chatAdapter.getItemCount();
                if (firstVisibleItem == 0 && ChatActivity.this.forwardEndReached[0]) {
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
            ChatActivity.this.updateMessagesVisiblePart(true);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$14 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        private void loadLastUnreadMention() {
            ChatActivity.this.wasManualScroll = true;
            if (ChatActivity.this.hasAllMentionsLocal) {
                MessagesStorage.getInstance(ChatActivity.this.currentAccount).getUnreadMention(ChatActivity.this.dialog_id, new ChatActivity$14$$Lambda$0(this));
                return;
            }
            MessagesStorage messagesStorage = MessagesStorage.getInstance(ChatActivity.this.currentAccount);
            TL_messages_getUnreadMentions req = new TL_messages_getUnreadMentions();
            req.peer = MessagesController.getInstance(ChatActivity.this.currentAccount).getInputPeer((int) ChatActivity.this.dialog_id);
            req.limit = 1;
            req.add_offset = ChatActivity.this.newMentionsCount - 1;
            ConnectionsManager.getInstance(ChatActivity.this.currentAccount).sendRequest(req, new ChatActivity$14$$Lambda$1(this, messagesStorage));
        }

        final /* synthetic */ void lambda$loadLastUnreadMention$0$ChatActivity$14(int param) {
            if (param == 0) {
                ChatActivity.this.hasAllMentionsLocal = false;
                loadLastUnreadMention();
                return;
            }
            ChatActivity.this.scrollToMessageId(param, 0, false, 0, false);
        }

        final /* synthetic */ void lambda$loadLastUnreadMention$2$ChatActivity$14(MessagesStorage messagesStorage, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new ChatActivity$14$$Lambda$2(this, response, error, messagesStorage));
        }

        final /* synthetic */ void lambda$null$1$ChatActivity$14(TLObject response, TL_error error, MessagesStorage messagesStorage) {
            messages_Messages res = (messages_Messages) response;
            if (error != null || res.messages.isEmpty()) {
                if (res != null) {
                    ChatActivity.this.newMentionsCount = res.count;
                } else {
                    ChatActivity.this.newMentionsCount = 0;
                }
                messagesStorage.resetMentionsCount(ChatActivity.this.dialog_id, ChatActivity.this.newMentionsCount);
                if (ChatActivity.this.newMentionsCount == 0) {
                    ChatActivity.this.hasAllMentionsLocal = true;
                    ChatActivity.this.showMentiondownButton(false, true);
                    return;
                }
                ChatActivity.this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(ChatActivity.this.newMentionsCount)}));
                loadLastUnreadMention();
                return;
            }
            int id = ((Message) res.messages.get(0)).var_id;
            long mid = (long) id;
            if (ChatObject.isChannel(ChatActivity.this.currentChat)) {
                mid |= ((long) ChatActivity.this.currentChat.var_id) << 32;
            }
            MessageObject object = (MessageObject) ChatActivity.this.messagesDict[0].get(id);
            messagesStorage.markMessageAsMention(mid);
            if (object != null) {
                object.messageOwner.media_unread = true;
                object.messageOwner.mentioned = true;
            }
            ChatActivity.this.scrollToMessageId(id, 0, false, 0, false);
        }

        public void onClick(View view) {
            loadLastUnreadMention();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$19 */
    class CLASSNAME extends SpanSizeLookup {
        CLASSNAME() {
        }

        public int getSpanSize(int position) {
            if (ChatActivity.this.mentionsAdapter.getItem(position) instanceof TL_inlineBotSwitchPM) {
                return 100;
            }
            if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                position--;
            }
            return ChatActivity.this.mentionGridLayoutManager.getSpanSizeForItem(position);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$1 */
    class CLASSNAME extends EmptyPhotoViewerProvider {
        CLASSNAME() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            int count = ChatActivity.this.chatListView.getChildCount();
            for (int a = 0; a < count; a++) {
                ImageReceiver imageReceiver = null;
                View view = ChatActivity.this.chatListView.getChildAt(a);
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
                    object.parentView = ChatActivity.this.chatListView;
                    object.imageReceiver = imageReceiver;
                    object.thumb = imageReceiver.getBitmapSafe();
                    object.radius = imageReceiver.getRoundRadius();
                    if ((view instanceof ChatActionCell) && ChatActivity.this.currentChat != null) {
                        object.dialogId = -ChatActivity.this.currentChat.var_id;
                    }
                    if ((ChatActivity.this.pinnedMessageView == null || ChatActivity.this.pinnedMessageView.getTag() != null) && (ChatActivity.this.reportSpamView == null || ChatActivity.this.reportSpamView.getTag() != null)) {
                        return object;
                    }
                    object.clipTopAddition = AndroidUtilities.m9dp(48.0f);
                    return object;
                }
            }
            return null;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$20 */
    class CLASSNAME extends ItemDecoration {
        CLASSNAME() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int i = 0;
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
            if (parent.getLayoutManager() == ChatActivity.this.mentionGridLayoutManager) {
                int position = parent.getChildAdapterPosition(view);
                if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                    outRect.top = AndroidUtilities.m9dp(2.0f);
                } else if (position != 0) {
                    position--;
                    if (!ChatActivity.this.mentionGridLayoutManager.isFirstRow(position)) {
                        outRect.top = AndroidUtilities.m9dp(2.0f);
                    }
                } else {
                    return;
                }
                if (!ChatActivity.this.mentionGridLayoutManager.isLastInRow(position)) {
                    i = AndroidUtilities.m9dp(2.0f);
                }
                outRect.right = i;
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$21 */
    class CLASSNAME implements MentionsAdapterDelegate {

        /* renamed from: org.telegram.ui.ChatActivity$21$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

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
        }

        /* renamed from: org.telegram.ui.ChatActivity$21$2 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

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
        }

        CLASSNAME() {
        }

        public void needChangePanelVisibility(boolean show) {
            if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionGridLayoutManager);
            } else {
                ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionLayoutManager);
            }
            if (show && ChatActivity.this.bottomOverlay.getVisibility() == 0) {
                show = false;
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
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        if (!preferences.getBoolean("secretbot", false)) {
                            Builder builder = new Builder(ChatActivity.this.getParentActivity());
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
                    ChatActivity.this.mentionListAnimation.addListener(new CLASSNAME());
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
                AnimatorSet access$15500 = ChatActivity.this.mentionListAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", new float[]{0.0f});
                access$15500.playTogether(animatorArr);
                ChatActivity.this.mentionListAnimation.addListener(new CLASSNAME());
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
            if (ChatActivity.this.getParentActivity() != null && result.content != null) {
                if (result.type.equals(MimeTypes.BASE_TYPE_VIDEO) || result.type.equals("web_player_video")) {
                    int[] size = MessageObject.getInlineResultWidthAndHeight(result);
                    EmbedBottomSheet.show(ChatActivity.this.getParentActivity(), result.title != null ? result.title : TtmlNode.ANONYMOUS_REGION_ID, result.description, result.content.url, result.content.url, size[0], size[1]);
                    return;
                }
                Browser.openUrl(ChatActivity.this.getParentActivity(), result.content.url);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$22 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$23 */
    class CLASSNAME implements ChatActivityEnterViewDelegate {
        CLASSNAME() {
        }

        public void onMessageSend(CharSequence message) {
            ChatActivity.this.moveScrollToLastMessage();
            ChatActivity.this.hideFieldPanel();
            if (ChatActivity.this.mentionsAdapter != null) {
                ChatActivity.this.mentionsAdapter.addHashtagsFromMessage(message);
            }
        }

        public void onSwitchRecordMode(boolean video) {
            ChatActivity.this.showVoiceHint(false, video);
        }

        public void onPreAudioVideoRecord() {
            ChatActivity.this.showVoiceHint(true, false);
        }

        public void onTextSelectionChanged(int start, int end) {
            if (ChatActivity.this.editTextItem != null) {
                if (end - start > 0) {
                    if (ChatActivity.this.editTextItem.getTag() == null) {
                        ChatActivity.this.editTextItem.setTag(Integer.valueOf(1));
                        ChatActivity.this.editTextItem.setVisibility(0);
                        ChatActivity.this.headerItem.setVisibility(8);
                        ChatActivity.this.attachItem.setVisibility(8);
                    }
                    ChatActivity.this.editTextStart = start;
                    ChatActivity.this.editTextEnd = end;
                } else if (ChatActivity.this.editTextItem.getTag() != null) {
                    ChatActivity.this.editTextItem.setTag(null);
                    ChatActivity.this.editTextItem.setVisibility(8);
                    if (ChatActivity.this.chatActivityEnterView.hasText()) {
                        ChatActivity.this.headerItem.setVisibility(8);
                        ChatActivity.this.attachItem.setVisibility(0);
                        return;
                    }
                    ChatActivity.this.headerItem.setVisibility(0);
                    ChatActivity.this.attachItem.setVisibility(8);
                }
            }
        }

        public void onTextChanged(final CharSequence text, boolean bigChange) {
            if (ChatActivity.this.startReplyOnTextChange && text.length() > 0) {
                ChatActivity.this.actionBar.getActionBarMenuOnItemClick().onItemClick(19);
                ChatActivity.this.startReplyOnTextChange = false;
            }
            MediaController instance = MediaController.getInstance();
            boolean z = !TextUtils.isEmpty(text) || ChatActivity.this.chatActivityEnterView.isEditingMessage();
            instance.setInputFieldHasText(z);
            if (!(ChatActivity.this.stickersAdapter == null || ChatActivity.this.chatActivityEnterView.isEditingMessage() || !ChatObject.canSendStickers(ChatActivity.this.currentChat))) {
                ChatActivity.this.stickersAdapter.loadStikersForEmoji(text);
            }
            if (ChatActivity.this.mentionsAdapter != null) {
                ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), ChatActivity.this.chatActivityEnterView.getCursorPosition(), ChatActivity.this.messages, false);
            }
            if (ChatActivity.this.waitingForCharaterEnterRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable);
                ChatActivity.this.waitingForCharaterEnterRunnable = null;
            }
            if (!ChatObject.canSendEmbed(ChatActivity.this.currentChat) || !ChatActivity.this.chatActivityEnterView.isMessageWebPageSearchEnabled()) {
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

        public void onTextSpansChanged(CharSequence text) {
            ChatActivity.this.searchLinks(text, true);
        }

        public void needSendTyping() {
            MessagesController.getInstance(ChatActivity.this.currentAccount).sendTyping(ChatActivity.this.dialog_id, 0, ChatActivity.this.classGuid);
        }

        public void onAttachButtonHidden() {
            if (!ChatActivity.this.actionBar.isSearchFieldVisible() && ChatActivity.this.headerItem != null) {
                ChatActivity.this.headerItem.setVisibility(8);
                ChatActivity.this.editTextItem.setVisibility(8);
                ChatActivity.this.attachItem.setVisibility(0);
            }
        }

        public void onAttachButtonShow() {
            if (!ChatActivity.this.actionBar.isSearchFieldVisible() && ChatActivity.this.headerItem != null) {
                ChatActivity.this.headerItem.setVisibility(0);
                ChatActivity.this.editTextItem.setVisibility(8);
                ChatActivity.this.attachItem.setVisibility(8);
            }
        }

        public void onMessageEditEnd(boolean loading) {
            if (!loading) {
                MentionsAdapter access$5400 = ChatActivity.this.mentionsAdapter;
                boolean z = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                access$5400.setNeedBotContext(z);
                ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                ChatActivity.this.chatListView.setClickable(true);
                ChatActivity.this.chatListView.setLongClickable(true);
                if (ChatActivity.this.editingMessageObject != null) {
                    ChatActivity.this.hideFieldPanel();
                }
                ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                if (ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 23) {
                    z = true;
                } else {
                    z = false;
                }
                boolean z2 = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                chatActivityEnterView.setAllowStickersAndGifs(z, z2);
                if (ChatActivity.this.editingMessageObjectReqId != 0) {
                    ConnectionsManager.getInstance(ChatActivity.this.currentAccount).cancelRequest(ChatActivity.this.editingMessageObjectReqId, true);
                    ChatActivity.this.editingMessageObjectReqId = 0;
                }
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateBottomOverlay();
                ChatActivity.this.updateVisibleRows();
            }
        }

        public void onWindowSizeChanged(int size) {
            boolean z = true;
            if (size < AndroidUtilities.m9dp(72.0f) + CLASSNAMEActionBar.getCurrentActionBarHeight()) {
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
            if (ChatActivity.this.chatAttachAlert != null) {
                ChatActivity.this.chatAttachAlert.setEditingMessageObject(null);
            }
            ChatActivity.this.openAttachMenu();
        }

        public void needStartRecordVideo(int state) {
            if (ChatActivity.this.instantCameraView == null) {
                return;
            }
            if (state == 0) {
                ChatActivity.this.instantCameraView.showCamera();
            } else if (state == 1 || state == 3 || state == 4) {
                ChatActivity.this.instantCameraView.send(state);
            } else if (state == 2) {
                ChatActivity.this.instantCameraView.cancel();
            }
        }

        public void needChangeVideoPreviewState(int state, float seekProgress) {
            if (ChatActivity.this.instantCameraView != null) {
                ChatActivity.this.instantCameraView.changeVideoPreviewState(state, seekProgress);
            }
        }

        public void needStartRecordAudio(int state) {
            int visibility = state == 0 ? 8 : 0;
            if (ChatActivity.this.overlayView.getVisibility() != visibility) {
                ChatActivity.this.overlayView.setVisibility(visibility);
            }
        }

        public void needShowMediaBanHint() {
            ChatActivity.this.showMediaBannedHint();
        }

        public void onStickersExpandedChange() {
            ChatActivity.this.checkRaiseSensors();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$25 */
    class CLASSNAME implements StickerPreviewViewerDelegate {
        CLASSNAME() {
        }

        public void sendSticker(Document sticker, Object parent) {
        }

        public boolean needSend() {
            return false;
        }

        public void openSet(InputStickerSet set) {
            if (set != null && ChatActivity.this.getParentActivity() != null) {
                TL_inputStickerSetID inputStickerSet = new TL_inputStickerSetID();
                inputStickerSet.access_hash = set.access_hash;
                inputStickerSet.var_id = set.var_id;
                ChatActivity.this.showDialog(new StickersAlert(ChatActivity.this.getParentActivity(), ChatActivity.this, inputStickerSet, null, ChatActivity.this.chatActivityEnterView));
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$2 */
    class CLASSNAME extends EmptyPhotoViewerProvider {
        CLASSNAME() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PlaceProviderObject object = null;
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
                        object = new PlaceProviderObject();
                        object.viewX = coords[0];
                        int i2 = coords[1];
                        if (VERSION.SDK_INT < 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        object.viewY = i2 - i;
                        object.parentView = ChatActivity.this.mentionListView;
                        object.imageReceiver = imageReceiver;
                        object.thumb = imageReceiver.getBitmapSafe();
                        object.radius = imageReceiver.getRoundRadius();
                    } else {
                        a++;
                    }
                }
            }
            return object;
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
            if (index >= 0 && index < ChatActivity.this.botContextResults.size()) {
                ChatActivity.this.sendBotInlineResult((BotInlineResult) ChatActivity.this.botContextResults.get(index));
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$31 */
    class CLASSNAME extends ViewOutlineProvider {
        CLASSNAME() {
        }

        @TargetApi(21)
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$34 */
    class CLASSNAME implements ChatAttachViewDelegate {
        CLASSNAME() {
        }

        public void didPressedButton(int button) {
            boolean z = false;
            if (ChatActivity.this.getParentActivity() != null && ChatActivity.this.chatAttachAlert != null) {
                if (ChatActivity.this.chatAttachAlert != null) {
                    ChatActivity.this.editingMessageObject = ChatActivity.this.chatAttachAlert.getEditingMessageObject();
                } else {
                    ChatActivity.this.editingMessageObject = null;
                }
                if (button == 8 || button == 7 || (button == 4 && !ChatActivity.this.chatAttachAlert.getSelectedPhotos().isEmpty())) {
                    if (button != 8) {
                        ChatActivity.this.chatAttachAlert.dismiss();
                    }
                    HashMap<Object, Object> selectedPhotos = ChatActivity.this.chatAttachAlert.getSelectedPhotos();
                    ArrayList<Object> selectedPhotosOrder = ChatActivity.this.chatAttachAlert.getSelectedPhotosOrder();
                    if (!selectedPhotos.isEmpty()) {
                        ArrayList<SendingMediaInfo> photos = new ArrayList();
                        for (int a = 0; a < selectedPhotosOrder.size(); a++) {
                            String charSequence;
                            ArrayList arrayList;
                            PhotoEntry photoEntry = (PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(a));
                            SendingMediaInfo info = new SendingMediaInfo();
                            if (photoEntry.imagePath != null) {
                                info.path = photoEntry.imagePath;
                            } else if (photoEntry.path != null) {
                                info.path = photoEntry.path;
                            }
                            info.isVideo = photoEntry.isVideo;
                            if (photoEntry.caption != null) {
                                charSequence = photoEntry.caption.toString();
                            } else {
                                charSequence = null;
                            }
                            info.caption = charSequence;
                            info.entities = photoEntry.entities;
                            if (photoEntry.stickers.isEmpty()) {
                                arrayList = null;
                            } else {
                                arrayList = new ArrayList(photoEntry.stickers);
                            }
                            info.masks = arrayList;
                            info.ttl = photoEntry.ttl;
                            info.videoEditedInfo = photoEntry.editedInfo;
                            info.canDeleteAfter = photoEntry.canDeleteAfter;
                            photos.add(info);
                            photoEntry.reset();
                        }
                        ChatActivity.this.fillEditingMediaWithCaption(((SendingMediaInfo) photos.get(0)).caption, ((SendingMediaInfo) photos.get(0)).entities);
                        long access$2300 = ChatActivity.this.dialog_id;
                        MessageObject access$2400 = ChatActivity.this.replyingMessageObject;
                        if (button == 4) {
                            z = true;
                        }
                        SendMessagesHelper.prepareSendingMedia(photos, access$2300, access$2400, null, z, SharedConfig.groupPhotosEnabled, ChatActivity.this.editingMessageObject);
                        ChatActivity.this.hideFieldPanel();
                        DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
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
            if (ChatActivity.this.chatActivityEnterView != null && !TextUtils.isEmpty(user.username)) {
                ChatActivity.this.chatActivityEnterView.setFieldText("@" + user.username + " ");
                ChatActivity.this.chatActivityEnterView.openKeyboard();
            }
        }

        public void onCameraOpened() {
            ChatActivity.this.chatActivityEnterView.closeKeyboard();
        }

        public boolean allowGroupPhotos() {
            return ChatActivity.this.allowGroupPhotos();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$36 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (animation.equals(ChatActivity.this.voiceHintAnimation)) {
                ChatActivity.this.voiceHintAnimation = null;
                ChatActivity.this.voiceHintHideRunnable = null;
                if (ChatActivity.this.voiceHintTextView != null) {
                    ChatActivity.this.voiceHintTextView.setVisibility(8);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (animation.equals(ChatActivity.this.voiceHintAnimation)) {
                ChatActivity.this.voiceHintHideRunnable = null;
                ChatActivity.this.voiceHintHideRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$37 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (animation.equals(ChatActivity.this.voiceHintAnimation)) {
                ChatActivity.this.voiceHintAnimation = null;
                AndroidUtilities.runOnUIThread(ChatActivity.this.voiceHintHideRunnable = new ChatActivity$37$$Lambda$0(this), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        }

        final /* synthetic */ void lambda$onAnimationEnd$0$ChatActivity$37() {
            ChatActivity.this.hideVoiceHint();
        }

        public void onAnimationCancel(Animator animation) {
            if (animation.equals(ChatActivity.this.voiceHintAnimation)) {
                ChatActivity.this.voiceHintAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$38 */
    class CLASSNAME extends AnimatorListenerAdapter {

        /* renamed from: org.telegram.ui.ChatActivity$38$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animation) {
                if (ChatActivity.this.mediaBanTooltip != null) {
                    ChatActivity.this.mediaBanTooltip.setVisibility(8);
                }
            }
        }

        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new ChatActivity$38$$Lambda$0(this), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }

        final /* synthetic */ void lambda$onAnimationEnd$0$ChatActivity$38() {
            if (ChatActivity.this.mediaBanTooltip != null) {
                AnimatorSet AnimatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.mediaBanTooltip, "alpha", new float[]{0.0f});
                AnimatorSet.playTogether(animatorArr);
                AnimatorSet.addListener(new CLASSNAME());
                AnimatorSet.setDuration(300);
                AnimatorSet.start();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$39 */
    class CLASSNAME extends AnimatorListenerAdapter {

        /* renamed from: org.telegram.ui.ChatActivity$39$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animation) {
                if (ChatActivity.this.gifHintTextView != null) {
                    ChatActivity.this.gifHintTextView.setVisibility(8);
                }
            }
        }

        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new ChatActivity$39$$Lambda$0(this), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        final /* synthetic */ void lambda$onAnimationEnd$0$ChatActivity$39() {
            if (ChatActivity.this.gifHintTextView != null) {
                AnimatorSet AnimatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.gifHintTextView, "alpha", new float[]{0.0f});
                AnimatorSet.playTogether(animatorArr);
                AnimatorSet.addListener(new CLASSNAME());
                AnimatorSet.setDuration(300);
                AnimatorSet.start();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$3 */
    class CLASSNAME implements OnItemLongClickListenerExtended {
        CLASSNAME() {
        }

        public boolean onItemClick(View view, int position, float x, float y) {
            ChatActivity.this.wasManualScroll = true;
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
                boolean outside = false;
                if (view instanceof ChatMessageCell) {
                    if (((ChatMessageCell) view).isInsideBackground(x, y)) {
                        outside = false;
                    } else {
                        outside = true;
                    }
                }
                ChatActivity.this.processRowSelect(view, outside);
            } else {
                ChatActivity.this.createMenu(view, false, true);
            }
            return true;
        }

        public void onLongClickRelease() {
        }

        public void onMove(float dx, float dy) {
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$40 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$41 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$42 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (animation.equals(ChatActivity.this.floatingDateAnimation)) {
                ChatActivity.this.floatingDateAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$43 */
    class CLASSNAME implements PhotoAlbumPickerActivityDelegate {
        CLASSNAME() {
        }

        public void didSelectPhotos(ArrayList<SendingMediaInfo> photos) {
            if (!photos.isEmpty()) {
                ChatActivity.this.fillEditingMediaWithCaption(((SendingMediaInfo) photos.get(0)).caption, ((SendingMediaInfo) photos.get(0)).entities);
                SendMessagesHelper.prepareSendingMedia(photos, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, false, SharedConfig.groupPhotosEnabled, ChatActivity.this.editingMessageObject);
                ChatActivity.this.hideFieldPanel();
                DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
            }
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
                FileLog.m13e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$44 */
    class CLASSNAME implements DocumentSelectActivityDelegate {
        CLASSNAME() {
        }

        public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files) {
            activity.lambda$checkDiscard$2$PollCreateActivity();
            ChatActivity.this.fillEditingMediaWithCaption(null, null);
            SendMessagesHelper.prepareSendingDocuments(files, files, null, null, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, ChatActivity.this.editingMessageObject);
            ChatActivity.this.hideFieldPanel();
            DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
        }

        public void startDocumentSelectActivity() {
            try {
                Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                if (VERSION.SDK_INT >= 18) {
                    photoPickerIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
                }
                photoPickerIntent.setType("*/*");
                ChatActivity.this.startActivityForResult(photoPickerIntent, 21);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void startMusicSelectActivity(BaseFragment parentFragment) {
            AudioSelectActivity fragment = new AudioSelectActivity();
            fragment.setDelegate(new ChatActivity$44$$Lambda$0(this, parentFragment));
            ChatActivity.this.presentFragment(fragment);
        }

        final /* synthetic */ void lambda$startMusicSelectActivity$0$ChatActivity$44(BaseFragment parentFragment, ArrayList audios) {
            parentFragment.removeSelfFromStack();
            ChatActivity.this.fillEditingMediaWithCaption(null, null);
            SendMessagesHelper.prepareSendingAudioDocuments(audios, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, ChatActivity.this.editingMessageObject);
            ChatActivity.this.hideFieldPanel();
            DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$45 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            ChatActivity.this.pagedownButtonCounter.setVisibility(4);
            ChatActivity.this.pagedownButton.setVisibility(4);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$46 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            ChatActivity.this.mentiondownButtonCounter.setVisibility(4);
            ChatActivity.this.mentiondownButton.setVisibility(4);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$49 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$4 */
    class CLASSNAME implements OnItemClickListenerExtended {
        CLASSNAME() {
        }

        public void onItemClick(View view, int position, float x, float y) {
            ChatActivity.this.wasManualScroll = true;
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
                boolean outside = false;
                if (view instanceof ChatMessageCell) {
                    if (((ChatMessageCell) view).isInsideBackground(x, y)) {
                        outside = false;
                    } else {
                        outside = true;
                    }
                }
                ChatActivity.this.processRowSelect(view, outside);
                return;
            }
            ChatActivity.this.createMenu(view, true, false);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$50 */
    class CLASSNAME implements Runnable {

        /* renamed from: org.telegram.ui.ChatActivity$50$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

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
        }

        CLASSNAME() {
        }

        public void run() {
            if (ChatActivity.this.hideAlertViewRunnable == this && ChatActivity.this.alertView.getTag() == null) {
                ChatActivity.this.alertView.setTag(Integer.valueOf(1));
                if (ChatActivity.this.alertViewAnimator != null) {
                    ChatActivity.this.alertViewAnimator.cancel();
                    ChatActivity.this.alertViewAnimator = null;
                }
                ChatActivity.this.alertViewAnimator = new AnimatorSet();
                AnimatorSet access$19800 = ChatActivity.this.alertViewAnimator;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.alertView, "translationY", new float[]{(float) (-AndroidUtilities.m9dp(50.0f))});
                access$19800.playTogether(animatorArr);
                ChatActivity.this.alertViewAnimator.setDuration(200);
                ChatActivity.this.alertViewAnimator.addListener(new CLASSNAME());
                ChatActivity.this.alertViewAnimator.start();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$51 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$52 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$53 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$54 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ChatActivity$55 */
    class CLASSNAME implements OnPreDrawListener {
        CLASSNAME() {
        }

        public boolean onPreDraw() {
            if (ChatActivity.this.avatarContainer != null) {
                ChatActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            return ChatActivity.this.fixLayoutInternal();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$5 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            int a;
            ArrayList<Integer> ids;
            int b;
            MessageObject messageObject;
            Bundle args;
            if (id == -1) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    for (a = 1; a >= 0; a--) {
                        ChatActivity.this.selectedMessagesIds[a].clear();
                        ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                        ChatActivity.this.selectedMessagesCanStarIds[a].clear();
                    }
                    ChatActivity.this.cantDeleteMessagesCount = 0;
                    ChatActivity.this.canEditMessagesCount = 0;
                    ChatActivity.this.actionBar.hideActionMode();
                    ChatActivity.this.updatePinnedMessageView(true);
                    ChatActivity.this.updateVisibleRows();
                    return;
                }
                ChatActivity.this.lambda$checkDiscard$2$PollCreateActivity();
            } else if (id == 10) {
                String str = TtmlNode.ANONYMOUS_REGION_ID;
                int previousUid = 0;
                for (a = 1; a >= 0; a--) {
                    ids = new ArrayList();
                    for (b = 0; b < ChatActivity.this.selectedMessagesCanCopyIds[a].size(); b++) {
                        ids.add(Integer.valueOf(ChatActivity.this.selectedMessagesCanCopyIds[a].keyAt(b)));
                    }
                    if (ChatActivity.this.currentEncryptedChat == null) {
                        Collections.sort(ids);
                    } else {
                        Collections.sort(ids, Collections.reverseOrder());
                    }
                    for (b = 0; b < ids.size(); b++) {
                        messageObject = (MessageObject) ChatActivity.this.selectedMessagesCanCopyIds[a].get(((Integer) ids.get(b)).intValue());
                        if (str.length() != 0) {
                            str = str + "\n\n";
                        }
                        StringBuilder append = new StringBuilder().append(str);
                        ChatActivity chatActivity = ChatActivity.this;
                        boolean z = ChatActivity.this.currentUser == null || !ChatActivity.this.currentUser.self;
                        str = append.append(chatActivity.getMessageContent(messageObject, previousUid, z)).toString();
                        previousUid = messageObject.messageOwner.from_id;
                    }
                }
                if (str.length() != 0) {
                    AndroidUtilities.addToClipboard(str);
                }
                for (a = 1; a >= 0; a--) {
                    ChatActivity.this.selectedMessagesIds[a].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                    ChatActivity.this.selectedMessagesCanStarIds[a].clear();
                }
                ChatActivity.this.cantDeleteMessagesCount = 0;
                ChatActivity.this.canEditMessagesCount = 0;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (id == 12) {
                if (ChatActivity.this.getParentActivity() != null) {
                    ChatActivity.this.createDeleteMessagesAlert(null, null);
                }
            } else if (id == 11) {
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
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
                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    if (id == 15) {
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                    } else if (isChat) {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$5$$Lambda$0(this, id, isChat));
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
                    args.putInt("user_id", ChatActivity.this.currentUser.var_id);
                    args.putBoolean("addContact", true);
                    ChatActivity.this.presentFragment(new ContactAddActivity(args));
                }
            } else if (id == 18) {
                ChatActivity.this.toggleMute(false);
            } else if (id == 24) {
                try {
                    DataQuery.getInstance(ChatActivity.this.currentAccount).installShortcut((long) ChatActivity.this.currentUser.var_id);
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            } else if (id == 21) {
                AlertsCreator.createReportAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.dialog_id, 0, ChatActivity.this);
            } else if (id == 19) {
                messageObject = null;
                a = 1;
                while (a >= 0) {
                    if (messageObject == null && ChatActivity.this.selectedMessagesIds[a].size() == 1) {
                        ids = new ArrayList();
                        for (b = 0; b < ChatActivity.this.selectedMessagesIds[a].size(); b++) {
                            ids.add(Integer.valueOf(ChatActivity.this.selectedMessagesIds[a].keyAt(b)));
                        }
                        messageObject = (MessageObject) ChatActivity.this.messagesDict[a].get(((Integer) ids.get(0)).intValue());
                    }
                    ChatActivity.this.selectedMessagesIds[a].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                    ChatActivity.this.selectedMessagesCanStarIds[a].clear();
                    a--;
                }
                if (messageObject != null && (messageObject.messageOwner.var_id > 0 || (messageObject.messageOwner.var_id < 0 && ChatActivity.this.currentEncryptedChat != null))) {
                    ChatActivity.this.showFieldPanelForReply(true, messageObject);
                }
                ChatActivity.this.cantDeleteMessagesCount = 0;
                ChatActivity.this.canEditMessagesCount = 0;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (id == 22) {
                for (a = 0; a < 2; a++) {
                    for (b = 0; b < ChatActivity.this.selectedMessagesCanStarIds[a].size(); b++) {
                        MessageObject msg = (MessageObject) ChatActivity.this.selectedMessagesCanStarIds[a].valueAt(b);
                        DataQuery.getInstance(ChatActivity.this.currentAccount).addRecentSticker(2, msg, msg.getDocument(), (int) (System.currentTimeMillis() / 1000), !ChatActivity.this.hasUnfavedSelected);
                    }
                }
                for (a = 1; a >= 0; a--) {
                    ChatActivity.this.selectedMessagesIds[a].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                    ChatActivity.this.selectedMessagesCanStarIds[a].clear();
                }
                ChatActivity.this.cantDeleteMessagesCount = 0;
                ChatActivity.this.canEditMessagesCount = 0;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (id == 23) {
                messageObject = null;
                a = 1;
                while (a >= 0) {
                    if (messageObject == null && ChatActivity.this.selectedMessagesIds[a].size() == 1) {
                        ids = new ArrayList();
                        for (b = 0; b < ChatActivity.this.selectedMessagesIds[a].size(); b++) {
                            ids.add(Integer.valueOf(ChatActivity.this.selectedMessagesIds[a].keyAt(b)));
                        }
                        messageObject = (MessageObject) ChatActivity.this.messagesDict[a].get(((Integer) ids.get(0)).intValue());
                    }
                    ChatActivity.this.selectedMessagesIds[a].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                    ChatActivity.this.selectedMessagesCanStarIds[a].clear();
                    a--;
                }
                ChatActivity.this.startReplyOnTextChange = false;
                ChatActivity.this.startEditingMessageObject(messageObject);
                ChatActivity.this.cantDeleteMessagesCount = 0;
                ChatActivity.this.canEditMessagesCount = 0;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (id == 14) {
                if (ChatActivity.this.chatAttachAlert != null) {
                    ChatActivity.this.chatAttachAlert.setEditingMessageObject(null);
                }
                ChatActivity.this.openAttachMenu();
            } else if (id == 30) {
                SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).sendMessage("/help", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            } else if (id == 31) {
                SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).sendMessage("/settings", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            } else if (id == ChatActivity.search) {
                ChatActivity.this.openSearchWithText(null);
            } else if (id == 32) {
                if (ChatActivity.this.currentUser != null && ChatActivity.this.getParentActivity() != null) {
                    VoIPHelper.startCall(ChatActivity.this.currentUser, ChatActivity.this.getParentActivity(), MessagesController.getInstance(ChatActivity.this.currentAccount).getUserFull(ChatActivity.this.currentUser.var_id));
                }
            } else if (id == 50) {
                if (ChatActivity.this.chatActivityEnterView != null) {
                    ChatActivity.this.chatActivityEnterView.getEditField().setSelectionOverride(ChatActivity.this.editTextStart, ChatActivity.this.editTextEnd);
                    ChatActivity.this.chatActivityEnterView.getEditField().makeSelectedBold();
                }
            } else if (id == 51) {
                if (ChatActivity.this.chatActivityEnterView != null) {
                    ChatActivity.this.chatActivityEnterView.getEditField().setSelectionOverride(ChatActivity.this.editTextStart, ChatActivity.this.editTextEnd);
                    ChatActivity.this.chatActivityEnterView.getEditField().makeSelectedItalic();
                }
            } else if (id == 52) {
                if (ChatActivity.this.chatActivityEnterView != null) {
                    ChatActivity.this.chatActivityEnterView.getEditField().setSelectionOverride(ChatActivity.this.editTextStart, ChatActivity.this.editTextEnd);
                    ChatActivity.this.chatActivityEnterView.getEditField().makeSelectedMono();
                }
            } else if (id == 53) {
                if (ChatActivity.this.chatActivityEnterView != null) {
                    ChatActivity.this.chatActivityEnterView.getEditField().setSelectionOverride(ChatActivity.this.editTextStart, ChatActivity.this.editTextEnd);
                    ChatActivity.this.chatActivityEnterView.getEditField().makeSelectedUrl();
                }
            } else if (id == 54 && ChatActivity.this.chatActivityEnterView != null) {
                ChatActivity.this.chatActivityEnterView.getEditField().setSelectionOverride(ChatActivity.this.editTextStart, ChatActivity.this.editTextEnd);
                ChatActivity.this.chatActivityEnterView.getEditField().makeSelectedRegular();
            }
        }

        final /* synthetic */ void lambda$onItemClick$0$ChatActivity$5(int id, boolean isChat, DialogInterface dialogInterface, int i) {
            if (id != 15) {
                if (!isChat) {
                    MessagesController.getInstance(ChatActivity.this.currentAccount).deleteDialog(ChatActivity.this.dialog_id, 0);
                } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                    MessagesController.getInstance(ChatActivity.this.currentAccount).deleteDialog(ChatActivity.this.dialog_id, 0);
                } else {
                    MessagesController.getInstance(ChatActivity.this.currentAccount).deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance(ChatActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChatActivity.this.currentAccount).getClientUserId())), null);
                }
                ChatActivity.this.lambda$checkDiscard$2$PollCreateActivity();
                return;
            }
            if (ChatActivity.this.chatInfo != null && ChatActivity.this.chatInfo.pinned_msg_id != 0) {
                MessagesController.getNotificationsSettings(ChatActivity.this.currentAccount).edit().putInt("pin_" + ChatActivity.this.dialog_id, ChatActivity.this.chatInfo.pinned_msg_id).commit();
                ChatActivity.this.updatePinnedMessageView(true);
            } else if (!(ChatActivity.this.userInfo == null || ChatActivity.this.userInfo.pinned_msg_id == 0)) {
                MessagesController.getNotificationsSettings(ChatActivity.this.currentAccount).edit().putInt("pin_" + ChatActivity.this.dialog_id, ChatActivity.this.userInfo.pinned_msg_id).commit();
                ChatActivity.this.updatePinnedMessageView(true);
            }
            MessagesController.getInstance(ChatActivity.this.currentAccount).deleteDialog(ChatActivity.this.dialog_id, 1);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$6 */
    class CLASSNAME extends ActionBarMenuItemSearchListener {
        boolean searchWas;

        CLASSNAME() {
        }

        public void onSearchCollapse() {
            ChatActivity.this.searchCalendarButton.setVisibility(0);
            if (ChatActivity.this.searchUserButton != null) {
                ChatActivity.this.searchUserButton.setVisibility(0);
            }
            if (ChatActivity.this.searchingForUser) {
                ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag(null, 0, null, false);
                ChatActivity.this.searchingForUser = false;
            }
            ChatActivity.this.mentionLayoutManager.setReverseLayout(false);
            ChatActivity.this.mentionsAdapter.setSearchingMentions(false);
            ChatActivity.this.searchingUserMessages = null;
            ChatActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
            ChatActivity.this.searchItem.setSearchFieldCaption(null);
            ChatActivity.this.avatarContainer.setVisibility(0);
            if (ChatActivity.this.editTextItem.getTag() != null) {
                if (ChatActivity.this.headerItem != null) {
                    ChatActivity.this.headerItem.setVisibility(8);
                    ChatActivity.this.editTextItem.setVisibility(0);
                    ChatActivity.this.attachItem.setVisibility(8);
                }
            } else if (ChatActivity.this.chatActivityEnterView.hasText()) {
                if (ChatActivity.this.headerItem != null) {
                    ChatActivity.this.headerItem.setVisibility(8);
                    ChatActivity.this.editTextItem.setVisibility(8);
                    ChatActivity.this.attachItem.setVisibility(0);
                }
            } else if (ChatActivity.this.headerItem != null) {
                ChatActivity.this.headerItem.setVisibility(0);
                ChatActivity.this.editTextItem.setVisibility(8);
                ChatActivity.this.attachItem.setVisibility(8);
            }
            ChatActivity.this.searchItem.setVisibility(8);
            ChatActivity.this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (this.searchWas) {
                ChatActivity.this.scrollToLastMessage(false);
            }
            ChatActivity.this.updateBottomOverlay();
            ChatActivity.this.updatePinnedMessageView(true);
            ChatActivity.this.updateVisibleRows();
        }

        public void onSearchExpand() {
            if (ChatActivity.this.openSearchKeyboard) {
                AndroidUtilities.runOnUIThread(new ChatActivity$6$$Lambda$0(this), 300);
            }
        }

        final /* synthetic */ void lambda$onSearchExpand$0$ChatActivity$6() {
            this.searchWas = false;
            ChatActivity.this.searchItem.getSearchField().requestFocus();
            AndroidUtilities.showKeyboard(ChatActivity.this.searchItem.getSearchField());
        }

        public void onSearchPressed(EditText editText) {
            this.searchWas = true;
            ChatActivity.this.updateSearchButtons(0, 0, -1);
            DataQuery.getInstance(ChatActivity.this.currentAccount).searchMessagesInChat(editText.getText().toString(), ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, 0, ChatActivity.this.searchingUserMessages);
        }

        public void onTextChanged(EditText editText) {
            if (ChatActivity.this.searchingForUser) {
                ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag("@" + editText.getText().toString(), 0, ChatActivity.this.messages, true);
            } else if (!ChatActivity.this.searchingForUser && ChatActivity.this.searchingUserMessages == null && ChatActivity.this.searchUserButton != null && TextUtils.equals(editText.getText(), LocaleController.getString("SearchFrom", R.string.SearchFrom))) {
                ChatActivity.this.searchUserButton.callOnClick();
            }
        }

        public void onCaptionCleared() {
            if (ChatActivity.this.searchingUserMessages != null) {
                ChatActivity.this.searchUserButton.callOnClick();
                return;
            }
            if (ChatActivity.this.searchingForUser) {
                ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag(null, 0, null, false);
                ChatActivity.this.searchingForUser = false;
                ChatActivity.this.searchItem.setSearchFieldText(TtmlNode.ANONYMOUS_REGION_ID, true);
            }
            ChatActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
            ChatActivity.this.searchCalendarButton.setVisibility(0);
            ChatActivity.this.searchUserButton.setVisibility(0);
            ChatActivity.this.searchingUserMessages = null;
        }

        public boolean forceShowClear() {
            return ChatActivity.this.searchingForUser;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter */
    public class ChatActivityAdapter extends Adapter {
        private int botInfoRow = -1;
        private boolean isBot;
        private int loadingDownRow;
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        /* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$1 */
        class CLASSNAME implements ChatMessageCellDelegate {
            CLASSNAME() {
            }

            public void didPressShare(ChatMessageCell cell) {
                if (ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        ChatActivity.this.chatActivityEnterView.closeKeyboard();
                    }
                    MessageObject messageObject = cell.getMessageObject();
                    if (!UserObject.isUserSelf(ChatActivity.this.currentUser) || messageObject.messageOwner.fwd_from.saved_from_peer == null) {
                        ArrayList<MessageObject> arrayList = null;
                        if (messageObject.getGroupId() != 0) {
                            GroupedMessages groupedMessages = (GroupedMessages) ChatActivity.this.groupedMessagesMap.get(messageObject.getGroupId());
                            if (groupedMessages != null) {
                                arrayList = groupedMessages.messages;
                            }
                        }
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                            arrayList.add(messageObject);
                        }
                        ChatActivity chatActivity = ChatActivity.this;
                        Context access$20800 = ChatActivityAdapter.this.mContext;
                        boolean z = ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup && ChatActivity.this.currentChat.username != null && ChatActivity.this.currentChat.username.length() > 0;
                        chatActivity.showDialog(new ShareAlert(access$20800, arrayList, null, z, null, false));
                        return;
                    }
                    Bundle args = new Bundle();
                    if (messageObject.messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
                        args.putInt("chat_id", messageObject.messageOwner.fwd_from.saved_from_peer.channel_id);
                    } else if (messageObject.messageOwner.fwd_from.saved_from_peer.chat_id != 0) {
                        args.putInt("chat_id", messageObject.messageOwner.fwd_from.saved_from_peer.chat_id);
                    } else if (messageObject.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
                        args.putInt("user_id", messageObject.messageOwner.fwd_from.saved_from_peer.user_id);
                    }
                    args.putInt("message_id", messageObject.messageOwner.fwd_from.saved_from_msg_id);
                    if (MessagesController.getInstance(ChatActivity.this.currentAccount).checkCanOpenChat(args, ChatActivity.this)) {
                        ChatActivity.this.presentFragment(new ChatActivity(args));
                    }
                }
            }

            public boolean needPlayMessage(MessageObject messageObject) {
                if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
                    return messageObject.isMusic() ? MediaController.getInstance().setPlaylist(ChatActivity.this.messages, messageObject) : false;
                } else {
                    boolean result = MediaController.getInstance().playMessage(messageObject);
                    MediaController.getInstance().setVoiceMessagesPlaylist(result ? ChatActivity.this.createVoiceMessagesPlaylist(messageObject, false) : null, false);
                    return result;
                }
            }

            public void didPressChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(cell, true);
                } else if (chat != null && chat != ChatActivity.this.currentChat) {
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.var_id);
                    if (postId != 0) {
                        args.putInt("message_id", postId);
                    }
                    if (MessagesController.getInstance(ChatActivity.this.currentAccount).checkCanOpenChat(args, ChatActivity.this, cell.getMessageObject())) {
                        ChatActivity.this.presentFragment(new ChatActivity(args));
                    }
                }
            }

            public void didPressOther(ChatMessageCell cell) {
                if (cell.getMessageObject().type != 16) {
                    ChatActivity.this.createMenu(cell, true, false, false);
                } else if (ChatActivity.this.currentUser != null) {
                    VoIPHelper.startCall(ChatActivity.this.currentUser, ChatActivity.this.getParentActivity(), MessagesController.getInstance(ChatActivity.this.currentAccount).getUserFull(ChatActivity.this.currentUser.var_id));
                }
            }

            public void didPressUserAvatar(ChatMessageCell cell, User user) {
                boolean z = true;
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(cell, true);
                } else if (user != null && user.var_id != UserConfig.getInstance(ChatActivity.this.currentAccount).getClientUserId()) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", user.var_id);
                    ProfileActivity fragment = new ProfileActivity(args);
                    if (ChatActivity.this.currentUser == null || ChatActivity.this.currentUser.var_id != user.var_id) {
                        z = false;
                    }
                    fragment.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(fragment);
                }
            }

            public void didPressBotButton(ChatMessageCell cell, KeyboardButton button) {
                if (ChatActivity.this.getParentActivity() == null) {
                    return;
                }
                if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (button instanceof TL_keyboardButtonSwitchInline) || (button instanceof TL_keyboardButtonCallback) || (button instanceof TL_keyboardButtonGame) || (button instanceof TL_keyboardButtonUrl) || (button instanceof TL_keyboardButtonBuy)) {
                    ChatActivity.this.chatActivityEnterView.didPressedBotButton(button, cell.getMessageObject(), cell.getMessageObject());
                }
            }

            public void didPressVoteButton(ChatMessageCell cell, TL_pollAnswer button) {
                SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).sendVote(cell.getMessageObject(), button, null);
            }

            public void didPressCancelSendButton(ChatMessageCell cell) {
                MessageObject message = cell.getMessageObject();
                if (message.messageOwner.send_state != 0) {
                    SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).cancelSendingMessage(message);
                }
            }

            public void didLongPress(ChatMessageCell cell) {
                ChatActivity.this.createMenu(cell, false, false);
            }

            public boolean canPerformActions() {
                return (ChatActivity.this.actionBar == null || ChatActivity.this.actionBar.isActionModeShowed()) ? false : true;
            }

            public void didPressUrl(MessageObject messageObject, CharacterStyle url, boolean longPress) {
                if (url != null) {
                    if (url instanceof URLSpanMono) {
                        ((URLSpanMono) url).copyToClipboard();
                        Toast.makeText(ChatActivity.this.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                    } else if (url instanceof URLSpanUserMention) {
                        User user = MessagesController.getInstance(ChatActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
                        if (user != null) {
                            MessagesController.openChatOrProfileWith(user, null, ChatActivity.this, 0, false);
                        }
                    } else if (url instanceof URLSpanNoUnderline) {
                        String str = ((URLSpanNoUnderline) url).getURL();
                        if (str.startsWith("@")) {
                            String username = str.substring(1).toLowerCase();
                            if ((ChatActivity.this.currentChat == null || TextUtils.isEmpty(ChatActivity.this.currentChat.username) || !username.equals(ChatActivity.this.currentChat.username.toLowerCase())) && (ChatActivity.this.currentUser == null || TextUtils.isEmpty(ChatActivity.this.currentUser.username) || !username.equals(ChatActivity.this.currentUser.username.toLowerCase()))) {
                                MessagesController.getInstance(ChatActivity.this.currentAccount).openByUserName(username, ChatActivity.this, 0);
                                return;
                            }
                            Bundle args = new Bundle();
                            if (ChatActivity.this.currentChat != null) {
                                args.putInt("chat_id", ChatActivity.this.currentChat.var_id);
                            } else if (ChatActivity.this.currentUser != null) {
                                args.putInt("user_id", ChatActivity.this.currentUser.var_id);
                                if (ChatActivity.this.currentEncryptedChat != null) {
                                    args.putLong("dialog_id", ChatActivity.this.dialog_id);
                                }
                            }
                            ProfileActivity fragment = new ProfileActivity(args);
                            fragment.setPlayProfileAnimation(true);
                            fragment.setChatInfo(ChatActivity.this.chatInfo);
                            fragment.setUserInfo(ChatActivity.this.userInfo);
                            ChatActivity.this.presentFragment(fragment);
                        } else if (str.startsWith("#") || str.startsWith("$")) {
                            if (ChatObject.isChannel(ChatActivity.this.currentChat)) {
                                ChatActivity.this.openSearchWithText(str);
                                return;
                            }
                            DialogsActivity fragment2 = new DialogsActivity(null);
                            fragment2.setSearchString(str);
                            ChatActivity.this.presentFragment(fragment2);
                        } else if (str.startsWith("/") && URLSpanBotCommand.enabled) {
                            ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                            boolean z = ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup;
                            chatActivityEnterView.setCommand(messageObject, str, longPress, z);
                            if (!longPress && ChatActivity.this.chatActivityEnterView.getFieldText() == null) {
                                ChatActivity.this.hideFieldPanel();
                            }
                        }
                    } else {
                        String urlFinal = ((URLSpan) url).getURL();
                        if (longPress) {
                            BottomSheet.Builder builder = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                            builder.setTitle(urlFinal);
                            builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new ChatActivity$ChatActivityAdapter$1$$Lambda$0(this, urlFinal));
                            ChatActivity.this.showDialog(builder.create());
                        } else if ((url instanceof URLSpanReplacement) && (urlFinal == null || !urlFinal.startsWith("mailto:"))) {
                            ChatActivity.this.showOpenUrlAlert(urlFinal, true);
                        } else if (url instanceof URLSpan) {
                            if (!(!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.cached_page == null)) {
                                String lowerUrl = urlFinal.toLowerCase();
                                String lowerUrl2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                if ((lowerUrl.contains("telegra.ph") || lowerUrl.contains("t.me/iv")) && (lowerUrl.contains(lowerUrl2) || lowerUrl2.contains(lowerUrl))) {
                                    ArticleViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity(), ChatActivity.this);
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

            final /* synthetic */ void lambda$didPressUrl$0$ChatActivity$ChatActivityAdapter$1(String urlFinal, DialogInterface dialog, int which) {
                boolean z = true;
                if (which == 0) {
                    Context parentActivity = ChatActivity.this.getParentActivity();
                    if (ChatActivity.this.inlineReturn != 0) {
                        z = false;
                    }
                    Browser.openUrl(parentActivity, urlFinal, z, false);
                } else if (which == 1) {
                    String url1 = urlFinal;
                    if (url1.startsWith("mailto:")) {
                        url1 = url1.substring(7);
                    } else if (url1.startsWith("tel:")) {
                        url1 = url1.substring(4);
                    }
                    AndroidUtilities.addToClipboard(url1);
                }
            }

            public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
                try {
                    EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, title, description, originalUrl, url, w, h);
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }

            public void didPressReplyMessage(ChatMessageCell cell, int id) {
                int i;
                MessageObject messageObject = cell.getMessageObject();
                ChatActivity chatActivity = ChatActivity.this;
                int id2 = messageObject.getId();
                if (messageObject.getDialogId() == ChatActivity.this.mergeDialogId) {
                    i = 1;
                } else {
                    i = 0;
                }
                chatActivity.scrollToMessageId(id, id2, true, i, false);
            }

            public void didPressViaBot(ChatMessageCell cell, String username) {
                if (ChatActivity.this.bottomOverlayChat != null && ChatActivity.this.bottomOverlayChat.getVisibility() == 0) {
                    return;
                }
                if ((ChatActivity.this.bottomOverlay == null || ChatActivity.this.bottomOverlay.getVisibility() != 0) && ChatActivity.this.chatActivityEnterView != null && username != null && username.length() > 0) {
                    ChatActivity.this.chatActivityEnterView.setFieldText("@" + username + " ");
                    ChatActivity.this.chatActivityEnterView.openKeyboard();
                }
            }

            public void didPressImage(ChatMessageCell cell) {
                MessageObject message = cell.getMessageObject();
                if (message.isSendError()) {
                    ChatActivity.this.createMenu(cell, false, false);
                } else if (!message.isSending()) {
                    File f;
                    if (message.needDrawBluredPreview()) {
                        if (ChatActivity.this.sendSecretMessageRead(message)) {
                            cell.invalidate();
                        }
                        SecretMediaViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        SecretMediaViewer.getInstance().openMedia(message, ChatActivity.this.photoViewerProvider);
                    } else if (message.type == 13) {
                        ChatActivity chatActivity = ChatActivity.this;
                        Context parentActivity = ChatActivity.this.getParentActivity();
                        BaseFragment baseFragment = ChatActivity.this;
                        InputStickerSet inputStickerSet = message.getInputStickerSet();
                        StickersAlertDelegate stickersAlertDelegate = (ChatActivity.this.bottomOverlayChat.getVisibility() == 0 || !ChatObject.canSendStickers(ChatActivity.this.currentChat)) ? null : ChatActivity.this.chatActivityEnterView;
                        chatActivity.showDialog(new StickersAlert(parentActivity, baseFragment, inputStickerSet, null, stickersAlertDelegate));
                    } else if (message.isVideo() || message.type == 1 || ((message.type == 0 && !message.isWebpageDocument()) || message.isGif())) {
                        if (message.isVideo()) {
                            ChatActivity.this.sendSecretMessageRead(message);
                        }
                        PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        if (PhotoViewer.getInstance().openPhoto(message, message.type != 0 ? ChatActivity.this.dialog_id : 0, message.type != 0 ? ChatActivity.this.mergeDialogId : 0, ChatActivity.this.photoViewerProvider)) {
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
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                            ChatActivity.this.alertUserOpenError(message);
                        }
                    } else if (message.type == 4) {
                        if (!AndroidUtilities.isGoogleMapsInstalled(ChatActivity.this)) {
                            return;
                        }
                        LocationActivity fragment;
                        if (message.isLiveLocation()) {
                            fragment = new LocationActivity(2);
                            fragment.setMessageObject(message);
                            fragment.setDelegate(ChatActivity.this);
                            ChatActivity.this.presentFragment(fragment);
                            return;
                        }
                        fragment = new LocationActivity(ChatActivity.this.currentEncryptedChat == null ? 3 : 0);
                        fragment.setMessageObject(message);
                        fragment.setDelegate(ChatActivity.this);
                        ChatActivity.this.presentFragment(fragment);
                    } else if (message.type == 9 || message.type == 0) {
                        if (message.getDocumentName().toLowerCase().endsWith("attheme")) {
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
                            if (ChatActivity.this.chatLayoutManager != null) {
                                int lastPosition = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                                if (lastPosition != 0) {
                                    ChatActivity.this.scrollToPositionOnRecreate = lastPosition;
                                    Holder holder = (Holder) ChatActivity.this.chatListView.findViewHolderForAdapterPosition(ChatActivity.this.scrollToPositionOnRecreate);
                                    if (holder != null) {
                                        ChatActivity.this.scrollToOffsetOnRecreate = (ChatActivity.this.chatListView.getMeasuredHeight() - holder.itemView.getBottom()) - ChatActivity.this.chatListView.getPaddingBottom();
                                    } else {
                                        ChatActivity.this.scrollToPositionOnRecreate = -1;
                                    }
                                } else {
                                    ChatActivity.this.scrollToPositionOnRecreate = -1;
                                }
                            }
                            ThemeInfo themeInfo = Theme.applyThemeFile(locFile, message.getDocumentName(), true);
                            if (themeInfo != null) {
                                ChatActivity.this.presentFragment(new ThemePreviewActivity(locFile, themeInfo));
                                return;
                            }
                            ChatActivity.this.scrollToPositionOnRecreate = -1;
                        }
                        boolean handled = false;
                        if (message.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                            PhotoViewer.getInstance().openPhoto(message, message.type != 0 ? ChatActivity.this.dialog_id : 0, message.type != 0 ? ChatActivity.this.mergeDialogId : 0, ChatActivity.this.photoViewerProvider);
                            handled = true;
                        }
                        if (!handled) {
                            try {
                                AndroidUtilities.openForView(message, ChatActivity.this.getParentActivity());
                            } catch (Throwable e2) {
                                FileLog.m13e(e2);
                                ChatActivity.this.alertUserOpenError(message);
                            }
                        }
                    }
                }
            }

            public void didPressInstantButton(ChatMessageCell cell, int type) {
                MessageObject messageObject = cell.getMessageObject();
                if (type == 0) {
                    if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                        ArticleViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity(), ChatActivity.this);
                        ArticleViewer.getInstance().open(messageObject);
                    }
                } else if (type == 5) {
                    ChatActivity.this.openVCard(messageObject.messageOwner.media.vcard, messageObject.messageOwner.media.first_name, messageObject.messageOwner.media.last_name);
                } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null) {
                    Browser.openUrl(ChatActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                }
            }

            public boolean isChatAdminCell(int uid) {
                if (ChatObject.isChannel(ChatActivity.this.currentChat) && ChatActivity.this.currentChat.megagroup) {
                    return MessagesController.getInstance(ChatActivity.this.currentAccount).isChannelAdmin(ChatActivity.this.currentChat.var_id, uid);
                }
                return false;
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$2 */
        class CLASSNAME implements ChatActionCellDelegate {
            CLASSNAME() {
            }

            public void didClickedImage(ChatActionCell cell) {
                MessageObject message = cell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640);
                if (photoSize != null) {
                    PhotoViewer.getInstance().openPhoto(photoSize.location, ChatActivity.this.photoViewerProvider);
                    return;
                }
                PhotoViewer.getInstance().openPhoto(message, 0, 0, ChatActivity.this.photoViewerProvider);
            }

            public void didLongPressed(ChatActionCell cell) {
                ChatActivity.this.createMenu(cell, false, false);
            }

            public void needOpenUserProfile(int uid) {
                Bundle args;
                if (uid < 0) {
                    args = new Bundle();
                    args.putInt("chat_id", -uid);
                    if (MessagesController.getInstance(ChatActivity.this.currentAccount).checkCanOpenChat(args, ChatActivity.this)) {
                        ChatActivity.this.presentFragment(new ChatActivity(args));
                    }
                } else if (uid != UserConfig.getInstance(ChatActivity.this.currentAccount).getClientUserId()) {
                    args = new Bundle();
                    args.putInt("user_id", uid);
                    if (ChatActivity.this.currentEncryptedChat != null && uid == ChatActivity.this.currentUser.var_id) {
                        args.putLong("dialog_id", ChatActivity.this.dialog_id);
                    }
                    ProfileActivity fragment = new ProfileActivity(args);
                    boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.var_id == uid;
                    fragment.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(fragment);
                }
            }

            public void didPressedReplyMessage(ChatActionCell cell, int id) {
                int i;
                MessageObject messageObject = cell.getMessageObject();
                ChatActivity chatActivity = ChatActivity.this;
                int id2 = messageObject.getId();
                if (messageObject.getDialogId() == ChatActivity.this.mergeDialogId) {
                    i = 1;
                } else {
                    i = 0;
                }
                chatActivity.scrollToMessageId(id, id2, true, i, false);
            }

            public void didPressedBotButton(MessageObject messageObject, KeyboardButton button) {
                if (ChatActivity.this.getParentActivity() == null) {
                    return;
                }
                if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (button instanceof TL_keyboardButtonSwitchInline) || (button instanceof TL_keyboardButtonCallback) || (button instanceof TL_keyboardButtonGame) || (button instanceof TL_keyboardButtonUrl) || (button instanceof TL_keyboardButtonBuy)) {
                    ChatActivity.this.chatActivityEnterView.didPressedBotButton(button, messageObject, messageObject);
                }
            }
        }

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
            boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.bot;
            this.isBot = z;
        }

        public void updateRows() {
            int i;
            this.rowCount = 0;
            if (ChatActivity.this.messages.isEmpty()) {
                this.loadingUpRow = -1;
                this.loadingDownRow = -1;
                this.messagesStartRow = -1;
                this.messagesEndRow = -1;
            } else {
                if (ChatActivity.this.forwardEndReached[0] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.forwardEndReached[1])) {
                    this.loadingDownRow = -1;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.loadingDownRow = i;
                }
                this.messagesStartRow = this.rowCount;
                this.rowCount += ChatActivity.this.messages.size();
                this.messagesEndRow = this.rowCount;
                if (ChatActivity.this.endReached[0] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.endReached[1])) {
                    this.loadingUpRow = -1;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.loadingUpRow = i;
                }
            }
            if (ChatActivity.this.currentUser == null || !ChatActivity.this.currentUser.bot) {
                this.botInfoRow = -1;
                return;
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.botInfoRow = i;
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
                chatMessageCell.setDelegate(new CLASSNAME());
                if (ChatActivity.this.currentEncryptedChat == null) {
                    chatMessageCell.setAllowAssistant(true);
                }
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new CLASSNAME());
            } else if (viewType == 2) {
                view = new ChatUnreadCell(this.mContext);
            } else if (viewType == 3) {
                view = new BotHelpCell(this.mContext);
                ((BotHelpCell) view).setDelegate(new ChatActivity$ChatActivityAdapter$$Lambda$0(this));
            } else if (viewType == 4) {
                view = new ChatLoadingCell(this.mContext);
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        final /* synthetic */ void lambda$onCreateViewHolder$0$ChatActivity$ChatActivityAdapter(String url) {
            if (url.startsWith("@")) {
                MessagesController.getInstance(ChatActivity.this.currentAccount).openByUserName(url.substring(1), ChatActivity.this, 0);
            } else if (url.startsWith("#") || url.startsWith("$")) {
                DialogsActivity fragment = new DialogsActivity(null);
                fragment.setSearchString(url);
                ChatActivity.this.presentFragment(fragment);
            } else if (url.startsWith("/")) {
                ChatActivity.this.chatActivityEnterView.setCommand(null, url, false, false);
                if (ChatActivity.this.chatActivityEnterView.getFieldText() == null) {
                    ChatActivity.this.hideFieldPanel();
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:91:0x0377  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == this.botInfoRow) {
                String str;
                BotHelpCell helpView = holder.itemView;
                if (ChatActivity.this.botInfo.size() != 0) {
                    str = ((BotInfo) ChatActivity.this.botInfo.get(ChatActivity.this.currentUser.var_id)).description;
                } else {
                    str = null;
                }
                helpView.setText(str);
                return;
            }
            if (position == this.loadingDownRow || position == this.loadingUpRow) {
                holder.itemView.setProgressVisible(ChatActivity.this.loadsCount > 1);
                return;
            }
            if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                MessageObject message = (MessageObject) ChatActivity.this.messages.get(position - this.messagesStartRow);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    int prevPosition;
                    int nextPosition;
                    int index;
                    final ChatMessageCell messageCell = (ChatMessageCell) view;
                    boolean z = ChatActivity.this.currentChat != null || UserObject.isUserSelf(ChatActivity.this.currentUser);
                    messageCell.isChat = z;
                    boolean pinnedBottom = false;
                    boolean pinnedTop = false;
                    GroupedMessages groupedMessages = ChatActivity.this.getValidGroupedMessage(message);
                    if (groupedMessages != null) {
                        GroupedMessagePosition pos = (GroupedMessagePosition) groupedMessages.positions.get(message);
                        if (pos != null) {
                            if ((pos.flags & 4) != 0) {
                                prevPosition = (groupedMessages.posArray.indexOf(pos) + position) + 1;
                            } else {
                                pinnedTop = true;
                                prevPosition = -100;
                            }
                            if ((pos.flags & 8) != 0) {
                                nextPosition = (position - groupedMessages.posArray.size()) + groupedMessages.posArray.indexOf(pos);
                            } else {
                                pinnedBottom = true;
                                nextPosition = -100;
                            }
                        } else {
                            prevPosition = -100;
                            nextPosition = -100;
                        }
                    } else {
                        nextPosition = position - 1;
                        prevPosition = position + 1;
                    }
                    int nextType = getItemViewType(nextPosition);
                    int prevType = getItemViewType(prevPosition);
                    if (!(message.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && nextType == holder.getItemViewType()) {
                        MessageObject nextMessage = (MessageObject) ChatActivity.this.messages.get(nextPosition - this.messagesStartRow);
                        pinnedBottom = nextMessage.isOutOwner() == message.isOutOwner() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                        if (pinnedBottom) {
                            if (ChatActivity.this.currentChat != null) {
                                pinnedBottom = nextMessage.messageOwner.from_id == message.messageOwner.from_id;
                            } else if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                pinnedBottom = nextMessage.getFromId() == message.getFromId();
                            }
                        }
                    }
                    if (prevType == holder.getItemViewType()) {
                        MessageObject prevMessage = (MessageObject) ChatActivity.this.messages.get(prevPosition - this.messagesStartRow);
                        pinnedTop = !(prevMessage.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300;
                        if (pinnedTop) {
                            if (ChatActivity.this.currentChat != null) {
                                pinnedTop = prevMessage.messageOwner.from_id == message.messageOwner.from_id;
                            } else if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                pinnedTop = prevMessage.getFromId() == message.getFromId();
                            }
                        }
                    }
                    messageCell.setMessageObject(message, groupedMessages, pinnedBottom, pinnedTop);
                    if ((view instanceof ChatMessageCell) && DownloadController.getInstance(ChatActivity.this.currentAccount).canDownloadMedia(message)) {
                        ((ChatMessageCell) view).downloadAudioIfNeed();
                    }
                    z = ChatActivity.this.highlightMessageId != Integer.MAX_VALUE && message.getId() == ChatActivity.this.highlightMessageId;
                    messageCell.setHighlighted(z);
                    if (ChatActivity.this.searchContainer != null && ChatActivity.this.searchContainer.getVisibility() == 0) {
                        if (DataQuery.getInstance(ChatActivity.this.currentAccount).isMessageFound(message.getId(), message.getDialogId() == ChatActivity.this.mergeDialogId) && DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery() != null) {
                            messageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                            index = ChatActivity.this.animatingMessageObjects.indexOf(message);
                            if (index != -1) {
                                ChatActivity.this.animatingMessageObjects.remove(index);
                                messageCell.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                                    public boolean onPreDraw() {
                                        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
                                        if (pipRoundVideoView != null) {
                                            pipRoundVideoView.showTemporary(true);
                                        }
                                        messageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                        ImageReceiver imageReceiver = messageCell.getPhotoImage();
                                        float scale = ((float) imageReceiver.getImageWidth()) / ChatActivity.this.instantCameraView.getCameraRect().width;
                                        int[] position = new int[2];
                                        messageCell.setAlpha(0.0f);
                                        messageCell.setTimeAlpha(0.0f);
                                        messageCell.getLocationOnScreen(position);
                                        position[0] = position[0] + imageReceiver.getImageX();
                                        position[1] = position[1] + imageReceiver.getImageY();
                                        final View cameraContainer = ChatActivity.this.instantCameraView.getCameraContainer();
                                        cameraContainer.setPivotX(0.0f);
                                        cameraContainer.setPivotY(0.0f);
                                        AnimatorSet animatorSet = new AnimatorSet();
                                        r8 = new Animator[8];
                                        r8[0] = ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView, "alpha", new float[]{0.0f});
                                        r8[1] = ObjectAnimator.ofFloat(cameraContainer, "scaleX", new float[]{scale});
                                        r8[2] = ObjectAnimator.ofFloat(cameraContainer, "scaleY", new float[]{scale});
                                        r8[3] = ObjectAnimator.ofFloat(cameraContainer, "translationX", new float[]{((float) position[0]) - rect.var_x});
                                        r8[4] = ObjectAnimator.ofFloat(cameraContainer, "translationY", new float[]{((float) position[1]) - rect.var_y});
                                        r8[5] = ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView.getSwitchButtonView(), "alpha", new float[]{0.0f});
                                        r8[6] = ObjectAnimator.ofInt(ChatActivity.this.instantCameraView.getPaint(), "alpha", new int[]{0});
                                        r8[7] = ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView.getMuteImageView(), "alpha", new float[]{0.0f});
                                        animatorSet.playTogether(r8);
                                        animatorSet.setDuration(180);
                                        animatorSet.setInterpolator(new DecelerateInterpolator());
                                        animatorSet.addListener(new AnimatorListenerAdapter() {

                                            /* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$3$1$2 */
                                            class CLASSNAME extends AnimatorListenerAdapter {
                                                CLASSNAME() {
                                                }

                                                public void onAnimationEnd(Animator animation) {
                                                    ChatActivity.this.instantCameraView.hideCamera(true);
                                                    ChatActivity.this.instantCameraView.setVisibility(4);
                                                }
                                            }

                                            public void onAnimationEnd(Animator animation) {
                                                messageCell.setAlpha(1.0f);
                                                Property<ChatMessageCell, Float> ALPHA = new FloatProperty<ChatMessageCell>("alpha") {
                                                    public void setValue(ChatMessageCell object, float value) {
                                                        object.setTimeAlpha(value);
                                                    }

                                                    public Float get(ChatMessageCell object) {
                                                        return Float.valueOf(object.getTimeAlpha());
                                                    }
                                                };
                                                AnimatorSet animatorSet = new AnimatorSet();
                                                Animator[] animatorArr = new Animator[2];
                                                animatorArr[0] = ObjectAnimator.ofFloat(cameraContainer, View.ALPHA, new float[]{0.0f});
                                                animatorArr[1] = ObjectAnimator.ofFloat(messageCell, ALPHA, new float[]{1.0f});
                                                animatorSet.playTogether(animatorArr);
                                                animatorSet.setDuration(100);
                                                animatorSet.setInterpolator(new DecelerateInterpolator());
                                                animatorSet.addListener(new CLASSNAME());
                                                animatorSet.start();
                                            }
                                        });
                                        animatorSet.start();
                                        return true;
                                    }
                                });
                            }
                        }
                    }
                    messageCell.setHighlightedText(null);
                    index = ChatActivity.this.animatingMessageObjects.indexOf(message);
                    if (index != -1) {
                    }
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                } else if (view instanceof ChatUnreadCell) {
                    ((ChatUnreadCell) view).setText(LocaleController.getString("UnreadMessages", R.string.UnreadMessages));
                    if (ChatActivity.this.createUnreadMessageAfterId != 0) {
                        ChatActivity.this.createUnreadMessageAfterId = 0;
                    }
                }
                if (message != null && message.messageOwner != null && message.messageOwner.media_unread && message.messageOwner.mentioned) {
                    if (!(ChatActivity.this.inPreviewMode || message.isVoice() || message.isRoundVideo())) {
                        ChatActivity.this.newMentionsCount = ChatActivity.this.newMentionsCount - 1;
                        if (ChatActivity.this.newMentionsCount <= 0) {
                            ChatActivity.this.newMentionsCount = 0;
                            ChatActivity.this.hasAllMentionsLocal = true;
                            ChatActivity.this.showMentiondownButton(false, true);
                        } else {
                            ChatActivity.this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(ChatActivity.this.newMentionsCount)}));
                        }
                        MessagesController.getInstance(ChatActivity.this.currentAccount).markMentionMessageAsRead(message.getId(), ChatObject.isChannel(ChatActivity.this.currentChat) ? ChatActivity.this.currentChat.var_id : 0, ChatActivity.this.dialog_id);
                        message.setContentIsRead();
                    }
                    if (!(view instanceof ChatMessageCell)) {
                        return;
                    }
                    if (ChatActivity.this.inPreviewMode) {
                        ((ChatMessageCell) view).setHighlighted(true);
                    } else {
                        ((ChatMessageCell) view).setHighlightedAnimated();
                    }
                }
            }
        }

        public int getItemViewType(int position) {
            if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                return ((MessageObject) ChatActivity.this.messages.get(position - this.messagesStartRow)).contentType;
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
                    int idx;
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        messageObject = ChatActivity.this.chatActivityEnterView.getEditingMessageObject();
                    } else {
                        messageObject = null;
                    }
                    if (message.getDialogId() == ChatActivity.this.dialog_id) {
                        idx = 0;
                    } else {
                        idx = 1;
                    }
                    if (messageObject == message || ChatActivity.this.selectedMessagesIds[idx].indexOfKey(message.getId()) >= 0) {
                        ChatActivity.this.setCellSelectionBackground(message, messageCell, idx);
                        selected = true;
                    } else {
                        messageCell.setBackgroundDrawable(null);
                    }
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
                if (!ChatActivity.this.inPreviewMode || !messageCell.isHighlighted()) {
                    if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID || messageCell.getMessageObject().getId() != ChatActivity.this.highlightMessageId) {
                        z = false;
                    }
                    messageCell.setHighlighted(z);
                }
            }
        }

        public void updateRowAtPosition(int index) {
            if (ChatActivity.this.chatLayoutManager != null) {
                int lastVisibleItem = -1;
                if (!(ChatActivity.this.wasManualScroll || ChatActivity.this.unreadMessageObject == null)) {
                    int pos = ChatActivity.this.messages.indexOf(ChatActivity.this.unreadMessageObject);
                    if (pos >= 0) {
                        lastVisibleItem = this.messagesStartRow + pos;
                    }
                }
                notifyItemChanged(index);
                if (lastVisibleItem != -1) {
                    ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(lastVisibleItem, ((ChatActivity.this.chatListView.getMeasuredHeight() - ChatActivity.this.chatListView.getPaddingBottom()) - ChatActivity.this.chatListView.getPaddingTop()) - AndroidUtilities.m9dp(29.0f));
                }
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject, boolean allowInPlace) {
            if (allowInPlace) {
                int count = ChatActivity.this.chatListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = ChatActivity.this.chatListView.getChildAt(a);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) child;
                        if (cell.getMessageObject() == messageObject) {
                            cell.setMessageObject(messageObject, cell.getCurrentMessagesGroup(), cell.isPinnedBottom(), cell.isPinnedTop());
                            return;
                        }
                    }
                }
            }
            int index = ChatActivity.this.messages.indexOf(messageObject);
            if (index != -1) {
                updateRowAtPosition(this.messagesStartRow + index);
            }
        }

        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    public ChatActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        CountDownLatch countDownLatch;
        int chatId = this.arguments.getInt("chat_id", 0);
        int userId = this.arguments.getInt("user_id", 0);
        int encId = this.arguments.getInt("enc_id", 0);
        this.inlineReturn = this.arguments.getLong("inline_return", 0);
        String inlineQuery = this.arguments.getString("inline_query");
        this.startLoadFromMessageId = this.arguments.getInt("message_id", 0);
        int migrated_to = this.arguments.getInt("migrated_to", 0);
        this.scrollToTopOnResume = this.arguments.getBoolean("scrollToTopOnResume", false);
        MessagesStorage messagesStorage;
        if (chatId != 0) {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(chatId));
            if (this.currentChat == null) {
                countDownLatch = new CountDownLatch(1);
                messagesStorage = MessagesStorage.getInstance(this.currentAccount);
                messagesStorage.getStorageQueue().postRunnable(new ChatActivity$$Lambda$0(this, messagesStorage, chatId, countDownLatch));
                try {
                    countDownLatch.await();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
            }
            if (chatId > 0) {
                this.dialog_id = (long) (-chatId);
            } else {
                this.isBroadcast = true;
                this.dialog_id = AndroidUtilities.makeBroadcastId(chatId);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance(this.currentAccount).startShortPoll(this.currentChat, false);
            }
        } else if (userId != 0) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId));
            if (this.currentUser == null) {
                messagesStorage = MessagesStorage.getInstance(this.currentAccount);
                countDownLatch = new CountDownLatch(1);
                messagesStorage.getStorageQueue().postRunnable(new ChatActivity$$Lambda$1(this, messagesStorage, userId, countDownLatch));
                try {
                    countDownLatch.await();
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance(this.currentAccount).putUser(this.currentUser, true);
            }
            this.dialog_id = (long) userId;
            this.botUser = this.arguments.getString("botUser");
            if (inlineQuery != null) {
                MessagesController.getInstance(this.currentAccount).sendBotStart(this.currentUser, inlineQuery);
            }
        } else if (encId == 0) {
            return false;
        } else {
            this.currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(encId));
            messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            if (this.currentEncryptedChat == null) {
                countDownLatch = new CountDownLatch(1);
                messagesStorage.getStorageQueue().postRunnable(new ChatActivity$$Lambda$2(this, messagesStorage, encId, countDownLatch));
                try {
                    countDownLatch.await();
                } catch (Throwable e22) {
                    FileLog.m13e(e22);
                }
                if (this.currentEncryptedChat == null) {
                    return false;
                }
                MessagesController.getInstance(this.currentAccount).putEncryptedChat(this.currentEncryptedChat, true);
            }
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentEncryptedChat.user_id));
            if (this.currentUser == null) {
                countDownLatch = new CountDownLatch(1);
                messagesStorage.getStorageQueue().postRunnable(new ChatActivity$$Lambda$3(this, messagesStorage, countDownLatch));
                try {
                    countDownLatch.await();
                } catch (Throwable e222) {
                    FileLog.m13e(e222);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance(this.currentAccount).putUser(this.currentUser, true);
            }
            this.dialog_id = ((long) encId) << 32;
            int[] iArr = this.maxMessageId;
            this.maxMessageId[1] = Integer.MIN_VALUE;
            iArr[0] = Integer.MIN_VALUE;
            iArr = this.minMessageId;
            this.minMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            iArr[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
        }
        if (this.currentUser != null) {
            MediaController.getInstance().startMediaObserver();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.historyCleared);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageSendError);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.replyMessagesDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botKeyboardDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatSearchResultsLoading);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.pinnedMessageDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.peerSettingsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newDraftReceived);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.channelRightsUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateMentionsCount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRecordTooShort);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatePollResults);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
        super.onFragmentCreate();
        if (this.currentEncryptedChat == null && !this.isBroadcast) {
            DataQuery.getInstance(this.currentAccount).loadBotKeyboard(this.dialog_id);
        }
        this.loading = true;
        MessagesController.getInstance(this.currentAccount).loadPeerSettings(this.currentUser, this.currentChat);
        MessagesController.getInstance(this.currentAccount).setLastCreatedDialogId(this.dialog_id, true);
        if (this.startLoadFromMessageId == 0) {
            SharedPreferences sharedPreferences = MessagesController.getNotificationsSettings(this.currentAccount);
            int messageId = sharedPreferences.getInt("diditem" + this.dialog_id, 0);
            if (messageId != 0) {
                this.wasManualScroll = true;
                this.loadingFromOldPosition = true;
                this.startLoadFromMessageOffset = sharedPreferences.getInt("diditemo" + this.dialog_id, 0);
                this.startLoadFromMessageId = messageId;
            }
        } else {
            this.needSelectFromMessageId = true;
        }
        MessagesController instance;
        long j;
        int i;
        int i2;
        boolean isChannel;
        int i3;
        if (this.startLoadFromMessageId != 0) {
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            int i4;
            if (migrated_to != 0) {
                this.mergeDialogId = (long) migrated_to;
                instance = MessagesController.getInstance(this.currentAccount);
                j = this.mergeDialogId;
                i = this.loadingFromOldPosition ? 50 : AndroidUtilities.isTablet() ? 30 : 20;
                i4 = this.startLoadFromMessageId;
                i2 = this.classGuid;
                isChannel = ChatObject.isChannel(this.currentChat);
                i3 = this.lastLoadIndex;
                this.lastLoadIndex = i3 + 1;
                instance.loadMessages(j, i, i4, 0, true, 0, i2, 3, 0, isChannel, i3);
            } else {
                instance = MessagesController.getInstance(this.currentAccount);
                j = this.dialog_id;
                i = this.loadingFromOldPosition ? 50 : AndroidUtilities.isTablet() ? 30 : 20;
                i4 = this.startLoadFromMessageId;
                i2 = this.classGuid;
                isChannel = ChatObject.isChannel(this.currentChat);
                i3 = this.lastLoadIndex;
                this.lastLoadIndex = i3 + 1;
                instance.loadMessages(j, i, i4, 0, true, 0, i2, 3, 0, isChannel, i3);
            }
        } else {
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            instance = MessagesController.getInstance(this.currentAccount);
            j = this.dialog_id;
            i = AndroidUtilities.isTablet() ? 30 : 20;
            i2 = this.classGuid;
            isChannel = ChatObject.isChannel(this.currentChat);
            i3 = this.lastLoadIndex;
            this.lastLoadIndex = i3 + 1;
            instance.loadMessages(j, i, 0, 0, true, 0, i2, 2, 0, isChannel, i3);
        }
        if (this.currentChat != null) {
            CountDownLatch countDownLatch2 = null;
            if (this.isBroadcast) {
                countDownLatch = new CountDownLatch(1);
            }
            MessagesController.getInstance(this.currentAccount).loadChatInfo(this.currentChat.var_id, countDownLatch2, true);
            if (this.isBroadcast && countDownLatch2 != null) {
                try {
                    countDownLatch2.await();
                } catch (Throwable e2222) {
                    FileLog.m13e(e2222);
                }
            }
        } else if (this.currentUser != null) {
            MessagesController.getInstance(this.currentAccount).loadUserInfo(this.currentUser, true, this.classGuid);
        }
        if (userId != 0 && this.currentUser.bot) {
            DataQuery.getInstance(this.currentAccount).loadBotInfo(userId, true, this.classGuid);
        } else if (this.chatInfo instanceof TL_chatFull) {
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.chatInfo.participants.participants.get(a)).user_id));
                if (user != null && user.bot) {
                    DataQuery.getInstance(this.currentAccount).loadBotInfo(user.var_id, true, this.classGuid);
                }
            }
        }
        if (this.currentUser != null) {
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.currentUser.var_id) >= 0;
        }
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(this.dialog_id), Boolean.valueOf(false));
        }
        if (!(this.currentEncryptedChat == null || AndroidUtilities.getMyLayerVersion(this.currentEncryptedChat.layer) == 73)) {
            SecretChatHelper.getInstance(this.currentAccount).sendNotifyLayerMessage(this.currentEncryptedChat, null);
        }
        return true;
    }

    final /* synthetic */ void lambda$onFragmentCreate$0$ChatActivity(MessagesStorage messagesStorage, int chatId, CountDownLatch countDownLatch) {
        this.currentChat = messagesStorage.getChat(chatId);
        countDownLatch.countDown();
    }

    final /* synthetic */ void lambda$onFragmentCreate$1$ChatActivity(MessagesStorage messagesStorage, int userId, CountDownLatch countDownLatch) {
        this.currentUser = messagesStorage.getUser(userId);
        countDownLatch.countDown();
    }

    final /* synthetic */ void lambda$onFragmentCreate$2$ChatActivity(MessagesStorage messagesStorage, int encId, CountDownLatch countDownLatch) {
        this.currentEncryptedChat = messagesStorage.getEncryptedChat(encId);
        countDownLatch.countDown();
    }

    final /* synthetic */ void lambda$onFragmentCreate$3$ChatActivity(MessagesStorage messagesStorage, CountDownLatch countDownLatch) {
        this.currentUser = messagesStorage.getUser(this.currentEncryptedChat.user_id);
        countDownLatch.countDown();
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
        MessagesController.getInstance(this.currentAccount).setLastCreatedDialogId(this.dialog_id, false);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyCleared);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageSendError);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.replyMessagesDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botKeyboardDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatSearchResultsLoading);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.pinnedMessageDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.peerSettingsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newDraftReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.channelRightsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateMentionsCount);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRecordTooShort);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatePollResults);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(this.dialog_id), Boolean.valueOf(true));
        }
        if (this.currentUser != null) {
            MediaController.getInstance().stopMediaObserver();
        }
        if (this.currentEncryptedChat != null) {
            try {
                if (VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                    MediaController.getInstance().setFlagSecure(this, false);
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
        if (this.currentUser != null) {
            MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(this.currentUser.var_id);
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
            MessagesController.getInstance(this.currentAccount).startShortPoll(this.currentChat, true);
        }
    }

    public View createView(Context context) {
        int a;
        CharSequence oldMessage;
        boolean z;
        View fragmentContextView;
        boolean z2;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (a = 0; a < 8; a++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        for (a = 1; a >= 0; a--) {
            this.selectedMessagesIds[a].clear();
            this.selectedMessagesCanCopyIds[a].clear();
            this.selectedMessagesCanStarIds[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.canEditMessagesCount = 0;
        this.roundVideoContainer = null;
        this.hasOwnBackground = true;
        if (this.chatAttachAlert != null) {
            try {
                if (this.chatAttachAlert.isShowing()) {
                    this.chatAttachAlert.dismiss();
                }
            } catch (Exception e) {
            }
            this.chatAttachAlert.onDestroy();
            this.chatAttachAlert = null;
        }
        Theme.createChatResources(context, false);
        this.actionBar.setAddToContainer(false);
        if (this.inPreviewMode) {
            this.actionBar.setBackButtonDrawable(null);
        } else {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.avatarContainer = new ChatAvatarContainer(context, this, this.currentEncryptedChat != null);
        if (this.inPreviewMode) {
            this.avatarContainer.setOccupyStatusBar(false);
        }
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        if (!(this.currentChat == null || ChatObject.isChannel(this.currentChat))) {
            int count = this.currentChat.participants_count;
            if (this.chatInfo != null) {
                count = this.chatInfo.participants.participants.size();
            }
            if (count == 0 || this.currentChat.deactivated || this.currentChat.left || (this.currentChat instanceof TL_chatForbidden) || (this.chatInfo != null && (this.chatInfo.participants instanceof TL_chatParticipantsForbidden))) {
                this.avatarContainer.setEnabled(false);
            }
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.currentEncryptedChat == null && !this.isBroadcast) {
            this.searchItem = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new CLASSNAME());
            this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
            this.searchItem.setVisibility(8);
        }
        this.headerItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        if (this.currentUser != null) {
            this.headerItem.addSubItem(32, LocaleController.getString("Call", R.string.Call));
            TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.currentUser.var_id);
            if (userFull == null || !userFull.phone_calls_available) {
                this.headerItem.hideSubItem(32);
            } else {
                this.headerItem.showSubItem(32);
            }
        }
        this.editTextItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.editTextItem.setTag(null);
        this.editTextItem.setVisibility(8);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("Bold", R.string.Bold));
        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, spannableStringBuilder.length(), 33);
        this.editTextItem.addSubItem(50, spannableStringBuilder);
        spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("Italic", R.string.Italic));
        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), 0, spannableStringBuilder.length(), 33);
        this.editTextItem.addSubItem(51, spannableStringBuilder);
        spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("Mono", R.string.Mono));
        spannableStringBuilder.setSpan(new TypefaceSpan(Typeface.MONOSPACE), 0, spannableStringBuilder.length(), 33);
        this.editTextItem.addSubItem(52, spannableStringBuilder);
        this.editTextItem.addSubItem(53, LocaleController.getString("CreateLink", R.string.CreateLink));
        this.editTextItem.addSubItem(54, LocaleController.getString("Regular", R.string.Regular));
        if (this.searchItem != null) {
            this.headerItem.addSubItem(search, LocaleController.getString("Search", R.string.Search));
        }
        if (!(this.currentChat == null || this.currentChat.creator)) {
            this.headerItem.addSubItem(21, LocaleController.getString("ReportChat", R.string.ReportChat));
        }
        if (this.currentUser != null) {
            this.addContactItem = this.headerItem.addSubItem(17, TtmlNode.ANONYMOUS_REGION_ID);
        }
        if (this.currentEncryptedChat != null) {
            this.timeItem2 = this.headerItem.addSubItem(13, LocaleController.getString("SetTimer", R.string.SetTimer));
        }
        if (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup && TextUtils.isEmpty(this.currentChat.username))) {
            this.headerItem.addSubItem(15, LocaleController.getString("ClearHistory", R.string.ClearHistory));
        }
        if (!ChatObject.isChannel(this.currentChat)) {
            if (this.currentChat == null || this.isBroadcast) {
                this.headerItem.addSubItem(16, LocaleController.getString("DeleteChatUser", R.string.DeleteChatUser));
            } else {
                this.headerItem.addSubItem(16, LocaleController.getString("DeleteAndExit", R.string.DeleteAndExit));
            }
        }
        if (this.currentUser == null || !this.currentUser.self) {
            this.muteItem = this.headerItem.addSubItem(18, null);
        } else if (this.currentUser.self) {
            this.headerItem.addSubItem(24, LocaleController.getString("AddShortcut", R.string.AddShortcut));
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
        if (this.inPreviewMode) {
            this.headerItem.setAlpha(0.0f);
            this.attachItem.setAlpha(0.0f);
        }
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        this.selectedMessagesCountTextView.setOnTouchListener(ChatActivity$$Lambda$4.$instance);
        if (this.currentEncryptedChat == null) {
            this.actionModeViews.add(actionMode.addItemWithWidth(23, R.drawable.group_edit, AndroidUtilities.m9dp(54.0f)));
            if (!this.isBroadcast) {
                this.actionModeViews.add(actionMode.addItemWithWidth(19, R.drawable.ic_ab_reply, AndroidUtilities.m9dp(54.0f)));
            }
            this.actionModeViews.add(actionMode.addItemWithWidth(22, R.drawable.ic_ab_fave, AndroidUtilities.m9dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(10, R.drawable.ic_ab_copy, AndroidUtilities.m9dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(11, R.drawable.ic_ab_forward, AndroidUtilities.m9dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(12, R.drawable.ic_ab_delete, AndroidUtilities.m9dp(54.0f)));
        } else {
            this.actionModeViews.add(actionMode.addItemWithWidth(23, R.drawable.group_edit, AndroidUtilities.m9dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(19, R.drawable.ic_ab_reply, AndroidUtilities.m9dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(22, R.drawable.ic_ab_fave, AndroidUtilities.m9dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(10, R.drawable.ic_ab_copy, AndroidUtilities.m9dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(12, R.drawable.ic_ab_delete, AndroidUtilities.m9dp(54.0f)));
        }
        ActionBarMenuItem item = actionMode.getItem(23);
        int i = (this.canEditMessagesCount == 1 && this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() == 1) ? 0 : 8;
        item.setVisibility(i);
        actionMode.getItem(10).setVisibility(this.selectedMessagesCanCopyIds[0].size() + this.selectedMessagesCanCopyIds[1].size() != 0 ? 0 : 8);
        actionMode.getItem(22).setVisibility(this.selectedMessagesCanStarIds[0].size() + this.selectedMessagesCanStarIds[1].size() != 0 ? 0 : 8);
        actionMode.getItem(12).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        checkActionBarMenu();
        this.fragmentView = new SizeNotifierFrameLayout(context) {
            int inputFieldHeight = 0;

            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId == 0 && messageObject.getDialogId() == ChatActivity.this.dialog_id) {
                    MediaController.getInstance().setTextureView(ChatActivity.this.createTextureView(false), ChatActivity.this.aspectRatioFrameLayout, ChatActivity.this.roundVideoContainer, true);
                }
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result;
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                boolean isRoundVideo = messageObject != null && messageObject.eventId == 0 && messageObject.isRoundVideo();
                if (!isRoundVideo || child != ChatActivity.this.roundVideoContainer) {
                    result = super.drawChild(canvas, child, drawingTime);
                    if (isRoundVideo && child == ChatActivity.this.chatListView && messageObject.type != 5 && ChatActivity.this.roundVideoContainer != null) {
                        super.drawChild(canvas, ChatActivity.this.roundVideoContainer, drawingTime);
                        if (ChatActivity.this.drawLaterRoundProgressCell != null) {
                            canvas.save();
                            canvas.translate(ChatActivity.this.drawLaterRoundProgressCell.getX(), (float) (ChatActivity.this.drawLaterRoundProgressCell.getTop() + ChatActivity.this.chatListView.getTop()));
                            ChatActivity.this.drawLaterRoundProgressCell.drawRoundProgress(canvas);
                            canvas.restore();
                        }
                    }
                } else if (messageObject.type == 5) {
                    if (Theme.chat_roundVideoShadow != null && ChatActivity.this.aspectRatioFrameLayout.isDrawingReady()) {
                        int x = ((int) child.getX()) - AndroidUtilities.m9dp(3.0f);
                        int y = ((int) child.getY()) - AndroidUtilities.m9dp(2.0f);
                        Theme.chat_roundVideoShadow.setAlpha(255);
                        Theme.chat_roundVideoShadow.setBounds(x, y, (AndroidUtilities.roundMessageSize + x) + AndroidUtilities.m9dp(6.0f), (AndroidUtilities.roundMessageSize + y) + AndroidUtilities.m9dp(6.0f));
                        Theme.chat_roundVideoShadow.draw(canvas);
                    }
                    result = super.drawChild(canvas, child, drawingTime);
                } else {
                    result = false;
                }
                if (child == ChatActivity.this.actionBar && ChatActivity.this.parentLayout != null) {
                    int i;
                    ActionBarLayout access$7900 = ChatActivity.this.parentLayout;
                    if (ChatActivity.this.actionBar.getVisibility() == 0) {
                        int measuredHeight = ChatActivity.this.actionBar.getMeasuredHeight();
                        i = (!ChatActivity.this.inPreviewMode || VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight;
                        i += measuredHeight;
                    } else {
                        i = 0;
                    }
                    access$7900.drawHeaderShadow(canvas, i);
                }
                return result;
            }

            protected boolean isActionBarVisible() {
                return ChatActivity.this.actionBar.getVisibility() == 0;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int allHeight = MeasureSpec.getSize(heightMeasureSpec);
                int heightSize = allHeight;
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(ChatActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ChatActivity.this.actionBar.getMeasuredHeight();
                if (ChatActivity.this.actionBar.getVisibility() == 0) {
                    heightSize -= actionBarHeight;
                }
                if (getKeyboardHeight() <= AndroidUtilities.m9dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                    heightSize -= ChatActivity.this.chatActivityEnterView.getEmojiPadding();
                    allHeight -= ChatActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                measureChildWithMargins(ChatActivity.this.chatActivityEnterView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                if (ChatActivity.this.inPreviewMode) {
                    this.inputFieldHeight = 0;
                } else {
                    this.inputFieldHeight = ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                }
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChatActivity.this.chatActivityEnterView || child == ChatActivity.this.actionBar)) {
                        int height;
                        if (child == ChatActivity.this.chatListView || child == ChatActivity.this.progressView) {
                            int contentWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, NUM);
                            int dp = AndroidUtilities.m9dp(10.0f);
                            int i2 = heightSize - this.inputFieldHeight;
                            int i3 = (!ChatActivity.this.inPreviewMode || VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight;
                            child.measure(contentWidthSpec, MeasureSpec.makeMeasureSpec(Math.max(dp, AndroidUtilities.m9dp((float) ((ChatActivity.this.chatActivityEnterView.isTopViewVisible() ? 48 : 0) + 2)) + (i2 - i3)), NUM));
                        } else if (child == ChatActivity.this.instantCameraView || child == ChatActivity.this.overlayView) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((allHeight - this.inputFieldHeight) + AndroidUtilities.m9dp(3.0f), NUM));
                        } else if (child == ChatActivity.this.emptyViewContainer) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
                        } else if (ChatActivity.this.chatActivityEnterView.isPopupView(child)) {
                            if (!AndroidUtilities.isInMultiwindow) {
                                height = child.getLayoutParams().height;
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                            } else if (AndroidUtilities.isTablet()) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.m9dp(320.0f), (((heightSize - this.inputFieldHeight) + actionBarHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                            } else {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((((heightSize - this.inputFieldHeight) + actionBarHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                            }
                        } else if (child == ChatActivity.this.mentionContainer) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ChatActivity.this.mentionContainer.getLayoutParams();
                            if (ChatActivity.this.mentionsAdapter.isBannedInline()) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, Integer.MIN_VALUE));
                            } else {
                                ChatActivity.this.mentionListViewIgnoreLayout = true;
                                int maxHeight;
                                int padding;
                                if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                                    maxHeight = ChatActivity.this.mentionGridLayoutManager.getRowsCount(widthSize) * 102;
                                    if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                                        maxHeight += 34;
                                    }
                                    height = (heightSize - ChatActivity.this.chatActivityEnterView.getMeasuredHeight()) + (maxHeight != 0 ? AndroidUtilities.m9dp(2.0f) : 0);
                                    padding = Math.max(0, height - AndroidUtilities.m9dp(Math.min((float) maxHeight, 122.399994f)));
                                    if (ChatActivity.this.mentionLayoutManager.getReverseLayout()) {
                                        ChatActivity.this.mentionListView.setPadding(0, 0, 0, padding);
                                    } else {
                                        ChatActivity.this.mentionListView.setPadding(0, padding, 0, 0);
                                    }
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
                                    height = (heightSize - ChatActivity.this.chatActivityEnterView.getMeasuredHeight()) + (maxHeight != 0 ? AndroidUtilities.m9dp(2.0f) : 0);
                                    padding = Math.max(0, height - AndroidUtilities.m9dp(Math.min((float) maxHeight, 122.399994f)));
                                    if (ChatActivity.this.mentionLayoutManager.getReverseLayout()) {
                                        ChatActivity.this.mentionListView.setPadding(0, 0, 0, padding);
                                    } else {
                                        ChatActivity.this.mentionListView.setPadding(0, padding, 0, 0);
                                    }
                                }
                                layoutParams.height = height;
                                layoutParams.topMargin = 0;
                                ChatActivity.this.mentionListViewIgnoreLayout = false;
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(layoutParams.height, NUM));
                            }
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
                if (ChatActivity.this.fixPaddingsInLayout) {
                    ChatActivity.this.globalIgnoreLayout = true;
                    ChatActivity.this.checkListViewPaddingsInternal();
                    ChatActivity.this.fixPaddingsInLayout = false;
                    ChatActivity.this.chatListView.measure(MeasureSpec.makeMeasureSpec(ChatActivity.this.chatListView.getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(ChatActivity.this.chatListView.getMeasuredHeight(), NUM));
                    ChatActivity.this.globalIgnoreLayout = false;
                }
                if (ChatActivity.this.scrollToPositionOnRecreate != -1) {
                    AndroidUtilities.runOnUIThread(new ChatActivity$7$$Lambda$0(this, ChatActivity.this.scrollToPositionOnRecreate));
                    ChatActivity.this.globalIgnoreLayout = true;
                    ChatActivity.this.scrollToPositionOnRecreate = -1;
                    ChatActivity.this.globalIgnoreLayout = false;
                }
            }

            final /* synthetic */ void lambda$onMeasure$0$ChatActivity$7(int scrollTo) {
                ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(scrollTo, ChatActivity.this.scrollToOffsetOnRecreate);
            }

            public void requestLayout() {
                if (!ChatActivity.this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.m9dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : ChatActivity.this.chatActivityEnterView.getEmojiPadding();
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
                                    if (ChatActivity.this.inPreviewMode && VERSION.SDK_INT >= 21) {
                                        childTop += AndroidUtilities.statusBarHeight;
                                        break;
                                    }
                                }
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (child == ChatActivity.this.mentionContainer) {
                            childTop -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight() - AndroidUtilities.m9dp(2.0f);
                        } else if (child == ChatActivity.this.pagedownButton) {
                            if (!ChatActivity.this.inPreviewMode) {
                                childTop -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                            }
                        } else if (child == ChatActivity.this.mentiondownButton) {
                            if (!ChatActivity.this.inPreviewMode) {
                                childTop -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                            }
                        } else if (child == ChatActivity.this.emptyViewContainer) {
                            childTop -= (this.inputFieldHeight / 2) - (ChatActivity.this.actionBar.getVisibility() == 0 ? ChatActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (ChatActivity.this.chatActivityEnterView.isPopupView(child)) {
                            if (AndroidUtilities.isInMultiwindow) {
                                childTop = (ChatActivity.this.chatActivityEnterView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.m9dp(1.0f);
                            } else {
                                childTop = ChatActivity.this.chatActivityEnterView.getBottom();
                            }
                        } else if (child == ChatActivity.this.gifHintTextView || child == ChatActivity.this.voiceHintTextView || child == ChatActivity.this.mediaBanTooltip) {
                            childTop -= this.inputFieldHeight;
                        } else if (child == ChatActivity.this.chatListView || child == ChatActivity.this.progressView) {
                            if (ChatActivity.this.chatActivityEnterView.isTopViewVisible()) {
                                childTop -= AndroidUtilities.m9dp(48.0f);
                            }
                        } else if (child == ChatActivity.this.actionBar) {
                            if (ChatActivity.this.inPreviewMode && VERSION.SDK_INT >= 21) {
                                childTop += AndroidUtilities.statusBarHeight;
                            }
                            childTop -= getPaddingTop();
                        } else if (child == ChatActivity.this.roundVideoContainer) {
                            childTop = ChatActivity.this.actionBar.getMeasuredHeight();
                        } else if (child == ChatActivity.this.instantCameraView || child == ChatActivity.this.overlayView) {
                            childTop = 0;
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                ChatActivity.this.updateMessagesVisiblePart(true);
                notifyHeightChanged();
            }
        };
        this.contentView = (SizeNotifierFrameLayout) this.fragmentView;
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper());
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(ChatActivity$$Lambda$5.$instance);
        if (this.currentEncryptedChat != null) {
            this.bigEmptyView = new ChatBigEmptyView(context, true);
            if (this.currentEncryptedChat.admin_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.bigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleOutgoing", R.string.EncryptedPlaceholderTitleOutgoing, UserObject.getFirstName(this.currentUser)));
            } else {
                this.bigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleIncoming", R.string.EncryptedPlaceholderTitleIncoming, UserObject.getFirstName(this.currentUser)));
            }
            this.emptyViewContainer.addView(this.bigEmptyView, new FrameLayout.LayoutParams(-2, -2, 17));
        } else if (this.currentUser == null || !this.currentUser.self) {
            this.emptyView = new TextView(context);
            if (this.currentUser == null || this.currentUser.var_id == 777000 || this.currentUser.var_id == 429000 || this.currentUser.var_id == 4244000 || !MessagesController.isSupportId(this.currentUser.var_id)) {
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
            this.emptyView.setPadding(AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(2.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(3.0f));
            this.emptyViewContainer.addView(this.emptyView, new FrameLayout.LayoutParams(-2, -2, 17));
        } else {
            this.bigEmptyView = new ChatBigEmptyView(context, false);
            this.emptyViewContainer.addView(this.bigEmptyView, new FrameLayout.LayoutParams(-2, -2, 17));
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
            if (this.chatActivityEnterView.isEditingMessage()) {
                oldMessage = null;
            } else {
                oldMessage = this.chatActivityEnterView.getFieldText();
            }
        } else {
            oldMessage = null;
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onDestroy();
        }
        this.chatListView = new RecyclerListView(context) {
            ArrayList<ChatMessageCell> drawCaptionAfter = new ArrayList();
            ArrayList<ChatMessageCell> drawNamesAfter = new ArrayList();
            ArrayList<ChatMessageCell> drawTimeAfter = new ArrayList();
            private float endedTrackingX;
            private long lastReplyButtonAnimationTime;
            private long lastTrackingAnimationTime;
            private boolean maybeStartTracking;
            private float replyButtonProgress;
            private boolean slideAnimationInProgress;
            private ChatMessageCell slidingView;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private float trackAnimationProgress;
            private boolean wasTrackingVibrate;

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

            private void setGroupTranslationX(ChatMessageCell view, float dx) {
                GroupedMessages group = view.getCurrentMessagesGroup();
                if (group != null) {
                    int count = getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        if (child != this && (child instanceof ChatMessageCell)) {
                            ChatMessageCell cell = (ChatMessageCell) child;
                            if (cell.getCurrentMessagesGroup() == group) {
                                cell.setTranslationX(dx);
                                cell.invalidate();
                            }
                        }
                    }
                    invalidate();
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent e) {
                boolean result = super.onInterceptTouchEvent(e);
                if (!ChatActivity.this.actionBar.isActionModeShowed()) {
                    processTouchEvent(e);
                }
                return result;
            }

            private void drawReplyButton(Canvas canvas) {
                if (this.slidingView != null) {
                    float scale;
                    int alpha;
                    float translationX = this.slidingView.getTranslationX();
                    long newTime = System.currentTimeMillis();
                    long dt = Math.min(17, newTime - this.lastReplyButtonAnimationTime);
                    this.lastReplyButtonAnimationTime = newTime;
                    boolean showing = translationX <= ((float) (-AndroidUtilities.m9dp(50.0f)));
                    if (showing) {
                        if (this.replyButtonProgress < 1.0f) {
                            this.replyButtonProgress += ((float) dt) / 180.0f;
                            if (this.replyButtonProgress > 1.0f) {
                                this.replyButtonProgress = 1.0f;
                            } else {
                                invalidate();
                            }
                        }
                    } else if (this.replyButtonProgress > 0.0f) {
                        this.replyButtonProgress -= ((float) dt) / 180.0f;
                        if (this.replyButtonProgress < 0.0f) {
                            this.replyButtonProgress = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                    if (showing) {
                        if (this.replyButtonProgress <= 0.8f) {
                            scale = 1.2f * (this.replyButtonProgress / 0.8f);
                        } else {
                            scale = 1.2f - (0.2f * ((this.replyButtonProgress - 0.8f) / 0.2f));
                        }
                        alpha = (int) Math.min(255.0f, 255.0f * (this.replyButtonProgress / 0.8f));
                    } else {
                        scale = this.replyButtonProgress;
                        alpha = (int) Math.min(255.0f, 255.0f * this.replyButtonProgress);
                    }
                    Theme.chat_shareDrawable.setAlpha(alpha);
                    Theme.chat_replyIconDrawable.setAlpha(alpha);
                    float x = ((float) getMeasuredWidth()) + (this.slidingView.getTranslationX() / 2.0f);
                    float y = (float) (this.slidingView.getTop() + (this.slidingView.getMeasuredHeight() / 2));
                    if (!Theme.isCustomTheme() || Theme.hasThemeKey(Theme.key_chat_shareBackground)) {
                        Theme.chat_shareDrawable.setColorFilter(Theme.getShareColorFilter(Theme.getColor(Theme.key_chat_shareBackground), false));
                    } else {
                        Theme.chat_shareDrawable.setColorFilter(Theme.colorFilter2);
                    }
                    Theme.chat_shareDrawable.setBounds((int) (x - (((float) AndroidUtilities.m9dp(14.0f)) * scale)), (int) (y - (((float) AndroidUtilities.m9dp(14.0f)) * scale)), (int) ((((float) AndroidUtilities.m9dp(14.0f)) * scale) + x), (int) ((((float) AndroidUtilities.m9dp(14.0f)) * scale) + y));
                    Theme.chat_shareDrawable.draw(canvas);
                    Theme.chat_replyIconDrawable.setBounds((int) (x - (((float) AndroidUtilities.m9dp(7.0f)) * scale)), (int) (y - (((float) AndroidUtilities.m9dp(6.0f)) * scale)), (int) ((((float) AndroidUtilities.m9dp(7.0f)) * scale) + x), (int) ((((float) AndroidUtilities.m9dp(5.0f)) * scale) + y));
                    Theme.chat_replyIconDrawable.draw(canvas);
                    Theme.chat_shareDrawable.setAlpha(255);
                    Theme.chat_replyIconDrawable.setAlpha(255);
                }
            }

            private void processTouchEvent(MotionEvent e) {
                ChatActivity.this.wasManualScroll = true;
                if (e.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    View view = getPressedChildView();
                    if (view instanceof ChatMessageCell) {
                        this.slidingView = (ChatMessageCell) view;
                        MessageObject message = this.slidingView.getMessageObject();
                        if ((ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46) && (!(ChatActivity.this.getMessageType(message) == 1 && (message.getDialogId() == ChatActivity.this.mergeDialogId || message.needDrawBluredPreview())) && ((ChatActivity.this.currentEncryptedChat != null || message.getId() >= 0) && ((ChatActivity.this.bottomOverlayChat == null || ChatActivity.this.bottomOverlayChat.getVisibility() != 0) && !ChatActivity.this.isBroadcast && (ChatActivity.this.currentChat == null || (!ChatObject.isNotInChat(ChatActivity.this.currentChat) && ((!ChatObject.isChannel(ChatActivity.this.currentChat) || ChatObject.canPost(ChatActivity.this.currentChat) || ChatActivity.this.currentChat.megagroup) && ChatObject.canSendMessages(ChatActivity.this.currentChat)))))))) {
                            this.startedTrackingPointerId = e.getPointerId(0);
                            this.maybeStartTracking = true;
                            this.startedTrackingX = (int) e.getX();
                            this.startedTrackingY = (int) e.getY();
                            return;
                        }
                        this.slidingView = null;
                    }
                } else if (this.slidingView != null && e.getAction() == 2 && e.getPointerId(0) == this.startedTrackingPointerId) {
                    int dx = Math.max(AndroidUtilities.m9dp(-80.0f), Math.min(0, (int) (e.getX() - ((float) this.startedTrackingX))));
                    int dy = Math.abs(((int) e.getY()) - this.startedTrackingY);
                    if (getScrollState() == 0 && this.maybeStartTracking && !this.startedTracking && ((float) dx) <= (-AndroidUtilities.getPixelsInCM(0.4f, true)) && Math.abs(dx) / 3 > dy) {
                        MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                        this.slidingView.onTouchEvent(event);
                        super.onInterceptTouchEvent(event);
                        event.recycle();
                        ChatActivity.this.chatLayoutManager.setCanScrollVertically(false);
                        this.maybeStartTracking = false;
                        this.startedTracking = true;
                        this.startedTrackingX = (int) e.getX();
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    } else if (this.startedTracking) {
                        if (Math.abs(dx) < AndroidUtilities.m9dp(50.0f)) {
                            this.wasTrackingVibrate = false;
                        } else if (!this.wasTrackingVibrate) {
                            try {
                                performHapticFeedback(3, 2);
                            } catch (Exception e2) {
                            }
                            this.wasTrackingVibrate = true;
                        }
                        this.slidingView.setTranslationX((float) dx);
                        if (this.slidingView.getMessageObject().isRoundVideo()) {
                            ChatActivity.this.updateTextureViewPosition();
                        }
                        setGroupTranslationX(this.slidingView, (float) dx);
                        invalidate();
                    }
                } else if (this.slidingView != null && e.getPointerId(0) == this.startedTrackingPointerId) {
                    if (e.getAction() == 3 || e.getAction() == 1 || e.getAction() == 6) {
                        if (Math.abs(this.slidingView.getTranslationX()) >= ((float) AndroidUtilities.m9dp(50.0f))) {
                            ChatActivity.this.showFieldPanelForReply(true, this.slidingView.getMessageObject());
                        }
                        this.endedTrackingX = this.slidingView.getTranslationX();
                        this.lastTrackingAnimationTime = System.currentTimeMillis();
                        this.trackAnimationProgress = 0.0f;
                        invalidate();
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        ChatActivity.this.chatLayoutManager.setCanScrollVertically(true);
                    }
                }
            }

            public boolean onTouchEvent(MotionEvent e) {
                boolean result = super.onTouchEvent(e);
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    return result;
                }
                processTouchEvent(e);
                boolean z = this.startedTracking || result;
                return z;
            }

            public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                super.requestDisallowInterceptTouchEvent(disallowIntercept);
                if (this.slidingView != null) {
                    this.endedTrackingX = this.slidingView.getTranslationX();
                    this.lastTrackingAnimationTime = System.currentTimeMillis();
                    this.trackAnimationProgress = 0.0f;
                    invalidate();
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    ChatActivity.this.chatLayoutManager.setCanScrollVertically(true);
                }
            }

            protected void onChildPressed(View child, boolean pressed) {
                super.onChildPressed(child, pressed);
                if (child instanceof ChatMessageCell) {
                    GroupedMessages groupedMessages = ((ChatMessageCell) child).getCurrentMessagesGroup();
                    if (groupedMessages != null) {
                        int count = getChildCount();
                        for (int a = 0; a < count; a++) {
                            View item = getChildAt(a);
                            if (item != child && (item instanceof ChatMessageCell)) {
                                ChatMessageCell cell = (ChatMessageCell) item;
                                if (((ChatMessageCell) item).getCurrentMessagesGroup() == groupedMessages) {
                                    cell.setPressed(pressed);
                                }
                            }
                        }
                    }
                }
            }

            public void onDraw(Canvas c) {
                super.onDraw(c);
                if (this.slidingView != null) {
                    float translationX = this.slidingView.getTranslationX();
                    if (!(this.maybeStartTracking || this.startedTracking || this.endedTrackingX == 0.0f || translationX == 0.0f)) {
                        long newTime = System.currentTimeMillis();
                        this.trackAnimationProgress += ((float) (newTime - this.lastTrackingAnimationTime)) / 180.0f;
                        if (this.trackAnimationProgress > 1.0f) {
                            this.trackAnimationProgress = 1.0f;
                        }
                        this.lastTrackingAnimationTime = newTime;
                        translationX = this.endedTrackingX * (1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(this.trackAnimationProgress));
                        if (translationX == 0.0f) {
                            this.endedTrackingX = 0.0f;
                        }
                        setGroupTranslationX(this.slidingView, translationX);
                        this.slidingView.setTranslationX(translationX);
                        if (this.slidingView.getMessageObject().isRoundVideo()) {
                            ChatActivity.this.updateTextureViewPosition();
                        }
                        invalidate();
                    }
                    drawReplyButton(c);
                }
            }

            protected void dispatchDraw(Canvas canvas) {
                ChatActivity.this.drawLaterRoundProgressCell = null;
                super.dispatchDraw(canvas);
            }

            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                ChatMessageCell cell;
                GroupedMessagePosition position;
                int a;
                int size;
                int clipLeft = 0;
                int clipBottom = 0;
                if (child instanceof ChatMessageCell) {
                    cell = (ChatMessageCell) child;
                    position = cell.getCurrentPosition();
                    GroupedMessages group = cell.getCurrentMessagesGroup();
                    if (position != null) {
                        if (position.var_pw != position.spanSize && position.spanSize == ChatActivity.id_chat_compose_panel && position.siblingHeights == null && group.hasSibling) {
                            clipLeft = cell.getBackgroundDrawableLeft();
                        } else if (position.siblingHeights != null) {
                            clipBottom = child.getBottom() - AndroidUtilities.m9dp((float) ((cell.isPinnedBottom() ? 1 : 0) + 1));
                        }
                    }
                    if (cell.needDelayRoundProgressDraw()) {
                        ChatActivity.this.drawLaterRoundProgressCell = cell;
                    }
                }
                if (clipLeft != 0) {
                    canvas.save();
                    canvas.clipRect(((float) clipLeft) + child.getTranslationX(), (float) child.getTop(), ((float) child.getRight()) + child.getTranslationX(), (float) child.getBottom());
                } else if (clipBottom != 0) {
                    canvas.save();
                    canvas.clipRect(((float) child.getLeft()) + child.getTranslationX(), (float) child.getTop(), ((float) child.getRight()) + child.getTranslationX(), (float) clipBottom);
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (!(clipLeft == 0 && clipBottom == 0)) {
                    canvas.restore();
                }
                int num = 0;
                int count = getChildCount();
                for (a = 0; a < count; a++) {
                    if (getChildAt(a) == child) {
                        num = a;
                        break;
                    }
                }
                if (num == count - 1) {
                    size = this.drawTimeAfter.size();
                    if (size > 0) {
                        for (a = 0; a < size; a++) {
                            cell = (ChatMessageCell) this.drawTimeAfter.get(a);
                            canvas.save();
                            canvas.translate(((float) cell.getLeft()) + cell.getTranslationX(), (float) cell.getTop());
                            cell.drawTimeLayout(canvas);
                            canvas.restore();
                        }
                        this.drawTimeAfter.clear();
                    }
                    size = this.drawNamesAfter.size();
                    if (size > 0) {
                        for (a = 0; a < size; a++) {
                            cell = (ChatMessageCell) this.drawNamesAfter.get(a);
                            canvas.save();
                            canvas.translate(((float) cell.getLeft()) + cell.getTranslationX(), (float) cell.getTop());
                            cell.drawNamesLayout(canvas);
                            canvas.restore();
                        }
                        this.drawNamesAfter.clear();
                    }
                    size = this.drawCaptionAfter.size();
                    if (size > 0) {
                        for (a = 0; a < size; a++) {
                            cell = (ChatMessageCell) this.drawCaptionAfter.get(a);
                            if (cell.getCurrentPosition() != null) {
                                canvas.save();
                                canvas.translate(((float) cell.getLeft()) + cell.getTranslationX(), (float) cell.getTop());
                                cell.drawCaptionLayout(canvas, (cell.getCurrentPosition().flags & 1) == 0);
                                canvas.restore();
                            }
                        }
                        this.drawCaptionAfter.clear();
                    }
                }
                if (child instanceof ChatMessageCell) {
                    ImageReceiver imageReceiver;
                    ChatMessageCell chatMessageCell = (ChatMessageCell) child;
                    position = chatMessageCell.getCurrentPosition();
                    if (position != null) {
                        if (position.last || (position.minX == (byte) 0 && position.minY == (byte) 0)) {
                            if (num == count - 1) {
                                canvas.save();
                                canvas.translate(((float) chatMessageCell.getLeft()) + chatMessageCell.getTranslationX(), (float) chatMessageCell.getTop());
                                if (position.last) {
                                    chatMessageCell.drawTimeLayout(canvas);
                                }
                                if (position.minX == (byte) 0 && position.minY == (byte) 0) {
                                    chatMessageCell.drawNamesLayout(canvas);
                                }
                                canvas.restore();
                            } else {
                                if (position.last) {
                                    this.drawTimeAfter.add(chatMessageCell);
                                }
                                if (position.minX == (byte) 0 && position.minY == (byte) 0 && chatMessageCell.hasNameLayout()) {
                                    this.drawNamesAfter.add(chatMessageCell);
                                }
                            }
                        }
                        if (num == count - 1) {
                            canvas.save();
                            canvas.translate(((float) chatMessageCell.getLeft()) + chatMessageCell.getTranslationX(), (float) chatMessageCell.getTop());
                            if (chatMessageCell.hasCaptionLayout() && (position.flags & 8) != 0) {
                                chatMessageCell.drawCaptionLayout(canvas, (position.flags & 1) == 0);
                            }
                            canvas.restore();
                        } else if (chatMessageCell.hasCaptionLayout() && (position.flags & 8) != 0) {
                            this.drawCaptionAfter.add(chatMessageCell);
                        }
                    }
                    MessageObject message = chatMessageCell.getMessageObject();
                    if (ChatActivity.this.roundVideoContainer != null && message.isRoundVideo() && MediaController.getInstance().isPlayingMessage(message)) {
                        imageReceiver = chatMessageCell.getPhotoImage();
                        float newX = ((float) imageReceiver.getImageX()) + chatMessageCell.getTranslationX();
                        float newY = (float) ((ChatActivity.this.inPreviewMode ? AndroidUtilities.statusBarHeight : 0) + (((ChatActivity.this.fragmentView.getPaddingTop() + chatMessageCell.getTop()) + imageReceiver.getImageY()) - (ChatActivity.this.chatActivityEnterView.isTopViewVisible() ? AndroidUtilities.m9dp(48.0f) : 0)));
                        if (!(ChatActivity.this.roundVideoContainer.getTranslationX() == newX && ChatActivity.this.roundVideoContainer.getTranslationY() == newY)) {
                            ChatActivity.this.roundVideoContainer.setTranslationX(newX);
                            ChatActivity.this.roundVideoContainer.setTranslationY(newY);
                            ChatActivity.this.fragmentView.invalidate();
                            ChatActivity.this.roundVideoContainer.invalidate();
                        }
                    }
                    imageReceiver = chatMessageCell.getAvatarImage();
                    if (imageReceiver != null) {
                        ViewHolder holder;
                        int p;
                        int idx;
                        GroupedMessages groupedMessages = ChatActivity.this.getValidGroupedMessage(message);
                        int top = child.getTop();
                        if (chatMessageCell.isPinnedBottom()) {
                            holder = ChatActivity.this.chatListView.getChildViewHolder(child);
                            if (holder != null) {
                                int nextPosition;
                                p = holder.getAdapterPosition();
                                if (groupedMessages == null || position == null) {
                                    nextPosition = p - 1;
                                } else {
                                    idx = groupedMessages.posArray.indexOf(position);
                                    size = groupedMessages.posArray.size();
                                    if ((position.flags & 8) != 0) {
                                        nextPosition = (p - size) + idx;
                                    } else {
                                        nextPosition = p - 1;
                                        a = idx + 1;
                                        while (idx < size && ((GroupedMessagePosition) groupedMessages.posArray.get(a)).minY <= position.maxY) {
                                            nextPosition--;
                                            a++;
                                        }
                                    }
                                }
                                if (ChatActivity.this.chatListView.findViewHolderForAdapterPosition(nextPosition) != null) {
                                    imageReceiver.setImageY(-AndroidUtilities.m9dp(1000.0f));
                                    imageReceiver.draw(canvas);
                                }
                            }
                        }
                        float tx = chatMessageCell.getTranslationX();
                        int y = child.getTop() + chatMessageCell.getLayoutHeight();
                        int maxY = ChatActivity.this.chatListView.getMeasuredHeight() - ChatActivity.this.chatListView.getPaddingBottom();
                        if (y > maxY) {
                            y = maxY;
                        }
                        if (chatMessageCell.isPinnedTop()) {
                            holder = ChatActivity.this.chatListView.getChildViewHolder(child);
                            if (holder != null) {
                                int tries = 0;
                                while (tries < 20) {
                                    int prevPosition;
                                    tries++;
                                    p = holder.getAdapterPosition();
                                    if (groupedMessages != null && position != null) {
                                        idx = groupedMessages.posArray.indexOf(position);
                                        if (idx < 0) {
                                            break;
                                        }
                                        size = groupedMessages.posArray.size();
                                        if ((position.flags & 4) != 0) {
                                            prevPosition = (p + idx) + 1;
                                        } else {
                                            prevPosition = p + 1;
                                            a = idx - 1;
                                            while (idx >= 0 && ((GroupedMessagePosition) groupedMessages.posArray.get(a)).maxY >= position.minY) {
                                                prevPosition++;
                                                a--;
                                            }
                                        }
                                    } else {
                                        prevPosition = p + 1;
                                    }
                                    holder = ChatActivity.this.chatListView.findViewHolderForAdapterPosition(prevPosition);
                                    if (holder == null) {
                                        break;
                                    }
                                    top = holder.itemView.getTop();
                                    if (y - AndroidUtilities.m9dp(48.0f) < holder.itemView.getBottom()) {
                                        tx = Math.min(holder.itemView.getTranslationX(), tx);
                                    }
                                    if (holder.itemView instanceof ChatMessageCell) {
                                        if (!((ChatMessageCell) holder.itemView).isPinnedTop()) {
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (y - AndroidUtilities.m9dp(48.0f) < top) {
                            y = top + AndroidUtilities.m9dp(48.0f);
                        }
                        if (tx != 0.0f) {
                            canvas.save();
                            canvas.translate(tx, 0.0f);
                        }
                        imageReceiver.setImageY(y - AndroidUtilities.m9dp(44.0f));
                        imageReceiver.draw(canvas);
                        if (tx != 0.0f) {
                            canvas.restore();
                        }
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
        this.chatListView.setPadding(0, AndroidUtilities.m9dp(4.0f), 0, AndroidUtilities.m9dp(3.0f));
        this.chatListView.setItemAnimator(null);
        this.chatListView.setLayoutAnimation(null);
        this.chatLayoutManager = new GridLayoutManagerFixed(context, id_chat_compose_panel, 1, true) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
                LinearSmoothScrollerMiddle linearSmoothScroller = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }

            public boolean shouldLayoutChildFromOpositeSide(View child) {
                if (!(child instanceof ChatMessageCell) || ((ChatMessageCell) child).getMessageObject().isOutOwner()) {
                    return false;
                }
                return true;
            }

            protected boolean hasSiblingChild(int position) {
                if (position < ChatActivity.this.chatAdapter.messagesStartRow || position >= ChatActivity.this.chatAdapter.messagesEndRow) {
                    return false;
                }
                int index = position - ChatActivity.this.chatAdapter.messagesStartRow;
                if (index < 0 || index >= ChatActivity.this.messages.size()) {
                    return false;
                }
                MessageObject message = (MessageObject) ChatActivity.this.messages.get(index);
                GroupedMessages group = ChatActivity.this.getValidGroupedMessage(message);
                if (group == null) {
                    return false;
                }
                GroupedMessagePosition pos = (GroupedMessagePosition) group.positions.get(message);
                if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == (byte) 0) {
                    return false;
                }
                int count = group.posArray.size();
                for (int a = 0; a < count; a++) {
                    GroupedMessagePosition p = (GroupedMessagePosition) group.posArray.get(a);
                    if (p != pos && p.minY <= pos.minY && p.maxY >= pos.minY) {
                        return true;
                    }
                }
                return false;
            }
        };
        this.chatLayoutManager.setSpanSizeLookup(new CLASSNAME());
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.chatListView.addItemDecoration(new CLASSNAME());
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.chatListView.setOnItemClickListener(this.onItemClickListener);
        this.chatListView.setOnScrollListener(new CLASSNAME());
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        this.progressView2 = new View(context);
        this.progressView2.setBackgroundResource(R.drawable.system_loader);
        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        this.progressBar = new RadialProgressView(context);
        this.progressBar.setSize(AndroidUtilities.m9dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        this.floatingDateView = new ChatActionCell(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getAlpha() == 0.0f) {
                    return false;
                }
                return super.onInterceptTouchEvent(ev);
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (getAlpha() == 0.0f) {
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        this.floatingDateView.setAlpha(0.0f);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.floatingDateView.setOnClickListener(new ChatActivity$$Lambda$6(this));
        if (this.currentEncryptedChat == null) {
            this.pinnedMessageView = new FrameLayout(context);
            this.pinnedMessageView.setTag(Integer.valueOf(1));
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.m9dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
            this.pinnedMessageView.setBackgroundResource(R.drawable.blockpanel);
            this.pinnedMessageView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelBackground), Mode.MULTIPLY));
            this.contentView.addView(this.pinnedMessageView, LayoutHelper.createFrame(-1, 50, 51));
            this.pinnedMessageView.setOnClickListener(new ChatActivity$$Lambda$7(this));
            this.pinnedLineView = new View(context);
            this.pinnedLineView.setBackgroundColor(Theme.getColor(Theme.key_chat_topPanelLine));
            this.pinnedMessageView.addView(this.pinnedLineView, LayoutHelper.createFrame(2, 32.0f, 51, 8.0f, 8.0f, 0.0f, 0.0f));
            this.pinnedMessageImageView = new BackupImageView(context);
            this.pinnedMessageView.addView(this.pinnedMessageImageView, LayoutHelper.createFrame(32, 32.0f, 51, 17.0f, 8.0f, 0.0f, 0.0f));
            this.pinnedMessageNameTextView = new SimpleTextView(context);
            this.pinnedMessageNameTextView.setTextSize(14);
            this.pinnedMessageNameTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelTitle));
            this.pinnedMessageNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.pinnedMessageView.addView(this.pinnedMessageNameTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.m9dp(18.0f), 51, 18.0f, 7.3f, 40.0f, 0.0f));
            this.pinnedMessageTextView = new SimpleTextView(context);
            this.pinnedMessageTextView.setTextSize(14);
            this.pinnedMessageTextView.setTextColor(Theme.getColor(Theme.key_chat_topPanelMessage));
            this.pinnedMessageView.addView(this.pinnedMessageTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.m9dp(18.0f), 51, 18.0f, 25.3f, 40.0f, 0.0f));
            this.closePinned = new ImageView(context);
            this.closePinned.setImageResource(R.drawable.miniplayer_close);
            this.closePinned.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelClose), Mode.MULTIPLY));
            this.closePinned.setScaleType(ScaleType.CENTER);
            this.pinnedMessageView.addView(this.closePinned, LayoutHelper.createFrame(36, 48, 53));
            this.closePinned.setOnClickListener(new ChatActivity$$Lambda$8(this));
        }
        this.reportSpamView = new LinearLayout(context);
        this.reportSpamView.setTag(Integer.valueOf(1));
        this.reportSpamView.setTranslationY((float) (-AndroidUtilities.m9dp(50.0f)));
        this.reportSpamView.setVisibility(8);
        this.reportSpamView.setBackgroundResource(R.drawable.blockpanel);
        this.reportSpamView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelBackground), Mode.MULTIPLY));
        this.contentView.addView(this.reportSpamView, LayoutHelper.createFrame(-1, 50, 51));
        this.addToContactsButton = new TextView(context);
        this.addToContactsButton.setTextColor(Theme.getColor(Theme.key_chat_addContact));
        this.addToContactsButton.setVisibility(8);
        this.addToContactsButton.setTextSize(1, 14.0f);
        this.addToContactsButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addToContactsButton.setSingleLine(true);
        this.addToContactsButton.setMaxLines(1);
        this.addToContactsButton.setPadding(AndroidUtilities.m9dp(4.0f), 0, AndroidUtilities.m9dp(4.0f), 0);
        this.addToContactsButton.setGravity(17);
        this.addToContactsButton.setText(LocaleController.getString("AddContactChat", R.string.AddContactChat));
        this.reportSpamView.addView(this.addToContactsButton, LayoutHelper.createLinear(-1, -1, 0.5f, 51, 0, 0, 0, AndroidUtilities.m9dp(1.0f)));
        this.addToContactsButton.setOnClickListener(new ChatActivity$$Lambda$9(this));
        this.reportSpamContainer = new FrameLayout(context);
        this.reportSpamView.addView(this.reportSpamContainer, LayoutHelper.createLinear(-1, -1, 1.0f, 51, 0, 0, 0, AndroidUtilities.m9dp(1.0f)));
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
        this.reportSpamButton.setPadding(AndroidUtilities.m9dp(50.0f), 0, AndroidUtilities.m9dp(50.0f), 0);
        this.reportSpamContainer.addView(this.reportSpamButton, LayoutHelper.createFrame(-1, -1, 51));
        this.reportSpamButton.setOnClickListener(new ChatActivity$$Lambda$10(this));
        this.closeReportSpam = new ImageView(context);
        this.closeReportSpam.setImageResource(R.drawable.miniplayer_close);
        this.closeReportSpam.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelClose), Mode.MULTIPLY));
        this.closeReportSpam.setScaleType(ScaleType.CENTER);
        this.reportSpamContainer.addView(this.closeReportSpam, LayoutHelper.createFrame(48, 48, 53));
        this.closeReportSpam.setOnClickListener(new ChatActivity$$Lambda$11(this));
        this.alertView = new FrameLayout(context);
        this.alertView.setTag(Integer.valueOf(1));
        this.alertView.setTranslationY((float) (-AndroidUtilities.m9dp(50.0f)));
        this.alertView.setVisibility(8);
        this.alertView.setBackgroundResource(R.drawable.blockpanel);
        this.alertView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_topPanelBackground), Mode.MULTIPLY));
        this.contentView.addView(this.alertView, LayoutHelper.createFrame(-1, 50, 51));
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
        this.pagedownButton = new FrameLayout(context);
        this.pagedownButton.setVisibility(4);
        this.contentView.addView(this.pagedownButton, LayoutHelper.createFrame(66, 59.0f, 85, 0.0f, 0.0f, -3.0f, 5.0f));
        this.pagedownButton.setOnClickListener(new ChatActivity$$Lambda$12(this));
        this.mentiondownButton = new FrameLayout(context);
        this.mentiondownButton.setVisibility(4);
        this.contentView.addView(this.mentiondownButton, LayoutHelper.createFrame(46, 59.0f, 85, 0.0f, 0.0f, 7.0f, 5.0f));
        this.mentiondownButton.setOnClickListener(new CLASSNAME());
        this.mentiondownButton.setOnLongClickListener(new ChatActivity$$Lambda$13(this));
        if (!this.isBroadcast) {
            this.mentionContainer = new FrameLayout(context) {
                public void onDraw(Canvas canvas) {
                    if (ChatActivity.this.mentionListView.getChildCount() > 0) {
                        int top;
                        if (ChatActivity.this.mentionLayoutManager.getReverseLayout()) {
                            top = ChatActivity.this.mentionListViewScrollOffsetY + AndroidUtilities.m9dp(2.0f);
                            Theme.chat_composeShadowDrawable.setBounds(0, top + Theme.chat_composeShadowDrawable.getIntrinsicHeight(), getMeasuredWidth(), top);
                            Theme.chat_composeShadowDrawable.draw(canvas);
                            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) top, Theme.chat_composeBackgroundPaint);
                            return;
                        }
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                            top = ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.m9dp(4.0f);
                        } else {
                            top = ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.m9dp(2.0f);
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
            this.contentView.addView(this.mentionContainer, LayoutHelper.createFrame(-1, 110, 83));
            this.mentionListView = new RecyclerListView(context) {
                private int lastHeight;
                private int lastWidth;

                public boolean onInterceptTouchEvent(MotionEvent event) {
                    if (ChatActivity.this.mentionLayoutManager.getReverseLayout()) {
                        if (!(ChatActivity.this.mentionListViewIsScrolling || ChatActivity.this.mentionListViewScrollOffsetY == 0 || event.getY() <= ((float) ChatActivity.this.mentionListViewScrollOffsetY))) {
                            return false;
                        }
                    } else if (!(ChatActivity.this.mentionListViewIsScrolling || ChatActivity.this.mentionListViewScrollOffsetY == 0 || event.getY() >= ((float) ChatActivity.this.mentionListViewScrollOffsetY))) {
                        return false;
                    }
                    boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, ChatActivity.this.mentionListView, 0, null);
                    if (super.onInterceptTouchEvent(event) || result) {
                        return true;
                    }
                    return false;
                }

                public boolean onTouchEvent(MotionEvent event) {
                    if (ChatActivity.this.mentionLayoutManager.getReverseLayout()) {
                        if (!(ChatActivity.this.mentionListViewIsScrolling || ChatActivity.this.mentionListViewScrollOffsetY == 0 || event.getY() <= ((float) ChatActivity.this.mentionListViewScrollOffsetY))) {
                            return false;
                        }
                    } else if (!(ChatActivity.this.mentionListViewIsScrolling || ChatActivity.this.mentionListViewScrollOffsetY == 0 || event.getY() >= ((float) ChatActivity.this.mentionListViewScrollOffsetY))) {
                        return false;
                    }
                    return super.onTouchEvent(event);
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
                    if (!(ChatActivity.this.mentionLayoutManager.getReverseLayout() || ChatActivity.this.mentionListView == null || ChatActivity.this.mentionListViewLastViewPosition < 0 || width != this.lastWidth || height - this.lastHeight == 0)) {
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
            this.mentionListView.setOnTouchListener(new ChatActivity$$Lambda$14(this));
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
                    this.size.width = 0.0f;
                    this.size.height = 0.0f;
                    BotInlineResult object = ChatActivity.this.mentionsAdapter.getItem(i);
                    if (object instanceof BotInlineResult) {
                        BotInlineResult inlineResult = object;
                        int b;
                        DocumentAttribute attribute;
                        if (inlineResult.document != null) {
                            float f2;
                            Size size = this.size;
                            if (inlineResult.document.thumb != null) {
                                f2 = (float) inlineResult.document.thumb.var_w;
                            } else {
                                f2 = 100.0f;
                            }
                            size.width = f2;
                            Size size2 = this.size;
                            if (inlineResult.document.thumb != null) {
                                f = (float) inlineResult.document.thumb.var_h;
                            }
                            size2.height = f;
                            for (b = 0; b < inlineResult.document.attributes.size(); b++) {
                                attribute = (DocumentAttribute) inlineResult.document.attributes.get(b);
                                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                    this.size.width = (float) attribute.var_w;
                                    this.size.height = (float) attribute.var_h;
                                    break;
                                }
                            }
                        } else if (inlineResult.content != null) {
                            for (b = 0; b < inlineResult.content.attributes.size(); b++) {
                                attribute = (DocumentAttribute) inlineResult.content.attributes.get(b);
                                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                    this.size.width = (float) attribute.var_w;
                                    this.size.height = (float) attribute.var_h;
                                    break;
                                }
                            }
                        } else if (inlineResult.thumb != null) {
                            for (b = 0; b < inlineResult.thumb.attributes.size(); b++) {
                                attribute = (DocumentAttribute) inlineResult.thumb.attributes.get(b);
                                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                    this.size.width = (float) attribute.var_w;
                                    this.size.height = (float) attribute.var_h;
                                    break;
                                }
                            }
                        } else if (inlineResult.photo != null) {
                            PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(inlineResult.photo.sizes, AndroidUtilities.photoSize.intValue());
                            if (photoSize != null) {
                                this.size.width = (float) photoSize.var_w;
                                this.size.height = (float) photoSize.var_h;
                            }
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
            this.mentionGridLayoutManager.setSpanSizeLookup(new CLASSNAME());
            this.mentionListView.addItemDecoration(new CLASSNAME());
            this.mentionListView.setItemAnimator(null);
            this.mentionListView.setLayoutAnimation(null);
            this.mentionListView.setClipToPadding(false);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setOverScrollMode(2);
            this.mentionContainer.addView(this.mentionListView, LayoutHelper.createFrame(-1, -1.0f));
            recyclerListView = this.mentionListView;
            chatActivityAdapter = new MentionsAdapter(context, false, this.dialog_id, new CLASSNAME());
            this.mentionsAdapter = chatActivityAdapter;
            recyclerListView.setAdapter(chatActivityAdapter);
            if (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup)) {
                this.mentionsAdapter.setBotInfo(this.botInfo);
            }
            this.mentionsAdapter.setParentFragment(this);
            this.mentionsAdapter.setChatInfo(this.chatInfo);
            this.mentionsAdapter.setNeedUsernames(this.currentChat != null);
            MentionsAdapter mentionsAdapter = this.mentionsAdapter;
            z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
            mentionsAdapter.setNeedBotContext(z);
            this.mentionsAdapter.setBotsCount(this.currentChat != null ? this.botsCount : 1);
            recyclerListView = this.mentionListView;
            OnItemClickListener chatActivity$$Lambda$15 = new ChatActivity$$Lambda$15(this);
            this.mentionsOnItemClickListener = chatActivity$$Lambda$15;
            recyclerListView.setOnItemClickListener(chatActivity$$Lambda$15);
            this.mentionListView.setOnItemLongClickListener(new ChatActivity$$Lambda$16(this));
            this.mentionListView.setOnScrollListener(new CLASSNAME());
        }
        this.pagedownButtonImage = new ImageView(context);
        this.pagedownButtonImage.setImageResource(R.drawable.pagedown);
        this.pagedownButtonImage.setScaleType(ScaleType.CENTER);
        this.pagedownButtonImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_goDownButtonIcon), Mode.MULTIPLY));
        this.pagedownButtonImage.setPadding(0, AndroidUtilities.m9dp(2.0f), 0, 0);
        Drawable drawable = Theme.createCircleDrawable(AndroidUtilities.m9dp(42.0f), Theme.getColor(Theme.key_chat_goDownButton));
        Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.pagedown_shadow).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_goDownButtonShadow), Mode.MULTIPLY));
        Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.m9dp(42.0f), AndroidUtilities.m9dp(42.0f));
        this.pagedownButtonImage.setBackgroundDrawable(combinedDrawable);
        this.pagedownButton.addView(this.pagedownButtonImage, LayoutHelper.createFrame(46, 46, 81));
        this.pagedownButtonCounter = new TextView(context);
        this.pagedownButtonCounter.setVisibility(4);
        this.pagedownButtonCounter.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pagedownButtonCounter.setTextSize(1, 13.0f);
        this.pagedownButtonCounter.setTextColor(Theme.getColor(Theme.key_chat_goDownButtonCounter));
        this.pagedownButtonCounter.setGravity(17);
        this.pagedownButtonCounter.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(11.5f), Theme.getColor(Theme.key_chat_goDownButtonCounterBackground)));
        this.pagedownButtonCounter.setMinWidth(AndroidUtilities.m9dp(23.0f));
        this.pagedownButtonCounter.setPadding(AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(1.0f));
        this.pagedownButton.addView(this.pagedownButtonCounter, LayoutHelper.createFrame(-2, 23, 49));
        this.mentiondownButtonImage = new ImageView(context);
        this.mentiondownButtonImage.setImageResource(R.drawable.mentionbutton);
        this.mentiondownButtonImage.setScaleType(ScaleType.CENTER);
        this.mentiondownButtonImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_goDownButtonIcon), Mode.MULTIPLY));
        this.mentiondownButtonImage.setPadding(0, AndroidUtilities.m9dp(2.0f), 0, 0);
        drawable = Theme.createCircleDrawable(AndroidUtilities.m9dp(42.0f), Theme.getColor(Theme.key_chat_goDownButton));
        shadowDrawable = context.getResources().getDrawable(R.drawable.pagedown_shadow).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_goDownButtonShadow), Mode.MULTIPLY));
        combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.m9dp(42.0f), AndroidUtilities.m9dp(42.0f));
        this.mentiondownButtonImage.setBackgroundDrawable(combinedDrawable);
        this.mentiondownButton.addView(this.mentiondownButtonImage, LayoutHelper.createFrame(46, 46, 83));
        this.mentiondownButtonCounter = new TextView(context);
        this.mentiondownButtonCounter.setVisibility(4);
        this.mentiondownButtonCounter.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.mentiondownButtonCounter.setTextSize(1, 13.0f);
        this.mentiondownButtonCounter.setTextColor(Theme.getColor(Theme.key_chat_goDownButtonCounter));
        this.mentiondownButtonCounter.setGravity(17);
        this.mentiondownButtonCounter.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(11.5f), Theme.getColor(Theme.key_chat_goDownButtonCounterBackground)));
        this.mentiondownButtonCounter.setMinWidth(AndroidUtilities.m9dp(23.0f));
        this.mentiondownButtonCounter.setPadding(AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(1.0f));
        this.mentiondownButton.addView(this.mentiondownButtonCounter, LayoutHelper.createFrame(-2, 23, 49));
        if (!AndroidUtilities.isTablet() || AndroidUtilities.isSmallTablet()) {
            fragmentContextView = new FragmentContextView(context, this, true);
            this.contentView.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
            fragmentContextView = new FragmentContextView(context, this, false);
            this.fragmentContextView = fragmentContextView;
            sizeNotifierFrameLayout.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            this.fragmentContextView.setAdditionalContextView(fragmentContextView);
            fragmentContextView.setAdditionalContextView(this.fragmentContextView);
        }
        this.contentView.addView(this.actionBar);
        this.overlayView = new View(context);
        this.overlayView.setOnTouchListener(new ChatActivity$$Lambda$17(this));
        this.contentView.addView(this.overlayView, LayoutHelper.createFrame(-1, -1, 51));
        this.overlayView.setVisibility(8);
        this.instantCameraView = new InstantCameraView(context, this);
        this.contentView.addView(this.instantCameraView, LayoutHelper.createFrame(-1, -1, 51));
        this.chatActivityEnterView = new ChatActivityEnterView(getParentActivity(), this.contentView, this, true);
        this.chatActivityEnterView.setDialogId(this.dialog_id, this.currentAccount);
        this.chatActivityEnterView.setId(id_chat_compose_panel);
        this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23;
        if (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46) {
            z2 = true;
        } else {
            z2 = false;
        }
        chatActivityEnterView.setAllowStickersAndGifs(z, z2);
        if (this.inPreviewMode) {
            this.chatActivityEnterView.setVisibility(4);
        }
        this.contentView.addView(this.chatActivityEnterView, this.contentView.getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
        this.chatActivityEnterView.setDelegate(new CLASSNAME());
        fragmentContextView = new FrameLayout(context) {
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
                    if (ChatActivity.this.mentiondownButton != null) {
                        FrameLayout access$10400 = ChatActivity.this.mentiondownButton;
                        if (ChatActivity.this.pagedownButton.getVisibility() == 0) {
                            translationY -= (float) AndroidUtilities.m9dp(72.0f);
                        }
                        access$10400.setTranslationY(translationY);
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
                    FrameLayout access$10200;
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
                        access$10200 = ChatActivity.this.pagedownButton;
                        if (ChatActivity.this.pagedownButton.getTag() == null) {
                            f = (float) AndroidUtilities.m9dp(100.0f);
                        }
                        access$10200.setTranslationY(f);
                    }
                    if (ChatActivity.this.mentiondownButton != null) {
                        access$10200 = ChatActivity.this.mentiondownButton;
                        if (ChatActivity.this.mentiondownButton.getTag() == null) {
                            f = (float) AndroidUtilities.m9dp(100.0f);
                        } else {
                            f = (float) (ChatActivity.this.pagedownButton.getVisibility() == 0 ? -AndroidUtilities.m9dp(72.0f) : 0);
                        }
                        access$10200.setTranslationY(f);
                    }
                }
            }
        };
        this.chatActivityEnterView.addTopView(fragmentContextView, 48);
        fragmentContextView.setOnClickListener(new ChatActivity$$Lambda$18(this));
        this.replyLineView = new View(context);
        this.replyLineView.setBackgroundColor(Theme.getColor(Theme.key_chat_replyPanelLine));
        fragmentContextView.addView(this.replyLineView, LayoutHelper.createFrame(-1, 1, 83));
        this.replyIconImageView = new ImageView(context);
        this.replyIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_replyPanelIcons), Mode.MULTIPLY));
        this.replyIconImageView.setScaleType(ScaleType.CENTER);
        fragmentContextView.addView(this.replyIconImageView, LayoutHelper.createFrame(52, 46, 51));
        this.replyCloseImageView = new ImageView(context);
        this.replyCloseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_replyPanelClose), Mode.MULTIPLY));
        this.replyCloseImageView.setImageResource(R.drawable.msg_panel_clear);
        this.replyCloseImageView.setScaleType(ScaleType.CENTER);
        fragmentContextView.addView(this.replyCloseImageView, LayoutHelper.createFrame(52, 46.0f, 53, 0.0f, 0.5f, 0.0f, 0.0f));
        this.replyCloseImageView.setOnClickListener(new ChatActivity$$Lambda$19(this));
        this.replyNameTextView = new SimpleTextView(context);
        this.replyNameTextView.setTextSize(14);
        this.replyNameTextView.setTextColor(Theme.getColor(Theme.key_chat_replyPanelName));
        this.replyNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        fragmentContextView.addView(this.replyNameTextView, LayoutHelper.createFrame(-1, 18.0f, 51, 52.0f, 6.0f, 52.0f, 0.0f));
        this.replyObjectTextView = new SimpleTextView(context);
        this.replyObjectTextView.setTextSize(14);
        this.replyObjectTextView.setTextColor(Theme.getColor(Theme.key_chat_replyPanelMessage));
        fragmentContextView.addView(this.replyObjectTextView, LayoutHelper.createFrame(-1, 18.0f, 51, 52.0f, 24.0f, 52.0f, 0.0f));
        this.replyImageView = new BackupImageView(context);
        fragmentContextView.addView(this.replyImageView, LayoutHelper.createFrame(34, 34.0f, 51, 52.0f, 6.0f, 0.0f, 0.0f));
        this.stickersPanel = new FrameLayout(context);
        this.stickersPanel.setVisibility(8);
        this.contentView.addView(this.stickersPanel, LayoutHelper.createFrame(-2, 81.5f, 83, 0.0f, 0.0f, 0.0f, 38.0f));
        StickerPreviewViewerDelegate CLASSNAME = new CLASSNAME();
        final StickerPreviewViewerDelegate stickerPreviewViewerDelegate = CLASSNAME;
        this.stickersListView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, ChatActivity.this.stickersListView, 0, stickerPreviewViewerDelegate);
                if (super.onInterceptTouchEvent(event) || result) {
                    return true;
                }
                return false;
            }
        };
        this.stickersListView.setTag(Integer.valueOf(3));
        this.stickersListView.setOnTouchListener(new ChatActivity$$Lambda$20(this, CLASSNAME));
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
        this.searchContainer.setOnTouchListener(ChatActivity$$Lambda$21.$instance);
        this.searchContainer.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.m9dp(3.0f), 0, 0);
        this.searchUpButton = new ImageView(context);
        this.searchUpButton.setScaleType(ScaleType.CENTER);
        this.searchUpButton.setImageResource(R.drawable.search_up);
        this.searchUpButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 48.0f, 0.0f));
        this.searchUpButton.setOnClickListener(new ChatActivity$$Lambda$22(this));
        this.searchDownButton = new ImageView(context);
        this.searchDownButton.setScaleType(ScaleType.CENTER);
        this.searchDownButton.setImageResource(R.drawable.search_down);
        this.searchDownButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new ChatActivity$$Lambda$23(this));
        if (this.currentChat != null && (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup)) {
            this.searchUserButton = new ImageView(context);
            this.searchUserButton.setScaleType(ScaleType.CENTER);
            this.searchUserButton.setImageResource(R.drawable.usersearch);
            this.searchUserButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
            this.searchContainer.addView(this.searchUserButton, LayoutHelper.createFrame(48, 48.0f, 51, 48.0f, 0.0f, 0.0f, 0.0f));
            this.searchUserButton.setOnClickListener(new ChatActivity$$Lambda$24(this));
        }
        this.searchCalendarButton = new ImageView(context);
        this.searchCalendarButton.setScaleType(ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(R.drawable.search_calendar);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 51));
        this.searchCalendarButton.setOnClickListener(new ChatActivity$$Lambda$25(this));
        this.searchCountText = new SimpleTextView(context);
        this.searchCountText.setTextColor(Theme.getColor(Theme.key_chat_searchPanelText));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchCountText.setGravity(5);
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 108.0f, 0.0f));
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
        this.bottomOverlay.setPadding(0, AndroidUtilities.m9dp(2.0f), 0, 0);
        this.contentView.addView(this.bottomOverlay, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayText = new TextView(context);
        this.bottomOverlayText.setTextSize(1, 14.0f);
        this.bottomOverlayText.setGravity(17);
        this.bottomOverlayText.setMaxLines(2);
        this.bottomOverlayText.setEllipsize(TruncateAt.END);
        this.bottomOverlayText.setLineSpacing((float) AndroidUtilities.m9dp(2.0f), 1.0f);
        this.bottomOverlayText.setTextColor(Theme.getColor(Theme.key_chat_secretChatStatusText));
        this.bottomOverlay.addView(this.bottomOverlayText, LayoutHelper.createFrame(-2, -2.0f, 17, 14.0f, 0.0f, 14.0f, 0.0f));
        this.bottomOverlayChat = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.m9dp(3.0f), 0, 0);
        this.bottomOverlayChat.setVisibility(4);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new ChatActivity$$Lambda$26(this));
        this.bottomOverlayChatText = new TextView(context);
        this.bottomOverlayChatText.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        this.bottomOverlayProgress = new RadialProgressView(context);
        this.bottomOverlayProgress.setSize(AndroidUtilities.m9dp(22.0f));
        this.bottomOverlayProgress.setProgressColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        this.bottomOverlayProgress.setVisibility(4);
        this.bottomOverlayProgress.setScaleX(0.1f);
        this.bottomOverlayProgress.setScaleY(0.1f);
        this.bottomOverlayProgress.setAlpha(1.0f);
        this.bottomOverlayChat.addView(this.bottomOverlayProgress, LayoutHelper.createFrame(30, 30, 17));
        this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        this.chatAdapter.updateRows();
        if (this.loading && this.messages.isEmpty()) {
            this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? 0 : 4);
            this.chatListView.setEmptyView(null);
        } else {
            this.progressView.setVisibility(4);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        }
        checkBotKeyboard();
        updateContactStatus();
        updateBottomOverlay();
        updateSecretStatus();
        updateSpamView();
        updatePinnedMessageView(true);
        try {
            if (this.currentEncryptedChat != null && VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                MediaController.getInstance().setFlagSecure(this, true);
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
        if (oldMessage != null) {
            this.chatActivityEnterView.setFieldText(oldMessage);
        }
        fixLayoutInternal();
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$6$ChatActivity(View view) {
        if (this.floatingDateView.getAlpha() != 0.0f) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(((long) this.floatingDateView.getCustomDate()) * 1000);
            int year = calendar.get(1);
            int monthOfYear = calendar.get(2);
            int dayOfMonth = calendar.get(5);
            calendar.clear();
            calendar.set(year, monthOfYear, dayOfMonth);
            jumpToDate((int) (calendar.getTime().getTime() / 1000));
        }
    }

    final /* synthetic */ void lambda$createView$7$ChatActivity(View v) {
        this.wasManualScroll = true;
        if (this.chatInfo != null) {
            scrollToMessageId(this.chatInfo.pinned_msg_id, 0, true, 0, false);
        } else if (this.userInfo != null) {
            scrollToMessageId(this.userInfo.pinned_msg_id, 0, true, 0, false);
        }
    }

    final /* synthetic */ void lambda$createView$9$ChatActivity(View v) {
        boolean allowPin = false;
        if (getParentActivity() != null) {
            if (ChatObject.isChannel(this.currentChat)) {
                if (this.currentChat.creator || (this.currentChat.admin_rights != null && ((this.currentChat.megagroup && this.currentChat.admin_rights.pin_messages) || (!this.currentChat.megagroup && this.currentChat.admin_rights.edit_messages)))) {
                    allowPin = true;
                }
            } else if (this.currentEncryptedChat != null) {
                allowPin = false;
            } else if (this.userInfo != null) {
                allowPin = this.userInfo.can_pin_message;
            } else if (this.currentChat == null) {
                allowPin = false;
            } else if (this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled) {
                allowPin = true;
            }
            if (allowPin) {
                Builder builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("UnpinMessageAlert", R.string.UnpinMessageAlert));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$94(this));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
                return;
            }
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            if (this.chatInfo != null) {
                preferences.edit().putInt("pin_" + this.dialog_id, this.chatInfo.pinned_msg_id).commit();
            } else if (this.userInfo != null) {
                preferences.edit().putInt("pin_" + this.dialog_id, this.userInfo.pinned_msg_id).commit();
            }
            updatePinnedMessageView(true);
        }
    }

    final /* synthetic */ void lambda$null$8$ChatActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).pinMessage(this.currentChat, this.currentUser, 0, false);
    }

    final /* synthetic */ void lambda$createView$10$ChatActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("user_id", this.currentUser.var_id);
        args.putBoolean("addContact", true);
        presentFragment(new ContactAddActivity(args));
    }

    final /* synthetic */ void lambda$createView$12$ChatActivity(View v) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                builder.setMessage(LocaleController.getString("ReportSpamAlertChannel", R.string.ReportSpamAlertChannel));
            } else if (this.currentChat != null) {
                builder.setMessage(LocaleController.getString("ReportSpamAlertGroup", R.string.ReportSpamAlertGroup));
            } else {
                builder.setMessage(LocaleController.getString("ReportSpamAlert", R.string.ReportSpamAlert));
            }
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$93(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$null$11$ChatActivity(DialogInterface dialogInterface, int i) {
        if (this.currentUser != null) {
            MessagesController.getInstance(this.currentAccount).blockUser(this.currentUser.var_id);
        }
        MessagesController.getInstance(this.currentAccount).reportSpam(this.dialog_id, this.currentUser, this.currentChat, this.currentEncryptedChat);
        updateSpamView();
        if (this.currentChat == null) {
            MessagesController.getInstance(this.currentAccount).deleteDialog(this.dialog_id, 0);
        } else if (ChatObject.isNotInChat(this.currentChat)) {
            MessagesController.getInstance(this.currentAccount).deleteDialog(this.dialog_id, 0);
        } else {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat((int) (-this.dialog_id), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), null);
        }
        lambda$checkDiscard$2$PollCreateActivity();
    }

    final /* synthetic */ void lambda$createView$13$ChatActivity(View v) {
        MessagesController.getInstance(this.currentAccount).hideReportSpam(this.dialog_id, this.currentUser, this.currentChat);
        updateSpamView();
    }

    final /* synthetic */ void lambda$createView$14$ChatActivity(View view) {
        this.wasManualScroll = true;
        this.checkTextureViewPosition = true;
        if (this.createUnreadMessageAfterId != 0) {
            scrollToMessageId(this.createUnreadMessageAfterId, 0, false, this.returnToLoadIndex, false);
        } else if (this.returnToMessageId > 0) {
            scrollToMessageId(this.returnToMessageId, 0, true, this.returnToLoadIndex, false);
        } else {
            scrollToLastMessage(true);
        }
    }

    final /* synthetic */ boolean lambda$createView$15$ChatActivity(View view) {
        for (int a = 0; a < this.messages.size(); a++) {
            MessageObject messageObject = (MessageObject) this.messages.get(a);
            if (messageObject.messageOwner.mentioned && !messageObject.isContentUnread()) {
                messageObject.setContentIsRead();
            }
        }
        this.newMentionsCount = 0;
        MessagesController.getInstance(this.currentAccount).markMentionsAsRead(this.dialog_id);
        this.hasAllMentionsLocal = true;
        showMentiondownButton(false, true);
        return true;
    }

    final /* synthetic */ boolean lambda$createView$16$ChatActivity(View v, MotionEvent event) {
        return StickerPreviewViewer.getInstance().onTouch(event, this.mentionListView, 0, this.mentionsOnItemClickListener, null);
    }

    final /* synthetic */ void lambda$createView$17$ChatActivity(View view, int position) {
        if (!this.mentionsAdapter.isBannedInline()) {
            TLObject object = this.mentionsAdapter.getItem(position);
            int start = this.mentionsAdapter.getResultStartPosition();
            int len = this.mentionsAdapter.getResultLength();
            if (object instanceof User) {
                Spannable spannableString;
                if (this.searchingForUser && this.searchContainer.getVisibility() == 0) {
                    this.searchingUserMessages = (User) object;
                    if (this.searchingUserMessages != null) {
                        String name = this.searchingUserMessages.first_name;
                        if (TextUtils.isEmpty(name)) {
                            name = this.searchingUserMessages.last_name;
                        }
                        this.searchingForUser = false;
                        String from = LocaleController.getString("SearchFrom", R.string.SearchFrom);
                        spannableString = new SpannableString(from + " " + name);
                        spannableString.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_actionBarDefaultSubtitle)), from.length() + 1, spannableString.length(), 33);
                        this.searchItem.setSearchFieldCaption(spannableString);
                        this.mentionsAdapter.searchUsernameOrHashtag(null, 0, null, false);
                        this.searchItem.setSearchFieldHint(null);
                        this.searchItem.clearSearchText();
                        DataQuery.getInstance(this.currentAccount).searchMessagesInChat(TtmlNode.ANONYMOUS_REGION_ID, this.dialog_id, this.mergeDialogId, this.classGuid, 0, this.searchingUserMessages);
                        return;
                    }
                    return;
                }
                User user = (User) object;
                if (user == null) {
                    return;
                }
                if (user.username != null) {
                    this.chatActivityEnterView.replaceWithText(start, len, "@" + user.username + " ", false);
                    return;
                }
                spannableString = new SpannableString(UserObject.getFirstName(user, false) + " ");
                spannableString.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + user.var_id, 1), 0, spannableString.length(), 33);
                this.chatActivityEnterView.replaceWithText(start, len, spannableString, false);
            } else if (object instanceof String) {
                if (this.mentionsAdapter.isBotCommands()) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage((String) object, this.dialog_id, this.replyingMessageObject, null, false, null, null, null);
                    this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    hideFieldPanel();
                    return;
                }
                this.chatActivityEnterView.replaceWithText(start, len, object + " ", false);
            } else if (object instanceof BotInlineResult) {
                if (this.chatActivityEnterView.getFieldText() != null) {
                    BotInlineResult result = (BotInlineResult) object;
                    if ((!result.type.equals("photo") || (result.photo == null && result.content == null)) && ((!result.type.equals("gif") || (result.document == null && result.content == null)) && (!result.type.equals(MimeTypes.BASE_TYPE_VIDEO) || result.document == null))) {
                        sendBotInlineResult(result);
                        return;
                    }
                    ArrayList<Object> arrayList = new ArrayList(this.mentionsAdapter.getSearchResultBotContext());
                    this.botContextResults = arrayList;
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, this.mentionsAdapter.getItemPosition(position), 3, this.botContextProvider, null);
                }
            } else if (object instanceof TL_inlineBotSwitchPM) {
                processInlineBotContextPM((TL_inlineBotSwitchPM) object);
            } else if (object instanceof EmojiSuggestion) {
                String code = ((EmojiSuggestion) object).emoji;
                this.chatActivityEnterView.addEmojiToRecent(code);
                this.chatActivityEnterView.replaceWithText(start, len, code, true);
            }
        }
    }

    final /* synthetic */ boolean lambda$createView$19$ChatActivity(View view, int position) {
        boolean z = false;
        if (getParentActivity() == null || !this.mentionsAdapter.isLongClickEnabled()) {
            return false;
        }
        Object object = this.mentionsAdapter.getItem(position);
        if (!(object instanceof String)) {
            return false;
        }
        if (!this.mentionsAdapter.isBotCommands()) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
            builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new ChatActivity$$Lambda$92(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
            return true;
        } else if (!URLSpanBotCommand.enabled) {
            return false;
        } else {
            this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
            ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
            String str = (String) object;
            if (this.currentChat != null && this.currentChat.megagroup) {
                z = true;
            }
            chatActivityEnterView.setCommand(null, str, true, z);
            return true;
        }
    }

    final /* synthetic */ void lambda$null$18$ChatActivity(DialogInterface dialogInterface, int i) {
        this.mentionsAdapter.clearRecentHashtags();
    }

    final /* synthetic */ boolean lambda$createView$20$ChatActivity(View v, MotionEvent event) {
        if (event.getAction() == 0) {
            checkRecordLocked();
        }
        this.overlayView.getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    final /* synthetic */ void lambda$createView$21$ChatActivity(View v) {
        if (this.forwardingMessages != null && !this.forwardingMessages.isEmpty()) {
            int N = this.forwardingMessages.size();
            for (int a = 0; a < N; a++) {
                MessageObject messageObject = (MessageObject) this.forwardingMessages.get(a);
                this.selectedMessagesIds[0].put(messageObject.getId(), messageObject);
            }
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 3);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(this);
            presentFragment(fragment);
        } else if (this.replyingMessageObject != null) {
            scrollToMessageId(this.replyingMessageObject.getId(), 0, true, 0, false);
        } else if (this.editingMessageObject != null && this.editingMessageObject.canEditMedia() && this.editingMessageObjectReqId == 0) {
            if (this.chatAttachAlert == null) {
                createChatAttachView();
            }
            this.chatAttachAlert.setEditingMessageObject(this.editingMessageObject);
            openAttachMenu();
        }
    }

    final /* synthetic */ void lambda$createView$22$ChatActivity(View v) {
        if (this.forwardingMessages != null) {
            this.forwardingMessages.clear();
        }
        showFieldPanel(false, null, null, null, this.foundWebPage, true);
    }

    final /* synthetic */ boolean lambda$createView$23$ChatActivity(StickerPreviewViewerDelegate stickerPreviewViewerDelegate, View v, MotionEvent event) {
        return StickerPreviewViewer.getInstance().onTouch(event, this.stickersListView, 0, this.stickersOnItemClickListener, stickerPreviewViewerDelegate);
    }

    final /* synthetic */ void lambda$createView$25$ChatActivity(View view) {
        DataQuery.getInstance(this.currentAccount).searchMessagesInChat(null, this.dialog_id, this.mergeDialogId, this.classGuid, 1, this.searchingUserMessages);
    }

    final /* synthetic */ void lambda$createView$26$ChatActivity(View view) {
        DataQuery.getInstance(this.currentAccount).searchMessagesInChat(null, this.dialog_id, this.mergeDialogId, this.classGuid, 2, this.searchingUserMessages);
    }

    final /* synthetic */ void lambda$createView$27$ChatActivity(View view) {
        this.mentionLayoutManager.setReverseLayout(true);
        this.mentionsAdapter.setSearchingMentions(true);
        this.searchCalendarButton.setVisibility(8);
        this.searchUserButton.setVisibility(8);
        this.searchingForUser = true;
        this.searchingUserMessages = null;
        this.searchItem.setSearchFieldHint(LocaleController.getString("SearchMembers", R.string.SearchMembers));
        this.searchItem.setSearchFieldCaption(LocaleController.getString("SearchFrom", R.string.SearchFrom));
        AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
        this.searchItem.clearSearchText();
    }

    final /* synthetic */ void lambda$createView$31$ChatActivity(View view) {
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            Calendar calendar = Calendar.getInstance();
            try {
                DatePickerDialog dialog = new DatePickerDialog(getParentActivity(), new ChatActivity$$Lambda$89(this), calendar.get(1), calendar.get(2), calendar.get(5));
                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMinDate(1375315200000L);
                datePicker.setMaxDate(System.currentTimeMillis());
                dialog.setButton(-1, LocaleController.getString("JumpToDate", R.string.JumpToDate), dialog);
                dialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), ChatActivity$$Lambda$90.$instance);
                if (VERSION.SDK_INT >= 21) {
                    dialog.setOnShowListener(new ChatActivity$$Lambda$91(datePicker));
                }
                showDialog(dialog);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    final /* synthetic */ void lambda$null$28$ChatActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        int date = (int) (calendar1.getTime().getTime() / 1000);
        clearChatData();
        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        long j = this.dialog_id;
        int i = this.classGuid;
        boolean isChannel = ChatObject.isChannel(this.currentChat);
        int i2 = this.lastLoadIndex;
        this.lastLoadIndex = i2 + 1;
        instance.loadMessages(j, 30, 0, date, true, 0, i, 4, 0, isChannel, i2);
    }

    static final /* synthetic */ void lambda$null$29$ChatActivity(DialogInterface dialog1, int which) {
    }

    static final /* synthetic */ void lambda$null$30$ChatActivity(DatePicker datePicker, DialogInterface dialog12) {
        int count = datePicker.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = datePicker.getChildAt(a);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = -1;
            child.setLayoutParams(layoutParams);
        }
    }

    final /* synthetic */ void lambda$createView$34$ChatActivity(View view) {
        if (getParentActivity() != null) {
            Builder builder = null;
            if (this.currentUser == null || !this.userBlocked) {
                if (this.currentUser != null && this.currentUser.bot && this.botUser != null) {
                    if (this.botUser.length() != 0) {
                        MessagesController.getInstance(this.currentAccount).sendBotStart(this.currentUser, this.botUser);
                    } else {
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage("/start", this.dialog_id, null, null, false, null, null, null);
                    }
                    this.botUser = null;
                    updateBottomOverlay();
                } else if (!ChatObject.isChannel(this.currentChat) || (this.currentChat instanceof TL_channelForbidden)) {
                    builder = new Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$88(this));
                } else if (ChatObject.isNotInChat(this.currentChat)) {
                    showBottomOverlayProgress(true, true);
                    MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.var_id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), null, 0, null, this);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
                } else {
                    toggleMute(true);
                }
            } else if (this.currentUser.bot) {
                String botUserLast = this.botUser;
                this.botUser = null;
                MessagesController.getInstance(this.currentAccount).unblockUser(this.currentUser.var_id);
                if (botUserLast == null || botUserLast.length() == 0) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage("/start", this.dialog_id, null, null, false, null, null, null);
                } else {
                    MessagesController.getInstance(this.currentAccount).sendBotStart(this.currentUser, botUserLast);
                }
            } else {
                builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", R.string.AreYouSureUnblockContact));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$87(this));
            }
            if (builder != null) {
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
        }
    }

    final /* synthetic */ void lambda$null$32$ChatActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).unblockUser(this.currentUser.var_id);
    }

    final /* synthetic */ void lambda$null$33$ChatActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).deleteDialog(this.dialog_id, 0);
        lambda$checkDiscard$2$PollCreateActivity();
    }

    private TextureView createTextureView(boolean add) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (VERSION.SDK_INT >= 21) {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChatActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer.setOutlineProvider(new CLASSNAME());
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                        super.onSizeChanged(w, h, oldw, oldh);
                        ChatActivity.this.aspectPath.reset();
                        ChatActivity.this.aspectPath.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Direction.CW);
                        ChatActivity.this.aspectPath.toggleInverseFillType();
                    }

                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChatActivity.this.contentView.invalidate();
                    }

                    public void setVisibility(int visibility) {
                        super.setVisibility(visibility);
                        if (visibility == 0) {
                            setLayerType(2, null);
                        }
                    }

                    protected void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(ChatActivity.this.aspectPath, ChatActivity.this.aspectPaint);
                    }
                };
                this.aspectPath = new Path();
                this.aspectPaint = new Paint(1);
                this.aspectPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
                this.aspectPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            }
            this.roundVideoContainer.setWillNotDraw(false);
            this.roundVideoContainer.setVisibility(4);
            this.aspectRatioFrameLayout = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout.setBackgroundColor(0);
            if (add) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            this.videoTextureView = new TextureView(getParentActivity());
            this.videoTextureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        ViewGroup parent = (ViewGroup) this.roundVideoContainer.getParent();
        if (!(parent == null || parent == this.contentView)) {
            parent.removeView(this.roundVideoContainer);
            parent = null;
        }
        if (parent == null) {
            this.contentView.addView(this.roundVideoContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    private void destroyTextureView() {
        if (this.roundVideoContainer != null && this.roundVideoContainer.getParent() != null) {
            this.contentView.removeView(this.roundVideoContainer);
            this.aspectRatioFrameLayout.setDrawingReady(false);
            this.roundVideoContainer.setVisibility(4);
            if (VERSION.SDK_INT < 21) {
                this.roundVideoContainer.setLayerType(0, null);
            }
        }
    }

    private void showBottomOverlayProgress(final boolean show, boolean animated) {
        int i = 4;
        float f = 0.1f;
        float f2 = 1.0f;
        if (show && this.bottomOverlayProgress.getTag() != null) {
            return;
        }
        if (show || this.bottomOverlayProgress.getTag() != null) {
            if (this.bottomOverlayAnimation != null) {
                this.bottomOverlayAnimation.cancel();
                this.bottomOverlayAnimation = null;
            }
            this.bottomOverlayProgress.setTag(show ? Integer.valueOf(1) : null);
            if (animated) {
                this.bottomOverlayAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (show) {
                    this.bottomOverlayProgress.setVisibility(0);
                    animatorSet = this.bottomOverlayAnimation;
                    animatorArr = new Animator[6];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.bottomOverlayChatText, "scaleX", new float[]{0.1f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.bottomOverlayChatText, "scaleY", new float[]{0.1f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.bottomOverlayChatText, "alpha", new float[]{0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.bottomOverlayProgress, "scaleX", new float[]{1.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.bottomOverlayProgress, "scaleY", new float[]{1.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.bottomOverlayProgress, "alpha", new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    this.bottomOverlayChatText.setVisibility(0);
                    animatorSet = this.bottomOverlayAnimation;
                    animatorArr = new Animator[6];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.bottomOverlayProgress, "scaleX", new float[]{0.1f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.bottomOverlayProgress, "scaleY", new float[]{0.1f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.bottomOverlayProgress, "alpha", new float[]{0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.bottomOverlayChatText, "scaleX", new float[]{1.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.bottomOverlayChatText, "scaleY", new float[]{1.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.bottomOverlayChatText, "alpha", new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.bottomOverlayAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.bottomOverlayAnimation != null && ChatActivity.this.bottomOverlayAnimation.equals(animation)) {
                            if (show) {
                                ChatActivity.this.bottomOverlayChatText.setVisibility(4);
                            } else {
                                ChatActivity.this.bottomOverlayProgress.setVisibility(4);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.bottomOverlayAnimation != null && ChatActivity.this.bottomOverlayAnimation.equals(animation)) {
                            ChatActivity.this.bottomOverlayAnimation = null;
                        }
                    }
                });
                this.bottomOverlayAnimation.setDuration(150);
                this.bottomOverlayAnimation.start();
                return;
            }
            float f3;
            this.bottomOverlayProgress.setVisibility(show ? 0 : 4);
            RadialProgressView radialProgressView = this.bottomOverlayProgress;
            if (show) {
                f3 = 1.0f;
            } else {
                f3 = 0.1f;
            }
            radialProgressView.setScaleX(f3);
            radialProgressView = this.bottomOverlayProgress;
            if (show) {
                f3 = 1.0f;
            } else {
                f3 = 0.1f;
            }
            radialProgressView.setScaleY(f3);
            RadialProgressView radialProgressView2 = this.bottomOverlayProgress;
            if (show) {
            }
            radialProgressView2.setAlpha(1.0f);
            TextView textView = this.bottomOverlayChatText;
            if (!show) {
                i = 0;
            }
            textView.setVisibility(i);
            TextView textView2 = this.bottomOverlayChatText;
            if (show) {
                f3 = 0.1f;
            } else {
                f3 = 1.0f;
            }
            textView2.setScaleX(f3);
            textView = this.bottomOverlayChatText;
            if (!show) {
                f = 1.0f;
            }
            textView.setScaleY(f);
            textView = this.bottomOverlayChatText;
            if (show) {
                f2 = 0.0f;
            }
            textView.setAlpha(f2);
        }
    }

    private void sendBotInlineResult(BotInlineResult result) {
        int uid = this.mentionsAdapter.getContextBotId();
        HashMap<String, String> params = new HashMap();
        params.put(TtmlNode.ATTR_ID, result.var_id);
        params.put("query_id", TtmlNode.ANONYMOUS_REGION_ID + result.query_id);
        params.put("bot", TtmlNode.ANONYMOUS_REGION_ID + uid);
        params.put("bot_name", this.mentionsAdapter.getContextBotName());
        SendMessagesHelper.prepareSendingBotContextResult(result, params, this.dialog_id, this.replyingMessageObject);
        this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
        hideFieldPanel();
        DataQuery.getInstance(this.currentAccount).increaseInlineRaiting(uid);
    }

    private void mentionListViewUpdateLayout() {
        if (this.mentionListView.getChildCount() <= 0) {
            this.mentionListViewScrollOffsetY = 0;
            this.mentionListViewLastViewPosition = -1;
            return;
        }
        View child = this.mentionListView.getChildAt(this.mentionListView.getChildCount() - 1);
        Holder holder = (Holder) this.mentionListView.findContainingViewHolder(child);
        int newOffset;
        RecyclerListView recyclerListView;
        if (this.mentionLayoutManager.getReverseLayout()) {
            if (holder != null) {
                this.mentionListViewLastViewPosition = holder.getAdapterPosition();
                this.mentionListViewLastViewTop = child.getBottom();
            } else {
                this.mentionListViewLastViewPosition = -1;
            }
            child = this.mentionListView.getChildAt(0);
            holder = (Holder) this.mentionListView.findContainingViewHolder(child);
            newOffset = (child.getBottom() >= this.mentionListView.getMeasuredHeight() || holder == null || holder.getAdapterPosition() != 0) ? this.mentionListView.getMeasuredHeight() : child.getBottom();
            if (this.mentionListViewScrollOffsetY != newOffset) {
                recyclerListView = this.mentionListView;
                this.mentionListViewScrollOffsetY = newOffset;
                recyclerListView.setBottomGlowOffset(newOffset);
                this.mentionListView.setTopGlowOffset(0);
                this.mentionListView.invalidate();
                this.mentionContainer.invalidate();
                return;
            }
            return;
        }
        if (holder != null) {
            this.mentionListViewLastViewPosition = holder.getAdapterPosition();
            this.mentionListViewLastViewTop = child.getTop();
        } else {
            this.mentionListViewLastViewPosition = -1;
        }
        child = this.mentionListView.getChildAt(0);
        holder = (Holder) this.mentionListView.findContainingViewHolder(child);
        if (child.getTop() <= 0 || holder == null || holder.getAdapterPosition() != 0) {
            newOffset = 0;
        } else {
            newOffset = child.getTop();
        }
        if (this.mentionListViewScrollOffsetY != newOffset) {
            recyclerListView = this.mentionListView;
            this.mentionListViewScrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            this.mentionListView.setBottomGlowOffset(0);
            this.mentionListView.invalidate();
            this.mentionContainer.invalidate();
        }
    }

    private void checkBotCommands() {
        boolean z = true;
        URLSpanBotCommand.enabled = false;
        if (this.currentUser != null && this.currentUser.bot) {
            URLSpanBotCommand.enabled = true;
        } else if (this.chatInfo instanceof TL_chatFull) {
            int a = 0;
            while (a < this.chatInfo.participants.participants.size()) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.chatInfo.participants.participants.get(a)).user_id));
                if (user == null || !user.bot) {
                    a++;
                } else {
                    URLSpanBotCommand.enabled = true;
                    return;
                }
            }
        } else if (this.chatInfo instanceof TL_channelFull) {
            if (this.chatInfo.bot_info.isEmpty() || this.currentChat == null || !this.currentChat.megagroup) {
                z = false;
            }
            URLSpanBotCommand.enabled = z;
        }
    }

    private GroupedMessages getValidGroupedMessage(MessageObject message) {
        if (message.getGroupId() == 0) {
            return null;
        }
        GroupedMessages groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(message.getGroupId());
        if (groupedMessages == null) {
            return groupedMessages;
        }
        if (groupedMessages.messages.size() <= 1 || groupedMessages.positions.get(message) == null) {
            return null;
        }
        return groupedMessages;
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
                        scrollToMessageId(message.getId(), 0, false, message.getDialogId() == this.mergeDialogId ? 1 : 0, false);
                        return;
                    }
                }
            } else if (((int) this.dialog_id) != 0) {
                clearChatData();
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController instance = MessagesController.getInstance(this.currentAccount);
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
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                if (this.dialog_id == ((long) user.var_id)) {
                    this.inlineReturn = this.dialog_id;
                    MessagesController.getInstance(this.currentAccount).sendBotStart(this.currentUser, object.start_param);
                    return;
                }
                Bundle args = new Bundle();
                args.putInt("user_id", user.var_id);
                args.putString("inline_query", object.start_param);
                args.putLong("inline_return", this.dialog_id);
                if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                    presentFragment(new ChatActivity(args));
                }
            }
        }
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            this.chatAttachAlert = new ChatAttachAlert(getParentActivity(), this);
            this.chatAttachAlert.setDelegate(new CLASSNAME());
        }
    }

    public long getDialogId() {
        return this.dialog_id;
    }

    public void setBotUser(String value) {
        if (this.inlineReturn != 0) {
            MessagesController.getInstance(this.currentAccount).sendBotStart(this.currentUser, value);
            return;
        }
        this.botUser = value;
        updateBottomOverlay();
    }

    public boolean playFirstUnreadVoiceMessage() {
        if (this.chatActivityEnterView != null && this.chatActivityEnterView.isRecordingAudioVideo()) {
            return true;
        }
        for (int a = this.messages.size() - 1; a >= 0; a--) {
            MessageObject messageObject = (MessageObject) this.messages.get(a);
            if ((messageObject.isVoice() || messageObject.isRoundVideo()) && messageObject.isContentUnread() && !messageObject.isOut()) {
                MediaController.getInstance().setVoiceMessagesPlaylist(MediaController.getInstance().playMessage(messageObject) ? createVoiceMessagesPlaylist(messageObject, true) : null, true);
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
                this.stickersListView.setPadding(AndroidUtilities.m9dp(18.0f), 0, AndroidUtilities.m9dp(18.0f), 0);
                RecyclerListView recyclerListView = this.stickersListView;
                Adapter stickersAdapter = new StickersAdapter(getParentActivity(), new ChatActivity$$Lambda$27(this));
                this.stickersAdapter = stickersAdapter;
                recyclerListView.setAdapter(stickersAdapter);
                recyclerListView = this.stickersListView;
                OnItemClickListener chatActivity$$Lambda$28 = new ChatActivity$$Lambda$28(this);
                this.stickersOnItemClickListener = chatActivity$$Lambda$28;
                recyclerListView.setOnItemClickListener(chatActivity$$Lambda$28);
            }
        }
    }

    final /* synthetic */ void lambda$initStickers$35$ChatActivity(final boolean show) {
        float f = 1.0f;
        if (!show || this.stickersPanel.getVisibility() != 0) {
            if (show || this.stickersPanel.getVisibility() != 8) {
                if (show) {
                    this.stickersListView.scrollToPosition(0);
                    this.stickersPanel.setVisibility(this.allowStickersPanel ? 0 : 4);
                }
                if (this.runningAnimation != null) {
                    this.runningAnimation.cancel();
                    this.runningAnimation = null;
                }
                if (this.stickersPanel.getVisibility() != 4) {
                    float f2;
                    this.runningAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.runningAnimation;
                    Animator[] animatorArr = new Animator[1];
                    FrameLayout frameLayout = this.stickersPanel;
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
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
                    animatorSet.playTogether(animatorArr);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() {
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
                    this.runningAnimation.start();
                } else if (!show) {
                    this.stickersPanel.setVisibility(8);
                }
            }
        }
    }

    final /* synthetic */ void lambda$initStickers$36$ChatActivity(View view, int position) {
        Document document = this.stickersAdapter.getItem(position);
        Object parent = this.stickersAdapter.getItemParent(position);
        if (document instanceof TL_document) {
            SendMessagesHelper.getInstance(this.currentAccount).sendSticker(document, this.dialog_id, this.replyingMessageObject, parent);
            hideFieldPanel();
            this.chatActivityEnterView.addStickerToRecent(document);
        }
        this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public void shareMyContact(MessageObject messageObject) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ShareYouPhoneNumberTitle", R.string.ShareYouPhoneNumberTitle));
        if (this.currentUser == null) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfo", R.string.AreYouSureShareMyContactInfo));
        } else if (this.currentUser.bot) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfoBot", R.string.AreYouSureShareMyContactInfoBot));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureShareMyContactInfoUser", R.string.AreYouSureShareMyContactInfoUser, CLASSNAMEPhoneFormat.getInstance().format("+" + UserConfig.getInstance(this.currentAccount).getCurrentUser().phone), ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name))));
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$29(this, messageObject));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$shareMyContact$37$ChatActivity(MessageObject messageObject, DialogInterface dialogInterface, int i) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(UserConfig.getInstance(this.currentAccount).getCurrentUser(), this.dialog_id, messageObject, null, null);
        moveScrollToLastMessage();
        hideFieldPanel();
    }

    private void hideVoiceHint() {
        this.voiceHintAnimation = new AnimatorSet();
        AnimatorSet animatorSet = this.voiceHintAnimation;
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.voiceHintTextView, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        this.voiceHintAnimation.addListener(new CLASSNAME());
        this.voiceHintAnimation.setDuration(300);
        this.voiceHintAnimation.start();
    }

    private void showVoiceHint(boolean hide, boolean video) {
        if (getParentActivity() != null && this.fragmentView != null) {
            if (!hide || this.voiceHintTextView != null) {
                if (this.voiceHintTextView == null) {
                    SizeNotifierFrameLayout frameLayout = this.fragmentView;
                    int index = frameLayout.indexOfChild(this.chatActivityEnterView);
                    if (index != -1) {
                        this.voiceHintTextView = new TextView(getParentActivity());
                        this.voiceHintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                        this.voiceHintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                        this.voiceHintTextView.setTextSize(1, 14.0f);
                        this.voiceHintTextView.setPadding(AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(7.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(7.0f));
                        this.voiceHintTextView.setGravity(16);
                        this.voiceHintTextView.setAlpha(0.0f);
                        frameLayout.addView(this.voiceHintTextView, index + 1, LayoutHelper.createFrame(-2, -2.0f, 85, 5.0f, 0.0f, 5.0f, 3.0f));
                    } else {
                        return;
                    }
                }
                if (hide) {
                    if (this.voiceHintAnimation != null) {
                        this.voiceHintAnimation.cancel();
                        this.voiceHintAnimation = null;
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.voiceHintHideRunnable);
                    this.voiceHintHideRunnable = null;
                    if (this.voiceHintTextView.getVisibility() == 0) {
                        hideVoiceHint();
                        return;
                    }
                    return;
                }
                this.voiceHintTextView.setText(video ? LocaleController.getString("HoldToVideo", R.string.HoldToVideo) : LocaleController.getString("HoldToAudio", R.string.HoldToAudio));
                if (this.voiceHintHideRunnable != null) {
                    if (this.voiceHintAnimation != null) {
                        this.voiceHintAnimation.cancel();
                        this.voiceHintAnimation = null;
                    } else {
                        AndroidUtilities.cancelRunOnUIThread(this.voiceHintHideRunnable);
                        Runnable chatActivity$$Lambda$30 = new ChatActivity$$Lambda$30(this);
                        this.voiceHintHideRunnable = chatActivity$$Lambda$30;
                        AndroidUtilities.runOnUIThread(chatActivity$$Lambda$30, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        return;
                    }
                } else if (this.voiceHintAnimation != null) {
                    return;
                }
                this.voiceHintTextView.setVisibility(0);
                this.voiceHintAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.voiceHintAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.voiceHintTextView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                this.voiceHintAnimation.addListener(new CLASSNAME());
                this.voiceHintAnimation.setDuration(300);
                this.voiceHintAnimation.start();
            }
        }
    }

    private void showMediaBannedHint() {
        if (getParentActivity() != null && this.currentChat != null && this.currentChat.banned_rights != null && this.fragmentView != null) {
            if (this.mediaBanTooltip == null || this.mediaBanTooltip.getVisibility() != 0) {
                SizeNotifierFrameLayout frameLayout = this.fragmentView;
                int index = frameLayout.indexOfChild(this.chatActivityEnterView);
                if (index != -1) {
                    if (this.mediaBanTooltip == null) {
                        this.mediaBanTooltip = new CorrectlyMeasuringTextView(getParentActivity());
                        this.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                        this.mediaBanTooltip.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                        this.mediaBanTooltip.setPadding(AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(7.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(7.0f));
                        this.mediaBanTooltip.setGravity(16);
                        this.mediaBanTooltip.setTextSize(1, 14.0f);
                        frameLayout.addView(this.mediaBanTooltip, index + 1, LayoutHelper.createFrame(-2, -2.0f, 85, 30.0f, 0.0f, 5.0f, 3.0f));
                    }
                    if (AndroidUtilities.isBannedForever(this.currentChat.banned_rights.until_date)) {
                        this.mediaBanTooltip.setText(LocaleController.getString("AttachMediaRestrictedForever", R.string.AttachMediaRestrictedForever));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", R.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) this.currentChat.banned_rights.until_date)));
                    }
                    this.mediaBanTooltip.setVisibility(0);
                    AnimatorSet AnimatorSet = new AnimatorSet();
                    AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, "alpha", new float[]{0.0f, 1.0f})});
                    AnimatorSet.addListener(new CLASSNAME());
                    AnimatorSet.setDuration(300);
                    AnimatorSet.start();
                }
            }
        }
    }

    private void showGifHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
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
                        frameLayout.addView(this.emojiButtonRed, index + 1, LayoutHelper.createFrame(10, 10.0f, 83, 30.0f, 0.0f, 0.0f, 27.0f));
                        this.gifHintTextView = new TextView(getParentActivity());
                        this.gifHintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                        this.gifHintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                        this.gifHintTextView.setTextSize(1, 14.0f);
                        this.gifHintTextView.setPadding(AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(7.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(7.0f));
                        this.gifHintTextView.setText(LocaleController.getString("TapHereGifs", R.string.TapHereGifs));
                        this.gifHintTextView.setGravity(16);
                        frameLayout.addView(this.gifHintTextView, index + 1, LayoutHelper.createFrame(-2, -2.0f, 83, 5.0f, 0.0f, 5.0f, 3.0f));
                        AnimatorSet AnimatorSet = new AnimatorSet();
                        AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.gifHintTextView, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.emojiButtonRed, "alpha", new float[]{0.0f, 1.0f})});
                        AnimatorSet.addListener(new CLASSNAME());
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
                this.mentionListAnimation.addListener(new CLASSNAME());
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
            this.mentionListAnimation.addListener(new CLASSNAME());
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
                    this.floatingDateAnimation.addListener(new CLASSNAME());
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

    protected void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, null, null, false);
    }

    protected void setIgnoreAttachOnPause(boolean value) {
        this.ignoreAttachOnPause = value;
    }

    private void checkScrollForLoad(boolean scroll) {
        if (this.chatLayoutManager != null && !this.paused) {
            int firstVisibleItem = this.chatLayoutManager.findFirstVisibleItemPosition();
            int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
            if (visibleItemCount > 0 || this.currentEncryptedChat != null) {
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
                if ((totalItemCount - firstVisibleItem) - visibleItemCount <= checkLoadCount && !this.loading) {
                    boolean z;
                    if (!this.endReached[0]) {
                        this.loading = true;
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        if (this.messagesByDays.size() != 0) {
                            instance = MessagesController.getInstance(this.currentAccount);
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
                            instance = MessagesController.getInstance(this.currentAccount);
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
                        instance = MessagesController.getInstance(this.currentAccount);
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
                if (visibleItemCount > 0 && !this.loadingForward && firstVisibleItem <= 10) {
                    if (this.mergeDialogId != 0 && !this.forwardEndReached[1]) {
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance(this.currentAccount);
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
                        instance = MessagesController.getInstance(this.currentAccount);
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
                    FileLog.m13e(e);
                    return;
                }
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
        } else if (which == 1) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                boolean allowGifs = (ChatObject.isChannel(this.currentChat) && this.currentChat.banned_rights != null && this.currentChat.banned_rights.send_gifs) ? false : this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(false, allowGifs, true, this);
                fragment.setMaxSelectedPhotos(this.editingMessageObject != null ? 1 : 100);
                fragment.setDelegate(new CLASSNAME());
                presentFragment(fragment);
                return;
            }
            try {
                getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
            } catch (Throwable th) {
            }
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
                    FileLog.m13e(e2);
                    return;
                }
            }
            try {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
            } catch (Throwable th2) {
            }
        } else if (which == 6) {
            if (AndroidUtilities.isGoogleMapsInstalled(this)) {
                LocationActivity fragment2 = new LocationActivity(this.currentEncryptedChat == null ? 1 : 0);
                fragment2.setDialogId(this.dialog_id);
                fragment2.setDelegate(this);
                presentFragment(fragment2);
            }
        } else if (which == 4) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                DocumentSelectActivity fragment3 = new DocumentSelectActivity(true);
                fragment3.setMaxSelectedFiles(this.editingMessageObject != null ? 1 : -1);
                fragment3.setDelegate(new CLASSNAME());
                presentFragment(fragment3);
                return;
            }
            try {
                getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
            } catch (Throwable th3) {
            }
        } else if (which == 3) {
            boolean allowPoll = false;
            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                allowPoll = ChatObject.isChannel(this.currentChat) && (this.currentChat.creator || this.currentChat.admin_rights != null);
            } else if (this.currentChat != null) {
                allowPoll = true;
            }
            if (allowPoll) {
                PollCreateActivity pollCreateActivity = new PollCreateActivity();
                pollCreateActivity.setDelegate(new ChatActivity$$Lambda$31(this));
                presentFragment(pollCreateActivity);
            } else if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                AudioSelectActivity fragment4 = new AudioSelectActivity();
                fragment4.setDelegate(new ChatActivity$$Lambda$32(this));
                presentFragment(fragment4);
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
            }
        } else if (which != 5) {
        } else {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                PhonebookSelectActivity activity = new PhonebookSelectActivity();
                activity.setDelegate(new ChatActivity$$Lambda$33(this));
                presentFragment(activity);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 5);
        }
    }

    final /* synthetic */ void lambda$processSelectedAttach$38$ChatActivity(TL_messageMediaPoll poll) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(poll, this.dialog_id, this.replyingMessageObject, null, null);
        hideFieldPanel();
        DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
    }

    final /* synthetic */ void lambda$processSelectedAttach$39$ChatActivity(ArrayList audios) {
        fillEditingMediaWithCaption(null, null);
        SendMessagesHelper.prepareSendingAudioDocuments(audios, this.dialog_id, this.replyingMessageObject, this.editingMessageObject);
        hideFieldPanel();
        DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
    }

    final /* synthetic */ void lambda$processSelectedAttach$40$ChatActivity(User user) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(user, this.dialog_id, this.replyingMessageObject, null, null);
        hideFieldPanel();
        DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    private void searchLinks(CharSequence charSequence, boolean force) {
        if (this.currentEncryptedChat == null || (MessagesController.getInstance(this.currentAccount).secretWebpagePreview != 0 && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) {
            if (force && this.foundWebPage != null) {
                if (this.foundWebPage.url != null) {
                    int index = TextUtils.indexOf(charSequence, this.foundWebPage.url);
                    char lastChar = 0;
                    boolean lenEqual = false;
                    if (index != -1) {
                        if (this.foundWebPage.url.length() + index == charSequence.length()) {
                            lenEqual = true;
                        } else {
                            lenEqual = false;
                        }
                        if (lenEqual) {
                            lastChar = 0;
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
                            lastChar = 0;
                        } else {
                            lastChar = charSequence.charAt(this.foundWebPage.display_url.length() + index);
                        }
                    }
                    if (index != -1 && (lenEqual || lastChar == ' ' || lastChar == ',' || lastChar == '.' || lastChar == '!' || lastChar == '/')) {
                        return;
                    }
                }
                this.pendingLinkSearchString = null;
                this.foundUrls = null;
                showFieldPanelForWebPage(false, this.foundWebPage, false);
            }
            Utilities.searchQueue.postRunnable(new ChatActivity$$Lambda$34(this, charSequence, MessagesController.getInstance(this.currentAccount), force));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00af A:{Catch:{ Exception -> 0x00dd }} */
    /* JADX WARNING: Removed duplicated region for block: B:83:? A:{SYNTHETIC, RETURN, ORIG_RETURN, Catch:{ Exception -> 0x00dd }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x010e A:{SYNTHETIC, Splitter: B:58:0x010e} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00d2 A:{Catch:{ Exception -> 0x00dd }} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0171  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x013f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$searchLinks$47$ChatActivity(CharSequence charSequence, MessagesController messagesController, boolean force) {
        ArrayList<CharSequence> urls;
        Throwable e;
        CharSequence textToCheck;
        TL_messages_getWebPagePreview req;
        int a;
        boolean clear;
        if (this.linkSearchRequestId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.linkSearchRequestId, true);
            this.linkSearchRequestId = 0;
        }
        Matcher m = AndroidUtilities.WEB_URL.matcher(charSequence);
        ArrayList<CharSequence> urls2 = null;
        while (m.find()) {
            try {
                if (m.start() > 0) {
                    if (charSequence.charAt(m.start() - 1) == '@') {
                        continue;
                    }
                }
                if (urls2 == null) {
                    urls = new ArrayList();
                } else {
                    urls = urls2;
                }
            } catch (Exception e2) {
                e = e2;
                urls = urls2;
                FileLog.m13e(e);
                String text = charSequence.toString().toLowerCase();
                if (charSequence.length() < 13 || !(text.contains("http://") || text.contains("https://"))) {
                    AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$82(this));
                    return;
                }
                textToCheck = charSequence;
                if (this.currentEncryptedChat == null) {
                }
                req = new TL_messages_getWebPagePreview();
                if (textToCheck instanceof String) {
                }
                this.linkSearchRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatActivity$$Lambda$84(this, req));
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.linkSearchRequestId, this.classGuid);
                return;
            }
            try {
                urls.add(charSequence.subSequence(m.start(), m.end()));
                urls2 = urls;
            } catch (Exception e3) {
                e = e3;
            }
        }
        if (charSequence instanceof Spannable) {
            URLSpanReplacement[] spans = (URLSpanReplacement[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), URLSpanReplacement.class);
            if (spans != null && spans.length > 0) {
                if (urls2 == null) {
                    urls = new ArrayList();
                } else {
                    urls = urls2;
                }
                for (URLSpanReplacement url : spans) {
                    urls.add(url.getURL());
                }
                if (!(urls == null || this.foundUrls == null || urls.size() != this.foundUrls.size())) {
                    clear = true;
                    for (a = 0; a < urls.size(); a++) {
                        if (!TextUtils.equals((CharSequence) urls.get(a), (CharSequence) this.foundUrls.get(a))) {
                            clear = false;
                        }
                    }
                    if (clear) {
                        return;
                    }
                }
                this.foundUrls = urls;
                if (urls != null) {
                    AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$81(this));
                    return;
                }
                textToCheck = TextUtils.join(" ", urls);
                if (this.currentEncryptedChat == null && messagesController.secretWebpagePreview == 2) {
                    AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$83(this, messagesController, charSequence, force));
                    return;
                }
                req = new TL_messages_getWebPagePreview();
                if (textToCheck instanceof String) {
                    req.message = (String) textToCheck;
                } else {
                    req.message = textToCheck.toString();
                }
                this.linkSearchRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatActivity$$Lambda$84(this, req));
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.linkSearchRequestId, this.classGuid);
                return;
            }
        }
        urls = urls2;
        clear = true;
        while (a < urls.size()) {
        }
        if (clear) {
        }
        this.foundUrls = urls;
        if (urls != null) {
        }
    }

    final /* synthetic */ void lambda$null$41$ChatActivity() {
        if (this.foundWebPage != null) {
            showFieldPanelForWebPage(false, this.foundWebPage, false);
            this.foundWebPage = null;
        }
    }

    final /* synthetic */ void lambda$null$42$ChatActivity() {
        if (this.foundWebPage != null) {
            showFieldPanelForWebPage(false, this.foundWebPage, false);
            this.foundWebPage = null;
        }
    }

    final /* synthetic */ void lambda$null$44$ChatActivity(MessagesController messagesController, CharSequence charSequence, boolean force) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$86(this, messagesController, charSequence, force));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setMessage(LocaleController.getString("SecretLinkPreviewAlert", R.string.SecretLinkPreviewAlert));
        showDialog(builder.create());
        messagesController.secretWebpagePreview = 0;
        MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", messagesController.secretWebpagePreview).commit();
    }

    final /* synthetic */ void lambda$null$43$ChatActivity(MessagesController messagesController, CharSequence charSequence, boolean force, DialogInterface dialog, int which) {
        messagesController.secretWebpagePreview = 1;
        MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", MessagesController.getInstance(this.currentAccount).secretWebpagePreview).commit();
        this.foundUrls = null;
        searchLinks(charSequence, force);
    }

    final /* synthetic */ void lambda$null$46$ChatActivity(TL_messages_getWebPagePreview req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$85(this, error, response, req));
    }

    final /* synthetic */ void lambda$null$45$ChatActivity(TL_error error, TLObject response, TL_messages_getWebPagePreview req) {
        this.linkSearchRequestId = 0;
        if (error != null) {
            return;
        }
        if (response instanceof TL_messageMediaWebPage) {
            this.foundWebPage = ((TL_messageMediaWebPage) response).webpage;
            if ((this.foundWebPage instanceof TL_webPage) || (this.foundWebPage instanceof TL_webPagePending)) {
                if (this.foundWebPage instanceof TL_webPagePending) {
                    this.pendingLinkSearchString = req.message;
                }
                if (this.currentEncryptedChat != null && (this.foundWebPage instanceof TL_webPagePending)) {
                    this.foundWebPage.url = req.message;
                }
                showFieldPanelForWebPage(true, this.foundWebPage, false);
            } else if (this.foundWebPage != null) {
                showFieldPanelForWebPage(false, this.foundWebPage, false);
                this.foundWebPage = null;
            }
        } else if (this.foundWebPage != null) {
            showFieldPanelForWebPage(false, this.foundWebPage, false);
            this.foundWebPage = null;
        }
    }

    private void forwardMessages(ArrayList<MessageObject> arrayList, boolean fromMyName) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (fromMyName) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    SendMessagesHelper.getInstance(this.currentAccount).processForwardFromMyName((MessageObject) it.next(), this.dialog_id);
                }
                return;
            }
            AlertsCreator.showSendMediaAlert(SendMessagesHelper.getInstance(this.currentAccount).sendMessage(arrayList, this.dialog_id), this);
        }
    }

    private void checkBotKeyboard() {
        if (this.chatActivityEnterView != null && this.botButtons != null && !this.userBlocked) {
            if (!(this.botButtons.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply)) {
                if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                    this.botReplyButtons = null;
                    hideFieldPanel();
                }
                this.chatActivityEnterView.setButtons(this.botButtons);
            } else if (MessagesController.getMainSettings(this.currentAccount).getInt("answered_" + this.dialog_id, 0) == this.botButtons.getId()) {
            } else {
                if (this.replyingMessageObject == null || this.chatActivityEnterView.getFieldText() == null) {
                    this.botReplyButtons = this.botButtons;
                    this.chatActivityEnterView.setButtons(this.botButtons);
                    showFieldPanelForReply(true, this.botButtons);
                }
            }
        }
    }

    public void hideFieldPanel() {
        showFieldPanel(false, null, null, null, null, false);
    }

    public void showFieldPanelForWebPage(boolean show, WebPage webPage, boolean cancel) {
        showFieldPanel(show, null, null, null, webPage, cancel);
    }

    public void showFieldPanelForForward(boolean show, ArrayList<MessageObject> messageObjectsToForward) {
        showFieldPanel(show, null, null, messageObjectsToForward, null, false);
    }

    public void showFieldPanelForReply(boolean show, MessageObject messageObjectToReply) {
        showFieldPanel(show, messageObjectToReply, null, null, null, false);
    }

    public void showFieldPanelForEdit(boolean show, MessageObject messageObjectToEdit) {
        showFieldPanel(show, null, messageObjectToEdit, null, null, false);
    }

    public void showFieldPanel(boolean show, MessageObject messageObjectToReply, MessageObject messageObjectToEdit, ArrayList<MessageObject> messageObjectsToForward, WebPage webPage, boolean cancel) {
        if (this.chatActivityEnterView != null) {
            if (show) {
                if (messageObjectToReply != null || messageObjectsToForward != null || messageObjectToEdit != null || webPage != null) {
                    MessageObject thumbMediaMessageObject;
                    if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                        this.actionBar.closeSearchField(false);
                        this.chatActivityEnterView.setFieldFocused();
                        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$35(this), 100);
                    }
                    boolean openKeyboard = false;
                    if (!(messageObjectToReply == null || messageObjectToReply.getDialogId() == this.dialog_id)) {
                        messageObjectsToForward = new ArrayList();
                        messageObjectsToForward.add(messageObjectToReply);
                        messageObjectToReply = null;
                        openKeyboard = true;
                    }
                    String mess;
                    User user;
                    String name;
                    Chat chat;
                    if (messageObjectToEdit != null) {
                        this.forwardingMessages = null;
                        this.replyingMessageObject = null;
                        this.editingMessageObject = messageObjectToEdit;
                        this.chatActivityEnterView.setReplyingMessageObject(null);
                        this.chatActivityEnterView.setEditingMessageObject(messageObjectToEdit, !messageObjectToEdit.isMediaEmpty());
                        if (this.foundWebPage == null) {
                            this.chatActivityEnterView.setForceShowSendButton(false, false);
                            this.replyIconImageView.setImageResource(R.drawable.group_edit);
                            if (messageObjectToEdit.isMediaEmpty()) {
                                this.replyNameTextView.setText(LocaleController.getString("EditMessage", R.string.EditMessage));
                            } else {
                                this.replyNameTextView.setText(LocaleController.getString("EditCaption", R.string.EditCaption));
                            }
                            if (messageObjectToEdit.canEditMedia()) {
                                this.replyObjectTextView.setText(LocaleController.getString("EditMessageMedia", R.string.EditMessageMedia));
                            } else if (messageObjectToEdit.messageText != null) {
                                mess = messageObjectToEdit.messageText.toString();
                                if (mess.length() > 150) {
                                    mess = mess.substring(0, 150);
                                }
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(mess.replace(10, ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
                            }
                        } else {
                            return;
                        }
                    } else if (messageObjectToReply != null) {
                        this.forwardingMessages = null;
                        this.editingMessageObject = null;
                        this.replyingMessageObject = messageObjectToReply;
                        this.chatActivityEnterView.setReplyingMessageObject(messageObjectToReply);
                        this.chatActivityEnterView.setEditingMessageObject(null, false);
                        if (this.foundWebPage == null) {
                            this.chatActivityEnterView.setForceShowSendButton(false, false);
                            if (messageObjectToReply.isFromUser()) {
                                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObjectToReply.messageOwner.from_id));
                                if (user != null) {
                                    name = UserObject.getUserName(user);
                                } else {
                                    return;
                                }
                            }
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageObjectToReply.messageOwner.to_id.channel_id));
                            if (chat != null) {
                                name = chat.title;
                            } else {
                                return;
                            }
                            this.replyIconImageView.setImageResource(R.drawable.msg_panel_reply);
                            this.replyNameTextView.setText(name);
                            if (messageObjectToReply.messageOwner.media instanceof TL_messageMediaGame) {
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(messageObjectToReply.messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
                            } else if (messageObjectToReply.messageText != null) {
                                mess = messageObjectToReply.messageText.toString();
                                if (mess.length() > 150) {
                                    mess = mess.substring(0, 150);
                                }
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(mess.replace(10, ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
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
                        this.editingMessageObject = null;
                        this.chatActivityEnterView.setReplyingMessageObject(null);
                        this.chatActivityEnterView.setEditingMessageObject(null, false);
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
                                    user = MessagesController.getInstance(this.currentAccount).getUser(uid);
                                } else {
                                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid.intValue()));
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
                                        } else if (!TextUtils.isEmpty(user.first_name)) {
                                            userNames.append(user.first_name);
                                        } else if (TextUtils.isEmpty(user.last_name)) {
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
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMessageCount", messageObjectsToForward.size()));
                                } else {
                                    MessageObject messageObject = (MessageObject) messageObjectsToForward.get(0);
                                    if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                                        this.replyObjectTextView.setText(Emoji.replaceEmoji(messageObject.messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
                                    } else {
                                        mess = messageObject.messageText.toString();
                                        if (mess.length() > 150) {
                                            mess = mess.substring(0, 150);
                                        }
                                        this.replyObjectTextView.setText(Emoji.replaceEmoji(mess.replace(10, ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
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
                            } else if (type == 5) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedRound", messageObjectsToForward.size()));
                            } else if (type == 14) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMusic", messageObjectsToForward.size()));
                            } else if (type == 13) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedSticker", messageObjectsToForward.size()));
                            } else if (type == 17) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedPoll", messageObjectsToForward.size()));
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
                    if (messageObjectToReply != null) {
                        thumbMediaMessageObject = messageObjectToReply;
                    } else if (messageObjectToEdit != null) {
                        thumbMediaMessageObject = messageObjectToEdit;
                    } else {
                        thumbMediaMessageObject = null;
                    }
                    FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.replyNameTextView.getLayoutParams();
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.replyObjectTextView.getLayoutParams();
                    PhotoSize photoSize = null;
                    if (thumbMediaMessageObject != null) {
                        photoSize = FileLoader.getClosestPhotoSizeWithSize(thumbMediaMessageObject.photoThumbs2, 80);
                        if (photoSize == null) {
                            photoSize = FileLoader.getClosestPhotoSizeWithSize(thumbMediaMessageObject.photoThumbs, 80);
                        }
                    }
                    int dp;
                    if (photoSize == null || (photoSize instanceof TL_photoSizeEmpty) || (photoSize.location instanceof TL_fileLocationUnavailable) || thumbMediaMessageObject.type == 13 || (thumbMediaMessageObject != null && thumbMediaMessageObject.isSecretMedia())) {
                        this.replyImageView.setImageBitmap(null);
                        this.replyImageLocation = null;
                        this.replyImageView.setVisibility(4);
                        dp = AndroidUtilities.m9dp(52.0f);
                        layoutParams2.leftMargin = dp;
                        layoutParams1.leftMargin = dp;
                    } else {
                        if (thumbMediaMessageObject == null || !thumbMediaMessageObject.isRoundVideo()) {
                            this.replyImageView.setRoundRadius(0);
                        } else {
                            this.replyImageView.setRoundRadius(AndroidUtilities.m9dp(17.0f));
                        }
                        this.replyImageLocation = photoSize.location;
                        this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null, (Object) thumbMediaMessageObject);
                        this.replyImageView.setVisibility(0);
                        dp = AndroidUtilities.m9dp(96.0f);
                        layoutParams2.leftMargin = dp;
                        layoutParams1.leftMargin = dp;
                    }
                    this.replyNameTextView.setLayoutParams(layoutParams1);
                    this.replyObjectTextView.setLayoutParams(layoutParams2);
                    this.chatActivityEnterView.showTopView(false, openKeyboard);
                }
            } else if (this.replyingMessageObject != null || this.forwardingMessages != null || this.foundWebPage != null || this.editingMessageObject != null) {
                if (this.replyingMessageObject != null && (this.replyingMessageObject.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply)) {
                    MessagesController.getMainSettings(this.currentAccount).edit().putInt("answered_" + this.dialog_id, this.replyingMessageObject.getId()).commit();
                }
                if (this.foundWebPage != null) {
                    this.foundWebPage = null;
                    this.chatActivityEnterView.setWebPage(null, !cancel);
                    if (!(webPage == null || (this.replyingMessageObject == null && this.forwardingMessages == null && this.editingMessageObject == null))) {
                        showFieldPanel(true, this.replyingMessageObject, this.editingMessageObject, this.forwardingMessages, null, false);
                        return;
                    }
                }
                if (this.forwardingMessages != null) {
                    forwardMessages(this.forwardingMessages, false);
                }
                this.chatActivityEnterView.setForceShowSendButton(false, false);
                this.chatActivityEnterView.hideTopView(false);
                this.chatActivityEnterView.setReplyingMessageObject(null);
                this.chatActivityEnterView.setEditingMessageObject(null, false);
                this.replyingMessageObject = null;
                this.editingMessageObject = null;
                this.forwardingMessages = null;
                this.replyImageLocation = null;
            }
        }
    }

    final /* synthetic */ void lambda$showFieldPanel$48$ChatActivity() {
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.openKeyboard();
        }
    }

    private void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    private boolean sendSecretMessageRead(MessageObject messageObject) {
        int i = 0;
        if (messageObject == null || messageObject.isOut() || !messageObject.isSecretMedia() || messageObject.messageOwner.destroyTime != 0 || messageObject.messageOwner.ttl <= 0) {
            return false;
        }
        if (this.currentEncryptedChat != null) {
            MessagesController.getInstance(this.currentAccount).markMessageAsRead(this.dialog_id, messageObject.messageOwner.random_id, messageObject.messageOwner.ttl);
        } else {
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            int id = messageObject.getId();
            if (ChatObject.isChannel(this.currentChat)) {
                i = this.currentChat.var_id;
            }
            instance.markMessageAsRead(id, i, null, messageObject.messageOwner.ttl, 0);
        }
        messageObject.messageOwner.destroyTime = messageObject.messageOwner.ttl + ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        return true;
    }

    private void clearChatData() {
        this.messages.clear();
        this.messagesByDays.clear();
        this.waitingForLoad.clear();
        this.groupedMessagesMap.clear();
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
        this.unreadMessageObject = null;
        this.createUnreadMessageAfterId = 0;
        this.createUnreadMessageAfterIdLoading = false;
        this.needSelectFromMessageId = false;
        this.chatAdapter.notifyDataSetChanged();
    }

    private void scrollToLastMessage(boolean pagedown) {
        if (!this.forwardEndReached[0] || this.first_unread_id != 0 || this.startLoadFromMessageId != 0) {
            clearChatData();
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            long j = this.dialog_id;
            int i = this.classGuid;
            boolean isChannel = ChatObject.isChannel(this.currentChat);
            int i2 = this.lastLoadIndex;
            this.lastLoadIndex = i2 + 1;
            instance.loadMessages(j, 30, 0, 0, true, 0, i, 0, 0, isChannel, i2);
        } else if (pagedown && this.chatLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            showPagedownButton(false, true);
            this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            updateVisibleRows();
        } else {
            this.chatLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0087  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateTextureViewPosition() {
        if (this.fragmentView != null && !this.paused) {
            boolean foundTextureViewMessage = false;
            int count = this.chatListView.getChildCount();
            int additionalTop = this.chatActivityEnterView.isTopViewVisible() ? AndroidUtilities.m9dp(48.0f) : 0;
            for (int a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    MessageObject messageObject = messageCell.getMessageObject();
                    if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                        ImageReceiver imageReceiver = messageCell.getPhotoImage();
                        this.roundVideoContainer.setTranslationX(((float) imageReceiver.getImageX()) + messageCell.getTranslationX());
                        this.roundVideoContainer.setTranslationY((float) ((this.inPreviewMode ? AndroidUtilities.statusBarHeight : 0) + (((this.fragmentView.getPaddingTop() + messageCell.getTop()) + imageReceiver.getImageY()) - additionalTop)));
                        this.fragmentView.invalidate();
                        this.roundVideoContainer.invalidate();
                        foundTextureViewMessage = true;
                        if (this.roundVideoContainer == null) {
                            messageObject = MediaController.getInstance().getPlayingMessageObject();
                            if (messageObject != null && messageObject.eventId == 0) {
                                if (foundTextureViewMessage) {
                                    MediaController.getInstance().setCurrentRoundVisible(true);
                                    scrollToMessageId(messageObject.getId(), 0, false, 0, true);
                                    return;
                                }
                                this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                                this.fragmentView.invalidate();
                                if (messageObject != null && messageObject.isRoundVideo()) {
                                    if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                                        MediaController.getInstance().setCurrentRoundVisible(false);
                                        return;
                                    } else {
                                        scrollToMessageId(messageObject.getId(), 0, false, 0, true);
                                        return;
                                    }
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    }
                }
            }
            if (this.roundVideoContainer == null) {
            }
        }
    }

    private void updateMessagesVisiblePart(boolean inLayout) {
        if (this.chatListView != null) {
            int a;
            MessageObject messageObject;
            int id;
            int count = this.chatListView.getChildCount();
            int additionalTop = this.chatActivityEnterView.isTopViewVisible() ? AndroidUtilities.m9dp(48.0f) : 0;
            int height = this.chatListView.getMeasuredHeight();
            int minPositionHolder = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int minPositionDateHolder = ConnectionsManager.DEFAULT_DATACENTER_ID;
            View minDateChild = null;
            View minChild = null;
            View minMessageChild = null;
            boolean foundTextureViewMessage = false;
            int maxPositiveUnreadId = Integer.MIN_VALUE;
            int maxNegativeUnreadId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int maxUnreadDate = Integer.MIN_VALUE;
            if (this.currentEncryptedChat != null) {
            }
            this.pollsToCheck.clear();
            for (a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                messageObject = null;
                if (view instanceof ChatMessageCell) {
                    int viewTop;
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    int top = messageCell.getTop();
                    int bottom = messageCell.getBottom();
                    if (top >= 0) {
                        viewTop = 0;
                    } else {
                        viewTop = -top;
                    }
                    int viewBottom = messageCell.getMeasuredHeight();
                    if (viewBottom > height) {
                        viewBottom = viewTop + height;
                    }
                    messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                    messageObject = messageCell.getMessageObject();
                    if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                        ImageReceiver imageReceiver = messageCell.getPhotoImage();
                        this.roundVideoContainer.setTranslationX(((float) imageReceiver.getImageX()) + messageCell.getTranslationX());
                        this.roundVideoContainer.setTranslationY((float) ((this.inPreviewMode ? AndroidUtilities.statusBarHeight : 0) + (((this.fragmentView.getPaddingTop() + top) + imageReceiver.getImageY()) - additionalTop)));
                        this.fragmentView.invalidate();
                        this.roundVideoContainer.invalidate();
                        foundTextureViewMessage = true;
                    }
                } else if (view instanceof ChatActionCell) {
                    messageObject = ((ChatActionCell) view).getMessageObject();
                }
                if (messageObject != null) {
                    if (!messageObject.isOut() && messageObject.isUnread()) {
                        id = messageObject.getId();
                        if (id > 0) {
                            maxPositiveUnreadId = Math.max(maxPositiveUnreadId, messageObject.getId());
                        }
                        if (id < 0) {
                            maxNegativeUnreadId = Math.min(maxNegativeUnreadId, messageObject.getId());
                        }
                        maxUnreadDate = Math.max(maxUnreadDate, messageObject.messageOwner.date);
                    }
                    if (messageObject.type == 17) {
                        this.pollsToCheck.add(messageObject);
                    }
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
                    if ((view instanceof ChatActionCell) && messageObject.isDateObject) {
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
            MessagesController.getInstance(this.currentAccount).addToPollsQueue(this.dialog_id, this.pollsToCheck);
            if (this.roundVideoContainer != null) {
                if (foundTextureViewMessage) {
                    MediaController.getInstance().setCurrentRoundVisible(true);
                } else {
                    this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                    this.fragmentView.invalidate();
                    messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId == 0 && this.checkTextureViewPosition) {
                        MediaController.getInstance().setCurrentRoundVisible(false);
                    }
                }
            }
            if (minMessageChild != null) {
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
                } else {
                    this.floatingDateView.setTranslationY((float) (((-this.floatingDateView.getMeasuredHeight()) * 2) + offset));
                }
            } else {
                hideFloatingDateView(true);
                this.floatingDateView.setTranslationY(0.0f);
            }
            if (!this.firstLoading && !this.paused && !this.inPreviewMode) {
                if (maxPositiveUnreadId != Integer.MIN_VALUE || maxNegativeUnreadId != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    int counterDicrement = 0;
                    for (a = 0; a < this.messages.size(); a++) {
                        messageObject = (MessageObject) this.messages.get(a);
                        id = messageObject.getId();
                        if (maxPositiveUnreadId != Integer.MIN_VALUE && id > 0 && id <= maxPositiveUnreadId && messageObject.isUnread()) {
                            messageObject.setIsRead();
                            counterDicrement++;
                        }
                        if (maxNegativeUnreadId != ConnectionsManager.DEFAULT_DATACENTER_ID && id < 0 && id >= maxNegativeUnreadId && messageObject.isUnread()) {
                            messageObject.setIsRead();
                            counterDicrement++;
                        }
                    }
                    if ((this.forwardEndReached[0] && maxPositiveUnreadId == this.minMessageId[0]) || maxNegativeUnreadId == this.minMessageId[0]) {
                        this.newUnreadMessageCount = 0;
                    } else {
                        this.newUnreadMessageCount -= counterDicrement;
                        if (this.newUnreadMessageCount < 0) {
                            this.newUnreadMessageCount = 0;
                        }
                    }
                    if (inLayout) {
                        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$36(this));
                    } else {
                        inlineUpdate1();
                    }
                    MessagesController instance = MessagesController.getInstance(this.currentAccount);
                    long j = this.dialog_id;
                    boolean z2 = maxPositiveUnreadId == this.minMessageId[0] || maxNegativeUnreadId == this.minMessageId[0];
                    instance.markDialogAsRead(j, maxPositiveUnreadId, maxNegativeUnreadId, maxUnreadDate, false, counterDicrement, z2);
                    this.firstUnreadSent = true;
                } else if (!this.firstUnreadSent && this.chatLayoutManager.findFirstVisibleItemPosition() == 0) {
                    this.newUnreadMessageCount = 0;
                    if (inLayout) {
                        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$37(this));
                    } else {
                        inlineUpdate2();
                    }
                    MessagesController.getInstance(this.currentAccount).markDialogAsRead(this.dialog_id, this.minMessageId[0], this.minMessageId[0], this.maxDate[0], false, 0, true);
                    this.firstUnreadSent = true;
                }
            }
        }
    }

    private void inlineUpdate1() {
        if (this.prevSetUnreadCount != this.newUnreadMessageCount) {
            this.prevSetUnreadCount = this.newUnreadMessageCount;
            this.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newUnreadMessageCount)}));
        }
        if (this.newUnreadMessageCount <= 0) {
            if (this.pagedownButtonCounter.getVisibility() != 4) {
                this.pagedownButtonCounter.setVisibility(4);
            }
        } else if (this.pagedownButtonCounter.getVisibility() != 0) {
            this.pagedownButtonCounter.setVisibility(0);
        }
    }

    private void inlineUpdate2() {
        if (this.prevSetUnreadCount != this.newUnreadMessageCount) {
            this.prevSetUnreadCount = this.newUnreadMessageCount;
            this.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newUnreadMessageCount)}));
        }
        if (this.pagedownButtonCounter.getVisibility() != 4) {
            this.pagedownButtonCounter.setVisibility(4);
        }
    }

    private void toggleMute(boolean instant) {
        Editor editor;
        TL_dialog dialog;
        if (MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id)) {
            editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            editor.putInt("notify2_" + this.dialog_id, 0);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(this.dialog_id, 0);
            editor.commit();
            dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
            }
            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.dialog_id);
        } else if (instant) {
            editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            editor.putInt("notify2_" + this.dialog_id, 2);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(this.dialog_id, 1);
            editor.commit();
            dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
                dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.dialog_id);
            NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(this.dialog_id);
        } else {
            showDialog(AlertsCreator.createMuteAlert(getParentActivity(), this.dialog_id));
        }
    }

    private int getScrollOffsetForMessage(MessageObject object) {
        int offset = ConnectionsManager.DEFAULT_DATACENTER_ID;
        GroupedMessages groupedMessages = getValidGroupedMessage(object);
        if (groupedMessages != null) {
            float itemHeight;
            GroupedMessagePosition currentPosition = (GroupedMessagePosition) groupedMessages.positions.get(object);
            float maxH = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
            if (currentPosition.siblingHeights != null) {
                itemHeight = currentPosition.siblingHeights[0];
            } else {
                itemHeight = currentPosition.var_ph;
            }
            float totalHeight = 0.0f;
            float moveDiff = 0.0f;
            SparseBooleanArray array = new SparseBooleanArray();
            for (int a = 0; a < groupedMessages.posArray.size(); a++) {
                GroupedMessagePosition pos = (GroupedMessagePosition) groupedMessages.posArray.get(a);
                if (array.indexOfKey(pos.minY) < 0 && pos.siblingHeights == null) {
                    array.put(pos.minY, true);
                    if (pos.minY < currentPosition.minY) {
                        moveDiff -= pos.var_ph;
                    } else if (pos.minY > currentPosition.minY) {
                        moveDiff += pos.var_ph;
                    }
                    totalHeight += pos.var_ph;
                }
            }
            if (Math.abs(totalHeight - itemHeight) < 0.02f) {
                offset = ((((int) (((float) this.chatListView.getMeasuredHeight()) - (totalHeight * maxH))) / 2) - this.chatListView.getPaddingTop()) - AndroidUtilities.m9dp(7.0f);
            } else {
                offset = ((((int) (((float) this.chatListView.getMeasuredHeight()) - ((itemHeight + moveDiff) * maxH))) / 2) - this.chatListView.getPaddingTop()) - AndroidUtilities.m9dp(7.0f);
            }
        }
        if (offset == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            offset = (this.chatListView.getMeasuredHeight() - object.getApproximateHeight()) / 2;
        }
        return Math.max(0, offset);
    }

    public void scrollToMessageId(int id, int fromMessageId, boolean select, int loadIndex, boolean smooth) {
        this.wasManualScroll = true;
        MessageObject object = (MessageObject) this.messagesDict[loadIndex].get(id);
        boolean query = false;
        if (object == null) {
            query = true;
        } else if (this.messages.indexOf(object) != -1) {
            if (select) {
                this.highlightMessageId = id;
            } else {
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            int yOffset = getScrollOffsetForMessage(object);
            if (smooth) {
                if (this.messages.get(this.messages.size() - 1) == object) {
                    this.chatListView.smoothScrollToPosition(this.chatAdapter.getItemCount() - 1);
                } else {
                    this.chatListView.smoothScrollToPosition(this.chatAdapter.messagesStartRow + this.messages.indexOf(object));
                }
            } else if (this.messages.get(this.messages.size() - 1) == object) {
                this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.getItemCount() - 1, yOffset, false);
            } else {
                this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.messagesStartRow + this.messages.indexOf(object), yOffset, false);
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
            if (this.currentEncryptedChat == null || MessagesStorage.getInstance(this.currentAccount).checkMessageId(this.dialog_id, this.startLoadFromMessageId)) {
                this.waitingForLoad.clear();
                this.waitingForReplyMessageLoad = true;
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.scrollToMessagePosition = -10000;
                this.startLoadFromMessageId = id;
                if (id == this.createUnreadMessageAfterId) {
                    this.createUnreadMessageAfterIdLoading = true;
                }
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController instance = MessagesController.getInstance(this.currentAccount);
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
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (show) {
                this.pagedownButtonShowedByScroll = false;
                if (this.pagedownButton.getTag() == null) {
                    if (this.pagedownButtonAnimation != null) {
                        this.pagedownButtonAnimation.cancel();
                        this.pagedownButtonAnimation = null;
                    }
                    if (animated) {
                        if (this.pagedownButton.getTranslationY() == 0.0f) {
                            this.pagedownButton.setTranslationY((float) AndroidUtilities.m9dp(100.0f));
                        }
                        this.pagedownButton.setVisibility(0);
                        this.pagedownButton.setTag(Integer.valueOf(1));
                        this.pagedownButtonAnimation = new AnimatorSet();
                        if (this.mentiondownButton.getVisibility() == 0) {
                            animatorSet = this.pagedownButtonAnimation;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{(float) (-AndroidUtilities.m9dp(72.0f))});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorSet = this.pagedownButtonAnimation;
                            animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                        this.pagedownButtonAnimation.setDuration(200);
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
                    this.pagedownButtonAnimation = new AnimatorSet();
                    if (this.mentiondownButton.getVisibility() == 0) {
                        animatorSet = this.pagedownButtonAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{(float) AndroidUtilities.m9dp(100.0f)});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        animatorSet = this.pagedownButtonAnimation;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{(float) AndroidUtilities.m9dp(100.0f)});
                        animatorSet.playTogether(animatorArr);
                    }
                    this.pagedownButtonAnimation.setDuration(200);
                    this.pagedownButtonAnimation.addListener(new CLASSNAME());
                    this.pagedownButtonAnimation.start();
                    return;
                }
                this.pagedownButton.setVisibility(4);
            }
        }
    }

    private void showMentiondownButton(boolean show, boolean animated) {
        if (this.mentiondownButton != null) {
            if (!show) {
                this.returnToMessageId = 0;
                if (this.mentiondownButton.getTag() != null) {
                    this.mentiondownButton.setTag(null);
                    if (this.mentiondownButtonAnimation != null) {
                        this.mentiondownButtonAnimation.cancel();
                        this.mentiondownButtonAnimation = null;
                    }
                    if (animated) {
                        if (this.pagedownButton.getVisibility() == 0) {
                            this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "alpha", new float[]{1.0f, 0.0f}).setDuration(200);
                        } else {
                            this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{(float) AndroidUtilities.m9dp(100.0f)}).setDuration(200);
                        }
                        this.mentiondownButtonAnimation.addListener(new CLASSNAME());
                        this.mentiondownButtonAnimation.start();
                        return;
                    }
                    this.mentiondownButton.setVisibility(4);
                }
            } else if (this.mentiondownButton.getTag() == null) {
                if (this.mentiondownButtonAnimation != null) {
                    this.mentiondownButtonAnimation.cancel();
                    this.mentiondownButtonAnimation = null;
                }
                if (animated) {
                    this.mentiondownButton.setVisibility(0);
                    this.mentiondownButton.setTag(Integer.valueOf(1));
                    if (this.pagedownButton.getVisibility() == 0) {
                        this.mentiondownButton.setTranslationY((float) (-AndroidUtilities.m9dp(72.0f)));
                        this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "alpha", new float[]{0.0f, 1.0f}).setDuration(200);
                    } else {
                        if (this.mentiondownButton.getTranslationY() == 0.0f) {
                            this.mentiondownButton.setTranslationY((float) AndroidUtilities.m9dp(100.0f));
                        }
                        this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{0.0f}).setDuration(200);
                    }
                    this.mentiondownButtonAnimation.start();
                    return;
                }
                this.mentiondownButton.setVisibility(0);
            }
        }
    }

    private void updateSecretStatus() {
        if (this.bottomOverlay != null) {
            boolean hideKeyboard = false;
            if (ChatObject.isChannel(this.currentChat) && this.currentChat.banned_rights != null && this.currentChat.banned_rights.send_messages) {
                if (AndroidUtilities.isBannedForever(this.currentChat.banned_rights.until_date)) {
                    this.bottomOverlayText.setText(LocaleController.getString("SendMessageRestrictedForever", R.string.SendMessageRestrictedForever));
                } else {
                    this.bottomOverlayText.setText(LocaleController.formatString("SendMessageRestricted", R.string.SendMessageRestricted, LocaleController.formatDateForBan((long) this.currentChat.banned_rights.until_date)));
                }
                this.bottomOverlay.setVisibility(0);
                if (this.mentionListAnimation != null) {
                    this.mentionListAnimation.cancel();
                    this.mentionListAnimation = null;
                }
                this.mentionContainer.setVisibility(8);
                this.mentionContainer.setTag(null);
                hideKeyboard = true;
            } else if (this.currentEncryptedChat == null || this.bigEmptyView == null) {
                this.bottomOverlay.setVisibility(4);
                return;
            } else {
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
                    this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, false);
                    hideKeyboard = true;
                } else if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                    this.bottomOverlay.setVisibility(4);
                }
                checkRaiseSensors();
                checkActionBarMenu();
            }
            if (hideKeyboard) {
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
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
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("PermissionNoAudioVideo", R.string.PermissionNoAudioVideo));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new ChatActivity$$Lambda$38(this));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.show();
            }
        } else if (requestCode == 19 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
            processSelectedAttach(0);
        } else if (requestCode == 20 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
            processSelectedAttach(2);
        } else if (requestCode == 101 && this.currentUser != null) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                VoIPHelper.permissionDenied(getParentActivity(), null);
            } else {
                VoIPHelper.startCall(this.currentUser, getParentActivity(), MessagesController.getInstance(this.currentAccount).getUserFull(this.currentUser.var_id));
            }
        }
    }

    final /* synthetic */ void lambda$onRequestPermissionsResultFragment$49$ChatActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Throwable e) {
            FileLog.m13e(e);
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
            if (messageObject.isEditing()) {
                return -1;
            }
            if ((this.isBroadcast || messageObject.getId() > 0 || !messageObject.isOut()) && !isBroadcastError) {
                if (messageObject.type == 6) {
                    return -1;
                }
                if (messageObject.type == 10 || messageObject.type == 11 || messageObject.type == 16) {
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
                            if (!DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.var_id)) {
                                return 7;
                            }
                        } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                            return 7;
                        }
                        return 9;
                    }
                    if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                        canSave = false;
                        if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath) && new File(messageObject.messageOwner.attachPath).exists()) {
                            canSave = true;
                        }
                        if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                            canSave = true;
                        }
                        if (canSave) {
                            if (messageObject.getDocument() != null) {
                                mime = messageObject.getDocument().mime_type;
                                if (mime != null) {
                                    if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
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
                    if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                        return 7;
                    }
                } else if (!messageObject.isRoundVideo() && ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                    canSave = false;
                    if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath) && new File(messageObject.messageOwner.attachPath).exists()) {
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

    private void addToSelectedMessages(MessageObject messageObject, boolean outside) {
        addToSelectedMessages(messageObject, outside, true);
    }

    private void addToSelectedMessages(MessageObject messageObject, boolean outside, boolean last) {
        int a;
        if (messageObject != null) {
            int index = messageObject.getDialogId() == this.dialog_id ? 0 : 1;
            GroupedMessages groupedMessages;
            if (outside && messageObject.getGroupId() != 0) {
                boolean hasUnselected = false;
                groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(messageObject.getGroupId());
                if (groupedMessages != null) {
                    int lastNum = 0;
                    for (a = 0; a < groupedMessages.messages.size(); a++) {
                        if (this.selectedMessagesIds[index].indexOfKey(((MessageObject) groupedMessages.messages.get(a)).getId()) < 0) {
                            hasUnselected = true;
                            lastNum = a;
                        }
                    }
                    a = 0;
                    while (a < groupedMessages.messages.size()) {
                        MessageObject message = (MessageObject) groupedMessages.messages.get(a);
                        if (!hasUnselected) {
                            addToSelectedMessages(message, false, a == groupedMessages.messages.size() + -1);
                        } else if (this.selectedMessagesIds[index].indexOfKey(message.getId()) < 0) {
                            addToSelectedMessages(message, false, a == lastNum);
                        }
                        a++;
                    }
                    return;
                }
                return;
            } else if (this.selectedMessagesIds[index].indexOfKey(messageObject.getId()) >= 0) {
                this.selectedMessagesIds[index].remove(messageObject.getId());
                if (messageObject.type == 0 || messageObject.caption != null) {
                    this.selectedMessagesCanCopyIds[index].remove(messageObject.getId());
                }
                if (messageObject.isSticker()) {
                    this.selectedMessagesCanStarIds[index].remove(messageObject.getId());
                }
                if (messageObject.canEditMessage(this.currentChat) && messageObject.getGroupId() != 0) {
                    groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(messageObject.getGroupId());
                    if (groupedMessages != null && groupedMessages.messages.size() > 1) {
                        this.canEditMessagesCount--;
                    }
                }
                if (!messageObject.canDeleteMessage(this.currentChat)) {
                    this.cantDeleteMessagesCount--;
                }
            } else if (this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() < 100) {
                this.selectedMessagesIds[index].put(messageObject.getId(), messageObject);
                if (messageObject.type == 0 || messageObject.caption != null) {
                    this.selectedMessagesCanCopyIds[index].put(messageObject.getId(), messageObject);
                }
                if (messageObject.isSticker()) {
                    this.selectedMessagesCanStarIds[index].put(messageObject.getId(), messageObject);
                }
                if (messageObject.canEditMessage(this.currentChat) && messageObject.getGroupId() != 0) {
                    groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(messageObject.getGroupId());
                    if (groupedMessages != null && groupedMessages.messages.size() > 1) {
                        this.canEditMessagesCount++;
                    }
                }
                if (!messageObject.canDeleteMessage(this.currentChat)) {
                    this.cantDeleteMessagesCount++;
                }
            } else {
                return;
            }
        }
        if (last && this.actionBar.isActionModeShowed()) {
            int selectedCount = this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size();
            if (selectedCount == 0) {
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                this.startReplyOnTextChange = false;
                return;
            }
            ActionBarMenuItem copyItem = this.actionBar.createActionMode().getItem(10);
            ActionBarMenuItem starItem = this.actionBar.createActionMode().getItem(22);
            ActionBarMenuItem editItem = this.actionBar.createActionMode().getItem(23);
            ActionBarMenuItem replyItem = this.actionBar.createActionMode().getItem(19);
            int copyVisible = copyItem.getVisibility();
            int starVisible = starItem.getVisibility();
            copyItem.setVisibility(this.selectedMessagesCanCopyIds[0].size() + this.selectedMessagesCanCopyIds[1].size() != 0 ? 0 : 8);
            int i = (DataQuery.getInstance(this.currentAccount).canAddStickerToFavorites() && this.selectedMessagesCanStarIds[0].size() + this.selectedMessagesCanStarIds[1].size() == selectedCount) ? 0 : 8;
            starItem.setVisibility(i);
            int newCopyVisible = copyItem.getVisibility();
            int newStarVisible = starItem.getVisibility();
            this.actionBar.createActionMode().getItem(12).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
            if (editItem != null) {
                i = (this.canEditMessagesCount == 1 && this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() == 1) ? 0 : 8;
                editItem.setVisibility(i);
            }
            this.hasUnfavedSelected = false;
            for (a = 0; a < 2; a++) {
                for (int b = 0; b < this.selectedMessagesCanStarIds[a].size(); b++) {
                    if (!DataQuery.getInstance(this.currentAccount).isStickerInFavorites(((MessageObject) this.selectedMessagesCanStarIds[a].valueAt(b)).getDocument())) {
                        this.hasUnfavedSelected = true;
                        break;
                    }
                }
                if (this.hasUnfavedSelected) {
                    break;
                }
            }
            starItem.setIcon(this.hasUnfavedSelected ? R.drawable.ic_ab_fave : R.drawable.ic_ab_unfave);
            if (replyItem != null) {
                boolean allowChatActions = true;
                if ((this.currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46) || this.isBroadcast || ((this.bottomOverlayChat != null && this.bottomOverlayChat.getVisibility() == 0) || (this.currentChat != null && (ChatObject.isNotInChat(this.currentChat) || !((!ChatObject.isChannel(this.currentChat) || ChatObject.canPost(this.currentChat) || this.currentChat.megagroup) && ChatObject.canSendMessages(this.currentChat)))))) {
                    allowChatActions = false;
                }
                int newVisibility = (allowChatActions && this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() == 1) ? 0 : 8;
                boolean z = newVisibility == 0 && !this.chatActivityEnterView.hasText();
                this.startReplyOnTextChange = z;
                if (replyItem.getVisibility() != newVisibility) {
                    if (this.replyButtonAnimation != null) {
                        this.replyButtonAnimation.cancel();
                    }
                    if (copyVisible == newCopyVisible && starVisible == newStarVisible) {
                        this.replyButtonAnimation = new AnimatorSet();
                        replyItem.setPivotX((float) AndroidUtilities.m9dp(54.0f));
                        editItem.setPivotX((float) AndroidUtilities.m9dp(54.0f));
                        AnimatorSet animatorSet;
                        Animator[] animatorArr;
                        if (newVisibility == 0) {
                            replyItem.setVisibility(newVisibility);
                            animatorSet = this.replyButtonAnimation;
                            animatorArr = new Animator[4];
                            animatorArr[0] = ObjectAnimator.ofFloat(replyItem, "alpha", new float[]{1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(replyItem, "scaleX", new float[]{1.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(editItem, "alpha", new float[]{1.0f});
                            animatorArr[3] = ObjectAnimator.ofFloat(editItem, "scaleX", new float[]{1.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorSet = this.replyButtonAnimation;
                            animatorArr = new Animator[4];
                            animatorArr[0] = ObjectAnimator.ofFloat(replyItem, "alpha", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(replyItem, "scaleX", new float[]{0.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(editItem, "alpha", new float[]{0.0f});
                            animatorArr[3] = ObjectAnimator.ofFloat(editItem, "scaleX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                        this.replyButtonAnimation.setDuration(100);
                        final int i2 = newVisibility;
                        final ActionBarMenuItem actionBarMenuItem = replyItem;
                        this.replyButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animation) && i2 == 8) {
                                    actionBarMenuItem.setVisibility(8);
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animation)) {
                                    ChatActivity.this.replyButtonAnimation = null;
                                }
                            }
                        });
                        this.replyButtonAnimation.start();
                        return;
                    }
                    if (newVisibility == 0) {
                        replyItem.setAlpha(1.0f);
                        replyItem.setScaleX(1.0f);
                    } else {
                        replyItem.setAlpha(0.0f);
                        replyItem.setScaleX(0.0f);
                    }
                    replyItem.setVisibility(newVisibility);
                }
            }
        }
    }

    private void processRowSelect(View view, boolean outside) {
        MessageObject message = null;
        if (view instanceof ChatMessageCell) {
            message = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            message = ((ChatActionCell) view).getMessageObject();
        }
        int type = getMessageType(message);
        if (type >= 2 && type != 20) {
            addToSelectedMessages(message, outside);
            updateActionModeTitle();
            updateVisibleRows();
        }
    }

    private void updateActionModeTitle() {
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (this.selectedMessagesIds[0].size() != 0 || this.selectedMessagesIds[1].size() != 0) {
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
                    this.avatarContainer.setTitle(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                } else if (MessagesController.isSupportId(this.currentUser.var_id) || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.var_id)) != null || (ContactsController.getInstance(this.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentAccount).isLoadingContacts())) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else if (TextUtils.isEmpty(this.currentUser.phone)) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else {
                    this.avatarContainer.setTitle(CLASSNAMEPhoneFormat.getInstance().format("+" + this.currentUser.phone));
                }
            }
        }
    }

    private void updateBotButtons() {
        if (this.headerItem != null && this.currentUser != null && this.currentEncryptedChat == null && this.currentUser.bot) {
            boolean hasHelp = false;
            boolean hasSettings = false;
            if (this.botInfo.size() != 0) {
                for (int b = 0; b < this.botInfo.size(); b++) {
                    BotInfo info = (BotInfo) this.botInfo.valueAt(b);
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
            if (MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id)) {
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
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.var_id));
            if (user != null) {
                this.currentUser = user;
            } else {
                return;
            }
        } else if (this.currentChat != null) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChat.var_id));
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

    public void openVideoEditor(String videoPath, String caption) {
        if (getParentActivity() != null) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            final ArrayList<Object> cameraPhoto = new ArrayList();
            PhotoEntry entry = new PhotoEntry(0, 0, 0, videoPath, 0, true);
            entry.caption = caption;
            cameraPhoto.add(entry);
            final Bitmap bitmap = thumb;
            PhotoViewer.getInstance().openPhotoForSelect(cameraPhoto, 0, 2, new EmptyPhotoViewerProvider() {
                public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
                    return new BitmapHolder(bitmap, null);
                }

                public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
                    ChatActivity.this.sendMedia((PhotoEntry) cameraPhoto.get(0), videoEditedInfo);
                }

                public boolean canScrollAway() {
                    return false;
                }
            }, this);
            return;
        }
        fillEditingMediaWithCaption(caption, null);
        SendMessagesHelper.prepareSendingVideo(videoPath, 0, 0, 0, 0, null, this.dialog_id, this.replyingMessageObject, null, null, 0, this.editingMessageObject);
        hideFieldPanel();
        DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0).show();
        }
    }

    private void fillEditingMediaWithCaption(CharSequence caption, ArrayList<MessageEntity> entities) {
        if (this.editingMessageObject != null) {
            if (!TextUtils.isEmpty(caption)) {
                this.editingMessageObject.editingMessage = caption;
                this.editingMessageObject.editingMessageEntities = entities;
            } else if (this.chatActivityEnterView != null) {
                this.editingMessageObject.editingMessage = this.chatActivityEnterView.getFieldText();
                if (this.editingMessageObject.editingMessage == null && !TextUtils.isEmpty(this.editingMessageObject.messageOwner.message)) {
                    this.editingMessageObject.editingMessage = TtmlNode.ANONYMOUS_REGION_ID;
                }
            }
        }
    }

    private void sendUriAsDocument(Uri uri) {
        if (uri != null) {
            String extractUriFrom = uri.toString();
            if (extractUriFrom.contains("com.google.android.apps.photos.contentprovider")) {
                try {
                    String firstExtraction = extractUriFrom.split("/1/")[1];
                    int index = firstExtraction.indexOf("/ACTUAL");
                    if (index != -1) {
                        uri = Uri.parse(URLDecoder.decode(firstExtraction.substring(0, index), CLASSNAMEC.UTF8_NAME));
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            String tempPath = AndroidUtilities.getPath(uri);
            String originalPath = tempPath;
            if (tempPath == null) {
                originalPath = uri.toString();
                tempPath = MediaController.copyFileToCache(uri, "file");
            }
            if (tempPath == null) {
                showAttachmentError();
                return;
            }
            fillEditingMediaWithCaption(null, null);
            SendMessagesHelper.prepareSendingDocument(tempPath, originalPath, null, null, null, this.dialog_id, this.replyingMessageObject, null, this.editingMessageObject);
            hideFieldPanel();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 0 || requestCode == 2) {
            createChatAttachView();
            if (this.chatAttachAlert != null) {
                this.chatAttachAlert.onActivityResultFragment(requestCode, data, this.currentPicturePath);
            }
            this.currentPicturePath = null;
        } else if (requestCode == 1) {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            Uri uri = data.getData();
            if (uri.toString().contains(MimeTypes.BASE_TYPE_VIDEO)) {
                String videoPath = null;
                try {
                    videoPath = AndroidUtilities.getPath(uri);
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                if (videoPath == null) {
                    showAttachmentError();
                }
                if (this.paused) {
                    this.startVideoEdit = videoPath;
                } else {
                    openVideoEditor(videoPath, null);
                }
            } else {
                fillEditingMediaWithCaption(null, null);
                SendMessagesHelper.prepareSendingPhoto(null, uri, this.dialog_id, this.replyingMessageObject, null, null, null, null, 0, this.editingMessageObject);
            }
            hideFieldPanel();
            DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
        } else if (requestCode != 21) {
        } else {
            if (data == null) {
                showAttachmentError();
                return;
            }
            if (data.getData() != null) {
                sendUriAsDocument(data.getData());
            } else if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    sendUriAsDocument(clipData.getItemAt(i).getUri());
                }
            } else {
                showAttachmentError();
            }
            hideFieldPanel();
            DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
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

    private void removeUnreadPlane(boolean scrollToEnd) {
        if (this.unreadMessageObject != null) {
            if (scrollToEnd) {
                boolean[] zArr = this.forwardEndReached;
                this.forwardEndReached[1] = true;
                zArr[0] = true;
                this.first_unread_id = 0;
                this.last_message_id = 0;
            }
            this.createUnreadMessageAfterId = 0;
            this.createUnreadMessageAfterIdLoading = false;
            removeMessageObject(this.unreadMessageObject);
            this.unreadMessageObject = null;
        }
    }

    public boolean processSendingText(String text) {
        return this.chatActivityEnterView.processSendingText(text);
    }

    /* JADX WARNING: Missing block: B:265:0x07d8, code:
            if (r116.indexOfKey(r122.getGroupId()) < 0) goto L_0x07da;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int id, int account, Object... args) {
        int index;
        int currentUserId;
        ArrayList<MessageObject> messArr;
        int a;
        MessageObject obj;
        int loadIndex;
        int count;
        LongSparseArray<GroupedMessages> newGroups;
        MessageObject player;
        ArrayList<MessageObject> dayArray;
        Message dateMsg;
        MessageObject messageObject;
        GroupedMessages groupedMessages;
        int idx;
        Chat chat;
        User user;
        Builder builder;
        MessageObject messageObject2;
        int b;
        int size;
        ArrayList<MessageObject> arrayList;
        View child;
        boolean updated;
        int key;
        int messageId;
        ArrayList<MessageObject> dayArr;
        int[] iArr;
        MessagesController instance;
        long j;
        boolean z;
        int i;
        int i2;
        boolean isChannel;
        int i3;
        int channelId;
        MessageObject removed;
        boolean z2;
        long mid;
        View view;
        ChatMessageCell cell;
        ContextLinkCell cell2;
        Message message;
        GroupedMessages slicedGroup;
        long did;
        MessageObject currentMessage;
        if (id == NotificationCenter.messagesDidLoad) {
            if (((Integer) args[10]).intValue() == this.classGuid) {
                setItemAnimationsEnabled(false);
                if (!this.openAnimationEnded) {
                    NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.botKeyboardDidLoad, NotificationCenter.userInfoDidLoad});
                }
                index = this.waitingForLoad.indexOf(Integer.valueOf(((Integer) args[11]).intValue()));
                currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                if (index != -1) {
                    this.waitingForLoad.remove(index);
                    messArr = args[2];
                    boolean createUnreadLoading = false;
                    if (this.waitingForReplyMessageLoad) {
                        if (!this.createUnreadMessageAfterIdLoading) {
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
                            if (!found) {
                                this.startLoadFromMessageId = 0;
                                return;
                            }
                        }
                        int startLoadFrom = this.startLoadFromMessageId;
                        boolean needSelect = this.needSelectFromMessageId;
                        int unreadAfterId = this.createUnreadMessageAfterId;
                        createUnreadLoading = this.createUnreadMessageAfterIdLoading;
                        clearChatData();
                        this.createUnreadMessageAfterId = unreadAfterId;
                        this.startLoadFromMessageId = startLoadFrom;
                        this.needSelectFromMessageId = needSelect;
                    }
                    this.loadsCount++;
                    loadIndex = ((Long) args[0]).longValue() == this.dialog_id ? 0 : 1;
                    count = ((Integer) args[1]).intValue();
                    boolean isCache = ((Boolean) args[3]).booleanValue();
                    int fnid = ((Integer) args[4]).intValue();
                    int last_unread_date = ((Integer) args[7]).intValue();
                    int load_type = ((Integer) args[8]).intValue();
                    int loaded_max_id = ((Integer) args[12]).intValue();
                    int loaded_mentions_count = ((Integer) args[13]).intValue();
                    if (loaded_mentions_count < 0) {
                        loaded_mentions_count *= -1;
                        this.hasAllMentionsLocal = false;
                    } else if (this.first) {
                        this.hasAllMentionsLocal = true;
                    }
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
                    int unread_to_load = 0;
                    if (fnid != 0) {
                        this.last_message_id = ((Integer) args[5]).intValue();
                        if (load_type == 3) {
                            if (this.loadingFromOldPosition) {
                                unread_to_load = ((Integer) args[6]).intValue();
                                if (unread_to_load != 0) {
                                    this.createUnreadMessageAfterId = fnid;
                                }
                                this.loadingFromOldPosition = false;
                            }
                            this.first_unread_id = 0;
                        } else {
                            this.first_unread_id = fnid;
                            unread_to_load = ((Integer) args[6]).intValue();
                        }
                    } else if (this.startLoadFromMessageId != 0 && (load_type == 3 || load_type == 4)) {
                        this.last_message_id = ((Integer) args[5]).intValue();
                    }
                    int newRowsCount = 0;
                    if (!(load_type == 0 || (this.startLoadFromMessageId == 0 && this.last_message_id == 0))) {
                        this.forwardEndReached[loadIndex] = false;
                    }
                    if ((load_type == 1 || load_type == 3) && loadIndex == 1) {
                        boolean[] zArr = this.endReached;
                        this.cacheEndReached[0] = true;
                        zArr[0] = true;
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
                            this.groupedMessagesMap.clear();
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
                        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$39(this));
                    }
                    if (load_type == 1) {
                        Collections.reverse(messArr);
                    }
                    if (this.currentEncryptedChat == null) {
                        DataQuery.getInstance(this.currentAccount).loadReplyMessagesForMessages(messArr, this.dialog_id);
                    }
                    int approximateHeightSum = 0;
                    if (load_type == 2 && messArr.isEmpty() && !isCache) {
                        this.forwardEndReached[0] = true;
                    }
                    newGroups = null;
                    LongSparseArray<GroupedMessages> changedGroups = null;
                    MediaController mediaController = MediaController.getInstance();
                    a = 0;
                    while (a < messArr.size()) {
                        obj = (MessageObject) messArr.get(a);
                        approximateHeightSum += obj.getApproximateHeight();
                        if (this.currentUser != null) {
                            if (this.currentUser.self) {
                                obj.messageOwner.out = true;
                            }
                            if ((this.currentUser.bot && obj.isOut()) || this.currentUser.var_id == currentUserId) {
                                obj.setIsRead();
                            }
                        }
                        if (this.messagesDict[loadIndex].indexOfKey(obj.getId()) < 0) {
                            addToPolls(obj, null);
                            if (isSecretChat()) {
                                checkSecretMessageForLocation(obj);
                            }
                            if (mediaController.isPlayingMessage(obj)) {
                                player = mediaController.getPlayingMessageObject();
                                obj.audioProgress = player.audioProgress;
                                obj.audioProgressSec = player.audioProgressSec;
                                obj.audioPlayerDuration = player.audioPlayerDuration;
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
                            if (obj.getId() == this.last_message_id) {
                                this.forwardEndReached[loadIndex] = true;
                            }
                            if (obj.type >= 0 && !(loadIndex == 1 && (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo))) {
                                if (this.needAnimateToMessage != null && this.needAnimateToMessage.getId() == obj.getId() && obj.getId() < 0 && obj.type == 5) {
                                    obj = this.needAnimateToMessage;
                                    this.animatingMessageObjects.add(obj);
                                    this.needAnimateToMessage = null;
                                }
                                this.messagesDict[loadIndex].put(obj.getId(), obj);
                                dayArray = (ArrayList) this.messagesByDays.get(obj.dateKey);
                                if (dayArray == null) {
                                    dayArray = new ArrayList();
                                    this.messagesByDays.put(obj.dateKey, dayArray);
                                    dateMsg = new TL_message();
                                    dateMsg.message = LocaleController.formatDateChat((long) obj.messageOwner.date);
                                    dateMsg.var_id = 0;
                                    dateMsg.date = obj.messageOwner.date;
                                    messageObject = new MessageObject(this.currentAccount, dateMsg, false);
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
                                if (obj.hasValidGroupId()) {
                                    groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(obj.getGroupIdForUse());
                                    if (groupedMessages != null && this.messages.size() > 1) {
                                        MessageObject previous;
                                        if (load_type == 1) {
                                            previous = (MessageObject) this.messages.get(0);
                                        } else {
                                            previous = (MessageObject) this.messages.get(this.messages.size() - 2);
                                        }
                                        if (previous.getGroupIdForUse() == obj.getGroupIdForUse()) {
                                            if (previous.localGroupId != 0) {
                                                obj.localGroupId = previous.localGroupId;
                                                groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(previous.localGroupId);
                                            }
                                        } else if (previous.getGroupIdForUse() != obj.getGroupIdForUse()) {
                                            obj.localGroupId = Utilities.random.nextLong();
                                            groupedMessages = null;
                                        }
                                    }
                                    if (groupedMessages == null) {
                                        groupedMessages = new GroupedMessages();
                                        groupedMessages.groupId = obj.getGroupId();
                                        this.groupedMessagesMap.put(groupedMessages.groupId, groupedMessages);
                                    } else {
                                        if (newGroups != null) {
                                        }
                                        if (changedGroups == null) {
                                            changedGroups = new LongSparseArray();
                                        }
                                        changedGroups.put(obj.getGroupId(), groupedMessages);
                                    }
                                    if (newGroups == null) {
                                        newGroups = new LongSparseArray();
                                    }
                                    newGroups.put(groupedMessages.groupId, groupedMessages);
                                    if (load_type == 1) {
                                        groupedMessages.messages.add(obj);
                                    } else {
                                        groupedMessages.messages.add(0, obj);
                                    }
                                } else if (obj.getGroupIdForUse() != 0) {
                                    obj.messageOwner.grouped_id = 0;
                                    obj.localSentGroupId = 0;
                                }
                                newRowsCount++;
                                dayArray.add(obj);
                                if (load_type == 1) {
                                    this.messages.add(0, obj);
                                } else {
                                    this.messages.add(this.messages.size() - 1, obj);
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
                                        dateMsg = new TL_message();
                                        dateMsg.message = TtmlNode.ANONYMOUS_REGION_ID;
                                        dateMsg.var_id = 0;
                                        messageObject = new MessageObject(this.currentAccount, dateMsg, false);
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
                                if (load_type != 2 && this.unreadMessageObject == null && this.createUnreadMessageAfterId != 0 && (((this.currentEncryptedChat == null && !obj.isOut() && obj.getId() >= this.createUnreadMessageAfterId) || !(this.currentEncryptedChat == null || obj.isOut() || obj.getId() > this.createUnreadMessageAfterId)) && (load_type == 1 || prevObj != null || (prevObj == null && createUnreadLoading && a == messArr.size() - 1)))) {
                                    dateMsg = new TL_message();
                                    dateMsg.message = TtmlNode.ANONYMOUS_REGION_ID;
                                    dateMsg.var_id = 0;
                                    messageObject = new MessageObject(this.currentAccount, dateMsg, false);
                                    messageObject.type = 6;
                                    messageObject.contentType = 2;
                                    if (load_type == 1) {
                                        this.messages.add(1, messageObject);
                                    } else {
                                        this.messages.add(this.messages.size() - 1, messageObject);
                                    }
                                    this.unreadMessageObject = messageObject;
                                    if (load_type == 3) {
                                        this.scrollToMessage = this.unreadMessageObject;
                                        this.startLoadFromMessageId = 0;
                                        this.scrollToMessagePosition = -9000;
                                    }
                                    newRowsCount++;
                                }
                            }
                        }
                        a++;
                    }
                    if (createUnreadLoading) {
                        this.createUnreadMessageAfterId = 0;
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
                    View firstVisView;
                    int testingPosition;
                    View testingView;
                    View goodView;
                    int goodPosition;
                    int top;
                    if (load_type == 1) {
                        int rowsRemoved = 0;
                        if (!(messArr.size() == count || (isCache && this.currentEncryptedChat == null && !this.forwardEndReached[loadIndex]))) {
                            this.forwardEndReached[loadIndex] = true;
                            if (loadIndex != 1) {
                                this.first_unread_id = 0;
                                this.last_message_id = 0;
                                this.createUnreadMessageAfterId = 0;
                                this.chatAdapter.notifyItemRemoved(this.chatAdapter.loadingDownRow);
                                rowsRemoved = 0 + 1;
                            }
                            this.startLoadFromMessageId = 0;
                        }
                        if (newRowsCount > 0) {
                            firstVisPos = this.chatLayoutManager.findFirstVisibleItemPosition();
                            if (firstVisPos == 0) {
                                firstVisPos++;
                            }
                            firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
                            testingPosition = firstVisPos;
                            testingView = firstVisView;
                            goodView = null;
                            goodPosition = -1;
                            if (testingView != null) {
                                while (null == null) {
                                    if (testingView instanceof ChatMessageCell) {
                                        if (!((ChatMessageCell) testingView).getMessageObject().hasValidGroupId()) {
                                            goodPosition = testingPosition;
                                            goodView = testingView;
                                            break;
                                        }
                                        testingPosition++;
                                        testingView = this.chatLayoutManager.findViewByPosition(testingPosition);
                                        if (testingView == null) {
                                            goodPosition = firstVisPos;
                                            goodView = firstVisView;
                                            break;
                                        }
                                    }
                                    goodPosition = testingPosition;
                                    goodView = testingView;
                                    break;
                                }
                            }
                            top = goodView == null ? 0 : (this.chatListView.getMeasuredHeight() - goodView.getBottom()) - this.chatListView.getPaddingBottom();
                            this.chatAdapter.notifyItemRangeInserted(1, newRowsCount);
                            if (goodPosition != -1) {
                                this.chatLayoutManager.scrollToPositionWithOffset((goodPosition + newRowsCount) - rowsRemoved, top);
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
                                TextView textView;
                                Object[] objArr;
                                this.forceScrollToTop = false;
                                this.chatAdapter.notifyDataSetChanged();
                                if (this.scrollToMessage != null) {
                                    int yOffset;
                                    boolean bottom = true;
                                    if (this.startLoadFromMessageOffset != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        yOffset = (-this.startLoadFromMessageOffset) - this.chatListView.getPaddingBottom();
                                        this.startLoadFromMessageOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    } else if (this.scrollToMessagePosition == -9000) {
                                        yOffset = getScrollOffsetForMessage(this.scrollToMessage);
                                        bottom = false;
                                    } else if (this.scrollToMessagePosition == -10000) {
                                        yOffset = -AndroidUtilities.m9dp(11.0f);
                                        bottom = false;
                                    } else {
                                        yOffset = this.scrollToMessagePosition;
                                    }
                                    if (!this.messages.isEmpty()) {
                                        if (this.messages.get(this.messages.size() - 1) == this.scrollToMessage || this.messages.get(this.messages.size() - 2) == this.scrollToMessage) {
                                            this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.loadingUpRow, yOffset, bottom);
                                        } else {
                                            this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.messagesStartRow + this.messages.indexOf(this.scrollToMessage), yOffset, bottom);
                                        }
                                    }
                                    this.chatListView.invalidate();
                                    if (this.scrollToMessagePosition == -10000 || this.scrollToMessagePosition == -9000) {
                                        showPagedownButton(true, true);
                                        if (!(unread_to_load == 0 || this.pagedownButtonCounter == null)) {
                                            this.pagedownButtonCounter.setVisibility(0);
                                            if (this.prevSetUnreadCount != this.newUnreadMessageCount) {
                                                textView = this.pagedownButtonCounter;
                                                objArr = new Object[1];
                                                this.newUnreadMessageCount = unread_to_load;
                                                objArr[0] = Integer.valueOf(unread_to_load);
                                                textView.setText(String.format("%d", objArr));
                                                this.prevSetUnreadCount = this.newUnreadMessageCount;
                                            }
                                        }
                                    }
                                    this.scrollToMessagePosition = -10000;
                                    this.scrollToMessage = null;
                                } else {
                                    moveScrollToLastMessage();
                                }
                                if (loaded_mentions_count != 0) {
                                    showMentiondownButton(true, true);
                                    if (this.mentiondownButtonCounter != null) {
                                        this.mentiondownButtonCounter.setVisibility(0);
                                        textView = this.mentiondownButtonCounter;
                                        objArr = new Object[1];
                                        this.newMentionsCount = loaded_mentions_count;
                                        objArr[0] = Integer.valueOf(loaded_mentions_count);
                                        textView.setText(String.format("%d", objArr));
                                    }
                                }
                            } else if (newRowsCount != 0) {
                                boolean end = false;
                                if (this.endReached[loadIndex] && ((loadIndex == 0 && this.mergeDialogId == 0) || loadIndex == 1)) {
                                    end = true;
                                    this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.loadingUpRow - 1, 2);
                                    this.chatAdapter.updateRows();
                                }
                                firstVisPos = this.chatLayoutManager.findFirstVisibleItemPosition();
                                firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
                                testingPosition = firstVisPos;
                                testingView = firstVisView;
                                goodView = null;
                                goodPosition = -1;
                                if (testingView != null) {
                                    while (null == null) {
                                        if (testingView instanceof ChatMessageCell) {
                                            if (!((ChatMessageCell) testingView).getMessageObject().hasValidGroupId()) {
                                                goodPosition = testingPosition;
                                                goodView = testingView;
                                                break;
                                            }
                                            testingPosition++;
                                            testingView = this.chatLayoutManager.findViewByPosition(testingPosition);
                                            if (testingView == null) {
                                                goodPosition = firstVisPos;
                                                goodView = firstVisView;
                                                break;
                                            }
                                        }
                                        goodPosition = testingPosition;
                                        goodView = testingView;
                                        break;
                                    }
                                }
                                top = goodView == null ? 0 : (this.chatListView.getMeasuredHeight() - goodView.getBottom()) - this.chatListView.getPaddingBottom();
                                if (newRowsCount - (end ? 1 : 0) > 0) {
                                    int insertStart = this.chatAdapter.messagesEndRow;
                                    this.chatAdapter.notifyItemChanged(this.chatAdapter.loadingUpRow);
                                    this.chatAdapter.notifyItemRangeInserted(insertStart, newRowsCount - (end ? 1 : 0));
                                }
                                if (goodPosition != -1) {
                                    this.chatLayoutManager.scrollToPositionWithOffset(goodPosition, top);
                                }
                            } else if (this.endReached[loadIndex] && ((loadIndex == 0 && this.mergeDialogId == 0) || loadIndex == 1)) {
                                this.chatAdapter.notifyItemRemoved(this.chatAdapter.loadingUpRow);
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
                    if (newGroups != null) {
                        for (a = 0; a < newGroups.size(); a++) {
                            groupedMessages = (GroupedMessages) newGroups.valueAt(a);
                            groupedMessages.calculate();
                            if (!(this.chatAdapter == null || changedGroups == null)) {
                                if (changedGroups.indexOfKey(newGroups.keyAt(a)) >= 0) {
                                    idx = this.messages.indexOf((MessageObject) groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                                    if (idx >= 0) {
                                        this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.messagesStartRow + idx, groupedMessages.messages.size());
                                    }
                                }
                            }
                        }
                    }
                    if (this.first && this.messages.size() > 0) {
                        this.first = false;
                    }
                    if (this.messages.isEmpty() && this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                        this.botUser = TtmlNode.ANONYMOUS_REGION_ID;
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
                    setItemAnimationsEnabled(true);
                }
            }
        } else if (id == NotificationCenter.emojiDidLoad) {
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
        } else if (id == NotificationCenter.chatOnlineCountDidLoad) {
            Integer chatId = args[0];
            if (this.chatInfo != null && this.currentChat != null && this.currentChat.var_id == chatId.intValue()) {
                this.chatInfo.online_count = ((Integer) args[1]).intValue();
                if (this.avatarContainer != null) {
                    this.avatarContainer.updateOnlineCount();
                    this.avatarContainer.updateSubtitle();
                }
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int updateMask = ((Integer) args[0]).intValue();
            if (!((updateMask & 1) == 0 && (updateMask & 16) == 0)) {
                if (this.currentChat != null) {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChat.var_id));
                    if (chat != null) {
                        this.currentChat = chat;
                    }
                } else if (this.currentUser != null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.var_id));
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
            if ((updateMask & MessagesController.UPDATE_MASK_CHANNEL) != 0 && ChatObject.isChannel(this.currentChat)) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChat.var_id));
                if (chat != null) {
                    this.currentChat = chat;
                    updateSubtitle = true;
                    updateBottomOverlay();
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setDialogId(this.dialog_id, this.currentAccount);
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
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                boolean updateChat = false;
                boolean hasFromMe = false;
                ArrayList<MessageObject> arr = args[1];
                if (this.currentEncryptedChat != null && arr.size() == 1) {
                    obj = (MessageObject) arr.get(0);
                    if (this.currentEncryptedChat != null && obj.isOut() && obj.messageOwner.action != null && (obj.messageOwner.action instanceof TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) && getParentActivity() != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 17 && this.currentEncryptedChat.ttl > 0 && this.currentEncryptedChat.ttl <= 60) {
                        builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        builder.setMessage(LocaleController.formatString("CompatibilityChat", R.string.CompatibilityChat, this.currentUser.first_name, this.currentUser.first_name));
                        showDialog(builder.create());
                    }
                }
                if (!(this.currentChat == null && this.inlineReturn == 0)) {
                    boolean notifiedSearch = false;
                    for (a = 0; a < arr.size(); a++) {
                        messageObject2 = (MessageObject) arr.get(a);
                        if (!notifiedSearch && messageObject2.isOut()) {
                            notifiedSearch = true;
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
                        }
                        if (this.currentChat != null) {
                            if (((messageObject2.messageOwner.action instanceof TL_messageActionChatDeleteUser) && messageObject2.messageOwner.action.user_id == currentUserId) || ((messageObject2.messageOwner.action instanceof TL_messageActionChatAddUser) && messageObject2.messageOwner.action.users.contains(Integer.valueOf(currentUserId)))) {
                                Chat newChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChat.var_id));
                                if (newChat != null) {
                                    this.currentChat = newChat;
                                    checkActionBarMenu();
                                    updateBottomOverlay();
                                    if (this.avatarContainer != null) {
                                        this.avatarContainer.updateSubtitle();
                                    }
                                }
                            } else if (messageObject2.messageOwner.reply_to_msg_id != 0 && messageObject2.replyMessageObject == null) {
                                messageObject2.replyMessageObject = (MessageObject) this.messagesDict[0].get(messageObject2.messageOwner.reply_to_msg_id);
                                if (messageObject2.messageOwner.action instanceof TL_messageActionPinMessage) {
                                    messageObject2.generatePinMessageText(null, null);
                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                                    messageObject2.generateGameMessageText(null);
                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionPaymentSent) {
                                    messageObject2.generatePaymentSentMessageText(null);
                                }
                                if (!(!messageObject2.isMegagroup() || messageObject2.replyMessageObject == null || messageObject2.replyMessageObject.messageOwner == null)) {
                                    Message message2 = messageObject2.replyMessageObject.messageOwner;
                                    message2.flags |= Integer.MIN_VALUE;
                                }
                            }
                        } else if (!(this.inlineReturn == 0 || messageObject2.messageOwner.reply_markup == null)) {
                            for (b = 0; b < messageObject2.messageOwner.reply_markup.rows.size(); b++) {
                                TL_keyboardButtonRow row = (TL_keyboardButtonRow) messageObject2.messageOwner.reply_markup.rows.get(b);
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
                if (this.forwardEndReached[0]) {
                    newGroups = null;
                    HashMap<String, ArrayList<MessageObject>> webpagesToReload = null;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m10d("received new messages " + arr.size() + " in dialog " + this.dialog_id);
                    }
                    for (a = 0; a < arr.size(); a++) {
                        int placeToPaste = -1;
                        obj = (MessageObject) arr.get(a);
                        if (isSecretChat()) {
                            checkSecretMessageForLocation(obj);
                        }
                        if (this.currentUser != null && ((this.currentUser.bot && obj.isOut()) || this.currentUser.var_id == currentUserId)) {
                            obj.setIsRead();
                        }
                        if (!(this.avatarContainer == null || this.currentEncryptedChat == null || obj.messageOwner.action == null || !(obj.messageOwner.action instanceof TL_messageEncryptedAction) || !(obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            this.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (obj.type >= 0 && this.messagesDict[0].indexOfKey(obj.getId()) < 0) {
                            addToPolls(obj, null);
                            if (a == 0 && obj.messageOwner.var_id < 0 && obj.type == 5) {
                                this.animatingMessageObjects.add(obj);
                            }
                            if (obj.hasValidGroupId()) {
                                groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(obj.getGroupId());
                                if (groupedMessages == null) {
                                    groupedMessages = new GroupedMessages();
                                    groupedMessages.groupId = obj.getGroupId();
                                    this.groupedMessagesMap.put(groupedMessages.groupId, groupedMessages);
                                }
                                if (newGroups == null) {
                                    newGroups = new LongSparseArray();
                                }
                                newGroups.put(groupedMessages.groupId, groupedMessages);
                                groupedMessages.messages.add(obj);
                            } else {
                                groupedMessages = null;
                            }
                            if (groupedMessages != null) {
                                messageObject2 = groupedMessages.messages.size() > 1 ? (MessageObject) groupedMessages.messages.get(groupedMessages.messages.size() - 2) : null;
                                if (messageObject2 != null) {
                                    placeToPaste = this.messages.indexOf(messageObject2);
                                }
                            }
                            if (placeToPaste == -1) {
                                if (obj.messageOwner.var_id < 0 || this.messages.isEmpty()) {
                                    placeToPaste = 0;
                                } else {
                                    size = this.messages.size();
                                    b = 0;
                                    while (b < size) {
                                        MessageObject lastMessage = (MessageObject) this.messages.get(b);
                                        if (lastMessage.type < 0 || lastMessage.messageOwner.date <= 0 || ((lastMessage.messageOwner.var_id <= 0 || obj.messageOwner.var_id <= 0 || lastMessage.messageOwner.var_id >= obj.messageOwner.var_id) && lastMessage.messageOwner.date >= obj.messageOwner.date)) {
                                            b++;
                                        } else {
                                            GroupedMessages lastGroupedMessages;
                                            if (lastMessage.getGroupId() != 0) {
                                                lastGroupedMessages = (GroupedMessages) this.groupedMessagesMap.get(lastMessage.getGroupId());
                                                if (lastGroupedMessages != null && lastGroupedMessages.messages.size() == 0) {
                                                    lastGroupedMessages = null;
                                                }
                                            } else {
                                                lastGroupedMessages = null;
                                            }
                                            if (lastGroupedMessages == null) {
                                                placeToPaste = b;
                                            } else {
                                                placeToPaste = this.messages.indexOf(lastGroupedMessages.messages.get(lastGroupedMessages.messages.size() - 1));
                                            }
                                            if (placeToPaste == -1 || placeToPaste > this.messages.size()) {
                                                placeToPaste = this.messages.size();
                                            }
                                        }
                                    }
                                    placeToPaste = this.messages.size();
                                }
                            }
                            if (this.currentEncryptedChat != null && (obj.messageOwner.media instanceof TL_messageMediaWebPage) && (obj.messageOwner.media.webpage instanceof TL_webPageUrlPending)) {
                                if (webpagesToReload == null) {
                                    webpagesToReload = new HashMap();
                                }
                                arrayList = (ArrayList) webpagesToReload.get(obj.messageOwner.media.webpage.url);
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
                                AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$41(this, this.parentLayout.fragmentsStack.size() > 0 ? (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) : null, bundle, obj.messageOwner.action.channel_id));
                                if (newGroups != null) {
                                    for (b = 0; b < newGroups.size(); b++) {
                                        ((GroupedMessages) newGroups.valueAt(b)).calculate();
                                    }
                                    return;
                                }
                                return;
                            }
                            if (this.currentChat != null && this.currentChat.megagroup && ((obj.messageOwner.action instanceof TL_messageActionChatAddUser) || (obj.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                                reloadMegagroup = true;
                            }
                            if (this.minDate[0] == 0 || obj.messageOwner.date < this.minDate[0]) {
                                this.minDate[0] = obj.messageOwner.date;
                            }
                            if (obj.isOut()) {
                                removeUnreadPlane(true);
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
                            this.messagesDict[0].put(obj.getId(), obj);
                            dayArray = (ArrayList) this.messagesByDays.get(obj.dateKey);
                            if (placeToPaste > this.messages.size()) {
                                placeToPaste = this.messages.size();
                            }
                            if (dayArray == null) {
                                dayArray = new ArrayList();
                                this.messagesByDays.put(obj.dateKey, dayArray);
                                dateMsg = new TL_message();
                                dateMsg.message = LocaleController.formatDateChat((long) obj.messageOwner.date);
                                dateMsg.var_id = 0;
                                dateMsg.date = obj.messageOwner.date;
                                messageObject = new MessageObject(this.currentAccount, dateMsg, false);
                                messageObject.type = 10;
                                messageObject.contentType = 1;
                                messageObject.isDateObject = true;
                                this.messages.add(placeToPaste, messageObject);
                                if (this.chatAdapter != null) {
                                    this.chatAdapter.notifyItemInserted(placeToPaste);
                                }
                            }
                            if (!obj.isOut() && this.paused && placeToPaste == 0) {
                                if (!(this.scrollToTopUnReadOnResume || this.unreadMessageObject == null)) {
                                    removeMessageObject(this.unreadMessageObject);
                                    if (placeToPaste > 0) {
                                        placeToPaste--;
                                    }
                                    this.unreadMessageObject = null;
                                }
                                if (this.unreadMessageObject == null) {
                                    dateMsg = new TL_message();
                                    dateMsg.message = TtmlNode.ANONYMOUS_REGION_ID;
                                    dateMsg.var_id = 0;
                                    messageObject = new MessageObject(this.currentAccount, dateMsg, false);
                                    messageObject.type = 6;
                                    messageObject.contentType = 2;
                                    this.messages.add(0, messageObject);
                                    if (this.chatAdapter != null) {
                                        this.chatAdapter.notifyItemInserted(0);
                                    }
                                    this.unreadMessageObject = messageObject;
                                    this.scrollToMessage = this.unreadMessageObject;
                                    this.scrollToMessagePosition = -10000;
                                    this.scrollToTopUnReadOnResume = true;
                                }
                            }
                            dayArray.add(0, obj);
                            this.messages.add(placeToPaste, obj);
                            if (this.chatAdapter != null) {
                                this.chatAdapter.notifyItemChanged(placeToPaste);
                                this.chatAdapter.notifyItemInserted(placeToPaste);
                            }
                            if (!obj.isOut() && obj.messageOwner.mentioned && obj.isContentUnread()) {
                                this.newMentionsCount++;
                            }
                            this.newUnreadMessageCount++;
                            if (obj.type == 10 || obj.type == 11) {
                                updateChat = true;
                            }
                        }
                    }
                    if (webpagesToReload != null) {
                        MessagesController.getInstance(this.currentAccount).reloadWebPages(this.dialog_id, webpagesToReload);
                    }
                    if (newGroups != null) {
                        for (a = 0; a < newGroups.size(); a++) {
                            groupedMessages = (GroupedMessages) newGroups.valueAt(a);
                            int oldCount = groupedMessages.posArray.size();
                            groupedMessages.calculate();
                            int newCount = groupedMessages.posArray.size();
                            if (newCount - oldCount > 0 && this.chatAdapter != null) {
                                index = this.messages.indexOf(groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                                if (index >= 0) {
                                    this.chatAdapter.notifyItemRangeChanged(index, newCount);
                                }
                            }
                        }
                    }
                    if (this.progressView != null) {
                        this.progressView.setVisibility(4);
                    }
                    if (this.chatAdapter == null) {
                        this.scrollToTopOnResume = true;
                    }
                    if (this.chatListView == null || this.chatAdapter == null) {
                        this.scrollToTopOnResume = true;
                    } else {
                        int lastVisible = this.chatLayoutManager.findFirstVisibleItemPosition();
                        if (lastVisible == -1) {
                            lastVisible = 0;
                        }
                        child = this.chatLayoutManager.findViewByPosition(lastVisible);
                        int diff;
                        if (child != null) {
                            diff = child.getBottom() - this.chatListView.getMeasuredHeight();
                        } else {
                            diff = 0;
                        }
                        if ((lastVisible != 0 || diff > AndroidUtilities.m9dp(5.0f)) && !hasFromMe) {
                            if (!(this.newUnreadMessageCount == 0 || this.pagedownButtonCounter == null)) {
                                this.pagedownButtonCounter.setVisibility(0);
                                if (this.prevSetUnreadCount != this.newUnreadMessageCount) {
                                    this.prevSetUnreadCount = this.newUnreadMessageCount;
                                    this.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newUnreadMessageCount)}));
                                }
                            }
                            showPagedownButton(true, true);
                        } else {
                            this.newUnreadMessageCount = 0;
                            if (!this.firstLoading) {
                                if (this.paused) {
                                    this.scrollToTopOnResume = true;
                                } else {
                                    this.forceScrollToTop = true;
                                    moveScrollToLastMessage();
                                }
                            }
                        }
                        if (!(this.newMentionsCount == 0 || this.mentiondownButtonCounter == null)) {
                            this.mentiondownButtonCounter.setVisibility(0);
                            this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newMentionsCount)}));
                            showMentiondownButton(true, true);
                        }
                    }
                } else {
                    int currentMaxDate = Integer.MIN_VALUE;
                    int currentMinMsgId = Integer.MIN_VALUE;
                    if (this.currentEncryptedChat != null) {
                        currentMinMsgId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    for (a = 0; a < arr.size(); a++) {
                        obj = (MessageObject) arr.get(a);
                        if (this.currentUser != null && ((this.currentUser.bot && obj.isOut()) || this.currentUser.var_id == currentUserId)) {
                            obj.setIsRead();
                        }
                        if (!(this.avatarContainer == null || this.currentEncryptedChat == null || obj.messageOwner.action == null || !(obj.messageOwner.action instanceof TL_messageEncryptedAction) || !(obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            this.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                            bundle = new Bundle();
                            bundle.putInt("chat_id", obj.messageOwner.action.channel_id);
                            AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$40(this, this.parentLayout.fragmentsStack.size() > 0 ? (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) : null, bundle, obj.messageOwner.action.channel_id));
                            return;
                        }
                        if (this.currentChat != null && this.currentChat.megagroup && ((obj.messageOwner.action instanceof TL_messageActionChatAddUser) || (obj.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                            reloadMegagroup = true;
                        }
                        if (a == 0 && obj.messageOwner.var_id < 0 && obj.type == 5) {
                            this.needAnimateToMessage = obj;
                        }
                        if (obj.isOut() && obj.isSending()) {
                            scrollToLastMessage(false);
                            return;
                        }
                        if (obj.type >= 0 && this.messagesDict[0].indexOfKey(obj.getId()) < 0) {
                            addToPolls(obj, null);
                            obj.checkLayout();
                            currentMaxDate = Math.max(currentMaxDate, obj.messageOwner.date);
                            if (obj.getId() > 0) {
                                currentMinMsgId = Math.max(obj.getId(), currentMinMsgId);
                                this.last_message_id = Math.max(this.last_message_id, obj.getId());
                            } else if (this.currentEncryptedChat != null) {
                                currentMinMsgId = Math.min(obj.getId(), currentMinMsgId);
                                this.last_message_id = Math.min(this.last_message_id, obj.getId());
                            }
                            if (obj.messageOwner.mentioned && obj.isContentUnread()) {
                                this.newMentionsCount++;
                            }
                            this.newUnreadMessageCount++;
                            if (obj.type == 10 || obj.type == 11) {
                                updateChat = true;
                            }
                        }
                    }
                    if (!(this.newUnreadMessageCount == 0 || this.pagedownButtonCounter == null)) {
                        this.pagedownButtonCounter.setVisibility(0);
                        if (this.prevSetUnreadCount != this.newUnreadMessageCount) {
                            this.prevSetUnreadCount = this.newUnreadMessageCount;
                            this.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newUnreadMessageCount)}));
                        }
                    }
                    if (!(this.newMentionsCount == 0 || this.mentiondownButtonCounter == null)) {
                        this.mentiondownButtonCounter.setVisibility(0);
                        this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newMentionsCount)}));
                        showMentiondownButton(true, true);
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
                    MessagesController.getInstance(this.currentAccount).loadFullChat(this.currentChat.var_id, 0, true);
                }
            }
        } else if (id == NotificationCenter.closeChats) {
            if (args == null || args.length <= 0) {
                removeSelfFromStack();
            } else if (((Long) args[0]).longValue() == this.dialog_id) {
                lambda$checkDiscard$2$PollCreateActivity();
            }
        } else if (id == NotificationCenter.messagesRead) {
            int size2;
            SparseLongArray inbox = args[0];
            SparseLongArray outbox = args[1];
            updated = false;
            if (inbox != null) {
                b = 0;
                size = inbox.size();
                while (b < size) {
                    key = inbox.keyAt(b);
                    long messageId2 = inbox.get(key);
                    if (((long) key) != this.dialog_id) {
                        b++;
                    } else {
                        size2 = this.messages.size();
                        for (a = 0; a < size2; a++) {
                            obj = (MessageObject) this.messages.get(a);
                            if (!obj.isOut() && obj.getId() > 0 && obj.getId() <= ((int) messageId2)) {
                                if (!obj.isUnread()) {
                                    break;
                                }
                                obj.setIsRead();
                                updated = true;
                                this.newUnreadMessageCount--;
                            }
                        }
                        removeUnreadPlane(false);
                    }
                }
            }
            if (updated) {
                if (this.newUnreadMessageCount < 0) {
                    this.newUnreadMessageCount = 0;
                }
                if (this.pagedownButtonCounter != null) {
                    if (this.prevSetUnreadCount != this.newUnreadMessageCount) {
                        this.prevSetUnreadCount = this.newUnreadMessageCount;
                        this.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newUnreadMessageCount)}));
                    }
                    if (this.newUnreadMessageCount <= 0) {
                        if (this.pagedownButtonCounter.getVisibility() != 4) {
                            this.pagedownButtonCounter.setVisibility(4);
                        }
                    } else if (this.pagedownButtonCounter.getVisibility() != 0) {
                        this.pagedownButtonCounter.setVisibility(0);
                    }
                }
            }
            if (outbox != null) {
                b = 0;
                size = outbox.size();
                while (b < size) {
                    key = outbox.keyAt(b);
                    messageId = (int) outbox.get(key);
                    if (((long) key) != this.dialog_id) {
                        b++;
                    } else {
                        size2 = this.messages.size();
                        for (a = 0; a < size2; a++) {
                            obj = (MessageObject) this.messages.get(a);
                            if (obj.isOut() && obj.getId() > 0 && obj.getId() <= messageId) {
                                if (!obj.isUnread()) {
                                    break;
                                }
                                obj.setIsRead();
                                updated = true;
                            }
                        }
                    }
                }
            }
            if (updated) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.historyCleared) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                int max_id = ((Integer) args[1]).intValue();
                updated = false;
                b = 0;
                while (b < this.messages.size()) {
                    obj = (MessageObject) this.messages.get(b);
                    int mid2 = obj.getId();
                    if (mid2 > 0 && mid2 <= max_id) {
                        if (this.chatInfo != null && this.chatInfo.pinned_msg_id == mid2) {
                            this.pinnedMessageObject = null;
                            this.chatInfo.pinned_msg_id = 0;
                            MessagesStorage.getInstance(this.currentAccount).updateChatPinnedMessage(this.chatInfo.var_id, 0);
                            updatePinnedMessageView(true);
                        } else if (this.userInfo != null && this.userInfo.pinned_msg_id == mid2) {
                            this.pinnedMessageObject = null;
                            this.userInfo.pinned_msg_id = 0;
                            MessagesStorage.getInstance(this.currentAccount).updateUserPinnedMessage(this.chatInfo.var_id, 0);
                            updatePinnedMessageView(true);
                        }
                        this.messages.remove(b);
                        b--;
                        this.messagesDict[0].remove(mid2);
                        dayArr = (ArrayList) this.messagesByDays.get(obj.dateKey);
                        if (dayArr != null) {
                            dayArr.remove(obj);
                            if (dayArr.isEmpty()) {
                                this.messagesByDays.remove(obj.dateKey);
                                if (b >= 0 && b < this.messages.size()) {
                                    this.messages.remove(b);
                                    b--;
                                }
                            }
                        }
                        updated = true;
                    }
                    b++;
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
                            this.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                            updateBottomOverlay();
                        }
                    } else {
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
                        instance = MessagesController.getInstance(this.currentAccount);
                        j = this.dialog_id;
                        z = !this.cacheEndReached[0];
                        i = this.minDate[0];
                        i2 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i3 = this.lastLoadIndex;
                        this.lastLoadIndex = i3 + 1;
                        instance.loadMessages(j, 30, 0, 0, z, i, i2, 0, 0, isChannel, i3);
                        this.loading = true;
                    }
                }
                if (updated && this.chatAdapter != null) {
                    removeUnreadPlane(true);
                    this.chatAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            ArrayList<Integer> markAsDeletedMessages = args[0];
            channelId = ((Integer) args[1]).intValue();
            loadIndex = 0;
            if (ChatObject.isChannel(this.currentChat)) {
                if (channelId == 0 && this.mergeDialogId != 0) {
                    loadIndex = 1;
                } else if (channelId == this.currentChat.var_id) {
                    loadIndex = 0;
                } else {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            updated = false;
            newGroups = null;
            size = markAsDeletedMessages.size();
            boolean updatedSelected = false;
            boolean updatedSelectedLast = false;
            a = 0;
            while (a < size) {
                Integer ids = (Integer) markAsDeletedMessages.get(a);
                obj = (MessageObject) this.messagesDict[loadIndex].get(ids.intValue());
                if (loadIndex == 0 && ((this.chatInfo != null && this.chatInfo.pinned_msg_id == ids.intValue()) || (this.userInfo != null && this.userInfo.pinned_msg_id == ids.intValue()))) {
                    this.pinnedMessageObject = null;
                    if (this.chatInfo != null) {
                        this.chatInfo.pinned_msg_id = 0;
                    } else if (this.userInfo != null) {
                        this.userInfo.pinned_msg_id = 0;
                    }
                    MessagesStorage.getInstance(this.currentAccount).updateChatPinnedMessage(channelId, 0);
                    updatePinnedMessageView(true);
                }
                if (obj != null) {
                    index = this.messages.indexOf(obj);
                    if (index != -1) {
                        if (this.selectedMessagesIds[loadIndex].indexOfKey(ids.intValue()) >= 0) {
                            updatedSelected = true;
                            updatedSelectedLast = a == size + -1;
                            addToSelectedMessages(obj, false, updatedSelectedLast);
                        }
                        removed = (MessageObject) this.messages.remove(index);
                        if (removed.getGroupId() != 0) {
                            groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(removed.getGroupId());
                            if (groupedMessages != null) {
                                if (newGroups == null) {
                                    newGroups = new LongSparseArray();
                                }
                                newGroups.put(groupedMessages.groupId, groupedMessages);
                                groupedMessages.messages.remove(obj);
                            }
                        }
                        this.messagesDict[loadIndex].remove(ids.intValue());
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
                a++;
            }
            if (updatedSelected && !updatedSelectedLast) {
                addToSelectedMessages(null, false, true);
            }
            if (newGroups != null) {
                for (a = 0; a < newGroups.size(); a++) {
                    groupedMessages = (GroupedMessages) newGroups.valueAt(a);
                    if (groupedMessages.messages.isEmpty()) {
                        this.groupedMessagesMap.remove(groupedMessages.groupId);
                    } else {
                        groupedMessages.calculate();
                        index = this.messages.indexOf((MessageObject) groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                        if (index >= 0 && this.chatAdapter != null) {
                            this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.messagesStartRow + index, groupedMessages.messages.size());
                        }
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
                        this.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                        updateBottomOverlay();
                    }
                } else {
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
                    instance = MessagesController.getInstance(this.currentAccount);
                    j = this.dialog_id;
                    z = !this.cacheEndReached[0];
                    i = this.minDate[0];
                    i2 = this.classGuid;
                    isChannel = ChatObject.isChannel(this.currentChat);
                    i3 = this.lastLoadIndex;
                    this.lastLoadIndex = i3 + 1;
                    instance.loadMessages(j, 30, 0, 0, z, i, i2, 0, 0, isChannel, i3);
                    this.loading = true;
                }
            }
            if (this.chatAdapter == null) {
                return;
            }
            if (updated) {
                removeUnreadPlane(false);
                count = this.chatListView.getChildCount();
                int position = -1;
                int bottom2 = 0;
                for (a = 0; a < count; a++) {
                    child = this.chatListView.getChildAt(a);
                    messageObject2 = null;
                    if (child instanceof ChatMessageCell) {
                        messageObject2 = ((ChatMessageCell) child).getMessageObject();
                    } else if (child instanceof ChatActionCell) {
                        messageObject2 = ((ChatActionCell) child).getMessageObject();
                    }
                    if (messageObject2 != null) {
                        idx = this.messages.indexOf(messageObject2);
                        if (idx >= 0) {
                            position = this.chatAdapter.messagesStartRow + idx;
                            bottom2 = child.getBottom();
                            break;
                        }
                    }
                }
                this.chatAdapter.notifyDataSetChanged();
                if (position != -1) {
                    this.chatLayoutManager.scrollToPositionWithOffset(position, (this.chatListView.getMeasuredHeight() - bottom2) - this.chatListView.getPaddingBottom());
                    return;
                }
                return;
            }
            this.first_unread_id = 0;
            this.last_message_id = 0;
            this.createUnreadMessageAfterId = 0;
            removeMessageObject(this.unreadMessageObject);
            this.unreadMessageObject = null;
            if (this.pagedownButtonCounter != null) {
                this.pagedownButtonCounter.setVisibility(4);
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[0];
            obj = (MessageObject) this.messagesDict[0].get(msgId.intValue());
            if (obj != null) {
                Integer newMsgId = args[1];
                if (newMsgId.equals(msgId) || this.messagesDict[0].indexOfKey(newMsgId.intValue()) < 0) {
                    Long grouped_id;
                    Message newMsgObj = args[2];
                    if (args.length >= 4) {
                        grouped_id = args[4];
                    } else {
                        grouped_id = Long.valueOf(0);
                    }
                    boolean mediaUpdated = false;
                    boolean updatedForward = false;
                    if (newMsgObj != null) {
                        try {
                            updatedForward = obj.isForwarded() && ((obj.messageOwner.reply_markup == null && newMsgObj.reply_markup != null) || !obj.messageOwner.message.equals(newMsgObj.message));
                            mediaUpdated = updatedForward || ((obj.messageOwner.params != null && obj.messageOwner.params.containsKey("query_id")) || !(newMsgObj.media == null || obj.messageOwner.media == null || newMsgObj.media.getClass().equals(obj.messageOwner.media.getClass())));
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                        }
                        if (!(obj.getGroupId() == 0 || newMsgObj.grouped_id == 0)) {
                            GroupedMessages oldGroup = (GroupedMessages) this.groupedMessagesMap.get(obj.getGroupId());
                            if (oldGroup != null) {
                                this.groupedMessagesMap.put(newMsgObj.grouped_id, oldGroup);
                            }
                            obj.localSentGroupId = obj.messageOwner.grouped_id;
                            obj.messageOwner.grouped_id = grouped_id.longValue();
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
                    this.messagesDict[0].remove(msgId.intValue());
                    this.messagesDict[0].put(newMsgId.intValue(), obj);
                    obj.messageOwner.var_id = newMsgId.intValue();
                    obj.messageOwner.send_state = 0;
                    obj.forceUpdate = mediaUpdated;
                    addToPolls(obj, null);
                    messArr = new ArrayList();
                    messArr.add(obj);
                    if (this.currentEncryptedChat == null) {
                        DataQuery.getInstance(this.currentAccount).loadReplyMessagesForMessages(messArr, this.dialog_id);
                    }
                    if (this.chatAdapter != null) {
                        this.chatAdapter.updateRowWithMessageObject(obj, true);
                    }
                    if (this.chatLayoutManager != null && mediaUpdated && this.chatLayoutManager.findFirstVisibleItemPosition() == 0) {
                        moveScrollToLastMessage();
                    }
                    NotificationsController.getInstance(this.currentAccount).playOutChatSound();
                    return;
                }
                removed = (MessageObject) this.messagesDict[0].get(msgId.intValue());
                this.messagesDict[0].remove(msgId.intValue());
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
            obj = (MessageObject) this.messagesDict[0].get(((Integer) args[0]).intValue());
            if (obj != null) {
                obj.messageOwner.send_state = 0;
                if (this.chatAdapter != null) {
                    this.chatAdapter.updateRowWithMessageObject(obj, true);
                }
            }
        } else if (id == NotificationCenter.messageSendError) {
            obj = (MessageObject) this.messagesDict[0].get(((Integer) args[0]).intValue());
            if (obj != null) {
                obj.messageOwner.send_state = 2;
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = args[0];
            if (this.currentChat != null && chatFull.var_id == this.currentChat.var_id) {
                if (chatFull instanceof TL_channelFull) {
                    if (this.currentChat.megagroup) {
                        int lastDate = 0;
                        if (chatFull.participants != null) {
                            for (a = 0; a < chatFull.participants.participants.size(); a++) {
                                lastDate = Math.max(((ChatParticipant) chatFull.participants.participants.get(a)).date, lastDate);
                            }
                        }
                        if (lastDate == 0 || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastDate)) > 3600) {
                            MessagesController.getInstance(this.currentAccount).loadChannelParticipants(Integer.valueOf(this.currentChat.var_id));
                        }
                    }
                    if (chatFull.participants == null && this.chatInfo != null) {
                        chatFull.participants = this.chatInfo.participants;
                    }
                }
                this.chatInfo = chatFull;
                if (this.chatActivityEnterView != null) {
                    this.chatActivityEnterView.setChatInfo(this.chatInfo);
                }
                if (this.mentionsAdapter != null) {
                    this.mentionsAdapter.setChatInfo(this.chatInfo);
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
                    SendMessagesHelper.getInstance(this.currentAccount).setCurrentChatInfo(this.chatInfo);
                }
                if (this.chatInfo instanceof TL_chatFull) {
                    this.hasBotsCommands = false;
                    this.botInfo.clear();
                    this.botsCount = 0;
                    URLSpanBotCommand.enabled = false;
                    for (a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.chatInfo.participants.participants.get(a)).user_id));
                        if (user != null && user.bot) {
                            URLSpanBotCommand.enabled = true;
                            this.botsCount++;
                            DataQuery.getInstance(this.currentAccount).loadBotInfo(user.var_id, true, this.classGuid);
                        }
                    }
                    if (this.chatListView != null) {
                        this.chatListView.invalidateViews();
                    }
                } else if (this.chatInfo instanceof TL_channelFull) {
                    this.hasBotsCommands = false;
                    this.botInfo.clear();
                    this.botsCount = 0;
                    z2 = (this.chatInfo.bot_info.isEmpty() || this.currentChat == null || !this.currentChat.megagroup) ? false : true;
                    URLSpanBotCommand.enabled = z2;
                    this.botsCount = this.chatInfo.bot_info.size();
                    for (a = 0; a < this.chatInfo.bot_info.size(); a++) {
                        BotInfo bot = (BotInfo) this.chatInfo.bot_info.get(a);
                        if (!bot.commands.isEmpty() && (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup))) {
                            this.hasBotsCommands = true;
                        }
                        this.botInfo.put(bot.user_id, bot);
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
                if (ChatObject.isChannel(this.currentChat) && this.mergeDialogId == 0 && this.chatInfo.migrated_from_chat_id != 0) {
                    this.mergeDialogId = (long) (-this.chatInfo.migrated_from_chat_id);
                    this.maxMessageId[1] = this.chatInfo.migrated_from_max_id;
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (id == NotificationCenter.chatInfoCantLoad) {
            int chatId2 = ((Integer) args[0]).intValue();
            if (this.currentChat != null && this.currentChat.var_id == chatId2) {
                int reason = ((Integer) args[1]).intValue();
                if (getParentActivity() != null && this.closeChatDialog == null) {
                    builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    if (reason == 0) {
                        builder.setMessage(LocaleController.getString("ChannelCantOpenPrivate", R.string.ChannelCantOpenPrivate));
                    } else if (reason == 1) {
                        builder.setMessage(LocaleController.getString("ChannelCantOpenNa", R.string.ChannelCantOpenNa));
                    } else if (reason == 2) {
                        builder.setMessage(LocaleController.getString("ChannelCantOpenBanned", R.string.ChannelCantOpenBanned));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    Dialog create = builder.create();
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
        } else if (id == NotificationCenter.contactsDidLoad) {
            updateContactStatus();
            if (this.currentEncryptedChat != null) {
                updateSpamView();
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.updateSubtitle();
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat chat2 = args[0];
            if (this.currentEncryptedChat != null && chat2.var_id == this.currentEncryptedChat.var_id) {
                this.currentEncryptedChat = chat2;
                updateContactStatus();
                updateSecretStatus();
                initStickers();
                if (this.chatActivityEnterView != null) {
                    ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
                    z2 = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23;
                    boolean z3 = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
                    chatActivityEnterView.setAllowStickersAndGifs(z2, z3);
                    this.chatActivityEnterView.checkRoundVideo();
                }
                if (this.mentionsAdapter != null) {
                    MentionsAdapter mentionsAdapter = this.mentionsAdapter;
                    z2 = !this.chatActivityEnterView.isEditingMessage() && (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46);
                    mentionsAdapter.setNeedBotContext(z2);
                }
            }
        } else if (id == NotificationCenter.messagesReadEncrypted) {
            int encId = ((Integer) args[0]).intValue();
            if (this.currentEncryptedChat != null && this.currentEncryptedChat.var_id == encId) {
                int date = ((Integer) args[1]).intValue();
                Iterator it = this.messages.iterator();
                while (it.hasNext()) {
                    obj = (MessageObject) it.next();
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
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            if (this.dialog_id == ((Long) args[0]).longValue()) {
                this.messages.clear();
                this.waitingForLoad.clear();
                this.messagesByDays.clear();
                this.groupedMessagesMap.clear();
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
                    this.selectedMessagesCanStarIds[a].clear();
                }
                this.cantDeleteMessagesCount = 0;
                this.canEditMessagesCount = 0;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                if (this.botButtons != null) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setButtons(null, false);
                    }
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
                    instance = MessagesController.getInstance(this.currentAccount);
                    j = this.dialog_id;
                    int i4 = AndroidUtilities.isTablet() ? 30 : 20;
                    i2 = this.classGuid;
                    isChannel = ChatObject.isChannel(this.currentChat);
                    i3 = this.lastLoadIndex;
                    this.lastLoadIndex = i3 + 1;
                    instance.loadMessages(j, i4, 0, 0, true, 0, i2, 2, 0, isChannel, i3);
                } else if (this.progressView != null) {
                    this.progressView.setVisibility(4);
                    this.chatListView.setEmptyView(this.emptyViewContainer);
                }
                if (this.chatAdapter != null) {
                    this.chatAdapter.notifyDataSetChanged();
                }
                if (this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                    this.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                    updateBottomOverlay();
                }
            }
        } else if (id == NotificationCenter.screenshotTook) {
            updateInformationForScreenshotDetector();
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            if (this.currentUser != null) {
                boolean oldValue = this.userBlocked;
                this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.currentUser.var_id) >= 0;
                if (oldValue != this.userBlocked) {
                    updateBottomOverlay();
                }
            }
        } else if (id == NotificationCenter.fileNewChunkAvailable) {
            messageObject2 = (MessageObject) args[0];
            long finalSize = ((Long) args[3]).longValue();
            if (finalSize != 0 && this.dialog_id == messageObject2.getDialogId()) {
                MessageObject currentObject = (MessageObject) this.messagesDict[0].get(messageObject2.getId());
                if (currentObject != null && currentObject.messageOwner.media.document != null) {
                    currentObject.messageOwner.media.document.size = (int) finalSize;
                    updateVisibleRows();
                }
            }
        } else if (id == NotificationCenter.didCreatedNewDeleteTask) {
            SparseArray<ArrayList<Long>> mids = args[0];
            boolean changed = false;
            for (int i5 = 0; i5 < mids.size(); i5++) {
                key = mids.keyAt(i5);
                ArrayList<Long> arr2 = (ArrayList) mids.get(key);
                for (a = 0; a < arr2.size(); a++) {
                    mid = ((Long) arr2.get(a)).longValue();
                    if (a == 0) {
                        channelId = (int) (mid >> 32);
                        if (channelId < 0) {
                            channelId = 0;
                        }
                        if (channelId != (ChatObject.isChannel(this.currentChat) ? this.currentChat.var_id : 0)) {
                            return;
                        }
                    }
                    messageObject2 = (MessageObject) this.messagesDict[0].get((int) mid);
                    if (messageObject2 != null) {
                        messageObject2.messageOwner.destroyTime = key;
                        changed = true;
                    }
                }
            }
            if (changed) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.messagePlayingDidStart) {
            messageObject2 = (MessageObject) args[0];
            if (messageObject2.eventId == 0) {
                sendSecretMessageRead(messageObject2);
                if (!(!messageObject2.isRoundVideo() || this.fragmentView == null || this.fragmentView.getParent() == null)) {
                    MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                    updateTextureViewPosition();
                }
                if (this.chatListView != null) {
                    MessageObject messageObject1;
                    count = this.chatListView.getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.chatListView.getChildAt(a);
                        if (view instanceof ChatMessageCell) {
                            cell = (ChatMessageCell) view;
                            messageObject1 = cell.getMessageObject();
                            if (messageObject1 != null) {
                                if (messageObject1.isVoice() || messageObject1.isMusic()) {
                                    cell.updateButtonState(false, false);
                                } else if (messageObject1.isRoundVideo()) {
                                    cell.checkRoundVideoPlayback(false);
                                    if (!(MediaController.getInstance().isPlayingMessage(messageObject1) || messageObject1.audioProgress == 0.0f)) {
                                        messageObject1.resetPlayingProgress();
                                        cell.invalidate();
                                    }
                                }
                            }
                        }
                    }
                    count = this.mentionListView.getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.mentionListView.getChildAt(a);
                        if (view instanceof ContextLinkCell) {
                            cell2 = (ContextLinkCell) view;
                            messageObject1 = cell2.getMessageObject();
                            if (messageObject1 != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                                cell2.updateButtonState(false, true);
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            if (id == NotificationCenter.messagePlayingDidReset) {
                destroyTextureView();
            }
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        messageObject2 = cell.getMessageObject();
                        if (messageObject2 != null) {
                            if (messageObject2.isVoice() || messageObject2.isMusic()) {
                                cell.updateButtonState(false, false);
                            } else if (messageObject2.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject2)) {
                                cell.checkRoundVideoPlayback(true);
                            }
                        }
                    }
                }
                count = this.mentionListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.mentionListView.getChildAt(a);
                    if (view instanceof ContextLinkCell) {
                        cell2 = (ContextLinkCell) view;
                        messageObject2 = cell2.getMessageObject();
                        if (messageObject2 != null && (messageObject2.isVoice() || messageObject2.isMusic())) {
                            cell2.updateButtonState(false, true);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid3 = args[0];
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        MessageObject playing = cell.getMessageObject();
                        if (playing != null && playing.getId() == mid3.intValue()) {
                            player = MediaController.getInstance().getPlayingMessageObject();
                            if (player != null) {
                                playing.audioProgress = player.audioProgress;
                                playing.audioProgressSec = player.audioProgressSec;
                                playing.audioPlayerDuration = player.audioPlayerDuration;
                                cell.updatePlayingMessageProgress();
                                if (this.drawLaterRoundProgressCell == cell) {
                                    this.fragmentView.invalidate();
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.didUpdatePollResults) {
            arrayList = (ArrayList) this.polls.get(((Long) args[0]).longValue());
            if (arrayList != null) {
                TL_poll poll = args[1];
                TL_pollResults results = args[2];
                int N = arrayList.size();
                for (a = 0; a < N; a++) {
                    MessageObject object = (MessageObject) arrayList.get(a);
                    TL_messageMediaPoll media = (TL_messageMediaPoll) object.messageOwner.media;
                    if (poll != null) {
                        media.poll = poll;
                    }
                    MessageObject.updatePollResults(media, results);
                    this.chatAdapter.updateRowWithMessageObject(object, true);
                }
            }
        } else if (id == NotificationCenter.updateMessageMedia) {
            message = args[0];
            MessageObject existMessageObject = (MessageObject) this.messagesDict[0].get(message.var_id);
            if (existMessageObject != null) {
                existMessageObject.messageOwner.media = message.media;
                existMessageObject.messageOwner.attachPath = message.attachPath;
                existMessageObject.generateThumbs(false);
                if (existMessageObject.getGroupId() != 0 && (existMessageObject.photoThumbs == null || existMessageObject.photoThumbs.isEmpty())) {
                    groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(existMessageObject.getGroupId());
                    if (groupedMessages != null) {
                        idx = groupedMessages.messages.indexOf(existMessageObject);
                        if (idx >= 0) {
                            int updateCount = groupedMessages.messages.size();
                            messageObject2 = null;
                            if (idx > 0 && idx < groupedMessages.messages.size() - 1) {
                                slicedGroup = new GroupedMessages();
                                slicedGroup.groupId = Utilities.random.nextLong();
                                slicedGroup.messages.addAll(groupedMessages.messages.subList(idx + 1, groupedMessages.messages.size()));
                                for (b = 0; b < slicedGroup.messages.size(); b++) {
                                    ((MessageObject) slicedGroup.messages.get(b)).localGroupId = slicedGroup.groupId;
                                    groupedMessages.messages.remove(idx + 1);
                                }
                                this.groupedMessagesMap.put(slicedGroup.groupId, slicedGroup);
                                messageObject2 = (MessageObject) slicedGroup.messages.get(slicedGroup.messages.size() - 1);
                                slicedGroup.calculate();
                            }
                            groupedMessages.messages.remove(idx);
                            if (groupedMessages.messages.isEmpty()) {
                                this.groupedMessagesMap.remove(groupedMessages.groupId);
                            } else {
                                if (messageObject2 == null) {
                                    messageObject2 = (MessageObject) groupedMessages.messages.get(groupedMessages.messages.size() - 1);
                                }
                                groupedMessages.calculate();
                                index = this.messages.indexOf(messageObject2);
                                if (index >= 0 && this.chatAdapter != null) {
                                    this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.messagesStartRow + index, updateCount);
                                }
                            }
                        }
                    }
                }
                if (message.media.ttl_seconds == 0 || !((message.media.photo instanceof TL_photoEmpty) || (message.media.document instanceof TL_documentEmpty))) {
                    updateVisibleRows();
                    return;
                }
                existMessageObject.setType();
                this.chatAdapter.updateRowWithMessageObject(existMessageObject, false);
            }
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            did = ((Long) args[0]).longValue();
            if (did == this.dialog_id || did == this.mergeDialogId) {
                loadIndex = did == this.dialog_id ? 0 : 1;
                ArrayList<MessageObject> messageObjects = args[1];
                newGroups = null;
                for (a = 0; a < messageObjects.size(); a++) {
                    messageObject2 = (MessageObject) messageObjects.get(a);
                    MessageObject old = (MessageObject) this.messagesDict[loadIndex].get(messageObject2.getId());
                    if (this.pinnedMessageObject != null && this.pinnedMessageObject.getId() == messageObject2.getId()) {
                        this.pinnedMessageObject = messageObject2;
                        updatePinnedMessageView(true);
                    }
                    if (old != null) {
                        addToPolls(messageObject2, old);
                        if (messageObject2.type >= 0) {
                            if (old.replyMessageObject != null) {
                                messageObject2.replyMessageObject = old.replyMessageObject;
                                if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                                    messageObject2.generateGameMessageText(null);
                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionPaymentSent) {
                                    messageObject2.generatePaymentSentMessageText(null);
                                }
                            }
                            if (!old.isEditing()) {
                                if (old.getFileName().equals(messageObject2.getFileName())) {
                                    messageObject2.messageOwner.attachPath = old.messageOwner.attachPath;
                                    messageObject2.attachPathExists = old.attachPathExists;
                                    messageObject2.mediaExists = old.mediaExists;
                                } else {
                                    messageObject2.checkMediaExistance();
                                }
                            }
                            this.messagesDict[loadIndex].put(old.getId(), messageObject2);
                        } else {
                            this.messagesDict[loadIndex].remove(old.getId());
                        }
                        index = this.messages.indexOf(old);
                        if (index >= 0) {
                            dayArr = (ArrayList) this.messagesByDays.get(old.dateKey);
                            int index2 = -1;
                            if (dayArr != null) {
                                index2 = dayArr.indexOf(old);
                            }
                            if (old.getGroupId() != 0) {
                                groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(old.getGroupId());
                                if (groupedMessages != null) {
                                    idx = groupedMessages.messages.indexOf(old);
                                    if (idx >= 0) {
                                        if (old.getGroupId() != messageObject2.getGroupId()) {
                                            this.groupedMessagesMap.put(messageObject2.getGroupId(), groupedMessages);
                                        }
                                        if (messageObject2.photoThumbs == null || messageObject2.photoThumbs.isEmpty()) {
                                            if (newGroups == null) {
                                                newGroups = new LongSparseArray();
                                            }
                                            newGroups.put(groupedMessages.groupId, groupedMessages);
                                            if (idx > 0 && idx < groupedMessages.messages.size() - 1) {
                                                slicedGroup = new GroupedMessages();
                                                slicedGroup.groupId = Utilities.random.nextLong();
                                                slicedGroup.messages.addAll(groupedMessages.messages.subList(idx + 1, groupedMessages.messages.size()));
                                                for (b = 0; b < slicedGroup.messages.size(); b++) {
                                                    ((MessageObject) slicedGroup.messages.get(b)).localGroupId = slicedGroup.groupId;
                                                    groupedMessages.messages.remove(idx + 1);
                                                }
                                                newGroups.put(slicedGroup.groupId, slicedGroup);
                                                this.groupedMessagesMap.put(slicedGroup.groupId, slicedGroup);
                                            }
                                            groupedMessages.messages.remove(idx);
                                        } else {
                                            groupedMessages.messages.set(idx, messageObject2);
                                            GroupedMessagePosition oldPosition = (GroupedMessagePosition) groupedMessages.positions.remove(old);
                                            if (oldPosition != null) {
                                                groupedMessages.positions.put(messageObject2, oldPosition);
                                            }
                                            if (newGroups == null) {
                                                newGroups = new LongSparseArray();
                                            }
                                            newGroups.put(groupedMessages.groupId, groupedMessages);
                                        }
                                    }
                                }
                            }
                            if (messageObject2.type >= 0) {
                                this.messages.set(index, messageObject2);
                                if (this.chatAdapter != null) {
                                    this.chatAdapter.updateRowAtPosition(this.chatAdapter.messagesStartRow + index);
                                }
                                if (index2 >= 0) {
                                    dayArr.set(index2, messageObject2);
                                }
                            } else {
                                this.messages.remove(index);
                                if (this.chatAdapter != null) {
                                    this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + index);
                                }
                                if (index2 >= 0) {
                                    dayArr.remove(index2);
                                    if (dayArr.isEmpty()) {
                                        this.messagesByDays.remove(old.dateKey);
                                        this.messages.remove(index);
                                        this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow);
                                    }
                                }
                            }
                        }
                    }
                }
                if (newGroups != null) {
                    for (b = 0; b < newGroups.size(); b++) {
                        groupedMessages = (GroupedMessages) newGroups.valueAt(b);
                        if (groupedMessages.messages.isEmpty()) {
                            this.groupedMessagesMap.remove(groupedMessages.groupId);
                        } else {
                            groupedMessages.calculate();
                            index = this.messages.indexOf((MessageObject) groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                            if (index >= 0 && this.chatAdapter != null) {
                                this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.messagesStartRow + index, groupedMessages.messages.size());
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateTitleIcons();
            if (ChatObject.isChannel(this.currentChat)) {
                updateBottomOverlay();
            }
        } else if (id == NotificationCenter.replyMessagesDidLoad) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.pinnedMessageDidLoad) {
            MessageObject message3 = args[0];
            if (message3.getDialogId() != this.dialog_id) {
                return;
            }
            if ((this.chatInfo != null && this.chatInfo.pinned_msg_id == message3.getId()) || (this.userInfo != null && this.userInfo.pinned_msg_id == message3.getId())) {
                this.pinnedMessageObject = message3;
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
                    currentMessage = (MessageObject) this.messagesDict[did == this.dialog_id ? 0 : 1].get(message.var_id);
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
            }
        } else if (id == NotificationCenter.didReceivedWebpagesInUpdates) {
            if (this.foundWebPage != null) {
                LongSparseArray<WebPage> hashMap = args[0];
                for (a = 0; a < hashMap.size(); a++) {
                    WebPage webPage = (WebPage) hashMap.valueAt(a);
                    if (webPage.var_id == this.foundWebPage.var_id) {
                        showFieldPanelForWebPage(!(webPage instanceof TL_webPageEmpty), webPage, false);
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.messagesReadContent) {
            ArrayList<Long> arrayList3 = args[0];
            updated = false;
            int currentChannelId = ChatObject.isChannel(this.currentChat) ? this.currentChat.var_id : 0;
            for (a = 0; a < arrayList3.size(); a++) {
                mid = ((Long) arrayList3.get(a)).longValue();
                channelId = (int) (mid >> 32);
                if (channelId < 0) {
                    channelId = 0;
                }
                if (channelId == currentChannelId) {
                    currentMessage = (MessageObject) this.messagesDict[0].get((int) mid);
                    if (currentMessage != null) {
                        currentMessage.setContentIsRead();
                        updated = true;
                        if (currentMessage.messageOwner.mentioned) {
                            this.newMentionsCount--;
                            if (this.newMentionsCount <= 0) {
                                this.newMentionsCount = 0;
                                this.hasAllMentionsLocal = true;
                                showMentiondownButton(false, true);
                            } else {
                                this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newMentionsCount)}));
                            }
                        }
                    }
                }
            }
            if (updated) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.botInfoDidLoad) {
            if (this.classGuid == ((Integer) args[1]).intValue()) {
                BotInfo info = args[0];
                if (this.currentEncryptedChat == null) {
                    if (!(info.commands.isEmpty() || ChatObject.isChannel(this.currentChat))) {
                        this.hasBotsCommands = true;
                    }
                    this.botInfo.put(info.user_id, info);
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyItemChanged(this.chatAdapter.botInfoRow);
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
        } else if (id == NotificationCenter.botKeyboardDidLoad) {
            if (this.dialog_id == ((Long) args[1]).longValue()) {
                message = (Message) args[0];
                if (message == null || this.userBlocked) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                            this.botReplyButtons = null;
                            hideFieldPanel();
                        }
                        this.chatActivityEnterView.setButtons(this.botButtons);
                        return;
                    }
                    return;
                }
                this.botButtons = new MessageObject(this.currentAccount, message, false);
                checkBotKeyboard();
            }
        } else if (id == NotificationCenter.chatSearchResultsAvailable) {
            if (this.classGuid == ((Integer) args[0]).intValue()) {
                messageId = ((Integer) args[1]).intValue();
                did = ((Long) args[3]).longValue();
                if (messageId != 0) {
                    scrollToMessageId(messageId, 0, true, did == this.dialog_id ? 0 : 1, false);
                }
                updateSearchButtons(((Integer) args[2]).intValue(), ((Integer) args[4]).intValue(), ((Integer) args[5]).intValue());
                if (this.searchItem != null) {
                    this.searchItem.setShowSearchProgress(false);
                }
            }
        } else if (id == NotificationCenter.chatSearchResultsLoading) {
            if (this.classGuid == ((Integer) args[0]).intValue() && this.searchItem != null) {
                this.searchItem.setShowSearchProgress(true);
            }
        } else if (id == NotificationCenter.didUpdatedMessagesViews) {
            SparseIntArray array = (SparseIntArray) args[0].get((int) this.dialog_id);
            if (array != null) {
                updated = false;
                for (a = 0; a < array.size(); a++) {
                    messageId = array.keyAt(a);
                    messageObject2 = (MessageObject) this.messagesDict[0].get(messageId);
                    if (messageObject2 != null) {
                        int newValue = array.get(messageId);
                        if (newValue > messageObject2.messageOwner.views) {
                            messageObject2.messageOwner.views = newValue;
                            updated = true;
                        }
                    }
                }
                if (updated) {
                    updateVisibleRows();
                }
            }
        } else if (id == NotificationCenter.peerSettingsDidLoad) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                updateSpamView();
            }
        } else if (id == NotificationCenter.newDraftReceived) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                applyDraftMaybe(true);
            }
        } else if (id == NotificationCenter.userInfoDidLoad) {
            Integer uid = args[0];
            if (this.currentUser != null && this.currentUser.var_id == uid.intValue()) {
                this.userInfo = (TL_userFull) args[1];
                if (this.headerItem != null) {
                    if (this.userInfo.phone_calls_available) {
                        this.headerItem.showSubItem(32);
                    } else {
                        this.headerItem.hideSubItem(32);
                    }
                }
                if (args[2] instanceof MessageObject) {
                    this.pinnedMessageObject = (MessageObject) args[2];
                    updatePinnedMessageView(false);
                    return;
                }
                updatePinnedMessageView(true);
            }
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            if (this.fragmentView != null) {
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
        } else if (id == NotificationCenter.channelRightsUpdated) {
            chat = args[0];
            if (this.currentChat != null && chat.var_id == this.currentChat.var_id && this.chatActivityEnterView != null) {
                this.currentChat = chat;
                this.chatActivityEnterView.checkChannelRights();
                checkRaiseSensors();
                updateSecretStatus();
            }
        } else if (id == NotificationCenter.updateMentionsCount) {
            if (this.dialog_id == ((Long) args[0]).longValue()) {
                count = ((Integer) args[1]).intValue();
                if (this.newMentionsCount > count) {
                    this.newMentionsCount = count;
                    if (this.newMentionsCount <= 0) {
                        this.newMentionsCount = 0;
                        this.hasAllMentionsLocal = true;
                        showMentiondownButton(false, true);
                        return;
                    }
                    this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newMentionsCount)}));
                }
            }
        } else if (id == NotificationCenter.audioRecordTooShort) {
            showVoiceHint(false, ((Boolean) args[0]).booleanValue());
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$50$ChatActivity() {
        if (this.parentLayout != null) {
            this.parentLayout.resumeDelayedFragmentAnimation();
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$52$ChatActivity(BaseFragment lastFragment, Bundle bundle, int channel_id) {
        ActionBarLayout parentLayout = this.parentLayout;
        if (lastFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(lastFragment, NotificationCenter.closeChats);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        parentLayout.presentFragment(new ChatActivity(bundle), true);
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$80(this, channel_id), 1000);
    }

    final /* synthetic */ void lambda$null$51$ChatActivity(int channel_id) {
        MessagesController.getInstance(this.currentAccount).loadFullChat(channel_id, 0, true);
    }

    final /* synthetic */ void lambda$didReceivedNotification$54$ChatActivity(BaseFragment lastFragment, Bundle bundle, int channel_id) {
        ActionBarLayout parentLayout = this.parentLayout;
        if (lastFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(lastFragment, NotificationCenter.closeChats);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        parentLayout.presentFragment(new ChatActivity(bundle), true);
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$79(this, channel_id), 1000);
    }

    final /* synthetic */ void lambda$null$53$ChatActivity(int channel_id) {
        MessagesController.getInstance(this.currentAccount).loadFullChat(channel_id, 0, true);
    }

    private void checkSecretMessageForLocation(MessageObject messageObject) {
        if (messageObject.type == 4 && !this.locationAlertShown && !SharedConfig.isSecretMapPreviewSet()) {
            this.locationAlertShown = true;
            AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new ChatActivity$$Lambda$42(this), true);
        }
    }

    final /* synthetic */ void lambda$checkSecretMessageForLocation$55$ChatActivity() {
        int count = this.chatListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell cell = (ChatMessageCell) view;
                if (cell.getMessageObject().type == 4) {
                    cell.forceResetMessageObject();
                }
            }
        }
    }

    public boolean processSwitchButton(TL_keyboardButtonSwitchInline button) {
        if (this.inlineReturn == 0 || button.same_peer || this.parentLayout == null) {
            return false;
        }
        String query = "@" + this.currentUser.username + " " + button.query;
        if (this.inlineReturn == this.dialog_id) {
            this.inlineReturn = 0;
            this.chatActivityEnterView.setFieldText(query);
        } else {
            DataQuery.getInstance(this.currentAccount).saveDraft(this.inlineReturn, query, null, null, false);
            if (this.parentLayout.fragmentsStack.size() > 1) {
                BaseFragment prevFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
                if ((prevFragment instanceof ChatActivity) && ((ChatActivity) prevFragment).dialog_id == this.inlineReturn) {
                    lambda$checkDiscard$2$PollCreateActivity();
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

    private void addToPolls(MessageObject obj, MessageObject old) {
        long pollId = obj.getPollId();
        if (pollId != 0) {
            ArrayList<MessageObject> arrayList = (ArrayList) this.polls.get(pollId);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.polls.put(pollId, arrayList);
            }
            arrayList.add(obj);
            if (old != null) {
                arrayList.remove(old);
            }
        }
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
            if (count < 0) {
                this.searchCountText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            } else if (count == 0) {
                this.searchCountText.setText(LocaleController.getString("NoResult", R.string.NoResult));
            } else {
                this.searchCountText.setText(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(num + 1), Integer.valueOf(count)));
            }
        }
    }

    public boolean needDelayOpenAnimation() {
        return this.firstLoading;
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad, NotificationCenter.userInfoDidLoad});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        if (isOpen) {
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        if (isOpen) {
            this.openAnimationEnded = true;
            if (VERSION.SDK_INT >= 21) {
                createChatAttachView();
            }
            if (this.chatActivityEnterView.hasRecordVideo() && !this.chatActivityEnterView.isSendButtonVisible()) {
                boolean isChannel = false;
                if (this.currentChat != null) {
                    if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                        isChannel = false;
                    } else {
                        isChannel = true;
                    }
                }
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                String key = isChannel ? "needShowRoundHintChannel" : "needShowRoundHint";
                if (preferences.getBoolean(key, true) && Utilities.random.nextFloat() < 0.2f) {
                    showVoiceHint(false, this.chatActivityEnterView.isInVideoMode());
                    preferences.edit().putBoolean(key, false).commit();
                }
            }
            if (!backward && this.parentLayout != null) {
                int N = this.parentLayout.fragmentsStack.size() - 1;
                for (int a = 0; a < N; a++) {
                    BaseFragment fragment = (BaseFragment) this.parentLayout.fragmentsStack.get(a);
                    if (fragment != this && (fragment instanceof ChatActivity) && ((ChatActivity) fragment).dialog_id == this.dialog_id) {
                        fragment.removeSelfFromStack();
                        return;
                    }
                }
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.closeChatDialog != null && dialog == this.closeChatDialog) {
            MessagesController.getInstance(this.currentAccount).deleteDialog(this.dialog_id, 0);
            if (this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
                lambda$checkDiscard$2$PollCreateActivity();
                return;
            }
            BaseFragment fragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);
            removeSelfFromStack();
            fragment.lambda$checkDiscard$2$PollCreateActivity();
        }
    }

    public boolean extendActionMode(Menu menu) {
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible() ? PhotoViewer.getInstance().getSelectiongLength() == 0 || menu.findItem(16908321) == null : this.chatActivityEnterView.getSelectionLength() == 0 || menu.findItem(16908321) == null) {
            if (VERSION.SDK_INT >= 23) {
                menu.removeItem(16908341);
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(LocaleController.getString("Bold", R.string.Bold));
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, stringBuilder.length(), 33);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_bold, 6, stringBuilder);
            stringBuilder = new SpannableStringBuilder(LocaleController.getString("Italic", R.string.Italic));
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), 0, stringBuilder.length(), 33);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_italic, 7, stringBuilder);
            stringBuilder = new SpannableStringBuilder(LocaleController.getString("Mono", R.string.Mono));
            stringBuilder.setSpan(new TypefaceSpan(Typeface.MONOSPACE), 0, stringBuilder.length(), 33);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_mono, 8, stringBuilder);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_link, 9, LocaleController.getString("CreateLink", R.string.CreateLink));
            menu.add(R.id.menu_groupbolditalic, R.id.menu_regular, 10, LocaleController.getString("Regular", R.string.Regular));
        }
        return true;
    }

    private void updateBottomOverlay() {
        if (this.bottomOverlayChatText != null) {
            if (this.currentChat == null) {
                showBottomOverlayProgress(false, false);
                if (this.userBlocked) {
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
                                hideFieldPanel();
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
            } else if (!ChatObject.isChannel(this.currentChat) || (this.currentChat instanceof TL_channelForbidden)) {
                this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisGroup", R.string.DeleteThisGroup));
            } else if (!ChatObject.isNotInChat(this.currentChat)) {
                boolean z;
                if (MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelUnmute", R.string.ChannelUnmute));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelMute", R.string.ChannelMute));
                }
                if (this.bottomOverlayProgress.getTag() != null) {
                    z = true;
                } else {
                    z = false;
                }
                showBottomOverlayProgress(false, z);
            } else if (MessagesController.getInstance(this.currentAccount).isJoiningChannel(this.currentChat.var_id)) {
                showBottomOverlayProgress(true, false);
            } else {
                this.bottomOverlayChatText.setText(LocaleController.getString("ChannelJoin", R.string.ChannelJoin));
                showBottomOverlayProgress(false, false);
            }
            if (this.inPreviewMode) {
                this.searchContainer.setVisibility(4);
                this.bottomOverlayChat.setVisibility(4);
                this.chatActivityEnterView.setFieldFocused(false);
                this.chatActivityEnterView.setVisibility(4);
            } else if (this.searchItem == null || this.searchItem.getVisibility() != 0) {
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
                    if (this.chatActivityEnterView.isEditingMessage()) {
                        this.chatActivityEnterView.setVisibility(0);
                        this.bottomOverlayChat.setVisibility(4);
                        this.chatActivityEnterView.setFieldFocused();
                        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$43(this), 100);
                    } else {
                        this.bottomOverlayChat.setVisibility(0);
                        this.chatActivityEnterView.setFieldFocused(false);
                        this.chatActivityEnterView.setVisibility(4);
                        this.chatActivityEnterView.closeKeyboard();
                    }
                    if (this.muteItem != null) {
                        this.muteItem.setVisibility(8);
                    }
                    this.attachItem.setVisibility(8);
                    this.editTextItem.setVisibility(8);
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

    final /* synthetic */ void lambda$updateBottomOverlay$56$ChatActivity() {
        this.chatActivityEnterView.openKeyboard();
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
                this.alertViewAnimator.addListener(new CLASSNAME());
                this.alertViewAnimator.start();
            }
            this.alertNameTextView.setText(name);
            this.alertTextView.setText(Emoji.replaceEmoji(message.replace(10, ' '), this.alertTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
            if (this.hideAlertViewRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.hideAlertViewRunnable);
            }
            Runnable CLASSNAME = new CLASSNAME();
            this.hideAlertViewRunnable = CLASSNAME;
            AndroidUtilities.runOnUIThread(CLASSNAME, 3000);
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
                animatorArr[0] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", new float[]{(float) (-AndroidUtilities.m9dp(50.0f))});
                animatorSet.playTogether(animatorArr);
                this.pinnedMessageViewAnimator.setDuration(200);
                this.pinnedMessageViewAnimator.addListener(new CLASSNAME());
                this.pinnedMessageViewAnimator.start();
                return;
            }
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.m9dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
        }
    }

    private void updatePinnedMessageView(boolean animated) {
        if (this.pinnedMessageView != null) {
            int pinned_msg_id;
            if (this.chatInfo != null) {
                if (!(this.pinnedMessageObject == null || this.chatInfo.pinned_msg_id == this.pinnedMessageObject.getId())) {
                    this.pinnedMessageObject = null;
                }
                if (this.chatInfo.pinned_msg_id != 0 && this.pinnedMessageObject == null) {
                    this.pinnedMessageObject = (MessageObject) this.messagesDict[0].get(this.chatInfo.pinned_msg_id);
                }
                pinned_msg_id = this.chatInfo.pinned_msg_id;
            } else if (this.userInfo != null) {
                if (!(this.pinnedMessageObject == null || this.userInfo.pinned_msg_id == this.pinnedMessageObject.getId())) {
                    this.pinnedMessageObject = null;
                }
                if (this.userInfo.pinned_msg_id != 0 && this.pinnedMessageObject == null) {
                    this.pinnedMessageObject = (MessageObject) this.messagesDict[0].get(this.userInfo.pinned_msg_id);
                }
                pinned_msg_id = this.userInfo.pinned_msg_id;
            } else {
                pinned_msg_id = 0;
            }
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            if ((this.chatInfo == null && this.userInfo == null) || pinned_msg_id == 0 || pinned_msg_id == preferences.getInt("pin_" + this.dialog_id, 0) || (this.actionBar != null && (this.actionBar.isActionModeShowed() || this.actionBar.isSearchFieldVisible()))) {
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
                        this.pinnedMessageViewAnimator.addListener(new CLASSNAME());
                        this.pinnedMessageViewAnimator.start();
                    } else {
                        this.pinnedMessageView.setTranslationY(0.0f);
                        this.pinnedMessageView.setVisibility(0);
                    }
                }
                FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.pinnedMessageNameTextView.getLayoutParams();
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.pinnedMessageTextView.getLayoutParams();
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs2, AndroidUtilities.m9dp(50.0f));
                if (photoSize == null) {
                    photoSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs, AndroidUtilities.m9dp(50.0f));
                }
                int dp;
                if (photoSize == null || (photoSize instanceof TL_photoSizeEmpty) || (photoSize.location instanceof TL_fileLocationUnavailable) || this.pinnedMessageObject.type == 13) {
                    this.pinnedMessageImageView.setImageBitmap(null);
                    this.pinnedImageLocation = null;
                    this.pinnedMessageImageView.setVisibility(4);
                    dp = AndroidUtilities.m9dp(18.0f);
                    layoutParams2.leftMargin = dp;
                    layoutParams1.leftMargin = dp;
                } else {
                    if (this.pinnedMessageObject.isRoundVideo()) {
                        this.pinnedMessageImageView.setRoundRadius(AndroidUtilities.m9dp(16.0f));
                    } else {
                        this.pinnedMessageImageView.setRoundRadius(0);
                    }
                    this.pinnedImageLocation = photoSize.location;
                    this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable) null, this.pinnedMessageObject);
                    this.pinnedMessageImageView.setVisibility(0);
                    dp = AndroidUtilities.m9dp(55.0f);
                    layoutParams2.leftMargin = dp;
                    layoutParams1.leftMargin = dp;
                }
                this.pinnedMessageNameTextView.setLayoutParams(layoutParams1);
                this.pinnedMessageTextView.setLayoutParams(layoutParams2);
                if (this.pinnedMessageObject.type == 17) {
                    this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedPoll", R.string.PinnedPoll));
                } else {
                    this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedMessage", R.string.PinnedMessage));
                }
                if (this.pinnedMessageObject.type == 14) {
                    this.pinnedMessageTextView.setText(String.format("%s - %s", new Object[]{this.pinnedMessageObject.getMusicAuthor(), this.pinnedMessageObject.getMusicTitle()}));
                } else if (this.pinnedMessageObject.type == 17) {
                    this.pinnedMessageTextView.setText(this.pinnedMessageObject.messageOwner.media.poll.question);
                } else if (this.pinnedMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(this.pinnedMessageObject.messageOwner.media.game.title, this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
                } else if (this.pinnedMessageObject.messageText != null) {
                    String mess = this.pinnedMessageObject.messageText.toString();
                    if (mess.length() > 150) {
                        mess = mess.substring(0, 150);
                    }
                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(mess.replace(10, ' '), this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(14.0f), false));
                }
            } else {
                this.pinnedImageLocation = null;
                hidePinnedMessageView(animated);
                if (this.loadingPinnedMessage != pinned_msg_id) {
                    this.loadingPinnedMessage = pinned_msg_id;
                    DataQuery.getInstance(this.currentAccount).loadPinnedMessage(this.dialog_id, ChatObject.isChannel(this.currentChat) ? this.currentChat.var_id : 0, pinned_msg_id, true);
                }
            }
            checkListViewPaddings();
        }
    }

    private void updateSpamView() {
        if (this.reportSpamView != null) {
            boolean show;
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            if (this.currentEncryptedChat != null) {
                if (this.currentEncryptedChat.admin_id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).isLoadingContacts() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.var_id)) != null) {
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
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m10d("show spam button");
                    }
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
                    this.reportSpamViewAnimator.addListener(new CLASSNAME());
                    this.reportSpamViewAnimator.start();
                }
            } else if (this.reportSpamView.getTag() == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("hide spam button");
                }
                this.reportSpamView.setTag(Integer.valueOf(1));
                if (this.reportSpamViewAnimator != null) {
                    this.reportSpamViewAnimator.cancel();
                }
                this.reportSpamViewAnimator = new AnimatorSet();
                animatorSet = this.reportSpamViewAnimator;
                animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", new float[]{(float) (-AndroidUtilities.m9dp(50.0f))});
                animatorSet.playTogether(animatorArr);
                this.reportSpamViewAnimator.setDuration(200);
                this.reportSpamViewAnimator.addListener(new CLASSNAME());
                this.reportSpamViewAnimator.start();
            }
            checkListViewPaddings();
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.m10d("no spam view found");
        }
    }

    private void updateContactStatus() {
        if (this.addContactItem != null) {
            if (this.currentUser == null) {
                this.addContactItem.setVisibility(8);
            } else {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.var_id));
                if (user != null) {
                    this.currentUser = user;
                }
                if ((this.currentEncryptedChat != null && !(this.currentEncryptedChat instanceof TL_encryptedChat)) || MessagesController.isSupportId(this.currentUser.var_id) || UserObject.isDeleted(this.currentUser) || ContactsController.getInstance(this.currentAccount).isLoadingContacts() || (!TextUtils.isEmpty(this.currentUser.phone) && ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.var_id)) != null && (ContactsController.getInstance(this.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(this.currentAccount).isLoadingContacts()))) {
                    this.addContactItem.setVisibility(8);
                } else {
                    this.addContactItem.setVisibility(0);
                    if (TextUtils.isEmpty(this.currentUser.phone)) {
                        this.addContactItem.setText(LocaleController.getString("ShareMyContactInfo", R.string.ShareMyContactInfo));
                        this.addToContactsButton.setVisibility(8);
                        this.reportSpamButton.setPadding(AndroidUtilities.m9dp(50.0f), 0, AndroidUtilities.m9dp(50.0f), 0);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 1.0f, 51, 0, 0, 0, AndroidUtilities.m9dp(1.0f)));
                    } else {
                        this.addContactItem.setText(LocaleController.getString("AddToContacts", R.string.AddToContacts));
                        this.reportSpamButton.setPadding(AndroidUtilities.m9dp(4.0f), 0, AndroidUtilities.m9dp(50.0f), 0);
                        this.addToContactsButton.setVisibility(0);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 0.5f, 51, 0, 0, 0, AndroidUtilities.m9dp(1.0f)));
                    }
                }
            }
            checkListViewPaddings();
        }
    }

    private void checkListViewPaddingsInternal() {
        if (this.chatLayoutManager != null) {
            try {
                int firstVisPos = this.chatLayoutManager.findFirstVisibleItemPosition();
                int lastVisPos = -1;
                if (!(this.wasManualScroll || this.unreadMessageObject == null)) {
                    int pos = this.messages.indexOf(this.unreadMessageObject);
                    if (pos >= 0) {
                        lastVisPos = pos + this.chatAdapter.messagesStartRow;
                        firstVisPos = -1;
                    }
                }
                int top = 0;
                if (firstVisPos != -1) {
                    View firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
                    top = firstVisView == null ? 0 : (this.chatListView.getMeasuredHeight() - firstVisView.getBottom()) - this.chatListView.getPaddingBottom();
                }
                FrameLayout.LayoutParams layoutParams;
                if (this.chatListView.getPaddingTop() != AndroidUtilities.m9dp(52.0f) && ((this.pinnedMessageView != null && this.pinnedMessageView.getTag() == null) || (this.reportSpamView != null && this.reportSpamView.getTag() == null))) {
                    this.chatListView.setPadding(0, AndroidUtilities.m9dp(52.0f), 0, AndroidUtilities.m9dp(3.0f));
                    layoutParams = (FrameLayout.LayoutParams) this.floatingDateView.getLayoutParams();
                    layoutParams.topMargin = AndroidUtilities.m9dp(52.0f);
                    this.floatingDateView.setLayoutParams(layoutParams);
                    this.chatListView.setTopGlowOffset(AndroidUtilities.m9dp(48.0f));
                } else if (this.chatListView.getPaddingTop() == AndroidUtilities.m9dp(4.0f) || ((this.pinnedMessageView != null && this.pinnedMessageView.getTag() == null) || (this.reportSpamView != null && this.reportSpamView.getTag() == null))) {
                    firstVisPos = -1;
                } else {
                    this.chatListView.setPadding(0, AndroidUtilities.m9dp(4.0f), 0, AndroidUtilities.m9dp(3.0f));
                    layoutParams = (FrameLayout.LayoutParams) this.floatingDateView.getLayoutParams();
                    layoutParams.topMargin = AndroidUtilities.m9dp(4.0f);
                    this.floatingDateView.setLayoutParams(layoutParams);
                    this.chatListView.setTopGlowOffset(0);
                }
                if (firstVisPos != -1) {
                    this.chatLayoutManager.scrollToPositionWithOffset(firstVisPos, top);
                } else if (lastVisPos != -1) {
                    this.chatLayoutManager.scrollToPositionWithOffset(lastVisPos, ((this.chatListView.getMeasuredHeight() - this.chatListView.getPaddingBottom()) - this.chatListView.getPaddingTop()) - AndroidUtilities.m9dp(29.0f));
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    private void checkListViewPaddings() {
        if (this.wasManualScroll || this.unreadMessageObject == null) {
            AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$44(this));
        } else if (this.messages.indexOf(this.unreadMessageObject) >= 0) {
            this.fixPaddingsInLayout = true;
            if (this.fragmentView != null) {
                this.fragmentView.requestLayout();
            }
        }
    }

    private void checkRaiseSensors() {
        if (this.chatActivityEnterView != null && this.chatActivityEnterView.isStickersExpanded()) {
            MediaController.getInstance().setAllowStartRecord(false);
        } else if (ChatObject.isChannel(this.currentChat) && this.currentChat.banned_rights != null && this.currentChat.banned_rights.send_media) {
            MediaController.getInstance().setAllowStartRecord(false);
        } else if (ApplicationLoader.mainInterfacePaused || ((this.bottomOverlayChat != null && this.bottomOverlayChat.getVisibility() == 0) || ((this.bottomOverlay != null && this.bottomOverlay.getVisibility() == 0) || (this.searchContainer != null && this.searchContainer.getVisibility() == 0)))) {
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

    protected void setInPreviewMode(boolean value) {
        super.setInPreviewMode(value);
        if (this.avatarContainer != null) {
            this.avatarContainer.setOccupyStatusBar(!value);
            this.avatarContainer.setLayoutParams(LayoutHelper.createFrame(-2, -1.0f, 51, !value ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setVisibility(!value ? 0 : 4);
        }
        if (this.actionBar != null) {
            this.actionBar.setBackButtonDrawable(!value ? new BackDrawable(false) : null);
            this.headerItem.setAlpha(!value ? 1.0f : 0.0f);
            this.attachItem.setAlpha(!value ? 1.0f : 0.0f);
        }
        if (this.chatListView != null) {
            int count = this.chatListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                MessageObject message = null;
                if (view instanceof ChatMessageCell) {
                    message = ((ChatMessageCell) view).getMessageObject();
                } else if (view instanceof ChatActionCell) {
                    message = ((ChatActionCell) view).getMessageObject();
                }
                if (message != null && message.messageOwner != null && message.messageOwner.media_unread && message.messageOwner.mentioned) {
                    if (!(message.isVoice() || message.isRoundVideo())) {
                        this.newMentionsCount--;
                        if (this.newMentionsCount <= 0) {
                            this.newMentionsCount = 0;
                            this.hasAllMentionsLocal = true;
                            showMentiondownButton(false, true);
                        } else {
                            this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.newMentionsCount)}));
                        }
                        MessagesController.getInstance(this.currentAccount).markMentionMessageAsRead(message.getId(), ChatObject.isChannel(this.currentChat) ? this.currentChat.var_id : 0, this.dialog_id);
                        message.setContentIsRead();
                    }
                    if (view instanceof ChatMessageCell) {
                        ((ChatMessageCell) view).setHighlighted(false);
                        ((ChatMessageCell) view).setHighlightedAnimated();
                    }
                }
            }
        }
        updateBottomOverlay();
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        MediaController.getInstance().startRaiseToEarSensors(this);
        checkRaiseSensors();
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onResume();
        }
        if (this.firstOpen && MessagesController.getInstance(this.currentAccount).isProxyDialog(this.dialog_id)) {
            SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
            if (preferences.getLong("proxychannel", 0) != this.dialog_id) {
                preferences.edit().putLong("proxychannel", this.dialog_id).commit();
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("UseProxySponsorInfo", R.string.UseProxySponsorInfo));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
            }
        }
        checkActionBarMenu();
        if (!(this.replyImageLocation == null || this.replyImageView == null)) {
            this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null, this.replyingMessageObject);
        }
        if (!(this.pinnedImageLocation == null || this.pinnedMessageImageView == null)) {
            this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable) null, this.pinnedMessageObject);
        }
        NotificationsController.getInstance(this.currentAccount).setOpenedDialogId(this.dialog_id);
        MessagesController.getInstance(this.currentAccount).setLastVisibleDialogId(this.dialog_id, true);
        if (this.scrollToTopOnResume) {
            if (!this.scrollToTopUnReadOnResume || this.scrollToMessage == null) {
                moveScrollToLastMessage();
            } else if (this.chatListView != null) {
                int yOffset;
                boolean bottom = true;
                if (this.scrollToMessagePosition == -9000) {
                    yOffset = getScrollOffsetForMessage(this.scrollToMessage);
                    bottom = false;
                } else if (this.scrollToMessagePosition == -10000) {
                    yOffset = -AndroidUtilities.m9dp(11.0f);
                    bottom = false;
                } else {
                    yOffset = this.scrollToMessagePosition;
                }
                this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.messagesStartRow + this.messages.indexOf(this.scrollToMessage), yOffset, bottom);
            }
            this.scrollToTopUnReadOnResume = false;
            this.scrollToTopOnResume = false;
            this.scrollToMessage = null;
        }
        this.paused = false;
        this.pausedOnLastMessage = false;
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
        fixLayout();
        applyDraftMaybe(false);
        if (!(this.bottomOverlayChat == null || this.bottomOverlayChat.getVisibility() == 0 || this.actionBar.isSearchFieldVisible())) {
            this.chatActivityEnterView.setFieldFocused(true);
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onResume();
        }
        if (this.currentUser != null) {
            this.chatEnterTime = System.currentTimeMillis();
            this.chatLeaveTime = 0;
        }
        if (this.startVideoEdit != null) {
            AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$45(this));
        }
        if (this.chatListView != null && (this.chatActivityEnterView == null || !this.chatActivityEnterView.isEditingMessage())) {
            this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
            this.chatListView.setOnItemClickListener(this.onItemClickListener);
            this.chatListView.setLongClickable(true);
        }
        checkBotCommands();
    }

    final /* synthetic */ void lambda$onResume$57$ChatActivity() {
        openVideoEditor(this.startVideoEdit, null);
        this.startVideoEdit = null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:78:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x01bb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPause() {
        boolean z;
        super.onPause();
        MessagesController.getInstance(this.currentAccount).markDialogAsReadNow(this.dialog_id);
        MediaController.getInstance().stopRaiseToEarSensors(this, true);
        this.paused = true;
        this.wasPaused = true;
        NotificationsController.getInstance(this.currentAccount).setOpenedDialogId(0);
        MessagesController.getInstance(this.currentAccount).setLastVisibleDialogId(this.dialog_id, false);
        CharSequence draftMessage = null;
        MessageObject replyMessage = null;
        boolean searchWebpage = true;
        if (!(this.ignoreAttachOnPause || this.chatActivityEnterView == null || this.bottomOverlayChat.getVisibility() == 0)) {
            this.chatActivityEnterView.onPause();
            replyMessage = this.replyingMessageObject;
            if (!this.chatActivityEnterView.isEditingMessage()) {
                CharSequence text = AndroidUtilities.getTrimmedString(this.chatActivityEnterView.getFieldText());
                if (!TextUtils.isEmpty(text)) {
                    if (!TextUtils.equals(text, "@gif")) {
                        draftMessage = text;
                    }
                }
            }
            searchWebpage = this.chatActivityEnterView.isMessageWebPageSearchEnabled();
            this.chatActivityEnterView.setFieldFocused(false);
        }
        if (this.chatAttachAlert != null) {
            if (this.ignoreAttachOnPause) {
                this.ignoreAttachOnPause = false;
            } else {
                this.chatAttachAlert.onPause();
            }
        }
        CharSequence[] message = new CharSequence[]{draftMessage};
        ArrayList<MessageEntity> entities = DataQuery.getInstance(this.currentAccount).getEntities(message);
        DataQuery instance = DataQuery.getInstance(this.currentAccount);
        long j = this.dialog_id;
        CharSequence charSequence = message[0];
        Message message2 = replyMessage != null ? replyMessage.messageOwner : null;
        if (searchWebpage) {
            z = false;
        } else {
            z = true;
        }
        instance.saveDraft(j, charSequence, entities, message2, z);
        MessagesController.getInstance(this.currentAccount).cancelTyping(0, this.dialog_id);
        if (!this.pausedOnLastMessage) {
            Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            int messageId = 0;
            int offset = 0;
            if (this.chatLayoutManager != null) {
                int position = this.chatLayoutManager.findFirstVisibleItemPosition();
                if (position != 0) {
                    Holder holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(position);
                    if (holder != null) {
                        int mid = 0;
                        if (holder.itemView instanceof ChatMessageCell) {
                            mid = ((ChatMessageCell) holder.itemView).getMessageObject().getId();
                        } else if (holder.itemView instanceof ChatActionCell) {
                            mid = ((ChatActionCell) holder.itemView).getMessageObject().getId();
                        }
                        if (mid == 0) {
                            holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(position + 1);
                        }
                        boolean ignore = false;
                        for (int a = position - 1; a >= this.chatAdapter.messagesStartRow; a--) {
                            int num = a - this.chatAdapter.messagesStartRow;
                            if (num >= 0 && num < this.messages.size()) {
                                MessageObject messageObject = (MessageObject) this.messages.get(num);
                                if (messageObject.getId() != 0) {
                                    if (!messageObject.isOut() && messageObject.isUnread()) {
                                        ignore = true;
                                        messageId = 0;
                                    }
                                    if (!(holder == null || ignore)) {
                                        if (!(holder.itemView instanceof ChatMessageCell)) {
                                            messageId = ((ChatMessageCell) holder.itemView).getMessageObject().getId();
                                        } else if (holder.itemView instanceof ChatActionCell) {
                                            messageId = ((ChatActionCell) holder.itemView).getMessageObject().getId();
                                        }
                                        if ((messageId > 0 || this.currentEncryptedChat != null) && (messageId >= 0 || this.currentEncryptedChat == null)) {
                                            messageId = 0;
                                        } else {
                                            offset = holder.itemView.getBottom() - this.chatListView.getMeasuredHeight();
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m10d("save offset = " + offset + " for mid " + messageId);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!(holder.itemView instanceof ChatMessageCell)) {
                        }
                        if (messageId > 0) {
                        }
                        messageId = 0;
                    }
                }
            }
            if (messageId != 0) {
                editor.putInt("diditem" + this.dialog_id, messageId);
                editor.putInt("diditemo" + this.dialog_id, offset);
            } else {
                this.pausedOnLastMessage = true;
                editor.remove("diditem" + this.dialog_id);
                editor.remove("diditemo" + this.dialog_id);
            }
            editor.commit();
        }
        if (this.currentUser != null) {
            this.chatLeaveTime = System.currentTimeMillis();
            updateInformationForScreenshotDetector();
        }
    }

    private void applyDraftMaybe(boolean canClear) {
        if (this.chatActivityEnterView != null) {
            DraftMessage draftMessage = DataQuery.getInstance(this.currentAccount).getDraft(this.dialog_id);
            Message draftReplyMessage = (draftMessage == null || draftMessage.reply_to_msg_id == 0) ? null : DataQuery.getInstance(this.currentAccount).getDraftMessage(this.dialog_id);
            if (this.chatActivityEnterView.getFieldText() == null) {
                if (draftMessage != null) {
                    CharSequence message;
                    this.chatActivityEnterView.setWebPage(null, !draftMessage.no_webpage);
                    if (draftMessage.entities.isEmpty()) {
                        message = draftMessage.message;
                    } else {
                        SpannableStringBuilder stringBuilder = SpannableStringBuilder.valueOf(draftMessage.message);
                        DataQuery.sortEntities(draftMessage.entities);
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
                                stringBuilder.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + user_id, 1), entity.offset + addToOffset, (entity.offset + addToOffset) + entity.length, 33);
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
                            } else if (entity instanceof TL_messageEntityTextUrl) {
                                stringBuilder.setSpan(new URLSpanReplacement(entity.url), entity.offset + addToOffset, (entity.offset + entity.length) + addToOffset, 33);
                            }
                        }
                        message = stringBuilder;
                    }
                    this.chatActivityEnterView.setFieldText(message);
                    if (getArguments().getBoolean("hasUrl", false)) {
                        this.chatActivityEnterView.setSelection(draftMessage.message.indexOf(10) + 1);
                        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$46(this), 700);
                    }
                }
            } else if (canClear && draftMessage == null) {
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                hideFieldPanel();
            }
            if (this.replyingMessageObject == null && draftReplyMessage != null) {
                this.replyingMessageObject = new MessageObject(this.currentAccount, draftReplyMessage, MessagesController.getInstance(this.currentAccount).getUsers(), false);
                showFieldPanelForReply(true, this.replyingMessageObject);
            }
        }
    }

    final /* synthetic */ void lambda$applyDraftMaybe$58$ChatActivity() {
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setFieldFocused(true);
            this.chatActivityEnterView.openKeyboard();
        }
    }

    private void updateInformationForScreenshotDetector() {
        if (this.currentUser != null) {
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
                MediaController.getInstance().setLastVisibleMessageIds(this.currentAccount, this.chatEnterTime, this.chatLeaveTime, this.currentUser, this.currentEncryptedChat, visibleMessages, 0);
                return;
            }
            SecretMediaViewer viewer = SecretMediaViewer.getInstance();
            MessageObject messageObject = viewer.getCurrentMessageObject();
            if (messageObject != null && !messageObject.isOut()) {
                MediaController.getInstance().setLastVisibleMessageIds(this.currentAccount, viewer.getOpenTime(), viewer.getCloseTime(), this.currentUser, null, null, messageObject.getId());
            }
        }
    }

    private boolean fixLayoutInternal() {
        if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
            this.selectedMessagesCountTextView.setTextSize(20);
        } else {
            this.selectedMessagesCountTextView.setTextSize(18);
        }
        HashMap<Long, GroupedMessages> newGroups = null;
        int count = this.chatListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.chatListView.getChildAt(a);
            if (child instanceof ChatMessageCell) {
                GroupedMessages groupedMessages = ((ChatMessageCell) child).getCurrentMessagesGroup();
                if (groupedMessages != null && groupedMessages.hasSibling) {
                    if (newGroups == null) {
                        newGroups = new HashMap();
                    }
                    if (!newGroups.containsKey(Long.valueOf(groupedMessages.groupId))) {
                        newGroups.put(Long.valueOf(groupedMessages.groupId), groupedMessages);
                        int idx = this.messages.indexOf((MessageObject) groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                        if (idx >= 0) {
                            this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.messagesStartRow + idx, groupedMessages.messages.size());
                        }
                    }
                }
            }
        }
        if (!AndroidUtilities.isTablet()) {
            return true;
        }
        if (AndroidUtilities.isSmallTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 1) {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
            if (this.fragmentContextView != null && this.fragmentContextView.getParent() == null) {
                ((ViewGroup) this.fragmentView).addView(this.fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            }
        } else {
            CLASSNAMEActionBar CLASSNAMEActionBar = this.actionBar;
            boolean z = this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(0) == this || this.parentLayout.fragmentsStack.size() == 1;
            CLASSNAMEActionBar.setBackButtonDrawable(new BackDrawable(z));
            if (!(this.fragmentContextView == null || this.fragmentContextView.getParent() == null)) {
                this.fragmentView.setPadding(0, 0, 0, 0);
                ((ViewGroup) this.fragmentView).removeView(this.fragmentContextView);
            }
        }
        return false;
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new CLASSNAME());
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        fixLayout();
        if (this.visibleDialog instanceof DatePickerDialog) {
            this.visibleDialog.dismiss();
        }
    }

    private void createDeleteMessagesAlert(MessageObject finalSelectedObject, GroupedMessages selectedGroup) {
        createDeleteMessagesAlert(finalSelectedObject, selectedGroup, 1);
    }

    private void createDeleteMessagesAlert(MessageObject finalSelectedObject, GroupedMessages finalSelectedGroup, int loadParticipant) {
        if (getParentActivity() != null) {
            int count;
            Builder builder = new Builder(getParentActivity());
            if (finalSelectedGroup != null) {
                count = finalSelectedGroup.messages.size();
            } else if (finalSelectedObject != null) {
                count = 1;
            } else {
                count = this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size();
            }
            builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", R.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("messages", count)));
            builder.setTitle(LocaleController.getString("Message", R.string.Message));
            boolean[] checks = new boolean[3];
            boolean[] deleteForAll = new boolean[1];
            User user = null;
            boolean canRevokeInbox = this.currentUser != null && MessagesController.getInstance(this.currentAccount).canRevokePmInbox;
            int revokeTimeLimit;
            if (this.currentUser != null) {
                revokeTimeLimit = MessagesController.getInstance(this.currentAccount).revokeTimePmLimit;
            } else {
                revokeTimeLimit = MessagesController.getInstance(this.currentAccount).revokeTimeLimit;
            }
            boolean hasOutgoing;
            int currentDate;
            int a;
            int b;
            MessageObject msg;
            boolean exit;
            View frameLayout;
            if (this.currentChat != null && this.currentChat.megagroup) {
                hasOutgoing = false;
                boolean canBan = ChatObject.canBlockUsers(this.currentChat);
                currentDate = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if (finalSelectedObject != null) {
                    if (finalSelectedObject.messageOwner.action == null || (finalSelectedObject.messageOwner.action instanceof TL_messageActionEmpty) || (finalSelectedObject.messageOwner.action instanceof TL_messageActionChatDeleteUser)) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(finalSelectedObject.messageOwner.from_id));
                    }
                    if (!finalSelectedObject.isSendError() && finalSelectedObject.getDialogId() == this.mergeDialogId && ((finalSelectedObject.messageOwner.action == null || (finalSelectedObject.messageOwner.action instanceof TL_messageActionEmpty)) && finalSelectedObject.isOut() && currentDate - finalSelectedObject.messageOwner.date <= revokeTimeLimit)) {
                        hasOutgoing = true;
                    } else {
                        hasOutgoing = false;
                    }
                } else {
                    int from_id = -1;
                    for (a = 1; a >= 0; a--) {
                        for (b = 0; b < this.selectedMessagesIds[a].size(); b++) {
                            msg = (MessageObject) this.selectedMessagesIds[a].valueAt(b);
                            if (from_id == -1) {
                                from_id = msg.messageOwner.from_id;
                            }
                            if (from_id < 0 || from_id != msg.messageOwner.from_id) {
                                from_id = -2;
                                break;
                            }
                        }
                        if (from_id == -2) {
                            break;
                        }
                    }
                    exit = false;
                    for (a = 1; a >= 0; a--) {
                        for (b = 0; b < this.selectedMessagesIds[a].size(); b++) {
                            msg = (MessageObject) this.selectedMessagesIds[a].valueAt(b);
                            if (a != 1) {
                                if (a == 0 && !msg.isOut()) {
                                    hasOutgoing = false;
                                    exit = true;
                                    break;
                                }
                            } else if (!msg.isOut() || msg.messageOwner.action != null) {
                                hasOutgoing = false;
                                exit = true;
                                break;
                            } else if (currentDate - msg.messageOwner.date <= 172800) {
                                hasOutgoing = true;
                            }
                        }
                        if (exit) {
                            break;
                        }
                    }
                    if (from_id != -1) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(from_id));
                    }
                }
                if (user == null || user.var_id == UserConfig.getInstance(this.currentAccount).getClientUserId() || loadParticipant == 2) {
                    if (hasOutgoing) {
                        frameLayout = new FrameLayout(getParentActivity());
                        frameLayout = new CheckBoxCell(getParentActivity(), 1);
                        frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        if (this.currentChat != null) {
                            frameLayout.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                        } else {
                            frameLayout.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(this.currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                        }
                        frameLayout.setPadding(LocaleController.isRTL ? AndroidUtilities.m9dp(16.0f) : AndroidUtilities.m9dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.m9dp(8.0f) : AndroidUtilities.m9dp(16.0f), 0);
                        frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                        frameLayout.setOnClickListener(new ChatActivity$$Lambda$50(deleteForAll));
                        builder.setView(frameLayout);
                    } else {
                        user = null;
                    }
                } else if (loadParticipant != 1 || this.currentChat.creator) {
                    frameLayout = new FrameLayout(getParentActivity());
                    int num = 0;
                    a = 0;
                    while (a < 3) {
                        if (canBan || a != 0) {
                            int dp;
                            int dp2;
                            frameLayout = new CheckBoxCell(getParentActivity(), 1);
                            frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            frameLayout.setTag(Integer.valueOf(a));
                            if (a == 0) {
                                frameLayout.setText(LocaleController.getString("DeleteBanUser", R.string.DeleteBanUser), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                            } else if (a == 1) {
                                frameLayout.setText(LocaleController.getString("DeleteReportSpam", R.string.DeleteReportSpam), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                            } else if (a == 2) {
                                frameLayout.setText(LocaleController.formatString("DeleteAllFrom", R.string.DeleteAllFrom, ContactsController.formatName(user.first_name, user.last_name)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                            }
                            if (LocaleController.isRTL) {
                                dp = AndroidUtilities.m9dp(16.0f);
                            } else {
                                dp = AndroidUtilities.m9dp(8.0f);
                            }
                            if (LocaleController.isRTL) {
                                dp2 = AndroidUtilities.m9dp(8.0f);
                            } else {
                                dp2 = AndroidUtilities.m9dp(16.0f);
                            }
                            frameLayout.setPadding(dp, 0, dp2, 0);
                            frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) (num * 48), 0.0f, 0.0f));
                            frameLayout.setOnClickListener(new ChatActivity$$Lambda$49(checks));
                            num++;
                        }
                        a++;
                    }
                    builder.setView(frameLayout);
                } else {
                    AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(getParentActivity(), 3)};
                    TLObject req = new TL_channels_getParticipant();
                    req.channel = MessagesController.getInputChannel(this.currentChat);
                    req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$48(this, progressDialog, ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatActivity$$Lambda$47(this, progressDialog, finalSelectedObject, finalSelectedGroup))), 1000);
                    return;
                }
            } else if (!ChatObject.isChannel(this.currentChat) && this.currentEncryptedChat == null) {
                hasOutgoing = false;
                currentDate = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if ((this.currentUser != null && this.currentUser.var_id != UserConfig.getInstance(this.currentAccount).getClientUserId() && !this.currentUser.bot) || this.currentChat != null) {
                    if (finalSelectedObject == null) {
                        exit = false;
                        for (a = 1; a >= 0; a--) {
                            for (b = 0; b < this.selectedMessagesIds[a].size(); b++) {
                                msg = (MessageObject) this.selectedMessagesIds[a].valueAt(b);
                                if (msg.messageOwner.action == null) {
                                    if (!msg.isOut() && !canRevokeInbox && (this.currentChat == null || (!this.currentChat.creator && (!this.currentChat.admin || !this.currentChat.admins_enabled)))) {
                                        exit = true;
                                        hasOutgoing = false;
                                        break;
                                    } else if (!hasOutgoing && currentDate - msg.messageOwner.date <= revokeTimeLimit) {
                                        hasOutgoing = true;
                                    }
                                }
                            }
                            if (exit) {
                                break;
                            }
                        }
                    } else if (finalSelectedObject.isSendError() || (!(finalSelectedObject.messageOwner.action == null || (finalSelectedObject.messageOwner.action instanceof TL_messageActionEmpty)) || (!(finalSelectedObject.isOut() || canRevokeInbox || (this.currentChat != null && (this.currentChat.creator || (this.currentChat.admin && this.currentChat.admins_enabled)))) || currentDate - finalSelectedObject.messageOwner.date > revokeTimeLimit))) {
                        hasOutgoing = false;
                    } else {
                        hasOutgoing = true;
                    }
                }
                if (hasOutgoing) {
                    frameLayout = new FrameLayout(getParentActivity());
                    frameLayout = new CheckBoxCell(getParentActivity(), 1);
                    frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (this.currentChat != null) {
                        frameLayout.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    } else {
                        frameLayout.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(this.currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    }
                    frameLayout.setPadding(LocaleController.isRTL ? AndroidUtilities.m9dp(16.0f) : AndroidUtilities.m9dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.m9dp(8.0f) : AndroidUtilities.m9dp(16.0f), 0);
                    frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    frameLayout.setOnClickListener(new ChatActivity$$Lambda$51(deleteForAll));
                    builder.setView(frameLayout);
                }
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$52(this, finalSelectedObject, finalSelectedGroup, deleteForAll, user, checks));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$createDeleteMessagesAlert$60$ChatActivity(AlertDialog[] progressDialog, MessageObject finalSelectedObject, GroupedMessages finalSelectedGroup, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$78(this, progressDialog, response, finalSelectedObject, finalSelectedGroup));
    }

    final /* synthetic */ void lambda$null$59$ChatActivity(AlertDialog[] progressDialog, TLObject response, MessageObject finalSelectedObject, GroupedMessages finalSelectedGroup) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        int loadType = 2;
        if (response != null) {
            TL_channels_channelParticipant participant = (TL_channels_channelParticipant) response;
            if (!((participant.participant instanceof TL_channelParticipantAdmin) || (participant.participant instanceof TL_channelParticipantCreator))) {
                loadType = 0;
            }
        }
        createDeleteMessagesAlert(finalSelectedObject, finalSelectedGroup, loadType);
    }

    final /* synthetic */ void lambda$createDeleteMessagesAlert$62$ChatActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new ChatActivity$$Lambda$77(this, requestId));
            showDialog(progressDialog[0]);
        }
    }

    final /* synthetic */ void lambda$null$61$ChatActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    static final /* synthetic */ void lambda$createDeleteMessagesAlert$63$ChatActivity(boolean[] checks, View v) {
        if (v.isEnabled()) {
            CheckBoxCell cell13 = (CheckBoxCell) v;
            Integer num1 = (Integer) cell13.getTag();
            checks[num1.intValue()] = !checks[num1.intValue()];
            cell13.setChecked(checks[num1.intValue()], true);
        }
    }

    static final /* synthetic */ void lambda$createDeleteMessagesAlert$64$ChatActivity(boolean[] deleteForAll, View v) {
        boolean z;
        CheckBoxCell cell12 = (CheckBoxCell) v;
        if (deleteForAll[0]) {
            z = false;
        } else {
            z = true;
        }
        deleteForAll[0] = z;
        cell12.setChecked(deleteForAll[0], true);
    }

    static final /* synthetic */ void lambda$createDeleteMessagesAlert$65$ChatActivity(boolean[] deleteForAll, View v) {
        boolean z;
        CheckBoxCell cell1 = (CheckBoxCell) v;
        if (deleteForAll[0]) {
            z = false;
        } else {
            z = true;
        }
        deleteForAll[0] = z;
        cell1.setChecked(deleteForAll[0], true);
    }

    final /* synthetic */ void lambda$createDeleteMessagesAlert$67$ChatActivity(MessageObject finalSelectedObject, GroupedMessages finalSelectedGroup, boolean[] deleteForAll, User userFinal, boolean[] checks, DialogInterface dialogInterface, int i) {
        ArrayList<Integer> ids = null;
        ArrayList<Long> random_ids;
        int a;
        if (finalSelectedObject != null) {
            ids = new ArrayList();
            random_ids = null;
            if (finalSelectedGroup != null) {
                for (a = 0; a < finalSelectedGroup.messages.size(); a++) {
                    MessageObject messageObject = (MessageObject) finalSelectedGroup.messages.get(a);
                    ids.add(Integer.valueOf(messageObject.getId()));
                    if (!(this.currentEncryptedChat == null || messageObject.messageOwner.random_id == 0 || messageObject.type == 10)) {
                        if (random_ids == null) {
                            random_ids = new ArrayList();
                        }
                        random_ids.add(Long.valueOf(messageObject.messageOwner.random_id));
                    }
                }
            } else {
                ids.add(Integer.valueOf(finalSelectedObject.getId()));
                if (!(this.currentEncryptedChat == null || finalSelectedObject.messageOwner.random_id == 0 || finalSelectedObject.type == 10)) {
                    random_ids = new ArrayList();
                    random_ids.add(Long.valueOf(finalSelectedObject.messageOwner.random_id));
                }
            }
            MessagesController.getInstance(this.currentAccount).deleteMessages(ids, random_ids, this.currentEncryptedChat, finalSelectedObject.messageOwner.to_id.channel_id, deleteForAll[0]);
        } else {
            for (a = 1; a >= 0; a--) {
                int b;
                MessageObject msg;
                ids = new ArrayList();
                for (b = 0; b < this.selectedMessagesIds[a].size(); b++) {
                    ids.add(Integer.valueOf(this.selectedMessagesIds[a].keyAt(b)));
                }
                random_ids = null;
                int channelId = 0;
                if (!ids.isEmpty()) {
                    msg = (MessageObject) this.selectedMessagesIds[a].get(((Integer) ids.get(0)).intValue());
                    if (null == null && msg.messageOwner.to_id.channel_id != 0) {
                        channelId = msg.messageOwner.to_id.channel_id;
                    }
                }
                if (this.currentEncryptedChat != null) {
                    random_ids = new ArrayList();
                    for (b = 0; b < this.selectedMessagesIds[a].size(); b++) {
                        msg = (MessageObject) this.selectedMessagesIds[a].valueAt(b);
                        if (!(msg.messageOwner.random_id == 0 || msg.type == 10)) {
                            random_ids.add(Long.valueOf(msg.messageOwner.random_id));
                        }
                    }
                }
                MessagesController.getInstance(this.currentAccount).deleteMessages(ids, random_ids, this.currentEncryptedChat, channelId, deleteForAll[0]);
            }
            this.actionBar.hideActionMode();
            updatePinnedMessageView(true);
        }
        if (userFinal != null) {
            if (checks[0]) {
                MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.currentChat.var_id, userFinal, this.chatInfo);
            }
            if (checks[1]) {
                TL_channels_reportSpam req = new TL_channels_reportSpam();
                req.channel = MessagesController.getInputChannel(this.currentChat);
                req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(userFinal);
                req.var_id = ids;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, ChatActivity$$Lambda$76.$instance);
            }
            if (checks[2]) {
                MessagesController.getInstance(this.currentAccount).deleteUserChannelHistory(this.currentChat, userFinal, 0);
            }
        }
    }

    static final /* synthetic */ void lambda$null$66$ChatActivity(TLObject response, TL_error error) {
    }

    private void createMenu(View v, boolean single, boolean listView) {
        createMenu(v, single, listView, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:406:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0213  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createMenu(View v, boolean single, boolean listView, boolean searchGroup) {
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
                    scrollToMessageId(message.messageOwner.reply_to_msg_id, message.messageOwner.var_id, true, 0, false);
                    return;
                }
                int a;
                GroupedMessages groupedMessages;
                boolean allowEdit;
                this.selectedObject = null;
                this.selectedObjectGroup = null;
                this.forwardingMessage = null;
                this.forwardingMessageGroup = null;
                for (a = 1; a >= 0; a--) {
                    this.selectedMessagesCanCopyIds[a].clear();
                    this.selectedMessagesCanStarIds[a].clear();
                    this.selectedMessagesIds[a].clear();
                }
                this.cantDeleteMessagesCount = 0;
                this.canEditMessagesCount = 0;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                if (searchGroup) {
                    groupedMessages = getValidGroupedMessage(message);
                } else {
                    groupedMessages = null;
                }
                boolean allowChatActions = true;
                boolean allowPin = ChatObject.isChannel(this.currentChat) ? message.getDialogId() != this.mergeDialogId && (this.currentChat.creator || (this.currentChat.admin_rights != null && ((this.currentChat.megagroup && this.currentChat.admin_rights.pin_messages) || (!this.currentChat.megagroup && this.currentChat.admin_rights.edit_messages)))) : this.currentEncryptedChat == null ? this.userInfo != null ? this.userInfo.can_pin_message : this.currentChat != null ? this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled : false : false;
                allowPin = allowPin && message.getId() > 0 && (message.messageOwner.action == null || (message.messageOwner.action instanceof TL_messageActionEmpty));
                boolean allowUnpin = message.getDialogId() != this.mergeDialogId && allowPin && ((this.chatInfo != null && this.chatInfo.pinned_msg_id == message.getId()) || (this.userInfo != null && this.userInfo.pinned_msg_id == message.getId()));
                if (groupedMessages == null) {
                    if (!(!message.canEditMessage(this.currentChat) || this.chatActivityEnterView.hasAudioToSend() || message.getDialogId() == this.mergeDialogId)) {
                        allowEdit = true;
                        if ((this.currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46) || ((type == 1 && (message.getDialogId() == this.mergeDialogId || message.needDrawBluredPreview())) || (message.messageOwner.action instanceof TL_messageActionSecureValuesSent) || ((this.currentEncryptedChat == null && message.getId() < 0) || ((this.bottomOverlayChat != null && this.bottomOverlayChat.getVisibility() == 0) || this.isBroadcast || (this.currentChat != null && (ChatObject.isNotInChat(this.currentChat) || !((!ChatObject.isChannel(this.currentChat) || ChatObject.canPost(this.currentChat) || this.currentChat.megagroup) && ChatObject.canSendMessages(this.currentChat)))))))) {
                            allowChatActions = false;
                        }
                        if (single && type >= 2 && type != 20 && !message.needDrawBluredPreview() && !message.isLiveLocation()) {
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
                                view.setPivotY((float) (CLASSNAMEActionBar.getCurrentActionBarHeight() / 2));
                                AndroidUtilities.clearDrawableAnimation(view);
                                animators.add(ObjectAnimator.ofFloat(view, "scaleY", new float[]{0.1f, 1.0f}));
                            }
                            animatorSet.playTogether(animators);
                            animatorSet.setDuration(250);
                            animatorSet.start();
                            addToSelectedMessages(message, listView);
                            this.selectedMessagesCountTextView.setNumber(this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size(), false);
                            updateVisibleRows();
                            return;
                        } else if (getParentActivity() == null) {
                            ArrayList<CharSequence> items = new ArrayList();
                            ArrayList<Integer> options = new ArrayList();
                            if (type >= 0 || (type == -1 && single && ((message.isSending() || message.isEditing()) && this.currentEncryptedChat == null))) {
                                this.selectedObject = message;
                                this.selectedObjectGroup = groupedMessages;
                                User user;
                                if (type == -1) {
                                    items.add(LocaleController.getString("CancelSending", R.string.CancelSending));
                                    options.add(Integer.valueOf(24));
                                } else if (type == 0) {
                                    items.add(LocaleController.getString("Retry", R.string.Retry));
                                    options.add(Integer.valueOf(0));
                                    items.add(LocaleController.getString("Delete", R.string.Delete));
                                    options.add(Integer.valueOf(1));
                                } else if (type == 1) {
                                    if (this.currentChat == null || this.isBroadcast) {
                                        if (message.messageOwner.action != null && (message.messageOwner.action instanceof TL_messageActionPhoneCall)) {
                                            Object string;
                                            TL_messageActionPhoneCall call = (TL_messageActionPhoneCall) message.messageOwner.action;
                                            if (((call.reason instanceof TL_phoneCallDiscardReasonMissed) || (call.reason instanceof TL_phoneCallDiscardReasonBusy)) && !message.isOutOwner()) {
                                                string = LocaleController.getString("CallBack", R.string.CallBack);
                                            } else {
                                                string = LocaleController.getString("CallAgain", R.string.CallAgain);
                                            }
                                            items.add(string);
                                            options.add(Integer.valueOf(18));
                                            if (VoIPHelper.canRateCall(call)) {
                                                items.add(LocaleController.getString("CallMessageReportProblem", R.string.CallMessageReportProblem));
                                                options.add(Integer.valueOf(19));
                                            }
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
                                        if (this.selectedObject.contentType == 0 && !this.selectedObject.isMediaEmptyWebpage() && this.selectedObject.getId() > 0 && !this.selectedObject.isOut() && (this.currentChat != null || (this.currentUser != null && this.currentUser.bot))) {
                                            items.add(LocaleController.getString("ReportChat", R.string.ReportChat));
                                            options.add(Integer.valueOf(23));
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
                                    if (ChatObject.isChannel(this.currentChat) && this.currentChat.megagroup && !TextUtils.isEmpty(this.currentChat.username)) {
                                        items.add(LocaleController.getString("CopyLink", R.string.CopyLink));
                                        options.add(Integer.valueOf(22));
                                    }
                                    if (type == 2) {
                                        if (this.selectedObject.type == 17 && !message.isPollClosed()) {
                                            if (message.isVoted()) {
                                                items.add(LocaleController.getString("Unvote", R.string.Unvote));
                                                options.add(Integer.valueOf(25));
                                            }
                                            if (!message.isForwarded() && ((message.isOut() && (this.currentChat == null || !ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup)) || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && (this.currentChat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.edit_messages))))) {
                                                items.add(LocaleController.getString("StopPoll", R.string.StopPoll));
                                                options.add(Integer.valueOf(26));
                                            }
                                        }
                                    } else if (type == 3) {
                                        if ((this.selectedObject.messageOwner.media instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                                            items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                                            options.add(Integer.valueOf(11));
                                        }
                                    } else if (type == 4) {
                                        if (this.selectedObject.isVideo()) {
                                            if (!this.selectedObject.needDrawBluredPreview()) {
                                                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                                options.add(Integer.valueOf(4));
                                                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                                options.add(Integer.valueOf(6));
                                            }
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
                                        } else if (!this.selectedObject.needDrawBluredPreview()) {
                                            items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                            options.add(Integer.valueOf(4));
                                        }
                                    } else if (type == 5) {
                                        items.add(LocaleController.getString("ApplyLocalizationFile", R.string.ApplyLocalizationFile));
                                        options.add(Integer.valueOf(5));
                                        items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                                        options.add(Integer.valueOf(10));
                                        items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                                        options.add(Integer.valueOf(6));
                                    } else if (type == 10) {
                                        items.add(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
                                        options.add(Integer.valueOf(5));
                                        items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                                        options.add(Integer.valueOf(10));
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
                                            options.add(Integer.valueOf(9));
                                        } else {
                                            items.add(LocaleController.getString("AddToStickers", R.string.AddToStickers));
                                            options.add(Integer.valueOf(9));
                                            if (DataQuery.getInstance(this.currentAccount).isStickerInFavorites(this.selectedObject.getDocument())) {
                                                items.add(LocaleController.getString("DeleteFromFavorites", R.string.DeleteFromFavorites));
                                                options.add(Integer.valueOf(21));
                                            } else if (DataQuery.getInstance(this.currentAccount).canAddStickerToFavorites()) {
                                                items.add(LocaleController.getString("AddToFavorites", R.string.AddToFavorites));
                                                options.add(Integer.valueOf(20));
                                            }
                                        }
                                    } else if (type == 8) {
                                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                                        if (!(user == null || user.var_id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.var_id)) != null)) {
                                            items.add(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                                            options.add(Integer.valueOf(15));
                                        }
                                        if (!TextUtils.isEmpty(this.selectedObject.messageOwner.media.phone_number)) {
                                            items.add(LocaleController.getString("Copy", R.string.Copy));
                                            options.add(Integer.valueOf(16));
                                            items.add(LocaleController.getString("Call", R.string.Call));
                                            options.add(Integer.valueOf(17));
                                        }
                                    } else if (type == 9) {
                                        if (DataQuery.getInstance(this.currentAccount).isStickerInFavorites(this.selectedObject.getDocument())) {
                                            items.add(LocaleController.getString("DeleteFromFavorites", R.string.DeleteFromFavorites));
                                            options.add(Integer.valueOf(21));
                                        } else {
                                            items.add(LocaleController.getString("AddToFavorites", R.string.AddToFavorites));
                                            options.add(Integer.valueOf(20));
                                        }
                                    }
                                    if (!(this.selectedObject.needDrawBluredPreview() || this.selectedObject.isLiveLocation())) {
                                        items.add(LocaleController.getString("Forward", R.string.Forward));
                                        options.add(Integer.valueOf(2));
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
                                    if (this.selectedObject.contentType == 0 && this.selectedObject.getId() > 0 && !this.selectedObject.isOut() && (this.currentChat != null || (this.currentUser != null && this.currentUser.bot))) {
                                        items.add(LocaleController.getString("ReportChat", R.string.ReportChat));
                                        options.add(Integer.valueOf(23));
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
                                    } else if (type == 8) {
                                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                                        if (!(user == null || user.var_id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.var_id)) != null)) {
                                            items.add(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                                            options.add(Integer.valueOf(15));
                                        }
                                        if (!TextUtils.isEmpty(this.selectedObject.messageOwner.media.phone_number)) {
                                            items.add(LocaleController.getString("Copy", R.string.Copy));
                                            options.add(Integer.valueOf(16));
                                            items.add(LocaleController.getString("Call", R.string.Call));
                                            options.add(Integer.valueOf(17));
                                        }
                                    }
                                    items.add(LocaleController.getString("Delete", R.string.Delete));
                                    options.add(Integer.valueOf(1));
                                }
                            }
                            if (!options.isEmpty()) {
                                Builder builder = new Builder(getParentActivity());
                                builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new ChatActivity$$Lambda$53(this, options));
                                builder.setTitle(LocaleController.getString("Message", R.string.Message));
                                showDialog(builder.create());
                                return;
                            }
                            return;
                        } else {
                            return;
                        }
                    }
                }
                allowEdit = false;
                allowChatActions = false;
                if (single) {
                }
                if (getParentActivity() == null) {
                }
            }
        }
    }

    final /* synthetic */ void lambda$createMenu$68$ChatActivity(ArrayList options, DialogInterface dialogInterface, int i) {
        if (this.selectedObject != null && i >= 0 && i < options.size()) {
            processSelectedOption(((Integer) options.get(i)).intValue());
        }
    }

    private void startEditingMessageObject(MessageObject messageObject) {
        if (messageObject != null && getParentActivity() != null) {
            if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
                this.chatActivityEnterView.setFieldFocused();
            }
            this.mentionsAdapter.setNeedBotContext(false);
            this.chatListView.setOnItemLongClickListener((OnItemLongClickListenerExtended) null);
            this.chatListView.setOnItemClickListener((OnItemClickListenerExtended) null);
            this.chatListView.setClickable(false);
            this.chatListView.setLongClickable(false);
            this.chatActivityEnterView.setVisibility(0);
            showFieldPanelForEdit(true, messageObject);
            updateBottomOverlay();
            checkEditTimer();
            this.chatActivityEnterView.setAllowStickersAndGifs(false, false);
            updatePinnedMessageView(true);
            updateVisibleRows();
            TL_messages_getMessageEditData req = new TL_messages_getMessageEditData();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) this.dialog_id);
            req.var_id = messageObject.getId();
            this.editingMessageObjectReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatActivity$$Lambda$54(this));
        }
    }

    final /* synthetic */ void lambda$startEditingMessageObject$70$ChatActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$75(this, response));
    }

    final /* synthetic */ void lambda$null$69$ChatActivity(TLObject response) {
        this.editingMessageObjectReqId = 0;
        if (response == null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("EditMessageError", R.string.EditMessageError));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
            if (this.chatActivityEnterView != null) {
                this.chatActivityEnterView.setEditingMessageObject(null, false);
                hideFieldPanel();
            }
        } else if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.showEditDoneProgress(false, true);
        }
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if (name && previousUid != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (chat != null) {
                    str = chat.title + ":\n";
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        }
        if (messageObject.messageOwner.media == null || messageObject.messageOwner.message == null) {
            return str + messageObject.messageText;
        }
        return str + messageObject.messageOwner.message;
    }

    private void saveMessageToGallery(MessageObject messageObject) {
        String path = messageObject.messageOwner.attachPath;
        if (!(TextUtils.isEmpty(path) || new File(path).exists())) {
            path = null;
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getPathToMessage(messageObject.messageOwner).toString();
        }
        MediaController.saveFile(path, getParentActivity(), messageObject.isVideo() ? 1 : 0, null, null);
    }

    private void processSelectedOption(int option) {
        if (this.selectedObject != null && getParentActivity() != null) {
            int a;
            Bundle args;
            File file;
            Builder builder;
            String path;
            Intent intent;
            switch (option) {
                case 0:
                    if (this.selectedObjectGroup == null) {
                        if (SendMessagesHelper.getInstance(this.currentAccount).retrySendMessage(this.selectedObject, false)) {
                            updateVisibleRows();
                            moveScrollToLastMessage();
                            break;
                        }
                    }
                    boolean success = true;
                    for (a = 0; a < this.selectedObjectGroup.messages.size(); a++) {
                        if (!SendMessagesHelper.getInstance(this.currentAccount).retrySendMessage((MessageObject) this.selectedObjectGroup.messages.get(a), false)) {
                            success = false;
                        }
                    }
                    if (success) {
                        moveScrollToLastMessage();
                        break;
                    }
                    break;
                case 1:
                    if (getParentActivity() != null) {
                        createDeleteMessagesAlert(this.selectedObject, this.selectedObjectGroup);
                        break;
                    }
                    this.selectedObject = null;
                    this.selectedObjectGroup = null;
                    return;
                case 2:
                    this.forwardingMessage = this.selectedObject;
                    this.forwardingMessageGroup = this.selectedObjectGroup;
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 3);
                    BaseFragment dialogsActivity = new DialogsActivity(args);
                    dialogsActivity.setDelegate(this);
                    presentFragment(dialogsActivity);
                    break;
                case 3:
                    AndroidUtilities.addToClipboard(getMessageContent(this.selectedObject, 0, false));
                    break;
                case 4:
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        if (this.selectedObjectGroup == null) {
                            saveMessageToGallery(this.selectedObject);
                            break;
                        }
                        for (a = 0; a < this.selectedObjectGroup.messages.size(); a++) {
                            saveMessageToGallery((MessageObject) this.selectedObjectGroup.messages.get(a));
                        }
                        break;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    this.selectedObjectGroup = null;
                    return;
                case 5:
                    File locFile = null;
                    if (!TextUtils.isEmpty(this.selectedObject.messageOwner.attachPath)) {
                        file = new File(this.selectedObject.messageOwner.attachPath);
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
                        if (!locFile.getName().toLowerCase().endsWith("attheme")) {
                            if (!LocaleController.getInstance().applyLanguageFile(locFile, this.currentAccount)) {
                                if (getParentActivity() != null) {
                                    builder = new Builder(getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setMessage(LocaleController.getString("IncorrectLocalization", R.string.IncorrectLocalization));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                    showDialog(builder.create());
                                    break;
                                }
                                this.selectedObject = null;
                                this.selectedObjectGroup = null;
                                return;
                            }
                            presentFragment(new LanguageSelectActivity());
                            break;
                        }
                        if (this.chatLayoutManager != null) {
                            int lastPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                            if (lastPosition != 0) {
                                this.scrollToPositionOnRecreate = lastPosition;
                                Holder holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(this.scrollToPositionOnRecreate);
                                if (holder != null) {
                                    this.scrollToOffsetOnRecreate = (this.chatListView.getMeasuredHeight() - holder.itemView.getBottom()) - this.chatListView.getPaddingBottom();
                                } else {
                                    this.scrollToPositionOnRecreate = -1;
                                }
                            } else {
                                this.scrollToPositionOnRecreate = -1;
                            }
                        }
                        ThemeInfo themeInfo = Theme.applyThemeFile(locFile, this.selectedObject.getDocumentName(), true);
                        if (themeInfo == null) {
                            this.scrollToPositionOnRecreate = -1;
                            if (getParentActivity() != null) {
                                builder = new Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("IncorrectTheme", R.string.IncorrectTheme));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                showDialog(builder.create());
                                break;
                            }
                            this.selectedObject = null;
                            this.selectedObjectGroup = null;
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
                    file = new File(path);
                    if (VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", file));
                            intent.setFlags(1);
                        } catch (Exception e) {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                        }
                    } else {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                    }
                    try {
                        getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                        break;
                    } catch (Throwable th) {
                        break;
                    }
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
                    this.selectedObjectGroup = null;
                    return;
                case 8:
                    showFieldPanelForReply(true, this.selectedObject);
                    break;
                case 9:
                    Context parentActivity = getParentActivity();
                    InputStickerSet inputStickerSet = this.selectedObject.getInputStickerSet();
                    StickersAlertDelegate stickersAlertDelegate = (this.bottomOverlayChat.getVisibility() == 0 || !ChatObject.canSendStickers(this.currentChat)) ? null : this.chatActivityEnterView;
                    showDialog(new StickersAlert(parentActivity, this, inputStickerSet, null, stickersAlertDelegate));
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
                        MediaController.saveFile(path, getParentActivity(), this.selectedObject.isMusic() ? 3 : 2, fileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : TtmlNode.ANONYMOUS_REGION_ID);
                        break;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    this.selectedObjectGroup = null;
                    return;
                    break;
                case 11:
                    Document document = this.selectedObject.getDocument();
                    MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject, document);
                    showGifHint();
                    this.chatActivityEnterView.addRecentGif(document);
                    break;
                case 12:
                    startEditingMessageObject(this.selectedObject);
                    this.selectedObject = null;
                    this.selectedObjectGroup = null;
                    break;
                case 13:
                    boolean[] checks;
                    int mid = this.selectedObject.getId();
                    builder = new Builder(getParentActivity());
                    if (this.currentUser != null) {
                        builder.setMessage(LocaleController.getString("PinMessageAlertChat", R.string.PinMessageAlertChat));
                        checks = new boolean[]{false};
                    } else if (!(ChatObject.isChannel(this.currentChat) && this.currentChat.megagroup) && (this.currentChat == null || ChatObject.isChannel(this.currentChat))) {
                        builder.setMessage(LocaleController.getString("PinMessageAlertChannel", R.string.PinMessageAlertChannel));
                        checks = new boolean[]{false};
                    } else {
                        builder.setMessage(LocaleController.getString("PinMessageAlert", R.string.PinMessageAlert));
                        checks = new boolean[]{true};
                        View frameLayout = new FrameLayout(getParentActivity());
                        CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
                        cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        cell.setText(LocaleController.getString("PinNotify", R.string.PinNotify), TtmlNode.ANONYMOUS_REGION_ID, true, false);
                        cell.setPadding(LocaleController.isRTL ? AndroidUtilities.m9dp(8.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.m9dp(8.0f), 0);
                        frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                        cell.setOnClickListener(new ChatActivity$$Lambda$55(checks));
                        builder.setView(frameLayout);
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$56(this, mid, checks));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    break;
                case 14:
                    builder = new Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("UnpinMessageAlert", R.string.UnpinMessageAlert));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$57(this));
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
                        intent.addFlags(CLASSNAMEC.ENCODING_PCM_MU_LAW);
                        getParentActivity().startActivityForResult(intent, 500);
                        break;
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                        break;
                    }
                case 18:
                    if (this.currentUser != null) {
                        VoIPHelper.startCall(this.currentUser, getParentActivity(), MessagesController.getInstance(this.currentAccount).getUserFull(this.currentUser.var_id));
                        break;
                    }
                    break;
                case 19:
                    VoIPHelper.showRateAlert(getParentActivity(), (TL_messageActionPhoneCall) this.selectedObject.messageOwner.action);
                    break;
                case 20:
                    DataQuery.getInstance(this.currentAccount).addRecentSticker(2, this.selectedObject, this.selectedObject.getDocument(), (int) (System.currentTimeMillis() / 1000), false);
                    break;
                case 21:
                    DataQuery.getInstance(this.currentAccount).addRecentSticker(2, this.selectedObject, this.selectedObject.getDocument(), (int) (System.currentTimeMillis() / 1000), true);
                    break;
                case 22:
                    TLObject req = new TL_channels_exportMessageLink();
                    req.var_id = this.selectedObject.getId();
                    req.channel = MessagesController.getInputChannel(this.currentChat);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, ChatActivity$$Lambda$58.$instance);
                    break;
                case 23:
                    AlertsCreator.createReportAlert(getParentActivity(), this.dialog_id, this.selectedObject.getId(), this);
                    break;
                case 24:
                    if (!this.selectedObject.isEditing() && (!this.selectedObject.isSending() || this.selectedObjectGroup != null)) {
                        if (this.selectedObject.isSending() && this.selectedObjectGroup != null) {
                            for (a = 0; a < this.selectedObjectGroup.messages.size(); a++) {
                                SendMessagesHelper.getInstance(this.currentAccount).cancelSendingMessage(new ArrayList(this.selectedObjectGroup.messages));
                            }
                            break;
                        }
                    }
                    SendMessagesHelper.getInstance(this.currentAccount).cancelSendingMessage(this.selectedObject);
                    break;
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL25 /*25*/:
                    AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(getParentActivity(), 3)};
                    int requestId = SendMessagesHelper.getInstance(this.currentAccount).sendVote(this.selectedObject, null, new ChatActivity$$Lambda$59(progressDialog));
                    if (requestId != 0) {
                        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$60(this, progressDialog, requestId), 500);
                        break;
                    }
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    MessageObject object = this.selectedObject;
                    builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("StopPollAlertTitle", R.string.StopPollAlertTitle));
                    builder.setMessage(LocaleController.getString("StopPollAlertText", R.string.StopPollAlertText));
                    builder.setPositiveButton(LocaleController.getString("Stop", R.string.Stop), new ChatActivity$$Lambda$61(this, object));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    break;
            }
            this.selectedObject = null;
            this.selectedObjectGroup = null;
        }
    }

    static final /* synthetic */ void lambda$processSelectedOption$71$ChatActivity(boolean[] checks, View v) {
        boolean z;
        CheckBoxCell cell1 = (CheckBoxCell) v;
        if (checks[0]) {
            z = false;
        } else {
            z = true;
        }
        checks[0] = z;
        cell1.setChecked(checks[0], true);
    }

    final /* synthetic */ void lambda$processSelectedOption$72$ChatActivity(int mid, boolean[] checks, DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).pinMessage(this.currentChat, this.currentUser, mid, checks[0]);
    }

    final /* synthetic */ void lambda$processSelectedOption$73$ChatActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).pinMessage(this.currentChat, this.currentUser, 0, false);
    }

    static final /* synthetic */ void lambda$null$74$ChatActivity(TLObject response) {
        if (response != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ((TL_exportedMessageLink) response).link));
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    static final /* synthetic */ void lambda$processSelectedOption$76$ChatActivity(AlertDialog[] progressDialog) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
    }

    final /* synthetic */ void lambda$processSelectedOption$78$ChatActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new ChatActivity$$Lambda$73(this, requestId));
            showDialog(progressDialog[0]);
        }
    }

    final /* synthetic */ void lambda$null$77$ChatActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    final /* synthetic */ void lambda$processSelectedOption$84$ChatActivity(MessageObject object, DialogInterface dialogInterface, int i) {
        AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(getParentActivity(), 3)};
        TL_messages_editMessage req = new TL_messages_editMessage();
        TL_messageMediaPoll mediaPoll = object.messageOwner.media;
        TL_inputMediaPoll poll = new TL_inputMediaPoll();
        poll.poll = new TL_poll();
        poll.poll.var_id = mediaPoll.poll.var_id;
        poll.poll.question = mediaPoll.poll.question;
        poll.poll.answers = mediaPoll.poll.answers;
        poll.poll.closed = true;
        req.media = poll;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) this.dialog_id);
        req.var_id = object.getId();
        req.flags |= MessagesController.UPDATE_MASK_CHAT_ADMINS;
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$69(this, progressDialog, ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatActivity$$Lambda$68(this, progressDialog, req))), 500);
    }

    final /* synthetic */ void lambda$null$81$ChatActivity(AlertDialog[] progressDialog, TL_messages_editMessage req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$71(progressDialog));
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
        } else {
            AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$72(this, error, req));
        }
    }

    static final /* synthetic */ void lambda$null$79$ChatActivity(AlertDialog[] progressDialog) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
    }

    final /* synthetic */ void lambda$null$80$ChatActivity(TL_error error, TL_messages_editMessage req) {
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
    }

    final /* synthetic */ void lambda$null$83$ChatActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new ChatActivity$$Lambda$70(this, requestId));
            showDialog(progressDialog[0]);
        }
    }

    final /* synthetic */ void lambda$null$82$ChatActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        if (this.forwardingMessage != null || this.selectedMessagesIds[0].size() != 0 || this.selectedMessagesIds[1].size() != 0) {
            int a;
            ArrayList<MessageObject> fmessages = new ArrayList();
            if (this.forwardingMessage != null) {
                if (this.forwardingMessageGroup != null) {
                    fmessages.addAll(this.forwardingMessageGroup.messages);
                } else {
                    fmessages.add(this.forwardingMessage);
                }
                this.forwardingMessage = null;
                this.forwardingMessageGroup = null;
            } else {
                for (a = 1; a >= 0; a--) {
                    int b;
                    ArrayList<Integer> ids = new ArrayList();
                    for (b = 0; b < this.selectedMessagesIds[a].size(); b++) {
                        ids.add(Integer.valueOf(this.selectedMessagesIds[a].keyAt(b)));
                    }
                    Collections.sort(ids);
                    for (b = 0; b < ids.size(); b++) {
                        Integer id = (Integer) ids.get(b);
                        MessageObject messageObject = (MessageObject) this.selectedMessagesIds[a].get(id.intValue());
                        if (messageObject != null && id.intValue() > 0) {
                            fmessages.add(messageObject);
                        }
                    }
                    this.selectedMessagesCanCopyIds[a].clear();
                    this.selectedMessagesCanStarIds[a].clear();
                    this.selectedMessagesIds[a].clear();
                }
                this.cantDeleteMessagesCount = 0;
                this.canEditMessagesCount = 0;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            long did;
            if (dids.size() > 1 || ((Long) dids.get(0)).longValue() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) || message != null) {
                for (a = 0; a < dids.size(); a++) {
                    did = ((Long) dids.get(a)).longValue();
                    if (message != null) {
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message.toString(), did, null, null, true, null, null, null);
                    }
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(fmessages, did);
                }
                fragment.lambda$checkDiscard$2$PollCreateActivity();
                return;
            }
            did = ((Long) dids.get(0)).longValue();
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
                if (lower_part == 0 || MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, fragment)) {
                    ChatActivity chatActivity = new ChatActivity(args);
                    if (presentFragment(chatActivity, true)) {
                        chatActivity.showFieldPanelForForward(true, fmessages);
                        if (!AndroidUtilities.isTablet()) {
                            removeSelfFromStack();
                            return;
                        }
                        return;
                    }
                    fragment.lambda$checkDiscard$2$PollCreateActivity();
                    return;
                }
                return;
            }
            fragment.lambda$checkDiscard$2$PollCreateActivity();
            moveScrollToLastMessage();
            showFieldPanelForForward(true, fmessages);
            if (AndroidUtilities.isTablet()) {
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            updateVisibleRows();
        }
    }

    public boolean checkRecordLocked() {
        if (this.chatActivityEnterView == null || !this.chatActivityEnterView.isRecordLocked()) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        if (this.chatActivityEnterView.isInVideoMode()) {
            builder.setTitle(LocaleController.getString("DiscardVideoMessageTitle", R.string.DiscardVideoMessageTitle));
            builder.setMessage(LocaleController.getString("DiscardVideoMessageDescription", R.string.DiscardVideoMessageDescription));
        } else {
            builder.setTitle(LocaleController.getString("DiscardVoiceMessageTitle", R.string.DiscardVoiceMessageTitle));
            builder.setMessage(LocaleController.getString("DiscardVoiceMessageDescription", R.string.DiscardVoiceMessageDescription));
        }
        builder.setPositiveButton(LocaleController.getString("DiscardVoiceMessageAction", R.string.DiscardVoiceMessageAction), new ChatActivity$$Lambda$62(this));
        builder.setNegativeButton(LocaleController.getString("Continue", R.string.Continue), null);
        showDialog(builder.create());
        return true;
    }

    final /* synthetic */ void lambda$checkRecordLocked$85$ChatActivity(DialogInterface dialog, int which) {
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.cancelRecordingAudioVideo();
        }
    }

    public boolean onBackPressed() {
        if (checkRecordLocked()) {
            return false;
        }
        if (this.actionBar != null && this.actionBar.isActionModeShowed()) {
            for (int a = 1; a >= 0; a--) {
                this.selectedMessagesIds[a].clear();
                this.selectedMessagesCanCopyIds[a].clear();
                this.selectedMessagesCanStarIds[a].clear();
            }
            this.actionBar.hideActionMode();
            updatePinnedMessageView(true);
            this.cantDeleteMessagesCount = 0;
            this.canEditMessagesCount = 0;
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
            int lastVisibleItem = -1;
            if (!(this.wasManualScroll || this.unreadMessageObject == null || this.chatListView.getMeasuredHeight() == 0)) {
                int pos = this.messages.indexOf(this.unreadMessageObject);
                if (pos >= 0) {
                    lastVisibleItem = this.chatAdapter.messagesStartRow + pos;
                }
            }
            int count = this.chatListView.getChildCount();
            MessageObject editingMessageObject = this.chatActivityEnterView != null ? this.chatActivityEnterView.getEditingMessageObject() : null;
            for (int a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    MessageObject messageObject = cell.getMessageObject();
                    boolean disableSelection = false;
                    boolean selected = false;
                    if (this.actionBar.isActionModeShowed()) {
                        int idx = messageObject.getDialogId() == this.dialog_id ? 0 : 1;
                        if (messageObject == editingMessageObject || this.selectedMessagesIds[idx].indexOfKey(messageObject.getId()) >= 0) {
                            setCellSelectionBackground(messageObject, cell, idx);
                            selected = true;
                        } else {
                            view.setBackgroundDrawable(null);
                        }
                        disableSelection = true;
                    } else {
                        view.setBackgroundDrawable(null);
                    }
                    cell.setMessageObject(cell.getMessageObject(), cell.getCurrentMessagesGroup(), cell.isPinnedBottom(), cell.isPinnedTop());
                    boolean z = !disableSelection;
                    boolean z2 = disableSelection && selected;
                    cell.setCheckPressed(z, z2);
                    z2 = (this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID || messageObject == null || messageObject.getId() != this.highlightMessageId) ? false : true;
                    cell.setHighlighted(z2);
                    if (this.searchContainer != null && this.searchContainer.getVisibility() == 0) {
                        if (DataQuery.getInstance(this.currentAccount).isMessageFound(messageObject.getId(), messageObject.getDialogId() == this.mergeDialogId) && DataQuery.getInstance(this.currentAccount).getLastSearchQuery() != null) {
                            cell.setHighlightedText(DataQuery.getInstance(this.currentAccount).getLastSearchQuery());
                        }
                    }
                    cell.setHighlightedText(null);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell cell2 = (ChatActionCell) view;
                    cell2.setMessageObject(cell2.getMessageObject());
                }
            }
            this.chatListView.invalidate();
            if (lastVisibleItem != -1) {
                this.chatLayoutManager.scrollToPositionWithOffset(lastVisibleItem, ((this.chatListView.getMeasuredHeight() - this.chatListView.getPaddingBottom()) - this.chatListView.getPaddingTop()) - AndroidUtilities.m9dp(29.0f));
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
                int dt = messageObject.canEditMessageAnytime(this.currentChat) ? 360 : (MessagesController.getInstance(this.currentAccount).maxEditTime + 300) - Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date);
                if (dt > 0) {
                    if (dt <= 300) {
                        SimpleTextView simpleTextView = this.replyObjectTextView;
                        Object[] objArr = new Object[1];
                        objArr[0] = String.format("%d:%02d", new Object[]{Integer.valueOf(dt / 60), Integer.valueOf(dt % 60)});
                        simpleTextView.setText(LocaleController.formatString("TimeToEdit", R.string.TimeToEdit, objArr));
                    }
                    AndroidUtilities.runOnUIThread(new ChatActivity$$Lambda$63(this), 1000);
                    return;
                }
                this.chatActivityEnterView.onEditTimeExpired();
                this.replyObjectTextView.setText(LocaleController.formatString("TimeToEditExpired", R.string.TimeToEditExpired, new Object[0]));
            }
        }
    }

    private ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject startMessageObject, boolean playingUnreadMedia) {
        ArrayList<MessageObject> messageObjects = new ArrayList();
        messageObjects.add(startMessageObject);
        int messageId = startMessageObject.getId();
        long startDialogId = startMessageObject.getDialogId();
        if (messageId != 0) {
            for (int a = this.messages.size() - 1; a >= 0; a--) {
                MessageObject messageObject = (MessageObject) this.messages.get(a);
                if ((messageObject.getDialogId() != this.mergeDialogId || startMessageObject.getDialogId() == this.mergeDialogId) && (((this.currentEncryptedChat == null && messageObject.getId() > messageId) || (this.currentEncryptedChat != null && messageObject.getId() < messageId)) && ((messageObject.isVoice() || messageObject.isRoundVideo()) && (!playingUnreadMedia || (messageObject.isContentUnread() && !messageObject.isOut()))))) {
                    messageObjects.add(messageObject);
                }
            }
        }
        return messageObjects;
    }

    private void alertUserOpenError(MessageObject message) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
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
        if (!this.actionBar.isSearchFieldVisible()) {
            this.avatarContainer.setVisibility(8);
            this.headerItem.setVisibility(8);
            this.attachItem.setVisibility(8);
            this.editTextItem.setVisibility(8);
            this.searchItem.setVisibility(0);
            updateSearchButtons(0, 0, -1);
            updateBottomOverlay();
        }
        this.openSearchKeyboard = text == null;
        this.searchItem.openSearch(this.openSearchKeyboard);
        if (text != null) {
            this.searchItem.setSearchFieldText(text, false);
            this.searchItem.getSearchField().setSelection(this.searchItem.getSearchField().length());
            DataQuery.getInstance(this.currentAccount).searchMessagesInChat(text, this.dialog_id, this.mergeDialogId, this.classGuid, 0, this.searchingUserMessages);
        }
        updatePinnedMessageView(true);
    }

    public void didSelectLocation(MessageMedia location, int live) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(location, this.dialog_id, this.replyingMessageObject, null, null);
        moveScrollToLastMessage();
        if (live == 1) {
            hideFieldPanel();
            DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
        }
        if (this.paused) {
            this.scrollToTopOnResume = true;
        }
    }

    public boolean isEditingMessageMedia() {
        return (this.chatAttachAlert == null || this.chatAttachAlert.getEditingMessageObject() == null) ? false : true;
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

    public boolean allowGroupPhotos() {
        return !isEditingMessageMedia() && (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 73);
    }

    public EncryptedChat getCurrentEncryptedChat() {
        return this.currentEncryptedChat;
    }

    public ChatFull getCurrentChatInfo() {
        return this.chatInfo;
    }

    public TL_userFull getCurrentUserInfo() {
        return this.userInfo;
    }

    public void sendMedia(PhotoEntry photoEntry, VideoEditedInfo videoEditedInfo) {
        if (photoEntry != null) {
            fillEditingMediaWithCaption(photoEntry.caption, photoEntry.entities);
            if (photoEntry.isVideo) {
                if (videoEditedInfo != null) {
                    SendMessagesHelper.prepareSendingVideo(photoEntry.path, videoEditedInfo.estimatedSize, videoEditedInfo.estimatedDuration, videoEditedInfo.resultWidth, videoEditedInfo.resultHeight, videoEditedInfo, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.entities, photoEntry.ttl, this.editingMessageObject);
                } else {
                    SendMessagesHelper.prepareSendingVideo(photoEntry.path, 0, 0, 0, 0, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.entities, photoEntry.ttl, this.editingMessageObject);
                }
                hideFieldPanel();
                DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
            } else if (photoEntry.imagePath != null) {
                SendMessagesHelper.prepareSendingPhoto(photoEntry.imagePath, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.entities, photoEntry.stickers, null, photoEntry.ttl, this.editingMessageObject);
                hideFieldPanel();
                DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
            } else if (photoEntry.path != null) {
                SendMessagesHelper.prepareSendingPhoto(photoEntry.path, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.entities, photoEntry.stickers, null, photoEntry.ttl, this.editingMessageObject);
                hideFieldPanel();
                DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
            }
        }
    }

    public void showOpenGameAlert(TL_game game, MessageObject messageObject, String urlStr, boolean ask, int uid) {
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
        String str;
        if (ask) {
            String name;
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (user != null) {
                name = ContactsController.formatName(user.first_name, user.last_name);
            } else {
                name = TtmlNode.ANONYMOUS_REGION_ID;
            }
            builder.setMessage(LocaleController.formatString("BotPermissionGameAlert", R.string.BotPermissionGameAlert, name));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatActivity$$Lambda$64(this, game, messageObject, urlStr, uid));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else if (VERSION.SDK_INT < 21 || AndroidUtilities.isTablet() || !WebviewActivity.supportWebview()) {
            Activity parentActivity = getParentActivity();
            str = game.short_name;
            String str2 = (user == null || user.username == null) ? TtmlNode.ANONYMOUS_REGION_ID : user.username;
            WebviewActivity.openGameInBrowser(urlStr, messageObject, parentActivity, str, str2);
        } else if (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
            str = (user == null || TextUtils.isEmpty(user.username)) ? TtmlNode.ANONYMOUS_REGION_ID : user.username;
            presentFragment(new WebviewActivity(urlStr, str, game.title, game.short_name, messageObject));
        }
    }

    final /* synthetic */ void lambda$showOpenGameAlert$86$ChatActivity(TL_game game, MessageObject messageObject, String urlStr, int uid, DialogInterface dialogInterface, int i) {
        showOpenGameAlert(game, messageObject, urlStr, false, uid);
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putBoolean("askgame_" + uid, false).commit();
    }

    public void showOpenUrlAlert(String url, boolean ask) {
        boolean z = true;
        if (Browser.isInternalUrl(url, null) || !ask) {
            Context parentActivity = getParentActivity();
            if (this.inlineReturn != 0) {
                z = false;
            }
            Browser.openUrl(parentActivity, url, z);
            return;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert", R.string.OpenUrlAlert, url));
        builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new ChatActivity$$Lambda$65(this, url));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$showOpenUrlAlert$87$ChatActivity(String url, DialogInterface dialogInterface, int i) {
        Browser.openUrl(getParentActivity(), url, this.inlineReturn == 0);
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = this.messages.indexOf(messageObject);
        if (index != -1) {
            this.messages.remove(index);
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + index);
            }
        }
    }

    public void openVCard(String vcard, String first_name, String last_name) {
        try {
            File f = new File(FileLoader.getDirectory(4), "sharing/");
            f.mkdirs();
            File f2 = new File(f, "vcard.vcf");
            BufferedWriter writer = new BufferedWriter(new FileWriter(f2));
            writer.write(vcard);
            writer.close();
            presentFragment(new PhonebookShareActivity(null, null, f2, ContactsController.formatName(first_name, last_name)));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void setCellSelectionBackground(MessageObject message, ChatMessageCell messageCell, int idx) {
        GroupedMessages groupedMessages = getValidGroupedMessage(message);
        if (groupedMessages != null) {
            boolean hasUnselected = false;
            for (int a = 0; a < groupedMessages.messages.size(); a++) {
                if (this.selectedMessagesIds[idx].indexOfKey(((MessageObject) groupedMessages.messages.get(a)).getId()) < 0) {
                    hasUnselected = true;
                    break;
                }
            }
            if (!hasUnselected) {
                groupedMessages = null;
            }
        }
        if (groupedMessages == null) {
            messageCell.setBackgroundColor(Theme.getColor(Theme.key_chat_selectedBackground));
        } else {
            messageCell.setBackground(null);
        }
    }

    private void setItemAnimationsEnabled(boolean enabled) {
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate selectedBackgroundDelegate = new ChatActivity$$Lambda$66(this);
        ThemeDescriptionDelegate attachAlertDelegate = new ChatActivity$$Lambda$67(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[372];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_chat_wallpaper);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[8] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[10] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getTitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[11] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getSubtitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, null, null, Theme.key_actionBarDefaultSubtitle, null);
        themeDescriptionArr[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[13] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[14] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[15] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[16] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault);
        themeDescriptionArr[17] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop);
        themeDescriptionArr[18] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector);
        themeDescriptionArr[19] = new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[20] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getTitleTextView() : null, 0, null, null, new Drawable[]{Theme.chat_muteIconDrawable}, null, Theme.key_chat_muteIcon);
        themeDescriptionArr[21] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getTitleTextView() : null, 0, null, null, new Drawable[]{Theme.chat_lockIconDrawable}, null, Theme.key_chat_lockIcon);
        themeDescriptionArr[22] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
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
        themeDescriptionArr[37] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble);
        themeDescriptionArr[38] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected);
        themeDescriptionArr[39] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, Theme.key_chat_inBubbleShadow);
        themeDescriptionArr[search] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble);
        themeDescriptionArr[41] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected);
        themeDescriptionArr[42] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, Theme.key_chat_outBubbleShadow);
        themeDescriptionArr[43] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[44] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceLink);
        themeDescriptionArr[45] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_replyIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable}, null, Theme.key_chat_serviceIcon);
        themeDescriptionArr[46] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[47] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackgroundSelected);
        themeDescriptionArr[48] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, null, null, Theme.key_chat_messageTextIn);
        themeDescriptionArr[49] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut);
        themeDescriptionArr[50] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, null, null, Theme.key_chat_messageLinkIn, null);
        themeDescriptionArr[51] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageLinkOut, null);
        themeDescriptionArr[52] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheck);
        themeDescriptionArr[53] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected);
        themeDescriptionArr[54] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutClockDrawable}, null, Theme.key_chat_outSentClock);
        themeDescriptionArr[55] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, null, Theme.key_chat_outSentClockSelected);
        themeDescriptionArr[56] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInClockDrawable}, null, Theme.key_chat_inSentClock);
        themeDescriptionArr[57] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, null, Theme.key_chat_inSentClockSelected);
        themeDescriptionArr[58] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck);
        themeDescriptionArr[59] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable}, null, Theme.key_chat_serviceText);
        themeDescriptionArr[60] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, null, Theme.key_chat_mediaSentClock);
        themeDescriptionArr[61] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, null, Theme.key_chat_outViews);
        themeDescriptionArr[62] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, null, Theme.key_chat_outViewsSelected);
        themeDescriptionArr[63] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable}, null, Theme.key_chat_inViews);
        themeDescriptionArr[64] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, null, Theme.key_chat_inViewsSelected);
        themeDescriptionArr[65] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, null, Theme.key_chat_mediaViews);
        themeDescriptionArr[66] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, Theme.key_chat_outMenu);
        themeDescriptionArr[67] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, Theme.key_chat_outMenuSelected);
        themeDescriptionArr[68] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, Theme.key_chat_inMenu);
        themeDescriptionArr[69] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, Theme.key_chat_inMenuSelected);
        themeDescriptionArr[70] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, Theme.key_chat_mediaMenu);
        themeDescriptionArr[71] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable, Theme.chat_msgOutCallDrawable}, null, Theme.key_chat_outInstant);
        themeDescriptionArr[72] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCallSelectedDrawable}, null, Theme.key_chat_outInstantSelected);
        themeDescriptionArr[73] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_msgInCallDrawable}, null, Theme.key_chat_inInstant);
        themeDescriptionArr[74] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInCallSelectedDrawable}, null, Theme.key_chat_inInstantSelected);
        themeDescriptionArr[75] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpRedDrawable, Theme.chat_msgCallDownRedDrawable}, null, Theme.key_calls_callReceivedRedIcon);
        themeDescriptionArr[76] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable, Theme.chat_msgCallDownGreenDrawable}, null, Theme.key_calls_callReceivedGreenIcon);
        themeDescriptionArr[77] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, Theme.key_chat_sentError);
        themeDescriptionArr[78] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, Theme.key_chat_sentErrorIcon);
        themeDescriptionArr[79] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, selectedBackgroundDelegate, Theme.key_chat_selectedBackground);
        themeDescriptionArr[80] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, Theme.key_chat_previewDurationText);
        themeDescriptionArr[81] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, Theme.key_chat_previewGameText);
        themeDescriptionArr[82] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantText);
        themeDescriptionArr[83] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantText);
        themeDescriptionArr[84] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantSelectedText);
        themeDescriptionArr[85] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantSelectedText);
        themeDescriptionArr[86] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, Theme.key_chat_secretTimeText);
        themeDescriptionArr[87] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerNameText);
        themeDescriptionArr[88] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, Theme.key_chat_botButtonText);
        themeDescriptionArr[89] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, Theme.key_chat_botProgress);
        themeDescriptionArr[90] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_timeBackgroundPaint, null, null, Theme.key_chat_mediaTimeBackground);
        themeDescriptionArr[91] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inForwardedNameText);
        themeDescriptionArr[92] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outForwardedNameText);
        themeDescriptionArr[93] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inViaBotNameText);
        themeDescriptionArr[94] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outViaBotNameText);
        themeDescriptionArr[95] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerViaBotNameText);
        themeDescriptionArr[96] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine);
        themeDescriptionArr[97] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine);
        themeDescriptionArr[98] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyLine);
        themeDescriptionArr[99] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText);
        themeDescriptionArr[100] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText);
        themeDescriptionArr[101] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyNameText);
        themeDescriptionArr[102] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText);
        themeDescriptionArr[103] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText);
        themeDescriptionArr[104] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageText);
        themeDescriptionArr[105] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageText);
        themeDescriptionArr[106] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText);
        themeDescriptionArr[107] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText);
        themeDescriptionArr[108] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyMessageText);
        themeDescriptionArr[109] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewLine);
        themeDescriptionArr[110] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewLine);
        themeDescriptionArr[111] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSiteNameText);
        themeDescriptionArr[112] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSiteNameText);
        themeDescriptionArr[113] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactNameText);
        themeDescriptionArr[114] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactNameText);
        themeDescriptionArr[115] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneText);
        themeDescriptionArr[116] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneSelectedText);
        themeDescriptionArr[117] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneText);
        themeDescriptionArr[118] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneSelectedText);
        themeDescriptionArr[119] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaProgress);
        themeDescriptionArr[120] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioProgress);
        themeDescriptionArr[121] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioProgress);
        themeDescriptionArr[122] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress);
        themeDescriptionArr[123] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSelectedProgress);
        themeDescriptionArr[124] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaTimeText);
        themeDescriptionArr[125] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText);
        themeDescriptionArr[126] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText);
        themeDescriptionArr[127] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText);
        themeDescriptionArr[128] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_adminText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_AC3] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_adminSelectedText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_HDMV_DTS] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText);
        themeDescriptionArr[131] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerfomerText);
        themeDescriptionArr[132] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerfomerSelectedText);
        themeDescriptionArr[133] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerfomerText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_SPLICE_INFO] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerfomerSelectedText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_E_AC3] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioTitleText);
        themeDescriptionArr[136] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioTitleText);
        themeDescriptionArr[137] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_DTS] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationText);
        themeDescriptionArr[139] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationSelectedText);
        themeDescriptionArr[140] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationSelectedText);
        themeDescriptionArr[141] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbar);
        themeDescriptionArr[142] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbar);
        themeDescriptionArr[143] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarSelected);
        themeDescriptionArr[144] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarSelected);
        themeDescriptionArr[145] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarFill);
        themeDescriptionArr[146] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioCacheSeekbar);
        themeDescriptionArr[147] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarFill);
        themeDescriptionArr[148] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioCacheSeekbar);
        themeDescriptionArr[149] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbar);
        themeDescriptionArr[150] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbar);
        themeDescriptionArr[151] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarSelected);
        themeDescriptionArr[152] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarSelected);
        themeDescriptionArr[153] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarFill);
        themeDescriptionArr[154] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarFill);
        themeDescriptionArr[155] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgress);
        themeDescriptionArr[156] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgress);
        themeDescriptionArr[157] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgressSelected);
        themeDescriptionArr[158] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgressSelected);
        themeDescriptionArr[159] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileNameText);
        themeDescriptionArr[160] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileNameText);
        themeDescriptionArr[161] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoText);
        themeDescriptionArr[162] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoText);
        themeDescriptionArr[163] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoSelectedText);
        themeDescriptionArr[164] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoSelectedText);
        themeDescriptionArr[165] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackground);
        themeDescriptionArr[166] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackground);
        themeDescriptionArr[167] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackgroundSelected);
        themeDescriptionArr[168] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackgroundSelected);
        themeDescriptionArr[169] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoText);
        themeDescriptionArr[170] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoText);
        themeDescriptionArr[171] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoSelectedText);
        themeDescriptionArr[172] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoSelectedText);
        themeDescriptionArr[173] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaInfoText);
        themeDescriptionArr[174] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, Theme.key_chat_linkSelectBackground);
        themeDescriptionArr[175] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, Theme.key_chat_textSelectBackground);
        themeDescriptionArr[176] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outLoader);
        themeDescriptionArr[177] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outMediaIcon);
        themeDescriptionArr[178] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outLoaderSelected);
        themeDescriptionArr[179] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outMediaIconSelected);
        themeDescriptionArr[180] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inLoader);
        themeDescriptionArr[181] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inMediaIcon);
        themeDescriptionArr[182] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inLoaderSelected);
        themeDescriptionArr[183] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inMediaIconSelected);
        themeDescriptionArr[184] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhoto);
        themeDescriptionArr[185] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhotoIcon);
        themeDescriptionArr[186] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoSelected);
        themeDescriptionArr[187] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoIconSelected);
        themeDescriptionArr[TsExtractor.TS_PACKET_SIZE] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhoto);
        themeDescriptionArr[PsExtractor.PRIVATE_STREAM_1] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhotoIcon);
        themeDescriptionArr[190] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoSelected);
        themeDescriptionArr[191] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoIconSelected);
        themeDescriptionArr[PsExtractor.AUDIO_STREAM] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhoto);
        themeDescriptionArr[193] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhotoIcon);
        themeDescriptionArr[194] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoSelected);
        themeDescriptionArr[195] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoIconSelected);
        themeDescriptionArr[196] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, null, Theme.key_chat_outFileIcon);
        themeDescriptionArr[197] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, null, Theme.key_chat_outFileSelectedIcon);
        themeDescriptionArr[198] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, null, Theme.key_chat_inFileIcon);
        themeDescriptionArr[199] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, null, Theme.key_chat_inFileSelectedIcon);
        themeDescriptionArr[Callback.DEFAULT_DRAG_ANIMATION_DURATION] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactBackground);
        themeDescriptionArr[201] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactIcon);
        themeDescriptionArr[202] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactBackground);
        themeDescriptionArr[203] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactIcon);
        themeDescriptionArr[204] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationBackground);
        themeDescriptionArr[205] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationIcon);
        themeDescriptionArr[206] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationBackground);
        themeDescriptionArr[207] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationIcon);
        themeDescriptionArr[208] = new ThemeDescription(this.mentionContainer, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[209] = new ThemeDescription(this.mentionContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[210] = new ThemeDescription(this.searchContainer, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[211] = new ThemeDescription(this.searchContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[212] = new ThemeDescription(this.bottomOverlay, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[213] = new ThemeDescription(this.bottomOverlay, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[214] = new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[215] = new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[216] = new ThemeDescription(this.chatActivityEnterView, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[217] = new ThemeDescription(this.chatActivityEnterView, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[218] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"audioVideoButtonContainer"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[219] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelText);
        themeDescriptionArr[220] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordSendText"}, null, null, null, Theme.key_chat_fieldOverlayText);
        themeDescriptionArr[221] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelHint);
        themeDescriptionArr[222] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, null, null, null, Theme.key_chat_messagePanelSend);
        themeDescriptionArr[223] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"emojiButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[224] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"botButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[225] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"notifyButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[226] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"attachButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[227] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"audioSendButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[228] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"videoSendButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[229] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonImage"}, null, null, null, Theme.key_chat_editDoneIcon);
        themeDescriptionArr[230] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioPanel"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[231] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"micDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed);
        themeDescriptionArr[232] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"cameraDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed);
        themeDescriptionArr[233] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"sendDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed);
        themeDescriptionArr[234] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLock);
        themeDescriptionArr[235] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockTopDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLock);
        themeDescriptionArr[236] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockArrowDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLock);
        themeDescriptionArr[237] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockBackgroundDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLockBackground);
        themeDescriptionArr[238] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockShadowDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLockShadow);
        themeDescriptionArr[239] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordDeleteImageView"}, null, null, null, Theme.key_chat_messagePanelVoiceDelete);
        themeDescriptionArr[PsExtractor.VIDEO_STREAM_MASK] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioBackground"}, null, null, null, Theme.key_chat_recordedVoiceBackground);
        themeDescriptionArr[241] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordTimeText"}, null, null, null, Theme.key_chat_recordTime);
        themeDescriptionArr[242] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordTimeContainer"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[243] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordCancelText"}, null, null, null, Theme.key_chat_recordVoiceCancel);
        themeDescriptionArr[244] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordPanel"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[245] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioTimeTextView"}, null, null, null, Theme.key_chat_messagePanelVoiceDuration);
        themeDescriptionArr[246] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordCancelImage"}, null, null, null, Theme.key_chat_recordVoiceCancel);
        themeDescriptionArr[247] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonProgress"}, null, null, null, Theme.key_contextProgressInner1);
        themeDescriptionArr[248] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonProgress"}, null, null, null, Theme.key_contextProgressOuter1);
        themeDescriptionArr[249] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"cancelBotButton"}, null, null, null, Theme.key_chat_messagePanelCancelInlineBot);
        themeDescriptionArr[Callback.DEFAULT_SWIPE_ANIMATION_DURATION] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"redDotPaint"}, null, null, null, Theme.key_chat_recordedVoiceDot);
        themeDescriptionArr[251] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paint"}, null, null, null, Theme.key_chat_messagePanelVoiceBackground);
        themeDescriptionArr[252] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paintRecord"}, null, null, null, Theme.key_chat_messagePanelVoiceShadow);
        themeDescriptionArr[253] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"seekBarWaveform"}, null, null, null, Theme.key_chat_recordedVoiceProgress);
        themeDescriptionArr[254] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"seekBarWaveform"}, null, null, null, Theme.key_chat_recordedVoiceProgressInner);
        themeDescriptionArr[255] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"playDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPause);
        themeDescriptionArr[256] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"pauseDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPause);
        themeDescriptionArr[257] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"playDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPausePressed);
        themeDescriptionArr[258] = new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"pauseDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPausePressed);
        themeDescriptionArr[259] = new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"dotPaint"}, null, null, null, Theme.key_chat_emojiPanelNewTrending);
        themeDescriptionArr[260] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelBackground);
        themeDescriptionArr[261] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelShadowLine);
        themeDescriptionArr[262] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelEmptyText);
        themeDescriptionArr[263] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelIcon);
        themeDescriptionArr[264] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelIconSelected);
        themeDescriptionArr[265] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelStickerPackSelector);
        themeDescriptionArr[266] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelIconSelector);
        themeDescriptionArr[267] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelBackspace);
        themeDescriptionArr[268] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelTrendingTitle);
        themeDescriptionArr[269] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelTrendingDescription);
        themeDescriptionArr[270] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelBadgeText);
        themeDescriptionArr[271] = new ThemeDescription(this.chatActivityEnterView != null ? this.chatActivityEnterView.getEmojiView() : this.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, selectedBackgroundDelegate, Theme.key_chat_emojiPanelBadgeBackground);
        themeDescriptionArr[272] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonText);
        themeDescriptionArr[273] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackground);
        themeDescriptionArr[274] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackgroundPressed);
        themeDescriptionArr[275] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        themeDescriptionArr[276] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        themeDescriptionArr[277] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        themeDescriptionArr[278] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerPerformer);
        themeDescriptionArr[279] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        themeDescriptionArr[280] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_returnToCallBackground);
        themeDescriptionArr[281] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_returnToCallText);
        themeDescriptionArr[282] = new ThemeDescription(this.pinnedLineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chat_topPanelLine);
        themeDescriptionArr[283] = new ThemeDescription(this.pinnedMessageNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle);
        themeDescriptionArr[284] = new ThemeDescription(this.pinnedMessageTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage);
        themeDescriptionArr[285] = new ThemeDescription(this.alertNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle);
        themeDescriptionArr[286] = new ThemeDescription(this.alertTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage);
        themeDescriptionArr[287] = new ThemeDescription(this.closePinned, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose);
        themeDescriptionArr[288] = new ThemeDescription(this.closeReportSpam, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose);
        themeDescriptionArr[289] = new ThemeDescription(this.reportSpamView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[290] = new ThemeDescription(this.alertView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[291] = new ThemeDescription(this.pinnedMessageView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[292] = new ThemeDescription(this.addToContactsButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_addContact);
        themeDescriptionArr[293] = new ThemeDescription(this.reportSpamButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_reportSpam);
        themeDescriptionArr[294] = new ThemeDescription(this.replyLineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chat_replyPanelLine);
        themeDescriptionArr[295] = new ThemeDescription(this.replyNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_replyPanelName);
        themeDescriptionArr[296] = new ThemeDescription(this.replyObjectTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_replyPanelMessage);
        themeDescriptionArr[297] = new ThemeDescription(this.replyIconImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_replyPanelIcons);
        themeDescriptionArr[298] = new ThemeDescription(this.replyCloseImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_replyPanelClose);
        themeDescriptionArr[299] = new ThemeDescription(this.searchUpButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[300] = new ThemeDescription(this.searchDownButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[301] = new ThemeDescription(this.searchCalendarButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[302] = new ThemeDescription(this.searchUserButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[303] = new ThemeDescription(this.searchCountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_searchPanelText);
        themeDescriptionArr[304] = new ThemeDescription(this.bottomOverlayText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_secretChatStatusText);
        themeDescriptionArr[305] = new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText);
        themeDescriptionArr[306] = new ThemeDescription(this.bottomOverlayProgress, 0, null, null, null, null, Theme.key_chat_fieldOverlayText);
        themeDescriptionArr[307] = new ThemeDescription(this.bigEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[308] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[309] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[310] = new ThemeDescription(this.stickersPanelArrow, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_stickersHintPanel);
        themeDescriptionArr[311] = new ThemeDescription(this.stickersListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{StickerCell.class}, null, null, null, Theme.key_chat_stickersHintPanel);
        themeDescriptionArr[312] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, null, null, null, Theme.key_chat_unreadMessagesStartBackground);
        themeDescriptionArr[313] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_chat_unreadMessagesStartArrowIcon);
        themeDescriptionArr[314] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_unreadMessagesStartText);
        themeDescriptionArr[315] = new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[316] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[317] = new ThemeDescription(this.bigEmptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[318] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[319] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[320] = new ThemeDescription(this.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotSwitchCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_botSwitchToInlineText);
        themeDescriptionArr[321] = new ThemeDescription(this.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[322] = new ThemeDescription(this.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"usernameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[323] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, new Drawable[]{Theme.chat_inlineResultFile, Theme.chat_inlineResultAudio, Theme.chat_inlineResultLocation}, null, Theme.key_chat_inlineResultIcon);
        themeDescriptionArr[324] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[325] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[326] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[327] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioProgress);
        themeDescriptionArr[328] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress);
        themeDescriptionArr[329] = new ThemeDescription(this.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_divider);
        themeDescriptionArr[330] = new ThemeDescription(this.gifHintTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_gifSaveHintBackground);
        themeDescriptionArr[331] = new ThemeDescription(this.gifHintTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_gifSaveHintText);
        themeDescriptionArr[332] = new ThemeDescription(this.pagedownButtonCounter, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground);
        themeDescriptionArr[333] = new ThemeDescription(this.pagedownButtonCounter, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_goDownButtonCounter);
        themeDescriptionArr[334] = new ThemeDescription(this.pagedownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButton);
        themeDescriptionArr[335] = new ThemeDescription(this.pagedownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chat_goDownButtonShadow);
        themeDescriptionArr[336] = new ThemeDescription(this.pagedownButtonImage, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_goDownButtonIcon);
        themeDescriptionArr[337] = new ThemeDescription(this.mentiondownButtonCounter, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground);
        themeDescriptionArr[338] = new ThemeDescription(this.mentiondownButtonCounter, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_goDownButtonCounter);
        themeDescriptionArr[339] = new ThemeDescription(this.mentiondownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButton);
        themeDescriptionArr[340] = new ThemeDescription(this.mentiondownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chat_goDownButtonShadow);
        themeDescriptionArr[341] = new ThemeDescription(this.mentiondownButtonImage, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_goDownButtonIcon);
        themeDescriptionArr[342] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getTimeItem() : null, 0, null, null, null, null, Theme.key_chat_secretTimerBackground);
        themeDescriptionArr[343] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getTimeItem() : null, 0, null, null, null, null, Theme.key_chat_secretTimerText);
        themeDescriptionArr[344] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[0]}, null, Theme.key_chat_attachCameraIcon1);
        themeDescriptionArr[345] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[0]}, null, Theme.key_chat_attachCameraIcon2);
        themeDescriptionArr[346] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[0]}, null, Theme.key_chat_attachCameraIcon3);
        themeDescriptionArr[347] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[0]}, null, Theme.key_chat_attachCameraIcon4);
        themeDescriptionArr[348] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[0]}, null, Theme.key_chat_attachCameraIcon5);
        themeDescriptionArr[349] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[0]}, null, Theme.key_chat_attachCameraIcon6);
        themeDescriptionArr[350] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[1]}, null, Theme.key_chat_attachGalleryBackground);
        themeDescriptionArr[351] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[1]}, null, Theme.key_chat_attachGalleryIcon);
        themeDescriptionArr[352] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[2]}, null, Theme.key_chat_attachVideoBackground);
        themeDescriptionArr[353] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[2]}, null, Theme.key_chat_attachVideoIcon);
        themeDescriptionArr[354] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[3]}, null, Theme.key_chat_attachAudioBackground);
        themeDescriptionArr[355] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[3]}, null, Theme.key_chat_attachAudioIcon);
        themeDescriptionArr[356] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[4]}, null, Theme.key_chat_attachFileBackground);
        themeDescriptionArr[357] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[4]}, null, Theme.key_chat_attachFileIcon);
        themeDescriptionArr[358] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[5]}, null, Theme.key_chat_attachContactBackground);
        themeDescriptionArr[359] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[5]}, null, Theme.key_chat_attachContactIcon);
        themeDescriptionArr[360] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[6]}, null, Theme.key_chat_attachLocationBackground);
        themeDescriptionArr[361] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[6]}, null, Theme.key_chat_attachLocationIcon);
        themeDescriptionArr[362] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[7]}, null, Theme.key_chat_attachHideBackground);
        themeDescriptionArr[363] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[7]}, null, Theme.key_chat_attachHideIcon);
        themeDescriptionArr[364] = new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[8]}, null, Theme.key_chat_attachSendBackground);
        themeDescriptionArr[365] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachButtonDrawables[8]}, null, Theme.key_chat_attachSendIcon);
        themeDescriptionArr[366] = new ThemeDescription(null, 0, null, null, null, attachAlertDelegate, Theme.key_dialogBackground);
        themeDescriptionArr[367] = new ThemeDescription(null, 0, null, null, null, attachAlertDelegate, Theme.key_dialogBackgroundGray);
        themeDescriptionArr[368] = new ThemeDescription(null, 0, null, null, null, attachAlertDelegate, Theme.key_dialogTextGray2);
        themeDescriptionArr[369] = new ThemeDescription(null, 0, null, null, null, attachAlertDelegate, Theme.key_dialogScrollGlow);
        themeDescriptionArr[370] = new ThemeDescription(null, 0, null, null, null, attachAlertDelegate, Theme.key_dialogGrayLine);
        themeDescriptionArr[371] = new ThemeDescription(null, 0, null, null, null, attachAlertDelegate, Theme.key_dialogCameraIcon);
        return themeDescriptionArr;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$88$ChatActivity() {
        updateVisibleRows();
        if (this.chatActivityEnterView != null && this.chatActivityEnterView.getEmojiView() != null) {
            this.chatActivityEnterView.getEmojiView().updateUIColors();
        }
    }

    final /* synthetic */ void lambda$getThemeDescriptions$89$ChatActivity() {
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.checkColors();
        }
    }
}
